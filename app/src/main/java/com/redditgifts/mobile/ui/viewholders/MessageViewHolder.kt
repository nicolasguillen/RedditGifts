package com.redditgifts.mobile.ui.viewholders

import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.redditgifts.mobile.R
import com.redditgifts.mobile.services.models.MessageModel

class MessageViewHolder(view: View,
                        private val delegate: Delegate?) : BaseViewHolder(view) {

    private var post: MessageModel.Data.Message? = null

    override fun bindData(data: Any) {
        post = data as MessageModel.Data.Message

        val messageTitle = view().findViewById<TextView>(R.id.messageTitle)
        messageTitle.text = post?.subject
        val textColor = if(post?.unread!!) R.color.colorAccent else R.color.textColor
        messageTitle.setTextColor(ContextCompat.getColor(context(), textColor))

        val messageSender = view().findViewById<TextView>(R.id.messageSender)
        messageSender.text = context().getString(R.string.messages_sender).format(post?.sender)

        val messageTime = view().findViewById<TextView>(R.id.messageTime)
        val time = post?.createdAt?.time ?: return
        messageTime.text = DateUtils.getRelativeTimeSpanString(time)

    }

    override fun onClick(v: View) {
        delegate?.didSelectMessage(post!!)
    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectMessage(gift: MessageModel.Data.Message)
    }
}