<%@page import="com.ah.bo.network.RoutingPolicyRule"%>
<%@page import="com.ah.bo.network.RoutingPolicy"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />"></script>

<script>
var formName = 'routingPolicy';
var POLICYRULE_SPLIT = <%=RoutingPolicy.POLICYRULE_SPLIT%>;
var POLICYRULE_ALL = <%=RoutingPolicy.POLICYRULE_ALL%>;
var POLICYRULE_CUSTOM = <%=RoutingPolicy.POLICYRULE_CUSTOM%>;
var FORWARDACTION_EXCEPTION = <%=RoutingPolicyRule.FORWARDACTION_EXCEPTION%>;
var FORWARDACTION_DROP = <%=RoutingPolicyRule.FORWARDACTION_DROP%>;
var ROUTING_POLICY_RULE_ANY = <%=RoutingPolicyRule.ROUTING_POLICY_RULE_ANY%>;
var ROUTING_POLICY_RULE_ANYGUEST = <%=RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST%>;

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
		top.changeIFrameDialog(880, 600);
	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
	    document.forms[formName].operation.value = operation;
	    argsEnableBeforeSubmit();
	    <s:if test="%{jsonMode}">
	   		if (operation == 'editDomObj'
				|| operation == 'editDomObjForAll'
				|| operation == 'newDomObj'
				|| operation == 'newDomObjForAll') {
	    		document.forms[formName].parentIframeOpenFlg.value = true;
	   		}
	    </s:if>
	    document.forms[formName].submit();
	    argsDisableBeforeSubmit();
	}
}

function argsEnableBeforeSubmit() {
	var anyIdx = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANY);
	if(Get("sourceUserProfilePri"+anyIdx)) {
    	Get("sourceUserProfilePri"+anyIdx).disabled = false;
    }
	if (Get("ipTrackForCheckPri"+anyIdx)) {
	    Get("ipTrackForCheckPri"+anyIdx).disabled = false;
	}
	anyIdx = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANYGUEST);
	if(Get("sourceUserProfilePri"+anyIdx)) {
    	Get("sourceUserProfilePri"+anyIdx).disabled = false;
    }
	if (Get("ipTrackForCheckPri"+anyIdx)) {
	    Get("ipTrackForCheckPri"+anyIdx).disabled = false;
	}
	if (Get("forwardActionTypePri"+anyIdx)) {
	    Get("forwardActionTypePri"+anyIdx).disabled = false;
	}
	if (Get("forwardActionTypeSec"+anyIdx)) {
	    Get("forwardActionTypeSec"+anyIdx).disabled = false;
	}
	
    $("select[id^=sourceUserProfileSec]").attr("disabled",false);
    $("select[id^=ipTrackForCheckSec]").attr("disabled",false);
    $("select[id^=sourceUserProfileSec]").each(function(){
    	var priElTmp = this.id.replace(/Sec/, "Pri");
    	$(this).empty();
    	$(this).append($("#"+priElTmp+" option").clone());
    	if (Get(priElTmp)) {
    		$(this).attr({"value": Get(priElTmp).value});
    	}
    });
	if (Get("ipTrackForCheckPri"+anyIdx)) {
    	Get("ipTrackForCheckPri"+anyIdx).disabled = false;
	}
}

function argsDisableBeforeSubmit() {
	var anyIdx = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANY);
	if (Get("sourceUserProfilePri"+anyIdx)) {
    	Get("sourceUserProfilePri"+anyIdx).disabled = true;
    }
    if (Get("ipTrackForCheckPri"+anyIdx)) {
    	Get("ipTrackForCheckPri"+anyIdx).disabled = true;
    }
    anyIdx = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANYGUEST);
	if (Get("sourceUserProfilePri"+anyIdx)) {
    	Get("sourceUserProfilePri"+anyIdx).disabled = true;
    }
    if (Get("ipTrackForCheckPri"+anyIdx)) {
    	Get("ipTrackForCheckPri"+anyIdx).disabled = true;
    }
    if (Get("forwardActionTypePri"+anyIdx)) {
	    Get("forwardActionTypePri"+anyIdx).disabled = true;
	}
    if (Get("forwardActionTypeSec"+anyIdx)) {
	    Get("forwardActionTypeSec"+anyIdx).disabled = true;
	}
    
    $("select[id^=ipTrackForCheckSec]").attr("disabled",true);
    $("select[id^=sourceUserProfileSec]").attr("disabled",true);
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>' 
			|| operation == 'cancel'
			|| operation == 'newDomObj'
			|| operation == 'newDomObjForAll') {
		return true;
	}
	
	if (operation === 'editDomObj') {
		var value = hm.util.validateListSelection(formName + "_domObjId");
    	if(value < 0){
			return false;
		} else {
			return true;
		}
	}
	if (operation === 'editDomObjForAll') {
		var value = hm.util.validateListSelection(formName + "_domObjIdForAll");
    	if(value < 0){
			return false;
		} else {
			return true;
		}
	}
	var policyNameTmp = Get(formName + "_dataSource_policyName");
	var message = hm.util.validateName(policyNameTmp.value, '<s:text name="config.routing.policy.name" />');
	if (message != null) {
		hm.util.reportFieldError(policyNameTmp, message);
		return false;
	}
	
	var chkIpCheckTmp = Get(formName + "_dataSource_enableIpTrackForCheck");
	var NULL_SELECT_VALUE = -1;
	if (chkIpCheckTmp.checked) {
		var ipTrackSelectTmp = Get(formName + "_trackIpId");
		if (ipTrackSelectTmp && ipTrackSelectTmp.value == NULL_SELECT_VALUE) {
			hm.util.reportFieldError(ipTrackSelectTmp, '<s:text name="info.selectObject" />');
			return false;
		}
	}

	var chkDomObjForAllTmp = Get(formName + "_dataSource_enableDomainObjectForDesList");
	if (chkDomObjForAllTmp.checked) {
		if (Get(formName + "_dataSource_policyRuleType2").checked) {
			var upDomObjForAllSelectTmp = Get(formName + "_domObjIdForAll");
			if (upDomObjForAllSelectTmp && upDomObjForAllSelectTmp.value == NULL_SELECT_VALUE) {
				hm.util.reportFieldError(upDomObjForAllSelectTmp, '<s:text name="info.selectObject" />');
				return false;
			}
		}
	}
	
	if (Get(formName + "_dataSource_policyRuleType3").checked) {
		var upDomObjForAllSelectTmp = Get(formName + "_domObjId");
		if(destinationListShowFlag  && upDomObjForAllSelectTmp.value == NULL_SELECT_VALUE){
			hm.util.reportFieldError(upDomObjForAllSelectTmp, '<s:text name="info.selectObject" />');
			return false;
		}
	}
	
	if(Get(formName + "_trackIpId").value != -1){
		if (Get(formName + "_dataSource_policyRuleType2").checked){
			if(Get("allAnyInterfacePri").value == Get("allAnyInterfaceSec").value){
				hm.util.reportFieldError(Get("errorSameInterfaceAll"), '<s:text name="message.interface.samenone" />');
				return false;
			}
		}else if(Get(formName + "_dataSource_policyRuleType1").checked){
			if(Get("splitAnyGuestInterfacePri").value == Get("splitAnyGuestInterfaceSec").value){
				hm.util.reportFieldError(Get("errorSameInterfaceSplit"), '<s:text name="message.interface.samenone" />');
				return false;
			}
			if(Get("splitAnyInterfacePri").value == Get("splitAnyInterfaceSec").value){
				hm.util.reportFieldError(Get(formName+"_dataSource_policyName"), '<s:text name="message.interface.samenone" />');
				return false;
			}
		}/* else{
			var nullInterface = false;
			$("table#customRuleTable select[id^=interfaceTypePri_]").each(function(){
				if(nullInterface == true){
					return;
				}
				var index = this.id;
				if (index.indexOf("_") > 0) {
					index = index.substr(index.indexOf("_"));
				}
				var sec = $("table#customRuleTable select[id=interfaceTypeSec"+index + "]");
				if(this.value == sec.attr("value")){
					nullInterface = true;
				}
			});
			if(nullInterface == true){
				hm.util.reportFieldError(Get("checkAll"), '<s:text name="message.interface.samenone" />');
				return false;
			}
		} */
	}
	
	
	var dealtUps = new Array();
	var blnDupUps = false;
	$("select[id^=sourceUserProfilePri_]").each(function(){
		if (blnDupUps == true || this.id === 'sourceUserProfilePri_Template') return;
		if (dealtUps.contains(this.value)) {
			blnDupUps = true;
			return;
		}
		dealtUps.push(this.value);
	});
	if (blnDupUps == true) {
		hm.util.reportFieldError(Get("checkAll"), '<s:text name="message.dup.userprofile.not.allowed" />');
		return false;
	}
	
	return true;
}

