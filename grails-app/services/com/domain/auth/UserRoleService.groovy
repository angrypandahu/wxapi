package com.domain.auth

import grails.transaction.Transactional

@Transactional
class UserRoleService {

    def saveUserRoles(params) {
        def userId = Long.parseLong(params.user)
        List<Long> roles = new ArrayList<>()
        params.role?.each {
            roles.add(Long.parseLong(it))
        }
        return saveUserRoles(userId, roles)
    }

    def saveUserRoles(Long userId, List<Long> roles) {
        def user = User.get(userId)
        List<Role> listOfRoles = new ArrayList()
        roles?.each {
            def role = Role.get(it)
            listOfRoles.add(role)
        }
        def saves = []
        if (user) {
            listOfRoles?.each {
                if (!UserRole.exists(user.id, it.id)) {
                    saves.add(UserRole.create(user, it))
                }
            }
            if (!listOfRoles) {
                UserRole.where {
                    user == user
                }.deleteAll()
            } else {
                UserRole.where {
                    user == user && !(role in listOfRoles)
                }.deleteAll()
            }

        }
        return saves

    }

    def serviceMethod() {

    }
}
