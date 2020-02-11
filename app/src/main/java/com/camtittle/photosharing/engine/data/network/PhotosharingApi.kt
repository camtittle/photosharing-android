package com.camtittle.photosharing.engine.data.network

import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.model.*
import retrofit2.Call
import retrofit2.http.*

interface PhotosharingApi {

    @GET("feed")
    fun getPosts(): Call<List<FeedPost>>

    @POST("posts")
    fun createPost(@Header("Authorization") token: String,
                   @Body body: CreateImagePostRequest): Call<CreatedPost>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<SinglePost>

    @GET("comments")
    fun getComments(@Query("postId") postId: String): Call<List<Comment>>


    @POST("comments")
    fun addComment(@Header("Authorization") token: String,
                   @Body body: AddCommentRequest): Call<AddCommentResponse>

    @POST("profile")
    fun updateProfile(@Header("Authorization") token: String,
                      @Body body: UpdateProfileRequest): Call<Unit>

    @GET("profile")
    fun getProfiles(@Query("userIds") userIds: String): Call<Map<String, Profile>>

}