<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'osObject';
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_osName").disabled == false) {
		document.getElementById(formName + "_dataSource_osName").focus();
	}
	<s:if test="%{jsonMode == true}">
 	if(top.isIFrameDialogOpen()) {
 		top.changeIFrameDialog(710,560);
 	}
 	</s:if>
 	
 	var dhcpRuleIndices = document.getElementsByName("dhcpRuleIndices");
	for (var i = 0; i < dhcpRuleIndices.length; i++) {
		disableOp55(dhcpRuleIndices[i].value,false)
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}

<s:if test="%{jsonMode == true}">
function saveOsObjectJsonDlg(operation) {
	if (!validate(operation)) {
		return;
	}
	var url = "<s:url action='osObject' includeParams='none' />?ignore="+new Date().getTime();
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("osObject"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveOsObjectJsonDlg, failure : failSaveOsObjectJsonDlg, timeout: 60000}, null);	
}
var succSaveOsObjectJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	} catch(e) {
	}
}
var failSaveOsObjectJsonDlg = function(o){
}
</s:if>

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_OS_OBJECT%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}

	if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_osName");
		var message = hm.util.validateName(name.value, '<s:text name="config.osObject.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    	if (name.value == '<s:text name="config.ipPolicy.any" />') {
    		hm.util.reportFieldError(name, '<s:text name="error.ipOrMacOrService.nameLimit"><s:param><s:text name="config.ipPolicy.any" /></s:param></s:text>');
        	name.focus();
        	return false;
    	}
	}

	var table = document.getElementById("checkAll");
    if (operation == 'addOsVersion') {
    	var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length >= 255) {
			hm.util.reportFieldError(table, '<s:text name="error.objectReachLimit"></s:text>');
			return false;
		}
    	// domain name
		var fieldSel = document.getElementById("osVersionSelect");
		var fieldValue = document.forms[formName].osVersion;
		if (!checkOsObject('<s:text name="config.osObject.version" />', fieldValue,'checkAll')) {
			return false;
		} else {
			if (!hm.util.hasSelectedOptionSameValue(fieldSel, fieldValue)) {
				document.forms[formName].osVersionId.value = -1;
			} else {
				document.forms[formName].osVersionId.value = fieldSel.options[fieldSel.selectedIndex].value;
			}
		}
    }

    if (operation == 'removeOsVersion' || operation == 'removeOsVersionNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text
													name="config.osObject.version" /></s:param></s:text>');
			return false;
		}
	}
    
    var tableDhcp = document.getElementById("checkAllDhcp");
    if (operation == 'addDhcpOsVersion') {
    	var cbs = document.getElementsByName('dhcpRuleIndices');
		if (cbs.length >= 255) {
			hm.util.reportFieldError(tableDhcp, '<s:text name="error.objectReachLimit"></s:text>');
			return false;
		}
    	// domain name
		var fieldSel = document.getElementById("osVersionDhcpSelect");
		var fieldValue = document.forms[formName].dhcpOsVersion;
		if (!checkOsObject('<s:text name="config.osObject.version" />', fieldValue,'checkAllDhcp')) {
			return false;
		} else {
			if (!hm.util.hasSelectedOptionSameValue(fieldSel, fieldValue)) {
				document.forms[formName].dhcpOsVersionId.value = -1;
				//option55
				var option55 = document.getElementById(formName+"_option55");
				if(!checkOption55('<s:text name="config.osObject.option55" />',option55)) {
					return false;
				}
				var option55s = document.getElementsByName('option55s');
				for(var i=0;i<option55s.length;i++){
					if(option55.value==option55s[i].value){
						hm.util.reportFieldError(tableDhcp, '<s:text name="error.sameObjectExists"><s:param><s:text name="config.osObject.option55" /></s:param></s:text>');
						option55.focus();
				        return false;
					}
				}
			} else {
				document.forms[formName].dhcpOsVersionId.value = fieldSel.options[fieldSel.selectedIndex].value;
			}
		}
    }
    
    if (operation == 'removeDhcpOsVersion' || operation == 'removeDhcpOsVersionNone') {
		var cbs = document.getElementsByName('dhcpRuleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(tableDhcp, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(tableDhcp, '<s:text name="error.pleaseSelectItem"><s:param><s:text
													name="config.osObject.version" /></s:param></s:text>');
			return false;
		}
	}
    
    if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update'+'<s:property value="lstForward"/>'
    	|| operation == 'create' || operation == 'update') {
    	var osVsersions = document.getElementsByName('osVsersions');
		//if(osVsersions.length == 0)
		//{
		//	hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.osObject.version" /></s:param></s:text>');
       	//	return false;
		//}

		for(var i = 0; i < osVsersions.length; i ++) {
			if (!checkOsObject('<s:text name="config.osObject.version" />'+' in '+(i+1)+' row',osVsersions[i],'checkAll')) {
				return false;
			}
		}
		
		var dhcpOsVersions = document.getElementsByName('dhcpOsVersions');
		//if(dhcpOsVersions.length == 0)
		//{
		//	hm.util.reportFieldError(tableDhcp, '<s:text name="error.requiredField"><s:param><s:text name="config.osObject.version" /></s:param></s:text>');
       //		return false;
		//}

		for(var i = 0; i < dhcpOsVersions.length; i ++) {
			if (!checkOsObject('<s:text name="config.osObject.version" />'+' in '+(i+1)+' row',dhcpOsVersions[i],'checkAllDhcp')) {
				return false;
			}
		}
		var osVersionDhcpSelect = document.getElementById('osVersionDhcpSelect');
		var option55s = document.getElementsByName('option55s');
		for(var i=0;i<option55s.length;i++){
			if(option55s[i].readOnly){
				break;
			}
			if (!checkOption55('<s:text name="config.osObject.option55" />'+' in '+(i+1)+' row',option55s[i])) {
				return false;
			}
		}
    }
	return true;
}

