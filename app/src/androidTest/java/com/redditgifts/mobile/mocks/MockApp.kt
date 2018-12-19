package com.redditgifts.mobile.mocks

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.di.ApplicationComponent
import com.redditgifts.mobile.di.DaggerApplicationComponent
import com.redditgifts.mobile.di.modules.ApplicationModule
import com.redditgifts.mobile.di.modules.NetworkModule
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.ApiService
import com.redditgifts.mobile.services.models.*
import com.redditgifts.mobile.storage.CookieRepository
import dagger.Module
import io.reactivex.Single

class MockApp: RedditGiftsApp() {

    override fun initApplicationComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
                .applicationModule(MockApplicationModule(this))
                .networkModule(MockNetworkModule())
                .build()
    }

    @Module class MockApplicationModule(application: Application) : ApplicationModule(application) {
        override fun provideCookieRepository(context: Context): CookieRepository {
            return object : CookieRepository {
                override fun removeCookie(): Single<Unit> {
                    return Single.just(Unit)
                }

                override fun getCookie(): Single<String> {
                    return Single.just("cookie")
                }

                override fun storeCookie(cookie: String): Single<Unit> {
                    return Single.just(Unit)
                }
            }
        }
    }

    @Module class MockNetworkModule : NetworkModule() {
        override fun provideApiClient(
            apiService: ApiService,
            cookieRepository: CookieRepository,
            gson: Gson
        ): ApiRepository {
            return object : ApiRepository {
                override fun login(user: String, password: String, cookie: String): Single<Map<String, String>> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getCurrentExchanges(): Single<CurrentExchangeModel> {
                    return Single.just(CurrentExchangeModel(
                        data = listOf(CurrentExchangeModel.Data(
                            exchanges = listOf(
                                CurrentExchangeModel.Data.Exchange(
                                    slug = "",
                                    title = "Secret Santa 10th Annual Extravaganza!",
                                    logoImageUrl = "https://static.redditgifts.com/images/uploaded/exchange-logo/2018/10/19/ss-logo-1539968633-634703081461.png")
                            )))))
                }

                override fun getExchangeStatus(exchangeId: String): Single<ExchangeStatusModel> {
                    TODO()
                }

                override fun getStatistics(exchangeId: String): Single<StatisticsModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getDetailedGift(exchangeId: String, giftId: String): Single<DetailedGiftModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getProfile(): Single<ProfileModel> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getCredits(): Single<CreditModel> {
                    return Single.just(
                        CreditModel(CreditModel.Data(
                            credits = 10
                        )))
                }
            }
        }
    }
}