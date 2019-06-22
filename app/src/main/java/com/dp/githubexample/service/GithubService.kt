package com.dp.githubexample.service

import retrofit2.http.GET

interface GithubService {

    @GET()
    fun getRepos();
}