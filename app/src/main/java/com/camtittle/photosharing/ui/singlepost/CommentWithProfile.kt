package com.camtittle.photosharing.ui.singlepost

import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.model.Comment

data class CommentWithProfile(
    val comment: Comment,
    val profile: Profile?
)