package com.camtittle.photosharing.engine.data.network

import com.camtittle.photosharing.engine.data.network.model.CreateImagePostRequest
import com.camtittle.photosharing.engine.data.network.model.CreatedPost
import com.camtittle.photosharing.engine.data.network.model.Post
import com.camtittle.photosharing.engine.data.network.model.PostsList
import retrofit2.Call
import retrofit2.http.*

interface PostApi {

    @GET("posts")
    fun getPosts(): Call<PostsList>

    @POST("posts")
    fun createPost(@Header("Authorization") token: String,
                   @Body body: CreateImagePostRequest): Call<CreatedPost>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<Post>

}