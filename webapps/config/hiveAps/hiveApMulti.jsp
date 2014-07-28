<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script src="<s:url value="/js/hm.options.js" includeParams="none" />?v=<s:property value='verParam' />"></script>
<script
	src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var noChangeKey = '[-No Change-]'
var formName = 'hiveAp';
var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;
var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;
var MODEL_330 = <%=HiveAp.HIVEAP_MODEL_330%>;
var MODEL_350 = <%=HiveAp.HIVEAP_MODEL_350%>;
var MODEL_370 = <%=HiveAp.HIVEAP_MODEL_370%>;
var MODEL_390 = <%=HiveAp.HIVEAP_MODEL_390%>;
var MODEL_230 = <%=HiveAp.HIVEAP_MODEL_230%>;
var MODEL_BR100 = <%=HiveAp.HIVEAP_MODEL_BR100%>;
var MODEL_BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var MODEL_BR200WP = <%=HiveAp.HIVEAP_MODEL_BR200_WP%>;
var MODEL_BR200_LTE_VZ = <%=HiveAp.HIVEAP_MODEL_BR200_LTE_VZ%>;
var MODEL_AP170 = <%=HiveAp.HIVEAP_MODEL_170%>;

var ETH_SPEED_AUTO = <%=AhInterface.ETH_SPEED_AUTO%>;
var ETH_SPEED_1000M = <%=AhInterface.ETH_SPEED_1000M%>;
var ETH_DUPLEX_AUTO = <%=AhInterface.ETH_DUPLEX_AUTO%>;
var ETH_DUPLEX_HALF = <%=AhInterface.ETH_DUPLEX_HALF%>;
var IF_ADMIN_STATUS_UP = <%=AhInterface.ADMIN_STATE_UP%>;
var IF_ADMIN_STATUS_DOWN = <%=AhInterface.ADMIN_STATE_DOWM%>;
var OPERATION_MODE_BACKHAUL = <%=AhInterface.OPERATION_MODE_BACKHAUL%>;
var OPERATION_MODE_ACCESS = <%=AhInterface.OPERATION_MODE_ACCESS%>;
var OPERATION_MODE_DUAL = <%=AhInterface.OPERATION_MODE_DUAL%>;

function is11nHiveAP(){
	var selectApModel = document.forms[formName].apModelType.value;
	if(selectApModel == MODEL_340 || selectApModel == MODEL_320 
			|| selectApModel == MODEL_380 || selectApModel == MODEL_120
			|| selectApModel == MODEL_121 || selectApModel == MODEL_141
			|| selectApModel == MODEL_110  
			|| selectApModel == MODEL_330 || selectApModel == MODEL_350 
			|| selectApModel == MODEL_370 || selectApModel == MODEL_390
			|| selectApModel == MODEL_BR100 || selectApModel == MODEL_BR200WP
			|| selectApModel == MODEL_AP170 || selectApModel == MODEL_BR200 
			|| selectApModel == MODEL_BR200_LTE_VZ
			|| selectApModel == MODEL_230){
		return true;
	}
	return false;
}

function onLoadPage() {
    if (document.forms[formName].operation.value == 'multiEdit') {
		initValue();
	} else {
		var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
		if (nativeVlanElement.value==0) {
			nativeVlanElement.value=noChangeKey;
		}
		var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
		if (mgtVlanElement.value==0) {
			mgtVlanElement.value=noChangeKey;
		}
		
	 	var deviceTxRetryElement = document.getElementById(formName + "_dataSource_deviceTxRetry");
		if (deviceTxRetryElement.value == -1) {
			deviceTxRetryElement.value = noChangeKey;
		}
		
		var clientTxRetryElement = document.getElementById(formName + "_dataSource_clientTxRetry"); 
		if (clientTxRetryElement.value == -1) {
			clientTxRetryElement.value = noChangeKey;
		}
	}
    var expanding_ip = <s:property value="%{expanding_ip}"/>;
	if(document.forms[formName].operation.value == 'multiEdit' || expanding_ip){
       	showCreateSection('ip');
    }
	//added for the when new added radioProfile is 5GHZ in the navigation mode
	if(document.getElementById(formName + "_wifi0RadioProfile") && 
			document.getElementById(formName + "_wifi0RadioProfile").value == -3){
		selectRadioProfile(document.getElementById(formName + "_wifi0RadioProfile"));
	}  
}

function onloadPageforJsonMode(){
	onLoadPage();
}

function initValue() {
	document.getElementById(formName + "_configTemplate").value = -3;
	document.getElementById(formName + "_topology").value = -3;
	
	document.getElementById(formName + "_dataSource_nativeVlan").value = noChangeKey;
	document.getElementById(formName + "_dataSource_mgtVlan").value = noChangeKey;

	document.getElementById(formName + "_dataSource_gateway").value = noChangeKey;
	document.getElementById(formName + "_dataSource_location").value = noChangeKey;
	
 	document.getElementById(formName + "_dataSource_deviceTxRetry").value = noChangeKey;
	document.getElementById(formName + "_dataSource_clientTxRetry").value = noChangeKey; 
	
	var eth0AllowVlan = document.getElementById(formName + "_dataSource_eth0_allowedVlan");
	if(eth0AllowVlan){
		eth0AllowVlan.value = noChangeKey;
	}
	//document.getElementById(formName + "_dataSource_classificationTag1").value = noChangeKey;
	//document.getElementById(formName + "_dataSource_classificationTag2").value = noChangeKey;
	//document.getElementById(formName + "_dataSource_classificationTag3").value = noChangeKey;
	
	document.getElementById(formName + "_dataSource_distributedPriority").value = -3;

	document.getElementById(formName + "_dataSource_cfgAdminUser").value = noChangeKey;
	document.getElementById("cfgAdminPassword").value = '';
	document.getElementById("cfCfgAdminPassword").value = '';

	document.getElementById(formName + "_dataSource_cfgReadOnlyUser").value = noChangeKey;
	document.getElementById("cfgReadOnlyPassword").value = '';
	document.getElementById("confirmCfgReadOnlyPassword").value = '';
	

	document.getElementById(formName + "_branchRouterEth0_adminState").value=-3;
	document.getElementById(formName + "_branchRouterEth1_adminState").value=-3;
	document.getElementById(formName + "_branchRouterEth2_adminState").value=-3;
	document.getElementById(formName + "_branchRouterEth3_adminState").value=-3;
	document.getElementById(formName + "_branchRouterEth4_adminState").value=-3;
	document.getElementById(formName + "_branchRouterUSB_adminState").value=-3;
	document.getElementById(formName + "_branchRouterLTE_adminState").value=-3;
	
	document.getElementById(formName + "_branchRouterEth0_duplex").value=-3;
	document.getElementById(formName + "_branchRouterEth1_duplex").value=-3;
	document.getElementById(formName + "_branchRouterEth2_duplex").value=-3;
	document.getElementById(formName + "_branchRouterEth3_duplex").value=-3;
	document.getElementById(formName + "_branchRouterEth4_duplex").value=-3;
	document.getElementById(formName + "_branchRouterUSB_duplex").value=-3;
	document.getElementById(formName + "_branchRouterLTE_duplex").value=-3;
	
	document.getElementById(formName + "_branchRouterEth0_speed").value=-3;
	document.getElementById(formName + "_branchRouterEth1_speed").value=-3;
	document.getElementById(formName + "_branchRouterEth2_speed").value=-3;
	document.getElementById(formName + "_branchRouterEth3_speed").value=-3;
	document.getElementById(formName + "_branchRouterEth4_speed").value=-3;
	document.getElementById(formName + "_branchRouterUSB_speed").value=-3;
	document.getElementById(formName + "_branchRouterLTE_speed").value=-3;
	
}


