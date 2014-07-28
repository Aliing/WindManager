<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.network.StpSettings"%>
<%@page import="com.ah.bo.hiveap.ConfigTemplateStormControl"%>

<script>
var dns_checked_flag = false;
var STP_MODE_MSTP = <%=StpSettings.STP_MODE_MSTP%>;
var supportSwitch = <s:property value="needSwitchSettings" />;
var BPS_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE%>;
var PPS_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE%>;
var PERCENTAGE_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE%>;

function prepareMgtAdvancedSaveBtPerrmit(){
	<s:if test="%{savePermit==false}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
}
window.setTimeout("prepareMgtAdvancedSaveBtPerrmit()", 100);

var formName = 'networkPolicyMgtAdvancedSetting';

function validateReportIntervalValue(){
	if (Get(formName + "_dataSource_enableReportCollection").checked) {
		/**var inputElement = document.getElementById(formName + "_dataSource_collectionInterval");
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
		}**/
		
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

function checkRangeValue(inputElementId, description, minValue, maxValue) {
	var inputElement = document.getElementById(inputElementId);
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>' + description + '</s:param></s:text>');
		//document.getElementById("connectionAlarmDetailDiv").style.display="block";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, description, minValue, maxValue);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		inputElement.focus();
		return false;
	}
	return true;
}

function validateConnectionAlarmValue(){
	if (Get(formName + "_dataSource_enableConnectionAlarm").checked) {
		if (!checkRangeValue("dataSource.txRetryThreshold", "Tx Retry Threshold", 1, 100)) {
			return false;
		}
		if (!checkRangeValue("dataSource.txFrameErrorThreshold", "Tx Frame Error Threshold", 1, 100)) {
			return false;
		}
		if (!checkRangeValue("dataSource.probRequestThreshold", "Prob Request Threshold", 0, 20)) {
			return false;
		}
		if (!checkRangeValue("dataSource.egressMulticastThreshold", "Egress Multicast Threshold", 0, 10000)) {
			return false;
		}
		if (!checkRangeValue("dataSource.ingressMulticastThreshold", "Ingress Multicast Threshold", 0, 10000)) {
			return false;
		}
		if (!checkRangeValue("dataSource.channelUtilizationThreshold", "Channel Utilization Threshold", 1, 100)) {
			return false;
		}
	}
	return true;
}

function validateAlgConfig(){
	var inputElement = document.getElementById(formName + "_algConfigId");
	if (inputElement.value < 0) {
	    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.algConfig" /></s:param></s:text>');
	    document.getElementById("hideServiceSettingsDiv").style.display="block";
        document.getElementById("showServiceSettingsDiv").style.display="none";
	    inputElement.focus();
	    return false;
	}
	return true;
}

function validateMgtOptionConfig(){
	var inputElement = document.getElementById(formName + "_mgtOptionId");
	if (inputElement.value < 0) {
	    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.mgtOption" /></s:param></s:text>');
	    document.getElementById("hideServiceSettingsDiv").style.display="";
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
		    document.getElementById("hideServiceSettingsDiv").style.display="block";
	        document.getElementById("showServiceSettingsDiv").style.display="none";
			probeInterval.focus();
			return false;
		}
		
		var probeRetryCount = Get(formName + "_dataSource_probeRetryCount");
		var message = hm.util.validateIntegerRange(probeRetryCount.value, '<s:text name="config.configTemplate.probe.retryCount" />', 1, 10);
		if (message != null) {
			hm.util.reportFieldError(probeRetryCount, message);
		    document.getElementById("hideServiceSettingsDiv").style.display="block";
	        document.getElementById("showServiceSettingsDiv").style.display="none";
			probeRetryCount.focus();
			return false;
		}
		
		var probeRetryInterval = Get(formName + "_dataSource_probeRetryInterval");
		var message = hm.util.validateIntegerRange(probeRetryInterval.value, '<s:text name="config.configTemplate.probe.retryInterval" />', 1, 60);
		if (message != null) {
			hm.util.reportFieldError(probeRetryInterval, message);
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
		    	 document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	 document.getElementById("showServiceSettingsDiv").style.display="none";
		         keyElement.focus();
		         return false;
	      	}
	
	      	if (confirmElement.value.length == 0) {
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.probe.confirmPassword" /></s:param></s:text>');
		    	document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	document.getElementById("showServiceSettingsDiv").style.display="none";
	            confirmElement.focus();
	            return false;
	      	}
	
	      	var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.hp.password" />');
		  	if (message != null) {
			      hm.util.reportFieldError(keyElement, message);
		    	  document.getElementById("hideServiceSettingsDiv").style.display="block";
	        	  document.getElementById("showServiceSettingsDiv").style.display="none";
			      keyElement.focus();
			      return false;
		  	}

	      	if (keyElement.value != confirmElement.value) {
		      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.configTemplate.probe.confirmPassword" /></s:param><s:param><s:text name="config.configTemplate.probe.password" /></s:param></s:text>');
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
/**
function showHideVlanDiv(value){
	if (value==1) {
		document.getElementById("showVlanDiv").style.display="none";
		document.getElementById("hideVlanDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showVlanDiv").style.display="block";
		document.getElementById("hideVlanDiv").style.display="none";
	}
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

function showHideStormControlDiv(value){
	if (value==1) {
		document.getElementById("showStormControlDiv").style.display="none";
		document.getElementById("hideStormControlDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showStormControlDiv").style.display="block";
		document.getElementById("hideStormControlDiv").style.display="none";
	}
}

function showLLDPCDPSettingDiv(value){
	if (value==1) {
		document.getElementById("showLLDPCDPSettinDiv").style.display="none";
		document.getElementById("hideLLDPCDPSettinDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showLLDPCDPSettinDiv").style.display="block";
		document.getElementById("hideLLDPCDPSettinDiv").style.display="none";
	}
}

function showHideRouterRadiusSettingsDiv(value){
	if (value==1) {
		document.getElementById("showRouterRadiusSettingsDiv").style.display="none";
		document.getElementById("hideRouterRadiusSettingsDiv").style.display="";
	}
	if (value==2) {
		document.getElementById("showRouterRadiusSettingsDiv").style.display="";
		document.getElementById("hideRouterRadiusSettingsDiv").style.display="none";
	}
}
function showIGMPSettingDiv(value){
	if (value==1) {
		document.getElementById("showIGMPSettinDiv").style.display="none";
		document.getElementById("hideIGMPSettinDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showIGMPSettinDiv").style.display="block";
		document.getElementById("hideIGMPSettinDiv").style.display="none";
	}
}
function showHideServiceSettingsDiv(value){
	//refreshOptionsList();
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
		document.getElementById(formName + "_dataSource_collectionInterval").value=10;
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

function enabledChkConnectionAlarm(value) {
	if (value) {
		document.getElementById("connectionAlarmDetailDiv").style.display="";
	}
	else {
		document.getElementById("connectionAlarmDetailDiv").style.display="none";
		//document.getElementById(formName + "_dataSource_collectionInterval").value='10';
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
var selectUIElement;
function submitAction(operation) {
//	console.log('hahahha');
	if (validate(operation)) {
		if (operation == 'newRouterIpTrack'
			||operation == 'newRouterIpTrack_Primary'
			||operation == 'newRouterIpTrack_Backup1'
			||operation == 'newRouterIpTrack_Backup2'
			||operation == 'editPrimaryTrackWan'
			||operation == 'editBackup1TrackWan'
			||operation == 'editBackup2TrackWan'
			||operation == 'newIpTrack'
			||operation == 'editIpTrack'
			||operation == 'editIpTrackWAN'
			||operation == 'newIpTrackWAN') {
			var o = operation;
			if(operation =='editIpTrackWAN') o='editIpTrack';
			else if(operation =='newIpTrackWAN') o='newIpTrack';
			if(operation == 'editPrimaryTrackWan'
				||operation == 'editBackup1TrackWan'
					||operation == 'editBackup2TrackWan'){
				o = 'editRouterIpTrack';
			}
			if(operation == 'newRouterIpTrack_Primary'
				||operation == 'newRouterIpTrack_Backup1'
					||operation == 'newRouterIpTrack_Backup2'){
				o = 'newRouterIpTrack';
			}
			var source = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ o
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editPrimaryTrackWan'){
				source = source + "&routerIpTrackId=" + document.forms[formName].routerIpTrackId.value + "&parentDomID=" + "ipTrackWANList_ID";
			} else if(operation == 'editBackup1TrackWan'){
				source = source + "&routerIpTrackId=" + document.forms[formName].routerIpTrackId.value + "&parentDomID=" + "ipTrackWANList_ID";
			}else if(operation == 'editBackup2TrackWan'){
				source = source + "&routerIpTrackId=" + document.forms[formName].routerIpTrackId.value + "&parentDomID=" + "ipTrackWANList_ID";
			}else if (operation == 'editIpTrack'){
				source = source + "&ipTrackId=" + document.forms[formName].ipTrackId.value+ "&parentDomID=leftOptions_ipTrackIds";
			} else if (operation == 'newRouterIpTrack'){
				source = source + "&parentDomID=" + "ipTrackWANList_ID";
			} else if (operation == 'newRouterIpTrack_Primary'){
				source = source + "&parentDomID=" + "primaryIpTrackId";
			} else if (operation == 'newRouterIpTrack_Backup1'){
				source = source + "&parentDomID=" + "backup1TrackId";
			} else if (operation == 'newRouterIpTrack_Backup2'){
				source = source + "&parentDomID=" + "backup2TrackId";
			} else if (operation == 'newIpTrack'){
				source = source + "&parentDomID=leftOptions_ipTrackIds";
			}else if(operation == 'editIpTrackWAN'){
				source = source + "&ipTrackId=" + document.forms[formName].ipTrackId.value+ "&parentDomID=ipTrackWANList_ID";
			}else if(operation == 'newIpTrackWAN'){
				source = source  + "&parentDomID=ipTrackWANList_ID";
			}
			openIFrameDialog(760, 550, source);
		} else if (operation == "newMgtDns" || operation == "editMgtDns") {
			var selectElId;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
			if(operation == "newMgtDns"){
				selectElId = formName + "_mgtDnsId";
				url = url + "&parentDomID=" + selectElId;
			} else if(operation == "editMgtDns"){
				url = url + "&mgtDnsId=" + document.forms[formName].mgtDnsId.value;
			}
			openIFrameDialog(750, 450, url);
		} else if (operation == 'newDeviceServiceFilter' || operation == 'editDeviceServiceFilter') {
			// set the UI for sub dialog
			selectUIElement = [Get(formName + "_deviceServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth1BackServiceId"),
				                   Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
			if(operation == 'newDeviceServiceFilter') {
				var url = "<s:url action='configTemplate' includeParams='none' />?operation="+operation+"&jsonMode=true"
			 	+ "&ignore="+new Date().getTime();

			} else if(operation == 'editDeviceServiceFilter') {
				var value =document.forms[formName].deviceServiceId.value
				var url = "<s:url action='configTemplate' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&deviceServiceId="+value
			 	+ "&ignore="+new Date().getTime();
				
			}
			openIFrameDialog(800, 400, url);
		} else if (operation == 'newEth0ServiceFilter' || operation == 'newEth1ServiceFilter' 
				|| operation == 'newRed0ServiceFilter' || operation == 'newAgg0ServiceFilter'
				|| operation == 'newBackEth0ServiceFilter' || operation == 'newBackEth1ServiceFilter'
				|| operation == 'newBackRed0ServiceFilter' || operation == 'newBackAgg0ServiceFilter'
				|| operation == 'newBackWireServiceFilter'){
			// set the UI for sub dialog
			switch (operation){
			case 'newEth0ServiceFilter' :
				selectUIElement = [Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),Get(formName + "_deviceServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth1BackServiceId"),
				                   Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newEth1ServiceFilter' :
				selectUIElement = [Get(formName + "_eth1ServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_redServiceId"),Get(formName + "_deviceServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth1BackServiceId"),
				                   Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newRed0ServiceFilter' :
				selectUIElement = [Get(formName + "_redServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_deviceServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth1BackServiceId"),
				                   Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newAgg0ServiceFilter' :
				selectUIElement = [Get(formName + "_aggServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),
				                   Get(formName + "_deviceServiceId"), Get(formName + "_eth0BackServiceId"),Get(formName + "_eth1BackServiceId"),
					               Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newBackEth0ServiceFilter' :
				selectUIElement = [Get(formName + "_eth0BackServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_deviceServiceId"),Get(formName + "_eth1BackServiceId"),
					               Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newBackEth1ServiceFilter' :
				selectUIElement = [Get(formName + "_eth1BackServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),
				                   Get(formName + "_redServiceId"),Get(formName + "_aggServiceId"),Get(formName + "_deviceServiceId"),
					               Get(formName + "_red0BackServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newBackRed0ServiceFilter' :
				selectUIElement = [Get(formName + "_red0BackServiceId"),Get(formName + "_eth1BackServiceId"),Get(formName + "_eth0BackServiceId"),Get(formName + "_eth0ServiceId"),
				                   Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),Get(formName + "_aggServiceId"),Get(formName + "_deviceServiceId"),
					               Get(formName + "_agg0BackServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newBackAgg0ServiceFilter' :
				selectUIElement = [Get(formName + "_agg0BackServiceId"),Get(formName + "_red0BackServiceId"),Get(formName + "_eth1BackServiceId"),Get(formName + "_eth0BackServiceId"),
				                   Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),Get(formName + "_aggServiceId"),
				                   Get(formName + "_deviceServiceId"),Get(formName + "_wireServiceId")];
				break;
			case 'newBackWireServiceFilter' :
				selectUIElement = [Get(formName + "_wireServiceId"),Get(formName + "_agg0BackServiceId"),Get(formName + "_red0BackServiceId"),Get(formName + "_eth1BackServiceId"),
				                   Get(formName + "_eth0BackServiceId"),Get(formName + "_eth0ServiceId"),Get(formName + "_eth1ServiceId"),Get(formName + "_redServiceId"),
				                   Get(formName + "_aggServiceId"),Get(formName + "_deviceServiceId")];
				break;
			}
			
			var url = "<s:url action='configTemplate' includeParams='none' />?operation="+operation+"&jsonMode=true"
		 		+ "&ignore="+new Date().getTime();
			openIFrameDialog(800, 400, url);
		} else if (operation == 'editEth0ServiceFilter' || operation == 'editEth1ServiceFilter' 
				|| operation == 'editRed0ServiceFilter' || operation == 'editAgg0ServiceFilter'
				|| operation == 'editBackEth0ServiceFilter' || operation == 'editBackEth1ServiceFilter'
				|| operation == 'editBackRed0ServiceFilter' || operation == 'editBackAgg0ServiceFilter'
				|| operation == 'editBackWireServiceFilter'){
			var url = "<s:url action='configTemplate' includeParams='none' />?operation="+operation+"&jsonMode=true";
		 	
			switch (operation){
			case 'editEth0ServiceFilter' :
				url = url + "&eth0ServiceId=" + document.forms[formName].eth0ServiceId.value;
				break;
			case 'editEth1ServiceFilter' :
				url = url + "&eth1ServiceId=" + document.forms[formName].eth1ServiceId.value;
				break;
			case 'editRed0ServiceFilter' :
				url = url + "&redServiceId=" + document.forms[formName].redServiceId.value;
				break;
			case 'editAgg0ServiceFilter' :
				url = url + "&aggServiceId=" + document.forms[formName].aggServiceId.value;
				break;
			case 'editBackEth0ServiceFilter' :
				url = url + "&eth0BackServiceId=" + document.forms[formName].eth0BackServiceId.value;
				break;
			case 'editBackEth1ServiceFilter' :
				url = url + "&eth1BackServiceId=" + document.forms[formName].eth1BackServiceId.value;
				break;
			case 'editBackRed0ServiceFilter' :
				url = url + "&red0BackServiceId=" + document.forms[formName].red0BackServiceId.value;
				break;
			case 'editBackAgg0ServiceFilter' :
				url = url + "&agg0BackServiceId=" + document.forms[formName].agg0BackServiceId.value;
				break;
			case 'editBackWireServiceFilter' :
				url = url + "&wireServiceId=" + document.forms[formName].wireServiceId.value;
				break;
			}
			
			var url = url+ "&ignore="+new Date().getTime();
			openIFrameDialog(800, 400, url);
		} else if (operation =="editMarkerMap" || operation =="newMarkerMap") {
			var source = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editMarkerMap'){
				source = source + "&markerMapId=" + document.forms[formName].markerMapId.value + "&parentDomID=" + formName+ "_markerMapId";
			} else if (operation == 'newMarkerMap'){
				source = source + "&parentDomID=" + formName+ "_markerMapId";
			}
			openIFrameDialog(870, 520, source);
		} else if (operation =="editClassifierMap" || operation =="newClassifierMap") {
			var source = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editClassifierMap'){
				source = source + "&classifierMapId=" + document.forms[formName].classifierMapId.value + "&parentDomID=" + formName+ "_classifierMapId";
			} else if (operation == 'newClassifierMap'){
				source = source + "&parentDomID=" + formName+ "_classifierMapId";
			}
			openIFrameDialog(900, 650, source);
/**
		} else if (operation =="newVlanNative" || operation =="editVlanNative") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editVlanNative'){
				url = url + "&vlanNativeId=" + document.forms[formName].vlanNativeIdSelect.value + "&parentDomID=" + "vlanNativeIdSelect";
			} else if (operation == 'newVlanNative'){
				url = url + "&parentDomID=" + "vlanNativeIdSelect";
			}
			openIFrameDialog(820,450, url);
		}else if (operation =="newVlan" || operation =="editVlan") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editVlan'){
				url = url + "&vlanId=" + document.forms[formName].vlanIdSelect.value + "&parentDomID=" + "vlanIdSelect";
			} else if (operation == 'newVlan'){
				url = url + "&parentDomID=" + "vlanIdSelect";
			}
			openIFrameDialog(800,450, url);
**/
		}else if (operation =="newMgtSnmp" || operation =="editMgtSnmp") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editMgtSnmp'){
				url = url + "&mgtSnmpId=" + document.forms[formName].mgtSnmpId.value + "&parentDomID=" + formName+ "_mgtSnmpId";
			} else if (operation == 'newMgtSnmp'){
				url = url + "&parentDomID=" +  formName+ "_mgtSnmpId";;
			}
			openIFrameDialog(850, 550, url);
		}else if (operation =="newMgtSyslog" || operation =="editMgtSyslog") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editMgtSyslog'){
				url = url + "&mgtSyslogId=" + document.forms[formName].mgtSyslogId.value + "&parentDomID=" + formName+ "_mgtSyslogId";
			} else if (operation == 'newMgtSyslog'){
				url = url + "&parentDomID=" +  formName+ "_mgtSyslogId";;
			}
			openIFrameDialog(750, 550, url);
		}else if (operation =="newMgtTime" || operation =="editMgtTime") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editMgtTime'){
				url = url + "&mgtTimeId=" + document.forms[formName].mgtTimeId.value + "&parentDomID=" + formName+ "_mgtTimeId";
			} else if (operation == 'newMgtTime'){
				url = url + "&parentDomID=" +  formName+ "_mgtTimeId";;
			}
			openIFrameDialog(650, 550, url);

		} else if (operation == "newMgtOption" || operation == "editMgtOption"){
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editMgtOption'){
				url = url + "&mgtOptionId=" + document.forms[formName].mgtOptionId.value + "&parentDomID=" + formName+ "_mgtOptionId";
			} else if (operation == 'newMgtOption'){
				url = url + "&parentDomID=" + formName+ "_mgtOptionId";
			}
			openIFrameDialog(900, 600, url);
		}else if (operation == "newIpFilter" || operation == "editIpFilter") {			
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editIpFilter'){
				url = url + "&ipFilterId=" + document.forms[formName].ipFilterId.value + "&parentDomID=" + formName+ "_ipFilterId";
			} else if (operation == 'newIpFilter'){
				url = url + "&parentDomID=" + formName+ "_ipFilterId";
			}
			openIFrameDialog(700, 450, url);
		}else if (operation == "newIdsPolicy" || operation == "editIdsPolicy"){//nxma
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editIdsPolicy'){
				url = url + "&idsPolicyId=" + document.forms[formName].idsPolicyId.value + "&parentDomID=" + formName+ "_idsPolicyId";
			} else if (operation == 'newIdsPolicy'){
				url = url + "&parentDomID=" + formName+ "_idsPolicyId";
			}
			openIFrameDialog(820, 600, url);		
		}else if (operation == "newLldpCdp" || operation == "editLldpCdp"){
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editLldpCdp'){
				url = url + "&lldpCdpId=" + document.forms[formName].lldpCdpId.value + "&parentDomID=" + formName+ "_lldpCdpId";
			} else if (operation == 'newLldpCdp'){
				url = url + "&parentDomID=" + formName+ "_lldpCdpId";
			}
			openIFrameDialog(760, 750, url);
			
		}else if (operation == "newRadiusProxy" || operation == "editRadiusProxy"){
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editRadiusProxy'){
				url = url + "&radiusProxyId=" + document.forms[formName].radiusProxyId.value + "&parentDomID=" + formName+ "_radiusProxyId";
			} else if (operation == 'newRadiusProxy'){
				url = url + "&parentDomID=" + formName+ "_radiusProxyId";
			}
			openIFrameDialog(900, 750, url);
		}else if (operation == "newRadiusServer" || operation == "editRadiusServer"){
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editRadiusServer'){
				url = url + "&radiusServerId=" + document.forms[formName].radiusServerId.value + "&parentDomID=" + formName+ "_radiusServerId";
			} else if (operation == 'newRadiusServer'){
				url = url + "&parentDomID=" + formName+ "_radiusServerId";
			}
			openIFrameDialog(900, 750, url);
		}else if (operation == "newRoutingPbrPolicy" || operation == "editRoutingPbrPolicy"){
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editRoutingPbrPolicy'){
				url = url + "&routingPolicyId=" + document.forms[formName].routingPolicyId.value + "&parentDomID=" + formName+ "_routingPolicyId";
			} else if (operation == 'newRoutingPbrPolicy'){
				url = url + "&parentDomID=" + formName+ "_routingPolicyId";
			}
		
			openIFrameDialog(1080, 600, url);
		/**	
		} else if (operation == 'newMgtNetwork' || operation == 'editMgtNetwork') {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&parentDomID=' + formName+ "_mgtNetworkId"
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editMgtNetwork'){
				url = url + "&mgtNetworkId=" + document.forms[formName].mgtNetworkId.value;
			}
			openIFrameDialog(850,630, url);**/
		} else if (operation == "newAlgConfig" || operation == "editAlgConfig") {			
			var url = '<s:url action="configTemplate" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if (operation == 'editAlgConfig'){
				url = url + "&algConfigId=" + document.forms[formName].algConfigId.value + "&parentDomID=" + formName+ "_algConfigId";
			} else if (operation == 'newAlgConfig'){
				url = url + "&parentDomID=" + formName+ "_algConfigId";
			}
			openIFrameDialog(720, 450, url);
		} else if (operation == "newAccessConsole" || operation == "editAccessConsole") {			
			var url = '<s:url action="configTemplate" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime()
				+ "&parentDomID=" + formName+ "_accessConsoleId";
			if (operation == 'editAccessConsole'){
				url = url + "&accessConsoleId=" + document.forms[formName].accessConsoleId.value;
			}
			openIFrameDialog(730, 450, url);
		} else if (operation == "newLocationServer" || operation == "editLocationServer") {
			var selectElId;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
			selectElId = formName + "_locationServerId";
			url = url + "&parentDomID=" + selectElId;
			if(operation == "editLocationServer"){
				url = url + "&locationServerId=" + document.forms[formName].locationServerId.value;
			}
			openIFrameDialog(950, 450, url);
		} else if (operation == "newClientWatch" || operation == "editClientWatch") {
			var selectElId;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
			selectElId = formName + "_clientWatchId";
			url = url + "&parentDomID=" + selectElId;
			if(operation == "editClientWatch"){
				url = url + "&clientWatchId=" + document.forms[formName].clientWatchId.value;
			}
			openIFrameDialog(950, 450, url);
		} else if(operation == 'newTvNetwork' ||operation == 'editTvNetwork'){
			var source = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			if(operation == 'newTvNetwork'){
				source = source + "&parentDomID=leftOptions_tvNetworkIds"
			} else if(operation == 'editTvNetwork'){
				source = source + "&tvNetworkId=" + document.forms[formName].tvNetworkId.value+ "&parentDomID=leftOptions_tvNetworkIds";
			}
			openIFrameDialog(640, 350, source);
			
		} else if (operation == "newRadiusOptName" || operation == "editRadiusOptName") {
			var selectElId;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
			if(operation == "newRadiusOptName"){
				selectElId = formName + "_radiusOptNameId";
				url = url + "&parentDomID=" + selectElId;
			} else if(operation == "editRadiusOptName"){
				url = url + "&radiusOptNameId=" + document.forms[formName].radiusOptNameId.value;
			}
			openIFrameDialog(900, 450, url);
			
		} else if(operation == "newSuppCLIBlob" || operation == "editSuppCLIBlob"){
			var selectElId;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&ignore="+new Date().getTime();
			if(operation == "newSuppCLIBlob"){
				selectElId = formName + "_supplementalCLIId";
				url = url + "&parentDomID=" + selectElId;
			} else if(operation == "editSuppCLIBlob"){
				url = url + "&supplementalCLIId=" + document.forms[formName].supplementalCLIId.value;
			}
			openIFrameDialog(830, 700, url);
		}else if (operation == "newAppProfile" || operation == "editAppProfile") {
			var url = "<s:url action='appProfile' includeParams='none' />?operation=init&jsonMode=true&parentDomID=appProfileId&ignore="+new Date().getTime();
            if(operation == "editAppProfile"){
				url = url + "&profile.id=" + document.getElementById("appProfileId").value;
			}
			openIFrameDialog(900, 450, url);
			
		} else if (operation =="newMstpRegion" || operation =="editMstpRegion") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editMstpRegion'){
				url = url + "&mstpRegionId=" + document.forms[formName].mstpRegionId.value + "&parentDomID=" + formName+ "_mstpRegionId";
			} else if (operation == 'newMstpRegion'){
				url = url + "&parentDomID=" +  formName+ "_mstpRegionId";;
			}
			openIFrameDialog(800, 450, url);
		}
	}
}

