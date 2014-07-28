<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var FOLDER_NAS = "nasSettings";
var FOLDER_REALM = "realmSettings";
var formName = 'radiusProxy';
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		if(Get(FOLDER_NAS))
			  Get(formName + "_dataSource_nasSettingsStyle").value = Get(FOLDER_NAS).style.display;
		Get(formName + "_dataSource_realmSettingsStyle").value = Get(FOLDER_REALM).style.display;
	    document.forms[formName].submit();
	}
}

function showFoldingContext(contextId){
	showHideContent(contextId,"");
}

function validate(operation) {
	if (operation == '<%=Navigation.L2_FEATURE_RADIUS_SERVER_PROXY%>' || operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_retryDelay").value = 0;
		document.getElementById(formName + "_dataSource_retryCount").value = 0;
		document.getElementById(formName + "_dataSource_deadTime").value = 0;
		return true;
	}
	if(operation == "editRadiusServer"){
   		var value = hm.util.validateListSelection(formName + "_radiusSer");
   		if(value < 0){
   			return false
   		}
   	}
	
	if(operation == "editIpAddress"){
   		var value = hm.util.validateListSelection("myIpSelect");
   		if(value < 0){
   			return false
   		}else{
   			document.forms[formName].ipAddress.value = value;
   		}
   	}
	<s:if test="%{jsonMode}">
	if(operation == "newRadiusServer" || operation == "editRadiusServer"){
		top.changeIFrameDialog(950, 500);
	} else if(operation == "newIpAddress" || operation == "editIpAddress"){
		top.changeIFrameDialog(950, 450);
	}
	</s:if>
	
	var rmChild = document.getElementById("checkAllRm");
	if (operation == 'addRealm') {
		var rowcount = document.getElementsByName('realmIndices');
		if(rowcount.length == 8) {
			hm.util.reportFieldError(rmChild, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusProxy.realm" /></s:param><s:param value="8" /></s:text>');
        	return false;
		}
		var rmName = document.forms[formName].realmName;
		var message = hm.util.validateName(rmName.value, '<s:text name="config.radiusProxy.realm.name" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(rmName, message);
	       	rmName.focus();
	       	return false;
	   	}
	   	
	   	if(Get(formName + "_realmEnableIDM") 
	   			&& Get("idmEnableRow")
	   			&& (!Get(formName + "_realmEnableIDM").checked || Get("idmEnableRow").style.display != "none")) {
			var primary = Get(formName + "_radiusSer");
	   		if(parseInt(primary.value) <= 0){
	   			hm.util.reportFieldError(primary, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusAssign.serverTab" /></s:param></s:text>');
				primary.focus();
				return false;
	   		}
	   	}
	}
	if (operation == 'removeRealm' || operation == 'removeRealmNone') {
		var cbs = document.getElementsByName('realmIndices');
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(rmChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusProxy.realm" /></s:param></s:text>');
			return false;
		}
	}
	
	var feChild = document.getElementById("checkAll");
	if (operation == 'addIpAddress') {
		var rowcount = document.getElementsByName('descriptions');
		if(rowcount.length == 128) {
			hm.util.reportFieldError(feChild, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.radiusNas" /></s:param><s:param value="128" /></s:text>');
        	//feChild.focus();
        	return false;
		}
		var ipnames = document.getElementById("myIpSelect");
		var ipValue = document.forms[formName].inputIpValue;
		if ("" == ipValue.value) {
	        hm.util.reportFieldError(ipValue, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusOnHiveAp.ipName" /></s:param></s:text>');
	        ipValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				var message = hm.util.validateName(ipValue.value, '<s:text name="config.radiusOnHiveAp.ipName" />');
		    	if (message != null) {
		    		hm.util.reportFieldError(ipValue, message);
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
		if (document.getElementById("chkShareKeyDisplay").checked) {
			password = document.getElementById("sharekey");
			confirm = document.getElementById("sharekeyConf");
		} else {
			password = document.getElementById("sharekey_text");
			confirm = document.getElementById("sharekeyConf_text");
		}
		if(!checkPassword(password, confirm, '<s:text name="config.radiusAssign.secret" />', '<s:text name="config.radiusAssign.confirmSecret" />')) {
        	return false;
    	}
	}
	if (operation == 'removeIpAddress' || operation == 'removeIpAddressNone') {
		var cbs = document.getElementsByName('ipIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.radius.nas" /></s:param></s:text>');
			return false;
		}
	}
	
	if (operation == 'create<s:property value="lstForward"/>' || operation == 'create') {
		if (!validateProxyName()) {
			return false;
		}
	}
	// check realm settings
	if (!checkNumberParam()) {
		return false;
	}
	if (operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'
		|| operation == 'create' || operation == 'update'){
		var rowcount = document.getElementsByName('realmIndices');
		if(rowcount.length == 2 && !isCloudAuthEnabled()) {
			hm.util.reportFieldError(rmChild, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusProxy.realm" /></s:param></s:text>');
        	return false;
		}
		/*
		* NAS Check
		*/
		var secrets = document.getElementsByName('sharedSecrets');
		if(secrets) {
			for(var i = 0; i < secrets.length-1; i += 2) {
				var activeObj;
				if (secrets[i].style.display == "") {
					activeObj = secrets[i];
				} else {
					activeObj = secrets[i+1];
				}
				if (!checkIfInput(activeObj, '<s:text name="config.radiusAssign.secret" />', FOLDER_NAS, feChild)) {
					return false;
				} else {
					var message = hm.util.validatePassword(activeObj.value, '<s:text name="config.radiusAssign.secret" />');
				   	if (message != null) {
				   		showFoldingContext(FOLDER_NAS);
				   		hm.util.reportFieldError(feChild, message);
				       	activeObj.focus();
				       	return false;
				   	}
				}
			}
		}
	}
	return true;
}

function checkNumberParam(){
	if (!validateParamNumber(document.getElementById(formName + "_dataSource_retryDelay"), '<s:text name="config.radiusProxy.retry.delay" />',
		<s:property value="%{delayRange.min()}" />, <s:property value="%{delayRange.max()}" />)) {
		return false;
	}
	if (!validateParamNumber(document.getElementById(formName + "_dataSource_retryCount"), '<s:text name="config.radiusProxy.retry.count" />',
		<s:property value="%{countRange.min()}" />, <s:property value="%{countRange.max()}" />)) {
		return false;
	}
	if (!validateParamNumber(document.getElementById(formName + "_dataSource_deadTime"), '<s:text name="config.radiusProxy.dead.time" />',
		<s:property value="%{deadRange.min()}" />, <s:property value="%{deadRange.max()}" />)) {
		return false;
	}
	return true;
}

function checkIfInput(inputElement, title, contextId, focusInput)
{
	if (inputElement.value.length == 0) {
		if(contextId != null){
        	showFoldingContext(contextId);
        }
        hm.util.reportFieldError(focusInput, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function checkPassword(password, confirm, passtitle, confirmtitle) {
	var message = hm.util.validatePassword(password.value, passtitle);
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, confirmtitle);
   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
       	confirm.focus();
       	return false;
   	}
	if (password.value != confirm.value) {
        hm.util.reportFieldError(confirm, '<s:text name="error.notEqual"><s:param>'+confirmtitle+'</s:param><s:param>'+passtitle+'</s:param></s:text>');
        confirm.focus();
        return false;
    }
    return true;
}

function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='radiusProxy' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
		if (operation == 'create') {
			// 
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["radiusProxy"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="radiusProxy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

function isEmptyStr(str) {
	if(typeof str == 'string' &&str.constructor == String) {
		return str.trim().length == 0;
	}
	return false;
}
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_proxyName").disabled) {
		document.getElementById(formName + "_dataSource_proxyName").focus();
	}
	/* Comment this block for Auto-select authentication proxy
	changeCloudAuthFlag(<s:property value="realmEnableIDM"/>);
	if(<s:property value="realmEnableIDM"/> && !isEmptyStr('<s:property value="radiusSer"/>')) {
		showHideContent("idmEnableRow", "");
	}
	*/
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(900, 750);
	}
	</s:if>
}

function validateProxyName(){
	var inputElement = document.getElementById(formName + "_dataSource_proxyName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.radiusProxy.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
	return true;
}

function validateParamNumber(inputElement, title, min, max){
	if (inputElement.value.length == 0) {
		showFoldingContext(FOLDER_REALM);
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, title, min, max);
	if (message != null) {
		showFoldingContext(FOLDER_REALM);
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

function showCreateSection(newButton, createButton, createSection) {
	hm.util.hide(newButton);
	hm.util.show(createButton);
	hm.util.show(createSection);
}

function hideCreateSection(createButton, newButton, createSection) {
	hm.util.hide(createButton);
	hm.util.show(newButton);
	hm.util.hide(createSection);
}

function toggleCheckAllRules(cb, index) {
	var cbs = document.getElementsByName(index);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeCloudAuthFlag(flag) {
	var DOM = YAHOO.util.Dom;
	if(flag) {
		hm.util.hide("realStripRow");
		
		hm.util.show("idmFolderRow");
		//showHideContent("idmFolderRow", "");
		
		DOM.insertBefore(Get("radiusServerRow"), Get("tlsPortRow"));
	} else {
		hm.util.show("realStripRow");
		
		showHideContent("idmEnableRow", "none");
		hm.util.hide("idmFolderRow");
		
		DOM.insertAfter(Get("radiusServerRow"), Get("idmEnableRow"));
	}
	if(Get(formName + "_realmStrip")) {
		Get(formName + "_realmStrip").checked=!flag;
	}
}
function changeAuthType(flag, index) {
	/*if(flag) {
		if(Get("radiusSers_"+index)
				&& Get("radiusSers_"+index).value.trim().length > 0) {
			hm.util.show("radiusRow_"+index);
			hm.util.hide("cloudAuthRow_"+index);
		} else {
			hm.util.hide("radiusRow_"+index);
			hm.util.show("cloudAuthRow_"+index);
		}
	} else {
		hm.util.hide("cloudAuthRow_"+index);
		hm.util.show("radiusRow_"+index);
	}
	*/
	if(Get("realmStripCHK_"+index)) {
		Get("realmStripCHK_"+index).checked = !flag;
		Get("realmStripCHK_"+index).disabled = flag;
	}
	if(isCloudAuthEnabled()) {
		hm.util.hide("nullRealmRow");
	} else {
		hm.util.show("nullRealmRow");
	}
}
function isCloudAuthEnabled() {
	var chks = document.getElementsByName("realmCloudAuths");
	if(chks) {
		for(var j=chks.length-1; j>=0 ; j--) {
			if(chks[j].checked) {
				return true;
			}
		}
	}
	return false;
}
</script>
<div id="content"><s:form action="radiusProxy">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="contentShowType" />
	<div id="titleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-RADIUS_Proxy.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.radiusproxy.dialog.new.title"/></td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.radiusproxy.dialog.edit.title"/></td>
							</s:else>
							
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>
	<s:hidden name="ipAddress" />
	<s:hidden name="dataSource.nasSettingsStyle"></s:hidden>
	<s:hidden name="dataSource.realmSettingsStyle"></s:hidden>
	<s:if test="%{jsonMode == true}">
		<div style="margin:0 auto; width:100%;">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
	</s:if>
	<s:else>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIUS_SERVER_PROXY%>');">
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
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="800">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800">
			</s:else>
				<tr>
					<td style="padding: 6px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="100">
										<label><s:text name="config.radiusProxy.name" /><font color="red"><s:text name="*"/></font></label>
									</td>
									<td>
										<s:textfield name="dataSource.proxyName" onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="24" maxlength="%{nameLength}" disabled="%{dataSource.id != null}" />
										<s:text name="config.ns.name.range" />
									</td>
								</tr>
								<tr>
									<td class="labelT1">
										<s:text name="config.ns.description" />
									</td>
									<td><s:textfield name="dataSource.description" size="48" maxlength="%{descriptionLength}" />
										<s:text name="config.ns.description.range" />
									</td>
								</tr>
								<tr>
									<td height="2px"></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td style="padding:4px 10px 4px 5px;">
								<fieldset>
									<legend><s:text name="config.radiusProxy.realm" />s</legend>
									<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td style="padding:4px 0 4px 0px;">
												<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="realmTable">
													<tr style="display:<s:property value="%{hideRmNewButton}"/>" id="newRmButton">
														<td colspan="4" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																	class="button" onClick="showCreateSection('newRmButton',
																			'createRmButton', 'createRmSection');"></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" <s:property value="writeDisabled" />
																	onClick="submitAction('removeRealm');"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr style="display:<s:property value="%{hideRmCreateItem}"/>" id="createRmButton">
														<td colspan="4" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																	class="button" onClick="submitAction('addRealm');"></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" <s:property value="writeDisabled" />
																	onClick="submitAction('removeRealmNone');"></td>
																<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																	class="button" onClick="hideCreateSection('createRmButton', 'newRmButton', 'createRmSection');"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr style="display:<s:property value="%{hideRmCreateItem}"/>" id="createRmSection">
														<td colspan="4" style="padding-left: 5px;">
														<fieldset style="width: 450px;background-color: #edf5ff">
														<legend><s:text name="config.radiusProxy.realm.addNew" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="130"><label><s:text
																	name="config.radiusProxy.realm.name" /><font color="red"><s:text name="*"/></font></label>
																</td>
																<td>
																	<s:textfield name="realmName" onkeypress="return hm.util.keyPressPermit(event,'name');"
																	size="24" maxlength="%{nameLength}" value="%{realmName}" />
																	<s:text name="config.ns.name.range" />
																</td>
															</tr>
                                                            <!--Comment this block for Auto-select authentication proxy tr>
                                                                <td colspan="2">
                                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                                        <tr>
                                                                            <td style="padding:2px 2px 2px 6px;"><s:checkbox name="realmEnableIDM" value="%{realmEnableIDM}" 
                                                                                onclick="changeCloudAuthFlag(this.checked);"/></td>
                                                                            <td class="labelT1" style="padding-left: 0"><label for="radiusProxy_realmEnableIDM"><s:text
                                                                                name="config.radiusProxy.cloudAuth.use" /></label></td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr id="idmFolderRow" style="display: none;">
                                                                <td colspan="2" style="padding-left: 8px;"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusProxy.cloudAuth.auth.settings" />','idmEnableRow');</script></td>
                                                            </tr>
                                                            <tr id="idmEnableRow" style="display: none;">
                                                                <td colspan="2" style="padding-left: 36px;">
                                                                    <table border="0" cellpadding="0" cellspacing="0">
                                                                        <tr id="tlsPortRow">
			                                                                 <td class="labelT1"><label><s:text name="config.radiusProxy.cloudAuth.tlsprot" />
			                                                                    </label></td>
			                                                                 <td><s:textfield name="realmTlsPort" value=""
			                                                                     maxlength="4" cssStyle="width: 148px;"
			                                                                     onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr-->
															<tr id="radiusServerRow">
																<td class="labelT1" style="width: 100px;"><label><s:text name="config.radiusAssign.serverTab" /><font color="red"><s:text name="*"/></font></label></td>
																<td><s:select name="radiusSer" cssStyle="width: 152px;" value="%{radiusSer}" 
																	list="%{availableRadiusServer}" listKey="id" listValue="value" />
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newRadiusServer')"><img class="dinl"
																		src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editRadiusServer')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>
															<tr id="realStripRow">
																<td colspan="2">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td style="padding:2px 2px 2px 6px;"><s:checkbox name="realmStrip" value="%{realmStrip}" /></td>
																			<td class="labelT1" style="padding-left: 0"><label for="radiusProxy_realmStrip"><s:text
																				name="config.radiusProxy.trip.description" /></label></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
													<tr id="headerRmSection">
														<th align="left" style="padding-left: 0;" width="10"><input
															type="checkbox" id="checkAllRm"
															onClick="toggleCheckAllRules(this, 'realmIndices');"></th>
														<th align="left" width="200"><s:text
															name="config.radiusProxy.realm.name" /></th>
														<th align="left" width="180"><s:text
															name="config.radiusAssign.serverTab" /></th>
														<th align="center" width="105"><s:text
															name="config.radiusProxy.trip.tile" /></th>
														<!-- Comment this block for Auto-select authentication proxy
														 th align="center" width="105"><s:text
															name="config.radiusProxy.cloudAuth.tile" /></th-->
													</tr>
													<s:iterator value="%{dataSource.radiusRealm}" status="status">
														<tr <s:if test="%{serverName == 'Null'}">id="nullRealmRow"</s:if> 
														      <s:if test="%{serverName == 'Null' && dataSource.enabledIDM}">style="display: none;"</s:if> >
															<td class="listCheck"><s:checkbox name="realmIndices"
																fieldValue="%{#status.index}" /></td>
															<td class="list">
																<s:property value="serverName" />
															</td>
															<s:if test="%{serverName == 'Null'}">
															<td class="list"><s:select name="radiusSers" cssStyle="width: 152px;"
																value="%{radiusServer.id}" list="%{availableRadiusServer}" listKey="id" listValue="value" /></td>
															</s:if>
															<s:else>
															<td class="list"
															    id="radiusRow_<s:property value='%{#status.index}'/>">
															<s:select name="radiusSers" cssStyle="width: 152px;" 
															    id="radiusSers_%{#status.index}"
																value="%{radiusServer.id}" list="%{availableRadiusServer}" listKey="id" listValue="value" /></td>
															</s:else>
															<td class="list" align="center">
															<s:if test="%{serverName == 'Null'}">
																&nbsp;
															</s:if>
															<s:else>
																<s:checkbox name="realmStrips" fieldValue="%{#status.index}" value="%{strip}" 
																    id="realmStripCHK_%{#status.index}" disabled="%{useIDM}"/>
															</s:else>
															</td>
															<!-- td class="list" align="center">
															<s:if test="%{serverName == 'Null'}">
																&nbsp;
															</s:if>
															<s:else>
																<s:checkbox name="realmCloudAuths" fieldValue="%{#status.index}" value="%{useIDM}" 
																    onclick="changeAuthType(this.checked, %{#status.index});"/>
															</s:else>
															</td-->
														</tr>
													</s:iterator>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
						<!-- Realm Settings -->
						<tr>
							<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusProxy.realm.settings" />','realmSettings');</script></td>
						</tr>
						<tr>
							<td style="padding-left: 10px;">
								<div id="realmSettings" style="display: <s:property value="%{dataSource.realmSettingsStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="200"><label><s:text
												name="config.radiusProxy.format" /></label></td>
											<td><s:select name="dataSource.proxyFormat" list="%{enumFormat}" listKey="key"
												listValue="value" cssStyle="width: 200px" /></td>
										</tr>
										<tr>
											<td class="labelT1">
												<label><s:text name="config.radiusProxy.retry.delay" /><font color="red"><s:text name="*"/></font></label>
											</td>
											<td><s:textfield size="12" name="dataSource.retryDelay" maxlength="2"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.radiusProxy.retry.delay.range" /></td>
										</tr>
										<tr>
											<td class="labelT1">
												<label><s:text name="config.radiusProxy.retry.count" /><font color="red"><s:text name="*"/></font></label>
											</td>
											<td><s:textfield size="12" name="dataSource.retryCount" maxlength="2"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.radiusProxy.retry.count.range" /></td>
										</tr>
										<tr>
											<td class="labelT1">
												<label><s:text name="config.radiusProxy.dead.time" /><font color="red"><s:text name="*"/></font></label>
											</td>
											<td><s:textfield size="12" name="dataSource.deadTime" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.radiusProxy.dead.time.range" /></td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="dataSource.injectOperatorNmAttri"
													value="%{dataSource.injectOperatorNmAttri}" />
												<s:text name="config.radiusAssign.injectOperatorNmAttri" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td height="4px"></td>
						</tr>
						<s:if test="%{!dataSource.proxy4Router}">
						<!-- NAS Settings -->
						<tr>
							<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.radius.nasSetting" />','nasSettings');</script></td>
						</tr>
						<tr>
							<td style="padding-left: 10px;">
								<div id="nasSettings" style="display: <s:property value="%{dataSource.nasSettingsStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr><td height="10px"></td></tr>
									<tr>
										<td valign="top">
											<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="radiusTable" width="100%">
												<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
													<td colspan="6" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																class="button" onClick="showCreateSection('newButton',
																			'createButton', 'createSection');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeIpAddress');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
													<td colspan="6" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" onClick="submitAction('addIpAddress');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button"
																onClick="submitAction('removeIpAddressNone');"></td>
															<td><input type="button" name="ignore" value="Cancel"
																class="button" onClick="hideCreateSection('createButton', 'newButton', 'createSection');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
													<td colspan="4" style="padding-left: 5px;">
														<fieldset style="background-color: #edf5ff">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr style="padding-top: 5px;">
																<td class="labelT1" width="150"><label><s:text
																	name="config.radiusOnHiveAp.ipName" /><font color="red"><s:text name="*"/></font></label></td>
																<td>
																	<ah:createOrSelect divId="errorDisplay" swidth="193px"
																		list="availableIpAddress" typeString="IpAddress"
																		selectIdName="myIpSelect"
																		inputValueName="inputIpValue" />
																</td>
															</tr>
															<tr>
																<td class="labelT1"><label><s:text
																	name="config.radiusAssign.secret" /><font color="red"><s:text name="*"/></font></label></td>
																<td><s:password size="32" id="sharekey" name="sharekey" onkeypress="return hm.util.keyPressPermit(event,'password');"
													                maxlength="31" showPassword="true" value="%{sharekey}" /><s:textfield id="sharekey_text" name="sharekey" maxlength="31" size="32" cssStyle="display:none"
																	onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" />
																	<s:text name="config.radiusOnHiveAp.shareRange" /></td>
															</tr>
															<tr>
																<td class="labelT1"><label><s:text
																	name="config.radiusAssign.confirmSecret" /><font color="red"><s:text name="*"/></font></label></td>
																<td><s:password size="32" id="sharekeyConf" onkeypress="return hm.util.keyPressPermit(event,'password');"
										                			maxlength="31" showPassword="true" value="%{sharekey}" /><s:textfield id="sharekeyConf_text" maxlength="31" size="32" cssStyle="display:none" 
										                			disabled="true" onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	<s:text name="config.radiusOnHiveAp.shareRange" /></td>
															</tr>
															<tr>
																<td>&nbsp;</td>
																<td><s:checkbox id="chkShareKeyDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
										                			(this.checked,['sharekey','sharekeyConf'],['sharekey_text','sharekeyConf_text']);" />
										                			<s:text name="config.radiusAssign.secret.obscure" /></td>
															</tr>
															<tr>
																<td class="labelT1"><label><s:text name="config.radiusAssign.description" /></label></td>
																<td><s:textfield size="40" name="description" maxlength="64" value="%{description}"/>
																	<s:text name="config.radiusOnHiveAp.desRange" /></td>
															</tr>
														</table>
														</fieldset>
													</td>
												</tr>
												<tr id="headerSection">
													<th align="left" style="padding-left: 0;" width="10"><input
														type="checkbox" id="checkAll"
														onClick="toggleCheckAllRules(this, 'ipIndices');"></th>
													<th align="left" width="200px"><s:text
														name="config.radiusOnHiveAp.ipName" /></th>
													<th align="left" width="150px"><s:text
														name="config.radiusAssign.secret" /></th>
													<th align="left" width="200px"><s:text
														name="config.radiusAssign.description" /></th>
												</tr>
												<s:iterator value="%{dataSource.radiusNas}" status="status">
													<tr>
														<td class="listCheck"><s:checkbox name="ipIndices"
															fieldValue="%{#status.index}" /></td>
														<td class="list">
															<s:property value="ipAddress.addressName" />
														</td>
														<td class="list" valign="middle">
															<s:password id="shareKey_%{#status.index}" name="sharedSecrets" onkeypress="return hm.util.keyPressPermit(event,'password');" 
												               	maxlength="31" showPassword="true" value="%{sharedKey}" size="24" /><s:textfield id="shareKey_%{#status.index}_text" name="sharedSecrets" maxlength="31" cssStyle="display:none"
																onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" size="24" /><br><s:checkbox id="chkPasswordDisplay_%{#status.index}" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
												                (this.checked,['shareKey_%{#status.index}'],['shareKey_%{#status.index}_text']);" />
												                <s:text name="config.radiusAssign.secret.obscure" />
														</td>
														<td class="list"><s:textfield size="30" name="descriptions"
															value="%{description}" maxlength="64" /></td>
													</tr>
												</s:iterator>
												<s:if test="%{gridCount > 0}">
													<s:generator separator="," val="%{' '}" count="%{gridCount}">
														<s:iterator>
															<tr>
																<td class="list" colspan="4">&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
												</s:if>
											</table>
										</td>
									</tr>
								</table>
								</div>
							</td>
						</tr>
					    </s:if>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<s:if test="%{jsonMode == true}">
		</div>
	</s:if>
</s:form></div>