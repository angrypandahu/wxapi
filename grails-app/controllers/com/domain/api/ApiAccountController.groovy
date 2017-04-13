package com.domain.api

import com.controller.BaseController
import com.domain.auth.User
import com.util.ExtJsUtil
import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class ApiAccountController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def apiAccountService
    def index(Integer max) {
        if (params.isJson) {
            def table = apiAccountService.getTable()
            ExtJsUtil.formatPaging(params)
            table.setDatas(ApiAccount.list(params))
            table.setTotalCount(ApiAccount.count())
            render(table.getDatas())
        } else {
            params.max = Math.min(max ?: 10, 100)
            respond ApiAccount.list(params), model: [apiAccountCount: ApiAccount.count()]
        }

    }

    def getApiAccounts() {
        def apiAccountList = UserApiAccount.findAllByUser(User.get(Long.parseLong(params.user)))?.apiAccount
        def ret = new JSONObject()
        ret.put("apiAccountList",new JSONArray(apiAccountList?.id) )
        render MyStringUtils.ajaxJSONReturnTrue(ret);
    }

    def show(ApiAccount apiAccount) {
        respond apiAccount
    }

    def create() {
        respond new ApiAccount(params)
    }

    @Transactional
    def save(ApiAccount apiAccount) {
        if (apiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiAccount.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiAccount.errors, view: 'create'
            return
        }

        apiAccount.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'apiAccount.label', default: 'ApiAccount'), apiAccount.id])
                redirect apiAccount
            }
            '*' { respond apiAccount, [status: HttpStatus.CREATED] }
        }
    }

    def edit(ApiAccount apiAccount) {
        respond apiAccount
    }

    @Transactional
    def update(ApiAccount apiAccount) {
        if (apiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiAccount.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiAccount.errors, view: 'edit'
            return
        }

        apiAccount.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'apiAccount.label', default: 'ApiAccount'), apiAccount.id])
                redirect apiAccount
            }
            '*' { respond apiAccount, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(ApiAccount apiAccount) {

        if (apiAccount == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        apiAccount.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'apiAccount.label', default: 'ApiAccount'), apiAccount.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'apiAccount.label', default: 'ApiAccount'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
