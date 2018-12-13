package com.redditgifts.mobile.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.StatisticsViewModel
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.sheet_statistics.*
import java.util.*
import javax.inject.Inject

class StatisticsBottomSheet(context: Context,
                            private val exchange: ExchangeOverviewModel.Exchange): BottomSheetDialog(context, R.style.Theme_RedditGifts_ExchangeBottomSheet) {

    @Inject lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading){
                    statisticsLoading.visibility = View.VISIBLE
                } else {
                    statisticsLoading.visibility = View.GONE
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

        viewModel.outputs.statistics()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { statistics ->
                statisticsList.visibility = View.VISIBLE
                this.setStatisticsValue(statisticsParticipants, R.string.statistics_participants, statistics.participants, statistics.participants)
                this.setStatisticsValue(statisticsMatches, R.string.statistics_matches, statistics.matches, statistics.participants)
                this.setStatisticsValue(statisticsRetrieved, R.string.statistics_retrieved, statistics.retrieved, statistics.participants)
                this.setStatisticsValue(statisticsShipped, R.string.statistics_shipped, statistics.shipped, statistics.participants)
                this.setStatisticsValue(statisticsGiftInGallery, R.string.statistics_gift, statistics.giftInGallery, statistics.participants)
                this.setStatisticsValue(statisticsRematchSignups, R.string.statistics_rematch_signups, statistics.rematchSignups, statistics.rematchSignups)
                this.setStatisticsValue(statisticsRematches, R.string.statistics_rematch_done, statistics.rematchCompleted, statistics.rematchSignups)
            }

        viewModel.inputs.exchangeId(exchange.referenceId)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.sheet_statistics)

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

    private fun setStatisticsValue(parent: View, @StringRes title: Int, value: Number, total: Number) {
        val percentage = parent.findViewById<TextView>(R.id.statisticsDataPercentage)
        percentage.text = "%d%%".format((value.toDouble()*100/total.toDouble()).toInt())
        val progress = parent.findViewById<ProgressBar>(R.id.statisticsDataProgress)
        progress.max = total.toInt()
        progress.progress = value.toInt()
        val progressValue = parent.findViewById<TextView>(R.id.statisticsDataProgressValue)
        progressValue.text = String.format(Locale.getDefault(), "%,d", value.toInt())
        val titleValue = parent.findViewById<TextView>(R.id.statisticsDataTitle)
        titleValue.setText(title)
    }

}
