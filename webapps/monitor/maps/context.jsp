<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.bo.admin.PlanToolConfig"%>

<script>
var RADIO_24GHZ = <%=PlanToolConfig.RADIO_24GHZ%>;
var RADIO_5GHZ = <%=PlanToolConfig.RADIO_5GHZ%>;

var selectedLayers = 1; // <s:property value="selectedLayers" />;
var mapWidthSizeText = "<s:text name="info.map.width.size"></s:text>";
var folderNoMapText = '<s:text name="info.map.folder.nomap"></s:text>';
var floorNoMapText = '<s:text name="info.map.floor.nomap"></s:text>';
var buildingNodeText = '<s:text name="info.map.building"></s:text>';
var imagesBaseUrl = "<s:url value="/images/" includeParams="none"/>";
var fullScreenMode = <s:property value="%{fullScreenMode}" />;
var mapsActionOperationUrl = "<s:url value="maps.action" includeParams="none"></s:url>" + "?operation=";
var autoPlacementMsg = "<s:text name="info.map.autoPlacement"></s:text>";
var heatMissingRssiText = '<s:text name="error.heatMap.missingRssi" />';
var heatMissingRssiMoreText = '<s:text name="error.heatMap.missingRssiMore" />';
var xmlUploadUrl = "<s:url action="xmlUpload" includeParams="none"/>";

function onMapOpsItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	// if menu item is disabled, do nothing.
	if (menuItem.cfg.getProperty("disabled") == true) {
		return;
	}
	if (selectedNode != null){
		var mapName = selectedNode.label;
		mapContainerId = selectedNode.data.id;
	}
	var text = menuItem.cfg.getProperty("text");
	if (!hm.map.bldMode()) {
		if (text != "<s:text name="topology.menu.operation.resize"/>"
			&& text != "<s:text name="topology.menu.operation.size"/>") {
			cancelSizeMap();
		}
		cancelWallOps();
	}
	if (text == "<s:text name="topology.menu.operation.resize"/>"
			|| text == "<s:text name="topology.menu.operation.size"/>") {
		showSizingTool();
	} else if (text == "<s:text name="topology.menu.operation.place"/>") {
		hm.map.showMeasuringTool();
		document.getElementById("measuringTool").checked = true;
	} else if (text == "<s:text name="topology.menu.operation.place.hide"/>") {
		cancelMeasuring();
	} else if (text == "<s:text name="topology.menu.operation.snap"/>") {
		var movedNode = null;
		if (movedNodeId != null) {
			movedNode = hm.map.snapNodeToGrid(movedNodeId);
			if (movedNode == null) {
				movedNode = hm.map.snapSimNodeToGrid(movedNodeId);
			}
		}
		if (movedNode == null) {
			showNote("Please move an AP close to the crosshair before invoking this operation.", 4);
		}
		movedNodeId = null;
	} else if (text == '<s:text name="topology.menu.operation.export2pdf"/>') {
		showNote("Creating PDF...", 1800);
		submitAction('maps','createDownloadData');
	} else if (text == '<s:text name="topology.menu.troubleshoot.clientTrace"/>') {
		if(mapContainerId != null ){
			openClientTracePanel(mapContainerId, null);
		}
	} else if (text == '<s:text name="topology.menu.planner.setting"/>') {
		openPlanningPanel();
	} else if (text == '<s:text name="topology.menu.global.setting"/>') {
		if(mapContainerId != null ){
			editGlobalParams(mapContainerId);
			globalPanel.cfg.setProperty('visible', true);
			document.getElementById("refreshInterval").focus();
		}
	} else if (text == '<s:text name="topology.menu.network.summary"/>') {
		if(mapContainerId != null && mapName != null){
			displaySummaryPanel(mapContainerId, mapName);
		}
	} else if (text == '<s:text name="topology.menu.tree.width"/>') {
		hm.map.showTreeWidthPanel('mapOpsButton');
	} else if (text == '<s:text name="topology.menu.adddelete.image"/>') {
		showImageReviewPanel();
	} else if (text == '<s:text name="topology.menu.operation.add.map"/>') {
		prepareMapNew(mapName, 1);
	} else if (text == '<s:text name="topology.menu.operation.add.building"/>') {
		prepareMapNew(mapName, 2);
	} else if (text.match('^'+addFloorOp)) {
		prepareMapNew(mapName, 3, selectedNode.data.tp);
	} else if (text.match('^'+addGroupOp)) {
		prepareMapNew(mapName, 99, selectedNode.data.tp);
	} else if (text == 'Edit') {
		prepareMapEdit(mapName, selectedNode.data.tp);
	} else if (text == "Move to Folder"){
		prepareMapMove(mapName, selectedNode);
	} else if (text == "Move Up"){
		prepareFloorMove(selectedNode, moveFloorUp, true);
	} else if (text == "Move Down"){
		prepareFloorMove(selectedNode, moveFloorDown, false);
	} else if (text == 'Clone') {
		prepareMapClone(mapName, selectedNode);
	} else if (text == 'Remove') {
		removeMapContainer(mapName);
	} else if (text == '<s:text name="topology.menu.operation.exportPlanData"/>') {
		requestPlanningData(mapContainerId);
	} else if (text == '<s:text name="topology.menu.operation.importPlanData"/>') {
		displayUploadXmlPanel(mapContainerId);
	}
}

