package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.LoginViewModel2
import kotlinx.android.synthetic.main.activity_login2.*


class LoginActivity2 : BaseActivity<LoginViewModel2>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.activity_login2)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loginButton.setOnClickListener {
            viewModel.inputs.didPressLogin()
        }

        webView.loadUrl("https://www.redditgifts.com/merchant/api-auth/login/")
//        webView.loadUrl("https://www.redditgifts.com/exchanges/#/select/my-current/")

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val cookieManager = CookieManager.getInstance()
                val cookie = cookieManager.getCookie(url)
                viewModel.inputs.cookie(cookie)
            }

        }
    }
}
