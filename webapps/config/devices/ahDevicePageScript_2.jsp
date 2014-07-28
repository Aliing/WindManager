<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.wlan.RadioProfile"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@page import="com.ah.bo.hiveap.HiveAPVirtualConnection"%>

<%-- HiveAP new layout functions --%>
<script type="text/javascript">
var RADIO_MODE_ACCESS_ALL = <%=HiveAp.RADIO_MODE_ACCESS_ALL%>;
var RADIO_MODE_ACCESS_ONE = <%=HiveAp.RADIO_MODE_ACCESS_ONE%>;
var RADIO_MODE_BRIDGE = <%=HiveAp.RADIO_MODE_BRIDGE%>;
var RADIO_MODE_CUSTOMIZE = <%=HiveAp.RADIO_MODE_CUSTOMIZE%>;
var RADIO_MODE_ACCESS_DUAL = <%=HiveAp.RADIO_MODE_ACCESS_DUAL%>;

var USE_ETHERNET_BOTH = <%=HiveAp.USE_ETHERNET_BOTH%>;
var USE_ETHERNET_AGG0 = <%=HiveAp.USE_ETHERNET_AGG0%>;
var USE_ETHERNET_RED0 = <%=HiveAp.USE_ETHERNET_RED0%>;

var USE_STATIC_IP = <%=HiveAp.USE_STATIC_IP%>;
var USE_DHCP_FALLBACK = <%=HiveAp.USE_DHCP_FALLBACK%>;
var USE_DHCP_WITHOUTFALLBACK = <%=HiveAp.USE_DHCP_WITHOUTFALLBACK%>;

var USE_AP_HOSTNAME_AS_NAS_IDE = <%=HiveAp.USE_AP_HOSTNAME_AS_NAS_IDE%>;
var USE_CUSTOMIZED_NAS_IDE = <%=HiveAp.USE_CUSTOMIZED_NAS_IDE%>;

var ETH0_OPERATION_LABEL = '<s:text name="hiveAp.ethernet.eth0.mode" />';
var ETH0_BRIDGE_LABEL = '<s:text name="hiveAp.ethernet0.bridge.mode" />';
var ETH1_OPERATION_LABEL = '<s:text name="hiveAp.ethernet.eth1.mode" />';
var ETH1_BRIDGE_LABEL = '<s:text name="hiveAp.ethernet1.bridge.mode" />';

