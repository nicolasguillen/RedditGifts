package com.redditgifts.mobile.mocks

import android.app.Application
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.di.ApplicationComponent
import com.redditgifts.mobile.di.DaggerApplicationComponent
import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.*
import dagger.Module
import io.reactivex.Single

class MockApp: RedditGiftsApp() {

    override fun initApplicationComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
                .applicationModule(MockApplicationModule(this))
                .build()
    }

    @Module class MockApplicationModule(application: Application) : ApplicationModule(application) {

        override fun provideHTMLParser(): HTMLParser {
            return object : HTMLParser {
                override fun parseExchanges(html: String): Single<ExchangeOverviewModel> {
                    return Single.just(ExchangeOverviewModel(
                        credits = 10,
                        listCurrentExchanges = listOf(
                            ExchangeOverviewModel.CurrentExchange("", "Secret Santa 10th Annual Extravaganza!", "https://static.redditgifts.com/images/uploaded/exchange-logo/2018/10/19/ss-logo-1539968633-634703081461.png")
                        )
                    ))
                }

                override fun parseStatuses(html: String): Single<ExchangeStatusModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun parseGallery(html: String): Single<List<GiftModel>> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun parseGift(html: String): Single<DetailedGiftModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun parseAccount(html: String): Single<AccountModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun parseStatistics(html: String): Single<StatisticsModel> {
                    return Single.just(StatisticsModel(102964, 141, 99306, 98710, 74884, 19448, 5978, 5, 2666201.99, 554581.80, 3220783.79, 30.96, 6.44))
                }
            }
        }

    }
}