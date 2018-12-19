package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.GenericResult
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.libs.operators.Operators
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.ProfileModel
import com.redditgifts.mobile.storage.CookieRepository
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface AccountViewModelInputs : BaseViewHolder.BaseViewHolderDelegate {
    fun onCreate()
    fun didPressLogout()
}

interface AccountViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun profileModel(): Observable<ProfileModel>
    fun didLogout(): Observable<Unit>
}

class AccountViewModel(private val apiRepository: ApiRepository,
                       private val cookieRepository: CookieRepository,
                       private val localizedErrorMessages: LocalizedErrorMessages): DisposableViewModel(), AccountViewModelInputs, AccountViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()
    private val didPressLogout = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val profileModel = PublishSubject.create<ProfileModel>()
    private val didLogout = PublishSubject.create<Unit>()

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

        this.didPressLogout
            .switchMapSingle {
                this.cookieRepository.removeCookie()
                    .lift(Operators.genericResult(this.localizedErrorMessages))
            }
            .crashingSubscribe { when(it) {
                is GenericResult.Successful ->
                    this.didLogout.onNext(Unit)
            } }

    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun didPressLogout() = this.didPressLogout.onNext(Unit)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun profileModel(): Observable<ProfileModel> = this.profileModel
    override fun didLogout(): Observable<Unit> = this.didLogout

}