var ethernetSetups = new Array();
<s:iterator value="%{enumEthernetSetups}">
	ethernetSetups[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>

var ethOperationModes = new Array();
<s:iterator value="%{enumEthOperationMode}">
	ethOperationModes[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>

function wifiClientWanChanged(type){
	document.forms[formName].wifiClientWan.value = parseInt(type);
}

function radioModeTypeChanged(type){
	var ethSetupEl = document.getElementById(formName + "_dataSource_ethConfigType");
	var value = ethSetupEl.value;
	var eth0ModeEl = document.getElementById(formName + "_dataSource_eth0_operationMode");
	var modeValue0 = eth0ModeEl.value;
	var eth1ModeEl = document.getElementById(formName + "_dataSource_eth1_operationMode");
	var modeValue1 = eth1ModeEl.value;

	var eth0ModelLabelTd = document.getElementById("eth0ModeLabelTd");
	var eth1ModelLabelTd = document.getElementById("eth1ModeLabelTd");

	switch(parseInt(type)){
	case RADIO_MODE_ACCESS_ALL:
	case RADIO_MODE_ACCESS_ONE:
	case RADIO_MODE_CUSTOMIZE:
	case RADIO_MODE_ACCESS_DUAL:
		ethSetupEl.length = 0;
		addOption(ethSetupEl, ethernetSetups[USE_ETHERNET_BOTH],USE_ETHERNET_BOTH, value==USE_ETHERNET_BOTH);
		addOption(ethSetupEl, ethernetSetups[USE_ETHERNET_AGG0],USE_ETHERNET_AGG0, value==USE_ETHERNET_AGG0);
		addOption(ethSetupEl, ethernetSetups[USE_ETHERNET_RED0],USE_ETHERNET_RED0, value==USE_ETHERNET_RED0);

		eth0ModeEl.length = 0;
		addOption(eth0ModeEl, ethOperationModes[OPERATION_MODE_ACCESS],OPERATION_MODE_ACCESS, modeValue0==OPERATION_MODE_ACCESS);
		addOption(eth0ModeEl, ethOperationModes[OPERATION_MODE_BACKHAUL],OPERATION_MODE_BACKHAUL, modeValue0==OPERATION_MODE_BACKHAUL);
		addOption(eth0ModeEl, ethOperationModes[OPERATION_MODE_BRIDGE],OPERATION_MODE_BRIDGE, modeValue0==OPERATION_MODE_BRIDGE);
		//eth0ModeEl.value = OPERATION_MODE_BACKHAUL;

		eth1ModeEl.length = 0;
		addOption(eth1ModeEl, ethOperationModes[OPERATION_MODE_ACCESS],OPERATION_MODE_ACCESS, modeValue1==OPERATION_MODE_ACCESS);
		addOption(eth1ModeEl, ethOperationModes[OPERATION_MODE_BACKHAUL],OPERATION_MODE_BACKHAUL, modeValue1==OPERATION_MODE_BACKHAUL);
		addOption(eth1ModeEl, ethOperationModes[OPERATION_MODE_BRIDGE],OPERATION_MODE_BRIDGE, modeValue1==OPERATION_MODE_BRIDGE);
		//eth1ModeEl.value = OPERATION_MODE_BACKHAUL;

		eth0ModelLabelTd.innerHTML = ETH0_OPERATION_LABEL;
		eth1ModelLabelTd.innerHTML = ETH1_OPERATION_LABEL;
		break;
	case RADIO_MODE_BRIDGE:
		ethSetupEl.length = 0;
		addOption(ethSetupEl, ethernetSetups[USE_ETHERNET_BOTH],USE_ETHERNET_BOTH, value==USE_ETHERNET_BOTH);

		eth0ModeEl.length = 0;
		addOption(eth0ModeEl, ethOperationModes[OPERATION_MODE_ACCESS],OPERATION_MODE_ACCESS, modeValue0==OPERATION_MODE_ACCESS);
		addOption(eth0ModeEl, ethOperationModes[OPERATION_MODE_BRIDGE],OPERATION_MODE_BRIDGE, modeValue0==OPERATION_MODE_BRIDGE);

		eth1ModeEl.length = 0;
		addOption(eth1ModeEl, ethOperationModes[OPERATION_MODE_ACCESS],OPERATION_MODE_ACCESS, modeValue1==OPERATION_MODE_ACCESS);
		addOption(eth1ModeEl, ethOperationModes[OPERATION_MODE_BRIDGE],OPERATION_MODE_BRIDGE, modeValue1==OPERATION_MODE_BRIDGE);

		eth0ModelLabelTd.innerHTML = ETH0_BRIDGE_LABEL;
		eth1ModelLabelTd.innerHTML = ETH1_BRIDGE_LABEL;
		//reset bridge config setting checkbox
		eth0OperationModeChange(OPERATION_MODE_ACCESS);
		eth1OperationModeChange(OPERATION_MODE_ACCESS);
		break;
	}
	document.getElementById("radioConfigTr").style.display = type == RADIO_MODE_CUSTOMIZE?"":"none";
	document.getElementById("radioConfigBR").style.display = type == RADIO_MODE_CUSTOMIZE?"":"none";
	ethSetupChanged();

	// update ssid allocation
	var wifi0OperationMode = getWifi0OperationMode();
	var wifi1OperationMode = getWifi1OperationMode();
	operationModeChanged(wifi0OperationMode, 'wifi0');
	operationModeChanged(wifi1OperationMode, 'wifi1');

	if(parseInt(type) != RADIO_MODE_ACCESS_DUAL && parseInt(type) != RADIO_MODE_ACCESS_ONE){
		var enableEthBridge = document.getElementById(formName + "_dataSource_enableEthBridge");
		//enableEthBridge.checked = false;
		//clickEnableEthBridge(false);
		enableEthBridge.disabled = true;
	}else{
		document.getElementById(formName + "_dataSource_enableEthBridge").disabled = false;
	}
	if(parseInt(type) == RADIO_MODE_ACCESS_ONE){
		document.getElementById("radioConfigTypeTr2_note").style.display = "";
		document.getElementById("radioConfigTypeDual_note").style.display = "none";
	}else if(parseInt(type) == RADIO_MODE_ACCESS_DUAL){
		document.getElementById("radioConfigTypeTr2_note").style.display = "none";
		document.getElementById("radioConfigTypeDual_note").style.display = "";
	}else{
		document.getElementById("radioConfigTypeTr2_note").style.display = "none";
		document.getElementById("radioConfigTypeDual_note").style.display = "none";
	}
}

function ethSetupChanged(){
	// fixed Bug 15123
	if(typeof(wholenativeVlanEl0) == "undefined") {
	    wholenativeVlanEl0 = $("#"+formName + "_dataSource_eth0_multiNativeVlan").attr("value");
	}
	if(typeof(wholenativeVlanEl1) == "undefined") {
	    wholenativeVlanEl1 = $("#"+formName + "_dataSource_eth1_multiNativeVlan").attr("value");
	}
	if(typeof(wholenativeVlanElagg0) == "undefined") {
		wholenativeVlanElagg0 = $("#"+formName + "_dataSource_agg0_multiNativeVlan").attr("value");
	}
	if(typeof(wholenativeVlanElred0) == "undefined") {
		wholenativeVlanElred0 = $("#"+formName + "_dataSource_red0_multiNativeVlan").attr("value");
	}

	if(!isEth1Available()){//ag20, 110,120
		var ethSetupEl = document.getElementById(formName + "_dataSource_ethConfigType");
		ethSetupEl.value = USE_ETHERNET_BOTH; // reset to default value;
		hm.util.show("eth0ModeTr");
		hm.util.hide("eth1ModeTr");
		hm.util.hide("agg0ModeTr");
		hm.util.hide("red0ModeTr");

		hm.util.hide("eth1ModeTr");
		hm.util.hide("eth1MacLearningTr");

		hm.util.hide('red0Row');
		hm.util.hide('red0MacLearningTr');

		hm.util.hide('agg0Row');
		hm.util.hide('agg0MacLearningTr');

		hm.util.hide('eth0RoleHeader');
		hm.util.hide('eth0RoleColumn');
		hm.util.hide('eth1RoleColumn');

		document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
		document.getElementById(formName + "_dataSource_eth1_adminState").disabled = false;
	}else{
		var ethSetupEl = document.getElementById(formName + "_dataSource_ethConfigType");
		var value = ethSetupEl.value;
		switch(parseInt(value)){
		case USE_ETHERNET_BOTH:
			hm.util.hide("agg0ModeTr");
			hm.util.hide("red0ModeTr");
			hm.util.show("eth0ModeTr");
			hm.util.show("eth1ModeTr");

			hm.util.show('multipleVlanSection');

			hm.util.hide('red0Row');
			hm.util.hide('red0MacLearningTr');

			hm.util.hide('agg0Row');
			hm.util.hide('agg0MacLearningTr');

			hm.util.hide('eth0RoleHeader');
			hm.util.hide('eth0RoleColumn');
			hm.util.hide('eth1RoleColumn');

			document.getElementById(formName + "_dataSource_eth0_adminState").disabled = false;
			document.getElementById(formName + "_dataSource_eth1_adminState").disabled = false;

			// fixed Bug 15123
			var eth0Select = document.getElementById(formName + "_dataSource_eth0_operationMode");
			eth0OperationModeChange(eth0Select.options[eth0Select.selectedIndex].value);
			var eth1Select = document.getElementById(formName + "_dataSource_eth1_operationMode");
			eth1OperationModeChange(eth1Select.options[eth1Select.selectedIndex].value);

			break;
		case USE_ETHERNET_AGG0:
			hm.util.show("agg0ModeTr");

			hm.util.hide("red0ModeTr");
			hm.util.hide('red0MacLearningTr');

			hm.util.hide("eth0ModeTr");
			//document.getElementById("eth0UseDefaultSettingsCbx").checked = true;
			hm.util.hide("eth0MacLearningTr");

			hm.util.hide("eth1ModeTr");
			hm.util.hide("eth1MacLearningTr");

			hm.util.hide('red0Row');
			hm.util.show('agg0Row');

			hm.util.hide('eth0RoleHeader');
			hm.util.hide('eth0RoleColumn');
			hm.util.hide('eth1RoleColumn');

			hm.util.hide('multipleVlanSection');

			document.getElementById(formName + "_dataSource_eth0_adminState").value = IF_ADMIN_STATUS_UP;
			document.getElementById(formName + "_dataSource_eth1_adminState").value = IF_ADMIN_STATUS_UP;
			document.getElementById(formName + "_dataSource_eth0_adminState").disabled = true;
			document.getElementById(formName + "_dataSource_eth1_adminState").disabled = true;

			// fixed Bug 15123
			var eth0Select = document.getElementById(formName + "_dataSource_eth0_operationMode");
			eth0OperationModeChange(eth0Select.options[eth0Select.selectedIndex].value);
			var eth1Select = document.getElementById(formName + "_dataSource_eth1_operationMode");
			eth1OperationModeChange(eth1Select.options[eth1Select.selectedIndex].value);
			
			var agg0Select = document.getElementById(formName + "_dataSource_agg0_operationMode");
			agg0OperationModeChange(agg0Select.options[agg0Select.selectedIndex].value);

			break;
		case USE_ETHERNET_RED0:
			hm.util.hide("agg0ModeTr");
			hm.util.hide('agg0MacLearningTr');

			hm.util.show("red0ModeTr");

			hm.util.hide("eth0ModeTr");
			hm.util.hide("eth0MacLearningTr");

			hm.util.hide("eth1ModeTr");
			hm.util.hide("eth1MacLearningTr");

			hm.util.show('red0Row');
			hm.util.hide('agg0Row');

			hm.util.show('eth0RoleHeader');
			hm.util.show('eth0RoleColumn');
			hm.util.show('eth1RoleColumn');

			hm.util.hide('multipleVlanSection');

			document.getElementById(formName + "_dataSource_eth0_adminState").value = IF_ADMIN_STATUS_UP;
			document.getElementById(formName + "_dataSource_eth1_adminState").value = IF_ADMIN_STATUS_UP;
			document.getElementById(formName + "_dataSource_eth0_adminState").disabled = true;
			document.getElementById(formName + "_dataSource_eth1_adminState").disabled = true;

			// fixed Bug 15123
			var eth0Select = document.getElementById(formName + "_dataSource_eth0_operationMode");
			eth0OperationModeChange(eth0Select.options[eth0Select.selectedIndex].value);
			var eth1Select = document.getElementById(formName + "_dataSource_eth1_operationMode");
			eth1OperationModeChange(eth1Select.options[eth1Select.selectedIndex].value);
			
			var red0Select = document.getElementById(formName + "_dataSource_red0_operationMode");
			red0OperationModeChange(red0Select.options[red0Select.selectedIndex].value);

			break;
		}
	}
}

function addOption(selector, text, value, isSelected){
	var option = new Option(text,value);
	option.selected = isSelected;
	try{// dom
		selector.add(option, null);
	}catch(e){// ie
		selector.add(option);
	}
}

<%--
function eth0MacLearningEnabled(checked){
	document.getElementById("eth0MacLearningEnabledTr").style.display = checked?"":"none";
}

function eth1MacLearningEnabled(checked){
	document.getElementById("eth1MacLearningEnabledTr").style.display = checked?"":"none";
}

function agg0MacLearningEnabled(checked){
	document.getElementById("agg0MacLearningEnabledTr").style.display = checked?"":"none";
}

function red0MacLearningEnabled(checked){
	document.getElementById("red0MacLearningEnabledTr").style.display = checked?"":"none";
}
--%>
function eth0OperationModeChange(operationValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_eth0_multiNativeVlan");
	// fixed Bug 15123
	var ethSetupType = document.getElementById(formName + "_dataSource_ethConfigType").value;
	var isHidden = false;
	if(ethSetupType != USE_ETHERNET_BOTH) {isHidden=true;}
	if(operationValue == OPERATION_MODE_BACKHAUL){
		nativeVlanEl.disabled = false;
		nativeVlanEl.value = wholenativeVlanEl0;
		hm.util.hide("eth0MacLearningTr");
	}else if(operationValue == OPERATION_MODE_BRIDGE){
		nativeVlanEl.disabled = false;
		nativeVlanEl.value = wholenativeVlanEl0;
		hm.util.show("macLearningSettings");
		hm.util.show("eth0MacLearningTr");
	}else{
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.show("macLearningSettings");
		hm.util.show("eth0MacLearningTr");
	}
	
	if (isHidden) {
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.hide("eth0MacLearningTr");
	}
}

function eth1OperationModeChange(operationValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_eth1_multiNativeVlan");
	// fixed Bug 15123
	var ethSetupType = document.getElementById(formName + "_dataSource_ethConfigType").value;
	var isHidden = false;
	//fix bug 28245
	if(ethSetupType != USE_ETHERNET_BOTH) {isHidden=true;}
	if(operationValue == OPERATION_MODE_BACKHAUL){
		nativeVlanEl.disabled = false;
		nativeVlanEl.value = wholenativeVlanEl1;
		hm.util.hide("eth1MacLearningTr");
		//hm.util.show("multipleVlanSection");
	}else if(operationValue == OPERATION_MODE_BRIDGE){
		nativeVlanEl.disabled = false;
		nativeVlanEl.value = wholenativeVlanEl1;
		hm.util.show("macLearningSettings");
		hm.util.show("eth1MacLearningTr");
		//hm.util.hide("multipleVlanSection");
	}else{
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.show("macLearningSettings");
		hm.util.show("eth1MacLearningTr");
		//hm.util.hide("multipleVlanSection");
	}
	
	if(isHidden){
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.hide("eth1MacLearningTr");
		//hm.util.show("multipleVlanSection");
	}
}

function agg0OperationModeChange(operationValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_agg0_multiNativeVlan");
	// fixed Bug 15123
	var ethSetupType = document.getElementById(formName + "_dataSource_ethConfigType").value;
	var isHidden = false;
	if(ethSetupType != USE_ETHERNET_AGG0) {isHidden=true;}
	if(operationValue == OPERATION_MODE_BACKHAUL){
		nativeVlanEl.value=wholenativeVlanElagg0;
		nativeVlanEl.disabled = false;
		hm.util.hide('agg0MacLearningTr');
	}else if(operationValue == OPERATION_MODE_BRIDGE){
		nativeVlanEl.value=wholenativeVlanElagg0;
		nativeVlanEl.disabled = false;
		hm.util.show("macLearningSettings");
		hm.util.show("agg0MacLearningTr");
		hm.util.hide("multipleVlanSection");
	}else{
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.show("macLearningSettings");
		hm.util.show('agg0MacLearningTr');
	}
	
	if (isHidden){
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.hide('agg0MacLearningTr');
	}
}

function red0OperationModeChange(operationValue){
	var nativeVlanEl = document.getElementById(formName + "_dataSource_red0_multiNativeVlan");
	// fixed Bug 15123
	var ethSetupType = document.getElementById(formName + "_dataSource_ethConfigType").value;
	var isHidden = false;
	if(ethSetupType != USE_ETHERNET_RED0) {isHidden=true;}
	if(operationValue == OPERATION_MODE_BACKHAUL){
		nativeVlanEl.value=wholenativeVlanElred0;
		nativeVlanEl.disabled = false;
		hm.util.hide('red0MacLearningTr');
	}else if(operationValue == OPERATION_MODE_BRIDGE){
		nativeVlanEl.value=wholenativeVlanElred0;
		nativeVlanEl.disabled = false;
		hm.util.show("macLearningSettings");
		hm.util.show("red0MacLearningTr");
		hm.util.hide("multipleVlanSection");
	}else{
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.show("macLearningSettings");
		hm.util.show('red0MacLearningTr');
	}
	
	if (isHidden){
		nativeVlanEl.value='';
		nativeVlanEl.disabled = true;
		hm.util.hide('red0MacLearningTr');
	}
}

function hideSimpleCreateSection(createSectionId){
	if(createSectionId){
		Get(createSectionId).style.display = "none";
	}
}

function showSimpleCreateSection(createSectionId, focusEl){
	if(createSectionId){
		Get(createSectionId).style.display = "";
		if(focusEl){
			focusEl.focus();
		}
	}
}

function newSimpleRadioProfile(wifiName){
	var createSectionId, focusEl;
	// create a new radio profile
	if(wifiName == 'wifi0'){
		createSectionId = "wifi0RadioProfileCreateSection";
		focusEl = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioName");
	}else if(wifiName == 'wifi1'){
		createSectionId = "wifi1RadioProfileCreateSection";
		focusEl = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioName");
	}
	showSimpleCreateSection(createSectionId, focusEl);
}

function validateWifi0Creation(operation){
	if("createWifi0RadioProfile" == operation){
		var inputElement = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.radioProfile.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
	}
	return true;
}

function validateWifi1Creation(operation){
	if("createWifi1RadioProfile" == operation){
		var inputElement = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.radioProfile.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
	}
	return true;
}

function changeWifi0RadioMode(value){
	if(RADIO_MODE_NA == value || RADIO_MODE_NG == value){
		var channelWidths = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40A+">40-MHz above</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40B+">40-MHz below</option>");
		document.getElementById("channelWidthTr0").style.display = "";
	}else if(RADIO_MODE_AC == value){
		var channelWidths = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40+">40 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_80+">80 MHz</option>");
		document.getElementById("channelWidthTr0").style.display = "";
	}else{
		document.getElementById("channelWidthTr0").style.display = "none";
	}
//	if (RADIO_MODE_NA == value) {
//		document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth").value=CHANNEL_WIDTH_40A;
//	} else {
//		document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth").value=CHANNEL_WIDTH_20;
//	}
}

function changeWifi1RadioMode(value){
	if(RADIO_MODE_NA == value || RADIO_MODE_NG == value){
		var channelWidths = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40A+">40-MHz above</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40B+">40-MHz below</option>");
		document.getElementById("channelWidthTr1").style.display = "";
	}else if(RADIO_MODE_AC == value){
		var channelWidths = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40+">40 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_80+">80 MHz</option>");
		document.getElementById("channelWidthTr1").style.display = "";
	}else{
		document.getElementById("channelWidthTr1").style.display = "none";
	}
//	if (RADIO_MODE_NA == value) {
//		document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth").value=CHANNEL_WIDTH_40A;
//	} else {
//		document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth").value=CHANNEL_WIDTH_20;
//	}
}

function macOuiEth0ListChanged(data){
	macOuiListChanged("eth0",data);
}

function macOuiEth1ListChanged(data){
	macOuiListChanged("eth1",data);
}

function macOuiAgg0ListChanged(data){
	macOuiListChanged("agg0",data);
}

function macOuiRed0ListChanged(data){
	macOuiListChanged("red0",data);
}

function macOuiListChanged(interfaceName,data){
	var eth0List = document.getElementById("leftOptions_eth0Maces");
	var eth1List = document.getElementById("leftOptions_eth1Maces");
	var agg0List = document.getElementById("leftOptions_agg0Maces");
	var red0List = document.getElementById("leftOptions_red0Maces");
	if("eth0"!=interfaceName){
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(eth0List, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(eth0List, data.items);
		}
	}
	if("eth1"!=interfaceName){
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(eth1List, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(eth1List, data.items);
		}
	}
	if("agg0"!=interfaceName){
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(agg0List, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(agg0List, data.items);
		}
	}
	if("red0"!=interfaceName){
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(red0List, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(red0List, data.items);
		}
	}
}

<%-- ethernet cwp --%>
var isEthCwpRadiusClient = <s:property value="configureEthCwpRadiusClient" />;
var isEthCwpDefaultRegUserProfile = <s:property value="configureEthCwpDefaultRegUserProfile" />;
var isEthCwpDefaultAuthUserProfile = <s:property value="configureEthCwpDefaultAuthUserProfile" />;
function updateEthCwpSettingLayout(){
	var _dataSource_ethCwpEnableEthCwp = document.getElementById(formName+"_dataSource_ethCwpEnableEthCwp");
	var _dataSource_ethCwpEnableMacAuth = document.getElementById(formName+"_dataSource_ethCwpEnableMacAuth");
	var cwpEl = document.getElementById(formName + "_cwpProfile");
	var ethCwpProfileTd = document.getElementById("ethCwpProfileTd");
	var ethCwpMacAuthTr = document.getElementById("ethCwpMacAuthTr");
	var ethCwpRadiusContentSettingTr = document.getElementById("ethCwpRadiusContentSettingTr");
	var ethCwpRadiusSelectionTr = document.getElementById("ethCwpRadiusSelectionTr");
	var ethCwpUserProfileSelectionTr = document.getElementById("ethCwpUserProfileSelectionTr");
	var ethCwpUserProfileRegTr = document.getElementById("ethCwpUserProfileRegTr");
	var ethCwpUserProfileAuthTr = document.getElementById("ethCwpUserProfileAuthTr");
	var ethCwpUserProfileAuthLabelTr = document.getElementById("ethCwpUserProfileAuthLabelTr");
	var ethCwpUserProfileDefaultLabelTr = document.getElementById("ethCwpUserProfileDefaultLabelTr");
	var ethCwpUserProfilesTr = document.getElementById("ethCwpUserProfilesTr");
	var ethCwpUserProifleParamsTr = document.getElementById("ethCwpUserProifleParamsTr");
	ethCwpProfileTd.style.display =  _dataSource_ethCwpEnableEthCwp.checked? "" : "none";
	<s:if test="%{isMacAuthDependOnCWP}">
		ethCwpMacAuthTr.style.display =  _dataSource_ethCwpEnableEthCwp.checked? "" : "none";
	</s:if>
	ethCwpRadiusContentSettingTr.style.display = (_dataSource_ethCwpEnableEthCwp.checked&&(cwpEl.value >0 || _dataSource_ethCwpEnableMacAuth.checked))? "" : "none";
	ethCwpUserProfileSelectionTr.style.display = _dataSource_ethCwpEnableEthCwp.checked? "" : "none";
	ethCwpRadiusSelectionTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpRadiusClient)?"":"none";
	ethCwpUserProfilesTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpRadiusClient)?"":"none";
	ethCwpUserProifleParamsTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpRadiusClient)?"":"none";
	ethCwpUserProfileRegTr.style.display = isEthCwpDefaultRegUserProfile? "" : "none";
	ethCwpUserProfileAuthTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpDefaultAuthUserProfile)?"":"none";
	ethCwpUserProfileAuthLabelTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpRadiusClient)?"":"none";
	ethCwpUserProfileDefaultLabelTr.style.display = (_dataSource_ethCwpEnableMacAuth.checked||isEthCwpRadiusClient)?"none":"";
}

