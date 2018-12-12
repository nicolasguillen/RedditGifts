package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
import com.redditgifts.mobile.services.models.ExchangeStatusModel
import com.redditgifts.mobile.ui.viewholders.ExchangeViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ExchangeStatusViewModelInputs : ExchangeViewHolder.Delegate {
    fun onCreate()
    fun exchangeId(id: String)
    fun didLoadHtml(html: String)
}

interface ExchangeStatusViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun exchangeStatus(): Observable<ExchangeStatusModel>
    fun showExchange(): Observable<ExchangeOverviewModel.Exchange>
    fun error(): Observable<String>
}

class ExchangeStatusViewModel(private val htmlParser: HTMLParser,
                              private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), ExchangeStatusViewModelInputs, ExchangeStatusViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val exchangeStatus = PublishSubject.create<ExchangeStatusModel>()
    private val showExchange = PublishSubject.create<ExchangeOverviewModel.Exchange>()
    private val error = PublishSubject.create<String>()

    val inputs: ExchangeStatusViewModelInputs = this
    val outputs: ExchangeStatusViewModelOutputs = this

    init {
        this.exchangeId
            .map { "https://www.redditgifts.com/exchanges/$it" }
            .doOnNext { this.isLoading.onNext(true) }
            .crashingSubscribe { this.loadHTML.onNext(it) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.htmlParser.parseStatuses(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.exchangeStatus.onNext(it.result)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(id: String) = this.exchangeId.onNext(id)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun didSelectExchange(exchange: ExchangeOverviewModel.Exchange) = this.showExchange.onNext(exchange)
    override fun exchangeStatus(): Observable<ExchangeStatusModel> = this.exchangeStatus
    override fun showExchange(): Observable<ExchangeOverviewModel.Exchange> = this.showExchange
    override fun error(): Observable<String> = this.error

}