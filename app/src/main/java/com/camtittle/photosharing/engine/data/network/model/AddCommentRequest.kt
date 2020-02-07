package com.camtittle.photosharing.engine.data.network.model

data class AddCommentRequest(
    val postId: String,
    val postTimestamp: Long,
    val content: String
)