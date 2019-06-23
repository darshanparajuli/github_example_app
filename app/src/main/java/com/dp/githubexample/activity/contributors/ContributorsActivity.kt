package com.dp.githubexample.activity.contributors

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dp.githubexample.R
import com.dp.githubexample.util.toast

class ContributorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repoName = intent.getStringExtra(EXTRA_REPO_NAME)
        if (repoName.isNullOrBlank()) {
            toast(R.string.invalid_repo_name)
            finish()
            return
        }

        supportActionBar?.subtitle = repoName
    }

    companion object {
        const val EXTRA_REPO_NAME = "repo_name"
    }
}
