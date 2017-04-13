package com.domain.auth

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class PrivilegeService {

    def serviceMethod() {

    }

    List<Privilege> findAllPrivilegeByUser(Long userId) {
        def hql = "select distinct p from ${Privilege.class.simpleName} as p," +
                " ${RolePrivilege.class.simpleName} as rp," +
                " ${Role.class.simpleName} as r," +
                " ${UserRole.class.simpleName} as ur," +
                " ${User.class.simpleName} as u" +
                " where p.id=rp.privilege and rp.role=r.id and r.id = ur.role and ur.user = u.id and u.id = ? order by p.module  "
        return Privilege.executeQuery(hql, [userId])
    }

    def findZTreeJSON(Long userId) {
        def privileges = findAllPrivilegeByUser(userId)
        def modules = new LinkedHashSet()
        def pids = new LinkedHashSet()
        JSONArray moduleArray = new JSONArray()
        def moduleObj = new JSONObject()
        def pid
        privileges?.each {
            if (!modules.contains(it.module)) {
                moduleObj.put('name', it.module)
                pid = it.id * -1
                moduleObj.put('id', pid)
                moduleObj.put('pId', 0)
                moduleObj.put('open', true)
                modules.add(it.module)
                pids.add(pid)
                moduleArray.put(moduleObj)
            } else {
                pid = pids.last()
            }
            def childObj = new JSONObject()
            childObj.put('name', it.name)
            childObj.put('pId', pid)
            childObj.put('id', it.id)
            childObj.put('url', it.url)
            childObj.put('target', it.code)
            moduleArray.put(childObj)

        }

        return moduleArray

    }
}
