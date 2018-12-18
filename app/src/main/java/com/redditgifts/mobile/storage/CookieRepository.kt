package com.redditgifts.mobile.storage

import android.content.Context
import androidx.core.content.edit
import io.reactivex.Single

interface CookieRepository {

    fun getCookie(): Single<String>

    fun storeCookie(cookie: String): Single<Unit>

}

class RedditGiftsCookieRepository(private val context: Context): CookieRepository {

    override fun getCookie(): Single<String> {
        val sharedPreferences = context.getSharedPreferences("Cookies", Context.MODE_PRIVATE)
        val cookie = sharedPreferences.getString(Constants.COOKIE.key, "")
        return Single.just(cookie)
    }

    override fun storeCookie(cookie: String): Single<Unit> {
        val sharedPreferences = context.getSharedPreferences("Cookies", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(Constants.COOKIE.key, cookie)
        }
        return Single.just(Unit)
    }

    enum class Constants(val key: String) {
        COOKIE("Cookie")
    }

}