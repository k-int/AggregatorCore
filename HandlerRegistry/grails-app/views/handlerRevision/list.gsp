
<%@ page import="com.k_int.handlerregistry.HandlerRevision" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="main" />
  <g:set var="entityName" value="${message(code: 'handlerRevision.label', default: 'HandlerRevision')}" />
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

        <g:sortableColumn property="id" title="${message(code: 'handlerRevision.id.label', default: 'Id')}" />

        <th><g:message code="handlerRevision.owner.label" default="Owner" /></th>

        <g:sortableColumn property="revision" title="${message(code: 'handlerRevision.revision.label', default: 'Revision')}" />

        </tr>
        </thead>
        <tbody>
        <g:each in="${handlerRevisionInstanceList}" status="i" var="handlerRevisionInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${handlerRevisionInstance.id}">${fieldValue(bean: handlerRevisionInstance, field: "id")}</g:link></td>

          <td>${fieldValue(bean: handlerRevisionInstance, field: "owner.name")}</td>

          <td>${fieldValue(bean: handlerRevisionInstance, field: "revision")}</td>

          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${handlerRevisionInstanceTotal}" />
    </div>
  </div>
</body>
</html>
