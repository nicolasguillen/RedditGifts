package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.*
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("exchanges/")
    fun getExchanges(@Header("cookie") cookie: String,
                     @Query("participating") participating: Boolean): Single<Response<CurrentExchangeModel>>

    @GET("exchanges/{exchangeId}/status/")
    fun getExchangeStatus(@Header("cookie") cookie: String,
                          @Path("exchangeId") exchangeId: String): Single<Response<ExchangeStatusModel>>

    @GET("exchanges/{exchangeId}/statistics/")
    fun getStatistics(@Path("exchangeId") exchangeId: String): Single<Response<StatisticsModel>>

    @GET("exchanges/{exchangeId}/gallery/")
    fun getGallery(@Path("exchangeId") exchangeId: String,
                   @Query("page_size") pageSize: Int,
                   @Query("page_number") pageNumber: Int): Single<Response<GalleryModel>>

    @GET("exchanges/{exchangeId}/gallery/gift/{giftId}/")
    fun getDetailedGift(@Path("exchangeId") exchangeId: String,
                        @Path("giftId") giftId: String): Single<Response<DetailedGiftModel>>

    @GET("profiles/me/")
    fun getProfile(@Header("cookie") cookie: String): Single<Response<ProfileModel>>

    @GET("https://www.redditgifts.com/exchanges/mgmt/participant/")
    fun getCredits(@Header("cookie") cookie: String): Single<Response<CreditModel>>

    @Headers("Cache-Control: no-cache")
    @POST("https://www.redditgifts.com/merchant/api-auth/login/")
    fun login(@Body data: RequestBody,
              @Header("content-type") type: String,
              @Header("origin") origin: String,
              @Header("user-agent") ua: String,
              @Header("referer") referer: String,
              @Header("accept") accept: String,
              @Header("upgrade-insecure-requests") uir: String,
              @Header("cookie") cookie: String): Single<Response<ResponseBody>>

    @GET("messages/")
    fun getAllMessages(@Header("cookie") cookie: String,
                       @Query("page_number") pageNumber: Int): Single<Response<MessageModel>>

    @GET("messages/unread")
    fun getUnreadMessages(@Header("cookie") cookie: String): Single<Response<MessageModel>>

}