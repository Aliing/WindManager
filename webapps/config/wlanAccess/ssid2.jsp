<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@page import="com.ah.bo.wlan.TX11aOr11gRateSetting"%>
<%@page import="com.ah.bo.wlan.Tx11acRateSettings"%>
<s:if test="%{jsonMode==false}">
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />"></script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/jquery-ui.css" includeParams="none"/>"></link>
</s:if>

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />

<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script>
var KEY_MGMT_OPEN = <%=SsidProfile.KEY_MGMT_OPEN%>;
var KEY_MGMT_WPA2_EAP_802_1_X = <%=SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X%>;
var KEY_MGMT_WPA2_PSK = <%=SsidProfile.KEY_MGMT_WPA2_PSK%>;
var KEY_MGMT_WPA_EAP_802_1_X = <%=SsidProfile.KEY_MGMT_WPA_EAP_802_1_X%>;
var KEY_MGMT_WPA_PSK = <%=SsidProfile.KEY_MGMT_WPA_PSK%>;
var KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X = <%=SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X%>;
var KEY_MGMT_AUTO_WPA_OR_WPA2_PSK = <%=SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK%>;
var KEY_MGMT_WEP_PSK = <%=SsidProfile.KEY_MGMT_WEP_PSK%>;
var KEY_MGMT_DYNAMIC_WEP = <%=SsidProfile.KEY_MGMT_DYNAMIC_WEP%>;
var SSID_RATE_SET_BASIC = <%=TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC%>;
var SSID_RATE_SET_NEITHER = <%=TX11aOr11gRateSetting.RATE_SET_TYPE_NEI%>;
var ACCESS_MODE_PSK = <%=SsidProfile.ACCESS_MODE_PSK%>;
var isHHMApplication   = <s:property value="%{blnHHMApplication}"/>;
<s:if test="%{allowedTrial}">
var idmCustomerUrl = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=completeCustomer";    
var createIDMCustomerUrl = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=createIDMCustomer";    
var trialSettingsUrl = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=trialSettings" + "&ignore="+new Date().getTime();
</s:if>
var formName = 'ssidProfilesFull';

var STREAM_SINGLE_RATE_MCS_7 = "<%=Tx11acRateSettings.STREAM_SINGLE_RATE_MCS_7%>";
var STREAM_SINGLE_RATE_MCS_8 = "<%=Tx11acRateSettings.STREAM_SINGLE_RATE_MCS_8%>";
var STREAM_SINGLE_RATE_MCS_9 = "<%=Tx11acRateSettings.STREAM_SINGLE_RATE_MCS_9%>";

var STREAM_TWO_RATE_MCS_7 = "<%=Tx11acRateSettings.STREAM_TWO_RATE_MCS_7%>";
var STREAM_TWO_RATE_MCS_8 = "<%=Tx11acRateSettings.STREAM_TWO_RATE_MCS_8%>";
var STREAM_TWO_RATE_MCS_9 = "<%=Tx11acRateSettings.STREAM_TWO_RATE_MCS_9%>";

var STREAM_THREE_RATE_MCS_7 = "<%=Tx11acRateSettings.STREAM_THREE_RATE_MCS_7%>";
var STREAM_THREE_RATE_MCS_8 = "<%=Tx11acRateSettings.STREAM_THREE_RATE_MCS_8%>";
var STREAM_THREE_RATE_MCS_9 = "<%=Tx11acRateSettings.STREAM_THREE_RATE_MCS_9%>";

var VHT_STREAM_SINGLE_RATE_MCS_7 = "<%=Tx11acRateSettings.VHT_STREAM_SINGLE_RATE_MCS_7%>";
var VHT_STREAM_SINGLE_RATE_MCS_8 = "<%=Tx11acRateSettings.VHT_STREAM_SINGLE_RATE_MCS_8%>";
var VHT_STREAM_SINGLE_RATE_MCS_9 = "<%=Tx11acRateSettings.VHT_STREAM_SINGLE_RATE_MCS_9%>";

var VHT_STREAM_TWO_RATE_MCS_7 = "<%=Tx11acRateSettings.VHT_STREAM_TWO_RATE_MCS_7%>";
var VHT_STREAM_TWO_RATE_MCS_8 = "<%=Tx11acRateSettings.VHT_STREAM_TWO_RATE_MCS_8%>";
var VHT_STREAM_TWO_RATE_MCS_9 = "<%=Tx11acRateSettings.VHT_STREAM_TWO_RATE_MCS_9%>";

var VHT_STREAM_THREE_RATE_MCS_7 = "<%=Tx11acRateSettings.VHT_STREAM_THREE_RATE_MCS_7%>";
var VHT_STREAM_THREE_RATE_MCS_8 = "<%=Tx11acRateSettings.VHT_STREAM_THREE_RATE_MCS_8%>";
var VHT_STREAM_THREE_RATE_MCS_9 = "<%=Tx11acRateSettings.VHT_STREAM_THREE_RATE_MCS_9%>";

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	if (tabId < 1) {
		if (document.getElementById(formName + "_dataSource_ssidName").disabled == false) {
			document.getElementById(formName + "_dataSource_ssidName").focus();
		}
	}
	if (document.getElementById(formName + "_enabledRekeyPeriod2").checked==false){
		document.getElementById("rekeyPeriod2").readOnly=true;
	}
	if (document.getElementById(formName + "_enabledRekeyPeriodGMK").checked==false){
		document.getElementById("rekeyPeriodGMK").readOnly=true;
	}
	if (document.getElementById(formName + "_enabledRekeyPeriodPTK").checked==false){
		document.getElementById("rekeyPeriodPTK").readOnly=true;
	}
	if (document.getElementById(formName + "_enabledReauthInterval").checked==false){
		document.getElementById("reauthInterval").readOnly=true;
	}
	if (document.getElementById(formName + "_enabledPskUserLimit").checked==false){
		document.getElementById("pskUserLimit").readOnly=true;
	}

	setSecurityExplainContext(<s:property value="%{accessSecurity}"/>);
	
	setCwpTitle();
	
	//Check the 802.11r should enabled or not
	wpa2Check();
	
	convtounicastClick(document.getElementsByName("dataSource.convtounicast")[0]);
    
    if(<s:property value="%{enabledIDM}"/>) {
    	if(Get("enableIDMChk")) {
	    	enabledCloudAuth(true);
    	}
    }
    
    if(<s:property value="%{dataSource.enableMDM}"/>){
		document.getElementById(formName + "_dataSource_enableMDM").checked = true;	
	}else{
		document.getElementById(formName + "_dataSource_enableMDM").checked = false;	
		Get("enablemdmselect").style.display="none";
	}
	/*     
    Some process when page onloaded, 
    such as Cwp should be unable if provisioning onboard enabled when page loading
*/
//add to handle singe ssid
	if($("#enableProvisionPrivate").attr("checked") == "checked"
			&& $("#enableSingleSSID").attr("checked") == "checked"){
				$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",true);
	}
	if($("#enableProvisionPrivate").attr("checked") == "checked"
		&& $("#enableProvisionPrivate").attr("checked") == "checked"){
				$("#enableSingleSSIDDiv").show(500);
	}
    if(($("#enableProvisionEnterprise").attr("checked") == "checked")
    		|| ($("#enableProvisionPersonal").attr("checked") == "checked")
    		|| ($("#enableProvisionPrivate").attr("checked") == "checked")){
    	$("#enablemdmselect").css("display",false);
    	 if(<s:property value="%{usabledIDM}"/> && $("#enableProvisionPrivate").attr("checked") !== "checked") {
    		 $("#enableIDMChk").attr("disabled",true);
    	   }
    	$("#ssidProfilesFull_dataSource_enableMDM").attr("checked",false);
    	$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",true);
    	$("#enableCwpSelect").attr("checked",false);
    	$("#enableCwpSelect").attr("disabled",true);
    	$("#ssidProfilesFull_configmdmId").prepend("<option value='-1' selected='selected'>None available</option>");
    }
    if(<s:property value="%{usabledIDM}"/>) {
    	if(Get("enableIDMChk").checked){
    		$("#enableProvisionEnterprise").attr("checked",false);
    		$("#enableProvisionPersonal").attr("checked",false);
    		//$("#enableProvisionPrivate").attr("checked",false);
    		$("#enableProvisionEnterprise").attr("disabled",true);
    		$("#enableProvisionPersonal").attr("disabled",true);
    		//$("#enableProvisionPrivate").attr("disabled",true);
    		$("#provisionSsidDiv").hide();
    		}
	   }
    if($("#enableCwpSelect").attr("checked") == "checked"){
    	$("#enableProvisionEnterprise").attr("checked",false);
		$("#enableProvisionPersonal").attr("checked",false);
		$("#enableProvisionPrivate").attr("checked",false);
		$("#enableProvisionEnterprise").attr("disabled",true);
		$("#enableProvisionPersonal").attr("disabled",true);
		$("#enableProvisionPrivate").attr("disabled",true);
		$("#provisionSsidDiv").hide();
    }
    <s:if test="%{allowedTrial}">
    initTrialLink();
    new YAHOO.widget.Tooltip('explaination', {context: 'idmexplaination', width: "350px", container: 'ssidProfilesFull'});
    </s:if>
     initStreamSlider();
     
    changeCloudCwpAuthValue(<s:property value="dataSource.enabledSocialLogin"/>);
}
//add to handle view client profile link
function handleViewClientProfile(ele,checked){
	if(checked){
		$(ele).siblings("a").show();
	}else{
		$(ele).siblings("a").hide();
	}
}
/* 
validateBeforeSaving was designed to make sure that 
no rest data in the former one still stay in the 
next one when accesstype have changed
 */
function validateBeforeSaving(){
	var accessMode1 = $("#accessMode1").attr("checked");
	var accessMode2 = $("#accessMode2").attr("checked");
	var accessMode3 = $("#accessMode3").attr("checked");
	var accessMode4 = $("#accessMode4").attr("checked");
	var accessMode5 = $("#accessMode5").attr("checked");
	if(accessMode1){
		if($("#enableProvisionPersonal").attr("checked") == "checked"){
			if($("#ssidPPSKKey").val() == null || $("#ssidPPSKKey").val() == ""){
				hm.util.reportFieldError(document.getElementById("messageForSsidKey"), "<s:text name="config.warn.message.provisioning.ssid"/>");
				$("#ssidPPSKKey").focus();
				return false;
			}
		}
		$("#enableProvisionEnterprise").attr("checked",false);
		$("#enableProvisionPrivate").attr("checked",false);
		return true;
	}
	if(accessMode2){
		$("#enableProvisionEnterprise").attr("checked",false);
		$("#enableProvisionPersonal").attr("checked",false);
		$("#ssidPPSKKey").val("");
		return true;
	}
	if(accessMode3){
		$("#enableProvisionPersonal").attr("checked",false);
		$("#enableProvisionPrivate").attr("checked",false);
		$("#ssidPPSKKey").val("");
		return true;
	}
	if(accessMode4){
		$("#enableProvisionEnterprise").attr("checked",false);
		$("#enableProvisionPersonal").attr("checked",false);
		$("#enableProvisionPrivate").attr("checked",false);
		$("#ssidPPSKKey").val("");
		return true;
	}
	if(accessMode5){
		$("#enableProvisionEnterprise").attr("checked",false);
		$("#enableProvisionPersonal").attr("checked",false);
		$("#enableProvisionPrivate").attr("checked",false);
		$("#ssidPPSKKey").val("");
		return true;
	}
}
var selectUIElement;
function submitAction(operation) {
	if((operation == "create") || operation =="update"){
		if(!validateBeforeSaving()){
			return false;
		}
	}
	if (validate(operation)) {
		<s:if test="%{!blnJsonMode}">
			showProcessing();
			document.forms[formName].operation.value = operation;
			if (operation == 'newSsidDos') {
				document.forms[formName].selectDosType.value = 'mac';
			}
			if (operation == 'newMacDos') {
				document.forms[formName].selectDosType.value = 'station';
			}
			if (operation == 'newConfigmdmPolicy') {
				document.forms[formName].selectDosType.value = 'configmdm';
			}
			hm.options.selectAllOptions('macFilters');
			//add handler to deal with something before form submit.
	 		beforeSubmitAction(document.forms[formName]);
			document.forms[formName].submit();
		</s:if>
		<s:else>
		
			selectUIElement = null;
			if(operation == 'newSsidDos'){
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+
						"&selectDosType="+'mac'+
						"&parentDomID=" + formName+ "_ssidDos"+
						"&ignore="+new Date().getTime();
			}else if(operation == 'newConfigmdmPolicy'){
				selectUIElement= Get(formName + "_configmdmId");
					var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
							"&blnJsonMode=true"+
							"&selectDosType="+'configmdm'+
							"&parentDomID=" + formName+ "_configmdmId"+
							"&ignore="+new Date().getTime();
					openIFrameDialog(780, 710, url);
					return;
			}else if(operation == 'newMacDos'){
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+
						"&selectDosType="+'station'+
						"&parentDomID=" + formName+ "_stationDos"+
						"&ignore="+new Date().getTime();
			}else if(operation == 'editSsidDos'){
				var value = hm.util.validateListSelection(formName + "_ssidDos");
				if(value < 0){
					return;
				}
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+
						"&ssidDos="+value+ 
						"&selectDosType="+'mac'+ 
						"&parentDomID=" + formName+ "_ssidDos"+
						"&ignore="+new Date().getTime();
			}else if(operation == 'editConfigmdmPolicy'){
					var value = hm.util.validateListSelection(formName + "_configmdmId");
					if(value < 0){
						return;
					}
					selectUIElement= Get(formName + "_configmdmId");
					var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
							"&blnJsonMode=true"+
							"&configmdmId="+value+ 
							"&parentDomID=" + formName+ "_configmdmId"+
							"&ignore="+new Date().getTime();
					openIFrameDialog(780, 710, url);
                    return;
			}else if(operation == 'editMacDos'){
				var value = hm.util.validateListSelection(formName + "_stationDos");
				if(value < 0){
					return;
				}
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+
						"&stationDos="+value+
						"&selectDosType="+'station'+ 
						"&parentDomID=" + formName+ "_stationDos"+
						"&ignore="+new Date().getTime();
			}else if(operation == 'newIpDos'){
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+ 
						"&ignore="+new Date().getTime();
				selectUIElement = Get(formName + "_ipDos");
			}else if(operation == 'editIpDos'){
				var value = hm.util.validateListSelection(formName + "_ipDos");
				if(value < 0){
					return;
				}
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+
						"&blnJsonMode=true"+
						"&ipDos="+value+
						"&ignore="+new Date().getTime();
				selectUIElement = Get(formName + "_ipDos");
			} else if(operation == 'newMacFilter'){
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+"&blnJsonMode=true"
			 	+ "&ignore="+new Date().getTime();
				openIFrameDialog(730, 450, url);
				return;
			} else if(operation == 'editMacFilter'){
				var value = hm.util.validateOptionTransferSelection("macFilters");
				if(value < 0){
					return;
				}		
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+"&blnJsonMode=true"+"&editMacFilterId="+value
			 	+ "&ignore="+new Date().getTime();
				openIFrameDialog(730, 450, url);
				return;
			} else if(operation == 'newServiceFilter') {
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+"&blnJsonMode=true"
			 	+ "&ignore="+new Date().getTime();
				// set the UI for sub dialog
				selectUIElement = Get(formName + "_serviceFilter");
			} else if(operation == 'editServiceFilter') {
				var value = hm.util.validateListSelection(formName + "_serviceFilter");
				if(value < 0){
					return ;
				}
				var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+"&blnJsonMode=true"+"&serviceFilter="+value
			 	+ "&ignore="+new Date().getTime();
				// set the UI for sub dialog
				selectUIElement = Get(formName + "_serviceFilter");
			}
			hm.options.selectAllOptions('macFilters');
			openIFrameDialog(800, 550, url);
			
			
		</s:else>
		
	}
}

function popUpSsidDosDlg(operation){
	if(operation == 'newSsidDos'){
		var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+operation+"&blnJsonMode=true"+"&selectDosType="+'mac'
			 + "&ignore="+new Date().getTime();
		hm.options.selectAllOptions('macFilters');
		
		openIFrameDialog(750, 500, url);
	}
}


function validateSsidForJson(operation){
	if (validate(operation)){
		hm.options.selectAllOptions('macFilters');
		return true;
	}
	return false;
}

function validate(operation) {
	if(operation == "editSsidDos"){
		var value = hm.util.validateListSelection(formName + "_ssidDos");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ssidDos.value = value;
		}
	}
	
	if(operation == "newConfigmdmPolicy")
	{
		return true;
	}
	if(operation =="editConfigmdmPolicy")
	{
		var value = hm.util.validateListSelection(formName + "_configmdmId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].configmdmId.value = value;
			return true;
		}
	}
	
	if(operation == "editMacDos"){
		var value = hm.util.validateListSelection(formName + "_stationDos");
		if(value < 0){
			return false
		}else{
			document.forms[formName].stationDos.value = value;
		}
	}
	
	if(operation == "editIpDos"){
		var value = hm.util.validateListSelection(formName + "_ipDos");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipDos.value = value;
		}
	}

	if(operation == "editMacFilter"){
		var value = hm.util.validateOptionTransferSelection("macFilters");
		if(value < 0){
			return false
		}else{
			document.forms[formName].editMacFilterId.value = value;
		}
	}
	if(operation == "editServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_serviceFilter");
		if(value < 0){
			return false
		}else{
			document.forms[formName].serviceFilter.value = value;
		}
	}
	
	if (operation == "newSsidDos" ||
		operation == "newMacDos" ||
		operation == "newIpDos" ||
		operation == "newMacFilter" ||
		operation == "newServiceFilter" ||
		operation == "editSsidDos" ||
		operation == "editMacDos" ||
		operation == "editIpDos" ||
		operation == "editMacFilter" ||
		operation == "editServiceFilter") {

		if (!validateGeneral()) {
			return false;
		}
		
		if (!validatePskUserLimit()) {
			return false;
		}
	
		if (!validateDtimSetting()) {
			return false;
		}
	
		if (!validateRtsThreshold()) {
			return false;
		}
	
		if (!validateFragThreshold()) {
			return false;
		}
		
		if (!validateUpdateInterval()) {
			return false;
		}
	
		if (!validateAgeOut()) {
			return false;
		}
		
		if (!validateClientAgeOut()){
			return false;
		}
		
		if (!validateMaxClient()) {
			return false;
		}

		if (!validateLocalCacheTimeout()) {
			return false;
		}
		
		if (!validateReauthInterval()) {
			return false;
		}
		
		if (!validateEapTimeOut()) {
			return false;
		}
		
		if (!validateEapRetries()) {
			return false;
		}
		return true;
	}
	
	if (operation == "<%=Navigation.L2_FEATURE_SSID_PROFILES_FULL%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>') {
		init_value();
		init_pass_value();
		document.getElementById(formName + "_dataSource_rtsThreshold").value=0;
		document.getElementById(formName + "_dataSource_dtimSetting").value=0;
		document.getElementById(formName + "_dataSource_fragThreshold").value=0;
		document.getElementById(formName + "_dataSource_updateInterval").value=0;
		document.getElementById("eapTimeOut").value=0;
		document.getElementById("eapRetries").value=0;
		document.getElementById(formName + "_dataSource_ageOut").value=0;
		document.getElementById(formName + "_dataSource_maxClient").value=0;
		document.getElementById(formName + "_dataSource_localCacheTimeout").value=0;
		document.getElementById("reauthInterval").value='0';
		document.getElementById("pskUserLimit").value='0';
		return true;
	}

	if (!validateSsidName()) {
		return false;
	}
	
	if (!validateSsid()) {
		return false;
	}

	if (!validateGeneral()) {
		return false;
	}
	
	if (!validatePskUserLimit()) {
		return false;
	}
	
	if (!validateDtimSetting()) {
		return false;
	}

	if (!validateRtsThreshold()) {
		return false;
	}

	if (!validateFragThreshold()) {
		return false;
	}

	if (!validateUpdateInterval()) {
		return false;
	}

	if (!validateAgeOut()) {
		return false;
	}
	
	if (!validateClientAgeOut()){
		return false;
	}

	if (!validateMaxClient()) {
		return false;
	}

	if (!validateLocalCacheTimeout()) {
		return false;
	}

	if (!validateReauthInterval()) {
		return false;
	}
	
	if (!validateEapTimeOut()) {
		return false;
	}
	
	if (!validateEapRetries()) {
		return false;
	}

	if (!checkRateSettings()) {
		return false;
	}
	
	if (!validatePpskSelfReg()) {
		return false;
	}
	if(!validateElemet(document.getElementById(formName+"_dataSource_cuthreshold"),'<s:text name="config.ssid.Advanced.IPMulticast.channelUtiThreshold" />',1,100)){
		return false;
	}
	if(!validateElemet(document.getElementById(formName+"_dataSource_memberthreshold"),'<s:text name="config.ssid.Advanced.IPMulticast.membership" />',1,30)){
		return false;
	}
	
	
	if(!validateVoiceEnterprise()){
		return false;
	}
	
	if(!validateEnrolledMdm()){
		return false;
	}
	
	return true;
}

function validateEnrolledMdm(){
	if($("#ssidProfilesFull_dataSource_enableMDM").attr("checked") == "checked"
			&& ($("#ssidProfilesFull_configmdmId").val() == "-1" 
			|| $("#ssidProfilesFull_configmdmId").val() == null)){
		hm.util.reportFieldError(document.getElementById("ssidProfilesFull_configmdmId"),
				'<s:text name="warn.port.access.mdm.invalid.message"/>');
		$("#ssidProfilesFull_configmdmId").focus();
		return false;
	}
	return true;
}

