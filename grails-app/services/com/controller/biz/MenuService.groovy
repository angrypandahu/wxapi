package com.controller.biz

import com.domain.auth.*
import com.util.ExtGrid
import com.utils.Constants
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class MenuService {

    def serviceMethod() {

    }

    List<Privilege> findAllPrivilegeByUser(User user) {
        def hql = "select distinct p from ${Privilege.class.simpleName} as p," +
                " ${RolePrivilege.class.simpleName} as rp," +
                " ${Role.class.simpleName} as r," +
                " ${UserRole.class.simpleName} as ur," +
                " ${User.class.simpleName} as u" +
                " where p.id=rp.privilege and rp.role=r.id and r.id = ur.role and ur.user = u.id and u.id = ? order by p.module  "
        return Privilege.executeQuery(hql, [user.id])

    }

    def findAllMenus(User user) {
        def privileges = findAllPrivilegeByUser(user)
        def modules = new LinkedHashSet()
        JSONArray moduleArray = new JSONArray()
        def moduleObj = new JSONObject()
        def children = new JSONArray()
        privileges?.each {
            moduleObj = new JSONObject()
            children = new JSONArray()
            if (!modules.contains(it.module)) {
                moduleObj.put('text', it.module)
                moduleObj.put('id', it.module)
                moduleObj.put('children', children)
                modules.add(it.module)
                moduleArray.put(moduleObj)
            } else {
                moduleObj = moduleArray.getJSONObject(moduleArray.size() - 1)
                children = moduleObj.getJSONArray('children')
            }
            def childObj = new JSONObject()
            childObj.put('text', it.name)
            childObj.put('id', '')
            childObj.put('hrefTarget', it.url)
            childObj.put('leaf', true)
            children.put(childObj)

        }
        def logout = new JSONObject()
        logout.put("text", '退出')
        logout.put("id", 'logout')
        logout.put("hrefTarget", '/logout')
        logout.put("leaf", true)
        moduleArray.put(logout)
        def root = new JSONObject()
        root.put('text', '.')
        root.put('children', moduleArray)
        return root
    }


    def getUserTable() {
        ExtGrid tableData = new ExtGrid(true, true, "/menu/getUsers", "con_user", "/user/create")
        tableData.addColumnData("user.id.label", Constants.DATATYPE_STRING, 20, false)
        tableData.addColumnData("user.username.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("user.displayName.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("user.email.label", Constants.DATATYPE_STRING, 100, true)
        return tableData
    }

    def getRoleTable() {
        ExtGrid tableData = new ExtGrid(true, true, "/menu/getRoles", "con_role", "/role/create")
        tableData.addColumnData("role.id.label", Constants.DATATYPE_STRING, 30, false)
        tableData.addColumnData("role.authority.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("role.roleName.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("role.description.label", Constants.DATATYPE_STRING, 100, true)
        return tableData
    }

    def getPrivilegeTable() {
        ExtGrid tableData = new ExtGrid(true, true, "/menu/getPrivileges", "con_privilege", "/privilege/create")
        tableData.addColumnData("privilege.id.label", Constants.DATATYPE_STRING, 30, false)
        tableData.addColumnData("privilege.name.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("privilege.module.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("privilege.url.label", Constants.DATATYPE_STRING, 100, true)
        return tableData
    }
}
