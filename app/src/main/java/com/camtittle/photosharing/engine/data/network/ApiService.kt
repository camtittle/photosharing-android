package com.camtittle.photosharing.engine.data.network

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object ApiService {

    val postApi: PostApi

    // private const val baseUrl = "http://10.0.2.2:3000/"
    private const val baseUrl = "https://sh3pakf5e3.execute-api.eu-central-1.amazonaws.com/dev/"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        postApi = retrofit.create(PostApi::class.java)
    }

}