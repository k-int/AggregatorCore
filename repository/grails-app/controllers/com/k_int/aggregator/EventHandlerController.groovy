package com.k_int.aggregator

import org.apache.commons.io.FileUtils;

class EventHandlerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [eventHandlerInstanceList: EventHandler.list(params), eventHandlerInstanceTotal: EventHandler.count()]
    }

    def create = {
        def eventHandlerInstance = new EventHandler()
        eventHandlerInstance.properties = params
        return [eventHandlerInstance: eventHandlerInstance]
    }

    def save = {
        def eventHandlerInstance = new EventHandler(params)
        if (eventHandlerInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), eventHandlerInstance.id])}"
            redirect(action: "show", id: eventHandlerInstance.id)
        }
        else {
            render(view: "create", model: [eventHandlerInstance: eventHandlerInstance])
        }
    }

    def show = {
        def eventHandlerInstance = EventHandler.get(params.id)
        if (!eventHandlerInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
            redirect(action: "list")
        }
        else {
            [eventHandlerInstance: eventHandlerInstance]
        }
    }

    def edit = {
        def eventHandlerInstance = EventHandler.get(params.id)
        if (!eventHandlerInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [eventHandlerInstance: eventHandlerInstance]
        }
    }

    def update = {
        def eventHandlerInstance = EventHandler.get(params.id)
        if (eventHandlerInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (eventHandlerInstance.version > version) {
                    
                    eventHandlerInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'eventHandler.label', default: 'EventHandler')] as Object[], "Another user has updated this EventHandler while you were editing")
                    render(view: "edit", model: [eventHandlerInstance: eventHandlerInstance])
                    return
                }
                
                try {
                    
                    eventHandlerInstance.properties = params
                    
                    //Create the groovy file from the database string
                    def handler_file = new File(eventHandlerInstance?.name + ".groovy");
                    //fileStore.createNewFile();
                    FileUtils.writeStringToFile(handler_file, eventHandlerInstance.scriptlet);
                    
                    // Class clazz = gcl.parseClass(handler_file.text);
                    log.debug("Compiling ${handler_file}");
        
                    GroovyClassLoader gcl = new GroovyClassLoader();
                    
                    Class clazz = gcl.parseClass(handler_file)
                    // log.debug("Number of annotations in ${handler_file} : ${clazz.annotations.length} (This should be >0 for classes with grape annotations)");
        
                    log.debug("Instatiating ${handler_file}");
                    Object h = clazz.newInstance();
            
                    log.info("Created handler for ${h.getHandlerName()} - Compilation completed OK");
                    
                    //EVERYTHING 'OK' SO LETS SAVE     
                    if (!eventHandlerInstance.hasErrors() && eventHandlerInstance.save(flush: true)) {
                        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), eventHandlerInstance.id])}"
                        redirect(action: "show", id: eventHandlerInstance.id)
                    }
                    else {
                        render(view: "edit", model: [eventHandlerInstance: eventHandlerInstance])
                    }
                 }
                 catch ( Exception e ) {
                     //Problem occurred so do not save and return error to user
                    log.error("Unable to compile handler",e);
                    flash.message = "Unable to compile handler, see the console for more information."
                    flash.compilation_error = e.toString()
                    render(view: "edit", model: [eventHandlerInstance: eventHandlerInstance])
                 }
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def eventHandlerInstance = EventHandler.get(params.id)
        if (eventHandlerInstance) {
            try {
                eventHandlerInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'eventHandler.label', default: 'EventHandler'), params.id])}"
            redirect(action: "list")
        }
    }
}
