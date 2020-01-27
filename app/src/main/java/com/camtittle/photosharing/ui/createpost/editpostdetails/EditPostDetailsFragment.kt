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
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.R.attr.bitmap
import android.util.Log
import android.widget.Toast
import com.camtittle.photosharing.engine.image.ImageUtils
import java.io.FileNotFoundException


class EditPostDetailsFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var binding: EditPostDetailsFragmentBinding

    private val maxImageSizePx = 1500

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
        getSavedBitmap()?.also {
            scaleBitmap(it).also { scaledBitmap ->
                Toast.makeText(context, "${scaledBitmap.width} x ${scaledBitmap.height}", Toast.LENGTH_LONG).show()
                binding.photoPreview.setImageBitmap(scaledBitmap)

                ImageUtils.compressBitmapToJpeg(scaledBitmap).also { jpeg ->
                    val b64 = ImageUtils.getBase64(jpeg)
                    Log.d("EditPostDetails", "" + b64.length)
                }
            }
        }

    }

    private fun getSavedBitmap(): Bitmap? {
        if (!viewModel.currentPhotoPath.isBlank()) {
            return try {
                BitmapFactory.decodeFile(viewModel.currentPhotoPath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        return null
    }

    private fun scaleBitmap(bm: Bitmap): Bitmap {
        return if (bm.width > maxImageSizePx || bm.height > maxImageSizePx) {
            ImageUtils.scaleBitmapToMaxSize(bm, maxImageSizePx)
        } else {
            bm
        }
    }

}