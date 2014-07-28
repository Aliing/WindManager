<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.ui.actions.hiveap.HiveApAction"%>

<script>
var filterFormName = 'hiveApFilter';
var filterChangedType=1;

var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;
var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;
var MODEL_330 = <%=HiveAp.HIVEAP_MODEL_330%>;
var MODEL_350 = <%=HiveAp.HIVEAP_MODEL_350%>;
var MODEL_170 = <%=HiveAp.HIVEAP_MODEL_170%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;
var MODEL_370 = <%=HiveAp.HIVEAP_MODEL_370%>;
var MODEL_390 = <%=HiveAp.HIVEAP_MODEL_390%>;
var MODEL_BR100 = <%=HiveAp.HIVEAP_MODEL_BR100%>;
var MODEL_BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var MODEL_BR200WP = <%=HiveAp.HIVEAP_MODEL_BR200_WP%>;
var MODEL_BR200_LTE_VZ = <%=HiveAp.HIVEAP_MODEL_BR200_LTE_VZ%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;

var DEFAULT_FILTER_CURRENT = "<%=HiveApAction.DEFAULT_FILTER_CURRENT%>";
var DEFAULT_FILTER_CURRENT_DEFAULT = "<%=HiveApAction.DEFAULT_FILTER_CURRENT_DEFAULT%>";

function is11nHiveAP(){
	var selectApModel = document.getElementById(filterFormName + "_filterApModel").value;
	if(selectApModel == MODEL_340 || selectApModel == MODEL_320 
			|| selectApModel == MODEL_380 || selectApModel == MODEL_120
			|| selectApModel == MODEL_110 || selectApModel == MODEL_170 
			|| selectApModel == MODEL_330 || selectApModel == MODEL_350
			|| selectApModel == MODEL_BR100 || selectApModel == MODEL_BR200 || selectApModel == MODEL_BR200WP || selectApModel == MODEL_BR200_LTE_VZ
			|| selectApModel == MODEL_121 || selectApModel == MODEL_141
			|| selectApModel == MODEL_370 || selectApModel == MODEL_390){
		return true;
	}
	return false;
}

function isEth1Available(){
	var selectApModel = document.getElementById(filterFormName + "_filterApModel").value;
	return (is11nHiveAP() && selectApModel != MODEL_120 && selectApModel != MODEL_121 && selectApModel != MODEL_141 && 
			selectApModel != MODEL_110 && selectApModel != MODEL_170 && selectApModel != MODEL_BR100 && 
			selectApModel != MODEL_BR200 && selectApModel != MODEL_BR200WP);
}


function validateFilter(operation) {
	if (operation=='removeFilter') {
		if (document.getElementById("filterSelect").value == '-1' || document.getElementById("filterSelect").value==DEFAULT_FILTER_CURRENT ||
				document.getElementById("filterSelect").value==DEFAULT_FILTER_CURRENT_DEFAULT){
			return false;
		}
	}
	
		if (document.getElementById(filterFormName+"_filterName").value==DEFAULT_FILTER_CURRENT || 
				document.getElementById(filterFormName+"_filterName").value==DEFAULT_FILTER_CURRENT_DEFAULT){
			hm.util.reportFieldError(document.getElementById(filterFormName+"_filterName"), 
					'<s:text name="error.hiveAp.filter.currentPolicy"></s:text>');
			return false;
		}
	
	return true;
}

function submitFilterAction(operation, currentPolicyFlg) {
	if (validateFilter(operation)) {
		<s:if test="%{jsonMode}">
			document.forms[filterFormName].operation.value = operation;
			YAHOO.util.Connect.setForm(document.forms[filterFormName]);
			url =  "<s:url action='hiveAp' includeParams='none' />" +
				"?hmListType=manageAPGuid"+
				"&jsonMode=true"+
				"&filter="+currentPolicyFlg+
				"&ignore="+new Date().getTime();
			if (currentPolicyFlg && (currentPolicyFlg==DEFAULT_FILTER_CURRENT || currentPolicyFlg==DEFAULT_FILTER_CURRENT_DEFAULT)) {
				var selecteds = hm.util.getSelectedCheckItems("networkPolicys");
				url = url + "&selectNetworkPolicyId="+selecteds[0];
			}
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFetchHiveApsList, failure : resultDoNothing, timeout: 60000}, null);
		</s:if>
		<s:else>
			document.forms[filterFormName].operation.value = operation;
	    	document.forms[filterFormName].submit();
		</s:else>
		hideOverlay();
    }
}
if(typeof(filterOverlay) != "undefined" && filterOverlay) {
    try{
        filterOverlay.destroy();
} catch(err) {
    //console.debug(err.description);
    }
} 

