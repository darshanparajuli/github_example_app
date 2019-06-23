package com.dp.githubexample.activity.contributors

import android.os.Bundle
import com.dp.githubexample.R
import com.dp.githubexample.activity.BaseActivityWithToolbar
import com.dp.githubexample.util.toast

class ContributorsActivity : BaseActivityWithToolbar() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contributors)

        val repoName = intent.getStringExtra(EXTRA_REPO_NAME)
        if (repoName.isNullOrBlank()) {
            toast(R.string.invalid_repo_name)
            finish()
            return
        }

        setupToolbar(R.id.toolbar)
        enableDisplayHomeAsUp()
        toolbar.subtitle = repoName
    }

    companion object {
        const val EXTRA_REPO_NAME = "repo_name"
    }
}
