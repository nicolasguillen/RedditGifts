package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

interface SendMessageViewModelInputs: BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
    fun messageTo(to: String)
    fun messageSubject(subject: String)
    fun messageBody(body: String)
    fun didPressSend()
}

interface SendMessageViewModelOutputs {
    fun isValid(): Observable<Boolean>
    fun isLoading(): Observable<Boolean>
    fun didSendMessage(): Observable<Unit>
    fun error(): Observable<String>
}

class SendMessageViewModel(private val apiRepository: ApiRepository,
                           private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), SendMessageViewModelInputs, SendMessageViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val messageTo = PublishSubject.create<String>()
    private val messageSubject = PublishSubject.create<String>()
    private val messageBody = PublishSubject.create<String>()
    private val didPressSend = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isValid = PublishSubject.create<Boolean>()
    private val isLoading = PublishSubject.create<Boolean>()
    private val didSendMessage = PublishSubject.create<Unit>()
    private val error = PublishSubject.create<String>()

    val inputs: SendMessageViewModelInputs = this
    val outputs: SendMessageViewModelOutputs = this

    private val messageData = PublishSubject.create<SendMessageData>()

    init {
        Observables.combineLatest(messageTo, messageSubject, messageBody) { to, subject, body -> SendMessageData(to, subject, body) }
            .crashingSubscribe { data ->
                messageData.onNext(data)
            }

        this.messageData
            .map { it.to.isNotEmpty() && it.subject.isNotEmpty() && it.body.isNotEmpty() }
            .crashingSubscribe { valid ->
                this.isValid.onNext(valid)
            }

        this.didPressSend
            .withLatestFrom(messageData) { _, data -> data }
            .switchMapSingle { data ->
                this.apiRepository.sendMessage(data.to, data.subject, data.body)
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { isLoading.onNext(true) }
                    .doOnSuccess { isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.didSendMessage.onNext(Unit)
                is GenericResult.Failed ->
                    this.error.onNext(it.errorMessage)
            } }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun messageTo(to: String) = this.messageTo.onNext(to)
    override fun messageSubject(subject: String) = this.messageSubject.onNext(subject)
    override fun messageBody(body: String) = this.messageBody.onNext(body)
    override fun didPressSend() = this.didPressSend.onNext(Unit)
    override fun isValid(): Observable<Boolean> = this.isValid
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun didSendMessage(): Observable<Unit> = this.didSendMessage
    override fun error(): Observable<String> = this.error

    inner class SendMessageData(val to: String, val subject: String, val body: String)
}