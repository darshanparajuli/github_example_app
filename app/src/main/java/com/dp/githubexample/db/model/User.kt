package com.dp.githubexample.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data model used for storing user information.
 */
@Entity(tableName = "users", indices = [Index("id")])
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "bio")
    val bio: String?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "public_repos")
    val publicRepos: Int,

    @ColumnInfo(name = "followers")
    val followers: Int
)