function removeRoutingProileRules() {
	var rmRowCount = 0;
	$("tr[id^=routingPolicyPriRow_] td input[name=ruleIndices]:checked").each(function() {
		rmRowCount++;
		var $pEl = $(this).parent().parent().remove();
		var pSecElId = $pEl.attr("id").replace(/PriRow_/, "SecRow_");
		$("#"+pSecElId).remove();
	});
	if (rmRowCount < 1) {
		warnDialog.cfg.setProperty('text', '<s:text name="info.selectObject" />');
		warnDialog.show();
	}
}

function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if (validate(operation)) {
		var url =  "<s:url action='routingPolicy' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["routingPolicy"].operation.value = operation;
		argsEnableBeforeSubmit();
		YAHOO.util.Connect.setForm(document.getElementById("routingPolicy"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveRoutingPolicy, failure : resultDoNothing, timeout: 60000}, null);
		argsDisableBeforeSubmit();
	}
}
var resultDoNothing = function(o) {
//	alert("failed.");
};

var succSaveRoutingPolicy = function (o) {
	eval("var details = " + o.responseText);
	if (details.t) {
		if (details.n){
			hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), true, true);
		}
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="routingPolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>

<div id="content"><s:form action="routingPolicy" name="routingPolicy" id="routingPolicy">
	<s:if test="%{jsonMode==true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg" />
	</s:if>
	
	<s:if test="%{jsonMode==false}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
									class="button"
									onClick="submitAction('create<s:property value="lstForward"/>');"
									<s:property value="writeDisabled" />></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
									class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
									<s:property value="updateDisabled" />></td>
							</s:else>
							<td><input type="button" name="ignore" value="Cancel"
								class="button"
								onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:if>
	<s:else>
	<div id="vlanTitleDiv" class="topFixedTitle">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Tracking.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.routingPolicy"/>
								</s:if>
								<s:else>
									<s:text name="config.title.routingPolicy.edit"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<s:if test="%{!parentIframeOpenFlg}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<td class="npcButton">
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:else>
						</tr>
					</table>
					</s:if>
					<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
					</s:else>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</div>
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
			<td height="5"></td>
		</tr>
		<tr>
			<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="700">
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="4"></td>
								</tr>
								<tr>
									<td class="labelT1" width="80"><label><s:text
										name="config.routing.policy.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.policyName"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="24" maxlength="%{policyNameLength}" disabled="%{disabledName}"/>
										&nbsp;<s:text name="config.routing.policy.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.routing.policy.description" /></td>
									<td><s:textfield name="dataSource.description" size="48"
										maxlength="%{descriptionLength}" /> <s:text
										name="config.routing.policy.description.range" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding:0 10px 0 10px">
							<fieldset><legend><s:text name="config.routing.policy.rules.title" /></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td width="230px"><s:radio onclick="this.blur();" onchange="policyRuleTypeChanged(this);" label="Gender"	name="dataSource.policyRuleType" list="%{splitTunnel}" listKey="key" listValue="value"/></td>
												    <td width="230px"><s:radio onclick="this.blur();" onchange="policyRuleTypeChanged(this);" label="Gender"	name="dataSource.policyRuleType" list="%{tunnelAll}" listKey="key" listValue="value"/></td>
												    <td><s:radio onclick="this.blur();" onchange="policyRuleTypeChanged(this);" label="Gender"	name="dataSource.policyRuleType" list="%{custom}" listKey="key" listValue="value"/></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr id="customTypeNote" style="display:<s:property value="customTypeTrStyle"/>">
													<td  style="padding-top:10px;" class="noteInfo" height="10px" >
														<s:text name="config.routing.policy.rules.type.custom.note"/><br>
													</td>
												</tr>
												<tr id="allTypeNote" style="display:<s:property value="allTypeTrStyle"/>">
													<td  style="padding-top:10px;" class="noteInfo" height="10px" >
														<s:text name="config.routing.policy.rules.type.all.note"/><br>
													</td>
												</tr>
												<tr id="splitTypeNote" style="display:<s:property value="splitTypeTrStyle"/>">
													<td  style="padding-top:10px;" class="noteInfo" height="10px" >
														<s:text name="config.routing.policy.rules.type.split.note"/><br>
													</td>
												</tr>
												<tr>
													<td>
											    		<table cellspacing="0" cellpadding="0" border="0">
											    			<tr>
											    				<td>
											    					<s:checkbox name="dataSource.enableIpTrackForCheck" onclick="enableIpTrack(this.checked)"/>
											    				</td>
											    				<td height="30px" style="padding-right:25px;">
														    	    &nbsp;<label for="routingPolicy_dataSource_enableIpTrackForCheck"><s:text name="config.routing.policy.rules.type.custom.iptrack" /></label>
														    	</td>
														    	<td id="trackIpId" style="display:<s:property value="enableIpTrackStyle"/>">
																	<s:select name="trackIpId" value="%{trackIpId}" onchange="changeTrackIp(this);"
																		list="trackIpList" listKey="id" listValue="value" cssStyle="width: 180px;"></s:select>
														    	</td>
											    			</tr>
											    			<tr id="destinationListForAll" style="display: <s:property value="%{destinationListStyleForAll}"/>">
																<td>
											    					<s:checkbox name="dataSource.enableDomainObjectForDesList" onclick="enableDestinationList(this.checked)"/>
											    				</td>
											    				<td height="30px" style="padding-right:25px;">
														    	    &nbsp;<label for="routingPolicy_dataSource_enableDomainObjectForDesList"><s:text name="config.routing.policy.rules.exception.deslist" /></label>
														    	</td>
																<td id="domObjIdForAll" style="display:<s:property value="EnableDomainObjectForDesListStyle"/>">
																	<s:select name="domObjIdForAll"  value="%{domObjIdForAll}" 
																		onchange="changeDestinationList(this);" 
																		list="availableDomainObjects" listKey="id" listValue="value" cssStyle="width: 180px;"></s:select>
																	<a class="marginBtn" href="javascript:submitAction('newDomObjForAll')"><img class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
																	<a class="marginBtn" href="javascript:submitAction('editDomObjForAll')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
														    	</td>
															</tr>
											    		</table>
											    	</td>
												</tr>
												<tr id="destinationListForAllNote" style="display: <s:property value="%{destinationListStyleForAll}"/>;">
													<td class="noteInfo" >
														<s:text name="config.routing.policy.rules.exception.deslist.note"/>
													</td>
												</tr> 
											</table>
										</td>
									</tr>
									<tr id="customTypeTr" style="display:<s:property value="customTypeTrStyle"/>">
										<td>
											<div>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr style="padding-top:5px;">
														<td colspan="5" style="padding-bottom: 2px;" >
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="New"
																		class="button" onClick="createRoutingPolicyRule();"></td>
																	<td><input type="button" name="ignore" value="Remove"
																		class="button" onClick="removeRoutingProileRules();"></td>
																	<td></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td width="100%">
															<fieldset>
																<table cellspacing="0" cellpadding="0" border="0">
																    <tr><td  height="5px"  style="padding-bottom: 2px;"></td></tr>
																	<tr>
																		<th align="left" width="10" style="padding-right:0px"><input
																			type="checkbox" id="checkAll"
																			onClick="toggleCheckAllRules(this);"></th>
																		<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.userprofile" /></th>
																		<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.trackIp" /></th>
																		<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.interface" /></th>
																		<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.action" /></th>
																	</tr>
																	<tr>
																		<td colspan="5" width="310px">
																			<table  width="100%" id="customRuleTable">
																				<s:iterator value="%{customRuleList}" status="status">
																				<tr id="routingPolicyPriRow_<s:property value='%{#status.index+1}' />">
																					<td class="listCheck" width="10px"> 
																						<s:if test="%{sourceUserProfile == null}">
																							&nbsp;
																						</s:if>
																						<s:else>
																							<s:checkbox name="ruleIndices" 
																								fieldValue="%{#status.index}" />
																						</s:else>
																						
																					</td>
																					<td class="list"  width="100px">
																						<s:if test="%{sourceUserProfile == null && ruleType == -1}">
																							<s:select id="sourceUserProfilePri_%{#status.index+1}" name="sourceUserProfilePri" disabled="true"
																								list="%{enumAny}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																						</s:if>
																						<s:elseif test="%{sourceUserProfile == null && ruleType == -2}">
																							<s:select id="sourceUserProfilePri_%{#status.index+1}" name="sourceUserProfilePri" disabled="true"
																								list="%{enumAnyGuest}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																						</s:elseif>
																						<s:else>
																							<s:select id="sourceUserProfilePri_%{#status.index+1}" name="sourceUserProfilePri"  value="%{sourceUserProfile.id}"
																									list="%{sourceUserProfileList}" listKey="id" listValue="value" 
																									cssStyle="width: 155px;"></s:select>
																						</s:else>
																					</td>
																					<td class="list" width="100px">
																						<s:if test="%{sourceUserProfile == null}">
																							<s:select  id="ipTrackForCheckPri_%{#status.index+1}" name="ipTrackForCheckPri" disabled="true"  
																							value="%{ipTrackReachablePri.id}"
																							list="%{ipTrackForCheckList}" listKey="id" listValue="value" 
																							cssStyle="width: 155px;"></s:select>
																						</s:if>
																						<s:else>
																							<s:select  id="ipTrackForCheckPri_%{#status.index+1}" name="ipTrackForCheckPri"  value="%{ipTrackReachablePri.id}"
																							list="%{ipTrackForCheckList}" listKey="id" listValue="value" 
																							cssStyle="width: 155px;"></s:select>
																						</s:else>
																					</td>
																					<td class="list" width="100px">
																						<s:select id="interfaceTypePri_%{#status.index+1}" name="interfaceTypePri"  value="%{interfaceTypePri}"
																								list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																					</td>
																					<td class="list">
																						<s:if test="%{sourceUserProfile == null && ruleType == -2}">
																							<s:select id="forwardActionTypePri_%{#status.index+1}"  name="forwardActionTypePri"  value="%{forwardActionTypePri}"
																									list="%{enumForwardActionList}" listKey="key" listValue="value" disabled="true"
																									cssStyle="width: 155px; display:%{forwardActionTypePriStyle};"></s:select>
																						</s:if>
																						<s:else>
																							<s:select id="forwardActionTypePri_%{#status.index+1}"  name="forwardActionTypePri"  value="%{forwardActionTypePri}"
																									list="%{enumForwardActionList}" listKey="key" listValue="value" 
																									cssStyle="width: 155px; display:%{forwardActionTypePriStyle};"></s:select>
																						</s:else>
																						
																					</td>
																				</tr>
																				<s:if test="%{ipTrackReachablePri != null}">
																				<tr id="routingPolicySecRow_<s:property value='%{#status.index+1}' />">
																					<td width="10px"></td>
																					<td class="list">
																						<span style="display:none;">
																						<s:if test="%{sourceUserProfile == null && ruleType == -1}">
																							<s:select id="sourceUserProfileSec_%{#status.index+1}" name="sourceUserProfileSec" disabled="true"
																								list="%{enumAny}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																						</s:if>
																						<s:elseif test="%{sourceUserProfile == null && ruleType == -2}">
																							<s:select id="sourceUserProfileSec_%{#status.index+1}" name="sourceUserProfileSec" disabled="true"
																								list="%{enumAnyGuest}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																						</s:elseif>
																						<s:else>
																							<s:select  id="sourceUserProfileSec_%{#status.index+1}"  name="sourceUserProfileSec"
																									list="%{sourceUserProfileList}" listKey="id" listValue="value" 
																									cssStyle="width: 155px;"></s:select>
																						</s:else>
																						</span>
																					</td>
																					<td class="list">
																						<s:select id="ipTrackForCheckSec_%{#status.index+1}" name="ipTrackForCheckSec"  value="%{ipTrackReachablePri.id}" disabled="true"
																								list="%{ipTrackForCheckList}" listKey="id" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																					</td>
																					<td class="list">
																						<s:select id="interfaceTypeSec_%{#status.index+1}"  name="interfaceTypeSec"  
																								list="%{enumInterfaceList}" listKey="key" listValue="value" 
																								cssStyle="width: 155px;"></s:select>
																					</td>
																					<td class="list">
																						<s:if test="%{sourceUserProfile == null && ruleType == -2}">
																							<s:select id="forwardActionTypeSec_%{#status.index+1}" name="forwardActionTypeSec"  disabled="true"
																								list="%{enumForwardActionList}" listKey="key" listValue="value" 
																								cssStyle="width: 155px; display:%{forwardActionTypeSecStyle};"></s:select>
																						</s:if>
																						<s:else>
																							<s:select id="forwardActionTypeSec_%{#status.index+1}" name="forwardActionTypeSec"  
																								list="%{enumForwardActionList}" listKey="key" listValue="value" 
																								cssStyle="width: 155px; display:%{forwardActionTypeSecStyle};"></s:select>
																						</s:else>
																					</td>
																				</tr>
																				</s:if>
																				</s:iterator>
																				<tr id="lastEmptyRow"><td height="5px"/></tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr id="destinationList" style="display: <s:property value="%{destinationListStyle}"/>">
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td colspan="4" style="padding-top:10px;" class="noteInfo" height="10px" >
																		<s:text name="config.routing.policy.rules.exception.deslist.note"/><br>
																	</td>
																</tr>
																<tr>
																	<td colspan="4" style="padding-top:10px;padding-bottom:10px">
															    		<label><s:text name="config.routing.policy.rules.exception.deslist" /></label>
																		<s:select name="domObjId" value="%{domObjId}" list="availableDomainObjects" listKey="id" listValue="value" cssStyle="width: 210px;"></s:select>
																		<a class="marginBtn" href="javascript:submitAction('newDomObj')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																		<a class="marginBtn" href="javascript:submitAction('editDomObj')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
															    	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr id="splitTypeTr" style="display:<s:property value="splitTypeTrStyle"/>">
										<td style="padding-top:2px;">
											<fieldset>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
															<table  width="100%" id="splitRuleTable">
																<tr id="errorSameInterfaceSplit"><td height="5px"  style="padding-bottom: 2px;"></td></tr>
																<tr>
																	<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.userprofile" /></th>
																	<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.trackIp" /></th>
																	<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.interface" /></th>
																	<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.action" /></th>
																</tr>
																<tr>
																	<td class="list"  width="100px">
																		<s:select  name="anyGuest" disabled="true"
																				list="%{enumAnyGuest}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select  id="anyGuestSplitTrackIpPriId" value="%{trackIpId}" disabled="true"
																			list="%{trackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select id="splitAnyGuestInterfacePri"  name="splitAnyGuestInterfacePri"  value="%{splitAnyGuestInterfacePri}"
																				list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="splitAnyGuestForwardActionPriSpan" style="display: <s:property value="splitAnyGuestForwardActionPriStyle"/>">
																			<s:select id="splitAnyGuestForwardActionPri" name="splitAnyGuestForwardActionPri"  disabled="true"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" value="2"
																							cssStyle="width: 155px;"></s:select></span>
																	</td>
																</tr>
																<tr id="splitAnyGuestSecTr" style="display:<s:property value="%{ruleSecStyle}"/>">
																	<td class="list"  width="100px">&nbsp;</td>
																	<td class="list" width="100px">
																		<s:select  id="anyGuestSplitTrackIpSecId"  value="%{trackIpId}" disabled="true"
																			list="%{trackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select id="splitAnyGuestInterfaceSec" name="splitAnyGuestInterfaceSec"  value="%{splitAnyGuestInterfaceSec}"
																				list="%{enumInterfaceList}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="splitAnyGuestForwardActionSecSpan" style="display: <s:property value="splitAnyGuestForwardActionSecStyle"/>">
																			<s:select id="splitAnyGuestForwardActionSec" name="splitAnyGuestForwardActionSec"  disabled="true"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" value="2"
																							cssStyle="width: 155px;"></s:select></span>
																	</td>
																</tr>
																<tr>
																	<td class="list"  width="100px">
																		<s:select  name="any" disabled="true"
																				list="%{enumAny}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select  id="anySplitTrackIpPriId" value="%{trackIpId}" disabled="true"
																			list="%{trackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select id="splitAnyInterfacePri"  name="splitAnyInterfacePri"  value="%{splitAnyInterfacePri}"
																				list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="splitAnyForwardActionPriSpan" style="display: <s:property value="splitAnyForwardActionPriStyle"/>">
																			<s:select id="splitAnyForwardActionPri" name="splitAnyForwardActionPri"  value="3" disabled="true"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" 
																							cssStyle="width: 155px;"></s:select></span>
																	</td>
																</tr>
																<tr id="splitAnySecTr" style="display:<s:property value="%{ruleSecStyle}"/>">
																	<td class="list"  width="100px">&nbsp;</td>
																	<td class="list" width="100px">
																		<s:select  id="anySplitTrackIpSecId"  value="%{trackIpId}" disabled="true"
																			list="%{trackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select id="splitAnyInterfaceSec" name="splitAnyInterfaceSec"  value="%{splitAnyInterfaceSec}"
																				list="%{enumInterfaceList}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="splitAnyForwardActionSecSpan" style="display: <s:property value="splitAnyForwardActionSecStyle"/>">
																			<s:select id="splitAnyForwardActionSec" name="splitAnyForwardActionSec"  value="3" disabled="true"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" 
																							cssStyle="width: 155px;"></s:select></span>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
									<tr id="allTypeTr" style="display:<s:property value="allTypeTrStyle"/>">
										<td style="padding-top:2px;">
											<fieldset>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
															<table  width="100%" id="allRuleTable">
																<tr id="errorSameInterfaceAll" ><td height="5px"  style="padding-bottom: 2px;"></td></tr>
																<tr>
																	<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.userprofile" /></th>
																	<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.trackIp" /></th>
																	<th align="left" style="width:150px;padding-left:6px"><s:text name="config.routing.policy.rules.table.head.interface" /></th>
																	<th align="left" width="150px" style="padding-left:0px"><s:text name="config.routing.policy.rules.table.head.action" /></th>
																</tr>
																<tr>
																	<td class="list"  width="100px">
																		<s:select  name="any" disabled="true"
																				list="%{enumAny}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select  id="anyAllTrackIpPriId" value="%{trackIpId}" disabled="true"
																			list="%{TrackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select  id="allAnyInterfacePri" name="allAnyInterfacePri"  value="%{allAnyInterfacePri}"
																				list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="allAnyForwardActionPriSpan" style="display:<s:property value="%{allAnyForwardActionPriStyle}"/>">
																			<s:select id="allAnyForwardActionPri"  disabled="true" value="%{allAnyForwardAction}"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" 
																							cssStyle="width: 155px; "></s:select></span>
																	</td>
																</tr>
																<tr id="allAnySecTr" style="display:<s:property value="%{ruleSecStyle}"/>">
																	<td class="list"  width="100px">&nbsp;</td>
																	<td class="list" width="100px">
																		<s:select  id="allAnyTrackIpSecId"  value="%{trackIpId}" disabled="true"
																			list="%{TrackIpListNone}" listKey="id" listValue="value" 
																			cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list" width="100px">
																		<s:select  id="allAnyInterfaceSec" name="allAnyInterfaceSec" value="%{allAnyInterfaceSec}"
																				list="%{enumInterfaceList}" listKey="key" listValue="value" 
																				cssStyle="width: 155px;"></s:select>
																	</td>
																	<td class="list">
																		<span id="allAnyForwardActionSecSpan" style="display:<s:property value="%{allAnyForwardActionSecStyle}"/>">
																			<s:select  id="allAnyForwardActionSec"  value="%{allAnyForwardAction}" disabled="true"
																							list="%{enumForwardActionList}" listKey="key" listValue="value" 
																							cssStyle="width: 155px;"></s:select></span>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</fieldset>
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
<div  style="display:none" >
	<table>
		<tr id="trackIpEmptyPri_Template">
			<td  width="10px">&nbsp;</td>
			<td class="list" width="100px">
				<s:select  id="sourceUserProfilePri_Template" name="sourceUserProfilePri" disabled="true"
						list="%{enumAny}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list" width="100px">
				<s:select  id="ipTrackForCheckPri_Template" name="ipTrackForCheckPri" disabled="true"
						list="%{enumNone}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list" width="100px">
				<s:select  id="interfaceTypePri_Template"  name="interfaceTypePri"
						list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="forwardActionTypePri_Template"  name="forwardActionTypePri" value="3"
						list="%{enumForwardActionList}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
		</tr>
		<tr id="trackIpEmptyAnyGuestPri_Template">
			<td  width="10px">&nbsp;</td>
			<td class="list" width="100px">
				<s:select  id="sourceUserProfilePri_Template" name="sourceUserProfilePri" disabled="true"
						list="%{enumAnyGuest}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list" width="100px">
				<s:select  id="ipTrackForCheckPri_Template" name="ipTrackForCheckPri" disabled="true"
						list="%{enumNone}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list" width="100px">
				<s:select  id="interfaceTypePri_Template"  name="interfaceTypePri"
						list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="forwardActionTypePri_Template"  name="forwardActionTypePri" 
						value = "2" disabled="true" 
						list="%{enumForwardActionList}"  listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
		</tr>
		<tr id="trackIpExistPri_Template">
			<td width="10px"> 
					<s:checkbox name="ruleIndices" 
						fieldValue="%{#status.index}" />
					
			</td>
			<td class="list">
				<s:select  id="sourceUserProfilePri_Template"  name="sourceUserProfilePri"
						list="%{sourceUserProfileList}" listKey="id" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="ipTrackForCheckPri_Template"  name="ipTrackForCheckPri"
						list="%{enumNone}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="interfaceTypePri_Template"  name="interfaceTypePri"
						list="%{enumInterfaceListPri}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="forwardActionTypePri_Template"  name="forwardActionTypePri" value="3"
						list="%{enumForwardActionList}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
		</tr>
		 <tr id="trackIpExistSec_Template">
			<td width="10px"></td>
			<td class="list">
				<span style="display:none">
				<s:select  id="sourceUserProfileSec_Template"  name="sourceUserProfileSec"
						list="%{sourceUserProfileList}" listKey="id" listValue="value" 
						cssStyle="width: 155px;"></s:select>
				</span>
			</td>
			<td class="list">
				<s:select  id="ipTrackForCheckSec_Template" name="ipTrackForCheckSec" disabled="true"
						list="%{enumNone}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="interfaceTypeSec_Template"  name="interfaceTypeSec"
						list="%{enumInterfaceListUSB}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
			<td class="list">
				<s:select  id="forwardActionTypeSec_Template"  name="forwardActionTypeSec" value="3"
						list="%{enumForwardActionList}" listKey="key" listValue="value" 
						cssStyle="width: 155px;"></s:select>
			</td>
		</tr> 
	</table>
