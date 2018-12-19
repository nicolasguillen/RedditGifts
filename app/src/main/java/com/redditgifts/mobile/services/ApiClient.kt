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

    override fun login(user: String, password: String, cookie: String): Single<String> {
        val csrf = cookie.getCookieValue("csrftoken")
        val encodedUser = URLEncoder.encode(user, "utf-8")
        val encodedPassword = URLEncoder.encode(password, "utf-8")
        val encodedSubmit = URLEncoder.encode("Log in", "utf-8")
        val encodedNext = URLEncoder.encode("/api/v1/", "utf-8")
        val content = "csrfmiddlewaretoken=$csrf&username=$encodedUser&password=$encodedPassword&next=$encodedNext&submit=$encodedSubmit"
        val body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content)
        return apiService.login(body,
            "application/x-www-form-urlencoded", "https://www.redditgifts.com", "Mozilla/5.0 (Linux; Android 8.0.0; Pixel XL Build/OPR3.170623.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.98 Mobile Safari/537.36",
            "https://www.redditgifts.com/merchant/api-auth/login/" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "1", cookie)
            .lift(loginError())
            .subscribeOn(Schedulers.io())
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