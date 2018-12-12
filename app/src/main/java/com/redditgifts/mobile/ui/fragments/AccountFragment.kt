package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.AccountViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseFragment<AccountViewModel>() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_account, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()

        viewModel.outputs.loadHTML()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { url ->
                this.loadUrl(url)
            }

        accountLogout.setOnClickListener {
            viewModel.inputs.didPressLogout()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        webView.settings.javaScriptEnabled = true
    }

    private fun loadUrl(url: String) {
        webView.webViewClient = null
        webView.removeJavascriptInterface("HTMLOUT")
        webView.loadUrl("about:blank")
        webView.loadUrl(url)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = true
        }
    }



}