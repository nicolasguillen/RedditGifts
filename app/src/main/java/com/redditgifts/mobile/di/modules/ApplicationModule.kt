package com.redditgifts.mobile.di.modules

import android.app.Application
import android.content.Context
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.JsoupHTMLParser
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.RedditGiftsErrorMessages
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
    @Singleton
    internal fun provideHTMLParser(): HTMLParser {
        return JsoupHTMLParser()
    }

    @Provides
    @Singleton
    internal fun provideLocalizedErrorMessages(context: Context): LocalizedErrorMessages {
        return RedditGiftsErrorMessages(context)
    }

}