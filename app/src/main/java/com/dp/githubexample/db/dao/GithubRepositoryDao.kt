package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.dp.githubexample.db.model.GithubRepository

@Dao
interface GithubRepositoryDao {

    /**
     * Get [GithubRepository] by id.
     *
     * @param id Id of the repo
     * @return Return [GithubRepository] or null if none exist for the given [id]
     */
    @Query("select * from github_repositories where github_repositories.id = :id")
    fun getRepoById(id: Int): GithubRepository?

    /**
     * Get the most starred repos.
     *
     * @return Return a datasource to be used by the paging adapter.
     */
    @Query(MOST_STARRED_QUERY)
    fun getMostStarredReposPaged(): DataSource.Factory<Int, GithubRepository>

    /**
     * This method is used for testing only.
     */
    @Query(MOST_STARRED_QUERY)
    fun getMostStarredRepos(): List<GithubRepository>

    /**
     * Insert github repos to the database.
     *
     * @param repos List of [GithubRepository]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repos: List<GithubRepository>)

    /**
     * Delete all [GithubRepository] row from the table.
     */
    @Query("delete from github_repositories")
    fun deleteAll()

    /**
     * Delete all github repos and insert new ones as a transaction.
     *
     * @param repos New repos to add.
     */
    @Transaction
    fun deleteAllAndInsertGithubRepositories(repos: List<GithubRepository>) {
        deleteAll()
        insert(repos)
    }

    /**
     * Get total count of github repos on the database.
     *
     * @return Return total count of github repos.
     */
    @Query("select count(*) from github_repositories")
    fun getCount(): Int

    companion object {
        private const val MOST_STARRED_QUERY =
            "select * from github_repositories order by github_repositories.star_count desc"
    }
}