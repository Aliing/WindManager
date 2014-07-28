<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.be.admin.QueueOperation.HHMUpdateStatusItem"%>
<%@page import="com.ah.be.admin.QueueOperation.HMUpdateStatus"%>

<s:if test="%{externalUpdate}">
	<style type="text/css">
	.client {
		background-image: none;
	}
</style>
</s:if>
<script>
var formName = 'updateSoftware';
var actionUrl;
<s:if test="%{externalUpdate}">
	actionUrl = "<s:url action='updateSoftwareExt' includeParams='none' />";
</s:if>
<s:else>
	actionUrl = "<s:url action='updateSoftware' includeParams='none' />";
</s:else>

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
	waitingPanel.setHeader("Uploading software...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var updateStatusOverLay;
var sysName = '<s:property value="%{systemNmsName}"/>';
var noConnectOld = 0;
var failConnectNew = 0;
function onLoadPage() {
	<s:if test="%{externalUpdate}">
		document.forms[formName].action = actionUrl;
	</s:if>
	<s:if test="%{updateSource == 'licenseServer' && downloadFileName == ''}">
		openDownloadFilePanel();
	</s:if>
	// Overlay for waiting dialog
	createWaitingPanel();
	
	// create update status overlay
	var div = document.getElementById('updateStatusPanel');
	updateStatusOverLay = new YAHOO.widget.Panel(div, {
		width:"350px",
		visible:false,
		fixedcenter:true,
		close:false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:4
		});
	updateStatusOverLay.render(document.body);
	div.style.display = "";
	
	<s:if test="%{needConfirm}">
		hm.util.hide('hideCommunicationTestResult');
		hm.util.show('vhmUpdateResult');
	</s:if>
	//HM update status Panel
	initHmUpdateStatusPanel();
}

function submitAction(operation) {
	<s:if test="%{externalUpdate || isInHomeDomain}">
		// home domain update
		if (validate(operation))
		{
			document.forms[formName].operation.value = operation;
	
			confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.restoreDB.updateSoftware"/>');
	  		confirmDialog.show();
		}
	</s:if>
	<s:else>
		// hhm update
		if (validateHHMUpdate())
		{
			if (operation == 'connectivityTest' ||  operation == "retryTest") {
				var updateVersion = document.getElementById('updateVersion');
				var url ;
				if (operation == "retryTest") {
					url = actionUrl + "?operation=retryTest&upVersion="+ encodeURIComponent(updateVersion.options[updateVersion.selectedIndex].value) +"&ignore=" + new Date().getTime();
				} else {
					url = actionUrl + "?operation=connectivityTest&upVersion="+ encodeURIComponent(updateVersion.options[updateVersion.selectedIndex].value) +"&ignore=" + new Date().getTime();
				}
				var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : connectivityTestResult}, null);
				
				if(waitingPanel != null){
					waitingPanel.setHeader("Connectivity testing...");
					waitingPanel.show();
				}
			} else if (operation == 'resultUpdate') {
				var msg;
				if (noConnectOld > 0 || failConnectNew > 0) {
					if (noConnectOld > 0) {
						if (failConnectNew > 0) {
							var paramStr1 = noConnectOld > 1 ? (noConnectOld+" devices are") : "1 device is";
							var paramStr2 = failConnectNew > 1 ? (failConnectNew+" devices") : "1 device";
							msg = '<s:text name="administrate.vhm.upgrade.firewall.warning.1"><s:param>'+paramStr1+'</s:param><s:param>'+paramStr2+'</s:param></s:text>';
						} else {
							var paramStr = noConnectOld > 1 ? (noConnectOld+" devices are") : "1 device is";
							msg = '<s:text name="administrate.vhm.upgrade.firewall.warning.3"><s:param>'+paramStr+'</s:param></s:text>';
						}
					} else if (failConnectNew > 0) {
						var paramStr = failConnectNew > 1 ? (failConnectNew+" devices") : "1 device";
						msg = '<s:text name="administrate.vhm.upgrade.firewall.warning.2"><s:param>'+paramStr+'</s:param></s:text>';
					}
					msg = msg + '<br>' + '<s:text name="administrate.vhm.upgrade.firewall.warning" />';
					confirmDialog.cfg.setProperty('text', msg);
			  		confirmDialog.show();
				} else {
					doContinueOper();
				}
			} else {
				if (operation == 'update') {
					document.getElementById("hideOkBtn").style.display="none";
					document.getElementById("hideVersionList").style.display="none";
					document.getElementById("hideCommunicationTest").style.display="block";
				} else if (operation == 'cancel' || operation == 'cancelTest') {
					if (operation == 'cancel') {
						document.getElementById("hideOkBtn").style.display="block";
						document.getElementById("hideVersionList").style.display="block";
						document.getElementById("hideCommunicationTest").style.display="none";
					} else {
						document.getElementById("hideCommunicationTestResult").style.display="none";
						document.getElementById("hideRetryTestBtn").style.display="none";
						document.getElementById("hideCommunicationTest").style.display="block";
						var url = actionUrl + "?operation=cancelConnTest"+"&ignore=" + new Date().getTime();
						var transaction = YAHOO.util.Connect.asyncRequest('GET', url, null, null);
					}
				}
			}
		}
	</s:else>
}

function connectivityTestResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	
	if (result.success)
	{
		document.getElementById("hideCommunicationTestResult").style.display = "block";
		document.getElementById("hideCommunicationTest").style.display = "none";
		
		var connectTestResult = result.testResult;
		noConnectOld = result.noConnectListLength;
		failConnectNew = result.failConnectListLength;
		document.getElementById("deviceCount").innerHTML=result.deviceCount;
		document.getElementById("connectOldDevicesCount").innerHTML=result.connectListLength;
		document.getElementById("noConnectOldDevicesCount").innerHTML=connectTestResult ? noConnectOld
				: ("<a onclick='openConnectivityTestResultPanel(0)'>"+noConnectOld+"</a>");
		document.getElementById("connectNewDevicesCount").innerHTML=result.sucConnectListLength;
		document.getElementById("noConnectNewDevicesCount").innerHTML=connectTestResult ? failConnectNew
				: ("<a onclick='openConnectivityTestResultPanel(1)'>"+failConnectNew+"</a>");
		
		document.getElementById("hideRetryTestBtn").style.display = connectTestResult ? "none" : "block";
	}
	else
	{
		showErrorMessage(result.message);
	}
}

function submitConfirmAction()
{
	document.forms[formName].operation.value = 'confirmUpdate';
	document.forms[formName].submit();
}

function doContinueOper()
{
	<s:if test="%{externalUpdate || isInHomeDomain}">
		// home domain update
		var isLocalHost = !document.getElementById("localFile").disabled;
		if (isLocalHost)
		{
			initProgressConfig();
		}
		else
		{
			abortResult();
		}
	</s:if>
	<s:else>
		// hhm update
		var updateVersion = document.getElementById('updateVersion');
		var url = actionUrl + "?operation=updateHHM&upVersion="+ encodeURIComponent(updateVersion.options[updateVersion.selectedIndex].value) +"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateResult }, null);
		
		if(waitingPanel != null){
			waitingPanel.setHeader("Preparing resources...");
			waitingPanel.show();
		}
	</s:else>
}

var pollTimeoutId;
var interval = 1;

function startPollUpdateStatusTimer() {
	pollTimeoutId = setTimeout("pollUpdateStatus()", interval * 1000);  // seconds
}
function pollUpdateStatus() {
	<s:if test="%{needConfirm}">
		// sometimes, timer not be stopped successfully after update, so add this code for fix
		if(null != updateStatusOverLay){
			updateStatusOverLay.cfg.setProperty('visible', false);
		}
	
		return;
	</s:if>

	var url = actionUrl + "?operation=pollUpdateStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateStatus }, null);
}

function onUnloadPage() {
	clearTimeout(pollTimeoutId);
}

