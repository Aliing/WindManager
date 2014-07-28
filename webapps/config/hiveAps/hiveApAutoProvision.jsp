<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.hiveap.HiveApAutoProvision"%>
<%@page import="com.ah.be.parameter.constant.util.AhWebUtil"%>
<%@page import="com.ah.util.devices.impl.Device"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script src="<s:url value="/js/hm.options.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0;
}
-->
</style>
<script type="text/javascript">
var formName = 'autoProvisioningConfig';
var MODEL_VPN_GATEWAY = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA%>;
var MODEL_CVG_APPLIANCE = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY%>;
var MODEL_HIVEAP_SR24 = <%=HiveAp.HIVEAP_MODEL_SR24%>;
var MODEL_HIVEAP_SR2024P = <%=HiveAp.HIVEAP_MODEL_SR2024P%>;
var MODEL_HIVEAP_SR2124P = <%=HiveAp.HIVEAP_MODEL_SR2124P%>;
var MODEL_HIVEAP_SR2148P = <%=HiveAp.HIVEAP_MODEL_SR2148P%>;
var DEVICE_TYPE_HIVEAP = <%=HiveAp.Device_TYPE_HIVEAP%>;
var DEVICE_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;
var DEVICE_TYPE_VPN_GATEWAY = <%=HiveAp.Device_TYPE_VPN_GATEWAY%>;
var DEVICE_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var BIND_INTERFACE_NONE = <%=AhInterface.ETH_BIND_IF_NULL%>;
var BIND_INTERFACE_AGG0 = <%=AhInterface.ETH_BIND_IF_AGG0%>;
var BIND_INTERFACE_RED0 = <%=AhInterface.ETH_BIND_IF_RED0%>;
var IF_ADMIN_STATUS_UP = <%=AhInterface.ADMIN_STATE_UP%>;
var IF_ADMIN_STATUS_DOWN = <%=AhInterface.ADMIN_STATE_DOWM%>;
var OPERATION_MODE_BACKHAUL = <%=AhInterface.OPERATION_MODE_BACKHAUL%>;
var OPERATION_MODE_ACCESS = <%=AhInterface.OPERATION_MODE_ACCESS%>;
var OPERATION_MODE_DUAL = <%=AhInterface.OPERATION_MODE_DUAL%>;
var OPERATION_MODE_SENSOR = <%=AhInterface.OPERATION_MODE_SENSOR%>;
var ETH_SPEED_AUTO = <%=AhInterface.ETH_SPEED_AUTO%>;
var ETH_SPEED_1000M = <%=AhInterface.ETH_SPEED_1000M%>;
var ETH_DUPLEX_AUTO = <%=AhInterface.ETH_DUPLEX_AUTO%>;
var ETH_DUPLEX_HALF = <%=AhInterface.ETH_DUPLEX_HALF%>;
var ETH0_DEVICE_DOWNSTREAM_BANDWIDTH = "<%=AhInterface.ETH0_DEVICE_DOWNSTREAM_BANDWIDTH%>";
var USB_DEVICE_DOWNSTREAM_BANDWIDTH = "<%=AhInterface.USB_DEVICE_DOWNSTREAM_BANDWIDTH%>";
var SENSOROPTIONVALUE = "<%=AhInterface.OPERATION_MODE_SENSOR%>";
var ETH_PSE_8023af = <%=AhInterface.ETH_PSE_8023af%>;
var ETH_PSE_8023af_EXTENDED = <%=AhInterface.ETH_PSE_8023af_EXTENDED%>;
var ETH_PSE_8023at = <%=AhInterface.ETH_PSE_8023at%>;

var hiveApModelOptsAp = new Array();
var hiveApModelOptsBranchRouter = new Array();
var hiveApModelOptsVpnGateway = new Array();

<%=AhWebUtil.getAllDevicesJs()%>
<%=AhWebUtil.getBoolArray(Device.IS_11n)%>
<%=AhWebUtil.getBoolArray(Device.ALL, Device.ETH_PORTS)%>

