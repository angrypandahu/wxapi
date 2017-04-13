package com.domain.api

import com.domain.BaseDomain
import com.domain.auth.User
import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache = true, includeNames = true, includePackage = false)
class UserApiOrg extends BaseDomain {
    User user
    ApiOrg apiOrg

    @Override
    boolean equals(other) {
        if (other instanceof UserApiOrg) {
            other.userId == user?.id && other.apiOrgId == apiOrg?.id
        } else {
            return false
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (apiOrg) builder.append(apiOrg.id)
        builder.toHashCode()
    }

    static UserApiOrg get(long userId, long apiOrgId) {
        criteriaFor(userId, apiOrgId).get()
    }

    static boolean exists(long userId, long apiOrgId) {
        criteriaFor(userId, apiOrgId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long apiOrgId) {
        where {
            user == User.load(userId) &&
                    apiOrg == ApiOrg.load(apiOrgId)
        }
    }

    static UserApiOrg create(User user, ApiOrg apiOrg) {
        def instance = new UserApiOrg(user: user, apiOrg: apiOrg)
        instance.save(flush: true)
        instance
    }

    static boolean remove(User u, ApiOrg o) {
        if (u != null && o != null) {
            UserApiOrg.where { user == u && apiOrg == o }.deleteAll()
        } else {
            return false
        }

    }

    static int removeAll(User u) {
        u == null ? 0 : UserApiOrg.where { user == u }.deleteAll()
    }

    static int removeAll(ApiOrg o) {
        o == null ? 0 : UserApiOrg.where { apiOrg == o }.deleteAll()
    }


    static constraints = {
        apiOrg validator: { ApiOrg o, UserApiOrg uo ->
            withNewSession {
                if (exists(uo.user.id, o.id)) {
                    return ['UserApiOrg.exists']
                }
            }

        }
    }
    static mapping = {
        id composite: ['user', 'apiOrg']
    }
}
