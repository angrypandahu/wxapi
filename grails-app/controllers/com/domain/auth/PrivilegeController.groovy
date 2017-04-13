package com.domain.auth

import com.controller.BaseController
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class PrivilegeController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def menuService
    def privilegeService
    def orgService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def userId = getAuthenticatedUser().id
        respond Privilege.list(params), model: [privilegeCount: Privilege.count(), zTree: privilegeService.findZTreeJSON(userId)]
    }


    def auth() {
        def userTable = menuService.getUserTable().toJSON().toString()
        def roleTable = menuService.getRoleTable().toJSON().toString()
        def privilegeTable = menuService.getPrivilegeTable().toJSON().toString()
        def treePanel = orgService.treePanel().toJSON().toString()
        render(view: 'auth', model: [userTable: userTable, roleTable: roleTable,privilegeTable:privilegeTable, treePanel: treePanel])
    }

    def show(Privilege privilege) {
        respond privilege
    }

    def getZtree() {
        render(privilegeService.findZTreeJSON(getAuthenticatedUser().id).toString())
    }

    def create() {
        respond new Privilege(params)
    }

    @Transactional
    def save(Privilege privilege) {
        if (privilege == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (privilege.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond privilege.errors, view: 'create'
            return
        }

        privilege.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'privilege.label', default: 'Privilege'), privilege.id])
                redirect privilege
            }
            '*' { respond privilege, [status: HttpStatus.CREATED] }
        }
    }

    def edit(Privilege privilege) {
        respond privilege
    }

    @Transactional
    def update(Privilege privilege) {
        if (privilege == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (privilege.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond privilege.errors, view: 'edit'
            return
        }

        privilege.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'privilege.label', default: 'Privilege'), privilege.id])
                redirect privilege
            }
            '*' { respond privilege, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(Privilege privilege) {

        if (privilege == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        privilege.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'privilege.label', default: 'Privilege'), privilege.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'privilege.label', default: 'Privilege'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
