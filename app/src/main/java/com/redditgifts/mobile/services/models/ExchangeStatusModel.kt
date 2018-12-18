package com.redditgifts.mobile.services.models

import java.util.*

class ExchangeStatusModel(
    val data: Data
) {
    class Data(
        val asSanta: Info,
        val asGiftee: Info,
        val lastRetrieved: Date
    ) {
        class Info(
            val currentStep: Int,
            val hasShipped: Boolean,
            val shipments: List<Shipment>,
            val todoList: List<TodoList>
        ) {
            class Shipment(val via: String, val tracking: String)
            class TodoList(val step: Int, val title: String)
        }
    }
}