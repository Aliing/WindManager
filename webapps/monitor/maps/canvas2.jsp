<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<script
	src="<s:url value="/js/raphael210-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
function onLoadCanvas() {
	top.requestNodes(window);
	top.reloadServerIconStyle();
	top.startTime = "<s:property value="%{start}" />";
}
function scrollUp() {
	if (YAHOO.env.ua.webkit > 0) {
		document.body.scrollLeft = 0;
		document.body.scrollTop = 0;
	} else {
		document.documentElement.scrollLeft = 0;
		document.documentElement.scrollTop = 0;
	}
}
function addWallListeners(wallClick, wallDblClick, wallMove) {
	YAHOO.util.Event.addListener("walls", "click", wallClick);
	YAHOO.util.Event.addListener("walls", "dblclick", wallDblClick);
	YAHOO.util.Event.addListener("walls", "mousemove", wallMove);
	// Doesn't work on IE because canvas is not sensitive
	YAHOO.util.Event.addListener("links", "dblclick", top.cancelMeasuring);
}
function createSurface(div, width, height) {
	return dojox.gfx.createSurface(div, width, height);
}
function createRafael(div, width, height) {
	return Raphael(div, width, height);
}
function createRafaelLine(surface, line, lineColor, alpha, width, dash) {
	return createRafaelPath(surface, "M "+line.x1+" "+line.y1+"L"+line.x2+" "+line.y2, lineColor, alpha, width, dash);
}
function createRafaelPath(surface, path, lineColor, alpha, width, dash) {
	var p = surface.path(path);
	p.attr({stroke: lineColor, 'stroke-width': width, 'stroke-opacity': alpha, 'stroke-linecap': 'round', 'stroke-dasharray': dash});
	return p;
}
function createRafaelCircle(surface, circle, circleColor, alpha, width) {
	var cx = circle.cx; var cy = circle.cy;
	var rc = surface.circle(cx, cy, circle.r);
	rc.attr({stroke: circleColor, 'stroke-width': width, "stroke-opacity": alpha});
	return rc;
}
function removeShape(surface, shape) {
//	surface.remove(shape); // for Dojo
	if (shape) {
		shape.remove();
	}
}
function createToolTip(nodeId, text) {
	var tooltip = new YAHOO.widget.Tooltip("tt_" + nodeId, { context: nodeId, text: text, showDelay:500, zIndex : 100 } );
}
var targetId;
function onMenuTrigger(p_sType, p_aArguments, p_aItemsArray) {
	var event = p_aArguments[0];
	if (event.target) {
		var tgt = event.target;
	} else {
		var tgt = event.srcElement;
	}
	targetId = tgt.parentNode.id;

	if (targetId == "clients" ||
		targetId == "rogues") {
		targetId = tgt.id;
	}
	if(targetId.search(/ll/) >= 0){
		//for click on P, AAA, P/AAA stuff, they are childNode of LabelDiv;
		//LabelDiv id is start with 'll'.
		targetId = tgt.parentNode.parentNode.id;
	}
	if (targetId == "" && tgt.parentNode.parentNode) {
		// In some cases, a span node is selected
		targetId = tgt.parentNode.parentNode.id;
	}

	if(p_aItemsArray == "nodeMenu"){
		var leafNodeId = targetId.replace(/n/,"").replace(/l/,"");
		var wlanNode = top.hm.map.wlanNodesHash['n' + leafNodeId];
		var items = top.buildContextMenuItems(wlanNode.jsonNode.dt, wlanNode.jsonNode.dm, top.hm.map.mapWritePermission);
		nodeMenu.clearContent();
		nodeMenu.addItems(items);
		nodeMenu.render("nmenus");
	}
}
function onMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		// This menu item is disabled, do nothing
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	var leafNodeId = targetId.replace(/n/,"").replace(/l/,"");
	if (targetId.charAt(0) == 's' || targetId.charAt(1) == 's') {
		top.processSimApMenuItem(text, leafNodeId);
		return;
	}
	if (targetId.charAt(0) == 'w') {
		top.processWallMenuItem(text, leafNodeId);
		return;
	}
	if (targetId.charAt(0) == 'p') {
		top.processPerimMenuItem(text, leafNodeId);
		return;
	}
	top.setTriggeredLeafNode(leafNodeId);
	if(text == "<s:text name="topology.menu.diagnostics"/>"
		|| text == "<s:text name="topology.menu.statistics"/>"
		|| text == "<s:text name="topology.menu.hiveAp.updates"/>"
		|| text == "<s:text name="topology.menu.lldpcdp"/>"){
		return;
	}else if(text == "<s:text name="topology.menu.client.information"/>" ){
		top.clearClientInfoTable();
		top.retrieveClientInfo();
	}else if (text == "<s:text name="topology.menu.neighbor.information"/>" ) {
		top.clearNeighborInfoTable();
		top.retrieveNeighborInfo();
	}else if (text == "<s:text name="topology.menu.acsp.neighbor.information"/>" ) {
		top.showAcspCoverage();
	}else if (text == "<s:text name="hiveAp.update.configuration"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeConfiguration&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.image"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeImage&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.l7.signature"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeSignature&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.bootstrap"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeBootstrap&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.countryCode"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeCountryCode&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.poe"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradePoe&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.netdump"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeNetdump&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="topology.menu.hiveAp.alarm"/>") {
		top.retrieveAlarmInfo();
	}else if(text=="<s:text name="topology.menu.ssh.web.client"/>"){
	    top.retrieveSshWebClient();
	} else if (text=="<s:text name="topology.menu.ssh.proxy.client"/>"){
		top.retrieveSshProxyClient();
	}else if(text=="<s:text name="topology.menu.packetCapture"/>"){
	    top.openPacketCapturePanel(leafNodeId);
	}else if (text == "<s:text name="topology.menu.remoteSniffer"/>") {
		top.openRemoteSnifferPanel(leafNodeId);
	}else if(text=="<s:text name="topology.menu.troubleshoot.clientTrace"/>"){
	    top.openClientTracePanel(null, leafNodeId);
	}else if(text=="<s:text name="topology.menu.troubleshoot.vlan.probe"/>"){
	    top.openVlanProbePanel(leafNodeId);
	}else if(text=="<s:text name="topology.menu.troubleshoot.path.probe"/>"){
	    top.openPathProbePanel(leafNodeId);
	}else if (text == "<s:text name="topology.menu.lldpcdp.on"/>"
			|| text == "<s:text name="topology.menu.lldpcdp.off"/>"
			|| text == "<s:text name="topology.menu.lldpcdp.clear"/>"
			|| text == "<s:text name="topology.menu.lldpcdp.interface"/>"
			|| text == "<s:text name="topology.menu.lldpcdp.parameter"/>") {
		top.openLldpCdpPanel(text,leafNodeId);
	} else if (text == "<s:text name="topology.menu.hiveAp.configuration.audit"/>") {
		top.requestConfigurationAudit();
	} else if (text == "<s:text name="hiveAp.update.cwp.remove"/>") {
		top.openCwpRemovePanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.reboot"/>") {
		top.openConfirmBootDialog(text);
	} else if (text == "<s:text name="topology.menu.hiveAp.reset.default"/>") {
		top.openConfirmBootDialog(text);
	} else if (text == "<s:text name="topology.menu.hiveAp.invokeBackup"/>") {
		top.openImageBootPanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.sshTunnel"/>") {
		top.openSshTunnelPanel(leafNodeId);
	} else if (text == "<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>") {
		top.openMulticastMonitorPanel(leafNodeId);
	} else if (text == "<s:text name="topology.menu.firewall.policy"/>") {
		top.openFirewallPolicyPanel(leafNodeId);
	} else if (text == "<s:text name="topology.menu.hiveAp.pse.reset"/>") {
		top.openConfirmResetPSEDialog(text);
	} else if(text == "<s:text name="topology.menu.hiveAp.locateAP"/>") {
		top.openLocateAPPanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>"){
		top.clearRadsecCredentials(text);
	} else{
		top.requestSingleItemCli(text);
	}
}
function onClientMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if (menuItem.cfg.getProperty("disabled") == true){
		//alert("This menu item is disabled.");
		return;
	}

	if (targetId == "") {
		return;
	}
	var target = document.getElementById(targetId);
	if (!target || !target.parentNode) {
		return;
	}
	parentId = target.parentNode.id;
	var text = menuItem.cfg.getProperty("text");
	if (text == "<s:text name="topology.menu.client.showRssi"/>" ||
	    text == "<s:text name="topology.menu.client.refreshRssi"/>") {
	    top.hideCoverage();
		top.showRssi(targetId, parentId);
	} else if (text == "<s:text name="topology.menu.client.hideRssi"/>") {
		top.hideRssi(targetId);
	} else if (text == "<s:text name="topology.menu.client.showCoverage"/>") {
		top.hideRssi(targetId);
		top.showCoverage(targetId);
		var menuItem = clientHideMenu.getItem(1, 1);
	} else if (text == "<s:text name="topology.menu.client.hideCoverage"/>") {
	    top.hideCoverage();
	} else if (text == "<s:text name="topology.menu.client.calibrate"/>") {
		top.calibrateRogueRssi(targetId, parentId);
	} else if (text == "<s:text name="topology.menu.client.uncalibrate"/>") {
		top.uncalibrateRogueRssi(targetId, parentId);
	}
}