function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode == true}">
			submitHiveApAction(operation);
		</s:if>
		<s:else>
			if (operation == 'multiUpdate') {
				showProcessing();
			}
		
			var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
			if (nativeVlanElement.value ==noChangeKey) {
				nativeVlanElement.value = 0;
			}
			var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
			if (mgtVlanElement.value ==noChangeKey) {
				mgtVlanElement.value = 0;
			}
			
		 	var deviceTxRetryElement = document.getElementById(formName + "_dataSource_deviceTxRetry");
			if (deviceTxRetryElement.value ==noChangeKey) {
				deviceTxRetryElement.value = -1;
			}
			
			var clientTxRetryElement = document.getElementById(formName + "_dataSource_clientTxRetry"); 
			if (clientTxRetryElement.value ==noChangeKey) {
				clientTxRetryElement.value = -1;
			}
			
			if(operation == 'cancel') {
				mgtVlanElement.value = 0;
				nativeVlanElement.value = 0;
 				deviceTxRetryElement.value = -1;
				clientTxRetryElement.value = -1; 
			}
			
			if(operation == 'addDynamicIp2'){
				document.getElementById("expanding_ip").value="false";
			}
			document.forms[formName].operation.value = operation;
			Get(formName + "_dataSource_advSettingsDisplayStyle").value = Get("advancedSettings").style.display;
			Get(formName + "_dataSource_bjgwConfigDisplayStyle").value = Get("bonjourGatewayConfig").style.display;
			Get(formName + "_dataSource_credentialsDisplayStyle").value = Get("credentials").style.display;
			Get(formName + "_dataSource_routingDisplayStyle").value = Get("routing").style.display;
			//add handler to deal with something before form submit.
			beforeSubmitAction(document.forms[formName]);
		    document.forms[formName].submit();
		</s:else>
	}
}

function validMultiEditHiveAPForJson(operation){
	if (!validate(operation)) {
		return false;
	}
	if (operation == 'multiUpdate') {
		showProcessing();
	}

	var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
	if (nativeVlanElement.value ==noChangeKey) {
		nativeVlanElement.value = 0;
	}
	var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
	if (mgtVlanElement.value ==noChangeKey) {
		mgtVlanElement.value = 0;
	}
	
 	var deviceTxRetryElement = document.getElementById(formName + "_dataSource_deviceTxRetry");
	if (deviceTxRetryElement.value ==noChangeKey) {
		deviceTxRetryElement.value = -1;
	}
	
	var clientTxRetryElement = document.getElementById(formName + "_dataSource_clientTxRetry"); 
	if (clientTxRetryElement.value ==noChangeKey) {
		clientTxRetryElement.value = -1;
	}
	
	if(operation == 'addDynamicIp2'){
		document.getElementById("expanding_ip").value="false";
	}
	Get(formName + "_dataSource_advSettingsDisplayStyle").value = Get("advancedSettings").style.display;
	Get(formName + "_dataSource_bjgwConfigDisplayStyle").value = Get("bonjourGatewayConfig").style.display;
	Get(formName + "_dataSource_credentialsDisplayStyle").value = Get("credentials").style.display;
	Get(formName + "_dataSource_routingDisplayStyle").value = Get("routing").style.display;
	return true;
}

function showAdvancedSettingsContent(){
	showHideContent("advancedSettings","");
}

function showbonjourGatewayConfigContent(){
	showHideContent("bonjourGatewayConfig","");
}

function showCredentialsContent(){
	showHideContent("credentials","");
}


function validate(operation) {
	if(operation == "<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"){
		return true;
	}

	if(!validateNetworkSetting(operation)){
		return false;
	}

	if(!validateInterfaceModes(operation)){
		return false;
	}

	if(!validateLocation(operation)){
		return false;
	}
	
	if(!validateNativeVlan(operation)){
		return false;
	}

	if(!validateMgtVlan(operation)){
		return false;
	}

	if(!validateAllowedVlan(operation)){
		return false;
	}
	
	if(!validateSpeedAndDuplex(operation)){
		return false;
	}
	
	if (!validateBonjourGatewayConfig(operation)){
		return false;
	}
	
	if(!validateCfgUsernameAndPassword(operation)){
		return false;
	}

	if(!checkUserDupleWithReadOnlyUser(operation)){
		return false;
	}

	if(!validateReadOnlyCfgUsernameAndPassword(operation)){
		return false;
	}

	if (!validateDtlsSettings(operation)) {
		return false;
	}

	if(!validateCapwapIp(operation)){
		return false;
	}

	if(!validateIpRoute(operation)){
		return false;
	}

	if(operation == "editTemplateMulti"){
		var value = hm.util.validateListSelection(formName + "_configTemplate");
		if(value < 0){
			return false
		}else{
			document.forms[formName].configTemplate.value = value;
		}
	}

	if(operation == "editSchedulerMulti"){
		var value = hm.util.validateListSelection(formName + "_scheduler");
		if(value < 0){
			return false
		}else{
			document.forms[formName].scheduler.value = value;
		}
	}

	if(operation == "editCapwapIpMulti"){
		var value = hm.util.validateListSelection("capwapSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].capwapIp.value = value;
		}
	}

	if(operation == "editCapwapBackupIpMulti"){
		var value = hm.util.validateListSelection("capwapBackupSelect");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].capwapBackupIp.value = value;
		}
	}
	if(operation == "editWifi0RadioProfileMulti"){
		var value = hm.util.validateListSelection(formName + "_wifi0RadioProfile");
		if(value < 0){
			return false;
		}
	}
	if(operation == "editWifi1RadioProfileMulti"){
		var value = hm.util.validateListSelection(formName + "_wifi1RadioProfile");
		if(value < 0){
			return false;
		}
	}
	
	if(operation == "editSuppCLIMulti"){
		var value = hm.util.validateListSelection(formName + "_supplementalCLIId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].supplementalCLIId.value = value;
		}
	}
	
	if(!validateTxRetryRate(operation)){
		return false;
	}

	return true;
}

