package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dp.githubexample.db.model.GithubRepository

@Dao
interface GithubRepositoryDao {

    @Query("select * from github_repositories order by github_repositories.star_count desc")
    fun getMostStarredReposPaged(): DataSource.Factory<Int, GithubRepository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGithubRepositories(repos: List<GithubRepository>)
}