package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.services.models.GalleryModel
import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("exchanges/{exchangeId}/statistics/")
    fun getStatistics(@Path("exchangeId") exchangeId: String): Single<Response<StatisticsModel>>

    @GET("exchanges/{exchangeId}/gallery/")
    fun getGallery(@Path("exchangeId") exchangeId: String,
                   @Query("page_size") pageSize: Int,
                   @Query("page_number") pageNumber: Int): Single<Response<GalleryModel>>

    @GET("exchanges/{exchangeId}/gallery/gift/{giftId}/")
    fun getDetailedGift(@Path("exchangeId") exchangeId: String,
                        @Path("giftId") giftId: String): Single<Response<DetailedGiftModel>>

}