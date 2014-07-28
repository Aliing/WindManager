<%@page import="com.ah.bo.useraccess.UserProfile"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.network.VpnService"%>
<%@page import="com.ah.bo.network.UserProfileForTrafficL3"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0 4px 6px 4px;
}
-->
</style>
<script type="text/javascript">
var formName = 'vpnServices';
var VPN_SERVER_TYPE_SINGLE = <%=VpnService.VPN_SERVER_TYPE_SINGLE%>;
var VPN_SERVER_TYPE_REDUNDANT = <%=VpnService.VPN_SERVER_TYPE_REDUNDANT%>;
var IPSEC_VPN_LAYER_2 = <%=VpnService.IPSEC_VPN_LAYER_2%>;
var IPSEC_VPN_LAYER_3 = <%=VpnService.IPSEC_VPN_LAYER_3%>;
var MAX_IP_POOL_SIZE = <%=VpnService.MAX_IP_POOL_SIZE%>;
/*Rollback to Support 4096 for CVG Device*/
var MAX_IP_POOL_SIZE_VPN2 = <%=VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE%>;

var USER_PROFILES_TUNNEL_ALLL3 = <%=VpnService.IPSEC_VPN_LAYER_3%>;
var USER_PROFILES_SPLIT_TUNNELL3 = <%=VpnService.USER_PROFILES_SPLIT_TUNNELL3%>;

var ROUTE_VPNTUNNEL_TRAFFIC_ALL = <%=VpnService.ROUTE_VPNTUNNEL_TRAFFIC_ALL%>;
var ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL = <%=VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL%>;
var USER_PROFILES_TUNNEL_ALL = <%=VpnService.USER_PROFILES_TUNNEL_ALL%>;

var VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS = <%=UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS%>;

function showCredentialsContent(){
	showHideContent("credentials","");
}

function showServerAdvContent(){
	showHideContent("serverAdv","");
}

function showClientAdvContent(){
	showHideContent("clientAdv","");
}

function showIpsecSettingContent(){
	showHideContent("ipsecSetting","");
}

function requestCredentialInfoForLayer3Ipsec(){
	if(Get(formName + "_dataSource_ipsecVpnType"+IPSEC_VPN_LAYER_3).checked){
		var table = Get("credential");
		if(table.rows.length < 1){
			requestCredentialInfo(MAX_IP_POOL_SIZE);
		}
	}
}

function submitAction(operation) {
	<s:if test="%{jsonMode}">
		saveVpnServiceToNetWorkPolicy(operation);
    </s:if>
    <s:else>
	    if (validate(operation)) {
			showProcessing();
			document.forms[formName].operation.value = operation;
			//save style values
			Get(formName + "_dataSource_credentialDisplayStyle").value = Get("credentials").style.display;
			Get(formName + "_dataSource_serverAdvDisplayStyle").value = Get("serverAdv").style.display;
			Get(formName + "_dataSource_clientAdvDisplayStyle").value = Get("clientAdv").style.display;
			Get(formName + "_dataSource_certificateDisplayStyle").value = Get("ipsecSetting").style.display;
		    document.forms[formName].submit();
		}
    </s:else>
}

function popUpDlgInVpnService(opera){
	if("vpnGateWayEdit" == opera){
		var url =  "<s:url action='hiveAp' includeParams='none' />"+
		"?operation=edit2"+
		"&id="+Get(formName + "_vpnGateWay").value+
		"&vpnGateWayDlg=true"+
		"&jsonMode=true"+
		"&parentDomID="+formName + "_vpnGateWay"+
		"&ignore="+new Date().getTime();
		openIFrameDialog(950, 650, url);
		return;
	}
	if("newDnsIp" == opera || "editDnsIp" == opera){
		var url =  "<s:url action='vpnServices' includeParams='none' />"+
		"?operation=" + opera +
		"&parentDomID=" + "dnsSelect" +
		"&jsonMode=true" +
		"&vpnServiceDnsIpFlag=true" +
		"&ignore="+new Date().getTime();
		if("editDnsIp" == opera){
			url = url + "&dnsIp=" + Get("dnsSelect").value;
		}
		openIFrameDialog(950,450,url);
		return;
	}
	if("importCa" == opera || "importCert" == opera || "importKey" == opera){
		var url =  "<s:url action='vpnServices' includeParams='none' />"+
		"?operation="+opera+
		"&jsonMode=true"+
		"&ignore="+new Date().getTime();
		if("importCa" == opera){
			url = url + "&parentDomID=" + formName + "_dataSource" +"_rootCa";
		}else if("importCert" == opera){
			url = url + "&parentDomID=" + formName + "_dataSource" +"_certificate";
		}else if("importKey" == opera){
			url = url + "&parentDomID=" + formName + "_dataSource" +"_privateKey";
		}
		openIFrameDialog(790, 380, url);
		return;
	}
}

function saveVpnServiceToNetWorkPolicy(opera) {
	var url = "<s:url action='vpnServices' includeParams='none' />?operation="+ opera +"&jsonMode=true&ignore="+new Date().getTime();
	if(!validateVpnForJson(opera)){
		return false;
	}
	if("vpnGateWayEdit" == opera
			|| 'importCa' ==  opera ||'importCert' == opera|| 'importKey' == opera
			|| "newDnsIp" == opera || "editDnsIp" == opera){
		popUpDlgInVpnService(opera);
	}else{
		YAHOO.util.Connect.setForm(document.getElementById("vpnServices"));
		if("addVpnGateWay" == opera
				|| "removeVpnGateWay" == opera
				|| "removeVpnGateWayNone" == opera){
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succReloadVpn, failure : resultDoNothing, timeout: 60000}, null);
		} else {
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveVpn, failure : resultDoNothing, timeout: 60000}, null);
		}
	}
}

var succReloadVpn=function(o){
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 4000);
}

var succSaveVpn = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if(subDrawerOperation == "updateVpnService"){
			succEditVpnService(o);
		}else{
			succNewVpn(o);
		}
		return;
	}
	if (details.t) {
		if (details.n && details.id){
            backNetWorkPolicy();
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation=finishSelectVpn&vpnSelectedId="+details.id+"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succShowSelectVpn, failure : finishShowVpnDoNothing, timeout: 60000}, null);
		} else {
			fetchConfigTemplate2Page(true);
		}
	}
}


var succShowSelectVpn = function (o){
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		hideSubDialogOverlay();
		yDom.replaceClass(Get("netWorkPolicyContentId"), "hidden", "block");
		set_innerHTML("netWorkPolicyContentId",o.responseText);
		return;
	}
	var hideErrNotes = function () {
		hm.util.wipeOut('errNote', 800);
	}
	hm.util.show("errNote");
	Get("errNote").className="noteError";
	Get("errNote").innerHTML=details.e;
	var notesTimeoutId = setTimeout("hideErrNotes()", 4000);
};

var finishShowVpnDoNothing = function(o){

}

function validateVpnForJson(operation){
	if (validate(operation)) {
		//save style values
		Get(formName + "_dataSource_credentialDisplayStyle").value = Get("credentials").style.display;
		Get(formName + "_dataSource_serverAdvDisplayStyle").value = Get("serverAdv").style.display;
		Get(formName + "_dataSource_clientAdvDisplayStyle").value = Get("clientAdv").style.display;
		Get(formName + "_dataSource_certificateDisplayStyle").value = Get("ipsecSetting").style.display;
		return true;
	}
	return false;
}

