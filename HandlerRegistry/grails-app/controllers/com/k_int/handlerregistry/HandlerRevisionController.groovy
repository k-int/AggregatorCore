package com.k_int.handlerregistry

class HandlerRevisionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [handlerRevisionInstanceList: HandlerRevision.list(params), handlerRevisionInstanceTotal: HandlerRevision.count()]
    }

    def create = {
        def handlerRevisionInstance = new HandlerRevision()
        handlerRevisionInstance.properties = params
        return [handlerRevisionInstance: handlerRevisionInstance]
    }

    def save = {
        def handlerRevisionInstance = new HandlerRevision(params)
        if (handlerRevisionInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), handlerRevisionInstance.id])}"
            redirect(action: "show", id: handlerRevisionInstance.id)
        }
        else {
            render(view: "create", model: [handlerRevisionInstance: handlerRevisionInstance])
        }
    }

    def show = {
        def handlerRevisionInstance = HandlerRevision.get(params.id)
        if (!handlerRevisionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
            redirect(action: "list")
        }
        else {
            [handlerRevisionInstance: handlerRevisionInstance]
        }
    }

    def edit = {
        def handlerRevisionInstance = HandlerRevision.get(params.id)
        if (!handlerRevisionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [handlerRevisionInstance: handlerRevisionInstance]
        }
    }

    def update = {
        def handlerRevisionInstance = HandlerRevision.get(params.id)
        if (handlerRevisionInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (handlerRevisionInstance.version > version) {
                    
                    handlerRevisionInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'handlerRevision.label', default: 'HandlerRevision')] as Object[], "Another user has updated this HandlerRevision while you were editing")
                    render(view: "edit2", model: [handlerRevisionInstance: handlerRevisionInstance])
                    return
                }
            }
            handlerRevisionInstance.properties = params
            if (!handlerRevisionInstance.hasErrors() && handlerRevisionInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), handlerRevisionInstance.id])}"
                redirect(action: "show", id: handlerRevisionInstance.id)
            }
            else {
                render(view: "edit2", model: [handlerRevisionInstance: handlerRevisionInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def handlerRevisionInstance = HandlerRevision.get(params.id)
        if (handlerRevisionInstance) {
            try {
                handlerRevisionInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), params.id])}"
            redirect(action: "list")
        }
    }
}
