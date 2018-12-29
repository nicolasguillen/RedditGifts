package com.redditgifts.mobile.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.models.DetailedMessagesViewModel
import com.redditgifts.mobile.ui.adapters.GenericAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_detailed_messages.*

class DetailedMessagesActivity : BaseActivity<DetailedMessagesViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.messages()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { messageData ->
                val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
                messagesItems.addItemDecoration(divider)
                val adapter = messagesItems.adapter as GenericAdapter
                adapter.setItems(messageData)
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        viewModel.inputs.messageId(intent.getIntExtra(IntentKey.MESSAGE_ID, 0))
        viewModel.inputs.onCreate()
    }

    private fun loadViews() {
        setContentView(R.layout.activity_detailed_messages)

        supportActionBar?.title = intent.getStringExtra(IntentKey.MESSAGE_TITLE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val linearLayoutManager = LinearLayoutManager(this)
        messagesItems.layoutManager = linearLayoutManager
        messagesItems.adapter = GenericAdapter(this.viewModel.inputs, mutableListOf())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
    }

}