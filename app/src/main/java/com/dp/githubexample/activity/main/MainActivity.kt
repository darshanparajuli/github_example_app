package com.dp.githubexample.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dp.githubexample.R
import com.dp.githubexample.activity.contributors.ContributorsActivity
import com.dp.githubexample.util.toast

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        // setup recyclerview
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

    private fun itemClickHandler(v: View) {
        val pos = linearLayoutManager.getPosition(v)
        // Make sure pos is valid
        if (pos == RecyclerView.NO_POSITION) {
            return
        }

        val repoName = adapter.currentList?.get(pos)?.fullName ?: return

        val intent = Intent(this, ContributorsActivity::class.java)
        intent.putExtra(ContributorsActivity.EXTRA_REPO_NAME, repoName)
        startActivity(intent)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this)
            .get(MainActivityViewModel::class.java)

        // Observe the data for most star repos
        viewModel.getMostStarredRepositories().observe(this, Observer {
            adapter.submitList(it)
        })

        // Observe load status
        viewModel.getLoadStatus().observe(this, Observer {
            if (it != null) {
                when (it) {
                    MainActivityViewModel.LoadStatus.LOADING -> {
                        swipeRefreshLayout.isRefreshing = true
                    }
                    MainActivityViewModel.LoadStatus.FINISHED_SUCCES -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    MainActivityViewModel.LoadStatus.FINISHED_ERROR -> {
                        swipeRefreshLayout.isRefreshing = false
                        this@MainActivity.toast(R.string.github_repos_fetch_error_message)
                    }
                }
            }
        })
    }
}
