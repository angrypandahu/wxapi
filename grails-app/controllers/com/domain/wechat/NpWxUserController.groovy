package com.domain.wechat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NpWxUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond NpWxUser.list(params), model:[npWxUserCount: NpWxUser.count()]
    }

    def show(NpWxUser npWxUser) {
        respond npWxUser
    }

    def create() {
        respond new NpWxUser(params)
    }

    @Transactional
    def save(NpWxUser npWxUser) {
        if (npWxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (npWxUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond npWxUser.errors, view:'create'
            return
        }

        npWxUser.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'npWxUser.label', default: 'NpWxUser'), npWxUser.id])
                redirect npWxUser
            }
            '*' { respond npWxUser, [status: CREATED] }
        }
    }

    def edit(NpWxUser npWxUser) {
        respond npWxUser
    }

    @Transactional
    def update(NpWxUser npWxUser) {
        if (npWxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (npWxUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond npWxUser.errors, view:'edit'
            return
        }

        npWxUser.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'npWxUser.label', default: 'NpWxUser'), npWxUser.id])
                redirect npWxUser
            }
            '*'{ respond npWxUser, [status: OK] }
        }
    }

    @Transactional
    def delete(NpWxUser npWxUser) {

        if (npWxUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        npWxUser.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'npWxUser.label', default: 'NpWxUser'), npWxUser.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'npWxUser.label', default: 'NpWxUser'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
