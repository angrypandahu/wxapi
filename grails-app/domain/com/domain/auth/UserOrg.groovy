package com.domain.auth

import com.domain.BaseDomain
import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache = true, includeNames = true, includePackage = false)
class UserOrg extends BaseDomain{
    User user
    Org org

    @Override
    boolean equals(other) {
        if (other instanceof UserOrg) {
            other.userId == user?.id && other.orgId == org?.id
        } else {
            return false
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (org) builder.append(org.id)
        builder.toHashCode()
    }

    static UserOrg get(long userId, long orgId) {
        criteriaFor(userId, orgId).get()
    }

    static boolean exists(long userId, long roleId) {
        criteriaFor(userId, roleId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long orgId) {
        where {
            user == User.load(userId) &&
                    org == Org.load(orgId)
        }
    }

    static UserOrg create(User user, Org org) {
        def instance = new UserOrg(user: user, org: org)
        instance.save()
        instance
    }

    static boolean remove(User u, Org o) {
        if (u != null && o != null) {
            where { user == u && org == o }.deleteAll()
        } else {
            return false
        }

    }

    static int removeAll(User u) {
        u == null ? 0 : where { user == u }.deleteAll()
    }

    static int removeAll(Org o) {
        o == null ? 0 : where { org == o }.deleteAll()
    }


    static constraints = {
        org validator: { Org o, UserOrg uo ->
            withNewSession {
                if (exists(uo.user.id, o.id)) {
                    return ['UserOrg.exists']
                }
            }

        }
    }
    static mapping = {
        id composite: ['user', 'org']
    }
}