var nodeMenu;
function recreateContextMenu(triggerIds) {
	nodeMenu.cfg.setProperty('trigger', triggerIds);
}

function createContextMenu(triggerIds,writePermission) {
	nodeMenu = new YAHOO.widget.ContextMenu("nodeMenu");
	nodeMenu.cfg.setProperty('trigger', triggerIds);
	nodeMenu.subscribe('click',onMenuItemClick);
	nodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger, "nodeMenu");
	nodeMenu.render('nmenus');
}

var simApMenu = null;
function createSimApContextMenu(triggerIds, writePermission) {
    var aItems = [ [ {text: "Edit"}, {text: "Remove"} ] ];
    if (simApMenu == null) {
		simApMenu = new YAHOO.widget.ContextMenu("simApMenu");
    	simApMenu.addItems(aItems);
		simApMenu.subscribe('click',onMenuItemClick);
		simApMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		simApMenu.render('nmenus');
	}
	simApMenu.cfg.setProperty('trigger', triggerIds);
}

function onWallMenuTrigger(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	if (event.target) {
		targetId = event.target.id;
	} else {
		targetId = event.srcElement.id;
	}
}

function onPerimMenuShow() {
	var items = perimMenu.getItem(0).cfg.getProperty("submenu").getItems();
	selectWallMenuItems(items, top.findPrm(targetId), 2);
}

