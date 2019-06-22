package com.dp.githubexample.api.model

import com.squareup.moshi.Json

data class SearchResponse(
    @field:Json(name = "total_count") val totalCount: Int,
    @field:Json(name = "items") val items: List<GithubRepository>
)