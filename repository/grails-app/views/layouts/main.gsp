<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <r:require modules="bootstrap"/>
        <g:layoutHead />
        <g:javascript library="application" />
        <r:layoutResources/>
    </head>
    <body>
        <g:layoutBody />
        <r:layoutResources/>
    </body>
</html>
