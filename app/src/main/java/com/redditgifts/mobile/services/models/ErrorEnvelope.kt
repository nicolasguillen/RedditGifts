package com.redditgifts.mobile.services.models

import com.google.gson.annotations.SerializedName

class ErrorEnvelope(
    @SerializedName("detail") val detail: String,
    @SerializedName("error") val error: Error? = null
) {
    class Error(
        val formErrors: List<FormError>
    ) {
        class FormError(
            val message: String
        )
    }
}