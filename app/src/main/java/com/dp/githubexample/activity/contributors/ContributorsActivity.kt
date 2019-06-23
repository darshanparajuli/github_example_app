package com.dp.githubexample.activity.contributors

import android.os.Bundle
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

    private fun setupView() {
        setupToolbar(R.id.toolbar)
        enableDisplayHomeAsUp()
        toolbar.subtitle = repoName

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.adapter = MyRecyclerViewAdapter().also {
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
