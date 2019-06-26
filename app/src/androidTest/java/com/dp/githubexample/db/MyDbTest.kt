package com.dp.githubexample.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.db.model.GithubRepository
import com.dp.githubexample.db.model.User
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MyDbTest {

    private lateinit var db: MyDb

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDb::class.java
        ).build()
    }

    @Throws(IOException::class)
    @After
    fun closeDb() {
        db.close()
    }

    @Throws(Exception::class)
    @Test
    fun userDao() {
        val users = listOf(
            User(12, "jon", "Hi, I'm Jon", "Jon Doe", 13, 200),
            User(14, "joe", "Hi, I'm Joe", "Joe", 13, 200)
        )
        val dao = db.userDao()
        dao.insert(users)

        for (user in users) {
            assertThat(dao.findUserById(user.id), equalTo(user))
        }

        dao.deleteAll()
        assertThat(dao.getCount(), equalTo(0))
    }

    @Throws(Exception::class)
    @Test
    fun githubRepositoryDao() {
        val dao = db.githubRepositoryDao()
        val repos = listOf(
            GithubRepository(12, "banana", "apple banana", "Some desc", 12, 0, 0),
            GithubRepository(13, "peach", "peach honey", "Hey", 13, 0, 0)
        )
        dao.insert(repos)
        assertThat(dao.getCount(), equalTo(2))
        assertThat(dao.getRepoById(12), equalTo(repos[0]))

        assertThat(dao.getMostStarredRepos(), equalTo(repos.reversed()))

        dao.deleteAll()
        assertThat(dao.getCount(), equalTo(0))
    }

    @Throws(Exception::class)
    @Test
    fun contributorDao() {
        db.githubRepositoryDao().run {
            val repo = GithubRepository(1, "banana", "apple banana", "Some desc", 12, 0, 0)
            insert(listOf(repo))
        }

        db.userDao().run {
            val users = listOf(
                User(4, "jon", "Hi, I'm Jon", "Jon Doe", 13, 200),
                User(5, "joe", "Hi, I'm Joe", "Joe", 13, 200)
            )
            insert(users)
        }

        val dao = db.contributorDao()
        val contributors = listOf(
            Contributor(40, "joe", null, 12, 1),
            Contributor(50, "jon", "some_url", 100, 1)
        )
        dao.insert(contributors)

        dao.getTopContributors(1).also {
            assertThat(it.size, equalTo(2))

            val reversed = contributors.reversed()
            for (i in 0 until 2) {
                assertThat(it[i].username, equalTo(reversed[i].username))
            }
        }

        dao.deleteAll()
        assertThat(dao.getCount(), equalTo(0))

        db.githubRepositoryDao().run {
            deleteAll()
            assertThat(getCount(), equalTo(0))
        }


        db.userDao().run {
            deleteAll()
            assertThat(getCount(), equalTo(0))
        }
    }
}