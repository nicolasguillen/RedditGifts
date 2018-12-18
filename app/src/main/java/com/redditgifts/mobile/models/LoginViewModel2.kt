package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface LoginViewModel2Inputs {
    fun cookie(cookie: String)
    fun didPressLogin()
}

interface LoginViewModel2Outputs {
    fun finish(): Observable<Unit>
}

class LoginViewModel2(private val apiRepository: ApiRepository,
                      private val cookieRepository: CookieRepository,
                      private val localizedErrorMessages: LocalizedErrorMessages) : DisposableViewModel(), LoginViewModel2Inputs, LoginViewModel2Outputs {

    //INPUTS
    private val cookie = PublishSubject.create<String>()
    private val didPressLogin = PublishSubject.create<Unit>()

    //OUTPUTS
    private val finish = PublishSubject.create<Unit>()

    val inputs: LoginViewModel2Inputs = this
    val outputs: LoginViewModel2Outputs = this

    init {
        this.didPressLogin
            .withLatestFrom(this.cookie) { _, cookie -> cookie}
            .switchMapSingle { cookie ->
                this.apiRepository.login("nikog_90@hotmail.com", "ngui2873", cookie)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe {
                this.finish.onNext(Unit)
            }

    }


    override fun cookie(cookie: String) = this.cookie.onNext(cookie)
    override fun didPressLogin() = this.didPressLogin.onNext(Unit)
    override fun finish(): Observable<Unit> = this.finish

}