
<%@ page import="com.k_int.handlerregistry.Handler" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'handler.label', default: 'Handler')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'handler.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="preconditions" title="${message(code: 'handler.preconditions.label', default: 'Preconditions')}" />
                        
                            <th><g:message code="handler.liveRevision.label" default="Live Revision" /></th>
                        
                            <g:sortableColumn property="name" title="${message(code: 'handler.name.label', default: 'Name')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${handlerInstanceList}" status="i" var="handlerInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${handlerInstance.id}">${fieldValue(bean: handlerInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: handlerInstance, field: "preconditions")}</td>
                        
                            <td>${fieldValue(bean: handlerInstance, field: "liveRevision")}</td>
                        
                            <td>${fieldValue(bean: handlerInstance, field: "name")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${handlerInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