function validateSsidName() {
    var inputElement = document.getElementById(formName + "_dataSource_ssidName");
    var  message = hm.util.validateName(inputElement.value, '<s:text name="config.ssid.ssidName" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateSsid() {
    var inputElement = document.getElementById(formName + "_dataSource_ssid");
	var message = hm.util.validateSsid(inputElement.value, '<s:text name="config.ssid.head.ssid" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateDtimSetting() {
      var inputElement = document.getElementById(formName + "_dataSource_dtimSetting");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.head.dtimSetting" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.head.dtimSetting" />',
                                                       <s:property value="%{dtimSettingRange.min()}" />,
                                                       <s:property value="%{dtimSettingRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
           	inputElement.focus();
            return false;
      }
      return true;
}
function validateRtsThreshold() {
      var inputElement = document.getElementById(formName + "_dataSource_rtsThreshold");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.head.rtsThreshold" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.head.rtsThreshold" />',
                                                       <s:property value="%{rtsThresholdRange.min()}" />,
                                                       <s:property value="%{rtsThresholdRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}

function validateFragThreshold() {
      var inputElement = document.getElementById(formName + "_dataSource_fragThreshold");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.head.fragmentThreshold" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.head.fragmentThreshold" />',
                                                       <s:property value="%{fragThresholdRange.min()}" />,
                                                       <s:property value="%{fragThresholdRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}

function validateClientAgeOut() {
      var inputElement = document.getElementById(formName + "_dataSource_clientAgeOut");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.inactiveClientAgeout" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.inactiveClientAgeout" />',
                                                       <s:property value="%{clientAgeOutRange.min()}" />,
                                                       <s:property value="%{clientAgeOutRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}


function validateUpdateInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_updateInterval");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.updateInterval" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.updateInterval" />',
                                                       <s:property value="%{updateIntervalRange.min()}" />,
                                                       <s:property value="%{updateIntervalRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}

function validateAgeOut() {
      var inputElement = document.getElementById(formName + "_dataSource_ageOut");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ageOut" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ageOut" />',
                                                       <s:property value="%{ageOutRange.min()}" />,
                                                       <s:property value="%{ageOutRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}

function validateMaxClient() {
      var inputElement = document.getElementById(formName + "_dataSource_maxClient");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.maxClient" /></s:param></s:text>');
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.maxClient" />',
                                                       <s:property value="%{maxClientRange.min()}" />,
                                                       <s:property value="%{maxClientRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideAdvancePanelDiv").style.display="block";
            document.getElementById("showAdvancePanelDiv").style.display="none";
            inputElement.focus();
            return false;
      }
      return true;
}

function validateLocalCacheTimeout(){
	var inputElement = document.getElementById(formName + "_dataSource_localCacheTimeout");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.localCachetimeout" /></s:param></s:text>');
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.localCachetimeout" />',
	                                                 <s:property value="%{localCacheTimeoutRange.min()}" />,
	                                                 <s:property value="%{localCacheTimeoutRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	return true;
}

function validateEapTimeOut(){
	var inputElement = document.getElementById("eapTimeOut");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.eapTimeOut" /></s:param></s:text>');
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.eapTimeOut" />', 5, 300);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	return true;
}
function validateEapRetries(){
	var inputElement = document.getElementById("eapRetries");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.eapRetries" /></s:param></s:text>');
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.eapRetries" />', 1, 5);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	return true;
}

function validateReauthInterval() {
	  var mgmtKey = document.getElementById("mgmtKey").value;
      if (mgmtKey == KEY_MGMT_WPA_EAP_802_1_X 
    	|| mgmtKey == KEY_MGMT_WPA2_EAP_802_1_X
		|| mgmtKey == KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
		if (document.getElementById(formName + "_enabledReauthInterval").checked){
		
	      var inputElement = document.getElementById("reauthInterval");
	      if (inputElement.value.length == 0) {
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.reauthInterval" /></s:param></s:text>');
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	            showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.reauthInterval" />',600,86400);
	      if (message != null) {
	            hm.util.reportFieldError(inputElement, message);
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	            showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	    }
	  }
      return true;
}

function validatePskUserLimit() {
	  if (document.getElementById("accessMode2").checked) {
		if (document.getElementById(formName + "_enabledPskUserLimit").checked){
	      var inputElement = document.getElementById("pskUserLimit");
	      if (inputElement.value.length == 0) {
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.pskUserLimit" /></s:param></s:text>');
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.pskUserLimit" />',0,15);
	      if (message != null) {
	            hm.util.reportFieldError(inputElement, message);
	            inputElement.focus();
	            return false;
	      }
	    }
	  }
      return true;
}

function validateRekeyPeriod() {
      var inputElement = document.getElementById("rekeyPeriod");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.rekeyPeriod" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.rekeyPeriod" />',600,50000000);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           //	document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateRekeyPeriod2() {
	if (document.getElementById(formName + "_enabledRekeyPeriod2").checked){
	      var inputElement = document.getElementById("rekeyPeriod2");
	      if (inputElement.value.length == 0) {
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.rekeyPeriod" /></s:param></s:text>');
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.rekeyPeriod" />',600,50000000);
	      if (message != null) {
	            hm.util.reportFieldError(inputElement, message);
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
      }
      return true;
}

function validateRekeyPeriodPTK() {
	if (document.getElementById(formName + "_enabledRekeyPeriodPTK").checked){
      var inputElement = document.getElementById("rekeyPeriodPTK");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkRekeyPeriod" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkRekeyPeriod" />',10,50000000);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
    }
    return true;
}

function validateRekeyPeriodGMK() {
	if (document.getElementById(formName + "_enabledRekeyPeriodGMK").checked){
	      var inputElement = document.getElementById("rekeyPeriodGMK");
	      if (inputElement.value.length == 0) {
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gmkRekeyPeriod" /></s:param></s:text>');
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gmkRekeyPeriod" />',600,50000000);
	      if (message != null) {
	            hm.util.reportFieldError(inputElement, message);
	            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	}
    return true;
}

function validatePTKTimeOut() {
      var inputElement = document.getElementById("ptkTimeOut");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkTimeOut" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkTimeOut" />',100,8000);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateGTKTimeOut() {
      var inputElement = document.getElementById("gtkTimeOut");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gtkTimeOut" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gtkTimeOut" />',100,8000);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validatePTKRetries() {
      var inputElement = document.getElementById("ptkRetries");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkRetries" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkRetries" />',1,10);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateGTKRetries() {
      var inputElement = document.getElementById("gtkRetries");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gtkRetries" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gtkRetries" />',1,10);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateReplayWindow() {
      var inputElement = document.getElementById("replayWindow");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.replayWindow" /></s:param></s:text>');
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.replayWindow" />',0,10);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            //document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateKeyConfirmValue(elementKey,elementConfirm,checkBoxId) {
	  var keyElement;
	  var confirmElement;
	  if (document.getElementById(checkBoxId).checked) {
	  	keyElement = document.getElementById(elementKey);
	  	confirmElement = document.getElementById(elementConfirm);
	  } else {
	  	keyElement = document.getElementById(elementKey+ "_text");
	  	confirmElement = document.getElementById(elementConfirm+ "_text");
	  }

	  if (keyElement.value.length ==0) {
	         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
	         keyElement.focus();
	         return false;
      }

      if (confirmElement.value.length == 0) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }
      if (keyElement.name=="firstKeyValue0")
      {
	      if (keyElement.value.length < 8) {
	            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
	            keyElement.focus();
	            return false;
	      }

	      if (confirmElement.value.length < 8) {
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
	            confirmElement.focus();
	            return false;
	      }
	  }

	  if (keyElement.name=="firstKeyValue0_1")
      {
	      if (keyElement.value.length < 64) {
	            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
	            keyElement.focus();
	            return false;
	      }
	      
	      var message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue" />');

      	  if (message != null) {
	            hm.util.reportFieldError(keyElement, message);
	            keyElement.focus();
	            return false;
      	  }

	      if (confirmElement.value.length < 64) {
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
	            confirmElement.focus();
	            return false;
	      }
	      
	      var message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue" />');

      	  if (message != null) {
	            hm.util.reportFieldError(confirmElement, message);
	            confirmElement.focus();
	            return false;
      	  }
	  }

      if (keyElement.value != confirmElement.value) {
	      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
	    	keyElement.focus();
	    	return false;
      }

      return true;

}

function validateKeyConfirmValueFour(elementKey,elementConfirm,defaultKeyIndexValue,checkBlank,checkBoxId) {
	  var keyElement;
	  var confirmElement;
	  if (document.getElementById(checkBoxId).checked) {
	  	keyElement = document.getElementById(elementKey);
	  	confirmElement = document.getElementById(elementConfirm);
	  } else {
	  	keyElement = document.getElementById(elementKey+ "_text");
	  	confirmElement = document.getElementById(elementConfirm+ "_text");
	  }

	  if (keyElement.value.length ==0 && checkBlank =="1") {
			if (defaultKeyIndexValue =="1") {
				hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue1" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="2") {
				hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue2" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="3") {
				hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue3" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="4") {
				hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue4" /></s:param></s:text>');
	        }
	        keyElement.focus();
	        return false;
      }

      if (confirmElement.value.length == 0 && checkBlank =="1") {
      		if (defaultKeyIndexValue =="1") {
            	hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue1" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="2") {
            	hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue2" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="3") {
            	hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue3" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="4") {
            	hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue4" /></s:param></s:text>');
	        }
            confirmElement.focus();
            return false;
      }

      if (keyElement.name.substr(keyElement.name.length - 1)=="1" && keyElement.name.substr(keyElement.name.length - 2)!="_1") {
	      if (keyElement.value.length < 13 && keyElement.value.length !=0) {
			    if (defaultKeyIndexValue =="1") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        }
	            keyElement.focus();
	            return false;
	      }

	      if (confirmElement.value.length < 13 && confirmElement.value.length !=0) {
			    if (defaultKeyIndexValue =="1") {
		            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
		            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
		            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
		            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
		        }
	            confirmElement.focus();
	            return false;
	      }
		  if (keyElement.value.length !=0) {
		      var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue" />');
			  if (message != null) {
				    if (defaultKeyIndexValue =="1") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue1" />');
						hm.util.reportFieldError(keyElement, message);
			        } else if (defaultKeyIndexValue =="2") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue2" />');
						hm.util.reportFieldError(keyElement, message);
					} else if (defaultKeyIndexValue =="3") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue3" />');
						hm.util.reportFieldError(keyElement, message);
					} else if (defaultKeyIndexValue =="4") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue4" />');
						hm.util.reportFieldError(keyElement, message);
					}
		            keyElement.focus();
		            return false;
			  }
		  }
	  }

      if (keyElement.name.substr(keyElement.name.length - 3)=="1_1") {
      		if (keyElement.value.length < 26 && keyElement.value.length !=0) {
	      		if (defaultKeyIndexValue =="1") {
	            	hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
	            	hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
	            	hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
	            	hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        }
	            keyElement.focus();
	            return false;
	      	}
	      	if (confirmElement.value.length < 26 && confirmElement.value.length !=0) {
	      		if (defaultKeyIndexValue =="1") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
		        }
	            confirmElement.focus();
	            return false;
	        }
	        
	        var message=null;
	        if (keyElement.value.length !=0) {
	        	if (defaultKeyIndexValue =="1") {
	        		message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue1" />');
		        } else if (defaultKeyIndexValue =="2") {
		        	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue2" />');
		        } else if (defaultKeyIndexValue =="3") {
		        	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue3" />');
		        } else if (defaultKeyIndexValue =="4") {
		       	 	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue4" />');
		        }
	            if (message != null) {
			        hm.util.reportFieldError(keyElement, message);
			        keyElement.focus();
			        return false;
		      	}
	        }
	       	if (confirmElement.value.length !=0) {
	        	if (defaultKeyIndexValue =="1") {
	       			message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue1" />');
		        } else if (defaultKeyIndexValue =="2") {
		        	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue2" />');
		        } else if (defaultKeyIndexValue =="3") {
		        	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue3" />');
		        } else if (defaultKeyIndexValue =="4") {
		       	 	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue4" />');
		        }
	            if (message != null) {
			        hm.util.reportFieldError(confirmElement, message);
			        confirmElement.focus();
			        return false;
		      	}
	       	}
	  }

      if (keyElement.name.substr(keyElement.name.length - 1)=="2" && keyElement.name.substr(keyElement.name.length - 2)!="_1") {
	      	if (keyElement.value.length < 5 && keyElement.value.length !=0) {
			    if (defaultKeyIndexValue =="1") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        }
	            keyElement.focus();
	            return false;
	      	}

	      	if (confirmElement.value.length < 5 && confirmElement.value.length !=0) {
	      		if (defaultKeyIndexValue =="1") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
		        }
	            confirmElement.focus();
	            return false;
	      }
	      if (keyElement.value.length !=0) {
	      	var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue" />');
			  if (message != null) {
				    if (defaultKeyIndexValue =="1") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue1" />');
						hm.util.reportFieldError(keyElement, message);
			        } else if (defaultKeyIndexValue =="2") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue2" />');
						hm.util.reportFieldError(keyElement, message);
					} else if (defaultKeyIndexValue =="3") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue3" />');
						hm.util.reportFieldError(keyElement, message);
					} else if (defaultKeyIndexValue =="4") {
		      			message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.keyValue4" />');
						hm.util.reportFieldError(keyElement, message);
					}
		            keyElement.focus();
		            return false;
			  } 
		  }
	  }

      if (keyElement.name.substr(keyElement.name.length - 3)=="2_1") {
      		if (keyElement.value.length < 10 && keyElement.value.length !=0) {
			    if (defaultKeyIndexValue =="1") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
			        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        }
	            keyElement.focus();
	            return false;
	      }
	      if (confirmElement.value.length < 10 && confirmElement.value.length !=0) {
	      		if (defaultKeyIndexValue =="1") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue1" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="2") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue2" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="3") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue3" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        } else if (defaultKeyIndexValue =="4") {
	            	hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue4" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
		        }
	            confirmElement.focus();
	            return false;
	      }
	        var message=null;
	        if (keyElement.value.length !=0) {
	        	if (defaultKeyIndexValue =="1") {
	        		message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue1" />');
		        } else if (defaultKeyIndexValue =="2") {
		        	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue2" />');
		        } else if (defaultKeyIndexValue =="3") {
		        	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue3" />');
		        } else if (defaultKeyIndexValue =="4") {
		       	 	message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue4" />');
		        }
	            if (message != null) {
			        hm.util.reportFieldError(keyElement, message);
			        keyElement.focus();
			        return false;
		      	}
	        }
	       	if (confirmElement.value.length !=0) {
	        	if (defaultKeyIndexValue =="1") {
	       			message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue1" />');
		        } else if (defaultKeyIndexValue =="2") {
		        	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue2" />');
		        } else if (defaultKeyIndexValue =="3") {
		        	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue3" />');
		        } else if (defaultKeyIndexValue =="4") {
		       	 	message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue4" />');
		        }
	            if (message != null) {
			        hm.util.reportFieldError(confirmElement, message);
			        confirmElement.focus();
			        return false;
		      	}
	       	}
      }

      if (keyElement.value != confirmElement.value) {
      		if (defaultKeyIndexValue =="1") {
	      		hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue1" /></s:param><s:param><s:text name="config.ssid.keyValue1" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="2") {
	      		hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue2" /></s:param><s:param><s:text name="config.ssid.keyValue2" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="3") {
	      		hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue3" /></s:param><s:param><s:text name="config.ssid.keyValue3" /></s:param></s:text>');
	        } else if (defaultKeyIndexValue =="4") {
	      		hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue4" /></s:param><s:param><s:text name="config.ssid.keyValue4" /></s:param></s:text>');
	        }
	    	confirmElement.focus();
	    	return false;
      }

      return true;

}

