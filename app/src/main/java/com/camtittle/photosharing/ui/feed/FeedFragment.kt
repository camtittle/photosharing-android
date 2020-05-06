package com.camtittle.photosharing.ui.feed

import android.app.Activity
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
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.engine.location.LatLong
import com.camtittle.photosharing.engine.location.LocationService
import com.google.android.material.snackbar.Snackbar

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FeedFragmentBinding

    private var location: LatLong? = null
    private var shouldRefreshOnNextLocation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)


        binding = FeedFragmentBinding.inflate(inflater, container, false)

        if (!ensureAuthorised() || context == null) {
            return binding.root
        }

        val adapter = PostListAdapter(viewModel)
        binding.feedFragmentRecyclerView.adapter = adapter

        observePosts(adapter)
        observeSwipeUp()
        observeErrors()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Location disabled for OnlyBeans
//        activity?.let {
//            observeLocation(it)
//        }

        refreshPosts()
    }

    private fun ensureAuthorised(): Boolean {
        if (!viewModel.isSignedIn()) {
            val action = FeedFragmentDirections.actionGlobalAuthNavigation()
            findNavController().navigate(action)
            return false
        }

        return true
    }

    private fun refreshPosts() {
        binding.feedSwipeRefresh.isRefreshing = true
        // Location disabled for OnlyBeans
//        location.let {
//            if (it == null) {
//                shouldRefreshOnNextLocation = true
//            } else {
//                viewModel.updatePostsList(it.lat, it.long)
//            }
//        }

        val location = LocationService.getOnlyBeansLocation()
        viewModel.updatePostsList(location.lat, location.long)
    }

    private fun observePosts(adapter: PostListAdapter) {
        viewModel.feedItems.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it)
                binding.feedSwipeRefresh.isRefreshing = false
            }
        })
    }

    private fun observeErrors() {
        viewModel.errors.observe(viewLifecycleOwner, EventObserver {
            binding.feedSwipeRefresh.isRefreshing = false
            showSnackbar(it)
        })
    }

    private fun observeSwipeUp() {
        binding.feedSwipeRefresh.setOnRefreshListener {
            refreshPosts()
        }
    }

    private fun observeLocation(activity: Activity) {
        LocationService.getInstance(activity).let { locationService ->

            locationService.location.observe(viewLifecycleOwner, Observer {
                location = it
                if (shouldRefreshOnNextLocation) {
                    shouldRefreshOnNextLocation = false
                    refreshPosts()
                }
            })

            locationService.errors.observe(viewLifecycleOwner, EventObserver {
                showSnackbar(it)
                shouldRefreshOnNextLocation = true
                binding.feedSwipeRefresh.isRefreshing = false
            })
        }
    }

    private fun showSnackbar(msg: String) {
        view?.let {
            Snackbar.make(it, msg, Snackbar.LENGTH_LONG).show()
        }
    }

}
