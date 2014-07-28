<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<tiles:insertDefinition name="tabView" />

<script src="<s:url value="/js/hm.options.js" includeParams="none" />?v=<s:property value='verParam' />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var hiveApTabs = null;
var formName = 'hiveAp';
var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;
var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;
var BIND_INTERFACE_NONE = <%=AhInterface.ETH_BIND_IF_NULL%>;
var BIND_INTERFACE_AGG0 = <%=AhInterface.ETH_BIND_IF_AGG0%>;
var BIND_INTERFACE_RED0 = <%=AhInterface.ETH_BIND_IF_RED0%>;
var IF_ADMIN_STATUS_UP = <%=AhInterface.ADMIN_STATE_UP%>;
var IF_ADMIN_STATUS_DOWN = <%=AhInterface.ADMIN_STATE_DOWM%>;
var OPERATION_MODE_BACKHAUL = <%=AhInterface.OPERATION_MODE_BACKHAUL%>;
var OPERATION_MODE_ACCESS = <%=AhInterface.OPERATION_MODE_ACCESS%>;
var ETH_SPEED_1000M = <%=AhInterface.ETH_SPEED_1000M%>;
var VPN_MARK_SERVER = <%=HiveAp.VPN_MARK_SERVER%>;
var VPN_MARK_CLIENT = <%=HiveAp.VPN_MARK_CLIENT%>;
var VPN_MARK_NONE = <%=HiveAp.VPN_MARK_NONE%>;

function is11nHiveAP(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	if(selectApModel == MODEL_340 || selectApModel == MODEL_320 
			|| selectApModel == MODEL_380 || selectApModel == MODEL_120
			|| selectApModel == MODEL_110 ){
		return true;
	}
	return false;
}

function isEth1Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	return (is11nHiveAP() && selectApModel != MODEL_120 && selectApModel != MODEL_110 );
}

function isWifi1Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	return selectApModel != MODEL_110;
}

function isVpnServerAvaliable(){
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	return (is11nHiveAP() && selectApModel != MODEL_120 && selectApModel != MODEL_110 );
}

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	hiveApTabs = new YAHOO.widget.TabView("hiveApTabs", {activeIndex:tabId});
	var expanding_dynamic = <s:property value="%{expanding_dynamic}"/>;
	var expanding_static = <s:property value="%{expanding_static}"/>;
	var operation = '<s:property value="%{operation}"/>';

	if(operation == 'new'){
		document.getElementById(formName + "_dataSource_hostName").focus();
	}
	if(operation == 'new' || expanding_dynamic){
		showCreateSection('dynamic');
	}
	if(operation == 'new' || expanding_static){
       	showCreateSection('static');
    }

    updateInterfaceLayout(); 
    // Update SSID allocation section
    updateSsidAllocation();
    // Update native vlan section
    updateNativeVlan();

    // updateCapwapIp();
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create' &&
			operation != 'addStaticRoute' &&
			operation != 'removeStaticRoutes' &&
			operation != 'addDynamicRoute' &&
			operation != 'removeDynamicRoutes') {
			showProcessing();
		}

		//close create section after apply ssid profiles
		if(operation == 'addStaticRoute'){
			document.getElementById("expanding_static").value="false";
		}
		if(operation == 'addDynamicRoute'){
			document.getElementById("expanding_dynamic").value="false";
		}
		document.forms[formName].operation.value = operation;
		document.forms[formName].tabId.value = hiveApTabs.get('activeIndex');
		hm.options.selectAllOptions('includedNeighbors');
		hm.options.selectAllOptions('excludedNeighbors');
		//hm.options.selectAllOptions('ipTracks');
		hm.options.selectAllOptions('dhcpServers');
		hm.options.selectAllOptions('preferredSsids');
		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
		enableBindInterfaceItems();
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if(operation == "<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>" || operation == 'cancel'){
		document.getElementById(formName + "_dataSource_metricInteval").value = 60;
		document.getElementById(formName + "_routeMinimun").value = 67;
		document.getElementById(formName + "_routeMaximun").value = 67;
		document.getElementById(formName + "_dataSource_nativeVlan").value = 1;

		return true;
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

	if(!validateInterfaceModes(operation)){
		return false;
	}

	if(!validateNativeVlan(operation)){
		return false;
	}

	if(!validateRadioProfile(operation)){
		return false;
	}

	if(!validateStaticRoute(operation)){
		return false;
	}

	if(!validateDynamicRoute(operation)){
		return false;
	}

	if(!validateNeighborMac(operation)){
		return false;
	}

	if(! validateDestinationMac(operation)){
		return false;
	}

	if(!validateCfgUsernameAndPassword(operation)){
		return false;
	}

	if(!validateReadOnlyCfgUsernameAndPassword(operation)){
		return false;
	}

	if(!checkUserDupleWithReadOnlyUser(operation)){
		return false;
	}

	if(!validateDtlsSettings(operation)){
		return false;
	}

	if(!validateL3Neighbor(operation)){
		return false;
	}

//	if(!validateIpTracking(operation)){
//		return false;
//	}

	if(!validateRADIUSServer(operation)){
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

	if(!validateCapwapIp(operation)){
		return false;
	}

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
			document.forms[formName].radiusServer.value = value;
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
	if(operation == "editLldpCdp"){
		var value = hm.util.validateListSelection(formName + "_lldpCdp");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].lldpCdp.value = value;
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
			document.forms[formName].scheduler.value = value;
		}
	}
	if(operation == "editIpTrack"){
		var value = hm.util.validateOptionTransferSelection("ipTracks");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ipTrack.value = value;
		}
	}

	return true;
}

