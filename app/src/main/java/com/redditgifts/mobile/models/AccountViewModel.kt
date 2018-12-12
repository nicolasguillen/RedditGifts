package com.redditgifts.mobile.models

import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface AccountViewModelInputs : BaseViewHolder.BaseViewHolderDelegate {
    fun didPressLogout()
}

interface AccountViewModelOutputs {
    fun isLoading(): Observable<Boolean>
    fun loadHTML(): Observable<String>
}

class AccountViewModel: DisposableViewModel(), AccountViewModelInputs, AccountViewModelOutputs {

    //INPUTS
    private val didPressLogout = PublishSubject.create<Unit>()

    //OUTPUTS
    private val isLoading = PublishSubject.create<Boolean>()
    private val loadHTML = PublishSubject.create<String>()

    val inputs: AccountViewModelInputs = this
    val outputs: AccountViewModelOutputs = this

    init {
        this.didPressLogout
            .map { "https://www.redditgifts.com/profiles/logout/" }
            .crashingSubscribe { this.loadHTML.onNext(it) }
    }

    override fun didPressLogout() = this.didPressLogout.onNext(Unit)
    override fun isLoading(): Observable<Boolean> = this.isLoading
    override fun loadHTML(): Observable<String> = this.loadHTML

}