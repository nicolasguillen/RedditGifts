package com.redditgifts.mobile

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.redditgifts.mobile.di.ApplicationComponent
import com.redditgifts.mobile.di.DaggerApplicationComponent
import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.di.modules.ModelModule
import com.redditgifts.mobile.di.modules.UseCaseModule
import io.fabric.sdk.android.Fabric

open class RedditGiftsApp : Application() {

    companion object {
        @JvmStatic lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = initApplicationComponent()

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
    }

    open fun initApplicationComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .modelModule(ModelModule())
                .useCaseModule(UseCaseModule())
                .build()
    }

}