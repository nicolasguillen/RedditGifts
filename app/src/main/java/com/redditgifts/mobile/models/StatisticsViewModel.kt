package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.StatisticsModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface StatisticsViewModelInputs {
    fun onCreate()
    fun exchangeId(id: String)
}

interface StatisticsViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun statistics(): Observable<StatisticsModel>
    fun error(): Observable<String>
}

class StatisticsViewModel(private val apiRepository: ApiRepository,
                          private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), StatisticsViewModelInputs, StatisticsViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val statistics = PublishSubject.create<StatisticsModel>()
    private val error = PublishSubject.create<String>()

    val inputs: StatisticsViewModelInputs = this
    val outputs: StatisticsViewModelOutputs = this

    init {
        this.exchangeId
            .switchMapSingle { id ->
                this.apiRepository.getStatistics(id)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
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
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun statistics(): Observable<StatisticsModel> = this.statistics
    override fun error(): Observable<String> = this.error

}