function toggleUnsizedItems(ah){
	var item = getItem(mapOpsMenu, '<s:text name="topology.menu.operation.show"/>');
	if(item){item.cfg.setProperty("disabled", false);}
}
function disableShowFunction(flag){
	var item = getItem(mapOpsMenu, '<s:text name="topology.menu.operation.show"/>');
	if(item){item.cfg.setProperty("disabled", flag);}
}
var walltype;
var wallTypes = <s:property value="wallTypes" escape="false" />;
var perimTypes = <s:property value="perimTypes" escape="false" />;
function addMapOpsMenuItems() {
	mapOpsMenu.addItems(
		[ [ { text: '<s:text name="topology.menu.operation.add.map"/>' },
	        { text: '<s:text name="topology.menu.operation.add.building"/>' },
	        { text: 'Edit' },
	        { text: 'Move to Folder' },
	        { text: 'Clone' },
	        { text: 'Move Up' },
	        { text: 'Move Down' },
	        { text: 'Remove' } ],
	    <s:if test="%{!planningOnly}">
	      [ { text: '<s:text name="topology.menu.operation.show"/>',
	          submenu:
		          { id: "mapOpsShow",
	     			itemdata:
		     			[ { text: '<img class="dinl" src="<s:url value="/images/radius_server.png" includeParams="none" />" /> <s:text name="hiveAp.server.label.radius"/>',
	     				    onclick: {fn: onServerMenuItemClick, obj: ".radiusImg"} },
	     			      { text: '<img class="dinl" src="<s:url value="/images/radius_proxy_server.png" includeParams="none" />" /> <s:text name="hiveAp.server.label.radius.proxy"/>',
	     				    onclick: {fn: onServerMenuItemClick, obj: ".radiusProxyImg"} },
	     				  { text: '<img class="dinl" src="<s:url value="/images/dhcp_server.png" includeParams="none" />" /> <s:text name="hiveAp.server.label.dhcp"/>',
	     				    onclick: {fn: onServerMenuItemClick, obj: ".dhcpImg"} },
	     				  { text: '<img class="dinl" src="<s:url value="/images/vpn_server_up.png" includeParams="none" />" /> <s:text name="hiveAp.server.label.vpn"/>',
	     				    onclick: {fn: onServerMenuItemClick, obj: ".vpnImg"} },
	     				  { text: '<img class="dinl" src="<s:url value="/images/vpn_client_up.png" includeParams="none" />" /> <s:text name="hiveAp.client.label.vpn"/>',
	     				    onclick: {fn: onServerMenuItemClick, obj: ".vpnImgClient"} }
	     				]
	              }
            }
	      ],
		</s:if>
	      [ { text: '<s:text name="topology.menu.operation.export2pdf"/>' }, 
	        { text: '<s:text name="topology.menu.operation.exportPlanData"/>' },
	        { text: '<s:text name="topology.menu.operation.importPlanData"/>' }],
	    <s:if test="%{planningOnly}">
		  [ { text: '<s:text name="topology.menu.planner.setting"/>' },
	     	{ text: '<s:text name="topology.menu.adddelete.image"/>' },
	     	{ text: '<s:text name="topology.menu.tree.width"/>' } ]
		</s:if>
		<s:else>
	      [ { text: '<s:text name="topology.menu.global.setting"/>' },
	     	{ text: '<s:text name="topology.menu.adddelete.image"/>' },
	     	{ text: '<s:text name="topology.menu.tree.width"/>' } ],
	      [ { text: '<s:text name="topology.menu.troubleshoot.clientTrace"/>' },
	     	{ text: '<s:text name="topology.menu.network.summary"/>' } ]
        </s:else>
	    ]);
}

function requestPlanHeatImg(frequency) {
	<%--
	prepareHeatImg();
	var url = "<s:url value="maps.action" includeParams="none"><s:param name="operation" value="%{'planHeatMap'}"/></s:url>&id=" + hm.map.mapId + "&ignore=" + new Date().getTime() +
			"&rssiThreshold=" + document.forms[formName].rssiThreshold.value + "&frequency=" + frequency + "&scale=" + hm.map.scale +
			"&pageId=" + hm.map.pageId + "&canvasWidth=" + (canvasWidth-gridBorderX) + "&canvasHeight=" + (canvasHeight-gridBorderY);
	var heatDiv = hm.map.canvasWindow.document.getElementById("heat");
	heatDiv.style.display = "";
	var img = heatDiv.firstChild;
	img.onload = planHeatImgLoaded;
	img.src = url;
  --%>
}
</script>
<style>
input.pckcb {
	height: 16px;
	margin: 1px 0 0 2px;
}

