<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!--CSS file (default YUI Sam Skin) -->
<link type="text/css" rel="stylesheet" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css"  includeParams="none"/>"></link>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/assets/skins/sam/layout.css"  includeParams="none"/>"></link>
<s:if test="%{isEnterFromTool}">
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
</s:if>
<!-- Dependencies -->
<script type="text/javascript" src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>"></script>

<!-- Source files -->
<script type="text/javascript" src="<s:url value="/yui/datatable/datatable-min.js"  includeParams="none"/>"></script>
<script type="text/javascript" src="<s:url value="/yui/animation/animation-min.js"  includeParams="none"/>"></script>
<s:if test="%{isEnterFromTool}">
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
</s:if>
<script type="text/javascript" src="<s:url value="/yui/layout/layout-min.js"  includeParams="none"/>"></script>

<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
    white-space: nowrap;
} .yui-skin-sam .yui-dt-scrollable .yui-dt-hd, .yui-skin-sam .yui-dt-scrollable .yui-dt-bd {
    border: medium none;
}
 /* selection */
.yui-skin-sam th.yui-dt-selected,
.yui-skin-sam th.yui-dt-selected a {
    background-color:#446CD7; /* bright blue selected cell */
}
.yui-skin-sam tr.yui-dt-selected td,
.yui-skin-sam tr.yui-dt-selected td.yui-dt-asc,
.yui-skin-sam tr.yui-dt-selected td.yui-dt-desc {
    background-color:#426FD9; /* bright blue selected row */
    color:#FFF;
}
.yui-skin-sam tr.yui-dt-selected td a,
.yui-skin-sam tr.yui-dt-selected td.yui-dt-asc a,
.yui-skin-sam tr.yui-dt-selected td.yui-dt-desc a {
    color:#FFF;
}
.yui-skin-sam tr.yui-dt-even td.yui-dt-selected,
.yui-skin-sam tr.yui-dt-odd td.yui-dt-selected {
    background-color:#446CD7; /* bright blue selected cell */
    color:#FFF;
}
.yui-skin-sam .yui-layout{
	background-color: transparent;
}

</style>
<style type="text/css">
	<s:if test="%{!isEnterFromTool}">
	html {
		overflow: hidden;
	}
	</s:if>
	.required {
		color: #FF0000;
	}
    #content {
        overflow: auto;
        width: 100%;
        height: 200px;
        border: 1px solid #ddd;
        padding: 0 4px;
        background: #fff;
    }
	 .description {
        overflow: hidden;
    }
	 .failedMsg{
	 	color: #ff0000;
	 }
	 .a0 {
        float: left;
        margin-left: 0px;
        width: 102px;
        height: 15px;
        border: 1px solid #5B94DF;
    }
	 .a1 {
        float: left;
        height: 13px;
        width: 0px;
        font-size: 12px;
        border-left: 1px solid #FFFFFF;
        border-top: 1px solid #FFFFFF;
        border-bottom: 1px solid #FFFFFF;
        background-Color: #8cd92b;
        filter: progid:DXImageTransform . Microsoft .gradient(startColorstr = #66f900, endColorstr = #81ACE7);
	}
	 #newClientDiv.bd{
	 	overflow: hidden;
	 }
	 a.debug{
	 	text-decoration: none;
	 	margin-right: 10px;
	 	padding: 1px 2px 1px 2px;
	 }
	 a.debug:hover{
	 	background-color: #0066cc;
	 	color: #fff;
	 }
	 
	 span span.unStarted{
	 	color: #f00;
	 }
	 
	 span span.total{
	 	color: #000;
	 }
	 
	 span a.recover{
	 	text-decoration: none;
	 	padding: 2px;
	 }
	 
	 span a.recover:hover{
	 	background-color: #0066cc;
	 	color: #fff;
	 }
	 
	 td.errorDetailCell {
	   width: 120px;
	 }
</style>
<style>
	#overlay.yui-overlay { position:absolute;background:#fff;border:1px dotted black;padding:5px;margin:10px; }
	#overlay.yui-overlay .hd { /*border:1px solid red;*/padding:3px; }
	#overlay.yui-overlay .bd { /*border:1px solid green;padding:5px;*/ min-height: 150px; max-height: 180px; overflow: auto; }
	#overlay.yui-overlay .ft { /*border:1px solid #000; padding-top:5px;*/ }
	.debugDetailItem1{width: 130px; margin-right: 20px;}
	.debugDetailItem2{width: 250px;}
	.debugDetailItem2 div{display:inline;}
	.closeBtn{
		position: absolute;
		top: 6px;
		right: 6px;
		width: 12px;
		height: 12px;
		font-size: 16px;
		font-weight: bold;
		line-height: 10px;
		color:#003366;
		text-align: center;
		cursor: pointer;
	}
	.bottomCloseBtn{
		position: absolute;
		bottom: 6px;
		right: 6px;
		width: 12px;
		height: 12px;
		font-size: 16px;
		font-weight: bold;
		line-height: 10px;
		color:#003366;
		text-align: center;
		cursor: pointer;
	}
	table#debugDetail tr.detailRow:hover{background: #FFCC33;}
