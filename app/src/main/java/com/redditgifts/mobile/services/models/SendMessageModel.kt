package com.redditgifts.mobile.services.models

class SendMessageModel(
    val data: Data,
    val error: Error
) {
    class Data(
        val status: String
    )

    class Error(
        val formErrors: List<FormError>,
        val message: String
    ) {
        class FormError(
            val message: String
        )
    }
}