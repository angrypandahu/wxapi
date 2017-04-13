package com.domain.api

import com.domain.BaseDomain
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'hierarchy')
@ToString(includes = 'name', includeNames = true, includePackage = false)
class ApiOrg extends BaseDomain {
    String hierarchy
    String parent
    int type
    String url
    String name
    String memo
    String method
    String extraParams
    static constraints = {
        name blank: false
        method blank: false,inList: ['GET','POST']
        extraParams nullable: true
        url blank: false
        hierarchy blank: false, unique: true
        parent nullable: true
        type blank: false
        memo nullable: true

    }
    static mapping = {
        cache true
    }
}
