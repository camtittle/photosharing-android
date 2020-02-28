package com.camtittle.photosharing.ui.createpost

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.common.result.Event
import com.camtittle.photosharing.engine.common.result.ResultEvent
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
    var latlong = ObservableField<LatLong?>()

    private val tag = CreatePostViewModel::class.java.name

    private val _creationResult = MutableLiveData<ResultEvent<CreatedPost>>()
    val creationResult: LiveData<ResultEvent<CreatedPost>> = _creationResult

    fun submitPost() {
        val request = buildImagePostRequest() ?: return
        val token = AuthManager.getIdToken()

        _creationResult.postValue(ResultEvent.loading())

        ApiService.api.createPost(token, request).enqueue(object : Callback<CreatedPost> {

            override fun onResponse(call: Call<CreatedPost>, response: Response<CreatedPost>) {
                Log.d(tag, "createPost Status code:" + response.message())
                if (response.isSuccessful) {
                    response.body()?.let { _creationResult.postValue(ResultEvent.success(it)) }
                    clearModel()
                } else {
                    _creationResult.postValue(ResultEvent.error("Something went wrong. Status Code: " + response.code()))
                }
            }

            override fun onFailure(call: Call<CreatedPost>, t: Throwable) {
                Log.e(tag, "createPost failed: " + t.message)
                _creationResult.postValue(ResultEvent.error(t.message ?: "Something went wrong. Please try again"))
            }

        })

    }

    private fun buildImagePostRequest(): CreateImagePostRequest? {
        val currentDescription = description.get()
        if (currentDescription.isNullOrEmpty()) {
            _creationResult.postValue(ResultEvent.error("Description required"))
            return null
        }

        if (imageBitmap == null) {
            _creationResult.postValue(ResultEvent.error("No Image found. Please try again"))
            return null
        }

        val currentLatLong = latlong.get()
        if (currentLatLong == null) {
            _creationResult.postValue(ResultEvent.error("Cannot post without location"))
            return null
        }

        val model = CreateImagePostRequest(getImageBase64(), currentDescription)
        model.longitude = currentLatLong.lat
        model.latitude = currentLatLong.long

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

    private fun clearModel() {
        description.set("")
    }
}