function changeEnableEthCwp(checked){
	if(checked){
		<%-- avoid automatically bug 11808
		// change eth0 operation to access automatically
		if(!isEthAccessPortAvailable()){
			var eth0Operation = document.getElementById(formName + "_dataSource_eth0_operationMode");
			eth0Operation.value = OPERATION_MODE_ACCESS
			eth0OperationModeChange(OPERATION_MODE_ACCESS);
		}
		--%>
		showNetworkSettingsContent();
	}
	updateEthCwpSettingLayout();
}

function cwpProfileChanged(){
	var cwpEl = document.getElementById(formName + "_cwpProfile");
	if(cwpEl.value <=0 ){
		isEthCwpRadiusClient = false;
		isEthCwpDefaultRegUserProfile = false;
		isEthCwpDefaultAuthUserProfile = false;
		updateEthCwpSettingLayout();
	}else{
		var cwpId = cwpEl.value;
		url = "<s:url action='hiveAp' includeParams='none' />?operation=requestCwp" + "&cwpProfile=" + cwpId+"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : getCwpInfo}, null);
	}
}

function getCwpInfo(o){
	eval("var details = " + o.responseText);
	isEthCwpRadiusClient = details.isEthCwpRadiusClient;
	isEthCwpDefaultRegUserProfile = details.isEthCwpDefaultRegUserProfile;
	isEthCwpDefaultAuthUserProfile = details.isEthCwpDefaultAuthUserProfile;
	updateEthCwpSettingLayout();
}

function changeEnableEthMacAuth(checked){
	updateEthCwpSettingLayout();
}

function changeEnableEthLimitUserprofile(checked){
	var el = document.getElementById("ethLimitUserprofileContent");
	if(el){
		el.style.display = checked ? "" : "none";
	}
}

function ethDenyActionChanged(action){
	var el = document.getElementById(formName + "_dataSource_ethCwpActiveTime");
	if(action == DENY_ACTION_BAN){
		el.disabled = false;
	}else{
		el.disabled = true;
	}
}

function selectVirtualAction(value) {
	if (value == <%=HiveAPVirtualConnection.ACTION_DROP%>) {
		document.getElementById(formName + "_virtualConnectInterface_out").disabled = true;
		document.getElementById(formName + "_virtualConnectRxMac").disabled = true;
	} else {
		document.getElementById(formName + "_virtualConnectInterface_out").disabled = false;
		<%--document.getElementById(formName + "_virtualConnectRxMac").disabled = false; fix bug 25058--%>
		var out = document.getElementById(formName + "_virtualConnectInterface_out").value;
		selectOutInterface(out);
	}
}

function selectInInterface(value) {
	if (value == <%=HiveAPVirtualConnection.INTERFACE_WIFI0%> || value == <%=HiveAPVirtualConnection.INTERFACE_WIFI1%>) {
		document.getElementById(formName + "_virtualConnectTxMac").disabled = false;
	} else {
		document.getElementById(formName + "_virtualConnectTxMac").disabled = true;
	}
}

