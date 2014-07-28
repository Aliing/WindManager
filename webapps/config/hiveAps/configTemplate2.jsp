<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css"/>" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-beta-min.js" />"></script>
<script>
var formName = 'networkPolicyTemplate';
var HIDE_CHANGE_LINK_TIMEOUT = 2500; // ms
var imagesBaseUrl = "<s:url value="/images/" includeParams="none"/>";

function onLoadPage() {

}

function submitEditAction(operation,ssidId){
	document.forms[formName].ssidId.value = ssidId;
	submitAction(operation);
}

function submitAction(operation) {
	if (validate(operation)) {
			showProcessing();
		    document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
	}
}

function validate(operation) {
	return true;
}

function openCwpListDialog(bindTarget, ssidId, isPPSK ,isWpa) {
	var source = '<s:url action="captivePortalWeb" includeParams="none"/>' 
		+ '?operation=cwpListDialog&ssidId=' + ssidId 
		+ '&ppskCwp=' + isPPSK
		+ '&wpaCwp=' + isWpa
		+ '&bindTarget=' + bindTarget
		+ '&ignore=' + new Date().getTime();
	openIFrameDialog(340, 300, source);
}

function openCwpSubdrawer(cwpId) {
	parent.editCWPInSubdrawer(cwpId, 0);
}

function displayCWPChange(num) {
	Get("cwpChange" + num).style.display = '';
}

function hideCWPChange(num) {
	setTimeout("hideChangeLink(" + num + ")", HIDE_CHANGE_LINK_TIMEOUT);
}

function hideChangeLink(num) {
	Get("cwpChange" + num).style.display = 'none';
}

function openSchedulerListDialog(bindTarget,ssidId,savePermit) {
	var source = '<s:url action="scheduler" includeParams="none"/>' 
		+ '?operation=schedulerListDialog&ssidId=' + ssidId 
		+ '&bindTarget=' +bindTarget
		+ '&ignore=' + new Date().getTime();
	openIFrameDialog(340, 300, source);
}

function displayHideAssignUs(e){
	if ($(e).attr("src").indexOf("expand_plus")>0) {
		$(e).attr("src", imagesBaseUrl + "/expand_minus.gif");
		var blnFound = false;
		$(e).parent().parent().nextAll("tr").each(function() {
			if (this.className != "npcListBlockSub") {
				blnFound = true;
			}
			if (blnFound) {
				return;
			}
			$(this).show();
		});
	} else {
		$(e).attr("src", imagesBaseUrl + "/expand_plus.gif");
		var blnFound = false;
		$(e).parent().parent().nextAll("tr").each(function() {
			if (this.className != "npcListBlockSub") {
				blnFound = true;
			}
			if (blnFound) {
				return;
			}
			$(this).hide();
		});
	}
}

