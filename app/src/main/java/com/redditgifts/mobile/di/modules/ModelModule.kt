package com.redditgifts.mobile.di.modules

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.models.*
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

    @Provides fun providesStatisticsViewModel(htmlParser: HTMLParser,
                                              localizedErrorMessages: LocalizedErrorMessages): StatisticsViewModel =
        StatisticsViewModel(htmlParser, localizedErrorMessages)

    @Provides fun providesGalleryViewModel(htmlParser: HTMLParser,
                                           localizedErrorMessages: LocalizedErrorMessages): GalleryViewModel =
        GalleryViewModel(htmlParser, localizedErrorMessages)

    @Provides fun providesGiftViewModel(htmlParser: HTMLParser,
                                        localizedErrorMessages: LocalizedErrorMessages): GiftViewModel =
        GiftViewModel(htmlParser, localizedErrorMessages)

    @Provides fun providesAccountViewModel(htmlParser: HTMLParser,
                                           localizedErrorMessages: LocalizedErrorMessages): AccountViewModel =
        AccountViewModel(htmlParser, localizedErrorMessages)

}