function selectOutInterface(value) {
	if (value == <%=HiveAPVirtualConnection.INTERFACE_WIFI0%> || value == <%=HiveAPVirtualConnection.INTERFACE_WIFI1%>) {
		document.getElementById(formName + "_virtualConnectRxMac").disabled = false;
	} else {
		document.getElementById(formName + "_virtualConnectRxMac").disabled = true;
	}
}

function clickEnableEthBridge(checked){
	var ethConfigType = document.getElementById(formName + "_dataSource_ethConfigType").value;
	if(checked){
		document.getElementById("enableEthBridge_note").style.display="";
		if(!isEth1Available()){
			document.getElementById(formName + "_dataSource_eth0_operationMode").value=OPERATION_MODE_ACCESS;
			eth0OperationModeChange(OPERATION_MODE_ACCESS);
			hm.util.hide("eth1MacLearningTr");
			return;
		}
		if(ethConfigType == USE_ETHERNET_BOTH){
			document.getElementById(formName + "_dataSource_eth0_operationMode").value=OPERATION_MODE_ACCESS;
			document.getElementById(formName + "_dataSource_eth1_operationMode").value=OPERATION_MODE_ACCESS;
			eth0OperationModeChange(OPERATION_MODE_ACCESS);
			eth1OperationModeChange(OPERATION_MODE_ACCESS);
		}else if(ethConfigType == USE_ETHERNET_AGG0){
			document.getElementById(formName + "_dataSource_agg0_operationMode").value=OPERATION_MODE_ACCESS;
			agg0OperationModeChange(OPERATION_MODE_ACCESS);
		}else if(ethConfigType == USE_ETHERNET_RED0){
			document.getElementById(formName + "_dataSource_red0_operationMode").value=OPERATION_MODE_ACCESS;
			red0OperationModeChange(OPERATION_MODE_ACCESS);
		}
	}else{
		document.getElementById("enableEthBridge_note").style.display="none";
		if(!isEth1Available()){
			document.getElementById(formName + "_dataSource_eth0_operationMode").value=OPERATION_MODE_BACKHAUL;
			eth0OperationModeChange(OPERATION_MODE_BACKHAUL);
			hm.util.hide("eth1MacLearningTr");
			return;
		}
		if(ethConfigType == USE_ETHERNET_BOTH){
			document.getElementById(formName + "_dataSource_eth0_operationMode").value=OPERATION_MODE_BACKHAUL;
			document.getElementById(formName + "_dataSource_eth1_operationMode").value=OPERATION_MODE_BACKHAUL;
			eth0OperationModeChange(OPERATION_MODE_BACKHAUL);
			eth1OperationModeChange(OPERATION_MODE_BACKHAUL);
		}else if(ethConfigType == USE_ETHERNET_AGG0){
			document.getElementById(formName + "_dataSource_agg0_operationMode").value=OPERATION_MODE_BACKHAUL;
			agg0OperationModeChange(OPERATION_MODE_BACKHAUL);
		}else if(ethConfigType == USE_ETHERNET_RED0){
			document.getElementById(formName + "_dataSource_red0_operationMode").value=OPERATION_MODE_BACKHAUL;
			red0OperationModeChange(OPERATION_MODE_BACKHAUL);
		}
	}
}

// vpn gateway function
function enableVRRPChange(checked){
	if(checked){
		document.getElementById("VPPRSetting").style.display = "";
	}else{
		document.getElementById("VPPRSetting").style.display = "none";
		document.getElementById(formName + "_secondVPNGateway").value = -1;
		document.getElementById(formName + "_dataSource_secondVPNGateway_eth0Interface_ipAndNetmask").value = null;
		document.getElementById(formName + "_dataSource_secondVPNGateway_eth1Interface_ipAndNetmask").value = null;
		document.getElementById(formName + "_vpnGatewayVirtualWanIp").value = null;
		document.getElementById(formName + "_vpnGatewayVirtualLanIp").value = null;
		document.getElementById(formName + "_vpnGatewayPreemptEnable").checked = false;
	}
}

var hiveApModelOpts = new Array();
<s:iterator value = "%{apModel}">
	hiveApModelOpts[<s:property value="key" />] = '<s:property value="value" />';
</s:iterator>

function showPMTUDBr(){
	hm.util.show('enableOverrideBrPMTUDTr');
}

function deviceTypeChanged(value){

	var selectApModel = document.getElementById(formName + "_dataSource_hiveApModel").value;
	deviceModeChange(selectApModel, value);
	return;
}

function deviceTypeChangedWarning(){
	showWarnDialog('<s:text name ="warning.hiveAp.deviceType.changed" />');
}

function branchRouterUsbChangedWarning(value){
	if(previousUsbRole != value){
		showWarnDialog('<s:text name ="warning.hiveAp.branchRouterusb.role.changed" />');
	}
}

function secondVPNGatewayChanged(value){
	var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=secondVPNGatewayChanged" +
		"&secondVPNGateway="+value + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : processSecondGatewayInf, failure : processSecondGatewayFailed}, null);
}

var processSecondGatewayInf = function(o){
	eval("var details = " + o.responseText);

	document.getElementById(formName + "_dataSource_secondVPNGateway_eth0Interface_ipAndNetmask").value = details.wanIp;
	document.getElementById(formName + "_dataSource_secondVPNGateway_eth1Interface_ipAndNetmask").value = details.lanIp;
}

var processSecondGatewayFailed = function(o) {
	document.getElementById(formName + "_dataSource_secondVPNGateway_eth0Interface_ipAndNetmask").value = null;
	document.getElementById(formName + "_dataSource_secondVPNGateway_eth1Interface_ipAndNetmask").value = null;
}

