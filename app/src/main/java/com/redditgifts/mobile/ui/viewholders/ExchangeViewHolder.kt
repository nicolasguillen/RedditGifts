package com.redditgifts.mobile.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.redditgifts.mobile.R
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.services.models.CurrentExchangeModel
import com.squareup.picasso.Picasso

class ExchangeViewHolder(view: View,
                         private val delegate: Delegate?) : BaseViewHolder(view) {

    private var currentExchange: CurrentExchangeModel.Data.Exchange? = null

    init {
        initToolbar()
    }

    override fun bindData(data: Any) {
        currentExchange = data as CurrentExchangeModel.Data.Exchange

        val giftTitle = view().findViewById<TextView>(R.id.exchangeTitle)
        giftTitle.text = currentExchange?.title

        Picasso.get().loadUrlIntoImage(
            view().findViewById(R.id.exchangeImage),
            currentExchange?.logoImageUrl!!
        )

        val exchangeStatus = view().findViewById<AppCompatButton>(R.id.exchangeStatus)
        exchangeStatus.setOnClickListener {
            delegate?.didSelectOpenStatus(currentExchange!!)
        }

    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectOpenStatus(currentExchange: CurrentExchangeModel.Data.Exchange)
        fun didSelectOpenStatistics(currentExchange: CurrentExchangeModel.Data.Exchange)
        fun didSelectOpenGallery(currentExchange: CurrentExchangeModel.Data.Exchange)
    }

    private fun initToolbar(){
        val menu = view().findViewById<Toolbar>(R.id.exchangeMenu)
        menu.inflateMenu(R.menu.menu_exchange)
        menu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.exchangeStatistics ->
                    delegate?.didSelectOpenStatistics(currentExchange!!)
                R.id.exchangeGallery ->
                    delegate?.didSelectOpenGallery(currentExchange!!)
            }
            false
        }
    }
}