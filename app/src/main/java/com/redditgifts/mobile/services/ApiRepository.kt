package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.*
import io.reactivex.Single

interface ApiRepository {

    fun getCurrentExchanges(): Single<CurrentExchangeModel>

    fun getExchangeStatus(exchangeId: String): Single<ExchangeStatusModel>

    fun getStatistics(exchangeId: String): Single<StatisticsModel>

    fun getGallery(exchangeId: String, pageSize: Int, pageNumber: Int): Single<GalleryModel>

    fun getDetailedGift(exchangeId: String, giftSlug: String): Single<DetailedGiftModel>

    fun upvoteGift(giftSlug: String): Single<UpvoteGiftModel>

    fun getProfile(): Single<ProfileModel>

    fun getCredits(): Single<CreditModel>

    fun login(user: String, password: String, cookie: String): Single<String>

    fun getAllMessages(pageNumber: Int): Single<MessageModel>

    fun getUnreadMessages(): Single<MessageModel>

    fun getDetailedMessages(messageId: Int): Single<MessageModel>

    fun sendMessage(to: String, subject: String, message: String): Single<SendMessageModel>

    fun sendReplyMessage(messageId: Int, message: String): Single<SendMessageModel>

}
