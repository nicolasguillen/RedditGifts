package com.redditgifts.mobile.di.modules

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.models.*
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.storage.CookieRepository
import dagger.Module
import dagger.Provides

@Module
open class ModelModule {

    @Provides fun providesLoginViewModel(cookieRepository: CookieRepository): LoginViewModel =
        LoginViewModel(cookieRepository)

    @Provides fun providesExchangesViewModel(apiRepository: ApiRepository,
                                             localizedErrorMessages: LocalizedErrorMessages): ExchangesViewModel =
        ExchangesViewModel(apiRepository, localizedErrorMessages)

    @Provides fun providesExchangeStatusViewModel(htmlParser: HTMLParser,
                                                  localizedErrorMessages: LocalizedErrorMessages): ExchangeStatusViewModel =
        ExchangeStatusViewModel(htmlParser, localizedErrorMessages)

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

    @Provides fun providesAccountViewModel(htmlParser: HTMLParser,
                                           localizedErrorMessages: LocalizedErrorMessages): AccountViewModel =
        AccountViewModel(htmlParser, localizedErrorMessages)

}