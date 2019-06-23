package com.dp.githubexample.activity.contributors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dp.githubexample.R
import com.dp.githubexample.db.model.Contributor
import com.dp.githubexample.util.CircularImageTransformation
import com.squareup.picasso.Picasso

typealias OnItemClickListener = (v: View) -> Unit

internal class MyRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener) :
    PagedListAdapter<Contributor, MyRecyclerViewAdapter.MyViewHolder>(diffCallback) {
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
        private val contributions: TextView = v.findViewById(R.id.contributions)

        fun bind(contributor: Contributor) {
            username.text = contributor.username
            contributions.text = context.getString(R.string.contributions_x, contributor.contributions)

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