function validateRadioProfile(operation){
	if(operation == 'create' || operation == 'update'){
		var inputElement1 = document.getElementById(formName + "_wifi0RadioProfile");
		var inputElement2 = document.getElementById(formName + "_wifi1RadioProfile");

		if(inputElement1.value <= 0){
			hiveApTabs.set('activeIndex', 0);
		    hm.util.reportFieldError(inputElement1, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
		    inputElement1.focus();
		    return false;
		}

		if(inputElement2.value <= 0){
			hiveApTabs.set('activeIndex', 0);
		    hm.util.reportFieldError(inputElement2, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
		    inputElement2.focus();
		    return false;
		}
	}

	return true;
}

function validateConfigTemplate(operation){
	if(operation == 'create' || operation == 'update'){
		var inputElement = document.getElementById(formName + "_configTemplate");
		if(inputElement.value <= 0){
			hiveApTabs.set('activeIndex', 0);
		    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.template" /></s:param></s:text>');
		    inputElement.focus();
		    return false;
		}
	}
	return true;
}

function validateLocation(operation){
	if(operation == 'create' || operation == 'update'){
		var inputElement = document.getElementById(formName + "_dataSource_location");
		if(inputElement.value.length > 0){
			var message = hm.util.validateString(inputElement.value, '<s:text name="hiveAp.location" />');
	    	if (message != null) {
	    		hiveApTabs.set('activeIndex', 0);
	    		hm.util.reportFieldError(inputElement, message);
	        	inputElement.focus();
	        	return false;
	    	}
		    if (inputElement.value.indexOf('@') > -1) {
		      hiveApTabs.set('activeIndex', 0);
		      hm.util.reportFieldError(inputElement, "<s:text name='error.value.internal.used'><s:param>'@'</s:param></s:text>");
		      inputElement.focus();
		      return false;
		    }
	    }
	}
	return true;
}

function validateHostName(operation){
	if(operation == 'create' || operation == 'update'){
		var inputElement = document.getElementById(formName + "_dataSource_hostName");

		var message = hm.util.validateName(inputElement.value, '<s:text name="hiveAp.hostName" />');
    	if (message != null) {
    		hiveApTabs.set('activeIndex', 0);
    		hm.util.reportFieldError(inputElement, message);
        	inputElement.focus();
        	return false;
    	}
	}
	return true;
}

function checkMacAddress(el, label, tabIndex){
	if(el.value.length == 0){
		hiveApTabs.set('activeIndex', tabIndex);
        hm.util.reportFieldError(el, '<s:text name="error.requiredField"><s:param>'+label+'</s:param></s:text>');
        el.focus();
        return false;
	} else if (!hm.util.validateMacAddress(el.value, 12)) {
		hiveApTabs.set('activeIndex', tabIndex);
		hm.util.reportFieldError(el, '<s:text name="error.formatInvalid"><s:param>'+label+'</s:param></s:text>');
		el.focus();
		return false;
	}
	return true;
}

function validateMacAddress(operation){
	if(operation == 'create'){
		var macAddressElement = document.getElementById(formName + "_dataSource_macAddress");
		if(!checkMacAddress(macAddressElement, '<s:text name="hiveAp.macaddress" />', 0)){
			return false;
		}
	}
	return true;
}

function validateDtlsSettings(operation){
	if(operation == 'create' || operation == 'update'){
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
	    		hiveApTabs.set('activeIndex', 1);
	    		hm.util.reportFieldError(newDtlsElement, message);
	        	newDtlsElement.focus();
	        	return false;
	    	}
			message = hm.util.validatePassword(confirmDtlsElement.value, '<s:text name="hiveAp.dtls.confirmPassPhrase" />');
	    	if (message != null) {
	    		hiveApTabs.set('activeIndex', 1);
	    		hm.util.reportFieldError(confirmDtlsElement, message);
	        	confirmDtlsElement.focus();
	        	return false;
	    	}

			if (newDtlsElement.value.length < 16) {
				hiveApTabs.set('activeIndex', 1);
			    hm.util.reportFieldError(newDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    newDtlsElement.focus();
			    return false;
			}

			if (confirmDtlsElement.value.length < 16) {
				hiveApTabs.set('activeIndex', 1);
			    hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    confirmDtlsElement.focus();
			    return false;
			}

			if (newDtlsElement.value != confirmDtlsElement.value) {
				hiveApTabs.set('activeIndex', 1);
			 	hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.notEqual"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param></s:text>');
				newDtlsElement.focus();
				return false;
			}
		}
	}
	return true;
}

function validateOptionNewUsernameAndPassword(operation, name, password, confirm){
	if(operation == 'create' || operation == 'update'){
		if (name.value.length > 0 || password.value.length > 0 || confirm.value.length > 0){
			var message = hm.util.validateUsername(name.value, '<s:text name="hiveAp.newUser" />');
	    	if (message != null) {
	    		hiveApTabs.set('activeIndex', 1);
	    		hm.util.reportFieldError(name, message);
	        	name.focus();
	        	return false;
	    	}

			if(name.value.length < 3){
				hiveApTabs.set('activeIndex', 1);
			    hm.util.reportFieldError(name, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.newUser" /></s:param><s:param><s:text name="hiveAp.currentUserRange" /></s:param></s:text>');
			    name.focus();
			    return false;
			}

			if (!hm.util.validateUserNewPasswordFormat(password, confirm, '<s:text name="hiveAp.newPassword" />',
	    			'<s:text name="hiveAp.newConfirmPassword" />', 8, '<s:text name="hiveAp.currentPasswordRange" />', name.value)) {
				hiveApTabs.set('activeIndex', 1);
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
			 	hiveApTabs.set('activeIndex', 1);
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
	if(operation == 'create' || operation == 'update'){
		var usernameElement = document.getElementById(formName + "_dataSource_cfgAdminUser");
		var readOnlyElement = document.getElementById(formName + "_dataSource_cfgReadOnlyUser");
		if(usernameElement.value.length > 0 && readOnlyElement.value.length > 0){
			if(readOnlyElement.value == usernameElement.value){
				hiveApTabs.set('activeIndex', 1);
	            hm.util.reportFieldError(readOnlyElement, "<s:text name='error.hiveAp.adminName'></s:text>");
	            readOnlyElement.focus();
	            return false;
			}
		}
	}
	return true;
}

function validateNetworkSetting(operation){
	if(operation == 'create' || operation == 'update'){
		var dhcpElement = document.getElementById(formName + "_dataSource_dhcp");
		var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
		var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
		var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
		var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
		if(!dhcpElement.checked ){// Static ip
			if (ipAddressElement.value.length == 0) {
				hiveApTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(ipAddressElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
	            ipAddressElement.focus();
	            return false;
    		}
    		if (netmaskElement.value.length == 0) {
    			hiveApTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(netmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
	            netmaskElement.focus();
	            return false;
    		}
    		if (! hm.util.validateIpAddress(ipAddressElement.value)) {
    			hiveApTabs.set('activeIndex', 0);
				hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
				ipAddressElement.focus();
				return false;
			}
			if(! hm.util.validateMask(netmaskElement.value)){
				hiveApTabs.set('activeIndex', 0);
				hm.util.reportFieldError(netmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
				netmaskElement.focus();
				return false;
			}
			if (gatewayElement.value.length != 0) {
				if (! hm.util.validateIpAddress(gatewayElement.value)) {
					hiveApTabs.set('activeIndex', 0);
					hm.util.reportFieldError(gatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
					gatewayElement.focus();
					return false;
				}
				//check if they are in the same subnet
				var message = hm.util.validateIpSubnet(ipAddressElement.value, '<s:text name="hiveAp.ipAddress" />', gatewayElement.value, '<s:text name="hiveAp.gateway" />', netmaskElement.value);
				if(null != message){
					hm.util.reportFieldError(ipAddressElement, message);
					return false;
				}
			}
		} else { // DHCP ip
			//check dhcp timeout value;
			var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
			if (dhcpTimeoutEl.value.length == 0) {
				hm.util.reportFieldError(dhcpTimeoutEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.dhcpTimeout" /></s:param></s:text>');
				hiveApTabs.set('activeIndex', 0);
				dhcpTimeoutEl.focus();
				return false;
			}
			var message = hm.util.validateIntegerRange(dhcpTimeoutEl.value, '<s:text name="hiveAp.dhcpTimeout" />',
			                                           <s:property value="0" />,
			                                           <s:property value="3600" />);
			if (message != null) {
				hm.util.reportFieldError(dhcpTimeoutEl, message);
				hiveApTabs.set('activeIndex', 0);
				dhcpTimeoutEl.focus();
				return false;
			}

			if(dhcpFallbackEl.checked){
				//when ip address is not blank, check for ipAddress, netmask, gateway
				if (ipAddressElement.value.length != 0) {
		    		if (! hm.util.validateIpAddress(ipAddressElement.value)) {
		    			hiveApTabs.set('activeIndex', 0);
						hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
						ipAddressElement.focus();
						return false;
					}
		    		if (netmaskElement.value.length == 0) {
		    			hiveApTabs.set('activeIndex', 0);
			            hm.util.reportFieldError(netmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			            netmaskElement.focus();
			            return false;
		    		}
					if(! hm.util.validateMask(netmaskElement.value)){
						hiveApTabs.set('activeIndex', 0);
						hm.util.reportFieldError(netmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
						netmaskElement.focus();
						return false;
					}
					if (gatewayElement.value.length != 0) {
						if (! hm.util.validateIpAddress(gatewayElement.value)) {
							hiveApTabs.set('activeIndex', 0);
							hm.util.reportFieldError(gatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
							gatewayElement.focus();
							return false;
						}
						//check if they are in the same subnet
						var message = hm.util.validateIpSubnet(ipAddressElement.value, '<s:text name="hiveAp.ipAddress" />', gatewayElement.value, '<s:text name="hiveAp.gateway" />', netmaskElement.value);
						if(null != message){
							hm.util.reportFieldError(ipAddressElement, message);
							return false;
						}
					}
				}
			}else{
				//when ip prefix is not blank, check for ip prefix format
				if (ipAddressElement.value.length != 0) {
		    		if (!hm.util.validateIpAddress(ipAddressElement.value)) {
		    			hiveApTabs.set('activeIndex', 0);
						hm.util.reportFieldError(ipAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.default.ipAddress" /></s:param></s:text>');
						ipAddressElement.focus();
						return false;
					}
					if(netmaskElement.value.length != 0 
							&& netmaskElement.value.trim() != "255.0.0.0"
							&& netmaskElement.value.trim() != "255.255.0.0"
							&& netmaskElement.value.trim() != "255.255.255.0"){
						hiveApTabs.set('activeIndex', 0);
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

function validateInterfaceModes(operation){
	if (operation == 'create' || operation == 'update'){
		var wifi0OperationModeEl = document.getElementById(formName+"_dataSource_wifi0_operationMode");
		var wifi1OperationModeEl = document.getElementById(formName+"_dataSource_wifi1_operationMode");
		var allAccessMode = false;
		if(isWifi1Available()){
			allAccessMode = (wifi0OperationModeEl.value == OPERATION_MODE_ACCESS && wifi1OperationModeEl.value == OPERATION_MODE_ACCESS);
		}else{
			allAccessMode = (wifi0OperationModeEl.value == OPERATION_MODE_ACCESS);
		}
		if(!allAccessMode){//Only need check when all mode is access
			return true;
		}
		var eth0OperationModeEl = document.getElementById(formName+"_dataSource_eth0_operationMode");
		var eth1OperationModeEl = document.getElementById(formName+"_dataSource_eth1_operationMode");
		var agg0OperationModeEl = document.getElementById(formName+"_dataSource_agg0_operationMode");
		var red0OperationModeEl = document.getElementById(formName+"_dataSource_red0_operationMode");
		var eth0AdminStateEl = document.getElementById(formName+"_dataSource_eth0_adminState");
		var eth1AdminStateEl = document.getElementById(formName+"_dataSource_eth1_adminState");
		var agg0AdminStateEl = document.getElementById(formName+"_dataSource_agg0_adminState");
		var red0AdminStateEl = document.getElementById(formName+"_dataSource_red0_adminState");
		var eth0BindInterfaceEl = document.getElementById(formName+"_dataSource_eth0_bindInterface");
		var eth1BindInterfaceEl = document.getElementById(formName+"_dataSource_eth1_bindInterface");
		
		if(isEth1Available()){
			var backhaulStateEls = [];
			if(eth0BindInterfaceEl.value==BIND_INTERFACE_NONE){
				if(eth0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(eth0AdminStateEl);
				}
			}else if(eth0BindInterfaceEl.value==BIND_INTERFACE_AGG0){
				if(agg0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(agg0AdminStateEl);
				}
			}else if(eth0BindInterfaceEl.value==BIND_INTERFACE_RED0){
				if(red0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(red0AdminStateEl);
				}
			}
			if(eth1BindInterfaceEl.value==BIND_INTERFACE_NONE){
				if(eth1OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(eth1AdminStateEl);
				}
			}else if(eth1BindInterfaceEl.value==BIND_INTERFACE_AGG0){
				if(agg0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(agg0AdminStateEl);
				}
			}else if(eth1BindInterfaceEl.value==BIND_INTERFACE_RED0){
				if(red0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					backhaulStateEls.push(red0AdminStateEl);
				}
			}
			if(backhaulStateEls.length == 0){
				hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			}
			var allDown = true;
			for(var el in backhaulStateEls){
				if(backhaulStateEls[el].value == IF_ADMIN_STATUS_UP){
					allDown = false;
					break;
				}
			}
			if(allDown){
				hm.util.reportFieldError(eth0AdminStateEl, '<s:text name="error.hiveAp.backhaul.downAll"></s:text>');
				return false;
			}
		}else{//Only eth0
			if(eth0OperationModeEl.value != OPERATION_MODE_BACKHAUL){
				hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			} else if(eth0AdminStateEl.value==IF_ADMIN_STATUS_DOWN){
				hm.util.reportFieldError(eth0AdminStateEl, '<s:text name="error.hiveAp.backhaul.downAll"></s:text>');
				return false;
			}
		}
	}
	return true;
}

function validateNativeVlan(operation){
	var cbxEl = document.getElementById("overrideVlan");
	if(!cbxEl.checked){
		return true;
	}
	var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");

	if (nativeVlanElement.value.length == 0) {
		hm.util.reportFieldError(nativeVlanElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.nativeVlan" /></s:param></s:text>');
		hiveApTabs.set('activeIndex', 4);
		nativeVlanElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(nativeVlanElement.value, '<s:text name="hiveAp.nativeVlan" />',
	                                           <s:property value="1" />,
	                                           <s:property value="4094" />);
	if (message != null) {
		hm.util.reportFieldError(nativeVlanElement, message);
		hiveApTabs.set('activeIndex', 4);
		nativeVlanElement.focus();
		return false;
	}
	return true;
}

function validateStaticRoute(operation){
	if(operation == 'addStaticRoute'){
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
	if (operation == 'removeStaticRoutes'){
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

function validateDynamicRoute(operation){
	if( operation == 'addDynamicRoute'){
		var neighborMacElement = document.getElementById(formName + "_neighborMac");
		var displayElement = document.getElementById("checkAll");
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
	}
	if (operation == 'removeDynamicRoutes'){
		var cbs = document.getElementsByName('dynamicRouteIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAll");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAll");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	if(operation == 'create' || operation == 'update'
		|| operation == 'addStaticRoute' || operation == 'addDynamicRoute'
		|| operation == 'removeStaticRoutes' || operation == 'removeDynamicRoutes'
		|| operation == 'newRadioProfile' || operation == 'newTemplate'
		|| operation == 'newCapwapIp' || operation == 'newIpTrack'
		|| operation == 'newScheduler' || operation == 'newRadius'
		|| operation == 'newDhcpServer' || operation == 'newLldpCdp'
		|| operation == 'newSuppCLI' || operation == 'editSuppCLI'){
		var metricIntervalElement = document.getElementById(formName + "_dataSource_metricInteval");
		var routeMinimumElement = document.getElementById(formName + "_routeMinimun");
		var routeMaximumElement = document.getElementById(formName + "_routeMaximun");
		var displayElement = document.getElementById("checkAll");
		if (metricIntervalElement.value.length == 0) {
			hm.util.reportFieldError(metricIntervalElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.cfg.dynamicRoute.metric.interval" /></s:param></s:text>');
			hiveApTabs.set('activeIndex', 2);
			return false;
		}
		var message = hm.util.validateIntegerRange(metricIntervalElement.value, '<s:text name="hiveAp.cfg.dynamicRoute.metric.interval" />',
		                                           <s:property value="%{metricIntervalRange.min()}" />,
		                                           <s:property value="%{metricIntervalRange.max()}" />);
		if (message != null) {
			hm.util.reportFieldError(metricIntervalElement, message);
			hiveApTabs.set('activeIndex', 2);
			return false;
		}

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
	return true;
}

function validateNeighborMac(operation){
	if(operation == 'addDynamicRoute'){
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
	if(operation == 'addStaticRoute'){
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
	if(operation == 'create' || operation == 'update'){
		var includedNeighbor = document.getElementById("includedNeighbors");
		var excludedNeighbor = document.getElementById("excludedNeighbors");
		var displayElement = document.getElementById("l3RoamingLabel");

		var in_options = includedNeighbor.options;
		var ex_options = excludedNeighbor.options;
		if(in_options != null && ex_options != null){
			for(var i=0; i< in_options.length; i++){
				var in_mac = in_options[i].value;
				for(var j=0; j< ex_options.length; j++){
					var ex_mac = ex_options[j].value;
					if(in_mac == ex_mac){
						hm.util.reportFieldError(displayElement, '<s:text name="error.bothInIncludeExcludeNeighbor"></s:text>');
						hiveApTabs.set('activeIndex', 4);
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

function validateIpTracking(operation){
	if(operation == 'create' || operation == 'update'){
		var ipTracking = document.getElementById("ipTracks");
		var displayElement = document.getElementById("ipTrackLabel");
		if(ipTracking.options.length > 8){
			hm.util.reportFieldError(displayElement, '<s:text name="error.hiveAp.overflow.ipTracking"></s:text>');
			hiveApTabs.set('activeIndex', 4);
			return false;
		}
	}
	return true;
}

function validateRADIUSServer(operation){
	if(operation == 'create' || operation == 'update'){
		var radiusElement = document.getElementById(formName + "_radiusServer");
		if(radiusElement.value > 0){
			var dhcpboxElement = document.getElementById(formName + "_dataSource_dhcp");
			if(dhcpboxElement.checked){
				hm.util.reportFieldError(dhcpboxElement, '<s:text name="error.hiveAp.radiusServer.term"></s:text>');
				hiveApTabs.set('activeIndex', 0);
				return false;
			}
		}
	}
	return true;
}

function validateVPNServer(operation){
	if(operation == 'create' || operation == 'update'){
		var modelElement = document.getElementById(formName + "_dataSource_hiveApModel");
		var vpnMarkElement = document.getElementById(formName + "_dataSource_vpnMark");
		if(vpnMarkElement.value == VPN_MARK_SERVER){
			// must be 11n ap
			if(!isVpnServerAvaliable()){
				hm.util.reportFieldError(modelElement, '<s:text name="error.hiveAp.vpnServer.hiveApModel"></s:text>');
				hiveApTabs.set('activeIndex', 0);
				return false;
			}
			// couldn't be dhcp client
			var dhcpboxElement = document.getElementById(formName + "_dataSource_dhcp");
			if(dhcpboxElement.checked){
				hm.util.reportFieldError(dhcpboxElement, '<s:text name="error.hiveAp.vpnServer.term"></s:text>');
				hiveApTabs.set('activeIndex', 0);
				return false;
			}
		}
	}
	return true;
}

function validateVPNClient(operation){
	if(operation == 'create' || operation == 'update'){
		var vpnMarkElement = document.getElementById(formName + "_dataSource_vpnMark");
		var modelElement = document.getElementById(formName + "_dataSource_hiveApModel");
		if(vpnElementClient.value == VPN_MARK_CLIENT){
			// must be 11n ap
			if(!is11nHiveAP()){
				hm.util.reportFieldError(modelElement, '<s:text name="error.hiveAp.vpnClient.hiveApModel"></s:text>');
				hiveApTabs.set('activeIndex', 0);
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
function dhcpEvent(isChecked){
	var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
	var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
	var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
	var dhcpFallbackEl = document.getElementById(formName + "_dataSource_dhcpFallback");
	var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
	var dhcpLabelEl = document.getElementById("dhcpLabel");
	var addressOnlyEl = document.getElementById(formName + "_dataSource_addressOnly");
	//clear dhcp fallback selection
	if(dhcpFallbackEl.checked){
		dhcpFallbackEl.checked = false;
		dhcpFallbackEvent(false);
	}
	if(addressOnlyEl.checked){
		addressOnlyEl.checked = false;
	}
//	ipAddressElement.disabled = isChecked;
//	netmaskElement.disabled = isChecked;
	gatewayElement.disabled = isChecked;
	dhcpFallbackEl.disabled = !isChecked;
	dhcpTimeoutEl.disabled = !isChecked;
	addressOnlyEl.disabled = !isChecked;
	if(isChecked){
		staticIp = ipAddressElement.value;
		staticMask = netmaskElement.value;
		ipAddressElement.value = defaultIp;
		netmaskElement.value = defaultMask;
		ipAddressElement.setAttribute("title", "<s:text name='hiveAp.default.ipAddress.note' />");
		netmaskElement.setAttribute("title","<s:text name='hiveAp.default.netmask.note' />");
		dhcpLabelEl.innerHTML = "<s:text name='hiveAp.default.ipAddress' />";
	}else{
		defaultIp = ipAddressElement.value;
		defaultMask = netmaskElement.value;
		ipAddressElement.value = staticIp;
		netmaskElement.value = staticMask;
		ipAddressElement.setAttribute("title", "");
		netmaskElement.setAttribute("title","");
		dhcpLabelEl.innerHTML = "<s:text name='hiveAp.ipAddress' />";
	}
}

function dhcpFallbackEvent(isChecked){
	var ipAddressElement = document.getElementById(formName + "_dataSource_cfgIpAddress");
	var netmaskElement = document.getElementById(formName + "_dataSource_cfgNetmask");
	var gatewayElement = document.getElementById(formName + "_dataSource_cfgGateway");
//	var dhcpTimeoutEl = document.getElementById(formName + "_dataSource_dhcpTimeout");
	var dhcpLabelEl = document.getElementById("dhcpLabel");
//	ipAddressElement.disabled = !isChecked;
//	netmaskElement.disabled = !isChecked;
	gatewayElement.disabled = !isChecked;
//	dhcpTimeoutEl.disabled = !isChecked;
	if(isChecked){
		defaultIp = ipAddressElement.value;
		defaultMask = netmaskElement.value;
		ipAddressElement.value = staticIp;
		netmaskElement.value = staticMask;
		ipAddressElement.setAttribute("title", "");
		netmaskElement.setAttribute("title","");
		dhcpLabelEl.innerHTML = "<s:text name='hiveAp.ipAddress' />";
	}else{
		staticIp = ipAddressElement.value;
		staticMask = netmaskElement.value;
		ipAddressElement.value = defaultIp;
		netmaskElement.value = defaultMask;
		ipAddressElement.setAttribute("title", "<s:text name='hiveAp.default.ipAddress.note' />");
		netmaskElement.setAttribute("title","<s:text name='hiveAp.default.netmask.note' />");
		dhcpLabelEl.innerHTML = "<s:text name='hiveAp.default.ipAddress' />";
	}
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

function addRadioProfile(cb) {
	if (cb.value != -2 ) {
		// change the channel list, joseph chen 04/23/2008
		// just for 11n box, do not invoke when for ag20 box!
		if(is11nHiveAP()){
			if(cb.id == (formName + "_wifi0RadioProfile")){
				getWifi0Channels(cb.value);
			}else if(cb.id == (formName + "_wifi1RadioProfile")){
				var operationMode = document.getElementById(formName + "_dataSource_wifi1_operationMode").value;
				getWifi1Channels(cb.value, operationMode);
			}
		}
		return;
	}

	// create a new radio profile
	var wifi0RadioProfileElement = document.getElementById(formName + "_wifi0RadioProfile");
	var wifi1RadioProfileElement = document.getElementById(formName + "_wifi1RadioProfile");

	if(cb == wifi0RadioProfileElement){
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "bg";//default radio type
		}else{
			document.forms[formName].radioType.value = "ng";//default radio type
		}
	}else if(cb == wifi1RadioProfileElement){
		if(!is11nHiveAP()){
			document.forms[formName].radioType.value = "a";//default radio type
		}else{
			document.forms[formName].radioType.value = "na";//default radio type
		}
	}
	submitAction('newRadioProfile');
}

function showCreateSection(type) {
    if(type=='dynamic')
    {
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
    }
	if(type=='static')
    {
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
    }
}
function hideCreateSection(type) {
    if(type=='dynamic'){
        hm.util.hide('createButton');
        hm.util.show('newButton');
        hm.util.hide('createSection');
        document.getElementById("expanding_dynamic").value="false";
    }
    if(type=='static'){
        hm.util.hide('createButtonStatic');
        hm.util.show('newButtonStatic');
        hm.util.hide('createSectionStatic');
        document.getElementById("expanding_static").value="false";
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

<%-- callback for changing hiveApModel, joseph chen, 04/14/2008 --%>
function changeApModel(){
	updateInterfaceLayout();
	// refresh radio profile and channels
	updateRadioProfiles();
	// refresh static route interface types
	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel");
	getStaticRouteInterface(selectApModel.value);
}

function updateInterfaceLayout(){
	var wifi0RadioProfile = document.getElementById(formName + "_wifi0RadioProfile");
	var wifi1RadioProfile = document.getElementById(formName + "_wifi1RadioProfile");
	
	if(isEth1Available()){  // 11n except HiveAP 120, HiveAP 110
		// show eth1/red0/agg0
		hm.util.show('eth1Row');
		hm.util.show('red0Row');
		hm.util.show('agg0Row');

		// show column of bind interface and role
		hm.util.show('eth0BindColumn');
		hm.util.show('eth0BindHeader');
		hm.util.show('eth0RoleColumn');
		hm.util.show('eth0RoleHeader');

		// hide or show eth0/eth1 bind interface
		changeBindInterface();
		
		// disable or enable eth0/eth1 bind interface
		ethOperationModeChanged();
	} else{ // ag20 or HiveAP 120 or HiveAP 110
		// hide eth1/red0/agg0
		hm.util.hide('eth1Row');
		hm.util.hide('red0Row');
		hm.util.hide('agg0Row');

		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth0_operationMode").disabled = false;

		document.getElementById(formName + "_dataSource_eth0_bindInterface").value = 0;
		document.getElementById(formName + "_dataSource_eth1_bindInterface").value = 0;

		// hide column of bind interface and role
		hm.util.hide('eth0BindColumn');
		hm.util.hide('eth0BindHeader');
		hm.util.hide('eth0RoleColumn');
		hm.util.hide('eth0RoleHeader');
	}
	if(isWifi1Available()){
		hm.util.show('wifi1Row');
	}else{
		hm.util.hide('wifi1Row');
	}
	if(!is11nHiveAP()){
		var eth0SpeedEl = document.getElementById(formName + "_dataSource_eth0_speed");
		if(eth0SpeedEl.value == ETH_SPEED_1000M){
			eth0SpeedEl.selectedIndex = 0;
		}	
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	if(details.wifi0){
		var wifi0 = document.getElementById(formName + "_wifi0RadioProfile");
		wifi0.length=0;
		wifi0.length=details.wifi0.length;
		for(var i = 0; i < details.wifi0.length; i ++) {
			wifi0.options[i].value = details.wifi0[i].id;
			wifi0.options[i].text = details.wifi0[i].v;
		}
		if(details.wifi0d){
			wifi0.value = details.wifi0d;
		}
		getWifi0Channels(details.wifi0d || details.wifi0[0].id);
	}
	if(details.wifi1){
		var wifi1 = document.getElementById(formName + "_wifi1RadioProfile");
		var operationMode = document.getElementById(formName + "_dataSource_wifi1_operationMode").value;
		wifi1.length=0;
		wifi1.length=details.wifi1.length;
		for(var i = 0; i < details.wifi1.length; i ++) {
			wifi1.options[i].value = details.wifi1[i].id;
			wifi1.options[i].text = details.wifi1[i].v;
		}
		if(details.wifi1d){
			wifi1.value = details.wifi1d;
		}
		getWifi1Channels(details.wifi1d || details.wifi1[0].id, operationMode);
	}
};

var detailsFailed = function(o) {
	//alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function updateRadioProfiles(){
	var modelType = document.getElementById(formName + "_dataSource_hiveApModel").value;
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=fetchRadioProfiles&apModelType="+modelType;
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
}

function getWifi0Channels(profileId) {
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi0Channels" + "&profileId="+profileId;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : changeRadioChannels, failure : connectFailed}, null);
}

function getWifi1Channels(profileId, wifi1OperationMode) {
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi1Channels" + "&profileId="+profileId + "&wifi1OperationMode="+wifi1OperationMode;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : changeRadioChannels, failure : connectFailed}, null);
}

var changeRadioChannels = function(o)
{
	eval("var details = " + o.responseText);
	var selectElement;
	var radioModeEl;
	var radioModeLabel;

	if(details[0].m == "bg" || details[0].m == "ng"){
	    selectElement = document.getElementById(formName + "_dataSource_wifi0_channel");
		radioModeEl = document.getElementById("wifi0RadioMode");
	}else{
	    selectElement = document.getElementById(formName + "_dataSource_wifi1_channel");
	    radioModeEl = document.getElementById("wifi1RadioMode");
	}

	radioModeLabel = details[0].l;
	
	selectElement.length=0;
	selectElement.length=details.length;

	for(var i = 0; i < details.length; i ++) {
		selectElement.options[i].value = details[i].k;
		selectElement.options[i].text = details[i].v;
	}
	// set radio mode label
	radioModeEl.replaceChild(document.createTextNode(radioModeLabel), radioModeEl.childNodes[0]);
};

var connectFailed = function(o)
{
    //alert("connection failed.");
};

// called when bind interface of eth0 or eth1 is changed, joseph chen 04/24/2008
function changeBindInterface() {
	var eth0BindElement = document.getElementById(formName + "_dataSource_eth0_bindInterface");
	var eth1BindElement = document.getElementById(formName + "_dataSource_eth1_bindInterface");

	// update admin status & operation mode
	if(eth0BindElement.value == BIND_INTERFACE_NONE){
		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth0_operationMode").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = true;
		document.getElementById(formName + "_dataSource_eth0_adminState").value = IF_ADMIN_STATUS_UP;
		document.getElementById(formName + "_dataSource_eth0_operationMode").disabled = true;
		document.getElementById(formName + "_dataSource_eth0_operationMode").value = OPERATION_MODE_BACKHAUL;
	}
	if(eth1BindElement.value == BIND_INTERFACE_NONE){
		document.getElementById(formName + "_dataSource_eth1_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_operationMode").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth1_adminState").disabled = true;
		document.getElementById(formName + "_dataSource_eth1_adminState").value = IF_ADMIN_STATUS_UP;
		document.getElementById(formName + "_dataSource_eth1_operationMode").disabled = true;
		document.getElementById(formName + "_dataSource_eth1_operationMode").value = OPERATION_MODE_BACKHAUL;
	}
	
	// update bind role header
	if(eth0BindElement.value == BIND_INTERFACE_RED0 || eth1BindElement.value == BIND_INTERFACE_RED0){
		hm.util.show('eth0RoleHeader');
	}else{
		hm.util.hide('eth0RoleHeader');
	}
	// update bind role
	if(eth0BindElement.value == BIND_INTERFACE_RED0){
		hm.util.show('eth0RoleColumn');
	}else{
		hm.util.hide('eth0RoleColumn');
	}
	if(eth1BindElement.value == BIND_INTERFACE_RED0){
		hm.util.show('eth1RoleColumn');
	}else{
		hm.util.hide('eth1RoleColumn');
	}

	// update red0 rows
	if(eth0BindElement.value == BIND_INTERFACE_RED0 || eth1BindElement.value == BIND_INTERFACE_RED0){
		hm.util.show('red0Row');
	}else{
		hm.util.hide('red0Row');
	}
	// update agg0
	if(eth0BindElement.value == BIND_INTERFACE_AGG0 || eth1BindElement.value == BIND_INTERFACE_AGG0){
		hm.util.show('agg0Row');
	}else{
		hm.util.hide('agg0Row');
	}
}

function ethOperationModeChanged(){
	if(!isEth1Available()){return;}
	var eth0OperationMode = document.getElementById(formName + "_dataSource_eth0_operationMode").value;
	var eth1OperationMode = document.getElementById(formName + "_dataSource_eth1_operationMode").value;
	var eth0AdminStatus = document.getElementById(formName + "_dataSource_eth0_adminState").value;
	var eth1AdminStatus = document.getElementById(formName + "_dataSource_eth1_adminState").value;
	if(eth0AdminStatus == IF_ADMIN_STATUS_UP && eth0OperationMode == OPERATION_MODE_BACKHAUL){
		document.getElementById(formName + "_dataSource_eth0_bindInterface").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth0_bindInterface").disabled = true;
	}
	if(eth1AdminStatus == IF_ADMIN_STATUS_UP && eth1OperationMode == OPERATION_MODE_BACKHAUL){
		document.getElementById(formName + "_dataSource_eth1_bindInterface").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth1_bindInterface").disabled = true;
	}
}

function ethAdminStatusChanged(){
	if(!isEth1Available()){return;}
	var eth0OperationMode = document.getElementById(formName + "_dataSource_eth0_operationMode").value;
	var eth1OperationMode = document.getElementById(formName + "_dataSource_eth1_operationMode").value;
	var eth0AdminStatus = document.getElementById(formName + "_dataSource_eth0_adminState").value;
	var eth1AdminStatus = document.getElementById(formName + "_dataSource_eth1_adminState").value;
	if(eth0AdminStatus == IF_ADMIN_STATUS_UP && eth0OperationMode == OPERATION_MODE_BACKHAUL){
		document.getElementById(formName + "_dataSource_eth0_bindInterface").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth0_bindInterface").disabled = true;
	}
	if(eth1AdminStatus == IF_ADMIN_STATUS_UP && eth1OperationMode == OPERATION_MODE_BACKHAUL){
		document.getElementById(formName + "_dataSource_eth1_bindInterface").disabled = false;
	}else{
		document.getElementById(formName + "_dataSource_eth1_bindInterface").disabled = true;
	}
}

/**
 * 1000M is only available in Ag20 model
 * called when speed of eth0 or eth1 is changed.
 * joseph chen 05/19/2008
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
 * joseph chen 05/19/2008
 */
function validateEthSpeed(operation) {
	if('create' == operation || 'update' == operation) {
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
		document.getElementById(formName + "_dataSource_eth0_bindInterface").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_bindInterface").disabled = false;
		document.getElementById(formName + "_dataSource_eth0_operationMode").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_operationMode").disabled = false;
		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_adminState").disabled = false;
	}
}

function getStaticRouteInterface(apModel) {
	url = "<s:url action='hiveAp' includeParams='none' />?operation=staticRouteInterface" + "&apModelType=" + apModel;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : refreshStaticRouteInterface, failure : connectFailed}, null);
}

var refreshStaticRouteInterface = function(o) {
	eval("var details = " + o.responseText);
	var selectElement = document.getElementById(formName + "_interfaceType");

	selectElement.length=0;
	selectElement.length=details.length;

	for(var i = 0; i < details.length; i ++) {
		selectElement.options[i].value = details[i].k;
		selectElement.options[i].text = details[i].v;
	}
};

function changeNativeVlanInput(cbxValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_nativeVlan");
	nativeVlanEl.disabled = !cbxValue;
}

function updateNativeVlan(){
	var overrideEl = document.getElementById("overrideVlan");
	if(!overrideEl.checked){
		document.getElementById(formName + "_dataSource_nativeVlan").value="";
	}
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
	var wifi0El = document.getElementById(formName + "_dataSource_wifi0_operationMode");
	var wifi0Ad = document.getElementById(formName + "_dataSource_wifi0_adminState");
	var operationMode = wifi0El.options[wifi0El.selectedIndex].value;
	var adminStatusMode = wifi0Ad.options[wifi0Ad.selectedIndex].value;
	var enabled = getSsidStatus(operationMode, adminStatusMode);//wifi0 by default;
	disableCheckBoxList("wifi0", enabled);
	setCheckBoxSelected("checkAll_0", "ssid0Indices");

	var wifi1El = document.getElementById(formName + "_dataSource_wifi1_operationMode");
	var wifi1Ad = document.getElementById(formName + "_dataSource_wifi1_adminState");
	operationMode = wifi1El.options[wifi1El.selectedIndex].value;
	adminStatusMode = wifi1Ad.options[wifi1Ad.selectedIndex].value;
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
			cbs[i].disabled = !enabled;
			// hide while element disabled
			cbs[i].parentNode.parentNode.style.display = enabled?"":"none";
		}
		el.disabled = !enabled;
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
	el.checked = allSelected;
}

function operationModeChanged(selectEl, wifiName){
	var adminStatusValue;
	var modeValue = selectEl.options[selectEl.selectedIndex].value;
	if("wifi0" == wifiName){
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi0_adminState").value;
	}else if ("wifi1" == wifiName){
		adminStatusValue = document.getElementById(formName + "_dataSource_wifi1_adminState").value;
		//change channel list for DFS
		var wifi1RadioProfileId = document.getElementById(formName + "_wifi1RadioProfile").value;
		getWifi1Channels(wifi1RadioProfileId, selectEl.value);
	}
	var enabled = getSsidStatus(modeValue, adminStatusValue);
	disableCheckBoxList(wifiName, enabled );
}

function getSsidStatus(modeValue, adminStatusValue){
	if(modeValue == OPERATION_MODE_ACCESS && adminStatusValue == IF_ADMIN_STATUS_UP){// access and up
		return true;
	}else if (modeValue == OPERATION_MODE_BACKHAUL || adminStatusValue == IF_ADMIN_STATUS_DOWN){// backhaul or down
		return false;
	}
	return false;
}

function adminStatusChanged(selectEl, wifiName){
	var adminStatusValue = selectEl.options[selectEl.selectedIndex].value;
	var modeValue;
	if("wifi0" == wifiName){
		modeValue = document.getElementById(formName + "_dataSource_wifi0_operationMode").value;
	}else if ("wifi1" == wifiName){
		modeValue = document.getElementById(formName + "_dataSource_wifi1_operationMode").value;
	}
	var enabled = getSsidStatus(modeValue, adminStatusValue);
	disableCheckBoxList(wifiName, enabled );
}


function requestTemplateInfo(selectEl){
	var templateId = selectEl.value;
	url = "<s:url action='hiveAp' includeParams='none' />?operation=requestTemplate" + "&configTemplate=" + templateId;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : getTemplateInfo}, null);
}

var getTemplateInfo = function(o){
	eval("var details = " + o.responseText);
	var wifi0_area = document.getElementById("wifi0ssidTable");
	var wifi1_area = document.getElementById("wifi1ssidTable");
	var wifi0_body = "";
	var wifi1_body = "";
	var head = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody>";
	var foot = "</tbody></table>";

	var vpn = details.vpn;
	var ssids = details.ssids;

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
			wifi0_body += "<tr valign=\"top\"><td style=\"padding: 5px 3px;\">"+
			"<input name=\"ssid0Indices\" value=\""+checkBoxValue+"\" "+checked +
			"id=\"hiveAp_ssid0Indices\" onclick=\"setCheckBoxSelected('checkAll_0', "+
			"'ssid0Indices');\" type=\"checkbox\"><input name=\"__checkbox_ssid0Indices\" "+
			"value=\""+checkBoxValue+"\" type=\"hidden\"></td>"+
			"<td class=\"labelT1\"><label title=\""+title+"\">"+label+"</label></td></tr>";
		}else{
			wifi1_body += "<tr valign=\"top\"><td style=\"padding: 5px 3px;\">"+
			"<input name=\"ssid1Indices\" value=\""+checkBoxValue+"\" "+checked +
			"id=\"hiveAp_ssid1Indices\" onclick=\"setCheckBoxSelected('checkAll_1', "+
			"'ssid1Indices');\" type=\"checkbox\"><input name=\"__checkbox_ssid1Indices\" "+
			"value=\""+checkBoxValue+"\" type=\"hidden\"></td>"+
			"<td class=\"labelT1\"><label title=\""+title+"\">"+label+"</label></td></tr>";
		}
	}
	wifi0_area.innerHTML = head + wifi0_body + foot;
	wifi1_area.innerHTML = head + wifi1_body + foot;
	updateSsidAllocation();
}

/*end*/

function validateCapwapIp(operation) {
	if(operation == 'create' || operation == 'update'){
		var capwapIpNames = document.getElementById("capwapSelect");
		var capwapIpValue = document.forms[formName].inputIpValue;
		var showError = document.getElementById("errorDisplay");

		var capwapBackupIpNames = document.getElementById("capwapBackupSelect");
		var capwapBackupIpValue = document.forms[formName].inputBackupIpValue;
		var showBackupError = document.getElementById("errorBackupDisplay");

	    if ("" != capwapIpValue.value) {
		    if (!hm.util.hasSelectedOptionSameValue(capwapIpNames, capwapIpValue)) {
				if (!hm.util.validateIpAddress(capwapIpValue.value)) {
					var message = hm.util.validateName(capwapIpValue.value, '<s:text name="hiveAp.capwap.server" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showError, message);
				   		hiveApTabs.set('activeIndex', 1);
				       	capwapIpValue.focus();
				       	return false;
				   	}
				}
		    	document.forms[formName].capwapIp.value = -1;
		    } else {
		    	document.forms[formName].capwapIp.value = capwapIpNames.options[capwapIpNames.selectedIndex].value;
		    }
		} else {
			document.forms[formName].capwapIp.value = -1;
		}
	    if ("" != capwapBackupIpValue.value) {
		    if (!hm.util.hasSelectedOptionSameValue(capwapBackupIpNames, capwapBackupIpValue)) {
				if (!hm.util.validateIpAddress(capwapBackupIpValue.value)) {
					var message = hm.util.validateName(capwapBackupIpValue.value, '<s:text name="hiveAp.capwap.server.backup" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showBackupError, message);
				   		hiveApTabs.set('activeIndex', 1);
				       	capwapBackupIpValue.focus();
				       	return false;
				   	}
				}
				document.forms[formName].capwapBackupIp.value = -1;
		    } else {
				document.forms[formName].capwapBackupIp.value = capwapBackupIpNames.options[capwapBackupIpNames.selectedIndex].value;
		}
		} else {
			document.forms[formName].capwapBackupIp.value = -1;
		}
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
</script>

<div id="content"><s:form action="hiveAp" >
<s:hidden name="radioType"/>
<s:hidden name="expanding_dynamic" id="expanding_dynamic" value="%{expanding_dynamic}"/>
<s:hidden name="expanding_static" id="expanding_static" value="%{expanding_static}"/>
<s:hidden name="dhcpServer" />
<s:hidden name="ipTrack" />
<s:hidden name="capwapIp" />
<s:hidden name="capwapBackupIp" />
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
							class="button" onClick="submitAction('create');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<div id="hiveApTabs" class="yui-navset" style="width:760px;">
			<ul class="yui-nav">
				<li class="selected"><a href="#tab1"><em><s:text
					name="hiveAp.tab.general" /></em></a></li>
				<li><a href="#tab2"><em><s:text
					name="hiveAp.tab.authentication" /></em></a></li>
				<li><a href="#tab3"><em><s:text
					name="hiveAp.tab.routing" /></em></a></li>
				<li><a href="#tab4"><em><s:text
					name="hiveAp.tab.service" /></em></a></li>
				<li><a href="#tab5"><em><s:text
					name="hiveAp.tab.advanced" /></em></a></li>
			</ul>
			<div class="yui-content">
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td>
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text
										name="hiveAp.hostName" /><font color="red"><s:text name="*"/></font></td>
									<td width="235px"><s:textfield name="dataSource.hostName" size="24" title="%{hostnameRange}"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{hostNameLength}"/></td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.dhcp" /></td>
									<td><s:checkbox name="dataSource.dhcp" value="%{dataSource.dhcp}"
									onclick="dhcpEvent(this.checked)"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text
									name="hiveAp.template" /></td>
									<td style="padding-right:20px;"><s:select list="%{configTemplates}"
									listKey="id" listValue="value" name="configTemplate" cssStyle="width: 198px;"
									onchange="requestTemplateInfo(this);" />
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newTemplate')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('editTemplate')"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
									</td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.addressOnly"/></td>
									<td><s:checkbox name="dataSource.addressOnly" value="%{dataSource.addressOnly}"
									disabled="%{dhcpFallbackStyle}"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text
									name="hiveAp.topology" /></td>
									<td><s:select list="%{topologys}"
									listKey="id" listValue="value" name="topology" cssStyle="width: 198px;" /></td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.dhcpFallback"/></td>
									<td><s:checkbox name="dataSource.dhcpFallback" value="%{dataSource.dhcpFallback}"
									onclick="dhcpFallbackEvent(this.checked);" disabled="%{dhcpFallbackStyle}"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text
										name="hiveAp.macaddress" /><font color="red"><s:text name="*"/></font></td>
									<td><s:textfield name="dataSource.macAddress" size="24"
										onkeypress="return hm.util.keyPressPermit(event,'hex');"
										maxlength="%{macAddressLength}" disabled="%{disabledName}"/></td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.dhcpTimeout" /></td>
									<td><s:textfield name="dataSource.dhcpTimeout" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="4" title="%{dhcpTimeoutRange}" disabled="%{dhcpFallbackStyle}"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.location" /></td>
									<td><s:textfield name="dataSource.location" size="24" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										title="%{locationRange}"/></td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140" id="dhcpLabel">
										<s:if test="%{dataSource.dhcp && !dataSource.dhcpFallback}">
											<s:text name="hiveAp.default.ipAddress" />
										</s:if>
										<s:else>
											<s:text name="hiveAp.ipAddress" />
										</s:else>
									</td>
									<td>
										<s:if test="%{dataSource.dhcp && !dataSource.dhcpFallback}">
											<s:textfield name="dataSource.cfgIpAddress" size="24" maxlength="%{cfgIpAddressLength}" title="%{defaultIpPrefixFormat}"/>
										</s:if>
										<s:else>
											<s:textfield name="dataSource.cfgIpAddress" size="24" maxlength="%{cfgIpAddressLength}"/>
										</s:else>
									</td>
									</tr>
									</table>
								</td>
							</tr>
							<tr valign="bottom">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
											<td class="labelT1" width="140"><s:text name="hiveAp.model" /></td>
											<td><s:select name="dataSource.hiveApModel" value="%{dataSource.hiveApModel}" list="%{apModel}" listKey="key"
													listValue="value" cssStyle="width: 198px;" onchange="changeApModel()" disabled="%{enableModel}" /></td>
									</tr>
									</table>
								</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.netmask" /></td>
									<td>
										<s:if test="%{dataSource.dhcp && !dataSource.dhcpFallback}">
											<s:textfield name="dataSource.cfgNetmask" size="24" maxlength="%{cfgNetmaskLength}" title="%{defaultNetmaskFormat}"/>
										</s:if>
										<s:else>
											<s:textfield name="dataSource.cfgNetmask" size="24" maxlength="%{cfgNetmaskLength}"/>
										</s:else>
									</td>
									</tr>
									</table>
								</td>
							</tr>
							<%-- add 'hiveApModel' joseph chen 04/14/2008 --%>
							<tr valign="bottom">
								<td>
								</td>
								<td valign="top">
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140"><s:text name="hiveAp.gateway" /></td>
									<td><s:textfield name="dataSource.cfgGateway" size="24" maxlength="%{cfgGatewayLength}"
									 disabled="%{networkSettingStyle}"/></td>
									</tr>
									</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
					<!-- tr>
						<td class="sepLine" colspan="3"><img
							src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
					</tr-->
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td valign="top" width="100%">
						<fieldset>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<th align="left" width="80px" nowrap="nowrap"><s:text name="hiveAp.lanIf" /></th>
								<th align="left" width="80px"><s:text name="hiveAp.if.adminState" /></th>
								<th align="left" width="120px"><s:text name="hiveAp.if.operationMode" /></th>
								<th align="left" width="120px"><s:text name="hiveAp.if.duplex" /></th>
								<th align="left" nowrap="nowrap"><s:text name="hiveAp.if.speed" /></th>
							<%-- add header 'bindInterface' and 'bindRole', joseph chen 04/10/2008 --%>
								<th id="eth0BindHeader" align="left" nowrap="nowrap" style="display: <s:property value="eth1StuffStyle"/>"><s:text name="hiveAp.if.bindIf" /></th>
								<th id="eth0RoleHeader" align="left" nowrap="nowrap" style="display: <s:property value="eth1StuffStyle"/>"><s:text name="hiveAp.if.bindRole" /></th>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td class="list"><s:text name="hiveAp.if.eth0"/></td>
								<td class="list"><s:select name="dataSource.eth0.adminState"
									value="%{dataSource.eth0.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value" onchange="ethAdminStatusChanged()"/></td>
								<td class="list"><s:select name="dataSource.eth0.operationMode"
									value="%{dataSource.eth0.operationMode}" list="%{enumEthOperationMode}" listKey="key"
									listValue="value" onchange="ethOperationModeChanged()"/></td>
								<td class="list"><s:select name="dataSource.eth0.duplex"
									value="%{dataSource.eth0.duplex}" list="%{enumDuplexType}" listKey="key"
									listValue="value" cssStyle="width: 106px;"/></td>
								<td class="list"><s:select name="dataSource.eth0.speed"
									value="%{dataSource.eth0.speed}" list="%{enumSpeedType}" listKey="key"
									listValue="value" cssStyle="width: 84px;" onchange="changeSpeed(this)"/></td>
							<%-- add column 'bindInterface' and 'bindRole', joseph chen 04/10/2008 --%>
								<td id="eth0BindColumn" class="list" style="display: <s:property value="eth1StuffStyle"/>"><s:select name="dataSource.eth0.bindInterface"
									value="%{dataSource.eth0.bindInterface}" list="%{enumBindInterface}" listKey="key"
									listValue="value" cssStyle="width: 60px;" onchange="changeBindInterface()"/></td>
								<td id="eth0RoleColumn" class="list" style="display: <s:property value="eth1StuffStyle"/>"><s:select name="dataSource.eth0.bindRole"
									value="%{dataSource.eth0.bindRole}" list="%{enumBindRole}" listKey="key"
									listValue="value" cssStyle="width: 80px;"/></td>
							</tr>
							<%-- add row 'eth1', 'red0', 'agg0', joseph chen 04/10/2008 --%>
							<tr id="eth1Row" style="display: <s:property value="eth1StuffStyle"/>">
								<td class="list"><s:text name="hiveAp.if.eth1"/></td>
								<td class="list"><s:select name="dataSource.eth1.adminState"
									value="%{dataSource.eth1.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value" onchange="ethAdminStatusChanged()"/></td>
								<td class="list"><s:select name="dataSource.eth1.operationMode"
									value="%{dataSource.eth1.operationMode}" list="%{enumEthOperationMode}" listKey="key"
									listValue="value" onchange="ethOperationModeChanged()"/></td>
								<td class="list"><s:select name="dataSource.eth1.duplex"
									value="%{dataSource.eth1.duplex}" list="%{enumDuplexType}" listKey="key"
									listValue="value" cssStyle="width: 106px;"/></td>
								<td class="list"><s:select name="dataSource.eth1.speed"
									value="%{dataSource.eth1.speed}" list="%{enumSpeedType}" listKey="key"
									listValue="value" cssStyle="width: 84px;" /></td>
								<td id="eth1BindColumn" class="list"><s:select name="dataSource.eth1.bindInterface"
									value="%{dataSource.eth1.bindInterface}" list="%{enumBindInterface}" listKey="key"
									listValue="value" cssStyle="width: 60px;" onchange="changeBindInterface()"/></td>
								<td id="eth1RoleColumn" class="list"><s:select name="dataSource.eth1.bindRole"
									value="%{dataSource.eth1.bindRole}" list="%{enumBindRole}" listKey="key"
									listValue="value" cssStyle="width: 80px;"/></td>
							</tr>
							<tr id="red0Row" style="display: <s:property value="eth1StuffStyle"/>">
								<td class="list"><s:text name="hiveAp.if.red0"/></td>
								<td class="list"><s:select name="dataSource.red0.adminState"
									value="%{dataSource.red0.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value"/></td>
								<td class="list"><s:select name="dataSource.red0.operationMode"
									value="%{dataSource.red0.operationMode}" list="%{enumRedOperationMode}" listKey="key"
									listValue="value"/></td>
								<td class="list" colspan="6">&nbsp;</td>
							</tr>
							<tr id="agg0Row" style="display: <s:property value="eth1StuffStyle"/>">
								<td class="list"><s:text name="hiveAp.if.agg0"/></td>
								<td class="list"><s:select name="dataSource.agg0.adminState"
									value="%{dataSource.agg0.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value"/></td>
								<td class="list"><s:select name="dataSource.agg0.operationMode"
									value="%{dataSource.agg0.operationMode}" list="%{enumRedOperationMode}" listKey="key"
									listValue="value"/></td>
								<td class="list" colspan="6">&nbsp;</td>
							</tr>
						</table>
						</fieldset>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td valign="top" width="100%">
						<fieldset>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<th align="left" width="80px"><s:text name="hiveAp.wlanIf" /></th>
								<th align="left" width="40px"><s:text name="hiveAp.if.radioMode" /></th>
								<th align="left" width="200px"><s:text name="hiveAp.if.radioProfile" /></th>
								<th align="left" width="80px"><s:text name="hiveAp.if.adminState" /></th>
								<th align="left" width="105px"><s:text name="hiveAp.if.operationMode" /></th>
								<th align="left" width="85px"><s:text name="hiveAp.if.channel" /></th>
								<th align="left" nowrap="nowrap"><s:text name="hiveAp.if.power" /></th>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td class="list"><s:text name="hiveAp.if.wifi0"/></td>
								<td id="wifi0RadioMode" class="list"><s:property value="%{wifi0RadioModeLabel}" /></td>
								<td class="list"><s:select name="wifi0RadioProfile" list="%{wifi0RadioProfiles}" listKey="id"
									listValue="value" cssStyle="width: 150px;" onchange="addRadioProfile(this)"/></td>
								<td class="list"><s:select name="dataSource.wifi0.adminState"
									value="%{dataSource.wifi0.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value" onchange="adminStatusChanged(this, 'wifi0');"/></td>
								<td class="list"><s:select name="dataSource.wifi0.operationMode"
									value="%{dataSource.wifi0.operationMode}" list="%{enumWifiOperationMode}" listKey="key"
									listValue="value" onchange="operationModeChanged(this, 'wifi0');" /></td>
								<td class="list"><s:select name="dataSource.wifi0.channel" value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}" listKey="key"
									listValue="value"/></td>
								<td class="list"><s:select name="dataSource.wifi0.power"
									value="%{dataSource.wifi0.power}" list="%{enumPowerType}" listKey="key"
									listValue="value"/></td>
							</tr>
							<tr id="wifi1Row" style="display: <s:property value="wifi1StuffStyle"/>">
								<td class="list"><s:text name="hiveAp.if.wifi1"/></td>
								<td id="wifi1RadioMode" class="list"><s:property value="%{wifi1RadioModeLabel}" /></td>
								<td class="list"><s:select name="wifi1RadioProfile" list="%{wifi1RadioProfiles}" listKey="id"
									listValue="value" cssStyle="width: 150px;" onchange="addRadioProfile(this)"/></td>
								<td class="list"><s:select name="dataSource.wifi1.adminState"
									value="%{dataSource.wifi1.adminState}" list="%{enumAdminStateType}" listKey="key"
									listValue="value" onchange="adminStatusChanged(this, 'wifi1');"/></td>
								<td class="list"><s:select name="dataSource.wifi1.operationMode"
									value="%{dataSource.wifi1.operationMode}" list="%{enumWifiOperationMode}" listKey="key"
									listValue="value" onchange="operationModeChanged(this, 'wifi1');" /></td>
								<td class="list"><s:select name="dataSource.wifi1.channel" value="%{dataSource.wifi1.channel}" list="%{wifi1Channel}" listKey="key"
									listValue="value"/></td>
								<td class="list"><s:select name="dataSource.wifi1.power"
									value="%{dataSource.wifi1.power}" list="%{enumPowerType}" listKey="key"
									listValue="value"/></td>
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
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="350px"><s:text name="hiveAp.distributedPriority"/></td>
									<td><s:select list="%{enumDistributedPriority}"
										listKey="key" listValue="value" name="dataSource.distributedPriority" value="%{dataSource.distributedPriority}"
										cssStyle="width:120px;" /></td>
									</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td valign="top" width="100%">
						<fieldset><legend><s:text name="hiveAp.classification.tag"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td class="labelT1" width="130px"><s:text name="hiveAp.classification.tag1"/></td>
								<td><s:textfield name="dataSource.classificationTag1" size="64" maxlength="%{classificationTag1Length}"/>
								<s:text name="hiveAp.classification.tag.desc"/></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="hiveAp.classification.tag2"/></td>
								<td><s:textfield name="dataSource.classificationTag2" size="64" maxlength="%{classificationTag2Length}"/>
								<s:text name="hiveAp.classification.tag.desc"/>
								</td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="hiveAp.classification.tag3"/></td>
								<td><s:textfield name="dataSource.classificationTag3" size="64" maxlength="%{classificationTag3Length}"/>
								<s:text name="hiveAp.classification.tag.desc"/>
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
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr style="display: <s:property value='mucDisplay'/>">
								<td width="10px"><s:checkbox
									name="dataSource.manageUponContact"
									value="%{dataSource.manageUponContact}"/></td>
								<td style="padding-left:2px"><s:text name="hiveAp.manageUponContact"/></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</div>
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td>
						<fieldset><legend><s:text name="hiveAp.superUser.tag"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" class="embedded">
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
										name="hiveAp.currentUser" /></td>
									<td><s:textfield name="dataSource.adminUser" size="24" maxlength="%{adminUserLength}"
										onkeypress="return hm.util.keyPressPermit(event,'name');"/> <s:text name="hiveAp.currentUserRange"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
										name="hiveAp.newUser" /></td>
									<td><s:textfield name="dataSource.cfgAdminUser" size="24" maxlength="%{cfgAdminUserLength}"
										onkeypress="return hm.util.keyPressPermit(event,'username');"/> <s:text name="hiveAp.currentUserRange"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.currentPassword" /></td>
									<td><s:password name="dataSource.adminPassword" size="24" maxlength="8"
									 onkeypress="return hm.util.keyPressPermit(event,'name');" id="currentPassword" showPassword="true"/>
									 <s:text name="hiveAp.currentPasswordRange"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.newPassword" /></td>
									<td><s:password name="dataSource.cfgPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgAdminPassword" showPassword="true"/>
									 <s:textfield name="dataSource.cfgPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgAdminPassword_text" disabled="true" cssStyle="display: none;"/>
									 <s:text name="hiveAp.currentPasswordRange"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.confirmPassword" /></td>
									<td><s:password name="confirmPassword" size="24" maxlength="8"
									 onkeypress="return hm.util.keyPressPermit(event,'name');" id="confirmPassword" value="%{dataSource.adminPassword}" showPassword="true"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.newConfirmPassword" /></td>
									<td><s:password name="confirmNewPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfCfgAdminPassword" value="%{dataSource.cfgPassword}" showPassword="true"/>
									 <s:textfield name="confirmNewPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfCfgAdminPassword_text" value="%{dataSource.cfgPassword}" disabled="true" cssStyle="display: none;"/>
									 </td>
									</tr>
									<tr>
									<td></td>
									<td>
									 <s:checkbox id="chkToggleDisplay" name="ignore" value="true" disabled="%{writeDisable4Struts}"
									 	onclick="hm.util.toggleObscurePassword(this.checked,['cfgAdminPassword','cfCfgAdminPassword'],['cfgAdminPassword_text','cfCfgAdminPassword_text']);" />
									 <s:text name="admin.user.obscurePassword" />
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
					<tr>
						<td>
						<fieldset><legend><s:text name="hiveAp.readOnlyUser.tag"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" class="embedded">
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
										name="hiveAp.currentUser" /></td>
									<td><s:textfield name="dataSource.readOnlyUser" size="24" maxlength="%{readOnlyUserLength}"
										onkeypress="return hm.util.keyPressPermit(event,'name');"/> <s:text name="hiveAp.currentUserRange"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
										name="hiveAp.newUser" /></td>
									<td><s:textfield name="dataSource.cfgReadOnlyUser" size="24" maxlength="%{cfgReadOnlyUserLength}"
										onkeypress="return hm.util.keyPressPermit(event,'username');"/> <s:text name="hiveAp.currentUserRange"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.currentPassword" /></td>
									<td><s:password name="dataSource.readOnlyPassword" size="24" maxlength="8"
									 onkeypress="return hm.util.keyPressPermit(event,'name');" id="readOnlyPassword" showPassword="true"/>
									 <s:text name="hiveAp.currentPasswordRange"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.newPassword" /></td>
									<td><s:password name="dataSource.cfgReadOnlyPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgReadOnlyPassword" showPassword="true"/>
									 <s:textfield name="dataSource.cfgReadOnlyPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgReadOnlyPassword_text" disabled="true" cssStyle="display: none;"/>
									 <s:text name="hiveAp.currentPasswordRange"/></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<!-- <td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.confirmPassword" /></td>
									<td><s:password name="confirmReadOnlyPassword" size="24" maxlength="8"
									 onkeypress="return hm.util.keyPressPermit(event,'name');" id="confirmReadOnlyPassword" value="%{dataSource.readOnlyPassword}" showPassword="true"/></td>
									</tr>
									</table>
								</td> -->
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td class="labelT1" width="140px"><s:text
									name="hiveAp.newConfirmPassword" /></td>
									<td><s:password name="confirmNewReadOnlyPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="confirmCfgReadOnlyPassword" value="%{dataSource.cfgReadOnlyPassword}" showPassword="true"/>
									 <s:textfield name="confirmNewReadOnlyPassword" size="24" maxlength="32"
									 onkeypress="return hm.util.keyPressPermit(event,'password');" id="confirmCfgReadOnlyPassword_text" value="%{dataSource.cfgReadOnlyPassword}" disabled="true" cssStyle="display: none"/>
									 </td>
									</tr>
									<tr>
									<td></td>
									<td>
									 <s:checkbox id="chkToggleDisplay_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
									 onclick="hm.util.toggleObscurePassword(this.checked,['cfgReadOnlyPassword','confirmCfgReadOnlyPassword'],['cfgReadOnlyPassword_text','confirmCfgReadOnlyPassword_text']);" />
									 <s:text name="admin.user.obscurePassword" />
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
					<tr>
						<td>
						<fieldset><legend><s:text name="hiveAp.capwap.tag"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" class="embedded">
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
									<tr>
									<td><s:checkbox name="changePassPhrase" onclick="clickDtlsBox();" disabled="%{writeDisable4Struts}"
										value="%{changePassPhrase}" id="changePassPhrase"/></td>
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
									<td><s:password name="dataSource.passPhrase" size="24" id="newDtls"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										maxlength="32" showPassword="true" disabled="%{passPhraseDisabled}"/>
										<s:textfield name="dataSource.passPhrase" size="24" id="newDtls_text"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										maxlength="32" cssStyle="display: none;" disabled="true"/>
										<s:text name="hiveAp.dtls.passPhraseRange"/></td>
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
									<td><s:password value="%{dataSource.passPhrase}" size="24" id="confirmDtls"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										maxlength="32" showPassword="true" disabled="%{passPhraseDisabled}"/>
										<s:textfield value="%{dataSource.passPhrase}" size="24" id="confirmDtls_text"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										maxlength="32" cssStyle="display: none;" disabled="true"/>
									</td>
									</tr>
									<tr>
									<td></td>
									<td>
									 <s:checkbox id="chkToggleDisplay_2" name="ignore" value="true" disabled="%{passPhraseDisabled}"
									 onclick="hm.util.toggleObscurePassword(this.checked,['newDtls','confirmDtls'],['newDtls_text','confirmDtls_text']);" />
									 <s:text name="admin.user.obscurePassword" />
									</td>
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
									<td width="260px">
										<ah:createOrSelect divId="errorDisplay" list="capwapIps" typeString="CapwapIp"
											selectIdName="capwapSelect" inputValueName="inputIpValue"
											swidth="152px"/>
									</td>
									</tr>
									<tr>
									<td class="labelT1"><s:text
										name="hiveAp.capwap.server.backup" /></td>
									<td>
										<ah:createOrSelect divId="errorBackupDisplay" list="capwapIps" typeString="CapwapBackupIp"
											selectIdName="capwapBackupSelect" inputValueName="inputBackupIpValue"
											swidth="152px"/>
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
				</div>
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td>
						<fieldset><legend><s:text name="hiveAp.cfg.l2Route"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding-left:10px;padding-right:0px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td width="120px" nowrap="nowrap"><s:text name="hiveAp.cfg.dynamicRoute.metric.interval"/></td>
									<td width="200px" nowrap="nowrap"><s:textfield name="dataSource.metricInteval" size="5" maxlength="5"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"/> <s:text name="hiveAp.cfg.dynamicRoute.metric.interval.range"/></td>
									<td width="100px" style="padding-left:20px;"><s:text name="hiveAp.cfg.dynamicRoute.metric.type"/></td>
									<td width="100px"><s:select list="%{enumMetricType}" listKey="key" listValue="value" value="%{dataSource.metric}" name="dataSource.metric"/></td>
								</tr>
							</table></td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
						</tr>
						<tr>
							<td style="padding: 4px 0 0 0;">
							<table cellspacing="0" cellpadding="0" border="0" width="100%" id="dynamicTable">
								<tr id="newButton">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New"
												class="button" onClick="showCreateSection('dynamic');" <s:property value="writeDisabled" />></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button"
												onClick="submitAction('removeDynamicRoutes');" <s:property value="writeDisabled" />></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButton">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addDynamicRoute');"></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button" onClick="submitAction('removeDynamicRoutes');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection('dynamic');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0;"><input
										type="checkbox" id="checkAll"
										onClick="toggleCheckAllDynamicRoutes(this);"></th>
									<th align="left"><s:text name="hiveAp.cfg.dynamicRoute.neighborMac" /></th>
									<th align="left"><s:text name="hiveAp.cfg.dynamicRoute.metric.minimum" /></th>
									<th align="left"><s:text name="hiveAp.cfg.dynamicRoute.metric.maximum" /></th>
								</tr>
								<tr style="display:none;" id="createSection">
									<td class="listHead">&nbsp;</td>
									<td class="listHead" valign="top" nowrap="nowrap"><s:textfield name="neighborMac" size="16" maxlength="12" onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
												<br><s:text name="config.macOrOui.addressRange"/></td>
									<td class="listHead" valign="top"><s:textfield name="routeMinimun" size="16" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
												<br><s:text name="hiveAp.cfg.dynamicRoute.metric.range"/></td>
									<td class="listHead" valign="top"><s:textfield name="routeMaximun" size="16" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
												<br><s:text name="hiveAp.cfg.dynamicRoute.metric.range"/></td>
								</tr>
								<s:iterator value="%{dataSource.dynamicRoutes}" status="status">
									<tr>
										<td class="listCheck"><s:checkbox name="dynamicRouteIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list"><s:property
											value="neighborMac" /></td>
										<td class="list"><s:property
											value="routeMinimun" /></td>
										<td class="list"><s:property
											value="routeMaximun" /></td>
									</tr>
								</s:iterator>
								<s:if test="%{gridCount > 0}">
									<s:generator separator="," val="%{' '}" count="%{gridCount}">
										<s:iterator>
											<tr>
												<td class="list" colspan="6">&nbsp;</td>
											</tr>
										</s:iterator>
									</s:generator>
								</s:if>
							</table></td>
						</tr>
						<tr>
							<td height="30"></td>
						</tr>
						<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
						</tr>
						<tr>
							<td style="padding: 4px 0 0 0;">
							<table cellspacing="0" cellpadding="0" border="0" width="100%" id="staticTable">
								<tr id="newButtonStatic">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New"
												class="button" onClick="showCreateSection('static');" <s:property value="writeDisabled" />></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button"
												onClick="submitAction('removeStaticRoutes');" <s:property value="writeDisabled" />></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButtonStatic">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addStaticRoute');"></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button" onClick="submitAction('removeStaticRoutes');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection('static');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSectionStatic">
									<th align="left" style="padding-left: 0;"><input
										type="checkbox" id="checkAllStatic"
										onClick="toggleCheckAllStaticRoutes(this);"></th>
									<th align="left"><s:text name="hiveAp.cfg.staticRoute.destination" /></th>
									<th align="left"><s:text name="hiveAp.cfg.staticRoute.interface" /></th>
									<th align="left"><s:text name="hiveAp.cfg.staticRoute.nextHop" /></th>
								</tr>
								<tr style="display:none;" id="createSectionStatic">
									<td class="listHead">&nbsp;</td>
									<td class="listHead" valign="top"><s:textfield name="destinationMac" size="16" maxlength="12" onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
										<br><s:text name="config.macOrOui.addressRange"/></td>
									<td class="listHead" valign="top"><s:select name="interfaceType"
												list="%{enumStaticRouteIfType}" listKey="key" listValue="value" cssStyle="width: 110px;"/></td>
									<td class="listHead" valign="top"><s:textfield name="nextHopMac" size="16" maxlength="12" onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
										<br><s:text name="config.macOrOui.addressRange"/></td>
								</tr>
								<s:iterator value="%{dataSource.staticRoutes}" status="status">
									<tr>
										<td class="listCheck"><s:checkbox name="staticRouteIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list"><s:property
											value="destinationMac" /></td>
										<td class="list"><s:select name="interfaceTypes"
											value="%{interfaceType}" list="%{enumStaticRouteIfType}" listKey="key"
											listValue="value"/></td>
										<td class="list"><s:property
											value="nextHopMac" /></td>
									</tr>
								</s:iterator>
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
						<td height="10"></td>
					</tr>
				</table>
				</div>
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td class="labelT1" width="150px" style="padding-right:40px;"><s:text name="hiveAp.radiusServerLabel"/></td>
						<td width="160px"><s:select list="%{radiusServers}"
								listKey="id" listValue="value" name="radiusServer" cssStyle="width: 150px;" />
						</td>
						<td style="padding:0 2px 0 2px;">
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newRadius')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editRadius')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
						</td>
					</tr>
					<tr id="vpnRuleTr" style="display: <s:property value="vpnRuleStyle"/>">
						<td class="labelT1" style="padding-right:40px;"><s:text name="hiveAp.server.vpn.role" /></td>
						<td width="160px"><s:select list="%{enumVPNMarkType}"
								listKey="key" listValue="value" name="dataSource.vpnMark" cssStyle="width: 150px;" />
						</td>
					</tr>
					<%--<tr>
						<td class="labelT1" width="150px" style="padding-right:40px;"><s:text name="hiveAp.vpnServerLabel"/></td>
						<td width="160px"><s:select list="%{vpnServices}"
								listKey="id" listValue="value" name="vpnServer" cssStyle="width: 150px;" />
						</td>
						<td style="padding:0 2px 0 2px;">
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newVpn')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editVpn')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
						</td>
					</tr>--%>
					<%--<tr>
						<td class="labelT1" width="150px" style="padding-right:40px;"><s:text name="hiveAp.discoveryProtocolLabel"/></td>
						<td width="160px"><s:select list="%{lldpCdps}"
								listKey="id" listValue="value" name="lldpCdp" cssStyle="width: 150px;" />
						</td>
						<td style="padding:0 2px 0 2px;">
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newLldpCdp')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editLldpCdp')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
						</td>
					</tr>--%>
					<tr>
						<td height="10"></td>
					</tr>
					<tr><td colspan="10">
						<fieldset><legend><s:text name="hiveAp.dhcpServerLabel"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
							<tr>
								<td height="10"></td>
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
									<%--<tr>
										<td height="5"></td>
									</tr>
									<tr>
										<td style="padding:0 5px 0 25px;">
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/new_disable.png" />"
												width="16" height="16" alt="New" title="New" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:submitAction('newDhcpServer')"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
											</s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/modify_disable.png" />"
												width="16" height="16" alt="Modify" title="Modify" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:submitAction('editDhcpServer')"><img class="dinl"
												src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else>
										</td>
									</tr>--%>
									</table>
								</td>
							</tr>
						</table>
						</fieldset>
					</td></tr>
				</table>
				</div>
				<div>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td>
						<fieldset><legend><s:text name="hiveAp.cfg.l3Roaming"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%"
							class="embedded">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
							<td style="padding-right:10px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="150px" style="padding-right:32px;"><s:text name="hiveAp.cfg.l3Roaming.threshold"/></td>
									<td><s:select name="dataSource.tunnelThreshold"
									value="%{dataSource.tunnelThreshold}" list="%{enumTunnelThresholdType}" listKey="key"
									listValue="value"  cssStyle="width: 80px;"/></td>
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
									<fieldset><legend><s:text name="hiveAp.cfg.l3Roaming.included"/></legend>
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
									<fieldset><legend><s:text name="hiveAp.cfg.l3Roaming.excluded"/></legend>
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
						</fieldset>
						</td>
					</tr>
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr><td><label id="ipTrackLabel"></label></td></tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<%--<td width="350px" valign="top">
								<fieldset><legend><s:text name="hiveAp.ipTrack.label"/></legend>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td style="padding-left: 10px;">
											<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<s:push value="%{ipTrackOptions}">
													<td><tiles:insertDefinition
														name="optionsTransfer" /></td>
												</s:push>
											</tr>
											<%--<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td style="padding:0 5px 0 25px;">
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newIpTrack')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editIpTrack')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
											</table>
										</td>
									</tr>
								</table>
								</fieldset>
								</td>
								<td width="5px"></td>--%>
								<td height="100%" valign="top">
								<fieldset><legend><s:text name="hiveAp.ssidAllocation.label"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td height="10" colspan="10"></td>
									</tr>
									<tr>
										<td valign="top">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<th><input type="checkbox" id="checkAll_0"
														onClick="toggleCheckAllWifiSsids(this,'ssid0Indices');"></th>
													<th nowrap="nowrap"><s:text name="hiveAp.ssidAllocation.wifi0.label"/></th>
												</tr>
												<tr>
													<td colspan="10" id="wifi0ssidTable">
													<table cellspacing="0" cellpadding="0" border="0">
													<s:iterator value="%{wifi0Ssids}" status="status">
														<tr valign="top">
															<td style="padding: 5px 3px;"><s:checkbox name="ssid0Indices"
																onclick="setCheckBoxSelected('checkAll_0', 'ssid0Indices');"
																fieldValue="%{ssid}" value="%{checked}" /></td>
															<td class="labelT1"><s:label value="%{ssidName}" title="%{tooltip}"/></td>
														</tr>
													</s:iterator>
													</table>
													</td>
												</tr>
											</table>
										</td>
										<td nowrap="nowrap">&nbsp;&nbsp;</td>
										<td valign="top">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<th><input type="checkbox" id="checkAll_1"
														onClick="toggleCheckAllWifiSsids(this,'ssid1Indices');"></th>
													<th nowrap="nowrap"><s:text name="hiveAp.ssidAllocation.wifi1.label"/></th>
												</tr>
												<tr>
													<td colspan="10" id="wifi1ssidTable">
													<table cellspacing="0" cellpadding="0" border="0">
													<s:iterator value="%{wifi1Ssids}" status="status">
														<tr valign="top">
															<td style="padding: 5px 3px;"><s:checkbox name="ssid1Indices"
																onclick="setCheckBoxSelected('checkAll_1', 'ssid1Indices');"
																fieldValue="%{ssid}" value="%{checked}" /></td>
															<td class="labelT1"><s:label value="%{ssidName}" title="%{tooltip}"/></td>
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
								<td></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="190px">
									<s:checkbox id="overrideVlan" name="overrideVlan" value="%{nativeVlanOverride}" onclick="changeNativeVlanInput(this.checked);"></s:checkbox>
									<s:text name="hiveAp.nativeVlan.override" />
								</td>
								<td><s:textfield name="dataSource.nativeVlan" size="24" disabled="%{nativeVlanDisabled}"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="4" title="%{vlanRange}"/></td>
							</tr>
							<tr>
							<td class="labelT1" width="190px"><s:text
								name="hiveAp.scheduler.label" /></td>
							<td width="160px"><s:select list="%{schedulers}"
								listKey="id" listValue="value" name="scheduler"
								cssStyle="width: 150px;" />
							</td>
							<td style="padding:0 2px 0 2px;">
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('newScheduler')"><img class="dinl"
									src="<s:url value="/images/new.png" />"
									width="16" height="16" alt="New" title="New" /></a>
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('editScheduler')"><img class="dinl"
									src="<s:url value="/images/modify.png" />"
									width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else>
							</td>
							</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="5"></td>
					</tr>
				</table>
				</div>
			</div>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>