function onWallMenuShow() {
	var items = wallMenu.getItem(0).cfg.getProperty("submenu").getItems();
	selectWallMenuItems(items, top.findWall(targetId), 0);
}

function selectWallMenuItems(items, wall, offset) {
	if (wall == null) {
		return;
	}
	for (var i = 0; i < items.length; i++) {
		if (i+offset == wall.tp) {
			items[i].cfg.setProperty("checked", true);
			items[i].cfg.setProperty("selected", true);
		} else {
			items[i].cfg.setProperty("checked", false);
			items[i].cfg.setProperty("selected", false);
		}
	}
}

var perimMenu = null;
function createPerimContextMenu(triggerIds) {
	if (perimMenu == null) {	
	    var aItems = [ [ {text: "Change Wall Type", submenu: { id: "perimTypes", itemdata: top.perimTypes } }, {text: "Remove"} ] ];
	    perimMenu = new YAHOO.widget.ContextMenu("perimMenu");
	    perimMenu.cfg.setProperty('trigger', triggerIds);
	    perimMenu.addItems(aItems);
	    perimMenu.subscribe('click',onMenuItemClick);
	    perimMenu.subscribe('beforeShow', onPerimMenuShow);
	    perimMenu.triggerContextMenuEvent.subscribe(onWallMenuTrigger);
	    perimMenu.render('nmenus');
	} else {
		perimMenu.cfg.setProperty('trigger', triggerIds);
	}
}

