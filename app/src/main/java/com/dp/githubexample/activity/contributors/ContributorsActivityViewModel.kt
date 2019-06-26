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
import com.dp.githubexample.db.model.ContributorInfo
import com.dp.githubexample.db.model.User
import com.dp.githubexample.util.ScopedAndroidViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

internal class ContributorsActivityViewModel(application: Application) : ScopedAndroidViewModel(application) {

    private lateinit var contributorsLiveData: LiveData<PagedList<ContributorInfo>>

    private val loadStatus = MutableLiveData<LoadStatus>()
    private var repoId = -1
    private var fetchJob: Job? = null

    fun getContributors(repoId: Int): LiveData<PagedList<ContributorInfo>> {
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
            .getTopContributorsPaged(repoId)
        contributorsLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<ContributorInfo>() {
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
                val contributors = getContributorsFromApi(repoId)
                if (contributors == null) {
                    loadStatus.postValue(LoadStatus.DONE_ERROR)
                    return@launch
                }

                val usernameSet = mutableSetOf<String>().apply {
                    for (c in contributors) {
                        add(c.username)
                    }
                }

                val users = getUsersFromApi(contributors).filter { it.username in usernameSet }

                val db = MyDb.getInstance(context)

                if (deleteTableFirst) {
                    db.runInTransaction {
                        db.userDao().deleteAllAndInsert(users)
                        db.contributorDao().deleteAllAndInsert(contributors)
                    }
                } else {
                    db.runInTransaction {
                        db.userDao().insert(users)
                        db.contributorDao().insert(contributors)
                    }
                }

                loadStatus.postValue(LoadStatus.DONE_SUCCESS)
            } catch (e: Exception) {
                Log.e(TAG, "Request FAILED, with exception", e)
                loadStatus.postValue(LoadStatus.DONE_ERROR)
            }
        }
    }

    private suspend fun getUsersFromApi(contributors: List<Contributor>) = coroutineScope {
        val users = mutableListOf<Deferred<User?>>()

        for (c in contributors) {
            users += async(IO) {
                val res = GithubApi.repositoryService.getUser(c.username)
                val body = res.body()
                if (res.isSuccessful && body != null) {
                    Log.d(TAG, "Request successful (user), status code: ${res.code()}")
                    User(body.id, body.username, body.bio, body.name, body.publicRepos, body.followers)
                } else {
                    Log.e(TAG, "Request FAILED (user), status code: ${res.code()}, body: $body")
                    null
                }
            }
        }

        users.awaitAll().filterNotNull()
    }

    private suspend fun getContributorsFromApi(repoId: Int) = coroutineScope {
        val db = MyDb.getInstance(context)
        val repo = db.githubRepositoryDao().getRepoById(repoId) ?: return@coroutineScope null

        val tokens = repo.fullName.split("/")
        val response = GithubApi.repositoryService.getContributors(tokens[0], tokens[1])
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Log.d(TAG, "Request successful (contributor), status code: ${response.code()}")

            body.map {
                Contributor(it.id, it.username, it.avatarUrl, it.contributions, repoId)
            }
        } else {
            Log.e(TAG, "Request FAILED (contributor), status code: ${response.code()}, body: $body")
            null
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
