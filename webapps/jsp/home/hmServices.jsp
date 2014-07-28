<%@page import="com.ah.bo.admin.HMServicesSettings"%>
<jsp:directive.page import="com.ah.ui.actions.home.HmServicesAction" />
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.util.EnumConstUtil"%>
<%@page import="com.ah.bo.admin.RemoteProcessCallSettings"%>
<%@page import="com.ah.bo.admin.OpenDNSAccount"%>
<link rel="stylesheet" type="text/css" href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/css/widget/ahdatatable/jquery.ui.theme.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<style type="text/css">
  .ul {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
    }
    .um {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
    }
    .ur {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
    }
    .ml {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
    }
    .mm {
    height:100%;
    background-color: #f9f9f7;
    }
    .mr {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
    }
    .bl {
    width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
    }
    .bm {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
    }

	.br {
	width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
    }

#newOpenDNSDevicePanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
</style> 
<script src="<s:url value="/js/widget/dataTable/ahDataTable.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/underscore-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/drag/ahdrag.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
	
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />"></script>
<script type="text/javascript">
$(document).ready(function() {
    $('#securityKeyId').bind("cut copy", function(e) {
        e.preventDefault();
    });

    $('#authorizationKeyId').bind("cut copy", function(e) {
        e.preventDefault();
    });
    
    $('#openDNSPasswordId').bind("cut copy", function(e) {
        e.preventDefault();
    });
});
</script>

<script>
var formName = 'hmServices';
var METHOD_LOCAL = <%=EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL%>;
var METHOD_RADIUS = <%=EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS%>;
var TFTP_ON = <%=HmServicesAction.TFTPSTATE_ON%>;
var TFTP_OFF = <%=HmServicesAction.TFTPSTATE_OFF%>;
var WEBSENSEMODE_HOSTED = <%=HMServicesSettings.WEBSENSEMODE_HOSTED%>;
var WEBSENSEMODE_HYBRID = <%=HMServicesSettings.WEBSENSEMODE_HYBRID%>;
var thisOperation;
var showError;
var OPENDNS_SERVER_1 = "<%=OpenDNSAccount.OPENDNS_SERVER_1%>";
var OPENDNS_SERVER_2 = "<%=OpenDNSAccount.OPENDNS_SERVER_2%>";

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Sending test email...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function onLoadPage()
{
	showError = document.getElementById("errorDisplay");
	selectUpdateAuth(<s:property value="%{updateAuth}"/>);

	initTableCellEnabled();

	var cbNotifyEnable = document.getElementById("cbEnableNotify");
	if (!cbNotifyEnable.checked)
	{
		disableNotifyTable();
	}

	if (cbNotifyEnable.disabled == true)
	{
		disableNotifyTable();
	}

	createWaitingPanel();

	if (document.getElementById('smtpEncryption').checked)
	{
		hm.util.show('smtpEncrySection');
	} else {
		hm.util.hide('smtpEncrySection');
	}

	var supportAuth = document.getElementById('supportAuth').checked;
	if (supportAuth)
	{
		hm.util.show('authSection');
	}

	if (document.getElementById('showNotifyInfo').checked)
	{
		document.getElementById('hmNotifyInfoTd').style.display="block";
	}
	else
	{
		document.getElementById('hmNotifyInfoTd').style.display="none";
	}
	if(<s:property value="%{fullMode}"/>){
		// init websecurity new/modify button
		checkWebsense(<s:property value="%{enableWebsense}"/>);
		checkBarracuda(<s:property value="%{enableBarracuda}"/>);
		checkOpenDNS(<s:property value="%{enableOpenDNS}"/>);
		
		//Init the Client Manager
		selectClientManagementUpdate(<s:property value="%{onboardUpdate}"/>);
		
		dislayGuestAanalytics(<s:property value="%{updateGuestAnalytics}"/>);
	}
	
	<s:if test="%{updateIDM}">
	displayIDMServiceSecion('idmRetrieveSection');
	</s:if>
	
	//Init the TeacherView
	alternateFoldingContent('tvCaseSensitiveSettingSection');
}

function disableNotifyTable()
{
	disableComponent("enabledCritical");
	disableComponent("enabledMajor");
	disableComponent("enabledMinor");
	disableComponent("enabledInfo");
	disableComponent("enabledClear");
	disableComponent("enabled_event");
}

function submitAction(operation)
{
    thisOperation = operation;
   	if (validate(thisOperation))
   	{
   		var warningArray = new Array();
   		var index = 0;
   		if ("update" == operation) {
   			if (document.getElementById("tftpStateUpdate").checked)
   	   		{
   	   			var tftpState = document.getElementById("tftpState").value;
   	   			if (tftpState == TFTP_ON)
   	   			{
   	   				warningArray[index] = '<s:text name="warn.admin.management.update.startTFTPservice" />';
   	   			}
   	   			else
   	   			{
   	   				warningArray[index] = '<s:text name="warn.admin.management.update.stopTFTPservice" />';
   	   			}

   	   			index++;
   	   		}

   	   		var authMethod = document.getElementById("adminAuth");
   	   		if (document.getElementById("updateAuth").checked && METHOD_RADIUS == authMethod.value)
   	   		{
   	   			warningArray[index] = '<s:text name="warn.admin.management.update.authentication" />';
   	   			index++;
   	   		}
   	   		// prompt warning message if enable RPC
   	   		if (document.getElementById("updateRPC") != null
   	   				&& document.getElementById("updateRPC").checked) {
   	   			warningArray[index] = '<s:text name="warn.admin.management.update.rac4studentmanager" />';
   	   			index++;
   	   		}
   	   		
   	   		// prompt for GA
   	   		if(Get(formName + "_updateGuestAnalytics") && Get(formName + "_updateGuestAnalytics").checked 
   	   				&& Get(formName + "_enabledGuestAnanlytics") && !Get(formName + "_enabledGuestAnanlytics").checked 
   	   				&& ssidsArray) {
   	   			document.forms[formName].resetSSIDs.value = true;
   	   			warningArray[index] = '<s:text name="home.services.guestanalytics.disable.warnning" />' + getULText(ssidsArray);
   	   			index++;
   	   		}
   		}

   		if (warningArray.length > 0)
   		{
   			confirmUpdate(warningArray);
   		}
   		else
   		{
   			doContinueOper();
   		}
    }
}

function doContinueOper()
{
	showProcessing();
    document.forms[formName].operation.value = thisOperation;

    document.forms[formName].submit();
}

function confirmUpdate(title)
{
	var titleMsg = "<br>";
	if (title.length == 1)
	{
		titleMsg += title[0]+"<br>";
	}
	else
	{
		for (i = 1;i<=title.length;i++)
		{
			titleMsg += i+". "+title[i-1]+"<br>"
		}
	}

	confirmDialog.cfg.setProperty('text',titleMsg+"Are you sure you want to perform the selected operation?");
	confirmDialog.show();
}

