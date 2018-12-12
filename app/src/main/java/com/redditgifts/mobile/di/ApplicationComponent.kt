package com.redditgifts.mobile.di

import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.di.modules.ModelModule
import com.redditgifts.mobile.di.modules.UseCaseModule
import com.redditgifts.mobile.ui.activities.GiftActivity
import com.redditgifts.mobile.ui.activities.LoginActivity
import com.redditgifts.mobile.ui.fragments.AccountFragment
import com.redditgifts.mobile.ui.fragments.GalleryFragment
import com.redditgifts.mobile.ui.fragments.ExchangesFragment
import com.redditgifts.mobile.ui.views.ExchangeBottomSheet
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ModelModule::class, UseCaseModule::class])
interface ApplicationComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(exchangesFragment: ExchangesFragment)
    fun inject(galleryFragment: GalleryFragment)
    fun inject(accountFragment: AccountFragment)
    fun inject(giftActivity: GiftActivity)
    fun inject(exchangeBottomSheet: ExchangeBottomSheet)
}
