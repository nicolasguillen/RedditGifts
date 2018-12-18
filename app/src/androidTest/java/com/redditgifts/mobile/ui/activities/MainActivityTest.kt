package com.redditgifts.mobile.ui.activities

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.redditgifts.mobile.R
import com.redditgifts.mobile.utils.DemoModeRule
import com.redditgifts.mobile.utils.LocaleTestRule
import com.redditgifts.mobile.utils.Screengrab
import com.redditgifts.mobile.utils.UiAutomatorScreenshotStrategy
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@Suppress("unused")
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    companion object {

        @get:ClassRule
        @JvmStatic
        val localeTestRule = LocaleTestRule()

        @get:ClassRule
        @JvmStatic
        val demoModeRule = DemoModeRule()

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Screengrab.defaultScreenshotStrategy = UiAutomatorScreenshotStrategy()
        }
    }

    @Test
    fun screengrabCurrentExchanges() {
        SystemClock.sleep(1500)

        Screengrab.screenshot("01")
    }

    @Test
    fun screengrabPastExchanges() {
        onView(ViewMatchers.withId(R.id.bottomBarGallery)).perform(click())

        SystemClock.sleep(1500)

        Screengrab.screenshot("02")
    }

}