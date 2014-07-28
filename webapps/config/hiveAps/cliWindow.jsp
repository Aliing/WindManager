<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<!--CSS file (default YUI Sam Skin) -->
<link type="text/css" rel="stylesheet"
	href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<!-- Dependencies -->
<script type="text/javascript"
	src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>
<!-- Source files -->
<script type="text/javascript"
	src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>

<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
	white-space: nowrap;
}

.yui-skin-sam .yui-dt-scrollable .yui-dt-hd,.yui-skin-sam .yui-dt-scrollable .yui-dt-bd{
	border: medium none;
}

/* striping */ 
.yui-skin-sam tr.yui-dt-even { background-color:#FFF; } /* white */ 
.yui-skin-sam tr.yui-dt-odd { background-color:#88DDff; } /* light blue */  
.yui-skin-sam tr.yui-dt-even td.yui-dt-asc,  
.yui-skin-sam tr.yui-dt-even td.yui-dt-desc { background-color:#FFF; } /* light blue sorted */  
.yui-skin-sam tr.yui-dt-odd td.yui-dt-asc,  
.yui-skin-sam tr.yui-dt-odd td.yui-dt-desc { background-color:#88DDff; } /* dark blue sorted */  

/* highlighting */  
.yui-skin-sam th.yui-dt-highlighted,  
.yui-skin-sam th.yui-dt-highlighted a {  
    background-color:#AAAAFF; /* med blue hover */  
}  
.yui-skin-sam tr.yui-dt-highlighted,   
.yui-skin-sam tr.yui-dt-highlighted td.yui-dt-asc,   
.yui-skin-sam tr.yui-dt-highlighted td.yui-dt-desc,   
.yui-skin-sam tr.yui-dt-even td.yui-dt-highlighted,  
.yui-skin-sam tr.yui-dt-odd td.yui-dt-highlighted {  
    cursor:pointer;   
    background-color:#AAAAFF; /* med blue hover */  
}  


</style>

<style>
<!--
html {
	overflow: hidden;
}
.required {
	color: #FF0000;
}

#results{
	padding: 5px 0 0 0;
}

fieldset {
	margin: 0;
}
-->
</style>

<script type="text/javascript">
var formName = "cliWindow";
var basic;
var results = [];
var pollTimeoutId;
var pollCount = 0;
var thisOperation;

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	initDataTable(440, 670);
	
	//save this reference on the parent
	top.cliWindowIFrame = window;
	
	if(hostName){
		top.updateCLIWindowTitle(hostName);
	}
	
	Get(formName + "_command").focus();
	
	startPollTimer();
}

var initDataTable = function(h, w){
    basic = function() {
        var myColumnDefs = [
            {key:"<s:text name='hiveap.tools.cliWindow.hostName' />", sortable:true, resizeable:false, width: 100},
            {key:"<s:text name='hiveap.tools.cliWindow.status' />", sortable:true, resizeable:false, minWidth: 520}
        ];

        var myDataSource = new YAHOO.util.DataSource(results);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["<s:text name='hiveap.tools.cliWindow.hostName' />",
            		 "<s:text name='hiveap.tools.cliWindow.status' />"]
        };

		var myDataTable = new YAHOO.widget.DataTable("results",
                myColumnDefs, myDataSource, {scrollable: true, width: w+"px", height: h+"px" });
		myDataTable.subscribe("rowMouseoverEvent", myDataTable.onEventHighlightRow);
		myDataTable.subscribe("rowMouseoutEvent", myDataTable.onEventUnhighlightRow);
		//myDataTable.subscribe("rowClickEvent", myDataTable.onEventSelectRow);
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
}

function pollCommandStatus() {
	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
	document.forms[formName].operation.value = "pollCommandStatus";
	ajaxRequest(formName, url, updateExeResults);
}

var startPollTimer = function(){
	var interval = 2;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	
	if (pollCount++ < total) {
		pollTimeoutId = setTimeout("pollCommandStatus()", interval * 1000);  // seconds
	}
}


var updateExeResults = function(o) {
	if(null != top.waitingPanel){
		top.waitingPanel.hide();
	}
	
	eval("var result = " + o.responseText);
	
	updateButtonStatus(result.status);
	updateResults(result.exeResult);
};

function updateButtonStatus(status) {
	if(status.running) {
		startPollTimer();
	} else {
		if(pollTimeoutId) {
			clearTimeout(pollTimeoutId);
		}
	}
}

function updateResults(exeResult) {
	if(exeResult){
		var previousTop = basic.oDT.getBdContainerEl().scrollTop;
		basic.oDT.getRecordSet().replaceRecords(exeResult);
		basic.oDT.render();
		basic.oDT.getBdContainerEl().scrollTop = previousTop;
	}
}

function onHidePage(){
	if(pollTimeoutId) {
		clearTimeout(pollTimeoutId);
	}
	
	cancelCommand();
}

function requestAction(action){
	if(validate(action)){
		var url = "<s:url action="hiveApToolkit" includeParams="none"></s:url>" + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = action;
		//alert(url);
		if("exeCommand" == action){
			Get(formName + "_command").select();
			Get(formName + "_command").focus();
			basic.oDT.getRecordSet().replaceRecords([]);
			basic.oDT.render();
		}
		
		if("importCommand" == action) {
			if(importPanel != null) {
				importPanel.hide();
			}
			
			Get("importNoteDiv").style.display="none";
		}
		
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
		
		startPollTimer();
		Get("noteDiv").style.display="none";
		thisOperation = action;
		
		if("importCommand" != action) {
			ajaxRequest(formName, url, requestActionProcess);
		} else {
			document.forms[formName].target="cliWindow";
			document.forms[formName].action = "<s:url action='hiveApToolkit' includeParams='none' />";
			document.forms[formName].submit();
			document.forms[formName].action = "<s:url action='hiveAp' includeParams='none' />";
			document.forms[formName].target="_self";
	
		}
	}
}

var requestActionProcess = function(o){
	eval("var result = " + o.responseText);
	
	if(thisOperation == "importCommand") {
		var status = result.status;
	
		if(!status.uploaded) {
			Get("noteId").innerHTML = "Failed to upload the script file.";
			Get("noteId").className="noteError";
			Get("noteDiv").style.display="";
		}
	}
}

function validate(action) {
	if(action == 'exeCommand') {
	var cmd = document.getElementById(formName + "_command").value;
	
	if(cmd.length == 0 || cmd.trim().length == 0) {
		Get("noteId").innerHTML = "Please input the command.";
		Get("noteId").className="noteError";
		Get("noteDiv").style.display="";
		document.getElementById(formName + "_command").focus();
		return false;
	}
	}
	
	if(action == 'importCommand') {
		var cmdFile = document.getElementById("scriptFile");
		
		if (cmdFile.value.length == 0) {
			Get("importNoteId").innerHTML = "Please select a script file.";
			Get("importNoteId").className="noteError";
			Get("importNoteDiv").style.display="";
			cmdFile.focus();
	        return false;
		}
	}
	return true;
}
function cancelCommand() {
	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
	document.forms[formName].operation.value = "cancelCommand";
	ajaxRequest(formName, url, null);
}

function keyPressed(e) {
	var keycode;
	
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
	} else {
		return true;
	}
	
	if(keycode == 13) {
		requestAction('exeCommand');
		return false;
	} else {
		return hm.util.keyPressPermit(e||window.event,'nameWithBlank');
	}
}
</script>
<script type="text/javascript">
var importPanel = null;

function createImportPanel(width, height){
	var div = document.getElementById("importPanel");
	width = width || 500;
	height = height || 400;
	importPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", 
												fixedcenter:true, 
												visible:false, 
												constraintoviewport:true,
												modal:true } );
	importPanel.render();
	div.style.display="";
}