function validateOptionNewUsernameAndPassword(operation, name, password, confirm){
	if(operation == 'multiUpdate'){
		if ((name.value.length > 0 && name.value != noChangeKey) || password.value.length > 0 || confirm.value.length > 0){
			var nameValue = (name.value == noChangeKey) ? "" : name.value;
			var message = hm.util.validateUsername(nameValue, '<s:text name="hiveAp.newUser" />');
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
	if(operation == 'multiUpdate'){
		var usernameElement = document.getElementById(formName + "_dataSource_cfgAdminUser");
		var readOnlyElement = document.getElementById(formName + "_dataSource_cfgReadOnlyUser");
		if((usernameElement.value != noChangeKey && usernameElement.value.length > 0 )
			&& ( readOnlyElement.value != noChangeKey && readOnlyElement.value.length > 0)){
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
	if(operation == 'multiUpdate'){

		var gatewayElement = document.getElementById(formName + "_dataSource_gateway");

		if (gatewayElement.value == noChangeKey) {
			return true;
		}

   		if (gatewayElement.value.length == 0) {
            hm.util.reportFieldError(gatewayElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
            gatewayElement.focus();
            return false;
   		}
		if (! hm.util.validateIpAddress(gatewayElement.value)) {
			hm.util.reportFieldError(gatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
			gatewayElement.focus();
			return false;
		}

	}
	return true;
}

function validateInterfaceModes(operation){
	if (operation == 'multiUpdate'){
		var wifi0OperationModeEl = document.getElementById(formName+"_dataSource_wifi0_operationMode");
		var wifi1OperationModeEl = document.getElementById(formName+"_dataSource_wifi1_operationMode");
		var eth0OperationModeEl = document.getElementById(formName+"_dataSource_eth0_operationMode");
		var eth0AdminStateEl = document.getElementById(formName+"_dataSource_eth0_adminState");

		// all are no-change is OK
		var allNoChanged = true;
		if(wifi0OperationModeEl && wifi0OperationModeEl.value != -3){allNoChanged = false;}
		if(wifi1OperationModeEl && wifi1OperationModeEl.value != -3){allNoChanged = false;}
		if(eth0OperationModeEl && eth0OperationModeEl.value != -3){allNoChanged = false;}
		if(allNoChanged){
			return true;
		}else{// one select, other ones must be select in express mode
			<s:if test="%{easyMode}">
			var allChanged = true;
			if(wifi0OperationModeEl && wifi0OperationModeEl.value == -3){allChanged = false;}
			if(wifi1OperationModeEl && wifi1OperationModeEl.value == -3){allChanged = false;}
			if(eth0OperationModeEl && eth0OperationModeEl.value == -3){allChanged = false;}
			if(!allChanged){
				hm.util.reportFieldError(eth0OperationModeEl||wifi0OperationModeEl, '<s:text name="error.hiveAp.operationMode.express"></s:text>');
				return false;
			}
			</s:if>
		}
		
		var haveBackhaulMode = true;
		if(wifi0OperationModeEl && wifi1OperationModeEl){
			haveBackhaulMode = (wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL || wifi1OperationModeEl.value == OPERATION_MODE_BACKHAUL ||
									wifi0OperationModeEl.value == OPERATION_MODE_DUAL || wifi1OperationModeEl.value == OPERATION_MODE_DUAL);
		}else if(wifi0OperationModeEl){
			haveBackhaulMode = (wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL || wifi0OperationModeEl.value == OPERATION_MODE_DUAL);
		}
		if(haveBackhaulMode){//Only need check when there's at least one backhaul for express mode
			<s:if test="%{easyMode}">
				//Not allow customized configuration
				if(wifi0OperationModeEl && wifi1OperationModeEl){
					if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
						hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.express"></s:text>');
						return false;
					}
				}else if(wifi0OperationModeEl && eth0OperationModeEl) {
					if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL && eth0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
						hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.all.express"></s:text>');
						return false;
					}
				}
			</s:if>
			return true;
		}
		if(eth0OperationModeEl && Get("interfaceEth0Div").style.display!="none"){//Only eth0 is allowed modification in this page
			if(eth0OperationModeEl.value != OPERATION_MODE_BACKHAUL){
				hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
				return false;
			} else if(eth0AdminStateEl.value==IF_ADMIN_STATUS_DOWN){
				hm.util.reportFieldError(eth0AdminStateEl, '<s:text name="error.hiveAp.backhaul.downAll"></s:text>');
				return false;
			}
		}//else{
			// no backhaul mode interface is not allowed
		//	hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.operationMode.backhaul"></s:text>');
		//	return false;
		//}
		<s:if test="%{easyMode}">
			//Not allow customized configuration
			if(wifi0OperationModeEl && wifi1OperationModeEl){
				if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.express"></s:text>');
					return false;
				}
			} else if(wifi0OperationModeEl && eth0OperationModeEl){
				if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL && eth0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.backhaul.all.express"></s:text>');
					return false;
				}
			}
		</s:if>
	}
	return true;
}

function validateLocation(operation){
	if(operation == 'multiUpdate'){
		var inputElement = document.getElementById(formName + "_dataSource_location");
		if (inputElement.value != noChangeKey && inputElement.value.length > 0) {
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

function validateBonjourGatewayConfig(operation){
	if(operation == 'multiUpdate'){
		var overrideEl = document.getElementById(formName + "_dataSource_priority");
		if(overrideEl.value != noChangeKey){
			var message = hm.util.validateIntegerRange(overrideEl.value, '<s:text name="hiveAp.bonjour.gateway.priority" />', 0, 255);
		    if (message != null) {
		    	showbonjourGatewayConfigContent();
		        hm.util.reportFieldError(overrideEl, message);
		        overrideEl.focus();
		        return false;
		    }
		}
	}
	return true;
}

function validateNativeVlan(operation){
	var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
	if(operation == 'multiUpdate'){
		if (nativeVlanElement.value.length == 0) {
			hm.util.reportFieldError(nativeVlanElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.nativeVlan" /></s:param></s:text>');
			nativeVlanElement.focus();
			return false;
		}
		if (nativeVlanElement.value != noChangeKey) {
			var message = hm.util.validateIntegerRange(nativeVlanElement.value, '<s:text name="hiveAp.nativeVlan" />',
			                                           <s:property value="1" />,
			                                           <s:property value="4094" />);
			if (message != null) {
				hm.util.reportFieldError(nativeVlanElement, message);
				nativeVlanElement.focus();
				return false;
			}
		}
	}

	return true;
}

function validateMgtVlan(operation){
	var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
	if(operation == 'multiUpdate'){
		if (mgtVlanElement.value.length == 0) {
			hm.util.reportFieldError(mgtVlanElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.mgtVlan" /></s:param></s:text>');
			mgtVlanElement.focus();
			return false;
		}
		if (mgtVlanElement.value != noChangeKey) {
			var message = hm.util.validateIntegerRange(mgtVlanElement.value, '<s:text name="hiveAp.mgtVlan" />',
			                                           <s:property value="1" />,
			                                           <s:property value="4094" />);
			if (message != null) {
				hm.util.reportFieldError(mgtVlanElement, message);
				mgtVlanElement.focus();
				return false;
			}
		}
	}

	return true;
}

function validateTxRetryRate(operation){
	var displayTxRetryRateConfigTrEle = Get("displayTxRetryRateConfigTr"); 
	if(displayTxRetryRateConfigTrEle.style.display != "none" ){
		var deviceTxRetryElement = document.getElementById(formName + "_dataSource_deviceTxRetry");
		
		if(operation == 'multiUpdate'){
			if (deviceTxRetryElement.value.length == 0) {
				hm.util.reportFieldError(deviceTxRetryElement, '<s:text name="error.requiredField"><s:param><s:text name="config.device.reportSettings.ifTxRetry.device" /></s:param></s:text>');
				deviceTxRetryElement.focus();
				return false;
			}
			if (deviceTxRetryElement.value != noChangeKey) {
				var message = hm.util.validateIntegerRange(deviceTxRetryElement.value, '<s:text name="config.device.reportSettings.ifTxRetry.device" />',
				                                           <s:property value="1" />,
				                                           <s:property value="100" />);
				if (message != null) {
					hm.util.reportFieldError(deviceTxRetryElement, message);
					deviceTxRetryElement.focus();
					return false;
				}
			}
		}
		
		
		var clientTxRetryElement = document.getElementById(formName + "_dataSource_clientTxRetry");
		if(operation == 'multiUpdate'){
			if (clientTxRetryElement.value.length == 0) {
				hm.util.reportFieldError(clientTxRetryElement, '<s:text name="error.requiredField"><s:param><s:text name="config.device.reportSettings.ifTxRetry.client" /></s:param></s:text>');
				clientTxRetryElement.focus();
				return false;
			}
			if (clientTxRetryElement.value != noChangeKey) {
				var message = hm.util.validateIntegerRange(clientTxRetryElement.value, '<s:text name="config.device.reportSettings.ifTxRetry.client" />',
				                                           <s:property value="1" />,
				                                           <s:property value="100" />);
				if (message != null) {
					hm.util.reportFieldError(clientTxRetryElement, message);
					clientTxRetryElement.focus();
					return false;
				}
			}
		}
	}
	
	return true;
}

function validateAllowedVlan(operation){
	var eth0AllowedVlanEl = document.getElementById(formName + "_dataSource_eth0_allowedVlan");
	if(operation == 'multiUpdate' && eth0AllowedVlanEl && eth0AllowedVlanEl.value != noChangeKey){
		if(!validateAllowVlanFormat(eth0AllowedVlanEl)){
			eth0AllowedVlanEl.focus();
			return false;
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

function validateSpeedAndDuplex(operation){
	if(operation == 'multiUpdate'){
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_dataSource_eth0_duplex"),document.getElementById(formName + "_dataSource_eth0_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth0_duplex"),document.getElementById(formName + "_branchRouterEth0_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth1_duplex"),document.getElementById(formName + "_branchRouterEth1_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth2_duplex"),document.getElementById(formName + "_branchRouterEth2_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth3_duplex"),document.getElementById(formName + "_branchRouterEth3_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterEth4_duplex"),document.getElementById(formName + "_branchRouterEth4_speed"))) {
			return false;
		}
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterUSB_duplex"),document.getElementById(formName + "_branchRouterUSB_speed"))) {
			return false;
		}
		
		if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_branchRouterLTE_duplex"),document.getElementById(formName + "_branchRouterLTE_speed"))) {
			return false;
		}

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


function operationModeChanged(selectEl, wifiName){
	if ("wifi1" == wifiName){
		//change channel list for DFS
		var wifi1RadioProfileId = document.getElementById(formName + "_wifi1RadioProfile").value;
		getWifi1Channels(wifi1RadioProfileId, selectEl.value);
	} else if("wifi0" == wifiName){
		var wifi0RadioProfileId = document.getElementById(formName + "_wifi0RadioProfile").value;
		getWifi0Channels(wifi0RadioProfileId, selectEl.value);
	}
}

function selectRadioProfile(cb) {
	var wifi0RadioProfileElement = document.getElementById(formName + "_wifi0RadioProfile");
	var wifi1RadioProfileElement = document.getElementById(formName + "_wifi1RadioProfile");
	
	if (cb.value == -3) {
		//reset radio mode label to [no change]
		if(cb == wifi0RadioProfileElement){// wifi0
			var wifi0ModeEl = document.getElementById("wifi0RadioMode");
			wifi0ModeEl.replaceChild(document.createTextNode("<s:property value="%{strNoChange}" />"), wifi0ModeEl.childNodes[0]);
			<s:if test="%{is110HiveAP}">
			var wifi0labelEl = document.getElementById("wifi0Label");
			wifi0labelEl.replaceChild(document.createTextNode(""), wifi0labelEl.childNodes[0]);
			</s:if>
			<s:if test="%{isBr100}">
			//added for the when new added radioProfile is 5GHZ in the navigation mode
			var wifi0labelEl = document.getElementById("wifi0Label");
			wifi0labelEl.replaceChild(document.createTextNode("2.4 GHz"), wifi0labelEl.childNodes[0]);
			</s:if>
		}else if(cb == wifi1RadioProfileElement){// wifi1
			var wifi1ModeEl = document.getElementById("wifi1RadioMode");
			wifi1ModeEl.replaceChild(document.createTextNode("<s:property value="%{strNoChange}" />"), wifi1ModeEl.childNodes[0]);
			<s:if test="%{is110HiveAP}">
			var wifi1labelEl = document.getElementById("wifi1Label");
			wifi1labelEl.replaceChild(document.createTextNode(""), wifi1labelEl.childNodes[0]);
			</s:if>
		}
		//return ;
	}

	if (cb.value != -2 ) {
		if(cb.id == (formName + "_wifi0RadioProfile")){
			var operationMode = document.getElementById(formName + "_dataSource_wifi0_operationMode").value;
			getWifi0Channels(cb.value, operationMode);
		}else if(cb.id == (formName + "_wifi1RadioProfile")){
			var operationMode = document.getElementById(formName + "_dataSource_wifi1_operationMode").value;
			getWifi1Channels(cb.value, operationMode);
		}
	}
}

function newWifi0RadioProfile(){
	// create a new radio profile
	if(!is11nHiveAP()){
		document.forms[formName].radioType.value = "bg";//default radio type
	}else{
		document.forms[formName].radioType.value = "ng";//default radio type
	}
	submitAction('newWifi0RadioProfileMulti');
}

function newWifi1RadioProfile(){
	// create a new radio profile
	if(!is11nHiveAP()){
		document.forms[formName].radioType.value = "a";//default radio type
	}else{
		document.forms[formName].radioType.value = "na";//default radio type
	}
	submitAction('newWifi1RadioProfileMulti');
}

function editWifi0RadioProfile(){
	// edit radio profile
	if(!is11nHiveAP()){
		document.forms[formName].radioType.value = "bg";//default radio type
	}else{
		document.forms[formName].radioType.value = "ng";//default radio type
	}
	submitAction('editWifi0RadioProfileMulti');
}

function editWifi1RadioProfile(){
	// edit radio profile
	if(!is11nHiveAP()){
		document.forms[formName].radioType.value = "a";//default radio type
	}else{
		document.forms[formName].radioType.value = "na";//default radio type
	}
	submitAction('editWifi1RadioProfileMulti');
}

function getWifi0Channels(profileId, wifi0OperationMode) {
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi0ChannelsMulti" + "&wifi0RadioProfile="+profileId + "&wifi0OperationMode="+wifi0OperationMode+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processRadioInfos, failure : connectFailed, argument: "wifi0"}, null);
}

function getWifi1Channels(profileId, wifi1OperationMode) {
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=wifi1ChannelsMulti" + "&wifi1RadioProfile="+profileId + "&wifi1OperationMode="+wifi1OperationMode+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processRadioInfos, failure : connectFailed, argument: "wifi1"}, null);
}

var processRadioInfos = function(o){
	eval("var details = " + o.responseText);

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
			channel1El.length=details.c.length+1;
			channel1El.options[0].value = -3;
			channel1El.options[0].text = noChangeKey;
			for(var i = 0; i < details.c.length; i ++) {
				channel1El.options[i+1].value = details.c[i].key;
				channel1El.options[i+1].text = details.c[i].value;
				channel1El.options[i+1].selected = details.c[i].key==channel1;
			}
		}
	}else{
		if(details.m){
			var radioModeEl = document.getElementById("wifi0RadioMode");
			radioModeEl.replaceChild(document.createTextNode(details.m), radioModeEl.childNodes[0]);
		}
		if(details.l){
			var wifi0labelEl = document.getElementById("wifi0Label");
			wifi0labelEl.replaceChild(document.createTextNode(details.l), wifi0labelEl.childNodes[0]);
		}
		//update channel
		if(details.c){
			var channel0El = document.getElementById(formName + "_dataSource_wifi0_channel");
			var channel0 = channel0El.value;
			channel0El.length=0;
			channel0El.length=details.c.length+1;
			channel0El.options[0].value = -3;
			channel0El.options[0].text = noChangeKey;
			for(var i = 0; i < details.c.length; i ++) {
				channel0El.options[i+1].value = details.c[i].key;
				channel0El.options[i+1].text = details.c[i].value;
				channel0El.options[i+1].selected = details.c[i].key==channel0;
			}
		}
	}
};

var connectFailed = function(o)
{
    //alert("connection failed.");
};


function changeSpeed(selectBox) {
	if(selectBox.value == 3) {
		if(!is11nHiveAP()) {
			hm.util.reportFieldError(selectBox, '<s:text name="error.hiveAp.noSpeed" />');
			selectBox.selectedIndex = 0;
		}
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
function validateDtlsSettings(operation){
	if(operation == 'multiUpdate'){
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

function validateCapwapIp(operation) {
	var capwapIpNames = document.getElementById("capwapSelect");
	var capwapIpValue = document.getElementById("dataSource.capwapText");
	var showError = document.getElementById("errorDisplay");

	var capwapBackupIpNames = document.getElementById("capwapBackupSelect");
	var capwapBackupIpValue = document.getElementById("dataSource.capwapBackupText");
	var showBackupError = document.getElementById("errorBackupDisplay");
	
    if ("" != capwapIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(capwapIpNames, capwapIpValue)) {
	    	if(operation == 'multiUpdate'){
				if (!hm.util.validateIpAddress(capwapIpValue.value)) {
					var message = hm.util.validateName(capwapIpValue.value, '<s:text name="hiveAp.capwap.server" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showError, message);
				   		showCredentialsContent();
				       	capwapIpValue.focus();
				       	return false;
				   	}
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
	    	if(operation == 'multiUpdate'){
				if (!hm.util.validateIpAddress(capwapBackupIpValue.value)) {
					var message = hm.util.validateName(capwapBackupIpValue.value, '<s:text name="hiveAp.capwap.server.backup" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showBackupError, message);
				   		showCredentialsContent();
				       	capwapBackupIpValue.focus();
				       	return false;
				   	}
				}
	    	}
			document.forms[formName].capwapBackupIp.value = -1;
	    } else {
			document.forms[formName].capwapBackupIp.value = capwapBackupIpNames.options[capwapBackupIpNames.selectedIndex].value;
	}
	} else {
		document.forms[formName].capwapBackupIp.value = -1;
	}
    return true;
}

function requestTemplateInfo(selectEl){
	var templateId = selectEl.value;
	url = "<s:url action='hiveAp' includeParams='none' />?operation=requestTemplate" + "&configTemplate=" + templateId + "&ignore="+ + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : getTemplateInfo}, null);
}

var getTemplateInfo = function(o){
	eval("var details = " + o.responseText);
	var vpn = details.vpn;

	if(vpn){
		document.getElementById("vpnRuleTr").style.display = "";
	}else{
		document.getElementById("vpnRuleTr").style.display = "none";
		document.getElementById(formName + "_dataSource_vpnMark").value = -3;
	}
}

function validateIpRoute(operation){
	if(operation == 'addIpRouteMulti'){
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
	if (operation == 'removeIpRouteMulti'){
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

function showCreateSection(type) {
    if(type=='ip'){
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
     }
}
function hideCreateSection(type) {
    if(type=='ip'){
        hm.util.hide('createButtonIp');
        hm.util.show('newButtonIp');
        hm.util.hide('createSectionIp');
        document.getElementById("expanding_ip").value="false";
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

function changeLayer3Route(checked){
	var ipRouteObjects = document.getElementById("ipRouteObjects");
	if(ipRouteObjects){
		ipRouteObjects.style.display = checked?"":"none";
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var adminStatusValue, operationMode;
	
	if(details.wifi0){
		var wifi0 = document.getElementById(formName + "_wifi0RadioProfile");
		var wifi0Id = wifi0.value;
		wifi0.length=0;
		wifi0.length=details.wifi0.length;
		for(var i = 0; i < details.wifi0.length; i ++) {
			wifi0.options[i].value = details.wifi0[i].id;
			wifi0.options[i].text = details.wifi0[i].v;
			wifi0.options[i].selected = details.wifi0[i].id==wifi0Id;
		}
		wifi0.value = callback.argument[0];
		selectRadioProfile(wifi0);
		
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
	}

	if(details.wifi1){
		var wifi1 = document.getElementById(formName + "_wifi1RadioProfile");
		var wifi1Id = wifi1.value;
		wifi1.length=0;
		wifi1.length=details.wifi1.length;
		for(var i = 0; i < details.wifi1.length; i ++) {
			wifi1.options[i].value = details.wifi1[i].id;
			wifi1.options[i].text = details.wifi1[i].v;
			wifi1.options[i].selected = details.wifi1[i].id==wifi1Id;
		}
		wifi1.value = callback.argument[0];
		selectRadioProfile(wifi1);
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
	}
};

var detailsFailed = function(o) {
	//alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};
//added for multi edit devices
function updateRadioProfiles(id,radioMode) {
	var params=[id,radioMode];
	callback.argument = params;
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=fetchRadioProfiles"+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
}

function clickMultiChangeLockRealmName(checked) {
	Get(formName + "_dataSource_lockRealmName").checked = false;
	if (checked) {
		Get("multiLockRealmNameTr").style.display="";
	} else {
		Get("multiLockRealmNameTr").style.display="none";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveAp" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		document.writeln('Modify (Multiple) </td>');
	</s:else>
}

function showDealyAlarmEnableMulty(checked){
	var dealyAlarmEnableMultyTr = Get("dealyAlarmEnableMultyTr");
	if(dealyAlarmEnableMultyTr){
		dealyAlarmEnableMultyTr.style.display = checked? "": "none";
	}
}

function showIDMAuthProxy(checked){
	Get("IDMAuthProxyTr").style.display = checked? "" : "none";
}
</script>

<div id="content"><s:form action="hiveAp">
	<s:hidden name="radioType" />
	<s:hidden name="apModelType" value="%{dataSource.hiveApModel}" />
	<s:hidden name="capwapIp" />
	<s:hidden name="capwapBackupIp" />
	<s:hidden name="expanding_ip" id="expanding_ip" value="%{expanding_ip}" />
	<s:hidden name="dataSource.advSettingsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.credentialsDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.bjgwConfigDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.routingDisplayStyle"></s:hidden>
	<s:if test="%{jsonMode}">
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="hmListType"/>
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode==false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore"
						value="<s:text name="button.update"/>" class="button"
						onClick="submitAction('multiUpdate');"
						<s:property value="writeDisabled" />></td>

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
			<s:if test="%{jsonMode==false}">
				<table class="editBox" style="padding: 0 4px 6px 4px;"
					cellspacing="0" cellpadding="0" border="0" width="820px">
			</s:if>
			<s:else>
				<table style="padding: 0 4px 6px 4px;"
					cellspacing="0" cellpadding="0" border="0" width="820px">
			</s:else>
				<tr>
					<td><!-- definition --> <%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td height="4"></td>
								</tr>
								<tr style="display: <s:property value="%{displayConfigTemplate}"/>"
									valign="bottom">
									<td class="labelT1" width="182px"><s:text
										name="hiveAp.template" /></td>
									<td><s:select list="%{configTemplates}" listKey="id"
										listValue="value" name="configTemplate" headerKey="-3"
										headerValue="%{strNoChange}" cssStyle="width: 198px;"
										onchange="requestTemplateInfo(this);" /> 
									</td>
								</tr>
								<tr valign="bottom">
									<td class="labelT1" width="182px"><s:text
										name="hiveAp.topology" /></td>
									<td><s:select list="%{topologys}" listKey="id"
										listValue="value" name="topology" headerKey="-3"
										headerValue="%{strNoChange}" cssStyle="width: 198px;" /></td>
								</tr>
								<tr valign="bottom" style="display:<s:property value="%{displayLocation}"/>">
									<td class="labelT1"><s:text name="hiveAp.location" /></td>
									<td><s:textfield name="dataSource.location" size="24"
										maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" /> <s:text
										name="hiveAp.location.range" /></td>
								</tr>
								<tr valign="bottom" style="display: <s:property value="%{displayGateWay}"/>">
									<td class="labelT1"><s:text name="hiveAp.gateway" /></td>
									<td><s:textfield name="dataSource.gateway" size="24"
										maxlength="%{cfgGatewayLength}" /></td>
								</tr>
								<tr valign="bottom" style="display: <s:property value="%{displayVlan}"/>">
									<td class="labelT1"><s:text name="hiveAp.nativeVlan" /></td>
									<td><s:textfield name="dataSource.nativeVlan" size="24"
										maxlength="13" /> <s:text name="hiveAp.nativeVlan.range" /></td>
								</tr>
								<tr valign="bottom" style="display: <s:property value="%{displayVlan}"/>">
									<td class="labelT1"><s:text name="hiveAp.mgtVlan" /></td>
									<td><s:textfield name="dataSource.mgtVlan" size="24"
										maxlength="13" /> <s:text name="hiveAp.nativeVlan.range" /></td>
								</tr>
								<s:if test="%{easyMode}">
									<tr valign="bottom">
										<td class="labelT1"><s:text
											name="config.configTemplate.enableAirTime" /></td>
										<td><s:select name="strEnableDas"
											list="#{'-1':'[-No Change-]', '1':'True', '2':'False'}"
											cssStyle="width:150px;" /></td>
									</tr>
								</s:if>
								<tr valign="bottom" id="vpnRuleTr"
									style="display: <s:property value="vpnRuleStyleOfMulti"/>">
									<td class="labelT1"><s:text name="hiveAp.server.vpn.role" /></td>
									<td><s:select list="%{enumVPNMarkWithoutServerType}"
										headerKey="-3" headerValue="%{strNoChange}" listKey="key"
										listValue="value" name="dataSource.vpnMark"
										cssStyle="width: 152px;" /></td>
								</tr>
							</table>
							</td>
						</tr>
						
						<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
													<td valign="top" width="100%">
								<fieldset><legend><s:text
														name="hiveAp.classification.tag" /></legend>
										<table cellspacing="0" cellpadding="0" border="0"
														width="100%" class="embedded">
														<tr>
													<td height="10"></td>
												</tr>																								
												<tr id="ap2ClassifierTagContainer"> 	</tr>
											</table>
								  </fieldset>
						  </td>
			           </tr>
						
						<tr>
							<td height="5px"></td>
						</tr>
						<s:if test="%{is20HiveAP || is300HiveAP || is110HiveAP || isBr100}">
							<tr>
								<td valign="top" width="100%">
								<fieldset>
								<table cellspacing="0" cellpadding="0" border="0" width="100%"
									class="embedded">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<th align="left" width="60px"><s:text
											name="hiveAp.wlanIf" /></th>
										<th align="left" width="105px"><s:text
											name="hiveAp.if.radioMode" /></th>
										<th align="left" width="185px"><s:text
											name="hiveAp.if.radioProfile" /></th>
										<th align="left" width="80px"><s:text
											name="hiveAp.if.adminState" /></th>
										<th align="left" width="100px" style="display: <s:property value="displayMultiOperationModeForWifi"/>"><s:text
											name="hiveAp.if.operationMode" /></th>
										<th align="left" width="100px"><s:text
											name="hiveAp.if.channel" /></th>
										<th align="left"><s:text name="hiveAp.if.power" /></th>
									</tr>										
									<tr>
										<td height="5"></td>
									</tr>
									<s:if test="%{is110HiveAP || isBr100}">
										<tr>
											<td class="list"><span id="wifi0Label"><s:property
												value="%{wifi0Label}" /></span> <s:text name="hiveAp.if.wifi0" /></td>
											<td id="wifi0RadioMode" class="list"><s:property
												value="%{wifi0RadioModeLabel_multiple}" /></td>
											<td class="list" nowrap="nowrap"><s:select name="wifi0RadioProfile"
												list="%{wifi0RadioProfiles}" listKey="id" listValue="value"
												headerKey="-3" headerValue="%{strNoChange}"
												cssStyle="width: 120px;" onchange="selectRadioProfile(this)" />
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />" width="16"
													height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:newWifi0RadioProfile()"><img
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
												headerKey="-3" headerValue="%{strNoChange}" /></td>
											<td class="list" style="display: <s:property value="displayMultiOperationModeForWifi"/>"><s:select
												name="dataSource.wifi0.operationMode"
												value="%{dataSource.wifi0.operationMode}"
												list="%{enumWifiOperationMode}" listKey="key"
												listValue="value" headerKey="-3"
												headerValue="%{strNoChange}"
												onchange="operationModeChanged(this, 'wifi0');" /></td>
											<td class="list"><s:select
												name="dataSource.wifi0.channel"
												value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select name="dataSource.wifi0.power"
												value="%{dataSource.wifi0.power}" list="%{enumPowerType}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
										</tr>
									</s:if>
									<s:else>
										<tr>
											<td class="list"><span id="wifi0Label"><s:property
												value="%{wifi0Label}" /></span> <s:text name="hiveAp.if.wifi0" /></td>
											<td id="wifi0RadioMode" class="list"><s:property
												value="%{wifi0RadioModeLabel_multiple}" /></td>
											<td class="list" nowrap="nowrap"><s:select
												name="wifi0RadioProfile" list="%{wifi0RadioProfiles}"
												listKey="id" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" cssStyle="width: 120px;"
												onchange="selectRadioProfile(this)" /> <s:if
												test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />" width="16"
													height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:newWifi0RadioProfile()"><img
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
												headerKey="-3" headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select
												name="dataSource.wifi0.operationMode"
												value="%{dataSource.wifi0.operationMode}"
												list="%{enumWifiOperationMode}" listKey="key"
												listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select
												name="dataSource.wifi0.channel"
												value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select name="dataSource.wifi0.power"
												value="%{dataSource.wifi0.power}" list="%{enumPowerType}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
										</tr>
										<tr>
											<td class="list"><span id="wifi1Label"><s:property
												value="%{wifi1Label}" /></span> <s:text name="hiveAp.if.wifi1" /></td>
											<td id="wifi1RadioMode" class="list"><s:property
												value="%{wifi1RadioModeLabel_multiple}" /></td>
											<td class="list" nowrap="nowrap"><s:select
												name="wifi1RadioProfile" list="%{wifi1RadioProfiles}"
												listKey="id" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" cssStyle="width: 120px;"
												onchange="selectRadioProfile(this)" /> <s:if
												test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />" width="16"
													height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:newWifi1RadioProfile()"><img
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
												headerKey="-3" headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select
												name="dataSource.wifi1.operationMode"
												value="%{dataSource.wifi1.operationMode}"
												list="%{enumWifiOperationMode}" listKey="key"
												listValue="value" headerKey="-3"
												headerValue="%{strNoChange}"
												onchange="operationModeChanged(this, 'wifi1');" /></td>
											<td class="list"><s:select
												name="dataSource.wifi1.channel"
												value="%{dataSource.wifi1.channel}" list="%{wifi1Channel}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
											<td class="list"><s:select name="dataSource.wifi1.power"
												value="%{dataSource.wifi1.power}" list="%{enumPowerType}"
												listKey="key" listValue="value" headerKey="-3"
												headerValue="%{strNoChange}" /></td>
										</tr>
									</s:else>
								</table>
								</fieldset>
								</td>
							</tr>
							<tr>
								<td height="10px"></td>
							</tr>
						</s:if>
						
						
						
						<tr style="display: <s:property value="displayLanInterface"/>">
							<td height="5px"></td>
						</tr>
						<tr style="display: <s:property value="displayLanInterface"/>" id="interfaceEth0Div">
							<td valign="top" width="100%">
							<fieldset>
							<table cellspacing="0" cellpadding="0" border="0" width="100%"
								class="embedded">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<th align="left" width="245px"><s:text
										name="hiveAp.lanIf" /></th>
									<th align="left"><s:text name="hiveAp.if.adminState" /></th>
									<th align="left"><s:text name="hiveAp.if.allowedVlan" /></th>
									<th align="left"><s:text name="hiveAp.if.operationMode" /></th>
									<th align="left"><s:text name="hiveAp.if.duplex" /></th>
									<th align="left"><s:text name="hiveAp.if.speed" /></th>
								</tr>
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td class="list"><s:text name="hiveAp.if.eth0" /></td>
									<td class="list"><s:select
										name="dataSource.eth0.adminState"
										value="%{dataSource.eth0.adminState}"
										list="%{enumAdminStateType}" listKey="key" listValue="value"
										headerKey="-3" headerValue="%{strNoChange}" /></td>
									<td class="list"><s:textfield
										name="dataSource.eth0.allowedVlan" size="12" maxlength="255"
										title="%{allowedVlanTitle}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
									<td class="list"><s:select
										name="dataSource.eth0.operationMode"
										value="%{dataSource.eth0.operationMode}"
										list="%{enumEthOperationMode}" listKey="key"
										listValue="value" headerKey="-3" headerValue="%{strNoChange}" /></td>
									<td class="list"><s:select name="dataSource.eth0.duplex"
										value="%{dataSource.eth0.duplex}" list="%{enumDuplexType}"
										listKey="key" listValue="value" headerKey="-3"
										headerValue="%{strNoChange}" /></td>
									<td class="list"><s:select name="dataSource.eth0.speed"
										onchange="changeSpeed(this)" value="%{dataSource.eth0.speed}"
										list="%{enumSpeedType}" listKey="key" listValue="value"
										headerKey="-3" headerValue="%{strNoChange}" /></td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						<tr style="display: <s:property value="displayPortInterface"/>">
							<td>
							<fieldset>
							<legend><s:text name="hiveAp.brRouter.port.settings" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<th align="left" width="50px"><s:text
										name="hiveAp.brRouter.port.settings.port" /></th>
									<th align="left" width="80px"><s:text
										name="hiveAp.brRouter.port.settings.role" /></th>
									<th align="left" width="80px"><s:text
										name="hiveAp.brRouter.port.settings.adminState" /></th>
									<th align="left" width="80px"><s:text
										name="hiveAp.brRouter.port.settings.transmissionType" /></th>
									<th align="left" width="80px"><s:text
										name="hiveAp.brRouter.port.settings.speed" /></th>
								</tr>
								<tr>
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth0"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterEth0.adminState"
											value="%{branchRouterEth0.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterEth0.duplex"
											value="%{branchRouterEth0.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterEth0.speed"
											value="%{branchRouterEth0.speed}"
											list="%{enumSpeedType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
								</tr>
								<tr id="br_eth1_setting">
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterEth1.adminState"
											value="%{branchRouterEth1.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterEth1.duplex"
											value="%{branchRouterEth1.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterEth1.speed"
											value="%{branchRouterEth1.speed}"
											list="%{enumSpeedType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
								</tr>
								<tr id="br_eth2_setting">
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterEth2.adminState"
											value="%{branchRouterEth2.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterEth2.duplex"
											value="%{branchRouterEth2.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterEth2.speed"
											value="%{branchRouterEth2.speed}"
											list="%{enumSpeedType}" listKey="key"
											listValue="value" cssStyle="width: 120px;"
											headerKey="-3" headerValue="%{strNoChange}" disabled="true"/></td>
								</tr>
								<tr id="br_eth3_setting">
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth3"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterEth3.adminState"
											value="%{branchRouterEth3.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterEth3.duplex"
											value="%{branchRouterEth3.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"  disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterEth3.speed"
											value="%{branchRouterEth3.speed}"
											list="%{enumSpeedType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
								</tr>
								<tr id="br_eth4_setting">
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth4"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterEth4.adminState"
											value="%{branchRouterEth4.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterEth4.duplex"
											value="%{branchRouterEth4.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterEth4.speed"
											value="%{branchRouterEth4.speed}"
											list="%{enumSpeedType}" listKey="key"
											listValue="value" cssStyle="width: 120px;"
											headerKey="-3" headerValue="%{strNoChange}" disabled="true"/></td>
								</tr>
 								<tr style="display:<s:property value="displayUsbPortInterface"/>">
									<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.usb"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterUSB.adminState"
											value="%{branchRouterUSB.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterUSB.duplex"
											value="%{branchRouterUSB.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterUSB.speed"
											value="%{branchRouterUSB.speed}"
											list="%{enumSpeedType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
								</tr>
 								<tr style="display:<s:property value="displayLTEPortInterface"/>">
									<td class="list"><s:text name="hiveAp.autoProvisioning.device.if.port.cellularmodem"/></td>
									<td class="list"><s:text name="hm.multiEdit.noChange" /></td>
									<td class="list"><s:select
											name="branchRouterLTE.adminState"
											value="%{branchRouterUSB.adminState}"
											list="%{enumAdminStateType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;"/></td>
									<td class="list"><s:select
											name="branchRouterLTE.duplex"
											value="%{branchRouterUSB.duplex}"
											list="%{enumDuplexType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
									<td class="list"><s:select
											name="branchRouterLTE.speed"
											value="%{branchRouterUSB.speed}"
											list="%{enumSpeedType}" listKey="key"
											headerKey="-3" headerValue="%{strNoChange}"
											listValue="value" cssStyle="width: 120px;" disabled="true"/></td>
								</tr>						
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
					</table>
					</td>
				</tr>
				
				
				
				<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
					<td><!-- Optional -->
					<fieldset><legend><s:text
						name="hiveAp.cfg.optional.tag" /></legend>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
							<td><!-- Credentials -->
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td height="5px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.tab.authentication" />','credentials');</script></td>
								</tr>
								<tr>
									<td>
									<div id="credentials"
										style="display: <s:property value="%{dataSource.credentialsDisplayStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
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
																	<td class="labelT1" width="156px"><s:text
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
																	<td class="labelT1" width="156px"><s:text
																		name="hiveAp.newPassword" /></td>
																	<td><s:password name="dataSource.cfgPassword"
																		size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgAdminPassword" showPassword="true" /> <s:textfield
																		name="dataSource.cfgPassword" size="24" maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgAdminPassword_text" cssStyle="display: none;"
																		disabled="true" /> <s:text
																		name="hiveAp.currentPasswordRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="156px"><s:text
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
																		value="%{dataSource.cfgPassword}"
																		cssStyle="display: none;" disabled="true" /></td>
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
																	<td class="labelT1" width="156px"><s:text
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
																	<td class="labelT1" width="156px"><s:text
																		name="hiveAp.newPassword" /></td>
																	<td><s:password
																		name="dataSource.cfgReadOnlyPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgReadOnlyPassword" showPassword="true" /> <s:textfield
																		name="dataSource.cfgReadOnlyPassword" size="24"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"
																		id="cfgReadOnlyPassword_text"
																		cssStyle="display: none;" disabled="true" /> <s:text
																		name="hiveAp.currentPasswordRange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="156px"><s:text
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
																		cssStyle="display: none;" disabled="true" /></td>
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
																	<td class="labelT1" width="156px"><s:text
																		name="hiveAp.dtls.enableChange" /></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td class="labelT1" width="156px"><s:text
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
																	<td class="labelT1" width="156px"><s:text
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
																	<td class="labelT1" width="156px"><s:text
																		name="hiveAp.capwap.server" /></td>
																	<td width="260px"><ah:createOrSelect
																		divId="errorDisplay" list="capwapIpsWithNoChange"
																		typeString="CapwapIpMulti" selectIdName="capwapSelect"
																		inputValueName="dataSource.capwapText" swidth="152px" />
																	</td>
																</tr>
																<tr>
																	<td class="labelT1"><s:text
																		name="hiveAp.capwap.server.backup" /></td>
																	<td><ah:createOrSelect divId="errorBackupDisplay"
																		list="capwapIpsWithNoChange"
																		typeString="CapwapBackupIpMulti"
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
						
						<tr style="display: <s:property value="%{displayRoutering}"/>">
							<td><!-- Routing -->
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
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
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
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
														name="hiveAp.cfg.l3Route" /></legend>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%" class="embedded">
														<tr>
															<td height="10px"></td>
														</tr>
														<tr>
															<td>
															<div>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td width="25px"><s:checkbox
																		name="dataSource.changeLayer3Route"
																		onclick="changeLayer3Route(this.checked)" /></td>
																	<td><s:text name="hiveAp.cfg.ipRoute.change" /></td>
																</tr>
															</table>
															</div>
															</td>
														</tr>
														<tr id="ipRouteObjects"
															style="display: <s:property value="%{ipRouteStyleOfMulti}"/>">
															<td style="padding: 4px 0 0 0;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td height="5px"></td>
																</tr>
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
																				onClick="doRemoveIpRouteMulti();"
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
																				onClick="doAddIpRouteMulti();"></td>
																			<td><input type="button" name="ignore"
																				value="Remove" class="button"
																				onClick="doRemoveIpRouteMulti();"></td>
																			<td><input type="button" name="ignore"
																				value="Cancel" class="button"
																				onClick="hideCreateSection('ip');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr id="headerSectionIp">
																	<th align="left" style="padding-left: 0;" width="60px"><input
																		type="checkbox" id="checkAllIp"
																		onClick="toggleCheckAllIpRoutes(this);"></th>
																	<th align="left" width="250px"><s:text
																		name="hiveAp.cfg.ipRoute.dest" /></th>
																	<th align="left" width="250px"><s:text
																		name="hiveAp.cfg.ipRoute.mask" /></th>
																	<th align="left" width="250px"><s:text
																		name="hiveAp.cfg.ipRoute.gateway" /></th>
																</tr>
																<tr style="display: none;" id="createSectionIp">
																	<td class="listHead">&nbsp;</td>
																	<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																		name="ipRouteIpInput" size="16" maxlength="15" /></td>
																	<td class="listHead" valign="top"><s:textfield
																		name="ipRouteMaskInput" size="16" maxlength="15" /></td>
																	<td class="listHead" valign="top"><s:textfield
																		name="ipRouteGwInput" size="16" maxlength="15" /></td>
																</tr>
																<tr>
																	<td colspan="4">
																		<table id="ipRoutesMultiTblData" cellspacing="0" cellpadding="0" border="0" class="embedded">
																			<s:iterator value="%{dataSource.ipRoutes}"
																				status="status">
																				<tr>
																					<td class="listCheck"><s:checkbox
																						name="ipRouteIndices" fieldValue="%{#status.index}" /></td>
																					<td class="list"><s:property value="sourceIp" /></td>
																					<td class="list"><s:property value="netmask" /></td>
																					<td class="list"><s:property value="gateway" /></td>
																				</tr>
																			</s:iterator>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td colspan="4" width="100%">
																		<table id="ipRouteMultiTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded">
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
																<tr>
																	<td height="30px"></td>
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
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						
						<tr id="deviceBonjourGatewayConfig" style="display: <s:property value="%{displayDeviceBonjourGatewayConfig}"/>">
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
														name="hiveAp.bonjour.gateway.priority" /></label></td>
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
													<td colspan="2" style="padding: 4px 0 4px 6px">
														<s:checkbox name="dataSource.multiChangeLockRealmName"
																onclick="clickMultiChangeLockRealmName(this.checked);"
																id="multiChangeLockRealmName" />
																Change Lock Realm Name
													</td>
												</tr>
												
												<tr id="multiLockRealmNameTr" style="display: <s:property value="%{multiLockRealmNameDisplayStyle}"/>">
													<td class="labelT1" colspan="2" style="padding-left: 40px">
														<s:checkbox name="dataSource.lockRealmName" />
														<s:text name="hiveAp.bonjour.gateway.lockRealmName" /></td>
												</tr>
												<tr>
													<td class="noteInfo" colspan="2" style="padding-left: 8px"><s:text name="hiveAp.bonjour.gateway.realm.name.note"/></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						
						<tr style="display: <s:property value="%{multiServiceSettingsStyle}"/>">
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
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td height="10px"></td>
													</tr>
													<tr>
														<td>
															<fieldset><legend><s:text name="hiveAp.idmAuthProxy.label" /></legend>
																<table cellspacing="0" cellpadding="0" border="0" width="600" class="embedded">
																	<tr>
																		<td height="5" colspan="2"></td>
																	</tr>
																	<tr>
																		<td>
																			<s:checkbox name="dataSource.changeIDMAuthProxy" onclick="showIDMAuthProxy(this.checked);"/>
																			<s:text name="hiveAp.server.idmAuthProxy.change" />
																		</td>
																	</tr>
																	<tr id="IDMAuthProxyTr" style="display:none">
																		<td style="padding: 10px 0px 0px 20px;">
																			<s:checkbox name="dataSource.enableIDMAuthProxy"/>
																			<s:text name="hiveAp.server.idmAuthProxy.enable" />
																		</td>
																	</tr>
																	<%-- <tr>
																		<td><s:radio label="Gender" name="dataSource.IDMAuthProxyStatus"
																			list="%{stringNoChange}"
																			listKey="key" listValue="value"
																			onclick="this.blur();" /></td>
																	</tr>
																	<tr>
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
																	</tr>
																	 --%>
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
						
						<tr style="display: <s:property value="%{displayAdvanceSetting}"/>">
							<td><!-- Advanced -->
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
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
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0"
												width="100%">
												<tr
													style="display: <s:property value="%{fullModeConfigStyle}"/>">
													<td height="5px"></td>
												</tr>
												
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr style="display: <s:property value='mucDisplay'/>">
															<td class="labelT1" width="170px"><s:text
																name="hiveAp.manageUponContact" /></td>
															<td><s:select name="strManageUponContact"
																list="#{'-1':'[-No Change-]', '1':'True', '2':'False'}"
																cssStyle="width:150px;" /></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display: <s:property value='displayDistributedPriority'/>">
													<td>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td class="labelT1" width="350px"><s:text
																name="hiveAp.distributedPriority" /></td>
															<td><s:select list="%{enumDistributedPriority}"
																headerKey="-3" headerValue="%{strNoChange}"
																listKey="key" listValue="value"
																name="dataSource.distributedPriority"
																cssStyle="width:120px;" /></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr
													style="display: <s:property value="%{auditSchedulerStyle}"/>">
													<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="170px"><s:text
																name="hiveAp.scheduler.label" /></td>
															<td width="160px"><s:select list="%{schedulers}"
																listKey="id" listValue="value" name="scheduler"
																headerKey="-3" headerValue="%{strNoChange}"
																cssStyle="width: 150px;" /></td>
															<td style="padding: 0 2px 0 2px;"><s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('newSchedulerMulti')"><img
																	class="dinl" src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
															</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('editSchedulerMulti')"><img
																	class="dinl" src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{displayLocation}"/>">
													<td><table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="475px"><s:text
																name="glasgow_12.hiveAp.insert.topology" /></td>
															<td width="160px"><s:select name="insertTopologyInfo"
																list="#{'-3':'[-No Change-]', '1':'Yes', '2':'No'}"
																cssStyle="width:150px;" /></td>
														</tr>
														<tr>
															<td style="padding: 5px 0 5px 20px;" colspan="10" align="left" class="noteInfo"><s:text name="glasgow_12.hiveAp.insert.topology.note" /></td>
														</tr>
													</table></td>
												</tr>
												
												<tr id="displayTxRetryRateConfigTr" style="display:<s:property value="displayTxRetryRateConfig"/>">
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
												<tr>
													<td><table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
																name="dataSource.multiChangeDelayAlarm" id="multiChangeDelayAlarm" value="%{dataSource.multiChangeDelayAlarm}"
																onclick="showDealyAlarmEnableMulty(this.checked);"></s:checkbox>
															</td>
															<td><span><s:text name="guadalupe_01.enable.capwap.alarm.multi.edit"/></span></td>
														</tr>
														<tr id="dealyAlarmEnableMultyTr" style="display : <s:property value="delayAlarmEnableMultyStyle"/>">
															<td style="padding: 5px 0 5px 30px;" align="left" colspan="2"><s:checkbox
																name="dataSource.enableDelayAlarm" value="%{dataSource.enableDelayAlarm}"></s:checkbox>
																<span><s:text name="guadalupe_01.enable.capwap.alarm"/></span>
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr  style="display: <s:property value="%{supplementalCLIStyle}"/>">
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
																		headerKey="-3" headerValue="%{strNoChange}"
																		cssStyle="width: 150px;" /> <s:if
																		test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New"
																			title="New" />
																	</s:if> <s:else>
																		<a class="marginBtn"
																			href="javascript:submitAction('newSuppCLIMulti')"><img
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
																			href="javascript:submitAction('editSuppCLIMulti')"><img
																			class="dinl"
																			src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify"
																			title="Modify" /></a>
																	</s:else></td>
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
					</table>
					</fieldset>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<script>
function adjustGridCount(gridCountId, gridCount, recCount) {
	var iRecCount = parseInt(recCount, 10);
	if (iRecCount > gridCount) {
		return;
	}
	if (iRecCount <= gridCount) {
		var table = Get(gridCountId);
		for(var i=table.rows.length - 1; i>=0; i--) {
			table.deleteRow(i);
		}
		for (var i = 0; i < gridCount - iRecCount; i++) {
			var newRow = table.insertRow(-1);
	    	var oCell = newRow.insertCell(-1);
	    	oCell.className = "list";
			oCell.innerHTML = "&nbsp;";
		}
	}
}

function doAddIpRouteMulti() {
	if(!validateIpRoute('addIpRouteMulti')) {
		return ;
	}
	var url = '<s:url action="hiveAp" includeParams="none"/>?ignore='+new Date().getTime();
	dealingBeforeSubmitingForm("addIpRouteMulti");
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddIpRouteMulti}, null);
	restoreArgsAfterSubmitingForm();
}
var succAddIpRouteMulti = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('checkAllIp'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('ipRoutesMultiTblData');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "60px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='ipRouteIndices' value='" + details.itemId + "' />";
    	// sourceIp
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
		oCell.innerHTML = details.sourceIp;
    	// netmask
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
    	oCell.innerHTML = details.netmask;
    	// gateway
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
    	oCell.innerHTML = details.gateway;
    	// add grid count
    	adjustGridCount("ipRouteMultiTblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveIpRouteMulti() {
	if(!validate('removeIpRouteMulti')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>?ignore='+new Date().getTime();
	dealingBeforeSubmitingForm("removeIpRouteMulti");
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveIpRouteMulti}, null);
	restoreArgsAfterSubmitingForm();
}
var succRemoveIpRouteMulti = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('ipRoutesMultiTblData');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);

    	oCell.style.align = "left";
    	oCell.style.width = "50px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='ipRouteIndices' value='" + subDetails.itemId + "' />";
    	// sourceIp
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.sourceIp;
    	// netmask
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.netmask;
    	// gateway
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "250px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.gateway;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("ipRouteMultiTblGridCount", <s:property value='gridCount' />, recCount);
}

var changedFormArgsNoReason = {};
function dealingBeforeSubmitingForm(operation) {
	changedFormArgsNoReason = {};
	var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
	if (nativeVlanElement.value ==noChangeKey) {
		nativeVlanElement.value = 0;
		changedFormArgsNoReason.nativeVlan = true;
	}
	var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
	if (mgtVlanElement.value ==noChangeKey) {
		mgtVlanElement.value = 0;
		changedFormArgsNoReason.mgtVlan = true;
	}
	
	document.forms[formName].operation.value = operation;
}

function restoreArgsAfterSubmitingForm() {
	if (changedFormArgsNoReason.nativeVlan) {
		var nativeVlanElement = document.getElementById(formName + "_dataSource_nativeVlan");
		if (nativeVlanElement.value == 0) {
			nativeVlanElement.value = noChangeKey;
		}
	}
	if (changedFormArgsNoReason.mgtVlan) {
		var mgtVlanElement = document.getElementById(formName + "_dataSource_mgtVlan");
		if (mgtVlanElement.value == 0) {
			mgtVlanElement.value = noChangeKey;
		}
	}
	changedFormArgsNoReason = {};
}
</script>

<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
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


.ui-menu .ui-menu-item a {
	width:90px;
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
		if(deviceTagInitValue1=="dataSource.classificationTag1")deviceTagInitValue1="[-No Change-]";
		if(deviceTagInitValue2=="dataSource.classificationTag2")deviceTagInitValue2="[-No Change-]";
		if(deviceTagInitValue3=="dataSource.classificationTag3")deviceTagInitValue3="[-No Change-]";	
		var ct = $("#ap2ClassifierTagContainer").classifierTag(
				{
					key: 8,
					types:  [{key: 4, text: 'Device Tags'}, null, null],
					widgetWidth: {desc: 0},
					valueProps: null,
					itemEditable: false,
					describable: false,	
					needShowTagFields: true,
					needNoChange: true,
					deviceTagInitValue: {
						Tag: [deviceTagInitValue1, deviceTagInitValue2, deviceTagInitValue3]
					}					
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

</script>	