<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
	<style>
.active{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/active.png) no-repeat center;
	vertical-align:center
}
.inactive{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/inactive.png) no-repeat center;
	vertical-align:center
}
.managed{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/yes.png) no-repeat center;
	vertical-align:center
}
.unmanaged{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/no.png) no-repeat center;
	vertical-align:center
}
</style>
<script src="<s:url value="/js/innerhtml.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />" type="text/javascript"></script>  
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript" src="js/app/wrapper.js"></script>
<script type="text/javascript">
function onLoadPage(){
	createWaitingPanel();
	$("#editTable").hide();
	//start client paging timer
	<s:if test="%{pageAutoRefresh}">
	    startClientPagingTimer();
	</s:if>
}
var waitingPanel = null;
var wipeConfirmDialog = null;
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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams='none'/>" />');
	waitingPanel.render(document.body);
	overlayManager.register(waitingPanel);
	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}


 function insertPageContext()
{
	document.writeln('<td class="crumb" nowrap>Managed Clients</td>');
}

function showOperationMenu()
{
	operationMenu.show();
}

var operationMenu;
function createOperationMenu()
{
	operationMenu = new YAHOO.widget.Menu('operation_menu', { fixedcenter: false });
	var operationItems = [
			 [
				/* { text: '<s:text name="monitor.enrolled.client.operaion.refresh"/>', onclick: { fn: doRefreshOp }}, */
				{ text: '<s:text name="monitor.enrolled.client.operation.retrieve.device"/>', onclick: { fn: doRetrieveDevice}},
		        { text: '<s:text name="monitor.enrolled.client.operation.unenroll"/>', onclick: { fn: doUnenrollOp }},
		        { text: '<s:text name="monitor.enrolled.client.operation.delete.device"/>', onclick: { fn: doDeleteDeviceOp }},
		        { text: '<s:text name="monitor.enrolled.client.opeation.lock.device"/>',onclick: {fn: lockDevices}},
		        { text: '<s:text name="monitor.enrolled.client.opeation.clear.passcode"/>',onclick: {fn: clearDevicePasscode}},
		        { text: '<s:text name="monitor.enrolled.client.operation.wipe.device"/>', onclick: { fn: doWipeDeviceOp}}
			 ]
	];
	
	operationMenu.addItems(operationItems);
	operationMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('operationMenuBtn');
		var y = YAHOO.util.Dom.getY('operationMenuBtn');
		YAHOO.util.Dom.setX('operation_menu', x);
		YAHOO.util.Dom.setY('operation_menu', y+20);
	});
	
	operationMenu.render(document.body);
}

function clearDevicePasscode(){
	
	submitAction("clearPasscode");
	
}

function lockDevices(){
	
	submitAction("lockDevices");
	
}
function doRetrieveDevice(){
	
	submitAction("retrieveDevices");
}
function doDeleteDeviceOp(){
	
	submitAction("deleteDevices");
	
}
function doWipeDeviceOp(){
	
	submitAction("wipeDevices");
}
function doRefreshOp(){
	
	submitAction("refreshDevices");
	
}

function doUnenrollOp(){
	
	submitAction("unenrollDevices");
}


