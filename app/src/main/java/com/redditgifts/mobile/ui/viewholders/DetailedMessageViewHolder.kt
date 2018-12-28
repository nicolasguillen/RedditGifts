package com.redditgifts.mobile.ui.viewholders

import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.libs.utils.toSpanned
import com.redditgifts.mobile.services.models.MessageModel

class DetailedMessageViewHolder(view: View) : BaseViewHolder(view) {

    private var post: MessageModel.Data.Message? = null

    override fun bindData(data: Any) {
        post = data as MessageModel.Data.Message

        val messageSender = view().findViewById<TextView>(R.id.messageSender)
        messageSender.text = post?.sender

        val messageTime = view().findViewById<TextView>(R.id.messageTime)
        val time = post?.createdAt?.time ?: return
        messageTime.text = DateUtils.getRelativeTimeSpanString(time)

        val messageBody = view().findViewById<TextView>(R.id.messageBody)
        messageBody.text = post?.messageHTML?.toSpanned()

    }

    override fun onClick(v: View) {  }

}