package com.redditgifts.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import com.redditgifts.mobile.ui.viewholders.EmptyViewHolder
import com.redditgifts.mobile.ui.viewholders.ExchangeViewHolder
import com.redditgifts.mobile.ui.viewholders.LoaderViewHolder

class ExchangeAdapter(private val delegate: BaseViewHolder.BaseViewHolderDelegate?,
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
        return R.layout.cell_exchange
    }

    private fun viewHolder(@LayoutRes layout: Int, view: View): BaseViewHolder {
        return when (layout) {
            R.layout.cell_exchange ->
                ExchangeViewHolder(view, delegate as ExchangeViewHolder.Delegate)
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