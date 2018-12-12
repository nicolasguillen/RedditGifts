package com.redditgifts.mobile.libs.operators

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import io.reactivex.SingleObserver
import io.reactivex.SingleOperator
import io.reactivex.disposables.Disposable

class GenericResultOperator<T>(private val localizedErrorMessages: LocalizedErrorMessages) : SingleOperator<GenericResult<T>, T> {

    override fun apply(observer: SingleObserver<in GenericResult<T>>): SingleObserver<in T> {
        return object : SingleObserver<T> {

            override fun onSubscribe(d: Disposable) = observer.onSubscribe(d)

            override fun onError(e: Throwable) {
                val errorMessage: String? = localizedErrorMessages.getMessageFromError(e)
                observer.onSuccess(GenericResult.Failed(errorMessage ?: "", e))
            }

            override fun onSuccess(t: T) = observer.onSuccess(GenericResult.Successful(t))
        }
    }

}
