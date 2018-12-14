package com.redditgifts.mobile.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.libs.utils.EndlessRecyclerViewScrollListener
import com.redditgifts.mobile.models.GalleryPageData
import com.redditgifts.mobile.models.GalleryViewModel
import com.redditgifts.mobile.ui.adapters.GalleryAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : BaseActivity<GalleryViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(!(galleryItems.adapter as GalleryAdapter).itemList.isEmpty()) {
                    gallerySwipe.isRefreshing = isLoading
                }
            }

        viewModel.outputs.loadHTML()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { url ->
                this.loadUrl(url)
            }

        viewModel.outputs.galleryPageData()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { galleryPageData ->
                this.displayItems(galleryPageData)
            }

        viewModel.outputs.startGiftDetail()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { giftSelected ->
                startActivity(
                    Intent(this, GiftActivity::class.java)
                        .putExtra(IntentKey.GIFT_ID, giftSelected.referenceId))
            }

        gallerySwipe.setOnRefreshListener {
            viewModel.inputs.loadPage(1)
        }

        viewModel.inputs.exchangeId(intent?.extras?.getString(IntentKey.EXCHANGE_ID)!!)
        viewModel.inputs.onCreate()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        setContentView(R.layout.activity_gallery)
        val title = intent?.extras?.get(IntentKey.EXCHANGE_TITLE)
        when(title) {
            is String? -> supportActionBar?.title = title
            is Int? -> supportActionBar?.title = getString(title!!)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.settings.javaScriptEnabled = true

        val linearLayoutManager = LinearLayoutManager(this)
        galleryItems.layoutManager = linearLayoutManager
        galleryItems.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        galleryItems.adapter = GalleryAdapter(this.viewModel.inputs, mutableListOf())

    }

    private fun loadUrl(url: String) {
        webView.webViewClient = null
        webView.removeJavascriptInterface("HTMLOUT")
        webView.loadUrl("about:blank")
        webView.loadUrl(url)

        val handler = Handler()
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = true
            override fun onPageFinished(view: WebView, url: String) {
                handler.postDelayed({
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                }, 2000)
            }
        }
    }

    private fun displayItems(galleryPageData: GalleryPageData) {
        val adapter = galleryItems.adapter as GalleryAdapter

        if(galleryPageData.page == 1) {
            galleryItems.addOnScrollListener(object : EndlessRecyclerViewScrollListener(galleryItems.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemCount: Int, view: RecyclerView) {
                    viewModel.inputs.loadPage(page)
                }
            })
        }
        adapter.setItems(galleryPageData)
    }

    internal inner class MyJavaScriptInterface {
        @Suppress("unused")
        @android.webkit.JavascriptInterface
        fun processHTML(html: String) {
            viewModel.inputs.didLoadHtml(html)
        }
    }

}