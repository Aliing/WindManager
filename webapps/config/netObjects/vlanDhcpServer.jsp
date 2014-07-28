<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.DhcpServerOptionsCustom"%>

<script>
var formName = 'vlanDhcpServer';
var CUSTOM_TYPE_INT = <%=DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER%>;
var CUSTOM_TYPE_IP = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_IP%>;
var CUSTOM_TYPE_STR = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING%>;
var CUSTOM_TYPE_HEX = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX%>;
var advanceEnabled;

var displayErrorObj;
var displayErrorObjCus;

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_profileName").disabled) {
		document.getElementById(formName + "_dataSource_profileName").focus();
	}
	advanceEnabled = <s:property value="%{advanceShowing}"/>;

    displayErrorObj = document.getElementById("checkAllPool");
    displayErrorObjCus = document.getElementById("checkAllCustom");
	<s:if test="%{jsonMode}">
	top.changeIFrameDialog(860, 550);
	</s:if>
	loadWaterMark();
}

function showServerContent(){
	showHideContent("serverOption","");
}

function showCustomContent(){
	showHideContent("customOption","");
}

function showAdvContent(){
	showHideContent("advOption","");
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>') {
			showProcessing();
		}
		Get(formName + "_dataSource_serverOptionDisplayStyle").value = Get("serverOption").style.display;
		Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
		Get(formName + "_dataSource_advancedDisplayStyle").value = Get("advOption").style.display;
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function getStartIpAddressValue(startIpAddress, netmask){
	var intAddress = hm.util.getIpAddressValue(startIpAddress);
	var maskValue = hm.util.getIpAddressValue(netmask);
	var s = intAddress & maskValue;
	return s < 0 ? Math.pow(2,32)+s : s;
}

function getEndIpAddressValue(startIpAddress, netmask){
	var intAddress = hm.util.getIpAddressValue(startIpAddress);
	var maskValue = hm.util.getIpAddressValue(netmask);
	var s = intAddress & maskValue;
	var ipCount = Math.pow(2,32) - maskValue;
	
	return s < 0 ? Math.pow(2,32)+s + ipCount -1 : s + ipCount - 1;
}

