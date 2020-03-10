package com.camtittle.photosharing.ui.shared

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.camtittle.photosharing.engine.data.network.model.FeedPost
import com.camtittle.photosharing.engine.data.network.model.SinglePost
import com.camtittle.photosharing.engine.data.network.model.VoteType
import java.text.DateFormat

data class CorePostModel(
    var id: String = "",
    var userId: String = "",
    var imageUrl: String? = null,
    var timestamp: Long = 0,
    var distanceKm: Double = 0.0,
    var description: String = "",
    var commentCount: Int = 0,
    var upvotes: ObservableInt = ObservableInt(0),
    var downvotes: ObservableInt = ObservableInt(0),
    var hasVoted: ObservableField<VoteType?> = ObservableField()) {

    var formattedDate: String = ""
    var formattedDistance: String = ""

    init {
        formattedDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(timestamp)
        formattedDistance = String.format("%.1fkm away", distanceKm)
    }

    companion object {

        fun fromFeedPost(feedPost: FeedPost): CorePostModel {
            return CorePostModel(feedPost.id, feedPost.userId,
                feedPost.imageUrl, feedPost.timestamp, feedPost.distanceKm,
                feedPost.description, feedPost.commentCount, ObservableInt(feedPost.upvotes),
                ObservableInt(feedPost.downvotes), ObservableField(feedPost.hasVoted))
        }

        fun fromSinglePost(singlePost: SinglePost): CorePostModel {
            return CorePostModel(singlePost.id, singlePost.userId,
                singlePost.imageUrl, singlePost.timestamp, singlePost.distanceKm,
                singlePost.description, singlePost.commentCount)
        }

    }
}