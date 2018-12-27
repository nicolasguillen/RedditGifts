package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.MessageModel
import com.redditgifts.mobile.ui.viewholders.MessageViewHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface MessagesViewModelInputs : MessageViewHolder.Delegate {
    fun onCreate()
    fun loadPage(page: Int)
}

interface MessagesViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun messages(): Observable<MessagePageData>
    fun error(): Observable<String>
}

class MessagesViewModel(private val apiRepository: ApiRepository,
                        private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), MessagesViewModelInputs, MessagesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val didSelectMessage = PublishSubject.create<MessageModel.Data.Message>()
    private val loadPage = PublishSubject.create<Int>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val messages = PublishSubject.create<MessagePageData>()
    private val error = PublishSubject.create<String>()

    val inputs: MessagesViewModelInputs = this
    val outputs: MessagesViewModelOutputs = this

    init {
        this.onCreate
            .crashingSubscribe { this.loadPage.onNext(1) }

        this.loadPage
            .switchMapSingle { page ->
                this.apiRepository.getAllMessages(page)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .withLatestFrom(loadPage) { result, page -> GalleryResult(result, page) }
            .crashingSubscribe { resultData ->
                val result = resultData.result
                when(result) {
                    is GenericResult.Successful ->
                        this.messages.onNext(MessagePageData(resultData.page, result.result.data.messages))
                    is GenericResult.Failed ->
                        this.error.onNext(result.errorMessage)
                } }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun loadPage(page: Int) = this.loadPage.onNext(page)
    override fun didSelectMessage(gift: MessageModel.Data.Message) = this.didSelectMessage.onNext(gift)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun messages(): Observable<MessagePageData> = this.messages
    override fun error(): Observable<String> = this.error

    inner class GalleryResult(val result: GenericResult<MessageModel>, val page: Int)

}

class MessagePageData(val page: Int, val items: List<MessageModel.Data.Message>)