<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.be.parameter.constant.util.AhWebUtil"%>
<%@page import="com.ah.util.devices.impl.Device"%>
<tiles:insertDefinition name="flashHeader" />
<script type="text/javascript"
	src="<s:url value="/js/hm.util.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script type="text/javascript"
	src="<s:url value="/js/hm.map.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<s:if test="%{gme}">
	<script type="text/javascript"
		src="https://maps.googleapis.com/maps/api/js?v=3&sensor=false<s:property escape="false" value="gmeKey" />"></script>
</s:if>

<style type="text/css">
<!--
#cliInfoPanel .bd {
	padding: 0;
}

#cliInfoPanel .bd_top {
	background-color: #eee;
}

#cliInfoPanel .cli_viewer {
	padding: 10px;
	overflow: auto;
	height: 25em;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #fff;
}

#cliInfoPanel .ft {
	height: 15px;
	padding: 0;
}

#cliInfoPanel .yui-resize-handle-br {
	right: 0;
	bottom: 0;
	height: 8px;
	width: 8px;
	position: absolute;
}

#detailsPanel .bd,#globalPanel .bd {
	overflow: auto;
}

#lldpCdpPanel .bd {
	overflow: auto;
}
td.textCap{
    text-transform: capitalize;
}
-->
</style>

<script type="text/javascript">
// QUIRKS FLAG, FOR BOX MODEL
var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
// UNDERLAY/IFRAME SYNC REQUIRED
var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

var formName = 'maps';
var setting_formName = 'mapSettings';
var upload_formName = 'mapUpload';
var thisOperation;
var planningOnly = false;
<s:if test="%{planningOnly}">
	planningOnly = true;
</s:if>
var internalUsedErrMsg = "<s:text name='error.value.internal.used'><s:param>'|'</s:param></s:text>";
var mapSettingsBaseUrl = "<s:url action='mapSettings' includeParams='none' />";
var backgroundFormatErrMsg = '<s:text name="error.map.background.image.format"></s:text>';
var hiveApRebootOp = "<s:text name="topology.menu.hiveAp.reboot"/>";
var hiveApResetConfigOp = "<s:text name="topology.menu.hiveAp.reset.default"/>";
var hiveApInvokeBackupOp = "<s:text name="topology.menu.hiveAp.invokeBackup"/>";
var hiveApResetPSEOp = "<s:text name="topology.menu.hiveAp.pse.reset"/>";
var mapSettingsActionUrl = "<s:url action='mapSettings' includeParams='none' />";
var hiveApClearCredentialsOp = "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>";
var hiveApClearCredentialsMsg = "<s:text name="topology.menu.hiveAp.clear.radsec.credentials.message"><s:param>device</s:param></s:text>";
var customWallLineColors = <s:property value="%{wallLineColors}" escape="false"/>;
var customWallLineTypes = <s:property value="%{wallLineTypes}" escape="false"/>;
var initPopUpFlag = <s:property value="%{popUpFlag}"/>;
var initRefreshInterval = <s:property value="%{refreshInterval}"/>;
var initTreeWidth = "<s:property value="%{userContext.treeWidth}" />";
var initSelectedMapId = <s:property value="%{selectedMapId}"/>;
var networkSummaryTitle = "<s:text name="topology.menu.network.summary"/> - ";
var loadingMap2Action = "<s:url value="mapBld.action" includeParams="none"><s:param name="operation" value="%{'loadingMap2'}"/></s:url>";
var refreshIntervalFieldErr = '<s:text name="error.requiredField"><s:param><s:text name="topology.map.refresh.time" /></s:param></s:text>';
var selectImageFieldErr = '<s:text name="error.pleaseSelect"><s:param><s:text name="topology.map.background.image" /></s:param></s:text>';
var diagnosticsPingFieldErr = '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>';
var waitingPanelBody = '<img src="<s:url value="/images/waiting.gif" includeParams="none"/>" />';
var hiveApAction = "<s:url action='hiveAp' includeParams='none' />";
var clientMonitorAction = "<s:url action='clientMonitor' includeParams='none' />";
var mapNodesAction = "<s:url action='mapNodes' includeParams='none' />";
var addMapOp = "<s:text name="topology.menu.operation.add.map"/>";
var addFloorOp = "<s:text name="topology.menu.operation.add.floor"/>";
var addGroupOp = "<s:text name="topology.menu.operation.add.group"/>";
var gme = <s:property value="%{gme}" />;
var apCount = <s:property value="%{domainApCount}" />;
var startDrawBldMsg = '<s:text name="info.gme.start.draw.bld"/>';
var closeDrawBldMsg = '<s:text name="info.gme.close.draw.bld"/>';
var bldPerimFloorMsg = '<s:text name="info.gme.bld.perim.floor"/>';
var geocodeAgainMsg = '<s:text name="info.gme.geocode.again"/>';
var noteInfoWrap = "<table border='0' cellspacing='0' cellpadding='0'><tr><td class='initNoteInfo'>";
var perimIntersectMsg = noteInfoWrap + '<s:text name="info.gme.perim.intersect"/></td></tr></table>';
var drawbuildingMsg = noteInfoWrap + '<s:text name="info.gme.draw.building"/></td></tr></table>';
var drawBldFirstMsg = noteInfoWrap + '<s:text name="info.gme.draw.bld.first"/></td></tr></table>';
var closeBldFirstMsg = noteInfoWrap + '<s:text name="info.gme.close.bld.first"/></td></tr></table>';
var closeBldPerimMsg = noteInfoWrap + '<s:text name="info.gme.close.bld.perim"/></td></tr></table>';
var createFloorPlanMsg = noteInfoWrap + '<s:text name="info.gme.create.floor.plan"/></td></tr></table>';
var clickAndReleaseMsg = noteInfoWrap + '<s:text name="info.gme.click.release"/></td></tr></table>';
var hiveApFetchImageVerOp = '<s:text name="topology.menu.hiveAp.fetchImageVer"/>';
var invokeBackupCurrentText = '<s:text name="topology.menu.hiveAp.invokeBackup.currentText" />';
var invokeBackupBackupText = '<s:text name="topology.menu.hiveAp.invokeBackup.backupText" />';
var invokeBackupCurrentTexWithVer = '<s:text name="topology.menu.hiveAp.invokeBackup.currentTextWithVer"><s:param>|</s:param></s:text>';
var invokeBackupBackupTextWithVer = '<s:text name="topology.menu.hiveAp.invokeBackup.backupTextWithVer"><s:param>|</s:param></s:text>';

var Device_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var Device_TYPE_AP = <%=HiveAp.Device_TYPE_HIVEAP%>;
var Device_TYPE_VPN_GATEWAY = <%=HiveAp.Device_TYPE_VPN_GATEWAY%>;
var Device_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;
var Device_TYPE_VPN_BR = <%=HiveAp.Device_TYPE_VPN_BR%>;
var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;
var MODEL_VPN_GATEWAY = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA%>;
var MODEL_CVG_APPLIANCE = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY%>;
var MODEL_BR200_LTE_VZ = <%=HiveAp.HIVEAP_MODEL_BR200_LTE_VZ%>;
var MODEL_BR200_WP = <%=HiveAp.HIVEAP_MODEL_BR200_WP%>;
var MODEL_BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var MODEL_BR100 = <%=HiveAp.HIVEAP_MODEL_BR100%>;
var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_390 = <%=HiveAp.HIVEAP_MODEL_390%>;
var MODEL_370 = <%=HiveAp.HIVEAP_MODEL_370%>;
var MODEL_350 = <%=HiveAp.HIVEAP_MODEL_350%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_330 = <%=HiveAp.HIVEAP_MODEL_330%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;
var MODEL_170 = <%=HiveAp.HIVEAP_MODEL_170%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;
var MODEL_230 = <%=HiveAp.HIVEAP_MODEL_230%>;
var MODEL_SR_24 = <%=HiveAp.HIVEAP_MODEL_SR24%>;
var MODEL_SR_2024P = <%=HiveAp.HIVEAP_MODEL_SR2024P%>;
var MODEL_SR_2124P = <%=HiveAp.HIVEAP_MODEL_SR2124P%>;
var MODEL_SR_2148P = <%=HiveAp.HIVEAP_MODEL_SR2148P%>;
var MODEL_SR_48 = <%=HiveAp.HIVEAP_MODEL_SR48%>;

