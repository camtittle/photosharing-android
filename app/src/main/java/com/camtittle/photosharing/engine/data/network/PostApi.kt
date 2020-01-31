package com.camtittle.photosharing.engine.data.network

import com.camtittle.photosharing.engine.data.network.model.CreateImagePostRequest
import com.camtittle.photosharing.engine.data.network.model.CreatedPost
import com.camtittle.photosharing.engine.data.network.model.PostsList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PostApi {

    @GET("posts")
    fun getPosts(): Call<PostsList>

    @POST("posts")
    fun createPost(@Header("Authorization") token: String,
                   @Body body: CreateImagePostRequest): Call<CreatedPost>

}