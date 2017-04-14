package com.domain.api

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ApiTokenController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ApiToken.list(params), model:[apiTokenCount: ApiToken.count()]
    }

    def show(ApiToken apiToken) {
        respond apiToken
    }

    def create() {
        respond new ApiToken(params)
    }

    @Transactional
    def save(ApiToken apiToken) {
        if (apiToken == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiToken.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiToken.errors, view:'create'
            return
        }

        apiToken.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'apiToken.label', default: 'ApiToken'), apiToken.id])
                redirect apiToken
            }
            '*' { respond apiToken, [status: CREATED] }
        }
    }

    def edit(ApiToken apiToken) {
        respond apiToken
    }

    @Transactional
    def update(ApiToken apiToken) {
        if (apiToken == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (apiToken.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond apiToken.errors, view:'edit'
            return
        }

        apiToken.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'apiToken.label', default: 'ApiToken'), apiToken.id])
                redirect apiToken
            }
            '*'{ respond apiToken, [status: OK] }
        }
    }

    @Transactional
    def delete(ApiToken apiToken) {

        if (apiToken == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        apiToken.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'apiToken.label', default: 'ApiToken'), apiToken.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'apiToken.label', default: 'ApiToken'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
