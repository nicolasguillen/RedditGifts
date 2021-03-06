package com.redditgifts.mobile.ui.activities

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.IntentKey
import com.redditgifts.mobile.libs.utils.toSpanned
import com.redditgifts.mobile.models.GiftViewModel
import com.redditgifts.mobile.ui.adapters.GiftImageAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_gift.*
import kotlinx.android.synthetic.main.cell_loader.*
import java.text.SimpleDateFormat
import java.util.*


class GiftActivity : BaseActivity<GiftViewModel>() {

    private var loaderAnimation: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        loadViews()

        viewModel.outputs.isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { isLoading ->
                if(isLoading){
                    loader.visibility = View.VISIBLE
                    loader.apply {
                        setBackgroundResource(R.drawable.loader)
                        loaderAnimation = background as AnimationDrawable
                    }
                    loaderAnimation?.start()
                } else {
                    loader.visibility = View.GONE
                    loaderAnimation?.stop()
                }
            }

        viewModel.outputs.error()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

        viewModel.outputs.detailedGift()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { gift ->
                giftData.visibility = View.VISIBLE
                giftTitle.text = gift.data.title
                giftPoster.text = getString(R.string.gallery_sender).format(gift.data.postedBy)
                giftUpvotes.text = resources.getQuantityString(R.plurals.likes, gift.data.votes, gift.data.votes)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                giftTime.text = dateFormat.format(gift.data.createdAt)
                giftDescription.text = gift.data.bodyHTML.toSpanned()
                giftImages.adapter = GiftImageAdapter(this, gift.data.assets)
                giftImagesIndicator.setupWithViewPager(giftImages, true)
                if(gift.data.hasVoted){
                    giftUpvoteAction.setImageResource(R.drawable.ic_upvote)
                } else {
                    giftUpvoteAction.setImageResource(R.drawable.ic_vote)
                }
            }

        viewModel.outputs.upvoteData()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { data ->
                if(data.didVote) {
                    giftUpvoteAction.setImageResource(R.drawable.ic_upvote)
                } else {
                    giftUpvoteAction.setImageResource(R.drawable.ic_vote)
                }
                giftUpvotes.text = resources.getQuantityString(R.plurals.likes, data.votes, data.votes)
            }

        giftUpvoteAction.setOnClickListener {
            viewModel.inputs.upvote()
        }

        viewModel.inputs.exchangeId(intent.getStringExtra(IntentKey.EXCHANGE_ID)!!)
        viewModel.inputs.giftSlug(intent.getStringExtra(IntentKey.GIFT_SLUG)!!)
        viewModel.inputs.onCreate()
    }

    private fun loadViews() {
        setContentView(R.layout.activity_gift)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        giftImages.clipToPadding = false
        giftImages.setPadding(50, 0, 50, 0)
        giftImages.pageMargin = 30
    }

}