</style>
<script type="text/javascript">
        var Dom = YAHOO.util.Dom;
        var basic;
        var dataTableInited = false;
        var magicNum = 548;
        var actionDivHeight = 0;
        var pollTimeoutId;
        var pollCount = 0;
		var formName = "clientDebug";
		var selClientEl;
		var pannelTitle = "<s:property value="%{stringForTitle}"/>";
		var supportMaxCount = <s:property value="%{maxClientMonitoringCount}"/>;

		<s:if test="%{isEnterFromTool}">
			var layoutWidth = 835;
			var layoutHeight = 600;
		</s:if>
		<s:else>
		var layoutWidth = document.body.offsetWidth;
		var layoutHeight = document.body.offsetHeight;
		</s:else>

        YAHOO.util.Event.addListener(window, "load", function() {
       	layout = new YAHOO.widget.Layout("layoutDiv",{
				width: (layoutWidth - 10),
				height: (layoutHeight - 25),
        		units: [
        				{ position: 'top', height: 185, maxHeight: 400, minHeight: 100, resize: true, body: 'clients', gutter: '0 0 5px 0' },
        				{ position: 'center', body: 'logs', gutter: '0 0 1px 0' }
        			   ]
        	});
        	 //On resize, resize the table and set the custom width on the Subject Column
        	layout.on('resize', function() {
        		if (basic.oDT) {
        			this.getUnitByPosition('top')._setWidth(Dom.get('clients'), this.getSizes().top.w);
        			basic.oDT.set('height', (this.getSizes().top.h - 34) + 'px');
        			basic.oDT.set('width', (this.getSizes().top.w) + 'px');
        			basic.oDT.setColumnWidth(basic.oDT.getColumn(statusKey), (this.getSizes().top.w - magicNum));
        			basic.oDT._syncColWidths();
        		}
        		Dom.setStyle(Dom.get('content'),'width',(this.getSizes().center.w - 34) + 'px');
        		Dom.setStyle(Dom.get('content'),'height',(this.getSizes().center.h - actionDivHeight - 42) + 'px');
        	}, layout, true);
        	layout.on('render', function() {
        		actionDivHeight = Dom.get('actions').clientHeight + 3;
        		Dom.setStyle(Dom.get('content'),'width',(this.getSizes().center.w - 34) + 'px');
        		Dom.setStyle(Dom.get('content'),'height',(this.getSizes().center.h - actionDivHeight - 42) + 'px');
        	}, layout, true);
        	layout.render();

        	if(!dataTableInited){
        		initDataTable(layout.getSizes().top.h, layout.getSizes().top.w);
        	}
        	// fetch initialize client debug data
        	fetchInitClientDebugData();
        	//save this reference on the parent
        	top.clientTraceIframeWindow = window;
        	if(pannelTitle){
        		top.updateClientDebugPanelTitle(pannelTitle);
        	}
        	
			selClientEl = setSelectEditable("selectedClient", 112);
			selClientEl.maxLength = 12;
			setupHiveAPLocationHash();
			var mapValue = Get("selectedMap").value;
			if( mapValue != "All"){
				resetHiveAPSelector(mapValue);
			}
			//resize panel, adjust the layout size
			top.debugClientPanelResizeCallback = function(){
				layout.set("width", document.body.offsetWidth - 10);
				layout.set("height", document.body.offsetHeight - 25);
				layout.resize(true);
			}
			
		    if(Get("errorDetailTable") && Get("errorDetailTable").innerHTML == '') {
		    	hm.util.hide("errDetailAnchor");
		    	hm.util.hide("errDetailBtn");
		    }
        });
		/*
		 * entry format:{actionKey:'',clientMacKey:'',apMacKey:'',statusKey:'',descKey:'',cookieIdKey:''}
		 */
        var actionKey = "<s:text name='debug.client.column.action' />";
        var clientMacKey = "<s:text name='debug.client.column.macAddress' />";
        var apMacKey = "<s:text name='debug.client.column.hiveApMac' />";
        var statusKey = "<s:text name='debug.client.column.status' />";
        var descKey = "<s:text name='debug.client.column.description' />";
        var excludeKey = "<s:text name='debug.client.column.excludeProbe' />";
        var cookieIdKey = "cookieId";
        var activeKey = "active";
        var clientMacDisplayKey = "clientMacDisplay";
        var apHostnameKey = "hostname";

        var initDataTable = function(h, w){
            basic = function() {
                var myColumnDefs = [
                    {key:actionKey, sortable:true, resizeable:false, width: 60},
                    {key:clientMacKey, sortable:true, resizeable:false, hidden: true},
                    {key:clientMacDisplayKey, sortable:true, resizeable:false, label:clientMacKey, width: 175},
                    {key:apMacKey, sortable:true, resizeable:false, hidden: true},
                    {key:apHostnameKey, sortable:true, resizeable:false, label:apMacKey, width: 130},
                    {key:statusKey, sortable:false, resizeable:false, width: (w-magicNum)},
                    {key:excludeKey, sortable:true, resizeable:false, width: 60}
                ];

                var myDataSource = new YAHOO.util.DataSource([]);
                myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                myDataSource.responseSchema = {
                    fields: [actionKey,clientMacDisplayKey,apMacKey,apHostnameKey,statusKey]
                };
        		var dtH = h - 34;
                var myDataTable = new YAHOO.widget.DataTable("clients",
                        myColumnDefs, myDataSource, {scrollable: true, width: w+"px", height: dtH+"px"});
        		myDataTable.subscribe("rowMouseoverEvent", myDataTable.onEventHighlightRow);
        		myDataTable.subscribe("rowMouseoutEvent", myDataTable.onEventUnhighlightRow);
        		myDataTable.subscribe("rowClickEvent", function(){
        				myDataTable.onEventSelectRow.apply(this, arguments);
        				selectedRowChanged();
            		});
                return {
                    oDS: myDataSource,
                    oDT: myDataTable
                };
            }();
            dataTableInited = true;
        }

        var selectedRowChanged = function(){
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
			//pollClientDebugData();
			startPollTimer();
        }

        var clientData = {clients:[]};

        var fetchInitClientDebugData = function(){
        	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
			document.forms[formName].operation.value = "fetchInitClientDebugData";
        	ajaxRequest(formName, url,processInitClientData);
        }

        var processInitClientData = function(o){
        	eval("var clientData = " + o.responseText);
        	updateClientData(clientData);
        	if(clientData && clientData.length > 0){
        		//select client if needed
        		var initSelectMac = "<s:property value="%{clientMac}"/>";
        		if(initSelectMac){
        			var initSelectedRow = getClientRow(initSelectMac);
            	}
        		// set the checkbox status
        		if(clientData[0].perfOn) {
        			Get("clientPerformance").checked = true;
        		}
            }else{
            	showAddDiv();
            }
            if(initSelectedRow){
            	basic.oDT.selectRow(initSelectedRow);
            }
        	//start polling client debug data
        	pollClientDebugData();
        }

        var wrapDescription = function(desc, entryKey){
        	desc = desc || "";
        	return "<div class='description' id='tt_"+entryKey+"'>"+desc+"</div>"
        }

        var startPollTimer = function(){
        	var interval = 5;        // seconds
        	var duration = <s:property value="%{sessionTimeOut}" /> * 60;  // minutes * 60
        	var total = duration / interval;
        	if (pollCount++ < total) {
        		pollTimeoutId = setTimeout("pollClientDebugData()", interval * 1000);  // seconds
        	}
        }

        var pollClientDebugData = function(){
            if(pollClientDebugData.pollingProcess){
                return;
            }
        	pollClientDebugData.pollingProcess = true;//set the flag, avoid multiple requests
        	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
			var val = Get("viewType_sel").value;
			var val1 = Get("filterType_sel").value;
			var val2 = Get("rowLimit_sel").value;
			var selectedClientMacs = getSelectClients();
			var selectedStages = getSelectStages();
			document.forms[formName].operation.value = "pollClientDebugData";
        	document.forms[formName].viewType.value = val;
        	document.forms[formName].filterType.value = val1;
        	document.forms[formName].rowLimit.value = val2;
        	document.forms[formName].clientMacs.value = selectedClientMacs;
        	document.forms[formName].stageNames.value =selectedStages; 
        	ajaxRequest(formName, url,processDebugData);
        }

        var processDebugData = function(o){
        	eval("var debugData = " + o.responseText);
        	updateClientData(debugData.clients);
        	updateLogData(debugData.logs);
        	fillDebugDetails();
        	pollClientDebugData.pollingProcess = false;//reset the flag
        	if(!pollClientDebugData.isPaused){
        		startPollTimer();
            }
        }

        var updateClientData = function(clients){
            if(clients && clients.length > 0){
           		for(var i=0; i<clients.length; i++){
           			var client = clients[i];
           			var mac = client[clientMacKey];
           			var apMac = client[apMacKey];
           			var msg = client[descKey] || "";
           			
           			if(client.start_failed) {
           				addErrorDetailRow(mac+"<br/>"+"("+client.hostname+")", client.msg);
           				continue;
           			}

   					var entryKey = mac + "|" + apMac;
           			if(!clientHash[entryKey]){//not exist, add in
           				clientHash[entryKey] = {};
           			}
           			updateClient(client);//update desc, action, rate
           		}
            	updateTableRows();
            }
        }

        var updateLogData = function(logs){
            if(logs != undefined){
        		var value = "";
        		for(var row in logs){
        			if(logs[row].v != undefined){
        				value += logs[row].v;
        			}
        		}
        		Dom.get('content').innerHTML = "<pre>"+value+"</pre>";
            }
        }

        var getSelectedClient = function(){
        	var row = basic.oDT.getSelectedRows()[0];
        	if(null != row && null != basic.oDT.getRecordSet().getRecord(row)){
        		return basic.oDT.getRecordSet().getRecord(row);
        	}
        	return null;
        }

        /*client must be unique in the table*/
        var getClientRow = function(clientMac){
        	if(null == clientMac){
        		return null;
        	}
        	var recordSet = basic.oDT.getRecordSet();
        	if(recordSet != null){
        		for(var i=0; i<recordSet.getLength(); i++){
        			var macAddress = recordSet.getRecord(i)._oData[clientMacKey];
        			if(clientMac == macAddress){
        				return recordSet.getRecord(i);
        			}
        		}
        	}
        	return null;
        }

        /*get client entries in client hash*/
        var getDebugEntries = function(clientMac){
        	var debugEntries = new Array();
        	if(null == clientMac){
        		return debugEntries;
        	}
			for(var key in clientHash){
				if(key.indexOf(clientMac)==0){
					debugEntries.push(clientHash[key]);
				}
			}
			return debugEntries;
        }

        var updateActionValue = function(clientMac, apMacs, actionStatus){
        	if(null==clientMac || null==apMacs){
        		return;
        	}
        	for(var i=0; i<apMacs.length; i++){
        		var entryKey = clientMac + "|" + apMacs[i];
        		clientHash[entryKey][actionKey] = actionStatus;
        	}
        }

        var startClientDebugRequest = function(clientMac){
        	if (null != clientMac) {
        		var successCheckStartClientDebug=function(o){
        			try {
        				eval("var data = " + o.responseText);
        			}catch(e){
        				showWarnDialog("Start client debug Error. Session timeout.", "Error");
        				return false;
        			}

        			if (data.t) {
        				startClientDebugRequestReal(clientMac);
        			} else {
        				showWarnDialog("Wired client doesn't support Client Monitor.", "Error");
        				return false;
        			}
        		}
        		
        		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>'
      			  + "?clientMac=" + clientMac + "&operation=checkStartClientDebugs&ignore="+ + new Date().getTime();

        		var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : successCheckStartClientDebug, timeout: 60000});
        	}
        }
        
       var startClientDebugRequestReal = function(clientMac){	
        	if(null != clientMac){
    			clearTimeout(pollTimeoutId);
                var entries = getDebugEntries(clientMac);
                var apMacs = new Array();
                for(var i=0; i<entries.length; i++){
                	apMacs.push(entries[i][apMacKey]);
                }
        		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>'
        			  + "?clientMac=" + clientMac + "&ignore="+ + new Date().getTime();
        		document.forms[formName].operation.value = "startClientDebugs";
				ajaxRequest(formName, url,startClientDebugProcess);
        		//update hash
        		var actionStatus = actionValues.starting();
        		updateActionValue(clientMac, apMacs, actionStatus);
				//update tabe row
        		var row = getClientRow(clientMac);
    			updateRowActionColumn(row,actionStatus);
    			updateRowClientColumn(row, clientMac);
    			//hide detail overlay
    			//if(null != overlay){overlay.hide();}
        	}
        }

        var clientHash = new Array();
        var startClientDebugProcess = function(o){
        	eval("var data = " + o.responseText);
        	if(data.length){
        		for(var i=0; i<data.length; i++){
        			updateClient(data[i]);
        		}
        	}
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
        	startPollTimer();
        }

        var stopClientDebugRequest = function(clientMac, apMac, cookieId){
        	if(null != clientMac){
    			clearTimeout(pollTimeoutId);
				var entries = getDebugEntries(clientMac);
                var apMacs = new Array();
                var cookieIds = new Array();
                for(var i=0; i<entries.length; i++){
                	if(entries[i][cookieIdKey]){
		               	apMacs.push(entries[i][apMacKey]);
                		cookieIds.push(entries[i][cookieIdKey]);
                	}
                 }
        		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?clientMac=" + clientMac
        				+ "&ignore="+ + new Date().getTime();
				document.forms[formName].apMacs.value = apMacs;
				document.forms[formName].cookieIds.value = cookieIds;
				document.forms[formName].operation.value="stopClientDebugs";
        		ajaxRequest(formName, url, stopClientDebugProcess, 'post');
        		
                // reset this two fields to avoid HTTP 400
                document.forms[formName].apMacs.value = '';
                document.forms[formName].cookieIds.value = '';
                
				//update hash
        		var actionStatus = actionValues.stopping();
        		updateActionValue(clientMac, apMacs, actionStatus);
				//update tabe row
        		var row = getClientRow(clientMac);
    			updateRowActionColumn(row,actionStatus);
    			updateRowClientColumn(row, clientMac)
    			//hide detail overlay
    			//if(null != overlay){overlay.hide();}
        	}
        }

        var stopClientDebugProcess = function(o){
        	eval("var data = " + o.responseText);
        	if(data.length){
        		for(var i=0; i<data.length; i++){
        			updateClient(data[i]);
        		}
        	}
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
        	startPollTimer();
        }

        var updateClient = function(data){
			var entryKey = data[clientMacKey] + "|" + data[apMacKey];
			clientHash[entryKey][clientMacKey] = data[clientMacKey];
			clientHash[entryKey][apMacKey]= data[apMacKey];
			clientHash[entryKey][excludeKey]= data[excludeKey];
        	if(data.stop_failed){
        		clientHash[entryKey][actionKey] = actionValues.stop(data[clientMacKey]);
        	}else if(data.stopping){
        		clientHash[entryKey][actionKey] = actionValues.stopping();
        	}else if(data.stop_suc){
        		clientHash[entryKey][actionKey] = actionValues.start(data[clientMacKey]);
        	}else if(data.start_failed){
        		clientHash[entryKey][actionKey] = actionValues.start(data[clientMacKey]);
        	}else if(data.starting){
        		clientHash[entryKey][actionKey] = actionValues.starting();
        	}else if(data.start_suc){
        		clientHash[entryKey][actionKey] = actionValues.stop(data[clientMacKey]);
        	}else if(data.aborted){
        		clientHash[entryKey][actionKey] = actionValues.start(data[clientMacKey]);
        	}else {
        		clientHash[entryKey][actionKey] = actionValues.start(data[clientMacKey]);
        	}
        	if(data[activeKey]){
        		clientHash[entryKey][activeKey] = data[activeKey];
        	}else{
        		delete clientHash[entryKey][activeKey];
        	}
        	if(data.msg){
            	var wrappedMsg = wrapDescription(data.msg, entryKey);
            	clientHash[entryKey][descKey] = wrappedMsg;
            }
        	if(data[apHostnameKey]){
        		clientHash[entryKey][apHostnameKey] = data[apHostnameKey];
        	}
        	if(data.cookieId){
        		clientHash[entryKey][cookieIdKey] = data.cookieId;
            }
            if(data[statusKey]){
            	clientHash[entryKey][statusKey] = data[statusKey];
            }
        }

        var clearLogs = function(){
        	clearTimeout(pollTimeoutId);
        	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
			var val = Get("viewType_sel").value;
			var val1 = Get("filterType_sel").value;
			var val2 = Get("rowLimit_sel").value;
			var selectedClientMacs = getSelectClients();
			document.forms[formName].operation.value = "clearDebugLogs";
        	document.forms[formName].viewType.value = val;
        	document.forms[formName].filterType.value = val1;
        	document.forms[formName].rowLimit.value = val2;
        	document.forms[formName].clientMacs.value = selectedClientMacs;
        	ajaxRequest(formName, url, processDebugData);
        }

        var recoverAPs = function(self, clientMac, recoverAPs){
            if(clientMac && recoverAPs){
        		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?apMacs=" + recoverAPs + "&clientMac=" + clientMac + "&ignore="+ + new Date().getTime();
        		document.forms[formName].operation.value = "startClientDebugs";
				ajaxRequest(formName, url, function(){/*dummy*/});
				self.parentNode.innerHTML = "";
            }
        }

        var updateTableRows = function(){
        	/* client info:{count:m,stopCount:n,startCount:l,activeItem:activeItem} */
        	var clientInfos = {};
        	for(var key in clientHash){
        		var client = clientHash[key];
        		var clientMac = client[clientMacKey];
        		var exclude = client[excludeKey];
        		if(!clientInfos[clientMac]){
        			clientInfos[clientMac] = {count:0, stopCount:0, startCount:0, excludeProbe:exclude};
        		}
        		clientInfos[clientMac].count++;
        		if(client[activeKey]&& !clientInfos[clientMac].activeItem){
        			clientInfos[clientMac].activeItem = client;
        		}
        		if(client[actionKey]==actionValues.start(clientMac)){
        			clientInfos[clientMac].startCount++;
        		}else if(client[actionKey]==actionValues.stop(clientMac)){
        			clientInfos[clientMac].stopCount++;
        		}
        	}
        	// update table values
        	for(var mac in clientInfos){
        		if(mac != "undefined"){
	        		var clientInfo = clientInfos[mac];
	        		var row = getClientRow(mac);
	        		if(!row){
						var client_tb = {};
						client_tb[actionKey] = clientInfo[actionKey];
						client_tb[clientMacKey] = mac;
						client_tb[clientMacDisplayKey] = getDebugDetail(mac);
	            		basic.oDT.addRow(client_tb);
	            	}
	        		row = getClientRow(mac);
	        		if(!row){continue;}
	        		if(clientInfo.activeItem){
	        			updateRowStatusColumn(row, clientInfo.activeItem[statusKey]);
	        			updateRowHiveApColumn(row, clientInfo.activeItem[apMacKey], clientInfo.activeItem[apHostnameKey])
	        		}
	        		if((row.getData(actionKey)==actionValues.stopping() && (clientInfo.stopCount == clientInfo.count)) || (row.getData(actionKey)!=actionValues.stopping() && clientInfo.stopCount)){
	    				updateRowActionColumn(row, actionValues.stop(mac));
	    				updateRowClientColumn(row, mac);
	    				if(!clientInfo.activeItem){
	    					updateRowStatusColumn(row, " ");// set an initial state
	    					updateRowHiveApColumn(row, " ");//reset to blank
	    				}
	    			}else if((row.getData(actionKey)==actionValues.starting() && (clientInfo.startCount == clientInfo.count)) || (row.getData(actionKey)!=actionValues.starting()&&clientInfo.startCount)){
	    				updateRowActionColumn(row, actionValues.start(mac));
	    				updateRowClientColumn(row, mac);
	    			}
	    			// update exclude probe column
	    			updateRowFilterProbeColumn(row, mac, clientInfo.excludeProbe, row.getData(actionKey)!=actionValues.start(mac));
        		}
        	}
        }

        var updateRowFilterProbeColumn = function(row, clientMac, isExcludeProbe, disabled){
            var columnHtml = isExcludeProbe?
            		"<input type='checkbox' checked='checked' onclick='processFilterProbe(this, \""+clientMac+"\")' " + (disabled? "disabled":"") + " />"
            		:"<input type='checkbox' onclick='processFilterProbe(this, \""+clientMac+"\")' " + (disabled? "disabled":"") + "/>";
            if(row){
            	basic.oDT.updateCell(row, excludeKey,columnHtml);
            }
        }

        var updateRowDescColumnMsg = function(row, msg){
        	if (row && (msg != null)) {
        		var descEl = basic.oDT.getTdLinerEl({
        			record: row,
        			column: basic.oDT.getColumn(descKey)
        		});
        		if (descEl.firstChild) {
        			descEl.firstChild.innerHTML = msg;
        		}
        	}
        }

        var updateRowClientColumn = function(row, clientMac){
        	if (row && clientMac) {
        		var clientEl = basic.oDT.getTdLinerEl({
        			record: row,
        			column: basic.oDT.getColumn(clientMacDisplayKey)
        		});
        		var actionEl = basic.oDT.getTdLinerEl({
        			record: row,
        			column: basic.oDT.getColumn(actionKey)
        		});
        		var debugEntries = getDebugEntries(clientMac);
        		// show clickable button only in case of action is starting or stop.
        		var actionText = actionEl.innerHTML;
        		if(actionText.toLowerCase()==actionValues.starting().toLowerCase() || actionText.toLowerCase()==actionValues.stop(clientMac).toLowerCase()){
            		//calculate count of total and not started yet.
            		var unStartedClientAP = [];
            		for(var i in debugEntries){
                		var client = debugEntries[i];
                		if(client[actionKey]==actionValues.start(clientMac)){
                			unStartedClientAP.push(client[apMacKey]);
                		}
                	}
                	
                	if(unStartedClientAP.length > 0){
                    	var str = "(<span class=\"unStarted\">"+unStartedClientAP.length+"</span>"+" / <span class=\"total\">"+debugEntries.length+"</span> <a class='recover' href='javascript:void(0);' onclick='recoverAPs(this, \""+clientMac+"\",\""+unStartedClientAP+"\")'>recover</a>)";
                		clientEl.getElementsByTagName("span")[0].innerHTML = str;
                    }else{
                    	clientEl.getElementsByTagName("span")[0].innerHTML = "";
                    }
            	}else{
            		clientEl.getElementsByTagName("span")[0].innerHTML = "";
                }
        	}
        }

        var updateRowActionColumn = function(row, action){
        	if(row && action){
       			basic.oDT.updateCell(row, actionKey,action);
        	}
        }

		var updateRowStatusColumn = function(row, status){
			if(row && status){
       			basic.oDT.updateCell(row, statusKey,status);
        	}
		}

		//hiveAP mac is hidden, just show hiveAP hostname
		var updateRowHiveApColumn = function(row, hiveApMac, hiveApHostname){
        	if(row && hiveApMac){
       			basic.oDT.updateCell(row, apMacKey,hiveApMac);
       			basic.oDT.updateCell(row, apHostnameKey,hiveApHostname||hiveApMac);
        	}
        }

        var actionValues = {
        	start: function(clientMac){
        		return "<a href=\"javascript:startClientDebugRequest('"+ clientMac + "')\">Start</a>";
        	}
        	,starting: function(){
        		return "Starting";
        	}
        	,stop: function(clientMac, apMac, cookieId){
        		return "<a href=\"javascript:stopClientDebugRequest('"+ clientMac +"')\">Stop</a>";
        	}
        	,stopping: function(){
        		return "Stopping";
        	}
        }

        function processFilterProbe(cbx, clientMac){
    		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?clientMac=" + clientMac + "&excludeProbe=" + cbx.checked + "&ignore="+ + new Date().getTime();
			document.forms[formName].operation.value="excludeProbe";
			ajaxRequest(formName, url, function(){/*dummy*/});
        }

        var getDebugDetail = function(clientMac){
        	return "<a href=\"javascript:void(0);\" onclick=\"javascript:openDebugDetailOverlay('"+clientMac+"',event)\" >"+clientMac+"</a> <span></span>";
        }

        function openDebugDetailOverlay(clientMac,e){
        	e = e || window.event;
        	var x = e.clientX;
        	var y = e.clientY;
			if(null == overlay){
				createDebugDetailOverlay();
			}
			overlay.showClient = clientMac;
			fillDebugDetails();
			overlay.cfg.setProperty("x", x+20);
			overlay.cfg.setProperty("y", y+22);
			overlay.header.firstChild.innerHTML = '<s:text name="debug.client.column.macAddress" />' + " - " + clientMac
			overlay.show();
        }

        var overlay = null;
        function createDebugDetailOverlay(){
        	var div = Get("overlay");
        	overlay = new YAHOO.widget.Overlay(div, {width: "400px"});
			overlay.render(document.body);
			div.style.display="";
        }

        function fillDebugDetails(){
			if(overlay == null || overlay.cfg.getProperty("visible")==false){
				return;
			}
			var clientMac = overlay.showClient;
        	var detailTable = Get("debugDetail");
			var rowCount = detailTable.rows.length;
			//remove old rows
			for(var i=rowCount-1; i > 0; i--){
				detailTable.deleteRow(i);
			}
        	var entries = getDebugEntries(clientMac);
        	for(var i=0; i<entries.length; i++){
        		var apMac = entries[i][apMacKey];
        		var apHostname = entries[i][apHostnameKey];
        		var desc = entries[i][descKey];
        		var row = detailTable.insertRow(i+1);
        		row.className = "detailRow";
        		var cell = row.insertCell(0);
				cell.className = 'list';
				var apLabel = apHostname?apMac+"<br />"+"("+apHostname+")":apMac;
				cell.innerHTML = apLabel;
				var cell = row.insertCell(1);
				cell.className = 'list';
				cell.innerHTML = desc;
        	}
        }
		function pauseLogs(btn){
			btn.value = btn.value == "Pause" ? "Continue" : "Pause";
			pollClientDebugData.isPaused = !pollClientDebugData.isPaused;
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
			pollClientDebugData();
		}
		function exportLogs(){
			var selectedClientMacs = getSelectClients();
			document.forms[formName].operation.value = 'export';
			document.forms[formName].clientMacs.value = selectedClientMacs;
			document.forms[formName].submit();
		}
		function viewTypeChange(viewType){
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
			pollClientDebugData();
		}
		function filterTypeChange(){
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
			pollClientDebugData();
		}
		function rowLimitChange(){
			clearTimeout(pollTimeoutId);
			pollCount = 0;//reset pollCount;
			pollClientDebugData();
		}
		function getSelectClients(){
			var clients = new Array();
			var selRows = basic.oDT.getSelectedRows();
			var recordSet = basic.oDT.getRecordSet();
			if(selRows.length){
				for(var j=selRows.length; j>0; j--){
					var row = recordSet.getRecord(selRows[j-1]);
					if(row){
						var clientMac = row.getData(clientMacKey);
						clients.push(clientMac);
					}
				}
			}
			return clients;
		}
		/* manual add client function */
		var clientAddPanel = null
		var anim = null;
		function removeClient(){
			var selRows = basic.oDT.getSelectedRows();
			var recordSet = basic.oDT.getRecordSet();
			if(selRows.length){
				var clientMacs = new Array();
				for(var j=selRows.length; j>0; j--){
					var row = recordSet.getRecord(selRows[j-1]);
					if(row){
						var clientMac = row.getData(clientMacKey);
						//remove entries from hash which client mac is current one
						var entries = getDebugEntries(clientMac);
						for(var i=0; i<entries.length; i++){
							var key = entries[i][clientMacKey] + "|" + entries[i][apMacKey];
							delete clientHash[key];
						}
						clientMacs.push(clientMac);
						basic.oDT.deleteRow(selRows[j-1]);
					}
				}
				//remove clientMacs
				if(clientMacs.length){
					try{
						var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?clientMacs=" + clientMacs + "&ignore="+ + new Date().getTime();
		        		document.forms[formName].operation.value = "closeDebugClients";
						ajaxRequest(formName, url,function(){/*dummy*/});
					}catch(e){}
				}
			}
		}
		function removeClients(){
			var recordSet = basic.oDT.getRecordSet();
			var length = recordSet.getLength();
			if(length){
				var clientMacs = new Array();
				for(var j=length-1; j>=0; j--){
					var row = recordSet.getRecord(j);
					if(row){
						var clientMac = row.getData(clientMacKey);
						//remove entries from hash which client mac is current one
						var entries = getDebugEntries(clientMac);
						for(var i=0; i<entries.length; i++){
							var key = entries[i][clientMacKey] + "|" + entries[i][apMacKey];
							delete clientHash[key];
						}
						clientMacs.push(clientMac);
						basic.oDT.deleteRow(j);
					}
				}
				//remove clientMacs
				if(clientMacs.length){
					try{
						var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?clientMacs=" + clientMacs + "&ignore="+ + new Date().getTime();
		        		document.forms[formName].operation.value = "closeDebugClients";
						ajaxRequest(formName, url,function(){/*dummy*/});
					}catch(e){}
				}
			}
			this.hide();
		}
		function showAddDiv(){
			if(null == clientAddPanel){
				createAddDiv();
			}
			<s:if test="%{!isEnterFromTool}">
				anim.attributes = {  points: {from: [-350, 0], to: [0, 0] } };
				anim.animate();
			</s:if>
			<s:else>
				var x = Dom.getX("layoutDiv");
				var y = Dom.getY("layoutDiv");
				clientAddPanel.cfg.setProperty("xy", [x, y+30]);
			</s:else>
			clientAddPanel.show();
		}
		function createAddDiv(){
			clientAddPanel = new YAHOO.widget.Overlay("newClientDiv",{ visible: false, context:["newLinkDiv","tl","bl"]});
			clientAddPanel.render(document.body);
			Get("newClientDiv").style.display = "";
            anim = new YAHOO.util.Motion('newClientDiv');
		}
		function hiddenAddDiv(){
			<s:if test="%{!isEnterFromTool}">
				anim.attributes = { points: { to: [-350, 0] } };
				anim.animate();
			</s:if>
			<s:else>
				clientAddPanel.hide();
			</s:else>
		}
		/*-------- Create an Object for URL param ---------*/
        function ClientParaObj() {
            this.clients = false;
            this.hiveAPs = false;
            this.maps = false;
            var _count = 0;
            
            var _selectedMap = function() { return Get("selectedMap").value; }
            var _selectedClient = function() { return Get(selClientEl).value; }
            this.selectedAllClients = function() { 
            	this.clients = "All";
            	this.maps = _selectedMap();
            }
            this.selectedAllAPs = function() {
            	this.hiveAPs = "All"; 
            	this.maps = _selectedMap();
            	this.clients = _selectedClient();
            }
            this.isSelectedAllOption = function() { return this.clients || this.hiveAPs; }
            this.increaseCount = function() { _count++; }
            this.getCount = function() { return _count; }
            this.getStringValue = function() { return this.clients + "," + this.maps + "," + this.hiveAPs; }
        }
        var addClientParams = {};
        
		function addNewClient(){
			if(!validateInput()){
				return;
			}
			var newClients = validateSelection();
			if(newClients){
				addNewClients(newClients);
			}else{
				return;
			}
		}

		function addNewClients(newClients){
			if(newClients.length>0){
				var newing = new Array();//for table values;
				var newClientAP = new Array();
				var tmp = tmp || {};
				for(var i = 0; i<newClients.length; i++){
					var client = newClients[i];
					var clientMac = client[clientMacKey];
					var apMac = client[apMacKey];
					var entryKey = clientMac + "|" + apMac;
					client[actionKey] = client[actionKey] || actionValues.start(clientMac);
					client[descKey] = client[descKey] || wrapDescription("",entryKey);

					newClientAP.push(entryKey);

					//for adding to table
					if(tmp[clientMac]){continue;}//exist in previous.
					if(getClientRow(clientMac)){continue;}//exist in table;
					var client_tb = {};
					client_tb[actionKey] = client[actionKey];
					client_tb[clientMacKey] = clientMac;
					client_tb[clientMacDisplayKey] = getDebugDetail(clientMac);
					newing.push(client_tb);
					tmp[clientMac] = client;
					
					addClientParams.increaseCount();
				}

       			if(addClientParams.isSelectedAllOption()) {
       				var newCount = addClientParams.getCount();
                    var allOptions = addClientParams.getStringValue();
                    
                    prepareInitializeClient(newCount, allOptions);
       			} else {
					prepareInitializeClient(newing.length, newClientAP);
        		}
        	}
		}
		
		function prepareInitializeClient(newCount, clientMacs) {
            /*new+exist > supportMaxCount return*/
            if(newCount && (basic.oDT.getRecordSet().getLength()+newCount) > supportMaxCount){
                var message = '<s:text name="error.hiveap.debug.client.monitor.reachMaxClientsOneGroup"><s:param>'
                    +supportMaxCount+'</s:param></s:text>';
                hm.util.reportFieldError(Get("infoTag"),message);
                return;
            }
            //for(var key in newingHash){
            //  clientHash[key] = newingHash[key];
            //}
            //if(newing.length){basic.oDT.addRows(newing);}

            var enablePerf = false;
            if(Get("clientPerformance").checked) {
                enablePerf = true;
            }
            // Do not add directly, just send to server side, update the table from the callback function
            var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?enablePerformance=" + enablePerf 
            		+ "&ignore="+ + new Date().getTime();
            document.forms[formName].operation.value = "initializeClientDebugs";
            document.forms[formName].clientMacs.value = clientMacs;
            ajaxRequest(formName, url, processInitializeClient);			
		}

        var processInitializeClient = function(o){
        	eval("var initalteClient = " + o.responseText);
        	if(initalteClient.length == 1 && initalteClient[0].start_failed) {
        		hm.util.reportFieldError(Get("infoTag"), initalteClient[0].msg);
        	} else {
        		hiddenAddDiv();
        		updateClientData(initalteClient);
        	}
        }
        
		function validateInput(){
			if(Get(selClientEl).value != "All"){
				var rl = checkMacAddress(Get(selClientEl), '<s:text name="debug.client.column.macAddress" />');
				if(!rl){
					return false;
				}
			}
			if(Get("selectedHiveAp").value != "All"){
				var rl = checkMacAddress(Get("selectedHiveAp"), '<s:text name="debug.client.column.hiveApMac" />');
				if(!rl){
					return false;
				}
			}
			return true;
		}
		function validateSelection(){
			var tempNewClient = new Array();
			addClientParams = new ClientParaObj();
			
			if(Get(selClientEl).value != "All" && Get("selectedHiveAp").value == "All"){
				//hiveAp all
				var clientValue = Get(selClientEl).value;
				var hiveApList = Get("selectedHiveAp").options;
				for(var i=0; i<hiveApList.length; i++){
					var hiveApValue = hiveApList[i].value;
					var hiveApText = hiveApList[i].text;
					if("All" != hiveApValue){
						var rst = validateClientInfo(clientValue, hiveApValue, hiveApText);
						if(!rst){
							return false;
						}else{
							tempNewClient.push(rst);
						}
					}
				}
				addClientParams.selectedAllAPs();

			}else if(Get(selClientEl).value != "All"){
				//neither all
				var selectedOption = Get("selectedHiveAp").options[Get("selectedHiveAp").selectedIndex];
				var rst = validateClientInfo(Get(selClientEl).value, selectedOption.value, selectedOption.text);
				if(!rst){
					return false;
				}else{
					tempNewClient.push(rst);
				}
			}else if(Get("selectedHiveAp").value != "All"){
				//client all
				var selectedOption = Get("selectedHiveAp").options[Get("selectedHiveAp").selectedIndex];
				var hiveApValue = selectedOption.value;
				var hiveApText = selectedOption.text;
				var clientList = Get("selectedClient").options;
                if(clientList.length == 0) {
                    hm.util.reportFieldError(Get("infoTag"), 
                            "No items were found in Client list.");
                    return false;
                }				
				for(var i=0; i<clientList.length; i++){
					var clientValue = clientList[i].value;
					if("All" != clientValue){
						var rst = validateClientInfo(clientValue, hiveApValue, hiveApText);
						if(!rst){
							return false;
						}else{
							tempNewClient.push(rst);
							// set the specific HiveAP
							addClientParams.hiveAPs = hiveApValue;
						}
					}
				}
				addClientParams.selectedAllClients();
			}else{
				//both all
				var hiveApList = Get("selectedHiveAp").options;
				var clientList = Get("selectedClient").options;
                if(clientList.length == 0) {
                    hm.util.reportFieldError(Get("infoTag"), 
                            "No items were found in Client list.");
                    return false;
                }				
				for(var i=0; i<clientList.length; i++){
					var clientValue = clientList[i].value;
					if("All" != clientValue){
						for(var j=0; j<hiveApList.length; j++){
							var hiveApValue = hiveApList[j].value;
							var hiveApText = hiveApList[j].text;
							if("All" != hiveApValue){
								var rst = validateClientInfo(clientValue, hiveApValue, hiveApText);
								if(!rst){
									return false;
								}else{
									tempNewClient.push(rst);
								}
							}
						}
					}
				}
				addClientParams.selectedAllClients();
				addClientParams.selectedAllAPs();
			}
			return tempNewClient;
		}
		function validateClientInfo(clientMac, apMac, apHostname){
			var entryKey = clientMac.toUpperCase() +"|"+ apMac.toUpperCase();
			if(clientHash[entryKey]){
				hm.util.reportFieldError(Get("infoTag"), "The combination of Client-<s:text name='hiveAp.tag' /> {"+clientMac+"-"+apHostname+"} has already existed.");
				return false;
			}
			var newClient = {};
			newClient[clientMacKey] = clientMac.toUpperCase();
			newClient[apMacKey] = apMac.toUpperCase();
			return newClient;
		}
		function checkMacAddress(el, label){
			if(el.value.length == 0){
		        hm.util.reportFieldError(Get("infoTag"), '<s:text name="error.requiredField"><s:param>'+label+'</s:param></s:text>');
		        el.focus();
		        return false;
			} else if (!hm.util.validateMacAddress(el.value, 12)) {
				hm.util.reportFieldError(Get("infoTag"), '<s:text name="error.formatInvalid"><s:param>'+label+'</s:param></s:text>');
				el.focus();
				return false;
			}
			return true;
		}

		function setSelectEditable(sel, selwidth){
			sel = (typeof sel == "string") ? document.getElementById(sel):sel
			selwidth = selwidth || 50;
			var div = document.createElement("div");
			div.style.position = "relative";
			var span = document.createElement("span");
			span.style.marginLeft = (selwidth-18) + "px";
			span.style.width = "18px";
			span.style.overflow = "hidden";
			div.appendChild(span);
			var sel1 = sel.cloneNode(true);
			sel1.style.marginLeft = -(selwidth-18) + "px";
			sel1.style.width = selwidth + "px";
			sel1.onclick = function(){this.parentNode.nextSibling.value = this.value;};
			span.appendChild(sel1);
			var input = document.createElement("input");
			input.type = "text";
			input.className = "selectInput";
			input.style.width = (selwidth-32) + "px";	
			input.onfocus = function(){this.select();}
			div.appendChild(input);
			sel.parentNode.replaceChild(div, sel);
			if(sel.options.length){
				input.value = sel.options[0].value;
			}
			return input;
		}

		var debugInfoArray = new Array();
		function setupHiveAPLocationHash(){
			var infos = document.getElementsByName("clientDebugInfos");
			for(var i=0; i<infos.length; i++){
				var info = infos[i].value;
				var values = info.split("|");
				var map = values[0] || "";
				var apMac = values[1] || "";
				var apHostname = values[2] || "";
				debugInfoArray[i] = [map,apMac,apHostname];
			}
		}
		function mapSelectionChange(selectEl){
			resetHiveAPSelector(selectEl.value);
		}
		function resetHiveAPSelector(mapValue){
			var hiveApEl = Get("selectedHiveAp");
			hiveApEl.options.length = 0;
			var tempHiveAp = {};
			for(var i=0; i<debugInfoArray.length; i++){
				var entry = debugInfoArray[i];
				if((mapValue != "All" && mapValue != entry[0])||""==entry[1] || tempHiveAp[entry[1]]){continue;}
				var op = new Option(entry[2],entry[1]);
				tempHiveAp[entry[1]] = entry[1];
				try{
					hiveApEl.add(op,null);//DOM
				}catch(e){
					hiveApEl.add(op);//IE
				}
			}
			if(hiveApEl.options.length>1){
				var op = new Option("All","All");
				try{
					hiveApEl.add(op,hiveApEl.options[0]);//DOM
				}catch(e){
					hiveApEl.add(op,0);//IE
				}
			}
			//reset default value;
			if(hiveApEl.options.length > 0){
				hiveApEl.selectedIndex = 0;
			}
		}
		var debugClientHash = function(){
			var count = 0;
			var t= "[\n";
			for(var entry in clientHash){
				count++;
				t += "{\n" + entry + ":\n{";
				var client = clientHash[entry];
				for(var item in client){
					t += item + ":" + client[item]+"\n";
				}
				t += "}\n}\n";
			}
			t += "]\n";
			return "Entry count:" + count + ", detail info:\n" + t;
		}
		
		function enablePerformanceMonitor(flag) {
			var cancelRemoveFn = function() {
				Get("clientPerformance").checked = !flag;
				this.hide();
			}
            var length = basic.oDT.getRecordSet().getLength();
            if(length > 0) {
				var confDialog = userDefinedConfirmDialog("The existing monitoring APs will be moved because you're changing the monitor type.<br>Are you sure to continue?",
		                [ { text:"Yes", handler:removeClients, isDefault:true },{ text:"&nbsp;No&nbsp;", handler:cancelRemoveFn } ], 
		                "Warning");
				confDialog.show();
            }
		}


