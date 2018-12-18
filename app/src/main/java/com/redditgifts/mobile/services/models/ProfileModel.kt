package com.redditgifts.mobile.services.models

class ProfileModel(
    val data: Data
) {
    class Data(
        val photoUrl: String,
        val redditUsername: String,
        val shortBioHtml: String
    )
}