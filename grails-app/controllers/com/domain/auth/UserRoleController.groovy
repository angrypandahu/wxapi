package com.domain.auth

import com.controller.BaseController
import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserRoleController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def userRoleService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserRole.list(params), model: [userRoleCount: UserRole.count()]
    }

    def show(UserRole userRole) {
        respond userRole
    }

    def create() {
        respond new UserRole(params)
    }

    def getUserRoles() {
        def roleList = UserRole.findAllByUser(User.get(Long.parseLong(params.user)))?.role
        def ret = new JSONObject()
        ret.put("roleList", new JSONArray(roleList?.id))
        render MyStringUtils.ajaxJSONReturnTrue(ret);
    }

    @Transactional
    def save(UserRole userRole) {
        if (userRole == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userRole.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userRole.errors, view: 'create'
            return
        }

        userRole.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userRole.label', default: 'UserRole'), userRole.user])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    def edit(UserRole userRole) {
        respond userRole
    }

    @Transactional
    def update(UserRole userRole) {
        if (userRole == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userRole.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userRole.errors, view: 'edit'
            return
        }

        userRole.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'userRole.label', default: 'UserRole'), userRole.id])
                redirect userRole
            }
            '*' { respond userRole, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(UserRole userRole) {

        if (userRole == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        userRole.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'userRole.label', default: 'UserRole'), userRole.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    @Transactional
    def ajaxSave() {
        def saveUserApiOrgs = userRoleService.saveUserRoles(params)
        def ret = new JSONObject()
        ret.put("count", saveUserApiOrgs.size())
        render MyStringUtils.ajaxJSONReturnTrue(ret);

    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRole.label', default: 'UserRole'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
