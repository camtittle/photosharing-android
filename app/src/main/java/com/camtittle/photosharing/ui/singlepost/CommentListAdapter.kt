package com.camtittle.photosharing.ui.singlepost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camtittle.photosharing.databinding.CommentListItemBinding
import com.camtittle.photosharing.engine.data.network.model.Comment

class CommentListAdapter : ListAdapter<CommentWithProfile, CommentListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)

        holder.apply {
            bind(comment)
            Log.d("CommentList", comment.profile?.name ?: "noName")
            itemView.tag = comment
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CommentListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(
        private val binding: CommentListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentWithProfile) {
            binding.comment = item.comment
            binding.profile = item.profile
        }
    }

}

private class DiffCallback : DiffUtil.ItemCallback<CommentWithProfile>() {
    override fun areContentsTheSame(oldItem: CommentWithProfile, newItem: CommentWithProfile): Boolean {
        return oldItem.comment.id == newItem.comment.id &&
                oldItem.profile == newItem.profile
    }

    override fun areItemsTheSame(oldItem: CommentWithProfile, newItem: CommentWithProfile): Boolean {
        return oldItem.comment.id == newItem.comment.id
    }

}