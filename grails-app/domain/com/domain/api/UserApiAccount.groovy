package com.domain.api

import com.domain.BaseDomain
import com.domain.auth.User
import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache = true, includeNames = true, includePackage = false)
class UserApiAccount extends BaseDomain {
    User user
    ApiAccount apiAccount

    @Override
    boolean equals(other) {
        if (other instanceof UserApiAccount) {
            other.userId == user?.id && other.apiAccountId == apiAccount?.id
        } else {
            return false
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (apiAccount) builder.append(apiAccount.id)
        builder.toHashCode()
    }

    static UserApiAccount get(long userId, long apiAccountId) {
        criteriaFor(userId, apiAccountId).get()
    }

    static boolean exists(long userId, long apiAccountId) {
        criteriaFor(userId, apiAccountId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long apiAccountId) {
       UserApiAccount.where {
            user == User.load(userId) &&
                    apiAccount == ApiAccount.load(apiAccountId)
        }
    }

    static UserApiAccount create(User user, ApiAccount apiAccount) {
        def instance = new UserApiAccount()
        instance.setUser(user)
        instance.setApiAccount(apiAccount)
        instance.save(flush: true)
        instance
    }

    static boolean remove(User u, ApiAccount o) {
        if (u != null && o != null) {
            UserApiAccount.where { user == u && apiAccount == o }.deleteAll()
        } else {
            return false
        }

    }

    static int removeAll(User u) {
        u == null ? 0 : UserApiAccount.where { user == u }.deleteAll()
    }

    static int removeAll(ApiAccount o) {
        o == null ? 0 : UserApiAccount.where { apiAccount == o }.deleteAll()
    }


    static constraints = {
        apiAccount validator: { ApiAccount o, UserApiAccount uo ->
            withNewSession {
                if (exists(uo.user.id, o.id)) {
                    return ['UserApiAccount.exists']
                }
            }

        }
    }
    static mapping = {
        id composite: ['user', 'apiAccount']
    }
}
