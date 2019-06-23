package com.dp.githubexample.api

import com.dp.githubexample.api.service.GithubRepositoryService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object GithubApi {
    private const val GITHUB_API_BASE_URL = "https://api.github.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val repositoryService = retrofit.create(GithubRepositoryService::class.java)
}