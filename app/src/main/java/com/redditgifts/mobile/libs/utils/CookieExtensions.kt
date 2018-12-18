package com.redditgifts.mobile.libs.utils

fun String.getCookieValue(key: String): String? {
    val temp = this.split(";")
    for (ar1 in temp) {
        if (ar1.contains(key)) {
            val temp1 = ar1.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return temp1[1]
        }
    }
    return null
}