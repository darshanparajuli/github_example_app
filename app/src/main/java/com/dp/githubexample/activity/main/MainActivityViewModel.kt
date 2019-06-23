package com.dp.githubexample.activity.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dp.githubexample.api.GithubApi
import com.dp.githubexample.common.viewmodel.LoadStatus
import com.dp.githubexample.db.MyDb
import com.dp.githubexample.db.model.GithubRepository
import com.dp.githubexample.util.ScopedAndroidViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class MainActivityViewModel(application: Application) : ScopedAndroidViewModel(application) {

    private lateinit var mostStarredRepositoriesLiveData: LiveData<PagedList<GithubRepository>>
    private var fetchJob: Job? = null

    private val loadStatus = MutableLiveData<LoadStatus>()

    private val boundaryCallback = object : PagedList.BoundaryCallback<GithubRepository>() {
        override fun onZeroItemsLoaded() {
            // This is show the loading indicator
            loadStatus.value = LoadStatus.LOADING
            fetchRepos()
        }

        override fun onItemAtEndLoaded(itemAtEnd: GithubRepository) {
        }
    }

    private fun fetchRepos(deleteTableFirst: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = launch(IO) {
            try {
                val response = GithubApi.repositoryService.getReposWithMostStars(100 /* get top 100 */)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.d(TAG, "Request successful, status code: ${response.code()}")

                    val repos = body.items.map {
                        GithubRepository(
                            it.id,
                            it.name,
                            it.fullName,
                            it.description,
                            it.starCount,
                            it.openIssuesCount,
                            it.forksCount
                        )
                    }

                    val db = MyDb.getInstance(context)
                    val dao = db.githubRepositoryDao()

                    if (deleteTableFirst) {
                        db.runInTransaction {
                            db.contributorDao().deleteAll()
                            dao.deleteAllAndInsertGithubRepositories(repos)
                        }
                    } else {
                        dao.insert(repos)
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
        // This is to show the loading indicator
        loadStatus.value = LoadStatus.LOADING

        // This is for swipe-to-refresh action; delete existing data before adding new data
        fetchRepos(deleteTableFirst = true)
    }

    fun getMostStarredRepositories(): LiveData<PagedList<GithubRepository>> {
        if (this::mostStarredRepositoriesLiveData.isInitialized) {
            return mostStarredRepositoriesLiveData
        }

        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(30)
            .setPrefetchDistance(10)
            .setEnablePlaceholders(false)
            .build()

        val factory = MyDb.getInstance(context)
            .githubRepositoryDao()
            .getMostStarredReposPaged()
        mostStarredRepositoriesLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return mostStarredRepositoriesLiveData
    }

    fun getLoadStatus(): LiveData<LoadStatus> = loadStatus

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
