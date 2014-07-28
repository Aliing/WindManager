<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.IpPolicyRule"%>

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'macPolicy';
var ACTION_PERMIT = <%=IpPolicyRule.POLICY_ACTION_PERMIT%>;

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(950, 500);
		}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation=="newMac" || operation=="editDestMac" || operation=="editSourceMac") {
			<s:if test="%{jsonMode==true}">
				if (parent!=null && !parent.isIFrameDialogOpen()) {
					//do nothing now
				} else {
					if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
						showProcessing();
					}
					document.forms[formName].operation.value = operation;
					document.forms[formName].parentIframeOpenFlg.value = true;
				   	document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			   	document.forms[formName].submit();
			</s:else>
		} else {
			<s:if test="%{jsonMode==true}">
				<s:if test="%{!parentIframeOpenFlg}">
				 if ('cancel<s:property value="lstForward"/>' == operation) {
						parent.closeIFrameDialog();	
						return;
					} else if ('create<s:property value="lstForward"/>' == operation || 'update<s:property value="lstForward"/>' == operation){
						saveMacPolicy(operation);
						return;
					} 
				</s:if>
			</s:if>
			if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
		   	document.forms[formName].submit();
		}
	}
}

function saveMacPolicy(operation) {
	var url = "<s:url action='macPolicy' includeParams='none' />"+ "?jsonMode=true&ignore="+new Date().getTime(); 
	document.forms["macPolicy"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.forms["macPolicy"]);
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveMacPolicy, failure : failSaveMacPolicy, timeout: 60000}, null);
}

var succSaveMacPolicy = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			parent.closeIFrameDialog();
			if(details.id != null && details.name != null){	
				if (details.parentDomID) {
					var parentDomIDs = details.parentDomID.split(',');
					if (parentDomIDs) {
						for(var i=0;i<parentDomIDs.length;i++) {
							var parentMacPolicySelect = parent.document.getElementById(parentDomIDs[i]);
							if(parentMacPolicySelect != null) {
								//dynamicAddSelect(parentMacPolicySelect,details.name, details.id);
								if (i==0) {
									hm.util.insertSelectValue(details.id,details.name,parentMacPolicySelect,false,true);	
								} else {
									hm.util.insertSelectValue(details.id,details.name,parentMacPolicySelect,false,false);	
								}
								 
							}
						}
					} 
				}
			}
		}
	}catch(e){
		alert("error")
		
		return;
	}
}

var failSaveMacPolicy = function(o){
	
}


