package com.camtittle.photosharing.ui.auth.unconfirmed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.UnconfirmedAccountFragmentBinding
import com.camtittle.photosharing.ui.auth.AuthViewModel

class UnconfirmedAccountFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: UnconfirmedAccountFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = UnconfirmedAccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        addButtonClickListener()
        observeSignInResponse()
        binding.model = viewModel
    }

    private fun addButtonClickListener() {
        binding.checkAccountStatusButton.setOnClickListener {
            viewModel.confirmAccount()
        }
    }

    private fun observeSignInResponse() {
        viewModel.signInResponse.observe(viewLifecycleOwner, Observer {
            navigateToFeed()
        })
    }

    private fun navigateToFeed() {
        val action = UnconfirmedAccountFragmentDirections.actionUnconfirmedAccountFragmentToFeedFragment()
        findNavController().navigate(action)
    }

}