input.pckcbie {
	height: 16px;
	padding-top: 1px;
	padding-bottom: 0;
}

input.pckcbie16 {
	height: 16px;
	padding: 2px 0 0 1px;
}

.pckul {
	border-bottom: 1px solid #cccccc;
}

.rtsls {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 9px;
	font-weight: bold;
}

.rtslsep {
	font-size: 2px;
	border-right: 1px solid #000;
}
</style>
<style type="text/css">
.yui-skin-sam .yui-button {
	border-width: 1px 0 0 0;
	margin: auto .20em;
}

.yui-skin-sam .yui-button button,.yui-skin-sam .yui-button a {
	padding: 0 10px;
	font-size: 93%; /* 12px */
	line-height: 2; /* ~24px */ *
	line-height: 1.9; /* For IE */
	min-height: 2.1em; /* For Gecko */ *
	min-height: auto; /* For IE */
}

.yui-skin-sam .yui-button .first-child {
	border-width: 0 1px;
}

td .line_draw {
	border: 1px solid #fff;
	background-color: #fff;
}

td .line_draw_hover {
	border: 1px dashed #999;
	background-color: #E1E1E1;
}

td .line_draw_selected {
	border: 1px solid #999;
	background-color: #E1E1E1;
}

td .bottom_tp_no_border {
	border-bottom: 1px solid #808080;
}

td .left_tp_no_border {
	border-left: 1px solid #808080;
}
</style>

