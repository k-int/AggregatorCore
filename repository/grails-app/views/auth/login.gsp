<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title>Login</title>
</head>
<body>

  <div class="row">
    <div class="offset4 span4">
    <h1>Login</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:form class="well form-inline" action="signIn">
      <input type="hidden" name="targetUri" value="${targetUri}" />
      <table>
        <tbody>
          <tr>
            <td>Username:</td>
            <td><input type="text" name="username" value="${username}" /></td>
          </tr>
          <tr>
            <td>Password:</td>
            <td><input type="password" name="password" value="" /></td>
          </tr>
          <tr>
            <td>Remember me?:</td>
            <td><g:checkBox name="rememberMe" value="${rememberMe}" /></td>
          </tr>
          <tr>
            <td />
            <td><input type="submit" value="Sign in" /></td>
          </tr>
        </tbody>
      </table>
    </g:form>
    </div>
  </div>
</body>
</html>
