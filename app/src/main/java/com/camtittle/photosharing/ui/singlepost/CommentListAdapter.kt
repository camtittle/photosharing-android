package com.camtittle.photosharing.ui.singlepost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camtittle.photosharing.databinding.CommentListItemBinding
import com.camtittle.photosharing.engine.data.network.model.Comment

class CommentListAdapter : ListAdapter<Comment, CommentListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)

        holder.apply {
            bind(comment)
            Log.d("CommentList", comment.content)
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

        fun bind(item: Comment) {
            binding.comment = item
        }
    }

}

private class DiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

}