package com.camtittle.photosharing.ui.feed

import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.model.FeedPost
import com.camtittle.photosharing.ui.shared.CorePostModel

data class FeedItemContainer (
    val post: CorePostModel,
    val profile: Profile?
)