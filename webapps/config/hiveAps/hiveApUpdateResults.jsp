<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page pageEncoding="UTF-8"%>
<%
	response.setHeader("Cache-Control", "no-store");
%>
<style type="text/css">
#cliInfoPanel .bd {
	padding: 0;
}

#cliInfoPanel .bd_top {
	background-color: #eee;
}

#cliInfoPanel .cli_viewer {
	padding: 10px;
	overflow: auto;
	height: 25em;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #fff;
}

#cliInfoPanel .ft {
	height: 15px;
	padding: 0;
}

#cliInfoPanel .yui-resize-handle-br {
	right: 0;
	bottom: 0;
	height: 8px;
	width: 8px;
	position: absolute;
}
.a0{
	float:left;
	margin-left:0px;
	width:102px;
	height:15px;
	border:1px solid #5B94DF;
}
.a1{
	float:left;
	height:13px;width:0px;font-size:12px;
	border-left:1px solid  #FFFFFF;
	border-top:1px solid  #FFFFFF;
	border-bottom:1px solid  #FFFFFF;
	background-Color:#8cd92b;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#66f900, endColorstr=#81ACE7);
}
.cancel, a.actionType{
	padding: 2px 2px 2px 0;
	text-decoration: none;
}
a.actionType:hover {
	color: #CC3300;
	text-decoration: underline;
}

.currentState{
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	font-style: italic;
}
.note{
	color: #F00;
}
</style>
<script>
var formName = 'hiveApUpdateRts';
var pageIndex = <s:property value="pageIndex"/>;
var pageCount = <s:property value="pageCount"/>;
var deviceTypes = <s:property value="deviceTypesJsonString" escapeHtml="false" />;
var Device_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var thisOperation;

function isAnySwitchSelected(selectedId){
	if(selectedId == null){
		return false;
	}
	for(var i=0; i<selectedId.length; i++){
		var id = selectedId[i];
		var type = deviceTypes["_" + id];
		if(type == Device_TYPE_SWITCH){
			return true;
		}
	}
	return false;
}

function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
		hm.util.checkAndConfirmDelete();
	} else if (operation == 'removeSuccessfulEntries') {
		hm.util.checkAndConfirmDeleteSuccessful();
	} else if (operation == 'rebootFromResultPage') {
		var selectedIds = hm.util.getSelectedIds();
		if(selectedIds.length < 1){
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
			return;
		}
		hm.util.confirmRebooting(isAnySwitchSelected(selectedIds));
	} else if (operation == 'retry') {
		var selectedIds = hm.util.getSelectedIds();
		if(selectedIds.length < 1){
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
			return;
		}
		doContinueOper();
	} else if (operation == 'cancel') {
		var selectedIds = hm.util.getSelectedIds();
		if(selectedIds.length < 1){
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
			return;
		}
		hm.util.confirmCancel();
	} else if (validate(operation)) {
		doContinueOper();
	}
}

