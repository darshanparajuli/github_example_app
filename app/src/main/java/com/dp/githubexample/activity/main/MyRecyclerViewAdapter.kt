package com.dp.githubexample.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dp.githubexample.R
import com.dp.githubexample.db.model.GithubRepository

/**
 * Alias for click listener callback lambda.
 */
typealias OnItemClickListener = (v: View) -> Unit

internal class MyRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener) :
    PagedListAdapter<GithubRepository, MyRecyclerViewAdapter.MyViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repo_list_item, parent, false)
        view.setOnClickListener {
            onItemClickListener(it)
        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    internal class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val context = v.context

        private val repoName: TextView = v.findViewById(R.id.repoName)
        private val description: TextView = v.findViewById(R.id.description)
        private val starCount: TextView = v.findViewById(R.id.starCount)

        fun bind(repo: GithubRepository) {
            repoName.text = repo.fullName
            starCount.text = context.getString(R.string.star_count_x, repo.starCount)

            if (repo.description != null) {
                description.text = repo.description
                description.isVisible = true
            } else {
                description.isVisible = false
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GithubRepository>() {
            override fun areItemsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
                return oldItem == newItem
            }
        }
    }
}