<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="3">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td nowrap id="mapTabs"></td>
				<td width="10000"></td>
				<td style="padding-left: 3px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img
							src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
							width="9" height="9" alt="" class="dblk" /></td>
					</tr>
					<tr>
						<td class="menu_bg"><img width="9" height="16"
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							alt="" class="dblk" /></td>
					</tr>
				</table>
				</td>
				<td id="actualDim2" style="display: none;" class="menu_bg radioOpt">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap>Band</td>
						<td nowrap style="padding: 0px 0px 0px 4px;"><select
							name="frequency" onchange="toggleFrequency(this);">
							<option value="5" selected>5 GHz</option>
							<option value="2">2.4 GHz</option>
						</select></td>
					</tr>
				</table>
				</td>
				<td id="actualDim2b1" style="display: none;" class="menu_bg">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-left: 10px;">Grid</td>
						<td style="padding-right: 5px;"><input type="checkbox"
							name="grid" checked onClick="toggleGrid(this);"></td>
					</tr>
				</table>
				</td>
				<td id="actualDim2b2" style="display: none;" class="menu_bg">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-left: 5px;">Measuring Tool</td>
						<td style="padding-right: 5px;"><input type="checkbox"
							name="measuringTool" id="measuringTool"
							onClick="toggleMeasuringTool(this);"></td>
					</tr>
				</table>
				</td>
				<td id="actualDim2a" style="display: none;" class="menu_bg">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-left: 5px;">Scaling Tool</td>
						<td style="padding-right: 10px;"><input type="checkbox"
							name="sizingTool" id="sizingTool"
							onClick="toggleSizingTool(this);"></td>
					</tr>
				</table>
				</td>
				<td id="actualDim2c" style="display: none;" class="menu_bg">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>Zoom</td>
						<td style="padding-left: 4px;"><select name="zoom"
							onchange="zoomIt()" style="width: 61px;">
							<option value=1>100%</option>
							<option value=2>200%</option>
							<option value=3>300%</option>
						</select></td>
					</tr>
				</table>
				</td>
				<td class="menu_bg">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td id="fscrn_<s:property value="%{fullScreenMode}" />"
							style="padding: 0px 0px 3px 8px;"><a
							href="javascript: toggleFullScreen();"><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="16" height="16" alt="" style="display: inline; border: 0;" /></a></td>
					</tr>
				</table>
				</td>
				<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img
							src="<s:url value="/images/rounded/top_right_white.gif" />"
							width="9" height="9" alt="" class="dblk" /></td>
					</tr>
					<tr>
						<td class="menu_bg"><img width="9" height="16"
							src="<s:url value="/images/spacer.gif" />" alt="" class="dblk" /></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="3" style="background-color: #2647a0;"><img
			height="3"
			src="<s:url value="/images/spacer.gif" includeParams="none"/>" alt=""
			class="dblk" /></td>
	</tr>
	<tr>
		<td class="left_tp_border bottom_tp_border">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="menu_bg"><img width="9" height="37"
					src="<s:url value="/images/spacer.gif" includeParams="none"/>"
					alt="" class="dblk" /></td>
			</tr>
		</table>
		</td>
		<td id="mapTabPanel" class="menu_bg bottom_tp_border" style="padding-right: 8px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-top: 2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
						<s:if test="%{!writeDisable4Struts}">
						<div id="mapOps" onmouseover="hoverMapOpsMenu();"
							onmouseout="hideMapOpsMenu();"><input type="button"
							id="mapOpsButton" onclick="showMapOpsMenu()" name="mapOps"
							value="Operations..." class="button" disabled style="width: 85px;"></div>
						</s:if>
						<s:else>
						<div id="mapOps"><input type="hidden" id="mapOpsButton" name="mapOps"></div>
						</s:else>
						</td>
					</tr>
				</table>
				</td>
				<td id="distanceLabel" style="display: none;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding: 0 2px 0 5px;">The distance between the two red markers is</td>
						<td id="distanceMetric"></td>
						<td nowrap style="padding: 0 2px 0 3px;">meters or</td>
						<td id="distanceFeet"></td>
						<td style="padding-left: 3px;">feet.</td>
					</tr>
				</table>
				</td>
				<td width="10000"></td>
				<td align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td id="actualDims" style="display: none;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td id="actualDim3" style="display: none;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding-right: 1px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="pckul" nowrap align="right" style="padding: 0 2px 2px 0;">Device Labels</td>
											</tr>
											<tr>
												<td nowrap style="padding: 1px 2px 0 0;">Nodes Locked</td>
											</tr>
										</table>
										</td>
										<td style="padding-right: 5px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><select style="margin: 0 0 0 1px;"
													name="rapLabels" onchange="hm.map.changeRapNodeLabels();">
													<option value="hn" selected>Host Name</option>
													<option value="cp">Channel/Power</option>
													<option value="ni">Node ID</option>
													<option value="ip">IP Address</option>
													<option value="ac">Client Count</option>
													<option value="no">No Label</option>
												</select></td>
											</tr>
											<tr>
												<td align="left"><input type="checkbox" class="pckcb"
													id="nodesLocked" name="nodesLocked"
													onClick="hm.map.toggleLockMapNodes(this.checked);" checked></td>
											</tr>
										</table>
										</td>
										<td style="padding-right: 4px;" class="radioOpt"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="1" height="33" alt="" class="dblk"
											style="background-color: #cccccc;" /></td>
										<td style="padding-right: 5px;" class="radioOpt">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr id="clientsOption">
												<s:if test="%{fullMode}">
													<td class="pckul" align="right">Clients</td>
													<td class="pckul" align="right"><input type="checkbox"
														class="pckcb" name="clients"
														onClick="toggleClients(this);"></td>
												</s:if>
												<s:else>
													<td class="pckul" align="right" style="display: none;">Clients</td>
													<td class="pckul" align="right" style="display: none;"><input
														type="checkbox" class="pckcb" name="clients"
														onClick="toggleClients(this);"></td>
												</s:else>
											</tr>
											<tr class="radioOpt">
												<td align="right">Rogues</td>
												<td align="right"><input type="checkbox" class="pckcb"
													name="rogues" onClick="toggleRogues(this);"></td>
											</tr>
										</table>
										</td>
										<td><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="1" height="33" alt="" class="dblk"
											style="background-color: #cccccc;" /></td>
										<td style="padding-left: 4px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr class="radioOpt">
												<td class="pckul" align="right">Mesh</td>
												<td class="pckul" align="right"><input type="checkbox"
													class="pckcb" id="mesh" name="mesh"
													onClick="toggleMesh(this);"></td>
											</tr>
											<tr>
												<td align="right">Ethernet</td>
												<td align="right"><input type="checkbox" class="pckcb"
													id="ethernet" name="ethernet"
													onClick="toggleEthernet(this);"></td>
											</tr>
										</table>
										</td>
										<td style="padding-left: 5px;"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="1" height="33" alt="" class="dblk"
											style="background-color: #cccccc;" /></td>
									</tr>
								</table>
								</td>
								<td id="actualDim4" style="display: none;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td id="actualDim4b" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td nowrap style="padding-right: 5px;">AP Labels</td>
												<td><select name="apLabels"
													onchange="hm.map.changeNodeLabels();">
													<option value="hn">Host Name</option>
													<option value="cp">Channel/Power</option>
													<option value="at" selected><s:text name="topology.map.deviceTypeLabel"/></option>
													<option value="no">No Label</option>
												</select></td>
											</tr>
										</table>
										</td>
										<td id="actualDim4a" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding: 0 1px 0 4px;"><img
													src="<s:url value="/images/spacer.gif" includeParams="none"/>"
													width="1" height="33" alt="" class="dblk"
													style="background-color: #cccccc;" /></td>
												<td style="padding-left: 1px;"><img class="dblk"
													width="24" height="32"
													src="<s:url value="/images/nbr_floors.png" includeParams="none" />" />
												</td>
												<td align="right"><input type="checkbox"
													style="margin: 0 0 0 2px;" id="showSpill" name="showSpill"
													onClick="toggleShowSpill(this);"></td>
											</tr>
										</table>
										</td>
										<td id="actualDim4c" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td nowrap style="padding-right: 4px;"><s:text
													name="hm.planning.config.fademargin" /></td>
												<td nowrap><s:select name="fadeMargin"
													onchange="fadeMarginChanged(this);"
													list="%{enumFadeMargin}" listKey="key" listValue="value" />&nbsp;dBm</td>
											</tr>
										</table>
										</td>
										<td style="padding-left: 4px;"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="1" height="33" alt="" class="dblk"
											style="background-color: #cccccc;" /></td>
									</tr>
								</table>
								</td>
								<td style="padding-left: 4px;" class="radioOpt">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="pckul" align="right">RSSI</td>
										<td class="pckul" align="right"><input type="checkbox"
											class="pckcb" name="heat" onClick="toggleHeatMap(this);"
											disabled></td>
										<s:if test="%{planningOnly}">
											<td class="pckul" nowrap align="right" id="intSnrLabel"
												style="padding-left: 6px;">SNR</td>
										</s:if>
										<s:else>
											<td class="pckul" nowrap align="right" id="intSnrLabel"
												style="padding-left: 6px;">Interference</td>
										</s:else>
										<td class="pckul" align="right"><input type="checkbox"
											class="pckcb" name="interference"
											onClick="toggleInterferenceMap(this);" disabled></td>
									</tr>
									<tr>
										<td align="right">Channels</td>
										<td align="right"><input type="checkbox" class="pckcb"
											name="channel" onClick="toggleChannelMap(this);" disabled></td>
										<td nowrap align="right" style="padding-left: 6px;">Data
										Rates</td>
										<td align="right"><input type="checkbox" class="pckcb"
											name="rates" onClick="toggleRatesMap(this);" disabled></td>
									</tr>
								</table>
								</td>
								<td style="padding: 3px 0px 0px 8px;" class="radioOpt">
								<div id="ratesLegendDiv" style="display: none;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td id="ratesLegendHT20"
											style="display: <s:property value="%{CWHT20}" />">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding: 1px 0px 0px 1px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr id="legendHT20">
														<td style="padding-right: 2px;">&nbsp;</td>
														<script>createHT20RatesLegend();</script>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtslsep" style="padding-left: 15px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 28px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 28px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 22px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 10px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 7px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 7px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 7px;">&nbsp;</td>
														<td nowrap class="rtslsep"
															style="border-right: none; padding-left: 2px;">&nbsp;</td>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtsls" style="padding-left: 9px;">130</td>
														<td nowrap class="rtsls" style="padding-left: 15px;">104</td>
														<td nowrap class="rtsls" style="padding-left: 17px;">78</td>
														<td nowrap class="rtsls" style="padding-left: 20px;">52</td>
														<td nowrap class="rtsls" style="padding-left: 14px;">36</td>
														<td nowrap class="rtsls" style="padding-left: 11px;">18</td>
														<td nowrap class="rtsls"
															style="padding-left: 11px; padding-right: 1px;">6</td>
													</tr>
												</table>
												</td>
												<td nowrap style="padding: 0px 0px 0px 9px;"><s:select
													name="rateThresholdHT20" list="%{rateThresholdHT20Values}"
													listKey="id" listValue="value"
													onchange="rateThresholdChanged(this);" /></td>
											</tr>
										</table>
										</td>
										<td id="ratesLegendHT40"
											style="display: <s:property value="%{CWHT40}" />">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding: 1px 0px 0px 1px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr id="legendHT40">
														<td style="padding-right: 2px;">&nbsp;</td>
														<script>createHT40RatesLegend();</script>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtslsep" style="padding-left: 9px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 31px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 10px;">&nbsp;</td>
														<td nowrap class="rtslsep"
															style="border-right: none; padding-left: 2px;">&nbsp;</td>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtsls" style="padding-left: 3px;">270</td>
														<td nowrap class="rtsls" style="padding-left: 18px;">216</td>
														<td nowrap class="rtsls" style="padding-left: 18px;">162</td>
														<td nowrap class="rtsls"
															style="padding-left: 3px; color: #fff;">130</td>
														<td nowrap class="rtsls" style="padding-left: 0px;">108</td>
														<td nowrap class="rtsls" style="padding-left: 17px;">54</td>
														<td nowrap class="rtsls"
															style="padding-left: 23px; padding-right: 1px;">6</td>
													</tr>
												</table>
												</td>
												<td nowrap style="padding: 0px 0px 0px 9px;"><s:select
													name="rateThresholdHT40" list="%{rateThresholdHT40Values}"
													listKey="id" listValue="value"
													onchange="rateThresholdChanged(this);" /></td>
											</tr>
										</table>
										</td>
										<td id="ratesLegendHT80"
											style="display: <s:property value="%{CWHT80}" />">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding: 1px 0px 0px 1px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr id="legendHT80">
														<td style="padding-right: 2px;">&nbsp;</td>
														<script>createHT80RatesLegend();</script>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtslsep" style="padding-left: 12px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 16px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 13px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 7px;">&nbsp;</td>
														<td nowrap class="rtslsep" style="padding-left: 4px;">&nbsp;</td>
														<td nowrap class="rtslsep"
															style="border-right: none; padding-left: 2px;">&nbsp;</td>
													</tr>
												</table>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap class="rtsls" style="padding-left: 4px;">1170</td>
														<td nowrap class="rtsls" style="padding-left: 18px;">910</td>
														<td nowrap class="rtsls" style="padding-left: 21px;">650</td>
														<td nowrap class="rtsls" style="padding-left: 21px;">390</td>
														<td nowrap class="rtsls" style="padding-left: 17px;">130</td>
														<td nowrap class="rtsls"
															style="padding-left: 6px; padding-right: 1px;">6</td>
													</tr>
												</table>
												</td>
												<td nowrap style="padding: 0px 0px 0px 9px;"><s:select
													name="rateThresholdHT80" list="%{rateThresholdHT80Values}"
													listKey="id" listValue="value"
													onchange="rateThresholdChanged(this);" /></td>
											</tr>
										</table>
										</td>
										<td nowrap style="padding: 1px 0px 0px 2px;">Mbps</td>
									</tr>
								</table>
								</div>
								<div id="coverageLegendDiv">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr id="coverageLegend">
										<td nowrap style="padding: 1px 2px 0px 0px;">-35</td>
										<script>createRssiLegend()</script>
										<td nowrap style="padding: 0px 0px 0px 2px;"><s:select
											name="rssiThreshold" list="%{rssiThresholdValues}"
											listKey="id" listValue="value"
											onchange="document.forms[formName].autoRssiThreshold.selectedIndex = this.selectedIndex; document.forms[formName].intRssiThreshold.selectedIndex = this.selectedIndex; rssiThresholdChanged(this);" /></td>
										<td nowrap style="padding: 1px 0px 0px 2px;">dBm</td>
									</tr>
								</table>
								</div>
								<div id="snrLegendDiv" style="display: none;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr id="snrLegend">
										<td nowrap style="padding: 1px 2px 0px 4px;">60</td>
										<script>createSnrLegend(<s:property value="snrThreshold"/>)</script>
										<td nowrap style="padding: 0px 0px 0px 2px;"><s:select
											name="snrThreshold" list="%{snrThresholdValues}" listKey="id"
											listValue="value" onchange="snrThresholdChanged(this);" /></td>
										<td nowrap style="padding: 1px 14px 0px 3px;">dB</td>
									</tr>
								</table>
								</div>
								<div id="interferenceLegendDiv" style="display: none;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td nowrap style="padding: 1px 3px 0px 3px;">Severity</td>
										<td title="Critical" style="background-color: #EE3424"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="35" height="20" alt="" class="dblk"></td>
										<td title="Major" style="background-color: #FF9933"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="35" height="20" alt="" class="dblk"></td>
										<td title="Minor" style="background-color: #FFFF33"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="35" height="20" alt="" class="dblk"></td>
										<td title="No Interference" style="background-color: #33FF33;"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="35" height="20" alt="" class="dblk"></td>
										<td nowrap style="padding: 0px 0px 0px 2px;"><s:select
											name="intRssiThreshold" list="%{rssiThresholdValues}"
											listKey="id" listValue="value"
											onchange="document.forms[formName].rssiThreshold.selectedIndex = this.selectedIndex; document.forms[formName].autoRssiThreshold.selectedIndex = this.selectedIndex; rssiThresholdChanged(this);" /></td>
										<td nowrap style="padding: 1px 0px 0px 2px;">dBm</td>
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
		</table>
		</td>
		<td id="wallsTabPanel" class="menu_bg bottom_tp_border"
			style="display: none; padding-right: 6px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-top: 2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-right: 5px;">Wall Type</td>
						<td><s:select id="newWallType" name="newWallType"
							onchange="wallType = this.selectedIndex" list="%{wallTypeInfo}"
							listKey="key" listValue="value" cssStyle="width: 142px;" /></td>
						<td nowrap style="padding-left: 10px;">Draw Wall</td>
						<td style="padding-left: 5px;"><img id="line_draw0"
							class="line_draw"
							src="<s:url value="/images/line-draw-on.png" includeParams="none"/>"
							onclick="lineDraw(false, false, 0);"
							onmouseover="onLineDrawOver(this, 0);"
							onmouseout="onLineDrawOut(this, 0);" width="20" height="20"
							alt="" class="dblk" /></td>
						<td style="padding-left: 3px;"><img id="line_draw1"
							class="line_draw"
							src="<s:url value="/images/line-draw-connected-on.png" includeParams="none"/>"
							onclick="lineDraw(true, false, 1);"
							onmouseover="onLineDrawOver(this, 1);"
							onmouseout="onLineDrawOut(this, 1);" width="20" height="20"
							alt="" class="dblk" /></td>
						<td style="padding-left: 3px;"><img id="line_draw2"
							class="line_draw"
							src="<s:url value="/images/line-draw-closed-on.png" includeParams="none"/>"
							onclick="lineDraw(true, true, 2);"
							onmouseover="onLineDrawOver(this, 2);"
							onmouseout="onLineDrawOut(this, 2);" width="20" height="20"
							alt="" class="dblk" /></td>
						<td style="padding-left: 10px;"><input type="button"
							name="removeAllWalls" id="removeAllWalls"
							value="Remove All Walls" class="button" style="width: 118px;"
							onClick="doRemoveAllWalls(this);"></td>
						<td style="padding-left: 5px;"><input type="button"
							name="togglePerimeter" id="togglePerimeter" value="..."
							class="button" style="width: 100px;"
							onClick="doTogglePerimeter(this);"></td>
					</tr>
				</table>
				</td>
				<td width="10000"></td>
				<td class="menu_bg" align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr><td style="display: none;" id="wallcfg" onclick="wallSettings()" title="Wall Settings"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td id="apsTabPanel" class="menu_bg bottom_tp_border"
			style="display: none; padding-right: 8px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-top: 2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-right: 5px;"><s:text name="topology.map.deviceTypeLabel"/></td>
						<td><s:select id="defApModel" name="defApModel"
							list="%{apModel}" listKey="key" listValue="value"
							cssStyle="width: 174px;" onchange="changeDefApModel(this);" /></td>
						<td nowrap style="padding: 0 5px 0 10px;">Channel</td>
						<td id="defWifi0Channel" style="display: none;"><s:select
							list="%{enumChannelNgType}" listKey="key" listValue="value"
							name="defWifi0Channel" cssStyle="width: 56px;" /></td>
						<td id="defWifi1Channel"><s:select
							list="%{enumChannelNaType}" listKey="key" listValue="value"
							name="defWifi1Channel" cssStyle="width: 56px;" /></td>
						<td nowrap style="padding: 0 5px 0 10px;">Power</td>
						<td id="defWifi0Power" style="display: none;" nowrap="nowrap"><s:select
							list="%{enumPowerType}" listKey="key" listValue="value"
							name="defWifi0Power" cssStyle="width: 45px;" />dBm</td>
						<td id="defWifi1Power" nowrap="nowrap"><s:select list="%{enumPowerType}"
							listKey="key" listValue="value" name="defWifi1Power"
							cssStyle="width: 45px;" />dBm</td>
						<td style="padding-left: 8px;"><input type="button"
							name="addAp" id="addAp"
							value="<s:text name="topology.menu.operation.simap.create"/>"
							class="button" style="width: 62px;" onClick="doAddAp(this);"></td>
						<td style="padding-left: 5px;"><input type="button"
							name="removeAllAps" id="removeAllAps" value="Remove All APs"
							class="button" style="width: 108px;"
							onClick="doRemoveAllAps(this);"></td>
					</tr>
				</table>
				</td>
				<td width="10000"></td>
				<td class="menu_bg" align="right" id="actualDim5">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-right: 5px;"><s:text
							name="hm.planning.config.channelwidth" /></td>
						<td><s:select name="channelWidth" list="%{enumChannelWidth}"
							listKey="key" listValue="value" cssStyle="width: 110px;"
							onchange="channelWidthChanged(this)" /></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td id="autoApsTabPanel" class="menu_bg bottom_tp_border"
			style="display: none; padding-right: 8px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding-top: 2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap style="padding-right: 5px;">Application</td>
						<td nowrap><s:select name="targetApp"
							onchange="targetAppChanged(this);" list="%{enumTargetApp}"
							listKey="key" listValue="value" /></td>
						<td nowrap style="padding: 0 5px 0 6px;">Signal Strength</td>
						<td nowrap><s:select name="autoRssiThreshold"
							list="%{rssiThresholdValues}" listKey="id" listValue="value"
							onchange="document.forms[formName].rssiThreshold.selectedIndex = this.selectedIndex; document.forms[formName].intRssiThreshold.selectedIndex = this.selectedIndex; rssiThresholdChanged(this);" /></td>
						<td nowrap style="padding: 1px 5px 0px 2px;">dBm</td>
						<td><input type="button" name="autoPlaceAps"
							id="autoPlaceAps" value="Auto Place APs" class="button"
							style="width: 108px;" onClick="doAutoPlaceAps(this);"></td>
						<td style="padding-left: 5px;"><input type="button"
							name="autoRemoveAllAps" id="autoRemoveAllAps"
							value="Remove All APs" class="button" style="width: 108px;"
							onClick="doRemoveAllAps(this);"></td>
					</tr>
				</table>
				</td>
				<td width="10000"></td>
				<td class="menu_bg" align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td id="apCount"
							style="border: 1px solid #999; padding: 1px 3px 1px 3px;">0</td>
						<td nowrap style="padding-left: 5px;">APs</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>

