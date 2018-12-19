package com.redditgifts.mobile.libs

import android.content.Context
import com.redditgifts.mobile.R
import com.redditgifts.mobile.services.errors.ApiException

interface LocalizedErrorMessages {
    fun getMessageFromError(error: Throwable): String
}

class RedditGiftsErrorMessages(private val context: Context): LocalizedErrorMessages {
    override fun getMessageFromError(error: Throwable): String {
        return when(error) {
            is ApiException ->
                error.errorEnvelope.detail
            else ->
                context.getString(R.string.error_unknown)
        }
    }
}