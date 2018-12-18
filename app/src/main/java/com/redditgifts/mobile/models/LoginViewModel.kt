package com.redditgifts.mobile.models

import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface LoginViewModelInputs {
    fun didLogin(cookie: String)
}

interface LoginViewModelOutputs {
    fun finish(): Observable<Unit>
}

class LoginViewModel(private val cookieRepository: CookieRepository) : DisposableViewModel(), LoginViewModelInputs, LoginViewModelOutputs {

    //INPUTS
    private val didLogin = PublishSubject.create<String>()

    //OUTPUTS
    private val finish = PublishSubject.create<Unit>()

    val inputs: LoginViewModelInputs = this
    val outputs: LoginViewModelOutputs = this

    init {
        this.didLogin
            .switchMapSingle { cookie ->
                this.cookieRepository.storeCookie(cookie)
            }
            .crashingSubscribe {
                this.finish.onNext(Unit)
            }

    }


    override fun didLogin(cookie: String) = this.didLogin.onNext(cookie)
    override fun finish(): Observable<Unit> = this.finish

}