</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="networkPolicyTemplate" id="networkPolicyTemplate">
<s:hidden name="operation" />
<s:hidden name="id" />
<s:hidden name="forward" />
<s:hidden name="formChanged" />
<s:hidden name="vlanId" />
<s:hidden name="vlanNativeId" />
<s:hidden name="configType4Port" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td class="noteErrorForNetworkPolicy"><div id="errNoteForAllNetwork" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
				   <%--  <s:if test="%{!dataSource.blnBonjourOnly}"> --%>
				   <s:if test="%{dataSource.configType.wirelessContained}">
					<tr>
						<td class="npcNoteTitle"><s:text name="config.networkpolicy.title"/></td>
					</tr>
					
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
								<td class="npcHead1" style="padding-left: 35px;padding-right: 20px;"><s:text name="config.networkpolicy.ssid.title"/></td>
								<s:if test="%{writeDisabled != 'disabled'}">
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btAddRemoveSsid" style="visibility: hidden;" onclick="addRemoveSsid();" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
								</s:else>
								</tr>
							</table>
						</td>
					</tr>
					<s:if test="%{dataSource.ssidInterfacesTreeMap!=null && dataSource.ssidInterfacesTreeMap.size>7}">
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout: fixed;">
								<tr>
									<td width="26px">&nbsp;</td>
									<td class="npcHead2" width="150px"><s:text name="config.networkpolicy.ssid.list.name" /></td>
									<td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.auth" /></td>
									<td class="npcHead2" width="15px">&nbsp;</td>
									<td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.userprofile" /></td>
									<td class="npcHead2" width="160px"> <s:text name="config.networkpolicy.ssid.list.vlan" /> </td>
								</tr>
								<s:iterator value="%{dataSource.ssidInterfacesTreeMap.values}" status="status" id="templateSsid">
									<s:if test="%{#templateSsid.ssidProfile != null}">
										<tr class="npcList">
										<s:if test="%{writeDisabled != 'disabled'}">
											<s:if test="%{#templateSsid.ssidProfile.schedulers != null && #templateSsid.ssidProfile.schedulers.size >0}">
												<td class="imageTd" style="padding-top: 11px;"><a href="javascript:void(0);" onclick="openSchedulerListDialog(1,<s:property value='ssidProfile.id'/>);" class="schedulerTd">
													<img src="<s:url value="/images/hm_v2/profile/HM-icon-schedules-active.png" />"
														width="20" height="20" class="dinl" alt="Scheduler" title="Scheduler" /></a>&nbsp;</td>
											</s:if>
											<s:else>
												<td class="imageTd" style="padding-top: 11px;"><a href="javascript:void(0);" onclick="openSchedulerListDialog(1,<s:property value='ssidProfile.id'/>);" class="schedulerTd">
													<img src="<s:url value="/images/hm_v2/profile/HM-icon-schedules.png" />"
														width="20" height="20" class="dinl" alt="Scheduler" title="Scheduler" /></a>&nbsp;</td>
											</s:else>
										</s:if>
										<s:else>
											<s:if test="%{#templateSsid.ssidProfile.schedulers != null && #templateSsid.ssidProfile.schedulers.size >0}">
												<td class="imageTd" style="padding-top: 11px;"><a href="javascript:void(0);" onclick="showWarmMessage();" class="schedulerTd">
													<img src="<s:url value="/images/hm_v2/profile/HM-icon-schedules-active.png" />"
														width="20" height="20" class="dinl" alt="Scheduler" title="Scheduler" /></a>&nbsp;</td>
											</s:if>
											<s:else>
												<td class="imageTd" style="padding-top: 11px;"><a href="javascript:void(0);" onclick="showWarmMessage();" class="schedulerTd">
													<img src="<s:url value="/images/hm_v2/profile/HM-icon-schedules.png" />"
														width="20" height="20" class="dinl" alt="Scheduler" title="Scheduler" /></a>&nbsp;</td>
											</s:else>
										</s:else>
											<td valign="top" style="padding-top: 11px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td rowspan="3" class="imageTd">
															<s:if test="%{#templateSsid.ssidProfile.radioMode == 1}">
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-2-4.png" />"
																	width="30" height="30" alt="11bg" title="11bg" />
															</s:if>
															<s:elseif test="%{#templateSsid.ssidProfile.radioMode == 2}">
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-5.png" />"
																	width="30" height="30" alt="11a" title="11a" />
															</s:elseif>
															<s:else>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid.png" />"
																	width="30" height="30" alt="both" title="both" />
															</s:else>
														</td>
														<td class="smallTd">
															&nbsp;
															<a class="npcLinkA" href="javascript:void(0);" onclick='editSsid(<s:property value="%{#templateSsid.ssidProfile.id}"/>)'><span title="<s:property value="%{interfaceName}" />"><s:property value="%{interfaceNameSubstr}" /></span></a>
														</td>
													</tr>
													<tr>
														<td class="smallTd">
															&nbsp;
															<s:property value="%{#templateSsid.ssidProfile.ssid}" />
														</td>
													</tr>
													<tr>
														<td class="smallTd">
															&nbsp;
															<s:property value="%{#templateSsid.ssidProfile.accessModeString}" />
														</td>
													</tr>
												</table>
											</td>
											
											<td valign="top" style="padding-top: 11px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<s:if test="%{#templateSsid.ssidProfile.cwpSelectEnabled}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
																	width="30" height="30" alt="CWP" title="CWP" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.cwp==null && #templateSsid.ssidProfile.userPolicy==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, false ,false);"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
																	</s:if>
																	<s:elseif test="%{#templateSsid.ssidProfile.cwp != null}">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, false ,false);"><span title="<s:property value="%{#templateSsid.ssidProfile.cwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.cwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.renameCWPType ? #templateSsid.ssidProfile.cwp.registrationTypeName4IDM : #templateSsid.ssidProfile.cwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:elseif>
																	<s:elseif test="%{#templateSsid.ssidProfile.userPolicy != null}">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, false ,false);"><span title="<s:property value="%{#templateSsid.ssidProfile.userPolicy.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.userPolicy.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.renameCWPType ? #templateSsid.ssidProfile.userPolicy.registrationTypeName4IDM : #templateSsid.ssidProfile.userPolicy.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:elseif>
																</td>
															</s:if>
															<s:else>
																<td>
																<s:if test="%{#templateSsid.ssidProfile.cwp==null && #templateSsid.ssidProfile.userPolicy==null}">
																	<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
																</s:if>
																<s:elseif test="%{#templateSsid.ssidProfile.cwp != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td>
																				<a class="npcLinkA" href="javascript:void(0);"
																					onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.cwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.cwp.cwpNameSubstr}" /></span></a>
																			</td>
																		</tr>
																		<tr>
																			<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.renameCWPType ? #templateSsid.ssidProfile.cwp.registrationTypeName4IDM : #templateSsid.ssidProfile.cwp.registrationTypeName}" /></td>
																		</tr>
																	</table>
																</s:elseif>
																<s:elseif test="%{#templateSsid.ssidProfile.userPolicy != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td>
																				<a class="npcLinkA" href="javascript:void(0);"
																					onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.userPolicy.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.userPolicy.cwpNameSubstr}" /></span></a>
																			</td>
																		</tr>
																		<tr>
																			<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.renameCWPType ? #templateSsid.ssidProfile.userPolicy.registrationTypeName4IDM : #templateSsid.ssidProfile.userPolicy.registrationTypeName}" /></td>
																		</tr>
																	</table>
																</s:elseif>
																</td>
															</s:else>
														</tr>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.enabledSocialLogin}">
													   <tr>
                                                            <td>
                                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
                                                                    width="30" height="30" alt="CWP" title="CWP" />&nbsp;
                                                            </td>
                                                            <td><label><s:text name="config.ssid.ga.label"/></label></td>
													   </tr>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.enabledCM}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-client-management.png" />"
																	width="30" height="30" alt="Client Management" title="Client Management" />&nbsp;
															</td>
															<td><label>&nbsp;<a id="clientManagementAnchor" href="<s:property value='%{#templateSsid.ssidProfile.manageLink4CM}'/>" tabindex="-1" target="_blank"><s:text name="config.radiusProxy.cloudAuth.ssid.cm"/></a></label></td>
														</tr>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.blnDisplayIDM}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/ah-hm-idm-30x30.png" />"
																	width="30" height="30" alt="Aerohive ID Manager" title="Aerohive ID Manager" />&nbsp;
															</td>
															<td><label>&nbsp;<a id="aerohiveIDManagerAnchor" href="<s:property value='%{#templateSsid.ssidProfile.manageLink4IDM}'/>" tabindex="-1" target="_blank"><s:text name="config.radiusProxy.cloudAuth.ssid"/></a></label><br><label class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.suffixIDMType}"/></label></td>
														</tr>
													</s:if>
													<s:else>													
													<s:if test="%{#templateSsid.ssidProfile.blnDisplayRadius}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
																	width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																	<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 1);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignment.id}" />, 1);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																	<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
													</s:if>
													</s:else>
													<s:if test="%{#templateSsid.ssidProfile.accessMode==2 && #templateSsid.ssidProfile.ssidSecurity.blnMacBindingEnable}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-hiveap-ppsk30x30.png" />"
																	width="30" height="30" alt="Private PSK Server" title="Private PSK Server" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}"/>);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}"/>);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																				</td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																				</td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.accessMode==2}">
														<s:if test="%{!#templateSsid.ssidProfile.enabledIDM}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-Local_User_Groups.png" />"
																	width="30" height="30" alt="Local User Groups" title="Local User Groups" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.localUserGroups==null || #templateSsid.ssidProfile.localUserGroups.size==0}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="addRemoveUserGroups('SSID', '<s:property value="%{#templateSsid.ssidProfile.id}"/>', 'PPSK');"><s:text name="config.networkpolicy.ssid.list.localUserGoup"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="addRemoveUserGroups('SSID', '<s:property value="%{#templateSsid.ssidProfile.id}"/>', 'PPSK');"><s:text name="config.networkpolicy.ssid.list.localUserGoup"/></a>
																				</td>
																			</tr>
																			<s:iterator value="%{#templateSsid.ssidProfile.localUserGroups}" id="groupStatus">
																				<tr>
																					<td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick='editLocalUserGroup(<s:property value="%{#groupStatus.id}"/>)'><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.localUserGroups==null || #templateSsid.ssidProfile.localUserGroups.size==0}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.localUserGoup"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.localUserGoup"/></a>
																				</td>
																			</tr>
																			<s:iterator value="%{#templateSsid.ssidProfile.localUserGroups}" id="groupStatus">
																				<tr>
																					<td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick="showWarmMessage();"><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														</s:if>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.enableAssignUserProfile}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-Local_User_Groups.png" />"
																	width="30" height="30" alt="RADIUS Local User Groups" title="RADIUS Local User Groups" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.radiusUserGroups==null || #templateSsid.ssidProfile.radiusUserGroups.size==0}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="addRemoveUserGroups('SSID', '<s:property value="%{#templateSsid.ssidProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="addRemoveUserGroups('SSID', '<s:property value="%{#templateSsid.ssidProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
																				</td>
																			</tr>
																			<s:iterator value="%{#templateSsid.ssidProfile.radiusUserGroups}" id="groupStatus">
																				<tr>
																					<td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick='editLocalUserGroup(<s:property value="%{#groupStatus.id}"/>)'><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.radiusUserGroups==null || #templateSsid.ssidProfile.radiusUserGroups.size==0}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
																				</td>
																			</tr>
																			<s:iterator value="%{#templateSsid.ssidProfile.radiusUserGroups}" id="groupStatus">
																				<tr>
																					<td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick="showWarmMessage();"><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.accessMode==2 && #templateSsid.ssidProfile.enablePpskSelfReg && #templateSsid.ssidProfile.enableSingleSsid}">
													<s:if test="enableClientManagement">
														<!--tr height="8px"><td></td><td></td></tr-->
														<s:if test="%{!#templateSsid.ssidProfile.enabledIDM}">
														<tr class="npcList">
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-hiveap-ppsk30x30.png" />"
																	width="30" height="30" alt="Private PSK Server" title="Private PSK Server" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}" />);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}" />);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																				</td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														</s:if>
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
																	width="30" height="30" alt="CWP" title="CWP" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, true ,false);"><s:text name="config.networkpolicy.ssid.list.self.pskcwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, true ,false);"><span title="<s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.ppskECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskcwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.ppskECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
													<s:if test="%{#templateSsid.ssidProfile.ppskECwp!=null && #templateSsid.ssidProfile.ppskECwp.ppskServerType==1}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
																	width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
															</td>
															<s:if test="%{#templateSsid.ssidProfile.enabledIDM}">
																<s:if test="%{writeDisabled != 'disabled'}">
																	<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 1);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignment.id}" />, 1);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:if>
																<s:else>
																	<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:else>
															</s:if>
															<s:else>
																<s:if test="%{writeDisabled != 'disabled'}">
																	<td id="radiusTD2_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignmentPpsk==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 2);"><s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.id}" />, 2);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:if>
																<s:else>
																	<td id="radiusTD2_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignmentPpsk==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:else>
															</s:else>
														</tr>
													</s:if>
													</s:if>
													</s:if>
												</table>
											</td>
											<td colspan="3" valign="top">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<s:if test="%{#templateSsid.ssidProfile.userProfileDefault != null}">
														<tr class="npcListBlock">
															<td width="15px">
																<s:if test="%{#templateSsid.ssidProfile.userProfileDefault.enableAssign && #templateSsid.ssidProfile.userProfileDefault.assignRules!=null && #templateSsid.ssidProfile.userProfileDefault.assignRules.size>0}">
																	<img src="<s:url value="/images/expand_plus.gif" />"
																		width="12px" height="12px" alt="Display/Hide Reassign User Profile Reassign Userprofile" title="Display/Hide Reassign User Profile"
																		onclick="displayHideAssignUs(this);"/>
																</s:if>
															</td>
															<td width="32px" style="padding-top: 8px;" nowrap="nowrap">
																<s:if test="%{#templateSsid.ssidProfile.userProfileDefault.enableAssign && #templateSsid.ssidProfile.userProfileDefault.assignRules!=null && #templateSsid.ssidProfile.userProfileDefault.assignRules.size>0}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:if>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:else>
															</td>
															<td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#templateSsid.ssidProfile.userProfileDefault.id}" />');"><span title="<s:property value="%{#templateSsid.ssidProfile.userProfileDefault.userProfileName}" />"><s:property value="%{#templateSsid.ssidProfile.userProfileDefault.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(default)</span></td>
															<td class="paddingLeft">&nbsp;
																	<s:if test="%{#templateSsid.ssidProfile.userProfileDefault.vlan != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																		<s:iterator value="%{usMapping.values}" id="myUsMapping">
																		<s:if test="%{#myUsMapping.userProfile.id==#templateSsid.ssidProfile.userProfileDefault.id}">
																		<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																			onmouseout="javascript: hideIFrameNoModalPanel();">
																				<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																					width="30px" height="30px" alt="vlan" title="vlan" />
																		</td><td>&nbsp;
																		<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																		</td>
																		</s:if>
																		</s:iterator>
																		<td></td>
																		</tr>
																	</table>
																	</s:if>
															</td>
														</tr>
														<s:if test="%{#templateSsid.ssidProfile.userProfileDefault.enableAssign && #templateSsid.ssidProfile.userProfileDefault.assignRules!=null && #templateSsid.ssidProfile.userProfileDefault.assignRules.size>0}">
															<s:iterator value="%{#templateSsid.ssidProfile.userProfileDefault.assignRules}" id="userProfileDefaultAssignStatus">
																<tr class="npcListBlockSub" style="display:none;">
																	<td width="15px"></td>
																	<td width="32px" style="padding-top: 8px;">
																		
																	</td>
																	<td width="162px" ><img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		style="vertical-align: middle;"
																		width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#userProfileDefaultAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileDefaultAssignStatus.UserProfileNameSubstr}" /></a></td>
																	<td class="paddingLeft">&nbsp;
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																			<s:iterator value="%{usMapping.values}" id="myUsMapping">
																			<s:if test="%{#myUsMapping.userProfile.id==#userProfileDefaultAssignStatus.userProfileId}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																					<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																						width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																			<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td>
																			</s:if>
																			</s:iterator>
																			<td></td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</s:iterator>
														</s:if>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg != null}">
														<tr class="npcListBlock">
															<td width="15px">
																<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg.enableAssign && #templateSsid.ssidProfile.userProfileSelfReg.assignRules!=null && #templateSsid.ssidProfile.userProfileSelfReg.assignRules.size>0}">
																	<img src="<s:url value="/images/expand_plus.gif" />"
																		width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
																		onclick="displayHideAssignUs(this);"/>	
																</s:if>
															</td>
															<td width="32px" style="padding-top: 8px;">
																<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg.enableAssign && #templateSsid.ssidProfile.userProfileSelfReg.assignRules!=null && #templateSsid.ssidProfile.userProfileSelfReg.assignRules.size>0}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:if>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:else>
															</td>
															<td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#templateSsid.ssidProfile.userProfileSelfReg.id}" />');"><span title="<s:property value="%{#templateSsid.ssidProfile.userProfileSelfReg.userProfileName}" />"><s:property value="%{#templateSsid.ssidProfile.userProfileSelfReg.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(self-reg)</span></td>
															<td class="paddingLeft">&nbsp;
																<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg.vlan != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																		<s:iterator value="%{usMapping.values}" id="myUsMapping">
																			<s:if test="%{#myUsMapping.userProfile.id==#templateSsid.ssidProfile.userProfileSelfReg.id}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																				<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																					width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																			<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td>
																			</s:if>
																		</s:iterator>
																		<td></td>
																		</tr>
																	</table>
																</s:if>
															</td>
														</tr>
														<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg.enableAssign && #templateSsid.ssidProfile.userProfileSelfReg.assignRules!=null && #templateSsid.ssidProfile.userProfileSelfReg.assignRules.size>0}">
															<s:iterator value="%{#templateSsid.ssidProfile.userProfileSelfReg.assignRules}" id="userProfileSelfRegAssignStatus">
																<tr class="npcListBlockSub" style="display:none;">
																	<td  width="15px"></td>
																	<td width="32px" style="padding-top: 8px;">
																		
																	</td>
																	<td width="162px"><img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		style="vertical-align: middle;"
																		width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#userProfileSelfRegAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileSelfRegAssignStatus.UserProfileNameSubstr}" /></a></td>
																	<td class="paddingLeft">&nbsp;
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																			<s:iterator value="%{usMapping.values}" id="myUsMapping">
																				<s:if test="%{#myUsMapping.userProfile.id==#userProfileSelfRegAssignStatus.userProfileId}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																					<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																						width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																				<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td></s:if>
																			</s:iterator>
																			</tr>
																		</table>
																	</td>
																</tr>
															</s:iterator>
														</s:if>
													</s:if>
													<s:if test="%{#templateSsid.ssidProfile.userProfileGuest != null}">
														<tr class="npcListBlock">
															<td width="15px">
																<s:if test="%{#templateSsid.ssidProfile.userProfileGuest.enableAssign && #templateSsid.ssidProfile.userProfileGuest.assignRules!=null && #templateSsid.ssidProfile.userProfileGuest.assignRules.size>0}">
																	<img src="<s:url value="/images/expand_plus.gif" />"
																		width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
																		onclick="displayHideAssignUs(this);"/>	
																</s:if>
															</td>
															<td width="32px" style="padding-top: 8px;">
																<s:if test="%{#templateSsid.ssidProfile.userProfileGuest.enableAssign && #templateSsid.ssidProfile.userProfileGuest.assignRules!=null && #templateSsid.ssidProfile.userProfileGuest.assignRules.size>0}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:if>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:else>
															</td>
															<td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#templateSsid.ssidProfile.userProfileGuest.id}" />');"><span title="<s:property value="%{#templateSsid.ssidProfile.userProfileGuest.userProfileName}" />"><s:property value="%{#templateSsid.ssidProfile.userProfileGuest.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(guest)</span></td>
															<td class="paddingLeft">&nbsp;
																<s:if test="%{#templateSsid.ssidProfile.userProfileGuest.vlan != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																		<s:iterator value="%{usMapping.values}" id="myUsMapping">
																			<s:if test="%{#myUsMapping.userProfile.id==#templateSsid.ssidProfile.userProfileGuest.id}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																				<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																					width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																			<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td>
																			</s:if>
																		</s:iterator>
																		<td></td>
																		</tr>
																	</table>
																</s:if>
															</td>
														</tr>
														<s:if test="%{#templateSsid.ssidProfile.userProfileGuest.enableAssign && #templateSsid.ssidProfile.userProfileGuest.assignRules!=null && #templateSsid.ssidProfile.userProfileGuest.assignRules.size>0}">
															<s:iterator value="%{#templateSsid.ssidProfile.userProfileGuest.assignRules}" id="userProfileGuestAssignStatus">
																<tr class="npcListBlockSub" style="display:none;">
																	<td  width="15px"></td>
																	<td width="32px" style="padding-top: 8px;">
																		
																	</td>
																	<td width="162px"><img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		style="vertical-align: middle;"
																		width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#userProfileGuestAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileGuestAssignStatus.UserProfileNameSubstr}" /></a></td>
																	<td class="paddingLeft">&nbsp;
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																			<s:iterator value="%{usMapping.values}" id="myUsMapping">
																				<s:if test="%{#myUsMapping.userProfile.id==#userProfileGuestAssignStatus.userProfileId}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																					<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																						width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																				<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td></s:if>
																			</s:iterator>
																			</tr>
																		</table>
																	</td>
																</tr>
															</s:iterator>
														</s:if>
													</s:if>
													<s:iterator value="%{#templateSsid.ssidProfile.radiusUserProfile}" id="ssidStatus">
														<tr class="npcListBlock">
															<td width="15px">
																<s:if test="%{#ssidStatus.enableAssign && #ssidStatus.assignRules!=null && #ssidStatus.assignRules.size>0}">
																	<img src="<s:url value="/images/expand_plus.gif" />"
																		width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
																		onclick="displayHideAssignUs(this);"/>	
																</s:if>
															</td>
															<td width="32px" style="padding-top: 8px;">
																<s:if test="%{#ssidStatus.enableAssign && #ssidStatus.assignRules!=null && #ssidStatus.assignRules.size>0}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:if>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		width="30px" height="30px" alt="user profile" title="user profile" />
																</s:else>
															</td>
															<td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#ssidStatus.id}" />');"><span title="<s:property value="%{#ssidStatus.userProfileName}" />"><s:property value="%{#ssidStatus.userProfileNameSubstr}" /></span></a></td>
															<td class="paddingLeft">&nbsp;
																<s:if test="%{#ssidStatus.vlan != null}">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<s:iterator value="%{usMapping.values}" id="myUsMapping">
																			<s:if test="%{#myUsMapping.userProfile.id==#ssidStatus.id}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																				<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																					width="30px" height="30px" alt="vlan" title="vlan" />
																		</td><td>&nbsp;
																			<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																		</td></s:if>
																		</s:iterator>
																		<td></td></tr>
																	</table>
																</s:if>
															</td>
														</tr>
														<s:if test="%{#ssidStatus.enableAssign && #ssidStatus.assignRules!=null && #ssidStatus.assignRules.size>0}">
															<s:iterator value="%{#ssidStatus.assignRules}" id="userProfileRadiusAssignStatus">
																<tr class="npcListBlockSub" style="display:none;">
																	<td  width="15px"></td>
																	<td width="32px" style="padding-top: 8px;">
																	</td>
																	<td width="162px"><img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
																		style="vertical-align: middle;"
																		width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#templateSsid.ssidProfile.id}" />', '<s:property value="%{#userProfileRadiusAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileRadiusAssignStatus.UserProfileNameSubstr}" /></a></td>
																	<td class="paddingLeft">&nbsp;
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																			<s:iterator value="%{usMapping.values}" id="myUsMapping">
																			<s:if test="%{#myUsMapping.userProfile.id==#userProfileRadiusAssignStatus.userProfileId}">
																			<td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																				onmouseout="javascript: hideIFrameNoModalPanel();">
																					<img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
																						width="30px" height="30px" alt="vlan" title="vlan" />
																			</td><td>&nbsp;
																				<span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
																			</td></s:if>
																			</s:iterator>
																			<td></td></tr>
																		</table>
																	</td>
																</tr>
															</s:iterator>
														</s:if>
													</s:iterator>
													<tr>
														<td colspan="3">	
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td width="32px;" />
																	<s:if test="%{writeDisabled != 'disabled'}">
																		<td style="padding-top:4px;">
																			<s:if test="%{#templateSsid.ssidProfile.userProfileDefault!=null || #templateSsid.ssidProfile.userProfileSelfReg!=null || (#templateSsid.ssidProfile.radiusUserProfile!=null && #templateSsid.ssidProfile.radiusUserProfile.size>0)}">
																				<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: showUserProfileSelectDialog('<s:property value="%{#templateSsid.ssidProfile.id}" />', '', null);" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
																			</s:if>
																			<s:else>
																				<a class="npcLinkAEmpty" href="javascript:void(0);" onClick="javascript: showUserProfileSelectDialog('<s:property value="%{#templateSsid.ssidProfile.id}" />', '', null);" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
																			</s:else>
																		</td>
																	</s:if>
																	<s:else>
																		<td style="padding-top:4px;">
																			<s:if test="%{#templateSsid.ssidProfile.userProfileDefault!=null || #templateSsid.ssidProfile.userProfileSelfReg!=null || (#templateSsid.ssidProfile.radiusUserProfile!=null && #templateSsid.ssidProfile.radiusUserProfile.size>0)}">
																				<a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
																			</s:if>
																			<s:else>
																				<a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
																			</s:else>
																		</td>
																	</s:else>
																</tr>
															</table>
														</td>
														<td>	
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td width="32px;" />
																	<s:if test="%{writeDisabled != 'disabled'}">
																		<td style="padding-top:4px;">
																			<s:if test="%{#templateSsid.ssidProfile.userProfileDefault!=null || #templateSsid.ssidProfile.userProfileSelfReg!=null || (#templateSsid.ssidProfile.radiusUserProfile!=null && #templateSsid.ssidProfile.radiusUserProfile.size>0)}">
																				<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfileVlanMappingPanel({type: 'ssid', id: '<s:property value="%{#templateSsid.ssidProfile.id}" />', parentOpen: false});" title="<s:text name="config.networkpolicy.userprofile.vlan.mapping"/>" ><span><s:text name="config.networkpolicy.userprofile.vlan.mapping"/></span></a>
																			</s:if>
																		</td>
																	</s:if>
																	<s:else>
																		<td style="padding-top:4px;">
																			<s:if test="%{#templateSsid.ssidProfile.userProfileDefault!=null || #templateSsid.ssidProfile.userProfileSelfReg!=null || (#templateSsid.ssidProfile.radiusUserProfile!=null && #templateSsid.ssidProfile.radiusUserProfile.size>0)}">
																				<a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.userprofile.vlan.mapping"/>" ><span><s:text name="config.networkpolicy.userprofile.vlan.mapping"/></span></a>
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
										
										<s:if test="%{#templateSsid.ssidProfile.accessMode==2 && #templateSsid.ssidProfile.enablePpskSelfReg}">
										<s:if test="%{!enableClientManagement || !#templateSsid.ssidProfile.enableProvisionPrivate || !#templateSsid.ssidProfile.enableSingleSsid }">
											<tr class="npcList"><td height="2px" colspan="5"></td></tr>
											<tr class="npcList"><td>&nbsp;</td>
												<td valign="top" style="padding-top: 11px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td rowspan="2" class="imageTd">
																<s:if test="%{#templateSsid.ssidProfile.radioMode == 1}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-2-4.png" />"
																		width="30" height="30" alt="11bg" title="11bg" />
																</s:if>
																<s:elseif test="%{#templateSsid.ssidProfile.radioMode == 2}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-5.png" />"
																		width="30" height="30" alt="11a" title="11a" />
																</s:elseif>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid.png" />"
																		width="30" height="30" alt="both" title="both" />
																</s:else>
															</td>
															<td class="smallTd">
																&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskOpenSsid}" />">
																<s:property value="%{#templateSsid.ssidProfile.ppskOpenSsidSubstr}" /></span>
															</td>
														</tr>
														<tr>
															<td class="smallTd">
																&nbsp; Open</td>
														</tr>
													</table>
												</td>
												<td style="padding-top: 11px;">
													<table cellspacing="0" cellpadding="0" border="0">
													   <s:if test="%{!#templateSsid.ssidProfile.enabledIDM}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-hiveap-ppsk30x30.png" />"
																	width="30" height="30" alt="Private PSK Server" title="Private PSK Server" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}" />);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="javascript:selectPpskServerIpDlg(<s:property value="%{#templateSsid.ssidProfile.id}" />);"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																				</td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskServer==null && #templateSsid.ssidProfile.blnBrAsPpskServer==false}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																			</tr>
																			<s:if test="%{#templateSsid.ssidProfile.blnBrAsPpskServer}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:text name="config.v2.select.ssid.profile.ppsk.br"/>"><s:text name="config.v2.select.ssid.profile.ppsk.br"/></span></td>
																				</tr>
																			</s:if>
																			<s:if test="%{#templateSsid.ssidProfile.ppskServer!=null}">
																				<tr>
																					<td class="smallTd">&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ppskServer.hostName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskServer.hostNameSubstr}" /></span></td>
																				</tr>
																			</s:if>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														</s:if>
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
																	width="30" height="30" alt="CWP" title="CWP" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, true ,false);"><s:text name="config.networkpolicy.ssid.list.self.pskcwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, true ,false);"><span title="<s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.ppskECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.ppskECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskcwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.ppskECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														<s:if test="%{#templateSsid.ssidProfile.ppskECwp!=null && #templateSsid.ssidProfile.ppskECwp.ppskServerType==1}">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
																	width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
															</td>
															<s:if test="%{#templateSsid.ssidProfile.enabledIDM}">
																<s:if test="%{writeDisabled != 'disabled'}">
																	<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 1);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignment.id}" />, 1);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:if>
																<s:else>
																	<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:else>
															</s:if>
															<s:else>
																<s:if test="%{writeDisabled != 'disabled'}">
																	<td id="radiusTD2_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignmentPpsk==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 2);"><s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.id}" />, 2);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:if>
																<s:else>
																	<td id="radiusTD2_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																		<s:if test="%{#templateSsid.ssidProfile.radiusAssignmentPpsk==null}">
																			<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver"/></a>
																		</s:if>
																		<s:else>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignmentPpsk.radiusNameSubstr}" /></span></a>
																					</td>
																				</tr>
																				<tr>
																					<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/></td>
																				</tr>
																			</table>
																		</s:else>
																	</td>
																</s:else>
															</s:else>
														</tr>
														</s:if>
														
													</table>
												</td>
												<td colspan="3"></td>
											</tr>
										</s:if>
										</s:if>
										<s:if test="%{enableClientManagement && #templateSsid.ssidProfile.accessMode==1 && #templateSsid.ssidProfile.enableProvisionPersonal}">
											<tr class="npcList"><td height="2px" colspan="5"></td></tr>
											<tr class="npcList"><td>&nbsp;</td>
												<td valign="top" style="padding-top: 11px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td rowspan="2" class="imageTd">
																<s:if test="%{#templateSsid.ssidProfile.radioMode == 1}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-2-4.png" />"
																		width="30" height="30" alt="11bg" title="11bg" />
																</s:if>
																<s:elseif test="%{#templateSsid.ssidProfile.radioMode == 2}">
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-5.png" />"
																		width="30" height="30" alt="11a" title="11a" />
																</s:elseif>
																<s:else>
																	<img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid.png" />"
																		width="30" height="30" alt="both" title="both" />
																</s:else>
															</td>
															<td class="smallTd">
																&nbsp;<span title="<s:property value="%{#templateSsid.ssidProfile.ssidPPSKKey}" />">
																<s:property value="%{#templateSsid.ssidProfile.wpaOpenSsidSubstr}" /></span>
															</td>
														</tr>
														<tr>
															<td class="smallTd">
																&nbsp; Open</td>
														</tr>
													</table>
												</td>
												<td style="padding-top: 11px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
																	width="30" height="30" alt="CWP" title="CWP" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.wpaECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, false ,true);"><s:text name="config.networkpolicy.ssid.list.self.wpacwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="openCwpListDialog(1, <s:property value='ssidProfile.id'/>, false, true);"><span title="<s:property value="%{#templateSsid.ssidProfile.wpaECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.wpaECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.wpaECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td>
																	<s:if test="%{#templateSsid.ssidProfile.wpaECwp==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.wpacwp"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);"
																						onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.wpaECwp.cwpName}" />"><s:property value="%{#templateSsid.ssidProfile.ppskECwp.cwpNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:property value="%{#templateSsid.ssidProfile.wpaECwp.registrationTypeName}" /></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														<s:if test="%{#templateSsid.ssidProfile.wpaECwp!=null }">
														<tr>
															<td>
																<img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
																	width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
															</td>
															<s:if test="%{writeDisabled != 'disabled'}">
																<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																	<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, '', 1);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="showRadiusServerSelectDialog(<s:property value="%{#templateSsid.ssidProfile.id}" />, <s:property value="%{#templateSsid.ssidProfile.radiusAssignment.id}" />, 1);"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:if>
															<s:else>
																<td id="radiusTD1_<s:property value="%{#templateSsid.ssidProfile.id}" />">
																	<s:if test="%{#templateSsid.ssidProfile.radiusAssignment==null}">
																		<a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver"/></a>
																	</s:if>
																	<s:else>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><span title="<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />"><s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
																				</td>
																			</tr>
																			<tr>
																				<td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
																			</tr>
																		</table>
																	</s:else>
																</td>
															</s:else>
														</tr>
														</s:if>
													</table>
												</td>
												<td colspan="3"></td>
											</tr>
										</s:if>
										<tr>
											<td height="4px"></td>
										</tr>
									</s:if>
								</s:iterator>
							</table>
						</td>
					</tr>
					
					<tr>
						<td height="10px" class="smallTd">&nbsp;</td>
					</tr>
					</s:if>
					<s:else>
					<tr>
						<td align="center">
						    <div class="chooseNote">Click <span style="font-weight:bold;">choose</span> to add SSIDs to your network.</div>
						</td>
					</tr>
					</s:else>
					</s:if>
					<!-- LAN Profiles for Routing-->
					<s:if test="%{dataSource.configType.routerOrSwitchContained}">
                    <tiles:insertDefinition name="portTemplate4NetworkPolicy" />
                    <tiles:insertDefinition name="access4NetworkPolicy" />
					</s:if>
					<!-- vlan settings -->
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="npcHead1" style="padding-left: 35px; padding-right: 20px;">
											<s:text name="config.configTemplate.vlanSettingTitle" />
										</td>
										<s:if test="%{writeDisabled != 'disabled'}">
											<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btMgtVlanEdit" style="visibility: hidden;" onclick="chooseMgtVlan();" title="<s:text name="config.networkpolicy.button.edit"/>"><span><s:text name="config.networkpolicy.button.edit"/></span></a></td>
										</s:if>
										<s:else>
											<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.edit"/>"><span><s:text name="config.networkpolicy.button.edit"/></span></a></td>
										</s:else>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td style="padding: 4px 4px 4px 90px;" valign="top" class="normalTd">
								<table border="0" cellspacing="0" cellpadding="0" >
									<tr>
											<td class="npcHead2" width="140px" nowrap="nowrap"><s:text
												name="config.configTemplate.vlan" /></td>
											<td width="150px"><span id="spanV_vlan">
												<s:property value="%{dataSource.vlan.vlanName}"/></span>
											</td>
										<td style="padding-left: 25px; display:<s:property value="%{hideNativeVlan}"/>" class="npcHead2" width="150px" nowrap="nowrap" ><s:text
											name="config.configTemplate.vlanNative" /></td>
										<td id="switchOnlyHideNaviteVlan" style="display:<s:property value="%{hideNativeVlan}"/>"><span id="spanV_vlanNative">
											<s:property value="%{dataSource.vlanNative.vlanName}"/></span>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						
						<s:if test="%{dataSource.configType.routerContained}">
						<tr>
							<td height="10px"/>
						</tr>
						<tr>
							<td id="configTemplateVlanNetworkMapping">
						 		<tiles:insertDefinition name="templateVlanNetworkMapping" />
						 	</td>
						 </tr>
						</s:if>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
								    <tr>
								    <s:if test="%{dataSource.configType.routerContained}">
								    <td width="400px">
									    <table>
											<tr>
												<td class="npcHead1" style="padding-left: 35px;padding-right: 20px;"><s:text name="config.networkpolicy.firewall.title"/></td>
												<s:if test="%{writeDisabled != 'disabled'}">
													<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btFirewallChoose" style="visibility: hidden;" onclick="selectFirewallPolicy(null);" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
												</s:else>
											</tr>
											<tr>
												<td colspan="2" style="padding-left: 30px" id="fwPolicyNameTd">
													<s:if test="%{dataSource.fwPolicy!=null}">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="imageTd" valign="middle"><img src="<s:url value="/images/hm_v2/profile/HM-icon-firewall.png" />" style="padding-left: 2px;" width="30" height="30" alt="firewall" title="firewall" /></td>
																<td style="padding-left: 5px; vertical-align: top;">
																	<a class="npcLinkA" href="javascript:void(0);" onclick='editFirewallPolicyInPanel(<s:property value="%{dataSource.fwPolicy.id}"/>)'><s:property value="dataSource.fwPolicy.policyName"/></a>
																</td>
															</tr>
														</table>
													</s:if>
													<s:else>
													   <s:if test="%{dataSource.bonjourGw!=null}">
													       <span style="height:43px; display:inline-block;"></span>
													   </s:if>
													</s:else>
												</td>
											</tr>
									    </table>
								    </td>
								    </s:if>
								    <td>
									    <table>
											<tr>
												<td class="npcHead1" style="padding-right: 20px; <s:if test='%{!dataSource.configType.routerContained}'>padding-left: 35px;</s:if>"><s:text name="config.networkpolicy.bonjour.title"/></td>
												<s:if test="%{writeDisabled != 'disabled'}">
													<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btBoujourGtChoose" style="visibility: hidden;" onclick="addRemoveBonjourGw();" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
												</s:else>
											</tr>
											<tr>
												<td colspan="2" id="bonjourGwTd" <s:if test="%{!dataSource.configType.routerContained}">style="padding-left: 30px"</s:if>>
													<s:if test="%{dataSource.bonjourGw!=null}">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="imageTd" valign="middle"><img src="<s:url value="/images/hm_v2/profile/HM-icon-bonjour-30x30.png" />" style="padding-left: 2px;" width="30" height="30" alt="bonjourGateWay" title="BonjourGateWay" /></td>
																<td style="padding-left: 5px; vertical-align: top;">
																	<a class="npcLinkA" href="javascript:void(0);" onclick='viewBonjourGw(<s:property value="%{dataSource.bonjourGw.id}"/>)'><s:property value="dataSource.bonjourGw.bonjourGwName"/></a>
																</td>
															</tr>
														</table>
													</s:if>
                                                    <s:else>
                                                       <s:if test="%{dataSource.fwPolicy!=null}">
                                                           <span style="height:43px; display:inline-block;"></span>
                                                       </s:if>
                                                    </s:else>													
												
											</tr>
									    </table>
								    </td>
								    </tr>
								</table>
							</td>
						</tr>
					<!-- VPN settings -->
					<%-- <s:if test="%{!dataSource.blnBonjourOnly}"> --%>
					<s:if test="%{dataSource.configType.routerContained || dataSource.configType.wirelessContained}">
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<s:if test="%{dataSource.configType.routerContained}">
										<td class="npcHead1" style="padding-left: 35px;padding-right: 20px;"><s:text name="config.networkpolicy.vpnL3.title"/></td>
									</s:if>
									<s:else>
										<td class="npcHead1" style="padding-left: 35px;padding-right: 20px;"><s:text name="config.networkpolicy.vpnL2.title"/></td>
									</s:else>
									<s:if test="%{writeDisabled != 'disabled'}">
										<td class="npcButton"><a href="javascript:void(0);" onclick="selectVpnBtn();" id="btVpnChoose" style="visibility: hidden;" class="btCurrent" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
									</s:if>
									<s:else>
										<td class="npcButton"><a href="javascript:void(0);" onclick="showWarmMessage();" class="btCurrent" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
									</s:else>
								</tr>
							</table>
						</td>
					</tr>
					<s:if test="%{dataSource.vpnService!=null}">
					<tr>
						<td style="padding-left: 30px">
							<table cellspacing="0" cellpadding="0" border="0">
								
								<tr>
									<td colspan="2" class="npcHead2" width="200px"><s:text name="config.networkpolicy.vpn.list.name" /></td>
									<td class="npcHead2" width="140px"><s:text name="config.networkpolicy.vpn.list.gateway" /></td>
									<td class="npcHead2" width="140px"><s:text name="config.networkpolicy.vpn.list.external.ipaddress" /></td>
									<s:if test="%{dataSource.configType.routerContained}">
										<td class="npcHead2" width="140px"><s:text name="config.networkpolicy.vpn.list.wan.ipaddress" /></td>
										<td class="npcHead2" width="100px"><s:text name="config.networkpolicy.vpn.list.prototcol" /></td>
									</s:if>
									<s:else>
										<td class="npcHead2" width="140px"><s:text name="config.networkpolicy.vpn.list.mgt0.ipaddress" /></td>
									</s:else>
									
									<td width="140px"></td>
								</tr>
								
								<tr class="npcList">
									<s:if test="%{writeDisabled != 'disabled'}">
										<td class="imageTd"><img src="<s:url value="/images/hm_v2/profile/hm-icon-vpn.png" />" width="30" height="30" alt="VPN" title="VPN" /></td>
										<td class="npcLinkA" style="padding-left: 5px" width="170px">
											<a class="npcLinkA" href="javascript:void(0);" onclick='editVpnService(<s:property value="dataSource.vpnService.id"/>,<s:property value="dataSource.configType.routerContained" />)'><s:property value="dataSource.vpnService.profileName"/></a>
										</td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnGatewaysString" escape="false"/></td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnExternalIpAddressString" escape="false"/></td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnIpAddressString" escape="false"/></td>
										<s:if test="%{dataSource.configType.routerContained}">
											<td class="normalTd"><s:property value="dataSource.vpnService.vpnPrototcolString" escape="false"/></td>
										</s:if>
										<s:if test="%{hasVpnTopology}">
											<td ><a href="javascript:void(0);" class="npcLinkA" onclick="openVpnTopologyPanel(<s:property value="dataSource.vpnService.id"/>)">VPN Topology</a></td>
										</s:if>
									</s:if>
									<s:else>
										<td class="imageTd"><img src="<s:url value="/images/hm_v2/profile/hm-icon-vpn.png" />" width="30" height="30" alt="VPN" title="VPN" /></td>
										<td class="npcLinkA" style="padding-left: 5px" width="170px">
											<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:property value="dataSource.vpnService.profileName"/></a>
										</td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnGatewaysString" escape="false"/></td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnExternalIpAddressString" escape="false"/></td>
										<td class="normalTd"><s:property value="dataSource.vpnService.vpnIpAddressString" escape="false"/></td>
										<s:if test="%{dataSource.configType.routerContained}">
											<td class="normalTd"><s:property value="dataSource.vpnService.vpnPrototcolString" escape="false"/></td>
										</s:if>
										<s:if test="%{hasVpnTopology}">
											<td ><a href="javascript:void(0);" class="npcLinkA" onclick="showWarmMessage();">VPN Topology</a></td>
										</s:if>
									</s:else>
								</tr>
							</table>
						</td>
					</tr>
					</s:if>
					<s:else>
						<tr>
							<td align="center">
							    <div class="chooseNote">Click <span style="font-weight:bold;">choose</span> to add a VPN to your network.</div>
							</td>
						</tr>
					</s:else>
					</s:if>
					<tr>
						<td style="padding-left: 30px">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="npcHead1" style="padding-right: 20px;">&nbsp;<s:text name="config.networkpolicy.advance.title"></s:text></td>
									<td class="npcButton" ><a href="javascript:void(0);" onClick="editMgtAdvancedSetting();" class="btCurrent" title="<s:text name="config.networkpolicy.button.edit"/>"><span><s:text name="config.networkpolicy.button.edit"/></span></a></td>
								</tr>
							</table>
						</td>
					</tr>
					<!-- LAN Profiles for Wireless-->
                    <s:if test="%{configType4Port == 3}">
                    <tiles:insertDefinition name="portTemplate4NetworkPolicy" />
                    <tiles:insertDefinition name="access4NetworkPolicy" />
                    </s:if>
				</table>
			</td>
		</tr>
	</table>
	</s:form>
</div>

<!-- QOS Setting start -->
 
<!--<div id="qosSettingNewPanel" style="display:none;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" width="950px"></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="ml"></td>
		<td class="mm">
			<table class="innerPage" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>	
						<div id="qosSettingNewPanelBD"></div>
		            </td>
		        </tr>
			</table>
		</td>
		<td class="mr"></td>
	</tr>
</table>
</td></tr>
	<tr><td>
	
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td class="bm" width="950px"></td><td class="br"></td>
		</tr>
	</table>
	
</td></tr>
</table>
</div>

<script type="text/javascript">
	var qosSettingNewPanel = null;
	
	YAHOO.util.Event.onDOMReady(function(){
		//Create qos setting panel
		var div = document.getElementById('qosSettingNewPanel');
		qosSettingNewPanel = new YAHOO.widget.Panel(div, { width:"950px", underlay: "none", visible:false,draggable:false,constraintoviewport:true,modal:true,zIndex:100,fixedcenter:false, close:false } );
		qosSettingNewPanel.render(document.body);
		div.style.display = "";
	});
	
	function showUpQosSettingNewDialog() {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=editQoS&jsonMode=true" + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditQoS, failure : resultDoNothing, timeout: 60000}, null);	
	}
	
	var succEditQoS=function(o){
		set_innerHTML("qosSettingNewPanelBD",o.responseText);
		if(null != qosSettingNewPanel){
			qosSettingNewPanel.center();
			qosSettingNewPanel.cfg.setProperty('visible', true);
		}
	}
	
	function hideQosSettingNewDialog() {
		if(null != qosSettingNewPanel){
			qosSettingNewPanel.cfg.setProperty('visible', false);
			set_innerHTML("qosSettingNewPanelBD","");
		}
	}
	
	function saveQosSettingNewDialog(){
		if (!validateSlaSettings()){
			return false;
		}
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=saveQoS&jsonMode=true&ignore="+new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("networkPolicyQoS"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveQoS, failure : resultDoNothing, timeout: 60000}, null);	
	}
	
	var succSaveQoS=function(o){
		hideQosSettingNewDialog();
	}
	
</script>
--><!-- QOS Setting end -->

<div id="userProfileSelectPanelId" style="display:none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="100%">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" id="tdUM" style="width:520px;"></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ml"></td>
			<td class="mm">
		<div id="userProfileSelectPanelContent"></div>
	</td>
			<td class="mr"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td class="bm" id="tdBM" style="width:520px;"></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
	
</div>

<!-- script code of wx start -->
<script type="text/javascript">
	var userProfileSelectDialog = null;
	var requestedUserProfiles = false;
	
	function prepareUserProfileSelectDialog() {
		//used to initialize the user profile popup dialog
		var div = document.getElementById('userProfileSelectPanelId');
		userProfileSelectDialog = new YAHOO.widget.Panel(div, {
			width:"600px",
			underlay: "none",
			visible:false,
			draggable:false,
			close:false,
			modal:true,
			constraintoviewport:true,
			zIndex:999
			});
		userProfileSelectDialog.render(document.body);
		div.style.display = "";
	}
	
	function onlyPopupUserProfileSelectDialog() {
		userProfileSelectDialog.cfg.setProperty('visible', true);
		userProfileSelectDialog.center();
	}
	
	function showUserProfileSelectDialog(ssidId, addedUserProfileId, args){
		if(null != userProfileSelectDialog && requestedUserProfiles == false){
			requestedUserProfiles = true;
			fetchUserProfile2ListPage(ssidId, addedUserProfileId, args);
		}
	}
	
	function hideUserProfileSelectDialog(){
		if(null != userProfileSelectDialog){
			userProfileSelectDialog.cfg.setProperty('visible', false);
		}
	}
	
	function fetchUserProfile2ListPage(ssidId, addedUserProfileId, args) {
		var upTabId = '';
		var upType = 0;
		if (args != null && args.upTabId) {
			upTabId = args.upTabId;
		}
		if (args != null && args.upType) {
			upType = args.upType;
		}
		var url = "<s:url action='ssidProfilesSimple' includeParams='none' />?operation=listUserProfiles"
			 + "&ssidId="+ssidId
			 + "&userProfileSubTabId="+upTabId
			 + "&addedUserProfileId="+addedUserProfileId
			 + "&ssidUserProfileType="+upType
			 + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchUserProfile2ListPage, failure : failFetchUserProfile2ListPage, timeout: 60000}, null);
	}
	var succFetchUserProfile2ListPage = function(o) {
		requestedUserProfiles = false;
		set_innerHTML("userProfileSelectPanelContent",o.responseText);
		YAHOO.util.Event.onContentReady("userProfileSelectPanelContent", onlyPopupUserProfileSelectDialog, this);
	}
	var failFetchUserProfile2ListPage = function(o) {
		requestedUserProfiles = false;
	}
	
	function selectUserProfile4Lan(accessId, addedUserProfileId, flag, support4LAN, args) {
		if(null == userProfileSelectDialog 
				|| requestedUserProfiles == true){
			return;
		}
		var upType = 0;
		if (args && args.upType) {
			upType = args.upType;
		}
	    var limitType = 0;
	    if(Get('networkPolicyTemplate_configType4Port')) {
	        limitType = Get('networkPolicyTemplate_configType4Port').value;
	    }
		requestedUserProfiles = true;
	    var url = "<s:url action='portAccess' includeParams='none' />?operation=showUserProfile4Access"
            +"&selectedAccessId="+accessId
			 + "&addedUserProfileId="+addedUserProfileId
			 + "&configPhoneData="+flag
			 + "&support4LAN="+support4LAN
			 + "&upSelectType="+upType
			 + "&limitType="+limitType
			 +"&jsonMode=true&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchSelectUserProfile4LanPage, failure : failFetchSelectUserProfile4LanPage, timeout: 60000}, null);
	}

	var succFetchSelectUserProfile4LanPage = function (o){
		requestedUserProfiles = false;
		set_innerHTML("userProfileSelectPanelContent",o.responseText);
		YAHOO.util.Event.onContentReady("userProfileSelectPanelContent", onlyPopupUserProfileSelectDialog, this);
	}
	var failFetchSelectUserProfile4LanPage = function(o) {
		requestedUserProfiles = false;
	}
	
	var succFinishSelectUserProfile4Lan = function(o) {
		try {
			eval("var details = " + o.responseText);
			if (details.resultStatus) {
				hideSubDialogOverlay();
			} else {
				Get("errNoteUserProfileSelectLan").innerHTML=details.errMsg;
			}
			fetchConfigTemplate2Page(true);
		} catch (e) {
			Get("errNoteUserProfileSelectLan").innerHTML='<s:text name="error.unknown" />';
			fetchConfigTemplate2Page(true);
		}
	}

