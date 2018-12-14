package com.redditgifts.mobile.services.models

class ExchangeOverviewModel(
    val credits: Int = 0,
    val listCurrentExchanges: List<CurrentExchange>
) {
    class CurrentExchange(
        val referenceId: String,
        val title: String,
        val imageURL: String
    )
}