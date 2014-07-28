<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tiles:insertDefinition name="tabView" />
<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'configTemplate';
var configTemplateTabs = null;
var ssidSelectArray = new Array();

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	configTemplateTabs = new YAHOO.widget.TabView("configTemplateTabs", {activeIndex:tabId});
	//if (tabId < 1) {
		if (document.getElementById(formName + "_dataSource_configName").disabled == false) {
			document.getElementById(formName + "_dataSource_configName").focus();
		}
	//}
	for(var j=0;j<document.getElementById('ssidProfileIds').length;j++) {
		ssidSelectArray[j] = document.getElementById('ssidProfileIds').options[j].value;
	}
	//changeValue();
}

function submitEditAction(operation,ssidId){
	document.forms[formName].ssidId.value = ssidId;
	submitAction(operation);
}

function submitAction(operation) {
	if (validate(operation)) {
		if (needShowProcess(operation)) {
			showProcessing();
		}
		//document.forms[formName].tabId.value = configTemplateTabs.get('activeIndex');
		hm.options.selectAllOptions('ssidProfileIds');
		hm.options.selectAllOptions('ipTrackIds');
		hm.options.selectAllOptions('tvNetworkIds');
	    document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == "editHive"){
		var value = hm.util.validateListSelection(formName + "_hiveId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].hiveId.value = value;
		}
	}
	if(operation == "editSsidSelect"){
		var value = hm.util.validateOptionTransferSelection("ssidProfileIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ssidId.value = value;
		}
	}
	if(operation == "editVlan"){
		var value = hm.util.validateListSelection("vlanIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanId.value = value;
		}
	}
	if(operation == "editMgtDns"){
		var value = hm.util.validateListSelection(formName + "_mgtDnsId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtDnsId.value = value;
		}
	}
	if(operation == "editVlanNative"){
		var value = hm.util.validateListSelection("vlanNativeIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanNativeId.value = value;
		}
	}
	if(operation == "editMgtTime"){
		var value = hm.util.validateListSelection(formName + "_mgtTimeId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtTimeId.value = value;
		}
	}
	if(operation == "editLocationServer"){
		var value = hm.util.validateListSelection(formName + "_locationServerId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].locationServerId.value = value;
		}
	}
	if(operation == "editMgtSyslog"){
		var value = hm.util.validateListSelection(formName + "_mgtSyslogId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtSyslogId.value = value;
		}
	}
	if(operation == "editMgtSnmp"){
		var value = hm.util.validateListSelection(formName + "_mgtSnmpId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtSnmpId.value = value;
		}
	}
	if(operation == "editClientWatch"){
		var value = hm.util.validateListSelection(formName + "_clientWatchId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].clientWatchId.value = value;
		}
	}
	if(operation == "editAlgConfig"){
		var value = hm.util.validateListSelection(formName + "_algConfigId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].algConfigId.value = value;
		}
	}
	if(operation == "editMgtOption"){
		var value = hm.util.validateListSelection(formName + "_mgtOptionId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtOptionId.value = value;
		}
	}
	if(operation == "editIdsPolicy"){
		var value = hm.util.validateListSelection(formName + "_idsPolicyId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].idsPolicyId.value = value;
		}
	}
	if(operation == "editAccessConsole"){
		var value = hm.util.validateListSelection(formName + "_accessConsoleId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].accessConsoleId.value = value;
		}
	}
	if(operation == "editLldpCdp"){
		var value = hm.util.validateListSelection(formName + "_lldpCdpId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].lldpCdpId.value = value;
		}
	}
	
	if(operation == "editIpTrack"){
		var value = hm.util.validateOptionTransferSelection("ipTrackIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipTrackId.value = value;
		}
	}
	
	if(operation == "editTvNetwork"){
		var value = hm.util.validateOptionTransferSelection("tvNetworkIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].tvNetworkId.value = value;
		}
	}
	
	if(operation == "editIpFilter"){
		var value = hm.util.validateListSelection(formName + "_ipFilterId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipFilterId.value = value;
		}
	}
	if(operation == "editServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_eth0ServiceId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].eth0ServiceId.value = value;
		}
	}
	if(operation == "editClassifierMap"){
		var value = hm.util.validateListSelection(formName + "_classifierMapId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].classifierMapId.value = value;
		}
	}
	if(operation == "editVpnService"){
		var value = hm.util.validateListSelection(formName + "_vpnServiceId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vpnServiceId.value = value;
		}
	}
	if(operation == "editMarkerMap"){
		var value = hm.util.validateListSelection(formName + "_markerMapId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].markerMapId.value = value;
		}
	}

	if (operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == 'newHive' ||
		operation == 'newVlan' ||
		operation == 'newVlanNative' ||
		operation == 'newMgtDns' ||
		operation == 'newMgtSnmp' ||
		operation == 'newMgtSyslog' ||
		operation == 'newMgtTime' ||
		operation == 'newMgtOption' ||
		operation == 'newClientWatch' ||
		operation == 'newAlgConfig' ||
		operation == 'newLocationServer' ||
		operation == 'newLldpCdp' ||
		operation == 'newIpTrack' ||
		operation == 'newTvNetwork' ||
		operation == 'newIdsPolicy' ||
		operation == 'newIpFilter' ||
		operation == 'newAccessConsole' ||
		operation == 'newSsidSelect' ||
		operation == 'newServiceFilter' ||
		operation == 'newEthernetAccess' ||
		operation == 'newEthernetBridge' ||
		operation == 'newClassifierMap' ||
		operation == 'newMarkerMap' ||
		operation == 'newClassification' ||
		operation == 'newVpnService' ||
		operation == 'editSsid' ||
		operation == 'editSsidSelect' ||
		operation == 'editHive' ||
		operation == 'editVlan' ||
		operation == 'editVlanNative' ||
		operation == 'editMgtDns' ||
		operation == 'editMgtSnmp' ||
		operation == 'editMgtSyslog' ||
		operation == 'editMgtTime' ||
		operation == 'editMgtOption' ||
		operation == 'editClientWatch' ||
		operation == 'editAlgConfig' ||
		operation == 'editLocationServer' ||
		operation == 'editLldpCdp' ||
		operation == 'editIpTrack' ||
		operation == 'editTvNetwork' ||
		operation == 'editIdsPolicy' ||
		operation == 'editIpFilter' ||
		operation == 'editAccessConsole' ||
		operation == 'editServiceFilter' ||
		operation == 'editEthernetAccess' ||
		operation == 'editEthernetBridge' ||
		operation == 'editClassifierMap' ||
		operation == 'editMarkerMap' ||
		operation == 'editVpnService') {
		var slaInterval = Get(formName + "_dataSource_slaInterval");
		if(isNaN(slaInterval.value)){slaInterval.value = 600;}
		if (!validateVLAN(false)){
			return false;
		}
		if (!validateVLANNative(false)) {
			return false;
		}
		return true;
	}

	if(operation == 'openCustomizePage') {
		return true;
	}
	
	if (!validateName()){
		return false;
	}

	if (!validateHive()){
		return false;
	}

	if (!validateVLAN(true)){
		return false;
	}
	
	if (!validateVLANNative(true)) {
		return false;
	}

	if (!validateAlgConfig()){
		return false;
	}
	
	if (!validateProbe()){
		return false;
	}
	
	if (!validateSlaSettings()){
		return false;
	}
	
	if (!validateReportIntervalValue()) {
		return false;
	}
	
	//if (!validateTextField()) {
	//	return false;
	//}
	return true;
}

function needShowProcess(operation) {
	if(operation == 'cancel' + '<s:property value="lstForward"/>') {
		return false;
	}
	
	if(operation == 'openCustomizePage') {
		return false;
	}
	
	return true;
}

function validateName(){
	var inputElement = document.getElementById(formName + "_dataSource_configName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.configTemplate.configName" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    configTemplateTabs.set('activeIndex', 0);
	    inputElement.focus();
	    return false;
	}
	return true;
}

function validateHive(){
	var inputElement = document.getElementById(formName + "_hiveId");
	if (inputElement.value < 0) {
	    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.hive" /></s:param></s:text>');
	    configTemplateTabs.set('activeIndex', 0);
	    inputElement.focus();
	    return false;
	}
	return true;
}

function validateReportIntervalValue(){
	if (Get(formName + "_dataSource_enableReportCollection").checked) {
		var inputElement = document.getElementById(formName + "_dataSource_collectionInterval");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.reportCollectionTitle" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.reportCollectionTitle" />', 1, 60);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionIfCrc");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.ifCrc" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.ifCrc" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionIfTxDrop");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.ifTxDrop" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.ifTxDrop" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionIfRxDrop");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.ifRxDrop" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.ifRxDrop" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionIfTxRetry");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.ifTxRetry" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.ifTxRetry" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionIfAirtime");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.ifAirtime" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.ifAirtime" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionClientTxDrop");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.clientTxDrop" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.clientTxDrop" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionClientRxDrop");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.clientRxDrop" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.clientRxDrop" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionClientTxRetry");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.clientTxRetry" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.clientTxRetry" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
		var inputElement = document.getElementById(formName + "_dataSource_collectionClientAirtime");
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.reportSettings.clientAirtime" /></s:param></s:text>');
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.reportSettings.clientAirtime" />', 1, 100);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			document.getElementById("hideReportSettingsDiv").style.display="block";
			document.getElementById("showReportSettingsDiv").style.display="none";
			inputElement.focus();
			return false;
		}
		
	}
	return true;
}

