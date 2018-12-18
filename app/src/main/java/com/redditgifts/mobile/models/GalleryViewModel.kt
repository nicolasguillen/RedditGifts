package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.GalleryModel
import com.redditgifts.mobile.ui.viewholders.GalleryViewHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface GalleryViewModelInputs : GalleryViewHolder.Delegate {
    fun onCreate()
    fun exchangeId(id: String)
    fun loadPage(page: Int)
}

interface GalleryViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun galleryPageData(): Observable<GalleryPageData>
    fun startGiftDetail(): Observable<GalleryModel.Data.GiftModel>
}

class GalleryViewModel(private val apiRepository: ApiRepository,
                       private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), GalleryViewModelInputs, GalleryViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val exchangeId = PublishSubject.create<String>()
    private val loadPage = PublishSubject.create<Int>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val galleryPageData = PublishSubject.create<GalleryPageData>()
    private val startGiftDetail = PublishSubject.create<GalleryModel.Data.GiftModel>()

    val inputs: GalleryViewModelInputs = this
    val outputs: GalleryViewModelOutputs = this

    init {
        this.onCreate
            .crashingSubscribe { this.loadPage.onNext(1) }

        this.loadPage
            .withLatestFrom(exchangeId)
            .switchMapSingle { pair ->
                this.apiRepository.getGallery(pair.second, 25, pair.first)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .withLatestFrom(loadPage)
            .crashingSubscribe { pair ->
                val result = pair.first
                when(result) {
                    is GenericResult.Successful ->
                        this.galleryPageData.onNext(GalleryPageData(pair.second, result.result.data.gifts))
                } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(id: String) = this.exchangeId.onNext(id)
    override fun loadPage(page: Int) = this.loadPage.onNext(page)
    override fun didSelectGift(gift: GalleryModel.Data.GiftModel) = this.startGiftDetail.onNext(gift)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun galleryPageData(): Observable<GalleryPageData> = this.galleryPageData
    override fun startGiftDetail(): Observable<GalleryModel.Data.GiftModel> = this.startGiftDetail

}

class GalleryPageData(val page: Int, val items: List<GalleryModel.Data.GiftModel>)