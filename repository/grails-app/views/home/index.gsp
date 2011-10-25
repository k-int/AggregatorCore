This is the index page

Public Aggregation Endpoints For this ODA are:

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
      <li></li>
    </ul>
  </li>
  <li>4Store
    <ul>
      <li></li>
    </ul>
  </li>
  <li>MongoDB
    <ul>
      <li></li>
    </ul>
  </li>
</ul>
