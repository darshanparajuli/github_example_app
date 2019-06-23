package com.dp.githubexample.db.model

import androidx.room.ColumnInfo

data class ContributorInfo(
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "contributions")
    val contributions: Int,

    @ColumnInfo(name = "bio")
    val bio: String?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "public_repos")
    val publicRepos: Int,

    @ColumnInfo(name = "followers")
    val followers: Int,

    @ColumnInfo(name = "repo_id")
    val repoId: Int
)