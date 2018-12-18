package com.redditgifts.mobile.services.errors

import com.redditgifts.mobile.services.models.ErrorEnvelope

/**
 * An exception class wrapping an [ErrorEnvelope].
 */
open class ApiException(val errorEnvelope: ErrorEnvelope) : ResponseException()