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
}

interface DetailedMessagesViewModelOutputs {
    fun messages(): Observable<List<MessageModel.Data.Message>>
    fun error(): Observable<String>
}

class DetailedMessagesViewModel(private val apiRepository: ApiRepository,
                                private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), DetailedMessagesViewModelInputs, DetailedMessagesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val messageId = PublishSubject.create<Int>()

    //OUTPUTS
    private val messages = PublishSubject.create<List<MessageModel.Data.Message>>()
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
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun messageId(id: Int) = this.messageId.onNext(id)
    override fun messages(): Observable<List<MessageModel.Data.Message>> = this.messages
    override fun error(): Observable<String> = this.error

}