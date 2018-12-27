package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface MainViewModelInputs {
    fun onCreate()
}

interface MainViewModelOutputs {
    fun amountOfUnreadMessages(): Observable<Int>
}

class MainViewModel(private val apiRepository: ApiRepository,
                    private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), MainViewModelInputs, MainViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()

    //OUTPUTS
    private val amountOfUnreadMessages = PublishSubject.create<Int>()

    val inputs: MainViewModelInputs = this
    val outputs: MainViewModelOutputs = this

    init {
        this.onCreate
            .switchMapSingle {
                this.apiRepository.getUnreadMessages()
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.amountOfUnreadMessages.onNext(it.result.data.total_number_messages)
            } }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun amountOfUnreadMessages(): Observable<Int> = this.amountOfUnreadMessages

}