var wallMenu = null;
function createWallContextMenu(triggerIds) {
	if (wallMenu == null) {	
	    var aItems = [ [ {text: "Change Wall Type", submenu: { id: "wallTypes", itemdata: top.wallTypes } },
	             	     {text: "Move"}, {text: "Clone"},
	             	     {text: "Remove"} ] ];
		wallMenu = new YAHOO.widget.ContextMenu("wallMenu");
		wallMenu.cfg.setProperty('trigger', triggerIds);
	    wallMenu.addItems(aItems);
		wallMenu.subscribe('click',onMenuItemClick);
		wallMenu.subscribe('beforeShow', onWallMenuShow);
		wallMenu.triggerContextMenuEvent.subscribe(onWallMenuTrigger);
		wallMenu.render('nmenus');
	} else {
		wallMenu.cfg.setProperty('trigger', triggerIds);
	}
}

var clientShowMenu = null;
var sampledShowMenu = null;
var rogueShowMenu = null;
var calClients = true;
var calRogues = false;
function createClientShowContextMenu(triggerIds, sampledIds, stl) {
    var showItems = [ {text: "<s:text name="topology.menu.client.showRssi"/>"},
		  			  {text: "<s:text name="topology.menu.client.hideRssi"/>", disabled: true} ];
    var covItems = [ {text: "<s:text name="topology.menu.client.showCoverage"/>"},
		  			 {text: "<s:text name="topology.menu.client.hideCoverage"/>", disabled: true} ];
	var calItems = [ showItems, covItems,
		[ {text: "<s:text name="topology.menu.client.calibrate"/>"} ]
	];
	var updItems = [ showItems, covItems,
		[ {text: "<s:text name="topology.menu.client.calibrate"/>"},
		  {text: "<s:text name="topology.menu.client.uncalibrate"/>"} ]
	];
	if (stl) {
		if (calRogues) {
			showItems = calItems;
		}
		rogueShowMenu = createClientContextMenu(triggerIds, rogueShowMenu, "rogueShowMenu", showItems);
	} else {
		if (!calClients) {
			calItems = showItems;
			updItems = showItems;
		}
		clientShowMenu = createClientContextMenu(triggerIds, clientShowMenu, "clientShowMenu", calItems);
	    sampledShowMenu = createClientContextMenu(sampledIds, sampledShowMenu, "sampledShowMenu", updItems);
	}
}