function validate(operation) {
	if(operation == "editMgtDns"){
		var value = hm.util.validateListSelection(formName + "_mgtDnsId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mgtDnsId.value = value;
		}
	}
	/**
	if(operation == "editVlanNative"){
		var value = hm.util.validateListSelection("vlanNativeIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanNativeId.value = value;
		}
	}
	**/
	if(operation == "editMgtTime"){
		var value = hm.util.validateListSelection(formName + "_mgtTimeId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mgtTimeId.value = value;
		}
	}
	if(operation == "editLocationServer"){
		var value = hm.util.validateListSelection(formName + "_locationServerId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].locationServerId.value = value;
		}
	}
	if(operation == "editMgtSyslog"){
		var value = hm.util.validateListSelection(formName + "_mgtSyslogId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mgtSyslogId.value = value;
		}
	}
	if(operation == "editMgtSnmp"){
		var value = hm.util.validateListSelection(formName + "_mgtSnmpId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mgtSnmpId.value = value;
		}
	}
	if(operation == "editClientWatch"){
		var value = hm.util.validateListSelection(formName + "_clientWatchId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].clientWatchId.value = value;
		}
	}
	if(operation == "editAlgConfig"){
		var value = hm.util.validateListSelection(formName + "_algConfigId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].algConfigId.value = value;
		}
	}
	if(operation == "editMgtOption"){
		var value = hm.util.validateListSelection(formName + "_mgtOptionId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mgtOptionId.value = value;
		}
	}
	if(operation == "editIdsPolicy"){
		var value = hm.util.validateListSelection(formName + "_idsPolicyId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].idsPolicyId.value = value;
		}
	}
	if(operation == "editAccessConsole"){
		var value = hm.util.validateListSelection(formName + "_accessConsoleId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].accessConsoleId.value = value;
		}
	}
	if(operation == "editLldpCdp"){
		var value = hm.util.validateListSelection(formName + "_lldpCdpId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].lldpCdpId.value = value;
		}
	}
	
	if(operation == "editRadiusProxy"){
		var value = hm.util.validateListSelection(formName + "_radiusProxyId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].radiusProxyId.value = value;
		}
	}
	
	if(operation == "editRadiusServer"){
		var value = hm.util.validateListSelection(formName + "_radiusServerId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].radiusServerId.value = value;
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
	
	if(operation == "editIpTrack"){
		var value = hm.util.validateOptionTransferSelection("ipTrackIds");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ipTrackId.value = value;
		}
	}
	
	if(operation == "editTvNetwork"){
		var value = hm.util.validateOptionTransferSelection("tvNetworkIds");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].tvNetworkId.value = value;
		}
	}
	
	if(operation == "editIpFilter"){
		var value = hm.util.validateListSelection(formName + "_ipFilterId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].ipFilterId.value = value;
		}
	}
	
	if(operation == "editEth0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_eth0ServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].eth0ServiceId.value = value;
		}
	}
	if(operation == "editEth1ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_eth1ServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].eth1ServiceId.value = value;
		}
	}
	if(operation == "editRed0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_redServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].redServiceId.value = value;
		}
	}
	if(operation == "editAgg0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_aggServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].aggServiceId.value = value;
		}
	}
	if(operation == "editBackEth0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_eth0BackServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].eth0BackServiceId.value = value;
		}
	}
	if(operation == "editBackEth1ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_eth1BackServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].eth1BackServiceId.value = value;
		}
	}
	if(operation == "editBackRed0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_red0BackServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].red0BackServiceId.value = value;
		}
	}
	if(operation == "editBackAgg0ServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_agg0BackServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].agg0BackServiceId.value = value;
		}
	}
	if(operation == "editBackWireServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_wireServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].wireServiceId.value = value;
		}
	}

	if(operation == "editDeviceServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_deviceServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].deviceServiceId.value = value;
		}
	}
	if(operation == "editClassifierMap"){
		var value = hm.util.validateListSelection(formName + "_classifierMapId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].classifierMapId.value = value;
		}
	}
	if(operation == "editMarkerMap"){
		var value = hm.util.validateListSelection(formName + "_markerMapId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].markerMapId.value = value;
		}
	}
	if(operation == "editPrimaryTrackWan"){
		var value = hm.util.validateListSelection("primaryIpTrackId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].routerIpTrackId.value = value;
		}
	}
	if(operation == "editBackup1TrackWan"){
		var value = hm.util.validateListSelection("backup1TrackId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].routerIpTrackId.value = value;
		}
	}
	if(operation == "editBackup2TrackWan"){
		var value = hm.util.validateListSelection("backup2TrackId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].routerIpTrackId.value = value;
		}
	}
	if(operation == "editIpTrackWAN"){
		var value = -1;
		value = $("#ipTrackWANList_ID").val();
		if(value == null){
			warnDialog.cfg.setProperty('text', "Please select one item.");
			warnDialog.show();
			return false;
		}else{
			document.forms[formName].ipTrackId.value = value;
			return true;
		}
	}
	
	if(operation == "editMstpRegion"){
		var value = hm.util.validateListSelection(formName + "_mstpRegionId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].mstpRegionId.value = value;
		}
	}
	if(operation == "editRadiusOptName"){
		var value = hm.util.validateListSelection(formName + "_radiusOptNameId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].radiusOptNameId.value = value;
		}
	}
	
	if(operation == "editSuppCLIBlob"){
		var value = hm.util.validateListSelection(formName + "_supplementalCLIId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].supplementalCLIId.value = value;
		}
	}
	/**
	if(operation == "editMgtNetwork"){
		var value = hm.util.validateListSelection(formName + "_mgtNetworkId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].mgtNetworkId.value = value;
		}
	}
	if(operation == "editRadius"){
		var value = hm.util.validateListSelection(formName + "_radiusServerId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].radiusServerId.value = value;
		}
	}
	**/
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		var slaInterval = Get(formName + "_dataSource_slaInterval");
		if(isNaN(slaInterval.value)){slaInterval.value = 600;}
		/**
		if (!validateVLAN(false)){
			return false;
		}
		if (!validateVLANNative(false)) {
			return false;
		}**/
		return true;
	
	}

	if (operation == 'newHive' ||
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
		operation == 'newRadiusProxy' ||
		operation == 'newRadiusServer' ||
		operation == 'newIpTrack' ||
		operation == 'newRouterIpTrack_Primary' ||
		operation == 'newRouterIpTrack_Backup1' ||
		operation == 'newRouterIpTrack_Backup2' ||
		operation == 'newTvNetwork' ||
		operation == 'newIdsPolicy' ||
		operation == 'newIpFilter' ||
		operation == 'newAccessConsole' ||
		operation == 'newSsidSelect' ||
		operation == 'newEth0ServiceFilter' || operation == 'newEth1ServiceFilter' 
		|| operation == 'newRed0ServiceFilter' || operation == 'newAgg0ServiceFilter'
		|| operation == 'newBackEth0ServiceFilter' || operation == 'newBackEth1ServiceFilter'
		|| operation == 'newBackRed0ServiceFilter' || operation == 'newBackAgg0ServiceFilter'
		|| operation == 'newBackWireServiceFilter' ||
		operation == 'newDeviceServiceFilter' ||
		operation == 'newEthernetAccess' ||
		operation == 'newEthernetBridge' ||
		operation == 'newClassifierMap' ||
		operation == 'newMarkerMap' ||
		operation == 'newClassification' ||
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
		operation == 'editRadiusProxy' ||
		operation == 'editRadiusServer' ||
		operation == 'editIpTrack' ||
		operation == 'editPrimaryTrackWan' ||
		operation == 'editBackup1TrackWan' ||
		operation == 'editBackup2TrackWan' ||
		operation == 'editTvNetwork' ||
		operation == 'editIdsPolicy' ||
		operation == 'editIpFilter' ||
		operation == 'editAccessConsole' ||
		operation == 'editEth0ServiceFilter' || operation == 'editEth1ServiceFilter' 
		|| operation == 'editRed0ServiceFilter' || operation == 'editAgg0ServiceFilter'
		|| operation == 'editBackEth0ServiceFilter' || operation == 'editBackEth1ServiceFilter'
		|| operation == 'editBackRed0ServiceFilter' || operation == 'editBackAgg0ServiceFilter'
		|| operation == 'editBackWireServiceFilter' ||
		operation == 'editDeviceServiceFilter' ||
		operation == 'editEthernetAccess' ||
		operation == 'editEthernetBridge' ||
		operation == 'editClassifierMap' ||
		operation == 'editMarkerMap' ||
		operation == 'newRouterIpTrack' ||
		operation == 'editRouterIpTrack' ||
		operation == 'editMgtNetwork'||
		operation == 'newMgtNetwork'||
		operation == 'newIpTrackWAN'||
		operation == 'editMstpRegion'||
		operation == 'newMstpRegion'||
		operation == 'newRoutingPbrPolicy'||
		operation == 'editRoutingPbrPolicy') {
		return true;
	}
	/**
	<s:if test="%{dataSource.blnWirelessRouter}">
		if (!validateMgtNetwork()){
			return false;
		}
	</s:if>
	<s:else>
		if (!validateVLAN(true)){
			return false;
		}
	</s:else>
	
	if (!validateVLANNative(true)) {
		return false;
	}
**/
	if(!validate8021XClientSettings()) {return false;}
	
	if(!validateStormControl()) {return false;}
	
	if (!validateMgtOptionConfig()){
		return false;
	}
	
	if (!validateAlgConfig()){
		return false;
	}
	
	if (!validateProbe()){
		return false;
	}
	
	/**
	if (!changeNetworkIp()){
		document.getElementById("hideServiceSettingsDiv").style.display="block";
        document.getElementById("showServiceSettingsDiv").style.display="none";
		return false;
	}**/
	
	if (!validateSlaSettings()){
		return false;
	}
	
	if (!validateReportIntervalValue()) {
		return false;
	}
	
	/**
	if (!validateConnectionAlarmValue()) {
		return false;
	}**/
	
	/* if(supportSwitch){
		if(Get(formName + "_dataSource_switchSettings_enableIgmpSnooping")){
			if(Get(formName + "_dataSource_switchSettings_enableIgmpSnooping").checked){
				if(!validateIgmpValue()){
					return false;
				}
			}else{
				var globalDelayLeaveQueryInterval = document.getElementById(formName+'_dataSource_switchSettings_globalDelayLeaveQueryInterval');
				var globalDelayLeaveQueryCount = document.getElementById(formName+'_dataSource_switchSettings_globalDelayLeaveQueryCount');
				var globalRouterPortAginTime = document.getElementById(formName+'_dataSource_switchSettings_globalRouterPortAginTime');
				var globalRobustnessCount = document.getElementById(formName+'_dataSource_switchSettings_globalRobustnessCount');
				if(globalDelayLeaveQueryInterval && globalDelayLeaveQueryInterval.value.length == 0){
					globalDelayLeaveQueryInterval.value = 1;
				}
				if(globalDelayLeaveQueryCount && globalDelayLeaveQueryCount.value.length == 0){
					globalDelayLeaveQueryCount.value = 2;
				}
				if(globalRouterPortAginTime && globalRouterPortAginTime.value.length == 0){
					globalRouterPortAginTime.value = 250;
				}
				if(globalRobustnessCount && globalRobustnessCount.value.length == 0){
					globalRobustnessCount.value = 2;
				}
			}
		}
	} */
	
	//validate VoIP
	if (<s:property value="dataSource.configType.wirelessAndRouterContained"/>) {
		if (Get(formName + "_dataSource_enableEth0LimitDownloadBandwidth").checked) {
			var eth0LimitDownloadRateElement = document.getElementById(formName + "_dataSource_eth0LimitDownloadRate");
			var eth0LimitDownloadRateMessage = hm.util.validateIntegerRange(eth0LimitDownloadRateElement.value, '<s:text name="config.configTemplate.voip.maxdownloadrate" />', 10, 15000);
			if (eth0LimitDownloadRateMessage != null) {
				hm.util.reportFieldError(eth0LimitDownloadRateElement, eth0LimitDownloadRateMessage);
				eth0LimitDownloadRateElement.focus();
				return false;
			}
		}
		if (Get(formName + "_dataSource_enableEth0LimitUploadBandwidth").checked) {
			var eth0LimitUploadRateElement = document.getElementById(formName + "_dataSource_eth0LimitUploadRate");
			var eth0LimitUploadRateMessage = hm.util.validateIntegerRange(eth0LimitUploadRateElement.value, '<s:text name="config.configTemplate.voip.maxuploadrate" />', 10, 15000);
			if (eth0LimitUploadRateMessage != null) {
				hm.util.reportFieldError(eth0LimitUploadRateElement, eth0LimitUploadRateMessage);
				eth0LimitUploadRateElement.focus();
				return false;
			}		
		}
		
		if (Get(formName + "_dataSource_enableUSBLimitDownloadBandwidth").checked) {
			var usbLimitDownloadRateElement = document.getElementById(formName + "_dataSource_usbLimitDownloadRate");
			var usbLimitDownloadRateMessage = hm.util.validateIntegerRange(usbLimitDownloadRateElement.value, '<s:text name="config.configTemplate.voip.maxdownloadrate" />', 10, 15000);
			if (usbLimitDownloadRateMessage != null) {
				hm.util.reportFieldError(usbLimitDownloadRateElement, usbLimitDownloadRateMessage);
				usbLimitDownloadRateElement.focus();
				return false;
			}
		}
		if (Get(formName + "_dataSource_enableUSBLimitUploadBandwidth").checked) {
			var usbLimitUploadRateElement = document.getElementById(formName + "_dataSource_usbLimitUploadRate");
			var usbLimitUploadRateMessage = hm.util.validateIntegerRange(usbLimitUploadRateElement.value, '<s:text name="config.configTemplate.voip.maxuploadrate" />', 10, 15000);
			if (usbLimitUploadRateMessage != null) {
				hm.util.reportFieldError(usbLimitUploadRateElement, usbLimitUploadRateMessage);
				usbLimitUploadRateElement.focus();
				return false;
			}
		}
	}
	
	if(!validateMstpRegion()){
		return false;
	}
	
	if(!validateManagementServerSettings()){
		return false;
	}
	return true;
}