function validateVpnGatewaySetting(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType != DEVICE_TYPE_VPN_GATEWAY && deviceType != DEVICE_TYPE_BR_VPN){
		return true;
	}
	if(operation == 'create2' || operation == 'update2'){
		var wanIpElement = document.getElementById(formName + "_wanInterface_ipAndNetmask");
		var wanGatewayElement = document.getElementById(formName + "_wanInterface_gateway");
		var lanIpElement = document.getElementById(formName + "_lanInterface_ipAndNetmask");
		if(wanIpElement && wanIpElement.value.length == 0){
			hm.util.reportFieldError(wanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
            wanIpElement.focus();
            return false;
		}
		if(wanIpElement && wanIpElement.value.indexOf("/") == -1){
			hm.util.reportFieldError(wanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
            wanIpElement.focus();
            return false;
		}
		var ipStr = wanIpElement.value.substring(0, wanIpElement.value.indexOf("/"));
		var maskInt = wanIpElement.value.substring(wanIpElement.value.indexOf("/")+1);
		if(maskInt && maskInt > 32){
			hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			wanIpElement.focus();
			return false;
		}
		var maskStr = hm.util.intToStringNetMask(maskInt);
		if (ipStr && ! hm.util.validateIpAddress(ipStr)) {
			hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
			wanIpElement.focus();
			return false;
		}
		if(maskStr && ! hm.util.validateMask(maskStr)){
			hm.util.reportFieldError(wanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
			wanIpElement.focus();
			return false;
		}
		//check wan gateway
		if (wanGatewayElement && wanGatewayElement.value.length != 0) {
			if (! hm.util.validateIpAddress(wanGatewayElement.value)) {
				hm.util.reportFieldError(wanGatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.gateway" /></s:param></s:text>');
				wanGatewayElement.focus();
				return false;
			}
			//check if they are in the same subnet
			var message = hm.util.validateIpSubnet(ipStr, '<s:text name="hiveAp.ipAddress" />', wanGatewayElement.value, '<s:text name="hiveAp.gateway" />', maskStr);
			if(null != message){
				hm.util.reportFieldError(wanIpElement, message);
				wanGatewayElement.focus();
				return false;
			}
		}

		//lan interface check

		//if(lanIpElement.value.length == 0){
		//	hm.util.reportFieldError(lanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.ipAddress" /></s:param></s:text>');
        //    lanIpElement.focus();
        //    return false;
		//}
		if(lanIpElement && lanIpElement.value.length > 0){
			if(lanIpElement.value.indexOf("/") == -1){
				hm.util.reportFieldError(lanIpElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
	            lanIpElement.focus();
	            return false;
			}
			var lanipStr = lanIpElement.value.substring(0, lanIpElement.value.indexOf("/"));
			var lanmaskInt = lanIpElement.value.substring(lanIpElement.value.indexOf("/")+1);
			if(lanmaskInt > 32){
				hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
				lanIpElement.focus();
				return false;
			}
			var lanmaskStr = hm.util.intToStringNetMask(lanmaskInt);
			if (! hm.util.validateIpAddress(lanipStr)) {
				hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.ipAddress.cvg" /></s:param></s:text>');
				lanIpElement.focus();
				return false;
			}
			if(! hm.util.validateMask(lanmaskStr)){
				hm.util.reportFieldError(lanIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.netmask" /></s:param></s:text>');
				lanIpElement.focus();
				return false;
			}

			var ipStr = wanIpElement.value.substring(0, wanIpElement.value.indexOf("/"));
			var maskInt = wanIpElement.value.substring(wanIpElement.value.indexOf("/")+1);
			var maskStr = hm.util.intToStringNetMask(maskInt);

			//check if they are in the same subnet
			var message = hm.util.validateIpSubnet(ipStr, 'WAN IP', lanipStr, 'LAN IP', lanmaskStr);
			if(null == message || "" == message){
				hm.util.reportFieldError(wanIpElement, '<s:text name="error.hiveap.cvg.wanAndLan.samenetwork" />');
				wanGatewayElement.focus();
				return false;
			}

			message = hm.util.validateIpSubnet(lanipStr, 'LAN IP', ipStr, 'WAN IP', maskStr);
			if(null == message || "" == message){
				hm.util.reportFieldError(lanIpElement, '<s:text name="error.hiveap.cvg.wanAndLan.samenetwork" />');
				lanIpElement.focus();
				return false;
			}
		}

		//vrrp second vpn gateway checked
		/**
		if(document.getElementById(formName + "_vpnGatewayVrrpEnable").checked){
			var secGatewayEle = document.getElementById(formName + "_secondVPNGateway");
			if(secGatewayEle.value < 0){
				hm.util.reportFieldError(secGatewayEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.vpnGateway.secondary.gateway" /></s:param></s:text>');
				secGatewayEle.focus();
				return false;
			}
		}

		<s:if test="%{dataSource.secondVpnGateway}">
			return true;
		</s:if>
		<s:else>
			var virtualWanElement = document.getElementById(formName + "_vpnGatewayVirtualWanIp");
			var virtualLanElement = document.getElementById(formName + "_vpnGatewayVirtualLanIp");
			if(virtualWanElement != null && virtualWanElement.value.length > 0){
				if (! hm.util.validateIpAddress(virtualWanElement.value)) {
					hm.util.reportFieldError(virtualWanElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.vpnGateway.virtual.wanIp" /></s:param></s:text>');
					virtualWanElement.focus();
					return false;
				}
				var message = hm.util.validateIpSubnet(virtualWanElement.value, '<s:text name="hiveAp.vpnGateway.virtual.wanIp" />',
						ipStr, '<s:text name="hiveAp.ipAddress" />', maskStr);
				if(null != message){
					hm.util.reportFieldError(virtualWanElement, message);
					virtualWanElement.focus();
					return false;
				}
				var secondGatewayWanElement = document.getElementById(formName + "_dataSource_secondVPNGateway_eth0Interface_ipAndNetmask");
				var secondGatewayWanIp = secondGatewayWanElement.value.substring(0, secondGatewayWanElement.value.indexOf("/"));
				var secondGatewayWanMask = secondGatewayWanElement.value.substring(secondGatewayWanElement.value.indexOf("/")+1);
				message = hm.util.validateIpSubnet(virtualWanElement.value, '<s:text name="hiveAp.vpnGateway.virtual.wanIp" />',
						secondGatewayWanIp, '<s:text name="hiveAp.vpnGateway.secondary.wanIp" />', secondGatewayWanMask);
				if(null != message){
					hm.util.reportFieldError(virtualWanElement, message);
					virtualWanElement.focus();
					return false;
				}
			}
			if(virtualLanElement != null && virtualLanElement.value.length > 0){
				if (! hm.util.validateIpAddress(virtualLanElement.value)) {
					hm.util.reportFieldError(virtualLanElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.vpnGateway.virtual.wanIp" /></s:param></s:text>');
					virtualLanElement.focus();
					return false;
				}
				var message = hm.util.validateIpSubnet(virtualLanElement.value, '<s:text name="hiveAp.vpnGateway.virtual.lanIp" />',
						lanipStr, '<s:text name="hiveAp.ipAddress" />', lanmaskStr);
				if(null != message){
					hm.util.reportFieldError(virtualLanElement, message);
					virtualLanElement.focus();
					return false;
				}
				var secondGatewayLanElement = document.getElementById(formName + "_dataSource_secondVPNGateway_eth1Interface_ipAndNetmask");
				var secondGatewayLanIp = secondGatewayLanElement.value.substring(0, secondGatewayLanElement.value.indexOf("/"));
				var secondGatewayLanMask = secondGatewayLanElement.value.substring(secondGatewayLanElement.value.indexOf("/")+1);
				message = hm.util.validateIpSubnet(virtualLanElement.value, '<s:text name="hiveAp.vpnGateway.virtual.wanIp" />',
						secondGatewayLanIp, '<s:text name="hiveAp.vpnGateway.secondary.wanIp" />', secondGatewayLanMask);
				if(null != message){
					hm.util.reportFieldError(virtualLanElement, message);
					virtualLanElement.focus();
					return false;
				}
			}
		</s:else>
		**/
		var displayErrorObj = document.getElementById("checkAllRoute");
		var tbl = document.getElementById("tbCVGIpRoute_id");
		var rows =tbl.getElementsByTagName("tr");
		if(rows){
			for(var i=0; i<rows.length; i++) {
				if(!(validateSRGatewayByEth0(rows[i].cells[3].innerHTML,displayErrorObj,i) == 0 || validateSRGatewayByEth1(rows[i].cells[3].innerHTML,displayErrorObj,i) == 0)){
					if(validateSRGatewayByEth0(rows[i].cells[3].innerHTML,displayErrorObj,i) == 2 || validateSRGatewayByEth1(rows[i].cells[3].innerHTML,displayErrorObj,i) == 2){
						hm.util.reportFieldError(displayErrorObj, '<s:text name="error.hiveap.cvg.static.route.gateway" />'+' in '+(i+1)+' row.' );
					}
					hm.util.show('staticRoutes')
					return false;
				}
			}
		}
	}
	return true;
}

// end vpn gateway function

//br100 function

function branchRouterVrrpEnableChanged(checked){
	if(checked){
		document.getElementById("brRouterVrrpSetting").style.display="";
	}else{
		document.getElementById("brRouterVrrpSetting").style.display="none";
	}
}

function brRouterEth0DhcpChanged(checked){
	if(checked){
		document.getElementById("brRouterEth0DhcpSettings").style.display="none";
	}else{
		document.getElementById("brRouterEth0DhcpSettings").style.display="";
	}
}

function validateBrRouterSetting(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if(deviceType != DEVICE_TYPE_BRANCH_ROUTER){
		return true;
	}
	if(operation == 'create2' || operation == 'update2'){
		if(document.getElementById(formName + "_branchRouterVrrpEnable") && document.getElementById(formName + "_branchRouterVrrpEnable").checked){
			var vrrpIdEle = document.getElementById(formName + "_vrrpId");
			var virtualWanIpEle = document.getElementById(formName + "_branchRouterVirtualWanIp");
			var virtualLanIpEle = document.getElementById(formName + "_branchRouterVirtualLanIp");
			var vrrpPriorityEle = document.getElementById(formName + "_vrrpPriority");
			var vrrpDelayEle = document.getElementById(formName + "_dataSource_vrrpDelay");

			//required field check
			if(vrrpIdEle.value.length == 0){
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpIdEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.brRouter.vrrp.vrid" /></s:param></s:text>');
	            vrrpIdEle.focus();
	            return false;
			}
			if(virtualWanIpEle.value.length == 0){
				showNetworkSettingsContent();
				hm.util.reportFieldError(virtualWanIpEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.vpnGateway.virtual.wanIp" /></s:param></s:text>');
	            virtualWanIpEle.focus();
	            return false;
			}
			if(virtualLanIpEle.value.length == 0){
				showNetworkSettingsContent();
				hm.util.reportFieldError(virtualLanIpEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.vpnGateway.virtual.lanIp" /></s:param></s:text>');
	            virtualLanIpEle.focus();
	            return false;
			}
			if(vrrpPriorityEle.value.length == 0){
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpPriorityEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.brRouter.vrrp.vrid.priority" /></s:param></s:text>');
	            vrrpPriorityEle.focus();
	            return false;
			}
			if(vrrpDelayEle.value.length == 0){
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpDelayEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.brRouter.vrrp.vrid.delay" /></s:param></s:text>');
	            vrrpDelayEle.focus();
	            return false;
			}

			//field range check
			var message = hm.util.validateIntegerRange(vrrpIdEle.value, '<s:text name="hiveAp.brRouter.vrrp.vrid" />', 0, 255);
			if (message != null) {
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpIdEle, message);
				vrrpIdEle.focus();
				return false;
			}

			var message = hm.util.validateIntegerRange(vrrpPriorityEle.value, '<s:text name="hiveAp.brRouter.vrrp.vrid.priority" />', 1, 255);
			if (message != null) {
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpPriorityEle, message);
				vrrpPriorityEle.focus();
				return false;
			}

			var message = hm.util.validateIntegerRange(vrrpDelayEle.value, '<s:text name="hiveAp.brRouter.vrrp.vrid.delay" />', 1, 255);
			if (message != null) {
				showNetworkSettingsContent();
				hm.util.reportFieldError(vrrpDelayEle, message);
				vrrpDelayEle.focus();
				return false;
			}

			//wan, lan IP address check
			if (! hm.util.validateIpAddress(virtualWanIpEle.value)) {
				showNetworkSettingsContent();
				hm.util.reportFieldError(virtualWanIpEle, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.vpnGateway.virtual.wanIp" /></s:param></s:text>');
				virtualWanIpEle.focus();
				return false;
			}
			if (! hm.util.validateIpAddress(virtualLanIpEle.value)) {
				showNetworkSettingsContent();
				hm.util.reportFieldError(virtualLanIpEle, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.vpnGateway.virtual.lanIp" /></s:param></s:text>');
				virtualLanIpEle.focus();
				return false;
			}
		}
	}
	return true;
}

//end br100 function

function radioNasTypeChanged() {
	var radioOptions = document.getElementsByName('dataSource.nasIdentifierType');
	var radioOption;
	for(var i=0; i<radioOptions.length; i++){
		if(radioOptions[i].checked){
			radioOption = radioOptions[i];
			break;
		}
	}
	var customizedNasIdentifierEl = document.getElementById(formName + "_dataSource_customizedNasIdentifier");
	if(radioOption && parseInt(radioOption.value) == USE_CUSTOMIZED_NAS_IDE){
		customizedNasIdentifierEl.disabled = false;
	} else {
		customizedNasIdentifierEl.disabled = true;
	}
}

//start for PMTUD

function enableMonitorCvgMSS(cbxValue){
	var thresholdCvgForAllTCP = document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP");
	var thresholdCvgThroughVPNTunnel = document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel");
	thresholdCvgForAllTCP.disabled = !cbxValue;
	thresholdCvgThroughVPNTunnel.disabled = !cbxValue;

	if(!cbxValue){
		thresholdCvgForAllTCP.value = "";
		thresholdCvgThroughVPNTunnel.value = "";
	}
}

function enableMonitorBrMSS(cbxValue){
	var thresholdBrForAllTCP = document.getElementById(formName + "_dataSource_thresholdBrForAllTCP");
	var thresholdBrThroughVPNTunnel = document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel");
	thresholdBrForAllTCP.disabled = !cbxValue;
	thresholdBrThroughVPNTunnel.disabled = !cbxValue;

	if(!cbxValue){
		thresholdBrForAllTCP.value = "";
		thresholdBrThroughVPNTunnel.value = "";
	}
}

function enableOverrideBrPMTUD(cbxValue){
	if(cbxValue){
		document.getElementById("enableBrPMTUDTr").style.display = "";
		document.getElementById("monitorBrMSSTr").style.display = "";
		document.getElementById("monitorBrMSSNoteTr").style.display = "";
		document.getElementById("thresholdBrForAllTCPTr").style.display = "";
		document.getElementById("thresholdBrThroughVPNTunnelTr").style.display = "";

		var enableBrPMTUD = document.getElementById(formName + "_dataSource_enableBrPMTUD");
		var monitorBrMSS = document.getElementById(formName + "_dataSource_monitorBrMSS");
		enableBrPMTUD.disabled = !cbxValue;
		monitorBrMSS.disabled = !cbxValue;

		if(!cbxValue){
			document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").disabled = !cbxValue;
			document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").disabled = !cbxValue;
		}else{
			if(monitorBrMSS.checked){
				document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").disabled = !cbxValue;
				document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").disabled = !cbxValue;
			}else{
				document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").disabled = cbxValue;
				document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").disabled = cbxValue;
			}
		}
	}else{
		document.getElementById("enableBrPMTUDTr").style.display = "none";
		document.getElementById("monitorBrMSSTr").style.display = "none";
		document.getElementById("monitorBrMSSNoteTr").style.display = "none";
		document.getElementById("thresholdBrForAllTCPTr").style.display = "none";
		document.getElementById("thresholdBrThroughVPNTunnelTr").style.display = "none";
	}
}

function beforeSubmitMSS(operation){
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') &&  (deviceType == DEVICE_TYPE_VPN_GATEWAY || deviceType == DEVICE_TYPE_BRANCH_ROUTER)){
		var inputElement = document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP");
		if (inputElement && inputElement.value.length == 0){
			inputElement.value =  0;
		}
		var inputElement2 = document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel");
		if (inputElement2 && inputElement2.value.length == 0){
			 inputElement2.value =  0;
		 }
		var inputElementBr = document.getElementById(formName + "_dataSource_thresholdBrForAllTCP");
		if (inputElementBr && inputElementBr.value.length == 0){
			inputElementBr.value = 0;
		}
		var inputElementBr2 = document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel");
		if (inputElementBr2 && inputElementBr2.value.length == 0){
				inputElementBr2.value = 0;
		}
	}
}

function validateCvgForMonitorMSS(operation) {
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') &&  deviceType == DEVICE_TYPE_VPN_GATEWAY){
		if(!document.getElementById(formName + "_dataSource_monitorCvgMSS").checked) {
			return true;
		}

		var inputElement = document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP");
		if (inputElement.value.length > 0) {
			 var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="hiveAp.vpn.threshold.all.tcp.connections" />', 64, 1460);
			    if (message != null) {
			    	showAdvancedSettingsContent();
			    	hm.util.reportFieldError(inputElement, message);
			        inputElement.focus();
			        return false;
			    }
	    }

	    var inputElement2 = document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel");
	    if (inputElement2.value.length > 0) {
	    	var message2 = hm.util.validateIntegerRange(inputElement2.value, '<s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" />', 64, 1460);
		    if (message2 != null) {
		    	showAdvancedSettingsContent();
		    	hm.util.reportFieldError(inputElement2, message2);
		        inputElement2.focus();
		        return false;
		    }
	    }
	}
    return true;
}

function validateBrForMonitorMSS(operation) {
	var deviceType = document.getElementById(formName + "_dataSource_deviceType").value;
	if((operation == 'create2' || operation == 'update2') && deviceType == DEVICE_TYPE_BRANCH_ROUTER){
		if(!document.getElementById(formName + "_dataSource_enableOverrideBrPMTUD").checked){
			return true;
		}
		if(!document.getElementById(formName + "_dataSource_monitorBrMSS").checked) {
			return true;
		}

		var inputElement = document.getElementById(formName + "_dataSource_thresholdBrForAllTCP");
		if (inputElement.value.length > 0) {
			var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="hiveAp.vpn.threshold.all.tcp.connections" />', 64, 1460);
		    if (message != null) {
		        hm.util.reportFieldError(inputElement, message);
		        inputElement.focus();
		        return false;
		    }
	    }

	    var inputElement2 = document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel");
		if (inputElement2.value.length > 0) {
			var message2 = hm.util.validateIntegerRange(inputElement2.value, '<s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" />', 64, 1460);
		    if (message2 != null) {
		        hm.util.reportFieldError(inputElement2, message2);
		        inputElement2.focus();
		        return false;
		    }
	    }

	}
    return true;
}

function validateBonjourGatewayConfig(operation){
	if(operation == 'create2' || operation == 'update2'){
		var supportBonjour = <s:property value="dataSource.supportBonjour"/>;
		if(supportBonjour){
			var overrideEl = document.getElementById(formName + "_dataSource_priority");
			if(overrideEl){
				if(overrideEl.value.length == 0){
					showBonjourGatewayConfigContent();
					hm.util.reportFieldError(overrideEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.bonjour.gateway.priority" /></s:param></s:text>');
					overrideEl.focus();
		            return false;
				}
				if(overrideEl.value.length > 0){
					var message = hm.util.validateIntegerRange(Number(overrideEl.value), '<s:text name="hiveAp.bonjour.gateway.priority" />', 0, 255);
				    if (message != null) {
				    	showBonjourGatewayConfigContent();
				        hm.util.reportFieldError(overrideEl, message);
				        overrideEl.focus();
				        return false;
				    }
				}
			}
			var realmNameEl = document.getElementById(formName + "_dataSource_realmName");
			if(realmNameEl){
				if(realmNameEl.value.length > 128){
			    	showBonjourGatewayConfigContent();
			    	hm.util.reportFieldError(realmNameEl, '<s:text name="error.keyLengthRange"><s:param><s:text name="monitor.bonjour.gateway.realm.modifyname" /></s:param><s:param><s:text name="config.BonjourGatewaySetting.description.range" /></s:param></s:text>');
			        realmNameEl.focus();
			        return false;
				}
			}
		}
	}
	return true;
}

function validateInterfaceMtu(operation){
	var interfaceMtu4EthernetEle = document.getElementById(formName + "_dataSource_interfaceMtu4Ethernet");
	var interfaceMtu4Mgt0Ele = document.getElementById(formName + "_dataSource_interfaceMtu4Mgt0");
	if(operation == 'create2' || operation == 'update2'){
		if(interfaceMtu4EthernetEle){
			if(interfaceMtu4EthernetEle.value.length == 0){
				showAdvancedSettingsContent();
				hm.util.reportFieldError(interfaceMtu4EthernetEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.interface.mtu.ethernet.label" /></s:param></s:text>');
				interfaceMtu4EthernetEle.focus();
	            return false;
			}

			if(interfaceMtu4EthernetEle.value.length > 0){
				var message = hm.util.validateIntegerRange(Number(interfaceMtu4EthernetEle.value), '<s:text name="hiveAp.interface.mtu.ethernet.label" />', 1500, 9600);
			    if (message != null) {
			    	showAdvancedSettingsContent();
			        hm.util.reportFieldError(interfaceMtu4EthernetEle, message);
			        interfaceMtu4EthernetEle.focus();
			        return false;
			    }
			}
		}

		if(interfaceMtu4Mgt0Ele){
			if(interfaceMtu4Mgt0Ele.value.length == 0){
				showAdvancedSettingsContent();
				hm.util.reportFieldError(interfaceMtu4Mgt0Ele, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.interface.mtu.mgt0.label" /></s:param></s:text>');
				interfaceMtu4Mgt0Ele.focus();
	            return false;
			}

			if(interfaceMtu4Mgt0Ele.value.length > 0){
				var message = hm.util.validateIntegerRange(Number(interfaceMtu4Mgt0Ele.value), '<s:text name="hiveAp.interface.mtu.mgt0.label" />', 100, 1500);
			    if (message != null) {
			    	showAdvancedSettingsContent();
			        hm.util.reportFieldError(interfaceMtu4Mgt0Ele, message);
			        interfaceMtu4Mgt0Ele.focus();
			        return false;
			    }
			}
		}

	}
	return true;
}

function updateCvgForMonitorMSS(){
	var overrideEl = document.getElementById(formName + "_dataSource_monitorCvgMSS");
	if(!overrideEl){
		return;
	}
	if(!overrideEl.checked){
		document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP").value="";
		document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP").value=="0"){
		document.getElementById(formName + "_dataSource_thresholdCvgForAllTCP").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel").value=="0"){
		document.getElementById(formName + "_dataSource_thresholdCvgThroughVPNTunnel").value="";
	}
}

function updateBrForMonitorMSS(){
	var overrideEl = document.getElementById(formName + "_dataSource_monitorBrMSS");
	if(!overrideEl){
		return;
	}
	if(!overrideEl.checked){
		document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").value="";
		document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").value == "0"){
		document.getElementById(formName + "_dataSource_thresholdBrForAllTCP").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").value == "0"){
		document.getElementById(formName + "_dataSource_thresholdBrThroughVPNTunnel").value="";
	}
}


//end for PMTUD
function changeEnabledBrAsRadiusServer(checked) {
	<s:if test="%{deviceSptRadiusServer}">
	if (checked) {
		hm.util.show('brAsRadiusServerOverrideDisplayDiv');
		changeEnabledBrAsRadiusServerOverride(Get(formName + "_dataSource_enabledOverrideRadiusServer").checked);
	} else {
		hm.util.hide('brAsRadiusServerOverrideDisplayDiv');
		Get(formName + "_dataSource_enabledOverrideRadiusServer").checked=false;
		changeEnabledBrAsRadiusServerOverride(false);
	}
	</s:if>
}

function changeEnabledBrAsRadiusServerOverride(checked) {
	if (checked) {
		hm.util.show('radiusServerRowDiv');
		hm.util.show('radiusProxyRowDiv');
	} else {
		hm.util.hide('radiusServerRowDiv');
		hm.util.hide('radiusProxyRowDiv');
		Get(formName + "_radiusServer").value="-1";
		Get(formName + "_radiusProxy").value="-1";
	}
}

function enabledPppoeCheckBox(checked){
	if (checked) {
		Get("eth0_pppoeAuth").style.display="";
	} else {
		Get("eth0_pppoeAuth").style.display="none";
	}
}

function enabledVoipCheckBox(checked, ifId) {
	if (checked) {
		Get(formName + ifId).readOnly=false;
	} else {
		Get(formName + ifId).readOnly=true;
		Get(formName + ifId).value=100;
	}
}

function enabledPseCheckBox(checked, ifId) {
	if (checked) {
		Get(ifId).disabled=false;
	} else {
		Get(ifId).disabled=true;
	}
}

function enabledOverrideVoip(checked) {
	if (checked) {
		Get("voipDetailDiv").style.display="";
	} else {
		Get("voipDetailDiv").style.display="none";
	}
	Get(formName + "_branchRouterEth0_maxDownload").value=100;
	Get(formName + "_branchRouterEth0_maxUpload").value=100;
	Get(formName + "_branchRouterEth0_maxDownload").readOnly=true;
	Get(formName + "_branchRouterEth0_maxUpload").readOnly=true;
	Get(formName + "_branchRouterEth0_enableMaxDownload").checked=false;
	Get(formName + "_branchRouterEth0_enableMaxUpload").checked=false;
	Get(formName + "_branchRouterUSB_maxDownload").value=100;
	Get(formName + "_branchRouterUSB_maxUpload").value=100;
	Get(formName + "_branchRouterUSB_maxDownload").readOnly=true;
	Get(formName + "_branchRouterUSB_maxUpload").readOnly=true;
	Get(formName + "_branchRouterUSB_enableMaxDownload").checked=false;
	Get(formName + "_branchRouterUSB_enableMaxUpload").checked=false;

}

function enabledOverrideRoutingPolicyFc(checked) {
	if (checked) {
		Get("routingPolicyDetailDiv").style.display="";
	} else {
		Get("routingPolicyDetailDiv").style.display="none";
		Get(formName + "_routingPolicyId").value=-1;
	}
}
</script>

<script type="text/javascript">
function cloneInterfaceTypeListBox(index, value) {
	var listBox = document.getElementById(formName+"_interfaceType");
	var listBoxClone = listBox.cloneNode(true);
	listBoxClone.id = formName + "_interfaceTypes_" + index;
	listBoxClone.name = "interfaceTypes";
	listBoxClone.style.width = "60px";
	listBoxClone.value = value;
	return listBoxClone;
}

function cloneVirtualConnectListBox(index, listName, listValue) {
	var listBox = document.getElementById(formName+"_"+listName);
	var listBoxClone = listBox.cloneNode(true);
	listBoxClone.id = formName + "_" + listName + "s_" + index;
	listBoxClone.name = listName + "s";
	listBoxClone.style.width = "55px";
	listBoxClone.value = listValue;
	return listBoxClone;
}


//function getInterfaceTypeListBoxString(value) {
//	var listBox = document.getElementById(formName+"_interfaceType");
//	var str = "<select id='hiveAp_interfaceType' name='interfaceType' width='60px'>";
//	for(var i=0; i<listBox.length; i++){
//		if(listBox.options[i].value == value) {
//			str += "<option value='"+listBox.options[i].value+"' selected>"+listBox.options[i].text+"</option>";
//		} else {
//			str += "<option value='"+listBox.options[i].value+"'>"+listBox.options[i].text+"</option>";
//		}
//	}
//	str += "</select>";
//	return str;
//}
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
function doAddDynamicRoute2() {
	if(!validate('addDynamicRoute2')) {
		return ;
	}

	var url = '<s:url action="hiveAp" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=addDynamicRoute2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'addDynamicRoute2';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddDynamicRoute2}, null);
}
var succAddDynamicRoute2 = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('headerSectionDynamicRoutes2'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('dynamicRoutes2TblData');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='dynamicRouteIndices' value='" + details.itemId + "' />";
    	// neighborMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "120px";
    	oCell.className = "list";
		oCell.innerHTML = details.neighborMac;
    	// routeMinimun
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details.routeMinimun;
    	// routeMaximun
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details.routeMaximun;
    	// add grid count
    	adjustGridCount("dynamicRoutes2TblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveDynamicRoutes2() {
	if(!validate('removeDynamicRoutes2')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeDynamicRoutes2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeDynamicRoutes2';
	</s:else>

	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveDynamicRoutes2}, null);
}
var succRemoveDynamicRoutes2 = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('dynamicRoutes2TblData');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='dynamicRouteIndices' value='" + subDetails.itemId + "' />";
    	// neighborMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "120px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.neighborMac;
    	// routeMinimun
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.routeMinimun;
    	// routeMaximun
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.routeMaximun;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("dynamicRoutes2TblGridCount", <s:property value='gridCount' />, recCount);
}


function doAddStaticRoute2() {
	if(!validate('addStaticRoute2')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=addStaticRoute2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'addStaticRoute2';
	</s:else>

	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddStaticRoute2}, null);
}
var succAddStaticRoute2 = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('checkAllStatic'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('staticRoute2TblData');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='staticRouteIndices' value='" + details.itemId + "' />";
    	// destinationMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
		oCell.innerHTML = details.destinationMac;
    	// interfaceType
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
    	oCell.appendChild(cloneInterfaceTypeListBox(details.itemId, details.interfaceType));
    	// nextHopMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
    	oCell.innerHTML = details.nextHopMac;
    	// add grid count
    	adjustGridCount("staticRoute2TblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveStaticRoutes2() {
	if(!validate('removeStaticRoutes2')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeStaticRoutes2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeStaticRoutes2';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveStaticRoutes2}, null);
}
var succRemoveStaticRoutes2 = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('staticRoute2TblData');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='staticRouteIndices' value='" + subDetails.itemId + "' />";
    	// destinationMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.destinationMac;
    	// interfaceType
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
    	oCell.appendChild(cloneInterfaceTypeListBox(subDetails.itemId, subDetails.interfaceType));
    	// nextHopMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.nextHopMac;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("staticRoute2TblGridCount", <s:property value='gridCount' />, recCount);
}


function doAddIpRoute2() {
	if(!validate('addIpRoute2')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=addIpRoute2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'addIpRoute2';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddIpRoute2}, null);
}
var succAddIpRoute2 = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('checkAllIp'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('ipRoutes2TblData');
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
    	oCell.style.width = "220px";
    	oCell.className = "list";
		oCell.innerHTML = details.sourceIp;
    	// netmask
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "150px";
    	oCell.className = "list";
    	oCell.innerHTML = details.netmask;
    	// gateway
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "150px";
    	oCell.className = "list";
    	oCell.innerHTML = details.gateway;
    	// add grid count
    	adjustGridCount("ipRoutes2TblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveIpRoutes2() {
	if(!validate('removeIpRoutes2')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeIpRoutes2';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeIpRoutes2';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveIpRoutes2}, null);
}
var succRemoveIpRoutes2 = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('ipRoutes2TblData');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);

    	oCell.style.align = "left";
    	oCell.style.width = "60px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='ipRouteIndices' value='" + subDetails.itemId + "' />";
    	// sourceIp
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "220px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.sourceIp;
    	// netmask
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "150px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.netmask;
    	// gateway
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "150px";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.gateway;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("ipRoutes2TblGridCount", <s:property value='gridCount' />, recCount);
}


function doAddMultipleVlan() {
	if(!validate('addMultipleVlan')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=addMultipleVlan';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'addMultipleVlan';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddMultipleVlan}, null);
}
var succAddMultipleVlan = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('checkAllVlanId'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('multipleVlanTblData');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='multiplevlanIndices' value='" + details.itemId + "' />";
    	// vlanId
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "400px";
    	oCell.className = "list";
		oCell.innerHTML = details.vlanId;
    	// add grid count
    	adjustGridCount("multipleVlanTblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveMultipleVlan() {
	if(!validate('removeMultipleVlan')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeMultipleVlan';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeMultipleVlan';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveMultipleVlan}, null);
}
var succRemoveMultipleVlan = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('multipleVlanTblData');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);

    	oCell.style.align = "left";
    	oCell.style.width = "180px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='multiplevlanIndices' value='" + subDetails.itemId + "' />";
    	// vlanId
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "400px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.vlanId;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("multipleVlanTblGridCount", <s:property value='gridCount' />, recCount);
}

/*Added by lidan  */
function doAddVirtualConnect() {
	if(!validate('addVirtualConnect')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=addVirtualConnect';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'addVirtualConnect';
	</s:else>

	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succAddVirtualConnect}, null);
}
var succAddVirtualConnect = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) { // failed
		hm.util.reportFieldError(Get('checkAllVirtualConnect'), details.errMsg);
    } else { // succeeded, add one row
		var table = Get('virtualConnectTblData');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='virtualConnectIndices' value='" + details.itemId + "' />";
    	// virtualConnectName
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
		oCell.innerHTML = details.virtualConnectName;
    	// virtualConnectAction
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectAction",details.virtualConnectAction));
       	oCell.innerHTML = details.virtualConnectAction_str;
    	// virtualConnectInterface_in
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectInterface_in",details.virtualConnectInterface_in));
    	oCell.innerHTML = details.virtualConnectInterface_in_str;
    	// virtualConnectSourceMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = details.virtualConnectSourceMac;
    	// virtualConnectDestMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = details.virtualConnectDestMac;
    	// virtualConnectTxMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = details.virtualConnectTxMac;
    	// virtualConnectInterface_out
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectInterface_out",details.virtualConnectInterface_out));
    	oCell.innerHTML = details.virtualConnectInterface_out_str;
    	// virtualConnectRxMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = details.virtualConnectRxMac;
    	// add grid count
    	adjustGridCount("virtualConnectTblGridCount", <s:property value='virtualConnectGridCount' />, details.gridCount);
	}
}

