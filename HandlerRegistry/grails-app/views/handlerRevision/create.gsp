

<%@ page import="com.k_int.handlerregistry.HandlerRevision" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'handlerRevision.label', default: 'HandlerRevision')}" />
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
            <g:hasErrors bean="${handlerRevisionInstance}">
            <div class="errors">
                <g:renderErrors bean="${handlerRevisionInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                                                
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="owner"><g:message code="handlerRevision.owner.label" default="Owner" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerRevisionInstance, field: 'owner', 'errors')}">
                                    <g:select name="owner.id" from="${com.k_int.handlerregistry.Handler.list()}" optionKey="id" value="${handlerRevisionInstance?.owner?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="revision"><g:message code="handlerRevision.revision.label" default="Revision" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerRevisionInstance, field: 'revision', 'errors')}">
                                    <g:textField name="revision" value="${fieldValue(bean: handlerRevisionInstance, field: 'revision')}" />
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
