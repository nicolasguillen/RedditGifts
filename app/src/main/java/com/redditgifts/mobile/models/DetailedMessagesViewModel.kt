package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.MessageModel
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface DetailedMessagesViewModelInputs: BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
    fun messageId(id: Int)
    fun replyMessage(message: String)
    fun didPressReply()
}

interface DetailedMessagesViewModelOutputs {
    fun isReplyValid(): Observable<Boolean>
    fun isLoading(): Observable<Boolean>
    fun messages(): Observable<List<MessageModel.Data.Message>>
    fun didReplyMessage(): Observable<Unit>
    fun error(): Observable<String>
}

class DetailedMessagesViewModel(private val apiRepository: ApiRepository,
                                private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), DetailedMessagesViewModelInputs, DetailedMessagesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val messageId = PublishSubject.create<Int>()
    private val replyMessage = PublishSubject.create<String>()
    private val didPressReply = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isReplyValid = PublishSubject.create<Boolean>()
    private val isLoading = PublishSubject.create<Boolean>()
    private val messages = PublishSubject.create<List<MessageModel.Data.Message>>()
    private val didReplyMessage = PublishSubject.create<Unit>()
    private val error = PublishSubject.create<String>()

    val inputs: DetailedMessagesViewModelInputs = this
    val outputs: DetailedMessagesViewModelOutputs = this

    init {
        this.onCreate
            .withLatestFrom(messageId) { _, id -> id }
            .switchMapSingle { id ->
                this.apiRepository.getDetailedMessages(id)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.messages.onNext(it.result.data.messages)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }

        this.onCreate
            .crashingSubscribe {
                this.isReplyValid.onNext(false)
            }

        this.replyMessage
            .map { it.isNotEmpty() }
            .crashingSubscribe { isValid ->
                this.isReplyValid.onNext(isValid)
            }

        this.didPressReply
            .withLatestFrom(messageId, replyMessage) { _, id, message -> ReplyMessageData(id, message) }
            .switchMapSingle { data ->
                this.apiRepository.sendReplyMessage(data.messageId, data.message)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { isLoading.onNext(true) }
                    .doOnSuccess { isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.didReplyMessage.onNext(Unit)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun messageId(id: Int) = this.messageId.onNext(id)
    override fun replyMessage(message: String) = this.replyMessage.onNext(message)
    override fun didPressReply() = this.didPressReply.onNext(Unit)
    override fun isReplyValid(): Observable<Boolean> = this.isReplyValid
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun messages(): Observable<List<MessageModel.Data.Message>> = this.messages
    override fun didReplyMessage(): Observable<Unit> = this.didReplyMessage
    override fun error(): Observable<String> = this.error

    inner class ReplyMessageData(val messageId: Int, val message: String)

}