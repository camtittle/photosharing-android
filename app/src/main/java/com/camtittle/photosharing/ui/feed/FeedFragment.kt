package com.camtittle.photosharing.ui.feed

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.camtittle.photosharing.R
import com.camtittle.photosharing.databinding.FeedFragmentBinding

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        ensureAuthorised()

        val binding = FeedFragmentBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = PostListAdapter()
        binding.feedFragmentRecyclerView.adapter = adapter

        observePosts(adapter)

        refreshPosts()

        return binding.root
    }

    private fun ensureAuthorised() {
        if (!viewModel.isSignedIn()) {
            val action = FeedFragmentDirections.actionGlobalAuthNavigation()
            findNavController().navigate(action)
        }
    }

    private fun refreshPosts() {
        viewModel.updatePostsList()
    }

    private fun observePosts(adapter: PostListAdapter) {
        viewModel.posts.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
    }

}