<%=AhWebUtil.getBoolArray(Device.SUPPORTED_LOCATE)%>

function onBeforeUnloadMap(){
	//nodify server to stop map refreshing
	var url = "<s:url action='maps' includeParams='none' />" + "?operation=stopMapRefreshing" + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {}, null);
}

function regAuthUrl(cb) {
	var hosted = <s:property value="hosted" />;
	if (!hosted && cb.checked) {
		if (!streetDialog) {
			initStreetDialog("<s:text name="info.gme.msg.support.head"/>");
		}
		var msg = "<s:text name="info.gme.msg.support.one"/>" + document.domain +
		          "<s:text name="info.gme.msg.support.two"/> <a href='<s:text name="info.gme.msg.support.url"/>?domain=" +
		          document.domain + "&systemid=<s:property escape="false" value="onPremiseID" />' target='_blank'>form</a> <s:text name="info.gme.msg.support.three"/>";
		streetDialog.cfg.setProperty("text", msg);
		streetDialog.show();
	}
}

var clientMac = "<s:property value="%{bssid}"/>";
var bssidType = <s:property value="%{bssidType}"/>;
var BSSID_TYPE_CLIENT = <s:property value="%{bssidTypeClient}"/>;
var BSSID_TYPE_ROGUE = <s:property value="%{bssidTypeRogue}"/>

var bldMenu, bldMoveItem, bldMoveUpItem, bldMoveDnItem;
var fldMenu, readOnlyMenu, globalMenu;
function createContextMenu(readNodes, writeNodes, globalWriteNodes, bldWriteNodes) {
	if (readNodes.length > 0 ) {
		readOnlyMenu = new YAHOO.widget.ContextMenu("readNodeMenu", { trigger: readNodes });
		readOnlyMenu.clickEvent.subscribe(onMenuItemClick);
		readOnlyMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		readOnlyMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.network.summary"/>"));
		readOnlyMenu.render(document.body);
	}

	if (writeNodes.length > 0 ) {
		fldMenu = new YAHOO.widget.ContextMenu("writeNodeMenu", { trigger: writeNodes });
		fldMenu.clickEvent.subscribe(onMenuItemClick);
		fldMenu.subscribe('beforeShow', onFldMenuShow);
		fldMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		fldMenu.addItem(new YAHOO.widget.ContextMenuItem('<s:text name="topology.menu.operation.add.map"/>'));
		fldMenu.addItem(new YAHOO.widget.ContextMenuItem('<s:text name="topology.menu.operation.add.building"/>'));
		fldMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		if (!sortFolders) {
			fldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Up"));
			fldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Down"));
		}
		fldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move to Folder"));
		fldMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove"));
		if (!planningOnly) {
			fldMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.troubleshoot.clientTrace"/>"), 1);
			fldMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.network.summary"/>"), 1);
		}
        
		fldMenu.render(document.body);
	}

	if (bldWriteNodes.length > 0) {
		bldMenu = new YAHOO.widget.ContextMenu("bldWriteNodeMenu", { trigger: bldWriteNodes });
		bldMenu.clickEvent.subscribe(onMenuItemClick);
		bldMenu.subscribe('beforeShow', onBldMenuShow);
		bldMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem(addFloorOp));
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move to Folder"));
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Up"));
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Down"));		
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Clone"));
		bldMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove"));
		bldMoveItem = bldMenu.removeItem(2);
		bldMoveUpItem = bldMenu.removeItem(2);
		bldMoveDnItem = bldMenu.removeItem(2);		
		if (!planningOnly) {
			bldMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.troubleshoot.clientTrace"/>"), 1);
			bldMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.network.summary"/>"), 1);
		}
        
		bldMenu.render(document.body);
	}
	
	if (globalWriteNodes.length > 0 ) {
		globalMenu = new YAHOO.widget.ContextMenu("globalWriteNodeMenu", { trigger: globalWriteNodes });
		globalMenu.clickEvent.subscribe(onMenuItemClick);
		globalMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem('<s:text name="topology.menu.operation.add.map"/>'));
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem('<s:text name="topology.menu.operation.add.building"/>'));
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		if (!sortFolders) {
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Up", { disabled: true }));
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("Move Down", { disabled: true }));
		}
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem("Move to Folder", { disabled: true }));
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove", { disabled: true }));
		if (planningOnly) {
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.planner.setting"/>"), 1);
		} else {
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.global.setting"/>"), 1);
		}
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.adddelete.image"/>"), 1);
		globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.tree.width"/>"), 1);
		if (!planningOnly) {
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.troubleshoot.clientTrace"/>"), 2);
			globalMenu.addItem(new YAHOO.widget.ContextMenuItem("<s:text name="topology.menu.network.summary"/>"), 2);
		}
		
		globalMenu.render(document.body);
	}
}

function onMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	var targetMap = mapTree.getNodeByElement(mapNode);
	var mapName = targetMap.label;
	var mapType = targetMap.data.tp;

	//menu item is disabled, do nothing.
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	if (text == "<s:text name="topology.menu.troubleshoot.clientTrace"/>"){
		//open client tracking pannel
		openClientTracePanel(mapContainerId, null);
		return;
	} else if (text == "<s:text name="topology.menu.planner.setting"/>") {
		//open planning panel
		openPlanningPanel();
		return;
	}

	if (text == '<s:text name="topology.menu.operation.add.map"/>') {
		prepareMapNew(mapName, 1);
	} else if (text == '<s:text name="topology.menu.operation.add.building"/>') {
		prepareMapNew(mapName, 2);
	} else if (text.match('^'+addFloorOp)) {
		prepareMapNew(mapName, 3, targetMap.data.tp);
	} else if (text.match('^'+addGroupOp)) {
		prepareMapNew(mapName, 99, targetMap.data.tp);
	} else if (text == "Edit"){
		prepareMapEdit(mapName, mapType);
	} else if (text == "Move to Folder"){
		prepareMapMove(mapName, targetMap);
	} else if (text == "Move Up"){
		prepareFloorMove(targetMap, moveFloorUp, true);
	} else if (text == "Move Down"){
		prepareFloorMove(targetMap, moveFloorDown, false);
	} else if (text == "Clone"){
		prepareMapClone(mapName, targetMap);
	} else if (text == "Remove"){
		removeMapContainer(mapName);
	} else if (text == "<s:text name="topology.menu.global.setting"/>") {
		editGlobalParams(mapContainerId);
		globalPanel.cfg.setProperty('visible', true);
		document.getElementById("refreshInterval").focus();
	} else if (text == "<s:text name="topology.menu.network.summary"/>") {
		displaySummaryPanel(mapContainerId, mapName);
	} else if (text == "<s:text name="topology.menu.adddelete.image"/>") {
		showImageReviewPanel();
	} else if (text == "<s:text name="topology.menu.tree.width"/>") {
		hm.map.showTreeWidthPanel('mapOpsButton');
	}
}

