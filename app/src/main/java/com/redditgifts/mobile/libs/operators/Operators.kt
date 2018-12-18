package com.redditgifts.mobile.libs.operators

import com.google.gson.Gson
import com.redditgifts.mobile.libs.LocalizedErrorMessages

class Operators {

    companion object {
        fun <T> genericResult(localizedErrorMessages: LocalizedErrorMessages): GenericResultOperator<T> {
            return GenericResultOperator(localizedErrorMessages)
        }

        fun <T> apiError(gson: Gson): ApiErrorOperator<T> {
            return ApiErrorOperator(gson)
        }

    }
}