function validate(operation) {
	if('<%=Navigation.L2_FEATURE_MAC_POLICY%>' == operation
		|| operation == 'newMac' 
		|| operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	if(operation == "editSourceMac" || operation == "editDestMac"){
		var value = operation == "editSourceMac" ? hm.util.validateListSelection(formName + "_sourceMacIds")
				: hm.util.validateListSelection(formName + "_destMacIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].macAddressId.value = value;
		}
	}
	var feChild = document.getElementById("checkAll");
	if (operation == 'addPolicyRules') {
		var source = document.forms[formName].sourceMacIds;
		if (!hasSelectedOptions(source.options)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.macPolicy.sourceMacs" /></s:param></s:text>'); 
            source.focus(); 
			return false;
		}
		var dest = document.forms[formName].destMacIds;
		if (!hasSelectedOptions(dest.options)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.macPolicy.destinationMacs" /></s:param></s:text>'); 
            dest.focus(); 
			return false;
		}
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
	if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var message = hm.util.validateName(name.value, '<s:text name="config.ipPolicy.policyName" />');
		if (message != null) {
	   		hm.util.reportFieldError(name, message);
	       	name.focus();
	       	return false;
	   	}
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
		if (options[i].selected) {
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

function changeLog(action)
{
	var actionLog = document.getElementById('actionLog');

	if(ACTION_PERMIT == action) {
		actionLog.length=0;
		actionLog.length=4;
		for(var i = 0; i < 4; i ++) {
			actionLog.options[i].value=i+1;
		}
		actionLog.options[0].text='Off';
		actionLog.options[1].text='Session Initiation';
		actionLog.options[2].text='Session Termination';
		actionLog.options[3].text='Both';
	} else {
		actionLog.length=0;
		actionLog.length=2;
		actionLog.options[0].value=1;
		actionLog.options[1].value=5;
		actionLog.options[0].text='Off';
		actionLog.options[1].text='Dropped Packets';
	}
}

function changeItemLog(index) {
	var actionLogs = document.getElementById("actionLog_"+index);
	if(ACTION_PERMIT == document.getElementById("filterAction_"+index).value) {
		if(actionLogs.length == 2) {
			actionLogs.length=0;
			actionLogs.length=4;
			for(var j = 0; j < 4; j ++) {
				actionLogs.options[j].value=j+1;
			}
			actionLogs.options[0].text='Off';
			actionLogs.options[1].text='Session Initiation';
			actionLogs.options[2].text='Session Termination';
			actionLogs.options[3].text='Both';
		}	
	} else {
		if(actionLogs.length == 4) {
			actionLogs.length=0;
			actionLogs.length=2;
			actionLogs.options[0].value=1;
			actionLogs.options[1].value=5;
			actionLogs.options[0].text='Off';
			actionLogs.options[1].text='Dropped Packets';
		}	
	}
}

function updateSourceList(data){
	if(data){
		var sourceListEl = document.getElementById(formName + "_sourceMacIds");
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(sourceListEl, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(sourceListEl, data.items);
		}
	}
}

function updateDestList(data){
	if(data){
		var destListEl = document.getElementById(formName + "_destMacIds");
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(destListEl, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(destListEl, data.items);
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="macPolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedPolicyName" />\'</td>');
		</s:else>
	</s:else>	
	
}
</script>
<div id="content"><s:form action="macPolicy">
	<s:hidden name="macAddressId" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="84%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-MAC_Policies.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.macPolicy.new"/>
								</s:if> <s:else>
									<s:text name="config.title.macPolicy.edit"/>
								</s:else>
							</td>
							<td style="padding-left:10px;">
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
											<s:if test="%{dataSource.id == null}">
												<td class="npcButton">
												<s:if test="'' == writeDisabled">
													<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
												</s:if>
												</td>
											</s:if>
											<s:else>
												<td class="npcButton">
												<s:if test="%{'' == writeDisabled}">
													<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
												</s:if>
												</td>
											</s:else>
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
		</div>
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_MAC_POLICY%>');">
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
	</table>
	</s:else>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
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
												<s:if test="%{dataSource.rules.size() > 1}">
													<td>&nbsp;</td>
												</s:if>
												<th align="left" style="padding-left: 0;" width="10"><input
													type="checkbox" id="checkAll"
													onClick="toggleCheckAllRules(this);"></th>
												<th align="left" width="80"><s:text
													name="config.ipPolicy.ruleId" /></th>
												<th align="left" width="270"><s:text
													name="config.macPolicy.sourceMacs" /></th>
												<th align="left" width="270"><s:text
													name="config.macPolicy.destinationMacs" /></th>
												<th align="left" width="80"><s:text
													name="config.macFilter.action" /></th>
												<th align="left" width="150"><s:text
													name="config.qos.logging" /></th>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
												<s:if test="%{dataSource.rules.size() > 1}">
													<td>&nbsp;</td>
												</s:if>
												<td class="listHead" width="10">&nbsp;</td>
												<td class="listHead" width="80">&nbsp;</td>
												<td class="listHead" valign="top" width="270">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:select multiple="true" size="7" name="sourceMacIds" onclick="hm.util.showtitle(this);"
																list="%{availableMacAddress}" listKey="id" listValue="value"
																cssStyle="width: 200px;" /></td>
															<td valign="top" style="padding-left:3px;">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<s:if test="%{fullMode}">
																		<a class="marginBtn" href="javascript:submitAction('newMac')"><img class="dinl"
																		src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																	</s:if>
																	<s:elseif test="%{easyMode}">
																		<a class="marginBtn" href="javascript:hm.simpleObject.newSimple(hm.simpleObject.TYPE_MAC,'','macPolicy_sourceMacIds','updateDestList',<s:property value="%{domainId}"/>)">
																			<img class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:elseif>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<s:if test="%{fullMode}">
																		<a class="marginBtn" href="javascript:submitAction('editSourceMac')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:if>
																	<s:elseif test="%{easyMode}">
																		<a class="marginBtn" href="javascript:hm.simpleObject.removeSimple(hm.simpleObject.TYPE_MAC,'macPolicy_sourceMacIds','updateDestList')"><img class="dinl"
																		src="<s:url value="/images/cancel.png" />"
																		width="16" height="16" alt="Remove" title="Remove" /></a>
																	</s:elseif>
																</s:else>
															</td>
														</tr>
													</table>
												</td>
												<td class="listHead" valign="top" width="270">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><s:select multiple="true" size="7" name="destMacIds" onclick="hm.util.showtitle(this);"
															list="%{availableMacAddress}" listKey="id" listValue="value"
															cssStyle="width: 200px;" /></td>
														<td valign="top" style="padding-left:3px;">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<s:if test="%{fullMode}">
																	<a class="marginBtn" href="javascript:submitAction('newMac')"><img class="dinl"
																	src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																</s:if>
																<s:elseif test="%{easyMode}">
																	<a class="marginBtn" href="javascript:hm.simpleObject.newSimple(hm.simpleObject.TYPE_MAC,'','macPolicy_destMacIds','updateSourceList',<s:property value="%{domainId}"/>)">
																		<img class="dinl" src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																</s:elseif>
															</s:else>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<s:if test="%{fullMode}">
																	<a class="marginBtn" href="javascript:submitAction('editDestMac')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:if>
																<s:elseif test="%{easyMode}">
																	<a class="marginBtn" href="javascript:hm.simpleObject.removeSimple(hm.simpleObject.TYPE_MAC,'macPolicy_destMacIds','updateSourceList')"><img class="dinl"
																	src="<s:url value="/images/cancel.png" />"
																	width="16" height="16" alt="Remove" title="Remove" /></a>
																</s:elseif>
															</s:else>
														</td>
													</tr>
												</table>
												</td>
												<td width="80" class="listHead" valign="top"><s:select name="filterAction"
													list="%{enumAction}" listKey="key" listValue="value" cssStyle="width: 80px;"
													onchange="changeLog(this.options[this.selectedIndex].value);"/></td>
												<td width="150" class="listHead" valign="top"><s:select name="actionLog" id="actionLog"
													list="%{enumDenyLog}" listKey="key" listValue="value" cssStyle="width: 150px;" /></td>
											</tr>
											<s:if test="%{gridCount > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount}">
													<s:iterator>
														<tr>
															<td class="list" colspan="6">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>	
										<tr>
											<s:if test="%{dataSource.rules.size() > 1}">
												<td valign="top" style="padding: 0px 10px 0 0;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td><input type="button" class="moveRow" value="Up"
																onclick="hm.util.moveRowsUp('policyTable');" /></td>
														</tr>
														<tr>
															<td><input type="button" class="moveRow" value="Down"
																onclick="hm.util.moveRowsDown('policyTable');" /></td>
														</tr>
														<s:if test="%{dataSource.rules.size() > 15}">
															<s:generator separator="," val="%{' '}" count="%{dataSource.rules.size()-2}">
																<s:iterator>
																	<tr>
																		<td>&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
															<tr>
																<td><input type="button" class="moveRow" value="Up"
																	onclick="hm.util.moveRowsUp('policyTable');" /></td>
															</tr>
															<tr>
																<td><input type="button" class="moveRow" value="Down"
																	onclick="hm.util.moveRowsDown('policyTable');" /></td>
															</tr>
														</s:if>
													</table>
												</td>
											</s:if>
											<td valign="top" colspan="6">
												<table cellspacing="0" cellpadding="0" border="0"
													class="embedded" id="policyTable">
													<s:iterator value="%{dataSource.rules}" status="status">
														<tr>
															<td class="listCheck" width="10"><s:checkbox name="ruleIndices"
																fieldValue="%{#status.index}" /></td>
															<td class="list" width="80"><s:property value="ruleId" /></td>
															<td class="list" width="270">
																<s:if test="%{sourceMac == null}">
																	<s:text name="config.ipPolicy.any" />
																</s:if>
																<s:else>
																	<s:property value="sourceMac.macOrOuiName" />
																</s:else>
															</td>
															<td class="list" width="270">
																<s:if test="%{destinationMac == null}">
																	<s:text name="config.ipPolicy.any" />
																</s:if>
																<s:else>
																	<s:property value="destinationMac.macOrOuiName" />
																</s:else>
															</td>
															<td class="list" width="80"><s:select name="filterActions" id="filterAction_%{#status.index}"
																value="%{filterAction}" list="%{enumAction}" listKey="key" cssStyle="width: 80px;"
																listValue="value" onchange="changeItemLog(%{#status.index});"/>
																<s:hidden name="ordering" value="%{#status.index}" /></td>
															<td class="list" width="150">
															<s:if test="%{filterAction == 1}">
																<s:select name="actionLogs" id="actionLog_%{#status.index}"
																value="%{actionLog}" list="%{enumPermitLog}" listKey="key"
																listValue="value" cssStyle="width: 150px;"/>
															</s:if>
															<s:else>
																<s:select name="actionLogs" id="actionLog_%{#status.index}"
																value="%{actionLog}" list="%{enumDenyLog}" listKey="key"
																listValue="value" cssStyle="width: 150px;"/>
															</s:else>
															</td>
														</tr>
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
