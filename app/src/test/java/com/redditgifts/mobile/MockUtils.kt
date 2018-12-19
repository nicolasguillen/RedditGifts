@file:Suppress("unused", "UNUSED_PARAMETER")

package com.redditgifts.mobile

import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.stubbing.Stubber
import kotlin.reflect.KClass

fun <T> Stubber.whenever(mock: T): T = `when`(mock)

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}
@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized(): T = null as T
/**
 * Creates a [KArgumentCaptor] for given type.
 */
inline fun <reified T : Any> argumentCaptor(): KArgumentCaptor<T> {
    return KArgumentCaptor(ArgumentCaptor.forClass(T::class.java), T::class)
}

/**
 * Creates a [KArgumentCaptor] for given type, taking in a lambda to allow fast verification.
 */
inline fun <reified T : Any> argumentCaptor(f: KArgumentCaptor<T>.() -> Unit): KArgumentCaptor<T> {
    return argumentCaptor<T>().apply(f)
}

/**
 * Creates a [KArgumentCaptor] for given nullable type.
 */
inline fun <reified T : Any> nullableArgumentCaptor(): KArgumentCaptor<T?> {
    return KArgumentCaptor(ArgumentCaptor.forClass(T::class.java), T::class)
}

/**
 * Creates a [KArgumentCaptor] for given nullable type, taking in a lambda to allow fast verification.
 */
inline fun <reified T : Any> nullableArgumentCaptor(f: KArgumentCaptor<T?>.() -> Unit): KArgumentCaptor<T?> {
    return nullableArgumentCaptor<T>().apply(f)
}

/**
 * Alias for [ArgumentCaptor.capture].
 */
inline fun <reified T : Any> capture(captor: ArgumentCaptor<T>): T {
    return captor.capture() ?: createInstance()
}

class KArgumentCaptor<out T : Any?>(
    private val captor: ArgumentCaptor<T>,
    private val tClass: KClass<*>
) {

    val firstValue: T
        get() = captor.firstValue

    @Suppress("UNCHECKED_CAST")
    fun capture(): T {
        return captor.capture() ?: createInstance(tClass) as T
    }
}

val <T> ArgumentCaptor<T>.firstValue: T
    get() = allValues[0]

val <T> ArgumentCaptor<T>.secondValue: T
    get() = allValues[1]

val <T> ArgumentCaptor<T>.thirdValue: T
    get() = allValues[2]

val <T> ArgumentCaptor<T>.lastValue: T
    get() = allValues.last()


inline fun <reified T : Any> createInstance(): T {
    return createInstance(T::class)
}

fun <T : Any> createInstance(kClass: KClass<T>): T {
    return castNull()
}

@Suppress("UNCHECKED_CAST")
private fun <T> castNull(): T = null as T