function validateStormControl(){
	if(supportSwitch){
		var table = document.getElementById("tbl_storm");
		var rowCount = table.rows.length-2; //remove note and header line
		for(var i=0;i<rowCount;i++){
			if(Get('arrayBroadcast_' + i) == null){ continue;}
			if(!Get('arrayBroadcast_' + i).checked 
					&& !Get('arrayUnknownUnicast_' + i).checked
					&& !Get('arrayMulticast_' + i).checked
					&& !Get('arrayTcpsyn_' + i).checked){
				continue;
			}
			var ifType = document.getElementById("scIterfaceType_"+i);
			var rateLimitValue = document.getElementById("arrayRateLimitValue_"+i);
			var rateLimitType = document.getElementById("arrayRateLimitType_"+i);
			var minValue = 0;
			var maxValue = 100;
			
			var selectedMode = $('input:radio[name="dataSource.switchStormControlMode"]:checked').val();
			if(selectedMode == 0){
				if(rateLimitType.value==0){
					maxValue=1000000;
				} else if(rateLimitType.value==1){
					maxValue=100000000;
				} else if(rateLimitType.value==2){
					maxValue= 100
				}
			} else {
				maxValue=100000000;
			}
			
			if(rateLimitValue.value.length == 0) {
		    	hm.util.reportFieldError(rateLimitValue, 
		            '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" /></s:param></s:text>');
				document.getElementById("showSwitchSettingsDiv").style.display="none";
				document.getElementById("hideSwitchSettingsDiv").style.display="block";
		    	document.getElementById("showStormControlDiv").style.display="none";
				document.getElementById("hideStormControlDiv").style.display="block";
		    	rateLimitValue.focus();
		        return false;
		    }
	        var message = hm.util.validateIntegerRange(rateLimitValue.value, 
	                '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" />', minValue, maxValue);
	        if(message) {
	            hm.util.reportFieldError(rateLimitValue, message);
	    		document.getElementById("showSwitchSettingsDiv").style.display="none";
	    		document.getElementById("hideSwitchSettingsDiv").style.display="block";
	    		document.getElementById("showStormControlDiv").style.display="none";
	    		document.getElementById("hideStormControlDiv").style.display="block";
	            rateLimitValue.focus();
	            return false;
	        }
		}
	}
	
	return true;
}

function validate8021XClientSettings() {
    // 802.1X optional settings
    var clientExpireTimeEl = Get(formName + "_dataSource_clientExpireTime8021X");
    var clientIntervalEl = Get(formName + "_dataSource_clientSuppressInterval8021X");
    if(clientExpireTimeEl && clientIntervalEl) {
        if(clientExpireTimeEl.value.length == 0) {
            hm.util.reportFieldError(clientExpireTimeEl, 
                    '<s:text name="error.requiredField"><s:param><s:text name="config.lanProfile.802dot1X.expireTime" /></s:param></s:text>');
            clientExpireTimeEl.focus();
            return false;
        }
        var message = hm.util.validateIntegerRange(clientExpireTimeEl.value, 
                '<s:text name="config.lanProfile.802dot1X.expireTime" />', 60, 86400);
        if(message) {
            hm.util.reportFieldError(clientExpireTimeEl, message);
            clientExpireTimeEl.focus();
            return false;
        }
        
        if(clientIntervalEl.value.length == 0) {
            hm.util.reportFieldError(clientIntervalEl, 
                    '<s:text name="error.requiredField"><s:param><s:text name="config.lanProfile.802dot1X.interval" /></s:param></s:text>');
            clientIntervalEl.focus();
            return false;
        }
        var message = hm.util.validateIntegerRange(clientIntervalEl.value, 
                '<s:text name="config.lanProfile.802dot1X.interval" />', 0, 3600);
        if(message) {
            hm.util.reportFieldError(clientIntervalEl, message);
            clientIntervalEl.focus();
            return false;
        }
    }
    return true;
}

function showOrHideMapPanel(value){
	if (value) {
		Get("hideOverrideMapPanelDiv").style.display="";
		Get("uncheckedNoteForMapOverride").style.display="none";
	}  else {
		Get("uncheckedNoteForMapOverride").style.display="";
		Get("hideOverrideMapPanelDiv").style.display="none";
		initMapPanelContent();
	}	
}

