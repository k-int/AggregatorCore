<html>
  <head>
    <title>Aggregator3 - Admin Home</title>
    <meta name="layout" content="main" />
  </head>
  <body>

  <g:if test='${flash.message}'>
    <div class="row">
      <div class="span12">
        <div class="FlashMessage"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
      </div>
    </div>
  </g:if>

  <div class="row">
    <div class="span8">

      <div class="well">
        <h2>Static Information</h2>

        <hr/>
        This is the admin home page - Open Data Aggregator - Instance ID : ${sysid}
        <hr/>
      </div>

      <div class="well">
        <h2>Public Aggregation Endpoints For this ODA</h2>

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
      </div>
    </div>
    <div class="span4">
      <div class="well">
        <h2>Actions</h2>
        <ul>
          <li><g:link action="clearHandlers">Clear down all handlers</g:link>
        </ul>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="span12">
      <div class="well">
        <h2>Registered Handlers for this ODA instance</h2>
        <table border="0" class="table table-striped table-bordered">
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
      </div>

      <div class="well">
        <h2>Currently Cached Handlers</h2>
        <g:if test="${handler_cache.size() > 0}">
          <table border="0" class="table table-striped table-bordered">
            <tr>
              <th>Handler Name</th><th>Actions</th>
            </tr>
            <g:each in="${handler_cache}" var="hi">
              <tr>
                <td>${hi.value.getHandlerName()}</td><td><a href="#">Evict</a></td>
              </tr>
            </g:each>
          </table>
        </g:if>
        <g:else>
          No handlers are currently cached
        </g:else>
      </div>

      <div class="well">
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
      </div>


    </div>
  </div>


</body>
</html>
