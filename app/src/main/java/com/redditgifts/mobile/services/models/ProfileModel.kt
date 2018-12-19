package com.redditgifts.mobile.services.models

class ProfileModel(
    val data: Data
) {
    class Data(
        val photoUrl: String,
        val redditUsername: String?,
        val firstName: String,
        val lastName: String,
        val shortBioHtml: String
    )
}