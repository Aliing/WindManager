<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.RadiusServer"%>

<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'radiusAssignment';
var thisOperation;
var ifUsedByAuth;

function onLoadPage() {
	ifUsedByAuth = <s:property value="%{ifUsedByAuth}"/>;
	if (!document.getElementById(formName + "_dataSource_radiusName").disabled) {
		document.getElementById(formName + "_dataSource_radiusName").focus();
	}
	<s:if test="%{jsonMode}">
		<s:if test="%{!parentIframeOpenFlg && dataSource.id != null && null != operation && ('create' == operation || 'update' == operation) && actionErrors.size == 0}">
			top.closeIFrameDialog();
			<s:if test="%{'create' == operation}">
				<s:if test="%{null != parentDomID && '' != parentDomID}">
					var parentVlanSelect = top.document.getElementById('<s:property value="%{parentDomID}"/>');
					if(undefined != parentVlanSelect) {
						hm.util.insertSelectValue('<s:property value="%{dataSource.id}"/>', '<s:property value="%{dataSource.radiusName}"/>', 
							parentVlanSelect, false, true);
					}
				</s:if>
				<s:else>
					top.fetchConfigTemplate2Page(true);
				</s:else>
			</s:if>
			<s:else>
				<s:if test="%{null == parentDomID || '' == parentDomID}">
					top.showRadiusServerSelectDialog('<s:property value="%{ssidForRadius}"/>', '<s:property value="%{dataSource.id}"/>', 
					'<s:property value="%{radiusTypeFlag}"/>');
				</s:if>
			</s:else>
		</s:if>
		<s:else>
			top.changeIFrameDialog(950, 500);
		</s:else>
	</s:if>
	
	// init the checkbox
	if(Get("enableDHCP4RSRow")) {
		var chkDHCPEl = Get(formName + "_dataSource_enableDHCP4RadiusServer");
		if(chkDHCPEl) {
			chkDHCPEl.onclick = function() {
				if(this.checked) {
					hm.util.hide("specificRSRow");
				} else {
					hm.util.show("specificRSRow");
				}
			}
			<s:if test="%{dataSource.enableDHCP4RadiusServer}">
			hm.util.hide("specificRSRow");
			</s:if>
		}
	}
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="radiusAssignment" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

function submitAction(operation) {
	thisOperation = operation;
	
	if (validate(operation)) {
		<s:if test="%{!jsonMode || parentIframeOpenFlg}">
			if (operation == 'update<s:property value="lstForward"/>' && 'hmServices' != '<s:property value="lstForward"/>' && ifUsedByAuth) {
				confirmDialog.cfg.setProperty('text', '<s:text name="warn.radius.service.usedForAuth" />');
				confirmDialog.show();		
			} else {
				doContinueOper();
			}
		</s:if>
		<s:else>
			if ('cancel' == operation) {
				top.closeIFrameDialog();
				<s:if test="%{dataSource.id != null && (null == parentDomID || '' == parentDomID)}">
					top.showRadiusServerSelectDialog('<s:property value="%{ssidForRadius}"/>', '<s:property value="%{dataSource.id}"/>', 
					'<s:property value="%{radiusTypeFlag}"/>');
				</s:if>
			} else {
				if ('create' == operation) {
					<s:if test="%{dataSource.id != null}">
						operation = 'update';
					</s:if>
				}
				document.forms[formName].operation.value = operation;
	   			document.forms[formName].submit();
			}
		</s:else>
	}
}

function doContinueOper() {
	if (thisOperation == 'create<s:property value="lstForward"/>' || thisOperation == 'update<s:property value="lstForward"/>') {
		showProcessing();
	}
    document.forms[formName].operation.value = thisOperation;
    Get(formName + "_dataSource_optionalStyle").value = Get("optionSet").style.display;
    if (Get('createSection').style.display == '') {
    	Get(formName + "_dataSource_advanceStyle").value = Get("advanceSet").style.display;
    }
    document.forms[formName].submit();
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_RADIUS_SERVER_ASSIGN%>' == operation || operation == 'cancel<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_retryInterval").value = 0;
		document.getElementById(formName + "_dataSource_updateInterval").value = 0;
		initNumberValue(true);
		return true;
	}
	
	if(operation == 'newIpAddress' || operation == 'editIpAddress') {	
		if (!checkIntervalValue()) {
        	return false;
    	}
		if(operation == "editIpAddress"){
			var value = hm.util.validateListSelection("myIpSelect");
			if(value < 0){
				return false
			}else{
				document.forms[formName].ipAddress.value = value;
			}
		}
		initNumberValue(true);
	}
	var table = document.getElementById("checkAll");
	if (operation == 'addRadiusServer') {
		var ipnames = document.getElementById("myIpSelect");
		var ipValue = document.forms[formName].inputIpValue;
		var showError = document.getElementById("errorDisplay");
		if ("" == ipValue.value) {
	        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusAssign.serverName" /></s:param></s:text>');
	        ipValue.focus();
			return false;
		}
		
		if(ipValue.value == "0.0.0.0" || ipValue.value == "255.255.255.255"){
			var message = '<s:text name="config.radiusAssign.serverName" />'
				hm.util.reportFieldError(showError, '<s:text name="config.radiusAssign.ipAddress.check"><s:param><s:text name="config.radiusAssign.serverName" /></s:param></s:text>');
	        	ipValue.focus();
	        	return false;
		}else{
			if(hm.util.validateMulticastAddress(ipValue.value)){
				var message = '<s:text name="config.radiusAssign.serverName" />'
					hm.util.reportFieldError(showError, '<s:text name="config.radiusAssign.ipAddress.check"><s:param><s:text name="config.radiusAssign.serverName" /></s:param></s:text>');
		        	ipValue.focus();
		        	return false;
			}
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				var message = hm.util.validateName(ipValue.value, '<s:text name="config.radiusAssign.serverName" />');
		    	if (message != null) {
		    		hm.util.reportFieldError(showError, message);
		        	ipValue.focus();
		        	return false;
		    	}
			}
			document.forms[formName].ipAddress.value = -1;
		} else {
			document.forms[formName].ipAddress.value = ipnames.options[ipnames.selectedIndex].value;
		}
		
		var password;
		var confirm;
		if (document.getElementById("chkPasswordDisplay").checked) {
			password = document.getElementById("sharedSecret");
			confirm = document.getElementById("confirm");
		} else {
			password = document.getElementById("sharedSecret_text");
			confirm = document.getElementById("confirm_text");
		}
		if (password.value.length > 0 || confirm.value.length > 0) {
			if(!checkPassword(password, confirm, '<s:text name="config.radiusAssign.secret" />', '<s:text name="config.radiusAssign.confirmSecret" />')) {
        		return false;
    		}
		}
		if ("" == document.getElementById("authTr").style.display) {
			var authPort = document.forms[formName].authPort;
	    	if(!checkIfInput(authPort, '<s:text name="config.radiusAssign.authPort" />', "advanceSet", authPort) 
	    		|| !checkInputRange(authPort, '<s:text name="config.radiusAssign.authPort" />', 1,65535, "advanceSet", authPort)) {
	    		return false;
	    	}
		}
    	
      	if("" == document.getElementById("acctTr").style.display) {
      		var accPort = document.forms[formName].acctPort;
      		if(!checkIfInput(accPort, '<s:text name="config.radiusAssign.accoutPort" />', "advanceSet", accPort) 
      			|| !checkInputRange(accPort, '<s:text name="config.radiusAssign.accoutPort" />', 1,65535, "advanceSet", accPort)) {
    			return false;
    		}
      	}
      	initNumberValue(false);
	}
	if (operation == 'removeRadiusServer' || operation == 'removeRadiusServerNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusAssign.serverTab" /></s:param></s:text>');
			return false;
		}
		initNumberValue(true);
	} 
	
	var name = document.getElementById(formName + "_dataSource_radiusName");
	if (operation == 'create<s:property value="lstForward"/>' || operation == 'create') {
		var message = hm.util.validateName(name.value, '<s:text name="config.radiusAssign.radiusName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    }
    if(operation == 'create<s:property value="lstForward"/>' 
    || operation == 'update<s:property value="lstForward"/>'
    || operation == 'create'
    || operation == 'update') {   	
    	if (!checkIntervalValue()) {
        	return false;
    	}
		
		if(Get("enableDHCP4RSRow")) {
		      var chkDHCPEl = Get(formName + "_dataSource_enableDHCP4RadiusServer");
		      if(chkDHCPEl) {
		      	if(!chkDHCPEl.checked) {
					var cbs = document.getElementsByName('ruleIndices');
					if (cbs && cbs.length == 0) {
					    hm.util.reportFieldError(table, '<s:text name="error.config.radiusAssign.noDHCP.emptyRADIUSServer" />');
					    return false;
					}
					if(!checkRADIUSServerRows()) {
					    return false;
					}
		      	}
		      }
		} else {
			if(!checkRADIUSServerRows()) {
				return false;
			}
		}
    }
    
	return true;
}

function checkRADIUSServerRows() {
	var table = document.getElementById("checkAll");
    var authPorts = document.getElementsByName('authPorts');
    var accoutPorts = document.getElementsByName('accoutPorts');
    var secrets = document.getElementsByName('sharedSecrets');
    
    for(var i = 0; i < authPorts.length; i ++) {
        if (!checkIfInput(authPorts[i], '<s:text name="config.radiusAssign.authPort" />', null, table) 
            || !checkInputRange(authPorts[i], '<s:text name="config.radiusAssign.authPort" />', 1,65535, null, table)) {
            return false;
        }
    }
    for(var i = 0; i < accoutPorts.length; i ++) {
        if (!checkIfInput(accoutPorts[i], '<s:text name="config.radiusAssign.accoutPort" />', null, table) 
            || !checkInputRange(accoutPorts[i], '<s:text name="config.radiusAssign.accoutPort" />', 1,65535, null, table)) {
            return false;
        }
    }
    for(var i = 0; i < secrets.length-1; i += 2) {
        var activeObj;
        if (secrets[i].style.display == "") {
            activeObj = secrets[i];
        } else {
            activeObj = secrets[i+1];
        }
        if (activeObj.value.length > 0) {
            var message = hm.util.validatePassword(activeObj.value, '<s:text name="config.radiusAssign.secret" />');
            if (message != null) {
                hm.util.reportFieldError(table, message);
                activeObj.focus();
                return false;
            }
        }
    }
    document.forms[formName].authPort.value=1812;
    document.forms[formName].acctPort.value=1813;
    
    return true;
}

function initNumberValue(changePort) {
	/* DON'T know why need this block
	var authPorts = document.getElementsByName('authPorts');
	var accoutPorts = document.getElementsByName('accoutPorts');
	for(var i = 0; i < authPorts.length; i ++) {
   		authPorts[i].value=1812;
	}
	for(var i = 0; i < accoutPorts.length; i ++) {
   		accoutPorts[i].value=1813;
	}
	*/
	if (changePort) {
		document.forms[formName].authPort.value=1812;
		document.forms[formName].acctPort.value=1813;
	}
}

function checkIntervalValue() 
{
	var retry = document.getElementById(formName + "_dataSource_retryInterval");
	if(!checkIfInput(retry, '<s:text name="config.radiusAssign.retryInterval" />', "optionSet", retry) 
		|| !checkInputRange(retry, '<s:text name="config.radiusAssign.retryInterval" />', 60,100000000, "optionSet", retry)) {
		return false;
	}
	var update = document.getElementById(formName + "_dataSource_updateInterval");
	if(!checkIfInput(update, '<s:text name="config.radiusAssign.updateInterval" />', "optionSet", update) 
		|| !checkInputRange(update, '<s:text name="config.radiusAssign.updateInterval" />', 10,100000000, "optionSet", update)) {
		return false;
	}
	return true;
}

function checkIfInput(inputElement, title, displayEle, errObj)
{
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(errObj, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        if (null != displayEle) {
        	showHideContent(displayEle,"");
        }
        inputElement.focus();
        return false;
    }
    return true;
}

function checkInputRange(inputElement, title, min, max, displayEle, errObj)
{
	var message = hm.util.validateIntegerRange(inputElement.value, title,min,max);
    if (message != null) {
        hm.util.reportFieldError(errObj, message);
        if (null != displayEle) {
        	showHideContent(displayEle,"");
        }
        inputElement.focus();
        return false;
    }
    return true; 
}

function checkPassword(password, confirm, passtitle, confirmtitle)
{
	var message = hm.util.validatePassword(password.value, passtitle);
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
   		showHideContent("advancSet","");
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, confirmtitle);
   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
   		showHideContent("advancSet","");
       	confirm.focus();
       	return false;
   	}
	if (password.value != confirm.value) {
        hm.util.reportFieldError(confirm, '<s:text name="error.notEqual"><s:param>'+confirmtitle+'</s:param><s:param>'+passtitle+'</s:param></s:text>');
        showHideContent("advancSet","");
        confirm.focus();
        return false;
    }
    return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	// var trh = document.getElementById('headerSection');
	// var trc = document.getElementById('createSection');
	// var table = trh.parentNode;	
	// table.removeChild(trh);
	// table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}
