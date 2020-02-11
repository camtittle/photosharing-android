package com.camtittle.photosharing.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camtittle.photosharing.databinding.PostListItemBinding
import com.camtittle.photosharing.ui.shared.CorePostModel
import com.squareup.picasso.Picasso

class PostListAdapter : ListAdapter<FeedItemContainer, PostListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedItem = getItem(position)
        holder.apply {
            bind(createOnClickListener(feedItem.post.id), feedItem)
            loadImg(feedItem.post)
            itemView.tag = feedItem
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

        fun bind(onClickListener: View.OnClickListener, item: FeedItemContainer) {
            binding.model = item
            binding.onClickListener = onClickListener
        }

        fun loadImg(item: CorePostModel) {
            if (!item.imageUrl.isNullOrBlank()) {
                Picasso.get().load(item.imageUrl).into(binding.postListItemCore.postListItemImage)
            }
        }
    }

}

private class DiffCallback : DiffUtil.ItemCallback<FeedItemContainer>() {
    override fun areContentsTheSame(oldItem: FeedItemContainer, newItem: FeedItemContainer): Boolean {
        return oldItem.post.id == newItem.post.id && oldItem.profile == newItem.profile
    }

    override fun areItemsTheSame(oldItem: FeedItemContainer, newItem: FeedItemContainer): Boolean {
        return oldItem.post.id == newItem.post.id
    }

}