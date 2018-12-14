package com.redditgifts.mobile.ui.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.services.models.PastExchangeModel
import com.squareup.picasso.Picasso

class PastExchangeViewHolder(view: View,
                             private val delegate: Delegate?) : BaseViewHolder(view) {

    private lateinit var exchange: PastExchangeModel

    override fun bindData(data: Any) {
        exchange = data as PastExchangeModel

        val giftTitle = view().findViewById<TextView>(R.id.pastExchangeTitle)
        giftTitle.setText(exchange.title)

        Picasso.get()
            .load(exchange.logo)
            .into(view().findViewById<ImageView>(R.id.pastExchangeLogo))
    }

    override fun onClick(v: View) {
        delegate?.didSelectExchange(exchange)
    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectExchange(exchange: PastExchangeModel)
    }
}