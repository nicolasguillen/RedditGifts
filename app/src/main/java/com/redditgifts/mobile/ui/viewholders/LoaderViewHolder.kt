package com.redditgifts.mobile.ui.viewholders

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import com.redditgifts.mobile.R

class LoaderViewHolder(view: View) : BaseViewHolder(view) {

    private lateinit var loaderAnimation: AnimationDrawable

    override fun bindData(data: Any) {
        view().findViewById<ImageView>(R.id.loader).apply {
            setBackgroundResource(R.drawable.loader)
            loaderAnimation = background as AnimationDrawable
        }
        loaderAnimation.start()
    }
}