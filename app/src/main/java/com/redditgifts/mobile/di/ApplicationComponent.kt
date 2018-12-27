package com.redditgifts.mobile.di

import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.di.modules.ModelModule
import com.redditgifts.mobile.di.modules.NetworkModule
import com.redditgifts.mobile.ui.activities.*
import com.redditgifts.mobile.ui.fragments.AccountFragment
import com.redditgifts.mobile.ui.fragments.ExchangesFragment
import com.redditgifts.mobile.ui.fragments.PastExchangesFragment
import com.redditgifts.mobile.ui.views.ExchangeBottomSheet
import com.redditgifts.mobile.ui.views.StatisticsBottomSheet
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ModelModule::class, NetworkModule::class])
interface ApplicationComponent {
    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(exchangesFragment: ExchangesFragment)
    fun inject(pastExchangesFragment: PastExchangesFragment)
    fun inject(accountFragment: AccountFragment)
    fun inject(giftActivity: GiftActivity)
    fun inject(exchangeBottomSheet: ExchangeBottomSheet)
    fun inject(statisticsBottomSheet: StatisticsBottomSheet)
    fun inject(galleryActivity: GalleryActivity)
    fun inject(messagesActivity: MessagesActivity)
}
