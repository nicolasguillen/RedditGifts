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

    private var currentExchange: ExchangeOverviewModel.CurrentExchange? = null

    override fun bindData(data: Any) {
        currentExchange = data as ExchangeOverviewModel.CurrentExchange

        val giftTitle = view().findViewById<TextView>(R.id.exchangeTitle)
        giftTitle.text = currentExchange?.title

        Picasso.get().loadUrlIntoImage(
            view().findViewById(R.id.exchangeImage),
            currentExchange?.imageURL!!
        )

        val exchangeStatus = view().findViewById<AppCompatButton>(R.id.exchangeStatus)
        exchangeStatus.setOnClickListener {
            delegate?.didSelectOpenStatus(currentExchange!!)
        }

        val exchangeStatistics = view().findViewById<AppCompatButton>(R.id.exchangeStatistics)
        exchangeStatistics.setOnClickListener {
            delegate?.didSelectOpenStatistics(currentExchange!!)
        }
    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectOpenStatus(currentExchange: ExchangeOverviewModel.CurrentExchange)
        fun didSelectOpenStatistics(currentExchange: ExchangeOverviewModel.CurrentExchange)
    }
}