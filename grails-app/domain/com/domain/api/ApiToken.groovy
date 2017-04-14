package com.domain.api

import com.domain.BaseDomain

class ApiToken extends BaseDomain {
    String accessToken
    Date expiresTime
    static constraints = {

    }
}
