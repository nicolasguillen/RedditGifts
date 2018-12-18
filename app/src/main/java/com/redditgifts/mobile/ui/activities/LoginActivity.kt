package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.LoginViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.finish()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Log in"

        webView.loadUrl("https://www.redditgifts.com/profiles/login/")

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if(request.url.toString() == "https://www.redditgifts.com/") {
                    val cookieManager = CookieManager.getInstance()
                    val cookie = cookieManager.getCookie(request.url.toString())
                    viewModel.inputs.didLogin(cookie)
                }
                return true
            }

        }

    }
}
