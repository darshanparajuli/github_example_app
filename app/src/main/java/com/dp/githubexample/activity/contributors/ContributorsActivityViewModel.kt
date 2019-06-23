package com.dp.githubexample.activity.contributors

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dp.githubexample.api.GithubApi
import com.dp.githubexample.common.viewmodel.LoadStatus
import com.dp.githubexample.db.MyDb
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.util.ScopedAndroidViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ContributorsActivityViewModel(application: Application) : ScopedAndroidViewModel(application) {

    private lateinit var contributorsLiveData: LiveData<PagedList<Contributor>>

    private val loadStatus = MutableLiveData<LoadStatus>()
    private var repoId = -1
    private var fetchJob: Job? = null

    fun getContributors(repoId: Int): LiveData<PagedList<Contributor>> {
        if (this::contributorsLiveData.isInitialized) {
            return contributorsLiveData
        }

        this.repoId = repoId

        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(30)
            .setPrefetchDistance(10)
            .setEnablePlaceholders(false)
            .build()

        val factory = MyDb.getInstance(context)
            .contributorDao()
            .getTopContributors(repoId)
        contributorsLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<Contributor>() {
                override fun onZeroItemsLoaded() {
                    loadStatus.postValue(LoadStatus.LOADING)
                    fetchContributors()
                }
            })
            .build()

        return contributorsLiveData
    }

    fun getLoadStatus(): LiveData<LoadStatus> = loadStatus

    private fun fetchContributors(deleteTableFirst: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = launch(IO) {
            try {
                val db = MyDb.getInstance(context)
                val repo = db.githubRepositoryDao().getRepoById(repoId)
                if (repo == null) {
                    loadStatus.postValue(LoadStatus.DONE_ERROR)
                    return@launch
                }

                val tokens = repo.fullName.split("/")
                val response = GithubApi.repositoryService.getContributors(tokens[0], tokens[1])
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.d(TAG, "Request successful, status code: ${response.code()}")

                    val contributors = body.map {
                        Contributor(it.id, it.username, it.avatarUrl, it.contributions, repoId)
                    }

                    val dao = db.contributorDao()
                    if (deleteTableFirst) {
                        dao.deleteAllAndInsert(contributors)
                    } else {
                        dao.insert(contributors)
                    }
                    loadStatus.postValue(LoadStatus.DONE_SUCCESS)
                } else {
                    Log.e(TAG, "Request FAILED, status code: ${response.code()}, body: $body")
                    loadStatus.postValue(LoadStatus.DONE_ERROR)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Request FAILED, with exception", e)
                loadStatus.postValue(LoadStatus.DONE_ERROR)
            }
        }
    }

    fun refresh() {
        loadStatus.postValue(LoadStatus.LOADING)
        fetchContributors(deleteTableFirst = true)
    }

    companion object {
        private const val TAG = "ContributorsViewModel"
    }
}