</script>
<!-- script code of wx end -->

<!-- please let the code the end of the file -->
<script type="text/javascript">
	function prepareBtPerrmit(){
		var DOM = YAHOO.util.Dom;
		<s:if test="%{savePermit==false}">
			showHideNetworkPolicySaveBT(false);
			DOM.setStyle("btAddRemoveSsid", "visibility", "hidden");
			DOM.setStyle("btMgtVlanEdit", "visibility", "hidden");
			DOM.setStyle("btAddRemoveLan", "visibility", "hidden");
			<s:if test="%{dataSource.configType.routerContained}">
				DOM.setStyle("btFirewallChoose", "visibility", "hidden");
			</s:if>
			DOM.setStyle("btBoujourGtChoose", "visibility", "hidden");
			DOM.setStyle("btVpnChoose", "visibility", "hidden");
			
		</s:if>
		<s:else>
			showHideNetworkPolicySaveBT(true);
			DOM.setStyle("btAddRemoveSsid", "visibility", "visible");
			DOM.setStyle("btMgtVlanEdit", "visibility", "visible");
			DOM.setStyle("btAddRemoveLan", "visibility", "visible");
			<s:if test="%{dataSource.configType.routerContained}">
				DOM.setStyle("btFirewallChoose", "visibility", "visible");
			</s:if>
			DOM.setStyle("btBoujourGtChoose", "visibility", "visible");
			DOM.setStyle("btVpnChoose", "visibility", "visible");
			
		</s:else>
	}
	function initClickEventForPorts() {
		
		var accessColorSet = eval("<s:property value='colortSets'/>"), 
		    colorPrefix = 'colorLabel_';
		var $content, label;
		for(var index=0; index<accessColorSet.length; index++) {
			var colorObj = accessColorSet[index];
			if(colorObj && colorObj.accessProfileId && colorObj.color) {
				$content = $('td#accessesContent');
				if($content.length == 1) {
					$label = $content.find('span#colorLabel_'+colorObj.accessProfileId);
					if($label.length == 1) {
						$label.css('background-color', colorObj.color);
					}
				}
			}
		}
		$('td.portTemplate').on('click', 'span.arrowRight', function(event) {
			var self = $(this);
			
			self.toggleClass('arrowDown');
			var templateId=self.attr('tmpId'), suffixId = self.attr('ref'), portNum = self.attr('portNum'), deviceType = self.attr('deviceType'), models = self.attr('models');
			var deviceM = models.split(',');
			for(a in deviceM) {
				deviceM[a] = parseInt(deviceM[a]);
			}
			var tmpl = $('#wiredPortTmpl_'+suffixId);
			var index = -1;
			if(suffixId.indexOf("_") > 0){
				index=suffixId.split("_")[1];
			}
			if(self.hasClass('arrowDown')) {
				tmpl.show();
			} else {
				tmpl.hide();
			}
			if(deviceType) { deviceType = parseInt(deviceType);}
			if(portNum) { portNum = parseInt(portNum);}
		    var groupSection = $('#wirePortGroupSection_'+suffixId);
		    if(groupSection.html().trim() == '') {
		    	groupSection.portsConfig({
		    		templateProfileId: templateId,
		    		deviceModels: deviceM,
		    		deviceType: deviceType,
		    		tmplId: 'wirePortGroupTmpl', mode: 5,
		    		tooltip: 'wiredPortTooltip_'+suffixId,
		    		portChannel: portNum > 5 ? {chk: 'enableLinkAggChk_'+suffixId, input: 'portChannel_'+suffixId} : null,
		    		editEvent: {
		    			editableFn: function() {
		    				var writeDisabled = '<s:property escape="false" value="writeDisabled"/>';
		    				if(writeDisabled != 'disabled'){
		    					$('#assignPorts_'+suffixId).attr('disabled', false);
			    				$('#disabledEditAPSAnchor_'+suffixId).show();
			    				$('#editAPSAnchor_'+suffixId).hide();
			    				
			    				$('#enableDeselectAnchor_'+suffixId).show();
			    				$('#disabledDeselectAnchor_'+suffixId).hide();
			    				
			    				// clear the selection status on other port template 
	                            $('div[id^="wirePortGroupSection_"]')
	                            .not('#wirePortGroupSection_'+suffixId)
	                            .not(':empty').each(function(jndex){
	                                $('#'+$(this).attr('id')).portsConfig('clear');
	                            });
			    				
	                            /*if($('#wirePortGroupSection_'+suffixId).portsConfig('existConfiguredPorts')) {
	                            	$('#resetPorts_'+suffixId).attr('disabled', false);
	                            }*/
		    				}
		    			},
		    			uneditableFn: function() {
		    				$('#assignPorts_'+suffixId).attr('disabled', true);
		    				/*$('#resetPorts_'+suffixId).attr('disabled', true);*/
		    				$('#disabledEditAPSAnchor_'+suffixId).hide();
		    				$('#editAPSAnchor_'+suffixId).show();
		    				
		    				$('#enableDeselectAnchor_'+suffixId).hide();
		    				$('#disabledDeselectAnchor_'+suffixId).show();
		    			}
		    		},
		    		errorFn: function(err){
		    			hm.util.reportFieldError({id: 'errorRow_'+suffixId}, err);
		    		}});
		    	// update port status
		    	$.getJSON("portConfigure.action", {operation: 'retrieveStatus', id: templateId, ignore: new Date().getTime()}, function(result) {
		    		if(result.succ && result.status) {
		    			groupSection.portsConfig('update', result.status);
		    			groupSection.portsConfig('updatePortColors', accessColorSet);
		    		}
		    		if(result.succ && result.desc) {
		    			new Template('wirePortGroupDescTmpl', {array: eval("("+result.desc+")")})
		    			.render('wiredPortDescTmpl_'+suffixId)
		    			.done(function() {
		    				// add expand/collapse for click
		    				var descTitle = $('#wiredPortDescTmpl_'+suffixId).find('p.portDesc');
		    				if(descTitle.length) {
		    					descTitle.click(function(){
		    						$(this).toggleClass('expanded');
		    						$(this).next().toggle();
		    					});
		    				}
		    			});
		    		}
		    		if(result.succ && result.poe) {
		    			groupSection.portsConfig('updatePortClaxx', result.poe);
		    		}
		    	});
			    $('#assignPorts_'+suffixId).click(function(){
			    	//popup the access profile selected dialog
			    	if(portNum > 5 && !groupSection.portsConfig('validatePortChannel')) {
			    		return;
			    	}
			    	var accessProfileId = groupSection.portsConfig('getAccessProfileId'), urlStr = "";
			    	if(accessProfileId > 0) {
			    		urlStr += "&selectedAccessId="+ accessProfileId;
			    	}
			    	
			    	if(index >= 0){
			    		addRemoveAccess(null, templateId, deviceType, portNum, urlStr,index);
			    	}else{
			    		addRemoveAccess(null, templateId, deviceType, portNum, urlStr);
			    	}
			    	
			    	return false;
			    });
			    /*$('#resetPorts_'+suffixId).click(function(){
			    	clearAccessButton(templateId, portNum, deviceType, index);
			    	return false;
			    });*/
			    $('#enableDeselectAnchor_'+suffixId).click(function(){
			    	groupSection.portsConfig('clear');
			    	return false;
			    });
		    }
			event.stopPropagation();
		});
		// expand the specific port template
		var expandId = '<s:property value="expandPortTemplateId"/>';
		var tmpIndex = '<s:property value="tmpIndex"/>';
		if(expandId.length > 0) {
			if(tmpIndex > -1){
				$('td.portTemplate span.arrowRight[ref="'+expandId+'_'+tmpIndex+'"]').click();
			}else{
				$('td.portTemplate span.arrowRight[ref="'+expandId+'"]').click();
			}
			
		}
	}
	
	function prepareNetworkDrawerAbout() {
		prepareBtPerrmit();
		if (userProfileSelectDialog == null) {
			prepareUserProfileSelectDialog();
		}
		initClickEventForPorts();
	}
	
	window.setTimeout("prepareNetworkDrawerAbout()", 100);
	
