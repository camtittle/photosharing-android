package com.camtittle.photosharing.ui.singlepost

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.Comment
import com.camtittle.photosharing.engine.data.network.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePostViewModel : ViewModel() {

    var commentText = ObservableField<String>()
    var postId: String? = null

    private val _post = MutableLiveData<Result<Post>>()
    val post: LiveData<Result<Post>> = _post

    private val _comments = MutableLiveData<Result<List<Comment>>>()
    val comments: LiveData<Result<List<Comment>>> = _comments

    private val TAG = SinglePostViewModel::class.java.name

    fun refresh() {
        postId?.let {postId ->
            if (postId.isEmpty()) {
                return
            }

            getPost(postId)
            getComments(postId)
        }
    }

    private fun getPost(postId: String) {
        // Emit a loading result
        _post.postValue(Result.loading())

        ApiService.api.getPost(postId).enqueue(object : Callback<Post?> {
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

    private fun getComments(postId: String) {
        _comments.postValue(Result.loading())

        ApiService.api.getComments(postId).enqueue(object : Callback<List<Comment>?> {
            override fun onFailure(call: Call<List<Comment>?>, t: Throwable) {
                Log.e(TAG, "Failed to fetch comments for post with id $postId")
                _comments.postValue(Result.error(t.message ?: "Error fetching comments"))
            }

            override fun onResponse(
                call: Call<List<Comment>?>,
                response: Response<List<Comment>?>
            ) {
                response.body().let {
                    if (it == null) {
                        _comments.postValue(Result.error("Error fetching comments. Status code: ${response.code()}"))
                    } else {
                        _comments.postValue(Result.success(it))
                    }
                }
            }
        })
    }


}
