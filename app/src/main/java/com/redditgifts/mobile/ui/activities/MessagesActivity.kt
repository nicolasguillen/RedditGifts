package com.redditgifts.mobile.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.utils.EndlessRecyclerViewScrollListener
import com.redditgifts.mobile.models.MessagePageData
import com.redditgifts.mobile.models.MessagesViewModel
import com.redditgifts.mobile.ui.adapters.MessageAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : BaseActivity<MessagesViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(!(messagesItems.adapter as MessageAdapter).isLoading) {
                    messagesSwipe.isRefreshing = isLoading
                }
            }

        viewModel.outputs.messages()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { messageData ->
                this.displayItems(messageData)
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        messagesSwipe.setOnRefreshListener {
            viewModel.inputs.loadPage(1)
        }

        viewModel.inputs.onCreate()
    }

    private fun loadViews() {
        setContentView(R.layout.activity_messages)

        supportActionBar?.setTitle(R.string.messages_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val linearLayoutManager = LinearLayoutManager(this)
        messagesItems.layoutManager = linearLayoutManager
        messagesItems.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        messagesItems.adapter = MessageAdapter(this.viewModel.inputs, mutableListOf())

    }

    private fun displayItems(messagePageData: MessagePageData) {
        val adapter = messagesItems.adapter as MessageAdapter
        if(messagePageData.page == 1) {
            messagesItems.addOnScrollListener(object : EndlessRecyclerViewScrollListener(messagesItems.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemCount: Int, view: RecyclerView) {
                    viewModel.inputs.loadPage(page)
                }
            })
        }
        adapter.setItems(messagePageData)
    }

}