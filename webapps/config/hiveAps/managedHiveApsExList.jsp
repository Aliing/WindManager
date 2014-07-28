<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.be.config.hiveap.UpdateParameters"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<link type="text/css" rel="stylesheet" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript" src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<!-- Dependencies -->
<script type="text/javascript"
	src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>
<!-- Source files -->
<script type="text/javascript"
	src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>

<style type="text/css">

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

</style>

<script type="text/javascript">

var formName = 'hiveAp';

var interval = 10;        // seconds
var duration = hm.util.sessionTimeout * 60;   // minutes * 60
var total = duration / interval;  // refresh will stop after 'total' refreshes
var count = 0;
var timeoutId = 0;

var UPDATE_FAILED = <%=UpdateParameters.UPDATE_FAILED%>
var UPDATE_SUCCESSFUL = <%=UpdateParameters.UPDATE_SUCCESSFUL%>
var UPDATE_TIMEOUT = <%=UpdateParameters.UPDATE_TIMEOUT%>
var UPDATE_ABORT = <%=UpdateParameters.UPDATE_ABORT%>
var UPDATE_CANCELED = <%=UpdateParameters.UPDATE_CANCELED%>
var ACTION_REBOOT = <%=UpdateParameters.ACTION_REBOOT%>
var REBOOTING = <%=UpdateParameters.REBOOTING%>
var WARNING = <%=UpdateParameters.WARNING%>
var REBOOT_SUCCESSFUL = <%=UpdateParameters.REBOOT_SUCCESSFUL%>
var deviceTypes = <s:property value="deviceTypesJsonString" escapeHtml="false" />;
var Device_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;

var hiveApPagingLiveCount = 0;
var hiveApPagingTimeoutId;

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
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function onLoadPage(){
	hm.util.registerRowClickedEvents();
	createWaitingPanel();
	createTooltip();
	<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
		initCheckBox();
	</s:if>
	<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
		startHiveApPagingTimer();
	</s:if>
	
    <s:if test="%{doneBtnDisplayFlag}">
    showGotoIDMButton();
    </s:if>
}

function onUnloadPage() {
	clearTimeout(hiveApPagingTimeoutId);
}


function validate(operation) {
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap>&nbsp;');
	document.writeln('</td>');
}

function uploadAction(url){
	if(validate("uploadWizard")){
		top.hideWarningMessage();
		document.forms[formName].operation.value = "uploadWizard";
		YAHOO.util.Connect.setForm(document.getElementById(formName));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, callbackUpdate, null);
		if(waitingPanel != null){
			waitingPanel.setHeader("Loading configuration...");
			waitingPanel.show();
		}
	}
}

function uploadConfig(){
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime()+"&exConfigGuideFeature=uploadConfigEx";
	uploadAction(url);
}

function uploadOperationForIDM(){
	var param = "configConfiguration=true&configCwp=true&configCertificate=true&configUserDatabase=true&saveUploadSetting=false";
	param = param + "&configSelectType=auto&configActivateType=activateAfterTime";
	uploadOperation(param);
}

function uploadOperation(param){
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime()+"&exConfigGuideFeature=uploadConfigEx";
	url = url + "&" + param;
	uploadAction(url);
}

var callbackUpdate = {
	success : uploadConfigResult,
	failure : uploadConfigResultFail,
	timeout: 240000
};

