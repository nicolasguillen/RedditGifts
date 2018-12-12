package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.GiftModel
import com.redditgifts.mobile.ui.viewholders.GalleryViewHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface GalleryViewModelInputs : GalleryViewHolder.Delegate {
    fun onCreate()
    fun loadPage(page: Int)
    fun didLoadHtml(html: String)
}

interface GalleryViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
    fun galleryPageData(): Observable<GalleryPageData>
    fun startGiftDetail(): Observable<GiftModel>
}

class GalleryViewModel(private val htmlParser: HTMLParser,
                       private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), GalleryViewModelInputs, GalleryViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val loadPage = PublishSubject.create<Int>()
    private val didLoadHtml = PublishSubject.create<String>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()
    private val galleryPageData = PublishSubject.create<GalleryPageData>()
    private val startGiftDetail = PublishSubject.create<GiftModel>()

    val inputs: GalleryViewModelInputs = this
    val outputs: GalleryViewModelOutputs = this

    init {
        this.loadPage
            .map { "https://www.redditgifts.com/gallery/#/?type=exchanges&pageNumber=$it&pageSize=25&sort=date&sortDirection=DESC&filterExchange=secret-santa-2018" }
            .crashingSubscribe { url ->
                this.isLoading.onNext(true)
                this.loadHTML.onNext(url)
            }

        this.onCreate
            .crashingSubscribe { this.loadPage.onNext(1) }

        this.didLoadHtml
            .switchMapSingle { html ->
                this.htmlParser.parseGallery(html)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .withLatestFrom(loadPage)
            .crashingSubscribe { pair ->
                val result = pair.first
                when(result) {
                    is GenericResult.Successful ->
                        this.galleryPageData.onNext(GalleryPageData(pair.second, result.result))
                } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun loadPage(page: Int) = this.loadPage.onNext(page)
    override fun didLoadHtml(html: String) = this.didLoadHtml.onNext(html)
    override fun didSelectGift(gift: GiftModel) = this.startGiftDetail.onNext(gift)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML
    override fun galleryPageData(): Observable<GalleryPageData> = this.galleryPageData
    override fun startGiftDetail(): Observable<GiftModel> = this.startGiftDetail
}

class GalleryPageData(val page: Int, val items: List<GiftModel>)