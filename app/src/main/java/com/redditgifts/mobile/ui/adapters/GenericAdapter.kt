package com.redditgifts.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.services.models.ExchangeOverviewModel
import com.redditgifts.mobile.services.models.PastExchangeModel
import com.redditgifts.mobile.ui.viewholders.*

class GenericAdapter(private val delegate: BaseViewHolder.BaseViewHolderDelegate?,
                     private val itemList: List<Any>): RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, layout: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(layout, viewGroup, false)
        return viewHolder(layout, view)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val data = objectFromPosition(position)
        viewHolder.bindData(data)
    }

    override fun getItemViewType(position: Int): Int {
        return layout()
    }

    private fun layout(): Int {
        if(this.itemList.isEmpty()){
            return R.layout.cell_loader
        }
        return when(this.itemList[0]){
            is ExchangeOverviewModel.CurrentExchange ->
                R.layout.cell_exchange
            is PastExchangeModel ->
                R.layout.cell_past_exchange
            else -> R.layout.cell_loader
        }
    }

    private fun viewHolder(@LayoutRes layout: Int, view: View): BaseViewHolder {
        return when (layout) {
            R.layout.cell_exchange ->
                ExchangeViewHolder(view, delegate as ExchangeViewHolder.Delegate)
            R.layout.cell_past_exchange ->
                PastExchangeViewHolder(view, delegate as PastExchangeViewHolder.Delegate)
            R.layout.cell_loader ->
                LoaderViewHolder(view)
            else ->
                EmptyViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return when {
            this.itemList.isEmpty() ->
                1
            else ->
                this.itemList.size
        }
    }

    private fun objectFromPosition(position: Int): Any {
        return if(this.itemList.isEmpty()) {
            false
        } else {
            this.itemList[position]
        }
    }

}