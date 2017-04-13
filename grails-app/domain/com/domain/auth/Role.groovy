package com.domain.auth

import com.domain.BaseDomain
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'roleName', includeNames = true, includePackage = false)
class Role extends BaseDomain{


    String authority
    String roleName
    String description
    static constraints = {
        authority blank: false, unique: true
        roleName nullable: true
        description nullable: true
    }

    static mapping = {
        cache true
    }
}
