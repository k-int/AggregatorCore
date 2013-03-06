<!DOCTYPE html>
<html>
  <head>
    <title><g:layoutTitle default="Grails" /></title>
    <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
  <r:require modules="bootstrap"/>
  <g:layoutHead />
  <r:layoutResources/>
  <style type="text/css">
    body {
      padding-top: 60px;
      padding-bottom: 40px;
    }
  </style>
</head>
<body>

  <nav class="navbar navbar-fixed-top">
    <div class="navbar-inner">
      <div class="container-fluid">
        <a class="brand" href="#">${grailsApplication.config.aggr.system.name}</a>
        <div class="nav-collapse">
          <ul class="nav">
            <li class="active"><a href="#">Home</a></li>
            <li><g:link controller="admin" action="index">Admin</g:link></li>
          </ul>
        </div>
      </div>
    </div>
  </nav>

  <div class="container">
    <g:layoutBody />
  </div>

  <footer>
    <p style="text-align:center">&copy; Knowledge Integration Ltd 2012</p>
  </footer>

<r:layoutResources/>
</body>
</html>
