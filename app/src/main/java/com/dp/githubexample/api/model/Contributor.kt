package com.dp.githubexample.api.model

import com.squareup.moshi.Json

/**
 * Data model used for fetching contributor info from the api.
 */
data class Contributor(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "login") val username: String,
    @field:Json(name = "avatar_url") val avatarUrl: String,
    @field:Json(name = "contributions") val contributions: Int
)