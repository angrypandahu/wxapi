package com.domain.auth

import grails.transaction.Transactional

@Transactional
class RolePrivilegeService {



    def saveRolePrivileges(params) {
        def roleId = Long.parseLong(params.role)
        List<Long> privileges = new ArrayList<>()
        params.privilege?.each {
            privileges.add(Long.parseLong(it))
        }
        return saveRolePrivileges(roleId, privileges)
    }

    def saveRolePrivileges(Long roleId, List<Long> privileges) {
        def role = Role.get(roleId)
        List<Privilege> listOfPrivileges = new ArrayList()
        privileges?.each {
            def privilege = Privilege.get(it)
            listOfPrivileges.add(privilege)
        }
        def saves = []
        if (role) {
            listOfPrivileges?.each {
                if (!RolePrivilege.exists(role.id, it.id)) {
                    saves.add(RolePrivilege.create(role, it))
                }
            }
            RolePrivilege.where {
                role == role && !(privilege in listOfPrivileges)
            }.deleteAll()
        }
        return saves

    }
    def serviceMethod() {

    }
}
