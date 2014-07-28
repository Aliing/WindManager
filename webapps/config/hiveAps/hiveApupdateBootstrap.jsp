<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveApUpdateSettings"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<style type="text/css">
<!--
    #scriptPanel .bd {
        overflow:auto;
        height:35em;
        background-color:#fff;
        padding:10px;
    }
    #scriptPanel .ft {
        height:15px;
        padding:0;
    }
    #scriptPanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }
-->
</style>
<script>
var formName = 'hiveApUpdate';

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

var scriptPanel = null;
var waitingPanel = null;
function onLoadPage(){
	//create Script View overlay;
	//createScriptPanel(); //create panel until first time used, to avoid odd vertical bar
	//create waiting panel;
	createWaitingPanel();
	
	selectedDefaultPassPhrase(true);
}

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
	waitingPanel.setHeader("Loading bootstrap...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams="none" />" />');
	waitingPanel.render(document.body);
}

function createScriptPanel() {
	var div = document.getElementById('scriptPanel');
	scriptPanel = new YAHOO.widget.Panel('scriptPanel', { width:"580px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	scriptPanel.render();
	div.style.display = "";

	// Create Resize instance, binding it to the 'scriptPanel' DIV
	var resize = new YAHOO.util.Resize('scriptPanel', {
	    handles: ['br'],
	    autoRatio: false,
	    minWidth: 300,
	    minHeight: 200,
	    status: true
	});

	// Setup resize handler to update the size of the Panel's body element
	// whenever the size of the 'resizablepanel' DIV changes
	resize.on('resize', resizeScirptPanel, scriptPanel, true);
}

function resizeScirptPanel(obj){
	// QUIRKS FLAG, FOR BOX MODEL
	var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

	// UNDERLAY/IFRAME SYNC REQUIRED
	var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

    var panelHeight = obj.height;
    var headerHeight = this.header.offsetHeight; // Content + Padding + Border
    var footerHeight = this.footer.offsetHeight; // Content + Padding + Border

    var bodyHeight = (panelHeight - headerHeight - footerHeight);
    var bodyContentHeight = (IE_QUIRKS) ? bodyHeight : bodyHeight - 20;

    YAHOO.util.Dom.setStyle(this.body, 'height', bodyContentHeight + 'px');

    if (IE_SYNC) {

        // Keep the underlay and iframe size in sync.

        // You could also set the width property, to achieve the
        // same results, if you wanted to keep the panel's internal
        // width property in sync with the DOM width.

        this.sizeUnderlay();

        // Syncing the iframe can be expensive. Disable iframe if you
        // don't need it.

        this.syncIframe();
    }
}

function validate(operation) {
	if(!validateBootstrapAdmin(operation)){
		return false;
	}
	if(!validateCapwapSettings(operation)){
		return false;
	}
	if(!validateApSelection(operation)){
		return false;
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'updateBootstrap'){
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
	}
	return true;
}

function validateBootstrapAdmin(operation){
	if(operation == 'updateBootstrap'){

		var usernameElement = document.getElementById("bootstrapAdmin");
		var passwordElement;
		var confirmElement;
		if(document.getElementById("chkToggleDisplay").checked){
			passwordElement = document.getElementById("bootstrapPassword");
			confirmElement = document.getElementById("confirmPassword");
		}else{
			passwordElement = document.getElementById("bootstrapPassword_text");
			confirmElement = document.getElementById("confirmPassword_text");
		}

		var message = hm.util.validateUsername(usernameElement.value, '<s:text name="hiveAp.update.bootstrap.adminName" />');
		if(message != null){
			hm.util.reportFieldError(usernameElement, message);
		    usernameElement.focus();
		    return false;
		}

		if(usernameElement.value.length < 3){
		    hm.util.reportFieldError(usernameElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.update.bootstrap.adminName" /></s:param><s:param><s:text name="hiveAp.update.bootstrap.adminName.range" /></s:param></s:text>');
		    usernameElement.focus();
		    return false;
		}

		if (!hm.util.validateUserNewPasswordFormat(passwordElement, confirmElement, '<s:text name="hiveAp.update.bootstrap.password" />',
    			'<s:text name="hiveAp.update.bootstrap.confirmPassword" />', 8, '<s:text name="hiveAp.update.bootstrap.password.range" />', usernameElement.value)) {
			return false;
		}
		if (usernameElement.value == 'root'
			 || usernameElement.value == 'daemon'
			 || usernameElement.value == 'bin'
			 || usernameElement.value == 'sys'
			 || usernameElement.value == 'sync'
			 || usernameElement.value == 'mail'
			 || usernameElement.value == 'proxy'
			 || usernameElement.value == 'sshd'
			 || usernameElement.value == '_radius'){
		   hm.util.reportFieldError(usernameElement, "<s:text name='error.value.internal.used'><s:param><s:text name='hiveAp.update.bootstrap.adminName' /></s:param></s:text>");
		   usernameElement.focus();
		   return false;
		}
	}
	return true;
}

function validateCapwapSettings(operation) {
	if(operation == 'updateBootstrap'){
		var serverName = document.getElementById("capwapServer");
		var backupServerName = document.getElementById("capwapServerBackup");
		var vhmName = document.getElementById("vhmName");
		var port = document.getElementById("udpPort");
		var timeOut = document.getElementById("timeOut");
		var deadInterval = document.getElementById("deadInterval");
		var newPhrase;
		var confirmPhrase;

		var message = hm.util.validateName(serverName.value, '<s:text name="admin.capwap.primaryCapwapIP" />');
    	if (message != null) {
    		hm.util.reportFieldError(serverName, message);
	        serverName.focus();
	        return false;
	    }
	    if(backupServerName.value > 0){
	    	var message = hm.util.validateName(backupServerName.value, '<s:text name="admin.capwap.backupCapwapIP" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(backupServerName, message);
	    		backupServerName.focus();
		        return false;
		    }
		}
	   
    	var message = hm.util.validateName(vhmName.value, '<s:text name="admin.vhmMgr.vhmName" />');
    	if (message != null) {
    		hm.util.reportFieldError(vhmName, message);
    		vhmName.focus();
	        return false;
	    }
		if (port.value.length == 0)
		{
	        hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.udpPort" /></s:param></s:text>');
	        port.focus();
	        return false;
	    }
		var message = hm.util.validateIntegerRange(port.value, '<s:text name="admin.capwap.udpPort" />',
		                                           <s:property value="1024" />,
		                                           <s:property value="65535" />);
		if (message != null) {
			hm.util.reportFieldError(port, message);
			port.focus();
			return false;
		}

		if (timeOut.value.length == 0)
		{
	        hm.util.reportFieldError(timeOut, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.timeOut" /></s:param></s:text>');
	        timeOut.focus();
	        return false;
	    }
	    var message = hm.util.validateIntegerRange(timeOut.value, '<s:text name="admin.capwap.timeOut" />',
		                                           <s:property value="30" />,
		                                           <s:property value="120" />);
		if (message != null) {
			hm.util.reportFieldError(timeOut, message);
			timeOut.focus();
			return false;
		}

		if (deadInterval.value.length == 0)
		{
	        hm.util.reportFieldError(deadInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.deadInterval" /></s:param></s:text>');
	        deadInterval.focus();
	        return false;
	    }
	    var message2 = hm.util.validateIntegerRange(deadInterval.value, '<s:text name="admin.capwap.deadInterval" />',
		                                           2*timeOut.value,
		                                           240);
		if (message2 != null) {
			hm.util.reportFieldError(deadInterval, message2);
			deadInterval.focus();
			return false;
		}
		var enableDTLS = document.getElementById("cbDTLS");
	    var enablePassPhrase = document.getElementById("cbPassPhrase");
	    if (enableDTLS.checked && !enablePassPhrase.checked)
	    {
	    	if(document.getElementById("chkToggleDisplay_1").checked){
				newPhrase = document.getElementById("newPassPhrase");
				confirmPhrase = document.getElementById("confirmPassPhrase");
	    	}else{
				newPhrase = document.getElementById("newPassPhrase_text");
				confirmPhrase = document.getElementById("confirmPassPhrase_text");
	    	}
	    	var message = hm.util.validatePassword(newPhrase.value, '<s:text name="admin.capwap.newPassPhrase" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(newPhrase, message);
		        newPhrase.focus();
		        return false;
		    }

			message = hm.util.validatePassword(confirmPhrase.value, '<s:text name="admin.capwap.confirmPassPhrase" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(confirmPhrase, message);
		        confirmPhrase.focus();
		        return false;
		    }

			if(newPhrase.value.length < 16){
			    hm.util.reportFieldError(newPhrase, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.newPassPhrase"/></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    newPhrase.focus();
			    return false;
			}
			if(confirmPhrase.value.length < 16){
			    hm.util.reportFieldError(confirmPhrase, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    confirmPhrase.focus();
			    return false;
			}
		   	if (confirmPhrase.value.valueOf() != newPhrase.value.valueOf() )
		  	{
		  		hm.util.reportFieldError(confirmPhrase, '<s:text name="error.notEqual"><s:param><s:text name="admin.capwap.confirmPassPhrase" /></s:param><s:param><s:text name="admin.capwap.newPassPhrase" /></s:param></s:text>');
		        confirmPhrase.focus();
		        return false;
		  	}
	    }
	}
	return true;
}

function selectedDTLS(checked){
	if (checked) {
		if (document.getElementById("cbPassPhrase").checked) {
			selectedDefaultPassPhrase(true);
		} else {
			selectedDefaultPassPhrase(false);
		}
		document.getElementById("cbPassPhrase").disabled = false;
	} else {
		selectedDefaultPassPhrase(true);
		document.getElementById("cbPassPhrase").disabled = true;
	}
}

function selectedDefaultPassPhrase(checked)
{
	var toggleCbxElement = document.getElementById("chkToggleDisplay_1");
	if (checked)
	{
		if(toggleCbxElement.checked){
			document.getElementById("newPassPhrase").disabled = true;
			document.getElementById("confirmPassPhrase").disabled = true;
		}else{
			document.getElementById("newPassPhrase_text").disabled = true;
			document.getElementById("confirmPassPhrase_text").disabled = true;
		}
		toggleCbxElement.disabled = true;
	}else{
		if(toggleCbxElement.checked){
			document.getElementById("newPassPhrase").disabled = false;
			document.getElementById("confirmPassPhrase").disabled = false;
		}else{
			document.getElementById("newPassPhrase_text").disabled = false;
			document.getElementById("confirmPassPhrase_text").disabled = false;
		}
		toggleCbxElement.disabled = false;
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var contentDiv = document.getElementById("content_viewer");
	contentDiv.innerHTML = details.v.replace(/\n/g,"<br>");
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(scriptPanel == null){
		createScriptPanel();
	}
	if(details.t){
		scriptPanel.header.innerHTML = "Bootstrap Details - " + details.t;
	}
	if (scriptPanel != null) {
		scriptPanel.cfg.setProperty('visible', true);
	}
	//if user click delta script and old script file not exist, alert;
	if(details.exist){
		warnDialog.cfg.setProperty('text', "<s:text name='error.config.absentOldConfig'/>");
		warnDialog.show();
	}
}

var detailsFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(scriptPanel == null){
		createScriptPanel();
	}
	if (scriptPanel != null) {
		var contentDiv = document.getElementById("content_viewer");
		contentDiv.innerHTML = "<s:text name='error.request.timeout'/>";
		scriptPanel.cfg.setProperty('visible', true);
	}
};
var callback = {
	success : detailsSuccess,
	failure : detailsFailed,
	timeout: 240000
};

function bootstrapDetails(id) {
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=viewBootstrap&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.bootstrap"/> </td>');
}
</script>

<div id="content"><s:form id="hiveApUpdate" action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('updateBootstrap');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
						<s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="600px">
				<tr>
					<td style="padding: 4px 10px 10px 10px;" valign="top">
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td>
									<div id="bootstrapDiv">
										<fieldset><legend><s:text name="hiveAp.update.bootstrap.admin.tag"/></legend>
										<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="170px" nowrap="nowrap"><s:text name="hiveAp.update.bootstrap.adminName"/><font color="red"><s:text name="*"/></font></td>
											<td><s:textfield name="bootstrapAdmin" id="bootstrapAdmin" maxlength="20" size="32"
												onkeypress="return hm.util.keyPressPermit(event,'username');"/> <s:text name="hiveAp.update.bootstrap.adminName.range"/></td>
										</tr>
										<tr>
											<td class="labelT1" nowrap="nowrap"><s:text name="hiveAp.update.bootstrap.password"/><font color="red"><s:text name="*"/></font></td>
											<td><s:password name="bootstrapPassword" id="bootstrapPassword" showPassword="true" maxlength="32" size="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"/>
												<s:textfield name="bootstrapPassword" id="bootstrapPassword_text" cssStyle="display: none;" disabled="true" maxlength="32" size="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"/>
												<s:text name="hiveAp.update.bootstrap.password.range"/></td>
										</tr>
										<tr>
											<td class="labelT1" nowrap="nowrap"><s:text name="hiveAp.update.bootstrap.confirmPassword"/><font color="red"><s:text name="*"/></font></td>
											<td><s:password name="confirmPassword" id="confirmPassword" showPassword="true" maxlength="32" size="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"/>
												<s:textfield name="confirmPassword" id="confirmPassword_text" cssStyle="display: none;" disabled="true" maxlength="32" size="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"/>
											</td>
										</tr>
										<tr>
										<td></td>
										<td>
											<s:checkbox id="chkToggleDisplay" name="ignore" value="true"
											 	onclick="hm.util.toggleObscurePassword(this.checked,['bootstrapPassword','confirmPassword'],['bootstrapPassword_text','confirmPassword_text']);" />
											<s:text name="admin.user.obscurePassword" />
										</td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										</table>
										</fieldset>
										<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										</table>
										<fieldset><legend><s:text name="hiveAp.update.bootstrap.dtls.tag"/></legend>
										<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10px"></td>
										</tr>
										<tr>
											<td class="labelT1" width="170px"><s:text name="admin.capwap.primaryCapwapIP" /><font color="red"><s:text name="*"/></font></td>
											<td>
												<s:textfield id="capwapServer" name="capwapServer" maxlength="32" size="32"
													onkeypress="return hm.util.keyPressPermit(event,'name');" />
												<s:text name="admin.capwap.serverNameRange" />
											</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text name="admin.capwap.backupCapwapIP" /></td>
											<td>
												<s:textfield id="capwapServerBackup" name="capwapServerBackup" maxlength="32" size="32"
													onkeypress="return hm.util.keyPressPermit(event,'name');" />
												<s:text name="admin.capwap.serverNameRange" />
											</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text name="admin.vhmMgr.vhmName" /><font color="red"><s:text name="*"/></font></td>
											<td>
												<s:textfield id="vhmName" name="vhmName" maxlength="64" size="32"
													onkeypress="return hm.util.keyPressPermit(event,'name');" />
												<s:text name="hiveAp.update.bootstrap.vhm.range" />
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="170px"><s:text name="admin.capwap.udpPort" /><font color="red"><s:text name="*"/></font></td>
											<td>
												<s:textfield id="udpPort" name="udpPort" maxlength="5" size="12"
													onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="admin.capwap.udpPortRange" />
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="170px"><s:text name="admin.capwap.timeOut" /><font color="red"><s:text name="*"/></font></td>
											<td>
												<s:textfield id="timeOut" name="timeOut"  maxlength="5" size="12"
													onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="admin.capwap.timeOutRange" />
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="170px"><s:text name="admin.capwap.deadInterval" /><font color="red"><s:text name="*"/></font></td>
											<td>
												<s:textfield id="deadInterval" name="deadInterval"  maxlength="5" size="12"
													onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="admin.capwap.deadIntervalRange" />
											</td>
										</tr>
										<tr>
											<td colspan="2" style="padding-left: 5px;"><s:checkbox name="enabledDTLS" id="cbDTLS" onclick="selectedDTLS(this.checked)"/>
												<s:text name="admin.capwap.enableDTLS" />
											</td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td colspan="2" >
												<div id="passPhraseSection">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<fieldset style="width: 500px">
																	<legend>
																		<s:text name="admin.capwap.passPhrase" />
																	</legend>
																	<div>
																		<table cellspacing="0" cellpadding="0" border="0"
																			width="100%">
																			<tr>
																				<td class="labelT1" colspan="2">
																					<s:checkbox name="defaultPassPhrase"
																						id="cbPassPhrase"
																						onclick="selectedDefaultPassPhrase(this.checked)"/>
																					<label>
																						<s:text name="admin.capwap.restoreDefaultPass" />
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" width="170px" >
																					<label>
																						<s:text name="admin.capwap.newPassPhrase" /><font color="red"><s:text name="*" /> </font>
																					</label>
																				</td>
																				<td width="350">
																				<s:password id="newPassPhrase" name="newPassPhrase"
																					disabled="%{dtlsStyle}" size="32" maxlength="32" 
																					showPassword="true"
																					onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																				<s:textfield id="newPassPhrase_text" name="newPassPhrase"
																					disabled="true" size="32" maxlength="32" 
																					cssStyle="display: none;" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																				<s:text name="admin.capwap.phraseRange" />
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" width="170px" >
																					<label>
																						<s:text name="admin.capwap.confirmPassPhrase" /><font color="red"><s:text name="*" /> </font>
																					</label>
																				</td>
																				<td>
																					<s:password id="confirmPassPhrase" name="confirmPassPhrase" value="%{newPassPhrase}"
																						disabled="%{dtlsStyle}" size="32" maxlength="32" showPassword="true" 
																						onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																					<s:textfield id="confirmPassPhrase_text" name="confirmPassPhrase" value="%{newPassPhrase}"
																						disabled="true" size="32" maxlength="32" cssStyle="display: none;" 
																						onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																					<s:text name="admin.capwap.phraseRange" />
																				</td>
																			</tr>
																			<tr>
																				<td>
																					&nbsp;
																				</td>
																				<td>
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																							<td>
																							<s:checkbox id="chkToggleDisplay_1" 
																								name="ignore" value="true"
																								disabled="%{dtlsStyle}"
											 													onclick="hm.util.toggleObscurePassword(this.checked,['newPassPhrase','confirmPassPhrase'],['newPassPhrase_text','confirmPassPhrase_text']);" />
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
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										</table>
										</fieldset>
										<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										</table>
									</div>
								</td>
							</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%" id="listTable">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="softVer" key="monitor.hiveAp.sw" /></th>
										<th align="left"><ah:sort name="location" key="hiveAp.location" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck"><ah:checkItem /></td>
											<td class="list"><a href="javascript: bootstrapDetails( '<s:property value="%{id}" />')"><s:property
													value="hostName" /></a>
											</td>
											<td class="list"><s:property value="macAddress" />&nbsp;</td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
											<td class="list"><s:property value="location" />&nbsp;</td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="scriptPanel" style="display: none;">
<div class="hd">Bootstrap Details</div>
<div class="bd" id="content_viewer">
</div>
<div class="ft"></div>
</div>
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>