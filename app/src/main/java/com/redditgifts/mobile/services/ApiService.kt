package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("exchanges/{exchangeId}/statistics/")
    fun getStatistics(@Path("exchangeId") exchangeId: String): Single<Response<StatisticsModel>>

}