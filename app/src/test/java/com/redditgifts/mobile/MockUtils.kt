package com.redditgifts.mobile

import org.mockito.Mockito
import org.mockito.stubbing.Stubber

fun <T> Stubber.whenever(mock: T): T = `when`(mock)

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}
@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized(): T = null as T