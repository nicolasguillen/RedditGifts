package com.redditgifts.mobile.models

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.rxkotlin.addTo

open class DisposableViewModel {

    private val disposables = CompositeDisposable()

    protected fun <I> Observable<I>.crashingSubscribe(onNext: (I) -> Unit) {
        subscribe(onNext) { throw OnErrorNotImplementedException(it) }.addTo(disposables)
    }

    fun clear() {
        disposables.clear()
    }

}