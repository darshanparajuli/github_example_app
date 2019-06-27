package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.db.model.ContributorInfo

@Dao
interface ContributorDao {

    /**
     * Load top github contributors from the database.
     *
     * @param repoId Github repo id
     * @return Return a [DataSource.Factory] of [ContributorInfo] used by [androidx.paging.PagedListAdapter]
     */
    @Query(TOP_CONTRIBUTORS_QUERY)
    fun getTopContributorsPaged(repoId: Int): DataSource.Factory<Int, ContributorInfo>

    /**
     * This method is used for testing purposes since testing datasource with [getTopContributorsPaged] seems way more
     * difficult/convoluted. Since the query is the same, the test should work.
     */
    @Query(TOP_CONTRIBUTORS_QUERY)
    fun getTopContributors(repoId: Int): List<ContributorInfo>

    /**
     * @param contributors Contributors to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contributors: List<Contributor>)

    /**
     * Delete all existing contributors.
     */
    @Query("delete from contributors")
    fun deleteAll()

    /**
     * Delete all contributors first and insert new contributors as a single transaction.
     *
     * @param contributors New contributors to insert
     */
    @Transaction
    fun deleteAllAndInsert(contributors: List<Contributor>) {
        deleteAll()
        insert(contributors)
    }

    /**
     * Fetch total count of contributors.
     *
     * @return Return the total contributor count
     */
    @Query("select count(*) from contributors")
    fun getCount(): Int

    companion object {
        private const val TOP_CONTRIBUTORS_QUERY = """
        select * from contributors 
        inner join users on contributors.username = users.username 
        where contributors.repo_id = :repoId order by contributors.contributions desc
        """
    }
}
