package com.redditgifts.mobile.services.models

import java.util.*

class DetailedGiftModel(
    val data: Data
) {
    class Data(
        val title: String,
        val createdAt: Date,
        val bodyHTML: String,
        val votes: Int,
        val from: String,
        val assets: List<Assets>,
        val commentsCount: Int
    ) {
        class Assets(
            val largeImageUrl: String
        )
    }
}