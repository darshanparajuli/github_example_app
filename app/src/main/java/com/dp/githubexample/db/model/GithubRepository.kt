package com.dp.githubexample.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "github_repositories", indices = [Index("id")])
data class GithubRepository(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "star_count")
    val starCount: Int
)
