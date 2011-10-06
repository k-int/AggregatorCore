

<%@ page import="com.k_int.handlerregistry.Handler" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'handler.label', default: 'Handler')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${handlerInstance}">
            <div class="errors">
                <g:renderErrors bean="${handlerInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${handlerInstance?.id}" />
                <g:hiddenField name="version" value="${handlerInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="preconditions"><g:message code="handler.preconditions.label" default="Preconditions" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerInstance, field: 'preconditions', 'errors')}">
                                    
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="liveRevision"><g:message code="handler.liveRevision.label" default="Live Revision" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerInstance, field: 'liveRevision', 'errors')}">
                                    <g:select name="liveRevision.id" from="${com.k_int.handlerregistry.HandlerRevision.list()}" optionKey="id" value="${handlerInstance?.liveRevision?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="handler.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${handlerInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="revisions"><g:message code="handler.revisions.label" default="Revisions" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerInstance, field: 'revisions', 'errors')}">
                                    
<ul>
<g:each in="${handlerInstance?.revisions?}" var="r">
    <li><g:link controller="handlerRevision" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="handlerRevision" action="create" params="['handler.id': handlerInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'handlerRevision.label', default: 'HandlerRevision')])}</g:link>

                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
