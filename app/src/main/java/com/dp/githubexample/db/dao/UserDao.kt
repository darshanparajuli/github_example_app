package com.dp.githubexample.db.dao

import androidx.room.*
import com.dp.githubexample.db.model.User

@Dao
interface UserDao {

    /**
     * Insert a list of users replacing existing ones.
     *
     * @param users User list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    /**
     * Delete all users.
     */
    @Query("delete from users")
    fun deleteAll()

    /**
     * Find a user with a given [id].
     *
     * @param id Id of the user
     * @return Return a [User] or null if none found.
     */
    @Query("select * from users where users.id = :id")
    fun findUserById(id: Int): User?

    /**
     * Get total count of users.
     *
     * @return Return total count of users.
     */
    @Query("select count(*) from users")
    fun getCount(): Int


    /**
     * Delete all users and add new users as a transaction.
     *
     * @param users New users to add.
     */
    @Transaction
    fun deleteAllAndInsert(users: List<User>) {
        deleteAll()
        insert(users)
    }
}