package com.dp.githubexample.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.dp.githubexample.db.model.Contributor

@Dao
interface ContributorDao {

    @Query("select * from contributors where contributors.repo_id = :repoId order by contributors.contributions desc")
    fun getTopContributors(repoId: Int): DataSource.Factory<Int, Contributor>

    @Insert
    fun insert(contributors: List<Contributor>)

    @Query("delete from contributors")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(contributors: List<Contributor>) {
        deleteAll()
        insert(contributors)
    }
}