function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

var moreLogPanel = null;
function showOutdatedDiv(){
	if(null == moreLogPanel){
		createMoreLogPanel();
	}
	<s:if test="%{!isEnterFromTool}">
	var el = document.getElementById("newLinkDiv").getElementsByTagName("a")[2];
	var x = Dom.getX(el)+120;
	var y = Dom.getY(el)+2;
	</s:if>
	<s:else>
	var x = Dom.getX("layoutDiv")+255;
	var y = Dom.getY("layoutDiv")+36;
	</s:else>
	moreLogPanel.cfg.setProperty("xy", [x, y]);
	moreLogPanel.show();
}

function createMoreLogPanel(){
	moreLogPanel = new YAHOO.widget.Overlay("moreLogs",{ visible: true, width: "240px"});
	moreLogPanel.render(document.body);
	Get("moreLogs").style.display = "";
}

function exportOutdatedLogs(){
	var selectedClientMacs = [];
	var els = document.getElementsByName("outdatedClientMacs");
	for(var i=0; i<els.length; i++){
		if(els[i].checked){
			selectedClientMacs.push(els[i].value);
		}
	}
	if(selectedClientMacs.length ==0){
		hm.util.reportFieldError(Get("logsInfo"), "Please select at least one item.");
		return;
	}
	document.forms[formName].operation.value = 'export';
	document.forms[formName].clientMacs.value = selectedClientMacs;
	document.forms[formName].submit();
}

