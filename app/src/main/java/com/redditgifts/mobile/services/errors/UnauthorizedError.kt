package com.redditgifts.mobile.services.errors

import com.redditgifts.mobile.services.models.ErrorEnvelope

class UnauthorizedError(errorEnvelope: ErrorEnvelope): ApiException(errorEnvelope) {

    constructor() : this(ErrorEnvelope(403, "", ""))

}