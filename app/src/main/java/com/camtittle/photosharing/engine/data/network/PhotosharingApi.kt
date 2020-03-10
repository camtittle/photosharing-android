package com.camtittle.photosharing.engine.data.network

import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.model.*
import retrofit2.Call
import retrofit2.http.*

interface PhotosharingApi {

    @GET("feed")
    fun getFeed(@Header("Authorization") token: String,
                @Query("lat") lat: Double,
                @Query("lon") long: Double): Call<List<FeedPost>>

    @POST("posts")
    fun createPost(@Header("Authorization") token: String,
                   @Body body: CreateImagePostRequest): Call<CreatedPost>

    @GET("posts/{id}")
    fun getPost(@Header("Authorization") token: String,
                @Path("id") id: String): Call<SinglePost>

    @POST("posts/vote")
    fun vote(@Header("Authorization") token: String,
             @Body body: VoteRequest): Call<Void>

    @GET("comments")
    fun getComments(@Query("postId") postId: String): Call<List<Comment>>


    @POST("comments")
    fun addComment(@Header("Authorization") token: String,
                   @Body body: AddCommentRequest): Call<AddCommentResponse>

    @POST("profile")
    fun updateProfile(@Header("Authorization") token: String,
                      @Body body: UpdateProfileRequest): Call<Void>

    @GET("profile/batch")
    fun getProfiles(@Query("userIds") userIds: String): Call<Map<String, Profile>>

    @GET("profile")
    fun getProfile(@Query("userId") userId: String): Call<Profile>

}