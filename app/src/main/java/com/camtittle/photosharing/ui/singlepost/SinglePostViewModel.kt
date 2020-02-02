package com.camtittle.photosharing.ui.singlepost

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePostViewModel : ViewModel() {

    var commentText = ObservableField<String>()
    var postId: String? = null

    private val _post = MutableLiveData<Result<Post>>()
    val post: LiveData<Result<Post>> = _post

    private val TAG = SinglePostViewModel::class.java.name

    fun refresh() {
        postId.let {postId ->
            if (postId.isNullOrEmpty()) {
                return
            }

            // Emit a loading result
            _post.postValue(Result.loading())

            ApiService.postApi.getPost(postId).enqueue(object : Callback<Post?> {
                override fun onFailure(call: Call<Post?>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch post with id $postId")
                    _post.postValue(Result.error(t.message ?: "Error fetching post"))
                }

                override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                    response.body().let {
                        if (it == null) {
                            _post.postValue(Result.error("Error fetching post. Status code: ${response.code()}"))
                        } else {
                            _post.postValue(Result.success(it))
                        }
                    }
                }
            })
        }
    }


}
