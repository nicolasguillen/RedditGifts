package com.redditgifts.mobile.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.redditgifts.mobile.R
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
import com.squareup.picasso.Picasso

class ExchangeViewHolder(view: View,
                         private val delegate: Delegate?) : BaseViewHolder(view) {

    private var exchange: ExchangeOverviewModel.Exchange? = null

    override fun bindData(data: Any) {
        exchange = data as ExchangeOverviewModel.Exchange

        val giftTitle = view().findViewById<TextView>(R.id.exchangeTitle)
        giftTitle.text = exchange?.title

        Picasso.get().loadUrlIntoImage(
            view().findViewById(R.id.exchangeImage),
            exchange?.imageURL!!
        )

        val exchangeStatus = view().findViewById<AppCompatButton>(R.id.exchangeStatus)
        exchangeStatus.setOnClickListener {
            delegate?.didSelectOpenStatus(exchange!!)
        }

        val exchangeStatistics = view().findViewById<AppCompatButton>(R.id.exchangeStatistics)
        exchangeStatistics.setOnClickListener {
            delegate?.didSelectOpenStatistics(exchange!!)
        }
    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectOpenStatus(exchange: ExchangeOverviewModel.Exchange)
        fun didSelectOpenStatistics(exchange: ExchangeOverviewModel.Exchange)
    }
}