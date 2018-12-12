package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.models.GiftViewModel
import com.redditgifts.mobile.ui.adapters.GiftImageAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_gift.*
import kotlinx.android.synthetic.main.cell_loader.*


class GiftActivity : BaseActivity<GiftViewModel>() {

    private var loaderAnimation: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

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
                webView.loadUrl(url)
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        viewModel.outputs.detailedGift()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { gift ->
                giftTitle.text = gift.title
                giftUpvotes.text = "+${gift.upvotes}"
                giftTime.text = gift.timeAndSource
                giftDescription.text = gift.description
                giftImages.adapter = GiftImageAdapter(this, gift.images)
            }

        viewModel.inputs.giftId(intent.getStringExtra(IntentKey.GIFT_ID)!!)
        viewModel.inputs.onCreate()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.activity_gift)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            }
        }

        giftImages.clipToPadding = false
        giftImages.setPadding(50, 0, 50, 0)
        giftImages.pageMargin = 30
    }

    internal inner class MyJavaScriptInterface {
        @Suppress("unused")
        @android.webkit.JavascriptInterface
        fun processHTML(html: String) {
            viewModel.inputs.didLoadHtml(html)
        }
    }
}