function validateGeneral() {
    var mgmtKey = document.getElementById("mgmtKey").value;
    if (mgmtKey == KEY_MGMT_WPA_EAP_802_1_X 
    	|| mgmtKey == KEY_MGMT_WPA2_EAP_802_1_X
		|| mgmtKey == KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
		|| mgmtKey == KEY_MGMT_WPA2_PSK
		|| mgmtKey == KEY_MGMT_WPA_PSK
		|| mgmtKey == KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
		if(!validateRekeyPeriod2()) {
			return false;
		}
		if(!validateRekeyPeriodGMK()) {
			return false;
		}
		if(!validatePTKTimeOut()) {
			return false;
		}
		if(!validatePTKRetries()) {
			return false;
		}
		if(!validateGTKTimeOut()) {
			return false;
		}
		if(!validateGTKRetries()) {
			return false;
		}
		if(!validateRekeyPeriodPTK()) {
			return false;
		}
		if(!validateReplayWindow()) {
			return false;
		}
    }

    if (mgmtKey == KEY_MGMT_WPA2_PSK
		|| mgmtKey == KEY_MGMT_WPA_PSK
		|| mgmtKey == KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
		if (!document.getElementById("accessMode2").checked) {
			if (document.getElementById("keyType3").value == '0') {
				if(!validateKeyConfirmValue("firstKeyValue0","firstConfirmValue0","chkToggleDisplay0")) {
					return false;
				}
			}
			if (document.getElementById("keyType3").value == '1') {
				if(!validateKeyConfirmValue("firstKeyValue0_1","firstConfirmValue0_1","chkToggleDisplay0_1")) {
					return false;
				}
			}
		}
    }
    
   if (mgmtKey == KEY_MGMT_DYNAMIC_WEP) {
		if(!validateRekeyPeriod()) {
			return false;
		}
    }

    if (mgmtKey == KEY_MGMT_WEP_PSK ) {
        if (document.getElementById("enc").value == '3') {
        	 if (document.getElementById("keyType4").value == '0') {
        	 	if (document.getElementById("defaultKeyIndex4").value =='1') {
	        	 	if(!validateKeyConfirmValueFour("firstKeyValue1" , "firstConfirmValue1" , "1","1","chkToggleDisplay1_1")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='2') {
	        	 	if(!validateKeyConfirmValueFour("secondKeyValue1" , "secondConfirmValue1" , "2","1","chkToggleDisplay1_2")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='3') {
	        	 	if(!validateKeyConfirmValueFour("thirdKeyValue1" , "thirdConfirmValue1" , "3","1","chkToggleDisplay1_3")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='4') {
	        	 	if(!validateKeyConfirmValueFour("fourthValue1" , "fourthConfirmValue1" , "4","1","chkToggleDisplay1_4")) {
						return false;
					}
				}
				
        	 	if(!validateKeyConfirmValueFour("firstKeyValue1" , "firstConfirmValue1" , "1","0","chkToggleDisplay1_1")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("secondKeyValue1" , "secondConfirmValue1" , "2","0","chkToggleDisplay1_2")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("thirdKeyValue1" , "thirdConfirmValue1" , "3","0","chkToggleDisplay1_3")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("fourthValue1" , "fourthConfirmValue1" , "4","0","chkToggleDisplay1_4")) {
					return false;
				}
        	 }

        	 if (document.getElementById("keyType4").value == '1') {
        	 	if (document.getElementById("defaultKeyIndex4").value =='1') {
	        	 	if(!validateKeyConfirmValueFour("firstKeyValue1_1" , "firstConfirmValue1_1" , "1","1","chkToggleDisplay1_1_1")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='2') {
	        	 	if(!validateKeyConfirmValueFour("secondKeyValue1_1" , "secondConfirmValue1_1" , "2","1","chkToggleDisplay1_1_2")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='3') {
	        	 	if(!validateKeyConfirmValueFour("thirdKeyValue1_1" , "thirdConfirmValue1_1" , "3","1","chkToggleDisplay1_1_3")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex4").value =='4') {
	        	 	if(!validateKeyConfirmValueFour("fourthValue1_1" , "fourthConfirmValue1_1" , "4","1","chkToggleDisplay1_1_4")) {
						return false;
					}
				}

        	 	if(!validateKeyConfirmValueFour("firstKeyValue1_1" , "firstConfirmValue1_1" , "1","0","chkToggleDisplay1_1_1")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("secondKeyValue1_1" , "secondConfirmValue1_1" , "2","0","chkToggleDisplay1_1_2")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("thirdKeyValue1_1" , "thirdConfirmValue1_1" , "3","0","chkToggleDisplay1_1_3")) {
					return false;
				}
        	 	if(!validateKeyConfirmValueFour("fourthValue1_1" , "fourthConfirmValue1_1" , "4","0","chkToggleDisplay1_1_4")) {
					return false;
				}
        	 }
        }
    	if (document.getElementById("enc").value == '4') {
    	    if (document.getElementById("keyType5").value == '0') {
        	 	if (document.getElementById("defaultKeyIndex5").value =='1') {
	        	 	if(!validateKeyConfirmValueFour("firstKeyValue2" , "firstConfirmValue2" , "1","1","chkToggleDisplay2_1")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='2') {
	        	 	if(!validateKeyConfirmValueFour("secondKeyValue2" , "secondConfirmValue2" , "2","1","chkToggleDisplay2_2")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='3') {
	        	 	if(!validateKeyConfirmValueFour("thirdKeyValue2" , "thirdConfirmValue2" , "3","1","chkToggleDisplay2_3")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='4') {
	        	 	if(!validateKeyConfirmValueFour("fourthValue2" , "fourthConfirmValue2" , "4","1","chkToggleDisplay2_4")) {
						return false;
					}
				}
  
        	 	if(!validateKeyConfirmValueFour("firstKeyValue2" , "firstConfirmValue2" , "1","0","chkToggleDisplay2_1")) {
					return false;
				}
			
        	 	if(!validateKeyConfirmValueFour("secondKeyValue2" , "secondConfirmValue2" , "2","0","chkToggleDisplay2_2")) {
					return false;
				}

        	 	if(!validateKeyConfirmValueFour("thirdKeyValue2" , "thirdConfirmValue2" , "3","0","chkToggleDisplay2_3")) {
					return false;
				}

        	 	if(!validateKeyConfirmValueFour("fourthValue2" , "fourthConfirmValue2" , "4","0","chkToggleDisplay2_4")) {
					return false;
				}
        	 }

        	 if (document.getElementById("keyType5").value == '1') {
        	 	if (document.getElementById("defaultKeyIndex5").value =='1') {
	        	 	if(!validateKeyConfirmValueFour("firstKeyValue2_1" , "firstConfirmValue2_1" , "1","1","chkToggleDisplay2_1_1")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='2') {
	        	 	if(!validateKeyConfirmValueFour("secondKeyValue2_1" , "secondConfirmValue2_1" , "2","1","chkToggleDisplay2_1_2")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='3') {
	        	 	if(!validateKeyConfirmValueFour("thirdKeyValue2_1" , "thirdConfirmValue2_1" , "3","1","chkToggleDisplay2_1_3")) {
						return false;
					}
				}
        	 	if (document.getElementById("defaultKeyIndex5").value =='4') {
	        	 	if(!validateKeyConfirmValueFour("fourthValue2_1" , "fourthConfirmValue2_1" , "4","1","chkToggleDisplay2_1_4")) {
						return false;
					}
				}

        	 	if(!validateKeyConfirmValueFour("firstKeyValue2_1" , "firstConfirmValue2_1" , "1","0","chkToggleDisplay2_1_1")) {
					return false;
				}

        	 	if(!validateKeyConfirmValueFour("secondKeyValue2_1" , "secondConfirmValue2_1" , "2","0","chkToggleDisplay2_1_2")) {
					return false;
				}

        	 	if(!validateKeyConfirmValueFour("thirdKeyValue2_1" , "thirdConfirmValue2_1" , "3","0","chkToggleDisplay2_1_3")) {
					return false;
				}

        	 	if(!validateKeyConfirmValueFour("fourthValue2_1" , "fourthConfirmValue2_1" , "4","0","chkToggleDisplay2_1_4")) {
					return false;
				}
        	 }
    	}
    }
    return true;

}

 function init_value()
  {
  	document.getElementById("rekeyPeriod").value='600';
  	document.getElementById("rekeyPeriod2").value='0';
  	document.getElementById("rekeyPeriod2").readOnly=true;
  	document.getElementById(formName + "_enabledRekeyPeriod2").checked=false;
  	
	document.getElementById("rekeyPeriodGMK").value='0';
  	document.getElementById("rekeyPeriodGMK").readOnly=true;
  	document.getElementById(formName + "_enabledRekeyPeriodGMK").checked=false;
	
	document.getElementById("rekeyPeriodPTK").value='0';
	document.getElementById("rekeyPeriodPTK").readOnly=true;
	document.getElementById(formName + "_enabledRekeyPeriodPTK").checked=false;
	document.getElementById("reauthInterval").value='0';
	document.getElementById("reauthInterval").readOnly=true;
	document.getElementById(formName + "_enabledReauthInterval").checked=false;
	document.getElementById("ptkTimeOut").value='4000';
	document.getElementById("ptkRetries").value='3';
	document.getElementById("gtkTimeOut").value='4000';
	document.getElementById("gtkRetries").value='3';
	document.getElementById("replayWindow").value='0';
	document.getElementById("strict1").checked=true;
	document.getElementById("strict2").checked=true;
	document.getElementById("localTkip").checked=true;
	document.getElementById("remoteTkip").checked=true;
	document.getElementById("proactiveEnabled").checked=false;
	document.getElementById("preauthenticationEnabled").checked=false;
 	document.getElementById("keyType3").value='0';
	document.getElementById("keyType4").value='0';
	document.getElementById("keyType5").value='0';
	document.getElementById("defaultKeyIndex4").value='1';
	document.getElementById("defaultKeyIndex5").value='1';
	
  }
 function init_pass_value() {
  		document.getElementById("keyType3").value='0';
		document.getElementById("keyType4").value='0';
		document.getElementById("keyType5").value='0';
		document.getElementById("defaultKeyIndex4").value='1';
		document.getElementById("defaultKeyIndex5").value='1';
		document.getElementById("firstKeyValue0").value='';
		document.getElementById("firstConfirmValue0").value='';
		document.getElementById("firstKeyValue1").value='';
		document.getElementById("firstConfirmValue1").value='';
		document.getElementById("secondKeyValue1").value='';
		document.getElementById("secondConfirmValue1").value='';
		document.getElementById("thirdKeyValue1").value='';
		document.getElementById("thirdConfirmValue1").value='';
		document.getElementById("fourthValue1").value='';
		document.getElementById("fourthConfirmValue1").value='';
		document.getElementById("firstKeyValue2").value='';
		document.getElementById("firstConfirmValue2").value='';
		document.getElementById("secondKeyValue2").value='';
		document.getElementById("secondConfirmValue2").value='';
		document.getElementById("thirdKeyValue2").value='';
		document.getElementById("thirdConfirmValue2").value='';
		document.getElementById("fourthValue2").value='';
		document.getElementById("fourthConfirmValue2").value='';
		document.getElementById("firstKeyValue0_1").value='';
		document.getElementById("firstConfirmValue0_1").value='';
		document.getElementById("firstKeyValue1_1").value='';
		document.getElementById("firstConfirmValue1_1").value='';
		document.getElementById("secondKeyValue1_1").value='';
		document.getElementById("secondConfirmValue1_1").value='';
		document.getElementById("thirdKeyValue1_1").value='';
		document.getElementById("thirdConfirmValue1_1").value='';
		document.getElementById("fourthValue1_1").value='';
		document.getElementById("fourthConfirmValue1_1").value='';
		document.getElementById("firstKeyValue2_1").value='';
		document.getElementById("firstConfirmValue2_1").value='';
		document.getElementById("secondKeyValue2_1").value='';
		document.getElementById("secondConfirmValue2_1").value='';
		document.getElementById("thirdKeyValue2_1").value='';
		document.getElementById("thirdConfirmValue2_1").value='';
		document.getElementById("fourthValue2_1").value='';
		document.getElementById("fourthConfirmValue2_1").value='';
		
		document.getElementById("firstKeyValue0_text").value='';
		document.getElementById("firstConfirmValue0_text").value='';
		document.getElementById("firstKeyValue1_text").value='';
		document.getElementById("firstConfirmValue1_text").value='';
		document.getElementById("secondKeyValue1_text").value='';
		document.getElementById("secondConfirmValue1_text").value='';
		document.getElementById("thirdKeyValue1_text").value='';
		document.getElementById("thirdConfirmValue1_text").value='';
		document.getElementById("fourthValue1_text").value='';
		document.getElementById("fourthConfirmValue1_text").value='';
		document.getElementById("firstKeyValue2_text").value='';
		document.getElementById("firstConfirmValue2_text").value='';
		document.getElementById("secondKeyValue2_text").value='';
		document.getElementById("secondConfirmValue2_text").value='';
		document.getElementById("thirdKeyValue2_text").value='';
		document.getElementById("thirdConfirmValue2_text").value='';
		document.getElementById("fourthValue2_text").value='';
		document.getElementById("fourthConfirmValue2_text").value='';
		document.getElementById("firstKeyValue0_1_text").value='';
		document.getElementById("firstConfirmValue0_1_text").value='';
		document.getElementById("firstKeyValue1_1_text").value='';
		document.getElementById("firstConfirmValue1_1_text").value='';
		document.getElementById("secondKeyValue1_1_text").value='';
		document.getElementById("secondConfirmValue1_1_text").value='';
		document.getElementById("thirdKeyValue1_1_text").value='';
		document.getElementById("thirdConfirmValue1_1_text").value='';
		document.getElementById("fourthValue1_1_text").value='';
		document.getElementById("fourthConfirmValue1_1_text").value='';
		document.getElementById("firstKeyValue2_1_text").value='';
		document.getElementById("firstConfirmValue2_1_text").value='';
		document.getElementById("secondKeyValue2_1_text").value='';
		document.getElementById("secondConfirmValue2_1_text").value='';
		document.getElementById("thirdKeyValue2_1_text").value='';
		document.getElementById("thirdConfirmValue2_1_text").value='';
		document.getElementById("fourthValue2_1_text").value='';
		document.getElementById("fourthConfirmValue2_1_text").value='';
}


function show_keyType3(expid1)
  {
    var hideThird_one = document.getElementById("hideThird_one");
    var hideThird_two = document.getElementById("hideThird_two");
    hideThird_one.style.display="none";
	hideThird_two.style.display="none";
	init_pass_value();
	if (expid1=="0") {
	    hideThird_one.style.display="block";
	}
	if (expid1=="1") {
		document.getElementById("keyType3").value='1';
	    hideThird_two.style.display="block";
	}

  }

function show_keyType4(expid1)
  {
    var hideFourth_one = document.getElementById("hideFourth_one");
    var hideFourth_two = document.getElementById("hideFourth_two");
    hideFourth_one.style.display="none";
	hideFourth_two.style.display="none";
	init_pass_value();
	if (expid1=="0") {
	    hideFourth_one.style.display="block";
	}
	if (expid1=="1") {
		document.getElementById("keyType4").value='1';
	    hideFourth_two.style.display="block";
	}

  }

function show_keyType5(expid1)
  {
    var hideFifth_one = document.getElementById("hideFifth_one");
    var hideFifth_two = document.getElementById("hideFifth_two");
    hideFifth_one.style.display="none";
	hideFifth_two.style.display="none";
	init_pass_value();
	if (expid1=="0") {
	    hideFifth_one.style.display="block";
	}
	if (expid1=="1") {
		document.getElementById("keyType5").value='1';
	    hideFifth_two.style.display="block";
	}

  }

function show_fourth(selectValue)
{
	 var mgmtKey = document.getElementById("mgmtKey").value;
	 var hideTkip = document.getElementById("hideTkip");
	 if (selectValue == 2 || selectValue == 5) {
		document.getElementById("localTkip").checked=true;
		document.getElementById("remoteTkip").checked=true;
	 	hideTkip.style.display="block";
	 } else {
	   	hideTkip.style.display="none";
	 }
	 if (mgmtKey == KEY_MGMT_WEP_PSK) {
		 if (selectValue=="3") {
		  	init_pass_value();
		 	document.getElementById("hideFourth").style.display="block";
		 	document.getElementById("hideFourth_one").style.display="block";
		 	document.getElementById("hideFourth_two").style.display="none";
		  	document.getElementById("hideFifth").style.display="none";
		  	document.getElementById("hideFifth_one").style.display="none";
		  	document.getElementById("hideFifth_two").style.display="none";
		  }
	
		 if (selectValue=="4") {
		  	init_pass_value();
		  	document.getElementById("hideFourth").style.display="none";
		 	document.getElementById("hideFourth_one").style.display="none";
		 	document.getElementById("hideFourth_two").style.display="none";
		  	document.getElementById("hideFifth").style.display="block";
		  	document.getElementById("hideFifth_one").style.display="block";
		  	document.getElementById("hideFifth_two").style.display="none";
		  }
	  }
	 
	 if (selectValue == "2" && (mgmtKey == KEY_MGMT_WPA2_EAP_802_1_X || mgmtKey == KEY_MGMT_WPA2_PSK)){
		 $("#hide80211w").hide();
		 $("#enable80211w").attr("disabled","");
		 $("#enableBip").attr("disabled","");
		 $("#wpa2mfpType1").attr("disabled","");
		 $("#wpa2mfpType2").attr("disabled","");
	 }else if ((mgmtKey == KEY_MGMT_WPA2_EAP_802_1_X || mgmtKey == KEY_MGMT_WPA2_PSK) 
			 && selectValue == "1" && !document.getElementById("accessMode2").checked){
		 $("#hide80211w").show();
		 $("#enable80211w").removeAttr("disabled");
		 $("#enableBip").removeAttr("disabled");
		 $("#wpa2mfpType1").removeAttr("disabled");
		 $("#wpa2mfpType2").removeAttr("disabled");
	 }
}

function show_tt(expid1)
  {
	wpa2Check();
  	setCwpTitle();
  	var hideStrict = document.getElementById("hideStrict");
  	var hideAfterStrict = document.getElementById("hideAfterStrict");
  	var hideGmkRekeyPeriod = document.getElementById("hideGmkRekeyPeriod");
  	var hideRekeyPeriod = document.getElementById("hideRekeyPeriod");
  	var hideTkip = document.getElementById("hideTkip");
  	var hideThird = document.getElementById("hideThird");
  	var hideFourth = document.getElementById("hideFourth");
  	var hideFifth = document.getElementById("hideFifth");
  	var hideThird_one = document.getElementById("hideThird_one");
    var hideThird_two = document.getElementById("hideThird_two");
    var hideFourth_one = document.getElementById("hideFourth_one");
    var hideFourth_two = document.getElementById("hideFourth_two");
    var hideFifth_one = document.getElementById("hideFifth_one");
    var hideFifth_two = document.getElementById("hideFifth_two");
    var hideReauthInterval = document.getElementById("hideReauthInterval");
    var hideLocalCacheTimeout = document.getElementById("hideLocalCacheTimeout");
    var hide80211w = document.getElementById("hide80211w");
    var hideKeyMgtNote=document.getElementById("keymgtNoteTr");
    
    hideStrict.style.display="block";
	hideGmkRekeyPeriod.style.display="none";
	hideRekeyPeriod.style.display="none";

	hideAfterStrict.style.display="none";
	hideTkip.style.display="none";
	
	hideThird.style.display="none";
	hideFourth.style.display="none";
	hideFifth.style.display="none";
  	hideThird_one.style.display="none";
    hideThird_two.style.display="none";
    hideFourth_one.style.display="none";
    hideFourth_two.style.display="none";
    hideFifth_one.style.display="none";
    hideFifth_two.style.display="none";
    hideReauthInterval.style.display="none";
    hideLocalCacheTimeout.style.display="none";
    hide80211w.style.display="none";
    hideKeyMgtNote.style.display="none";

	var enc = document.getElementById("enc");
	var aut = document.getElementById("aut");
	document.getElementById("hideAuthMethord").style.display="none";
	if (expid1==KEY_MGMT_OPEN) {

		enc.length=0;
		enc.length=1;
		enc.options[0].value='0';
		enc.options[0].text='NONE';

		aut.length=0;
		aut.length=1;
		aut.options[0].value='0';
		aut.options[0].text='OPEN';
		hideStrict.style.display="none";
    }

    if (expid1==KEY_MGMT_WPA2_EAP_802_1_X || expid1==KEY_MGMT_WPA2_PSK || expid1==KEY_MGMT_WPA_EAP_802_1_X || expid1==KEY_MGMT_WPA_PSK) {

		enc.length=0;
		enc.length=2;
		enc.options[0].value='1';
		enc.options[0].text='CCMP (AES)';
		enc.options[1].value='2';
		enc.options[1].text='TKIP';

		if (expid1==KEY_MGMT_WPA2_EAP_802_1_X || expid1==KEY_MGMT_WPA_EAP_802_1_X) {
			aut.length=0;
			aut.length=1;
			aut.options[0].value='1';
			aut.options[0].text='EAP(802.1X)';
			if (expid1==KEY_MGMT_WPA2_EAP_802_1_X) {
			 	hideAfterStrict.style.display="block";
			 	hideStrict.style.display="none"
			}
		}
		if (expid1==KEY_MGMT_WPA2_PSK || expid1==KEY_MGMT_WPA_PSK) {
			if (!document.getElementById("accessMode2").checked) {
				hideThird.style.display="block";
				if (Get("keyType3").value=='0') {
		    		hideThird_one.style.display="block";
		    	} else {
		    		hideThird_two.style.display="block";
		    	}
			}
			aut.length=0;
			aut.length=1;
			aut.options[0].value='0';
			aut.options[0].text='OPEN';
		}
		
		hideGmkRekeyPeriod.style.display="block";
		
		if (expid1==KEY_MGMT_WPA2_EAP_802_1_X || expid1==KEY_MGMT_WPA2_PSK && !document.getElementById("accessMode2").checked) {
			 $("#hide80211w").show();
			 $("#enable80211w").removeAttr("disabled");
			 $("#wpa2mfpType1").removeAttr("disabled");
			 $("#wpa2mfpType2").removeAttr("disabled");
		}else{
			 $("#hide80211w").hide();
			 $("#enable80211w").attr("disabled","");
			 $("#wpa2mfpType1").attr("disabled","");
			 $("#wpa2mfpType2").attr("disabled","");
		}
		
    }
    if (expid1==KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {

    	hideAfterStrict.style.display="block";
    	hideStrict.style.display="none"
    	hideTkip.style.display="block";
		enc.length=0;
		enc.length=1;
		enc.options[0].value='5';
		enc.options[0].text='Auto-TKIP or CCMP (AES)';

		aut.length=0;
		aut.length=1;
		aut.options[0].value='1';
		aut.options[0].text='EAP(802.1X)';
		
		hideGmkRekeyPeriod.style.display="block";
    }

    if (expid1==KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
		if (!document.getElementById("accessMode2").checked) {
	    	hideThird.style.display="block";
	    	if (Get("keyType3").value=='0') {
	    		hideThird_one.style.display="block";
	    	} else {
	    		hideThird_two.style.display="block";
	    	}
	    }
    	hideTkip.style.display="block";
		enc.length=0;
		enc.length=1;
		enc.options[0].value='5';
		enc.options[0].text='Auto-TKIP or CCMP (AES)';

		aut.length=0;
		aut.length=1;
		aut.options[0].value='0';
		aut.options[0].text='OPEN';
		
		hideGmkRekeyPeriod.style.display="block";
    }

    if (expid1==KEY_MGMT_WEP_PSK) {
    
    	hideStrict.style.display="none";
		hideFourth.style.display="block";
		document.getElementById("hideAuthMethord").style.display="block";
		if (Get("keyType4").value=='0') {
    		hideFourth_one.style.display="block";
    	} else {
    		hideFourth_two.style.display="block";
    	}

    	document.getElementById("showOptionDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideEap").style.display="none";
		document.getElementById("eapTimeOut").value=30;
		document.getElementById("eapRetries").value=3;
		
		enc.length=0;
		enc.length=2;
		enc.options[0].value='3';
		enc.options[0].text='WEP 104';
		enc.options[1].value='4';
		enc.options[1].text='WEP 40';

		aut.length=0;
		aut.length=2;
		aut.options[0].value='0';
		aut.options[0].text='OPEN';
		aut.options[1].value='2';
		aut.options[1].text='SHARED';

    }
    if (expid1==KEY_MGMT_DYNAMIC_WEP) {
    	document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
    	document.getElementById("hideEap").style.display="block";
    	document.getElementById("eapTimeOut").value=30;
		document.getElementById("eapRetries").value=3;
		
		hideStrict.style.display="none";
		hideRekeyPeriod.style.display="block";
		
		enc.length=0;
		enc.length=2;
		enc.options[0].value='3';
		enc.options[0].text='WEP 104';
		enc.options[1].value='4';
		enc.options[1].text='WEP 40';

		aut.length=0;
		aut.length=1;
		aut.options[0].value='1';
		aut.options[0].text='EAP(802.1X)';
    }
    if (expid1==KEY_MGMT_WPA2_EAP_802_1_X 
    	|| expid1==KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X 
    	|| expid1==KEY_MGMT_WPA_EAP_802_1_X ) {
    	hideReauthInterval.style.display="block";
    	hideLocalCacheTimeout.style.display="block";
    } else {
    	document.getElementById(formName + "_dataSource_localCacheTimeout").value=86400;
    }
    
    if (expid1==KEY_MGMT_WPA_PSK  || expid1==KEY_MGMT_WPA_EAP_802_1_X ) {
    	hideKeyMgtNote.style.display="";
    }
    
    changeCwpAuthValue();
  }
 
function enableGRateSet(checked) {
	for (var i = 0; i < 12; i ++) {
		document.getElementById("gRateSetType_"+i).disabled =!checked;
	}	
}

function enableARateSet(checked) {
	for (var i = 0; i < 8; i ++) {
		document.getElementById("aRateSetType_"+i).disabled =!checked;
	}	
}

function enableNRateSet(checked) {
	for (var i = 0; i < 24; i ++) {
		document.getElementById("nRateSetType_"+i).disabled =!checked;
	}	
}

function checkRateSettings() {
	var enableGRateSet = document.getElementById(formName + "_dataSource_enableGRateSet");
	if (enableGRateSet.checked) {
		var j = 0;
		for (var i = 0; i < 12; i ++) {
			if (SSID_RATE_SET_BASIC == document.getElementById("gRateSetType_"+i).value) {
				j ++;
			}
		}
		if (j == 0) {
			hm.util.reportFieldError(enableGRateSet, '<s:text name="error.ssidProfile.rateSet"><s:param>11g Radio</s:param></s:text>');
			enableGRateSet.focus();
			showHideRadioRateDiv(1);
			return false;
		}
	}
	var enableARateSet = document.getElementById(formName + "_dataSource_enableARateSet");
	if (enableARateSet.checked) {
		var j = 0;
		for (var i = 0; i < 8; i ++) {
			if (SSID_RATE_SET_BASIC == document.getElementById("aRateSetType_"+i).value) {
				j ++;
			}
		}
		if (j == 0) {
			hm.util.reportFieldError(enableARateSet, '<s:text name="error.ssidProfile.rateSet"><s:param>11a Radio</s:param></s:text>');
			enableARateSet.focus();
			showHideRadioRateDiv(1);
			return false;
		}
	}
	
	var enableNRateSet = document.getElementById(formName + "_dataSource_enableNRateSet");
	if (enableNRateSet.checked) {
		var j = 0;
		for (var i = 0; i < 24; i ++) {
			if (SSID_RATE_SET_NEITHER == document.getElementById("nRateSetType_"+i).value) {
				j ++;
			}
		}
		if (j == 24) {
			hm.util.reportFieldError(enableNRateSet, 'Must set one MCS rate at least.');
			enableNRateSet.focus();
			showHideRadioRateDiv(1);
			return false;
		}
	}
	
	//11ac MCS rate settings
	var acRateSetError = document.getElementById("acRateSetError");
	if(!Get("stream_state_1").checked 
			&& !Get("stream_state_2").checked 
			&& !Get("stream_state_3").checked){
		hm.util.reportFieldError(acRateSetError, '<s:text name="error.ssidProfile.rateSet"><s:param>11ac Radio</s:param></s:text>');
		Get("stream_state_1").focus();
		return false;
	}
	
	return true;
}

function changeSsidName() {
	document.getElementById(formName + "_dataSource_ssid").value=document.getElementById(formName + "_dataSource_ssidName").value;
	$("#privateSsidmodel1").next().text("Allow users to register themselves on this SSID (" + document.getElementById(formName + "_dataSource_ssidName").value + ")");
}



function changeEnabledUseGuestManager(){
	if (document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked) {
		document.getElementById(formName + "_dataSource_macAuthEnabled").checked=false;
	}
	changeCwpAuthValue();
}

function changeCwpAuthValue(checked,parent) {
	if(checked != null){
		if(checked){
			$("#enableProvisionPersonal").attr("checked",false);
			$("#enableProvisionPersonal").attr("disabled",true);
			$("#enableProvisionEnterprise").attr("checked",false);
			$("#enableProvisionEnterprise").attr("disabled",true);
			$("#enableProvisionPrivate").attr("checked",false);
			$("#enableProvisionPrivate").attr("disabled",true);
			$("#provisionSsidDiv").css("display","none");
			$("#ssidPPSKKey").val("");
		}else{
			<s:if test="usabledIDM">
			if(Get("enableIDMChk").checked == false){
				$("#enableProvisionPersonal").attr("disabled",false);
				$("#enableProvisionPrivate").attr("disabled",false);
				$("#enableProvisionEnterprise").attr("disabled",false);
			} else {
				$("#enableProvisionPrivate").attr("disabled",false);
			}
			</s:if>
			<s:else>
				$("#enableProvisionPersonal").attr("disabled",false);
				$("#enableProvisionPrivate").attr("disabled",false);
				$("#enableProvisionEnterprise").attr("disabled",false);
			</s:else>
		}
	}
	var blnMacAuth;
	if (document.getElementById("accessMode2").checked) {
		blnMacAuth = document.getElementById(formName + "_dataSource_macAuthEnabled").checked || document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked
		if (document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked=false;
		}
	} else {
	 	blnMacAuth=document.getElementById(formName + "_dataSource_macAuthEnabled").checked;
	}
	if (blnMacAuth) {
		document.getElementById("hideRadiusPAPCHAP").style.display="block";
	} else {
		document.getElementById("hideRadiusPAPCHAP").style.display="none";
		document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
	}
	
	if (document.getElementById("accessMode5").checked && blnMacAuth && Get("enableCwpSelect").checked) {
		var keyManagement=document.getElementById("mgmtKey").value;
		var url = '<s:url action="ssidProfilesFull"><s:param name="operation" value="changeCwpAuthOperation"/></s:url>' + "&blnMacAuth="+blnMacAuth + "&keyManagement="+keyManagement + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	} else {
		document.getElementById("hideFallBackToEcwp").style.display="none";
		document.getElementById(formName + "_dataSource_fallBackToEcwp").checked=false;
	}
	if(!Get("enableCwpSelect").checked 
			&& (Get("accessMode1").checked || Get("accessMode4").checked || Get("accessMode5").checked)) {
		if(Get("enableIDMChk") && Get("enableIDMChk").checked) {
			Get("enableIDMChk").checked = false;
			enabledCloudAuth(false);
		}
	} else if (Get("enableCwpSelect").checked && Get("accessMode4").checked) {
		if(Get("enableIDMChk") && Get("enableIDMChk").checked) {
			disabledCWP(Get("mgmtKey").value==KEY_MGMT_WEP_PSK);
		}
	}
	
	if (Get("accessMode5").checked) {
		if(Get("enableCloudCwpSelect")) {
			changeCloudCwpAuthValue(Get("enableCloudCwpSelect").checked);
		}
	} else {
		resetCloudCwpAuth(true);
	}
}

function changeCloudCwpAuthValue(flag) {
	if(Get("enableCloudCwpSelect")) {
		if(flag && Get("accessMode5").checked) {
			if(Get("enableIDMChk")) {
				Get("idmEnabledRow").style.display = "none";
				Get("enableIDMChk").checked = false;
			}
			if(Get("enableCwpSelect")) {
				Get("cwpAuthTr").style.display = "none";
				Get("enableCwpSelect").checked = false;
			}
		} else {
			if(Get("enableIDMChk")) {
				Get("idmEnabledRow").style.display = "";
			}
			if(Get("enableCwpSelect")) {
				Get("cwpAuthTr").style.display = "";
			}
			if(Get("accessMode5").checked) {
				resetCloudCwpAuth(Get("enableCwpSelect").checked);
			} else {
				resetCloudCwpAuth(true);
			}
		}
	}
}
function resetCloudCwpAuth(hide) {
	if(Get("enableCloudCwpSelect")) {
		Get("enableCloudCwpSelect").checked = false;
		Get("cloudCwpAuthTr").style.display = hide ? "none" : "";
	}
}

function enableWpa2mfpType(checked){
	if(checked){
		Get("wpa2mfpTypeTD").style.display="";
		Get("enableBipTr").style.display="";
		Get("enableBip").disabled = false;
	}else{
		Get("wpa2mfpTypeTD").style.display="none";
		Get("enableBipTr").style.display="none";
		Get("enableBip").checked = false;
		Get("enableBip").disabled = true;
	}
}

function enabledVoiceEnterprise(){
	var cancelBtn;
	var continueBtn;
	var mybuttons;
	var deviceTypeChangeMsg;
	var dlg;
	if(Get(formName +"_dataSource_enabledVoiceEnterprise").checked){
		
		mybuttons = [ { text:"OK", handler: function(){changeVoiceEnterprise(1);this.hide();} }, 
	                  { text:"Cancel", handler: function(){this.hide();Get(formName +"_dataSource_enabledVoiceEnterprise").checked = false;}, isDefault:true} ];
	    deviceTypeChangeMsg = "<html><body>"+'<s:text name ="config.ssid.voice.enterprise.wmm.note" />' +"</body></html>";
	    dlg = userDefinedConfirmDialog(deviceTypeChangeMsg, mybuttons, "Warning");
	    dlg.show();
		
	 }else{
		mybuttons = [ { text:"Yes", handler: function(){changeVoiceEnterprise(2);this.hide();} }, 
		              { text:"No", handler: function(){changeVoiceEnterprise(3);this.hide();} }, 
		              { text:"Cancel", handler: function(){this.hide();Get(formName +"_dataSource_enabledVoiceEnterprise").checked = true}, isDefault:true} ];
		deviceTypeChangeMsg = "<html><body>"+'<s:text name ="config.ssid.voice.enterprise.disable.wmm.note" />' +"</body></html>";
		dlg = userDefinedConfirmDialog(deviceTypeChangeMsg, mybuttons, "Warning");
		dlg.show();
	 }
}

function changeVoiceEnterprise(state){
	if(state == 1){
		 Get(formName +"_enabledwmm").checked = true;
		 Get(formName +"_enabledwmm").disabled = true;
		 Get(formName +"_dataSource_enabledwmm").value = true;
		 
		 Get(formName +"_enabled80211k").checked = true;
		 Get(formName +"_enabled80211v").checked = true;
		 Get(formName +"_enabled80211r").checked = true;
		 
		 Get(formName +"_dataSource_enabled80211k").value = true;
		 Get(formName +"_dataSource_enabled80211v").value = true;
		 Get(formName +"_dataSource_enabled80211r").value = true;
		 
		 Get(formName +"_enabled80211k").disabled = true;
		 Get(formName +"_enabled80211v").disabled = true;
		 Get(formName +"_enabled80211r").disabled = true;
		 
		 Get(formName +"_enabledAcVoice").checked = true;
		 Get(formName +"_enabledAcVoice").disabled = true;
		 Get(formName +"_dataSource_enabledAcVoice").value = true;
		 
		 Get(formName +"_dataSource_enabledAcVideo").disabled = false;
		 Get(formName +"_dataSource_enabledUnscheduled").disabled = false;
	}else if(state == 2){
		 Get(formName +"_dataSource_enabled80211k").value = false;
		 Get(formName +"_dataSource_enabled80211v").value = false;
		 Get(formName +"_dataSource_enabled80211r").value = false;
		 
		 Get(formName +"_enabled80211k").checked = false;
		 Get(formName +"_enabled80211v").checked = false;
		 Get(formName +"_enabled80211r").checked = false;
		 
		 Get(formName +"_enabled80211k").disabled = false;
		 Get(formName +"_enabled80211v").disabled = false;
		 Get(formName +"_enabled80211r").disabled = false;
		 
		 Get(formName +"_enabledAcVoice").checked = false;
		 Get(formName +"_enabledAcVoice").disabled = false;
		 Get(formName +"_dataSource_enabledAcVoice").value = false;
		 
		 Get(formName +"_dataSource_enabledAcVideo").disabled = false;
		 Get(formName +"_dataSource_enabledUnscheduled").disabled = false;
		 
		 Get(formName +"_enabledwmm").disabled = false;
	}else{
		Get(formName +"_enabled80211k").disabled = false;
		Get(formName +"_enabled80211v").disabled = false;
		Get(formName +"_enabled80211r").disabled = false;
		Get(formName +"_enabledAcVoice").disabled = false;
		Get(formName +"_enabledwmm").disabled = false;
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	// 1: all null
	// 2: self-reg
	// 3: open wep
	// 4: auth
	// 5: both
	
	var fallToEcwp = details.f;
	if (fallToEcwp==1){
		document.getElementById("hideFallBackToEcwp").style.display="block";
	} else {
		document.getElementById("hideFallBackToEcwp").style.display="none";
		document.getElementById(formName + "_dataSource_fallBackToEcwp").checked=false;
	}
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function changeRekeyPeriod2Value(checked){
    var rekeyPeriod2 = document.getElementById("rekeyPeriod2");
	if (checked){
		rekeyPeriod2.value=600;
		rekeyPeriod2.readOnly=false;
	}
	if (!checked){
		rekeyPeriod2.value=0;
		rekeyPeriod2.readOnly=true;
	}
}

function changeRekeyPeriodGMKValue(checked){
    var rekeyPeriodGMK = document.getElementById("rekeyPeriodGMK");
	if (checked){
		rekeyPeriodGMK.value=86400;
		rekeyPeriodGMK.readOnly=false;
	}
	if (!checked){
		rekeyPeriodGMK.value=0;
		rekeyPeriodGMK.readOnly=true;
	}
}

function changePtkRekeyPeriodValue(checked){
    var ptkRekeyPeriod = document.getElementById("rekeyPeriodPTK");
	if (checked){
		ptkRekeyPeriod.value=0;
		ptkRekeyPeriod.readOnly=false;
	}
	if (!checked){
		ptkRekeyPeriod.value=0;
		ptkRekeyPeriod.readOnly=true;
	}
}
function changeReauthIntervalValue(checked){
    var reauthInterval = document.getElementById("reauthInterval");
	if (checked){
		reauthInterval.value=0;
		reauthInterval.readOnly=false;
	}
	if (!checked){
		reauthInterval.value=0;
		reauthInterval.readOnly=true;
	}
}

function changePskUserLimitValue(checked){
    var pskUserLimit = document.getElementById("pskUserLimit");
	if (checked){
		pskUserLimit.value=0;
		pskUserLimit.readOnly=false;
	}
	if (!checked){
		pskUserLimit.value=0;
		pskUserLimit.readOnly=true;
	}
}

function setCwpTitle(){
	if (document.getElementById("accessMode2").checked
	 	||document.getElementById("accessMode3").checked
	 	||document.getElementById("mgmtKey").value==KEY_MGMT_DYNAMIC_WEP){
	 	
	 	document.getElementById("cwpTitleLabel").innerHTML ="Enable a captive web portal with use policy acceptance";
	 } else {
	 	document.getElementById("cwpTitleLabel").innerHTML ="Enable Captive Web Portal";
	 }
}

function setSecurityExplainContext(accessMode){
	if (accessMode==1) {
		document.getElementById("hideSecurityExplain").style.display="block";
		document.getElementById("securityExplainContext").innerHTML ="<s:text name="config.ssid.access1.note"/>";
	} else if(accessMode==2) {
		document.getElementById("hideSecurityExplain").style.display="block";
		document.getElementById("securityExplainContext").innerHTML ="<s:text name="config.ssid.access2.note"/>";
	} else if(accessMode==3) {
		document.getElementById("hideSecurityExplain").style.display="block";
		document.getElementById("securityExplainContext").innerHTML ="<s:text name="config.ssid.access3.note"/>";
	} else if(accessMode==4) {
		document.getElementById("hideSecurityExplain").style.display="block";
		document.getElementById("securityExplainContext").innerHTML ="<s:text name="config.ssid.access4.note"/>";
	} else if(accessMode==5) {
		document.getElementById("hideSecurityExplain").style.display="block";
		document.getElementById("securityExplainContext").innerHTML ="<s:text name="config.ssid.access5.note"/>";
	}
}
function checkIDMEnable(accessMode){
	if(<s:property value="%{usabledIDM}"/>) {
		if(accessMode == 1){
			if(Get("enableCwpSelect").checked == true || document.getElementById("enableIDMChk").checked){
				$("#enableProvisionPersonal").attr("disabled",true);
				$("#provisionSsidDiv").hide();
			}else{
				if($("#enableIDMChk").attr("disabled") == 'disabled'){
					$("#enableIDMChk").attr("disabled",false);
				}
				$("#enableProvisionPersonal").attr("disabled",false);
				$("#provisionSsidDiv").hide();
			}
		}
		if(accessMode == 2){
			if(Get("enableCwpSelect").checked == true){
				$("#enableProvisionPrivate").attr("disabled",true);
			}else{
				if($("#enableIDMChk").attr("disabled") == 'disabled'){
					$("#enableIDMChk").attr("disabled",false);
				}
				$("#enableProvisionPrivate").attr("disabled",false);
			}
		}
		if(accessMode == 3){
			if(Get("enableCwpSelect").checked == true || document.getElementById("enableIDMChk").checked){
				$("#enableProvisionEnterprise").attr("disabled",true);
			}else{
				if($("#enableIDMChk").attr("disabled") == 'disabled'){
					$("#enableIDMChk").attr("disabled",false);
				}
				$("#enableProvisionEnterprise").attr("disabled",false);
			}
		}
		if(accessMode == 4 || accessMode == 5){
			if($("#enableIDMChk").attr("disabled") == 'disabled'){
				$("#enableIDMChk").attr("disabled",false);
			}
		}
    }else{
    	if(accessMode == 1){
			if(Get("enableCwpSelect").checked == true ){
				$("#enableProvisionPersonal").attr("disabled",true);
				$("#provisionSsidDiv").hide();
			}else{
				$("#enableProvisionPersonal").attr("disabled",false);
				$("#provisionSsidDiv").hide();
			}
		}
		if(accessMode == 2){
			if(Get("enableCwpSelect").checked == true ){
				$("#enableProvisionPrivate").attr("disabled",true);
			}else{
				$("#enableProvisionPrivate").attr("disabled",false);
			}
		}
		if(accessMode == 3){
			if(Get("enableCwpSelect").checked == true ){
				$("#enableProvisionEnterprise").attr("disabled",true);
			}else{
				$("#enableProvisionEnterprise").attr("disabled",false);
			}
		}
    }
	
}

function changeAccessMode(accessMode){
    <s:if test="enableClientManagement==true">
	$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",false);
	$("#ssidProfilesFull_dataSource_enableMDM").attr("checked",false);
	$("#enablemdmselect").css("display","none");
	
	wpa2Check();
	
	setSecurityExplainContext(accessMode);
	
	setCwpTitle();
    document.getElementById("cwpAuthTr").style.display="";
    document.getElementById("macAuthTr").style.display="";
    if(Get("idmEnabledRow"))
        Get("idmEnabledRow").style.display = "";
    
	document.getElementById("eapTimeOut").value=30;
	document.getElementById("eapRetries").value=3;
	
	if (accessMode==2) {
		//PPSK
		if (!isHHMApplication) {
			document.getElementById("hideUseGuestManagerDiv").style.display="block";
		}
		if($("#enableProvisionPersonal").attr("checked") == "checked"){
			$("#enableProvisionPersonal").attr("checked",false);
			$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		}
		if($("#enableProvisionEnterprise").attr("checked") == "checked"){
			$("#enableProvisionEnterprise").attr("checked",false);
			$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		}
		$("#enableCwpSelect").attr("disabled",false);
		$("#provisionSsidDiv").css("display","none");
		$("#hideRadiusPAPCHAP").css("display","none");
		$("#ssidProfilesFull_dataSource_macAuthEnabled").attr("checked",false);
		document.getElementById("provisionTr").style.display="none";
		document.getElementById("onboardProvisionEPA").style.display="none";
		document.getElementById("hidePskSelfReg").style.display="block";
		document.getElementById("macBindingTr").style.display="";
		document.getElementById("hidePskUserLimit").style.display="block";
		
	} else {
		document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked=false;
		document.getElementById("hidePskUserLimit").style.display="none";
		if (!document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			document.getElementById("hideRadiusPAPCHAP").style.display="none";
			document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
		}
		document.getElementById("hideUseGuestManagerDiv").style.display="none";
		document.getElementById("hidePskSelfReg").style.display="none";
		document.getElementById("macBindingTr").style.display="none";
		showHidePskSelfRegAdv(false);
		changePskMacBindingValue(false);
		
		document.getElementById("pskUserLimit").value='0';
		document.getElementById("pskUserLimit").readOnly=true;
		document.getElementById(formName + "_enabledPskUserLimit").checked=false;
		document.getElementById("blnMacBindingEnable").checked=false;
	}
	init_value();
	if (accessMode==1 || accessMode==2) {
		if(accessMode==1){
			if($("#enableProvisionPrivate").attr("checked") == "checked"){
				$("#enableProvisionPrivate").attr("checked",false);
				$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
				}
			if($("#enableProvisionEnterprise").attr("checked") == "checked"){
				$("#enableProvisionEnterprise").attr("checked",false);
				$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
			}
			document.getElementById("provisionTr").style.display="block";
			document.getElementById("onboardProvisionEPA").style.display="none";
			$("#hideRadiusPAPCHAP").css("display","none");
			$("#ssidProfilesFull_dataSource_macAuthEnabled").attr("checked",false);
			$("#ssidPPSKKey").val("");
		}
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=3;
		mgmtKey.options[0].value=KEY_MGMT_AUTO_WPA_OR_WPA2_PSK;
		mgmtKey.options[0].text='Auto-(WPA or WPA2)-PSK';
		mgmtKey.options[1].value=KEY_MGMT_WPA_PSK;
		mgmtKey.options[1].text='WPA-(WPA or Auto)-PSK';
		mgmtKey.options[2].value=KEY_MGMT_WPA2_PSK;
		mgmtKey.options[2].text='WPA2-(WPA2 Personal)-PSK';
		mgmtKey.options[2].selected=true;
		mgmtKey.value=mgmtKey.options[2].value;
		mgmtKey.text=mgmtKey.options[2].text;
		show_tt(KEY_MGMT_WPA2_PSK);
	} else if (accessMode==3) {
		if($("#enableProvisionPrivate").attr("checked") == "checked"){
			$("#enableProvisionPrivate").attr("checked",false);
			$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		}
		if($("#enableProvisionPersonal").attr("checked") == "checked"){
			$("#enableProvisionPersonal").attr("checked",false);
			$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		}
		document.getElementById("provisionTr").style.display="none";
		document.getElementById("onboardProvisionEPA").style.display="block";
		document.getElementById("hideEap").style.display="block";
		$("#hideRadiusPAPCHAP").css("display","none");
		$("#ssidProfilesFull_dataSource_macAuthEnabled").attr("checked",false);
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=3;
		mgmtKey.options[0].value=KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X;
		mgmtKey.options[0].text='Auto-(WPA or WPA2)-EAP (802.1X)';
		mgmtKey.options[1].value=KEY_MGMT_WPA_EAP_802_1_X;
		mgmtKey.options[1].text='WPA-(WPA or Auto)-802.1X';
		mgmtKey.options[2].value=KEY_MGMT_WPA2_EAP_802_1_X;
		mgmtKey.options[2].text='WPA2-(WPA2 Enterprise)-802.1X';
		mgmtKey.options[2].selected=true;
		mgmtKey.value=mgmtKey.options[2].value;
		mgmtKey.text=mgmtKey.options[2].text;
		show_tt(KEY_MGMT_WPA2_EAP_802_1_X);
	} else if (accessMode==4) {
		$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		document.getElementById("provisionTr").style.display="none";
		document.getElementById("onboardProvisionEPA").style.display="none";
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=2;
		mgmtKey.options[0].value=KEY_MGMT_WEP_PSK;
		mgmtKey.options[0].text='WEP';
		mgmtKey.options[0].selected=true;
		mgmtKey.value=mgmtKey.options[0].value;
		mgmtKey.text=mgmtKey.options[0].text;
		mgmtKey.options[1].value=KEY_MGMT_DYNAMIC_WEP;
		mgmtKey.options[1].text='WEP 802.1X';
		show_tt(KEY_MGMT_WEP_PSK);
	} else if (accessMode==5) {
		$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		document.getElementById("provisionTr").style.display="none";
		document.getElementById("onboardProvisionEPA").style.display="none";
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=1;
		mgmtKey.options[0].value=KEY_MGMT_OPEN;
		mgmtKey.options[0].text='Open';
		mgmtKey.options[0].selected=true;
		mgmtKey.value=mgmtKey.options[0].value;
		mgmtKey.text=mgmtKey.options[0].text;
		show_tt(KEY_MGMT_OPEN);
	}
	
	if (Get("mgmtKey").value==KEY_MGMT_OPEN || Get("mgmtKey").value==KEY_MGMT_WEP_PSK){ 
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("showOptionDiv").style.display="none";
	} else {
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
	}
	document.getElementById("hideAuthMethord").style.display="none";
	if (accessMode==1 || accessMode==2 || accessMode==3){
		document.getElementById("hideKeyManagement").style.display="";
	} else if (accessMode==4){
		document.getElementById("hideKeyManagement").style.display="";
		if (document.getElementById("mgmtKey").value==KEY_MGMT_DYNAMIC_WEP) {
			document.getElementById("hideAuthMethord").style.display="none";
		} else {
			document.getElementById("hideAuthMethord").style.display="block";
		}
	} else {
		document.getElementById("hideKeyManagement").style.display="none";
	}
	
	if(Get("enableIDMChk")) {
		enabledCloudAuth(Get("enableIDMChk").checked);
    }
	 checkIDMEnable(accessMode);
	 //added for Single SSID
	 checkSingleSSID(accessMode);
	 checkViewClientProfile(accessMode);
	 
	 if(accessMode==5) {
		 if((Get("enableIDMChk") && Get("enableIDMChk").checked) 
				 || (Get("enableCwpSelect") && Get("enableCwpSelect").checked)) {
			 resetCloudCwpAuth(true);
		 } else {
			 resetCloudCwpAuth();
		 }
	 } else {
		 resetCloudCwpAuth(true);
	 }
	</s:if>
	<s:else>
	wpa2Check();
	
	setSecurityExplainContext(accessMode);
	
	setCwpTitle();
    document.getElementById("cwpAuthTr").style.display="";
    document.getElementById("macAuthTr").style.display="";
    if(Get("idmEnabledRow"))
        Get("idmEnabledRow").style.display = "";
    
	document.getElementById("eapTimeOut").value=30;
	document.getElementById("eapRetries").value=3;
	
	if (accessMode==2) {
		// PPSK
		if (!isHHMApplication) {
			document.getElementById("hideUseGuestManagerDiv").style.display="block";
		}

		document.getElementById("hidePskSelfReg").style.display="block";
		document.getElementById("macBindingTr").style.display="";
		document.getElementById("hidePskUserLimit").style.display="block";
		
	} else {
		document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked=false;
		document.getElementById("hidePskUserLimit").style.display="none";
		if (!document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			document.getElementById("hideRadiusPAPCHAP").style.display="none";
			document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
		}
		document.getElementById("hideUseGuestManagerDiv").style.display="none";
		document.getElementById("hidePskSelfReg").style.display="none";
		document.getElementById("macBindingTr").style.display="none";
		showHidePskSelfRegAdv(false);
		changePskMacBindingValue(false);
		
		document.getElementById("pskUserLimit").value='0';
		document.getElementById("pskUserLimit").readOnly=true;
		document.getElementById(formName + "_enabledPskUserLimit").checked=false;
		document.getElementById("blnMacBindingEnable").checked=false;
	}
	init_value();
	if (accessMode==1 || accessMode==2) {
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=3;
		mgmtKey.options[0].value=KEY_MGMT_AUTO_WPA_OR_WPA2_PSK;
		mgmtKey.options[0].text='Auto-(WPA or WPA2)-PSK';
		mgmtKey.options[1].value=KEY_MGMT_WPA_PSK;
		mgmtKey.options[1].text='WPA-(WPA or Auto)-PSK';
		mgmtKey.options[2].value=KEY_MGMT_WPA2_PSK;
		mgmtKey.options[2].text='WPA2-(WPA2 Personal)-PSK';
		mgmtKey.options[2].selected=true;
		mgmtKey.value=mgmtKey.options[2].value;
		mgmtKey.text=mgmtKey.options[2].text;
		show_tt(KEY_MGMT_WPA2_PSK);
	} else if (accessMode==3) {
		document.getElementById("hideEap").style.display="block";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=3;
		mgmtKey.options[0].value=KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X;
		mgmtKey.options[0].text='Auto-(WPA or WPA2)-EAP (802.1X)';
		mgmtKey.options[1].value=KEY_MGMT_WPA_EAP_802_1_X;
		mgmtKey.options[1].text='WPA-(WPA or Auto)-802.1X';
		mgmtKey.options[2].value=KEY_MGMT_WPA2_EAP_802_1_X;
		mgmtKey.options[2].text='WPA2-(WPA2 Enterprise)-802.1X';
		mgmtKey.options[2].selected=true;
		mgmtKey.value=mgmtKey.options[2].value;
		mgmtKey.text=mgmtKey.options[2].text;
		show_tt(KEY_MGMT_WPA2_EAP_802_1_X);
	} else if (accessMode==4) {
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=2;
		mgmtKey.options[0].value=KEY_MGMT_WEP_PSK;
		mgmtKey.options[0].text='WEP';
		mgmtKey.options[0].selected=true;
		mgmtKey.value=mgmtKey.options[0].value;
		mgmtKey.text=mgmtKey.options[0].text;
		mgmtKey.options[1].value=KEY_MGMT_DYNAMIC_WEP;
		mgmtKey.options[1].text='WEP 802.1X';
		show_tt(KEY_MGMT_WEP_PSK);
	} else if (accessMode==5) {
		document.getElementById("hideEap").style.display="none";
		var mgmtKey = document.getElementById("mgmtKey");
		mgmtKey.length=0;
		mgmtKey.length=1;
		mgmtKey.options[0].value=KEY_MGMT_OPEN;
		mgmtKey.options[0].text='Open';
		mgmtKey.options[0].selected=true;
		mgmtKey.value=mgmtKey.options[0].value;
		mgmtKey.text=mgmtKey.options[0].text;
		show_tt(KEY_MGMT_OPEN);
	}
	
	if (Get("mgmtKey").value==KEY_MGMT_OPEN || Get("mgmtKey").value==KEY_MGMT_WEP_PSK){ 
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("showOptionDiv").style.display="none";
	} else {
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
	}
	document.getElementById("hideAuthMethord").style.display="none";
	if (accessMode==1 || accessMode==2 || accessMode==3){
		document.getElementById("hideKeyManagement").style.display="";
	} else if (accessMode==4){
		document.getElementById("hideKeyManagement").style.display="";
		if (document.getElementById("mgmtKey").value==KEY_MGMT_DYNAMIC_WEP) {
			document.getElementById("hideAuthMethord").style.display="none";
		} else {
			document.getElementById("hideAuthMethord").style.display="block";
		}
	} else {
		document.getElementById("hideKeyManagement").style.display="none";
	}
	
	if(Get("enableIDMChk")) {
		enabledCloudAuth(Get("enableIDMChk").checked);
    }
	
    if(accessMode==5) {
        if((Get("enableIDMChk") && Get("enableIDMChk").checked) 
                || (Get("enableCwpSelect") && Get("enableCwpSelect").checked)) {
            resetCloudCwpAuth(true);
        } else {
            resetCloudCwpAuth();
        }
    } else {
        resetCloudCwpAuth(true);
    }
	</s:else>
}
function checkViewClientProfile(model){
	if(model == 4 || model == 5){
		$(".viewClientProfile").hide();
	}
	if(model == 3){
		if(Get("enableProvisionEnterprise").checked){
			$('#enableProvisionEnterprise').siblings('a').show();
		}else{
			$(".viewClientProfile").hide();
		}
	}
	if(model == 2){
		if(Get("enableProvisionPrivate").checked){
			$('#enableProvisionPrivate').siblings('a').show();
		}else{
			$(".viewClientProfile").hide();
		}
	}
	if(model == 1){
		if(Get("enableProvisionPersonal").checked){
			$('#enableProvisionPersonal').siblings('a').show();
		}else{
			$(".viewClientProfile").hide();
		}
	}
}
function showHideAdvancePanelDiv(value){
	if (value==1) {
		document.getElementById("showAdvancePanelDiv").style.display="none";
		document.getElementById("hideAdvancePanelDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showAdvancePanelDiv").style.display="block";
		document.getElementById("hideAdvancePanelDiv").style.display="none";
	}
}

function showHideOptionDiv(value){
	if (value==1) {
		document.getElementById("showOptionDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
	}
}

function showHideSecurityDiv(value){
	if (value==1) {
		document.getElementById("showSecurityDiv").style.display="none";
		document.getElementById("hideSecurityDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showSecurityDiv").style.display="block";
		document.getElementById("hideSecurityDiv").style.display="none";
	}
}
function showHideRadioRateDiv(value){
	if (value==1) {
		document.getElementById("showRadioRateDiv").style.display="none";
		document.getElementById("hideRadioRateDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showRadioRateDiv").style.display="block";
		document.getElementById("hideRadioRateDiv").style.display="none";
	}
}
function showHideProvisionSsid(ele,checked){
	if(checked){
		$("#provisionSsidDiv").css("display","block");
		$("#enableCwpSelect").attr("checked",false); 
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("checked",false);
			$("#enableIDMChk").attr("disabled",true);
		}
		$("#enableCwpSelect").attr("disabled",true);
		$("#enablemdmselect").css("display","none");
		$("#ssidProfilesFull_configmdmId").prepend("<option value='-1' selected='selected'>None available</option>");
		$("#ssidProfilesFull_dataSource_enableMDM").attr("checked",false);
		$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",true);
	}else{
		$("#provisionSsidDiv").css("display","none");
		$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		$("#enableCwpSelect").attr("disabled",false);
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("disabled",false);
		}
		$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",false);
		$("#ssidPPSKKey").val("");
	}
	handleViewClientProfile(ele,checked);
}
function hideCwpProSelected(ele,checked){
	if(checked){
	    var ppskAccessMode = document.getElementById("accessMode2").checked;
		$("#enableCwpSelect").attr("checked",false); 
		$("#enableCwpSelect").attr("disabled",true);
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("disabled", ppskAccessMode ? false : true);
		}
		$("#ssidProfilesFull_dataSource_enableMDM").attr("checked",false);
		$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",true);
		$("#enablemdmselect").css("display","none");
		$("#ssidProfilesFull_configmdmId").prepend("<option value='-1' selected='selected'>None available</option>");
	}else{
		$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
		$("#enableCwpSelect").attr("disabled",false);
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("disabled",false);
		}
		$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",false);
	}
	// add to handle Single SSID
	handleSingleSsidCheckBox(checked);
	handleViewClientProfile(ele,checked);
}
function hideSingleSsidCheckBox(tag){
		$("#enableSingleSSIDDiv").hide(500);
		$("#enableSingleSSIDKeyDiv").hide(500);
		$("#enableSingleSSID").attr("checked",false);
		$("#singleSsidValue").val("");
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",false);
		$("#privateSsidmodel1").attr('disabled',true);
		$("#enableProvisionPrivate").attr('checked',false);
		$("#openSsidDiv").show();
		$("#singleSsidDes").show();
		$("#singleSsidDIV").hide();
		$("#privateSsidmodel0").attr('checked','true');
}
function handleSingleSsidCheckBox(tag){
	if(tag){
		$("#privateSsidmodel1").attr("disabled",false);
		if($("#privateSsidmodel1").is(":checked")){
//			$("#singleSsidDes").show(100);
			$("#singleSsidDIV").show(100);
			$("#openSsidDiv").hide(100);
		}
	}else{
		$("#enableSingleSSID").attr("checked",false);
		$("#singleSsidValue").val("");
		$("#enableSingleSSIDDiv").hide(500);
		$("#enableSingleSSIDKeyDiv").hide(500);
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",false);
		$("#privateSsidmodel1").attr("disabled",true).attr('checked',false);
		$("#singleSsidDes").show(100);
		$("#singleSsidDIV").hide(100);
		$("#privateSsidmodel0").attr("disabled",false).attr('checked',true);
		$("#openSsidDiv").show(100);
		
	}
}
function checkSingleSSID(mode){
	if(mode == 2){
		$("#enableSingleSSID").attr("checked", false);
		$("#singleSsidValue").val("");
		$("#enableSingleSSIDDiv").hide(500);
		$("#enableSingleSSIDKeyDiv").hide(500);
	}
}
function hideSingleSsidKey(tag){
	if(tag){
		$("#enableSingleSSIDKeyDiv").show(500);
	}else{
		$("#enableSingleSSIDKeyDiv").hide(500);
	}
	//add to handle Retistration SSID input text field
	if(tag){
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",true);
	}else{
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",false);
	}
}
//added for new single ssid ui
function changeSsidModel(model){
	if(model == 0){
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",false);
		$("#singleSsidDes").show(100);
		$("#singleSsidDIV").hide(100);
		$("#openSsidDiv").show(100);
	}
	if(model == 1){
		$("#openSsidDiv").hide(100);
		$("#singleSsidDes").hide(100);
		$("#singleSsidDIV").show(100);
		$("#ssidProfilesFull_dataSource_ppskOpenSsid").attr("disabled",true);
	}
}
function showDefaultOption(){
	document.getElementById("showOptionDiv").style.display="none";
	document.getElementById("hideOptionDiv").style.display="block";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ssidProfilesFull" includeParams="none"/>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedSsidName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedSsidName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}

function handleSlefOnboardWithCwp(checked){
	if(checked){
		if(Get("enableCwpSelect").checked == true){
			$("#enableProvisionPrivate").attr("checked",false);
			$("#enableProvisionPrivate").attr("disabled",true);
		}else{
			 $("#enableProvisionPrivate").attr("disabled",false);
		}
	}else{
		$("#enableProvisionPrivate").siblings("a").hide();
		$("#enableProvisionPrivate").attr("checked",false);
		$("#enableProvisionPrivate").attr("disabled",false);
		$("#enableCwpSelect").attr("disabled",false);
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("disabled",false);
		}
	}
}
function showHidePskSelfRegAdv(checked){
	var ppskEle = document.getElementById("hidePskSelfRegAdv");
	handleSlefOnboardWithCwp(checked);
	if(checked){
		ppskEle.style.display="";
		Get("blnMacBindingEnable").checked=false;
	}else{
		/*  $("#enableCwpSelect").attr("disabled",false);
		$("#enableCwpSelect").attr("checked",false);
		$("#enableProvisionPrivate").attr("disabled",false);
		$("#enableProvisionPrivate").attr("checked",false); */ 
		$("#ssidProfilesFull_dataSource_enableMDM").attr("disabled",false);
		ppskEle.style.display="none";
		document.getElementById(formName + "_dataSource_enablePpskSelfReg").checked=false;
		document.getElementById(formName + "_dataSource_ppskOpenSsid").value="";
		$("#ssidProfilesFull_configmdmId option[value='-1']").remove();
	}
	//add for single ssid
	hideSingleSsidCheckBox(checked);
}

function changePskMacBindingValue(checked) {
	if (checked) {
		showHidePskSelfRegAdv(false);
		if(<s:property value="%{usabledIDM}"/>){
			$("#enableIDMChk").attr("disabled",false);
		}
	}
}

function validatePpskSelfReg(){
	var accessModel = document.getElementById("accessMode2");
	var ppskSelfEnable = document.getElementById(formName + "_dataSource_enablePpskSelfReg");
	if(accessModel.value == ACCESS_MODE_PSK && ppskSelfEnable.checked){
		
		var ppskOpenSsid = document.getElementById(formName + "_dataSource_ppskOpenSsid");
		if(document.getElementById('privateSsidmodel0').checked){
			if(ppskOpenSsid.value.length == 0){
				hm.util.reportFieldError(ppskOpenSsid, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.ppskreg.regSsid" /></s:param></s:text>');
				return false;
			}
		} else {
			var ppskSingleSsid = document.getElementById("singleSsidValue");
			if(ppskSingleSsid.value.length == 0){
				hm.util.reportFieldError(ppskSingleSsid, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.enable.single.ssid.value.label" /></s:param></s:text>');
				return false;
			}
			if(ppskSingleSsid.value.length < 8) {
				hm.util.reportFieldError(ppskSingleSsid, '<s:text name="error.required.field.min.length.warn"><s:param><s:text name="config.ssid.enable.single.ssid.value.label" /></s:param><s:param>8</s:param></s:text>');
				return false;
			}
			return true;
		}
	}
	
	return true;
}

function onLoadForJson(){
	<s:if test="%{updateDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
	onLoadPage();
}

<s:if test="%{blnJsonMode==true}">
	window.setTimeout("onLoadForJson()", 100);
</s:if>


function convtounicastClick(element){
	if(element.checked && element.value==1){
		hm.util.show("tr_channelUtiThreshold");
		hm.util.show("tr_membership");
	}
	else{
		hm.util.hide("tr_channelUtiThreshold");
		hm.util.hide("tr_membership");
	}
}
function validateElemet(inputElement,label ,min, max) {
	var firstradio=document.getElementsByName("dataSource.convtounicast")[0];
	if(!(firstradio&&firstradio.checked)){
		return true;
	}
	var message = hm.util.validateString(inputElement.value, label);
	if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
	}
	message = hm.util.validateIntegerRange(inputElement.value, label, min, max);
	if (message != null) {
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	}
	return true;
}

function disabledCWP(flag) {
	document.getElementById("enableCwpSelect").disabled = flag;
}
function disabledOnboarding(flag,accessMode){
	if(flag){
		if(document.getElementById("accessMode1").checked){
			$("#enableProvisionPersonal").attr("checked",false);
			$("#enableProvisionPersonal").attr("disabled",true);
		}
		if(document.getElementById("accessMode2").checked){
		  //do nothing
		}
		if(document.getElementById("accessMode3").checked){
			$("#enableProvisionEnterprise").attr("checked",false);
			$("#enableProvisionEnterprise").attr("disabled",true);
		}
	}else{
		if(document.getElementById("accessMode1").checked){
			if(Get("enableCwpSelect").checked == false){
				$("#enableProvisionPersonal").attr("disabled",false)
			}else{
				$("#enableProvisionPersonal").attr("checked",false);
				$("#enableProvisionPersonal").attr("disabled",true);
			}
		}
		if(document.getElementById("accessMode2").checked){
			if(Get("enableCwpSelect").checked == false){
				$("#enableProvisionPrivate").attr("disabled",false);
			}else{
				$("#enableProvisionPrivate").attr("checked",false);
				$("#enableProvisionPrivate").attr("disabled",true);
			}
		}
		if(document.getElementById("accessMode3").checked){
			if(Get("enableCwpSelect").checked == false){
				$("#enableProvisionEnterprise").attr("disabled",false);
			}else{
				$("#enableProvisionEnterprise").attr("checked",false);
				$("#enableProvisionEnterprise").attr("disabled",true);
			}
		}
	}
}

function getAccessModeType(){
	for(var i = 1 ; i<=5 ; i++){
		if(Get("accessMode"+i).checked){
			return i;
		}
	}
}


function enabledCloudAuth(flag) {
	<s:if test="enableClientManagement==true">
		disabledOnboarding(flag);
		if(getAccessModeType() == 1){
			if(Get("enableProvisionPersonal").checked){
				disabledCWP(true);
			}else{
				disabledCWP(false);
			} 
		}
		if(getAccessModeType() == 2){
			if(Get("enableProvisionPrivate").checked){
				disabledCWP(true);
			}else{
				disabledCWP(false);
			} 
		}
		if(getAccessModeType() == 3){
			if(Get("enableProvisionEnterprise").checked){
				disabledCWP(true);
			}else{
				disabledCWP(false);
			} 
		}
		if(getAccessModeType() == 4 || getAccessModeType() == 5){
				disabledCWP(false);
		}
	</s:if>
	<s:else>
		disabledCWP(false);
	</s:else>
	if(document.getElementById("accessMode2").checked) {
		// Private PSK
	    if(flag) {
			document.getElementById(formName + "_dataSource_macAuthEnabled").checked = false;
	        document.getElementById("macAuthTr").style.display="none";
	        if (!isHHMApplication) {
	            document.getElementById("hideUseGuestManagerDiv").style.display="none";
	        }
	
	        document.getElementById("hidePskSelfReg").style.display="block";
	        document.getElementById("macBindingTr").style.display="none";
	        //document.getElementById("hidePskUserLimit").style.display="none";   
	        document.getElementById("hideRadiusPAPCHAP").style.display="none";
	    } else {
	        document.getElementById("macAuthTr").style.display="";
	        if (!isHHMApplication) {
	            document.getElementById("hideUseGuestManagerDiv").style.display="block";
	        }
	
	        document.getElementById("hidePskSelfReg").style.display="block";
	        document.getElementById("macBindingTr").style.display="";
	        //document.getElementById("hidePskUserLimit").style.display="block";	
	    }
	} else if(document.getElementById("accessMode3").checked) {
		// 802.1x
		if(flag) {
	         document.getElementById(formName + "_dataSource_macAuthEnabled").checked = false;
	         document.getElementById("macAuthTr").style.display="none";
	         document.getElementById("hideRadiusPAPCHAP").style.display="none";
		} else {
	         document.getElementById("macAuthTr").style.display="";
		}
	} else if(document.getElementById("accessMode1").checked || document.getElementById("accessMode5").checked) {
		// PSK, Open
		if(flag) {
			document.getElementById(formName + "_dataSource_macAuthEnabled").checked = false;
			document.getElementById("enableCwpSelect").checked = true;
			
			document.getElementById("macAuthTr").style.display="none";
			document.getElementById("hideRadiusPAPCHAP").style.display="none";
			
			disabledCWP(true);
		} else {
	        document.getElementById("macAuthTr").style.display="";
	        document.getElementById("cwpAuthTr").style.display="";
		}
	} else {
		// WEP
		if(flag) {
			document.getElementById(formName + "_dataSource_macAuthEnabled").checked = false;
			document.getElementById("enableCwpSelect").checked = true;
			
			document.getElementById("macAuthTr").style.display="none";
			document.getElementById("hideRadiusPAPCHAP").style.display="none";
			
			if(Get("mgmtKey").value==KEY_MGMT_WEP_PSK) {
				disabledCWP(true);
			}
		} else {
	        document.getElementById("macAuthTr").style.display="";
		}
	}
	if(Get("manageGuestIDMAnchor")) {
		Get("manageGuestIDMAnchor").style.display = (flag ? "" : "none");
	}
	if (Get("accessMode5").checked) {
		resetCloudCwpAuth(flag || Get("enableCwpSelect").checked);
	} else {
		resetCloudCwpAuth(true);
	}
}


function enableWmmAdmctlDisplayStyle(checked){
	Get(formName +"_dataSource_enabledUnscheduled").disabled = !checked;
	//Get(formName +"_dataSource_enabledAcBesteffort").disabled = !checked;
	//Get(formName +"_dataSource_enabledAcBackground").disabled = !checked;
	Get(formName +"_dataSource_enabledAcVideo").disabled = !checked;
	
	Get(formName +"_enabledAcVoice").disabled = !checked;
	
	Get(formName +"_dataSource_enabledwmm").value = checked;
	Get(formName +"_dataSource_enabledAcVoice").value = checked;
	
	if(!checked){
		Get(formName +"_dataSource_enabledUnscheduled").checked = false;
		//Get(formName +"_dataSource_enabledAcBesteffort").checked = false;
		Get(formName +"_dataSource_enabledAcVideo").checked = false;
		Get(formName +"_enabledAcVoice").checked = false;
		//Get(formName +"_dataSource_enabledAcBackground").checked = false;
	}
}

function validateVoiceEnterprise(){
	if(Get(formName +"_dataSource_enabledVoiceEnterprise").checked){		
		var inputElementInfo = Get(formName +"_dataSource_enabledAcVoice");
		if(Get(formName +"_enabledAcVoice").checked && Get(formName +"_enabledwmm").checked){
			return true;
		}else{
			hm.util.reportFieldError(inputElementInfo, '<s:text name="error.voice.enterprise.wmmac.notenabled"></s:text>');
			showHideAdvancePanelDiv(1);
			Get(formName +"_dataSource_enabledVoiceEnterprise").focus();
			return false;
		}
	}
	return true;
}

function enableMDMcheck(checked) {
	if (checked) {
		Get("enablemdmselect").style.display="block";
	} else {
		Get("enablemdmselect").style.display="none";
		//Get(formName + "_routingPolicyId").value=-1;
	}
}

function changeEnabled80211v(checked){
	if(!checked){
		Get(formName +"_dataSource_enabledVoiceEnterprise").checked = false;
		Get(formName +"_dataSource_enabled80211v").value = false;
	}else{
		Get(formName +"_dataSource_enabled80211v").value = true;
	}
}

function changeEnabled80211r(checked){
	if(!checked){
		Get(formName +"_dataSource_enabledVoiceEnterprise").checked = false;
		Get(formName +"_dataSource_enabled80211r").value = false;
	}else{
		Get(formName +"_dataSource_enabled80211r").value = true;
	}
}	

function changeEnabled80211k(checked){
	if(!checked){
		Get(formName +"_dataSource_enabledVoiceEnterprise").checked = false;
		Get(formName +"_dataSource_enabled80211k").value = false;
	}else{
		Get(formName +"_dataSource_enabled80211k").value = true;
	}
}

//802.11r is just enabled for wpa2, so need to check
function wpa2Check(){
    var showFlag = false;
    if(Get("accessMode1").checked){
        if(Get("mgmtKey").value==KEY_MGMT_AUTO_WPA_OR_WPA2_PSK || Get("mgmtKey").value==KEY_MGMT_WPA2_PSK){
            showFlag = true;
        }   
    }else if(Get("accessMode2").checked){
        if(Get("mgmtKey").value==KEY_MGMT_WPA2_PSK || Get("mgmtKey").value==KEY_MGMT_AUTO_WPA_OR_WPA2_PSK){
            showFlag = true;
        }           
    }else if(Get("accessMode3").checked){
        if(Get("mgmtKey").value==KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X || Get("mgmtKey").value==KEY_MGMT_WPA2_EAP_802_1_X){
            showFlag = true;
        }           
    }
    
    if(!showFlag){
        Get(formName +"_dataSource_enabledVoiceEnterprise").checked = false;
        Get(formName +"_enabled80211r").checked = false;
        Get(formName +"_dataSource_enabled80211r").value = false;
    }
    
    Get(formName +"_dataSource_enabledVoiceEnterprise").disabled = !showFlag;
    Get(formName +"_dataSource_enabled80211r").disabled = !showFlag;
    Get(formName +"_enabled80211r").disabled = !showFlag;

    if(Get(formName +"_dataSource_enabledVoiceEnterprise").checked){
    	 Get(formName +"_enabledwmm").checked = true;
		 Get(formName +"_enabledwmm").disabled = true;
		 Get(formName +"_dataSource_enabledwmm").value = true;

		 Get(formName +"_enabled80211k").checked = true;
		 Get(formName +"_enabled80211v").checked = true;
		 Get(formName +"_enabled80211r").checked = true;
		 
		 Get(formName +"_dataSource_enabled80211k").value = true;
		 Get(formName +"_dataSource_enabled80211v").value = true;
		 Get(formName +"_dataSource_enabled80211r").value = true;
		 
		 Get(formName +"_enabled80211k").disabled = true;
		 Get(formName +"_enabled80211v").disabled = true;
		 Get(formName +"_enabled80211r").disabled = true;
		 
		 Get(formName +"_enabledAcVoice").checked = true;
		 Get(formName +"_enabledAcVoice").disabled = true;
		 Get(formName +"_dataSource_enabledAcVoice").value = true;
    }else{
    	Get(formName +"_enabled80211k").disabled = false;
     	Get(formName +"_enabled80211v").disabled = false;
     	
		Get(formName +"_enabledwmm").disabled = false;
    }
}

function initStreamSlider() {
	// initial constuct
	for(var i = 1; i <= 3; i ++){
		$("#stream_slider_" + i).attr("index",i);
 		$("#stream_slider_" + i).slider({
 			range: "min",
 			min: 7,
 			max: 9,
 			slide: function( event, ui ) {
 				var indexId = $(this).attr("index");
 				$("#hidden_mcsValue_" + indexId).val(ui.value);
 				$("#mcsValue_" + indexId).text(ui.value);
 				var dataRate = "", vhtDataRate = "";
 					if(indexId == 1){
 						switch(ui.value){
							case 7:
								dataRate = STREAM_SINGLE_RATE_MCS_7;
								vhtDataRate = VHT_STREAM_SINGLE_RATE_MCS_7;
								break;
							case 8:
								dataRate = STREAM_SINGLE_RATE_MCS_8;
								vhtDataRate = VHT_STREAM_SINGLE_RATE_MCS_8;
								break;
							case 9:
								dataRate = STREAM_SINGLE_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_SINGLE_RATE_MCS_9;
								break;
							default:
								dataRate = STREAM_SINGLE_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_SINGLE_RATE_MCS_9;
								break;
						}
 					}
 					
 					if(indexId == 2){
 						switch(ui.value){
							case 7:
								dataRate = STREAM_TWO_RATE_MCS_7;
								vhtDataRate = VHT_STREAM_TWO_RATE_MCS_7;
								break;
							case 8:
								dataRate = STREAM_TWO_RATE_MCS_8;
								vhtDataRate = VHT_STREAM_TWO_RATE_MCS_8;
								break;
							case 9:
								dataRate = STREAM_TWO_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_TWO_RATE_MCS_9;
								break;
							default:
								dataRate = STREAM_TWO_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_TWO_RATE_MCS_9;
								break;
						}
 					}
 					
 					if(indexId == 3){
 						switch(ui.value){
							case 7:
								dataRate = STREAM_THREE_RATE_MCS_7;
								vhtDataRate = VHT_STREAM_THREE_RATE_MCS_7;
								break;
							case 8:
								dataRate = STREAM_THREE_RATE_MCS_8;
								vhtDataRate = VHT_STREAM_THREE_RATE_MCS_8;
								break;
							case 9:
								dataRate = STREAM_THREE_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_THREE_RATE_MCS_9;
								break;
							default:
								dataRate = STREAM_THREE_RATE_MCS_9;
								vhtDataRate = VHT_STREAM_THREE_RATE_MCS_9;
								break;
						}
 					}
 						
 					$("#dataRate_" + indexId).text("(Up to " + vhtDataRate + " for 2.4 GHz and "+ dataRate +" for 5 GHz)");
 				
 			}
 			
 		});
 		$("#stream_slider_" + i).slider("option", "value", $("#mcsValue_" + i).text());
 		$("#hidden_mcsValue_" + i).val($("#stream_slider_" + i).slider("value"));
 		
 		if(i == 2){
 			Get("stream_state_" + 1).checked = true;
 			Get("stream_state_" + 1).disabled = true;
 			Get("streamEnable_" + 1).value == "true";
 			$("#stream_slider_" + 1).slider("option", "disabled", false);
 		}else if(i == 3){
 			Get("stream_state_" + 1).checked = true;
 			Get("stream_state_" + 1).disabled = true;
 			Get("streamEnable_" + 1).value == "true";
 			
 			
 			if(Get("streamEnable_" + i).value == "true"){
 				Get("streamEnable_" + 2).value == "true";
 				Get("stream_state_" + 2).checked = true;
 	 			Get("stream_state_" + 2).disabled = true;
 	 			$("#stream_slider_" + 2).slider("option", "disabled", false);
 			}else{
 				Get("stream_state_" + 2).disabled = false;
 			}
 		}
 		
 		if(Get("streamEnable_" + i).value == "true"){
 			$("#stream_slider_" + i).slider("option", "disabled", false);
 		}else{
 			$("#stream_slider_" + i).slider("option", "disabled", true);
 		}
	}
	changeACRateSet(Get("enableACRateSet").checked);
}

function change11acMcs(checked, containerId){
	if(checked){
		$("#stream_slider_" + containerId).slider("option", "disabled", false);
	}else{
		$("#stream_slider_" + containerId).slider("option", "disabled", true);
	}
	$("#streamEnable_" + containerId).val(checked);
	
	if(containerId == 3){
		if(checked){
			$("#streamEnable_" + 1).val(checked);
			$("#streamEnable_" + 2).val(checked);
			$("#streamEnable_" + containerId).val(checked);
			
			Get("stream_state_" + 1).checked = true;
 			Get("stream_state_" + 1).disabled = true;
 			Get("stream_state_" + 2).checked = true;
 			Get("stream_state_" + 2).disabled = true;
 			$("#stream_slider_" + 2).slider("option", "disabled", false);
		}else{
 			Get("stream_state_" + 2).disabled = false;
		}
	}
	
	if(containerId == 2){
		if(checked){
			Get("stream_state_" + 3).disabled = false;
		}
	}
}

function changeACRateSet(checked) {
	if(checked){
		if(Get("stream_state_" + 3).checked){
			$("#streamEnable_" + 2).val(checked);
			$("#streamEnable_" + 3).val(checked);
			
 			Get("stream_state_" + 2).checked = true;
 			Get("stream_state_" + 2).disabled = true;
 			Get("stream_state_" + 3).checked = true;
 			
 			$("#stream_slider_" + 2).slider("option", "disabled", false);
 			$("#stream_slider_" + 3).slider("option", "disabled", false);
		}else if(Get("stream_state_" + 2).checked){
			Get("stream_state_" + 2).disabled = false;
			$("#streamEnable_" + 2).val(checked);
			Get("stream_state_" + 2).checked = true;
 			$("#stream_slider_" + 2).slider("option", "disabled", false);
 			
		}else{
			Get("stream_state_" + 2).disabled = false;
			
		}
		Get("stream_state_" + 3).disabled = false;
		$("#streamEnable_" + 1).val(checked);
		Get("stream_state_" + 1).checked = true;
		Get("stream_state_" + 1).disabled = true;
		$("#stream_slider_" + 1).slider("option", "disabled", false);
	}else{
		for (var i = 1; i <= 3; i ++) {
			Get("stream_state_" + i).disabled =!checked;
			$("#stream_slider_" + i).slider("option", "disabled", !checked);
		}
	}
}
</script>
<div id="content">
<s:form action="ssidProfilesFull" id="ssidProfilesFull" name="ssidProfilesFull">
	<s:hidden name="selectDosType" />
	<s:hidden name="blnMacAuth" />
	<s:hidden name="keyManagement" />
	<s:hidden name="editMacFilterId" />
	<s:hidden name="fromObjId" />
	<s:hidden name="manualLstForward" />
	<s:if test="%{blnJsonMode==true}">
		<s:hidden name="id" />
	</s:if>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{blnJsonMode==false}">
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
							class="button" id="updateSSIDButton"
							onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button" id="cancelSSIDButton"
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
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{blnJsonMode == true}">
				<table border="0" cellspacing="0" cellpadding="0" width="860px" id="ssidEditTable">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="860px" id="ssidEditTable">
			</s:else>
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
									<td class="labelT1" width="165px">
										<s:text name="config.ssid.ssidName" /><font color="red"><s:text name="*"/></font>
									</td>
									<td><s:textfield name="dataSource.ssidName" size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{ssidNameLength}" disabled="%{disabledName}" 
										onchange="changeSsidName();"/>&nbsp;<s:text name="config.ssid.ssidName_range" />
									</td>
								</tr>
								<tr>
									<td class="labelT1">
										<s:text name="config.ssid.head.ssid" /><font color="red"><s:text name="*"/></font>
									</td>
									<td><s:textfield name="dataSource.ssid" size="24"
										onkeypress="return hm.util.keyPressPermit(event,'ssid');"
										maxlength="%{ssidNameLength}"/>&nbsp;<s:text name="config.ssid.ssidName_range" />
									</td>
								</tr>
								<tr>
									<td class="labelT1" width="165px"><s:text name="config.ssid.radioRate.radioMode"></s:text></td>
									<td><s:select name="dataSource.radioMode" list="%{enumRadioMode}" listKey="key"
										listValue="value" cssStyle="width: 250px;" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.ssid.description" /></td>
									<td><s:textfield name="dataSource.comment" size="50"
										maxlength="64" />&nbsp;<s:text
										name="config.ssid.description_range" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="3"></td>
						</tr>
						<tr>
							<td style="padding: 4px 4px 4px 4px;"> 
								<fieldset><legend><s:text name="config.ssid.accessSecurity" /></legend>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr><td height="6px"></td></tr>
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td align="left">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td width="190px"><s:radio label="Gender" id="accessMode"
																			name="dataSource.accessMode"
																			list="#{1:'WPA/WPA2 PSK (Personal)'}"
																			value="%{dataSource.accessMode}"
																			onclick="changeAccessMode(1);" /></td>
																<td width="100px"><s:radio label="Gender" id="accessMode"
																			name="dataSource.accessMode"
																			list="#{2:'Private PSK'}"
																			value="%{dataSource.accessMode}"
																			onclick="changeAccessMode(2);" /></td>
																<td width="220px" id="accessMode8021xTd">
																		<s:radio label="Gender" id="accessMode"
																			name="dataSource.accessMode"
																			list="#{3:'WPA/WPA2 802.1X (Enterprise)'}"
																			value="%{dataSource.accessMode}"
																			onclick="changeAccessMode(3);" /></td>
															</tr>
															<tr>
																<td colspan="3" nowrap="nowrap" width="100%">
																	<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td>
																				<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																					<tr>
																						<td class="sepLine">
																							<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
																						</td>
																					</tr>
																				</table>
																			</td>
																			<td width="45px" align="center">
																				<table  cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td>
																							<font style="font-size: 10px; color:#474646">Secure</font>
																						</td>
																					</tr>
																				</table>
																			</td>
																			<td>
																				<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																					<tr>
																						<td class="sepLine">
																							<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
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
													<td align="right" width="140px" style="padding-right: 80px">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td width="60px" align="left"><s:radio label="Gender" id="accessMode"
																			name="dataSource.accessMode"
																			list="#{4:'WEP'}"
																			value="%{dataSource.accessMode}"
																			onclick="changeAccessMode(4);" /></td>
																<td width="60px" align="center"><s:radio label="Gender" id="accessMode"
																			name="dataSource.accessMode"
																			list="#{5:'Open'}"
																			value="%{dataSource.accessMode}"
																			onclick="changeAccessMode(5);" /></td>
															</tr>
															<tr>
																<td colspan="2" nowrap="nowrap" width="100%">
																	<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td>
																				<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																					<tr>
																						<td class="sepLine">
																							<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
																						</td>
																					</tr>
																				</table>
																			</td>
																			<td width="60px" align="center">
																				<table  cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td>
																							<font style="font-size: 10px; color:#474646">Not Secure</font>
																						</td>
																					</tr>
																				</table>
																			</td>
																			<td>
																				<table  cellspacing="0" cellpadding="0" border="0" width="100%">
																					<tr>
																						<td class="sepLine">
																							<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
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
									<tr>
										<td>
											<div style="display:block" id="hideSecurityExplain">
												<table  cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td>
															<label id="securityExplainContext"></label>
														</td>
													</tr>
													<tr>
														<td height="5">
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr id="idmEnabledRow">
									   <td>
									       <div>
									       <table cellpadding="0" cellspacing="0" border="0" width="100%">
									       <tr style="line-height: 2em;">
									           <td width="160px"/>
									           <td id="idmContent">
									           <s:if test="%{usabledIDM}">
									           <s:checkbox id="enableIDMChk" name="dataSource.enabledIDM" onclick="enabledCloudAuth(this.checked);"/>
									           </s:if>
									           <span class="icon-idm <s:if test="%{!usabledIDM}">icon-idm-disable</s:if>">
									           <label for="enableIDMChk" class="text-idm <s:if test="%{!usabledIDM}">text-idm-disable</s:if>"
									           <s:if test="%{!usabledIDM}">title="<s:text name='warn.cloudauth.guide.register'/>"</s:if>
									           ><s:text name="config.radiusProxy.cloudAuth.use"/></label>
									           </span>&nbsp;
									           <s:if test="%{usabledIDM}">
									           <a id="manageGuestIDMAnchor" style="display: none;" href="<s:property value='manageGuestLink4IDM'/>" tabindex="-1" target="_blank"><s:text name="config.ssid.idm.guest.link.text"/></a>
									           </s:if>
									           <s:elseif test="%{allowedTrial}">
									           <a id="trialIDMAnchor" href="javascript: void(0);" tabindex="-1" title="<s:text name='config.ssid.idm.trial.link.desc'/>"><s:text name="config.ssid.idm.trial.link.text"/></a>&nbsp;&nbsp;
									           <a id="idmexplaination" href="javascript: void(0);" tabindex="-1" title="<s:text name='config.ssid.idm.desc'/>">?</a>
									           </s:elseif>
									           <!-- a href="<s:property value='helpLink4IDM'/>" target="_blank"><s:text name="config.radiusProxy.cloudAuth.help.link"/></a-->
									           </td>
									       </tr>
									       </table>
									       </div>
									   </td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="hideKeyManagement"/>" id="hideKeyManagement">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="config.ssid.keyManagement" /></td>
													<td><s:select id="mgmtKey" name="dataSource.mgmtKey"
														list="%{enumKeyMgmt}" listKey="key" listValue="value"
														value="dataSource.mgmtKey"
														onchange="show_tt(this.options[this.selectedIndex].value);"
														cssStyle="width: 280px;" /></td>
												</tr>
												<tr id="keymgtNoteTr" style="display:<s:property value="hideKeyManagementNote"/>">
													<td colspan="2" class="noteInfo" style="padding: 4px 0 4px 10px"><s:text name="config.ssid.keymanagement.note"></s:text></td>
												</tr>

												<tr>
													<td class="labelT1"><s:text
														name="config.ssid.encriptionMethord" /></td>
													<td><s:if test="%{dataSource.mgmtKey == 0}">
														<s:select id="enc" name="dataSource.encryption"
															list="#{'0':'NONE'}" value="dataSource.encryption"
															onchange="show_fourth(this.options[this.selectedIndex].value);"
															cssStyle="width: 280px;" />
													</s:if> <s:elseif
														test="%{dataSource.mgmtKey == 1 || dataSource.mgmtKey == 2 || dataSource.mgmtKey == 3 || dataSource.mgmtKey == 4}">
														<s:select id="enc" name="dataSource.encryption"
															list="#{'1':'CCMP (AES)', '2':'TKIP'}"
															value="dataSource.encryption"
															onchange="show_fourth(this.options[this.selectedIndex].value);"
															cssStyle="width: 280px;" />
													</s:elseif> <s:elseif
														test="%{dataSource.mgmtKey == 5 || dataSource.mgmtKey == 6}">
														<s:select id="enc" name="dataSource.encryption"
															list="#{'5':'Auto-TKIP or CCMP (AES)'}"
															value="dataSource.encryption"
															onchange="show_fourth(this.options[this.selectedIndex].value);"
															cssStyle="width: 280px;" />
													</s:elseif> <s:else>
														<s:select id="enc" name="dataSource.encryption"
															list="#{'3':'WEP 104', '4':'WEP 40'}"
															value="dataSource.encryption"
															onchange="show_fourth(this.options[this.selectedIndex].value);"
															cssStyle="width: 280px;" />
													</s:else></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="hideAuthMethord"/>" id="hideAuthMethord">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="config.ssid.authenticationMethord" />
													</td>
													<td><s:if
														test="%{dataSource.mgmtKey == 0 || dataSource.mgmtKey == 2 || dataSource.mgmtKey == 4 || dataSource.mgmtKey == 6}">
														<s:select id="aut" name="dataSource.authentication"
															list="#{'0':'OPEN'}" value="dataSource.authentication"
															cssStyle="width: 280px;" />
													</s:if> <s:elseif
														test="%{dataSource.mgmtKey == 1 || dataSource.mgmtKey == 3 || dataSource.mgmtKey == 5 || dataSource.mgmtKey == 8}">
														<s:select id="aut" name="dataSource.authentication"
															list="#{'1':'EAP (802.1X)'}" value="dataSource.authentication"
															cssStyle="width: 280px;" />
													</s:elseif> <s:else>
														<s:select id="aut" name="dataSource.authentication"
															list="#{'0':'OPEN', '2':'SHARED'}"
															value="dataSource.authentication" cssStyle="width: 280px;" />
													</s:else></td>
													
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="hideThird"/>"
												id="hideThird">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="labelT1" width="150px"><s:text
															name="config.ssid.keyType" /></td>
														<td><s:select id="keyType3" name="keyType3"
															list="#{'0':'ASCII Key', '1':'Hex Key'}"
															onchange="show_keyType3(this.options[this.selectedIndex].value);"
															value="keyType3" cssStyle="width:280px;" /></td>
													</tr>
												</table>
											</div>
												<div style="display:<s:property value="hideThird_one"/>"
													id="hideThird_one">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
															<td><s:password id="firstKeyValue0" name="firstKeyValue0"
																size="50" maxlength="63" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
																<s:textfield id="firstKeyValue0_text" name="firstKeyValue0"
																size="50" maxlength="63" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'ssid');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
															<td><s:password id="firstConfirmValue0" showPassword="true"
																name="firstConfirmValue0" value="%{firstKeyValue0}" size="50"
																maxlength="63" 
																onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
																<s:textfield id="firstConfirmValue0_text"
																name="firstConfirmValue0" value="%{firstKeyValue0}" size="50"
																maxlength="63" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'ssid');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay0" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue0','firstConfirmValue0'],['firstKeyValue0_text','firstConfirmValue0_text']);" />
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
												<div style="display:<s:property value="hideThird_two"/>"
													id="hideThird_two">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
															<td><s:password id="firstKeyValue0_1" showPassword="true"
																name="firstKeyValue0_1" size="50" maxlength="64"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstKeyValue0_1_text" cssStyle="display:none" disabled="true"
																name="firstKeyValue0_1" size="50" maxlength="64"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
															<td><s:password id="firstConfirmValue0_1" showPassword="true"
																name="firstConfirmValue0_1" value="%{firstKeyValue0_1}"
																size="50" maxlength="64"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstConfirmValue0_1_text"
																name="firstConfirmValue0_1" value="%{firstKeyValue0_1}"
																size="50" maxlength="64" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay0_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue0_1','firstConfirmValue0_1'],['firstKeyValue0_1_text','firstConfirmValue0_1_text']);" />
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
											
											<div style="display:<s:property value="hideFourth"/>"
												id="hideFourth">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="labelT1" width="150px"><s:text
															name="config.ssid.keyType" /></td>
														<td><s:select id="keyType4" name="keyType4"
															list="#{'0':'ASCII Key', '1':'Hex Key'}" value="keyType4"
															onchange="show_keyType4(this.options[this.selectedIndex].value);"
															cssStyle="width:280px;" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.ssid.defaultKey" /></td>
														<td><s:select id="defaultKeyIndex4" name="defaultKeyIndex4"
															list="#{'1':'Key Value 1', '2':'Key Value 2', '3':'Key Value 3', '4':'Key Value 4'}"
															value="defaultKeyIndex4" cssStyle="width:280px;" /></td>
													</tr>
												</table>
											</div>
												<div style="display:<s:property value="hideFourth_one"/>"
													id="hideFourth_one">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue1" /></td>
															<td><s:password id="firstKeyValue1" name="firstKeyValue1"
																size="50" maxlength="13" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="firstKeyValue1_text" name="firstKeyValue1"
																size="50" maxlength="13" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue1" /></td>
															<td><s:password id="firstConfirmValue1" showPassword="true"
																name="firstConfirmValue1" value="%{firstKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="firstConfirmValue1_text" cssStyle="display:none" disabled="true"
																name="firstConfirmValue1" value="%{firstKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue1','firstConfirmValue1'],['firstKeyValue1_text','firstConfirmValue1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue2" /></td>
															<td><s:password id="secondKeyValue1" name="secondKeyValue1"
																size="50" maxlength="13" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="secondKeyValue1_text" name="secondKeyValue1"
																size="50" maxlength="13" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue2" /></td>
															<td><s:password id="secondConfirmValue1" showPassword="true"
																name="secondConfirmValue1" value="%{secondKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="secondConfirmValue1_text" cssStyle="display:none" disabled="true"
																name="secondConfirmValue1" value="%{secondKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_2" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['secondKeyValue1','secondConfirmValue1'],['secondKeyValue1_text','secondConfirmValue1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue3" /></td>
															<td><s:password id="thirdKeyValue1" name="thirdKeyValue1"
																size="50" maxlength="13" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="thirdKeyValue1_text" name="thirdKeyValue1"
																size="50" maxlength="13" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue3" /></td>
															<td><s:password id="thirdConfirmValue1" showPassword="true"
																name="thirdConfirmValue1" value="%{thirdKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="thirdConfirmValue1_text" cssStyle="display:none" disabled="true"
																name="thirdConfirmValue1" value="%{thirdKeyValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_3" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['thirdKeyValue1','thirdConfirmValue1'],['thirdKeyValue1_text','thirdConfirmValue1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue4" /></td>
															<td><s:password id="fourthValue1" name="fourthValue1"
																size="50" maxlength="13" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="fourthValue1_text" name="fourthValue1"
																size="50" maxlength="13" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue4" /></td>
															<td><s:password id="fourthConfirmValue1" showPassword="true"
																name="fourthConfirmValue1" value="%{fourthValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="fourthConfirmValue1_text" cssStyle="display:none" disabled="true"
																name="fourthConfirmValue1" value="%{fourthValue1}" size="50"
																maxlength="13" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_4" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['fourthValue1','fourthConfirmValue1'],['fourthValue1_text','fourthConfirmValue1_text']);" />
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
												<div style="display:<s:property value="hideFourth_two"/>"
													id="hideFourth_two">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue1" /></td>
															<td><s:password id="firstKeyValue1_1" showPassword="true"
																name="firstKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstKeyValue1_1_text" cssStyle="display:none" disabled="true"
																name="firstKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue1" /></td>
															<td><s:password id="firstConfirmValue1_1" showPassword="true"
																name="firstConfirmValue1_1" value="%{firstKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstConfirmValue1_1_text" cssStyle="display:none" disabled="true"
																name="firstConfirmValue1_1" value="%{firstKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue1_1','firstConfirmValue1_1'],['firstKeyValue1_1_text','firstConfirmValue1_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue2" /></td>
															<td><s:password id="secondKeyValue1_1" showPassword="true"
																name="secondKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="secondKeyValue1_1_text" cssStyle="display:none" disabled="true"
																name="secondKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue2" /></td>
															<td><s:password id="secondConfirmValue1_1" showPassword="true"
																name="secondConfirmValue1_1" value="%{secondKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="secondConfirmValue1_1_text" cssStyle="display:none" disabled="true"
																name="secondConfirmValue1_1" value="%{secondKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_1_2" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['secondKeyValue1_1','secondConfirmValue1_1'],['secondKeyValue1_1_text','secondConfirmValue1_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue3" /></td>
															<td><s:password id="thirdKeyValue1_1" showPassword="true"
																name="thirdKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="thirdKeyValue1_1_text" cssStyle="display:none" disabled="true"
																name="thirdKeyValue1_1" size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue3" /></td>
															<td><s:password id="thirdConfirmValue1_1" showPassword="true"
																name="thirdConfirmValue1_1" value="%{thirdKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="thirdConfirmValue1_1_text" cssStyle="display:none" disabled="true"
																name="thirdConfirmValue1_1" value="%{thirdKeyValue1_1}"
																size="50" maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_1_3" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['thirdKeyValue1_1','thirdConfirmValue1_1'],['thirdKeyValue1_1_text','thirdConfirmValue1_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue4" /></td>
															<td><s:password id="fourthValue1_1" name="fourthValue1_1"
																size="50" maxlength="26" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="fourthValue1_1_text" name="fourthValue1_1"
																size="50" maxlength="26" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range1_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue4" /></td>
															<td><s:password id="fourthConfirmValue1_1" showPassword="true"
																name="fourthConfirmValue1_1" value="%{fourthValue1_1}" size="50"
																maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="fourthConfirmValue1_1_text" cssStyle="display:none" disabled="true"
																name="fourthConfirmValue1_1" value="%{fourthValue1_1}" size="50"
																maxlength="26"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay1_1_4" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['fourthValue1_1','fourthConfirmValue1_1'],['fourthValue1_1_text','fourthConfirmValue1_1_text']);" />
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
											
											<div style="display:<s:property value="hideFifth"/>" id="hideFifth">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="labelT1" width="150px"><s:text
															name="config.ssid.keyType" /></td>
														<td><s:select id="keyType5" name="keyType5"
															list="#{'0':'ASCII Key', '1':'Hex Key'}" value="keyType5"
															onchange="show_keyType5(this.options[this.selectedIndex].value);"
															cssStyle="width:280px;" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.ssid.defaultKey" /></td>
														<td><s:select id="defaultKeyIndex5" name="defaultKeyIndex5"
															list="#{'1':'Key Value 1', '2':'Key Value 2', '3':'Key Value 3', '4':'Key Value 4'}"
															value="defaultKeyIndex5" cssStyle="width:280px;" /></td>
													</tr>
												</table>
											</div>
												<div style="display:<s:property value="hideFifth_one"/>"
													id="hideFifth_one">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue1" /></td>
															<td><s:password id="firstKeyValue2" name="firstKeyValue2"
																size="50" maxlength="5" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="firstKeyValue2_text" name="firstKeyValue2"
																size="50" maxlength="5" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue1" /></td>
															<td><s:password id="firstConfirmValue2" showPassword="true"
																name="firstConfirmValue2" value="%{firstKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="firstConfirmValue2_text" cssStyle="display:none" disabled="true"
																name="firstConfirmValue2" value="%{firstKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue2','firstConfirmValue2'],['firstKeyValue2_text','firstConfirmValue2_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue2" /></td>
															<td><s:password id="secondKeyValue2" name="secondKeyValue2"
																size="50" maxlength="5" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="secondKeyValue2_text" name="secondKeyValue2"
																size="50" maxlength="5" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue2" /></td>
															<td><s:password id="secondConfirmValue2" showPassword="true"
																name="secondConfirmValue2" value="%{secondKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="secondConfirmValue2_text" cssStyle="display:none" disabled="true"
																name="secondConfirmValue2" value="%{secondKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_2" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['secondKeyValue2','secondConfirmValue2'],['secondKeyValue2_text','secondConfirmValue2_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue3" /></td>
															<td><s:password id="thirdKeyValue2" name="thirdKeyValue2"
																size="50" maxlength="5" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="thirdKeyValue2_text" name="thirdKeyValue2"
																size="50" maxlength="5" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue3" /></td>
															<td><s:password id="thirdConfirmValue2" showPassword="true"
																name="thirdConfirmValue2" value="%{thirdKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="thirdConfirmValue2_text" cssStyle="display:none" disabled="true"
																name="thirdConfirmValue2" value="%{thirdKeyValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_3" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['thirdKeyValue2','thirdConfirmValue2'],['thirdKeyValue2_text','thirdConfirmValue2_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue4" /></td>
															<td><s:password id="fourthValue2" name="fourthValue2"
																size="50" maxlength="5" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="fourthValue2_text" name="fourthValue2"
																size="50" maxlength="5" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue4" /></td>
															<td><s:password id="fourthConfirmValue2" showPassword="true"
																name="fourthConfirmValue2" value="%{fourthValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:textfield id="fourthConfirmValue2_text" cssStyle="display:none" disabled="true"
																name="fourthConfirmValue2" value="%{fourthValue2}" size="50"
																maxlength="5" 
																onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_4" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['fourthValue2','fourthConfirmValue2'],['fourthValue2_text','fourthConfirmValue2_text']);" />
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
												<div style="display:<s:property value="hideFifth_two"/>"
													id="hideFifth_two">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.keyValue1" /></td>
															<td><s:password id="firstKeyValue2_1" showPassword="true"
																name="firstKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstKeyValue2_1_text" cssStyle="display:none" disabled="true"
																name="firstKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue1" /></td>
															<td><s:password id="firstConfirmValue2_1" showPassword="true"
																name="firstConfirmValue2_1" value="%{firstKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="firstConfirmValue2_1_text" cssStyle="display:none" disabled="true"
																name="firstConfirmValue2_1" value="%{firstKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue2_1','firstConfirmValue2_1'],['firstKeyValue2_1_text','firstConfirmValue2_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue2" /></td>
															<td><s:password id="secondKeyValue2_1" showPassword="true"
																name="secondKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="secondKeyValue2_1_text" cssStyle="display:none" disabled="true"
																name="secondKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue2" /></td>
															<td><s:password id="secondConfirmValue2_1" showPassword="true"
																name="secondConfirmValue2_1" value="%{secondKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="secondConfirmValue2_1_text" cssStyle="display:none" disabled="true"
																name="secondConfirmValue2_1" value="%{secondKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_1_2" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['secondKeyValue2_1','secondConfirmValue2_1'],['secondKeyValue2_1_text','secondConfirmValue2_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue3" /></td>
															<td><s:password id="thirdKeyValue2_1" showPassword="true"
																name="thirdKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="thirdKeyValue2_1_text" cssStyle="display:none" disabled="true"
																name="thirdKeyValue2_1" size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2_1" /></td>
														</tr>
														<tr>
															<td class="labelT1" width="150px"><s:text
																name="config.ssid.confirmValue3" /></td>
															<td><s:password id="thirdConfirmValue2_1" showPassword="true"
																name="thirdConfirmValue2_1" value="%{thirdKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="thirdConfirmValue2_1_text" cssStyle="display:none" disabled="true"
																name="thirdConfirmValue2_1" value="%{thirdKeyValue2_1}"
																size="50" maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_1_3" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['thirdKeyValue2_1','thirdConfirmValue2_1'],['thirdKeyValue2_1_text','thirdConfirmValue2_1_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.keyValue4" /></td>
															<td><s:password id="fourthValue2_1" name="fourthValue2_1"
																size="50" maxlength="10" showPassword="true"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="fourthValue2_1_text" name="fourthValue2_1"
																size="50" maxlength="10" cssStyle="display:none" disabled="true"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>&nbsp;<s:text name="config.ssid.keyValue_range2_1" /></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text name="config.ssid.confirmValue4" /></td>
															<td><s:password id="fourthConfirmValue2_1" showPassword="true"
																name="fourthConfirmValue2_1" value="%{fourthValue2_1}" size="50"
																maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<s:textfield id="fourthConfirmValue2_1_text" cssStyle="display:none" disabled="true"
																name="fourthConfirmValue2_1" value="%{fourthValue2_1}" size="50"
																maxlength="10"
																onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay2_1_4" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['fourthValue2_1','fourthConfirmValue2_1'],['fourthValue2_1_text','fourthConfirmValue2_1_text']);" />
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
									
									<tr> 
										<td style="padding:6px 2px 0 160px">
											<div style="display:<s:property value="hidePskUserLimit"/>" id="hidePskUserLimit">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td width="330px"><s:checkbox
														name="enabledPskUserLimit"
														value="%{enabledPskUserLimit}"
														onclick="changePskUserLimitValue(this.checked);" /><s:text
														name="config.ssid.pskUserLimit" /></td>
													<td><s:textfield id="pskUserLimit" name="pskUserLimit"
														size="5" maxlength="2"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
														name="config.ssid.pskUserLimit_range" /></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="showOption"/>" id="showOptionDiv">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" onclick="showHideOptionDiv(1);" style="cursor: pointer">
															<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																alt="Show Option" class="expandImg" style="display: inline"
																/>&nbsp;&nbsp;<s:text name="config.ssid.tab.advanceOption" />
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="hideOption"/>" id="hideOptionDiv">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" onclick="showHideOptionDiv(2);" style="cursor: pointer">
														<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
															alt="Hide Option" class="expandImg" style="display: inline"
															/>&nbsp;&nbsp;<s:text name="config.ssid.tab.advanceOption" />
													</td>
												</tr>
												<tr>
													<td height="4px"/>
												</tr>
												<tr>
													<td style="padding: 0 2px 4px 6px">
													<fieldset>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td>
																	<div style="display:<s:property value="hideRekeyPeriod"/>" id="hideRekeyPeriod">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="labelT1" width="140px"><s:text
																				name="config.ssid.rekeyPeriod" /></td>
																			<td align="left" width="260px"><s:textfield id="rekeyPeriod" name="rekeyPeriod"
																				size="10" maxlength="8"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.rekeyPeriod_range" /></td>
																		</tr>
																	</table>
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																<div style="display:<s:property value="hideGmkRekeyPeriod"/>"id="hideGmkRekeyPeriod">
																	<table border="0" cellspacing="0" cellpadding="0" width="100%">
																		<tr>
																			<td height="4"></td>
																		</tr>
																		<tr>
																			<td style="padding:0 2px 0 6px" width="140px"><s:checkbox
																				name="enabledRekeyPeriod2"
																				value="%{enabledRekeyPeriod2}" 
																				onclick="changeRekeyPeriod2Value(this.checked);"/><s:text
																				name="config.ssid.rekeyPeriod" /></td>
																			<td align="left" width="260px"><s:textfield id="rekeyPeriod2" name="rekeyPeriod2"
																				size="10" maxlength="8"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.rekeyPeriod_range" /></td>
																			<td class="labelT1" width="95px"><s:text
																				name="config.ssid.gtkTimeOut" /></td>
																			<td><s:textfield id="gtkTimeOut" name="gtkTimeOut"
																				size="6" maxlength="4"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.gtkTimeOut_range" /></td>
																		</tr>
																		<tr>
																			<td style="padding:0 2px 0 6px" width="140px"><s:checkbox
																				name="enabledRekeyPeriodGMK"
																				value="%{enabledRekeyPeriodGMK}" 
																				onclick="changeRekeyPeriodGMKValue(this.checked);"/><s:text
																				name="config.ssid.gmkRekeyPeriod" /></td>
																			<td width="260px"><s:textfield id="rekeyPeriodGMK" name="rekeyPeriodGMK"
																				size="10" maxlength="8"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.gmkRekeyPeriod_range" /></td>
																			<td class="labelT1" width="95px"><s:text
																				name="config.ssid.gtkRetries" /></td>
																			<td><s:textfield id="gtkRetries" name="gtkRetries"
																				size="10" maxlength="2"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.gtkRetries_range" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><s:text
																				name="config.ssid.ptkTimeOut" /></td>
																			<td><s:textfield id="ptkTimeOut" name="ptkTimeOut"
																				size="10" maxlength="4"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.ptkTimeOut_range" /></td>
																			<td class="labelT1"><s:text
																				name="config.ssid.ptkRetries" /></td>
																			<td><s:textfield id="ptkRetries" name="ptkRetries"
																				size="10" maxlength="2"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.ssid.ptkRetries_range" /></td>
																		</tr>
			
																		<tr>
																			<td style="padding:0 2px 0 6px"><s:checkbox
																				name="enabledRekeyPeriodPTK"
																			value="%{enabledRekeyPeriodPTK}" 
																			onclick="changePtkRekeyPeriodValue(this.checked);"/><s:text
																			name="config.ssid.ptkRekeyPeriod" /></td>
																		<td><s:textfield id="rekeyPeriodPTK" name="rekeyPeriodPTK"
																			size="10" maxlength="8"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																			name="config.ssid.rekeyPeriodPTK_range" /></td>
																		<td class="labelT1"><s:text
																			name="config.ssid.replayWindow" /></td>
																		<td><s:textfield id="replayWindow" name="replayWindow"
																			size="10" maxlength="2"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																			name="config.ssid.replayWindow_range" /></td>
																	</tr>
																</table>
																</div>
															</td>
														</tr>
														<tr>
															<td height="4"></td>
														</tr>
														<tr> 
															<td>
																<div style="display:<s:property value="hideReauthInterval"/>" id="hideReauthInterval">
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td width="142px" style="padding:0 2px 0 6px"><s:checkbox
																			name="enabledReauthInterval"
																			value="%{enabledReauthInterval}"
																			onclick="changeReauthIntervalValue(this.checked);" /><s:text
																			name="config.ssid.reauthInterval" /></td>
																		<td><s:textfield id="reauthInterval" name="reauthInterval"
																			size="10" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																			name="config.ssid.reauthInterval_range" /></td>
																	</tr>
																</table>
																</div>
															</td>
														</tr>
														<tr>
															<td>
															<div style="display:<s:property value="hideTkip"/>" id="hideTkip">
																<table border="0" cellspacing="0" cellpadding="0">	
																	<tr>
																		<td style="padding:0 2px 0 6px"><s:checkbox id="localTkip"
																			name="dataSource.ssidSecurity.localTkip" 
																			value="%{dataSource.ssidSecurity.localTkip}" /></td>
																		<td><s:text name="config.ssid.localTkip" /></td>
																		
																		<td style="padding:0 2px 0 20px"><s:checkbox id="remoteTkip"
																			name="dataSource.ssidSecurity.remoteTkip" 
																			value="%{dataSource.ssidSecurity.remoteTkip}" /></td>
																		<td><s:text name="config.ssid.remoteTkip" /></td>
																	</tr>
																</table>
																</div>
															</td>
														</tr>		
														<tr>
															<td>
																<div style="display:<s:property value="hideStrict"/>" id="hideStrict">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td style="padding:0 2px 0 6px"><s:checkbox id="strict1"
																				name="strict1" value="%{strict1}" /></td>
																			<td><s:text name="config.ssid.nonStrict" /></td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
														<tr>
															<td>
																<div style="display:<s:property value="hideAfterStrict"/>" id="hideAfterStrict">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td style="padding:0 2px 0 6px"><s:checkbox id="strict2"
																				name="strict2" value="%{strict2}" /></td>
																			<td><s:text name="config.ssid.nonStrict" /></td>
										
																			<td style="padding:0 2px 0 20px"><s:checkbox
																				id="preauthenticationEnabled"
																				name="dataSource.preauthenticationEnabled"
																				value="%{dataSource.preauthenticationEnabled}" /></td>
																			<td><s:text name="config.ssid.preauthentication" /></td>
																			<td style="padding:0 2px 0 20px"><s:checkbox
																				id="proactiveEnabled"
																				name="dataSource.ssidSecurity.proactiveEnabled"
																				value="%{dataSource.ssidSecurity.proactiveEnabled}" /></td>
																			<td><s:text name="config.ssid.enabledProactive" /></td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
														<tr>
															<td>
																<div style="display:<s:property value="hide80211w"/>" id="hide80211w">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="noteInfo" colspan="4" style="padding-top: 5px; padding-left: 10px;">
																				<s:text name="config.ssid.not.supported.80211w.note" />
																			</td>
																		</tr>
																		<tr>
																			<td style="width:23px;padding:0 2px 0 6px">
																				<s:checkbox id="enable80211w"
																				name="enable80211w" value="%{enable80211w}"
																				onclick="enableWpa2mfpType(this.checked)" />
																			<td style="padding:3px 5px 0 0">
																				<s:text name="config.ssid.80211w" />
																			</td>
																			<td id="wpa2mfpTypeTD" style="padding: 3px 0px 0px;
																				display:<s:property value="%{hideWpa2mfpType}"/>">
																				<s:radio label="Gender" id="wpa2mfpType"
																						name="wpa2mfpType" list="#{1:'Mandatory',2:'Optional'}"
																						value="%{wpa2mfpType}" />
																			</td>
																		</tr>
																		<tr id="enableBipTr" style="display:<s:property value="%{hideWpa2mfpType}"/>">
																			<td style="padding:0 2px 0 6px">
																				<s:checkbox id="enableBip" name="enableBip"
																					value ="%{enableBip}" />
																			</td>
																			<td style="padding:3px 5px 0 0">
																				<s:text name="config.ssid.bip" />
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
										</div>
										</td>
									</tr>
									<s:if test="%{disaplySocialLogin}">
									<tr id="cloudCwpAuthTr">
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td style="padding:0 4px 0 6px"><s:checkbox name="dataSource.enabledSocialLogin" 
														id="enableCloudCwpSelect" onclick="changeCloudCwpAuthValue(this.checked);"></s:checkbox>
														<label for="enableCloudCwpSelect"><s:text name="ssid.open.enable.ga"/></label>
													</td>
													<td valign="middle">
													</td>
												</tr>
											</table>
										</td>
									</tr>
									</s:if>
									<tr id="cwpAuthTr">
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td style="padding:0 4px 0 6px"><s:checkbox name="dataSource.cwpSelectEnabled" 
														id="enableCwpSelect" onclick="changeCwpAuthValue(this.checked,'personal');"></s:checkbox><LABEL id="cwpTitleLabel" for="enableCwpSelect"></LABEL></td>
													<td valign="middle">
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="hideUseGuestManager"/>" id="hideUseGuestManagerDiv">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td style="padding:0 2px 0 6px"> <s:checkbox
															name="dataSource.enabledUseGuestManager"
															value="%{dataSource.enabledUseGuestManager}"
															onclick="changeEnabledUseGuestManager();"/><label for="ssidProfilesFull_dataSource_enabledUseGuestManager"><s:text name="config.ssid.useGuestManager" /></label> 
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr id="macAuthTr">
										<td>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding:0 2px 0 6px"> <s:checkbox
														name="dataSource.macAuthEnabled" value="%{dataSource.macAuthEnabled}" 
														onclick="changeCwpAuthValue();"/><label for="ssidProfilesFull_dataSource_macAuthEnabled"><s:text name="config.ssid.enabledMAC" /></label> 
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td style="padding-left:10px">
										<div style="display:<s:property value="hideRadiusPAPCHAP"/>" id="hideRadiusPAPCHAP">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="4px"></td>
												</tr>
												<tr>
													<td width="310px">
														<s:text name="config.ssid.radiusAuth" />
													</td>
													<td>
														<s:select name="dataSource.personPskRadiusAuth"
															list="%{enumRadiusAuth}" listKey="key"
															listValue="value" cssStyle="width: 160px;" />
													</td>
												</tr>
											</table>
										</div>
										</td>
									</tr>
									<tr>
										<td>
										<s:if test="enableClientManagement == true">
										<div id="provisionTr" style="display: <s:property value="hidePersonalProvisionEnable"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding:0 2px 0 6px" class="">
														<s:checkbox name="dataSource.enableProvisionPersonal" value="%{dataSource.enableProvisionPersonal}"
														id="enableProvisionPersonal" onClick="showHideProvisionSsid(this,this.checked);"/><s:text name="config.ssid.enable.provision"/>&nbsp;&nbsp;
														<s:if test="hmSuperUser"><a class="viewClientProfile" style="display: <s:property value="hidePersonalViewURL"/>" href="<s:property value='viewClientProfileURL'/>" tabindex="-1" target="_blank"><s:text name="config.ssid.client.management.helplink.label"/></a></s:if>
													</td>
												</tr>
												<tr>
													<td style="padding:0 2px 0 6px" class="">
														<div id="provisionSsidDiv" style="display:<s:property value="hideProvisionSsidEnable"/>">
															<table>
																<tr>
																	<td width="265px" class="" style="padding:0 0 0 22px">
																		<s:text name="config.ssid.provision.ssid"/><font color="red"><s:text name="*"/></font>
																	</td>
																	<td><div id="messageForSsidKey"></div><s:textfield id="ssidPPSKKey" name="dataSource.wpaOpenSsid" value="%{dataSource.wpaOpenSsid}" maxlength="32" style="width:156px"/>&nbsp;<s:text name="config.ssid.advanced.mdm.enrollment.username.range"/></td>
																</tr>
															</table>
														</div>
													</td>
												</tr>
											</table>
										</div>
										</s:if> 
										</td>
									</tr>
									<s:if test="enableClientManagement==true">
									<tr id="onboardProvisionEPA" style="display: <s:property value="hideEnterpriseProvisionEnable"/>">
									<td>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td style="padding:0 2px 0 6px" class="">
													<s:checkbox id="enableProvisionEnterprise" name="dataSource.enableProvisionEnterprise" value="dataSource.enableProvisionEnterprise"
													onclick="hideCwpProSelected(this,this.checked);"/><s:text name="config.ssid.enable.provision.epa"/><s:if test="hmSuperUser">&nbsp;&nbsp;<a class="viewClientProfile" style="display:<s:property value="hideEnterViewURL"/> ;" href="<s:property value='viewClientProfileURL'/>" tabindex="-1" target="_blank"><s:text name="config.ssid.client.management.helplink.label"/></a></s:if>
												</td>
											</tr>
										</table>
									</td>
									</tr>
									</s:if> 
									<tr id="macBindingTr" style="display:<s:property value="hidePskSelfReg"/>">
										<td style="padding:0 2px 0 6px"><s:checkbox
											name="dataSource.ssidSecurity.blnMacBindingEnable"
											id="blnMacBindingEnable"
											onclick="changePskMacBindingValue(this.checked);"
											value="%{dataSource.ssidSecurity.blnMacBindingEnable}"/><s:text
											name="config.ssid.macBindingEnable" /></td>
									</tr>
									<tr>
										<td>
											<div id="hidePskSelfReg" style="display:<s:property value="hidePskSelfReg"/>">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													 <tr>
														<td style="padding:0 2px 0 6px">
															<s:checkbox name="dataSource.enablePpskSelfReg" value="%{dataSource.enablePpskSelfReg}" onclick="showHidePskSelfRegAdv(this.checked);"/>
															<label for="ssidProfilesFull_dataSource_enablePpskSelfReg"><s:text name="config.configTemplate.ppskreg.signle.ssid.enable"/></label>
														</td>
													</tr>
												<tr>
													<td style="padding:0 2px 0 20px">
														<div id="hidePskSelfRegAdv" style="display:<s:property value="hidePskSelfRegAdv"/>">
															 <table cellspacing="0" cellpadding="0" border="0" width="100%">
																<s:if test="hideSingleCheckBox">
																<tr>
																		<td width="350px"><s:radio label="Gender" id="privateSsidmodel" name="dataSource.privateSsidModel" value="%{dataSource.privateSsidModel}" 
																		list="#{'0' : 'Use a different SSID for self-registration'}" onclick="changeSsidModel(0)"></s:radio></td>
																		<td></td>
																</tr>
																<tr id="singleSsidDes">
																		<td colspan='2' style="padding:0 0 0 20px;color:grey"><s:text name="config.configTemplate.ppskreg.single.description"/></td>
																</tr>
																 <tr id="openSsidDiv">
																		<td style="padding:0 0 0 20px" width="290px"><s:text name="config.configTemplate.ppskreg.regSsid"/><font color="red"><s:text name="*"/></font>
																		</td>
																		<td><s:textfield name="dataSource.ppskOpenSsid" value="%{dataSource.ppskOpenSsid}" maxlength="%{ssidNameLength}" cssStyle="width: 156px;"/>
																			<s:text name="config.ssid.ssidName_range" />
																		</td>
																</tr>
																<s:if test="enableClientManagement">
																	<s:if test="%{dataSource.enableProvisionPrivate}">
																		<tr>
																		<td width="350px" colspan="2"><s:radio label="Gender" id="privateSsidmodel" name="dataSource.privateSsidModel" value="%{dataSource.privateSsidModel}" 
																		list="#{'1': 'Allow users to register themselves on this SSID'}" onclick="changeSsidModel(1)" 
																		/><s:if test="%{dataSource.ssidName != null && dataSource.ssidName != ''}">(<s:property value="%{dataSource.ssidName}"/>)</s:if></td>
																		</tr>
																	</s:if>
																	<s:else>
																		<tr>
																		<td width="350px" colspan="2"><s:radio label="Gender" id="privateSsidmodel" name="dataSource.privateSsidModel" value="%{dataSource.privateSsidModel}" 
																		list="#{'1': 'Allow users to register themselves on this SSID'}" onclick="changeSsidModel(1)" 
																		disabled="true"/><s:if test="%{dataSource.ssidName != null && dataSource.ssidName != ''}">(<s:property value="%{dataSource.ssidName}"/>)</s:if></td>
																		</tr>
																	</s:else>
																<tr id="singleSsidDIV" style="display:none">
																		<td style="padding:0 0 0 20px" width="270px"><s:text name="config.ssid.enable.single.ssid.value.label"/><font color="red"> *</font></td>
																		<td><div id="singleSsidKeyMessage"></div><s:textfield type="text" name="dataSource.singleSsidValue" 
																				maxlength="63" value="%{dataSource.singleSsidValue}" id="singleSsidValue" style="width:156px;" disabled="<s:property value='hideSingleCheckBox'/>"/>&nbsp<s:text name="config.ssid.advanced.mdm.enrollment.username.single.ssid.range">
																				<s:param>8</s:param><s:param>63</s:param></s:text>
																		</td>
																</tr>
																</s:if>
																</s:if>
																<s:else>
																<tr>
																		<td width="350px"><s:radio label="Gender" id="privateSsidmodel" name="dataSource.privateSsidModel" value="%{dataSource.privateSsidModel}" 
																		list="#{'0' : 'Use a different SSID for self-registration'}" onclick="changeSsidModel(0)"></s:radio></td>
																		<td></td>
																</tr>
																<tr id="singleSsidDes" style="display:none">
																		<td colspan='2' style="padding:0 0 0 20px;color:grey"><s:text name="config.configTemplate.ppskreg.single.description"/></td>
																</tr>
																 <tr id="openSsidDiv" style="display:none">
																		<td style="padding:0 0 0 20px" width="290px"><s:text name="config.configTemplate.ppskreg.regSsid"/><font color="red"><s:text name="*"/></font>
																		</td>
																		<td><s:textfield name="dataSource.ppskOpenSsid" value="%{dataSource.ppskOpenSsid}" maxlength="%{ssidNameLength}" cssStyle="width: 156px;"/>
																			<s:text name="config.ssid.ssidName_range" />
																		</td>
																</tr>
																<s:if test="enableClientManagement">
																<tr>
																		<td width="350px" colspan="2"><s:radio label="Gender" id="privateSsidmodel" name="dataSource.privateSsidModel" value="%{dataSource.privateSsidModel}" 
																		list="#{'1': 'Allow users to register themselves on this SSID'}" onclick="changeSsidModel(1)" 
																		/><s:if test="%{dataSource.ssidName != null && dataSource.ssidName != ''}">(<s:property value="%{dataSource.ssidName}"/>)</s:if></td>
																</tr>
																<tr id="singleSsidDIV">
																		<td style="padding:0 0 0 20px" width="270px"><s:text name="config.ssid.enable.single.ssid.value.label"/><font color="red"> *</font></td>
																		<td><div id="singleSsidKeyMessage"></div><s:textfield type="text" name="dataSource.singleSsidValue" 
																				maxlength="63" value="%{dataSource.singleSsidValue}" id="singleSsidValue" style="width:156px;" disabled="<s:property value='hideSingleCheckBox'/>"/>&nbsp<s:text name="config.ssid.advanced.mdm.enrollment.username.single.ssid.range">
																				<s:param>8</s:param><s:param>63</s:param></s:text>
																		</td>
																</tr>
																</s:if>
																</s:else>
																<s:if test="enableClientManagement">
																<tr>
																		<td class="">
																			<s:checkbox id="enableProvisionPrivate" name="dataSource.enableProvisionPrivate" value="%{dataSource.enableProvisionPrivate}"
																			onclick="hideCwpProSelected(this,this.checked);"/>
																			<label for="enableProvisionPrivate"><s:text name="config.ssid.enable.provision"/></label><s:if test="hmSuperUser">&nbsp;&nbsp;<a class="viewClientProfile" style="display: <s:property value='hidePrivateViewURL'/>;" href="<s:property value='viewClientProfileURL'/>" tabindex="-1" target="_blank"><s:text name="config.ssid.client.management.helplink.label"/></a></s:if>
																		</td>
																		<td></td>
																</tr> 
																</s:if>
															 	</table>
															 </div>
															 </td>
														</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr>
									<td style="padding: 0pt 2px 0pt 6px;">
										<div style="display:<s:property value="hideFallBackToEcwp"/>" id="hideFallBackToEcwp">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="4px"></td>
												</tr>
												<tr>
													<td >
														<s:checkbox name="dataSource.fallBackToEcwp"></s:checkbox>
														<s:text name="config.ssid.enabledFallBackToEcwp" />
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
						<tr>
							<td height="4"></td>
						</tr>
					</table>
				</td>
			</tr>

<%-- radioRate panel --%>
			<tr>
				<td style="padding: 4px 4px 4px 4px;">
					<fieldset><legend><s:text name="config.ssid.allOption.legend" /></legend>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<div style="display:<s:property value="showRadioRateDiv"/>" id="showRadioRateDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideRadioRateDiv(1);" style="cursor: pointer">
												<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg" style="display: inline"
													/>&nbsp;&nbsp;<s:text name="config.ssid.tab.radioRate" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="hideRadioRateDiv"/>" id="hideRadioRateDiv">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" onclick="showHideRadioRateDiv(2);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;<s:text name="config.ssid.tab.radioRate" />
										</td>
									</tr>
									<tr>
										<td height="4px"/>
									</tr>
									<tr>
										<td style="padding: 4px 4px 4px 4px;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>	
												<td style="padding:0 15px 0 30px;" class="borderRight">
												<fieldset><legend><s:text name="config.ssid.ratebg.legend" /></legend>
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td colspan="4">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td style="padding:0 2px 0 0"><s:checkbox
																	name="dataSource.enableGRateSet" onclick="enableGRateSet(this.checked);"/></td>
																<td><s:text name="config.ssidProfile.enableGRateSet" /></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td  style="padding-right:12px;">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<s:iterator value="%{dataSource.gRateSets.values}" status="status1">
															<s:if test="%{#status1.index < 6}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="gRateSetType0" id="gRateSetType_%{#status1.index}"
																	value="%{rateSet}" list="%{enumRateType}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableGRateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														</table>
														</td>
														<td >
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<s:iterator value="%{dataSource.gRateSets.values}" status="status2">
															<s:if test="%{#status2.index > 5}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="gRateSetType1" id="gRateSetType_%{#status2.index}"
																	value="%{rateSet}" list="%{enumRateType}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableGRateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														</table>
														</td>
													</tr>
												</table>
												</fieldset>
												</td>
												<td style="padding-left:15px;">
												<fieldset><legend><s:text name="config.ssid.ratea.legend" /></legend>
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td colspan="4">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td style="padding:0 2px 0 0"><s:checkbox
																	name="dataSource.enableARateSet" onclick="enableARateSet(this.checked);"/></td>
																<td><s:text name="config.ssidProfile.enableARateSet" /></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td  style="padding-right:12px;" colspan="2">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<s:iterator value="%{dataSource.aRateSets.values}" status="status3">
															<s:if test="%{#status3.index < 4}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="aRateSetType0" id="aRateSetType_%{#status3.index}"
																	value="%{rateSet}" list="%{enumRateType}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableARateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														<tr>
															<td height="55px"></td>
														</tr>
														</table>
														</td>
														<td >
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<s:iterator value="%{dataSource.aRateSets.values}" status="status4">
															<s:if test="%{#status4.index > 3}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="aRateSetType1" id="aRateSetType_%{#status4.index}"
																	value="%{rateSet}" list="%{enumRateType}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableARateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														<tr>
															<td height="55px"></td>
														</tr>
														</table>
														</td>
													</tr>
												</table>
												</fieldset>
												</td>
											</tr>
											<tr>	
												<td style="padding:4px 0 0 30px;" colspan="2" >
													<div>
												<fieldset><legend><s:text name="config.ssid.raten.legend" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td colspan="4">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td style="padding:0 2px 0 0"><s:checkbox
																	name="dataSource.enableNRateSet" onclick="enableNRateSet(this.checked);"/></td>
																<td><s:text name="config.ssidProfile.enableNRateSet" /></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td  style="padding-right:12px;">
														<table cellspacing="0" cellpadding="0" border="0">
														<s:iterator value="%{dataSource.nRateSets.values}" status="status1">
															<s:if test="%{#status1.index < 8}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="nRateSetType0" id="nRateSetType_%{#status1.index}"
																	value="%{rateSet}" list="%{enumRateType11n}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableNRateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														</table>
														</td>
														<td >
														<table cellspacing="0" cellpadding="0" border="0">
														<s:iterator value="%{dataSource.nRateSets.values}" status="status2">
															<s:if test="%{#status2.index > 7 && #status2.index < 16}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="nRateSetType1" id="nRateSetType_%{#status2.index}"
																	value="%{rateSet}" list="%{enumRateType11n}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableNRateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														</table>
														</td>
														<td >
														<table cellspacing="0" cellpadding="0" border="0">
														<s:iterator value="%{dataSource.nRateSets.values}" status="status3">
															<s:if test="%{#status3.index > 15}">
															<tr>
																<td class="list" align="center"><s:property value="%{value}" /></td>
																<td class="list" align="center"><s:select name="nRateSetType2" id="nRateSetType_%{#status3.index}"
																	value="%{rateSet}" list="%{enumRateType11n}" listKey="key"
																	listValue="value" disabled="%{!dataSource.enableNRateSet}"/></td>
															</tr>
															</s:if>
														</s:iterator>
														</table>
														</td>
													</tr>
												</table>
												</fieldset>
													</div>
												</td>
											</tr>
											<tr>
												<td style="padding:4px 0 0 30px;" colspan="2">
													<div>
														<fieldset>
															<legend><s:text name="config.ssid.rateac.legend" /></legend>
															<table>
																<tr>
																	<td style="width:20px;">
																		<s:checkbox id="enableACRateSet" name="dataSource.enableACRateSet" onclick="changeACRateSet(this.checked);"/>
																	</td>
																	<td style="padding-top: 4px;"><s:text name="config.ssidProfile.enableACRateSet"/></td>
																</tr>
																<tr style="display:none">
																	<td id="acRateSetError" colspan="2"></td>
																</tr>
																<tr>
																	<td colspan="2" style="padding-left:15px;">
																		<table>
																			<s:iterator value="%{dataSource.acRateSets}" id="acRateSets">
																				<tr>
																					<td>
																						<s:checkbox
																							id="stream_state_%{#acRateSets.streamType}"
																							name="stream_state"
																							value="%{#acRateSets.streamEnable}"
																							onclick="change11acMcs(this.checked,%{#acRateSets.streamType})" />
																						<s:hidden id="streamEnable_%{#acRateSets.streamType}" 
																								  name="streamEnable" value="%{#acRateSets.streamEnable}" />
																					</td>
																					<td style="padding-top: 4px;"><s:text name="config.ssid.rateac.stream.%{#acRateSets.streamType}"/></td>
																					<td style="padding: 4px 10px 0 25px;"><s:text name="config.ssid.rateac.mcs.minValue.title" /></td>
																					<td style="padding-top: 4px;">
																						<s:hidden id="hidden_mcsValue_%{#acRateSets.streamType}" name="mcsValue"/>
																						<s:label id="mcsValue_%{#acRateSets.streamType}" 
																							value="%{#acRateSets.mcsValue}" 
																							cssStyle="border: 0; color: #f6931f; font-weight: bold;"/>
																					</td>
																					<td style="padding: 4px 15px 0 15px; width: 130px;" >
																						<div id="stream_slider_<s:property value="%{#acRateSets.streamType}"/>">
																						</div>
																					</td>
																					<td style="padding: 4px 10px 0;">						
																						<s:label id="dataRate_%{#acRateSets.streamType}" 
																							value="(Up to  %{#acRateSets.vhtDataRate} for 2.4 GHz and %{#acRateSets.dataRate} for 5 GHz)" />
																					</td>
																				</tr>
																			</s:iterator>
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
								</div>
							</td>
						</tr>
			<%-- security panel --%>	
						<tr>
							<td>
								<div style="display:<s:property value="showSecurityDiv"/>" id="showSecurityDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideSecurityDiv(1);" style="cursor: pointer">
												<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg" style="display: inline"
													/>&nbsp;&nbsp;<s:text name="config.ssid.tab.security" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="hideSecurityDiv"/>" id="hideSecurityDiv">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" onclick="showHideSecurityDiv(2);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;<s:text name="config.ssid.tab.security" />
										</td>
									</tr>
									<tr>
										<td style="padding-left:30px;">
											<fieldset><legend><s:text name="config.ssid.security.dosPrevention" /></legend>
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="labelT1" width="180px"><s:text
															name="config.ssid.ssidDos" /></td>
														<td width="200px" style="padding-right:5px;"><s:select
															name="ssidDos" list="%{macDosParameterProfiles}" listKey="id"
															listValue="value" cssStyle="width: 200px;" /></td>
														<td width="20px">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newSsidDos')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
														</td>
														<td>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editSsidDos')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="180px"><s:text
															name="config.ssid.macDos" /></td>
														<td width="200px" style="padding-right:5px;"><s:select
															name="stationDos" list="%{stationDosParameterProfiles}"
															listKey="id" listValue="value" cssStyle="width: 200px;" /></td>
														<td width="20px">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newMacDos')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
														</td>
														<td>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editMacDos')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="180px"><s:text
															name="config.ssid.ipDos" /></td>
														<td width="200px" style="padding-right:5px;"><s:select
															name="ipDos" list="%{ipDosParameterProfiles}" listKey="id"
															listValue="value" cssStyle="width: 200px;" /></td>
														<td width="20px">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newIpDos')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
														</td>
														<td>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editIpDos')"><img class="dinl"
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
										<td height="4px"/>
									</tr>
									<tr>
										<td style="padding-left:30px">
											<fieldset><legend><s:text name="config.ssid.security.macAddressFilters" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td height="4px"></td>
													</tr>
													<tr>
														<s:push value="%{macFilterOptions}">
															<td colspan="3"><tiles:insertDefinition
																name="optionsTransfer" /></td>
														</s:push>
													</tr>
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td nowrap="nowrap" width="80px"><s:text name="config.ssid.defaultAction" /></td>
														<td width="325px"><s:select name="dataSource.defaultAction"
															list="%{enumFilterAction}" listKey="key" listValue="value"
															value="dataSource.defaultAction" cssStyle="width: 100px;" />
														</td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
									<tr>
										<td height="4px"/>
									</tr>
									<tr>
										<td style="padding-left:43px">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" width="180px"><s:text
														name="config.ssid.mgtServiceFilter" /></td>
													<td width="200px" style="padding-right:5px;"><s:select
														name="serviceFilter" list="%{serviceFilterProfiles}" listKey="id"
														listValue="value" cssStyle="width: 200px;" /></td>
													<td width="20px">
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
													</td>
													<td>
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
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="4px"/>
									</tr>
								</table>
								</div>	
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="showAdvancePanelDiv"/>" id="showAdvancePanelDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideAdvancePanelDiv(1);" style="cursor: pointer">
												<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg" style="display: inline"
													/>&nbsp;&nbsp;<s:text name="config.ssid.tab.advanced" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="hideAdvancePanelDiv"/>" id="hideAdvancePanelDiv">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" onclick="showHideAdvancePanelDiv(2);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;<s:text name="config.ssid.tab.advanced" />
										</td>
									</tr>
									<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td style="padding-left:20px;" width="100%">
													<fieldset><legend><s:text name="config.ssid.advanceConfigLegend" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
															
																<td width="130px" class="labelT1"><s:text
																	name="config.ssid.maxClient" /></td>
																<td width="210px"><s:textfield
																	name="dataSource.maxClient" size="10" maxlength="3"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.maxClient_range" /></td>
																<td width="180px"><s:text
																	name="config.ssid.inactiveClientAgeout" /></td>
																<td><s:textfield
																	name="dataSource.clientAgeOut" size="10" maxlength="2"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.inactiveClientAgeout_range" /></td>
															</tr>
															<tr> 
																<td colspan="4">
																	<div style="display:<s:property value="hideEap"/>" id="hideEap">
																	<table cellspacing="0" cellpadding="0" border="0" width="100%">
																		<tr>
																			<td class="labelT1" width="130px"><s:text
																				name="config.ssid.eapTimeOut" /></td>
																			<td width="210px"><s:textfield id="eapTimeOut" name="dataSource.eapTimeOut"
																				size="10" maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																				name="config.ssid.eapTimeOut_range" /></td>
																			<td width="180px"><s:text
																				name="config.ssid.eapRetries" /></td>
																			<td><s:textfield id="eapRetries" name="dataSource.eapRetries"
																				size="10" maxlength="1"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																				name="config.ssid.eapRetries_range" /></td>
																		</tr>
																	</table>
																	</div>
																</td>
															</tr>
															<tr>
																<td class="labelT1" width="130px"><s:text
																	name="config.ssid.head.rtsThreshold" /></td>
																<td width="210px"><s:textfield
																	name="dataSource.rtsThreshold" size="10" maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.rts_threshold_range" /></td>
																<td width="180px"><s:text
																	name="config.ssid.updateInterval" /></td>
																<td><s:textfield
																	name="dataSource.updateInterval" size="10" maxlength="5"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.updateInterval_range" /></td>
															</tr>
															<tr>
																<td class="labelT1" width="130px"><s:text
																	name="config.ssid.head.fragmentThreshold" /></td>
																<td width="210px"><s:textfield
																	name="dataSource.fragThreshold" size="10" maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.fragment_threshold_range" /></td>
																<td width="180px"><s:text
																	name="config.ssid.ageOut" /></td>
																<td><s:textfield
																	name="dataSource.ageOut" size="10" maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.ageOut_range" /></td>
															</tr>
															<tr>
																<td class="labelT1" width="130px"><s:text
																	name="config.ssid.head.dtimSetting" /></td>
																<td width="210px"><s:textfield
																	name="dataSource.dtimSetting" size="10" maxlength="3"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.dtim_setting_range" /></td>
																<td colspan="2">
																	<div style="display:<s:property value="hideReauthInterval"/>" id="hideLocalCacheTimeout">
																		<table cellspacing="0" cellpadding="0" border="0" width="100%">
																			<tr>
																				<td width="180px">
																					<s:text name="config.ssid.localCachetimeout" /></td>
																				<td width="220px"><s:textfield
																					name="dataSource.localCacheTimeout" size="10" maxlength="6"
																					onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																					name="config.ssid.localCachetimeout.range" /></td>
																			</tr>
																		</table>
																	</div>
																</td>
															</tr>
														</table>
													</fieldset>
												</td>
											</tr>
											<tr>
												<td	style="padding-left: 20px">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td><s:checkbox
																	name="dataSource.broadcase" value="%{dataSource.broadcase}" /></td>
																<td><s:text name="config.ssid.ignore_broadcast_probe" /></td>
														</tr>
														<tr>
															<td><s:checkbox
																	name="dataSource.hide" value="%{dataSource.hide}" /></td>
																<td><s:text name="config.ssid.hideSsid" /></td>	
															
														</tr>
														<tr>
															<td><s:checkbox
																	name="dataSource.enabledLegacy" value="%{dataSource.enabledLegacy}" /></td>
																<td><s:text name="config.ssid.legacy" /></td>	
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td style="padding-left: 16px">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="220px"><s:text name="config.ssid.auth.sequence"></s:text></td>
															<td width="200px" class="labelT1"><s:select name="dataSource.authSequence"
																list="%{enumAuthSequence}" listKey="key" listValue="value"
																value="dataSource.authSequence" cssStyle="width: 300px;" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<%-- IP Multicast begin--%>
											<s:set name="_auto" value="%{getText('config.ssid.Advanced.IPMulticast.converToUnicast.options.auto')}"></s:set>
											<s:set name="_always" value="%{getText('config.ssid.Advanced.IPMulticast.converToUnicast.options.always')}"></s:set>
											<s:set name="_disable" value="%{getText('config.ssid.Advanced.IPMulticast.converToUnicast.options.disable')}"></s:set> 
											<tr>
												<td style="padding-left:20px;" width="100%">
													<fieldset><legend><s:text name="config.ssid.Advanced.IPMulticast.title" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td width="190px" class="labelT1"><s:text
																	name="config.ssid.Advanced.IPMulticast.converToUnicast" />
																</td>
																<td colspan="3">
																<s:radio  name="dataSource.convtounicast"
																			list="#{1:#_auto,2:#_always,3:#_disable}"
																			onclick="convtounicastClick(this);" />
																</td>
															</tr>
															<s:set name="condispcontr" value="s{dataSource.convtounicast==1?'visible':'hidden'}"></s:set>
															<tr id="tr_channelUtiThreshold" >
																<td class="labelT1" width="190px"><s:text
																	name="config.ssid.Advanced.IPMulticast.channelUtiThreshold" /><font color="red"><s:text name="*"/></font></td>
																<td colspan="3"><s:textfield
																	name="dataSource.cuthreshold" size="10" maxlength="3"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.Advanced.IPMulticast.channelUtiThreshold.range" /></td>
															</tr>
															<tr id="tr_membership" >
																<td class="labelT1" width="190px"><s:text
																	name="config.ssid.Advanced.IPMulticast.membership" /><font color="red"><s:text name="*"/></font></td>		
																<td  colspan="3"><s:textfield
																	name="dataSource.memberthreshold" size="10" maxlength="2"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" /> &nbsp;<s:text
																	name="config.ssid.Advanced.IPMulticast.membership.range" /></td>
															</tr>
															
														</table>
													</fieldset>
												</td>
											</tr>
											<%-- IP Multicast end --%>
											
											<%-- WMM Admission control begin--%>
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td style="padding-left:20px;" width="100%">
													<fieldset><legend><s:text name="config.ssid.wmm.admission.control.wmm" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td class="labelT1" width="25px">
																<s:hidden name="dataSource.enabledwmm" value="%{dataSource.enabledwmm}" />
																<s:checkbox
																		name="enabledwmm" onclick="enableWmmAdmctlDisplayStyle(this.checked)" 
																		disabled="%{dataSource.enabledVoiceEnterprise}" />
																</td>
																<td style="padding-top:7px" colspan="3">
																	<s:text name="config.ssid.enabledwmm" />
																</td>
															</tr>
															<tr>
																<td style="padding-left:30px" colspan="3">
																	<fieldset><legend><s:text name="config.ssid.wmm.admission.control.mandatory" /></legend>
																		<table cellspacing="0" cellpadding="0" border="0" width="100%">
																			<tr>
																				<td class="noteInfo" colspan="4" style="padding-top: 5px; padding-left: 14px;">
																					<s:text name="config.ssid.not.supported.wmm.note" />
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1" style="width:25px;padding-top:3px">
																					<s:hidden name="dataSource.enabledAcVoice" value="%{dataSource.enabledAcVoice}" />
																					<s:checkbox name="enabledAcVoice" 
																					disabled="%{ !enabledwmm || dataSource.enabledVoiceEnterprise}" />
																				</td>
																				<td style="width:50px"><s:text name="config.ssid.wmm.admission.control.voice" /></td>
																				<td class="labelT1" style="width:25px;padding-top:3px">
																					<s:checkbox disabled="%{!enabledwmm}" name="dataSource.enabledAcVideo" />
																				</td>
																				<td><s:text name="config.ssid.wmm.admission.control.video" /></td>
																				<%-- <td class="labelT1">
																					<s:checkbox
																					disabled="%{!dataSource.enabledwmm}"
																					name="dataSource.enabledAcBackground" value="%{dataSource.enabledAcBackground}" />
																					<s:text name="config.ssid.wmm.admission.control.background" /></td>
																				<td class="labelT1">
																					<s:checkbox
																					disabled="%{!dataSource.enabledwmm}"
																					name="dataSource.enabledAcBesteffort" value="%{dataSource.enabledAcBesteffort}" />
																					<s:text name="config.ssid.wmm.admission.control.besteffect" /></td> --%>
																			</tr>
																		</table>
																	</fieldset>
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:checkbox
																	disabled="%{!enabledwmm}"
																	name="dataSource.enabledUnscheduled" />
																</td>
																<td style="padding-top:7px">
																	<s:text name="config.ssid.enabledUnscheduled" />
																</td>
															</tr>														
														</table>
													</fieldset>
												</td>
											</tr>
											<%--  WMM Admission control end --%>
											
											<%-- Voice Enterprise Certification begin--%>
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td style="padding-left:20px;" width="100%">
													<fieldset><legend><s:text name="config.ssid.wmm.admission.control.title" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td class="noteInfo" colspan="2" style="padding-top: 5px; padding-left: 14px;">
																	<s:text name="config.ssid.not.supported.ve.note" />
																</td>
															</tr>
															<%-- <tr>
																<td class="noteInfo" colspan="2" style="padding-top: 5px; padding-left: 14px;">
																	<s:text name="config.ssid.not.supported.voice.enterprise.note" />
																</td>
															</tr> --%>
															<tr>
																<td class="labelT1" style="width:25px;padding-top:3px"><s:checkbox
                                                                        name="dataSource.enabledVoiceEnterprise"
                                                                        onClick="enabledVoiceEnterprise();"/>
                                                                </td>
                                                                <td>
                                                                	<s:text name="config.ssid.voice.enterprise" />
                                                                </td>
															</tr>
                                                           
                                                            <tr>
                                                                 <td colspan="2">
                                                                     <table id="enablekvr" cellspacing="0" cellpadding="0" border="0" width="100%">
                                                                        <tr>
                                                                            <td class="labelT1" style="width:25px;padding-top:3px">
                                                                            	<s:hidden name="dataSource.enabled80211k" value="%{dataSource.enabled80211k}" />
                                                                            	<s:checkbox  
                                                                            			disabled="%{dataSource.enabledVoiceEnterprise}" 
                                                                            			onclick="changeEnabled80211k(this.checked)" 
                                                                            			name="enabled80211k" />
																			</td>
																			<td>
																				<s:text name="config.ssid.80211k" />
																			</td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td class="labelT1" style="width:25px;padding-top:3px">
                                                                            	<s:hidden name="dataSource.enabled80211v" value="%{dataSource.enabled80211v}" />
                                                                            	<s:checkbox 
                                                                            			disabled="%{dataSource.enabledVoiceEnterprise}" 
                                                                            			onclick="changeEnabled80211v(this.checked)" 
                                                                            			name="enabled80211v" />
                                                                            </td>
                                                                            <td>
                                                                            	<s:text name="config.ssid.80211v" />
                                                                            </td>
                                                                            
                                                                        </tr>
                                                                        <tr>
                                                                            <td class="labelT1" style="width:25px;padding-top:3px">
                                                                            	<s:hidden name="dataSource.enabled80211r" value="%{dataSource.enabled80211r}" />
                                                                            	<s:checkbox 
                                                                            		onclick="changeEnabled80211r(this.checked)" 
                                                                            		disabled="%{dataSource.enabledVoiceEnterprise}"
                                                                            		name="enabled80211r"  />
                                                                            </td>
                                                                            <td>
                                                                            	<s:text name="config.ssid.80211r" />
                                                                            </td>                                                                            
                                                                        </tr>  
                                                                        <tr>
                                                                            <td colspan="2" class="noteInfo" style="padding: 0 0 0 35px;">                                                                                
                                                                            	<s:text name="config.ssid.80211r.enablenote"></s:text>                                                                                 
                                                                            </td>                                                                            
                                                                        </tr>             
                                                                    </table>                                                                 
                                                                 </td>
                                                            </tr>           	
														</table>
													</fieldset>
												</td>
											</tr>
											<%-- Voice Enterprise Certification end--%>
											
											<%-- Mobile Device Management start --%>
											<tr>
												<td height="10"></td>
											</tr>
											<%-- <tr style="display: <s:property value="%{fullModeConfigStyle}"/>"> --%>
											<tr>
												<td style="padding-left:20px;" width="100%">
													<fieldset><legend><s:text name="config.ssid.advanced.mdm.enrollment.title" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td class="labelT1" width="100px"  colspan="5">
																<s:checkbox name="dataSource.enableMDM" onclick="enableMDMcheck(this.checked);"/>
																<s:text name="config.ssid.advanced.mdm.enrollment.enable"/>
																</td>
																<td id="enablemdmselect" style="padding-top:6px;">		
																		<s:select name="configmdmId" list="%{configmdmidParameterProfiles}" listKey="id" listValue="value" cssStyle="width: 140px;" />
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																				src="<s:url value="/images/new_disable.png" />"
																				width="16" height="16" alt="New" title="New" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('newConfigmdmPolicy')"><img class="dinl"
																				src="<s:url value="/images/new.png" />"
																				width="16" height="16" alt="New" title="New" /></a>
																			</s:else>
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn"
																				src="<s:url value="/images/modify_disable.png" />"
																				width="16" height="16" alt="Modify" title="Modify" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('editConfigmdmPolicy')"><img class="dinl"
																				src="<s:url value="/images/modify.png" />"
																				width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else>
																		
																	
																</td>
															</tr>
													 	</table>
													</fieldset>
												</td>
											</tr>
											<%-- Mobile Device Management end --%>
										</table>
										</td>
									</tr>
									<tr>
										<td height="4"/>
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
<s:if test="%{allowedTrial}">
<tiles:insertDefinition name="idmTrialSection" />
</s:if>
<s:if test="%{jsonMode == true}">
    <script>
       setCurrentHelpLinkUrl('<s:property value="helpLink" />');
    </script>
</s:if>
