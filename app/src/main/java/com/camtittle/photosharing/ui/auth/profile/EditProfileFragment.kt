package com.camtittle.photosharing.ui.auth.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.EditProfileFragmentBinding
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.ui.KeyboardUtils
import com.camtittle.photosharing.ui.auth.AuthViewModel
import com.camtittle.photosharing.ui.auth.signin.SignInFragment

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: EditProfileFragmentBinding

    companion object {
        fun newInstance() = SignInFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        addButtonClickListener()
        observeSaveResponse()
        binding.model = viewModel
    }

    override fun onResume() {
        super.onResume()

        refreshProfile()
    }

    private fun addButtonClickListener() {
        binding.editProfileSubmitButton.setOnClickListener {
            viewModel.saveProfileDetails()
        }
    }

    private fun observeSaveResponse() {
        viewModel.saveProfileDetailsResponse.observe(viewLifecycleOwner, EventObserver {
            KeyboardUtils.hide(activity)
            navigateToFeed()
        })
    }

    private fun navigateToFeed() {
        val action = EditProfileFragmentDirections.actionEditProfileFragmentToFeedFragment()
        findNavController().navigate(action)
    }

    private fun refreshProfile() {
        viewModel.loading.set(true)
        viewModel.refreshProfileData()?.let {
            it.observe(viewLifecycleOwner, Observer { profileResult ->
                viewModel.loading.set(false)
                if (profileResult.status == Result.Status.SUCCESS) {
                    viewModel.model.name = profileResult.data?.name ?: ""
                }
            })
        }

    }

}