function removeOutdatedLogs(){
	var selectedClientMacs = [];
	var els = document.getElementsByName("outdatedClientMacs");
	for(var i=0; i<els.length; i++){
		if(els[i].checked){
			selectedClientMacs.push(els[i].value);
		}
	}
	if(selectedClientMacs.length ==0){
		hm.util.reportFieldError(Get("logsInfo"), "Please select at least one item.");
		return;
	}
	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?operation=removeLogs&clientMacs="+selectedClientMacs+"&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, removeOutdatedLogResult);
}

function removeOutdatedLogResult(o){
	eval("var result = " + o.responseText);
	if(result.suc){
		var trIds = [];
		var els = document.getElementsByName("outdatedClientMacs");
		for(var i=0; i<els.length; i++){
			if(els[i].checked){trIds.push(els[i].parentNode.parentNode.id)}
		}
		var table = document.getElementById("outdatedLogsTable");
		for(var i=table.rows.length; i>0; i--){
			var trid = table.rows[i-1].id;
			for(var ii in trIds){
				if(trid == trIds[ii]){
					table.deleteRow(i-1);
				}
			}
		}
	}else if(result.msg){
		hm.util.reportFieldError(Get("logsInfo"), result.msg);
	}else{
		hm.util.reportFieldError(Get("logsInfo"), "Remove item failed.");
	}
}

