package com.dp.githubexample.api.model

import com.squareup.moshi.Json

data class User(
    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "login")
    val username: String,

    @field:Json(name = "bio")
    val bio: String?,

    @field:Json(name = "name")
    val name: String?,

    @field:Json(name = "public_repos")
    val publicRepos: Int,

    @field:Json(name = "followers")
    val followers: Int
)