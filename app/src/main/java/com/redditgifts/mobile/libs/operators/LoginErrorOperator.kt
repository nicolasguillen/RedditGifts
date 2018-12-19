package com.redditgifts.mobile.libs.operators

import com.redditgifts.mobile.libs.utils.getCookieValue
import com.redditgifts.mobile.services.errors.ApiException
import com.redditgifts.mobile.services.models.ErrorEnvelope
import io.reactivex.SingleObserver
import io.reactivex.SingleOperator
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Response

class LoginErrorOperator: SingleOperator<String, Response<ResponseBody>> {

    override fun apply(observer: SingleObserver<in String>): SingleObserver<in Response<ResponseBody>> {
        return object : SingleObserver<Response<ResponseBody>> {

            override fun onSubscribe(d: Disposable) {
                observer.onSubscribe(d)
            }

            override fun onError(e: Throwable) {
                observer.onError(e)
            }

            override fun onSuccess(response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    try {
                        val headers = response.headers().values("set-cookie")
                        val buffer = StringBuffer()
                        for (header in headers) {
                            val sessionId = header.getCookieValue("sessionid")
                            val csrftoken = header.getCookieValue("csrftoken")
                            if(sessionId != null && sessionId.isNotEmpty()) {
                                buffer.append("sessionid=$sessionId; ")
                            }
                            if(csrftoken != null && csrftoken.isNotEmpty()) {
                                buffer.append("csrftoken=$csrftoken; ")
                            }
                        }
                        observer.onSuccess(buffer.toString().dropLast(2))
                    } catch (e: Exception) {
                        observer.onError(ApiException(ErrorEnvelope("Wrong credentials. Please try again")))
                    }
                } else {
                    observer.onError(ApiException(ErrorEnvelope("Wrong credentials. Please try again")))
                }
            }
        }
    }

}