</script>
<!-- ====================== VPN topology begin ====================== -->
<div>
<div id="vpnTitle" class="leftNavH1"></div>
<table id="vpnTable" border="0" cellspacing="0" cellpadding="0" width="100%">
</table>
</div>
<div id="vpnTopologyPanelElement" style="display: none;">
	<div class="bd">
		<iframe id="vpnTopology" name="vpnTopology" width="0" height="0"
			frameborder="0" style="background-color: #fff;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var vpnTopologyPanel;

function createVpnTopologyPanel(width, height){
	var div = document.getElementById("vpnTopologyPanelElement");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vpnTopology");
	iframe.width = width;
	iframe.height = height;
	vpnTopologyPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", 
							fixedcenter:true, 
							visible:false, 
							constraintoviewport:true } );
	vpnTopologyPanel.render(document.body);
	div.style.display="";
	vpnTopologyPanel.beforeHideEvent.subscribe(clearVpnTopologyData);
	var resize = createResizer("vpnTopologyPanelElement");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, vpnTopologyPanel, true);
	resize.on("endResize", function(args){
		vpnTopologyPanelResizeCallback();
	}, vpnTopologyPanel, true);
}

//Create Resize instance, binding it to the 'resizablepanel' DIV
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 650,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
function vpnTopologyPanelResizeCallback(){
	if(null != vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.location.reload();
	}
}
function clearVpnTopologyData(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "none";
	}
	if(vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.onHidePage();
	}
}
function openVpnTopologyPanel(id){
	if(null == vpnTopologyPanel){
		var width = YAHOO.util.Dom.getViewportWidth();
		var height = YAHOO.util.Dom.getViewportHeight();
		createVpnTopologyPanel(width*0.8, height*0.8);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "";
	}
	vpnTopologyPanel.show();
	var iframe = document.getElementById("vpnTopology");
	iframe.src ="<s:url value='vpnServices.action' includeParams='none' />?operation=initVpnTopologyPanel"
			+"&jsonMode=true&id="+id+"&pageId="+new Date().getTime();
}
function updateVpnTopologyPanelTitle(str){
	if(null != vpnTopologyPanel){
		var iframeTopologyDom = hm.util.getIFrameDOMById("vpnTopology");
		iframeTopologyDom.updateTopologyDialogTitle("<s:text name='config.vpn.service.topology'/>"+" - "+str);
	}
}
var vpnTopologyIframeWindow;
</script>
<!-- ====================== VPN topology end ====================== -->