function uploadConfigResult(o){
	eval("var resp = " + o.responseText);
	var actionErrors = resp.actionErrors;
	if(actionErrors != null && actionErrors.length>0){
		for(var i=0; i<actionErrors.length; i++){
			showError(actionErrors[i]);
		}
	}else{
		//initAllCheckBox(false);
		refreshList();
	}
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function uploadConfigResultFail(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function initAllCheckBox(checkValue){
	//var inputElements = document.getElementsByName('selectedIds');
	//if (inputElements) {
	//    for (var i = 0; i < inputElements.length; i++) {
    //		var cb = inputElements[i];
	//		if (!cb.disabled && cb.checked != checkValue) {
	//			cb.checked = checkValue;
	//		}
	//	}
	//}
	
	var allCheckedEle = document.getElementById('checkAll');
	allCheckedEle.checked = false;
	hm.util.toggleCheckAll(allCheckedEle);
}


function refreshList() {
	if (count++ < total) {
		url = "<s:url action='hiveAp' includeParams='none'/>" + "?operation=uploadResultRefresh" + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:refreshSuccess}, null);
	}
}

function refreshSuccess(o){
	eval("var resp = " + o.responseText);
	var currentIndex = resp.index;
	var currentCount = resp.count;
	var jsonResults = resp.list;

	//if(currentIndex != pageIndex || currentCount!= pageCount){
	//	window.clearInterval(timeoutId);
	//	submitAction("success");
	//	return;
	//}

	var rowChanged = false;
	for (var i = 0; i < jsonResults.length; i++) {
		var entryId = jsonResults[i].id;
		var entryContent = jsonResults[i].result;
		var trEl = document.getElementById("r" + entryId);
		// Child nodes of tr are td elements to be updated.
		if(null == trEl){
			rowChanged = true;
			continue;
		}else{
			updateRow(trEl,jsonResults[i]);
		}
	}
	//if(rowChanged){
	//	window.clearInterval(timeoutId);
	//	submitAction("success");
	//	return;
	//}
	//if(resp.showRebootNote){
	//	document.getElementById('rebootNote').style.display = '';
	//}else {
	//	document.getElementById('rebootNote').style.display = 'none';
	//}
	// fix trigger issue
	tooltip.cfg.setProperty("context", tooltip.cfg.getProperty('context'));
	window.clearTimeout(timeoutId);
	timeoutId = window.setTimeout("refreshList()", interval * 1000);
};

function updateRow(tr,jsonResults){
	var result = jsonResults.result;
	for(var i=1; i<tr.cells.length; i++){
		var cell = tr.cells[i];
		var cellId = cell.id;
		if(cellId == "d7" && result != null){
	 	 	//setup the download rate column
	 	 	if(result.d9_key == UPDATE_SUCCESSFUL){
		 	 	cell.innerHTML="";
	 	 		top.showCheckMark('upload');
	 	 		showGotoIDMButton(true);
	 	 	}
	 	 	if(result.d11_key == ACTION_REBOOT){
	 	 		cell.innerHTML = "<a class='actionType' href='javascript:requestReboot("+ jsonResults.id + ");'>Reboot</a>";
	 	 		showGotoIDMButton(true);
	 	 	}else if(result.d9_key == UPDATE_FAILED ||
				result.d9_key == UPDATE_SUCCESSFUL ||
				result.d9_key == UPDATE_TIMEOUT ||
				result.d9_key == UPDATE_ABORT ||
				result.d9_key == UPDATE_CANCELED ||
				result.d9_key == REBOOTING || 
				result.d9_key == WARNING || 
				result.d9_key == REBOOT_SUCCESSFUL){
				
				var status = result.d9_value + "&nbsp;";
				if(result.d10){
					var desc = result.d10;
				}else{
					var desc = "";
				}
				setUploadStatus(cell, tr.id+"_desc", status, desc);				
			}else{
				cell.innerHTML = getUploadRateDiv(result.d7);
			}
		}else if(cellId == "connected"){
			cell.innerHTML = jsonResults.connected;
		}else if(cellId == "ipAddress"){
			cell.innerHTML = jsonResults.ipAddress;
		}else if(cellId == "apType"){
			cell.innerHTML = jsonResults.apType;
		}else if(cellId == "softVer"){
			cell.innerHTML = jsonResults.softVer;
		}else if(cellId == "configIndicationIcon"){
			cell.innerHTML = jsonResults.configIndicationIcon;
		}
		
		//FIXME
		showGotoIDMButton(true);
	}
}

