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
    fun error(): Observable<String>
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
    private val error = PublishSubject.create<String>()
    private val startGiftDetail = PublishSubject.create<GalleryModel.Data.GiftModel>()

    val inputs: GalleryViewModelInputs = this
    val outputs: GalleryViewModelOutputs = this

    init {
        this.onCreate
            .crashingSubscribe { this.loadPage.onNext(1) }

        this.loadPage
            .withLatestFrom(exchangeId) { page, id -> PageData(page, id) }
            .switchMapSingle { pageData ->
                this.apiRepository.getGallery(pageData.exchangeId, 25, pageData.page)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .withLatestFrom(loadPage) { result, page -> GalleryResult(result, page) }
            .crashingSubscribe { resultData ->
                val result = resultData.result
                when(result) {
                    is GenericResult.Successful ->
                        this.galleryPageData.onNext(GalleryPageData(resultData.page, result.result.data.gifts))
                    is GenericResult.Failed ->
                        this.error.onNext(result.errorMessage)
                } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun exchangeId(id: String) = this.exchangeId.onNext(id)
    override fun loadPage(page: Int) = this.loadPage.onNext(page)
    override fun didSelectGift(gift: GalleryModel.Data.GiftModel) = this.startGiftDetail.onNext(gift)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun galleryPageData(): Observable<GalleryPageData> = this.galleryPageData
    override fun error(): Observable<String> = this.error
    override fun startGiftDetail(): Observable<GalleryModel.Data.GiftModel> = this.startGiftDetail

    inner class PageData(val page: Int, val exchangeId: String)
    inner class GalleryResult(val result: GenericResult<GalleryModel>, val page: Int)

}

class GalleryPageData(val page: Int, val items: List<GalleryModel.Data.GiftModel>)