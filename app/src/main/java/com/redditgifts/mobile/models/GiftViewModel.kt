package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface GiftViewModelInputs : BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
    fun giftId(giftId: String)
    fun didLoadHtml(html: String)
}

interface GiftViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun detailedGift(): Observable<DetailedGiftModel>
    fun error(): Observable<String>
}

class GiftViewModel(private val HTMLParser: HTMLParser,
                    private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), GiftViewModelInputs, GiftViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val giftId = PublishSubject.create<String>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val detailedGift = PublishSubject.create<DetailedGiftModel>()
    private val error = PublishSubject.create<String>()

    val inputs: GiftViewModelInputs = this
    val outputs: GiftViewModelOutputs = this

    init {
        this.onCreate
            .crashingSubscribe { this.isLoading.onNext(true) }

        this.giftId
            .map { "https://www.redditgifts.com$it" }
            .crashingSubscribe { this.loadHTML.onNext(it) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.HTMLParser.parseGift(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
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
    override fun giftId(giftId: String) = this.giftId.onNext(giftId)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun detailedGift(): Observable<DetailedGiftModel> = this.detailedGift
    override fun error(): Observable<String> = this.error

}