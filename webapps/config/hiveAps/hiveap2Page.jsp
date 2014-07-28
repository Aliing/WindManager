<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.ui.actions.hiveap.HiveApAction"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.wlan.RadioProfile"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@page import="com.ah.bo.hiveap.HiveAPVirtualConnection"%>
<script>
var formName = 'hiveAp';

var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;
var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_170 = <%=HiveAp.HIVEAP_MODEL_170%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;
var MODEL_330 = <%=HiveAp.HIVEAP_MODEL_330%>;
var MODEL_350 = <%=HiveAp.HIVEAP_MODEL_350%>;
var MODEL_370 = <%=HiveAp.HIVEAP_MODEL_370%>;
var MODEL_390 = <%=HiveAp.HIVEAP_MODEL_390%>;
var MODEL_230 = <%=HiveAp.HIVEAP_MODEL_230%>;
var MODEL_SR24= <%=HiveAp.HIVEAP_MODEL_SR24%>;
var MODEL_SR2024P= <%=HiveAp.HIVEAP_MODEL_SR2024P%>;
var MODEL_SR2124P= <%=HiveAp.HIVEAP_MODEL_SR2124P%>;
var MODEL_SR2148P= <%=HiveAp.HIVEAP_MODEL_SR2148P%>;
var MODEL_SR48 = <%=HiveAp.HIVEAP_MODEL_SR48%>;
var BR100 = <%=HiveAp.HIVEAP_MODEL_BR100%>;
var BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var BR200_WP = <%=HiveAp.HIVEAP_MODEL_BR200_WP%>;
var BR200_LTE_VZ = <%=HiveAp.HIVEAP_MODEL_BR200_LTE_VZ%>;
var VPN_GATEWAY = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA%>;
var CVG_APPLIANCE = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY%>;
var DEVICE_TYPE_VPN_GATEWAY = <%=HiveAp.Device_TYPE_VPN_GATEWAY%>;
var DEVICE_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;
var DEVICE_TYPE_HIVEAP = <%=HiveAp.Device_TYPE_HIVEAP%>;
var DEVICE_TYPE_BR_VPN = <%=HiveAp.Device_TYPE_VPN_BR%>;
var DEVICE_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var DEVICE_CONNECT_DHCP=<%=HiveAp.DEVICE_CONNECT_DHCP%>;
var DEVICE_CONNECT_STATIC=<%=HiveAp.DEVICE_CONNECT_STATIC%>;
var DEVICE_CONNECT_PPPOE=<%=HiveAp.DEVICE_CONNECT_PPPOE%>;
var BIND_INTERFACE_NONE = <%=AhInterface.ETH_BIND_IF_NULL%>;
var BIND_INTERFACE_AGG0 = <%=AhInterface.ETH_BIND_IF_AGG0%>;
var BIND_INTERFACE_RED0 = <%=AhInterface.ETH_BIND_IF_RED0%>;
var IF_ADMIN_STATUS_UP = <%=AhInterface.ADMIN_STATE_UP%>;
var IF_ADMIN_STATUS_DOWN = <%=AhInterface.ADMIN_STATE_DOWM%>;
var OPERATION_MODE_BACKHAUL = <%=AhInterface.OPERATION_MODE_BACKHAUL%>;
var OPERATION_MODE_ACCESS = <%=AhInterface.OPERATION_MODE_ACCESS%>;
var OPERATION_MODE_BRIDGE = <%=AhInterface.OPERATION_MODE_BRIDGE%>;
var OPERATION_MODE_DUAL = <%=AhInterface.OPERATION_MODE_DUAL%>;
var OPERATION_MODE_SENSOR = <%=AhInterface.OPERATION_MODE_SENSOR%>;
var OPERATION_MODE_WAN_ACCESS = <%=AhInterface.OPERATION_MODE_WAN_ACCESS%>;
var ETH_SPEED_AUTO = <%=AhInterface.ETH_SPEED_AUTO%>;
var ETH_SPEED_1000M = <%=AhInterface.ETH_SPEED_1000M%>;
var BRANCH_ROLE_PRIMARY = <%=AhInterface.ROLE_PRIMARY%>;
var BRANCH_ROLE_WAN = <%=AhInterface.ROLE_WAN%>;
var ETH_DUPLEX_AUTO = <%=AhInterface.ETH_DUPLEX_AUTO%>;
var ETH_DUPLEX_HALF = <%=AhInterface.ETH_DUPLEX_HALF%>;
var VPN_MARK_SERVER = <%=HiveAp.VPN_MARK_SERVER%>;
var VPN_MARK_CLIENT = <%=HiveAp.VPN_MARK_CLIENT%>;
var VPN_MARK_NONE = <%=HiveAp.VPN_MARK_NONE%>;
var RADIO_MODE_NA = <%=RadioProfile.RADIO_PROFILE_MODE_NA%>;
var RADIO_MODE_NG = <%=RadioProfile.RADIO_PROFILE_MODE_NG%>;
var RADIO_MODE_AC = <%=RadioProfile.RADIO_PROFILE_MODE_AC%>;
var CHANNEL_WIDTH_20 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20%>;
var CHANNEL_WIDTH_40A = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A%>;
var CHANNEL_WIDTH_80 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80%>;
var CHANNEL_WIDTH_40 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40%>;
var CHANNEL_WIDTH_40B = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B%>;
var DENY_ACTION_BAN = <%=SsidProfile.DENY_ACTION_BAN%>;
var PREFERRED_SSID_MAX_NUMBER = <%=HiveApAction.PREFERRED_SSID_MAX_NUMBER%>;
var BR_MAX_ROUTE_COUNT = <%=AhInterface.BR_MAX_ROUTE_COUNT%>;
var delgridCountFlg = true;
var previousNPId = "<s:property value='defaultDisplayNetworkPolicyId' />";
var previousUsbRole = "<s:property value='branchRouterUSB.role' />";
var preNetworkType = "<s:property value='mgt0NetworkType'/>";
<s:if test="%{defaultDisplayNpWirelessRoutingEnabled}">
var npWirelessRoutingEnabledHiveAp = true;
</s:if>
<s:else>
var npWirelessRoutingEnabledHiveAp = false;
</s:else>
var primaryWanObj;
var backup1WanObj;
var backup2WanObj;



function is11nHiveAP(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	if(selectApModel == MODEL_340 || selectApModel == MODEL_320
			|| selectApModel == MODEL_380 || selectApModel == MODEL_120 || selectApModel == MODEL_170
			|| selectApModel == MODEL_110 || selectApModel == BR100
			|| selectApModel == BR200 || selectApModel == BR200_WP || selectApModel == BR200_LTE_VZ
			|| selectApModel == MODEL_330 || selectApModel == MODEL_350 
			|| selectApModel == MODEL_370 || selectApModel == MODEL_390
			|| selectApModel == MODEL_121 || selectApModel == MODEL_141
			|| selectApModel == MODEL_230){
		return true;
	}
	return false;
}

function isEth1Available(){
	//var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	//return (is11nHiveAP() && selectApModel != MODEL_120 && selectApModel != MODEL_110 && selectApModel != MODEL_170 && selectApModel != BR100 && selectApModel != BR200 && selectApModel != BR200_WP);
	
	//fix bug 26968
	<s:if test="%{eth1Available}">
		return true;
	</s:if>
	<s:else>
		return false;
	</s:else>
}

function isWifi1Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	return selectApModel != MODEL_110 && selectApModel != BR100 && selectApModel != BR200 && selectApModel != BR200_WP && selectApModel != BR200_LTE_VZ;
}

function isVpnServerAvaliable(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	if(selectApModel == VPN_GATEWAY 
			|| selectApModel == CVG_APPLIANCE 
			|| selectApModel == MODEL_230
			|| selectApModel == MODEL_320 || selectApModel == MODEL_340
			|| selectApModel == MODEL_330 || selectApModel == MODEL_350 
			|| selectApModel == MODEL_370 || selectApModel == MODEL_390){
		return true;
	}else {
		return false;
	}
}

function showNetworkSettingsContent(){
	showHideContent("networkSettings","");
	showMgt0DhcpAdvSettingsContent();
}

function showMgt0DhcpAdvSettingsContent(){
	showHideContent("mgt0DhcpSettings","");
}

function showServiceSettingsContent(){
	showHideContent("serviceSettings","");
}

function showSsidAllocationContent(){
	showHideContent("ssidAllocation","");
}

function showEthAdvSettingsContent(){
	showHideContent("ethAdvSettings","");
}

function showAdvancedSettingsContent(){
	showHideContent("advancedSettings","");
}

function showBonjourGatewayConfigContent(){
	showHideContent("bonjourGatewayConfig","");
}

function showCredentialsContent(){
	showHideContent("credentials","");
}

function showL3RoamingContent(){
	showHideContent("l3Roaming","");
}

function showRoutingContent(){
	showHideContent("routing","");
}

function showEth0BridgeAdvSettingsContent(){
	showHideContent("eth0BridgeAdvSettings","");
}

function showEth1BridgeAdvSettingsContent(){
	showHideContent("eth1BridgeAdvSettings","");
}

function showAgg0BridgeAdvSettingsContent(){
	showHideContent("agg0BridgeAdvSettings","");
}

function showRed0BridgeAdvSettingsContent(){
	showHideContent("red0BridgeAdvSettings","");
}

function showEthCwpSettingContent(){
	showHideContent("ethcwpSettings", "")
}

function showBRStaticRouting(){
	showHideContent("brStaticRouting", "")
}

function selectEnableDynamicRouting(checked)
{
	if (checked)
	{
		hm.util.show('dynamicRoutingSection');
	}
	else
	{
		hm.util.hide('dynamicRoutingSection');
	}
}

function selectUseMD5(checked)
{
	if (checked){
		hm.util.show('useMD5Section');
	}else{
		hm.util.hide('useMD5Section');
	}
}

function showDynamicRoutingContent() {
	var typeFlag = document.getElementById("typeFlag").value;

	var ripv2Checked = typeFlag == 1;
	var ospfChecked = typeFlag == 2;
	var bgpChecked = typeFlag == 3;
	var noneChecked = typeFlag == 4;
	//document.getElementById("").style.display = ripv2Checked ? "" : "none";
	document.getElementById("ospfSection").style.display = ospfChecked ? "" : "none";
	document.getElementById("bgpSection").style.display = bgpChecked ? "" : "none";
	//document.getElementById("").style.display = noneChecked ? "" : "none";
}


var parentHeight = 0;

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	var expanding_dynamic = <s:property value="%{expanding_dynamic}"/>;
	var expanding_staticRoutes = <s:property value="%{expanding_staticRoutes}"/>;
	var expanding_static = <s:property value="%{expanding_static}"/>;
	var expanding_ip = <s:property value="%{expanding_ip}"/>;
	var expanding_virtualConnect = <s:property value="%{expanding_virtualConnect}"/>;
	var operation = '<s:property value="%{operation}"/>';
	var radioConfigType = <s:property value="%{dataSource.radioConfigType}"/>;
	var deviceType = <s:property value="%{dataSource.deviceType}"/>;
	var deviceModel = <s:property value="%{dataSource.hiveApModel}"/>;

	var expanding_vlanid = <s:property value="%{expanding_vlanid}"/>;
	if(operation == 'new2'||operation == 'new3'||operation == 'clone2'){
		document.getElementById(formName + "_dataSource_hostName").focus();
	}
	if(operation == 'new2'||operation == 'new3' || expanding_dynamic){
		showCreateSection('dynamic');
	}
	if(operation == 'new2'||operation == 'new3' || expanding_static){
       	showCreateSection('static');
    }
	if(operation == 'new2'||operation == 'new3' || expanding_ip){
       	showCreateSection('ip');
    }
	if(operation == 'new2'||operation == 'new3' || expanding_virtualConnect){
       	showCreateSection('virtualConnect');
    }
	if(operation == 'new2'||operation == 'new3' || expanding_vlanid){
       	showCreateSection('vlanid');
    }
	if(operation == 'new2'||operation == 'new3' || expanding_staticRoutes){
		showCreateSection('staticRoutes');
	}

    // Update SSID allocation section
    updateSsidAllocation();
    // Update native vlan section
    updateNativeVlan();
	// Update mgt vlan section
	updateMgtVlan();
    // updateCapwapIp();
	changeApModel();

	//update cvg PMTUD
	updateCvgForMonitorMSS();
	//update cvg PMTUD
	updateBrForMonitorMSS();


    wholenativeVlanEl0 = document.getElementById(formName + "_dataSource_eth0_multiNativeVlan").value;
    wholenativeVlanEl1 = document.getElementById(formName + "_dataSource_eth1_multiNativeVlan").value;
    wholenativeVlanElagg0 = document.getElementById(formName + "_dataSource_agg0_multiNativeVlan").value;
    wholenativeVlanElred0 = document.getElementById(formName + "_dataSource_red0_multiNativeVlan").value;

	//control usbmodem display
	var branchRouterUSBrole = $("#"+formName + "_branchRouterUSB_role").attr("value");
	if(branchRouterUSBrole){
		branchRouterUSBChanged(branchRouterUSBrole);
	}
	
	//new wan usbmodem display
	branchRouterUSBInited();


    var parentEl = parent.document.getElementById('apAnimation');
    if (parentEl) {
    	parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
	    YAHOO.util.Dom.setStyle(parentEl, 'height', '580px');
    } else {
    	parentHeight = 0;
    }

    <s:if test="%{jsonMode == true && vpnGateWayDlg==true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(950, 650);
	 	}
	</s:if>
	
	 if(<s:property value="%{dataSource.enableMDM}"/>){
			document.getElementById(formName + "_dataSource_enableMDM").checked = true;	
			if($("#hiveAp_configmdmId option:last").val() == "-1"){
				$("#hiveAp_configmdmId option[value='-1']").remove();
				$("#hiveAp_configmdmId").prepend("<option value='-1' selected='selected'>None available</option>");
			}else{
				$("#hiveAp_configmdmId option[value='-1']").remove();
			}
		}else{
			document.getElementById(formName + "_dataSource_enableMDM").checked = false;	
			Get("enablemdmselect").style.display="none";
		}
		 if($("#hiveAp_configmdmId option:last").val() == "-1"){
				$("#hiveAp_configmdmId option[value='-1']").remove();
				$("#hiveAp_configmdmId").prepend("<option value='-1' selected='selected'>None available</option>");
		}else{
				$("#hiveAp_configmdmId option[value='-1']").remove();
		}
	initWifiClientMode();
	initWanOrderOnClientSide();
}

function initWifiClientMode(){
 	 var wifi0Value = $("#wifi0strTd").html().trim();
	 if(wifi0Value == "WAN"){
		hm.util.show("wfcmInterfaceInfo");
		document.getElementById("wfcmInterfaceInfo").innerHTML='<td style="padding-left: 20px;" class="noteInfo"><s:text name="info.wificlientmode.wifienabled"><s:param>wifi0</s:param></s:text></font></td>';
		hm.util.hide("wfcmInterfaceNote");
	 }else{
		hm.util.show("wfcmInterfaceNote");
		hm.util.hide("wfcmInterfaceInfo");
	 }
}

function saveVpnGatewayDlg(operation){
	var url =  "<s:url action='hiveAp' includeParams='none' />" +
		"?jsonMode=true"+
		"&vpnGateWayDlg=true"+
		"&ignore="+new Date().getTime();
	document.forms["hiveAp"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("hiveAp"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succUpdateVpnGateway, failure : resultDoNothing, timeout: 60000}, null);
}

var succUpdateVpnGateway = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			Get("errNoteHiveAp").innerHTML = details.errMsg;
			return;
		}else{
			if(details.parentDomID){
				var vpnGateWaySelect = parent.document.getElementById(details.parentDomID);
				if(vpnGateWaySelect){
					if(details.id != null && details.name != null){
						 vpnGateWaySelect.value = details.id;
						 vpnGateWaySelect.options[vpnGateWaySelect.selectedIndex].text = details.name;
				   }
				}

			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		//alert("error");
		return;
	}
}

var resultDoNothing = function(o){
}



var selectUIElement;
function submitAction(operation) { 
	if(validate(operation)) {
		beforeSubmitMSS(operation);
		wanOrderPreCheck();
		if(operation == 'addStaticRoute'
			|| operation == 'removeStaticRouteNone'
			|| operation == 'removeStaticRoute'){
			url =  "<s:url action='hiveAp' includeParams='none' />?operation="+ operation +
			"&hmListType=manageAPGuid"+
			"&staticRouteIpInput="+ document.getElementById(formName+"_staticRouteIpInput").value +
			"&staticRouteMaskInput="+ document.getElementById(formName+"_staticRouteMaskInput").value +
			"&staticRouteGwInput="+ document.getElementById(formName+"_staticRouteGwInput").value +
			"&distributeNet="+ document.getElementById(formName+"_distributeNet").checked +
			"&jsonMode=true"+
			"&ignore="+new Date().getTime();
			if ( operation == 'removeStaticRouteNone'
				|| operation == 'removeStaticRoute') {
				var checkBoxs = document.getElementsByName('routingProfilesStaticRoutesIndices');
				for (var i = 0; i < checkBoxs.length; i++) {
					if (checkBoxs[i].checked ) {
						url = url + "&routingProfilesStaticRoutesIndices="+i
					}
				}
			}
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succAddStaticRoute, failure : resultDoNothing, timeout: 60000}, null);
			return;
		}

		//hiveApGuid define in configGuide2.jsp
		<s:if test="%{jsonMode == true}">
			<s:if test="%{vpnGateWayDlg == true}">
				if("cancelDlg" == operation){
					parent.closeIFrameDialog();
					return;
				}else if ("update2" == operation){
					saveVpnGatewayDlg(operation);
					return;
				}
				if (operation == "newMgtDns" || operation == "editMgtDns"
					|| operation == "newMgtTime" || operation == "editMgtTime"
					|| operation == "newMgtSyslog" || operation == "editMgtSyslog"
					|| operation == "newMgtSnmp" || operation == "editMgtSnmp"
					|| operation == "newScheduler" || operation == "editScheduler"
					|| operation == "newIpTrack" || operation == "editIpTrack"
					|| operation == "newCapwapIp" || operation == "editCapwapIp"
					|| operation == "newCapwapBackupIp" || operation == "editCapwapBackupIp"
					|| operatin =="newSuppCLI" || operation == "editSuppCLI") {
					document.forms[formName].parentIframeOpenFlg.value=true;
				}
			</s:if>
			<s:else>
				if(operation == "newMgtDns" || operation == "editMgtDns"){
					var selectElId;
					var url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
					if(operation == "newMgtDns"){
						selectElId = formName + "_dnsForCVGId";
						url = url + "&parentDomID=" + selectElId;
					} else if(operation == "editMgtDns"){
						url = url + "&dnsForCVGId=" + document.forms[formName].dnsForCVGId.value;
					}
					openIFrameDialog(750, 450, url);
				}else if(operation == "newMgtTime" || operation == "editMgtTime"){
					var selectElId;
					var url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
					if(operation == "newMgtTime"){
						selectElId = formName + "_ntpForCVGId";
						url = url + "&parentDomID=" + selectElId;
					} else if(operation == "editMgtTime"){
						url = url + "&ntpForCVGId=" + document.forms[formName].ntpForCVGId.value;
					}
					openIFrameDialog(650, 550, url);
				}else if(operation == "newMgtSyslog" || operation == "editMgtSyslog"){
					var selectElId;
					var url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
					if(operation == "newMgtSyslog"){
						selectElId = formName + "_syslogForCVGId";
						url = url + "&parentDomID=" + selectElId;
					} else if(operation == "editMgtSyslog"){
						url = url + "&syslogForCVGId=" + document.forms[formName].syslogForCVGId.value;
					}
					openIFrameDialog(850, 550, url);
				}else if(operation == "newMgtSnmp" || operation == "editMgtSnmp"){
					var selectElId;
					var url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
					if(operation == "newMgtSnmp"){
						selectElId = formName + "_snmpForCVGId";
						url = url + "&parentDomID=" + selectElId;
					} else if(operation == "editMgtSnmp"){
						url = url + "&snmpForCVGId=" + document.forms[formName].snmpForCVGId.value;
					}
					openIFrameDialog(850, 550, url);
				}else{
					submitHiveApAction(operation);
				}
				return;
			</s:else>
		</s:if>

		if (operation != 'addIpRoute2' &&
		operation != 'removeIpRoutes2' &&
		operation != 'addStaticRoute2' &&
		operation != 'removeStaticRoutes2' &&
		operation != 'addDynamicRoute2' &&
		operation != 'removeDynamicRoutes2' &&
		operation != 'removeUSBConnection2'&&
		operation != 'addMultipleVlan'&&
		operation != 'removeMultipleVlan' &&
		operation != 'addStaticRoute' &&
		operation != 'removeStaticRouteNone' &&
		operation != 'removeStaticRoute') {
			showProcessing();
		}

		//close create section after apply ssid profiles
		if(operation == 'addStaticRoute2'){
			$("#expanding_static").attr("value", "false");
		}
		if(operation == 'addDynamicRoute2'){
			$("#expanding_dynamic").attr("value", "false");
		}
		if(operation == 'addDynamicIp2'){
			$("#expanding_ip").attr("value", "false");
		}
		if(operation == 'addVirtualConnect'){
			$("#expanding_virtualConnect").attr("value", "false");
		}
		if(operation == 'addMultipleVlan'){
			$("#expanding_vlanid").attr("value", "false");
		}
		if(operation == 'addStaticRoute') {
			$("#expanding_staticRoutes").attr("value", "false")
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('includedNeighbors');
		hm.options.selectAllOptions('excludedNeighbors');
		hm.options.selectAllOptions('dhcpServers');
		hm.options.selectAllOptions('preferredSsids');
		hm.options.selectAllOptions('eth0Maces');
		hm.options.selectAllOptions('eth1Maces');
		hm.options.selectAllOptions('agg0Maces');
		hm.options.selectAllOptions('red0Maces');
		hm.options.selectAllOptions('ethUserProfiles');
		hm.options.selectAllOptions('preferredSsids');

		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
		enableBindInterfaceItems();

		//save style values 
		$("#" + formName + "_dataSource_networkSettingsDisplayStyle").attr("value", $("#networkSettings").css("display"));
		$("#" + formName + "_dataSource_serviceSettingsDisplayStyle").attr("value", $("#serviceSettings").css("display"));
		$("#" + formName + "_dataSource_ssidAllocationDisplayStyle").attr("value", $("#ssidAllocation").css("display"));
		$("#" + formName + "_dataSource_advEthSettingsDisplayStyle").attr("value", $("#ethAdvSettings").css("display"));
		$("#" + formName + "_dataSource_advSettingsDisplayStyle").attr("value", $("#advancedSettings").css("display"));
		$("#" + formName + "_dataSource_credentialsDisplayStyle").attr("value", $("#credentials").css("display"));
		$("#" + formName + "_dataSource_l3RoamingDisplayStyle").attr("value", $("#l3Roaming").css("display"));
		$("#" + formName + "_dataSource_routingDisplayStyle").attr("value", $("#routing").css("display"));
		$("#" + formName + "_dataSource_eth0BridgeAdvDisplayStyle").attr("value", $("#eth0BridgeAdvSettings").css("display"));
		$("#" + formName + "_dataSource_eth1BridgeAdvDisplayStyle").attr("value", $("#eth1BridgeAdvSettings").css("display"));
		$("#" + formName + "_dataSource_agg0BridgeAdvDisplayStyle").attr("value", $("#agg0BridgeAdvSettings").css("display"));
		$("#" + formName + "_dataSource_red0BridgeAdvDisplayStyle").attr("value", $("#red0BridgeAdvSettings").css("display"));
		$("#" + formName + "_dataSource_tempWifi0RadioProfileCreateDisplayStyle").attr("value", $("#wifi0RadioProfileCreateSection").css("display"));
		$("#" + formName + "_dataSource_ethCwpSettingDisplayStyle").attr("value", $("#ethcwpSettings").css("display"));
		$("#" + formName + "_dataSource_staticRoutesDisplayStyle").attr("value", $("#staticRoutes").css("display"));
		$("#" + formName + "_dataSource_cvgMgtServerDisplayStyle").attr("value", $("#cvgMgtServer").css("display"));
		$("#" + formName + "_dataSource_wifiClientModeDisplayStyle").attr("value", $("#wifiClientModeSettings").css("display"));
		$("#" + formName + "_dataSource_mgt0DhcpSettingsStyle").attr("value", $("#mgt0DhcpSettings").css("display"));
		$("#" + formName + "_dataSource_configMdmContentDisplayStyle").attr("value", $("#configMdmContent").css("display"));
	    
		if ("update2"!=operation &&
	    	"cancel"!=operation &&
	    	"clearWifi1RadioProfile"!=operation &&
			"updateWifi1RadioProfile"!=operation &&
			"createWifi1RadioProfile"!=operation &&
			"clearWifi0RadioProfile"!=operation &&
			"updateWifi0RadioProfile"!=operation &&
			"createWifi0RadioProfile"!=operation &&
			"removeVirtualConnect"!=operation &&
			"addVirtualConnect"!=operation &&
			"removeIpRoutes2"!=operation &&
			"addIpRoute2"!=operation &&
			"removeDynamicRoutes2"!=operation &&
			"removeUSBConnection2"!=operation &&
			"addDynamicRoute2"!=operation &&
			"removeStaticRoutes2"!=operation &&
			"addStaticRoute2"!=operation&&
			"addMultipleVlan"!=operation&&
			"removeMultipleVlan"!=operation&&
			"addStaticRoute"!=operation &&
			"removeStaticRouteNone"!=operation &&
			"removeStaticRoute"!=operation) {
 			//document.forms[formName].target = "_parent";
 		}
 		//if ("update"!=operation && "create"!=operation) {
 		//	document.forms[formName].target = "_parent";
 		//}
 		if ("cancel" == operation || "update2" == operation) {
 		    var parentEl = parent.document.getElementById('apAnimation');
 		    if (parentEl) {
 			    YAHOO.util.Dom.setStyle(parentEl, 'height', parentHeight + 'px');
 		    }
 		}
	    document.forms[formName].submit();
	   // document.forms[formName].target = "_self";
	}

	if (operation =="newMstpRegion" || operation =="editMstpRegion") {
		var url = '<s:url action="networkPolicy" includeParams="none"/>'
			+ '?operation='+ operation
			+ '&jsonMode=fasle'
			+ '&ignore=' + new Date().getTime();

		if (operation == 'editMstpRegion'){
			url = url + "&mstpRegionId=" + document.forms[formName].mstpRegionId.value + "&parentDomID=" + formName+ "_mstpRegionId";
		} else if (operation == 'newMstpRegion'){
			url = url + "&parentDomID=" +  formName+ "_mstpRegionId";;
		}
		openIFrameDialog(850, 550, url);
	}
	if(operation == 'editConfigmdmPolicy'||operation == 'newConfigmdmPolicy'){
		var url = '<s:url action="hiveAp" includeParams="none"/>' 
			+ '?operation='+operation
			+ '&jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		
		if (operation == 'editConfigmdmPolicy'){
			url = url + "&configmdmId=" + $("#hiveAp_configmdmId").val();
		}
		selectUIElement= Get(formName + "_configmdmId");
		
		openIFrameDialog(850, 550, url);
	}
		
		
	
	
}

function validateHiveAp2ForJson(operation) {
	if (validate(operation)) {
		hm.options.selectAllOptions('includedNeighbors');
		hm.options.selectAllOptions('excludedNeighbors');
		hm.options.selectAllOptions('dhcpServers');
		hm.options.selectAllOptions('preferredSsids');
		hm.options.selectAllOptions('eth0Maces');
		hm.options.selectAllOptions('eth1Maces');
		hm.options.selectAllOptions('agg0Maces');
		hm.options.selectAllOptions('red0Maces');
		hm.options.selectAllOptions('ethUserProfiles');
		return true;
	}
	return false;
}

function dynamicAddGrid(gridCount){
	if (gridCount == null) {
		return;
	}
	for (var i=0;i<gridCount;i++) {
		var tbl = document.getElementById("tbl_id");
		var row = tbl.insertRow(tbl.rows.length);
		var cell0 = row.insertCell(0);
		cell0.setAttribute("class","listCheck");
		cell0.setAttribute("colspan","4");
		cell0.innerHTML = "&nbsp";

	}
}

function dynamicAddTr(tableName, addList){
	var tbl = document.getElementById(tableName);
	for (i=0;i<addList.length;i++) {
		var trStr = addList[i].split(',');
		var rowIndex = tbl.rows.length;
		var row = tbl.insertRow(rowIndex);
		for(j=0; j<trStr.length; j++){
			var tdStr = trStr[j];
			var cell = row.insertCell(j);
			if(j == 0){
				cell.setAttribute("class","listCheck");
			}else{
				cell.setAttribute("class","list");
				cell.setAttribute("width","200");
			}
			cell.innerHTML = tdStr;
		}
	}
}

function dynamicDelGrid(tableName, gridCount){
	if (gridCount == null) {
		return;
	}
	for (var i=0;i<gridCount;i++) {
		var tbl = document.getElementById(tableName);
		var row = tbl.rows[tbl.rows.length-1];
		tbl.deleteRow(row.rowIndex);
	}
}