function openImportPanel() {
	if(null == importPanel){
		createImportPanel(600, 350);
	}
	
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("importPanel").style.display = "";
	}

	importPanel.show();
}

function cancelImport() {
	if(null != importPanel) {
		importPanel.hide();
	}
	
	Get("importNoteDiv").style.display="none";
}

function submitAction(){
	var table_obj = $("#results tbody[class=yui-dt-data] tr td:first-child div font");
	table_obj.each(function (){
		$(this).before('<s:hidden name="hostNameString" cssStyle="display:none" value="' + $(this).text() + '"/>');
	});

	document.forms[formName].operation.value = "exportCommandResult";
	document.forms[formName].submit();
}
</script>
<div><s:form action="hiveApToolkit" 
			id="cliWindow" name="cliWindow" 
			enctype="multipart/form-data" method="POST">
<s:hidden name="operation" />
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td height="5px"></td>
	</tr>
	<tr>
		<td>
			<tiles:insertDefinition name="notes" />
		</td>
	</tr>
	<tr>
			<td colspan="10">
			<div id="noteDiv" style="display:none">
			<table width="450px" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td id="noteId"></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="10">
		<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td class="labelT1"><s:text
					name="hiveap.tools.cliWindow.command" />&nbsp;&nbsp;
				</td>
				<td><s:textfield name="command" maxlength="128" cssStyle="width:320px" 
						size="60" onkeypress="return keyPressed(event);" />
				</td>
				<td>
					<input id="execute" type="button" name="ignore" value="Execute"
						class="button" onClick="requestAction('exeCommand');"
						<s:property value="writeDisabled" />>
				</td>
				<td>
					<input id="export" type="button" name="ignore" value="Export"
						class="button" onClick="submitAction('exportCommandResult');"
						<s:property value="writeDisabled" />>
				</td>
				<td>
					<input id="import" type="button" name="ignore" value="Import"
						class="button" onClick="openImportPanel();"
						<s:property value="writeDisabled" />>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="5px"></td>
	</tr>
</table>
<fieldset>
	<legend><s:text name="hiveap.tools.cliWindow.results" /></legend>
	<div id="results"></div>
</fieldset>
<div id="importPanel" style="display: none;">
	<div class="hd"><s:text name="hiveap.tools.cliWindow.import"/></div>
	<div class="bd">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="5" />
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="import" value="Import" class="button"
									onClick="requestAction('importCommand');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Cancel" class="button"
									onClick="cancelImport();"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="5" />
			</tr>
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td colspan="10">
							<div id="importNoteDiv" style="display:none">
								<table width="450px" border="0" cellspacing="0" cellpadding="0"
									class="note">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding: 0 0 2px 4px;" id="importNoteId"></td>
									</tr>
									<tr>
										<td height="5px"></td>
									</tr>
								</table>
							</div>
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="100px">
								<label>
									<s:text name="hiveap.tools.cliWindow.script" />
								</label>
							</td>
							<td>
									<s:file id="scriptFile" name="upload"
										accept="text/html,text/plain" size="70" />
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
</s:form></div>