var clientHideMenu = null;
var sampledHideMenu = null;
var rogueHideMenu = null;
function createClientHideContextMenu(jnode, stl) {
    var hideItems = [ {text: "<s:text name="topology.menu.client.refreshRssi"/>"},
		  			  {text: "<s:text name="topology.menu.client.hideRssi"/>"} ];
    var covItems = [ {text: "<s:text name="topology.menu.client.showCoverage"/>"},
		  			 {text: "<s:text name="topology.menu.client.hideCoverage"/>", disabled: false} ];
	var calItems = [ hideItems, covItems,
		[ {text: "<s:text name="topology.menu.client.calibrate"/>"} ]
	];
	var updItems = [ hideItems, covItems,
		[ {text: "<s:text name="topology.menu.client.calibrate"/>"},
		  {text: "<s:text name="topology.menu.client.uncalibrate"/>"}]
	];
	if (stl) {
	    rogueHideMenu = createClientContextMenu(jnode.mac, rogueHideMenu, "rogueHideMenu", hideItems);
	} else {
		if (!calClients) {
			clientHideMenu = createClientContextMenu(jnode.mac, clientHideMenu, "clientHideMenu", hideItems);
		} else {
			if (jnode.sd) {
		    	sampledHideMenu = createClientContextMenu(jnode.mac, sampledHideMenu, "sampledHideMenu", updItems);
				var rssiItem = sampledHideMenu.getItem(0, 0);
				var hrssiItem = sampledHideMenu.getItem(1, 0);
				var covItem = sampledHideMenu.getItem(1, 1);
			} else {
				clientHideMenu = createClientContextMenu(jnode.mac, clientHideMenu, "clientHideMenu", calItems);
				var rssiItem = clientHideMenu.getItem(0, 0);
				var hrssiItem = clientHideMenu.getItem(1, 0);
				var covItem = clientHideMenu.getItem(1, 1);
			}
			var hc = top.clientCoverage != null;
			if (hc) {
				rssiItem.cfg.setProperty("text", "<s:text name="topology.menu.client.showRssi"/>");
			} else {
				rssiItem.cfg.setProperty("text", "<s:text name="topology.menu.client.refreshRssi"/>");
			}
			hrssiItem.cfg.setProperty("disabled", hc);
			covItem.cfg.setProperty("disabled", !hc);
		}
	}
}

function createClientContextMenu(triggerIds, menu, menuId, items) {
	if (menu == null) {
		var menu = new YAHOO.widget.ContextMenu(menuId);
    	menu.addItems(items);
		menu.subscribe('click',onClientMenuItemClick);
		menu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		menu.render('nmenus');
	}
	menu.cfg.setProperty('trigger', triggerIds);
	return menu;
}

function restoreClientContextMenu(excludeId) {
	if (clientShowMenu != null) {
		var triggerIds = clientShowMenu.cfg.getProperty('trigger');
		var includeId = null;
		if (clientHideMenu != null) {
			includeId = clientHideMenu.cfg.getProperty('trigger');
		}
		clientShowMenu.cfg.setProperty('trigger', excludeTriggerId(triggerIds, excludeId, includeId));
	}
	if (sampledShowMenu != null) {
		var triggerIds = sampledShowMenu.cfg.getProperty('trigger');
		var includeId = null;
		if (sampledHideMenu != null) {
			includeId = sampledHideMenu.cfg.getProperty('trigger');
		}
		sampledShowMenu.cfg.setProperty('trigger', excludeTriggerId(triggerIds, excludeId, includeId));
	}
	if (rogueShowMenu != null) {
		var triggerIds = rogueShowMenu.cfg.getProperty('trigger');
		var includeId = null;
		if (rogueHideMenu != null) {
			includeId = rogueHideMenu.cfg.getProperty('trigger');
		}
		rogueShowMenu.cfg.setProperty('trigger', excludeTriggerId(triggerIds, excludeId, includeId));
	}
}

function excludeTriggerId(triggerIds, excludeId, includeId) {
	if (excludeId == null) {
		var newIds = triggerIds;
	} else {
		var newIds = new Array();
		for (var i = 0; i < triggerIds.length; i++) {
			if (triggerIds[i] == excludeId) {
				continue;
			}
			newIds[newIds.length] = triggerIds[i];
		}
	}
	if (includeId != null && includeId != excludeId) {
		for (var i = 0; i < newIds.length; i++) {
			if (newIds[i] == includeId) {
				return newIds;
			}
		}
		newIds[newIds.length] = includeId;
	}
	return newIds;
}

