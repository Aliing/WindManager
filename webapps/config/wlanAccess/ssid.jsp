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
<style type="text/css">
/* picture for the text tip */
.domainNameTip {
background-image: url(images/tip-fullname.png);
background-repeat: no-repeat;
}
.domainUserNameTip {
background-image: url(images/tip-domainuser.png);
background-repeat: no-repeat;
}

.topFisrtLayer {
z-index: 4;
}
.topSecondLayer {
z-index: 3;
}
div.topFixedTitle {
	position:fixed;
	z-index:9999;
	top:0;
	height:30px;
	display:inline;
	width:100%;
	padding-left:-10px;
	margin-left:-30px;
	padding-right:25px;
	background-color: #f9f9f7;
}
table.topFixedTitle {
	margin-top:30px;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<tiles:insertDefinition name="tabView" />

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
var isFullMode = <s:property value="%{blnShowSsid}"/>;
var isHHMApplication   = <s:property value="%{blnHHMApplication}"/>;
var isShowSimpleUserInfo = <s:property value="%{dataSource.showExpressUserAccess}"/>;
var ssidProfileTabs = null;
<s:if test="%{allowedTrial}">
var idmCustomerUrl = "<s:url action='ssidProfiles' includeParams='none' />?operation=completeCustomer";    
var createIDMCustomerUrl = "<s:url action='ssidProfiles' includeParams='none' />?operation=createIDMCustomer";    
var trialSettingsUrl = "<s:url action='ssidProfiles' includeParams='none' />?operation=trialSettings" + "&ignore="+new Date().getTime();    
</s:if>
var formName = 'ssidProfiles';
var changeDNAdminText = false;

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
    waitingPanel.setHeader("The operation is progressing...");
    waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
    waitingPanel.render(document.body);
}

var isFormChanged = <s:property value="%{formChangedSession}"/>;

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	ssidProfileTabs = new YAHOO.widget.TabView("ssidProfileTabs", {activeIndex:tabId});
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
	setDefaultOptionSettingLabel();
	
	setCwpTitle();

	createWaitingPanel();
	
	if(!isFullMode) {
		// do this when edit operation
		<s:if test="%{dataSource.id != null}">
		displaySSIDButtons(false);
		</s:if>
		
		// disable AD integration enable checkbox if no available radius server was selected
		var hiveRadiusIp = '<s:property value="%{getDataSource().getSelectNewHiveApRadiusPrimaryIp()}"/>';
		//console.debug("hiveRadiusIp="+hiveRadiusIp);
		if (hiveRadiusIp == '') {
			enableADintegration(false);
		}
		// show test auth descript
		if(Get("showTestAuthDiv").style.display == "") {
			hm.util.show("testAuthDescriptId");
		}
		// show more information about the AD server
		var adServerIp = '<s:property value="%{getDataSource().getAdServerIpAddress()}"/>';
		var adServerBaseDN = '<s:property value="%{getDataSource().getBaseDN()}"/>';
		if(adServerIp != '' && adServerBaseDN != '') {
			displayADInfo(true);
		}
		// show the tip for the Text
		checkTextTip("domainFullName");
		checkTextTip("domainTestUser");

		// add autocomplete UI
		initAutoCompleteRadiusComboBox();
		// init 'Join' credential status
		var adminUserName = '<s:property value="%{getDataSource().getAdDomainAdmin()}"/>';
		var testUserName = '<s:property value="%{getDataSource().getAdDomainTestUser()}"/>';
		if(adminUserName == '' && testUserName != '') {
			// set the flag, if the admin user name is empty and the test user name is not empty
			discardJoinInfo = 1;
		}
	}
	convtounicastClick(document.getElementsByName("dataSource.convtounicast")[0]);
	
    if(<s:property value="%{enabledIDM}"/>) {
    	if(Get("enableIDMChk")) {
    	    enabledCloudAuth(true);
    	}
    } else {
    	dynamicUpdateCWPs(false);
    }
    
    if(<s:property value="%{dataSource.enableMDM}"/>){
		document.getElementById(formName + "_dataSource_enableMDM").checked = true;	
	}else{
		document.getElementById(formName + "_dataSource_enableMDM").checked = false;	
		Get("enablemdmselect").style.display="none";
	}
    <s:if test="%{allowedTrial}">
    initTrialLink();
    new YAHOO.widget.Tooltip('explaination', {context: 'idmexplaination', width: "350px", container: 'ssidProfiles'});
    </s:if>
    
    initStreamSlider();
}

function initAutoCompleteRadiusComboBox() {
	// init DataSource
	var hiveAPArray = new Array();
	var index = 0;
	<s:iterator value="availablePrimaryHiveApIpAddress" id="hiveApName">
	hiveAPArray[index++] = "<s:property value='hiveApName'/>";
	</s:iterator>

	var hiveAPDataSource = new YAHOO.util.LocalDataSource(hiveAPArray);
	// AutoComplete ComboBox primary RADIUS server constructor
	var acPrimaryComboBox = autoCompelteComboBox('primaryHiveApRadius', 'primaryRadiusContainer', 'acComboBoxPrimary', hiveAPDataSource, hiveAPArray.length);
	//////// Start: custom event for primary RADIUS server ///////////
	acPrimaryComboBox.oAC.textboxChangeEvent.subscribe(function(){
		onChangePrimaryHiveAP();
	});
	acPrimaryComboBox.oAC.itemSelectEvent.subscribe(function(){
		onChangePrimaryHiveAP();
	});
	var initHiveApName = "<s:property value='%{dataSource.selectNewHiveApRadiusPrimaryIp}'/>";
	if(initHiveApName.trim() != '') {
		acPrimaryComboBox.oAC.textboxFocusEvent.subscribe(function(){
				if(acPrimaryComboBox.oAC.getInputEl().value.trim() == initHiveApName){
					//acPrimaryComboBox.oAC.getInputEl().focus();
					setTimeout(function() {// For IE
						acPrimaryComboBox.oAC.sendQuery(initHiveApName);
					}, 0);
				} else {
					onChangePrimaryHiveAP();
				}
		});
	} else {
		if(index == 1 && hiveAPArray[0] == 'None available') {
			Get('primaryHiveApRadius').value = hiveAPArray[0]; 
		}
	}
	//////// End: custom event for primary RADIUS server ///////////
	
	// AutoComplete ComboBox sencondary RADIUS server constructor
	var acSecondComboBox = autoCompelteComboBox('secondHiveApRadius', 'secondRadiusContainer', 'acComboBoxSecond', hiveAPDataSource, hiveAPArray.length);
}

// add form listener
YAHOO.util.Event.onDOMReady(registerFormListener);

function registerFormListener() {
	if(isFullMode) return;
	var parentEl = parent.document.getElementById('ssidAnimation');
	if(parentEl){
		
		 var ssidForm = document.forms['ssidProfiles'];
		 
		 for(var i=0; i<ssidForm.elements.length; i++) {
			 switch(ssidForm.elements[i].type) {
		        case 'text':
		        case 'hidden':
		        case 'password':
		        case 'textarea':
		        case 'select-one':
		        case 'select-multiple':
		        case 'radio':
		        case 'checkbox':
				  	YAHOO.util.Event.addListener(ssidForm.elements[i], "change", changeStateCallback);
		        case 'undefined':
	       	default:
	       		//dump
			 }
		 }
	}
}

function changeStateCallback(e) { 
	//console.debug("element " + this + " id:" +this.id + " is changed\ntype="+this.type);
	displaySSIDButtons(true);
}
/*
 * change the buttons display style
 */
function displaySSIDButtons(flag) {
	var parentEl = parent.document.getElementById('ssidAnimation');
	if (parentEl) {
		var ssidForm = document.forms['ssidProfiles'];
		// if the form changed session is true, set the flag
		if(isFormChanged) flag = true;
		if (flag) {
			if(Get('updateSSIDButton')) hm.util.show('updateSSIDButton');
			if(Get('cancelSSIDButton')) hm.util.show('cancelSSIDButton');
			top.hideButtonAndCheckMark('ssid');
		} else{
			if(Get('updateSSIDButton')) hm.util.hide('updateSSIDButton');
			if(Get('cancelSSIDButton')) hm.util.hide('cancelSSIDButton');
			top.showButton('ssid');
		}
	}
}
var selectUIElement;
function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
		//document.forms[formName].tabId.value = ssidProfileTabs.get('activeIndex');
		if (operation == 'newSsidDos') {
			document.forms[formName].selectDosType.value = 'mac';
		}
		if (operation == 'newMacDos') {
			document.forms[formName].selectDosType.value = 'station';
		}
		if (operation == 'newConfigmdmPolicy') {
			selectUIElement= Get(formName + "_configmdmId");
			document.forms[formName].selectDosType.value = 'configmdm';
		}
		hm.options.selectAllOptions('macFilters');
		hm.options.selectAllOptions('schedulers');
		hm.options.selectAllOptions('selectUserProfiles');
		hm.options.selectAllOptions('localUserGroupIds');
		if (isFullMode) {
			hm.options.selectAllOptions('hiveApLocalUserGroupIds');
		}
		
		//add handler to deal with something before form submit.
 		beforeSubmitAction(document.forms[formName]);
 		if ("update"!=operation 
 			&& "create"!=operation 
 			&& "editSimpleModeUserProfile"!=operation 
 			&& "newUserOperator"!=operation
 			&& "newUserPolicy"!=operation
 			&& "editUserPolicy"!=operation
 			&& "newCwp"!=operation
 			&& "editCwp"!=operation) {
 			document.forms[formName].target = "_parent";
 		}
		document.forms[formName].submit();
		document.forms[formName].target = "_self";

	}
}