function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('ruleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function serverTypeChange(type) {
	var showAuth = false;
	var showAcct = false;
	switch (parseInt(type)) {
		case <%=RadiusServer.RADIUS_SERVER_TYPE_BOTH%>:
			showAuth = true;
			showAcct = true;
			break;
		case <%=RadiusServer.RADIUS_SERVER_TYPE_AUTH%>:
			showAuth = true;
			showAcct = false;
			break;
		case <%=RadiusServer.RADIUS_SERVER_TYPE_ACCT%>:
			showAuth = false;
			showAcct = true;
			break;
		default:
        	break;
	}
	document.getElementById("authTr").style.display=showAuth?"":"none";
	document.getElementById("acctTr").style.display=showAcct?"":"none";
}
</script>
<div id="content"><s:form action="radiusAssignment">
	<s:hidden name="dataSource.optionalStyle"></s:hidden>
	<s:hidden name="dataSource.advanceStyle"></s:hidden>
	<s:hidden name="ipAddress" />
	<s:if test="%{jsonMode}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="parentIframeOpenFlg" />
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{!jsonMode}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIUS_SERVER_ASSIGN%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<s:else>
		<tr>
			<td>
			<div class="topFixedTitle">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.radiusAssign.config2.dialog.title.new"/>
								</s:if>
								<s:else>
									<s:text name="config.radiusAssign.config2.dialog.title.edit"/>
								</s:else>
							</td>
							<td style="padding-left: 10px">
								<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk" />
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<s:if test="%{parentIframeOpenFlg}">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton">
									<s:if test="%{writeDisabled == 'disabled'}">
										&nbsp;
									</s:if>
									<s:else>
										<s:if test="%{dataSource.id == null}">
											<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.update"/>">
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>">
										</s:else>
										<span><s:text name="button.update"/></span></a>
									</s:else>
								</td>
							</tr>
						</s:if>
						<s:else>
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel');" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton">
									<s:if test="%{writeDisabled == 'disabled'}">
										&nbsp;
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a>
									</s:else>
								</td>
							</tr>
						</s:else>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		</s:else>
		<tr>
			<td style="padding-top: 5px;">
				<s:if test="%{!jsonMode}">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0">
				</s:if>
				<s:else>
					<table cellspacing="0" cellpadding="0" border="0" class="topFixedTitle">
				</s:else>
					<tr>
						<td><tiles:insertDefinition name="notes" /></td>
					</tr>
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="4">
										<%-- add this password dummy to fix issue with auto complete function --%>
										<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
									</td>
								</tr>
								<tr>
									<td class="labelT1" width="100"><label><s:text
										name="config.radiusAssign.radiusName" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield size="24" name="dataSource.radiusName"
										maxlength="%{radiusNameLength}" disabled="%{disabledName}" 
										onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
										name="config.ssid.ssidName_range" /></td>
								</tr>
								<tr>
									<td class="labelT1"><label><s:text
										name="config.radiusAssign.description" /></label></td>
									<td><s:textfield size="48" name="dataSource.description"
										maxlength="%{commentLength}" />&nbsp;<s:text
										name="config.ssid.description_range" /></td>
								</tr>
								<tr>
									<td height="2"></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding:4px 10px 4px 5px;">
							<fieldset>
								<legend><s:text name="config.radiusAssign.serverTab" />s</legend>
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td style="padding:4px 0 4px 0px;" class="noteInfo"><s:text name="config.radiusProxy.notice.radius.server" /></td>
								</tr>
								<tr>
									<td style="padding:4px 0 4px 30px;" class="noteInfo"><s:text name="config.radiusAssign.shared.secrets.note" /></td>
								</tr>
								<tr>
									<td style="padding:4px 0 4px 30px;" class="noteInfo"><s:text name="config.radiusAssign.router.note" /></td>
								</tr>
								<s:if test="%{!easyMode}">
								<tr id="enableDHCP4RSRow">
								    <td style="padding: 4px 0 4px 0;">
								        <s:checkbox name="dataSource.enableDHCP4RadiusServer" />
								        <label for="radiusAssignment_dataSource_enableDHCP4RadiusServer"><s:text name="config.radiusAssign.enableDHCP4RADIUSserver.desc"/></label>
								    </td>
								</tr>
								</s:if>
								<tr id="specificRSRow">
									<td style="padding:4px 0 4px 0px;">
											<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="radiusTable">
												<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
													<td colspan="10" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																class="button" onClick="showCreateSection();"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeRadiusServer');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
													<td colspan="6" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																class="button" onClick="submitAction('addRadiusServer');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeRadiusServerNone');"></td>
															<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																class="button" onClick="hideCreateSection();"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
													<td colspan="6" style="padding-left: 5px;">
													<fieldset style="background-color: #edf5ff">
													<legend><s:text name="config.radiusAssign.serverTab.add.new" /></legend>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="160"><label><s:text
																name="config.radiusAssign.serverName" /><font color="red"><s:text name="*"/></font></label></td>
															<td>
																<ah:createOrSelect divId="errorDisplay"
																list="availableIpAddress" typeString="IpAddress" selectIdName="myIpSelect"
																inputValueName="inputIpValue" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label><s:text name="config.radiusAssign.serverType" /><font color="red"><s:text name="*"/></font></label></td>
															<td colspan="2"><s:select name="serverType" cssStyle="width: 110px;" value="%{serverType}" 
																list="%{enumSerType}" listKey="key" listValue="value" onchange="serverTypeChange(this.options[this.selectedIndex].value);" /></td>
														</tr>
														<tr>
															<td class="labelT1"><label><s:text
																name="config.radiusAssign.secret" /></label></td>
															<td colspan="2"><s:password id="sharedSecret" name="sharedSecret" onkeypress="return hm.util.keyPressPermit(event,'password');" 
												               	maxlength="%{commentLength}" showPassword="true" value="%{sharedSecret}" size="48" /><s:textfield id="sharedSecret_text" name="sharedSecret" maxlength="%{commentLength}" cssStyle="display:none"
																onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" size="48" />
																<s:text name="config.radiusOnHiveAp.desRange" /></td>
														</tr>
														<tr>
															<td class="labelT1"><label><s:text
																name="config.radiusAssign.confirmSecret" /></label></td>
															<td colspan="2"><s:password id="confirm" onkeypress="return hm.util.keyPressPermit(event,'password');" 
												                maxlength="%{commentLength}" showPassword="true" value="%{sharedSecret}" size="48" /><s:textfield id="confirm_text" maxlength="%{commentLength}" cssStyle="display:none"
																onkeypress="return hm.util.keyPressPermit(event,'password');" size="48" />
																<s:text name="config.radiusOnHiveAp.desRange" /></td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td colspan="2"><s:checkbox id="chkPasswordDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
												                (this.checked,['sharedSecret','confirm'],['sharedSecret_text','confirm_text']);" />
												                <s:text name="config.radiusAssign.secret.obscure" /></td>
														</tr>
														<tr>
															<td class="labelT1"><label><s:text
																name="config.radiusAssign.priority" /><font color="red"><s:text name="*"/></font></label></td>
															<td colspan="2"><s:select name="serverPriority" cssStyle="width: 100px;" value="%{serverPriority}" 
																list="%{enumPriority}" listKey="key" listValue="value" /></td>
														</tr>
														<tr>
													       	<td style="padding-left: 5px;" colspan="3"><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.advanced.label" />','advanceSet');</script></td>
													    </tr>
													    <tr>
													    	<td id="advanceSet" style="padding-left: 10px;display:<s:property value="%{dataSource.advanceStyle}"/>" colspan="3">
													       		<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td height="4"></td>
																	</tr>
																	<tr id="authTr" style="display:<s:property value="%{serverType!=3?'':'none'}"/>">
																		<td class="labelT1" width="150"><label><s:text
																			name="config.radiusAssign.authPort" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="6" name="authPort" value="%{authPort}" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			<s:text name="config.radiusAssign.portRange" /></td>
																	</tr>
																	<tr id="acctTr" style="display:<s:property value="%{serverType!=2?'':'none'}"/>">
																		<td class="labelT1" width="150"><label><s:text
																			name="config.radiusAssign.accoutPort" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="6" name="acctPort" maxlength="5" value="%{acctPort}"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			<s:text name="config.radiusAssign.portRange" /></td>
																	</tr>
																	<tr>
																		<td height="2"></td>
																	</tr>
																</table>
													       	</td>
													    </tr>
													</table>
													</fieldset>
													</td>
												</tr>
												<tr id="headerSection">
													<th align="left" style="padding-left: 0;" width="10"><input
														type="checkbox" id="checkAll"
														onClick="toggleCheckAllRules(this);"></th>
													<th align="left" width="200"><s:text
														name="config.radiusAssign.serverName" /></th>
													<th align="left" width="120"><s:text
														name="config.radiusAssign.serverType" /></th>
													<th align="left" width="150"><s:text
														name="config.radiusAssign.secret" /></th>
													<th align="left" width="100"><s:text
														name="config.radiusAssign.priority" /></th>
													<th align="left" width="120"><s:text
														name="config.radiusAssign.authPort" /></th>
													<th align="left" width="100"><s:text
														name="config.radiusAssign.accoutPort" /></th>
												</tr>
												<s:iterator value="%{dataSource.services}" status="status">
													<tr>
														<td class="listCheck"><s:checkbox name="ruleIndices"
															fieldValue="%{#status.index}" /></td>
														<td class="list" width="200">
															<s:property value="ipAddress.addressName" />
														</td>
														<td class="list"><s:property value="typeStr" /></td>
														<td class="list" valign="middle">
															<s:password id="shareKey_%{#status.index}" name="sharedSecrets" onkeypress="return hm.util.keyPressPermit(event,'password');" 
												               	maxlength="%{commentLength}" showPassword="true" value="%{sharedSecret}" size="17" /><s:textfield id="shareKey_%{#status.index}_text" name="sharedSecrets" maxlength="%{commentLength}" cssStyle="display:none"
																onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" size="17" /><br><s:checkbox id="chkPasswordDisplay_%{#status.index}" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
												                (this.checked,['shareKey_%{#status.index}'],['shareKey_%{#status.index}_text']);" />
												                <s:text name="config.radiusAssign.secret.obscure" />
														</td>
														<td class="list"><s:select name="priorities" cssStyle="width: 85px;"
															value="%{serverPriority}" list="%{enumPriority}" listKey="key"
															listValue="value" /></td>
														<td class="list" align="center">
														<s:if test="%{serverType == 3}">
															--
														</s:if>
														<s:else>
															<s:textfield size="6" name="authPorts" value="%{authPort}" maxlength="5"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />
														</s:else>
														</td>
														<td class="list" align="center">
														<s:if test="%{serverType == 2}">
															--
														</s:if>
														<s:else>
															<s:textfield size="6" name="accoutPorts" value="%{acctPort}" maxlength="5"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />
														</s:else>
														</td>
													</tr>
												</s:iterator>
												<s:if test="%{gridCount > 0}">
													<s:generator separator="," val="%{' '}" count="%{gridCount}">
														<s:iterator>
															<tr>
																<td class="list" colspan="7">&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
												</s:if>		
											</table>
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
					<tr>
				       	<td style="padding-left: 5px; padding-top: 5px;"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusAssign.optional" />','optionSet');</script></td>
				    </tr>
				    <tr>
				       	<td id="optionSet" style="padding: 5px 0 0 10px;display: <s:property value="%{dataSource.optionalStyle}"/>">
				       		<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="4"></td>
								</tr>
								<tr>
									<td class="labelT1" width="200"><label><s:text
										name="config.radiusAssign.retryInterval" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield size="24" name="dataSource.retryInterval" maxlength="9"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
										name="config.radiusAssign.retryRange" /></td>
								</tr>
								<tr>
									<td class="labelT1"><label><s:text
										name="config.radiusAssign.updateInterval" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield size="24" name="dataSource.updateInterval" maxlength="9"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
										name="config.radiusAssign.updateRangee" /></td>
								</tr>
								<tr>
									<td style="padding: 4px 0 0 6px;" colspan="2">
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>
													<s:checkbox name="dataSource.enableExtensionRadius"
														value="%{dataSource.enableExtensionRadius}" />
												</td>
												<td>
													<s:text name="config.radiusAssign.enableExtensionRadius" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="padding: 4px 0 0 6px;" colspan="2">
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>
													<s:checkbox name="dataSource.injectOperatorNmAttri"
														value="%{dataSource.injectOperatorNmAttri}" />
												</td>
												<td>
													<s:text name="config.radiusAssign.injectOperatorNmAttri" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="2"></td>
								</tr>
							</table>
				       	</td>
			       	</tr>
			       	<tr>
						<td height="6"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>