function updateStatus(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	if(null != updateStatusOverLay){
		updateStatusOverLay.cfg.setProperty('visible', false);
	}
	
	eval("var result = " + o.responseText);
	var status = result.status;
	if (status == <%=HHMUpdateStatusItem.UPDATE_FINISHED%>)
	{
		clearTimeout(pollTimeoutId);
		if (result.success) {
			hm.util.hide('hideCommunicationTestResult');
			hm.util.show('vhmUpdateResult');
		} else {
			showErrorMessage(result.message);
		}
		
		return;
	} else if (status == <%=HHMUpdateStatusItem.UPDATE_WAITTING%>)
	{
		document.getElementById('updateWaitTD').style.display = "block";
		document.getElementById('updateRunTD').style.display = "none";
	
		document.getElementById("updateStatusTD").innerHTML = "<td id='updateStatusTD'>"+ result.message +"</td>";
		
		interval = 5;
		
	} else if (status == <%=HHMUpdateStatusItem.UPDATE_RUNNING%>)
	{
		document.getElementById('updateWaitTD').style.display = "none";
		document.getElementById('updateRunTD').style.display = "block";
	
		// update status icon
		var backupImg = document.getElementById("backupImg");
		var transferImg = document.getElementById("transferImg");
		var restoreImg = document.getElementById("restoreImg");
		var laterImg = document.getElementById("laterImg");
		var runStatus = result.runStatus;
		if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Backup_Data%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			transferImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		} 
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Move_Data%>) 
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			restoreImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		}
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Restore_Data%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		}
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Change%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		}
		
		interval = 1;
	}
	
	<s:if test="%{needConfirm}">
		// sometimes, timer not be stopped successfully after update, so add this code for fix
		return;
	</s:if>
	
	if(null != updateStatusOverLay){
		updateStatusOverLay.cfg.setProperty('visible', true);
	}
	
	startPollUpdateStatusTimer();
}

function updateResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	
	if (result.success)
	{
		startPollUpdateStatusTimer();
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
	else
	{
		showErrorMessage(result.message);
	}
}

function cancelUpdate()
{
	var url = actionUrl + "?operation=cancelUpdate&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : cancelUpdateStatus }, null);
}

function cancelUpdateStatus(o)
{
	eval("var data = " + o.responseText);
	if (data.success)
	{
		clearTimeout(pollTimeoutId);
		if(null != updateStatusOverLay){
			updateStatusOverLay.cfg.setProperty('visible', false);
		}
	}
}

function initProgressConfig()
{
	var localFile = document.getElementById("localFile");

	url = actionUrl + "?operation=initProgessConfig&localFileFileName="+encodeURIComponent(localFile.value)+"&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: abortResult, failure:abortResult,timeout: 5000}, null);
}

var abortResult = function(o)
{
	try
    {
		hideNotes();
    	document.forms[formName].submit();
    	if(TrackHmStatusPanel != null){
		  TrackHmStatusPanel.show();
		}
    	startTrackHmUpdateStatus();
  	}
  	catch (e)
  	{
  	 	if (e instanceof Error && e.name == "TypeError")
  	 	{
  	 		url = actionUrl + "?operation=endProgessConfig&ignore=" + new Date().getTime();

			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: focusUpdateFile, failure: focusUpdateFile,timeout: 5000}, null);
  		}
 	}
}