function validate(operation) {
	if ('<%=Navigation.L2_FEATURE_VLAN_DHCP_SERVER%>' == operation || operation == 'cancel<s:property value="lstForward"/>') {
		if (advanceEnabled) {
			document.getElementById(formName + "_customNumber").value="0";
		}
		document.getElementById(formName + "_dataSource_interVlan").value="1";
		return true;
	}

	if (operation == 'create<s:property value="lstForward"/>' || "create" == operation) {
		var name = document.getElementById(formName + "_dataSource_profileName");
		var message = hm.util.validateName(name.value, '<s:text name="config.ipFilter.name" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(name, message);
	       	name.focus();
	       	return false;
	   	}
	}

	if (operation == 'addIPAddress') {
		var rowcount = document.getElementsByName('poolIndices');
		if(rowcount.length >= 8) {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="error.entryLimit"><s:param><s:text name="config.network.object.dhcp.server.ip.pools" /></s:param><s:param value="8" /></s:text>');
        	return false;
		}
		if (!checkInterfaceInfo()) {
			return false;
		}
		var startIp = document.getElementById(formName + "_startIp");
		var endIp = document.getElementById(formName + "_endIp");
    	if(!checkIpAddressSameInter(displayErrorObj, startIp, '<s:text name="config.tunnelSetting.startIpAddress" />')
			|| !checkIpAddressSameInter(displayErrorObj, endIp, '<s:text name="config.tunnelSetting.endIpAddress" />')) {
			return false;
		}
		if (!hm.util.compareIpAddress(startIp.value, endIp.value)) {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="error.notLargerThan"><s:param><s:text name="config.tunnelSetting.startIpAddress" /></s:param><s:param><s:text name="config.tunnelSetting.endIpAddress" /></s:param></s:text>');
			startIp.focus();
			return false;
		}
		
		var dhcpMgt = document.getElementById(formName + "_dataSource_dhcpMgt");
		if (dhcpMgt.value > 0) {
			var interfaceIp = document.getElementById(formName + "_dataSource_interfaceIp");
			var netmask = document.getElementById(formName + "_dataSource_interfaceNet");
			if(hm.util.getIpAddressValue(endIp.value) == getEndIpAddressValue(interfaceIp.value, netmask.value)) {
				hm.util.reportFieldError(displayErrorObj, '<s:text name="error.config.network.dhcp.ip.pool.contain.broadcast.ip"><s:param>IP pool</s:param></s:text>');
				endIp.focus();
				return false;
			}
		} else if(startIp.value.split(".")[3] == 0) {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="error.config.network.dhcp.ip.pool.contain.broadcast.ip"><s:param>IP pool</s:param></s:text>');
			start.focus();
			return false;
		}
    }

    if (operation == 'removeIPAddress' || operation == 'removeIPAddressNone') {
		var cbs = document.getElementsByName('poolIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(displayErrorObj, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.network.object.dhcp.server.ip.pools" /></s:param></s:text>');
			return false;
		}
	}

	if (operation == 'addCustom') {
 		if(!checkCustomNumber()) {
       		return false;
 		}
		var hideInteger = document.getElementById("hideInteger");
		var hideIp = document.getElementById("hideIp");
		var hideString = document.getElementById("hideString");
		var hideHex = document.getElementById("hideHex");

		var customNumber = document.getElementById(formName + "_customNumber").value;
		if (customNumber == 226) {
			if (hideIp.style.display == "none") {
				hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.hivemanager.ip" /></s:param></s:text>');
				return false;
			} else {
				var ipValue = document.getElementById(formName + "_ipValue");
				if (!checkIpAddressSameInter(displayErrorObjCus, ipValue, '<s:text name="config.network.object.dhcp.server.hivemanager.ip" />')) {
					return false;
				}
			}
		}
		if (customNumber == 225 && hideString.style.display == "none") {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.hivemanager.name" /></s:param></s:text>');
			return false;
		}

		if(hideInteger.style.display == "") {
	 		var integerValue = document.getElementById(formName + "_integerValue");
	 		if(!checkNumber(displayErrorObjCus, integerValue, '<s:text name="config.network.object.dhcp.server.options.custom.value" />', 0, 2147483647, null)) {
        		return false;
	 		}
	 	}
	 	if(hideIp.style.display == "") {
	 		var ipValue = document.getElementById(formName + "_ipValue");
			if(!checkIpAddress(displayErrorObjCus, ipValue, '<s:text name="config.network.object.dhcp.server.options.custom.value" />')) {
				return false;
			}
	 	}
	 	if(hideString.style.display == "") {
	 		var strValue = document.getElementById(formName + "_strValue");
	 		if(strValue.value.length == 0){
				hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
	 			strValue.focus();
	 			return false;
			}
	 		var message = hm.util.validateString(strValue.value, '<s:text name="config.network.object.dhcp.server.options.custom.value" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(strValue, message);
	    		strValue.focus();
	        	return false;
	    	}
	 	}
	 	if(hideHex.style.display == "") {
	 		var hexValue = document.getElementById(formName + "_hexValue");
	 		if(YAHOO.util.Dom.hasClass(hexValue, hintClassName)) {
	 			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
	 			hexValue.focus();
	 			return false;
	 		} else {
	 			if(hexValue.value.length == 0){
					hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
		 			hexValue.focus();
		 			return false;
				}
	 			if(hexValue.value.length > 254){
					hm.util.reportFieldError(displayErrorObjCus,
						'<s:text name="config.network.object.dhcp.server.options.custom.outOfRange"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
					hexValue.focus();
					return false;
				}
	 		}
	 		
			var message = hm.util.validateHexRange(hexValue.value, '<s:text name="config.network.object.dhcp.server.options.custom.value" />');
      	  	if (message != null) {
	            hm.util.reportFieldError(displayErrorObjCus, message);
	            hexValue.focus();
	            return false;
      	  	}
	 	}
    }

    if (operation == 'removeCustom' || operation == 'removeCustomNone') {
		var cbs = document.getElementsByName('customIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.network.object.dhcp.server.options.custom" /></s:param></s:text>');
			return false;
		}
	}
	//if (operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'){
	if ((operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>')
		|| (operation == 'update' || operation == 'create')){
		if (!checkInterfaceInfo()) {
			return false;
		}

	 	if (advanceEnabled) {
	 		var poolIndices = document.getElementsByName('poolIndices');
			if(poolIndices.length == 0) {
				hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.ip.pools" /></s:param></s:text>');
	       		displayErrorObj.focus();
	       		return false;
			}
	 		var defaultGateway = document.getElementById(formName + "_dataSource_defaultGateway");
	 		if(defaultGateway.value.length > 0 && !checkIpAddressSameInter(defaultGateway, defaultGateway, '<s:text name="admin.interface.defaultGateway" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
			var dhcpMgt = document.getElementById(formName + "_dataSource_dhcpMgt");
			if (dhcpMgt.value > 0) {
				var interfaceIp = document.getElementById(formName + "_dataSource_interfaceIp");
				var netmask = document.getElementById(formName + "_dataSource_interfaceNet");
				if(hm.util.getIpAddressValue(defaultGateway.value) == getStartIpAddressValue(interfaceIp.value, netmask.value) || hm.util.getIpAddressValue(defaultGateway.value) == getEndIpAddressValue(interfaceIp.value, netmask.value)) {
					hm.util.reportFieldError(defaultGateway, '<s:text name="error.config.network.dhcp.broadcast.ip.address"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
					defaultGateway.focus();
					showServerContent();
					return false;
				}
			} else if(defaultGateway.value.split(".")[3] == 0) {
				hm.util.reportFieldError(defaultGateway, '<s:text name="error.config.network.dhcp.broadcast.ip.address"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
				defaultGateway.focus();
				showServerContent();
				return false;
			}
	 		
	 		var leaseTime = document.getElementById(formName + "_dataSource_leaseTime");
	 		if(leaseTime.value.length > 0 && !checkNumber(leaseTime, leaseTime, '<s:text name="config.cwp.head.leaseTime" />', 60, 86400000, showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var dnsServer1 = document.getElementById(formName + "_dataSource_dnsServer1");
	 		if(dnsServer1.value.length > 0 && !checkIpAddressSameInter(dnsServer1, dnsServer1, '<s:text name="config.network.object.dhcp.server.dns1" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var dhcpNetmask = document.getElementById(formName + "_dataSource_dhcpNetmask");
	 		if(dhcpNetmask.value.length > 0 && !checkNetmask(dhcpNetmask, '<s:text name="config.ipAddress.netmask" />', showServerContent)) {
	 			showServerContent();
	 			return false;
			}
			var dnsServer2 = document.getElementById(formName + "_dataSource_dnsServer2");
	 		if(dnsServer2.value.length > 0 && !checkIpAddressSameInter(dnsServer2, dnsServer2, '<s:text name="config.network.object.dhcp.server.dns2" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
			var domainName = document.getElementById(formName + "_dataSource_domainName");
	 		if(domainName.value.length > 0) {
				var message = hm.util.validateName(domainName.value, '<s:text name="config.mgmtservice.domain.name" />');
			   	if (message != null) {
			   		hm.util.reportFieldError(domainName, message);
			   		showServerContent();
			       	domainName.focus();
			       	return false;
			   	}
			}

	 		var mtu = document.getElementById(formName + "_dataSource_mtu");
	 		if(mtu.value.length > 0 && !checkNumber(mtu, mtu, '<s:text name="config.network.object.dhcp.server.mtu" />', 68, 8192, showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var dnsServer3 = document.getElementById(formName + "_dataSource_dnsServer3");
	 		if(dnsServer3.value.length > 0 && !checkIpAddressSameInter(dnsServer3, dnsServer3, '<s:text name="config.network.object.dhcp.server.dns3" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
			if (dnsServer1.value.length == 0 && (dnsServer2.value.length > 0 || dnsServer3.value.length > 0)) {
				hm.util.reportFieldError(dnsServer1, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.dns1" /></s:param></s:text>');
				showServerContent();
	 			dnsServer1.focus();
	 			return false;
			}
			if (dnsServer2.value.length == 0 && dnsServer3.value.length > 0) {
				hm.util.reportFieldError(dnsServer2, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.dns2" /></s:param></s:text>');
				showServerContent();
	 			dnsServer2.focus();
	 			return false;
			}
			var pop3 = document.getElementById(formName + "_dataSource_pop3");
	 		if(pop3.value.length > 0 && !checkIpAddressSameInter(pop3, pop3, '<s:text name="config.network.object.dhcp.server.pop3" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var ntpServer1 = document.getElementById(formName + "_dataSource_ntpServer1");
	 		if(ntpServer1.value.length > 0 && !checkIpAddressSameInter(ntpServer1, ntpServer1, '<s:text name="config.network.object.dhcp.server.ntp1" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var ntpServer2 = document.getElementById(formName + "_dataSource_ntpServer2");
	 		if(ntpServer2.value.length > 0 && !checkIpAddressSameInter(ntpServer2, ntpServer2, '<s:text name="config.network.object.dhcp.server.ntp2" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
			if (ntpServer1.value.length == 0 && ntpServer2.value.length > 0) {
				hm.util.reportFieldError(ntpServer1, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.ntp1" /></s:param></s:text>');
				showServerContent();
	 			ntpServer1.focus();
	 			return false;
			}
	 		var smtp = document.getElementById(formName + "_dataSource_smtp");
	 		if(smtp.value.length > 0 && !checkIpAddressSameInter(smtp, smtp, '<s:text name="config.network.object.dhcp.server.smtp" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var wins1 = document.getElementById(formName + "_dataSource_wins1");
	 		if(wins1.value.length > 0 && !checkIpAddressSameInter(wins1, wins1, '<s:text name="config.network.object.dhcp.server.wins1" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var logsrv = document.getElementById(formName + "_dataSource_logsrv");
	 		if(logsrv.value.length > 0 && !checkIpAddressSameInter(logsrv, logsrv, '<s:text name="config.network.object.dhcp.server.logsrv" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
	 		var wins2 = document.getElementById(formName + "_dataSource_wins2");
	 		if(wins2.value.length > 0 && !checkIpAddressSameInter(wins2, wins2, '<s:text name="config.network.object.dhcp.server.wins2" />', showServerContent)) {
	 			showServerContent();
				return false;
			}
			var interfaceNet = document.getElementById(formName + "_dataSource_interfaceNet");
			var interfaceMgt = document.getElementById(formName + "_dataSource_dhcpMgt").value;
		 	if(dhcpNetmask.value.length > 0 && interfaceNet.value != dhcpNetmask.value && interfaceMgt > 0){
		 		hm.util.reportFieldError(interfaceNet,'<s:text name="error.notMatch.netmask" />');
		 		showServerContent();
		 		return false;
		 	}
	 	} else {
	 		var ipHelper1 = document.getElementById(formName + "_dataSource_ipHelper1");
	 		if(!checkIpAddressSameInter(ipHelper1, ipHelper1, '<s:text name="config.network.object.dhcp.server.ip.helper1" />')) {
				return false;
			}
	 		var ipHelper2 = document.getElementById(formName + "_dataSource_ipHelper2");
	 		if(ipHelper2.value.length > 0 && !checkIpAddressSameInter(ipHelper2, ipHelper2, '<s:text name="config.network.object.dhcp.server.ip.helper2" />')) {
				return false;
			}
	 	}
	}

	return true;
}

function checkNumber(focus, field, title, min, max, fn){
	if (field.value.length == 0) {
        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        field.focus();
        return false;
    } else if(field.value.length > 1 && field.value.substring(0,1) == '0') {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		field.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(field.value, title, min, max);
    if (message != null) {
        hm.util.reportFieldError(focus, message);
		if(null != fn){
			fn();
		}
        field.focus();
        return false;
    }
	return true;
}

function checkCustomNumber(){
	var customNumber = document.getElementById(formName + "_customNumber").value;
	if (customNumber.length == 0) {
		 hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
		 return false;
    } else if(customNumber.length > 1 && customNumber.substring(0,1) == '0') {
    	hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.formatInvalid"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
    	return false;
	}
	if (isNaN(customNumber)) {
		hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be a positive integer number.');
		return false;
	} else {
		for (var count = 0; count<customNumber.length; count++) {
	       var code = customNumber.charCodeAt(count);
	       if (48 > code || code > 57) {
	    	   hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be a positive integer number.');
	    	   return false;
	       }
	   }
	}
	var limitValues = [3, 6, 7, 15, 26, 42, 44, 51, 58, 59, 69, 70];
	for (var index in limitValues) {
		if (customNumber == limitValues[index]) {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.config.network.dhcp.custom.number"><s:param>'+customNumber+'</s:param></s:text>');
	        return false;
		}
	}

	if (customNumber < 2 || customNumber > 254){
		hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be between 2 and 254.');
        return false;
	}
	return true;
}

function checkIpAddress(focus, ip, title, fn) {
	if (ip.value.length == 0) {
        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        ip.focus();
        return false;
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

function checkIpAddressSameInter(focus, ip, title, fn) {
	if (checkIpAddress(focus, ip, title, fn)) {
		var dhcpMgt = document.getElementById(formName + "_dataSource_dhcpMgt");
		if (dhcpMgt.value > 0) {
			var interfaceIp = document.getElementById(formName + "_dataSource_interfaceIp");
			if(interfaceIp.value == ip.value) {
		        hm.util.reportFieldError(focus, '<s:text name="error.config.network.dhcp.interface.ip.address"><s:param>'+title+'</s:param></s:text>');
				if(null != fn){
					fn();
				}
		        ip.focus();
		        return false;
			}
		}
    } else {
		return false;
	}
	return true;
}

function checkNetmask(net, title, fn) {
	if (net.value.length == 0) {
        hm.util.reportFieldError(net, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        net.focus();
        return false;
    } else if (!hm.util.validateMask(net.value)) {
		hm.util.reportFieldError(net, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		net.focus();
		return false;
	}
	return true;
}

function checkInterfaceInfo() {
	var dhcpMgt = document.getElementById(formName + "_dataSource_dhcpMgt");
	if (dhcpMgt.value > 0) {
		var interfaceIp = document.getElementById(formName + "_dataSource_interfaceIp");
		if(!checkIpAddress(interfaceIp, interfaceIp, '<s:text name="config.network.object.dhcp.server.inter.ip" />')) {
			return false;
		}
		var interfaceNet = document.getElementById(formName + "_dataSource_interfaceNet");
		if(!checkNetmask(interfaceNet, '<s:text name="config.network.object.dhcp.server.inter.net" />')) {
			return false;
		}
		var vlan = document.getElementById(formName + "_dataSource_interVlan");
		if (!checkNumber(vlan, vlan, '<s:text name="config.vlan.vlanId" />', 1, 4094, null)) {
        	return false;
	 	}
	}
	return true;
}

function changeInterface(interface) {
	var hide = 0 == interface;
	document.getElementById("showInterfaceIp").style.display = hide ? "none" : "";
	document.getElementById("showInterfaceNet").style.display = hide ? "none" : "";
	document.getElementById("showInterfaceVlan").style.display = hide ? "none" : "";
	document.getElementById("showInterfacePing").style.display = hide ? "none" : "";
	document.getElementById(formName + "_dataSource_dhcpNetmask").disabled = hide ? "disabled" : "";
}

function dhcpServerOptionSelect(checked) {
	document.getElementById("option").style.display= "";
	document.getElementById("relay").style.display= "none";
	advanceEnabled = true;
}

function dhcpRelaySelect(checked) {
	document.getElementById("option").style.display= "none";
	document.getElementById("relay").style.display= "";
	advanceEnabled = false;
}

function changeCustomType(type) {
	var hideInteger = document.getElementById("hideInteger");
	var hideIp = document.getElementById("hideIp");
	var hideString = document.getElementById("hideString");
	var hideHex = document.getElementById("hideHex");

	switch(parseInt(type)) {
		case CUSTOM_TYPE_INT:
			hideInteger.style.display= "";
	    	hideIp.style.display="none";
			hideString.style.display="none";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_IP:
			hideInteger.style.display= "none";
	    	hideIp.style.display="";
			hideString.style.display="none";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_STR:
			hideInteger.style.display= "none";
	    	hideIp.style.display="none";
			hideString.style.display="";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_HEX:
			hideInteger.style.display= "none";
	    	hideIp.style.display="none";
			hideString.style.display="none";
			hideHex.style.display="";
			break;
		default:
			break;
	}
}

function showCreateSection(poolChecked) {
	hm.util.hide(poolChecked ? 'poolNewButton' : 'customNewButton');
	hm.util.show(poolChecked ? 'poolCreateButton' : 'customCreateButton');
	hm.util.show(poolChecked ? 'poolCreateSection' : 'customCreateSection');
	var trh = document.getElementById(poolChecked ? 'poolHeaderSection' : 'customHeaderSection');
	var trc = document.getElementById(poolChecked ? 'poolCreateSection' : 'customCreateSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection(poolChecked) {
	hm.util.hide(poolChecked ? 'poolCreateButton' : 'customCreateButton');
	hm.util.show(poolChecked ? 'poolNewButton' : 'customNewButton');
	hm.util.hide(poolChecked ? 'poolCreateSection' : 'customCreateSection');
}

function toggleCheckAll(cb, poolChecked) {
	var cbs = document.getElementsByName(poolChecked ? 'poolIndices' : 'customIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}
function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='vlanDhcpServer' includeParams='none' />" + "?jsonMode=true"
				+ "&ignore=" + new Date().getTime();
		if (operation == 'create') {
			//
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["vlanDhcpServer"]);

		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
}
<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vlanDhcpServer" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>
</script>
<div id="content"><s:form action="vlanDhcpServer"   name="vlanDhcpServer" id="vlanDhcpServer">
<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<div id="vlanTitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td  width="80%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-DHCP_Server_Relay.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.vlanDhcpServer.dialog.new.title"/></td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.vlanDhcpServer.dialog.edit.title"/></td>
							</s:else>

						</tr>
					</table>
					</td>
					<td width="20%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
</s:if>
	<s:hidden name="dataSource.serverOptionDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.customOptionDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.advancedDisplayStyle"></s:hidden>
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
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_VLAN_DHCP_SERVER%>');">
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
			<s:if test="%{jsonMode}">
				<table border="0" cellspacing="0" cellpadding="0" width="750">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="750">
			</s:else>
				<tr>
					<td style="padding-left:2px">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="105"><label><s:text name="config.ipFilter.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.profileName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="%{nameLength}" disabled="%{disabledName}"/>
									<s:text name="config.ipFilter.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.network.object.dhcp.server.inter.dhcp" /></td>
								<td><s:select name="dataSource.dhcpMgt" onchange="changeInterface(this.options[this.selectedIndex].value);"
									list="%{enumDhcpInterfaceMgt}" listKey="key" listValue="value" cssStyle="width: 100px;"
									 disabled="%{disableDhcpInterface}" /></td>
							</tr>
							<tr id="showInterfaceIp" style="display:<s:property value="displayInterfaceIp"/>">
								<td class="labelT1"><s:text name="config.network.object.dhcp.server.inter.ip" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.interfaceIp" size="24" maxlength="15"
									onkeypress="return hm.util.keyPressPermit(event,'ip');" disabled="%{disableDhcpInterface}" /></td>
							</tr>
							<tr id="showInterfaceNet" style="display:<s:property value="displayInterfaceIp"/>">
								<td class="labelT1"><s:text name="config.network.object.dhcp.server.inter.net" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.interfaceNet" size="24" maxlength="15"
									onkeypress="return hm.util.keyPressPermit(event,'ip');" disabled="%{disableDhcpInterface}" /></td>
							</tr>
							<tr id="showInterfaceVlan" style="display:<s:property value="displayInterfaceIp"/>">
								<td class="labelT1"><s:text name="config.vlan.vlanId" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.interVlan" size="10" maxlength="4" disabled="%{disableDhcpInterface}"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />
									<s:text name="config.localUserGroup.vlanIdRange" /></td>
							</tr>
							<tr id="showInterfacePing" style="display:<s:property value="displayInterfaceIp"/>">
								<td>&nbsp;</td>
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 0"><s:checkbox name="dataSource.enablePing"
									 		value="%{dataSource.enablePing}" /></td>
									 	<td><s:text name="config.network.object.dhcp.server.ping" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.ipFilter.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" />
									<s:text name="config.ipFilter.description.range" /></td>
							</tr>
							<tr>
								<td style="padding:6px 4px 6px 2px" colspan="2">
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
								<td style="padding-left:6px" colspan="2">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td><s:radio label="Gender" name="dhcpType" disabled="%{disableDhcpInterface}"
											list="#{'option':'Enable a DHCP server on this interface'}" onclick="dhcpServerOptionSelect(this.checked);"
											value="%{dhcpType}"/>
											<s:radio label="Gender" name="dhcpType" disabled="%{disableDhcpInterface}"
											list="#{'relay':'Enable a DHCP relay agent on this interface'}" onclick="dhcpRelaySelect(this.checked);"
											value="%{dhcpType}"/>
										</td>
									</tr>
									<tr id="option" style="display:<s:property value="displayDhcpOption"/>">
										<td>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding:6px 2px 2px 6px">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding-right:2px"><s:checkbox name="dataSource.authoritative"
															value="%{dataSource.authoritative}" /></td>
														<td><s:text name="config.network.object.dhcp.server.author" /></td>
														<td style="padding:0 2px 0 20px"><s:checkbox name="dataSource.enableArp"
															value="%{dataSource.enableArp}" /></td>
														<td><s:text name="config.network.object.dhcp.server.arp" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<td style="padding:4px 4px 4px 4px;" valign="top">
													<fieldset><legend><s:text
														name="config.network.object.dhcp.server.ip.pools" /></legend>
													<table cellspacing="0" cellpadding="0" border="0" class="embedded">
														<tr style="display:<s:property value="%{hidePoolNewButton}"/>" id="poolNewButton">
															<td colspan="3" style="padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="New"
																		class="button" onClick="showCreateSection(true);"
																		<s:property value="writeDisabled" />></td>
																	<td><input type="button" name="ignore" value="Remove"
																		class="button" <s:property value="writeDisabled" />
																		onClick="submitAction('removeIPAddress');"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display:<s:property value="%{hidePoolCreateItem}"/>" id="poolCreateButton">
															<td colspan="3" style="padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																		class="button" <s:property value="writeDisabled" /> onClick="submitAction('addIPAddress');"></td>
																	<td><input type="button" name="ignore" value="Remove"
																		class="button" <s:property value="writeDisabled" />
																		onClick="submitAction('removeIPAddressNone');"></td>
																	<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																		class="button" onClick="hideCreateSection(true);"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr id="poolHeaderSection">
															<th align="left" style="padding-left: 0;" width="10"><input
																type="checkbox" id="checkAllPool"
																onClick="toggleCheckAll(this, true);"></th>
															<th align="left" width="200"><s:text
																name="config.tunnelSetting.startIpAddress" /></th>
															<th align="left" width="200"><s:text
																name="config.tunnelSetting.endIpAddress" /></th>
														</tr>
														<tr style="display:<s:property value="%{hidePoolCreateItem}"/>" id="poolCreateSection">
															<td class="listHead" width="10">&nbsp;</td>
															<td class="listHead" valign="top"><s:textfield size="20" name="startIp" maxlength="15"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															<td class="listHead" valign="top"><s:textfield size="20" name="endIp" maxlength="15"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
														</tr>
														<s:iterator value="%{dataSource.ipPools}" status="status">
															<tr>
																<td class="listCheck"><s:checkbox name="poolIndices"
																	fieldValue="%{#status.index}" /></td>
																<td class="list" width="200"><s:property value="startIp" /></td>
																<td class="list" width="200"><s:property value="endIp" /></td>
															</tr>
														</s:iterator>
														<s:if test="%{poolGridCount > 0}">
															<s:generator separator="," val="%{' '}" count="%{poolGridCount}">
																<s:iterator>
																	<tr>
																		<td class="list" colspan="3">&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
														</s:if>
													</table>
													</fieldset>
												</td>
											</tr>
											<tr><td height="5px"></td></tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.network.object.dhcp.server.options" />','serverOption');</script></td>
											</tr>
											<tr>
												<td valign="top">
													<div id="serverOption" style="display: <s:property value="%{dataSource.serverOptionDisplayStyle}"/>">
													<fieldset>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="100"><s:text name="admin.interface.defaultGateway" /></td>
																<td><s:textfield name="dataSource.defaultGateway" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1" width="95"><s:text name="config.cwp.head.leaseTime" /></td>
																<td><s:textfield name="dataSource.leaseTime" size="20" maxlength="8"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text name="config.network.object.dhcp.server.lease.range" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.dns1" /></td>
																<td><s:textfield name="dataSource.dnsServer1" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1"><s:text name="config.ipAddress.netmask" /></td>
																<td><s:textfield name="dataSource.dhcpNetmask" size="20" maxlength="15" disabled="%{disableDhcpNetmask}"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.dns2" /></td>
																<td><s:textfield name="dataSource.dnsServer2" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1"><label><s:text
																	name="config.mgmtservice.domain.name" /></label></td>
																<td><s:textfield name="dataSource.domainName" size="20"
																	onkeypress="return hm.util.keyPressPermit(event,'name');" maxlength="32"/>
																	<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
															</tr>
															<tr>
																<td class="labelT1" width="100"><s:text name="config.network.object.dhcp.server.dns3" /></td>
																<td><s:textfield name="dataSource.dnsServer3" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1" width="100"><s:text name="config.network.object.dhcp.server.mtu" /></td>
																<td><s:textfield name="dataSource.mtu" size="20" maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text name="config.network.object.dhcp.server.mtu.range" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.pop3" /></td>
																<td><s:textfield name="dataSource.pop3" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.ntp1" /></td>
																<td><s:textfield name="dataSource.ntpServer1" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.smtp" /></td>
																<td><s:textfield name="dataSource.smtp" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.ntp2" /></td>
																<td><s:textfield name="dataSource.ntpServer2" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.wins1" /></td>
																<td><s:textfield name="dataSource.wins1" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.logsrv" /></td>
																<td><s:textfield name="dataSource.logsrv" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.network.object.dhcp.server.wins2" /></td>
																<td><s:textfield name="dataSource.wins2" size="20" maxlength="15"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															</tr>
														</table>
													</fieldset>
													</div>
												</td>
											</tr>
											<tr><td height="4px"></td></tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.network.object.dhcp.server.options.custom" />','customOption');</script></td>
											</tr>
											<tr>
												<td valign="top">
													<div id="customOption" style="display: <s:property value="%{dataSource.customOptionDisplayStyle}"/>">
													<fieldset style="padding-top: 2px;">
													<table cellspacing="0" cellpadding="0" border="0" class="embedded">
														<tr style="display:<s:property value="%{hideCustomNewButton}"/>" id="customNewButton">
															<td colspan="4" style="padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="New"
																		class="button" onClick="showCreateSection(false);"
																		<s:property value="writeDisabled" />></td>
																	<td><input type="button" name="ignore" value="Remove"
																		class="button" <s:property value="writeDisabled" />
																		onClick="submitAction('removeCustom');"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display:<s:property value="%{hideCustomCreateItem}"/>" id="customCreateButton">
															<td colspan="4" style="padding-bottom: 2px;">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																		class="button" <s:property value="writeDisabled" /> onClick="submitAction('addCustom');"></td>
																	<td><input type="button" name="ignore" value="Remove"
																		class="button" <s:property value="writeDisabled" />
																		onClick="submitAction('removeCustomNone');"></td>
																	<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																		class="button" onClick="hideCreateSection(false);"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td colspan="3" class="noteInfo" width="100%"><label><s:text
																name="config.network.object.dhcp.server.hivemanager.range" /></label></td>
														</tr>
														<tr id="customHeaderSection">
															<th align="left" style="padding-left: 0;" width="10"><input id="checkAllCustom"
																type="checkbox" onClick="toggleCheckAll(this, false);"></th>
															<th align="left" width="100"><s:text
																name="config.network.object.dhcp.server.options.custom.number" /></th>
															<th align="left" width="100"><s:text
																name="config.network.object.dhcp.server.options.custom.type" /></th>
															<th align="left" width="200"><s:text
																name="config.network.object.dhcp.server.options.custom.value" /></th>
														</tr>
														<tr style="display:<s:property value="%{hideCustomCreateItem}"/>" id="customCreateSection">
															<td class="listHead" width="10">&nbsp;</td>
															<td class="listHead" valign="top"><s:textfield size="20" name="customNumber" maxlength="3"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR/><s:text
																name="config.network.object.dhcp.server.options.custom.number.range" /></td>
															<td width="100" class="listHead" valign="top"><s:select name="customType"
																list="%{enumCustomType}" listKey="key" listValue="value"
																onchange="changeCustomType(this.options[this.selectedIndex].value);"/></td>
															<td class="listHead" valign="top" width="200" id="hideInteger" style="display:<s:property value="displayCustomInt"/>" >
																<s:textfield size="30" maxlength="10"
																name="integerValue" onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR/>
																<s:text name="config.network.object.dhcp.server.options.custom.int.range" /></td>
															<td class="listHead" valign="top" width="200" id="hideIp" style="display:<s:property value="displayCustomIp"/>" >
																<s:textfield size="30" maxlength="15"
																name="ipValue" onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
															<td class="listHead" valign="top" width="200" id="hideString" style="display:<s:property value="displayCustomStr"/>" >
																<s:textfield size="30" maxlength="255"
																name="strValue" onkeypress="return hm.util.keyPressPermit(event,'name');" /><BR/>
																<s:text name="config.string.range" /></td>
															<td class="listHead" valign="top" width="200" id="hideHex" style="display:<s:property value="displayCustomHex"/>" >
																<s:textarea name="hexValue"  cssStyle="width: 280px;resize:none" rows="7" maxLength="254"
																					onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
																<BR/>
																<s:text name="config.network.object.dhcp.server.options.custom.hex" /></td>
														</tr>
														<s:iterator value="%{dataSource.customs}" status="status">
															<tr>
																<td class="listCheck"><s:checkbox name="customIndices"
																	fieldValue="%{#status.index}" /></td>
																<td class="list"><s:property value="number" /></td>
																<td class="list"><s:property value="strType" /></td>
																<td class="list"><span class="ellipsis" title="<s:property value='value' />" style="width:200px;"><s:property value="value" /></span></td>
															</tr>
														</s:iterator>
														<s:if test="%{customGridCount > 0}">
															<s:generator separator="," val="%{' '}" count="%{customGridCount}">
																<s:iterator>
																	<tr>
																		<td class="list" colspan="4">&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
														</s:if>
													</table>
													</fieldset>
													</div>
												</td>
											</tr>
											<tr><td height="4px"></td></tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.hp.tab.advanced" />','advOption');</script></td>
											</tr>
											<tr>
												<td valign="top">
													<div id="advOption" style="display: <s:property value="%{dataSource.advancedDisplayStyle}"/>">
													<fieldset>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td style="padding:0 2px 0 0"><s:checkbox name="dataSource.natSupport"
															 		value="%{dataSource.natSupport}" /></td>
															 	<td><s:text name="config.lldpcdpprofile.support.nat" /></td>
															</tr>
															<tr>
																<td style="padding:0 2px 0 0">&nbsp;</td>
															 	<td><s:text name="config.lldpcdpprofile.support.nat.note" /></td>
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
							<tr>
								<td style="padding-left:6px" colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="4"></td>
									</tr>
									<tr id="relay" style="display:<s:property value="displayDhcpRelay"/>">
										<td class="labelT1" width="145" style="padding-left:10px"><s:text name="config.network.object.dhcp.server.ip.helper1" /><font color="red"><s:text name="*"/></font></td>
										<td style="padding-right:10px"><s:textfield name="dataSource.ipHelper1" size="20" maxlength="15"
											onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
										<td class="labelT1" width="150"><s:text name="config.network.object.dhcp.server.ip.helper2" /></td>
										<td><s:textfield name="dataSource.ipHelper2" size="20" maxlength="15"
											onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									</tr>
									<tr><td height="5px"></td></tr>
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
<style>
.hint {
color: gray;
}
</style>
<script>
var hintClassName="hint";
var hexValueHint = '<s:text name="guadalupe_2.config.network.object.dhcp.server.options.custom.hex.hint"/>';
function loadWaterMark(){
	// initial water mark
	showWaterMark(Get(formName+'_hexValue'), hexValueHint);
	// water mark event
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "focus", focusAction);
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "blur", blurAction);
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "keyup", keyupAction);
}

///--------------- Hex value: WaterMark -----------------///
function focusAction(e) {
	hideWaterMark(this.id);
}

function blurAction(e) {
	showWaterMark(this.id, hexValueHint);
}

function keyupAction(e){
	checkMaxLength(this.id);
}

function hideWaterMark(elementId) {
	var el = Get(elementId);
	if(el) {
		if(YAHOO.util.Dom.hasClass(el, hintClassName)) {
			YAHOO.util.Dom.removeClass(el, hintClassName);
			el.value = "";
		}
	}
}
function showWaterMark(elementId, text) {
	var el = Get(elementId);
	if(el) {
		var value = el.value;
		if (value.length == 0 || value.trim().length == 0 || text == value) {
			YAHOO.util.Dom.addClass(el, hintClassName);
			el.value = text;
		} else {
			checkMaxLength(elementId);
		}
	}
}
function checkMaxLength(elementId){
	var el = $("#"+elementId);
	var maxLength = parseInt(el.attr('maxlength'));
	if(maxLength > 0 && el.val().length > maxLength){
		el.val(el.val().substr(0,maxLength));
	}
}
</script>