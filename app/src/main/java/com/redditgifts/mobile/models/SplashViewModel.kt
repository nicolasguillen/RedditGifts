package com.redditgifts.mobile.models

import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface SplashViewModelInputs {
    fun onCreate()
}

interface SplashViewModelOutputs {
    fun startLogin(): Observable<Unit>
    fun startMainActivity(): Observable<Unit>
}

class SplashViewModel(private val cookieRepository: CookieRepository): DisposableViewModel(), SplashViewModelInputs, SplashViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()

    //OUTPUTS
    private val startLogin = PublishSubject.create<Unit>()
    private val startMainActivity = PublishSubject.create<Unit>()

    val inputs: SplashViewModelInputs = this
    val outputs: SplashViewModelOutputs = this

    init {
        this.onCreate
            .switchMapSingle {
                this.cookieRepository.getCookie()
            }
            .crashingSubscribe { credentials ->
                if(credentials.isEmpty()) {
                    startLogin.onNext(Unit)
                } else {
                    startMainActivity.onNext(Unit)
                }
            }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun startLogin(): Observable<Unit>  = this.startLogin
    override fun startMainActivity(): Observable<Unit>  = this.startMainActivity

}