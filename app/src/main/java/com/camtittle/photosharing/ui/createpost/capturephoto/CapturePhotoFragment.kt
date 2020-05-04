package com.camtittle.photosharing.ui.createpost.capturephoto

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.R

import com.camtittle.photosharing.databinding.CreatePostFragmentBinding
import com.camtittle.photosharing.ui.createpost.CreatePostViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*





class CapturePhotoFragment : Fragment() {

    companion object {
        fun newInstance() = CapturePhotoFragment()
    }

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var binding: CreatePostFragmentBinding

    private val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE = 2
    private val fileProviderAuthority = "com.camtittle.photosharing.fileprovider"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreatePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.also {
            viewModel = ViewModelProviders.of(it).get(CreatePostViewModel::class.java)
        }

        addSelectPhotoButtonListener()
        addOpenCameraButtonListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearModel()
    }

    private fun addOpenCameraButtonListener() {
        binding.openCameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }


    private fun addSelectPhotoButtonListener() {
        binding.selectPhotoButton.setOnClickListener {
            dispatchPickImageIntent()
        }
    }

    private fun dispatchPickImageIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun dispatchTakePictureIntent() {

        activity?.packageManager?.let { packageManager ->

            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                showCameraMissingError()
                return@dispatchTakePictureIntent
            }

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {

                    // Create new file for photo to save into
                    val photoFile = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        showGenericCaptureError()
                        null
                    }

                    // Only continue if file successfully created
                    photoFile?.also {
                        activity?.also { activity ->
                            val photoURI = FileProvider.getUriForFile(
                                activity.applicationContext,
                                fileProviderAuthority,
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            }
        }
    }

    private fun showCameraMissingError() {
        getString(R.string.no_camera_error).let { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showGenericCaptureError() {
        getString(R.string.capture_error).let { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            navigateToEditDetailsFragment()
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            val url = data?.data;
            if (data == null || url == null) {
                getString(com.camtittle.photosharing.R.string.capture_error).let { message ->
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
            context?.let {
                val stream = it.contentResolver.openInputStream(url)
                val bitmap = BitmapFactory.decodeStream(stream)
                viewModel.imageBitmap = bitmap
                navigateToEditDetailsFragment()
            }
        }


    }

    private fun navigateToEditDetailsFragment() {
        val action = CapturePhotoFragmentDirections.actionCreatePostToEditPostDetailsFragment()
        findNavController().navigate(action)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES).let { storageDir ->
            return@createImageFile File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                viewModel.currentPhotoPath = absolutePath
            }
        }

    }
}
