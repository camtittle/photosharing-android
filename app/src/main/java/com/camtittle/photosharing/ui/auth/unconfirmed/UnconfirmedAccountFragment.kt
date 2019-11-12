package com.camtittle.photosharing.ui.auth.unconfirmed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.camtittle.photosharing.databinding.UnconfirmedAccountFragmentBinding

class UnconfirmedAccountFragment : Fragment() {

    private lateinit var viewModel: UnconfirmedAccountViewModel
    private lateinit var binding: UnconfirmedAccountFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = UnconfirmedAccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UnconfirmedAccountViewModel::class.java)
        addButtonClickListener()

        val args = UnconfirmedAccountFragmentArgs.fromBundle(arguments)
        args.confirmationLinkDestination.let {
            viewModel.message = it
        }

        binding.model = viewModel
    }

    private fun addButtonClickListener() {
        binding.checkAccountStatusButton.setOnClickListener {
            viewModel.onClickCheckStatusButton()
        }
    }

}