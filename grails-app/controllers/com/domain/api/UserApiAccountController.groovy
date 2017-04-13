package com.domain.api

import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserApiAccountController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def userApiAccountService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserApiAccount.list(params), model:[userApiAccountCount: UserApiAccount.count()]
    }

    def show(UserApiAccount userApiAccount) {
        respond userApiAccount
    }

    def create() {
        respond new UserApiAccount(params)
    }

    @Transactional
    def save(UserApiAccount userApiAccount) {
        if (userApiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userApiAccount.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userApiAccount.errors, view:'create'
            return
        }

        userApiAccount.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userApiAccount.label', default: 'UserApiAccount'), userApiAccount.id])
                redirect userApiAccount
            }
            '*' { respond userApiAccount, [status: HttpStatus.CREATED] }
        }
    }

    def edit(UserApiAccount userApiAccount) {
        respond userApiAccount
    }

    @Transactional
    def update(UserApiAccount userApiAccount) {
        if (userApiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userApiAccount.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userApiAccount.errors, view:'edit'
            return
        }

        userApiAccount.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'userApiAccount.label', default: 'UserApiAccount'), userApiAccount.id])
                redirect userApiAccount
            }
            '*'{ respond userApiAccount, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(UserApiAccount userApiAccount) {

        if (userApiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        userApiAccount.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'userApiAccount.label', default: 'UserApiAccount'), userApiAccount.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: HttpStatus.NO_CONTENT }
        }
    }

    @Transactional
    def ajaxSave() {
        def saveUserApiOrgs = userApiAccountService.saveUserApiAccounts(params)
        def ret = new JSONObject()
        ret.put("count", saveUserApiOrgs.size())
        render MyStringUtils.ajaxJSONReturnTrue(ret);

    }


    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userApiAccount.label', default: 'UserApiAccount'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_FOUND }
        }
    }
}
