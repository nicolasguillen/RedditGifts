package com.redditgifts.mobile.services.models

class StatisticsModel(
    val participants: Int,
    val countries: Int,
    val matches: Int,
    val retrieved: Int,
    val shipped: Int,
    val giftInGallery: Int,
    val rematchSignups: Int,
    val rematchCompleted: Int,
    val spentOnGifts: Double,
    val spentOnShipping: Double,
    val totalSpent: Double,
    val medianGiftCost: Double,
    val medianShippingCost: Double
)