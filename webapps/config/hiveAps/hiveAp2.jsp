<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.wlan.RadioProfile"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@page import="com.ah.bo.hiveap.HiveAPVirtualConnection"%>
<%@page import="com.ah.bo.hiveap.DeviceInfo"%>
<style>
.hint {
color: gray;
}
</style>
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script src="<s:url value="/js/hm.options.js" includeParams="none" />?v=<s:property value='verParam' />"></script>
<script
	src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>!window.jQuery.ui && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<style type="text/css">
#ap2ClassifierTagContainer td.listHead {
    background-color: #FFFFFF;
}

#ap2ClassifierTagContainer ul {
 padding-left: 100px;
 width: auto !important;
 }

 #ap2ClassifierTagContainer td.defaultContainer input {
    width: 100px;
}

#ap2ClassifierTagContainer div {
    float:left;
    width:33.3%;
 }

#ap2ClassifierTagContainer td.listHead{
border-bottom: 0 ;
}

#ap2ClassifierTagContainer li {
    text-decoration: none;
	line-height: none;
	background-image: none;
		line-height: 1.9em;
 }

#ap2ClassifierTagContainer li.tag-choice a.cancel-icon{
	top: 2px;
 }

</style>
<script>
function initWidgetGui() {
	if(!window.jQuery) {
		head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(!window.jQuery.ui) {
		head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
			"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
	function(){
		var deviceTagInitValue1="<s:text name="dataSource.classificationTag1"/>";
		var deviceTagInitValue2="<s:text name="dataSource.classificationTag2"/>";
		var deviceTagInitValue3="<s:text name="dataSource.classificationTag3"/>";
		if(deviceTagInitValue1=="dataSource.classificationTag1")deviceTagInitValue1="None"
		if(deviceTagInitValue2=="dataSource.classificationTag2")deviceTagInitValue2="None"
		if(deviceTagInitValue3=="dataSource.classificationTag3")deviceTagInitValue3="None"
		var templateEle = document.getElementById("hiveAp_configTemplate");
		var ct = $("#ap2ClassifierTagContainer").classifierTag(
				{
					key: 8,
					types:  [{key: 4, text: 'Device Tags'}, null, null],
					widgetWidth: {desc: 0},
					valueProps: null,
					itemEditable: false,
					describable: false,
					needShowTagFields: true,
					deviceTagInitValue: {
						Tag: [deviceTagInitValue1, deviceTagInitValue2, deviceTagInitValue3]
					},
					inputTagDone:{
						funcName: requestClassificationInfo,
						argument: templateEle}
				});
   		$("#ap2ClassifierTagContainer").show();
	});	
}

<s:if test="%{jsonMode == true}">
window.setTimeout("initWidgetGui()", 200);
</s:if>
<s:else>
$(document).ready(function()
{
	initWidgetGui();
	
}
);
</s:else>
function enabledOverrideCaptureDataCheckBox(value){
	if (value) {
		document.getElementById("global_captureDataCwp").style.display= "";
	} else {
		document.getElementById("global_captureDataCwp").style.display= "none";
	}
}
</script>


<tiles:insertDefinition name="hiveap2Page" />
<tiles:insertDefinition name="hiveap2Page_2" />

<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
<s:form action="hiveAp" id="hiveAp" name="hiveAp">
	<s:hidden name="radioProfileName" />
	<s:hidden name="radioProfileMode" />
	<s:hidden name="radioProfileChannelWidth" />
	<s:hidden name="radioType" />
	<s:hidden name="expanding_staticRoutes" id="expanding_staticRoutes"
		value="%{expanding_staticRoutes}" />
	<s:hidden name="expanding_intNetwork" id="expanding_intNetwork"
		value="%{expanding_intNetwork}" />
	<s:hidden name="expanding_brStaticRouting" id="expanding_brStaticRouting"
		value="%{expanding_brStaticRouting}" />
	<s:hidden name="expanding_dynamic" id="expanding_dynamic"
		value="%{expanding_dynamic}" />
	<s:hidden name="expanding_static" id="expanding_static"
		value="%{expanding_static}" />
	<s:hidden name="expanding_ip" id="expanding_ip" value="%{expanding_ip}" />
	<s:hidden name="expanding_vlanid" id="expanding_vlanid" value="%{expanding_vlanid}" />
	<s:hidden name="expanding_virtualConnect" id="expanding_virtualConnect"
		value="%{expanding_virtualConnect}" />
	<s:hidden name="dhcpServer" />
    <s:hidden name="preferredSsid" />
    <s:hidden name="wifiClientWan" />
	<s:hidden name="capwapIp" />
	<s:hidden name="capwapBackupIp" />
	<s:hidden name="learningMacId" />
	<s:hidden name="enableMdmTag" id="enableMdmTag"/>
	<s:hidden name="dataSource.hiveApModel" />
	<s:hidden name="dataSource.networkSettingsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.serviceSettingsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.ssidAllocationDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.advEthSettingsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.advSettingsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.credentialsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.l3RoamingDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.routingDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.eth0BridgeAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.eth1BridgeAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.agg0BridgeAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.red0BridgeAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.tempWifi0RadioProfileCreateDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.ethCwpSettingDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.staticRoutesDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.cvgMgtServerDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.wifiClientModeDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.mgt0DhcpSettingsStyle"></s:hidden>
	<s:hidden name="dataSource.configMdmContentDisplayStyle"></s:hidden>
	<s:hidden name="ethUserProfileId"></s:hidden>
	<s:hidden name="dataSource.dhcp"></s:hidden>
	<s:hidden name="dataSource.dhcpFallback"></s:hidden>
	<s:hidden name="oldDeviceType"/>
	<%-- <s:hidden name="dataSource.forwardingDBSettingStyle"></s:hidden>
	<s:hidden name="dataSource.lldpcdpSettingStyle"></s:hidden>
	<s:hidden name="dataSource.resrvedVlansSettingStyle"></s:hidden>--%>
	<!-- fix bug 14675 -->
	<s:hidden name="hmListType"/>
	<!-- fix bug 19461 -->
	<s:hidden name="brSameSubnetList"/>
	<s:if test="%{jsonMode}">
		<%-- <s:hidden name="dataSource.id"></s:hidden> --%>
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="id" />
	</s:if>
	<s:if test="%{jsonMode && vpnGateWayDlg}">
		<s:hidden name="operation" />
		<s:hidden name="parentIframeOpenFlg" />
	</s:if>
	<s:if test="%{jsonMode && vpnGateWayDlg}">
	<div  class="topFixedTitle">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" >
				<tr>
					<td style="padding:10px 10px 10px 10px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-cvg.png" includeParams="none"/>"
										width="40" height="40" alt="" class="dblk" />
									</td>
									<td class="dialogPanelTitle"><s:text name="config.vpnservice.config.vpngateway.title"/></td>
								</tr>
							</table>
							</td>
							<td align="right" width="120px">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="margin-right: 20px;" title="Cancel" onclick="submitAction('cancelDlg');"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
									<!-- <td width="25px">&nbsp;</td> -->
									<td class="npcButton">
										<s:if test="%{writeDisabled == 'disabled'}">
											<a href="javascript:void(0);" class="btCurrent"  style="float: right;" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="submitAction('update2');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
										</s:else>
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
	<s:if test="%{jsonMode && vpnGateWayDlg}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" id="editHiveAPTable"  class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" id="editHiveAPTable">
	</s:else>
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<s:if test="%{writeDisabled != 'disabled'}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create2');"
							<s:property value="writeDisabled" />></td>
						</s:if>
					</s:if>
					<s:else>
						<s:if test="%{writeDisabled != 'disabled'}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update2');"
							<s:property value="writeDisabled" />></td>
						</s:if>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="submitAction('cancel');"></td>
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
			<s:if test="%{vpnGateWayDlg == true || jsonMode == true}">
				<table style="padding: 0 4px 6px 4px;"
					cellspacing="0" cellpadding="0" border="0" width="810px">
			</s:if>
			<s:else>
				<table class="editBox" style="padding: 0 4px 6px 4px;"
					cellspacing="0" cellpadding="0" border="0" width="810px">
			</s:else>
				<tr>
					<td><!-- global section -->
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td style="padding-left:10px"><div id="errNoteHiveAp" class="noteError"></div></td>
						</tr>
						<tr>
							<td height="10"/>
						</tr>
						<tr id="deviceBaseSettings">
							<td><%-- add this password dummy to fix issue with auto complete function --%>
							<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
								type="password">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="2px"></td>
								</tr>
								<tr>
									<td class="labelT1" width="150px"><s:text
										name="hiveAp.hostName" /><font color="red"><s:text
										name="*" /></font></td>
									<td width="200px"><s:textfield name="dataSource.hostName" size="24"
										title="%{hostnameRange}"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{hostNameLength}" /></td>
									<td class="labelT1" width="120px"><s:text name="hiveAp.model" /></td>
									<td><s:property value="dataSource.deviceModelName"/></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hiveAp.macaddress" /><font color="red"><s:text name="*" /></font></td>
									<td><s:textfield name="dataSource.macAddress" size="24"
										onkeypress="return hm.util.keyPressPermit(event,'hex');"
										maxlength="%{macAddressLength}" disabled="%{disabledName}" /></td>
									<td class="labelT1"><s:text name="hiveAp.device.type" /></td>
									<td>
									<s:hidden id="hiveApDeviceTypeValue" value="%{dataSource.deviceType}" />
									<s:select name="dataSource.deviceType"
										value="%{dataSource.deviceType}" list="%{deviceTypeList}"
										listKey="key" listValue="value" cssStyle="width: 198px;"
										onchange="changeApModel(); deviceTypeChangedWarning();changeModelDiv();"
										disabled="%{deviceTypeDisabled}"  /></td>
								</tr>
								<tr id="cvgNetworkTr" style="display: <s:property value="%{cvgDpdStyle}"/>">
									<td class="labelT1"><s:text name="hiveAp.cvg.mgt.network"/><font color="red"><s:text name="*" /></font></td>
									<td><s:select list="%{cvgMgtNetworkList}" listKey="id" listValue="value"
										 name="cvgMgtNetwork" cssStyle="width: 198px;"/></td>
									<td class="labelT1"><s:text name="hiveAp.cvg.mgt.vlan"/><font color="red"><s:text name="*" /></font></td>
									<td><s:select list="%{cvgMgtVlanList}" listKey="id" listValue="value"
										 name="cvgMgtVlan" cssStyle="width: 198px;"/></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hiveAp.location" /></td>
									<td><s:textfield name="dataSource.location" size="24"
										maxlength="32" disabled="%{bR100PlatformDevice}"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
										title="%{locationRange}" /></td>
									<td class="labelT1"><s:text name="hiveAp.topology" /></td>
									<td><s:select list="%{topologys}" listKey="id"
										listValue="value" name="topology" cssStyle="width: 198px;" 
										onchange="generateRealmName();"/></td>
								</tr>
								<tr id="networkPolicyTr" style="display: <s:property value="%{configTemplateStyle}"/>">
									<td class="labelT1"><s:text name="hiveAp.template" /></td>
									<td><s:select
										list="%{configTemplates}" listKey="id" listValue="value"
										name="configTemplate" cssStyle="width: 198px;"
										onchange="requestTemplateInfo(this);generateRealmName();" />
									</td>
									
									<td class="labelT1"><s:text name="hiveAp.port.template" /></td>
									<td><s:textfield name="dataSource.portTemplate" readonly="true" size="24"/>
									</td>
								</tr>
								<tr style="display: <s:property value='mucDisplay'/>">
									<td><s:checkbox name="dataSource.manageUponContact"
										value="%{dataSource.manageUponContact}" /></td>
									<td style="padding-left: 2px"><s:text
										name="hiveAp.manageUponContact" /></td>
								</tr>
								<tr id="vpnServerNameTr"  style="display: <s:property value="%{cvgDpdStyle}"/>">
									<td class="labelT1"><s:text name="hiveAp.cvg.vpnService"/></td>
									<td><s:property value="dataSource.vpnServerName" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr id="radioConfigStyle"
									style="display: <s:property value='radioConfigStyle'/>">
							<td style="padding-left: 5px;">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><s:radio label="Gender"
										name="dataSource.radioConfigType" list="%{radioModeType1}"
										listKey="key" listValue="value"
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" /></td>
								</tr>

								<tr id="radioConfigTypeTr2" >
									<td><s:radio label="Gender"
										name="dataSource.radioConfigType" list="%{radioModeType2}"
										listKey="key" listValue="value"
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" /></td>
								</tr>
								<tr id="radioConfigTypeTr2_note"
									style="display: <s:property value='radioConfigType2NoteStyle'/>">
									<td style="padding-left: 22px;" class="noteInfo">
										<s:text name="hiveAp.radioMode.meshNote"/></td>
								</tr>

								<tr style="display:<s:property value='radioTypeAccessDualStyle'/>">
									<td><s:radio label="Gender"
										name="dataSource.radioConfigType" list="%{radioModeType5}"
										listKey="key" listValue="value"
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" /></td>
								</tr>
								<tr id="radioConfigTypeDual_note"
									style="display: <s:property value='radioConfigDualNoteStyle'/>">
									<td style="padding-left: 22px;" class="noteInfo">
										<s:text name="hiveAp.radioMode.meshNote"/></td>
								</tr>
								
								<tr id="radioConfigTypeAccessWan_tr" style="display:<s:property value='radioTypeAccessWanStyle2'/>">
									<td>
									<s:if test="%{RadioTypeAccessWanStyle == 'none'}"> 
									<s:radio label="Gender" disabled="true"
										name="dataSource.radioConfigType" list="%{radioModeType6}"
										listKey="key" listValue="value" 
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" />
									</s:if>
									<s:else>
									<s:radio label="Gender" 
										name="dataSource.radioConfigType" list="%{radioModeType6}"
										listKey="key" listValue="value" 
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" />
									</s:else>	
										</td>
								</tr>
								
								<tr id="radioConfigTypeAccessWan_checkboxTr" style="display:<s:property value='radioTypeAccessWanStyle'/>">
											
											<td>
											&nbsp;&nbsp;&nbsp;&nbsp;
											<s:checkbox name="dataSource.enableDynamicBandSwitch" id="dynamicbandswitch_checkbox" onclick="clickEnableDynamicBandSwitch(this.checked);"
													style="vertical-align:bottom;" />
												<s:text name="hiveAp.radioMode.accessWan.dynamic.bandswitch"/>
											</td>
								</tr>
								<tr>
									<td style="padding-left:20px"><table><tr id="wifiClientErrorTip"/></table></td>
								</tr>
								<tr id = "radioConfigTypeCustomer">
									<td><s:radio label="Gender"
										name="dataSource.radioConfigType" list="%{radioModeType4}"
										listKey="key" listValue="value"
										onchange="radioModeTypeChanged(this.value);"
										onclick="this.blur();" /></td>
								</tr>


								<tr id="enableEthBridge_id" >
									<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td><s:checkbox name="dataSource.enableEthBridge" onclick="clickEnableEthBridge(this.checked);"
													disabled="%{enableEthBridgeDisabled}" />
												<s:text name="hiveAp.radioMode.ethbridge"/>
											</td>
										</tr>
										<tr id="enableEthBridge_note"
											style="display: <s:property value="%{enableEthBridgeNodeStyle}"/>">
											<td style="padding-left: 25px;" class="noteInfo">
												<s:text name="hiveAp.radioMode.bridgeNote1"/><br>
												<s:text name="hiveAp.radioMode.bridgeNote2"/>
											</td>
										</tr>
									</table></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr id="hiveApTag"
											style="display: <s:property value="%{hiveApTagStyle}"/>">
											<td>
											<fieldset><legend><s:text
												name="hiveAp.classification.tag" /></legend>
											<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
												<tr>
													<td height="10"></td>
												</tr>
												<tr id="ap2ClassifierTagContainer"> 	</tr>
											</table>
							</fieldset>
					      </td>
				       </tr>

						<tr>
							<td height="10px"></td>
						</tr>
						
						<tr id="wifiConfigStyle" style="display: <s:property value='wifiConfigStyle'/>">
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td valign="top">
									<fieldset>
									<!-- legend id="wanSettingsLegend">WAN Settings</legend -->
									<table cellspacing="0" cellpadding="0" border="0">
										<tr id="wifiConfig_blank">
											<td height="5"></td>
										</tr>
										<tr id="wifiConfigTitle">
											<th align="left" width="120px"><s:text
												name="hiveAp.wlanIf" /></th>
											<th align="left" width="80px"><s:text
												name="hiveAp.if.radioMode" /></th>
											<th align="left" width="200px" id="radioProfileWanBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:text
												name="hiveAp.if.radioProfile" /></th>
											<th align="left" width="80px"><s:text
												name="hiveAp.if.adminState" /></th>
											<th align="left" width="85px" id="radioChannelWanBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:text
												name="hiveAp.if.channel" /></th>
											<th align="left" nowrap="nowrap" id="radioPowerWanBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:text
												name="hiveAp.if.power" /></th>
										</tr>
										<tr id="wifiConfig_blank">
											<td height="5"></td>
										</tr>
										<tr id="wifi0Row">
											<td class="list"><span id="wifi0Label"><s:property
												value="%{wifi0Label}" /></span> <s:text name="hiveAp.if.wifi0" /></td>
											<td id="wifi0RadioMode" class="list"><s:property
												value="%{wifi0RadioModeLabel}" /></td>
											<td class="list" id="wifi0RadioProfileBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:select name="wifi0RadioProfile"
												list="%{wifi0RadioProfiles}" listKey="id" listValue="value"
												cssStyle="width: 150px;" onchange="selectRadioProfile(this)" />
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />" width="16"
													height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:newSimpleRadioProfile('wifi0')"><img
													class="dinl" src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
											</s:else> <s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:editWifi0RadioProfile()"><img
													class="dinl" src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else></td>
											<td class="list"><s:select
												name="dataSource.wifi0.adminState"
												value="%{dataSource.wifi0.adminState}"
												list="%{enumAdminStateType}" listKey="key" listValue="value"
												onchange="adminStatusChanged(this, 'wifi0');" /></td>
											<td class="list" id="wifi0ChannelBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:select
												name="dataSource.wifi0.channel"
												value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}"
												listKey="key" listValue="value" /></td>
											<td class="list" id="wifi0PowerBR200WPID" style="display: <s:property value="%{dynamicBandSwitchStyle}"/>"><s:select name="dataSource.wifi0.power"
												value="%{dataSource.wifi0.power}" list="%{enumPowerType}"
												listKey="key" listValue="value" /></td>
										</tr>
										<tr>
											<td colspan="10"><div id="errNoteForRadiobg"/></div>
										</tr>
										<tr>
											<td colspan="10">
											<div id="wifi0RadioProfileCreateSection"
												style="display: <s:property value="%{dataSource.tempWifi0RadioProfileCreateDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td style="padding-bottom: 2px; padding-left: 10px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<s:if test="%{wifi0RadioProfileNameDisabled}">
																<td><input type="button" name="ignore"
																	value="Apply" class="button"
																	onClick="submitAction('updateWifi0RadioProfile');"
																	<s:property value="wifi0RadioProfileApplyDisabled"/>></td>
															</s:if>
															<s:else>
																<td><input type="button" name="ignore"
																	value="Apply" class="button"
																	onClick="submitAction('createWifi0RadioProfile');"></td>
															</s:else>
															<s:if test="%{wifi0RadioProfileNameDisabled}">
																<td><input type="button" name="ignore"
																	value="Cancel" class="button"
																	onClick="submitAction('clearWifi0RadioProfile');"></td>
															</s:if>
															<s:else>
																<td><input type="button" name="ignore"
																	value="Cancel" class="button"
																	onClick="hideSimpleCreateSection('wifi0RadioProfileCreateSection');"></td>
															</s:else>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td class="sepLine"><img
														src="<s:url value="/images/spacer.gif"/>" height="2"
														class="dblk" /></td>
												</tr>
												<tr>
													<td>
													<table class="listembedded" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="170px"><s:text
																name="config.radioProfile.name" /></td>
															<td><s:textfield
																name="dataSource.tempWifi0RadioProfile.radioName"
																size="32" maxlength="%{radioProfileNameLength}"
																disabled="%{wifi0RadioProfileNameDisabled}" /> <s:text
																name="config.ssid.ssidName_range" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text
																name="config.radioProfile.radioMode" /></td>
															<td><s:select
																name="dataSource.tempWifi0RadioProfile.radioMode"
																disabled="%{wifi0RadioProfileNameDisabled}"
																list="%{enumRadioMode}" listKey="key" listValue="value"
																cssStyle="width: 110px"
																onchange="changeWifi0RadioMode(this.value);" /></td>
														</tr>
														<tr id="channelWidthTr0"
															style="display: <s:property value="%{wifi0RadioProfileChannelStyle}"/>">
															<td class="labelT1"><s:text
																name="config.radioProfile.channel.width" /></td>
															<td><s:select
																name="dataSource.tempWifi0RadioProfile.channelWidth"
																list="%{enumRadioChannelWidth}" listKey="key"
																listValue="value" cssStyle="width: 110px" /></td>
														</tr>
														<tr>
															<s:if test="%{wifi0RadioProfileNameDisabled}">
																<td class="labelT1" colspan="2"
																	style="padding-right: 30px;"><a class="textLink"
																	href="javascript:editWifi0RadioProfile();">More
																Settings...</a></td>
															</s:if>
															<s:else>
																<td class="labelT1" colspan="2"
																	style="padding-right: 30px;"><a class="textLink"
																	href="javascript:newWifi0RadioProfile();">More
																Settings...</a></td>
															</s:else>
														</tr>
														<tr>
															<td height="10px" colspan="2"></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
										<tr id="wifiConfig_blank">
											<td height="5px"></td>
										</tr>
										<tr id="wifi1Row"
											style="display: <s:property value="wifi1StuffStyle"/>">
											<td class="list"><span id="wifi1Label"><s:property
												value="%{wifi1Label}" /></span> <s:text name="hiveAp.if.wifi1" /></td>
											<td id="wifi1RadioMode" class="list"><s:property
												value="%{wifi1RadioModeLabel}" /></td>
											<td class="list"><s:select name="wifi1RadioProfile"
												list="%{wifi1RadioProfiles}" listKey="id" listValue="value"
												cssStyle="width: 150px;" onchange="selectRadioProfile(this)" />
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />" width="16"
													height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:newSimpleRadioProfile('wifi1')"><img
													class="dinl" src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
											</s:else> <s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:editWifi1RadioProfile()"><img
													class="dinl" src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else></td>
											<td class="list"><s:select
												name="dataSource.wifi1.adminState"
												value="%{dataSource.wifi1.adminState}"
												list="%{enumAdminStateType}" listKey="key" listValue="value"
												onchange="adminStatusChanged(this, 'wifi1');" /></td>
											<td class="list"><s:select
												name="dataSource.wifi1.channel"
												value="%{dataSource.wifi1.channel}" list="%{wifi1Channel}"
												listKey="key" listValue="value" /></td>
											<td class="list"><s:select name="dataSource.wifi1.power"
												value="%{dataSource.wifi1.power}" list="%{enumPowerType}"
												listKey="key" listValue="value" /></td>
										</tr>
										<tr>
											<td colspan="10"><div id="errNoteForRadioa"/></div>
										</tr>
										<tr>
											<td colspan="10">
											<div id="wifi1RadioProfileCreateSection"
												style="display: <s:property value="%{dataSource.tempWifi1RadioProfileCreateDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td style="padding-bottom: 2px; padding-left: 10px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<s:if test="%{wifi1RadioProfileNameDisabled}">
																<td><input type="button" name="ignore"
																	value="Apply" class="button"
																	onClick="submitAction('updateWifi1RadioProfile');"
																	<s:property value="wifi1RadioProfileApplyDisabled"/>></td>
															</s:if>
															<s:else>
																<td><input type="button" name="ignore"
																	value="Apply" class="button"
																	onClick="submitAction('createWifi1RadioProfile');"></td>
															</s:else>
															<s:if test="%{wifi1RadioProfileNameDisabled}">
																<td><input type="button" name="ignore"
																	value="Cancel" class="button"
																	onClick="submitAction('clearWifi1RadioProfile');"></td>
															</s:if>
															<s:else>
																<td><input type="button" name="ignore"
																	value="Cancel" class="button"
																	onClick="hideSimpleCreateSection('wifi1RadioProfileCreateSection');"></td>
															</s:else>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td class="sepLine"><img
														src="<s:url value="/images/spacer.gif"/>" height="2"
														class="dblk" /></td>
												</tr>
												<tr>
													<td>
													<table class="listembedded" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="170px"><s:text
																name="config.radioProfile.name" /></td>
															<td><s:textfield
																name="dataSource.tempWifi1RadioProfile.radioName"
																size="32" maxlength="%{radioProfileNameLength}"
																disabled="%{wifi1RadioProfileNameDisabled}" /> <s:text
																name="config.ssid.ssidName_range" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text
																name="config.radioProfile.radioMode" /></td>
															<td><s:select
																name="dataSource.tempWifi1RadioProfile.radioMode"
																disabled="%{wifi1RadioProfileNameDisabled}"
																list="%{enumRadioMode}" listKey="key" listValue="value"
																cssStyle="width: 110px"
																onchange="changeWifi1RadioMode(this.value);" /></td>
														</tr>
														<tr id="channelWidthTr1"
															style="display: <s:property value="%{wifi1RadioProfileChannelStyle}"/>">
															<td class="labelT1"><s:text
																name="config.radioProfile.channel.width" /></td>
															<td><s:select
																name="dataSource.tempWifi1RadioProfile.channelWidth"
																list="%{enumRadioChannelWidth}" listKey="key"
																listValue="value" cssStyle="width: 110px" /></td>
														</tr>
														<tr>
															<s:if test="%{wifi1RadioProfileNameDisabled}">
																<td class="labelT1" colspan="2"
																	style="padding-right: 30px;"><a class="textLink"
																	href="javascript:editWifi1RadioProfile();">More
																Settings...</a></td>
															</s:if>
															<s:else>
																<td class="labelT1" colspan="2"
																	style="padding-right: 30px;"><a class="textLink"
																	href="javascript:newWifi1RadioProfile();">More
																Settings...</a></td>
															</s:else>
														</tr>
														<tr>
															<td height="10px" colspan="2"></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
										

										 <tr id="routerWanRow" style="display: <s:property value="%{routerWanRowStyle}"/>">
										<td colspan="10">
											<%-- <table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1">
													<s:checkbox name="branchRouterEth0.enableDhcp" onclick="brRouterEth0DhcpChanged(this.checked);"/>
													<s:text name="hiveAp.br100.dhcp.enable"/>
												</td>
											</tr>
											<tr id="brRouterEth0DhcpSettings"
												style="display: <s:property value='brRouterEth0DhcpSettingsStyle'/>">
												<td style="padding-left: 22px;"><table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td class="labelT1" width="110px">
															<s:text name="hiveAp.br100.wan.ipaddress" />
														</td>
														<td class="labelT1" width="300px">
															<s:textfield name="branchRouterEth0.ipAndNetmask" size="18"
																maxlength="18"
																onkeypress="return hm.util.keyPressPermit(event,'ipMask');" />
															<s:text name="config.vpn.subnet.ipnetwork.range" />
														</td>
														<td class="labelT1" width="100px">
															<s:text name="hiveAp.gateway" />
														</td>
														<td class="labelT1" width="200px">
															<s:textfield name="branchRouterEth0.gateway" size="18" maxlength="15"
																onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
														</td>
													</tr>
												</table></td>
											</tr>
										</table> --%></td></tr> 
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
					</td>
				</tr>
				<tr id="vpnGatewayIntStyle" style="display: <s:property value="%{vpnGatewayIntStyle}"/>">
					<td><table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td height="10px"/>
						</tr>
						<tr><td><fieldset>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr id="cvgWanIntSettings">
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td style="padding-left: 8px;"><table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr id="cvgPortSettingHead">
													<th align="left" width="50px"><s:text
															name="hiveAp.cfg.staticRoute.interface" /></th>
													<th align="left" width="200px"><s:text
															name="hiveAp.ipAddress.cvg" /></th>
													<th align="left" width="80px"><s:text
															name="hiveAp.gateway" /></th>
													<th id="cvgHeadAdmin" align="left" width="50px"><s:text
															name="hiveAp.if.adminState" /></th>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr id="cvgWanPortSettings">
													<td class="list"><s:text name="hiveAp.vpnGateway.if.wan.display"/><font
														color="red"><s:text name="*" /></font></td>
													<td class="list"><s:textfield name="wanInterface.ipAndNetmask" size="18" maxlength="18"
														onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/>
														<s:text name="config.vpn.subnet.ipnetwork.range" /></td>
													<td class="list"><s:textfield name="wanInterface.gateway" size="18" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
													<td class="list" id="cvgWanAdmin"><s:select
															name="wanInterface.adminState"
															value="%{wanInterface.adminState}"
															list="%{enumAdminStateType}" listKey="key" listValue="value"
															onchange="" /></td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr id="cvgLanPortSettings" style="display:<s:property value="%{cVGLanPortStyle}" />">
													<td class="list"><s:text name="hiveAp.vpnGateway.if.lan.display"/></td>
													<td class="list"><s:textfield name="lanInterface.ipAndNetmask" size="18" maxlength="18"
														onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/></td>
													<td class="list">&nbsp;</td>
													<td class="list" id="cvgLanAdmin"><s:select
															name="lanInterface.adminState"
															value="%{lanInterface.adminState}"
															list="%{enumAdminStateType}" listKey="key" listValue="value"
															onchange="" /></td>
												</tr>
											</table></td>
										</tr>
									</table>
								</td>
							</tr>

							<!-- RoutingProfiles start -->
							<tr id="dynamicRoutingSettings">
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td>
															<s:checkbox name="dataSource.routingProfile.enableDynamicRouting" id="enableDynamicRouting"
																onclick="selectEnableDynamicRouting(this.checked);" />
															<label>
																<s:text name="hiveAp.vpnGateway.routing.profile.title" />
															</label>
														</td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr style="display:<s:property value="enableDynamicRoutingStyle"/>" id="dynamicRoutingSection">
														<td style="padding-left:20px;">
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr id="dynamicRoutingNote"/>													
															<tr><td height="5px"></td></tr>														
															<tr>
																<td>
																	<s:select id="typeFlag" cssStyle="width:100px"  name="dataSource.routingProfile.typeFlag" list="dynamicRoutingList" value="defaultDynamicRouting"
																		listKey="id" listValue="value" onchange="showDynamicRoutingContent();"></s:select>
																</td>
															</tr>
															<tr>
																<td height="10"></td>
															</tr>
															<tr>
																<td><fieldset>
																	<table border="0" cellspacing="0" cellpadding="0" width="100%">
																		<tr>
																			<td>
																				<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td class="labelT1"><label><s:text name="hiveAp.vpnGateway.routing.profile.route.advertisement" /></label></td>
																						<td style="padding-left: 50px;">
																							<s:checkbox name="dataSource.routingProfile.enableRouteWan" id="enableRouteWan"
																								value="%{dataSource.routingProfile.enableRouteWan}" >
																							</s:checkbox>
																								<label>
																									<s:text name="hiveAp.vpnGateway.if.wan.display" />
																								</label>
																						</td>
																						<td style="padding-left: 80px; display:<s:property value="%{cVGLanPortStyle}"/>;">
																							<s:checkbox name="dataSource.routingProfile.enableRouteLan" id="enableRouteLan"
																								value="%{dataSource.routingProfile == null || dataSource.routingProfile.enableRouteLan ? true : fasle }" >
																							</s:checkbox>
																								<label>
																									<s:text name="hiveAp.vpnGateway.if.lan.display" />
																								</label>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr style="padding-left: 25px">
																			<td>
																				<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																					<td class="labelT1">
																						<table border="0" cellspacing="0" cellpadding="0" width="100%">
																							<tr>
																								<td >
																									<s:checkbox name="dataSource.routingProfile.useMD5" id="useMD5" onclick="selectUseMD5(this.checked);" />
																									<label><s:text name="config.routingProfiles.useMD5" /></label>
																								</td>
																							</tr>
																						</table>

																					</td>
																					<td class="labelT1">
																						<table border="0" cellspacing="0" cellpadding="0" width="100%">
																							<tr style="display:<s:property value="useMD5Style" />" id="useMD5Section">
																								<td style="padding-left: 36px;">
																									<s:text name="config.routingProfiles.password" /><font color="red"><s:text name="*"/></font>&nbsp;&nbsp;
																								</td>
																								<td>
																									<s:password name="dataSource.routingProfile.password" id="md5Password"
																										size="24" maxlength="32" onkeypress="return hm.util.keyPressPermit(event,'password');"
																										showPassword="true"/>
																									<s:textfield name="dataSource.routingProfile.password" id="md5Password_text"
																										size="24" maxlength="32" cssStyle="display:none;"  disabled="true"
																										onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																								</td>
																								<td>
																									<s:checkbox id="chkToggleDisplay_md5"
																										name="ignore" value="true"
																										onclick="hm.util.toggleObscurePassword(this.checked,
																											['md5Password'],
																											['md5Password_text']);" />
																								</td>
																								<td>
																									<s:text name="admin.user.obscurePassword" />
																								</td>
																							</tr>
																						</table>
																					</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr style="padding-left: 25px;display:<s:property value="bgpStyle"/>" id="bgpSection">
																			<td colspan="2" style="padding-top: 0px">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																				<td>
																					<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td class="labelT1" width="200"><label><s:text
																							name="config.routingProfiles.autoSysNm" /><font color="red"><s:text name="*"/></font>
																							</label></td>
																						<td><s:textfield size="24" name="dataSource.routingProfile.autonmousSysNm" id="autonmousSysNm" maxlength="5"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text
																							name="config.routingProfiles.autoSysNm.range" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="200"><label><s:text
																							name="config.routingProfiles.keepalive" />
																							</label></td>
																						<td><s:textfield size="24" name="keepalive" id="keepalive" maxlength="5"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text
																							name="config.routingProfiles.keepalive.range" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="200"><label><s:text
																							name="config.routingProfiles.bgpRouterID" />
																							</label></td>
																						<td><s:textfield size="24" name="bgpRouterId" id="bgpRouterId" maxlength="15"
																							onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																					</tr>
																				</table>
																				</td>
																				<td>
																					<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td></td>
																						<td><label><s:text
																							name="config.routingProfiles.bgpNeighbors" /><font color="red"><s:text name="*"/></font>
																							</label></td>
																					</tr>
																					<tr>
																						<td></td>
																						<td><s:textarea name="bgpNeighbors" id="bgpNeighbors" wrap="true" cssStyle="width:200px"
																							rows="5" /></td>
																					</tr>
																					<tr></tr>

																				</table>
																				</td>
																				</tr>
																			</table>
																			</td>
																		</tr>
																		<tr style="padding-left: 25px;display:<s:property value="ospfStyle"/>" id="ospfSection">
																			<td colspan="2" style="padding-top: 0px">
																				<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td class="labelT1" width="60"><label><s:text
																							name="config.routingProfiles.area" />
																							</label></td>
																						<td><s:textfield size="20" name="area" id="area" maxlength="15"
																							onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																						<td class="labelT1" width="60"><label><s:text
																							name="config.routingProfiles.routerId" />
																							</label></td>
																						<td><s:textfield size="20" name="routerId" id="routerId" maxlength="15"
																							onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</fieldset></td>
															</tr>
															<tr>
																<td height="10"></td>
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

							<!-- CVG Internal Netowrks  -->
							<tr id="internalNetworkSettings">
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.internalNetwork" />','intNetwork');</script></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td style="padding-left:20px;">
														<div id="intNetwork" style="display: <s:property value="%{dataSource.internalNetworksDisplayStyle}"/>">
														<fieldset>
														<table cellspacing="0" cellpadding="0" border="0" class="embedded">
															<tr>
																<td height="10"></td>
															</tr>
															<tr id="newButtonIntNetwork">
																<td colspan="3" style="padding-bottom: 2px;" >
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td><input type="button" name="ignore" value="New"
																			class="button" onClick="showCreateSection('intNetwork');"
																			<s:property value="writeDisabled" /> ></td>
																		<td><input type="button" name="ignore" value="Remove"
																			class="button" <s:property value="writeDisabled" />
																			onClick="doRemoveIntNetwork();"></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr style="display:none" id="createButtonIntNetwork">
																<td colspan="3" style="padding-bottom: 2px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																			class="button" <s:property value="writeDisabled" /> onClick="doAddIntNetwork();"></td>
																		<td><input type="button" name="ignore" value="Remove"
																			class="button" <s:property value="writeDisabled" />
																			onClick="doRemoveIntNetwork();"></td>
																		<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																			class="button" onClick="hideCreateSection('intNetwork');"></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr id="headerSectionIntNetwork">
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<th align="left" style="padding-left: 0;" width="30"><input
																			type="checkbox" id="checkAllIntNetwork"
																			onClick="toggleCheckAllIntNetwork(this);"></th>
																		<th align="left" width="200"><s:text
																			name="hiveAp.cvg.internalNetwork.network" /></th>
																		<th align="left" width="200"><s:text
																			name="hiveAp.cvg.internalNetwork.netmask" /></th>
																	</tr>
																</table>
																</td>
															</tr>
															<tr style="display:none" id="createSectionIntNetwork">
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="listHead" width="30">&nbsp;</td>
																		<td class="listHead" valign="top" width="200"><s:textfield size="20" name="interNetIpInput" maxlength="15"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																		<td class="listHead" valign="top" width="200"><s:textfield size="20" name="interNetMaskInput" maxlength="15"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td>
																<table border="0" cellspacing="0" cellpadding="0" id="tbnet_id">
																<s:iterator value="%{dataSource.internalNetworks}" status="status">
																	<tr>
																		<td class="listCheck" width="30"><s:checkbox name="intNetworkIndices"
																			fieldValue="%{#status.index}" /></td>
																		<td class="list" width="200"><s:property value="internalNetwork" /></td>
																		<td class="list" width="200"><s:property value="netmask" /></td>
																	</tr>
																</s:iterator>
																</table>
																</td>
															</tr>
															<tr>
																<td colspan="5" width="100%">
																	<table id="intNetworkTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:if test="%{gridCount > 0}">
																		<s:generator separator="," val="%{' '}"
																			count="%{gridCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="6">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																	</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</div>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>

							<!-- CVG Static Routers  -->
							<tr id="cvgStaticRouterSettings">
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="5px"></td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.staticRoutes" />','staticRoutes');</script></td>
													</tr>
													<tr>
														<td style="padding-left:20px;">
														<div id="staticRoutes" style="display: <s:property value="%{dataSource.staticRoutesDisplayStyle}"/>">
														<fieldset>
															<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="tbl_id">
																<tr>
																	<td height="10"></td>
																</tr>
																<tr id="newButtonStaticRoutes">
																		<td colspan="3" style="padding-bottom: 2px;" >
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="New"
																					class="button" onClick="showCreateSection('staticRoutes');"
																					<s:property value="writeDisabled" />></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeStaticRoute');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display:none" id="createButtonStaticRoutes">
																		<td colspan="3" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																					class="button" <s:property value="writeDisabled" /> onClick="submitAction('addStaticRoute');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeStaticRouteNone');"></td>
																				<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																					class="button" onClick="hideCreateSection('staticRoutes');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerSectionStaticRoutes">
																		<th align="left" style="padding-left: 0;" width="10"><input
																			type="checkbox" id="checkAllRoute"
																			onClick="toggleCheckAllRoutingProfilesStaticRoutes(this);"></th>
																		<th align="left" width="200"><s:text
																			name="config.routingProfiles.ip" /></th>
																		<th align="left" width="200"><s:text
																			name="config.routingProfiles.netmask" /></th>
																		<th align="left" width="200"><s:text
																			name="config.routingProfiles.gateway" /></th>
																		<th align="left" width="200"><s:text
																			name="hiveAp.cvg.internalNetwork.distribute" /></th>
																	</tr>
																	<tr style="display:none" id="createSectionStaticRoutes">
																		<td class="listHead" width="10">&nbsp;</td>
																		<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteIpInput" maxlength="15"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																		<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteMaskInput" maxlength="15"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																		<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteGwInput" maxlength="15"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																		<td class="listHead" valign="top" width="200"><s:checkbox name="distributeNet" /></td>
																	</tr>
																	<tr><td colspan="10">
																		<table border="0" cellspacing="0" cellpadding="0" id="tbCVGIpRoute_id">
																			<s:iterator value="%{dataSource.ipRoutes}" status="status">
																				<tr>
																					<td class="listCheck"><s:checkbox name="routingProfilesStaticRoutesIndices"
																						fieldValue="%{#status.index}" /></td>
																					<td class="list" width="200"><s:property value="sourceIp" /></td>
																					<td class="list" width="200"><s:property value="netmask" /></td>
																					<td class="list" width="200"><s:property value="gateway" /></td>
																					<td class="list" width="200"><s:checkbox name ="distributeBR" disabled="true" /></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</td></tr>
																	<tr>
																		<td colspan="10" width="100%">
																			<table id="cvgStaticRouteTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																			<s:if test="%{ipRouteCount > 0}">
																				<s:generator separator="," val="%{' '}"
																					count="%{ipRouteCount}">
																					<s:iterator>
																						<tr>
																							<td class="list" colspan="6">&nbsp;</td>
																						</tr>
																					</s:iterator>
																				</s:generator>
																			</s:if>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
															</div>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>

							<!-- RoutingProfiles end -->
							<tr style="display:none">
								<td><table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<s:if test="%{dataSource.secondVpnGateway}">
											<td class="labelT1">
												<s:text name="hiveAp.secondVpnGateway.VRRPDesc"/>
											</td>
										</s:if>
										<s:else>
											<td class="labelT1">
												<s:checkbox name="vpnGatewayVrrpEnable" onchange="enableVRRPChange(this.checked);" />
												<s:text name="hiveAp.vpnGateway.enableVRRP"/>
											</td>
										</s:else>

									</tr>
									<tr id="VPPRSetting" style="display: <s:property value='%{vPPRSettingStyle}'/>"><td style="padding-left: 25px;"><table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<s:if test="%{dataSource.secondVpnGateway}">
												<td class="labelT1">
													<s:text name="hiveAp.secondvpnGateway.primary.gateway"/>
												</td>
												<td colspan="3">
													<s:textfield name="dataSource.primaryVPNGateway.hostName"
														size="18"  disabled="true" />
													<s:if test="%{vpnGatewayPreemptEnable}">
														<s:text name="hiveAp.secondvpnGateway.primary.preempt"/>
													</s:if>
												</td>
											</s:if>
											<s:else>
												<td class="labelT1"><s:text name="hiveAp.vpnGateway.secondary.gateway"/></td>
												<td>
													<s:select
														list="%{secondVPNGateways}" listKey="id" listValue="value"
														name="secondVPNGateway" cssStyle="width: 127px;"
														onchange="secondVPNGatewayChanged(this.value);" />
												</td>
												<td colspan="2">
													<s:checkbox name="vpnGatewayPreemptEnable" />
													<s:text name="hiveAp.vpnGateway.enablePrimary"/>
												</td>
											</s:else>
										</tr>
										<tr>
											<s:if test="%{dataSource.secondVpnGateway}">
												<td class="labelT1"><s:text name="hiveAp.secondvpnGateway.primary.wanIp"/></td>
												<td>
													<s:textfield name="dataSource.primaryVPNGateway.eth0Interface.ipAddress"
														size="18" disabled="true" />
												</td>
												<td><s:text name="hiveAp.vpnGateway.virtual.wanIp"/></td>
												<td>
													<s:textfield name="dataSource.primaryVPNGateway.virtualWanIp" size="18"
														maxlength="%{virtualWanIpLength}" disabled="%{dataSource.secondVpnGateway}" />
												</td>
											</s:if>
											<s:else>
												<td class="labelT1"><s:text name="hiveAp.vpnGateway.secondary.wanIp"/></td>
												<td>
													<s:textfield name="dataSource.secondVPNGateway.eth0Interface.ipAndNetmask"
														size="18" disabled="true" />
												</td>
												<td><s:text name="hiveAp.vpnGateway.virtual.wanIp"/></td>
												<td>
													<s:textfield name="vpnGatewayVirtualWanIp" size="18"
														maxlength="15" disabled="%{dataSource.secondVpnGateway}"
														onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
												</td>
											</s:else>
										</tr>
										<tr>
											<s:if test="%{dataSource.secondVpnGateway}">
												<td class="labelT1"><s:text name="hiveAp.secondvpnGateway.primary.lanIp"/></td>
												<td>
													<s:textfield name="dataSource.primaryVPNGateway.eth1Interface.ipAddress"
														size="18" disabled="true"/>
												</td>
												<td><s:text name="hiveAp.vpnGateway.virtual.lanIp"/></td>
												<td>
													<s:textfield name="dataSource.primaryVPNGateway.virtualLanIp" size="18"
														maxlength="%{virtualLanIpLength}" disabled="%{dataSource.secondVpnGateway}" />
												</td>
											</s:if>
											<s:else>
												<td class="labelT1"><s:text name="hiveAp.vpnGateway.secondary.lanIp"/></td>
												<td>
													<s:textfield name="dataSource.secondVPNGateway.eth1Interface.ipAndNetmask"
														size="18" disabled="true"/>
												</td>
												<td><s:text name="hiveAp.vpnGateway.virtual.lanIp"/></td>
												<td>
													<s:textfield name="vpnGatewayVirtualLanIp" size="18"
														maxlength="15" disabled="%{dataSource.secondVpnGateway}"
														onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
												</td>
											</s:else>
										</tr>
									</table></td></tr>
								</table></td>
							</tr>
						</table>
					</fieldset></td></tr>
					<tr><td height="5px"></td></tr>
					</table></td>
				</tr>
				<tr id="hiveApOptionSection">
					<td><!-- optional section -->
					<fieldset><legend><s:text
						name="hiveAp.cfg.optional.tag" /></legend>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr id="captureDataCwpSettings" style="display: none;" >
						<!-- <tr id="captureDataCwpSettings" style="display: <s:property value='overrideConfigCaptureDataStyle'/>" > -->
							<td>
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="geneva_09.hiveap.captureDataCwp.settings" />','captureDataCwpDiv');</script></td>
									</tr>
									<tr>
										<td>
											<table id="captureDataCwpDiv" style="padding-left: 15px;display: <s:property value="%{dataSource.captureDataCwpDivStyle}"/>" cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<s:checkbox onclick="enabledOverrideCaptureDataCheckBox(this.checked);"
															name="dataSource.overrideCaptureDataByCWP" /> <s:text
															name="geneva_09.hiveap.captureDataCwp.settings.override"></s:text>
													</td>
												</tr>
												<tr>
												<td id="global_captureDataCwp" style="padding-left: 22px;display: <s:property value="%{enabledOverrideCaptureDataMode}"/>">
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																		<table width="100%" cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<table width="100%" cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<td class="labelT1">
																								<s:checkbox
																									name="dataSource.enableCaptureDataByCWP" /> <s:text
																									name="geneva_09.hiveap.captureDataCwp.capture.data"></s:text>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>
																</div>
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
						<tr id="ethConfigCwpStyle" style="display: <s:property value='ethConfigCwpStyle'/>" >
							<td><!-- Ethernet CWP Settings -->
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td height="5px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethCwp.label" />','ethcwpSettings');</script></td>
								</tr>
								<tr>
									<td>
									<div id="ethcwpSettings"
										style="display: <s:property value="%{dataSource.ethCwpSettingDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td style="padding-left: 8px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td><s:checkbox name="dataSource.ethCwpEnableEthCwp"
														onclick="changeEnableEthCwp(this.checked)"></s:checkbox></td>
													<td width="230px" class="labelT1"
														style="padding-left: 0px;"><label><s:text
														name="hiveAp.ethCwp.enable.cwp" /></label></td>
													<td id="ethCwpProfileTd"
														style="display: <s:property value="%{dataSource.ethCwpEnableEthCwp?'':'none'}" />">
													<s:select name="cwpProfile" list="%{cwps}" listKey="id"
														listValue="value" onchange="cwpProfileChanged();"
														cssStyle="width: 200px; font-size:100%"></s:select> <s:if
														test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
															src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" />
													</s:if> <s:else>
														<a class="marginBtn"
															href="javascript:submitAction('newEthCwpCwpProfile')"><img
															class="dinl" src="<s:url value="/images/new.png" />"
															width="16" height="16" alt="New" title="New" /></a>
													</s:else> <s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
															src="<s:url value="/images/modify_disable.png" />"
															width="16" height="16" alt="Modify" title="Modify" />
													</s:if> <s:else>
														<a class="marginBtn"
															href="javascript:submitAction('editEthCwpCwpProfile')"><img
															class="dinl" src="<s:url value="/images/modify.png" />"
															width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="ethCwpMacAuthTr"
													style="display: <s:property value="%{ethCwpMacAuthSettingDisplayStyle}"/>">
											<td style="padding-left: 8px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td><s:checkbox name="dataSource.ethCwpEnableMacAuth"
														onclick="changeEnableEthMacAuth(this.checked)"></s:checkbox></td>
													<td class="labelT1" style="padding-left: 0px;"><label><s:text
														name="hiveAp.ethCwp.enable.macAuth" /></label></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="ethCwpRadiusContentSettingTr"
											style="display: <s:property value="%{ethCwpRadiusContentSettingDisplayStyle}"/>">
											<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr id="ethCwpRadiusSelectionTr"
													style="display: <s:property value="%{ethCwpRadiusSelectionDisplayStyle}"/>">
													<td style="padding-left: 18px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="230px"><s:text
																name="hiveAp.ethCwp.radius.profile" /></td>
															<td><s:select name="ethCwpRadiusClient"
																list="%{radiusClientProfiles}" listKey="id"
																listValue="value" cssStyle="width: 200px;"></s:select> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('newEthCwpRadiusClient')"><img
																	class="dinl" src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
															</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('editEthCwpRadiusClient')"><img
																	class="dinl" src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text
																name="hiveAp.ethCwp.authMethod" /></td>
															<td><s:select name="dataSource.ethCwpAuthMethod"
																list="%{enumAuthMethod}" listKey="key" listValue="value"
																cssStyle="width: 200px;"></s:select></td>
														</tr>
														<tr>
															<td height="2px"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="ethCwpUserProfileSelectionTr"
													style="display: <s:property value="%{ethCwpUserProfileSelectionDisplayStyle}"/>">
													<td>
													<fieldset><legend><s:text
														name="hiveAp.ethCwp.userprofile.tag" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr id="ethCwpUserProfileRegTr"
															style="display: <s:property value="%{ethCwpUserProfileRegDisplayStyle}"/>">
															<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td class="labelT1"><s:text
																		name="hiveAp.ethCwp.default.reg.userprofile" /></td>
																</tr>
																<tr>
																	<td style="padding-left: 66px;"><s:select
																		name="ethDefaultRegUserprofile" list="%{userProfiles}"
																		listKey="id" listValue="value"
																		cssStyle="width: 200px;"></s:select> <s:if
																		test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newEthCwpDefaultRegUserProfile')"><img
																			class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('editEthCwpDefaultRegUserProfile')"><img
																			class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16"
																			height="16" alt="Modify" title="Modify" /></a>
																	</s:else></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr id="ethCwpUserProfileAuthTr"
															style="display: <s:property value="%{ethCwpUserProfileAuthDisplayStyle}"/>">
															<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr id="ethCwpUserProfileAuthLabelTr"
																	style="display: <s:property value="%{ethCwpUserProfileAuthLabelDisplayStyle}"/>">
																	<td class="labelT1"><s:text
																		name="hiveAp.ethCwp.default.auth.userprofile" /></td>
																</tr>
																<tr id="ethCwpUserProfileDefaultLabelTr"
																	style="display: <s:property value="%{ethCwpUserProfileDefaultLabelDisplayStyle}"/>">
																	<td class="labelT1"><s:text
																		name="hiveAp.ethCwp.userprofile.default.label" /></td>
																</tr>
																<tr>
																	<td style="padding-left: 66px;"><s:select
																		name="ethDefaultAuthUserprofile"
																		list="%{userProfiles}" listKey="id" listValue="value"
																		cssStyle="width: 200px;"></s:select> <s:if
																		test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newEthCwpDefaultAuthUserProfile')"><img
																			class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('editEthCwpDefaultAuthUserProfile')"><img
																			class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16"
																			height="16" alt="Modify" title="Modify" /></a>
																	</s:else></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr id="ethCwpUserProfilesTr"
															style="display: <s:property value="%{ethCwpUserProfilesDisplayStyle}"/>">
															<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td class="labelT1"><s:text
																		name="hiveAp.ethCwp.userprofiles" /></td>
																</tr>
																<tr>
																	<s:push value="%{ethUserprofileOptions}">
																		<td style="padding-left: 66px;"><tiles:insertDefinition
																			name="optionsTransfer" /></td>
																	</s:push>
																</tr>
															</table>
															</td>
														</tr>
														<tr id="ethCwpUserProifleParamsTr"
															style="display: <s:property value="%{ethCwpUserProifleParamsDisplayStyle}"/>">
															<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td style="padding-left: 8px;">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td><s:checkbox
																				name="dataSource.ethCwpLimitUserProfiles"
																				onclick="changeEnableEthLimitUserprofile(this.checked)"></s:checkbox></td>
																			<td class="labelT1" style="padding-left: 0px;"><label><s:text
																				name="hiveAp.ethCwp.userprofile.limit" /></label></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr>
																	<td id="ethLimitUserprofileContent"
																		style="display: <s:property value="%{dataSource.ethCwpLimitUserProfiles?'':'none'}"/>">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td style="padding-left: 20px;">
																			<table cellspacing="0" cellpadding="0" border="0"
																				width="100%">
																				<tr>
																					<td class="labelT1" width="215px"><s:text
																						name="hiveAp.ethCwp.userprofile.action" /></td>
																					<td><s:select
																						name="dataSource.ethCwpDenyAction"
																						list="%{enumDenyAction}" listKey="key"
																						listValue="value"
																						onchange="ethDenyActionChanged(this.value);"
																						cssStyle="width: 112px;"></s:select></td>
																				</tr>
																				<tr>
																					<td class="labelT1"><s:text
																						name="hiveAp.ethCwp.userprofile.actionTime" /></td>
																					<td><s:textfield
																						name="dataSource.ethCwpActiveTime" maxlength="10"
																						disabled="%{ethCwpActionTimeDisabled}" size="16"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																					<s:text
																						name="hiveAp.ethCwp.userprofile.actionTime.note"></s:text></td>
																				</tr>
																			</table>
																			</td>
																		</tr>
																		<tr>
																			<td style="padding-left: 25px;">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td><s:checkbox
																						name="dataSource.ethCwpEnableStriction"></s:checkbox></td>
																					<td class="labelT1" style="padding-left: 0px;"><label><s:text
																						name="hiveAp.ethCwp.userprofile.deauthenticate" /></label></td>
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
													</fieldset>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<s:if test="%{!hostBasedDependOnAuthEnabled}">
											<tr>
												<td id="sameVlanSection" style="padding-left: 8px;<s:if test="%{!dataSource.ethCwpEnableEthCwp && !dataSource.ethCwpEnableMacAuth}">display:none</s:if>;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<s:checkbox id="enabledSameVlan" name="dataSource.enabledSameVlan"/>
															</td>
															<td class="labelT1" style="padding-left: 0px;">
													        	<label for="enabledSameVlan"><s:text name="config.port.authentication.sameVLAN.label"/></label>
													       </td>
														</tr>
													</table>			
												</td>
									        </tr>
										</s:if>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="mgt0InterfaceConfigStyle">
							<td><!-- MGT0 Interface Settings -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.mgt0Interface" />','mgt0DhcpSettings');</script></td>
								</tr>
								<tr>
									<td>
									<div id="mgt0DhcpSettings" style="display: <s:property value='dataSource.mgt0DhcpSettingsStyle'/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td><table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
												<td style="padding-left:5px" >
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td width="160px"><s:radio label="Gender"
															name="mgt0NetworkType" list="%{Mgt0NetworkType1}"
															listKey="key" listValue="value"
															onchange="radioMgt0NetworkType(this.value,true);"
															onclick="this.blur();" /></td>

														<td width="280px"><s:radio label="Gender"
															name="mgt0NetworkType" list="%{Mgt0NetworkType2}"
															listKey="key" listValue="value"
															onchange="radioMgt0NetworkType(this.value,true);"
															onclick="this.blur();" /></td>

														<td width="200x"><s:radio label="Gender"
															name="mgt0NetworkType" list="%{Mgt0NetworkType3}"
															listKey="key" listValue="value"
															onchange="radioMgt0NetworkType(this.value,true);"
															onclick="this.blur();" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<td height="5px"></td>
											</tr>
											<tr>
												<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" id="dhcpLabel" width="250px">
															<s:if test="%{mgt0NetworkType == 3}">
																<s:text name="hiveAp.default.ipAddress" />
															</s:if>
															<s:elseif test="%{mgt0NetworkType == 2}">
																<s:text name="hiveAp.mgt0Interface.dhcpFallback" />
															</s:elseif>
															<s:else>
																<s:text name="hiveAp.ipAddress" />
															</s:else>
														</td>
														<td width="160px"><s:if test="%{mgt0NetworkType == 3}">
															<s:textfield name="dataSource.cfgIpAddress" size="15"
																maxlength="%{cfgIpAddressLength}"
																title="%{defaultIpPrefixFormat}" />
														</s:if> <s:else>
															<s:textfield name="dataSource.cfgIpAddress" size="15"
																maxlength="%{cfgIpAddressLength}" />
														</s:else></td>

														<td class="labelT1" width="60px"><s:text name="hiveAp.netmask" /></td>
														<td width="160px"><s:if
															test="%{mgt0NetworkType == 3}">
															<s:textfield name="dataSource.cfgNetmask" size="15"
																maxlength="%{cfgNetmaskLength}"
																title="%{defaultNetmaskFormat}" />
														</s:if> <s:else>
															<s:textfield name="dataSource.cfgNetmask" size="15"
																maxlength="%{cfgNetmaskLength}" />
														</s:else></td>

														<td width="100px" class="labelT1" id="gatewayLbTd"
															style="display: <s:property value="%{gatewayVisibilityStyle}"/>;"><s:text
															name="hiveAp.gateway" /></td>
														<td id="gatewayTd"
															style="display: <s:property value="%{gatewayVisibilityStyle}"/>;"><s:textfield
															name="dataSource.cfgGateway" size="15"
															maxlength="%{cfgGatewayLength}"
															/></td>

													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:if test="%{mgt0NetworkType == 3}">
															<td class="labelT1" id="dhcpTimeoutLbTd" width="250px"
																style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;">
														</s:if>
														<s:else>
															<td class="labelT1" id="dhcpTimeoutLbTd" width="120px"
																style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;">
														</s:else>
															<s:text name="hiveAp.dhcpTimeout" /></td>
														<td id="dhcpTimeoutTd" width="117px"
															style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;"><s:textfield
															name="dataSource.dhcpTimeout" size="15"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															maxlength="4" title="%{dhcpTimeoutRange}"
															/></td>

														<td class="headCheck" id="addressOnlyLbTd"
															style="padding-left: 50px;display: <s:property value="%{addressOnlyVisibilityStyle}"/>;"><s:checkbox
															name="dataSource.addressOnly"
															value="%{dataSource.addressOnly}"
															/></td>
														<td class="labelT1" id="addressOnlyTd"
															style="display: <s:property value="%{addressOnlyVisibilityStyle}"/>;"><s:text
															name="hiveAp.addressOnly" /></td>
													</tr>
												</table>
												</td>
											</tr>
											</table></td>
											</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="ethConfigStyle" style="display: <s:property value='ethConfigStyle'/>">
							<td><!-- Ethernet and Network Settings -->
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethNetwork.label" />','networkSettings');</script></td>
								</tr>
								<tr>
									<td>
									<div id="networkSettings"
										style="display: <s:property value="%{dataSource.networkSettingsDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										 <tr>
									    	<td colspan="2" class="noteInfo" style="padding-left: 20px;"><s:text name="hiveAp.interface.wanorder.note" /></td>
									    </tr>
										<tr id="hiveApNetworkSettings"
											style="display: <s:property value="%{hiveApNetworkSettingsStyle}"/>"><td><table cellspacing="0" cellpadding="0" border="0" width="100%">

											<tr id="ethModeSettings">
												<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr id="ethSetupTr">
														<td class="labelT1" width="140px"><s:text
															name="hiveAp.ethernet.setup" /></td>
														<td><s:select name="dataSource.ethConfigType"
															list="%{enumEthernetSetups}" listKey="key"
															listValue="value" cssStyle="width: 200px;"
															onchange="ethSetupChanged();" /></td>
														<td></td>
														<td></td>
													</tr>
													<tr id="eth0ModeTr">
														<td class="labelT1" width="140px" id="eth0ModeLabelTd"><s:text
															name="hiveAp.ethernet.eth0.mode" /></td>
														<td width="160px"><s:select
															name="dataSource.eth0.operationMode"
															onchange="eth0OperationModeChange(this.value);"
															value="%{dataSource.eth0.operationMode}"
															list="%{enumEthOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 200px;" /></td>
													</tr>
													<tr id="eth1ModeTr">
														<td class="labelT1" width="140px" id="eth1ModeLabelTd"><s:text
															name="hiveAp.ethernet.eth1.mode" /></td>
														<td width="160px"><s:select
															name="dataSource.eth1.operationMode"
															onchange="eth1OperationModeChange(this.value);"
															value="%{dataSource.eth1.operationMode}"
															list="%{enumEthOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 200px;" /></td>
													</tr>
													<tr id="red0ModeTr">
														<td class="labelT1" width="140px"><s:text
															name="hiveAp.ethernet.red0.mode" /></td>
														<td width="160px"><s:select
															name="dataSource.red0.operationMode"
															onchange="red0OperationModeChange(this.value);"
															value="%{dataSource.red0.operationMode}"
															list="%{enumRedOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 200px;" /></td>
													</tr>
													<tr id="agg0ModeTr">
														<td class="labelT1" width="140px"><s:text
															name="hiveAp.ethernet.agg0.mode" /></td>
														<td width="160px"><s:select
															name="dataSource.agg0.operationMode"
															onchange="agg0OperationModeChange(this.value);"
															value="%{dataSource.agg0.operationMode}"
															list="%{enumRedOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 200px;" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="radioConfigTr" style="display: none;">
												<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td class="labelT1" width="140px"><s:text
															name="hiveAp.radio.setup" /></td>
														<td></td>
														<td></td>
														<td></td>
													</tr>
													<tr>
														<td class="labelT1" width="140px">wifi0</td>
														<td width="170px"><s:select
															name="dataSource.wifi0.operationMode"
															value="%{dataSource.wifi0.operationMode}"
															list="%{enumWifiOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 152px"
															onchange="operationModeChanged(this.value,'wifi0')" /></td>
														<td id="customizeWifi1Label" class="labelT1" width="140px"
															style="visibility: <s:property value="%{customizeWifi1Style}"/>">wifi1</td>
														<td id="customizeWifi1Selector"
															style="visibility: <s:property value="%{customizeWifi1Style}"/>">
														<s:select name="dataSource.wifi1.operationMode"
															value="%{dataSource.wifi1.operationMode}"
															list="%{enumWifiOperationMode}" listKey="key"
															listValue="value" cssStyle="width: 152px"
															onchange="operationModeChanged(this.value,'wifi1')" /></td>
													</tr>
												</table>
												</td>
											</tr>

											<tr id="macLearningSettings">
												<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td height="5px"></td>
													</tr>
													<tr id="eth0MacLearningTr"
														style="display: <s:property value="%{eth0MacLearningStyle}"/>">
														<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth0 MAC Learning -->
														<fieldset><legend><s:text
															name="hiveAp.ethernet.eth0.settings" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<%-- <tr>
																						<td class="headCheck"><s:checkbox name="dataSource.eth0.macLearningEnabled" onclick="eth0MacLearningEnabled(this.checked);"/></td>
																						<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
																					</tr> --%>
																	<tr id="eth0MacLearningEnabledTr"<%--<style="display: <s:property value="%{eth0MacLearningEnabledStyle}"/>"--%>>
																		<td></td>
																		<td class="labelT1" style="padding-left: 0" width="127px"><s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
																		<td><s:textfield name="dataSource.eth0.idelTimeout"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			size="24" maxlength="4" /> <s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td style="padding-left: 4px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="5px"></td>
																	</tr>
																	<tr>
																		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','eth0BridgeAdvSettings');</script></td>
																	</tr>
																	<tr>
																		<td>
																		<div id="eth0BridgeAdvSettings"
																			style="display: <s:property value="%{dataSource.eth0BridgeAdvDisplayStyle}"/>">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr
																				style="display: <s:property value="%{fullModeConfigStyle}"/>">
																				<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="80px"><label><s:text
																							name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																						<td><s:select name="userProfileEth0"
																							cssStyle="width: 220px;" list="%{userProfiles}"
																							listKey="id" listValue="value" /></td>
																						<td><s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newUserProfileEth0')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />" width="16"
																								height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify" title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editUserProfileEth0')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify" title="Modify" /></a>
																						</s:else></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																				<fieldset><legend><s:text
																					name="hiveAp.ethernet.macLearning.mac.label" /></legend>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<s:push value="%{eth0MacOptions}">
																							<td colspan="3"><tiles:insertDefinition
																								name="optionsTransfer" /></td>
																						</s:push>
																					</tr>
																				</table>
																				</fieldset>
																				</td>
																			</tr>
																		</table>
																		</div>
																		</td>
																	</tr>
																</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
													<tr id="eth1MacLearningTr"
														style="display: <s:property value="%{eth1MacLearningStyle}"/>">
														<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth1 MAC Learning -->
														<fieldset><legend><s:text
															name="hiveAp.ethernet.eth1.settings" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<%-- <tr>
																						<td class="headCheck"><s:checkbox name="dataSource.eth1.macLearningEnabled" onclick="eth1MacLearningEnabled(this.checked);"/></td>
																						<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
																					</tr> --%>
																	<tr id="eth1MacLearningEnabledTr"<%--style="display: <s:property value="%{eth1MacLearningEnabledStyle}"/>" --%>>
																		<td></td>
																		<td class="labelT1" style="padding-left: 0" width="127px"><s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
																		<td><s:textfield name="dataSource.eth1.idelTimeout"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			size="24" maxlength="4" /> <s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td style="padding-left: 4px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="5px"></td>
																	</tr>
																	<tr>
																		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','eth1BridgeAdvSettings');</script></td>
																	</tr>
																	<tr>
																		<td>
																		<div id="eth1BridgeAdvSettings"
																			style="display: <s:property value="%{dataSource.eth1BridgeAdvDisplayStyle}"/>">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr
																				style="display: <s:property value="%{fullModeConfigStyle}"/>">
																				<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="80px"><label><s:text
																							name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																						<td><s:select name="userProfileEth1"
																							cssStyle="width: 220px;" list="%{userProfiles}"
																							listKey="id" listValue="value" /></td>
																						<td><s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newUserProfileEth1')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />" width="16"
																								height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify" title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editUserProfileEth1')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify" title="Modify" /></a>
																						</s:else></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																				<fieldset><legend><s:text
																					name="hiveAp.ethernet.macLearning.mac.label" /></legend>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<s:push value="%{eth1MacOptions}">
																							<td colspan="3"><tiles:insertDefinition
																								name="optionsTransfer" /></td>
																						</s:push>
																					</tr>
																				</table>
																				</fieldset>
																				</td>
																			</tr>
																		</table>
																		</div>
																		</td>
																	</tr>
																</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
													<tr id="agg0MacLearningTr"
														style="display: <s:property value="%{agg0MacLearningStyle}"/>">
														<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Agg0 MAC Learning -->
														<fieldset><legend><s:text
															name="hiveAp.ethernet.agg0.settings" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<%--<tr>
																						<td class="headCheck"><s:checkbox name="dataSource.agg0.macLearningEnabled" onclick="agg0MacLearningEnabled(this.checked);"/></td>
																						<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
																					</tr>--%>
																	<tr id="agg0MacLearningEnabledTr"<%--style="display: <s:property value="%{agg0MacLearningEnabledStyle}"/>"--%>>
																		<td></td>
																		<td class="labelT1" style="padding-left: 0" width="127px"><s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
																		<td><s:textfield name="dataSource.agg0.idelTimeout"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			size="24" maxlength="4" /> <s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td style="padding-left: 4px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="5px"></td>
																	</tr>
																	<tr>
																		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','agg0BridgeAdvSettings');</script></td>
																	</tr>
																	<tr>
																		<td>
																		<div id="agg0BridgeAdvSettings"
																			style="display: <s:property value="%{dataSource.agg0BridgeAdvDisplayStyle}"/>">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr
																				style="display: <s:property value="%{fullModeConfigStyle}"/>">
																				<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="80px"><label><s:text
																							name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																						<td><s:select name="userProfileAgg0"
																							cssStyle="width: 220px;" list="%{userProfiles}"
																							listKey="id" listValue="value" /></td>
																						<td><s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newUserProfileAgg0')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />" width="16"
																								height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify" title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editUserProfileAgg0')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify" title="Modify" /></a>
																						</s:else></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																				<fieldset><legend><s:text
																					name="hiveAp.ethernet.macLearning.mac.label" /></legend>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<s:push value="%{agg0MacOptions}">
																							<td colspan="3"><tiles:insertDefinition
																								name="optionsTransfer" /></td>
																						</s:push>
																					</tr>
																				</table>
																				</fieldset>
																				</td>
																			</tr>
																		</table>
																		</div>
																		</td>
																	</tr>
																</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
													<tr id="red0MacLearningTr"
														style="display: <s:property value="%{red0MacLearningStyle}"/>">
														<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth0 MAC Learning -->
														<fieldset><legend><s:text
															name="hiveAp.ethernet.red0.settings" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<%--<tr>
																						<td class="headCheck"><s:checkbox name="dataSource.red0.macLearningEnabled" onclick="red0MacLearningEnabled(this.checked);"/></td>
																						<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
																					</tr>--%>
																	<tr id="red0MacLearningEnabledTr"<%--style="display: <s:property value="%{red0MacLearningEnabledStyle}"/>"--%>>
																		<td></td>
																		<td class="labelT1" style="padding-left: 0" width="127px"><s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
																		<td><s:textfield name="dataSource.red0.idelTimeout"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			size="24" maxlength="4" /> <s:text
																			name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td style="padding-left: 4px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="5px"></td>
																	</tr>
																	<tr>
																		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','red0BridgeAdvSettings');</script></td>
																	</tr>
																	<tr>
																		<td>
																		<div id="red0BridgeAdvSettings"
																			style="display: <s:property value="%{dataSource.red0BridgeAdvDisplayStyle}"/>">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr
																				style="display: <s:property value="%{fullModeConfigStyle}"/>">
																				<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<td class="labelT1" width="80px"><label><s:text
																							name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																						<td><s:select name="userProfileRed0"
																							cssStyle="width: 220px;" list="%{userProfiles}"
																							listKey="id" listValue="value" /></td>
																						<td><s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newUserProfileRed0')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />" width="16"
																								height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify" title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editUserProfileRed0')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify" title="Modify" /></a>
																						</s:else></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																				<fieldset><legend><s:text
																					name="hiveAp.ethernet.macLearning.mac.label" /></legend>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td height="5px"></td>
																					</tr>
																					<tr>
																						<s:push value="%{red0MacOptions}">
																							<td colspan="3"><tiles:insertDefinition
																								name="optionsTransfer" /></td>
																						</s:push>
																					</tr>
																				</table>
																				</fieldset>
																				</td>
																			</tr>
																		</table>
																		</div>
																		</td>
																	</tr>
																</table>
																</td>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
												</table></td>
											</tr>
											<tr id="ethAdvanceSettings">
												<td style="padding-left: 10px;">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr id="ethAdvSettingsTr">
														<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.adv.label" />','ethAdvSettings');</script></td>
													</tr>
													<tr>
														<td>
														<div id="ethAdvSettings"
															style="display: <s:property value="%{dataSource.advEthSettingsDisplayStyle}"/>">
														<table cellspacing="0" cellpadding="0" border="0"
															width="100%">
															<tr>
																<td>
																<fieldset>
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%" class="embedded">
																	<tr>
																		<td height="10"></td>
																	</tr>
																	<tr>
																		<th align="left" width="50px"><s:text
																			name="hiveAp.lanIf" /></th>
																		<th align="left" width="70px"><s:text
																			name="hiveAp.if.adminState" /></th>
																		<th align="left" width="90px"><s:text
																			name="hiveAp.ethernet.multiple.native.vlan" />
																		<br>
																		<s:text name="hiveAp.ethernet.multiple.native.vlan.range"/></th>
																		<th align="left" width="80px"><s:text
																			name="hiveAp.if.allowedVlan" /></th>
																		<th align="left" width="110px"><s:text
																			name="hiveAp.if.duplex" /></th>
																		<th align="left" width="90px"><s:text
																			name="hiveAp.if.speed" /></th>
																		<th id="eth0RoleHeader" align="left" nowrap="nowrap"
																			style="display: <s:property value="eth1StuffStyle"/>"><s:text
																			name="hiveAp.if.bindRole" /></th>
																	</tr>
																	<tr>
																		<td height="5"></td>
																	</tr>
																	<tr id="eth0Row">
																		<td class="list"><s:text name="hiveAp.if.eth0" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth0.adminState"
																			value="%{dataSource.eth0.adminState}"
																			list="%{enumAdminStateType}" listKey="key"
																			listValue="value" /></td>
																		<td class="list"><s:textfield
																			name="dataSource.eth0.multiNativeVlan"  size="8"
																			maxlength="4" title="%{multiNativeVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');"/></td>
																		<td class="list"><s:textfield
																			name="dataSource.eth0.allowedVlan" size="8"
																			maxlength="255" title="%{allowedVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth0.duplex"
																			value="%{dataSource.eth0.duplex}"
																			list="%{enumDuplexType}" listKey="key"
																			listValue="value" cssStyle="width: 106px;" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth0.speed"
																			value="%{dataSource.eth0.speed}"
																			list="%{enumSpeedType}" listKey="key"
																			listValue="value" cssStyle="width: 84px;"
																			onchange="changeSpeed(this)" /></td>
																		<td id="eth0RoleColumn" class="list"
																			style="display: <s:property value="eth1StuffStyle"/>"><s:select
																			name="dataSource.eth0.bindRole"
																			value="%{dataSource.eth0.bindRole}"
																			list="%{enumBindRole}" listKey="key" listValue="value"
																			cssStyle="width: 80px;" /></td>
																	</tr>
																	<%-- add row 'eth1', 'red0', 'agg0' --%>
																	<tr id="eth1Row"
																		style="display: <s:property value="eth1StuffStyle"/>">
																		<td class="list"><s:text name="hiveAp.if.eth1" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth1.adminState"
																			value="%{dataSource.eth1.adminState}"
																			list="%{enumAdminStateType}" listKey="key"
																			listValue="value" /></td>
																		<td class="list"><s:textfield
																			name="dataSource.eth1.multiNativeVlan"  size="8"
																			maxlength="4" title="%{multiNativeVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');"/></td>
																		<td class="list"><s:textfield
																			name="dataSource.eth1.allowedVlan" size="8"
																			maxlength="255" title="%{allowedVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth1.duplex"
																			value="%{dataSource.eth1.duplex}"
																			list="%{enumDuplexType}" listKey="key"
																			listValue="value" cssStyle="width: 106px;" /></td>
																		<td class="list"><s:select
																			name="dataSource.eth1.speed"
																			value="%{dataSource.eth1.speed}"
																			list="%{enumSpeedType}" listKey="key"
																			listValue="value" cssStyle="width: 84px;" /></td>
																		<td id="eth1RoleColumn" class="list"><s:select
																			name="dataSource.eth1.bindRole"
																			value="%{dataSource.eth1.bindRole}"
																			list="%{enumBindRole}" listKey="key" listValue="value"
																			cssStyle="width: 80px;" /></td>
																	</tr>
																	<tr id="red0Row"
																		style="display: <s:property value="eth1StuffStyle"/>">
																		<td class="list"><s:text name="hiveAp.if.red0" /></td>
																		<td class="list"><s:select
																			name="dataSource.red0.adminState"
																			value="%{dataSource.red0.adminState}"
																			list="%{enumAdminStateType}" listKey="key"
																			listValue="value" /></td>
																		<td class="list"><s:textfield
																		name="dataSource.red0.multiNativeVlan"  size="8"
																		maxlength="4"/></td>

																		<td class="list"><s:textfield
																			name="dataSource.red0.allowedVlan" size="8"
																			maxlength="255" title="%{allowedVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																	<tr id="agg0Row"
																		style="display: <s:property value="eth1StuffStyle"/>">
																		<td class="list"><s:text name="hiveAp.if.agg0" /></td>
																		<td class="list"><s:select
																			name="dataSource.agg0.adminState"
																			value="%{dataSource.agg0.adminState}"
																			list="%{enumAdminStateType}" listKey="key"
																			listValue="value" /></td>
																		<td class="list"><s:textfield
																		name="dataSource.agg0.multiNativeVlan"  size="8"
																		maxlength="4"/></td>
																		<td class="list"><s:textfield
																			name="dataSource.agg0.allowedVlan" size="8"
																			maxlength="255" title="%{allowedVlanTitle}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</table>
																</fieldset>
																</td>
															</tr>
														</table>
														</div>
														</td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="ConfigMdmSettings">
												<td style="padding-left: 10px;">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr id="ConfigMdmContentTr">
														<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.ssid.advanced.mdm.enrollment.title" />','configMdmContent');</script></td>
													</tr>
													<tr>
														<td>
														<div id="configMdmContent"
															style="display: <s:property value="%{dataSource.configMdmContentDisplayStyle}"/>">
														<table cellspacing="0" cellpadding="0" border="0"
															width="100%">
															<tr>
																<td>
<!-- 											<tr id="ConfigMdmContent"> -->
<!-- 														<td style="padding-left: 10px; padding-bottom: 10px;" > -->
															<fieldset>
																		<table cellspacing="0" cellpadding="0" border="0" width="100%">
																			<tr>
																				<td class="labelT1" width="100px"  colspan="5">
																				<s:checkbox name="dataSource.enableMDM" onclick="enableMDMcheck(this.checked);"  />
																				<s:text name="config.ssid.advanced.mdm.enrollment.enable"/>
																				</td>
																				<td id="enablemdmselect" style="padding-top:6px;">		
																						<s:select name="configmdmId" list="%{configMdmList}" listKey="id" listValue="value" cssStyle="width: 140px;" />
																							
																							
																								<a class="marginBtn" href="javascript:submitAction('newConfigmdmPolicy')"><img class="dinl"
																								src="<s:url value="/images/new.png" />"
																								width="16" height="16" alt="New" title="New" /></a>
																							
																								<a class="marginBtn" href="javascript:submitAction('editConfigmdmPolicy')"><img class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify" title="Modify" /></a>
																						
																						</td>
																			</tr>
																 	</table>
																</fieldset>
																</td>
															</tr>
														</table>
														</div>
														</td>
													</tr>
												</table>
												</td>
											</tr>
										</table></td>
										</tr>
										<tr>
											<td>
											<div id="routerNetworkSetting"
												style="display: <s:property value="routerNetworkSettingStyle"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr id="radioConfigBR" style="display: none;">
													<td class="labelT1">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td>
																<fieldset>
																	<legend><s:text name="hiveAp.radio.setup" /></legend>
																	<table cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td class="labelT1" width="80px">wifi0</td>
																			<td width="270px"><s:select
																				name="wifi0ModeBR"
																				id="wifi0ModeBR"
																				value="%{dataSource.wifi0.operationMode}"
																				list="%{enumWifiOperationMode}" listKey="key"
																				listValue="value" cssStyle="width: 152px"
																				onchange="operationModeChanged(this.value,'wifi0'),changewifimode('wifi0');" /></td>
																			<td id="customizeWifi1LabelBR" class="labelT1" width="80px"
																				style="visibility: <s:property value="%{customizeWifi1Style}"/>">wifi1</td>
																			<td id="customizeWifi1SelectorBR"
																				style="visibility: <s:property value="%{customizeWifi1Style}"/>">
																			<s:select name="wifi1ModeBR"
																				id="wifi1ModeBR"
																				value="%{dataSource.wifi1.operationMode}"
																				list="%{enumWifiOperationMode}" listKey="key"
																				listValue="value" cssStyle="width: 152px"
																				onchange="operationModeChanged(this.value,'wifi1'),changewifimode('wifi1');" /></td>
																		</tr>
																	</table>
																</fieldset>
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="devicePortSettings">
													<td class="labelT1">
												
<fieldset>
	<legend>
		<s:text name="hiveAp.brRouter.port.settings" />
	</legend>
	<div id="routerdiv">
	<s:if test="{dataSource.deviceType!=3}">	
	<table id="interfaceEthSettingTable" cellspacing="0" cellpadding="0"
		border="0" width="900px" class="embedded">
		<tr>
			<td height="10"></td>
		</tr>
		<s:if test="%{dataSource.usbAsCellularModem}">
			<tr>
				<td colspan="10"> 
					<s:checkbox name="dataSource.enableCellularModem" id="dataSource.enableCellularModem" onclick="enableCellularModem(this.checked);"/> <s:text
						name="geneva_03.hiveAp.embededCellularModem.enable" />
				</td>
			</tr>
			<tr>
				<td height="5px" />
			</tr>
		</s:if>
		<tr id="br_head_setting">
			<th align="left" width="40px"><s:text
					name="hiveAp.brRouter.port.settings.port" /></th>
			<th align="left" width="40px"><s:text
					name="hiveAp.brRouter.port.settings.role" /></th>
			<th align="left" width="10px" style="display:none" ><s:text
					name="prority" /></th>
			<th id="head_priority" align="left" width="60px"><s:text
					name="hiveAp.brRouter.port.settings.priority" /></th>
			<th align="left" width="40px"><s:text
					name="hiveAp.brRouter.port.settings.adminState" /></th>
			<th align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.transmissionType" /></th>
			<th align="left" width="60px"><s:text
					name="hiveAp.brRouter.port.settings.speed" /></th>
			<th id="head_enableNat" align="left" width="40px"><s:text
					name="hiveAp.brRouter.port.settings.enableNat" /></th>
			<th id="head_disablePortForwarding" align="left" width="100px"><s:text
					name="hiveAp.brRouter.port.settings.disablePortForwarding" /></th>
			<th id="head_connectionType" align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.connectionType" /></th>
			<th id="head_staticIp" align="left" width="80px">
				<table>
					<tr><td><s:text
						name="hiveAp.brRouter.port.settings.staticIp" /></td></tr>
					<tr><td><s:text
					name="hiveAp.brRouter.port.settings.netmask" /></td></tr>			
				</table> 
			</th>
			<th id="head_defaultGateway" align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.defaultGateway" /></th>
			<th id="head_pppoeAuth" align="left" width="140px"><s:text
					name="hiveAp.brRouter.port.settings.pppoeAuth" /></th>
		</tr>
	 	<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth0andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth0_setting">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth0" /></td>
			<td class="list" id="eth0strTd">
				<s:text name="hiveAp.vpnGateway.if.wan" /> 
			</td>
			<td id="eth0_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth0.priority" /></td>
			<td class="list">
			<div id="eth0TdDiv" style="display:<s:property value="%{flageth0Str}" /> ">
			<s:select name="branchRouterEth0.wanOrder" id="eth0WanSelectListID"
					value="%{branchRouterEth0.wanOrder}" onchange="wanOrderTrigger('eth0WanSelectListID');"
					list="%{enumPriorityEth0LevelType}" listKey="key" listValue="value" />
			</div>
			</td>
			<td class="list"><s:select name="branchRouterEth0.adminState"
					value="%{branchRouterEth0.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth0.duplex"
					value="%{branchRouterEth0.duplex}" list="%{enumDuplexType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth0.speed"
					value="%{branchRouterEth0.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;" onchange="" /></td>
			<td id="eth0_enableNat" class="list"><s:checkbox
					name="branchRouterEth0.enableNat" /></td>
		  <td id="eth0_disablePortForwarding" class="list"><s:checkbox 
		           name="branchRouterEth0.disablePortForwarding" /></td>
			<td class="list" id="eth0ConnectTdDiv"><s:select
					name="branchRouterEth0.connectionType" id="branchRouterEth0_connectionType"
					value="%{branchRouterEth0.connectionType}"
					list="%{enumConnectionType1}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth0');"/></td>
			<td class="list" >
			<div id="eth0StaticIP" style="display:<s:property value='eth0StaticIpFlag'/>">
				<s:textfield name="branchRouterEth0.ipAndNetmask" id="branchRouterEth0_ipAddress"  onkeypress="return hm.util.keyPressPermit(event,'ipMask');"></s:textfield>
				</div>
			</td>
			<td class="list" >
				<div id="eth0DefaultGateway" style="display:<s:property value='eth0StaticIpFlag'/>">
					<s:textfield name="branchRouterEth0.gateway" id="branchRouterEth0_gateway"/>
				</div>
			</td>

			<td  class="list">
			<div id="eth0_pppoeAuthrouter" style="display:<s:property value='PppoeAuthFlag'/>">
				<s:select list="%{pppoeAuthProfiles}" listKey="id" listValue="value" id="eth0_pppoeAuthProfile"
					name="pppoeAuthProfile" cssStyle="width: 60px;" /> <s:if
					test="%{writeDisabled == 'disabled'}">
					<img class="dinl marginBtn"
						src="<s:url value="/images/new_disable.png" />" width="16"
						height="16" alt="New" title="New" />
				</s:if> <s:else>
					<a class="marginBtn" href="javascript:submitAction('newPPPoE')"><img
						class="dinl" src="<s:url value="/images/new.png" />" width="16"
						height="16" alt="New" title="New" /></a>
				</s:else> <s:if test="%{writeDisabled == 'disabled'}">
					<img class="dinl marginBtn"
						src="<s:url value="/images/modify_disable.png" />" width="16"
						height="16" alt="Modify" title="Modify" />
				</s:if> <s:else>
					<a class="marginBtn" href="javascript:submitAction('editPPPoE')"><img
						class="dinl" src="<s:url value="/images/modify.png" />" width="16"
						height="16" alt="Modify" title="Modify" /></a>
				</s:else>
				</div>
				</td>
		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth1andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth1_setting"
			style="display:<s:property value="%{lan1Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth1" /></td>
			<td class="list" id="eth1strTd"><s:property  value="%{eth1str}" /></td>
			<td id="eth1_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterEth1.priority" /></td>
			<td class="list" >
			<div id="eth1TdDiv" style="display:<s:property value="%{flageth1Str}" /> ">
					<s:select name="branchRouterEth1.wanOrder"
						value="%{branchRouterEth1.wanOrder}" id="eth1WanSelectListID" onchange="wanOrderTrigger('eth1WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
					</div>	
				</td>
			<td class="list"><s:select name="branchRouterEth1.adminState"
					value="%{branchRouterEth1.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" />
			</td>
			<td class="list"><s:select name="branchRouterEth1.duplex"
					value="%{branchRouterEth1.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="%{enableEthSetting}" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth1.speed"
					value="%{branchRouterEth1.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="%{enableEthSetting}" onchange="" /></td>
			<td id="eth1_enableNat" class="list"
				style="display:<s:property value="%{eth1RoleStyle}" /> "><s:checkbox
					name="branchRouterEth1.enableNat" /></td>
			<td id="eth1_disablePortForwarding" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth1.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth1ConnectTdDiv" style="display:<s:property value="%{flageth1Str}" /> ">
			<s:select	name="branchRouterEth1.connectionType" id="branchRouterEth1_connectionType"
					value="%{branchRouterEth1.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth1');"/>
			</div>
			</td>
			
			<td class="list" id="eth1StaticIP" style="display:<s:property value='eth1StaticIpFlag'/>">
				<s:textfield name="branchRouterEth1.ipAndNetmask" id="branchRouterEth1_ipAddress" onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/>
			</td>
			<td class="list" id="eth1DefaultGateway" style="display:<s:property value='eth1StaticIpFlag'/>">
				<s:textfield name="branchRouterEth1.gateway" id="branchRouterEth1_gateway"/>
			</td>
			<td class="list"></td>


		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth2andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth2_setting"
			style="display:<s:property value="%{lan2Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth2" /></td>
			<td class="list" id="eth2strTd"><s:property  value="%{eth2str}" /></td>
			<td id="eth2_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterEth2.priority" /></td>
			<td class="list" >
			<div id="eth2TdDiv" style="display:<s:property value="%{flageth2Str}" /> ">
					<s:select name="branchRouterEth2.wanOrder" id="eth2WanSelectListID" onchange="wanOrderTrigger('eth2WanSelectListID');"
						value="%{branchRouterEth2.wanOrder}" 
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth2.adminState"
					value="%{branchRouterEth2.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth2.duplex"
					value="%{branchRouterEth2.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth2.speed"
					value="%{branchRouterEth2.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth2_enableNat" class="list"
				style="display:<s:property value="%{eth2RoleStyle}" /> "><s:checkbox
					name="branchRouterEth2.enableNat" /></td>
			<td id="eth2_disablePortForwarding" class="list" 
			    style="display:<s:property value="%{eth2RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth2.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth2ConnectTdDiv" style="display:<s:property value="%{flageth2Str}" /> ">
			<s:select	name="branchRouterEth2.connectionType" id="branchRouterEth2_connectionType"
					value="%{branchRouterEth2.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth2');"/>
			</div>		
			</td>
				
			<td class="list" id="eth2StaticIP" style="display:<s:property value='eth2StaticIpFlag'/>">
				<s:textfield name="branchRouterEth2.ipAndNetmask" id="branchRouterEth2_ipAddress"></s:textfield>
			</td>
			<td class="list" id="eth2DefaultGateway" style="display:<s:property value='eth2StaticIpFlag'/>">
				<s:textfield name="branchRouterEth2.gateway" id="branchRouterEth2_gateway"/>
			</td>
			<td class="list"></td>

		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth3andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth3_setting"
			style="display:<s:property value="%{lan3Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth3" /></td>
			<td class="list" id="eth3strTd"><s:property  value="%{eth3str}" /></td>
			<td id="eth3_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterEth3.priority" /></td>
			<td class="list">
			<div id="eth3TdDiv" style="display:<s:property value="%{flageth3Str}" /> ">
					<s:select name="branchRouterEth3.wanOrder" id="eth3WanSelectListID" onchange="wanOrderTrigger('eth3WanSelectListID');"
						value="%{branchRouterEth3.wanOrder}"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth3.adminState"
					value="%{branchRouterEth3.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth3.duplex"
					value="%{branchRouterEth3.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth3.speed"
					value="%{branchRouterEth3.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth3_enableNat" class="list"
				style="display:<s:property value="%{eth3RoleStyle}" /> "><s:checkbox
					name="branchRouterEth3.enableNat" /></td>
			<td id="eth3_disablePortForwarding" class="list" 
			    style="display:<s:property value="%{eth3RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth3.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth3ConnectTdDiv" style="display:<s:property value="%{flageth3Str}" /> ">
			<s:select name="branchRouterEth3.connectionType" id="branchRouterEth3_connectionType"
					value="%{branchRouterEth3.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth3');"/>
			</div>		
					</td>
					
			<td class="list" id="eth3StaticIP" style="display:<s:property value='eth3StaticIpFlag'/>">
				<s:textfield name="branchRouterEth3.ipAndNetmask" id="branchRouterEth3_ipAddress"></s:textfield>
			</td>
			<td class="list" id="eth3DefaultGateway" style="display:<s:property value='eth3StaticIpFlag'/>">
				<s:textfield name="branchRouterEth3.gateway" id="branchRouterEth3_gateway"/>
			</td>
			<td class="list"></td>

		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth4andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth4_setting"
			style="display:<s:property value="%{lan4Style}" /> ">
			<td class="list" ><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth4" /></td>
			<td class="list" id="eth4strTd"><s:property  value="%{eth4str}" /></td>
			<td id="eth4_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterEth4.priority" /></td>
			<td class="list">
			<div id="eth4TdDiv" style="display:<s:property value="%{flageth4Str}" /> ">
					<s:select name="branchRouterEth4.wanOrder"
						value="%{branchRouterEth4.wanOrder}" id="eth4WanSelectListID" onchange="wanOrderTrigger('eth4WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth4.adminState"
					value="%{branchRouterEth4.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" />
			</td>
			<td class="list"><s:select name="branchRouterEth4.duplex"
					value="%{branchRouterEth4.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth4.speed"
					value="%{branchRouterEth4.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth4_enableNat" class="list"
				style="display:<s:property value="%{eth4RoleStyle}" /> "><s:checkbox
					name="branchRouterEth4.enableNat" /></td>
			<td id="eth4_disablePortForwarding" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth4.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth4ConnectTdDiv" style="display:<s:property value="%{flageth4Str}" /> ">
			<s:select 	name="branchRouterEth4.connectionType" id="branchRouterEth4_connectionType"
					value="%{branchRouterEth4.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth4');"/>
			</div>
			</td>
			
		
			<td class="list" id="eth4StaticIP" style="display:<s:property value='eth4StaticIpFlag'/>">
				<s:textfield name="branchRouterEth4.ipAndNetmask" id="branchRouterEth4_ipAddress"/>
			</td>
			<td class="list" id="eth4DefaultGateway" style="display:<s:property value='eth4StaticIpFlag'/>">
				<s:textfield name="branchRouterEth4.gateway" id="branchRouterEth4_gateway"/>
			</td>
			<td class="list"></td>


		</tr>
	 	<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_usbandgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_usb_setting" <s:if test="%{dataSource.enableCellularModem}">style="display:"</s:if><s:else>style="display:none"</s:else>>
			<td class="list">
			   <s:if test="%{dataSource.usbAsCellularModem}">
			   		<s:text name="hiveAp.autoProvisioning.device.if.port.cellularmodem" />   
			   </s:if>
			   <s:else>
			         <s:text name="hiveAp.autoProvisioning.br100.if.port.usb" />   
			   </s:else>
			</td>
			<td class="list" id="usbstrTd">
					<s:property  value="%{usbstr}" />
			<td class="list">
				<div id="usbTdDiv" style="display:<s:property value="%{flageUsbStr}" /> ">
					<s:select name="branchRouterUSB.wanOrder" id="usbWanSelectListID" onchange="wanOrderTrigger('usbWanSelectListID');"
							value="%{branchRouterUSB.wanOrder}" 
							list="%{enumPriorityUsbLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterUSB.adminState"
					value="%{branchRouterUSB.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"></td>
			<td class="list"></td>
<%-- 			<td class="list"><s:select name="branchRouterUSB.duplex"
					value="%{branchRouterUSB.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterUSB.speed"
					value="%{branchRouterUSB.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td> --%>
			<td id="usb_enableNat" class="list"><s:checkbox
					name="branchRouterUSB.enableNat" /></td>
			<td id="usb_disablePortForwarding" class="list"><s:checkbox 
			       name="branchRouterUSB.disablePortForwarding" /></td>
			<td >
			<div id="usbConnectTdDiv" style="display:none">
			<s:select
					name="branchRouterUSB.connectionType" id="branchRouterUSB_connectionType"
					value="%{branchRouterUSB.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'usb');"/>
			</div>
			</td>
							
			<td id="usbStaticIP" style="display:none">
				<s:textfield name="branchRouterUSB.ipAndNetmask" id="branchRouterUSB_ipAddress"/>
			</td>
			<td id="usbDefaultGateway" style="display:none" >
				<s:textfield name="branchRouterUSB.gateway" id="branchRouterUSB_gateway"/>
			</td>
			<td ></td>


		</tr>
	 	<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_wifi0andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_wifi0_setting" >
			<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0" />
			</td>
			<td class="list" id="wifi0strTd">
					<s:property  value="%{wifi0str}" />
			</td>
			<td id="wifi0_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterWifi0.priority" /></td>
			<td class="list">
					<s:select name="branchRouterWifi0.wanOrder"
						value="%{branchRouterWifi0.wanOrder}" id="wifi0WanSelectListID" onchange="wanOrderTrigger('wifi0WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
			</td>
			<td class="list"><s:select name="branchRouterWifi0.adminState"
					value="%{branchRouterWifi0.adminState}"
					list="%{enumAdminStateType}" listKey="key" listValue="value"
					cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterWifi0.duplex"
					value="%{branchRouterWifi0.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;display:none" /></td>
			<td class="list"><s:select name="branchRouterWifi0.speed"
					value="%{branchRouterWifi0.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;display:none"
					disabled="false" onchange="" /></td>
			<td id="eth5_enableNat" class="list"><s:checkbox
					name="branchRouterWifi0.enableNat" /></td>
			<td id="eth5_disablePortForwarding" class="list" ><s:checkbox 
			       name="branchRouterWifi0.disablePortForwarding" /></td>
			<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0.dhcp" /> 
			</td>
			<%-- <td class="list" >
			<s:select name="branchRouterWifi0.connectionType" id="branchRouterWifi0_connectionType"
					value="%{branchRouterWifi0.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'wifi0');"/>
			</td>	 --%>			
			<td class="list" id="wifi0StaticIP"   >
				<s:textfield name="branchRouterWifi0.ipAndNetmask" />
			</td>
			<td class="list" id="wifi0DefaultGateway" >
				<s:textfield name="branchRouterWifi0.gateway" />
			</td>
			<td class="list"></td>
		</tr>
 		<tr>
			<td height="5" colspan="10">
			<table>
				<tr>
					<td id="error_message_wifi1andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_wifi1_setting"  >
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.wifi1" /></td>
			<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.role.wan" />
			</td>
			<td id="wifi1_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterWifi1.priority" /></td>
			<td class="list">
					<s:select name="branchRouterWifi1.priority"
						value="%{branchRouterWifi1.priority}"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
			</td>
			<td class="list"><s:select name="branchRouterWifi1.adminState"
					value="%{branchRouterWifi1.adminState}"
					list="%{enumAdminStateType}" listKey="key" listValue="value"
					cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterWifi1.duplex"
					value="%{branchRouterWifi1.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterWifi1.speed"
					value="%{branchRouterWifi1.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth6_enableNat" class="list"><s:checkbox
					name="branchRouterWifi1.enableNat" /></td>
		    <td id="eth6_disablePortForwarding" class="list" ><s:checkbox 
			       name="branchRouterWifi1.disablePortForwarding" /></td>
			<td class="list" ><s:select
					name="branchRouterWifi1.connectionType" id="branchRouterWifi1_connectionType"
					value="%{branchRouterWifi1.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'wifi1');"/></td>
									
			<td class="list" id="wifi1StaticIP" >
				<s:textfield name="branchRouterWifi1.ipAndNetmask"></s:textfield>
			</td>
			<td class="list" id="wifi1DefaultGateway" >
				<s:textfield name="branchRouterWifi1.gateway"/>
			</td>
			<td class="list"></td>

		</tr>
		
	</table>
	</s:if>
	<s:else>
	<table id="interfaceEthSettingTable" cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
														<tr>
															<td height="10"></td>
														</tr>
														<tr id="br_head_setting">
															<th align="left" width="40px"><s:text
																name="hiveAp.brRouter.port.settings.port" /></th>
															<th align="left" width="80px"><s:text
																name="hiveAp.brRouter.port.settings.role" /></th>
															<th align="left" width="40px"><s:text
																name="hiveAp.brRouter.port.settings.adminState" /></th>
															<th align="left" width="80px"><s:text
																name="hiveAp.brRouter.port.settings.transmissionType" /></th>
															<th align="left" width="60px"><s:text
																name="hiveAp.brRouter.port.settings.speed" /></th>
															<th id="head_enableNat" align="left" width="62px"><s:text
																name="hiveAp.brRouter.port.settings.enableNat" /></th>
														    <th id="head_disablePortForwarding" align="left" width="100px"><s:text
					                                            name="hiveAp.brRouter.port.settings.disablePortForwarding" /></th>
															<th id="head_priority" align="left" width="60px"><s:text
																name="hiveAp.brRouter.port.settings.priority" /></th>
															<th id="head_enablePppoe" align="left" width="80px">
															 	<s:text name="hiveAp.brRouter.port.settings.enablePppoe"/></th>
															<th id="head_pppoeAuth" align="left" width="140px">
															 	<s:text name="hiveAp.brRouter.port.settings.pppoeAuth"/></th>
														</tr>
														<tr id="br_eth0_setting">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth0"/></td>
															<td class="list">
																<table cellspacing="0" cellpadding="0" border="0" width="100%"><tr>
																	<td id="br_eth0_setting_role_backhaul" style="display:none" >
																		<s:text name="hiveAp.autoProvisioning.br100.if.role.backhaul"/>
																	</td>
																	<td id="br_eth0_setting_role_wan" style="display:none" >
																		<s:text name="hiveAp.vpnGateway.if.wan"/>
																	</td>
																	<td id="br_eth0_setting_role">
																		<s:text name="hiveAp.autoProvisioning.br100.if.role.wan"/>
																		<s:hidden name="branchRouterEth0.role"/>
																	</td>
																</tr></table>
															</td>
															<td class="list"><s:select
																	name="branchRouterEth0.adminState"
																	value="%{branchRouterEth0.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth0.duplex"
																	value="%{branchRouterEth0.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth0.speed"
																	value="%{branchRouterEth0.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	onchange="" /></td>
															<td id="eth0_enableNat" class="list">
																<s:checkbox name="branchRouterEth0.enableNat" />
															</td>
															<td id="eth0_disablePortForwarding" class="list">
															   <s:checkbox  name="branchRouterEth0.disablePortForwarding" />
															</td>
															<td id="eth0_priority" class="list">
																<s:textfield name="branchRouterEth0.priority" size="4" maxlength="4"/>
															</td>
															<td id="eth0_enablePppoe" class="list"><s:checkbox name="dataSource.enablePppoe" onclick="enabledPppoeCheckBox(this.checked)"></s:checkbox></td>
															<td id="eth0_pppoeAuth" class="list" style="display: <s:property value='pppoeAuthProfileStyle'/>">
																<s:select list="%{pppoeAuthProfiles}" listKey="id" id="eth0_pppoeAuthProfile"
																	listValue="value" name="pppoeAuthProfile" cssStyle="width: 80px;" />
																	<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newPPPoE')"><img
																			class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('editPPPoE')"><img
																			class="dinl" src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else></td>
														</tr>
														<tr id="br_eth1_setting" style="display:<s:property value="%{lan1Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
															<td class="list">
																		<s:select name="branchRouterEth1.role"
																			value="%{branchRouterEth1.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(1,this.value);branchRouterEthChangedWarning(1,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth1.adminState"
																	value="%{branchRouterEth1.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth1.duplex"
																	value="%{branchRouterEth1.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="%{enableEthSetting}"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth1.speed"
																	value="%{branchRouterEth1.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="%{enableEthSetting}"
																	onchange="" /></td>
															<td id="eth1_enableNat" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth1.enableNat"/>
															</td>
															<td id="eth1_disablePortForwarding" class="list">
															   <s:checkbox  name="branchRouterEth1.disablePortForwarding" />
															</td>
															<td id="eth1_priority" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> ">
																<s:textfield name="branchRouterEth1.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth2_setting" style="display:<s:property value="%{lan2Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
															<td class="list">
																		<s:select name="branchRouterEth2.role"
																			value="%{branchRouterEth2.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(2,this.value);branchRouterEthChangedWarning(2,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth2.adminState"
																	value="%{branchRouterEth2.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth2.duplex"
																	value="%{branchRouterEth2.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth2.speed"
																	value="%{branchRouterEth2.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth2_enableNat" class="list" style="display:<s:property value="%{eth2RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth2.enableNat"/>
															</td>
															<td id="eth2_disablePortForwarding" class="list"  style="display:<s:property value="%{eth2RoleStyle}" /> ">
															    <s:checkbox  name="branchRouterEth2.disablePortForwarding" /></td>
															<td id="eth2_priority" class="list" style="display:<s:property value="%{eth2RoleStyle}" /> ">
																<s:textfield name="branchRouterEth2.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth3_setting" style="display:<s:property value="%{lan3Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth3"/></td>
															<td class="list">
																		<s:select name="branchRouterEth3.role"
																			value="%{branchRouterEth3.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(3,this.value);branchRouterEthChangedWarning(3,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth3.adminState"
																	value="%{branchRouterEth3.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth3.duplex"
																	value="%{branchRouterEth3.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth3.speed"
																	value="%{branchRouterEth3.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth3_enableNat" class="list" style="display:<s:property value="%{eth3RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth3.enableNat"/>
															</td>
															<td id="eth3_disablePortForwarding" class="list" style="display:<s:property value="%{eth3RoleStyle}" /> ">
															    <s:checkbox  name="branchRouterEth3.disablePortForwarding" />
															</td>
															<td id="eth3_priority" class="list" style="display:<s:property value="%{eth3RoleStyle}" /> ">
																<s:textfield name="branchRouterEth3.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth4_setting" style="display:<s:property value="%{lan4Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth4"/></td>
															<td class="list">
																		<s:select name="branchRouterEth4.role"
																			value="%{branchRouterEth4.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(4,this.value);branchRouterEthChangedWarning(4,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth4.adminState"
																	value="%{branchRouterEth4.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth4.duplex"
																	value="%{branchRouterEth4.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth4.speed"
																	value="%{branchRouterEth4.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth4_enableNat" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth4.enableNat"/>
															</td>
															<td id="eth4_disablePortForwarding" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
															<s:checkbox name="branchRouterEth4.disablePortForwarding" /></td>
															<td id="eth4_priority" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
																<s:textfield name="branchRouterEth4.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_usb_setting">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.usb"/></td>
															<td class="list"><s:select
																	name="branchRouterUSB.role"
																	value="%{branchRouterUSB.role}"
																	list="%{enumUsbRoleType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="branchRouterUSBChanged(this.value); branchRouterUsbChangedWarning(this.value)" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.adminState"
																	value="%{branchRouterUSB.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.duplex"
																	value="%{branchRouterUSB.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.speed"
																	value="%{branchRouterUSB.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="usb_enableNat" class="list">
																<s:checkbox name="branchRouterUSB.enableNat" />
															</td>
															<td id="usb_disablePortForwarding" class="list">
																<s:checkbox name="branchRouterUSB.disablePortForwarding" />
															</td>
															<td id="usb_priority" class="list">
																<s:textfield name="branchRouterUSB.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_wifi0_setting" style="display:<s:property value="%{wifi0Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0"/></td>
															<td class="list">
																		<s:select name="branchRouterWifi0.role"
																			value="%{branchRouterWifi0.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(5,this.value);branchRouterEthChangedWarning(5,this.value);changeWifiClinetMode('wifi0',this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterWifi0.adminState"
																	value="%{branchRouterWifi0.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi0.duplex"
																	value="%{branchRouterWifi0.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi0.speed"
																	value="%{branchRouterWifi0.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth5_enableNat" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi0.enableNat"/>
															</td>
															<td id="eth5_disablePortForwarding" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi0.disablePortForwarding"/>
															</td>
															<td id="eth5_priority" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:textfield name="branchRouterWifi0.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_wifi1_setting" style="display:<s:property value="%{wifi1Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi1"/></td>
															<td class="list">
																		<s:select name="branchRouterWifi1.role"
																			value="%{branchRouterWifi1.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(6,this.value);branchRouterEthChangedWarning(6,this.value);changeWifiClinetMode('wifi1',this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterWifi1.adminState"
																	value="%{branchRouterWifi1.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi1.duplex"
																	value="%{branchRouterWifi1.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi1.speed"
																	value="%{branchRouterWifi1.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth6_enableNat" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi1.enableNat"/>
															</td>
															<td id="eth6_disablePortForwarding" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
															      <s:checkbox name="branchRouterWifi1.disablePortForwarding" />
															 </td>
															<td id="eth6_priority" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
																<s:textfield name="branchRouterWifi1.priority" size="4" maxlength="4"/>
															</td>
														</tr>
													</table>
	</s:else>
</div>

	<div id="routingPolicySettings">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td height="10px" />
			</tr>
			<tr>
				<td><s:checkbox name="enabledOverrideRoutingPolicy"
						onclick="enabledOverrideRoutingPolicyFc(this.checked);" /> <s:text
						name="hiveAp.routingPolicy.override" /></td>
			</tr>
			<tr style="display:<s:property value='routingPolicyDetailDivStyle'/>"
				id="routingPolicyDetailDiv">
				<td style="padding-left: 30px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="120px"><s:text
									name="config.networkpolicy.routingPolicy.title" /></td>
							<td width="200px"><s:select name="routingPolicyId"
									list="%{list_routingPolicy}" listKey="id" listValue="value"
									cssStyle="width: 140px;" /> <s:if
									test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />" width="16"
										height="16" alt="New" title="New" />
								</s:if> <s:else>
									<a class="marginBtn"
										href="javascript:submitAction('newRoutingPbrPolicy')"><img
										class="dinl" src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
								</s:else> <s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />" width="16"
										height="16" alt="Modify" title="Modify" />
								</s:if> <s:else>
									<a class="marginBtn"
										href="javascript:submitAction('editRoutingPbrPolicy')"><img
										class="dinl" src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>

													<div id="voipSettings">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td height="10px"/>
														</tr>
														<tr>
															<td> <s:checkbox name="dataSource.enabledOverrideVoipSetting" onclick="enabledOverrideVoip(this.checked);"/>
																<s:text name="hiveAp.brRouter.port.settings.voipTitle"/></td>
														</tr>
														<tr id="voipDetailDiv" style="display:<s:property value='voipDetailDivStyle'/>">
															<td style="padding-left: 15px">
																<table cellspacing="0" cellpadding="0" border="0"  class="embedded">
																	<tr>
																		<th align="left" class="noUnderline" width="50px">&nbsp;</th>
																		<th align="left" class="noUnderline" width="230px" colspan="2"><s:text
																			name="hiveAp.brRouter.port.settings.voipLimitDn" /></th>
																		<th align="left" class="noUnderline" width="230px" colspan="2"><s:text
																			name="hiveAp.brRouter.port.settings.voipLimitUp" /></th>
																	</tr>
																	<tr>
																		<th align="left" width="50px"><s:text
																			name="hiveAp.brRouter.port.settings.port" /></th>
																		<th align="left" width="50px" ><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
																		<th align="left" width="180px"><s:text name="hiveAp.brRouter.port.settings.voipMaxRate"/></th>
																		<th align="left" width="50px" ><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
																		<th align="left" width="180px"><s:text name="hiveAp.brRouter.port.settings.voipMaxRate"/></th>
																	</tr>
																	<tr>
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth0"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth0.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth0_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth0.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth0DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth0.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth0_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth0.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth0UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	
																	<tr id="eth1_voip_detail" style="display:<s:property value="%{eth1RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth1.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth1_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth1.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth1DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth1.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth1_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth1.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth1UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr id="eth2_voip_detail" style="display:<s:property value="%{eth2RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth2.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth2_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth2.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth2DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth2.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth2_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth2.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth2UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr id="eth3_voip_detail" style="display:<s:property value="%{eth3RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth3"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth3.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth3_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth3.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth3DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth3.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth3_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth3.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth3UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr id="eth4_voip_detail" style="display:<s:property value="%{eth4RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth4"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth4.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth4_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth4.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth4DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterEth4.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterEth4_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterEth4.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipEth4UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr>
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.usb"/></td>
																		<td class="list"><s:checkbox name="branchRouterUSB.enableMaxDownload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterUSB_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterUSB.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipUsbDnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterUSB.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterUSB_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterUSB.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipUsbUpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr id="eth5_voip_detail" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0"/></td>
																		<td class="list"><s:checkbox name="branchRouterWifi0.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterWifi0_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterWifi0.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipWifi0DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterWifi0.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterWifi0_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterWifi0.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipWifi0UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																	<tr id="eth6_voip_detail" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
																		<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi1"/></td>
																		<td class="list"><s:checkbox name="branchRouterWifi1.enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_branchRouterWifi1_maxDownload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterWifi1.maxDownload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipWifi1DnReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																		<td class="list"><s:checkbox name="branchRouterWifi1.enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_branchRouterWifi1_maxUpload')"></s:checkbox></td>
																		<td class="list"><s:textfield name="branchRouterWifi1.maxUpload" size="5" maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				readonly="%{voipWifi1UpReadOnly}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
													</div>

													<div id="pseSettingAllDiv">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td height="10px"></td>
														</tr>
														<tr>
															<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.brRouter.pse.settings" />','pseSettingDiv');</script></td>
														</tr>
														<tr>
															<td>
																<div id="pseSettingDiv" style="display: <s:property value="%{dataSource.pseSettingsDisplayStyle}"/>">
																	<table cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr><td height="10px"/>
																		</tr>
																		<tr>
																			<td valign="top" style="padding-left: 15px">
																				<table cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
																					<tr>
																						<th align="left" width="50px"><s:text name="hiveAp.brRouter.port.settings.port" /></th>
																						<th align="left" width="80px"><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
																						<th align="left" width="160px"><s:text name="hiveAp.brRouter.pse.settings.mode"/></th>
																						<th align="left" width="150px"><s:text name="hiveAp.brRouter.pse.settings.priority"/></th>
																					</tr>
																					<tr>
																						<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
																						<td class="list"><s:checkbox name="branchRouterEth1.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth1PseState')"></s:checkbox></td>
																						<td class="list"><s:select name="branchRouterEth1.pseState" id="eth1PseState" cssStyle="width: 130px;"
																								value="%{branchRouterEth1.pseState}" list="%{enumPseType}" listKey="key"
																								listValue="value" disabled="branchRouterEth1.disabledPseState"/></td>
																						<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																							list="#{'0':''}" value="%{radioPsePriority}" />&nbsp;</td>
																					</tr>
																					<tr>
																						<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
																						<td class="list"><s:checkbox name="branchRouterEth2.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth2PseState')"></s:checkbox></td>
																						<td class="list"><s:select name="branchRouterEth2.pseState" id="eth2PseState" cssStyle="width: 130px;"
																								value="%{branchRouterEth2.pseState}" list="%{enumPseType}" listKey="key"
																								listValue="value" disabled="branchRouterEth2.disabledPseState"/></td>
																						<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																							list="#{'1':''}" value="%{radioPsePriority}" />&nbsp;</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
												<tr id="usbSettings">
													<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding-left: 10px">
																<fieldset>
																	<legend>
																		<s:if test="%{dataSource.br200LteVZ}">
																			<s:text name="geneva_03.hiveAp.brRouter.cellular.modem.settings" />
																		</s:if>
																		<s:else>
																			<s:text name="hiveAp.brRouter.usb.modem.settings" />
																		</s:else>
																	</legend>
																	<table cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td height="10"></td>
																		</tr>
																		<tr id= "usbConnectionModelTr">
																			<td width="120"></td>
																			<td><s:radio label="Gender"
																					name="dataSource.usbConnectionModel" list="%{usbConnectNeeded}"
																					listKey="key" listValue="value"
																					onclick="this.blur();" /></td>
																			<td width="50"></td>
																			<td><s:radio label="Gender"
																					name="dataSource.usbConnectionModel" list="%{usbConnectAlways}"
																					listKey="key" listValue="value"
																					onclick="this.blur();" /></td>
																			<td width="120"></td>
																		</tr>
																		<tr>
																			<td height="10"></td>
																		</tr>
																		<!--
																		<tr>
																			<td colspan="10">
																				<input type="button" name="ignore" value="Remove" style="width:80px"
																					class="button" onClick=""
																					<s:property value="writeDisabled" /> />
																				<input type="button" name="ignore" value="Cancel" style="width:80px"
																					class="button" onClick=""
																					<s:property value="writeDisabled" /> />
																			</td>
																		</tr>
																		 -->
																		<tr>
																			<td colspan="10"><table cellspacing="0" cellpadding="0" border="0" width="100%">
																				<tr>
																				<!--
																					<th align="left" style="padding-left: 0;" width="30"><input
																						type="checkbox" id="checkAll"
																						onClick="toggleCheckAllDynamicRoutes(this);"></th>
																				 -->
																					<th align="left" width="180"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.modem" /></th>
																					<th align="left" width="80"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.apn" /></th>
																					<th align="left" width="80" nowrap="nowrap" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.dialup" /></th>
																					<th align="left" width="120" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.userId" /></th>
																					<th align="left" width="80" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.password" /></th>
																					<th align="left" width="90" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																						name="hiveAp.brRouter.usb.obscure.password" /></th>
																					<th align="left" width="100" style="display:<s:property value='modemUsb4Br200LteStyle'/>"><s:text
																						name="hiveAp.brRouter.usb.modemProfile.cellularMode"/></th>
																				</tr>
																				<s:iterator value="%{dataSource.usbModemList}"
																					status="status">
																					<tr>
																					<!--
																						<td class="listCheck"><s:checkbox
																							name="usbConnectionIndices"
																							fieldValue="%{#status.index}" /></td>
																					 -->
																						<td class="list"><s:property value="displayName"/></td>
																						<td style="display:none"><s:textfield name="modemName" value="%{modemName}" /></td>
																						<td style="display:none"><s:textfield name="displayName" value="%{displayName}" /></td>
																						<td class="list"><s:textfield name="apn" value="%{apn}" size="10" /></td>
																						<td class="list" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:textfield name="dialupNum" value="%{dialupNum}" size="8" /></td>
																						<td class="list" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:textfield name="userId" value="%{userId}" size="16" /></td>
																						<td class="list" colspan="2" style="display:<s:property value='modemUsb2OtherStyle'/>">
																							<s:password name="password" value="%{password}" showPassword="true" size="10" 
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:textfield name="password_text" value="%{password}" size="10" disabled="true" cssStyle="display: none"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:checkbox name="ignore" value="true" cssStyle="margin-left: 20px" onclick="hm.util.toggleObscurePasswordList(this, 'password', 'password_text');" disabled="%{writeDisable4Struts}"/>
																						</td>
																						<td class="list" style="display:<s:property value='modemUsb4Br200LteStyle'/>"><s:select name="cellularMode" list="%{enumCellularMode}" value="%{cellularMode}" listKey="key" listValue="value" /></td>
																					</tr>
																				</s:iterator>
																				<!--
																				<s:if test="%{usbGridCount > 0}">
																					<s:generator separator="," val="%{' '}"
																						count="%{gridCount}">
																						<s:iterator>
																							<tr>
																								<td class="list" colspan="6">&nbsp;</td>
																							</tr>
																						</s:iterator>
																					</s:generator>
																				</s:if>
																				 -->
																			</table></td>
																		</tr>
																	</table>
																</fieldset>
															</td>
														</tr>
													</table></td>
												</tr>
												<tr id="vrrpSettings" style="display:none">
													<td><table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1">
																<s:checkbox name="branchRouterVrrpEnable" onclick="branchRouterVrrpEnableChanged(this.checked);"/>
																<s:text name="hiveAp.vpnGateway.enableVRRP"/>
															</td>
														</tr>
														<tr id="brRouterVrrpSetting"
															style="display: <s:property value='brRouterVrrpSettingStyle'/>">
															<td style="padding-left: 26px">
															<fieldset>
																<legend><s:text name="hiveAp.brRouter.vrrp.settings" /></legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1">
																			<s:text name="hiveAp.brRouter.vrrp.vrid"/><font color="red"><s:text name="*" /></font>
																		</td>
																		<td>
																			<s:textfield name="vrrpId" size="12" maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			<s:text name="hiveAp.brRouter.vrrp.vrid.range"/>
																		</td>
																		<td width="45"></td>
																		<td colspan="2" class="labelT1">
																			<s:checkbox name="branchRouterPreemptEnable"/>
																			<s:text name="hiveAp.brRouter.vrrp.enable.preempt"/>
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<s:text name ="hiveAp.vpnGateway.virtual.wanIp"/><font color="red"><s:text name="*" /></font>
																		</td>
																		<td>
																			<s:textfield name="branchRouterVirtualWanIp" size="18" maxlength="15"
																				onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
																		</td>
																		<td width="45"></td>
																		<td class="labelT1">
																			<s:text name ="hiveAp.brRouter.vrrp.vrid.priority" /><font color="red"><s:text name="*" /></font>
																		</td>
																		<td>
																			<s:textfield name="vrrpPriority" size="15" maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			<s:text name ="hiveAp.brRouter.vrrp.vrid.priority.range"/>
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<s:text name ="hiveAp.vpnGateway.virtual.lanIp"/><font color="red"><s:text name="*" /></font>
																		</td>
																		<td>
																			<s:textfield name="branchRouterVirtualLanIp" size="18" maxlength="15"
																				onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
																		</td>
																		<td width="45"></td>
																		<td class="labelT1">
																			<s:text name ="hiveAp.brRouter.vrrp.vrid.delay"/><font color="red"><s:text name="*" /></font>
																		</td>
																		<td>
																			<s:textfield name="dataSource.vrrpDelay" size="15" maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			<s:text name ="hiveAp.brRouter.vrrp.vrid.delay.range"/>
																		</td>
																	</tr>
																</table>
															</fieldset>
															</td>
														</tr>
													</table></td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="ssidBindConfigStyle" style="display: <s:property value='ssidBindConfigStyle'/>">
							<td><!-- SSID Allocation -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ssidAllocation.label" />','ssidAllocation');</script></td>
								</tr>
								<tr>
									<td>
									<div id="ssidAllocation"
										style="display: <s:property value="%{dataSource.ssidAllocationDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td valign="top">
											<table cellspacing="0" cellpadding="0" border="0"
												width="600px">
												<tr>
													<td id="wifi2GSsidAllocation" valign="top"
														style="display: <s:property value="%{wifi2GSsidAllocationStyle}"/>">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<th width="10px"><input type="checkbox"
																id="checkAll_0"
																onClick="toggleCheckAllWifiSsids(this,'ssid0Indices');"></th>
															<th nowrap="nowrap" align="left"
																style="padding-left: 10px"><s:text
																name="hiveAp.ssidAllocation.wifi0.label" /></th>
														</tr>
														<tr valign="top">
															<td colspan="2">
																<s:hidden name="ssid0Indices" />
															</td>
														</tr>
														<tr>
															<td colspan="10" id="wifi0ssidTable">
															<table cellspacing="0" cellpadding="0" border="0">
																<s:iterator value="%{wifi0Ssids}" status="status">
																	<tr valign="top">
																		<td style="padding: 5px 3px;" width="10px"><s:checkbox
																			name="ssid0Indices"
																			onclick="setCheckBoxSelected('checkAll_0', 'ssid0Indices');"
																			fieldValue="%{ssid}" value="%{checked}" /></td>
																		<td class="labelT1"><s:label value="%{ssidName}"
																			title="%{tooltip}" /></td>
																	</tr>
																</s:iterator>
															</table>
															</td>
														</tr>
													</table>
													</td>
													<td id="wifi2GSsidAllocationSpacer" nowrap="nowrap"
														style="display: <s:property value="%{wifi2GSsidAllocationStyle}"/>">&nbsp;&nbsp;</td>
													<td id="wifi5GSsidAllocation" valign="top"
														style="display: <s:property value="%{wifi5GSsidAllocationStyle}"/>">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<th width="10px"><input type="checkbox"
																id="checkAll_1"
																onClick="toggleCheckAllWifiSsids(this,'ssid1Indices');"></th>
															<th nowrap="nowrap" align="left"
																style="padding-left: 10px"><s:text
																name="hiveAp.ssidAllocation.wifi1.label" /></th>
														</tr>
														<tr valign="top">
															<td colspan="2">
																<s:hidden name="ssid1Indices" />
															</td>
														</tr>
														<tr>
															<td colspan="10" id="wifi1ssidTable">
															<table cellspacing="0" cellpadding="0" border="0">
																<s:iterator value="%{wifi1Ssids}" status="status">
																	<tr valign="top">
																		<td style="padding: 5px 3px;" width="10px"><s:checkbox
																			name="ssid1Indices"
																			onclick="setCheckBoxSelected('checkAll_1', 'ssid1Indices');"
																			fieldValue="%{ssid}" value="%{checked}" /></td>
																		<td class="labelT1"><s:label value="%{ssidName}"
																			title="%{tooltip}" /></td>
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
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<!-- tr id="lanBindConfig" style="display: <s:property value='lanBindConfigStyle'/>" -->
						<tr id="lanBindConfig" style="display:none">
							<td><!-- LAN Profile Allocation -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.brRouter.lan.allocation" />','lanAllocation');</script></td>
								</tr>
								<tr>
									<td>
									<div id="lanAllocation"
										style="display: <s:property value="%{dataSource.lanAllocationDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td valign="top">
											<table cellspacing="0" cellpadding="0" border="0"
												width="600px">
												<tr>
													<!-- LAN1  -->
													<td valign="top">
														<div id="lan1Allocation" style="display:<s:property value="%{lan1Style}"/>">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<th width="10px"><s:checkbox name="dataSource.enableLan_1" /></th>
																		<th nowrap="nowrap" align="left"
																			style="padding-left: 3px"><s:text
																			name="hiveAp.brRouter.lan.enableEth1" /></th>
																	</tr>
																</table>
																</td>
																<td nowrap="nowrap" width="20px">&nbsp;&nbsp;</td>
															</tr>
														</table>
														</div>
													</td>

													<!-- LAN2  -->
													<td valign="top">
														<div id="lan2Allocation" style="display:<s:property value="%{lan2Style}"/>">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<th width="10px"><s:checkbox name="dataSource.enableLan_2" /></th>
																		<th nowrap="nowrap" align="left"
																			style="padding-left: 3px"><s:text
																			name="hiveAp.brRouter.lan.enableEth2" /></th>
																	</tr>
																</table>
																</td>
																<td nowrap="nowrap" width="20px">&nbsp;&nbsp;</td>
															</tr>
														</table>
														</div>
													</td>

													<!-- LAN3  -->
													<td valign="top">
														<div id="lan3Allocation" style="display:<s:property value="%{lan3Style}"/>">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<th width="10px"><s:checkbox name="dataSource.enableLan_3" /></th>
																		<th nowrap="nowrap" align="left"
																			style="padding-left: 3px"><s:text
																			name="hiveAp.brRouter.lan.enableEth3" /></th>
																	</tr>
																</table>
																</td>
																<td nowrap="nowrap" width="20px">&nbsp;&nbsp;</td>
															</tr>
														</table>
														</div>
													</td>

													<!-- LAN4  -->
													<td valign="top">
														<div id="lan4Allocation" style="display:<s:property value="%{lan4Style}"/>">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<th width="10px"><s:checkbox name="dataSource.enableLan_4" /></th>
																		<th nowrap="nowrap" align="left"
																			style="padding-left: 3px"><s:text
																			name="hiveAp.brRouter.lan.enableEth4" /></th>
																	</tr>
																</table>
																</td>
																<td nowrap="nowrap" width="20px">&nbsp;&nbsp;</td>
															</tr>
														</table>
														</div>
													</td>

												</tr>
												<tr>
													<td height="20"></td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="brStaticRoutingStyle" style="display: <s:property value='brStaticRoutingStyle'/>">
							<td><!-- BR Static Routing-->
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.staticRoutes" />','brStaticRouting');</script></td>
									</tr>
									<tr>
										<td style="padding-left:20px;">
										<div id="brStaticRouting" style="display: <s:property value="%{dataSource.brStaticRoutingDisplayStyle}"/>">
										<fieldset>
											<table cellspacing="0" cellpadding="0" border="0" class="embedded">
												<tr>
													<td height="10"></td>
												</tr>
												<tr id="newButtonBrStaticRouting">
													<td colspan="3" style="padding-bottom: 2px;" >
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore" value="New"
																	class="button" onClick="showCreateSection('brStaticRouting');"
																	<s:property value="writeDisabled" />></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" <s:property value="writeDisabled" />
																	onClick="doRemoveBrStaticRouting();"></td>
																<td>
																	<a style="padding-left: 15px;" href="javascript: openPortDetails();"><s:text name="hvieAp.brstaticRoute.portDetails"/></a>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr style="display:none" id="createButtonBrStaticRouting">
													<td colspan="3" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" <s:property value="writeDisabled" /> onClick="doAddBrStaticRouting();"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="doRemoveBrStaticRouting();"></td>
															<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																class="button" onClick="hideCreateSection('brStaticRouting');"></td>
															<td>
																<a style="padding-left: 15px;" href="javascript: openPortDetails();"><s:text name="hvieAp.brstaticRoute.portDetails"/></a>
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="headerSectionBrStaticRouting">
													<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<th align="left" style="padding-left: 0;" width="30px"><input
																	type="checkbox" id="checkAllBrStaticRouting"
																	onClick="toggleCheckAllBrStaticRouting(this);"></th>
																<th align="left" width="200px"><s:text
																	name="config.routingProfiles.ip" /></th>
																<th align="left" width="200px"><s:text
																	name="config.routingProfiles.netmask" /></th>
																<th align="left" width="200px"><s:text
																	name="config.routingProfiles.gateway" /></th>
																<th align="left" width="200px"><s:text
																	name="hiveAp.br.staticrouting.advertise" /></th>
															</tr>
														</table>
													</td>
												</tr>
												<tr style="display:none" id="createSectionBrStaticRouting">
													<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="listHead" width="30px">&nbsp;</td>
																<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteIpInput" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteMaskInput" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteGwInput" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="listHead" valign="top" align ="center" width="200px"><s:checkbox name="advertiseCvg" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
													<table border="0" cellspacing="0" cellpadding="0" id="tbbrroute_id">
														<tr>
															<td id="error_message_static_route"/>
														</tr>
													<s:iterator value="%{dataSource.ipRoutes}" status="status">
														<tr>
															<td class="listCheck" width="30px"><s:checkbox name="brStaticRouteingIndices"
																fieldValue="%{#status.index}" /></td>
															<td class="list" width="200px"><s:property value="sourceIp" /></td>
															<td class="list" width="200px"><s:property value="netmask" /></td>
															<td class="list" width="200px"><s:property value="gateway" /></td>
															<td class="list" width="200px" align ="center"><s:checkbox name ="advertiseCvg" disabled="true" /></td>
														</tr>
													</s:iterator>
													</table>
													</td>
												</tr>
												<tr>
													<td colspan="5" width="100%">
													<table id="intBrStaticRouitngTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
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
												</td>
											</tr>
											</table>
											</fieldset>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr id="serviceSettingStyle" style="display: <s:property value='serviceSettingStyle'/>">
							<td><!-- Service Settings -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.service.label" />','serviceSettings');</script></td>
								</tr>
								<tr>
									<td>
									<div id="serviceSettings"
										style="display: <s:property value="%{dataSource.serviceSettingsDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr id="brAsRadiusServerDisplayDiv" style="display: <s:property value="%{brAsRadiusServerDisplayStyle}"/>">
													<td colspan="4" style="padding: 2px 0 2px 6px">
														<s:checkbox name="dataSource.enabledBrAsRadiusServer" onclick="changeEnabledBrAsRadiusServer(this.checked)"></s:checkbox>
														<s:text name="hiveAp.enableBrAsRadiusServer"/>
													</td>
												</tr>
												<tr id="brAsRadiusServerOverrideDisplayDiv" style="display: <s:property value="%{brAsRadiusServerOverrideDisplayDiv}"/>">
													<td colspan="4" style="padding: 2px 0 2px 26px">
														<s:checkbox name="dataSource.enabledOverrideRadiusServer" onclick="changeEnabledBrAsRadiusServerOverride(this.checked)"></s:checkbox>
														<s:text name="hiveAp.enableBrAsRadiusServerOverride"/>
													</td>
												</tr>
												<tr id="radiusServerRowDiv" style="display: <s:property value="%{radiusServerRowDivStyle}"/>">
													<td colspan="10"><table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td id="radioServerHeadTd">&nbsp;</td>
															<td class="labelT1" width="150px" style="padding-right: 20px;">
																<span id="radiusServerLabelId">
																<s:property value="radiusServerLabelValue"/></span></td>
															<td><s:select list="%{radiusServers}" listKey="id"
																listValue="value" name="radiusServer"
																cssStyle="width: 150px;" />
															<s:if test="%{!easyMode}">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('newRadius')"><img
																		class="dinl" src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('editRadius')"><img
																		class="dinl" src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
															</s:if></td>
														</tr>
													</table></td>
												</tr>
												<tr id="radiusProxyRowDiv" style="display: <s:property value="%{radiusProxyRowDivStyle}"/>">
													<td colspan="10">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td id="radioProxyHeadTd">&nbsp;</td>
																<td class="labelT1" width="150px"
																	style="padding-right: 20px;"><span id="radiusProxyLabelId">
																	<s:property value="radiusProxyLabelValue"/> </span></td>
																<td><s:select list="%{radiusProxys}" listKey="id"
																	listValue="value" name="radiusProxy"
																	cssStyle="width: 150px;" />
																<s:if test="%{!easyMode}">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newRadiusProxy')"><img
																			class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('editRadiusProxy')"><img
																			class="dinl" src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</s:if></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr id="brAsPpskServerDisplayDiv" style="display: <s:property value="%{brAsPpskServerDisplayStyle}"/>">
													<td colspan="4" style="padding: 2px 0 2px 6px">
														<s:checkbox name="dataSource.enabledBrAsPpskServer"></s:checkbox>
														<s:text name="hiveAp.enableBrAsPpskServer"/>
													</td>
												</tr>
												<tr id="useNasIdenInRadiusReq">
													<td class="labelT1" colspan="4" style="padding: 5px 0 0px 10px"><s:text
														name="hiveAp.nasIdentifier" /></td>
												</tr>
												<tr id="radioNasTypeTr2" >
													<td colspan="10" style="padding-left: 22px" class="labelT1"><table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td ><s:radio label="Gender"
																name="dataSource.nasIdentifierType" list="%{radioNasType1}"
																listKey="key" listValue="value"
																onchange="radioNasTypeChanged(this.value);"
																onclick="this.blur();" /></td>
														</tr>
													</table></td>
												</tr>
												<tr id="rradioNasTypeTr2" >
													<td colspan="10" style="padding-left: 22px" class="labelT1"><table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td width="230px"><s:radio label="Gender"
																name="dataSource.nasIdentifierType" list="%{radioNasType2}"
																listKey="key" listValue="value"
																onchange="radioNasTypeChanged(this.value);"
																onclick="this.blur();" /></td>
															<td><s:textfield name="dataSource.customizedNasIdentifier" size="24"
																	maxlength="64" disabled="%{customizedNasIdenReadonly}"/></td>
															<td>&nbsp;<s:text name="hiveAp.nasIdentifier.range" /></td>
														</tr>
													</table></td>
												</tr>
												<tr id="vpnRuleTr" style="display: <s:property value="vpnRuleStyle"/>">
													<td><table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td height="5"></td>
														</tr>
														<tr>
															<td class="labelT1" width="150px" style="padding-right: 20px;" colspan="2"><s:text
																name="hiveAp.server.vpn.role" /></td>
															<td><s:select list="%{enumVPNMarkType}" listKey="key"
																listValue="value" name="dataSource.vpnMark"
																cssStyle="width: 150px;" /></td>
														</tr>
														<tr>
															<td height="5"></td>
														</tr>
													</table></td>
												</tr>
												
												<s:if test="%{IDMEnableForCurrentVHM}">
												<tr>
													<td colspan="10">
														<fieldset><legend><s:text name="hiveAp.idmAuthProxy.label" /></legend>
															<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
																<tr>
																	<td height="5"></td>
																</tr>
																<tr>
																	<td>
																		<s:checkbox name="dataSource.enableIDMAuthProxy"/>
																		<s:text name="hiveAp.server.idmAuthProxy.enable" />
																	</td>
																</tr>
																<%-- <tr>
																	<td><s:radio label="Gender" name="dataSource.IDMAuthProxyStatus"
																		list="%{IDMAuthProxyTypeAuto}"
																		listKey="key" listValue="value"
																		onclick="this.blur();" /></td>
																</tr>
																<tr>
																	<td><s:radio label="Gender" name="dataSource.IDMAuthProxyStatus"
																		list="%{IDMAuthProxyTypeDisable}"
																		listKey="key" listValue="value"
																		onclick="this.blur();" /></td>
																</tr> --%>
															</table>
														</fieldset>
													</td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												</s:if>
												
												<tr id="dhcpServerOptionsRow"
													style="display: <s:property value="dhcpServerOptionsRowStyle"/>">
													<td colspan="10">
													<fieldset><legend><s:text
														name="hiveAp.dhcpServerLabel" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%" class="embedded">
														<tr>
															<td height="5"></td>
														</tr>
														<tr>
															<td style="padding-left: 10px;">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<s:push value="%{dhcpServerOptions}">
																		<td><tiles:insertDefinition
																			name="optionsTransfer" /></td>
																	</s:push>
																</tr>
															</table>
															</td>
														</tr>
													</table>
													</fieldset>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="l3RoamingStyle" style="display: <s:property value='l3RoamingStyle'/>" >
							<td><!-- Layer 3 Roaming -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.cfg.l3Roaming" />','l3Roaming');</script></td>
								</tr>
								<tr>
									<td>
									<div id="l3Roaming"
										style="display: <s:property value="%{dataSource.l3RoamingDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%" class="embedded">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td style="padding-right: 10px;" id="l3RoamingThresholdStyle" style="display: <s:property value='l3RoamingThresholdStyle'/>">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="150px"
																style="padding-right: 32px;"><s:text
																name="hiveAp.cfg.l3Roaming.threshold" /></td>
															<td><s:select name="dataSource.tunnelThreshold"
																value="%{dataSource.tunnelThreshold}"
																list="%{enumTunnelThresholdType}" listKey="key"
																listValue="value" cssStyle="width: 80px;" /></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td><label id="l3RoamingLabel"></label></td>
															<td width="338px">
															<fieldset><legend><s:text
																name="hiveAp.cfg.l3Roaming.included" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td height="5"></td>
																</tr>
																<tr>
																	<s:push value="%{includedNeighborOptions}">
																		<td><tiles:insertDefinition
																			name="optionsTransfer" /></td>
																	</s:push>
																</tr>
																<tr>
																	<td height="5"></td>
																</tr>
															</table>
															</fieldset>
															</td>
															<td width="5px"></td>
															<td width="338px">
															<fieldset><legend><s:text
																name="hiveAp.cfg.l3Roaming.excluded" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td height="5"></td>
																</tr>
																<tr>
																	<s:push value="%{excludedNeighborOptions}">
																		<td><tiles:insertDefinition
																			name="optionsTransfer" /></td>
																	</s:push>
																</tr>
																<tr>
																	<td height="5"></td>
																</tr>
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
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="cvgMgtServerTr" style="display: <s:property value="%{cvgMgtServerStyle}"/>">
							<!-- Management Server Settings(add for CVG) -->
							<td><div><table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.configTemplate.serverSettings" />','cvgMgtServer');</script></td>
								</tr>
								<tr>
									<td style="padding-left:7px"><div id="cvgMgtServer" style="display: <s:property value="%{dataSource.cvgMgtServerDisplayStyle}"/>">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="100px"><s:text name="config.configTemplate.mgtDns" /></td>
												<td width="280px">
													<s:select name="dnsForCVGId" list="%{dnsForCVGList}" listKey="id"
														listValue="value" cssStyle="width: 160px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newMgtDns')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editMgtDns')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
											 	</td>
											 	
											 	<td class="labelT1" width="100px"><s:text name="config.configTemplate.mgtSyslog" /></td>
												<td>
													<s:select name="syslogForCVGId" list="%{syslogForCVGList}" listKey="id"
														listValue="value" cssStyle="width: 160px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newMgtSyslog')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editMgtSyslog')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
											 	</td>
											</tr>
											<tr>
												<td class="labelT1"><s:text name="config.configTemplate.mgtTime" /></td>
												<td>
													<s:select name="ntpForCVGId" list="%{ntpForCVGList}" listKey="id"
														listValue="value" cssStyle="width: 160px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newMgtTime')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editMgtTime')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
												
												<td class="labelT1"><s:text name="config.configTemplate.mgtSnmp" /></td>
												<td>
													<s:select name="snmpForCVGId" list="%{snmpForCVGList}" listKey="id"
														listValue="value" cssStyle="width: 160px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newMgtSnmp')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editMgtSnmp')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
											 	</td>
											</tr>
										</table>
									</div></td>
								</tr>
							</table></div></td>
						</tr>
						<tr id="credentialsTr" style="display: <s:property value="%{fullModeConfigStyle}"/>">
							<td><!-- Credentials -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.tab.authentication" />','credentials');</script></td>
								</tr>
								<tr>
									<td>
									<div id="credentials"
										style="display: <s:property value="%{dataSource.credentialsDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0"
												width="100%">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td>
													<fieldset><legend><s:text
														name="hiveAp.superUser.tag" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														class="embedded">
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newUser" /></td>
																	<td><s:textfield name="dataSource.cfgAdminUser"
																		size="24" maxlength="%{cfgAdminUserLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'username');" />
																	<s:text name="hiveAp.currentUserRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newPassword" /></td>
																	<td><s:password name="dataSource.cfgPassword"
																		size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgAdminPassword" showPassword="true" /> <s:textfield
																		name="dataSource.cfgPassword" size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgAdminPassword_text" disabled="true"
																		cssStyle="display: none;" /> <s:text
																		name="hiveAp.currentPasswordRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newConfirmPassword" /></td>
																	<td><s:password name="confirmNewPassword"
																		size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfCfgAdminPassword"
																		value="%{dataSource.cfgPassword}" showPassword="true" />
																	<s:textfield name="confirmNewPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfCfgAdminPassword_text"
																		value="%{dataSource.cfgPassword}" disabled="true"
																		cssStyle="display: none;" /></td>
																</tr>
																<tr>
																	<td></td>
																	<td><s:checkbox id="chkToggleDisplay"
																		name="ignore" value="true"
																		disabled="%{writeDisable4Struts}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['cfgAdminPassword','cfCfgAdminPassword'],['cfgAdminPassword_text','cfCfgAdminPassword_text']);" />
																	<s:text name="admin.user.obscurePassword" /></td>
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
												<tr>
													<td>
													<fieldset><legend><s:text
														name="hiveAp.readOnlyUser.tag" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														class="embedded">
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newUser" /></td>
																	<td><s:textfield name="dataSource.cfgReadOnlyUser"
																		size="24" maxlength="%{cfgReadOnlyUserLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'username');" />
																	<s:text name="hiveAp.currentUserRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newPassword" /></td>
																	<td><s:password
																		name="dataSource.cfgReadOnlyPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgReadOnlyPassword" showPassword="true" /> <s:textfield
																		name="dataSource.cfgReadOnlyPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgReadOnlyPassword_text" disabled="true"
																		cssStyle="display: none;" /> <s:text
																		name="hiveAp.currentPasswordRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.newConfirmPassword" /></td>
																	<td><s:password name="confirmNewReadOnlyPassword"
																		size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="confirmCfgReadOnlyPassword"
																		value="%{dataSource.cfgReadOnlyPassword}"
																		showPassword="true" /> <s:textfield
																		name="confirmNewReadOnlyPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="confirmCfgReadOnlyPassword_text"
																		value="%{dataSource.cfgReadOnlyPassword}"
																		disabled="true" cssStyle="display: none" /></td>
																</tr>
																<tr>
																	<td></td>
																	<td><s:checkbox id="chkToggleDisplay_1"
																		name="ignore" value="true"
																		disabled="%{writeDisable4Struts}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['cfgReadOnlyPassword','confirmCfgReadOnlyPassword'],['cfgReadOnlyPassword_text','confirmCfgReadOnlyPassword_text']);" />
																	<s:text name="admin.user.obscurePassword" /></td>
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
												<tr>
													<td>
													<fieldset><legend><s:text
														name="hiveAp.capwap.tag" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														class="embedded">
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:checkbox name="changePassPhrase"
																		onclick="clickDtlsBox();"
																		disabled="%{writeDisable4Struts}"
																		value="%{changePassPhrase}" id="changePassPhrase" /></td>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.dtls.enableChange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.dtls.newPassPhrase" /></td>
																	<td><s:password name="dataSource.passPhrase"
																		size="24" id="newDtls"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		maxlength="32" showPassword="true"
																		disabled="%{passPhraseDisabled}" /> <s:textfield
																		name="dataSource.passPhrase" size="24"
																		id="newDtls_text"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		maxlength="32" cssStyle="display: none;"
																		disabled="true" /> <s:text
																		name="hiveAp.dtls.passPhraseRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.dtls.confirmPassPhrase" /></td>
																	<td><s:password value="%{dataSource.passPhrase}"
																		size="24" id="confirmDtls"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		maxlength="32" showPassword="true"
																		disabled="%{passPhraseDisabled}" /> <s:textfield
																		value="%{dataSource.passPhrase}" size="24"
																		id="confirmDtls_text"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		maxlength="32" cssStyle="display: none;"
																		disabled="true" /></td>
																</tr>
																<tr>
																	<td></td>
																	<td><s:checkbox id="chkToggleDisplay_2"
																		name="ignore" value="true"
																		disabled="%{passPhraseDisabled}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['newDtls','confirmDtls'],['newDtls_text','confirmDtls_text']);" />
																	<s:text name="admin.user.obscurePassword" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="140px"><s:text
																		name="hiveAp.capwap.server" /></td>
																	<td width="260px"><ah:createOrSelect
																		divId="errorDisplay" list="capwapIps"
																		typeString="CapwapIp" selectIdName="capwapSelect"
																		inputValueName="dataSource.capwapText" swidth="152px" />
																	</td>
																</tr>
																<tr>
																	<td class="labelT1"><s:text
																		name="hiveAp.capwap.server.backup" /></td>
																	<td><ah:createOrSelect divId="errorBackupDisplay"
																		list="capwapIps" typeString="CapwapBackupIp"
																		selectIdName="capwapBackupSelect"
																		inputValueName="dataSource.capwapBackupText"
																		swidth="152px" /></td>
																</tr>
															</table>
															</td>
														</tr>
													</table>
													</fieldset>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="routingConfigStyle" style="display: <s:property value='routingConfigStyle'/>" >
							<td><!-- Routing -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.tab.routing" />','routing');</script></td>
								</tr>
								<tr>
									<td>
									<div id="routing"
										style="display: <s:property value="%{dataSource.routingDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0"
												width="100%">
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td>
													<fieldset><legend><s:text
														name="hiveAp.cfg.l2Route" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%" class="embedded">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding-left: 10px; padding-right: 0px;">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td width="120px" nowrap="nowrap"><s:text
																		name="hiveAp.cfg.dynamicRoute.metric.interval" /></td>
																	<td width="200px" nowrap="nowrap"><s:textfield
																		name="dataSource.metricInteval" size="5" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text
																		name="hiveAp.cfg.dynamicRoute.metric.interval.range" /></td>
																	<td width="100px" style="padding-left: 20px;"><s:text
																		name="hiveAp.cfg.dynamicRoute.metric.type" /></td>
																	<td width="100px"><s:select
																		list="%{enumMetricType}" listKey="key"
																		listValue="value" value="%{dataSource.metric}"
																		name="dataSource.metric" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td class="sepLine" colspan="3"><img
																src="<s:url value="/images/spacer.gif"/>" height="1"
																class="dblk" /></td>
														</tr>
														<tr>
															<td style="padding: 4px 0 0 0;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%" id="dynamicTable">
																<tr id="newButton">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="New" class="button"
																				onClick="showCreateSection('dynamic');"
																				<s:property value="writeDisabled" />></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveDynamicRoutes2();"
																				<s:property value="writeDisabled" />></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr style="display: none;" id="createButton">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="Apply" class="button"
																				onClick="doAddDynamicRoute2();"></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveDynamicRoutes2();"></td>
																			<td><input type="button" name="ignore"
																				value="Cancel" class="button"
																				onClick="hideCreateSection('dynamic');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr id="headerSectionDynamicRoutes2">
																	<td colspan="5"></td>
																</tr>
																<tr id="headerSection">
																	<th align="left" style="padding-left: 0;" width="30"><input
																		type="checkbox" id="checkAll"
																		onClick="toggleCheckAllDynamicRoutes(this);"></th>
																	<th align="left" width="120"><s:text
																		name="hiveAp.cfg.dynamicRoute.neighborMac" /></th>
																	<th align="left" width="200"><s:text
																		name="hiveAp.cfg.dynamicRoute.metric.minimum" /></th>
																	<th align="left" width="200"><s:text
																		name="hiveAp.cfg.dynamicRoute.metric.maximum" /></th>
																</tr>
																<tr style="display: none;" id="createSection">
																	<td class="listHead" width="30">&nbsp;</td>
																	<td class="listHead" valign="top" nowrap="nowrap" width="120"><s:textfield
																		name="neighborMac" size="16" maxlength="12"
																		onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																	<br>
																	<s:text name="config.macOrOui.addressRange" /></td>
																	<td class="listHead" valign="top" width="200"><s:textfield
																		name="routeMinimun" size="16" maxlength="4"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<br>
																	<s:text name="hiveAp.cfg.dynamicRoute.metric.range" /></td>
																	<td class="listHead" valign="top" width="200"><s:textfield
																		name="routeMaximun" size="16" maxlength="4"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<br>
																	<s:text name="hiveAp.cfg.dynamicRoute.metric.range" /></td>
																</tr>
																<tr>
																	<td colspan="5">
																		<table id="dynamicRoutes2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																<s:iterator value="%{dataSource.dynamicRoutes}"
																	status="status">
																	<tr class="list">
																		<td class="listCheck" width="30"><s:checkbox
																			name="dynamicRouteIndices"
																			fieldValue="%{#status.index}" /></td>
																		<td class="list" width="120"><s:property value="neighborMac" /></td>
																		<td class="list" width="200"><s:property value="routeMinimun" /></td>
																		<td class="list" width="200"><s:property value="routeMaximun" /></td>
																	</tr>
																</s:iterator>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td colspan="5" width="100%">
																		<table id="dynamicRoutes2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:if test="%{gridCount > 0}">
																	<s:generator separator="," val="%{' '}"
																		count="%{gridCount}">
																		<s:iterator>
																			<tr>
																				<td class="list" colspan="6" width="100%">&nbsp;</td>
																			</tr>
																		</s:iterator>
																	</s:generator>
																	</s:if>
																		</table>
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="30"></td>
														</tr>
														<tr>
															<td class="sepLine" colspan="3"><img
																src="<s:url value="/images/spacer.gif"/>" height="1"
																class="dblk" /></td>
														</tr>
														<tr>
															<td style="padding: 4px 0 0 0;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%" id="staticTable">
																<tr id="newButtonStatic">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="New" class="button"
																				onClick="showCreateSection('static');"
																				<s:property value="writeDisabled" />></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveStaticRoutes2();"
																				<s:property value="writeDisabled" />></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr style="display: none;" id="createButtonStatic">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="Apply" class="button"
																				onClick="doAddStaticRoute2();"></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveStaticRoutes2();"></td>
																			<td><input type="button" name="ignore"
																				value="Cancel" class="button"
																				onClick="hideCreateSection('static');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr id="headerSectionStatic">
																	<th align="left" style="padding-left: 0;" width="30"><input
																		type="checkbox" id="checkAllStatic"
																		onClick="toggleCheckAllStaticRoutes(this);"></th>
																	<th align="left" width="180"><s:text
																		name="hiveAp.cfg.staticRoute.destination" /></th>
																	<th align="left" width="180"><s:text
																		name="hiveAp.cfg.staticRoute.interface" /></th>
																	<th align="left" width="180"><s:text
																		name="hiveAp.cfg.staticRoute.nextHop" /></th>
																</tr>
																<tr style="display: none;" id="createSectionStatic">
																	<td class="listHead">&nbsp;</td>
																	<td class="listHead" valign="top"><s:textfield
																		name="destinationMac" size="16" maxlength="12"
																		onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																	<br>
																	<s:text name="config.macOrOui.addressRange" /></td>
																	<td class="listHead" valign="top"><s:select
																		name="interfaceType" list="%{enumStaticRouteIfType}"
																		listKey="key" listValue="value"
																		cssStyle="width: 110px;" /></td>
																	<td class="listHead" valign="top"><s:textfield
																		name="nextHopMac" size="16" maxlength="12"
																		onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																	<br>
																	<s:text name="config.macOrOui.addressRange" /></td>
																</tr>
																<tr>
																	<td colspan="5">
																		<table id="staticRoute2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																<s:iterator value="%{dataSource.staticRoutes}"
																	status="status">
																	<tr>
																		<td class="listCheck" width="30"><s:checkbox
																			name="staticRouteIndices"
																			fieldValue="%{#status.index}" /></td>
																		<td class="list" width="180"><s:property
																			value="destinationMac" /></td>
																		<td class="list" width="180"><s:select name="interfaceTypes"
																			value="%{interfaceType}"
																			list="%{enumStaticRouteIfType}" listKey="key"
																			listValue="value" /></td>
																		<td class="list" width="180"><s:property value="nextHopMac" /></td>
																	</tr>
																</s:iterator>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td colspan="5" width="100%">
																		<table id="staticRoute2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																<s:if test="%{gridCount > 0}">
																	<s:generator separator="," val="%{' '}"
																		count="%{gridCount}">
																		<s:iterator>
																			<tr>
																				<td class="list" colspan="6">&nbsp;</td>
																			</tr>
																		</s:iterator>
																	</s:generator>
																</s:if>
																		</table>
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="30"></td>
														</tr>
													</table>
													</fieldset>
													</td>
												</tr>
												<tr>
													<td height="5px"></td>
												</tr>
												<tr>
													<td>
													<fieldset><legend><s:text
														name="hiveAp.cfg.l3Route" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%" class="embedded">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding: 4px 0 0 0;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr id="newButtonIp">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="New" class="button"
																				onClick="showCreateSection('ip');"
																				<s:property value="writeDisabled" />></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveIpRoutes2();"
																				<s:property value="writeDisabled" />></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr style="display: none;" id="createButtonIp">
																	<td colspan="5" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore"
																				value="Apply" class="button"
																				onClick="doAddIpRoute2();"></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveIpRoutes2();"></td>
																			<td><input type="button" name="ignore"
																				value="Cancel" class="button"
																				onClick="hideCreateSection('ip');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr id="headerSectionIp">
																	<th align="left" style="padding-left: 0;" width="60"><input
																		type="checkbox" id="checkAllIp"
																		onClick="toggleCheckAllIpRoutes(this);"></th>
																	<th align="left" width="220"><s:text
																		name="hiveAp.cfg.ipRoute.dest" /></th>
																	<th align="left" width="150"><s:text
																		name="hiveAp.cfg.ipRoute.mask" /></th>
																	<th align="left" width="150"><s:text
																		name="hiveAp.cfg.ipRoute.gateway" /></th>
																</tr>
																<tr style="display: none;" id="createSectionIp">
																	<td class="listHead" width="60">&nbsp;</td>
																	<td class="listHead" valign="top" nowrap="nowrap" width="220"><s:textfield
																		name="ipRouteIpInput" size="16" maxlength="15" /></td>
																	<td class="listHead" valign="top" width="150"><s:textfield
																		name="ipRouteMaskInput" size="16" maxlength="15" /></td>
																	<td class="listHead" valign="top" width="150"><s:textfield
																		name="ipRouteGwInput" size="16" maxlength="15" /></td>
																</tr>
																<tr>
																	<td colspan="5">
																		<table id="ipRoutes2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																<s:iterator value="%{dataSource.ipRoutes}"
																	status="status">
																	<tr>
																		<td class="listCheck" width="60"><s:checkbox
																			name="ipRouteIndices" fieldValue="%{#status.index}" /></td>
																		<td class="list" width="220"><s:property value="sourceIp" /></td>
																		<td class="list" width="150"><s:property value="netmask" /></td>
																		<td class="list" width="150"><s:property value="gateway" /></td>
																	</tr>
																</s:iterator>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td colspan="5" width="100%">
																		<table id="ipRoutes2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																<s:if test="%{gridCount > 0}">
																	<s:generator separator="," val="%{' '}"
																		count="%{gridCount}">
																		<s:iterator>
																			<tr>
																				<td class="list" colspan="6">&nbsp;</td>
																			</tr>
																		</s:iterator>
																	</s:generator>
																</s:if>
																		</table>
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="30"></td>
														</tr>
													</table>
													</fieldset>
													</td>
												</tr>
												<tr>
												<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td height="5px"></td>
													</tr>
													<tr id="multipleVlanSection"
														style="display: <s:property value="%{multipleVlanDisplay}"/>">

														<td valign="top" width="100%" style="padding-top: 5px;">

														<fieldset><legend><s:text
															name="hiveAp.ethernet.multiple.route" /></legend>
														<table cellspacing="0" cellpadding="0" border="0"
															width="100%" class="embedded">
															<tr>
																<td height="10"></td>
															</tr>
															<tr>
																<td style="padding: 4px 0 0 0;">
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%" id="multipleVlanTable">
																	<tr id="newButtonVlanId">
																		<td colspan="5" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore"
																					value="New" class="button"
																					onClick="showCreateSection('vlanid');"
																					<s:property value="writeDisabled" />></td>
																				<td><input type="button" name="ignore"
																					value="Remove" class="button"
																					onClick="doRemoveMultipleVlan();"
																					<s:property value="writeDisabled" />></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display: none;" id="createButtonVlanId">
																		<td colspan="5" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore"
																					value="Apply" class="button"
																					onClick="doAddMultipleVlan();"></td>
																				<td><input type="button" name="ignore"
																					value="Remove" class="button"
																					onClick="doRemoveMultipleVlan();"></td>
																				<td><input type="button" name="ignore"
																					value="Cancel" class="button"
																					onClick="hideCreateSection('vlanid');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerSectionVlanId">
																		<th align="left" style="padding-left: 0;" width="180"><input
																			type="checkbox" id="checkAllVlanId"
																			onClick="toggleCheckAllMultipleVlan(this);"></th>
																		<th align="left" width="400"><s:text
																			name="hiveAp.ethernet.multiple.vlan" /></th>
																	</tr>
																	<tr style="display: none;" id="createSectionVlanId">
																		<td class="listHead">&nbsp;</td>
																		<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																			name="multiplevlanInput" size="16" maxlength="15" />
																			<br>
																		<s:text name="hiveAp.ethernet.multiple.vlan.range" /></td>
																		</tr>
																	<tr>
																	<td colspan="5">
																		<table id="multipleVlanTblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:iterator value="%{dataSource.multipleVlan}"
																		status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox
																				name="multiplevlanIndices" fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="vlanid" />

																		</tr>
																	</s:iterator>
																		</table>
																	</td>
																	</tr>
																	<tr>
																	<td colspan="5" width="100%">
																		<table id="multipleVlanTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:if test="%{gridCount > 0}">
																		<s:generator separator="," val="%{' '}"
																			count="%{gridCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="6">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																		</table>
																	</td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<td height="30"></td>
															</tr>
														</table>
														</fieldset>
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
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="deviceBonjourGatewayConfig">
							<td><!-- Bonjour Geteway Configuration -->
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.bonjourGateway.label" />','bonjourGatewayConfig');</script></td>
									</tr>
									<tr>
										<td>
											<div id="bonjourGatewayConfig"
												style="display: <s:property value="%{dataSource.bjgwConfigDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" width="100"><label><s:text
														name="hiveAp.bonjour.gateway.priority" /><font color="red"><s:text name="*" /></font></label></td>
													<td><s:textfield name="dataSource.priority" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														<s:text name="hiveAp.bonjour.gateway.priority.range"/></td>
												</tr>
												<tr>
													<td class="labelT1" width="100"><label><s:text
														name="monitor.bonjour.gateway.realm.modifyname" /></label></td>
													<td><s:textfield name="dataSource.realmName" maxlength="128" size="48" onkeypress="return hm.util.keyPressPermit(event,'name');"/>
														<s:text name="config.BonjourGatewaySetting.description.range"/></td>
												</tr>
												<tr>
													<td class="labelT1" colspan="2">
														<s:checkbox name="dataSource.lockRealmName" />
														<s:text name="hiveAp.bonjour.gateway.lockRealmName" /></td>
												</tr>
												<tr>
													<td colspan="2" class="noteInfo" style="padding-left: 10px;"><s:text name="hiveAp.bonjour.gateway.realm.name.note"/></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr id="deviceWifiClientModeSettings">
							<td><!-- WIFI Client Mode Settings -->
							   <table border="0" cellspacing="0" cellpadding="0">
								   <tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.wifiClientMode.label" />','wifiClientModeSettings');</script></td>
									</tr>
									<tr>
										<td>
										  <div id="wifiClientModeSettings"  style="display: <s:property value="%{dataSource.wifiClientModeDisplayStyle}"/>">
										  		<table cellspacing="0" cellpadding="0" border="0">
										  			<tr id="wfcmInterfaceInfo">
														<td style="padding-left: 20px;" class="noteInfo"><s:text name="hiveAp.wifiClientMode.wifienabled.note"/></td>
													</tr>
							  				   		<tr id="wfcmInterfaceNote">
							  				   			<td style="padding-left: 20px;" class="noteInfo"><s:text name="hiveAp.wifiClientMode.wifiwanneed.note"/></td>
													</tr>
													<tr id="wfcmSSIDNote"/>													
										  			<tr><td height="5px"></td></tr>
										  			<tr>
										  			  <td>
										  			    <fieldset><legend><s:text name="hiveAp.wifiClientMode.preferredssids" /></legend>
																<table style="padding-top:10px" cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td height="4px"></td>
																	</tr>
	                                                                <tr>
	                                                                    <s:push value="%{preferredSsidOptions}">
	                                                                        <td style="padding-left: 10px;"><tiles:insertDefinition
	                                                                            name="optionsTransfer" /></td>
	                                                                    </s:push>
	                                                                </tr>
																</table>
										  			 	</fieldset>
										  			  </td>
										  			</tr>
										  		</table>
										  </div>
										</td>
									</tr>
							   </table>
							</td>
						</tr>
						<tr id="deviceAdvancedSettings">
							<td><!-- Advanced Settings -->
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.advanced.label" />','advancedSettings');</script></td>
								</tr>
								<tr>
									<td>
									<div id="advancedSettings"
										style="display: <s:property value="%{dataSource.advSettingsDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
									   <tr id="poeSetting" style="display: <s:property value="%{poeSettingStyle}"/>">
									     <td style="padding: 5px 0 5px 15px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
										       	   <td width="70px"><s:text name="hiveAp.advanced.settings.poe.mode" /></td>
										       	   <td width="130px">
										       	      <s:select id="poeModeSelection" list="%{poEModeList}" listKey="key"
															listValue="value" name="dataSource.poeMode"
															cssStyle="width: 80px;" 
															onchange="selectPoEChange(this.value)"/>
										       	   </td>
										       	   <td>
										       	   		<div id="poePrimaryEthSection" style="display:<s:property value="%{poePrimaryEthSectionStyle}"/>">
										       	   			 <table border="0" cellspacing="0" cellpadding="0">
										       	   			 	<tr>
										       	   			 		<td width="280px"><s:text name="hiveAp.advanced.settings.poe.primaryeth" /></td>
										       	   			 		<td><s:select id="poePrimaryEthSelection" list="%{poEPrimaryEthList}" listKey="key"
																		listValue="value" name="dataSource.poePrimaryEth"
																		cssStyle="width: 80px;" /></td>
										       	   			 	</tr>
										       	   			 </table>
										       	   		</div>
										       	   </td>
												</tr>
											</table>		
										  </td>								    
									    </tr>
										<tr id="vlanSetting"
											style="display: <s:property value="%{vlanSettingStyle}"/>">
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
														id="overrideVlan" name="overrideVlan"
														value="%{nativeVlanOverride}"
														onclick="changeNativeVlanInput(this.checked);"></s:checkbox>
													</td>
													<td width="200px"><s:if test="%{easyMode}">
														<span><s:text
															name="hiveAp.nativeVlan.override.express" /></span>
													</s:if> <s:else>
														<span><s:text name="hiveAp.nativeVlan.override" /></span>
													</s:else></td>
													<td><s:textfield name="dataSource.nativeVlan"
														size="24" disabled="%{nativeVlanDisabled}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="4" title="%{vlanRange}" /> <s:text
														name="hiveAp.nativeVlan.range" /></td>
												</tr>
												<tr>
													<td style="padding: 5px 0 5px 10px;"><s:checkbox
														id="overrideMgtVlan" name="overrideMgtVlan"
														value="%{mgtVlanOverride}"
														onclick="changeMgtVlanInput(this.checked);"></s:checkbox>
													</td>
													<td><s:if test="%{easyMode}">
														<span><s:text
															name="hiveAp.mgtVlan.override.express" /></span>
													</s:if> <s:else>
														<span><s:text name="hiveAp.mgtVlan.override" /></span>
													</s:else></td>
													<td><s:textfield name="dataSource.mgtVlan" size="24"
														disabled="%{mgtVlanDisabled}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="4" title="%{mgtVlanRange}" /> <s:text
														name="hiveAp.nativeVlan.range" /></td>
												</tr>
												<s:if test="%{easyMode}">
													<tr>
														<td style="padding: 5px 0 5px 10px;"><s:checkbox
															name="dataSource.enableDas"
															value="%{dataSource.enableDas}"></s:checkbox></td>
														<td><span><s:text
															name="config.configTemplate.enableAirTime" /></span></td>
														<td></td>
													</tr>
												</s:if>
											</table>
											</td>
										</tr>
										<!--start br100 PMTUD  -->
										<tr id="brPmtudSettings">
											<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr id="enableOverrideBrPMTUDTr">
												<td><table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.enableOverrideBrPMTUD" value="dataSource.enableOverrideBrPMTUD"
																								onclick="enableOverrideBrPMTUD(this.checked);"/></td>
														<td><s:text name="hiveAp.vpn.br.override.pmtud.mss"/></td>
													</tr>
												</table></td></tr>
												<tr id="enableBrPMTUDTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
												<td><table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding: 5px 0 5px 25px;"><s:checkbox name="dataSource.enableBrPMTUD" value="dataSource.enableBrPMTUD"
																									disabled="%{!dataSource.enableOverrideBrPMTUD}"/></td>
														<td><s:text name="hiveAp.vpn.enablepmtud"/></td>
													</tr>
												</table></td></tr>
												<tr id="monitorBrMSSTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
												<td><table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td  style="padding: 5px 0 5px 25px;"><s:checkbox name="dataSource.monitorBrMSS" value="dataSource.monitorBrMSS"
																									disabled="%{!dataSource.enableOverrideBrPMTUD}"
																									onclick="enableMonitorBrMSS(this.checked);"/></td>
														<td><s:text name="hiveAp.vpn.enablemonitormss"/></td>
													</tr>
												</table></td></tr>
												<tr id="monitorBrMSSNoteTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
													<td style="padding-left: 45px;padding-bottom: 5px;" class="noteInfo">
														<s:text name="hiveAp.vpn.enablemonitormss.note"/></td>
												</tr>
												<tr id="thresholdBrForAllTCPTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" style="padding-left: 45px;" width="360" ><s:text name="hiveAp.vpn.threshold.all.tcp.connections" /></td>
																<td><s:textfield name="dataSource.thresholdBrForAllTCP" size="18"
																		disabled="%{!dataSource.monitorBrMSS&&!dataSource.enableOverrideBrPMTUD}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="hiveAp.vpn.threshold.range"/></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr id="thresholdBrThroughVPNTunnelTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" style="padding-left: 45px;" width="360" ><s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" /></td>
																<td><s:textfield name="dataSource.thresholdBrThroughVPNTunnel" size="18"
																		disabled="%{!dataSource.monitorBrMSS&&!dataSource.enableOverrideBrPMTUD}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="hiveAp.vpn.threshold.range"/></td>
															</tr>
														</table>
													</td>
												</tr>
											</table></td>
										</tr>
										<!--end br100 PMTUD  -->
										<tr id="brRouteInterval" style="display:<s:property value="%{brRouteIntervalStyle}" />">
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td width="220" class="labelT1"><s:text name="hiveAp.vpn.br.route.interval" /></td>
														<td><s:textfield name="dataSource.routeInterval" size="18"
																onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
															<s:text name="hiveAp.vpn.br.route.interval.range"/></td>
													</tr>
													<tr id="maxPowerSourceRow">
														<td width="220" class="labelT1"><s:text name="hiveAp.vpn.br.route.powersource.maximum" /></td>
														<td><s:textfield name="dataSource.maxPowerSource" size="18"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="2"/>
															<s:text name="hiveAp.vpn.br.route.powersource.range"/></td>
													</tr>
												</table>
											</td>
										</tr>
										<!-- start CVG PMTUD -->
										<tr id="cvgPmtudSettings">
											<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr id="enableCvgPMTUDTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
												<td><table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.enableCvgPMTUD" value="dataSource.enableCvgPMTUD" /></td>
														<td><s:text name="hiveAp.vpn.enablepmtud"/></td>
													</tr>
												</table></td></tr>
												<tr id="monitorCvgMSSTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
												<td><table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td  style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.monitorCvgMSS" value="dataSource.monitorCvgMSS"
																									onclick="enableMonitorCvgMSS(this.checked);"/></td>
														<td><s:text name="hiveAp.vpn.enablemonitormss"/></td>
													</tr>
												</table></td></tr>
												<tr id="monitorCvgMSSNoteTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
													<td style="padding-left: 28px;padding-bottom: 5px;" class="noteInfo">
														<s:text name="hiveAp.vpn.enablemonitormss.note"/></td>
												</tr>
												<tr id="thresholdCvgForAllTCPTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" style="padding-left: 28px;" width="360" ><s:text name="hiveAp.vpn.threshold.all.tcp.connections" /></td>
																<td><s:textfield name="dataSource.thresholdCvgForAllTCP" size="18"
																		disabled="%{!dataSource.monitorCvgMSS}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="hiveAp.vpn.threshold.range"/></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr id="thresholdCvgThroughVPNTunnelTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" style="padding-left: 28px;" width="360" ><s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" /></td>
																<td><s:textfield name="dataSource.thresholdCvgThroughVPNTunnel" size="18"
																		disabled="%{!dataSource.monitorCvgMSS}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="hiveAp.vpn.threshold.range"/></td>
															</tr>
														</table>
													</td>
												</tr>
											</table></td>
										</tr>
										<!-- end CVG PMTUD -->
										<tr id="vpnIpTrackTr" style="display:<s:property value="%{vpnGatewayIntStyle}" />">
										<td><table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1" width="200px"><s:text
													name="hiveAp.vpn.gateway.ip.track.label" /></td>
												<td><s:select list="%{availableIpTracks}" listKey="id"
													listValue="value" name="vpnIpTrackId"
													cssStyle="width: 150px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
													<a class="marginBtn"
														href="javascript:submitAction('newIpTrack')"><img
														class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
													<a class="marginBtn"
														href="javascript:submitAction('editIpTrack')"><img
														class="dinl" src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
										</table></td></tr>
										<tr id="auditScheduler" style="display: <s:property value="%{auditSchedulerStyle}"/>">
										<td><table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1" width="220px"><s:text
													name="hiveAp.scheduler.label" /></td>
												<td><s:select list="%{schedulers}" listKey="id"
													listValue="value" name="scheduler"
													cssStyle="width: 150px;" /> <s:if
													test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
												</s:if> <s:else>
													<a class="marginBtn"
														href="javascript:submitAction('newScheduler')"><img
														class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
												</s:else> <s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
												</s:if> <s:else>
													<a class="marginBtn"
														href="javascript:submitAction('editScheduler')"><img
														class="dinl" src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else></td>
											</tr>
										</table></td></tr>
										<s:if test="%{!bR100PlatformDevice}">
										<tr>
										<td><table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
													name="dataSource.includeTopologyInfo" value="%{dataSource.includeTopologyInfo}"></s:checkbox>
												</td>
												<td><span><s:text name="glasgow_12.hiveAp.insert.topology" /></span></td>
											</tr>
											<tr>
												<td style="padding: 5px 0 5px 30px;" colspan="10" align="left" class="noteInfo"><s:text name="glasgow_12.hiveAp.insert.topology.note" /></td>
											</tr>
										</table></td></tr>
										</s:if>
										<tr>
											<td height="5px"></td>
										</tr>
										<tr id="distributedPriority"
											style="display: <s:property value="%{distributedPriorityStyle}"/>">
											<td>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td class="labelT1" width="350px"><s:text
														name="hiveAp.distributedPriority" /></td>
													<td><s:select list="%{enumDistributedPriority}"
														listKey="key" listValue="value"
														name="dataSource.distributedPriority"
														value="%{dataSource.distributedPriority}"
														cssStyle="width:120px;" /></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td><table cellspacing="0" cellpadding="0" border="0">
												<s:if test="%{easyMode}">
												<tr>
													<td style="padding: 5px 0 5px 10px;" align="left" colspan="2"><s:checkbox
														name="dataSource.enableDelayAlarm" value="%{dataSource.enableDelayAlarm}"></s:checkbox>
														<span><s:text name="guadalupe_01.enable.capwap.alarm"/></span>
													</td>
												</tr>
												</s:if>
												<s:else>
												<tr id="overrideEnableDelayAlarmTr">
													<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
														name="dataSource.overrideEnableDelayAlarm" id="overrideEnableDelayAlarm" value="%{dataSource.overrideEnableDelayAlarm}"
														onclick="showDealyAlarmEnable(this.checked);"></s:checkbox>
													</td>
													<td><span><s:text name="guadalupe_01.enable.capwap.alarm.override"/></span></td>
												</tr>
												<tr style="display: <s:property value="%{delayAlarmEnableStyle}"/>" id="delayAlarmEnableTr">
													<td style="padding: 5px 0 5px 30px;" align="left" colspan="2" id="enableDelayAlarmTd"><s:checkbox
														name="dataSource.enableDelayAlarm" id="enableDelayAlarmCK" value="%{dataSource.enableDelayAlarm}"></s:checkbox>
														<span><s:text name="guadalupe_01.enable.capwap.alarm"/></span>
													</td>
												</tr>
												</s:else>
											</table></td></tr>
										<tr style="display: <s:property value="%{supplementalCLIStyle}"/>">
											<td>
												<table cellspacing="0"
													cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="220px"><s:text
																name="hollywood_02.supp_cli_setting" /></td>
														<td><s:select
																list="%{list_cliBlob}" listKey="id"
																listValue="value"
																name="supplementalCLIId"
																cssStyle="width: 150px;" /> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New"
																	title="New" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('newSuppCLI')"><img
																	class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New"
																	title="New" /></a>
															</s:else> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify"
																	title="Modify" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('editSuppCLI')"><img
																	class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify"
																	title="Modify" /></a>
															</s:else></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr style="display: <s:property value="%{txRetryShowStyle}"/>" id="tx-rate">
											<td style="padding-left: 6px">
 												<fieldset>
													<legend><s:text name="config.configTemplate.reportSettings.clientTxRetry"></s:text></legend>
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tbody>
															<tr>
																<td width="300px" class="labelT1">
																	<s:text name="config.device.reportSettings.ifTxRetry.device"></s:text>
																</td>
																<td>
																	<s:textfield name="dataSource.deviceTxRetry" size="24"
																				 onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				 maxlength="3" />% &nbsp; (1-100)
																</td>																
															</tr>
															<tr>
																<td width="300px" class="labelT1">
																	<s:text name="config.device.reportSettings.ifTxRetry.client"></s:text>
																</td>
																<td>
																	<s:textfield name="dataSource.clientTxRetry" size="24"
																			 onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			 maxlength="3" />% &nbsp; (1-100)
																</td>		
															</tr>
															<tr>
																<td align="left" style="padding: 5px 0 5px 30px;" colspan="2" class="noteInfo">
																	<s:text name="config.device.reportSettings.ifTxRetry.note"></s:text>
																</td>
															</tr>
														</tbody>
													</table>
												</fieldset>
											</td>
										</tr>										
										
										<tr id="virtualConnectSection"
											style="display: <s:property value="%{virtualConnectDisplay}"/>">
											<td valign="top" width="100%" style="padding-top: 5px;">
											<fieldset><legend><s:text
												name="hiveAp.virtualConnection" /></legend>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%" class="embedded">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td style="padding: 0 0 0 0;">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td colspan="10" align="left" class="noteInfo"><s:text
																name="hiveAp.virtualConnection.note" /></td>
														</tr>
														<tr id="newButtonVirtualConnect">
															<td colspan="5" style="padding-top: 5px;padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="New"
																		class="button"
																		onClick="showCreateSection('virtualConnect');"
																		<s:property value="writeDisabled" />></td>
																	<td><input type="button" name="ignore"
																		value="Remove" class="button"
																		onClick="doRemoveVirtualConnect();"
																		<s:property value="writeDisabled" />></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display: none;" id="createButtonVirtualConnect">
															<td colspan="5" style="padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore"
																		value="Apply" class="button"
																		onClick="doAddVirtualConnect();"></td>
																	<td><input type="button" name="ignore"
																		value="Remove" class="button"
																		onClick="doRemoveVirtualConnect();"></td>
																	<td><input type="button" name="ignore"
																		value="Cancel" class="button"
																		onClick="hideCreateSection('virtualConnect');"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<s:if test="%{jsonMode}">
																<div style="overflow-x:auto;overflow-y:hidden;width: 687px;">
															</s:if>
															<s:else>
																<div>
															</s:else>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td colspan="9">
																				<table id="virtualConnectTblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																					<tr id="headerSectionVirtualConnect">
																						<th align="left" style="padding-left: 0;"><input
																							type="checkbox" id="checkAllVirtualConnect"
																							onClick="toggleCheckAllVirtualConnects(this);"></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.name" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.action" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.inInterface" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.sourceMac" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.destMac" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.txMac" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.outInterface" /></th>
																						<th align="left" nowrap="nowrap"><s:text
																							name="hiveAp.virtualConnection.rxMac" /></th>
																					</tr>
																					<tr style="display: none;"
																						id="createSectionVirtualConnect">
																						<td class="listHead">&nbsp;</td>
																						<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																							name="virtualConnectName" size="16" maxlength="32" /><br>
																						<s:text name="config.configTemplate.configName.range" /></td>
																						<td class="listHead" valign="top"><s:select
																							name="virtualConnectAction"
																							list="%{virtualConnectActions}" listKey="key"
																							listValue="value" cssStyle="width: 56px;"
																							onchange="selectVirtualAction(this.value)" /></td>
																						<td class="listHead" valign="top"><s:select
																							name="virtualConnectInterface_in"
																							list="%{virtualConnectInterfaces}" listKey="key"
																							listValue="value" cssStyle="width: 56px;"
																							onchange="selectInInterface(this.value)" /></td>
																						<td class="listHead" valign="top"><s:textfield
																							name="virtualConnectSourceMac" size="12" maxlength="12"
																							onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																						<br>
																						<s:text name="hiveAp.virtualConnection.sourceMac.range" /></td>
																						<td class="listHead" valign="top"><s:textfield
																							name="virtualConnectDestMac" size="12" maxlength="12"
																							onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																						<br>
																						<s:text name="config.macOrOui.addressRange" /></td>
																						<td class="listHead" valign="top"><s:textfield
																							name="virtualConnectTxMac" size="12" maxlength="12"
																							onkeypress="return hm.util.keyPressPermit(event,'hex');"
																							disabled="true" /> <br>
																						<s:text name="hiveAp.virtualConnection.txMac.note" /></td>
																						<td class="listHead" valign="top"><s:select
																							name="virtualConnectInterface_out"
																							list="%{virtualConnectInterfaces}" listKey="key"
																							listValue="value" cssStyle="width: 56px;"
																							onchange="selectOutInterface(this.value)" /></td>
																						<td class="listHead" valign="top"><s:textfield
																							name="virtualConnectRxMac" size="12" maxlength="12"
																							onkeypress="return hm.util.keyPressPermit(event,'hex');"
																							disabled="true" /> <br>
																						<s:text name="config.macOrOui.addressRange" /></td>
																					</tr>
																					<s:iterator value="%{dataSource.virtualConnections}"
																						status="status">
																						<tr id = "virtualConnectionsOnly">
																							<td class="listCheck"><s:checkbox
																								name="virtualConnectIndices"
																								fieldValue="%{#status.index}" /></td>
																							<td class="list"><s:property value="forwardName" /></td>
																							<td class="list"><s:property
																								value="forwardAction4Display" /></td>
																							<td class="list"><s:property
																								value="interface_in4Display" /></td>
																							<td class="list"><s:property value="sourceMac" /></td>
																							<td class="list"><s:property value="destMac" /></td>
																							<td class="list"><s:property value="txMac" /></td>
																							<td class="list"><s:property
																								value="interface_out4Display" /></td>
																							<td class="list"><s:property value="rxMac" /></td>
																						</tr>
																					</s:iterator>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td colspan="10" width="100%">
																				<table id="virtualConnectTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																					<s:if test="%{virtualConnectGridCount > 0}">
																						<s:generator separator="," val="%{' '}"
																							count="%{virtualConnectGridCount}">
																							<s:iterator>
																								<tr>
																									<td class="list" colspan="9">&nbsp;</td>
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
														<%-- <tr id="headerSectionVirtualConnect">
															<th align="left" style="padding-left: 0;"><input
																type="checkbox" id="checkAllVirtualConnect"
																onClick="toggleCheckAllVirtualConnects(this);"></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.name" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.action" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.inInterface" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.sourceMac" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.destMac" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.txMac" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.outInterface" /></th>
															<th align="left" nowrap="nowrap"><s:text
																name="hiveAp.virtualConnection.rxMac" /></th>
														</tr>
														<tr style="display: none;"
															id="createSectionVirtualConnect">
															<td class="listHead">&nbsp;</td>
															<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																name="virtualConnectName" size="16" maxlength="32" /><br>
															<s:text name="config.configTemplate.configName.range" /></td>
															<td class="listHead" valign="top"><s:select
																name="virtualConnectAction"
																list="%{virtualConnectActions}" listKey="key"
																listValue="value" cssStyle="width: 110px;"
																onchange="selectVirtualAction(this.value)" /></td>
															<td class="listHead" valign="top"><s:select
																name="virtualConnectInterface_in"
																list="%{virtualConnectInterfaces}" listKey="key"
																listValue="value" cssStyle="width: 110px;"
																onchange="selectInInterface(this.value)" /></td>
															<td class="listHead" valign="top"><s:textfield
																name="virtualConnectSourceMac" size="16" maxlength="12"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
															<br>
															<s:text name="hiveAp.virtualConnection.sourceMac.range" /></td>
															<td class="listHead" valign="top"><s:textfield
																name="virtualConnectDestMac" size="16" maxlength="12"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
															<br>
															<s:text name="config.macOrOui.addressRange" /></td>
															<td class="listHead" valign="top"><s:textfield
																name="virtualConnectTxMac" size="16" maxlength="12"
																onkeypress="return hm.util.keyPressPermit(event,'hex');"
																disabled="true" /> <br>
															<s:text name="config.macOrOui.addressRange" /></td>
															<td class="listHead" valign="top"><s:select
																name="virtualConnectInterface_out"
																list="%{virtualConnectInterfaces}" listKey="key"
																listValue="value" cssStyle="width: 110px;"
																onchange="selectOutInterface(this.value)" /></td>
															<td class="listHead" valign="top"><s:textfield
																name="virtualConnectRxMac" size="16" maxlength="12"
																onkeypress="return hm.util.keyPressPermit(event,'hex');"
																disabled="true" /> <br>
															<s:text name="config.macOrOui.addressRange" /></td>
														</tr>
														<tr>
															<td colspan="10">
																<table id="virtualConnectTblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:iterator value="%{dataSource.virtualConnections}"
																		status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox
																				name="virtualConnectIndices"
																				fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="forwardName" /></td>
																			<td class="list"><s:property
																				value="forwardAction4Display" /></td>
																			<td class="list"><s:property
																				value="interface_in4Display" /></td>
																			<td class="list"><s:property value="sourceMac" /></td>
																			<td class="list"><s:property value="destMac" /></td>
																			<td class="list"><s:property value="txMac" /></td>
																			<td class="list"><s:property
																				value="interface_out4Display" /></td>
																			<td class="list"><s:property value="rxMac" /></td>
																		</tr>
																	</s:iterator>
																</table>
															</td>
														</tr>
														<tr>
															<td colspan="10" width="100%">
																<table id="virtualConnectTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
																	<s:if test="%{virtualConnectGridCount > 0}">
																		<s:generator separator="," val="%{' '}"
																			count="%{virtualConnectGridCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="9">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																</table>
															</td>
														</tr> --%>

													</table>
													</td>
												</tr>
												<tr>
													<td height="30"></td>
												</tr>
											</table>
											</fieldset>
											</td>
										</tr>
									</table>
									</div>
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
			</td>
		</tr>
	</table>
</s:form>
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" style="background-color: #FFFFFF;" src="">
		</iframe>
	</div>
</div>

<div id="portDetailsPanel" style="display: none;">
	<div class="hd"><s:text name="monitor.hiveAp.port.title"/></div>
	<div class="bd">
		<iframe id="portDetailsFrame" name="portDetailsFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<div id="cvgdiv" style="display:none">
<table id="interfaceEthSettingTableTemp" cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
														<tr>
															<td height="10"></td>
														</tr>
														<tr id="br_head_setting">
															<th align="left" width="40px"><s:text
																name="hiveAp.brRouter.port.settings.port" /></th>
															<th align="left" width="80px"><s:text
																name="hiveAp.brRouter.port.settings.role" /></th>
															<th align="left" width="80px"><s:text
																name="hiveAp.brRouter.port.settings.adminState" /></th>
															<th align="left" width="80px"><s:text
																name="hiveAp.brRouter.port.settings.transmissionType" /></th>
															<th align="left" width="60px"><s:text
																name="hiveAp.brRouter.port.settings.speed" /></th>
															<th id="head_enableNat" align="left" width="62px"><s:text
																name="hiveAp.brRouter.port.settings.enableNat" /></th>
															<th id="head_disablePortForwarding" align="left" width="100px"><s:text
					                                            name="hiveAp.brRouter.port.settings.disablePortForwarding" /></th>
															<th id="head_priority" align="left" width="60px"><s:text
																name="hiveAp.brRouter.port.settings.priority" /></th>
															<th id="head_enablePppoe" align="left" width="80px">
															 	<s:text name="hiveAp.brRouter.port.settings.enablePppoe"/></th>
															<th id="head_pppoeAuth" align="left" width="140px">
															 	<s:text name="hiveAp.brRouter.port.settings.pppoeAuth"/></th>
														</tr>
														<tr id="br_eth0_setting">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth0"/></td>
															<td class="list">
																<table cellspacing="0" cellpadding="0" border="0" width="100%"><tr>
																	<td id="br_eth0_setting_role_backhaul" style="display:none" >
																		<s:text name="hiveAp.autoProvisioning.br100.if.role.backhaul"/>
																	</td>
																	<td id="br_eth0_setting_role_wan" style="display:none" >
																		<s:text name="hiveAp.vpnGateway.if.wan"/>
																	</td>
																	<td id="br_eth0_setting_role">
																		<s:text name="hiveAp.autoProvisioning.br100.if.role.wan"/>
																		<s:hidden name="branchRouterEth0.role"/>
																	</td>
																</tr></table>
															</td>
															<td class="list"><s:select
																	name="branchRouterEth0.adminState"
																	value="%{branchRouterEth0.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth0.duplex"
																	value="%{branchRouterEth0.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth0.speed"
																	value="%{branchRouterEth0.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	onchange="" /></td>
															<td id="eth0_enableNat" class="list">
																<s:checkbox name="branchRouterEth0.enableNat" />
															</td>
															<td id="eth0_disablePortForwarding" class="list">
															   <s:checkbox  name="branchRouterEth0.disablePortForwarding" />
															</td>
															<td id="eth0_priority" class="list">
																<s:textfield name="branchRouterEth0.priority" size="4" maxlength="4"/>
															</td>
															<td id="eth0_enablePppoe" class="list"><s:checkbox name="dataSource.enablePppoe" onclick="enabledPppoeCheckBox(this.checked)"></s:checkbox></td>
															<td id="eth0_pppoeAuth" class="list" style="display: <s:property value='pppoeAuthProfileStyle'/>">
																<s:select list="%{pppoeAuthProfiles}" listKey="id" id="eth0_pppoeAuthProfile"
																	listValue="value" name="pppoeAuthProfile" cssStyle="width: 80px;" />
																	<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newPPPoE')"><img
																			class="dinl" src="<s:url value="/images/new.png" />"
																			width="16" height="16" alt="New" title="New" /></a>
																	</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('editPPPoE')"><img
																			class="dinl" src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else></td>
														</tr>
														<tr id="br_eth1_setting" style="display:<s:property value="%{lan1Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
															<td class="list">
																		<s:select name="branchRouterEth1.role"
																			value="%{branchRouterEth1.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(1,this.value);branchRouterEthChangedWarning(1,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth1.adminState"
																	value="%{branchRouterEth1.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth1.duplex"
																	value="%{branchRouterEth1.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="%{enableEthSetting}"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth1.speed"
																	value="%{branchRouterEth1.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="%{enableEthSetting}"
																	onchange="" /></td>
															<td id="eth1_enableNat" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth1.enableNat"/>
															</td>
															<td id="eth1_disablePortForwarding" class="list">
															   <s:checkbox  name="branchRouterEth1.disablePortForwarding" />
															</td>
															<td id="eth1_priority" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> ">
																<s:textfield name="branchRouterEth1.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth2_setting" style="display:<s:property value="%{lan2Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
															<td class="list">
																		<s:select name="branchRouterEth2.role"
																			value="%{branchRouterEth2.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(2,this.value);branchRouterEthChangedWarning(2,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth2.adminState"
																	value="%{branchRouterEth2.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth2.duplex"
																	value="%{branchRouterEth2.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth2.speed"
																	value="%{branchRouterEth2.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth2_enableNat" class="list" style="display:<s:property value="%{eth2RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth2.enableNat"/>
															</td>
															<td id="eth2_disablePortForwarding" class="list"  style="display:<s:property value="%{eth2RoleStyle}" /> ">
															    <s:checkbox name="branchRouterEth2.disablePortForwarding" /></td>
															<td id="eth2_priority" class="list" style="display:<s:property value="%{eth2RoleStyle}" /> ">
																<s:textfield name="branchRouterEth2.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth3_setting" style="display:<s:property value="%{lan3Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth3"/></td>
															<td class="list">
																		<s:select name="branchRouterEth3.role"
																			value="%{branchRouterEth3.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(3,this.value);branchRouterEthChangedWarning(3,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth3.adminState"
																	value="%{branchRouterEth3.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth3.duplex"
																	value="%{branchRouterEth3.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth3.speed"
																	value="%{branchRouterEth3.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth3_enableNat" class="list" style="display:<s:property value="%{eth3RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth3.enableNat"/>
															</td>
															<td id="eth3_disablePortForwarding" class="list"  style="display:<s:property value="%{eth3RoleStyle}" /> ">
															    <s:checkbox  name="branchRouterEth3.disablePortForwarding" />
															 </td>
															<td id="eth3_priority" class="list" style="display:<s:property value="%{eth3RoleStyle}" /> ">
																<s:textfield name="branchRouterEth3.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_eth4_setting" style="display:<s:property value="%{lan4Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth4"/></td>
															<td class="list">
																		<s:select name="branchRouterEth4.role"
																			value="%{branchRouterEth4.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(4,this.value);branchRouterEthChangedWarning(4,this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterEth4.adminState"
																	value="%{branchRouterEth4.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterEth4.duplex"
																	value="%{branchRouterEth4.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterEth4.speed"
																	value="%{branchRouterEth4.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth4_enableNat" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
																<s:checkbox name="branchRouterEth4.enableNat"/>
															</td>
															<td id="eth4_disablePortForwarding" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
															<s:checkbox name="branchRouterEth4.disablePortForwarding" /></td>
															<td id="eth4_priority" class="list" style="display:<s:property value="%{eth4RoleStyle}" /> ">
																<s:textfield name="branchRouterEth4.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_usb_setting">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.usb"/></td>
															<td class="list"><s:select
																	name="branchRouterUSB.role"
																	value="%{branchRouterUSB.role}"
																	list="%{enumUsbRoleType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="branchRouterUSBChanged(this.value); branchRouterUsbChangedWarning(this.value)" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.adminState"
																	value="%{branchRouterUSB.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.duplex"
																	value="%{branchRouterUSB.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterUSB.speed"
																	value="%{branchRouterUSB.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="usb_enableNat" class="list">
																<s:checkbox name="branchRouterUSB.enableNat" />
															</td>
															<td id="usb_disablePortForwarding" class="list">
															    <s:checkbox name="branchRouterUSB.disablePortForwarding" />
															</td>
															<td id="usb_priority" class="list">
																<s:textfield name="branchRouterUSB.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_wifi0_setting" style="display:<s:property value="%{wifi0Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0"/></td>
															<td class="list">
																		<s:select name="branchRouterWifi0.role"
																			value="%{branchRouterWifi0.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(5,this.value);branchRouterEthChangedWarning(5,this.value);changeWifiClinetMode('wifi0',this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterWifi0.adminState"
																	value="%{branchRouterWifi0.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi0.duplex"
																	value="%{branchRouterWifi0.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi0.speed"
																	value="%{branchRouterWifi0.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth5_enableNat" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi0.enableNat"/>
															</td>
															<td id="eth5_disablePortForwarding" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi0.disablePortForwarding"/>
															</td>
															<td id="eth5_priority" class="list" style="display:<s:property value="%{wifi0RoleStyle}" /> ">
																<s:textfield name="branchRouterWifi0.priority" size="4" maxlength="4"/>
															</td>
														</tr>
														<tr id="br_wifi1_setting" style="display:<s:property value="%{wifi1Style}" /> ">
															<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.wifi1"/></td>
															<td class="list">
																		<s:select name="branchRouterWifi1.role"
																			value="%{branchRouterWifi1.role}"
																			list="%{enumRoleType}" listKey="key"
																			listValue="value" cssStyle="width: 80px;"
																		    onchange="branchRouterEthChanged(6,this.value);branchRouterEthChangedWarning(6,this.value);changeWifiClinetMode('wifi1',this.value);" />
															</td>
															<td class="list"><s:select
																	name="branchRouterWifi1.adminState"
																	value="%{branchRouterWifi1.adminState}"
																	list="%{enumAdminStateType}" listKey="key"
																	listValue="value" cssStyle="width: 80px;"
																	onchange="" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi1.duplex"
																	value="%{branchRouterWifi1.duplex}"
																	list="%{enumDuplexType}" listKey="key"
																	disabled="false"
																	listValue="value" cssStyle="width: 80px;" /></td>
															<td class="list"><s:select
																	name="branchRouterWifi1.speed"
																	value="%{branchRouterWifi1.speed}"
																	list="%{enumSpeedType}" listKey="key"
																	listValue="value" cssStyle="width: 60px;"
																	disabled="false"
																	onchange="" /></td>
															<td id="eth6_enableNat" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
																<s:checkbox name="branchRouterWifi1.enableNat"/>
															</td>
															<td id="eth6_disablePortForwarding" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
															      <s:checkbox name="branchRouterWifi1.disablePortForwarding" />
															 </td>
															<td id="eth6_priority" class="list" style="display:<s:property value="%{wifi1RoleStyle}" /> ">
																<s:textfield name="branchRouterWifi1.priority" size="4" maxlength="4"/>
															</td>
														</tr>
													</table>
<table id="interfaceEthSettingTableTemp2" cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
		<tr>
			<td height="10"></td>
		</tr>
		<tr id="br_head_setting">
			<th align="left" width="40px"><s:text
					name="hiveAp.brRouter.port.settings.port" /></th>
			<th align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.role" /></th>
			<th align="left" width="10px" style="display:none" ><s:text
					name="prority" /></th>
			<th id="head_priority" align="left" width="60px"><s:text
					name="hiveAp.brRouter.port.settings.priority" /></th>
			<th align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.adminState" /></th>
			<th align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.transmissionType" /></th>
			<th align="left" width="60px"><s:text
					name="hiveAp.brRouter.port.settings.speed" /></th>
			<th id="head_enableNat" align="left" width="62px"><s:text
					name="hiveAp.brRouter.port.settings.enableNat" /></th>
			<th id="head_disablePortforwarding" align="left" width="100px"><s:text
					name="hiveAp.brRouter.port.settings.disablePortForwarding" />&nbsp;&nbsp;</th>
			<th id="head_connectionType" align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.connectionType" /></th>
			<th id="head_staticIp" align="left" width="80px">
			<table>
				<tr><td><s:text
					name="hiveAp.brRouter.port.settings.staticIp" /></td></tr>
				<tr><td><s:text
					name="hiveAp.brRouter.port.settings.netmask" /></td></tr>			
			</table>
			</th>
			<th id="head_defaultGateway" align="left" width="80px"><s:text
					name="hiveAp.brRouter.port.settings.defaultGateway" /></th>
			<th id="head_pppoeAuth" align="left" width="140px"><s:text
					name="hiveAp.brRouter.port.settings.pppoeAuth" /></th>
		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth0andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth0_setting">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth0" /></td>
			<td class="list" id="eth0strTd">
				<s:text name="hiveAp.vpnGateway.if.wan" /> 
			</td>
			<td id="eth0_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth0.priority" /></td>
			<td class="list">
			<div id="eth0TdDiv" style="display:<s:property value="%{flageth0Str}" /> ">
			<s:select name="branchRouterEth0.wanOrder" id="eth0WanSelectListID"
					value="%{branchRouterEth0.wanOrder}" onchange="wanOrderTrigger('eth0WanSelectListID');"
					list="%{enumPriorityEth0LevelType}" listKey="key" listValue="value" />
			</div>
			</td>
			<td class="list"><s:select name="branchRouterEth0.adminState"
					value="%{branchRouterEth0.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth0.duplex"
					value="%{branchRouterEth0.duplex}" list="%{enumDuplexType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth0.speed"
					value="%{branchRouterEth0.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;" onchange="" /></td>
			<td id="eth0_enableNat" class="list"><s:checkbox
					name="branchRouterEth0.enableNat" /></td>
			<td id="eth0_disablePortForwarding" class="list"><s:checkbox
					name="branchRouterEth0.disablePortForwarding" /></td>
			<td class="list" id="eth0ConnectTdDiv"><s:select
					name="branchRouterEth0.connectionType" id="branchRouterEth0_connectionType"
					value="%{branchRouterEth0.connectionType}"
					list="%{enumConnectionType1}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth0');"/></td>
			<td class="list" >
			<div id="eth0StaticIP" style="display:<s:property value='eth0StaticIpFlag'/>">
				<s:textfield name="branchRouterEth0.ipAndNetmask" id="branchRouterEth0_ipAddress"  onkeypress="return hm.util.keyPressPermit(event,'ipMask');" size="12"></s:textfield>
				</div>
			</td>
			<td class="list" >
				<div id="eth0DefaultGateway" style="display:<s:property value='eth0StaticIpFlag'/>">
					<s:textfield name="branchRouterEth0.gateway" id="branchRouterEth0_gateway" size="12"/>
				</div>
			</td>

			<td  class="list">
			<div id="eth0_pppoeAuthrouter" style="display:<s:property value='PppoeAuthFlag'/>">
				<s:select list="%{pppoeAuthProfiles}" listKey="id" listValue="value" id="eth0_pppoeAuthProfile"
					name="pppoeAuthProfile" cssStyle="width: 80px;" /> <s:if
					test="%{writeDisabled == 'disabled'}">
					<img class="dinl marginBtn"
						src="<s:url value="/images/new_disable.png" />" width="16"
						height="16" alt="New" title="New" />
				</s:if> <s:else>
					<a class="marginBtn" href="javascript:submitAction('newPPPoE')"><img
						class="dinl" src="<s:url value="/images/new.png" />" width="16"
						height="16" alt="New" title="New" /></a>
				</s:else> <s:if test="%{writeDisabled == 'disabled'}">
					<img class="dinl marginBtn"
						src="<s:url value="/images/modify_disable.png" />" width="16"
						height="16" alt="Modify" title="Modify" />
				</s:if> <s:else>
					<a class="marginBtn" href="javascript:submitAction('editPPPoE')"><img
						class="dinl" src="<s:url value="/images/modify.png" />" width="16"
						height="16" alt="Modify" title="Modify" /></a>
				</s:else>
				</div>
				</td>
		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth1andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth1_setting"
			style="display:<s:property value="%{lan1Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth1" /></td>
			<td class="list" id="eth1strTd"><s:property  value="%{eth1str}" /></td>
			<td id="eth1_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth1.priority" /></td>
			<td class="list" >
			<div id="eth1TdDiv" style="display:<s:property value="%{flageth1Str}" /> ">
					<s:select name="branchRouterEth1.wanOrder" id="eth1WanSelectListID"
						value="%{branchRouterEth1.wanOrder}" onchange="wanOrderTrigger('eth1WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
					</div>	
				</td>
			<td class="list"><s:select name="branchRouterEth1.adminState"
					value="%{branchRouterEth1.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" />
			</td>
			<td class="list"><s:select name="branchRouterEth1.duplex"
					value="%{branchRouterEth1.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="%{enableEthSetting}" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth1.speed"
					value="%{branchRouterEth1.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="%{enableEthSetting}" onchange="" /></td>
			<td id="eth1_enableNat" class="list"
				style="display:<s:property value="%{eth1RoleStyle}" /> "><s:checkbox
					name="branchRouterEth1.enableNat" /></td>
			<td id="eth1_disablePortForwarding" class="list" style="display:<s:property value="%{eth1RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth1.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth1ConnectTdDiv" style="display:<s:property value="%{flageth1Str}" /> ">
			<s:select	name="branchRouterEth1.connectionType" id="branchRouterEth1_connectionType"
					value="%{branchRouterEth1.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth1');"/>
			</div>
			</td>
			
			<td class="list" id="eth1StaticIP" style="display:<s:property value='eth1StaticIpFlag'/>">
				<s:textfield name="branchRouterEth1.ipAndNetmask" id="branchRouterEth1_ipAddress" onkeypress="return hm.util.keyPressPermit(event,'ipMask');" size="12"/>
			</td>
			<td class="list" id="eth1DefaultGateway" style="display:<s:property value='eth1StaticIpFlag'/>">
				<s:textfield name="branchRouterEth1.gateway" id="branchRouterEth1_gateway" size="12"/>
			</td>
			<td class="list"></td>


		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth2andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth2_setting"
			style="display:<s:property value="%{lan2Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth2" /></td>
			<td class="list" id="eth2strTd"><s:property  value="%{eth2str}" /></td>
			<td id="eth2_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth2.priority" /></td>
			<td class="list" >
			<div id="eth2TdDiv" style="display:<s:property value="%{flageth2Str}" /> ">
					<s:select name="branchRouterEth2.wanOrder" id="eth2WanSelectListID" onchange="wanOrderTrigger('eth2WanSelectListID');"
						value="%{branchRouterEth2.wanOrder}" 
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth2.adminState"
					value="%{branchRouterEth2.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth2.duplex"
					value="%{branchRouterEth2.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth2.speed"
					value="%{branchRouterEth2.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth2_enableNat" class="list"
				style="display:<s:property value="%{eth2RoleStyle}" /> "><s:checkbox
					name="branchRouterEth2.enableNat" /></td>
		   <td id="eth2_disablePortForwarding" class="list" style="display:<s:property value="%{eth2RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth2.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth2ConnectTdDiv" style="display:<s:property value="%{flageth2Str}" /> ">
			<s:select	name="branchRouterEth2.connectionType" id="branchRouterEth2_connectionType"
					value="%{branchRouterEth2.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth2');"/>
			</div>		
			</td>
				
			<td class="list" id="eth2StaticIP" style="display:<s:property value='eth2StaticIpFlag'/>">
				<s:textfield name="branchRouterEth2.ipAndNetmask" id="branchRouterEth2_ipAddress" size="12"></s:textfield>
			</td>
			<td class="list" id="eth2DefaultGateway" style="display:<s:property value='eth2StaticIpFlag'/>">
				<s:textfield name="branchRouterEth2.gateway" id="branchRouterEth2_gateway" size="12"/>
			</td>
			<td class="list"></td>

		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth3andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth3_setting"
			style="display:<s:property value="%{lan3Style}" /> ">
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth3" /></td>
			<td class="list" id="eth3strTd"><s:property  value="%{eth3str}" /></td>
			<td id="eth3_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth3.priority" /></td>
			<td class="list">
			<div id="eth3TdDiv" style="display:<s:property value="%{flageth3Str}" /> ">
					<s:select name="branchRouterEth3.wanOrder" id="eth3WanSelectListID"
						value="%{branchRouterEth3.wanOrder}" onchange="wanOrderTrigger('eth3WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth3.adminState"
					value="%{branchRouterEth3.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterEth3.duplex"
					value="%{branchRouterEth3.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth3.speed"
					value="%{branchRouterEth3.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth3_enableNat" class="list"
				style="display:<s:property value="%{eth3RoleStyle}" /> "><s:checkbox
					name="branchRouterEth3.enableNat" /></td>
			<td id="eth3_disablePortForwarding" class="list" 
			    style="display:<s:property value="%{eth3RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth3.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth3ConnectTdDiv" style="display:<s:property value="%{flageth3Str}" /> ">
			<s:select name="branchRouterEth3.connectionType" id="branchRouterEth3_connectionType"
					value="%{branchRouterEth3.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth3');"/>
			</div>		
					</td>
					
			<td class="list" id="eth3StaticIP" style="display:<s:property value='eth3StaticIpFlag'/>">
				<s:textfield name="branchRouterEth3.ipAndNetmask" id="branchRouterEth3_ipAddress" size="12"></s:textfield>
			</td>
			<td class="list" id="eth3DefaultGateway" style="display:<s:property value='eth3StaticIpFlag'/>">
				<s:textfield name="branchRouterEth3.gateway" id="branchRouterEth3_gateway" size="12"/>
			</td>
			<td class="list"></td>

		</tr>
		<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_eth4andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_eth4_setting"
			style="display:<s:property value="%{lan4Style}" /> ">
			<td class="list" ><s:text
					name="hiveAp.autoProvisioning.br100.if.port.eth4" /></td>
			<td class="list" id="eth4strTd"><s:property  value="%{eth4str}" /></td>
			<td id="eth4_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterEth4.priority" /></td>
			<td class="list">
			<div id="eth4TdDiv" style="display:<s:property value="%{flageth4Str}" /> ">
					<s:select name="branchRouterEth4.wanOrder" id="eth4WanSelectListID"
						value="%{branchRouterEth4.wanOrder}" onchange="wanOrderTrigger('eth4WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
				</div>
				</td>
			<td class="list"><s:select name="branchRouterEth4.adminState"
					value="%{branchRouterEth4.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" />
			</td>
			<td class="list"><s:select name="branchRouterEth4.duplex"
					value="%{branchRouterEth4.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterEth4.speed"
					value="%{branchRouterEth4.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth4_enableNat" class="list"
				style="display:<s:property value="%{eth4RoleStyle}" /> "><s:checkbox
					name="branchRouterEth4.enableNat" /></td>
			<td id="eth4_disablePortForwarding" class="list"
			   style="display:<s:property value="%{eth4RoleStyle}" /> "><s:checkbox 
			       name="branchRouterEth4.disablePortForwarding" /></td>
			<td class="list" >
			<div id="eth4ConnectTdDiv" style="display:<s:property value="%{flageth4Str}" /> ">
			<s:select 	name="branchRouterEth4.connectionType" id="branchRouterEth4_connectionType"
					value="%{branchRouterEth4.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'eth4');"/>
			</div>
			</td>
			
		
			<td class="list" id="eth4StaticIP" style="display:<s:property value='eth4StaticIpFlag'/>">
				<s:textfield name="branchRouterEth4.ipAndNetmask" id="branchRouterEth4_ipAddress" size="12"/>
			</td>
			<td class="list" id="eth4DefaultGateway" style="display:<s:property value='eth4StaticIpFlag'/>">
				<s:textfield name="branchRouterEth4.gateway" id="branchRouterEth4_gateway" size="12"/>
			</td>
			<td class="list"></td>


		</tr>
	 	<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_usbandgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_usb_setting" >
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.usb" /></td>
			<td class="list" id="usbstrTd">
					<s:property  value="%{usbstr}" />
			<td id="usb_wan_priority" style="display:none" class="list"><s:text
					name="branchRouterUSB.priority" /></td>
			<td class="list">
				<div id="usbTdDiv" style="display:<s:property value="%{flageUsbStr}" /> ">
					<s:select name="branchRouterUSB.wanOrder" id="usbWanSelectListID"
						value="%{branchRouterUSB.wanOrder}" onchange="wanOrderTrigger('usbWanSelectListID');"
						list="%{enumPriorityUsbLevelType}" listKey="key" listValue="value" onClick=""/>
						</div>
				</td>
			<td class="list"><s:select name="branchRouterUSB.adminState"
					value="%{branchRouterUSB.adminState}" list="%{enumAdminStateType}"
					listKey="key" listValue="value" cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"></td>
			<td class="list"></td>
			<%-- <td class="list"><s:select name="branchRouterUSB.duplex"
					value="%{branchRouterUSB.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterUSB.speed"
					value="%{branchRouterUSB.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td> --%>
			<td id="usb_enableNat" class="list"><s:checkbox
					name="branchRouterUSB.enableNat" /></td>
			<td id="usb_disablePortForwarding" class="list"><s:checkbox
					name="branchRouterUSB.disablePortForwarding" /></td>
			<td >
			<div id="usbConnectTdDiv" style="display:none">
			<s:select
					name="branchRouterUSB.connectionType" id="branchRouterUSB_connectionType"
					value="%{branchRouterUSB.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'usb');"/>
			</div>
			</td>
							
			<td id="usbStaticIP" style="display:none">
				<s:textfield name="branchRouterUSB.ipAddress" id="branchRouterUSB_pAddress" size="12"/>
			</td>
			<td id="usbDefaultGateway" style="display:none" >
				<s:textfield name="branchRouterUSB.gateway" id="branchRouterUSB_gateway" size="12"/>
			</td>
			<td ></td>


		</tr>
	 	<tr>
			<td height="5"  colspan="10">
			<table>
				<tr>
					<td id="error_message_wifi0andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_wifi0_setting" >
			<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0" />
			</td>
			<td class="list" id="wifi0strTd">
					<s:property  value="%{wifi0str}" />
			</td>
			<td id="wifi0_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterWifi0.priority" /></td>
			<td class="list">
					<s:select name="branchRouterWifi0.wanOrder"
						value="%{branchRouterWifi0.wanOrder}" id="wifi0WanSelectListID" onchange="wanOrderTrigger('wifi0WanSelectListID');"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
			</td>
			<td class="list"><s:select name="branchRouterWifi0.adminState"
					value="%{branchRouterWifi0.adminState}"
					list="%{enumAdminStateType}" listKey="key" listValue="value"
					cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterWifi0.duplex"
					value="%{branchRouterWifi0.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterWifi0.speed"
					value="%{branchRouterWifi0.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth5_enableNat" class="list"><s:checkbox
					name="branchRouterWifi0.enableNat" /></td>
			<td id="eth5_disablePortForwarding" class="list" ><s:checkbox 
			       name="branchRouterWifi0.disablePortForwarding" /></td>
		<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.port.wifi0.dhcp" /> 
			</td>
			<%--<td class="list" >
			
			 <s:select name="branchRouterWifi0.connectionType" id="branchRouterWifi0_connectionType"
					value="%{branchRouterWifi0.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'wifi0');"/>
			</td> --%>				
			<td class="list" id="wifi0StaticIP"   >
				<s:textfield name="branchRouterWifi0.ipAndNetmask" size="12" />
			</td>
			<td class="list" id="wifi0DefaultGateway" >
				<s:textfield name="branchRouterWifi0.gateway" size="12" />
			</td>
			<td class="list"></td>
		</tr>
 		<tr>
			<td height="5" colspan="10">
			<table>
				<tr>
					<td id="error_message_wifi1andgateway" colspan="10"></td>
				</tr>
			</table>
			</td>
		</tr> 
		<tr id="br_wifi1_setting"  >
			<td class="list"><s:text
					name="hiveAp.autoProvisioning.br100.if.port.wifi1" /></td>
			<td class="list">
			<s:text name="hiveAp.autoProvisioning.br100.if.role.wan" />
			</td>
			<td id="wifi1_wan_priority" style="display:none" class="list" ><s:text
					name="branchRouterWifi1.priority" /></td>
			<td class="list">
					<s:select name="branchRouterWifi1.priority"
						value="%{branchRouterWifi1.priority}"
						list="%{enumPriorityLevelType}" listKey="key" listValue="value" />
			</td>
			<td class="list"><s:select name="branchRouterWifi1.adminState"
					value="%{branchRouterWifi1.adminState}"
					list="%{enumAdminStateType}" listKey="key" listValue="value"
					cssStyle="width: 80px;" onchange="" /></td>
			<td class="list"><s:select name="branchRouterWifi1.duplex"
					value="%{branchRouterWifi1.duplex}" list="%{enumDuplexType}"
					listKey="key" disabled="false" listValue="value"
					cssStyle="width: 80px;" /></td>
			<td class="list"><s:select name="branchRouterWifi1.speed"
					value="%{branchRouterWifi1.speed}" list="%{enumSpeedType}"
					listKey="key" listValue="value" cssStyle="width: 60px;"
					disabled="false" onchange="" /></td>
			<td id="eth6_enableNat" class="list"><s:checkbox
					name="branchRouterWifi1.enableNat" /></td>
		    <td id="eth6_disablePortForwarding" class="list" ><s:checkbox 
			       name="branchRouterWifi1.disablePortForwarding" /></td>
			<td class="list" ><s:select
					name="branchRouterWifi1.connectionType" id="branchRouterWifi1_connectionType"
					value="%{branchRouterWifi1.connectionType}"
					list="%{enumConnectionType2}" listValue="value" listKey="key" onchange="changeIPandGateway(this,'wifi1');"/></td>
									
			<td class="list" id="wifi1StaticIP" >
				<s:textfield name="branchRouterWifi1.ipAndNetmask" size="12"></s:textfield>
			</td>
			<td class="list" id="wifi1DefaultGateway" >
				<s:textfield name="branchRouterWifi1.gateway" size="12"/>
			</td>
			<td class="list"></td>

		</tr>
		
	</table>
</div>
<script type="text/javascript">
//====================== water mark start ==========================
	var hintClassName="hint";
	var bgpNeighborsNamesHint = '<s:text name="config.routingProfiles.bgpNeighbors.default"/>'
		//-------------Common functions-----------------//

		YAHOO.util.Event.onDOMReady(setWaterMark);

		function setWaterMark() {
			// initial water mark
			showWaterMark(Get('bgpNeighbors'), bgpNeighborsNamesHint);
			// water mark event
			YAHOO.util.Event.on(Get('bgpNeighbors'), "focus", focusAction);
			YAHOO.util.Event.on(Get('bgpNeighbors'), "blur", blurAction);
		}

		window.setTimeout("setWaterMark()", 100);

	///--------------- Domain Names: WaterMark -----------------///
	function focusAction(e) {
		hideWaterMark(this.id);
	}

	function blurAction(e) {
		showWaterMark(this.id, bgpNeighborsNamesHint);
	}

	function hideWaterMark(elementId) {
		var element = Get(elementId);
		if(element) {
			if(YAHOO.util.Dom.hasClass(element, hintClassName)) {
				YAHOO.util.Dom.removeClass(element, hintClassName);
				element.value = "";
			}
		}
	}
	function showWaterMark(elementId, text) {
		var element = Get(elementId);
		if(element) {
			var value = element.value;
			if (value.length == 0 || value.trim().length == 0) {
				YAHOO.util.Dom.addClass(element, hintClassName);
				element.value = text;
			}
		}
	}
	//====================== water mark end ==========================
</script>



<s:if test="%{jsonMode == true}">
<script type="text/javascript">

	function judgeFoldingIcon() {
		adjustFoldingIcon('ethcwpSettings');
		adjustFoldingIcon('networkSettings');
		adjustFoldingIcon('eth0BridgeAdvSettings');
		adjustFoldingIcon('eth1BridgeAdvSettings');
		adjustFoldingIcon('agg0BridgeAdvSettings');
		adjustFoldingIcon('red0BridgeAdvSettings');
		adjustFoldingIcon('ethAdvSettings');
		adjustFoldingIcon('configMdmContent');
		adjustFoldingIcon('ssidAllocation');
		adjustFoldingIcon('lanAllocation');
		adjustFoldingIcon('pseSettingDiv');
		adjustFoldingIcon('serviceSettings');
		adjustFoldingIcon('l3Roaming');
		adjustFoldingIcon('credentials');
		adjustFoldingIcon('routing');
		adjustFoldingIcon('advancedSettings');
		adjustFoldingIcon('staticRoutes');
		adjustFoldingIcon('mgt0DhcpSettings');
	}
	YAHOO.util.Event.onContentReady('hiveApOptionSection', judgeFoldingIcon, this);
</script>
</s:if>





