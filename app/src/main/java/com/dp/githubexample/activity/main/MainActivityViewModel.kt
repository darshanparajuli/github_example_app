package com.dp.githubexample.activity.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dp.githubexample.api.service.GithubService
import com.dp.githubexample.db.MyDb
import com.dp.githubexample.db.model.GithubRepository
import com.dp.githubexample.util.ScopedAndroidViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivityViewModel(application: Application) : ScopedAndroidViewModel(application) {

    private val githubService = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build().run {
            create(GithubService::class.java)
        }

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
                val response = githubService.getReposWithMostStars(100 /* get top 100 */)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.d(TAG, "Request successful, status code: ${response.code()}")

                    val repos = body.items.map {
                        GithubRepository(it.id, it.name, it.fullName, it.description, it.starCount)
                    }

                    val dao = MyDb.getInstance(context)
                        .githubRepositoryDao()

                    if (deleteTableFirst) {
                        dao.deleteAllAndInsertGithubRepositories(repos)
                    } else {
                        dao.insertGithubRepositories(repos)
                    }
                    loadStatus.postValue(LoadStatus.FINISHED_SUCCES)
                } else {
                    Log.e(TAG, "Request FAILED, status code: ${response.code()}, body: $body")
                    loadStatus.postValue(LoadStatus.FINISHED_ERROR)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Request FAILED, with exception", e)
                loadStatus.postValue(LoadStatus.FINISHED_ERROR)
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

    enum class LoadStatus {
        LOADING,
        FINISHED_SUCCES,
        FINISHED_ERROR,
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
        private const val GITHUB_API_BASE_URL = "https://api.github.com"
    }
}
