package com.k_int.handlerregistry

class HandlerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [handlerInstanceList: Handler.list(params), handlerInstanceTotal: Handler.count()]
    }

    def create = {
        def handlerInstance = new Handler()
        handlerInstance.properties = params
        return [handlerInstance: handlerInstance]
    }

    def save = {
        def handlerInstance = new Handler(params)
        if (handlerInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'handler.label', default: 'Handler'), handlerInstance.id])}"
            redirect(action: "show", id: handlerInstance.id)
        }
        else {
            render(view: "create", model: [handlerInstance: handlerInstance])
        }
    }

    def show = {
        def handlerInstance = Handler.get(params.id)
        if (!handlerInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
            redirect(action: "list")
        }
        else {
            [handlerInstance: handlerInstance]
        }
    }

    def edit = {
        def handlerInstance = Handler.get(params.id)
        if (!handlerInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [handlerInstance: handlerInstance]
        }
    }

    def update = {
        def handlerInstance = Handler.get(params.id)
        if (handlerInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (handlerInstance.version > version) {
                    
                    handlerInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'handler.label', default: 'Handler')] as Object[], "Another user has updated this Handler while you were editing")
                    render(view: "edit", model: [handlerInstance: handlerInstance])
                    return
                }
            }
            handlerInstance.properties = params
            if (!handlerInstance.hasErrors() && handlerInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'handler.label', default: 'Handler'), handlerInstance.id])}"
                redirect(action: "show", id: handlerInstance.id)
            }
            else {
                render(view: "edit", model: [handlerInstance: handlerInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def handlerInstance = Handler.get(params.id)
        if (handlerInstance) {
            try {
                handlerInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handler.label', default: 'Handler'), params.id])}"
            redirect(action: "list")
        }
    }
}