var succAddStaticRoute = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.t) {
			var gridCount = 3;
			// delete all macOrOuiIndices
			var table = document.getElementById("tbCVGIpRoute_id");
			for(var i=table.rows.length - 1; i>=0; i--) {
				table.deleteRow(i);
			}

			//add macOrOuiIndices list
			var staticRouteInfoList = details.staticRoute
			dynamicAddTr("tbCVGIpRoute_id", staticRouteInfoList);

			adjustGridCount("cvgStaticRouteTblGridCount", gridCount, details.gridCount);

			if (details.operation == 'removeStaticRoute') {
				showCreateSection('staticRoutes');
			} else if(details.operation == 'addStaticRoute') {
				hideCreateSection('staticRoutes');
			}
		} else {
			hm.util.reportFieldError(Get(formName +'_staticRouteIpInput'), details.errMsg);
			Get(formName +'_staticRouteIpInput').focus();
		}
	}catch(e){
		//alert("error");
		return;
	}
}
var primarCountAdd=0;
var backup1CountAdd=0;
var backup2CountAdd=0;
function validateWanPriority(){
	
		counter("hiveAp_branchRouterEth0_priority");
		
	if($("#eth1strTd").html().trim()=="WAN"){
		counter("hiveAp_branchRouterEth1_priority");
	}
 	if($("#eth2strTd").html().trim()=="WAN"){
		
		counter("hiveAp_branchRouterEth2_priority");
	}
	if($("#eth3strTd").html().trim()=="WAN"){
		counter("hiveAp_branchRouterEth3_priority");
	}
	if($("#eth4strTd").html().trim()=="WAN"){
		counter("hiveAp_branchRouterEth4_priority");
	}
	if($("#usbstrTd").html().trim()=="WAN"){
		counter("hiveAp_branchRouterUSB_priority");
	}
	
	if(primarCountAdd>1 ){
	hm.util.reportFieldError(Get("error_message_eth0andgateway"), '<s:text name="device.config.intereth.priority.warn.primary"/>');
	primarCountAdd=0;
	backup1CountAdd=0;
	backup2CountAdd=0;
		return false;
	}
	if(backup1CountAdd>1 ){
		hm.util.reportFieldError(Get("error_message_eth0andgateway"), '<s:text name="device.config.intereth.priority.warn.backup1"/>');
		primarCountAdd=0;
		backup1CountAdd=0;
		backup2CountAdd=0;
		return false;
	}	
	if(backup2CountAdd>1)
	{
		hm.util.reportFieldError(Get("error_message_eth0andgateway"), '<s:text name="device.config.intereth.priority.warn.backup2"/>');
		primarCountAdd=0;
		backup1CountAdd=0;
		backup2CountAdd=0;	
		return false;
	}
	primarCountAdd=0;
	backup1CountAdd=0;
	backup2CountAdd=0;
return true;

}
function counter(id){
	if($("#"+id).find("option:selected").text()=="Primary"){
		primarCountAdd++;
	}else if($("#"+id).find("option:selected").text()=="Backup1"){
		backup1CountAdd++;
	}else if($("#"+id).find("option:selected").text()=="Backup2"){
		backup2CountAdd++;
	}
}
function validate(operation) {
	if(operation == "<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>" || operation == 'cancel'){
		$("#"+formName + "_dataSource_metricInteval").attr("value", 60);
		$("#"+formName + "_routeMinimun").attr("value", 67);
		$("#"+formName + "_routeMaximun").attr("value", 67);
		$("#"+formName + "_dataSource_nativeVlan").attr("value", 1);
		$("#"+formName + "_dataSource_mgtVlan").attr("value", 1);
		return true;
	}

	if(!validateRouteingProfiles(operation)) {
		return false;
	}

	if(!validateHostName(operation)){
		return false;
	}

	if(!validateConfigTemplate(operation)){
		return false;
	}

	if(!validateMacAddress(operation)){
		return false;
	}

	if(!validateLocation(operation)){
		return false;
	}

	if(!validateNetworkSetting(operation)){
		return false;
	}

	if(!validateMacLearningSetting(operation)){
		return false;
	}

	if(!validateEthCwpSetting(operation)){
		return false;
	}

	if(!validateOperationMode(operation)){
		return false;
	}

	if(!validateEnrolledMdm(operation)){
		return false;
	}
	if(!validateAllowedVlan(operation)){
		return false;
	}
	if(!validateMultiNativeVlan(operation)){
		return false;
	}
	if(!validateSpeedAndDuplex(operation)){
		return false;
	}
	if(!validateUploadAndDownload()){
		return false;
	}
// 	if(!validatePriority()){
// 		return false;
// 	}

	if(!validateStaticIpandGateway(operation)){
		return false;
	}

	if(!validateNativeVlan(operation)){
		return false;
	}

	if(!validateRouterInterval(operation)){
		return false;
	}

	if(!validateMaxPowerSource(operation)){
		return false;
	}

	if(!validateMgtVlan(operation)){
		return false;
	}

	if(!validateRadioProfile(operation)){
		return false;
	}

	if(!validateStaticRoute(operation)){
		return false;
	}

	if(!validateIntNetwork(operation)){
		return false;
	}

	if(!validateBrStaticRouting(operation)){
		return false;
	}

	if(!validateDynamicRoute(operation)){
		return false;
	}

	if(!validateIpRoute(operation)){
		return false;
	}
	if(!validateMultipleVlan(operation)){
		return false;
	}
	if(!validateMultipleVlanId(operation)){
		return false;
	}
	if(!validateVirtualConnect(operation))
	{
		return false;
	}

	if(!validateNeighborMac(operation)){
		return false;
	}

	if(! validateDestinationMac(operation)){
		return false;
	}
	if(! validateVpnGatewaySetting(operation)){
		return false;
	}
	if(! validateBrRouterSetting(operation)){
		return false;
	}

	<s:if test="%{!easyMode}">
	if(!validateCfgUsernameAndPassword(operation)){
		return false;
	}

	if(!validateReadOnlyCfgUsernameAndPassword(operation)){
		return false;
	}

	if(!checkUserDupleWithReadOnlyUser(operation)){
		return false;
	}
	</s:if>

	if(!validateDtlsSettings(operation)){
		return false;
	}

	if(!validateL3Neighbor(operation)){
		return false;
	}

	if(!validateRADIUSServer(operation)){
		return false;
	}

	<s:if test="%{!easyMode}">
	if(!validateRADIUSProxy(operation)){
		return false;
	}

	if(!validateRADIUSServieAndProxy(operation)) {
		return false;
	}
	</s:if>

	if(!validateCustomizedNasIdentifier(operation)) {
		return false;
	}
	if(!validateVPNServer(operation)){
		return false;
	}

//	if(!validateVPNClient(operation)){
//		return false;
//	}

	if(!validateEthSpeed(operation)) {
		return false;
	}

//	if(!validateInterfaceRole(operation)){
//		return false;
//	}

	if(!validateCapwapIp(operation)){
		return false;
	}

	if(!validateWifi0Creation(operation)){
		return false;
	}

	if(!validateWifi1Creation(operation)){
		return false;
	}

	if(!validateBrForMonitorMSS(operation)){
		return false;
	}

	if(!validateCvgForMonitorMSS(operation)){
		return false;
	}

	if(!validateBonjourGatewayConfig(operation)){
		return false;
	}

	if(!validateWifiClientMode(operation)){
		return false;
	}
	
	if(!validateDynamicRouting(operation)){
		return false;
	}

 	if(!validatePPPoe(operation)){
 		return false;
 	}
	
	
 	if(!validateWanOrder(operation)){
 		return false;
 	}
 	
 	if(!validateBRAdvertiseRoute(operation)){
 		return false;
 	}
 	
 	<s:if test="%{txRetryShowFlag}">
 	if(!validateTxRetrtRate(operation)){
 		return false;
 	}
 	</s:if>

	if(operation == "editTemplate"){
		var value = hm.util.validateListSelection(formName + "_configTemplate");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].configTemplate.value = value;
		}
	}
	if(operation == "editCapwapIp"){
		var value = hm.util.validateListSelection("capwapSelect");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].capwapIp.value = value;
		}
	}
	if(operation == "editCapwapBackupIp"){
		var value = hm.util.validateListSelection("capwapBackupSelect");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].capwapBackupIp.value = value;
		}
	}
	if(operation == "editRadius"){
		var value = hm.util.validateListSelection(formName + "_radiusServer");
		if(value < 0){
			return false;
		}else{
			<s:if test="%{jsonMode == false}">
				document.forms[formName].radiusServer.value = value;
			</s:if>
			<s:else>
				Get(formName + "_radiusServer").value = value;
			</s:else>
		}
	}
	if(operation == "editRadiusProxy"){
		var value = hm.util.validateListSelection(formName + "_radiusProxy");
		if(value < 0){
			return false;
		}else{
			<s:if test="%{jsonMode == false}">
				document.forms[formName].radiusProxy.value = value;
			</s:if>
			<s:else>
				Get(formName + "_radiusProxy").value = value;
			</s:else>
		}
	}
	if(operation == "editRoutingPbrPolicy"){
		var value = hm.util.validateListSelection(formName + "_routingPolicyId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].routingPolicyId.value = value;
		}
	}

	if(operation == "editVpn"){
		var value = hm.util.validateListSelection(formName + "_vpnServer");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].vpnServer.value = value;
		}
	}
	if(operation == "editDhcpServer"){
		var value = hm.util.validateOptionTransferSelection("dhcpServers");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].dhcpServer.value = value;
		}
	}
    if(operation == "editWifiClientPreferredSsid"){
        var value = hm.util.validateOptionTransferSelection("preferredSsids");
        if(value < 0){
            return false;
        }else{
            document.forms[formName].preferredSsid.value = value;
        }
    }
	if(operation == "editScheduler"){
		var value = hm.util.validateListSelection(formName + "_scheduler");
		if(value < 0){
			return false;
		}else{
			document.getElementById(formName+"_scheduler").value = value;
		}
	}
 	if(operation == "editPPPoE"){
		var value = hm.util.validateListSelection("eth0_pppoeAuthProfile");
		if(value < 0){
			return false;
		}else{
			document.getElementById("eth0_pppoeAuthProfile").value = value;
		}
	} 
	if(operation == "editIpTrack"){
		var value = hm.util.validateListSelection(formName + "_vpnIpTrackId");
		if(value < 0){
			return false;
		}else{
			document.getElementById(formName+"_vpnIpTrackId").value = value;
		}
	}
	if(operation == "editUserProfileEth0"){
		var value = hm.util.validateListSelection(formName + "_userProfileEth0");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].userProfileEth0.value = value;
		}
	}
	if(operation == "editUserProfileEth1"){
		var value = hm.util.validateListSelection(formName + "_userProfileEth1");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].userProfileEth1.value = value;
		}
	}
	if(operation == "editUserProfileAgg0"){
		var value = hm.util.validateListSelection(formName + "_userProfileAgg0");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].userProfileAgg0.value = value;
		}
	}
	if(operation == "editUserProfileRed0"){
		var value = hm.util.validateListSelection(formName + "_userProfileRed0");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].userProfileRed0.value = value;
		}
	}
	if(operation == "editMacAddressEth0"){
		var value = hm.util.validateOptionTransferSelection("eth0Maces");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].learningMacId.value = value;
		}
	}
	if(operation == "editMacAddressEth1"){
		var value = hm.util.validateOptionTransferSelection("eth1Maces");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].learningMacId.value = value;
		}
	}
	if(operation == "editMacAddressAgg0"){
		var value = hm.util.validateOptionTransferSelection("agg0Maces");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].learningMacId.value = value;
		}
	}
	if(operation == "editMacAddressRed0"){
		var value = hm.util.validateOptionTransferSelection("red0Maces");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].learningMacId.value = value;
		}
	}
	if(operation == "editEthCwpUserprofile"){
		var value = hm.util.validateOptionTransferSelection("ethUserProfiles");
		if(value < 0){
			return false;
		}else{
			//document.forms[formName].ethDefaultAuthUserprofile.value = value;
			document.forms[formName].ethUserProfileId.value = value;
		}
	}
	if(operation == "editEthCwpDefaultAuthUserProfile"){
		var value = hm.util.validateListSelection(formName + "_ethDefaultAuthUserprofile");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ethDefaultAuthUserprofile.value = value;
		}
	}
	if(operation == "editEthCwpDefaultRegUserProfile"){
		var value = hm.util.validateListSelection(formName + "_ethDefaultRegUserprofile");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ethDefaultRegUserprofile.value = value;
		}
	}
	if(operation == "editEthCwpCwpProfile"){
		var value = hm.util.validateListSelection(formName + "_cwpProfile");
		if(value < 0){
			return false;
		} else{
			document.forms[formName].cwpProfile.value = value;
		 }
	}
	if(operation == "editEthCwpRadiusClient"){
		var value = hm.util.validateListSelection(formName + "_ethCwpRadiusClient");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ethCwpRadiusClient.value = value;
		}
	}
	if(operation == "editSuppCLI"){
		var value = hm.util.validateListSelection(formName + "_supplementalCLIId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].supplementalCLIId.value = value;
		}
	}
	return true;
}

function validateEnrolledMdm(operation){
	if(operation == "editConfigmdmPolicy"
			|| operation == "update2"){
		if($("#hiveAp_dataSource_enableMDM").attr("checked") == "checked"
			&& $("#hiveAp_configmdmId").val() == "-1"){
		hm.util.reportFieldError(
				document.getElementById("hiveAp_configmdmId"),
				'<s:text name="warn.port.access.mdm.invalid.message"/>');
		$("#hiveAp_configmdmId").focus();
		return false;
		}
	}
	return true;
}
function validateSubnet(staticIpId,gatewayId,errorMessageId){
	var ipAddress=$("#"+staticIpId).val();
	var ipGateway=$("#"+gatewayId).val();
	var tempPosition = ipAddress.indexOf('/');
	var ipStr=ipAddress.substring(0,tempPosition);
	var maskInt=ipAddress.substring(tempPosition+1);
	var maskStr = hm.util.intToStringNetMask(maskInt);
	var message = hm.util.validateIpSubnet(ipStr,"Static IP/Netmask Address",ipGateway,"Default Gateway",maskStr);
	if(message!=null && message!=""){
		hm.util.reportFieldError(Get(errorMessageId),message);
		document.getElementById(gatewayId).focus();
		return false;
	}
	return true;
} 

function validateWifiClientMode(operation){
 	if((operation == 'create2' || operation == 'update2') && Get('deviceWifiClientModeSettings').style.display!="none"){
		var value = document.getElementById("preferredSsids").options;
		if(value.length > 0 && value.length <= PREFERRED_SSID_MAX_NUMBER){
			 if($("#wifi0strTd").html().trim() != 'WAN'){
				 hm.util.reportFieldError(Get('wifiClientErrorTip'), '<s:text name="error.wificlientmode.wifiwanneed"/>');
				 if(Get('networkSettings').style.display=="none"){
					 alternateFoldingContent('networkSettings');
				 }
				 document.getElementById(formName + "_dataSource_radioConfigType6").focus();
				 return false;
			 }
			 return true;
		}else if(value.length > PREFERRED_SSID_MAX_NUMBER){
			if(Get('wifiClientModeSettings').style.display=="none"){
				 alternateFoldingContent('wifiClientModeSettings');
			}
			hm.util.reportFieldError(Get('wfcmSSIDNote'), '<s:text name="error.wificlientmode.maxpreferrednum"/>');
			preferredSsids.focus();
			return false;
		}
	} 
	return true;
}

function validateDynamicRouting(operation){
	if((operation == 'create2' || operation == 'update2') && Get('vpnGatewayIntStyle').style.display!="none"){
		var typeFlag = document.getElementById("typeFlag");
		if(typeFlag.value == 2){
			var wanChecked = document.getElementById("enableRouteWan").checked;
			var lanChecked = document.getElementById("enableRouteLan").checked;
			if(wanChecked == false && lanChecked == false){
				hm.util.reportFieldError(Get('dynamicRoutingNote'), '<s:text name="error.dynamicRouting.ospf.ethcheck"/>');
				typeFlag.focus();
				return false;
			}
		}
	}
	
	return true;
}

