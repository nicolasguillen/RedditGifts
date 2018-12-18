package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.*
import io.reactivex.Single
import okhttp3.ResponseBody

interface ApiRepository {

    fun getCurrentExchanges(): Single<CurrentExchangeModel>

    fun getExchangeStatus(exchangeId: String): Single<ExchangeStatusModel>

    fun getStatistics(exchangeId: String): Single<StatisticsModel>

    fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel>

    fun getDetailedGift(exchangeId: String, giftId: String): Single<DetailedGiftModel>

    fun getProfile(): Single<ProfileModel>

    fun getCredits(): Single<CreditModel>

    fun login(user: String, password: String, cookie: String): Single<ResponseBody>

}
