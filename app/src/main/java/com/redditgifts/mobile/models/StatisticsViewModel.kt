package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface StatisticsViewModelInputs {
    fun onCreate()
    fun exchangeId(id: String)
    fun didLoadHtml(html: String)
}

interface StatisticsViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun statistics(): Observable<StatisticsModel>
    fun error(): Observable<String>
}

class StatisticsViewModel(private val htmlParser: HTMLParser,
                          private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), StatisticsViewModelInputs, StatisticsViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val statistics = PublishSubject.create<StatisticsModel>()
    private val error = PublishSubject.create<String>()

    val inputs: StatisticsViewModelInputs = this
    val outputs: StatisticsViewModelOutputs = this

    init {
        this.exchangeId
            .map { "https://www.redditgifts.com/exchanges/#/stats/$it" }
            .doOnNext { this.isLoading.onNext(true) }
            .crashingSubscribe { this.loadHTML.onNext(it) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.htmlParser.parseStatistics(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.statistics.onNext(it.result)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(id: String) = this.exchangeId.onNext(id)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun statistics(): Observable<StatisticsModel> = this.statistics
    override fun error(): Observable<String> = this.error

}