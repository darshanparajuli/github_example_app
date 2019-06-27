package com.dp.githubexample.db.model

import androidx.room.*

/**
 * Data model for storing contributor information.
 */
@Entity(
    tableName = "contributors",
    indices = [Index("id"), Index("repo_id")],
    foreignKeys = [ForeignKey(entity = GithubRepository::class, parentColumns = ["id"], childColumns = ["repo_id"])]
)
data class Contributor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "contributions")
    val contributions: Int,

    @ColumnInfo(name = "repo_id")
    val repoId: Int
)
