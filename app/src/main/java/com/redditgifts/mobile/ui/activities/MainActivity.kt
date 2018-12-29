package com.redditgifts.mobile.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.redditgifts.mobile.R
import com.redditgifts.mobile.RedditGiftsApp
import com.redditgifts.mobile.libs.ActivityRequestCodes
import com.redditgifts.mobile.models.MainViewModel
import com.redditgifts.mobile.ui.fragments.AccountFragment
import com.redditgifts.mobile.ui.fragments.ExchangesFragment
import com.redditgifts.mobile.ui.fragments.PastExchangesFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_messages.*


class MainActivity: BaseActivity<MainViewModel>() {

    val exchangesFragment = ExchangesFragment()
    private val pastExchangesFragment = PastExchangesFragment()
    private val accountFragment = AccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RedditGiftsApp.applicationComponent.inject(this)

        setContentView(R.layout.activity_main)

        mainBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.bottomBarExchanges ->
                    changeFragment(exchangesFragment)
                R.id.bottomBarGallery ->
                    changeFragment(pastExchangesFragment)
                R.id.bottomBarAccount ->
                    changeFragment(accountFragment)
            }
            true
        }
        mainBottomNavigation.selectedItemId = R.id.bottomBarExchanges

        mainMessage.setOnClickListener {
            startActivityForResult(Intent(this, MessagesActivity::class.java),
                ActivityRequestCodes.READ_MESSAGE)
        }

        viewModel.outputs.amountOfUnreadMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .crashingSubscribe { amount ->
                if(amount > 0) {
                    mainMessageCounter.visibility = View.VISIBLE
                    mainMessageCounter.text = amount.toString()
                } else {
                    mainMessageCounter.visibility = View.INVISIBLE
                }
            }

        viewModel.inputs.onCreate()
    }

    private fun changeFragment(to: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if(to.isAdded) {
            fragmentTransaction.show(to)
        } else {
            fragmentTransaction.add(R.id.mainContent, to)
        }

        when(to) {
            is ExchangesFragment -> {
                fragmentTransaction.hideIfNeeded(pastExchangesFragment)
                fragmentTransaction.hideIfNeeded(accountFragment)
            }
            is PastExchangesFragment -> {
                fragmentTransaction.hideIfNeeded(exchangesFragment)
                fragmentTransaction.hideIfNeeded(accountFragment)
            }
            is AccountFragment -> {
                fragmentTransaction.hideIfNeeded(exchangesFragment)
                fragmentTransaction.hideIfNeeded(pastExchangesFragment)
            }
        }
        fragmentTransaction.commit()
    }

    private fun FragmentTransaction.hideIfNeeded(fragment: Fragment) {
        if(fragment.isAdded) {
            hide(fragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            ActivityRequestCodes.READ_MESSAGE -> {
                viewModel.inputs.onCreate()
            }
        }
    }

}