var filterOverlay = null;
function createFilterOverlay() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"720px",
		visible:false,
		fixedcenter:"contained",
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	filterOverlay.render(document.body);
	div.style.display = "";
	overlayManager.register(filterOverlay);
	filterOverlay.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
	//reg
	var vpnServer = document.getElementById(filterFormName + "_filterVpnServer");
	var vpnClient = document.getElementById(filterFormName + "_filterVpnClient");

//	vpnServer.onclick = function(){
//		vpnServerClicked(vpnServer, vpnClient);
//	};
//	vpnClient.onclick = function(){
//		vpnClientClicked(vpnServer, vpnClient);
//	};
}

function vpnServerClicked(vpnServer, vpnClient){
	if(vpnServer.checked){
		vpnClient.checked = false;
	}
}

function vpnClientClicked(vpnServer, vpnClient){
	if(vpnClient.checked){
		vpnServer.checked = false;
	}
}

function openFilterOverlay(){
	if(null == filterOverlay){
		createFilterOverlay();
	}
	initialValues();
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}

function provisionFlagClick(checked){
	var provision_el = document.getElementById(filterFormName + "_filterProvision");
	provision_el.disabled = !checked;
}

function filterChanged(value,changeype){
//	alert(value);
	filterChangedType=changeype;
	if(value == -1 || value== DEFAULT_FILTER_CURRENT || value== DEFAULT_FILTER_CURRENT_DEFAULT){
		if (filterChangedType == 2) {
			return ;
		} else {
			submitFilterAction('view',value);
		}
	}else{
		url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=requestFilterValues&filter=" + encodeURIComponent(value) + "&ignore=" + new Date().getTime();
//		alert(url);
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:filterResult,failure:connectedFailed,timeout: 60000}, null);
	}
}

function editFilterOverlay(){
	filterChanged(document.getElementById("filterSelect").value, 2);
}

