package com.redditgifts.mobile.services.models

class GalleryModel(
    val data: Data
) {
    class Data(
        val gifts: List<GiftModel>
    ) {
        class GiftModel(
            val bodyHTML: String,
            val thumbnailUrl: String,
            val slug: String,
            val title: String,
            val postedBy: String,
            val bodySnudown: String,
            val commentsCount: Int,
            val votes: Int
        )
    }
}