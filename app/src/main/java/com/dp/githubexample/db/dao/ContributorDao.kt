package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.db.model.ContributorInfo

@Dao
interface ContributorDao {

    @Query(
        """
        select * from contributors 
        inner join users on contributors.username = users.username 
        where contributors.repo_id = :repoId order by contributors.contributions desc
        """
    )
    fun getTopContributors(repoId: Int): DataSource.Factory<Int, ContributorInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contributors: List<Contributor>)

    @Query("delete from contributors")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(contributors: List<Contributor>) {
        deleteAll()
        insert(contributors)
    }
}
