package com.camtittle.photosharing.ui.auth.signup

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.SignUpFragmentBinding
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.ui.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar

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
        setLoading(false)
    }

    private fun addSubmitButtonClickListener() {
        binding.signUpSubmitButton.setOnClickListener {
            setLoading(true)
            viewModel.signUp()
        }
    }

    private fun addSignInButtonClickListener() {
        binding.signUpSignInButton.setOnClickListener {
            navigateToSignIn()
        }
    }


    private fun observeSignUpResult() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, EventObserver {
            when (it.status) {
                Result.Status.ERROR -> {
                    showError(it.message ?: "Something went wrong")
                    setLoading(false)
                }
                Result.Status.SUCCESS -> {
                    Log.d("SignUpFragment", "success: " + it.data?.confirmed)
                    it.data?.let { response ->
                        if (!response.confirmed) navigateToUnconfirmedAccount()
                        else navigateToSignIn()
                    }
                }
            }
        })
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.main, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToUnconfirmedAccount() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToUnconfirmedAccountFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignIn() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)

    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            binding.signUpProgressBar.visibility = View.VISIBLE
            binding.signUpSubmitButton.visibility = View.GONE
        } else {
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpSubmitButton.visibility = View.VISIBLE
        }
    }


}