<div id="mapSizePanel" style="display: none;">
<div class="hd">Scale Map Section</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 5px 5px 5px 5px;">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding-bottom: 2px;"><input type="radio"
							name="viewType" value="width" checked onclick="switchSizeWidth()" /></td>
						<td width="30px;">Width</td>
						<td style="padding-bottom: 2px;"><input type="radio"
							name="viewType" value="height" onclick="switchSizeHeight()" /></td>
						<td width="45px" nowrap>Height</td>
						<td><s:textfield id="mapSizeWidth" size="10"
							name="mapSizeWidth" maxlength="16" cssStyle="height:15px;"
							onkeypress="return hm.util.keyPressPermit(event,'tendot');" /></td>
						<td style="padding: 0 0 0px 4px;"><select id="mapSizeUnit"
							name="mapSizeUnit">
							<option value="1">meters</option>
							<option value="2">feet</option>
						</select></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 7px;" align="center">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="button" name="ignore" value="Update"
					class="button" 
					onClick="updateSizeMap('<s:url action="mapSettings" includeParams="none" />?operation=resizeMap&id=');"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="cancelSizeMap();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="treeWidthPanel" style="display: none;">
<div class="hd">Left Navigation Tree Width</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 5px 5px 5px 5px;">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td width="90px;">Left Nav Width</td>
						<td><s:textfield id="treeWidth" size="6" name="treeWidth"
							maxlength="16" cssStyle="height:15px;"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
						<td style="padding: 0 0 0px 4px;">pixels</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 7px;" align="center">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="button" name="ignore" value="Update"
					class="button" 
					onClick="updateTreeWidth();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="closeTreeWidth();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="simApDetailsPanel" style="display: none;">
