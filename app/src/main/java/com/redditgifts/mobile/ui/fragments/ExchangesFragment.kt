package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.ActivityRequestCodes
import com.redditgifts.mobile.libs.ActivityRequestCodes.LOGIN_WORKFLOW
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.models.ExchangesViewModel
import com.redditgifts.mobile.ui.activities.GalleryActivity
import com.redditgifts.mobile.ui.views.ExchangeBottomSheet
import com.redditgifts.mobile.ui.activities.LoginActivity
import com.redditgifts.mobile.ui.adapters.GenericAdapter
import com.redditgifts.mobile.ui.views.StatisticsBottomSheet
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.cell_loader.*
import kotlinx.android.synthetic.main.fragment_exchanges.*

class ExchangesFragment : BaseFragment<ExchangesViewModel>() {

    private var loaderAnimation: AnimationDrawable? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_exchanges, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading){
                    loader.visibility = View.VISIBLE
                    loader.apply {
                        setBackgroundResource(R.drawable.loader)
                        loaderAnimation = background as AnimationDrawable
                    }
                    loaderAnimation?.start()
                } else {
                    loader.visibility = View.GONE
                    loaderAnimation?.stop()
                }
            }

        viewModel.outputs.loadHTML()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { url ->
                this.loadUrl(url)
            }

        viewModel.outputs.exchangeOverview()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { data ->
                exchangesLogin.visibility = View.INVISIBLE
                exchangesCredits.text = getString(R.string.exchanges_credits).format(data.credits)
                exchangesList.adapter = GenericAdapter(this.viewModel.inputs, data.listCurrentExchanges)
            }

        viewModel.outputs.mustLogin()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                startActivityForResult(Intent(context, LoginActivity::class.java), LOGIN_WORKFLOW)
            }

        viewModel.outputs.showExchange()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { exchange ->
                if(isAdded) {
                    ExchangeBottomSheet(context!!, exchange).show()
                }
            }

        viewModel.outputs.showStatistics()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { exchange ->
                if(isAdded) {
                    StatisticsBottomSheet(context!!, exchange).show()
                }
            }

        viewModel.outputs.showGallery()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { exchange ->
                startActivity(
                    Intent(activity, GalleryActivity::class.java)
                        .putExtra(IntentKey.EXCHANGE_ID, exchange.referenceId)
                        .putExtra(IntentKey.EXCHANGE_TITLE, exchange.title))
            }

        exchangesLogin.setOnClickListener {
            startActivityForResult(Intent(context, LoginActivity::class.java), LOGIN_WORKFLOW)
        }

        viewModel.inputs.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ActivityRequestCodes.LOGIN_WORKFLOW -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        webView.reload()
                        exchangesLogin.visibility = View.INVISIBLE
                    }
                    else ->
                        exchangesLogin.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        webView.settings.javaScriptEnabled = true

        val linearLayoutManager = LinearLayoutManager(context)
        exchangesList.layoutManager = linearLayoutManager
        exchangesList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

}