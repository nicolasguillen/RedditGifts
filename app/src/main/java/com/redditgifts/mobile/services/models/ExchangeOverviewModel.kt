package com.redditgifts.mobile.services.models

class ExchangeOverviewModel(
    val credits: Int = 0,
    val listExhanges: List<Exchange>
) {
    class Exchange(
        val referenceId: String,
        val title: String,
        val imageURL: String
    )
}