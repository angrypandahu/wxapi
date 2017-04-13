package com.domain.api

import com.controller.BaseController
import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserApiOrgController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def menuService
    def apiOrgService
    def apiAccountService
    def userApiOrgService

    def auth() {
        def userTable = menuService.getUserTable().toJSON().toString()
        def accountTable = apiAccountService.getTable().toJSON().toString()
        def treePanel = apiOrgService.treePanel().toJSON().toString()
        render(view: 'auth', model: [userTable: userTable, accountTable: accountTable, treePanel: treePanel])
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserApiOrg.list(params), model: [userApiOrgCount: UserApiOrg.count()]
    }


    def show(UserApiOrg userApiOrg) {
        respond userApiOrg
    }

    def create() {
        respond new UserApiOrg(params)
    }


    @Transactional
    def save(UserApiOrg userApiOrg) {
        if (userApiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userApiOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userApiOrg.errors, view: 'create'
            return
        }

        userApiOrg.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userApiOrg.label', default: 'UserApiOrg'), userApiOrg.apiOrg])
                redirect action: "index", method: "GET"
            }
            '*' { respond userApiOrg, [status: HttpStatus.CREATED] }
        }
    }

    def edit(UserApiOrg userApiOrg) {
        respond userApiOrg
    }

    @Transactional
    def update(UserApiOrg userApiOrg) {
        if (userApiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userApiOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userApiOrg.errors, view: 'edit'
            return
        }

        userApiOrg.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'userApiOrg.label', default: 'UserApiOrg'), userApiOrg.apiOrg])
                redirect action: "index", method: "GET"
            }
            '*' { respond userApiOrg, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(UserApiOrg userApiOrg) {

        if (userApiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        userApiOrg.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'userApiOrg.label', default: 'UserApiOrg'), userApiOrg.apiOrg])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    @Transactional
    def ajaxSave() {
        def saveUserApiOrgs = userApiOrgService.saveUserApiOrgs(params)
        def ret = new JSONObject()
        ret.put("count", saveUserApiOrgs.size())
        render MyStringUtils.ajaxJSONReturnTrue(ret);

    }


    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userApiOrg.label', default: 'UserApiOrg'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