function showGotoIDMButton(send2Server) {
    if(parent && parent.document.getElementById('doneButton')) {
    	var btn = parent.document.getElementById('doneButton');
    	if(btn) {
    		btn.onclick = idmDoneClick;
    		parent.document.getElementById('doneButtonCell').style.display = "";
    		
	        if(send2Server) {
	            //uploadConfigSucc
	            var url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=uploadConfigSucc4IDM" +"&ignore=" 
	                    + new Date().getTime();
	            var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : function(o){
	                //dump
	            } }, null);
	        }
    	}
        
    }
}

function getUploadRateDiv(rate){
	return "<div class='a0'><div class='a1' style='width:" + rate + "px'>" + rate + "%</div></div>";
}

function setUploadStatus(cell, id, status, desc){
	var descSpan = cell.firstChild;
	if(!descSpan || descSpan.tagName != "SPAN" || descSpan.id != id){
		cell.innerHTML = "<span id='" + id + "'></span>";
		descSpan = cell.firstChild;
	}
	descSpan.innerHTML = status;
	//setup the description
	if(desc){
		descSpan.setAttribute("desc", desc);
 	}else{
 		descSpan.removeAttribute("desc");
 	}
}

var tooltip = null;
function createTooltip(){
	var table = document.getElementById("hiveApListTable");
	var rows = table.rows;
	var ids = [];
	if(rows.length > 0){
		for(var i=0; i< rows.length; i++){
			var rowId = rows[i].id;
			if(rowId){
				//if(document.getElementById(rowId+"_desc") != null){
				ids.push(rowId+"_desc");
				//}
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

function validate(operation) {
	if(operation == 'uploadWizard'){
		if(!validateApSelection(operation)){
			return false;
		}
	}
	return true;
}

function validateApSelection(operation){
	var cbs = document.getElementsByName('selectedIds');
	var isSelected = false;
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			isSelected = true;
			break;
		}
	}
	if(!isSelected){
		var listElement = document.getElementById('checkAll');
		hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
		return false;
	}
	return true;
}

var notesTimeoutId;
function delayHideNotes(seconds) {
	notesTimeoutId = setTimeout("hideNotes()", seconds * 2000);  // seconds
}
function hideNotes() {
	hm.util.wipeOut('note', 800);
}

function showError(message) {
	var td = document.getElementById("noteText");
	td.removeChild(td.firstChild);
	td.appendChild(document.createTextNode(message));
	YAHOO.util.Dom.setStyle('note', "display", "");
	delayHideNotes(5);
}

function initCheckBox(){
	var allCheckEle = document.getElementById('checkAll');
	allCheckEle.checked = true;
	hm.util.toggleCheckAll(allCheckEle);
}

var thisRebootId;
var thisOperation;
function requestReboot(id){
	thisOperation = "requestReboot";
	thisRebootId = id;
	hm.util.confirmRebooting(isAnySwitchSelected([id]));
}

function doContinueOper() {
	if (thisOperation == 'requestReboot') {
		doAjaxRequestReboot(thisRebootId, "rebootHiveAPs", cliInfoResult);
		return;
	}else if(thisOperation == '<s:text name="topology.menu.hiveAp.reboot"/>'){
		doAjaxRequestReboot(null, "rebootHiveAPs", cliInfoResult);
		return;
	}else if(thisOperation == 'checkConnectStatus'){
		configUpdateOperation();
	}
}

var connectedFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}


function idmDoneClick(){
	var successDoneButton= function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showError("Redirect to IDM error.");
			return false;
		}
		
		if(data.t) {
			var url = data.url + data.params + encodeURIComponent(data.paramsValue);
			window.location.href = url;
		} else {
			showError(data.m);
			return false;
		}
	}
	var url = "hiveApUpdate.action?operation=doneToIDM&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : successDoneButton, timeout: 60000});
}
</script>

