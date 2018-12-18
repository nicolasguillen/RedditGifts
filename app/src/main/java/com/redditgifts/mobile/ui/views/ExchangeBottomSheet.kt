package com.redditgifts.mobile.ui.views

import android.content.Context
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.view_giftee_status.*
import kotlinx.android.synthetic.main.view_giftee_status_detail.*
import kotlinx.android.synthetic.main.view_santa_status.*
import kotlinx.android.synthetic.main.view_santa_status_detail.*
import javax.inject.Inject

class ExchangeBottomSheet(context: Context,
                          private val currentExchange: CurrentExchangeModel.Data.Exchange): BottomSheetDialog(context, R.style.Theme_RedditGifts_ExchangeBottomSheet) {

    @Inject lateinit var viewModel: ExchangeStatusViewModel

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
                    when(i.step) {
                        1 -> {
                            santaStatus1.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed1.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        2 -> {
                            santaStatus2.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed2.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        3 -> {
                            santaStatus3.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed3.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        4 -> {
                            santaStatus4.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed4.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        5 -> {
                            santaStatus5.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed5.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        6 -> {
                            santaStatus6.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed6.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                        7 -> {
                            santaStatus7.setStatusEnabled(i, status.data.asGiftee.currentStep)
                            santaStatusDetailed7.setStatusEnabled(i, status.data.asGiftee.currentStep)
                        }
                    }
                }
                for (i in status.data.asSanta.todoList) {
                    when(i.step) {
                        1 -> {
                            gifteeStatus1.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed1.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        2 -> {
                            gifteeStatus2.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed2.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        3 -> {
                            gifteeStatus3.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed3.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        4 -> {
                            gifteeStatus4.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed4.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        5 -> {
                            gifteeStatus5.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed5.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        6 -> {
                            gifteeStatus6.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed6.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        7 -> {
                            gifteeStatus7.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed7.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                        8 -> {
                            gifteeStatus8.setStatusEnabled(i, status.data.asSanta.currentStep)
                            gifteeStatusDetailed8.setStatusEnabled(i, status.data.asSanta.currentStep)
                        }
                    }
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
        isEnabled = todoList.step < currentStep
    }

    private fun CheckBox.setStatusEnabled(todoList: ExchangeStatusModel.Data.Info.TodoList, currentStep: Int) {
        text = todoList.title.toSpanned()
        isChecked = todoList.step < currentStep
    }

}