function doRemoveVirtualConnect() {
	if(!validate('removeVirtualConnect')) {
		return ;
	}

	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
				+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeVirtualConnect';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeVirtualConnect';
	</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveVirtualConnect}, null);
}
var succRemoveVirtualConnect = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('virtualConnectTblData');
	var index = table.rows.length;
	if(document.getElementById("textfe_checkAllVirtualConnect")){
		for(var i=table.rows.length - 1; i>=3; i--) {
			table.deleteRow(i);
		}
	}else{
		for(var i=table.rows.length - 1; i>=2; i--) {
			table.deleteRow(i);
		}
	}
	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='virtualConnectIndices' value='" + subDetails.itemId + "' />";
    	// virtualConnectName
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.virtualConnectName;
    	// virtualConnectAction
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectAction",subDetails.virtualConnectAction));
    	oCell.innerHTML = subDetails.virtualConnectAction_str;
    	// virtualConnectInterface_in
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectInterface_in",subDetails.virtualConnectInterface_in));
    	oCell.innerHTML = subDetails.virtualConnectInterface_in_str;
    	// virtualConnectSourceMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.virtualConnectSourceMac;
    	// virtualConnectDestMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.virtualConnectDestMac;
    	// virtualConnectTxMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.virtualConnectTxMac;
    	// virtualConnectInterface_out
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	//oCell.appendChild(cloneVirtualConnectListBox(details.itemId, "virtualConnectInterface_out",subDetails.virtualConnectInterface_out));
    	oCell.innerHTML = subDetails.virtualConnectInterface_out_str;
    	// virtualConnectRxMac
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.className = "list";
    	oCell.innerHTML = subDetails.virtualConnectRxMac===undefined?"":subDetails.virtualConnectRxMac;;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("virtualConnectTblGridCount", <s:property value='virtualConnectGridCount' />, recCount);
}

