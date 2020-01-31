package com.camtittle.photosharing.engine.data.network.model

class Post {

    var id: String = ""
    var postType: Int = 0
    var imageUrl: String? = null
    var timestamp: Long = 0
    var userId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var description: String = ""
    var commentCount: Int = 0

}