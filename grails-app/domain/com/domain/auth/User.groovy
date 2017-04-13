package com.domain.auth

import com.domain.BaseDomain
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'displayName', includeNames = false, includePackage = false)
class User extends BaseDomain{


    transient springSecurityService

    String username
    String displayName
    String password
    String email
    String phone
    Boolean sex
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired


    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this)*.role
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        displayName blank: false
        email email: true,nullable: true
        phone nullable: true
        enabled nullable: true
        accountExpired nullable: true
        accountLocked nullable: true
        passwordExpired nullable: true
        password blank: false, password: true
        sex nullable: true

    }
    static mapping = {
        password column: '`password`'
    }
}
