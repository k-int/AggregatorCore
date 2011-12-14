

<%@ page import="com.k_int.handlerregistry.HandlerRevision" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'handlerRevision.label', default: 'HandlerRevision')}" />
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
       
            <g:form method="post" >
                <g:hiddenField name="id" value="${handlerRevisionInstance?.id}" />
                <g:hiddenField name="version" value="${handlerRevisionInstance?.version}" />
                <div class="dialog">
                <table style="border-top: 0; border-left: 0; border-right : 0">
                	<tbody>
                		<tr>
	                		<td colspan="2">
		                		<h1>
		                			<g:message code="default.edit.label" args="[entityName]" />
		                		</h1>
					            <g:if test="${flash.message}">
					            <div class="message">${flash.message}</div>
					            </g:if>
					            <g:hasErrors bean="${handlerRevisionInstance}">
					            <div class="errors">
					                <g:renderErrors bean="${handlerRevisionInstance}" as="list" />
					            </div>
					            </g:hasErrors>
	                		</td>
                		</tr>
                		<tr class="prop">
                            <td valign="top" class="name">
		                		<label for="owner"><g:message code="handlerRevision.owner.label" default="Owner" /></label>
		                  	</td>
		                  	 <td valign="top" class="value">
		                  		<g:select name="owner.id" from="${com.k_int.handlerregistry.Handler.list()}" optionKey="id" optionValue="name" value="${handlerRevisionInstance?.owner?.id}"  />
		                	</td>
		                </tr>	
		                <tr class="prop">
		                	<td valign="top" class="name">
                				<label for="revision"><g:message code="handlerRevision.revision.label" default="Revision" /></label>
                			</td>
                			<td valign="top" class="value">
                  				<g:textField name="revision" value="${fieldValue(bean: handlerRevisionInstance, field: 'revision')}" />
                  			</td>
                  		</tr>
                  	</tbody>
                </table>
                <g:textArea name="handler" cols="40" rows="5" value="${handlerRevisionInstance?.handlerText}" />
                </div>
                <div id="tabs">
					<ul>
						<li><a href="#tabs-1">Console</a></li>
					</ul>
					<div id="tabs-1">
						<p>Console messages here</p>
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
		
     		var editor = CodeMirror.fromTextArea(document.getElementById("handler"), 
     		{
        		lineNumbers: true,
        		matchBrackets: true,
        		mode: "text/x-groovy"
     		});
     	});
		</g:javascript>
    </body>
</html>
