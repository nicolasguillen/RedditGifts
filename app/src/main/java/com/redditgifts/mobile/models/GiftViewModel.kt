package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.DetailedGiftModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface GiftViewModelInputs {
    fun onCreate()
    fun exchangeId(exchangeId: String)
    fun giftId(giftId: String)
}

interface GiftViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun detailedGift(): Observable<DetailedGiftModel>
    fun error(): Observable<String>
}

class GiftViewModel(private val apiRepository: ApiRepository,
                    private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), GiftViewModelInputs, GiftViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()
    private val giftId = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val detailedGift = PublishSubject.create<DetailedGiftModel>()
    private val error = PublishSubject.create<String>()

    val inputs: GiftViewModelInputs = this
    val outputs: GiftViewModelOutputs = this

    init {
        this.giftId
            .withLatestFrom(this.exchangeId)
            .switchMapSingle { pair ->
                this.apiRepository.getDetailedGift(pair.second, pair.first)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.detailedGift.onNext(it.result)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(exchangeId: String) = this.exchangeId.onNext(exchangeId)
    override fun giftId(giftId: String) = this.giftId.onNext(giftId)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun detailedGift(): Observable<DetailedGiftModel> = this.detailedGift
    override fun error(): Observable<String> = this.error

}