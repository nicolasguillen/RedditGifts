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
import com.redditgifts.mobile.libs.utils.loadUrlIntoImage
import com.redditgifts.mobile.libs.utils.toSpanned
import com.redditgifts.mobile.models.AccountViewModel
import com.redditgifts.mobile.services.models.ProfileModel
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
                Picasso.get().loadUrlIntoImage(accountImage, model.data.photoUrl)
                accountName.text = this.getUserName(model)
                accountDescription.text = model.data.shortBioHtml.toSpanned()
            }

        viewModel.outputs.didLogout()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

        accountLogout.setOnClickListener {
            viewModel.inputs.didPressLogout()
        }

        viewModel.inputs.onCreate()
    }

    private fun getUserName(model: ProfileModel): String {
        return model.data.redditUsername ?: "${model.data.firstName} ${model.data.lastName}"
    }

}