function validate(operation)
{
	if(operation == "newIpAddress" || operation == "newRadius" || operation == "newWebsenseWhitelists" || operation == "newBarracudaWhitelists")
	{
		return true;
	}

	if(operation == "editIpAddress"){
		var value = hm.util.validateListSelection("myIpSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipAddressId.value = value;
			return true;
		}
	}

	if(operation == "editRadius"){
		var value = hm.util.validateListSelection("radiusList");
		if(value < 0){
			return false
		}else{
			document.forms[formName].radiusServiceId.value = value;
			return true;
		}
	}
	if(operation == "editWebsenseWhitelists" || operation == "editBarracudaWhitelists"){
		var value;
		if (operation == "editWebsenseWhitelists") {
			value = hm.util.validateListSelection("websenseWhitelist");
		} else {
			value = hm.util.validateListSelection("barracudaWhitelist");
		}
		if(value < 0){
			return false
		}else{
			document.forms[formName].whiteListId.value = value;
			return true;
		}
	}

	if (document.getElementById("updateAuth").checked)
	{
		var authMethod = document.getElementById("adminAuth");

		if (METHOD_LOCAL != authMethod.value)
		{
	        var radiusId = document.forms[formName].radiusServiceId;
			if (!hasSelectedOptions(radiusId.options)) {
		        hm.util.reportFieldError(radiusId, '<s:text name="error.pleaseSelect"><s:param><s:text name="admin.management.radiusServer" /></s:param></s:text>');
		        radiusId.focus();
				return false;
			}
	    }
	}

	if (document.getElementById("updateSNMP").checked)
	{
		var snmpCommunity = document.getElementById("snmpCommunity");

		if ( snmpCommunity.value.length == 0)
		{
	        hm.util.reportFieldError(snmpCommunity, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.snmpCommunity" /></s:param></s:text>');
	        snmpCommunity.focus();
	        return false;
	  	}
		if (!checkNameValid(snmpCommunity, '<s:text name="admin.management.snmpCommunity" />', snmpCommunity)) {
	       	return false;
	   	}

		var ipnames = document.getElementById("myIpSelect");
		var ipValue = document.forms[formName].inputIpValue;
		if ("" == ipValue.value) {
	        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="admin.management.snmpTrapReceiver" /></s:param></s:text>');
	        ipValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				if (!checkNameValid(ipValue, '<s:text name="admin.management.snmpTrapReceiver" />', showError)) {
			       	return false;
			   	}
			}
   			document.forms[formName].ipAddressId.value = -1;
   		} else {
   			document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
   		}
	}

	if (document.getElementById("updateLogServer").checked)
	{
		if (operation == 'addSubnet')
		{
			var ip = document.getElementById("subnetIP");
			var mask = document.getElementById("subnetMask");

			if (ip.value.length == 0) {
		            hm.util.reportFieldError(ip, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.subnetIP" /></s:param></s:text>');
		            ip.focus();
		            return false;
		    } else if (! hm.util.validateIpAddress(ip.value)) {
				hm.util.reportFieldError(ip, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.management.subnetIP" /></s:param></s:text>');
				ip.focus();
				return false;
			}

			if (mask.value.length == 0) {
		            hm.util.reportFieldError(mask, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.netmask" /></s:param></s:text>');
		            mask.focus();
		            return false;
		    } else if (! hm.util.validateMask(mask.value)) {
				hm.util.reportFieldError(mask, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.management.netmask" /></s:param></s:text>');
				mask.focus();
				return false;
			}
		}
	}

	if (document.getElementById("enableTVProxy").checked){
		var ip = document.getElementById("tvProxyIP");
		var port = document.getElementById("tvProxyPort");
		var autoProxyFile = document.getElementById("tvAutoProxyFile");
		if (ip.value.length == 0) {
            hm.util.reportFieldError(ip, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.tvproxyip" /></s:param></s:text>');
            ip.focus();
            return false;
   		}

		var message = hm.util.validateIntegerRange(port.value, '<s:text name="admin.management.tvproxyport" />', 1, 65535);
		if (message != null) {
		    hm.util.reportFieldError(port,message);
			port.focus();
			return false;
		}

		if ((autoProxyFile.value.length != 0) && (!(autoProxyFile.value.indexOf('http') == 0
		            || autoProxyFile.value.indexOf('https') == 0)
		            || autoProxyFile.value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 )){
		            hm.util.reportFieldError(autoProxyFile,
		                '<s:text name="error.home.hmservice.proxyserver.autoproxy.urlinvalid"><s:param><s:text name="admin.management.tvautoproxyfile"/></s:param></s:text>');
		            element.focus();
		            return false;
		}
	}

	if(<s:property value="%{fullMode}"/>){
		if (document.getElementById("updateBarracudaServer").checked)
		{
			var serviceHost = document.getElementById("serviceHost");
			var servicePort = document.getElementById("servicePort");
			var barracudaDefaultUserName = document.getElementById("barracudaDefaultUserName");
			var authorizationKeyId;
			var chkAuthorizationKey = document.getElementById("chkToggleDisplay_authorizationKey").checked;
			if(chkAuthorizationKey) {
				authorizationKeyId = document.getElementById("authorizationKeyId");
			} else {
				authorizationKeyId = document.getElementById("authorizationKeyId_text");
			}

			if (authorizationKeyId.value.length == 0)
			{
		        hm.util.reportFieldError(authorizationKeyId, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateBarracudaServer.authorizationKey" /></s:param></s:text>');
		        authorizationKeyId.focus();
		        return false;
		    }
			else if (authorizationKeyId.value.length != 40)
		    {
		    	hm.util.reportFieldError(authorizationKeyId, '<s:text name="error.keyLengthRange"><s:param><s:text name="admin.management.updateBarracudaServer.authorizationKey" /></s:param><s:param><s:text name="admin.management.updateBarracudaServer.authorizationKeyRange" /></s:param></s:text>');
		    	authorizationKeyId.focus();
				return false;
		    }

			if (serviceHost.value.length == 0)
			{
		        hm.util.reportFieldError(serviceHost, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.webSecurity.serviceHost" /></s:param></s:text>');
		        serviceHost.focus();
		        return false;
		    } else if (serviceHost.value.length > 64) {
		    	hm.util.reportFieldError(serviceHost, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.serviceHost" /></s:param><s:param><s:text name="admin.management.webSecurity.serviceHost.range" /></s:param></s:text>');
		    	serviceHost.focus();
		        return false;
		    }

			if (servicePort.value.length == 0)
			{
		        hm.util.reportFieldError(servicePort, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.webSecurity.servicePort" /></s:param></s:text>');
		        servicePort.focus();
		        return false;
		    } else if (!isValidServicePort(servicePort.value))
		    {
		    	hm.util.reportFieldError(servicePort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.servicePort" /></s:param><s:param><s:text name="admin.management.webSecurity.range" /></s:param></s:text>');
		    	servicePort.focus();
				return false;
		    }

			if (barracudaDefaultUserName.value.length == 0)
			{
		        hm.util.reportFieldError(barracudaDefaultUserName, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName" /></s:param></s:text>');
		        barracudaDefaultUserName.focus();
		        return false;
		    } else if (barracudaDefaultUserName.value.length > 32 )
		    {
		    	hm.util.reportFieldError(barracudaDefaultUserName, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName" /></s:param><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName.range" /></s:param></s:text>');
		    	barracudaDefaultUserName.focus();
				return false;
		    }
		}

		if (document.getElementById("updateWebSenseServer").checked)
		{
			var accountID = document.getElementById("accountID");
			var webSenseDefaultUserName = document.getElementById("webSenseDefaultUserName");
			var securityKeyId;
			var chkSecurityKey = document.getElementById("chkToggleDisplay_securityKey").checked;
			if(chkSecurityKey) {
				securityKeyId = document.getElementById("securityKeyId");
			} else {
				securityKeyId = document.getElementById("securityKeyId_text");
			}
			var defaultDomain = document.getElementById("defaultDomain");

			if (accountID.value.length == 0)
			{
		        hm.util.reportFieldError(accountID, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateWebsenseServer.accountID" /></s:param></s:text>');
		        accountID.focus();
		        return false;
		    } else if(accountID.value.length > 16){
		    	hm.util.reportFieldError(accountID, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.updateWebsenseServer.accountID" /></s:param><s:param><s:text name="admin.management.updateWebsenseServer.accountID.range" /></s:param></s:text>');
		    	accountID.focus();
				return false;
		    }
			
			if(webSenseDefaultUserName.value.length == 0){
				hm.util.reportFieldError(webSenseDefaultUserName, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName" /></s:param></s:text>');
				webSenseDefaultUserName.focus();
				return false;
			} else if(webSenseDefaultUserName.value.length > 32){
		    	hm.util.reportFieldError(webSenseDefaultUserName, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName" /></s:param><s:param><s:text name="admin.management.webSecurity.webSenseDefaultUserName.range" /></s:param></s:text>');
		    	webSenseDefaultUserName.focus();
				return false;
		    }

			if (securityKeyId.value.length == 0)
			{
		        hm.util.reportFieldError(securityKeyId, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.webSecurity.websense.securityKey" /></s:param></s:text>');
		        securityKeyId.focus();
		        return false;
		    } else if(securityKeyId.value.length != 32){
		    	hm.util.reportFieldError(securityKeyId, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.websense.securityKey" /></s:param><s:param><s:text name="admin.management.webSecurity.websense.securityKey.range" /></s:param></s:text>');
		    	securityKeyId.focus();
				return false;
		    } else if (!validateHex(securityKeyId.value)) {
		    	hm.util.reportFieldError(securityKeyId, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.management.webSecurity.websense.securityKey" /></s:param><s:param><s:text name="admin.management.webSecurity.websense.securityKey.range" /></s:param></s:text>');
		    	securityKeyId.focus();
				return false;
		    }

			if (defaultDomain.value.length > 32) {
		    	hm.util.reportFieldError(defaultDomain, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.webSecurity.websense.defaultDomain" /></s:param><s:param><s:text name="admin.management.webSecurity.websense.defaultDomain.range" /></s:param></s:text>');
		    	defaultDomain.focus();
				return false;
			}
		}
		
		if ($('#updateOpenDNSServer').is(':checked'))
		{	
			if(!openDNSValidate()){
				return false;
			}
			
		}
	}

	if (document.getElementById("updateCAPWAP").checked)
	{
		var primaryCapwapIP = document.getElementById("primaryCapwapIP");
		var backupCapwapIP = document.getElementById("backupCapwapIP");
		if (primaryCapwapIP.value.length > 32) {
			hm.util.reportFieldError(primaryCapwapIP, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.primaryCapwapIP" /></s:param><s:param><s:text name="admin.capwap.serverNameRange" /></s:param></s:text>');
			primaryCapwapIP.focus();
			return false;
		}

		if (backupCapwapIP.value.length > 32) {
			hm.util.reportFieldError(backupCapwapIP, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.backupCapwapIP" /></s:param><s:param><s:text name="admin.capwap.serverNameRange" /></s:param></s:text>');
			backupCapwapIP.focus();
			return false;
		}

		var port = document.getElementById("capwapUdpPort");
		var timeOut = document.getElementById("capwapTimeOut");
		var deadInterval = document.getElementById("deadInterval");
		var	trapFilterInterval = document.getElementById("trapFilterInterval");

		if (port.value.length == 0)
		{
	        hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.udpPort" /></s:param></s:text>');
	        port.focus();
	        return false;
	    }
	    else if (!isValidCapwapPort(port.value))
	    {
			hm.util.reportFieldError(port, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.udpPort" /></s:param><s:param><s:text name="admin.capwap.udpPortRange" /></s:param></s:text>');
			port.focus();
			return false;
		}

		if (timeOut.value.length == 0)
		{
	        hm.util.reportFieldError(timeOut, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.timeOut" /></s:param></s:text>');
	        timeOut.focus();
	        return false;
	    }
	    else if (!isValidTimeout(timeOut.value))
	    {
			hm.util.reportFieldError(timeOut, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.timeOut" /></s:param><s:param><s:text name="admin.capwap.timeOutRange" /></s:param></s:text>');
			timeOut.focus();
			return false;
		}

		if (deadInterval.value.length == 0)
		{
	        hm.util.reportFieldError(deadInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.deadInterval" /></s:param></s:text>');
	        deadInterval.focus();
	        return false;
	    }
	    else if (!isValidDeadInterval(deadInterval.value,timeOut.value))
	    {
			hm.util.reportFieldError(deadInterval, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.deadInterval" /></s:param><s:param><s:text name="admin.capwap.deadIntervalRange" /></s:param></s:text>');
			deadInterval.focus();
			return false;
		}

		if (trapFilterInterval.value.length == 0)
		{
	        hm.util.reportFieldError(trapFilterInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.trapFilterInterval" /></s:param></s:text>');
	        trapFilterInterval.focus();
	        return false;
	    }
	    else if (!isValidTrapInterval(trapFilterInterval.value))
	    {
			hm.util.reportFieldError(trapFilterInterval, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.trapFilterInterval" /></s:param><s:param><s:text name="admin.capwap.trapIntervalRange" /></s:param></s:text>');
			trapFilterInterval.focus();
			return false;
		}

	    var cbPassPhrase = document.getElementById("cbPassPhrase");
	    if (!cbPassPhrase.checked)
	    {
	    	var newPhrase;
	    	var confirmPhrase;
	    	if (document.getElementById("chkToggleDisplay_capwap").checked)
	    	{
	    		newPhrase = document.getElementById("capwapPass");
	    		confirmPhrase = document.getElementById("confirmCapwapPass");
	    	}
	    	else
	    	{
	    		newPhrase = document.getElementById("capwapPass_text");
	    		confirmPhrase = document.getElementById("confirmCapwapPass_text");
	    	}

		    if (newPhrase.value.length == 0)
			{
		        hm.util.reportFieldError(newPhrase, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.newPassPhrase" /></s:param></s:text>');
		        newPhrase.focus();
		        return false;
		    }

		    if (newPhrase.value.indexOf(' ') > -1) {
		        hm.util.reportFieldError(newPhrase, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.capwap.newPassPhrase" /></s:param></s:text>');
		        newPhrase.focus();
		        return false;
			}

		    if (confirmPhrase.value.length == 0)
			{
		        hm.util.reportFieldError(confirmPhrase, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.confirmPassPhrase" /></s:param></s:text>');
		        confirmPhrase.focus();
		        return false;
		    }

		    if (newPhrase.value.length < 16)
			{
		        hm.util.reportFieldError(newPhrase, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.newPassPhrase" /></s:param><s:param><s:text name="admin.capwap.phraseRange" /></s:param></s:text>');
				newPhrase.focus();
				return false;
		    }

		    if (confirmPhrase.value.length < 16)
			{
		        hm.util.reportFieldError(confirmPhrase, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.confirmPassPhrase" /></s:param><s:param><s:text name="admin.capwap.phraseRange" /></s:param></s:text>');
				confirmPhrase.focus();
				return false;
		    }

		    if (confirmPhrase.value.valueOf() != newPhrase.value.valueOf() )
		  	{
		  		hm.util.reportFieldError(confirmPhrase, '<s:text name="error.notEqual"><s:param><s:text name="admin.capwap.confirmPassPhrase" /></s:param><s:param><s:text name="admin.capwap.newPassPhrase" /></s:param></s:text>');
		        confirmPhrase.focus();
		        return false;
		  	}
	    }
	}

	if (document.getElementById('updateEmail').checked)
	{
		var smtpServer = document.getElementById("smtpServer");
		if (smtpServer.value.length == 0) {
			hm.util.reportFieldError(smtpServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.emailNotify.smtpServer" /></s:param></s:text>');
			smtpServer.focus();
			return false;
		}

		var fromEmail = document.getElementById("fromEmail");
		if (fromEmail.value.length == 0) {
			hm.util.reportFieldError(fromEmail, '<s:text name="error.requiredField"><s:param><s:text name="admin.emailNotify.fromEmail" /></s:param></s:text>');
			fromEmail.focus();
			return false;
		}
		else if (!hm.util.validateEmail(fromEmail.value))
		{
			hm.util.reportFieldError(fromEmail, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.fromEmail" /></s:param></s:text>');
			fromEmail.focus();
			return false;
		}

		var toEmail1 = document.getElementById("toEmail1");
		if (toEmail1.value.length > 0 && !hm.util.validateEmail(toEmail1.value))
		{
			hm.util.reportFieldError(toEmail1, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.toEmail1" /></s:param></s:text>');
			toEmail1.focus();
			return false;
		}

		var toEmail2 = document.getElementById("toEmail2");
		if (toEmail2.value.length > 0 && !hm.util.validateEmail(toEmail2.value))
		{
			hm.util.reportFieldError(toEmail2, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.toEmail2" /></s:param></s:text>');
			toEmail2.focus();
			return false;
		}

		var toEmail3 = document.getElementById("toEmail3");
		if (toEmail3.value.length > 0 && !hm.util.validateEmail(toEmail3.value))
		{
			hm.util.reportFieldError(toEmail3, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.toEmail3" /></s:param></s:text>');
			toEmail3.focus();
			return false;
		}

		var toEmail4 = document.getElementById("toEmail4");
		if (toEmail4.value.length > 0 && !hm.util.validateEmail(toEmail4.value))
		{
			hm.util.reportFieldError(toEmail4, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.toEmail4" /></s:param></s:text>');
			toEmail4.focus();
			return false;
		}

		var toEmail5 = document.getElementById("toEmail5");
		if (toEmail5.value.length > 0 && !hm.util.validateEmail(toEmail5.value))
		{
			hm.util.reportFieldError(toEmail5, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.emailNotify.toEmail5" /></s:param></s:text>');
			toEmail5.focus();
			return false;
		}

		var smtpPort = document.getElementById("smtpPort");
		if (smtpPort.value.length == 0) {
			hm.util.reportFieldError(smtpPort, '<s:text name="error.requiredField"><s:param><s:text name="admin.emailNotfiy.smtpPort" /></s:param></s:text>');
			smtpPort.focus();
			return false;
		} else if (!isValidPort(smtpPort.value))
	    {
			hm.util.reportFieldError(smtpPort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.emailNotfiy.smtpPort" /></s:param><s:param><s:text name="admin.emailNotfiy.smtpPort.range" /></s:param></s:text>');
			smtpPort.focus();
			return false;
		}

		if (document.getElementById('supportAuth').checked)
		{
			var emailUserName = document.getElementById("emailUserName");
			if (emailUserName.value.length == 0) {
				hm.util.reportFieldError(emailUserName, '<s:text name="error.requiredField"><s:param><s:text name="admin.emailNotfiy.emailUserName" /></s:param></s:text>');
				emailUserName.focus();
				return false;
			}

			var emailPassword;
			if (document.getElementById("chkToggleDisplay_email").checked)
			{
				emailPassword = document.getElementById("emailPassword");
			}
			else
			{
				emailPassword = document.getElementById("emailPassword_text");
			}

			if (emailPassword.value.length == 0) {
				hm.util.reportFieldError(emailPassword, '<s:text name="error.requiredField"><s:param><s:text name="admin.emailNotify.emailPassword" /></s:param></s:text>');
				emailPassword.focus();
				return false;
			}
		}
	}

	if (document.getElementById("updateNotifyInfo").checked)
	{
		if (document.getElementById("showNotifyInfo").checked)
		{
			var title = document.getElementById('hmNotifyTitle');
			var content = document.getElementById('hmNotifyInfo');

			if (title.value.length == 0) {
				hm.util.reportFieldError(title, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.notifyTitle" /></s:param></s:text>');
				title.focus();
				return false;
			}

			if (content.value.length == 0) {
				hm.util.reportFieldError(content, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.notifyContent" /></s:param></s:text>');
				content.focus();
				return false;
			}
		}
	}

	if (document.getElementById("updateAirtight") != null &&
		document.getElementById("updateAirtight").checked) {
		 if(!validateAirTight()) return false;
	}

	if (document.getElementById("updateRPC") != null &&
		document.getElementById("updateRPC").checked) {
		if(!validateRPC()) return false;
	}


	if (document.getElementById("snpStateUpdate").checked){
		var snpState = document.getElementById("snpState");
		if (snpState.value.length == 0) {
            hm.util.reportFieldError(snpState, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.snpStateUpdate" /></s:param></s:text>');
            snpState.focus();
            return false;
      	}
      	var message = hm.util.validateIntegerRange(snpState.value, '<s:text name="admin.management.snpStateUpdate" />',2,30);
      	if (message != null) {
            hm.util.reportFieldError(snpState, message);
            snpState.focus();
            return false;
      	}
 	}
	if(document.getElementById("onboardUpdate").checked){
		/* if(document.getElementById("enableCustomizeCA").checked){
			var ca = document.getElementById("certificateFile");
			var key = document.getElementById("certificateKeyFile");
			if(ca.value.length != 0 && key.value.length == 0){
				hm.util.reportFieldError(key,'<s:text name="home.hmSettings.clientManagement.useCustomerCA.caKeyRequired"></s:text>');
				key.focus();
				return false;
			}else if(ca.value.length == 0 && key.value.length != 0){
				hm.util.reportFieldError(ca,'<s:text name="home.hmSettings.clientManagement.useCustomerCA.caRequired"></s:text>');
				ca.focus();
				return false;
			}
		} */
	}
	
	return true;
}


function openDNSValidate(){
	var openDNSUserName = $('#openDNSUserName');
	if(openDNSUserName.attr('value').length == 0){
        hm.util.reportFieldError(openDNSUserName.get(0), '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateOpenDNSServer.username" /></s:param></s:text>');
        openDNSUserName.focus();
        return false;
	}
	if(openDNSUserName.attr('value').length > 30){				
		hm.util.reportFieldError(openDNSUserName.get(0), '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.updateOpenDNSServer.username" /></s:param><s:param><s:text name="admin.management.updateOpenDNSServer.username.range" /></s:param></s:text>');
		openDNSUserName.focus();
        return false;
	}
	
	var openDNSPassword;
	if($('#chkToggleDisplay_OpenDNSPassword').is(':checked')){
	    openDNSPassword = $('#openDNSPasswordId');
	}else{
		openDNSPassword =  $('#openDNSPasswordIdText');
	}
	
	if(openDNSPassword.attr('value').length == 0){
        hm.util.reportFieldError(openDNSPassword.get(0), '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateOpenDNSServer.password" /></s:param></s:text>');
        openDNSPassword.focus();
        return false;
	}
	
	if(openDNSPassword.attr('value').length > 32){				
		hm.util.reportFieldError(openDNSPassword.get(0), '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.updateOpenDNSServer.password" /></s:param><s:param><s:text name="admin.management.updateOpenDNSServer.password.range" /></s:param></s:text>');
        openDNSPassword.focus();
        return false;
	}
	
/* 	var openDNSCustomerName = $('#openDNSCustomerName');
	if(openDNSCustomerName.attr('value').length == 0){
        hm.util.reportFieldError(openDNSCustomerName.get(0), '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateOpenDNSServer.customerName" /></s:param></s:text>');
        openDNSCustomerName.focus();
        return false;
	}
	
	if(openDNSCustomerName.attr('value').length > 64){				
		hm.util.reportFieldError(openDNSCustomerName.get(0), '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.updateOpenDNSServer.customerName" /></s:param><s:param><s:text name="admin.management.updateOpenDNSServer.customerName.range" /></s:param></s:text>');
		openDNSCustomerName.focus();
        return false;
	}*/
	
	var openDNSServer1IP = $('#openDNSServer1IP');
	if(openDNSServer1IP.attr('value').length == 0){
        hm.util.reportFieldError(openDNSServer1IP.get(0), '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateOpenDNSServer.dnsServer1IP" /></s:param></s:text>');
        openDNSServer1IP.focus();
        return false;
	}
	
	if(!hm.util.validateIpAddressFormat(openDNSServer1IP.attr('value'))){				
		hm.util.reportFieldError(openDNSServer1IP.get(0), '<s:text name="error.formatInvalid"><s:param><s:text name="admin.management.updateOpenDNSServer.dnsServer1IP" /></s:param></s:text>');
		openDNSServer1IP.focus();
        return false;
	}
	
	var openDNSServer2IP = $('#openDNSServer2IP');
	if(openDNSServer2IP.attr('value').length == 0){
        hm.util.reportFieldError(openDNSServer2IP.get(0), '<s:text name="error.requiredField"><s:param><s:text name="admin.management.updateOpenDNSServer.dnsServer2IP" /></s:param></s:text>');
        openDNSServer2IP.focus();
        return false;
	}
	
	if(!hm.util.validateIpAddressFormat(openDNSServer2IP.attr('value'))){				
		hm.util.reportFieldError(openDNSServer2IP.get(0), '<s:text name="error.formatInvalid"><s:param><s:text name="admin.management.updateOpenDNSServer.dnsServer2IP" /></s:param></s:text>');
		openDNSServer2IP.focus();
        return false;
	}
	
	return true;
}

function validateHex(value)
{
	if (value.length == 0) {
		return false;
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if (!((code >47 && code <58) ||(code >64 && code <71) || (code >96 && code <103))) {
	       		return false;
	       	}
	   }
	}

	return true;
}

function isValidPort(port)
{
	var intValue = port.valueOf();
	if ( intValue >=1 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

function isValidTimeout(timeout)
{
	var intValue = timeout.valueOf();
	if ( intValue >=30 && intValue <= 120 )
	{
		return true;
	}

	return false;
}

function isValidDeadInterval(interval,timeout)
{
	var intValue = interval.valueOf();
	if ( intValue >=2*(timeout.valueOf()) && intValue <= 240 )
	{
		return true;
	}

	return false;
}

function isValidTrapInterval(interval)
{
	var intValue = interval.valueOf();
	if ( intValue >= 0 && intValue <= 60 )
	{
		return true;
	}

	return false;
}

function isValidCapwapPort(port)
{
	var intValue = port.valueOf();
	if ( intValue >=1024 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

//Http Https Service Port validate
function isValidServicePort(port)
{
	var intValue = port.valueOf();
	if ( intValue >=1 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

function checkNameValid(name, title, errorDis) {
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(errorDis, message);
       	name.focus();
       	return false;
   	}
   	return true;
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function selectUpdateAuth(checked)
{
	document.getElementById("adminAuth").disabled=!checked;
	document.getElementById("authType").disabled=!checked;
	document.getElementById("radiusList").disabled=!checked;
	document.getElementById("staticButton").style.display=checked ? "none" : "";
	document.getElementById("activeButton").style.display=checked ? "" : "none";
}

function selectAdminAuth(value)
{
	if (value == 1)
	{
		hm.util.hide('radiusSection');
	}
	else
	{
		hm.util.show('radiusSection');
	}
}

function selectTftpStateUpdate(checked)
{
	document.getElementById("tftpState").disabled=!checked;
}

function selectSnpStateUpdate(checked){
	Get("snpState").disabled=!checked;
}
function selectPresenceUpdate(checked){
	if(checked){
		document.getElementById("presenceTr").style.display="";
	}else{
		document.getElementById("presenceTr").style.display="none";
	}
}
function selectUpdateSNMP(checked)
{
	if (checked)
	{
		hm.util.show('snmpSection');
	}
	else
	{
		hm.util.hide('snmpSection');
	}
}

function selectUpdateLogServer(checked)
{
	if (checked)
	{
		hm.util.show('logServerSection');
	}
	else
	{
		hm.util.hide('logServerSection');
	}
}

function selectUpdateNotifyInfo(checked)
{
	if (checked)
	{
		hm.util.show('notifyInfoSection');
	}
	else
	{
		hm.util.hide('notifyInfoSection');
	}
}

function selectUpdateBarracudaServer(checked)
{
	if (checked)
	{
		hm.util.show('barracudaServerSection');
	}
	else
	{
		hm.util.hide('barracudaServerSection');
	}
}

function selectUpdateOpenDNSServer(checked){
	if (checked)
	{
		hm.util.show('openDNSServerSection');
	}
	else
	{
		hm.util.hide('openDNSServerSection');
	}
}

function selectUpdateWebSenseServer(checked)
{
	if (checked)
	{
		hm.util.show('webSenseServerSection');
	}
	else
	{
		hm.util.hide('webSenseServerSection');
	}
}

function selectShowNotifyInfo(checked)
{
	if (checked)
	{
		document.getElementById('hmNotifyInfoTd').style.display="block";
	}
	else
	{
		document.getElementById('hmNotifyInfoTd').style.display="none";
	}
}

function selectUpdateSysLog(checked)
{
	if (checked)
	{
		hm.util.show('logServerSection');
	}
	else
	{
		hm.util.hide('logServerSection');
	}
}

function selectUpdateCAPWAP(checked)
{
	if (checked)
	{
		hm.util.show('capwapSection');
	}
	else
	{
		hm.util.hide('capwapSection');
	}
}

function selectUpdateEmail(checked)
{
	if (checked)
	{
		hm.util.show('emailSection');
		document.getElementById('testEmailTD').style.display="block";
	}
	else
	{
		hm.util.hide('emailSection');
		document.getElementById('testEmailTD').style.display="none";
	}
}

function selectUpdateTeacher(checked)
{
	if (checked)
	{
		hm.util.show('teacherSection');
		var enableTVProxyEl = document.getElementById("enableTVProxy");
	    if(enableTVProxyEl) {
		   	document.getElementById("enableTVProxy").disabled = !document.getElementById('enableTeacher').checked;
	    }
	    
		var enableCaseSensitiveEl = document.getElementById("enableCaseSensitive");
	    if(enableCaseSensitiveEl) {
		   	document.getElementById("enableCaseSensitive").disabled = !document.getElementById('enableTeacher').checked;
	    }
	}
	else
	{
		hm.util.hide('teacherSection');
	}
}

function selectEnableTeacher(checked)
{
   var casServerEl = document.getElementById("casServer");
   if(casServerEl) {
		document.getElementById("casServer").disabled = !checked;
   }

   var enableTVProxyEl = document.getElementById("enableTVProxy");
   document.getElementById("enableTVProxy").disabled = !checked;
   document.getElementById("enableTVProxy").checked = false;
   if(checked){
       selectEnableTVProxy(enableTVProxyEl.check);
   }else{
       selectEnableTVProxy(checked);
   }
   
   document.getElementById("enableCaseSensitive").disabled = !checked;
}

function selectEnableTVProxy(checked)
{
   var tvProxyIPEl = document.getElementById("tvProxyIP");
   if(tvProxyIPEl) {
		document.getElementById("tvProxyIP").disabled = !checked;
   }
   var tvProxyPortEl = document.getElementById("tvProxyPort");
   if(tvProxyPortEl) {
		document.getElementById("tvProxyPort").disabled = !checked;
   }
   var tvAutoProxyFileEl = document.getElementById("tvAutoProxyFile");
   if(tvProxyPortEl) {
        document.getElementById("tvAutoProxyFile").disabled = !checked;
   }
}

function selectUpdateAirtight(checked)
{
	if (checked)
	{
		hm.util.show('airtightSection');
	}
	else
	{
		hm.util.hide('airtightSection');
	}
}

function radioModeTypeChanged(value) {
	var webSenseServiceHost = document.getElementById("webSenseServiceHost");
	switch(parseInt(value)) {
	case WEBSENSEMODE_HOSTED:
		webSenseServiceHost.value='<s:text name="admin.management.webSecurity.websense.serviceHost.hosted" />';
		document.getElementById('webSenseNote').style.display="";
		break;
	case WEBSENSEMODE_HYBRID:
		webSenseServiceHost.value='<s:text name="admin.management.webSecurity.websense.serviceHost.hybrid" />';
		document.getElementById('webSenseNote').style.display="none";
		break;
	}
}

function checkAirTight(checked) {
	if(checked) {
		document.getElementById(formName + "_airTightURL").disabled = false;
		document.getElementById(formName + "_airTightUserName").disabled = false;

		if(document.getElementById("chkToggleDisplay_sge").checked) {
		document.getElementById("sgePassword").disabled = false;
		document.getElementById("airTightPasswordConfirm").disabled = false;
		} else {
		document.getElementById("sgePassword_text").disabled = false;
		document.getElementById("airTightPasswordConfirm_text").disabled = false;
		}

		document.getElementById("chkToggleDisplay_sge").disabled = false;
		document.getElementById(formName + "_airTightInterval").disabled = false;
	} else {
		document.getElementById(formName + "_airTightURL").disabled = true;
		document.getElementById(formName + "_airTightUserName").disabled = true;
		document.getElementById("sgePassword").disabled = true;
		document.getElementById("airTightPasswordConfirm").disabled = true;
		document.getElementById("sgePassword_text").disabled = true;
		document.getElementById("airTightPasswordConfirm_text").disabled = true;
		document.getElementById("chkToggleDisplay_sge").disabled = true;
		document.getElementById(formName + "_airTightInterval").disabled = true;
	}
}
function checkWebsense(checked) {
	var disabledFlag = !checked;
	document.getElementById("webSenseServiceHost").disabled = disabledFlag;
	document.getElementById("accountID").disabled = disabledFlag;
	document.getElementById("webSenseDefaultUserName").disabled = disabledFlag;
	
	if(checked) {
		if(document.getElementById("chkToggleDisplay_securityKey").checked){
			document.getElementById("securityKeyId").disabled = disabledFlag;
		} else {
			document.getElementById("securityKeyId_text").disabled = disabledFlag;
		}
		enableLink(document.getElementById("newWebsenseWhitelistsId"),"javascript:submitAction('newWebsenseWhitelists')");
		enableLink(document.getElementById("editWebsenseWhitelistsId"),"javascript:submitAction('editWebsenseWhitelists')");
		//enableLink(document.getElementById("webSenseNoteId"),"http://www.websense.com/aerohive"); /* fix bug 16199 */
		changeImage(document.getElementById("newWebsenseWhitelistsImageId"),'<s:url value="/images/new.png" />');
		changeImage(document.getElementById("editWebsenseWhitelistsImageId"),'<s:url value="/images/modify.png" />');
		addCss("newWebsenseWhitelistsId","marginBtn");
		addCss("editWebsenseWhitelistsId","marginBtn");

	} else {
		document.getElementById("securityKeyId").disabled = disabledFlag;
		document.getElementById("securityKeyId_text").disabled = disabledFlag;
		disableLink(document.getElementById("newWebsenseWhitelistsId"));
		disableLink(document.getElementById("editWebsenseWhitelistsId"));
		//disableLink(document.getElementById("webSenseNoteId")); /* fix bug 16199 */
		changeImage(document.getElementById("newWebsenseWhitelistsImageId"),'<s:url value="/images/new_disable.png" />');
		changeImage(document.getElementById("editWebsenseWhitelistsImageId"),'<s:url value="/images/modify_disable.png" />');
		removeCss("newWebsenseWhitelistsId");
		removeCss("editWebsenseWhitelistsId");
	}
	document.getElementById("chkToggleDisplay_securityKey").disabled = disabledFlag;
	document.getElementById("defaultDomain").disabled = disabledFlag;
	document.getElementById("websenseWhitelist").disabled = disabledFlag;

}

function checkOpenDNS(checked){
	var disabledFlag = !checked;
	$('#openDNSUserName').attr("disabled",disabledFlag);   
	$('#chkToggleDisplay_OpenDNSPassword').attr("disabled",disabledFlag);
	$('#openDNSCustomerName').attr("disabled",disabledFlag);
	$('#openDNSServer1IP').attr("disabled",disabledFlag);
	$('#openDNSServer2IP').attr("disabled",disabledFlag);
	$('#btn_openDNSLoginTest').attr("disabled",disabledFlag);
	if(checked){
		if($('#chkToggleDisplay_OpenDNSPassword').is(":checked")){
			$('#openDNSPasswordId').attr("disabled",disabledFlag);
		} else {
			$('#openDNSPasswordIdText').attr("disabled",disabledFlag);
		}
		$('#section_opendnsmapping').css("display","block");
		$('#note_opendnsmapping').css("display","none");
		$('#note_userswitch').css("display","none");		
	}else{
		$('#openDNSPasswordId').attr("disabled",disabledFlag);
		$('#openDNSPasswordIdText').attr("disabled",disabledFlag);
		$('#section_opendnsmapping').css("display","none");
		$('#note_userswitch').css("display","none");
		$('#note_opendnsmapping').css("display","block");
	}
}

function checkBarracuda(checked) {
	var disabledFlag = !checked;
	document.getElementById("serviceHost").disabled = disabledFlag;
	document.getElementById("servicePort").disabled = disabledFlag;
	document.getElementById("windowsDomain").disabled = disabledFlag;
	document.getElementById("barracudaDefaultUserName").disabled = disabledFlag;
	document.getElementById("barracudaWhitelist").disabled = disabledFlag;
	document.getElementById("chkToggleDisplay_authorizationKey").disabled = disabledFlag;
	if(checked) {
		if(document.getElementById("chkToggleDisplay_authorizationKey").checked){
			document.getElementById("authorizationKeyId").disabled = disabledFlag;
		} else {
			document.getElementById("authorizationKeyId_text").disabled = disabledFlag;
		}
		enableLink(document.getElementById("newBarracudaWhitelistsId"),"javascript:submitAction('newBarracudaWhitelists')");
		enableLink(document.getElementById("editBarracudaWhitelistsId"),"javascript:submitAction('editBarracudaWhitelists')");
		changeImage(document.getElementById("newBarracudaWhitelistsImageId"),'<s:url value="/images/new.png" />');
		changeImage(document.getElementById("editBarracudaWhitelistsImageId"),'<s:url value="/images/modify.png" />');
		addCss("newBarracudaWhitelistsId","marginBtn");
		addCss("editBarracudaWhitelistsId","marginBtn");

	} else {
		document.getElementById("authorizationKeyId").disabled = disabledFlag;
		document.getElementById("authorizationKeyId_text").disabled = disabledFlag;
		disableLink(document.getElementById("newBarracudaWhitelistsId"));
		disableLink(document.getElementById("editBarracudaWhitelistsId"));
		changeImage(document.getElementById("newBarracudaWhitelistsImageId"),'<s:url value="/images/new_disable.png" />');
		changeImage(document.getElementById("editBarracudaWhitelistsImageId"),'<s:url value="/images/modify_disable.png" />');
		removeCss("newBarracudaWhitelistsId");
		removeCss("editBarracudaWhitelistsId");
	}
}

function removeCss(id){
	$('#'+id).removeClass();
}

function addCss(id,cssName){
	$('#'+id).addClass(cssName);
}

function changeImage(image,src) {
	image.src=src
}

function disableLink(link) {
	link.setAttribute("disabled",true);
	link.removeAttribute('href');
}

function enableLink(link,href) {
	link.setAttribute("disabled",false);
	link.setAttribute("href",href);
}

function checkRPCServer(checked) {
	var disabledFlag = !checked;
	document.getElementById(formName + "_rpcUserName").disabled = disabledFlag;
	if(checked){
		if(document.getElementById("chkToggleDisplay_rpc").checked){
			document.getElementById("rpcPasswdId").disabled = disabledFlag;
			document.getElementById("rpcRePasswdId").disabled = disabledFlag;
		}else{
			document.getElementById("rpcPasswdId_text").disabled = disabledFlag;
			document.getElementById("rpcRePasswdId_text").disabled = disabledFlag;
		}
	}else{
			document.getElementById("rpcPasswdId").disabled = disabledFlag;
			document.getElementById("rpcRePasswdId").disabled = disabledFlag;
			document.getElementById("rpcPasswdId_text").disabled = disabledFlag;
			document.getElementById("rpcRePasswdId_text").disabled = disabledFlag;
	}

	document.getElementById("chkToggleDisplay_rpc").disabled = disabledFlag;
	document.getElementById(formName + "_rpcInterval").disabled = disabledFlag;
}

function selectUpdateRPC(checked)
{
	if (checked){
		hm.util.show('rpcSection');
	}else{
		hm.util.hide('rpcSection');
	}
}

function selectDisableLog(checked)
{
	document.getElementById("specialEntry").disabled=true;
	document.getElementById("anyEntry").disabled=true;
	document.getElementById("addBtn").disabled=true;
	document.getElementById("removeBtn").disabled=true;
	document.getElementById("applyBtn").disabled=true;
	document.getElementById("cancelBtn").disabled=true;
	document.getElementById("subnetTable").disabled=true;
}

function selectEnableLog(checked)
{
	document.getElementById("specialEntry").disabled=false;
	document.getElementById("anyEntry").disabled=false;
	if (document.getElementById("specialEntry").checked)
	{
		document.getElementById("addBtn").disabled=false;
		document.getElementById("removeBtn").disabled=false;
		document.getElementById("applyBtn").disabled=false;
		document.getElementById("cancelBtn").disabled=false;
		document.getElementById("subnetTable").disabled=false;
	}
}

function selectAnyEntry(checked)
{
	document.getElementById("addBtn").disabled=true;
	document.getElementById("removeBtn").disabled=true;
	document.getElementById("applyBtn").disabled=true;
	document.getElementById("cancelBtn").disabled=true;
	document.getElementById("subnetTable").disabled=true;
}

function selectSpecialEntry(checked)
{
	document.getElementById("addBtn").disabled=false;
	document.getElementById("removeBtn").disabled=false;
	document.getElementById("applyBtn").disabled=false;
	document.getElementById("cancelBtn").disabled=false;
	document.getElementById("subnetTable").disabled=false;
}

function showCreateSection()
{
	document.getElementById("addBtn").style.display = "none";
	document.getElementById("removeBtn").style.display = "none";
	hm.util.show('createButton');
	hm.util.show('createSection');
	// to fix column overlap issue on certain browsers
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection()
{
	document.getElementById("addBtn").style.display = "block";
	document.getElementById("removeBtn").style.display = "block";
	hm.util.hide('createButton');
	hm.util.hide('createSection');
}

function selectedDefaultPassPhrase(checked)
{
	if (checked)
	{
		document.getElementById("capwapPass").disabled = true;
		document.getElementById("confirmCapwapPass").disabled = true;
		document.getElementById("capwapPass_text").disabled = true;
		document.getElementById("confirmCapwapPass_text").disabled = true;
		document.getElementById("chkToggleDisplay_capwap").disabled = true;
	}
	else
	{
		document.getElementById("chkToggleDisplay_capwap").disabled = false;
		var hidePassword = document.getElementById("chkToggleDisplay_capwap").checked;

		document.getElementById("capwapPass").disabled = !hidePassword;
		document.getElementById("confirmCapwapPass").disabled = !hidePassword;

		document.getElementById("capwapPass_text").disabled = hidePassword;
		document.getElementById("confirmCapwapPass_text").disabled = hidePassword;

	}
}

function testSendEmail()
{
	if (validate("testEmail"))
	{
		document.getElementById(formName + "_operation").value = 'testEmail';
		var formObject = document.getElementById(formName);
		YAHOO.util.Connect.setForm(formObject);

		var url = "<s:url action='hmServices' includeParams='none' />";

		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:testEmailResult,failure:testEmailRequestFailed,timeout: 60000}, null);
		if(waitingPanel != null)
		{
			waitingPanel.show();
		}
	}
}

var testEmailResult = function(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var response = " + o.responseText);
	if(response.result)
	{
		showInfoDialog(response.rspMessage.valueOf());
	}
	else
	{
		if(warnDialog != null)
		{
			warnDialog.cfg.setProperty('text', response.rspMessage.valueOf());
			warnDialog.show();
		}
	}
}

var testEmailRequestFailed = function(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	if(warnDialog != null)
	{
		warnDialog.cfg.setProperty('text', "The operation timed out.");
		warnDialog.show();
	}
}

function selectedEnableNotfiy(checked)
{
	if (checked)
	{
		enableComponent("enabledCritical");
		enableComponent("enabledMajor");
		enableComponent("enabledMinor");
		enableComponent("enabledInfo");
		enableComponent("enabledClear");
		enableComponent("enabled_event");

		initTableCellEnabled();
	}
	else
	{
		disableComponent("enabledCritical");
		disableComponent("enabledMajor");
		disableComponent("enabledMinor");
		disableComponent("enabledInfo");
		disableComponent("enabledClear");
		disableComponent("enabled_event");
	}
}

function initTableCellEnabled()
{
	disableComponent("enabledCritical");
	disableComponent("enabledMajor");
	disableComponent("enabledMinor");
	disableComponent("enabledInfo");
	disableComponent("enabledClear");

	document.getElementById("enabled_critical_Hardware_Radio").disabled = false;
	document.getElementById("enabled_major_Hardware_Radio").disabled = false;
	document.getElementById("enabled_minor_Hardware_Radio").disabled = false;
	document.getElementById("enabled_critical_CAPWAP").disabled = false;
	document.getElementById("enabled_major_CAPWAP").disabled = false;
	document.getElementById("enabled_minor_CAPWAP").disabled = false;
	document.getElementById("enabled_clear_CAPWAP").disabled = false;
	document.getElementById("enabled_major_Configuration").disabled = false;
	document.getElementById("enabled_critical_License Expiration").disabled = false;
	document.getElementById("enabled_major_License Expiration").disabled = false;
	document.getElementById("enabled_minor_License Expiration").disabled = false;
	document.getElementById("enabled_major_User Database").disabled = false;
	document.getElementById("enabled_clear_User Database").disabled = false;
	document.getElementById("enabled_major_Spoofed BSSIDs").disabled = false;
	document.getElementById("enabled_critical_TCA Alarm").disabled = false;
	document.getElementById("enabled_clear_TCA Alarm").disabled = false;
	document.getElementById("enabled_major_System").disabled = false;
	document.getElementById("enabled_clear_System").disabled = false;
	//document.getElementById("enabled_minor_Client").disabled = false;//hidden connection alarming feature,Jan.31 2013
	//document.getElementById("enabled_clear_Client").disabled = false;//hidden connection alarming feature

	uncheckedItems();
}
/**
 * For the version upgrade bug from 3.4r4 to 4.0r1
 * see Bug 13281
 */
function uncheckedItems() {
	var elementIds = ["enabled_info_Hardware_Radio", "enabled_clear_Hardware_Radio",//For Hardware Radio
	                  "enabled_info_CAPWAP",//For CAPWAP
	                  "enabled_critical_Configuration", "enabled_minor_Configuration", "enabled_info_Configuration", "enabled_clear_Configuration",//For Configuration
	                  "enabled_info_License Expiration", "enabled_clear_License Expiration",//For License Expiration
	                  "enabled_critical_User Database", "enabled_minor_User Database", "enabled_info_User Database",//For User Database
	                  "enabled_critical_Spoofed BSSIDs", "enabled_minor_Spoofed BSSIDs", "enabled_info_Spoofed BSSIDs", "enabled_clear_Spoofed BSSIDs",//For Configuration
	                  "enabled_major_TCA Alarm", "enabled_minor_TCA Alarm", "enabled_info_TCA Alarm",//For TCA Alarm
	                  "enabled_critical_System", "enabled_minor_System", "enabled_info_System"//For System
	                  ];
	for(var index=elementIds.length-1; index>=0; index--) {
		var elementId = elementIds[index];
		if(Get(elementId) && Get(elementId).checked) {
			Get(elementId).checked = false;
		}
	}
}

function selectSupportAuth(checked)
{
	if (checked)
	{
		hm.util.show('authSection');
	}
	else
	{
		hm.util.hide('authSection');
	}
}

function selectSmtpEncryption(checked)
{
	if (checked)
	{
		hm.util.show('smtpEncrySection');
		if (document.getElementById("ssl").checked)
		{
			document.getElementById('smtpPort').value="465";
		} else {
			document.getElementById('smtpPort').value="587";
		}
	} else {
		hm.util.hide('smtpEncrySection');
		document.getElementById('smtpPort').value="25";
	}
}

function selectSSL(checked)
{
	document.getElementById('smtpPort').value="465";
}

function selectTLS(checked)
{
	document.getElementById('smtpPort').value="587";
}

function disableComponent(name)
{
	var components = document.getElementsByName(name);
	for (i=0; i<components.length; ++ i)
	{
		components[i].disabled = true;
	}
}

function enableComponent(name)
{
	var components = document.getElementsByName(name);
	for (i=0; i<components.length; ++ i)
	{
		components[i].disabled = false;
	}
}

function validateAirTight() {
	var element = document.getElementById("enableAirtight");

	if(!element.checked) {
		return true;
	}

	// server url
	element = document.getElementById(formName + "_airTightURL");
	var value = element.value;

	if(value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.ip" /></s:param></s:text>');
		element.focus();
		return false;
	}

	/* if ( !(value.indexOf('https') == 0)
		|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){
    	hm.util.reportFieldError(element,
	    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="hm.airtight.url" /></s:param></s:text>');
	    element.focus();
	    element.select();
		return false;

    }  */

	// user name
	element = document.getElementById(formName + "_airTightUserName");

	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.userName" /></s:param></s:text>');
		element.focus();
		return false;
	}

	var elementC;

	if (document.getElementById("chkToggleDisplay_sge").checked)
	{
		element = document.getElementById("sgePassword");
		elementC = document.getElementById("airTightPasswordConfirm");

	}
	else
	{
		element = document.getElementById("sgePassword_text");
		elementC = document.getElementById("airTightPasswordConfirm_text");
	}

	// password
	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.password" /></s:param></s:text>');
		element.focus();
		return false;
	}

	// password confirmation
	if(elementC.value.trim().length == 0) {
		hm.util.reportFieldError(elementC, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.password.confirm" /></s:param></s:text>');
		elementC.focus();
		return false;
	}

	if(element.value != elementC.value) {
		hm.util.reportFieldError(element, '<s:text name="hm.airtight.password.mismatch" />');
		element.focus();
		return false;
	}

	// timeout
	element = document.getElementById(formName + "_airTightInterval");

	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.timeout" /></s:param></s:text>');
		element.focus();
		return false;
	}

	var message = hm.util.validateIntegerRange(element.value, '<s:text name="hm.airtight.timeout" />',
     									               <s:property value="%{minInterval}" />,
                                                       <s:property value="%{maxInterval}" />);
    if (message != null) {
    	hm.util.reportFieldError(element, message);
        element.focus();
        return false;
     }

	return true;
}

function validateRPC() {
	if(!document.getElementById("enableRPCServerId").checked){
		return true;
	}

	// user name
	var userName = document.getElementById(formName + "_rpcUserName");

	if(userName.value.trim().length == 0) {
		hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.userName" /></s:param></s:text>');
		userName.focus();
		return false;
	}

	var passwd;
	var rePasswd;

	if (document.getElementById("chkToggleDisplay_rpc").checked){
		passwd = document.getElementById("rpcPasswdId");
		rePasswd = document.getElementById("rpcRePasswdId");
	}else{
		passwd = document.getElementById("rpcPasswdId_text");
		rePasswd = document.getElementById("rpcRePasswdId_text");
	}
	// password
	if(passwd.value.trim().length == 0) {
		hm.util.reportFieldError(passwd, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.password" /></s:param></s:text>');
		passwd.focus();
		return false;
	}
	if(passwd.value.trim().length > 32){
		hm.util.reportFieldError(passwd, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.airtight.password" /></s:param></s:text>');
		passwd.focus();
	    return false;
	}

	// password confirmation
	if(rePasswd.value.trim().length == 0) {
		hm.util.reportFieldError(rePasswd, '<s:text name="error.requiredField"><s:param><s:text name="hm.airtight.password.confirm" /></s:param></s:text>');
		rePasswd.focus();
		return false;
	}
	if(rePasswd.value.trim().length > 32){
		hm.util.reportFieldError(rePasswd, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.airtight.password.confirm" /></s:param></s:text>');
		rePasswd.focus();
	    return false;
	}
	if(passwd.value != rePasswd.value) {
		hm.util.reportFieldError(rePasswd, '<s:text name="hm.airtight.password.mismatch" />');
		rePasswd.focus();
		return false;
	}

	// timeout
	var timeout = document.getElementById(formName + "_rpcInterval");

	if(timeout.value.trim().length == 0) {
		hm.util.reportFieldError(timeout, '<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.rpc.timeout" /></s:param></s:text>');
		timeout.focus();
		return false;
	}
	message = hm.util.validateIntegerRange(timeout.value, '<s:text name="home.hmSettings.rpc.timeout" />', <%=RemoteProcessCallSettings.MIN_OVERTIME%>, <%=RemoteProcessCallSettings.MAX_OVERTIME%>);
	if (message != null) {
	    hm.util.reportFieldError(timeout, message);
	    timeout.focus();
	    return false;
	}
	return true;
}
function selectClientManagementUpdate(value)
{
	if(value){
		document.getElementById("generalSettingsId").style.display = "block";
		/* document.getElementById("caImportManagementId").style.display = "block"; */
		document.getElementById("troubleshootingToolId").style.display = "block";
		document.getElementById("contentId").style.display = "block";
		/* document.getElementById("content2Id").style.display = "block"; */
		/*if(document.getElementById("enabledClientProfile").checked){
			document.getElementById("cidPolicyId").style.display = "block";
			/* document.getElementById("caImportManagementId").style.display = "block"; */
		/*}else{
			document.getElementById("cidPolicyId").style.display = "none";
			/* document.getElementById("caImportManagementId").style.display = "none"; */
		/*}*/
		/* customizeCA(document.getElementById("enableCustomizeCA").checked); */
	}else{
		document.getElementById("generalSettingsId").style.display = "none";
		/* document.getElementById("caImportManagementId").style.display = "none"; */
		document.getElementById("troubleshootingToolId").style.display = "none";
		document.getElementById("contentId").style.display = "none";
		/* document.getElementById("content2Id").style.display = "none"; */
	}
}

/* function customizeCA(value)
{
	if(value){
		document.getElementById("caFileId").style.display = "";
		document.getElementById("caKeyFileId").style.display = "";
		document.getElementById("customizeCANoteId").style.display = "";
		document.getElementById("customizeKeyNoteId").style.display = "";
	}else{
		document.getElementById("caFileId").style.display = "none";
		document.getElementById("caKeyFileId").style.display = "none";
		document.getElementById("customizeCANoteId").style.display = "none";
		document.getElementById("customizeKeyNoteId").style.display = "none";
	}
} */

function updateL7SettingCheckbox(value) {
	if(value){
		document.getElementById("enableSystemL7SwitchTr").style.display = "block";
	}else{
		document.getElementById("enableSystemL7SwitchTr").style.display = "none";
		//document.getElementById("updateL7Setting").checked = false ;
	}
}

function showCidPolicy(value)
{
	if(value){
		document.getElementById("cidPolicyId").style.display = "block";
		/* document.getElementById("caImportManagementId").style.display = "block" */
	}else{
		document.getElementById("cidPolicyId").style.display = "none";
		document.getElementById("enabledCidPolicy").checked = false ;
		/* document.getElementById("caImportManagementId").style.display = "none";
		document.getElementById("enableCustomizeCA").checked = false ; */
	}
}

</script>

<div id="content">
	<s:form action="hmServices" enctype="multipart/form-data" method="POST">
		<s:hidden name="ipAddressId" />
		<s:hidden name="whiteListId" />
		<s:hidden name="resetSSIDs" />
		<s:hidden name="enabledProxy" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ok" value="Update"
								class="button" onClick="submitAction('update');"
								<s:property value="writeDisabled" />></td>
							<td id="testEmailTD" style="display: none"><input
								type="button" name="testEmail" value="Email Test"
								class="button long" onClick="testSendEmail();"
								<s:property value="writeDisabled" />></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td style="padding-top: 5px;">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="800px">
						<tr>
							<td height="10">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
								type="password">
							</td>
						</tr>
						<!-- AirTight SpectraGuard Enterprise (SGE) Integration Settings -->
						<s:if test="%{!oEMSystem}">
							<tr>
								<td
									style="padding-left: 10px;display:<s:property value="%{hide4VHM}"/>">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="400px"><s:checkbox
													name="updateAirtight" id="updateAirtight"
													onclick="selectUpdateAirtight(this.checked);" /> <label>
													<s:text name="home.hmSettings.airTight" />
											</label></td>
										</tr>
										<tr
											style="display:<s:property value="%{showAirtightSection}"/>"
											id="airtightSection">
											<td style="padding-left: 35px;">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td><s:checkbox name="enableAirtight"
																id="enableAirtight"
																onclick="checkAirTight(this.checked);" /> <label>
																<s:text name="hm.airtight.enable" />
														</label></td>
													</tr>
													<tr>
														<td height="6px"></td>
													</tr>
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<%-- <tr>
																<td>
																</td>
																<td>
																	<FONT color="blue"><s:text name="hm.airtight.url.note" /></FONT>
																</td>
															</tr> --%>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.ip" /></td>
																	<td><s:textfield name="airTightURL"
																			cssStyle="width: 200px;"
																			maxlength="%{serverUrlLength}"
																			disabled="%{!enableAirtight}" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.userName" /></td>
																	<td><s:textfield name="airTightUserName"
																			maxlength="%{userNameLength}"
																			disabled="%{!enableAirtight}" /> &nbsp; <s:text
																			name="hm.airtight.userName.range" /></td>
																</tr>
																<tr id="sgePassword_tr">
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.password" /></td>
																	<td><s:password id="sgePassword"
																			name="airTightPassword" value="%{airTightPassword}"
																			maxlength="%{passwordLength}"
																			disabled="%{!enableAirtight}" showPassword="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		&nbsp; <s:text name="hm.airtight.password.range" /></td>
																</tr>
																<tr id="sgePassword_text_tr" style="display: none">
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.password" /></td>
																	<td><s:textfield id="sgePassword_text"
																			cssStyle="display:none;" name="airTightPassword"
																			value="%{airTightPassword}"
																			maxlength="%{passwordLength}" disabled="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		&nbsp; <s:text name="hm.airtight.password.range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.password.confirm" /></td>
																	<td><s:password id="airTightPasswordConfirm"
																			value="%{airTightPassword}"
																			maxlength="%{passwordLength}"
																			disabled="%{!enableAirtight}" showPassword="true" />
																		<s:textfield id="airTightPasswordConfirm_text"
																			cssStyle="display:none;" value="%{airTightPassword}"
																			maxlength="%{passwordLength}" disabled="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	</td>
																</tr>
																<tr>
																	<td></td>
																	<td>
																		<table>
																			<tr>
																				<td><s:checkbox id="chkToggleDisplay_sge"
																						name="ignore" value="true"
																						disabled="%{!enableAirtight}"
																						onclick="hm.util.toggleObscurePassword(this.checked,
																						['sgePassword','airTightPasswordConfirm'],
																						['sgePassword_text','airTightPasswordConfirm_text'],'sgePassword_tr','sgePassword_text_tr');" />
																				</td>
																				<td><s:text name="admin.user.obscurePassword" />
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="150px"><s:text
																			name="hm.airtight.timeout" /></td>
																	<td><s:textfield name="airTightInterval"
																			maxlength="4"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"
																			disabled="%{!enableAirtight}" /> &nbsp; <s:if
																			test="%{debugMode}">
																			<s:text name="hm.airtight.timeout.range.debug" />
																		</s:if> <s:else>
																			<s:text name="hm.airtight.timeout.range.release" />
																		</s:else></td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.clientID" /></td>
																	<td><s:property value="%{airTightClientID}" /></td>
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
						</s:if>
						<!-- Application Visibility and Control Settings -->
						<s:if test="%{isInHomeDomain}">
							<tr>
								<td style="padding-left: 10px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="300px"><s:checkbox
													id="updateL7Setting" name="updateL7Setting"
													onclick="updateL7SettingCheckbox(this.checked);" /> <label>
													<s:text name="home.hmSettings.L7Setting.updateL7Setting"></s:text>
											</label></td>
										</tr>
										<tr id="enableSystemL7SwitchTr" style="display: none">
											<td style="padding-left: 30px;"><s:checkbox
													id="enableSystemL7Switch" name="enableSystemL7Switch"
													onclick="enableSystemL7Switch(this.checked)" />
													<s:text	name="home.hmSettings.L7Setting.enableSystemL7Switch"></s:text></td>
											
										</tr>
									</table>
								</td>
							</tr>
						</s:if>
						<!-- Barracuda Server Settings -->
						<s:if test="%{fullMode}">
							<tr>
								<td style="padding-left: 10px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="200px"><s:checkbox
													name="updateBarracudaServer" id="updateBarracudaServer"
													onclick="selectUpdateBarracudaServer(this.checked);" /> <label>
													<s:text name="admin.management.updateBarracudaServer" />
											</label></td>
										</tr>
										<tr
											style="display:<s:property value="%{hideBarracudaServer}"/>"
											id="barracudaServerSection">
											<td style="padding-left: 20px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td colspan="2"><s:checkbox name="enableBarracuda"
																id="enableBarracudaId"
																onclick="checkBarracuda(this.checked);" /> <label>
																<s:text
																	name="admin.management.webSecurity.enablebarracuda" />
														</label></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td class="labelT1" width="180"
															style="padding-left: 15px;"><label> <s:text
																	name="admin.management.updateBarracudaServer.authorizationKey" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:password name="authorizationKey"
																id="authorizationKeyId" value="%{authorizationKey}"
																maxlength="40" size="60"
																onkeypress="return hm.util.keyPressPermit(event,'password');"
																showPassword="true" /> <s:textfield
																name="authorizationKeyText" id="authorizationKeyId_text"
																cssStyle="display:none;" value="%{authorizationKey}"
																maxlength="40" size="60"
																onkeypress="return hm.util.keyPressPermit(event,'password');" />
															<s:text
																name="admin.management.updateBarracudaServer.authorizationKeyRange" />
														</td>
													</tr>
													<tr>
														<td></td>
														<td>
															<table>
																<tr>
																	<td><s:checkbox
																			id="chkToggleDisplay_authorizationKey"
																			name="chkAuthorizationKey" value="true"
																			disabled="%{!enableBarracuda}"
																			onclick="hm.util.toggleObscurePassword(this.checked,
																				['authorizationKeyId'],
																				['authorizationKeyId_text']);" />
																	</td>
																	<td><s:text
																			name="admin.management.updateBarracudaServer.authorizationKey.obscure" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="180"
															style="padding-left: 15px;"><label> <s:text
																	name="admin.management.webSecurity.serviceHost" /> <font
																color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:textfield id="serviceHost" name="serviceHost"
																size="40" maxlength="64" disabled="%{!enableBarracuda}" />
															<s:text
																name="admin.management.webSecurity.serviceHost.range" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text name="admin.management.webSecurity.servicePort" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:textfield id="servicePort" name="servicePort"
																maxlength="5" size="40" disabled="%{!enableBarracuda}"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />
															<s:text name="admin.management.webSecurity.range" /></td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text
																	name="admin.management.updateBarracudaServer.windowsDomain" />
														</label></td>
														<td><s:textfield id="windowsDomain"
																name="windowsDomain" size="40"
																disabled="%{!enableBarracuda}" /></td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text
																	name="admin.management.webSecurity.webSenseDefaultUserName" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:textfield id="barracudaDefaultUserName"
																name="barracudaDefaultUserName" size="40" maxlength="32"
																disabled="%{!enableBarracuda}" /> <s:text
																name="admin.management.webSecurity.webSenseDefaultUserName.range" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text name="admin.management.webSecurity.whitelist" />
														</label></td>
														<td><s:select list="%{barracudaWhitelists}"
																listKey="id" disabled="%{!enableBarracuda}"
																listValue="value" name="barracudaWhitelist"
																id="barracudaWhitelist" cssStyle="width: 200px;" /> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
															</s:if> <s:else>
																<a class="marginBtn" id="newBarracudaWhitelistsId"
																	href="javascript:submitAction('newBarracudaWhitelists')"><img
																	id="newBarracudaWhitelistsImageId" class="dinl"
																	src="<s:url value="/images/new.png" />" width="16"
																	height="16" alt="New" title="New" /></a>
															</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
															</s:if> <s:else>
																<a class="marginBtn" id="editBarracudaWhitelistsId"
																	href="javascript:submitAction('editBarracudaWhitelists')"><img
																	id="editBarracudaWhitelistsImageId" class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16"
																	height="16" alt="Modify" title="Modify" /></a>
															</s:else></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</s:if>
						<!-- CAPWAP Server Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="300px"><s:checkbox
												name="updateCAPWAP" id="updateCAPWAP"
												onclick="selectUpdateCAPWAP(this.checked);" /> <label>
												<s:text name="admin.management.updateCapwap" />
										</label></td>
									</tr>
									<tr style="display: none" id="capwapSection">
										<td style="padding-left: 20px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td class="labelT1" width="180" style="padding-left: 15px;">
														<label> <s:text
																name="admin.capwap.primaryCapwapIP" />
													</label>
													</td>
													<td><s:textfield id="primaryCapwapIP"
															name="primaryCapwapIP" size="32" maxlength="32"
															onkeypress="return hm.util.keyPressPermit(event,'name');" />
														<s:text name="admin.capwap.serverNameRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="180" style="padding-left: 15px;">
														<label> <s:text name="admin.capwap.backupCapwapIP" />
													</label>
													</td>
													<td><s:textfield id="backupCapwapIP"
															name="backupCapwapIP" size="32" maxlength="32"
															onkeypress="return hm.util.keyPressPermit(event,'name');" />
														<s:text name="admin.capwap.serverNameRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="180" style="padding-left: 15px;">
														<label> <s:text name="admin.capwap.udpPort" /> <font
															color="red"><s:text name="*" /> </font>
													</label>
													</td>
													<td><s:textfield id="capwapUdpPort"
															name="capwapUdpPort" maxlength="5"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															size="32" /> <s:text name="admin.capwap.udpPortRange" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 15px;"><label>
															<s:text name="admin.capwap.timeOut" /><font color="red"><s:text
																	name="*" /> </font>
													</label></td>
													<td><s:textfield id="capwapTimeOut"
															name="capwapTimeOut"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															maxlength="3" size="32" /> <s:text
															name="admin.capwap.timeOutRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 15px;"><label>
															<s:text name="admin.capwap.deadInterval" /> <font
															color="red"><s:text name="*" /> </font>
													</label></td>
													<td><s:textfield id="deadInterval" name="deadInterval"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															maxlength="3" size="32" /> <s:text
															name="admin.capwap.deadIntervalRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 15px;"><label>
															<s:text name="admin.capwap.trapFilterInterval" /> <font
															color="red"><s:text name="*" /> </font>
													</label></td>
													<td><s:textfield id="trapFilterInterval"
															name="trapFilterInterval"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															maxlength="2" size="32" /> <s:text
															name="admin.capwap.trapIntervalRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 15px;" colspan="2">
														<s:checkbox name="enableRollback" id="enableRollback" />
														<label> <s:text name="admin.capwap.enableRollback" />
													</label>
													</td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td colspan="2" style="padding-left: 15px;">
														<div id="passPhraseSection">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																		<fieldset style="width: 600px">
																			<legend>
																				<s:text name="admin.capwap.passPhrase" />
																			</legend>
																			<div>
																				<table cellspacing="0" cellpadding="0" border="0"
																					width="100%">
																					<tr>
																						<td class="labelT1" colspan="2"
																							style="padding-left: 15px;"><s:checkbox
																								name="defaultPassPhrase" id="cbPassPhrase"
																								onclick="selectedDefaultPassPhrase(this.checked)"
																								disabled="%{writeDisable4Struts}" /> <label>
																								<s:text name="admin.capwap.restoreDefaultPass" />
																						</label></td>
																					</tr>
																					<tr>
																						<td width="160" class="labelT1"><label>
																								<s:text name="admin.capwap.newPassPhrase" /> <font
																								color="red"><s:text name="*" /> </font>
																						</label></td>
																						<td><s:password id="capwapPass"
																								name="newPassPhrase" size="32" maxlength="32"
																								disabled="%{disablePassPhrase}"
																								showPassword="true"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:textfield id="capwapPass_text"
																								name="newPassPhrase" size="32" maxlength="32"
																								disabled="true" value=""
																								onkeypress="return hm.util.keyPressPermit(event,'password');"
																								cssStyle="display:none" /> <s:text
																								name="admin.capwap.phraseRange" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1"><label> <s:text
																									name="admin.capwap.confirmPassPhrase" /> <font
																								color="red"><s:text name="*" /> </font>
																						</label></td>
																						<td><s:password id="confirmCapwapPass"
																								name="confirmPassPhrase" size="32"
																								maxlength="32" disabled="%{disablePassPhrase}"
																								showPassword="true"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:textfield id="confirmCapwapPass_text"
																								name="confirmPassPhrase" size="32"
																								maxlength="32" disabled="true" value=""
																								onkeypress="return hm.util.keyPressPermit(event,'password');"
																								cssStyle="display:none" /> <s:text
																								name="admin.capwap.phraseRange" /></td>
																					</tr>
																					<tr>
																						<td>&nbsp;</td>
																						<td>
																							<table border="0" cellspacing="0" cellpadding="0">
																								<tr>
																									<td><s:checkbox
																											id="chkToggleDisplay_capwap" name="ignore"
																											value="true" disabled="%{disablePassPhrase}"
																											onclick="hm.util.toggleObscurePassword(this.checked,['capwapPass','confirmCapwapPass'],['capwapPass_text','confirmCapwapPass_text']);" />
																									</td>
																									<td><s:text
																											name="admin.user.obscurePassword" /></td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</fieldset>
																	</td>
																</tr>
															</table>
														</div>
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
						<!-- Client Management Settings -->
						<tr style="display: <s:property value="hide4ClientManagement" />">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												id="onboardUpdate" name="onboardUpdate"
												onclick="selectClientManagementUpdate(this.checked);" /> <label>
												<s:text name="home.hmSettings.clientManagement.setting"></s:text>
										</label></td>
									</tr>
									<tr id="generalSettingsId" style='display:<s:property value="onboardUpdate?'':'none'"/>'>
										<td style="padding-left: 30px">
											<fieldset style="width: 600px">
												<legend>
													<s:text
														name="home.hmSettings.clientManagement.generalSetting"></s:text>
												</legend>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr id="onboardSystemId">
																	<td class="labelT1"><s:checkbox
																			id="enabledClientProfile" name="enabledClientProfile"
																			disabled="%{disableClientManagement}"/> <label><s:text
																				name="home.hmSettings.clientManagement.enabledClientManagement"></s:text></label></td>
																</tr>
																<%-- <tr id="cidPolicyId" style="display:">
																	<td class="labelT1"><s:checkbox
																			id="enabledCidPolicy"
																			name="enabledCidPolicyEnforcement" /> <label><s:text
																				name="home.hmSettings.clientManagement.enabledCidPolicyEnforcement"></s:text></label></td>
																</tr> --%>
															</table>
														</td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
									<tr id="contentId" style="display: none">
										<td height="10"></td>
									</tr>
									<!--  
						            <tr>
						                <td style="padding-left: 30px">
						                    <fieldset style="width: 600px">
						                        <legend>
						                            Client HTTP Proxy Setting
						                        </legend>
						                        <table cellspacing="0" cellpadding="0" border="0" width="100%">
						                         
						                        </table>
						                    </fieldset>
						                </td>
						            </tr>
						            <tr>
							           <td height="10"></td>
						            </tr>
						            -->
								<!-- <tr id="caImportManagementId" style="display: none">
										<td style="padding-left: 30px">
											<fieldset style="width: 600px">
												<legend>
													<s:text
														name="home.hmSettings.clientManagement.certificateManagement"></s:text>
												</legend>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td class="labelT1"><s:checkbox
																			id="enableCustomizeCA" name="enableCustomizeCA" onclick="customizeCA(this.checked)"/> <label><s:text
																				name="home.hmSettings.clientManagement.useCustomerCA"></s:text></label></td>
																</tr>
																<tr>
																	<td style="padding-left:22px">
																		<table cellspacing="0" cellpadding="0" border="0"
																			width="100%">
																			<tr id="caFileId">
																				<td class="labelT1" width="120px"><label>
																						<s:text
																							name="home.hmSettings.clientManagement.caFile"></s:text>
																				</label></td>
																				<td><s:file
																						id="certificateFile" name="upload" accept="application/x-x509-ca-cert" size="40" /></td>
																			</tr>
																			<tr id="customizeCANoteId" class="noteInfo">
																			        <td colspan="2" style="padding-left: 10px">
																			             <font color="blue"><s:text name="home.hmSettings.clientManagement.useCustomerCA.note"></s:text></font>
																			        </td>
																			</tr>
																		     <tr id="caKeyFileId">
																				<td class="labelT1" width="120px"><label>
																						<s:text
																							name="home.hmSettings.clientManagement.caKeyFile"></s:text>
																				</label></td>
																				<td><s:file
																						id="certificateKeyFile" name="upload" accept="application/x-x509-ca-cert" size="40" /></td>
																			</tr>
																			<tr id="customizeKeyNoteId" class="noteInfo">
																			
																			       <td colspan="2" style="padding-left: 10px">
																			              <font color="blue"><s:text name="home.hmSettings.clientManagement.useCustomerKey.note"></s:text></font>
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
									<tr id="content2Id" style="display: none">
										<td height="10"></td>
									</tr>-->
									<tr id="troubleshootingToolId" style="display: none">
										<td style="padding-left: 30px">
											<fieldset style="width: 600px">
												<legend>
													<s:text
														name="home.hmSettings.clientManagement.troubleshooting"></s:text>
												</legend>
												<table cellspacing="0" cellpadding="0" border="0"
													width="100%">
													<tr>
														<td style="padding-left: 10px">
															<div id="troubleshootingId" style="display: none">
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%">
																	<tr>
																		<td height="10"></td>
																	</tr>
																	<tr>
																		<td class="noteInfo" id="trbInfoMessage"></td>
																	</tr>
																	<tr>
																		<td class="noteError" id="trbErrorMessage"></td>
																	</tr>
																</table>
															</div>
														</td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td style="text-align:center;"><input type="button" name="ignore"
															value="Test" class="button"
															onClick="detectTrouble();" style="width: 90px;">
														</td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- Customer ID Retrieval -->
						<s:if test="%{!hMOnline && isInHomeDomain}">
							<tr>
								<td style="padding-left: 10px;">
									<table style="width: 100%; border-width: 0; border-spacing: 0;">
										<tr>
											<td class="labelT1">
												<s:checkbox id="updateIDM" name="updateIDM" onclick="displayIDMServiceSecion('idmRetrieveSection');"/>
												<label for="updateIDM"><s:text name="home.hmSettings.retrieve.customer.id" /></label></td>
										</tr>
										<tr id="idmRetrieveSection" style="display: none;">
											<td style="padding-left: 25px;">
												<table
													style="width: 100%; border-width: 0; border-spacing: 0;">
													<tr>
														<td class="labelT1" width="140px"><label> <s:text
																	name="admin.user.emailAddress" /><font color="red"><s:text
																		name="*" /> </font>
														</label></td>
														<td><s:textfield id="idmEmail" name="idmUserEmail"
																size="48" maxlength="128" /> <s:text
																name="admin.email.address.range" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label> <s:text
																	name="admin.user.password" /><font color="red"><s:text
																		name="*" /> </font>
														</label></td>
														<td><s:password id="idmUserPassword"
																name="idmUserPassword" size="24" maxlength="32" /> <s:textfield
																id="idmUserPassword_text" name="idmUserPassword"
																disabled="true" size="24" maxlength="32"
																cssStyle="display:none" /> <s:text
																name="admin.user.password.ranger" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label> <s:text
																	name="admin.user.password.confirm" /> <font
																color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:password id="idmUserPasswordConfirm"
																name="idmUserPasswordConfirm" size="24"
																maxlength="%{passwdLength}" /> <s:textfield
																id="idmUserPasswordConfirm_text"
																name="idmUserPasswordConfirm" disabled="true" size="24"
																maxlength="%{passwdLength}" cssStyle="display:none" />
															<s:text name="admin.user.password.ranger" /></td>
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:checkbox id="idmChkToggleDisplay"
																			name="ignore" value="true"
																			disabled="%{writeDisable4Struts}"
																			onclick="hm.util.toggleObscurePassword(this.checked,['idmUserPassword','idmUserPasswordConfirm'],['idmUserPassword_text','idmUserPasswordConfirm_text']);" />
																	</td>
																	<td><s:text name="admin.user.obscurePassword" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<s:if test="%{enabledProxy}">
													<tr>
													   <td class="labelT1" colspan="2" style="padding-left: 5px;"><s:checkbox name="enableProxyIdm" id="enabledProxy4IDM"/><label><s:text name="guadalupe_06.home.idm.retrieveCustomerId.label"/></label></td>
													</tr>
													</s:if>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td style="padding-left: 5px;" colspan="2">
															<div id="idmNoteSection" style="display: none">
																<table border="0" cellspacing="0" cellpadding="0"
																	class="note">
																	<tr>
																		<td class="noteInfo" id="infoRow"></td>
																	</tr>
																	<tr>
																		<td class="noteError" id="errorRow"></td>
																	</tr>
																</table>
															</div>
														</td>
													</tr>
													<tr>
														<td />
														<td><input type="button" name="ignore"
															value="Retrieve" class="button"
															onClick="getCustomerId();" style="width: 90px;"
															<s:property value="writeDisabled" />></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</s:if>
						<!-- Email Service Settings -->
						<tr>
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												name="updateEmail" id="updateEmail"
												onclick="selectUpdateEmail(this.checked);" /> <label>
												<s:text name="admin.management.updateEmail" />
										</label></td>
									</tr>
									<tr style="display: none" id="emailSection">
										<td style="padding-left: 20px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table width="100%" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td>
																	<table cellspacing="0" cellpadding="0" border="0"
																		width="100%">
																		<tr>
																			<td class="labelT1" width="130px"><label>
																					<s:text name="admin.emailNotify.smtpServer" />
																			</label></td>
																			<td><s:textfield id="smtpServer"
																					name="smtpServer" maxlength="256" size="64" /></td>
																		</tr>
																		<!-- server port -->
																		<tr>
																			<td class="labelT1"><s:text
																					name="admin.emailNotfiy.smtpPort" /></td>
																			<td width="200px"><s:textfield id="smtpPort"
																					name="smtpPort"
																					onkeypress="return hm.util.keyPressPermit(event,'ten');"
																					maxlength="5" size="10" /> <s:text
																					name="admin.emailNotfiy.smtpPort.range" /></td>
																		</tr>
																		<!-- encryption and auth -->
																		<tr>
																			<td colspan="2" class="labelT1">
																				<table>
																					<tr style="padding-top: 5px;">
																						<td width="30px"><s:checkbox
																								name="smtpEncryption" id="smtpEncryption"
																								onclick="selectSmtpEncryption(this.checked)" />
																						</td>
																						<td width="300px"><label> <s:text
																									name="admin.emailNotfiy.encryption" />
																						</label></td>
																					</tr>
																					<tr>
																						<td id="smtpEncrySection" style="display: none;"
																							colspan="2">
																							<table>
																								<tr>
																									<td style="padding-left: 20px"><s:radio
																											label="Gender" id="" name="smtpEncryProtocol"
																											list="#{'ssl':'SSL'}"
																											onclick="selectSSL(this.checked);"
																											value="%{smtpEncryProtocol}" /> <s:radio
																											label="Gender" id="" name="smtpEncryProtocol"
																											list="#{'tls':'TLS'}"
																											onclick="selectTLS(this.checked);"
																											value="%{smtpEncryProtocol}" /></td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																					<tr style="padding-top: 5px;">
																						<td width="30px"><s:checkbox
																								name="supportAuth" id="supportAuth"
																								onclick="selectSupportAuth(this.checked)" /></td>
																						<td><label> <s:text
																									name="admin.emailNotfiy.supportAuth" />
																						</label></td>
																					</tr>
																					<tr>
																						<td id="authSection" style="display: none;"
																							colspan="4">
																							<table>
																								<tr>
																									<td style="padding-left: 30px" width="160px">
																										<label> <s:text
																												name="admin.emailNotfiy.emailUserName" />
																									</label>
																									</td>
																									<td><s:textfield id="emailUserName"
																											name="emailUserName" maxlength="64" /></td>
																								</tr>
																								<tr>
																									<td style="padding-left: 30px"><label>
																											<s:text
																												name="admin.emailNotfiy.emailPassword" />
																									</label></td>
																									<td><s:password id="emailPassword"
																											name="emailPassword" size="20" maxlength="32"
																											showPassword="true"
																											onkeypress="return hm.util.keyPressPermit(event,'password');" />
																										<s:textfield id="emailPassword_text"
																											name="emailPassword" disabled="true"
																											size="20" maxlength="32"
																											cssStyle="display:none" /></td>
																								</tr>
																								<tr>
																									<td>&nbsp;</td>
																									<td>
																										<table border="0" cellspacing="0"
																											cellpadding="0">
																											<tr>
																												<td><s:checkbox
																														id="chkToggleDisplay_email" name="ignore"
																														value="true"
																														disabled="%{writeDisable4Struts}"
																														onclick="hm.util.toggleObscurePassword(this.checked,['emailPassword'],['emailPassword_text']);" />
																												</td>
																												<td><s:text
																														name="admin.user.obscurePassword" /></td>
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
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.fromEmail" />
																			</label></td>
																			<td><s:textfield id="fromEmail" name="fromEmail"
																					maxlength="256" size="64" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.toEmail1" />
																			</label></td>
																			<td><s:textfield id="toEmail1" name="toEmail1"
																					maxlength="128" size="64" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.toEmail2" />
																			</label></td>
																			<td><s:textfield id="toEmail2" name="toEmail2"
																					maxlength="128" size="64" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.toEmail3" />
																			</label></td>
																			<td><s:textfield id="toEmail3" name="toEmail3"
																					maxlength="128" size="64" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.toEmail4" />
																			</label></td>
																			<td><s:textfield id="toEmail4" name="toEmail4"
																					maxlength="128" size="64" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label> <s:text
																						name="admin.emailNotify.toEmail5" />
																			</label></td>
																			<td><s:textfield id="toEmail5" name="toEmail5"
																					maxlength="128" size="64" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td style="padding-left: 10px; padding-top: 5px;">
														<fieldset style="width: 700px;">
															<legend>
																<s:text name="admin.emailNotify.notifyTable" />
															</legend>
															<div>
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%">
																	<tr>
																		<td height="5"></td>
																	</tr>
																	<tr>
																		<td><s:checkbox name="enableNotify"
																				id="cbEnableNotify"
																				onclick="selectedEnableNotfiy(this.checked)" /> <label>
																				<s:text name="admin.emailNotfiy.enableNotify" />
																		</label></td>
																	</tr>
																	<tr>
																		<td height="5"></td>
																	</tr>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0"
																				width="100%">
																				<tr>
																					<th align="left" width="200px"><s:text
																							name="admin.emailNotify.eventType" /></th>
																					<th align="left"><s:text
																							name="admin.emailNotify.enable" /></th>
																				</tr>
																				<s:iterator id="eventTable" value="%{eventTypeList}"
																					status="status">
																					<tr class="list">
																						<td class="list" width="200px"><s:property
																								value="%{displayName}" /></td>
																						<td class="listCheck" align="left"><s:checkbox
																								name="enabled_event" id="enabled_event_%{value}"
																								fieldValue="%{#status.index}" /></td>
																					</tr>
																				</s:iterator>
																			</table>
																		</td>
																		<td style="padding-left: 10px;" valign="top">
																			<table border="0" cellspacing="0" cellpadding="0"
																				width="100%">
																				<tr>
																					<th align="left"><s:text
																							name="admin.emailNotify.alert" /></th>
																					<th align="center"><s:text
																							name="admin.emailNotify.critical" /></th>
																					<th align="center"><s:text
																							name="admin.emailNotify.major" /></th>
																					<th align="center"><s:text
																							name="admin.emailNotify.min" /></th>
																					<th align="center"><s:text
																							name="admin.emailNotify.info" /></th>
																					<th align="center"><s:text
																							name="admin.emailNotify.clear" /></th>
																				</tr>
																				<s:iterator id="alertTable" value="%{alertTypeList}"
																					status="status">
																					<tr class="list">
																						<td class="list"><s:property
																								value="%{displayName}" /></td>
																						<td class="listCheck" align="center"><s:checkbox
																								name="enabledCritical"
																								id="enabled_critical_%{value}"
																								fieldValue="%{#status.index}" /></td>
																						<td class="listCheck" align="center"><s:checkbox
																								name="enabledMajor" id="enabled_major_%{value}"
																								fieldValue="%{#status.index}" /></td>
																						<td class="listCheck" align="center"><s:checkbox
																								name="enabledMinor" id="enabled_minor_%{value}"
																								fieldValue="%{#status.index}" /></td>
																						<td class="listCheck" align="center"><s:checkbox
																								name="enabledInfo" id="enabled_info_%{value}"
																								fieldValue="%{#status.index}" /></td>
																						<td class="listCheck" align="center"><s:checkbox
																								name="enabledClear" id="enabled_clear_%{value}"
																								fieldValue="%{#status.index}" /></td>
																					</tr>
																				</s:iterator>
																			</table>
																		</td>
																	</tr>
																</table>
															</div>
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
	
						<!-- HiveManager Admin Authentication Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="280px"><s:checkbox name="updateAuth"
												id="updateAuth" onclick="selectUpdateAuth(this.checked);"
												disabled="%{disableAuthSelect}" /> <label> <s:text
													name="admin.management.adminAuth" />
										</label>
										</td>
										<td>
											<s:select name="adminAuth" id="adminAuth"
												cssStyle="width: 100px;" list="%{adminAuthValues}"
												listKey="key" listValue="value"
												onchange="selectAdminAuth(this.value)"
												disabled="%{disableAdminAuth}" /></td>
									</tr>
									<tr style="display:<s:property value="%{hideRadius}"/>"
										id="radiusSection">
										<td style="padding-left: 35px;" colspan="2">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td width="175px"><s:text
															name="admin.management.authType" /></td>
													<td colspan="3"><s:select name="authType"
															id="authType" cssStyle="width: 100px;"
															list="%{authTypeValues}" listKey="key" listValue="value"
															disabled="%{!updateAuth}" /></td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td><s:text name="admin.management.radiusServer" /></td>
													<td><s:select name="radiusServiceId"
															disabled="%{!updateAuth}" id="radiusList"
															cssStyle="width: 250px;" list="%{radiusAssignment}"
															listKey="id" listValue="value" /></td>
													<td valign="top" style="padding-left: 3px;"
														id="staticButton"><img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />" width="16"
														height="16" alt="New" title="New" /> <img
														class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></td>
													<td valign="top" style="padding-left: 3px;"
														id="activeButton"><a class="marginBtn"
														href="javascript:submitAction('newRadius')"><img
															class="dinl" src="<s:url value="/images/new.png" />"
															width="16" height="16" alt="New" title="New" /></a> <a
														class="marginBtn"
														href="javascript:submitAction('editRadius')"><img
															class="dinl" src="<s:url value="/images/modify.png" />"
															width="16" height="16" alt="Modify" title="Modify" /></a></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- HiveManager TFTP Service Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="280px"><s:checkbox
												name="tftpStateUpdate" id="tftpStateUpdate"
												onclick="selectTftpStateUpdate(this.checked);" /> <label>
												<s:text name="admin.management.tftpStateUpdate" />
										</label></td>
										<td><s:select name="tftpState" id="tftpState"
												cssStyle="width: 100px;" list="%{tftpStateValues}"
												listKey="key" listValue="value"
												disabled="%{disableTftpState}" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- Log Server Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												name="updateLogServer" id="updateLogServer"
												onclick="selectUpdateLogServer(this.checked);" /> <label>
												<s:text name="admin.management.updateLogServer" />
										</label></td>
									</tr>
									<tr style="display:<s:property value="%{hideLogServer}"/>"
										id="logServerSection">
										<td style="padding-left: 15px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding-left: 15px;"><s:radio
															label="Gender" id="" name="logStatus"
															list="%{disableLogServer}" listKey="key"
															listValue="value"
															onclick="selectDisableLog(this.checked);"
															value="%{logStatus}" /></td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td style="padding-left: 15px;"><s:radio
															label="Gender" id="" name="logStatus"
															list="%{enableLogServer}" listKey="key" listValue="value"
															onclick="selectEnableLog(this.checked);"
															value="%{logStatus}" /></td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td style="padding-left: 30px;">
														<table border="0" cellspacing="0" cellpadding="0"
															width="100%">
															<tr>
																<td style="padding-left: 15px;"><s:radio
																		label="Gender" id="" name="entryStatus"
																		list="#{'anyEntry':'Allow any remote syslog entry'}"
																		onclick="selectAnyEntry(this.checked);"
																		value="%{entryStatus}" disabled="%{disabledLogServer}" />
																</td>
															</tr>
															<tr>
																<td height="5"></td>
															</tr>
															<tr>
																<td style="padding-left: 15px;"><s:radio
																		label="Gender" id="" name="entryStatus"
																		list="#{'specialEntry':'Allow special remote syslog entry'}"
																		onclick="selectSpecialEntry(this.checked);"
																		value="%{entryStatus}" disabled="%{disabledLogServer}" />
																</td>
															</tr>
															<tr>
																<td height="5"></td>
															</tr>
															<tr>
																<td style="padding-left: 30px;">
																	<table border="0" cellspacing="0" cellpadding="0"
																		width="100%">
																		<tr>
																			<td class="buttons">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr id="newButton">
																						<td><input type="button" id="addBtn"
																							name="add" value="Add" class="button"
																							onClick="showCreateSection();"
																							<s:property value="writeDisabled" />
																							<s:property value="disabledAnyEntry" />>
																						</td>
																						<td><input type="button" id="removeBtn"
																							name="remove" value="Remove" class="button"
																							onClick="submitAction('removeSubnet');"
																							<s:property value="writeDisabled" />
																							<s:property value="disabledAnyEntry" />>
																						</td>
																					</tr>
																					<tr style="display: none;" id="createButton">
																						<td><input type="button" name="ignore"
																							value="Apply" id="applyBtn" class="button"
																							onClick="submitAction('addSubnet');"
																							<s:property value="disabledAnyEntry" />>
																						</td>
																						<td><input type="button" name="ignore"
																							value="Cancel" id="cancelBtn" class="button"
																							onClick="hideCreateSection();"
																							<s:property value="disabledAnyEntry" />>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table cellspacing="0" cellpadding="0" border="0"
																					class="view" width="500" id="subnetTable">
																					<tr id="headerSection">
																						<th class="check"><input type="checkbox"
																							id="checkAll"
																							onClick="hm.util.toggleCheckAll(this);">
																						</th>
																						<th><s:text name="admin.management.subnetIP" />
																						</th>
																						<th><s:text name="admin.management.netmask" />
																						</th>
																					</tr>
																					<tr style="display: none;" id="createSection">
																						<td class="listHead"></td>
																						<td class="listHead" valign="top"><s:textfield
																								id="subnetIP" name="subnetIP" size="30"
																								maxlength="%{ipAddressLength}"
																								onkeypress="return hm.util.keyPressPermit(event,'ip');"
																								disabled="%{disabledAnyEntry}" /></td>
																						<td class="listHead" valign="top"><s:textfield
																								id="subnetMask" name="subnetMask" size="30"
																								maxlength="%{ipAddressLength}"
																								onkeypress="return hm.util.keyPressPermit(event,'ip');"
																								disabled="%{disabledAnyEntry}" /></td>
																					</tr>
																					<s:if test="%{subnetList.size() == 0}">
																						<ah:emptyList />
																					</s:if>
																					<tiles:insertDefinition name="selectAll" />
																					<s:iterator value="subnetList" status="status">
																						<tiles:insertDefinition name="rowClass" />
																						<tr class="<s:property value="%{#rowClass}"/>">
																							<td class="listCheck"><ah:checkItem /></td>
																							<td class="list"><s:property value="ip" />
																							</td>
																							<td class="list"><s:property value="mask" />
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
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>						
						<!-- Notification Information Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												name="updateNotifyInfo" id="updateNotifyInfo"
												onclick="selectUpdateNotifyInfo(this.checked);" /> <label>
												<s:text name="admin.management.updateNotfiyInfo" />
										</label></td>
									</tr>
									<tr style="display: none" id="notifyInfoSection">
										<td style="padding-left: 35px;">
											<table border="0" cellspacing="0" cellpadding="0"
												width="100%">
												<tr>
													<td><s:checkbox name="showNotifyInfo"
															id="showNotifyInfo"
															onclick="selectShowNotifyInfo(this.checked);" /> <label>
															<s:text name="admin.management.showNotfiyInfo" />
													</label></td>
												</tr>
												<tr>
													<td style="padding-top: 5px; padding-left: 5px;">
														<div id="hmNotifyInfoTd">
															<table border="0" cellspacing="0" cellpadding="0"
																width="100%">
																<tr>
																	<td width="80px"><s:text
																			name="admin.management.notifyTitle" /></td>
																	<td><s:textfield id="hmNotifyTitle"
																			name="hmNotifyTitle" maxlength="64"
																			cssStyle="width: 200px;" /></td>
																</tr>
																<tr>
																	<td height="4px" colspan="2"></td>
																</tr>
																<tr>
																	<td width="80px" valign="top"><s:text
																			name="admin.management.notifyContent" /></td>
																	<td><s:textarea rows="5" cols="100"
																			id="hmNotifyInfo" name="hmNotifyInfo" /></td>
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
						<!--  OpenDNS Server Settings -->
						<s:if test="%{fullMode}">
							<tr>
							    <td style="padding-left:10px;">
							    	<table border="0" cellspacing="0" cellpadding="0" width="100%">
							    		<tr>
							    			<td class="labelT1" width="200px"><s:checkbox
													name="updateOpenDNSServer" id="updateOpenDNSServer"
													onclick="selectUpdateOpenDNSServer(this.checked);" /> <label>
													<s:text name="admin.management.updateOpenDNSServer" />
											</label></td>
							    		</tr>
							    		<tr style="display:<s:property value="%{hideOpenDNSServer}"/>" id="openDNSServerSection">
							    			<td style="padding-left: 20px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td colspan="2"><s:checkbox name="enableOpenDNS"
																id="enableOpenDNS"
																onclick="checkOpenDNS(this.checked);" /> <label for="enableOpenDNS">
																<s:text
																	name="admin.management.webSecurity.enableopenDNS" />
														</label></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td style="padding-left: 15px;">
															<fieldset style="width: 600px">
																<legend>
																	<s:text name="glasgow_16.home.hmSettings.openDNS.title.setting"></s:text>
																</legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="180"
																			style="padding-left: 15px;"><label> <s:text
																					name="admin.management.updateOpenDNSServer.username" /> <font
																				color="red"><s:text name="*" /> </font>
																		</label></td>
																		<td><s:textfield id="openDNSUserName" name="openDNSUserName"
																				size="30" maxlength="30" disabled="%{!enableOpenDNS}" />
																			<s:text
																				name="admin.management.updateOpenDNSServer.username.range" />
																		</td>
																	</tr>	
																	<tr>
																		<td class="labelT1" width="180"
																			style="padding-left: 15px;"><label> <s:text
																					name="admin.management.updateOpenDNSServer.password" /> <font
																				color="red"><s:text name="*" /> </font>
																		</label></td>
																		<td><s:password name="openDNSPassword"
																				id="openDNSPasswordId" value="%{openDNSPassword}"
																				maxlength="32" size="30"
																				onkeypress="return hm.util.keyPressPermit(event,'password');"
																				showPassword="true" /> 
																				<s:textfield
																				name="openDNSPasswordText" id="openDNSPasswordIdText"
																				cssStyle="display:none;" value="%{openDNSPassword}"
																				maxlength="32" size="30"
																				onkeypress="return hm.util.keyPressPermit(event,'password');" />
																			<s:text
																				name="admin.management.updateOpenDNSServer.password.range" />
																		</td>
																	</tr>
																	<tr>
																		<td></td>
																		<td>
																			<table>
																				<tr>
																					<td><s:checkbox
																							id="chkToggleDisplay_OpenDNSPassword"
																							name="chkOpenDNSPassword" value="true"
																							disabled="%{!enableOpenDNS}"
																							onclick="hm.util.toggleObscurePassword(this.checked,
																								['openDNSPasswordId'],
																								['openDNSPasswordIdText']);" />
																					</td>
																					<td><s:text
																							name="admin.management.updateBarracudaServer.authorizationKey.obscure" />
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>													
	<%-- 																<tr>
																		<td class="labelT1" width="180"
																			style="padding-left: 15px;"><label> <s:text
																					name="admin.management.updateOpenDNSServer.customerName" /> <font
																				color="red"><s:text name="*" /> </font>
																		</label></td>
																		<td><s:textfield id="openDNSCustomerName" name="openDNSCustomerName"
																				size="40" maxlength="64" disabled="%{!enableOpenDNS}" />
																			<s:text
																				name="admin.management.updateOpenDNSServer.customerName.range" />
																		</td>
																	</tr>	 --%>													
																	<tr>
																		<td class="labelT1" width="180"
																			style="padding-left: 15px;"><label> <s:text
																					name="admin.management.updateOpenDNSServer.dnsServer1IP" /> <font
																				color="red"><s:text name="*" /> </font>
																		</label></td>
																		<td><s:textfield id="openDNSServer1IP" name="openDNSServer1IP"
																				size="30" maxlength="64" disabled="%{!enableOpenDNS}" />
																		</td>
																	</tr>	
																	<tr>
																		<td class="labelT1" width="180"
																			style="padding-left: 15px;"><label> <s:text
																					name="admin.management.updateOpenDNSServer.dnsServer2IP" /> <font
																				color="red"><s:text name="*" /> </font>
																		</label></td>
																		<td><s:textfield id="openDNSServer2IP" name="openDNSServer2IP"
																				size="30" maxlength="64" disabled="%{!enableOpenDNS}" />
																		</td>
																	</tr>	
																	<tr>
																		<td colspan="2" style="height:10px"></td>
																	</tr>	
																	<tr>
																		<td colspan="2">
																			<div id="note_openDNSLoginTest" style="display: none">
																				<table cellspacing="0" cellpadding="0" border="0"
																					width="100%">
																					<tr>
																						<td height="10"></td>
																					</tr>
																					<tr align="center">
																						<td class="noteInfo" id="info_openDNSLogin"></td>
																					</tr>
																					<tr align="center">
																						<td class="noteError" id="error_openDNSLogin"></td>
																					</tr>
																				</table>
																			</div>														
																		</td>
																	</tr>	
																	<tr>
																		<td/>
																		<td>
																			<input id="btn_openDNSLoginTest" type="button" name="ignore" value="Connection Test" class="button" onClick="openDNSLoginTest();" style="width: 160px;"/>															
																		</td>
																	</tr>															
																</table>	
														   </fieldset>													
														</td>
													</tr>
													<tr>
														<td style="height:10px" colspan="2" />
													</tr>
													<tr>
														<td style="padding-left: 15px;">																													
															<fieldset  style="width: 600px">
																<legend>
																	<s:text name="glasgow_16.home.hmSettings.openDNS.title.didmapping"></s:text>
																</legend>
																<div id="note_userswitch" style="padding-top:10px;">
																	<font color="blue"><s:text name="glasgow_16.home.hmSettings.openDNS.didmapping.userswitch.note"/></font>
																</div>																
																<div id="note_opendnsmapping" style="padding-top:10px;">
																	<font color="blue"><s:text name="glasgow_16.home.hmSettings.openDNS.didmapping.display.note"/></font>
																</div>
 																<div id="section_opendnsmapping" style="padding-top:10px; padding-bottom:10px;">
 																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td>
																				<div id="note_openDNSSyncNote" style="display: none">
																					<table cellspacing="0" cellpadding="0" border="0" width="100%">
																						<tr>
																							<td height="10"></td>
																						</tr>
																						<tr>
																							<td class="noteInfo" id="info_openDNSSyncNote"></td>
																						</tr>
																						<tr>
																							<td class="noteError" id="error_openDNSSyncNote"></td>
																						</tr>
																					</table>
																				</div>														
																			</td>
																		</tr>	
																		<tr>
 																			<td>
 																				<table cellspacing="0" cellpadding="0" border="0">
 																					<tr>
	 																					<th align="left" width="200px"><s:text name="glasgow_16.home.hmSettings.openDNS.title.userprofile"/></th>
	 																					<th align="left" width="200px">
	 																						<s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidlabel"/>
	 																						<a href="javascript:refreshOpenDNSDevice();" class="marginBtn">
																								<img width="16" height="16" title="New" alt="New" src="images/dashboard/refresh.png" class="dinl">
																							</a>
																						</th>
	 																					<th align="left" width="200px"><s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidid"/></th>
 																					</tr>	 																					
 																					<s:iterator value="openDNSMappings" status="st">
																						<tr id="specificRow_<s:property value='%{#st.index+1}' />">																							
																							<td class="list"><s:property value="userProfile.userProfileName" /><s:hidden name="openDNSMappingIds" value="%{id}"></s:hidden></td>
																							<td class="list" style="width: 260px;">
																								<s:select id="openDNSDevice_%{id}" list="allOpenDNSDevices" name="openDNSDevices" cssStyle="width: 200px; " 
																										  value="%{openDNSDevice.id}" listKey="id" listValue="value"																											
																										  onchange="refreshDeviceId(this.value, %{id});"/>
																								<a href="javascript:clickOpenDNSDevice();" class="marginBtn">
																									<img width="16" height="16" title="New" alt="New" src="images/new.png" class="dinl">
																								</a>																																																			
																							<td class="list" id="openDNSDId_<s:property value='%{id}' />"><s:property value="openDNSDevice.deviceId" /></td>
																						</tr>	 																					
 																					</s:iterator>
 																				</table>
 																			</td>
 																		</tr> 																		
 																	</table>																																
																</div>																									
															</fieldset>			
														</td>
													</tr>																																																															
												</table>
							    			</td>
							    		</tr>
							    	</table>
							    </td>
							</tr>
						</s:if>
						<!-- Presence Analytics Settings -->
						<tr style="display:<s:property value="%{displayPresence}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												id="presenceUpdate" name="presenceUpdate"
												onclick="selectPresenceUpdate(this.checked);" /> <label>
												<s:text name="admin.management.presenceUpdate"></s:text>
										</label></td>
									</tr>
									<tr id="presenceTr" style="display: none">
										<td style="padding-left: 30px;"><s:checkbox
												id="presenceEnable" name="presenceEnable" /> <label>
												<s:text name="admin.management.presenceEnable"></s:text>
										</label></td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- SNMP Server Settings -->
						<tr style="display:<s:property value="%{hide4VHM}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												name="updateSNMP" id="updateSNMP"
												onclick="selectUpdateSNMP(this.checked);" /> <label>
												<s:text name="admin.management.updateSNMP" />
										</label></td>
									</tr>
									<tr style="display:<s:property value="%{hideSNMP}"/>"
										id="snmpSection">
										<td style="padding-left: 35px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td width="150px"><s:text
															name="admin.management.snmpCommunity" /></td>
													<td><s:textfield id="snmpCommunity"
															name="snmpCommunity"
															onkeypress="return hm.util.keyPressPermit(event,'name');"
															maxlength="32" cssStyle="width: 95px;" /> <s:text
															name="admin.management.snmpCommunity.range" /></td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td width="150px"><s:text
															name="admin.management.snmpTrapReceiver" /></td>
													<td><ah:createOrSelect divId="errorDisplay"
															list="availableIpAddress" typeString="IpAddress"
															selectIdName="myIpSelect" inputValueName="inputIpValue" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
                        <!-- Social Login Settings -->
                        <tr style="display: <s:property value="hide4ClientManagement" />">
                            <td style="padding-left: 10px;">
                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                    <tr>
                                        <td class="labelT1" width="200px">
                                        <s:checkbox name="updateGuestAnalytics" onclick="dislayGuestAanalytics(this.checked);"/>
                                        <label>
                                                <s:text name="home.services.guestanalytics.label"></s:text>
                                        </label>
                                        </td>
                                    </tr>
                                    <tr id="generalSettingsGA" style="display: none;">
                                        <td style="padding-left: 30px">
                                            <fieldset style="width: 600px">
                                                <legend>
                                                    <s:text name="home.hmSettings.clientManagement.generalSetting"></s:text>
                                                </legend>
                                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                    <tr>
                                                        <td>
                                                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                                                <tr>
                                                                    <td class="labelT1">
                                                                    <s:checkbox name="enabledGuestAnanlytics" onclick="enableGuestAanalytics(this.checked);"/>
                                                                    <label><s:text name="home.services.guestanalytics.enable"></s:text></label>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </fieldset>
                                        </td>
                                    </tr>
                                    <tr id="troubleshootingGA" style="display: none;">
                                        <td style="padding: 10px 0 10px 30px;">
                                            <fieldset style="width: 600px">
                                                <legend>
                                                    <s:text name="home.services.guestanalytics.test"></s:text>
                                                </legend>
                                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                                    <tr>
                                                        <td style="padding-left: 10px">
                                                            <div id="troubleshootingMsgGA" style="display: none">
                                                                <table cellspacing="0" cellpadding="0" border="0"
                                                                    width="100%">
                                                                    <tr>
                                                                        <td height="10"></td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="noteInfo" id="trbGAInfoMessage"></td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="noteError" id="trbGAErrorMessage"></td>
                                                                    </tr>
                                                                </table>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td height="10"></td>
                                                    </tr>
                                                    <tr>
                                                        <td style="text-align:center;"><input type="button" name="ignore"
                                                            value="Test" class="button"
                                                            onClick="checkACPPService();" style="width: 90px;">
                                                        </td>
                                                    </tr>
                                                </table>
                                            </fieldset>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>                   						
						<!-- Spectrum Analysis Maximum -->
						<tr style="display:<s:property value="%{hide4HHMHome}"/>">
							<td style="padding-left: 10px;">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="labelT1" width="200px"><s:checkbox
												name="snpStateUpdate" id="snpStateUpdate"
												onclick="selectSnpStateUpdate(this.checked);" /> <label>
												<s:text name="admin.management.snpStateUpdate" />
										</label></td>
										<td><s:textfield name="snpState" id="snpState"
												maxlength="2" size="15" disabled="%{disableSnpState}"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="admin.management.snpStateUpdate.range" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<s:if test="%{!oEMSystem}">
							<!-- StudentManager Settings for Remote Access Credentials -->
							<tr>
								<td
									style="padding-left: 10px;display:<s:property value="%{hide4VHM}"/>">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="400px"><s:checkbox
													name="updateRPC" id="updateRPC"
													onclick="selectUpdateRPC(this.checked);" /> <label>
													<s:text name="home.hmSettings.rpc" />
											</label></td>
										</tr>
										<tr style="display:<s:property value="%{showRPCSection}"/>"
											id="rpcSection">
											<td style="padding-left: 35px;">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td colspan="2"><s:checkbox name="enableRPCServer"
																			id="enableRPCServerId"
																			onclick="checkRPCServer(this.checked);" /> <label>
																			<s:text name="home.hmSettings.rpc.enable" />
																	</label></td>
																</tr>
																<tr>
																	<td height="6px"></td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.userName" /><font color="red"><s:text
																				name="*" /> </font></td>
																	<td><s:textfield name="rpcUserName"
																			disabled="%{!enableRPCServer}"
																			maxlength="%{userNameLength}" />&nbsp;</td>
																	<td><s:text name="hm.airtight.userName.range" />
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.password" /><font color="red"><s:text
																				name="*" /> </font></td>
																	<td><s:password id="rpcPasswdId" name="rpcPasswd"
																			value="%{rpcPasswd}" disabled="%{!enableRPCServer}"
																			maxlength="%{passwordLength}" showPassword="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield cssStyle="display:none;" disabled="true"
																			name="rpcPasswd" id="rpcPasswdId_text"
																			value="%{rpcPasswd}" maxlength="%{passwordLength}"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />&nbsp;
																	</td>
																	<td><s:text name="hm.airtight.password.range" />
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="120px"><s:text
																			name="hm.airtight.password.confirm" /> <font
																		color="red"><s:text name="*" /> </font></td>
																	<td><s:password name="rpcRePasswd"
																			id="rpcRePasswdId" disabled="%{!enableRPCServer}"
																			value="%{rpcRePasswd}" maxlength="%{passwordLength}"
																			showPassword="true" /> <s:textfield
																			name="rpcRePasswd" id="rpcRePasswdId_text"
																			cssStyle="display:none;" disabled="true"
																			value="%{rpcRePasswd}" maxlength="%{passwordLength}"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	</td>
																</tr>
																<tr>
																	<td></td>
																	<td>
																		<table>
																			<tr>
																				<td><s:checkbox id="chkToggleDisplay_rpc"
																						name="ignore" value="true"
																						disabled="%{!enableRPCServer}"
																						onclick="hm.util.toggleObscurePassword(this.checked,
																						['rpcPasswdId','rpcRePasswdId'],
																						['rpcPasswdId_text','rpcRePasswdId_text']);" />
																				</td>
																				<td><s:text name="admin.user.obscurePassword" />
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="150px"><s:text
																			name="home.hmSettings.rpc.timeout" /> <font
																		color="red"><s:text name="*" /> </font></td>
																	<td><s:textfield name="rpcInterval" maxlength="3"
																			disabled="%{!enableRPCServer}"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		&nbsp;</td>
																	<td><s:text
																			name="home.hmSettings.rpc.timeout.range" /></td>
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
							<!-- TeacherView Settings -->
							<tr>
								<td
									style="padding-left: 10px;display: <s:property value="%{displayTeacher}"/>">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="200px"><s:checkbox
													name="updateTeacher" id="updateTeacher"
													onclick="selectUpdateTeacher(this.checked);" /> <label>
													<s:text name="admin.management.updateTeacher" />
											</label></td>
										</tr>
										<tr style="display: none" id="teacherSection">
											<td style="padding-left: 35px;">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td colspan="10"><s:checkbox name="enableTeacher"
																id="enableTeacher"
																onclick="selectEnableTeacher(this.checked);" /> <label>
																<s:text name="admin.management.enableTeacherView" />
														</label></td>
													</tr>
													<s:if test="%{!hMOnline && isInHomeDomain}">
														<tr>
															<td style="padding: 10px 0 0 30px;" width="200px"><label>
																	<s:text name="admin.management.ssoServer" />
															</label></td>
															<td style="padding: 10px 0 0 0;"><s:textfield
																	name="casServer" id="casServer"
																	disabled="%{!enableTeacher}" /></td>
														</tr>
														<tr>
															<td style="padding: 0 0 0 30px;" colspan="10"><font
																color="blue"> <s:text
																		name="admin.management.ssoServer.note" />
															</font></td>
														</tr>
													</s:if>
													<tr>
														<td colspan="10" style="padding: 10px 0 0 30px;"><s:checkbox
																name="enableTVProxy" id="enableTVProxy"
																onclick="selectEnableTVProxy(this.checked);" /> <label>
																<s:text name="admin.management.enabletvproxy" />
														</label></td>
													</tr>
													<tr>
														<td style="padding: 5px 0 0 30px;" width="200px"><label>
																<s:text name="admin.management.tvproxyip" /> <font
																color="red"><s:text name="*" /> </font>
														</label></td>
														<td style="padding: 5px 0 0 0;"><s:textfield
																name="tvProxyIP" id="tvProxyIP"
																onkeypress="return hm.util.keyPressPermit(event,'name');"
																maxlength="128" disabled="%{!enableTVProxy}" /> <s:text
																name="admin.management.tvproxyip.range" /></td>
													</tr>
													<tr>
														<td style="padding: 5px 0 0 30px;" width="200px"><label>
																<s:text name="admin.management.tvproxyport" /> <font
																color="red"><s:text name="*" /> </font>
														</label></td>
														<td style="padding: 5px 0 0 0;"><s:textfield
																name="tvProxyPort" id="tvProxyPort"
																onkeypress="return hm.util.keyPressPermit(event,'name');"
																maxlength="5" disabled="%{!enableTVProxy}" /> <s:text
																name="admin.management.tvproxyport.range" /></td>
													</tr>
													<tr>
														<td style="padding: 5px 0 0 30px;" width="200px"><label>
																<s:text name="admin.management.tvautoproxyfile" />
														</label></td>
														<td style="padding: 5px 0 0 0;"><s:textfield
																name="tvAutoProxyFile" id="tvAutoProxyFile"
																onkeypress="return hm.util.keyPressPermit(event,'name');"
																maxlength="128" disabled="%{!enableTVProxy}" /> <s:text
																name="admin.management.tvautoproxyfile.range" /></td>
													</tr>
													<tr>
														<td style="padding: 0 0 0 30px;" colspan="10"><font
															color="blue"> <s:text
																	name="admin.management.autoproxyfile.note" />
														</font></td>
													</tr>
													<tr>
														<td style="padding: 0 0 0 30px;" colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td height="10px"></td>
																</tr>
																<tr>
																	<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="admin.management.enableCaseSensitive" />','tvCaseSensitiveSettingSection');</script></td>
																</tr>
																<tr id="tvCaseSensitiveSettingSection">
																	<td>
																		<table>
																			<tr>
																				<td style="padding-left: 20px"><s:checkbox
																						name="enableCaseSensitive"
																						id="enableCaseSensitive" /> <label> <s:text
																							name="admin.management.casesensitive.enable" />
																				</label></td>
																			</tr>
																			<tr>
																				<td style="padding: 0 0 0 30px;" colspan="10">
																					<font color="blue"> <s:text
																							name="admin.management.enableCaseSensitive.note" />
																				</font>
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
									</table>
								</td>
							</tr>
						</s:if>
						<!-- WebSense Server Settings -->
						<s:if test="%{fullMode}">
							
							<tr>
								<td style="padding-left: 10px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="200px"><s:checkbox
													name="updateWebSenseServer" id="updateWebSenseServer"
													onclick="selectUpdateWebSenseServer(this.checked);" /> <label>
													<s:text name="admin.management.updateWebsenseServer" />
											</label></td>
										</tr>
										<tr
											style="display:<s:property value="%{hideWebSenseServer}"/>"
											id="webSenseServerSection">
											<td style="padding-left: 20px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td colspan="2"><s:checkbox name="enableWebsense"
																id="enableWebsenseId"
																onclick="checkWebsense(this.checked);" /> <label>
																<s:text
																	name="admin.management.webSecurity.enablewebsense" />
														</label></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<!-- remove radio button
													<tr>
														<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																	<td style="padding-left: 50px;" ><s:radio label="Gender"
																			name="wensenseMode" list="%{hostedMode}"
																			listKey="key" listValue="value" value="wensenseMode"
																			onchange="radioModeTypeChanged(this.value);"
																			onclick="this.blur();" /></td>
																	<td style="padding-left: 100px;"><s:radio label="Gender"
																			name="wensenseMode" list="%{hybridMode}"
																			listKey="key" listValue="value" value="wensenseMode"
																			onchange="radioModeTypeChanged(this.value);"
																			onclick="this.blur();" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													 -->
													<tr>
														<td class="labelT1" width="180"
															style="padding-left: 15px;"><label> <s:text
																	name="admin.management.webSecurity.serviceHost" />
														</label></td>
														<td><s:textfield id="webSenseServiceHost"
																name="webSenseServiceHost" size="40"
																disabled="%{!enableWebsense}" /> <!-- Service host field will be made editable temporarily,This should be changed back to read-only before FCS. -->
															<!-- <s:textfield id="webSenseServiceHost" name="webSenseServiceHost" size="40" disabled="true"/> -->
														</td>
													</tr>
													<tr
														style="display:<s:property value="%{wensenseNoteStyle}"/>;"
														id="webSenseNote">
														<td colspan="3" style="padding-left: 15px;"><s:text
																name="admin.management.webSecurity.websense.note" /></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td class="labelT1" width="180"
															style="padding-left: 15px;"><label> <s:text
																	name="admin.management.updateWebsenseServer.accountID" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:textfield id="accountID" name="accountID"
																disabled="%{!enableWebsense}" maxlength="16" size="40" />
															<s:text
																name="admin.management.updateWebsenseServer.accountID.range" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text
																	name="admin.management.webSecurity.websense.securityKey" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:password name="securityKey" id="securityKeyId"
																disabled="%{!enableWebsense}" value="%{securityKey}"
																maxlength="32" size="40"
																onkeypress="return hm.util.keyPressPermit(event,'password');"
																showPassword="true" /> <s:textfield
																name="securityKeyText" id="securityKeyId_text"
																disabled="%{!enableWebsense}" cssStyle="display:none;"
																value="%{securityKey}" maxlength="32" size="40"
																onkeypress="return hm.util.keyPressPermit(event,'password');" />
															<s:text
																name="admin.management.webSecurity.websense.securityKey.range" />
														</td>
													</tr>
													<tr>
														<td></td>
														<td>
															<table>
																<tr>
																	<td><s:checkbox id="chkToggleDisplay_securityKey"
																			name="chkSecurityKey" value="true"
																			disabled="%{!enableWebsense}"
																			onclick="hm.util.toggleObscurePassword(this.checked,
																				['securityKeyId'],
																				['securityKeyId_text']);" />
																	</td>
																	<td><s:text
																			name="admin.management.webSecurity.websense.securityKey.obscure" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text
																	name="admin.management.webSecurity.websense.defaultDomain" />
														</label></td>
														<td><s:textfield id="defaultDomain"
																name="defaultDomain" disabled="%{!enableWebsense}"
																maxlength="32" size="40" /> <s:text
																name="admin.management.webSecurity.websense.defaultDomain.range" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text
																	name="admin.management.webSecurity.webSenseDefaultUserName" /><font color="red"><s:text name="*" /> </font>
														</label></td>
														<td><s:textfield id="webSenseDefaultUserName"
																name="webSenseDefaultUserName"
																disabled="%{!enableWebsense}" maxlength="32" size="40" />
															<s:text
																name="admin.management.webSecurity.webSenseDefaultUserName.range" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 15px;"><label>
																<s:text name="admin.management.webSecurity.whitelist" />
														</label></td>
														<td><s:select list="%{websenseWhitelists}"
																listKey="id" disabled="%{!enableWebsense}"
																listValue="value" name="websenseWhitelist"
																id="websenseWhitelist" cssStyle="width: 200px;" /> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
															</s:if> <s:else>
																<a class="marginBtn" id="newWebsenseWhitelistsId"
																	href="javascript:submitAction('newWebsenseWhitelists')"><img
																	id="newWebsenseWhitelistsImageId" class="dinl"
																	src="<s:url value="/images/new.png" />" width="16"
																	height="16" alt="New" title="New" /></a>
															</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
															</s:if> <s:else>
																<a class="marginBtn" id="editWebsenseWhitelistsId"
																	href="javascript:submitAction('editWebsenseWhitelists')"><img
																	id="editWebsenseWhitelistsImageId" class="dinl"
																	src="<s:url value="/images/modify.png" />" width="16"
																	height="16" alt="Modify" title="Modify" /></a>
															</s:else></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</s:if>		
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<div id="newOpenDNSDevicePanelId" style="display: none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM" style="width:650px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newOpenDNSDevicePanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:650px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
</div>
<script>
var YUD = YAHOO.util.Dom;
var noteTimer;
function displayIDMServiceSecion(id) {
    var el = Get(id);
    if(el) {
        if(el.style.display == '') {
            hm.util.hide(id);
            resetIDMService();
        } else {
            hm.util.show(id);
        }
    }
}
function resetIDMService() {
    var emailEl = YUD.get("idmEmail");
    if(emailEl) {
        emailEl.value = "";
    }
    var enableObs = YUD.get("idmChkToggleDisplay");
    var pwEl = YUD.get("idmUserPassword");
    var confirmpwEl = YUD.get("idmUserPasswordConfirm");
    if(pwEl) {
        pwEl.value = "";
    }
    if(confirmpwEl) {
        confirmpwEl.value = "";
    }
    pwEl = YUD.get("idmUserPassword_text");
    confirmpwEl = YUD.get("idmUserPasswordConfirm_text");
    if(pwEl) {
        pwEl.value = "";
    }
    if(confirmpwEl) {
        confirmpwEl.value = "";
    }
}
function getCustomerId() {
    var YUC = YAHOO.util.Connect;
    var emailEl = YUD.get("idmEmail");
    var value = emailEl.value.trim();
    if(value.length == 0) {
        emailEl.focus();
        hm.util.hideFieldError();
        hm.util.reportFieldError(emailEl, 
                '<s:text name="error.requiredField"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
        return;
    }
       if(!hm.util.validateEmail(value)) {
        emailEl.focus();
        hm.util.hideFieldError();
        hm.util.reportFieldError(emailEl, 
                '<s:text name="error.gml.temporary.email.invalid"></s:text>');
        return;
       }
        
    var enableObs = YUD.get("idmChkToggleDisplay");
    var pwEl = YUD.get("idmUserPassword");
    var confirmpwEl = YUD.get("idmUserPasswordConfirm");
    if(!enableObs.checked) {
        pwEl = YUD.get("idmUserPassword_text");
        confirmpwEl = YUD.get("idmUserPasswordConfirm_text");
    }
    if(pwEl.value.trim().length == 0) {
        pwEl.focus();
        hm.util.hideFieldError();
        hm.util.reportFieldError(pwEl, 
                '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password" /></s:param></s:text>');    
        return;
    }
    if(confirmpwEl.value.trim().length == 0) {
        confirmpwEl.focus();
        hm.util.hideFieldError();
        hm.util.reportFieldError(confirmpwEl, 
                '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password.confirm" /></s:param></s:text>');
        return;
    }
    if (confirmpwEl.value.trim() != pwEl.value.trim()) {
        pwEl.focus();
        hm.util.hideFieldError();
        hm.util.reportFieldError(pwEl, '<s:text name="error.passwordConfirm"></s:text>');
        return;
    }
    
    var url = "<s:url action='retrieveCACustomerId' includeParams='none' />?operation=retrieve"
            + "&ignore="+new Date().getTime();
    var postData = "userName=" + encodeURIComponent(emailEl.value.trim()) + "&password=" + encodeURIComponent(pwEl.value.trim());
    if(Get('enabledProxy4IDM')) {
    	postData += "&usingProxy="+Get('enabledProxy4IDM').checked;
    }
    YUC.asyncRequest('POST', url, 
            {success : succRetriveId, failure : failRetriveId, timeout: 60000}, postData);
    
    if(null == waitingPanel) {
        createWaitingPanel();
    }
    waitingPanel.setHeader("Retrieving your customer ID...");
    waitingPanel.show();
}
function succRetriveId(o) {
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
    eval("var details = " + o.responseText);
    if(details.succ) {
        showRespMsg('<s:text name="info.idm.retrieveCustomerId.succ"></s:text>' ,true);
    } else {
        showRespMsg(details.err);
    }
}
function failRetriveId(o) {
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
}
function showRespMsg(txt, flag) {
    hm.util.hideFieldError();
    if(flag) {
        YUD.get("infoRow").innerHTML = txt;
        YUD.get("errorRow").innerHTML = '';
    } else {
        YUD.get("infoRow").innerHTML = '';
        YUD.get("errorRow").innerHTML = txt;
    }
    YUD.get("idmNoteSection").style.display = '';
    noteTimer = setTimeout("hideRespMsg()", 10 * 1000);  // 5 seconds
}
function hideRespMsg() {
    YUD.get("infoRow").innerHTML = '';
    YUD.get("errorRow").innerHTML = '';
    YUD.get("idmNoteSection").style.display = 'none';  
}
function onUnloadNotes() {
    clearTimeout(noteTimer);
}

// ACM Troubleshooting
function detectTrouble(){
	var YUC = YAHOO.util.Connect;
	var url = "<s:url action='hmServices' includeParams='none' />?operation=troubleshooting" + "&ignore="+new Date().getTime();
	YUC.asyncRequest("GET",url,{success:succTrouble,failure:failureTrouble,timeout:60000},null);
	if(null == waitingPanel) {
        createWaitingPanel();
    }
    waitingPanel.setHeader("Detecting Client Management services...");
    waitingPanel.show();
}

function failureTrouble(o){}

function succTrouble(o){
	if(null != waitingPanel) {
        waitingPanel.hide();
    }
	eval("var details = " + o.responseText);
    if(details.succ) {
    	if(details.msg) {
	        showTroubleMsg(details.msg,true);
    	}
    } else {
        showTroubleMsg(details.msg,false);
    }
    if(details.warn) {
        showWarnDialog(details.warn);
    }
}

function showTroubleMsg(msg,isSucc){
	//hm.util.hideFieldError();
	 if(isSucc) {
        YUD.get("trbInfoMessage").innerHTML = msg;
        YUD.get("trbErrorMessage").innerHTML = '';
    } else {
        YUD.get("trbInfoMessage").innerHTML = '';
        YUD.get("trbErrorMessage").innerHTML = msg;
    }
    YUD.get("troubleshootingId").style.display = '';
    noteTimer = setTimeout("hideTroubleMsg()", 10 * 1000);  // 5 seconds
}

function hideTroubleMsg(){
	YUD.get("trbInfoMessage").innerHTML='';
	YUD.get("trbErrorMessage").innerHTML='';
	YUD.get("troubleshootingId").style.display='none';
}

function openDNSLoginTest(){
	if(!openDNSValidate()){
		return;
	} 
	
	document.getElementById(formName + "_operation").value = 'testOpenDNSLogin';
	var formObject = document.getElementById(formName);
	YAHOO.util.Connect.setForm(formObject);

	var url = "<s:url action='hmServices' includeParams='none' />";

	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:succOpenDNSLogin,failure:failOpenDNSLogin,timeout: 60000}, null);
	if(waitingPanel == null)
	{
		createWaitingPanel();
	}

	waitingPanel.setHeader("OpenDNS Login Testing...");
    waitingPanel.show();
}	

function succOpenDNSLogin(o){
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var response = " + o.responseText);
	if(response.isScuess)
	{
		$("#info_openDNSLogin").html(response.msg.valueOf());
		$("#error_openDNSLogin").html("");
	}
	else
	{
		$("#infor_openDNSLogin").html("");
		$("#error_openDNSLogin").html(response.msg.valueOf());		
	}
	
	$("#note_openDNSLoginTest").css("display","");
    noteTimer = setTimeout("hideOpenDNSMsg()", 10 * 1000);  // 5 seconds
}

function failOpenDNSLogin(){}

function hideOpenDNSMsg(){
	$("#infor_openDNSLogin").html("");
	$("#error_openDNSLogin").html("");
	$("#note_openDNSLoginTest").css("display","none");
}

function clickOpenDNSDevice(){
	fetchSelectServiceNewDlg();
}

var newOpenDNSDevicePanel = null;
function preparePanels4OpenDNSDevice() {
	var div = document.getElementById('newOpenDNSDevicePanelId');
	newOpenDNSDevicePanel = new YAHOO.widget.Panel(div, {
		width:"600px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:3
		});
	newOpenDNSDevicePanel.render(document.body);
	div.style.display = "";
}

YAHOO.util.Event.onContentReady("newOpenDNSDevicePanelId", function() {
	preparePanels4OpenDNSDevice();
}, this);

function fetchSelectServiceNewDlg() {
	var url = "";
	url = "<s:url action='hmServices' includeParams='none' />?operation=openDNSDeviceSettings&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
		waitingPanel.setHeader("Accessing OpenDNS Identity settings...");
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	set_innerHTML("newOpenDNSDevicePanelContentId", o.responseText);
	YAHOO.util.Event.onContentReady("newOpenDNSDevicePanelContentId", showCreateOpenDNSDevicePanel, this);
}

var resultDoNothing = function(){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
};

function showCreateOpenDNSDevicePanel(){
	if(null != newOpenDNSDevicePanel){
		newOpenDNSDevicePanel.cfg.setProperty('visible', true);
		newOpenDNSDevicePanel.center();
	}
}

function hideCreateOpenDNSDevicePanel(){
	if(null != newOpenDNSDevicePanel){
		set_innerHTML("newOpenDNSDevicePanelContentId", "");
		newOpenDNSDevicePanel.cfg.setProperty('visible', false);
	}
}

function updateOpenDNSOptions(id, deviceLabel){
	$("select[name='openDNSDevices']").each(function(){
		$(this).append('<option value="'+id+'" >'+deviceLabel+'</option>');			
	});
}

function refreshDeviceId(id, upid){
	var url = "";
	url = "<s:url action='hmServices' includeParams='none' />?operation=getDeviceId&selDeviceId="+id+"&selUserProfileId="+upid+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succRefreshDeviceId, failure : resultDoNothing, timeout: 60000}, null);
}

function succRefreshDeviceId(o){
	eval("var response = " + o.responseText);
	if(response.isSuccess)
	{
		var id = "openDNSDId_" + response.upId;
		$("#"+ id).html(response.deviceId);
	}
}

$("#openDNSServer1IP").blur(function(){
	var dnsServer1 = $(this).val();
	if(dnsServer1 != OPENDNS_SERVER_1 && dnsServer1 != OPENDNS_SERVER_2){
		hm.util.reportFieldError($(this).get(0), '<s:text name="glasgow_16.error.home.hmservice.opendns.dnsserver"><s:param>'+OPENDNS_SERVER_1+'</s:param><s:param>'+OPENDNS_SERVER_2+'</s:param></s:text>');
		$(this).focus();
	}
});

$("#openDNSServer2IP").blur(function(){
	var dnsServer2 = $(this).val();
	if(dnsServer2 != OPENDNS_SERVER_1 && dnsServer2 != OPENDNS_SERVER_2){
		hm.util.reportFieldError($(this).get(0), '<s:text name="glasgow_16.error.home.hmservice.opendns.dnsserver"><s:param>'+OPENDNS_SERVER_1+'</s:param><s:param>'+OPENDNS_SERVER_2+'</s:param></s:text>');
		$(this).focus();
	}
});

$("#openDNSUserName").blur(function(){
	var activeUserName = $(this).val();
	var url = "<s:url action='hmServices' includeParams='none' />?operation=checkActiveUserName&activeUserName="+activeUserName+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : successCheckActiveName, failure : resultDoNothing, timeout: 60000}, null);
});

function successCheckActiveName(o){
	eval("var response = " + o.responseText);
	if(response.activeFlag){
		$("#note_userswitch").css("display","block");
		$("#note_opendnsmapping").css("display","none");
		$("#section_opendnsmapping").css("display","none");
	}else{
		$("#note_userswitch").css("display","none");
		$("#note_opendnsmapping").css("display","none");
		$("#section_opendnsmapping").css("display","block");
	}
}

function refreshOpenDNSDevice(){
	var url = "";
	url = "<s:url action='hmServices' includeParams='none' />?operation=refreshOpenDNSDevice&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succRefreshOpenDNSDevice, failure : resultDoNothing, timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
		waitingPanel.setHeader("Accessing OpenDNS Identity settings...");
	}
}

function succRefreshOpenDNSDevice(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var response = " + o.responseText);
	if(response.noupdate == true){		
		$("#info_openDNSSyncNote").html(response.msg.valueOf());
		var devices = response.devices;
		for(var j=0;j<devices.length;j++){
	 		$("select[name='openDNSDevices'] option[value='"+devices[j].deviceid+"']").each(function(){
	 			$(this).remove();
			})
		}

		var userprofiles = response.userprofiles;
		for(var i=0; i < userprofiles.length; i++){
	 		var id = "openDNSDevice_" + userprofiles[i].profileid;
			var query = id + " option[value='"+ userprofiles[i].deviceid +"']";
			$("#" + query).attr("selected","selected"); 
			
			var did = "openDNSDId_" + userprofiles[i].profileid;
			$("#"+did).html(userprofiles[i].did);
		} 
	}
	else{
		$("#info_openDNSSyncNote").html(response.msg.valueOf());	
	}
	
	$("#note_openDNSSyncNote").css("display","");
    noteTimer = setTimeout("hideOpenDNSSyncNote()", 10 * 1000);  // 5 seconds
}

function hideOpenDNSSyncNote(){
	$("#info_openDNSSyncNote").html("");
	$("#error_openDNSSyncNote").html("");
	$("#note_openDNSSyncNote").css("display","none");
}

var ssidsArray = null;
function getULText(strArray) {
	var array = strArray.split(",");
	var text = "<ul>";
	for(var i=0;i<array.length;i++) {
		text += "<li>" + array[i].trim() + "</li>";
	}
	text += "</ul>";
	return text;
}
function succCheckGASSID(o) {
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
    eval("var details = " + o.responseText);
    if(details.succ) {
    	ssidsArray = details.values;
    }	
}
function failCheckGASSID(o) {
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
}
function dislayGuestAanalytics(flag) {
	if(flag) {
		$('#generalSettingsGA, #troubleshootingGA').show();
	} else {
		$('#generalSettingsGA, #troubleshootingGA').hide();
	}
}
function enableGuestAanalytics(flag) {
    if(!flag && ssidsArray === null) {
          var YUC = YAHOO.util.Connect;
          var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=checkGASSIDs&ignore="+new Date().getTime();
          YUC.asyncRequest('GET', url, 
                  {success : succCheckGASSID, failure : failCheckGASSID, timeout: 60000}, null);
          if(null == waitingPanel) {
              createWaitingPanel();
          }
          waitingPanel.setHeader("Checking SSIDs...");
          waitingPanel.show();
    }
}
function checkACPPService() {
	$('#troubleshootingMsgGA').hide();
    var YUC = YAHOO.util.Connect;
    var url = "<s:url action='hmServices' includeParams='none' />?operation=checkGA&ignore="+new Date().getTime();
    YUC.asyncRequest('GET', url, 
            {success : succCheckGA, failure : failCheckGA, timeout: 60*1000*10}, null);
    if(null == waitingPanel) {
        createWaitingPanel();
    }
    waitingPanel.setHeader("Checking...");
    waitingPanel.show();
}
function succCheckGA(o) {
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
    eval("var details = " + o.responseText);
    if(details.succ) {
    	$('#trbGAInfoMessage').text(details.msg);
    	$('#trbGAErrorMessage').text('');
    	$('#troubleshootingMsgGA').show();
    } else {
    	$('#trbGAErrorMessage').text(details.msg);
    	$('#trbGAInfoMessage').text('');
    	$('#troubleshootingMsgGA').show();
    }
}
function failCheckGA(o) {
    $('#trbGAErrorMessage').text('<s:text name="home.services.guestanalytics.check.service.fail"/>');
    $('#trbGAInfoMessage').text('');
	$('#troubleshootingMsgGA').show();
    if(null != waitingPanel) {
        waitingPanel.hide();
        waitingPanel.setHeader("Sending test email...");
    }
}
</script>
