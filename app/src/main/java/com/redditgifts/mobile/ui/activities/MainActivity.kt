package com.redditgifts.mobile.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.redditgifts.mobile.R
import com.redditgifts.mobile.ui.fragments.AccountFragment
import com.redditgifts.mobile.ui.fragments.GalleryFragment
import com.redditgifts.mobile.ui.fragments.ExchangesFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity: AppCompatActivity() {

    private val exchangesFragment = ExchangesFragment()
    private val galleryFragment = GalleryFragment()
    private val accountFragment = AccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mainBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.bottomBarExchanges ->
                    changeFragment(exchangesFragment)
                R.id.bottomBarGallery ->
                    changeFragment(galleryFragment)
//                R.id.bottomBarAccount ->
//                    changeFragment(accountFragment)
            }
            true
        }
        mainBottomNavigation.selectedItemId = R.id.bottomBarExchanges
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
                fragmentTransaction.hideIfNeeded(galleryFragment)
                fragmentTransaction.hideIfNeeded(accountFragment)
            }
            is GalleryFragment -> {
                fragmentTransaction.hideIfNeeded(exchangesFragment)
                fragmentTransaction.hideIfNeeded(accountFragment)
            }
            is AccountFragment -> {
                fragmentTransaction.hideIfNeeded(exchangesFragment)
                fragmentTransaction.hideIfNeeded(galleryFragment)
            }
        }
        fragmentTransaction.commit()
    }

    private fun FragmentTransaction.hideIfNeeded(fragment: Fragment) {
        if(fragment.isAdded) {
            hide(fragment)
        }
    }

}