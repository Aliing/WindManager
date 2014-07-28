<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.wlan.RadioProfile"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@page import="com.ah.bo.hiveap.HiveAPVirtualConnection"%>
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

<tiles:insertDefinition name="ahDevicePageScript_1" />

<tiles:insertDefinition name="ahDevicePageScript_2" />

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
	<s:hidden name="dataSource.switchPortSettingsStyle"></s:hidden>
	<s:hidden name="dataSource.stormControlDivStyle"></s:hidden>
	<s:hidden name="dataSource.forwardingDBSettingStyle"></s:hidden>
	<s:hidden name="dataSource.lldpcdpSettingStyle"></s:hidden>
	<s:hidden name="dataSource.resrvedVlansSettingStyle"></s:hidden>
	<s:hidden name="dataSource.igmpDivStyle"></s:hidden>
	<s:hidden name="ethUserProfileId"></s:hidden>
	<s:hidden name="dataSource.dhcp"></s:hidden>
	<s:hidden name="dataSource.dhcpFallback"></s:hidden>
	<s:hidden name="oldDeviceType"/>
	<s:hidden name="dataSource.captureDataCwpDivStyle"></s:hidden>
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

	<tiles:insertDefinition name="deviceTitle" />

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
			<td class="buttons"><tiles:insertDefinition name="deviceButton" /></td>
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
							<td><tiles:insertDefinition name="deviceBaseSettings" /></td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr id="radioConfigStyle">
							<td><tiles:insertDefinition name="radioConfig" /></td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr id="hiveApTag">
							<td><tiles:insertDefinition name="deviceTag" /></td>
				       	</tr>
				       	<tr>
							<td height="10px"></td>
						</tr>
						<tr id="wifiConfigStyle">
							<td><tiles:insertDefinition name="deviceWifiConfig" /></td>
				       	</tr>
				       	<tr>
							<td height="10"></td>
						</tr>
						
					</table>
					</td>
				</tr>
				<tr id="vpnGatewayIntStyle">
					<td><tiles:insertDefinition name="cvgSettings" /></td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
				<tr id="hiveApOptionSection">
					<td><!-- optional section -->
					<fieldset><legend><s:text
						name="hiveAp.cfg.optional.tag" /></legend>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr style="display: none;">
							<td><tiles:insertDefinition name="captureDataCwp" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceEthernetCwp" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceMgt0Config" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceEthernetConfig" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="switchPortSettings" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="switchSettings" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="stormControlSettings"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceSwitchQosSetting"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="igmpSettings"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="forwardingDB"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="vlanSettings" /></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceSwitchPSE"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceBrStaticRouter"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceServiceSettings"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceL3Roaming"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="cvgMgtService"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceCredentials"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceRoutingConfig"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceBonjourConfig"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceWifiClientMode"/></td>
						</tr>
						<tr>
							<td><tiles:insertDefinition name="deviceAdvancedSettings"/></td>
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
		adjustFoldingIcon('captureDataCwp');
		adjustFoldingIcon('ethcwpSettings');
		adjustFoldingIcon('networkSettings');
		adjustFoldingIcon('eth0BridgeAdvSettings');
		adjustFoldingIcon('eth1BridgeAdvSettings');
		adjustFoldingIcon('agg0BridgeAdvSettings');
		adjustFoldingIcon('red0BridgeAdvSettings');
		adjustFoldingIcon('ethAdvSettings');
		adjustFoldingIcon('ssidAllocation');
		adjustFoldingIcon('lanAllocation');
		adjustFoldingIcon('pseSettingDiv');
		adjustFoldingIcon('serviceSettings');
		adjustFoldingIcon('l3Roaming');
		adjustFoldingIcon('credentials');
		adjustFoldingIcon('routing');
		adjustFoldingIcon('advancedSettings');
		adjustFoldingIcon('staticRoutes');
		adjustFoldingIcon('igmpSettings');
		adjustFoldingIcon('mgt0DhcpSettings');
		adjustFoldingIcon('vlanSettings');
		adjustFoldingIcon('mgt0DhcpSettings');
		adjustFoldingIcon('vlanSettings');
		adjustFoldingIcon('mgt0DhcpSettings');
	}
	YAHOO.util.Event.onContentReady('hiveApOptionSection', judgeFoldingIcon, this);
</script>
</s:if>