var filterResult = function(o) {
//	alert("response received...");
	eval("var result = " + o.responseText);
	initialValues();
	if(result.fname){
		document.getElementById(filterFormName+"_filterName").value = result.fname;
		Get("filterNameCheckBox").checked=true;
		document.getElementById(filterFormName+"_filterName").readOnly=false;
	} else {
		Get("filterNameCheckBox").checked=false;
		document.getElementById(filterFormName+"_filterName").value='';
		document.getElementById(filterFormName+"_filterName").readOnly=true;
	}
	if(result.ftemp){
		document.getElementById(filterFormName+"_filterTemplate").value = result.ftemp;
	}
	if(result.fhive){
		document.getElementById(filterFormName+"_filterHive").value = result.fhive;
	}
	if(result.ftopo){
		document.getElementById(filterFormName+"_filterTopology").value = result.ftopo;
	}
	if(result.fip){
		document.getElementById(filterFormName+"_filterIp").value = result.fip;
	}else{
		document.getElementById(filterFormName+"_filterIp").value = "";
	}
	if(result.fprovisionf){
		document.getElementById(filterFormName+"_filterProvisionFlag").checked = result.fprovisionf;
		document.getElementById(filterFormName + "_filterProvision").disabled = !result.fprovisionf;
	}else{
		document.getElementById(filterFormName+"_filterProvisionFlag").checked = false;
		document.getElementById(filterFormName + "_filterProvision").disabled = true;
	}
	if(result.fprovision){
		document.getElementById(filterFormName+"_filterProvision").value = result.fprovision;
	}
	if(result.fconfig){
		document.getElementById(filterFormName+"_filterConfig").value = result.fconfig;
	}
	if(result.fradiusProxy){
		document.getElementById(filterFormName+"_filterRadiusProxy").checked = true;
	}
	if(result.fradius){
		document.getElementById(filterFormName+"_filterRadiusServer").checked = true;
	}
	if(result.fvpn){
		document.getElementById(filterFormName+"_filterVpnServer").checked = true;
	}
	if(result.fvpnClient){
		document.getElementById(filterFormName+"_filterVpnClient").checked = true;
	}
	if(result.fdhcp){
		document.getElementById(filterFormName+"_filterDhcpServer").checked = true;
	}
	if(result.fApType){
		document.getElementById(filterFormName+"_filterApType").value = result.fApType;
	}
	if(result.fDeType){
		document.getElementById(filterFormName+"_filterDeviceType").value = result.fDeType;
	} else {
		document.getElementById(filterFormName+"_filterDeviceType").value = 0;
	}
	
	if(result.fApModel){
		document.getElementById(filterFormName+"_filterApModel").value = result.fApModel;
	} else {
		document.getElementById(filterFormName+"_filterApModel").value = 0;
	}
	if(result.fDisplayVer){
		document.getElementById(filterFormName+"_filterVersion").value = result.fDisplayVer;
	}
	if(result.fHostname){
		document.getElementById(filterFormName+"_filterHostname").value = result.fHostname;
	}
	if(result.fTag1){
		document.getElementById(filterFormName+"_classificationTag1").value = result.fTag1;
	}
	if(result.fTag2){
		document.getElementById(filterFormName+"_classificationTag2").value = result.fTag2;
	}
	if(result.fTag3){
		document.getElementById(filterFormName+"_classificationTag3").value = result.fTag3;
	}
	if(result.fBEth0){
		document.getElementById("filterEth0Bridge").checked = true;
	}
	if(result.fBEth1){
		document.getElementById("filterEth1Bridge").checked = true;
	}
	if(result.fBRed0){
		document.getElementById("filterRed0Bridge").checked = true;
	}
	if(result.fBAgg0){
		document.getElementById("filterAgg0Bridge").checked = true;
	}
	if(result.fSerialNumber){
		document.getElementById(filterFormName+"_filterSerialNumber").value = result.fSerialNumber;
	}
	
	if (filterChangedType ==1) {
		submitFilterAction('search');
	} else {
		hiveApModelChanged();//trigger to update the bridge settings
		if(null == filterOverlay){
			createFilterOverlay();
		}
		filterOverlay.cfg.setProperty('visible', true);
	}
}

var connectedFailed = function(o) {
	//
}

function initialValues(){
	document.getElementById(filterFormName+"_filterName").value = '';
	Get("filterNameCheckBox").checked=false;
	document.getElementById(filterFormName+"_filterName").readOnly=true;
	
	document.getElementById(filterFormName+"_filterTemplate").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterHive").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterTopology").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterIp").value = '';
	document.getElementById(filterFormName+"_filterHostname").value = '';
	document.getElementById(filterFormName+"_filterProvisionFlag").checked = false;
	document.getElementById(filterFormName+"_filterProvision").disabled = true;
	document.getElementById(filterFormName+"_filterProvision").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterConfig").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterVpnServer").checked = false;
	document.getElementById(filterFormName+"_filterVpnClient").checked = false;
	document.getElementById(filterFormName+"_filterRadiusServer").checked = false;
	document.getElementById(filterFormName+"_filterRadiusProxy").checked = false;
	document.getElementById(filterFormName+"_filterDhcpServer").checked = false;
	document.getElementById(filterFormName+"_filterApType").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterApModel").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterDeviceType").selectedIndex = 0;
	document.getElementById(filterFormName+"_filterVersion").value = '';
	document.getElementById(filterFormName+"_classificationTag1").value = '';
	document.getElementById(filterFormName+"_classificationTag2").value = '';
	document.getElementById(filterFormName+"_classificationTag3").value = '';
	document.getElementById(filterFormName+"_filterSerialNumber").value = '';
	document.getElementById("filterEth0Bridge").checked = false;
	document.getElementById("filterEth1Bridge").checked = false;
	document.getElementById("filterRed0Bridge").checked = false;
	document.getElementById("filterAgg0Bridge").checked = false;
}