function selectOutdatedLogs(checked){
	var els = document.getElementsByName("outdatedClientMacs");
	for(var i=0; i<els.length; i++){
		els[i].checked = checked;
		els[i].parentNode.parentNode.style.backgroundColor = checked?"#FFCC33":"#fff";
	}
}

function selectOutdatedLog(cbx){
	cbx.parentNode.parentNode.style.backgroundColor = cbx.checked?"#FFCC33":"#fff";
}

function hideLogsPanel(){
	if(null != moreLogPanel){
		moreLogPanel.hide();
		var els = document.getElementsByName("outdatedClientMacs");
		for(var i=0; i<els.length; i++){
			els[i].checked = false;
			els[i].parentNode.parentNode.style.backgroundColor = "#fff";
		}
	}
}

var includedStagesPanel = null;
var openStagesPanel=false;
function showIncludedStagesDiv(){
	if(null == includedStagesPanel){
		createIncludedStagesPanel();
	}
	<s:if test="%{!isEnterFromTool}">
	//var el = document.getElementById("stagesHref").getElementsByTagName("a")[0];
	//var x = Dom.getX(el)-120;
	//var y = Dom.getY(el)-140;
	var x = Dom.getX("layoutDiv")+348;
	var y = Dom.getY("layoutDiv")+98;
	</s:if>
	<s:else>
	var x = Dom.getX("layoutDiv")+325;
	var y = Dom.getY("layoutDiv")+108;
	</s:else>
	includedStagesPanel.cfg.setProperty("xy", [x, y]);
	includedStagesPanel.show();
	
	openStagesPanel = true;
}

