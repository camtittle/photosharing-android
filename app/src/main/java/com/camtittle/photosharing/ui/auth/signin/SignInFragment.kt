package com.camtittle.photosharing.ui.auth.signin

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.camtittle.photosharing.databinding.SignInFragmentBinding
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.ui.auth.AuthViewModel
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.ui.KeyboardUtils
import com.google.android.material.snackbar.Snackbar


class SignInFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: SignInFragmentBinding

    companion object {
        fun newInstance() = SignInFragment()
    }

    private val logTag = SignInFragment::class.java.name

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = SignInFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        binding.model = viewModel

        setHasOptionsMenu(false)

        addSignUpButtonClickListener()
        addSignInButtonClickListener()
        observeSignInResponse()
    }

    private fun addSignUpButtonClickListener() {
        binding.signInSignUpButton.setOnClickListener {
            hideKeyboard()
            navigateToSignUp()
        }
    }

    private fun addSignInButtonClickListener() {
        binding.signInSubmitButton.setOnClickListener {
            hideKeyboard()
            viewModel.signIn()
        }
    }

    private fun observeSignInResponse() {
        viewModel.signInResponse.observe(viewLifecycleOwner, EventObserver {
            Log.d(logTag, "observing sign in response")
            when (it.status) {
                Result.Status.ERROR -> toast(it.message ?: "An error occurred")
                Result.Status.SUCCESS -> navigateToFeed()
            }

        })
    }

    private fun toast(msg: String) {
        Snackbar.make(binding.main, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToSignUp() {
        val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(action)
    }

    private fun navigateToFeed() {
        val action = SignInFragmentDirections.actionSignInFragmentToFeedFragment()
        findNavController().navigate(action)
    }

    private fun hideKeyboard() {
        KeyboardUtils.hide(activity)
    }

}
