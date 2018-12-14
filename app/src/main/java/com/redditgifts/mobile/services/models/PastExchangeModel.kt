package com.redditgifts.mobile.services.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class PastExchangeModel(
    val referenceId: String,
    @StringRes val title: Int,
    @DrawableRes val logo: Int
)