<script type="text/javascript">
var waitingPanel = null;

function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"240px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Loading configuration...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams="none" />" />');
	waitingPanel.render(document.body);
}

</script>

<script>

function rebootHiveAps(){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	thisOperation = '<s:text name="topology.menu.hiveAp.reboot"/>';
	hm.util.confirmRebooting(isAnySwitchSelected(selectedIds));
}

function doAjaxRequestReboot(id, operation, callback, postData ){
	var url = "<s:url action='hiveApUpdate' includeParams='none' />?exConfigGuideFeature == manageAPEx&jsonMode=true";
	if(id > 0){
		url +="&operation=" + operation + "&selectedIds=" + id;
	}else{
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms[formName]);
	}
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 60000}, postData);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var cliInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	refreshList();
	eval("var details = " + o.responseText);
	if (!details.t){
		hm.util.displayJsonErrorNote(details.m);
	}
}

var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, { width:"600px", 
												visible:false, 
												fixedcenter:"contained", 
												draggable:true, 
												constraintoviewport:true,
												modal:true } );
	cliInfoPanel.render();
	div.style.display = "";
	cliInfoPanel.beforeHideEvent.subscribe(closeCliInfoPanelIE);
}

function closeCliInfoPanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("cliInfoPanel").style.display = "none";
	}
	// reset the parent height
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		YAHOO.util.Dom.setStyle(parentEl, 'height', parentHeight+'px');
		document.getElementById("cliInfoPanel").height = 0;
	}
}

</script>


