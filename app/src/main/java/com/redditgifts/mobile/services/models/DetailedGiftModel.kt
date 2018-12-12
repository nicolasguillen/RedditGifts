package com.redditgifts.mobile.services.models

class DetailedGiftModel(
    val title: String,
    val timeAndSource: String,
    val description: String,
    val upvotes: String,
    val images: List<String>,
    val comments: List<String>
)