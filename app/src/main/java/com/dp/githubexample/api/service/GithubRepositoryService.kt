package com.dp.githubexample.api.service

import com.dp.githubexample.api.model.Contributor
import com.dp.githubexample.api.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubRepositoryService {

    @GET("/search/repositories?q=stars:>0&s=stars&o=desc&page=1&type=Repositories")
    suspend fun getReposWithMostStars(@Query("per_page") count: Int): Response<SearchResponse>

    @GET("/repos/{name_a}/{name_b}/contributors")
    suspend fun getContributors(@Path("name_a") nameA: String, @Path("name_b") nameB: String): Response<List<Contributor>>
}