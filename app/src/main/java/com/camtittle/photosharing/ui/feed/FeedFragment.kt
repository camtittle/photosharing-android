package com.camtittle.photosharing.ui.feed

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.camtittle.photosharing.R

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        ensureAuthorised()
    }

    private fun ensureAuthorised() {
        Log.d(tag, "Ensure authorised")
        if (!viewModel.isSignedIn()) {
            val action = FeedFragmentDirections.actionGlobalAuthNavigation()
            findNavController().navigate(action)
        }
    }

}