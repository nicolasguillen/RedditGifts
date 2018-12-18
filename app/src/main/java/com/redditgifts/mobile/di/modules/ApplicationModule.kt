package com.redditgifts.mobile.di.modules

import android.app.Application
import android.content.Context
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.RedditGiftsErrorMessages
import com.redditgifts.mobile.storage.CookieRepository
import com.redditgifts.mobile.storage.RedditGiftsCookieRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
open class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return application
    }

    @Provides
    open fun provideCookieRepository(context: Context): CookieRepository {
        return RedditGiftsCookieRepository(context)
    }

    @Provides
    internal fun provideLocalizedErrorMessages(context: Context): LocalizedErrorMessages {
        return RedditGiftsErrorMessages(context)
    }

}