function buildContextMenuItems(deviceType, deviceModel, writePermission){	
	var tItems=[];
	var subItem2=[];
	if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) {
		subItem2.push({text: "<s:text name="topology.menu.client.information"/>"});
	}
	if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48 && deviceModel!=MODEL_BR200) {
		subItem2.push({text: "<s:text name="topology.menu.neighbor.information"/>"});
		subItem2.push({text: "<s:text name="topology.menu.acsp.neighbor.information"/>"});
	}
	if (subItem2.length>0){
		tItems.push(subItem2);
	}
	
	var subItem=[];
	var subArray=[];
	subItem2=[];
	subArray.push("<s:text name="topology.menu.diagnostics.ping"/>");
	subArray.push("<s:text name="topology.menu.diagnostics.showlog"/>");
	if (deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_48 || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P) {
		subArray.push("<s:text name="topology.menu.diagnostics.showfdb"/>");
	}
	
	subArray.push("<s:text name="topology.menu.diagnostics.showversion"/>");
	subArray.push("<s:text name="topology.menu.diagnostics.showrunningconfig"/>");
	subArray.push("<s:text name="topology.menu.diagnostics.showiproutes"/>");
	if (deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		subArray.push("<s:text name="topology.menu.diagnostics.showmacroutes"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showarpcache"/>");
	}
	
	if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		subArray.push("<s:text name="topology.menu.diagnostics.showroamingcache"/>");
	}
	if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceType== Device_TYPE_AP) {
		subArray.push("<s:text name="topology.menu.diagnostics.showdnxpneighbor"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showdnxpcache"/>");
	}
	if (deviceType!=Device_TYPE_VPN_GATEWAY && deviceType!= Device_TYPE_SWITCH) {
		subArray.push("<s:text name="topology.menu.diagnostics.showdhcpclientallocation"/>");
	}
	if (subArray.length>0){
		subItem.push(subArray);
	}
	
	subArray=[];
	if (deviceType!=Device_TYPE_AP && deviceType!=Device_TYPE_SWITCH) {
		subArray.push("<s:text name="topology.menu.diagnostics.showpathmtudiscovery"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showtcpmss"/>");
	}
	if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) {
		 subArray.push("<s:text name="topology.menu.diagnostics.showamrptunnel"/>");
	}
	if (deviceType==Device_TYPE_AP) {
		 subArray.push("<s:text name="topology.menu.diagnostics.showvpngretunnel"/>");
	}
	if (deviceType!=Device_TYPE_SWITCH) {
		subArray.push("<s:text name="topology.menu.diagnostics.showvpnikeevent"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showvpnikesa"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showvpnipsecsa"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showvpnipsectunnel"/>");
	}
	if (subArray.length>0){
		subItem.push(subArray);
	}

	 subArray=[];
	 subArray.push("<s:text name="topology.menu.diagnostics.showcpu"/>");
	 subArray.push("<s:text name="topology.menu.diagnostics.showmemory"/>");
	if (deviceModel==MODEL_320 || deviceModel==MODEL_340) {
		subArray.push("<s:text name="topology.menu.diagnostics.showsystempower"/>");
	}
	if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) {
		 subArray.push("<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>");
	}

	if (deviceModel==MODEL_BR200_LTE_VZ || deviceModel==MODEL_BR200_WP || deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P ||deviceModel==MODEL_SR_48) {
		 subArray.push("<s:text name="topology.menu.diagnostics.showpse"/>");
	 }
	 subItem.push(subArray);
	 
	 subArray=[];
	 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subArray.push("<s:text name="topology.menu.troubleshoot.clientTrace"/>");
	 }
	 if (deviceType!=Device_TYPE_VPN_GATEWAY){
		 subArray.push("<s:text name="topology.menu.troubleshoot.vlan.probe"/>");
	 }
	 if (deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subArray.push("<s:text name="topology.menu.remoteSniffer"/>");
	 }
	 
	if (deviceType==Device_TYPE_BRANCH_ROUTER || deviceType==Device_TYPE_VPN_BR || deviceType==Device_TYPE_VPN_GATEWAY) {
		 subArray.push({text: "<s:text name="topology.menu.firewall.policy"/>", disabled: !writePermission});
	 }
	 
	 if (deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_48 || deviceModel==MODEL_SR_2148P || deviceModel==MODEL_SR_2024P) {
		 //subArray.push("<s:text name="topology.menu.diagnostics.virtualCableTester"/>");
	 }
	 if (subArray.length>0){
		 subItem.push(subArray);
	 }
	 
	 subItem2.push({text: "<s:text name="topology.menu.diagnostics"/>",submenu: {id: "diagnostics",itemdata:subItem}});
	 
	 subItem=[];
	 subArray=[];
	 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subArray.push("<s:text name="topology.menu.statistics.acsp"/>");
	 }
	 subArray.push("<s:text name="topology.menu.statistics.interface"/>");
	 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subArray.push("<s:text name="topology.menu.statistics.summary"/>");
	 }
	 subItem2.push({text: "<s:text name="topology.menu.statistics"/>",submenu: {id: "statistics",itemdata:subArray}});
	 
	 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) { 
		 subArray=[];
		 subArray.push("<s:text name="topology.menu.lldpcdp.clear"/>");
		 subArray.push("<s:text name="topology.menu.lldpcdp.showLldpPara"/>");
		 subArray.push("<s:text name="topology.menu.lldpcdp.showLldpNeighbor"/>");
		 subArray.push("<s:text name="topology.menu.lldpcdp.showCdpPara"/>");
		 subArray.push("<s:text name="topology.menu.lldpcdp.showCdpNeighbor"/>");
		 subItem2.push({text: "<s:text name="topology.menu.lldpcdp"/>",submenu: {id: "lldpcdp",itemdata:subArray}});
	 }
	 
	 if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) {
		 subItem2.push({text: "<s:text name="topology.menu.alg.sip.name"/>"});
	 }
	 subItem2.push({text: "<s:text name="topology.menu.hiveAp.configuration.audit"/>"});
	 tItems.push(subItem2);
	 
	 subArray=[];
	 subItem2=[];
	 subArray.push("<s:text name="hiveAp.update.configuration"/>");
	 subArray.push("<s:text name="hiveAp.update.image"/>");
	 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_AG20 && deviceModel!=MODEL_AG28 
				 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_48) {
	 	subArray.push("<s:text name="hiveAp.update.l7.signature"/>");
	 }
	 subArray.push("<s:text name="hiveAp.update.cwp.remove"/>");
	 subArray.push("<s:text name="hiveAp.update.bootstrap"/>");
	 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
	 	subArray.push("<s:text name="hiveAp.update.countryCode"/>");
	 }
	 if (deviceModel==MODEL_320 || deviceModel==MODEL_340) {
	 	subArray.push("<s:text name="hiveAp.update.poe"/>");
	 }
	 if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE) {
	 	subArray.push("<s:text name="hiveAp.update.netdump"/>");
	 } 
	 //else {
	//	 subArray.push("<s:text name="hiveAp.update.cvg.url.menu"/>");
	 //}
	 
	 subItem2.push({text: "<s:text name="topology.menu.hiveAp.updates"/>",submenu: {id: "updates",itemdata:subArray},disabled: !writePermission});
	 tItems.push(subItem2);
	 
	 subItem2=[];
	 if (deviceModel==MODEL_BR200_LTE_VZ || deviceModel==MODEL_BR200_WP || deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P || deviceModel==MODEL_SR_48) {
	 	subItem2.push({text: "<s:text name="topology.menu.hiveAp.pse.reset"/>",disabled: !writePermission});
	 }
	 subItem2.push({text: "<s:text name="topology.menu.hiveAp.reboot"/>",disabled: !writePermission});
	 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_AG20 && deviceModel!=MODEL_AG28 
			 && deviceModel!=MODEL_170 && deviceModel!=MODEL_141 && deviceModel!=MODEL_121 && deviceModel!=MODEL_120 && deviceModel!=MODEL_110) {
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.invokeBackup"/>",disabled: !writePermission});
	 }
	 if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.locateAP"/>",disabled: !writePermission});
	 }
	 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel != MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>",disabled: !writePermission});
	 }
	 //subItem2.push({text: "<s:text name="topology.menu.hiveAp.reset.default"/>",disabled: !writePermission});
	 tItems.push(subItem2);
	 
	 
	 tItems.push([{text: "<s:text name="topology.menu.hiveAp.alarm"/>"}]);
	 
	 subItem2=[];
	 subItem2.push({text: "<s:text name="topology.menu.ssh.web.client"/>",disabled: !writePermission});
	 subItem2.push({text: "<s:text name="topology.menu.ssh.proxy.client"/>",disabled: !writePermission});
	 tItems.push(subItem2);
	 
	 //tItems.push([{text: "<s:text name="topology.menu.hiveAp.sshTunnel"/>",disabled: !writePermission}]);

	 //if (showSegMenu) {
	//	 tItems.push([{text: "<s:text name="topology.menu.syncWithSGE"/>",disabled: !writePermission}]);
	 //}
	 
	 //tItems.push([{text: "<s:text name="topology.menu.get.tech.file"/>",disabled: !writePermission}]);
	 //if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_BR200 && deviceModel!=MODEL_AG20 && deviceModel!=MODEL_AG28 
	//		 && deviceModel!=MODEL_320 && deviceModel!=MODEL_340 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_48) {
	//	 tItems.push([{text: "<s:text name="topology.menu.spectralAnalysis"/>",disabled: !writePermission}]);
	 //}
	 
	 //if(visibleCliWindow){
	//	 tItems.push([{text: "<s:text name="hiveap.tools.cliWindow.menu"/>",disabled: !writePermission}]);
	//}
	 return tItems;
}

