package com.camtittle.photosharing.engine.data.network

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object ApiService {

    val api: PhotosharingApi

//     private const val baseUrl = "http://10.0.2.2:3000/"
//    private const val baseUrl = "https://sh3pakf5e3.execute-api.eu-central-1.amazonaws.com/dev/"
    private const val baseUrl = "http://7f50a7c0.ngrok.io/"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(PhotosharingApi::class.java)
    }

}