function hiveApModelChanged(){
	if(document.getElementById(filterFormName + "_filterApModel").value == -2 || isEth1Available()){
		document.getElementById("eth1BridgeTd").style.display = "";
		document.getElementById("red0BridgeTd").style.display = "";
		document.getElementById("agg0BridgeTd").style.display = "";
		document.getElementById("eth1BridgeTdl").style.display = "";
		document.getElementById("red0BridgeTdl").style.display = "";
		document.getElementById("agg0BridgeTdl").style.display = "";
	}else{
		document.getElementById("eth1BridgeTd").style.display = "none";
		document.getElementById("red0BridgeTd").style.display = "none";
		document.getElementById("agg0BridgeTd").style.display = "none";
		document.getElementById("eth1BridgeTdl").style.display = "none";
		document.getElementById("red0BridgeTdl").style.display = "none";
		document.getElementById("agg0BridgeTdl").style.display = "none";
		document.getElementById("filterEth1Bridge").checked = false;
		ethBridgeClick();
		document.getElementById("filterRed0Bridge").checked = false;
		red0BridgeClick();
		document.getElementById("filterAgg0Bridge").checked = false;
		agg0BridgeClick();
	}
}

function filterNameCheckBoxClick() {
	if (Get("filterNameCheckBox").checked) {
		document.getElementById(filterFormName+"_filterName").value = '';
		document.getElementById(filterFormName+"_filterName").readOnly=false;
	} else {
		document.getElementById(filterFormName+"_filterName").value = '';
		document.getElementById(filterFormName+"_filterName").readOnly=true;
	}

}

function ethBridgeClick(){
	<%--
	var eth0Checked = document.getElementById("filterEth0Bridge").checked;
	var eth1Checked = document.getElementById("filterEth1Bridge").checked;
	var elmRed0 = document.getElementById("filterRed0Bridge");
	var elmAgg0 = document.getElementById("filterAgg0Bridge");
	if(eth0Checked || eth1Checked){
		elmRed0.checked = false;
		elmAgg0.checked = false;
	}--%>
}

function red0BridgeClick(){
	<%--
	var red0Checked = document.getElementById("filterRed0Bridge").checked;
	var elmAgg0 = document.getElementById("filterAgg0Bridge");
	var elmEth0 = document.getElementById("filterEth0Bridge");
	var elmEth1 = document.getElementById("filterEth1Bridge");
	if(red0Checked){
		elmAgg0.checked = false;
		elmEth0.checked = false;
		elmEth1.checked = false;
	}--%>
}

function agg0BridgeClick(){
	<%--
	var agg0Checked = document.getElementById("filterAgg0Bridge").checked;
	var elmRed0 = document.getElementById("filterRed0Bridge");
	var elmEth0 = document.getElementById("filterEth0Bridge");
	var elmEth1 = document.getElementById("filterEth1Bridge");
	if(agg0Checked){
		elmRed0.checked = false;
		elmEth0.checked = false;
		elmEth1.checked = false;
	}--%>
}