function doContinueOper() {
	if(thisOperation != 'success'
		&& thisOperation != 'cancel'
		&& thisOperation != 'retry'
		&& thisOperation != 'rebootFromResultPage'){
		showProcessing();
	}
	//cancel upload is AJAX
	if(thisOperation == 'cancel'){
		cancelProcess();
		return;
	}
	if (thisOperation == 'rebootFromResultPage') {
		//document.getElementById(formName + "_operation").value = thisOperation;
		requestRebootFromResultPage();
		//return;
	}
	if(thisOperation == 'retry'){
		if(thisRetryId != null){
			document.forms[formName].id.value = thisRetryId;
			thisRetryId = null;
		}
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function validate(operation) {
	return true;
}

var interval = 10;        // seconds
var duration = hm.util.sessionTimeout * 60;   // minutes * 60
var total = duration / interval;  // refresh will stop after 'total' refreshes
var count = 0;
var timeoutId;
function onLoadPage(){
	timeoutId = window.setTimeout("refreshList()", interval * 1000);
	//create waiting panel
	createWaitingPanel();
	// Overlay for reboot information.
	createCliInfoPanel();
	//create tooltip for description
	createTooltip();
}

var tooltip = null;
function createTooltip(){
	var table = document.getElementById("resultTable");
	var rows = table.rows;
	var ids = [];
	if(rows.length > 0){
		for(var i=0; i< rows.length; i++){
			var rowId = rows[i].id;
			if(rowId){
				ids.push(rowId+"_desc");
			}
		}
	}
	tooltip = new YAHOO.widget.Tooltip("ttDesc", {
		context:ids,
		autodismissdelay: 900000,
		width: "300px"
	});
	tooltip.contextMouseOverEvent.subscribe(
		function(type, args) {
			var context = args[0];
			if (context.getAttribute("desc") == undefined) {
				return false;
			} else {
				return true;
			}
		}
	);
	tooltip.contextTriggerEvent.subscribe(
		function(type, args) {
			var context = args[0];
			this.cfg.setProperty("text", context.getAttribute("desc"));
		}
	);
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Rebooting...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, { width:"600px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	cliInfoPanel.render(document.body);
	div.style.display = "";
}

function updateRow(tr, rid, result){
	for(var i=1; i<tr.cells.length; i++){
		var cell = tr.cells[i];
		var cid = cell.id;
		if(cid == undefined || cid == ""){
			continue;
		}
		if(cid == "r" + rid + "_d7"){
	 	 	//setup the download rate column
 			cell.innerHTML = '<div class="a0"><div class="a1" style="width:'+result.d7+'px">'+result.d7+'%</div></div>';
		}else if(cid == "r" + rid + "_d10"){
 	 		//setup the description column
 	 		var descSpan = cell.firstChild;
 	 		var descObj = result.d10;
 	 		if(descObj.desc){
				descSpan.setAttribute("desc", descObj.desc);
 	 	 	}else{
 	 	 	 	descSpan.removeAttribute("desc");
 	 	 	}
 	 	 	descSpan.innerHTML = descObj.value;
		}else{
			cell.innerHTML = result[cid.replace(/\w*_/, "")] + "&nbsp;";
		}
	}
}

var refreshSuccess = function(o){
	eval("var resp = " + o.responseText);
	var currentIndex = resp.index;
	var currentCount = resp.count;
	var jsonResults = resp.list;

	if(currentIndex != pageIndex || currentCount!= pageCount){
		window.clearInterval(timeoutId);
		submitAction("success");
		return;
	}

	var rowChanged = false;
	for (var i = 0; i < jsonResults.length; i++) {
		var entryId = jsonResults[i].id;
		var entryContent = jsonResults[i].result;
		var trEl = document.getElementById("r" + entryId);
		// Child nodes of tr are td elements to be updated.
		if(null == trEl){
			rowChanged = true;
			break;
		}else{
			updateRow(trEl, entryId, entryContent);
		}
	}
	if(rowChanged){
		window.clearInterval(timeoutId);
		submitAction("success");
		return;
	}
	if(resp.showRebootNote){
		document.getElementById('rebootNote').style.display = '';
	}else {
		document.getElementById('rebootNote').style.display = 'none';
	}
	if(resp.showWarningNote){
		document.getElementById('reconfigNote').style.display = '';
	}else {
		document.getElementById('reconfigNote').style.display = 'none';
	}
	timeoutId = window.setTimeout("refreshList()", interval * 1000);
};
var refreshFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : refreshSuccess,
	failure : refreshFailed
};

function refreshList() {
	if (count++ < total) {
		url = "<s:url action='hiveApUpdateRts' includeParams='none'/>" + "?operation=refresh" + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	}
}

var thisCancelId;
function requestCancel(id){
	thisOperation = "cancel";
	thisCancelId = id;
	hm.util.confirmCancel();
}
var thisRetryId;
function requestRetry(id){
	thisOperation = "retry";
	thisRetryId = id;
	doContinueOper();
}
var thisRebootId;
function requestReboot(id){
	thisOperation = "rebootFromResultPage";
	thisRebootId = id;
	hm.util.confirmRebooting(isAnySwitchSelected([id]));
}

function confirmCancelHandler(){
	resetSelectId();
}

function handleNo() {
    this.hide();
    resetSelectId();
};

function resetSelectId(){
	thisCancelId = null;
	thisRebootId = null;
	thisRetryId = null;
}

function cancelProcess(){
	if(null != thisCancelId){
		url = "<s:url action='hiveApUpdateRts' includeParams='none'/>" + "?operation=cancel" + "&id=" + thisCancelId + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:cancelSuccess,failure:connectedFailed,timeout: 60000}, null);
		//show waiting icon.
		document.getElementById("f"+thisCancelId).style.display = "none";
		document.getElementById("t"+thisCancelId).style.display = "inline";
		thisCancelId = null;
	}else{
		document.forms[formName].operation.value = thisOperation;
		var formObject = document.getElementById('hiveApUpdateRts');
		YAHOO.util.Connect.setForm(formObject);
		url = "<s:url action='hiveApUpdateRts' includeParams='none' />";
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:cancelSuccess,failure:connectedFailed,timeout: 60000}, null);
		var selectedIds = hm.util.getSelectedIds();
		for(var i=0; i<selectedIds.length; i++){
			var el1 = document.getElementById("f"+selectedIds[i]);
			var el2 = document.getElementById("t"+selectedIds[i]);
			if(null != el1 && null != el2){
				el1.style.display = "none";
				el2.style.display = "inline";
			}
		}
	}
}

