package com.camtittle.photosharing.ui.singlepost

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer

import com.camtittle.photosharing.R
import com.camtittle.photosharing.databinding.CommentListItemBinding
import com.camtittle.photosharing.databinding.SinglePostFragmentBinding
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.network.model.Comment
import com.camtittle.photosharing.engine.data.network.model.Post
import com.camtittle.photosharing.engine.data.network.model.PostType
import com.squareup.picasso.Picasso

class SinglePostFragment : Fragment() {

    companion object {
        fun newInstance() = SinglePostFragment()
    }

    private lateinit var viewModel: SinglePostViewModel
    private lateinit var binding: SinglePostFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SinglePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SinglePostViewModel::class.java)
        binding.model = viewModel

        observePost()
        setupCommentsRecyclerView()

        viewModel.postId = arguments?.getString("postId") ?: ""
        viewModel.refresh()

    }

    private fun setupCommentsRecyclerView() {
        val commentsAdapter = CommentListAdapter()
        binding.commentsRecyclerView.adapter = commentsAdapter

        observeComments(commentsAdapter)
    }

    private fun observePost() {
        viewModel.post.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING ->
                    Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                Result.Status.SUCCESS ->
                    bindPost(it.data)
                Result.Status.ERROR ->
                    Toast.makeText(context, "Error fetching post: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeComments(adapter: CommentListAdapter) {
        viewModel.comments.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING ->
                    Toast.makeText(context, "Loading comments...", Toast.LENGTH_SHORT).show()
                Result.Status.SUCCESS -> {
                    Log.d("SinglePostFragment", it.toString())
                    adapter.submitList(it.data)
                }
                Result.Status.ERROR ->
                    Toast.makeText(context, "Error fetching comments: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindPost(post: Post?) {
        binding.singlePostItemCore.model = post
        if (post?.imageUrl != null) {
            Picasso.get().load(post.imageUrl).into(binding.singlePostItemCore.postListItemImage)
        }
    }
}
