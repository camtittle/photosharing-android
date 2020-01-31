package com.camtittle.photosharing.ui.createpost

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.CreateImagePostRequest
import com.camtittle.photosharing.engine.data.network.model.CreatedPost
import com.camtittle.photosharing.engine.image.ImageUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostViewModel : ViewModel() {

    lateinit var currentPhotoPath: String
    var description = ObservableField<String>("")
    var imageBitmap: Bitmap? = null

    private val tag = CreatePostViewModel::class.java.name

    private val _creationResult = MutableLiveData<CreatedPost>()
    val creationResult: LiveData<CreatedPost> = _creationResult

    fun submitPost() {
        // todo handle error nicely
        val request = buildImagePostRequest() ?: return

        val token = AuthManager.getIdToken()
        Log.d(tag, "token: $token")

        ApiService.postApi.createPost(token, request).enqueue(object : Callback<CreatedPost> {

            override fun onResponse(call: Call<CreatedPost>, response: Response<CreatedPost>) {
                Log.d(tag, "createPost Status code:" + response.message())
                _creationResult.postValue(response.body())
            }

            override fun onFailure(call: Call<CreatedPost>, t: Throwable) {
                Log.e(tag, "createPost failed: " + t.message)
                _creationResult.postValue(null)
            }

        })

    }

    private fun buildImagePostRequest(): CreateImagePostRequest? {
        val currentDescription = description.get()
        Log.d(tag, "desc: $currentDescription")
        if (currentDescription.isNullOrEmpty() || imageBitmap == null) {
            // TODO emit an "error" observable
            return null
        }

        val model = CreateImagePostRequest(getImageBase64(), currentDescription)
        model.longitude = 50.0
        model.latitude = 120.0

        return model
    }

    private fun getImageBase64(): String {
        return imageBitmap.let {
            if (it == null) throw Exception("Cannot get image as base64 - bitmap is null")
            ImageUtils.compressBitmapToJpeg(it).let { bytes ->
                val b64 = ImageUtils.getBase64(bytes)
                Log.d(tag, b64)
                b64
            }
        }
    }
}
