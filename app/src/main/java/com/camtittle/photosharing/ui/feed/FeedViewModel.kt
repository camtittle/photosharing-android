package com.camtittle.photosharing.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.Post
import com.camtittle.photosharing.engine.data.network.model.PostsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val tag = FeedViewModel::class.java.name

    fun isSignedIn(): Boolean {
        return AuthManager.isSignedIn()
    }

    fun updatePostsList() {
        Log.d(tag, "updatePostsList")
        ApiService.api.getPosts().enqueue(object : Callback<PostsList> {

            override fun onResponse(call: Call<PostsList>, response: Response<PostsList>) {
                val posts = response.body()
                Log.d(tag, "postCount: " + posts?.posts?.size)
                _posts.postValue(posts?.posts)
            }

            override fun onFailure(call: Call<PostsList>, t: Throwable) {
                Log.e(tag, "Error fetching posts" + t.message)
            }

        })
    }

}
