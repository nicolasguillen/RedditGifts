package com.redditgifts.mobile.ui.viewholders

import android.view.View
import android.widget.TextView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.services.models.GalleryModel
import com.squareup.picasso.Picasso

class GalleryViewHolder(view: View,
                        private val delegate: Delegate?) : BaseViewHolder(view) {

    private var post: GalleryModel.Data.GiftModel? = null

    override fun bindData(data: Any) {
        post = data as GalleryModel.Data.GiftModel

        val giftTitle = view().findViewById<TextView>(R.id.giftTitle)
        giftTitle.text = post?.title

        Picasso.get().loadUrlIntoImage(
            view().findViewById(R.id.giftImage),
            post?.thumbnailUrl!!
        )
    }

    override fun onClick(v: View) {
        delegate?.didSelectGift(post!!)
    }

    interface Delegate : BaseViewHolderDelegate {
        fun didSelectGift(gift: GalleryModel.Data.GiftModel)
    }
}