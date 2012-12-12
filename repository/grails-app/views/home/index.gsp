<html>
  <head>
    <title>${grailsApplication.config.aggr.system.name}</title>
    <meta name="layout" content="main" />
  </head>
  <body>

<div class="hero-unit">
Aggregation Instance ID : ${sysid}
</div>

<div class="hero-unit">
<h2>Public Aggregation (Search/Retrieve) Endpoints For This Repository</h2>
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
</div>

<div class="hero-unit">

<h2>Registered Handlers for this repository instance</h2>
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
    <td>${h.name}</td>
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
<p>
When deposited resources are not identified by any registered handler, the following services will be consulted to see if an appropriate handler can be dynamically downloaded and installed
<ul>
<g:each in="${handlerrepos}" var="hr">
  <li>${hr.url} using userid ${hr.user}</li>
</g:each>
</ul>
</p>
</div>

  </body>
</html>
