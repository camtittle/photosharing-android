package com.camtittle.photosharing.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
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
            bind(createOnClickListener(post.id), post)
            loadImg(post)
            itemView.tag = post
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(postId: String): View.OnClickListener {
        return View.OnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToSinglePostFragment(postId)
            it.findNavController().navigate(action)
        }
    }

    class ViewHolder(
        private val binding: PostListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onClickListener: View.OnClickListener, item: Post) {
            binding.model = item
            binding.onClickListener = onClickListener
        }

        fun loadImg(item: Post) {
            if (!item.imageUrl.isNullOrBlank()) {
                Picasso.get().load(item.imageUrl).into(binding.postListItemCore.postListItemImage)
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