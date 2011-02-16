<h1>Metadata upload form</h1>

<g:if test="${code}">Response code : ${code} </br></g:if>
<g:if test="${status}">Response status : ${status} </br></g:if>
<g:if test="${message}">Response message : ${message} </br></g:if>

<form method="POST" enctype="multipart/form-data">
  File: <input type="file" name="upload" label="File"/><br/>
  On Behalf Of: <input type="text" name="owner" label="Owner"/><br/>
  <input type="submit"/>
<form>