function validateVLAN(flag){
	var vlannames = document.getElementById("vlanIdSelect");
	var vlanValue = document.forms[formName].inputVlanIdValue;
	if (flag){
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlan" /></s:param></s:text>');
		    configTemplateTabs.set('activeIndex', 0);
		    vlanValue.focus();
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlan" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), message);
	        configTemplateTabs.set('activeIndex', 0);
	        vlanValue.focus();
	        return false;
	    }
	    document.forms[formName].vlanId.value = -1;
	} else {
		document.forms[formName].vlanId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}

function validateVLANNative(flag){
	var vlannames = document.getElementById("vlanNativeIdSelect");
	var vlanValue = document.forms[formName].inputVlanNativeIdValue;
	if (flag) {
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlanNative"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlanNative" /></s:param></s:text>');
		    configTemplateTabs.set('activeIndex', 0);
		    vlanValue.focus();
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlanNative" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlanNative"), message);
	        configTemplateTabs.set('activeIndex', 0);
	        vlanValue.focus();
	        return false;
	    }
	    document.forms[formName].vlanNativeId.value = -1;
	} else {
		document.forms[formName].vlanNativeId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}

function validateAlgConfig(){
	var inputElement = document.getElementById(formName + "_algConfigId");
	if (inputElement.value < 0) {
	    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.algConfig" /></s:param></s:text>');
	    configTemplateTabs.set('activeIndex', 1);
	    document.getElementById("hideServiceSettingsDiv").style.display="block";
        document.getElementById("showServiceSettingsDiv").style.display="none";
	    inputElement.focus();
	    return false;
	}
	return true;
}

function validateProbe(){
	if (Get(formName + "_dataSource_enableProbe").checked) {
		var probeInterval = Get(formName + "_dataSource_probeInterval");
		var message = hm.util.validateIntegerRange(probeInterval.value, '<s:text name="config.configTemplate.probe.periodInterval" />', 60, 600);
		if (message != null) {
			hm.util.reportFieldError(probeInterval, message);
			configTemplateTabs.set('activeIndex', 1);
		    document.getElementById("hideServiceSettingsDiv").style.display="block";
	        document.getElementById("showServiceSettingsDiv").style.display="none";
			probeInterval.focus();
			return false;
		}
		
		var probeRetryCount = Get(formName + "_dataSource_probeRetryCount");
		var message = hm.util.validateIntegerRange(probeRetryCount.value, '<s:text name="config.configTemplate.probe.retryCount" />', 1, 10);
		if (message != null) {
			hm.util.reportFieldError(probeRetryCount, message);
			configTemplateTabs.set('activeIndex', 1);
		    document.getElementById("hideServiceSettingsDiv").style.display="block";
	        document.getElementById("showServiceSettingsDiv").style.display="none";
			probeRetryCount.focus();
			return false;
		}
		
		var probeRetryInterval = Get(formName + "_dataSource_probeRetryInterval");
		var message = hm.util.validateIntegerRange(probeRetryInterval.value, '<s:text name="config.configTemplate.probe.retryInterval" />', 1, 60);
		if (message != null) {
			hm.util.reportFieldError(probeRetryInterval, message);
			configTemplateTabs.set('activeIndex', 1);
		    document.getElementById("hideServiceSettingsDiv").style.display="block";
	        document.getElementById("showServiceSettingsDiv").style.display="none";
			probeRetryInterval.focus();
			return false;
		}
		
		if (Get(formName + "_dataSource_probeUsername").value!=''){
			var keyElement;
			var confirmElement;
			if (document.getElementById("chkToggleDisplay").checked) {
				keyElement = document.getElementById("probePassword");
			  	confirmElement = document.getElementById("confirmProbePassword");
			} else {
			  	keyElement = document.getElementById("probePassword_text");
			  	confirmElement = document.getElementById("confirmProbePassword_text");
			}
			if (keyElement.value.length ==0) {
		         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.probe.password" /></s:param></s:text>');
		         configTemplateTabs.set('activeIndex', 1);
		    	 document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	 document.getElementById("showServiceSettingsDiv").style.display="none";
		         keyElement.focus();
		         return false;
	      	}
	
	      	if (confirmElement.value.length == 0) {
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.probe.confirmPassword" /></s:param></s:text>');
	            configTemplateTabs.set('activeIndex', 1);
		    	document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	document.getElementById("showServiceSettingsDiv").style.display="none";
	            confirmElement.focus();
	            return false;
	      	}
	
	      	var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.hp.password" />');
		  	if (message != null) {
			      hm.util.reportFieldError(keyElement, message);
			      configTemplateTabs.set('activeIndex', 1);
		    	  document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	  document.getElementById("showServiceSettingsDiv").style.display="none";
			      keyElement.focus();
			      return false;
		  	}

	      	if (keyElement.value != confirmElement.value) {
		      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.configTemplate.probe.confirmPassword" /></s:param><s:param><s:text name="config.configTemplate.probe.password" /></s:param></s:text>');
		    	configTemplateTabs.set('activeIndex', 1);
		    	document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	document.getElementById("showServiceSettingsDiv").style.display="none";
		    	keyElement.focus();
		    	return false;
	      	}
		} else {
			document.getElementById("probePassword").value='';
			document.getElementById("confirmProbePassword").value='';
			document.getElementById("probePassword_text").value='';
			document.getElementById("confirmProbePassword_text").value='';
		}
	}
	return true;
}


function validateSlaSettings(){
	var slaInterval = Get(formName + "_dataSource_slaInterval");
	var message = hm.util.validateIntegerRange(slaInterval.value, '<s:text name="config.userprofile.sla.interval" />',
            <s:property value="%{slaIntervalRange.min()}" />,
            <s:property value="%{slaIntervalRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(slaInterval, message);
		document.getElementById("hideQosSettingsDiv").style.display="block";
        document.getElementById("showQosSettingsDiv").style.display="none";
		slaInterval.focus();
		return false;
	}
	return true;
}

function showCreateSection() {
   	if (document.getElementById('ssidProfileIds').length>0) {
  		hm.options.moveAllOptions(document.getElementById('ssidProfileIds'), document.getElementById('leftOptions_ssidProfileIds'), false, '', '', 0);
   	}
    if (ssidSelectArray.length>0) {
    	hm.options.moveSelectedItems(document.getElementById('leftOptions_ssidProfileIds'), document.getElementById('ssidProfileIds'), ssidSelectArray, false, '', '', '0');
    }
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');

}
function hideCreateSection() {
	hm.util.show('newButton');
	hm.util.hide('createButton');
	hm.util.hide('createSection');
}

function addClassification(index,ssidName) {
	var changeElement = document.getElementById("classification_" + index);
	var classValue=changeElement.value;
 	if (classValue !=-2) {
 		return;
 	}
	document.forms[formName].operation.value = 'newClassification';
	if (ssidName == 'eth0') {
		document.forms[formName].interfaceKey.value = -1;
	} else if (ssidName == 'eth1') {
		document.forms[formName].interfaceKey.value = -2;
	} else if (ssidName == 'red0') {
		document.forms[formName].interfaceKey.value = -3;
	} else if (ssidName == 'agg0') {
		document.forms[formName].interfaceKey.value = -4;
	} else {
		document.forms[formName].interfaceKey.value = ssidName;
	}
	//document.forms[formName].tabId.value = configTemplateTabs.get('activeIndex');
	//add handler to deal with something before form submit.
	beforeSubmitAction(document.forms[formName]);
	document.forms[formName].submit();
}

function changeCheckboxValue(idStart,index, ethFlg){
	var changeId = document.getElementById(idStart + "_" + index);
	if (changeId.checked) {
		if (idStart=='arrayCheckE') {
			document.getElementById('arrayCheckD_' + index).checked=false;
		} else if (idStart=='arrayCheckET'){
			document.getElementById('arrayCheckDT_' + index).checked=false;
		} else if (idStart=='arrayCheckP'){
			document.getElementById('arrayCheckD_' + index).checked=false;
		} else if (idStart=='arrayCheckPT'){
			document.getElementById('arrayCheckDT_' + index).checked=false;
		} else if (idStart=='arrayCheckD'){
			if (ethFlg) {
				document.getElementById('arrayCheckP_' + index).checked=false;
			} else {
				document.getElementById('arrayCheckE_' + index).checked=false;
			}
		} else if (idStart=='arrayCheckDT'){
			if (ethFlg) {
				document.getElementById('arrayCheckPT_' + index).checked=false;
			} else {
				document.getElementById('arrayCheckET_' + index).checked=false;
			}
		}
	}
}

