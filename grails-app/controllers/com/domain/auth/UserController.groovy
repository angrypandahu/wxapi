package com.domain.auth

import com.controller.BaseController
import com.util.ExtGrid
import com.util.ExtJsUtil
import com.utils.Constants
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def springSecurityService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        println(params)
        respond User.list(params), model: [userCount: User.count()]
    }

    def extIndex() {
        ExtGrid tableData = new ExtGrid(true, true)
        tableData.addColumnData("User.username.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("User.displayName.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("User.email.label", Constants.DATATYPE_STRING, 100, true)
        if (params.isJson) {
            ExtJsUtil.formatPaging(params)
            tableData.setDatas(User.list(params))
            tableData.setTotalCount(User.count())
            render(tableData.getDatas())
        }
        render(view: 'extIndex', model: [tableData: tableData])
    }

    def show(User user) {
        respond user
    }

    def me() {
        User user = getAuthenticatedUser() as User;
        respond user
    }

    def updatePassword() {
        User user = getAuthenticatedUser() as User;
        respond user
    }

    def create() {
        respond new User(params)
    }

    @Transactional
    def save(User user) {
        if (user == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors, view: 'create'
            return
        }

        user.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect user
            }
            '*' { respond user, [status: HttpStatus.CREATED] }
        }
    }

    @Transactional
    def saveMe() {
        def user = getAuthenticatedUser();
        if (user == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }
        def passwordEncoder = springSecurityService?.passwordEncoder
        def password = springSecurityService.getCurrentUser().getPassword()
        if (params.newPassword != params.confirmPassword) {
            user.errors.reject("newPasswordMismatch", "New password not equal confirm password")
        } else if (!passwordEncoder.isPasswordValid(password, params.currentPassword, null)) {
            user.errors.reject("currentPasswordMismatch", "Current password mismatch")
        }
        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors, view: 'updatePassword'
            return
        }
        user.setPassword(params.newPassword);
        user.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = "Password updated"
                redirect action: 'me'
            }
            '*' { respond user, [status: HttpStatus.OK] }
        }
    }


    def edit(User user) {
        respond user
    }

    @Transactional
    def update(User user) {
        if (user == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors, view: 'edit'
            return
        }

        user.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect user
            }
            '*' { respond user, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(User user) {

        if (user == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        user.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }


}
