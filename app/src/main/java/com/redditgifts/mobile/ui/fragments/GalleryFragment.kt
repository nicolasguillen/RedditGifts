package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.redditgifts.mobile.ui.activities.GiftActivity
import com.redditgifts.mobile.ui.adapters.GalleryAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : BaseFragment<GalleryViewModel>() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_gallery, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                    Intent(activity, GiftActivity::class.java)
                        .putExtra(IntentKey.GIFT_ID, giftSelected.referenceId))
            }

        gallerySwipe.setOnRefreshListener {
            viewModel.inputs.loadPage(1)
        }

        viewModel.inputs.onCreate()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadViews() {
        webView.settings.javaScriptEnabled = true

        val linearLayoutManager = LinearLayoutManager(context)
        galleryItems.layoutManager = linearLayoutManager
        galleryItems.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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
                if(!isAdded) return
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