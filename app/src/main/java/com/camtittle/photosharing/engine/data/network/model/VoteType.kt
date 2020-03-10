package com.camtittle.photosharing.engine.data.network.model

import com.google.gson.annotations.SerializedName

enum class VoteType {
    @SerializedName("up")
    UP,

    @SerializedName("down")
    DOWN
}