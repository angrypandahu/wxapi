package com.controller.biz

import com.controller.BaseController
import com.domain.auth.Privilege
import com.domain.auth.Role
import com.domain.auth.User
import com.util.ExtJsUtil

class MenuController extends BaseController {

    def menuService

    def index() {

    }

    def usermanager() {
        def userTable = menuService.getUserTable().toJSON().toString();
        def roleTable = menuService.getRoleTable().toJSON().toString();
        render(view: 'usermanager', model: [userTable: userTable, roleTable: roleTable])

    }

    def getUsers() {
        def tableData = menuService.getUserTable();
        ExtJsUtil.formatPaging(params)
        tableData.setDatas(User.list(params))
        tableData.setTotalCount(User.count())
        render(tableData.getDatas())
    }

    def getRoles() {
        def tableData = menuService.getRoleTable();
        ExtJsUtil.formatPaging(params)
        tableData.setDatas(Role.list(params))
        tableData.setTotalCount(Role.count())
        render(tableData.getDatas())
    }

    def getPrivileges() {
        def tableData = menuService.getPrivilegeTable();
        ExtJsUtil.formatPaging(params)
        tableData.setDatas(Privilege.list(params))
        tableData.setTotalCount(Privilege.count())
        render(tableData.getDatas())
    }


    def getMenus() {
        User user = getAuthenticatedUser()
        def findAll = menuService.findAllMenus(user)
        render(findAll.toString())
    }

    def getAllOrgs() {

    }

}
