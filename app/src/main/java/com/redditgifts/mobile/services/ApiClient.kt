package com.redditgifts.mobile.services

import com.google.gson.Gson
import com.redditgifts.mobile.libs.operators.ApiErrorOperator
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.services.models.GalleryModel
import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ApiClient(private val apiService: ApiService,
                private val gson: Gson): ApiRepository {

    override fun getStatistics(exchangeId: String): Single<StatisticsModel> {
        return apiService
                .getStatistics(exchangeId)
                .lift(apiErrorOperator())
                .subscribeOn(Schedulers.io())
    }

    override fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel> {
        return apiService
                .getGallery(exchangeId, pageSize, pageNumber)
                .lift(apiErrorOperator())
                .subscribeOn(Schedulers.io())
    }

    override fun getDetailedGift(exchangeId: String, giftId: String): Single<DetailedGiftModel> {
        return apiService
            .getDetailedGift(exchangeId, giftId)
            .lift(apiErrorOperator())
            .subscribeOn(Schedulers.io())
    }

    /**
     * Utility to create a new [ApiErrorOperator], saves us from littering references to gson throughout the client.
     */
    private fun <T> apiErrorOperator(): ApiErrorOperator<T> {
        return Operators.apiError(gson)
    }
}