//Prompt for confirm wipe, delete, unenroll and retrieve
function checkAndConfirmoOperationRule(idName,operation) {
	var inputElements = document.getElementsByName(idName);
		if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
			if(operation == "deleteDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.delete.item.message"/>');
				warnDialog.show();
			}
			if(operation == "wipeDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.wipe.item.message"/>');
				warnDialog.show();
			}
			if(operation == "unenrollDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.unenroll.item.message"/>');
				warnDialog.show();
			}
			if(operation == "retrieveDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.refresh.item.message"/>');
				warnDialog.show();
			}
			if(operation == "lockDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.lock.item.message"/>');
				warnDialog.show();
			}
			if(operation == "clearPasscode"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.clear.passcode.item.message"/>');
				warnDialog.show();
			}
			
		} else if (!hm.util.hasCheckedBoxes(inputElements)) {
			if(operation == "deleteDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
			if(operation == "wipeDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
			if(operation == "unenrollDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
			if(operation == "retrieveDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
			if(operation == "lockDevices"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
			if(operation == "clearPasscode"){
				warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.no.selected.item.message"/>');
				warnDialog.show();
			}
		} else {
			if(operation == "deleteDevices"){
				if(!checkManagedType("delete")){
					return false;
				}
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.delete.success.warn.message"/>');
				confirmDialog.show();
			}
			if(operation == "wipeDevices"){
				if(!checkManagedType("wipe")){
					return false;
				}
				/* if(!checkWipeType("wipe")){
					return false;
					} */
				/* wipeConfirmDialog = createFirstConfirmDialog(); 
				wipeConfirmDialog.cfg.setProperty('text', "<html><body>This operation is going to remove all data on the end user's device, including the personal contacts, photos and files. Are you sure you want to proceed?</body></html>");
				wipeConfirmDialog.show();*/
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.wipe.success.warn.message"/>');
				confirmDialog.show();
			}
			if(operation == "clearPasscode"){
				if(!checkManagedType("clear the passcode of")){
					return false;
				}
				/* if(!checkWipeType("clear the passcode of")){
					return false;
					} */
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.clear.passcode.success.warn.message"/>');
				confirmDialog.show();
			}
			if(operation == "unenrollDevices"){
				if(!checkManagedType("unenroll")){
					return false;
				}
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.unenroll.success.warn.message"/>');
				confirmDialog.show();
			}
			if(operation == "retrieveDevices"){
				if(!checkManagedType("refresh")){
					return false;
				}
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.refresh.success.warn.message"/>');
				confirmDialog.show();
			}
			if(operation == "lockDevices"){
				if(!checkManagedType("lock")){
					return false;
				}
				confirmDialog.cfg.setProperty('text', '<s:text name="enrolled.client.lock.success.warn.message"/>');
				confirmDialog.show();
			}
		}
}
function createFirstConfirmDialog(){
	var firstConfirmDialog = new YAHOO.widget.SimpleDialog("simpleObjectConfirmDialog",
          { width: "350px",
            fixedcenter: true,
            visible: false,
            draggable: true,
            modal:true,
            close: true,
            icon: YAHOO.widget.SimpleDialog.ICON_WARN,
            constraintoviewport: true,
            buttons: [ { text:"Yes", handler:checkContinueWipeOper, isDefault:true },
                       { text:"&nbsp;No&nbsp;", handler:function(){this.hide()} } ]
          } );
	firstConfirmDialog.setHeader("Confirm");
	firstConfirmDialog.render(document.body);
     return firstConfirmDialog;
}
function checkContinueWipeOper(){
	if(wipeConfirmDialog != null){
		wipeConfirmDialog.hide();
	}
	confirmDialog.cfg.setProperty('text', "<html><body>All data on the end user's device will be permanently deleted. Please confirm</body></html>");
	confirmDialog.show();
}

var formName = 'enrolledClients';
var thisOperation;
function submitAction(operation) {
	
    thisOperation = operation;
    
    if(operation == "deleteDevices"){
    	
    //	hm.util.checkAndConfirmDelete();
    	checkAndConfirmoOperationRule("selectedIds","deleteDevices");
    	
	}else if(operation == "wipeDevices"){
		
		checkAndConfirmoOperationRule("selectedIds","wipeDevices");
		
	}else if(operation == "unenrollDevices"){
		
		checkAndConfirmoOperationRule("selectedIds","unenrollDevices");
		
	}else if(operation == "retrieveDevices"){
		
		checkAndConfirmoOperationRule("selectedIds","retrieveDevices");
		
	}else if(operation == "clearPasscode"){
		
		checkAndConfirmoOperationRule("selectedIds","clearPasscode");
	}else if(operation == "lockDevices"){
		
			checkAndConfirmoOperationRule("selectedIds","lockDevices");
	}else{
		doContinueWipeOper();
	}
}
function doContinueWipeOper(){
	if(thisOperation == "wipeDevices"){
		checkContinueWipeOper(thisOperation);
	}else{
		doContinueOper();
	}
}


function doContinueOper(operation) {
	if(thisOperation == "unenrollDevices"){
			showProcessing();
		document.getElementById("enrolledClients").action="unenrolledClients.action?selectedClientIDStr="+
		hm.util.getSelectedIds(); 
		document.getElementById("enrolledClients").submit();
	}else if(thisOperation == "deleteDevices"){
			showProcessing();
			document.getElementById("enrolledClients").action="doDeleteDevices.action?selectedClientIDStr="+
			hm.util.getSelectedIds();
			document.getElementById("enrolledClients").submit();
	}else if(thisOperation == "wipeDevices"){
			showProcessing();
			document.getElementById("enrolledClients").action="deWipeDevices.action?selectedClientIDStr="+
			hm.util.getSelectedIds();
			document.getElementById("enrolledClients").submit();
	}else if(thisOperation == "refreshDevices"){
				showProcessing();
				$("#enrolledClients").attr("action",'refreshClients.action');
				$("#enrolledClients").submit();		
	}else if(thisOperation == "retrieveDevices"){
			showProcessing();
			document.getElementById("enrolledClients").action="doRetrieveDevices.action?selectedClientIDStr="+
			hm.util.getSelectedIds();
			document.getElementById("enrolledClients").submit();
	}else{
		showProcessing();
		document.forms[formName].operation.value = thisOperation;
		document.forms[formName].submit();
	}
}
var selectedArray;
/* function  checkWipeType(operation){
	var byodValue = true;
	$("input[name='selectedIds']").each(function (n,value){
		if($(this).attr("checked") == "checked"){
			var ownerShip = $('#markOwnerShip_' + n).text();
			if(ownerShip != 1){
				warnDialog.cfg.setProperty('text', "Can't " + operation + " the BYOD devices");
				warnDialog.show();
				byodValue = false;
			}
		}

	});
	if(!byodValue){
		return false;
	}
	return true;
} */

function  checkManagedType(operation){
	var byodValue = true;
		$("input[name='selectedIds']").each(function (n,value){
			if($(this).attr("checked") == "checked"){
				var man = $('#markManaged_' + n).text();
				if(operation == "delete"){
					if(man != 0){
						warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.operation.stop.warning.message"/>');
						warnDialog.show();
						byodValue = false;
					}
				}else{
					if(man == 0){
						warnDialog.cfg.setProperty('text', '<s:text name="enrolled.client.operation.stop.delete.warning.message"/>');
						warnDialog.show();
						byodValue = false;
					}
				}
			}

		});
	
	if(!byodValue){
		return false;
	}
	return true;
}
 
function onUnloadPage() {
  clearTimeout(clientPagingTimeoutId);
}
var doCustomAutoRefreshSettingSubmit = function(postfix) {
	var baseUrl = "<s:url action="enrolledClients" includeParams="none" />?ignore=" + new Date().getTime();
	var url = baseUrl + "&" + postfix;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateAutoRefreshSetting }, null);
}
var updateAutoRefreshSetting = function(o) {
	eval("var result = " + o.responseText);
	if (hm.util.isAFunction(updateAutoRefreshStatus)) {
		updateAutoRefreshStatus(result.autoOn);
	}
	if (result.autoOn == false) {
		clearTimeout(clientPagingTimeoutId);
	} else {
		if (result.refreshOnce) {
			submitAction('refreshFromCache');
		} else {
			startClientPagingTimer();
		}
	}
}

var clientPagingLiveCount = 0;
var clientPagingTimeoutId;
var duration = <s:property value="%{sessionTimeOut}" /> * 60;  // minutes * 60
var interval = <s:property value="%{pageRefInterval}"/>; // seconds
var total = duration / interval;

function startClientPagingTimer() {
	if (clientPagingLiveCount++ < total) {
		clientPagingTimeoutId = setTimeout("pollEnrolledClientsPaging()", interval * 1000);  // seconds
	}
}
function pollEnrolledClientsPaging() {
	var url = "<s:url action="enrolledClients" includeParams="none" />?operation=pollEnrolledClientsList&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateClient }, null);
}
//cached value for refresh which is updated when overlay is showing
var cachedRefresh = false;

function updateClient(o) {
	eval("var updates = " + o.responseText);

	for (var i = 0; i < updates.length; i++) {
		if (updates[i].id < 0) {
			submitAction('refreshFromCache');
			return;
		}
	}
	startClientPagingTimer();
}

function isAnyOverlayShowing(){
	if(null != overlayManager){
		var overlays = overlayManager.overlays;
		for(var i=0; i<overlays.length; i++){
			if(overlays[i].cfg.getProperty("visible")){
				return true;
			}
		}
	}
	return false;
}

function isAnyItemSelected(){
	var selectedIds = hm.util.getSelectedIds();
	return selectedIds.length > 0 ? true : false;
}


</script>
<div id="content">
	<s:form action="enrolledClients" id="enrolledClients" name="enrolledClients">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					 <tiles:insertDefinition name="context" /> 
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
						<script>createOperationMenu();</script> 
						<!-- createSettingMenu(); -->
							<td>
								<input type="button" name="ignore" value="Operation..."
									class="button" id="operationMenuBtn"
									onclick="showOperationMenu();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<div id="o_menu" class="yuimenu"></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%" >
						<tr>
							<td>
								<table width="100%" cellspacing="0" cellpadding="0" border="0"
									class="view">
									<thead>
									<tr>
										<th class="check">
											<input type="checkbox" id="checkAll"
												onClick="hm.util.toggleCheckAll(this);">
										</th>
									 <s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 0}">
												<th style="display:none"></th>
									 		</s:if>
											<s:if test="%{columnId == 1}">
												<th>
													<ah:sort name="DeviceName" key="monitor.enrolled.client.name"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th>
													<ah:sort name="EnrollUserName" key="monitor.enrolled.client.user.name"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th>
													<ah:sort name="ActiveStatus" key="monitor.enrolled.client.status"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 5}">
												<th>
													<ah:sort name="Status" key="monitor.enrolled.client.managed"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 4}">
												<th>
													<ah:sort name="OwnerType" key="monitor.enrolled.client.ownership"/>
												</th>
											</s:if>
											<s:if test="columnId == 11">
												<th>
													<ah:sort name="LastConnectedTime" key="monitor.enrolled.client.connect"/>
												</th>
											</s:if>
											 <s:if test="%{columnId == 6}">
												<th>
													<ah:sort name="WifiMAC" key="monitor.enrolled.client.wifi.macadd"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 8}">
												<th>
													<ah:sort name="OsVersion,OsType" key="monitor.enrolled.client.os.version"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 10}">
												<th>
													<s:text name="monitor.enrolled.client.sys.mode"/>
												</th>
											</s:if>
										</s:iterator> 
									</tr>
									</thead>
									<tbody>
									<s:if test="page.size==0">
										<ah:emptyList/>
									</s:if>
									<tiles:insertDefinition name="selectAll" />
 							<s:iterator value="page" status="num">
										 <tiles:insertDefinition name="rowClass" /> 
										<tr class="<s:property value='#rowClass'/>"> 
											<td class="listCheck">
												  <ah:checkItem/>
											</td>  
											
											 <s:iterator value="%{selectedColumns}" status="mark">
												<s:if test="%{columnId == 0}">
													<td style="display:none"><div id="markManaged_<s:property value='#num.index'/>"><s:property value='managed'/></div><div id="markOwnerShip_<s:property value='#num.index'/>"><s:property value="ownerShip"/></div></td>
												</s:if>
												<s:if test="%{columnId == 1}">
													<td nowrap="nowrap" class="list">
															<a href='<s:url action="showDetails">
															<s:param name="operation" value="{'showDetails'}"/>
															<s:param name="deviceId" value="%{deviceId}"/>
															<s:param name="name" value="%{name}"/>
															</s:url>'>
															<s:property value="name"/></a>
													</td>
												</s:if>
												<s:if test="%{columnId == 2}">
												<s:if test="%{enrollUserName == null || enrollUserName ==''}">
														<td class="list" nowrap="nowrap">
																<s:text name="monitor.enrolled.device.detail.info.blank"/>
														</td>
													</s:if>
													<s:else>
													<td class="list" nowrap="nowrap">
														<s:property value="enrollUserName"/>
													</td>
													</s:else>
												</s:if>
												<s:if test="%{columnId == 3}">
												<td class="list" nowrap="nowrap">
														<s:if test="%{status == 1}">
															<div class="active" title="<s:text name="monitor.enrolled.client.status.on"/>"></div>&nbsp;
														</s:if>
														<s:else>
															<div class="inactive" title="<s:text name="monitor.enrolled.client.status.off"/>"></div>&nbsp;
														</s:else>
													</td>
												</s:if>
												<s:if test="%{columnId == 5}">
												    <td class="list" nowrap="nowrap">
														<s:if test="%{managed == 0}">
															<div class="unmanaged" title="<s:text name="monitor.enrolled.client.managed.no"/>"></div>&nbsp;
														</s:if>
														<s:else>
															<div class="managed" title="<s:text name="monitor.enrolled.client.managed.yes"/>"></div>&nbsp;
														</s:else>
													</td>
												</s:if>
												<s:if test="%{columnId == 4}">
													<td class="list" nowrap="nowrap"><s:if test="%{ownerShip == 0}"><s:text name="monitor.enrolled.client.ownership.unknown"/></s:if><s:if test="%{ownerShip == 1}"><s:text name="monitor.enrolled.client.ownership.public"/></s:if><s:if test="%{ownerShip == 3}"><s:text name="monitor.enrolled.client.ownership.private"/>(<font color="red"><strong><s:text name="monitor.enrolled.client.ownership.mismatch"/></strong></font>)</s:if><s:if test="%{ownerShip == 2}"><s:text name="monitor.enrolled.client.ownership.private"/></s:if>
													<s:if test="%{ownerShip == 4}"><s:text name="monitor.enrolled.client.ownership.public"/>(<font color="red"><strong><s:text name="monitor.enrolled.client.ownership.mismatch"/></strong></font>)</s:if></td>
												</s:if>
												<s:if test="%{columnId == 11}">
													<td class="list" nowrap="nowrap">
														<s:property value="lastCon" />&nbsp;
													</td>
												</s:if>
												 <s:if test="%{columnId == 6}">
													<td class="list" nowrap="nowrap">
															<s:if test="%{wifiMac == null}">
																<s:text name="monitor.enrolled.device.detail.info.blank"/>
															</s:if>
															<s:else>
																<s:property value="wifiMac"/>
															</s:else>
													</td>
												</s:if>
												<s:if test="%{columnId == 8}">
													<td class="list" nowrap="nowrap">
														<s:if test="%{platForm == 1}">
															<s:text name="monitor.enrolled.client.os.version.os"/> <s:property value="osVersion" />&nbsp;
														</s:if>
														<s:if test="%{platForm == 2}">
															<s:text name="monitor.enrolled.client.os.version.andriod"/> <s:property value="osVersion" />&nbsp;
														</s:if>
														<s:if test="%{platForm == 3}">
															<s:text name="monitor.enrolled.client.os.version.osx"/> <s:property value="osVersion" />&nbsp;
														</s:if>
													</td>
												</s:if>
												<s:if test="%{columnId == 10}">
													<td class="list" nowrap="nowrap">
														<s:property value="sysMode" />&nbsp;
													</td>
												</s:if>
											</s:iterator> 
										</tr>  
									</s:iterator> 
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>