function doAddIntNetwork() {
	if(!validateIntNetwork('addIntNetwork')) {
		return ;
	}
	var url =  "<s:url action='hiveAp' includeParams='none' />?operation=addIntNetwork"+
		"&interNetIpInput="+ document.getElementById(formName + "_interNetIpInput").value +
		"&interNetMaskInput="+ document.getElementById(formName + "_interNetMaskInput").value +
		"&distributeNet="+ document.getElementById(formName + "_distributeNet").checked +
		"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success : succAddIntNetwork, failure : resultDoNothing, timeout: 60000}, null);
}
var succAddIntNetwork = function(o) {
	eval("var details = " + o.responseText);

	if(!details.resultStatus) {
		var interNetIpInput = document.getElementById(formName + "_interNetIpInput");
		hm.util.reportFieldError(interNetIpInput, details.errMsg);
		interNetIpInput.focus();
		//hm.util.reportFieldError(Get('errMsg'), details.errMsg);
    } else {
		var table = Get('tbnet_id');
    	var newRow = table.insertRow(-1);

    	var oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='intNetworkIndices' value='" + details.itemId + "' />";

		oCell = newRow.insertCell(-1);
		oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details.network;

    	oCell = newRow.insertCell(-1);
		oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details.netmask;

    	adjustGridCount("intNetworkTblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveIntNetwork() {
	if(!validateIntNetwork('removeIntNetwork')) {
		return ;
	}
	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	document.getElementById(formName).operation.value = 'removeIntNetwork';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveIntNetwork}, null);
}
var succRemoveIntNetwork = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('tbnet_id');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);

		var oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='intNetworkIndices' value='" + subDetails.itemId + "' />";

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.network;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.netmask;

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("intNetworkTblGridCount", <s:property value='gridCount' />, recCount);
}

