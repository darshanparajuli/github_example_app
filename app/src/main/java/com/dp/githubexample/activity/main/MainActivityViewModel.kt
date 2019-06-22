package com.dp.githubexample.activity.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
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
    private var refreshJob: Job? = null

    private val boundaryCallback = object : PagedList.BoundaryCallback<GithubRepository>() {
        override fun onZeroItemsLoaded() {
            refresh()
        }

        override fun onItemAtEndLoaded(itemAtEnd: GithubRepository) {
        }
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = launch(IO) {
            val response = githubService.getReposWithMostStars(100 /* get top 100 */)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Log.d(TAG, "Request successful, status code: ${response.code()}")

                val repos = body.items.map {
                    GithubRepository(it.id, it.name, it.fullName, it.description, it.starCount)
                }

                MyDb.getInstance(context)
                    .githubRepositoryDao()
                    .insertGithubRepositories(repos)
            } else {
                Log.e(TAG, "Request FAIL, status code: ${response.code()}, body: $body")
            }
        }
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

    companion object {
        private const val TAG = "MainActivityViewModel"
        private const val GITHUB_API_BASE_URL = "https://api.github.com"
    }
}