<!-- ============ Add/Remove Networks for LAN: start ============= -->
<div id="networksSelectPanelId" style="display: none;">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="100%">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="ul"></td>
				<td class="um" style="width: 500px;"></td>
				<td class="ur"></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="100%">
		<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="ml"></td>
				<td class="mm">
				<table class="innerPage" cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
						<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
							<td>
								<img src="images/hm_v2/profile/hm-icon-vlan-big.png" class="dialogTitleImg" style="padding-right:0;" />
							</td>
							<td>
								<span class="npcHead1" style="padding-left:0" id="networkDlgTitle">Choose Network Objects</span>
							</td>
							</tr>
						</table>
						</td>
						<td>
						     <a href="javascript: void(0);" onclick="hideSelectNetworkDialog(); return false;">
                               <img src="<s:url value="/images/cancel.png" />"
                                width="16" height="16" alt="Cancel" title="Cancel" class="dinl"/></a>
						</td>
					</tr>
					<tr>
						<td colspan="2" height="15px"/>
					</tr>
					<tr>
						<td colspan="2" >
							<div id="networksSelectPanelContent">
							</div>
						</td>
					</tr>
				</table>
				</td>
				<td class="mr"></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="100%">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="bl"></td>
				<td class="bm" style="width: 500px;"></td>
				<td class="br"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