</script>
<div id="leftFilter">
	<s:if test="%{!jsonMode}">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="filterSep" colspan="2">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="sepLine"><img
						src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="filterH1">Filter </td>
			<td><s:select name="filter" headerKey="-1" headerValue="None" id="filterSelect"
				list="filterList" cssStyle="width:100px;" onchange="filterChanged(this.value, 1);"/></td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td colspan="2">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding-left: 50px;">
							<a class="marginBtn" href="javascript:openFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:editFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:submitFilterAction('removeFilter');">
								<img class="dinl" src="<s:url value="/images/cancel.png" />"
								width="16" height="16" alt="Remove" title="Remove" /></a>
						</td>
						
						<%-- input type="button"
							name="ignore" value="New" class="button" style="width: 76px;"
							onClick="openFilterOverlay();"></td>
						<td class="filterBtn" style="padding-left: 0px;"><input type="button"
							name="ignore" value="Remove" class="button"
							onClick="submitFilterAction('removeFilter');"></td>
						--%>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</s:if>

	<div id="filterPanel" style="display: none;">
	<div class="hd"><s:text name="hiveAp.filter.filterBy"/></div>
	<div class="bd"><s:form action="hiveAp" id="hiveApFilter" name="hiveApFilter">
	<s:hidden name="operation" />
	<!-- fix bug 16816 -->
	<s:hidden name="hmListType"/>
	
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td colspan="2">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td style="padding: 6px 5px 5px 5px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
								<td class="labelT1" colspan="2"><s:text name="hiveAp.template"/></td>
								<td><s:select name="filterTemplate" headerKey="-2" headerValue="All"
									list="filterTemplates" listKey="id" listValue="value" cssStyle="width:152px;"/></td>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.hiveProfile"/></td>
								<td><s:select name="filterHive" headerKey="-2" headerValue="All"
									list="filterHives" listKey="id" listValue="value" cssStyle="width:152px;"/></td>
							</tr>
							<tr>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.topology"/></td>
								<td><s:select name="filterTopology" headerKey="-2" headerValue="All"
									list="filterTopologys" listKey="id" listValue="value" cssStyle="width:152px;"/></td>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.configuration.indication.filter"/></td>
								<td><s:select name="filterConfig" headerKey="-2" headerValue="All"
									list="filterConfigs" listKey="key" listValue="value" cssStyle="width:152px;"/></td>
							</tr>
							<tr>
								<td width="120px" class="labelT1" colspan="2"><s:text name="hiveAp.model"/></td>
								<td width="175px"><s:select name="filterApModel" headerKey="-2" headerValue="All"
									list="enumHiveApModel" listKey="key" listValue="value" cssStyle="width:152px;" onchange="hiveApModelChanged();"/></td>
								<td width="120px" class="labelT1" colspan="2"><s:text name="hiveAp.apType"/></td>
								<td><s:select name="filterApType" headerKey="-2" headerValue="All"
									list="enumHiveApType" listKey="key" listValue="value" cssStyle="width:152px;"/></td>
							</tr>
							<tr>
								<td width="120px" class="labelT1" colspan="2"><s:text name="hiveAp.device.type"/></td>
								<td width="175px"><s:select name="filterDeviceType" headerKey="-2" headerValue="All"
									list="enumHiveFilterDeviceType" listKey="key" listValue="value" cssStyle="width:152px;"/></td>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.serialNumber"/></td>
								<td><s:textfield name="filterSerialNumber" maxlength="14" size="24"/></td>
							</tr>
							<tr>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.autoProvisioning.imageVersion.label"/></td>
								<td><s:textfield name="filterVersion" maxlength="20" size="24"/></td>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.interface.ipAddress"/></td>
								<td><s:textfield name="filterIp" maxlength="20" size="24"/></td>
							</tr>
							<tr>
								<td class="labelT1" width="112px"><s:text name="hiveAp.autoProvisioning.filter"/></td>
								<td><s:checkbox name="filterProvisionFlag" onclick="provisionFlagClick(this.checked);"/></td>
								<td><s:select name="filterProvision" disabled="%{filterSelectionDisabled}"
									list="filterProvisions" listKey="key" listValue="value" cssStyle="width:152px;"/></td>
								<td class="labelT1" colspan="2"><s:text name="hiveAp.hostName"/></td>
								<td><s:textfield name="filterHostname" maxlength="32" size="24"/></td>
							</tr>
							<tr>
								<td colspan="10">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="107px"><s:text name="hiveAp.filter.bridging"/></td>
											<td style="padding: 5px;"><s:checkbox name="filterEth0Bridge" id="filterEth0Bridge" onclick="ethBridgeClick();"/></td>
											<td width="70px"><s:text name="hiveAp.ethernet.eth0.settings" /></td>
											<td style="padding: 5px;" id="eth1BridgeTd"><s:checkbox name="filterEth1Bridge" id="filterEth1Bridge" onclick="ethBridgeClick();"/></td>
											<td width="70px" id="eth1BridgeTdl"><s:text name="hiveAp.ethernet.eth1.settings" /></td>
											<td style="padding: 5px;" id="red0BridgeTd"><s:checkbox name="filterRed0Bridge" id="filterRed0Bridge" onclick="red0BridgeClick();"/></td>
											<td width="90px" id="red0BridgeTdl"><s:text name="hiveAp.ethernet.red0.settings"/></td>
											<td style="padding: 5px;" id="agg0BridgeTd"><s:checkbox name="filterAgg0Bridge" id="filterAgg0Bridge" onclick="agg0BridgeClick();"/></td>
											<td width="70px" id="agg0BridgeTdl"><s:text name="hiveAp.ethernet.agg0.settings"/></td>
										</tr>
									</table>
								</td>
								<%--
								<td class="labelT1">&nbsp;&nbsp;
									<s:checkbox name="filterEth0Bridge"/><s:text name="hiveAp.filter.eth0.bridging" /></td>
								<td colspan="2" id="eth1BridgeTd"><s:checkbox name="filterEth1Bridge"/><s:text name="hiveAp.filter.eth1.bridging" /></td>
								--%>
							</tr>
							<tr>
								<td colspan="10">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="107px"><s:text name="hiveAp.server.label.role" /></td>
											<td style="padding: 5px;"><s:checkbox name="filterVpnServer"/></td>
											<td width="70px"><s:text name="hiveAp.server.label.vpn" /></td>
											<td style="padding: 5px;"><s:checkbox name="filterVpnClient"/></td>
											<td width="70px"><s:text name="hiveAp.client.label.vpn" /></td>
											<td style="padding: 5px;"><s:checkbox name="filterRadiusServer"/></td>
											<td width="90px"><s:text name="hiveAp.server.label.radius" /></td>
											<td style="padding: 5px;display: <s:property value="%{fullModeConfigStyle}"/>;"><s:checkbox name="filterRadiusProxy"/></td>
											<td width="115px" style="display: <s:property value="%{fullModeConfigStyle}"/>"><s:text name="hiveAp.server.label.radius.proxy" /></td>
											<td style="padding: 5px;"><s:checkbox name="filterDhcpServer"/></td>
											<td><s:text name="hiveAp.server.label.dhcp" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr style="display: <s:property value="%{fullModeConfigStyle}"/>">
								<td colspan="10">
									<fieldset><legend><s:text name="hiveAp.classification.tag"/></legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="132px" style="padding-left: 0;"><s:property value="customTag1String"/></td>
											<td><s:select name="classificationTag1" listKey="key"
												listValue="value" list="classificationTag1List" cssStyle="width:152px;"/></td>
										</tr>
										<tr>
											<td class="labelT1" style="padding-left: 0;"><s:property value="customTag2String"/></td>
											<td><s:select name="classificationTag2" listKey="key"
												listValue="value" list="classificationTag2List" cssStyle="width:152px;"/></td>
										</tr>
										<tr>
											<td class="labelT1" style="padding-left: 0;"><s:property value="customTag3String"/></td>
											<td><s:select name="classificationTag3" listKey="key"
												listValue="value" list="classificationTag3List" cssStyle="width:152px;"/></td>
										</tr>
									</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><s:checkbox name="filterNameCheckBox" id="filterNameCheckBox" onclick="filterNameCheckBoxClick();"></s:checkbox> </td>
					<td class="labelT1" width="141px"><s:text name="hiveAp.filter.name"/></td>
					<td><s:textfield name="filterName" maxlength="20" size="24" readonly="true"/></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 8px;" colspan="2">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Search"
						id="search" class="button" onClick="submitFilterAction('search');"></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="hideOverlay();"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table></s:form>
	</div>
	</div>
</div>