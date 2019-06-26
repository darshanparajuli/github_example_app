package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.db.model.ContributorInfo

@Dao
interface ContributorDao {

    @Query(TOP_CONTRIBUTORS_QUERY)
    fun getTopContributorsPaged(repoId: Int): DataSource.Factory<Int, ContributorInfo>

    @Query(TOP_CONTRIBUTORS_QUERY)
    fun getTopContributors(repoId: Int): List<ContributorInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contributors: List<Contributor>)

    @Query("delete from contributors")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(contributors: List<Contributor>) {
        deleteAll()
        insert(contributors)
    }

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
