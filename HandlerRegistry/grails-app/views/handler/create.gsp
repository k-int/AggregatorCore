

<%@ page import="com.k_int.handlerregistry.Handler" %>
<html>
    <head>
        <g:javascript library="jquery"/>
        <jqui:resources/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'handler.label', default: 'Handler')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
        <script language="JavaScript">
          function addPrecondition() {
            alert("addPrecondition");
          }
        </script>
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
            <g:hasErrors bean="${handlerInstance}">
            <div class="errors">
                <g:renderErrors bean="${handlerInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >

                <g:hiddenField id="preconditionCount" name="numPreconditions" value="${handlerInstance?.preconditions?.size()}"/>

                <div class="dialog">
                    <table>
                        <tbody>
                            

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
                                    <label for="preconditions"><g:message code="handler.preconditions.label" default="Preconditions" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: handlerInstance, field: 'preconditions', 'errors')}">
                                    There are currently ${handlerInstance?.preconditions?.size()} Preconditions defined.
                                    <ul id="PreconditionUL">
                                      <g:each in="${handlerInstance?.preconditions}" status="i" var="precondition">
                                        <li>One for each precondition ${precondition}</li>
                                      </g:each>
                                    </ul>
                                    <input type="button" name="Add Precondition" onClick="javascript:addPrecondition();"/>
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
