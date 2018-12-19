package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.utils.onChange
import com.redditgifts.mobile.models.LoginViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.cell_loader.*


class LoginActivity : BaseActivity<LoginViewModel>() {

    private var loaderAnimation: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isFormValid()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isValid ->
                loginButton.isEnabled = isValid
            }

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading){
                    (loader.parent as View).visibility = View.VISIBLE
                    loader.apply {
                        setBackgroundResource(R.drawable.loader)
                        loaderAnimation = background as AnimationDrawable
                    }
                    loaderAnimation?.start()
                } else {
                    (loader.parent as View).visibility = View.GONE
                    loaderAnimation?.stop()
                }
            }

        viewModel.outputs.finish()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        loginEmail.onChange { email ->
            viewModel.inputs.email(email)
        }

        loginPassword.onChange { password ->
            viewModel.inputs.password(password)
        }

        loginButton.setOnClickListener {
            this.hideKeyboard()
            viewModel.inputs.didPressLogin()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.activity_login)

        webView.loadUrl("https://www.redditgifts.com/merchant/api-auth/login/?next=/api/v1/")
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

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