function changeSsidOnlyCheckBox(index, count, name){


	for(var i=0;i<count;i++){
		Get('arrayNetwork_' + i).disabled=false;
		Get('arrayMacOui_' + i).disabled=false;
		Get('arraySsid_' + i).disabled=false;
		Get('arrayCheckD_' + i).disabled=false;
		Get('arrayCheckDT_' + i).disabled=false;
		
		if (Get('arrayCheckE_' + i)!=null && Get('arrayCheckE_' + i)!='undefined') {
			Get('arrayCheckE_' + i).disabled=false;
			Get('arrayCheckET_' + i).disabled=false;
		}
		if (Get('arrayCheckP_' + i)!=null && Get('arrayCheckP_' + i)!='undefined') {
			Get('arrayCheckP_' + i).disabled=false;
			Get('arrayCheckPT_' + i).disabled=false;
		}
	}

	if (document.getElementById('arraySsidOnly_' + index).checked){	
		Get('arrayNetwork_' + index).checked=false;
		Get('arrayMacOui_' + index).checked=false;
		Get('arraySsid_' + index).checked=false;
		Get('arrayCheckD_' + index).checked=false;
		Get('arrayCheckDT_' + index).checked=false;
		
		Get('arrayNetwork_' + index).disabled=true;
		Get('arrayMacOui_' + index).disabled=true;
		Get('arraySsid_' + index).disabled=true;
		Get('arrayCheckD_' + index).disabled=true;
		Get('arrayCheckDT_' + index).disabled=true;
		if (name=='ssid') {
			Get('arrayCheckE_' + index).checked=false;
			Get('arrayCheckET_' + index).checked=false;
			Get('arrayCheckE_' + index).disabled=true;
			Get('arrayCheckET_' + index).disabled=true;
		} else {
			Get('arrayCheckP_' + index).checked=false;
			Get('arrayCheckPT_' + index).checked=false;
			Get('arrayCheckP_' + index).disabled=true;
			Get('arrayCheckPT_' + index).disabled=true;
		}
		for(var i=0;i<count;i++){
			if (i!=index){
				Get('arraySsidOnly_' + i).checked=false;
			}
		}
	}
}

/**function changeValue() {
	var ssidSize = <s:property value="listQosRateLimit.size"/>;
	var totleAmodel=0;
	var totleBGmodel=0;
	for (var i=0; i<ssidSize; i++) {
		var amodelRate = document.getElementById("amodelRate_" + i);
		alert(amodelRate.value);
		if (amodelRate != null && amodelRate !="undifined") {
			if(isNaN(amodelRate.value) || amodelRate.value =='') {
				amodelRate.value='0';
				return false;
			}

			var amodelRate11n = document.getElementById("amodelRate11n_" + i);
			if(isNaN(amodelRate11n.value) || amodelRate11n.value =='') {
				amodelRate11n.value='0';
				return false;
			}

			var amodelWeight = document.getElementById("amodelWeight_" + i);
			if(isNaN(amodelWeight.value) || amodelWeight.value =='') {
				amodelWeight.value='0';
 				changeValue();
				return false;
			}
			totleAmodel = parseInt(parseInt(totleAmodel,10) + parseInt(amodelWeight.value,10));
		}
		var bgmodelRate = document.getElementById("bgmodelRate_" + i);
		if (bgmodelRate != null && bgmodelRate !="undifined") {
			if(isNaN(bgmodelRate.value) || bgmodelRate.value =='') {
				bgmodelRate.value='0';
				return false;
			}

			var bgmodelRate11n = document.getElementById("bgmodelRate11n_" + i);
			if(isNaN(bgmodelRate11n.value) || bgmodelRate11n.value =='') {
				bgmodelRate11n.value='0';
				return false;
			}

			var bgmodelWeight = document.getElementById("bgmodelWeight_" + i);
			if(isNaN(bgmodelWeight.value) || bgmodelWeight.value =='') {
				bgmodelWeight.value='0';
 				changeValue();
				return false;
			}
			totleBGmodel = parseInt(parseInt(totleBGmodel,10) + parseInt(bgmodelWeight.value,10));
		}
	}
	for (var i=0; i<ssidSize; i++) {
		var amodelWeight = document.getElementById("amodelWeight_" + i);
		if (amodelWeight != null && amodelWeight !="undifined") {
			var amodelWeightPercent = document.getElementById("amodelWeightPercent_" + i);
			if (totleAmodel != 0) {
				amodelWeightPercent.value= amodelWeight.value/totleAmodel * 100;
			}
		} else {
			var bgmodelWeight = document.getElementById("bgmodelWeight_" + i);
			var bgmodelWeightPercent = document.getElementById("bgmodelWeightPercent_" + i);
			if (totleBGmodel != 0) {
				bgmodelWeightPercent.value= bgmodelWeight.value/totleBGmodel * 100;
			}
		}

	}
}

function validateTextField() {
	var ssidSize = <s:property value="dataSource.qosPolicies.size"/>;
	for (var i=0; i<ssidSize; i++) {
		var amodelRate = document.getElementById("amodelRate_" + i);
		if (amodelRate != null && amodelRate !="undifined") {
			var message = hm.util.validateIntegerRange(amodelRate.value, '<s:text name="config.configTemplate.model.rate.nobr" />' + "802.11a",0,54000);
			if (message != null) {
			    hm.util.reportFieldError(amodelRate, message);
			    configTemplateTabs.set('activeIndex', 2);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    amodelRate.focus();
			    return false;
			}

			var amodelRate11n = document.getElementById("amodelRate11n_" + i);
			var message = hm.util.validateIntegerRange(amodelRate11n.value, '<s:text name="config.configTemplate.model.rate.nobr" />' + "802.11na",0,2000000);
			if (message != null) {
			    hm.util.reportFieldError(amodelRate11n, message);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    amodelRate11n.focus();
			    configTemplateTabs.set('activeIndex', 2);
			    return false;
			}

			var amodelWeight = document.getElementById("amodelWeight_" + i);
			var message = hm.util.validateIntegerRange(amodelWeight.value, '<s:text name="config.configTemplate.model.weight.nobr" />',0,1000);
			if (message != null) {
			    hm.util.reportFieldError(amodelWeight, message);
			    configTemplateTabs.set('activeIndex', 2);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    amodelWeight.focus();
			    return false;
			}
		}

		var bgmodelRate = document.getElementById("bgmodelRate_" + i);
		if (bgmodelRate != null && bgmodelRate !="undifined") {
			var message = hm.util.validateIntegerRange(bgmodelRate.value, '<s:text name="config.configTemplate.model.rate.nobr" />' + "802.11b/g",0,54000);
			if (message != null) {
			    hm.util.reportFieldError(bgmodelRate, message);
			    configTemplateTabs.set('activeIndex', 2);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    bgmodelRate.focus();
			    return false;
			}

			var bgmodelRate11n = document.getElementById("bgmodelRate11n_" + i);
			var message = hm.util.validateIntegerRange(bgmodelRate11n.value, '<s:text name="config.configTemplate.model.rate.nobr" />' + "802.11ng",0,2000000);
			if (message != null) {
			    hm.util.reportFieldError(bgmodelRate11n, message);
			    configTemplateTabs.set('activeIndex', 2);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    bgmodelRate11n.focus();
			    return false;
			}

			var bgmodelWeight = document.getElementById("bgmodelWeight_" + i);
			var message = hm.util.validateIntegerRange(bgmodelWeight.value, '<s:text name="config.configTemplate.model.weight.nobr" />',0,1000);
			if (message != null) {
			    hm.util.reportFieldError(bgmodelWeight, message);
			    configTemplateTabs.set('activeIndex', 2);
			    document.getElementById("hideQosSettingsDiv").style.display="block";
            	document.getElementById("showQosSettingsDiv").style.display="none";
			    bgmodelWeight.focus();
			    return false;
			}
		}
	}

	return true;
}
**/
function showHideNetworkSettingsDiv(value){
	if (value==1) {
		document.getElementById("showNetworkSettingsDiv").style.display="none";
		document.getElementById("hideNetworkSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showNetworkSettingsDiv").style.display="block";
		document.getElementById("hideNetworkSettingsDiv").style.display="none";
	}
}

function showHideServiceSettingsDiv(value){
	if (value==1) {
		document.getElementById("showServiceSettingsDiv").style.display="none";
		document.getElementById("hideServiceSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showServiceSettingsDiv").style.display="block";
		document.getElementById("hideServiceSettingsDiv").style.display="none";
	}
}

function showHideServerSettingsDiv(value){
	if (value==1) {
		document.getElementById("showServerSettingsDiv").style.display="none";
		document.getElementById("hideServerSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showServerSettingsDiv").style.display="block";
		document.getElementById("hideServerSettingsDiv").style.display="none";
	}
}

function showHideQosSettingsDiv(value){
	if (value==1) {
		document.getElementById("showQosSettingsDiv").style.display="none";
		document.getElementById("hideQosSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showQosSettingsDiv").style.display="block";
		document.getElementById("hideQosSettingsDiv").style.display="none";
	}
}
function showHideVpnSettingsDiv(value){
	if (value==1) {
		document.getElementById("showVpnSettingsDiv").style.display="none";
		document.getElementById("hideVpnSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showVpnSettingsDiv").style.display="block";
		document.getElementById("hideVpnSettingsDiv").style.display="none";
	}
}

