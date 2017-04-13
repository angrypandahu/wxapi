package com.domain.auth

import com.domain.BaseDomain
import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache = true, includeNames = true, includePackage = false)
class RolePrivilege extends BaseDomain {
    Role role
    Privilege privilege

    @Override
    boolean equals(other) {
        if (other instanceof RolePrivilege) {
            other.roleId == role?.id && other.privilegeId == privilege?.id
        } else {
            return false
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (role) builder.append(role.id)
        if (privilege) builder.append(privilege.id)
        builder.toHashCode()
    }

    static RolePrivilege get(long roleId, long privilegeId) {
        criteriaFor(roleId, privilegeId).get()
    }

    static boolean exists(long roleId, long privilegeId) {
        criteriaFor(roleId, privilegeId).count()
    }

    private static DetachedCriteria criteriaFor(long roleId, long privilegeId) {
        where {
            role == Role.load(roleId) &&
                    privilege == Privilege.load(privilegeId)
        }
    }

    static RolePrivilege create(Role role, Privilege privilege) {
        def instance = new RolePrivilege(role: role, privilege: privilege)
        instance.save()
        instance
    }

    static boolean remove(Role u, Privilege o) {
        if (u != null && o != null) {
            where { role == u && privilege == o }.deleteAll()
        } else {
            return false
        }

    }

    static int removeAll(Role u) {
        u == null ? 0 : where { role == u }.deleteAll()
    }

    static int removeAll(Privilege o) {
        o == null ? 0 : where { privilege == o }.deleteAll()
    }


    static constraints = {
        role validator: { Role r, RolePrivilege rp ->
            if (rp.privilege?.id) {
                withNewSession {
                    if (exists(r.id, rp.privilege.id)) {
                        return ['RolePrivilege.exists']
                    }else {
                        return true
                    }
                }
            }

        }
    }
    static mapping = {
        id composite: ['role', 'privilege']
    }
}
