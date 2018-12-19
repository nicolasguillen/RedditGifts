package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.storage.CookieRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface LoginViewModelInputs {
    fun email(email: String)
    fun password(password: String)
    fun cookie(cookie: String)
    fun didPressLogin()
}

interface LoginViewModelOutputs {
    fun isFormValid(): Observable<Boolean>
    fun isLoading(): Observable<Boolean>
    fun finish(): Observable<Unit>
    fun error(): Observable<String>
}

class LoginViewModel(private val apiRepository: ApiRepository,
                     private val cookieRepository: CookieRepository,
                     private val localizedErrorMessages: LocalizedErrorMessages) : DisposableViewModel(), LoginViewModelInputs, LoginViewModelOutputs {

    //INPUTS
    private val email = PublishSubject.create<String>()
    private val password = PublishSubject.create<String>()
    private val cookie = PublishSubject.create<String>()
    private val didPressLogin = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isFormValid = PublishSubject.create<Boolean>()
    private val isLoading = PublishSubject.create<Boolean>()
    private val finish = PublishSubject.create<Unit>()
    private val error = PublishSubject.create<String>()

    val inputs: LoginViewModelInputs = this
    val outputs: LoginViewModelOutputs = this

    private val loginData = PublishSubject.create<LoginData>()

    init {
        Observables.combineLatest(this.email, this.password, this.cookie) { email, password, cookie -> LoginData(email, password, cookie) }
            .crashingSubscribe { data ->
                this.loginData.onNext(data)
            }

        this.loginData
            .map { it.email.isNotEmpty() && it.password.isNotEmpty() && it.cookies.isNotEmpty() }
            .crashingSubscribe { isValid ->
                this.isFormValid.onNext(isValid)
            }

        this.didPressLogin
            .withLatestFrom(this.loginData) { _, data -> data}
            .switchMapSingle { data ->
                this.performLogin(data)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.finish.onNext(it.result)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }
    }

    private fun performLogin(data: LoginData): Single<Unit> {
        return this.apiRepository.login(data.email, data.password, data.cookies)
            .flatMap { newCookie ->
                this.cookieRepository.storeCookie(newCookie)
            }
    }

    override fun email(email: String) = this.email.onNext(email)
    override fun password(password: String) = this.password.onNext(password)
    override fun cookie(cookie: String) = this.cookie.onNext(cookie)
    override fun didPressLogin() = this.didPressLogin.onNext(Unit)
    override fun isFormValid(): Observable<Boolean> = this.isFormValid
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun finish(): Observable<Unit> = this.finish
    override fun error(): Observable<String> = this.error

    inner class LoginData(val email: String, val password: String, val cookies: String)

}