package com.dp.githubexample.api.service

import com.dp.githubexample.api.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This service is used for providing search related endpoints.
 */
interface SearchService {

    @GET("/search/repositories?q=stars:>0&s=stars&o=desc&page=1&type=Repositories")
    suspend fun getReposWithMostStars(@Query("per_page") count: Int): Response<SearchResponse>
}