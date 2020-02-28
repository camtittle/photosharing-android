package com.camtittle.photosharing.ui.createpost.editpostdetails

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.camtittle.photosharing.databinding.EditPostDetailsFragmentBinding
import com.camtittle.photosharing.ui.createpost.CreatePostViewModel
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import com.camtittle.photosharing.engine.common.result.EventObserver
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.image.ImageUtils
import com.camtittle.photosharing.ui.KeyboardUtils
import com.camtittle.photosharing.ui.createpost.LatLong
import com.camtittle.photosharing.ui.createpost.LocationUtil
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.io.FileNotFoundException


class EditPostDetailsFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var binding: EditPostDetailsFragmentBinding

    private lateinit var locationManager: FusedLocationProviderClient

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
            binding.viewModel = viewModel

            observeCreationResult()
            addSubmitButtonClickListener()
            locationManager = LocationServices.getFusedLocationProviderClient(it)
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

    override fun onResume() {
        super.onResume()
        activity?.let { setupLocationClient(it) }
    }

    private fun navigateToCapturePhotoFragment() {
        val action = EditPostDetailsFragmentDirections.actionGlobalCreatePostNavigation()
        findNavController().navigate(action)
    }

    private fun showSavedFileInImageView() {
        getSavedBitmap()?.also {
            scaleBitmap(it).also { scaledBitmap ->
                binding.photoPreview.setImageBitmap(scaledBitmap)
                viewModel.imageBitmap = scaledBitmap

                val b64 = ImageUtils.getBase64(ImageUtils.compressBitmapToJpeg(scaledBitmap))
                Log.d("EditPost", b64.substring(0, 1000))
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

    private fun addSubmitButtonClickListener() {
        binding.createPostSubmitButton.setOnClickListener {
            KeyboardUtils.hide(activity)
            viewModel.latlong.get().let {
                if (it == null) {
                    showSnackbar("Cannot submit post without location")
                } else {
                    viewModel.submitPost()
                }
            }
        }
    }

    private fun observeCreationResult() {
        viewModel.creationResult.observe(viewLifecycleOwner, EventObserver {
            when (it.status) {
                Result.Status.LOADING -> setLoading(true)
                Result.Status.ERROR -> {
                    setLoading(false)
                    showSnackbar(it.message ?: "Something went wrong")
                }
                Result.Status.SUCCESS -> {
                    showSnackbar("Post created successfully")
                    navigateToFeed()
                }
            }
        })
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            binding.createPostSubmitButton.visibility = View.GONE
            binding.submitPostLoadingBar.visibility = View.VISIBLE
        } else {
            binding.createPostSubmitButton.visibility = View.VISIBLE
            binding.submitPostLoadingBar.visibility = View.GONE
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToFeed() {
        val action = EditPostDetailsFragmentDirections.actionGlobalFeedFragment()
        findNavController().navigate(action)
    }

    private fun setupLocationClient(activity: Activity) {
        LocationUtil.checkLocationPermission(activity)

        val locationRequest = LocationUtil.createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            locationManager.requestLocationUpdates(locationRequest, object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        Log.d("EditPostDetails", "Location: ${location.latitude}, ${location.longitude}")
                        viewModel.latlong.set(LatLong(location.latitude, location.longitude))
                    }
                }

            }, Looper.getMainLooper())
        }

        task.addOnFailureListener {
            showSnackbar("Error getting location. Ensure location services are enabled and try again")
        }
    }

}