<div id="content">
	<s:form action="hiveAp">
		<s:hidden name="cacheId" />
		<!-- Simplly update param  -->
		<s:hidden name="simpleUpdate" id="simpleUpdate"/>
		<s:hidden name="completeCfgUpdate" id="completeCfgUpdate"/>
		<s:hidden name="imageUpgrade" id="imageUpgrade"/>
		<s:hidden name="forceImageUpgrade" id="forceImageUpgrade"/>
		<s:hidden name="simplifiedRebootType" id="simplifiedRebootType"/>
		<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="10px"></td>
			</tr>
			<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
			<tr>
				<td align="right" style="padding-right: 20px">
					<div class="buttons"><table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<s:if test="%{userSourceFromIdm}">
								<td>
									<input type="button" name="importImage2" id="updateMenu" value="Upload" style="width:80px"
										class="button" onclick="updateButtonClick();" 
										<s:property value="writeDisabled" /> />
								</td>
								<td width="1px"></td>
								<td>
									<input type="button" value="Reboot" style="width:80px" class="button" onclick="rebootHiveAps();" 
										<s:property value="writeDisabled" /> />
								</td>
								<td width="1px"></td>
								<td>
									<input type="button" name="importImage2" value="Options" style="width:80px"
										class="button" onClick='openUpdateOptionPanel("Device Upload Options", "updateOptionEx");' 
										<s:property value="writeDisabled" /> />
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="importImage2" id="updateMenu" value="Upload" style="width:80px"
										class="button" onclick="updateButtonClick();" 
										<s:property value="writeDisabled" /> />
								</td>
								<td width="1px"></td>
								<td>
									<input type="button" value="Reboot" style="width:80px" class="button" onclick="rebootHiveAps();" 
										<s:property value="writeDisabled" /> />
								</td>
								<td width="1px"></td>
								<td>
									<input type="button" name="importImage2" value="Options" style="width:80px"
										class="button" onClick='openUpdateOptionPanel("Device Upload Options", "updateOptionEx");' 
										<s:property value="writeDisabled" /> />
								</td>
							</s:else>
							<td>
								<div id="updateMenuDiv" class="yuimenu"></div>
							</td>
						</tr>
					</table></div>
				</td>
			</tr>
			</s:if>
			
			<tr>
				<td style="padding: 0px 20px 0px 20px;" class="labelT1">
					<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
						<font color="#767676">
						<s:text name="guided.configuration.hiveap.manageList.note1"/><br>
						<s:text name="guided.configuration.hiveap.manageList.note2"/><br>
						<s:text name="guided.configuration.hiveap.manageList.note3"/><br>
						</font>
					</s:if>
					<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
						<font color="#767676">
						<s:text name="guided.configuration.hiveap.uploadList.note1"/>
						</font>
					</s:if>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr id="note" style="display:none">
				<td style="padding: 4px 0 0 4px">
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="500"
					class="note">
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td class="noteError" id="noteText">&nbsp;</td>
					</tr>
					<tr>
						<td height="6"></td>
					</tr>
				</table>
				</div>
				</td>
			</tr>
			
			<tr>
				<td style="padding: 0px 20px 0px 20px;">
					<table id="hiveApListTable" cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
						<tr>
							<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
							<th class="check">
								<input type="checkbox" id="checkAll"
									onClick="hm.util.toggleCheckAll(this);">
							</th>
							</s:if>
							<th width="60px">
								<ah:sort name="connectStatus" key="guided.configuration.hiveap.column.online" />
							</th>
							<th>
								<ah:sort name="hostName" key="guided.configuration.hiveap.column.hostName" />
							</th>
							<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
								<th>
									<ah:sort name="hiveApModel" key="guided.configuration.hiveap.column.deviceType" />
								</th>
							</s:if>
							<th>
								<ah:sort name="macAddress" key="guided.configuration.hiveap.column.macaddress" />
							</th>
							<th>
								<ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" />
							</th>
							<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
								<th>
									<ah:sort name="dhcp" key="guided.configuration.hiveap.column.ip" />
								</th>
								<th>
									<ah:sort name="mapContainer" key="guided.configuration.hiveap.column.Map" />
								</th>
							</s:if>
							<th>
								<ah:sort name="hiveApType" key="hiveAp.apType" />
							</th>
							<th>
								<ah:sort name="softver" key="guided.configuration.hiveap.column.version" />
							</th>
							<th>
								<ah:sort name="pendingIndex" key="guided.configuration.hiveap.column.Updated" />
							</th>
							<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
								<th><s:text name="guided.configuration.hiveap.column.uploadStatus" /></th>
							</s:if>
						</tr>
						<s:if test="%{page.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="page" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/>">
								<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck">
											<ah:checkItem />
										</td>
									</s:else>
								</s:if>
								<td nowrap="nowrap" class="list" id="connected"
									style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
									<s:property value="connectionIcon" escape="false" />&nbsp;
								</td>
								<td class="list">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<s:if test="%{iconMaxCount > 0}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem1" escape="false" />
												</td>
											</s:if>
											<s:if test="%{iconMaxCount > 1}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem2" escape="false" />
												</td>
											</s:if>
											<s:if test="%{iconMaxCount > 2}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem3" escape="false" />
												</td>
											</s:if>
											<s:if test="%{iconMaxCount > 3}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem4" escape="false" />
												</td>
											</s:if>
											<s:if test="%{iconMaxCount > 4}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem5" escape="false" />
												</td>
											</s:if>
											<s:if test="%{iconMaxCount > 5}">
												<td style="padding-right: 3px;">
													<s:property value="iconItem6" escape="false" />
												</td>
											</s:if>
											
											<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
												<td nowrap="nowrap">
													<s:property value="hostName" />
													&nbsp;
												</td>
											</s:if>
											<s:elseif test="%{showDomain}">
												<td class="textLink">
													<a
														href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit2'}"/><s:param name="id" value="%{id}"/>
		       													<s:param name="domainId" value="%{owner.id}"/>
		       													<s:param name="exConfigGuideFeature" value="%{'hiveapEx'}"/>
		       													<s:param name="lastExConfigGuide" value="%{'hiveapEx'}"/>
		       													<s:param name="hmListType" value="%{'manageAPEx'}"/>
		       													<s:param name="listTypeFromSession" value="%{'manageAPEx'}"/>
		       													</s:url>'><s:property
																value="hostName" />
													</a>
												</td>
											</s:elseif>
											<s:else>
												<td class="textLink">
													<a
														href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit2'}"/><s:param name="id" value="%{id}"/>
															<s:param name="exConfigGuideFeature" value="%{'hiveapEx'}"/>
		       													<s:param name="hmListType" value="%{'manageAPEx'}"/>
		       													<s:param name="listTypeFromSession" value="%{'manageAPEx'}"/></s:url>'><s:property
															value="hostName" />
													</a>
												</td>
											</s:else>
										</tr>
									</table>
								</td>
								<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
									<td class="list" nowrap="nowrap">
										<s:property value="hiveApModelString" />
										&nbsp;
									</td>
								</s:if>
								<td class="list" nowrap="nowrap">
									<s:property value="macAddressFormat" />
									&nbsp;
								</td>
								<td class="list" nowrap="nowrap" id="ipAddress">
									<s:property value="ipAddress" />
									&nbsp;
								</td>
								<s:if test="%{exConfigGuideFeature == 'manageAPEx'}">
									<td class="list" nowrap="nowrap">
										<s:property value="ipTypeString" />
										&nbsp;
									</td>
									<td class="list" nowrap="nowrap">
										<s:property value="mapContainer.mapNameEx" />
										&nbsp;
									</td>
								</s:if>
								<td class="list" nowrap="nowrap" id="apType">
									<s:property value="hiveApTypeString" />
									&nbsp;
								</td>
								<td class="list" nowrap="nowrap" id="softVer">
									<s:property value="softVerString" />
									&nbsp;
								</td>
								<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="configIndicationIcon">
									<s:property value="configIndicationIcon" escape="false" />
								</td>
								<s:if test="%{exConfigGuideFeature == 'uploadConfigEx'}">
									<td id="d7" class="list">
										-----
									</td>
								</s:if>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-right: 10px">
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
		</table>
	</s:form>
