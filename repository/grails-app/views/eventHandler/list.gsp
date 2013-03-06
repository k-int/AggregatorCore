
<%@ page import="com.k_int.aggregator.EventHandler" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="main" />
  <g:set var="entityName" value="${message(code: 'eventHandler.label', default: 'EventHandler')}" />
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

        <g:sortableColumn property="id" title="${message(code: 'eventHandler.id.label', default: 'Id')}" />

        <g:sortableColumn property="eventCode" title="${message(code: 'eventHandler.eventCode.label', default: 'Event Code')}" />

        <g:sortableColumn property="active" title="${message(code: 'eventHandler.active.label', default: 'Active')}" />

        <g:sortableColumn property="installDate" title="${message(code: 'eventHandler.installDate.label', default: 'Install Date')}" />

        <g:sortableColumn property="name" title="${message(code: 'eventHandler.name.label', default: 'Name')}" />

        <g:sortableColumn property="preconditions" title="${message(code: 'eventHandler.preconditions.label', default: 'Preconditions')}" />

        </tr>
        </thead>
        <tbody>
        <g:each in="${eventHandlerInstanceList}" status="i" var="eventHandlerInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${eventHandlerInstance.id}">${fieldValue(bean: eventHandlerInstance, field: "id")}</g:link></td>

          <td>${fieldValue(bean: eventHandlerInstance, field: "eventCode")}</td>

          <td><g:formatBoolean boolean="${eventHandlerInstance.active}" /></td>

          <td><g:formatDate date="${eventHandlerInstance.installDate}" /></td>

          <td>${fieldValue(bean: eventHandlerInstance, field: "name")}</td>

          <td>${fieldValue(bean: eventHandlerInstance, field: "preconditions")}</td>

          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${eventHandlerInstanceTotal}" />
    </div>
  </div>
</body>
</html>
