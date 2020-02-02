package com.camtittle.photosharing.ui.singlepost

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.camtittle.photosharing.R

class SinglePostFragment : Fragment() {

    companion object {
        fun newInstance() = SinglePostFragment()
    }

    private lateinit var viewModel: SinglePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.single_post_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SinglePostViewModel::class.java)
    }

}
