package com.redditgifts.mobile.libs

sealed class GenericResult<T> {
    data class Successful<T>(val result: T): GenericResult<T>()
    data class Failed<T>(val errorMessage: String, val throwable: Throwable): GenericResult<T>()
}