package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.ExchangeStatusModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ExchangeStatusViewModelInputs {
    fun onCreate()
    fun exchangeId(id: String)
}

interface ExchangeStatusViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun exchangeStatus(): Observable<ExchangeStatusModel>
    fun error(): Observable<String>
}

class ExchangeStatusViewModel(private val apiRepository: ApiRepository,
                              private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), ExchangeStatusViewModelInputs, ExchangeStatusViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val exchangeStatus = PublishSubject.create<ExchangeStatusModel>()
    private val error = PublishSubject.create<String>()

    val inputs: ExchangeStatusViewModelInputs = this
    val outputs: ExchangeStatusViewModelOutputs = this

    init {
        this.exchangeId
            .switchMapSingle { exchangeId ->
                this.apiRepository.getExchangeStatus(exchangeId)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
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
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun exchangeStatus(): Observable<ExchangeStatusModel> = this.exchangeStatus
    override fun error(): Observable<String> = this.error

}