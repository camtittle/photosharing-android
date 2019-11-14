package com.camtittle.photosharing.ui.auth.signup

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.SignUpFragmentBinding
import com.camtittle.photosharing.ui.auth.AuthViewModel

class SignUpFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: SignUpFragmentBinding

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        binding.model = viewModel

        addSubmitButtonClickListener()
        addSignInButtonClickListener()
        observeSignUpResult()
    }

    private fun addSubmitButtonClickListener() {
        binding.signUpSubmitButton.setOnClickListener {
            viewModel.signUp()
        }
    }

    private fun addSignInButtonClickListener() {
        binding.signUpSignInButton.setOnClickListener {
            navigateToSignIn()
        }
    }


    private fun observeSignUpResult() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
            if (!it.confirmed) {
                navigateToUnconfirmedAccount()
            }
        })
    }

    private fun navigateToUnconfirmedAccount() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToUnconfirmedAccountFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignIn() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)

    }


}
