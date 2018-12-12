package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLError
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.AccountModel
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface AccountViewModelInputs : BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
//    fun didPressLogout()
    fun didLoadHtml(html: String)
}

interface AccountViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun mustLogin(): Observable<Unit>
    fun accountModel(): Observable<AccountModel>
}

class AccountViewModel(private val htmlParser: HTMLParser,
                       private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), AccountViewModelInputs, AccountViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
//    private val didPressLogout = PublishSubject.create<Unit>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val mustLogin = PublishSubject.create<Unit>()
    private val accountModel = PublishSubject.create<AccountModel>()

    val inputs: AccountViewModelInputs = this
    val outputs: AccountViewModelOutputs = this

    init {
        this.onCreate
            .map { "https://www.redditgifts.com/profiles/update/" }
            .doOnNext { this.isLoading.onNext(true) }
            .crashingSubscribe { this.loadHTML.onNext(it) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.htmlParser.parseAccount(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.accountModel.onNext(it.result)
                is GenericResult.Failed -> {
                    if(it.throwable is HTMLError.NeedsLogin) {
                        this.mustLogin.onNext(Unit)
                    }
                }
            } }

//        this.didPressLogout
//            .map { "https://www.redditgifts.com/profiles/logout/" }
//            .doOnNext { this.isLoading.onNext(true) }
//            .crashingSubscribe { this.loadHTML.onNext(it) }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
//    override fun didPressLogout() = this.didPressLogout.onNext(Unit)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun mustLogin(): Observable<Unit> = this.mustLogin
    override fun accountModel(): Observable<AccountModel> = this.accountModel

}