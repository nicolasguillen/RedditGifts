package com.redditgifts.mobile.services

import com.google.gson.Gson
import com.redditgifts.mobile.libs.operators.ApiErrorOperator
import com.redditgifts.mobile.libs.operators.LoginErrorOperator
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.libs.utils.getCookieValue
import com.redditgifts.mobile.services.errors.UnauthorizedError
import com.redditgifts.mobile.services.models.*
import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.intellij.lang.annotations.Language
import java.net.URLEncoder

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

    override fun getDetailedGift(exchangeId: String, giftSlug: String): Single<DetailedGiftModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getDetailedGift(cookie, exchangeId, giftSlug)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun upvoteGift(giftSlug: String): Single<UpvoteGiftModel> {
        return this.cookie()
            .flatMap { cookie ->
                val csrf = cookie.getCookieValue("csrftoken")
                val contentOfBody = "csrfmiddlewaretoken=$csrf"
                val body = RequestBody.create(MediaType.parse("application/json"), contentOfBody)
                apiService.upvoteGift(cookie, giftSlug, body)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
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

    override fun login(user: String, password: String, cookie: String): Single<String> {
        val csrf = cookie.getCookieValue("csrftoken")
        val encodedUser = URLEncoder.encode(user, "utf-8")
        val encodedPassword = URLEncoder.encode(password, "utf-8")
        val encodedSubmit = URLEncoder.encode("Log in", "utf-8")
        val encodedNext = URLEncoder.encode("/api/v1/", "utf-8")
        val content = "csrfmiddlewaretoken=$csrf&username=$encodedUser&password=$encodedPassword&next=$encodedNext&submit=$encodedSubmit"
        val body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content)
        return apiService.login(cookie, body)
            .lift(loginError())
            .subscribeOn(Schedulers.io())
    }

    override fun getAllMessages(pageNumber: Int): Single<MessageModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getAllMessages(cookie, pageNumber)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun getUnreadMessages(): Single<MessageModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getUnreadMessages(cookie)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun getDetailedMessages(messageId: Int): Single<MessageModel> {
        return this.cookie()
            .flatMap { cookie ->
                apiService.getDetailedMessages(cookie, messageId)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun sendMessage(to: String, subject: String, message: String): Single<SendMessageModel> {
        @Language("JSON") val content = """
{
"to_reddit_username": "$to",
"subject": "$subject",
"message": "$message"
}
            """
        val encodedContentType = URLEncoder.encode("application/json", "utf-8")
        val encodedContent = URLEncoder.encode(content, "utf-8")
        return this.cookie()
            .flatMap { cookie ->
                val csrf = cookie.getCookieValue("csrftoken")
                val contentOfBody = "csrfmiddlewaretoken=$csrf&_content_type=$encodedContentType&_content=$encodedContent"
                val body = RequestBody.create(MediaType.parse("application/json"), contentOfBody)
                apiService.sendMessage(cookie, body)
                    .lift(apiErrorOperator())
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun sendReplyMessage(messageId: Int, message: String): Single<SendMessageModel> {
        val encodedMessage = URLEncoder.encode(message, "utf-8")
        return this.cookie()
            .flatMap { cookie ->
                val csrf = cookie.getCookieValue("csrftoken")
                val contentOfBody = "csrfmiddlewaretoken=$csrf&message=$encodedMessage"
                val body = RequestBody.create(MediaType.parse("application/json"), contentOfBody)
                apiService.sendReplyMessage(cookie, messageId, body)
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

    private fun loginError(): LoginErrorOperator {
        return Operators.loginError()
    }

}