function updateOrigin(flid, ox, oy) {
	var url = "mapBld.action?operation=updateOrigin&id="+flid+"&originX="+ox+"&originY="+oy+"&pageId="+hm.map.pageId+"&ignore="+new Date().getTime();
	YAHOO.util.Connect.asyncRequest('GET', url, { success : originUpdated }, null);
}

function showAjaxSubmitError(){
	showWarnDialog("<s:text name="error.map.object.persist.error" />", "Error");
}

function retrieveSshWebClient(){
	var leafNodeId = document.forms[formName].leafNodeId.value;
	var url="<s:url action='sshWebClient' includeParams='none'/>" + "?operation=webSsh&leafNodeId=" + leafNodeId;
	window.open(url,"","scrollbars=yes,width=700px,height=650px,resizable=yes,top=150,left=250");
}

function retrieveSshProxyClient(){
	var leafNodeId = document.forms[formName].leafNodeId.value;
	var url="<s:url action='sshClient' includeParams='none'/>" + "?operation=sshConfig&leafNodeId=" + leafNodeId;
	window.open(url,"","scrollbars=yes,width=600px,height=360px,resizable=yes,top=150,left=250");
}

var statsInterfaceOp = "<s:text name="topology.menu.statistics.interface"/>";
var showDhcpClientAllocationOp = "<s:text name="topology.menu.diagnostics.showdhcpclientallocation"/>";
var allocationNotExistMsg = "<s:text name="error.dhcp.client.allocation.notexist"/>";
var resetPseSuccessMsg = "<s:text name="info.cli.pse.reset.success"/>";
var resetPseCompleteMsg = "<s:text name="info.cli.pse.reset.complete"/>";

var diagPingOp = "<s:text name="topology.menu.diagnostics.ping"/>";
var diagTraceOp = "<s:text name="topology.menu.diagnostics.traceroute"/>";
var sipNameOp = "<s:text name="topology.menu.alg.sip.name"/>";
var lldpcdpClearOp = "<s:text name="topology.menu.lldpcdp.clear"/>";
var cwpRemoveTtl = '<s:text name="hiveAp.update.cwp.remove"/>';
var cwpRemoveSuccessTtl = '<s:text name="info.cwp.direcotry.remove.success" />';
var initMaxImageSize = <s:property value="%{imageMaxSize}" />;

</script>

<script type="text/javascript"> 
var swfu;
function initSwfUpload(){
	var settings = {
			flash_url : "<s:url value="/js/swfupload/swfupload.swf"  includeParams="none"/>",
			upload_url: "<s:property value="%{webAppHttpUrl}" />"+"<s:url value="mapUpload.action"  includeParams="none"/>",
			use_query_string: true,
			file_size_limit : "10 MB",
			file_types : "*.jpg;*.png",
			file_types_description : "Web Image Files",
			file_upload_limit : 0,
			file_queue_limit : 5,
			custom_settings : {
				JSESSIONID: "<%=request.getSession().getId()%>",
				isSecure: <%=request.isSecure()%>,
				path: "<%=request.getContextPath()%>",
				progressTarget : "fsUploadProgress",
				validateBeforeStarting: validateFileSize,
				uploadSuccessCallback: imageUploadSuccess,
				uploadErrorCallback: imageUploadFailed,
				MESSAGE_EXCEEDS_SIZE_LIMIT: "<s:text name="hm.planning.config.image.exceeds.limit" />"
			},
			debug: false,

			// Button settings
			button_width: "76",
			button_height: "24",
			button_image_url: "<s:url value="/images/upload_btns.png"  includeParams="none"/>",
			button_placeholder_id: "spanButtonPlaceHolder",
			button_window_mode: SWFUpload.WINDOW_MODE.OPAQUE,
			
			// The event handler functions are defined in handlers.js
			file_queued_handler : fileQueued,
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_start_handler : uploadStart,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess,
			upload_complete_handler : uploadComplete,
			queue_complete_handler : queueComplete	// Queue plugin event
		};
	if(document.getElementById("spanButtonPlaceHolder")) {
		swfu = new SWFUpload(settings);
	}
}
</script>

<style>
td.panelLabel {
	padding: 0px 0px 5px 8px;
	color: #003366;
}
td.panelText {
	padding: 0px 0px 5px 0px;
}
td.initNoteInfo {
	color: #003366;
}
.initStatusNote {
	background-color: #FFFFCC;
}
</style>

<div id="content">
	<s:form action="maps">
		<s:hidden name="operation" />
		<s:hidden name="menuText" />
		<s:hidden name="leafNodeId" />
		<s:hidden name="fileName" />
		<table width="100" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-bottom: 1px;"><tiles:insertDefinition
						name="mapContext" /></td>
			</tr>
			<tr id="processing" style="display: none;">
				<td style="position: relative;">
					<div
						style="position: absolute; z-index:1; opacity: 0.9; filter: alpha(opacity =   '90');">
						<table border="0" cellspacing="0" cellpadding="0"
							class="statusNote"
							style="-moz-border-radius: 5px; -webkit-border-radius: 5px;">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td class="noteInfo" id="note">Your request is being
									processed ...</td>
							</tr>
							<tr>
								<td height="6"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr id="noteSection" style="display: none;">
				<td style="position: relative;">
					<div
						style="position: absolute; z-index:1; opacity: 0.9; filter: alpha(opacity = '90');">
						<table border="0" cellspacing="0" cellpadding="0" class="statusNote" style="-moz-border-radius: 5px; -webkit-border-radius: 5px;">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td class="noteInfo" id="noteTD"></td>
								<td class="buttons">
									<div id="downloadSection" style="display: none">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" id="downloadBtn" name="ignore"
													value="Download" class="button"
													onClick="submitAction('maps', 'download');"></td>
												<td><input type="button" id="cancelBtn" name="ignore"
													value="Cancel" class="button" onClick="initNoteSection();">
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td style="padding-top: 1px; display: none;" id="gmtd"><div
						id="gmdiv"></div></td>
			</tr>
			<tr>
				<td style="padding-top: 1px;" id="cstd"><iframe id="viewport"
						name="viewport" frameborder="0"> </iframe></td>
			</tr>
		</table>
	</s:form>
