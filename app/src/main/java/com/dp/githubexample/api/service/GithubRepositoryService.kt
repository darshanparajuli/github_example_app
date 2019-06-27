package com.dp.githubexample.api.service

import com.dp.githubexample.api.model.Contributor
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * This service is used for providing repository related endpoints.
 */
interface GithubRepositoryService {

    @GET("/repos/{name_a}/{name_b}/contributors")
    suspend fun getContributors(@Path("name_a") nameA: String, @Path("name_b") nameB: String): Response<List<Contributor>>
}