package com.redditgifts.mobile.services.models

class CurrentExchangeModel(
    val data: List<Data>
) {
    class Data(
        val exchanges: List<Exchange>
    ) {
        class Exchange(
            val slug: String,
            val title: String,
            val logoImageUrl: String
        )
    }
}
