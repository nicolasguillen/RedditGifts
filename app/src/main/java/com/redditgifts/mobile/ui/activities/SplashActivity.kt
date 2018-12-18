package com.redditgifts.mobile.ui.activities

import android.content.Intent
import android.os.Bundle
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.models.SplashViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

class SplashActivity: BaseActivity<SplashViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        viewModel.outputs.startLogin()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        viewModel.outputs.startMainActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        viewModel.inputs.onCreate()
    }

}