function hideIncludedStagesPanel(){
	if(null != includedStagesPanel){
		includedStagesPanel.hide();
		
		clearTimeout(pollTimeoutId);
		pollCount = 0;//reset pollCount;
		pollClientDebugData();
		
		openStagesPanel = false;
	}
}

function selectStages(checked){
	var els = document.getElementsByName("stagesCHK");
	for(var i=0; i<els.length; i++){
		els[i].checked = checked;
		els[i].parentNode.parentNode.style.backgroundColor = checked?"#FFCC33":"#fff";
	}
}

function selectStage(cbx){
	cbx.parentNode.parentNode.style.backgroundColor = cbx.checked?"#FFCC33":"#fff";
	if(isAllStagesSelected())
		document.getElementById("allStageschk").checked = true;
	else
		document.getElementById("allStageschk").checked = false;
}

function createIncludedStagesPanel(){
	includedStagesPanel = new YAHOO.widget.Overlay("includedStages",{ visible: true, width: "150px"});
	includedStagesPanel.render(document.body);
	Get("includedStages").style.display = "";
}

function getSelectStages(){
	var stages = new Array();
	var els = document.getElementsByName("stagesCHK");
	for(var i=0; i<els.length; i++){
		if(els[i].checked){
			stages.push(els[i].value);
		}
	}
	return stages;
}

