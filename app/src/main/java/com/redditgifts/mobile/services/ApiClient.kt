package com.redditgifts.mobile.services

import com.google.gson.Gson
import com.redditgifts.mobile.libs.operators.ApiErrorOperator
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.errors.UnauthorizedError
import com.redditgifts.mobile.services.models.*
import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ApiClient(private val apiService: ApiService,
                private val cookieRepository: CookieRepository,
                private val gson: Gson): ApiRepository {

    override fun getCurrentExchanges(): Single<CurrentExchangeModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getExchanges(cookie, true)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun getExchangeStatus(exchangeId: String): Single<ExchangeStatusModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getExchangeStatus(cookie, exchangeId)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun getStatistics(exchangeId: String): Single<StatisticsModel> {
        return apiService.getStatistics(exchangeId)
            .lift(apiErrorOperator())
            .subscribeOn(Schedulers.io())
    }

    override fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel> {
        return apiService.getGallery(exchangeId, pageSize, pageNumber)
            .lift(apiErrorOperator())
            .subscribeOn(Schedulers.io())
    }

    override fun getDetailedGift(exchangeId: String, giftId: String): Single<DetailedGiftModel> {
        return apiService.getDetailedGift(exchangeId, giftId)
            .lift(apiErrorOperator())
            .subscribeOn(Schedulers.io())
    }

    override fun getProfile(): Single<ProfileModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getProfile(cookie)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun getCredits(): Single<CreditModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getCredits(cookie)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    private fun cookie(): Single<String> {
        return this.cookieRepository.getCookie()
            .map { cookie ->
                if(cookie.isEmpty()) {
                    throw UnauthorizedError()
                }
                cookie
            }
    }

    /**
     * Utility to create a new [ApiErrorOperator], saves us from littering references to gson throughout the client.
     */
    private fun <T> apiErrorOperator(): ApiErrorOperator<T> {
        return Operators.apiError(gson)
    }
}