</div>
<div id="detailsPanel" style="display: none;">
	<div class="hd" id="dialogTitle">Map Details</div>
	<div class="bd">
		<s:form action="mapSettings">
			<s:hidden name="operation" />
			<s:hidden name="selectedMapId" />
			<s:hidden name="mapType" />
			<s:hidden name="id" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td style="padding: 6px 5px 0 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr id="trname">
											<td class="labelT1" nowrap><s:text
													name="topology.map.name" /><font color="red"><s:text
														name="*" /></font></td>
											<td style="padding-left: 10px;"><s:textfield
													id="mapName" size="25" name="mapName"
													onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
													maxlength="32" /></td>
											<td width="100%"></td>
										</tr>
										<tr id="trpre">
											<td class="labelT1" nowrap>Floor Names Prefix</td>
											<td style="padding-left: 10px;"><input type="text"
												name="prefix" size="25" maxlength="31" value="" id="prefix"
												onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" /></td>
										</tr>
										<tr id="trprnt">
											<td class="labelT1" nowrap>Folder</td>
											<td style="padding-left: 10px;"><select id="region"
													name="region" style="width: 260px;" ></select></td>
										</tr>
										<tr id="tricn">
											<td class="labelT1"><s:text name="topology.map.icon" /></td>
											<td style="padding-left: 10px;"><select id="mapIcon"
												name="mapIcon" style="width: 158px;"></select></td>
										</tr>
										<tr id="trenv">
											<td class="labelT1"><s:text
													name="topology.map.environment" /></td>
											<td style="padding-left: 10px;"><s:select id="mapEnv"
													name="mapEnv" list="%{enumMapEnv}" listKey="key"
													listValue="value" cssStyle="width: 158px;" /></td>
										</tr>
										<tr id="tratt">
											<td class="labelT1" nowrap><s:text
													name="hm.planning.config.floorAttenuation" /></td>
											<td style="padding-left: 10px;"><s:textfield
													id="attenuation" size="25" name="loss" maxlength="16"
													onkeypress="return hm.util.keyPressPermit(event,'tendot');" />&nbsp;dB</td>
										</tr>
										<tr id="trimg">
											<td class="labelT1" nowrap><s:text
													name="topology.map.background.image" /></td>
											<td style="padding-left: 10px;"><select id="mapImage"
												name="mapImage" style="width: 158px;"
												onchange="toggleMapImage(this)"
												onkeyup="toggleMapImage(this)"></select></td>
										</tr>
										<tr id="sizeImg">
											<td class="labelT1" nowrap>Map Width (optional)</td>
											<td style="padding-left: 10px;"><s:textfield
													id="mapWidth" size="25" name="mapWidth" maxlength="16"
													onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
												<select id="mapWidthUnit" name="mapWidthUnit"
												onchange="toggleWidthUnit(this)"
												onkeyup="toggleWidthUnit(this)">
													<option value="1">meters</option>
													<option value="2">feet</option>
											</select></td>
											<td width="100%"></td>
										</tr>
										<tr id="sizeBlank" style="display: none">
											<td class="labelT1" nowrap>Map Size</td>
											<td style="padding-left: 10px;" nowrap><s:textfield
													id="sizeX" size="8" name="sizeX" maxlength="16"
													onkeypress="return hm.util.keyPressPermit(event,'tendot');" />&nbsp;x&nbsp;<s:textfield
													id="sizeY" size="8" name="sizeY" maxlength="16"
													onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
												<select id="mapBlankUnit" name="mapBlankUnit"
												onchange="toggleBlankUnit(this)"
												onkeyup="toggleBlankUnit(this)">
													<option value="1">meters</option>
													<option value="2">feet</option>
											</select></td>
											<td width="100%"></td>
										</tr>
										<tr id="trinst">
											<td class="labelT1" nowrap><s:text
													name="hm.planning.config.installationHeight" /></td>
											<td style="padding-left: 10px;" nowrap><s:textfield
													id="apElevation" size="25" name="apElevation"
													maxlength="16"
													onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
												<select id="apElevationUnit" name="apElevationUnit"
												onchange="toggleApElevationUnit(this)"
												onkeyup="toggleApElevationUnit(this)">
													<option value="1">meters</option>
													<option value="2">feet</option>
											</select></td>
											<td width="100%"></td>
										</tr>
										<tr id="traddr" style="display: none">
											<td class="labelT1" nowrap><s:text
													name="topology.map.address" /></td>
											<td style="padding-left: 10px;" colspan="2"><s:textfield
													id="mapAddress" size="65" name="mapAddress"
													onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
													maxlength="100" /></td>
											<td width="100%"></td>
										</tr>
										<tr>
											<td height="6px"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td id="createTd" style="display: none;"><input
									type="button" name="ignore" value="Create" class="button"
									onClick="submitAction('mapSettings','createMap');"></td>
								<td id="updateTd" style="display: none;"><input
									type="button" name="ignore" value="Update" class="button"
									onClick="submitAction('mapSettings','updateMap');"></td>
								<td id="moveTd" style="display: none;"><input type="button"
									name="ignore" value="Move" class="button"
									onClick="submitAction('mapSettings','moveMap');"></td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button"
									onClick="submitAction('mapSettings','cancel_map');"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>

<div id="importXmlPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.operation.importxml.title" />
	</div>
	<div class="bd">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="noteInfo"><label><s:text
							name="topology.menu.operation.importxml.note" /></label></td>
			</tr>
			<tr id="fe_ErrorMsgRow" style="display: none">
				<td class="noteError" colspan="4"><span id="textfe_ErrorMsgRow">To be changed</span></td>
			</tr>
			<tr>
				<td><s:form action="xmlUpload" enctype="multipart/form-data"
						method="post">
						<s:hidden name="operation" value="uploadPlanningData" />
						<s:hidden name="id" />
						<table width="100%">
							<tr>
								<td><s:file name="uploadXml" label="File" size="44"/></td>
							</tr>
							<tr id="overrideBgRow" style="display: none;">
							<td><input type="checkBox" name="overrideBg" value="true" id="overrideBgChk"/>
							<label for="overrideBgChk"><s:text name="topology.menu.operation.override.images"/></label></td>
							</tr>
							<tr>
								<td style="padding: 10px 0 5px;"><input type="button"
									name="ignore" value="Upload" class="button"
									onclick="uploadXmlPlanningData();" /> <input type="button"
									name="ignore" value="Cancel" class="button"
									onclick="uploadXmlPanel.hide();" /></td>
							</tr>
						</table>
					</s:form></td>
			</tr>
		</table>
	</div>
</div>