<script type="text/javascript">
var networksDialog;
function initSelectNetworksDialog() {
	// create Dialog overlay
		var div = document.getElementById('networksSelectPanelId');
		networksDialog = new YAHOO.widget.Panel(div, {
			width:"520px",
			visible:false,
			fixedcenter:true,
			close: false,
			draggable:false,
			modal:true,
			constraintoviewport:true,
			underlay: "none",
			zIndex:1
			});
		  //Allow escape key to close box
	    var escListener = new YAHOO.util.KeyListener(document, { keys:27},                                 
	              { fn:hideSelectNetworkDialog,   
	                    scope:networksDialog,   
	                    correctScope:true } );
	    networksDialog.cfg.queueProperty("keylisteners", escListener); 
		networksDialog.render(document.body);
		div.style.display = "";
		YAHOO.util.Dom.setStyle(div, "border-width", "0");
}
function hideSelectNetworkDialog(){
	if(null != networksDialog){
		networksDialog.cfg.setProperty('visible', false);
	}
}
function showSelectNetworksDialog(title) {
	if(null == networksDialog) {
		initSelectNetworksDialog();
	}
	if(title) {
		Get("networkDlgTitle").innerHTML = title;
	}
	networksDialog.cfg.setProperty('visible', true);
}
function selectNetworks4Lan(lanId) {
	Get("networksSelectPanelContent").innerHTML = "";
	
	// associate with the Native VLAN in the 'Management Settings'
	var selectedVlanName = "";
	var nativeVlanName = Get("spanV_vlanNative");
	if(nativeVlanName) {
		var vName = nativeVlanName.innerHTML.replace("\n", "").trim();
		if(vName.length > 0) {
			selectedVlanName = "&selectedVlanName=" + vName; 
		}
	}
	var url = "<s:url action='lanProfiles' includeParams='none' />?operation=showNetworks4LAN"
			+ "&selectedLANId=" + lanId + selectedVlanName + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
			{success : fetchNetworksList, failure: failureFectch,timeout: 60000}, null);
}
var fetchNetworksList = function(o) {
	set_innerHTML("networksSelectPanelContent",o.responseText);
	showSelectNetworksDialog('Choose Network Objects');
}
var failureFectch = function(o) {
}
</script>
<!-- ============ Add/Remove Networks for LAN: end ============  -->
<script>
function selectVlans4Lan(lanId) {
	Get("networksSelectPanelContent").innerHTML = "";
    var selectedVlanName = "";
    var nativeVlanName = Get("spanV_vlanNative");
    if(nativeVlanName) {
        var vName = nativeVlanName.innerHTML.replace("\n", "").trim();
        if(vName.length > 0) {
            selectedVlanName = "&selectedVlanName=" + vName; 
        }
    }
    var url = "<s:url action='lanProfiles' includeParams='none' />?operation=showVlans4LAN"
            + "&selectedLANId=" + lanId + selectedVlanName + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
            {success : fetchVlanList, failure: failureFectch,timeout: 60000}, null);
}
var fetchVlanList = function(o) {
    set_innerHTML("networksSelectPanelContent",o.responseText);
    showSelectNetworksDialog('Choose VLAN Objects');	
}

