package com.dp.githubexample.activity.contributors

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dp.githubexample.R
import com.dp.githubexample.activity.BaseActivityWithToolbar
import com.dp.githubexample.common.viewmodel.LoadStatus
import com.dp.githubexample.util.toast


class ContributorsActivity : BaseActivityWithToolbar() {

    private var repoId: Int = -1
    private lateinit var repoName: String

    private lateinit var adapter: MyRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewModel: ContributorsActivityViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contributors)

        val repoName = intent.getStringExtra(EXTRA_REPO_NAME)
        if (repoName.isNullOrBlank()) {
            toast(R.string.invalid_repo_name)
            finish()
            return
        }

        val repoId = intent.getIntExtra(EXTRA_REPO_ID, -1)
        if (repoId == -1) {
            toast(R.string.invalid_repo_id)
            finish()
            return
        }

        viewModel = ViewModelProviders.of(this)
            .get(ContributorsActivityViewModel::class.java)
        this.repoName = repoName
        this.repoId = repoId

        setupView()
        setupViewModel()
    }

    private fun itemClickHandler(v: View) {
        val pos = linearLayoutManager.getPosition(v)
        if (pos == RecyclerView.NO_POSITION) {
            return
        }

        val contributor = adapter.currentList?.get(pos) ?: return

        val intent = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setShowTitle(true)
            .build()
        intent.launchUrl(this, Uri.parse("https://github.com/${contributor.username}"))
    }

    private fun setupView() {
        setupToolbar(R.id.toolbar)
        enableDisplayHomeAsUp()
        toolbar.subtitle = repoName

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this).also {
            linearLayoutManager = it
        }
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.adapter = MyRecyclerViewAdapter(this::itemClickHandler).also {
            adapter = it
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupViewModel() {
        viewModel.getContributors(repoId).observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.getLoadStatus().observe(this, Observer {
            if (it != null) {
                when (it) {
                    LoadStatus.LOADING -> {
                        swipeRefreshLayout.isRefreshing = true
                    }
                    LoadStatus.DONE_SUCCESS -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    LoadStatus.DONE_ERROR -> {
                        swipeRefreshLayout.isRefreshing = false
                        toast(R.string.error_fetching_contributors)
                    }
                }
            }
        })
    }

    companion object {
        const val EXTRA_REPO_ID = "repo_id"
        const val EXTRA_REPO_NAME = "repo_name"
    }
}