<div id="imageReviewPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.adddelete.image" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td><select size="10" id="mapReviewImage"
											name="mapReviewImage" style="width: 150px;"
											onchange="showImage(this);"></select></td>
										<td id="imageContent"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<s:if test="%{!writeDisable4Struts}">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td><s:form action="mapUpload" enctype="multipart/form-data"
									method="post">
									<s:hidden name="operation" />
									<s:hidden name="domainMapId" />
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0"
													width="100%">
													<tr>
														<td>
															<div class="flash" id="fsUploadProgress"></div> <!--<div id="divStatus">0 Files Uploaded</div> -->
														</td>
													</tr>
													<tr>
														<td>
															<div>
																<span id="spanButtonPlaceHolder"></span>&nbsp;<input
																	type="button" name="ignore" value="Delete"
																	id="imageDeletion" class="button"
																	onClick="deleteImage();">
																	<%--<input type="button"
																	name="ignore" value="Close Window" class="button"
																	style="width: 95px;"
																	onClick="submitAction('mapUpload','cancel_review');"> --%>
															</div>
														</td>
													</tr>
													<tr id="traditionalTr1">
														<td style="height: 24px;"><a href="##"
															onclick="showTraditionalContent();"><s:text
																	name="hm.topology.init.map.background.traditional" /></a></td>
													</tr>
													<tr id="traditionalTr2" style="display: none;">
														<td>
															<div
																style="background-color: #ddd; border: 1px solid #888; padding: 2px; margin-top: 5px;">
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%">
																	<tr>
																		<td></td>
																		<td class="noteInfo"><s:text
																				name="hm.topology.init.map.background.note" /></td>
																	</tr>
																	<tr>
																		<td></td>
																		<td><s:file id="imagedata"
																				onchange="traditionalUpoad();" name="imagedata"
																				size="42px" /></td>
																	</tr>
																</table>
															</div>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</s:form></td>
						</tr>
					</table>
				</td>
			</tr>
			</s:if>
		</table>
	</div>
</div>

<div id="globalPanel" style="display: none;">
	<div class="hd">Map Global Settings</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td style="padding: 6px 0px 6px 10px;" class="textCap"><s:text
												name="topology.map.refresh.time" /><font color="red"><s:text
													name="*" /></font></td>
										<td width="10"></td>
										<td style="padding-bottom: 2px;"><s:textfield
												name="refreshInterval" size="6" maxlength="4"
												id="refreshInterval"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="topology.map.refresh.time.range" /></td>
									</tr>
									<tr>
										<td nowrap="nowrap" style="padding: 7px 0px 6px 10px;" class="textCap"><s:text
												name="topology.map.resolution" /></td>
										<td></td>
										<td><select id="heatmapResolution"
											name="heatmapResolution" style="width: 80px;">
												<option value="1">Auto</option>
												<option value="2">Low</option>
												<option value="3">Medium</option>
												<option value="4">High</option>
										</select></td>
									</tr>
									<tr>
										<td nowrap="nowrap" style="padding: 7px 0px 6px 10px;" class="textCap"><s:text
												name="topology.map.rssiThreshold" /></td>
										<td></td>
										<td style="padding: 0px 0 0 0;"><s:select
												id="clientRssiThreshold" name="clientRssiThreshold"
												list="%{rssiThresholdValues}" listKey="id" listValue="value" />&nbsp;dBm</td>
									</tr>
									<tr>
										<td nowrap="nowrap" style="padding: 7px 0px 6px 10px;" class="textCap"><s:text
												name="topology.map.calibrateRange" /></td>
										<td></td>
										<td style="padding: 0px 0 0 0;"><s:select
												id="calibrateRssiUntil" name="calibrateRssiUntil"
												list="%{rssiThresholdValues}" listKey="id" listValue="value" />&nbsp;dBm&nbsp;&nbsp;&nbsp;to&nbsp;&nbsp;&nbsp;<s:select
												id="calibrateRssiFrom" name="calibrateRssiFrom"
												list="%{rssiThresholdValues}" listKey="id" listValue="value" />&nbsp;dBm</td>
									</tr>
									<tr>
										<td colspan="3" style="padding: 7px 0px 5px 10px;" nowrap class="textCap"><s:text
												name="topology.map.bg.opacity" />&nbsp;<s:select
												id="bgMapOpacity" name="bgMapOpacity"
												list="%{opacityValues}" listKey="id" listValue="value" />&nbsp;%&nbsp;&nbsp;&nbsp;coverage&nbsp;<s:select
												id="heatMapOpacity" name="heatMapOpacity"
												list="%{opacityValues}" listKey="id" listValue="value" />&nbsp;%&nbsp;&nbsp;&nbsp;walls&nbsp;<s:select
												id="wallsOpacity" name="wallsOpacity"
												list="%{opacityValues}" listKey="id" listValue="value" />&nbsp;%</td>
									</tr>
									<tr>
										<td colspan="3" width="100%">
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td nowrap="nowrap" style="padding: 6px 5px 5px 10px;">Minimum</td>
													<td style="padding: 2px 5px 0px 0px;"><select
														id="minRssi" name="minRssi" style="width: 40px;">
															<option value="2">2</option>
															<option value="3">3</option>
															<option value="4">4</option>
															<option value="5">5</option>
													</select></td>
													<td nowrap="nowrap" style="padding-top: 4px;" width="100%"><s:text
															name="topology.map.minRssi" />.</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td colspan="3" width="100%">
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td style="padding: 4px 2px 0px 5px;"><input
														type="radio" id="rtYes" name="realTime" value="true"
														checked="checked" onclick="toggleRealTime(this)" /></td>
													<td colspan="3" style="padding-top: 5px;"><s:text
															name="topology.map.realTime" />.</td>
												</tr>
												<tr>
													<td style="padding: 1px 2px 0px 5px;"><input
														type="radio" id="rtNo" name="realTime" value="false"
														onclick="toggleRealTime(this)" /></td>
													<td nowrap="nowrap" style="padding-top: 2px;"><s:text
															name="topology.map.locationWindow" /></td>
													<td style="padding: 2px 4px 0px 5px;"><s:textfield
															name="locationWindow" size="2" maxlength="2"
															cssStyle="width: 20px;" id="locationWindow"
															disabled="true"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
													<td nowrap="nowrap" style="padding-top: 1px;" width="100%">minutes.</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td class="sepLine" colspan="3"><img
											src="<s:url value="/images/spacer.gif"/>" height="1"
											class="dblk" /></td>
									</tr>
									<tr>
										<td height="4px"></td>
									</tr>
									<tr>
										<td colspan="3" style="padding-left: 4px">
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td width="21px" style="padding-bottom: 1px;"><s:checkbox
															id="useStreetMaps" name="useStreetMaps" onclick="regAuthUrl(this)" /></td>
													<td><s:text name="topology.map.useStreetMaps.flag" /></td>
												</tr>
												<tr>
													<td width="21px" style="padding-bottom: 1px;"><s:checkbox
															id="popUpFlag" name="popUpFlag" /></td>
													<td><s:text name="topology.map.popUpSummary.flag" /></td>
												</tr>
												<tr>
													<td style="padding-bottom: 1px;"><s:checkbox
															id="showRssi" name="showRssi" /></td>
													<td><s:text name="topology.map.linkRssi.flag" /></td>
												</tr>
												<tr>
													<td style="padding-bottom: 1px;"><s:checkbox
															id="showOnHover" name="showOnHover" /></td>
													<td><s:text name="topology.map.hoverAp.flag" /></td>
												</tr>
												<tr>
													<td style="padding-bottom: 1px;"><s:checkbox
															id="calibrateHeatmap" name="calibrateHeatmap" /></td>
													<td><s:text name="topology.map.calibrate" /></td>
												</tr>
												<tr>
													<td style="padding-bottom: 1px;"><s:checkbox
															id="useHeatmap" name="useHeatmap" /></td>
													<td><s:text name="topology.map.useHeatmap" /></td>
												</tr>
												<%--
  --%>
												<tr>
													<td style="padding-bottom: 1px;"><s:checkbox
															id="periVal" name="periVal" /></td>
													<td><s:text name="topology.map.periVal" /></td>
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
				<td style="padding-top: 8px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" id="intervalSet" name="ignore"
								value="Update" class="button"
								onClick="setGlobalParams('<s:text name="topology.map.refresh.time" />');"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button"
								onClick="submitAction('mapSettings','cancel_global');"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="clientInfoPanel" style="display: none;">
	<div class="hd" id="clientTitle">
		<s:text name="topology.menu.client.information" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td><label id="clientInfoLabel"></label></td>
				<td>
					<div
						style="height: 300px; width: 600px; overflow-x: scroll; overflow-y: scroll;">
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%" id="clientInfoTable">
							<tr>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.macAddress" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.ipAddress" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.hostname" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.apName" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.connectionTime" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.rssi" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.authenticationMethod" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.encryptionMethod" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.cwpUsed" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.radioMode" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.ssid" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.vlan" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.userProfile" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.channel" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.transmissionRate" /></th>
							</tr>
							<s:generator separator="," val="%{' '}" count="12">
								<s:iterator>
									<tr>
										<td class="list" colspan="16">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="neighborInfoPanel" style="display: none;">
	<div class="hd" id="neighborTitle">
		<s:text name="topology.menu.neighbor.information" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td><label id="neighborInfoLabel"></label></td>
				<td>
					<div
						style="height: 300px; width: 600px; overflow-x: scroll; overflow-y: scroll;">
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%" id="neighborInfoTable">
							<tr>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.neighborHostname" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.neighborNodeId" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.connectionTime" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.linkCost" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.rssi" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.linkType" /></th>
							</tr>
							<s:generator separator="," val="%{' '}" count="12">
								<s:iterator>
									<tr>
										<td class="list" colspan="6">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</div>
