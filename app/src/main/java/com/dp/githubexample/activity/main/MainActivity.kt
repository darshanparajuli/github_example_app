package com.dp.githubexample.activity.main

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
        // start with refreshing state
        swipeRefreshLayout.isRefreshing = true
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
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this)
            .get(MainActivityViewModel::class.java)

        // Observe the data for most star repos
        viewModel.getMostStarredRepositories().observe(this, Observer {
            adapter.submitList(it)

            // Once we have the list, set refreshing state to false.
            swipeRefreshLayout.isRefreshing = false
        })
    }
}