<div class="hd" id="dialogTitle">AP Details</div>
<div class="bd"><s:hidden id="simApId" />
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 6px 5px 5px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td class="labelT1" width="85px">Host Name</td>
						<td><s:textfield id="simApName" size="18" name="simApName"
							onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
							maxlength="15" /></td>
					</tr>
					<tr>
						<td class="labelT1"><s:text name="topology.map.deviceTypeLabel"/></td>
						<td><s:select id="simApModel" name="simApModel"
							onchange="updateDetailPanelItem(true);" list="%{apModel}"
							listKey="key" listValue="value" cssStyle="width: 174px;" /></td>
					</tr>
					<tr id="simApRadioRow">
						<td class="labelT1">Wifi0 Radio</td>
						<td><s:select id="simApRadio" name="simApRadio"
							onchange="updateDetailPanelItem(false);" list="%{radios}"
							listKey="key" listValue="value" /></td>
					</tr>
					<tr id="simApCh0Row">
						<td class="labelT1">Wifi0 Channel</td>
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><s:select list="%{enumChannelNgType}" listKey="key"
									listValue="value" id="wifi0Channel" name="wifi0Channel"
									cssStyle="width: 55px;" /></td>
								<td width="100%" style="padding-left: 4px" id="autoWifi0Channel">(where)</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="simApPwr0Row">
						<td class="labelT1">Wifi0 Power</td>
						<td><s:select list="%{enumPowerType}" listKey="key"
							listValue="value" id="wifi0Power" name="wifi0Power"
							cssStyle="width: 55px;" /></td>
					</tr>
					<tr id="simApCh1Row">
						<td class="labelT1">Wifi1 Channel</td>
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><s:select list="%{enumChannelNaType}" listKey="key"
									listValue="value" id="wifi1Channel" name="wifi1Channel"
									cssStyle="width: 55px;" /></td>
								<td width="100%" style="padding-left: 4px" id="autoWifi1Channel">(where)</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="simApPwr1Row">
						<td class="labelT1">Wifi1 Power</td>
						<td><s:select list="%{enumPowerType}" listKey="key"
							listValue="value" id="wifi1Power" name="wifi1Power"
							cssStyle="width: 55px;" /></td>
					</tr>
					<tr>
						<td height="4px"></td>
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
				<td><input type="button" name="ignore" value="Update" id="simApUpdBtn"
					class="button" onClick="updateSimApMap();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button"
					onClick="simApDetailsPanel.cfg.setProperty('visible', false);"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