</div>

<!-- panel for upload config setting -->
<div id="updateOptionPanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="updateOptionFrame" name="updateOptionFrame" width="0" height="0" frameborder="0" src="">
		</iframe>
	</div>
</div>

<tiles:insertDefinition name="updateSimpleModel" />

<style type="text/css">
<!--
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
-->
</style>


<div id="cliInfoPanel" style="display: none;">
	<div class="hd" id="cliInfoTitle">
		Dialog
	</div>
	<div class="bd">
		<div id="bd_top" class="bd_top"/>
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft"></div>
</div>

<script>

var updateOptionPanel = null;
var updateOptionPanelAttr = null;
var parentHeight;

function createUpdateOptionPanel(width, height, title){
	var div = document.getElementById("updateOptionPanel");
	var iframe = document.getElementById("updateOptionFrame");
	iframe.width = width;
	iframe.height = height;
	updateOptionPanel = new YAHOO.widget.Panel(div, { 	width:(width+10)+"px", 
														fixedcenter:"contained", 
														visible:false, 
														draggable: true,
														zindex:4,
														modal:true,
														constraintoviewport:true } );
														
	updateOptionPanel.setHeader(title);
	updateOptionPanel.render();
	div.style.display="";
	updateOptionPanel.beforeHideEvent.subscribe(closeUpdateOptionPanelIE);
}

function closeUpdateOptionPanel() {
	updateOptionPanel.hide();
	
}

function closeUpdateOptionPanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("updateOptionFrame").style.display = "none";
	}
	// reset the parent height
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		YAHOO.util.Dom.setStyle(parentEl, 'height', parentHeight+'px');
		document.getElementById("updateOptionFrame").height = 0;
	}
}