var focusUpdateFile = function(o)
{
	var file = document.getElementById("localFile");
	hm.util.reportFieldError(file, '<s:text name="error.fileNotExist"></s:text>');
	file.focus();

	//hm.util.hide('processing');

	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function validate(operation) {
	var isLocalHost = !document.getElementById("localFile").disabled;
	if (isLocalHost) {
		var localFile = document.getElementById("localFile");
		if ( localFile.value.length == 0) {
            hm.util.reportFieldError(localFile, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.filePath" /></s:param></s:text>');
            localFile.focus();
            return false;
   	 	}
	} else if (!document.getElementById("serverIP").disabled){
		var ip = document.getElementById("serverIP");
		var port = document.getElementById("scpPort");
		var filePath = document.getElementById("filePath");
		var userName = document.getElementById("userName");

		var password;
		if (document.getElementById("chkToggleDisplay").checked)
		{
		    password = document.getElementById("password");
		}
		else
		{
			password = document.getElementById("password_text");
		}


		if (ip.value.length == 0)
		{
            hm.util.reportFieldError(ip, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.ip" /></s:param></s:text>');
            ip.focus();
            return false;
        }
        else if (! hm.util.validateIpAddress(ip.value))
        {
			hm.util.reportFieldError(ip, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.updateSoftware.ip" /></s:param></s:text>');
			ip.focus();
			return false;
		}

		if (port.value.length == 0)
		{
            hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.scpPort" /></s:param></s:text>');
            port.focus();
            return false;
        }
        else if ( !isValidPort(port) )
        {
        	hm.util.reportFieldError(port, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.updateSoftware.scpPort" /></s:param><s:param><s:text name="admin.updateSoftware.scpPortRange" /></s:param></s:text>');
			port.focus();
			return false;
        }

        if (filePath.value.length == 0)
		{
            hm.util.reportFieldError(filePath, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.filePath" /></s:param></s:text>');
            filePath.focus();
            return false;
        }

        if (userName.value.length == 0)
		{
            hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.user" /></s:param></s:text>');
            userName.focus();
            return false;
        }

        if (password.value.length == 0)
		{
            hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.Password" /></s:param></s:text>');
            password.focus();
            return false;
        }
    // has download the upgrade file from license server
	} else {
		waitingPanel.setHeader("Processing...");
	}
    
	return true;
}

function validateHHMUpdate()
{
	var upver = document.getElementById("updateVersion");
   	if(upver.options.length == 0 || upver.options[upver.selectedIndex].value.indexOf(".") < 0) {
   		hm.util.reportFieldError(upver, '<s:text name="error.admin.download.software.no.version" />');
   		upver.focus();
        return false;
   	}
   	
   	return true;
}

function isValidPort(port)
{
	var intValue = parseInt(port.value);
	if ( intValue>=0 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

function insertPageContext() {
	<s:if test="%{externalUpdate}">
		document.writeln('<td class="crumb" nowrap><s:property value="%{displayedTitle}" />');
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
		document.writeln('</td>');
	</s:else>
}

function selectLocalhost(checked, ifLicense)
{
	if (checked)
	{
		document.getElementById("localFile").disabled = ifLicense;
		document.getElementById("serverIP").disabled = true;
		document.getElementById("scpPort").disabled = true;
		document.getElementById("filePath").disabled = true;
		document.getElementById("userName").disabled = true;
		document.getElementById("chkToggleDisplay").disabled = true;
		document.getElementById("password").disabled = true;
		document.getElementById("password_text").disabled = true;
		if (ifLicense) {
			openDownloadFilePanel();
		}
	}
}

function selectRemoteServer(checked)
{
	if (checked)
	{
		document.getElementById("localFile").disabled = true;
		document.getElementById("serverIP").disabled = false;
		document.getElementById("scpPort").disabled = false;
		document.getElementById("filePath").disabled = false;
		document.getElementById("userName").disabled = false;
		document.getElementById("chkToggleDisplay").disabled = false;
		document.getElementById("password").disabled = !document.getElementById("chkToggleDisplay").checked;
		document.getElementById("password_text").disabled = document.getElementById("chkToggleDisplay").checked;
	}
}

function selectPartUpdate(checked)
{
}

function selectFullUpdate(checked)
{
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var cell = document.getElementById("desFromLicense");
	cell.innerHTML = details.v;
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function getNewDescription() {
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("downloadFileFrame").style.display = "none";
	}
	var url = "<s:url action='updateSoftware' includeParams='none' />" + "?operation=getNewDes&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteInfo";
	hm.util.show("noteSection");
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

var hideErrNotes = function () {
	hm.util.wipeOut('noteSection', 800);
}

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
	// err msg display 8 second
	var notesTimeout = setTimeout("hideErrNotes()", 8000);
}

function initNoteSection()
{
	hm.util.hide('noteSection');
}

</script>

<div id="content">
	<s:form action="updateSoftware" enctype="multipart/form-data"
		method="POST">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr id="hideOkBtn">
				<td class="buttons">
					<input type="button" id="submitBtn" name="ok" value="OK" class="button"
					 onClick="submitAction('update');" <s:property value="%{upgradeButtonDisabled}" />>
				</td>
			</tr>
			<tr>
				<td colspan="10">
					<div id="noteSection" style="display:none">
						<table width="700px" border="0" cellspacing="0" cellpadding="0"
							class="note">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td id="noteTD" nowrap="nowrap">
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<s:if test="%{externalUpdate || isInHomeDomain}">
				<script>
				function delayHideNotes() {}
			</script>
				<tr>
					<td>
						<table class="editBox" cellspacing="0" cellpadding="0" border="0"
							width="800px">
							<tr>
								<td height="10">
									<%-- add this password dummy to fix issue with auto complete function --%>
									<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
										type="password">
								</td>
							</tr>
							<tr>
								<td style="padding:0 0 0 5px" colspan="2">
									<s:radio label="Gender" id="partUpdate" name="updateScope"
										list="#{'noAlarmEvent':'Update and clear the alarm and event logs'}"
										onclick="selectPartUpdate(this.checked);"
										value="%{updateScope}" disabled="%{upgradeButtonDisabled}" />
								</td>
							</tr>
							<tr>
								<td style="padding:10px 0 0 5px" colspan="2">
									<s:radio label="Gender" id="fullUpdate" name="updateScope"
										list="#{'full':'Full update'}"
										onclick="selectFullUpdate(this.checked);"
										value="%{updateScope}" disabled="%{upgradeButtonDisabled}" />
								</td>
							</tr>
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td style="padding:0 4px 0 4px" colspan="2">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="sepLine">
												<img src="<s:url value="/images/spacer.gif"/>" height="1"
													class="dblk" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr style="display:<s:property value="%{showUpgradeManually}"/>">
								<td style="padding:0 0 0 5px" width="140px">
									<s:radio label="Gender" id="localHost" name="updateSource"
										list="#{'localhost':'File from local host'}"
										onclick="selectLocalhost(this.checked, false);"
										value="%{updateSource}" disabled="%{upgradeButtonDisabled}" />
								</td>
								<td style="padding-top: 10px;">
									<s:file id="localFile" name="localFile" size="40"
										disabled="%{disabledLocal}" />
								</td>
							</tr>
							<tr style="display:<s:property value="%{showUpgradeManually}"/>">
								<td colspan=2 style="padding:0 0 0 5px">
									<s:radio label="Gender" id="remoteServer" name="updateSource"
										list="#{'remoteServer':'File from remote server'}"
										onclick="selectRemoteServer(this.checked);"
										value="%{updateSource}" disabled="%{upgradeButtonDisabled}" />
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
							<tr style="display:<s:property value="%{showUpgradeManually}"/>">
								<td style="padding:5px 0 0 25px" colspan="2">
									<fieldset style="width: 735px">
										<legend>
											<s:text name="admin.updateSoftware.scp" />
										</legend>
										<div>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td class="labelT1" width="80px">
														<label>
															<s:text name="admin.updateSoftware.ip" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td class="labelT1">
														<s:textfield id="serverIP" name="serverIP"
															maxlength="%{ipAddressLength}" disabled="%{disabledSCP}"
															onkeypress="return hm.util.keyPressPermit(event,'ip');" />
													</td>
													<td class="labelT1" width="80px">
														<label>
															<s:text name="admin.updateSoftware.scpPort" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td class="labelT1">
														<s:textfield id="scpPort" name="scpPort"
															maxlength="%{SCPPortLength}" disabled="%{disabledSCP}"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />
														<s:text name="admin.updateSoftware.scpPortRange" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="80px">
														<label>
															<s:text name="admin.updateSoftware.filePath" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td class="labelT1" colspan=3>
														<s:textfield id="filePath" name="filePath"
															maxlength="%{filePathLength}" disabled="%{disabledSCP}"
															size="68" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="80px">
														<label>
															<s:text name="admin.updateSoftware.user" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td class="labelT1">
														<s:textfield id="userName" name="userName"
															maxlength="%{userNameLength}" disabled="%{disabledSCP}" />
													</td>
													<td class="labelT1" width="80px">
														<label>
															<s:text name="admin.updateSoftware.Password" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td class="labelT1">
														<s:password id="password" name="password" size="20"
															maxlength="%{passwdLength}" disabled="%{disabledSCP}" />
														<s:textfield id="password_text" name="password" size="20"
															maxlength="%{passwdLength}" disabled="true" value=""
															cssStyle="display:none" />
													</td>
												</tr>
												<tr>
													<td colspan="3">
														&nbsp;
													</td>
													<td style="padding-left: 10px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td>
																	<s:checkbox id="chkToggleDisplay" name="ignore"
																		value="true" disabled="%{disabledSCP}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['password'],['password_text']);" />
																</td>
																<td>
																	<s:text name="admin.user.obscurePassword" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</fieldset>
								</td>
							</tr>
							<s:if test="%{!externalUpdate && !oEMSystem}">
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td colspan="2">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding:0 0 0 5px">
													<s:radio label="Gender" id="licenseServer"
														name="updateSource" list="#{'licenseServer':''}"
														disabled="%{upgradeButtonDisabled}"
														onclick="selectLocalhost(this.checked, true);"
														value="%{updateSource}" />
												</td>
												<td>
													<label id="desFromLicense" for="licenseServerlicenseServer"><s:property value="%{descriptionForUpdate}" /></label>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</s:if>
							<tr>
								<td height="10"></td>
							</tr>
						</table>
					</td>
				</tr>
			</s:if>
			<s:else>
				<tr>
					<td>
						<div style="display:<s:property value="%{hideVersionList}"/>"
							id="hideVersionList">
							<table class="editBox" cellspacing="0" cellpadding="0" border="0"
								width="450px">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" width="160px">
													<label>
														<s:text name="admin.updateSoftware.available.list" />
													</label>
												</td>
												<td>
													<s:select name="upVersion" value="%{upVersion}"
														list="%{versionList}" cssStyle="width: 150px;"
														id="updateVersion" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
							</table>
						</div>
						<div style="display:none;padding-top:20px" id="vhmUpdateResult">
							<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="450px">
								<tr>
									<td style="padding:10px 0 10px 10px">
										<strong><s:text name="admin.hmOperation.updateSoftware.vhm.result.title" />
										</strong>
									</td>
								</tr>
								<tr>
									<td style="padding:0 0 10px 10px">
										<s:text name="update.software.success.confirm.message" />
									</td>
								</tr>
								<tr>
									<td class="buttons" style="padding:0 0 10px 10px">
										<input type="button" id="activateBtn" value="Activate" class="button"
											onClick="submitConfirmAction();">
									</td>
								</tr>
							</table>
						</div>
						<div style="display:none;padding-top:20px" id="hideCommunicationTest">
							<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="450px">
								<tr>
									<td style="padding:10px 0 10px 10px">
										<strong><s:text name="admin.hmOperation.updateSoftware.communication.test.title" />
										</strong>
									</td>
								</tr>
								<tr>
									<td style="padding:0 0 10px 10px">
										<s:text name="admin.hmOperation.updateSoftware.communication.test.text" />
									</td>
								</tr>
								<tr>
									<td class="buttons" style="padding:0 0 10px 10px">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<input type="button" id="proceedBtn" value="<s:text name="admin.updateSoftware.button.proceed"/>"
														class="button" onClick="submitAction('connectivityTest');">
												</td>
												<td style="display:none" id="hideRetryTestBtn">
													<input type="button" value="<s:text name="admin.updateSoftware.button.retryTest"/>" id="retryTestBtn"
														class="button" onClick="submitAction('retryTest');">
												</td>
												<td>
													<input type="button" value="<s:text name="admin.updateSoftware.button.cancelupdate"/>" id="cancelBtn"
														class="button" onClick="submitAction('cancel');">
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
						<div style="display:none;padding-top:20px" id="hideCommunicationTestResult">
							<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="350px">
								<tr>
									<td style="padding:10px 0 10px 10px">
									<s:text name="admin.updateSoftware.testResult"/>
									</td>
								</tr>
								<tr>
									<td class="buttons" style="padding:0 0 10px 10px">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<input type="button" id="proceedBtn" value="<s:text name="admin.updateSoftware.button.proceed"/>"
														class="button" onClick="submitAction('resultUpdate');">
												</td>
												<td>
													<input type="button" value="<s:text name="admin.updateSoftware.button.cancelupdate"/>" id="cancelBtn"
														class="button" onClick="submitAction('cancelTest');">
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</s:else>
		</table>
	</s:form>
</div>

<div id="updateStatusPanel" style="display:none">
	<div class="hd">
		Update Software ...
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td id="updateWaitTD">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td width="20px">
								<img id="ignore"
									src="<s:url value="/images/waitingSquare.gif" />" />
							</td>
							<td id="updateStatusTD" width="400px">
							</td>
						</tr>
						<tr style="padding-top: 5px">
							<td align="center" colspan="2">
								<input type="button" id="cancelBtn" name="ignore" value="Cancel"
									class="button" onClick="cancelUpdate();" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td id="updateRunTD">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<img id="backupImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="update.software.backup.database" /></td>
						</tr>
						<tr>
							<td>
								<img id="transferImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="update.software.move.data" /></td>
						</tr>
						<tr>
							<td>
								<img id="restoreImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="update.software.restore.database" /></td>
						</tr>
						<tr>
							<td>
								<img id="laterImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="update.software.other.actions" /></td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
							</td>
							<td>
								<img src="<s:url value="/images/waiting.gif" />" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="TrackHmStatusPanel" style="display:none">
	<div class="hd">
		Updating HiveManager Software.....
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<img id="uploadFileImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="hollywood_06.action.uploadFile"></s:text> </td>
						</tr>
						<tr>
							<td>
								<img id="checkFileImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="hollywood_06.action.verifyFile"></s:text> </td>
						</tr>
						<tr>
							<td>
								<img id="extractFileImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="hollywood_06.action.untarFile"></s:text> </td>
						</tr>
						<tr>
							<td>
								<img id="checkEnvImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="hollywood_06.action.checkEnv"></s:text> </td>
						</tr>
						<tr>
							<td>
								<img id="backUpDataImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<td><s:text name="hollywood_06.action.backUpData"></s:text> </td>
						<tr>
							<td>
								<img id="rebootSystemImg"
									src="<s:url value="/images/HM-capwap-down.png" />" />
							</td>
							<s:if test="%{haHHMApp}">
							 <td><s:text name="hollywood_06.action.restartHM"></s:text> </td>
							</s:if><s:else>
							 <td><s:text name="hollywood_06.action.rebootSystem"></s:text></td>
							</s:else>
							
						</tr>
						<tr>
							<td id="sucMsgId" colspan="2"></td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr id="waitingId">
							<td></td>
							<td>
								<img src="<s:url value="/images/waiting.gif" />" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<div id="downloadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="downloadFileFrame" width="100%" height="1" frameborder="0"
			style="background-color: #FFFFFF;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var downloadFilePanel = null;
var oprationName;
function createDownloadFilePanel(width, height){
	var div = document.getElementById("downloadFilePanel");
	var iframe = document.getElementById("downloadFileFrame");
	iframe.width = width;
	iframe.height = height;
	downloadFilePanel = new YAHOO.widget.Panel(div, 
	                                        { width:(width+20)+"px", 
											  fixedcenter:true, 
											  visible:false,
											  draggable: true,
											  constraintoviewport:true } );
	downloadFilePanel.render();
	div.style.display="";
	downloadFilePanel.beforeHideEvent.subscribe(getNewDescription);
}
function openDownloadFilePanel()
{
	if ('' == '<s:property value="%{downloadFileName}"/>') {		
		if(downloadFilePanel==null){
			createDownloadFilePanel(605,200);
		}
		var iframe = document.getElementById("downloadFileFrame");
		
		//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
		if(YAHOO.env.ua.ie){
			iframe.style.display = "";
		}
		downloadFilePanel.setHeader('<s:text name="license.server.available.software.download"/>');
		downloadFilePanel.show();
		// iframe.src ="<s:url action='downloadSoft' includeParams='none' />";
		// fix fiefox bug
		iframe.contentWindow.location ="<s:url action='downloadSoft' includeParams='none' />?operation=downloadSoft";
	}
}
</script>

<div id="connectivityTestResultPanel" style="display: none;">
	<div class="hd"><s:text name="admin.updateSoftware.testResult.menu.title"/></div>
	<div class="bd">
		<iframe id="connectivityTestResultFrame" name="connectivityTestResultFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>

<script type="text/javascript">
var connectivityTestResultPanel = null;

function createConnectivityTestResultPanel(width, height){
	var div = document.getElementById("connectivityTestResultPanel");
	var iframe = document.getElementById("connectivityTestResultFrame");
	iframe.width = width;
	iframe.height = height;
	connectivityTestResultPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	connectivityTestResultPanel.render();
	div.style.display="";
	overlayManager.register(connectivityTestResultPanel);
	connectivityTestResultPanel.beforeHideEvent.subscribe(closeConnectivityTestResultPanel);
	connectivityTestResultPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openConnectivityTestResultPanel(testFailType)
{

	if(null == connectivityTestResultPanel){
		createConnectivityTestResultPanel(540,260);
	}

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("connectivityTestResultFrame").style.display = "";
	}
	
	connectivityTestResultPanel.show();
	var iframe = document.getElementById("connectivityTestResultFrame");

	iframe.src = actionUrl + "?operation=connectivityTestResult&testFailType=" + testFailType;

}

function closeConnectivityTestResultPanel() {
	//connectivityTestResultIFrameWindow.onHidePage();
	//connectivityTestResultPanel.hide();
	
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("connectivityTestResultFrame").style.display = "none";
	}
}
//var connectivityTestResultIFrameWindow;
</script>

<script type="text/javascript">
//create hm update status overlay
var TrackHmStatusPanel;
 function initHmUpdateStatusPanel(){
	 if(TrackHmStatusPanel==null){
		 var hmStatusPanel = document.getElementById('TrackHmStatusPanel');
		 TrackHmStatusPanel=new YAHOO.widget.Panel(hmStatusPanel, {
			width:"350px",
			visible:false,
			fixedcenter:true,
			close:false,
			draggable:false,
			modal:true,
			constraintoviewport:true,
			zIndex:4
			});
		 TrackHmStatusPanel.render(document.body);
		 hmStatusPanel.style.display="";
	 }
}

var trackId;
var uploadFileImg=document.getElementById("uploadFileImg");
var checkFileImg=document.getElementById("checkFileImg");
var extractFileImg=document.getElementById("extractFileImg");
var checkEnvImg=document.getElementById("checkEnvImg");
var backUpDataImg=document.getElementById("backUpDataImg");
var rebootSystemImg=document.getElementById("rebootSystemImg");
var trackInterval=5;
function startTrackHmUpdateStatus() {
	trackId= setTimeout("trackHmUpdateStatus()",trackInterval*1000);  // seconds
}
function trackHmUpdateStatus() {
	var url = actionUrl + "?operation=trackHmUpdateStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : UpdateHmUpgradeStatus }, null);
}
function UpdateHmUpgradeStatus(o){
	eval("var result = " + o.responseText);
	var status = result.status;
	clearTimeout(trackId);
	initHmUpdateStatusImg(status);
	startTrackHmUpdateStatus();
}
function initHmUpdateStatusImg(status){
	if (status == <%=HMUpdateStatus.UPLOAD_FILE%>)
	{
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-down.png" />";
	} else if (status == <%=HMUpdateStatus.CHECK_FILE%>)
	{
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-down.png" />";
	}else if (status == <%=HMUpdateStatus.UNTAR_FILE%>)
	{
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		trackInterval=10;
	} else if (status == <%=HMUpdateStatus.CHECK_ENV%>)
	{
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		trackInterval=10;
	}else if(status == <%=HMUpdateStatus.BACKUP_DATA%>)
	{
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-down.png" />";
	}else if(status == <%=HMUpdateStatus.RESTART_APP%> || status == <%=HMUpdateStatus.REBOOT_SYS%>){
		uploadFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		extractFileImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		checkEnvImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		backUpDataImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		rebootSystemImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		document.getElementById("waitingId").style.display="none";
		document.getElementById("sucMsgId").innerHTML="<s:text name='hollywood_06.update.software.success.message' />";
		initHmUpdateStatusPanel();
		TrackHmStatusPanel.cfg.setProperty('close', true);
		TrackHmStatusPanel.show();
	}
}
//restart SoftWare
<s:if test="%{restartApp}">
    initHmUpdateStatusImg(<%=HMUpdateStatus.RESTART_APP%>);
</s:if>
//reboot System
<s:if test="%{rebootSys}">
    initHmUpdateStatusImg(<%=HMUpdateStatus.REBOOT_SYS%>);
</s:if>

function removeErrorMsg(){
	hideNotes();
	var url = actionUrl + "?operation=removeErrorMsg&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('GET', url,null, null);
}
</script>
