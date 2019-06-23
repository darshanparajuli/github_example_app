package com.dp.githubexample.activity.contributors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dp.githubexample.R
import com.dp.githubexample.db.model.ContributorInfo
import com.dp.githubexample.util.CircularImageTransformation
import com.squareup.picasso.Picasso

typealias OnItemClickListener = (v: View) -> Unit

internal class MyRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener) :
    PagedListAdapter<ContributorInfo, MyRecyclerViewAdapter.MyViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contributor_list_item, parent, false)
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
        private val avatar: ImageView = v.findViewById(R.id.avatar)
        private val username: TextView = v.findViewById(R.id.username)
        private val bio: TextView = v.findViewById(R.id.bio)
        private val contributions: TextView = v.findViewById(R.id.contributions)
        private val repoCount: TextView = v.findViewById(R.id.repoCount)
        private val followers: TextView = v.findViewById(R.id.followers)

        fun bind(contributor: ContributorInfo) {
            if (contributor.name != null) {
                username.text = context.getString(R.string.username_with_name, contributor.username, contributor.name)
            } else {
                username.text = contributor.username
            }

            if (contributor.bio != null) {
                bio.text = contributor.bio
                bio.isVisible = true
            } else {
                bio.isVisible = false
            }

            contributions.text = context.getString(R.string.integer_x, contributor.contributions)
            repoCount.text = context.getString(R.string.integer_x, contributor.publicRepos)
            followers.text = context.getString(R.string.integer_x, contributor.followers)

            if (contributor.avatarUrl != null) {
                Picasso.get()
                    .load(contributor.avatarUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.tinted_ic_avatar_placeholder)
                    .transform(CircularImageTransformation(contributor.avatarUrl))
                    .into(avatar)
            } else {
                avatar.background = ContextCompat.getDrawable(context, R.drawable.tinted_ic_avatar_placeholder)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ContributorInfo>() {
            override fun areItemsTheSame(oldItem: ContributorInfo, newItem: ContributorInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ContributorInfo, newItem: ContributorInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}