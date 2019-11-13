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

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: SignUpFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)

        addButtonClickListener()
        observeSignUpResult()

        binding.model = viewModel
    }

    private fun addButtonClickListener() {
        binding.signUpSubmitButton.setOnClickListener {
            viewModel.onClickSubmitButton()
        }
    }


    private fun observeSignUpResult() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
            if (!it.confirmed) {
                navigateToUnconfirmedAccount(viewModel.signUp.email)
            }
        })
    }

    private fun navigateToUnconfirmedAccount(confirmationDestination: String) {
        val action = SignUpFragmentDirections.actionSignUpFragmentToUnconfirmedAccountFragment(confirmationDestination)
        findNavController().navigate(action)
    }


}
