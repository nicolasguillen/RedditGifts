package com.redditgifts.mobile.libs

import android.content.Context
import com.redditgifts.mobile.R

interface LocalizedErrorMessages {
    fun getMessageFromError(error: Throwable): String
}

class RedditGiftsErrorMessages(private val context: Context): LocalizedErrorMessages {
    override fun getMessageFromError(error: Throwable): String {
        return context.getString(R.string.error_unknown)
    }
}