function doAddBrStaticRouting() {
	if(!validateBrStaticRouting('addBrStaticRouting')) {
		return ;
	}
	
	var wanIfConnTypes = [];
	var wanIfIpAndNetmasks = [];
	var wanIfNums = [];
	var connTypeEle = $("select[id*='hiveAp_connectionType_']");
	if(connTypeEle){
		connTypeEle.each(function(i,element){
			wanIfConnTypes.push(element.value);
		});
	}
	var wanIfIpAndNetmaskEle = $("input[id*='switch_ipAddress_']"); 
	if(wanIfIpAndNetmaskEle){
		wanIfIpAndNetmaskEle.each(function(i,element){
			var wanportid =$("input[id='switch_ipAddress_"+i+"']").parent().parent().parent().attr('id');
			if(typeof(wanportid) !='undefined' && wanportid != 'switch3000'){
				wanIfIpAndNetmasks.push(element.value);
				wanIfNums.push(wanportid.substring(6,wanportid.length));//switch28
			}
		});
	}
	var reqArgs = {
			'operation': 'addBrStaticRouting',
			'brStaticRouteIpInput': document.getElementById(formName + "_brStaticRouteIpInput").value,
			'brStaticRouteMaskInput': document.getElementById(formName + "_brStaticRouteMaskInput").value,
			'brStaticRouteGwInput': document.getElementById(formName + "_brStaticRouteGwInput").value,
			'advertiseCvg': document.getElementById(formName + "_advertiseCvg").checked,
			'wanIfConnTypes':wanIfConnTypes,
			'wanIfIpAndNetmasks':wanIfIpAndNetmasks,
			'wanIfNums':wanIfNums,
			'ignore': new Date().getTime()
	};
	$.post('hiveAp.action',
			$.param(reqArgs, true),
			function(data, textStatus) {
				succAddBrStaticRouting(data);
			},
			'json');
}

var succAddBrStaticRouting = function(details) {
	if(!details.resultStatus) {
		var interNetIpInput;
		if(details.focusName){
			interNetIpInput= document.getElementById(formName +"_" +details.focusName);
		} else {
			interNetIpInput= document.getElementById(formName + "_brStaticRouteIpInput");
		}
		hm.util.reportFieldError(interNetIpInput, details.errMsg);
		interNetIpInput.focus();
		//hm.util.reportFieldError(Get('errMsg'), details.errMsg);
    } else {
		var table = Get('tbbrroute_id');
    	var newRow = table.insertRow(-1);

    	var oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='brStaticRouteingIndices' value='" + details.itemId + "' />";

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = details.ip;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = details.netmask;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = details.gateway;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = "<input type='checkbox' name='advertiseCvg' disabled='true' " + details.advertiseCvg + " />";

    	adjustGridCount("intBrStaticRouitngTblGridCount", <s:property value='gridCount' />, details.gridCount);
	}
}

function doRemoveBrStaticRouting() {
	if(!validateBrStaticRouting('removeBrStaticRouting')) {
		return ;
	}
	var url = '<s:url action="hiveAp.action" includeParams="none"></s:url>'
		+ '?ignore='+new Date().getTime();
	<s:if test="%{jsonMode}">
		url += '&operation=removeBrStaticRouting';
	</s:if>
	<s:else>
		document.getElementById(formName).operation.value = 'removeBrStaticRouting';
	</s:else>

	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: succRemoveBrStaticRouting}, null);
}

var succRemoveBrStaticRouting = function(o) {
	eval("var details = " + o.responseText);
	var table = Get('tbbrroute_id');

	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	var recCount = '0';
	for(var i=0; i<details.length; i++) {
		var subDetails = details[i];
		var newRow = table.insertRow(-1);

		var oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "30px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='brStaticRouteingIndices' value='" + subDetails.itemId + "' />";

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.ip;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.netmask;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = subDetails.gateway;

		oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
		oCell.innerHTML = "<input type='checkbox' name='advertiseCvg' disabled='true' " + subDetails.advertiseCvg + " />";

    	recCount = subDetails.gridCount;
	}
	// add grid count
	adjustGridCount("intBrStaticRouitngTblGridCount", <s:property value='gridCount' />, recCount);
}

var portDetailsPanel = null;

function createPortDetailsPanel(width, height){
	var div = document.getElementById("portDetailsPanel");
	var iframe = document.getElementById("portDetailsFrame");
	iframe.width = width;
	iframe.height = height;
	portDetailsPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	portDetailsPanel.render();
	div.style.display="";
	overlayManager.register(portDetailsPanel);
	portDetailsPanel.beforeHideEvent.subscribe(closePortDetailsPanel);
	portDetailsPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openPortDetails(){
	if(null == portDetailsPanel){
		createPortDetailsPanel(750,260);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("portDetailsFrame").style.display = "";
	}

	portDetailsPanel.show();
	var iframe = document.getElementById("portDetailsFrame");

	var wanIfConnTypes = [];
	var wanIfIpAndNetmasks = [];
	var wanIfNums = [];
	var connTypeEle = $("select[id*='hiveAp_connectionType_']");
	if(connTypeEle){
		connTypeEle.each(function(i,element){
			wanIfConnTypes.push(element.value);
		});
	}
	var wanIfIpAndNetmaskEle = $("input[id*='switch_ipAddress_']"); 
	if(wanIfIpAndNetmaskEle){
		wanIfIpAndNetmaskEle.each(function(i,element){
			var wanportid =$("input[id='switch_ipAddress_"+i+"']").parent().parent().parent().attr('id');
			if(typeof(wanportid) !='undefined' && wanportid != 'switch3000'){
				wanIfIpAndNetmasks.push(element.value);
				wanIfNums.push(wanportid.substring(6,wanportid.length));//switch28
			}
		});
	}
	
	var condition = "";
	for(var i=0;i<wanIfNums.length;i++){
		condition+="&wanIfNums="+wanIfNums[i];
	}
	
	for(var i=0;i<wanIfConnTypes.length;i++){
		condition+="&wanIfConnTypes="+wanIfConnTypes[i];
	}
	for(var i=0;i<wanIfIpAndNetmasks.length;i++){
		condition+="&wanIfIpAndNetmasks="+wanIfIpAndNetmasks[i];
	}
	iframe.src ="<s:url value='hiveAp.action' includeParams='none' />?operation=showPortDetails"+condition;
	
	//iframe.src ="<s:url value='hiveAp.action' includeParams='none' />?operation=showPortDetails&wanIfConnTypes="+wanIfConnTypes+"&wanIfIpAndNetmasks="+wanIfIpAndNetmasks+"&wanIfNums="+wanIfNums;
}

function closePortDetailsPanel() {
	portDetailsIFrameWindow.onHidePage();
	portDetailsPanel.hide();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("portDetailsFrame").style.display = "none";
	}
}
var portDetailsIFrameWindow;
</script>