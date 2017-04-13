package com.domain.api

import com.domain.auth.User
import com.utils.MyStringUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class ApiOrgController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def apiOrgService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ApiOrg.list(params), model: [apiOrgCount: ApiOrg.count()]
    }
    def getApiOrgs() {
        def apiOrgList = UserApiOrg.findAllByUser(User.get(Long.parseLong(params.user)))?.apiOrg
        def ret = new JSONObject()
        ret.put("apiOrgList",new JSONArray(apiOrgList?.id) )
        render MyStringUtils.ajaxJSONReturnTrue(ret);
    }

    def show() {
        def apiOrg = apiOrgService.get(params.id)
        respond apiOrg
    }

    def create() {
        respond new ApiOrg(params)
    }

    def tree() {
        render(apiOrgService.tree())
    }

    @Transactional
    def save(ApiOrg apiOrg) {
        if (apiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiOrg.errors, view: 'create'
            return
        }

        apiOrg.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'apiOrg.label', default: 'ApiOrg'), apiOrg.id])
                redirect apiOrg
            }
            '*' { respond apiOrg, [status: HttpStatus.CREATED] }
        }
    }

    def edit(ApiOrg apiOrg) {
        respond apiOrg
    }

    @Transactional
    def update(ApiOrg apiOrg) {
        if (apiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiOrg.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiOrg.errors, view: 'edit'
            return
        }

        apiOrg.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'apiOrg.label', default: 'ApiOrg'), apiOrg.id])
                redirect apiOrg
            }
            '*' { respond apiOrg, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(ApiOrg apiOrg) {

        if (apiOrg == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }
        apiOrgService.delete(apiOrg)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'apiOrg.label', default: 'ApiOrg'), apiOrg.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'apiOrg.label', default: 'ApiOrg'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
