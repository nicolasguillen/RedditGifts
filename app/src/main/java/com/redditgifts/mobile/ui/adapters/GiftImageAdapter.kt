package com.redditgifts.mobile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.redditgifts.mobile.R
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.squareup.picasso.Picasso


class GiftImageAdapter(private val context: Context,
                       private val assets: List<DetailedGiftModel.Data.Assets>) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(context).inflate(R.layout.cell_gift_image, collection, false)
        val imageView = layout.findViewById<ImageView>(R.id.giftImage)
        Picasso.get().loadUrlIntoImage(imageView, assets[position].largeImageUrl)
        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return assets.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

}