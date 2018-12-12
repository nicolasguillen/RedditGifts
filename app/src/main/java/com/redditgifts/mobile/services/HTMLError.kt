package com.redditgifts.mobile.services

sealed class HTMLError: Throwable() {
    object NeedsLogin: HTMLError()
    object LoadHTMLError: HTMLError()
}