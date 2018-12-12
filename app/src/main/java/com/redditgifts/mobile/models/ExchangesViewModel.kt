package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLError
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
import com.redditgifts.mobile.ui.viewholders.ExchangeViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ExchangesViewModelInputs : ExchangeViewHolder.Delegate {
    fun onCreate()
    fun didLoadHtml(html: String)
}

interface ExchangesViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun exchangeOverview(): Observable<ExchangeOverviewModel>
    fun mustLogin(): Observable<Unit>
    fun showExchange(): Observable<ExchangeOverviewModel.Exchange>
}

class ExchangesViewModel(private val htmlParser: HTMLParser,
                         private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), ExchangesViewModelInputs, ExchangesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val exchangeOverview = PublishSubject.create<ExchangeOverviewModel>()
    private val mustLogin = PublishSubject.create<Unit>()
    private val showExchange = PublishSubject.create<ExchangeOverviewModel.Exchange>()

    val inputs: ExchangesViewModelInputs = this
    val outputs: ExchangesViewModelOutputs = this

    init {
        this.onCreate
            .map { "https://www.redditgifts.com/exchanges/#/select/my-current/" }
            .doOnNext { this.isLoading.onNext(true) }
            .crashingSubscribe { this.loadHTML.onNext(it) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.htmlParser.parseExchanges(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.exchangeOverview.onNext(it.result)
                is GenericResult.Failed -> {
                    if(it.throwable is HTMLError.NeedsLogin) {
                        this.mustLogin.onNext(Unit)
                    }
                }
            } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun didSelectExchange(exchange: ExchangeOverviewModel.Exchange) = this.showExchange.onNext(exchange)
    override fun exchangeOverview(): Observable<ExchangeOverviewModel> = this.exchangeOverview
    override fun mustLogin(): Observable<Unit> = this.mustLogin
    override fun showExchange(): Observable<ExchangeOverviewModel.Exchange> = this.showExchange

}