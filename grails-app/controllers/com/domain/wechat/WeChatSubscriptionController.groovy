package com.domain.wechat

import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class WeChatSubscriptionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond WeChatSubscription.list(params), model:[weChatSubscriptionCount: WeChatSubscription.count()]
    }

    def show(WeChatSubscription weChatSubscription) {
        respond weChatSubscription
    }

    def create() {
        respond new WeChatSubscription(params)
    }

    @Transactional
    def save(WeChatSubscription weChatSubscription) {
        if (weChatSubscription == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (weChatSubscription.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond weChatSubscription.errors, view:'create'
            return
        }

        weChatSubscription.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'weChatSubscription.label', default: 'WeChatSubscription'), weChatSubscription.id])
                redirect weChatSubscription
            }
            '*' { respond weChatSubscription, [status: HttpStatus.CREATED] }
        }
    }

    def edit(WeChatSubscription weChatSubscription) {
        respond weChatSubscription
    }

    @Transactional
    def update(WeChatSubscription weChatSubscription) {
        if (weChatSubscription == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (weChatSubscription.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond weChatSubscription.errors, view:'edit'
            return
        }

        weChatSubscription.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'weChatSubscription.label', default: 'WeChatSubscription'), weChatSubscription.id])
                redirect weChatSubscription
            }
            '*'{ respond weChatSubscription, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(WeChatSubscription weChatSubscription) {

        if (weChatSubscription == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        weChatSubscription.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'weChatSubscription.label', default: 'WeChatSubscription'), weChatSubscription.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'weChatSubscription.label', default: 'WeChatSubscription'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_FOUND }
        }
    }
}