var operationCode;
function validate(operation) {
	operationCode = operation;
	if (operation == "newUserProfileRadius") {
		showOptionNewUserProfilePanel();
		return false;
	}
	if (operation == "newUserOperator") {
		var pId='<s:property value="%{id}"/>';
		if (pId==null || pId=='') {
			warnDialog.cfg.setProperty('text', "The SSID configuration has not yet been saved. You must first save the configuration before proceeding.");
			warnDialog.show();
			return false;
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
	if(operation == "editSsidDos"){
		var value = hm.util.validateListSelection(formName + "_ssidDos");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ssidDos.value = value;
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
	if(operation == "editUserPolicy"){
		var value = hm.util.validateListSelection(formName + "_userPolicyId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userPolicyId.value = value;
		}
	}
	if(operation == "editScheduler"){
		var value = hm.util.validateOptionTransferSelection("schedulers");
		if(value < 0){
			return false
		}else{
			document.forms[formName].editScheduleId.value = value;
		}
	}
	if(operation == "editCwp"){
		var value = hm.util.validateListSelection(formName + "_cwpId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].cwpId.value = value;
		}
	}
	if(operation == "editRadius"){
		var value = hm.util.validateListSelection(formName + "_radiusId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].radiusId.value = value;
		}
	}
	if(operation == "editRadiusPpsk"){
		var value = hm.util.validateListSelection(formName + "_radiusPpskId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].radiusPpskId.value = value;
		}
	}
	if(operation == "editLocalUserGroup"){
		var value = hm.util.validateOptionTransferSelection("localUserGroupIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].editLocalUserGroupId.value = value;
		}
	}
	if(operation == "editLocalUserGroupForRadius"){
		var value = hm.util.validateOptionTransferSelection("hiveApLocalUserGroupIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].editLocalUserGroupIdForRadius.value = value;
		}
	}
	if(operation == "editUserProfileSelfReg"){
		var value = hm.util.validateListSelection(formName + "_userProfileSelfRegId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userProfileSelfRegId.value = value;
		}
	}
	if(operation == "editUserProfileDefault"){
		var value = hm.util.validateListSelection(formName + "_userProfileDefaultId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userProfileDefaultId.value = value;
		}
	}
	if(operation == "editUserProfileRadius"){
		var value = hm.util.validateOptionTransferSelection("selectUserProfiles");
		if(value < 0){
			return false
		}else{
			document.forms[formName].editSelectUserProfileId.value = value;
		}
	}
    if(operation == 'editAsRuleGroup') {
    	var value = hm.util.validateListSelection("asRuleGroup");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].asRuleGroup.value = value;
		}
    }
    if(operation == 'editIpAddress') {
    	var value = hm.util.validateListSelection("newRadiusPrimaryIpSelect");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].newRadiusPrimaryIp.value = value;
		}
    }
    if(operation == 'editIpAddressSec') {
    	var value = hm.util.validateListSelection("newRadiusSecondaryIpSelect");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].newRadiusSecondaryIp.value = value;
		}
    }
    if(operation == 'editSelfVlan') {
    	var value = hm.util.validateListSelection(formName + "_newSelfVlanId");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].newSelfVlanId.value = value;
		}
    }
    if(operation == 'editDefaultVlan') {
    	var value = hm.util.validateListSelection(formName + "_newDefaultVlanId");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].newDefaultVlanId.value = value;
		}
    }
    if(operation == 'editOptionVlan') {
    	var value = hm.util.validateListSelection(formName + "_newOptionVlanId");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].newOptionVlanId.value = value;
		}
    }
    
	if (operation == "newSsidDos" ||
		operation == "newMacDos" ||
		operation == "newIpDos" ||
		operation == "newMacFilter" ||
		operation == "newServiceFilter" ||
		operation == "newUserPolicy" ||
		operation == "newScheduler" ||
		operation == "newCwp" ||
		operation == "newRadius" ||
		operation == "newLocalUserGroup" ||
		operation == "newLocalUserGroupForRadius" ||
		operation == "newUserProfileSelfReg" ||
		operation == "newUserProfileDefault" ||
		operation == "newUserProfileRadiusMore" ||
		operation == "editSsidDos" ||
		operation == "editMacDos" ||
		operation == "editIpDos" ||
		operation == "editMacFilter" ||
		operation == "editServiceFilter" ||
		operation == "editUserPolicy" ||
		operation == "editScheduler" ||
		operation == "editCwp" ||
		operation == "editRadius" ||
		operation == "editLocalUserGroup" ||
		operation == "editLocalUserGroupForRadius" ||
		operation == "editUserProfileSelfReg" ||
		operation == "editUserProfileDefault" ||
		operation == "editUserProfileRadius" || 
		operation == "newIpAddress" ||
		operation == "editIpAddress" ||
		operation == "newIpAddressSec" ||
		operation == "editIpAddressSec" ||
		operation == "saveNewRadiusSetting" ||
		operation == "saveSelfNewUserProfileSetting" ||
		operation == "newSelfVlan" ||
		operation == "editSelfVlan" ||
		operation == "saveDefaultNewUserProfileSetting" ||
		operation == "newDefaultVlan" ||
		operation == "editDefaultVlan" ||
		operation == "saveOptionNewUserProfileSetting" ||
		operation == "newOptionVlan" ||
		operation == "editOptionVlan" ||
		operation == "newDevicePolicy" ||
		operation == "editDevicePolicy" ||
		operation == "newPpskECwp" ||
		operation == "editPpskECwp" ||
		operation == "newUserOperator" ||
		operation == "newRadiusPpsk" ||
		operation == "editRadiusPpsk"
		
		) {
		if (!isFullMode && !validateSsidName()) {
			return false;
		}
		if (!validateGeneral()) {
			return false;
		}
		if (!isFullMode){
			if (!validateRadiusServer()) {
				return false;
			}
			if (!validateUserProfileInformationPanel()){
				return false;
			}
		}
		
		if (!validatePskUserLimit()) {
			return false;
		}

		if (!validateActionTime()) {
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
		
		if (operation == 'saveNewRadiusSetting') {
			if (!validateNewRadiusPanel()) {
				return false;
			}
		}
		if (operation == 'saveSelfNewUserProfileSetting') {
			if (!validateSelfNewUserProfilePanel()) {
				return false;
			}
		}
		
		if (operation == 'saveDefaultNewUserProfileSetting') {
			if (!validateDefaultNewUserProfilePanel()) {
				return false;
			}
		}
		
		if (operation == 'saveOptionNewUserProfileSetting') {
			if (!validateOptionNewUserProfilePanel()) {
				return false;
			}
		}
		
		return true;
	}
	
	if (operation == "<%=Navigation.L2_FEATURE_SSID_PROFILES%>" ||
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
		document.getElementById("actionTime").value=60;
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
	
	if (!isFullMode){
		if (!validateUserProfileInformationPanel()){
			return false;
		}
	}
	
	if (!validatePskUserLimit()) {
		return false;
	}
	
	if (!validatePrivatePSKGroup()) {
		return false;
	}
	
	if (!validateRadiusServer()) {
		return false;
	}
	if (!validateSelectUserProfiles()) {
		return false;
	}
	if (!validateActionTime()) {
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
	
	if(!validateMDM()){
		return false;
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
function validateUserProfileInformationPanel(){
	var vlanNames = document.getElementById("newUserProfileVlanSelect");
	var vlanValue = document.forms[formName].inputNewUserProfileVlanValue;
	var showError = document.getElementById("errorDisplayUserProfileVlan");
	
	if ("" == vlanValue.value) {
	    hm.util.reportFieldError(showError, 
	    	'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="topology.client.title.vlan" /></s:param></s:text>');
	    vlanValue.focus();
		return false;
	}
    
    if (!hm.util.hasSelectedOptionSameValue(vlanNames, vlanValue)) {
    	var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="topology.client.title.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanValue.focus();
            return false;
        }
		document.forms[formName].newUserInfoVlanId.value = -1;
	} else {
		document.forms[formName].newUserInfoVlanId.value = vlanNames.options[vlanNames.selectedIndex].value;
	}


	if (Get("hideUserRateLimit").style.display != "none") {
		var inputElement = document.getElementById(formName + "_dataSource_userRatelimit");
		if (inputElement.value.length == 0) {
			 hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.userInfo.userRateLimit" /></s:param></s:text>');
			 inputElement.focus();
			 return false;
		 }
		 var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.userInfo.userRateLimit" />', 1, 5000);
		 if (message != null) {
			 hm.util.reportFieldError(inputElement,message);
			 inputElement.focus();
			 return false;
		 }
	 }
	if (Get("hideUserNumberPsk").style.display != "none") {
		var inputElement = document.getElementById(formName + "_dataSource_userNumberPsk");
		if (inputElement.value.length == 0) {
			 hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.userInfo.numberPsk" /></s:param></s:text>');
			 inputElement.focus();
			 return false;
		 }
		 var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.userInfo.numberPsk" />', 1, 9999);
		 if (message != null) {
			 hm.util.reportFieldError(inputElement,message);
			 inputElement.focus();
			 return false;
		 }
	 }
     
	return true;
}

function validateSelfNewUserProfilePanel(){
    var inputElement = document.getElementById("newSelfUserProfileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.userprofile.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
	
   	var inputElement=document.getElementById("newSelfAttributeValue");
    if (inputElement.value.length == 0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.attribute" /></s:param></s:text>');
         inputElement.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.attribute" />', 1, 4095);
     if (message != null) {
         hm.util.reportFieldError(inputElement,message);
         inputElement.focus();
         return false;
     }
     
	var vlanNames = document.getElementById("newSelfVlanSelect");
	var vlanValue = document.forms[formName].inputNewSelfVlanValue;
	var showError = document.getElementById("errorDisplaySelfVlan");
	
	if ("" == vlanValue.value) {
	    hm.util.reportFieldError(showError, 
	    	'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.userprofile.vlan" /></s:param></s:text>');
	    vlanValue.focus();
		return false;
	}
    
    if (!hm.util.hasSelectedOptionSameValue(vlanNames, vlanValue)) {
    	var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.userprofile.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanValue.focus();
            return false;
        }
		document.forms[formName].newSelfVlanId.value = -1;
	} else {
		document.forms[formName].newSelfVlanId.value = vlanNames.options[vlanNames.selectedIndex].value;
	}
     
	return true;
}

function validateDefaultNewUserProfilePanel(){
    var inputElement = document.getElementById("newDefaultUserProfileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.userprofile.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
	
   	var inputElement=document.getElementById("newDefaultAttributeValue");
    if (inputElement.value.length == 0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.attribute" /></s:param></s:text>');
         inputElement.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.attribute" />', 1, 4095);
     if (message != null) {
         hm.util.reportFieldError(inputElement,message);
         inputElement.focus();
         return false;
     }

	var vlanNames = document.getElementById("newDefaultVlanSelect");
	var vlanValue = document.forms[formName].inputNewDefaultVlanValue;
	var showError = document.getElementById("errorDisplayDefaultVlan");

	if ("" == vlanValue.value) {
	    hm.util.reportFieldError(showError, 
	    	'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.userprofile.vlan" /></s:param></s:text>');
	    vlanValue.focus();
		return false;
	}

    if (!hm.util.hasSelectedOptionSameValue(vlanNames, vlanValue)) {
    	var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.userprofile.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanValue.focus();
            return false;
        }
		document.forms[formName].newDefaultVlanId.value = -1;
	} else {
		document.forms[formName].newDefaultVlanId.value = vlanNames.options[vlanNames.selectedIndex].value;
	}
     
	return true;
}

function validateOptionNewUserProfilePanel(){
    var inputElement = document.getElementById("newOptionUserProfileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.userprofile.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
	
   	var inputElement=document.getElementById("newOptionAttributeValue");
    if (inputElement.value.length == 0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.attribute" /></s:param></s:text>');
         inputElement.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.attribute" />', 1, 4095);
     if (message != null) {
         hm.util.reportFieldError(inputElement,message);
         inputElement.focus();
         return false;
     }

	var vlanNames = document.getElementById("newOptionVlanSelect");
	var vlanValue = document.forms[formName].inputNewOptionVlanValue;
	var showError = document.getElementById("errorDisplayOptionVlan");
	
	if ("" == vlanValue.value) {
	    hm.util.reportFieldError(showError, 
	    	'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.userprofile.vlan" /></s:param></s:text>');
	    vlanValue.focus();
		return false;
	}

    if (!hm.util.hasSelectedOptionSameValue(vlanNames, vlanValue)) {
    	var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.userprofile.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanValue.focus();
            return false;
        }
		document.forms[formName].newOptionVlanId.value = -1;
	} else {
		document.forms[formName].newOptionVlanId.value = vlanNames.options[vlanNames.selectedIndex].value;
	}

	return true;
}

function validateNewRadiusPanel(){
	if (isFullMode){
	    var inputElement = document.getElementById(formName + "_newRadiusName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.ssid.newRadius.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
	}

/**	if (Get(formName + "_newRadiusPrimaryIp").value<0) {
           hm.util.reportFieldError(Get(formName + "_newRadiusPrimaryIp"), '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
           Get(formName + "_newRadiusPrimaryIp").focus();
           return false;
	}
	if (ipValue == ipSecValue) {
           hm.util.reportFieldError(Get(formName + "_newRadiusSecondaryIp"), '<s:text name="error.equal"><s:param><s:text name="config.ssid.newRadius.secondary.server" /></s:param><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
           Get(formName + "_newRadiusSecondaryIp").focus();
           return false;
	}**/
	
	var checkRADIUS = true;
	
	if (isFullMode || (!isFullMode && Get("newRadiusType1").checked)) {
		var ipnames = document.getElementById("newRadiusPrimaryIpSelect");
		var ipValue = document.forms[formName].inputNewRadiusPrimaryIpValue;
		var ipSecnames = document.getElementById("newRadiusSecondaryIpSelect");
		var ipSecValue = document.forms[formName].inputNewRadiusSecondaryIpValue;
		
		if(Get("enableIDMChk") && Get("enableIDMChk").checked) {
			if("" == ipValue.value && "" == ipSecValue.value) {
				checkRADIUS = false;
			}
		}
		
		if(checkRADIUS) {
		if ("" == ipValue.value) {
		    hm.util.reportFieldError(Get("errorDisplayPrimaryIp"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
		    ipValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				var message = hm.util.validateName(ipValue.value, '<s:text name="config.ssid.newRadius.primary.server" />');
			    if (message != null) {
			    	hm.util.reportFieldError(Get("errorDisplayPrimaryIp"), message);
			        ipValue.focus();
			        return false;
			    }
			}
			document.forms[formName].newRadiusPrimaryIp.value = -1;
		} else {
			document.forms[formName].newRadiusPrimaryIp.value = ipnames.options[ipnames.selectedIndex].value;
		}
		
		if (ipSecValue.value!='') {
			if (!hm.util.hasSelectedOptionSameValue(ipSecnames, ipSecValue)) {
				if (!hm.util.validateIpAddress(ipSecValue.value)) {
					var message = hm.util.validateName(ipSecValue.value, '<s:text name="config.ssid.newRadius.secondary.server" />');
				    if (message != null) {
				    	hm.util.reportFieldError(Get("errorDisplaySecondaryIp"), message);
				        ipSecValue.focus();
				        return false;
				    }
				}
				document.forms[formName].newRadiusSecondaryIp.value = -1;
			} else {
				document.forms[formName].newRadiusSecondaryIp.value = ipSecnames.options[ipSecnames.selectedIndex].value;
			}
		} else {
			document.forms[formName].newRadiusSecondaryIp.value = -1;
		}
		
		if (ipValue.value == ipSecValue.value) {
	        hm.util.reportFieldError(Get("errorDisplaySecondaryIp"), '<s:text name="error.equal"><s:param><s:text name="config.ssid.newRadius.secondary.server" /></s:param><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
	        ipSecValue.focus();
	        return false;
		}
		}
	} else {
		if(!validateSimpleModeRadiusServer()) return false;
	}
	
	if (Get("newRadiusType1").checked && checkRADIUS) {
		if (!validateRadiusKeyConfirmValue("newRadiusSecret","newRadiusConfirmSecret","chkToggleDisplayPrimarySecret")) {
			return false;
		}
		if (ipSecValue.value!='') {
			if (!validateRadiusKeyConfirmValue("newRadiusSecondSecret","newRadiusSecondConfirmSecret","chkToggleDisplaySecondSecret")){
				return false;
			}
		}
	}
	if (isFullMode) {
		if (Get("newRadiusType2").checked) {
			var localUserGroupIds = document.getElementById("hiveApLocalUserGroupIds");
			if (localUserGroupIds.length<1) {
				hm.util.reportFieldError(document.getElementById("hiveApRadiusMsg"), '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.selected.group" /></s:param></s:text>');
				localUserGroupIds.focus();
				return false;
			}
		}	
	}
	return true;
}

function validateHiveAPConfig() {
    var hiveAPIpAddressElement=Get("hiveAPIpAddress");
    if(hiveAPIpAddressElement.value==""){
        hm.util.reportFieldError(hiveAPIpAddressElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.ipAddress" /></s:param></s:text>');
        hiveAPIpAddressElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpAddressElement.value)){
        hm.util.reportFieldError(Get("hiveAPIpAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.ipAddress" /></s:param></s:text>');
        hiveAPIpAddressElement.focus();
        return false;
    }
    var hiveAPIpNetmaskElement=Get("hiveAPNetmask");
    if(hiveAPIpNetmaskElement.value==""){
        hm.util.reportFieldError(hiveAPIpNetmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.netmask" /></s:param></s:text>');
        hiveAPIpNetmaskElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpNetmaskElement.value)){
        hm.util.reportFieldError(hiveAPIpNetmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.netmask" /></s:param></s:text>');
        hiveAPIpNetmaskElement.focus();
        return false;
    }
    var hiveAPIpGatewayElement=Get("hiveAPGateway");
    if(hiveAPIpGatewayElement.value==""){
        hm.util.reportFieldError(hiveAPIpGatewayElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.gateway" /></s:param></s:text>');
        hiveAPIpGatewayElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpGatewayElement.value)){
        hm.util.reportFieldError(hiveAPIpGatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.gateway" /></s:param></s:text>');
        hiveAPIpGatewayElement.focus();
        return false;
    }
    var hiveAPDNSServerElement=Get("hiveAPDNSServer");
    if(hiveAPDNSServerElement.value==""){
        hm.util.reportFieldError(hiveAPDNSServerElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.dns" /></s:param></s:text>');
        hiveAPDNSServerElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPDNSServerElement.value)){
        hm.util.reportFieldError(hiveAPDNSServerElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.dns" /></s:param></s:text>');
        hiveAPDNSServerElement.focus();
        return false;
    }
    return true;
}

function validateSimpleModeRadiusServer(){
	//validate radius server setting
	var checkRADIUS = true;
     if(Get("enableIDMChk") && Get("enableIDMChk").checked) {
         if((Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") 
        		 && (Get("secondHiveApRadius").value=="" || Get("secondHiveApRadius").value=="None available")) {
             checkRADIUS = false;
         }
     }
	if(checkRADIUS) {
	    if (Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") {
	        hm.util.reportFieldError(Get("primaryHiveApRadius"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
	        Get("primaryHiveApRadius").focus();
	        return false;
	    }else{
	    	if(!validateHiveAPConfig()) return false;
	    }
	    if (Get("primaryHiveApRadius").value==Get("secondHiveApRadius").value) {
	        hm.util.reportFieldError(Get("secondHiveApRadius"), '<s:text name="error.equal"><s:param><s:text name="config.ssid.newRadius.secondary.server" /></s:param><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
	        Get("secondHiveApRadius").focus();
	        return false;
	    }
	    //validate AD settings
	    if(!validateADSetting()) return false;
	}
    
    return true;
}

function validateADSetting(){
	var adIntegrationElement=Get("ADIntegration");
	if(adIntegrationElement.checked){
		// validate
		var domainFullNameElement=Get("domainFullName");
        
        if(domainFullNameElement.value==""){
            hm.util.reportFieldError(domainFullNameElement, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.realmName" /></s:param></s:text>');
            domainFullNameElement.focus();
            return false;
        }
        
        var dName=Get("domainName").value;
        var adIp=Get("adServerIpAddress").value;
        var adBDN=Get("baseDN").value;
        
        if(dName=="" || adIp=="" || adBDN ==""){
        	var msg;
        	if(operationCode == "create" || operationCode == "update") {
        		msg = '<s:text name="error.ssid.retrieve.save" />';
        	} else {
        		msg = '<s:text name="error.ssid.retrieve.proceed" />';
        	}
        	 hm.util.reportFieldError(domainFullNameElement, msg);
        	 return false;
        }
        var enalbeObscure = isEnableObscurePasswd();
        // need to save admin credentials for 'Join' operation, checke the GUI settings
        if(0 == discardJoinInfo) {
	        var domainAdminElement=Get("domainAdmin");
	        if(domainAdminElement.value==""){
	            hm.util.reportFieldError(domainAdminElement, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param></s:text>');
	            domainAdminElement.focus();
	            return false;
	        }
	        var domainAdminPasswdElement = enalbeObscure ? Get("domainAdminPasswd"):Get("domainAdminPasswd_text");
	        if(domainAdminPasswdElement.value==""){
	            hm.util.reportFieldError(domainAdminPasswdElement, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.password" /></s:param></s:text>');
	            domainAdminPasswdElement.focus();
	            return false;
	        }
        }
        
        if(changeDNAdminText){
           	var msg;
        	if(operationCode == "create" || operationCode == "update") {
        		msg = '<s:text name="error.ssid.join.save" />';
        	} else {
        		msg = '<s:text name="error.ssid.join.proceed" />';
        	}
        	 hm.util.reportFieldError(domainAdminElement, msg);
        	 return false;
        }
        var domainTestUserElement=Get("domainTestUser");
        if(domainTestUserElement.value==""){
            hm.util.reportFieldError(domainTestUserElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.testUserName" /></s:param></s:text>');
            domainTestUserElement.focus();
            return false;
        }
        var domainTestUserPasswdElement = enalbeObscure ? Get("domainTestUserPasswd"):Get("domainTestUserPasswd_text");        
        if(domainTestUserPasswdElement.value==""){
            hm.util.reportFieldError(domainTestUserPasswdElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.testUserPasswd" /></s:param></s:text>');
            domainTestUserPasswdElement.focus();
            return false;
        }
        
		return true;
	}
	return true;
}

function validateRadiusKeyConfirmValue(elementKey,elementConfirm, checkBoxId) {
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
	         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.newRadius.primary.secret" /></s:param></s:text>');
	         keyElement.focus();
	         return false;
      }

      if (confirmElement.value.length == 0) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.newRadius.primary.confirmSecret" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }

      var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.ssid.newRadius.primary.secret" />');
	  if (message != null) {
	      hm.util.reportFieldError(keyElement, message);
	      keyElement.focus();
	      return false;
	  }

      if (keyElement.value != confirmElement.value) {
	      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.newRadius.primary.confirmSecret" /></s:param><s:param><s:text name="config.ssid.newRadius.primary.secret" /></s:param></s:text>');
	    	keyElement.focus();
	    	return false;
      }

      return true;
}

function validateSsidName() {
    var inputElement = document.getElementById(formName + "_dataSource_ssidName");
    var message;
    //if (!isFullMode){
	// 	message = hm.util.validateSsid(inputElement.value, '<s:text name="config.ssid.head.ssid" />');
	//} else {
		message = hm.util.validateName(inputElement.value, '<s:text name="config.ssid.ssidName" />');
	//}
	if (message != null) {
		ssidProfileTabs.set('activeIndex', 0);
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateSsid() {
    var inputElement = document.getElementById(formName + "_dataSource_ssid");
	//if (isFullMode) {
		var message = hm.util.validateSsid(inputElement.value, '<s:text name="config.ssid.head.ssid" />');
		if (message != null) {
			ssidProfileTabs.set('activeIndex', 0);
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
	//} else {
	//	document.getElementById(formName + "_dataSource_ssid").value=document.getElementById(formName + "_dataSource_ssidName").value;
	//}
    return true;
}

function validateDtimSetting() {
      var inputElement = document.getElementById(formName + "_dataSource_dtimSetting");
      if (inputElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
      		ssidProfileTabs.set('activeIndex', 4);
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
		ssidProfileTabs.set('activeIndex', 4);
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
		ssidProfileTabs.set('activeIndex', 4);
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
		ssidProfileTabs.set('activeIndex', 4);
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.eapTimeOut" /></s:param></s:text>');
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.eapTimeOut" />', 5, 300);
	if (message != null) {
		ssidProfileTabs.set('activeIndex', 4);
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
		ssidProfileTabs.set('activeIndex', 4);
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.eapRetries" /></s:param></s:text>');
		document.getElementById("hideAdvancePanelDiv").style.display="block";
		document.getElementById("showAdvancePanelDiv").style.display="none";
		inputElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.eapRetries" />', 1, 5);
	if (message != null) {
		ssidProfileTabs.set('activeIndex', 4);
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
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.reauthInterval" /></s:param></s:text>');
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	            showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.reauthInterval" />',600,86400);
	      if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, message);
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.pskUserLimit" /></s:param></s:text>');
	            //document.getElementById("hideOptionDiv").style.display="block";
	            //document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&lt;&lt;"
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.pskUserLimit" />',0,15);
	      if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, message);
	            //document.getElementById("hideOptionDiv").style.display="block";
	            //document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&lt;&lt;"
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
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.rekeyPeriod" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.rekeyPeriod" />',600,50000000);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
           	document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.rekeyPeriod" /></s:param></s:text>');
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.rekeyPeriod" />',600,50000000);
	      if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, message);
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkRekeyPeriod" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkRekeyPeriod" />',10,50000000);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
	      //var rekeyPeriodValue = document.getElementById("rekeyPeriod").value;
	      if (inputElement.value.length == 0) {
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gmkRekeyPeriod" /></s:param></s:text>');
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        	showDefaultOption();
	            inputElement.focus();
	            return false;
	      }
	      //var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gmkRekeyPeriod" />',parseInt(rekeyPeriodValue)+1,50000000);
	      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gmkRekeyPeriod" />',600,50000000);
	      if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 4);
	            hm.util.reportFieldError(inputElement, message);
	            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkTimeOut" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkTimeOut" />',100,8000);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateGTKTimeOut() {
      var inputElement = document.getElementById("gtkTimeOut");
      if (inputElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gtkTimeOut" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gtkTimeOut" />',100,8000);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validatePTKRetries() {
      var inputElement = document.getElementById("ptkRetries");
      if (inputElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ptkRetries" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.ptkRetries" />',1,10);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateGTKRetries() {
      var inputElement = document.getElementById("gtkRetries");
      if (inputElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.gtkRetries" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.gtkRetries" />',1,10);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      return true;
}

function validateReplayWindow() {
      var inputElement = document.getElementById("replayWindow");
      if (inputElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.replayWindow" /></s:param></s:text>');
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
	        showDefaultOption();
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.replayWindow" />',0,10);
      if (message != null) {
      		ssidProfileTabs.set('activeIndex', 4);
            hm.util.reportFieldError(inputElement, message);
            document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
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
      		ssidProfileTabs.set('activeIndex', 0);
	         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
	         keyElement.focus();
	         return false;
      }

      if (confirmElement.value.length == 0) {
      		ssidProfileTabs.set('activeIndex', 0);
            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }
      if (keyElement.name=="firstKeyValue0")
      {
	      if (keyElement.value.length < 8) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
	            keyElement.focus();
	            return false;
	      }

	      if (confirmElement.value.length < 8) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
	            confirmElement.focus();
	            return false;
	      }
	      
	      //var message = hm.util.validateNameWithBlanks(keyElement.value, '<s:text name="config.ssid.keyValue" />');
		  //if (message != null) {
		  //	ssidProfileTabs.set('activeIndex', 0);
		  //    hm.util.reportFieldError(keyElement, message);
		  //    keyElement.focus();
		  //    return false;
		  //}
	  }

	  if (keyElement.name=="firstKeyValue0_1")
      {
	      if (keyElement.value.length < 64) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
	            keyElement.focus();
	            return false;
	      }
	      
	      var message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue" />');

      	  if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(keyElement, message);
	            keyElement.focus();
	            return false;
      	  }

	      if (confirmElement.value.length < 64) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
	            confirmElement.focus();
	            return false;
	      }
	      
	      var message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue" />');

      	  if (message != null) {
	      		ssidProfileTabs.set('activeIndex', 0);
	            hm.util.reportFieldError(confirmElement, message);
	            confirmElement.focus();
	            return false;
      	  }
	  }

      if (keyElement.value != confirmElement.value) {
      		ssidProfileTabs.set('activeIndex', 0);
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
      		ssidProfileTabs.set('activeIndex', 0);
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
      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
		      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
			      	ssidProfileTabs.set('activeIndex', 0);
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
			      	ssidProfileTabs.set('activeIndex', 0);
			        hm.util.reportFieldError(confirmElement, message);
			        confirmElement.focus();
			        return false;
		      	}
	       	}
	  }

      if (keyElement.name.substr(keyElement.name.length - 1)=="2" && keyElement.name.substr(keyElement.name.length - 2)!="_1") {
	      	if (keyElement.value.length < 5 && keyElement.value.length !=0) {
	      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
		      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
	      		ssidProfileTabs.set('activeIndex', 0);
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
			      	ssidProfileTabs.set('activeIndex', 0);
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
			      	ssidProfileTabs.set('activeIndex', 0);
			        hm.util.reportFieldError(confirmElement, message);
			        confirmElement.focus();
			        return false;
		      	}
	       	}
      }

      if (keyElement.value != confirmElement.value) {
      		ssidProfileTabs.set('activeIndex', 0);
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

		// return true;
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

		// return true;
    }
    
   if (mgmtKey == KEY_MGMT_DYNAMIC_WEP) {
		if(!validateRekeyPeriod()) {
			return false;
		}
		// return true;
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
		// return true;
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
 function init_pass_value()
  	{
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

//function keyPressPermitName(e,type) {
//	var keycode;
//	if(window.event) // IE
//	{
//		keycode = e.keyCode;
//	} else if(e.which) // Netscape/Firefox/Opera
//	{
//		keycode = e.which;
//		if (keycode==8) {return true;}
//	} else {
//		return true;
//	}

//	if (type=="name") {
//		if(32 != keycode && 47 != keycode && 63!= keycode && 39!= keycode){
//			return true;
//		}
//	}
//	return false;
//}

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
  }

function show_tt(expid1)
  {
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
				if (!document.getElementById("enabledDefaultSetting").checked) {
					hideThird.style.display="block";
					if (Get("keyType3").value=='0') {
			    		hideThird_one.style.display="block";
			    	} else {
			    		hideThird_two.style.display="block";
			    	}
				} else {
			    	hideThird_one.style.display="block";
		    	}
			}
			aut.length=0;
			aut.length=1;
			aut.options[0].value='0';
			aut.options[0].text='OPEN';
		}
		hideGmkRekeyPeriod.style.display="block";
		
		if (expid1==KEY_MGMT_WPA2_EAP_802_1_X || expid1==KEY_MGMT_WPA2_PSK) {
			hide80211w.style.display="block";
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
			if (!document.getElementById("enabledDefaultSetting").checked) {
		    	hideThird.style.display="block";
		    	if (Get("keyType3").value=='0') {
		    		hideThird_one.style.display="block";
		    	} else {
		    		hideThird_two.style.display="block";
		    	}
		    } else {
		    	hideThird_one.style.display="block";
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
    	
		if (!document.getElementById("enabledDefaultSetting").checked) {
			hideFourth.style.display="block";
			document.getElementById("hideAuthMethord").style.display="block";
			if (Get("keyType4").value=='0') {
	    		hideFourth_one.style.display="block";
	    	} else {
	    		hideFourth_two.style.display="block";
	    	}
		} else {
	    	hideFourth_one.style.display="block";
    	}
    	
		document.getElementById("enableCwpSelect").checked=false;
		document.getElementById("hideCwpSelect").style.display="none";
		document.getElementById(formName +"_cwpId").value=-1;
    	document.getElementById("hideUserPolicyDiv").style.display="none";
		document.getElementById(formName +"_userPolicyId").value=-1;
    	
    	document.getElementById("hideOptionDiv").style.display="none";
		//document.getElementById("showOption").style.display="none";
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
    
    	if (!document.getElementById("enabledDefaultSetting").checked) {
	    	document.getElementById("hideOptionDiv").style.display="block";
			//document.getElementById("showOption").style.display="block";
			//document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&gt;&gt;"
    	} else {
	    	document.getElementById("hideOptionDiv").style.display="none";
			//document.getElementById("showOption").style.display="none";
    	}
		
		document.getElementById("enableCwpSelect").checked=false;
		document.getElementById("hideCwpSelect").style.display="none";
		document.getElementById(formName +"_cwpId").value=-1;
    	document.getElementById("hideUserPolicyDiv").style.display="none";
		document.getElementById(formName +"_userPolicyId").value=-1;
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
			ssidProfileTabs.set('activeIndex', 1);
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
			ssidProfileTabs.set('activeIndex', 1);
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
			ssidProfileTabs.set('activeIndex', 1);
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
}

function changeEnableCwpSelect(checked) {
	if (checked){
		if (document.getElementById("accessMode2").checked 
			|| document.getElementById("accessMode3").checked
			|| document.getElementById("mgmtKey").value ==KEY_MGMT_DYNAMIC_WEP) {
				document.getElementById("hideUserPolicyDiv").style.display="block";
				document.getElementById("hideCwpSelect").style.display="none";
				document.getElementById(formName +"_cwpId").value=-1;
		} else {
				document.getElementById("hideUserPolicyDiv").style.display="none";
				document.getElementById(formName +"_userPolicyId").value=-1;
				document.getElementById("hideCwpSelect").style.display="block";
		}
	}
	if (!checked){
		hm.util.hideFieldError();
		if (document.getElementById("accessMode2").checked 
			|| document.getElementById("accessMode3").checked
			|| document.getElementById("mgmtKey").value ==KEY_MGMT_DYNAMIC_WEP) {
				document.getElementById("hideUserPolicyDiv").style.display="none";
				document.getElementById(formName +"_userPolicyId").value=-1;
		} else {
				document.getElementById("hideCwpSelect").style.display="none";
				document.getElementById(formName +"_cwpId").value=-1;
				changeCwpAuthValue();
		}
	}
}

function changeEnabledUseGuestManager(){
	if (document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked) {
		//document.getElementById("hideRadiusPAPCHAP").style.display="block";
		document.getElementById(formName + "_dataSource_macAuthEnabled").checked=false;
	//} else {
		//document.getElementById("hideRadiusPAPCHAP").style.display="none";
		//document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
	}
	changeCwpAuthValue();
}

function changeCwpAuthValue() {
	var cwpId=document.getElementById(formName + "_cwpId").value;
	var blnMacAuth;
	if (document.getElementById("accessMode2").checked) {
		blnMacAuth = document.getElementById(formName + "_dataSource_macAuthEnabled").checked || document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked
		if (document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			//document.getElementById("hideRadiusPAPCHAP").style.display="block";
			//document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
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
	
	var keyManagement=document.getElementById("mgmtKey").value;
	var url = '<s:url action="ssidProfiles"><s:param name="operation" value="changeCwpAuthOperation"/></s:url>' + "&cwpId="+cwpId + "&blnMacAuth="+blnMacAuth + "&keyManagement="+keyManagement + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	
    if(!Get("enableCwpSelect").checked 
            && (Get("accessMode1").checked || Get("accessMode4").checked || Get("accessMode5").checked)) {
        if(Get("enableIDMChk")) {
            Get("enableIDMChk").checked = false;
            enabledCloudAuth(false);
        }
    }	
}

function enableWpa2mfpType(checked){
	if(checked){
		Get("wpa2mfpTypeTD").style.display="";
	}else{
		Get("wpa2mfpTypeTD").style.display="none";
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
	
	if (value==1) {
		if (isFullMode) {
			document.getElementById("hideRadius").style.display="none";
			Get("newRadiusPanel").style.display="none";
		} else {
			document.getElementById("hideSimpleRadius").style.display="none";
		}
	    document.getElementById("hideSelfReg").style.display="none";
	    hideSelfNewUserProfilePanel();
	    document.getElementById("hideOpenWep").style.display="none";
	    document.getElementById("hideAuth").style.display="none";
	    document.getElementById("hideOpenWepRadius").style.display="none";
	    hideDefaultNewUserProfilePanel();
	    document.getElementById("hideAuth2").style.display="none";
	    document.getElementById("hideCheckBoxOnly").style.display="none";
	    document.getElementById("hideUserProfileList").style.display="none";
	    hideOptionNewUserProfilePanel();
	    document.getElementById("hidePSKUserMessage").style.display="none";
	    document.getElementById("hidePSKDefaultUserProfile").style.display="none";
	} else if (value==2) {
		if (isFullMode) {
	    	document.getElementById("hideRadius").style.display="none";
	    	Get("newRadiusPanel").style.display="none";
	    } else {
	   		document.getElementById("hideSimpleRadius").style.display="none";
	    }
	    document.getElementById("hideSelfReg").style.display="block";
	    document.getElementById("hideOpenWep").style.display="none";
	    document.getElementById("hideAuth").style.display="none";
	    document.getElementById("hideOpenWepRadius").style.display="none";
	    hideDefaultNewUserProfilePanel();
	    document.getElementById("hideAuth2").style.display="none";
	    document.getElementById("hideCheckBoxOnly").style.display="none";
	    document.getElementById("hideUserProfileList").style.display="none";
	    hideOptionNewUserProfilePanel();
	    document.getElementById("hidePSKUserMessage").style.display="none";
	    document.getElementById("hidePSKDefaultUserProfile").style.display="none";
	} else if (value==3) {
		if (isFullMode) {
	    	document.getElementById("hideRadius").style.display="none";
	    	Get("newRadiusPanel").style.display="none";
	    } else {
	   		document.getElementById("hideSimpleRadius").style.display="none";
	    }

	    document.getElementById("hideSelfReg").style.display="none";
	    hideSelfNewUserProfilePanel();
	    document.getElementById("hideAuth").style.display="none";
	    document.getElementById("hideAuth2").style.display="none";
		document.getElementById("hideCheckBoxOnly").style.display="none";
		document.getElementById("hidePSKDefaultUserProfile").style.display="none";
	    if (document.getElementById("accessMode2").checked) {
		    document.getElementById("hideUserProfileList").style.display="block";
		    document.getElementById("hidePSKUserMessage").style.display="block";
		    document.getElementById("hideOpenWep").style.display="none";
		   	document.getElementById("hideOpenWepRadius").style.display="none";
		   	hideDefaultNewUserProfilePanel();
	    } else {
	    	document.getElementById("hideUserProfileList").style.display="none";
	    	hideOptionNewUserProfilePanel();
	    	document.getElementById("hidePSKUserMessage").style.display="none";
		    document.getElementById("hideOpenWep").style.display="block";
		   	document.getElementById("hideOpenWepRadius").style.display="block";
	    }
	} else if(value==4) {
		if (isFullMode) {
	    	document.getElementById("hideRadius").style.display="block";
	    } else {
	   		document.getElementById("hideSimpleRadius").style.display="block";
	    }
	    document.getElementById("hideSelfReg").style.display="none";
	    hideSelfNewUserProfilePanel();
	    document.getElementById("hideOpenWep").style.display="none";
	    if (document.getElementById("accessMode2").checked 
	    	&& document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
	    	document.getElementById("hideAuth").style.display="none";
	    	document.getElementById("hideAuth2").style.display="none";
	    	document.getElementById("hideCheckBoxOnly").style.display="none";
	    	document.getElementById("hidePSKUserMessage").style.display="block";
	    	document.getElementById("hidePSKDefaultUserProfile").style.display="block";
	    } else {
	    	document.getElementById("hideAuth").style.display="block";
	    	document.getElementById("hideAuth2").style.display="block";
	    	document.getElementById("hideCheckBoxOnly").style.display="block";
	     	document.getElementById("hidePSKUserMessage").style.display="none";
	     	document.getElementById("hidePSKDefaultUserProfile").style.display="none";
	    }
	    document.getElementById("hideOpenWepRadius").style.display="block";
	    document.getElementById("hideUserProfileList").style.display="block";
	    
	} else if (value==5){
		if (isFullMode) {
	    	document.getElementById("hideRadius").style.display="block";
	    } else {
	   		document.getElementById("hideSimpleRadius").style.display="block";
	    }
	    document.getElementById("hideSelfReg").style.display="block";
	    document.getElementById("hideOpenWep").style.display="none";
	    document.getElementById("hideAuth").style.display="block";
	    document.getElementById("hideOpenWepRadius").style.display="block";
	    document.getElementById("hideAuth2").style.display="block";
	    document.getElementById("hideCheckBoxOnly").style.display="block";
	    document.getElementById("hideUserProfileList").style.display="block";
	    document.getElementById("hidePSKUserMessage").style.display="none";
	    document.getElementById("hidePSKDefaultUserProfile").style.display="none";
	}
	if (document.getElementById("hideAuth2").style.display=="none"){
	   	document.getElementById(formName + "_dataSource_chkUserOnly").checked=false;
	    document.getElementById("denyAction").value=3;
	    document.getElementById("actionTime").value=60;
	    document.getElementById("actionTime").disabled = true;
	    document.getElementById(formName + "_dataSource_chkDeauthenticate").checked=false;
	    document.getElementById("hideAction").style.display="none";
    }
    if (!isFullMode) {
	    document.getElementById("hideCreateLocal").style.display="none";
	    if (document.getElementById("hideSimpleRadius").style.display!="none" && 
	    	document.getElementById("newRadiusType2").checked==true) {
	    	if (!Get("ADIntegration").checked){
	    		document.getElementById("hideCreateLocal").style.display="";
	    	}
	    } 
		if (document.getElementById("accessMode2").checked){
	    	document.getElementById("hideCreateLocal").style.display="";
	    }
    }
    
    // IDM
    if(Get("enableIDMChk")) {
        enabledCloudAuth(Get("enableIDMChk").checked, true);
    }
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function changeDenyAction(select) {
	if(select.value == 1) {
		document.getElementById("actionTime").disabled = false;
	} else {
		document.getElementById("actionTime").value = 60;
		document.getElementById("actionTime").disabled = true;
	}
}

function showActionEdit(checked) {
    var hideAction = document.getElementById("hideAction");
	if (checked){
		hideAction.style.display="block";
		document.getElementById("denyAction").value ="3";
		document.getElementById("actionTime").value ="60";
		document.getElementById(formName + "_dataSource_chkDeauthenticate").checked=false;
	}
	if (!checked){
		document.getElementById("denyAction").value ="3";
		document.getElementById("actionTime").value ="60";
		document.getElementById(formName + "_dataSource_chkDeauthenticate").checked=false;
		hideAction.style.display="none";
	}
}

function validateRadiusServer() {
	if (document.getElementById("hideRadius").style.display!="none") {
		var radiusId = document.getElementById(formName + "_radiusId");
		if (radiusId.value<0) {
			hm.util.reportFieldError(document.getElementById("noRadiusMsg"), '<s:text name="error.requiredField"><s:param>RADIUS Server</s:param></s:text>');
		    radiusId.focus();
		    return false;
		}
	}
	if (!isFullMode && document.getElementById("hideSimpleRadius").style.display!="none") {
		if (!validateNewRadiusPanel()) {
			return false;
		}
	}
	return true;	
}

function validatePrivatePSKGroup() {
	if (document.getElementById("accessMode2").checked && isFullMode) {
		var localUserGroupIds = document.getElementById("localUserGroupIds");
		if (localUserGroupIds.length<1) {
			hm.util.reportFieldError(document.getElementById("pskMsg"), '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.wizard.selectPrivatePsk" /></s:param></s:text>');
		    localUserGroupIds.focus();
		    return false;
		}
		if (!document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked && !document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			var selectUserProfileIds = document.getElementById("selectUserProfiles");
			if (selectUserProfileIds.length<1) {
				hm.util.reportFieldError(document.getElementById("selectUserProfileMsg"), '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.wizard.selectUserProfile" /></s:param></s:text>');
			    selectUserProfileIds.focus();
			    return false;
			}
		}
	}
	return true;	
}

function validateSelectUserProfiles() {
	var selectUserProfileSelfReg = document.getElementById(formName + "_userProfileSelfRegId");
	var selectUserProfile = document.getElementById(formName + "_userProfileDefaultId");
	if (document.getElementById("hideSelfReg").style.display!='none' && document.getElementById("hideRadius").style.display!='none') {
		if (selectUserProfile.value==selectUserProfileSelfReg.value) {
			hm.util.reportFieldError(document.getElementById("radiusMsg"), '<s:text name="error.bind.userProfile.reduplicate"/>');
		    selectUserProfile.focus();
		    return false;
		} 
	}
	var enableCwpSelect = document.getElementById("enableCwpSelect");
	var selectCwp = document.getElementById(formName + "_cwpId");
	var selectUserPolicy = document.getElementById(formName + "_userPolicyId");
	if (enableCwpSelect.checked && selectCwp.value<0  && selectUserPolicy.value<0) {
		hm.util.reportFieldError(document.getElementById("hideCwpSelect"), '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.cwp" /></s:param></s:text>');
	    selectCwp.focus();
	    return false;
	} 
	return true;	
}

function validateActionTime() {
	if(document.getElementById(formName +"_dataSource_chkUserOnly").checked){
		var actionTimeItem = document.getElementById("actionTime");
		var message = hm.util.validateIntegerRange(actionTimeItem.value, '<s:text name="config.configTemplate.wizard.actionTime" />',1,100000000);
		if (message != null) {
		    hm.util.reportFieldError(actionTimeItem, message);
		    actionTimeItem.focus();
		    return false;
		}
	}
	return true;
}

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

function changeAccessMode(accessMode){
	setDefaultOptionSettingLabel();
	setCwpTitle();
	
    document.getElementById("cwpAuthTr").style.display="";
    document.getElementById("macAuthTr").style.display="";
	
	document.getElementById("eapTimeOut").value=30;
	document.getElementById("eapRetries").value=3;
	
	document.getElementById("enableCwpSelect").checked=false;
	document.getElementById("hideCwpSelect").style.display="none";
	
	document.getElementById(formName +"_cwpId").value=-1;
   	document.getElementById("hideUserPolicyDiv").style.display="none";
	document.getElementById(formName +"_userPolicyId").value=-1;
	
	if (accessMode==2) {
		if (!isHHMApplication && isFullMode) {
			document.getElementById("hideUseGuestManagerDiv").style.display="block";
		}
		if(isFullMode) {
			document.getElementById("hideLocalUserGroup").style.display="block";
			document.getElementById("hidePskSelfReg").style.display="block";
			document.getElementById("macBindingTr").style.display="";
			
		}
		document.getElementById("hidePskUserLimit").style.display="block";
		document.getElementById("userManagerTr").style.display="";

	} else {
		document.getElementById("userManagerTr").style.display="none";
		document.getElementById(formName + "_dataSource_blnUserManager").checked=false;
		document.getElementById(formName + "_dataSource_enabledUseGuestManager").checked=false;
		document.getElementById("hidePskUserLimit").style.display="none";
		if (!document.getElementById(formName + "_dataSource_macAuthEnabled").checked) {
			document.getElementById("hideRadiusPAPCHAP").style.display="none";
			document.getElementById(formName + "_dataSource_personPskRadiusAuth").value = 1;
		}
		document.getElementById("hideLocalUserGroup").style.display="none";
		document.getElementById("hideUseGuestManagerDiv").style.display="none";
		document.getElementById("hidePskSelfReg").style.display="none";
		document.getElementById("macBindingTr").style.display="none";
		showHidePskSelfRegAdv(false);
		changePskMacBindingValue(false);
	}
	if (accessMode==1 || accessMode==2) {
		document.getElementById("showOption").style.display="block";
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&gt;&gt;"
		document.getElementById("enabledDefaultSetting").checked=true;
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideEap").style.display="none";
		document.getElementById("hideAuthMethord").style.display="none";
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
		init_value();
	} else if (accessMode==3) {
		document.getElementById("showOption").style.display="block";
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&gt;&gt;"
		document.getElementById("enabledDefaultSetting").checked=true;
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideEap").style.display="block";
		document.getElementById("hideAuthMethord").style.display="none";
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
		init_value();
	} else if (accessMode==4) {
		document.getElementById("showOption").style.display="block";
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&gt;&gt;"
		document.getElementById("enabledDefaultSetting").checked=true;
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideEap").style.display="none";
		document.getElementById("hideAuthMethord").style.display="none";
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
		init_value();
	} else if (accessMode==5) {
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("showOption").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideAuthMethord").style.display="none";
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
		init_value();
	}
	
	var userCategoryType;
	if (Get("userCategory2").checked) {
		userCategoryType=2;
	} else if (Get(formName + "_dataSource_expressUserCategoryVoice").checked) {
		userCategoryType=3;
	//} else if (Get("userCategory3").checked) {
	//	userCategoryType=1;
	} else {
		userCategoryType=1;
	}
	changeUserCategory(userCategoryType, true);
	
	if (accessMode!=2){
		document.getElementById("pskUserLimit").value='0';
		document.getElementById("pskUserLimit").readOnly=true;
		document.getElementById(formName + "_enabledPskUserLimit").checked=false;
		document.getElementById("blnMacBindingEnable").checked=false;
	}
	document.getElementById("enabledDefaultSetting").checked=false;
	
   if(Get("enableIDMChk")) {
        enabledCloudAuth(Get("enableIDMChk").checked);
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
function showHideScheduleDiv(value){
	if (value==1) {
		document.getElementById("showScheduleDiv").style.display="none";
		document.getElementById("hideScheduleDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showScheduleDiv").style.display="block";
		document.getElementById("hideScheduleDiv").style.display="none";
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

function showDefaultSetting(value){
	if (value) {
		if (document.getElementById("accessMode1").checked){
			changeAccessMode(1);
		} else if (document.getElementById("accessMode2").checked){
			changeAccessMode(2);
		} else if (document.getElementById("accessMode3").checked){
			changeAccessMode(3);
		} else if (document.getElementById("accessMode4").checked){
			changeAccessMode(4);
		} 
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&lt;&lt;"
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideAuthMethord").style.display="none";
		document.getElementById("hideThird").style.display="none";
		document.getElementById("hideFourth").style.display="none";
		document.getElementById("hideFifth").style.display="none";
	} else {
		if (document.getElementById("mgmtKey").value==KEY_MGMT_OPEN
		 	|| document.getElementById("mgmtKey").value==KEY_MGMT_WEP_PSK){ 
			document.getElementById("hideOptionDiv").style.display="none";
		} else {
			document.getElementById("hideOptionDiv").style.display="block";
		}
		
		if (document.getElementById("accessMode1").checked){
			document.getElementById("hideKeyManagement").style.display="block";
			document.getElementById("hideThird").style.display="block";
		} else if (document.getElementById("accessMode2").checked){
			document.getElementById("hideKeyManagement").style.display="block";
		} else if (document.getElementById("accessMode3").checked){
			document.getElementById("hideKeyManagement").style.display="block";
		} else if (document.getElementById("accessMode4").checked){
			document.getElementById("hideKeyManagement").style.display="block";
			if (document.getElementById("mgmtKey").value==KEY_MGMT_DYNAMIC_WEP) {
				document.getElementById("hideAuthMethord").style.display="none";
				document.getElementById("hideFourth").style.display="none";
				document.getElementById("hideFifth").style.display="none";
			} else {
				document.getElementById("hideAuthMethord").style.display="block";
				if (document.getElementById("enc").value==3) {
					document.getElementById("hideFourth").style.display="block";
				} else if (document.getElementById("enc").value==4) {
					document.getElementById("hideFifth").style.display="block";
				}
			}
		}
	}
	document.getElementById("enabledDefaultSetting").checked=value;
}

function showDefaultOption(){
	if (document.getElementById("hideEnabledDefaultSettingDiv").style.display!="none") {
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideKeyManagement").style.display="none";
		document.getElementById("hideAuthMethord").style.display="none";
		document.getElementById("hideThird").style.display="none";
		document.getElementById("hideFourth").style.display="none";
		document.getElementById("hideFifth").style.display="none";
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&gt;&gt;"
	} else {
		document.getElementById("hideEnabledDefaultSettingDiv").style.display="block";
		document.getElementById("buttonOptions").innerHTML="Advanced Access Security Settings&lt;&lt;"
		showDefaultSetting(Get("enabledDefaultSetting").checked);
	}
}

function setDefaultOptionSettingLabel(){
	if (document.getElementById("accessMode1").checked){
		document.getElementById("defaultOptionLabel").innerHTML ="Use Default WPA/WPA2 PSK Settings";
	} else if (document.getElementById("accessMode2").checked){
		document.getElementById("defaultOptionLabel").innerHTML ="Use Default Private PSK Settings";
	} else if (document.getElementById("accessMode3").checked){
		document.getElementById("defaultOptionLabel").innerHTML ="Use Default 802.1X Settings";
	} else if (document.getElementById("accessMode4").checked){
		document.getElementById("defaultOptionLabel").innerHTML ="Use Default WEP Settings";
	} else{
		document.getElementById("defaultOptionLabel").innerHTML ="Use Default Open Settings";
	}
}
// add for apply new radius server  
function hideNewRadiusPanel(){
	if (isFullMode){
		Get("newRadiusPanel").style.display="none";
	}
}
function showNewRadiusPanel(){
	Get("newRadiusType1").checked=true;
	Get(formName + "_newRadiusName").value="";
	Get(formName + "_newRadiusPrimaryIp").value=-1;
	Get(formName + "_newRadiusSecondaryIp").value=-1;
	Get("inputNewRadiusSecondaryIpValue").value="";
	Get("inputNewRadiusPrimaryIpValue").value="";
	Get("newRadiusPrimaryIpSelect").value=-1;
	Get("newRadiusSecondaryIpSelect").value=-1;
	
	Get("inputNewRadiusSecondaryIpValue").disabled=false;
	Get("inputNewRadiusPrimaryIpValue").disabled=false;
	Get("newRadiusSecret").value="";
	Get("newRadiusConfirmSecret").value="";
	Get("newRadiusConfirmSecret_text").value="";
	Get("newRadiusSecret_text").value="";
	Get("newRadiusSecondSecret").value="";
	Get("newRadiusSecondConfirmSecret").value="";
	Get("newRadiusSecondConfirmSecret_text").value="";
	Get("newRadiusSecondSecret_text").value="";
	Get("newRadiusSecretPanel").style.display="block";
	Get("newRadiusSecondSecretPanel").style.display="block";
	if(isFullMode){
		Get("hideHiveApLocalUserGroupDiv").style.display="none";
		Get("newRadiusPanel").style.display="block";
	}
}

function showSelfNewUserProfilePanel(){
	Get("hideSelfNewUserProfileDiv").style.display="block";
	Get("newSelfUserProfileName").value="";
	Get("newSelfAttributeValue").value=1;
	Get("inputNewSelfVlanValue").value="";
	Get("newSelfVlanSelect").value=-1;
	Get("inputNewSelfVlanValue").disabled=false;
	Get(formName+"_newSelfVlanId").value=-1;
	Get(formName+"_dataSource_newSelfBlnUserManager").checked=false;
}
function hideSelfNewUserProfilePanel(){
	Get("hideSelfNewUserProfileDiv").style.display="none";
}

function showDefaultNewUserProfilePanel(){
	Get("hideDefaultNewUserProfileDiv").style.display="block";
	Get("newDefaultUserProfileName").value="";
	Get("newDefaultAttributeValue").value=1;
	Get(formName+"_newDefaultVlanId").value=-1;
	Get("newDefaultVlanSelect").value=-1;
	Get("inputNewDefaultVlanValue").value="";
	Get("inputNewDefaultVlanValue").disabled=false;
	Get(formName+"_dataSource_newDefaultBlnUserManager").checked=false;
}
function hideDefaultNewUserProfilePanel(){
	Get("hideDefaultNewUserProfileDiv").style.display="none";
}

function showOptionNewUserProfilePanel(){
	Get("hideOptionNewUserProfileDiv").style.display="block";
	Get("newOptionUserProfileName").value="";
	Get("newOptionAttributeValue").value=1;
	Get("inputNewOptionVlanValue").value="";
	Get("inputNewOptionVlanValue").disabled=false;
	Get(formName+"_newOptionVlanId").value=-1;
	Get("newOptionVlanSelect").value=-1;
	Get(formName+"_dataSource_newOptionBlnUserManager").checked=false;
}
function hideOptionNewUserProfilePanel(){
	Get("hideOptionNewUserProfileDiv").style.display="none";
}


function changeNewRadiusType(type){
	//EXTERNAL
	if (type==1) {
		Get("newRadiusSecretPanel").style.display="block";
		Get("newRadiusSecondSecretPanel").style.display="block";
		Get("hideHiveApLocalUserGroupDiv").style.display="none";
	//INTERNAL
	} else {
		Get("newRadiusSecretPanel").style.display="none";
		Get("newRadiusSecondSecretPanel").style.display="none";
		Get("hideHiveApLocalUserGroupDiv").style.display="block";
	}
}
function changeSimpleNewRadiusType(type){
	//EXTERNAL
	if (type==1) {
		Get("newSimpleRadiusPanel").style.display="block";
		Get("newHiveApRadiusPanelDiv").style.display="none";
		if (document.getElementById("accessMode2").checked){
			document.getElementById("hideCreateLocal").style.display="";
		} else {
			document.getElementById("hideCreateLocal").style.display="none";
		}
	//INTERNAL
	} else {
		Get("newSimpleRadiusPanel").style.display="none";
		Get("newHiveApRadiusPanelDiv").style.display="block";
		if (!Get("ADIntegration").checked){
			document.getElementById("hideCreateLocal").style.display="";
		} else {
			document.getElementById("hideCreateLocal").style.display="none";
		}
		if (document.getElementById("accessMode2").checked){
			document.getElementById("hideCreateLocal").style.display="";
		}
		//Get("primaryHiveApRadius").onchange();
	}
}

function changeUserCategoryCheck(checked) {
	if (checked) {
		changeUserCategory(3);
	} else {
		changeUserCategory(1);
	}
}

function changeUserCategory(value, needChangeAccessMode) {
	if (value==2) {
		Get("userCategoryEmployee1").checked=false;
		Get("hideUserCategory").style.display="none";
		Get(formName + "_dataSource_expressUserCategoryVoice").checked=false;
		if (!needChangeAccessMode) {
			document.getElementById(formName + "_dataSource_macAuthEnabled").checked=false;
			document.getElementById("accessMode1").checked=true;
			changeAccessMode(1);
		}
	} else if (value==5) {
		Get("userCategory2").checked=false;
		Get(formName + "_dataSource_expressUserCategoryVoice").checked=false;
		//Get("userCategory1").checked=true;
		Get("hideUserCategory").style.display="";
		value=1;
		if (!needChangeAccessMode) {
			document.getElementById("accessMode3").checked=true;
			changeAccessMode(3);
		}
	} else {
		Get("userCategoryEmployee1").checked=true;;
		Get("hideUserCategory").style.display="";
	}
	if (value==2) {
		Get("macAuthTr").style.display="none";
		Get("accessMode8021xTd").style.display="none";
	} else {
		Get("macAuthTr").style.display="";
		Get("accessMode8021xTd").style.display="";
	}
	
	Get(formName + "_dataSource_userRatelimit").value=3000;
	Get(formName + "_dataSource_userNumberPsk").value=200;
	//if (value == 2) {
	//	Get("moreSettingWord").innerHTML="Advanced firewall, QoS, and tunnel settings";
	//} else if (value == 1 || value == 3) {
	//	Get("moreSettingWord").innerHTML="Advanced firewall, QoS, and SLA settings";
	//} else {
	//	Get("moreSettingWord").innerHTML="Advanced firewall, QoS, tunnel, and SLA settings";
	//}
	if (document.getElementById("accessMode2").checked){
		if (value == 1 || value == 3 || value == 4) {
			Get("hideUserRateLimit").style.display="none";
			Get("hideUserPskMethod").style.display="block";
			if (Get(formName + "_dataSource_userPskMethod").value==2) {
				Get("hideUserNumberPsk").style.display="block";
			} else {
				Get("hideUserNumberPsk").style.display="none";
			}
			Get("hideUserInternetAccess").style.display="none";
		} else if (value == 2) {
			if (isShowSimpleUserInfo) {
				Get("hideUserRateLimit").style.display="block";
				Get("hideUserInternetAccess").style.display="block";
			} else {
				Get("hideUserRateLimit").style.display="none";
				Get("hideUserInternetAccess").style.display="none";
			}
			Get("hideUserPskMethod").style.display="none";
			Get("hideUserNumberPsk").style.display="block";
			
		} 
	} else{
		if (value == 1 || value == 3 || value == 4) {
			Get("hideUserRateLimit").style.display="none";
			Get("hideUserPskMethod").style.display="none";
			Get("hideUserNumberPsk").style.display="none";
			Get("hideUserInternetAccess").style.display="none";
		} else if (value == 2 ) {
			if (isShowSimpleUserInfo) {
				Get("hideUserRateLimit").style.display="block";
				Get("hideUserInternetAccess").style.display="block";
			} else {
				Get("hideUserRateLimit").style.display="none";
				Get("hideUserInternetAccess").style.display="none";
			}
			Get("hideUserPskMethod").style.display="none";
			Get("hideUserNumberPsk").style.display="none";
			
		} 
	}
}

function changeUserPskMethod(value) {
	if (value==2) {
		Get("hideUserNumberPsk").style.display="block";
	} else {
		Get("hideUserNumberPsk").style.display="none";
	}
}

var primaryCallBack = function (data) {
	if (data && data.items) {
		hm.simpleObject.INPUT_FIELD_ID = "inputNewRadiusSecondaryIpValue";
		hm.simpleObject.removeOptions(document.getElementById("newRadiusSecondaryIpSelect"), data.items);
	}
}

var secondaryCallBack = function (data) {
	if (data && data.items) {
		hm.simpleObject.INPUT_FIELD_ID = "inputNewRadiusPrimaryIpValue";
		hm.simpleObject.removeOptions(document.getElementById("newRadiusPrimaryIpSelect"), data.items);
	}
}

function removeSSID() {
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected SSID.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

function doContinueOper() {
	submitAction('removeSSID');
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ssidProfiles" includeParams="none"/>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

function validateTextIsBlank(name, title)
{
	var message = hm.util.validateStringWithBlank(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}
    return true;
}

var isHiveApADServer = '<s:property value="%{isHiveApADServer}"/>';

function showADIntegrationDiv(checked) {
	
	if(checked){
		hm.util.show('adIntegrationDiv');
		
		var fullName = document.getElementById("domainFullName").value;
    	if(null == fullName || '' == fullName) {
    		// hide the div
    		hideTestAdDiv();
    	}
	}else{
		hm.util.hide('adIntegrationDiv');
	}
	
	if (!checked){
		document.getElementById("hideCreateLocal").style.display="";
	} else {
		document.getElementById("hideCreateLocal").style.display="none";
	}
	if (document.getElementById("accessMode2").checked){
		document.getElementById("hideCreateLocal").style.display="";
	}
}

function enableADintegration (flag) {
	if(flag) {
		document.getElementById("ADIntegration").disabled = "";
	} else {
		document.getElementById("ADIntegration").disabled = "disabled";
	}
}

function validateListSelection(selectedId){
	var el = document.getElementById(selectedId);
	if (el.options.length == 0) {//blank list
		hm.util.reportFieldError(el, '<s:text name="info.config.no.hiveAP.radius.server" />');
		return -1;
	} else {
		var index = el.selectedIndex;
		if (index < 0) {//no selection
			hm.util.reportFieldError(el, '<s:text name="info.config.select.hiveAP.radius.server" />');
			return -1;
		}else if(index == 0){
			hm.util.reportFieldError(el, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text >');
			return -1;
		}
		return index;
	}
}

function hideTestAdDiv(){
	
    document.getElementById("domainAdmin").value="";
    document.getElementById("domainAdminPasswd").value="";
    document.getElementById("domainAdminPasswd_text").value="";
    document.getElementById("domainTestUser").value="";
    document.getElementById("domainTestUserPasswd").value="";
    document.getElementById("domainTestUserPasswd_text").value="";
    
    document.getElementById("domainName").value="";
    document.getElementById("adServerIpAddress").value="";
    document.getElementById("baseDN").value="";
    
    hm.util.hide('showTestAdDiv');
    
    document.getElementById("chkPasswordADisplay").checked=true;
}

function resetHiveApIpInfo(){
	document.getElementById("hiveAPIpAddress").value="";
	document.getElementById("hiveAPNetmask").value="";
	document.getElementById("hiveAPGateway").value="";
	document.getElementById("hiveAPDNSServer").value="";
}

function resetADIntegrateInfo(){
    document.getElementById("ADIntegration").checked=false;
    
    hm.util.hide('adIntegrationDiv');
     
    document.getElementById("domainFullName").value="";
    // display the retrieve button
    displayRetrieveButton(true);
    // hide the joint/test sections
    hideTestAdDiv();
    // clear the AD Server information
    displayADInfo(false);
    
    // hide the message rows
    hm.util.hide('retrieveADRow');
    hm.util.hide("joinDNRow");
    hm.util.hide("testAuthRow");
}

function displayADInfo(flag) {
	if(flag) {
		hm.util.show('adServerInfoDiv');
		Get("adServerIp").innerHTML = document.getElementById("adServerIpAddress").value;
		Get("adServerBaseDN").innerHTML = document.getElementById("baseDN").value;
		// the AD information is complete, hide the button
		displayRetrieveButton(false);
	} else {
		hm.util.hide('adServerInfoDiv');
		// the AD information is incomplete, show the button
		displayRetrieveButton(true);
	}
}

function displayRetrieveButton(flag) {
	if(flag) {
		hm.util.show("retriveColumn");
		hm.util.hide("retriveTmpColumn");
	} else {
		hm.util.hide("retriveColumn");
		hm.util.show("retriveTmpColumn");
	}
}

function onChangePrimaryHiveAP(){
	var display=document.getElementById("hiveRadiusServerDiv").style.display;
	var selectedValue = document.getElementById("primaryHiveApRadius").value;
	//console.debug("get the select hiveAP info and show : value="+ selectedValue+" display="+display);
	if(selectedValue.trim() == '' || selectedValue.trim() == 'None available'){
		if(display!='none') {
			resetHiveApIpInfo();
		    document.getElementById("hiveRadiusServerDiv").style.display='none';
		    resetADIntegrateInfo();
		}
		enableADintegration(false);
		return;
	}
	enableADintegration(true);
	
	document.getElementById("hiveRadiusServerDiv").style.display='block';
	var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=changePrimaryHiveAP&primaryHiveApRadius="+selectedValue + "&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : settingIPCfginfo, timeout: 30000 }, null);
}

function settingIPCfginfo(o){
	eval("var details = " + o.responseText);
	if(details.dhcp){
		resetHiveApIpInfo();
	}else{
		document.getElementById("hiveAPIpAddress").value=details.ipAddress;
		document.getElementById("hiveAPNetmask").value=details.netmask;
		document.getElementById("hiveAPGateway").value=details.gateway;
	}
	document.getElementById("hiveAPDNSServer").value=details.dnsServer;
	
	var apMessage = document.getElementById("apMessage");
	if(details.resCode == 0){
		hm.util.hide("apServer");
		//details.fullDomainName
		if(details.fullDomainName != '') {
			var msg = '<s:text name="info.config.getApDomain.success" ><s:param>' + details.fullDomainName + '</s:param></s:text>';
			apMessage.innerHTML = msg;
			YAHOO.util.Dom.replaceClass(apMessage, "noteError", "noteInfo");
			delaySuccessHideNotes(5, "apServer");		
		}
	}else{
		if(details.newState){
			apMessage.innerHTML = '<s:text name="warn.config.hiveAp.newState" />';
			YAHOO.util.Dom.replaceClass(apMessage, "noteInfo", "noteError");
			hm.util.show("apServer");
		}else if(details.dhcp) {
			apMessage.innerHTML = '<s:text name="error.config.hiveAp.have.no.staticip.dns" />';
			YAHOO.util.Dom.replaceClass(apMessage, "noteInfo", "noteError");
			hm.util.show("apServer");
        } else {
        	apMessage.innerHTML = '';
			hm.util.hide("apServer");
        }
	}
	
    if(details.enableADIntegration){
        document.getElementById("ADIntegration").checked=true;
        document.getElementById("chkPasswordADisplay").checked=true;
        hm.util.show('adIntegrationDiv');
        if(details.sameFullDN){
	        document.getElementById("domainFullName").value=details.domainFullName;
	
	        hm.util.show('showTestAdDiv');
	        
	        document.getElementById("domainAdmin").value=details.domainAdmin;
	        document.getElementById("domainAdminPasswd").value=details.domainAdminPasswd;
	        document.getElementById("domainTestUser").value=details.domainTestUser;
	        document.getElementById("domainTestUserPasswd").value=details.domainTestUserPasswd;
	        
	        document.getElementById("domainName").value=details.domainName;
	        document.getElementById("adServerIpAddress").value=details.adServerIpAddress;
	        document.getElementById("baseDN").value=details.baseDN;
	        // show the AD information
	        displayADInfo(true);
	        
	    	// show the tip for the Text
			checkTextTip("domainFullName");
			checkTextTip("domainTestUser");
        }else{
        	document.getElementById("domainFullName").value=details.fullDomainName;
        	displayADInfo(false);
        	
        	hideTestAdDiv();
        }
     }else{
    	 resetADIntegrateInfo();
    }	
}

var tempDomainFullName = '<s:property value="%{adDomainFullName}"/>';
function changeDomainFullName() {
	
	if(tempDomainFullName == '' && !isFilledTestInfo) return;
	
	var domainFullNameText = document.getElementById("domainFullName").value
	//console.debug("domainFullName="+tempDomainFullName+" domainFullNameText="+domainFullNameText);
	var flag = tempDomainFullName == '' ? false : tempDomainFullName == domainFullNameText;
	showValuesInActiveDiretoryDiv(flag);
}

function isFilledTestInfo() {
        if(document.getElementById("domainAdmin").value!=""||
            document.getElementById("domainAdminPasswd").value!=""||
            document.getElementById("domainTestUser").value!=""||
            document.getElementById("domainTestUserPasswd").value!="")
            return true;
         else
            return false;
}

function showValuesInActiveDiretoryDiv(flag) {
	
    if(flag){
    	hm.util.show('showTestAdDiv');
        document.getElementById("domainAdmin").value= '<s:property value="%{dataSource.adDomainAdmin}"/>';
        document.getElementById("domainAdminPasswd").value = '<s:property value="%{dataSource.adDomainAdminPasswd}"/>';
        document.getElementById("domainTestUser").value = '<s:property value="%{dataSource.adDomainTestUser}"/>';
        document.getElementById("domainTestUserPasswd").value = '<s:property value="%{dataSource.adDomainTestUserPasswd}"/>';
        
        document.getElementById("domainName").value = '<s:property value="%{dataSource.adDomainName}"/>';
        document.getElementById("adServerIpAddress").value = '<s:property value="%{dataSource.adServerIpAddress}"/>';
        document.getElementById("baseDN").value = '<s:property value="%{dataSource.baseDN}"/>';        
    }else{
    	hideTestAdDiv();
    	// show retrive button
    	displayRetrieveButton(true);
        // hide the message row
        hm.util.hide('retrieveADRow');
        // set the discard join flag
        discardJoinInfo = 0;
    }
    displayADInfo(flag);
}

function retrieveADInfo() {
	
    var selectedValue = document.getElementById("primaryHiveApRadius").value;
    var domainFullName = document.getElementById("domainFullName").value;

    if (Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") {
        hm.util.reportFieldError(Get("primaryHiveApRadius"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
        Get("primaryHiveApRadius").focus();
        return;
    }
    
    if(!validateTextIsBlank(document.getElementById("domainFullName"), '<s:text name="config.radiusOnHiveAp.realmName" />'))
    	return;

    var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=retrieveADInfo&adDomainFullName="+ domainFullName+"&primaryHiveApRadius="+selectedValue+ "&ignore=" + new Date().getTime();

    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : retrieveADDetails, timeout: 120000 }, null);

    if(waitingPanel != null) {
        waitingPanel.show();
        clearAutoCompleteRaiudCombBoxStyle();
    }	
}

function retrieveADDetails(o) {
	if(waitingPanel != null) {
	    waitingPanel.hide();
	    resetAutoCompleteRaiudCombBoxStyle();
	} 
	eval("var details = " + o.responseText);
	var retrieveMessage = document.getElementById("retrieveMessage");
	if(details.resCode == 0){//success
		hm.util.hide('retrieveADRow');
	
		retrieveMessage.innerHTML = details.msg;
		YAHOO.util.Dom.replaceClass(retrieveMessage, "noteError", "noteInfo");
		
		delaySuccessHideNotes(5, "retrieveADRow");
		
		hm.util.show('showTestAdDiv');

        document.getElementById("domainName").value=details.domainName;
        document.getElementById("adServerIpAddress").value=details.adServer;
        document.getElementById("baseDN").value=details.baseDN;
        
        displayADInfo(true);
        
    	// hide retrive button
        displayRetrieveButton(false);
	}else{//fails
		hm.util.hide('retrieveADRow');
		
		retrieveMessage.innerHTML = details.msg;
		YAHOO.util.Dom.replaceClass(retrieveMessage, "noteInfo", "noteError");
		
		//delaySuccessHideNotes(5, "retrieveADRow");
		
		showValuesInActiveDiretoryDiv(false);
		
		hm.util.show('retrieveADRow');
	}
	hideTestAuthDive(true);

}

function hideTestAuthDive(flag) {
	if(flag) {
		hm.util.hide('showTestAuthDiv');
		hm.util.hide('testAuthDescriptId');
	} else {
		hm.util.show('showTestAuthDiv');
		hm.util.show('testAuthDescriptId');
	}
}

function changeJoinInfo(){
	
    var tmpAdmin= '<s:property value="%{dataSource.adDomainAdmin}"/>';
    var tmpAdminPasswd = '<s:property value="%{dataSource.adDomainAdminPasswd}"/>';
	var domainAdminText = document.getElementById("domainAdmin").value
	var enableObscure = isEnableObscurePasswd();
	var domainAdminPasswdText = enableObscure ? document.getElementById("domainAdminPasswd").value:
			document.getElementById("domainAdminPasswd_text").value;
	
	if(tmpAdmin==domainAdminText && tmpAdminPasswd==domainAdminPasswdText){
		hideTestAuthDive(false);
	    document.getElementById("domainTestUser").value = '<s:property value="%{dataSource.adDomainTestUser}"/>';
	    document.getElementById("domainTestUserPasswd").value = '<s:property value="%{dataSource.adDomainTestUserPasswd}"/>';
	    document.getElementById("domainTestUserPasswd_text").value = '<s:property value="%{dataSource.adDomainTestUserPasswd}"/>';
	    changeDNAdminText = false;
	}else{
		hideTestAuthDive(true);
	    document.getElementById("domainTestUser").value = '';
	    document.getElementById("domainTestUserPasswd").value = '';
	    document.getElementById("domainTestUserPasswd_text").value = '';
	    changeDNAdminText = true;
	}
	// check the domain user text
	checkTextTip("domainTestUser");
}

var discardJoinInfo = 0;
function testjoinDomain(isDiscardJoinInfo) {
    // 
    var selectedValue = document.getElementById("primaryHiveApRadius").value;
    var domainFullName = document.getElementById("domainFullName").value;
    var domainName = document.getElementById("domainName").value;
    var adServer = document.getElementById("adServerIpAddress").value;
    var baseDN = document.getElementById("baseDN").value;
    var adminName = document.getElementById("domainAdmin").value;
    var adminPasswd;
    if(isEnableObscurePasswd())
    	adminPasswd = document.getElementById("domainAdminPasswd").value;
    else
    	adminPasswd = document.getElementById("domainAdminPasswd_text").value;
    	
    
    if (Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") {
        hm.util.reportFieldError(Get("primaryHiveApRadius"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
        Get("primaryHiveApRadius").focus();
    }
    
    if(!validateTextIsBlank(document.getElementById("domainFullName"), '<s:text name="config.radiusOnHiveAp.realmName" />'))
    	return;
    
    if(domainName=="" || adServer=="" || baseDN==""){
        hm.util.reportFieldError(Get("domainFullName"), '<s:text name="error.ssid.retrieve.proceed" />');
        return;
    }
    if(adminName==""){
        hm.util.reportFieldError(document.getElementById("domainAdmin"), '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param></s:text>');
        return;
    }
    if(adminPasswd==""){
        hm.util.reportFieldError(isEnableObscurePasswd()?document.getElementById("domainAdminPasswd"):document.getElementById("domainAdminPasswd_text"), 
        		'<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.password" /></s:param></s:text>');
        return;
    }
    // save or discard admin credentials for 'Join' operation
    if(isDiscardJoinInfo){
    	discardJoinInfo = 1;
    } else {
    	discardJoinInfo = 0;
    }

	// ldapSaslWrapping 0:plain, 1:sign, 2:seal 
	//var ldapSaslWrappingEl = document.getElementById(formName + "_dataSource_ldapSaslWrapping");
	//var ldapSaslWrapping = ldapSaslWrappingEl.options[ldapSaslWrappingEl.selectedIndex].value;
	var ldapSaslWrapping = 0; // revert LDAP SASL feature in FUJI, make it always return ""(plain);
    
    var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=testjoinDomain&adDomainFullName="+ domainFullName+
        "&adDomainName="+domainName+"&adServerIpAddress="+adServer+"&baseDN="+baseDN+
        "&adDomainAdmin="+escape(adminName)+ "&adDomainAdminPasswd="+escape(adminPasswd)+"&primaryHiveApRadius="+selectedValue+
        "&discardJoinInfo="+discardJoinInfo+"&ignore=" + new Date().getTime();
    if (url.length > 0 && ldapSaslWrapping.length > 0) {
    	url += "&ldapSaslWrapping=" + ldapSaslWrapping;
	}

    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : testJoinDetails, timeout: 120000 }, null);

    if(waitingPanel != null) {
        waitingPanel.show();
        clearAutoCompleteRaiudCombBoxStyle();
    }   
}

function testJoinDetails(o) {
    if(waitingPanel != null) {
        waitingPanel.hide();
        resetAutoCompleteRaiudCombBoxStyle();
    } 
    eval("var details = " + o.responseText);
    var retrieveMessage = document.getElementById("joinDNMessage");
   	retrieveMessage.innerHTML = details.msg;
    if(details.resCode == 0){//success
    	hm.util.hide("joinDNRow");
    	
    	changeDNAdminText = false;
		YAHOO.util.Dom.replaceClass(retrieveMessage, "noteError", "noteInfo");
		
		delaySuccessHideNotes(5, "joinDNRow");
		
		hideTestAuthDive(false);
    }else{//fails
    	hm.util.hide("joinDNRow");
    	
    	YAHOO.util.Dom.replaceClass(retrieveMessage, "noteInfo", "noteError");
    	
    	hm.util.show("joinDNRow");
    	
    	hideTestAuthDive(true);
    }
}

function testADAuth() {
    // 
    var selectedValue = document.getElementById("primaryHiveApRadius").value;
    var domainFullName = document.getElementById("domainFullName").value;
    var domainName = document.getElementById("domainName").value;
    var adServer = document.getElementById("adServerIpAddress").value;
    var baseDN = document.getElementById("baseDN").value;    
    var testName = document.getElementById("domainTestUser").value;
    var testPasswd;
    if(isEnableObscurePasswd())
    	testPasswd = document.getElementById("domainTestUserPasswd").value;
    else
    	testPasswd = document.getElementById("domainTestUserPasswd_text").value;
    
    if (Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") {
        hm.util.reportFieldError(Get("primaryHiveApRadius"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
        Get("primaryHiveApRadius").focus();
        return;
    }
    
    if(!validateTextIsBlank(document.getElementById("domainFullName"), '<s:text name="config.radiusOnHiveAp.realmName" />'))
    	return;
    
    if(domainName=="" || adServer=="" || baseDN==""){
        hm.util.reportFieldError(Get("domainFullName"), '<s:text name="error.ssid.retrieve.proceed" />');
        return;
    }
    if(testName==""){
        hm.util.reportFieldError(document.getElementById("domainTestUser"), '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.testUserName" /></s:param></s:text>');
        return;
    } else {
    	// fixed Bug 15445, for the backend side slice the test user name to test.
    	var pos = testName.indexOf("@");
    	if(pos > -1) {
    		if(pos < testName.length) {
    			if(testName.substr(pos+1) != domainFullName) {
			        hm.util.reportFieldError(document.getElementById("domainTestUser"), 
			        		'<s:text name="error.ssid.test.domain.mismatch"/>');
			        return;
    			}
    		}
    	}
    }
    
    if(testPasswd==""){
        hm.util.reportFieldError(isEnableObscurePasswd()?document.getElementById("domainTestUserPasswd"):document.getElementById("domainTestUserPasswd_text"),
        		'<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.testUserPasswd" /></s:param></s:text>');
        return;
    }
    var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=testAuth&adDomainFullName="+ domainFullName+
        "&adDomainName="+domainName+"&adServerIpAddress="+adServer+"&baseDN="+baseDN+
        "&adDomainTestUser="+escape(testName)+ "&adDomainTestUserPasswd="+escape(testPasswd)+"&primaryHiveApRadius="+selectedValue+
        "&ignore=" + new Date().getTime();

    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : testAuthDetails, timeout: 120000 }, null);

    if(waitingPanel != null) {
        waitingPanel.show();
        clearAutoCompleteRaiudCombBoxStyle();
    }   
}

function testAuthDetails(o) {
    if(waitingPanel != null) {
        waitingPanel.hide();
        resetAutoCompleteRaiudCombBoxStyle();
    } 
    eval("var details = " + o.responseText);
    var retrieveMessage = document.getElementById("testAuthMessage");
    if(details.resCode == 0){//success
    	hm.util.hide("testAuthRow");
    	YAHOO.util.Dom.replaceClass(retrieveMessage, "noteError", "noteInfo");
		retrieveMessage.innerHTML = details.msg;
		
		delaySuccessHideNotes(5, "testAuthRow");
    }else{//fails
    	hm.util.hide("testAuthRow");
    	YAHOO.util.Dom.replaceClass(retrieveMessage, "noteInfo", "noteError");
		retrieveMessage.innerHTML = details.msg;
		
		hm.util.show("testAuthRow");
    }
}

//--------START: add push configuration(IP/Netmask/Gateway/DNS) to HiveAP--------------------
function pushConfig2HiveAP() {
    if (Get("primaryHiveApRadius").value=="" || Get("primaryHiveApRadius").value=="None available") {
        hm.util.reportFieldError(Get("primaryHiveApRadius"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.ssid.newRadius.primary.server" /></s:param></s:text>');
        Get("primaryHiveApRadius").focus();
        return;
    }
	if(!validateHiveAPConfig()) return;
	
	var hiveRadius = Get("primaryHiveApRadius").value;
	var apIPAddress = Get("hiveAPIpAddress").value;
	var netmask = Get("hiveAPNetmask").value;
	var gateway = Get("hiveAPGateway").value;
	var dnsServer = Get("hiveAPDNSServer").value;
    var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=pushConfigToAp"+
    		"&primaryHiveApRadius="+hiveRadius+
    		"&dataSource.staticHiveAPIpAddress="+apIPAddress+"&dataSource.staticHiveAPNetmask="+netmask+
    		"&dataSource.staticHiveAPGateway="+gateway+ "&dataSource.dnsServer="+dnsServer+
    		"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : pushConfigDetail, timeout: 120000 }, null);
	
    if(waitingPanel != null) {
        waitingPanel.show();
        clearAutoCompleteRaiudCombBoxStyle();
    } 
}
function pushConfigDetail(o) {
    if(waitingPanel != null) {
        waitingPanel.hide();
        resetAutoCompleteRaiudCombBoxStyle();
    } 
    eval("var details = " + o.responseText);

	var apMessage = document.getElementById("apMessage");
	if(details.resCode == 0){
		hm.util.hide("apServer");
		apMessage.innerHTML = "";
	}else{
		hm.util.hide("apServer");
    	YAHOO.util.Dom.replaceClass(apMessage, "noteInfo", "noteError");
    	apMessage.innerHTML = details.msg;
		hm.util.show("apServer");
	}    
}
//--------END: add push configuration to HiveAP--------------------

/**
 * checke the text value and show tip,
 * divide the operation to sub methods for single invoking
 */
function checkTextTip(textId) {
	if(textId == 'domainFullName') {
		showTextTip(textId, "domainNameTip");
		hideTextTip(textId);
	} else if(textId == 'domainTestUser') {
		showTextTip(textId, "domainUserNameTip");
		hideTextTip(textId);
	}
}

function showTextTip(textId, className) {
	var element = Get(textId);
	if(element && element.type =="text") {
		var textValue = element.value.trim();
		if(textValue.length == 0) {
			if(!YAHOO.util.Dom.hasClass(element, className)) {
				YAHOO.util.Dom.addClass(element, className);
			}
		}
	}
}

function hideTextTip(textId) {
	var element = Get(textId);
	if(element && element.type =="text") {
		var textValue = element.value.trim();
		if(textValue.length > 0) {
			var className = YAHOO.util.Dom.getAttribute(element, "class");
			if(className) {
				YAHOO.util.Dom.removeClass(element, className);
			}
		}
	}	
}

function resetAutoCompleteRaiudCombBoxStyle() {
	var primaryRadiusDiv = Get('primaryHiveApRadiusDiv');
	if(!YAHOO.util.Dom.hasClass(primaryRadiusDiv, "topFisrtLayer")) {
		YAHOO.util.Dom.addClass(primaryRadiusDiv, "topFisrtLayer");
	}
	var secondRadiusDiv = Get('secondHiveApRadiusDiv');
	if(!YAHOO.util.Dom.hasClass(secondRadiusDiv, "topSecondLayer")) {
		YAHOO.util.Dom.addClass(secondRadiusDiv, "topSecondLayer");
	}
}

function clearAutoCompleteRaiudCombBoxStyle() {
	YAHOO.util.Dom.removeClass(Get('primaryHiveApRadiusDiv'), "topFisrtLayer");
	YAHOO.util.Dom.removeClass(Get('secondHiveApRadiusDiv'), "topSecondLayer");
}

var successNotesTimeoutId;
function delaySuccessHideNotes(seconds, elId) {
	hm.util.show(elId);
	successNotesTimeoutId = setTimeout('hideSuccessNotes("'+ elId +'")', seconds * 2000);  // seconds
}
function hideSuccessNotes(elId) {
	hm.util.wipeOut(elId, 800);
}

function isEnableObscurePasswd(){
	return document.getElementById("chkPasswordADisplay").checked;
}

function showHidePskSelfRegAdv(checked){
	var ppskEle = document.getElementById("hidePskSelfRegAdv");
	if(checked){
		ppskEle.style.display="";
		Get("hidePPskServerIpDiv").style.display="";
		Get("blnMacBindingEnable").checked=false;
	}else{
		ppskEle.style.display="none";
		Get("hidePPskServerIpDiv").style.display="none";
		document.getElementById(formName + "_dataSource_enablePpskSelfReg").checked=false;
		document.getElementById("ppskServerIp").selectedIndex=0;
		document.getElementById(formName + "_ppskECwpId").selectedIndex=0;
		document.getElementById(formName + "_dataSource_ppskOpenSsid").value="";
		Get("ppskRadiusServerTr").style.display="none";
		Get(formName + "_radiusPpskId").selectedIndex=0;
		
	}
}

function changePskMacBindingValue(checked) {
	if (checked) {
		showHidePskSelfRegAdv(false);
		Get("hidePPskServerIpDiv").style.display="";
	} else {
		Get("hidePPskServerIpDiv").style.display="none";
		document.getElementById("ppskServerIp").selectedIndex=0;
	}
}

function changePpskEcwpValue(selectValue) {
    var url = "<s:url action="ssidProfiles" includeParams="none" />?operation=changePpskEcwp"+
    		"&ppskECwpId="+selectValue+
    		"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : changePpskEcwpDetail, timeout: 120000 }, null);
	
}

function changePpskEcwpDetail(o) {
    eval("var details = " + o.responseText);
	if(details.t){
		Get("ppskRadiusServerTr").style.display="";
	}else{
		Get("ppskRadiusServerTr").style.display="none";
		Get(formName + "_radiusPpskId").selectedIndex=0;
	}    
}

function validatePpskSelfReg(){
	var accessModel = document.getElementById("accessMode2");
	var ppskSelfEnable = document.getElementById(formName + "_dataSource_enablePpskSelfReg");
	var ppskMacBindEnable = Get("blnMacBindingEnable");
	if(accessModel.value == ACCESS_MODE_PSK && ppskSelfEnable.checked){
		var ppskServerIp = document.getElementById("ppskServerIp");
		var ppskECwpId = document.getElementById(formName + "_ppskECwpId");
		var ppskOpenSsid = document.getElementById(formName + "_dataSource_ppskOpenSsid");
		if(ppskServerIp.value.length == 0){
			 hm.util.reportFieldError(ppskServerIp, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.ppskreg.server" /></s:param></s:text>');
			 return false;
		}
		if(ppskECwpId.selectedIndex == 0){
			hm.util.reportFieldError(ppskECwpId, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.cwp" /></s:param></s:text>');
			return false;
		}
		if(ppskOpenSsid.value.length == 0){
			hm.util.reportFieldError(ppskOpenSsid, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.ppskreg.regSsid" /></s:param></s:text>');
			return false;
		}
		
		if (Get("ppskRadiusServerTr").style.display!="none") {
			if(Get(formName + "_radiusPpskId").selectedIndex==0){
				hm.util.reportFieldError(Get(formName + "_radiusPpskId"), '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.ppskRadiusServer" /></s:param></s:text>');
				return false;
			}
		}
	}
	
	if(accessModel.value == ACCESS_MODE_PSK && ppskMacBindEnable.checked){
		var ppskServerIp = document.getElementById("ppskServerIp");
		if(ppskServerIp.value.length == 0){
			 hm.util.reportFieldError(ppskServerIp, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.ppskreg.server" /></s:param></s:text>');
			 return false;
		}
	}
	
	return true;
}

function clickOsDectionCheckbox(checked){
	if (checked) {
		Get("hideOsDectionNote").style.display="";
	} else {
		Get("hideOsDectionNote").style.display="none";
	}
}


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
		document.getElementById(formName + "_dataSource_cuthreshold").value="<%=SsidProfile.CUTHRESHOLD_DEFAULT_VALUE%>";
		document.getElementById(formName + "_dataSource_memberthreshold").value="<%=SsidProfile.MEMBERTHRESHOLD_DEFAULT_VALUE%>";
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

//Added for MDM from Dakar
function enableMDM(checked){
	document.getElementById(formName + "_dataSource_rootURLPath").disabled = !checked;
	document.getElementById(formName + "_dataSource_mdmUserName").disabled = !checked;
	document.getElementById(formName + "_dataSource_mdmType").disabled = !checked;
	document.getElementById(formName + "_dataSource_enableAppleOs").disabled = !checked;
	document.getElementById(formName + "_dataSource_enableMacOs").disabled = !checked;
	
	if(Get("__checkbox_"+formName+"_dataSource_enableAppleOs")){
		Get("__checkbox_"+formName+"_dataSource_enableAppleOs").disabled=!checked;
	}
	if(Get("__checkbox_"+formName+"_dataSource_enableMacOs")){
		Get("__checkbox_"+formName+"_dataSource_enableMacOs").disabled=!checked;
	}
	
	document.getElementById("chkToggleDisplay").disabled = !checked;
	
	if(checked){
		if(document.getElementById("chkToggleDisplay").checked){
			document.getElementById("userPassword").disabled =false ;
			document.getElementById("cfUserPassword").disabled = false;
		}else{
			document.getElementById("userPassword_text").disabled = false;
			document.getElementById("cfUserPassword_text").disabled = false;
		}
	}else{
		document.getElementById("userPassword").disabled = true;
		document.getElementById("cfUserPassword").disabled = true;
		document.getElementById("userPassword_text").disabled = true;
		document.getElementById("cfUserPassword_text").disabled = true;
	}
}

function validateMDM() {
	var element = document.getElementById(formName + "_dataSource_enableMDM");
	
	if(!element.checked) {
		return true;
	}
	
	// rootURLPath
	element = document.getElementById(formName + "_dataSource_rootURLPath");
	if(element != null){
		
	
	var value = element.value;
	
	if(value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.urlpath" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	if (!(value.indexOf('http') == 0
			|| value.indexOf('https') == 0)
			|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
	    	hm.util.reportFieldError(element, 
		    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.urlpath"/></s:param></s:text>');
	    	element.focus();
			return false;
		
	 } 
	}
    
	// user name
	element = document.getElementById(formName + "_dataSource_mdmUserName");
	if(element != null){
	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.username" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	if (element.value.indexOf(' ') > -1) {
        hm.util.reportFieldError(element, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.username" /></s:param></s:text>');
        element.focus();
        return false;
	}
	}
	
	var elementC;
	
	if(document.getElementById("chkToggleDisplay") != null){
	if (document.getElementById("chkToggleDisplay").checked)
	{
		element = document.getElementById("userPassword");
		elementC = document.getElementById("cfUserPassword");
	}
	else
	{
		element = document.getElementById("userPassword_text");
		elementC = document.getElementById("cfUserPassword_text");
	}
	}
	if(element != null){
	
	// password
	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.password" /></s:param></s:text>');
		element.focus();
		return false;
	}
	}
	
	if(elementC !=null){
	// password confirmation
	if(elementC.value.trim().length == 0) {
		hm.util.reportFieldError(elementC, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.password.confirm" /></s:param></s:text>');
		elementC.focus();
		return false;
	}
	}
	
	if(element !=null && elementC !=null){
	if(element.value != elementC.value) {
		hm.util.reportFieldError(element, '<s:text name="config.ssid.advanced.mdm.password.mismatch" />');
		element.focus();
		return false;
	}
	}
	
	return true;
}

function disabledCWP(flag) {
    document.getElementById("enableCwpSelect").disabled = flag;
}

function enabledCloudAuth(flag, noRefresh) {
    disabledCWP(false);
	if(flag) {
		document.getElementById(formName + "_dataSource_macAuthEnabled").checked = false;
		hm.util.hide("hideSimpleRadius");
	}
	
    if(document.getElementById("accessMode2").checked) {
        // Private PSK
        if(flag) {
            document.getElementById("hideCwpSelect").style.display="none";
            document.getElementById(formName +"_cwpId").value=-1;
            
            document.getElementById("macAuthTr").style.display="none";
            if (!isHHMApplication) {
                document.getElementById("hideUseGuestManagerDiv").style.display="none";
            }
    
            document.getElementById("hidePskSelfReg").style.display="none";
            document.getElementById("macBindingTr").style.display="none";
            document.getElementById("hideRadiusPAPCHAP").style.display="none";
        } else {
            document.getElementById("macAuthTr").style.display="";
            if (!isHHMApplication && isFullMode) {
                document.getElementById("hideUseGuestManagerDiv").style.display="block";
            }
            if(isFullMode) {
            	document.getElementById("hidePskSelfReg").style.display="block";
            	document.getElementById("macBindingTr").style.display="";
            }
        }
    } else if(document.getElementById("accessMode3").checked) {
        // 802.1x
        if(flag) {
             document.getElementById("hideCwpSelect").style.display="none";
             document.getElementById(formName +"_cwpId").value=-1;
                
             document.getElementById("macAuthTr").style.display="none";
             document.getElementById("hideRadiusPAPCHAP").style.display="none";
             // for IDM authentication proxy support
             hm.util.show("hideSimpleRadius");
        } else {
             document.getElementById("macAuthTr").style.display="";
             hm.util.show("hideSimpleRadius");
        }
    } else if(document.getElementById("accessMode1").checked || document.getElementById("accessMode5").checked) {
        // PSK, Open
        if(flag) {
            document.getElementById("macAuthTr").style.display="none";
            document.getElementById("hideRadiusPAPCHAP").style.display="none";
            
            document.getElementById("enableCwpSelect").checked = true;
            document.getElementById("hideCwpSelect").style.display="";
            
            disabledCWP(true);
        } else {
            document.getElementById("macAuthTr").style.display="";
        }
    } else {
        // WEP
        if(flag) {
            document.getElementById("macAuthTr").style.display="none";
            document.getElementById("hideRadiusPAPCHAP").style.display="none";
            
            document.getElementById("enableCwpSelect").checked = true;
            document.getElementById("hideCwpSelect").style.display="";
            
            disabledCWP(true);
        } else {
            document.getElementById("macAuthTr").style.display="";
        }
    }
    if(Get("manageGuestIDMAnchor")) {
        Get("manageGuestIDMAnchor").style.display = (flag ? "" : "none");
    }
    if(!noRefresh) {
    	dynamicUpdateCWPs(flag);
    }
}
var firstLoad = true;
function dynamicUpdateCWPs(idmEnabled) {
    if(firstLoad) {
        firstLoad = false;
    } else {
        var cwps = "<s:property value='cwpsJSONStr'/>";
        var cwpsIdm ="<s:property value='idmCwpsJSONStr'/>";
        var cwpsAuth ="<s:property value='authCwpsJSONStr'/>";
        var notOpenMode = !document.getElementById("accessMode5").checked;
        var array = eval("("+(idmEnabled ? (notOpenMode ? cwpsAuth : cwpsIdm) : cwps)+")");
        var select = Get(formName +"_cwpId");
        emptyOptions(select);
        initOptios(select, array);
    }
}
function emptyOptions(element) {
	while(element.options && element.options.length) {
		element.options[0] = null;
	}
}
function initOptios(element, array) {
	for(var i=0; i<array.length; i++) {
		var option = document.createElement("option");
		option.value = array[i].key, option.text = array[i].value;
		
		try {
			element.add(option, element.options[null]);
		} catch(e) {
			element.add(option, null);
		}
	}
}
function enableWmmAdmctlDisplayStyle(checked){
	Get(formName +"_dataSource_enabledUnscheduled").disabled = !checked;
	
	if(!checked){
		Get(formName +"_dataSource_enabledUnscheduled").checked = false;
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
<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 15px">
</s:if>
<s:else>
<div id="content">
</s:else>
<s:form action="ssidProfiles">
	<s:hidden name="selectDosType" />
	<s:hidden name="blnMacAuth" />
	<s:hidden name="keyManagement" />
	<s:hidden name="wlanId" />
	<s:hidden name="editMacFilterId" />
	<s:hidden name="editScheduleId" />
	<s:hidden name="editLocalUserGroupId" />
	<s:hidden name="editSelectUserProfileId" />
	<s:hidden name="editLocalUserGroupIdForRadius" />
	
	<s:hidden name="newRadiusPrimaryIp" />
	<s:hidden name="newRadiusSecondaryIp" />
	
	<s:hidden name="newSelfVlanId" />
	<s:hidden name="newDefaultVlanId" />
	<s:hidden name="newOptionVlanId" />
	<s:hidden name="newUserInfoVlanId" />
	<s:hidden name="fromObjId" />
	<s:hidden name="manualLstForward" />
	
	<s:hidden name="dataSource.adDomainName" id="domainName"/>
	<s:hidden name="dataSource.adServerIpAddress" id="adServerIpAddress"/>
	<s:hidden name="dataSource.baseDN" id="baseDN"/>
	<s:if test="%{easyMode && lastExConfigGuide!=null}">
		<div class="topFixedTitle">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="buttons" align="right">
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
				
			</table>
		</div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
		</table>
	</s:if>
	<s:else>

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
	</table>
	</s:else>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{easyMode && lastExConfigGuide!=null}">
			<table border="0" cellspacing="0" cellpadding="0" width="860px" id="ssidEditTable" >
			</s:if>
			<s:else>
			<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="860px" id="ssidEditTable">
			</s:else>
				<tr>
					<td>
					<%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr style="display:<s:property value="hideUserInfo"/>">
							<td class="labelT1"><font color="#767676"><s:text name="config.ssid.express.title"/>
							</font>
							</td>
						</tr>
						<tr style="display:<s:property value="hideUserInfo"/>">
							<td>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr align="center">
										<td align="right" width="390px"><s:radio label="Gender" id="userCategoryEmployee"
											name="dataSource.expressEmployee"
											list="#{1:''}"
											value="%{dataSource.expressEmployee}"
											title="%{ExpressEmployeeTitle}"
											onclick="changeUserCategory(5);" 
											disabled="%{disabledName}"/>
											<span title='<s:text name="config.ssid.express.employee.title"/>'>
											<font style="font-size: 14px; font-weight: bold; color:#474646">Internal Access</font></span></td>
										<td align="left" style="padding-left: 16px"><s:radio label="Gender" id="userCategory"
											name="dataSource.userCategory"
											list="#{2:''}"
											value="%{dataSource.userCategory}"
											title="%{ExpressGuestTitle}"
											onclick="changeUserCategory(2);" 
											disabled="%{disabledName}"/>
											<span title='<s:text name="config.ssid.express.guest.title"/>'>
											<font style="font-size: 14px; font-weight: bold; color:#474646">Guest Access</font></span></td>
									</tr>
								</table>
							</td>
						</tr>
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
										onchange="changeSsidName();"/>&nbsp;
										<s:if test="%{blnShowSsid}">
											<s:text name="config.ssid.ssidName_range" />
										</s:if>
										<s:else>
											<s:text name="config.localUserGroup.userNamePrefixRange" />
										</s:else>
										</td>
								</tr>
								<tr>
									<td class="labelT1">
										<s:text name="config.ssid.head.ssid" /><font color="red"><s:text name="*"/></font>
									</td>
									<td><s:textfield name="dataSource.ssid" size="24"
										onkeypress="return hm.util.keyPressPermit(event,'ssid');"
										maxlength="%{ssidNameLength}"/>&nbsp;
										<s:if test="%{blnShowSsid}">
											<s:text name="config.ssid.ssidName_range" />
										</s:if>
										<s:else>
											<s:text name="config.localUserGroup.userNamePrefixRange" />
										</s:else></td>
								</tr>
								<tr>
									<td class="labelT1" width="165px"><s:text name="config.ssid.radioRate.radioMode"></s:text></td>
									<td><s:select name="dataSource.radioMode" list="%{enumRadioMode}" listKey="key"
										listValue="value" cssStyle="width: 250px;" /></td>
								</tr>
								<tr style="display:<s:property value="hideSsid"/>">
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
									<tr> 
										<td style="padding:0 2px 5px 6px">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td align="right">
														<div style="display:<s:property value="showOption"/>" id="showOption">
															<a href="#btos" class="textLink" id="buttonOptions" onClick="showDefaultOption();">Advanced Access Security Settings&gt;&gt;
																 </a>
														</div>
													</td>
												</tr>
											</table>
										</td>	
									</tr>
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
																<td width="220px" id="accessMode8021xTd" style="display:<s:property value="hideMacAuthTr"/>">
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
										<td style="padding:0 2px 0 6px">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<div style="display:<s:property value="hideEnabledDefaultSetting"/>" id="hideEnabledDefaultSettingDiv">
															<s:checkbox name="dataSource.enabledDefaultSetting" id="enabledDefaultSetting" onclick="showDefaultSetting(this.checked);" ></s:checkbox><label id="defaultOptionLabel"></label>
														</div>
													</td>
												</tr>
											</table>
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
										<td style="padding:0 2px 0 160px">
										<div style="display:<s:property value="hideLocalUserGroup"/>" id="hideLocalUserGroup">
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td colspan="2">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td style="padding-left: 2px;"> <label id="pskMsg"></label></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<s:push value="%{localUserGroupOptions}">
													<td colspan="2"><tiles:insertDefinition
														name="optionsTransfer" /></td>
												</s:push>
											</tr>
											<tr>
												<td height="4" colspan="2"></td>
											</tr>
										</table>
										</div>
										</td>
									</tr>
									<s:if test="%{fullMode}">
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
									</s:if>
									<tr>
										<td>
										<div style="display:none" id="hideOptionDiv">
										<fieldset><legend><s:text name="config.ssid.advanceOption" /></legend>
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td>
														<div style="display:<s:property value="hideRekeyPeriod"/>" id="hideRekeyPeriod">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="4"></td>
															</tr>
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
																	size="10" maxlength="4"
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
																	<td style="padding:8px 2px 0 6px" width="320px"><s:checkbox id="enable80211w"
																		name="enable80211w" value="%{enable80211w}"
																		onclick="enableWpa2mfpType(this.checked)" />
																		<s:text name="config.ssid.80211w" /></td>
																	<td id="wpa2mfpTypeTD" style="margin-left:0px;display:<s:property value="%{hideWpa2mfpType}"/>" 
																		width="160px" align="left"><s:radio 
																		label="Gender" id="wpa2mfpType"
																		name="wpa2mfpType"
																		list="#{1:'Mandatory',2:'Optional'}"
																		value="%{wpa2mfpType}"
																	/></td>
																</tr>
															</table>
														</div>
													</td>
												</tr>
											</table>
										</fieldset>
										<br/>
										</div>
										</td>
									</tr>
									<s:if test="%{easyMode}">
									<tr> 
										<td style="padding:6px 2px 0 6px">
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
									</s:if>
									<tr id="cwpAuthTr">
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td style="padding:0 4px 0 6px"><s:checkbox name="dataSource.cwpSelectEnabled"  id="enableCwpSelect" onclick="changeEnableCwpSelect(this.checked)"></s:checkbox><LABEL id="cwpTitleLabel"></LABEL></td>
													<td valign="middle">
														<div style="display:<s:property value="hideCwpSelect"/>" id="hideCwpSelect">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td><s:select name="cwpId" list="%{list_cwp}" listKey="id"
																		listValue="value" cssStyle="width: 200px;"
																		onchange="changeCwpAuthValue()"/></td>
																	<td width="20px">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newCwp')"><img class="dinl"
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
																			<a class="marginBtn" href="javascript:submitAction('editCwp')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
															</table>
														</div>
														<div style="display:<s:property value="hideUserPolicyDiv"/>" id="hideUserPolicyDiv">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td><s:select
																		name="userPolicyId" list="%{userPolicyProfiles}" listKey="id"
																		listValue="value" cssStyle="width: 200px;" /></td>
																	<td width="20px">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newUserPolicy')"><img class="dinl"
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
																			<a class="marginBtn" href="javascript:submitAction('editUserPolicy')"><img class="dinl"
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
															onclick="changeEnabledUseGuestManager();"/><s:text name="config.ssid.useGuestManager" />
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr id="macAuthTr" style="display:<s:property value="hideMacAuthTr"/>">
										<td>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding:0 2px 0 6px"> <s:checkbox
														name="dataSource.macAuthEnabled" value="%{dataSource.macAuthEnabled}" 
														onclick="changeCwpAuthValue();"/><s:text name="config.ssid.enabledMAC" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td style="padding:2px 0 0 10px">
										<div style="display:<s:property value="hideRadius"/>" id="hideRadius">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td colspan="4">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td> <label id="noRadiusMsg"></label></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td width="150px"><s:text name="config.configTemplate.wizard.step3.title"></s:text></td>
													<td width="200px"><s:select name="radiusId" list="%{list_radius}" listKey="id"
															listValue="value" cssStyle="width: 280px;" /></td>
													<td width="20px">
														<s:if test="%{writeDisabled == 'disabled'}">
															<img class="dinl marginBtn"
															src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" />
														</s:if>
														<s:else>
															<a class="marginBtn" href="javascript:showNewRadiusPanel()"><img class="dinl"
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
															<a class="marginBtn" href="javascript:submitAction('editRadius')"><img class="dinl"
															src="<s:url value="/images/modify.png" />"
															width="16" height="16" alt="Modify" title="Modify" /></a>
														</s:else>
													</td>
												</tr>
												<tr>
													<td height="4px"/>
												</tr>
											</table>
										</div>
										</td>	
									</tr>
									<s:if test="%{blnShowSsid}">
									<tr>
										<td style="padding-left:10px">
											<div style="display:<s:property value="hideNewRadiusPanelDiv"/>" id="newRadiusPanel">
												<fieldset>
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td height="4px"/>
																</tr>
																<tr>
																	<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																		class="button" onClick="submitAction('saveNewRadiusSetting');" 
																		<s:property value="updateDisabled" />></td>
																	<td><input type="button" name="cancel" value="Cancel"
																		class="button" onClick="hideNewRadiusPanel();"
																		<s:property value="updateDisabled" />></td>
																</tr>
																<tr>
																	<td height="4px"/>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="sepLine"><img
															src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
													</tr>
												</table>
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td width="190px"><s:radio label="Gender" id="newRadiusType"
																			name="dataSource.newRadiusType"
																			list="%{enumHiveAPRadius}"
																			listKey="key" listValue="value"
																			value="%{dataSource.newRadiusType}"
																			onclick="changeNewRadiusType(2);" /></td>
																	<td width="190px"><s:radio label="Gender" id="newRadiusType"
																			name="dataSource.newRadiusType"
																			list="%{enumExternalRadius}"
																			listKey="key" listValue="value"
																			value="%{dataSource.newRadiusType}"
																			onclick="changeNewRadiusType(1);" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.name" /><font color="red"><s:text name="*"/></font></td>
																	<td><s:textfield name="newRadiusName" size="33" maxlength="32"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" />
																			&nbsp;<s:text name="config.ssid.ssidName_range" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.server" /><font color="red"><s:text name="*"/></font></td>
																	<td colspan="3"><ah:createOrSelect divId="errorDisplayPrimaryIp"
																		list="availableIpAddress" typeString="IpAddress"
																		selectIdName="newRadiusPrimaryIpSelect"
																		inputValueName="inputNewRadiusPrimaryIpValue" 
																		swidth="200px"/></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
															<div style="display:<s:property value="hideNewRadiusPanelPassDiv"/>" id="newRadiusSecretPanel">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.secret" /><font color="red"><s:text name="*"/></font></td>
																		<td><s:password id="newRadiusSecret" name="newRadiusSecret"
																			size="33" maxlength="32" showPassword="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																			<s:textfield id="newRadiusSecret_text" name="newRadiusSecret"
																			size="33" maxlength="32" cssStyle="display:none" disabled="true"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																		<td>&nbsp;<s:text name="config.ssid.ssidName_range" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.confirmSecret" /><font color="red"><s:text name="*"/></font></td>
																		<td><s:password id="newRadiusConfirmSecret" showPassword="true"
																			name="newRadiusConfirmSecret" value="%{newRadiusSecret}" size="33"
																			maxlength="32"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
																			<s:textfield id="newRadiusConfirmSecret_text" cssStyle="display:none" disabled="true"
																			name="newRadiusConfirmSecret" value="%{newRadiusSecret}" size="33"
																			maxlength="32"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td>
																						<s:checkbox id="chkToggleDisplayPrimarySecret" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																							onclick="hm.util.toggleObscurePassword(this.checked,['newRadiusSecret','newRadiusConfirmSecret'],['newRadiusSecret_text','newRadiusConfirmSecret_text']);" />
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
														<td> 
															<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.secondary.server" /></td>
																	<td colspan="3"><ah:createOrSelect divId="errorDisplaySecondaryIp"
																		list="availableIpAddress" typeString="IpAddressSec"
																		selectIdName="newRadiusSecondaryIpSelect"
																		inputValueName="inputNewRadiusSecondaryIpValue" 
																		swidth="200px"/></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
														<div style="display:<s:property value="hideNewRadiusPanelPassDiv"/>" id="newRadiusSecondSecretPanel">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.secret" /></td>
																	<td><s:password id="newRadiusSecondSecret" name="newRadiusSecondSecret"
																		size="33" maxlength="32" showPassword="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusSecondSecret_text" name="newRadiusSecondSecret"
																		size="33" maxlength="32" cssStyle="display:none" disabled="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>&nbsp;<s:text name="config.ssid.ssidName_range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.confirmSecret" /></td>
																	<td><s:password id="newRadiusSecondConfirmSecret" showPassword="true"
																		name="newRadiusSecondConfirmSecret" value="%{newRadiusSecondSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusSecondConfirmSecret_text" cssStyle="display:none" disabled="true"
																		name="newRadiusSecondConfirmSecret" value="%{newRadiusSecondSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td>
																					<s:checkbox id="chkToggleDisplaySecondSecret" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																						onclick="hm.util.toggleObscurePassword(this.checked,['newRadiusSecondSecret','newRadiusSecondConfirmSecret'],['newRadiusSecondSecret_text','newRadiusSecondConfirmSecret_text']);" />
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
														<td style="padding:4px 2px 2px 10px">
														<div style="display:<s:property value="hideHiveApLocalUserGroupDiv"/>" id="hideHiveApLocalUserGroupDiv">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td colspan="2">
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td style="padding-left:2px"> <label id="hiveApRadiusMsg"></label></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr>
																<s:push value="%{hiveApLocalUserGroupOptions}">
																	<td colspan="2"><tiles:insertDefinition
																		name="optionsTransfer" /></td>
																</s:push>
															</tr>
														</table>
														</div>
														</td>
													</tr>
													<tr>
														<td> <a href="javascript:submitAction('newRadius')"><s:text name="config.ssid.newRadius.moreSetting" /></a></td>
													</tr>
												</table>
												</fieldset>
											</div>
										</td>
									</tr>
									</s:if>
									<tr>
										<td style="padding-left:10px">
										<div style="display:<s:property value="hideRadiusPAPCHAP"/>" id="hideRadiusPAPCHAP">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="4px"></td>
												</tr>
												<tr>
													<td width="270px">
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
															<s:checkbox name="dataSource.enablePpskSelfReg" value="%{dataSource.enablePpskSelfReg}" onclick="showHidePskSelfRegAdv(this.checked);"/><s:text name="config.configTemplate.ppskreg.enable"/>
														</td>
													</tr>
													<tr>
														<td style="padding:0 2px 0 20px">
															<div id="hidePPskServerIpDiv" style="display:<s:property value="hidePPskServerIpDiv"/>">
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td class="labelT1" width="170px" ><s:text name="config.configTemplate.ppskreg.server"/><font color="red"><s:text name="*"/></font>
																		</td>
																		<td><s:select name="dataSource.ppskServerIp" id="ppskServerIp" 
																				list="%{ppskServerList}" listKey="key" listValue="value" 
																				cssStyle="width: 160px;" />
																		</td>
																	</tr>
																</table>
															</div>
														</td>
													</tr>
													<tr>
														<td style="padding:0 2px 0 20px"><div id="hidePskSelfRegAdv" style="display:<s:property value="hidePskSelfRegAdv"/>">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td class="labelT1" width="170px"><s:text name="config.ssid.cwp"/><font color="red"><s:text name="*"/></font>
																</td>
																<td>
																	<s:select name="ppskECwpId"
																	list="%{list_ppskECwp}" listKey="id" listValue="value" 
																	onchange="changePpskEcwpValue(this.options[this.selectedIndex].value);"
																	cssStyle="width: 160px;" />
																	
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newPpskECwp')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editPpskECwp')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.configTemplate.ppskreg.regSsid"/><font color="red"><s:text name="*"/></font>
																</td>
																<td><s:textfield name="dataSource.ppskOpenSsid" value="%{dataSource.ppskOpenSsid}" maxlength="%{ssidNameLength}" cssStyle="width: 156px;"/>
																	<s:text name="config.ssid.ssidName_range" />
																</td>
															</tr>
															<tr id="ppskRadiusServerTr" style="display:<s:property value="hidePpskRadiusServerTr"/>">
																<td class="labelT1"><s:text name="config.ssid.ppskRadiusServer"/><font color="red"><s:text name="*"/></font></td>
																<td>
																	<s:select name="radiusPpskId" list="%{list_radius}" listKey="id"
																		listValue="value" cssStyle="width: 160px;"/>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newRadiusPpsk')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editRadiusPpsk')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>
														</div></table></td>
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
						<s:if test="%{blnShowSsid == false}">
						<tr>
							<td style="padding: 4px 4px 4px 4px;"> 
								<div style="display:<s:property value="hideSimpleRadius"/>" id="hideSimpleRadius">
									<fieldset><legend><s:text name="config.ssid.newRadius.legend" /></legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td> 
												<table cellspacing="0" cellpadding="0" border="0" width="95%">
													<tr>
														<td width="190px"><s:radio label="Gender" id="newRadiusType"
																name="dataSource.newRadiusType"
																list="%{enumHiveAPRadius}"
																listKey="key" listValue="value"
																value="%{dataSource.newRadiusType}"
																onclick="changeSimpleNewRadiusType(2);" /></td>
														<td width="190px"><s:radio label="Gender" id="newRadiusType"
																name="dataSource.newRadiusType"
																list="%{enumExternalRadius}"
																listKey="key" listValue="value"
																value="%{dataSource.newRadiusType}"
																onclick="changeSimpleNewRadiusType(1);" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<div style="display:<s:property value="hideNewRadiusPanelPassDiv"/>" id="newSimpleRadiusPanel">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.server" /><font color="red"><s:text name="*"/></font></td>
																	<td colspan="3"><ah:createOrSelect divId="errorDisplayPrimaryIp"
																		list="availableIpAddress" typeString="IpAddress"
																		selectIdName="newRadiusPrimaryIpSelect"
																		inputValueName="inputNewRadiusPrimaryIpValue" 
																		callbackFn="primaryCallBack" swidth="200px"/></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.secret" /><font color="red"><s:text name="*"/></font></td>
																	<td><s:password id="newRadiusSecret" name="newRadiusSecret"
																		size="33" maxlength="32" showPassword="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusSecret_text" name="newRadiusSecret"
																		size="33" maxlength="32" cssStyle="display:none" disabled="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>&nbsp;<s:text name="config.ssid.ssidName_range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.confirmSecret" /><font color="red"><s:text name="*"/></font></td>
																	<td><s:password id="newRadiusConfirmSecret" showPassword="true"
																		name="newRadiusConfirmSecret" value="%{newRadiusSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusConfirmSecret_text" cssStyle="display:none" disabled="true"
																		name="newRadiusConfirmSecret" value="%{newRadiusSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td>
																					<s:checkbox id="chkToggleDisplayPrimarySecret" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																						onclick="hm.util.toggleObscurePassword(this.checked,['newRadiusSecret','newRadiusConfirmSecret'],['newRadiusSecret_text','newRadiusConfirmSecret_text']);" />
																				</td>
																				<td>
																					<s:text name="admin.user.obscurePassword" />
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
															<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.secondary.server" /></td>
																	<td colspan="3"><ah:createOrSelect divId="errorDisplaySecondaryIp"
																		list="availableIpAddress" typeString="IpAddressSec"
																		selectIdName="newRadiusSecondaryIpSelect"
																		inputValueName="inputNewRadiusSecondaryIpValue" 
																		callbackFn="secondaryCallBack" swidth="200px"/></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.secret" /></td>
																	<td><s:password id="newRadiusSecondSecret" name="newRadiusSecondSecret"
																		size="33" maxlength="32" showPassword="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusSecondSecret_text" name="newRadiusSecondSecret"
																		size="33" maxlength="32" cssStyle="display:none" disabled="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>&nbsp;<s:text name="config.ssid.ssidName_range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.confirmSecret" /></td>
																	<td><s:password id="newRadiusSecondConfirmSecret" showPassword="true"
																		name="newRadiusSecondConfirmSecret" value="%{newRadiusSecondSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																		<s:textfield id="newRadiusSecondConfirmSecret_text" cssStyle="display:none" disabled="true"
																		name="newRadiusSecondConfirmSecret" value="%{newRadiusSecondSecret}" size="33"
																		maxlength="32"
																		onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td>
																					<s:checkbox id="chkToggleDisplaySecondSecret" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																						onclick="hm.util.toggleObscurePassword(this.checked,['newRadiusSecondSecret','newRadiusSecondConfirmSecret'],['newRadiusSecondSecret_text','newRadiusSecondConfirmSecret_text']);" />
																				</td>
																				<td>
																					<s:text name="admin.user.obscurePassword" />
																				</td>
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
										<tr>
											<td>
												<div style="display:<s:property value="hideHiveApLocalUserGroupDiv"/>" id="newHiveApRadiusPanelDiv">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td> 
															<table cellspacing="0" cellpadding="0" border="0" width="100%">
													       		<tr id="fe_primaryHiveApRadius" style="display: none;">
													       			<td colspan="2" id="textfe_primaryHiveApRadius" class="noteError" align="left" style="padding-left: 170px;">ToChange</td>
												       			</tr>
																<tr>
																	<td colspan="2">
																	   <table cellspacing="0" cellpadding="0" border="0">
																	   	<%-- 
																	       <tr>
																				<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.server" /><font color="red"><s:text name="*"/></font></td>
																				<td><s:select id="primaryHiveApRadius" name="dataSource.selectNewHiveApRadiusPrimaryIp"
																								list="availablePrimaryHiveApIpAddress"
																								cssStyle="width: 200px;" onchange="onChangePrimaryHiveAP();"/>
																				</td>
																	       </tr>
																	       --%>
											       				        	<tr>
																				<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.primary.server" /><font color="red"><s:text name="*"/></font></td>																        		
																        		<td>
																        		<div class="topFisrtLayer" id="primaryHiveApRadiusDiv">
																	        		<s:textfield id="primaryHiveApRadius" name="dataSource.selectNewHiveApRadiusPrimaryIp"
																	                    maxlength="18" />
																	                    <a id="acComboBoxPrimary" tabindex="-1" href="javascript:void(0);" class="acDropdown"></a>
																        			<div id="primaryRadiusContainer"></div>
																        		</div>
																        		</td>
																        	</tr>
																	   </table>
																	</td>
																</tr>
														       <tr id="apServer" style="display:none"><td class="noteInfo" id="apMessage" style="padding-left: 170px;"/></tr>
																<tr>
																    <td style="padding: 5px 10px 5px;" colspan="2">
																        <div id="hiveRadiusServerDiv" style="display:<s:property value="hideHiveRadiusServerInfoDiv"/>">
																        <table cellspacing="0" cellpadding="0" border="0">
																			<tr id="fe_hiveAPIpAddress" style="display: none">
																				<td/>
																				<td class="noteError" id="textfe_hiveAPIpAddress">To be changed</td>
																			</tr>
																			<tr id="fe_hiveAPNetmask" style="display: none">
																				<td/><td/><td/><td/>
																				<td class="noteError" id="textfe_hiveAPNetmask">To be changed</td>
																			</tr>
																			<tr>
																                <td class="labelT1" width="120px"><s:text name="config.ssid.radiusServer.ipAddress" /></td>
																                <td><s:textfield id="hiveAPIpAddress" name="dataSource.staticHiveAPIpAddress"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																                <td width="30px"/>    
																                <td class="labelT1" width="92px"><s:text name="config.ssid.radiusServer.netmask" /></td>
																                <td><s:textfield id="hiveAPNetmask" name="dataSource.staticHiveAPNetmask"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																            </tr>
																			<tr id="fe_hiveAPGateway" style="display: none">
																				<td/>
																				<td class="noteError" id="textfe_hiveAPGateway">To be changed</td>
																			</tr>
																			<tr id="fe_hiveAPDNSServer" style="display: none">
																				<td/><td/><td/><td/>
																				<td class="noteError" id="textfe_hiveAPDNSServer">To be changed</td>
																			</tr>
																            <tr>
																                <td class="labelT1"><s:text name="config.ssid.radiusServer.gateway" /></td>
																                <td><s:textfield id="hiveAPGateway" name="dataSource.staticHiveAPGateway"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																                <td width="30px"/>    
																                <td class="labelT1"><s:text name="config.ssid.radiusServer.dns" /></td>
																                <td><s:textfield id="hiveAPDNSServer" name="dataSource.dnsServer"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
															                    <td style="padding-left: 10px;"><input type="button" name="apply" class="button"
															                    	style="width:100px"
															                    	value="<s:text name="config.radiusOnHiveAp.button.apply.ap.config"/>"
						                                                           	onClick="pushConfig2HiveAP();" <s:property value="writeDisabled" />></td>
																            </tr>
																        </table>
																        </div>
																    </td>
																</tr>
													       		<tr id="fe_secondHiveApRadius" style="display: none;">
													       			<td colspan="2" id="textfe_secondHiveApRadius" class="noteError" align="left" style="padding-left: 170px;">ToChange</td>
												       			</tr>
																<tr>
																    <td colspan="2">
																        <table cellspacing="0" cellpadding="0" border="0">
																        	<tr>
																        		<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.secondary.server" /></td>
																        		<td>
																        		<div class="topSecondLayer" id="secondHiveApRadiusDiv">
																	        		<s:textfield id="secondHiveApRadius" name="dataSource.selectNewHiveApRadiusSecondaryIp"
																	                    maxlength="18" />
																	                    <a id="acComboBoxSecond" tabindex="-1" href="javascript:void(0);" class="acDropdown"></a>
																        			<div id="secondRadiusContainer"></div>
																        		</div>																        		
																        		</td>
																        	</tr>
																        	<%--
																        	<tr>
																				<td class="labelT1" width="160px"><s:text name="config.ssid.newRadius.secondary.server" /></td>
																				<td><s:select name="dataSource.selectNewHiveApRadiusSecondaryIp" value="%{dataSource.selectNewHiveApRadiusSecondaryIp}"
																								list="availableHiveApIpAddress"
																								headerKey="" headerValue=""
																								cssStyle="width: 200px;"/>
																				</td>
																            </tr>
																             --%>
																        </table>
																    </td>
																</tr>
																<tr>
																    <td colspan="2" style="padding-top: 5px;"><s:checkbox id="ADIntegration" name="dataSource.enableADIntegration" 
																    	onclick="showADIntegrationDiv(this.checked);"/>
																        <label for="ADIntegration"><s:text name="config.ssid.radiusServer.adIntegration"/></label></td>
																</tr>
																<tr id="fe_domainFullName" style="display: none;">
													       			<td colspan="2" id="textfe_domainFullName" class="noteError" align="left" style="padding-left: 170px;">ToChange</td>
												       			</tr>
																<tr id="adIntegrationDiv" style="display: <s:property value="showAdIntegrationDiv"/>">
																    <td style="padding: 5px 10px 5px;" colspan="3">
																        <table cellspacing="0" cellpadding="0" border="0" style="table-layout: fixed;" width="100%">
						                                                    <tr>
						                                                        <td class="labelT1" width="146"><label><s:text name="config.radiusOnHiveAp.realmName" /></label></td>
						                                                        <td width="305px"><s:textfield size="48" name="dataSource.adDomainFullName" id="domainFullName" maxlength="64"
						                                                            onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" cssStyle="width: 200px"
						                                                            onchange="changeDomainFullName();" onkeyup="checkTextTip(this.id);" onblur="checkTextTip(this.id);"/>
						                                                            <s:text name="config.radiusOnHiveAp.passRange" /></td>
						                                                        <td style="padding-left: 10px;" id="retriveColumn" width="200"><input style="width: 190px" type="button" name="retrieve" value="Retrieve Directory Information" class="button" 
						                                                           onClick="retrieveADInfo();" <s:property value="writeDisabled" />></td>
						                                                        <td id="retriveTmpColumn" style="display: none;" width="210"></td>
						                                                    </tr>
						                                                    <tr id="retrieveADRow" style="display:none"><td/><td class="noteInfo" id="retrieveMessage" colspan="3"/></tr>													        
						                                                    <tr id="adServerInfoDiv" style="display: none;">
						                                                    	<td colspan="3">
						                                                    		<table cellspacing="0" cellpadding="0" border="0">
						                                                    			<tr>
						                                                    				<td class="labelT1" width="145"><label><s:text name="config.ssid.radiusServer.adServerIp" /></label></td>
						                                                    				<td id="adServerIp"><s:property value="dataSource.adServerIpAddress" /></td>
						                                                    			</tr>
						                                                    			<tr>
						                                                    				<td class="labelT1" width="145"><label><s:text name="config.ssid.radiusServer.adServerBaseDN" /></label></td>
						                                                    				<td id="adServerBaseDN"><s:property value="dataSource.baseDN" /></td>
						                                                    			</tr>
						                                                    		</table>
						                                                    	</td>
						                                                    </tr>
						                                                    <tr id="showTestAdDiv" style="display: <s:property value="showTestAdDiv"/>">
						                                                        <td colspan="4">
						                                                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
						                                                            	<tr id="showTestJoinDiv">
						                                                                  	<td>
						                                                                  		<table cellspacing="0" cellpadding="0" border="0">
						                                                                  <tr>
									                                                        <td class="labelT1" width="145"><label><s:text name="config.radiusOnHiveAp.userName" /></label></td>
									                                                        <td width="306px"><s:textfield size="48" name="dataSource.adDomainAdmin" id="domainAdmin" maxlength="32"
									                                                            onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" 
									                                                            onchange="changeJoinInfo();"
									                                                            cssStyle="width: 200px"/>
									                                                            <s:text name="config.radiusOnHiveAp.nameRange1" /></td>
						                                                                  </tr>
									                                                    <tr>
									                                                        <td class="labelT1"><label><s:text name="config.radiusOnHiveAp.password" /></label></td>
									                                                        <td><s:password size="48" name="dataSource.adDomainAdminPasswd" id="domainAdminPasswd" maxlength="64"
									                                                            onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" 
									                                                            onchange="changeJoinInfo();" cssStyle="width: 200px"/>
									                                                            <s:textfield id="domainAdminPasswd_text" name="dataSource.adDomainAdminPasswd" size="48" maxlength="64"
									                                                              onchange="changeJoinInfo();"
									                                                              cssStyle="display:none; width: 200px;" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
									                                                            <s:text name="config.radiusOnHiveAp.passRange" /></td>
									                                                    <%-- </tr>
														                                <tr>
																							<td class="labelT1"><s:text name="config.radiusOnHiveAp.ldap.sasl.wrapping" /></td>
																							<td><s:select name="dataSource.ldapSaslWrapping" cssStyle="width: 93px;"
																								list="%{ldapSaslWrappings}" listKey="key" listValue="value" /></td> --%>
									                                                        <td style="padding-left: 10px;" id="joinDomainColumn">
									                                                        	<input type="button" name="jointDomain" value="Join and Save" class="button"
									                                                        	style="width: 100px;" onClick="testjoinDomain();" <s:property value="testADDisabled" />>
									                                                        	<input type="button" name="jointDomain" value="Join and Discard" class="button"
									                                                        	style="width: 110px;" onClick="testjoinDomain(true);" <s:property value="testADDisabled" />>
									                                                        	</td>
																						</tr>
						                                                                  		</table>
						                                                                  	</td>
						                                                            	</tr>
									                                                    <tr id="joinDNRow" style="display:none"><td class="noteInfo" id="joinDNMessage" style="padding-left: 158px;"/></tr>														        
				                                                            			<tr id="testAuthDescriptId" style="display: none;"><td class="labelT1"><label><s:text name="config.ssid.radiusServer.testDescription"/></label></td></tr>
						                                                            	<tr id="showTestAuthDiv">
						                                                            		<td>
							                                                            		<table cellspacing="0" cellpadding="0" border="0">
												                                                    <tr>
												                                                        <td class="labelT1" width="145"><s:text name="config.ssid.radiusServer.testUserName" /><font color="red"><s:text name="*"/></font></td>
												                                                        <td width="306px"><s:textfield size="48" name="dataSource.adDomainTestUser" id="domainTestUser" maxlength="32"
												                                                            onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" cssStyle="width: 200px"
												                                                            onkeyup="checkTextTip(this.id);" onblur="checkTextTip(this.id);"/>
												                                                            <s:text name="config.radiusOnHiveAp.nameRange1" /></td>
											                                                          	<td/>
												                                                    </tr>																        
												                                                    <tr>
												                                                        <td class="labelT1"><s:text name="config.ssid.radiusServer.testUserPasswd" /><font color="red"><s:text name="*"/></font></td>
												                                                        <td><s:password size="48" name="dataSource.adDomainTestUserPasswd" id="domainTestUserPasswd" maxlength="64"
												                                                            onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" cssStyle="width: 200px"/>
												                                                            <s:textfield id="domainTestUserPasswd_text" name="dataSource.adDomainTestUserPasswd" size="48" maxlength="64"
												                                                              cssStyle="display:none;width: 200px;" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
												                                                            <s:text name="config.radiusOnHiveAp.passRange" /></td>
												                                                        <td style="padding-left: 10px;" id="testAuthColumn"><input style="width: 140px" type="button" name="testAuth" value="Test Authentication" class="button" 
												                                                           onClick="testADAuth();" <s:property value="testADDisabled" />></td>
												                                                    </tr>
							                                                            		</table>
						                                                            		</td>
						                                                            	</tr>
									                                                    <tr id="testAuthRow" style="display:none"><td class="noteInfo" id="testAuthMessage" style="padding-left: 158px;"/></tr>
						                                                            	<tr>
						                                                            		<td>
						                                                            		<table cellspacing="0" cellpadding="0" border="0">
									                                                    <tr>
									                                                    	<td width="150">&nbsp;</td>
									                                                        <td><s:checkbox id="chkPasswordADisplay" name="ignore" value="true" 
									                                                            onclick="hm.util.toggleObscurePassword(this.checked,['domainAdminPasswd','domainTestUserPasswd'],['domainAdminPasswd_text','domainTestUserPasswd_text']);" 
									                                                            disabled="%{writeDisable4Struts}" /><label><s:text name="admin.user.obscurePassword" /></label></td>
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
												</table>
												</div>
											</td>
										</tr>
									</table>
								</fieldset>
								</div>
							</td>
						</tr>
						</s:if>
						<tr>
							<td style="padding: 4px 4px 4px 4px;"> 
								<div style="display:<s:property value="hideUserInfo"/>">
								<fieldset><legend><s:text name="config.ssid.userInfo.title" /></legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td colspan="2" align="right"> 
											<s:if test="%{writeDisabled == 'disabled'}">
												<a class="textLinkNoPermit" href="#nothingDo" id="moreSettingWord">
												<s:property value="moreSettingWord" /></a>
											</s:if>
											<s:else>
												<a class="textLink" href="javascript:submitAction('editSimpleModeUserProfile')" id="moreSettingWord">
												<s:property value="moreSettingWord" /></a>
											</s:else>
											
											</td>
										</tr>
									</table>
									<table cellspacing="0" cellpadding="0" border="0" >
										<tr>
											<td colspan="2">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr style="display:<s:property value="hideUserCategory"/>" id="hideUserCategory">
														<td style="padding: 2px 0 4px 6px" colspan="2">
															<s:checkbox name="dataSource.expressUserCategoryVoice" disabled="%{disabledName}" onclick="changeUserCategoryCheck(this.checked);"></s:checkbox>
															<s:text name="config.ssid.userInfo.userCategory"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="170px"><s:text name="topology.client.title.vlan" /><font color="red"><s:text name="*"/></font></td>
														<td width="154px"><ah:createOrSelect divId="errorDisplayUserProfileVlan"
															list="availableVlan" typeString="UserProfileVlan"
															selectIdName="newUserProfileVlanSelect"
															inputValueName="inputNewUserProfileVlanValue" 
															swidth="152px"/></td>
														<td><s:text name="config.localUserGroup.vlanIdRange" /></td>
													</tr>
													<tr id="userManagerTr" style="display:<s:property value="hideUserManagerTr"/>">
														<td style="padding: 2px 0 4px 6px" colspan="2">
															<s:checkbox name="dataSource.blnUserManager"></s:checkbox>
															<s:text name="config.userprofile.userManager" />
														</td>
														<td>
															<s:if test="%{displayNewUserOperation}">
																<a class="textLink" href="javascript:submitAction('newUserOperator')">
																	<s:text name="config.ssid.userInfo.newUserOperator" /></a>
															</s:if>
															<s:else>
																<a class="textLinkNoPermit" href="#nothingDo">
																	<s:text name="config.ssid.userInfo.newUserOperator" /></a>
																
															</s:else>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<div style="display:<s:property value="hideUserRateLimit"/>" id="hideUserRateLimit">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td class="labelT1" width="170px"><s:text name="config.ssid.userInfo.userRateLimit"></s:text></td>
															<td> <s:textfield name="dataSource.userRatelimit" maxlength="6" size="24"
																onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																<s:text name="config.ssid.userInfo.userRateLimit.range" /> </td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<div style="display:<s:property value="hideUserPskMethod"/>" id="hideUserPskMethod">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td class="labelT1" width="170px"><s:text name="config.ssid.userInfo.pskCreateMethod"></s:text></td>
															<td><s:select name="dataSource.userPskMethod" value="%{dataSource.userPskMethod}"
																	list="#{3:'Manual',2:'Automatic'}"
																	cssStyle="width: 152px;"
																	onchange="changeUserPskMethod(this.options[this.selectedIndex].value);"/> </td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<div style="display:<s:property value="hideUserNumberPsk"/>" id="hideUserNumberPsk">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td class="labelT1" width="170px"><s:text name="config.ssid.userInfo.numberPsk"></s:text></td>
															<td> <s:textfield name="dataSource.userNumberPsk" maxlength="4" size="24"
																onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																<s:text name="config.ssid.userInfo.numberPsk.range" /> </td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<div style="display:<s:property value="hideUserInternetAccess"/>" id="hideUserInternetAccess">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td style="padding: 2px 0 4px 6px">
																<s:checkbox name="dataSource.userInternetAccess"></s:checkbox>
																<s:text name="config.ssid.userInfo.internetAccess" /></td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										
										<tr>
											<td colspan="2">
												<div style="display:<s:property value="hideCreateLocal"/>" id="hideCreateLocal">
													<s:if test="%{lastExConfigGuide!=null}">
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td style="padding: 2px 0 4px 6px">
																<s:if test="%{writeDisabled == 'disabled'}">
																	<a class="textLinkNoPermit" href="#nothingDo">
																		<s:text name="config.ssid.userInfo.newLocalUser"/></a>
																</s:if>
																<s:else>
																	<a class="textLink" href="javascript:openLocalUserPanel('Manage Local Users', 'expressList');">
																		<s:text name="config.ssid.userInfo.newLocalUser"/></a>
																</s:else>
															</td>
														</tr>
													</table>
													</s:if>
												</div>
											</td>
										</tr>
									</table>
								</fieldset>
								</div>
							</td>
						</tr>
						
							<%-- I am the split line--%>
						<tr>
							<td style="padding: 4px 4px 4px 4px;"> 
								<div style="display:<s:property value="hideSsid"/>">
								<fieldset><legend><s:text name="config.configTemplate.wizard.step4.title" /></legend>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="4px"/>
									</tr>
									<tr>
										<td style="padding-left:80px">
											<div style="display:<s:property value="hideSelfReg"/>" id="hideSelfReg">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td colspan="3"><s:text name="config.configTemplate.wizard.step4.subtitle1"></s:text></td>
													</tr>
													<tr>
														<td width="200px" style="padding-left:80px"><s:select
																name="userProfileSelfRegId"
																list="%{availableUserProfile}" listKey="id" listValue="value"
																cssStyle="width: 280px;" /> </td>
														<td width="20px">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:showSelfNewUserProfilePanel();"><img class="dinl"
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
																<a class="marginBtn" href="javascript:submitAction('editUserProfileSelfReg')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>
													<tr>
														<td height="4px">
													</tr>
													
													<tr>
														<td style="padding-left:80px" colspan="3">
															<div style="display:<s:property value="hideSelfNewUserProfileDiv"/>" id="hideSelfNewUserProfileDiv">
																<fieldset>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td> 
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td height="4px"/>
																				</tr>
																				<tr>
																					<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																						class="button" onClick="submitAction('saveSelfNewUserProfileSetting');" 
																						<s:property value="updateDisabled" />></td>
																					<td><input type="button" name="cancel" value="Cancel"
																						class="button" onClick="hideSelfNewUserProfilePanel();"
																						<s:property value="updateDisabled" />></td>
																				</tr>
																				<tr>
																					<td height="4px"/>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td class="sepLine"><img
																			src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
																	</tr>
																</table>
																<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td class="labelT1" width="120px"><s:text
																			name="config.userprofile.name" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2">
																			<s:textfield name="newSelfUserProfileName" size="24" id="newSelfUserProfileName"
																			maxlength="32"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" />
																			<s:text name="config.name.range"/></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.userprofile.attribute" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2">
																			<s:textfield name="newSelfAttributeValue" size="24" maxlength="4" id="newSelfAttributeValue"
																		     onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																		     <s:text name="config.userprofile.attrubute.range"/></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.userprofile.vlan" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2"><ah:createOrSelect divId="errorDisplaySelfVlan"
																			list="availableVlan" typeString="SelfVlan"
																			selectIdName="newSelfVlanSelect"
																			inputValueName="inputNewSelfVlanValue" 
																			swidth="200px"/></td>
																	</tr>
																	<tr>
																		<td style="padding: 2px 0 4px 6px" colspan="3">
																			<s:checkbox name="dataSource.newSelfBlnUserManager"></s:checkbox>
																			<s:text name="config.userprofile.userManager" />
																		</td>
																	</tr>
																	<tr>
																		<td> 
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<a class="textLinkNoPermit" href="#nothingDo"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																			</s:if>
																			<s:else>
																				<a class="textLink" href="javascript:submitAction('newUserProfileSelfReg')"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																			</s:else>
																		</td>
																	</tr>
																</table>
																</fieldset>
															</div>
														</td>
													</tr>
												</table>
											</div>
										</td>		
									</tr>
									<tr>
										<td height="4px"/>
									</tr>
									<tr>
										<td style="padding-left:40px">
											<div style="display:<s:property value="hideOpenWep"/>" id="hideOpenWep">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding-left:40px"><s:text name="config.configTemplate.wizard.step4.subtitle5"></s:text></td>
												</tr>
											</table>
											</div>
										</td>		
									</tr>
									<tr>
										<td style="padding-left:40px">
											<div style="display:<s:property value="hideRadiusOnly"/>" id="hideAuth">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding-left:40px"><s:text name="config.configTemplate.wizard.step4.subtitle3"></s:text></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td style="padding-left:40px">
											<div style="display:<s:property value="hidePSKDefaultUserProfile"/>" id="hidePSKDefaultUserProfile">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td height="4px"/>
												</tr>	
												<tr>
													<td style="padding-left:40px"><s:text name="config.ssid.defaultPSK.userProfile"></s:text></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td height="4px"/>
									</tr>		
									<tr>
										<td style="padding-left:40px">
											<div style="display:<s:property value="hideOpenWepRadius"/>" id="hideOpenWepRadius">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td style="padding-left:30px"> <label id="radiusMsg"></label></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td width="200px" style="padding-left:120px"><s:select
																		name="userProfileDefaultId"
																		list="%{availableUserProfile}" listKey="id" listValue="value"
																		cssStyle="width: 280px;" /> </td>
																<td width="20px">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:showDefaultNewUserProfilePanel();"><img class="dinl"
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
																		<a class="marginBtn" href="javascript:submitAction('editUserProfileDefault')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																</td>
															</tr>
															<tr>
																<td height="4px"/>
															</tr>
															<tr>
																<td style="padding-left:120px" colspan="3">
																	<div style="display:<s:property value="hideDefaultNewUserProfileDiv"/>" id="hideDefaultNewUserProfileDiv">
																		<fieldset>
																		<table cellspacing="0" cellpadding="0" border="0" width="100%">
																			<tr>
																				<td> 
																					<table cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<td height="4px"/>
																						</tr>
																						<tr>
																							<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																								class="button" onClick="submitAction('saveDefaultNewUserProfileSetting');" 
																								<s:property value="updateDisabled" />></td>
																							<td><input type="button" name="cancel" value="Cancel"
																								class="button" onClick="hideDefaultNewUserProfilePanel();"
																								<s:property value="updateDisabled" />></td>
																						</tr>
																						<tr>
																							<td height="4px"/>
																						</tr>
																					</table>
																				</td>
																			</tr>
																			<tr>
																				<td class="sepLine"><img
																					src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
																			</tr>
																		</table>
																		<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
																			<tr>
																				<td class="labelT1" width="120px"><s:text
																					name="config.userprofile.name" /><font color="red"><s:text name="*"/></font></td>
																				<td colspan="2">
																					<s:textfield name="newDefaultUserProfileName" size="24" id="newDefaultUserProfileName"
																					maxlength="32"
																					onkeypress="return hm.util.keyPressPermit(event,'name');" />
																					<s:text name="config.name.range"/></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><s:text name="config.userprofile.attribute" /><font color="red"><s:text name="*"/></font></td>
																				<td colspan="2">
																					<s:textfield name="newDefaultAttributeValue" size="24" maxlength="4" id="newDefaultAttributeValue"
																				     onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																				     <s:text name="config.userprofile.attrubute.range"/></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><s:text name="config.userprofile.vlan" /><font color="red"><s:text name="*"/></font></td>
																				<td colspan="2"><ah:createOrSelect divId="errorDisplayDefaultVlan"
																					list="availableVlan" typeString="DefaultVlan"
																					selectIdName="newDefaultVlanSelect"
																					inputValueName="inputNewDefaultVlanValue" 
																					swidth="200px"/></td>
																			</tr>
																			<tr>
																				<td style="padding: 2px 0 4px 6px" colspan="3">
																					<s:checkbox name="dataSource.newDefaultBlnUserManager"></s:checkbox>
																					<s:text name="config.userprofile.userManager" />
																				</td>
																			</tr>
																			<tr>
																				<td> 
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<a class="textLinkNoPermit" href="#nothingDo"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																					</s:if>
																					<s:else>
																						<a class="textLink" href="javascript:submitAction('newUserProfileDefault')"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																					</s:else>
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
									<tr>
										<td>
											<div style="display:<s:property value="hideRadiusOnly"/>" id="hideAuth2">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td height="4px"/>
												</tr>
												<tr>
													<td style="padding-left:80px"><s:text name="config.configTemplate.wizard.step4.subtitle4"></s:text></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td style="padding-left:40px">
											<div style="display:<s:property value="hidePSKUserMessage"/>" id="hidePSKUserMessage">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td style="padding-left:40px" colspan="2"><s:text name="config.ssid.pskUserInfo"></s:text></td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr>
										<td style="padding-left:160px">	
											<div style="display:<s:property value="hideRadiusOrPSK"/>" id="hideUserProfileList">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td height="4px"/>
													</tr>
													<tr>
														<td colspan="2">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td> <label id="selectUserProfileMsg"></label></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<s:push value="%{userProfileOptions}">
															<td colspan="2"><tiles:insertDefinition
																name="optionsTransfer" /></td>
														</s:push>
													</tr>
													<tr>
														<td height="4px"/>
													</tr>
													<tr>
														<td colspan="2">
															<div style="display:<s:property value="hideOptionNewUserProfileDiv"/>" id="hideOptionNewUserProfileDiv">
																<fieldset>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td> 
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td height="4px"/>
																				</tr>
																				<tr>
																					<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																						class="button" onClick="submitAction('saveOptionNewUserProfileSetting');" 
																						<s:property value="updateDisabled" />></td>
																					<td><input type="button" name="cancel" value="Cancel"
																						class="button" onClick="hideOptionNewUserProfilePanel();"
																						<s:property value="updateDisabled" />></td>
																				</tr>
																				<tr>
																					<td height="4px"/>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td class="sepLine"><img
																			src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
																	</tr>
																</table>
																<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td class="labelT1" width="120px"><s:text
																			name="config.userprofile.name" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2">
																			<s:textfield name="newOptionUserProfileName" size="24" id="newOptionUserProfileName"
																			maxlength="32"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" />
																			<s:text name="config.name.range"/></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.userprofile.attribute" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2">
																			<s:textfield name="newOptionAttributeValue" size="24" maxlength="4" id="newOptionAttributeValue"
																		     onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																		     <s:text name="config.userprofile.attrubute.range"/></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.userprofile.vlan" /><font color="red"><s:text name="*"/></font></td>
																		<td colspan="2"><ah:createOrSelect divId="errorDisplayOptionVlan"
																			list="availableVlan" typeString="OptionVlan"
																			selectIdName="newOptionVlanSelect"
																			inputValueName="inputNewOptionVlanValue" 
																			swidth="200px"/></td>
																	</tr>
																	<tr>
																		<td style="padding: 2px 0 4px 6px" colspan="3">
																			<s:checkbox name="dataSource.newOptionBlnUserManager"></s:checkbox>
																			<s:text name="config.userprofile.userManager" />
																		</td>
																	</tr>
																	<tr>
																		<td> 
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<a class="textLinkNoPermit" href="#nothingDo"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																			</s:if>
																			<s:else>
																				<a class="textLink" href="javascript:submitAction('newUserProfileRadiusMore')"><s:text name="config.ssid.newRadius.moreSetting" /></a>
																			</s:else>
																		</td>
																	</tr>
																</table>
																</fieldset>
															</div>
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<table>
												<tr>
													<td style="padding-left:68px">
														<s:checkbox name="dataSource.enableOsDection" onclick="clickOsDectionCheckbox(this.checked);"></s:checkbox><s:text name="config.ssid.enable.os.detection" />
													</td>
												</tr>
												<tr style="display:<s:property value="hideOsDectionNote"/>" id="hideOsDectionNote">
													<td style="padding-left:75px" class="noteInfo">
														<s:text name="config.ssid.classification.rule.note"/>
													</td>
												</tr>
											</table>
										</td>
									</tr>	
									<tr>
										<td>	
										<div style="display:<s:property value="hideRadiusOnly"/>" id="hideCheckBoxOnly">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td height="4px"/>
												</tr>
												<tr>
													<td style="padding-left:70px">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td><s:checkbox name="dataSource.chkUserOnly" onclick="showActionEdit(this.checked);" ></s:checkbox> </td>
																<td width="100%"><s:text name="config.configTemplate.wizard.chkOnly"></s:text></td>
															</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td height="4px"/>
												</tr>
												<tr>
													<td style="padding-left:138px">
														<div style="display:<s:property value="hideAction"/>" id="hideAction">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td width="100px" style="padding-left:4px"><s:text name="config.configTemplate.wizard.denyAction"></s:text></td>
																<td><s:select name="dataSource.denyAction"	id="denyAction" value="%{dataSource.denyAction}" list="%{enumDenyAction}" listKey="key"
																		listValue="value" cssStyle="width: 108px;" onchange="changeDenyAction(this)"
																		title="Action to perform if RADIUS returns an attribute for a user profile that is not selected"/></td>
															</tr>
															<tr>
																<td height="2px"/>
															</tr>
															<tr>
																<td width="100px" style="padding-left:4px"><s:text name="config.configTemplate.wizard.actionTime"></s:text></td>
																<td><s:textfield name="dataSource.actionTime" id="actionTime" maxlength="9" size="15"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"
																		disabled="%{actionTimeDisabled}"/>
																	<s:text name="config.configTemplate.wizard.actionTimeRange"></s:text></td>
															</tr>
															<tr>
																<td height="2px"/>
															</tr>
															
															<tr>
																<td colspan="2"><s:checkbox name="dataSource.chkDeauthenticate"></s:checkbox>
																				<s:text name="config.configTemplate.wizard.chkDeauthenticate"></s:text></td>
															</tr>
														</table>
														</div>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
								</table>
								</fieldset>
								</div>
							</td>
						</tr>
						<tr>
							<td height="4px"/>
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
														<td>
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
														<td style="padding-right:12px;" colspan="2">
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
														<td>
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
														<td style="padding-right:12px;">
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
														<td>
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
														<td>
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
												</td>
											</tr>
											<tr>
												<td style="padding:4px 0 0 30px;" colspan="2" >
													<fieldset>
														<legend><s:text name="config.ssid.rateac.legend" /></legend>
														<table>
															<tr>
																<td style="width:20px;">
																	<s:checkbox id="enableACRateSet" name="dataSource.enableACRateSet" onclick="changeACRateSet(this.checked);"/>
																</td>
																<td style="padding-top: 4px;"><s:text name="config.ssidProfile.enableACRateSet"/></td>
															</tr>
															<tr style="display:none" colspan="2">
																<td id="acRateSetError"></td>
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
												<%--tr>
													<td class="labelT1"><s:text
														name="config.air.screen.rule.group.fullName" /></td>
													<td><s:select id="asRuleGroup" name="asRuleGroup" value="%{asRuleGroup}"
													    list="%{asRuleGroupList}" listKey="id" listValue="value"
													    cssStyle="width: 200px;" />
													</td>
													<td>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newAsRuleGroup')"><img class="dinl"
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
														<a class="marginBtn" href="javascript:submitAction('editAsRuleGroup')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
													</td>
												</tr> --%>
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
			<%-- schedules panel --%>	
						<tr>
							<td>
								<div style="display:<s:property value="showScheduleDiv"/>" id="showScheduleDiv">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" onclick="showHideScheduleDiv(1);" style="cursor: pointer">
												<img src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg" style="display: inline"
													/>&nbsp;&nbsp;<s:text name="config.ssid.tab.schedules" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="hideScheduleDiv"/>" id="hideScheduleDiv">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" onclick="showHideScheduleDiv(2);" style="cursor: pointer">
											<img src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
												alt="Hide Option" class="expandImg" style="display: inline"
												/>&nbsp;&nbsp;<s:text name="config.ssid.tab.schedules" />
										</td>
									</tr>
									<tr>
										<td style="padding-left:30px">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td height="4px"></td>
												</tr>
												<tr>
													<s:push value="%{schedulerOptions}">
														<td colspan="2"><tiles:insertDefinition name="optionsTransfer" /></td>
													</s:push>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								</div>	
							</td>
						</tr>
			<%-- advanced panel --%>	
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
															<td width="20px"><s:checkbox
																	name="dataSource.enabledwmm"
																	value="%{dataSource.enabledwmm}" onclick="enableWmmAdmctlDisplayStyle(this.checked)" /></td>
																<td width="180px"><s:text name="config.ssid.enabledwmm" /></td>
															<td><s:checkbox
																	name="dataSource.broadcase" value="%{dataSource.broadcase}" /></td>
																<td><s:text name="config.ssid.ignore_broadcast_probe" /></td>
														</tr>
														<tr>
															<td><s:checkbox
																	name="dataSource.hide" value="%{dataSource.hide}" /></td>
																<td><s:text name="config.ssid.hideSsid" /></td>	
															<td><s:checkbox
																	name="dataSource.enabledUnscheduled"
																	disabled="%{!dataSource.enabledwmm}"
																	value="%{dataSource.enabledUnscheduled}" /></td>
																<td><s:text name="config.ssid.enabledUnscheduled" /></td>
														</tr>
														<tr>
															<td><s:checkbox
																	name="dataSource.enabledLegacy" value="%{dataSource.enabledLegacy}" /></td>
																<td colspan="3"><s:text name="config.ssid.legacy" /></td>	
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
		<s:if test="%{easyMode && id != null && lastExConfigGuide != null && ssidCount > 1}">
		<tr>
			<td align="center">
				<table>
					<tr>
						<td align="center" style="padding-bottom: 20px;">
							<input type="button" name="removessid" value="Remove SSID" style="width: 100px;"
							class="button" onClick="removeSSID();" <s:property value="writeDisabled" />>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</s:if>
	</table>
</s:form></div>
<div id="localUserPanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="localUserFrame" name="localUserFrame" width="0" height="0" frameborder="0" src="">
		</iframe>
	</div>
</div>
<s:if test="%{allowedTrial}">
<tiles:insertDefinition name="idmTrialSection" />
</s:if>
<script>

var localUserPanel = null;

function createLocalUserPanel(width, height, title){
	var div = document.getElementById("localUserPanel");
	var iframe = document.getElementById("localUserFrame");
	iframe.width = width;
	iframe.height = height;
	localUserPanel = new YAHOO.widget.Panel(div, { 	width:(width+10)+"px", 
														fixedcenter:false, 
														visible:false, 
														draggable: false,
														zindex:999,
														modal:false,
														constraintoviewport:true } );
														
	localUserPanel.setHeader(title);
	localUserPanel.render();
	div.style.display="";
	localUserPanel.beforeHideEvent.subscribe(closeLocalUserPanelIE);
}

var resultDoNothing = function(o) {
}

function closeLocalUserPanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("localUserFrame").style.display = "none";
	}
	var url = '<s:url action="localUser" includeParams="none"/>' + "?operation=removeAllSessionAttr"+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: resultDoNothing, failure:resultDoNothing,timeout: 3000000}, null);
	
}

function openLocalUserPanel(title, doOperation){
	var pId='<s:property value="%{id}"/>';

	if (pId==null || pId=='') {
		warnDialog.cfg.setProperty('text', "The SSID configuration has not yet been saved. You must first save the configuration before proceeding.");
		warnDialog.show();
		return;
	}
	if(null == localUserPanel){
		createLocalUserPanel(780,500,title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("localUserFrame").style.display = "";
	}
	localUserPanel.center();
	localUserPanel.show();
	//document.forms[formName].target="localUserFrame";
	var iframe = document.getElementById("localUserFrame");
	//document.forms[formName].target="_self";
	iframe.src ="<s:url value='localUser.action' includeParams='none' />?operation="+doOperation + "&ignore=" + new Date().getTime();
	//document.forms[formName].target="_self";
}
</script>
