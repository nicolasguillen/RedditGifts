package com.redditgifts.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.ActivityRequestCodes
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.libs.utils.toSpanned
import com.redditgifts.mobile.models.AccountViewModel
import com.redditgifts.mobile.ui.activities.LoginActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.cell_loader.*
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseFragment<AccountViewModel>() {

    private var loaderAnimation: AnimationDrawable? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RedditGiftsApp.applicationComponent.inject(this)
        return inflater.inflate(R.layout.fragment_account, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        viewModel.outputs.profileModel()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { model ->
                accountData.visibility = View.VISIBLE
                accountLogin.visibility = View.GONE
                Picasso.get().loadUrlIntoImage(accountImage, model.data.photoUrl)
                accountName.text = model.data.redditUsername
                accountDescription.text = model.data.shortBioHtml.toSpanned()
            }

        accountLogin.setOnClickListener {
            startActivityForResult(Intent(context, LoginActivity::class.java), ActivityRequestCodes.LOGIN_WORKFLOW)
        }

        viewModel.inputs.onCreate()
    }

}