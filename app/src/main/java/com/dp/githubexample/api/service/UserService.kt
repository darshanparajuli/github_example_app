package com.dp.githubexample.api.service

import com.dp.githubexample.api.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * This service is used for providing user related endpoints.
 */
interface UserService {

    @GET("/users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<User>
}