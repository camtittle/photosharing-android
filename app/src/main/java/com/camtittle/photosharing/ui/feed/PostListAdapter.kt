package com.camtittle.photosharing.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camtittle.photosharing.databinding.PostListItemBinding
import com.camtittle.photosharing.engine.data.network.model.VoteType
import com.camtittle.photosharing.ui.shared.CorePostModel
import com.camtittle.photosharing.ui.shared.FeedItemContainer
import com.squareup.picasso.Picasso

class PostListAdapter(val viewModel: FeedViewModel) : ListAdapter<FeedItemContainer, PostListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedItem = getItem(position)
        holder.apply {
            bind(createOnClickListener(feedItem.post.id), createUpVoteClickListener(feedItem),
                createDownVoteClickListener(feedItem), feedItem)
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

    private fun createUpVoteClickListener(item: FeedItemContainer): View.OnClickListener {
        return View.OnClickListener {
            if (item.post.hasVoted.get() == VoteType.DOWN) {
                item.post.downvotes.apply { set(get() - 1)}
            }

            if (item.post.hasVoted.get() != VoteType.UP) {
                item.post.hasVoted.set(VoteType.UP)
                item.post.upvotes.apply { set(get() + 1) }
                viewModel.submitVote(item.post.id, VoteType.UP)
            }
        }
    }

    private fun createDownVoteClickListener(item: FeedItemContainer): View.OnClickListener {
        return View.OnClickListener {
            if (item.post.hasVoted.get() == VoteType.UP) {
                item.post.upvotes.apply { set(get() - 1)}
            }

            if (item.post.hasVoted.get() != VoteType.DOWN) {
                item.post.hasVoted.set(VoteType.DOWN)
                item.post.downvotes.apply { set(get() + 1) }
                viewModel.submitVote(item.post.id, VoteType.DOWN)
            }
        }
    }

    class ViewHolder(
        private val binding: PostListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onClickListener: View.OnClickListener, upVoteClickListener: View.OnClickListener,
                 downVoteClickListener: View.OnClickListener, item: FeedItemContainer) {
            binding.model = item
            binding.onClickListener = onClickListener
            binding.onUpVoteClickListener = upVoteClickListener
            binding.onDownVoteClickListener = downVoteClickListener
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