function validate(operation) {
	if(operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	if(operation == 'addVpnGateWay'){
		var vpnGateWays = Get(formName + "_vpnGateWay");
		if(vpnGateWays.value == -1){
			hm.util.reportFieldError(vpnGateWays, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.gateway.settings.gateways" /></s:param></s:text>');
			vpnGateWays.focus();
			return false;
		}
		if(!validateExternalIpAddress()){
			return false;
		}
		if(!validateVpnGateWayCount()){
			return false;
		}
		return true;
	}

	if(operation == 'vpnGateWayEdit'){
		var value = hm.util.validateListSelection(formName + "_vpnGateWay");
		if(value < 0){
			return false;
		}
		document.forms[formName].vpnGateWayId.value = value;
		return true;
	}
	if (operation == 'removeVpnGateWay' || operation == 'removeVpnGateWayNone') {
		var table = document.getElementById("checkAllGateway");
		var cbs = document.getElementsByName('vpnGatewayIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
	           hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.vpn.gateway.settings.gateways" /></s:param></s:text>');
			return false;
		}
		return true;
	}

	if(operation == 'newDnsIp' || operation == 'editDnsIp'
		|| operation == 'importCa' ||operation == 'importCert' || operation == 'importKey'){
		if(!validatePhase1LifeTime()){
			return false;
		}
		if(!validatePhase2LifeTime()){
			return false;
		}
		if(!validateDpd()){
			return false;
		}
		if(!validateAmrp()){
			return false;
		}
		if(operation == "editDnsIp"){
			var value = hm.util.validateListSelection("dnsSelect");
			if(value < 0){
				return false;
			}else{
				document.forms[formName].dnsIp.value = value;
			}
		}else{
			var dnsIpNames = document.getElementById("dnsSelect");
			document.forms[formName].dnsIp.value = dnsIpNames.options[dnsIpNames.selectedIndex].value;
		}
		return true;
	}
	if(!validateProfileName()){
		return false;
	}


	if(Get(formName + "_dataSource_ipsecVpnType"+IPSEC_VPN_LAYER_2).checked){
		if(!validatePublicIp1()){
			return false;
		}
		if(!validatePrivateIp1()){
			return false;
		}
		if(!validatePoolStart1()){
			return false;
		}
		if(!validatePoolEnd1()){
			return false;
		}
		if(!validatePoolNetmask1()){
			return false;
		}
		if(!validatePoolRange1()){
			return false;
		}
		//some fields of server 2 is not empty, need to check
		if(Get(formName + "_dataSource_vpnServerType"+VPN_SERVER_TYPE_REDUNDANT).checked){
			if(!validatePublicIp2()){
				return false;
			}
			if(!validatePrivateIp2()){
				return false;
			}
			if(!validatePoolStart2()){
				return false;
			}
			if(!validatePoolEnd2()){
				return false;
			}
			if(!validatePoolNetmask2()){
				return false;
			}
			if(!validatePoolRange2()){
				return false;
			}
			if(!validateServer2Ip()){
				return false;
			}
			var startIp1 = Get(formName + "_dataSource_clientIpPoolStart1");
			var endIp1 = Get(formName + "_dataSource_clientIpPoolEnd1");
			var startIp2 = Get(formName + "_dataSource_clientIpPoolStart2");
			var endIp2 = Get(formName + "_dataSource_clientIpPoolEnd2");
			if(isIpRangeAcross(startIp1.value,endIp1.value,startIp2.value,endIp2.value)){
				hm.util.reportFieldError(startIp2, '<s:text name="error.hiveAp.vpn.ippool.across"><s:param><s:text name="config.vpn.service.server2.client.ippool" /></s:param><s:param><s:text name="config.vpn.service.server1.client.ippool" /></s:param></s:text>');
				startIp2.focus();
				return false;
			}
		}
		getUserProfilesSettingsForL2();
	}else{
		var table = Get("checkAllGateway");
		var gateWaySettings = Get("vpnGateWayTable");
		var rows = gateWaySettings.rows.length
		if(rows < 1)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.gateway.settings.gateways" /></s:param></s:text>');
			table.focus();
       		return false;
		}
		if(operation == 'create'+'<s:property value="lstForward"/>'
		    	|| operation == 'update' + '<s:property value="lstForward"/>'
		    	|| operation == 'create'
		    	|| operation == 'update'){
			var externalIpAddresses = document.getElementsByName('externalIpAddresses');
			for(var i = 0; i < externalIpAddresses.length; i ++) {
				if(!checkIpAddress(externalIpAddresses[i], '<s:text name="config.vpn.gateway.settings.gateways.external.ipaddress" />'+' in '+(i+1)+' row')) {
						return false;

				}
			}
		}
	}

	if(!validateServerSettings()){
		return false;
	}
	if(!validatePhase1LifeTime()){
		return false;
	}
	if(!validatePhase2LifeTime()){
		return false;
	}
	if(!validateDnsIp(operation)){
		return false;
	}
	if(!validateDpd()){
		return false;
	}
	if(!validateAmrp()){
		return false;
	}

	return true;
}

function checkIpAddress(ip, title) {
	var table = document.getElementById("vpnGateWayTable");
	if (ip.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        ip.focus();
        return false;
    } else if (!hm.util.validateIpAddress(ip.value)) {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		ip.focus();
		return false;
	}
	return true;
}

function validateProfileName() {
    var inputElement = Get(formName + "_dataSource_profileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.vpn.service.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateExternalIpAddress(){
    var ipAddressEl = Get(formName + "_externalIpAddress");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.gateway.settings.gateways.external.ipaddress" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.gateway.settings.gateways.external.ipaddress" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validateVpnGateWayCount(){
	var gateWaySettings = Get("vpnGateWayTable");
	<s:if test="%{dataSource.vpnGateWaysSetting.size() >= 2}">
		hm.util.reportFieldError(gateWaySettings, '<s:text name="error.vpnGateWayMoreThanTwo"></s:text>');
		return false;
	</s:if>
	<s:else>
		return true;
	</s:else>
}


function validatePrivateIp1(){
	var ipAddressEl = Get(formName + "_dataSource_serverPrivateIp1");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server1.privateIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server1.privateIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePublicIp1(){
	var ipAddressEl = Get(formName + "_dataSource_serverPublicIp1");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server1.publicIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server1.publicIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePrivateIp2(){
	var ipAddressEl = Get(formName + "_dataSource_serverPrivateIp2");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server2.privateIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server2.privateIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePublicIp2(){
	var ipAddressEl = Get(formName + "_dataSource_serverPublicIp2");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server2.publicIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server2.publicIp" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePoolStart1(){
	var ipAddressEl = Get(formName + "_dataSource_clientIpPoolStart1");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.start" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.start" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePoolEnd1(){
	var ipAddressEl = Get(formName + "_dataSource_clientIpPoolEnd1");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.end" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.end" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePoolNetmask1(){
	var netmaskEl = Get(formName + "_dataSource_clientIpPoolNetmask1");
	if (netmaskEl.value.length == 0) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.netmask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
  	}
	if (! hm.util.validateMask(netmaskEl.value)) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.netmask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
	}
	return true;
}

function validatePoolRange1(){
	var startIp1 = Get(formName + "_dataSource_clientIpPoolStart1");
	var endIp1 = Get(formName + "_dataSource_clientIpPoolEnd1");
	var netmask1 = Get(formName + "_dataSource_clientIpPoolNetmask1");
	var message = hm.util.validateIpSubnet(startIp1.value, '<s:text name="config.vpn.service.server.ippool.start" />', endIp1.value, '<s:text name="config.vpn.service.server.ippool.end" />', netmask1.value, true, MAX_IP_POOL_SIZE_VPN2);
	if(null != message){
		hm.util.reportFieldError(startIp1, message);
		return false;
	}
	return true;
}

function validateServer2Ip(){
	var ipAddressPublicEl = Get(formName + "_dataSource_serverPublicIp1");
	var ipAddressPublicEl2 = Get(formName + "_dataSource_serverPublicIp2");
	if(ipAddressPublicEl.value.length > 0 && ipAddressPublicEl2.value.length > 0 && ipAddressPublicEl.value == ipAddressPublicEl2.value){
		hm.util.reportFieldError(ipAddressPublicEl2, '<s:text name="error.equal"><s:param><s:text name="config.vpn.service.server2.publicIp" /></s:param><s:param><s:text name="config.vpn.service.server1.publicIp" /></s:param></s:text>');
		return false;
	}
	var ipAddressEl = Get(formName + "_dataSource_serverPrivateIp1");
	var ipAddressEl2 = Get(formName + "_dataSource_serverPrivateIp2");
	if(ipAddressEl.value.length > 0 && ipAddressEl2.value.length > 0 && ipAddressEl.value == ipAddressEl2.value){
		hm.util.reportFieldError(ipAddressEl2, '<s:text name="error.equal"><s:param><s:text name="config.vpn.service.server2.privateIp" /></s:param><s:param><s:text name="config.vpn.service.server1.privateIp" /></s:param></s:text>');
		return false;
	}
	return true;
}

function validatePoolRange2(){
	var startIp2 = Get(formName + "_dataSource_clientIpPoolStart2");
	var endIp2 = Get(formName + "_dataSource_clientIpPoolEnd2");
	var netmask2 = Get(formName + "_dataSource_clientIpPoolNetmask2");
	var message = hm.util.validateIpSubnet(startIp2.value, '<s:text name="config.vpn.service.server.ippool.start" />', endIp2.value, '<s:text name="config.vpn.service.server.ippool.end" />', netmask2.value, true, MAX_IP_POOL_SIZE_VPN2);
	if(null != message){
		hm.util.reportFieldError(startIp2, message);
		return false;
	}
	return true;
}

function validatePoolStart2(){
	var ipAddressEl = Get(formName + "_dataSource_clientIpPoolStart2");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.start" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.start" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePoolEnd2(){
	var ipAddressEl = Get(formName + "_dataSource_clientIpPoolEnd2");
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.end" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.end" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validatePoolNetmask2(){
	var netmaskEl = Get(formName + "_dataSource_clientIpPoolNetmask2");
	if (netmaskEl.value.length == 0) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.server.ippool.netmask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
  	}
	if (! hm.util.validateMask(netmaskEl.value)) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.server.ippool.netmask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
	}
	return true;
}

function validateServerSettings(){
	var rootCaEl = Get(formName + "_dataSource_rootCa");
	if (rootCaEl.value.length == 0) {
		hm.util.reportFieldError(rootCaEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.rootCa" /></s:param></s:text>');
		showIpsecSettingContent();
		rootCaEl.focus();
		return false;
  	}
	var certificateEl = Get(formName + "_dataSource_certificate");
	if (certificateEl.value.length == 0) {
		hm.util.reportFieldError(certificateEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.certificate" /></s:param></s:text>');
		showIpsecSettingContent();
		certificateEl.focus();
		return false;
  	}
  	var privateKeyEl = Get(formName + "_dataSource_privateKey");
  	if (privateKeyEl.value.length == 0) {
		hm.util.reportFieldError(privateKeyEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.privateKey" /></s:param></s:text>');
		showIpsecSettingContent();
		privateKeyEl.focus();
		return false;
  	}
	return true;
}

function validatePhase1LifeTime(){
	var phase1LifeTimeEl = Get(formName + "_dataSource_phase1LifeTime");
	if (phase1LifeTimeEl.value.length == 0) {
		hm.util.reportFieldError(phase1LifeTimeEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.phase1.lifetime" /></s:param></s:text>');
		showServerAdvContent();
		phase1LifeTimeEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(phase1LifeTimeEl.value, '<s:text name="config.vpn.service.phase1.lifetime" />',
	                                           <s:property value="%{phase1LifeTimeRange.min()}" />,
	                                           <s:property value="%{phase1LifeTimeRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(phase1LifeTimeEl, message);
		showServerAdvContent();
		phase1LifeTimeEl.focus();
		return false;
	}
	return true;
}

function validatePhase2LifeTime(){
	var phase2LifeTimeEl = Get(formName + "_dataSource_phase2LifeTime");
	if (phase2LifeTimeEl.value.length == 0) {
		hm.util.reportFieldError(phase2LifeTimeEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.phase2.lifetime" /></s:param></s:text>');
		showServerAdvContent();
		phase2LifeTimeEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(phase2LifeTimeEl.value, '<s:text name="config.vpn.service.phase2.lifetime" />',
	                                           <s:property value="%{phase2LifeTimeRange.min()}" />,
	                                           <s:property value="%{phase2LifeTimeRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(phase2LifeTimeEl, message);
		showServerAdvContent();
		phase2LifeTimeEl.focus();
		return false;
	}
	return true;
}

function validateDpd(){
	var dpdIdelIntervalEl = Get(formName + "_dataSource_dpdIdelInterval");
	if (dpdIdelIntervalEl.value.length == 0) {
		hm.util.reportFieldError(dpdIdelIntervalEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.dpd.interval" /></s:param></s:text>');
		showClientAdvContent();
		dpdIdelIntervalEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(dpdIdelIntervalEl.value, '<s:text name="config.vpn.service.dpd.interval" />',
	                                           <s:property value="%{dpdIdelIntervalRange.min()}" />,
	                                           <s:property value="%{dpdIdelIntervalRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(dpdIdelIntervalEl, message);
		showClientAdvContent();
		dpdIdelIntervalEl.focus();
		return false;
	}

	var dpdRetryEl = Get(formName + "_dataSource_dpdRetry");
	if (dpdRetryEl.value.length == 0) {
		hm.util.reportFieldError(dpdRetryEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.dpd.retry" /></s:param></s:text>');
		showClientAdvContent();
		dpdRetryEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(dpdRetryEl.value, '<s:text name="config.vpn.service.dpd.retry" />',
	                                           <s:property value="%{dpdRetryRange.min()}" />,
	                                           <s:property value="%{dpdRetryRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(dpdRetryEl, message);
		showClientAdvContent();
		dpdRetryEl.focus();
		return false;
	}

	var dpdRetryIntervalEl = Get(formName + "_dataSource_dpdRetryInterval");
	if (dpdRetryIntervalEl.value.length == 0) {
		hm.util.reportFieldError(dpdRetryIntervalEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.dpd.retryInterval" /></s:param></s:text>');
		showClientAdvContent();
		dpdRetryIntervalEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(dpdRetryIntervalEl.value, '<s:text name="config.vpn.service.dpd.retryInterval" />',
	                                           <s:property value="%{dpdRetryIntervalRange.min()}" />,
	                                           <s:property value="%{dpdRetryIntervalRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(dpdRetryIntervalEl, message);
		showClientAdvContent();
		dpdRetryIntervalEl.focus();
		return false;
	}
	return true;
}

function validateAmrp(){
	var amrpIntervalEl = Get(formName + "_dataSource_amrpInterval");
	if (amrpIntervalEl.value.length == 0) {
		hm.util.reportFieldError(amrpIntervalEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.amrp.interval" /></s:param></s:text>');
		showClientAdvContent();
		amrpIntervalEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(amrpIntervalEl.value, '<s:text name="config.vpn.service.amrp.interval" />',
	                                           <s:property value="%{amrpIntervalRange.min()}" />,
	                                           <s:property value="%{amrpIntervalRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(amrpIntervalEl, message);
		showClientAdvContent();
		amrpIntervalEl.focus();
		return false;
	}

	var amrpRetryEl = Get(formName + "_dataSource_amrpRetry");
	if (amrpRetryEl.value.length == 0) {
		hm.util.reportFieldError(amrpRetryEl, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.service.amrp.retry" /></s:param></s:text>');
		showClientAdvContent();
		amrpRetryEl.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(amrpRetryEl.value, '<s:text name="config.vpn.service.amrp.retry" />',
	                                           <s:property value="%{amrpRetryRange.min()}" />,
	                                           <s:property value="%{amrpRetryRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(amrpRetryEl, message);
		showClientAdvContent();
		amrpRetryEl.focus();
		return false;
	}
	return true;
}

function onLoadPage() {
	createWaitingPanel();
	if(Get(formName + "_dataSource_profileName").disabled == false){
		Get(formName + "_dataSource_profileName").focus();
	}
	showError = document.getElementById("errorDisplay");
	bindPoolListener();
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"320px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Updating server - client credentials...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function validateDnsIp(operation) {
	var dnsIpNames = document.getElementById("dnsSelect");
	var dnsIpValue = document.getElementById("dataSource.inputText");
	var showError = document.getElementById("errorDisplay");

    if ("" != dnsIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(dnsIpNames, dnsIpValue)) {
	    	if(operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'
	    		|| operation == 'update' || operation == 'create'){
				if (!hm.util.validateIpAddress(dnsIpValue.value)) {
			   		showServerAdvContent();
					hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.service.client.dns.server" /></s:param></s:text>');
			       	dnsIpValue.focus();
			       	return false;
				}
	    	}
	    	document.forms[formName].dnsIp.value = -1;
	    } else {
	    	document.forms[formName].dnsIp.value = dnsIpNames.options[dnsIpNames.selectedIndex].value;
		}
	} else {
		document.forms[formName].dnsIp.value = -1;
	}
    return true;
}

function ikeValidateEnable(checked){
	document.getElementById(formName + "_dataSource_serverIkeId").disabled = !checked;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vpnServices" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
//10.155.10.1
function getActualIpAddressCount(startIpAddress, endIpAddress, netmask){
	var ipValue1 = hm.util.getIpAddressValue(startIpAddress);
	var ipValue2 = hm.util.getIpAddressValue(endIpAddress);
	return ipValue2 - ipValue1 + 1;
}

function isIpRangeAcross(range1Start, range1End, range2Start, range2End){
	var ip1Start = hm.util.getIpAddressValue(range1Start);
	var ip1End = hm.util.getIpAddressValue(range1End);
	var ip2Start = hm.util.getIpAddressValue(range2Start);
	var ip2End = hm.util.getIpAddressValue(range2End);
	if((ip1Start < ip2Start && ip1End < ip2Start) || (ip1Start > ip2End && ip1End > ip2End)){
		return false;
	}
	return true;
}

function bindPoolListener(){
	var start_pool1 = Get(formName + "_dataSource_clientIpPoolStart1");
	var end_pool1 = Get(formName + "_dataSource_clientIpPoolEnd1");
	var mask_pool1 = Get(formName + "_dataSource_clientIpPoolNetmask1");

	var start_pool2 = Get(formName + "_dataSource_clientIpPoolStart2");
	var end_pool2 = Get(formName + "_dataSource_clientIpPoolEnd2");
	var mask_pool2 = Get(formName + "_dataSource_clientIpPoolNetmask2");

	start_pool1.onblur = end_pool1.onblur = mask_pool1.onblur =
	start_pool2.onblur = end_pool2.onblur = mask_pool2.onblur = (function(start1, end1, mask1, start2, end2, mask2){
		return function(){poolSizeChanged(start1, end1, mask1, start2, end2, mask2);}
	})(start_pool1, end_pool1, mask_pool1, start_pool2, end_pool2, mask_pool2);
}

function poolSizeChanged(startIp1, endIp1, netmask1, startIp2, endIp2, netmask2){
	//console.log(startIp1.value + ":" + endIp1.value + ":"+ netmask1.value + startIp2.value + ":" + endIp2.value + ":"+ netmask2.value);
	var pool1success = false;
	var pool2success = false;
	if(Get(startIp1).value.length>0 && Get(endIp1).value.length>0 && Get(netmask1).value.length>0){
		if(validatePoolStart1() && validatePoolEnd1() && validatePoolNetmask1()){
			pool1success = true;
		}
	}
	if(Get(startIp2).value.length>0 && Get(endIp2).value.length>0 && Get(netmask2).value.length>0){
		if(validatePoolStart2() && validatePoolEnd2() && validatePoolNetmask2()){
			pool2success = true;
		}
	}
	if(pool1success){
		if(!validatePoolRange1()){
			pool1success = false;
		}
	}

	if(pool2success){
		if(!validatePoolRange2()){
			pool2success = false;
		}
	}

	var count1 = 0;
	var count2 = 0;
	if(pool1success){
		count1 = getActualIpAddressCount(startIp1.value, endIp1.value, netmask1.value);
	}
	if(pool2success){
		count2 = getActualIpAddressCount(startIp2.value, endIp2.value, netmask2.value);
	}
	var finalCount = pool2success?Math.min(count1, count2):count1;
	requestCredentialInfo(finalCount>MAX_IP_POOL_SIZE_VPN2?MAX_IP_POOL_SIZE_VPN2:finalCount);
	//Get(formName + "_dataSource_profileName").value = finalCount;
}
var locked;//fix FF send multiple blur events
function requestCredentialInfo(finalCount){
	if(locked){return;}
	locked = true;
	var table = Get("credential");
	var delta = finalCount - table.rows.length;
	if(delta > 0){//add
		var url = "<s:url action="vpnServices" includeParams="none" />?operation=generateCredential&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:addCredential, failure:addCredential }, "credentialCount="+delta);
		showWaitingPanel();
	}else if(delta < 0){//remove
		delta = -delta;
		var rowCount = Get("credential").rows.length;
		var removeIndexs = new Array();
		for(var i=0; i< delta; i++){
			removeIndexs.push(rowCount-1-i);
		}
		var url = "<s:url action="vpnServices" includeParams="none" />?operation=removeCredential&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:removeCredential, failure:removeCredential }, "credentialIds="+removeIndexs);
		showWaitingPanel();
	}else{
		locked = false;
	}
}
function operateGenerateCredential(){
	var selIndexes = getSelectIndexes();
	if(selIndexes.length==0){
		var feChild = document.getElementById("checkAll");
        hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.vpn.service.credential.name" /></s:param></s:text>');
		return;
	}
	var url = "<s:url action="vpnServices" includeParams="none" />?operation=updateCredential&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:updateCredential, failure:updateCredential }, "credentialIds="+selIndexes);
	showWaitingPanel();
}
function operateRemoveCredential(){
	var selIndexes = getSelectIndexes();
	if(selIndexes.length==0){
		var feChild = document.getElementById("checkAll");
        hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param>Credential</s:param></s:text>');
		return;
	}
	var url = "<s:url action="vpnServices" includeParams="none" />?operation=removeCredential&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:removeCredential, failure:removeCredential }, "credentialIds="+selIndexes);

	showWaitingPanel();
}

function showWaitingPanel(){
	if(null != waitingPanel){
		waitingPanel.show();
	}
}

function hideWaitingPanel(){
	if(null != waitingPanel){
		waitingPanel.hide();
	}
}

function updateCredential(o){
	hideWaitingPanel();
	if(!o){
		return;
	}
	eval("var result = " + o.responseText);
	if(result){
		var table = Get("credential");
		var rowCount = table.rows.length;
		for(var i=0; i< result.length; i++){
			var entry = result[i];
			if(rowCount > entry.index){
				var row = table.rows[entry.index];
				row.cells[1].innerHTML = entry.pwd;
				row.cells[2].innerHTML = "false";
				//row.cells[3].innerHTML = "&nbsp;"; do not update!
			}
		}
		updateCredentialTag();
		locked = false;
	}
}

function removeCredential(o){
	hideWaitingPanel();
	if(!o){
		return;
	}
	try{
		eval("var result = " + o.responseText);
		if(result){
			var table = Get("credential");
			var rowCount = table.rows.length;
			for(var i=0; i< result.length; i++){
				var entry = result[i];
				if(rowCount > entry.index){
					table.deleteRow(entry.index);
				}
			}
			updateCredentialTag();
			locked = false;
		}
	}catch(e){
		console.log(e);
	}
	
}
function addCredential(o){
	hideWaitingPanel();
	if(!o){
		return;
	}
	eval("var result = " + o.responseText);
	if(result){
		var table = Get("credential");
		var rowCount = table.rows.length;
		for(var i=0; i< result.length; i++){
			var entry = result[i];
			var tableRow = table.insertRow(rowCount);
			var cell0 = tableRow.insertCell(0);
			var cell1 = tableRow.insertCell(1);
			var cell2 = tableRow.insertCell(2);
			var cell3 = tableRow.insertCell(3);
			var cell4 = tableRow.insertCell(4);
			var cell5 = tableRow.insertCell(5);
			cell0.className = "listCheck";
			cell1.className = cell2.className = cell3.className= cell4.className = cell5.className = "list";
			cell0.width= "25px";
			cell1.width= "240px";
			cell2.width= "70px";
			cell3.width = "150px";
			cell4.width = "100px";
			cell0.innerHTML = "<input type='checkbox' name='cbx' />";
			cell1.innerHTML = entry.pwd;
			cell2.innerHTML = "false";
			cell3.innerHTML = "&nbsp;";
			cell4.innerHTML = "&nbsp;";
			cell5.innerHTML = "&nbsp;";
		}
		updateCredentialTag();
		locked = false;
	}
}

function getSelectIndexes(){
	var table = Get("credential");
	var rowCount = table.rows.length;
	var cbxes = document.getElementsByName("cbx");
	var selectedArray = new Array();
	for(var i=0; i<cbxes.length; i++){
		if(cbxes[i].checked){
			selectedArray.push(i);
		}
	}
	return selectedArray.reverse();
}
function toggleCheckAll(cb){
	var cbs = document.getElementsByName('cbx');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}
function updateCredentialTag(){
	showCredentialsContent();//expand this content
	Get("credentialTag").innerHTML = "(" + Get("credential").rows.length + " items)";
}

function vpnSettingChanged(radiobox){
	var start_pool1 = Get(formName + "_dataSource_clientIpPoolStart1");
	var end_pool1 = Get(formName + "_dataSource_clientIpPoolEnd1");
	var mask_pool1 = Get(formName + "_dataSource_clientIpPoolNetmask1");

	var start_pool2 = Get(formName + "_dataSource_clientIpPoolStart2");
	var end_pool2 = Get(formName + "_dataSource_clientIpPoolEnd2");
	var mask_pool2 = Get(formName + "_dataSource_clientIpPoolNetmask2");

	if(radiobox.value == VPN_SERVER_TYPE_SINGLE){
		Get("loadBalanceCbxTd").style.display = "none";
		Get("loadBalanceLabelTd").style.display = "none";
		Get("server2Settings").style.display = "none";
		if(start_pool2.value.length>0){start_pool2.DEFAULT_VALUE=start_pool2.value;start_pool2.value='';}
		if(end_pool2.value.length>0){end_pool2.DEFAULT_VALUE=end_pool2.value;end_pool2.value='';}
		if(mask_pool2.value.length>0){mask_pool2.DEFAULT_VALUE=mask_pool2.value;mask_pool2.value='';}
	}else if(radiobox.value == VPN_SERVER_TYPE_REDUNDANT){
		Get("loadBalanceCbxTd").style.display = "";
		Get("loadBalanceLabelTd").style.display = "";
		Get("server2Settings").style.display = "";
		if(start_pool2.DEFAULT_VALUE){start_pool2.value=start_pool2.DEFAULT_VALUE;}
		if(end_pool2.DEFAULT_VALUE){end_pool2.value=end_pool2.DEFAULT_VALUE;}
		if(mask_pool2.DEFAULT_VALUE){mask_pool2.value=mask_pool2.DEFAULT_VALUE;}
	}
	poolSizeChanged(start_pool1,end_pool1,mask_pool1,start_pool2,end_pool2,mask_pool2);
}

function changehiveApVpnServer1(){
	var hiveApVpnServer1 = document.getElementById(formName + "_dataSource_hiveApVpnServer1");
	var url = '<s:url value="vpnServices.action"  includeParams="none" />?operation=viewHiveApInfo&id='+hiveApVpnServer1.value;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success: updateHiveApInfo1}, null);
}

function updateHiveApInfo1(o){
	eval("var details = " + o.responseText);
	var serverDeaultGateway1 = document.getElementById(formName + "_dataSource_serverDeaultGateway1");
	var serverPrivateIp1 = document.getElementById(formName + "_dataSource_serverPrivateIp1");
	var serverPublicIp1 = document.getElementById(formName + "_dataSource_serverPublicIp1");
	serverDeaultGateway1.value = "";
	serverPrivateIp1.value = "";

	if(details){
		if(details.dhcp == "true"){
			serverDeaultGateway1.readOnly = false;
			serverPrivateIp1.readOnly = false;
			hm.util.reportFieldError(serverPublicIp1, '<s:text name="info.config.vpn.service.select.dynamic.prompt"></s:text>');
			return;
		}else{
			if(details.cfgGateway){
				serverDeaultGateway1.value = details.cfgGateway;
				serverDeaultGateway1.readOnly = true;
			}
			if(details.serverPrivateIp){
				serverPrivateIp1.value = details.serverPrivateIp;
				serverPrivateIp1.readOnly = true;
			}
		}
	}
	if(details.id == -1){
		serverPrivateIp1.readOnly = false;
		serverDeaultGateway1.readOnly = false;
	}
}

function changehiveApVpnServer2(){
	var hiveApVpnServer2 = document.getElementById(formName + "_dataSource_hiveApVpnServer2");
	var url = '<s:url value="vpnServices.action"  includeParams="none" />?operation=viewHiveApInfo&id='+hiveApVpnServer2.value;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success: updateHiveApInfo2}, null);
}

function updateHiveApInfo2(o){
	eval("var details = " + o.responseText);
	var serverPublicIp2 = document.getElementById(formName + "_dataSource_serverPublicIp2");
	var serverDeaultGateway2 = document.getElementById(formName + "_dataSource_serverDeaultGateway2");
	var serverPrivateIp2 = document.getElementById(formName + "_dataSource_serverPrivateIp2");
	serverDeaultGateway2.value = "";
	serverPrivateIp2.value = "";
	if(details){
		if(details.dhcp == "true"){
			serverDeaultGateway2.readOnly = false;
			serverPrivateIp2.readOnly = false;
			hm.util.reportFieldError(serverPublicIp2, '<s:text name="info.config.vpn.service.select.dynamic.prompt"></s:text>');
			return;
		}else{
			if(details.cfgGateway){
				serverDeaultGateway2.value = details.cfgGateway;
				serverDeaultGateway2.readOnly = true;
			}
			if(details.serverPrivateIp){
				serverPrivateIp2.value = details.serverPrivateIp;
				serverPrivateIp2.readOnly = true;
			}
		}
	}
	if(details.id == -1){
		serverPrivateIp2.readOnly = false;
		serverDeaultGateway2.readOnly = false;
	}
}

function checkAllUserProfiles(cb, toggleName){
	var cbs = document.getElementsByName(toggleName);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function iPsecVpnChanged(radiobox){
	if(radiobox.value == IPSEC_VPN_LAYER_3){
		Get("ipsecVpnLayer2Row").style.display = "none";
		Get("userProfilesSettingsLayer2Row").style.display = "none";
		//Get("optioalSettingsLayer2Row").style.display = "none";

		Get("vpnClientDnsServer").style.display = "none";
		Get("trafficTunnelOperation").style.display = "none";
		Get("heartBeatSetting").style.display = "none";

		Get("ipsecVpnLayer3Row").style.display = "";
		//Get("userProfilesSettingsLayer3Row").style.display = "";
		//Get("routeTunnelTrafficLayer3Row").style.display = "";
		//Get("routeTunnelTrafficExpendIcon").style.display = "";
	}else if(radiobox.value == IPSEC_VPN_LAYER_2){
		Get("ipsecVpnLayer2Row").style.display = "";
		Get("userProfilesSettingsLayer2Row").style.display = "";
		//Get("optioalSettingsLayer2Row").style.display = "";
		Get("vpnClientDnsServer").style.display = "";
		Get("trafficTunnelOperation").style.display = "";
		Get("heartBeatSetting").style.display = "";

		Get("ipsecVpnLayer3Row").style.display = "none";
		//Get("userProfilesSettingsLayer3Row").style.display = "none";
		//Get("routeTunnelTrafficLayer3Row").style.display = "none";
		//Get("routeTunnelTrafficExpendIcon").style.display = "none";
	}
}

function checkCheckbox(elements){
	if(elements){
		for(var i=0;i<elements.length;i++){
			if(elements[i].type.toUpperCase() == "CHECKBOX"){
				return true;
			}
		}
	}
	return false;
}

function changeTunnelModeL2(option,index){
	if(option == 2){
		Get(formName + "_tunnelSelected_" + index + "tunnelAll").parentNode.style.display="";
		Get(formName + "_tunnelSelected_" + index + "splitTunnel").parentNode.style.display="";
		Get(formName + "_tunnelSelected_" + index + "tunnelAll").checked = true;
	}else{
		Get(formName + "_tunnelSelected_" + index + "tunnelAll").parentNode.style.display="none";
		Get(formName + "_tunnelSelected_" + index + "splitTunnel").parentNode.style.display="none";
	}
}

function showCreateSection() {
	hm.util.hide('newButton');
	var externalIpAddressEl = Get(formName + "_externalIpAddress");
	externalIpAddressEl.value = "";
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

 function showCreateSectionTunnelException() {
	hm.util.hide('newTunnelException');
	hm.util.show('createTunnelException');
	hm.util.show('createSectionTunnelException');
	var trh = document.getElementById('headerSectionTunnelException');
	var trc = document.getElementById('createSectionTunnelException');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function toggleCheckAllGateway(cb) {
	var cbs = document.getElementsByName('vpnGatewayIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function toggleCheckAllTunnelTrafficRules(cb) {
	var cbs = document.getElementsByName('tunnelExceptionIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function getUserProfilesSettingsForL2() {
	var result = new Array();
	var count = <s:property value="%{dataSource.userProfileTrafficL2.size()}"/>;
	if(count > 0){
		for (var index = 0; index < count; index++ ) {
			var init = "";
			var selected = document.getElementsByName("tunnelSelected_"+index);
			for(var j = 0; j < 2; j ++) {
				if (selected[j].checked) {
					init = selected[j].value;
					break;
				}
			}
			result [index]= Get("vpnTunnelModeL2_" + index ).value + "_" +init;
		}
	}
	document.getElementById(formName+"_userProfilesTrafficForL2Str").value = result;
}

<s:if test="%{jsonMode}">
	window.setTimeout("onLoadPage()", 100);
	<s:if test="%{writeDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
</s:if>
</script>

<div id="content"><s:form action="vpnServices" name="vpnServices" id="vpnServices">
	<s:hidden name="dataSource.credentialDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.serverAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.clientAdvDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.certificateDisplayStyle"></s:hidden>
	<s:hidden name="dnsIp" />
	<s:hidden name="vpnGateWayId" />
	<s:hidden name="userProfilesTrafficForL2Str" />
	<s:hidden name="wirelessRoutingEnabled" />
	
	<s:if test="%{jsonMode}">
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
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
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
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
				<table border="0" cellspacing="0" cellpadding="0" width="770px">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="770px">
			</s:else>
				<tr>
					<td><!-- definition -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="145px"><label><s:text
									name="config.vpn.service.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.profileName"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="32" maxlength="%{profileNameLength}" disabled="%{disabledName}"/>
									<s:text name="config.vpn.service.name.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.vpn.service.description" /></td>
								<td><s:textfield name="dataSource.description" size="64"
									maxlength="%{descriptionLength}" /> <s:text
									name="config.vpn.service.description.note" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="10px"></td></tr>
				<!-- begin add Layer 2 IPsec VPN and Layer 3 IPsec VPN-->
				<tr>
					<td style="padding-left: 2px;">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td width="400px" style="display: <s:property value="%{wirelessRoutingEnabledStyle}"/>"><s:radio onclick="this.blur();" onchange="iPsecVpnChanged(this);" label="Gender"	name="dataSource.ipsecVpnType" list="%{iPsecVpnLayer3}" listKey="key" listValue="value"/></td>
								<td><s:radio onclick="this.blur();" disabled="%{wirelessRoutingEnabled&&jsonMode}" onchange="iPsecVpnChanged(this);" label="Gender"	name="dataSource.ipsecVpnType" list="%{iPsecVpnLayer2}" listKey="key" listValue="value"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<!-- end add Layer 2 IPsec VPN and Layer 3 IPsec VPN -->
			    <tr><td height="5px"></td></tr>
				<tr><td height="5px"></td></tr>
				<tr id = "ipsecVpnLayer2Row" style="display: <s:property value="%{ipsecVpnLayer2Type}"/>">
					<td>
						<fieldset><legend><s:text name="config.vpn.service.settings" /></legend>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
							       <td  width="200px"><s:radio onclick="this.blur();" onchange="vpnSettingChanged(this);" label="Gender"	name="dataSource.vpnServerType" list="%{vpnServerTypeSingle}" listKey="key" listValue="value"/></td>
								   <td colspan="2"><s:radio onclick="this.blur();" onchange="vpnSettingChanged(this);" label="Gender"	name="dataSource.vpnServerType" list="%{vpnServerTypeRedundant}" listKey="key" listValue="value"/></td>
							</tr>
							<tr>
								  <td ></td>
								  <td colspan="2" >
							      <span id="loadBalanceCbxTd" style="margin-left:20px;display: <s:property value="%{vpnServer2SettingStyle}"/>"><s:checkbox name="dataSource.loadBalance"></s:checkbox>
								  <span id="loadBalanceLabelTd" style="display: <s:property value="%{vpnServer2SettingStyle}"/>"><label><s:text name="config.vpn.service.load.balance" /></label></span></td>
							 </tr>
							<tr>
								<td width="240px">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td height="52px">&nbsp;</td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.publicIp" /><font color="red"><s:text name="*"/></font></label></td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.privateIp" /><font color="red"><s:text name="*"/></font></label></td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.defaultGateway" /><font color="red"><s:text name="*"/></font></label></td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.ippool.start" /><font color="red"><s:text name="*"/></font></label></td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.ippool.end" /><font color="red"><s:text name="*"/></font></label></td></tr>
										<tr><td height="26px"><label><s:text name="config.vpn.service.server.ippool.netmask" /><font color="red"><s:text name="*"/></font></label></td></tr>
									</table>
								</td>
								<td width="200px">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td></td><td height="26px"><label><s:text name="config.vpn.service.server1.tag" /></label></td></tr>
										<tr><td></td><td height="26px"><s:select name="dataSource.hiveApVpnServer1" list="availableHiveApsList" listKey="id" listValue="value" cssStyle="width: 155px;"  onchange="changehiveApVpnServer1(this.options[this.selectedIndex].value);"></s:select></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverPublicIp1" size="24" maxlength="%{serverPublicIp1Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverPrivateIp1" size="24" maxlength="%{serverPrivateIp1Length}" readonly="%{disablePrivateServer1}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverDeaultGateway1" size="24" maxlength="%{serverDefaultGateway1Length}" readonly="%{disablePrivateServer1}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolStart1" size="24" maxlength="%{clientIpPoolStart1Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolEnd1" size="24" maxlength="%{clientIpPoolEnd1Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolNetmask1" size="24" maxlength="%{clientIpPoolNetmask1Length}" /></td></tr>
									</table>
								</td>
								<td id="server2Settings" style="display: <s:property value="%{vpnServer2SettingStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td></td><td height="26px"><label><s:text name="config.vpn.service.server2.tag" /></label></td></tr>
										<tr><td></td><td height="26px"><s:select name="dataSource.hiveApVpnServer2" list="availableHiveApsList" listKey="id" listValue="value" cssStyle="width: 155px;" onchange="changehiveApVpnServer2(this.options[this.selectedIndex].value);"></s:select></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverPublicIp2" size="24" maxlength="%{serverPublicIp2Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverPrivateIp2" size="24" maxlength="%{serverPrivateIp2Length}" readonly="%{disablePrivateServer2}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.serverDeaultGateway2" size="24" maxlength="%{serverDefaultGateway2Length}" readonly="%{disablePrivateServer2}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolStart2" size="24" maxlength="%{clientIpPoolStart2Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolEnd2" size="24" maxlength="%{clientIpPoolEnd2Length}" /></td></tr>
										<tr><td></td><td height="26px"><s:textfield name="dataSource.clientIpPoolNetmask2" size="24" maxlength="%{clientIpPoolNetmask2Length}" /></td></tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" class="noteInfo"><s:text name="config.vpn.service.layer2.cvg.client.count.note" /></td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>
				<!--used for layer3 vpn gateway setting  -->
				<tr id="ipsecVpnLayer3Row" style="display: <s:property value="%{ipsecVpnLayer3Type}"/>">
					<td>
						<fieldset><legend><s:text name="config.vpn.gateway.settings" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" class="embedded">
									<tr><td height="5px"></td></tr>
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" onClick="showCreateSection();"
													<s:property value="updateDisabled" />></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeVpnGateWay');"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" onClick="submitAction('addVpnGateWay');"></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button"
													onClick="submitAction('removeVpnGateWayNone');"></td>
												<td><input type="button" name="ignore" value="Cancel"
													class="button" onClick="hideCreateSection();"></td>
											</tr>
										</table>
										</td>
									</tr>
								 	<tr id="headerSection">
								 		<th></th>
										<th align="left" style="padding-left:0px;">
											<input type="checkbox" id="checkAllGateway"
												onClick="toggleCheckAllGateway(this);">
											<s:text name="config.vpn.gateway.settings.gateways" /></th>
										<th align="left" ><s:text name="config.vpn.gateway.settings.wan.ipaddress" /></th>
										<th align="left" ><s:text name="config.vpn.gateway.settings.lan.ipaddress" /></th>
										<th align="left" ><s:text name="config.vpn.gateway.settings.gateways.dynamicRouting" /></th>
										<th align="left" ><s:text name="config.vpn.gateway.settings.gateways.external.ipaddress" /></th>
										<s:if test="%{dataSource.vpnGateWaysSetting.size() > 1}">
											<td>&nbsp;</td>
										</s:if>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
										<td class="listHead">&nbsp;</td>
										<td class="listHead" valign="top" nowrap><s:select name="vpnGateWay" list="%{vpnGateWaysList}" listKey="id" listValue="value" cssStyle="width: 155px;"></s:select>
											<a class="marginBtn" href="javascript:submitAction('vpnGateWayEdit')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a></td>
										<td class="listHead" valign="top"></td>
										<td class="listHead" valign="top"></td>
										<!-- <td class="listHead" valign="top"></td> -->
										<td class="listHead" valign="top"></td>
										<%-- <td class="listHead" valign="top"><s:textfield name="externalIpAddress" size="24" /></td> --%>
										<td class="listHead" valign="top"><s:textfield name="externalIpAddress" size="24"  maxlength="15" onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
										<s:if test="%{dataSource.vpnGateWaysSetting.size() > 1}">
											<td>&nbsp;</td>
										</s:if>
									</tr>
									<tr>
										<td valign="top" style="padding: 0px 10px 0 0;">
											<table cellspacing="0" cellpadding="0" border="0">
												<s:if test="%{dataSource.vpnGateWaysSetting.size() > 0}">
													<tr>
														<td style="text-align: center;" width="60px;" height="30px;" nowrap><label><s:text name="config.vpn.service.vpn3.primary.server"/></label></td>
													</tr>
												</s:if>
												<s:if test="%{dataSource.vpnGateWaysSetting.size() > 1}">
													<tr>
														<td style="text-align: center;" width="60px;"  height="25px;" nowrap><label><s:text name="config.vpn.service.vpn3.backup.server"/></label></td>
													</tr>
												</s:if>
											</table>
										</td>
										<td valign="top" colspan="6">
											<table cellspacing="0" cellpadding="0" border="0" id="vpnGateWayTable">
												<s:iterator value="%{dataSource.vpnGateWaysSetting}" status="status">
													<tr>
														 <td class="listCheck" width="10" style="text-align: center;"><s:checkbox name="vpnGatewayIndices"
																fieldValue="%{#status.index}" /></td>
												        <td class="list" style="text-align: center;" width="95" nowrap>
												        	&nbsp;<s:property value="hiveAP.hostName" />
												        	<s:hidden name="ordering" value="%{#status.index}" /></td>
														<td class="list" style="text-align: center;" width="110" nowrap>&nbsp;<s:property value="wanIpAddress" /></td>
														<td class="list" style="text-align: center;" width="100" nowrap>&nbsp;<s:property value="lanIpAddress" /></td>
														<td class="list" style="text-align: center;" width="110" nowrap>&nbsp;<s:property value="dynamicRoutStringStr" /></td>
														<%-- <td class="list" style="text-align: center;" width="150" nowrap>&nbsp;<s:property value="externalIpAddress" /></td>	 --%>
														<td class="list" style="text-align: center;" width="150" nowrap>&nbsp;<s:textfield size="15" name="externalIpAddresses"
															maxlength="15" value="%{externalIpAddress}"
															onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>

													</tr>
												</s:iterator>
											</table>
										</td>
										<s:if test="%{dataSource.vpnGateWaysSetting.size() > 1}">
											<td valign="top" style="padding: 0px 10px 0 0;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td><input type="button" class="moveRow" value="Up"
															onclick="hm.util.moveRowsUp('vpnGateWayTable');" /></td>
													</tr>
													<tr>
														<td><input type="button" class="moveRow" value="Down"
															onclick="hm.util.moveRowsDown('vpnGateWayTable');" /></td>
													</tr>
													<s:if test="%{dataSource.vpnGateWaysSetting.size() > 15}">
													<s:generator separator="," val="%{' '}" count="%{dataSource.vpnGateWaysSetting.size()-2}">
														<s:iterator>
															<tr>
																<td>&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
														<tr>
															<td><input type="button" class="moveRow" value="Up"
																onclick="hm.util.moveRowsUp('vpnGateWayTable');" /></td>
														</tr>
														<tr>
															<td><input type="button" class="moveRow" value="Down"
																onclick="hm.util.moveRowsDown('vpnGateWayTable');" /></td>
														</tr>
													</s:if>
												</table>
											</td>
										</s:if>
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
							</table>

						</fieldset>
					</td>
				</tr>

				<!-- begin user profile for traffic management -->
				<tr id = "userProfilesSettingsLayer2Row" style="display: <s:property value="%{ipsecVpnLayer2Type}"/>">
					<td>
						<fieldset><legend><s:text name="config.vpn.service.userProfiles.settings"/></legend>
							<table id="userProfilesSettingsLayer2RowTbl" cellspacing="0" cellpadding="0" border="0" style="margin-bottom:10px;">
								<tr>
									<th nowrap="nowrap"><s:text name="config.vpn.service.userProfiles.available"/></th>
									<th nowrap="nowrap"><s:text name="config.vpn.service.userProfiles.tunnelenabled"/></th>
									<th nowrap="nowrap"><s:text name="config.vpn.service.userProfiles.tunnelAll"/></th>
									<th nowrap="nowrap"><s:text name="config.vpn.service.userProfiles.splitTunnel"/></th>
								</tr>
								<s:iterator value="%{dataSource.userProfileTrafficL2}"  status="status">
									<tr class="odd">
										<td class="list" ><s:label value="%{userProfile.userProfileName}" title="%{userProfile.userProfileName}"/>
										</td>
										<td class="list" style="padding: 5px 3px;">
											<%-- <s:checkbox  name="tunnelEnabled_%{#status.index}" value="%{tunnelEnabled}"
											onclick="setTunnelSelectedL2(this.checked,%{#status.index});" /> --%>
											<s:select  name="vpnTunnelModesL2" id="vpnTunnelModeL2_%{#status.index}" value="%{vpnTunnelModeL2}"
												list="%{enumVpnTunnelModeL2}" listKey="key" listValue="value"
												onchange="changeTunnelModeL2(this.options[this.selectedIndex].value,%{#status.index});"
												cssStyle="width: 105px;"></s:select>
										</td>
										<td class="list">
											&nbsp;
										    <span  style="display: <s:property value="%{tunnelTrafficStyle}"/>">
												<s:radio onclick="this.blur();"  label="Gender"	name="tunnelSelected_%{#status.index}"
												  list="#{'tunnelAll':''}"  value="%{tunnelSelected}"/>
									   		</span>
										</td>
										<td class="list">
											&nbsp;
											<span style="display: <s:property value="%{tunnelTrafficStyle}"/>">
												<s:radio onclick="this.blur();"  label="Gender"	name="tunnelSelected_%{#status.index}"
												  list="#{'splitTunnel':''}" value="%{tunnelSelected}"/>
									   		</span>
										</td>
									</tr>
								</s:iterator>
							</table>
						</fieldset>
					</td>
				</tr>
				<!-- end user profile for traffic management -->
				<tr>
					<td><!-- optional settings -->
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td>
									<fieldset><legend><s:text name="config.vpn.service.optional.settings" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td height="10px"></td></tr>
										<!-- server settings -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.vpn.service.ipsecSetting" />','ipsecSetting');</script></td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<div id="ipsecSetting" style="display: <s:property value="%{dataSource.certificateDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="185px"><label><s:text
															name="config.vpn.service.rootCa" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:select name="dataSource.rootCa" list="%{certificates}" cssStyle="width: 200px;"></s:select></td>
														<td>&nbsp;<input type="button" value="Import" class="button short" onClick="submitAction('importCa');"></td>
													</tr>
													<tr>
														<td class="labelT1" width="185px"><label><s:text
															name="config.vpn.service.certificate" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:select name="dataSource.certificate" list="%{certificates}" cssStyle="width: 200px;"></s:select></td>
														<td>&nbsp;<input type="button" value="Import" class="button short" onClick="submitAction('importCert');"></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.vpn.service.privateKey" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:select name="dataSource.privateKey" list="%{certificates}" cssStyle="width: 200px;"></s:select></td>
														<td>&nbsp;<input type="button" value="Import" class="button short" onClick="submitAction('importKey');"></td>
													</tr>
												</table>
												</div>
											</td>
										</tr>
										<tr><td height="10px"></td></tr>
										<!-- server client Credentials -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.vpn.service.credentials" />','credentials');</script></td>
										</tr>
										<tr><td height="10px"></td></tr>
										<tr>
											<td style="padding-left: 15px;">
												<div id="credentials" style="display: <s:property value="%{dataSource.credentialDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td width="4px"></td>
																<td><input type="button" name="ignore" value="Generate"
																	class="button" onClick="operateGenerateCredential();"></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" onClick="operateRemoveCredential();"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td>
														<table cellspacing="0" cellpadding="0" border="0" width="710px">
															<tr>
																<th align="left" width="25px" style="padding-left: 0; padding-right: 0;"><input type="checkbox" id="checkAll" onclick="toggleCheckAll(this);"></th>
																<th align="left" width="240px"><s:text name="config.vpn.service.credential.name" />
																	<span style="padding-left: 15px;" id="credentialTag">(<s:property value="%{credentialCount}" /> items)</span></th>
																<th align="left" width="70px"><s:text name="config.vpn.service.credential.allocated" /></th>
																<th align="left" width="150px"><s:text name="config.vpn.service.credential.clientHostname" /></th>
																<th align="left" width="100px"><s:text name="config.vpn.service.credential.primary" /></th>
																<th align="left"><s:text name="config.vpn.service.credential.backup" /></th>
															</tr>
															<tr>
																<td colspan="6">
																	<div style="overflow:auto; width:100%;height:150px">
																	<table id="credential" cellspacing="0" cellpadding="0" border="0" width="95%">
																	<s:iterator value="%{dataSource.vpnCredentials}" status="status">
																		<tr>
																			<td width="25px" class="listCheck"><s:checkbox name="cbx" fieldValue="%{#status.index}" /></td>
																			<td width="240px" class="list"><s:property value="credential" /></td>
																			<td width="70px" class="list"><s:property value="allocated" /></td>
																			<td width="150px" class="list"><s:property value="hostname" escape="false" /></td>
																			<td width="100px" class="list"><s:property value="primaryRoleString" escape="false" /></td>
																			<td class="list"><s:property value="backupRoleString" escape="false" /></td>
																		</tr>
																	</s:iterator>
																	</table>
																	</div>
																</td>
															</tr>
														</table>
														</td>
													</tr>
													<tr><td height="10px"></td></tr>
												</table>
												</div>
											</td>
										</tr>
										<!-- server advance options -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.vpn.service.server.advanced.options" />','serverAdv');</script></td>
										</tr>
										<tr><td height="10px"></td></tr>
										<tr>
											<td style="padding-left: 15px;">
												<div id="serverAdv" style="display: <s:property value="%{dataSource.serverAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
															<fieldset><legend><s:text name="config.vpn.service.phase1.options" /></legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="160px"><label><s:text
																			name="config.vpn.service.phase1.auth" /></label></td>
																		<td><s:select name="dataSource.phase1AuthMethod" list="%{phase1AuthMethods}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase1.encryption" /></label></td>
																		<td><s:select name="dataSource.phase1EncrypAlg" list="%{phase1EncrypAlgs}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase1.hash" /></label></td>
																		<td><s:select name="dataSource.phase1Hash" list="%{phase1Hashs}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase1.dhgroup" /></label></td>
																		<td><s:select name="dataSource.phase1DhGroup" list="%{phase1DhGroups}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase1.lifetime" /></label></td>
																		<td><s:textfield name="dataSource.phase1LifeTime" maxlength="8"
																			size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.phase1.lifetime.note"></s:text></td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr>
														<td>
															<fieldset><legend><s:text name="config.vpn.service.phase2.options" /></legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="160px"><label><s:text
																			name="config.vpn.service.phase2.encryption" /></label></td>
																		<td><s:select name="dataSource.phase2EncrypAlg" list="%{phase2EncrypAlgs}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase2.hash" /></label></td>
																		<td><s:select name="dataSource.phase2Hash" list="%{phase2Hashs}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase2.dhgroup" /></label></td>
																		<td><s:select name="dataSource.phase2PfsGroup" list="%{phase2PfsGroups}" listKey="key" listValue="value" cssStyle="width: 200px;"></s:select></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text
																			name="config.vpn.service.phase2.lifetime" /></label></td>
																		<td><s:textfield name="dataSource.phase2LifeTime" maxlength="8"
																			size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.phase2.lifetime.note"></s:text></td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td><s:checkbox name="dataSource.ikeValidation" onclick="ikeValidateEnable(this.checked)"></s:checkbox></td>
																	<td width="165px"><label><s:text name="config.vpn.service.peer.ike.validation" /></label></td>
																	<td><s:select name="dataSource.serverIkeId" list="%{ikeIds}" listKey="key" listValue="value" cssStyle="width: 200px;" disabled="%{!dataSource.ikeValidation}"></s:select></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr><td height="5px"></td></tr>
												</table>
												</div>
											</td>
										</tr>
										<!-- client advance options -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.vpn.service.client.advanced.options" />','clientAdv');</script></td>
										</tr>
										<tr>
											<td style="padding-left: 15px;">
												<div id="clientAdv" style="display: <s:property value="%{dataSource.clientAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr id ="vpnClientDnsServer" style="display: <s:property value="%{ipsecVpnLayer2Type}"/>">
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" style="padding-left: 3px" width="182px"><label><s:text
																		name="config.vpn.service.client.dns.server" /></label></td>
																	<td>
																		<ah:createOrSelect divId="errorDisplay" list="ipAddresses" typeString="DnsIp"
																			selectIdName="dnsSelect" inputValueName="dataSource.inputText" swidth="200px" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr ><td height="5px"></td></tr>
													<tr id = "trafficTunnelOperation" style="display: <s:property value="%{ipsecVpnLayer2Type}"/>">
														<td>
															<fieldset style="width:670px;"><legend><s:text name="config.vpn.service.trafficTunnel.option" /></legend>
															<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr><td height="10px"></td></tr>
																<tr>
																	<td width="25px"><s:checkbox name="dataSource.capwapThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.capwap" /></label></td>
																	<td rowspan="20" width="420px" valign="top" class="noteInfo"><s:text name="config.vpn.service.tunnel.option.note" /></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.logThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.logging" /></label></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.snmpThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.snmp" /></label></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.ntpThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.ntp" /></label></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.radiusThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.radius" /></label></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.dbTypeAdThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.ad" /></label></td>
																</tr>
																<tr><td height="5px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.dbTypeLdapThroughTunnel"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.traffic.ldap" /></label></td>
																</tr>
															</table>
															</fieldset>
														</td>
													</tr>
													<tr>
														<td>
															<fieldset><legend><s:text name="config.vpn.service.client.ike.setting" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr><td height="10px"></td></tr>
																<tr>
																	<td><s:checkbox name="dataSource.natTraversal"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.nat.traversal" /></label></td>
																</tr>
																<%-- hidden it since it hasn't been configured on device
																<tr>
																	<td><s:checkbox name="dataSource.keepAlive"></s:checkbox></td>
																	<td><label><s:text name="config.vpn.service.keepAlives" /></label></td>
																</tr>
																--%>
															</table>
															</fieldset>
														</td>
													</tr>
												<%-- <tr>
														<td>
															<fieldset><legend><s:text name="config.vpn.service.dpd.setting" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr><td height="5px"></td></tr>
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.vpn.service.dpd.interval"/></td>
																	<td><s:textfield name="dataSource.dpdIdelInterval" maxlength="8"
																			size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.dpd.interval.note"></s:text></td>
																</tr>
																<tr>
																	<td class="labelT1"><s:text name="config.vpn.service.dpd.retry"/></td>
																	<td><s:textfield name="dataSource.dpdRetry" maxlength="8"
																			size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.dpd.retry.note"></s:text></td>
																</tr>
																<tr>
																	<td class="labelT1"><s:text name="config.vpn.service.dpd.retryInterval"/></td>
																	<td><s:textfield name="dataSource.dpdRetryInterval" maxlength="8"
																			size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.dpd.retryInterval.note"></s:text></td>
																</tr>
															</table>
															</fieldset>
														</td>
													</tr> --%>
													<tr>
														<td>
															<fieldset><legend><s:text name="config.vpn.service.dpd.setting" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr><td height="5px"></td></tr>
																<tr>
																	<td>
																		<table align = "center">
																			<tr>
																				<td colspan="2" align = "center">
																				  <s:text name="config.vpn.service.dpd.detection"/>
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" ><s:text name="config.vpn.service.dpd.heartbeat.interval"/></td>
																		     	<td><s:textfield name="dataSource.dpdIdelInterval" maxlength="8"
																					size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																				<s:text name="config.vpn.service.dpd.interval.note"></s:text></td>
																			</tr>
																			<tr>
																				<td colspan="2" align="right">
																					<s:text name="config.vpn.service.dpd.interval.note.prompt"></s:text>
																				</td>
																			</tr>
																		</table>
																	</td>
																		<td>
																		<table align = "center">
																			<tr>
																				<td colspan="2" align = "center">
																				  <s:text name="config.vpn.service.dpd.retry.title"/>
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1"><s:text name="config.vpn.service.dpd.retry"/></td>
																				<td><s:textfield name="dataSource.dpdRetry" maxlength="8"
																						size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																					<s:text name="config.vpn.service.dpd.retry.note"></s:text></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><s:text name="config.vpn.service.dpd.retryInterval"/></td>
																				<td><s:textfield name="dataSource.dpdRetryInterval" maxlength="8"
																						size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																					<s:text name="config.vpn.service.dpd.retryInterval.note"></s:text></td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
															</fieldset>
														</td>
													</tr>
													<tr id="heartBeatSetting" style="display: <s:property value="%{ipsecVpnLayer2Type}"/>">
														<td>
															<fieldset><legend><s:text name="config.vpn.service.amrp.setting" /></legend>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr><td height="5px"></td></tr>
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.vpn.service.amrp.interval"/></td>
																	<td><s:textfield name="dataSource.amrpInterval" maxlength="8"
																			size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.amrp.interval.note"></s:text></td>
																</tr>
																<tr>
																	<td class="labelT1"><s:text name="config.vpn.service.amrp.retry"/></td>
																	<td><s:textfield name="dataSource.amrpRetry" maxlength="8"
																			size="8" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.vpn.service.amrp.retry.note"></s:text></td>
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
</s:form></div>
<s:if test="%{jsonMode == true}">
<script type="text/javascript">
	function judgeFoldingIcon4VpnProfile() {
		adjustFoldingIcon('ipsecSetting');
		adjustFoldingIcon('credentials');
		adjustFoldingIcon('serverAdv');
		adjustFoldingIcon('clientAdv');
	}

	setTimeout("judgeFoldingIcon4VpnProfile()", 10);
</script>
</s:if>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
	<script>
		setCurrentHelpLinkUrl('<s:property value="helpLink" />');
	</script>
</s:if>