function cancelSuccess(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var resp = " + o.responseText);
	if(resp.isDone){
		window.clearInterval(timeoutId);
		submitAction("success");
		return;
	}else if (resp.msg != undefined) {
		var label = document.getElementById("note");
		hm.util.reportFieldError(label, resp.msg);
	}
}

function requestRebootFromResultPage(){
	if(thisRebootId != null){
		document.forms[formName].id.value = thisRebootId;
		thisRebootId = null;//reset;
	}
	/**
	var formObject = document.getElementById('hiveApUpdateRts');
	YAHOO.util.Connect.setForm(formObject);
	url = "<s:url action='hiveApUpdateRts' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
	**/
}

var connectedFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

var nothingToDo = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

var cliInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var details = " + o.responseText);
	if (!details.t){
		hm.util.displayJsonErrorNote(details.m);
	}
	
	/***
	eval("var result = " + o.responseText);

	if(result.t){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = result.t;
	}
	if(result.h){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = dialogTitleDiv.innerHTML + ' - ' + result.h;
	}
	if(result.v){
		var cliDiv = document.getElementById("cli_viewer");
		cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g,"<br>") + "</pre>";
	}
	cliInfoPanel.cfg.setProperty('visible', true);
	**/
}

function onUnloadPage(){
	window.clearTimeout(timeoutId);
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
YAHOO.util.Event.onDOMReady(function () {
// remove... menu
	var mMenu = new YAHOO.widget.Menu("m_menu", { fixedcenter: false });
	mMenu.addItems([
		    [
		        { text: 'All successful items', onclick: { fn: onRemoveMenuClick, obj: "removeSuccessfulEntries" } },
		        { text: 'All selected items', onclick: { fn: onRemoveMenuClick, obj: "remove" } }
			]
	]);
	mMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('m_menutoggle');
		var y = YAHOO.util.Dom.getY('m_menutoggle');
		YAHOO.util.Dom.setX('m_menu', x);
		YAHOO.util.Dom.setY('m_menu', y+20);
	});

	mMenu.render();

	YAHOO.util.Event.addListener("m_menutoggle", "click", mMenu.show, null, mMenu);
});
function onRemoveMenuClick(p_sType, p_aArgs, p_oValue) {
	submitAction(p_oValue);
}
</script>

