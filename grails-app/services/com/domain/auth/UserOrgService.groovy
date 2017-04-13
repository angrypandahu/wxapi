package com.domain.auth

import grails.transaction.Transactional

@Transactional
class UserOrgService {

    def serviceMethod() {

    }

    def saveUserOrgs(def params) {
        def userId = Long.parseLong(params.user)
        List<Long> orgs = new ArrayList<>();
        params.org?.each {
            orgs.add(Long.parseLong(it))
        }
        return saveUserOrgs(userId, orgs)
    }

    def saveUserOrgs(Long userId, List<Long> orgs) {
        def user = User.get(userId)
        List<Org> listOfOrgs = new ArrayList()
        orgs?.each {
            def org = Org.get(it)
            listOfOrgs.add(org)
        }
        def saves = []
        if (user) {
            listOfOrgs?.each {
                if (!UserOrg.exists(user.id, it.id)) {
                    saves.add(UserOrg.create(user, it))
                }
            }
            if (!listOfOrgs) {
                UserOrg.where {
                    user == user
                }.deleteAll()
            } else {
                UserOrg.where {
                    user == user && !(org in listOfOrgs)
                }.deleteAll()
            }

        }
        return saves

    }
}
