package com.camtittle.photosharing.engine.data.network.model

class CreateImagePostRequest(
    var base64Image: String,
    var description: String) {

    var latitude: Double? = null
    var longitude: Double? = null

    val type: PostType = PostType.IMAGE

}