<div id="content"><s:form action="hiveApUpdateRts">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Retry"
						class="button" onClick="submitAction('retry');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Reboot"
						class="button" onClick="submitAction('rebootFromResultPage');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="submitAction('cancel');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove..."
						class="button" id="m_menutoggle"
						<s:property value="writeDisabled" />></td>
					<td><div id="m_menu" class="yuimenu"></div></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="noteInfo" id="note"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td>
			<table id="resultTable" cellspacing="0" cellpadding="0" border="0"
				width="100%" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
					<s:if test="%{columnId == 1}">
						<th><ah:sort name="hostname" key="hiveAp.hostName" /></th>
					</s:if>
					<s:elseif test="%{columnId == 2}">
						<th><ah:sort name="nodeId" key="hiveAp.macaddress" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 3}">
						<th><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" orderByIp="true"/></th>
					</s:elseif>
					<s:elseif test="%{columnId == 4}">
						<th><ah:sort name="updateType" key="hiveAp.update.type" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 5}">
						<th><ah:sort name="startTime" key="hiveAp.update.startTime" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 6}">
						<th><ah:sort name="finishTime" key="hiveAp.update.finishTime" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 7}">
						<th><ah:sort name="downloadRate"
							key="hiveAp.update.downloadRate" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 8}">
						<th><ah:sort name="state" key="hiveAp.update.state" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 11}">
						<th><ah:sort name="actionType"
							key="hiveAp.update.result.action" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 9}">
						<th><ah:sort name="result" key="hiveAp.update.result" /></th>
					</s:elseif>
					<s:elseif test="%{columnId == 10}">
						<th><ah:sort name="description"
							key="hiveAp.update.result.description" /></th>
					</s:elseif>
					</s:iterator>
					<s:if test="%{showDomain}">
						<th><ah:sort name="owner.domainName" key="config.domain" /></th>
					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr id="r<s:property value="#pageRow.id" />"
						class="<s:property value="%{#rowClass}"/>">
						<s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
       						<td class="listCheck"><input type="checkbox" disabled="disabled" /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="hostname" />&nbsp;</td>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="nodeId" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 3}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="ipAddress" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 4}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="updateTypeHtmlString" escape="false" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 5}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="startTimeString" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 6}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="finishTimeString" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 7}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><div class="a0"><div class="a1" style="width:<s:property value="downloadRateString"/>px"><s:property value="downloadRateString"/>%</div></div></td>
						</s:elseif>
						<s:elseif test="%{columnId == 8}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list" nowrap="nowrap"><s:property value="stateString"
								escape="false" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 11}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="actionTypeString"
								escape="false" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 9}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><s:property value="resultString"
								escape="false" />&nbsp;</td>
						</s:elseif>
						<s:elseif test="%{columnId == 10}">
							<td id="r<s:property value="#pageRow.id" />_d<s:property value="columnId" />" class="list"><span id="r<s:property value="#pageRow.id" />_desc" <s:if test="%{!descriptionTitle.isEmpty()}">desc='<s:property value="descriptionTitle" escape="false"/>'</s:if>><s:property value="descriptionValue" />&nbsp;</span></td>
						</s:elseif>
						</s:iterator>
						<s:if test="%{showDomain}">
							<td class="list"><s:property value="%{owner.domainName}" /></td>
						</s:if>
					</tr>
				</s:iterator>
				<s:if test="%{anyAuto}">
					<tr>
						<td colspan="100" class="labelT1">
							<span class="note">&#8224; </span>
							<s:text name="info.hiveAp.update.autoProvision.note"></s:text>
						</td>
					</tr>
				</s:if>
				<s:if test="%{anyDistributor}">
					<tr>
						<td colspan="100" class="labelT1">
							<span class="note">&#8225; </span>
							<s:text name="info.hiveAp.update.distributor.note"></s:text>
						</td>
					</tr>
				</s:if>
				<tr id="rebootNote" style="display: <s:property value="%{showRebootNote}"/>">
					<td colspan="100" class="labelT1">
						<span class="note">* </span>
						<s:text name="info.hiveAp.update.reboot.note"></s:text>
					</td>
				</tr>
				<tr id="reconfigNote" style="display: <s:property value="%{showWarningNote}"/>">
					<td colspan="100" class="labelT1">
						<span class="note">&#33; </span>
						<s:text name="info.hiveAp.update.reconfig.note"></s:text>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="cliInfoPanel" style="display: none;">
<div class="hd" id="cliInfoTitle">Dialog</div>
<div class="bd">
<div id="cli_viewer" class="cli_viewer"></div>
</div>
<div class="ft"></div>
</div>