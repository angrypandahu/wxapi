package com.domain.api

import com.domain.auth.User
import com.util.ExtTreePanel
import grails.transaction.Transactional

@Transactional
class UserApiOrgService {

    def serviceMethod() {

    }

    def isAuth(User user, id) {
        if (user && id) {
            def apiOrg = ApiOrg.get(id)
            if (apiOrg) {
                return isAuth(user, apiOrg)
            }
        }
        return false

    }

    def isAuth(User user, ApiOrg apiOrg) {
        def apiOrgs = UserApiOrg.findAllByUser(user)?.apiOrg
        if (apiOrgs) {
            for (ApiOrg one : apiOrgs) {
                def isStartWith = apiOrg.getHierarchy().startsWith(one.getHierarchy())
                if (isStartWith) {
                    return true
                }
            }
        }
        return false
    }

    def getApiOrgTree() {
        ExtTreePanel extTreePanel = new ExtTreePanel();
        extTreePanel.addColumnData()
        return extTreePanel
    }

    def saveUserApiOrgs(def params) {
        def userId = Long.parseLong(params.user)
        List<Long> orgs = new ArrayList<>();
        params.apiOrg?.each {
            orgs.add(Long.parseLong(it))
        }
        return saveUserApiOrgs(userId, orgs)
    }

    def saveUserApiOrgs(Long userId, List<Long> orgs) {
        def user = User.get(userId)
        List<ApiOrg> apiOrgs = new ArrayList()
        orgs?.each {
            def apiOrg = ApiOrg.get(it)
            apiOrgs.add(apiOrg)
        }
        def saves = []
        if (user) {
            apiOrgs?.each {
                if (!UserApiOrg.exists(user.id, it.id)) {
                    saves.add(UserApiOrg.create(user, it))
                }
            }
            if (!apiOrgs) {
                UserApiOrg.where {
                    user == user
                }.deleteAll()
            } else {
                UserApiOrg.where {
                    user == user && !(apiOrg in apiOrgs)
                }.deleteAll()
            }

        }
        return saves

    }
}
