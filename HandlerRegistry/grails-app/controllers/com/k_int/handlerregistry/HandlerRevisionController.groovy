package com.k_int.handlerregistry

import org.apache.commons.io.FileUtils;

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
                    render(view: "edit", model: [handlerRevisionInstance: handlerRevisionInstance])
                    return
                }
            }

            /* BEFORE SAVE WE MUST COMPILE */          
            try {
                
                handlerRevisionInstance.properties = params
                
                //Create the groovy file from the database string
                def handler_file = new File(handlerRevisionInstance?.owner?.name + ".groovy");
                //fileStore.createNewFile();
                FileUtils.writeStringToFile(handler_file, handlerRevisionInstance.handlerText);
                
                // Class clazz = gcl.parseClass(handler_file.text);
                log.debug("Compiling ${handler_file}");
    
                GroovyClassLoader gcl = new GroovyClassLoader();
                
                Class clazz = gcl.parseClass(handler_file)
                // log.debug("Number of annotations in ${handler_file} : ${clazz.annotations.length} (This should be >0 for classes with grape annotations)");
    
                log.debug("Instatiating ${handler_file}");
                Object h = clazz.newInstance();
        
                log.info("Created handler for ${h.getHandlerName()} - Compilation completed OK");
                
                //EVERYTHING 'OK' SO LETS SAVE
                
                if (!handlerRevisionInstance.hasErrors() && handlerRevisionInstance.save(flush: true)) {
                    flash.message = "${message(code: 'default.updated.message', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision'), handlerRevisionInstance.id])}"
                    redirect(action: "show", id: handlerRevisionInstance.id)
                }
                else {
                    render(view: "edit", model: [handlerRevisionInstance: handlerRevisionInstance])
                }
             }
             catch ( Exception e ) {
                 //Problem occurred so do not save and return error to user
                log.error("Unable to compile handler",e);
                flash.message = e.toString()
                flash.compilation_error = e.toString()
                render(view: "edit", model: [handlerRevisionInstance: handlerRevisionInstance])
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
