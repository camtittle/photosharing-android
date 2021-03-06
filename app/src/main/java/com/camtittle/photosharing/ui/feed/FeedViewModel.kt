package com.camtittle.photosharing.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.common.livedata.CombinedLiveData
import com.camtittle.photosharing.engine.common.result.Event
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.FeedPost
import com.camtittle.photosharing.engine.data.network.model.VoteRequest
import com.camtittle.photosharing.engine.data.network.model.VoteType
import com.camtittle.photosharing.engine.data.repository.ProfileRepository
import com.camtittle.photosharing.ui.shared.CorePostModel
import com.camtittle.photosharing.ui.shared.FeedItemContainer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel : ViewModel() {

    private val posts = MutableLiveData<List<FeedPost>>()

    private val _errors = MutableLiveData<Event<String>>()
    val errors: LiveData<Event<String>> = _errors

    var profiles: LiveData<Result<Map<String, Profile>>> = Transformations.switchMap(posts) {
        if (it == null) return@switchMap MutableLiveData<Result<Map<String, Profile>>>()
        val postIds = it.map { post -> post.userId }.distinct()
        ProfileRepository.getProfiles(postIds)
    }

    var feedItems = CombinedLiveData(posts, profiles) { newPosts, newProfiles ->
        val profileMap = if (newProfiles?.status == Result.Status.SUCCESS) newProfiles.data else null
        mapToFeedItemContainers(newPosts, profileMap)
    }

    private val tag = FeedViewModel::class.java.name

    fun isSignedIn(): Boolean {
        return AuthManager.isSignedIn()
    }

    fun updatePostsList(lat: Double, lon: Double) {
        Log.d(tag, "Getting feed for location $lat, $lon")

        val token = AuthManager.getIdToken()
        ApiService.api.getFeed(token, lat, lon).enqueue(object : Callback<List<FeedPost>> {

            override fun onResponse(call: Call<List<FeedPost>>, response: Response<List<FeedPost>>) {
                response.body().let {
                    if (it == null) {
                        _errors.postValue(Event("Error fetching feed. Status code: ${response.code()}"))
                    } else {
                        posts.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<FeedPost>>, t: Throwable) {
                Log.e(tag, "Error fetching posts" + t.message)
                _errors.postValue(Event("Error fetching feed. Check your network connection."))
            }

        })

    }

    fun submitVote(postId: String, voteType: VoteType) {
        Log.d(tag, "vote submitted for postId $postId")
        val token = AuthManager.getIdToken()
        val request = VoteRequest(postId, voteType)
        ApiService.api.vote(token, request).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                response.code().let {
                    if (it < 200 || it > 299) {
                        _errors.postValue(Event("Error submitting vote. Status code: ${response.code()}"))
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(tag, "Error submitting vote" + t.message)
                _errors.postValue(Event("Error submitting vote. Check your network connection."))
            }
        })
    }

    private fun mapToFeedItemContainers(posts: List<FeedPost>?,
                                        profiles: Map<String, Profile>?): List<FeedItemContainer> {
        if (posts == null) {
            return emptyList()
        }

        return posts.map {
            FeedItemContainer(
                CorePostModel.fromFeedPost(
                    it
                ), profiles?.get(it.userId)
            )
        }
    }

}
