package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.redditgifts.mobile.models.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseActivity<ViewModelType: DisposableViewModel> : AppCompatActivity(){

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}