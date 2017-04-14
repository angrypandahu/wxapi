package com.domain.api

import com.domain.BaseDomain
import groovy.transform.ToString

@ToString(includes = 'name', includeNames = true, includePackage = false)
class ApiAccount extends BaseDomain {
    String name
    String type
    String appId
    String secret
    String description
    ApiToken apiToken
    static constraints = {
        name blank: false
        type blank: false, inList: ["微信公众号"]
        appId blank: true
        secret blank: true
        description nullable: true
        apiToken nullable: true
    }
}
