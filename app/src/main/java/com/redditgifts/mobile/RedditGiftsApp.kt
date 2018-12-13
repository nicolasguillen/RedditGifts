package com.redditgifts.mobile

import android.app.Application
import android.provider.Settings
import com.crashlytics.android.Crashlytics
import com.redditgifts.mobile.di.ApplicationComponent
import com.redditgifts.mobile.di.DaggerApplicationComponent
import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.di.modules.ModelModule
import com.redditgifts.mobile.di.modules.UseCaseModule
import io.fabric.sdk.android.Fabric
import com.google.firebase.analytics.FirebaseAnalytics



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

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab")
        if ("true" == testLabSetting) {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
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