package com.dp.githubexample.api

import com.dp.githubexample.BuildConfig
import com.dp.githubexample.api.service.GithubRepositoryService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object GithubApi {
    private const val GITHUB_API_BASE_URL = "https://api.github.com"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            if (BuildConfig.GITHUB_ACCESS_TOKEN.isNotEmpty()) {
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "token ${BuildConfig.GITHUB_ACCESS_TOKEN}")
                    .build()
                chain.proceed(req)
            } else {
                chain.proceed(chain.request())
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    val repositoryService: GithubRepositoryService = retrofit.create(GithubRepositoryService::class.java)
}