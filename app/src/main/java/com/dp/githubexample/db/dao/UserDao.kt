package com.dp.githubexample.db.dao

import androidx.room.*
import com.dp.githubexample.db.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    @Query("delete from users")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(users: List<User>) {
        deleteAll()
        insert(users)
    }
}