package com.camtittle.photosharing.ui.auth.signin

import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import com.camtittle.photosharing.ui.KeyboardUtils


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
            navigateToSignUp()
        }
    }

    private fun addSignInButtonClickListener() {
        binding.signInSubmitButton.setOnClickListener {
            Log.d(logTag, "SIGN IN CLICK ${viewModel.model.email}")
            hideKeyboard()
            viewModel.signIn()
        }
    }

    private fun observeSignInResponse() {
        viewModel.signInResponse.observe(viewLifecycleOwner, EventObserver {
            Log.d(logTag, "observing sign in response")
            navigateToFeed()
        })
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
