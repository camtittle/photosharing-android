package com.camtittle.photosharing.engine.data.network.model

class CreateImagePostRequest(
    var base64Image: String,
    var description: String,
    var latitude: Double,
    var longitude: Double) {

    val type: PostType = PostType.IMAGE

}