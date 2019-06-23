package com.dp.githubexample.db

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dp.githubexample.db.dao.ContributorDao
import com.dp.githubexample.db.dao.GithubRepositoryDao
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.db.model.GithubRepository

@Database(entities = [GithubRepository::class, Contributor::class], version = 1, exportSchema = false)
abstract class MyDb : RoomDatabase() {

    abstract fun githubRepositoryDao(): GithubRepositoryDao

    abstract fun contributorDao(): ContributorDao

    companion object {
        private const val DB_NAME = "my_db"
        private val lock = Any()

        @GuardedBy("lock")
        @Volatile
        private var instance: MyDb? = null

        fun getInstance(context: Context): MyDb {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(lock) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyDb::class.java,
                    DB_NAME
                ).build()
                    .apply {
                    instance = this
                }
            }
        }
    }
}
