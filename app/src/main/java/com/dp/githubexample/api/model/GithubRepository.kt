package com.dp.githubexample.api.model

import com.squareup.moshi.Json

data class GithubRepository(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "stargazers_count") val starCount: Int,
    @field:Json(name = "open_issues_count") val openIssuesCount: Int,
    @field:Json(name = "forks_count") val forksCount: Int
)