function showHideReportSettingsDiv(value){
	if (value==1) {
		document.getElementById("showReportSettingsDiv").style.display="none";
		document.getElementById("hideReportSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showReportSettingsDiv").style.display="block";
		document.getElementById("hideReportSettingsDiv").style.display="none";
	}
}

function showHideTVSettingsDiv(value){
	if (value==1) {
		document.getElementById("showTVSettingsDiv").style.display="none";
		document.getElementById("hideTVSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showTVSettingsDiv").style.display="block";
		document.getElementById("hideTVSettingsDiv").style.display="none";
	}
}

function showOrHideMapPanel(value){
	if (value) {
		Get("hideOverrideMapPanelDiv").style.display="block";
	}  else {
		Get("hideOverrideMapPanelDiv").style.display="none";
	}
}

function changeLocationServer(){
	var locationServerValue=document.getElementById(formName + "_locationServerId").value;
	var url = '<s:url action="configTemplate"><s:param name="operation" value="changeLocationServerOperation"/></s:url>' + "&locationServerId="+locationServerValue + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);

}
var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	if (value==1){
		document.getElementById("hideClientWatchDiv").style.display="block";
		//document.getElementById("hideClientWatchDiv").style.display="none";
	} else {
		document.getElementById("hideClientWatchDiv").style.display="none";
	}
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function enabledChkReportCollection(value) {
	if (value) {
		document.getElementById("hideReportSettingsDetailDiv").style.display="";
	}
	else {
		document.getElementById("hideReportSettingsDetailDiv").style.display="none";
		
		//document.getElementById(formName + "_dataSource_collectionInterval").readOnly=true;
		document.getElementById(formName + "_dataSource_collectionInterval").value='10';
		document.getElementById(formName + "_dataSource_collectionIfCrc").value='30';
		document.getElementById(formName + "_dataSource_collectionIfTxDrop").value='40';
		document.getElementById(formName + "_dataSource_collectionIfRxDrop").value='40';
		document.getElementById(formName + "_dataSource_collectionIfTxRetry").value='40';
		document.getElementById(formName + "_dataSource_collectionIfAirtime").value='50';
		document.getElementById(formName + "_dataSource_collectionClientTxDrop").value='40';
		document.getElementById(formName + "_dataSource_collectionClientRxDrop").value='40';
		document.getElementById(formName + "_dataSource_collectionClientTxRetry").value='40';
		document.getElementById(formName + "_dataSource_collectionClientAirtime").value='30';
	}
}

function show_hideProbeDeatil(value) {
	if (value) {
		Get("hideProbeDetail").style.display="";
	} else {
		Get("hideProbeDetail").style.display="none";
		Get(formName+"_dataSource_probeInterval").value='60';
		Get(formName+"_dataSource_probeRetryCount").value='3';
		Get(formName+"_dataSource_probeRetryInterval").value='10';
		Get(formName+"_dataSource_probeUsername").value='';
		Get("probePassword").value='';
		Get("confirmProbePassword").value='';
		Get("probePassword_text").value='';
		Get("confirmProbePassword_text").value='';
	}
}

function enabledTvSettingSelect(value) {
	if (value) {
		document.getElementById("tvOptionDiv").style.display="";
	} else {
		document.getElementById("tvOptionDiv").style.display="none";
	}
}