</div>
<script>

	 $(document).ready(function(){
		$("table#customRuleTable select[id^=ipTrackForCheckPri]").change(function(){
		//$("table#customRuleTable select[id^=ipTrackForCheckPri]").live('change',function(){
		    var index = this.id.substring(this.id.indexOf("_")+1,this.id.length);
		 	if(this.value != 0 ){
		 		if (Get("routingPolicySecRow_"+index)) {
		 			return;
		 		}
		 		var existRowSec = Get("trackIpExistSec_Template");
		 		var newRowSec = existRowSec.cloneNode(true);
		 		newRowSec.id="routingPolicySecRow_"+index;
		 		changeSpecificRowDom(newRowSec,index);
		 		var row = Get("routingPolicyPriRow_"+index);
		 		YAHOO.util.Dom.insertAfter(newRowSec,row);
		 		$("table#customRuleTable select[id=ipTrackForCheckSec_"+index + "] > option[value=0]").attr({value:this.value}).text(this.options[this.selectedIndex].text);
		 	
		 	}else{
		 		var row = Get("routingPolicySecRow_"+index);
		 		if(row){
		 			Get("customRuleTable").deleteRow(row.rowIndex);
		 		}
		 	}
	    })
		}
	); 
	
	(function(){
		//bind change event for pri track ip
		 $("table#customRuleTable select[id^=ipTrackForCheckPri]").live('change', function() {
		    var index = this.id.substring(this.id.indexOf("_")+1,this.id.length);
		 	if(this.value != 0 ){
		 		if (Get("routingPolicySecRow_"+index)) {
		 			return;
		 		}
		 		var existRowSec = Get("trackIpExistSec_Template");
		 		var newRowSec = existRowSec.cloneNode(true);
		 		newRowSec.id="routingPolicySecRow_"+index;
		 		changeSpecificRowDom(newRowSec,index);
		 		var row = Get("routingPolicyPriRow_"+index);
		 		YAHOO.util.Dom.insertAfter(newRowSec,row);
		 		//$("table#customRuleTable select[id=ipTrackForCheckSec_"+index + "] > option[value=0]").attr({value:this.value,text:this.options[this.selectedIndex].text});
		 		$("table#customRuleTable select[id=ipTrackForCheckSec_"+index + "] > option[value=0]").attr({value:this.value}).text(this.options[this.selectedIndex].text);
		 	}else{
		 		var row = Get("routingPolicySecRow_"+index);
		 		if(row){
		 			Get("customRuleTable").deleteRow(row.rowIndex);
		 		}
		 	}
		}); 
		
		//Control the show of forward action by interface type in Custom
		$("table#customRuleTable select[id^=interfaceTypePri]").live('change', function() {
			var index = this.id.substring(this.id.indexOf("_")+1,this.id.length);
			if(this.value == 0){
				$("table#customRuleTable select[id=forwardActionTypePri_"+index + "]").hide();
			}else{
				$("table#customRuleTable select[id=forwardActionTypePri_"+index + "]").show();
			}
		});
		
		$("table#customRuleTable select[id^=interfaceTypeSec]").live('change', function() {
			var index = this.id.substring(this.id.indexOf("_")+1,this.id.length);
			if(this.value == 0){
				$("table#customRuleTable select[id=forwardActionTypeSec_"+index + "]").hide();
			}else{
				$("table#customRuleTable select[id=forwardActionTypeSec_"+index + "]").show();
			}
		});
		
		//Control the show of forward action by interface type in Tunnel All
		$("table#allRuleTable select[id=allAnyInterfacePri]").live('change', function() {
			if(this.value == 0){
				$("table#allRuleTable span[id=allAnyForwardActionPriSpan]").hide();
			}else{
				$("table#allRuleTable span[id=allAnyForwardActionPriSpan]").show();
			}
		});
		
		$("table#allRuleTable select[id=allAnyInterfaceSec]").live('change', function() {
			if(this.value == 0){
				$("table#allRuleTable span[id=allAnyForwardActionSecSpan]").hide();
			}else{
				$("table#allRuleTable span[id=allAnyForwardActionSecSpan]").show();
			}
		});
		
		//Control the show of forward action by interface type in Split Tunnel
		$("table#splitRuleTable select[id=splitAnyInterfacePri]").live('change', function() {
			if(this.value == 0){
				$("table#splitRuleTable span[id=splitAnyForwardActionPriSpan]").hide();
			}else{
				$("table#splitRuleTable span[id=splitAnyForwardActionPriSpan]").show();
			}
		});
		
		$("table#splitRuleTable select[id=splitAnyInterfaceSec]").live('change', function() {
			if(this.value == 0){
				$("table#splitRuleTable span[id=splitAnyForwardActionSecSpan]").hide();
			}else{
				$("table#splitRuleTable span[id=splitAnyForwardActionSecSpan]").show();
			}
		});
		
		$("table#splitRuleTable select[id=splitAnyGuestInterfacePri]").live('change', function() {
			if(this.value == 0){
				$("table#splitRuleTable span[id=splitAnyGuestForwardActionPriSpan]").hide();
			}else{
				$("table#splitRuleTable span[id=splitAnyGuestForwardActionPriSpan]").show();
			}
		});
		
		$("table#splitRuleTable select[id=splitAnyGuestInterfaceSec]").live('change', function() {
			if(this.value == 0){
				$("table#splitRuleTable span[id=splitAnyGuestForwardActionSecSpan]").hide();
			}else{
				$("table#splitRuleTable span[id=splitAnyGuestForwardActionSecSpan]").show();
			}
		});
		
		//Control the show of destination list by forward action
		$("table#customRuleTable select[id*=forwardActionType]").live('change', function() {
			destinationListShowFlag = false;
			$("table#customRuleTable select[id*=forwardActionType]").each(function() {
				if(this.value == FORWARDACTION_EXCEPTION){
					destinationListShowFlag = true;
				}
			});
			$("#destinationList").toggle(destinationListShowFlag);
			if(!destinationListShowFlag){
				Get(formName+"_domObjId").value = -1;
			}
		});
		
		$("tr[id^=routingPolicyPriRow] input[name=ruleIndices]").live('change', function() {
			if (this.checked == false) {
				Get("checkAll").checked = false;
			}
		});
		
		$("select[id^=sourceUserProfilePri_]").live('change', function() {
			var secIdTmp = this.id.replace(/Pri_/, 'Sec_');
			if (Get(secIdTmp)) {
				Get(secIdTmp).value = this.value;
			}
		});
	})();
	
	function checkToShowDestinationList() {
		var destinationListShowFlag = false;
		$("table#customRuleTable select[id*=forwardActionType]").each(function() {
			if(this.value == FORWARDACTION_EXCEPTION){
				destinationListShowFlag = true;
			}
		});
		$("#destinationList").toggle(destinationListShowFlag);
	}
	
	function policyRuleTypeChanged(radiobox){
		if(POLICYRULE_SPLIT == radiobox.value){
			$("#splitTypeNote").show();
			$("#splitTypeTr").show();
			$("#allTypeNote").hide();
			$("#allTypeTr").hide();
			$("#customTypeNote").hide();
			$("#customTypeTr").hide();
			$("#allTypeTr").hide();
			$("#destinationListForAll").hide();
			$("#destinationListForAllNote").hide();
		}else if(POLICYRULE_ALL == radiobox.value){
			$("#allTypeNote").show();
			$("#allTypeTr").show();
			$("#splitTypeNote").hide();
			$("#splitTypeTr").hide();
			$("#customTypeNote").hide();
			$("#customTypeTr").hide();
			$("#allTypeTr").show();
			$("#destinationListForAll").show();
			$("#destinationListForAllNote").show();
		}else{
			$("#customTypeNote").show();
			$("#customTypeTr").show();
			$("#splitTypeNote").hide();
			$("#splitTypeTr").hide();
			$("#allTypeNote").hide();
			$("#allTypeTr").hide();
			$("#allTypeTr").hide();
			$("#destinationListForAll").hide();
			$("#destinationListForAllNote").hide();
			$("select[id^=sourceUserProfileSec]").hide();
		}
		changeTrackIp(Get(formName+"_trackIpId"));
	}
	
	function changeTrackIpForAny(idxTmp,source){
		var ipTrackAnyPriTmp = Get("ipTrackForCheckPri"+idxTmp);
		var upAnyPriTmp = Get("sourceUserProfilePri"+idxTmp);
		var upAnySecTmp = Get("sourceUserProfileSec"+idxTmp);
		if (ipTrackAnyPriTmp) {
			ipTrackAnyPriTmp.value = source.value;
		}
		$("#ipTrackForCheckPri"+idxTmp).trigger("change");
		if (upAnySecTmp && upAnySecTmp.style.display != "none") {
			$("select#sourceUserProfileSec" + idxTmp + " option").remove();
			$("select#sourceUserProfileSec" +idxTmp ).append($("select#sourceUserProfilePri" +idxTmp + " option").clone());
			Get("sourceUserProfileSec"+idxTmp).value = Get("sourceUserProfilePri"+idxTmp).value;
		}
	}
	
	function changeTrackIp(trackIp){
		var ruleType = Get(formName+"_");
		if(trackIp.value != -1){
			var source = Get(formName+"_trackIpId");
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_CUSTOM).checked){
				//change the pri and sec show option
				var $elPris = $("table#customRuleTable select[id*=ipTrackForCheckPri] > option[value!=0]");
				if ($elPris.length > 0) {
					$elPris.each(function() {
						if (source.value == -1) {
							$(this).remove();
						} else {
							//$(this).attr({value:source.value, text: source.options[source.selectedIndex].text});
							$(this).attr({value:source.value}).text(source.options[source.selectedIndex].text);
						}
					});
				} else {
					var $addedOption = $("<option value='" + source.value + "'>" + source.options[source.selectedIndex].text + "</option>");
					$("table#customRuleTable select[id*=ipTrackForCheckPri]").append($addedOption);
				}
				
				var $elSecs = $("table#customRuleTable select[id*=ipTrackForCheckSec] > option[value!=0]");
				if ($elSecs.length > 0) {
					$elSecs.each(function() {
						if (source.value == -1) {
							$(this).remove();
						} else {
							//$(this).attr({value:source.value, text: source.options[source.selectedIndex].text});
							$(this).attr({value:source.value}).text(source.options[source.selectedIndex].text);
						}
					});
				} else {
					var $addedOption = $("<option value='" + source.value + "'>" + source.options[source.selectedIndex].text + "</option>");
					$("table#customRuleTable select[id*=ipTrackForCheckSec]").append($addedOption);
				}
				
				
				var idxTmp = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANY);
				if (idxTmp && idxTmp != null && idxTmp!="") {
					changeTrackIpForAny(idxTmp,source);
				}
				
				idxTmp = getIndexOfSelectForAny(ROUTING_POLICY_RULE_ANYGUEST);
				if (idxTmp && idxTmp != null && idxTmp!="") {
					changeTrackIpForAny(idxTmp,source);
					if(Get("forwardActionTypeSec"+idxTmp)){
						Get("forwardActionTypeSec"+idxTmp).value = FORWARDACTION_DROP;
						Get("forwardActionTypeSec"+idxTmp).disabled = true;
					}
				} 
			}
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_SPLIT).checked){
				Get("anySplitTrackIpPriId").value = source.value;
				Get("anyAllTrackIpPriId").value = source.value;
				$("#splitAnySecTr").show();
				$("#allAnySecTr").show();
				Get("anySplitTrackIpSecId").value = source.value;
				Get("allAnyTrackIpSecId").value = source.value;
				//any guest
				Get("anyGuestSplitTrackIpPriId").value = source.value;
				$("#splitAnyGuestSecTr").show();
				Get("anyGuestSplitTrackIpSecId").value = source.value;
			}
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_ALL).checked){
				Get("anySplitTrackIpPriId").value = source.value;
				Get("anyGuestSplitTrackIpPriId").value = source.value;
				Get("anyAllTrackIpPriId").value = source.value;
				$("#splitAnySecTr").show();
				$("#splitAnyGuestSecTr").show();
				$("#allAnySecTr").show();
				Get("anySplitTrackIpSecId").value = source.value;
				Get("anyGuestSplitTrackIpSecId").value = source.value;
				Get("allAnyTrackIpSecId").value = source.value;
			}
		}else{
			//$("#routeGuestSecTr").hide();
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_CUSTOM).checked){
				//change the pri show option
				$("table#customRuleTable select[id^=ipTrackForCheckPri] > option[value!=0]").remove();
				//remove all sec rows
				$("table#customRuleTable tr[id^=routingPolicySecRow]").remove();
			}
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_SPLIT).checked){
				Get("anySplitTrackIpPriId").value = 0;
				Get("anyGuestSplitTrackIpPriId").value = 0;
				
				$("#splitAnySecTr").hide();
				$("#splitAnyGuestSecTr").hide();
				
			}
			if(Get(formName + "_dataSource_policyRuleType"+POLICYRULE_ALL).checked){
				Get("anyAllTrackIpPriId").value = 0;
				$("#allAnySecTr").hide();
			}
		}
		
		//fixGuestRuleForwardDrop();
		checkToShowDestinationList();
	}
	
	function $getUserProfileSelectForAny(value) {
		return $("select[id^=sourceUserProfilePri] option[value="+value+"]").parent();
	}
	function getIndexOfSelectForAny(value) {
		var idStr = $getUserProfileSelectForAny(value).attr("id");
		if (idStr.indexOf("_") > 0) {
			return idStr.substr(idStr.indexOf("_"));
		}
		return "";
	}
	
	function getExistRowCounts() {
		return $("table#customRuleTable select[id^=sourceUserProfilePri]").length;
	}
		
	function changeSpecificRowDom(anotherRow, suffix) {
		var elements = anotherRow.getElementsByTagName("select");
		for(var index=0; index<elements.length; index++) {
			
			if(elements[index].id == "sourceUserProfilePri_Template"){
				elements[index].id = 'sourceUserProfilePri_' + suffix;
			}else if(elements[index].id == "ipTrackForCheckPri_Template") {
				elements[index].id = 'ipTrackForCheckPri_' + suffix;
				var eblCheck = Get(formName+"_dataSource_enableIpTrackForCheck");
				var source = Get(formName+"_trackIpId");
				if (eblCheck.checked && source.selectedIndex >= 0 && source.value != -1) {
					var option = new Option(source.options[source.selectedIndex].text,source.value);
					try{
						elements[index].add(option, null);
					}catch(e){
						elements[index].add(option);
					}
				}
			}else if(elements[index].id == "interfaceTypePri_Template"){
				elements[index].id = 'interfaceTypePri_' + suffix;
			}else if(elements[index].id == "forwardActionTypePri_Template"){
				elements[index].id = 'forwardActionTypePri_' + suffix;
			}else if(elements[index].id == "sourceUserProfileSec_Template"){
				elements[index].id = 'sourceUserProfileSec_' + suffix;
			}else if(elements[index].id == "ipTrackForCheckSec_Template"){
				elements[index].id = 'ipTrackForCheckSec_'+suffix;
			}else if(elements[index].id == "interfaceTypeSec_Template"){
				elements[index].id = 'interfaceTypeSec_'+suffix;
			}else if(elements[index].id == "forwardActionTypeSec_Template"){
				elements[index].id = 'forwardActionTypeSec_'+suffix;
			}
		}
	}
	
	function createAnyRule(newRowEmpty,lastRow,count){
		//add default row
		var suffix = count + 1;
		newRowEmpty.id = 'routingPolicyPriRow_' + suffix;
		changeSpecificRowDom(newRowEmpty,suffix);
		YAHOO.util.Dom.insertBefore(newRowEmpty, lastRow);
		rowCount ++;

		//set value according track ip
		/* var trackIpIdEle = Get(formName + "_trackIpId");
		if(trackIpIdEle.value != -1){
			$("#ipTrackForCheckPri_"+suffix).append("<option value="+trackIpIdEle.value+">"+trackIpIdEle[trackIpIdEle.selectedIndex].text +"</option>");
			Get("ipTrackForCheckPri_"+suffix).value = trackIpIdEle.value;
			$("#ipTrackForCheckSec_"+suffix).append("<option value="+trackIpIdEle.value+">"+trackIpIdEle[trackIpIdEle.selectedIndex].text +"</option>");
			Get("ipTrackForCheckSec_"+suffix).value = trackIpIdEle.value;
			$("select#sourceUserProfileSec_"+suffix+" option").remove();
			$("select#sourceUserProfileSec_"+suffix+"").append($("select#sourceUserProfilePri_"+suffix+" option").clone());
			Get("sourceUserProfileSec_"+suffix).value = Get("sourceUserProfilePri_"+suffix).value;
			
		} */
	}
	
	function createRoutingPolicyRule(){
		var table=Get("customRuleTable");
		var emptyRowPri = Get("trackIpEmptyPri_Template");
		var emptyRowAnyGuestPri = Get("trackIpEmptyAnyGuestPri_Template");
		//var emptyRowSec = Get("trackIpEmptySec_Template");
		var existRowPri = Get("trackIpExistPri_Template");
		var lastRow = Get("lastEmptyRow");
		var newRowEmpty,newRowPri,newRowEmptyAnyGuest;
		if(rowCount == 0){
			newRowEmpty = emptyRowPri.cloneNode(true);
			createAnyRule(newRowEmpty,lastRow,1);
			newRowEmptyAnyGuest = emptyRowAnyGuestPri.cloneNode(true);
			createAnyRule(newRowEmptyAnyGuest,newRowEmpty,0);
		} else {
			newRowPri = existRowPri.cloneNode(true);
			var suffix = rowCount++ + 1;
			newRowPri.id = 'routingPolicyPriRow_' + suffix;
			changeSpecificRowDom(newRowPri,suffix);
			var rowIdStr= $("table#customRuleTable select[id^=sourceUserProfilePri][value=-2]").attr("id");
			if (rowIdStr.indexOf("_") > 0) {
				var index = rowIdStr.substr(rowIdStr.indexOf("_"));
				var anyRow = Get("routingPolicyPriRow"+index);
				YAHOO.util.Dom.insertBefore(newRowPri, anyRow);
			}
		}
	}
	
	function enableIpTrack(checkbox){
		$("#trackIpId").toggle(checkbox);
		if (checkbox == false) {
			var $trackIPElTmp = $("select[id$=_trackIpId]");
			$trackIPElTmp[0].selectedIndex = 0;
			$trackIPElTmp.trigger("change");
		}
	}
	
	function enableRouteGuest(checkbox){
		$("#guestUserProfileInterfacePri").toggle(checkbox);
		$("#guestUserProfileInterfaceSec").toggle(checkbox);
	}
	
	function enableDestinationList(value){
		$("#domObjIdForAll").toggle(value);
		if (value == false) {
			var $destWhiteListTmp = $("select[id$=_domObjIdForAll]");
			$destWhiteListTmp[0].selectedIndex = 0;
			$destWhiteListTmp.trigger("change");
		}
	}
	
	function fixGuestRuleForwardDrop() {
		var forwardTypeDrop = 2;
		$("select[id$=splitGuestForwardActionPri]").attr("value", forwardTypeDrop);
		$("select[id$=splitGuestForwardActionSec]").attr("value", forwardTypeDrop);
		$("select[id$=allGuestForwardActionPri]").attr("value", forwardTypeDrop);
		$("select[id$=allGuestForwardActionSec]").attr("value", forwardTypeDrop);
	}
	
	function changeUserProfileGuest(checkbox){
		if( -1 == checkbox.value){
			$("#splitGuestPriTr").hide();
			$("#splitGuestSecTr").hide();
			$("#allGuestPriTr").hide();
			$("#allGuestSecTr").hide();
		}else{
			$("#splitGuestPriTr").show();
			$("#allGuestPriTr").show();
			Get("splitGuestUserProfileId").value=checkbox.value;
			Get("allGuestUserProfileId").value=checkbox.value;
			//fixGuestRuleForwardDrop();
			if(Get(formName+"_trackIpId").value != -1){
				Get("splitGuestTrackIpPriId").value=Get(formName+"_trackIpId").value;
				$("#splitGuestSecTr").show();
				Get("splitGuestTrackIpSecId").value=Get(formName+"_trackIpId").value;
				Get("allGuestTrackIpPriId").value=Get(formName+"_trackIpId").value;
				$("#allGuestSecTr").show();
				Get("allGuestTrackIpSecId").value=Get(formName+"_trackIpId").value;
			}else{
				$("#splitGuestSecTr").hide();
				Get("splitGuestTrackIpPriId").value=0;
				$("#allGuestSecTr").hide();
				Get("allGuestTrackIpPriId").value=0;
			}
			
		}
	}
	
	function toggleCheckAllRules(chkEl) {
		$("tr[id^=routingPolicyPriRow] input[name=ruleIndices]").attr("checked", chkEl.checked);
	}
	
	function changeDestinationList(ele){
		if(ele.value != -1){
			Get("allAnyForwardActionPri").value = 4;
			Get("allAnyForwardActionSec").value = 4;
		}else{
			Get("allAnyForwardActionPri").value = 1;
			Get("allAnyForwardActionSec").value = 1;
		}
	}
</script>
<script>
var rowCount = 0; // sepcific row count
var destinationListShowFlag = false;//used to label whether white list show in Custom
//-------------Common functions-----------------//
YAHOO.util.Event.onDOMReady(function () {
	<s:if test="%{null !=dataSource.routingPolicyRuleList && dataSource.routingPolicyRuleList.size() >= 0}">
		rowCount = <s:property value="%{dataSource.routingPolicyRuleList.size()}"/>;
		//when type change from other to customer, have to add the default item.
		<s:if test="%{dataSource.policyRuleType != 3}">
			rowCount = 0;
		</s:if>
		if(rowCount == 0) {
			createRoutingPolicyRule();
		//	Get(formName + "_trackIpId").value = -1;
			Get(formName + "_trackIpId").onchange();
		}
		<s:if test="%{dataSource.policyRuleType == 3}">
			checkToShowDestinationList();
		</s:if>
	</s:if> 
});

$(function() {
	//fixGuestRuleForwardDrop();
});
</script>