function checkOption55(name,option55){
	var table = document.getElementById('checkAllDhcp');
	if (trim(option55.value).length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+name+'</s:param></s:text>');
        option55.focus();
        return false;
    }
	var pattern = /^(\d+,)*\d+$/;  //like 1,15,3,6,44,46,47,31,33,249,43
	if(!pattern.test(trim(option55.value))){
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+name+'</s:param></s:text>');
		option55.focus();
        return false;
	}
	var option55List = option55.value.split(',');
	var pattern_value = /^[1-9]\d{0,1}$|^1\d{2}$|^2[0-4]\d{1}$|^25[0-5]$/; //1-255

    for(var i=0;i<option55List.length;i++) {
		if(!pattern_value.test(option55List[i])){
			hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+name+'</s:param></s:text>');
			option55.focus();
	        return false;
		}
	}
	return true;
}

function checkOsObject(name, version,checkAll) {
	var table = document.getElementById(checkAll);
	if (version.value.trim().length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+name+'</s:param></s:text>');
        version.focus();
        return false;
    }
	return true;
}

function showCreateSection(type) {
	var trh;
	var trc;
	if (type =='dhcp') {
		hm.util.hide('newDhcpButton');
		hm.util.show('createDhcpButton');
		hm.util.show('createDhcpSection');
		trh = document.getElementById('headerDhcpSection');
		trc = document.getElementById('createDhcpSection');
	} else {
		hm.util.hide('newButton');
		hm.util.show('createButton');
		hm.util.show('createSection');
		trh = document.getElementById('headerSection');
		trc = document.getElementById('createSection');
	}
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection(type) {
	if(type=='dhcp') {
		hm.util.hide('createDhcpButton');
		hm.util.show('newDhcpButton');
		hm.util.hide('createDhcpSection');
	} else {
		hm.util.hide('createButton');
		hm.util.show('newButton');
		hm.util.hide('createSection');
	}
	
}

function toggleCheckAllRules(cb,name) {
	var cbs = document.getElementsByName(name);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function disableOp55(id,flag) {
	var osVersionDhcpSelect = document.getElementById('osVersionDhcpSelect');
	var dhcpOsVersion = document.getElementById('dhcpOsVersions_'+id);
	var option55 = document.getElementById('option55s_'+id);
	var option55Values = document.getElementById("option55DhcpSelect");
	for (var i=0;i<osVersionDhcpSelect.options.length;i++) {
		if (osVersionDhcpSelect.options[i].text==dhcpOsVersion.value && dhcpOsVersion.value!=''){
			if(option55Values!= undefined && option55Values[i].text != ""){
				option55.value=option55Values[i].text;
			} else {
				option55.value='Default';
			}
			option55.readOnly=true;
			break;
		}
		if(i==osVersionDhcpSelect.options.length-1) {
			option55.readOnly=false;
			if(flag && option55.value=='Default'){
				option55.value='';
			}
		}
	}
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="osObject" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>

<div id="content"><s:form action="osObject">
	<s:hidden name="osVersionId"></s:hidden>
	<s:hidden name="dhcpOsVersionId"></s:hidden>
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="osObjectTitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-OS_Objects.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.osObject"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.osObject.edit"/></td>
						</s:else>
					</tr>
				</table>
				</td>
				<td align="right">
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{writeDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onclick="saveOsObjectJsonDlg('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onclick="saveOsObjectJsonDlg('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:else>
					</tr>
				</table>
				</s:if>
				<s:else>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="osObjectSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_OS_OBJECT%>');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="600">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="600">
			</s:else>
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="100"><label><s:text
										name="config.osObject.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield
										name="dataSource.osName" size="24"
										maxlength="%{addressNameLength}" disabled="%{disabledName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
										name="config.ssid.ssidName_range" /></td>
								</tr>
								<tr>
									<td style="padding:6px 0px 6px 0px" colspan="3">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td class="sepLine"><img
												src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="3" style="padding:4px 0px 4px 4px;" valign="top">
										<fieldset>
											<legend><s:text name="config.osObject.dhcp.title" /></legend>
											<table cellspacing="0" cellpadding="0" border="0"  width="100%" class="embedded">
												<tr>
													<td height="10px"></td>
												</tr>
												<tr style="display:<s:property value="%{hideDhcpNewButton}"/>" id="newDhcpButton">
													<td colspan="7" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="New"
																class="button" onClick="showCreateSection('dhcp');"
																<s:property value="writeDisabled" />></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeDhcpOsVersion');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideDhcpCreateItem}"/>" id="createDhcpButton">
													<td colspan="7" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" onClick="submitAction('addDhcpOsVersion');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button"
																onClick="submitAction('removeDhcpOsVersionNone');"></td>
															<td><input type="button" name="ignore" value="Cancel"
																class="button" onClick="hideCreateSection('dhcp');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="headerDhcpSection">
													<th align="left" style="padding-left: 0;" width="10"><input
														type="checkbox" id="checkAllDhcp"
														onClick="toggleCheckAllRules(this,'dhcpRuleIndices');"></th>
													<th align="left" width="250"><s:text
														name="config.osObject.version" />
														<s:text name="config.ssid.ssidName_range" /></th>
													<th align="left" width="250"><s:text
														name="config.osObject.option55" />
														<s:text name="config.osObject.option55.range" /><br>
														<s:text name="config.osObject.option55.example"></s:text>
													</th>
												</tr>
												<tr style="display:<s:property value="%{hideDhcpCreateItem}"/>" id="createDhcpSection">
													<td class="listHead" width="10">&nbsp;</td>
													<td class="listHead" valign="top">
														<ah:createOrSelect divId="errorDisplay" list="availableDhcpOsVersionFields" swidth="200px"
															selectIdName="osVersionDhcpSelect" inputValueName="dhcpOsVersion" hideButton="true"
															inputKeyPress="nameWithBlank" />
														<s:select id="option55DhcpSelect" list="availableDhcpOption55Fields" 
															listKey="id" listValue="value" cssStyle="display:none;"></s:select>
													</td>
													<td width="100" class="listHead" valign="top"><s:textfield size="24" name="option55"
														maxlength="256" /></td>
												</tr>
												<s:iterator value="%{dataSource.dhcpItems}" status="status">
													<tr>
														<td class="listCheck"><s:checkbox name="dhcpRuleIndices"
															fieldValue="%{#status.index}" /><s:property value="dhcpOsVersion"/></td>
														<td class="list" width="100"><s:textfield size="30" name="dhcpOsVersions" id="dhcpOsVersions_%{#status.index}"
															maxlength="32" value="%{osVersion}" onblur="disableOp55('%{#status.index}',true);"/></td>
														<td width="100" class="list"><s:textfield size="24" name="option55s" value="%{option55}" id="option55s_%{#status.index}"
															maxlength="256" /></td>
													</tr>
												</s:iterator>
												<s:if test="%{dhcpGridCount > 0}">
													<s:generator separator="," val="%{' '}" count="%{dhcpGridCount}">
														<s:iterator>
															<tr>
																<td class="list" colspan="5">&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
												</s:if>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td colspan="3" style="padding:4px 0px 4px 4px;" valign="top">
										<fieldset>
											<legend><s:text name="config.osObject.http.title" /></legend>
											<table cellspacing="0" cellpadding="0" border="0"  width="100%" class="embedded">
												<tr>
													<td height="10px"></td>
												</tr>
												<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
													<td colspan="7" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="New"
																class="button" onClick="showCreateSection('http');"
																<s:property value="writeDisabled" />></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeOsVersion');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
													<td colspan="7" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" onClick="submitAction('addOsVersion');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button"
																onClick="submitAction('removeOsVersionNone');"></td>
															<td><input type="button" name="ignore" value="Cancel"
																class="button" onClick="hideCreateSection('http');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="headerSection">
													<th align="left" style="padding-left: 0;" width="10"><input
														type="checkbox" id="checkAll"
														onClick="toggleCheckAllRules(this,'ruleIndices');"></th>
													<th align="left" width="250"><s:text
														name="config.osObject.version" />
														<s:text name="config.ssid.ssidName_range" /></th>
													<th align="left" width="250"><s:text
														name="config.ipAddress.description" />
														<s:text name="config.ssid.description_range" /></th>
												</tr>
												<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
													<td class="listHead" width="10">&nbsp;</td>
													<td class="listHead" valign="top">
														<ah:createOrSelect divId="errorDisplay" list="availableOsVersionFields" swidth="200px"
															selectIdName="osVersionSelect" inputValueName="osVersion" hideButton="true"
															inputKeyPress="nameWithBlank" /></td>
													<td width="100" class="listHead" valign="top"><s:textfield size="24" name="description"
														maxlength="%{commentLength}" /></td>
												</tr>
												<s:iterator value="%{dataSource.items}" status="status">
													<tr>
														<td class="listCheck"><s:checkbox name="ruleIndices"
															fieldValue="%{#status.index}" /></td>
														<td class="list" width="100"><s:textfield size="30" name="osVsersions"
															maxlength="32" value="%{osVersion}" /></td>
														<td width="100" class="list"><s:textfield size="24" name="descriptions" value="%{description}"
															maxlength="%{commentLength}" /></td>
													</tr>
												</s:iterator>
												<s:if test="%{gridCount > 0}">
													<s:generator separator="," val="%{' '}" count="%{gridCount}">
														<s:iterator>
															<tr>
																<td class="list" colspan="5">&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
												</s:if>
											</table>
										</fieldset>
									</td>
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
	</table>
</s:form></div>
