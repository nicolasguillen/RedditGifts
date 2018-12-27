package com.redditgifts.mobile.di.modules

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.models.*
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.storage.CookieRepository
import dagger.Module
import dagger.Provides

@Module
open class ModelModule {

    @Provides fun providesSplashViewModel(cookieRepository: CookieRepository): SplashViewModel =
        SplashViewModel(cookieRepository)

    @Provides fun providesLoginViewModel(apiRepository: ApiRepository,
                                         cookieRepository: CookieRepository,
                                         localizedErrorMessages: LocalizedErrorMessages): LoginViewModel =
        LoginViewModel(apiRepository, cookieRepository, localizedErrorMessages)

    @Provides fun providesExchangesViewModel(apiRepository: ApiRepository,
                                             localizedErrorMessages: LocalizedErrorMessages): ExchangesViewModel =
        ExchangesViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesMainViewModel(apiRepository: ApiRepository,
                                        localizedErrorMessages: LocalizedErrorMessages): MainViewModel =
        MainViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesExchangeStatusViewModel(apiRepository: ApiRepository,
                                                  localizedErrorMessages: LocalizedErrorMessages): ExchangeStatusViewModel =
        ExchangeStatusViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesStatisticsViewModel(apiRepository: ApiRepository,
                                              localizedErrorMessages: LocalizedErrorMessages): StatisticsViewModel =
        StatisticsViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesPastExchangesViewModel(): PastExchangesViewModel =
        PastExchangesViewModel()

    @Provides fun providesGalleryViewModel(apiRepository: ApiRepository,
                                           localizedErrorMessages: LocalizedErrorMessages): GalleryViewModel =
        GalleryViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesGiftViewModel(apiRepository: ApiRepository,
                                        localizedErrorMessages: LocalizedErrorMessages): GiftViewModel =
        GiftViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesAccountViewModel(apiRepository: ApiRepository,
                                           cookieRepository: CookieRepository,
                                           localizedErrorMessages: LocalizedErrorMessages): AccountViewModel =
        AccountViewModel(apiRepository, cookieRepository, localizedErrorMessages)

}