function showWarmMessage(){
	warnDialog.cfg.setProperty('text', "<s:text name='error.readOnly.noPermission'/>");
	warnDialog.show();
}
</script>

<script>
function modifyVlanNetworkMapping(){
	if (Get("vlanNetworkMappingDetailTR").style.display!="") {
		Get("modifyVlanNetworkMappingIcon").src="images/expand_minus.gif";
		Get("vlanNetworkMappingDetailTR").style.display="";
	} else {
		Get("vlanNetworkMappingDetailTR").style.display="none";
		Get("modifyVlanNetworkMappingIcon").src="images/expand_plus.gif";
	}
}

function editVlanNetworkMappingVlan(vlanId){
	var url = '<s:url action="networkPolicy" includeParams="none"/>' 
		+ '?operation=editVlan'
		+ '&jsonMode=true'
		+ '&vlanId=' + vlanId
		+ '&ignore=' + new Date().getTime();
	openIFrameDialog(800,450, url);
}

function editVlanNetworkMappingNetwork(vlanId,networkId){
	if (networkId) {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveNetworkObjPage"
			+ "&preNetworkId=" + networkId
			+ "&preVlanId=" + vlanId
		 	+ "&ignore="+new Date().getTime();
	} else {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveNetworkObjPage"
			+ "&preVlanId=" + vlanId
		 	+ "&ignore="+new Date().getTime();
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkObjPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchNetworkObjPage = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-networks-40.png" width="40px" height="40px" '+
					'title="<s:text name="config.v2.select.vlanmap.profile.popup.title" />" class="dialogTitleImg" />'+
					'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.v2.select.vlanmap.profile.popup.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

function refreshNetworkObjPage(){
	if (!Get("configTemplateVlanNetworkMapping")){
		return false;
	}
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=refreshNetworkObjPage&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succRefreshNetworkObjPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succRefreshNetworkObjPage = function (o) {
	set_innerHTML('configTemplateVlanNetworkMapping', o.responseText);
}

function finishSelectNetworkObject(){
	var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
	document.forms["configTemplateNetworkSelectPage"].operation.value = "finishSelectNetworkObject";
	YAHOO.util.Connect.setForm(document.getElementById("configTemplateNetworkSelectPage"));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFinishSelectNetworkObject, failure : resultDoNothing, timeout: 60000}, null);
}

var succFinishSelectNetworkObject = function (o){
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		hideSubDialogOverlay();
		set_innerHTML('configTemplateVlanNetworkMapping', o.responseText);
		return;
	}
	var hideErrNotes = function () {
		hm.util.wipeOut('errNote', 800);
	}
	if(subDialogOverlay.cfg.getProperty('visible') == false) {
		openSubDialogOverlay();
	}
	hm.util.show("errNote");
	Get("errNote").className="noteError";
	Get("errNote").innerHTML=details.e;
	var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
};

