package com.redditgifts.mobile.services.models

class StatisticsModel(
    val data: Data
) {
    class Data(
        val rematchesPercentage: Int,
        val exchangeTitle: String,
        val rematches: Int,
        val rematchSignupsPercentage: Int,
        val shippedPercentage: Int,
        val giftsPercentage: Int,
        val spentShipping: Double,
        val totalMatches: Int,
        val participants: Int,
        val rematchSignups: Int,
        val averageGiftCost: Double,
        val matches: Int,
        val retrieved: Int,
        val gifts: Int,
        val participantsGivers: Int,
        val spentGifts: Double,
        val averageShippingCost: Double,
        val spentTotal: Double,
        val participantsReceivers: Int,
        val countries: Int,
        val shipped: Int
    )
}