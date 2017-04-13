package com.domain.auth

import com.domain.BaseDomain
import groovy.transform.ToString

@ToString(includes = 'name', includeNames = true, includePackage = false)
class Privilege extends BaseDomain{
    String name
    String function
    String module
    String code
    String url
    String memo
    static constraints = {
        name blank: false
        function blank: false
        module blank: false
        code nullable: true
        url blank: false
        memo nullable: true
    }
}
