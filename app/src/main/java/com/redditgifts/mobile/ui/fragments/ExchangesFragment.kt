package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.ActivityRequestCodes
import com.redditgifts.mobile.libs.ActivityRequestCodes.LOGIN_WORKFLOW
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.models.ExchangesViewModel
import com.redditgifts.mobile.ui.activities.GalleryActivity
import com.redditgifts.mobile.ui.activities.LoginActivity
import com.redditgifts.mobile.ui.adapters.GenericAdapter
import com.redditgifts.mobile.ui.views.ExchangeBottomSheet
import com.redditgifts.mobile.ui.views.StatisticsBottomSheet
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_exchanges.*

class ExchangesFragment : BaseFragment<ExchangesViewModel>() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_exchanges, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()

        viewModel.outputs.exchangeOverview()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { data ->
                exchangesLogin.visibility = View.INVISIBLE
                val adapter = exchangesList.adapter as GenericAdapter
                adapter.setItems(data.data[0].exchanges)
            }

        viewModel.outputs.credits()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { credits ->
                exchangesCredits.text = getString(R.string.exchanges_credits).format(credits.data.credits)
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
                        .putExtra(IntentKey.EXCHANGE_ID, exchange.slug)
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
                        viewModel.inputs.didLogin()
                        exchangesLogin.visibility = View.INVISIBLE
                    }
                    else ->
                        exchangesLogin.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadViews() {
        val linearLayoutManager = LinearLayoutManager(context)
        exchangesList.layoutManager = linearLayoutManager
        exchangesList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        exchangesList.adapter = GenericAdapter(this.viewModel.inputs, mutableListOf())
    }

}