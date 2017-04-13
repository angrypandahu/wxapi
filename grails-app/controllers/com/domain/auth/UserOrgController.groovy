package com.domain.auth

import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserOrgController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def userOrgService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserOrg.list(params), model:[userOrgCount: UserOrg.count()]
    }

    def show(UserOrg userOrg) {
        respond userOrg
    }

    def create() {
        respond new UserOrg(params)
    }

    @Transactional
    def save(UserOrg userOrg) {
        if (userOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userOrg.errors, view:'create'
            return
        }

        userOrg.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userOrg.label', default: 'UserOrg'), userOrg.id])
                redirect userOrg
            }
            '*' { respond userOrg, [status: HttpStatus.CREATED] }
        }
    }

    def edit(UserOrg userOrg) {
        respond userOrg
    }

    @Transactional
    def update(UserOrg userOrg) {
        if (userOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userOrg.errors, view:'edit'
            return
        }

        userOrg.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'userOrg.label', default: 'UserOrg'), userOrg.id])
                redirect userOrg
            }
            '*'{ respond userOrg, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(UserOrg userOrg) {

        if (userOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        userOrg.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'userOrg.label', default: 'UserOrg'), userOrg.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: HttpStatus.NO_CONTENT }
        }
    }
    @Transactional
    def ajaxSave() {
        def saveUserApiOrgs = userOrgService.saveUserOrgs(params)
        def ret = new JSONObject()
        ret.put("count", saveUserApiOrgs.size())
        render MyStringUtils.ajaxJSONReturnTrue(ret);

    }
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userOrg.label', default: 'UserOrg'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_FOUND }
        }
    }
}