function editNetworkObjectDialog(networkId, event) {
    // close this dialog
    hideSubDialogOverlay();
    // expand the subdrawer
    editNetworkObject(networkId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
	// this function will be called when back to the NetworkPolicy drawer from the sub drawer,
	// and destroy after invoked.
    networkPolicyCallbackFn = function() {
   		editVlanNetworkMappingNetwork(preVlanId, networkId);
    }
}

function cloneNetworkObjectDialog(networkId, event) {
    // close this dialog
    hideSubDialogOverlay();
    // expand the subdrawer
    cloneNetworkObject(networkId);
    
    // stop bubble!!!!
    hm.util.stopBubble(event);

    // init the callback function
	// this function will be called when back to the NetworkPolicy drawer from the sub drawer,
	// and destroy after invoked.
    networkPolicyCallbackFn = function() {
    	editVlanNetworkMappingNetwork(preVlanId, networkId);
    }
}


function cloneNetworkObject(networkId) {
	subDrawerCloneOperation="cloneNetworkObject";
    var networkArray = new Array();
    networkArray.push(networkId);
    var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=clone&selectedIds=" + networkArray 
	    + "&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNetworkObject, failure : resultDoNothing, timeout: 60000}, null);

}

function newNetworkObject(){
	subDrawerCloneOperation='';
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=new&jsonMode=true"
		+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNetworkObject, failure : resultDoNothing, timeout: 60000}, null);

	var preNetworkId=-1;
	var els = $("input[type=checkbox][name=selectNetworkId]:checked");
	if (els.length > 0) {
		preNetworkId = els[0].value;

	}
	networkPolicyCallbackFn = function() {
		editVlanNetworkMappingNetwork(preVlanId,preNetworkId);
    }
}

function editNetworkObject(networkId){
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=edit&contentShowType=subdrawer&jsonMode=true&id=" + networkId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditNetworkObject, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditNetworkObject = function(o) {
	subDrawerOperation = "updateNetworkObject";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit Network"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

var succNetworkObject = function(o) {
	subDrawerOperation= "createNetworkObject";

	hideSubDialogOverlay();
	// set the sub drawer title
    if(subDrawerCloneOperation == "cloneNetworkObject") {
	    accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone Network"));
    } else {
        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New Network"));
    }
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// fetch the page
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

</script>