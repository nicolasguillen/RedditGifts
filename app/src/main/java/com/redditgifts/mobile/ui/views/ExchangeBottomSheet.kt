package com.redditgifts.mobile.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.utils.toSpanned
import com.redditgifts.mobile.models.ExchangeStatusViewModel
import com.redditgifts.mobile.services.models.CurrentExchangeModel
import com.redditgifts.mobile.services.models.ExchangeStatusModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.sheet_exchange.*
import javax.inject.Inject

class ExchangeBottomSheet(context: Context,
                          private val currentExchange: CurrentExchangeModel.Data.Exchange): BottomSheetDialog(context, R.style.Theme_RedditGifts_ExchangeBottomSheet) {

    @Inject lateinit var viewModel: ExchangeStatusViewModel

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading){
                    exchangeLoading.visibility = View.VISIBLE
                } else {
                    exchangeLoading.visibility = View.GONE
                }
            }

        viewModel.outputs.exchangeStatus()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { status ->
                exchangeStatus.visibility = View.VISIBLE
                for (i in status.data.asGiftee.todoList) {
                    val inflatedView = layoutInflater.inflate(R.layout.view_status_step, null)
                    val stepBubble = inflatedView.findViewById<TextView>(R.id.statusStep)
                    stepBubble.setStatusEnabled(i, status.data.asGiftee.currentStep)
                    exchangeStatusAsGiftee.findViewById<ViewGroup>(R.id.exchangeStatusList).addView(inflatedView)
                    val inflatedDetailedView = layoutInflater.inflate(R.layout.view_status_detailed_step, null) as CheckBox
                    inflatedDetailedView.setStatusEnabled(i, status.data.asGiftee.currentStep)
                    exchangeDetailedStatusAsGiftee.addView(inflatedDetailedView)
                }
                for (i in status.data.asSanta.todoList) {
                    val inflatedView = layoutInflater.inflate(R.layout.view_status_step, null)
                    val stepBubble = inflatedView.findViewById<TextView>(R.id.statusStep)
                    stepBubble.setStatusEnabled(i, status.data.asSanta.currentStep)
                    exchangeStatusAsSanta.findViewById<ViewGroup>(R.id.exchangeStatusList).addView(inflatedView)
                    val inflatedDetailedView = layoutInflater.inflate(R.layout.view_status_detailed_step, null) as CheckBox
                    inflatedDetailedView.setStatusEnabled(i, status.data.asSanta.currentStep)
                    exchangeDetailedStatusAsSanta.addView(inflatedDetailedView)
                }
            }

        viewModel.inputs.exchangeId(currentExchange.slug)

    }

    private fun loadViews() {
        setContentView(R.layout.sheet_exchange)
    }

    private val disposables = CompositeDisposable()

    private fun <I> Observable<I>.crashingSubscribe(onNext: (I) -> Unit) {
        subscribe(onNext) { throw OnErrorNotImplementedException(it) }.addTo(disposables)
    }

    private fun TextView.setStatusEnabled(todoList: ExchangeStatusModel.Data.Info.TodoList, currentStep: Int) {
        text = todoList.step.toString()
        isEnabled = todoList.step <= currentStep
    }

    private fun CheckBox.setStatusEnabled(todoList: ExchangeStatusModel.Data.Info.TodoList, currentStep: Int) {
        text = todoList.title.toSpanned()
        isChecked = todoList.step <= currentStep
    }

}