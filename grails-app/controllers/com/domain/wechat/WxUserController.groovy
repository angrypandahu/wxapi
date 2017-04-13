package com.domain.wechat

import com.domain.auth.User
import grails.transaction.Transactional
import org.grails.plugins.filterpane.FilterPaneUtils
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class WxUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def filterPane7Service
    def wxUserService
    def userApiAccountService
    def filter = {
        if (!params.max) params.max = 10
        if (!authApiAccount()) {
            return notFound()
        }
        render(view: 'index',
                model: [wxUserList  : filterPane7Service.filter(params, WxUser.class),
                        wxUserCount : filterPane7Service.count(params, WxUser.class),
                        wxGroupList : WxGroup.list(),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params      : params])

    }

    def authApiAccount() {
        params.'filter.op.apiAccount.name' = "Equal"
        String apiAccountName = params.'filter.apiAccount.name'
        User user = getAuthenticatedUser() as User
        if (apiAccountName) {
            return userApiAccountService.isAuthByName(user, apiAccountName)
        } else {
            def accounts = userApiAccountService.getApiAccounts(user)
            if (accounts && accounts.size() > 0) {
                params.'filter.apiAccount.name' = accounts.get(0).name
                return true
            }
        }
        return false

    }

    def index() {
        filter()
    }

    def sendMsg() {
        def id = params.id
        WxUser wxUser = WxUser.get(id)
        String msg = params.msg as String
        render(wxUserService.customSend(wxUser, msg))

    }

    def show(WxUser wxUser) {
        respond wxUser
    }

    def create() {
        respond new WxUser(params)
    }

    @Transactional
    def save(WxUser wxUser) {
        if (wxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (wxUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond wxUser.errors, view: 'create'
            return
        }

        wxUser.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'wxUser.label', default: 'WxUser'), wxUser.id])
                redirect wxUser
            }
            '*' { respond wxUser, [status: HttpStatus.CREATED] }
        }
    }

    def edit(WxUser wxUser) {
        respond wxUser
    }

    @Transactional
    def update(WxUser wxUser) {
        if (wxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (wxUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond wxUser.errors, view: 'edit'
            return
        }

        wxUser.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'wxUser.label', default: 'WxUser'), wxUser.id])
                redirect wxUser
            }
            '*' { respond wxUser, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(WxUser wxUser) {

        if (wxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        wxUser.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'wxUser.label', default: 'WxUser'), wxUser.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'wxUser.label', default: 'WxUser'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }


}
