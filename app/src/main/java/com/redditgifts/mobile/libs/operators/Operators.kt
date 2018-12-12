package com.redditgifts.mobile.libs.operators

import com.redditgifts.mobile.libs.LocalizedErrorMessages

class Operators {

    companion object {
        fun <T> genericResult(localizedErrorMessages: LocalizedErrorMessages): GenericResultOperator<T> {
            return GenericResultOperator(localizedErrorMessages)
        }
    }
}