function enabledTvCheckBox(value) {
	if (value) {
		document.getElementById("tvEnableTr").style.display="";
	} else {
		document.getElementById("tvEnableTr").style.display="none";
		Get(formName + "_dataSource_enableTVService").checked=false;
		document.getElementById("tvOptionDiv").style.display="none";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="configTemplate" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedConfigName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedConfigName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="configTemplate">
	<s:hidden name="interfaceKey" />
	<s:hidden name="ssidId" />
	<s:hidden name="ipTrackId" />
	<s:hidden name="tvNetworkId" />
	<s:hidden name="vlanId" />
	<s:hidden name="vlanNativeId" />
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
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="1000px">
					<tr>
						<td>
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td height="4"></td>
											</tr>
											<tr>
												<td class="labelT1" width="150"><s:text
													name="config.configTemplate.configName" /><font color="red"><s:text name="*" /></font></td>
												<td><s:textfield name="dataSource.configName" size="24"
													maxlength="%{configNameLength}" disabled="%{disabledName}"
													onkeypress="return hm.util.keyPressPermit(event,'name');" />
												&nbsp;<s:text name="config.configTemplate.configName.range" /></td>
											</tr>
											<tr>
												<td class="labelT1" width="150px"><s:text
													name="config.configTemplate.description" /></td>
												<td><s:textfield name="dataSource.description" size="48"
													maxlength="%{descriptionLength}" /> &nbsp;<s:text
													name="config.configTemplate.description.range" /></td>
											</tr>
											<tr>
												<td class="labelT1" width="150px"><s:text
													name="config.configTemplate.hive" /><font color="red"><s:text name="*" /></font></td>
												<td style="padding-right: 5px;"><s:select name="hiveId"
													list="%{list_hive}" listKey="id" listValue="value" cssStyle="width: 160px;" />
													&nbsp;
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newHive')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editHive')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="8"></td>
								</tr>
								<tr>
									<td class="sepLine"><img
										src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
								</tr>
								<tr>
									<td height="8"></td>
								</tr>
								<tr>
									<td style="padding: 4px 4px 4px 4px;">
										<fieldset><legend><s:text name="config.configTemplate.overview" /></legend>
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="8"></td>
												</tr>
												<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
													<td style="padding: 0 0 2px 10px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="Add/Remove SSID Profile"
																class="button toolong" onClick="showCreateSection();"
																<s:property value="updateDisabled" />></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hideCreateButton}"/>" id="createButton">
													<td style="padding: 0 0 2px 10px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" onClick="submitAction('applySsid');"
																<s:property value="updateDisabled" />></td>
															<td><input type="button" name="ignore" value="Cancel"
																class="button" onClick="hideCreateSection();"
																<s:property value="updateDisabled" />></td>
														</tr>
													</table>
													</td>
												</tr>
			
												<tr style="display:<s:property value="%{hideCreateButton}"/>" id="createSection">
													<td style="padding: 5px 0 0 10px" class="listHead">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<s:push value="%{ssidOptions}">
																	<td><tiles:insertDefinition name="optionsTransfer" /></td>
																	<td align="center" class="noteInfo">
																		&nbsp;<s:property value="%{sSIDNotes}" />
																		<s:if test="%{!oEMSystem}">
																			&nbsp;<s:text name="config.configTemplate.apAssignSsid.note.7" />
																		</s:if>
																	</td>
																</s:push>
															</tr>
															<%--<tr>
																<td height="5"></td>
															</tr>
															<tr>
																<td style="padding-left: 20px">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newSsid')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editSsidSelect')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>--%>
														</table>
													</td>
												</tr>
												<tr>
													<td style="padding: 5px 10px 5px 10px">
													<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
														<tr>
															<th align="left" width="150px" nowrap="nowrap"><s:text
																name="config.configTemplate.ssidProfile" /></th>
															<th align="left" width="100px" nowrap="nowrap"><s:text
																name="config.configTemplate.ssid" /></th>
															<th align="left" width="120px" nowrap="nowrap"><s:text
																name="config.configTemplate.cwp" /></th>
															<th align="left" width="150px" nowrap="nowrap"><s:text
																name="config.configTemplate.radios" /></th>
															<th align="left" width="150px" nowrap="nowrap"><s:text
																name="config.configTemplate.radioMode" /></th>
															<th align="left" width="170px" nowrap="nowrap"><s:text
																name="config.configTemplate.userProfile" /></th>
															<th align="left" width="140px"><s:text
																name="config.configTemplate.default" /></th>
														</tr>
														<s:iterator value="%{dataSource.ssidInterfaces.values}" status="status" id="templateSsid">
															<s:if test="%{#templateSsid.ssidProfile != null}">
																<tr>
																	<td valign="top" class="list"
																		rowspan="<s:property value="%{#templateSsid.ssidProfile.allUserProfileSize + 1}"/>">
																		<s:if test="%{lstForward == 'modifySsid'}">
																			<s:property value="%{interfaceName}" />
																		</s:if>
																		<s:else>
																			<a href="javascript:submitEditAction('editSsid','<s:property value="%{#templateSsid.ssidProfile.id}" />');"><s:property value="%{interfaceName}" /></a>
																		</s:else>
																	</td>
																	<td valign="top" class="list"
																		rowspan="<s:property value="%{#templateSsid.ssidProfile.allUserProfileSize + 1}"/>">
																	<s:property value="%{#templateSsid.ssidProfile.ssid}" /></td>
																	<td valign="top" class="list"
																		rowspan="<s:property value="%{#templateSsid.ssidProfile.allUserProfileSize + 1}"/>">
																	<s:if test="%{#templateSsid.ssidProfile.cwp != null}">
																		<s:property value="%{#templateSsid.ssidProfile.cwp.cwpName}" />
																	</s:if>
																	<s:elseif test="%{#templateSsid.ssidProfile.userPolicy != null}">
																		<s:property value="%{#templateSsid.ssidProfile.userPolicy.cwpName}" />
																	</s:elseif>
																	<s:else> - </s:else></td>
																	<td valign="top" class="list"
																		rowspan="<s:property value="%{#templateSsid.ssidProfile.allUserProfileSize + 1}"/>">
																	<s:if test="%{#templateSsid.ssidProfile.radiusAssignment != null}">
																		<s:property value="%{#templateSsid.ssidProfile.radiusAssignment.radiusName}" />
																	</s:if> <s:else> - </s:else></td>
																	<td valign="top" class="list"
																		rowspan="<s:property value="%{#templateSsid.ssidProfile.allUserProfileSize + 1}"/>">
																	<s:property value="%{#templateSsid.ssidProfile.radioModeString}" /></td>
																</tr>
																<s:if test="%{#templateSsid.ssidProfile.userProfileSelfReg != null}">
																	<tr>
																		<td class="list"><s:property
																			value="%{#templateSsid.ssidProfile.userProfileSelfReg.userProfileName}" /></td>
																		<td class="list"><s:text name="config.configTemplate.uptype.2" /></td>
																	</tr>
																</s:if>
																<s:if test="%{#templateSsid.ssidProfile.userProfileDefault != null}">
																	<tr>
																		<td class="list"><s:property
																			value="%{#templateSsid.ssidProfile.userProfileDefault.userProfileName}" /></td>
																		<s:if test="%{#templateSsid.ssidProfile.userProfileTypeDefault == true}">
																			<td class="list"><s:text name="config.configTemplate.uptype.1" /></td>
																		</s:if>
																		<s:else>
																			<td class="list"><s:text name="config.configTemplate.uptype.3" /></td>
																		</s:else>
																	</tr>
																</s:if>
																<s:if test="%{#templateSsid.ssidProfile.accessMode == 2 && #templateSsid.ssidProfile.macAuthEnabled == false && #templateSsid.ssidProfile.enabledUseGuestManager==false}">
																	<s:iterator value="%{#templateSsid.ssidProfile.radiusUserProfile}" status="ssidStatus">
																		<tr>
																			<td class="list"><s:property value="%{userProfileName}" /></td>
																			<td class="list"><s:text name="config.configTemplate.uptype.5" /></td>
																		</tr>
																	</s:iterator>
																</s:if>
																<s:else>
																	<s:iterator value="%{#templateSsid.ssidProfile.radiusUserProfile}" status="ssidStatus">
																		<tr>
																			<td class="list"><s:property value="%{userProfileName}" /></td>
																			<td class="list"><s:text name="config.configTemplate.uptype.4" /></td>
																		</tr>
																	</s:iterator>
																</s:else>
															</s:if>
														</s:iterator>
														<s:if test="%{gridCount > 0}">
															<s:generator separator="," val="%{' '}" count="%{gridCount}">
																<s:iterator>
																	<tr>
																		<td class="list" colspan="7">&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
														</s:if>
													</table>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td height="4"></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding: 4px 4px 4px 4px;" valign="top">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td>
										<fieldset><legend><s:text name="config.configTemplate.vlanSettingTitle" /></legend>
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="labelT1" width="140px" nowrap="nowrap"><s:text
														name="config.configTemplate.vlan" /></td>
													<td width="240px">
														<ah:createOrSelect divId="errorDisplayVlan" list="list_vlan" typeString="Vlan" 
															selectIdName="vlanIdSelect" inputValueName="inputVlanIdValue" swidth="160px" />
													</td>
													<td class="labelT1" width="140px" nowrap="nowrap" style="padding-left: 25px"><s:text
														name="config.configTemplate.vlanNative" /></td>
													<td>
														<ah:createOrSelect divId="errorDisplayVlanNative" list="list_vlan" typeString="VlanNative" 
															selectIdName="vlanNativeIdSelect" inputValueName="inputVlanNativeIdValue" swidth="160px"/>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td height="4"></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding: 4px 4px 4px 4px;" valign="top">	
<%-- optional panel --%>	<fieldset><legend><s:text name="config.configTemplate.optionalSettingsTitle" /></legend>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
<%-- Network Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showNetworkSettingsDiv}"/>" id="showNetworkSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideNetworkSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.networkSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideNetworkSettingsDiv}"/>" id="hideNetworkSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideNetworkSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.networkSettings" />
													</td>
												</tr>
												<tr>
													<td>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td style="padding: 4px 4px 4px 30px;">
																	<fieldset><legend><s:text name="config.configTemplate.serviceFilterSettings" /></legend>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<th width="100px" align="left" nowrap="nowrap"><s:text
																					name="config.configTemplate.ethernetInterface" /></th>
																				<th width="250px" align="left"><s:text
																					name="config.configTemplate.accessSettings" /></th>
																				<th width="250px" align="left"><s:text
																					name="config.configTemplate.backhaulSettings" /></th>
																			</tr>
																			<tr>
																				<td height="4px" />
																			</tr>
																			<tr>
																				<td align="left"><s:text name="config.configTemplate.eth0" /></td>
																				<td align="left" nowrap="nowrap"><s:select name="eth0ServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newServiceFilter')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editServiceFilter')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																					</td>
																				<td align="left"><s:select name="eth0BackServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																			</tr>
																				
																			<tr>
																				<td height="4px" />
																			</tr>
																			<tr>
																				<td align="left"><s:text name="config.configTemplate.eth1" /></td>
																				<td align="left"><s:select name="eth1ServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																				<td align="left"><s:select name="eth1BackServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																			</tr>
																			<tr>
																				<td height="4px" />
																			</tr>
																			<tr>
																				<td align="left"><s:text name="config.configTemplate.red0" /></td>
																				<td align="left"><s:select name="redServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																				<td align="left"><s:select name="red0BackServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																			</tr>
																			<tr>
																				<td height="4px" />
																			</tr>
																			<tr>
																				<td align="left"><s:text name="config.configTemplate.agg0" /></td>
																				<td align="left"><s:select name="aggServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																				<td align="left"><s:select name="agg0BackServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																			</tr>
																			<tr>
																				<td height="4px" />
																			</tr>
																			<tr>
																				<td align="left"><s:text name="config.configTemplate.wirelessBackhaul" /></td>
																				<td/>
																				<td align="left"><s:select name="wireServiceId"
																					list="%{list_service}" listKey="id" listValue="value"
																					cssStyle="width: 180px;" /></td>
																			</tr>
																		</table>
																	</fieldset>
																</td>
															</tr>
															<tr>
																<td height="4"></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
<%-- Service Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showServiceSettingsDiv}"/>" id="showServiceSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideServiceSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.serviceSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideServiceSettingsDiv}"/>" id="hideServiceSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideServiceSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.serviceSettings" />
													</td>
												</tr>
												<tr>
													<td>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td style="padding: 4px 4px 4px 30px;">
																	<fieldset><legend><s:text name="config.configTemplate.serviceSettings" /></legend>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.algConfig" /></td>
																				<td width="240px"><s:select
																					name="algConfigId" list="%{list_algConfig}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newAlgConfig')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editAlgConfig')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																				<td class="labelT1" width="140px" style="padding-left: 25px"><s:text
																					name="config.configTemplate.accessConsole" /></td>
																				<td width="240px"><s:select
																					name="accessConsoleId" list="%{list_accessConsole}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newAccessConsole')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editAccessConsole')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.mgtOption" /></td>
																				<td width="240px"><s:select
																					name="mgtOptionId" list="%{list_mgtOption}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newMgtOption')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editMgtOption')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																				<td class="labelT1" width="140px" style="padding-left: 25px"><s:text
																					name="config.configTemplate.ipFilter" /></td>
																				<td width="240px"><s:select
																					name="ipFilterId" list="%{list_ipFilter}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newIpFilter')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editIpFilter')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.idsPolicy" /></td>
																				<td style="padding-right: 5px;"><s:select
																					name="idsPolicyId" list="%{list_idsPolicy}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newIdsPolicy')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editIdsPolicy')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																				<td class="labelT1" width="140px" style="padding-left: 25px"><s:text
																					name="hiveAp.discoveryProtocolLabel" /></td>
																				<td width="240px"><s:select
																					name="lldpCdpId" list="%{list_lldpCdp}" listKey="id"
																					listValue="value" cssStyle="width: 160px;" />
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
																			</tr>
																			<tr>
																				<td colspan="4" style="padding: 10px 10px 4px 8px;"> 
																					<fieldset><legend><s:text name="hiveAp.ipTrack.label"/></legend>
																						<table cellspacing="0" cellpadding="0" border="0">
																							<tr>
																								<td height="4px"></td>
																							</tr>
																							<tr>
																								<s:push value="%{ipTrackOptions}">
																									<td><tiles:insertDefinition
																										name="optionsTransfer" /></td>
																								</s:push>
																							</tr>
																						</table>
																					</fieldset>
																				</td>
																			</tr>
																			<tr>
																				<td colspan="4">
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																							<td style="padding:0 2px 0 6px"><s:checkbox
																									name="dataSource.enableHttpServer"
																									value="%{dataSource.enableHttpServer}" /></td>
																							<td><s:text name="config.configTemplate.enabledHttpServer" /></td>
																						</tr>
																						<tr>
																							<td style="padding:0 2px 0 6px"><s:checkbox
																									name="dataSource.enableProbe"
																									value="%{dataSource.enableProbe}"
																									onclick="show_hideProbeDeatil(this.checked);" /></td>
																							<td><s:text name="config.configTemplate.probe.enableText" /></td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																			<tr>
																				<td colspan="4">
																					<div style="display:<s:property value="%{hideProbeDetail}"/>" id="hideProbeDetail" >
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text
																								name="config.configTemplate.probe.periodInterval" /></td>
																							<td colspan="2"><s:textfield name="dataSource.probeInterval"
																								size="20" maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																								&nbsp;<s:text name="config.configTemplate.probe.periodInterval.range" /></td>
																						</tr>
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text
																								name="config.configTemplate.probe.retryCount" /></td>
																							<td colspan="2"><s:textfield name="dataSource.probeRetryCount"
																								size="20" maxlength="2"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																								&nbsp;<s:text name="config.configTemplate.probe.retryCount.range" /></td>
																						</tr>
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text
																								name="config.configTemplate.probe.retryInterval" /></td>
																							<td colspan="2"><s:textfield name="dataSource.probeRetryInterval"
																								size="20" maxlength="2"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																								&nbsp;<s:text name="config.configTemplate.probe.retryInterval.range" /></td>
																						</tr>
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text
																								name="config.configTemplate.probe.userName" /></td>
																							<td colspan="2"><s:textfield name="dataSource.probeUsername"
																								size="20" maxlength="32"
																								onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
																								&nbsp;<s:text name="config.configTemplate.probe.userName.range" /></td>
																						</tr>
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text
																								name="config.configTemplate.probe.password" /></td>
																							<td><s:password name="dataSource.probePassword" showPassword="true"
																								id="probePassword" size="20" maxlength="64" 
																								onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																								<s:textfield name="dataSource.probePassword" cssStyle="display:none" disabled="true"
																								id="probePassword_text" size="20" maxlength="64" 
																								onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
																							<td>&nbsp;&nbsp;<s:text name="config.configTemplate.probe.password.range" /></td>
																						</tr>
																						<tr>
																							<td width="160px" class="labelT1" style="padding-left:30px"><s:text name="config.configTemplate.probe.confirmPassword" /></td>
																							<td><s:password name="confirmProbePassword" id="confirmProbePassword"
																								value="%{dataSource.probePassword}" size="20" showPassword="true"
																								maxlength="64" 
																								onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																								<s:textfield name="confirmProbePassword" id="confirmProbePassword_text"
																								value="%{dataSource.probePassword}" size="20"
																								maxlength="64" cssStyle="display:none" disabled="true"
																								onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
																							<td>
																								<table border="0" cellspacing="0" cellpadding="0">
																									<tr>
																										<td>
																											<s:checkbox id="chkToggleDisplay" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																												onclick="hm.util.toggleObscurePassword(this.checked,['probePassword','confirmProbePassword'],['probePassword_text','confirmProbePassword_text']);" />
																										</td>
																										<td>
																											<s:text name="admin.user.obscurePassword" />
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
<%-- Management Server Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showServerSettingsDiv}"/>" id="showServerSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideServerSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.serverSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideServerSettingsDiv}"/>" id="hideServerSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideServerSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.serverSettings" />
													</td>
												</tr>
												<tr>
													<td>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td style="padding: 4px 4px 4px 30px;">
																	<fieldset><legend><s:text name="config.configTemplate.networkServerAssignment" /></legend>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.mgtSnmp" /></td>
																				<td style="padding-right: 5px;"><s:select
																					name="mgtSnmpId" list="%{list_mgtSnmp}" listKey="id"
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
																				<td class="labelT1" width="120px" style="padding-left: 25px"><s:text
																					name="config.configTemplate.mgtDns" /></td>
																				<td width="240px"><s:select
																					name="mgtDnsId" list="%{list_mgtDns}" listKey="id"
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
																			</tr>
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.mgtSyslog" /></td>
																				<td width="240px"><s:select
																					name="mgtSyslogId" list="%{list_mgtSyslog}" listKey="id"
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
																				<td class="labelT1" width="120px" style="padding-left: 25px"><s:text
																					name="config.configTemplate.mgtTime" /></td>
																				<td width="240px"><s:select
																					name="mgtTimeId" list="%{list_mgtTime}" listKey="id"
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
																			</tr>
																			<tr>
																				<td class="labelT1" width="140px"><s:text
																					name="config.configTemplate.locationServer" /></td>
																				<td width="240px"><s:select
																					name="locationServerId" list="%{list_locationServer}"
																					listKey="id" listValue="value" cssStyle="width: 160px;" 
																					onchange="changeLocationServer();"/>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newLocationServer')"><img class="dinl"
																						src="<s:url value="/images/new.png" />"
																						width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn"
																						src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editLocationServer')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />"
																						width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																				<td colspan="2">
																					<div style="display:<s:property value="%{hideClientWatch}"/>" id="hideClientWatchDiv">
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td class="labelT1" width="120px" style="padding-left: 25px"><s:text
																									name="config.configTemplate.clientWatch" /></td>
																								<td width="240px"><s:select
																									name="clientWatchId" list="%{list_clientWatch}" listKey="id"
																									listValue="value" cssStyle="width: 160px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newClientWatch')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editClientWatch')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
																									</s:else>
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
										</div>
									</td>
								</tr>
<%-- QoS Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showQosSettingsDiv}"/>" id="showQosSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideQosSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.qosSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideQosSettingsDiv}"/>" id="hideQosSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideQosSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.qosSettings" />
													</td>
												</tr>
												<tr>
													<td>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td style="padding: 4px 4px 4px 30px;">
																	<fieldset><legend><s:text name="config.configTemplate.block.classification" /></legend>
																		<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																					<table cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<td style="padding: 0 0 0 2px" width="90px"><s:text name="config.configTemplate.classifierMap" /></td>
																							<td><s:select name="classifierMapId"
																								list="%{list_classifierMap}" listKey="id" listValue="value"
																								cssStyle="width: 230px;" /></td>
																							<td>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn"
																									src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('newClassifierMap')"><img class="dinl"
																									src="<s:url value="/images/new.png" />"
																									width="16" height="16" alt="New" title="New" /></a>
																								</s:else>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn"
																									src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify" title="Modify" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('editClassifierMap')"><img class="dinl"
																									src="<s:url value="/images/modify.png" />"
																									width="16" height="16" alt="Modify" title="Modify" /></a>
																								</s:else>
																							</td>
																							<td style="padding: 0 10px 0 30px"><s:text name="config.configTemplate.markerMap" /></td>
																							<td><s:select name="markerMapId"
																								list="%{list_markerMap}" listKey="id" listValue="value"
																								cssStyle="width: 230px;" /></td>
																							<td>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn"
																									src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('newMarkerMap')"><img class="dinl"
																									src="<s:url value="/images/new.png" />"
																									width="16" height="16" alt="New" title="New" /></a>
																								</s:else>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn"
																									src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify" title="Modify" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('editMarkerMap')"><img class="dinl"
																									src="<s:url value="/images/modify.png" />"
																									width="16" height="16" alt="Modify" title="Modify" /></a>
																								</s:else>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																			<tr>
																				<td height="4px"></td>
																			</tr>
																			<tr>
																				<td style="padding: 2px 0 4px 6px">
																					<s:checkbox name="dataSource.enabledMapOverride" value="%{dataSource.enabledMapOverride}" 
																						onclick="showOrHideMapPanel(this.checked);"></s:checkbox>
																						<s:text name="config.configTemplate.enabledMapOverride"></s:text>
																				</td>
																			</tr>
																			<tr>
																				<td height="4px"></td>
																			</tr>
																			<tr>
																				<td style="padding-left: 25px">
																					<div style="display:<s:property value="%{hideOverrideMapPanel}"/>" id="hideOverrideMapPanelDiv">
																					<table cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<s:if test="!oEMSystem">
																								<td style="padding-left: 10px" colspan="10"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
																							</s:if>
																						</tr>
																						<tr>
																							<th align="center" colspan="8"><s:text
																								name="config.configTemplate.classificationOverride" /></th>
																							<th align="center" colspan="2"><s:text
																								name="config.configTemplate.markingOverride" /></th>
																						</tr>
																						<tr>
																							<td class="list"><s:text
																								name="config.configTemplate.interfaceSsid" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.ssidOnly" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.network" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.macOui" /></td>
																							<td class="list" width="50px"><s:text
																								name="config.configTemplate.qos.ssid" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.11e" />/<s:text
																								name="config.configTemplate.qos.11p" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.diff" /></td>
																							<td class="list" width="30px"></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.11e" />/<s:text
																								name="config.configTemplate.qos.11p" /></td>
																							<td class="list"><s:text
																								name="config.configTemplate.qos.diff" /></td>
																						</tr>
																						<s:iterator value="%{dataSource.ssidInterfaces.values}"
																							status="status" id="templateSsid">
																							<s:if test="%{interfaceName=='eth0'}">
																								<tr>
																									<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsidOnly" value="%{#templateSsid.ssidOnlyEnabled}" 
																											id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
																											onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth0');"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayNetwork" value="%{#templateSsid.networkServicesEnabled}" 
																											id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}" 
																											disabled="%{#templateSsid.disabledField}" ></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayMacOui" value="%{#templateSsid.macOuisEnabled}" 
																											id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsid" value="%{#templateSsid.ssidEnabled}" 
																											id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckP" value="%{#templateSsid.CheckP}" 
																											id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																											disabled="%{#templateSsid.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckD" value="%{#templateSsid.checkD}" 
																											id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																											disabled="%{#templateSsid.disabledField}"></s:checkbox>
																									</td>
																									<td class="list" width="30px">&nbsp;</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckPT" value="%{#templateSsid.CheckPT}" 
																											id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																											disabled="%{#templateSsid.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckDT" value="%{#templateSsid.checkDT}" 
																											id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid.disabledField}"
																											onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);" ></s:checkbox>
																									</td>
																									<%--td valign="top" class="list">
																										<s:select name="qosClassifications"
																											value="%{#templateSsid.classfierAndMarker.id}"
																											id="classification_%{#status.index}"
																											list="%{list_qosClassification}" listKey="id"
																											listValue="value" cssStyle="width: 230px;"
																											onchange="addClassification('%{#status.index}','%{#templateSsid.interfaceName}');" />
																									</td--%>
																								</tr>
																							</s:if>
																						</s:iterator>
																						<s:iterator value="%{dataSource.ssidInterfaces.values}"
																							status="status" id="templateSsid3">
																							<s:if test="%{interfaceName=='eth1'}">
																								<tr>
																									<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsidOnly" value="%{#templateSsid3.ssidOnlyEnabled}" 
																											id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
																											onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth1');"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayNetwork" value="%{#templateSsid3.networkServicesEnabled}" 
																											id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayMacOui" value="%{#templateSsid3.macOuisEnabled}" 
																											id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsid" value="%{#templateSsid3.ssidEnabled}" 
																											id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckP" value="%{#templateSsid3.CheckP}" 
																											id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckD" value="%{#templateSsid3.checkD}" 
																											id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td class="list" width="30px">&nbsp;</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckPT" value="%{#templateSsid3.CheckPT}" 
																											id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckDT" value="%{#templateSsid3.checkDT}" 
																											id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																											disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																									</td>
																									<%--td valign="top" class="list">
																										<s:select name="qosClassifications"
																											value="%{#templateSsid3.classfierAndMarker.id}"
																											id="classification_%{#status.index}"
																											list="%{list_qosClassification}" listKey="id"
																											listValue="value" cssStyle="width: 230px;"
																											onchange="addClassification('%{#status.index}','%{#templateSsid3.interfaceName}');" />
																									<FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT>
																									</td--%>
																								</tr>
																							</s:if>
																						</s:iterator>
																						<s:iterator value="%{dataSource.ssidInterfaces.values}"
																							status="status" id="templateSsid4">
																							<s:if test="%{interfaceName=='red0'}">
																								<tr>
																									<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsidOnly" value="%{#templateSsid4.ssidOnlyEnabled}" 
																											id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
																											onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'red0');"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayNetwork" value="%{#templateSsid4.networkServicesEnabled}" 
																											id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayMacOui" value="%{#templateSsid4.macOuisEnabled}" 
																											id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsid" value="%{#templateSsid4.ssidEnabled}" 
																											id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckP" value="%{#templateSsid4.CheckP}" 
																											id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckD" value="%{#templateSsid4.checkD}" 
																											id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td class="list" width="30px">&nbsp;</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckPT" value="%{#templateSsid4.CheckPT}" 
																											id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckDT" value="%{#templateSsid4.checkDT}" 
																											id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																											disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																									</td>
																									<%--td valign="top" class="list">
																										<s:select name="qosClassifications"
																											value="%{#templateSsid4.classfierAndMarker.id}"
																											id="classification_%{#status.index}"
																											list="%{list_qosClassification}" listKey="id"
																											listValue="value" cssStyle="width: 230px;"
																											onchange="addClassification('%{#status.index}','%{#templateSsid4.interfaceName}');" />
																									<FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT>
																									</td--%>
																								</tr>
																							</s:if>
																						</s:iterator>
																						<s:iterator value="%{dataSource.ssidInterfaces.values}"
																							status="status" id="templateSsid5">
																							<s:if test="%{interfaceName=='agg0'}">
																								<tr>
																									<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsidOnly" value="%{#templateSsid5.ssidOnlyEnabled}" 
																											id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
																											onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'agg0');"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayNetwork" value="%{#templateSsid5.networkServicesEnabled}" 
																											id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayMacOui" value="%{#templateSsid5.macOuisEnabled}" 
																											id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsid" value="%{#templateSsid5.ssidEnabled}" 
																											id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckP" value="%{#templateSsid5.CheckP}" 
																											id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckD" value="%{#templateSsid5.checkD}" 
																											id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td class="list" width="30px">&nbsp;</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckPT" value="%{#templateSsid5.CheckPT}" 
																											id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckDT" value="%{#templateSsid5.checkDT}" 
																											id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																											disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																									</td>
																									<%--td valign="top" class="list">
																										<s:select name="qosClassifications"
																											value="%{#templateSsid5.classfierAndMarker.id}"
																											id="classification_%{#status.index}"
																											list="%{list_qosClassification}" listKey="id"
																											listValue="value" cssStyle="width: 230px;"
																											onchange="addClassification('%{#status.index}','%{#templateSsid5.interfaceName}');" />
																									<FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT>
																									</td--%>
																								</tr>
																							</s:if>
																						</s:iterator>
																						<s:iterator value="%{dataSource.ssidInterfaces.values}"
																							status="status" id="templateSsid2">
																							<s:if test="%{interfaceName!='eth0' && interfaceName!='eth1' && interfaceName!='red0' && interfaceName!='agg0'}">
																								<tr>
																									<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsidOnly" value="%{#templateSsid2.ssidOnlyEnabled}" 
																											id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
																											onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'ssid');"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayNetwork" value="%{#templateSsid2.networkServicesEnabled}" 
																											id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayMacOui" value="%{#templateSsid2.macOuisEnabled}" 
																											id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arraySsid" value="%{#templateSsid2.ssidEnabled}" 
																											id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckE" value="%{#templateSsid2.CheckE}" 
																											id="arrayCheckE_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckE',%{#status.index}, false);"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckD" value="%{#templateSsid2.checkD}" 
																											id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, false);"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td class="list" width="30px">&nbsp;</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckET" value="%{#templateSsid2.CheckET}" 
																											id="arrayCheckET_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckET',%{#status.index}, false);"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									<td valign="top" class="list">
																										<s:checkbox name="arrayCheckDT" value="%{#templateSsid2.checkDT}" 
																											id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
																											onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, false);"
																											disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																									</td>
																									
																									<%--td valign="top" class="list">
																										<s:select name="qosClassifications"
																											value="%{#templateSsid2.classfierAndMarker.id}"
																											id="classification_%{#status.index}"
																											list="%{list_qosClassification}" listKey="id"
																											listValue="value" cssStyle="width: 230px;"
																											onchange="addClassification('%{#status.index}','%{#templateSsid2.ssidProfile.id}');" />
																									</td--%>
																								</tr>
																							</s:if>
																						</s:iterator>
																					</table>
																					</div>
																				</td>
																			</tr>
																		</table>
																	</fieldset>
																</td>
															</tr>
															<tr>
																<td height="8px"></td>
															</tr>
															<tr>
																<td style="padding-left: 30px;">
																	<table cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td colspan="4" style="padding-top: 5px;" align="left" class="noteInfo"><s:text
																				name="config.configTemplate.airtime.note" /></td>
																		</tr>
																		<tr>
																			<td width="25px"><s:checkbox name="dataSource.enableAirTime"/></td>
																			<td width="310px"><s:text name="config.configTemplate.enableAirTime" /></td>
																			<td class="labelT1" width="140px">
																				<s:text	name="config.userprofile.sla.interval" />
																			</td>
																			<td>
																				<s:textfield name="dataSource.slaInterval" size="24" maxlength="4"
																					onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	     		<s:text name="config.userprofile.sla.interval.range"/>
																	     	</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td style="padding: 4px 4px 4px 30px;">
																<s:if test="%{listQosRateLimit.size > 0}">
																	<fieldset><legend><s:text name="config.configTemplate.block.policing" /></legend>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td style="padding-top: 5px;" colspan="4" align="left" class="noteInfo"><s:text
																					name="config.configTemplate.policyRateLimit.note" /></td>
																			</tr>
																			<tr>
																				<s:if test="%{radioModeASize}">
																					<td valign="top">
																						<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																							<tr>
																								<td nowrap="nowrap" style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
																									name="config.configTemplate.model.typeA" /></b></td>
																							</tr>
																							<tr>
																								<th align="center"><s:text
																									name="config.configTemplate.model.name" /></th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.rate" /><br>802.11a</th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.rate" /><br>802.11na</th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.weight" /></th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.weightPercent" /></th>
																							</tr>
																							<s:iterator value="%{listQosRateLimit}" status="status" id="templateAModel">
																								<s:if test="%{#templateAModel.radioMode == 2}">
																									<tr>
																										<td valign="top" class="list"><s:property
																											value="%{userProfile.userProfileName}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{policingRate}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{policingRate11n}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{policingRate11ac}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{schedulingWeight}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{weightPercent}" /></td>
																									</tr>
																								</s:if>
																							</s:iterator>
																						</table>
																					</td>
																					<td valign="top" style="padding-left: 30px;" />
																				</s:if>
																				<s:if test="%{radioModeBGSize}">
																					<td valign="top" >
																						<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																							<tr>
																								<td nowrap="nowrap" style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
																									name="config.configTemplate.model.typeBG" /></b></td>
																							</tr>
																							<tr>
																								<th align="center"><s:text
																									name="config.configTemplate.model.name" /></th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.rate" /><br>802.11b/g</th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.rate" /><br>802.11ng</th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.weight" /></th>
																								<th align="center"><s:text
																									name="config.configTemplate.model.weightPercent" /></th>
																							</tr>
																							<s:iterator value="%{listQosRateLimit}"
																								status="status" id="templateBGModel">
																								<s:if test="%{#templateBGModel.radioMode == 1}">
																									<tr>
																										<td valign="top" class="list"><s:property
																											value="%{userProfile.userProfileName}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{policingRate}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{policingRate11n}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{schedulingWeight}" /></td>
																										<td valign="top" class="list"><s:property
																											value="%{weightPercent}" /></td>
																									</tr>
																								</s:if>
																							</s:iterator>
																						</table>
																					</td>
																				</s:if>
																			</tr>
																		</table>
																	</fieldset>
																</s:if>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
<%-- VPN client Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showVpnSettingsDiv}"/>" id="showVpnSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideVpnSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.vpnSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideVpnSettingsDiv}"/>" id="hideVpnSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideVpnSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.vpnSettings" />
													</td>
												</tr>
												<tr>
													<td style="padding-left:25px">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td width="120px"><s:text name="config.configTemplate.vpnService" /></td>
																<td><s:select name="vpnServiceId"
																	list="%{list_vpnService}" listKey="id" listValue="value"
																	cssStyle="width: 230px;" /></td>
																<td>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newVpnService')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editVpnService')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<s:if test="%{list_vpnUserProfile.size > 0}">
												<tr>
													<td height="15px"></td>
												</tr>
												<tr>
													<td style="padding-left:25px">
													<div>
														<table cellspacing="0" cellpadding="0" border="0" class="embedded">
															<tr>
																<th align="left" width="150px" nowrap="nowrap"><s:text
																	name="config.configTemplate.userProfile" /></th>
																<th align="left" width="100px" nowrap="nowrap"><s:text
																	name="config.configTemplate.vpnPanel.tunnel" /></th>
																<th align="left" width="120px" nowrap="nowrap"><s:text
																	name="config.configTemplate.vpnPanel.trafic" /></th>
															</tr>
															<s:iterator value="%{list_vpnUserProfile}" status="status" id="vpnUserProfile">
																	<tr>
																		<td align="left">&nbsp;<s:property value="%{userProfileName}" /></td>
																		<td align="left">&nbsp;<s:property value="%{tunnelUsedString}" /></td>
																		<td align="left">&nbsp;<s:property value="%{splitTunnelString}" /></td>
																	</tr>
															</s:iterator>
														</table>
													</div>
													</td>
												</tr>
												</s:if>
											</table>
										</div>
									</td>
								</tr>
<%-- report Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showReportSettingsDiv}"/>" id="showReportSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideReportSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.reportSettings" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideReportSettingsDiv}"/>" id="hideReportSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideReportSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.reportSettings" />
													</td>
												</tr>
												<tr>
													<td style="padding-left:25px">
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td style="padding: 2px 0 4px 6px" class="labelT1">
																	<s:checkbox name="dataSource.enableReportCollection" onclick="enabledChkReportCollection(this.checked);"/> 
																	<s:text name="config.configTemplate.reportSettings.enabledCollectionTitle"></s:text>
																</td>
															</tr>
															<tr>
																<td style="padding: 2px 0 4px 40px">
																	<div style="display:<s:property value="%{hideReportSettingsDetailDiv}"/>" id="hideReportSettingsDetailDiv">
																		<table border="0" cellspacing="0" cellpadding="0" >
																			<tr>
																				<td width="322px" style="padding-left: 6px" class="labelT1">
																					<s:text name="config.configTemplate.reportSettings.reportCollectionTitle"></s:text></td>
																				<td><s:textfield name="dataSource.collectionInterval" size="15" maxlength="2" 
																					onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																				 &nbsp; <s:text name="config.configTemplate.reportSettings.collectionInterval.range" /></td>
																			</tr>
																		</table>
																		<table border="0" cellspacing="0" cellpadding="0" width="100%">
																			<tr>
																				<td colspan="2" style="padding-left: 6px">
																					<fieldset><legend><s:text name="config.configTemplate.reportSettings.ifBlockTitle" /></legend>
																						<table border="0" cellspacing="0" cellpadding="0" width="100%">
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.ifCrc"></s:text></td>
																								<td><s:textfield name="dataSource.collectionIfCrc" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.ifTxDrop"></s:text></td>
																								<td><s:textfield name="dataSource.collectionIfTxDrop" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.ifRxDrop"></s:text></td>
																								<td><s:textfield name="dataSource.collectionIfRxDrop" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.ifTxRetry"></s:text></td>
																								<td><s:textfield name="dataSource.collectionIfTxRetry" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.ifAirtime"></s:text></td>
																								<td><s:textfield name="dataSource.collectionIfAirtime" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																						</table>
																					</fieldset>
																				</td>
																			</tr>
																			<tr>
																				<td colspan="2" style="padding:4px 2px 4px 6px">
																					<fieldset><legend><s:text name="config.configTemplate.reportSettings.clientBlockTitle" /></legend>
																						<table border="0" cellspacing="0" cellpadding="0" width="100%">
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.clientTxDrop"></s:text></td>
																								<td><s:textfield name="dataSource.collectionClientTxDrop" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.clientRxDrop"></s:text></td>
																								<td><s:textfield name="dataSource.collectionClientRxDrop" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.clientTxRetry"></s:text></td>
																								<td><s:textfield name="dataSource.collectionClientTxRetry" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																							</tr>
																							<tr>
																								<td width="300px" class="labelT1"><s:text name="config.configTemplate.reportSettings.clientAirtime"></s:text></td>
																								<td><s:textfield name="dataSource.collectionClientAirtime" size="15" maxlength="3" 
																									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%
																								 &nbsp; <s:text name="config.configTemplate.reportSettings.ifCrc.range" /></td>
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
										</div>
									</td>
								</tr>
								
<%-- teacherView Settings --%>
								<tr>
									<td>
										<div style="display:<s:property value="%{showTVSettingsDiv}"/>" id="showTVSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" onclick="showHideTVSettingsDiv(1);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
															alt="Show Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.tvsetting.title" />
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div style="display:<s:property value="%{hideTVSettingsDiv}"/>" id="hideTVSettingsDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideTVSettingsDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.configTemplate.tvsetting.title" />
													</td>
												</tr>
												<tr>
													<td style="padding-left:25px">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td style="padding: 2px 0 4px 6px" class="labelT1">
																	<s:checkbox name="dataSource.enableOSDURL" onclick="enabledTvCheckBox(this.checked);"/> 
																	<s:text name="config.configTemplate.tvsetting.enableOSDURL"></s:text>
																</td>

															</tr>
															<tr id="tvEnableTr" style="display:<s:property value="%{hideTVCheckBoxOption}"/>">
																<td style="padding: 2px 0 4px 21px" class="labelT1">
																	<s:checkbox name="dataSource.enableTVService" onclick="enabledTvSettingSelect(this.checked);"/> 
																	<s:text name="config.configTemplate.tvsetting.enableTvService"></s:text>
																</td>
															</tr>
															<tr id="tvOptionDiv" style="display:<s:property value="%{hideTVSelectOption}"/>">
																<td style="padding: 10px 10px 4px 33px;">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td height="4px"></td>
																		</tr>
																		<tr>
																			<s:push value="%{tvNetworkOptions}">
																				<td><tiles:insertDefinition
																					name="optionsTransfer" /></td>
																			</s:push>
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
							</fieldset>	
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>