package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Single

interface ApiRepository {

    fun getStatistics(exchangeId: String): Single<StatisticsModel>

}