function isAllStagesSelected(){
	var els = document.getElementsByName("stagesCHK");
	for(var i=0; i<els.length; i++){
		if(!els[i].checked){
			return false;
		}
	}
	return true;
}

function mousePosition(ev){
	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop - document.body.clientTop
	};
}

document.onmousedown = mouseMove;

function mouseMove(ev){
    ev = ev || window.event;
    var mousePos = mousePosition(ev);
    
    if(openStagesPanel){
    	var x = Dom.getX("includedStages");
    	var y = Dom.getY("includedStages");
    	if(!((x < mousePos.x && mousePos.x < x+150) && 
    			(y < mousePos.y && mousePos.y < y+135))){
    		hideIncludedStagesPanel();
    	}
    }
}
//======== Add Error Details Panel =========
var errorDetailsPanel = null;
function showErrorDetails(){
    if(null == errorDetailsPanel){
        createErrorDetailsPanel();
    }
    errorDetailsPanel.center();
    errorDetailsPanel.show();
}

function createErrorDetailsPanel(){
    errorDetailsPanel = new YAHOO.widget.Overlay("errorDetails",
            { visible: true, width: "400px" });
    errorDetailsPanel.render(document.body);
    Get("errorDetails").style.display = "";
}

function addErrorDetailRow(cell1Text, cell2Text) {
    var showElement = function(elementId) {
        var el = document.getElementById(elementId);
        if(el && "none" == el.style.display) {
            el.style.display = "";
        }
    }
    var table = document.getElementById("errorDetailTable");
    var row = table.insertRow(-1);
    row.className = "detailRow";
    var cell = row.insertCell(0);
    cell.className = 'errorDetailCell list';
    cell.innerHTML = cell1Text;
    var cell = row.insertCell(1);
    cell.className = 'noteError list';
    cell.innerHTML = cell2Text;
    
    showElement("errDetailAnchor");
    showElement("errDetailBtn");
}

//======== Add Log Descirption Details Panel =========
function showDescDialog(detailEl, clientMac, apMac) {
	if(detailEl && detailEl.tagName.toLowerCase() == 'span') {
		openDescDetailsPannel(detailEl.innerHTML, clientMac, apMac);
	}
}	
var descDetailPannel = null;
function openDescDetailsPannel(text, clientMac, apMac) {
	if(null == descDetailPannel) {
		createDescDetailPannel();
	}
	descDetailPannel.setHeader("Selected client [" + clientMac + "] on AP [" + apMac + "]");
	// Format the description
	text = text.replace("[","<br><br>").replace(/,/g, "<br>").replace("]", "");
	descDetailPannel.setBody(text);
	descDetailPannel.center();
	descDetailPannel.cfg.setProperty('visible', true);
}
function closeDescDetailsPannel() {
    if(null != descDetailPannel){
    	descDetailPannel.cfg.setProperty('visible', false);
    }	
}
function createDescDetailPannel() {
	descDetailPannel = new YAHOO.widget.Panel("descDetails", {
        width:"350px",
        height:"450px",
        visible:false,
        draggable:true,
        constraintoviewport:true,
        underlay: "none",
        zIndex:2
        });
   var escListener = new YAHOO.util.KeyListener(document, { keys:27},                                 
              { fn:closeDescDetailsPannel,scope:descDetailPannel,correctScope:true } );
   descDetailPannel.cfg.queueProperty("keylisteners", escListener);
   descDetailPannel.render(document.body);
}
</script>
<s:if test="%{isEnterFromTool}">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td><tiles:insertDefinition name="context" /></td>
	</tr>
</table>
</s:if>
<div id="layoutDiv">
<div>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<s:if test="%{isEnterFromTool}">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="New"
						class="button" onClick="showAddDiv();"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="removeClient();"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Show Log"
						class="button" onClick="showOutdatedDiv();"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Error Details"
						class="button" onClick="showErrorDetails();" id="errDetailBtn"
						style="display: none; width: 90px;"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
	</s:if>
	<s:else>
		<tr>
			<td>
				<div id="newLinkDiv" style="padding-bottom: 2px">
					<a class="debug" href="javascript:void(0);" onclick="showAddDiv();"><s:text name='debug.client.track.new' /></a>
					<a class="debug" href="javascript:void(0);" onclick="removeClient();"><s:text name='debug.client.track.remove' /></a>
					<a class="debug" href="javascript:void(0);" onclick="showOutdatedDiv();"><s:text name='debug.client.track.outdated' /></a>
					<a class="debug" href="javascript:void(0);" onclick="showErrorDetails();" id="errDetailAnchor" style="display: none;"><s:text name='debug.client.track.error' /></a>
				</div>
			</td>
		</tr>
	</s:else>
