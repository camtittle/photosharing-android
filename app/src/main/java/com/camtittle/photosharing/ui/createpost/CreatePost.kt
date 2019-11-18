package com.camtittle.photosharing.ui.createpost

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.camtittle.photosharing.R
import com.camtittle.photosharing.databinding.CreatePostFragmentBinding

class CreatePost : Fragment() {

    companion object {
        fun newInstance() = CreatePost()
    }

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var binding: CreatePostFragmentBinding

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreatePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreatePostViewModel::class.java)
        addSelectPhotoButtonListener()
    }

    private fun addSelectPhotoButtonListener() {
        binding.openCameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        activity?.packageManager?.let { packageManager ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

}