function validateRadioProfile(operation){
	if(operation == 'create2' || operation == 'update2'){
		var inputElement1 = document.getElementById(formName + "_wifi0RadioProfile");
		var inputElement2 = document.getElementById(formName + "_wifi1RadioProfile");

		if(inputElement1 && inputElement1.value <= 0){
		    hm.util.reportFieldError(inputElement1, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
		    inputElement1.focus();
		    return false;
		}

		if(inputElement2 && inputElement2.value <= 0){
		    hm.util.reportFieldError(inputElement2, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
		    inputElement2.focus();
		    return false;
		}
	}

	return true;
}

function validateConfigTemplate(operation){
	if(operation == 'create2' || operation == 'update2'){
		var inputElement = document.getElementById(formName + "_configTemplate");
		if(inputElement.value <= 0){
		    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.template" /></s:param></s:text>');
		    inputElement.focus();
		    return false;
		}
	}
	return true;
}

function validateLocation(operation){
	if(operation == 'create2' || operation == 'update2'){
		var inputElement = document.getElementById(formName + "_dataSource_location");
		if(inputElement.value.length > 0){
			var message = hm.util.validateStringWithBlank(inputElement.value, '<s:text name="hiveAp.location" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(inputElement, message);
	        	inputElement.focus();
	        	return false;
	    	}
		    if (inputElement.value.indexOf('@') > -1) {
		      hm.util.reportFieldError(inputElement, "<s:text name='error.value.internal.used'><s:param>'@'</s:param></s:text>");
		      inputElement.focus();
		      return false;
		    }
	    }
	}
	return true;
}

function validateRouteingProfiles(operation) {
	if(!document.getElementById('enableDynamicRouting')){
		return true;
	}

	var enableDynamicRouting = document.getElementById('enableDynamicRouting').checked;
	if (operation == 'create2' || operation == 'update2') {
		if (!enableDynamicRouting) {
			return true;
		}
		var useMD5 = document.getElementById("useMD5").checked;
		if (useMD5) {
			var chkMd5 = document.getElementById("chkToggleDisplay_md5").checked;
			var md5Password;
			if (chkMd5) {
				md5Password = document.getElementById("md5Password");
			} else {
				md5Password = document.getElementById("md5Password_text");
			}

			if (md5Password.value.length == 0) {
				hm.util.reportFieldError(md5Password, '<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.password" /></s:param></s:text>');
				md5Password.focus();
	            return false;
			} else if(md5Password.value.length>32){
				hm.util.reportFieldError(md5Password, '<s:text name="error.keyValueRange"><s:param><s:text name="config.routingProfiles.password" /></s:param><s:param><s:text name="config.routingProfiles.password.range" /></s:param></s:text>');
				md5Password.focus();
				return false;
			}
		}
		var checkValue = document.getElementById("typeFlag").value;
		if (checkValue == 2) {
			var area = document.getElementById("area");
			var ospfRouteId = document.getElementById("routerId");
			if (!checkAera(false,area,area,'<s:text name="config.routingProfiles.area"/>')
					||!checkAera(false,ospfRouteId,ospfRouteId,'<s:text name="config.routingProfiles.routerId"/>')) {
				return false;
			}
		} else if(checkValue == 3) {
			var autoSysNm = document.getElementById("autonmousSysNm");
			if (autoSysNm.value.length == 0) {
				hm.util.reportFieldError(autoSysNm, '<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.autoSysNm" /></s:param></s:text>');
				autoSysNm.focus();
	            return false;
			} else if (!isValidIntRange(autoSysNm.value,1,65535)) {
				hm.util.reportFieldError(autoSysNm, '<s:text name="error.keyValueRange"><s:param><s:text name="config.routingProfiles.autoSysNm" /></s:param><s:param><s:text name="config.routingProfiles.autoSysNm.range" /></s:param></s:text>');
				autoSysNm.focus();
				return false;
			}
			var keepalive = document.getElementById("keepalive");
			if (!isValidIntRange(keepalive.value,0,21845)) {
				hm.util.reportFieldError(keepalive, '<s:text name="error.keyValueRange"><s:param><s:text name="config.routingProfiles.keepalive" /></s:param><s:param><s:text name="config.routingProfiles.keepalive.range" /></s:param></s:text>');
				keepalive.focus();
				return false;
			}
			var bgpRouterId = document.getElementById("bgpRouterId");
			if (!checkAera(false,bgpRouterId,bgpRouterId,'<s:text name="config.routingProfiles.routerId"/>')) {
				return false;
			}
			var bgpNeighbors = document.getElementById("bgpNeighbors");
			if(YAHOO.util.Dom.hasClass(bgpNeighbors, hintClassName)) {
	    		hm.util.reportFieldError(bgpNeighbors,
	    				'<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.bgpNeighbors"/></s:param></s:text>');
	    		bgpNeighbors.focus();
	        	return false;
			} else {
				if(bgpNeighbors.value.length == 0) {
		    		hm.util.reportFieldError(bgpNeighbors,
		    				'<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.bgpNeighbors"/></s:param></s:text>');
		    		bgpNeighbors.focus();
		        	return false;
				}
			}

			var neighbors = bgpNeighbors.value.split(new RegExp("\n"));
			for (i=0;i<neighbors.length;i++) {
				if (!hm.util.validateIpAddress(neighbors[i])) {
					hm.util.reportFieldError(bgpNeighbors, '<s:text name="error.formatInvalid"><s:param><s:text name="config.routingProfiles.bgpNeighbors"/></s:param></s:text>');
					bgpNeighbors.focus();
					return false;
				}
			}
		}
	}
	var displayErrorObj = document.getElementById("checkAllRoute");
	if (operation == 'addStaticRoute') {
		var destinationIp = document.getElementById(formName + "_staticRouteIpInput");
		var gateway = document.getElementById(formName + "_staticRouteGwInput");
		var netmask = document.getElementById(formName + "_staticRouteMaskInput");

		//fix bug15031
		if (netmask.value == '255.255.255.255') {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="error.formatInvalid"><s:param><s:text name="config.routingProfiles.netmask" /></s:param></s:text>');
			return false;
		}

		if (!checkIpAddress(true,displayErrorObj,destinationIp,'<s:text name="config.routingProfiles.ip" />')
			|| !checkIpAddress(true,displayErrorObj,netmask,'<s:text name="config.routingProfiles.netmask" />')
			|| !checkIpAddress(true,displayErrorObj,gateway,'<s:text name="config.routingProfiles.gateway" />')) {
			return false;
		}

		if(!(validateSRGatewayByEth0(gateway.value,displayErrorObj) == 0 || validateSRGatewayByEth1(gateway.value,displayErrorObj) == 0)){
			if(validateSRGatewayByEth0(gateway.value,displayErrorObj) == 2 || validateSRGatewayByEth1(gateway.value,displayErrorObj) == 2){
				hm.util.reportFieldError(displayErrorObj, '<s:text name="error.hiveap.cvg.static.route.gateway" />.');
			}
			return false;
		}
	}

    if (operation == 'removeStaticRoute' || operation == 'removeStaticRouteNone') {

		var cbs = document.getElementsByName('routingProfilesStaticRoutesIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.network.object.dhcp.server.ip.pools" /></s:param></s:text>');
			return false;
		}
	}
	return true;
}

function validateSRGatewayByEth0(gateway,displayErrorObj,index){
	var wanIpElement = document.getElementById(formName + "_wanInterface_ipAndNetmask");
	if(wanIpElement.value.length == 0){
		hm.util.reportFieldError(wanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
        wanIpElement.focus();
        return 1;
	}
	if(wanIpElement.value.indexOf("/") == -1){
		hm.util.reportFieldError(wanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
        wanIpElement.focus();
        return 1;
	}
	var ipStr = wanIpElement.value.substring(0, wanIpElement.value.indexOf("/"));
	var maskInt = wanIpElement.value.substring(wanIpElement.value.indexOf("/")+1);
	if(maskInt > 32){
		hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
		wanIpElement.focus();
		return 1;
	}
	var maskStr = hm.util.intToStringNetMask(maskInt);
	if (! hm.util.validateIpAddress(ipStr)) {
		hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
		wanIpElement.focus();
		return 1;
	}
	if(! hm.util.validateMask(maskStr)){
		hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
		wanIpElement.focus();
		return 1;
	}

	//check if they are in the same subnet
	var message = hm.util.validateIpSubnet(ipStr, '<s:text name="hiveAp.ipAddress" />', gateway, '<s:text name="hiveAp.gateway" />', maskStr);
	if(null != message ){
		return 2;
	}

	return 0;
}

function validateSRGatewayByEth1(gateway,displayErrorObj,index){

	var lanIpElement = document.getElementById(formName + "_lanInterface_ipAndNetmask");
	if(lanIpElement.value.length > 0){
		if(lanIpElement.value.indexOf("/") == -1){
			hm.util.reportFieldError(lanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
            lanIpElement.focus();
            return 1;
		}
		var lanipStr = lanIpElement.value.substring(0, lanIpElement.value.indexOf("/"));
		var lanmaskInt = lanIpElement.value.substring(lanIpElement.value.indexOf("/")+1);
		if(lanmaskInt > 32){
			hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			lanIpElement.focus();
			return 1;
		}
		var lanmaskStr = hm.util.intToStringNetMask(lanmaskInt);
		if (! hm.util.validateIpAddress(lanipStr)) {
			hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
			lanIpElement.focus();
			return 1;
		}
		if(! hm.util.validateMask(lanmaskStr)){
			hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			lanIpElement.focus();
			return 1;
		}

		//check if they are in the same subnet
		var message = hm.util.validateIpSubnet(lanipStr, '<s:text name="hiveAp.ipAddress" />', gateway, '<s:text name="hiveAp.gateway" />', lanmaskStr);
		if(null != message){
			return 2;
		}

	} else {
		return 1;
	}
	return 0;
}

//check string range
function isValidStrRange(strName,start,end)
{
	var strValue = strName.valueOf();

	if ( strValue.length >=start && strValue.length <= end )
	{
		return true;
	}

	return false;
}

//check int range
function isValidIntRange(intName,start,end)
{
	var intValue = intName.valueOf();

	if ( intValue >=start && intValue <= end )
	{
		return true;
	}

	return false;
}

function checkIpAddress(isRequired,focus, ip, title, fn) {
	if (ip.value.length == 0) {
		if (isRequired) {
	        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
			if(null != fn){
				fn();
			}
	        ip.focus();
	        return false;
		}

    } else if (!hm.util.validateIpAddress(ip.value)) {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		ip.focus();
		return false;
	}
	return true;
}

function checkAera(isRequired,focus, ip, title, fn) {
	if (ip.value.length == 0) {
		if (isRequired) {
	        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
			if(null != fn){
				fn();
			}
	        ip.focus();
	        return false;
		}

    } else if (!validateAera(ip.value)) {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		ip.focus();
		return false;
	}
	return true;
}

function validateAera(value){
	if (value.length == 0) {
		return false;
	}
    var tokens = hm.util.trim(value).split(".");
	if (tokens.length != 4) {
		return false;
	}

	for (var k=0; k<4; k++) {
		if (isNaN(tokens[k])|| (tokens[k].length ==0) || (tokens[k].length >3) ||tokens[k]>255 || tokens[k]<0) {
			return false;
		}
		if ((tokens[k].indexOf(" ")>-1)) {
			return false;
		}
	}
	return true;
}

function validateHostName(operation){
	if(operation == 'create2' || operation == 'update2'){
		var inputElement = document.getElementById(formName + "_dataSource_hostName");
		if(!inputElement){
			return true;
		}
		var message = hm.util.validateName(inputElement.value, '<s:text name="hiveAp.hostName" />');
    	if (message != null) {
    		hm.util.reportFieldError(inputElement, message);
        	inputElement.focus();
        	return false;
    	}
	}
	return true;
}

function checkMacAddress(el, label, tabIndex){
	if(el.value.length == 0){
        hm.util.reportFieldError(el, '<s:text name="error.requiredField"><s:param>'+label+'</s:param></s:text>');
        el.focus();
        return false;
	} else if (!hm.util.validateMacAddress(el.value, 12)) {
		hm.util.reportFieldError(el, '<s:text name="error.formatInvalid"><s:param>'+label+'</s:param></s:text>');
		el.focus();
		return false;
	}
	return true;
}

function validateMacAddress(operation){
	if(operation == 'create2'){
		var macAddressElement = document.getElementById(formName + "_dataSource_macAddress");
		if(!checkMacAddress(macAddressElement, '<s:text name="hiveAp.macaddress" />', 0)){
			return false;
		}
	}
	return true;
}

function validateDtlsSettings(operation){
	if(operation == 'create2' || operation == 'update2'){
		var dtlsBoxElement = document.getElementById("changePassPhrase");
		var newDtlsElement;
		var confirmDtlsElement;
		if (document.getElementById("chkToggleDisplay_2").checked){
			newDtlsElement = document.getElementById("newDtls");
			confirmDtlsElement = document.getElementById("confirmDtls");
		}else{
			newDtlsElement = document.getElementById("newDtls_text");
			confirmDtlsElement = document.getElementById("confirmDtls_text");
		}
		if(dtlsBoxElement.checked){
			var message = hm.util.validatePassword(newDtlsElement.value, '<s:text name="hiveAp.dtls.newPassPhrase" />');
	    	if (message != null) {
	    		showCredentialsContent();
	    		hm.util.reportFieldError(newDtlsElement, message);
	        	newDtlsElement.focus();
	        	return false;
	    	}
			message = hm.util.validatePassword(confirmDtlsElement.value, '<s:text name="hiveAp.dtls.confirmPassPhrase" />');
	    	if (message != null) {
	    		showCredentialsContent();
	    		hm.util.reportFieldError(confirmDtlsElement, message);
	        	confirmDtlsElement.focus();
	        	return false;
	    	}

			if (newDtlsElement.value.length < 16) {
				showCredentialsContent();
			    hm.util.reportFieldError(newDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    newDtlsElement.focus();
			    return false;
			}

			if (confirmDtlsElement.value.length < 16) {
				showCredentialsContent();
			    hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    confirmDtlsElement.focus();
			    return false;
			}

			if (newDtlsElement.value != confirmDtlsElement.value) {
				showCredentialsContent();
			 	hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.notEqual"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param></s:text>');
				newDtlsElement.focus();
				return false;
			}
		}
	}
	return true;
}

function validateOptionNewUsernameAndPassword(operation, name, password, confirm){
	if(operation == 'create2' || operation == 'update2'){
		if (name.value.length > 0 || password.value.length > 0 || confirm.value.length > 0){
			var message = hm.util.validateUsername(name.value, '<s:text name="hiveAp.newUser" />');
	    	if (message != null) {
	    		showCredentialsContent();
	    		hm.util.reportFieldError(name, message);
	        	name.focus();
	        	return false;
	    	}

			if(name.value.length < 3){
				showCredentialsContent();
			    hm.util.reportFieldError(name, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.newUser" /></s:param><s:param><s:text name="hiveAp.currentUserRange" /></s:param></s:text>');
			    name.focus();
			    return false;
			}

			if (!hm.util.validateUserNewPasswordFormat(password, confirm, '<s:text name="hiveAp.newPassword" />',
	    			'<s:text name="hiveAp.newConfirmPassword" />', 8, '<s:text name="hiveAp.currentPasswordRange" />', name.value)) {
				showCredentialsContent();
				return false;
			}
			
			 if (name.value == 'root'
			 	|| name.value == 'daemon'
			 	|| name.value == 'bin'
			 	|| name.value == 'sys'
			 	|| name.value == 'sync'
			 	|| name.value == 'mail'
			 	|| name.value == 'proxy'
			 	|| name.value == 'sshd'
			 	|| name.value == '_radius'){
				 showCredentialsContent();
			    hm.util.reportFieldError(name, "<s:text name='error.value.internal.used'><s:param><s:text name='hiveAp.newUser' /></s:param></s:text>");
			    name.focus();
			    return false;
			 }
		}
	}
	return true;
}

function validateCfgUsernameAndPassword(operation){
	var usernameElement = document.getElementById(formName + "_dataSource_cfgAdminUser");
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay").checked){
       passwordElement = document.getElementById("cfgAdminPassword");
       confirmElement = document.getElementById("cfCfgAdminPassword");
    }else{
       passwordElement = document.getElementById("cfgAdminPassword_text");
       confirmElement = document.getElementById("cfCfgAdminPassword_text");
    }
	return validateOptionNewUsernameAndPassword(operation,usernameElement,passwordElement,confirmElement);
}

function validateReadOnlyCfgUsernameAndPassword(operation){
	var usernameElement = document.getElementById(formName + "_dataSource_cfgReadOnlyUser");
	var passwordElement;
	var confirmElement;
	if (document.getElementById("chkToggleDisplay_1").checked){
		passwordElement = document.getElementById("cfgReadOnlyPassword");
		confirmElement = document.getElementById("confirmCfgReadOnlyPassword");
	}else{
		passwordElement = document.getElementById("cfgReadOnlyPassword_text");
		confirmElement = document.getElementById("confirmCfgReadOnlyPassword_text");
	}
	return validateOptionNewUsernameAndPassword(operation,usernameElement,passwordElement,confirmElement);
}

function checkUserDupleWithReadOnlyUser(operation){
	if(operation == 'create2' || operation == 'update2'){
		var usernameElement = document.getElementById(formName + "_dataSource_cfgAdminUser");
		var readOnlyElement = document.getElementById(formName + "_dataSource_cfgReadOnlyUser");
		if(usernameElement.value.length > 0 && readOnlyElement.value.length > 0){
			if(readOnlyElement.value == usernameElement.value){
				showCredentialsContent();
	            hm.util.reportFieldError(readOnlyElement, "<s:text name='error.hiveAp.adminName'></s:text>");
	            readOnlyElement.focus();
	            return false;
			}
		}
	}
	return true;
}

function validateNetworkSetting(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') && (deviceType == DEVICE_TYPE_HIVEAP || deviceType == DEVICE_TYPE_SWITCH)){

		//var dhcpElement = document.getElementById(formName + "_dataSource_dhcp");
		var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
		var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
		var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
		//var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
		if(preNetworkType == USE_STATIC_IP){// Static ip
			if (ipAddressElement.value.length == 0) {
				showNetworkSettingsContent();
	            hm.util.reportFieldError(ipAddressElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
	            ipAddressElement.focus();
	            return false;
    		}
    		if (netmaskElement.value.length == 0) {
    			showNetworkSettingsContent();
	            hm.util.reportFieldError(netmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
	            netmaskElement.focus();
	            return false;
    		}
    		
			if(! hm.util.validateMask(netmaskElement.value)){
				showNetworkSettingsContent();
				hm.util.reportFieldError(netmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
				netmaskElement.focus();
				return false;
			}
			if (! hm.util.validateIpAddressWithMask(ipAddressElement.value, netmaskElement.value)) {
    			showNetworkSettingsContent();
				hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
				ipAddressElement.focus();
				return false;
			}
			if (gatewayElement.value.length != 0) {
				if (! hm.util.validateIpAddressWithMask(gatewayElement.value, netmaskElement.value)) {
					showNetworkSettingsContent();
					hm.util.reportFieldError(gatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
					gatewayElement.focus();
					return false;
				}
				//check if they are in the same subnet
				var message = hm.util.validateIpSubnet(ipAddressElement.value, '<s:text name="hiveAp.ipAddress" />', gatewayElement.value, '<s:text name="hiveAp.gateway" />', netmaskElement.value);
				if(null != message){
					showNetworkSettingsContent();
					hm.util.reportFieldError(ipAddressElement, message);
					return false;
				}
			}
		} else { // DHCP ip
			//check dhcp timeout value;
			var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
			if (dhcpTimeoutEl.value.length == 0) {
				hm.util.reportFieldError(dhcpTimeoutEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.dhcpTimeout" /></s:param></s:text>');
				showNetworkSettingsContent();
				dhcpTimeoutEl.focus();
				return false;
			}
			var message = hm.util.validateIntegerRange(dhcpTimeoutEl.value, '<s:text name="hiveAp.dhcpTimeout" />',
			                                           <s:property value="0" />,
			                                           <s:property value="3600" />);
			if (message != null) {
				hm.util.reportFieldError(dhcpTimeoutEl, message);
				showNetworkSettingsContent();
				dhcpTimeoutEl.focus();
				return false;
			}

			if(preNetworkType == USE_DHCP_FALLBACK){

				//when ip address is not blank, check for ipAddress, netmask, gateway
				if (ipAddressElement.value.length != 0) {
		    		if (netmaskElement.value.length == 0) {
		    			showNetworkSettingsContent();
			            hm.util.reportFieldError(netmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			            netmaskElement.focus();
			            return false;
		    		}
					if(! hm.util.validateMask(netmaskElement.value)){
						showNetworkSettingsContent();
						hm.util.reportFieldError(netmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
						netmaskElement.focus();
						return false;
					}
					if (! hm.util.validateIpAddressWithMask(ipAddressElement.value, netmaskElement.value)) {
		    			showNetworkSettingsContent();
						hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
						ipAddressElement.focus();
						return false;
					}
					if (gatewayElement.value.length != 0) {
						if (! hm.util.validateIpAddressWithMask(gatewayElement.value, netmaskElement.value)) {
							showNetworkSettingsContent();
							hm.util.reportFieldError(gatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
							gatewayElement.focus();
							return false;
						}
						//check if they are in the same subnet
						var message = hm.util.validateIpSubnet(ipAddressElement.value, '<s:text name="hiveAp.ipAddress" />', gatewayElement.value, '<s:text name="hiveAp.gateway" />', netmaskElement.value);
						if(null != message){
							showNetworkSettingsContent();
							hm.util.reportFieldError(ipAddressElement, message);
							return false;
						}
					}
				}
			}else{

				//when ip prefix is not blank, check for ip prefix format
				if (ipAddressElement.value.length != 0) {
		    		if (!hm.util.validateIpAddress(ipAddressElement.value)) {
		    			showNetworkSettingsContent();
						hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.default.ipAddress" /></s:param></s:text>');
						ipAddressElement.focus();
						return false;
					}
					if(netmaskElement.value.length != 0
							&& netmaskElement.value.trim() != "255.0.0.0"
							&& netmaskElement.value.trim() != "255.255.0.0"
							&& netmaskElement.value.trim() != "255.255.255.0"){
						showNetworkSettingsContent();
						hm.util.reportFieldError(netmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
						netmaskElement.focus();
						return false;
					}
				}
			}
		}
	}
	return true;
}

function validateOperationMode(operation){
	if(operation == 'create2' || operation == 'update2'){
		//var radioOptions = document.forms[formName]['dataSource.radioConfigType'];
		var radioOptions = document.getElementsByName('dataSource.radioConfigType');
		var radioOption;
		for(var i=0; i<radioOptions.length; i++){
			if(radioOptions[i].checked){
				radioOption = radioOptions[i];
				break;
			}
		}
		if(radioOption){
			switch(parseInt(radioOption.value)){
			case RADIO_MODE_ACCESS_ALL:
				if(!isBackhaulPortAvaiable()){
					return false;
				}
			//case RADIO_MODE_ACCESS_ONE:
			//case RADIO_MODE_BRIDGE:
			case RADIO_MODE_CUSTOMIZE:
				var wifi0OperationModeEl, wifi1OperationModeEl;
				if(document.getElementById(formName + "_dataSource_deviceType").value == DEVICE_TYPE_BRANCH_ROUTER){
					wifi0OperationModeEl = document.getElementById("wifi0ModeBR");
					wifi1OperationModeEl = document.getElementById("wifi1ModeBR");
				}else{
					wifi0OperationModeEl = document.getElementById(formName + "_dataSource_wifi0_operationMode");
					wifi1OperationModeEl = document.getElementById(formName + "_dataSource_wifi1_operationMode");
				}
				if(isWifi1Available()){
					if(wifi0OperationModeEl.value != OPERATION_MODE_BACKHAUL && wifi1OperationModeEl.value != OPERATION_MODE_BACKHAUL &&
						wifi0OperationModeEl.value != OPERATION_MODE_DUAL && wifi1OperationModeEl.value != OPERATION_MODE_DUAL){
						if(!isBackhaulPortAvaiable()){
							return false;
						}
					}
				}else{
					if(wifi0OperationModeEl.value != OPERATION_MODE_BACKHAUL &&
						wifi0OperationModeEl.value != OPERATION_MODE_DUAL){
						if(!isBackhaulPortAvaiable()){
							return false;
						}
					}
				}
			}
		}
	}
	return true;
}

function isBackhaulPortAvaiable(){
	if(!isEth1Available()){// just check eth0
		var eth0OperationModeEl = document.getElementById(formName + "_dataSource_eth0_operationMode");
		if(eth0OperationModeEl.value != OPERATION_MODE_BACKHAUL){
			showNetworkSettingsContent();
			hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
			return false;
		}
	}else{
		var ethSetupTypeEl = document.getElementById(formName + "_dataSource_ethConfigType");
		if(ethSetupTypeEl.value == USE_ETHERNET_BOTH){
			var eth0OperationModeEl = document.getElementById(formName + "_dataSource_eth0_operationMode");
			var eth1OperationModeEl = document.getElementById(formName + "_dataSource_eth1_operationMode");
			if(eth0OperationModeEl.value != OPERATION_MODE_BACKHAUL && eth1OperationModeEl.value != OPERATION_MODE_BACKHAUL){
				showNetworkSettingsContent();
				hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			}
		}else if(ethSetupTypeEl.value == USE_ETHERNET_AGG0){
			var agg0OperationModeEl = document.getElementById(formName + "_dataSource_agg0_operationMode");
			if(agg0OperationModeEl.value != OPERATION_MODE_BACKHAUL){
				showNetworkSettingsContent();
				hm.util.reportFieldError(agg0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			}
		}else if(ethSetupTypeEl.value == USE_ETHERNET_RED0){
			var red0OperationModeEl = document.getElementById(formName + "_dataSource_red0_operationMode");
			if(red0OperationModeEl.value != OPERATION_MODE_BACKHAUL){
				showNetworkSettingsContent();
				hm.util.reportFieldError(red0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			}
		}
	}
	return true;
}

function isEthAccessPortAvailable(){
	var ethSetupTypeEl = document.getElementById(formName + "_dataSource_ethConfigType");
	if(ethSetupTypeEl.value != USE_ETHERNET_BOTH){
		return false;
	}
	if(!isEth1Available()){// just check eth0
		var eth0OperationModeEl = document.getElementById(formName + "_dataSource_eth0_operationMode");
		if(eth0OperationModeEl.value != OPERATION_MODE_ACCESS){
			return false;
		}
	}else{
		var eth0OperationModeEl = document.getElementById(formName + "_dataSource_eth0_operationMode");
		var eth1OperationModeEl = document.getElementById(formName + "_dataSource_eth1_operationMode");
		if(eth0OperationModeEl.value != OPERATION_MODE_ACCESS && eth1OperationModeEl.value != OPERATION_MODE_ACCESS){
			return false;
		}
	}
	return true;
}

function validateAllowedVlan(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') && deviceType == DEVICE_TYPE_HIVEAP){
		var ethSetupTypeEl = document.getElementById(formName + "_dataSource_ethConfigType");
		var eth0AllowedVlanEl = document.getElementById(formName + "_dataSource_eth0_allowedVlan");
		var eth1AllowedVlanEl = document.getElementById(formName + "_dataSource_eth1_allowedVlan");
		var agg0AllowedVlanEl = document.getElementById(formName + "_dataSource_agg0_allowedVlan");
		var red0AllowedVlanEl = document.getElementById(formName + "_dataSource_red0_allowedVlan");

		if(!validateAllowVlanFormat(eth0AllowedVlanEl)){
			showNetworkSettingsContent();
			showEthAdvSettingsContent();
			eth0AllowedVlanEl.focus();
			return false;
		}
		if(isEth1Available()){
			if(!validateAllowVlanFormat(eth1AllowedVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				eth1AllowedVlanEl.focus();
				return false;
			}
		}
		if(ethSetupTypeEl.value == USE_ETHERNET_AGG0){
			if(!validateAllowVlanFormat(agg0AllowedVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				agg0AllowedVlanEl.focus();
				return false;
			}
		}else if(ethSetupTypeEl.value == USE_ETHERNET_RED0){
			if(!validateAllowVlanFormat(red0AllowedVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				red0AllowedVlanEl.focus();
				return false;
			}
		}
	}
	return true;
}

function validateAllowVlanFormat(allowedVlans){
	if (allowedVlans.value.length == 0) {
        hm.util.reportFieldError(allowedVlans, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.allowedVlan" /></s:param></s:text>');
        return false;
    }
	var vlans = allowedVlans.value.split(",");
	var hasAllOption = false;
	for(var i=0; i<vlans.length; i++){
		var vlan = vlans[i];
		if('' == vlan){
			hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
			return false;
		}
		if(isNaN(vlan)){//not a number
			if(!vlan.match(/^all$/i) && !vlan.match(/^auto$/i) ){
				// is a number range?
				var range = vlan.split("-");
				if(range.length != 2){
					hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
					return false;
				}
				var start = range[0];
				var end = range[1];
				var number = parseInt(start);
				if(isNaN(number) || number <1 || number > 4094){
					hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
					return false;
				}
				number = parseInt(end);
				if(isNaN(number) || number <1 || number > 4094){
					hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
					return false;
				}
				if(parseInt(start) > parseInt(end)){
					hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
					return false;
				}
			}else{
				if(vlan.match(/^all$/i)){
					hasAllOption = true;
				}
			}
		}else{
			var number = parseInt(vlan);
			if(number <1 || number > 4094){
				hm.util.reportFieldError(allowedVlans, "<s:text name="hiveAp.if.allowedVlan.note"></s:text>");
				return false;
			}
		}
	}
	if(hasAllOption && vlans.length > 1){
		hm.util.reportFieldError(allowedVlans, '<s:text name="hiveAp.if.allowedVlan.note2"></s:text>');
		return false;
	}
	return true;
}
function validateInterfaceRole(operation){
	if(operation == 'create2' || operation == 'update2'){
		var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
		if(deviceType == DEVICE_TYPE_BRANCH_ROUTER){
			var eth0Role = document.getElementById(formName + "_branchRouterEth0_role");
			var usbRole = document.getElementById(formName + "_branchRouterUSB_role");
			if(eth0Role.value == usbRole.value){
				hm.util.reportFieldError(eth0Role,"<s:text name="error.hiveAp.ethernet.interfaceRole"></s:text>");
				eth0Role.focus();
				return false;
			}else{
				return true;
			}
		}
	}
	return true;
}
function validateSpeedAndDuplex(operation){
	if(operation == 'create2' || operation == 'update2'){
		var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
		if(deviceType == DEVICE_TYPE_VPN_GATEWAY){
			return true;
		}
		if(deviceType == DEVICE_TYPE_HIVEAP){
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_dataSource_eth0_duplex"),document.getElementById(formName + "_dataSource_eth0_speed"))) {
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				return false;
			}
			/**
			var _dataSource_eth0_duplex = document.getElementById(formName + "_dataSource_eth0_duplex");
			var _dataSource_eth0_speed = document.getElementById(formName + "_dataSource_eth0_speed");
			if((_dataSource_eth0_duplex.value == ETH_DUPLEX_AUTO && _dataSource_eth0_speed.value != ETH_SPEED_AUTO)
					|| (_dataSource_eth0_duplex.value != ETH_DUPLEX_AUTO && _dataSource_eth0_speed.value == ETH_SPEED_AUTO)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				hm.util.reportFieldError(_dataSource_eth0_duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit"></s:text>');
				_dataSource_eth0_duplex.focus();
				return false;
			}
			if(_dataSource_eth0_duplex.value == ETH_DUPLEX_HALF && _dataSource_eth0_speed.value == ETH_SPEED_1000M){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				hm.util.reportFieldError(_dataSource_eth0_duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit2"></s:text>');
				_dataSource_eth0_duplex.focus();
				return false;
			}**/
			if(isEth1Available()){
				if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_dataSource_eth1_duplex"),document.getElementById(formName + "_dataSource_eth1_speed"))) {
					showNetworkSettingsContent();
					showEthAdvSettingsContent();
					return false;
				}
				/**
				var _dataSource_eth1_duplex = document.getElementById(formName + "_dataSource_eth1_duplex");
				var _dataSource_eth1_speed = document.getElementById(formName + "_dataSource_eth1_speed");
				if((_dataSource_eth1_duplex.value == ETH_DUPLEX_AUTO && _dataSource_eth1_speed.value != ETH_SPEED_AUTO)
						|| (_dataSource_eth1_duplex.value != ETH_DUPLEX_AUTO && _dataSource_eth1_speed.value == ETH_SPEED_AUTO)){
					showNetworkSettingsContent();
					showEthAdvSettingsContent();
					hm.util.reportFieldError(_dataSource_eth1_duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit"></s:text>');
					_dataSource_eth1_duplex.focus();
					return false;
				}
				if(_dataSource_eth1_duplex.value == ETH_DUPLEX_HALF && _dataSource_eth1_speed.value == ETH_SPEED_1000M){
					showNetworkSettingsContent();
					showEthAdvSettingsContent();
					hm.util.reportFieldError(_dataSource_eth1_duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit2"></s:text>');
					_dataSource_eth1_duplex.focus();
					return false;
				}**/
			}
		} else if(deviceType == DEVICE_TYPE_BRANCH_ROUTER){
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth0_duplex"),document.getElementById(formName + "_branchRouterEth0_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth1_duplex"),document.getElementById(formName + "_branchRouterEth1_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth2_duplex"),document.getElementById(formName + "_branchRouterEth2_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth3_duplex"),document.getElementById(formName + "_branchRouterEth3_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth4_duplex"),document.getElementById(formName + "_branchRouterEth4_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterUSB_duplex"),document.getElementById(formName + "_branchRouterUSB_speed"))) {
				showNetworkSettingsContent();
				return false;
			}
			var apModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
// 			if (apModel!=MODEL_330 && apModel!=MODEL_350) {
// 				if (Get(formName + "_dataSource_enablePppoe").checked) {
// 					if (Get(formName + "_pppoeAuthProfile").value<1) {
// 						showNetworkSettingsContent();
// 						hm.util.reportFieldError(Get(formName + "_pppoeAuthProfile"), '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.brRouter.port.settings.pppoeAuth" /></s:param></s:text>');
// 		           		Get(formName + "_pppoeAuthProfile").focus();
// 		            	return false;
// 					}
// 				}
// 			}

			if(Get(formName + "_enabledOverrideRoutingPolicy").checked) {
				if (Get(formName + "_routingPolicyId").value<0) {
					showNetworkSettingsContent();
					hm.util.reportFieldError(Get(formName + "_routingPolicyId"), '<s:text name="error.requiredField"><s:param><s:text name="config.networkpolicy.routingPolicy.title" /></s:param></s:text>');
		           	Get(formName + "_routingPolicyId").focus();
		            return false;
				}
			}
			
			if(Get(formName + "_dataSource_enableMDM").checked) {
				if (Get(formName + "_configmdmId").value<0) {
					showNetworkSettingsContent();
					hm.util.reportFieldError(Get(formName + "_configmdmId"), '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.title" /></s:param></s:text>');
		           	Get(formName + "_configmdmId").focus();
		            return false;
				}
			}

			var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth0_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
		    if (message != null) {
		    	showNetworkSettingsContent();
	            hm.util.reportFieldError(Get(formName + "_branchRouterEth0_maxDownload"), message);
	           	Get(formName + "_branchRouterEth0_maxDownload").focus();
	            return false;
		    }
		    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth0_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
		    if (message != null) {
		    	showNetworkSettingsContent();
	            hm.util.reportFieldError(Get(formName + "_branchRouterEth0_maxUpload"), message);
	           	Get(formName + "_branchRouterEth0_maxUpload").focus();
	            return false;
		    }
		    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterUSB_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
		    if (message != null) {
		    	showNetworkSettingsContent();
	            hm.util.reportFieldError(Get(formName + "_branchRouterUSB_maxDownload"), message);
	           	Get(formName + "_branchRouterUSB_maxDownload").focus();
	            return false;
		    }
		    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterUSB_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
		    if (message != null) {
		    	showNetworkSettingsContent();
	            hm.util.reportFieldError(Get(formName + "_branchRouterUSB_maxUpload"), message);
	           	Get(formName + "_branchRouterUSB_maxUpload").focus();
	            return false;
		    }

		}
	}
	return true;
}

function validateUploadAndDownload(){
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth1_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth1_maxDownload"), message);
       	Get(formName + "_branchRouterEth1_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth1_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth1_maxUpload"), message);
       	Get(formName + "_branchRouterEth1_maxUpload").focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth2_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth2_maxDownload"), message);
       	Get(formName + "_branchRouterEth2_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth2_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth2_maxUpload"), message);
       	Get(formName + "_branchRouterEth2_maxUpload").focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth3_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth3_maxDownload"), message);
       	Get(formName + "_branchRouterEth3_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth3_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth3_maxUpload"), message);
       	Get(formName + "_branchRouterEth3_maxUpload").focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth4_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth4_maxDownload"), message);
       	Get(formName + "_branchRouterEth4_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth4_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth4_maxUpload"), message);
       	Get(formName + "_branchRouterEth4_maxUpload").focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi0_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi0_maxDownload"), message);
       	Get(formName + "_branchRouterWifi0_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi0_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi0_maxUpload"), message);
       	Get(formName + "_branchRouterWifi0_maxUpload").focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi1_maxDownload").value, '<s:text name="hiveAp.brRouter.port.settings.voipDownload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi1_maxDownload"), message);
       	Get(formName + "_branchRouterWifi1_maxDownload").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi1_maxUpload").value, '<s:text name="hiveAp.brRouter.port.settings.voipUpload" />', 10, 15000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi1_maxUpload"), message);
       	Get(formName + "_branchRouterWifi1_maxUpload").focus();
        return false;
    }
    return true;
}

function validatePriority(){
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth0_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth0_priority"), message);
       	Get(formName + "_branchRouterEth0_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth1_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth1_priority"), message);
       	Get(formName + "_branchRouterEth1_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth2_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth2_priority"), message);
       	Get(formName + "_branchRouterEth2_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth3_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth3_priority"), message);
       	Get(formName + "_branchRouterEth3_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterEth4_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterEth4_priority"), message);
       	Get(formName + "_branchRouterEth4_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterUSB_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 0, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterUSB_priority"), message);
       	Get(formName + "_branchRouterUSB_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi0_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi0_priority"), message);
       	Get(formName + "_branchRouterWifi0_priority").focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(Get(formName + "_branchRouterWifi1_priority").value, '<s:text name="hiveAp.brRouter.port.settings.priority" />', 1, 2000);
    if (message != null) {
    	showNetworkSettingsContent();
        hm.util.reportFieldError(Get(formName + "_branchRouterWifi1_priority"), message);
       	Get(formName + "_branchRouterWifi1_priority").focus();
        return false;
    }
    return true;
}

function validateSpeedAndDuplexfunc(duplex, speed){
	if(duplex != null && speed != null){
		if((duplex.value == ETH_DUPLEX_AUTO && speed.value != ETH_SPEED_AUTO)
				|| (duplex.value != ETH_DUPLEX_AUTO && speed.value == ETH_SPEED_AUTO)){
			hm.util.reportFieldError(duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit"></s:text>');
			duplex.focus();
			return false;
		}
		if(duplex.value == ETH_DUPLEX_HALF && speed.value == ETH_SPEED_1000M){
			hm.util.reportFieldError(duplex, '<s:text name="error.hiveAp.ethernet.speedDuplexLimit2"></s:text>');
			duplex.focus();
			return false;
		}
	}

	return true;
}

function validateMacLearningSetting(operation){
	if(operation == 'create2' || operation == 'update2'){
		var eth0MacLearningTr = document.getElementById("eth0MacLearningTr");
		var eth1MacLearningTr = document.getElementById("eth1MacLearningTr");
		var agg0MacLearningTr = document.getElementById("agg0MacLearningTr");
		var red0MacLearningTr = document.getElementById("red0MacLearningTr");
		if(eth0MacLearningTr && eth0MacLearningTr.style.display == ""){
			//var checkbox = document.getElementById(formName + "_dataSource_eth0_macLearningEnabled");
			//if(checkbox && checkbox.checked){
				var input= document.getElementById(formName + "_dataSource_eth0_idelTimeout");
				if (input.value.length == 0) {
					hm.util.reportFieldError(input, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethernet.macLearning.idelTimeout" /></s:param></s:text>');
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(input.value, '<s:text name="hiveAp.ethernet.macLearning.idelTimeout" />',
				                                           <s:property value="10" />,
				                                           <s:property value="3600" />);
				if (message != null) {
					hm.util.reportFieldError(input, message);
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
			//}
		}
		if(eth1MacLearningTr && eth1MacLearningTr.style.display == ""){
			//var checkbox = document.getElementById(formName + "_dataSource_eth1_macLearningEnabled");
			//if(checkbox && checkbox.checked){
				var input= document.getElementById(formName + "_dataSource_eth1_idelTimeout");
				if (input.value.length == 0) {
					hm.util.reportFieldError(input, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethernet.macLearning.idelTimeout" /></s:param></s:text>');
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(input.value, '<s:text name="hiveAp.ethernet.macLearning.idelTimeout" />',
				                                           <s:property value="10" />,
				                                           <s:property value="3600" />);
				if (message != null) {
					hm.util.reportFieldError(input, message);
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
			//}
		}
		if(agg0MacLearningTr && agg0MacLearningTr.style.display == ""){
			//var checkbox = document.getElementById(formName + "_dataSource_agg0_macLearningEnabled");
			//if(checkbox && checkbox.checked){
				var input= document.getElementById(formName + "_dataSource_agg0_idelTimeout");
				if (input.value.length == 0) {
					hm.util.reportFieldError(input, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethernet.macLearning.idelTimeout" /></s:param></s:text>');
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(input.value, '<s:text name="hiveAp.ethernet.macLearning.idelTimeout" />',
				                                           <s:property value="10" />,
				                                           <s:property value="3600" />);
				if (message != null) {
					hm.util.reportFieldError(input, message);
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
			//}
		}
		if(red0MacLearningTr && red0MacLearningTr.style.display == ""){
			//var checkbox = document.getElementById(formName + "_dataSource_red0_macLearningEnabled");
			//if(checkbox && checkbox.checked){
				var input= document.getElementById(formName + "_dataSource_red0_idelTimeout");
				if (input.value.length == 0) {
					hm.util.reportFieldError(input, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethernet.macLearning.idelTimeout" /></s:param></s:text>');
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(input.value, '<s:text name="hiveAp.ethernet.macLearning.idelTimeout" />',
				                                           <s:property value="10" />,
				                                           <s:property value="3600" />);
				if (message != null) {
					hm.util.reportFieldError(input, message);
					showNetworkSettingsContent();
					input.focus();
					return false;
				}
			//}
		}
	}
	return true;
}

function validateEthCwpSetting(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType != DEVICE_TYPE_HIVEAP){
		//fix bug 26714 only AP exist this function.
		return true;
	}
	if(operation == 'create2' || operation == 'update2'){
		var _dataSource_ethCwpEnableEthCwp = document.getElementById(formName + "_dataSource_ethCwpEnableEthCwp");
		if(!_dataSource_ethCwpEnableEthCwp){
			return true;
		}
		var _dataSource_ethCwpEnableMacAuth = document.getElementById(formName + "_dataSource_ethCwpEnableMacAuth");
		var _dataSource_ethCwpLimitUserProfiles = document.getElementById(formName + "_dataSource_ethCwpLimitUserProfiles");
		
		if(_dataSource_ethCwpEnableEthCwp.checked){
			var _cwpProfile = document.getElementById(formName + "_cwpProfile");
			if(_cwpProfile.value <=0){
				showEthCwpSettingContent();
				hm.util.reportFieldError(_cwpProfile, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethCwp.profile.label" /></s:param></s:text>');
				_cwpProfile.focus();
				return false;
			}
		}
		
		if(_dataSource_ethCwpEnableEthCwp.checked || _dataSource_ethCwpEnableMacAuth.checked){
			
			if(_dataSource_ethCwpEnableMacAuth.checked || isEthCwpRadiusClient){// need check RADIUS settings
				var _ethCwpRadiusClient = document.getElementById(formName + "_ethCwpRadiusClient");
				if(_ethCwpRadiusClient.value <=0){
					showEthCwpSettingContent();
					hm.util.reportFieldError(_ethCwpRadiusClient, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethCwp.radius.profile" /></s:param></s:text>');
					_ethCwpRadiusClient.focus();
					return false;
				}
			}
			if(_dataSource_ethCwpLimitUserProfiles.checked){
				var _dataSource_ethCwpActiveTime = document.getElementById(formName + "_dataSource_ethCwpActiveTime");
				if(!_dataSource_ethCwpActiveTime.disabled){
					if (_dataSource_ethCwpActiveTime.value.length == 0) {
						showEthCwpSettingContent();
						hm.util.reportFieldError(_dataSource_ethCwpActiveTime, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethCwp.userprofile.actionTime" /></s:param></s:text>');
						_dataSource_ethCwpActiveTime.focus();
						return false;
					}
					var message = hm.util.validateIntegerRange(_dataSource_ethCwpActiveTime.value, '<s:text name="hiveAp.ethCwp.userprofile.actionTime" />',
					                                           <s:property value="1" />,
					                                           <s:property value="100000000 " />);
					if (message != null) {
						showEthCwpSettingContent();
						hm.util.reportFieldError(_dataSource_ethCwpActiveTime, message);
						_dataSource_ethCwpActiveTime.focus();
						return false;
					}
				}
			}
			if(!isEthAccessPortAvailable()){// check ethernet operation mode
				showEthCwpSettingContent();
				showNetworkSettingsContent();
				if(_dataSource_ethCwpEnableEthCwp.checked){
					hm.util.reportFieldError(_cwpProfile, '<s:text name="error.hiveAp.ethernet.cwp.enable"></s:text>');
				}else if(_dataSource_ethCwpEnableMacAuth.checked){
					hm.util.reportFieldError(_dataSource_ethCwpEnableMacAuth, '<s:text name="error.hiveAp.ethernet.macAuth.enable"></s:text>');
				}
				
				return false;
			}
		}
	}
	return true;
}

function validateNativeVlan(operation){
	var cbxEl = document.getElementById("overrideVlan");
	if(!cbxEl){
		return true;
	}
	if(!cbxEl.checked){
		return true;
	}
	var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");

	if (nativeVlanElement.value.length == 0) {
		hm.util.reportFieldError(nativeVlanElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.nativeVlan" /></s:param></s:text>');
		showAdvancedSettingsContent();
		nativeVlanElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(nativeVlanElement.value, '<s:text name="hiveAp.nativeVlan" />',
	                                           <s:property value="1" />,
	                                           <s:property value="4094" />);
	if (message != null) {
		hm.util.reportFieldError(nativeVlanElement, message);
		showAdvancedSettingsContent();
		nativeVlanElement.focus();
		return false;
	}
	return true;
}

function validateRouterInterval(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType != DEVICE_TYPE_BRANCH_ROUTER){
		return true;
	}
	if(operation == 'create2' || operation == 'update2'){
		var routeIntervalEle = document.getElementById(formName + "_dataSource_routeInterval");

		if (routeIntervalEle.value.length == 0) {
			hm.util.reportFieldError(routeIntervalEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.vpn.br.route.interval" /></s:param></s:text>');
			showAdvancedSettingsContent();
			routeIntervalEle.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(routeIntervalEle.value, '<s:text name="hiveAp.vpn.br.route.interval" />',
		                                           <s:property value="30" />,
		                                           <s:property value="600" />);
		if (message != null) {
			hm.util.reportFieldError(routeIntervalEle, message);
			showAdvancedSettingsContent();
			routeIntervalEle.focus();
			return false;
		}
	}
	return true;
}

function validateMaxPowerSource(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType != DEVICE_TYPE_BRANCH_ROUTER){
		return true;
	}
	if(operation == 'create2' || operation == 'update2'){
		var maxPowerSource = document.getElementById(formName + "_dataSource_maxPowerSource");

		if (maxPowerSource.value.length == 0) {
			hm.util.reportFieldError(maxPowerSource, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.vpn.br.route.powersource.maximum" /></s:param></s:text>');
			showAdvancedSettingsContent();
			maxPowerSource.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(maxPowerSource.value, '<s:text name="hiveAp.vpn.br.route.powersource.maximum" />',
		                                           <s:property value="0" />,
		                                           <s:property value="44" />);
		if (message != null) {
			hm.util.reportFieldError(maxPowerSource, message);
			showAdvancedSettingsContent();
			maxPowerSource.focus();
			return false;
		}
	}
	return true;
}

function validateMgtVlan(operation){
	var cbxEl = document.getElementById("overrideMgtVlan");
	if(!cbxEl || !cbxEl.checked){
		return true;
	}
	var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");

	if (mgtVlanElement.value.length == 0) {
		hm.util.reportFieldError(mgtVlanElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.mgtVlan" /></s:param></s:text>');
		showAdvancedSettingsContent();
		mgtVlanElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(mgtVlanElement.value, '<s:text name="hiveAp.mgtVlan" />',
	                                           <s:property value="1" />,
	                                           <s:property value="4094" />);
	if (message != null) {
		hm.util.reportFieldError(mgtVlanElement, message);
		showAdvancedSettingsContent();
		mgtVlanElement.focus();
		return false;
	}
	return true;
}

function validateStaticRoute(operation){
	if(operation == 'addStaticRoute2'){
		var destnationMacElement = document.getElementById(formName + "_destinationMac");
		var nextHopMacElement = document.getElementById(formName + "_nextHopMac");
		var displayElement = document.getElementById("checkAllStatic");

		if (destnationMacElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.staticRoute.destination" /></s:param></s:text>');
			destnationMacElement.focus();
			return false;
		}
		if (nextHopMacElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.staticRoute.nextHop" /></s:param></s:text>');
			nextHopMacElement.focus();
			return false;
		}
		if (! hm.util.validateMacAddress(destnationMacElement.value,12)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.hiveAp.macFormatInvalid"><s:param><s:text name="hiveAp.cfg.staticRoute.destination" /></s:param></s:text>');
			destnationMacElement.focus();
			return false;
		}
		if (! hm.util.validateMacAddress(nextHopMacElement.value,12)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.hiveAp.macFormatInvalid"><s:param><s:text name="hiveAp.cfg.staticRoute.nextHop" /></s:param></s:text>');
			nextHopMacElement.focus();
			return false;
		}
	}
	if (operation == 'removeStaticRoutes2'){
		var cbs = document.getElementsByName('staticRouteIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllStatic");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllStatic");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateBrStaticRouting(operation){
	if(operation == 'addBrStaticRouting'){
		var brStaticRouteIpInput = document.getElementById(formName + "_brStaticRouteIpInput");
		var brStaticRouteMaskInput = document.getElementById(formName + "_brStaticRouteMaskInput");
		var brStaticRouteGwInput = document.getElementById(formName + "_brStaticRouteGwInput");

		if (brStaticRouteIpInput.value.length == 0) {
			hm.util.reportFieldError(brStaticRouteIpInput, '<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.ip" /></s:param></s:text>');
			brStaticRouteIpInput.focus();
			return false;
		}
		if (brStaticRouteMaskInput.value.length == 0) {
			hm.util.reportFieldError(brStaticRouteMaskInput, '<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.netmask" /></s:param></s:text>');
			brStaticRouteMaskInput.focus();
			return false;
		}
		if (brStaticRouteGwInput.value.length == 0) {
			hm.util.reportFieldError(brStaticRouteGwInput, '<s:text name="error.requiredField"><s:param><s:text name="config.routingProfiles.gateway" /></s:param></s:text>');
			brStaticRouteGwInput.focus();
			return false;
		}

		if (brStaticRouteIpInput.value != '0.0.0.0' && !hm.util.validateIpAddress(brStaticRouteIpInput.value)) {
			hm.util.reportFieldError(brStaticRouteIpInput, '<s:text name="error.formatInvalid"><s:param><s:text name="config.routingProfiles.ip" /></s:param></s:text>');
			brStaticRouteIpInput.focus();
			return false;
		}
		if (brStaticRouteMaskInput.value != '0.0.0.0' && !hm.util.validateMask(brStaticRouteMaskInput.value)) {
			hm.util.reportFieldError(brStaticRouteMaskInput, '<s:text name="error.formatInvalid"><s:param><s:text name="config.routingProfiles.netmask" /></s:param></s:text>');
			brStaticRouteMaskInput.focus();
			return false;
		}
		if (!hm.util.validateIpAddress(brStaticRouteGwInput.value)) {
			hm.util.reportFieldError(brStaticRouteGwInput, '<s:text name="error.formatInvalid"><s:param><s:text name="config.routingProfiles.gateway" /></s:param></s:text>');
			brStaticRouteGwInput.focus();
			return false;
		}
	}
	if (operation == 'removeBrStaticRouting'){
		var cbs = document.getElementsByName('brStaticRouteingIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllBrStaticRouting");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllBrStaticRouting");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateIntNetwork(operation){
	if(operation == 'addIntNetwork'){
		var interNetIpInput = document.getElementById(formName + "_interNetIpInput");
		var interNetMaskInput = document.getElementById(formName + "_interNetMaskInput");

		if (interNetIpInput.value.length == 0) {
			hm.util.reportFieldError(interNetIpInput, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cvg.internalNetwork.network" /></s:param></s:text>');
			interNetIpInput.focus();
			return false;
		}
		if (interNetMaskInput.value.length == 0) {
			hm.util.reportFieldError(interNetMaskInput, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cvg.internalNetwork.netmask" /></s:param></s:text>');
			interNetMaskInput.focus();
			return false;
		}
		if (!hm.util.validateIpAddress(interNetIpInput.value)) {
			hm.util.reportFieldError(interNetIpInput, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cvg.internalNetwork.network" /></s:param></s:text>');
			interNetIpInput.focus();
			return false;
		}
		if (!hm.util.validateMask(interNetMaskInput.value)) {
			hm.util.reportFieldError(interNetMaskInput, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cvg.internalNetwork.netmask" /></s:param></s:text>');
			interNetMaskInput.focus();
			return false;
		}
	}
	if (operation == 'removeIntNetwork'){
		var cbs = document.getElementsByName('intNetworkIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllIntNetwork");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllIntNetwork");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateDynamicRoute(operation){
	if( operation == 'addDynamicRoute2'){
		var neighborMacElement = document.getElementById(formName + "_neighborMac");
		//var displayElement = document.getElementById("checkAll");
		var displayElement = document.getElementById("headerSectionDynamicRoutes2");
		if (neighborMacElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.dynamicRoute.neighborMac" /></s:param></s:text>');
			neighborMacElement.focus();
			return false;
		}
		if (! hm.util.validateMacAddress(neighborMacElement.value,12)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.hiveAp.macFormatInvalid"><s:param><s:text name="hiveAp.cfg.dynamicRoute.neighborMac" /></s:param></s:text>');
			neighborMacElement.focus();
			return false;
		}
		var routeMinimumElement = document.getElementById(formName + "_routeMinimun");
		var routeMaximumElement = document.getElementById(formName + "_routeMaximun");
		//var displayElement = document.getElementById("checkAll");
		var displayElement = document.getElementById("headerSectionDynamicRoutes2");
		if (routeMinimumElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.minimum" /></s:param></s:text>');
			return false;
		}
		var message = hm.util.validateIntegerRange(routeMinimumElement.value, '<s:text name="hiveAp.cfg.dynamicRoute.metric.minimum" />',
		                                           <s:property value="8" />,
		                                           <s:property value="1200" />);
		if (message != null) {
			hm.util.reportFieldError(displayElement, message);
			return false;
		}

		if (routeMaximumElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.maximum" /></s:param></s:text>');
			return false;
		}
		var message = hm.util.validateIntegerRange(routeMaximumElement.value, '<s:text name="hiveAp.cfg.dynamicRoute.metric.maximum" />',
		                                           <s:property value="8" />,
		                                           <s:property value="1200" />);
		if (message != null) {
			hm.util.reportFieldError(displayElement, message);
			return false;
		}
		if(parseInt(routeMaximumElement.value) < parseInt(routeMinimumElement.value)){
			hm.util.reportFieldError(displayElement, '<s:text name="error.notLargerThan"><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.minimum" /></s:param><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.maximum" /></s:param></s:text>');
			return false;
		}
	}
	if (operation == 'removeDynamicRoutes2'){
		var cbs = document.getElementsByName('dynamicRouteIndices');
		if (cbs.length == 0) {
			//var feChild = document.getElementById("checkAll");
			var feChild = document.getElementById("headerSectionDynamicRoutes2");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			//var feChild = document.getElementById("checkAll");
			var feChild = document.getElementById("headerSectionDynamicRoutes2");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	if(operation == 'create2' || operation == 'update2'){
		var metricIntervalElement = document.getElementById(formName + "_dataSource_metricInteval");
		if(!metricIntervalElement){
			return true;
		}
		if (metricIntervalElement.value.length == 0) {
			hm.util.reportFieldError(metricIntervalElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.interval" /></s:param></s:text>');
			showRoutingContent();
			return false;
		}
		var message = hm.util.validateIntegerRange(metricIntervalElement.value, '<s:text name="hiveAp.cfg.dynamicRoute.metric.interval" />',
		                                           <s:property value="%{metricIntervalRange.min()}" />,
		                                           <s:property value="%{metricIntervalRange.max()}" />);
		if (message != null) {
			hm.util.reportFieldError(metricIntervalElement, message);
			showRoutingContent();
			return false;
		}
	}
	return true;
}

function validateIpRoute(operation){
	if(operation == 'addIpRoute2'){
		var ipRouteIpElement = document.getElementById(formName + "_ipRouteIpInput");
		var ipRouteMaskElement = document.getElementById(formName + "_ipRouteMaskInput");
		var ipRouteGwElement = document.getElementById(formName + "_ipRouteGwInput");

		var displayElement = document.getElementById("checkAllIp");

		if (ipRouteIpElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.ipRoute.dest" /></s:param></s:text>');
			ipRouteIpElement.focus();
			return false;
		}
		if (ipRouteMaskElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.ipRoute.mask" /></s:param></s:text>');
			ipRouteMaskElement.focus();
			return false;
		}
		if (ipRouteGwElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.ipRoute.gateway" /></s:param></s:text>');
			ipRouteGwElement.focus();
			return false;
		}

		if (!hm.util.validateIpAddress(ipRouteIpElement.value)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cfg.ipRoute.dest" /></s:param></s:text>');
			ipRouteIpElement.focus();
			return false;
		}
		if (!hm.util.validateMask(ipRouteMaskElement.value)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cfg.ipRoute.mask" /></s:param></s:text>');
			ipRouteMaskElement.focus();
			return false;
		}
		if (!hm.util.validateIpAddress(ipRouteGwElement.value)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cfg.ipRoute.gateway" /></s:param></s:text>');
			ipRouteGwElement.focus();
			return false;
		}
	}
	if (operation == 'removeIpRoutes2'){
		var cbs = document.getElementsByName('ipRouteIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllIp");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllIp");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateMultiNativeVlan(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') && deviceType == DEVICE_TYPE_HIVEAP){
		var ethSetupTypeEl = document.getElementById(formName + "_dataSource_ethConfigType");
		var eth0MultiNativeVlanEl = document.getElementById(formName + "_dataSource_eth0_multiNativeVlan");
		var eth1MultiNativeVlanEl = document.getElementById(formName + "_dataSource_eth1_multiNativeVlan");
		var agg0MultiNativeVlanEl = document.getElementById(formName + "_dataSource_agg0_multiNativeVlan");
		var red0MultiNativeVlanEl = document.getElementById(formName + "_dataSource_red0_multiNativeVlan");

		if(eth0MultiNativeVlanEl.disabled==false&&!validateMultipleNativeVlanFormat(eth0MultiNativeVlanEl)){
			showNetworkSettingsContent();
			showEthAdvSettingsContent();
			eth0MultiNativeVlanEl.focus();
			return false;
		}
		if(isEth1Available()){
			if(eth1MultiNativeVlanEl.disabled==false&&!validateMultipleNativeVlanFormat(eth1MultiNativeVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				eth1MultiNativeVlanEl.focus();
				return false;
			}
		}
		
		if(ethSetupTypeEl.value == USE_ETHERNET_AGG0){
			if(agg0MultiNativeVlanEl.disabled==false&&!validateMultipleNativeVlanFormat(agg0MultiNativeVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				agg0MultiNativeVlanEl.focus();
				return false;
			}
		}else if(ethSetupTypeEl.value == USE_ETHERNET_RED0){
			if(red0MultiNativeVlanEl.disabled==false&&!validateMultipleNativeVlanFormat(red0MultiNativeVlanEl)){
				showNetworkSettingsContent();
				showEthAdvSettingsContent();
				red0MultiNativeVlanEl.focus();
				return false;
			}
		}
	}
	return true;
}
function validateMultipleNativeVlanFormat(multipleVlans){
	if (multipleVlans.value.length == 0) {
		return true;
	}
	var message = hm.util.validateIntegerRange(multipleVlans.value, '<s:text name="hiveAp.if.multinativeVlan.note" />',
	                                           <s:property value="1" />,
	                                           <s:property value="4094" />);
	if (message != null) {
		hm.util.reportFieldError(multipleVlans, message);
		multipleVlans.focus();
		return false;
	}
	return true;
}
function validateMultipleVlan(operation){
	if(operation == 'addMultipleVlan'){
		var MultipleVlanElement = document.getElementById(formName + "_multiplevlanInput");
		var displayElement = document.getElementById("checkAllVlanId");

		if(!validateMultipleVlanFormat(MultipleVlanElement)){
			MultipleVlanElement.focus();
			return false;
		}
		if(isEth1Available()){
			if(!validateMultipleVlanFormat(MultipleVlanElement)){
				MultipleVlanElement.focus();
				return false;
			}
		}
	}
	if (operation == 'removeMultipleVlan'){
		var cbs = document.getElementsByName('multiplevlanIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllVlanId");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllVlanId");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateMultipleVlanId(operation){
	if(operation == 'addMultipleVlan'){
		var rows =document.getElementById("multipleVlanTable").rows;
		var MultipleVlanElement = document.getElementById(formName + "_multiplevlanInput");
		var displayElement = document.getElementById("checkAllVlanId");
		if(rows != null){
			for(var i=3; i<rows.length; i++){
				var td = rows[i].cells[1];
				if(null != td){
					var vlanid = td.innerHTML;
					if(MultipleVlanElement.value.match(vlanid.trim())){
						hm.util.reportFieldError(displayElement, '<s:text name="error.addSameNameObjectExist"><s:param><s:text name="hiveAp.ethernet.multiple.vlan" /></s:param></s:text>');
						return false;
					}
				}
			}
		}
	}
	return true;
}
function validateMultipleVlanFormat(multipleVlans){
	if (multipleVlans.value.length == 0) {
        hm.util.reportFieldError(multipleVlans, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ethernet.multiple.route" /></s:param></s:text>');
        return false;
    }
	var vlans = multipleVlans.value.split(",");
	var vlans2 = multipleVlans.value.split(".");
	if(vlans2.length>1){
		hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
		return false;
	}
	var hasAllOption = false;
	for(var i=0; i<vlans.length; i++){
		var vlan = vlans[i];
		if('' == vlan){
			hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
			return false;
		}
		if(isNaN(vlan)){//not a number
			if(!vlan.match(/^all$/i) && !vlan.match(/^auto$/i) ){
			// is a number range?
			var range = vlan.split("-");
			if(range.length != 2){
				hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
				return false;
			}
			var start = range[0];
			var end = range[1];
			var number = parseInt(start);
			if(isNaN(number) || number <1 || number > 4094||number.length>4){
				hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
				return false;
			}
			number = parseInt(end);
			if(isNaN(number) || number <1 || number > 4094||number.length>4){
				hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
				return false;
			}
			if(parseInt(start) > parseInt(end)){
				hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
				return false;
			}
		}
	}else{
			var number = parseInt(vlan);
			if(number <1 || number > 4094||number.length>4){
				hm.util.reportFieldError(multipleVlans, "<s:text name="hiveAp.if.multipleVlan.note"></s:text>");
				return false;
			}
		}
	}
	return true;
}

function validateVirtualConnect(operation){
	if(operation == 'addVirtualConnect'){
		var virtualConnectName = document.getElementById(formName + "_virtualConnectName");
		var virtualConnectSourceMac = document.getElementById(formName + "_virtualConnectSourceMac");
		var virtualConnectDestMac = document.getElementById(formName + "_virtualConnectDestMac");
		var virtualConnectTxMac = document.getElementById(formName + "_virtualConnectTxMac");
		var virtualConnectRxMac = document.getElementById(formName + "_virtualConnectRxMac");

		var displayElement = document.getElementById("checkAllVirtualConnect");

		if (virtualConnectName.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.virtualConnection.name" /></s:param></s:text>');
			virtualConnectName.focus();
			return false;
		}

		if (!virtualConnectSourceMac.disabled)
		{
			if (virtualConnectSourceMac.value.length > 0) {
				if (!hm.util.validateMacAddress(virtualConnectSourceMac.value,6) && !hm.util.validateMacAddress(virtualConnectSourceMac.value,12)) {
					hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.virtualConnection.sourceMac" /></s:param></s:text>');
					virtualConnectSourceMac.focus();
					return false;
				}
			}
		}

		if (!virtualConnectDestMac.disabled)
		{
			if (virtualConnectDestMac.value.length == 0) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.virtualConnection.destMac" /></s:param></s:text>');
				virtualConnectDestMac.focus();
				return false;
			}
			if (!hm.util.validateMacAddress(virtualConnectDestMac.value,12)) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.virtualConnection.destMac" /></s:param></s:text>');
				virtualConnectDestMac.focus();
				return false;
			}
		}

		if (!virtualConnectTxMac.disabled)
		{
			if (virtualConnectTxMac.value.length == 0) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.virtualConnection.txMac" /></s:param></s:text>');
				virtualConnectTxMac.focus();
				return false;
			}
			if (!hm.util.validateMacAddress(virtualConnectTxMac.value,12)) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.virtualConnection.txMac" /></s:param></s:text>');
				virtualConnectTxMac.focus();
				return false;
			}
		}

		if (!virtualConnectRxMac.disabled)
		{
			if (virtualConnectRxMac.value.length == 0) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.virtualConnection.rxMac" /></s:param></s:text>');
				virtualConnectRxMac.focus();
				return false;
			}
			if (!hm.util.validateMacAddress(virtualConnectRxMac.value,12)) {
				hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.virtualConnection.rxMac" /></s:param></s:text>');
				virtualConnectRxMac.focus();
				return false;
			}
		}
	}
	if (operation == 'removeVirtualConnect'){
		var cbs = document.getElementsByName('virtualConnectIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllVirtualConnect");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllVirtualConnect");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function validateNeighborMac(operation){
	if(operation == 'addDynamicRoute2'){
		var rows =document.getElementById("dynamicTable").rows;
		var neighborMacElement = document.getElementById(formName + "_neighborMac");
		var displayElement = document.getElementById("checkAll");
		if(rows != null){
			for(var i=3; i<rows.length; i++){
				var td = rows[i].cells[1];
				if(null != td){
					var neighborMac = td.innerHTML;
					if(neighborMacElement.value == neighborMac){
						hm.util.reportFieldError(displayElement, '<s:text name="error.addSameNameObjectExist"><s:param><s:text name="hiveAp.cfg.dynamicRoute.neighborMac" /></s:param></s:text>');
						return false;
					}
				}
			}
		}
	}
	return true;
}

function validateDestinationMac(operation){
	if(operation == 'addStaticRoute2'){
		var rows =document.getElementById("staticTable").rows;
		var destinationMacElement = document.getElementById(formName + "_destinationMac");
		var displayElement = document.getElementById("checkAllStatic");
		if(rows != null){
			for(var i=3; i<rows.length; i++){
				var td = rows[i].cells[1];
				if(null != td){
					var destinationMac = td.innerHTML;
					if(destinationMacElement.value == destinationMac){
						hm.util.reportFieldError(displayElement, '<s:text name="error.addSameNameObjectExist"><s:param><s:text name="hiveAp.cfg.staticRoute.destination" /></s:param></s:text>');
						return false;
					}
				}
			}
		}
	}
	return true;
}

function validateL3Neighbor(operation){
	if(operation == 'create2' || operation == 'update2'){
		var includedNeighbor = document.getElementById("includedNeighbors");
		var excludedNeighbor = document.getElementById("excludedNeighbors");
		var displayElement = document.getElementById("l3RoamingLabel");
		if(!includedNeighbor){
			return true;
		}

		var in_options = includedNeighbor.options;
		var ex_options = excludedNeighbor.options;
		
		//include neighbor + exclude neighbor max support 32
		var all_options_size = in_options.length + ex_options.length;
		if(all_options_size > 32){
			hm.util.reportFieldError(displayElement, '<s:text name="error.bothInIncludeExcludeNeighbor.maxSize"><s:param>32</s:param></s:text>');
			return false;
		}
		
		if(in_options != null && ex_options != null){
			for(var i=0; i< in_options.length; i++){
				var in_mac = in_options[i].value;
				for(var j=0; j< ex_options.length; j++){
					var ex_mac = ex_options[j].value;
					if(in_mac == ex_mac){
						hm.util.reportFieldError(displayElement, '<s:text name="error.bothInIncludeExcludeNeighbor"></s:text>');
						showL3RoamingContent();
						includedNeighbor.selectedIndex = i;
						excludedNeighbor.selectedIndex = j;
						return false;
					}
				}
			}
		}
	}
	return true;
}

function validateRADIUSServer(operation){
	if (operation == 'create2' || operation == 'update2') {
		var radiusElement = document.getElementById(formName + "_radiusServer");
		if(radiusElement && radiusElement.value > 0){
			var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
			var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
			//if((selectApModel == MODEL_330 || selectApModel == MODEL_350)
			//		&& deviceType == DEVICE_TYPE_BRANCH_ROUTER) {
			if(deviceType == DEVICE_TYPE_BRANCH_ROUTER || deviceType == DEVICE_TYPE_VPN_GATEWAY) {
				// fixed Bug 14928, go thru
				return true;
			} else {
				//var dhcpboxElement = document.getElementById(formName + "_dataSource_dhcp");
				var dhcpboxElement = document.getElementById(formName + "_mgt0NetworkType1");
				if(!dhcpboxElement.checked){
					hm.util.reportFieldError(dhcpboxElement, '<s:text name="error.hiveAp.radiusServer.term"></s:text>');
					showNetworkSettingsContent();
					hm.util.show("mgt0DhcpSettings");
					return false;
				}
			}
		}
	}
	return true;
}

function validateRADIUSProxy(operation){
	if(operation == 'create2' || operation == 'update2'){
		var radiusElement = document.getElementById(formName + "_radiusProxy");
		if(radiusElement && radiusElement.value > 0){
			var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
			var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
			//if((selectApModel == MODEL_330 || selectApModel == MODEL_350)
			//		&& deviceType == DEVICE_TYPE_BRANCH_ROUTER) {
			if(deviceType == DEVICE_TYPE_BRANCH_ROUTER || deviceType == DEVICE_TYPE_VPN_GATEWAY) {
				// fixed Bug 14928, go thru
				return true;
			} else {
				// couldn't be dhcp client
				//var dhcpboxElement = document.getElementById(formName + "_dataSource_dhcp");
				var dhcpboxElement = document.getElementById(formName + "_mgt0NetworkType1");
				if(!dhcpboxElement.checked){
					hm.util.reportFieldError(dhcpboxElement, '<s:text name="error.hiveAp.radiusProxy.term"></s:text>');
					showNetworkSettingsContent();
					hm.util.show("mgt0DhcpSettings");
					return false;
				}
			}
		}
	}
	return true;
}

function validateRADIUSServieAndProxy(operation){
	if(operation == 'create2' || operation == 'update2'){
		var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
		var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
		//if((selectApModel == MODEL_330 || selectApModel == MODEL_350)
		//		&& deviceType == DEVICE_TYPE_BRANCH_ROUTER) {
		if(deviceType == DEVICE_TYPE_BRANCH_ROUTER && selectApModel!= BR100) {
			if (Get(formName + "_dataSource_enabledBrAsRadiusServer").checked &&
				Get(formName + "_dataSource_enabledOverrideRadiusServer").checked) {
				var radiusElement = document.getElementById(formName + "_radiusServer");
				var proxyElement = document.getElementById(formName + "_radiusProxy");
				if(radiusElement.value < 0 && proxyElement.value<0){
					hm.util.reportFieldError(proxyElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.radiusServerLabel.br" /> or <s:text name="hiveAp.radiusProxyLabel.br" /></s:param></s:text>');
					showServiceSettingsContent();
					return false;
				}
			}
		}
	}
	return true;
}


function validateCustomizedNasIdentifier(operation) {
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') &&
			(deviceType == DEVICE_TYPE_HIVEAP || deviceType == DEVICE_TYPE_BRANCH_ROUTER)){
		var radioOptions = document.getElementsByName('dataSource.nasIdentifierType');
		var radioOption;
		for(var i=0; i<radioOptions.length; i++){
			if(radioOptions[i].checked){
				radioOption = radioOptions[i];
				break;
			}
		}
		if(radioOption && parseInt(radioOption.value) == USE_CUSTOMIZED_NAS_IDE){
			var customizedNasIdentifierEl = document.getElementById(formName + "_dataSource_customizedNasIdentifier");
			if (customizedNasIdentifierEl.value.length == 0) {
				showServiceSettingsContent();
		        hm.util.reportFieldError(customizedNasIdentifierEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.nasIdentifier.customized" /></s:param></s:text>');
		        customizedNasIdentifierEl.focus();
		        return false;
			}
		}
	}
	return true;
}

function validateVPNServer(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') && deviceType == DEVICE_TYPE_HIVEAP){
		var vpnMarkElement = document.getElementById(formName + "_dataSource_vpnMark");
		if(vpnMarkElement.value == VPN_MARK_SERVER){
			// must be 11n ap
			if(!isVpnServerAvaliable()){
				hm.util.reportFieldError(vpnMarkElement, '<s:text name="error.hiveAp.vpnServer.hiveApModel"></s:text>');
				showServiceSettingsContent();
				return false;
			}
			// couldn't be dhcp client
			//var dhcpboxElement = document.getElementById(formName + "_dataSource_dhcp");
			var dhcpboxElement = document.getElementById(formName + "_mgt0NetworkType1");
			if(!dhcpboxElement.checked){
				hm.util.reportFieldError(dhcpboxElement, '<s:text name="error.hiveAp.vpnServer.term"></s:text>');
				showNetworkSettingsContent();
				hm.util.show("mgt0DhcpSettings");
				return false;
			}
		}
	}
	return true;
}

function validateVPNClient(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	var deviceMode = document.getElementById(formName + "_dataSource_hiveApModel").value;
	if((operation == 'create2' || operation == 'update2') && deviceType == DEVICE_TYPE_HIVEAP){
		var vpnMarkElement = document.getElementById(formName + "_dataSource_vpnMark");
		if(vpnElementClient.value == VPN_MARK_CLIENT){
			// must be 11n ap
			if(deviceMode == MODEL_AG20 || deviceMode == VPN_GATEWAY || deviceMode == CVG_APPLIANCE){
				hm.util.reportFieldError(vpnMarkElement, '<s:text name="error.hiveAp.vpnClient.hiveApModel"></s:text>');
				showServiceSettingsContent();
				return false;
			}
		}
	}
	return true;
}

var staticIp = "";
var staticMask = "";
var defaultIp = "";
var defaultMask = "";
function dhcpEvent(isChecked,isRadio){
	var dhcpEl = document.getElementById(formName + "_dataSource_dhcp");
	var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
	var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
	var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
	var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
	var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
	var addressOnlyEl = document.getElementById(formName + "_dataSource_addressOnly");
	var gatewayLbTdEl = document.getElementById("gatewayLbTd");
	var gatewayTdEl = document.getElementById("gatewayTd");
	var dhcpTimeoutLbTdEl = document.getElementById("dhcpTimeoutLbTd");
	var dhcpTimeoutTdEl = document.getElementById("dhcpTimeoutTd");
	var addressOnlyLbTdEl = document.getElementById("addressOnlyLbTd");
	var addressOnlyTdEl = document.getElementById("addressOnlyTd");
	var dhcpLabelEl = document.getElementById("dhcpLabel");
//	ipAddressElement.disabled = !isChecked;
//	netmaskElement.disabled = !isChecked;
	gatewayElement.disabled = isChecked;
	dhcpTimeoutEl.disabled = !isChecked;
	addressOnlyEl.disabled = !isChecked;

	gatewayLbTdEl.style.display = !isChecked? "" : "none";
	gatewayTdEl.style.display = !isChecked? "" : "none";
	dhcpTimeoutLbTdEl.style.display = isChecked? "" : "none";
	dhcpTimeoutTdEl.style.display = isChecked? "" : "none";
	addressOnlyLbTdEl.style.display = isChecked? "" : "none";
	addressOnlyTdEl.style.display = isChecked? "" : "none";

	dhcpEl.value = isChecked;
	dhcpFallbackEl.value = !isChecked;
	if(isRadio){
		staticIp = ipAddressElement.value;
		staticMask = netmaskElement.value;
	}
	ipAddressElement.value = defaultIp;
	netmaskElement.value = defaultMask;
	ipAddressElement.setAttribute("title", "<s:text name='hiveAp.default.ipAddress.note' />");
	netmaskElement.setAttribute("title","<s:text name='hiveAp.default.netmask.note' />");
	dhcpLabelEl.style.width = "250px";
	dhcpLabelEl.innerHTML = "<s:text name='hiveAp.default.ipAddress' />";
	dhcpTimeoutLbTdEl.style.width = "250px";
}

function dhcpFallbackEvent(isChecked){
	var dhcpEl = document.getElementById(formName + "_dataSource_dhcp");
	var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
	var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
	var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
	var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
	var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
	var addressOnlyEl = document.getElementById(formName + "_dataSource_addressOnly");
	var gatewayLbTdEl = document.getElementById("gatewayLbTd");
	var gatewayTdEl = document.getElementById("gatewayTd");
	var dhcpTimeoutLbTdEl = document.getElementById("dhcpTimeoutLbTd");
	var dhcpTimeoutTdEl = document.getElementById("dhcpTimeoutTd");
	var addressOnlyLbTdEl = document.getElementById("addressOnlyLbTd");
	var addressOnlyTdEl = document.getElementById("addressOnlyTd");
	var dhcpLabelEl = document.getElementById("dhcpLabel");
//	ipAddressElement.disabled = !isChecked;
//	netmaskElement.disabled = !isChecked;
	gatewayElement.disabled = !isChecked;
	dhcpTimeoutEl.disabled = !isChecked;
	addressOnlyEl.disabled = !isChecked;
	gatewayLbTdEl.style.display = isChecked? "" : "none";
	gatewayTdEl.style.display = isChecked? "" : "none";
	dhcpTimeoutLbTdEl.style.display = isChecked? "" : "none";
	dhcpTimeoutTdEl.style.display = isChecked? "" : "none";
	addressOnlyLbTdEl.style.display = isChecked? "" : "none";
	addressOnlyTdEl.style.display = isChecked? "" : "none";

	dhcpEl.value = isChecked;
	dhcpFallbackEl.value = isChecked;

	if(preNetworkType == USE_DHCP_WITHOUTFALLBACK){
		defaultIp = ipAddressElement.value;
		defaultMask = netmaskElement.value;
		ipAddressElement.value = staticIp;
		netmaskElement.value = staticMask;
	}
	ipAddressElement.setAttribute("title", "");
	netmaskElement.setAttribute("title","");
	dhcpLabelEl.style.width = "120px";
	dhcpLabelEl.innerHTML = "<s:text name='hiveAp.mgt0Interface.dhcpFallback' />";
	dhcpTimeoutLbTdEl.style.width="120px";

}

function staticEvent(isChecked){
	var dhcpEl = document.getElementById(formName + "_dataSource_dhcp");
	var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
	var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
	var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
	var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
	var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
	var addressOnlyEl = document.getElementById(formName + "_dataSource_addressOnly");
	var gatewayLbTdEl = document.getElementById("gatewayLbTd");
	var gatewayTdEl = document.getElementById("gatewayTd");
	var dhcpTimeoutLbTdEl = document.getElementById("dhcpTimeoutLbTd");
	var dhcpTimeoutTdEl = document.getElementById("dhcpTimeoutTd");
	var addressOnlyLbTdEl = document.getElementById("addressOnlyLbTd");
	var addressOnlyTdEl = document.getElementById("addressOnlyTd");
	var dhcpLabelEl = document.getElementById("dhcpLabel");
//	ipAddressElement.disabled = !isChecked;
//	netmaskElement.disabled = !isChecked;

	dhcpEl.value = !isChecked;
	dhcpFallbackEl.value = !isChecked;

	gatewayElement.disabled = !isChecked;
	dhcpTimeoutEl.disabled = isChecked;
	addressOnlyEl.disabled = isChecked;
	gatewayLbTdEl.style.display = isChecked? "" : "none";
	gatewayTdEl.style.display = isChecked? "" : "none";
	dhcpTimeoutLbTdEl.style.display = !isChecked? "" : "none";
	dhcpTimeoutTdEl.style.display = !isChecked? "" : "none";
	addressOnlyLbTdEl.style.display = !isChecked? "" : "none";
	addressOnlyTdEl.style.display = !isChecked? "" : "none";

	if(preNetworkType == USE_DHCP_WITHOUTFALLBACK){
		defaultIp = ipAddressElement.value;
		defaultMask = netmaskElement.value;
		ipAddressElement.value = staticIp;
		netmaskElement.value = staticMask;
	}
	ipAddressElement.setAttribute("title", "");
	netmaskElement.setAttribute("title","");
	dhcpLabelEl.style.width = "100px";
	dhcpLabelEl.innerHTML = "<s:text name='hiveAp.ipAddress' />";

}

function radioMgt0NetworkType(type,isRadio){
	switch(parseInt(type)){
	case USE_STATIC_IP:
		staticEvent(true);
		break;
	case USE_DHCP_FALLBACK:
		dhcpFallbackEvent(true);
		break;
	case USE_DHCP_WITHOUTFALLBACK:
		dhcpEvent(true,isRadio);
		break;
	}
	preNetworkType = type;
}

function radioMgt0NetworkTypeOnload(type,isRadio){
	var defaultIp = document.getElementById(formName + "_dataSource_cfgIpAddress").value;
	var defaultMask = document.getElementById(formName + "_dataSource_cfgNetmask").value;
	var defaultGateway = document.getElementById(formName + "_dataSource_cfgGateway").value;
	if(!defaultIp){
		return;
	}
	radioMgt0NetworkType(type,isRadio);
	document.getElementById(formName + "_dataSource_cfgIpAddress").value = defaultIp;
	document.getElementById(formName + "_dataSource_cfgNetmask").value = defaultMask;
	document.getElementById(formName + "_dataSource_cfgGateway").value = defaultGateway;
}

function clickDtlsBox() {
	var dtlsBoxElement = document.getElementById("changePassPhrase");
	var newDtlsElement = document.getElementById("newDtls");
	var confirmDtlsElement = document.getElementById("confirmDtls");
	var newDtls_textElement = document.getElementById("newDtls_text");
	var confirmDtls_textElement = document.getElementById("confirmDtls_text");
	var toggleCbxElement = document.getElementById("chkToggleDisplay_2");
	if(dtlsBoxElement.checked){
		if(toggleCbxElement.checked){
			newDtlsElement.disabled = false;
			confirmDtlsElement.disabled = false;
		}else{
			newDtls_textElement.disabled = false;
			confirmDtls_textElement.disabled = false;
		}
		toggleCbxElement.disabled = false;
	}else{
		if(toggleCbxElement.checked){
			newDtlsElement.disabled = true;
			confirmDtlsElement.disabled = true;
		}else{
			newDtls_textElement.disabled = true;
			confirmDtls_textElement.disabled = true;
		}
		toggleCbxElement.disabled = true;
	}
}

function selectRadioProfile(cb) {
	if (cb.value > 0 ) {
		if(cb.id == (formName + "_wifi0RadioProfile")){
			var operationMode = getWifi0OperationMode();
			getWifi0Channels(cb.value, operationMode);
		}else if(cb.id == (formName + "_wifi1RadioProfile")){
			var operationMode = getWifi1OperationMode();
			getWifi1Channels(cb.value, operationMode);
		}
	}
}

function newWifi0RadioProfile(){
	<s:if test="%{!jsonMode}">
		// create a new radio profile
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "bg";//default radio type
		}else{
			document.forms[formName].radioType.value = "ng";//default radio type
		}
		var radioNameObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioName");
		if(radioNameObj){
			document.forms[formName].radioProfileName.value = radioNameObj.value;
		}
		var radioModeObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioMode");
		if(radioModeObj){
			document.forms[formName].radioProfileMode.value = radioModeObj.options[radioModeObj.selectedIndex].value;
		}
		var channelWidthObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth");
		if(channelWidthObj){
			document.forms[formName].radioProfileChannelWidth.value = channelWidthObj.options[channelWidthObj.selectedIndex].value;
		}
	</s:if>
	<s:else>
		hideSimpleCreateSection('wifi0RadioProfileCreateSection');
	</s:else>
	submitAction('newWifi0RadioProfile');
}

function newWifi1RadioProfile(){
	<s:if test="%{!jsonMode}">
		// create a new radio profile
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "a";//default radio type
		}else{
			document.forms[formName].radioType.value = "na";//default radio type
		}
		var radioNameObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioName");
		if(radioNameObj){
			document.forms[formName].radioProfileName.value = radioNameObj.value;
		}
		var radioModeObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioMode");
		if(radioModeObj){
			document.forms[formName].radioProfileMode.value = radioModeObj.options[radioModeObj.selectedIndex].value;
		}
		var channelWidthObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth");
		if(channelWidthObj){
			document.forms[formName].radioProfileChannelWidth.value = channelWidthObj.options[channelWidthObj.selectedIndex].value;
		}
	</s:if>
	<s:else>
		hideSimpleCreateSection('wifi1RadioProfileCreateSection');
	</s:else>
	submitAction('newWifi1RadioProfile');
}

function editWifi0RadioProfile(){
	<s:if test="%{!jsonMode}">
		// edit radio profile
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "bg";//default radio type
		}else{
			document.forms[formName].radioType.value = "ng";//default radio type
		}
	</s:if>
	submitAction('editWifi0RadioProfile');
}

function editWifi1RadioProfile(){
	<s:if test="%{!jsonMode}">
		// edit radio profile
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "a";//default radio type
		}else{
			document.forms[formName].radioType.value = "na";//default radio type
		}
	</s:if>
	submitAction('editWifi1RadioProfile');
}

function showCreateSection(type) {
    if(type=='dynamic'){
       hm.util.hide('newButton');
	   hm.util.show('createButton');
	   hm.util.show('createSection');
	   // to fix column overlap issue on certain browsers
	   var trh = document.getElementById('headerSection');
	   var trc = document.getElementById('createSection');
	   var table = trh.parentNode;
	   table.removeChild(trh);
	   table.insertBefore(trh, trc);
	   document.getElementById("expanding_dynamic").value="true";
    }else if(type=='static'){
       hm.util.hide('newButtonStatic');
	   hm.util.show('createButtonStatic');
	   hm.util.show('createSectionStatic');
	   // to fix column overlap issue on certain browsers
	   var trh = document.getElementById('headerSectionStatic');
	   var trc = document.getElementById('createSectionStatic');
	   var table = trh.parentNode;
	   table.removeChild(trh);
	   table.insertBefore(trh, trc);
	   document.getElementById("expanding_static").value="true";
    }else if(type=='ip'){
       hm.util.hide('newButtonIp');
 	   hm.util.show('createButtonIp');
 	   hm.util.show('createSectionIp');
 	   // to fix column overlap issue on certain browsers
 	   var trh = document.getElementById('headerSectionIp');
 	   var trc = document.getElementById('createSectionIp');
 	   var table = trh.parentNode;
 	   table.removeChild(trh);
 	   table.insertBefore(trh, trc);
 	   document.getElementById("expanding_ip").value="true";
	 }else if(type=='vlanid'){
       hm.util.hide('newButtonVlanId');
 	   hm.util.show('createButtonVlanId');
 	   hm.util.show('createSectionVlanId');
 	   // to fix column overlap issue on certain browsers
 	   var trh = document.getElementById('headerSectionVlanId');
 	   var trc = document.getElementById('createSectionVlanId');
 	   var table = trh.parentNode;
 	   table.removeChild(trh);
 	   table.insertBefore(trh, trc);
 	   document.getElementById("expanding_vlanid").value="true";
     }else if(type=='virtualConnect'){
       hm.util.hide('newButtonVirtualConnect');
   	   hm.util.show('createButtonVirtualConnect');
   	   hm.util.show('createSectionVirtualConnect');
   	   // to fix column overlap issue on certain browsers
   	   var trh = document.getElementById('headerSectionVirtualConnect');
   	   var trc = document.getElementById('createSectionVirtualConnect');
   	   var table = trh.parentNode;
   	   table.removeChild(trh);
   	   table.insertBefore(trh, trc);
   	   document.getElementById("expanding_virtualConnect").value="true";
     } else if (type=='staticRoutes') {
       hm.util.hide('newButtonStaticRoutes');
  	   hm.util.show('createButtonStaticRoutes');
  	   hm.util.show('createSectionStaticRoutes');
  	   // to fix column overlap issue on certain browsers
  	   var trh = document.getElementById('headerSectionStaticRoutes');
  	   var trc = document.getElementById('createSectionStaticRoutes');
  	   var table = trh.parentNode;
  	   table.removeChild(trh);
  	   table.insertBefore(trh, trc);
  	   document.getElementById("expanding_staticRoutes").value="true";
   	 } else if (type=='intNetwork'){
		hm.util.hide('newButtonIntNetwork');
		hm.util.show('createButtonIntNetwork');
		hm.util.show('createSectionIntNetwork');
		// to fix column overlap issue on certain browsers
  	   var trh = document.getElementById('headerSectionIntNetwork');
  	   var trc = document.getElementById('createSectionIntNetwork');
  	   var table = trh.parentNode;
  	   table.removeChild(trh);
  	   table.insertBefore(trh, trc);
  	   document.getElementById("expanding_intNetwork").value="true";
   	 } else if(type=='brStaticRouting') {
   		hm.util.hide('newButtonBrStaticRouting');
		hm.util.show('createButtonBrStaticRouting');
		hm.util.show('createSectionBrStaticRouting');
		// to fix column overlap issue on certain browsers
  	    var trh = document.getElementById('headerSectionBrStaticRouting');
  	    var trc = document.getElementById('createSectionBrStaticRouting');
  	    var table = trh.parentNode;
  	    table.removeChild(trh);
  	    table.insertBefore(trh, trc);
  	    document.getElementById("expanding_brStaticRouting").value="true";
   	 }
}
function hideCreateSection(type) {
    if(type=='dynamic'){
        hm.util.hide('createButton');
        hm.util.show('newButton');
        hm.util.hide('createSection');
        document.getElementById("expanding_dynamic").value="false";
    }else if(type=='static'){
        hm.util.hide('createButtonStatic');
        hm.util.show('newButtonStatic');
        hm.util.hide('createSectionStatic');
        document.getElementById("expanding_static").value="false";
    }else if(type=='ip'){
        hm.util.hide('createButtonIp');
        hm.util.show('newButtonIp');
        hm.util.hide('createSectionIp');
        document.getElementById("expanding_ip").value="false";
    }else if(type=='vlanid'){
  		hm.util.hide('createButtonVlanId');
        hm.util.show('newButtonVlanId');
        hm.util.hide('createSectionVlanId');
        document.getElementById("expanding_vlanid").value="false";
    }else if(type=='virtualConnect'){
        hm.util.hide('createButtonVirtualConnect');
        hm.util.show('newButtonVirtualConnect');
        hm.util.hide('createSectionVirtualConnect');
        document.getElementById("expanding_virtualConnect").value="false";
    } else if(type=='staticRoutes') {
        hm.util.hide('createButtonStaticRoutes');
        hm.util.show('newButtonStaticRoutes');
        hm.util.hide('createSectionStaticRoutes');
        document.getElementById("expanding_staticRoutes").value="false";
    } else if(type=='intNetwork') {
        hm.util.hide('createButtonIntNetwork');
        hm.util.show('newButtonIntNetwork');
        hm.util.hide('createSectionIntNetwork');
        document.getElementById("expanding_intNetwork").value="false";
    } else if(type=='brStaticRouting'){
    	 hm.util.hide('createButtonBrStaticRouting');
         hm.util.show('newButtonBrStaticRouting');
         hm.util.hide('createSectionBrStaticRouting');
         document.getElementById("expanding_brStaticRouting").value="false";
    }
}

function toggleCheckAllStaticRoutes(checkBox){
	var checkBoxs = document.getElementsByName('staticRouteIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllDynamicRoutes(checkBox){
	var checkBoxs = document.getElementsByName('dynamicRouteIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllIpRoutes(checkBox){
	var checkBoxs = document.getElementsByName('ipRouteIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllRoutingProfilesStaticRoutes(checkBox) {
	var checkBoxs = document.getElementsByName('routingProfilesStaticRoutesIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllIntNetwork(checkBox) {
	var checkBoxs = document.getElementsByName('intNetworkIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllBrStaticRouting(checkBox) {
	var checkBoxs = document.getElementsByName('brStaticRouteingIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllMultipleVlan(checkBox){
	var checkBoxs = document.getElementsByName('multiplevlanIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function toggleCheckAllVirtualConnects(checkBox){
	var checkBoxs = document.getElementsByName('virtualConnectIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}

function getWifi0OperationMode(){
	var radioConfigTypeEls = document.getElementsByName("dataSource.radioConfigType");
	var radioConfigType = 0;
	for(var i=0; i<radioConfigTypeEls.length; i++){
		var el = radioConfigTypeEls[i];
		if(el.checked){
			radioConfigType = el.value;
			break;
		}
	}
	switch(parseInt(radioConfigType)){
	case RADIO_MODE_ACCESS_ALL:
	case RADIO_MODE_ACCESS_DUAL:
		return OPERATION_MODE_ACCESS;
	case RADIO_MODE_ACCESS_ONE:
	case RADIO_MODE_BRIDGE:
		return isWifi1Available()?OPERATION_MODE_ACCESS:OPERATION_MODE_BACKHAUL;
	case RADIO_MODE_CUSTOMIZE:
		var wifi0El;
		if(document.getElementById(formName + "_dataSource_deviceType").value == DEVICE_TYPE_BRANCH_ROUTER){
			wifi0El = document.getElementById("wifi0ModeBR");
		}else{
			wifi0El = document.getElementById(formName + "_dataSource_wifi0_operationMode");
		}
		return wifi0El.value;
	case RADIO_MODE_ACCESS_WAN:
		return OPERATION_MODE_WAN_ACCESS;
	}
}

function getWifi1OperationMode(){
	var radioConfigTypeEls = document.getElementsByName("dataSource.radioConfigType");
	var radioConfigType = 0;
	for(var i=0; i<radioConfigTypeEls.length; i++){
		var el = radioConfigTypeEls[i];
		if(el.checked){
			radioConfigType = el.value;
			break;
		}
	}
	switch(parseInt(radioConfigType)){
	case RADIO_MODE_ACCESS_ALL:
		return OPERATION_MODE_ACCESS;
	case RADIO_MODE_ACCESS_ONE:
	case RADIO_MODE_BRIDGE:
		return OPERATION_MODE_BACKHAUL;
	case RADIO_MODE_CUSTOMIZE:
		var wifi1El;
		if(document.getElementById(formName + "_dataSource_deviceType").value == DEVICE_TYPE_BRANCH_ROUTER){
			wifi1El = document.getElementById("wifi0ModeBR");
		}else{
			wifi1El = document.getElementById(formName + "_dataSource_wifi1_operationMode");
		}
		return wifi1El.value;
	case RADIO_MODE_ACCESS_DUAL:
		return OPERATION_MODE_DUAL;
	}
}

function wanOrderPreCheck( ){
	var eth0=Get("eth0WanSelectListID");
 	if(Get("eth1strTd").innerHTML == 'LAN')
		{
		var eth1=Get("eth1WanSelectListID");
		eth1.options.length = 0;
		}
 	if(Get("eth2strTd").innerHTML == 'LAN')
	{
	    var eth2=Get("eth2WanSelectListID");
	    eth2.options.length = 0;
	}
 	if(Get("eth3strTd").innerHTML == 'LAN')
	{
		var eth3=Get("eth3WanSelectListID");
	    eth3.options.length = 0;
	}
 	if(Get("eth4strTd").innerHTML == 'LAN')
	{
		var eth4=Get("eth4WanSelectListID");
	    eth4.options.length = 0;
	}
 	if(Get("usbstrTd").innerHTML == 'LAN')
	{
		var usb=Get("usbWanSelectListID");
	    usb.options.length = 0;
	}
 	if(Get("wifi0strTd").innerHTML == 'LAN')
	{
		var wifi0=Get("wifi0WanSelectListID");
		wifi0.options.length = 0;
	}
}

function setWanObj(wanCombobox) {
	if (wanCombobox.value == 1) {
		primaryWanObj = wanCombobox;
	}
	else if (wanCombobox.value == 2) {
		backup1WanObj = wanCombobox;
	}
    else if (wanCombobox.value == 3) {
		backup2WanObj = wanCombobox;
	}

}

function adjustWanOrder(){
	var usbAsCellularFlag = '<s:property value="dataSource.usbAsCellularModem"/>';
	var usb = Get("usbWanSelectListID");
	var eth0 = Get("eth0WanSelectListID");
	var eth1 = Get("eth1WanSelectListID");
	var eth2 = Get("eth2WanSelectListID");
	var eth3 = Get("eth3WanSelectListID");
	var eth4 = Get("eth4WanSelectListID");
	var wifi0 = Get("wifi0WanSelectListID");
	if(usbAsCellularFlag == 'true' && usb.value == 4) { //disabled
		//var wanNum = parseInt(eth0.length);
		if (primaryWanObj == null) { //previous usb is primary
			if (backup1WanObj != null) { //upgrade backup1
				backup1WanObj.value = 1;
			}
			if (backup2WanObj != null) { //upgrade backup2
				backup2WanObj.value = 2;
			}
		}
		if (backup2WanObj == null) { //previous usb is backup1WanObj
			if (backup2WanObj != null) { //upgrade backup2
				backup2WanObj.value = 2;
			}
		}

	}
}


function wanOrderTrigger(wanCombobox)
{
	primaryWanObj = null;
	backup1WanObj = null;
	backup2WanObj = null;
	var oldVaule = 1;
	var newValue=Get(wanCombobox).value;
	var eth0=Get("eth0WanSelectListID");
	setWanObj(eth0);

	
	var wanNum=parseInt(eth0.length);
	if(wanNum==1){
		return;
	}
	var valueArray= new Array();
	//
	for(var v=0;v<wanNum;v++){
		valueArray[v]=v+1;
	}
	var arrayObj = new Array();
	arrayObj.push(eth0);
	if($("#eth1strTd").html().trim()=="WAN")
	{
		var eth1=Get("eth1WanSelectListID");
		arrayObj.push(eth1);
		setWanObj(eth1);
	}
	if($("#eth2strTd").html().trim()=="WAN")
{
		var eth2=Get("eth2WanSelectListID");
		arrayObj.push(eth2);
		setWanObj(eth2);
}
	if($("#eth3strTd").html().trim()=="WAN")
{
		var eth3=Get("eth3WanSelectListID");
		arrayObj.push(eth3);
		setWanObj(eth3);
}
	if($("#eth4strTd").html().trim()=="WAN")
{
		var eth4=Get("eth4WanSelectListID");
		arrayObj.push(eth4);
		setWanObj(eth4);
}
	if($("#usbstrTd").html().trim() == 'WAN')
{
		var usb=Get("usbWanSelectListID");
		arrayObj.push(usb);
		setWanObj(usb);
}
	if($("#wifi0strTd").html().trim() == 'WAN')
	{
			var wifi0=Get("wifi0WanSelectListID");
			arrayObj.push(wifi0);
			setWanObj(wifi0);
	}
	
	var isExist = false;
	for(var i=0;i<wanNum;i++){
		isExist=false;
		for(var j=0;j<arrayObj.length;j++){
			if(arrayObj[j].value == (i+1)){
				
				isExist=true;
				break;
			}
		
		}
		if(isExist == false){
			oldVaule=i+1;
			break;
		}
	}

	for(var j=0;j<arrayObj.length;j++){
		if(arrayObj[j].value == newValue && arrayObj[j].id != wanCombobox){
			selectorForWan(arrayObj[j].id,oldVaule);
			break;
		}
	
	}
	
	//hide usbConnectionModelTr if usb set to primary
	if($("#usbstrTd").html().trim() == 'WAN')
    {
		var usb=$("#usbWanSelectListID").val();
		if(usb !=null && parseInt(usb) == 1){
			$("#usbConnectionModelTr").hide();
		}else{
			$("#usbConnectionModelTr").show();
		}
    }
	else{
		$("#usbConnectionModelTr").show();
	}
	
	
}


function selectorForWan(id,wanOrder)
{
	if(parseInt(wanOrder) == 1){
		var selectVaule ='<s:text name="hiveAP.interface.wan.priority.1" />'; 
	}else if(parseInt(wanOrder) == 2){
		var selectVaule ='<s:text name="hiveAP.interface.wan.priority.2" />'; 
	}else if(parseInt(wanOrder) == 3){
		var selectVaule ='<s:text name="hiveAP.interface.wan.priority.3" />'; 
	}else {
		var selectVaule ='<s:text name="hiveAP.interface.wan.priority.1" />'; 
	}
	
//	selector(id,selectVaule);
	$("#"+id).children().eq(wanOrder-1).attr({selected:true});
}


function initWanOrderOnClientSide( ){
	var deviceTypeEle = document.getElementById(formName + "_dataSource_deviceType");
	var deviceTypeEleValue = deviceTypeEle.value;

	if(DEVICE_TYPE_BRANCH_ROUTER == deviceTypeEleValue){

	var eth0=Get("eth0WanSelectListID");
	var wanOrder = 1;
	var wanNum=parseInt(eth0.length);
	selectorForWan("eth0WanSelectListID",wanOrder);
	wanOrder++;
	
	var wanPrority1;
	var wanNum=parseInt(eth0.length);
	if(wanNum==1){
		return;
	}
	var valueArray= new Array();
	
	var valueArrayIndex =0;
	for(var v=0;v<wanNum;v++){
		valueArray[v]=v+1;
	}

	valueArray[valueArrayIndex]=$("#eth0_wan_priority").html().trim();
	valueArrayIndex++;
	
	if($("#eth1strTd").html().trim()=="WAN")
	{
		valueArray[valueArrayIndex]=$("#eth1_wan_priority").html().trim();
		valueArrayIndex++;
	}
	if($("#eth2strTd").html().trim()=="WAN")
{
		valueArray[valueArrayIndex]=$("#eth2_wan_priority").html().trim();
		valueArrayIndex++;
}
	if($("#eth3strTd").html().trim()=="WAN")
{
		valueArray[valueArrayIndex]=$("#eth3_wan_priority").html().trim();
		valueArrayIndex++;
}
	if($("#eth4strTd").html().trim()=="WAN")
{
		valueArray[valueArrayIndex]=$("#eth4_wan_priority").html().trim();
		valueArrayIndex++;
}
	if($("#usbstrTd").html().trim() == 'WAN') {
		if  (Get("br_usb_setting").style.display != "none") { //not hidden
			valueArray[valueArrayIndex]=$("#usb_wan_priority").html().trim();
			valueArrayIndex++;
		}
    }
	if($("#wifi0strTd").html().trim() == 'WAN')
{
		valueArray[valueArrayIndex]=$("#wifi0_wan_priority").html().trim();
		valueArrayIndex++;
}
	
	valueArray.sort(function(a,b){return a-b;});
	
	for(var v=0;v<wanNum;v++){
	
		if(valueArray[v]==$("#eth0_wan_priority").html().trim()){
			selectorForWan("eth0WanSelectListID",v+1);
			break;
		}
	}
	
	
 	if($("#eth1strTd").html().trim()=="WAN")
		{
 		for(var v=0;v<wanNum;v++){
 			
 			if(valueArray[v]==$("#eth1_wan_priority").html().trim()){
 				selectorForWan("eth1WanSelectListID",v+1);
 				break;
 			}
 		}
		}
 	if($("#eth2strTd").html().trim()=="WAN")
	{
 		for(var v=0;v<wanNum;v++){
 			
 			if(valueArray[v]==$("#eth2_wan_priority").html().trim()){
 				selectorForWan("eth2WanSelectListID",v+1);
 				break;
 			}
 		}
	}
 	if($("#eth3strTd").html().trim()=="WAN")
	{
 		for(var v=0;v<wanNum;v++){
 			
 			if(valueArray[v]==$("#eth3_wan_priority").html().trim()){
 				selectorForWan("eth3WanSelectListID",v+1);
 				break;
 			}
 		}
	}
 	if($("#eth4strTd").html().trim()=="WAN")
	{
 		for(var v=0;v<wanNum;v++){
 			
 			if(valueArray[v]==$("#eth4_wan_priority").html().trim()){
 				selectorForWan("eth4WanSelectListID",v+1);
 				break;
 			}
 		}
	}
 	if($("#usbstrTd").html().trim() == 'WAN')
	{
 		if  (Get("br_usb_setting").style.display != "none") { //not hidden
			for(var v=0;v<wanNum;v++){
				if(valueArray[v]==$("#usb_wan_priority").html().trim()){
					selectorForWan("usbWanSelectListID",v+1);
					break;
				}
		    }
		}
	}

 	if($("#wifi0strTd").html().trim() == 'WAN')
	{
            for(var v=0;v<wanNum;v++){
			if(valueArray[v]==$("#wifi0_wan_priority").html().trim()){
				selectorForWan("wifi0WanSelectListID",v+1);
				break;
			}
		}
	}
 	
 	if($("#usbstrTd").html().trim() == 'WAN')
    {
		var usb=$("#usbWanSelectListID").val();
		if(usb !=null && parseInt(usb) == 1){
			$("#usbConnectionModelTr").hide();
		}else{
			$("#usbConnectionModelTr").show();
		}
    }
	else{
		$("#usbConnectionModelTr").show();
	}
 	
	}
 	
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;

	if(selectApModel == BR100 && deviceType == DEVICE_TYPE_HIVEAP){
		$("#eth0strTd").html("Backhaul");
		$("#eth0TdDiv").hide();
		$("#eth0ConnectTdDiv").hide();
		$("#eth0_enableNat").hide();
		$("#eth0StaticIP").hide();
		$("#eth0DefaultGateway").hide();
		$("#eth0_disablePortForwarding").hide();
		$("#eth0_voip_detail").hide();
	
	}

}

function validatePPPoe(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	var apModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	if(('create2' == operation || 'update2' == operation) && (deviceType == DEVICE_TYPE_BRANCH_ROUTER)) {
		var eth0=Get("eth0WanSelectListID");
		if($("#branchRouterEth0_connectionType").val()==DEVICE_CONNECT_PPPOE){
					if ($("#eth0_pppoeAuthProfile").val()<1) {
						showNetworkSettingsContent();
						hm.util.reportFieldError(Get("error_message_eth0andgateway"), '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.brRouter.port.settings.pppoeAuth" /></s:param></s:text>');
						$("#eth0_pppoeAuthProfile").focus();
		            	return false;
					}
	}
	
	}
	return true;
	
}

function validateWanOrder(operation){
 	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(('create2' == operation || 'update2' == operation) && (deviceType == DEVICE_TYPE_BRANCH_ROUTER)) {

	var eth0=Get("eth0WanSelectListID");
	var chooseLength=eth0.length;
	
	var sum=parseInt(eth0.value);
 	if(Get("eth1strTd").innerHTML == 'WAN')
	{
	var eth1=Get("eth1WanSelectListID");
	sum+=parseInt(eth1.value);
	}
	if(Get("eth2strTd").innerHTML == 'WAN')
{
    var eth2=Get("eth2WanSelectListID");
    sum+=parseInt(eth2.value);
}
	if(Get("eth3strTd").innerHTML == 'WAN')
{
	var eth3=Get("eth3WanSelectListID");
	sum+=parseInt(eth3.value);
}
	if(Get("eth4strTd").innerHTML == 'WAN')
{
	var eth4=Get("eth4WanSelectListID");
	sum+=parseInt(eth4.value);
}
	if($("#usbstrTd").html().trim() == 'WAN') {
		if  (Get("br_usb_setting").style.display != "none") { //not hidden
			var usb=Get("usbWanSelectListID");
			sum+=parseInt(usb.value);
		}
    }
	if($("#wifi0strTd").html().trim() == 'WAN')
	{
		var wifi0=Get("wifi0WanSelectListID");
		sum+=parseInt(wifi0.value);
	}
	if( chooseLength == 1){
		if(sum != 1){
			hm.util.reportFieldError(eth0, '<s:text name="device.config.intereth.priority.warn.order.error" />');
			eth0.focus();
			return false;
		}
	}else if( chooseLength == 2){
		if(sum != 3){
			hm.util.reportFieldError(eth0, '<s:text name="device.config.intereth.priority.warn.order.error" />');
			eth0.focus();
			return false;
		}
	}else if(chooseLength == 3){
		if(sum != 6){
			hm.util.reportFieldError(eth0, '<s:text name="device.config.intereth.priority.warn.order.error" />');
			eth0.focus();
			return false;
		}
	}
	} 
	return true;
	
}

function validateBRAdvertiseRoute(operation){
	if(operation != 'create2' && operation != 'update2'){
		return true;
	}

	var allAdvertiseRoutes = document.getElementsByName("advertiseCvg");
	if(!allAdvertiseRoutes){
		return true;
	}
	var routeCounts = 0;
	for(var i=0; i<allAdvertiseRoutes.length; i++){
		var el = allAdvertiseRoutes[i];
		if(el.checked){
			routeCounts ++;
		}
	}

	if(routeCounts > BR_MAX_ROUTE_COUNT && $("#"+formName+"_dataSource_deviceType").val() == DEVICE_TYPE_BRANCH_ROUTER){
		var message = '<s:text name="error.be.config.create.br.max.advertiseRoute">
						<s:param><%=AhInterface.BR_MAX_ROUTE_COUNT%></s:param>
					</s:text>'
		var eleObj = document.getElementById("error_message_static_route");
		hm.util.reportFieldError(eleObj, message);
		showBRStaticRouting();
		showHideContent("networkSettings", "none");
		eleObj.focus();
		return false;
	}else{
		return true;
	}
}

function validateTxRetrtRate(operation){
	if(operation == 'create2' || operation == 'update2'){
		var deviceTxRetryElement = document.getElementById(formName + "_dataSource_deviceTxRetry");
		var clientTxRetryElement = document.getElementById(formName + "_dataSource_clientTxRetry");
		
		if (deviceTxRetryElement.value.length == 0) {			
			showAdvancedSettingsContent();
			hm.util.reportFieldError(deviceTxRetryElement, '<s:text name="error.requiredField"><s:param><s:text name="config.device.reportSettings.ifTxRetry.device" /></s:param></s:text>');
			deviceTxRetryElement.focus();
			return false;
		}
		var devicemessage = hm.util.validateIntegerRange(deviceTxRetryElement.value, '<s:text name="config.device.reportSettings.ifTxRetry.device" />',
                <s:property value="1" />,
                <s:property value="100" />);
		if (devicemessage != null) {			
			showAdvancedSettingsContent();
			hm.util.reportFieldError(deviceTxRetryElement, devicemessage);
			deviceTxRetryElement.focus();
			return false;
		}
		
		if (clientTxRetryElement.value.length == 0) {
			showAdvancedSettingsContent();		
			hm.util.reportFieldError(clientTxRetryElement, '<s:text name="error.requiredField"><s:param><s:text name="config.device.reportSettings.ifTxRetry.client" /></s:param></s:text>');
			clientTxRetryElement.focus();
			return false;
		}
		var clientmessage = hm.util.validateIntegerRange(clientTxRetryElement.value, '<s:text name="config.device.reportSettings.ifTxRetry.client" />',
                <s:property value="1" />,
                <s:property value="100" />);
		if (clientmessage != null) {
			showAdvancedSettingsContent();
			hm.util.reportFieldError(clientTxRetryElement, clientmessage);
			clientTxRetryElement.focus();
			return false;
		}
	}
	return true;
}

function updateWanPortSelectList(wanPortNum) {
	
	var eth0=Get("eth0WanSelectListID");
	var eth1=Get("eth1WanSelectListID");
	var eth2=Get("eth2WanSelectListID");
	var eth3=Get("eth3WanSelectListID");
	var eth4=Get("eth4WanSelectListID");
	var usb=Get("usbWanSelectListID");
	var wifi0=Get("wifi0WanSelectListID");
	
	if(eth0!=null){
		updateWanList(eth0,wanPortNum);
	}
	if(eth1!=null){
		updateWanList(eth1,wanPortNum);
	}
	if(eth2!=null){
		updateWanList(eth2,wanPortNum);
	}
	if(eth3!=null){
		updateWanList(eth3,wanPortNum);
	}
	if(eth4!=null){
		updateWanList(eth4,wanPortNum);
	}
	if(usb!=null){
		updateWanList(usb,wanPortNum);
	}
	if(wifi0!=null){
		updateWanList(wifi0,wanPortNum);
	}
	
	
	initWanOrderOnClientSide();

}

function updateUsbWanList(usb, wanPortNum) {
	updateWanList(usb, wanPortNum);
	<s:if test="%{dataSource.usbAsCellularModem}">
		var newopt = document.createElement("OPTION"); 
		newopt.value = 4;  
		newopt.innerHTML ='Disabled'; 
		usb.appendChild(newopt);
	</s:if>
}

function updateWanList(combobox ,portNUm){

	combobox.options.length = 0;
	if(parseInt(portNUm)==1){
	var op1 = document.createElement("OPTION"); 
    op1.value = 1;  
    op1.innerHTML ='<s:text name="hiveAP.interface.wan.priority.1" />'; 
    combobox.appendChild(op1); 
	}else if(parseInt(portNUm)==2){
		var op1 = document.createElement("OPTION"); 
	    op1.value = 1;  
	    op1.innerHTML ='<s:text name="hiveAP.interface.wan.priority.1" />'; 
	    combobox.appendChild(op1);
	    
	    var op2 = document.createElement("OPTION"); 
	    op2.value = 2;  
	    op2.innerHTML ='<s:text name="hiveAP.interface.wan.priority.2" />'; 
	    combobox.appendChild(op2);
	    
	}else if(parseInt(portNUm)==3){
		var op1 = document.createElement("OPTION"); 
	    op1.value = 1;  
	    op1.innerHTML ='<s:text name="hiveAP.interface.wan.priority.1" />'; 
	    combobox.appendChild(op1);
	    
	    var op2 = document.createElement("OPTION"); 
	    op2.value = 2;  
	    op2.innerHTML ='<s:text name="hiveAP.interface.wan.priority.2" />'; 
	    combobox.appendChild(op2);
	    
	    var op3 = document.createElement("OPTION"); 
	    op3.value = 3;  
	    op3.innerHTML ='<s:text name="hiveAP.interface.wan.priority.3" />'; 
	    combobox.appendChild(op3);
	}
}
	
	

function deviceModeChange(mode, deviceType){
	if(deviceConfigChanged || modelFunctionChanged){
		var templateId = document.getElementById(formName + "_configTemplate").value;
	}else {
		var templateId = 0;
	}
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=deviceModeChange&dataSource.hiveApModel="+mode
			+"&dataSource.deviceType="+deviceType
			+"&tempConfigTemplate=" + templateId
			+"&oldDeviceType=" + $("#" + formName + "_oldDeviceType").val()
			+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : deviceModeChangeSucc}, null);
}

var deviceConfigChanged = false;
var deviceModelChangeIndex = 0;

var deviceTypeChangeTemp = null;
var deviceModelChangeTemp = null;

var deviceModeChangeSucc = function(o){
	eval("var details = " + o.responseText);

	if(!details.t){
		return;
	}

	var model = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var deviceTypeEle = document.getElementById(formName + "_dataSource_deviceType");
	var deviceTypeEleValue = deviceTypeEle.value;
	deviceTypeEle.length = 0;

	var jsonResults = details.deviceTypeList;
	for(var i=0; i<jsonResults.length; i++){
		var deviceKey = jsonResults[i].deviceKey;
		var deviceValue= jsonResults[i].deviceValue;
		addOption(deviceTypeEle, deviceValue,
				deviceKey, deviceKey == deviceTypeEleValue);
	}

	var jsonStyleList = details.devicePageStyleList;
	for(var i=0; i<jsonStyleList.length; i++){
		var id = jsonStyleList[i].id;
		var hide= jsonStyleList[i].hide;
		if(hide){
			hm.util.hide(id);
		}else{
			hm.util.show(id);
		}
	}
	
	
	<s:if test="%{dataSource.configTemplate.configType.wirelessEnabled && !dataSource.configTemplate.configType.RouterEnabled}">
		$("#vpnRuleTr").css("display", "block");
	</s:if>
	<s:else>
		$("#vpnRuleTr").css("display", "none");
		$("#"+formName + "_dataSource_vpnMark").attr("value", VPN_MARK_NONE);
	</s:else>

	/* var vpnMarkEle = document.getElementById(formName + "_dataSource_vpnMark");
	var vpnMarkEleValue = vpnMarkEle.value;
	vpnMarkEle.length = 0;
	var jsonResults = details.vpnServiceRoleList;
	for(var i=0; i<jsonResults.length; i++){
		var vpnKey = jsonResults[i].vpnKey;
		var vpnValue= jsonResults[i].vpnValue;
		addOption(vpnMarkEle, vpnValue,
				vpnKey, vpnKey == vpnMarkEleValue);
	} */
	
	//no matter what the selected device type is AP or Router, if 'Network Policy' or 'Device Function'
	//changed, ethPortWanStatus should be changed(yzhang fix bug 27569)
	var wanPortNumbers=details.wanPortNum;
 	if (deviceModelChangeIndex > 0) {
		if(deviceConfigChanged){
			changeEthPortWanStatus(details);
			$("#wifi0strTd").html(details.wifi0str);
			if(details.wifi0str=="WAN"){
				$("#br_wifi0_setting").show();
			}else{
				$("#br_wifi0_setting").hide();
			}  
		}else if(modelFunctionChanged){
			changeEthPortWanStatus(details);
		}
		//alert(wanPortNumbers);
		updateWanPortSelectList(wanPortNumbers);
	} 

	//re hidden usb interface
	var usbAsCellularFlag = '<s:property value="dataSource.usbAsCellularModem"/>';
	if (usbAsCellularFlag == 'true') {
		var enableFlag = document.getElementById("dataSource.enableCellularModem").checked;
		enableCellularModem(enableFlag);
	}
	

	var templateEle = document.getElementById(formName + "_configTemplate");
	if(templateEle){
		var templateEleValue = templateEle.value;
		templateEle.length = 0;
		var templateList = details.templateList;
		for(var i=0; i<templateList.length; i++){
			var tempKey = templateList[i].id;
			var tempValue= templateList[i].name;
			addOption(templateEle, tempValue,
					tempKey, tempKey == templateEleValue);
		}
		if(templateEle.value != templateEleValue){
			requestTemplateInfo(templateEle);
			generateRealmName();
		}
	}

	// update radio config type selection
	updateRadioConfigTypes();

	<s:if test="%{hMOnline}">
		hm.util.hide('auditScheduler');
	</s:if>

	<s:if test="%{!fullMode}">
		hm.util.hide('hiveApTag');
		hm.util.hide('virtualConnectSection');
		hm.util.hide('ethConfigCwpStyle');
		hm.util.hide('networkPolicyTr');
		hm.util.hide('captureDataCwpSettings');
	</s:if>

	var operationMode1 = document.getElementById(formName + "_dataSource_eth1_operationMode").value;
	if(operationMode1 != OPERATION_MODE_BACKHAUL){
		hm.util.hide('multipleVlanSection');
	}

	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;

	//deviceModelChangeIndex == 0 is onloadpage.
	if (deviceModelChangeIndex > 0) {
		if(deviceType == DEVICE_TYPE_VPN_GATEWAY){
			Get(formName + "_dataSource_vpnMark").value=VPN_MARK_SERVER;
			delayAlarmFunctionChangeForLayer3();
		}else if(deviceType == DEVICE_TYPE_BRANCH_ROUTER){
			Get(formName + "_dataSource_vpnMark").value=VPN_MARK_CLIENT;
			if(selectApModel == BR100){
				Get(formName + "_dataSource_enabledBrAsRadiusServer").checked=false;
				Get(formName + "_dataSource_enabledOverrideRadiusServer").checked=false;
			}else{
				Get(formName + "_dataSource_enabledBrAsRadiusServer").checked=true;
				Get(formName + "_dataSource_enabledOverrideRadiusServer").checked=false;
			}
		}else if(deviceType == DEVICE_TYPE_HIVEAP){
			//vpn mark set to none
			Get(formName + "_dataSource_vpnMark").value=VPN_MARK_NONE;
			//mgt0 dhcp enable
			document.getElementById(formName + "_mgt0NetworkType3").checked = true;
			radioMgt0NetworkType(USE_DHCP_WITHOUTFALLBACK,true);

			//ap model change or device type change use default settings for ethconfig.
			Get(formName + "_dataSource_ethConfigType").value = USE_ETHERNET_BOTH;
			Get(formName + "_dataSource_eth0_operationMode").value = OPERATION_MODE_BACKHAUL;
			Get(formName + "_dataSource_eth1_operationMode").value = OPERATION_MODE_BACKHAUL;
			
			delayAlarmFunctionChangeForAp();
		}
	}else{
		if(deviceType == DEVICE_TYPE_HIVEAP){
			var mgt0NetworkEles = document.getElementsByName("mgt0NetworkType");
			var mgt0Inter = 0;
			for(var i=0; i<mgt0NetworkEles.length; i++){
				var el = mgt0NetworkEles[i];
				if(el.checked){
					mgt0Inter = el.value;
					break;
				}
			}
			radioMgt0NetworkTypeOnload(mgt0Inter,false);
			
			delayAlarmFunctionChangeForAp();
		}
	}
	if(deviceType == DEVICE_TYPE_VPN_GATEWAY){
		//null
		delayAlarmFunctionChangeForLayer3();
	}else if(deviceType == DEVICE_TYPE_BRANCH_ROUTER){
		changeEnabledBrAsRadiusServer(Get(formName + "_dataSource_enabledBrAsRadiusServer").checked);
		changeEnabledBrAsRadiusServerOverride(Get(formName + "_dataSource_enabledOverrideRadiusServer").checked);

		Get("radiusServerLabelId").innerHTML='<s:text name="hiveAp.radiusServerLabel.br"/>';
		Get("radiusProxyLabelId").innerHTML='<s:text name="hiveAp.radiusProxyLabel.br"/>';
		Get("radioServerHeadTd").style.width="60px";
		Get("radioProxyHeadTd").style.width="60px";
	}else if(deviceType == DEVICE_TYPE_HIVEAP){
		Get("radiusServerLabelId").innerHTML='<s:text name="hiveAp.radiusServerLabel"/>';
		Get("radiusProxyLabelId").innerHTML='<s:text name="hiveAp.radiusProxyLabel"/>';
		Get("radioServerHeadTd").style.width="1px";
		Get("radioProxyHeadTd").style.width="1px";

		ethSetupChanged();

		var operationMode0 = document.getElementById(formName + "_dataSource_eth0_operationMode");
	    var operationMode1 = document.getElementById(formName + "_dataSource_eth1_operationMode");
	    var operationMode2 = document.getElementById(formName + "_dataSource_agg0_operationMode");
	    var operationMode3 = document.getElementById(formName + "_dataSource_red0_operationMode");
		eth0OperationModeChange(operationMode0.value);
	    eth1OperationModeChange(operationMode1.value);
		agg0OperationModeChange(operationMode2.value);
		red0OperationModeChange(operationMode3.value);
		
		delayAlarmFunctionChangeForAp();
	}

	deviceModelChangeIndex ++;
}

function delayAlarmFunctionChangeForAp(){
	if(Get("overrideEnableDelayAlarm")){
		Get("enableDelayAlarmTd").style.paddingLeft = "30px";
		if(Get("overrideEnableDelayAlarm").checked){
			Get("delayAlarmEnableTr").style.display = "";
		}else{
			Get("delayAlarmEnableTr").style.display = "none";
			Get("enableDelayAlarmCK").checked = true;
		}
	}
}

function delayAlarmFunctionChangeForLayer3(){
	if(Get("delayAlarmEnableTr")){
		Get("delayAlarmEnableTr").style.display = "";
		Get("enableDelayAlarmTd").style.paddingLeft = "10px";
	}
}

function changeEthPortWanStatus(details){
	$("#eth1strTd").html(details.eth1str);
	if(details.eth1str=="WAN"){
		$("#eth1TdDiv").show();
		$("#eth1ConnectTdDiv").show();
		$("#eth1_enableNat").show();
		$("#eth1_disablePortForwarding").show();
		$("#eth1_voip_detail").show();
		if($("#branchRouterEth1_connectionType").val()==DEVICE_CONNECT_STATIC){
			$("#eth1StaticIP").show();
			$("#eth1DefaultGateway").show();
		}
		
	}else{
		$("#eth1TdDiv").hide();
		$("#eth1ConnectTdDiv").hide();
		$("#eth1_enableNat").hide();
		$("#eth1StaticIP").hide();
		$("#eth1DefaultGateway").hide();
		$("#eth1_disablePortForwarding").hide();
		$("#eth1_voip_detail").hide();
	}
	
	$("#eth2strTd").html(details.eth2str);
	if(details.eth2str=="WAN"){
		$("#eth2TdDiv").show();
		$("#eth2ConnectTdDiv").show();
		$("#eth2_enableNat").show();
		$("#eth2_disablePortForwarding").show();
		$("#eth2_voip_detail").show();
		if($("#branchRouterEth2_connectionType").val()==DEVICE_CONNECT_STATIC){
			$("#eth2StaticIP").show();
			$("#eth2DefaultGateway").show();
		}
		
	}else{
		$("#eth2TdDiv").hide();
		$("#eth2ConnectTdDiv").hide();
		$("#eth2_enableNat").hide();
		$("#eth2StaticIP").hide();
		$("#eth2DefaultGateway").hide();
		$("#eth2_disablePortForwarding").hide();
		$("#eth2_voip_detail").hide();
	}
	$("#eth3strTd").html(details.eth3str);
	if(details.eth3str=="WAN"){
		$("#eth3TdDiv").show();
		$("#eth3ConnectTdDiv").show();
		$("#eth3_enableNat").show();
		$("#eth3_disablePortForwarding").show();
		$("#eth3_voip_detail").show();
		if($("#branchRouterEth3_connectionType").val()==DEVICE_CONNECT_STATIC){
			$("#eth3StaticIP").show();
			$("#eth3DefaultGateway").show();
		}
	}else{
		$("#eth3TdDiv").hide();
		$("#eth3ConnectTdDiv").hide();
		$("#eth3_enableNat").hide();
		$("#eth3StaticIP").hide();
		$("#eth3DefaultGateway").hide();
		$("#eth3_disablePortForwarding").hide();
		$("#eth3_voip_detail").hide();
	}
	$("#eth4strTd").html(details.eth4str);
	if(details.eth4str=="WAN"){
		$("#eth4TdDiv").show();
		$("#eth4ConnectTdDiv").show();
		$("#eth4_enableNat").show();
		$("#eth4_disablePortForwarding").show();
		$("#eth4_voip_detail").show();
		if($("#branchRouterEth4_connectionType").val()==DEVICE_CONNECT_STATIC){
			$("#eth4StaticIP").show();
			$("#eth4DefaultGateway").show();
		}
	}else{
		$("#eth4TdDiv").hide();
		$("#eth4ConnectTdDiv").hide();
		$("#eth4_enableNat").hide();
		$("#eth4StaticIP").hide();
		$("#eth4DefaultGateway").hide();
		$("#eth4_disablePortForwarding").hide();
		$("#eth4_voip_detail").hide();
	}
	
	if($("#usbstrTd").html().trim() == 'WAN')
    {
		var usb=$("#usbWanSelectListID").val();
		if(usb !=null && parseInt(usb) == 1){
			$("#usbConnectionModelTr").hide();
		}else{
			$("#usbConnectionModelTr").show();
		}
    }
	else{
		$("#usbConnectionModelTr").show();
	}
	
}

function changeDefaultValueForPriority(model){
	var priority = document.getElementById(formName+"_dataSource_priority");
	var deviceBonjourGatewayConfig = document.getElementById("deviceBonjourGatewayConfig")
	if(model == MODEL_SR24 || model == MODEL_SR2124P || model == MODEL_SR2024P || model == MODEL_SR2148P || model == MODEL_SR48){ // for switch (50)
		priority.value = 50;
		deviceBonjourGatewayConfig.style.display='';
	} else if (model == BR200 || model == BR200_WP || model == BR20_LTE_VZ){ //br200 (40)
		priority.value = 40;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == VPN_GATEWAY || model == CVG_APPLIANCE){//CVG (25)
		priority.value = 25;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_330 || model == MODEL_350){//AP330/350 (20)
		priority.value = 20;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_370 || model == MODEL_390){//AP370/390 (23)
		priority.value = 23;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_340 || model == MODEL_320){//AP340/320 (15)
		priority.value = 15;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_120 || model == MODEL_121
			|| model == MODEL_141 || model == MODEL_170){//AP/120/121/141/170 (10)
		priority.value = 10;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_110){//AP110 (5)
		priority.value = 5;
		deviceBonjourGatewayConfig.style.display='';
	} else if(model == MODEL_230){//AP230 (21)
		priority.value = 21;
		deviceBonjourGatewayConfig.style.display='';
	} else {
		priority.value = "";
		deviceBonjourGatewayConfig.style.display='none';
	}
}

var modelFunctionChanged = false;
var modelFunctionChangeIndex = 0;
<%-- callback for changing hiveApModel --%>
function changeApModel(){
	var model = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	$("#" + formName + "_oldDeviceType").attr("value", $("#hiveApDeviceTypeValue").val());
	if (modelFunctionChangeIndex > 0) {
		modelFunctionChanged = true;
	}
	deviceModeChange(model, deviceType);

	ethSetupChanged();

	updateInterfaceLayout();

	//changeTransTypeAndSpeed(model,deviceType);

	// refresh radio profile and channels
	updateRadioProfiles();
	// refresh static route interface types
	//updateStaticRouteInterface();
	showNetworkSettingsContent();//TODO
	
	modelFunctionChangeIndex++;


	//deviceTypeChanged(document.getElementById(formName + "_dataSource_deviceType").value, model);
}


function changeTransTypeAndSpeed(model,deviceType) {
	// disable transmission type and speed setting when model is br
	if (model == MODEL_330 || model == MODEL_350) {
		document.getElementById(formName + '_branchRouterEth0_speed').disabled = false;
		document.getElementById(formName + '_branchRouterEth1_speed').disabled = false;
		document.getElementById(formName + '_branchRouterEth0_duplex').disabled = false;
		document.getElementById(formName + '_branchRouterEth1_duplex').disabled = false;
		document.getElementById(formName + '_branchRouterUSB_speed').disabled = false;
		document.getElementById(formName + '_branchRouterUSB_duplex').disabled = false;
	} else {
		document.getElementById(formName + '_branchRouterEth0_speed').disabled = true;
		document.getElementById(formName + '_branchRouterEth0_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterEth0_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterEth0_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterEth1_speed').disabled = true;
		document.getElementById(formName + '_branchRouterEth1_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterEth1_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterEth1_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterEth2_speed').disabled = true;
		document.getElementById(formName + '_branchRouterEth2_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterEth2_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterEth2_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterEth3_speed').disabled = true;
		document.getElementById(formName + '_branchRouterEth3_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterEth3_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterEth3_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterEth4_speed').disabled = true;
		document.getElementById(formName + '_branchRouterEth4_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterEth4_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterEth4_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterUSB_speed').disabled = true;
		document.getElementById(formName + '_branchRouterUSB_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterUSB_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterUSB_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterWifi0_speed').disabled = true;
		document.getElementById(formName + '_branchRouterWifi0_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterWifi0_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterWifi0_duplex').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_branchRouterWifi1_speed').disabled = true;
		document.getElementById(formName + '_branchRouterWifi1_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_branchRouterWifi1_duplex').disabled = true;
		document.getElementById(formName + '_branchRouterWifi1_duplex').value = ETH_DUPLEX_AUTO;
	}
	//fix bug 17497 cvg as ap
	if(model == VPN_GATEWAY && deviceType == DEVICE_TYPE_HIVEAP){
		document.getElementById(formName + '_dataSource_eth0_speed').disabled = true;
		document.getElementById(formName + '_dataSource_eth0_speed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_dataSource_eth0_duplex').disabled = true;
		document.getElementById(formName + '_dataSource_eth0_duplex').value = ETH_DUPLEX_AUTO;
	}else{
		document.getElementById(formName + '_dataSource_eth0_speed').disabled = false;
		document.getElementById(formName + '_dataSource_eth0_duplex').disabled = false;
	}
}

function updateRadioConfigTypes(){
	if(isWifi1Available()){
		var radioConfigType1 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_ALL);
		radioConfigType1.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.allAccess' />";
		var radioConfigType2 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_ONE);
		radioConfigType2.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.oneAccess' />";
		var radioConfigType5 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_DUAL);
		radioConfigType5.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.accessDual' />";
	}else{
		//check another box
		var radioConfigType1 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_ALL);
		radioConfigType1.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.access' />";
		var radioConfigType2 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_ONE);
		radioConfigType2.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.mesh' />";
		var radioConfigType5 = document.getElementById(formName + "_dataSource_radioConfigType"+RADIO_MODE_ACCESS_DUAL);
		radioConfigType5.nextSibling.innerHTML = "<s:text name='hiveAp.radioMode.dual' />";
	}

	var radioConfigTypeEls = document.getElementsByName("dataSource.radioConfigType");
	var radioConfigType = 0;
	for(var i=0; i<radioConfigTypeEls.length; i++){
		var el = radioConfigTypeEls[i];
		if(el.checked){
			radioConfigType = el.value;
			break;
		}
	}
	radioModeTypeChanged(radioConfigType);
}

function updateStaticRouteInterface(){
	var enumStaticRoute = new Array();
	<s:iterator value="%{enumStaticRoute}">
		enumStaticRoute[<s:property value="key" />] = '<s:property value="value" />';
	</s:iterator>
	var enumStaticRouteDual = new Array();
	<s:iterator value="%{enumStaticRouteDual}">
		enumStaticRouteDual[<s:property value="key" />] = '<s:property value="value" />';
	</s:iterator>
	var enumStaticRouteSingle = new Array();
	<s:iterator value="%{enumStaticRouteSingle}">
		enumStaticRouteSingle[<s:property value="key" />] = '<s:property value="value" />';
	</s:iterator>

	var selectElement = document.getElementById(formName + "_interfaceType");
	var value = selectElement.value;
	selectElement.length=0;

	if(is11nHiveAP()){
		if(isWifi1Available()){
			for(var property in enumStaticRouteDual){
				if(typeof(enumStaticRouteDual[property]) == "string"){
					addOption(selectElement ,enumStaticRouteDual[property], property, value == property)
				}
			}
		}else{
			for(var property in enumStaticRouteSingle){
				if(typeof(enumStaticRouteSingle[property]) == "string"){
					addOption(selectElement ,enumStaticRouteSingle[property], property, value == property)
				}
			}
		}
	}else{
		for(var property in enumStaticRoute){
			if(typeof(enumStaticRoute[property]) == "string"){
				addOption(selectElement ,enumStaticRoute[property], property, value == property)
			}
		}
	}
}

function updateInterfaceLayout(){
	if(isEth1Available()){  // 11n except HiveAP 120, HiveAP 110
		// show eth1
		hm.util.show('eth1Row');
		// HiveAP new layout
		hm.util.show('ethSetupTr');
	} else{ // ag20 or HiveAP 120 or HiveAP 110
		document.getElementById(formName + "_dataSource_ethConfigType").value = USE_ETHERNET_BOTH;

		hm.util.hide('eth1Row');
		// HiveAP new layout
		hm.util.hide('ethSetupTr');
	}
	ethSetupChanged();

	if(isWifi1Available()){
		hm.util.show('wifi1Row');
		document.getElementById('customizeWifi1Label').style.visibility = "visible";
		document.getElementById('customizeWifi1Selector').style.visibility = "visible";

		document.getElementById('customizeWifi1LabelBR').style.visibility = "visible";
		document.getElementById('customizeWifi1SelectorBR').style.visibility = "visible";
	}else{
		hm.util.hide('wifi1Row');
		document.getElementById('customizeWifi1Label').style.visibility = "hidden";
		document.getElementById('customizeWifi1Selector').style.visibility = "hidden";

		document.getElementById('customizeWifi1LabelBR').style.visibility = "hidden";
		document.getElementById('customizeWifi1SelectorBR').style.visibility = "hidden";
	}
	if(!is11nHiveAP()){
		var eth0SpeedEl = document.getElementById(formName + "_dataSource_eth0_speed");
		if(eth0SpeedEl.value == ETH_SPEED_1000M){
			eth0SpeedEl.selectedIndex = 0;
		}
	}

	var apModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	//hideShowCol(5, (apModel != MODEL_330 && apModel != MODEL_350));
	//hideShowColWithTitle(7, (apModel != MODEL_330 && apModel != MODEL_350), Get(formName + "_dataSource_enablePppoe").checked);
	hideShowPseSettings(apModel == BR200_WP || apModel == BR200_LTE_VZ);
}

function hideShowPseSettings(isShow){
	if(isShow) {
		Get("pseSettingAllDiv").style.display='';
	} else {
		Get("pseSettingAllDiv").style.display='none';
	}
}

function hideShowCol(colIdx, isShow) {
	var ethSetting = document.getElementById('interfaceEthSettingTable');
	var table = ethSetting.children[0];
	var rowsLen = table.rows.length;
	for (var i=0; i<rowsLen; i++)
	{
		var tr = table.rows[i];
		if ((i==0||i==7)&&tr.children[colIdx] && tr.children[colIdx] != undefined) {
			tr.children[colIdx].style.display = isShow ? "" : "none";
		}
	}
}

function hideShowColWithTitle(colIdx, isShowTitle, showSub) {
	var ethSetting = document.getElementById('interfaceEthSettingTable');
	var table = ethSetting.children[0];
	var rowsLen = table.rows.length;
	for (var i=0; i<rowsLen; i++)
	{
		var tr = table.rows[i];
		if (tr.children[colIdx] && tr.children[colIdx] != undefined) {
			if (i==0) {
				tr.children[colIdx].style.display = isShowTitle ? "" : "none";
			} else {
				tr.children[colIdx].style.display = (isShowTitle && showSub) ? "" : "none";
			}
		}
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var adminStatusValue, operationMode;
	if(details.wifi0){
		var wifi0 = document.getElementById(formName + "_wifi0RadioProfile");
		var wifi0Id = wifi0.value;
		operationMode = getWifi0OperationMode();
		wifi0.length=0;
		wifi0.length=details.wifi0.length;
		for(var i = 0; i < details.wifi0.length; i ++) {
			wifi0.options[i].value = details.wifi0[i].id;
			wifi0.options[i].text = details.wifi0[i].v;
			wifi0.options[i].selected = details.wifi0[i].id==wifi0Id;
		}
		if(callback.argument != null && callback.argument != "" && (callback.argument[1] == 1 || callback.argument[1] == 5)){
			wifi0.value = callback.argument[0];
		}
		if(details.wifi0dl){
			/* var wifi0ModeEl = document.getElementById("wifi0RadioMode");
			wifi0ModeEl.replaceChild(document.createTextNode(details.wifi0dl), wifi0ModeEl.childNodes[0]); */
			selectRadioProfile(wifi0);
		}
		if(details.wifi0label){
			var wifi0labelEl = document.getElementById("wifi0Label");
			wifi0labelEl.replaceChild(document.createTextNode(details.wifi0label), wifi0labelEl.childNodes[0]);
		}
		//update channel
		if(details.wifi0c){
			var channel0El = document.getElementById(formName + "_dataSource_wifi0_channel");
			var channel0 = channel0El.value;
			channel0El.length=0;
			channel0El.length=details.wifi0c.length;
			for(var i = 0; i < details.wifi0c.length; i ++) {
				channel0El.options[i].value = details.wifi0c[i].key;
				channel0El.options[i].text = details.wifi0c[i].value;
				channel0El.options[i].selected = details.wifi0c[i].key==channel0;
			}
		}
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi0_adminState").value;
		var enabled = getSsidStatus(operationMode, adminStatusValue);
		disableCheckBoxList("wifi0", enabled );
	}
	if(details.wifi1){
		var wifi1 = document.getElementById(formName + "_wifi1RadioProfile");
		var wifi1Id = wifi1.value;
		operationMode = getWifi1OperationMode();
		wifi1.length=0;
		wifi1.length=details.wifi1.length;
		for(var i = 0; i < details.wifi1.length; i ++) {
			wifi1.options[i].value = details.wifi1[i].id;
			wifi1.options[i].text = details.wifi1[i].v;
			wifi1.options[i].selected = details.wifi1[i].id==wifi1Id;
		}
		if(callback.argument != null && callback.argument != "" && (callback.argument[1] == 2 || callback.argument[1] == 4)){
			wifi1.value = callback.argument[0];
		}
		if(details.wifi1dl){
			/* var wifi1ModeEl = document.getElementById("wifi1RadioMode");
			wifi1ModeEl.replaceChild(document.createTextNode(details.wifi1dl), wifi1ModeEl.childNodes[0]); */
			selectRadioProfile(wifi1);
		}
		if(details.wifi1label){
			var wifi1labelEl = document.getElementById("wifi1Label");
			wifi1labelEl.replaceChild(document.createTextNode(details.wifi1label), wifi1labelEl.childNodes[0]);
		}
		//update channel
		if(details.wifi1c){
			var channel1El = document.getElementById(formName + "_dataSource_wifi1_channel");
			var channel1 = channel1El.value;
			channel1El.length=0;
			channel1El.length=details.wifi1c.length;
			for(var i = 0; i < details.wifi1c.length; i ++) {
				channel1El.options[i].value = details.wifi1c[i].key;
				channel1El.options[i].text = details.wifi1c[i].value;
				channel1El.options[i].selected = details.wifi1c[i].key==channel1;
			}
		}
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi1_adminState").value;
		var enabled = getSsidStatus(operationMode, adminStatusValue);
		disableCheckBoxList("wifi1", enabled );
	}
	//update ssid allocation style
	if(!isWifi1Available()){
		hm.util.show('wifi2GSsidAllocation');
		hm.util.show('wifi2GSsidAllocationSpacer');
		hm.util.hide('wifi5GSsidAllocation');
	}else{
		hm.util.show('wifi2GSsidAllocation');
		hm.util.show('wifi2GSsidAllocationSpacer');
		hm.util.show('wifi5GSsidAllocation');
	}
};

var detailsFailed = function(o) {
	//alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function updateRadioProfiles(id,radioMode) {
	if (id != null && id != '' && radioMode != null && radioMode != ''){
		var params=[id,radioMode];
		callback.argument = params;
	}
	var modelType = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var wifi0OperationMode = getWifi0OperationMode();
	var wifi1OperationMode = getWifi1OperationMode();
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=fetchRadioProfiles&apModelType="+modelType+"&wifi0OperationMode="+wifi0OperationMode+"&wifi1OperationMode="+wifi1OperationMode+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
}

function getWifi0Channels(profileId, wifi0OperationMode) {
	
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi0Channels" + "&wifi0RadioProfile="+profileId + "&wifi0OperationMode="+wifi0OperationMode+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processRadioInfos, failure : connectFailed, argument: "wifi0"}, null);
}

function getWifi1Channels(profileId, wifi1OperationMode) {
	if(!isWifi1Available()){
		return;
	}
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi1Channels" + "&wifi1RadioProfile="+profileId + "&wifi1OperationMode="+wifi1OperationMode+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processRadioInfos, failure : connectFailed, argument: "wifi1"}, null);
}

var processRadioInfos = function(o){
	eval("var details = " + o.responseText);

	var adminStatusValue;
	if(o.argument=='wifi1'){
		if(details.m){
			var radioModeEl = document.getElementById("wifi1RadioMode");
			radioModeEl.replaceChild(document.createTextNode(details.m), radioModeEl.childNodes[0]);
		}
		if(details.l){
			var wifi1labelEl = document.getElementById("wifi1Label");
			wifi1labelEl.replaceChild(document.createTextNode(details.l), wifi1labelEl.childNodes[0]);
		}
		//update channel
		if(details.c){
			var channel1El = document.getElementById(formName + "_dataSource_wifi1_channel");
			var channel1 = channel1El.value;
			channel1El.length=0;
			channel1El.length=details.c.length;
			for(var i = 0; i < details.c.length; i ++) {
				channel1El.options[i].value = details.c[i].key;
				channel1El.options[i].text = details.c[i].value;
				channel1El.options[i].selected = details.c[i].key==channel1;
			}
		}
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi1_adminState").value;
	}else{
		
		if($("#dynamicbandswitch_checkbox").attr("checked")=="checked"){
			$("#wifi0Label").html("2.4 GHz/5 GHz");
			$("#wifi0RadioMode").html("11ng/11na");
		}else{
	
		if(details.m){
			var radioModeEl = document.getElementById("wifi0RadioMode");
			radioModeEl.replaceChild(document.createTextNode(details.m), radioModeEl.childNodes[0]);
		}
		if(details.l){
			var wifi0labelEl = document.getElementById("wifi0Label");
			wifi0labelEl.replaceChild(document.createTextNode(details.l), wifi0labelEl.childNodes[0]);
		}
		}
		//update channel
		if(details.c){
			var channel0El = document.getElementById(formName + "_dataSource_wifi0_channel");
			var channel0 = channel0El.value;
			channel0El.length=0;
			channel0El.length=details.c.length;
			for(var i = 0; i < details.c.length; i ++) {
				channel0El.options[i].value = details.c[i].key;
				channel0El.options[i].text = details.c[i].value;
				channel0El.options[i].selected = details.c[i].key==channel0;
			}
		}
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi0_adminState").value;
	}
	//update ssid allocation style
	var enabled = getSsidStatus(details.o, adminStatusValue);
	disableCheckBoxList(details.enable5G ? "wifi1":"wifi0", enabled );
	if(!isWifi1Available()){
		if(details.enable5G){
			hm.util.hide('wifi2GSsidAllocation');
			hm.util.hide('wifi2GSsidAllocationSpacer');
			hm.util.show('wifi5GSsidAllocation');
		}else{
			hm.util.show('wifi2GSsidAllocation');
			hm.util.show('wifi2GSsidAllocationSpacer');
			hm.util.hide('wifi5GSsidAllocation');
		}
	}
};

var connectFailed = function(o){
    //alert("connection failed.");
};

/**
 * 1000M is only available in Ag20 model
 * called when speed of eth0 or eth1 is changed.
 */
function changeSpeed(selectBox) {
	if(selectBox.value == ETH_SPEED_1000M && !is11nHiveAP()) {
		hm.util.reportFieldError(selectBox, '<s:text name="error.hiveAp.noSpeed" />');
		selectBox.value = 0;
	}
}

/**
 * 1000M is only available in Ag20 model
 * called when the page is submitted
 */
function validateEthSpeed(operation) {
	if('create2' == operation || 'update2' == operation) {
		var selectBox = document.getElementById(formName + "_dataSource_eth0_speed");
		if(selectBox.value == ETH_SPEED_1000M && !is11nHiveAP()) {
			hm.util.reportFieldError(selectBox, '<s:text name="error.hiveAp.noSpeed" />');
			return false;
		}
	}
	return true;
}

/* for send these values to server*/
function enableBindInterfaceItems(){
	if(isEth1Available()){
		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_adminState").disabled = false;
	}
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType == DEVICE_TYPE_BRANCH_ROUTER && !document.getElementById(formName + "_dataSource_enableOverrideBrPMTUD").checked)
	{
		document.getElementById(formName + "_dataSource_enableBrPMTUD").disabled = false;
	}
}

function changeNativeVlanInput(cbxValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_nativeVlan");
	nativeVlanEl.disabled = !cbxValue;
}

function updateNativeVlan(){
	var overrideEl = document.getElementById("overrideVlan");
	if(overrideEl && !overrideEl.checked){
		document.getElementById(formName + "_dataSource_nativeVlan").value="";
	}
}

function changeMgtVlanInput(cbxValue){
	var mgtVlanEl = document.getElementById(formName + "_dataSource_mgtVlan");
	mgtVlanEl.disabled = !cbxValue;
}

function updateMgtVlan(){
	var overrideEl = document.getElementById("overrideMgtVlan");
	if(overrideEl && !overrideEl.checked){
		document.getElementById(formName + "_dataSource_mgtVlan").value="";
	}
}

function changeMultipleVlanInput(cbxValue){
	var multipleVlanEl = document.getElementById(formName + "_dataSource_vlanid");
	multipleVlanEl.disabled = !cbxValue;
}

/*SSID Allocation section*/
function toggleCheckAllWifiSsids(cb, toggleName){
	var cbs = document.getElementsByName(toggleName);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function updateSsidAllocation(){
	var adminStatusMode = $("#"+formName + "_dataSource_wifi0_adminState").attr("value");
	if(!adminStatusMode){
		return;
	}
	var operationMode = getWifi0OperationMode();
	var enabled = getSsidStatus(operationMode, adminStatusMode);//wifi0 by default;
	disableCheckBoxList("wifi0", enabled);
	setCheckBoxSelected("checkAll_0", "ssid0Indices");

	adminStatusMode = $("#"+formName + "_dataSource_wifi1_adminState").attr("value");
	if(!adminStatusMode){
		return;
	}
	operationMode = getWifi1OperationMode();
	enabled = getSsidStatus(operationMode, adminStatusMode);//wifi1 by default;
	disableCheckBoxList("wifi1", enabled);
	setCheckBoxSelected("checkAll_1", "ssid1Indices");
}

function disableCheckBoxList(wifiName, enabled){
	var elName, toggleName;
	if("wifi0" == wifiName){
		elName = "checkAll_0";
		toggleName = "ssid0Indices";
	}else if("wifi1" == wifiName){
		elName = "checkAll_1";
		toggleName = "ssid1Indices";
	}
	if(elName && toggleName){
		var el = document.getElementById(elName);
		var cbs = document.getElementsByName(toggleName);
		for (var i=0; i< cbs.length; i++){
			//cbs[i].disabled = !enabled;
			// hide while element disabled
			cbs[i].parentNode.parentNode.style.display = enabled?"":"none";
		}
		if(el != null){
		el.disabled = !enabled;
	    }
	}
}

function setCheckBoxSelected(elName, toggleName){
	var el = document.getElementById(elName);
	var allSelected = true;
	var cbs = document.getElementsByName(toggleName);
	for (var i=0; i< cbs.length; i++){
		if (!cbs[i].checked) {
			allSelected = false;
		}
	}
	if(cbs.length == 0){
		allSelected = false;
	}
	if(el !=null){
	el.checked = allSelected;
	}
}

function operationModeChanged(operationMode, wifiName){
	var adminStatusValue;
	if("wifi0" == wifiName){
		adminStatusValue = $("#"+formName + "_dataSource_wifi0_adminState").attr("value");
		
	}else{
		adminStatusValue = $("#"+formName + "_dataSource_wifi1_adminState").attr("value");
		
	}
	if(!adminStatusValue){
		return;
	}
	var enabled = getSsidStatus(operationMode, adminStatusValue);
	disableCheckBoxList(wifiName, enabled );
	if("wifi0" == wifiName){
		if(!isWifi1Available()){
			// for those wifi1 not avaiable boxes, wifi0 will be one of a/b/g/n mode,
			// so need to update its channel as well.
			//change channel list for DFS
			var wifi0RadioProfileId = document.getElementById(formName + "_wifi0RadioProfile").value;
			getWifi0Channels(wifi0RadioProfileId, operationMode);
		}
	}else if ("wifi1" == wifiName){
		if(isWifi1Available()){
			// for those wifi1 not available boxes, do not need to get wifi1 channels,
			//change channel list for DFS
			var wifi1RadioProfileId = document.getElementById(formName + "_wifi1RadioProfile").value;
			getWifi1Channels(wifi1RadioProfileId, operationMode);
		}
	}
	//fix bug 32988 and 32985. 
	//disable  adminState and channel drop-down box of wifi interfacefor sensor mode
	setSensorWifiChannel(operationMode, wifiName);
}
function setSensorWifiChannel(operationMode, wifiName){
	if("wifi0" == wifiName){
		var wifi0AdminState=document.getElementById(formName + "_dataSource_wifi0_adminState");
		var wifi0Channel=document.getElementById(formName + "_dataSource_wifi0_channel");
		if(operationMode==OPERATION_MODE_SENSOR){
			if(null!=wifi0AdminState){
				wifi0AdminState.value=0;
				wifi0AdminState.disabled=true;
			}
			if(null!=wifi0Channel){
				wifi0Channel.value=0;
				wifi0Channel.disabled=true;
			}
		}else{
			if(null!=wifi0AdminState){
				wifi0AdminState.value="<s:property value='%{dataSource.wifi0.adminState}'/>";
				wifi0AdminState.disabled=false;
			}
			if(null!=wifi0Channel){
				wifi0Channel.value="<s:property value='%{dataSource.wifi0.channel}'/>";
				wifi0Channel.disabled=false;
			}
		}
		adminStatusChanged(wifi0AdminState,"wifi0");
	}else if("wifi1" == wifiName){
		var wifi1AdminState=document.getElementById(formName + "_dataSource_wifi1_adminState");
		var wifi1Channel=document.getElementById(formName + "_dataSource_wifi1_channel");
		if(operationMode==OPERATION_MODE_SENSOR){
			if(null!=wifi1AdminState){
				wifi1AdminState.value=0;
				wifi1AdminState.disabled=true;
			}
			if(null!=wifi1Channel){
				wifi1Channel.value=0;
				wifi1Channel.disabled=true;
			}
		}else{
			if(null!=wifi1AdminState){
				wifi1AdminState.value="<s:property value='%{dataSource.wifi1.adminState}'/>";
				wifi1AdminState.disabled=false;
			}
			if(null!=wifi1Channel){
				wifi1Channel.value="<s:property value='%{dataSource.wifi1.channel}'/>";
				wifi1Channel.disabled=false;
			}
		}
		adminStatusChanged(wifi1AdminState,"wifi1");
	}
}

function getSsidStatus(modeValue, adminStatusValue){
	if((modeValue == OPERATION_MODE_ACCESS || modeValue == OPERATION_MODE_DUAL || modeValue == OPERATION_MODE_WAN_ACCESS) 
			&& adminStatusValue == IF_ADMIN_STATUS_UP){// access and up
		return true;
	}else if (modeValue == OPERATION_MODE_BACKHAUL || modeValue==OPERATION_MODE_SENSOR || adminStatusValue == IF_ADMIN_STATUS_DOWN ){// backhaul or down
		return false;
	}
	return false;
}

function adminStatusChanged(selectEl, wifiName){
	var adminStatusValue = selectEl.options[selectEl.selectedIndex].value;
	var modeValue;
	if("wifi0" == wifiName){
		modeValue = getWifi0OperationMode();
	}else if ("wifi1" == wifiName){
		modeValue = getWifi1OperationMode();
	}
	var enabled = getSsidStatus(modeValue, adminStatusValue);
	disableCheckBoxList(wifiName, enabled );
}

function branchRouterUSBChanged(value){
	var usbRoleValue = value;
	if(usbRoleValue == BRANCH_ROLE_PRIMARY){
		hm.util.hide("usbConnectionModelTr");
		Get(formName + "_branchRouterUSB_priority").value="0";
		Get(formName + "_branchRouterUSB_priority").disabled=true;
	}else{
		hm.util.show("usbConnectionModelTr");
		Get(formName + "_branchRouterUSB_priority").value="2000";
		Get(formName + "_branchRouterUSB_priority").disabled=false;
	}
}


function branchRouterUSBInited(){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;

	if(deviceType == DEVICE_TYPE_BRANCH_ROUTER){

	if($("#usbstrTd").html().trim() == 'WAN')
    {
		var usb=$("#usbWanSelectListID").val();
		if(usb !=null && parseInt(usb) == 1){
			$("#usbConnectionModelTr").hide();
		}else{
			$("#usbConnectionModelTr").show();
		}
    }
	else{
		$("#usbConnectionModelTr").show();
	}
	}
}

function branchRouterEthChanged(order,value){
	if(value==BRANCH_ROLE_WAN){
		hm.util.show("eth"+order+"_enableNat");
		hm.util.show("eth"+order+"_priority");
		hm.util.show("eth"+order+"_voip_detail");
	}else{
		hm.util.hide("eth"+order+"_enableNat");
		hm.util.hide("eth"+order+"_priority");
		hm.util.hide("eth"+order+"_voip_detail");
	}
}

function branchRouterEthChangedWarning(order,value){
	if(value == BRANCH_ROLE_WAN){
		var eth1 = document.getElementById(formName + "_branchRouterEth1_role");
		var eth2 = document.getElementById(formName + "_branchRouterEth2_role");
		var eth3 = document.getElementById(formName + "_branchRouterEth3_role");
		var eth4 = document.getElementById(formName + "_branchRouterEth4_role");
		var eth5 = document.getElementById(formName + "_branchRouterWifi0_role");
		var eth6 = document.getElementById(formName + "_branchRouterWifi1_role");
		var arrayObj = new Array();
		arrayObj.push(eth1);
		arrayObj.push(eth2);
		arrayObj.push(eth3);
		arrayObj.push(eth4);
		arrayObj.push(eth5);
		arrayObj.push(eth6);
		for(var i=0; i<6;i++){
			if(i!=(order-1)&&arrayObj[i].value==BRANCH_ROLE_WAN){
				showWarnDialog('<s:text name ="warning.hiveAp.branchRoutereth.role.changed" />');
				arrayObj[order-1].value=0;
				hm.util.hide("eth"+order+"_enableNat");
				hm.util.hide("eth"+order+"_priority");
				hm.util.hide("eth"+order+"_voip_detail");
			}
		}
	}
}

function generateRealmName(){
	var topologyMapId = document.getElementById(formName + "_topology").value;
	var templateId = document.getElementById(formName + "_configTemplate").value;
	var lockRealmNameEl = document.getElementById(formName + "_dataSource_lockRealmName");
	var realmNameValue = document.getElementById(formName + "_dataSource_realmName").value;
	if(!lockRealmNameEl.checked){
		var url = "<s:url action='hiveAp' includeParams='none' />?operation=generateRealmName"
			+ "&configTemplate=" + templateId
			+"&topologyMapId="+topologyMapId
			+"&oldRealmName="+realmNameValue
			+"&lockRealmName="+lockRealmNameEl.checked
			+"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : setRealmName}, null);
	}
}

var setRealmName =  function (o){
	eval("var details = " + o.responseText);
	var strRealmName = details.realmName;
	var realmName = document.getElementById(formName+"_dataSource_realmName");
	if(realmName){
		realmName.value = strRealmName;
	}
}

function requestClassificationInfo(selectEl,classificationTag1,classificationTag2,classificationTag3){
	var templateId = selectEl.value;
	url = "<s:url action='hiveAp' includeParams='none' />?operation=requestTemplate" + "&configTemplate=" + templateId
			+"&previousNPId="+previousNPId
			+"&classificationTag1="+classificationTag1
			+"&classificationTag2="+classificationTag2
			+"&classificationTag3="+classificationTag3
			+"&ignore="+new Date().getTime();
	previousNPId = templateId;
	//set configChanged to true
	deviceConfigChanged = true;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : getTemplateInfo}, null);
}

function requestTemplateInfo(selectEl){
	var templateId = selectEl.value;
	url = "<s:url action='hiveAp' includeParams='none' />?operation=requestTemplate" + "&configTemplate=" + templateId
			+"&previousNPId="+previousNPId
			+"&ignore="+new Date().getTime();
	previousNPId = templateId;
	//set configChanged to true
	deviceConfigChanged = true;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : getTemplateInfo}, null);
}

var getTemplateInfo = function(o){
	
	eval("var details = " + o.responseText);
	var wifi0_area = document.getElementById("wifi0ssidTable");
	var wifi1_area = document.getElementById("wifi1ssidTable");
	
	document.getElementById("hiveAp_dataSource_portTemplate").value=details.portTemplate;
	changeEthPortWanStatus(details);
	
	 $("#wifi0strTd").html(details.wifi0str);
	
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var wanPortNumbers=details.wanPortNum;
	if(details.wifi0str=="WAN"){
		
		$("#br_wifi0_setting").show();
		$("#"+formName+"_dataSource_radioConfigType6").removeAttr("disabled");
		//$("#radioConfigTypeAccessWan_tr").show();
		if($(":radio[name='dataSource.radioConfigType']:checked").val()!=RADIO_MODE_ACCESS_WAN){
			wanPortNumbers=parseInt(wanPortNumbers)-1;
			radioWanModeSwitch4BR200WP(false);
		}
	}else{
		$("#br_wifi0_setting").hide();
		if(selectApModel == BR200_WP || selectApModel == BR200_LTE_VZ)
			{
			
			$("#"+formName+"_dataSource_radioConfigType6").attr("disabled","disabled");
			//<!--$("#radioConfigTypeAccessWan_tr").hide();-->
			radioWanModeSwitch4BR200WP(false);
			}

		if($(":radio[name='dataSource.radioConfigType']:checked").val()==RADIO_MODE_ACCESS_WAN){
			$(":radio[name='dataSource.radioConfigType']").val(["1"]);
		}
	} 
	
	if (deviceModelChangeIndex > 0) {
		//alert(wanPortNumbers);
		updateWanPortSelectList(wanPortNumbers);
		
	}
	
	var wifi0_body = "";
	var wifi1_body = "";
	var head = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tbody>";
	var foot = "</tbody></table>";

	var vpn = details.vpn;
	var ssids = details.ssids;
	var userProfiles = details.userProfiles;
	var toWirelessRouting = details.toWirelessRouting;
	var wirelessRoutingChged = details.wirelessRoutingChged;
	if (wirelessRoutingChged) {
		npWirelessRoutingEnabledHiveAp = toWirelessRouting;
	}
	
	if(details.wirelessEnabled && !details.routerEnabled){
		$("#vpnRuleTr").css("display", "block");
	}else{
		$("#vpnRuleTr").css("display", "none");
		$("#"+formName + "_dataSource_vpnMark").attr("value", VPN_MARK_NONE);
	}

	if(vpn){
		document.getElementById("vpnRuleTr").style.display = "";
	}else{
		document.getElementById("vpnRuleTr").style.display = "none";
		document.getElementById(formName + "_dataSource_vpnMark").value = VPN_MARK_NONE;
	}
	
	for(var i = 0; i< ssids.length; i++){
		var ssidEl = ssids[i];
		var checkBoxValue = ssidEl.ssid;
		var title = ssidEl.tooltip;
		var label = ssidEl.ssidName;
		var type = ssidEl.type;
		var checked = ssidEl.checked ? "checked=\"checked\"" : "";
		if("wifi0" == type){
			wifi0_body += "<tr valign=\"top\"><td style=\"padding: 5px 3px;\" width=\"10px\">"+
			"<input name=\"ssid0Indices\" value=\""+checkBoxValue+"\" "+checked +
			"id=\"hiveAp_ssid0Indices\" onclick=\"setCheckBoxSelected('checkAll_0', "+
			"'ssid0Indices');\" type=\"checkbox\"><input name=\"__checkbox_ssid0Indices\" "+
			"value=\""+checkBoxValue+"\" type=\"hidden\"></td>"+
			"<td class=\"labelT1\"><label title=\""+title+"\">"+label+"</label></td></tr>";
		}else{
			wifi1_body += "<tr valign=\"top\"><td style=\"padding: 5px 3px;\" width=\"10px\">"+
			"<input name=\"ssid1Indices\" value=\""+checkBoxValue+"\" "+checked +
			"id=\"hiveAp_ssid1Indices\" onclick=\"setCheckBoxSelected('checkAll_1', "+
			"'ssid1Indices');\" type=\"checkbox\"><input name=\"__checkbox_ssid1Indices\" "+
			"value=\""+checkBoxValue+"\" type=\"hidden\"></td>"+
			"<td class=\"labelT1\"><label title=\""+title+"\">"+label+"</label></td></tr>";
		}
	}
	//wifi0_area.style.display = "none";
	//wifi1_area.style.display = "none";
	wifi0_area.innerHTML = head + wifi0_body + foot;
	wifi1_area.innerHTML = head + wifi1_body + foot;
	updateSsidAllocation();
	//wifi0_area.style.display = "";
	//wifi1_area.style.display = "";

	if (userProfiles && wirelessRoutingChged) {
		saveTmpUserProfilesSelection(toWirelessRouting);
		refreshUserprofilesOfHiveAp(userProfiles);
		restoreTmpUserProfilesSelection(toWirelessRouting);
	}
}

/*end*/

var selectedUserProfilesVlan = {};
var selectedUserProfilesNetwork = {};
function saveTmpUserProfilesSelection(tobeWirelessRouting) {
	if (tobeWirelessRouting) {
		selectedUserProfilesVlan.up1 = document.forms[formName].ethDefaultRegUserprofile.value;
		selectedUserProfilesVlan.up2 = document.forms[formName].ethDefaultAuthUserprofile.value;
		selectedUserProfilesVlan.up3 = document.forms[formName].userProfileEth0.value;
		selectedUserProfilesVlan.up4 = document.forms[formName].userProfileEth1.value;
		selectedUserProfilesVlan.up5 = document.forms[formName].userProfileAgg0.value;
		selectedUserProfilesVlan.up6 = document.forms[formName].userProfileRed0.value;
		selectedUserProfilesVlan.up7 = hm.util.getMultiSelectValues(Get("ethUserProfiles"));
	} else {
		selectedUserProfilesNetwork.up1 = document.forms[formName].ethDefaultRegUserprofile.value;
		selectedUserProfilesNetwork.up2 = document.forms[formName].ethDefaultAuthUserprofile.value;
		selectedUserProfilesNetwork.up3 = document.forms[formName].userProfileEth0.value;
		selectedUserProfilesNetwork.up4 = document.forms[formName].userProfileEth1.value;
		selectedUserProfilesNetwork.up5 = document.forms[formName].userProfileAgg0.value;
		selectedUserProfilesNetwork.up6 = document.forms[formName].userProfileRed0.value;
		selectedUserProfilesNetwork.up7 = hm.util.getMultiSelectValues(Get("ethUserProfiles"));
	}
}

function restoreTmpUserProfilesSelection(tobeWirelessRouting) {
	if (tobeWirelessRouting) {
		if (selectedUserProfilesNetwork && selectedUserProfilesNetwork.up1) {
			document.forms[formName].ethDefaultRegUserprofile.value = selectedUserProfilesNetwork.up1;
			document.forms[formName].ethDefaultAuthUserprofile.value = selectedUserProfilesNetwork.up2;
			document.forms[formName].userProfileEth0.value = selectedUserProfilesNetwork.up3;
			document.forms[formName].userProfileEth1.value = selectedUserProfilesNetwork.up4;
			document.forms[formName].userProfileAgg0.value = selectedUserProfilesNetwork.up5;
			document.forms[formName].userProfileRed0.value = selectedUserProfilesNetwork.up6;
			hm.util.refreshRightOfMultiSelectValue(Get("ethUserProfiles"), selectedUserProfilesNetwork.up7, Get("leftOptions_ethUserProfiles"));
		} else {
			hm.util.refreshRightOfMultiSelectValue(Get("ethUserProfiles"), null, Get("leftOptions_ethUserProfiles"));
		}
	} else {
		if (selectedUserProfilesVlan && selectedUserProfilesVlan.up1) {
			document.forms[formName].ethDefaultRegUserprofile.value = selectedUserProfilesVlan.up1;
			document.forms[formName].ethDefaultAuthUserprofile.value = selectedUserProfilesVlan.up2;
			document.forms[formName].userProfileEth0.value = selectedUserProfilesVlan.up3;
			document.forms[formName].userProfileEth1.value = selectedUserProfilesVlan.up4;
			document.forms[formName].userProfileAgg0.value = selectedUserProfilesVlan.up5;
			document.forms[formName].userProfileRed0.value = selectedUserProfilesVlan.up6;
			hm.util.refreshRightOfMultiSelectValue(Get("ethUserProfiles"), selectedUserProfilesVlan.up7, Get("leftOptions_ethUserProfiles"));
		} else {
			hm.util.refreshRightOfMultiSelectValue(Get("ethUserProfiles"), null, Get("leftOptions_ethUserProfiles"));
		}
	}
}

function refreshUserprofilesOfHiveAp(userProfiles) {
		hm.util.refreshAllSelectValue(document.forms[formName].ethDefaultRegUserprofile, userProfiles);
		hm.util.refreshAllSelectValue(document.forms[formName].ethDefaultAuthUserprofile, userProfiles);
		hm.util.refreshAllSelectValue(document.forms[formName].userProfileEth0, userProfiles);
		hm.util.refreshAllSelectValue(document.forms[formName].userProfileEth1, userProfiles);
		hm.util.refreshAllSelectValue(document.forms[formName].userProfileAgg0, userProfiles);
		hm.util.refreshAllSelectValue(document.forms[formName].userProfileRed0, userProfiles);
		hm.util.refreshAllSelectValue(Get("leftOptions_ethUserProfiles"), userProfiles);
}

function validateCapwapIp(operation) {
	var capwapIpNames = document.getElementById("capwapSelect");
	var capwapIpValue = document.getElementById("dataSource.capwapText");
	var showError = document.getElementById("errorDisplay");

	var capwapBackupIpNames = document.getElementById("capwapBackupSelect");
	var capwapBackupIpValue = document.getElementById("dataSource.capwapBackupText");
	var showBackupError = document.getElementById("errorBackupDisplay");

    if ("" != capwapIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(capwapIpNames, capwapIpValue)) {
	    	if(operation == 'create2' || operation == 'update2'){
				if (!hm.util.validateIpAddress(capwapIpValue.value)) {
					var message = hm.util.validateName(capwapIpValue.value, '<s:text name="hiveAp.capwap.server" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showError, message);
				   		hiveApTabs.set('activeIndex', 1);
				       	capwapIpValue.focus();
				       	return false;
				   	}
				}
	    	}
	    	document.getElementById("hiveAp_capwapIp").value = -1;
	    } else {
	    	document.getElementById("hiveAp_capwapIp").value = capwapIpNames.options[capwapIpNames.selectedIndex].value;
	    }
	} else {
		document.getElementById("hiveAp_capwapIp").value = -1;

	}
    if ("" != capwapBackupIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(capwapBackupIpNames, capwapBackupIpValue)) {
	    	if(operation == 'create2' || operation == 'update2'){
				if (!hm.util.validateIpAddress(capwapBackupIpValue.value)) {
					var message = hm.util.validateName(capwapBackupIpValue.value, '<s:text name="hiveAp.capwap.server.backup" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showBackupError, message);
				   		hiveApTabs.set('activeIndex', 1);
				       	capwapBackupIpValue.focus();
				       	return false;
				   	}
				}
	    	}
	    	document.getElementById("hiveAp_capwapBackupIp").value = -1;
	    } else {
	    	document.getElementById("hiveAp_capwapBackupIp").value = capwapBackupIpNames.options[capwapBackupIpNames.selectedIndex].value;
	}
	} else {
		document.getElementById("hiveAp_capwapBackupIp").value  = -1;
	}
    return true;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveAp" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedHiveApName" />\'</td>');
		</s:else>
	</s:else>
}

function enableMDMcheck(checked) {
	if (checked) {
		$("#enablemdmselect").show();
		$("#enableMdmTag").attr("value","enable");
	} else {
		$("#enablemdmselect").hide();
		$("#enableMdmTag").attr("value","unable");
	}
}

function checkIpStr(ipStr)
{ 
		if(ipStr == "") 
		{ 
		return false; 
		} 
		if(ipStr.match(/[\u4E00-\u9FA5]/)!=null) 
		{ 
		return false; 
		} 
		if(ipStr.length>15) 
		{ 
		return false; 
		} 
		if(ipStr.length<7) 
		{ 
		return false; 
		} 
		if(ipStr.indexOf(" ")!=-1) 
		{ 
		return false; 
		} 
		var ipDomainPat=/^(\d{1,3})[.](\d{1,3})[.](\d{1,3})[.](\d{1,3})$/; 
		var IPArray = ipStr.match(ipDomainPat); 
		 if (IPArray != null)
		 { 
			for (var i = 1; i <= 4; i++) 
			{ 
			       if (i == 1) 
			       { 
			           if (IPArray[i] == 0 || IPArray[i] > 255) 
						{ 
			               return false; 
			            }       
			       }       
			       else 
				   { 
						if(IPArray[i] > 255) 
						{ 
						return false; 
						} 
					} 
	        } 
			        return true; 
	    } 
			
			return false; 
	
} 
function checkGatewayStr(ipStr) 
{ 
	if(ipStr.indexOf("/")>=0){
		var arr2=ipStr.split("/");
			if(!checkIpStr(arr2[0]))
			{
			return false;
			}
			var netmask=/^(\d{1,2})$/; 
			var reg = new RegExp(netmask);
			if(parseInt(arr2[1])<4||parseInt(arr2[1])>31||!reg.test(arr2[1]))
			{
				 return false;
			}
		return true;
	}
	else
	{
		return false;
	}
} 
function matchIp(Str){
	var reg="ipAddress";
	var re=new RegExp(reg);
	if(Str.search(re)!=-1){
		return true;
	}
	return false;
}

function checkStaticIpAndGateway(Str){
	if(matchIp(Str)){
		if(!checkGatewayStr($("#"+Str).val()))
		{
	 		if(Str=="branchRouterEth0_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth0andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterEth1_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth1andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterEth2_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth2andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterEth3_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth3andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterEth4_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth4andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterUSB_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_usbandgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterWifi0_ipAddress")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_wifi0andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			}
			if(Str=="branchRouterWifi1_ipAddress"){
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_wifi1andgateway"),'<s:text name="router.port.setting.static.ip.warn" />');
				return false;
			} 
//	 		$("tr[name='errormessagetr']").remove();
//	 		 hm.util.reporttableFieldError(document.getElementById(formName +Str),'<s:text name="config.routing.policy.rules.sourcevalue.iprangewarning" />');
//	 		return false;
	
		}
			return true;
}else{
		if(!checkIpStr($("#"+Str).val()))
		{
			if(Str=="branchRouterEth0_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth0andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterEth1_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth1andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterEth2_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth2andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterEth3_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth3andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterEth4_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_eth4andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterUSB_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_usbandgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterWifi0_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_wifi0andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}
			if(Str=="branchRouterWifi1_gateway")
			{
				$("tr[name='errormessagetr']").remove();
				hm.util.reporttableFieldError(document.getElementById("error_message_wifi1andgateway"),'<s:text name="router.port.setting.default.gateway.wan" />');
				return false;
			}  
//	 		$("tr[name='errormessagetr']").remove();
//	 		 hm.util.reporttableFieldError(document.getElementById(formName +Str),'<s:text name="config.routing.policy.rules.sourcevalue.iprangewarning" />');
//	 		return false;
		}
		return true;
	}
}
	
	
function validateStaticIpandGateway(operation){
	if(operation=="create2"||operation=="update2"){
	var modelType=$("#"+formName + "_dataSource_hiveApModel").val();
	if(modelType==MODEL_330|| modelType==MODEL_350){
		 if($("#branchRouterEth0_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth0_ipAddress")){
				$("#branchRouterEth0_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth0_gateway")){
				$("#branchRouterEth0_gateway").focus();
				return false;
			}
			var tempVa = validateSubnet("branchRouterEth0_ipAddress","branchRouterEth0_gateway","error_message_eth0andgateway");
			if(!tempVa){
				return false;
			}
		}
	 	if($("#eth1strTd").html() == "WAN" &&
	 			$("#branchRouterEth1_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth1_ipAddress")){
				$("#branchRouterEth1_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth1_gateway")){
				$("#branchRouterEth1_gateway").focus();
				return false;
			}
			var tempEth1 = validateSubnet("branchRouterEth1_ipAddress","branchRouterEth1_gateway","error_message_eth1andgateway");
			if(!tempEth1){
				return false;
			}
		}   
		if($("#branchRouterUSB_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterUSB_ipAddress")){
				$("#branchRouterUSB_gateway").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterUSB_gateway")){
				$("#branchRouterUSB_gateway").focus();
				return false;
			}
			var tempUsb = validateSubnet("branchRouterUSB_ipAddress","branchRouterUSB_gateway","error_message_usbandgateway");
			if(!tempUsb){
				return false;
			}
			
		}
	}else{
		if($("#branchRouterEth0_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth0_ipAddress")){
				$("#branchRouterEth0_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth0_gateway")){
				$("#branchRouterEth0_gateway").focus();
				return false;
			}
			var tempEth0 = validateSubnet("branchRouterEth0_ipAddress","branchRouterEth0_gateway","error_message_eth0andgateway");
			if(!tempEth0){
				return false;
			}
		}
		 if($("#eth1strTd").html() == "WAN" && 
				 $("#branchRouterEth1_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth1_ipAddress")){
				$("#branchRouterEth1_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth1_gateway")){
				$("#branchRouterEth1_gateway").focus();
				return false;
			}
			var tempEth1 = validateSubnet("branchRouterEth1_ipAddress","branchRouterEth1_gateway","error_message_eth1andgateway");
			if(!tempEth1){
				return false;
			}
		}
		 if($("#branchRouterUSB_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterUSB_ipAddress")){
				$("#"+formName + "_branchRouterUSB_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterUSB_gateway")){
				$("#branchRouterUSB_gateway").focus();
				return false;
			}
			var tempUsb = validateSubnet("branchRouterUSB_ipAddress","branchRouterUSB_gateway","error_message_usbandgateway");
			if(!tempUsb){
				return false;
			}
			
		}
		 if($("#eth2strTd").html() == "WAN"  && 
				 $("#branchRouterEth2_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth2_ipAddress")){
				$("#branchRouterEth2_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth2_gateway")){
				$("#"+formName + "_branchRouterEth2_gateway").focus();
				return false;
			}
			var tempEth2 = validateSubnet("branchRouterEth2_ipAddress","branchRouterEth2_gateway","error_message_eth2andgateway");
			if(!tempEth2){
				return false;
			}
		}
		if($("#eth3strTd").html() == "WAN" &&
				$("#branchRouterEth3_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth3_ipAddress")){
				$("#branchRouterEth3_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth3_gateway")){
				$("#branchRouterEth3_gateway").focus();
				return false;
				
			}
			var tempEth3 = validateSubnet("branchRouterEth3_ipAddress","branchRouterEth3_gateway","error_message_eth3andgateway");
			if(!tempEth3){
				return false;
			}
		}
		 if($("#eth4strTd").html() == "WAN" &&
				 $("#branchRouterEth4_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterEth4_ipAddress")){
				$("#branchRouterEth4_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterEth4_gateway")){
				$("#branchRouterEth4_gateway").focus();
				return false;
			}
			var tempEth4 = validateSubnet("branchRouterEth4_ipAddress","branchRouterEth4_gateway","error_message_eth4andgateway");
			if(!tempEth4){
				return false;
			}
		}
		 /* if($("#branchRouterWifi0_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterWifi0_ipAddress")){
				$("#branchRouterWifi0_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterWifi0_gateway")){
				$("#branchRouterWifi0_gateway").focus();
				return false;
			}
		 var tempWifi0 = validateSubnet("branchRouterWifi0_ipAddress","branchRouterWifi0_gateway","error_message_wifi0andgateway");
			if(!tempWifi0){
				return false;
			}
		} */
		 if($("#branchRouterWifi1_connectionType").val()==DEVICE_CONNECT_STATIC){
			if(!checkStaticIpAndGateway("branchRouterWifi1_ipAddress")){
				$("#branchRouterWifi1_ipAddress").focus();
				return false;
			}
			if(!checkStaticIpAndGateway("branchRouterWifi1_gateway")){
				$("#branchRouterWifi1_gateway").focus();
				return false;
			}
			var tempWifi1 = validateSubnet("branchRouterWifi1_ipAddress","branchRouterWifi1_gateway","error_message_wifi1andgateway");
			if(!tempWifi1){
				return false;
			}
		} 
		
		}
	}
	return true;
}
function changeModelDiv()
{
	if($("#hiveAp_dataSource_deviceType").val()=="3"){
		
			$("#interfaceEthSettingTable").remove();
		
	 		var ethtable=$("#interfaceEthSettingTableTemp").clone();
	 		ethtable.attr("id","interfaceEthSettingTable");
	 		$("#routerdiv").html(ethtable);

		}else {
			
			$("#interfaceEthSettingTable").remove();
			
	 		var ethtable2=$("#interfaceEthSettingTableTemp2").clone();
	 		ethtable2.attr("id","interfaceEthSettingTable");
	 		$("#routerdiv").html(ethtable2);
			
			
		}
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;

	if(selectApModel == BR100 && deviceType == DEVICE_TYPE_HIVEAP){
		$("#eth0strTd").html("Backhaul");
		$("#eth0TdDiv").hide();
		$("#eth0ConnectTdDiv").hide();
		$("#eth0_enableNat").hide();
		$("#eth0StaticIP").hide();
		$("#eth0DefaultGateway").hide();
		$("#eth0_disablePortForwarding").hide();
		$("#eth0_voip_detail").hide();
	
	}

}
function changeIPandGateway(obj,name){
if("eth0"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#eth0StaticIP").attr("style","display:");
		$("#eth0DefaultGateway").attr("style","display:");
		
	}else{
		$("#eth0StaticIP").attr("style","display:none");
		$("#eth0DefaultGateway").attr("style","display:none");
		
		
	}
	if($(obj).val()==DEVICE_CONNECT_PPPOE){
		$("#eth0_pppoeAuthrouter").attr("style","display:");
		
	}else{
		$("#eth0_pppoeAuthrouter").attr("style","display:none");
	}
}else if("eth1"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#eth1StaticIP").attr("style","display:");
		$("#eth1DefaultGateway").attr("style","display:");
	}else{
		$("#eth1StaticIP").attr("style","display:none");
		$("#eth1DefaultGateway").attr("style","display:none");
	}
}else if("eth2"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#eth2StaticIP").attr("style","display:");
		$("#eth2DefaultGateway").attr("style","display:");
	}else{
		$("#eth2StaticIP").attr("style","display:none");
		$("#eth2DefaultGateway").attr("style","display:none");
	}	
}else if("eth3"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#eth3StaticIP").attr("style","display:");
		$("#eth3DefaultGateway").attr("style","display:");
	}else{
		$("#eth3StaticIP").attr("style","display:none");
		$("#eth3DefaultGateway").attr("style","display:none");
	}	
}else if("eth4"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#eth4StaticIP").attr("style","display:");
		$("#eth4DefaultGateway").attr("style","display:");
	}else{
		$("#eth4StaticIP").attr("style","display:none");
		$("#eth4DefaultGateway").attr("style","display:none");
	}	
}else if("usb"==name){
		if($(obj).val()==DEVICE_CONNECT_STATIC){
			$("#usbStaticIP").attr("style","display:");
			$("#usbDefaultGateway").attr("style","display:");
		}else{
			$("#usbStaticIP").attr("style","display:none");
			$("#usbDefaultGateway").attr("style","display:none");
		}
}else if("wifi0"==name){
		if($(obj).val()==DEVICE_CONNECT_STATIC){
			$("#wifi0StaticIP").attr("style","display:");
			$("#wifi0DefaultGateway").attr("style","display:");
		}else{
			$("#wifi0StaticIP").attr("style","display:none");
			$("#wifi0DefaultGateway").attr("style","display:none");
		}
}else if("wifi1"==name){
	if($(obj).val()==DEVICE_CONNECT_STATIC){
		$("#wifi1StaticIP").attr("style","display:");
		$("#wifi1DefaultGateway").attr("style","display:");
	}else{
		$("#wifi1StaticIP").attr("style","display:none");
		$("#wifi1DefaultGateway").attr("style","display:none");
	}

}	
	
}
function selector(id,selectvalue)
{
	for(var i=0;i<$("#"+id).children().length;i++)
	{
		
		if($("#"+id).children().eq(i).val()==selectvalue)
		{
			$("#"+id).children().eq(i).attr({selected:true});
		}
		
	}
}
function changewifimode(wifiname){
	if("wifi0" == wifiname){
		if($("#wifi0ModeBR").val()=="5")
		{
			selector("wifi1ModeBR",1);
			$("#br_wifi0_setting").attr("style","display:");
			$("#br_wifi1_setting").attr("style","display:none");
			$("#eth5_voip_detail").attr("style","display:");
			$("#eth6_voip_detail").attr("style","display:none");
		}else{
			$("#br_wifi0_setting").attr("style","display:none");
			$("#eth5_voip_detail").attr("style","display:none");
			
		}
		if($("#wifi1ModeBR").val()=="5"){
			$("#br_wifi1_setting").attr("style","display:");
			$("#eth6_voip_detail").attr("style","display:");
			
		}
	}else{
		if($("#wifi1ModeBR").val()=="5")
		{
			selector("wifi0ModeBR",1);
			$("#br_wifi0_setting").attr("style","display:none");
			$("#br_wifi1_setting").attr("style","display:");
			$("#eth5_voip_detail").attr("style","display:none");
			$("#eth6_voip_detail").attr("style","display:");
		
		}else{
			$("#eth6_voip_detail").attr("style","display:none");
			$("#br_wifi1_setting").attr("style","display:none");
		}
		if($("#wifi0ModeBR").val()=="5"){
			$("#br_wifi0_setting").attr("style","display:");
			$("#eth5_voip_detail").attr("style","display:");
		}
	}
}

function enableCellularModem(enableFlag) {
	if(enableFlag == true){
		$("#br_usb_setting").attr("style","display:");
	}
	else {
		$("#br_usb_setting").attr("style","display:none");
	}
	var wanCount = getWanCount();
	updateWanPortSelectList(wanCount);
}

function getWanCount() {
	var wanCount = 1; //eth0 is wan
 	if($("#eth1strTd").html().trim()=="WAN" || $("#eth2strTd").html().trim()=="WAN" 
		  || $("#eth3strTd").html().trim()=="WAN" || $("#eth4strTd").html().trim()=="WAN") {
		wanCount++;
	}
 	if($("#usbstrTd").html().trim() == 'WAN') {
		//alert(document.getElementById("br_usb_setting").style.display);
		if  (Get("br_usb_setting").style.display != "none") { //not hidden
				wanCount++;	
		}

	}
 	if($("#wifi0strTd").html().trim() == 'WAN'){
 		wanCount++;
	}	
	return wanCount;
}

	
</script>