function openUpdateOptionPanel(title, doOperation){
	var width = 680, height = 400;
	// set the parent height to fix this panel
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
		if (parentHeight < 500) {
			YAHOO.util.Dom.setStyle(parentEl, 'height', '480px');
		}
	}
	
	if(null == updateOptionPanel){
		createUpdateOptionPanel(width , height, title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("updateOptionFrame").style.display = "";
	}
	// set the inner iframe height
	var iframe = document.getElementById("updateOptionFrame");
	iframe.height  = height;
	updateOptionPanel.render();
	
	updateOptionPanel.show();
	iframe.src ="<s:url value='hiveApUpdate.action' includeParams='none' />?operation="+doOperation;
}
</script>
<script>
function startHiveApPagingTimer() {
	var interval = 10;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (hiveApPagingLiveCount++ < total) {
		hiveApPagingTimeoutId = setTimeout("pollHiveApPagingCache()", interval * 1000);  // seconds
	}
}
function pollHiveApPagingCache() {
	var url = "<s:url action="hiveAp" includeParams="none" />?operation=updates&exConfigGuideFeature=manageAPEx&hmListType=manageAPEx&listTypeFromSession=manageAPEx&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateHiveAp }, null);
}

var cachedRefresh = false;
function updateHiveAp(o) {
	eval("var updates = " + o.responseText);
	var unallowRefresh = false

	for (var i = 0; i < updates.length; i++) {
		if (updates[i].id < 0) {
			if(unallowRefresh){
				cachedRefresh = true;
			}else{
				submitAction('refreshFromCache');
				return;
			}
		}
	}
	if(!unallowRefresh && cachedRefresh){
		submitAction('refreshFromCache');
		return;
	}
	startHiveApPagingTimer();
}

function isAnyItemSelected(){
	var selectedIds = hm.util.getSelectedIds();
	return selectedIds.length > 0 ? true : false;
}

var up_menu = null;
function updateButtonClick(){
	
	if(up_menu == null){
		// updateMenuDiv	toolsMenuDiv
		up_menu = new YAHOO.widget.Menu("updateMenuDiv", { fixedcenter: false, zIndex: 999 });
		
		var tItems=[];
		tItems.push([{ text: '<s:text name="geneva_06.update.menu.update"/>', onclick: { fn: onMenuItemClick, obj: "simpllyUpdate" } }]);
		<s:if test="%{!userSourceFromIdm}">
			tItems.push([{ text: '<s:text name="geneva_06.update.menu.advanced_update"/>', onclick: { fn: onMenuItemClick, obj: "advancedUpdate" } }]);
		</s:if>
		up_menu.addItems(tItems);
		
		up_menu.subscribe("beforeShow", function(){
       		var x = YAHOO.util.Dom.getX('updateMenu');
       		var y = YAHOO.util.Dom.getY('updateMenu');
       		YAHOO.util.Dom.setX('updateMenuDiv', x);
       		YAHOO.util.Dom.setY('updateMenuDiv', y+20);
       	});
		
		up_menu.render();
	}
	up_menu.show();
}

function onMenuItemClick(p_sType, p_aArgs, p_oValue) {
	if(p_oValue == "simpllyUpdate"){
		configSimplifiedUpdate();
	}else if(p_oValue == "advancedUpdate"){
		openUpdateOptionPanel("Device Upload Options", "updateOptionEx");
	}
}

function configSimplifiedUpdate(){
	arrayOperations = [
    	"checkConnectStatus",
    	"getDeviceCounts",
    	"getRebootDevices",
    	"checkNetworkPolicy",
    	"uploadWizard"
    ];
	configUpdateOperation();
}

function configUpdateAdvanceMode_express(params){
	arrayOperations = [
			"checkConnectStatus",
			"checkNetworkPolicy",
			["uploadWizard", params]
		];
	configUpdateOperation();
}

</script>
<tiles:insertDefinition name="deviceMappingRedirector" />