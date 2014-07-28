<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.be.admin.QueueOperation.RestoreStatusItem"%>
<%@page import="com.ah.util.EnumConstUtil"%>

<script>
var formName = 'restoreDB';
var thisOperation;

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
	waitingPanel.setHeader("Uploading...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var restoreStatusOverLay;
var pollTimeoutId;
var interval = 5;

function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();

	if (!<s:property value="isExistVHM" />)
	{
		document.getElementById('existVHMSection').disabled = true;
		document.getElementById("newVHMOption").checked = true;
	}

	selectAdvancedOption(document.getElementById("advancedOption").checked);

	// create restore status overlay
	var div = document.getElementById('restoreStatusPanel');
	restoreStatusOverLay = new YAHOO.widget.Panel(div, {
		width:"320px",
		visible:false,
		fixedcenter:true,
		close:false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:4
		});
	restoreStatusOverLay.render(document.body);
	div.style.display = "";

	//
	var needPoolStatus = <s:property value="needPoolStatus" />;
	if (needPoolStatus)
	{
		document.getElementById("statusImgTD").style.display="block";
		document.getElementById("waitingTD").style.display="block";
		document.getElementById("cancelTD").style.display="block";

		pollRestoreStatus();

		if(waitingPanel != null){
			waitingPanel.setHeader("Polling restore operation status...");
			waitingPanel.show();
		}
	}

	// restore protocol
	var protocol = document.getElementById("restoreProtocol").value;
	if (protocol == <%=EnumConstUtil.RESTORE_PROTOCOL_LOCAL%>)
	{
		hm.util.hide('remoteSection');
		hm.util.show('localSection');
	}
	else
	{
		hm.util.show('remoteSection');
		hm.util.hide('localSection');
	}
}

function onUnloadPage() {
	clearTimeout(pollTimeoutId);
}

function submitAction(operation) {
	if (validate(operation))
	{
		document.forms[formName].operation.value = operation;

		var advancedOption = document.getElementById('advancedOption').checked;
		var restore2NewVHM = advancedOption && document.getElementById("newVHMOption").checked;
		var restore2ExistVHM = advancedOption && document.getElementById("existVHMOption").checked;
  		if (<s:property value="%{restartAfterRestore}" /> && !(restore2NewVHM || restore2ExistVHM))
    	{
  			var haTips = '';
  			var needCheckHA = <s:property value="needCheckHA" />;
  			if (needCheckHA && !document.getElementById("advancedOption").checked) {
  				haTips = '<br/>' + '<font color="red">(<s:text name="admin.hmOperation.restoreDB.haTips" />)</font>';
  			}

  			confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.restoreDB" />');
  			confirmDialog.show();
    	}
    	else
    	{
    		confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.confirm" />');
  			confirmDialog.show();
    	}
	}
}

function doContinueOper()
{
	if (thisOperation == 'confirmSlaveOffline')
	{
		standbySlave();
	}
	else
	{
		checkHANode();
	}
}

function checkHANode()
{
	if(waitingPanel != null){
		waitingPanel.setHeader("Initializing...");
		waitingPanel.show();
	}

	var needCheckHA = <s:property value="needCheckHA" />;
	if (needCheckHA && !document.getElementById("advancedOption").checked)
	{
		url = "<s:url action='restoreDB' includeParams='none' />?operation=checkMasterNode&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkMasterNodeResult,timeout: 30000}, null);
	}
	else
	{
		initProgressConfig();
	}
}


function initProgressConfig()
{
	var restoreFile = document.getElementById("restoreFile");

	url = "<s:url action='restoreDB' includeParams='none' />" + "?operation=initProgessConfig&restoreFileFileName="+encodeURIComponent(restoreFile.value)+"&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: initProgressResult,timeout: 5000}, null);
}

function initProgressResult(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	submitRestoreOperation();
}

function checkMasterNodeResult(o)
{
	eval("var result = " + o.responseText);

	if (result.online)
	{
		url = "<s:url action='restoreDB' includeParams='none' />?operation=checkSlaveNode&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkSlaveNodeResult,timeout: 30000}, null);
	}
	else
	{
		if(waitingPanel != null){
			waitingPanel.hide();
		}

		showErrorMessage(result.message);
	}
}

function checkSlaveNodeResult(o)
{
	eval("var result = " + o.responseText);

	if (result.online)
	{
		standbySlave();
	} else {
		if(waitingPanel != null){
			waitingPanel.hide();
		}

		thisOperation = 'confirmSlaveOffline';

		confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.restoreDB.slaveNode" />');
		confirmDialog.show();
	}
}

