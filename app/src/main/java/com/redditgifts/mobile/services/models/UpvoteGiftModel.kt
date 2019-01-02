package com.redditgifts.mobile.services.models

class UpvoteGiftModel(
    val data: Data,
    val error: Error
) {
    class Data(
        val action: String
    )

    enum class UpvoteAction(val action: String) {
        VOTE("vote"), UNVOTE("unvote")
    }

    class Error(
        val formErrors: List<FormError>,
        val message: String
    ) {
        class FormError(
            val message: String
        )
    }
}