function restoreSampledClientContextMenu(mac) {
	var showIds = clientShowMenu.cfg.getProperty('trigger');
	var sampledIds = sampledShowMenu.cfg.getProperty('trigger');
	var hideId = null;
	if (clientHideMenu != null) {
		hideId = clientHideMenu.cfg.getProperty('trigger');
		if (hideId != null) {
			clientHideMenu.cfg.setProperty('trigger', null);
		}
	}
	if (hideId == null && sampledHideMenu != null) {
		hideId = sampledHideMenu.cfg.getProperty('trigger');
		if (hideId != null) {
			sampledHideMenu.cfg.setProperty('trigger', null);
		}
	}
	var newIds = excludeTriggerId(showIds, mac, null);
	if (newIds.length < showIds.length) {
		sampledIds = excludeTriggerId(sampledIds, null, mac);
	} else if (hideId != null) {
		sampledIds = excludeTriggerId(sampledIds, null, hideId);
	}
	clientShowMenu.cfg.setProperty('trigger', newIds);
	sampledShowMenu.cfg.setProperty('trigger', sampledIds);
}

function restoreUnsampledClientContextMenu(mac) {
	var showIds = clientShowMenu.cfg.getProperty('trigger');
	var sampledIds = sampledShowMenu.cfg.getProperty('trigger');
	var newIds = excludeTriggerId(sampledIds, mac, null);
	if (newIds.length < sampledIds.length) {
		showIds = excludeTriggerId(showIds, null, mac);
	}
	var hideId = null;
	if (clientHideMenu != null) {
		hideId = clientHideMenu.cfg.getProperty('trigger');
		if (hideId != null) {
			showIds = excludeTriggerId(showIds, null, hideId);
			clientHideMenu.cfg.setProperty('trigger', null);
		}
	}
	if (hideId == null && sampledHideMenu != null) {
		hideId = sampledHideMenu.cfg.getProperty('trigger');
		if (hideId != null) {
			newIds = excludeTriggerId(newIds, null, hideId);
			sampledHideMenu.cfg.setProperty('trigger', null);
		}
	}
	sampledShowMenu.cfg.setProperty('trigger', newIds);
	clientShowMenu.cfg.setProperty('trigger', showIds);
}

</script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<%--
<script type="text/javascript" src="struts/dojoroot/dojo/dojo.js?v=<s:property value="verParam" />"
	djConfig="parseOnLoad: true"></script>
<script language="JavaScript" type="text/javascript">
	dojo.require("dojox.gfx");
</script>
  --%>

<s:if test="%{useCdn}">
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/menu/assets/skins/sam/menu.css" />
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/container/container-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/menu/menu-min.js"></script>
</s:if>
<s:else>
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/logger/assets/skins/sam/logger.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<script type="text/javascript"
		src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script type="text/javascript"
		src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script type="text/javascript"
		src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script type="text/javascript"
		src="<s:url value="/yui/menu/menu-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script type="text/javascript"
		src="<s:url value="/yui/logger/logger-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