<s:iterator value = "%{apModelAp}">
	hiveApModelOptsAp[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>
<s:iterator value = "%{apModelBranchRouter}">
	hiveApModelOptsBranchRouter[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>
<s:iterator value = "%{apModelVpnGateway}">
	hiveApModelOptsVpnGateway[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>


function is11nHiveAP(){
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	if(IS_11n.contains(selectApModel)) {
		return true;
	}
	return false;
}

function isBR100PlatformDevice(){
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	if (selectApModel == BR100) {
		return true;
	}else{
		return false;
	}	
}

function isBR100Like() {
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	var selectDevcieType = document.getElementById(formName + "_dataSource_deviceType").value;
	if (selectApModel == BR100 || selectApModel == BR200) {
		return true;
	} else if (selectApModel == AP330 || selectApModel == AP350 || 
			   selectApModel == BR200_WP || selectApModel == BR200_LTE_VZ) {
		if (selectDevcieType == DEVICE_TYPE_BRANCH_ROUTER ) {
			return true;
		}
	}
	return false;
}

function isBR100AsAP() {
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	var selectDevcieType = document.getElementById(formName + "_dataSource_deviceType").value;
	if (selectApModel == BR100 && selectDevcieType == DEVICE_TYPE_HIVEAP) {
		return true;
	}
	return false;
}

function isBRWith4LanPorts() {
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	if (selectApModel == BR100 || selectApModel == BR200 || selectApModel == BR200_WP || selectApModel == BR200_LTE_VZ) {
		return true;
	}
	return false;
}

function isEth1Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	var eths = ALL_ETH_PORTS.fetch(selectApModel);
	if (eths == null) return false;

	for(i = 0; i < eths[1].length; i++) {
		if (eths[1][i] == 'eth1'.toLowerCase()) return true;
	}
	return false;
}

function isWifi0Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	return selectApModel != BR200;
}

function isWifi1Available(){
	var selectApModel = document.getElementById(formName + "_dataSource_modelType").value;
	return selectApModel != AP110 && selectApModel != BR100 && selectApModel != BR200 && selectApModel != BR200_WP && selectApModel != BR200_LTE_VZ;
}

function onLoadPage(){
	var vle = document.getElementById(formName+"_dataSource_uploadImage");
	imageInstallChanged(vle.checked);
	//updateInterfaceLayout2();
	<s:if test="%{blnNoChangeDeviceType == false}">
		apModelChanged();
	</s:if>
	<s:else>
		judgeDeviceTypeOfModelType();
		updateInterfaceLayout2();
	</s:else>
	fetchImageVersionInfo();
}

function imageInstallChanged(cb_value){
	//alert(cb_value);
	var version_el = document.getElementById(formName+"_dataSource_imageVersion");
	displaySelectedImageName(version_el.value);
	var selectedImage = document.getElementById("imageNameSpan");
	var imageImport = document.getElementById("importImage");
	version_el.disabled = !cb_value;
	if(imageImport){
		imageImport.disabled = !cb_value;
	}
	<s:if test="%{dsEnable}">
	selectedImage.style.display = "none";
	</s:if>
	<s:else>
	selectedImage.style.display = cb_value? "" : "none";
	</s:else>
	var reboot_el = document.getElementById(formName+"_dataSource_rebooting");
	var uploadScript_el = document.getElementById(formName+"_dataSource_uploadConfig");
	if(cb_value){
		reboot_el.disabled = false;
	}else{
		reboot_el.disabled = !uploadScript_el.checked;
	}
}

function scriptChanged(cb_value) {
	var reboot_el = document.getElementById(formName+"_dataSource_rebooting");
	var uploadImage_el = document.getElementById(formName+"_dataSource_uploadImage");
	if(cb_value){
		reboot_el.disabled = false;
	}else{
		reboot_el.disabled = !uploadImage_el.checked;
	}
}

function accessControlCheck(checkBox){
	var el = document.getElementById('aclSection');
	if(checkBox.checked){
		el.style.display = '';
	}else{
		el.style.display = 'none';
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

function judgeDeviceTypeOfModelType() {
	var modelType = document.getElementById(formName + "_dataSource_modelType");
	var model = modelType.value;
	var deviceType = document.getElementById(formName + "_dataSource_deviceType");
	var deviceTypeValue = deviceType.value;
	deviceType.length = 0;

	if(model == AP330 || model == AP350 || model == BR100) {
		if(model == BR100){
			<s:if test="%{dataSource.id == null}">
			deviceTypeValue = 1;
			</s:if>
		}
		$("#"+formName+"_dataSource_deviceType option").remove();
		$("#"+formName+"_dataSource_deviceType")
			.append("<option value="+DEVICE_TYPE_HIVEAP+">"+'<s:property value="hiveApName"/>'+"</option>")
			.append("<option value="+DEVICE_TYPE_BRANCH_ROUTER+">"+'<s:property value="bRName"/>'+"</option>");
		$("#"+formName+"_dataSource_deviceType option[value="+deviceTypeValue+"]").attr("selected", true);
	} else if (model == BR200 || model == BR200_WP || model == BR200_LTE_VZ){
		hm.simpleObject.addOption(deviceType, DEVICE_TYPE_BRANCH_ROUTER, '<s:property value="bRName"/>',
				DEVICE_TYPE_BRANCH_ROUTER == deviceTypeValue);
	} else if (model == MODEL_VPN_GATEWAY || model == MODEL_CVG_APPLIANCE){
		hm.simpleObject.addOption(deviceType, DEVICE_TYPE_VPN_GATEWAY, '<s:property value="cVGName"/>',
				DEVICE_TYPE_VPN_GATEWAY == deviceTypeValue);
	} else if (isSwitchWithBRMode(model)) {
		$("#"+formName+"_dataSource_deviceType option").remove();
		$("#"+formName+"_dataSource_deviceType")
			.append("<option value="+DEVICE_TYPE_SWITCH+">"+'<s:property value="switchName"/>'+"</option>")
			.append("<option value="+DEVICE_TYPE_BRANCH_ROUTER+">"+'<s:property value="bRName"/>'+"</option>");
		$("#"+formName+"_dataSource_deviceType option[value="+deviceTypeValue+"]").attr("selected", true);
	} else if (isSwitchWithoutBRMode(model)) {
		$("#"+formName+"_dataSource_deviceType option").remove();
		$("#"+formName+"_dataSource_deviceType")
			.append("<option value="+DEVICE_TYPE_SWITCH+">"+'<s:property value="switchName"/>'+"</option>")
		$("#"+formName+"_dataSource_deviceType option[value="+deviceTypeValue+"]").attr("selected", true);
	} else {
		hm.simpleObject.addOption(deviceType, DEVICE_TYPE_HIVEAP, '<s:property value="hiveApName"/>',
				DEVICE_TYPE_HIVEAP == deviceTypeValue);
	}

	changeTransTypeAndSpeed(model);
	operateSensorOption(model);
}

function getPseWithModel(){
	var modelType = document.getElementById(formName + "_dataSource_modelType");
	var model = modelType.value;
	if (model == BR200 || model == BR200_WP){
		$("#eth1PseState option").remove();
		$("#eth1PseState")
			.append("<option value="+ETH_PSE_8023af+">"+'<s:property value="eth_PSE_8023af"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023af_EXTENDED+">"+'<s:property value="eth_PSE_8023af_EXTENDED"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023at+">"+'<s:property value="eth_PSE_8023at"/>'+"</option>");
		
		$("#eth2PseState option").remove();
		$("#eth2PseState")
			.append("<option value="+ETH_PSE_8023af+">"+'<s:property value="eth_PSE_8023af"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023af_EXTENDED+">"+'<s:property value="eth_PSE_8023af_EXTENDED"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023at+">"+'<s:property value="eth_PSE_8023at"/>'+"</option>");
	}else if(model == BR200_LTE_VZ){		
		$("#eth1PseState option").remove();
		$("#eth1PseState")
			.append("<option value="+ETH_PSE_8023af+">"+'<s:property value="eth_PSE_8023af"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023af_EXTENDED+">"+'<s:property value="eth_PSE_8023af_EXTENDED"/>'+"</option>");
		
		$("#eth2PseState option").remove();
		$("#eth2PseState")
			.append("<option value="+ETH_PSE_8023af+">"+'<s:property value="eth_PSE_8023af"/>'+"</option>")
			.append("<option value="+ETH_PSE_8023af_EXTENDED+">"+'<s:property value="eth_PSE_8023af_EXTENDED"/>'+"</option>");
	} 
}

function changeBRUsbModeName(){
	var model = document.getElementById(formName + "_dataSource_modelType").value; 
	var deviceTypeValue = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceTypeValue == DEVICE_TYPE_BRANCH_ROUTER){
		if(model == BR200_LTE_VZ){
			document.getElementById("usbModeName").innerHTML="<s:text name='hiveAp.autoProvisioning.device.if.port.cellularmodem'/>";
		}else{
			document.getElementById("usbModeName").innerHTML="<s:text name='hiveAp.autoProvisioning.br100.if.port.usb'/>";
		} 
	}
}

var wifi0Option;
var wifi1Option;
var wifi0index;
var index;
var addOption;
function operateSensorOption(model){
	var wifi0ModelList = document.getElementById(formName+"_dataSource_wifi0_operationMode");
	var wifi1ModelList = document.getElementById(formName+"_dataSource_wifi1_operationMode");
	for(var i=0;i<wifi0ModelList.length;i++){
		if(wifi0ModelList.options[i].value==SENSOROPTIONVALUE){
		  wifi0Option=new Option(wifi0ModelList.options[i].text,wifi0ModelList.options[i].value);
		  wifi1Option=new Option(wifi1ModelList.options[i].text,wifi1ModelList.options[i].value);
		  index=i;
		}
	}
	if(null==wifi0Option){
		return;
	}
	if(model==AP110 || model==AP20 ||
			model==AP28 || model==AP370 || model==AP390){
		if(!addOption){
		  wifi0ModelList.options.remove(index);
		  wifi1ModelList.options.remove(index);
		  addOption=true;
		}
	}else{
		if(addOption){
			wifi0ModelList.options.add(wifi0Option);
			wifi1ModelList.options.add(wifi1Option);
			addOption=false;
		}
	}
}

function changeTransTypeAndSpeed(model) {
	// disable transmission type and speed setting when model is br
	if (model == AP330 || model == AP350) {
		document.getElementById(formName + '_br100Eth0_interfaceSpeed').disabled = false;
		document.getElementById(formName + '_br100Eth1_interfaceSpeed').disabled = false;
		document.getElementById(formName + '_br100Eth0_interfaceTransmissionType').disabled = false;
		document.getElementById(formName + '_br100Eth1_interfaceTransmissionType').disabled = false;
	} else {
		document.getElementById(formName + '_br100Eth0_interfaceSpeed').disabled = true;
		document.getElementById(formName + '_br100Eth0_interfaceSpeed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_br100Eth0_interfaceTransmissionType').disabled = true;
		document.getElementById(formName + '_br100Eth0_interfaceTransmissionType').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_br100Eth1_interfaceSpeed').disabled = true;
		document.getElementById(formName + '_br100Eth1_interfaceSpeed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_br100Eth1_interfaceTransmissionType').disabled = true;
		document.getElementById(formName + '_br100Eth1_interfaceTransmissionType').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_br100Eth2_interfaceSpeed').disabled = true;
		document.getElementById(formName + '_br100Eth2_interfaceSpeed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_br100Eth2_interfaceTransmissionType').disabled = true;
		document.getElementById(formName + '_br100Eth2_interfaceTransmissionType').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_br100Eth3_interfaceSpeed').disabled = true;
		document.getElementById(formName + '_br100Eth3_interfaceSpeed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_br100Eth3_interfaceTransmissionType').disabled = true;
		document.getElementById(formName + '_br100Eth3_interfaceTransmissionType').value = ETH_DUPLEX_AUTO;
		document.getElementById(formName + '_br100Eth4_interfaceSpeed').disabled = true;
		document.getElementById(formName + '_br100Eth4_interfaceSpeed').value = ETH_SPEED_AUTO;
		document.getElementById(formName + '_br100Eth4_interfaceTransmissionType').disabled = true;
		document.getElementById(formName + '_br100Eth4_interfaceTransmissionType').value = ETH_DUPLEX_AUTO;
	}
}

function apModelChanged(){
	judgeDeviceTypeOfModelType();
	apDeviceChanged(document.getElementById(formName + "_dataSource_deviceType").value);
}

//only changed layout of interface setting
function apDeviceChanged(value) {
	updateOneTimePassword(value);
	updateInterfaceLayout2();
	updateRadioProfiles();
	if (shouldLimitHiveOSImageVersion(value)) {
		var el = Get(formName + "_dataSource_uploadImage");
		if (el) {
			if (el.checked === false) {
				el.checked = true;
				imageInstallChanged(true);
				showHideContent("advancedSettings", "");
			}
		}
	}
}

function shouldLimitHiveOSImageVersion(deviceType, deviceModel) {
	deviceType = deviceType || document.getElementById(formName + "_dataSource_deviceType").value;
	deviceModel = deviceModel || document.getElementById(formName + "_dataSource_modelType").value;
	if (deviceType == DEVICE_TYPE_BRANCH_ROUTER
			&& !isModelTypeSwitch(deviceModel)) {
		return true;
	}
}

//it will decide BR100 like interface setting or another to show
function updateInterfaceLayout2() {
	var isRouter = isBR100Like();
	if (isRouter) {
		hm.util.show('interfaceSetting2');
		hm.util.hide('interfaceSetting1');
		updateInterfaceLayoutBR100like();
		disableEth4Br();
	} else {
		hm.util.show('interfaceSetting1');
		hm.util.hide('interfaceSetting2');
		updateInterfaceLayout();
	}
	
	updateUsbModemSettingSection(isRouter);
	updateLocationTopologyInfo();

	var apModel = document.getElementById(formName + "_dataSource_modelType").value;
	hideShowPseSettings(apModel == BR200_WP || apModel == BR200_LTE_VZ);
	
	if (isModelTypeSwitch(apModel)) {
		hm.util.hide('interfaceSettingsController');
	} else {
		hm.util.show('interfaceSettingsController');
	}
}

function isModelTypeSwitch(apModel) {
	if (apModel == SR24 || apModel == SR2124P || apModel == SR2024P || apModel == SR2148P) {
		return true;
	}
	return false;
}

function isSwitchWithoutBRMode(apModel){
	if (apModel == SR2148P || apModel == SR2124P) {
		return true;
	}
	return false;
}

function isSwitchWithBRMode(apModel){
	if (apModel == SR24 || apModel == SR2024P) {
		return true;
	}
	return false;
}

function updateUsbModemSettingSection(isRouter) {
	if (isRouter) {
		if (isBR100AsAP()) {
			Get("usbModemSettingSection").style.display = "none";
			if (Get("interfacePortOfUsb")) {
				Get("interfacePortOfUsb").style.display = "none";
			}
			toggleEth0RoleConfig(false);
		} else {
			Get("usbModemSettingSection").style.display = "";
			if (Get("interfacePortOfUsb")) {
				Get("interfacePortOfUsb").style.display = "";
			}
			if (Get(formName + "_br100Usb_interfaceRole")) {
				branchRouterUSBChanged(Get(formName + "_br100Usb_interfaceRole").value);
			}
			toggleEth0RoleConfig(true);
		}
		
	} else {
		Get("usbModemSettingSection").style.display = "none";
	}
}

function updateLocationTopologyInfo(){
	var includeTopologyInfoTr = document.getElementById("includeTopologyInfoTr");
	includeTopologyInfoTr.style.display = isBR100PlatformDevice() ? "none" : "";
}

function toggleEth0RoleConfig(canConfig) {
	if(Get("br_eth0_setting_role_backhaul")
			&& Get("br_eth0_setting_role")) {
		if (canConfig === true) {
			Get("br_eth0_setting_role_backhaul").style.display = "none";
			Get("br_eth0_setting_role").style.display = "";
		} else {
			Get("br_eth0_setting_role_backhaul").style.display = "";
			Get("br_eth0_setting_role").style.display = "none";
		}
	}
}

function hideShowCol(colIdx, isShow) {
	var ethSetting = document.getElementById('interfaceEthSettingTable');
	var table = ethSetting.children[0];
	var rowsLen = table.rows.length;
	for (var i=0; i<rowsLen; i++)
	{
		var tr = table.rows[i];
		if (tr.children[colIdx] && tr.children[colIdx] != undefined) {
			tr.children[colIdx].style.display = isShow ? "" : "none";
		}
	}
}

//for BR100 like device
function updateInterfaceLayoutBR100like() {
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	hm.util.show('wifiConfigTitle');
	if(isWifi0Available()){
		hm.util.show('wifi0Row');
	}else{
		hm.util.hide('wifi0Row');
		hm.util.hide('wifiConfigTitle');
	}
	if(isWifi1Available()){
		hm.util.show('wifi1Row');
	}else{
		hm.util.hide('wifi1Row');
	}
	if (isBRWith4LanPorts()) {
		hm.util.show('brWith4LanPortEth2Id');
		hm.util.show('brWith4LanPortEth3Id');
		hm.util.show('brWith4LanPortEth4Id');
	} else {
		hm.util.hide('brWith4LanPortEth2Id');
		hm.util.hide('brWith4LanPortEth3Id');
		hm.util.hide('brWith4LanPortEth4Id');
	}
}

//temporary used
function disableEth4Br() {
	if (isBRWith4LanPorts()) {
	//	Get(formName + "_br100Eth1_interfaceTransmissionType").disabled = true;
	//	Get(formName + "_br100Eth1_interfaceSpeed").disabled = true;
	}
}

function updateInterfaceLayout(){
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	if(isEth1Available()){
		// show eth1/red0/agg0
		hm.util.show('eth1Row');
		hm.util.show('red0Row');
		hm.util.show('agg0Row');
		// show column of bind interface and role
		hm.util.show('eth0BindCell');
		hm.util.show('eth0BindHeader');
		hm.util.show('eth0RoleCell');
		hm.util.show('eth0RoleHeader');

		bindInterfaceChanged();
		ethOperationModeChanged();
	}else{
		hm.util.hide('eth1Row');
		hm.util.hide('red0Row');
		hm.util.hide('agg0Row');
		// hide column of bind interface and role
		hm.util.hide('eth0BindCell');
		hm.util.hide('eth0BindHeader');
		hm.util.hide('eth0RoleCell');
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

function bindInterfaceChanged(){
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
		hm.util.show('eth0RoleCell');
	}else{
		hm.util.hide('eth0RoleCell');
	}
	if(eth1BindElement.value == BIND_INTERFACE_RED0){
		hm.util.show('eth1RoleCell');
	}else{
		hm.util.hide('eth1RoleCell');
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

function changeSpeed(selectBox) {
	if(selectBox.value == ETH_SPEED_1000M && !is11nHiveAP()) {
		hm.util.reportFieldError(selectBox, '<s:text name="error.hiveAp.noSpeed" />');
		selectBox.value = 0;
	}
}

function fetchFailed(o) {
	//alert("failed.");
}

function fetchSuccess(o){
	eval("var details = " + o.responseText);
	if(details.wifi0){
		var wifi0 = document.getElementById(formName + "_dataSource_wifi0ProfileId");
		wifi0.length=0;
		wifi0.length=details.wifi0.length;
		for(var i = 0; i < details.wifi0.length; i ++) {
			wifi0.options[i].value = details.wifi0[i].id;
			wifi0.options[i].text = details.wifi0[i].v;
		}
		if(details.wifi0d){
			wifi0.value = details.wifi0d;
		}
		if(details.wifi0dl){
			var wifi0ModeEl = document.getElementById("wifi0RadioMode");
			wifi0ModeEl.replaceChild(document.createTextNode(details.wifi0dl), wifi0ModeEl.childNodes[0]);
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
	}
	if(details.wifi1){
		var wifi1 = document.getElementById(formName + "_dataSource_wifi1ProfileId");
		wifi1.length=0;
		wifi1.length=details.wifi1.length;
		for(var i = 0; i < details.wifi1.length; i ++) {
			wifi1.options[i].value = details.wifi1[i].id;
			wifi1.options[i].text = details.wifi1[i].v;
		}
		if(details.wifi1d){
			wifi1.value = details.wifi1d;
		}
		if(details.wifi1dl){
			var wifi1ModeEl = document.getElementById("wifi1RadioMode");
			wifi1ModeEl.replaceChild(document.createTextNode(details.wifi1dl), wifi1ModeEl.childNodes[0]);
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
	}

	if(details.versionList){
		var versionList = document.getElementById(formName + "_dataSource_imageVersion");
		var versionNum = versionList.value;
		versionList.length=0;
		versionList.length=details.versionList.length;
		for(var i = 0; i < details.versionList.length; i ++) {
			versionList.options[i].value = details.versionList[i].key;
			versionList.options[i].text = details.versionList[i].value;
			versionList.options[i].selected = details.versionList[i].key == versionNum;
		}
		// bind data onto select element
		$("#" + formName + "_dataSource_imageVersion").data("versions", details.versionList);
		displaySelectedImageName(versionList.value);
	}
}

var callback = {
	success : fetchSuccess,
	failure : fetchFailed
};

function fetchImageVersionInfo(){
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	var url = '<s:url action="autoProvisioningConfig" includeParams="none"></s:url>' + "?operation=fetchImageVersionInfos&apModelType="+modelType
	+"&ignore="+new Date().getTime();
	ajaxRequest(null, url, function(o){
		eval("var details = " + o.responseText);
		$("#" + formName + "_dataSource_imageVersion").data("versions", details.versionList);
	});
}

function displaySelectedImageName(version){
	var versions = $("#" + formName + "_dataSource_imageVersion").data("versions");
	if(versions){
		var img = "";
		for(var i=0; i<versions.length; i++){
			if(versions[i].key == version){
				img = versions[i].img;
				break;
			}
		}
		document.getElementById("imageNameSpan").innerHTML = img;
	}
}

function updateRadioProfiles(){
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	var countryCode = document.getElementById(formName + "_dataSource_countryCode").value;
	var wifi0OperationMode = document.getElementById(formName+"_dataSource_wifi0_operationMode").value;
	var wifi1OperationMode = document.getElementById(formName+"_dataSource_wifi1_operationMode").value;
	var url = '<s:url action="autoProvisioningConfig" includeParams="none"></s:url>' + "?operation=fetchRadioProfiles&apModelType="+modelType
			+"&countryCode="+countryCode+"&wifi0OperationMode="+wifi0OperationMode+"&wifi1OperationMode="+wifi1OperationMode
			+"&apDeviceType="+deviceType+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
}

function addWifi0RadioProfile(cb){
	var operationMode = document.getElementById(formName+"_dataSource_wifi0_operationMode").value;
	if (cb.value != -2 ) {
		getRadioMode(cb.value, "wifi0", operationMode);
		return;
	}
	// create a new radio profile
	if(is11nHiveAP()){
		document.forms[formName].radioType.value = "ng";//default radio type
	}else{
		document.forms[formName].radioType.value = "bg";//default radio type
	}
	submitAction('newWifi0RadioProfile');
}

function addWifi1RadioProfile(cb){
	var operationMode = document.getElementById(formName+"_dataSource_wifi1_operationMode").value;
	if (cb.value != -2 ) {
		getRadioMode(cb.value, "wifi1", operationMode);
		return;
	}
	// create a new radio profile
	if(is11nHiveAP()){
		document.forms[formName].radioType.value = "na";//default radio type
	}else{
		document.forms[formName].radioType.value = "a";//default radio type
	}
	submitAction('newWifi1RadioProfile');
}

function wifiOperationModeChanged(operationMode, wifi){
	var profileId;
	if(wifi == 'wifi1'){
		profileId = document.getElementById(formName + "_dataSource_wifi1ProfileId").value;
	}else{
		profileId = document.getElementById(formName + "_dataSource_wifi0ProfileId").value;
	}
	getRadioMode(profileId, wifi, operationMode);
}

function getRadioMode(profileId, wifi, operationMode){
	var countryCode = document.getElementById(formName + "_dataSource_countryCode").value;
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	var url = '<s:url action="autoProvisioningConfig" includeParams="none"></s:url>' + "?operation=getRadioMode&id="+profileId+"&countryCode="+countryCode+"&operationMode="+operationMode+"&apModelType="+modelType+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processRadioMode, failure : null, argument: wifi}, null);
}

function processRadioMode(o){
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
			channel1El.length=details.c.length;
			for(var i = 0; i < details.c.length; i ++) {
				channel1El.options[i].value = details.c[i].key;
				channel1El.options[i].text = details.c[i].value;
				channel1El.options[i].selected = details.c[i].key==channel1;
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
			channel0El.length=details.c.length;
			for(var i = 0; i < details.c.length; i ++) {
				channel0El.options[i].value = details.c[i].key;
				channel0El.options[i].text = details.c[i].value;
				channel0El.options[i].selected = details.c[i].key==channel0;
			}
		}
	}
}

function requestChannels(){
	var countryCode = document.getElementById(formName + "_dataSource_countryCode").value;
	//country code change for Japan.
	countryCodeChange(document.getElementById(formName + "_dataSource_countryCode"));
	var modelType = document.getElementById(formName + "_dataSource_modelType").value;
	var wifi0ProfileId = document.getElementById(formName + "_dataSource_wifi0ProfileId").value;
	var wifi1ProfileId = document.getElementById(formName + "_dataSource_wifi1ProfileId").value;
	var wifi0OperationMode = document.getElementById(formName+"_dataSource_wifi0_operationMode").value;
	var wifi1OperationMode = document.getElementById(formName+"_dataSource_wifi1_operationMode").value;
	var url = '<s:url action="autoProvisioningConfig" includeParams="none"></s:url>' + "?operation=requestChannels&wifi0ProfileId="+wifi0ProfileId+"&wifi1ProfileId="+wifi1ProfileId+"&countryCode="+countryCode+"&wifi0OperationMode="+wifi0OperationMode+"&wifi1OperationMode="+wifi1OperationMode+"&apModelType="+modelType+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processChannels, failure : null}, null);
}

function processChannels(o){
	eval("var details = " + o.responseText);
	if(details.wifi0Channel){
		var channel0El = document.getElementById(formName + "_dataSource_wifi0_channel");
		var channel0 = channel0El.value;
		channel0El.length=0;
		channel0El.length=details.wifi0Channel.length;
		for(var i = 0; i < details.wifi0Channel.length; i ++) {
			channel0El.options[i].value = details.wifi0Channel[i].key;
			channel0El.options[i].text = details.wifi0Channel[i].value;
			channel0El.options[i].selected = details.wifi0Channel[i].key==channel0;
		}
	}
	if(details.wifi1Channel){
		var channel1El = document.getElementById(formName + "_dataSource_wifi1_channel");
		var channel1 = channel1El.value;
		channel1El.length=0;
		channel1El.length=details.wifi1Channel.length;
		for(var i = 0; i < details.wifi1Channel.length; i ++) {
			channel1El.options[i].value = details.wifi1Channel[i].key;
			channel1El.options[i].text = details.wifi1Channel[i].value;
			channel1El.options[i].selected = details.wifi1Channel[i].key==channel1;
		}
	}
}

function submitAction(operation) {
	thisOperation = operation;
	if (validate(operation)) {
		doContinueOper();
	}
}

function validate(operation){
	if (operation == 'create' || operation == 'create<s:property value="lstForward"/>') {
		if (!validateName())
			return false;
	}
	if(!validateInterfaceModes(operation)){
		return false;
	}
	if(!validateAllowedVlan(operation)){
		return false;
	}
	if(!validateSpeedAndDuplex(operation)){
		return false;
	}
	if(!validateConfigSection(operation)){
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
	<s:if test="%{!dsEnable}">
	if(!validateImageSection(operation)){
		return false;
	}
	</s:if>
	/*if(operation == "editTemplate"){
		var value = hm.util.validateListSelection(formName + "_dataSource_configTemplateId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].configTemplateId.value = value;
		}
	}*/
	if(operation == "editCapwapIp"){
		var value = hm.util.validateListSelection("cfgCapwapIpId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].capwapIpId.value = value;
		}
	}
	if(operation == "editCapwapBackupIp"){
		var value = hm.util.validateListSelection("cfgCapwapBackupIpId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].capwapBackupIpId.value = value;
		}
	}
	if(!validateCapwapIp(operation)){
		return false;
	}
	return true;
}

function validateName() {
    var inputElement = document.getElementById("autoProvisioningNameId");
    var message = hm.util.validateName(inputElement.value, '<s:text name="hiveAp.autoProvisioning.apProperties.name" />');
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function validateImageSection(operation){
	if (operation == 'create' || operation == 'update'){
		var imageFlag = document.getElementById(formName+"_dataSource_uploadImage");
		var imageHook = document.getElementById(formName+"_dataSource_imageVersion");
		if(imageFlag.checked){
			var selectedImage = document.getElementById("imageNameSpan");
			var selectedValue = selectedImage.innerHTML;
			if(!selectedValue.match('.img')){
				hm.util.reportFieldError(imageHook, '<s:text name="error.hiveap.image.image.version.notfound"></s:text>');
				imageHook.focus();
				return false;
			}
		}
	}
	return true;
}

function validateConfigSection(operation){
	if (operation == 'create' || operation == 'update'){
		var configFlag = document.getElementById(formName+"_dataSource_uploadConfig");
		if(configFlag.checked){
			var selectedTemplate = document.getElementById(formName+"_dataSource_configTemplateId");
			var selected0Profile = document.getElementById(formName+"_dataSource_wifi0ProfileId");
			var selected1Profile = document.getElementById(formName+"_dataSource_wifi1ProfileId");
			if(selectedTemplate.value <= 0){
			    hm.util.reportFieldError(selectedTemplate, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.template" /></s:param></s:text>');
			    selectedTemplate.focus();
			    return false;
			}
			if(selected0Profile.value <= 0){
			    hm.util.reportFieldError(selected0Profile, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
			    selected0Profile.focus();
			    return false;
			}
			if(selected1Profile.value <= 0){
			    hm.util.reportFieldError(selected1Profile, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.radioProfile" /></s:param></s:text>');
			    selected1Profile.focus();
			    return false;
			}
		}
	}
	return true;
}

function validateInterfaceModes(operation){
	if (operation == 'create' || operation == 'update'){
		var wifi0OperationModeEl = document.getElementById(formName+"_dataSource_wifi0_operationMode");
		var wifi1OperationModeEl = document.getElementById(formName+"_dataSource_wifi1_operationMode");
		var eth0OperationModeEl = document.getElementById(formName+"_dataSource_eth0_operationMode");
		var allAccessMode = false;
		if(isWifi1Available()){
			allAccessMode = (wifi0OperationModeEl.value == OPERATION_MODE_ACCESS
								&& wifi1OperationModeEl.value == OPERATION_MODE_ACCESS);
		}else{
			allAccessMode = (wifi0OperationModeEl.value == OPERATION_MODE_ACCESS);
		}
		if(!allAccessMode){//Only need check when all mode is access
			<s:if test="%{easyMode}">
				//Not allow customized configuration
				if(isWifi1Available()){
					if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
						hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.express"></s:text>');
						return false;
					}
				} else {
					if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL && eth0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
						hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.all.express"></s:text>');
						return false;
					}
				}
			</s:if>
			return true;
		}
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
		<s:if test="%{easyMode}">
			//Not allow customized configuration
			if(isWifi1Available()){
				if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					hm.util.reportFieldError(wifi0OperationModeEl, '<s:text name="error.hiveAp.backhaul.express"></s:text>');
					return false;
				}
			} else {
				if(wifi0OperationModeEl.value == OPERATION_MODE_BACKHAUL && eth0OperationModeEl.value == OPERATION_MODE_BACKHAUL){
					hm.util.reportFieldError(eth0OperationModeEl, '<s:text name="error.hiveAp.backhaul.all.express"></s:text>');
					return false;
				}
			}
		</s:if>
	}
	return true;
}

function validateAllowedVlan(operation){
	if(operation == 'create' || operation == 'update'){
		var eth0AllowedVlanEl = document.getElementById(formName + "_dataSource_eth0_allowedVlan");
		var eth1AllowedVlanEl = document.getElementById(formName + "_dataSource_eth1_allowedVlan");
		var agg0AllowedVlanEl = document.getElementById(formName + "_dataSource_agg0_allowedVlan");
		var red0AllowedVlanEl = document.getElementById(formName + "_dataSource_red0_allowedVlan");

		if(!validateAllowVlanFormat(eth0AllowedVlanEl)){
			eth0AllowedVlanEl.focus();
			return false;
		}
		if(isEth1Available()){
			if(!validateAllowVlanFormat(eth1AllowedVlanEl)){
				eth1AllowedVlanEl.focus();
				return false;
			}
		}
		if(YAHOO.util.Dom.getStyle(document.getElementById("agg0Row"),'display') != 'none'){
			if(!validateAllowVlanFormat(agg0AllowedVlanEl)){
				agg0AllowedVlanEl.focus();
				return false;
			}
		}
		if(YAHOO.util.Dom.getStyle(document.getElementById("red0Row"),'display') != 'none'){
			if(!validateAllowVlanFormat(red0AllowedVlanEl)){
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

function validateSpeedAndDuplex(operation){
	if(operation == 'create' || operation == 'update'){
		var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
		if(deviceType == DEVICE_TYPE_VPN_GATEWAY){
			return true;
		}
		if(deviceType == DEVICE_TYPE_HIVEAP){
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_dataSource_eth0_duplex"),document.getElementById(formName + "_dataSource_eth0_speed"))) {
				hm.util.show('interfaceSettings');
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
					hm.util.show('interfaceSettings');
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
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Eth0_interfaceTransmissionType"),document.getElementById(formName + "_br100Eth0_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Eth1_interfaceTransmissionType"),document.getElementById(formName + "_br100Eth1_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Eth2_interfaceTransmissionType"),document.getElementById(formName + "_br100Eth2_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Eth3_interfaceTransmissionType"),document.getElementById(formName + "_br100Eth3_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Eth4_interfaceTransmissionType"),document.getElementById(formName + "_br100Eth4_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}
			if (!validateSpeedAndDuplexfunc(document.getElementById(formName + "_br100Usb_interfaceTransmissionType"),document.getElementById(formName + "_br100Usb_interfaceSpeed"))) {
				hm.util.show('interfaceSettings');
				return false;
			}

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

function validateOptionNewUsernameAndPassword(operation, name, password, confirm){
	if(operation == 'create' || operation == 'update'){
		if (name.value.length > 0 || password.value.length > 0 || confirm.value.length > 0){
			var message = hm.util.validateUsername(name.value, '<s:text name="hiveAp.newUser" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(name, message);
	        	name.focus();
	        	return false;
	    	}
	    	
			if(name.value.length < 3){
			    hm.util.reportFieldError(name, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.newUser" /></s:param><s:param><s:text name="hiveAp.currentUserRange" /></s:param></s:text>');
			    name.focus();
			    return false;
			}

			if (!hm.util.validateUserNewPasswordFormat(password, confirm, '<s:text name="hiveAp.newPassword" />',
	    			'<s:text name="hiveAp.newConfirmPassword" />', 8, '<s:text name="hiveAp.currentPasswordRange" />', name.value)) {
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
			    hm.util.reportFieldError(name, "<s:text name='error.value.internal.used'><s:param><s:text name='hiveAp.newUser' /></s:param></s:text>");
			    name.focus();
			    return false;
			 }
		}
	}
	return true;
}

function checkUserDupleWithReadOnlyUser(operation){
	if(operation == 'create' || operation == 'update'){
		var usernameElement = document.getElementById(formName + "_dataSource_cfgAdminUser");
		var readOnlyElement = document.getElementById(formName + "_dataSource_cfgReadOnlyUser");
		if(usernameElement.value.length > 0 && readOnlyElement.value.length > 0){
			if(readOnlyElement.value == usernameElement.value){
	            hm.util.reportFieldError(readOnlyElement, "<s:text name='error.hiveAp.adminName'></s:text>");
	            readOnlyElement.focus();
	            return false;
			}
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
	    		hm.util.reportFieldError(newDtlsElement, message);
	        	newDtlsElement.focus();
	        	return false;
	    	}
			message = hm.util.validatePassword(confirmDtlsElement.value, '<s:text name="hiveAp.dtls.confirmPassPhrase" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(confirmDtlsElement, message);
	        	confirmDtlsElement.focus();
	        	return false;
	    	}
			if (newDtlsElement.value.length < 16) {
			    hm.util.reportFieldError(newDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    newDtlsElement.focus();
			    return false;
			}
			if (confirmDtlsElement.value.length < 16) {
			    hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.keyValueRange"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.passPhraseRange" /></s:param></s:text>');
			    confirmDtlsElement.focus();
			    return false;
			}
			if (newDtlsElement.value != confirmDtlsElement.value) {
			 	hm.util.reportFieldError(confirmDtlsElement, '<s:text name="error.notEqual"><s:param><s:text name="hiveAp.dtls.confirmPassPhrase" /></s:param><s:param><s:text name="hiveAp.dtls.newPassPhrase" /></s:param></s:text>');
				newDtlsElement.focus();
				return false;
			}
		}
	}
	return true;
}

function removeSerialNumbers(){
	var selectEl = document.getElementById("leftOptions_serialNumbers");
	selectItems = new Array();
	for(var i=0; i<selectEl.options.length; i++){
		if(selectEl.options[i].selected == true){
			selectItems.push(selectEl.options[i].value);
		}
	}
	if(selectItems.length ==0 ){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.selectObject" />");
			warnDialog.show();
		}
	}else if(selectItems.length==1 && selectItems[0]<0){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.emptyList" />");
			warnDialog.show();
		}
	}else{
		thisOperation = "removeSn";
		hm.util.confirmRemoveItems();
	}
}

function removeIpSubNetworks(){
	var selectEl = document.getElementById("leftOptions_ipSubNetworks");
	selectItems = new Array();
	for(var i=0; i<selectEl.options.length; i++){
		if(selectEl.options[i].selected == true){
			selectItems.push(selectEl.options[i].value);
		}
	}
	if(selectItems.length ==0 ){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.selectObject" />");
			warnDialog.show();
		}
	}else if(selectItems.length==1 && selectItems[0]<0){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.emptyList" />");
			warnDialog.show();
		}
	}else{
		thisOperation = "removeIpSubNetworks";
		hm.util.confirmRemoveItems();
	}
}

function doContinueOper() {
	showProcessing();
	saveOptionUnfoldStatus();
	if(thisOperation == 'removeSn'){
		document.forms[formName].snString.value = selectItems;
	} else if (thisOperation == 'removeIpSubNetworks') {
		document.forms[formName].ipSubNetworkString.value = selectItems;
	}
	document.forms[formName].operation.value = thisOperation;
	//add handler to deal with something before form submit.
	beforeSubmitAction(document.forms[formName]);
	enableBindInterfaceItems();
	hm.options.selectAllOptions('serialNumbers');
	hm.options.selectAllOptions('ipSubNetworks');
    document.forms[formName].submit();
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

function validateCapwapIp(operation) {
	var capwapIpNames = document.getElementById("cfgCapwapIpId");
	var capwapIpValue = document.getElementById("dataSource.capwapText");
	var showError = document.getElementById("errorDisplay");

	var capwapBackupIpNames = document.getElementById("cfgCapwapBackupIpId");
	var capwapBackupIpValue = document.getElementById("dataSource.capwapBackupText");
	var showBackupError = document.getElementById("errorBackupDisplay");

    if ("" != capwapIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(capwapIpNames, capwapIpValue)) {
	    	if(operation == 'update' || operation == 'create'){
		    	if (!hm.util.validateIpAddress(capwapIpValue.value)) {
					var message = hm.util.validateName(capwapIpValue.value, '<s:text name="hiveAp.capwap.server" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showError, message);
				   		capwapIpValue.focus();
				       	return false;
				   	}
				}
			}
	    	document.forms[formName].capwapIpId.value = -1;
	    } else {
	    	document.forms[formName].capwapIpId.value = capwapIpNames.options[capwapIpNames.selectedIndex].value;
	    }
	} else {
		document.forms[formName].capwapIpId.value = -1;
	}
    if ("" != capwapBackupIpValue.value) {
	    if (!hm.util.hasSelectedOptionSameValue(capwapBackupIpNames, capwapBackupIpValue)) {
	    	if(operation == 'update' || operation == 'create'){
		    	if (!hm.util.validateIpAddress(capwapBackupIpValue.value)) {
					var message = hm.util.validateName(capwapBackupIpValue.value, '<s:text name="hiveAp.capwap.server.backup" />');
				   	if (message != null) {
				   		hm.util.reportFieldError(showBackupError, message);
				       	capwapBackupIpValue.focus();
				       	return false;
				   	}
				}
	    	}
			document.forms[formName].capwapBackupIpId.value = -1;
	    } else {
			document.forms[formName].capwapBackupIpId.value = capwapBackupIpNames.options[capwapBackupIpNames.selectedIndex].value;
	    }
	} else {
		document.forms[formName].capwapBackupIpId.value = -1;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="autoProvisioningConfig" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

function changImageVersion(value){
	//alert(value);
	//alert("3.5.0.0" > value);

	var wifi0ModelList = document.getElementById(formName+"_dataSource_wifi0_operationMode");
	var wifi1ModelList = document.getElementById(formName+"_dataSource_wifi1_operationMode");
	var wifi0Value = wifi0ModelList.value;
	var wifi1Value = wifi1ModelList.value;
	wifi0ModelList.length = wifi1ModelList.length = 0;
	if("4.0.1.0" > value){
		wifi0ModelList.length = wifi1ModelList.length = 2;
		wifi0ModelList.options[0].value = wifi1ModelList.options[0].value = OPERATION_MODE_ACCESS;
		wifi0ModelList.options[1].value = wifi1ModelList.options[1].value = OPERATION_MODE_BACKHAUL;

		wifi0ModelList.options[0].text = wifi1ModelList.options[0].text = "Access";
		wifi0ModelList.options[1].text = wifi1ModelList.options[1].text = "Backhaul";
	}else if("5.1.2.0">value){
		wifi0ModelList.length = wifi1ModelList.length = 3;
		wifi0ModelList.options[0].value = wifi1ModelList.options[0].value = OPERATION_MODE_ACCESS;
		wifi0ModelList.options[1].value = wifi1ModelList.options[1].value = OPERATION_MODE_BACKHAUL;
		wifi0ModelList.options[2].value = wifi1ModelList.options[2].value = OPERATION_MODE_DUAL;

		wifi0ModelList.options[0].text = wifi1ModelList.options[0].text = "Access";
		wifi0ModelList.options[1].text = wifi1ModelList.options[1].text = "Backhaul";
		wifi0ModelList.options[2].text = wifi1ModelList.options[2].text = "Dual";
	}else{
		wifi0ModelList.length = wifi1ModelList.length = 4;
		wifi0ModelList.options[0].value = wifi1ModelList.options[0].value = OPERATION_MODE_ACCESS;
		wifi0ModelList.options[1].value = wifi1ModelList.options[1].value = OPERATION_MODE_BACKHAUL;
		wifi0ModelList.options[2].value = wifi1ModelList.options[2].value = OPERATION_MODE_DUAL;
		wifi0ModelList.options[3].value = wifi1ModelList.options[3].value = OPERATION_MODE_SENSOR;

		wifi0ModelList.options[0].text = wifi1ModelList.options[0].text = "Access";
		wifi0ModelList.options[1].text = wifi1ModelList.options[1].text = "Backhaul";
		wifi0ModelList.options[2].text = wifi1ModelList.options[2].text = "Dual";
		wifi0ModelList.options[3].text = wifi1ModelList.options[3].text = "Sensor";
	}
	for(var j=0; j<wifi0ModelList.length; j++){
		if(wifi0ModelList.options[j].value == wifi0Value){
			wifi0ModelList.selectedIndex = j;
		}
		if(wifi1ModelList.options[j].value == wifi1Value){
			wifi1ModelList.selectedIndex = j;
		}
	}
	displaySelectedImageName(value);
}

function saveOptionUnfoldStatus() {
	if (Get("capwapSettings")) {
		Get(formName+"_dataSource_capwapConfigOptionDisplayStyle").value = Get("capwapSettings").style.display;
	}
	if (Get("interfaceSettings")) {
		Get(formName+"_dataSource_interfaceSettingOptionDisplayStyle").value = Get("interfaceSettings").style.display;
	}
	if (Get("advancedSettings")) {
		Get(formName+"_dataSource_advancedSettingOptionDisplayStyle").value = Get("advancedSettings").style.display;
	}
}

//OTP Start
function updateOneTimePassword(value){
	var el = document.getElementById("otpTr");
	if(el){
		if (value == DEVICE_TYPE_BRANCH_ROUTER){
			var modelType = document.getElementById(formName + "_dataSource_modelType").value;
			if(modelType == MODEL_HIVEAP_SR24 || modelType == MODEL_HIVEAP_SR2124P || modelType == MODEL_HIVEAP_SR2024P || modelType == MODEL_HIVEAP_SR2148P){
				document.getElementById("otpTr").style.display = "none";
			}else{
				document.getElementById("otpTr").style.display = "";
			}
		}else{
			document.getElementById("otpTr").style.display = "none";
		}
	}
}

function countryCodeChange(element){
	if(element.value == 392){
		document.getElementById("noteForJapan").style.display = "";
	}else{
		document.getElementById("noteForJapan").style.display = "none";
	}
}
</script>

<div id="content"><s:form action="autoProvisioningConfig">
	<s:hidden name="radioType"/>
	<s:hidden name="configTemplateId" />
	<s:hidden name="capwapIpId" />
	<s:hidden name="capwapBackupIpId" />
	<s:hidden name="snString"></s:hidden>
	<s:hidden name="ipSubNetworkString" />
	<s:hidden name="dataSource.capwapConfigOptionDisplayStyle" />
	<s:hidden name="dataSource.interfaceSettingOptionDisplayStyle" />
	<s:hidden name="dataSource.advancedSettingOptionDisplayStyle" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" style="padding: 0 4px 6px 4px;"
				cellspacing="0" cellpadding="0" border="0" width="920px">
				<tr>
					<td><!-- global control -->
					<%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td colspan="2" class="labelT1"><s:checkbox
								name="dataSource.autoProvision"
								value="%{dataSource.autoProvision}" />
								<label><s:text
								name="hiveAp.autoProvisioning.flag" /></label></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td style="padding-left: 15px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td><!-- properties section -->
							<fieldset><legend><s:text name="hiveAp.autoProvisioning.apProperties.label" /></legend>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="152px">
										<label><s:text name="hiveAp.autoProvisioning.apProperties.name" /><font color="red"><s:text name="*"/></font></label>
									</td>
									<td  width="300px"><s:textfield name="dataSource.name" maxlength="%{nameLength}" disabled="%{disabledName}"
										id = "autoProvisioningNameId"
										onkeypress="return hm.util.keyPressPermit(event,'name');"/>&nbsp;<s:text name="config.name.range"/></td>
									<td class="labelT1" width="152px">
										<label><s:text name="hiveAp.model" /></label>
									</td>
									<td><s:select name="dataSource.modelType" list="%{apModel}" listKey="key"
										listValue="value" cssStyle="width: 150px;" onchange="apModelChanged();getPseWithModel();changeBRUsbModeName();" disabled="%{disabledName}"/></td>
								</tr>
								<tr>
									<td class="labelT1" width="152px">
										<label><s:text name="hiveAp.autoProvisioning.apProperties.description" /></label>
									</td>
									<td><s:textfield name="dataSource.description" cssStyle="width:270px;" maxlength="255"/></td>
									<td class="labelT1" width="152px" style="display: <s:property value="%{wirelessRoutingStyle}"/>">
										<label><s:text name="hiveAp.autoProvisioning.apProperties.device.type" /></label>
									</td>
									<td style="display: <s:property value="%{wirelessRoutingStyle}"/>">
									<s:select name="dataSource.deviceType" list="%{enumDeviceType}" listKey="key"
										listValue="value" cssStyle="width: 150px;" onchange="apDeviceChanged(this.value);" disabled="%{disabledName}"/></td>
								</tr>
								<!-- <tr>
									<td colspan="4" class="noteInfo" style="padding: 5px 10px 0;"><s:text name="hiveAp.autoProvisioning.property.note" /></td>
								</tr>-->
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td>
								<table>
								<tr>
										<td><!-- access control section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td colspan="2" class="labelT1" ><s:checkbox name="dataSource.accessControled" 
													onclick="accessControlCheck(this);" /><label style="margin-top:1px;"><s:text
													name="hiveAp.autoProvisioning.access.control.label" /></label></td>
												<td></td>
											</tr>
											<tr id="aclSection" style="display: <s:property value="ACLStatus"/>">
												<td></td>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0" align="center">
																	<tr>
																		<td colspan="2">
																		<s:hidden name="dataSource.aclType" value="1"/>
																		<label><s:text
																			name="hiveAp.autoProvisioning.serialNumber.title" /></label>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr><td height="5px"></td></tr>
														<tr id="snSection"><!-- ACL serial number -->
															<td style="padding-left: 35px;">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<s:push value="%{snOptions}">
																			<td><tiles:insertDefinition
																				name="optionsTransfer" /></td>
																		</s:push>
																	</tr>
																	<tr><td height="2px"></td></tr>
																	<tr>
																		<td>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<input type="button" name="importSn" value="Import" class="button" style="width: 62px;" <s:property value="writeDisabled" /> onclick="submitAction('importSn')">
																				</td>
																				<td>
																					<input type="button" name="removeSn" value="Remove" class="button" style="width: 62px;" <s:property value="writeDisabled" /> onclick="removeSerialNumbers()">
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
												<!-- for ip subnetwork import -->
												<td>
													<table cellspacing="0" cellpadding="0" border="0" style="padding-left:95px;display: <s:property value="wirelessRoutingStyle"/>">
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0" align="center">
																	<tr>
																		<td colspan="2">
																		<label><s:text
																			name="hiveAp.autoProvisioning.ipSubNetwork.title" /></label>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr><td height="5px"></td></tr>
														<tr id="ipInterfaceSection"><!-- ip subnetworks -->
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<s:push value="%{ipSubNetworkOptions}">
																			<td><tiles:insertDefinition
																				name="optionsTransfer" /></td>
																		</s:push>
																	</tr>
																	<tr><td height="2px"></td></tr>
																	<tr>
																		<td>
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td>
																					<input type="button" name="importIpSubNetwork" value="Import" class="button" style="width: 62px;" <s:property value="writeDisabled" /> onclick="submitAction('importIpSubNetwork')">
																				</td>
																				<td>
																					<input type="button" name="removeIpSubNetwork" value="Remove" class="button" style="width: 62px;" <s:property value="writeDisabled" /> onclick="removeIpSubNetworks()">
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
								</table>
							</td>
						</tr>
						<!-- OTP Start -->
						<tr id = "otpTr" style="display: <s:property value="otpTrStyle"/>">
							<td>
								<table>
								<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td colspan="2" class="labelT1"><s:checkbox name="dataSource.enableOneTimePassword"/><label style="margin-top:1px;"><s:text
													name="hiveAp.autoProvisioning.otp.enable" /></label></td>
												<td></td>
											</tr>
											<tr>
												<td colspan="2" class="noteInfo" style="padding-left:32px;" ><s:text name="hiveAp.autoProvisioning.otp.enable.note" /></td>
												<td></td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- OTP End  -->
						
						<tr><td height="10px"></td></tr>
									<tr id="HiveApAutoProvisionTag">
										<td>
										<fieldset><legend><s:text
											name="hiveAp.classification.device.tag" /></legend>
										<table cellspacing="0" cellpadding="0" border="0"
											width="100%" class="embedded">
											<tr>
												<td height="10"></td>
											</tr>
											<tr id="apAutoClassifierTagContainer"> 	</tr>
										</table>
										</fieldset>
										</td>
						</tr>
						
						<tr><td height="15px"></td></tr>
						<tr>
							<td><!-- configuration section -->
							<fieldset><legend><s:text name="hiveAp.autoProvisioning.configuration.label" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td><!-- general settings section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" width="152px" style="display: <s:property value="%{fullModeConfigStyle}"/>">
													<label><s:text name="hiveAp.template"/><font color="red"><s:text name="*"/></font></label>
												</td>
												<td style="display: <s:property value="%{fullModeConfigStyle}"/>">
													<s:select list="%{configTemplates}" listKey="id" listValue="value" name="dataSource.configTemplateId" cssStyle="width: 180px;" ></s:select>
												</td>
												<td class="labelT1" width="152px"><label><s:text name="hiveAp.autoProvisioning.defaultTopoMap.label" /></label></td>
												<td>
													<s:select list="%{topoMaps}" listKey="id" listValue="value" name="dataSource.mapContainerId" cssStyle="width: 200px;"></s:select>
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<label><s:text name="hiveAp.countryCode" /></label>
												</td>
												<td><s:select name="dataSource.countryCode" list="%{countryCodeValues}" listKey="key"
													listValue="value" cssStyle="width: 290px;" onchange="requestChannels();" /></td>
											</tr>
											<tr id="noteForJapan" style="display:none">
												<td class="noteInfo" colspan="10" style="padding-left : 10px">
													<s:text name="hiveAp.update.countryCode.japan.note"/>
												</td>
											</tr>
											<tr>
												<td height="10px"/>
											</tr>
										</table>
									</td>
								</tr>
								<tr style="display: <s:property value="%{fullModeConfigStyle}"/>"><td height="5px"></td></tr>
								<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
									<td><!-- admin settings section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td width="50%" valign="top">
												<fieldset><legend><s:text name="hiveAp.superUser.tag"/></legend>
												<table cellspacing="0" cellpadding="0" border="0" class="embedded">
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
																name="hiveAp.newUser" /></td>
															<td><s:textfield name="dataSource.cfgAdminUser" size="16" maxlength="%{cfgAdminUserLength}"
																onkeypress="return hm.util.keyPressPermit(event,'username');"/> <s:text name="hiveAp.currentUserRange"/></td>
															</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
															name="hiveAp.newPassword" /></td>
															<td><s:password name="dataSource.cfgPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgAdminPassword" showPassword="true"/><s:textfield name="dataSource.cfgPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgAdminPassword_text" disabled="true" cssStyle="display: none;"/>
															 <s:text name="hiveAp.currentPasswordRange"/></td>
															</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
															name="hiveAp.newConfirmPassword" /></td>
															<td><s:password name="confirmNewPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfCfgAdminPassword" value="%{dataSource.cfgPassword}" showPassword="true"/>
															 <s:textfield name="confirmNewPassword" size="16" maxlength="32"
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
												<td width="3px"></td>
												<td valign="top">
												<fieldset><legend><s:text name="hiveAp.readOnlyUser.tag"/></legend>
												<table cellspacing="0" cellpadding="0" border="0" class="embedded">
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
																name="hiveAp.newUser" /></td>
															<td><s:textfield name="dataSource.cfgReadOnlyUser" size="16" maxlength="%{cfgReadOnlyUserLength}"
																onkeypress="return hm.util.keyPressPermit(event,'username');"/> <s:text name="hiveAp.currentUserRange"/></td>
															</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
															name="hiveAp.newPassword" /></td>
															<td><s:password name="dataSource.cfgReadOnlyPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgReadOnlyPassword" showPassword="true"/><s:textfield name="dataSource.cfgReadOnlyPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfgReadOnlyPassword_text" disabled="true" cssStyle="display: none;"/>
															 <s:text name="hiveAp.currentPasswordRange"/></td>
															</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
															<tr>
															<td class="labelT1" width="140px"><s:text
															name="hiveAp.newConfirmPassword" /></td>
															<td><s:password name="confirmNewReadOnlyPassword" size="16" maxlength="32"
															 onkeypress="return hm.util.keyPressPermit(event,'password');" id="confirmCfgReadOnlyPassword" value="%{dataSource.cfgReadOnlyPassword}" showPassword="true"/>
															 <s:textfield name="confirmNewReadOnlyPassword" size="16" maxlength="32"
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
										</table>
									</td>
								</tr>
								<tr style="display: <s:property value="%{fullModeConfigStyle}"/>"><td height="10px"></td></tr>
								<tr style="display: <s:property value="%{fullModeConfigStyle}"/>"><td>
									<script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.capwap.tag" />','capwapSettings');</script>
								</td></tr>
								<tr style="display: <s:property value="%{fullModeConfigStyle}"/>"><td height="5px"></td></tr>
								<tr id="capwapSettings" style='display: <s:property value="%{dataSource.capwapConfigOptionDisplayStyle}"/>;'>
									<td><!-- capwap section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%" style="padding-left:20px;">
											<tr>
												<td>
													<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
														<tr>
															<td>
																<table border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																<td colspan="2" width="50%"><s:checkbox name="changePassPhrase" onclick="clickDtlsBox();" disabled="%{writeDisable4Struts}"
																	value="%{changePassPhrase}" id="changePassPhrase"/>
																<s:text name="hiveAp.dtls.enableChange" /></td>
																<td class="labelT1" width="153px"><s:text
																	name="hiveAp.capwap.server" /></td>
																<td><ah:createOrSelect divId="errorDisplay" list="capwapIps" typeString="CapwapIp"
																	selectIdName="cfgCapwapIpId" inputValueName="dataSource.capwapText" swidth="150px" /></td>
																</tr>
																<tr>
																<td class="labelT1" width="130px"><s:text
																	name="hiveAp.dtls.newPassPhrase" /></td>
																<td><s:password name="dataSource.passPhrase" size="16" id="newDtls"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"
																	maxlength="32" showPassword="true" disabled="%{passPhraseDisabled}"/>
																	<s:textfield name="dataSource.passPhrase" size="16" id="newDtls_text"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"
																	maxlength="32" cssStyle="display: none;" disabled="true"/>
																	<s:text name="hiveAp.dtls.passPhraseRange"/></td>
																<td class="labelT1"><s:text name="hiveAp.capwap.server.backup" /></td>
																<td><ah:createOrSelect divId="errorBackupDisplay" list="capwapIps" typeString="CapwapBackupIp"
																	selectIdName="cfgCapwapBackupIpId" inputValueName="dataSource.capwapBackupText" swidth="150px" /></td>
																</tr>
																<tr>
																<td class="labelT1"><s:text
																	name="hiveAp.dtls.confirmPassPhrase" /></td>
																<td><s:password value="%{dataSource.passPhrase}" size="16" id="confirmDtls"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"
																	maxlength="32" showPassword="true" disabled="%{passPhraseDisabled}"/>
																	<s:textfield value="%{dataSource.passPhrase}" size="16" id="confirmDtls_text"
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
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr id="interfaceSettingsController" style="display: <s:property value="%{interfaceSettingStyle}"/>">
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr><td height="10px"></td></tr>
											<tr><td>
												<script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.autoProvisioning.interface.setting.label" />','interfaceSettings');</script>
											</td></tr>
											<tr><td height="5px"></td></tr>
											<tr id="interfaceSettings" style='display: <s:property value="%{dataSource.interfaceSettingOptionDisplayStyle}"/>;'>
												<td><!-- interface settings section -->
													<table cellspacing="0" cellpadding="0" border="0" width="100%" style="padding-left:20px;">
													<tr>
														<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr id="usbModemSettingSection" style="display: <s:property value="%{usbModemSettingShownStyle}"/>" >
																	<td>
																		<fieldset style="margin-bottom:10px;">
																			<legend><s:text name="hiveAp.brRouter.usb.modem.settings" /></legend>
																			<table cellspacing="0" cellpadding="0" border="0" width="100%">
																				<tr>
																					<td height="10"></td>
																				</tr>
																				<tr>
																					<td width="120"></td>
																					<td><s:radio label="Gender"
																							name="dataSource.usbConnectionModel" list="%{usbConnectNeeded}"
																							listKey="key" listValue="value"
																							onclick="this.blur();" /></td>
																					<td width="50"></td>
																					<td><s:radio label="Gender"
																							name="dataSource.usbConnectionModel" list="%{usbConnectAlways}"
																							listKey="key" listValue="value"
																							onclick="this.blur();" /></td>
																					<td width="120"></td>
																				</tr>
																			</table>
																		</fieldset>
																	</td>
																</tr>
																<tr id="interfaceSetting1"  style="display: <s:property value="%{notBR100LikeStyle}"/>" >
																	<td>
																		<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
																			<tr><td height="10px"></td></tr>
																			<tr>
																				<th align="left" width="80px"><s:text name="hiveAp.lanIf" /></th>
																				<th align="left" width="90px"><s:text name="hiveAp.if.adminState" /></th>
																				<th align="left" width="80px"><s:text name="hiveAp.if.allowedVlan" /></th>
																				<th align="left" width="135px"><s:text name="hiveAp.if.operationMode" /></th>
																				<th align="left" width="145px"><s:text name="hiveAp.if.duplex" /></th>
																				<th align="left" nowrap="nowrap"><s:text name="hiveAp.if.speed" /></th>
																				<th id="eth0BindHeader" align="left" nowrap="nowrap" style="display: <s:property value="eth1StuffStyle"/>"><s:text name="hiveAp.if.bindIf" /></th>
																				<th id="eth0RoleHeader" align="left" nowrap="nowrap" style="display: <s:property value="eth1StuffStyle"/>"><s:text name="hiveAp.if.bindRole" /></th>
																			</tr>
																			<tr><td height="5px"></td></tr>
																			<tr>
																				<td class="list"><s:text name="hiveAp.if.eth0"/></td>
																				<td class="list"><s:select name="dataSource.eth0.adminState"
																					value="%{dataSource.eth0.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value" onchange="ethAdminStatusChanged();"/></td>
																				<td class="list"><s:textfield name="dataSource.eth0.allowedVlan" size="8" maxlength="255"
																					title="%{allowedVlanTitle}" onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																				<td class="list"><s:select name="dataSource.eth0.operationMode"
																					value="%{dataSource.eth0.operationMode}" list="%{enumEthOperationMode}" listKey="key"
																					listValue="value" onchange="ethOperationModeChanged()"/></td>
																				<td class="list"><s:select name="dataSource.eth0.duplex"
																					value="%{dataSource.eth0.duplex}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" cssStyle="width: 106px;"/></td>
																				<td class="list"><s:select name="dataSource.eth0.speed"
																					value="%{dataSource.eth0.speed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" cssStyle="width: 84px;" onchange="changeSpeed(this)"/></td>
																				<td id="eth0BindCell" class="list" style="display: <s:property value="eth1StuffStyle"/>"><s:select name="dataSource.eth0.bindInterface"
																					value="%{dataSource.eth0.bindInterface}" list="%{enumBindInterface}" listKey="key"
																					listValue="value" cssStyle="width: 60px;" onchange="bindInterfaceChanged()"/></td>
																				<td id="eth0RoleCell" class="list" style="display: <s:property value="eth1StuffStyle"/>"><s:select name="dataSource.eth0.bindRole"
																					value="%{dataSource.eth0.bindRole}" list="%{enumBindRole}" listKey="key"
																					listValue="value" cssStyle="width: 80px;"/></td>
																			</tr>
																			<tr id="eth1Row" style="display: <s:property value="eth1StuffStyle"/>">
																				<td class="list"><s:text name="hiveAp.if.eth1"/></td>
																				<td class="list"><s:select name="dataSource.eth1.adminState"
																					value="%{dataSource.eth1.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value" onchange="ethAdminStatusChanged()"/></td>
																				<td class="list"><s:textfield name="dataSource.eth1.allowedVlan" size="8" maxlength="255"
																					title="%{allowedVlanTitle}" onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																				<td class="list"><s:select name="dataSource.eth1.operationMode"
																					value="%{dataSource.eth1.operationMode}" list="%{enumEthOperationMode}" listKey="key"
																					listValue="value" onchange="ethOperationModeChanged()"/></td>
																				<td class="list"><s:select name="dataSource.eth1.duplex"
																					value="%{dataSource.eth1.duplex}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" cssStyle="width: 106px;"/></td>
																				<td class="list"><s:select name="dataSource.eth1.speed"
																					value="%{dataSource.eth1.speed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" cssStyle="width: 84px;" /></td>
																				<td id="eth1BindCell" class="list"><s:select name="dataSource.eth1.bindInterface"
																					value="%{dataSource.eth1.bindInterface}" list="%{enumBindInterface}" listKey="key"
																					listValue="value" cssStyle="width: 60px;" onchange="bindInterfaceChanged()"/></td>
																				<td id="eth1RoleCell" class="list"><s:select name="dataSource.eth1.bindRole"
																					value="%{dataSource.eth1.bindRole}" list="%{enumBindRole}" listKey="key"
																					listValue="value" cssStyle="width: 80px;"/></td>
																			</tr>
																			<tr id="red0Row" style="display: <s:property value="eth1StuffStyle"/>">
																				<td class="list"><s:text name="hiveAp.if.red0"/></td>
																				<td class="list"><s:select name="dataSource.red0.adminState"
																					value="%{dataSource.red0.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:textfield name="dataSource.red0.allowedVlan" size="8" maxlength="255"
																					title="%{allowedVlanTitle}" onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
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
																				<td class="list"><s:textfield name="dataSource.agg0.allowedVlan" size="8" maxlength="255"
																					title="%{allowedVlanTitle}" onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
																				<td class="list"><s:select name="dataSource.agg0.operationMode"
																					value="%{dataSource.agg0.operationMode}" list="%{enumRedOperationMode}" listKey="key"
																					listValue="value"/></td>
																				<td class="list" colspan="6">&nbsp;</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<!-- for interface setting of BR100 -->
																<tr id="interfaceSetting2" style="display: <s:property value="%{bR100LikeStyle}"/>">
																	<td>
																		<table id="interfaceEthSettingTable" cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
			
																			<tr>
																				<th align="left" width="100px"><s:text name="hiveAp.autoProvisioning.br100.if.port" /></th>
																				<th align="left" width="160px"><s:text name="hiveAp.autoProvisioning.br100.if.role" /></th>
																				<th align="left" width="120px"><s:text name="hiveAp.autoProvisioning.br100.if.adminState" /></th>
																				<th align="left" width="160px"><s:text name="hiveAp.autoProvisioning.br100.if.transmissionType" /></th>
																				<th align="left" width="120px"><s:text name="hiveAp.autoProvisioning.br100.if.speed" /></th>
																				<th id="head_enableNat" align="left" width="62px"><s:text
																					name="hiveAp.brRouter.port.settings.enableNat" /></th>
																				</tr>
			
																			<tr>
																				<td class="list">
																					<s:hidden name="br100Eth0.interfacePort"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.port.eth0"/>
																				</td>
																				<td class="list">
																					<table cellspacing="0" cellpadding="0" border="0" width="100%"><tr>
																						<td id="br_eth0_setting_role_backhaul" style="display:none" >
																							<s:text name="hiveAp.autoProvisioning.br100.if.role.backhaul"/>
																						</td>
																						<td id="br_eth0_setting_role">
																							<s:select
																								name="br100Eth0.interfaceRole"
																								value="%{br100Eth0.interfaceRole}"
																								list="%{enumUsbRoleType}" listKey="key"
																								listValue="value" cssStyle="width: 80px;" 
																								onchange="branchRouterEth0RoleChanged(this.value);"/>
																						</td>
																					</tr></table>
																				</td>
																				<td class="list"><s:select name="br100Eth0.adminState"
																					value="%{br100Eth0.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth0.interfaceTransmissionType"
																					value="%{br100Eth0.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth0.interfaceSpeed"
																					value="%{br100Eth0.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value"/></td>
																				<td id="eth0_enableNat" class="list">
																					<s:checkbox name="br100Eth0.enableNat" />
																				</td>
																				</tr>
																			<tr>
																				<td class="list">
																					<s:hidden name="br100Eth1.interfacePort"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/>
																				</td>
																				<td class="list">
																					<s:hidden name="br100Eth1.interfaceRole"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.role.wan.lan"/></td>
																				<td class="list"><s:select name="br100Eth1.adminState"
																					value="%{br100Eth1.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth1.interfaceTransmissionType"
																					value="%{br100Eth1.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth1.interfaceSpeed"
																					value="%{br100Eth1.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value"/></td>
																				</tr>
																			<tr id="brWith4LanPortEth2Id" style="display:<s:property value='brWith4LanPortString' />">
																				<td class="list">
																					<s:hidden name="br100Eth2.interfacePort"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/>
																				</td>
																				<td class="list">
																					<s:hidden name="br100Eth2.interfaceRole"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.role.wan.lan"/></td>
																				<td class="list"><s:select name="br100Eth2.adminState"
																					value="%{br100Eth2.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth2.interfaceTransmissionType"
																					value="%{br100Eth2.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" /></td>
																				<td class="list"><s:select name="br100Eth2.interfaceSpeed"
																					value="%{br100Eth2.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" /></td>
																				</tr>
																			<tr id="brWith4LanPortEth3Id" style="display:<s:property value='brWith4LanPortString' />">
																				<td class="list">
																					<s:hidden name="br100Eth3.interfacePort"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.port.eth3"/>
																				</td>
																				<td class="list">
																					<s:hidden name="br100Eth3.interfaceRole"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.role.wan.lan"/></td>
																				<td class="list"><s:select name="br100Eth3.adminState"
																					value="%{br100Eth3.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth3.interfaceTransmissionType"
																					value="%{br100Eth3.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" /></td>
																				<td class="list"><s:select name="br100Eth3.interfaceSpeed"
																					value="%{br100Eth3.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" /></td>
																				</tr>
																			<tr id="brWith4LanPortEth4Id" style="display:<s:property value='brWith4LanPortString' />">
																				<td class="list">
																					<s:hidden name="br100Eth4.interfacePort"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.port.eth4"/>
																				</td>
																				<td class="list">
																					<s:hidden name="br100Eth4.interfaceRole"/>
																					<s:text name="hiveAp.autoProvisioning.br100.if.role.wan.lan"/></td>
																				<td class="list"><s:select name="br100Eth4.adminState"
																					value="%{br100Eth4.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Eth4.interfaceTransmissionType"
																					value="%{br100Eth4.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" /></td>
																				<td class="list"><s:select name="br100Eth4.interfaceSpeed"
																					value="%{br100Eth4.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" /></td>
																				</tr>
																			<tr id="interfacePortOfUsb" style="display:<s:property value='interfacePortOfUsbDisplayString' />">
																				<td class="list">
																					<s:hidden name="br100Usb.interfacePort"/>
																					<label id="usbModeName"><s:property value="bRPortUSBModeDisplayString"/></label>																																							
																				</td>
																				<td class="list">
																					<s:select
																						name="br100Usb.interfaceRole"
																						value="%{br100Usb.interfaceRole}"
																						list="%{enumUsbRoleType}" listKey="key"
																						listValue="value" cssStyle="width: 80px;" 
																						onchange="branchRouterUsbRoleChanged(this.value);"/>
																				</td>
																				<td class="list"><s:select name="br100Usb.adminState"
																					value="%{br100Usb.adminState}" list="%{enumAdminStateType}" listKey="key"
																					listValue="value"/></td>
																				<td class="list"><s:select name="br100Usb.interfaceTransmissionType"
																					value="%{br100Usb.interfaceTransmissionType}" list="%{enumDuplexType}" listKey="key"
																					listValue="value" disabled="true" /></td>
																				<td class="list"><s:select name="br100Usb.interfaceSpeed"
																					value="%{br100Usb.interfaceSpeed}" list="%{enumSpeedType}" listKey="key"
																					listValue="value" disabled="true" /></td>
																				<td id="usb_enableNat" class="list">
																					<s:checkbox name="br100Usb.enableNat" />
																				</td>
																				</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr id="pseSettingAllDiv" style="display: none;">
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td height="10px"></td>
																</tr>
																<tr>
																	<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.brRouter.pse.settings" />','pseSettingDiv');</script></td>
																</tr>
																<tr>
																	<td>
																		<div id="pseSettingDiv" style="display: '';">
																			<table cellspacing="0" cellpadding="0" border="0" width="100%">
																				<tr><td height="10px"/>
																				</tr>
																				<tr>
																					<td valign="top" style="padding-left: 15px">
																						<table cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
																							<tr>
																								<th align="left" width="50px"><s:text name="hiveAp.brRouter.port.settings.port" /></th>
																								<th align="left" width="80px"><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
																								<th align="left" width="160px"><s:text name="hiveAp.brRouter.pse.settings.mode"/></th>
																								<th align="left" width="150px"><s:text name="hiveAp.brRouter.pse.settings.priority"/></th>
																							</tr>
																							<tr>
																								<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
																								<td class="list"><s:checkbox name="br100Eth1.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth1PseState')"></s:checkbox></td>
																								
																								<td class="list"><s:select name="br100Eth1.pseState" id="eth1PseState" cssStyle="width: 130px;"
																										value="%{br100Eth1.pseState}" list="%{enumPseType}" listKey="key"
																										listValue="value" disabled="br100Eth1.disabledPseState"/></td>
																								<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																									list="#{'0':''}" value="%{radioPsePriority}" />&nbsp;</td>
																							</tr>
																							<tr>
																								<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
																								<td class="list"><s:checkbox name="br100Eth2.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth2PseState')"></s:checkbox></td>
																								
																								<td class="list"><s:select name="br100Eth2.pseState" id="eth2PseState" cssStyle="width: 130px;"
																										value="%{br100Eth2.pseState}" list="%{enumPseType}" listKey="key"
																										listValue="value" disabled="br100Eth2.disabledPseState"/></td>
																								<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																									list="#{'1':''}" value="%{radioPsePriority}" />&nbsp;</td>
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
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
																<tr>
																	<td height="10px"></td>
																</tr>
																<tr id="wifiConfigTitle">
																	<th align="left" width="80px"><s:text name="hiveAp.wlanIf" /></th>
																	<th align="center" width="50px"><s:text name="hiveAp.if.radioMode" /></th>
																	<th align="left" width="220px"><s:text name="hiveAp.if.radioProfile" /></th>
																	<th align="left" width="100px"><s:text name="hiveAp.if.adminState" /></th>
																	<th align="left" width="135px"><s:text name="hiveAp.if.operationMode" /></th>
																	<th align="left" width="110px"><s:text name="hiveAp.if.channel" /></th>
																	<th align="left"><s:text name="hiveAp.if.power" /></th>
																</tr>
																<tr>
																	<td height="5px"></td>
																</tr>
																<tr id="wifi0Row">
																	<td class="list"><span id="wifi0Label"><s:property value="%{wifi0Label}"/></span> <s:text name="hiveAp.if.wifi0" /></td>
																	<td id="wifi0RadioMode" class="list" align="center">
																		<s:property value="%{wifi0RadioModeLabel}"/>
																	</td>
																	<td class="list"><s:select name="dataSource.wifi0ProfileId" list="%{wifi0RadioProfiles}" listKey="id"
																		listValue="value" cssStyle="width: 150px;" onchange="addWifi0RadioProfile(this)"/></td>
																	<td class="list"><s:select name="dataSource.wifi0.adminState"
																		value="%{dataSource.wifi0.adminState}" list="%{enumAdminStateType}" listKey="key"
																		listValue="value"/></td>
																	<td class="list"><s:select name="dataSource.wifi0.operationMode" onchange="wifiOperationModeChanged(this.value, 'wifi0');"
																		value="%{dataSource.wifi0.operationMode}" list="%{enumWifiOperationMode}" listKey="key"
																		listValue="value" /></td>
																	<td class="list"><s:select name="dataSource.wifi0.channel" value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}" listKey="key"
																		listValue="value"/></td>
																	<td class="list"><s:select name="dataSource.wifi0.power"
																		value="%{dataSource.wifi0.power}" list="%{enumPowerType}" listKey="key"
																		listValue="value"/></td>
																</tr>
																<tr id="wifi1Row" style="display: <s:property value="wifi1StuffStyle"/>">
																	<td class="list"><span id="wifi1Label"><s:property value="%{wifi1Label}"/></span> <s:text name="hiveAp.if.wifi1" /></td>
																	<td id="wifi1RadioMode" class="list" align="center">
																		<s:property value="%{wifi1RadioModeLabel}"/>
																	</td>
																	<td class="list"><s:select name="dataSource.wifi1ProfileId" list="%{wifi1RadioProfiles}" listKey="id"
																		listValue="value" cssStyle="width: 150px;" onchange="addWifi1RadioProfile(this)"/></td>
																	<td class="list"><s:select name="dataSource.wifi1.adminState"
																		value="%{dataSource.wifi1.adminState}" list="%{enumAdminStateType}" listKey="key"
																		listValue="value"/></td>
																	<td class="list"><s:select name="dataSource.wifi1.operationMode" onchange="wifiOperationModeChanged(this.value, 'wifi1');"
																		value="%{dataSource.wifi1.operationMode}" list="%{enumWifiOperationMode}" listKey="key"
																		listValue="value" /></td>
																	<td class="list"><s:select name="dataSource.wifi1.channel" value="%{dataSource.wifi1.channel}" list="%{wifi1Channel}" listKey="key"
																		listValue="value"/></td>
																	<td class="list"><s:select name="dataSource.wifi1.power"
																		value="%{dataSource.wifi1.power}" list="%{enumPowerType}" listKey="key"
																		listValue="value"/></td>
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
							</fieldset>
							</td>
						</tr>
						<tr><td height="10px"></td></tr>
						<tr><td>
							<script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.autoProvisioning.advanced.setting.label" />','advancedSettings');</script>
						</td></tr>
						<tr><td height="5px"></td></tr>
						<tr id="advancedSettings" style='display: <s:property value="%{dataSource.advancedSettingOptionDisplayStyle}"/>;'>
							<td>
								<table style="padding-left:15px;">
									<tr>
										<td><!-- image upload section -->
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td width="15px"><s:checkbox name="dataSource.uploadImage"
													value="%{dataSource.uploadImage}"
													onclick="imageInstallChanged(this.checked);" /></td>
												<td colspan="10" class="labelT1"><label><s:text
													name="hiveAp.autoProvisioning.uploadImage" /></label></td>
											</tr>
											<tr>
												<td></td>
												<td width="180px" class="labelT1"><label><s:text
													name="hiveAp.autoProvisioning.imageVersion.label" /></label></td>
												<td width="80px"><s:select list="%{versionList}" listKey="key"
													listValue="value" name="dataSource.imageVersion"
													disabled="%{imageInstallDisabled}" onchange="changImageVersion(this.value);"></s:select>
												</td>
												<td>
													<span id="imageNameSpan"><s:property value="dataSource.imageNameHtml" escapeHtml="false"/></span>
													<s:if test="%{isInHomeDomain && !dsEnable}">
													<input type="button" name="importImage" id="importImage"
															value="Add/Remove" class="button" style="width: 85px; margin-left: 30px;"
														onClick='openUploadFilePanel("Add/Remove <s:text name='hiveAp.autoProvisioning.imageName.label'/>", "newImageFile");'>
													</s:if>
												</td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td><!-- configuration upload section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td width="15px"><s:checkbox
													name="dataSource.uploadConfig"
													value="%{dataSource.uploadConfig}"
													onclick="scriptChanged(this.checked);" /></td>
												<td class="labelT1"><label><s:text
													name="hiveAp.autoProvisioning.uploadConfig" /></label></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr id="includeTopologyInfoTr">
									<td><table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td style="padding: 5px 0 5px 0px;" width="15px"><s:checkbox
												name="dataSource.includeTopologyInfo" value="%{dataSource.includeTopologyInfo}"></s:checkbox>
											</td>
											<td class="labelT1"><span><s:text name="glasgow_12.hiveAp.insert.topology" /></span></td>
										</tr>
										<tr>
											<td style="padding: 5px 0 5px 30px;" colspan="10" align="left" class="noteInfo"><s:text name="glasgow_12.hiveAp.insert.topology.note" /></td>
										</tr>
									</table></td></tr>
									<tr>
										<td><!-- reboot section -->
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelH1" colspan="10">
												<label><s:text
													name="hiveAp.autoProvisioning.rebooting.label"></s:text></label>
												</td>
											</tr>
										</table>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td width="15px"><s:checkbox name="dataSource.rebooting"
													value="%{dataSource.rebooting}" /></td>
												<td class="labelT1"><label><s:text
													name="hiveAp.autoProvisioning.rebooting" /></label></td>
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
</s:form></div>
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var uploadFilePanel = null;
function createUploadFilePanel(width, height, title){
	var div = document.getElementById("uploadFilePanel");
	var iframe = document.getElementById("uploadFileFrame");
	iframe.width = width;
	iframe.height = height;
	uploadFilePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	uploadFilePanel.setHeader(title);
	uploadFilePanel.render();
	div.style.display="";
	uploadFilePanel.beforeHideEvent.subscribe(refreshImageList);
}
function refreshImageList(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("uploadFileFrame").style.display = "none";
	}
	fetchImageVersionInfo();
}
function openUploadFilePanel(title, doOperation){
	if(null == uploadFilePanel){
		createUploadFilePanel(555,600,title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("uploadFileFrame").style.display = "";
	}
	uploadFilePanel.show();
	var iframe = document.getElementById("uploadFileFrame");
	iframe.src ="<s:url value='hiveApFile.action' includeParams='none' />?operation="+doOperation;
}
var uploadFileIframeWindow;
</script>
<script type="text/javascript">
var BRANCH_ROLE_PRIMARY = <%=AhInterface.ROLE_PRIMARY%>;
var BRANCH_ROLE_BACKUP = <%=AhInterface.ROLE_BACKUP%>;
function enabledPseCheckBox(checked, ifId) {
	if (checked) {
		Get(ifId).disabled=false;
	} else {
		Get(ifId).disabled=true;
	}
}
function branchRouterEth0RoleChanged(value){
	if (value == BRANCH_ROLE_PRIMARY) {
		Get(formName + "_br100Usb_interfaceRole").value = BRANCH_ROLE_BACKUP;
	} else {
		Get(formName + "_br100Usb_interfaceRole").value = BRANCH_ROLE_PRIMARY;
	}
	branchRouterUSBChanged(Get(formName + "_br100Usb_interfaceRole").value);
}
function branchRouterUsbRoleChanged(value){
	if (value == BRANCH_ROLE_PRIMARY) {
		Get(formName + "_br100Eth0_interfaceRole").value = BRANCH_ROLE_BACKUP;
	} else {
		Get(formName + "_br100Eth0_interfaceRole").value = BRANCH_ROLE_PRIMARY;
	}
	branchRouterUSBChanged(value);
}
function branchRouterUSBChanged(value){
	var usbRoleValue = value;
	if(usbRoleValue == BRANCH_ROLE_PRIMARY){
		hm.util.hide("usbModemSettingSection");
	}else{
		hm.util.show("usbModemSettingSection");
	}
}
function hideShowPseSettings(isShow){
	if(isShow) {
		Get("pseSettingAllDiv").style.display='';
	} else {
		Get("pseSettingAllDiv").style.display='none';
	}
}
</script>


<script>!window.jQuery.ui && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value='verParam' />"></script>
<style type="text/css">
#apAutoClassifierTagContainer td.listHead {
    background-color: #FFFFFF;    
}

#apAutoClassifierTagContainer ul {    
 padding-left: 70px;
 }
 
#apAutoClassifierTagContainer td.defaultContainer input {    
    width: 100px;
}

#apAutoClassifierTagContainer div {
    float:left;
    width:33.3%;
 }

#apAutoClassifierTagContainer td.listHead{
border-bottom: 0 ;
}
</style>
<script>



$(document).ready(function()
{	
	var deviceTagInitValue1="<s:text name="dataSource.classificationTag1"/>";
	var deviceTagInitValue2="<s:text name="dataSource.classificationTag2"/>";
	var deviceTagInitValue3="<s:text name="dataSource.classificationTag3"/>";
	if(deviceTagInitValue1=="dataSource.classificationTag1")deviceTagInitValue1="None"
	if(deviceTagInitValue2=="dataSource.classificationTag2")deviceTagInitValue2="None"
	if(deviceTagInitValue3=="dataSource.classificationTag3")deviceTagInitValue3="None"
	var ct = $("#apAutoClassifierTagContainer").classifierTag(
				{
					key: 8,
					types:  [{key: 4, text: 'Device Tags'}, null, null],
					widgetWidth: {desc: 0},
					valueProps: null,
					itemEditable: false,
					describable: false,	
					needShowTagFields: true,
					deviceTagInitValue: {
						Tag: [deviceTagInitValue1, deviceTagInitValue2, deviceTagInitValue3]
					}					
				}); 
   
    $("#apAutoClassifierTagContainer").show();
  
    
}
);
</script>