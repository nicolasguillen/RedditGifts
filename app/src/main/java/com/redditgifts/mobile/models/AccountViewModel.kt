package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.ProfileModel
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface AccountViewModelInputs : BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
}

interface AccountViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun profileModel(): Observable<ProfileModel>
}

class AccountViewModel(private val apiRepository: ApiRepository,
                       private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), AccountViewModelInputs, AccountViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val profileModel = PublishSubject.create<ProfileModel>()

    val inputs: AccountViewModelInputs = this
    val outputs: AccountViewModelOutputs = this

    init {
        this.onCreate
            .switchMapSingle {
                this.apiRepository.getProfile()
                    .lift(Operators.genericResult(this.localizedErrorMessages))
                    .doOnSubscribe { this.isLoading.onNext(true) }
                    .doOnSuccess { this.isLoading.onNext(false) }
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.profileModel.onNext(it.result)
            } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun profileModel(): Observable<ProfileModel> = this.profileModel

}