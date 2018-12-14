package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.models.PastExchangesViewModel
import com.redditgifts.mobile.ui.activities.GalleryActivity
import com.redditgifts.mobile.ui.adapters.GenericAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_past_exchanges.*

class PastExchangesFragment : BaseFragment<PastExchangesViewModel>() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_past_exchanges, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()

        viewModel.outputs.pastExchanges()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { pastExchanges ->
                val adapter = pastExchangesItems.adapter as GenericAdapter
                adapter.setItems(pastExchanges)
            }

        viewModel.outputs.startExchangeGallery()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { exchange ->
                startActivity(
                    Intent(activity, GalleryActivity::class.java)
                        .putExtra(IntentKey.EXCHANGE_ID, exchange.referenceId)
                        .putExtra(IntentKey.EXCHANGE_TITLE, exchange.title))
            }

        viewModel.inputs.onCreate()
    }

    private fun loadViews() {
        val linearLayoutManager = LinearLayoutManager(context)
        pastExchangesItems.layoutManager = linearLayoutManager
        pastExchangesItems.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        pastExchangesItems.adapter = GenericAdapter(this.viewModel.inputs, mutableListOf())
    }

}