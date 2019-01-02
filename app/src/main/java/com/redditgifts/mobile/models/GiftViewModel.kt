package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.services.models.UpvoteGiftModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface GiftViewModelInputs {
    fun onCreate()
    fun exchangeId(exchangeId: String)
    fun giftSlug(giftSlug: String)
    fun upvote()
}

interface GiftViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun detailedGift(): Observable<DetailedGiftModel>
    fun upvoteData(): Observable<GiftViewModel.UpvoteData>
    fun error(): Observable<String>
}

class GiftViewModel(private val apiRepository: ApiRepository,
                    private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), GiftViewModelInputs, GiftViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()
    private val giftSlug = PublishSubject.create<String>()
    private val upvote = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val detailedGift = PublishSubject.create<DetailedGiftModel>()
    private val upvoteData = PublishSubject.create<UpvoteData>()
    private val error = PublishSubject.create<String>()

    val inputs: GiftViewModelInputs = this
    val outputs: GiftViewModelOutputs = this

    private val voteResult = PublishSubject.create<UpvoteGiftModel>()

    init {
        this.giftSlug
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

        this.upvote
            .withLatestFrom(this.giftSlug) { _, slug -> slug }
            .switchMapSingle { slug ->
                this.apiRepository.upvoteGift(slug)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.voteResult.onNext(it.result)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }

        this.voteResult
            .withLatestFrom(this.detailedGift)
            .map { pair ->
                when(pair.first.data.action) {
                    UpvoteGiftModel.UpvoteAction.VOTE.action ->
                        UpvoteData(true, pair.second.data.votes + 1)
                    UpvoteGiftModel.UpvoteAction.UNVOTE.action ->
                        UpvoteData(false, pair.second.data.votes)
                    else -> { UpvoteData(false, pair.second.data.votes) }
                }
            }
            .crashingSubscribe { data ->
                this.upvoteData.onNext(data)
            }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(exchangeId: String) = this.exchangeId.onNext(exchangeId)
    override fun giftSlug(giftSlug: String) = this.giftSlug.onNext(giftSlug)
    override fun upvote() = this.upvote.onNext(Unit)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun detailedGift(): Observable<DetailedGiftModel> = this.detailedGift
    override fun upvoteData(): Observable<UpvoteData> = this.upvoteData
    override fun error(): Observable<String> = this.error

    inner class UpvoteData(val didVote: Boolean, val votes: Int)
}