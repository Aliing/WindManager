<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<style>
td.uplist {
	padding: 2px 5px 2px 3px;
	border-bottom: 1px solid #cccccc;
	background-color: #FFFF00;
}
</style>

<script>
var formName = 'devicePolicy';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_DEVICE_POLICY%>' == operation
		|| operation == 'newMac'
		|| operation == 'newOs'
		|| operation == 'newUserProfile'
		|| operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	if(operation == "editMac" || operation == "editOs" || operation == "editUserProfile"){
		var value
		if (operation == "editMac") {
			value = hm.util.validateListSelection(formName + "_macObjId");
		} else if (operation == "editOs") {
			value = hm.util.validateListSelection(formName + "_osObjId");
		} else {
			value = hm.util.validateListSelection(formName + "_userProfileId");
		}
		if(value < 0){
			return false
		}else{
			document.forms[formName].editObjId.value = value;
		}
	}
	
	var feChild = document.getElementById("checkAll");
	if (operation == 'addPolicyRules') {
		// domain name
		var fieldSel = document.getElementById("domainSelect");
		var fieldValue = document.forms[formName].domainName;
		if ("" != fieldValue.value) {
			if (!hm.util.hasSelectedOptionSameValue(fieldSel, fieldValue)) {
				retMsg = hm.util.validateString(fieldValue.value, '<s:text name="config.devicePolicy.domain" />');
				if (null != retMsg) {
					hm.util.reportFieldError(feChild, retMsg);
				    fieldValue.focus();
				    return false;
				}
				document.forms[formName].domainNameId.value = -1;
			} else {
				document.forms[formName].domainNameId.value = fieldSel.options[fieldSel.selectedIndex].value;
			}
		}
			
		// original attribute
		// var orgAttribute = document.forms[formName].orgAttribute;
		// if(!checkAttributeValue(orgAttribute, '<s:text name="config.devicePolicy.orgAttribute" />')) {
			// return false;
		// }
		
		// user profile
		// var userProfileId = document.forms[formName].userProfileId;
		// if (!hasSelectedOptions(userProfileId.options)) {
            // hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.devicePolicy.userProfile" /></s:param></s:text>'); 
            // userProfileId.focus(); 
			// return false;
		// }
	} 
	if (operation == 'removePolicyRules' || operation == 'removePolicyRulesNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ipPolicy.rules" /></s:param></s:text>');
			return false;
		}
	} 
	var name = document.getElementById(formName + "_dataSource_policyName");
	if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'
		|| operation == 'update'+'<s:property value="lstForward"/>' || operation == 'create') {
		var message = hm.util.validateName(name.value, '<s:text name="config.ipPolicy.policyName" />');
		if (message != null) {
	   		hm.util.reportFieldError(name, message);
	       	name.focus();
	       	return false;
	   	}
    }
	return true;
}

