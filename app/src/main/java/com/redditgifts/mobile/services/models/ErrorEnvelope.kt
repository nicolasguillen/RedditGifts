package com.redditgifts.mobile.services.models

import com.google.gson.annotations.SerializedName
import com.redditgifts.mobile.services.errors.ApiException

class ErrorEnvelope(
        @SerializedName("status")
                    val status: Int?,
        @SerializedName("error")
                    val error: String,
        @SerializedName("message")
                    val message: String) {

    companion object {

        /**
         * Tries to extract an [ErrorEnvelope] from an exception, and if it
         * can't returns null.
         */
        fun fromThrowable(t: Throwable): ErrorEnvelope? {
            if (t is ApiException) {
                return t.errorEnvelope
            }
            return null
        }
    }
}