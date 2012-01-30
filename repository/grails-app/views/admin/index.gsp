<html>
  <head>
    <title>Aggregator3 - Admin Home</title>
    <meta name="layout" content="main" />
  </head>
  <body>

    <g:if test='${flash.message}'>
      <div class="FlashMessage"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
    </g:if>

<table>
<tr>
  <th>Static Information</th>
  <th>Actions</th>
</tr>
<tr>
<td>

<hr/>
This is the admin home page - Open Data Aggregator - Instance ID : ${sysid}
<hr/>

<h2>Public Aggregation Endpoints For this ODA</h2>

<p>
<ul>
  <li>ElasticSearch
    <ul>
      <g:each in="${aggregations['es']}" var="i">
        <li>${i.title}
          <table>
            <tr><td>Identifier:</td><td>${i.identifier}</td></tr>
            <tr><td>Description:</td><td>${i.description}</td></tr>
          </table>
        </li>
      </g:each>
    </ul>
  </li>
  <li>SOLR
    <ul>
      <g:each in="${aggregations['solr']}" var="i">
        <li>${i.title}
          <table>
            <tr><td>Identifier:</td><td>${i.identifier}</td></tr>
            <tr><td>Description:</td><td>${i.description}</td></tr>
          </table>
        </li>
      </g:each>
    </ul>
  </li>
  <li>4Store
    <ul>
      <g:each in="${aggregations['4s']}" var="i">
        <li>${i.title}
          <table>
            <tr><td>Identifier:</td><td>${i.identifier}</td></tr>
            <tr><td>Description:</td><td>${i.description}</td></tr>
          </table>
        </li>
      </g:each>
    </ul>
  </li>
  <li>MongoDB
    <ul>
      <g:each in="${aggregations['mongo']}" var="i">
        <li>${i.title}
          <table>
            <tr><td>Identifier:</td><td>${i.identifier}</td></tr>
            <tr><td>Description:</td><td>${i.description}</td></tr>
          </table>
        </li>
      </g:each>
    </ul>
  </li>
</ul>
</p>

<h2>Registered Handlers for this ODA instance</h2>
<p>
<table border="1">
  <tr>
    <th>Handler Name</th>
    <th>Event Code</th>
    <th>Active?</th>
    <th>Preconditions</th>
    <th>Install date</th>
  </tr>
<g:each in="${handlers}" var="h">
  <tr>
    <td><g:link class="create" controller="eventHandler" id="${h.id}" action="edit">${h.name}</g:link></td>
    <td>${h.eventCode}</td>
    <td>${h.active}</td>
    <td>
      <ul>
      <g:each in="${h.preconditions}" var="p">
        <li>${p}</li>
      </g:each>
      </ul>
    </td>
    <td>${h.installDate}</td>
  </tr>
</g:each>
</table>
</p>

<h2>Currently Cached Handlers</h2>
<table border="1">
  <tr>
    <th>Handler Name</th>
  </tr>
<g:each in="${handler_cache}" var="hi">
  <tr>
    <td>${hi.value.getHandlerName()}</td>
  </tr>
</g:each>
</table>

<h2>Registered Remote Handler Repository for this ODA (${sysid})</h2>
<p>
When deposited resources are not identified by any registered handler, the following services will be consulted to see if an appropriate handler can be dynamically downloaded and installed
</p>
<p>
<ul>
<g:each in="${handlerrepos}" var="hr">
  <li>${hr.url} using userid ${hr.user}</li>
</g:each>
</ul>
</p>

        </td>
        <td>
          <ul>
            <li><g:link action="clearHandlers">Clear down all handlers</g:link>
          </ul>
        </td>
      </tr>
    </table>


  </body>
</html>
