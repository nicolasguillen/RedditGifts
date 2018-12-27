package com.redditgifts.mobile.services.models

import java.util.*

class MessageModel(
    val data: Data
) {
    class Data(
        val total_number_messages: Int,
        val messages: List<Message>
    ) {
        class Message(
            val id: Int,
            val subject: String,
            val createdAt: Date,
            val sender: String
        )
    }
}