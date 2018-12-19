package com.redditgifts.mobile.services.models

import com.google.gson.annotations.SerializedName

class ErrorEnvelope(
    @SerializedName("detail") val detail: String
)