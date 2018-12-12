package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import com.redditgifts.mobile.models.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseFragment<ViewModelType: DisposableViewModel> : Fragment(){

    private val disposables = CompositeDisposable()

    @Inject lateinit var viewModel: ViewModelType

    protected fun <I> Observable<I>.crashingSubscribe(onNext: (I) -> Unit) {
        subscribe(onNext) { throw OnErrorNotImplementedException(it) }.addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        viewModel.clear()
    }

}