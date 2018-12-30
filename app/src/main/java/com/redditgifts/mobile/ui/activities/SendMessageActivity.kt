package com.redditgifts.mobile.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.utils.onChange
import com.redditgifts.mobile.models.SendMessageViewModel
import com.redditgifts.mobile.ui.views.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_send_message.*

class SendMessageActivity : BaseActivity<SendMessageViewModel>() {

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this, R.string.messages_sending) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isValid()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isValid ->
                sendMessageAction.isEnabled = isValid
            }

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading) loadingDialog.show() else loadingDialog.hide()
            }

        viewModel.outputs.didSendMessage()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                setResult(Activity.RESULT_OK)
                finish()
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        sendMessageTo.onChange { to ->
            viewModel.inputs.messageTo(to)
        }

        sendMessageSubject.onChange { subject ->
            viewModel.inputs.messageSubject(subject)
        }

        sendMessageBody.onChange { body ->
            viewModel.inputs.messageBody(body)
        }

        sendMessageAction.setOnClickListener {
            viewModel.inputs.didPressSend()
        }

        viewModel.inputs.onCreate()
    }

    private fun loadViews() {
        setContentView(R.layout.activity_send_message)

        supportActionBar?.setTitle(R.string.send_message_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

}