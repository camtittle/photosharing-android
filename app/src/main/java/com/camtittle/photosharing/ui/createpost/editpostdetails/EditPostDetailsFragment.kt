package com.camtittle.photosharing.ui.createpost.editpostdetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.EditPostDetailsFragmentBinding
import com.camtittle.photosharing.ui.createpost.CreatePostViewModel
import java.io.File

class EditPostDetailsFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var binding: EditPostDetailsFragmentBinding

    companion object {
        fun newInstance() = EditPostDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = EditPostDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.also {
            viewModel = ViewModelProviders.of(it).get(CreatePostViewModel::class.java)
        }
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.currentPhotoPath.isBlank()) {
            navigateToCapturePhotoFragment()
        } else {
            showSavedFileInImageView()
        }
    }

    private fun navigateToCapturePhotoFragment() {
        val action = EditPostDetailsFragmentDirections.actionGlobalCreatePostNavigation()
        findNavController().navigate(action)
    }

    private fun showSavedFileInImageView() {
        File(viewModel.currentPhotoPath).also {
            if (it.exists()) {
                binding.photoPreview.setImageURI(Uri.fromFile(it))
            }
        }
    }

}