function checkAttributeValue(orgAttribute, title) {
	var feChild = document.getElementById("checkAll");
	var retMsg = hm.util.validateIntegerRange(orgAttribute.value, title,1,4095);
	if(retMsg != null) {
		hm.util.reportFieldError(feChild, retMsg);
		orgAttribute.focus();
		return false;
	}
	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;	
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value != -1) {
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

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="devicePolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="devicePolicy">
	<s:hidden name="editObjId" />
	<s:hidden name="domainNameId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_DEVICE_POLICY%>');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="90"><label><s:text
									name="config.ipPolicy.policyName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="24" name="dataSource.policyName"
									maxlength="%{policyNameLength}" disabled="%{disabledName}" 
									onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.ipPolicy.description" /></label></td>
								<td><s:textfield size="48" name="dataSource.description"
									maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr>
								<td colspan="2" class="labelT1">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><s:checkbox name="dataSource.enableSingleCheck" /></td>
										<td>&nbsp;<s:text name="config.devicePolicy.enableSingleCheck" /></td>
									</tr>
									<tr>
										<td height="6"></td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td><s:text name="config.devicePolicy.enableSingleCheck.note" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" class="labelT1">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><s:text name="config.devicePolicy.classfication.method" /></td>
										<td>&nbsp;<s:checkbox name="dataSource.enableOui" /></td>
										<td>&nbsp;<s:text name="config.macOrOui.macAddress" /></td>
										<td>&nbsp;<s:checkbox name="dataSource.enableOs" /></td>
										<td>&nbsp;<s:text name="config.osObject.full.name" /></td>
										<td>&nbsp;<s:checkbox name="dataSource.enableDomain" /></td>
										<td>&nbsp;<s:text name="config.devicePolicy.domain" /></td>
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
					<td style="padding:0 10px 0 10px">
						<fieldset><legend><s:text name="config.ipPolicy.rules" /></legend>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td style="padding:4px 0px 4px 0px;" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" class="embedded">
											
											<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
												<td colspan="7" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
															class="button" onClick="showCreateSection();"></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button" <s:property value="writeDisabled" />
															onClick="submitAction('removePolicyRules');"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
												<td colspan="7" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
															class="button" onClick="submitAction('addPolicyRules');"></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button" <s:property value="writeDisabled" />
															onClick="submitAction('removePolicyRulesNone');"></td>
														<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
															class="button" onClick="hideCreateSection();"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="headerSection">
												<th align="left" style="padding-left: 0;" width="10"><input
													type="checkbox" id="checkAll"
													onClick="toggleCheckAllRules(this);"></th>
												<th align="left" width="50"><s:text
													name="config.ipPolicy.ruleId" /></th>
												<th align="left" width="220"><s:text
													name="config.devicePolicy.mac" /></th>
												<th align="left" width="220"><s:text
													name="config.devicePolicy.os" /></th>
												<th align="left" width="190"><s:text
													name="config.devicePolicy.domain" /><br/><s:text name="config.description.range" /></th>
												<th align="left" width="150"><s:text
													name="config.devicePolicy.userProfile.init" /></th>
												<th align="left" width="220"><s:text
													name="config.devicePolicy.userProfile" /></th>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
												<td class="listHead" width="10">&nbsp;</td>
												<td class="listHead" width="50">&nbsp;</td>
												<td class="listHead" valign="top" width="220" nowrap>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:select name="macObjId" list="%{availableMacAddress}" listKey="id" listValue="value"
																cssStyle="width: 150px;" /></td>
															<td valign="top" style="padding-left:3px;">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newMac')"><img class="dinl"
																	src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editMac')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
															</td>
														</tr>
													</table>
												</td>
												<td class="listHead" valign="top" width="220" nowrap>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:select name="osObjId" list="%{availableOsObjects}" listKey="id" listValue="value"
																cssStyle="width: 150px;" /></td>
															<td valign="top" style="padding-left:3px;">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newOs')"><img class="dinl"
																	src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editOs')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
															</td>
														</tr>
													</table>
												</td>
												<td width="190" class="listHead" valign="top">
													<ah:createOrSelect divId="errorDisplay" list="availableDomainFields" swidth="200px" tlength="64"
													selectIdName="domainSelect" inputValueName="domainName" hideButton="true" /></td>
												<td width="150" class="listHead" valign="top">
													<s:select name="initUserProfileId" cssStyle="width: 150px;" list="%{availableUserProfiles}" 
													listKey="id" listValue="value" />
												</td>
												<td width="220" class="listHead" valign="top" nowrap>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:select name="userProfileId" cssStyle="width: 150px;"
																list="%{availableUserProfiles}" listKey="id" listValue="value" /></td>
															<td valign="top" style="padding-left:3px;">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newUserProfile')"><img class="dinl"
																	src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editUserProfile')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<s:if test="%{gridCount > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount}">
													<s:iterator>
														<tr>
															<td class="list" colspan="7">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>	
										<tr>
											<td valign="top" colspan="7">
												<table cellspacing="0" cellpadding="0" border="0"
													class="embedded" id="policyTable">
													<s:iterator value="%{dataSource.rules}" status="status">
														<tr>
															<td class="listCheck" width="10"><s:checkbox name="ruleIndices"
																fieldValue="%{#status.index}" /></td>
															<td class="list" width="50"><s:property value="ruleId" /></td>
															<td class="list" width="205">&nbsp;&nbsp;
																<s:if test="%{macObj == null}">
																	<s:text name="config.ipPolicy.any" />
																</s:if>
																<s:else
																	><s:property value="macObj.macOrOuiName" />
																</s:else>
															</td>
															<td class="list" width="205">&nbsp;&nbsp;
																<s:if test="%{osObj == null}">
																	<s:text name="config.ipPolicy.any" /></s:if>
																<s:else
																	><s:property value="osObj.osName" /></s:else>
															</td>
															<td width="190" class="list" valign="top"><s:textfield size="28" maxlength="64"
																onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" 
																name="domainNames" value= "%{domainName}" /></td>
															<td width="150" class="list" valign="top"><s:select cssStyle="width: 150px;" 
																list="%{availableUserProfiles}" listKey="id" listValue="value" name="initUserProfileIds" value="%{initUserProId}"/></td>
															<td width="150" class="uplist" valign="top"><s:select cssStyle="width: 150px;" 
																list="%{availableUserProfiles}" listKey="id" listValue="value" name="userProfileIds" value="%{userProId}"/></td></tr>
													</s:iterator>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
			</table>
		</td>
		</tr>
	</table>
</s:form></div>