<div id="networkSummaryPanel" style="display: none;">
	<div class="hd" id="summaryTitle">
		<s:text name="topology.menu.network.summary" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" nowrap style="padding-right: 10px;"><s:text
												name="topology.map.networkSummary.totalManagedAp" /></td>
										<td>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td id="upManagedHiveApTd">_</td>
													<td><FONT color='green'><b><s:text
																	name="topology.map.networkSummary.totalAp.up" /></b></FONT></td>
													<td id="downManagedHiveApTd">_</td>
													<td><FONT color='red'><b><s:text
																	name="topology.map.networkSummary.totalAp.down" /></b></FONT></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td class="labelT1" nowrap style="padding-right: 10px;"><s:text
												name="topology.map.networkSummary.totalNewAp" /></td>
										<td>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td id="upNewHiveApTd">_</td>
													<td><FONT color='green'><b><s:text
																	name="topology.map.networkSummary.totalAp.up" /></b></FONT></td>
													<td id="downNewHiveApTd">_</td>
													<td><FONT color='red'><b><s:text
																	name="topology.map.networkSummary.totalAp.down" /></b></FONT></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td class="sepLine" colspan="3"><img
											src="<s:url value="/images/spacer.gif"/>" height="1"
											class="dblk" /></td>
									</tr>
									<tr>
										<td class="labelT1" width="200px"><s:text
												name="topology.map.networkSummary.totalClient" /></td>
										<td id="activeClientTd">_</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<div id="cliInfoPanel" style="display: none;">
	<div class="hd" id="cliInfoTitle">Dialog</div>
	<div class="bd">
		<div id="bd_top" class="bd_top">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr id="pingTr" style="display: none;">
					<td class="labelT1" width="100px" style="padding-right: 10px;"><s:text
							name="topology.menu.diagnostics.ping.name" /></td>
					<td><s:textfield id="targetIp" maxlength="15" size="15px" /></td>
					<td><input type="button" id="ping" name="ignore" value="Ping"
						class="button" onClick="ping();"></td>
				</tr>
				<tr id="interfaceTr" style="display: none;">
					<td class="labelT1" width="100px"><s:text
							name="topology.map.statistics.interface.name" /></td>
					<td><s:select id="interfaceType" list="%{enumInterfaceType}"
							listKey="key" listValue="value" cssStyle="width: 80px;"
							onchange="retrieveIfDetail();" /></td>
				</tr>
				<tr id="sipTr" style="display: none;">
					<td class="labelT1" width="100px"><s:text
							name="topology.menu.alg.sip.label" /></td>
					<td><s:textfield id="callId" maxlength="128" size="64"
							onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
					<td><input type="button" id="sipCall" name="ignore" value="OK"
						class="button" onClick="sipCalls();"></td>
				</tr>
			</table>
		</div>
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft"></div>
</div>
<div id="apInfoPanel" style="display: none;">
	<div class="hd" id="apInfoTitle">
		<s:text name="report.summary.widgetitle.apInfo" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%"
			class="settingBox">
			<tr>
				<td height="3"></td>
			</tr>
			<tr>
				<td colspan="2"><label id="apInfoLabel"></label></td>
			</tr>
			<tr>
				<td class="panelLabel" width="180px"><s:text
						name="hiveAp.hostName" /></td>
				<td class="panelText" id="apName">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.hiveAp.ipAddress" /></td>
				<td class="panelText" id="apIp">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.hiveAp.macAddress" /></td>
				<td class="panelText" id="apMac">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.hiveAp.location" /></td>
				<td class="panelText" id="apLocation">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
						name="monitor.hiveAp.clientCount" /></td>
				<td class="panelText" id="apClientCount">.</td>
			</tr>
			<tr id="ssidInfoRow">
				<td class="panelLabel" valign="top"><s:text
						name="monitor.hiveAp.activeSsid" /> &nbsp;&nbsp;&nbsp; [<a
					href="#synchronizeSsidInfo" onclick="synchronizeSsidInfo();"><s:text
							name="monitor.hiveAp.ssidRefresh" /></a>]</td>
				<td class="panelText" id="apActiveSsid">.</td>
			</tr>
			<tr>
				<td class="panelText" height="4"></td>
			</tr>
			<tr>
				<td class="labelT1" colspan="2"><a href="#hiveApDetails"
					onclick="hiveApDetails();"><s:text name="monitor.hiveAp.more" /></a></td>
			</tr>
		</table>
	</div>
</div>

