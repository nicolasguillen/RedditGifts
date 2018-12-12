package com.redditgifts.mobile.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.ExchangeStatusViewModel
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
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
                          private val exchange: ExchangeOverviewModel.Exchange): BottomSheetDialog(context, R.style.Theme_RedditGifts_ExchangeBottomSheet) {

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

        viewModel.outputs.loadHTML()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { url ->
                this.loadUrl(url)
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                hide()
            }

        viewModel.outputs.exchangeStatus()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { status ->
                exchangeStatus.visibility = View.VISIBLE
                for (i in status.santaStatus.indices) {
                    when(i) {
                        0 -> {
                            santaStatus1.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed1.setStatusEnabled(status.santaStatus[i])
                        }
                        1 -> {
                            santaStatus2.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed2.setStatusEnabled(status.santaStatus[i])
                        }
                        2 -> {
                            santaStatus3.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed3.setStatusEnabled(status.santaStatus[i])
                        }
                        3 -> {
                            santaStatus4.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed4.setStatusEnabled(status.santaStatus[i])
                        }
                        4 -> {
                            santaStatus5.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed5.setStatusEnabled(status.santaStatus[i])
                        }
                        5 -> {
                            santaStatus6.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed6.setStatusEnabled(status.santaStatus[i])
                        }
                        6 -> {
                            santaStatus7.setStatusEnabled(status.santaStatus[i])
                            santaStatusDetailed7.setStatusEnabled(status.santaStatus[i])
                        }
                    }
                }
                for (i in status.gifteeStatus.indices) {
                    when(i) {
                        0 -> {
                            gifteeStatus1.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed1.setStatusEnabled(status.gifteeStatus[i])
                        }
                        1 -> {
                            gifteeStatus2.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed2.setStatusEnabled(status.gifteeStatus[i])
                        }
                        2 -> {
                            gifteeStatus3.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed3.setStatusEnabled(status.gifteeStatus[i])
                        }
                        3 -> {
                            gifteeStatus4.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed4.setStatusEnabled(status.gifteeStatus[i])
                        }
                        4 -> {
                            gifteeStatus5.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed5.setStatusEnabled(status.gifteeStatus[i])
                        }
                        5 -> {
                            gifteeStatus6.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed6.setStatusEnabled(status.gifteeStatus[i])
                        }
                        6 -> {
                            gifteeStatus7.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed7.setStatusEnabled(status.gifteeStatus[i])
                        }
                        7 -> {
                            gifteeStatus8.setStatusEnabled(status.gifteeStatus[i])
                            gifteeStatusDetailed8.setStatusEnabled(status.gifteeStatus[i])
                        }
                    }
                }
            }

        viewModel.inputs.exchangeId(exchange.referenceId)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.sheet_exchange)

        webView.settings.javaScriptEnabled = true
    }

    private fun loadUrl(url: String) {
        webView.removeJavascriptInterface("HTMLOUT")
        webView.loadUrl(url)

        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =  true
            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            }
        }
    }

    internal inner class MyJavaScriptInterface {
        @Suppress("unused")
        @android.webkit.JavascriptInterface
        fun processHTML(html: String) {
            viewModel.inputs.didLoadHtml(html)
        }
    }

    private val disposables = CompositeDisposable()

    private fun <I> Observable<I>.crashingSubscribe(onNext: (I) -> Unit) {
        subscribe(onNext) { throw OnErrorNotImplementedException(it) }.addTo(disposables)
    }

    private fun TextView.setStatusEnabled(statusData: ExchangeStatusModel.StatusData) {
        isEnabled = statusData.status != ExchangeStatusModel.Status.INCOMPLETE
    }

    private fun CheckBox.setStatusEnabled(statusData: ExchangeStatusModel.StatusData) {
        text = statusData.title
        isChecked = statusData.status != ExchangeStatusModel.Status.INCOMPLETE
    }

}