

<%@ page import="com.k_int.aggregator.EventHandler" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'eventHandler.label', default: 'EventHandler')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
        <link rel="stylesheet" href="${resource(dir:'js/code-mirror',file:'codemirror.css')}" type="text/css" media="all"/>
        <link rel="stylesheet" href="${resource(dir:'js/code-mirror/theme',file:'default.css')}" type="text/css" media="all"/>
        <link rel="stylesheet" href="${resource(dir:'js/jquery/jquery-ui-1.8.16.custom.css')}" type="text/css" media="all"/>
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" type="text/javascript"></script>
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/jquery-ui.min.js" type="text/javascript"></script>
        <script src="${resource(dir:'js/code-mirror',file:'codemirror.js')}" type="text/javascript"></script>    
        <script src="${resource(dir:'js/code-mirror',file:'groovy.js')}" type="text/javascript"></script>      
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <g:form method="post" id="${eventHandlerInstance?.id}">
            <g:hiddenField name="id" value="${eventHandlerInstance?.id}" />
            <g:hiddenField name="version" value="${eventHandlerInstance?.version}" />
            <div class="dialog">
                <table style="border-top: 0; border-left: 0; border-right : 0">
                    <tbody>
                    
                    	<tr>
                    		<td colspan="2">
	                    	 	<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
						        <g:if test="${flash.message}">
						        <div class="message">${flash.message}</div>
						        </g:if>
						        <g:hasErrors bean="${eventHandlerInstance}">
						        <div class="errors">
						            <g:renderErrors bean="${eventHandlerInstance}" as="list" />
						        </div>
						        </g:hasErrors>
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
                                <g:if test="${eventHandlerInstance?.preconditions && eventHandlerInstance?.preconditions.size() > 0}">
                                <ul>
                                    <g:each in="${eventHandlerInstance?.preconditions}">
                                        <li>${it}</li>
                                    </g:each> 
                                </ul>   
                                </g:if>  
                                <g:else>
                                    None
                                </g:else>                     
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="eventCode"><g:message code="eventHandler.eventCode.label" default="Event Code" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: eventHandlerInstance, field: 'eventCode', 'errors')}">
                              ${eventHandlerInstance?.eventCode}                      
                            </td>
                        </tr>
                    
                    </tbody>
                </table>
                Event Handler Code <br/>
                <%
					boolean hasScriptlet = false;
					try{
						if ( eventHandlerInstance?.scriptlet )
						  hasScriptlet = true;
					} catch (Exception e) {
					    hasScriptlet = false;
					}
			    %>
                <g:if test="${hasScriptlet}">
               	    <g:textArea name="scriptlet" id="scriptlet" cols="40" rows="20" value="${eventHandlerInstance?.scriptlet}" />
               	</g:if>
            </div>
            <div id="tabs">
				<ul>
					<li><a href="#tabs-1">Console</a></li>
				</ul>
				<div id="tabs-1">
					<g:if test="${flash.compilation_error}">
                        <p>${flash.compilation_error}</p>
                    </g:if>
				</div>
			</div>  
            <div class="buttons">
                <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
            </div>
        </g:form>
        <g:javascript>
     	$(document).ready(function() 
		{
			$( "#tabs" ).tabs();
		
     		var editor = CodeMirror.fromTextArea(document.getElementById("scriptlet"), 
     		{
        		lineNumbers: true,
        		matchBrackets: true,
        		mode: "text/x-groovy"
     		});
     	});
		</g:javascript>
    </body>
</html>
