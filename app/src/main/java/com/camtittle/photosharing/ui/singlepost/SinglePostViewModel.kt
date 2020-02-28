package com.camtittle.photosharing.ui.singlepost

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.common.livedata.CombinedLiveData
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.*
import com.camtittle.photosharing.engine.data.repository.ProfileRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePostViewModel : ViewModel() {

    var commentText = ObservableField<String>()
    var postId: String = ""

    private val _submitComment = MutableLiveData<Result<AddCommentResponse>>()
    val submitComment: LiveData<Result<AddCommentResponse>> = _submitComment

    private val _post = MutableLiveData<Result<SinglePost>>()
    val post: LiveData<Result<SinglePost>> = _post

    var profile: LiveData<Profile> = Transformations.switchMap(post) {
        if (it == null || it.status != Result.Status.SUCCESS || it.data == null) {
            MutableLiveData<Profile>()
        } else {
            val userId = it.data.userId
            Transformations.map(ProfileRepository.getProfiles(listOf(userId))) { result ->
                if (result.status != Result.Status.SUCCESS || result.data == null) {
                    Profile()
                } else {
                    result.data[userId]
                }
            }
        }
    }

    private val _comments = MutableLiveData<Result<List<Comment>>>()
    private val comments: LiveData<Result<List<Comment>>> = _comments

    private val commentsProfiles: LiveData<Result<Map<String, Profile>>> = Transformations.switchMap(comments) {
        if (it == null || it.status != Result.Status.SUCCESS || it.data == null) {
            MutableLiveData<Result<Map<String, Profile>>>()
        } else {
            Log.d(TAG, "fetching profiles for comments")
            it.data.map { comment -> comment.userId }.distinct().let { userIds ->
                ProfileRepository.getProfiles(userIds)
            }
        }
    }

    val commentsProfilesCombined: LiveData<Result<List<CommentWithProfile>>> =
        CombinedLiveData(comments, commentsProfiles) { newComments, newProfiles ->
        Log.d(TAG, "combined live data")
        mapToCommentProfileContainers(newComments, newProfiles)
    }

    private val TAG = SinglePostViewModel::class.java.name

    fun refresh() {
        postId.let {postId ->
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

        ApiService.api.getPost(postId).enqueue(object : Callback<SinglePost?> {
            override fun onFailure(call: Call<SinglePost?>, t: Throwable) {
                Log.e(TAG, "Failed to fetch post with id $postId")
                _post.postValue(Result.error(t.message ?: "Error fetching post"))
            }

            override fun onResponse(call: Call<SinglePost?>, response: Response<SinglePost?>) {
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

    fun submitComment() {
        Log.d(TAG, "Click comment submit")
        val commentContent = commentText.get()
        Log.d(TAG, "comment: $commentContent")
        if (commentContent.isNullOrEmpty()) return

        val post = _post.value?.data ?: return
        val accessToken = AuthManager.getIdToken()
        val request = AddCommentRequest(post.id, post.timestamp, commentContent)

        Log.d(TAG, "Submitting comment: $commentContent")

        ApiService.api.addComment(accessToken, request).enqueue(object : Callback<AddCommentResponse> {
            override fun onFailure(call: Call<AddCommentResponse>, t: Throwable) {
                Log.e(TAG, t.message ?: "Error submitting comment")
                _submitComment.postValue(Result.error(t.message ?: "Error submitting comment"))
            }

            override fun onResponse(call: Call<AddCommentResponse>, response: Response<AddCommentResponse>) {
                val value = response.body()?.let { Result.success(it) }
                    ?: Result.error("Error submitting comment. Code: ${response.code()}")
                _submitComment.postValue(value)
            }
        })
    }

    fun resetCommentForm() {
        commentText.set("")
    }

    private fun mapToCommentProfileContainers(comments: Result<List<Comment>>?,
                                              profiles: Result<Map<String, Profile>>?)
            : Result<List<CommentWithProfile>> {

        if (comments != null && comments.status == Result.Status.ERROR) {
            return Result.error("Error fetching comments")
        }

        if (comments == null || comments.status != Result.Status.SUCCESS || comments.data == null)
            return Result.success(emptyList())

        val profilesData =
            if (profiles != null && profiles.status == Result.Status.SUCCESS && profiles.data != null) profiles.data
            else emptyMap()

        return Result.success(comments.data.map {
            Log.d(TAG, "mapping the name: " + profilesData[it.userId]?.name ?: "")
            CommentWithProfile(
                it,
                profilesData[it.userId]
            )
        })
    }


}