function standbySlave()
{
	url = "<s:url action='restoreDB' includeParams='none' />?operation=standbySlaveNode&ignore=" + new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: standbySlaveNodeResult,timeout: 30000}, null);

    if(waitingPanel != null){
		waitingPanel.show();
	}
}

function standbySlaveNodeResult(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	if (result.success)
	{
		initProgressConfig();
	}
	else
	{
		showErrorMessage(result.message);
	}
}

function submitRestoreOperation()
{
	try
    {
    	//var formObject = document.getElementById('restoreDB');
		//YAHOO.util.Connect.setForm(formObject);

		//var url = "<s:url action='restoreDB' includeParams='none' />";
		//var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:restoreResult}, null);

    	document.forms[formName].submit();
    	if(waitingPanel != null){
			waitingPanel.setHeader("Uploading...");
			waitingPanel.show();
		}
  	}
  	catch (e)
  	{
  	 	if (e instanceof Error && e.name == "TypeError")
  	 	{
  	 		url = "<s:url action='restoreDB' includeParams='none' />" + "?operation=endProgessConfig&ignore=" + new Date().getTime();

			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: focusRestoreFile, failure:focusRestoreFile,timeout: 5000}, null);
  		}
 	}
}

var focusRestoreFile = function(o)
{
	var file = document.getElementById("restoreFile");
	hm.util.reportFieldError(file, '<s:text name="error.fileNotExist"></s:text>');
	file.focus();

	hm.util.hide('processing');

	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function pollRestoreStatus()
{
	var url = "<s:url action='restoreDB' includeParams='none' />?operation=pollRestoreStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : restoreStatus }, null);
}
//fix bug 14131
function redirect2LoginPage() {
	setTimeout("location.assign(\"logout.action\")",5000);
}
function restoreStatus(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	eval("var data = " + o.responseText);
	var status = data.status;
	if (status == <%=RestoreStatusItem.RESTORE_FINISHED%>)
	{

		clearTimeout(pollTimeoutId);
		if (data.success) {
			//showHangMessage(data.message);
			document.getElementById("restoreStatusTD").innerHTML = "<td id='restoreStatusTD'>"+ data.message +"</td>";
			redirect2LoginPage();
		} else {
			if(null != restoreStatusOverLay){
				restoreStatusOverLay.cfg.setProperty('visible', false);
			}
			showErrorMessage(data.message);
		}

		return;
	} else if (status == <%=RestoreStatusItem.RESTORE_RUNNING%>)
	{
		document.getElementById("statusImgTD").style.display="none";
		document.getElementById("restoreStatusTD").innerHTML = "<td id='restoreStatusTD'>"+ data.message +"</td>";
		document.getElementById("waitingTD").style.display="block";
		document.getElementById("cancelTD").style.display="none";


		pollTimeoutId = setTimeout("pollRestoreStatus()", interval * 1000);  // seconds
	} else if (status == <%=RestoreStatusItem.RESTORE_WAITTING%>)
	{
		document.getElementById("restoreStatusTD").innerHTML = "<td id='restoreStatusTD'>"+ data.message +"</td>";
		document.getElementById("waitingTD").style.display="none";

		pollTimeoutId = setTimeout("pollRestoreStatus()", interval * 1000);  // seconds
	}

	if(null != restoreStatusOverLay){
		restoreStatusOverLay.cfg.setProperty('visible', true);
	}
}

function cancelRestore()
{
	var url = "<s:url action='restoreDB' includeParams='none' />?operation=cancelRestore&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : cancelRestoreStatus }, null);
}

function cancelRestoreStatus(o)
{
	eval("var data = " + o.responseText);
	if (data.success)
	{
		clearTimeout(pollTimeoutId);
		if(null != restoreStatusOverLay){
			restoreStatusOverLay.cfg.setProperty('visible', false);
		}
	}
}

function validate(operation)
{
	if (operation != 'restore')
	{
		return true;
	}

	var protocol = document.getElementById("restoreProtocol");
	if (protocol.value == <%=EnumConstUtil.RESTORE_PROTOCOL_LOCAL%>)
	{
		var restoreFile = document.getElementById("restoreFile");
		if (restoreFile.value.length ==0 )
		{
			hm.util.reportFieldError(restoreFile, '<s:text name="error.requiredField"><s:param><s:text name="admin.restoreDB.restoreFile" /></s:param></s:text>');
	        restoreFile.focus();
	        return false;
		}
	} else {
		// remote site
		var remoteServer = document.getElementById("remoteServer");
	 	if (remoteServer.value.length == 0)
		{
	        hm.util.reportFieldError(remoteServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.ip" /></s:param></s:text>');
	        remoteServer.focus();
	        return false;
	    }

		var remotePort = document.getElementById("remotePort");
		if (remotePort.value.length == 0)
		{
	        hm.util.reportFieldError(remotePort, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.port" /></s:param></s:text>');
	        remotePort.focus();
	        return false;
	     }
	     else if ( !isValidPort(remotePort) )
	     {
	     	hm.util.reportFieldError(remotePort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.backupDB.port" /></s:param><s:param><s:text name="admin.backupDB.portRange" /></s:param></s:text>');
	     	remotePort.focus();
			return false;
	     }

		var filePath = document.getElementById("remoteFilePath");
		if (filePath.value.length == 0)
		{
	        hm.util.reportFieldError(filePath, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.filePath" /></s:param></s:text>');
	        filePath.focus();
	        return false;
	    }

	 	var userName = document.getElementById("remoteUserName");
	 	if (userName.value.length == 0)
		{
	        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.user" /></s:param></s:text>');
	        userName.focus();
	        return false;
	    }

	    var password;
	    if (document.getElementById("chkToggleDisplay").checked)
		{
			password = document.getElementById("remotePassword");
		}
		else
		{
			password = document.getElementById("remotePassword_text");
		}

	    if (password.value.length == 0)
		{
		    hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.password" /></s:param></s:text>');
		    password.focus();
		    return false;
	    }
	}

	if (document.getElementById("advancedOption").checked)
	{
		if (document.getElementById("newVHMOption").checked)
		{
			var vhmName = document.getElementById("vhmName");
			if (vhmName.value.length == 0)
			{
		        hm.util.reportFieldError(vhmName, '<s:text name="error.requiredField"><s:param><s:text name="admin.restoreDB.newVHMName" /></s:param></s:text>');
		        vhmName.focus();
		        return false;
		    }
		    if (vhmName.value.indexOf(' ') > -1) {
		        hm.util.reportFieldError(vhmName, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.restoreDB.newVHMName" /></s:param></s:text>');
		        vhmName.focus();
		        return false;
			}
		}
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

function selectAdvancedOption(checked)
{
	if (checked)
	{
		hm.util.show('advancedSection');
		document.getElementById("setType").style.display="none";
	}
	else
	{
		hm.util.hide('advancedSection');
		document.getElementById("setType").style.display="";
	}
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

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
}

function initNoteSection()
{
	hm.util.hide('noteSection');
}

function selectRestoreProtocol(value)
{
	if (value == <%=EnumConstUtil.RESTORE_PROTOCOL_LOCAL%>)
	{
		hm.util.hide('remoteSection');
		hm.util.show('localSection');
	}
	else
	{
		hm.util.show('remoteSection');
		hm.util.hide('localSection');
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="restoreDB" enctype="multipart/form-data" method="POST">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons"><input type="button"
					name="restore" value="Restore" class="button"
					onClick="submitAction('restore');"
					<s:property value="writeDisabled" />></td>
			</tr>
			<tr>
				<td colspan="10">
					<div id="noteSection" style="display: none">
						<table width="700px" border="0" cellspacing="0" cellpadding="0"
							class="note">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td id="noteTD" nowrap="nowrap"></td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<script>
				function delayHideNotes() {}
			</script>
			<tr>
				<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
				<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password"></td>
			</tr>
			<tr>
				<td>
					<table class="editBox" width="800" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="100px"><label> <s:text
										name="admin.restoreDB.fileFrom" /><font color="red"><s:text
											name="*" /> </font>
							</label></td>
							<td><s:select name="restoreProtocol" id="restoreProtocol"
									cssStyle="width: 160px;margin-left:27px;"
									list="%{restoreProtocols}" listKey="key" listValue="value"
									onchange="selectRestoreProtocol(this.value)" /></td>
						</tr>
						<tr id="setType">
							<%-- <td style="padding: 10px 0 0 5px; display: <s:property value="%{hiddenDomainType}"/>" colspan="2">
						<s:radio id="gzBackup" name="restoreType"
						list="#{'gz':'Aerohive backup type (.xml file)'}" value="%{restoreType}" />
						&nbsp;&nbsp;
						<s:radio id="dumpBackup" name="restoreType"
						list="#{'dump':'Database backup type (pg_dump file)'}" value="%{restoreType}" />

					</td> --%>
						</tr>
						<tr id="localSection">
							<td style="padding: 5px 0 0 15px" colspan=2>
								<fieldset style="width: 740px">
									<legend> Local File </legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td class="labelT1" width="100px"></td>
												<td><s:text name="admin.restoreDB.note" /></td>
											</tr>
											<tr>
												<td class="labelT1" width="100px"><label> <s:text
															name="admin.restoreDB.selectFile" />
												</label></td>
												<td><s:file id="restoreFile" name="restoreFile"
														size="60" /></td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr style="display: none" id="remoteSection">
							<td style="padding: 5px 0 0 15px" colspan=2>
								<fieldset style="width: 740px">
									<legend> Remote Site </legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td class="labelT1" width="100px"><label> <s:text
															name="admin.backupDB.ip" /><font color="red"><s:text
																name="*" /> </font>
												</label></td>
												<td class="labelT1" width="150px"><s:textfield
														id="remoteServer" name="remoteServer" maxlength="32"
														onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
												<td class="labelT1" width="50px"><label> <s:text
															name="admin.backupDB.port" /><font color="red"><s:text
																name="*" /> </font>
												</label></td>
												<td class="labelT1"><s:textfield
														id="remotePort" name="remotePort" maxlength="5"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="admin.backupDB.portRange" /></td>
											</tr>
											<tr>
												<td class="labelT1"><label> <s:text
															name="admin.backupDB.filePath" /><font color="red"><s:text
																name="*" /> </font>
												</label></td>
												<td class="labelT1" colspan=3><s:textfield
														id="remoteFilePath" name="remoteFilePath" size="67" /></td>
											</tr>
											<tr>
												<td class="labelT1"><label> <s:text
															name="admin.backupDB.user" /><font color="red"><s:text
																name="*" /> </font>
												</label></td>
												<td class="labelT1"><s:textfield id="remoteUserName"
														name="remoteUserName" maxlength="32" /></td>
												<td class="labelT1" width="80"><label> <s:text
															name="admin.backupDB.password" /><font color="red"><s:text
																name="*" /> </font>
												</label></td>
												<td class="labelT1"><s:password id="remotePassword"
														name="remotePassword" maxlength="32" showPassword="true" />
													<s:textfield id="remotePassword_text" name="remotePassword"
														value="" maxlength="32" cssStyle="display:none"
														disabled="true" /></td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
												<td style="padding-left: 10px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:checkbox id="chkToggleDisplay" name="ignore"
																	value="true" disabled="%{writeDisable4Struts}"
																	onclick="hm.util.toggleObscurePassword(this.checked,['remotePassword'],['remotePassword_text']);" />
															</td>
															<td><s:text name="admin.user.obscurePassword" /></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding: 0 4px 0 4px" colspan=2>
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="sepLine"><img
											src="<s:url value="/images/spacer.gif"/>" height="1"
											class="dblk" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td height="10px"></td>
						</tr>
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td colspan="2">
								<table width="500" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="labelT1" width="300px"><s:checkbox
												name="advancedOption" id="advancedOption"
												onclick="selectAdvancedOption(this.checked);" /> <label
											style="vertical-align: middle; padding-top: 3px"> <s:text
													name="admin.restoreDB.advancedOption" />
										</label></td>
									</tr>
									<tr style="display: none" id="advancedSection">
										<td style="padding-left: 20px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr id="existVHMSection">
													<td class="labelT1" width="200px"><s:radio
															label="Gender" id="" name="restoreOption"
															list="%{restoreOptionLst1}" listKey="key"
															listValue="value" value="%{restoreOption}" /></td>
													<td class="labelT1" style="padding-right: 100px"><s:select
															id="existVHMId" name="existVHMId" value="%{existVHMId}"
															list="vhmList" listKey="id" listValue="domainName"
															cssStyle="width:150px;" /></td>
												</tr>
												<tr>
													<td class="labelT1" colspan="2"><s:radio
															label="Gender" id="" name="restoreOption"
															list="%{restoreOptionLst2}" listKey="key"
															listValue="value" value="%{restoreOption}" /></td>
												</tr>
												<tr>
													<td style="padding-left: 20px" colspan="2">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="120"><label> <s:text
																			name="admin.restoreDB.newVHMName" />
																</label></td>
																<td><s:textfield id="vhmName" name="vhmName"
																		size="24"
																		onkeypress="return hm.util.keyPressPermit(event,'name');"
																		maxlength="32" /> <s:text
																		name="admin.vhmMgr.name.range" /></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<div id="restoreStatusPanel" style="display: none">
	<div class="hd">Restore Database ...</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table>
						<tr>
							<td width="20px">
								<div id="statusImgTD">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img id="ignore"
												src="<s:url value="/images/waitingSquare.gif" />" /></td>
										</tr>
									</table>
								</div>
							</td>
							<td id="restoreStatusTD" width="400px"></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr style="padding-top: 5px">
				<td id="waitingTD" style="padding-left: 15px"><img
					src="<s:url value="/images/waiting.gif" />" /></td>
			</tr>
			<tr style="padding-top: 5px">
				<td align="center" id="cancelTD"><input type="button"
					id="cancelBtn" name="ignore" value="Cancel" class="button"
					onClick="cancelRestore();" /></td>
			</tr>
		</table>
	</div>
</div>
