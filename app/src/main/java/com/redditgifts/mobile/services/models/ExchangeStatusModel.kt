package com.redditgifts.mobile.services.models

class ExchangeStatusModel(
    val santaStatus: List<StatusData>,
    val gifteeStatus: List<StatusData>
) {
    class StatusData(val title: String, val status: Status)

    enum class Status { COMPLETED, ACTIVE, INCOMPLETE }
}