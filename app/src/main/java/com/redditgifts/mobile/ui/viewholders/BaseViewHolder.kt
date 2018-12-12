package com.redditgifts.mobile.ui.viewholders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(private val view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

    init {
        @Suppress("LeakingThis")
        view.setOnClickListener(this)
    }

    override fun onClick(v: View) {  }

    abstract fun bindData(data: Any)

    protected fun view(): View {
        return view
    }

    protected fun context(): Context {
        return view.context
    }

    interface BaseViewHolderDelegate

}