package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.services.models.GalleryModel
import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Single

interface ApiRepository {

    fun getStatistics(exchangeId: String): Single<StatisticsModel>

    fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel>

    fun getDetailedGift(exchangeId: String, giftId: String): Single<DetailedGiftModel>

}