</table>
</div>
<div style="clear: both;"></div>
<div id="clients"></div>
<fieldset id="logs" style="padding: 5px; margin: 5px;">
    <legend>
        <s:text name="debug.client.logMessage.tag" />
    </legend>
    <div id="actions" style="padding: 5px 0; height: 25px;">
    	<div style="float: left; padding-left: 5px;">
	        <label class="labelT1">
	            <s:text name='debug.client.orderBy' />
	        </label>
	        <select id="viewType_sel" onchange="viewTypeChange(this.value);">
	 			<option value="<s:text name='debug.client.log.time' />"><s:text name='debug.client.log.time' /></option>
	            <option value="<s:text name='debug.client.column.macAddress' />"><s:text name='debug.client.column.macAddress' /></option>
				<option value="<s:text name='debug.client.column.hiveApMac' />"><s:text name='debug.client.column.hiveApMac' /></option>
	        </select>
    	</div>
    	<div style="float: left; padding-left: 10px;">
	        <label class="labelT1">
	            <s:text name='debug.client.filterBy' />
	        </label>
	        <select id="filterType_sel" onchange="filterTypeChange(this.value);">
	 			<option value="<s:text name='debug.client.filter.level.detail' />"><s:text name='debug.client.filter.level.detail' /></option>
	            <option value="<s:text name='debug.client.filter.level.info' />"><s:text name='debug.client.filter.level.info' /></option>
				<option value="<s:text name='debug.client.filter.level.basic' />"><s:text name='debug.client.filter.level.basic' /></option>
	        </select>
    	</div>
    	<div style="float: left; padding-left: 10px;">
	        <label class="labelT1">
	            <s:text name='debug.client.rowLimit' />
	        </label>
	        <select id="rowLimit_sel" onchange="rowLimitChange(this.value);">
	 			<option value="10">10</option>
	            <option value="20">20</option>
				<option value="50" selected="selected">50</option>
				<option value="100">100</option>
				<option value="300">300</option>
				<option value="500">500</option>
	        </select>
    	</div>
    	<!-- 
    	<div style="width: 81px; float: left; padding-left: 4px;" id="stagesHref">
    	    <a class="debug" href="javascript:void(0);" onclick="showIncludedStagesDiv();" style="display: block; width: 78px;">
	            <s:text name='debug.client.includedStages'/>
	        </a>
    	</div>
    	 -->
        <div style="float: right;"><input <s:property value="writeDisabled" /> class="button" type="button" value="Export" onclick="exportLogs();" /></div>
        <div style="float: right;"><input <s:property value="writeDisabled" /> class="button" type="button" value="Clear" onclick="clearLogs();" /></div>
    	<div style="float: right;"><input <s:property value="writeDisabled" /> class="button" type="button" value="Pause" onclick="pauseLogs(this);" /></div>
        <div style="float: right;"><input <s:property value="writeDisabled" /> class="button" type="button" value="Stage Filter" onclick="showIncludedStagesDiv();" /></div>
    </div>
    <div id="content">
    </div>
</fieldset>
</div>
<s:form id="clientDebug" name="clientDebug" action="hiveApToolkit">
	<s:hidden name="viewType" />
	<s:hidden name="filterType" />
	<s:hidden name="rowLimit" />
	<s:hidden name="clientMacs" />
	<s:hidden name="operation" />
	<s:hidden name="stageNames" />
	<s:hidden name="debugGroupId" value="%{debugGroupId}" />
	<s:hidden name="apMacs" />
	<s:hidden name="cookieIds" />
</s:form>
<s:iterator id="clientDebugInfo" value="clientDebugInfos" status="status">
    <s:hidden name="clientDebugInfos" value="%{clientDebugInfo}" />
</s:iterator>
<div id="newClientDiv" style="display: none;">
	<div class="bd">
		<table class="settingBox" border="0" cellspacing="0" cellpadding="0"
			width="280px">
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td style="padding-left: 10px;" colspan="2">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><span id="infoTag"></span></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name='debug.client.column.macAddress' /></td>
				<td><s:select list="%{selectedClients}" name="selectedClient" id="selectedClient"/></td>
			</tr>
			<tr>
				<td class="labelT1" width="130px" nowrap="nowrap"><s:text name='debug.client.track.location' /></td>
				<td><s:select onchange="mapSelectionChange(this);" cssStyle="width:112px;" list="%{selectedMaps}" value="%{selMapName}" name="selectedMap" id="selectedMap"/></td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name='debug.client.column.hiveApMac' /></td>
				<td><s:select cssStyle="width:112px;" listKey="key" listValue="value" list="%{selectedHiveAps}" name="selectedHiveAp" id="selectedHiveAp"/></td>
			</tr>
			<tr>
			    <td colspan="2">
			    <fieldset>
			    <legend><s:text name="config.ssid.advanceOption"/></legend>
			    <div style="padding-top: 5px;">
			    <input type="checkbox" id="clientPerformance" onclick="enablePerformanceMonitor(this.checked);"/>
			    <label for="clientPerformance"><s:text name="debug.client.performance"/></label>
			    </div>
			    </fieldset>
			    </td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input <s:property value="writeDisabled" /> type="button" name="ignore"
								value="Add" class="button" onClick="addNewClient();"></td>
							<td><input <s:property value="writeDisabled" /> type="button" name="ignore" value="Cancel"
								class="button"
								onClick="hiddenAddDiv();"></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
		</table>
	</div>
</div>
<div id="overlay" style="display: none">
	<div class="hd" style="border-bottom: 1px dotted;"><span></span><div class="closeBtn" title="Close" onclick="if(null != overlay){overlay.hide();}">&#215;</div></div>
	<div class="bd">
	<table cellspacing="0" cellpadding="0" border="0" width="100%" id="debugDetail">
		<tr>
			<th align="left" nowrap="nowrap"><s:text name="debug.client.track.hiveAp" /></th>
			<th align="left" nowrap="nowrap"><s:text name="debug.client.column.description" /></th>
		</tr>
	</table>
	</div>
	<div class="ft"></div>
</div>
<div id="includedStages" style="display: none">
	<div class="bd">
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<div style="width: 100%; padding: 2px 5px; height:135px;">
						<table id="includedStagesTable" cellspacing="0" cellpadding="0"
							border="0">
							<tr>
								<td/>
								<td/>
							</tr>
							<tr>
								<th width="15px"><input id="allStageschk" type="checkbox" onclick="selectStages(this.checked);" checked="checked"/></th>
								<th width="100px"><s:text name="debug.client.selectedIncludedStages" /></th>
							</tr>
							<s:iterator id="includedStage" value="%{includedStages}">
								<tr id="tr_<s:property value="includedStage"/>" style="background-color: #FFCC33;">
									<td style="border-bottom: 1px solid #999; padding-left: 2px;"
										width="15px" align="center">
										<s:checkbox onclick="selectStage(this);"
											fieldValue="%{includedStage}" name="stagesCHK" value="true"/>
									</td>
									<td style="border-bottom: 1px solid #999;" width="100px"
										align="left"><s:property value="%{includedStage}" /></td>
								</tr>
							</s:iterator>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<div class="closeBtn" title="Close" onclick="hideIncludedStagesPanel();">&#215;</div>
	</div>
</div>
<div id="moreLogs" style="display: none">
	<div class="bd">
	<div class="closeBtn" title="Close" onclick="hideLogsPanel();">&#215;</div>
	<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
				<div style="width: 100%; padding: 2px 5px; height:180px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" >
						<tr>
							<td></td>
							<td><span id="logsInfo"></span></td>
						</tr>
						<tr>
							<th width="15px"><input type="checkbox" onclick="selectOutdatedLogs(this.checked);" /></th>
							<th width="150px"><s:text name="debug.client.log.hiveAp" /></th>
						</tr>
					</table>
					</div>
					<div style="width:90%; height:150px; overflow: auto;">
					<table id="outdatedLogsTable" cellspacing="0" cellpadding="0" border="0" >
						<s:iterator id="outdatedClient" value="%{outdatedClients}">
						<tr id="tr_<s:property value="%{outdatedClient}"/>">
							<td style="border-bottom: 1px solid #999; padding-left:3px;" width="20px" align="center">
								<s:checkbox onclick="selectOutdatedLog(this);" fieldValue="%{outdatedClient}" name="outdatedClientMacs"></s:checkbox>
							</td>
							<td style="border-bottom: 1px solid #999;" width="160px" align="center">
								<s:property value="%{outdatedClient}"/>
							</td>
						</tr>
						</s:iterator>
					</table>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div style="width: 100%; padding: 5px;" align="center">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><input type="button" name="ignore"
								value="Export" class="button" onClick="exportOutdatedLogs();"></td>
							<td><input type="button" name="ignore"
								value="Remove" class="button" onClick="removeOutdatedLogs();"></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
</div>
<div id="errorDetails" style="display: none;">
    <div class="hd">
        <div class="closeBtn" title="Close" onclick="if(null != errorDetailsPanel){errorDetailsPanel.hide();}">&#215;</div>
    </div>
    <div class="bd">
    <table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%" style="padding-top: 10px;">
        <tr>
            <th align="left" nowrap="nowrap" style="width: 120px;">&nbsp;&nbsp;Clients</th>
            <th align="left" nowrap="nowrap"><s:text name="debug.client.track.error" /></th>
        </tr>
        <tr>
            <td colspan="2">
            <div style="width: 380px; height: 250px; overflow: auto; padding: 0 10px;">
            <table id="errorDetailTable" cellspacing="0" cellpadding="0" border="0" width="100%">
            </table>
            </div>
            </td>
        </tr>
    </table>    
    </div>
    <div class="ft"></div>
</div>
<div id="descDetails">
    <div class="hd"></div>
    <div class="bd" style="overflow-x: hidden; overflow-y: auto;"></div>
</div>