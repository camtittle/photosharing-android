package com.camtittle.photosharing.ui.auth.unconfirmed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.UnconfirmedAccountFragmentBinding
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.ui.KeyboardUtils
import com.camtittle.photosharing.ui.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoading(false)
    }

    private fun addButtonClickListener() {
        binding.checkAccountStatusButton.setOnClickListener {
            setLoading(true)
            viewModel.confirmAccount()
        }
    }

    private fun observeSignInResponse() {
        viewModel.signInResponse.observe(viewLifecycleOwner, EventObserver {
            setLoading(false)
            when (it.status) {
                Result.Status.SUCCESS -> {
                    navigateToEditProfile()
                    KeyboardUtils.hide(activity)
                }
                Result.Status.ERROR -> {
                    showError(it.message ?: "Something went wrong")
                }
            }
        })
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToEditProfile() {
        val action = UnconfirmedAccountFragmentDirections.actionUnconfirmedAccountFragmentToEditProfileFragment()
        findNavController().navigate(action)
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            binding.confirmProgressBar.visibility = View.VISIBLE
            binding.checkAccountStatusButton.visibility = View.GONE
        } else {
            binding.confirmProgressBar.visibility = View.GONE
            binding.checkAccountStatusButton.visibility = View.VISIBLE
        }
    }

}