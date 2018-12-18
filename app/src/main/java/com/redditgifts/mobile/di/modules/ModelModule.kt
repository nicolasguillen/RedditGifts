package com.redditgifts.mobile.di.modules

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.models.*
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.HTMLParser
import dagger.Module
import dagger.Provides

@Module
open class ModelModule {

    @Provides fun providesLoginViewModel(): LoginViewModel =
        LoginViewModel()

    @Provides fun providesExchangesViewModel(htmlParser: HTMLParser,
                                             localizedErrorMessages: LocalizedErrorMessages): ExchangesViewModel =
        ExchangesViewModel(htmlParser, localizedErrorMessages)

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

    @Provides fun providesGiftViewModel(htmlParser: HTMLParser,
                                        localizedErrorMessages: LocalizedErrorMessages): GiftViewModel =
        GiftViewModel(htmlParser, localizedErrorMessages)

    @Provides fun providesAccountViewModel(htmlParser: HTMLParser,
                                           localizedErrorMessages: LocalizedErrorMessages): AccountViewModel =
        AccountViewModel(htmlParser, localizedErrorMessages)

}