</s:else>
<script>
if (top.document.forms['maps'].zoom.value == 1) {
	document.writeln('<style>html, body { overflow: hidden; }</style>');
}
</script>
<style>
/* HM default css overrides */
input{
	font-size: 1em;
	padding: 2px;
}
select{
	font-size: 1em;
	padding: 1px;
	height: 21px;
}
input[type='checkbox']{
	margin: 0 0 0 2px;
}
input[type="text"], input[type="password"] {
    height: 15px;
}
select[multiple], select[size] {
    height: auto;
}
.yui-skin-sam .yuimenuitem a.selected {
	background-color:#B3D4FF;
/*  background-color:#0079d6; */
}
.yui-skin-sam .yuimenu .bd {
    background-color:#FFFFFF;
}
.yui-skin-sam .yuimenuitemlabel {
	font-weight: bold;
    color: #003366;
}
.yui-skin-sam .yuimenuitemlabel-disabled {
    color: #b9b9b9;
}
.dhcpImg {
	display: none;
	padding-bottom: 3px;
	padding-right: 1px;
	border: 1px solid #ffffff;
}
.vpnImg {
	display: none;
	padding-bottom: 3px;
	padding-right: 1px;
	border: 1px solid #ffffff;
}
.vpnImgClient {
	display: none;
	padding-bottom: 3px;
	padding-right: 1px;
	border: 1px solid #ffffff;
}
.radiusImg {
	display: none;
	padding-bottom: 3px;
	padding-right: 1px;
	border: 1px solid #ffffff;
}
.radiusProxyImg {
	display: none;
	padding-bottom: 3px;
	padding-right: 1px;
	border: 1px solid #ffffff;
}
.portalLabel{
	padding-right: 2px;
	font-weight: bold;
}
.leafLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #003366;
	white-space: nowrap;
	padding: 1px 3px 1px 3px;
	border-top: 1px solid #999999;
	border-right: 1px solid #999999;
	border-bottom: 1px solid #999999;
	border-left: 1px solid #999999; /* remove for target icons */
	background-color: #FFFFFF;
}
.leafLabelEmpty {
	position:absolute;
	font-size: 12px;
	padding-top: 3px;
	border-left: 1px solid #221E1F;
}
.rssiLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #002222;
	white-space: nowrap;
	padding: 1px 3px 1px 3px;
	border-top: 1px solid #999999;
	border-right: 1px solid #999999;
	border-bottom: 1px solid #999999;
	border-left: 1px solid #999999; /* remove for target icons */
	background-color: #00FFFF;
}
.linkLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 11px;
	color: #003366;
	white-space: nowrap;
	padding: 0px 2px 0px 2px;
	border-top: 1px solid #999999;
	border-right: 2px solid #999999;
	border-bottom: 1px solid #999999;
	border-left: 2px solid #999999; /* remove for target icons */
	background-color: #FFFFFF;
}
.rogueLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #003366;
	white-space: nowrap;
	padding: 1px 3px 1px 3px;
	border-top: 1px solid #DD0000;
	border-right: 1px solid #DD0000;
	border-bottom: 1px solid #DD0000;
	border-left: 1px solid #DD0000; /* remove for target icons */
	background-color: #FFFFFF;
}
.rogueColor {
	color: #DD0000;
}
.clientLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #000099;
	white-space: nowrap;
	padding: 1px 3px 1px 3px;
	border-top: 1px solid #000099;
	border-right: 1px solid #000099;
	border-bottom: 1px solid #000099;
	border-left: 1px solid #000099; /* remove for target icons */
	background-color: #FFFFFF;
}
.clientColor {
	color: #000099;
}
.gridLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #666666;
}
.gridLine {
	filter:alpha(opacity=50);
	opacity:.50;
}
.heatMap {
	filter:alpha(opacity=<s:property value="heatMapOpacityAlpha" />);
	opacity:<s:property value="heatMapOpacity" />;
}
.bgMap {
	filter:alpha(opacity=<s:property value="bgMapOpacityAlpha" />);
	opacity:<s:property value="bgMapOpacity" />;
}
#walls {
	filter:alpha(opacity=00);
	opacity:.00;
	cursor:crosshair;
}
.handle {
	filter:alpha(opacity=01);
}
.planMap {
	filter:alpha(opacity=<s:property value="heatMapOpacityAlpha" />);
	opacity:<s:property value="heatMapOpacity" />;
}
</style>
</head>
<body class="yui-skin-sam" onload="onLoadCanvas()">
<script>
top.wallsOpacity = <s:property value="wallsOpacity" />;
// Creating a dragable element must be from the IFRAME document
function enableDrag(nodeId) {
	if (top.document.forms[top.formName].zoom.value == 1) {
		return new YAHOO.util.DD(nodeId, "hive", { scroll: false });
	} else {
		return new YAHOO.util.DD(nodeId, "hive", { scroll: true });
	}
}

document.oncontextmenu = new Function("return false;")
document.onmousedown=click;
if (document.layers) {
	document.captureEvents(Event.MOUSEDOWN);
}
function click(e) {
	if (document.all) {
		if (event.button==2||event.button==3) {
			oncontextmenu='return false';
		}
	}
	if (document.layers) {
		if (e.which == 3) {
			oncontextmenu='return false';
		}
	}
}