<div id="lldpCdpPanel" style="display: none;">
	<div class="hd" id="lldpCdpTitle">
		<s:text name="topology.menu.lldpcdp.on" />
	</div>
	<div class="bd">
		<s:form action="mapNodes" name="lldpCdpForm">
			<s:hidden name="operation" />
			<s:hidden name="leafNodeId" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td width="90px" class="labelT1"><s:checkbox name="chkLldp"
										id="chkLldp" onclick="clickLldpCdp('lldp',this.checked);" />
									<s:text name="topology.menu.lldpcdp.lldp" /></td>
								<td class="labelT1"><s:checkbox name="chkCdp" id="chkCdp"
										onclick="clickLldpCdp('cdp',this.checked);" />
									<s:text name="topology.menu.lldpcdp.cdp" /></td>
							</tr>
							<tr>
								<td height="10"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="OK"
									class="button" onClick="requestLldpCdpAction();" /></td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideLldpCdpPanel();" /></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div id="cwpRemovePanel" style="display: none;">
	<div class="hd" id="cwpRemoveTitle">
		<s:text name="hiveAp.update.cwp.remove" />
	</div>
	<div class="bd">
		<s:form action="mapNodes" id="cwpRemoval" name="cwpRemoval">
			<s:hidden name="operation" />
			<s:hidden name="leafNodeId" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td><label id="cwpRemoveLabel"></label></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td>
									<div id="cwpSelection" style="margin: 5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td width="250px"><s:radio
														list="#{'removeAll':'Remove All Web Page Directories'}"
														label="Gender" name="cwpDirectory"
														onclick="disableDirs();" /></td>
												<td><s:radio
														list="#{'remove':'Remove Specific Web Page Directory'}"
														label="Gender" name="cwpDirectory"
														onclick="retrieveDirs();" /></td>
											</tr>
										</table>
									</div>
									<div id="cwpDirectories" style="margin: 5px;"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="Submit"
									class="button"
									onClick="submitAction('cwpRemoval','removeCwpDirectory');"></td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideCwpRemovePanel();"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div id="imageBootPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.invokeBackup" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="noteError">
												<s:label id="imageBootPanelMsg"></s:label>
											</td>
										</tr>
										<tr>
											<td><s:radio list="#{'current':'Current HiveOS Image'}"
													label="Gender" name="imageBoot" /></td>
										</tr>
										<tr>
											<td height="5px"></td>
										</tr>
										<tr>
											<td><s:radio list="#{'backup':'Backup HiveOS Image'}"
													label="Gender" name="imageBoot" /></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Submit" id="imageBootPanelSubmit"
								class="button"
								onClick="openConfirmBootDialog(hiveApInvokeBackupOp);"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideImageBootPanel();"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<%--for SSH Tunnel panel--%>
<div id="sshTunnelPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.sshTunnel" />
	</div>
	<div class="bd">
		<iframe id="ssh_tunnel" name="ssh_tunnel" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var toolkitAction = "<s:url value='hiveApToolkit.action' includeParams='none' />";
function updateSshTunnelPanelTitle(str){
	if(null != sshTunnelPanel){
		sshTunnelPanel.header.innerHTML = "<s:text name="topology.menu.hiveAp.sshTunnel"/>"+" - "+str;
	}
}
</script>
<%--for packet capture panel--%>
<div id="packetCapturePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.packetCapture" />
	</div>
	<div class="bd">
		<iframe id="packetCaptureFrame" name="packetCaptureFrame" width="0"
			height="0" frameborder="0" src=""> </iframe>
	</div>
</div>
<%--for remote sniffer panel--%>
<div id="remoteSnifferPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.remoteSniffer" />
	</div>
	<div class="bd">
		<iframe id="remoteSnifferFrame" name="remoteSnifferFrame" width="0"
			height="0" frameborder="0" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
function updateRemoteSnifferTitle(str) {
	if(null != remoteSnifferPanel){
		remoteSnifferPanel.header.innerHTML = "<s:text name="topology.menu.remoteSniffer"/>" + " - " + str;
	}
}
</script>

<div id="debugClientPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.clientTrace" />
	</div>
	<div class="bd">
		<iframe id="debug_client" name="debug_client" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
<%--
function debugClientPanelResizeCallback(){/**/}
function createDebugClientResizer() {
	var resize = createResizer("debugClientPanel");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, debugClientPanel, true);
	resize.on("endResize", function(args){
		debugClientPanelResizeCallback();
	}, debugClientPanel, true);
}
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 780,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
--%>
function updateClientDebugPanelTitle(str){
	if(null != debugClientPanel){
		debugClientPanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.clientTrace"/>"+" - "+str;
	}
}
</script>
<div id="vlanProbePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.vlan.probe" />
	</div>
	<div class="bd">
		<iframe id="vlan_probe" name="vlan_probe" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
function updateVlanProbePanelTitle(str){
	if(null != vlanProbePanel){
		vlanProbePanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.vlan.probe"/>"+" - "+str;
	}
}
</script>
<div id="pathProbePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.path.probe" />
	</div>
	<div class="bd">
		<iframe id="path_probe" name="path_probe" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
function updatePathProbePanelTitle(str){
	if(null != pathProbePanel){
		pathProbePanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.path.probe"/>"+" - "+str;
	}
}
</script>
<div id="moveAPsPanel" style="display: none;">
	<div class="hd">Devices</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td><b>Available Devices</b></td>
				<td></td>
				<td><b id="dvsLbl"></b></td>
			</tr>
			<tr>
				<td height="3"></td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><select multiple="multiple" id="leftAPs" size="12"
											style="width: 190px;"></select></td>
						</tr>
					</table>
				</td>
				<td valign="center" style="padding: 0 5px 0 5px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><input type="button" class="transfer" value="&gt;"
											onclick="moveApListItems(document.getElementById('leftAPs'), document.getElementById('rightAPs'));" />
							</td>
						</tr>
						<tr>
							<td><input type="button" class="transfer" value="&lt;"
											onclick="moveApListItems(document.getElementById('rightAPs'), document.getElementById('leftAPs'));" />
							</td>
						</tr>
					</table>
				</td>
				<td style="padding-right: 4px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><select multiple="multiple" id="rightAPs" size="10"
											style="width: 190px;"></select></td>
						</tr>
						<tr>
							<td style="padding-top: 6px;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><input type="button" name="ignore" value="Update" id="updateApList"
														class="button" onClick="updateApListItems();"></td>
										<td><input type="button" name="ignore" value="Reset" id="resetApList"
														class="button" onClick="resetApListItems();"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="multicastMonitorPanel" style="display: none;">
	<div class="hd"><s:text name="topology.menu.diagnostics.showmulticastmonitor"/></div>
	<div class="bd">
		<iframe id="multicastMonitorFrame" name="multicastMonitorFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
function updateMulticastMonitorTitle(str) {
	if(null != multicastMonitorPanel){
		multicastMonitorPanel.header.innerHTML = "<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>" + " - " + str;
	}
}
</script>

<div id="fwPolicyRulePanel" style="display: none;">
	<div class="hd" id="fwPolicyRuleTitle">
		<s:text name="topology.menu.firewall.policy" />
	</div>
	<div class="bd">
		<iframe id="fwPolicyRuleFrame" name="fwPolicyRuleFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>

<div id="locateAPPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.locateAP" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" id="noLedSection" colspan="10"><s:checkbox
													name="noLed" id="noLed"
													onclick="selectNoLed(this.checked);" /> <s:text
													name="topology.menu.hiveAp.locateAP.noLed" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="80px"><s:text
													name="topology.menu.hiveAp.locateAP.color" /></td>
											<td width="130px">
											<select name="locateAPColorMore" id="locateAPColorMore" style="width: 100px;"
												onchange="locateAPColorMoreChange();">
											</select>
											<select name="locateAPColorLess" id="locateAPColorLess"
												style="width: 100px;" onchange="locateAPColorLessChange();">
													<option value="green" style="background-color: #00FF00"
														selected="selected">Green</option>
													<option value="red" style="background-color: #FF0000">Red</option>
													<option value="orange" style="background-color: #FF6000">Orange</option>
													<option value="off" style="background-color: #FFFFFF">Off</option>
											</select>
											</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
													name="topology.menu.hiveAp.locateAP.blinkMode" /></td>
											<td><select name="locateAPBlink" id="locateAPBlink"
												style="width: 100px;">
													<option value="fast-blink" selected="selected">Fast</option>
													<option value="slow-blink">Slow</option>
													<option value="no-blink">Steady</option>
											</select></td>
										</tr>
									</table>
								</div></td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Submit"
								class="button" onClick="submitLocateAP();">
							</td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideLocateAPPanel();">
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
	</div>
</div>


