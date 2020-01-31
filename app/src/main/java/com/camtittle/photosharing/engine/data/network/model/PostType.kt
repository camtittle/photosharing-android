package com.camtittle.photosharing.engine.data.network.model

import com.google.gson.annotations.SerializedName

enum class PostType {
    @SerializedName("image")
    IMAGE,

    @SerializedName("text")
    TEXT
}