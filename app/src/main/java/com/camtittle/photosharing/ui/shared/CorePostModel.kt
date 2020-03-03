package com.camtittle.photosharing.ui.shared

import com.camtittle.photosharing.engine.data.network.model.FeedPost
import com.camtittle.photosharing.engine.data.network.model.SinglePost
import java.text.DateFormat

data class CorePostModel(
    var id: String = "",
    var userId: String = "",
    var imageUrl: String? = null,
    var timestamp: Long = 0,
    var distanceKm: Double = 0.0,
    var description: String = "",
    var commentCount: Int = 0) {

    var formattedDate: String = ""
    var formattedDistance: String = ""

    init {
        formattedDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(timestamp)
        formattedDistance = String.format("%.2fkm away", distanceKm)
    }

    companion object {

        fun fromFeedPost(feedPost: FeedPost): CorePostModel {
            return CorePostModel(feedPost.id, feedPost.userId,
                feedPost.imageUrl, feedPost.timestamp, feedPost.distanceKm,
                feedPost.description, feedPost.commentCount)
        }

        fun fromSinglePost(singlePost: SinglePost): CorePostModel {
            return CorePostModel(singlePost.id, singlePost.userId,
                singlePost.imageUrl, singlePost.timestamp, singlePost.distanceKm,
                singlePost.description, singlePost.commentCount)
        }

    }
}