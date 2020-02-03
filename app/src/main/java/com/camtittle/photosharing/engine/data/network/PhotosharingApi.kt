package com.camtittle.photosharing.engine.data.network

import com.camtittle.photosharing.engine.data.network.model.*
import retrofit2.Call
import retrofit2.http.*

interface PhotosharingApi {

    @GET("posts")
    fun getPosts(): Call<PostsList>

    @POST("posts")
    fun createPost(@Header("Authorization") token: String,
                   @Body body: CreateImagePostRequest): Call<CreatedPost>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<Post>

    @GET("comments")
    fun getComments(@Query("postId") postId: String): Call<List<Comment>>

}