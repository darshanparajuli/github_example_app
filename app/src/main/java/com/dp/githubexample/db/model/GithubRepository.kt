package com.dp.githubexample.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data model used for storing github repository information on the database.
 */
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
    val starCount: Int,

    @ColumnInfo(name = "open_issues_count")
    val openIssuesCount: Int,

    @ColumnInfo(name = "forks_count")
    val forksCount: Int
)
