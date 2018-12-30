package com.redditgifts.mobile.ui.views

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.redditgifts.mobile.R
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog(context: Context,
                    @StringRes val text: Int = R.string.main_loading) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_loading)

        loadingText.setText(text)

        setCancelable(false)
    }
}