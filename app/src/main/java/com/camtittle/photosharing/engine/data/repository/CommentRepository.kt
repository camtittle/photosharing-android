package com.camtittle.photosharing.engine.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.camtittle.photosharing.ui.singlepost.CommentWithProfile
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.model.Profile

object CommentRepository {

    private val TAG = CommentRepository::class.java.name

    fun getCommentsWithProfiles(postId: String): LiveData<Result<List<CommentWithProfile>>> {

        val commentsWithProfile = MutableLiveData<Result<List<CommentWithProfile>>>()

        ApiService.api.getComments(postId).enqueue(object : Callback<List<Comment>> {
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Log.e(TAG, "Error fetching comments for post: " + t.message)
                commentsWithProfile.postValue(Result.error(t.message ?: "Error fetching comments"))
            }

            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                response.body().let {
                    if (it == null) commentsWithProfile.postValue(Result.error("Error fetching comments"))
                    else {
                        commentsWithProfile.postValue(Result.success(mapCommentsWithProfiles(it, emptyMap())))
                        getProfilesForComments(commentsWithProfile, it)
                    }
                }
            }

        })

        return commentsWithProfile
    }

    private fun getProfilesForComments(result: MutableLiveData<Result<List<CommentWithProfile>>>, comments: List<Comment>) {

    }

    private fun mapCommentsWithProfiles(comments: List<Comment>, profiles: Map<String, Profile>?):
            List<CommentWithProfile> {
        return comments.map {
            CommentWithProfile(
                it,
                profiles?.get(it.userId)
            )
        }
    }

}