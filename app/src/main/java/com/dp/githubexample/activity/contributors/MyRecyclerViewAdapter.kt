package com.dp.githubexample.activity.contributors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dp.githubexample.R
import com.dp.githubexample.db.model.Contributor

internal class MyRecyclerViewAdapter : PagedListAdapter<Contributor, MyRecyclerViewAdapter.MyViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contributor_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    internal class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val context = v.context
        private val username: TextView = v.findViewById(R.id.username)
        private val contributions: TextView = v.findViewById(R.id.contributions)

        fun bind(contributor: Contributor) {
            username.text = contributor.username
            contributions.text = context.getString(R.string.contributions_x, contributor.contributions)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Contributor>() {
            override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
                return oldItem == newItem
            }
        }
    }
}