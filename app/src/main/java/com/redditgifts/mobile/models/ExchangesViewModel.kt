package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.CreditModel
import com.redditgifts.mobile.services.models.CurrentExchangeModel
import com.redditgifts.mobile.ui.viewholders.ExchangeViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ExchangesViewModelInputs : ExchangeViewHolder.Delegate {
    fun onCreate()
    fun didLogin()
}

interface ExchangesViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun exchangeOverview(): Observable<CurrentExchangeModel>
    fun credits(): Observable<CreditModel>
    fun showExchange(): Observable<CurrentExchangeModel.Data.Exchange>
    fun showStatistics(): Observable<CurrentExchangeModel.Data.Exchange>
    fun showGallery(): Observable<CurrentExchangeModel.Data.Exchange>
}

class ExchangesViewModel(private val apiRepository: ApiRepository,
                         private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), ExchangesViewModelInputs, ExchangesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val didLogin = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val exchangeOverview = PublishSubject.create<CurrentExchangeModel>()
    private val credits = PublishSubject.create<CreditModel>()
    private val showExchange = PublishSubject.create<CurrentExchangeModel.Data.Exchange>()
    private val showStatistics = PublishSubject.create<CurrentExchangeModel.Data.Exchange>()
    private val showGallery = PublishSubject.create<CurrentExchangeModel.Data.Exchange>()

    val inputs: ExchangesViewModelInputs = this
    val outputs: ExchangesViewModelOutputs = this

    init {
        this.onCreate
            .switchMapSingle {
                this.apiRepository.getCurrentExchanges()
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.exchangeOverview.onNext(it.result)
            } }

        this.exchangeOverview
            .switchMapSingle {
                this.apiRepository.getCredits()
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.credits.onNext(it.result)
            } }

        this.didLogin
            .crashingSubscribe {
                this.onCreate.onNext(Unit)
            }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun didLogin() = this.didLogin.onNext(Unit)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun didSelectOpenStatus(currentExchange: CurrentExchangeModel.Data.Exchange) = this.showExchange.onNext(currentExchange)
    override fun didSelectOpenStatistics(currentExchange: CurrentExchangeModel.Data.Exchange) = this.showStatistics.onNext(currentExchange)
    override fun didSelectOpenGallery(currentExchange: CurrentExchangeModel.Data.Exchange) = this.showGallery.onNext(currentExchange)
    override fun exchangeOverview(): Observable<CurrentExchangeModel> = this.exchangeOverview
    override fun credits(): Observable<CreditModel> = this.credits
    override fun showExchange(): Observable<CurrentExchangeModel.Data.Exchange> = this.showExchange
    override fun showStatistics(): Observable<CurrentExchangeModel.Data.Exchange> = this.showStatistics
    override fun showGallery(): Observable<CurrentExchangeModel.Data.Exchange> = this.showGallery

}