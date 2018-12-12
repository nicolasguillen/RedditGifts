package com.redditgifts.mobile.libs.utils

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

fun Picasso.loadUrlIntoImage(imageView: ImageView, urlPath: String){
    load(urlPath).networkPolicy(NetworkPolicy.OFFLINE)
            .into(imageView, object: Callback {
                override fun onSuccess() {}

                override fun onError(e: Exception) {
                    // Try again online if it failed
                    load(urlPath).into(imageView)
                }
            })
}