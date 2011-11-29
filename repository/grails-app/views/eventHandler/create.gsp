

<%@ page import="com.k_int.aggregator.EventHandler" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'eventHandler.label', default: 'EventHandler')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${eventHandlerInstance}">
            <div class="errors">
                <g:renderErrors bean="${eventHandlerInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="eventCode"><g:message code="eventHandler.eventCode.label" default="Event Code" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'eventCode', 'errors')}">
                                    <g:textArea name="eventCode" cols="40" rows="5" value="${eventHandlerInstance?.eventCode}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="active"><g:message code="eventHandler.active.label" default="Active" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'active', 'errors')}">
                                    <g:checkBox name="active" value="${eventHandlerInstance?.active}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="installDate"><g:message code="eventHandler.installDate.label" default="Install Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'installDate', 'errors')}">
                                    <g:datePicker name="installDate" precision="day" value="${eventHandlerInstance?.installDate}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="eventHandler.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${eventHandlerInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="preconditions"><g:message code="eventHandler.preconditions.label" default="Preconditions" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'preconditions', 'errors')}">
                                    
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