document.writeln('<div id="carea" style="position:absolute;z-index:6;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;background-color: #fff;"><img src="<s:url value="/images/spacer.gif" includeParams="none"/>" width="',top.canvasWidth-top.gridBorderX,'" height="',top.canvasHeight-top.gridBorderY,'"/></div>');
document.writeln('<div id="walls" style="position:absolute;z-index:9;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"><img src="<s:url value="/images/spacer.gif" includeParams="none"/>" width="',top.canvasWidth-top.gridBorderX,'" height="',top.canvasHeight-top.gridBorderY,'"/></div>');
document.writeln('<div id="bgMap" class="bgMap" style="position:absolute;z-index:10;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;background-color: #FFFFFF;"><img id="imageId" src="<s:url value="/images/spacer.gif" includeParams="none"/>" width="',top.canvasWidth-top.gridBorderX,'" height="',top.canvasHeight-top.gridBorderY,'"/></div>');
document.writeln('<div id="nmenus" style="position:absolute;z-index:86;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="clients" style="position:absolute;z-index:85;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="rogues" style="position:absolute;z-index:85;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="links" style="position:absolute;z-index:75;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="anchor" style="position:absolute;z-index:80;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"><div id="wlszr" style="position:absolute;display: none" class="linkLabel">test ...</div></div>');
document.writeln('<div id="heat" class="heatMap" style="position:absolute;z-index:20;display:none;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="planSpill" style="position:absolute;z-index:21;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="plan" style="position:absolute;z-index:22;display:none;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
</script>

<div id="grid" style="position:absolute;z-index:40;top:0px;left:0px;"></div>

<div id="cross1"
	style="position:absolute;top:15px;left:205px;width:50px;height:50px;">
<div style="position:absolute;top:10px;left:10px;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="30" height="30" /></div>
<div
	style="position:absolute;top:25px;height:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="50" height="1" /></div>
<div
	style="position:absolute;left:25px;width:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="1" height="50" /></div>
</div>
<div id="cross2"
	style="position:absolute;top:15px;left:280px;width:50px;height:50px;">
<div style="position:absolute;top:10px;left:10px;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="30" height="30" /></div>
<div
	style="position:absolute;top:25px;height:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="50" height="1" /></div>
<div
	style="position:absolute;left:25px;width:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="1" height="50" /></div>
</div>

<div id="measuringTool"
	style="position:absolute;top:0px;left:0px;z-index:5;">

<div id="mtc1"
	style="position:absolute;top:18px;left:90px;width:50px;height:50px;">
<div style="position:absolute;top:10px;left:10px;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="30" height="30" /></div>
<div
	style="position:absolute;top:25px;height:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="50" height="1" /></div>
<div
	style="position:absolute;left:25px;width:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="1" height="50" /></div>
</div>

<div id="mtc2"
	style="position:absolute;top:22px;left:95px;width:50px;height:50px;">
<div style="position:absolute;top:10px;left:10px;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="30" height="30" /></div>
<div
	style="position:absolute;top:25px;height:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="50" height="1" /></div>
<div
	style="position:absolute;left:25px;width:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="1" height="50" /></div>
</div>

<div id="mtc3"
	style="position:absolute;top:22px;left:90px;width:50px;height:50px;">
<div style="position:absolute;top:10px;left:10px;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="30" height="30" /></div>
<div
	style="position:absolute;top:25px;height:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="50" height="1" /></div>
<div
	style="position:absolute;left:25px;width:1px;background-color: #DD0000;"><img
	src="<s:url value="/images/spacer.gif" includeParams="none"/>"
	width="1" height="50" /></div>
<div style="position:absolute;top:2px;left:30px;" class="leafLabel"
	id="mtc3l" />&nbsp;</div>
</div>

</div>
</body>
</html>
