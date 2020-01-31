package com.camtittle.photosharing.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camtittle.photosharing.databinding.PostListItemBinding
import com.camtittle.photosharing.engine.data.network.model.Post
import com.squareup.picasso.Picasso

class PostListAdapter : ListAdapter<Post, PostListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.apply {
            bind(post)
            loadImg(post)
            itemView.tag = post
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(
        private val binding: PostListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            binding.model = item
        }

        fun loadImg(item: Post) {
            if (!item.imageUrl.isNullOrBlank()) {
                Picasso.get().load(item.imageUrl).into(binding.postListItemImage)
            }
        }
    }

}

private class DiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

}