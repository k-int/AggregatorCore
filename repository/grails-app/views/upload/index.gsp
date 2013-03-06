<h1>${grailsApplication.config.aggr.system.name} - Resource Deposit</h1>

<g:if test="${code}">Response code : ${code} </br></g:if>
<g:if test="${status}">Response status : ${status} </br></g:if>
<g:if test="${message}">Response message : ${message} </br></g:if>

<form method="POST" enctype="multipart/form-data">
  File: <input type="file" name="upload" label="File"/><br/>
  On Behalf Of: <input type="text" name="owner" label="Owner"/><br/>
  <input type="submit"/>
  <form>

    <g:if test="${eventLog}">
      <div id="eventLog">
        <table>
          <g:each in="${eventLog}" var="m">
            <tr>
              <td>${m.ts}</td>
              <td>${m.type}</td>
              <td>
            <g:if test="${m.type=='msg'}">
${m.msg}
            </g:if>
            <g:if test="${m.type=='ref'}">
              &nbsp;
              <g:if test="${m.serviceref=='mongo'}">
                Mongo uri : <a href="http://localhost:28017/${m.mongodb}/${m.mongoindex}/?filter__id=${m.mongoid}">http://localhost:28017/${m.mongodb}/${m.mongoindex}/?filter__id=${m.mongoid}</a>
              </g:if>
              <g:elseif test="${m.serviceref=='es'}">
                ElasticSearch : <a href="http://localhost:9200/${m.escollection}/${m.estype}/_search?q=_id:${m.esid}">http://localhost:9200/${m.escollection}/${m.estype}/_search?q=${m.esid}</a>
              </g:elseif>
              <g:elseif test="${m.serviceref=='xcriportal'}">
                XCRI Portal : <a href="http://localhost/XCRI/course/${m.id}">http://localhost/XCRI/course/${m.id}</a>
              </g:elseif>
              <g:else>
                ref params: ${m}
              </g:else>
            </g:if>
            <g:if test="${m.type=='item'}">
              An item was added
            </g:if>
            </td>
            </tr>
          </g:each>
        </table>
      </div>
    </g:if>
