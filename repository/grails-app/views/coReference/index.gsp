<html>
  <head>
    <title>${grailsApplication.config.aggr.system.name}</title>
    <meta name="layout" content="main" />
    <script src="${resource(dir:'js/jquery/downloadify',file:'downloadify.min.js')}"></script>
    <script src="${resource(dir:'js/jquery/downloadify',file:'swfobject.js')}"></script>
  </head>
  <body>
    <g:if test="${message}">
        <div class="message">${message}</div>
    </g:if>
    <h1>Import Co References</h1>
    <p>You should have previously exported your existing list of co refs as a JSON file. 
    To recreate your Co Refs in the database select the most recent JSON export file and click Submit. </p>
    <g:uploadForm mapping="coRefUpload">
        <input type="file" name="myFile" />
        <input type="submit" value="Submit"/>
    </g:uploadForm>  
    <h1>Export Co References</h1>
    <p>Click the button below to download an JSON export of the co refs.</p>
    <div id="downloadify"></div>

    <script>
    var CONTEXT_PATH = '<%= request.getContextPath()%>';
    var coRefData = '';
    
    function appendExportButton()
    {
    	Downloadify.create('downloadify',
    	{
   		  filename: 'corefs-export-' + new Date().getTime() + '.json',
             dataType: 'string',
             data: function()
             {
                 return JSON.stringify(window.coRefData);
             },
   		  onComplete: function(){ 
   		    alert('Your File Has Been Saved!'); 
   		  },
   		  onCancel: function(){ 
   		    alert('You have cancelled the saving of this file.');
   		  },
   		  onError: function(){ 
   		    alert('You must put something in the File Contents or there will be nothing to save!'); 
   		  },
   		  swf: window.CONTEXT_PATH + '/js/jquery/downloadify/media/downloadify.swf',
             downloadImage: window.CONTEXT_PATH + '/js/jquery/downloadify/media/download.png',
   		  width: 100,
   		  height: 30,
   		  transparent: true,
   		  append: false
    	});
    }
    
    /* get the coref data */
    $(document).ready(function()
    {         
    	 $.ajax({
    	     type: 'GET',
    	     dataType: 'json',
    	     url: window.CONTEXT_PATH + '/admin/coReference/exporter.json',
    	     success: function(data)
    	     {
    	         window.coRefData = data;
    	     },
    	     error: function()
    	     {
    	         alert('Error');
    	     }
    	 });
    	    	
         appendExportButton();
    });
    </script>
  </body>
</html>