function initMapPanelContent(){
	$("input[name='arraySsidOnly']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayNetwork']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayMacOui']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arraySsid']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckP']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckD']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckPT']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckDT']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckE']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
	$("input[name='arrayCheckET']").each(function(i){
		this.checked = false;
		this.disabled = false;
	});
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

function changeAllTrafficTypeCheckBox(index){
	if (document.getElementById('arrayAllTrafficType_' + index).checked){	
		Get('arrayBroadcast_' + index).checked=true;
		Get('arrayUnknownUnicast_' + index).checked=true;
		Get('arrayMulticast_' + index).checked=true;
		Get('arrayTcpsyn_' + index).checked=true;
		Get('arrayRateLimitValue_' + index).disabled = false;
		Get('scIterfaceType_' + index).disabled = false;
		Get('arrayRateLimitType_' + index).disabled = false;
		
	} else {
		Get('arrayBroadcast_' + index).checked=false;
		Get('arrayUnknownUnicast_' + index).checked=false;
		Get('arrayMulticast_' + index).checked=false;
		Get('arrayTcpsyn_' + index).checked=false;
		Get('arrayRateLimitValue_' + index).disabled = true;
		Get('scIterfaceType_' + index).disabled = true;
		Get('arrayRateLimitType_' + index).disabled = true;
	}
}

function openQuestionMask() {
	showInfoDialog('<s:text name="gotham_23.config.configTemplate.routerWanConfigurationEnbale.information"/>');
}

function clickOverrideTF4IndividualAPs(checked){
	if(checked){
		$('#trafficFilter4IndividualAPs').show();
	} else {
		$('#trafficFilter4IndividualAPs').hide();
	}
}

function changeTrafficTypeCheckBox(index){
		var allTrafficType = document.getElementById('arrayAllTrafficType_' + index);
		if(Get('arrayBroadcast_' + index).checked 
				&& Get('arrayUnknownUnicast_' + index).checked
				&& Get('arrayMulticast_' + index).checked
				&& Get('arrayTcpsyn_' + index).checked){
			allTrafficType.checked= true;
		} else {
			allTrafficType.checked = false;
		}
		
		if(Get('arrayBroadcast_' + index).checked 
				|| Get('arrayUnknownUnicast_' + index).checked
				|| Get('arrayMulticast_' + index).checked
				|| Get('arrayTcpsyn_' + index).checked){
			Get('arrayRateLimitValue_' + index).disabled = false;
			Get('scIterfaceType_' + index).disabled = false;
			Get('arrayRateLimitType_' + index).disabled = false;
		} else {
			Get('arrayRateLimitValue_' + index).disabled = true;
			Get('scIterfaceType_' + index).disabled = true;
			Get('arrayRateLimitType_' + index).disabled = true;
		}
}

function changeRateLimitType(index,value){
	var text ='';
	var rateLimitValue = document.getElementById('arrayRateLimitValue_' + index);
	if(value==0){
		text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"/>';
		rateLimitValue.maxLength=7
		rateLimitValue.value=BPS_DEFULT_VALUE;
	} else if(value==1) {
		text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"/>';
		rateLimitValue.maxLength=9
		rateLimitValue.value=PPS_DEFULT_VALUE;
	} else {
		text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"/>';
		rateLimitValue.maxLength=3
		rateLimitValue.value=PERCENTAGE_DEFULT_VALUE;
	}
	//$("#scRateLimitRange_"+ index).text(text);
	Get('scRateLimitRange_' + index).innerHTML = text;
}

function changestormMode(value,length){
	for(var i=0;i<length;i++){
		var rateLimitValue = document.getElementById('arrayRateLimitValue_' + i);
		var limitType = $("#arrayRateLimitType_"+i).val();
		if(value == 0){
			$("#stormLimitTypePacketId_"+i).hide();
			$("#arrayRateLimitType_"+i).show();
			if(limitType==2){
				text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"/>';
				rateLimitValue.maxLength=3;
				if(!Get('arrayBroadcast_' + i).checked 
						&& !Get('arrayUnknownUnicast_' + i).checked
						&& !Get('arrayMulticast_' + i).checked
						&& !Get('arrayTcpsyn_' + i).checked){
					rateLimitValue.value=PERCENTAGE_DEFULT_VALUE;
				}
				
			} else {
				text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"/>';
				rateLimitValue.maxLength=7;
				
				if(!Get('arrayBroadcast_' + i).checked 
						&& !Get('arrayUnknownUnicast_' + i).checked
						&& !Get('arrayMulticast_' + i).checked
						&& !Get('arrayTcpsyn_' + i).checked){
					rateLimitValue.value=BPS_DEFULT_VALUE;
				}
			}
		} else {
			$("#stormLimitTypePacketId_"+i).show();
			$("#arrayRateLimitType_"+i).hide();
			text = '&nbsp;<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"/>';
			rateLimitValue.maxLength=9;
			if(!Get('arrayBroadcast_' + i).checked 
					&& !Get('arrayUnknownUnicast_' + i).checked
					&& !Get('arrayMulticast_' + i).checked
					&& !Get('arrayTcpsyn_' + i).checked){
				rateLimitValue.value=PPS_DEFULT_VALUE;
			}
		}
		Get('scRateLimitRange_' + i).innerHTML = text;
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
/**
function validateMgtNetwork() {
	var inputElement = document.getElementById(formName + "_mgtNetworkId");
	if (inputElement.value < 0) {
	    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.managementNetwork" /></s:param></s:text>');
        document.getElementById("hideVlanDiv").style.display="block";
       	document.getElementById("showVlanDiv").style.display="none";
	    inputElement.focus();
	    return false;
	}
	return true;

}

function validateVLAN(flag){
	var vlannames = document.getElementById("vlanIdSelect");
	var vlanValue = document.forms[formName].inputVlanIdValue;
	if (flag){
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlan" /></s:param></s:text>');
		    vlanValue.focus();
		    document.getElementById("hideVlanDiv").style.display="block";
        	document.getElementById("showVlanDiv").style.display="none";
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlan" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), message);
	        vlanValue.focus();
	        document.getElementById("hideVlanDiv").style.display="block";
        	document.getElementById("showVlanDiv").style.display="none";
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
		    vlanValue.focus();
		    document.getElementById("hideVlanDiv").style.display="block";
        	document.getElementById("showVlanDiv").style.display="none";
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlanNative" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlanNative"), message);
	        vlanValue.focus();
	        document.getElementById("hideVlanDiv").style.display="block";
        	document.getElementById("showVlanDiv").style.display="none";
	        return false;
	    }
	    document.forms[formName].vlanNativeId.value = -1;
	} else {
		document.forms[formName].vlanNativeId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}
**/
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
/**
function changeNetworkIp(){
	var ipNetwork=Get("managementNetwork");
	if (ipNetwork.value.length==0) {
		hm.util.reportFieldError(ipNetwork, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.managementNetwork" /></s:param></s:text>');
 		ipNetwork.focus();
 		return false;
	}
	var ipArr=ipNetwork.value.split("/");
	if (ipArr.length!=2) {
		hm.util.reportFieldError(ipNetwork, '<s:text name="error.formatInvalid"><s:param><s:text name="config.configTemplate.managementNetwork" /></s:param></s:text>');
		ipNetwork.focus();
		return false;
	}
	if(!checkIpAddress(ipNetwork, ipNetwork, ipArr[0], '<s:text name="config.configTemplate.managementNetwork" />')) {
		return false;
	}
	
	var message = hm.util.validateIntegerRange(ipArr[1], '<s:text name="config.configTemplate.managementNetwork" />' + ' netmask',4,32);
	if (message != null) {
           hm.util.reportFieldError(ipNetwork, message);
           ipNetwork.focus();
           return false;
     }
     return true;
}
function checkIpAddress(focus, ip, ipValue,title, fn) {
	if (ipValue.length == 0) {
        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        ip.focus();
        return false;
    } else if (!hm.util.validateIpAddress(ipValue)) {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		ip.focus();
		return false;
	}
	return true;
}
**/
function validateMgtAdvancedForJson(operation){
	if (validate(operation)){
		hm.options.selectAllOptions('ipTrackIds');
		hm.options.selectAllOptions('tvNetworkIds');
		return true;
	}
	return false;
}

function enabledVoipCheckBox(checked, ifId) {
	if (checked){
		Get(formName + "_dataSource_" + ifId).readOnly=false;
	} else {
		Get(formName + "_dataSource_" + ifId).readOnly=true;
		Get(formName + "_dataSource_" + ifId).value=100
	}
}

function showHideSwitchSettingsDiv(value){
	if (value==1) {
		document.getElementById("showSwitchSettingsDiv").style.display="none";
		document.getElementById("hideSwitchSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showSwitchSettingsDiv").style.display="block";
		document.getElementById("hideSwitchSettingsDiv").style.display="none";
	}
}

function showHideSTPSettingsDiv(value){
	if (value==1) {
		document.getElementById("showSTPSettingsDiv").style.display="none";
		document.getElementById("hideSTPSettingsDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showSTPSettingsDiv").style.display="block";
		document.getElementById("hideSTPSettingsDiv").style.display="none";
	
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

function enabledStpCheckBox(value) {
	disabledAllStpSettingsRaido(value);
	if (value) {
		document.getElementById("enabledStpMode").style.display= "";
	} else {
		document.getElementById("enabledStpMode").style.display= "none";
	}
}

function disabledAllStpSettingsRaido(enableStp){
	var allStpSettings = document.getElementsByName("dataSource.switchSettings.stpSettings.stp_mode");
	var mstp = document.getElementById("stpModeMstp" + STP_MODE_MSTP);
	if(enableStp){
		for(var i = 0 ; i < allStpSettings.length ; i ++){
			allStpSettings[i].disabled = false;
		}
		 if(mstp.checked){
			changeMstp(enableStp);
		}
	}else{
		changeMstp(!enableStp);
		for(var i = 0 ; i < allStpSettings.length ; i ++){
			allStpSettings[i].disabled = true;
		}
	}
}

function changeSelect(value){
	if(value){
  		changeMstp(!value);
 	} 
}

function changeMstp(value){
	var mstpRegion = document.getElementById(formName + "_mstpRegionId");
	var writeDisabled =  document.getElementById(formName + "_writeDisabled");
	var mstpControlLink = document.getElementById("mstpControlLink");
	var enableStp = document.getElementById(formName + "_dataSource_switchSettings_stpSettings_enableStp");
	if(value && enableStp.checked){
		mstpRegion.disabled = false;
		mstpRegion.style.display = "block";
		if(writeDisabled.value == "disabled"){
			mstpControlLink.innerHTML="<a class='marginBtn' href='javascript:submitAction(\"editMstpRegion\");' ><img class='dinl' src='<s:url value='/images/modify.png' />' width='16' height='16' alt='Modify' title='Modify' /></a>";
		} else {
			mstpControlLink.innerHTML="<a class='marginBtn' href='javascript:submitAction(\"newMstpRegion\");' ><img class='dinl' src='<s:url value='/images/new.png' />' width='16' height='16' alt='New' title='New' /></a><a class='marginBtn' href='javascript:submitAction(\"editMstpRegion\")' ><img class='dinl' src='<s:url value='/images/modify.png' />' width='16' height='16' alt='Modify' title='Modify' /></a>";
		}
	}else{
		mstpRegion.disabled = true;
		mstpRegion.style.display = "none";
		mstpControlLink.innerHTML="";
	}
}

function validateMstpRegion(){
	if(supportSwitch){
	var mstpRegion = document.getElementById(formName + "_mstpRegionId");
	var enableStp = document.getElementById(formName + "_dataSource_switchSettings_stpSettings_enableStp");
	var mstp = document.getElementById("stpModeMstp" + STP_MODE_MSTP);
	var mstpError = document.getElementById("mstpError");
	
	if(enableStp.checked && mstp.checked){
		if(typeof mstpRegion.value == "undefined"|| mstpRegion.value == -1 || mstpRegion.value.length == 0){
			hm.util.reportFieldError(mstpError, '<s:text name="error.stp.mstp.region.required" />');
			
			$("#showSwitchSettingsDiv").hide();
			$("#hideSwitchSettingsDiv").show();
			
			$("#showSTPSettingsDiv").hide();
			$("#hideSTPSettingsDiv").show();
	        return false;
	    }
	}
	}
	return true;
}

function enabledIgmpSnoopingCheckBox(value) {
	if (value) {
		document.getElementById("show_igmp").style.display= "";
	} else {
		document.getElementById("show_igmp").style.display= "none";
	}
}

function validateIgmpValue(){
	if(supportSwitch){
		var globalDelayLeaveQueryInterval = document.getElementById(formName+'_dataSource_switchSettings_globalDelayLeaveQueryInterval');
		var globalDelayLeaveQueryCount = document.getElementById(formName+'_dataSource_switchSettings_globalDelayLeaveQueryCount');
		var globalRouterPortAginTime = document.getElementById(formName+'_dataSource_switchSettings_globalRouterPortAginTime');
		var globalRobustnessCount = document.getElementById(formName+'_dataSource_switchSettings_globalRobustnessCount');
		if(globalDelayLeaveQueryInterval){
			if(globalDelayLeaveQueryInterval.value.length != 0){
				var message = hm.util.validateIntegerRange(globalDelayLeaveQueryInterval.value, '<s:text name="config.switchSettings.igmp.delay.leave.interval" />',
		                <s:property value="1" />,
		                <s:property value="25" />);
				if (message != null) {
				hm.util.reportFieldError(globalDelayLeaveQueryInterval, message);
				showIGMPSettingDiv(1);
				globalDelayLeaveQueryInterval.focus();
				return false;
				}
			}else{
				hm.util.reportFieldError(globalDelayLeaveQueryInterval, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.interval" /></s:param></s:text>');
				showIGMPSettingDiv(1);
				globalDelayLeaveQueryInterval.focus();
				return false;
			}
		}
		if(globalDelayLeaveQueryCount){
			if(globalDelayLeaveQueryCount.value.length != 0){
				var message = hm.util.validateIntegerRange(globalDelayLeaveQueryCount.value, '<s:text name="config.switchSettings.igmp.delay.leave.count" />',
		                <s:property value="1" />,
		                <s:property value="7" />);
				if (message != null) {
				hm.util.reportFieldError(globalDelayLeaveQueryCount, message);
				showIGMPSettingDiv(1);
				globalDelayLeaveQueryCount.focus();
				return false;
				}
			}else{
				hm.util.reportFieldError(globalDelayLeaveQueryCount, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.count" /></s:param></s:text>');
				showIGMPSettingDiv(1);
				globalDelayLeaveQueryCount.focus();
				return false;
			}
		}
		if(globalRouterPortAginTime){
			if(globalRouterPortAginTime.value.length != 0){
				var message = hm.util.validateIntegerRange(globalRouterPortAginTime.value, '<s:text name="config.switchSettings.igmp.router.aging.time" />',
		                <s:property value="30" />,
		                <s:property value="1000" />);
				if (message != null) {
				hm.util.reportFieldError(globalRouterPortAginTime, message);
				showIGMPSettingDiv(1);
				globalRouterPortAginTime.focus();
				return false;
				}
			}else{
				hm.util.reportFieldError(globalRouterPortAginTime, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.router.aging.time" /></s:param></s:text>');
				showIGMPSettingDiv(1);
				globalRouterPortAginTime.focus();
				return false;
			}
		}
		if(globalRobustnessCount){
			if(globalRobustnessCount.value.length != 0){
				var message = hm.util.validateIntegerRange(globalRobustnessCount.value, '<s:text name="config.switchSettings.igmp.robustness.count" />',
		                <s:property value="1" />,
		                <s:property value="3" />);
				if (message != null) {
				hm.util.reportFieldError(globalRobustnessCount, message);
				showIGMPSettingDiv(1);
				globalRobustnessCount.focus();
				return false;
				}
			}else{
				hm.util.reportFieldError(globalRobustnessCount, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.robustness.count" /></s:param></s:text>');
				showIGMPSettingDiv(1);
				globalRobustnessCount.focus();
				return false;
			}
		}
	}	
	return true;
}
function refreshOptionsList(){
	var primarySelected = $("#primaryIpTrackId").val();
	var backup1Selected = $("#backup1TrackId").val();
	var backup2Selected = $("#backup2TrackId").val();
	
	var selectedArray = new Array();
	selectedArray.push(primarySelected);
	if($.inArray(backup1Selected,selectedArray)<0) selectedArray.push(backup1Selected);
	if($.inArray(backup2Selected,selectedArray)<0) selectedArray.push(backup2Selected);
	
	
	$("#primaryIpTrackId option").remove();
	$("#backup1TrackId option").remove();
	$("#backup2TrackId option").remove();
	
	$("#primaryIpTrackId").append("<option value='0'></option>");
	$("#backup1TrackId").append("<option value='0'></option>");
	$("#backup2TrackId").append("<option value='0'></option>");
	
	$("#ipTrackWANList_ID option").each(function(){
		var v = $(this).attr("value");
		if(v == primarySelected){
			$("#primaryIpTrackId").append($(this).clone().attr("selected","seleted"));
		}
		if(v == backup1Selected){
			$("#backup1TrackId").append($(this).clone().attr("selected","seleted"));
		}
		if(v == backup2Selected){
			$("#backup2TrackId").append($(this).clone().attr("selected","seleted"));
		} 
		var exist = $.inArray(v,selectedArray);
		if(exist < 0){
			$("#primaryIpTrackId").append($(this).clone());
			$("#backup1TrackId").append($(this).clone());
			$("#backup2TrackId").append($(this).clone());
		}
	});
}

function validateManagementServerSettings(){
	if(dns_checked_flag){
		return true;
	}
	var dnsSettings = document.getElementById(formName + "_mgtDnsId");
	var ntpSettings = document.getElementById(formName + "_mgtTimeId");
	if(dnsSettings.value == -1 || ntpSettings.value == -1){
		var mybuttons = [ { text:"OK", handler: function(){dns_checked_flag = true;saveSubNetWorkPolicy();this.hide();} }, 
	                  { text:"Cancel", handler: function(){this.hide();showHideServerSettingsDiv(1);dnsSettings.focus()}, isDefault:true} ];
	    var msg = "<html><body>"+'<s:text name ="confirm.template.dns.ntp" />' +"</body></html>";
	    var dlg = userDefinedConfirmDialog(msg, mybuttons, "Please Confirm");
	    dlg.show();
		return false;
	}
	return true;
}
</script>
<div>
	<s:form action="networkPolicy" name="networkPolicyMgtAdvancedSetting"
		id="networkPolicyMgtAdvancedSetting">
		<%--<s:hidden name="vlanId" />
	<s:hidden name="vlanNativeId" />--%>
		<s:hidden name="ipTrackId" />
		<s:hidden name="tvNetworkId" />
		<s:hidden name="operation" />
		<s:hidden name="routerIpTrackId" />
		<s:hidden name="writeDisabled" value="%{writeDisabled}" />
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td style="padding: 4px 4px 4px 4px;" valign="top">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<%-- Vlan Settings 
					<tr>
						<td>
							<div style="display:<s:property value="%{showVlanDiv}"/>" id="showVlanDiv">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" onclick="showHideVlanDiv(1);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;
												<s:if test="%{dataSource.blnWirelessRouter}">
													<s:text name="config.configTemplate.vlanRouterSettingTitle" />
												</s:if>
												<s:else>
													<s:text name="config.configTemplate.vlanSettingTitle" />
												</s:else>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="display:<s:property value="%{hideVlanDiv}"/>" id="hideVlanDiv">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" onclick="showHideVlanDiv(2);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;
												<s:if test="%{dataSource.blnWirelessRouter}">
													<s:text name="config.configTemplate.vlanRouterSettingTitle" />
												</s:if>
												<s:else>
													<s:text name="config.configTemplate.vlanSettingTitle" />
												</s:else>
										</td>
									</tr>
									<tr>
										<td style="padding: 4px 4px 4px 30px;" valign="top">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td>
														<fieldset>
															<table border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																	<s:if test="%{dataSource.blnWirelessRouter}">
																		<td class="labelT1" width="140px" nowrap="nowrap"><s:text
																			name="config.configTemplate.managementNetwork" /></td>
																		<td width="200px"><s:select
																			name="mgtNetworkId" list="%{list_mgtNetwork}" listKey="id"
																			listValue="value" cssStyle="width: 140px;" />
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																				src="<s:url value="/images/new_disable.png" />"
																				width="16" height="16" alt="New" title="New" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('newMgtNetwork')"><img class="dinl"
																				src="<s:url value="/images/new.png" />"
																				width="16" height="16" alt="New" title="New" /></a>
																			</s:else>
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																				src="<s:url value="/images/modify_disable.png" />"
																				width="16" height="16" alt="Modify" title="Modify" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('editMgtNetwork')"><img class="dinl"
																				src="<s:url value="/images/modify.png" />"
																				width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else>
																		</td>
																	</s:if>
																	<s:else>
																		<td class="labelT1" width="140px" nowrap="nowrap"><s:text
																			name="config.configTemplate.vlan" /></td>
																		<td width="200px">
																			<ah:createOrSelect divId="errorDisplayVlan" list="list_vlan" typeString="Vlan" 
																				selectIdName="vlanIdSelect" inputValueName="inputVlanIdValue" swidth="140px" />
																		</td>
																	</s:else>
																	<td class="labelT1" width="140px" nowrap="nowrap" style="padding-left: 25px"><s:text
																		name="config.configTemplate.vlanNative" /></td>
																	<td>
																		<ah:createOrSelect divId="errorDisplayVlanNative" list="list_vlan" typeString="VlanNative" 
																			selectIdName="vlanNativeIdSelect" inputValueName="inputVlanNativeIdValue" swidth="140px"/>
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
								</table>
							</div>
						</td>
					</tr>--%>
						<%-- Router Radius Settings --%>
						<tr>
							<td><div style="margin-left: 10px;">
									<table>
										<tr>
											<td class="labelT1" width="50px"><s:text
													name="config.configTemplate.hive" /><font color="red"><s:text
														name="*" /></font></td>
											<td style="padding-right: 5px;"><s:select id="np_hiveId"
													name="hiveId" list="%{list_hive}" listKey="id"
													listValue="value" cssStyle="width: 160px;" /> &nbsp; <s:if
													test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />" width="16"
														height="16" alt="New" title="New" />
												</s:if> <s:else>
													<a class="marginBtn" href="javascript:newHiveProfile();"><img
														class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
												</s:else> <s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
												</s:if> <s:else>
													<a class="marginBtn" href="javascript:editHiveProfile();"><img
														class="dinl" src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else></td>
										</tr>
									</table>
								</div></td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{showRouterRadiusSettingsDiv}"/>"
									id="showRouterRadiusSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1"
												onclick="showHideRouterRadiusSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.routerSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{hideRouterRadiusSettingsDiv}"/>"
									id="hideRouterRadiusSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1"
												onclick="showHideRouterRadiusSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.routerSettings" /></td>
										</tr>
										<tr>
											<td style="padding: 4px 4px 4px 30px;"><s:checkbox
													name="dataSource.enabledWanConfiguration"
													value="%{dataSource.enabledWanConfiguration}" />
												 <s:text name="gotham_23.config.configTemplate.routerWanConfigurationEnbale" />
												 <a style="padding-left: 15px;" href="javascript: openQuestionMask();">?</a>
											</td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 30px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.routerRadiusSettings" />
																</legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.routerRadiusServer" /></td>
																		<td style="padding-right: 5px;"><s:select
																				name="radiusServerId"
																				list="%{list_radiusServerOnAp}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newRadiusServer')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editRadiusServer')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																		<td class="labelT1" width="120px"><s:text
																				name="config.configTemplate.routerRadiusProxy" /></td>
																		<td width="200px"><s:select name="radiusProxyId"
																				list="%{list_radiusProxy}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newRadiusProxy')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editRadiusProxy')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>
																</table>
															</fieldset> <%--
														<table>
															<tr>
																<td height="4px"/>
															</tr>
														</table>
														<fieldset><legend><s:text name="config.configTemplate.routerPpskSettings"></s:text></legend>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td style="padding: 4px 2px 4px 6px">
																		<s:checkbox name="dataSource.enabledRouterPpskServer"></s:checkbox>
																	</td>
																	<td><s:text name="config.configTemplate.routerPpskEnable" /></td>
																</tr>
															</table>
														</fieldset>
														--%>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 30px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.router8021XSettings" />
																</legend>
																<table cellpadding="0" cellspacing="0" border="0">
																	<tr>
																		<td class="labelT1" width="220px"><s:text
																				name="config.lanProfile.802dot1X.expireTime" /></td>
																		<td class="labelT1"><s:textfield
																				name="dataSource.clientExpireTime8021X"
																				maxlength="5"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;
																			<s:text
																				name="config.lanProfile.802dot1X.expireTime.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text
																				name="config.lanProfile.802dot1X.interval" /></td>
																		<td class="labelT1"><s:textfield
																				name="dataSource.clientSuppressInterval8021X"
																				maxlength="4"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;
																			<s:text
																				name="config.lanProfile.802dot1X.interval.range" />
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td style="padding-left: 30px">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="labelT1" width="120px"><s:text
																name="config.networkpolicy.routingPolicy.title" /></td>
														<td width="200px"><s:select name="routingPolicyId"
																list="%{list_routingPolicy}" listKey="id"
																listValue="value" cssStyle="width: 140px;" /> <s:if
																test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
															</s:if> <s:else>
																<a class="marginBtn"
																	href="javascript:submitAction('newRoutingPbrPolicy')"><img
																	class="dinl" src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
															</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
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
							</td>
						</tr>
						<%-- Switch Settings --%>
						<s:if test="%{needSwitchSettings}">
							<tr>
								<td>
									<div
										style="display:<s:property value="%{showSwitchSettingsDiv}"/>"
										id="showSwitchSettingsDiv">
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1" onclick="showHideSwitchSettingsDiv(1);"
													style="cursor: pointer"><img
													src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
														name="config.switchSettings" /></td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div
										style="display:<s:property value="%{hideSwitchSettingsDiv}"/>"
										id="hideSwitchSettingsDiv">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" onclick="showHideSwitchSettingsDiv(2);"
													style="cursor: pointer"><img
													src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
													alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
														name="config.switchSettings" /></td>
											</tr>
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0"
														width="100%">
														<tr>
															<td style="padding-left: 15px;">
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%">
																	<tr>
																		<td class="labelT1" colspan="3"><div>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td><script type="text/javascript">insertFoldingLabelContext('&nbsp;<s:text name="config.switchSettings.stpSettings" />','stpSettings');</script></td>
																					</tr>
																					<tr>
																						<td><div id="stpSettings"
																								style="display: <s:property value="hideSwitchSettingsDiv"/>">
																								<table border="0" cellspacing="0"
																									cellpadding="0">
																									<tr>
																										<td class="labelT1" colspan="3"
																											style="padding-left: 13px"><s:checkbox
																												name="dataSource.switchSettings.stpSettings.enableStp"
																												onclick="enabledStpCheckBox(this.checked);"
																												style="margin-bottom:2px;" /> <s:text
																												name="config.switchSettings.enableSTP"></s:text></td>
																									</tr>
																									<tr>
																										<td style="padding-left: 26px">
																											<table border="0" cellspacing="0"
																												cellpadding="0" width="100%"
																												id="enabledStpMode"
																												style="display:<s:property value="%{enabledStpMode}"/>">
																												<tr>
																													<td rowspan="3" valign="top"
																														class="labelT1" width="130px"><s:text
																															name="config.switchSettings.STPmode"></s:text>
																													</td>
																													<td class="labelT1" width="100px"><s:radio
																															list="%{stpModeStp}"
																															name="dataSource.switchSettings.stpSettings.stp_mode"
																															listKey="key" listValue="value"
																															disabled="!dataSource.switchSettings.stpSettings.enableStp"
																															onclick="changeSelect(this.checked)" /></td>
																												</tr>
																												<tr>
																													<td class="labelT1"><s:radio
																															list="%{stpModeRstp}"
																															name="dataSource.switchSettings.stpSettings.stp_mode"
																															listKey="key" listValue="value"
																															disabled="!dataSource.switchSettings.stpSettings.enableStp"
																															onclick="changeSelect(this.checked)" /></td>
																												</tr>
																												<tr>
																													<td class="labelT1"><s:radio list="%{stpModeMstp}"
																															name="dataSource.switchSettings.stpSettings.stp_mode"
																															id="stpModeMstp" listKey="key" listValue="value"
																															disabled="!dataSource.switchSettings.stpSettings.enableStp"
																															onclick="changeMstp(this.checked)" /></td>
																													<td style="padding-right: 5px; width: 140px;"><s:select
																															name="mstpRegionId" list="%{list_mstpRegion}"
																															listKey="id" listValue="value"
																															cssStyle="width: 140px;display:%{mstpEditButton}"
																															disabled="%{enabledMstpMode}" /></td>
																													<td id="mstpControlLink">
																														<s:if test="%{writeDisabled}">
																															<a class='marginBtn'
																																href='javascript:submitAction("editMstpRegion");' style="display:<s:property value="%{mstpEditButton}"/>"><img
																																class='dinl'
																																src='<s:url value='images/modify.png' />'
																																width='16' height='16' alt='Modify'
																																title='Modify' /></a>
																														</s:if>
																														<s:else>
																															<a class='marginBtn'
																																href='javascript:submitAction("newMstpRegion");' style="display:<s:property value="%{mstpEditButton}"/>"><img
																																class='dinl'
																																src='<s:url value='images/new.png' />'
																																width='16' height='16' alt='New' title='New' /></a>
																															<a class='marginBtn'
																																href='javascript:submitAction("editMstpRegion")' style="display:<s:property value="%{mstpEditButton}"/>"><img
																																class='dinl'
																																src='<s:url value='/images/modify.png' />'
																																width='16' height='16' alt='Modify'
																																title='Modify' /></a>
																														</s:else>
																													</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																								</table>
																							</div></td>
																					</tr>
																				</table>
																			</div></td>
																	</tr>
																	<tr>
																		<td id="mstpError"></td>
																	</tr>
																	<%-- Storm Control --%>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{showStormControlDiv}"/>"
																							id="showStormControlDiv">
																							<table cellspacing="0" cellpadding="0" border="0">
																								<tr>
																									<td class="labelT1"
																										onclick="showHideStormControlDiv(1);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																										alt="Show Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.configTemplate.switchSettings.stormControl.title" />
																									</td>
																								</tr>
																							</table>
																						</div>
																					</td>
																				</tr>
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{hideStormControlDiv}"/>"
																							id="hideStormControlDiv">
																							<table cellspacing="0" cellpadding="0" border="0"
																								width="100%">
																								<tr>
																									<td class="labelT1"
																										onclick="showHideStormControlDiv(2);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																										alt="Hide Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.configTemplate.switchSettings.stormControl.title" />
																									</td>
																								</tr>
																								<tr>
																									<td style="padding-left: 20px;">
																										<table cellspacing="0" cellpadding="0"
																											border="0" id="tbl_storm">
																											<tr>
																												<td class="labelT1" align="center"
																													colspan="4"><s:radio label="Gender"
																														id="stormControlByteMode"
																														name="dataSource.switchStormControlMode"
																														list="%{stormRateLimitByte}"
																														onchange="changestormMode(this.value,%{dataSource.stormControlList.size()});"
																														listKey="key" listValue="value" /></td>
																												<td class="labelT1" align="center"
																													colspan="4"><s:radio label="Gender"
																														id="stormControlPackedMode"
																														name="dataSource.switchStormControlMode"
																														list="%{stormRateLimitPacket}"
																														onchange="changestormMode(this.value,%{dataSource.stormControlList.size()});"
																														listKey="key" listValue="value" /></td>
																											</tr>

																											<tr>
																												<th align="center" width="100px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.interface" /></th>
																												<th align="center" width="50px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.typeall" /></th>
																												<th align="center" width="60px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.broadcast" /></th>
																												<th align="center" width="100px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.unknownUnicast" /></th>
																												<th align="center" width="50px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.muticast" /></th>
																												<th align="center" width="50px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.tcpsyn" /></th>
																												<th align="center" width="100px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.ratelimittype" /></th>
																												<th align="left" width="180px;"><s:text
																														name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" /></th>
																											</tr>
																											<s:iterator
																												value="%{dataSource.stormControlTreeMap.values}"
																												status="status"
																												id="templeteStormControl_access">
																												<s:if test="%{interfaceType=='Access'}">
																													<tr style="background-color: #FFFFFF;">
																														<td align="center"><s:property
																																value="interfaceType" /> <s:hidden
																																name="arrayInterfaceType"
																																id="scIterfaceType_%{#status.index}"
																																disabled="%{#templeteStormControl_access.disableRateLimit}"
																																value="%{#templeteStormControl_access.interfaceType}" />
																														</td>
																														<td align="center"><s:checkbox
																																name="arrayAllTrafficType"
																																value="%{#templeteStormControl_access.allTrafficType}"
																																id="arrayAllTrafficType_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeAllTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayBroadcast"
																																value="%{#templeteStormControl_access.broadcast}"
																																id="arrayBroadcast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayUnknownUnicast"
																																value="%{#templeteStormControl_access.unknownUnicast}"
																																id="arrayUnknownUnicast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayMulticast"
																																value="%{#templeteStormControl_access.multicast}"
																																id="arrayMulticast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayTcpsyn"
																																value="%{#templeteStormControl_access.tcpsyn}"
																																id="arrayTcpsyn_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:select
																																name="arrayRateLimitType"
																																value="%{#templeteStormControl_access.rateLimitType}"
																																disabled="%{#templeteStormControl_access.disableRateLimit}"
																																id="arrayRateLimitType_%{#status.index}"
																																list="%{list_stormLimitType}"
																																listKey="id" listValue="value"
																																cssStyle="display: %{dataSource.showStormLimitTypeBased}"
																																onchange="changeRateLimitType(%{#status.index},this.value);" />
																															<label
																															id="stormLimitTypePacketId_<s:property value="%{#status.index}"/>"
																															style="display: <s:property value="%{dataSource.showStormLimitTypePacket}"/>">PPS</label></td>
																														<td align="left"><s:textfield
																																name="arrayRateLimitValue"
																																value="%{#templeteStormControl_access.rateLimitValue}"
																																disabled="%{#templeteStormControl_access.disableRateLimit}"
																																id="arrayRateLimitValue_%{#status.index}"
																																cssStyle="width:70px;"
																																onkeypress="return hm.util.keyPressPermit(event,'ten');"
																																maxlength="%{#templeteStormControl_access.rateLimitValueLength}" />
																															<label
																															id="scRateLimitRange_<s:property value='%{#status.index}'/>"><s:property
																																	value="%{#templeteStormControl_access.rateLimitRange}" /></label></td>
																													</tr>
																												</s:if>
																											</s:iterator>
																											<s:iterator
																												value="%{dataSource.stormControlTreeMap.values}"
																												status="status"
																												id="templeteStormControl_8021Q">
																												<s:if test="%{interfaceType=='802.1Q'}">
																													<tr style="background-color: #F5F5F5;">
																														<td align="center"><s:property
																																value="interfaceType" /> <s:hidden
																																name="arrayInterfaceType"
																																id="scIterfaceType_%{#status.index}"
																																disabled="%{#templeteStormControl_8021Q.disableRateLimit}"
																																value="%{#templeteStormControl_8021Q.interfaceType}" />
																														</td>
																														<td align="center"><s:checkbox
																																name="arrayAllTrafficType"
																																value="%{#templeteStormControl_8021Q.allTrafficType}"
																																id="arrayAllTrafficType_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeAllTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayBroadcast"
																																value="%{#templeteStormControl_8021Q.broadcast}"
																																id="arrayBroadcast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayUnknownUnicast"
																																value="%{#templeteStormControl_8021Q.unknownUnicast}"
																																id="arrayUnknownUnicast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayMulticast"
																																value="%{#templeteStormControl_8021Q.multicast}"
																																id="arrayMulticast_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:checkbox
																																name="arrayTcpsyn"
																																value="%{#templeteStormControl_8021Q.tcpsyn}"
																																id="arrayTcpsyn_%{#status.index}"
																																fieldValue="%{interfaceType}"
																																onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
																														<td align="center"><s:select
																																name="arrayRateLimitType"
																																value="%{#templeteStormControl_8021Q.rateLimitType}"
																																disabled="%{#templeteStormControl_8021Q.disableRateLimit}"
																																id="arrayRateLimitType_%{#status.index}"
																																list="%{list_stormLimitType}"
																																listKey="id" listValue="value"
																																cssStyle="display: %{dataSource.showStormLimitTypeBased}"
																																onchange="changeRateLimitType(%{#status.index},this.value);" />
																															<label
																															id="stormLimitTypePacketId_<s:property value="%{#status.index}"/>"
																															style="display: <s:property value="%{dataSource.showStormLimitTypePacket}"/>">PPS</label></td>
																														<td align="left"><s:textfield
																																name="arrayRateLimitValue"
																																value="%{#templeteStormControl_8021Q.rateLimitValue}"
																																disabled="%{#templeteStormControl_8021Q.disableRateLimit}"
																																id="arrayRateLimitValue_%{#status.index}"
																																cssStyle="width:70px;"
																																onkeypress="return hm.util.keyPressPermit(event,'ten');"
																																maxlength="%{#templeteStormControl_8021Q.rateLimitValueLength}" />
																															<label
																															id="scRateLimitRange_<s:property value='%{#status.index}'/>"><s:property
																																	value="%{#templeteStormControl_8021Q.rateLimitRange}" /></label></td>
																													</tr>
																												</s:if>
																											</s:iterator>
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
																	<!-- LLDP/CDP Settings -->
																	<%-- <tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0" width="100%">
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{showLLDPCDPSettingDiv}"/>"
																							id="showLLDPCDPSettinDiv">
																							<table cellspacing="0" cellpadding="0" border="0">
																								<tr>
																									<td class="labelT1"
																										onclick="showLLDPCDPSettingDiv(1);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																										alt="Show Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.switchSettings.lldp.settings" />
																									</td>
																								</tr>
																							</table>
																						</div>
																					</td>
																				</tr>
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{hideLLDPCDPSettingDiv}"/>"
																							id="hideLLDPCDPSettinDiv">
																							<table cellspacing="0" cellpadding="0" border="0"
																								width="100%">
																								<tr>
																									<td class="labelT1"
																										onclick="showLLDPCDPSettingDiv(2);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																										alt="Hide Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.switchSettings.lldp.settings" />
																									</td>
																								</tr>
																								<tr>
																									<td style="padding-left: 20px;">
																										<table cellspacing="0" cellpadding="0" border="0"  width="100%">
																											<tr>
																												<td style="padding: 5px 0 5px 10px;" class="noteInfo" colspan="2"><s:text
																													name="config.configTemplate.switchSettings.lldpcdp.note" /></td>
																											</tr>
																											<tr>
																												<td style="padding: 5px 0 5px 10px;" width="60%"><s:checkbox
																													name="dataSource.switchSettings.enableRunLLDPHostPorts"
																													value="%{dataSource.switchSettings.enableRunLLDPHostPorts}"></s:checkbox>
																													<span><s:text
																													name="config.configTemplate.switchSettings.lldp.hostports" /></span>
																												</td>
																												<td>
																													<s:checkbox
																													name="dataSource.switchSettings.enableRunLLDPNonHostPorts"
																													value="%{dataSource.switchSettings.enableRunLLDPNonHostPorts}"></s:checkbox>
																													<span><s:text
																													name="config.configTemplate.switchSettings.lldp.none.hostports" /></span>
																												</td>
																											</tr>
																											<tr>
																												<td style="padding: 5px 0 5px 10px;"><s:checkbox
																													name="dataSource.switchSettings.enableRunCDPHostPorts"
																													value="%{dataSource.switchSettings.enableRunCDPHostPorts}"></s:checkbox>
																													<span><s:text
																													name="config.configTemplate.switchSettings.cdp.hostports" /></span>
																												</td>
																												<td>
																													<s:checkbox
																													name="dataSource.switchSettings.enableRunCDPNonHostPorts"
																													value="%{dataSource.switchSettings.enableRunCDPNonHostPorts}"></s:checkbox>
																													<span><s:text
																													name="config.configTemplate.switchSettings.cdp.none.hostports" /></span>
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
																	</tr> --%>
																	<!-- IGMP Settings start -->
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0"
																				width="100%">
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{showIGMPSettingDiv}"/>"
																							id="showIGMPSettinDiv">
																							<table cellspacing="0" cellpadding="0" border="0">
																								<tr>
																									<td class="labelT1"
																										onclick="showIGMPSettingDiv(1);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																										alt="Show Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.switchSettings.igmp.settings" />
																									</td>
																								</tr>
																							</table>
																						</div>
																					</td>
																				</tr>
																				<tr>
																					<td>
																						<div
																							style="display:<s:property value="%{hideIGMPSettingDiv}"/>"
																							id="hideIGMPSettinDiv">
																							<table cellspacing="0" cellpadding="0" border="0"
																								width="100%">
																								<tr>
																									<td colspan="3" class="labelT1"
																										onclick="showIGMPSettingDiv(2);"
																										style="cursor: pointer"><img
																										src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																										alt="Hide Option" class="expandImg"
																										style="display: inline" />&nbsp;&nbsp;<s:text
																											name="config.switchSettings.igmp.settings" />
																									</td>
																								</tr>
																								<tr>
																									<td style="padding-left: 22px">
																										<table cellspacing="0" cellpadding="0"
																											border="0">
																											<tr>
																												<td><s:checkbox
																														onclick="enabledIgmpSnoopingCheckBox(this.checked);"
																														name="dataSource.switchSettings.enableIgmpSnooping"/> <s:text
																														name="config.switchSettings.igmp.snooping"></s:text>
																												</td>
																											</tr>
																											<tr id="show_igmp"
																												style="display:<s:property value="%{enabledIgmpMode}"/>">
																												<td style="padding-left: 22px">
																													<table width="100%" cellspacing="0"
																														cellpadding="0" border="0">
																														<tr>
																															<td width="200px" class="labelT1"><s:checkbox
																																	name="dataSource.switchSettings.enableImmediateLeave"/> <s:text
																																	name="config.switchSettings.igmp.immediate.leave"></s:text>
																															</td>
																															<td class="labelT1"><s:checkbox
																																	name="dataSource.switchSettings.enableReportSuppression"/> <s:text
																																	name="config.switchSettings.igmp.report.suppression"></s:text>
																															</td>
																														</tr>
																														<%-- <tr>
																													<td class="labelT1">
																														<s:text name="config.switchSettings.igmp.delay.leave.interval"></s:text>
																													</td>
																													<td class="labelT1">
																													<s:textfield name="dataSource.switchSettings.globalDelayLeaveQueryInterval" maxlength="2"
																														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																													<s:text name="config.switchSettings.igmp.delay.leave.interval.range"></s:text>
																													</td>
																												</tr>
																												<tr>
																													<td class="labelT1">
																														<s:text name="config.switchSettings.igmp.delay.leave.count"></s:text>
																													</td>
																													<td class="labelT1">
																													<s:textfield name="dataSource.switchSettings.globalDelayLeaveQueryCount" maxlength="1"
																														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																													<s:text name="config.switchSettings.igmp.delay.leave.count.range"></s:text>
																													</td>
																												</tr>
																												<tr>
																													<td class="labelT1">
																														<s:text name="config.switchSettings.igmp.router.aging.time"></s:text>
																													</td>
																													<td class="labelT1">
																													<s:textfield name="dataSource.switchSettings.globalRouterPortAginTime" maxlength="4"
																														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																													<s:text name="config.switchSettings.igmp.router.aging.time.range"></s:text>
																													</td>
																												</tr>
																												<tr>
																													<td class="labelT1">
																														<s:text name="config.switchSettings.igmp.robustness.count"></s:text>
																													</td>
																													<td class="labelT1">
																													<s:textfield name="dataSource.switchSettings.globalRobustnessCount" maxlength="1"
																														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																													<s:text name="config.switchSettings.igmp.robustness.count.range"></s:text>
																													</td>
																												</tr> --%>
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
																	<!-- LGMP Settings end -->
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
						</s:if>
						<%-- Network Settings --%>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{showNetworkSettingsDiv}"/>"
									id="showNetworkSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideNetworkSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.networkSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{hideNetworkSettingsDiv}"/>"
									id="hideNetworkSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideNetworkSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.networkSettings" /></td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 30px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.serviceFilterSettings" />
																</legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1"
																			style="width: 130px; padding-left: 13px;"><s:text
																				name="config.configTemplate.traffic.filter.device" /></td>
																		<td style="padding-left: 10px;"><s:select
																				name="deviceServiceId" list="%{list_service}"
																				listKey="id" listValue="value"
																				cssStyle="width: 180px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newDeviceServiceFilter')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editDeviceServiceFilter')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>
																	<tr>
																		<td colspan="2" class="noteInfo"
																			style="padding-left: 13px;"><s:text
																				name="config.configTemplate.traffic.filter.override.note" />
																		</td>
																	</tr>
																	<tr
																		style="display: <s:property value="%{hideTrafficFilter4IndividualAPs}"/>">
																		<td colspan="2">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td class="labelT1"><s:checkbox
																							name="dataSource.overrideTF4IndividualAPs"
																							value="%{dataSource.overrideTF4IndividualAPs}"
																							onclick="clickOverrideTF4IndividualAPs(this.checked);" />
																						<s:text
																							name="config.configTemplate.traffic.filter.override" />
																					</td>
																				</tr>
																				<tr id="trafficFilter4IndividualAPs"
																					style="display: <s:property value="%{TrafficFilter4IndividualAPsStyle}"/>">
																					<td style="padding-left: 30px;">
																						<table cellspacing="0" cellpadding="0" border="0">
																							<tr>
																								<th width="100px" align="left" nowrap="nowrap"
																									style="padding-right: 20px;"><s:text
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
																								<td align="left"><s:text
																										name="config.configTemplate.eth0" /></td>
																								<td align="left" nowrap="nowrap"><s:select
																										name="eth0ServiceId" list="%{list_service}"
																										listKey="id" listValue="value"
																										cssStyle="width: 180px;" /> <s:if
																										test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/new_disable.png" />"
																											width="16" height="16" alt="New" title="New" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('newEth0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/new.png" />"
																											width="16" height="16" alt="New" title="New" /></a>
																									</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/modify_disable.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('editEth0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/modify.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" /></a>
																									</s:else></td>
																								<td align="left"><s:select
																										name="eth0BackServiceId"
																										list="%{list_service}" listKey="id"
																										listValue="value" cssStyle="width: 180px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newBackEth0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editBackEth0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
																									</s:else>		
																								</td>
																							</tr>
																							<tr>
																								<td height="4px" />
																							</tr>
																							<tr>
																								<td align="left"><s:text
																										name="config.configTemplate.eth1" /></td>
																								<td align="left"><s:select
																										name="eth1ServiceId" list="%{list_service}"
																										listKey="id" listValue="value"
																										cssStyle="width: 180px;" /><s:if
																										test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/new_disable.png" />"
																											width="16" height="16" alt="New" title="New" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('newEth1ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/new.png" />"
																											width="16" height="16" alt="New" title="New" /></a>
																									</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/modify_disable.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('editEth1ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/modify.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" /></a>
																									</s:else></td>
																								<td align="left"><s:select
																										name="eth1BackServiceId"
																										list="%{list_service}" listKey="id"
																										listValue="value" cssStyle="width: 180px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newBackEth1ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editBackEth1ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
																									</s:else>			
																								</td>
																							</tr>
																							<tr>
																								<td height="4px" />
																							</tr>
																							<tr>
																								<td align="left"><s:text
																										name="config.configTemplate.red0" /></td>
																								<td align="left"><s:select
																										name="redServiceId" list="%{list_service}"
																										listKey="id" listValue="value"
																										cssStyle="width: 180px;" /><s:if
																										test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/new_disable.png" />"
																											width="16" height="16" alt="New" title="New" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('newRed0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/new.png" />"
																											width="16" height="16" alt="New" title="New" /></a>
																									</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/modify_disable.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('editRed0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/modify.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" /></a>
																									</s:else></td>
																								<td align="left"><s:select
																										name="red0BackServiceId"
																										list="%{list_service}" listKey="id"
																										listValue="value" cssStyle="width: 180px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newBackRed0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editBackRed0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
																									</s:else>	
																								</td>
																							</tr>
																							<tr>
																								<td height="4px" />
																							</tr>
																							<tr>
																								<td align="left"><s:text
																										name="config.configTemplate.agg0" /></td>
																								<td align="left"><s:select
																										name="aggServiceId" list="%{list_service}"
																										listKey="id" listValue="value"
																										cssStyle="width: 180px;" /><s:if
																										test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/new_disable.png" />"
																											width="16" height="16" alt="New" title="New" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('newAgg0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/new.png" />"
																											width="16" height="16" alt="New" title="New" /></a>
																									</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																											src="<s:url value="/images/modify_disable.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" />
																									</s:if> <s:else>
																										<a class="marginBtn"
																											href="javascript:submitAction('editAgg0ServiceFilter')"><img
																											class="dinl"
																											src="<s:url value="/images/modify.png" />"
																											width="16" height="16" alt="Modify"
																											title="Modify" /></a>
																									</s:else></td>
																								<td align="left"><s:select
																										name="agg0BackServiceId"
																										list="%{list_service}" listKey="id"
																										listValue="value" cssStyle="width: 180px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newBackAgg0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editBackAgg0ServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
																									</s:else>	
																								</td>
																							</tr>
																							<tr>
																								<td height="4px" />
																							</tr>
																							<tr>
																								<td align="left"><s:text
																										name="config.configTemplate.wirelessBackhaul" /></td>
																								<td />
																								<td align="left"><s:select
																										name="wireServiceId" list="%{list_service}"
																										listKey="id" listValue="value"
																										cssStyle="width: 180px;" />
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/new_disable.png" />"
																										width="16" height="16" alt="New" title="New" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('newBackWireServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/new.png" />"
																										width="16" height="16" alt="New" title="New" /></a>
																									</s:else>
																									<s:if test="%{writeDisabled == 'disabled'}">
																										<img class="dinl marginBtn"
																										src="<s:url value="/images/modify_disable.png" />"
																										width="16" height="16" alt="Modify" title="Modify" />
																									</s:if>
																									<s:else>
																										<a class="marginBtn" href="javascript:submitAction('editBackWireServiceFilter')"><img class="dinl"
																										src="<s:url value="/images/modify.png" />"
																										width="16" height="16" alt="Modify" title="Modify" /></a>
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
								<div
									style="display:<s:property value="%{showServiceSettingsDiv}"/>"
									id="showServiceSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideServiceSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.serviceSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{hideServiceSettingsDiv}"/>"
									id="hideServiceSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideServiceSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.serviceSettings" /></td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 30px;">
															<fieldset>
																<legend>
																	<s:text name="config.configTemplate.serviceSettings" />
																</legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.mgtOption" /></td>
																		<td width="200px"><s:select name="mgtOptionId"
																				list="%{list_mgtOption}" listKey="id"
																				headerKey="-1" headerValue=""
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newMgtOption')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editMgtOption')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.ipFilter" /></td>
																		<td width="200px"><s:select name="ipFilterId"
																				list="%{list_ipFilter}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newIpFilter')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editIpFilter')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>

																	<tr
																		style="display:<s:property value="%{hideDivSwitchOrRouterMode}"/>">
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.algConfig" /></td>
																		<td width="200px"><s:select name="algConfigId"
																				list="%{list_algConfig}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newAlgConfig')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editAlgConfig')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>

																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.accessConsole" /></td>
																		<td width="200px"><s:select
																				name="accessConsoleId" list="%{list_accessConsole}"
																				listKey="id" listValue="value"
																				cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newAccessConsole')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editAccessConsole')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>
																	<tr>
																		<td class="labelT1" width="130px"><s:text
																				name="hiveAp.discoveryProtocolLabel" /></td>
																		<td width="200px"><s:select name="lldpCdpId"
																				list="%{list_lldpCdp}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newLldpCdp')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editLldpCdp')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>

																		<td class="labelT1" width="130px"
																			style="display:<s:property value="%{hideDivSwitchOrRouterMode}"/>"><s:text
																				name="config.configTemplate.idsPolicy" /></td>
																		<td
																			style="padding-right: 5px; display:<s:property value="%{hideDivSwitchOrRouterMode}"/>"><s:select
																				name="idsPolicyId" list="%{list_idsPolicy}"
																				listKey="id" listValue="value"
																				cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newIdsPolicy')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editIdsPolicy')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>

																	<tr
																		style="display:<s:property value="%{showRouterRadiusSettingsDiv}"/>">
																		<td colspan="4" style="padding: 10px 10px 4px 8px;">
																			<fieldset>
																				<legend>
																					<s:text
																						name="config.configTemplate.iptrackWAN.title" />
																				</legend>
																				<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td height="4px"></td>
																					</tr>

																					<tr style="display: none">
																						<td><s:select id="ipTrackWANList_ID"
																								style="width:100%" size="6"
																								list="%{ipTrackWANList}" listKey="id"
																								listValue="value"></s:select></td>
																					</tr>



																					<tr>
																						<td class="labelT1" style="width: 110px">Primary</td>
																						<td style="width: 200px"><s:select
																								id="primaryIpTrackId" style="width:140px"
																								name="primaryIpTrackId"
																								list="%{ipTrackWANListWithBlank}" listKey="id"
																								listValue="value"></s:select> 
																								<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newRouterIpTrack_Primary')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editPrimaryTrackWan')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																					</tr>
																					<tr>
																						<td class="labelT1" style="width: 110px">Backup1</td>
																						<td style="width: 200px"><s:select
																								id="backup1TrackId" style="width:140px"
																								name="backup1TrackId"
																								list="%{ipTrackWANListWithBlank}" listKey="id"
																								listValue="value"></s:select>
																								 <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newRouterIpTrack_Backup1')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editBackup1TrackWan')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else>
																								</td>
																					</tr>
																					<tr>
																						<td class="labelT1" style="width: 110px">Backup2</td>
																						<td style="width: 200px"><s:select
																								id="backup2TrackId" style="width:140px"
																								name="backup2TrackId"
																								list="%{ipTrackWANListWithBlank}" listKey="id"
																								listValue="value"></s:select>
																								 <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newRouterIpTrack_Backup2')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editBackup2TrackWan')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else>
																								</td>
																					</tr>

																				</table>
																			</fieldset>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="4" style="padding: 10px 10px 4px 8px;">
																			<fieldset>
																				<legend>
																					<s:text name="config.configTemplate.iptrack.title" />
																				</legend>
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
																					<td style="padding: 0 2px 0 6px"><s:checkbox
																							name="dataSource.enableHttpServer"
																							value="%{dataSource.enableHttpServer}" /></td>
																					<td><s:text
																							name="config.configTemplate.enabledHttpServer" /></td>
																				</tr>
																				<tr>
																					<td style="padding: 0 2px 0 6px"><s:checkbox
																							name="dataSource.enableProbe"
																							value="%{dataSource.enableProbe}"
																							onclick="show_hideProbeDeatil(this.checked);" /></td>
																					<td><s:text
																							name="config.configTemplate.probe.enableText" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="4">
																			<div
																				style="display:<s:property value="%{hideProbeDetail}"/>"
																				id="hideProbeDetail">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.periodInterval" /></td>
																						<td colspan="2"><s:textfield
																								name="dataSource.probeInterval" size="20"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							&nbsp;<s:text
																								name="config.configTemplate.probe.periodInterval.range" /></td>
																					</tr>
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.retryCount" /></td>
																						<td colspan="2"><s:textfield
																								name="dataSource.probeRetryCount" size="20"
																								maxlength="2"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							&nbsp;<s:text
																								name="config.configTemplate.probe.retryCount.range" /></td>
																					</tr>
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.retryInterval" /></td>
																						<td colspan="2"><s:textfield
																								name="dataSource.probeRetryInterval" size="20"
																								maxlength="2"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							&nbsp;<s:text
																								name="config.configTemplate.probe.retryInterval.range" /></td>
																					</tr>
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.userName" /></td>
																						<td colspan="2"><s:textfield
																								name="dataSource.probeUsername" size="20"
																								maxlength="32"
																								onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																							&nbsp;<s:text
																								name="config.configTemplate.probe.userName.range" /></td>
																					</tr>
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.password" /></td>
																						<td><s:password
																								name="dataSource.probePassword"
																								showPassword="true" id="probePassword" size="20"
																								maxlength="64"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:textfield name="dataSource.probePassword"
																								cssStyle="display:none" disabled="true"
																								id="probePassword_text" size="20" maxlength="64"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																						<td>&nbsp;&nbsp;<s:text
																								name="config.configTemplate.probe.password.range" /></td>
																					</tr>
																					<tr>
																						<td width="160px" class="labelT1"
																							style="padding-left: 30px"><s:text
																								name="config.configTemplate.probe.confirmPassword" /></td>
																						<td><s:password name="confirmProbePassword"
																								id="confirmProbePassword"
																								value="%{dataSource.probePassword}" size="20"
																								showPassword="true" maxlength="64"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" />
																							<s:textfield name="confirmProbePassword"
																								id="confirmProbePassword_text"
																								value="%{dataSource.probePassword}" size="20"
																								maxlength="64" cssStyle="display:none"
																								disabled="true"
																								onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																						<td>
																							<table border="0" cellspacing="0" cellpadding="0">
																								<tr>
																									<td><s:checkbox id="chkToggleDisplay"
																											name="ignore" value="true"
																											disabled="%{writeDisable4Struts}"
																											onclick="hm.util.toggleObscurePassword(this.checked,['probePassword','confirmProbePassword'],['probePassword_text','confirmProbePassword_text']);" />
																									</td>
																									<td><s:text
																											name="admin.user.obscurePassword" /></td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</td>
																	</tr>
																	
																	<tr>
																		<td colspan="4">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding: 0 2px 0 6px"><s:checkbox
																								name="dataSource.enableDelayAlarm" id="enableDelayAlarm"
																								value="%{dataSource.enableDelayAlarm}"/>
																					</td>
																					<td>
																						<label for="enableDelayAlarm"><s:text name="guadalupe_01.enable.capwap.alarm"/></label>
																					</td>
																			    </tr>
																		     </table>
																		</td> 
																	</tr>      
																	<tr>
																		<td colspan="4">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td class="labelT1" width="200px"><s:text
																							name="config.configTemplate.radius.operator.name" />
																					</td>
																					<td><s:select name="radiusOptNameId"
																							list="%{list_radiusOptNames}" listKey="id"
																							listValue="value" cssStyle="width: 140px;" /> <s:if
																							test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newRadiusOptName')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />"
																								width="16" height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editRadiusOptName')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" /></a>
																						</s:else></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr style="display: <s:property value="%{supplementalCLIStyle}"/>">
																		<td colspan="4">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																				 	<td class="labelT1" width="200px"><s:text
																							name="hollywood_02.supp_cli_setting" />
																					</td>
																					<td><s:select name="supplementalCLIId"
																							list="%{list_cliBlob}" listKey="id"
																							listValue="value" cssStyle="width: 140px;" /> <s:if
																							test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newSuppCLIBlob')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />"
																								width="16" height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editSuppCLIBlob')"><img
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
								<div
									style="display:<s:property value="%{showServerSettingsDiv}"/>"
									id="showServerSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideServerSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.serverSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{hideServerSettingsDiv}"/>"
									id="hideServerSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideServerSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.serverSettings" /></td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 30px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.networkServerAssignment" />
																</legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.mgtSnmp" /></td>
																		<td style="padding-right: 5px;"><s:select
																				name="mgtSnmpId" list="%{list_mgtSnmp}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newMgtSnmp')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editMgtSnmp')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																		<td class="labelT1" width="120px"><s:text
																				name="config.configTemplate.mgtDns" /></td>
																		<td width="200px"><s:select name="mgtDnsId"
																				list="%{list_mgtDns}" listKey="id" listValue="value"
																				cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newMgtDns')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editMgtDns')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>
																	<tr>
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.mgtSyslog" /></td>
																		<td width="200px"><s:select name="mgtSyslogId"
																				list="%{list_mgtSyslog}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newMgtSyslog')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editMgtSyslog')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																		<td class="labelT1" width="120px"><s:text
																				name="config.configTemplate.mgtTime" /></td>
																		<td width="200px"><s:select name="mgtTimeId"
																				list="%{list_mgtTime}" listKey="id"
																				listValue="value" cssStyle="width: 140px;" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newMgtTime')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editMgtTime')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																	</tr>
																	<tr
																		style="display:<s:property value="%{hideLocationAndClientWatchDiv}"/>">
																		<td class="labelT1" width="130px"><s:text
																				name="config.configTemplate.locationServer" /></td>
																		<td width="200px"><s:select
																				name="locationServerId"
																				list="%{list_locationServer}" listKey="id"
																				listValue="value" cssStyle="width: 140px;"
																				onchange="changeLocationServer();" /> <s:if
																				test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/new_disable.png" />"
																					width="16" height="16" alt="New" title="New" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('newLocationServer')"><img
																					class="dinl"
																					src="<s:url value="/images/new.png" />" width="16"
																					height="16" alt="New" title="New" /></a>
																			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																					src="<s:url value="/images/modify_disable.png" />"
																					width="16" height="16" alt="Modify" title="Modify" />
																			</s:if> <s:else>
																				<a class="marginBtn"
																					href="javascript:submitAction('editLocationServer')"><img
																					class="dinl"
																					src="<s:url value="/images/modify.png" />"
																					width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else></td>
																		<td colspan="2">
																			<div
																				style="display:<s:property value="%{hideClientWatch}"/>"
																				id="hideClientWatchDiv">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="120px"><s:text
																								name="config.configTemplate.clientWatch" /></td>
																						<td width="200px"><s:select
																								name="clientWatchId" list="%{list_clientWatch}"
																								listKey="id" listValue="value"
																								cssStyle="width: 140px;" /> <s:if
																								test="%{writeDisabled == 'disabled'}">
																								<img class="dinl marginBtn"
																									src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																							</s:if> <s:else>
																								<a class="marginBtn"
																									href="javascript:submitAction('newClientWatch')"><img
																									class="dinl"
																									src="<s:url value="/images/new.png" />"
																									width="16" height="16" alt="New" title="New" /></a>
																							</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																								<img class="dinl marginBtn"
																									src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify"
																									title="Modify" />
																							</s:if> <s:else>
																								<a class="marginBtn"
																									href="javascript:submitAction('editClientWatch')"><img
																									class="dinl"
																									src="<s:url value="/images/modify.png" />"
																									width="16" height="16" alt="Modify"
																									title="Modify" /></a>
																							</s:else></td>
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
								<div style="display:<s:property value="%{showQosSettingsDiv}"/>"
									id="showQosSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideQosSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.qosSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="%{hideQosSettingsDiv}"/>"
									id="hideQosSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideQosSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.qosSettings" /></td>
										</tr>
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td style="padding: 4px 4px 4px 20px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.block.classification" />
																</legend>
																<table cellspacing="0" cellpadding="0" border="0"
																	class="embedded">
																	<tr>
																		<td height="5px"></td>
																	</tr>
																	<tr>
																		<td>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td style="padding: 0 0 0 2px" width="90px"><s:text
																							name="config.configTemplate.classifierMap" /></td>
																					<td><s:select name="classifierMapId"
																							list="%{list_classifierMap}" listKey="id"
																							listValue="value" cssStyle="width: 180px;" /></td>
																					<td><s:if
																							test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newClassifierMap')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />"
																								width="16" height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editClassifierMap')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" /></a>
																						</s:else></td>
																					<td style="padding: 0 10px 0 30px"><s:text
																							name="config.configTemplate.markerMap" /></td>
																					<td><s:select name="markerMapId"
																							list="%{list_markerMap}" listKey="id"
																							listValue="value" cssStyle="width: 180px;" /></td>
																					<td><s:if
																							test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/new_disable.png" />"
																								width="16" height="16" alt="New" title="New" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('newMarkerMap')"><img
																								class="dinl"
																								src="<s:url value="/images/new.png" />"
																								width="16" height="16" alt="New" title="New" /></a>
																						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																								src="<s:url value="/images/modify_disable.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" />
																						</s:if> <s:else>
																							<a class="marginBtn"
																								href="javascript:submitAction('editMarkerMap')"><img
																								class="dinl"
																								src="<s:url value="/images/modify.png" />"
																								width="16" height="16" alt="Modify"
																								title="Modify" /></a>
																						</s:else></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td height="4px"></td>
																	</tr>
																	<tr
																		style="display:<s:property value="%{hideDivSwitchModeOnly}"/>">
																		<td style="padding: 2px 0 4px 6px"><s:checkbox
																				name="dataSource.enabledMapOverride"
																				value="%{dataSource.enabledMapOverride}"
																				onclick="showOrHideMapPanel(this.checked);"></s:checkbox>
																			<s:text
																				name="config.configTemplate.enabledMapOverride"></s:text>
																		</td>
																	</tr>
																	<tr
																		style="display:<s:property value="%{hideDivSwitchModeOnly}"/>">
																		<td height="4px"></td>
																	</tr>
																	<tr id="uncheckedNoteForMapOverride"
																		style="display:<s:property value="%{showOverrideMapUnCheckedNote}"/>">
																		<td style="padding: 2px 0 4px 26px;" class="noteInfo"><s:text
																				name="config.configTemplate.enabledMapOverride.unchecked.note" /></td>
																	</tr>
																	<tr
																		style="display:<s:property value="%{hideDivSwitchModeOnly}"/>">
																		<td style="padding-left: 20px">
																			<div
																				style="display:<s:property value="%{hideOverrideMapPanel}"/>"
																				id="hideOverrideMapPanelDiv">
																				<table cellspacing="0" cellpadding="0" border="0">
																					<%-- <tr>
																				<s:if test="!oEMSystem">
																					<td style="padding-left: 10px" colspan="10">
																					<span><a href='javascript:void(0);' onclick='javascript: hm.util.toggleElementDisplay("infoForTypesAndDevice");'>?</a>&nbsp;</span>
																						<table id='infoForTypesAndDevice' style='display: none;'>
																							<tr><td colspan="2" style="color: blue;">Interface types and the platforms that support them:</td></tr>
																							<tr><td style="color: blue; width: 115px;">eth0</td><td style="color: blue;">All Aerohive devices</td></tr>
																							<tr><td style="color: blue; width: 115px;">eth1</td><td style="color: blue;">AP300 series devices and routers</td></tr>
																							<tr><td style="color: blue; width: 115px;">red0, agg0</td><td style="color: blue;">AP300 series devices</td></tr>
																							<tr><td style="color: blue; width: 115px;">eth2, eth3, eth4</td><td style="color: blue;">Routers</td></tr>
																						</table>
																					</td>
																				</s:if>
																			</tr> --%>
																					<tr>
																						<td style="padding: 2px 0 4px 6px;" colspan="10"><s:text
																								name="config.configTemplate.enabledMapOverride.checked.title" /></td>
																					</tr>
																					<tr>
																						<td style="padding: 2px 0 4px 6px;"
																							class="noteInfo" colspan="10"><s:text
																								name="config.configTemplate.enabledMapOverride.checked.note" /></td>
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
																								name="geneva_26.config.configTemplate.qos.services" /></td>
																						<td class="list"><s:text
																								name="config.configTemplate.qos.macOui" /></td>
																						<td class="list" width="50px"><s:text
																								name="config.configTemplate.qos.ssid" /></td>
																						<td class="list"><s:text
																								name="config.configTemplate.qos.11e" />/<s:text
																								name="config.configTemplate.qos.11p" /></td>
																						<td class="list" style="padding-right: 15px"><s:text
																								name="config.configTemplate.qos.diff" /></td>
																						<td class="list" width="15px"
																							style="border-left: 1px solid #999999">&nbsp;</td>
																						<td class="list"><s:text
																								name="config.configTemplate.qos.11e" />/<s:text
																								name="config.configTemplate.qos.11p" /></td>
																						<td class="list"><s:text
																								name="config.configTemplate.qos.diff" /></td>
																					</tr>
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid">
																						<s:if test="%{interfaceName=='eth0'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth0');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 10px;"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid.disabledField}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"></s:checkbox>
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
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid3">
																						<s:if test="%{interfaceName=='eth1'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid3.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth1');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid3.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid3.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid3.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid3.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid3.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid3.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid3.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid3.disabledField}"></s:checkbox>
																								</td>
																								<!--<s:if test="!oEMSystem">
																						<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
																						</s:if>
																						-->
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
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid6">
																						<s:if test="%{interfaceName=='eth2'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid6.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth2');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid6.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid6.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid6.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid6.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid6.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid6.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid6.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid6.disabledField}"></s:checkbox>
																								</td>
																							</tr>
																						</s:if>
																					</s:iterator>
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid7">
																						<s:if test="%{interfaceName=='eth3'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid7.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth3');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid7.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid7.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid7.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid7.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid7.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid7.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid7.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid7.disabledField}"></s:checkbox>
																								</td>
																							</tr>
																						</s:if>
																					</s:iterator>
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid8">
																						<s:if test="%{interfaceName=='eth4'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid8.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth4');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid8.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid8.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid8.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid8.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid8.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid8.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid8.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid8.disabledField}"></s:checkbox>
																								</td>
																							</tr>
																						</s:if>
																					</s:iterator>
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid4">
																						<s:if test="%{interfaceName=='red0'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid4.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'red0');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid4.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid4.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid4.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid4.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid4.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid4.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid4.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid4.disabledField}"></s:checkbox>
																								</td>
																								<!--<s:if test="!oEMSystem">
																						<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
																						</s:if>
																						-->
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
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid5">
																						<s:if test="%{interfaceName=='agg0'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid5.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'agg0');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid5.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid5.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid5.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckP"
																										value="%{#templateSsid5.CheckP}"
																										id="arrayCheckP_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid5.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckPT"
																										value="%{#templateSsid5.CheckPT}"
																										id="arrayCheckPT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid5.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
																										disabled="%{#templateSsid5.disabledField}"></s:checkbox>
																								</td>
																								<!--<s:if test="!oEMSystem">
																						<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
																						</s:if>
																						-->
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
																					<s:iterator
																						value="%{dataSource.ssidInterfacesTreeMap.values}"
																						status="status" id="templateSsid2">
																						<s:if
																							test="%{interfaceName!='eth0' && interfaceName!='eth1' && interfaceName!='red0' && interfaceName!='agg0' && interfaceName!='eth2' && interfaceName!='eth3' && interfaceName!='eth4'}">
																							<tr>
																								<td valign="top" class="list"><s:property
																										value="%{interfaceName}" /></td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsidOnly"
																										value="%{#templateSsid2.ssidOnlyEnabled}"
																										id="arraySsidOnly_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'ssid');"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayNetwork"
																										value="%{#templateSsid2.networkServicesEnabled}"
																										id="arrayNetwork_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayMacOui"
																										value="%{#templateSsid2.macOuisEnabled}"
																										id="arrayMacOui_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arraySsid"
																										value="%{#templateSsid2.ssidEnabled}"
																										id="arraySsid_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckE"
																										value="%{#templateSsid2.CheckE}"
																										id="arrayCheckE_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckE',%{#status.index}, false);"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"
																									style="padding-right: 15px"><s:checkbox
																										name="arrayCheckD"
																										value="%{#templateSsid2.checkD}"
																										id="arrayCheckD_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, false);"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td class="list" width="15px"
																									style="border-left: 1px solid #999999">&nbsp;</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckET"
																										value="%{#templateSsid2.CheckET}"
																										id="arrayCheckET_%{#status.index}"
																										fieldValue="%{interfaceName}"
																										onclick="changeCheckboxValue('arrayCheckET',%{#status.index}, false);"
																										disabled="%{#templateSsid2.disabledField}"></s:checkbox>
																								</td>
																								<td valign="top" class="list"><s:checkbox
																										name="arrayCheckDT"
																										value="%{#templateSsid2.checkDT}"
																										id="arrayCheckDT_%{#status.index}"
																										fieldValue="%{interfaceName}"
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
													<tr
														style="display:<s:property value="%{hideQosScheduleAndAirtimePanel}"/>">
														<td style="padding-left: 30px;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td colspan="4" style="padding-top: 5px;" align="left"
																		class="noteInfo"><s:text
																			name="config.configTemplate.airtime.note" /></td>
																</tr>
																<tr>
																	<td width="25px"><s:checkbox
																			name="dataSource.enableAirTime" /></td>
																	<td width="310px"><s:text
																			name="config.configTemplate.enableAirTime" /></td>
																	<td class="labelT1" width="140px"><s:text
																			name="config.userprofile.sla.interval" /></td>
																	<td><s:textfield name="dataSource.slaInterval"
																			size="24" maxlength="4"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																		<s:text name="config.userprofile.sla.interval.range" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr
														style="display:<s:property value="%{showVoIPSetting}"/>">
														<td style="padding: 4px 4px 4px 20px;">
															<fieldset>
																<legend>
																	<s:text
																		name="config.configTemplate.voip.bandwidthlimitingsettings" />
																</legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td style="padding-top: 5px;" colspan="5" align="left"
																			class="noteInfo"><s:text
																				name="config.configTemplate.voip.note" /></td>
																	</tr>
																	<tr>
																		<th width="100px"><s:text
																				name="config.configTemplate.voip.waninterface" /></th>
																		<th colspan="2" width="300px">
																			<div align="left">
																				<span style="padding-left: 10px;"><s:text
																						name="config.configTemplate.voip.enablelimitbandwidth.download.title" /></span>
																			</div>
																			<div align="left">
																				<span style="padding-left: 15px;"><s:text
																						name="config.configTemplate.voip.enable" /></span> <span
																					style="padding-left: 32px;"><s:text
																						name="config.configTemplate.voip.maxrate" /></span>
																			</div>
																		</th>
																		<th colspan="2" width="300px">
																			<div align="left">
																				<span style="padding-left: 10px;"><s:text
																						name="config.configTemplate.voip.enablelimitbandwidth.upload.title" /></span>
																			</div>
																			<div align="left">
																				<span style="padding-left: 15px;"><s:text
																						name="config.configTemplate.voip.enable" /></span> <span
																					style="padding-left: 32px;"><s:text
																						name="config.configTemplate.voip.maxrate" /></span>
																			</div>
																		</th>
																	</tr>
																	<tr>
																		<td class="list" style="width: 100px;"><s:text
																				name="config.configTemplate.voip.waninterface.eth0" /></td>
																		<td class="list" align="right" style="width: 30px;">
																			<s:checkbox
																				name="dataSource.enableEth0LimitDownloadBandwidth"
																				onclick="enabledVoipCheckBox(this.checked,'eth0LimitDownloadRate')" />
																		</td>
																		<td class="list" align="left"
																			style="width: 150px; padding-left: 44px;"><s:textfield
																				name="dataSource.eth0LimitDownloadRate" size="10"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				maxlength="5"
																				readonly="%{enableEth0LimitDownloadBandwidth}" /> <s:text
																				name="config.configTemplate.voip.enablelimitbandwidth.range" />
																		</td>
																		<td class="list" align="right" style="width: 30px;">
																			<s:checkbox
																				name="dataSource.enableEth0LimitUploadBandwidth"
																				onclick="enabledVoipCheckBox(this.checked,'eth0LimitUploadRate')" />
																		</td>
																		<td class="list" align="left"
																			style="width: 150px; padding-left: 42px;"><s:textfield
																				name="dataSource.eth0LimitUploadRate" size="10"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				maxlength="5"
																				readonly="%{enableEth0LimitUploadBandwidth}" /> <s:text
																				name="config.configTemplate.voip.enablelimitbandwidth.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="list"><s:text
																				name="config.configTemplate.voip.waninterface.usbmodem" /></td>
																		<td class="list" align="right" style="width: 30px;">
																			<s:checkbox
																				name="dataSource.enableUSBLimitDownloadBandwidth"
																				onclick="enabledVoipCheckBox(this.checked,'usbLimitDownloadRate')" />
																		</td>
																		<td class="list" align="left"
																			style="width: 150px; padding-left: 44px;"><s:textfield
																				name="dataSource.usbLimitDownloadRate" size="10"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				maxlength="5"
																				readonly="%{enableUSBLimitDownloadBandwidth}" /> <s:text
																				name="config.configTemplate.voip.enablelimitbandwidth.range" />
																		</td>
																		<td class="list" align="right" style="width: 30px;">
																			<s:checkbox
																				name="dataSource.enableUSBLimitUploadBandwidth"
																				onclick="enabledVoipCheckBox(this.checked,'usbLimitUploadRate')" />
																		</td>
																		<td class="list" align="left"
																			style="width: 150px; padding-left: 42px;"><s:textfield
																				name="dataSource.usbLimitUploadRate" size="10"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				maxlength="5"
																				readonly="%{enableUSBLimitUploadBandwidth}" /> <s:text
																				name="config.configTemplate.voip.enablelimitbandwidth.range" />
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr
														style="display:<s:property value="%{hideQosScheduleAndAirtimePanel}"/>">
														<td style="padding: 4px 4px 4px 20px;"><s:if
																test="%{listQosRateLimit.size > 0}">
																<fieldset>
																	<legend>
																		<s:text name="config.configTemplate.block.policing" />
																	</legend>
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td style="padding-top: 5px;" colspan="4"
																				align="left" class="noteInfo"><s:text
																					name="config.configTemplate.policyRateLimit.note" /></td>
																		</tr>
																		<tr>
																			<s:if test="%{radioModeASize}">
																				<td valign="top">
																					<table cellspacing="0" cellpadding="0" border="0"
																						class="embedded">
																						<tr>
																							<td nowrap="nowrap"
																								style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
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
																									name="config.configTemplate.model.rate" /><br>802.11ac</th>
																							<th align="center"><s:text
																									name="config.configTemplate.model.weight" /></th>
																							<th align="center"><s:text
																									name="config.configTemplate.model.weightPercent" /></th>
																						</tr>
																						<s:iterator value="%{listQosRateLimit}"
																							status="status" id="templateAModel">
																							<s:if test="%{#templateAModel.radioMode == 2}">
																								<tr>
																									<td valign="top" class="list"><span
																										title='<s:property value="%{userProfile.userProfileName}" />'><s:property
																												value="%{userProfile.userProfileNameSubstr}" /></span></td>
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
																				<td valign="top" style="padding-left: 10px;" />
																			</s:if>
																			<s:if test="%{radioModeBGSize}">
																				<td valign="top">
																					<table cellspacing="0" cellpadding="0" border="0"
																						class="embedded">
																						<tr>
																							<td nowrap="nowrap"
																								style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
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
																									<td valign="top" class="list"><span
																										title='<s:property value="%{userProfile.userProfileName}" />'><s:property
																												value="%{userProfile.userProfileNameSubstr}" /></span></td>
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
															</s:if></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<%-- report Settings --%>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{showReportSettingsDiv}"/>"
									id="showReportSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideReportSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.reportSettings" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div
									style="display:<s:property value="%{hideReportSettingsDiv}"/>"
									id="hideReportSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideReportSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.reportSettings" /></td>
										</tr>
										<tr>
											<td style="padding-left: 25px">
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<!--  -->
													<tr style="display: none">
														<td style="padding: 2px 0 4px 6px" class="labelT1">
															<!--<s:text name="config.configTemplate.reportSettings.enabledCollectionTitle"></s:text>-->
															&nbsp;&nbsp;Application Profile &nbsp;&nbsp;<select
															id="appProfileId" name="appProfileId"
															style="width: 140px;">
																<option value="0"></option>
																<s:iterator value="appProfileList" status="status">
																	<option value="<s:property value='id'/>">
																		<s:property value="profileName" />
																	</option>
																</s:iterator>
														</select> <a href="javascript:submitAction('newAppProfile')"
															class="marginBtn"><img width="16" height="16"
																title="New" alt="New" src="/hm/images/new.png"
																class="dinl"></a> <a
															href="javascript:submitAction('editAppProfile')"
															class="marginBtn"><img width="16" height="16"
																title="Modify" alt="Modify" src="/hm/images/modify.png"
																class="dinl"></a>
														</td>
													</tr>
													<!--  -->
													<tr>
														<td style="padding: 2px 0 4px 6px" class="labelT1"><s:checkbox
																name="dataSource.enableReportCollection"
																onclick="enabledChkReportCollection(this.checked);" />
															<s:text
																name="config.configTemplate.reportSettings.enabledCollectionTitle"></s:text>
														</td>
													</tr>
													<tr style="display:<s:property value="%{hideReportSettingsDetailDiv}"/>"
																id="hideReportSettingsDetailDiv">
														<td style="padding: 2px 0 4px 40px">
														    <table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td width="322px" style="padding-left: 6px"
																			class="labelT1"><s:text
																				name="config.configTemplate.reportSettings.reportCollectionTitle"></s:text></td>
																		<td><s:select name="dataSource.collectionInterval"
																		list="%{reportCollectionIntervalList}" listKey="key"
																		listValue="value" cssStyle="width: 120px;" /></td>
																	</tr>
																</table>
																<table border="0" cellspacing="0" cellpadding="0"
																	width="100%">
																	<tr>
																		<td colspan="2" style="padding-left: 6px">
																			<fieldset>
																				<legend>
																					<s:text
																						name="config.configTemplate.reportSettings.ifBlockTitle" />
																				</legend>
																				<table border="0" cellspacing="0" cellpadding="0"
																					width="100%">
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.ifCrc"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionIfCrc" size="15"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.ifTxDrop"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionIfTxDrop" size="15"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.ifRxDrop"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionIfRxDrop" size="15"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.ifTxRetry"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionIfTxRetry" size="15"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.ifAirtime"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionIfAirtime" size="15"
																								maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																				</table>
																			</fieldset>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="2" style="padding: 4px 2px 4px 6px">
																			<fieldset>
																				<legend>
																					<s:text
																						name="config.configTemplate.reportSettings.clientBlockTitle" />
																				</legend>
																				<table border="0" cellspacing="0" cellpadding="0"
																					width="100%">
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.clientTxDrop"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionClientTxDrop"
																								size="15" maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.clientRxDrop"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionClientRxDrop"
																								size="15" maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.clientTxRetry"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionClientTxRetry"
																								size="15" maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																					<tr>
																						<td width="300px" class="labelT1"><s:text
																								name="config.configTemplate.reportSettings.clientAirtime"></s:text></td>
																						<td><s:textfield
																								name="dataSource.collectionClientAirtime"
																								size="15" maxlength="3"
																								onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;%&nbsp;<s:text
																								name="config.configTemplate.reportSettings.ifCrc.range" /></td>
																					</tr>
																				</table>
																			</fieldset>
																		</td>
																	</tr>
																</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1" style="padding: 2px 0 4px 6px"><s:checkbox
																name="dataSource.enableKddr" value="%{dataSource.enableKddr}"/>
															<s:text
																name="config.configTemplate.enableKddr.enabledCheckBoxTitle"></s:text>
														</td>
													</tr>
													<tr>
													     <td class="noteInfo" style="padding-left: 8px">
														    <s:text name="config.configTemplate.enableKddr.note"></s:text>
														</td>
													</tr>
													<!--connection alarm setting start-->
													<!--
												<tr>
													<td style="padding: 2px 0 4px 6px" class="labelT1">
														<s:checkbox name="dataSource.enableConnectionAlarm" onclick="enabledChkConnectionAlarm(this.checked);"/> 
														<s:text name="config.configTemplate.connectionAlarm.enabledCheckBoxTitle"></s:text>
													</td>
												</tr>
												<tr>
													<td style="padding: 2px 0 4px 40px">
									                <div id="connectionAlarmDetailDiv" <s:if test="%{dataSource.enableConnectionAlarm == true}">style="display:"</s:if><s:else>style="display:none"</s:else> >
															<table border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																	<td colspan="2" style="padding-left: 6px">
																		<fieldset><legend><s:text name="config.configTemplate.connectionAlarm.frameTitle" /></legend>
																			<table border="0" cellspacing="0" cellpadding="0" width="100%">
																				<tr>
																					<td width="300px" class="labelT1">Tx Retry Threshold</td>
																					<td>
																					<input type="text" id="dataSource.txRetryThreshold" name="dataSource.txRetryThreshold" value="<s:property value='dataSource.txRetryThreshold'/>" 
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"> %&nbsp; (1-100)
																					 </td>
																				</tr>
																				<tr>
																					<td width="300px" class="labelT1">Tx Frame Error Threshold</td>
																					<td><input type="text" id="dataSource.txFrameErrorThreshold" name="dataSource.txFrameErrorThreshold" value="<s:property value='dataSource.txFrameErrorThreshold'/>"
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');">%&nbsp; (1-100)
																					 </td>
																				</tr>
																				<tr>
																					<td width="300px" class="labelT1">Prob Request Threshold</td>
																					<td><input type="text" id="dataSource.probRequestThreshold" name="dataSource.probRequestThreshold" value="<s:property value='dataSource.probRequestThreshold'/>"
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp; (0-20)
																					 </td>
																					</tr>
																				<tr>
																					<td width="300px" class="labelT1">Egress Multicast Threshold</td>
																					<td><input type="text" id="dataSource.egressMulticastThreshold" name="dataSource.egressMulticastThreshold" value="<s:property value='dataSource.egressMulticastThreshold'/>"
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp; (0-10000)
																					 </td>
														
																				</tr>
																				<tr>
																					<td width="300px" class="labelT1">Ingress Multicast Threshold</td>
																					<td><input type="text" id="dataSource.ingressMulticastThreshold" name="dataSource.ingressMulticastThreshold" value="<s:property value='dataSource.ingressMulticastThreshold'/>"
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp; (0-10000)
																					 </td>
															
																				</tr>
																				<tr>
																					<td width="300px" class="labelT1">Channel Utilization Threshold</td>
																					<td><input type="text" id="dataSource.channelUtilizationThreshold" name="dataSource.channelUtilizationThreshold" value="<s:property value='dataSource.channelUtilizationThreshold'/>"
																					    size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>%&nbsp; (1-100)
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
												-->
													<!--connection alarm setting end-->
													
												<tr>
													<td style="padding: 8px 0 4px 6px" class="labelT1">
														<!--<s:checkbox name="dataSource.enableL7Switch" />--> 
														<!--<s:text name="config.configTemplate.enableL7Switch.enabledCheckBoxTitle"></s:text>-->
														<s:text name="config.configTemplate.enableL7Switch.enabledCheckBoxTitle"></s:text>
															<input type="radio" name="dataSource.enableL7Switch" id="enableL7" value="true" <s:if test="%{dataSource.enableL7Switch == true}">checked</s:if>><label for="enableL7">On</label>
															<input type="radio" name="dataSource.enableL7Switch" id="disableL7" value="false" <s:if test="%{dataSource.enableL7Switch == false}">checked</s:if>><label for="disableL7">Off</label>
													</td>
												</tr>
												<tr>
													<td class="noteInfo" style="padding-left: 8px">
														<s:text name="config.configTemplate.enableL7Switch.note"></s:text>
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
								<div style="display:<s:property value="%{showTVSettingsDiv}"/>"
									id="showTVSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideTVSettingsDiv(1);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
												alt="Show Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.tvsetting.title" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="%{hideTVSettingsDiv}"/>"
									id="hideTVSettingsDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" onclick="showHideTVSettingsDiv(2);"
												style="cursor: pointer"><img
												src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;&nbsp;<s:text
													name="config.configTemplate.tvsetting.title" /></td>
										</tr>
										<tr>
											<td style="padding-left: 25px">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding: 2px 0 4px 6px" class="labelT1"><s:checkbox
																name="dataSource.enableOSDURL"
																onclick="enabledTvCheckBox(this.checked);" /> <s:text
																name="config.configTemplate.tvsetting.enableOSDURL"></s:text>
														</td>

													</tr>
													<tr id="tvEnableTr"
														style="display:<s:property value="%{hideTVCheckBoxOption}"/>">
														<td style="padding: 2px 0 4px 21px" class="labelT1">
															<s:checkbox name="dataSource.enableTVService"
																onclick="enabledTvSettingSelect(this.checked);" /> <s:text
																name="config.configTemplate.tvsetting.enableTvService"></s:text>
														</td>
													</tr>
													<tr id="tvOptionDiv"
														style="display:<s:property value="%{hideTVSelectOption}"/>">
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
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script>
function initAppProfileData(){
	document.getElementById('appProfileId').value='<s:property value="dataSource.appProfile.id"/>';	
}
window.setTimeout("initAppProfileData()", 100);
</script>
<script>
function newHiveProfile() {
	hideSubDialogOverlay();
	var url = "<s:url action='hiveProfiles' includeParams='none' />?operation=new&parentDomID=np_hiveId&jsonMode=true&contentShowType=dlg&ignore="+new Date().getTime();
	openIFrameDialog(800, 450, url);
}
function editHiveProfile() {
	hideSubDialogOverlay();
	var url = "<s:url action='hiveProfiles' includeParams='none' />?operation=edit&id="+Get("np_hiveId").value+"&parentDomID=np_hiveId&jsonMode=true&contentShowType=dlg&ignore="+new Date().getTime();
	openIFrameDialog(800, 450, url);
}

	

	

	

	

	

	

	

	

	

	
</script>
