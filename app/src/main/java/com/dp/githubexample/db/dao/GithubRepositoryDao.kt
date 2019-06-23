package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.dp.githubexample.db.model.GithubRepository

@Dao
interface GithubRepositoryDao {

    @Query("select * from github_repositories where github_repositories.id = :id")
    fun getRepoById(id: Int): GithubRepository?

    @Query("select * from github_repositories order by github_repositories.star_count desc")
    fun getMostStarredReposPaged(): DataSource.Factory<Int, GithubRepository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repos: List<GithubRepository>)

    @Query("delete from github_repositories")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsertGithubRepositories(repos: List<GithubRepository>) {
        deleteAll()
        insert(repos)
    }

    @Query("select COUNT(*) from github_repositories")
    fun getCount(): Int
}