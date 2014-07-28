var hm = hm || {};
hm.map = hm.map || {};

/*
 * Set size of viewport, preserve aspect ratio of background image
 */
hm.map.setViewportSize = function(viewportId, mapWidth, mapHeight) {
	hm.map.mapWidth = mapWidth;
	var vp = YAHOO.util.Dom.get(viewportId);
	var ww = YAHOO.util.Dom.getViewportWidth();
	var vpX = YAHOO.util.Dom.getX(vp);
	hm.map.viewportWidth = ww - vpX;
	if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 8) {
		hm.map.viewportWidth -= 1;
	} else if (YAHOO.env.ua.ie > 8) {
		hm.map.viewportWidth -= 2;
	}

	var wh = YAHOO.util.Dom.getViewportHeight();
	var vpY = YAHOO.util.Dom.getY(vp);
	hm.map.viewportHeight = wh - vpY - 3 - 1;
	if (YAHOO.env.ua.ie > 0) {
		hm.map.viewportHeight += 4;
		if (YAHOO.env.ua.ie < 8) {
			hm.map.viewportHeight -= 1;
		} else if (YAHOO.env.ua.ie > 8) {
			hm.map.viewportHeight -= 5;
		}
	}

	top.actualViewportWidth = hm.map.viewportWidth;
	top.actualViewportHeight = hm.map.viewportHeight;

	if (document.forms[formName].grid.checked && actualHeight > 0) {
		top.gridBorderX = 15;
		var ahi = actualHeight;
		while (ahi >= 100) {
			top.gridBorderX += 7;
			ahi /= 10;
		}
		top.gridBorderY = 15;
	} else {
		top.gridBorderX = 0;
		top.gridBorderY = 0;
	}

	// Preserve image aspect ratio
	if (mapWidth > 0) {
		var aspectRatio = mapWidth / mapHeight;
		if (aspectRatio * (hm.map.viewportHeight - top.gridBorderY) > (hm.map.viewportWidth - top.gridBorderX)) {
			// Use full width, adjust height
			hm.map.viewportHeight = Math
					.round((hm.map.viewportWidth - top.gridBorderX)
							/ aspectRatio + top.gridBorderY);
		} else {
			// Use full height, adjust width
			hm.map.viewportWidth = Math
					.round((hm.map.viewportHeight - top.gridBorderY)
							* aspectRatio + top.gridBorderX);
		}
	}

	YAHOO.util.Dom.setStyle(vp, "width", top.actualViewportWidth + "px");
	YAHOO.util.Dom.setStyle(vp, "height", top.actualViewportHeight + "px");
	return vp;
}

/*
 * Set size of viewport, preserve aspect ratio
 */
hm.map.setCanvasSize = function(viewWindow, zoomFactor) {
	hm.map.viewWindow = viewWindow;

	viewWindow.canvasWidth = zoomFactor
			* (hm.map.viewportWidth - viewWindow.gridBorderX)
			+ viewWindow.gridBorderX;
	viewWindow.canvasHeight = zoomFactor
			* (hm.map.viewportHeight - viewWindow.gridBorderY)
			+ viewWindow.gridBorderY;

	hm.map.scale = (viewWindow.canvasWidth - viewWindow.gridBorderX)
			/ hm.map.mapWidth;
}

hm.map.loadPopUpFlag = function(popUpFlag) {
	hm.map.popUpFlag = popUpFlag;
}

hm.map.loadMap = function(mapId) {
	top.rssiData = null;
	hm.map.mapId = mapId;
	hm.map.clearAlarmsTimer();
	document.forms[formName].mapOps.disabled = true;
	if (hm.map.viewWindow.mapSizePanel != null) {
		hm.map.viewWindow.mapSizePanel.cfg.setProperty('visible', false);
	}
	moveAPsPanel.cfg.setProperty('visible', false);
	hm.map.requestMapDetails(hm.map.processMapDetails);
	// display network summary panel
	if (hm.map.popUpFlag) {
		top.displaySummaryPanel(hm.map.mapId, selectedNode.label);
	}
	// initial eth icon count;
	hm.map.ethIconCount = 0;
}

hm.map.processMapDetails = function(o) {
	hm.util.toggleHideElement("cstd", false);
	hm.util.toggleHideElement("gmtd", true);
	eval("var data = " + o.responseText);
	if (data) {
		hm.map.mapWritePermission = data.writePermission;
		if (data.bg) {
			hm.map.viewWindow.canvasBackground = data.path + data.bg;
		} else {
			hm.map.viewWindow.canvasBackground = data.commPath
					+ "/images/spacer.gif";
		}
		hm.map.viewWindow.gridSize = data.gridSize;
		hm.map.viewWindow.leafMapContainer = data.leafMapContainer;
		hm.map.viewWindow.actualHeight = 0;
		if (hm.map.viewWindow.gridSize > 0) {
			hm.map.setLengthUnit(data);
			hm.map.viewWindow.actualGridSize = data.actualGridSize;
			hm.map.viewWindow.actualHeight = data.actualHeight;
			hm.map.viewWindow.actualScale = data.actualHeight / data.height;
			hm.map.viewWindow.mapGeo = null;
		} else {
			hm.map.setMapGeo(data);
		}
		hm.map.setViewportSize("viewport", data.width, data.height);
		hm.map.viewWindow.initMapControls();
		hm.map.setCanvasSize(hm.map.viewWindow, 1);
		top.moveSummaryPanel();
		// Reset zooming
		hm.map.viewWindow.scaleDelta = -1;
		hm.map.viewWindow.document.forms[formName].zoom.value = 1;
		hm.map.viewWindow.viewport.location.reload();
	}
}

hm.map.setLengthUnit = function(data) {
	top.lengthUnit = data.lengthUnit;
	var select = top.document.getElementById('mapSizeUnit');
	select.options[top.lengthUnit - 1].selected = true;
}

hm.map.setMapGeo = function(data) {
	top.mapGeo = {
		address : data.address,
		ll : data.ll,
		lat : data.lat,
		lng : data.lng,
		zm : data.zm,
		pzm : data.pzm,
		vt : data.vt,
		map : null,
		mt : null,
		perims : null
	};
}

hm.map.selectNewTreeNode = function(newMapId) {
	var treeNode = mapTree.getNodeByProperty("id", newMapId);
	if (treeNode) {
		highlightNode(treeNode);
		hm.map.loadMap(newMapId);
	}
}

/*
 * Map container double clicked.
 */
hm.map.containerDblClicked = function(event) {
	if (!event) {
		// Use window.event for IE
		event = window.event;
	}
	if (event.target) {
		var img = event.target;
	} else {
		var img = event.srcElement;
	}
	var targetId = img.parentNode.id;
	hm.map.selectNewTreeNode(targetId.substring(1));
}

/*
 * Map Leaf node left clicked.
 */
hm.map.leafSglClicked = function(event) {
	if (!event) {
		// Use window.event for IE
		event = window.event;
	}
	if (event.target) {
		var targetNode = event.target;
	} else {
		var targetNode = event.srcElement;
	}
	var targetId = targetNode.parentNode.id;
	var div = hm.map.canvasWindow.document.getElementById(targetId);
	if (div.style.cursor == "default") {
		// Only if nodes are locked
		top.displayAPInfoPanel(targetId);
	}
}

/*
 * Find tree node
 */
hm.map.findTreeNode = function(treeNode, nodeId) {
	for ( var i = 0; i < treeNode.children.length; i++) {
		if (treeNode.children[i].widgetId == nodeId) {
			return treeNode.children[i];
		} else {
			var node = hm.map.findTreeNode(treeNode.children[i], nodeId);
			if (node != null) {
				return node;
			}
		}
	}
	return null;
}

/*
 * Pre-load icons
 */
hm.map.loadIcons = function(baseUrl) {
	hm.map.spacer = hm.util.loadImage(baseUrl + "/spacer.gif");

	hm.map.leafNodeIcons = new Array();
	// hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] =
	// hm.util.loadImage(baseUrl
	// + "/nodes/white/white_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/yellow/yellow_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/orange/orange_target_icon.png");
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/red/red_target_icon.png");
	// Foreign map
	hm.map.leafNodeIcons[hm.map.leafNodeIcons.length] = hm.util
			.loadImage(baseUrl + "/nodes/external_map.png");

	hm.map.containerNodeIcons = new Array();
	var buildingIcons = new Array();
	var houseIcons = new Array();
	var penantIcons = new Array();
	var floorIcons = new Array();
	var hexagonIcons = new Array();
	var starIcons = new Array();
	var ovalIcons = new Array();
	// building icons
	// buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_building_32x32.png");
	buildingIcons[buildingIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_building_32x32.png");
	// house icons
	// houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_house_32x32.png");
	houseIcons[houseIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_house_32x32.png");
	// penant icons
	// penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_penant_32x32.png");
	penantIcons[penantIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_penant_32x32.png");
	// floor icons
	// floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_floor_32x32.png");
	floorIcons[floorIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_floor_32x32.png");
	// hexagon icons
	// hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_hexagon_32x32.png");
	hexagonIcons[hexagonIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_hexagon_32x32.png");
	// star icons
	// starIcons[starIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_star_32x32.png");
	starIcons[starIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_star_32x32.png");
	// oval icons
	// ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl +
	// "/nodes/white/white_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/green/green_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/yellow/yellow_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/orange/orange_oval_32x32.png");
	ovalIcons[ovalIcons.length] = hm.util.loadImage(baseUrl
			+ "/nodes/red/red_oval_32x32.png");

	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = buildingIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = houseIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = penantIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = floorIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = hexagonIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = starIcons;
	hm.map.containerNodeIcons[hm.map.containerNodeIcons.length] = ovalIcons;

	hm.map.clientIcon = hm.util.loadImage(baseUrl + "/nodes/client.png");
	hm.map.rogueClientIcon = hm.util.loadImage(baseUrl
			+ "/nodes/rogue-client.png");
	hm.map.rogueApIcon = hm.util.loadImage(baseUrl
			+ "/nodes/white/white_target_icon.png");
	hm.map.rogueApIconSize = 18;
	hm.map.radiusIcon = hm.util.loadImage(baseUrl + "/radius_server.png");
	hm.map.radiusProxyIcon = hm.util.loadImage(baseUrl
			+ "/radius_proxy_server.png");
	hm.map.vpnServerIcon = hm.util.loadImage(baseUrl + "/vpn_server.png");
	hm.map.dhcpIcon = hm.util.loadImage(baseUrl + "/dhcp_server.png");
	hm.map.vpnClientIcon = hm.util.loadImage(baseUrl + "/vpn_client.png");
	hm.map.vpnServerIcon_up = hm.util.loadImage(baseUrl + "/vpn_server_up.png");
	hm.map.vpnClientIcon_up = hm.util.loadImage(baseUrl + "/vpn_client_up.png");
	hm.map.vpnClientIcon_half = hm.util.loadImage(baseUrl
			+ "/vpn_client_half.png");

	if (top.gme) {
		hm.map.markerIcons = new Array();
		hm.map.markerIcons[hm.map.markerIcons.length] = baseUrl
				+ "/nodes/green/green_oval_32x32.png";
		hm.map.markerIcons[hm.map.markerIcons.length] = baseUrl
				+ "/nodes/green/green_building_32x32.png";
	}
}

hm.map.getContainerNodeIcon = function(severity, iconName) {
	if (typeof (severity) == "undefined") {
		severity = 0;
	}
	if (typeof (iconName) == "undefined") {
		iconName = "building_32x32.png";
	}
	switch (iconName) {
	case "building_32x32.png":
		return hm.map.containerNodeIcons[0][severity];
	case "house_32x32.png":
		return hm.map.containerNodeIcons[1][severity];
	case "penant_32x32.png":
		return hm.map.containerNodeIcons[2][severity];
	case "floor_32x32.png":
		return hm.map.containerNodeIcons[3][severity];
	case "hexagon_32x32.png":
		return hm.map.containerNodeIcons[4][severity];
	case "star_32x32.png":
		return hm.map.containerNodeIcons[5][severity];
	case "oval_32x32.png":
		return hm.map.containerNodeIcons[6][severity];
	default:
		return hm.map.containerNodeIcons[0][severity];
	}
}

hm.map.getHiveAPTypeNodes = function(apType) {
	var nodes = new Array();
	if (apType == undefined) {// avoid js error
		return nodes;
	}
	var types = apType.split("|");
	var isPortal = 1 == types[0];
	var isRadius = 1 == types[1];
	var isVpnServer = 1 == types[2];
	var isVpnClient = 2 == types[2];
	var isVpnServer_up = 3 == types[2];
	var isVpnClient_up = 4 == types[2];
	var isVpnClient_half = 5 == types[2];
	var isDhcp = 1 == types[3];
	var isRADIUSProxy = 1 == types[4];
	if (isPortal) {
		var b = hm.map.canvasWindow.document.createElement("span");
		b.appendChild(hm.map.canvasWindow.document.createTextNode("P"));
		b.className = "portalLabel";
		b.title = "Portal";
		nodes.push(b);
	}
	if (isRadius) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.radiusIcon.src;
		img.className = "radiusImg";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "RADIUS Server";
		nodes.push(img);
	}
	if (isDhcp) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.dhcpIcon.src;
		img.className = "dhcpImg";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "DHCP Server";
		nodes.push(img);
	}
	if (isVpnServer) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.vpnServerIcon.src;
		img.className = "vpnImg";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "VPN Server";
		nodes.push(img);
	}
	if (isVpnClient) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.vpnClientIcon.src;
		img.className = "vpnImgClient";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "VPN Client";
		nodes.push(img);
	}
	if (isVpnServer_up) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.vpnServerIcon_up.src;
		img.className = "vpnImg";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "VPN Server";
		nodes.push(img);
	}
	if (isVpnClient_up) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.vpnClientIcon_up.src;
		img.className = "vpnImgClient";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "VPN Client";
		nodes.push(img);
	}
	if (isVpnClient_half) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.vpnClientIcon_half.src;
		img.className = "vpnImgClient";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "VPN Client";
		nodes.push(img);
	}
	if (isRADIUSProxy) {
		var img = hm.map.canvasWindow.document.createElement("img");
		img.src = hm.map.radiusProxyIcon.src;
		img.className = "radiusProxyImg";
		img.height = img.width = "12";
		img.align = "absmiddle";
		img.title = "RADIUS Proxy Server";
		nodes.push(img);
	}
	return nodes;
}

hm.map.getMousePos = function(e) {
	if (YAHOO.env.ua.webkit > 0) {
		var x = hm.map.canvasWindow.document.body.scrollLeft;
		var y = hm.map.canvasWindow.document.body.scrollTop;
	} else {
		var x = hm.map.canvasWindow.document.documentElement.scrollLeft;
		var y = hm.map.canvasWindow.document.documentElement.scrollTop;
	}
	return {
		x1 : x + e.clientX - hm.map.viewWindow.gridBorderX,
		y1 : y + e.clientY - hm.map.viewWindow.gridBorderY
	};
}

hm.map.showWallLayer = function(show) {
	var div = hm.map.canvasWindow.document.getElementById("walls");
	if(div) {
		if (!show) {
			hm.map.resetDrawOps();
			if (div.style.zIndex == 9) {
				return;
			}
		}
		hm.map.showWallHandles(!show);
		div.style.zIndex = show ? "76" : "9";
	}
}

hm.map.resetDrawOps = function() {
	for (id = 0; id < 3; id++) {
		img = document.getElementById('line_draw' + id).className = "line_draw";
	}
}

hm.map.showWallHandles = function(show) {
	for ( var i = 0; i < hm.map.walls.length; i++) {
		if (!hm.map.walls[i]) {
			continue;
		}
		hm.map.showHandlePair(show, i);
	}
}

hm.map.showHandlePair = function(show, id) {
	hm.map.showWallHandle(show, "wo" + id);
	hm.map.showWallHandle(show, "wt" + id);
}

hm.map.showWallHandle = function(show, handleId) {
	var div = hm.map.canvasWindow.document.getElementById(handleId);
	if (div) {
		if (show) {
			div.style.display = "";
		} else {
			div.style.display = "none";
		}
	}
}

hm.map.removeHandlePair = function(id) {
	hm.map.removeWallHandle("wo" + id);
	hm.map.removeWallHandle("wt" + id);
}

hm.map.removeWallHandle = function(handleId) {
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var div = hm.map.canvasWindow.document.getElementById(handleId);
	if (div) {
		anchor.removeChild(div);
	}
}

hm.map.addPerimMenu = function() {
	if (!hm.map.mapWritePermission) {
		return;
	}
	var triggerIds = new Array();
	for ( var p = 0; p < hm.map.prms.length; p++) {
		var prm = hm.map.prms[p].prm;
		for ( var i = 0; i < prm.length; i++) {
			triggerIds[triggerIds.length] = prm[i].shape.node.id;
		}
	}
	hm.map.canvasWindow.createPerimContextMenu(triggerIds);
}

hm.map.addWallMenu = function() {
	if (!hm.map.mapWritePermission) {
		return;
	}
	var triggerIds = new Array();
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall) {
			triggerIds[triggerIds.length] = wall.shape.node.id;
		}
	}
	hm.map.canvasWindow.createWallContextMenu(triggerIds);
}

hm.map.exitSelectedWall = function() {
	if (hm.map.selectedWall) {
		hm.map.canvasWindow.removeShape(hm.map.surface,
				hm.map.selectedWall.shape);
		hm.map.canvasWindow.removeShape(hm.map.surface,
				hm.map.selectedWall.inside);
		if (hm.map.selectedWall.bi < hm.map.walls.length) {
			hm.map.drawSelectedWalls(hm.map.selectedWall.bi,
					hm.map.selectedWall.ei);
		}
		hm.map.selectedWall = null;
	}
}

hm.map.moveSelectedWall = function(targetId) {
	hm.map.prepareSelectedWall(targetId, false)
}

hm.map.cloneSelectedWall = function(targetId) {
	hm.map.prepareSelectedWall(targetId, true)
}

hm.map.prepareSelectedWall = function(targetId, clone) {
	var wi = findWallIndex(targetId);
	if (wi < 0) {
		return null;
	}
	for ( var ei = wi; ei + 1 < hm.map.walls.length
			&& hm.map.nextConnectedWall(hm.map.walls[ei], ei) != null; ei++)
		;
	for ( var bi = wi; bi > 0
			&& hm.map.prevConnectedWall(hm.map.walls[bi], bi) != null; bi--)
		;
	var dxmin, dxmax, dymin, dymax;
	var first = hm.map.walls[bi];
	dxmin = dxmax = first.x1;
	dymin = dymax = first.y1;
	for ( var i = bi; i <= ei; i++) {
		var wall = hm.map.walls[i];
		if (wall.x2 < dxmin) {
			dxmin = wall.x2;
		} else if (wall.x2 > dxmax) {
			dxmax = wall.x2;
		}
		if (wall.y2 < dymin) {
			dymin = wall.y2;
		} else if (wall.y2 > dymax) {
			dymax = wall.y2;
		}
	}
	dxmax = hm.map.viewWindow.canvasWidth - hm.map.viewWindow.gridBorderX
			- dxmax;
	dymax = hm.map.viewWindow.canvasHeight - hm.map.viewWindow.gridBorderY
			- dymax;
	var tx = 0, ty = 0, txyd = 20;
	if (clone) {
		if (dxmax < txyd) {
			if (dxmin > dxmax) {
				tx = dxmin < txyd ? -dxmin : -txyd;
			} else {
				tx = dxmax;
			}
		} else {
			tx = txyd;
		}
		if (tx == 0) {
			if (dymax < txyd) {
				if (dymin > dymax) {
					ty = dymin < txyd ? dymin : txyd;
				} else {
					ty = -dymax;
				}
			} else {
				ty = -txyd;
			}
		}
	}
	dxmin += tx;
	dxmax -= tx;
	dymin += ty;
	dymax -= ty;
	var path = "M" + (hm.map.walls[bi].x1 + tx) + " "
			+ (hm.map.walls[bi].y1 + ty);
	var closed = hm.map.walls[bi].closed;
	for ( var i = bi; i <= ei; i++) {
		var wall = hm.map.walls[i];
		if (!clone) {
			hm.map.removeHandlePair(i);
			hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
			wall.shape = null;
		}
		if (i < ei || !closed) {
			path += "L" + (wall.x2 + tx) + " " + (wall.y2 + ty);
		}
	}
	if (closed) {
		path += "Z";
	}
	var shape = hm.map.canvasWindow.createRafaelPath(hm.map.surface, path,
			"#98CBFF", 0.6, 13, "");
	var inside = hm.map.canvasWindow.createRafaelPath(hm.map.surface, path,
			"#0000FF", 0.8, 1, "");
	shape.node.style.cursor = "pointer";
	inside.node.style.cursor = "pointer";
	var wdx = 0, wdy = 0;
	var onstart = function(x, y, e) {
	}
	var onmove = function(dx, dy, x, y, e) {
		if (dx < -dxmin) {
			dx = -dxmin;
		}
		if (dx > dxmax) {
			dx = dxmax;
		}
		if (dy < -dymin) {
			dy = -dymin;
		}
		if (dy > dymax) {
			dy = dymax;
		}
		wdx = dx;
		wdy = dy;
		shape.transform("t" + dx + "," + dy);
		inside.transform("t" + dx + "," + dy);
	}
	var onend = function(e) {
		for ( var i = bi; i <= ei; i++) {
			var wall = hm.map.walls[i];
			if (clone) {
				hm.map.walls[hm.map.walls.length] = {
					tp : wall.tp,
					x1 : wall.x1 + wdx + tx,
					y1 : wall.y1 + wdy + ty,
					x2 : wall.x2 + wdx + tx,
					y2 : wall.y2 + wdy + ty,
					closed : wall.closed,
					close : wall.close
				};
			} else {
				wall.x1 += wdx;
				wall.y1 += wdy;
				wall.x2 += wdx;
				wall.y2 += wdy;
			}
		}
		if (clone) {
			if (hm.map.walls[bi].closed) {
				hm.map.walls[hm.map.selectedWall.bi].closed = true;
				hm.map.walls[hm.map.selectedWall.ei].close = hm.map.selectedWall.bi;
			}
		}
		hm.map.exitSelectedWall();
		top.saveWalls();
	}
	shape.drag(onmove, onstart, onend, null, null, null);
	inside.drag(onmove, onstart, onend, null, null, null);
	hm.map.selectedWall = {
		bi : clone ? hm.map.walls.length : bi,
		ei : clone ? hm.map.walls.length + ei - bi : ei,
		shape : shape,
		inside : inside
	};
}

hm.map.removeWall = function(targetId) {
	var any = false;
	var loopStart = -1;
	var loopBroken = false;
	var reconnect = false;
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall && wall.shape) {
			if (wall.closed) {
				loopStart = i;
			}
			if (wall.shape.node.id == targetId) {
				hm.map.t1wall = wall;
				hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
				hm.map.removeHandlePair(i);
				hm.map.walls[i] = null;
				hm.map.newWalls = true;
				if (loopStart > -1) {
					loopBroken = true;
					if (wall.closed) {
						hm.map
								.drawWallHandle(hm.map.walls[i + 1], i + 1,
										false);
					} else {
						hm.map.walls[loopStart].closed = false;
						if (wall.close >= 0) {
							hm.map.drawWallHandle(hm.map.walls[loopStart],
									loopStart, false);
						} else {
							reconnect = true;
						}
					}
				} else {
					if (i + 1 < hm.map.walls.length) {
						var nextWall = hm.map.nextConnectedWall(wall, i);
						if (nextWall) {
							hm.map.drawWallHandle(nextWall, i + 1, false);
						}
					}
				}
			} else {
				any = true;
			}
			if (wall.close >= 0) {
				if (loopBroken) {
					wall.close = -1;
					if (reconnect) {
						hm.map.reconnectWalls(loopStart, i);
					}
				}
				loopStart = -1;
				loopBroken = false;
			}
		}
	}
	if (!any) {
		hm.map.walls = new Array();
	}
	top.invalidateHiddenMaps();
	top.saveWalls();
}

hm.map.nextConnectedWall = function(wall, i) {
	var nextWall = hm.map.walls[i + 1];
	return hm.map.wallsConnected(wall, nextWall) ? nextWall : null;
}

hm.map.prevConnectedWall = function(wall, i) {
	var prevWall = hm.map.walls[i - 1];
	return hm.map.wallsConnected(prevWall, wall) ? prevWall : null;
}

hm.map.wallsConnected = function(wall, nextWall) {
	return wall != null && nextWall != null && nextWall.x1 == wall.x2
			&& nextWall.y1 == wall.y2;
}

hm.map.reconnectWalls = function(from, until) {
	for ( var i = from; i <= until; i++) {
		hm.map.removeHandlePair(i);
	}
	do {
		var lastWall = hm.map.walls[from];
		for ( var i = from + 1; i <= until; i++) {
			var wall = hm.map.walls[i];
			hm.map.walls[i] = lastWall;
			lastWall = wall;
		}
		hm.map.walls[from] = lastWall;
	} while (hm.map.walls[until]);
	hm.map.drawWallHandle(hm.map.walls[from], from, false);
	for ( var i = from; i < until; i++) {
		hm.map.drawWallHandle(hm.map.walls[i], i, true);
	}
}

hm.map.removeAllWalls = function() {
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall) {
			hm.map.removeHandlePair(i);
			hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
		}
	}
	hm.map.walls = new Array();
	hm.map.newWalls = true;
	top.invalidateHiddenMaps();
	top.saveWalls();
}

hm.map.moveWallLabel = function(wall, mp) {
	var div = hm.map.canvasWindow.document.getElementById("wlszr");
	var x = mp.x1 + 5;
	var y = mp.y1 - 20;
	div.style.left = x + "px";
	div.style.top = y + "px";
	div.style.display = "";
	var dx = (mp.x1 - wall.x1) * top.actualScale / hm.map.scale;
	var dy = (mp.y1 - wall.y1) * top.actualScale / hm.map.scale;
	var d = Math.round(Math.sqrt(dx * dx + dy * dy) * 100) / 100;
	div.removeChild(div.firstChild);
	if (hm.map.viewWindow.lengthUnit == 2) {
		var unit = " ft";
	} else {
		var unit = " m";
	}
	div.appendChild(hm.map.canvasWindow.document.createTextNode(d + unit));
}

hm.map.hideWallLabel = function(wall) {
	var div = hm.map.canvasWindow.document.getElementById("wlszr");
	div.style.display = "none";
}

hm.map.wallClick = function(e) {
	hm.map.moveWall = false;
	var mp = hm.map.getMousePos(e);
	if (hm.map.wallMode) {
		if (hm.map.iwall) {
			hm.map.iwall.tp = top.wallType;
			if (Math.abs(hm.map.iwall.x1 - mp.x1) <= 5
					&& Math.abs(hm.map.iwall.y1 - mp.y1) <= 5) {
				if (hm.map.connectedMode) {
					hm.map.moveWall = true;
				} else {
					if (hm.map.iwall.shape) {
						hm.map.canvasWindow.removeShape(hm.map.surface,
								hm.map.iwall.shape);
						hm.map.iwall.shape = null;
					}
					hm.map.iwall = null;
				}
				return;
			}
			hm.map.redrawWall(hm.map.iwall, hm.map.iwall.x1, hm.map.iwall.y1,
					mp);
			hm.map.iwall.x2 = mp.x1;
			hm.map.iwall.y2 = mp.y1;
			hm.map.addWallAttrs(hm.map.iwall, hm.map.walls.length);
			var needHandle = false;
			if (hm.map.connectedMode) {
				if (hm.map.connectedStart < 0) {
					hm.map.connectedStart = hm.map.walls.length;
					if (hm.map.closedMode) {
						hm.map.iwall.closed = true;
					} else {
						needHandle = true;
					}
				}
			} else {
				needHandle = true;
			}
			if (needHandle) {
				hm.map.drawWallHandle(hm.map.iwall, hm.map.walls.length, false);
			}
			hm.map.drawWallHandle(hm.map.iwall, hm.map.walls.length, true);
			hm.map.walls[hm.map.walls.length] = hm.map.iwall;
			hm.map.addWallMenu();
			hm.map.newWalls = true;
			hm.map.t1wall = hm.map.iwall;
			hm.map.hideWallLabel();
			top.invalidateHiddenMaps();
			top.saveWalls();
			if (hm.map.connectedMode) {
				hm.map.iwall = mp;
				hm.map.iwall.tp = top.wallType;
			} else {
				hm.map.iwall = null;
				return;
			}
		} else {
			hm.map.iwall = mp;
			hm.map.iwall.tp = top.wallType;
		}
	} else {
		if (hm.map.perim.length > 0) {
			var wall = hm.map.perim[hm.map.perim.length - 1];
			if (Math.abs(wall.x1 - mp.x1) <= 3
					&& Math.abs(wall.y1 - mp.y1) <= 3) {
				hm.map.moveWall = true;
				return;
			}
			if (wall.shape) {
				hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
				wall.shape = null;
			}
			var first = hm.map.perim[0];
			if (Math.abs(first.x1 - mp.x1) <= 10
					&& Math.abs(first.y1 - mp.y1) <= 10) {
				hm.map.wallDblClick(e);
				return;
			}
			var line = {
				x1 : wall.x1,
				y1 : wall.y1,
				x2 : mp.x1,
				y2 : mp.y1
			};
			wall.shape = hm.map.drawWall(line, -1);
			hm.map.addPerimAttrs(wall, hm.map.perim.length, 9999);
		}
		mp.tp = -1;
		hm.map.perim[hm.map.perim.length] = mp;
	}
	hm.map.moveWall = true;
}

hm.map.wallDblClick = function(e) {
	hm.map.moveWall = false;
	if (hm.map.wallMode) {
		if (hm.map.closedMode) {
			hm.map.closeWall();
		}
		hm.map.hideWallLabel();
		hm.map.showWallLayer(false);
		top.toggleCreateWall(true);
		return;
	} else {
		hm.map.hideWallLabel();
		hm.map.showWallLayer(false);
		top.disablePerimeter();
	}
	if (hm.map.perim.length < 3) {
		hm.map.removePerimShapes();
	} else {
		var first = hm.map.perim[0];
		var last = hm.map.perim[hm.map.perim.length - 1];
		var line = {
			x1 : first.x1,
			y1 : first.y1,
			x2 : last.x1,
			y2 : last.y1
		};
		last.shape = hm.map.drawWall(line, -1);
		hm.map.addPerimAttrs(last, 0, 9999);
		hm.map.submitWalls("validatePerimeter", hm.map.validatedPerimeter,
				hm.map.getPerimData());
	}
}

hm.map.closeWall = function() {
	if (hm.map.connectedStart < 0) {
		return;
	}
	var first = hm.map.walls[hm.map.connectedStart];
	var last = hm.map.walls[hm.map.walls.length - 1];
	var wall = {
		x1 : last.x2,
		y1 : last.y2,
		x2 : first.x1,
		y2 : first.y1
	};
	wall.tp = top.wallType;
	wall.shape = hm.map.drawWall(wall, wall.tp);
	wall.close = hm.map.connectedStart;
	hm.map.addWallAttrs(wall, hm.map.walls.length);
	hm.map.drawWallHandle(wall, hm.map.walls.length, true);
	hm.map.walls[hm.map.walls.length] = wall;
	hm.map.addWallMenu();
	hm.map.newWalls = true;
	hm.map.t1wall = wall;
	hm.map.hideWallLabel();
	top.invalidateHiddenMaps();
	top.saveWalls();
}

hm.map.wallMove = function(e) {
	if (!hm.map.moveWall) {
		// Not in drag wall mode
		return;
	}
	var mp = hm.map.getMousePos(e);
	if (hm.map.wallMode) {
		// Interior wall
		var wall = hm.map.iwall;
		hm.map.iwall.tp = top.wallType;
	} else {
		// Perimeter
		var wall = hm.map.perim[hm.map.perim.length - 1];
	}
	hm.map.redrawWall(wall, wall.x1, wall.y1, mp);
	hm.map.moveWallLabel(wall, mp);
}

hm.map.redrawWall = function(wall, x, y, mp) {
	hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
	wall.shape = null;
	var line = {
		x1 : x,
		y1 : y,
		x2 : mp.x1,
		y2 : mp.y1
	};
	wall.shape = hm.map.drawWall(line, wall.tp);
}

hm.map.changeWallType = function(wall, wallIndex, wallType) {
	wall.tp = wallType;
	hm.map.redrawWall(wall, wall.x2, wall.y2, wall);
	hm.map.addWallAttrs(wall, wallIndex);
	hm.map.addWallMenu();
}

hm.map.removePerimById = function(id) {
	var any = false;
	for ( var p = 0; p < hm.map.prms.length; p++) {
		if (hm.map.prms[p].id == id) {
			hm.map.removePrmShapes(hm.map.prms[p].prm);
			hm.map.prms[p].prm = new Array();
			hm.map.prms[p].id = "";
		} else if (hm.map.prms[p].prm.length > 0) {
			any = true;
		}
	}
	if (!any) {
		hm.map.prms = new Array();
	}
}

hm.map.removePerimShapes = function() {
	hm.map.removePrmShapes(hm.map.perim);
	hm.map.perim = new Array();
}

hm.map.removePrmShapes = function(prm) {
	hm.map.moveWall = false;
	for ( var i = 0; i < prm.length; i++) {
		if (prm[i].shape) {
			hm.map.canvasWindow.removeShape(hm.map.surface, prm[i].shape);
			prm[i].shape = null;
		}
	}
	top.toggleDrawPerimeter();
}

hm.map.getPerimData = function() {
	var fd = "ignore=" + new Date().getTime();
	for ( var i = 0; i < hm.map.perim.length; i++) {
		var wall = hm.map.perim[i];
		fd += "&xs=" + wall.x1 + "&ys=" + wall.y1;
	}
	return fd;
}

hm.map.getWallsData = function() {
	var fd = "ignore=" + new Date().getTime();
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall) {
			fd += "&xs=" + wall.x1 + "&ys=" + wall.y1 + "&xs=" + wall.x2
					+ "&ys=" + wall.y2 + "&tps=" + wall.tp + "&tps=" + wall.tp;
		}
	}
	return fd;
}

hm.map.submitWalls = function(operation, callback, fd) {
	var action = operation == "validatePerimeter" ? "maps" : "mapBld";
	var url = action + ".action?operation=" + operation + "&id=" + hm.map.mapId
			+ "&pageId=" + hm.map.pageId + "&scale=" + hm.map.scale
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest(fd ? 'POST' : 'GET', url,
			{
				success : callback
			}, fd);
}

hm.map.validatedPerimeter = function(o) {
	try {
		eval("var data = " + o.responseText);
	} catch (e) {
		top.showAjaxSubmitError();
		return;
	}
	if (data) {
		if (data.length == 0) {
			hm.map.newPerim = true;
			top.toggleDrawPerimeter();
			hm.map.submitWalls("savePerimeter", hm.map.perimeterSaved, hm.map
					.getPerimData());
			return;
		}
		if (hm.map.pageId != data[0].pageId) {
			return;
		}
		hm.map.llis = new Array();
		for ( var i = 0; i < data.length; i++) {
			var shape = {
				cx : data[i].x,
				cy : data[i].y,
				r : 2
			};
			data[i].shape = hm.map.canvasWindow.createRafaelCircle(
					hm.map.surface, shape, "#F00", 1, 4);
			hm.map.llis[hm.map.llis.length] = data[i];
		}
		hm.map.fadePerimeter();
	}
}

hm.map.perimeterSaved = function(o) {
	eval("var data = " + o.responseText);
	hm.map.newPerim = false;
	if (data.id < 0) {
		hm.map.removePerimShapes();
	} else {
		hm.map.prms[hm.map.prms.length] = {
			"prm" : hm.map.perim,
			"id" : data.id,
			"tp" : data.tp
		};
		for ( var i = 0; i < hm.map.perim.length; i++) {
			var wall = hm.map.perim[i];
			wall.shape.node.id = "p" + i + "_" + data.id;
		}
	}
	hm.map.perim = new Array();
	hm.map.addPerimMenu();
	if (hm.map.planningMode()) {
		hm.map.updateSpillCache(hm.map.viewWindow.getFrequency(),
				document.forms[formName].rssiThreshold.value, true,
				hm.map.updateSpillCacheWallsDone);
	} else if (hm.map.viewWindow.getLayers()) {
		invalidateHeatChannelMap();
	}
}

hm.map.fadePerimeter = function() {
	showNote("Perimeter walls should not intersect.  Please try again.", 4);
	for ( var i = 0; i < hm.map.llis.length; i++) {
		hm.map.llis[i].shape.animate({
			"stroke-opacity" : 0
		}, 3000);
	}
	for ( var i = 1; i < hm.map.perim.length; i++) {
		hm.map.perim[i].shape.animate({
			"stroke-opacity" : 0
		}, 3000);
	}
	hm.map.perim[0].shape.animate({
		"stroke-opacity" : 0
	}, 3000, hm.map.removellis);
}

hm.map.removellis = function() {
	if (hm.map.llis.length == 0) {
		return;
	}
	for ( var i = 0; i < hm.map.llis.length; i++) {
		hm.map.canvasWindow.removeShape(hm.map.surface, hm.map.llis[i].shape);
	}
	hm.map.llis = new Array();
	hm.map.removePerimShapes();
}

/*
 * Request map nodes/links as json objects
 */
hm.map.requestMapNodes = function(apLabels) {
	if (hm.map.viewWindow.gridBorderY > 0 && hm.map.viewWindow.gridSize > 0) {
		hm.map.createGrid();
	}
	if (hm.map.mapWidth > 0) {
		hm.map.canvasWindow.addWallListeners(hm.map.wallClick,
				hm.map.wallDblClick, hm.map.wallMove);
	}
	hm.map.requestMapObjects('nodes', hm.map.processMapNodes, apLabels);
}
hm.map.requestMapLinks = function() {
	hm.map.requestMapObjects("links", hm.map.processMapLinks);
}
hm.map.requestMapDetails = function(callback) {
	hm.map.pageId = new Date().getTime();
	hm.map.requestMapObjects("mapDetails", callback);
}

hm.map.requestMapObjects = function(operation, callback, param1, param2, param3) {
	// To prevent browser from returning a cached version
	var url = "maps.action?operation=" + operation + "&id=" + hm.map.mapId
			+ "&pageId=" + hm.map.pageId + "&ignore=" + new Date().getTime();
	if (operation != "mapDetails") {
		url += "&scale=" + hm.map.scale;
	}
	if (operation == "rssiRange") {
		url += "&rssiThreshold=" + param1;
	} else if (operation == "channels" || (operation == "nextIds")) {
		url += "&frequency=" + param1;
		if (param2 != null) {
			url += "&latchId=" + param2;
		}
	} else if (operation == "clientRssi" || operation == "rogueRssi"
			|| operation == "uncalibrateClientRssi"
			|| operation == "uncalibrateRogueRssi") {
		url += "&bssid=" + param1;
	} else if (operation == "acspNbrRssi") {
		url += "&acspId=" + param1 + "&frequency="
				+ hm.map.viewWindow.getFrequency();
	} else if (operation == "calibrateClientRssi"
			|| operation == "calibrateRogueRssi") {
		url += "&bssid=" + param1 + "&xs=" + param2 + "&ys=" + param3;
	} else if (operation == "alarms") {
		url += "&rogueChecked=" + param1 + "&clientChecked=" + param2
				+ "&summaryChecked=" + param3;
	} else if (operation == "nodes") {
		url += "&apLabels=" + param1;
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : callback
	}, null);
}

hm.map.updateGeoInfoDone = function(o) {
}

hm.map.updateGeoInfo = function(ll, zm, vt, localMapId) {
	var url = "mapBld.action?operation=updateGeo&id=" + localMapId + "&ctrLat="
			+ ll.lat() + "&ctrLong=" + ll.lng() + "&ctrZm=" + zm + "&ctrVt="
			+ vt + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.updateGeoInfoDone
	});
}

hm.map.updateGeoContainerInfo = function(ll, nodeId) {
	var url = "mapBld.action?operation=updateGeoContainer&id="
			+ nodeId.substring(1) + "&ctrLat=" + ll.lat() + "&ctrLong="
			+ ll.lng() + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.updateGeoInfoDone
	});
}

hm.map.selectMarkerIcon = function(isBuilding, severity) {
	if(typeof (severity) == "undefined") {
		return hm.map.markerIcons[isBuilding ? 1 : 0];
	} else {
		return hm.map.containerNodeIcons[isBuilding ? 0 : 6][severity].src;
	}
}

hm.map.createGeoMarker = function(map, ll, wlanNode) {
	function markerDblClick() {
		hm.map.selectNewTreeNode(wlanNode.jsonNode.nodeId.substring(1));
	}
	function markerDragEnd() {
		hm.map.updateGeoContainerInfo(marker.getPosition(),
				wlanNode.jsonNode.nodeId);
	}
//	var icon = hm.map.selectMarkerIcon(wlanNode.jsonNode.ctp == 2, wlanNode.jsonNode.s);
	var icon = hm.map.getContainerNodeIcon(wlanNode.jsonNode.s, wlanNode.jsonNode.i).src;
	var marker = new google.maps.Marker({
		map : map,
		draggable : true,
		icon : icon,
		animation : google.maps.Animation.DROP,
		position : ll,
		title : wlanNode.jsonNode.mapName
	});
	google.maps.event.addListener(marker, 'dragend', markerDragEnd);
	google.maps.event.addListener(marker, 'dblclick', markerDblClick);
}

hm.map.createGeoTempMarker = function(map, ll, geocoder) {
	var address = top.mapGeo.address;
	function processGeo(results, status) {
		function markerDblClick() {
			if (map.getZoom() < 21) {
				map.setOptions({
					zoom : map.getZoom() + 1,
					center : marker.getPosition()
				});
			}
		}
		if (status == google.maps.GeocoderStatus.OK) {
			ll = results[0].geometry.location;
		}
//		var icon = hm.map.selectMarkerIcon(hm.map.bldMode());
		var icon = hm.map.getContainerNodeIcon(0, 0).src;
		var marker = new google.maps.Marker({
			map : map,
			draggable : true,
			icon : icon,
			animation : google.maps.Animation.DROP,
			position : ll,
			title : selectedNode.label
		});
		google.maps.event.addListener(marker, 'dblclick', markerDblClick);
	}
	if (address.length > 0) {
		geocoder.geocode({
			'address' : address
		}, processGeo);
	} else {
		geocoder.geocode({
			'address' : selectedNode.label
		}, processGeo);
	}
}

hm.map.createGeoContainer = function(map, ll, geocoder, wlanNode) {
	function processGeo(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			hm.map.createGeoMarker(map, results[0].geometry.location, wlanNode);
		} else {
			hm.map.createGeoMarker(map, ll, wlanNode);
		}
	}
	if (!wlanNode.jsonNode.na) {
		ll = new google.maps.LatLng(wlanNode.jsonNode.lat,
				wlanNode.jsonNode.lng);
		hm.map.createGeoMarker(map, ll, wlanNode);
	} else if (wlanNode.jsonNode.address.length > 0) {
		geocoder.geocode({
			'address' : wlanNode.jsonNode.address
		}, processGeo);
	} else {
		geocoder.geocode({
			'address' : wlanNode.jsonNode.mapName
		}, processGeo);
	}
}

hm.map.createGeoMap = function(ll, zm, data, localMapId, lls, zms, vts) {
	function centerChanged() {
		var lln = map.getCenter();
		var zmn = map.getZoom();
		var vtn = map.getMapTypeId();
		if (!lls || lls.lat() != lln.lat() || lls.lng() != lln.lng()
				|| zmn != zms || vtn != vts) {
			hm.map.updateGeoInfo(lln, zmn, vtn, localMapId);
			lls = lln;
			zms = zmn;
			vts = vtn;
			top.mapGeo.lat = lln.lat();
			top.mapGeo.lng = lln.lng();
		}
	}
	if (!ll) { // world
		ll = new google.maps.LatLng(33.5505616, -112.4107207);
	}
	var gmdiv = document.getElementById("gmdiv");
	gmdiv.style.width = hm.map.viewWindow.canvasWidth + "px";
	gmdiv.style.height = hm.map.viewWindow.canvasHeight + "px";
	var mapTypeId = top.mapGeo.vt;
	if (mapTypeId.length == 0) {
		mapTypeId = hm.map.bldMode() ? google.maps.MapTypeId.HYBRID
				: google.maps.MapTypeId.ROADMAP;
	}
	var myOptions = {
		zoom : zm,
		center : ll,
		mapTypeId : mapTypeId,
		panControl : false,
		scaleControl : true,
		rotateControl : false,
		tilt : 0,
		disableDoubleClickZoom : hm.map.bldMode(),
		zoomControlOptions : {
			position : google.maps.ControlPosition.LEFT_TOP
		}
	};
	var map = new google.maps.Map(gmdiv, myOptions);
	var geocoder = new google.maps.Geocoder();
	hm.map.addGeoControls(map, geocoder);
	if (hm.map.bldMode() && hm.map.mapWritePermission) {
		hm.map.addBldGeoControls(map);
	}
	top.mapGeo.lat = ll.lat();
	top.mapGeo.lng = ll.lng();
	top.mapGeo.zm = zm;
	top.mapGeo.map = map;
	google.maps.event.addListener(map, "mouseout", centerChanged);
	var hasContainers = false;
	if (data) {
		for ( var i = 0; i < data.length; i++) {
			var wlanNode = {
				jsonNode : data[i]
			};
			if (i == 0) {
				if (hm.map.pageId != wlanNode.jsonNode.pageId) {
					return;
				}
			}
			if (!wlanNode.jsonNode.container) {
				continue;
			}
			hm.map.createGeoContainer(map, ll, geocoder, wlanNode);
			hasContainers = true;
		}
	}
	if (!hasContainers) {
		hm.map.createGeoTempMarker(map, ll, geocoder);
	}
}

hm.map.addGeoControls = function(map, geocoder) {
	function processGeo(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.setCenter(results[0].geometry.location);
		}
	}
	var address = top.mapGeo.address;
	if (address.length > 0) {
		var controlDiv = hm.map.addGeoControl('Home', geocodeAgainMsg);
		google.maps.event.addDomListener(controlDiv, 'click', function() {
			geocoder.geocode({
				'address' : address
			}, processGeo);
		});
		map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
	}
}

hm.map.clickCreateFloor = function(map, busy) {
	hm.map.removeGeoNote(map);
	if (busy) {
		hm.map.addGeoNote(map, closeBldFirstMsg);
	} else if (!top.mapGeo.perims) {
		hm.map.addGeoNote(map, drawBldFirstMsg);
	} else {
		hm.map.addGeoNote(map, createFloorPlanMsg);
		hm.map.submitWalls("saveGeoPerimeter", hm.map.geoPerimSaved, hm.map
				.getGeoPerimData());
	}
}

hm.map.addBldGeoControls = function(map) {
	var perimWall, busy = false;
	var controlDiv = hm.map
			.addGeoControl('Create Floor Plan', bldPerimFloorMsg);
	google.maps.event.addDomListener(controlDiv, 'click', function() {
		hm.map.clickCreateFloor(map, busy);
	});
	map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
	controlDiv = hm.map.addGeoControl('Draw Perimeter', startDrawBldMsg);
	var lastWall, l1, l2, l3;
	var textDiv = controlDiv.firstChild.firstChild;
	function addPerimWall(path) {
		var pline = new google.maps.Polyline({
			path : path,
			strokeColor : "#029FF5",
			strokeOpacity : 1.0,
			strokeWeight : 3,
			clickable : false
		});
		pline.setMap(map);
		return pline;
	}
	function addVertex(me) {
		hm.map.removeGeoNote(map);
		if (lastWall) {
			lastWall.setMap(null);
			// Add vertex to perimWall
			perimWall.getPath().push(me.latLng);
			var plen = perimWall.getPath().getLength();
			if (plen == 3) {
				hm.map.addGeoNote(map, closeBldPerimMsg);
			} else if (plen > 3) {
				var first = perimWall.getPath().getAt(0);
				var d = 6371 * hm.map.getLatLngDistance(first, me.latLng) * 1000;
				if (d < 3) {
					perimWall.getPath().pop();
					hm.map.closeGeoPerimeter(map, perimWall, null, l1, l2, l3,
							textDiv);
					busy = false;
					return;
				}
			}
		} else {
			// Init perimWall with first vertex
			perimWall = addPerimWall([ me.latLng ]);
		}
		// Starts as a dot
		lastWall = addPerimWall([ me.latLng, me.latLng ]);
	}
	function movePerimeter(me) {
		if (lastWall) {
			lastWall.getPath().pop();
			lastWall.getPath().push(me.latLng);
		}
	}
	function closePerimeter(me) {
		hm.map.closeGeoPerimeter(map, perimWall, lastWall, l1, l2, l3, textDiv);
		busy = false;
	}
	function clickDrawPerimeter() {
		if (busy) {
			hm.map.closeGeoPerimeter(map, perimWall, lastWall, l1, l2, l3,
					textDiv);
		} else {
			hm.map.removeGeoNote(map);
			hm.map.addGeoNote(map, clickAndReleaseMsg);
			lastWall = null;
			perimWall = null;
			l1 = google.maps.event.addListener(map, 'click', addVertex);
			l2 = google.maps.event.addListener(map, 'mousemove', movePerimeter);
			l2 = google.maps.event.addListener(map, 'dblclick', closePerimeter);
			map.setOptions({
				draggableCursor : 'crosshair'
			});
			textDiv.innerHTML = "Close Perimeter (Dbl Click)";
			controlDiv.firstChild.title = closeDrawBldMsg;
		}
		busy = !busy;
	}
	google.maps.event.addDomListener(controlDiv, 'click', function() {
		clickDrawPerimeter();
	});
	map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
	hm.map.addGeoNote(map, drawbuildingMsg);
}

hm.map.closeGeoPerimeter = function(map, perimWall, lastWall, l1, l2, l3,
		textDiv) {
	google.maps.event.removeListener(l1);
	google.maps.event.removeListener(l2);
	google.maps.event.removeListener(l3);
	if (lastWall) {
		lastWall.setMap(null);
	}
	if (perimWall) {
		var newPerim = perimWall;
		perimWall = null;
		if (newPerim.getPath().getLength() < 3) {
			newPerim.setMap(null);
		} else {
			var path = newPerim.getPath();
			var last = path.getLength() - 1;
			var lp = path.getAt(last);
			var np = path.getAt(last - 1);
			var dx = Math.abs(lp.lng() - np.lng());
			var dy = Math.abs(lp.lat() - np.lat());
			var eps = 0.00000000001;
			if (dx < eps && dy < eps) {
				path.setAt(last, path.getAt(0));
			} else {
				path.push(path.getAt(0));
			}
			if (top.mapGeo.perims == null) {
				top.mapGeo.perims = new Array();
			}
			top.mapGeo.perims[top.mapGeo.perims.length] = newPerim;
			hm.map.submitWalls("validateGeoPerimeter",
					hm.map.geoPerimValidated, hm.map.getGeoPerimData());
		}
	}
	hm.map.removeGeoNote(map);
	var url = 'url(http://maps.google.com/mapfiles/openhand.cur), move';
	map.setOptions({
		draggableCursor : url
	});
	textDiv.innerHTML = "Draw Perimeter";
	textDiv.parentNode.title = startDrawBldMsg;
}

hm.map.getGeoPerimData = function() {
	var fd = "ignore=" + new Date().getTime();
	for ( var i = 0; i < top.mapGeo.perims.length; i++) {
		var perimWall = top.mapGeo.perims[i];
		perimWall.getPath().forEach(function(node, index) {
			fd += "&lats=" + node.lat() + "&lngs=" + node.lng();
		});
	}
	return fd;
}

hm.map.geoPerimValidated = function(o) {
	eval("var data = " + o.responseText);
	if (!data.success) {
		hm.map.addGeoNote(top.mapGeo.map, perimIntersectMsg);
		var last = top.mapGeo.perims.length - 1;
		top.mapGeo.perims[last].setMap(null);
		if (last == 0) {
			top.mapGeo.perims = null;
		} else {
			var newPerims = new Array();
			for ( var i = 0; i < top.mapGeo.perims.length - 1; i++) {
				newPerims[newPerims.length] = top.mapGeo.perims[i];
			}
			top.mapGeo.perims = newPerims;
		}
	}
}

hm.map.geoPerimSaved = function(o) {
	eval("var data = " + o.responseText);
	if (data.floorId) {
		document.location.href = mapsActionOperationUrl + "viewFloor&id="
				+ data.floorId;
	}
}

hm.map.addGeoControl = function(html, title) {
	var div = document.createElement('div');
	div.style.backgroundColor = 'white';
	div.style.borderStyle = 'solid';
	div.style.borderWidth = '1px';
	div.style.cursor = 'pointer';
	div.title = title;
	var controlDiv = document.createElement('div');
	controlDiv.style.padding = '5px';
	controlDiv.appendChild(div);
	var text = document.createElement('div');
	text.style.fontSize = '12px';
	text.style.padding = '2px 6px 2px 6px';
	text.innerHTML = html;
	div.appendChild(text);
	return controlDiv;
}

hm.map.showGeoNote = function(message) {
	if (top.gme && hm.map.mapWidth == 0 && selectedNode.data.tp == 1) {
		hm.map.removeGeoNote(top.mapGeo.map);
		hm.map.addGeoNote(top.mapGeo.map, "<b>" + message + "<b>");
	} else {
		var td = document.getElementById("note");
		hm.util.replaceChildren(td, document.createTextNode(message));
		hm.util.show('processing');
	}
}

hm.map.removeGeoNote = function(map) {
	if (map.controls[google.maps.ControlPosition.TOP_LEFT].getLength() > 0) {
		map.controls[google.maps.ControlPosition.TOP_LEFT].pop();
	}
}

hm.map.addGeoNote = function(map, html) {
	var div = document.createElement('div');
	div.className = "statusNote initStatusNote";
	var controlDiv = document.createElement('div');
	controlDiv.appendChild(div);
	var text = document.createElement('div');
	text.style.padding = '5px 6px 5px 6px';
	text.innerHTML = html;
	div.appendChild(text);
	map.controls[google.maps.ControlPosition.TOP_LEFT].push(controlDiv);
}

hm.map.processGeoContainer = function(data) {
	var dnodes = data ? data.nodes : null;
	var address = top.mapGeo.address;
	function processGeo(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var ll = results[0].geometry.location;
			var zm = top.mapGeo.pzm < 0 ? 2 : top.mapGeo.pzm + 2;
			if (top.mapGeo.zm > 0) {
				zm = top.mapGeo.zm; // override
			}
			if (!dnodes && address.length > 0 && zm < 18) {
				zm = 18; // Building node with no floors, zoom close.
			}
			hm.map.createGeoMap(ll, zm, dnodes, localMapId, null, -1, null);
		} else {
			hm.map.createGeoMap(null, 2, dnodes, localMapId, null, -1, null);
		}
	}
	var localMapId = hm.map.mapId;
	if (top.mapGeo.ll) {
		var ll = new google.maps.LatLng(top.mapGeo.lat, top.mapGeo.lng);
		hm.map.createGeoMap(ll, top.mapGeo.zm, dnodes, localMapId, ll,
				top.mapGeo.zm, top.mapGeo.vt);
	} else {
		var geocoder = new google.maps.Geocoder();
		if (address.length > 0) {
			geocoder.geocode({
				'address' : address
			}, processGeo);
		} else {
			geocoder.geocode({
				'address' : selectedNode.label
			}, processGeo);
		}
	}
}

hm.map.showStreetMap = function(data) {
	if (top.gme) {
		hm.util.toggleHideElement("cstd", true);
		hm.util.toggleHideElement("gmtd", false);
		hm.map.processGeoContainer(data);
	}
}

hm.map.showDistanceTool = function() {
	var map = top.mapGeo.map;
	var ll = new google.maps.LatLng(top.mapGeo.lat, top.mapGeo.lng);
	function markerDrag(e) {
		mt.setPath([ m1.getPosition(), m2.getPosition() ]);
		hm.map.showDistance(m1.getPosition(), m2.getPosition());
	}
	var ll1 = new google.maps.LatLng(ll.lat(), ll.lng() - .00015);
	var m1 = new google.maps.Marker({
		map : map,
		draggable : true,
		animation : google.maps.Animation.DROP,
		position : ll1,
		title : selectedNode.label
	});
	var ll2 = new google.maps.LatLng(ll.lat(), ll.lng() + .00015);
	var m2 = new google.maps.Marker({
		map : map,
		draggable : true,
		animation : google.maps.Animation.DROP,
		position : ll2,
		title : selectedNode.label
	});
	hm.map.showDistance(m1.getPosition(), m2.getPosition());
	google.maps.event.addListener(m1, 'drag', markerDrag);
	google.maps.event.addListener(m2, 'drag', markerDrag);
	var mtPath = [ ll1, ll2 ];
	var mt = new google.maps.Polyline({
		path : mtPath,
		strokeColor : "#FF0000",
		strokeOpacity : 1.0,
		strokeWeight : 2
	});
	mt.setMap(map);
	top.mapGeo.m1 = m1;
	top.mapGeo.m2 = m2;
	top.mapGeo.mt = mt;
	hm.util.toggleHideElement("distanceLabel", false);
}

hm.map.showDistance = function(p1, p2) {
	var c = hm.map.getLatLngDistance(p1, p2);
	var d = Math.round(6371 * c * 100000) / 100;
	var td = hm.map.viewWindow.document.getElementById('distanceMetric');
	if (td) {
		hm.util.replaceChildren(td, document.createTextNode(d));
	}
	d = Math.round(6371 * c * 100000 / 0.3048) / 100;
	td = hm.map.viewWindow.document.getElementById('distanceFeet');
	if (td) {
		hm.util.replaceChildren(td, document.createTextNode(d));
	}
}

hm.map.getLatLngDistance = function(p1, p2) {
	var dLat = hm.map.degToRad(p2.lat() - p1.lat());
	var dLon = hm.map.degToRad(p2.lng() - p1.lng());
	var dLat1 = hm.map.degToRad(p1.lat());
	var dLat2 = hm.map.degToRad(p2.lat());
	var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(dLat1)
			* Math.cos(dLat1) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

hm.map.degToRad = function(deg) {
	return deg * Math.PI / 180;
}

hm.map.hideDistanceTool = function() {
	hm.util.toggleHideElement("distanceLabel", true);
	var mt = top.mapGeo.mt;
	if (mt) {
		top.mapGeo.mt = null;
		mt.setMap(null);
		var m1 = top.mapGeo.m1;
		top.mapGeo.m1 = null;
		m1.setMap(null);
		var m2 = top.mapGeo.m2;
		top.mapGeo.m2 = null;
		m2.setMap(null);
	}
}

/*
 * Process json map nodes
 */
hm.map.processMapNodes = function(o) {
	eval("var data = " + o.responseText);
	hm.map.noRadioOpt = false;
	if (!data || data.pageId != hm.map.pageId) {
		return;
	}
	if (hm.map.mapWidth == 0) {
		hm.map.showStreetMap(data);
		top.enableHeatChannelBoxes();
	} else {
		hm.map.createGfxSurface();
		hm.map.prms = data.perim;
		hm.map.perim = new Array();
		if (data.walls) {
			hm.map.walls = data.walls;
		} else {
			hm.map.walls = new Array();
		}
		hm.map.llis = new Array();
		hm.map.newPerim = false;
		hm.map.newWalls = false;
		hm.map.moveWall = false;
		hm.map.noRadioOpt = data.noRadio;
		top.toggleDrawPerimeter();
		hm.map.drawPrms();
		hm.map.drawWalls();
		hm.map.processPlannedNodes(data);
		data = data.nodes;
		if (data) {
			var start = new Date().getTime();
			var triggerIds = new Array();
			hm.map.clientNodes = new Array();
			// Enumeration
			hm.map.wlanNodes = new Array();
			// Hash table, to look up from/to nodes
			hm.map.wlanNodesHash = new Array();
			for ( var i = 0; i < data.length; i++) {
				var wlanNode = {
					jsonNode : data[i]
				};
				if (i == 0) {
					if (hm.map.pageId != wlanNode.jsonNode.pageId) {
						return;
					}
				}
				hm.map.addNode(wlanNode);
				if (!wlanNode.jsonNode.container) {
					if (wlanNode.jsonNode.ethId != undefined) {
						hm.map.createEthLink(wlanNode);
					}
				}
				hm.map.wlanNodes[hm.map.wlanNodes.length] = wlanNode;
				hm.map.wlanNodesHash[data[i].nodeId] = wlanNode;
				if (!wlanNode.jsonNode.container
						&& wlanNode.jsonNode.apId.charAt(0) != 'M'
						&& wlanNode.jsonNode.isManaged) {
					triggerIds[triggerIds.length] = wlanNode.jsonNode.nodeId;
				}
			}
			var delta = new Date().getTime() - start;
			hm.map.lockMapNodes();
			hm.map.canvasWindow.createContextMenu(triggerIds,
					hm.map.mapWritePermission);
			hm.map.requestMapLinks();
		}
	}
}

hm.map.hideRadioOptions = function() {
	var opts = document.querySelectorAll('tr.radioOpt, td.radioOpt');
	if(opts && opts.length) {
		for(var i=0;i<opts.length;i++) {
			opts[i].style.display = hm.map.noRadioOpt || false ? "none" : "";
		}
		// hide power-channel from select
		var selector = document.querySelector('select[name="rapLabels"]');
		if(selector && selector.options) {
			if(hm.map.noRadioOpt || false) {
				for(var i=0;i<selector.options.length;i++) {
					if((hm.map.noRadioOpt || false) && selector.options[i].value == 'cp') {
						selector.remove(i);
						break;
					}
				}
			} else {
				for(var i=0;i<selector.options.length;i++) {
					if(selector.options[i].value == 'cp') {
						break;
					} else if(i==selector.options.length-1) {
						var option = document.createElement('option');
						option.value = 'cp';
						option.text = 'Channel/Power';
						try{
							selector.add(option, selector.options[1]);
						}catch(e) {
							selector.add(option, 1);
						}
						break;
					}
				}
			}
		}
	}
}
hm.map.processPlannedNodes = function(data) {
	var apLabels = hm.map.viewWindow.getApLabels();
	var frequency = hm.map.viewWindow.getFrequency();
	hm.map.simNodes = new Array();
	if (!data.planned) {
		return;
	}
	hm.map.updateApCount(data.planned.length);
	if (data.planned.length > 0) {
		for ( var i = 0; i < data.planned.length; i++) {
			var wlanNode = {
				jsonNode : data.planned[i]
			};
			wlanNode.jsonNode.tile = {
				x : 0,
				y : 0
			};
			hm.map.simNodes[hm.map.simNodes.length] = hm.map.addPlannedAp(
					wlanNode, false);
			hm.map.changeNodeLabel(wlanNode, apLabels, frequency);
		}
		hm.map.createPlannedMenu();
	}
}

hm.map.updateApCount = function(count) {
	if (count < 0) {
		count = 0;
		for ( var i = 0; i < hm.map.simNodes.length; i++) {
			if (hm.map.simNodes[i]) {
				count++;
			}
		}
	}
	var td = hm.map.viewWindow.document.getElementById('apCount');
	if (td) {
		hm.util.replaceChildren(td, document.createTextNode(count));
	}
}

hm.map.changeRapNodeLabels = function() {
	var rapLabels = hm.map.viewWindow.getRapLabels();
	if (rapLabels == "cp" || rapLabels == "ac") {
		hm.map.requestMapObjects('channels', hm.map.processMapNodeLabels,
				hm.map.viewWindow.getFrequency(), null);
	} else {
		hm.map.restoreMapNodeLabels(rapLabels);
	}
}

hm.map.changeNodeLabels = function() {
	var apLabels = hm.map.viewWindow.getApLabels();
	var frequency = hm.map.viewWindow.getFrequency();
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		hm.map.changeNodeLabel(hm.map.simNodes[i], apLabels, frequency);
	}
}

hm.map.changeNodeLabel = function(wlanNode, apLabels, frequency) {
	var labelDiv = hm.map.canvasWindow.document.getElementById('ll'
			+ wlanNode.jsonNode.nodeId);
	if (labelDiv != null) {
		if (labelDiv.childNodes.length > 0) {
			if (apLabels == "hn") {
				var label = wlanNode.jsonNode.hostName;
			} else if (apLabels == "cp") {
				if (frequency == 1) {
					if (wlanNode.jsonNode.w1Ebd) {
						var label = wlanNode.jsonNode.ch2 < 0 ? -wlanNode.jsonNode.ch2
								: wlanNode.jsonNode.ch2 + "*";
						label = "Ch " + label + " - Pwr "
								+ wlanNode.jsonNode.pwr2;
					} else {
						var label = wlanNode.jsonNode.apName;
					}
				} else {
					if (wlanNode.jsonNode.w0Ebd) {
						var label = wlanNode.jsonNode.ch1 < 0 ? -wlanNode.jsonNode.ch1
								: wlanNode.jsonNode.ch1 + "*";
						label = "Ch " + label + " - Pwr "
								+ wlanNode.jsonNode.pwr1;
					} else {
						var label = wlanNode.jsonNode.apName;
					}
				}
			} else if (apLabels == "no") {
				var label = null;
			} else {
				var label = wlanNode.jsonNode.apName;
			}
			labelDiv.removeChild(labelDiv.firstChild);
			if (label) {
				labelDiv.className = "leafLabel";
				labelDiv.appendChild(hm.map.canvasWindow.document
						.createTextNode(label));
			} else {
				labelDiv.className = "leafLabelEmpty";
				var img = hm.map.canvasWindow.document.createElement("img");
				img.src = hm.map.spacer.src;
				labelDiv.appendChild(img);
			}
		}
	}
}

hm.map.planningMode = function() {
	if (!hm.map.wlanNodes) {
		return true;
	}
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (!node.jsonNode.container) {
			return false;
		}
	}
	return true;
}

hm.map.bldMode = function() {
	return selectedNode.data.tp == 2;
}

hm.map.removeSimAp = function(targetId) {
	hm.map.newSimNodes = new Array();
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var wlanNode = hm.map.simNodes[i];
		if (wlanNode.jsonNode.nodeId == targetId) {
			break;
		} else {
			hm.map.newSimNodes[hm.map.newSimNodes.length] = wlanNode;
		}
	}
	for (i++; i < hm.map.simNodes.length; i++) {
		hm.map.newSimNodes[hm.map.newSimNodes.length] = hm.map.simNodes[i];
	}
	hm.map.simNodes = hm.map.newSimNodes;
	hm.map.newSimNodes = null;
	hm.map.updateApCount(-1);
	hm.map.createPlannedMenu();
	var planDiv = hm.map.canvasWindow.document.getElementById("plan");
	var div = hm.map.canvasWindow.document.getElementById("h"
			+ wlanNode.jsonNode.nodeId);
	if (div) {
		planDiv.removeChild(div);
	}
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var div = hm.map.canvasWindow.document
			.getElementById(wlanNode.jsonNode.nodeId);
	if (div) {
		anchor.removeChild(div);
	}
	var tile = wlanNode.jsonNode.tile;
	if (!tile.w) {
		tile.w = -1;
		tile.h = -1;
	}
	var url = "mapBld.action?operation=removeSimAp&id=" + hm.map.mapId
			+ "&pageId=" + hm.map.pageId + "&ch1=" + tile.x + "&ch2=" + tile.y
			+ "&pwr1=" + tile.w + "&pwr2=" + tile.h + "&scale=" + hm.map.scale
			+ "&nodeId=" + targetId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.simApRemoved
	});
	top.toggleRemoveAps();
}

hm.map.simApRemoved = function(o) {
	eval("var data = " + o.responseText);
	if (data && data.w > 0) {
		hm.map.repairSimApLaps(null, data, data);
	}
	hm.map.repairSpilled();
}

hm.map.removeAllSimAps = function() {
	var url = "mapBld.action?operation=removeAllSimAps&id=" + hm.map.mapId
			+ "&pageId=" + hm.map.pageId + "&scale=" + hm.map.scale
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.allSimApsRemoved
	});
}

hm.map.allSimApsRemoved = function(o) {
	hm.map.updateApCount(0);
	var planDiv = hm.map.canvasWindow.document.getElementById("plan");
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var wlanNode = hm.map.simNodes[i];
		var div = hm.map.canvasWindow.document.getElementById("h"
				+ wlanNode.jsonNode.nodeId);
		if (div) {
			planDiv.removeChild(div);
		}
		var div = hm.map.canvasWindow.document
				.getElementById(wlanNode.jsonNode.nodeId);
		if (div) {
			anchor.removeChild(div);
		}
	}
	hm.map.simNodes = new Array();
	top.toggleRemoveAps();
	hm.map.repairSpilled();
	
	// re-create the tabs to get back the "Device" tab
	hm.map.loadMap(hm.map.mapId);
}

hm.map.addCanvasAttrs = function() {
	var imgWidth = hm.map.viewWindow.canvasWidth
			- hm.map.viewWindow.gridBorderX;
	var imgHeight = hm.map.viewWindow.canvasHeight
			- hm.map.viewWindow.gridBorderY;
	return "&id=" + hm.map.mapId + "&pageId=" + hm.map.pageId + "&canvasWidth="
			+ imgWidth + "&canvasHeight=" + imgHeight + "&scale="
			+ hm.map.scale + "&ignore=" + new Date().getTime();
}

hm.map.autoSimAps = function(hwModel, pwr1, ch1, pwr2, ch2) {
	var rssi = document.forms[formName].rssiThreshold.value;
	if (hm.map.viewWindow.getLayers() == 4) {
		rssi = 95 - document.forms[formName].snrThreshold.value
				- document.forms[formName].fadeMargin.value;
	}
	var url = "maps.action?operation=autoSimAps" + hm.map.addCanvasAttrs()
			+ "&rssiThreshold=" + rssi + "&frequency="
			+ hm.map.viewWindow.getFrequency() + "&hwModel=" + hwModel
			+ "&pwr1=" + pwr1 + "&ch1=" + ch1 + "&pwr2=" + pwr2 + "&ch2=" + ch2;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.autoSimApsDone
	});
}

hm.map.autoSimApsDone = function(o) {
	eval("var data = " + o.responseText);
	setTimeout(function() {hm.util.hide('processing');}, 5000);
	if (data) {
		if (data.error) {
			top.showWarnDialog(data.error);
		} else {
			hm.map.processPlannedNodes(data);
			top.toggleRemoveAps();
			hm.map.updateSimApLaps(hm.map.processSimApLaps, hm.map.viewWindow
					.getFrequency(),
					document.forms[formName].rssiThreshold.value,
					document.forms[formName].snrThreshold.value,
					hm.map.viewWindow.getRateThreshold(), null, "");
		}
	}
}

hm.map.findSimAp = function(targetId) {
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var wlanNode = hm.map.simNodes[i];
		if (wlanNode.jsonNode.nodeId == targetId) {
			return wlanNode;
		}
	}
	return null;
}

hm.map.requestSimAp = function(frequency, rssiThreshold, snrThreshold, hwModel,
		pwr1, ch1, pwr2, ch2) {
	if (!hm.map.requestSimAp.reqCount) {
		hm.map.requestSimAp.reqCount = 0;
	}
	var layers = hm.map.viewWindow.getLayers();
	if (layers == 4) {
		rssiThreshold = 95 - document.forms[formName].snrThreshold.value
				- document.forms[formName].fadeMargin.value;
	}
	var imgWidth = hm.map.viewWindow.canvasWidth
			- hm.map.viewWindow.gridBorderX;
	var locateX = parseInt((100 + 10 * hm.map.requestSimAp.reqCount++)
			% imgWidth);
	var url = "maps.action?operation=addSimAp&hwModel=" + hwModel
			+ hm.map.addCanvasAttrs() + "&layers=" + layers + "&frequency="
			+ frequency + "&rssiThreshold=" + rssiThreshold + "&snrThreshold="
			+ snrThreshold + "&rateThreshold="
			+ hm.map.viewWindow.getRateThreshold() + "&pwr1=" + pwr1 + "&ch1="
			+ ch1 + "&pwr2=" + pwr2 + "&ch2=" + ch2 + "&latchId=" + locateX;
	var fd = hm.map.getSimApsCh();
	var transaction = YAHOO.util.Connect.asyncRequest(fd ? 'POST' : 'GET', url,
			{
				success : hm.map.processSimAp
			}, fd);
}

hm.map.processSimAp = function(o) {
	eval("var data = " + o.responseText);
	if (data) {
		if (data.error) {
			top.showWarnDialog(data.error);
		} else if (data.nodeId && hm.map.planningMode()) {
			var apLabels = hm.map.viewWindow.getApLabels();
			var frequency = hm.map.viewWindow.getFrequency();
			var wlanNode = hm.map.addPlannedAp({
				jsonNode : data
			}, true);
			hm.map.changeNodeLabel(wlanNode, apLabels, frequency);
			hm.map.repairSimApDetails(data.planned, null, true);
			hm.map.repairSimApLaps(null, wlanNode.jsonNode.tile,
					wlanNode.jsonNode.tile);
			hm.map.simNodes[hm.map.simNodes.length] = wlanNode;
			hm.map.updateApCount(-1);
			hm.map.createPlannedMenu();
			top.toggleRemoveAps();
			if (hm.map.spill) {
				wlanNode.jsonNode.refresh = true;
			}
			hm.map.repairSpilled();
		}
	}
}

hm.map.createPlannedMenu = function() {
	if (!hm.map.mapWritePermission) {
		return;
	}
	var triggerIds = new Array();
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		triggerIds[triggerIds.length] = hm.map.simNodes[i].jsonNode.nodeId;
	}
	hm.map.canvasWindow.createSimApContextMenu(triggerIds,
			hm.map.mapWritePermission);
}

hm.map.addPlannedAp = function(wlanNode, requestLap) {
	hm.map.addNode(wlanNode);
	if (!hm.map.mapWritePermission) {
		wlanNode.dragWrapper.lock();
	}
	var planDiv = hm.map.canvasWindow.document.getElementById("plan");
	var div = hm.map.canvasWindow.document.createElement("div");
	div.id = "h" + wlanNode.jsonNode.nodeId;
	div.style.position = "absolute";
	var img = hm.map.canvasWindow.document.createElement("img");
	if (wlanNode.jsonNode.tile.w) {
		img.width = wlanNode.jsonNode.tile.w;
		img.height = wlanNode.jsonNode.tile.h;
		if (!YAHOO.env.ua.ie) {
			img.src = hm.map.spacer.src;
		}
	} else {
		img.src = hm.map.spacer.src;
	}
	hm.map.setSimApMapDxy(wlanNode);
	img.className = "planMap";
	div.appendChild(img);
	planDiv.appendChild(div);
	hm.map.moveSimMap(div, wlanNode);
	if (requestLap) {
		hm.map.requestSimApLap(img, wlanNode);
	}
	return wlanNode;
}

hm.map.setSimApMapDxy = function(wlanNode) {
	wlanNode.jsonNode.dx = YAHOO.util.Dom
			.getX(wlanNode.dragWrapper.getDragEl())
			- wlanNode.jsonNode.tile.x;
	wlanNode.jsonNode.dy = YAHOO.util.Dom
			.getY(wlanNode.dragWrapper.getDragEl())
			- wlanNode.jsonNode.tile.y;
}

hm.map.requestSimApLap = function(img, wlanNode) {
	var layers = hm.map.viewWindow.getLayers();
	if (layers == 0) {
		return;
	}
	var frequency = hm.map.viewWindow.getFrequency();
	if (frequency == 1) {
		var power = wlanNode.jsonNode.pwr2;
		var chi = wlanNode.jsonNode.ch2i;
	} else {
		var power = wlanNode.jsonNode.pwr1;
		var chi = wlanNode.jsonNode.ch1i;
	}
	var channelWidth = hm.map.viewWindow.getChannelWidth();
	var fadeMargin = document.forms[formName].fadeMargin.value;
	var url = "maps.action?operation=predictedLap&channelWidth=" + channelWidth
			+ "&fadeMargin=" + fadeMargin + hm.map.addCanvasAttrs()
			+ "&rssiThreshold=" + document.forms[formName].rssiThreshold.value
			+ "&snrThreshold=" + document.forms[formName].snrThreshold.value
			+ "&rateThreshold=" + hm.map.viewWindow.getRateThreshold()
			+ "&frequency=" + frequency + "&layers=" + layers + "&bssid="
			+ wlanNode.jsonNode.nodeId + "&pwr1=" + power + "&ch1=" + chi;
	img.src = url;
}

hm.map.getSimApsCh = function() {
	var fd = "ignore=" + new Date().getTime();
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		fd += "&ch1s=" + hm.map.simNodes[i].jsonNode.ch1 + "&ch1is="
				+ hm.map.simNodes[i].jsonNode.ch1i + "&ch2s="
				+ hm.map.simNodes[i].jsonNode.ch2 + "&ch2is="
				+ hm.map.simNodes[i].jsonNode.ch2i;
	}
	return fd;
}

hm.map.updateSimApLaps = function(callback, frequency, rssiThreshold,
		snrThreshold, rateThreshold, fd, nodeId) {
	var layers = hm.map.viewWindow.getLayers();
	var channelWidth = hm.map.viewWindow.getChannelWidth();
	var fadeMargin = document.forms[formName].fadeMargin.value;
	var url = "maps.action?operation=updateSimApLaps&channelWidth="
			+ channelWidth + "&fadeMargin=" + fadeMargin
			+ hm.map.addCanvasAttrs() + "&rssiThreshold=" + rssiThreshold
			+ "&snrThreshold=" + snrThreshold + "&rateThreshold="
			+ rateThreshold + "&frequency=" + frequency + "&layers=" + layers
			+ "&bssid=" + nodeId;
	var transaction = YAHOO.util.Connect.asyncRequest(fd ? 'POST' : 'GET', url,
			{
				success : callback
			}, fd);
}

hm.map.cacheSpill = function() {
	hm.map.spill = false;
	var layers = hm.map.viewWindow.getLayers();
	if (layers >= 4) { // CJS
		return;
	}
	var url = "maps.action?operation=cacheSpill" + hm.map.addCanvasAttrs();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.cacheSpillDone
	});
}

hm.map.cacheSpillDone = function(o) {
	eval("var data = " + o.responseText);
	if (!data.success) {
		hm.map.spill = false;
		return;
	}
	hm.map.spill = true;
	hm.map.stopRepairQueue();
	var queueIndex = hm.map.imgQueueIndex + 1;
	if (queueIndex >= 0) {
		hm.map.stopImgQueue();
	}
	hm.map.createSpillDiv(data);
	for ( var i = 0; i < data.spilled.length; i++) {
		var wlanNode = hm.map.findSimAp(data.spilled[i].nodeId);
		if (!wlanNode) {
			continue;
		}
		wlanNode.jsonNode.refresh = true;
	}
	hm.map.refreshFromCache(queueIndex >= 0);
}

hm.map.refreshFromCache = function(all) {
	var spillOnly = hm.map.viewWindow.getLayers() == 0;
	var showSpill = hm.map.refreshSpill(spillOnly);
	if (spillOnly) {
		return;
	}
	if (all) {
		hm.map.stopCacheQueue();
		hm.map.cacheQueueSpill = showSpill;
		hm.map.startCacheQueue();
		return;
	}
	for ( var i = hm.map.simNodes.length - 1; i >= 0; i--) {
		var wlanNode = hm.map.simNodes[i];
		var div = hm.map.canvasWindow.document.getElementById("h"
				+ wlanNode.jsonNode.nodeId);
		if (div) {
			var img = div.firstChild;
			if (wlanNode.jsonNode.refresh) {
				wlanNode.jsonNode.refresh = false;
				var url = "mapBld.action?operation=spilled&id=" + hm.map.mapId
						+ "&showSpill=" + showSpill + "&nodeId="
						+ wlanNode.jsonNode.nodeId + "&ignore="
						+ new Date().getTime();
				img.src = url;
			}
		}
	}
}

hm.map.stopCacheQueue = function() {
	hm.map.cacheQueueIndex = -1;
}

hm.map.startCacheQueue = function() {
	hm.map.cacheQueueIndex = hm.map.simNodes.length;
	for ( var i = 0; i < 6; i++) {
		hm.map.popCacheQueue();
	}
}

hm.map.popCacheQueue = function() {
	var queueIndex = --hm.map.cacheQueueIndex;
	if (queueIndex < 0) {
		return;
	}
	if (queueIndex < hm.map.simNodes.length) {
		var wlanNode = hm.map.simNodes[queueIndex];
		if (wlanNode) {
			var div = hm.map.canvasWindow.document.getElementById("h"
					+ wlanNode.jsonNode.nodeId);
			if (div) {
				var img = div.firstChild;
				img.onload = hm.map.cacheQueueLoaded;
				var url = "mapBld.action?operation=spilled&id=" + hm.map.mapId
						+ "&showSpill=" + hm.map.cacheQueueSpill + "&nodeId="
						+ wlanNode.jsonNode.nodeId + "&ignore="
						+ new Date().getTime();
				img.src = url;
			}
		}
	} else {
		hm.map.stopCacheQueue();
	}
}

hm.map.cacheQueueLoaded = function() {
	this.onload = null;
	hm.map.popCacheQueue();
}

hm.map.updateSpillCache = function(frequency, rssiThreshold, spillOnly,
		callback) {
	hm.map.spill = false;
	var channelWidth = hm.map.viewWindow.getChannelWidth();
	var url = "maps.action?operation=updateSpillCache&channelWidth="
			+ channelWidth + hm.map.addCanvasAttrs() + "&rssiThreshold="
			+ rssiThreshold + "&frequency=" + frequency + "&showSpill="
			+ spillOnly;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : callback
	});
}

hm.map.updateSpillCacheDone = function(o) {
	eval("var data = " + o.responseText);
	if (!data.success) {
		hm.map.spill = false;
		return;
	}
	hm.map.spill = true;
	var showSpill = hm.map.refreshSpill(true);
}

hm.map.updateSpillCacheWallsDone = function(o) {
	eval("var data = " + o.responseText);
	if (data.success) {
		hm.map.spill = true;
		var showSpill = hm.map.refreshSpill(false);
	} else {
		hm.map.spill = false;
	}
	hm.map.redoSimApLaps();
	hm.map.t1wall = null;
	hm.map.t2wall = null;
	hm.map.t3wall = null;
	hm.map.t4wall = null;
}

hm.map.createSpillDiv = function(data) {
	var div = hm.map.canvasWindow.document.getElementById("spill");
	if (div) {
		return;
	}
	var planDiv = hm.map.canvasWindow.document.getElementById("planSpill");
	div = hm.map.canvasWindow.document.createElement("div");
	div.id = "spill";
	div.style.position = "absolute";
	var img = hm.map.canvasWindow.document.createElement("img");
	img.width = data.w;
	img.height = data.h;
	if (!YAHOO.env.ua.ie) {
		img.src = hm.map.spacer.src;
	}
	img.className = "planMap";
	div.appendChild(img);
	planDiv.appendChild(div);
}

hm.map.refreshSpill = function(spillOnly) {
	if (!hm.map.planningMode()) {
		return;
	}
	var showSpill = document.forms[formName].showSpill.checked;
	var div = hm.map.canvasWindow.document.getElementById("spill");
	if (div) {
		if (showSpill) {
			div.style.display = "";
		} else {
			div.style.display = "none";
			return showSpill;
		}
		var img = div.firstChild;
		var url = "mapBld.action?operation=spill&id=" + hm.map.mapId
				+ "&showSpill=" + spillOnly + "&ignore=" + new Date().getTime();
		img.src = url;
	}
	return showSpill;
}

hm.map.toggleHideSpill = function(hide) {
	var div = hm.map.canvasWindow.document.getElementById("spill");
	if (div) {
		if (div.style.display.length > 0) {
			// div.firstChild.src = hm.map.spacer.src;
		}
		div.style.display = hide ? "none" : "";
	}
}

hm.map.repairSimApDetails = function(data, nodeId, refresh) {
	var layers = hm.map.viewWindow.getLayers();
	var frequency = hm.map.viewWindow.getFrequency();
	var apLabels = hm.map.viewWindow.getApLabels();
	for ( var i = 0; i < data.length; i++) {
		var wlanNode = hm.map.findSimAp(data[i].nodeId);
		if (!wlanNode) {
			continue;
		}
		wlanNode.jsonNode.ch1 = data[i].ch1;
		wlanNode.jsonNode.ch2 = data[i].ch2;
		if (wlanNode.jsonNode.ch1i != data[i].ch1i) {
			if (layers == 2 && frequency == 2 && data[i].nodeId != nodeId
					&& refresh) {
				wlanNode.jsonNode.refresh = true;
			}
			wlanNode.jsonNode.ch1i = data[i].ch1i;
		}
		if (wlanNode.jsonNode.ch2i != data[i].ch2i) {
			if (layers == 2 && frequency == 1 && data[i].nodeId != nodeId
					&& refresh) {
				wlanNode.jsonNode.refresh = true;
			}
			wlanNode.jsonNode.ch2i = data[i].ch2i;
		}
		wlanNode.jsonNode.apName = data[i].apName;
		hm.map.changeNodeLabel(wlanNode, apLabels, frequency);
	}
}

hm.map.processSimApLap = function(o) {
	// moved or updated
	eval("var data = " + o.responseText);
	if (data && data.length > 0) {
		var wlanNode = hm.map.findSimAp(data[0].nodeId);
		hm.map.repairSimApDetails(data[0].planned, data[0].nodeId,
				wlanNode.jsonNode.tile.w);
		if (wlanNode.jsonNode.tile.w) {
			hm.map.repairSimApLaps(data[0].nodeId, wlanNode.jsonNode.tile,
					data[0].tile);
		}
		var wlanNode = hm.map.findSimAp(data[0].nodeId);
		if (wlanNode) {
			var img = hm.map.processSimApNode(wlanNode, data[0].tile,
					wlanNode.jsonNode.ch1i, wlanNode.jsonNode.ch2i);
			if (hm.map.spill) {
				wlanNode.jsonNode.refresh = true;
			} else { // Don't queue this one
				hm.map.requestSimApLap(img, wlanNode);
			}
		}
		hm.map.repairSpilled();
	}
}

hm.map.repairSpilled = function() {
	if (hm.map.spill) {
		var url = "mapBld.action?operation=repairSpilled"
				+ hm.map.addCanvasAttrs();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
			success : hm.map.repairSpilledDone
		});
	} else {
		hm.map.stopRepairQueue();
		var queueIndex = hm.map.imgQueueIndex + 1;
		if (queueIndex >= 0) {
			var repairCount = 0, doubleCount = 0;
			for ( var i = 0; i < hm.map.simNodes.length; i++) {
				var wlanNode = hm.map.simNodes[i];
				if (wlanNode.jsonNode.refresh) {
					repairCount++;
					if (i >= queueIndex) {
						doubleCount++;
						wlanNode.jsonNode.refresh = false;
					}
				}
			}
		}
		hm.map.startRepairQueue();
	}
}

hm.map.repairSpilledDone = function(o) {
	eval("var data = " + o.responseText);
	if (!data.success) {
		return;
	}
	hm.map.refreshFromCache(false);
}

hm.map.processSimApLaps = function(o) {
	eval("var data = " + o.responseText);
	if (data && data.length == hm.map.simNodes.length) {
		hm.map.processSimApNodes(data);
		if (document.forms[formName].zoom.value == 1) {
			hm.map.canvasWindow.scrollUp();
		}
		if (selectedNode.data.tp == 3) {
			hm.map.cacheSpill();
		} else {
			hm.map.spill = false;
		}
	}
}

hm.map.processSimApNodes = function(data) {
	hm.map.stopImgQueue();
	hm.map.stopRepairQueue();
	hm.map.stopCacheQueue();
	for ( var i = 0; i < data.length; i++) {
		var wlanNode = hm.map.findSimAp(data[i].nodeId);
		if (!wlanNode) {
			continue;
		}
		wlanNode.jsonNode.refresh = false; // In case it was in the repair
		// queue
		var img = hm.map.processSimApNode(wlanNode, data[i].tile,
				wlanNode.jsonNode.ch1i, wlanNode.jsonNode.ch2i);
	}
	hm.map.startImgQueue();
}

hm.map.stopImgQueue = function() {
	hm.map.imgQueueIndex = -1000000;
}

hm.map.startImgQueue = function() {
	hm.map.imgQueueIndex = -1;
	for ( var i = 0; i < 5; i++) {
		hm.map.popImgQueue();
	}
}

hm.map.popImgQueue = function() {
	var queueIndex = ++hm.map.imgQueueIndex;
	if (queueIndex < 0) {
		return;
	}
	if (queueIndex < hm.map.simNodes.length) {
		var wlanNode = hm.map.simNodes[queueIndex];
		if (wlanNode) {
			var div = hm.map.canvasWindow.document.getElementById("h"
					+ wlanNode.jsonNode.nodeId);
			if (div) {
				var img = div.firstChild;
				img.onload = hm.map.imgQueueLoaded;
				hm.map.requestSimApLap(img, wlanNode);
			}
		}
	} else {
		hm.map.stopImgQueue();
	}
}

hm.map.imgQueueLoaded = function() {
	this.onload = null;
	hm.map.popImgQueue();
}

hm.map.stopRepairQueue = function() {
	hm.map.repairQueueIndex = -1000000;
}

hm.map.startRepairQueue = function() {
	hm.map.repairQueueIndex = -1;
	for ( var i = 0; i < 4; i++) {
		hm.map.popRepairQueue();
	}
}

hm.map.popRepairQueue = function() {
	var queueIndex = ++hm.map.repairQueueIndex;
	if (queueIndex < 0) {
		return;
	}
	while (queueIndex < hm.map.simNodes.length) {
		var wlanNode = hm.map.simNodes[queueIndex];
		if (wlanNode && wlanNode.jsonNode.refresh) {
			wlanNode.jsonNode.refresh = false;
			if (hm.map.repairQueueIndex >= 0) {
				hm.map.repairQueueIndex = queueIndex;
			}
			var div = hm.map.canvasWindow.document.getElementById("h"
					+ wlanNode.jsonNode.nodeId);
			if (div) {
				var img = div.firstChild;
				if (!img.onload) {
					img.onload = hm.map.repairQueueLoaded;
					hm.map.requestSimApLap(img, wlanNode);
				}
			}
			return;
		}
		queueIndex++;
	}
	hm.map.stopRepairQueue();
}

hm.map.repairQueueLoaded = function() {
	this.onload = null;
	hm.map.popRepairQueue();
}

hm.map.repairSimApLaps = function(nodeId, oldTile, newTile) {
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var wlanNode = hm.map.simNodes[i];
		if (nodeId == wlanNode.jsonNode.nodeId) {
			continue;
		}
		if (wlanNode.jsonNode.refresh) {
			continue;
		}
		if (!wlanNode.jsonNode.tile.w) {
			continue;
		}
		if (hm.map.redoSimApNbr(wlanNode.jsonNode.tile, oldTile)
				|| hm.map.redoSimApNbr(wlanNode.jsonNode.tile, newTile)) {
			wlanNode.jsonNode.refresh = true;
		}
	}
}

hm.map.redoSimApNbr = function(nbrTile, tile) {
	if (nbrTile.x + nbrTile.w < tile.x) {
		return false;
	}
	if (tile.x + tile.w < nbrTile.x) {
		return false;
	}
	if (nbrTile.y + nbrTile.h < tile.y) {
		return false;
	}
	if (tile.y + tile.h < nbrTile.y) {
		return false;
	}
	return true;
}

hm.map.redoSimApLaps = function() {
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var wlanNode = hm.map.simNodes[i];
		if (!hm.map.t1wall || hm.map.redoSimApLap(wlanNode, hm.map.t1wall)
				|| hm.map.redoSimApLap(wlanNode, hm.map.t2wall)
				|| hm.map.redoSimApLap(wlanNode, hm.map.t3wall)
				|| hm.map.redoSimApLap(wlanNode, hm.map.t4wall)) {
			wlanNode.jsonNode.refresh = true;
		}
	}
	hm.map.repairSpilled();
}

hm.map.redoSimApLap = function(wlanNode, wall) {
	if (!wlanNode.jsonNode.tile.w || !wall) {
		return false;
	}
	var edge = wlanNode.jsonNode.tile.x;
	if (wall.x1 < edge && wall.x2 < edge) {
		return false;
	}
	edge += wlanNode.jsonNode.tile.w;
	if (wall.x1 > edge && wall.x2 > edge) {
		return false;
	}
	edge = wlanNode.jsonNode.tile.y;
	if (wall.y1 < edge && wall.y2 < edge) {
		return false;
	}
	edge += wlanNode.jsonNode.tile.h;
	if (wall.y1 > edge && wall.y2 > edge) {
		return false;
	}
	return true;
}

hm.map.processSimApNode = function(wlanNode, tile, ch1i, ch2i) {
	var div = hm.map.canvasWindow.document.getElementById("h"
			+ wlanNode.jsonNode.nodeId);
	var img = div.firstChild;
	wlanNode.jsonNode.tile = tile;
	wlanNode.jsonNode.ch1i = ch1i;
	wlanNode.jsonNode.ch2i = ch2i;
	img.width = tile.w;
	img.height = tile.h;
	hm.map.setSimApMapDxy(wlanNode);
	hm.map.moveSimMap(div, wlanNode);
	return img;
}

hm.map.moveSimMap = function(div, wlanNode) {
	var nx = YAHOO.util.Dom.getX(wlanNode.dragWrapper.getDragEl());
	var ny = YAHOO.util.Dom.getY(wlanNode.dragWrapper.getDragEl());
	var x = nx - wlanNode.jsonNode.dx;
	var y = ny - wlanNode.jsonNode.dy;
	div.style.left = x + "px";
	div.style.top = y + "px";
}

hm.map.apMoved = function(wlanNode) {
	top.invalidateHeatChannelMap();
	var x = YAHOO.util.Dom.getX(wlanNode.dragWrapper.getDragEl())
			+ wlanNode.iconCX - hm.map.viewWindow.gridBorderX - 1;
	var y = YAHOO.util.Dom.getY(wlanNode.dragWrapper.getDragEl())
			+ wlanNode.iconCY - hm.map.viewWindow.gridBorderY;
	var url = "maps.action?operation=saveNode&id=" + hm.map.mapId + "&pageId="
			+ hm.map.pageId + "&scale=" + hm.map.scale + "&bssid="
			+ wlanNode.jsonNode.nodeId + "&pwr1=" + x + "&ch1=" + y
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : null
	});
}

hm.map.simApMoved = function(wlanNode) {
	top.invalidateHiddenMaps();
	var frequency = hm.map.viewWindow.getFrequency();
	var rssiThreshold = document.forms[formName].rssiThreshold.value;
	var snrThreshold = document.forms[formName].snrThreshold.value;
	var rateThreshold = hm.map.viewWindow.getRateThreshold();
	hm.map.updateSimApLaps(hm.map.processSimApLap, frequency, rssiThreshold,
			snrThreshold, rateThreshold, hm.map.getSimApsCh() + "&pwr2="
					+ wlanNode.jsonNode.x + "&ch2=" + wlanNode.jsonNode.y,
			wlanNode.jsonNode.nodeId);
}

hm.map.drawPrms = function() {
	for ( var i = 0; i < hm.map.prms.length; i++) {
		hm.map.drawPerimeter(hm.map.prms[i].prm, hm.map.prms[i].id);
	}
	hm.map.addPerimMenu();
}

hm.map.drawPerimeter = function(prm, pid) {
	if (prm.length < 2) {
		return;
	}
	var first = prm[0];
	for ( var i = 1; i < prm.length; i++) {
		var wall = prm[i];
		var line = {
			x1 : first.x1,
			y1 : first.y1,
			x2 : wall.x1,
			y2 : wall.y1
		};
		first.shape = hm.map.drawWall(line, -1);
		hm.map.addPerimAttrs(first, i, pid);
		first = wall;
	}
	first = prm[0];
	var line = {
		x1 : first.x1,
		y1 : first.y1,
		x2 : wall.x1,
		y2 : wall.y1
	};
	wall.shape = hm.map.drawWall(line, -1);
	hm.map.addPerimAttrs(wall, 0, pid);
}

hm.map.drawWalls = function() {
	hm.map.drawSelectedWalls(0, hm.map.walls.length - 1);
}

hm.map.drawSelectedWalls = function(from, until) {
	var lastWall = null;
	for ( var i = from; i <= until; i++) {
		var wall = hm.map.walls[i];
		if (wall == null) {
			continue;
		}
		var line = {
			x1 : wall.x1,
			y1 : wall.y1,
			x2 : wall.x2,
			y2 : wall.y2
		};
		wall.shape = hm.map.drawWall(line, wall.tp);
		hm.map.addWallAttrs(wall, i);
		if (hm.map.mapWritePermission) {
			if (lastWall == null || lastWall.x2 != wall.x1
					|| lastWall.y2 != wall.y1) {
				if (!wall.closed) {
					hm.map.drawWallHandle(wall, i, false);
				}
			}
			hm.map.drawWallHandle(wall, i, true);
		}
		lastWall = wall;
	}
	hm.map.addWallMenu();
}
hm.map.addEventListener = function(element, type, listener, useCapture) {
	if(element) {
		if(element.addEventListener) {
			element.addEventListener(type, listener, useCapture);
		} else if(element.attachEvent) {
			element.attachEvent('on'+type, listener);
		}
	}
}
hm.map.drawWallHandle = function(wall, id, first) {
	if (first) {
		var wx2 = wall.x2;
		var wy2 = wall.y2;
		var did = "wo";
	} else {
		var wx2 = wall.x1;
		var wy2 = wall.y1;
		var did = "wt";
	}
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var div = hm.map.canvasWindow.document.getElementById(did + id);
	if (div) {
		anchor.removeChild(div);
	}
	div = hm.map.canvasWindow.document.createElement("div");
	div.id = did + id;
	if (id == hm.map.walls.length) {
		div.style.display = "none";
	} else {
		div.style.display = "block";
	}
	div.style.position = "absolute";
	var ds = 6;
	div.style.left = (wx2 - ds) + "px";
	div.style.top = (wy2 - ds) + "px";
	var ws = ds * 2 + 1;
	div.style.width = ws + "px";
	div.style.height = ws + "px";
	div.style.cursor = "pointer";
	var trace = false;
	if (trace) {
		if (first) {
			div.style.backgroundColor = "#f00";
		} else {
			div.style.backgroundColor = "#00f";
		}
	}
	if (YAHOO.env.ua.ie > 0) {
		div.style.backgroundColor = "#fff";
		div.className = "handle";
	}
	var img = hm.map.canvasWindow.document.createElement("img");
	img.width = ws;
	img.height = ws;
	img.src = hm.map.spacer.src;
	div.appendChild(img);
	anchor.appendChild(div);
	var hcircle = null;
	var circle = null;
	var over = function(e) {
		if (!circle) {
			hcircle = hm.map.drawWallEnd(wall, wx2, wy2);
		}
	};
	var out = function(e) {
		hm.map.canvasWindow.removeShape(hm.map.surface, hcircle);
		hcircle = null;
	};
	// YAHOO.util.Event.addListener(div, "mouseover", over); bug 23762
	hm.map.addEventListener(div, 'mouseover', over, false);
	// YAHOO.util.Event.addListener(div, "mouseout", out); bug 23762
	hm.map.addEventListener(div, 'mouseout', out, false);

	var dd = hm.map.canvasWindow.enableDrag(div);
	// No X, Y constraints, use limitMouseMos instead
	var connectedWall = null;
	var connectedWallIndex = -1;
	dd.startDrag = function(x, y) {
		if (hcircle) {
			circle = hcircle;
			hcircle = null;
		} else {
			circle = hm.map.drawWallEnd(wall, wx2, wy2);
		}
		connectedWall = null;
		connectedWallIndex = -1;
		hm.map.exitSelectedWall();
		if (wall.close >= 0) {
			connectedWall = hm.map.walls[wall.close];
			connectedWallIndex = wall.close;
		} else if (id + 1 < hm.map.walls.length && this.id.indexOf("wo") > -1) {
			connectedWall = hm.map.nextConnectedWall(wall, id);
			if (connectedWall) {
				connectedWallIndex = id + 1;
			}
		}
	}
	dd.onDrag = function(e) {
		var mp = hm.map.getMousePos(e);
		hm.map.limitMousePos(mp);
		circle.transform("t" + (mp.x1 - wx2) + "," + (mp.y1 - wy2));
		hm.map.rotateWall(wall, first, mp);
		if (connectedWall) {
			hm.map.rotateWall(connectedWall, false, mp);
		}
	}
	dd.endDrag = function(e) {
		hm.map.canvasWindow.removeShape(hm.map.surface, circle);
		circle = null;
		var mp = hm.map.getMousePos(e);
		hm.map.limitMousePos(mp);
		wx2 = mp.x1;
		wy2 = mp.y1;
		hm.map.t2wall = {
			x1 : wall.x1,
			y1 : wall.y1,
			x2 : wall.x2,
			y2 : wall.y2
		};
		if (first) {
			wall.x2 = mp.x1;
			wall.y2 = mp.y1;
		} else {
			wall.x1 = mp.x1;
			wall.y1 = mp.y1;
		}
		hm.map.rotateWall(wall, first, mp);
		hm.map.addWallAttrs(wall, id);
		if (connectedWall) {
			hm.map.t3wall = {
				x1 : connectedWall.x1,
				y1 : connectedWall.y1,
				x2 : connectedWall.x2,
				y2 : connectedWall.y2
			};
			connectedWall.x1 = mp.x1;
			connectedWall.y1 = mp.y1;
			hm.map.rotateWall(connectedWall, false, mp);
			hm.map.addWallAttrs(connectedWall, connectedWallIndex);
			hm.map.t4wall = connectedWall;
		}
		hm.map.addWallMenu();
		div.style.left = (mp.x1 - ds) + "px";
		div.style.top = (mp.y1 - ds) + "px";
		hm.map.t1wall = wall;
		top.invalidateHiddenMaps();
		top.saveWalls();
	}
	dd.onMouseDown = function(e) {
		if (circle) {
			endDrag(e);
		}
	}
}

hm.map.limitMousePos = function(mp) {
	if (mp.x1 < 0) {
		mp.x1 = 0;
	}
	if (mp.y1 < 0) {
		mp.y1 = 0;
	}
	if (mp.x1 > hm.map.viewWindow.canvasWidth - hm.map.viewWindow.gridBorderX) {
		mp.x1 = hm.map.viewWindow.canvasWidth - hm.map.viewWindow.gridBorderX;
	}
	if (mp.y1 > hm.map.viewWindow.canvasHeight - hm.map.viewWindow.gridBorderY) {
		mp.y1 = hm.map.viewWindow.canvasHeight - hm.map.viewWindow.gridBorderY;
	}
}

hm.map.rotateWall = function(wall, first, mp) {
	hm.map.redrawWall(wall, first ? wall.x1 : wall.x2, first ? wall.y1
			: wall.y2, mp);
}

hm.map.wallColors = [ "#C3A480", "#838383", "#B07842", "#E1390B", "#A10000",
		"#3D3C3C", "#DF8909", "#A1A10D", "#5353F2", "#5353F2" ];
hm.map.wallDash = [ "", "-", "", "", "", "", "", "", "-", "" ];

hm.map.loadWallSettings = function(wallLineColors, wallLineTypes) {
	if (wallLineColors && YAHOO.lang.isArray(wallLineColors)) {
		hm.map.wallColors = wallLineColors;
	}
	if (wallLineTypes && YAHOO.lang.isArray(wallLineTypes)) {
		hm.map.wallDash = wallLineTypes;
	}
}

hm.map.drawWall = function(line, tp) {
	var color = tp < 0 ? "#029FF5" : hm.map.wallColors[tp];
	var dash = tp < 0 ? "" : hm.map.wallDash[tp];
	return hm.map.canvasWindow.createRafaelLine(hm.map.surface, line, color,
			top.wallsOpacity, tp < 0 ? 3 : 3, dash);
}

hm.map.addPerimAttrs = function(wall, index, pid) {
	wall.shape.node.id = "p" + index + "_" + pid;
	if (hm.map.mapWritePermission) {
		var over = function(e) {
			this.attr({
				'stroke-width' : 6,
				stroke : "#029FF5"
			});
		};
		var out = function(e) {
			this.attr({
				'stroke-width' : 3,
				stroke : "#029FF5"
			});
		};
		wall.shape.mouseover(over);
		wall.shape.mouseout(out);
	}
}

hm.map.addWallAttrs = function(wall, id) {
	wall.shape.node.id = "w" + id;
	// wall.shape.node.style.cursor = "pointer";
	if (hm.map.mapWritePermission) {
		var over = function(e) {
			this.attr({
				'stroke-width' : 6,
				stroke : hm.map.wallColors[wall.tp]
			});
		};
		var out = function(e) {
			this.attr({
				'stroke-width' : 3,
				stroke : hm.map.wallColors[wall.tp]
			});
		};
		wall.shape.mouseover(over);
		wall.shape.mouseout(out);
	}
}

hm.map.drawWallEnd = function(wall, cx, cy) {
	var shape = {
		cx : cx,
		cy : cy,
		r : 2
	};
	return hm.map.canvasWindow.createRafaelCircle(hm.map.surface, shape,
			hm.map.wallColors[wall.tp], 1, 4); // "#702963"
}

hm.map.createGfxSurface = function() {
	hm.map.surface = hm.map.canvasWindow.createRafael("links",
			hm.map.viewWindow.canvasWidth - hm.map.viewWindow.gridBorderX,
			hm.map.viewWindow.canvasHeight - hm.map.viewWindow.gridBorderY);
}

/*
 * Process json map Links
 */
hm.map.processMapLinks = function(o) {
	eval("var data = " + o.responseText);
	if (data) {
		var links = data.links;
	}
	if (links) {
		var start = new Date().getTime();
		document.getElementById("mesh").checked = data.mesh;
		hm.map.mesh = data.mesh;
		hm.map.processLinks(links);
		hm.map.ethernet = hm.map.mesh;
		document.getElementById("ethernet").checked = hm.map.ethernet;
		if (hm.map.ethernet) {
			hm.map.drawEth();
		}
		var delta = new Date().getTime() - start;
		hm.map.createCrossHairs();
		hm.map.liveCount = 0;
		hm.map.startAlarmsTimer();
	} else {
		// alert("No data ...");
	}
	top.enableHeatChannelBoxes();
	top.showClients();
}

/*
 * Create a node icon image and div element, make it dragable
 */
hm.map.addNode = function(wlanNode) {
	wlanNode.links = new Array();
	if (wlanNode.jsonNode.container) {
		wlanNode.iconCX = parseInt(hm.map.getContainerNodeIcon(0,
				wlanNode.jsonNode.i).width / 2);
		wlanNode.iconCY = parseInt(hm.map.getContainerNodeIcon(0,
				wlanNode.jsonNode.i).height / 2);
	} else {
		wlanNode.iconCX = parseInt(hm.map.leafNodeIcons[0].width / 2);
		wlanNode.iconCY = parseInt(hm.map.leafNodeIcons[0].height / 2);
	}
	hm.map.addNodeDiv(wlanNode);
}

/*
 * Create div element for the node on the canvas.
 */
hm.map.addNodeDiv = function(wlanNode) {
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var div = hm.map.canvasWindow.document.createElement("div");
	div.id = wlanNode.jsonNode.nodeId;
	div.style.display = "block";
	div.style.position = "absolute";
	var canvasX = wlanNode.jsonNode.x - wlanNode.iconCX + 1;
	var canvasY = wlanNode.jsonNode.y - wlanNode.iconCY;
	// make sure all the icon appears on the map
	var imgWidth = hm.map.viewWindow.canvasWidth
			- hm.map.viewWindow.gridBorderX;
	var imgHeight = hm.map.viewWindow.canvasHeight
			- hm.map.viewWindow.gridBorderY;

	if (canvasX + wlanNode.iconCX > imgWidth) {
		canvasX = imgWidth - wlanNode.iconCX;
	}
	if (canvasY + wlanNode.iconCY > imgHeight) {
		canvasY = imgHeight - wlanNode.iconCY;
	}
	div.style.left = canvasX + "px";
	div.style.top = canvasY + "px";

	var img = hm.map.canvasWindow.document.createElement("img");
	if (wlanNode.jsonNode.container) {
		img.src = hm.map.getContainerNodeIcon(wlanNode.jsonNode.s,
				wlanNode.jsonNode.i).src;
		div.appendChild(img);
	} else {
		if (wlanNode.jsonNode.sim && hm.map.mapWritePermission) {
			div.style.cursor = "pointer";
		}
		if (wlanNode.jsonNode.apId.charAt(0) == 'M') {
			img.src = hm.map.leafNodeIcons[6].src;
		} else {
			img.src = hm.map.leafNodeIcons[wlanNode.jsonNode.s].src;
		}
		var imgDiv = hm.map.canvasWindow.document.createElement("div");
		imgDiv.id = 'l' + wlanNode.jsonNode.nodeId;
		imgDiv.appendChild(img);
		var labelDiv = hm.map.canvasWindow.document.createElement("div");
		labelDiv.id = 'll' + wlanNode.jsonNode.nodeId;
		labelDiv.className = "leafLabel";
		labelDiv.style.left = "33px";
		labelDiv.style.top = "9px";
		var typeNodes = hm.map.getHiveAPTypeNodes(wlanNode.jsonNode.apType);
		for ( var i = 0; i < typeNodes.length; i++) {
			labelDiv.appendChild(typeNodes[i]);
		}
		labelDiv.appendChild(hm.map.canvasWindow.document
				.createTextNode(wlanNode.jsonNode.apName));
		div.appendChild(imgDiv);
		div.appendChild(labelDiv);
		if (wlanNode.jsonNode.apId.charAt(0) != 'M'
				&& wlanNode.jsonNode.isManaged) {
			// For register mouse over event
			wlanNode.imgDiv = imgDiv;
		}
	}
	anchor.appendChild(div);

	if (wlanNode.jsonNode.container) {
		// YAHOO.util.Event.addListener(div, "dblclick",
		// hm.map.containerDblClicked); bug 23762
		hm.map.addEventListener(div, 'dblclick', hm.map.containerDblClicked, false);
		var tt = hm.map.canvasWindow.createToolTip(wlanNode.jsonNode.nodeId,
				wlanNode.jsonNode.mapName);
	} else {
		if (wlanNode.jsonNode.apId.charAt(0) != 'M'
				&& wlanNode.jsonNode.isManaged) {
			// YAHOO.util.Event.addListener(div, "click",
			// hm.map.leafSglClicked); bug 23762
			hm.map.addEventListener(div, 'click', hm.map.leafSglClicked, false);
		}
	}
	var dd = hm.map.canvasWindow.enableDrag(wlanNode.jsonNode.nodeId);
	// dd.primaryButtonOnly = false;
	var nodeX = YAHOO.util.Dom.getX(dd.getDragEl()) + wlanNode.iconCX
			- hm.map.viewWindow.gridBorderX;
	var nodeY = YAHOO.util.Dom.getY(dd.getDragEl()) + wlanNode.iconCY
			- hm.map.viewWindow.gridBorderY;
	wlanNode.dragWrapper = dd;
	dd.setXConstraint(nodeX - 2, hm.map.viewWindow.canvasWidth - nodeX
			- hm.map.viewWindow.gridBorderX);
	dd.setYConstraint(nodeY - 2, hm.map.viewWindow.canvasHeight - nodeY
			- hm.map.viewWindow.gridBorderY - 2);
	// dd.setHandleElId(imgDiv);
	dd.startDrag = function(x, y) {
		top.movedNodeId = wlanNode.jsonNode.nodeId;
		hm.map.removeNodeLinks(wlanNode);
	}
	dd.onDrag = function(e) {
		wlanNode.hasMoved = true;
		for ( var i = 0; i < wlanNode.links.length; i++) {
			var wlanLink = wlanNode.links[i];
			if (wlanLink.line) {
				hm.map.canvasWindow.removeShape(hm.map.surface, wlanLink.line);
			}
			if (hm.map.mesh) {
				var line = hm.map.getLine(wlanLink);
				hm.map.drawLink(wlanLink, line);
				hm.map.moveLinkLabel(wlanLink.fromDiv, line.x1, line.x2,
						line.y1, line.y2);
				hm.map.moveLinkLabel(wlanLink.toDiv, line.x2, line.x1, line.y2,
						line.y1);
			}
		}
		if (wlanNode.ethLink != undefined) {
			hm.map.moveEthLink(wlanNode);
		}
		if (wlanNode.jsonNode.sim) {
			var div = hm.map.canvasWindow.document.getElementById("h"
					+ wlanNode.jsonNode.nodeId);
			if (div) {
				hm.map.moveSimMap(div, wlanNode);
			}
		}
	}
	dd.endDrag = function(e) {
		hm.map.setNodePos(wlanNode);
		for ( var i = 0; i < wlanNode.links.length; i++) {
			var wlanLink = wlanNode.links[i];
			if (wlanLink.line) {
				hm.map.canvasWindow.removeShape(hm.map.surface, wlanLink.line);
			}
			if (hm.map.mesh) {
				var line = hm.map.getLine(wlanLink);
				hm.map.drawLink(wlanLink, line);
				hm.map.moveLinkLabel(wlanLink.fromDiv, line.x1, line.x2,
						line.y1, line.y2);
				hm.map.moveLinkLabel(wlanLink.toDiv, line.x2, line.x1, line.y2,
						line.y1);
			}
		}
		if (wlanNode.jsonNode.sim) {
			hm.map.simApMoved(wlanNode);
		} else {
			hm.map.apMoved(wlanNode);
		}
	}
}

hm.map.setNodePos = function(wlanNode) {
	var addX = wlanNode.iconCX - hm.map.viewWindow.gridBorderX - 1;
	var addY = wlanNode.iconCY - hm.map.viewWindow.gridBorderY;
	wlanNode.jsonNode.x = YAHOO.util.Dom.getX(wlanNode.dragWrapper.getDragEl())
			+ addX;
	wlanNode.jsonNode.y = YAHOO.util.Dom.getY(wlanNode.dragWrapper.getDragEl())
			+ addY;
}

hm.map.addClientDiv = function(rogue, stl) {
	var div = hm.map.canvasWindow.document.createElement("div");
	div.id = rogue.mac;
	div.style.display = "block";
	div.style.position = "absolute";
	var canvasX = rogue.x - hm.map.rogueApIconSize + 1;
	var canvasY = rogue.y - hm.map.rogueApIconSize;
	div.style.left = canvasX + "px";
	div.style.top = canvasY + "px";

	var img = hm.map.canvasWindow.document.createElement("img");
	img.src = hm.map.rogueApIcon.src;
	var labelDiv = hm.map.canvasWindow.document.createElement("div");
	labelDiv.style.left = "33px";
	labelDiv.style.top = "9px";
	var b = hm.map.canvasWindow.document.createElement("b");
	if (stl) {
		var anchor = hm.map.canvasWindow.document.getElementById("rogues");
		b.className = "rogueColor";
		b.appendChild(hm.map.canvasWindow.document.createTextNode(rogue.tp
				+ " "));
		labelDiv.className = "rogueLabel";
		labelDiv.appendChild(b);
		labelDiv.appendChild(hm.map.canvasWindow.document
				.createTextNode(rogue.mac));
	} else {
		var anchor = hm.map.canvasWindow.document.getElementById("clients");
		b.className = "clientColor";
		b.appendChild(hm.map.canvasWindow.document.createTextNode("C "));
		labelDiv.className = "clientLabel";
		labelDiv.appendChild(b);
		labelDiv.appendChild(hm.map.canvasWindow.document
				.createTextNode(rogue.lbl));
	}
	div.appendChild(img);
	div.appendChild(labelDiv);
	anchor.appendChild(div);
	var dd = hm.map.canvasWindow.enableDrag(div.id);
}

hm.map.removeNodeLinks = function(wlanNode) {
	for ( var i = 0; i < wlanNode.links.length; i++) {
		var wlanLink = wlanNode.links[i];
		if (wlanLink.line) {
			hm.map.canvasWindow.removeShape(hm.map.surface, wlanLink.line);
			wlanLink.line = null;
		}
	}
}

hm.map.moveEthLink = function(wlanNode) {
	if (!hm.map.ethernet) {
		return;
	}
	var addX = wlanNode.iconCX;
	var addY = wlanNode.iconCY;
	// if (YAHOO.env.ua.ie > 0) {
	// addX += hm.map.canvasWindow.document.documentElement.scrollLeft;
	// addY += hm.map.canvasWindow.document.documentElement.scrollTop;
	// }
	if (YAHOO.env.ua.ie > 0) {
		addX += 2;
	}
	var X = YAHOO.util.Dom.getX(wlanNode.dragWrapper.getDragEl()) + addX;
	var Y = YAHOO.util.Dom.getY(wlanNode.dragWrapper.getDragEl()) + addY;
	var squareDX = 3;
	var squareDY = 4;
	wlanNode.ethLink.style.left = (X - hm.map.viewWindow.gridBorderX - 2 - squareDX)
			+ "px";
	wlanNode.ethLink.style.top = Math.min(YAHOO.util.Dom.getY(wlanNode.ethIcon)
			- hm.map.viewWindow.gridBorderY, Y)
			+ "px";
	var lineHeight = Math.abs(YAHOO.util.Dom.getY(wlanNode.ethIcon)
			- hm.map.viewWindow.gridBorderY - Y);
	var line = wlanNode.ethLink.firstChild;
	var lineImg = line.firstChild;
	lineImg.style.height = lineHeight + "px";
	line.style.height = lineHeight + "px";
	var square = wlanNode.ethLink.childNodes[1];
	if (Y < YAHOO.util.Dom.getY(wlanNode.ethIcon)
			- hm.map.viewWindow.gridBorderY) {
		square.style.top = (lineHeight - squareDY + 1) + "px";
	} else {
		square.style.top = (-squareDY + 1) + "px";
	}
}

/*
 * Create a line and div element from json link
 */
hm.map.addLink = function(wlanLink) {
	var fromWlanNode = hm.map.wlanNodesHash[wlanLink.jsonNode.fromId];
	var toWlanNode = hm.map.wlanNodesHash[wlanLink.jsonNode.toId];
	if (!fromWlanNode) {
		return false;
	}
	if (!toWlanNode) {
		return false;
	}
	wlanLink.fromWlanNode = fromWlanNode;
	wlanLink.toWlanNode = toWlanNode;

	fromWlanNode.links[fromWlanNode.links.length] = wlanLink;
	toWlanNode.links[toWlanNode.links.length] = wlanLink;

	return true;
}

hm.map.createLinkLabel = function(lbl, x1, x2, y1, y2, r) {
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var div = hm.map.canvasWindow.document.createElement("div");
	div.style.position = "absolute";
	div.className = "linkLabel";

	var dpx = x2 - x1;
	var dpy = y2 - y1;
	var a = dpx * dpx + dpy * dpy;
	if (r > 40) {
		var d = Math.sqrt(a);
		if (d < 100) {
			r = d * 0.45;
		}
	}
	var mu1 = (Math.sqrt(4 * a * r * r)) / (2 * a);
	// var mu2 = - mu1;
	var x = x1 + mu1 * dpx - 11;
	var y = y1 + mu1 * dpy - 8;

	div.style.left = x + "px";
	div.style.top = y + "px";
	div.appendChild(hm.map.canvasWindow.document.createTextNode(lbl));
	anchor.appendChild(div);
	return div;
}

hm.map.moveLinkLabel = function(lblDiv, x1, x2, y1, y2) {
	if (lblDiv == null) {
		return;
	}

	var r = 35;
	var dpx = x2 - x1;
	var dpy = y2 - y1;
	var a = dpx * dpx + dpy * dpy;
	var mu1 = (Math.sqrt(4 * a * r * r)) / (2 * a);
	// var mu2 = - mu1;
	var x = x1 + mu1 * dpx - 11;
	var y = y1 + mu1 * dpy - 8;

	lblDiv.style.left = x + "px";
	lblDiv.style.top = y + "px";
}

hm.map.drawLink = function(wlanLink, line) {
	if (wlanLink.jsonNode.isCritical) {
		var lineColor = "#FF0000";
	} else {
		var lineColor = "#00AA00";
	}
	wlanLink.line = hm.map.canvasWindow.createRafaelLine(hm.map.surface, line,
			lineColor, 1, 2, "");
}

hm.map.getLine = function(wlanLink) {
	return hm.map.getLinkLine(wlanLink.fromWlanNode, wlanLink.toWlanNode);
}

hm.map.getLinkLine = function(fromWlanNode, toWlanNode) {
	var addX2 = toWlanNode.iconCX - hm.map.viewWindow.gridBorderX - 1;
	var addY2 = toWlanNode.iconCY - hm.map.viewWindow.gridBorderY;
	var X2 = YAHOO.util.Dom.getX(toWlanNode.dragWrapper.getDragEl());
	var Y2 = YAHOO.util.Dom.getY(toWlanNode.dragWrapper.getDragEl());
	return hm.map.getLineTo(fromWlanNode, X2 + addX2, Y2 + addY2);
}

hm.map.getLineTo = function(wlanNode, X2, Y2) {
	var addX1 = wlanNode.iconCX - hm.map.viewWindow.gridBorderX - 1;
	var addY1 = wlanNode.iconCY - hm.map.viewWindow.gridBorderY;
	var X1 = YAHOO.util.Dom.getX(wlanNode.dragWrapper.getDragEl());
	var Y1 = YAHOO.util.Dom.getY(wlanNode.dragWrapper.getDragEl());
	var line = {
		x1 : X1 + addX1,
		y1 : Y1 + addY1,
		x2 : X2,
		y2 : Y2
	};
	return line;
}

/*
 * Create hidden elements on a form to sumit node X and Y values using Ajax
 */
hm.map.createNodeElements = function(form) {
	var movedNodes = 0;
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (node.hasMoved) {
			movedNodes++;
			var nodeX = YAHOO.util.Dom.getX(node.dragWrapper.getDragEl())
					+ node.iconCX - hm.map.viewWindow.gridBorderX - 1;
			var nodeY = YAHOO.util.Dom.getY(node.dragWrapper.getDragEl())
					+ node.iconCY - hm.map.viewWindow.gridBorderY;
			var input = document.createElement("input");
			input.type = "hidden";
			input.name = "selectedIds";
			input.value = node.jsonNode.nodeId.substring(1);
			form.appendChild(input);
			input = document.createElement("input");
			input.type = "hidden";
			input.name = "xs";
			input.value = nodeX;
			form.appendChild(input);
			input = document.createElement("input");
			input.type = "hidden";
			input.name = "ys";
			input.value = nodeY;
			form.appendChild(input);
		}
	}
	return movedNodes;
}

hm.map.clearNodeMoved = function() {
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (node.hasMoved) {
			node.hasMoved = false;
		}
	}
}

hm.map.zoomMapNodes = function(scaleDelta) {
	if (hm.map.viewWindow.gridBorderY > 0 && hm.map.viewWindow.gridSize > 0) {
		hm.map.createGrid();
		if (!hm.map.viewWindow.document.forms[hm.map.viewWindow.formName].grid.checked) {
			var grid = hm.map.canvasWindow.document.getElementById("grid");
			grid.style.display = "none";
		}
	}
	hm.map.createGfxSurface();
	hm.map.ethIconCount = 0;
	hm.map.canvasWindow.addWallListeners(hm.map.wallClick, hm.map.wallDblClick,
			hm.map.wallMove);
	for ( var p = 0; p < hm.map.prms.length; p++) {
		var prm = hm.map.prms[p].prm;
		for ( var i = 0; i < prm.length; i++) {
			var wall = prm[i];
			wall.x1 = Math.round(wall.x1 * scaleDelta);
			wall.y1 = Math.round(wall.y1 * scaleDelta);
		}
	}
	hm.map.drawPrms();
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall == null) {
			continue;
		}
		wall.x1 = Math.round(wall.x1 * scaleDelta);
		wall.y1 = Math.round(wall.y1 * scaleDelta);
		wall.x2 = Math.round(wall.x2 * scaleDelta);
		wall.y2 = Math.round(wall.y2 * scaleDelta);
	}
	hm.map.drawWalls();
	var triggerIds = new Array();
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		node.jsonNode.x = Math.round(node.jsonNode.x * scaleDelta);
		node.jsonNode.y = Math.round(node.jsonNode.y * scaleDelta);
		hm.map.addNodeDiv(node);
		if (!node.jsonNode.container && node.jsonNode.apId.charAt(0) != 'M'
				&& node.jsonNode.isManaged) {
			triggerIds[triggerIds.length] = node.jsonNode.nodeId;
		}
		if (node.jsonNode.ethId != undefined) {
			hm.map.createEthLink(node);
		}
	}
	if (!hm.map.planningMode() && hm.map.viewWindow.getRapLabels() != "hn") {
		hm.map.changeRapNodeLabels();
	}
	hm.map.canvasWindow
			.createContextMenu(triggerIds, hm.map.mapWritePermission);
	if (hm.map.mesh) {
		hm.map.drawLinks();
	}
	var apLabels = hm.map.viewWindow.getApLabels();
	var frequency = hm.map.viewWindow.getFrequency();
	for ( var i = 0; i < hm.map.simNodes.length; i++) {
		var node = hm.map.simNodes[i];
		node.jsonNode.x = Math.round(node.jsonNode.x * scaleDelta);
		node.jsonNode.y = Math.round(node.jsonNode.y * scaleDelta);
		hm.map.addPlannedAp(node, false);
		hm.map.changeNodeLabel(node, apLabels, frequency);
	}
	hm.map.createPlannedMenu();
	hm.map.createCrossHairs();
	hm.map.lockMapNodes();
	if (!document.getElementById("nodesLocked").checked) {
		hm.map.unlockMapNodes();
	}
}

hm.map.toggleLockMapNodes = function(checked) {
	if (checked) {
		hm.map.lockMapNodes();
	} else {
		hm.map.unlockMapNodes();
	}
}

/*
 * Lock map nodes.
 */
hm.map.lockMapNodes = function() {
	var showOnHover = top.document.getElementById('showOnHover');
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		node.dragWrapper.lock();
		hm.map.setNodeCursor(node, "default");
		if (showOnHover != null && showOnHover.checked && node.imgDiv != null) {
			YAHOO.util.Event.addListener(node.imgDiv, "mouseover",
					hm.map.leafSglClicked);
		}
	}
	// hm.map.lockClientNodes();
}

/*
 * Unlock map nodes.
 */
hm.map.unlockMapNodes = function() {
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		node.dragWrapper.unlock();
		var div = hm.map.setNodeCursor(node, "pointer");
		if (node.imgDiv != null) {
			YAHOO.util.Event.removeListener(node.imgDiv, "mouseover",
					hm.map.leafSglClicked);
		}
	}
	// hm.map.unlockClientNodes();
}

/*
 * lock/unlock client nodes.
 */
hm.map.lockClientNodes = function() {
	for ( var i = 0; i < hm.map.clientNodes.length; i++) {
		var dd = hm.map.clientNodes[i];
		dd.lock();
		// not right now
		// dd.getEl().firstChild.style.cursor = "default";
	}
}

hm.map.unlockClientNodes = function() {
	if (hm.map.clientNodes.length > 0) {
		for ( var i = 0; i < hm.map.clientNodes.length; i++) {
			hm.map.clientNodes[i].unlock();
		}
	} else {
		var anchor = hm.map.canvasWindow.document.getElementById("clients");
		for ( var i = 0; i < anchor.childNodes.length; i++) {
			var div = anchor.childNodes[i];
			var dd = hm.map.canvasWindow.enableDrag(div.id);
			hm.map.clientNodes[hm.map.clientNodes.length] = dd;
			// not right now
			// dd.getEl().firstChild.style.cursor = "pointer";
		}
	}
}

/*
 * Set map node labels.
 */
hm.map.processMapNodeLabels = function(o) {
	eval("var data = " + o.responseText);
	if (data && data.length > 0) {
		var labelNode = data[0];
		if (hm.map.pageId != labelNode.pageId) {
			return data;
		}
		for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
			var wlanNode = hm.map.wlanNodes[i];
			if (!wlanNode.jsonNode.container) {
				wlanNode.label = null;
			}
		}
		var rapLabels = hm.map.viewWindow.getRapLabels();
		for ( var i = 0; i < data.length; i++) {
			labelNode = data[i];
			var wlanNode = hm.map.wlanNodesHash[labelNode.nodeId];
			if (wlanNode != null && !wlanNode.jsonNode.container) {
				if (rapLabels == "cp") {
					if (labelNode.apName.length > 0) {
						wlanNode.label = labelNode.apName;
					}
				} else if (rapLabels == "ac") {
					wlanNode.label = labelNode.ac;
				}
				if (wlanNode.label) {
					var labelDiv = hm.map.canvasWindow.document
							.getElementById('ll' + wlanNode.jsonNode.nodeId);
					if (labelDiv != null) {
						if (labelDiv.childNodes.length > 0
								&& labelDiv.firstChild.data == wlanNode.label) {
							// Don't recreate label
						} else {
							while (labelDiv.childNodes.length > 0) {
								labelDiv.removeChild(labelDiv.firstChild);
							}
							labelDiv.className = "leafLabel";
							labelDiv.appendChild(hm.map.canvasWindow.document
									.createTextNode(wlanNode.label));
						}
					}
				}
			}
		}
		for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
			var wlanNode = hm.map.wlanNodes[i];
			if (!wlanNode.jsonNode.container && wlanNode.label == null) {
				hm.map.restoreMapNodeLabel(wlanNode, null);
			}
		}
	}
	if (rapLabels == "ac") {
		hm.map.clientsTimeoutId = setTimeout("hm.map.refreshClientCounts()",
				15 * 1000);
	}
	return data; // Need this for heatChannelMapDone(o) function
}

hm.map.refreshClientCounts = function() {
	hm.map.requestMapObjects('channels', hm.map.processMapNodeLabels,
			hm.map.viewWindow.getFrequency(), null);
}

/*
 * Set map node labels.
 */
hm.map.restoreMapNodeLabels = function(rapLabels) {
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var wlanNode = hm.map.wlanNodes[i];
		if (!wlanNode.jsonNode.container) {
			wlanNode.label = null;
			hm.map.restoreMapNodeLabel(wlanNode, rapLabels);
		}
	}
}

hm.map.restoreMapNodeLabel = function(wlanNode, rapLabels) {
	var labelDiv = hm.map.canvasWindow.document.getElementById('ll'
			+ wlanNode.jsonNode.nodeId);
	if (labelDiv != null) {
		while (labelDiv.childNodes.length > 0) {
			labelDiv.removeChild(labelDiv.firstChild);
		}
		labelDiv.className = "leafLabel";
		var typeNodes = hm.map.getHiveAPTypeNodes(wlanNode.jsonNode.apType);
		if (rapLabels != "no") {
			for ( var i = 0; i < typeNodes.length; i++) {
				labelDiv.appendChild(typeNodes[i]);
			}
		}
		var labelText = wlanNode.jsonNode.apName;
		if (rapLabels == "ni") {
			labelText = wlanNode.jsonNode.apId;
		} else if (rapLabels == "ip") {
			labelText = wlanNode.jsonNode.ipAddress;
		} else if (rapLabels == "no") {
			labelText = null;
		}
		if (labelText) {
			labelDiv.className = "leafLabel";
			labelDiv.appendChild(hm.map.canvasWindow.document
					.createTextNode(labelText));
		} else {
			labelDiv.className = "leafLabelEmpty";
			var img = hm.map.canvasWindow.document.createElement("img");
			img.src = hm.map.spacer.src;
			labelDiv.appendChild(img);
		}
	}
}

hm.map.setNodeCursor = function(node, cursor) {
	if (node.jsonNode.container) {
		var divId = node.jsonNode.nodeId;
	} else {
		var divId = 'l' + node.jsonNode.nodeId;
	}
	var div = hm.map.canvasWindow.document.getElementById(divId);
	div.style.cursor = cursor;
}

hm.map.startAlarmsTimer = function() {
	var interval = hm.map.refreshInterval || 15; // seconds
	var duration = hm.util.sessionTimeout * 60; // minutes * 60
	var total = duration / interval;
	if (hm.map.liveCount++ < total) {
		hm.map.alarmsTimeoutId = setTimeout("hm.map.pollAlarmsCache()",
				interval * 1000); // seconds
	}
}

hm.map.clearAlarmsTimer = function() {
	if (hm.map.alarmsTimeoutId) {
		clearTimeout(hm.map.alarmsTimeoutId);
	}
	if (hm.map.clientsTimeoutId) {
		clearTimeout(hm.map.clientsTimeoutId);
	}
}

hm.map.loadRefreshInterval = function(interval) {
	hm.map.refreshInterval = interval;
}

hm.map.pollAlarmsCache = function() {
	var roguesChecked = hm.map.viewWindow.document.forms[hm.map.viewWindow.formName].rogues.checked;
	var clientsChecked = hm.map.viewWindow.document.forms[hm.map.viewWindow.formName].clients.checked;

	hm.map.requestMapObjects('alarms', hm.map.processMapAlarms, roguesChecked,
			clientsChecked, hm.map.summaryIsShow);
}

/*
 * Process json map nodes
 */
hm.map.processMapAlarms = function(o) {
	eval("var data = " + o.responseText);
	if (data) {
		var start = new Date().getTime();
		var ntp = "alarms";
		if (data.ntp == "links") {
			ntp = "links";
			hm.map.removeLinks();
			hm.map.processLinks(data.links);
		} else if (data.ntp == "nodes") {
			ntp = "nodes";
			hm.map.removeLinks();
			var triggerIds = hm.map.processNodes(data.nodes);
			hm.map.canvasWindow.recreateContextMenu(triggerIds);
		} else if (data.ntp == "summary") {
			ntp = "summary";
			top.setupSummaryInfo(data);
		} else if (data.ntp == "rogues") {
			ntp = "rogues";
			if (!rssiData) { // looking at measurements
				hm.map.removeClientNodes("rogues");
				hm.map.processClientNodes(o, true);
			}
		} else if (data.ntp == "clients") {
			ntp = "clients";
			if (!rssiData) { // looking at measurements
				hm.map.removeClientNodes("clients");
				hm.map.processClientNodes(o, false);
			}
		} else if (data.ntp == "alarms") {
			data = data.alarms;
			for ( var i = 0; i < data.length; i++) {
				var nodeId = data[i].nodeId;
				if (data[i].container) {
					var div = hm.map.canvasWindow.document
							.getElementById(nodeId);
					var img = div.firstChild;
					img.src = hm.map.getContainerNodeIcon(data[i].s, data[i].i).src;
				} else {
					var div = hm.map.canvasWindow.document.getElementById('l'
							+ nodeId);
					var img = div.firstChild;
					img.src = hm.map.leafNodeIcons[data[i].s].src;
				}
			}
		}
		var delta = new Date().getTime() - start;
		if (ntp == "nodes") {
			// Fetch links right away.
			hm.map.pollAlarmsCache();
		} else {
			hm.map.startAlarmsTimer();
		}
	} else {
		// alert("No data ...");
	}
}

/*
 * Process nodes
 */
hm.map.processNodes = function(data) {
	var triggerIds = new Array();
	// Enumeration
	var newNodes = new Array();
	// Hash table, to look up from/to nodes
	var newNodesHash = new Array();
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	var linksLayer = hm.map.canvasWindow.document.getElementById("links");
	for ( var i = 0; i < data.length; i++) {
		if (i == 0) {
			if (hm.map.pageId != data[0].pageId) {
				return triggerIds;
			}
		}
		var wlanNode = hm.map.wlanNodesHash[data[i].nodeId];
		if (wlanNode == null) {
			// Create new node
			wlanNode = {
				jsonNode : data[i]
			};
			hm.map.addNode(wlanNode);
			if (!wlanNode.jsonNode.container) {
				if (wlanNode.jsonNode.ethId != undefined) {
					hm.map.createEthLink(wlanNode);
				}
			}
		} else if (wlanNode.jsonNode.isCritical != data[i].isCritical
				|| wlanNode.jsonNode.ethId != data[i].ethId) {
			// for ethLink status changed.
			var div = hm.map.canvasWindow.document
					.getElementById(wlanNode.jsonNode.nodeId);
			anchor.removeChild(div);
			if (wlanNode.jsonNode.ethId != undefined) {
				var ethLinkdiv = hm.map.canvasWindow.document
						.getElementById('ethlink-' + wlanNode.jsonNode.nodeId);
				linksLayer.removeChild(ethLinkdiv);
			}
			wlanNode = {
				jsonNode : data[i]
			};
			hm.map.addNode(wlanNode);
			if (wlanNode.jsonNode.ethId != undefined) {
				hm.map.createEthLink(wlanNode);
			}
		} else if (wlanNode.jsonNode.x != data[i].x
				|| wlanNode.jsonNode.y != data[i].y) {
			// node has moved.
			if (false) { // TODO, need to move eth links also, and take into
				// account zooming
				alert("wlanNode: (" + wlanNode.jsonNode.x + ", "
						+ wlanNode.jsonNode.y + ") -> (" + data[i].x + ", "
						+ data[i].y + ")");
				wlanNode.jsonNode.x = data[i].x - 1;
				wlanNode.jsonNode.y = data[i].y;
				var div = wlanNode.dragWrapper.getDragEl();
				var canvasX = wlanNode.jsonNode.x - wlanNode.iconCX + 1;
				var canvasY = wlanNode.jsonNode.y - wlanNode.iconCY;
				div.style.left = canvasX + "px";
				div.style.top = canvasY + "px";
			}
		}
		newNodes[newNodes.length] = wlanNode;
		newNodesHash[data[i].nodeId] = wlanNode;
		if (!wlanNode.jsonNode.container
				&& wlanNode.jsonNode.apId.charAt(0) != 'M'
				&& wlanNode.jsonNode.isManaged) {
			triggerIds[triggerIds.length] = wlanNode.jsonNode.nodeId;
		}
	}
	// Remove obsoleted nodes.
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		var wlanNode = newNodesHash[node.jsonNode.nodeId];
		if (wlanNode == null) {
			var div = hm.map.canvasWindow.document
					.getElementById(node.jsonNode.nodeId);
			anchor.removeChild(div);
			if (node.jsonNode.ethId != undefined) {
				var ethLinkdiv = hm.map.canvasWindow.document
						.getElementById('ethlink-' + node.jsonNode.nodeId);
				linksLayer.removeChild(ethLinkdiv);
			}
		}
	}
	// Activate new nodes list/hash.
	hm.map.wlanNodes = newNodes;
	hm.map.wlanNodesHash = newNodesHash;
	hm.map.toggleLockMapNodes(document.getElementById("nodesLocked").checked);
	return triggerIds;
}

/*
 * Process links
 */
hm.map.processLinks = function(data) {
	// Enumeration
	hm.map.wlanLinks = new Array();
	for ( var i = 0; i < data.length; i++) {
		var wlanLink = {
			jsonNode : data[i]
		};
		if (i == 0) {
			if (hm.map.pageId != wlanLink.jsonNode.pageId) {
				return;
			}
		}
		if (hm.map.addLink(wlanLink)) {
			hm.map.wlanLinks[hm.map.wlanLinks.length] = wlanLink;
		}
	}
	if (hm.map.mesh) {
		hm.map.drawLinks();
	}
}

hm.map.drawLinks = function() {
	for ( var i = 0; i < hm.map.wlanLinks.length; i++) {
		var link = hm.map.wlanLinks[i];
		var line = hm.map.getLine(link);
		hm.map.drawLink(link, line);
		if (link.jsonNode.fromLbl != null) {
			link.fromDiv = hm.map.createLinkLabel(link.jsonNode.fromLbl,
					line.x1, line.x2, line.y1, line.y2, 35);
		}
		if (link.jsonNode.toLbl != null) {
			link.toDiv = hm.map.createLinkLabel(link.jsonNode.toLbl, line.x2,
					line.x1, line.y2, line.y1, 35);
		}
	}
}

hm.map.hideLinks = function() {
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	for ( var i = 0; i < hm.map.wlanLinks.length; i++) {
		var link = hm.map.wlanLinks[i];
		if (link.fromDiv != null) {
			anchor.removeChild(link.fromDiv);
		}
		if (link.toDiv != null) {
			anchor.removeChild(link.toDiv);
		}
		hm.map.canvasWindow.removeShape(hm.map.surface, link.line);
		link.line = null;
	}
}

/*
 * Remove links
 */
hm.map.removeLinks = function() {
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		node.links = new Array();
	}
	hm.map.hideLinks();
	hm.map.wlanLinks = new Array();
}

hm.map.removeClientNodes = function(anchorId) {
	top.hideRssi();
	top.hideCoverage();
	hm.map.clientNodes = new Array();
	var anchor = hm.map.canvasWindow.document.getElementById(anchorId);
	while (anchor.firstChild != null) {
		anchor.removeChild(anchor.firstChild);
	}
}

hm.map.processClientNodes = function(o, stl) {
	eval("var data = " + o.responseText);
	if (data) {
		data = stl == true ? data.rogues : data.clients;
	}
	var triggerIds = new Array();
	var sampledIds = new Array();
	if (data) {
		for ( var i = 0; i < data.length; i++) {
			if (i == 0) {
				if (hm.map.pageId != data[0].pageId) {
					return;
				}
			}
			hm.map.addClientDiv(data[i], stl);
			if (stl || !data[i].sd) {
				triggerIds[triggerIds.length] = data[i].mac;
			} else {
				sampledIds[sampledIds.length] = data[i].mac;
			}
		}
		hm.map.canvasWindow.createClientShowContextMenu(triggerIds, sampledIds,
				stl);
	}
}

hm.map.showRssiArea = function(operation, mac) {
	top.cancelMeasuring();
	var div = hm.map.canvasWindow.document.getElementById("carea");
	div.style.backgroundColor = "";
	div.style.zIndex = "76";
	var img = div.firstChild;
	var imgWidth = hm.map.viewWindow.canvasWidth
			- hm.map.viewWindow.gridBorderX;
	var imgHeight = hm.map.viewWindow.canvasHeight
			- hm.map.viewWindow.gridBorderY;
	var url = "maps.action?operation=" + operation + hm.map.addCanvasAttrs()
			+ "&bssid=" + mac;
	img.src = url;
}

hm.map.restoreRssiArea = function() {
	var div = hm.map.canvasWindow.document.getElementById("carea");
	var img = div.firstChild;
	img.src = hm.map.spacer.src;
	div.style.zIndex = "6";
}

hm.map.processClientRssi = function(o) {
	hm.map.processRssi(o, "clients", true);
}

hm.map.processRogueRssi = function(o) {
	hm.map.processRssi(o, "rogues", true);
}

hm.map.processRssi = function(o, anchorId, move) {
	hm.map.hideMesh();
	invalidateHeatChannelMap();
	eval("var data = " + o.responseText);
	if (data && data.length > 0) {
		data[0].pid = anchorId;
		var wlanNode = hm.map.wlanNodesHash[data[0].apId];
		if (move) {
			var anchor = hm.map.canvasWindow.document.getElementById(anchorId);
			var clientDiv = hm.map.canvasWindow.document
					.getElementById(data[0].mac);
			if (clientDiv) {
				anchor.removeChild(clientDiv);
			}
			if (data[0].x < 0) {
				// this one is gone.
				return;
			}
			var stl = anchorId == "rogues";
			if (data[0].circle > 0) {
				if (!wlanNode) {
					return;
				}
				var shape = {
					cx : wlanNode.jsonNode.x,
					cy : wlanNode.jsonNode.y,
					r : data[0].circle
				};
				data[0].shape = hm.map.canvasWindow.createRafaelCircle(
						hm.map.surface, shape, "#00B4F7", 1, 3);
				var shape = {
					cx : data[0].x,
					cy : data[0].y,
					r : 3
				};
				data[0].dot = hm.map.canvasWindow.createRafaelCircle(
						hm.map.surface, shape, "#00B4F7", 1, 3);
				hm.map.addClientDiv(data[0], stl);
				hm.map.showRssiLinks(data, move, wlanNode);
				return;
			}
			var rcra = stl ? "rogueRssiArea" : "clientRssiArea";
			hm.map.showRssiArea(rcra, data[0].mac);
			var excludeId = YAHOO.env.ua.ie ? null : data[0].mac;
			hm.map.canvasWindow.restoreClientContextMenu(excludeId);
			hm.map.addClientDiv(data[0], stl);
			hm.map.canvasWindow.createClientHideContextMenu(data[0], stl);
			var shape = {
				cx : data[0].x,
				cy : data[0].y,
				r : 8
			};
			data[0].shape = hm.map.canvasWindow.createRafaelCircle(
					hm.map.surface, shape, "#00B4F7", 1, 14);
		} else {
			if (data[0].x < 0) {
				// this one is gone.
				return;
			}
			var excludeId = YAHOO.env.ua.ie ? null : data[0].mac;
			hm.map.canvasWindow.restoreClientContextMenu(excludeId);
			hm.map.canvasWindow.createClientHideContextMenu(data[0], stl);
		}
		hm.map.showRssiLinks(data, move, wlanNode);
	}
}

hm.map.hideMesh = function() {
	var mcb = document.getElementById("mesh");
	if (mcb.checked) {
		mcb.checked = false;
		toggleMesh(mcb);
	}
}

hm.map.showRssiLinks = function(data, move, associatedNode) {
	for ( var i = 1; i < data.length; i++) {
		var wlanNode = hm.map.wlanNodesHash[data[i].nodeId];
		var dash = "-";
		if (wlanNode == associatedNode) {
			dash = "";
		}
		var div = hm.map.canvasWindow.document.getElementById(data[0].mac);
		var x = YAHOO.util.Dom.getX(div) + wlanNode.iconCX
				- hm.map.viewWindow.gridBorderX - 1;
		var y = YAHOO.util.Dom.getY(div) + wlanNode.iconCY
				- hm.map.viewWindow.gridBorderY;
		var line = hm.map.getLineTo(wlanNode, x, y);
		data[i].line = hm.map.canvasWindow.createRafaelLine(hm.map.surface,
				line, "#00AA00", 1, 2, dash);
		data[i].div = hm.map.createLinkLabel(data[i].rssi, line.x1, line.x2,
				line.y1, line.y2, 45);
		if (move) {
			if (data[i].r > 0) {
				shape = {
					cx : data[i].cx,
					cy : data[i].cy,
					r : data[i].r
				};
				data[i].shape = hm.map.canvasWindow.createRafaelCircle(
						hm.map.surface, shape, "#00B4F7", 1, 2);
			}
		}
	}
	top.rssiData = data;
}

hm.map.removeClientRssi = function(data) {
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	hm.map.canvasWindow.removeShape(hm.map.surface, data[0].shape);
	if (data[0].dot) {
		hm.map.canvasWindow.removeShape(hm.map.surface, data[0].dot);
	}
	for ( var i = 1; i < data.length; i++) {
		if (data[i].line) {
			hm.map.canvasWindow.removeShape(hm.map.surface, data[i].line);
		}
		if (data[i].div) {
			anchor.removeChild(data[i].div);
		}
		if (data[i].r > 0) {
			hm.map.canvasWindow.removeShape(hm.map.surface, data[i].shape);
		}
	}
}

hm.map.calibrateRssi = function(operation, mac, parentId) {
	var anchor = hm.map.canvasWindow.document.getElementById(parentId);
	var clientDiv = hm.map.canvasWindow.document.getElementById(mac);
	var nodeX = YAHOO.util.Dom.getX(clientDiv) + hm.map.rogueApIconSize
			- hm.map.viewWindow.gridBorderX - 1;
	var nodeY = YAHOO.util.Dom.getY(clientDiv) + hm.map.rogueApIconSize
			- hm.map.viewWindow.gridBorderY;
	hm.map
			.requestMapObjects(operation, processCalibrateRssi, mac, nodeX,
					nodeY);
}

hm.map.uncalibrateRssi = function(operation, mac, parentId) {
	hm.map.requestMapObjects(operation, processUncalibrateRssi, mac);
}

/*
 * Create grid lines
 */
hm.map.createGrid = function() {
	var grid = hm.map.canvasWindow.document.getElementById("grid");
	if (hm.map.viewWindow.lengthUnit == 2) {
		var unit = "feet";
		var gridMin = 38;
	} else {
		var unit = "meters";
		var gridMin = 56;
	}
	grid.appendChild(hm.map.createGridLabel(0,
			hm.map.viewWindow.gridBorderX + 13, unit, false));
	var step = 1;
	if (hm.map.viewWindow.gridSize * hm.map.scale < gridMin) {
		step = 2;
	}
	hm.map.createVerticalLines(grid, step);
	hm.map.createHorizontalLines(grid, step);
}

hm.map.createVerticalLines = function(grid, step) {
	var gridX = 0;
	var actualGridX = 0;
	var edge = hm.map.viewWindow.actualViewportWidth;
	if (hm.map.viewWindow.canvasWidth > edge) {
		edge = hm.map.viewWindow.canvasWidth;
	}
	for ( var gridXcanvas = 0; gridXcanvas + hm.map.viewWindow.gridBorderX < hm.map.viewWindow.canvasWidth;) {
		grid.appendChild(hm.map.createGridLine(0, gridXcanvas
				+ hm.map.viewWindow.gridBorderX, 1,
				hm.map.viewWindow.canvasHeight));
		if (gridXcanvas + hm.map.viewWindow.gridBorderX + 30 < edge) {
			grid.appendChild(hm.map.createGridLabel(0, gridXcanvas
					+ hm.map.viewWindow.gridBorderX + 3, actualGridX, false));
		}
		gridX += hm.map.viewWindow.gridSize * step;
		actualGridX += hm.map.viewWindow.actualGridSize * step;
		gridXcanvas = parseInt(gridX * hm.map.scale);
	}
}

hm.map.createHorizontalLines = function(grid, step) {
	var gridY = 0;
	var actualGridY = 0;
	var edge = hm.map.viewWindow.actualViewportHeight;
	if (hm.map.viewWindow.canvasHeight > edge) {
		edge = hm.map.viewWindow.canvasHeight;
	}
	for ( var gridYcanvas = 0; gridYcanvas + hm.map.viewWindow.gridBorderY < hm.map.viewWindow.canvasHeight;) {
		grid.appendChild(hm.map.createGridLine(gridYcanvas
				+ hm.map.viewWindow.gridBorderY, 0,
				hm.map.viewWindow.canvasWidth, 1));
		if (gridYcanvas + hm.map.viewWindow.gridBorderY + 15 < edge) {
			grid.appendChild(hm.map.createGridLabel(gridYcanvas
					+ hm.map.viewWindow.gridBorderY + 1, 0, actualGridY, true));
		}
		gridY += hm.map.viewWindow.gridSize * step;
		actualGridY += hm.map.viewWindow.actualGridSize * step;
		gridYcanvas = parseInt(gridY * hm.map.scale);
	}
}

hm.map.createGridLine = function(top, left, width, height) {
	var img = hm.map.canvasWindow.document.createElement("img");
	img.src = hm.map.spacer.src;
	img.width = width;
	img.height = height;
	var div = hm.map.canvasWindow.document.createElement("div");
	div.className = "gridLine";
	div.style.display = "block";
	div.style.position = "absolute";
	div.style.backgroundColor = "#000";
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = width + "px";
	div.style.height = height + "px";
	div.appendChild(img);
	return div;
}

hm.map.createGridLabel = function(top, left, text, alignRight) {
	var div = hm.map.canvasWindow.document.createElement("div");
	div.className = "gridLabel";
	div.style.display = "block";
	div.style.position = "absolute";
	div.style.top = top + "px";
	var dx = 0;
	if (alignRight) {
		var lx = text;
		for ( var bx = hm.map.viewWindow.gridBorderX; bx >= 15; bx -= 7) {
			if (lx < 10) {
				dx += 7;
			} else {
				lx /= 10;
			}
		}
	}
	div.style.left = (left + dx) + "px";
	div.appendChild(hm.map.canvasWindow.document.createTextNode(text));
	return div;
}

hm.map.drawEth = function() {
	hm.map.hideEth(); // Just in case
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (!node.jsonNode.container) {
			if (node.jsonNode.ethId != undefined) {
				hm.map.createEthLink(node);
			}
		}
	}
}

hm.map.hideEth = function() {
	var linksLayer = hm.map.canvasWindow.document.getElementById("links");
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (!node.jsonNode.container) {
			if (node.jsonNode.ethId != undefined) {
				var ethLinkdiv = hm.map.canvasWindow.document
						.getElementById('ethlink-' + node.jsonNode.nodeId);
				if (ethLinkdiv != null) {
					linksLayer.removeChild(ethLinkdiv);
				}
				var div = hm.map.canvasWindow.document.getElementById('eth-'
						+ node.jsonNode.ethId);
				if (div != null) {
					linksLayer.removeChild(div);
				}
			}
		}
	}
	hm.map.ethIconCount = 0;
}

hm.map.createEthLink = function(wlanNode) {
	if (hm.map.ethernet) {
		hm.map.drawEthLink(wlanNode);
	}
}

hm.map.drawEthLink = function(wlanNode) {
	hm.map.setEthIcon(wlanNode);
	var squareDX = 3;
	var squareDY = 4;
	var divColor = "#0000FF"; // "#00AA00"
	if (wlanNode.jsonNode.isCritical) {
		divColor = "#FF0000";
	}
	var div = hm.map.canvasWindow.document.createElement("div");
	div.id = 'ethlink-' + wlanNode.jsonNode.nodeId;
	div.style.display = "block";
	div.style.position = "absolute";
	div.style.top = Math.min(wlanNode.jsonNode.y, YAHOO.util.Dom
			.getY(wlanNode.ethIcon)
			- hm.map.viewWindow.gridBorderY)
			+ "px";
	div.style.left = (wlanNode.jsonNode.x - 1 - squareDX) + "px";
	var height = Math.abs(YAHOO.util.Dom.getY(wlanNode.ethIcon)
			- wlanNode.jsonNode.y - hm.map.viewWindow.gridBorderY);
	var line = hm.map.createEthDiv(0, squareDX, 2, height, divColor);
	div.appendChild(line);
	if (wlanNode.jsonNode.y < YAHOO.util.Dom.getY(wlanNode.ethIcon)
			- hm.map.viewWindow.gridBorderY) {
		var square = hm.map.createEthDiv(height - squareDY + 1, 0,
				squareDX * 2 + 2, squareDY * 2 + 2, divColor);
	} else {
		var square = hm.map.createEthDiv(-squareDY + 1, 0, squareDX * 2 + 2,
				squareDY * 2 + 2, divColor);
	}
	div.appendChild(square);
	var linksLayer = hm.map.canvasWindow.document.getElementById("links");
	linksLayer.appendChild(div);
	wlanNode.ethLink = div;
}

hm.map.ethIconCount = 0;
hm.map.setEthIcon = function(wlanNode) {
	var div = hm.map.canvasWindow.document.getElementById('eth-'
			+ wlanNode.jsonNode.ethId);
	if (div == null) {
		div = hm.map.createEthDiv(hm.map.viewWindow.canvasHeight - 30
				- hm.map.ethIconCount * 30 - hm.map.viewWindow.gridBorderY, 10,
				hm.map.viewWindow.canvasWidth - 20
						- hm.map.viewWindow.gridBorderX, 4, "#0000FF");
		div.id = 'eth-' + wlanNode.jsonNode.ethId;
		var labelDiv = hm.map.canvasWindow.document.createElement("div");
		labelDiv.className = "leafLabel";
		labelDiv.style.top = "5px";
		labelDiv.style.left = "0";
		var labelNode = hm.map.canvasWindow.document
				.createTextNode(wlanNode.jsonNode.ethId);
		labelDiv.appendChild(labelNode);
		div.appendChild(labelDiv);
		var linksLayer = hm.map.canvasWindow.document.getElementById("links");
		linksLayer.appendChild(div);
		hm.map.ethIconCount++;
	}
	wlanNode.ethIcon = div;
}

hm.map.createEthDiv = function(top, left, width, height, backgroundColor) {
	var img = hm.map.canvasWindow.document.createElement("img");
	img.src = hm.map.spacer.src;
	img.border = 0;
	img.width = width;
	img.height = height;

	var div = hm.map.canvasWindow.document.createElement("div");
	div.style.display = "block";
	div.style.position = "absolute";
	div.style.backgroundColor = backgroundColor;
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = width + "px";
	div.style.height = height + "px";

	div.appendChild(img);
	return div;
}

hm.map.createCrossHairs = function() {
	if (hm.map.mapWidth == 0) {
		hm.map.hideMeasuringTool();
		hm.map.canvasWindow.document.getElementById("cross1").style.display = "none";
		hm.map.canvasWindow.document.getElementById("cross2").style.display = "none";
		return;
	}
	top.cross1dd = hm.map.canvasWindow.enableDrag("cross1");
	top.cross2dd = hm.map.canvasWindow.enableDrag("cross2");
	top.cross1dd.onDrag = function(e) {
		if (top.useWidth) {
			var Y = YAHOO.util.Dom.getY(top.cross1dd.getDragEl());
			var div = top.cross2dd.getDragEl();
			div.style.top = Y + "px";
		} else {
			var X = YAHOO.util.Dom.getX(top.cross1dd.getDragEl());
			var div = top.cross2dd.getDragEl();
			div.style.left = X + "px";
		}
	}
	top.cross2dd.onDrag = function(e) {
		if (top.useWidth) {
			var Y = YAHOO.util.Dom.getY(top.cross2dd.getDragEl());
			var div = top.cross1dd.getDragEl();
			div.style.top = Y + "px";
		} else {
			var X = YAHOO.util.Dom.getX(top.cross2dd.getDragEl());
			var div = top.cross1dd.getDragEl();
			div.style.left = X + "px";
		}
	}
	hm.map.createMeasuringTool();
}

hm.map.showCrossHairs = function() {
	var div1 = top.cross1dd.getDragEl();
	div1.style.display = "";
	div1.style.zIndex = "110";
	div1.style.left = 205 + "px";
	div1.style.top = 60 + "px";
	var div2 = top.cross2dd.getDragEl();
	div2.style.display = "";
	div2.style.zIndex = "110";
	if (top.useWidth) {
		div2.style.left = 280 + "px";
		div2.style.top = 60 + "px";
		hm.map.setXYConstraints(top.cross1dd, 0, 45);
		hm.map.setXYConstraints(top.cross2dd, 0, 45);
	} else {
		div2.style.left = 205 + "px";
		div2.style.top = 130 + "px";
		hm.map.setXYConstraints(top.cross1dd, 0, 45);
		hm.map.setXYConstraints(top.cross2dd, 75, 115);
	}
}

hm.map.showMapSizePanel = function(btnId, offset) {
	var x = YAHOO.util.Dom.getX(btnId);
	top.mapSizePanel.cfg.setProperty('x', x + 65 + offset);
	top.mapSizePanel.cfg.setProperty('y', 0);
	top.mapSizePanel.cfg.setProperty('visible', true);
	var el = top.document.getElementById("mapSizeWidth");
	if (el)
		el.focus();
}

hm.map.showTreeWidthPanel = function(btnId) {
	var x = YAHOO.util.Dom.getX(btnId);
	var y = YAHOO.util.Dom.getY(btnId);
	top.treeWidthPanel.cfg.setProperty('x', x - 2);
	top.treeWidthPanel.cfg.setProperty('y', y - 2);
	top.treeWidthPanel.cfg.setProperty('visible', true);
	var el = top.document.getElementById("treeWidth");
	if (el)
		el.focus();
}

hm.map.createMeasuringTool = function() {
	top.mtc1dd = hm.map.canvasWindow.enableDrag("mtc1");
	top.mtc2dd = hm.map.canvasWindow.enableDrag("mtc2");
	top.mtc3dd = hm.map.canvasWindow.enableDrag("mtc3");
	top.mtc1dd.onDrag = function(e) {
		var X = YAHOO.util.Dom.getX(top.mtc1dd.getDragEl());
		var div = top.mtc3dd.getDragEl();
		div.style.left = X + "px";
		hm.map.showDxDy(X, YAHOO.util.Dom.getY(top.mtc3dd.getDragEl()));
	}
	top.mtc2dd.onDrag = function(e) {
		var Y = YAHOO.util.Dom.getY(top.mtc2dd.getDragEl());
		var div = top.mtc3dd.getDragEl();
		div.style.top = Y + "px";
		hm.map.showDxDy(YAHOO.util.Dom.getX(top.mtc3dd.getDragEl()), Y);
	}
	top.mtc3dd.onDrag = function(e) {
		var Y = YAHOO.util.Dom.getY(top.mtc3dd.getDragEl());
		var div = top.mtc2dd.getDragEl();
		div.style.top = Y + "px";
		var X = YAHOO.util.Dom.getX(top.mtc3dd.getDragEl());
		div = top.mtc1dd.getDragEl();
		div.style.left = X + "px";
		hm.map.showDxDy(X, Y);
	}
	top.mtSetXY = true;
}

hm.map.showDxDy = function(X, Y) {
	Y = Y - YAHOO.util.Dom.getY(top.mtc1dd.getDragEl());
	X = X - YAHOO.util.Dom.getX(top.mtc2dd.getDragEl());
	Y = Math.round(Math.abs(Y * top.actualScale / hm.map.scale * 100)) / 100;
	X = Math.round(Math.abs(X * top.actualScale / hm.map.scale * 100)) / 100;
	div = hm.map.canvasWindow.document.getElementById("mtc3l");
	div.removeChild(div.firstChild);
	div.appendChild(hm.map.canvasWindow.document.createTextNode(X + ' x ' + Y));
}

hm.map.setXYConstraints = function(dd, offsetX, offsetY) {
	var nodeX = YAHOO.util.Dom.getX(dd.getDragEl()) + 25 - top.gridBorderX
			+ offsetX;
	var nodeY = YAHOO.util.Dom.getY(dd.getDragEl()) + 25 - top.gridBorderY
			- offsetY;
	dd.setXConstraint(nodeX, top.canvasWidth - nodeX - top.gridBorderX - 2);
	dd.setYConstraint(nodeY, top.canvasHeight - nodeY - top.gridBorderY - 1);
}

hm.map.initMeasuringTool = function() {
	hm.map.setXYConstraints(top.mtc1dd, 0, 0);
	hm.map.setXYConstraints(top.mtc2dd, 0, 0);
	hm.map.setXYConstraints(top.mtc3dd, 0, 0);
	var div = top.mtc1dd.getDragEl();
	var dx = 140;
	if (dx > top.canvasWidth - 50) {
		dx = top.canvasWidth - 50;
		if (dx < 0) {
			dx = 0;
		}
	}
	var dy = 100;
	if (dy > top.canvasHeight - 50) {
		dy = top.canvasHeight - 50;
		if (dy < 0) {
			dy = 0;
		}
	}
	div.style.left = (top.gridBorderX + dx) + "px";
	div.style.top = (top.gridBorderY - 25) + "px";
	div = top.mtc2dd.getDragEl();
	div.style.left = (top.gridBorderX - 25) + "px";
	div.style.top = (top.gridBorderY + dy) + "px";
	div = top.mtc3dd.getDragEl();
	div.style.left = (top.gridBorderX + dx) + "px";
	div.style.top = (top.gridBorderY + dy) + "px";
	hm.map.showDxDy(YAHOO.util.Dom.getX(top.mtc3dd.getDragEl()), YAHOO.util.Dom
			.getY(top.mtc3dd.getDragEl()));
	top.mtSetXY = false;
}

hm.map.showMeasuringTool = function() {
	if (top.mtSetXY) {
		hm.map.initMeasuringTool();
	}
	var measuringTool = hm.map.canvasWindow.document
			.getElementById("measuringTool");
	measuringTool.style.zIndex = "110";
	measuringTool.style.display = "";
}

hm.map.hideMeasuringTool = function() {
	var measuringTool = hm.map.canvasWindow.document
			.getElementById("measuringTool");
	measuringTool.style.display = "none";
}

hm.map.snapNodeToGrid = function(nodeId) {
	var wlanNode = hm.map.wlanNodesHash[nodeId];
	if (wlanNode == null) {
		return null;
	}
	hm.map.removeNodeLinks(wlanNode);
	hm.map.moveDivToGrid(wlanNode);
	for ( var i = 0; i < wlanNode.links.length; i++) {
		var wlanLink = wlanNode.links[i];
		var line = hm.map.getLine(wlanLink);
		hm.map.drawLink(wlanLink, line);
		hm.map.moveLinkLabel(wlanLink.fromDiv, line.x1, line.x2, line.y1,
				line.y2);
		hm.map
				.moveLinkLabel(wlanLink.toDiv, line.x2, line.x1, line.y2,
						line.y1);
	}
	if (wlanNode.ethLink != undefined) {
		hm.map.moveEthLink(wlanNode);
	}
	hm.map.apMoved(wlanNode);
	return wlanNode;
}

hm.map.snapSimNodeToGrid = function(nodeId) {
	var wlanNode = hm.map.findSimAp(nodeId);
	if (wlanNode == null) {
		return null;
	}
	hm.map.moveDivToGrid(wlanNode);
	hm.map.setNodePos(wlanNode);
	hm.map.simApMoved(wlanNode);
	return wlanNode;
}

hm.map.moveDivToGrid = function(wlanNode) {
	var X = YAHOO.util.Dom.getX(top.mtc3dd.getDragEl());
	var Y = YAHOO.util.Dom.getY(top.mtc3dd.getDragEl());
	var div = wlanNode.dragWrapper.getDragEl();
	div.style.left = (X - hm.map.viewWindow.gridBorderX - wlanNode.iconCX + 26)
			+ "px";
	div.style.top = (Y - hm.map.viewWindow.gridBorderY - wlanNode.iconCY + 25)
			+ "px";
}

hm.map.setSelectedIndex = function(select, value) {
	for ( var i = 0; i < select.options.length; i++) {
		if (select.options[i].value == value) {
			select.selectedIndex = i;
			return;
		}
	}
}

hm.map.moveSelectedOptions = function(objSourceElement, objTargetElement,
		toSort, notMove1, notMove2, limitCount) {
	var test1 = function(val) {
		return false;
	};
	var test2 = test1;
	if (objSourceElement.length == 0 || objSourceElement.options[0].value == -1) {
		warnDialog.cfg.setProperty('text', "No items found.");
		warnDialog.show();
	} else {
		var j = 0;
		for ( var i = 0; i < objSourceElement.length; i++) {
			if (objSourceElement.options[i].selected) {
				j++;
			}
		}
		if (j == 0) {
			warnDialog.cfg
					.setProperty('text', "Please select at least one item.");
			warnDialog.show();
		} else {
			if (limitCount > 0 && (j + objTargetElement.length) > limitCount) {
				warnDialog.cfg.setProperty('text',
						"The selected items will overrun the limit "
								+ limitCount + ".");
				warnDialog.show();
			} else {
				hm.map.moveOptions(objSourceElement, objTargetElement, toSort,
						function(opt) {
							return (opt.selected && !test1(opt.value)
									&& !test2(opt.value) && opt.value != -1);
						});
			}
		}
	}
}
hm.map.moveOptions = function(objSourceElement, objTargetElement, toSort,
		chooseFunc) {
	var aryTempSourceOptions = new Array();
	var aryTempTargetOptions = new Array();
	var x = 0;

	// looping through source element to find selected options
	for ( var i = 0; i < objSourceElement.length; i++) {
		if (chooseFunc(objSourceElement.options[i])) {
			// need to move this option to target element
			if (objTargetElement.length == 1
					&& objTargetElement.options[0].value < 0) {
				objTargetElement.length = 0;
			}
			var intTargetLen = objTargetElement.length++;
			objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;
			objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;
		} else {
			// storing options that stay to recreate select element
			var objTempValues = new Object();
			objTempValues.text = objSourceElement.options[i].text;
			objTempValues.value = objSourceElement.options[i].value;
			aryTempSourceOptions[x] = objTempValues;
			x++;
		}
	}

	// sorting and refilling target list
	for ( var i = 0; i < objTargetElement.length; i++) {
		var objTempValues = new Object();
		objTempValues.text = objTargetElement.options[i].text;
		objTempValues.value = objTargetElement.options[i].value;
		aryTempTargetOptions[i] = objTempValues;
	}

	if (toSort) {
		aryTempTargetOptions.sort(hm.map.sortByText);
	}

	for ( var i = 0; i < objTargetElement.length; i++) {
		objTargetElement.options[i].text = aryTempTargetOptions[i].text;
		objTargetElement.options[i].value = aryTempTargetOptions[i].value;
		objTargetElement.options[i].selected = false;
	}

	// resetting length of source
	objSourceElement.length = aryTempSourceOptions.length;
	// looping through temp array to recreate source select element
	for ( var i = 0; i < aryTempSourceOptions.length; i++) {
		objSourceElement.options[i].text = aryTempSourceOptions[i].text;
		objSourceElement.options[i].value = aryTempSourceOptions[i].value;
		objSourceElement.options[i].selected = false;
	}
}
hm.map.sortByText = function(a, b) {
	if (a.text < b.text) {
		return -1
	}
	if (a.text > b.text) {
		return 1
	}
	return 0;
}

// j_layoutMap2

function initConfirmDialog() {
	confirmDialog = new YAHOO.widget.SimpleDialog(
			"confirmDialog",
			{
				width : "350px",
				fixedcenter : true,
				visible : false,
				draggable : true,
				modal : true,
				close : true,
				text : "<html><body>This operation will remove the selected item(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
				icon : YAHOO.widget.SimpleDialog.ICON_WARN,
				constraintoviewport : true,
				buttons : [ {
					text : "Yes",
					handler : handleYes,
					isDefault : true
				}, {
					text : "&nbsp;No&nbsp;",
					handler : handleNo
				} ]
			});
	confirmDialog.setHeader("Confirm");
	confirmDialog.render(document.body);
	confirmDialog.cancelEvent.subscribe(confirmCancelHandler);
}
var streetDialog = null;
function initStreetDialog(head) {
	var handleStreetYes = function() {
		this.hide();
	};
	var handleStreetNo = function() {
		document.getElementById("useStreetMaps").checked = false;
		this.hide();
	};
	streetDialog = new YAHOO.widget.SimpleDialog("streetDialog", {
		width : "550px",
		fixedcenter : true,
		visible : false,
		draggable : true,
		modal : true,
		close : false,
		icon : YAHOO.widget.SimpleDialog.ICON_WARN,
		constraintoviewport : true,
		buttons : [ {
			text : "Yes",
			handler : handleStreetYes
		}, {
			text : "&nbsp;No&nbsp;",
			handler : handleStreetNo,
			isDefault : true
		} ]
	});
	streetDialog.setHeader(head);
	streetDialog.render(document.body);
	streetDialog.cancelEvent.subscribe(confirmCancelHandler);
}
function initWarnDialog() {
	warnDialog = new YAHOO.widget.SimpleDialog("warnDialog", {
		width : "350px",
		fixedcenter : true,
		visible : false,
		draggable : true,
		modal : true,
		close : true,
		icon : YAHOO.widget.SimpleDialog.ICON_ALARM,
		constraintoviewport : true,
		buttons : [ {
			text : "OK",
			handler : handleNo,
			isDefault : true
		} ]
	});
	warnDialog.setHeader("Warning");
	warnDialog.render(document.body);
}
function showWarnDialog(message, headMessage) {
	if (warnDialog == null) {
		initWarnDialog();
	}
	if (headMessage != null) {
		warnDialog.setHeader(headMessage);
	} else {
		warnDialog.setHeader("Warning");
	}
	warnDialog.cfg.setProperty("text", message);
	warnDialog.show();
}
function showInfoDialog(info) {
	if (infoDialog == null) {
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
			width : "350px",
			fixedcenter : true,
			modal : true,
			visible : false,
			draggable : true,
			constraintoviewport : true,
			icon : YAHOO.widget.SimpleDialog.ICON_INFO,
			buttons : [ {
				text : "&nbsp;OK&nbsp;",
				handler : handleNo,
				isDefault : true
			} ]
		});
		infoDialog.setHeader("Information");
		infoDialog.render(document.body);
	}
	infoDialog.cfg.setProperty("text", info);
	infoDialog.show();
}
function confirmCancelHandler() {
}
var handleYes = function() {
	this.hide();
	doContinueOper();
};
var handleNo = function() {
	this.hide();
};

function treeResized() {
	hm.map.loadMap(selectedNode.data.id);
}

function addTreeOpImg(id, w) {
	var td = document.getElementById(id);
	var img = document.createElement("img");
	img.src = hm.map.spacer.src;
	img.width = w;
	img.height = "1";
	td.appendChild(img);
}

function addSettingsImg(id) {
	var td = document.getElementById(id);
	var img = document.createElement("img");
	img.src = hm.map.spacer.src;
	img.width = "24";
	img.height = "1";
	td.appendChild(img);
}

var mapTree = null;
var labelClicked = null;
function createTree() {
	mapTree = new YAHOO.widget.TreeView("treeDiv");
	populateTree(mapHierarchy, mapTree.getRoot(), 0);
	mapTree.subscribe("labelClick", function(node) {
		if (node.data.uiz) {
			return;
		}
		labelClicked = node.label;
		highlightNode(node);
		hm.util.toggleHideElement("actualDim3", true);
		hm.util.toggleHideElement("actualDim4", true);
		document.forms[formName].heat.disabled = true;
		document.forms[formName].channel.disabled = true;
		document.forms[formName].rates.disabled = true;
		document.forms[formName].interference.disabled = true;
		document.forms[formName].nodesLocked.disabled = true;
		hm.util.toggleHideElement("distanceLabel", true);
		document.forms[formName].measuringTool.disabled = true;
		document.forms[formName].sizingTool.disabled = true;
		disableReadOnlyGroups(true);
		detailsPanel.cfg.setProperty('visible', false);
		hm.map.loadMap(node.data.id);
	});
	mapTree.subscribe("clickEvent", function(node) {
		initNoteSection();
	});
	mapTree.subscribe("expand", function(node) {
		if (labelClicked == node.label) {
			labelClicked = null;
			return false;
		}
		expandTreeNode(node.data.id, true);
	});
	mapTree.subscribe("collapse", function(node) {
		if (labelClicked == node.label) {
			labelClicked = null;
			return false;
		}
		expandTreeNode(node.data.id, false);
	});
	mapTree.render();
}

function populateTree(items, parentNode, depth) {
	for ( var i = 0; i < items.length; i++) {
		if (items[i] != undefined) {
			var node = new YAHOO.widget.TextNode(items[i], parentNode);
			node.title = null; // Interferes with menus
			populateTree(items[i].items, node, depth + 1);
		}
	}
}
function scanTree(items, parentNode, depth) {
	for ( var i = 0, j = 0; i < items.length; i++) {
		if (items[i] != undefined) {
			var child = parentNode.children[j];
			var len = parentNode.children.length;
			if (j == len || items[i].id != child.data.id) {
				var node = new YAHOO.widget.TextNode(items[i], parentNode);
				if (j == len) {
					// Insert at end
				} else {
					node.insertBefore(child);
				}
				node.title = null; // Interferes with menus
				populateTree(items[i].items, node, depth + 1);
			} else {
				scanTree(items[i].items, parentNode.children[j], depth + 1);
			}
			j++;
		}
	}
}
function highlightNode(node) {
	// expands parent nodes
	var parentNode = node.parent;
	while (null != parentNode.parent) {
		parentNode.expand();
		parentNode = parentNode.parent;
	}

	if (selectedNode != null) {
		YAHOO.util.Dom.removeClass(selectedNode.labelElId, 'ygtvlabelSel');
	}
	selectedNode = node;
	YAHOO.util.Dom.addClass(selectedNode.labelElId, 'ygtvlabelSel');
}
function createPlanningPanel(width, height) {
	var div = document.getElementById("planningPanel");
	width = width || 300;
	height = height || 200;
	var iframe = document.getElementById("planning_tool");
	iframe.width = width;
	iframe.height = height;
	planningPanel = new YAHOO.widget.Panel(div, {
		modal : true,
		fixedcenter : "contained",
		visible : false,
		constraintoviewport : true
	});
	planningPanel.render(document.body);
	div.style.display = "";
	planningPanel.beforeHideEvent.subscribe(clearPlanningData);
}
function clearPlanningData() {
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("planning_tool").style.display = "none";
	}
	confirmed = false;
}

// j_view2

hm.map.validateMapNameAndSize = function(operation, fieldName, invalidChar,
		mapType) {
	var mapNameElement = document.getElementById("mapName");
	// Device should support names with blanks starting from R3
	var message = hm.util.validateNameWithBlanks(mapNameElement.value,
			fieldName);
	if (message != null) {
		hm.util.reportFieldError(mapNameElement, message);
		mapNameElement.focus();
		return false;
	}
	// Map name does not support '|'
	var mapName = document.getElementById("mapName");
	if (mapName.value.indexOf("|") > -1) {
		hm.util.reportFieldError(mapName, invalidChar);
		mapName.focus();
		return false;
	}
	var mapImage = document.getElementById("mapImage");
	if (mapImage.value == "" && iconDefault != 4) { // Not if building
		var sizeX = document.getElementById("sizeX");
		var sizeY = document.getElementById("sizeY");
		if (sizeX.value.length > 0 || mapType == 3) {
			var message = hm.util.validateNumberRange(sizeX.value, 'Width', 1,
					100000000);
			if (message != null) {
				hm.util.reportFieldError(sizeX, message);
				sizeX.focus();
				return false;
			}
			var message = hm.util.validateNumberRange(sizeY.value, 'Height', 1,
					100000000);
			if (message != null) {
				hm.util.reportFieldError(sizeX, message);
				sizeY.focus();
				return false;
			}
			if (parseInt(sizeX.value / sizeY.value) > 10
					|| parseInt(sizeY.value / sizeX.value) > 10) {
				hm.util.reportFieldError(sizeX,
						"The aspect ratio should not be more than 10x.");
				sizeX.focus();
				return false;
			}
		}
	}
	return true;
}

hm.map.checkMapName = function(baseUrl, operation, mapType) {
	var mapName = document.getElementById("mapName").value;
	var url = baseUrl + "?operation=checkMapName&mapName="
			+ encodeURIComponent(mapName) + "&domainMapId=" + mapContainerId
			+ "&mapType=" + mapType + "&ignore=" + new Date().getTime();
	if (operation == "updateMap") {
		url += "&id=" + mapContainerId;
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.checkMapNameResult,
		failure : connectedFailed,
		argument : {
			opt : operation,
			mt : mapType
		}
	}, null);
}

hm.map.checkMapNameResult = function(o) {
	eval("var result = " + o.responseText);
	if (result.v) {
		var mapNameElement = document.getElementById("mapName");
		hm.util.reportFieldError(mapNameElement, result.v);
		return;
	} else {
		detailsPanel.cfg.setProperty('visible', false);
		if (o.argument.mt == 99) {
			document.getElementById("mapIcon").selectedIndex = 6;
		}
		document.forms[setting_formName].operation.value = o.argument.opt;
		document.forms[setting_formName].submit();
	}
}

hm.map.checkDeletionRestrict = function(baseUrl) {
	var url = baseUrl + "?operation=checkDeletionRestrict&id=" + mapContainerId
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : hm.map.checkDeletionRestrictResult,
		failure : connectedFailed
	}, null);
}

hm.map.checkDeletionRestrictResult = function(o) {
	eval("var result = " + o.responseText);
	if (!result.removable) {
		showWarnDialog(result.msg, "Error");
		return;
	} else {
		showProcessing();
		document.forms[setting_formName].operation.value = "removeMap";
		document.forms[setting_formName].id.value = mapContainerId;
		document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
		document.forms[setting_formName].submit();
	}
}

function submitAction(formName, operation) {
	if (operation == "saveNodes") {
		saveNodes();
		return;
	} else if (operation == "create") {
		createNode();
		return;
	} else if (operation == "cancel_map") {
		detailsPanel.cfg.setProperty('visible', false);
		return;
	} else if (operation == "cancel_global") {
		globalPanel.cfg.setProperty('visible', false);
		return;
	} else if (operation == "cancel_review") {
		imageReviewPanel.cfg.setProperty('visible', false);
		return;
	} else if (operation == "createMap" || operation == "updateMap") {
		var mapType = document.forms[setting_formName].mapType.value;
		if (!hm.map.validateMapNameAndSize(operation, "Name",
				internalUsedErrMsg, mapType)) {
			return;
		}
		hm.map.checkMapName(mapSettingsBaseUrl, operation, mapType);
		return;
	} else if (operation == 'removeCwpDirectory') {
		thisOperation = operation;
		hm.util.confirmRemoveCwpDirectory();
		return;
	} else if (operation == 'createDownloadData') {
		createTechData();
		return;
	}
	// avoid upload file when submit form
	document.getElementById("imagedata").disabled = true;
	document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
	detailsPanel.cfg.setProperty('visible', false);
}
function validateUploadImage() {
	var uploadImageElement = document.getElementById("imagedata");
	if (uploadImageElement.value.toLowerCase().search(".jpg") == -1
			&& uploadImageElement.value.toLowerCase().search(".png") == -1) {
		hm.util.reportFieldError(uploadImageElement, backgroundFormatErrMsg);
		uploadImageElement.focus();
		return false;
	}
	return true;
}
function openConfirmBootDialog(text) {
	thisOperation = text;
	var leafNodeId = document.forms[formName].leafNodeId.value;
	var isSwitchNode = false;
	var wlanNode = hm.map.wlanNodesHash['n' + leafNodeId];
	if (wlanNode) {
		isSwitchNode = wlanNode.jsonNode.dt == Device_TYPE_SWITCH;
	}
	if (text == hiveApRebootOp) {
		hm.util.confirmRebooting(isSwitchNode);
	} else if (text == hiveApResetConfigOp) {
		hm.util.confirmResetConfig(isSwitchNode);
	} else if (text == hiveApInvokeBackupOp) {
		hm.util.confirmBootImage(isSwitchNode);
	}
}
function openConfirmResetPSEDialog(text){
	thisOperation = text;
	hm.util.confirmRestPSE();
}
function removeMapContainer(mapName) {
	thisOperation = "removeMap";
	hm.util.confirmRemoveMapContainer(mapName);
}
function removeMapImage(imageName) {
	thisOperation = "removeImage";
	hm.util.confirmRemoveMapImage(imageName);
}

function doContinueOper() {
	if (bigFiles.length > 0) {
		swfu.startUpload();
		bigFiles = [];
	} else if (thisOperation == 'removeCwpDirectory') {
		submitCwpRemovePanel();
	} else if (thisOperation == 'removeMap') {
		hm.map.checkDeletionRestrict(mapSettingsActionUrl);
	} else if (thisOperation == 'removeImage') {
		calDeleteImage();
	} else if (thisOperation == hiveApRebootOp) {
		requestSingleItemCli(hiveApRebootOp);
	} else if (thisOperation == hiveApResetConfigOp) {
		requestSingleItemCli(hiveApResetConfigOp);
	} else if (thisOperation == hiveApInvokeBackupOp) {
		requestImageBoot();
	} else if (thisOperation == 'upgradeToHM') { // called from
		// techSupport.jsp
		upgradeToHM(); // defined in techSupport.jsp
	} else if (thisOperation == 'removeAllWalls') {
		confirmedRemoveAllWalls();
	} else if (thisOperation == 'removeAllAPs') {
		confirmRemoveAllAps();
	} else if (thisOperation == hiveApResetPSEOp) {
		requestMultipleItemCli(hiveApResetPSEOp);
	} else if (thisOperation == hiveApClearCredentialsOp){
		doAjaxRequest("clearRadsecCerts", thisOperation, cliInfoResult, true);
	}
}

function initMapControls() {
	invalidateHiddenMaps();
	uncheckAllCBs(true);
	if (YAHOO.env.ua.ie > 0) {
		document.forms[formName].nodesLocked.className = "pckcbie16";
		document.forms[formName].heat.className = "pckcbie";
		document.forms[formName].channel.className = "pckcbie";
		document.forms[formName].interference.className = "pckcbie";
		document.forms[formName].rates.className = "pckcbie";
		document.forms[formName].clients.className = "pckcbie";
		document.forms[formName].rogues.className = "pckcbie";
		document.forms[formName].mesh.className = "pckcbie";
		document.forms[formName].ethernet.className = "pckcbie";
	}
	showActualDims();
	var bm = hm.map.bldMode();
	var nc = !bm || !selectedNode.hasChildren(false);
	if (hm.map.wallMode) {
		cancelWallOps();
	}
	document.forms[formName].nodesLocked.checked = true;
	hm.util.hide('processing');
	if (hm.map.mapWidth == 0) {
		if (!top.gme && nc) {
			var td = document.getElementById("note");
			hm.util.replaceChildren(td, document
					.createTextNode(bm ? buildingNodeText
							: selectedNode.data.tp == 3 ? floorNoMapText
									: folderNoMapText));
			hm.util.show('processing');
		}
	}
}
function showActualDims() {
	var ah = actualHeight > 0;
	var bm = hm.map.bldMode();
	var gn = hm.map.mapWidth == 0;
	var nc = !bm || !selectedNode.hasChildren(false);
	hm.util.toggleHideElement("actualDims", !ah);
	hm.util.toggleHideElement("actualDim2", !ah && nc);
	var sm = top.gme && nc;
	hm.util.toggleHideElement("actualDim2b1", !ah);
	hm.util.toggleHideElement("actualDim2b2", !ah && (!gn || !sm));
	hm.util.toggleHideElement("actualDim2a", bm || gn);
	hm.util.toggleHideElement("actualDim2c", bm || gn);
}
function setTriggeredLeafNode(leafNodeId) {
	document.forms[formName].leafNodeId.value = leafNodeId;
}

var canvasWidth;
var canvasHeight;
var canvasBackground;
var canvasBackgroundPath;
var actualViewportWidth;
var actualViewportHeight;
var scaleDelta = -1;
var gridBorderX = 0;
var gridBorderY = 0;
var gridSize;
var lengthUnit;
var actualGridSize;
var actualHeight;
var leafMapContainer;

function onLoadPage() {
	// Load icon images
	hm.map.loadIcons(imagesBaseUrl);
	addTreeOpImg("yexpall", "15");
	addTreeOpImg("ycolall", "15");
	addTreeOpImg("yexpcol", "4");
	addSettingsImg("wallcfg");
	document.getElementsByName("sortFolders")[0].checked = sortFolders;

	// load summary overlay pop up flag
	hm.map.loadPopUpFlag(initPopUpFlag);
	hm.map.loadRefreshInterval(initRefreshInterval);

	hm.map.loadWallSettings(customWallLineColors, customWallLineTypes);

	// load swf upload tool
	initSwfUpload();
	// create Overlay Manager
	createOverlayManager();
	// Overlay for create/edit maps
	createMapDetailsPanel();
	// Overlay for sizing maps
	createMapSizePanel();
	createTreeWidthPanel(initTreeWidth);
	// Overlay for editing simulated AP
	createSimApDetailsPanel();
	// Overlay for map ops menu
	createMapOpsMenu();
	// Overlay for images review
	createImageReviewPanel();
	// Overlay for global settings
	createMapGlobalPanel();
	// Overlay for client information.
	createClientInfoPanel();
	// Overlay for neighbor information.
	createNeighborInfoPanel();
	// Overlay for waiting dialog
	createWaitingPanel();
	// Overlay for network summary dialog
	createNetworkSummaryPanel();
	// Overlay for CLI information.
	createCliInfoPanel();
	// Overlay for AP information.
	createAPInfoPanel();
	// Overlay for AP LLDP/CDP.
	createLldpCdpPanel();
	// Overlay for remove Captive Web Page directory
	createCwpRemovePanel();
	// Overlay for image to boot
	createImageBootPanel();
	createMoveAPsPanel();

	var selectedMapId = initSelectedMapId;
	var selectedMap = null;
	if (selectedMapId > 0) {
		selectedMap = mapTree.getNodeByProperty("id", selectedMapId);
	}
	if (selectedMap == null) {
		selectedMap = mapTree.getRoot().children[0];
	}
	if (selectedMap != null) {
		highlightNode(selectedMap);
		hm.map.mapId = selectedMap.data.id;
		hm.map.requestMapDetails(processRootMapDetails);
		if (hm.map.popUpFlag) {
			displaySummaryPanel(hm.map.mapId, selectedMap.label);
		}
	}
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion,
			requiredMinorVersion, requiredRevision);
	if (!hasRequestedVersion) {
		showTraditionalContent();
	}
}
function createTreeMenus() {
	var readNodes = new Array();
	var writeNodes = new Array();
	var bldWriteNodes = new Array();
	var globalWriteNodes = new Array();
	findTriggerNodes(mapTree.getRoot().children, readNodes, writeNodes,
			globalWriteNodes, bldWriteNodes);
	createContextMenu(readNodes, writeNodes, globalWriteNodes, bldWriteNodes);
}

function expandTreeNode(mapId, isExpanded) {
	var url = "maps.action?operation=expandMapNode" + "&id=" + mapId
			+ "&mapExpanded=" + isExpanded + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {}, null);
}
function refreshMap() {
	submitAction("maps", "view");
}
function reloadTopo(o) {
	document.location.reload();
}
function toggleSortFolders(cb) {
	cb.disabled = true;
	var url = "mapBld.action?operation=updateSortFolders&sortFolders="
			+ cb.checked + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : reloadTopo
	});
}

function showClients() {
	if (clientMac.length == 0) {
		return;
	}
	if (clientMac.charAt(0) == '|') {
		document.forms[formName].clients.checked = true;
		showClient();
		return;
	}
	if (bssidType == BSSID_TYPE_CLIENT) {
		document.forms[formName].clients.checked = true;
		showClient();
	} else if (bssidType == BSSID_TYPE_ROGUE) {
		document.forms[formName].rogues.checked = true;
		showRogue();
	}
}
function showClient() {
	if (clientMac.length == 0) {
		return;
	}
	hm.map.canvasWindow.createClientShowContextMenu(new Array(), new Array(),
			false);
	showRssi(clientMac, "clients");
}
function showRogue() {
	if (clientMac.length == 0) {
		return;
	}
	hm.map.canvasWindow.createClientShowContextMenu(new Array(), new Array(),
			true);
	showRssi(clientMac, "rogues");
}
function findTriggerNodes(nodeList, readNodes, writeNodes, globalWriteNodes,
		bldWriteNodes) {
	for ( var i = 0; i < nodeList.length; i++) {
		var node = nodeList[i];
		var writePermission = node.data.wp;
		var level = node.data.lvl;
		var nodeType = node.data.tp;
		var uninitialized = node.data.uiz;
		if (uninitialized) {
			continue;
		}
		if (!writePermission) {
			readNodes[readNodes.length] = node.getElId();
		} else if (level == 1) {
			globalWriteNodes[globalWriteNodes.length] = node.getElId();
		} else {
			if (nodeType == 1) {
				writeNodes[writeNodes.length] = node.getElId();
			} else if (nodeType == 2) {
				bldWriteNodes[bldWriteNodes.length] = node.getElId();
			}
		}
		findTriggerNodes(node.children, readNodes, writeNodes,
				globalWriteNodes, bldWriteNodes);
	}
}

function onFldMenuShow() {
	var targetMap = mapTree.getNodeByElement(mapNode);
	if (!sortFolders) {
		fldMenu.getItem(3).cfg.setProperty("disabled",
				targetMap.previousSibling == null);
		fldMenu.getItem(4).cfg.setProperty("disabled",
				targetMap.nextSibling == null);
	}
}

function onBldMenuShow() {
	var targetMap = mapTree.getNodeByElement(mapNode);
	var addFloor = addFloorOp;
	var mi = 2;
	if (targetMap.data.tp == 3 || !sortFolders) {
		mi = 4;
		if (bldMoveUpItem != null) {
			bldMenu.insertItem(bldMoveDnItem, 2);
			bldMoveDnItem = null;
			bldMenu.insertItem(bldMoveUpItem, 2);
			bldMoveUpItem = null;
		}
	} else if (bldMoveUpItem == null) {
		bldMoveUpItem = bldMenu.removeItem(2);
		bldMoveDnItem = bldMenu.removeItem(2);
	}
	if (bldMoveUpItem == null) {
		bldMenu.getItem(2).cfg.setProperty("disabled",
				targetMap.previousSibling == null);
		bldMenu.getItem(3).cfg.setProperty("disabled",
				targetMap.nextSibling == null);
	}
	if (targetMap.data.tp == 3) {
		addFloor += ' Below';
		if (bldMoveItem == null) {
			bldMoveItem = bldMenu.removeItem(mi);
		}
	} else {
		if (bldMoveItem != null) {
			bldMenu.insertItem(bldMoveItem, mi);
			bldMoveItem = null;
		}
	}
	bldMenu.getItem(0).cfg.setProperty("text", addFloor);
}
var mapNode;
var mapContainerId;
function onMenuTrigger(p_oEvent) {
	var targetNode = this.contextEventTarget;
	/*
	 * Get the TextNode instance that that triggered the display of the
	 * ContextMenu instance.
	 */
	var oTextNode = YAHOO.util.Dom.hasClass(targetNode, "ygtvlabel") ? targetNode
			: YAHOO.util.Dom.getAncestorByClassName(targetNode, "ygtvlabel");
	if (oTextNode) {
		mapNode = oTextNode;
		mapContainerId = mapTree.getNodeByElement(mapNode).data.id;
	} else {
		// Cancel the display of the ContextMenu instance.
		this.cancel();
	}
}

function toggleRealTime(radio) {
	var lw = document.getElementById('locationWindow');
	lw.disabled = radio.value == "true";
}
function toggleWidthUnit(cb) {
	var select = document.getElementById('apElevationUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapBlankUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleBlankUnit(cb) {
	var select = document.getElementById('apElevationUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapWidthUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleApElevationUnit(cb) {
	var select = document.getElementById('mapWidthUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapBlankUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleLengthUnit(cb) {
	var width = document.getElementById('mapWidth');
	var elevation = document.getElementById('apElevation');
	var sizeX = document.getElementById('sizeX');
	var sizeY = document.getElementById('sizeY');
	toggleLength(width, cb);
	toggleLength(elevation, cb);
	toggleLength(sizeX, cb);
	toggleLength(sizeY, cb);
}
function toggleLength(input, cb) {
	if (input.value.length != 0) {
		if (cb.value == 2) {
			input.value /= 0.3048;
		} else {
			input.value *= 0.3048;
		}
		input.value = Math.round(input.value * 100000) / 100000;
	}
}

var overlayManager = null
function createOverlayManager() {
	overlayManager = new YAHOO.widget.OverlayManager();
}
var detailsPanel = null;
function createMapDetailsPanel() {
	var div = document.getElementById('detailsPanel');
	detailsPanel = new YAHOO.widget.Panel(div, {
		width : top.gme ? (YAHOO.env.ua.webkit > 0 ? "645px" : "550px")
				: "450px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	// font on IE is not crisp when using FADE
	// ,effect:{
	// effect:YAHOO.widget.ContainerEffect.FADE,
	// duration:0.25
	// }
	});
	detailsPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(detailsPanel);
	detailsPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var imageReviewPanel = null;
function createImageReviewPanel() {
	var div = document.getElementById('imageReviewPanel');
	imageReviewPanel = new YAHOO.widget.Panel(div, {
		width : "420px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		modal : false,
		constraintoviewport : true,
		zIndex : 5
	});
	imageReviewPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(imageReviewPanel);
	imageReviewPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var globalPanel = null;
function createMapGlobalPanel() {
	var div = document.getElementById("globalPanel");
	globalPanel = new YAHOO.widget.Panel(div, {
		width : "487px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	// font on IE is not crisp when using FADE
	// ,effect:{
	// effect:YAHOO.widget.ContainerEffect.FADE,
	// duration:0.25
	// }
	});
	globalPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(globalPanel);
	globalPanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function bringPanelToTop() {
	overlayManager.bringToTop(this);
}

var iconDefault = 0;
function prepareMapNew(mapName, mapType, parentType) {
	document.forms[setting_formName].id.value = mapContainerId;
	document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
	document.forms[setting_formName].mapType.value = mapType;
	newInformation(mapContainerId);
	displaydetailsPanelButton(false);
	if (mapType == 1) {
		var inner = "Add Folder to "; // Used for both Logical Folder and
		// Folder with Map
		iconDefault = 6;
	} else if (mapType == 2) {
		var inner = "Add Building to ";
		iconDefault = 4;
	} else if (mapType == 99) {
		if (parentType == 3) {
			var inner = "Add Folder below ";
		} else if (parentType == 2) {
			var inner = "Add Folder to Building ";
		} else {
			var inner = "Add Folder to ";
		}
		iconDefault = 4;
	} else {
		if (parentType == 3) {
			var inner = "Add Floor below ";
		} else {
			var inner = "Add Floor to Building ";
		}
		iconDefault = 1;
	}
	detailsPanel.header.innerHTML = inner + mapName;
	prepareDetailsPanel(mapType);
	detailsPanel.cfg.setProperty('visible', true);
	document.getElementById("mapName").focus();
}

function prepareMapEdit(mapName, mapType) {
	document.forms[setting_formName].id.value = mapContainerId;
	document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
	document.forms[setting_formName].mapType.value = mapType;
	editInformation(mapContainerId);
	displaydetailsPanelButton(true);
	if (mapType == 1) {
		var inner = "Edit Folder ";
		iconDefault = 6;
	} else if (mapType == 2) {
		var inner = "Edit Building ";
		iconDefault = 4;
	} else {
		var inner = "Edit Floor ";
		iconDefault = 1;
	}
	detailsPanel.header.innerHTML = inner + mapName;
	prepareDetailsPanel(mapType);
	detailsPanel.cfg.setProperty('visible', true);
	document.getElementById("mapName").focus();
}

hm.map.findRegions = function(srcId, items, select, prefix) {
	for ( var i = 0; i < items.length; i++) {
		if (items[i].tp == 1 && items[i].id != srcId) {
			select.options[select.options.length] = new Option(prefix
					+ items[i].label, items[i].id);
			hm.map.findRegions(srcId, items[i].items, select, prefix + "_");
		}
	}
}
hm.map.filterRegions = function(srcId) {
	var select = document.getElementById("region");
	select.options.length = 0;
	for ( var i = 0; i < mapHierarchy.length; i++) {
		if (mapHierarchy[i] != undefined && mapHierarchy[i].wp) {
			break;
		}
	}
	select.options[0] = new Option(mapHierarchy[i].label, mapHierarchy[i].id);
	hm.map.findRegions(srcId, mapHierarchy[i].items, select, "|_");
}

function prepareFloorMove(targetFloor, floorMoveSucceeded, up) {
	var url = mapSettingsBaseUrl + "?operation=moveFloor&id=" + mapContainerId
			+ "&useWidth=" + up + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : floorMoveSucceeded,
		failure : floorMoveFailed,
		argument : {
			tf : targetFloor
		}
	}, null);
}

function moveFloorUp(o) {
	o.argument.tf.insertBefore(o.argument.tf.previousSibling);
	o.argument.tf.parent.refresh();
	YAHOO.util.Dom.removeClass(selectedNode.labelElId, 'ygtvlabelSel');
	YAHOO.util.Dom.addClass(selectedNode.labelElId, 'ygtvlabelSel');
	repairMoveFloorOps(o.argument.tf);
}

function moveFloorDown(o) {
	o.argument.tf.insertAfter(o.argument.tf.nextSibling);
	o.argument.tf.parent.refresh();
	YAHOO.util.Dom.removeClass(selectedNode.labelElId, 'ygtvlabelSel');
	YAHOO.util.Dom.addClass(selectedNode.labelElId, 'ygtvlabelSel');
	repairMoveFloorOps(o.argument.tf);
}

function repairMoveFloorOps(tf) {
	var mi = selectedNode.data.tp == 1 ? 3 : 2;
	mapOpsMenu.getItem(mi).cfg.setProperty("disabled",
			selectedNode.previousSibling == null);
	mapOpsMenu.getItem(mi + 1).cfg.setProperty("disabled",
			selectedNode.nextSibling == null);
	if (tf.data.tp != 3) {
		var readNodes = new Array();
		var writeNodes = new Array();
		var bldWriteNodes = new Array();
		var globalWriteNodes = new Array();
		findTriggerNodes(mapTree.getRoot().children, readNodes, writeNodes,
				globalWriteNodes, bldWriteNodes);
		if (readNodes.length > 0) {
			readOnlyMenu.cfg.setProperty('trigger', readNodes);
		}
		if (writeNodes.length > 0) {
			fldMenu.cfg.setProperty('trigger', writeNodes);
		}
		if (bldWriteNodes.length > 0) {
			bldMenu.cfg.setProperty('trigger', bldWriteNodes);
		}
		if (globalWriteNodes.length > 0) {
			globalMenu.cfg.setProperty('trigger', globalWriteNodes);
		}
	}
}

function floorMoveFailed(o) {
}

function prepareMapMove(mapName, targetMap) {
	prepareMapClone(mapName, targetMap);
	hm.util.toggleHideElement("trname", true);
	hm.util.toggleHideElement("trpre", true);
	hm.util.toggleHideElement("traddr", true);
	detailsPanel.header.innerHTML = "Move " + mapName;
	document.getElementById("moveTd").style.display = "";
	document.getElementById("createTd").style.display = "none";
}

function prepareMapClone(mapName, targetMap) {
	document.forms[setting_formName].id.value = mapContainerId;
	document.forms[setting_formName].mapType.value = -1;
	displaydetailsPanelButton(false);
	iconDefault = 1;
	detailsPanel.header.innerHTML = "Clone " + mapName;
	prepareDetailsPanel(2);
	hm.util.toggleHideElement("tricn", true);
	if (targetMap.data.tp == 3) {
		hm.util.toggleHideElement("traddr", true);
	} else {
		hm.map.filterRegions(targetMap.data.tp == 1 ? mapContainerId : null);
		var parentId = targetMap.parent.data.id;
		hm.map.setSelectedIndex(document.getElementById("region"), parentId);
		hm.util.toggleHideElement("trprnt", false);
		hm.util.toggleHideElement("trpre", true);
	}
	document.getElementById("sizeX").value = 1;
	document.getElementById("sizeY").value = 1;
	document.getElementById("createTd").firstChild.disabled = false;
	detailsPanel.cfg.setProperty('visible', true);
	document.getElementById("mapName").focus();
}

function prepareDetailsPanel(mapType) {
	var bld = mapType == 2 || mapType == 99;
	hm.util.toggleHideElement("trname", false);
	hm.util.toggleHideElement("trprnt", true);
	hm.util.toggleHideElement("trpre", true);
	hm.util.toggleHideElement("tricn", mapType == 3 || mapType == 99);
	hm.util.toggleHideElement("traddr", !top.gme || mapType == 3);
	hm.util.toggleHideElement("trenv", bld);
	hm.util.toggleHideElement("trimg", bld);
	hm.util.toggleHideElement("tratt", mapType != 3);
	if (bld) {
		hm.util.toggleHideElement("sizeImg", true);
		hm.util.toggleHideElement("sizeBlank", true);
	}
	hm.util.toggleHideElement("trinst", bld);
}

function displaySummaryPanel(mapId, mapName) {
	retrieveSummaryInfo(mapId);
	var dialogTitleDiv = document.getElementById("summaryTitle");
	dialogTitleDiv.innerHTML = networkSummaryTitle + mapName;
	var up_td = document.getElementById("upManagedHiveApTd");
	var down_td = document.getElementById("downManagedHiveApTd");
	var upNew_td = document.getElementById("upNewHiveApTd");
	var downNew_td = document.getElementById("downNewHiveApTd");
	var client_td = document.getElementById("activeClientTd");
	up_td.innerHTML = '_';
	down_td.innerHTML = '_';
	upNew_td.innerHTML = '_';
	downNew_td.innerHTML = '_';
	client_td.innerHTML = '_';
	networkSummaryPanel.cfg.setProperty('y', 125);
	networkSummaryPanel.cfg.setProperty('visible', true);
	hm.map.summaryIsShow = true;
}

function displaydetailsPanelButton(isEditDialog) {
	var createBtnElement = document.getElementById("createTd");
	var updateBtnElement = document.getElementById("updateTd");
	createBtnElement.firstChild.disabled = true;
	updateBtnElement.firstChild.disabled = true;
	if (isEditDialog) {
		createBtnElement.style.display = "none";
		updateBtnElement.style.display = "";
	} else {
		createBtnElement.style.display = "";
		updateBtnElement.style.display = "none";
		document.getElementById("mapName").value = "";
		document.getElementById("mapAddress").value = "";
	}
	document.getElementById("moveTd").style.display = "none";
}

var rootMapData = null;
function processRootMapDetails(o) {
	eval("rootMapData = " + o.responseText);
	if (rootMapData) {
		var vpX = YAHOO.util.Dom.getX("viewport");
		if (vpX < 5) {
			var timeoutId = setTimeout("processMapData(rootMapData)", 100); // seconds
			return;
		} else {
			processMapData(rootMapData);
		}
	}
}
function processMapData(data) {
	if (data) {
		hm.map.mapWritePermission = data.writePermission;
		canvasBackgroundPath = data.path;
		if (data.bg) {
			canvasBackground = data.path + data.bg;
		} else {
			canvasBackground = data.commPath + "/images/spacer.gif";
		}
		gridSize = data.gridSize;
		leafMapContainer = data.leafMapContainer;
		actualHeight = 0;
		if (gridSize > 0) {
			hm.map.setLengthUnit(data);
			actualGridSize = data.actualGridSize;
			actualHeight = data.actualHeight;
			actualScale = data.actualHeight / data.height;
			mapGeo = null;
		} else {
			hm.map.setMapGeo(data);
		}
		var vp = hm.map.setViewportSize("viewport", data.width, data.height);
		initMapControls();
		hm.map.setCanvasSize(top, 1);
		moveSummaryPanel();
		if (!vp.style.backgroundColor) {
			vp.style.backgroundColor = "#fff";
			vp.src = loadingMap2Action + "&start=" + new Date().getTime();
		} else {
			// Must be due to back button navigation, just refresh
			refreshMap();
		}
	}
}

function moveSummaryPanel() {
	// move summary panel
	var newX = YAHOO.util.Dom.getX("viewport") + hm.map.viewportWidth - 354;
	if (YAHOO.env.ua.ie > 0) {
		newX -= 4;
	}
	networkSummaryPanel.cfg.setProperty('x', newX);
}
function requestNodes(canvasWindow) {
	hm.map.canvasWindow = canvasWindow;
	if (scaleDelta > 0) {
		// Client size re-scaling
		hm.map.zoomMapNodes(scaleDelta);
		var img = hm.map.canvasWindow.document.getElementById("imageId");
		img.src = canvasBackground;
		toggleHeatChannelMap();
		if (!hm.map.planningMode()) {
			var oneClient = false;
			var oneRogue = false;
			if (rssiData) {
				var mac = rssiData[0].mac;
				var pid = rssiData[0].pid;
				oneClient = clientMac.length > 0 && pid == "clients";
				oneRogue = clientMac.length > 0 && pid == "rogues";
				var circle = rssiData[0].circle > 0 ? "|" : "";
				rssiData = null;
				showRssi(circle + mac, pid);
			}
			var cb = document.forms[formName].clients;
			if (cb.checked && !oneClient) {
				toggleClients(cb);
			}
			cb = document.forms[formName].rogues;
			if (cb.checked && !oneRogue) {
				toggleRogues(cb);
			}
			hm.map.startAlarmsTimer();
		}
	} else {
		// Fetch nodes from DB
		hm.map.requestMapNodes(getApLabels());
	}
}
function requestBldNodes(canvasWindow, floorCount) {
	hm.map.canvasWindow = canvasWindow;
	if (floorCount == 0) {
		hm.map.showStreetMap(null);
	}
}

function originUpdated(o) {
	eval("var data = " + o.responseText);
	if (data.success) {
		viewport.location.reload();
	}
}
function removePerimeter(pi) {
	hm.map.submitWalls("removePerimeter", perimeterRemoved, "pwr1=" + pi);
}
function updatePerimeter(pi, wi) {
	hm.map.submitWalls("updatePerimeter", perimeterRemoved, "pwr1=" + pi
			+ "&pwr2=" + wi);
}
function perimeterRemoved() {
	hm.map.newPerim = false;
	if (hm.map.planningMode()) {
		hm.map.updateSpillCache(getFrequency(),
				document.forms[formName].rssiThreshold.value, true,
				hm.map.updateSpillCacheWallsDone);
	} else if (hm.map.viewWindow.getLayers()) {
		invalidateHeatChannelMap();
	}
}
function saveWalls() {
	hm.map.submitWalls("saveWalls", wallsSaved, hm.map.getWallsData());
}
function wallsSaved(o) {
	try {
		eval("var data = " + o.responseText);
	} catch (e) {
		showAjaxSubmitError();
		return;
	}
	hm.map.newWalls = false;
	toggleRemoveWalls();
	if (hm.map.planningMode()) {
		hm.map.updateSpillCache(getFrequency(),
				document.forms[formName].rssiThreshold.value, true,
				hm.map.updateSpillCacheWallsDone);
	} else if (hm.map.viewWindow.getLayers()) {
		invalidateHeatChannelMap();
	}
}

function showNote(message, duration) {
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document.createTextNode(message));
	hm.util.show('processing');
	delayHideProcessing(duration);
}

function showSuccessNote(message) {
	showNote(message, 5);
}

var processingTimeoutId;
function delayHideProcessing(seconds) {
	processingTimeoutId = setTimeout("hideProcessing()", seconds * 1000); // seconds
}
function hideProcessing() {
	hm.util.hide('processing');
}
function onUnloadNotes() {
	clearTimeout(processingTimeoutId);
	// avoid js error
	if (planningOnly) {
		if (pollTimeoutId) {
			clearTimeout(pollTimeoutId); // defined in techSupport.jsp
		}
	}
}
function onUnloadPage() {
	hm.map.clearAlarmsTimer();
}

function toggleMapImage(select) {
	hm.util.toggleHideElement("sizeImg", select.value.length == 0);
	hm.util.toggleHideElement("sizeBlank", select.value.length > 0);
}

var detailSuccess = function(o) {
	eval("var detail = " + o.responseText);
	var iconElement = document.getElementById("mapIcon");
	var imageElement = document.getElementById("mapImage");
	var mapNameElement = document.getElementById("mapName");
	var mapAddressElement = document.getElementById("mapAddress");
	var mapWidthElement = document.getElementById("mapWidth");
	var sizeXelement = document.getElementById("sizeX");
	var sizeYelement = document.getElementById("sizeY");
	var apElevationElement = document.getElementById("apElevation");
	var attenuationElement = document.getElementById("attenuation");
	var mapEnvElement = document.getElementById("mapEnv");
	var intervalElement = document.getElementById("refreshInterval");
	var reviewImageElement = document.getElementById("mapReviewImage");
	var popUpFlagElement = document.getElementById("popUpFlag");
	var useStreetMapsElement = document.getElementById("useStreetMaps");

	var icons = detail.icons;
	var images = detail.images;
	var map = detail.map;
	var msg = detail.msg;
	var interval = detail.interval;
	var flag = detail.flag;
	var resolution = detail.resolution;
	var minRssiCount = detail.minRssi;
	var clientRssiThreshold = detail.clientRssiThreshold;
	var calibrateRssiFrom = detail.calibrateRssiFrom;
	var calibrateRssiUntil = detail.calibrateRssiUntil;
	var bgMapOpacity = detail.bgMapOpacity;
	var heatMapOpacity = detail.heatMapOpacity;
	var wallsOpacity = detail.wallsOpacity;
	var useStreetMaps = detail.useStreetMaps;
	var realTime = detail.realTime;
	var locationWindow = detail.locationWindow;

	if (icons) {
		iconElement.length = icons.length;
		for ( var i = 0; i < icons.length; i++) {
			iconElement.options[i].value = icons[i].v;
			iconElement.options[i].text = icons[i].t;
		}
		if (iconElement.length > 0) {
			iconElement.selectedIndex = 0;
		}
		// reset this selection value
		if (mapEnvElement.length > 1) {
			if (planningOnly) {
				mapEnvElement.selectedIndex = 3;
			} else {
				mapEnvElement.selectedIndex = 0;
			}
		}
		// reset this textfield value
		mapWidthElement.value = '';
		apElevationElement.value = '';
	}
	if (images) {
		imageElement.length = images.length + 1;
		imageElement.options[0].value = "";
		imageElement.options[0].text = "None";
		reviewImageElement.length = images.length;
		for ( var i = 0; i < images.length; i++) {
			imageElement.options[i + 1].value = images[i].t;
			imageElement.options[i + 1].text = images[i].t;
			reviewImageElement.options[i].value = images[i].t;
			reviewImageElement.options[i].text = images[i].t;
		}
		if (imageElement.length > 0) {
			imageElement.selectedIndex = 0;
		}
		reviewImageElement.selectedIndex = -1;
	}
	if (map) {
		attenuationElement.value = map.loss;
		if (map.mapWidth == 0) {
			mapWidthElement.value = '';
			apElevationElement.value = '';
		} else {
			mapWidthElement.value = map.mapWidth;
			sizeXelement.value = map.mapWidth;
			sizeYelement.value = map.mapHeight;
			apElevationElement.value = map.apElevation;
			var select = document.getElementById('mapWidthUnit');
			select.options[map.lengthUnit - 1].selected = true;
			select = document.getElementById('apElevationUnit');
			select.options[map.lengthUnit - 1].selected = true;
			select = document.getElementById('mapBlankUnit');
			select.options[map.lengthUnit - 1].selected = true;
		}
		mapEnvElement.value = map.mapEnv;
		mapNameElement.value = map.mapName;
		mapAddressElement.value = map.address;
		if (!map.image) {
			imageElement.value = "";
		} else {
			imageElement.value = map.image;
		}
		iconElement.value = map.icon;
	} else {
		iconElement.selectedIndex = iconDefault;
		attenuationElement.value = 15;
		sizeXelement.value = '';
		sizeYelement.value = '';
	}
	if (iconDefault != 4) {
		toggleMapImage(imageElement);
	}
	if (interval) {
		intervalElement.value = interval;
		var select = document.getElementById('heatmapResolution');
		select.options[resolution - 1].selected = true;
		select = document.getElementById('minRssi');
		select.options[minRssiCount - 2].selected = true;
		select = document.getElementById('clientRssiThreshold');
		select.options[clientRssiThreshold - 40].selected = true;
		select = document.getElementById('calibrateRssiFrom');
		select.options[calibrateRssiFrom - 40].selected = true;
		select = document.getElementById('calibrateRssiUntil');
		select.options[calibrateRssiUntil - 40].selected = true;
		select = document.getElementById('bgMapOpacity');
		select.options[bgMapOpacity / 10 - 1].selected = true;
		select = document.getElementById('heatMapOpacity');
		select.options[heatMapOpacity / 10 - 1].selected = true;
		select = document.getElementById('wallsOpacity');
		select.options[wallsOpacity / 10 - 1].selected = true;
		select = document.getElementById('locationWindow');
		select.value = locationWindow;
		select.disabled = realTime;
		if (realTime) {
			select = document.getElementById('rtYes');
		} else {
			select = document.getElementById('rtNo');
		}
		select.checked = true;
		document.getElementById("intervalSet").disabled = false;
	}
	useStreetMapsElement.checked = useStreetMaps;
	popUpFlagElement.checked = flag;
	document.getElementById("createTd").firstChild.disabled = false;
	document.getElementById("updateTd").firstChild.disabled = false;
	if (msg) {
		showSuccessNote(msg);
	}
}

var detailFailed = function(o) {
};

var mapDetailsCB = {
	success : detailSuccess,
	failure : detailFailed,
	upload : uploadResult
};

function editInformation(id) {
	url = mapSettingsActionUrl + "?operation=edit&id=" + id + "&ignore="
			+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, mapDetailsCB,
			null);
}
function newInformation(id) {
	url = mapSettingsActionUrl + "?operation=new&domainMapId=" + id
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, mapDetailsCB,
			null);
}
function editGlobalParams(id) {
	document.getElementById("intervalSet").disabled = true;
	globalPanel.mapContainerId = id; // store the map container id
	url = mapSettingsActionUrl + "?operation=editGlobalParams&id=" + id
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, mapDetailsCB,
			null);
}
function reviewImages() {
	var domainMapId = document.forms[upload_formName].domainMapId.value;
	url = mapSettingsActionUrl + "?operation=reviewImages&domainMapId="
			+ domainMapId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, mapDetailsCB,
			null);
}
var uploadResult = function(o) {
	eval("var result = " + o.responseText);
	alert(result);
}

function showImageReviewPanel() {
	if (mapContainerId != null) {
		document.forms[upload_formName].domainMapId.value = mapContainerId;
		reviewImages();
		hideTraditionalContent();
		imageReviewPanel.cfg.setProperty('visible', true);
	}
}

function setGlobalParams(refreshIntervalTitle) {
	var intervalTfd = document.getElementById("refreshInterval");
	var showOnHover = document.getElementById("showOnHover");
	var calibrateHeatmap = document.getElementById("calibrateHeatmap");
	var useHeatmapCB = document.getElementById("useHeatmap");
	var useHeatmap = false;
	if (useHeatmapCB != null) {
		useHeatmap = useHeatmapCB.checked;
	}
	var periVal = document.getElementById("periVal");
	var heatmapResolution = document.getElementById("heatmapResolution");
	var minRssi = document.getElementById("minRssi");
	var showRssi = document.getElementById("showRssi");
	var clientRssiThreshold = document.getElementById("clientRssiThreshold");
	var calibrateRssiFrom = document.getElementById("calibrateRssiFrom");
	var calibrateRssiUntil = document.getElementById("calibrateRssiUntil");
	var bgMapOpacity = document.getElementById("bgMapOpacity");
	var heatMapOpacity = document.getElementById("heatMapOpacity");
	var wallsOpacity = document.getElementById("wallsOpacity");
	var realTime = document.getElementById("rtYes");
	var locationWindow = document.getElementById("locationWindow");
	var popUpFlagElement = document.getElementById("popUpFlag");
	var useStreetMapsElement = document.getElementById("useStreetMaps");
	var flagValue = popUpFlagElement.checked;
	if (intervalTfd.value.length == 0) {
		hm.util.reportFieldError(intervalTfd, refreshIntervalFieldErr);
		intervalTfd.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(intervalTfd.value,
			refreshIntervalTitle, 30, 900);
	if (message != null) {
		hm.util.reportFieldError(intervalTfd, message);
		intervalTfd.focus();
		return;
	}
	if (calibrateRssiUntil.value >= calibrateRssiFrom.value) {
		hm.util.reportFieldError(calibrateRssiUntil, 'The range is invalid.');
		calibrateRssiUntil.focus();
		return;
	}
	url = mapSettingsBaseUrl
			+ "?operation=setGlobalParams&refreshInterval="
			+ intervalTfd.value
			+ "&id="
			+ globalPanel.mapContainerId
			+ "&ppFlag="
			+ flagValue
			+ "&showRssi="
			+ showRssi.checked
			+ "&showOnHover="
			+ showOnHover.checked
			+ "&calibrateHeatmap="
			+ calibrateHeatmap.checked
			+ "&useHeatmap="
			+ useHeatmap
			+ "&heatmapResolution="
			+ heatmapResolution.options[heatmapResolution.selectedIndex].value
			+ "&minRssi="
			+ minRssi.options[minRssi.selectedIndex].value
			+ "&clientRssiThreshold="
			+ clientRssiThreshold.options[clientRssiThreshold.selectedIndex].value
			+ "&calibrateRssiFrom="
			+ calibrateRssiFrom.options[calibrateRssiFrom.selectedIndex].value
			+ "&calibrateRssiUntil="
			+ calibrateRssiUntil.options[calibrateRssiUntil.selectedIndex].value
			+ "&bgMapOpacity="
			+ bgMapOpacity.options[bgMapOpacity.selectedIndex].value
			+ "&heatMapOpacity="
			+ heatMapOpacity.options[heatMapOpacity.selectedIndex].value
			+ "&wallsOpacity="
			+ wallsOpacity.options[wallsOpacity.selectedIndex].value
			+ "&periVal=" + periVal.checked + "&realTime=" + realTime.checked
			+ "&locationWindow=" + locationWindow.value + "&useStreetMaps="
			+ useStreetMapsElement.checked + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : globalSuccess,
		failure : connectedFailed,
		argument : {
			ppFlag : flagValue,
			ppInterval : intervalTfd.value
		}
	}, null);
	globalPanel.cfg.setProperty('visible', false);
	if (!flagValue) {
		networkSummaryPanel.cfg.setProperty('visible', false);
		hm.map.summaryIsShow = false;
	}
}

var globalSuccess = function(o) {
	eval("var result = " + o.responseText);
	if (result.msg) {
		document.forms[formName].mapOps.disabled = true;
		top.invalidateHeatChannelMap();
		showSuccessNote(result.msg);
		hm.map.loadPopUpFlag(o.argument.ppFlag);
		hm.map.loadRefreshInterval(o.argument.ppInterval);
		document.location.reload();
	}
}

function deleteImage() {
	var imageElement = document.getElementById("mapReviewImage");
	if (imageElement.selectedIndex >= 0) {
		var imageValue = imageElement.options[imageElement.selectedIndex].text;
	}
	if (imageElement.selectedIndex < 0 || imageValue.length == 0) {
		hm.util.reportFieldError(imageElement, selectImageFieldErr);
		imageElement.focus();
		return;
	}
	removeMapImage(imageValue);
}
function calDeleteImage() {
	var imageElement = document.getElementById("mapReviewImage");
	var imageValue = imageElement.options[imageElement.selectedIndex].text;
	var domainMapId = document.forms[upload_formName].domainMapId.value;
	url = mapSettingsActionUrl + "?operation=deleteImage&imageName="
			+ encodeURIComponent(imageValue) + "&domainMapId=" + domainMapId
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, mapDetailsCB,
			null);
}
function showImage(imageElement) {
	var imageName = imageElement.options[imageElement.selectedIndex].text;
	var contentEl = document.getElementById("imageContent");
	var value = canvasBackgroundPath + imageName;
	var img = document.createElement("img");
	img.src = value;
	img.width = "230";
	img.height = "165";
	hm.util.replaceChildren(contentEl, img);
}

var connectedFailed = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
}
var clientInfoPanel = null;
function createClientInfoPanel() {
	var div = window.document.getElementById('clientInfoPanel');
	clientInfoPanel = new YAHOO.widget.Panel(div, {
		width : "620px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	clientInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(clientInfoPanel);
	clientInfoPanel.beforeShowEvent.subscribe(bringPanelToTop);
}
var neighborInfoPanel = null;
function createNeighborInfoPanel() {
	var div = window.document.getElementById('neighborInfoPanel');
	neighborInfoPanel = new YAHOO.widget.Panel(div, {
		width : "620px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	neighborInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(neighborInfoPanel);
	neighborInfoPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external
	// content to load
	waitingPanel = new YAHOO.widget.Panel('wait', {
		width : "260px",
		fixedcenter : true,
		close : false,
		draggable : false,
		zindex : 4,
		modal : true,
		visible : false
	});
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody(waitingPanelBody);
	waitingPanel.render(document.body);
}

var networkSummaryPanel = null;
function createNetworkSummaryPanel() {
	var div = window.document.getElementById('networkSummaryPanel');
	networkSummaryPanel = new YAHOO.widget.Panel(div, {
		width : "390px",
		visible : false,
		draggable : true,
		constraintoviewport : true
	});
	networkSummaryPanel.cfg.setProperty('x', document.body.clientWidth - 360);
	networkSummaryPanel.cfg.setProperty('y', 125);
	networkSummaryPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(networkSummaryPanel);
	networkSummaryPanel.beforeShowEvent.subscribe(bringPanelToTop);
	networkSummaryPanel.beforeHideEvent.subscribe(function() {
		hm.map.summaryIsShow = false;
	});
}

var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, {
		width : "600px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	cliInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(cliInfoPanel);
	cliInfoPanel.hideEvent.subscribe(resetParams);
	cliInfoPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var cwpRemovePanel = null;
function createCwpRemovePanel() {
	var div = window.document.getElementById('cwpRemovePanel');
	cwpRemovePanel = new YAHOO.widget.Panel(div, {
		width : "600px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	cwpRemovePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(cwpRemovePanel);
	cwpRemovePanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var imageBootPanel = null;
function createImageBootPanel() {
	var div = window.document.getElementById('imageBootPanel');
	imageBootPanel = new YAHOO.widget.Panel(div, {
		width : "310px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	imageBootPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(imageBootPanel);
	imageBootPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

function resetPanelSize(panelHeight, panel) {
	var headerHeight = panel.header.offsetHeight; // Content + Padding +
	// Border
	var footerHeight = panel.footer.offsetHeight; // Content + Padding +
	// Border
	var headerWidth = panel.header.offsetWidth; // Content + Padding + Border;
	var bodyTopHeight = YAHOO.util.Dom.get('bd_top').offsetHeight;

	var bodyHeight = (panelHeight - headerHeight - footerHeight);
	var bodyContentHeight = (IE_QUIRKS) ? bodyHeight - bodyTopHeight
			: bodyHeight - 20 - bodyTopHeight;

	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('cli_viewer'), 'height',
			bodyContentHeight + 'px');

	if (IE_SYNC) {
		YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('cli_viewer'), 'width',
				(headerWidth - 24) + 'px');
		// Keep the underlay and iframe size in sync.

		// You could also set the width property, to achieve the
		// same results, if you wanted to keep the panel's internal
		// width property in sync with the DOM width.

		panel.sizeUnderlay();

		// Syncing the iframe can be expensive. Disable iframe if you
		// don't need it.

		panel.syncIframe();
	}
}

function resetParams() {
	var interfaceEl = document.getElementById("interfaceType");
	var callIdEl = document.getElementById("callId");
	interfaceEl.selectedIndex = 0;
	callIdEl.value = "";
}

var moveAPsPanel = null;
var moveAPsChanged = false;
function createMoveAPsPanel() {
	var div = document.getElementById('moveAPsPanel');
	moveAPsPanel = new YAHOO.widget.Panel(div, {
		width : "450px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		close : true,
		constraintoviewport : true
	});
	moveAPsPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(moveAPsPanel);
	moveAPsPanel.beforeHideEvent.subscribe(closeMoveAPsPanel);
	moveAPsPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

var apInfoPanel = null;
function createAPInfoPanel() {
	var div = window.document.getElementById('apInfoPanel');
	apInfoPanel = new YAHOO.widget.Panel(div, {
		width : "450px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true,
		modal : false
	});
	apInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(apInfoPanel);
	apInfoPanel.beforeShowEvent.subscribe(bringPanelToTop);
}

function retrieveClientInfo() {
	doAjaxRequest("retrieveClientInfo", "", clientInfoResult, true);
}

function retrieveNeighborInfo() {
	doAjaxRequest("retrieveNeighborInfo", "", neighborInfoResult, true);
}

function requestSingleItemCli(menuText) {
	doAjaxRequest("requestSingleItemCli", menuText, cliInfoResult, true);
}

function requestConfigurationAudit() {
	doAjaxRequest("configurationAudit", "", cliInfoResult, true);
}

function sipCalls() {
	var callId = document.getElementById("callId");
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, true, "value1="
			+ callId.value);
	document.getElementById("cli_viewer").innerHTML = '';
}

function retrieveIfDetail() {
	var interfaceElement = document.getElementById("interfaceType");
	var item = interfaceElement.options[interfaceElement.selectedIndex].text;
	var interfaceList = new Array();
	for(var i=0;i<interfaceElement.options.length;i++){
		interfaceList[interfaceList.length]=interfaceElement.options[i].value;
	}
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, true,"value1="+item+"&value2="+interfaceList);
	document.getElementById("cli_viewer").innerHTML = '';
}

function ping() {
	var targetIpElement = document.getElementById("targetIp");
	if (targetIpElement.value.length == 0) {
		hm.util.reportFieldError(targetIpElement, diagnosticsPingFieldErr);
		targetIpElement.focus();
		return;
	}
	if (!hm.util.validateIpAddress(targetIpElement.value)) {
		hm.util.reportFieldError(targetIpElement, diagnosticsPingFieldErr);
		targetIpElement.focus();
		return;
	}
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, true, "value1="
			+ targetIpElement.value);
	document.getElementById("cli_viewer").innerHTML = '';
}

function retrieveAlarmInfo() {
	doAjaxRequest("alarm", "", alarmInfo, false);
}

var alarmInfo = function(o) {
	eval("var result = " + o.responseText);
	var redirect_url = alarmsAction + "?operation=search&apId=" + result.v
			+ "&ignore=" + new Date().getTime();
	window.location.href = redirect_url;
}

function retrieveSummaryInfo(mapId) {
	url = mapSettingsActionUrl + "?operation=retrieveSummaryInfo&id=" + mapId
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : summaryInfoResult,
		failure : null
	}, null);
}
var summaryInfoResult = function(o) {
	eval("var result = " + o.responseText);
	setupSummaryInfo(result);
}

function setupSummaryInfo(result) {
	// set up HiveAp count;
	if (result.m_up) {
		var up_td = document.getElementById("upManagedHiveApTd");
		var up_a = document.createElement("a");
		var up_url = hiveApAction
				+ "?operation=view&hmListType=managedHiveAps&isNew=false&mapId="
				+ result.map_Id + "&isConnected=true" + "&ignore="
				+ new Date().getTime();
		up_a.setAttribute("href", up_url);
		up_a.innerHTML = result.m_up;
		up_td.innerHTML = '';
		up_td.appendChild(up_a);
	}
	if (result.n_up) {
		var up_td = document.getElementById("upNewHiveApTd");
		var up_a = document.createElement("a");
		var up_url = hiveApAction
				+ "?operation=view&hmListType=managedHiveAps&isNew=true&mapId="
				+ result.map_Id + "&isConnected=true" + "&ignore="
				+ new Date().getTime();
		up_a.setAttribute("href", up_url);
		up_a.innerHTML = result.n_up;
		up_td.innerHTML = '';
		up_td.appendChild(up_a);
	}
	// set down HiveAp count;
	if (result.m_down) {
		var down_td = document.getElementById("downManagedHiveApTd");
		var down_a = document.createElement("a");
		var down_url = hiveApAction
				+ "?operation=view&hmListType=managedHiveAps&isNew=false&mapId="
				+ result.map_Id + "&isConnected=false" + "&ignore="
				+ new Date().getTime();
		down_a.setAttribute("href", down_url);
		down_a.innerHTML = result.m_down;
		down_td.innerHTML = '';
		down_td.appendChild(down_a);
	}
	if (result.n_down) {
		var down_td = document.getElementById("downNewHiveApTd");
		var down_a = document.createElement("a");
		var down_url = hiveApAction
				+ "?operation=view&hmListType=managedHiveAps&isNew=true&mapId="
				+ result.map_Id + "&isConnected=false" + "&ignore="
				+ new Date().getTime();
		down_a.setAttribute("href", down_url);
		down_a.innerHTML = result.n_down;
		down_td.innerHTML = '';
		down_td.appendChild(down_a);
	}
	// set client count;
	if (result.client) {
		var client_td = document.getElementById("activeClientTd");
		var client_a = document.createElement("a");
		var client_url = clientMonitorAction
				+ "?operation=view&mapContainerId=" + result.map_Id
				+ "&ignore=" + new Date().getTime();
		client_a.setAttribute("href", client_url);
		client_a.innerHTML = result.client;
		client_td.innerHTML = '';
		client_td.appendChild(client_a);
	}
}

function doAjaxRequest(operation, menuText, callback, showWaiting, postData) {
	document.forms[formName].operation.value = operation;
	if (menuText != undefined && menuText != "") {
		document.forms[formName].menuText.value = menuText;
	}
	YAHOO.util.Connect.setForm(document.forms[formName]);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', mapNodesAction, {
		success : callback,
		failure : connectedFailed,
		timeout : 60000
	}, postData);
	if (showWaiting && waitingPanel != null) {
		waitingPanel.show();
	}
}

var _abgModelItems = [ 'all', 'wifi0', 'wifi1', 'eth0', 'mgt0', 'wifi0.1',
		'wifi0.2', 'wifi0.3', 'wifi0.4', 'wifi0.5', 'wifi0.6', 'wifi0.7',
		'wifi1.1', 'wifi1.2', 'wifi1.3', 'wifi1.4', 'wifi1.5', 'wifi1.6',
		'wifi1.7' ];
var _11nModelItems = [ 'all', 'wifi0', 'wifi1', 'eth0', 'eth1', 'mgt0', 'agg0',
		'red0', 'wifi0.1', 'wifi0.2', 'wifi0.3', 'wifi0.4', 'wifi0.5',
		'wifi0.6', 'wifi0.7', 'wifi0.8', 'wifi0.9', 'wifi0.10', 'wifi0.11',
		'wifi0.12', 'wifi0.13', 'wifi0.14', 'wifi0.15', 'wifi0.16', 'wifi1.1',
		'wifi1.2', 'wifi1.3', 'wifi1.4', 'wifi1.5', 'wifi1.6', 'wifi1.7',
		'wifi1.8', 'wifi1.9', 'wifi1.10', 'wifi1.11', 'wifi1.12', 'wifi1.13',
		'wifi1.14', 'wifi1.15', 'wifi1.16' ];
var _11nModelOneRaidoSimpleItems = [ 'all', 'wifi0', 'eth0', 'eth1', 'mgt0',
		'agg0', 'red0', 'wifi0.1', 'wifi0.2', 'wifi0.3', 'wifi0.4', 'wifi0.5',
		'wifi0.6', 'wifi0.7', 'wifi0.8' ];
var _11nModelOneRadioItems = [ 'all', 'wifi0', 'eth0', 'eth1', 'mgt0', 'agg0',
		'red0', 'wifi0.1', 'wifi0.2', 'wifi0.3', 'wifi0.4', 'wifi0.5',
		'wifi0.6', 'wifi0.7', 'wifi0.8', 'wifi0.9', 'wifi0.10', 'wifi0.11',
		'wifi0.12', 'wifi0.13', 'wifi0.14', 'wifi0.15', 'wifi0.16' ];
var _11nNoRadioItems = [ 'all', 'eth0', 'eth1', 'mgt0', 'agg0', 'red0' ];

var _11nCvgItems = [ 'all' ];

var updateInterfaceItems = function(is11n, radioDsType,interfacelist) {
	var interfaceEl = document.getElementById("interfaceType");
	if(interfacelist == "undefined" || interfacelist==null){
		interfacelist = ["all"];
	}
	interfaceEl.length = interfacelist.length;
	for(var i=0; i<interfacelist.length; i++){
		interfaceEl.options[i].value = interfacelist[i];
		interfaceEl.options[i].text = interfacelist[i];
	}
	/*if (is11n) {// the ap is 11n model
		// radioDsType 1: 8 one radio wifi, 2: 16 one radio wifi, 3: no wifi
		// 4:cvg
		if (radioDsType == 1) {
			interfaceEl.length = _11nModelOneRaidoSimpleItems.length;
			for ( var i = 0; i < _11nModelOneRaidoSimpleItems.length; i++) {
				interfaceEl.options[i].value = _11nModelOneRaidoSimpleItems[i];
				interfaceEl.options[i].text = _11nModelOneRaidoSimpleItems[i];
			}
		} else if (radioDsType == 2) {
			interfaceEl.length = _11nModelOneRadioItems.length;
			for ( var i = 0; i < _11nModelOneRadioItems.length; i++) {
				interfaceEl.options[i].value = _11nModelOneRadioItems[i];
				interfaceEl.options[i].text = _11nModelOneRadioItems[i];
			}
		} else if (radioDsType == 3) {
			interfaceEl.length = _11nNoRadioItems.length;
			for ( var i = 0; i < _11nNoRadioItems.length; i++) {
				interfaceEl.options[i].value = _11nNoRadioItems[i];
				interfaceEl.options[i].text = _11nNoRadioItems[i];
			}
		} else {
			interfaceEl.length = _11nModelItems.length;
			for ( var i = 0; i < _11nModelItems.length; i++) {
				interfaceEl.options[i].value = _11nModelItems[i];
				interfaceEl.options[i].text = _11nModelItems[i];
			}
		}
	} else {
		if (radioDsType == 4) {
			interfaceEl.length = _11nCvgItems.length;
			for ( var i = 0; i < _11nCvgItems.length; i++) {
				interfaceEl.options[i].value = _11nCvgItems[i];
				interfaceEl.options[i].text = _11nCvgItems[i];
			}
		} else {
			interfaceEl.length = _abgModelItems.length;
			for ( var i = 0; i < _abgModelItems.length; i++) {
				interfaceEl.options[i].value = _abgModelItems[i];
				interfaceEl.options[i].text = _abgModelItems[i];
			}
		}
	}*/
}

var selectedLeafNodeId;
var cliInfoResult = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	var pingTr = document.getElementById("pingTr");
	var interfaceTr = document.getElementById("interfaceTr");
	var sipTr = document.getElementById("sipTr");
	if (statsInterfaceOp == result.t) {
		pingTr.style.display = "none";
		sipTr.style.display = "none";
		interfaceTr.style.display = "";
		updateInterfaceItems(result.is11n, result.radioDsType,result.interfacelist);
	} else if (diagPingOp == result.t) {
		pingTr.style.display = "";
		sipTr.style.display = "none";
		interfaceTr.style.display = "none";
		if (result.ip) {
			document.getElementById("targetIp").value = result.ip;
		}
	} else if (diagTraceOp == result.t) {
		pingTr.style.display = "none";
		sipTr.style.display = "none";
		interfaceTr.style.display = "none";
		if (result.ip) {
			document.getElementById("tracerouteIp").value = result.ip;
		}
	} else if (sipNameOp == result.t) {
		pingTr.style.display = "none";
		interfaceTr.style.display = "none";
		sipTr.style.display = "";
		if (result.callId) {
			document.getElementById("callId").value = result.callId;
		}
	} else {
		pingTr.style.display = "none";
		sipTr.style.display = "none";
		interfaceTr.style.display = "none";
	}
	if (result.t) {
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = result.t;
	}
	if (result.h) {
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = dialogTitleDiv.innerHTML + ' - ' + result.h;
	}

	// var cliDiv = document.getElementById("cli_viewer");
	// cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g, "<br>") + "</pre>";

	var cliDiv = document.getElementById("cli_viewer");
	if (showDhcpClientAllocationOp == result.t) {
		if (result.dhcpMsgs) {
			var dhcpMsgs = result.dhcpMsgs;
			var subnets = result.subnets;
			var reult = "<table><tr><th>Sub-Network</th><th>DHCP Client</th></tr>";
			for ( var i = 0; i < dhcpMsgs.length; i++) {
				reult = reult + "<tr><td>" + subnets[i] + "</td><td>";
				reult = reult + "<pre>" + dhcpMsgs[i].replace(/\n/g, "<br>")
						+ "</pre>";
				reult = reult + "</td></tr>";
			}
			reult = reult + "</table>";
			cliDiv.innerHTML = reult
		} else {
			cliDiv.innerHTML = allocationNotExistMsg;
		}

	} else {
		cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g, "<br>") + "</pre>";

		// fix bug 22830
		if (result.v == resetPseSuccessMsg) {
			setTimeout(function() {
				cliDiv.innerHTML = "<pre>" + resetPseCompleteMsg + "</pre>";
			}, 3000);
		}
	}

	if (null == cliInfoPanel) {
		createCliInfoPanel();
	}

	var panelHeight = YAHOO.util.Dom.get('cliInfoPanel').offsetHeight;
	resetPanelSize(panelHeight, cliInfoPanel);
	cliInfoPanel.cfg.setProperty('visible', true);
}

var clientInfoResult = function(o) {
	eval("var result = " + o.responseText);
	hm.util.hideFieldError();
	var clientInfoElement = document.getElementById("clientInfoLabel");
	var clientInfoTable = document.getElementById("clientInfoTable");
	var titleDiv = document.getElementById("clientTitle");
	retrieveResult(result, clientInfoTable, clientInfoElement, titleDiv);
	clientInfoPanel.cfg.setProperty('visible', true);
}
var neighborInfoResult = function(o) {
	eval("var result = " + o.responseText);
	hm.util.hideFieldError();
	var neighborInfoElement = document.getElementById("neighborInfoLabel");
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	var titleDiv = document.getElementById("neighborTitle");
	retrieveResult(result, neighborInfoTable, neighborInfoElement, titleDiv);
	neighborInfoPanel.cfg.setProperty('visible', true);
}
function retrieveResult(result, table, tableLabelElement, dialogTitleDiv) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	if (result.h) {
		var currentStr = dialogTitleDiv.innerHTML;
		if (currentStr.indexOf(' - ') > 0) {
			dialogTitleDiv.innerHTML = currentStr.slice(0, currentStr
					.indexOf(' - '))
					+ ' - ' + result.h;
		} else {
			dialogTitleDiv.innerHTML = currentStr + ' - ' + result.h;
		}
	}
	if (result.e) {
		hm.util.reportFieldError(tableLabelElement, result.e);
		return;
	}
	if (result.data) {
		var data = result.data;
		for ( var i = 0; i < data.length; i++) {
			var rowData = data[i].rowData;
			if (i + 1 < table.rows.length) {
				table.deleteRow(i + 1);
			}
			var row = table.insertRow(i + 1);
			for ( var j = 0; j < rowData.length; j++) {
				var cell = row.insertCell(j);
				cell.className = 'list';
				cell.innerHTML = rowData[j].v + "&nbsp;";
			}
		}
	}
}
function clearClientInfoTable() {
	var clientInfoTable = document.getElementById("clientInfoTable");
	clearTableData(clientInfoTable);
}
function clearNeighborInfoTable() {
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	clearTableData(neighborInfoTable);
}
function clearTableData(table) {
	var rowCount = table.rows.length;
	for ( var i = 1; i < rowCount; i++) {
		table.deleteRow(1);
	}
	for ( var j = 0; j < 12; j++) {
		var row = table.insertRow(1);
		for ( var k = 0; k < table.rows[0].cells.length; k++) {
			var cell = row.insertCell(k);
			cell.className = 'list';
			cell.innerHTML = "&nbsp;";
		}
	}
}
function displayAPInfoPanel(targetId) {
	var leafNodeId = targetId.replace(/n/, "").replace(/l/, "");
	setTriggeredLeafNode(leafNodeId);
	doAjaxRequest("retrieveApInfo", "", apInfo, false);
}

var detailAp_id = null;
var detailAp_domainId = null;
var apInfo = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if (result.v) {
		var apInfoLabel = document.getElementById("apInfoLabel");
		hm.util.reportFieldError(apInfoLabel, result.v);
		if (null != apInfoPanel) {
			apInfoPanel.cfg.setProperty('visible', true);
		}
		return;
	}
	if (result.h) {
		var titleDiv = document.getElementById("apInfoTitle");
		var currentStr = titleDiv.innerHTML;
		if (currentStr.indexOf(' - ') > 0) {
			titleDiv.innerHTML = currentStr.slice(0, currentStr.indexOf(' - '))
					+ ' - ' + result.h;
		} else {
			titleDiv.innerHTML = currentStr + ' - ' + result.h;
		}
	}
	replaceText("apName", result.h);
	replaceText("apIp", result.apIp);
	replaceText("apMac", result.apMac);
	replaceText("apLocation", result.apLocation);
	replaceText("apClientCount", result.apClientCount);
	replaceText("apActiveSsid", result.apActiveSsid);

	var ssidInfoRow = document.getElementById('ssidInfoRow');
	if (ssidInfoRow) {
		// hide the SSIDs information for non-APs
		ssidInfoRow.style.display = result.showSSIDs ? "" : "none";
	}

	detailAp_id = result.id;
	detailAp_domainId = result.domainId;
	if (null != apInfoPanel) {
		apInfoPanel.cfg.setProperty('visible', true);
	}
}
function replaceText(nodeId, text) {
	var td = document.getElementById(nodeId);
	if (td != null) {
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(text));
	}
}
function hiveApDetails() {
	var url = hiveApAction + "?operation=showHiveApDetails&id=" + detailAp_id
			+ "&domainId=" + detailAp_domainId + "&hmListType=managedHiveAps";
	window.location.href = url;
}
function synchronizeSsidInfo() {
	doAjaxRequest("syncSsidInfo", "", updateSsidText, false);
	replaceText("apActiveSsid", "");
}
var updateSsidText = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if (result.apActiveSsid) {
		replaceText("apActiveSsid", result.apActiveSsid);
	}
}

var lldpCdpPanel = null;
function createLldpCdpPanel() {
	var div = window.document.getElementById('lldpCdpPanel');
	lldpCdpPanel = new YAHOO.widget.Panel(div, {
		width : "380px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	lldpCdpPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(lldpCdpPanel);
	lldpCdpPanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function openLldpCdpPanel(text, leafNodeId) {
	selectedLeafNodeId = leafNodeId;

	var div = window.document.getElementById('lldpCdpTitle');
	div.innerHTML = text;

	document.getElementById('chkLldp').checked = true;
	document.getElementById('chkCdp').checked = false;

	hm.util.hideFieldError();
	if (IE_SYNC) {
		// Keep the underlay and iframe size in sync.

		// You could also set the width property, to achieve the
		// same results, if you wanted to keep the panel's internal
		// width property in sync with the DOM width.
		lldpCdpPanel.sizeUnderlay();
		// Syncing the iframe can be expensive. Disable iframe if you
		// don't need it.
		lldpCdpPanel.syncIframe();
	}
	lldpCdpPanel.cfg.setProperty('visible', true);
}
function hideLldpCdpPanel() {
	if (null != lldpCdpPanel) {
		lldpCdpPanel.cfg.setProperty('visible', false);
	}
}
function requestLldpCdpAction() {
	var text = window.document.getElementById('lldpCdpTitle').innerHTML;
	if (text == lldpcdpClearOp) {
		document.forms['lldpCdpForm'].operation.value = "lldpcdpclear";
	}
	document.forms['lldpCdpForm'].leafNodeId.value = selectedLeafNodeId;
	var formObject = document.getElementById('mapNodes');
	YAHOO.util.Connect.setForm(formObject);

	var transaction = YAHOO.util.Connect.asyncRequest('POST', mapNodesAction, {
		success : lldpCdpCliInfoResult,
		failure : lldpCdpConnectedFailed,
		timeout : 100000
	}, null);
	if (waitingPanel != null) {
		waitingPanel.show();
	}

}
var lldpCdpCliInfoResult = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	hideLldpCdpPanel();

	eval("var response = " + o.responseText);
	if (response.result) {
		showSuccessNote(response.rspMessage.valueOf());
	} else {
		showWarnDialog(response.rspMessage.valueOf());
	}
}
var lldpCdpConnectedFailed = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	hideLldpCdpPanel();
}
function clickLldpCdp(text, value) {
	var chkLldp = document.getElementById('chkLldp');
	var chkCdp = document.getElementById('chkCdp');
	if (text == "lldp") {
		if (value == false) {
			if (!chkCdp.checked) {
				chkCdp.checked = true;
			}
		}
	} else if (text == "cdp") {
		if (value == false) {
			if (!chkLldp.checked) {
				chkLldp.checked = true;
			}
		}
	} else {
		if (value == true) {
			document.getElementById(text).readOnly = false;
		} else {
			document.getElementById(text).readOnly = true;
		}
	}
}

// CWP directory removal feature
var cwpRemovalFormName = "cwpRemoval";
function hideCwpRemovePanel() {
	if (cwpRemovePanel != null) {
		cwpRemovePanel.cfg.setProperty('visible', false);
	}
}
function openCwpRemovePanel() {
	if (cwpRemovePanel != null) {
		hm.util.hideFieldError();
		var leafNodeId = document.forms[formName].leafNodeId.value;
		document.forms[cwpRemovalFormName].leafNodeId.value = leafNodeId;
		document.getElementById(cwpRemovalFormName + '_leafNodeId').value = leafNodeId;

		// clear directorys and select the default radio box;
		document.getElementById(cwpRemovalFormName + '_cwpDirectoryremoveAll').checked = true;
		document.getElementById("cwpRemoveTitle").innerHTML = cwpRemoveTtl;
		hideCwpDirectories();

		cwpRemovePanel.cfg.setProperty('visible', true);
	}
}
function hideCwpDirectories() {
	var cwpDirectories = document.getElementById('cwpDirectories');
	cwpDirectories.style.display = 'none';
}
function createCwpDirectories(dirs) {
	var cwpDirectories = document.getElementById('cwpDirectories');
	cwpDirectories.style.display = '';
	var head = "<table border='0' cellspacing='0' cellpadding='2px'>";
	var foot = "</table>";
	var body = "";
	var dirExist = false;
	if (dirs != undefined) {
		body += "<tr><td></td><td class='panelLabel'>Web Page Directories</td><td class='panelLabel'>SSIDs</td></tr>";
		body += "<tr><td>- - -</td><td>- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -</td><td>- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -</td></tr>";
		for ( var dir in dirs) {
			if (!dirs.hasOwnProperty(dir)) {
				continue;
			}
			if (dirs[dir].ssids == undefined) {
				body += "<tr><td><input type='checkbox' name='selectedDirs' value='"
						+ dirs[dir].name
						+ "'/> </td><td>"
						+ dirs[dir].name
						+ " </td></tr>";
			} else {
				body += "<tr><td><input type='checkbox' name='selectedDirs' value='"
						+ dirs[dir].name
						+ "'/> </td><td>"
						+ dirs[dir].name
						+ " </td><td>" + dirs[dir].ssids[0] + "</td></tr>";
				for ( var i = 1; i < dirs[dir].ssids.length; i++) {
					body += "<tr><td></td><td></td><td>" + dirs[dir].ssids[i]
							+ "</td></tr>";
				}
			}
		}
		if (dirs.length > 0) {
			dirExist = true;
		}
	}
	cwpDirectories.innerHTML = dirExist ? ("<fieldset>" + head + body + foot + "</fieldset>")
			: (head + body + foot);
}
function disableDirs() {
	var dirs = document.getElementsByName('selectedDirs');
	for ( var i = 0; i < dirs.length; i++) {
		dirs[i].disabled = true;
	}
}
function retrieveDirs() {
	document.forms[cwpRemovalFormName].operation.value = 'retrieveCwpDirectory';
	var formObject = document.forms[cwpRemovalFormName];
	YAHOO.util.Connect.setForm(formObject);

	var transaction = YAHOO.util.Connect.asyncRequest('POST', mapNodesAction, {
		success : cwpDirectoryResult,
		failure : connectedFailed,
		timeout : 60000
	}, null);
	if (waitingPanel != null) {
		waitingPanel.show();
	}
}
function submitCwpRemovePanel() {
	document.forms[cwpRemovalFormName].operation.value = 'removeCwpDirectory';
	var formObject = document.forms[cwpRemovalFormName];
	YAHOO.util.Connect.setForm(formObject);

	var transaction = YAHOO.util.Connect.asyncRequest('POST', mapNodesAction, {
		success : removeCwpDirectoryResult,
		failure : connectedFailed,
		timeout : 60000
	}, null);
	if (waitingPanel != null) {
		waitingPanel.show();
	}
}
var removeCwpDirectoryResult = function(o) {
	eval("var result = " + o.responseText);
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	if (result.suc) {
		showInfoDialog(cwpRemoveSuccessTtl);
		// set removeAll button selected;
		document.getElementById(cwpRemovalFormName + '_cwpDirectoryremoveAll').checked = true;
		disableDirs();
	}
	if (result.msg) {
		var reportEl = document.getElementById("cwpRemoveLabel");
		hm.util.reportFieldError(reportEl, result.msg);
	}
}
var cwpDirectoryResult = function(o) {
	eval("var result = " + o.responseText);
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	if (result.h) {
		var dialogTitleDiv = document.getElementById("cwpRemoveTitle");
		dialogTitleDiv.innerHTML = cwpRemoveTtl + ' - ' + result.h;
	}
	if (result.msg) {
		var reportEl = document.getElementById("cwpRemoveLabel");
		hm.util.reportFieldError(reportEl, result.msg);
	}
	createCwpDirectories(result.dirs);
}
// end feature

// set image to boot feature
function openImageBootPanel() {
	
	doAjaxRequest("fetchImageVer", hiveApFetchImageVerOp, fetchImageVerResult,true);
}

function fetchImageVerResult(o){
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(null == imageBootPanel){
		createImageBootPanel();
	}
	
	if(null != imageBootPanel){
		var imageBootcurrent_for = document.getElementById('imageBootcurrent').parentElement.childNodes[1];
		var imageBootbackup_for = document.getElementById('imageBootbackup').parentElement.childNodes[1];
		var imageBootPanelMsg = document.getElementById('imageBootPanelMsg');
		var imageBootPanelSubmit = document.getElementById("imageBootPanelSubmit");
		if(result.isConnected){
			imageBootPanelSubmit.disabled = false;
			if(result.v){
				imageBootPanelMsg.innerHTML=result.v;
				imageBootcurrent_for.innerHTML = invokeBackupCurrentText;
				imageBootbackup_for.innerHTML = invokeBackupBackupText;
			} else {
				imageBootPanelMsg.innerHTML="";
				if(result.currentVer && ""!=result.currentVer){
					imageBootcurrent_for.innerHTML =invokeBackupCurrentTexWithVer.replace("|",result.currentVer);
				} else {
					imageBootcurrent_for.innerHTML = invokeBackupCurrentText;
				}
				if(result.backupVer && ""!=result.backupVer){
					imageBootbackup_for.innerHTML = invokeBackupBackupTextWithVer.replace("|",result.backupVer);
				} else {
					imageBootbackup_for.innerHTML = invokeBackupBackupText;
				}
			}
		} else {
			imageBootPanelSubmit.disabled = true;
			if(result.v){
				imageBootPanelMsg.innerHTML=result.v;
				imageBootcurrent_for.innerHTML = invokeBackupCurrentText;
				imageBootbackup_for.innerHTML = invokeBackupBackupText;
			}
		}
		
		document.getElementById('imageBootcurrent').checked = true;
		imageBootPanel.show();
	}
}

function hideImageBootPanel() {
	if (null != imageBootPanel) {
		imageBootPanel.hide();
	}
}
function requestImageBoot() {
	var value = "backup";
	if (document.getElementById('imageBootcurrent').checked) {
		value = "current";
	}
	doAjaxRequest("requestSingleItemCli", hiveApInvokeBackupOp, cliInfoResult,
			true, "value1=" + value);
	hideImageBootPanel();
}
// end feature
var pdfInterval = null;
function createTechData() {
	var layers = hm.map.viewWindow.getLayers();
	if (actualHeight == 0) { // If map has no size, there is no way for user
		// to select layers, so default to signal
		// strength
		layers = 1;
	}
	var url = mapsActionOperationUrl + "createDownloadData&id=" + hm.map.mapId
			+ "&frequency=" + hm.map.viewWindow.getFrequency()
			+ "&rssiThreshold=" + document.forms[formName].rssiThreshold.value
			+ "&rateThreshold=" + hm.map.viewWindow.getRateThreshold()
			+ "&snrThreshold=" + document.forms[formName].snrThreshold.value
			+ "&layers=" + layers + "&apLabels="
			+ hm.map.viewWindow.getApLabels() + "&rapLabels="
			+ hm.map.viewWindow.getRapLabels() + "&gridChecked="
			+ document.forms[formName].grid.checked + "&channelWidth="
			+ document.forms[formName].channelWidth.value + "&pageId="
			+ hm.map.pageId + "&scale=" + hm.map.scale + "&ignore="
			+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : createDataResult
	}, null);
	var interval = 5000;
	clearInterval(pdfInterval);
	pdfInterval = setInterval('getPdfCreateFlag()', interval);
	
	initNoteSection();
}

var getPdfCreateFlag = function() {
	var url = mapsActionOperationUrl + "getPdfCreateFlag" + "&ignore="
			+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : clearPdfInterval
	}, null);
}

var clearPdfInterval = function(o) {
	eval("var data = " + o.responseText);
	if (!data.pdfCreatingFlag) {
		clearInterval(pdfInterval);
		hm.util.hide('processing');
		if(data.pdfCreatSuccessFlag){
			showHangMessage("Report file has been generated. &nbsp;&nbsp;&nbsp;&nbsp;");
			var btn = document.getElementById("downloadBtn");
			if (btn) {
				// clear the file name which is for folder data (.xml|.tar) 
				document.forms['maps'].fileName.value = '';
				
				btn.onclick = function() {
					submitAction('maps', 'download');
				}
			}
			hm.util.show('downloadSection');
		}
	}
}

var createDataResult = function(o) {
	eval("var data = " + o.responseText);
	var isSucc = data.result;
	if (isSucc) {
		/*
		 * showHangMessage("Report file has been generated.
		 * &nbsp;&nbsp;&nbsp;&nbsp;"); var btn =
		 * document.getElementById("downloadBtn"); if (btn) { btn.onclick =
		 * function() { submitAction('maps', 'download'); } }
		 * hm.util.show('downloadSection');
		 */
	} else {
		hm.util.hide('processing');
		showErrorMessage(data.message);
	}
}

function showHangMessage(message) {
	var noteTD = document.getElementById("noteTD");
	YAHOO.util.Dom.removeClass(noteTD, "noteError");
	noteTD.innerHTML = message;
	hm.util.show("noteSection");
}

function showErrorMessage(message) {
	var noteTD = document.getElementById("noteTD");
	YAHOO.util.Dom.addClass(noteTD, "noteError");
	noteTD.innerHTML = message;
	hm.util.show("noteSection");
}

function initNoteSection() {
	hm.util.hide('downloadSection');
	hm.util.hide('noteSection');
}

function showProcessing() {
	hm.util.show('processing');
}

var bigFiles = [];
var pollTimeouted;
function validateFileSize(file) {
	// dynamically set the value before upload
	swfu.setPostParams({
		"operation" : "uploadImage",
		"domainMapId" : document.forms[upload_formName].domainMapId.value
	});
	var maxSize = initMaxImageSize;
	clearTimeout(pollTimeouted);
	if (file.size > maxSize) {
		bigFiles.push(file);
	}
	pollTimeouted = setTimeout("startUploadImage()", 300);
}
function startUploadImage() {
	if (bigFiles.length > 0) {
		hm.util.confirmUploadMapImage(bigFiles);
	} else {
		swfu.startUpload();
	}
}
function handleNo() {
	this.hide();
	confirmCancelHandler();
}
function confirmCancelHandler() {
	if (bigFiles.length > 0) {
		for ( var i = 0; i < bigFiles.length; i++) {
			swfu.cancelUpload(bigFiles[i].id);
		}
		bigFiles = [];
		// upload file that size is small
		swfu.startUpload();
	}
}
function imageUploadSuccess(serverData) {
	eval("var result = " + serverData);
	if (result.uploaded) {
		var existed = false;
		var selector = document.getElementById("mapReviewImage");
		for ( var i = 0; i < selector.options.length; i++) {
			if (result.image == selector.options[i].value) {
				selector.options[i].selected = true;
				existed = true;
				break;
			}
		}
		if (!existed) {
			selector.length = selector.length + 1;
			selector.options[selector.options.length - 1].value = result.image;
			selector.options[selector.options.length - 1].text = result.image;
			selector.options[selector.options.length - 1].selected = true;
		}
		// show uploaded image
		showImage(selector);
		if (result.sucMsg) {
			showSuccessNote(result.sucMsg);
		}
	} else if (result.error) {
		showInfoDialog(result.error);
	}
}
function imageUploadFailed() {
	document.getElementById("traditionalTr1").style.display = "";
}
function traditionalUpoad() {
	if (!validateUploadImage()) {
		return;
	}
	document.forms[upload_formName].operation.value = "uploadImage";
	YAHOO.util.Connect.setForm(upload_formName, true, true);
	var transaction = YAHOO.util.Connect.asyncRequest("post",
			mapSettingsBaseUrl, {
				upload : traditionalUpoadSuccess
			}, null);
}
function traditionalUpoadSuccess(o) {
	imageUploadSuccess(o.responseText);
}
function showTraditionalContent() {
	document.getElementById("traditionalTr1").style.display = "none";
	document.getElementById("traditionalTr2").style.display = "";
}
function hideTraditionalContent() {
	document.getElementById("traditionalTr1").style.display = "";
	document.getElementById("traditionalTr2").style.display = "none";
}

var sshTunnelPanel = null;
var sshTunnelIframeWindow;
function createSshTunnelPanel(width, height) {
	var div = document.getElementById("sshTunnelPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("ssh_tunnel");
	iframe.width = width;
	iframe.height = height;
	sshTunnelPanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : true,
		visible : false,
		constraintoviewport : true
	});
	sshTunnelPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(sshTunnelPanel);
	sshTunnelPanel.beforeHideEvent.subscribe(clearSshTunnelData);
	sshTunnelPanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function clearSshTunnelData() {
	sshTunnelIframeWindow.onHidePage();
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("ssh_tunnel").style.display = "none";
	}
}
function openSshTunnelPanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null == sshTunnelPanel) {
		createSshTunnelPanel(600, 500);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("ssh_tunnel").style.display = "";
	}
	sshTunnelPanel.show();
	var iframe = document.getElementById("ssh_tunnel");
	iframe.src = toolkitAction + "?operation=initSshTunnelPanel&leafNodeId="
			+ leafNodeId;
}

var packetCapturePanel = null;
var packetCaptureIframeWindow;
function createPacketCapturePanel(width, height) {
	var div = document.getElementById("packetCapturePanel");
	width = width || 600;
	height = height || 600;
	var iframe = document.getElementById("packetCaptureFrame");
	iframe.width = width;
	iframe.height = height;
	packetCapturePanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : "contained",
		visible : false,
		constraintoviewport : true
	});
	packetCapturePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(packetCapturePanel);
	packetCapturePanel.beforeHideEvent.subscribe(clearPacketCaptureData);
	packetCapturePanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function clearPacketCaptureData() {
	packetCaptureIframeWindow
			.clearTimeout(packetCaptureIframeWindow.pollWifi0TimeoutId);
	packetCaptureIframeWindow
			.clearTimeout(packetCaptureIframeWindow.pollWifi1TimeoutId);
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("packetCaptureFrame").style.display = "none";
	}
}
function openPacketCapturePanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;

	var viewportWidth = YAHOO.util.Dom.getViewportWidth() * 0.8;
	var viewportHeight = YAHOO.util.Dom.getViewportHeight() * 0.8;
	if (viewportWidth >= 600) {
		viewportWidth = 600;
	}
	if (viewportHeight >= 600) {
		viewportHeight = 600;
	}

	if (null == packetCapturePanel) {
		createPacketCapturePanel(viewportWidth, viewportHeight);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("packetCaptureFrame").style.display = "";
	}
	packetCapturePanel.show();
	var iframe = document.getElementById("packetCaptureFrame");
	iframe.src = toolkitAction + "?operation=initPacketCapture&leafNodeId="
			+ leafNodeId;
}

var remoteSnifferPanel = null;
var remoteSnifferIFrameWindow;
function createRemoteSnifferPanel(width, height) {
	var div = document.getElementById("remoteSnifferPanel");
	var iframe = document.getElementById("remoteSnifferFrame");
	iframe.width = width;
	iframe.height = height;
	remoteSnifferPanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : "contained",
		visible : false,
		constraintoviewport : true
	});
	remoteSnifferPanel.render();
	div.style.display = "";
	overlayManager.register(remoteSnifferPanel);
	remoteSnifferPanel.beforeHideEvent.subscribe(closeRemoteSnifferPanel);
	remoteSnifferPanel.beforeShowEvent.subscribe(function() {
		overlayManager.bringToTop(this)
	});
}
function openRemoteSnifferPanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null == remoteSnifferPanel) {
		createRemoteSnifferPanel(540, 260);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("remoteSnifferFrame").style.display = "";
	}

	remoteSnifferPanel.show();
	var iframe = document.getElementById("remoteSnifferFrame");
	iframe.src = toolkitAction + "?operation=initRemoteSniffer&leafNodeId="
			+ leafNodeId;
}
function closeRemoteSnifferPanel() {
	remoteSnifferIFrameWindow.onHidePage();
	remoteSnifferPanel.hide();
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("remoteSnifferFrame").style.display = "none";
	}
}

var debugClientPanel = null;
var clientTraceIframeWindow;
function createDebugClientPanel(width, height) {
	var div = document.getElementById("debugClientPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("debug_client");
	iframe.width = width;
	iframe.height = height;
	debugClientPanel = new YAHOO.widget.Panel(div, {
		fixedcenter : true,
		width : (width + 20) + "px",
		visible : false,
		constraintoviewport : true
	});
	debugClientPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(debugClientPanel);
	debugClientPanel.beforeHideEvent.subscribe(clearDebugClientData);
	debugClientPanel.beforeShowEvent.subscribe(bringPanelToTop);
	// createDebugClientResizer();
}
function clearDebugClientData() {
	clientTraceIframeWindow.onUnloadPage();
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("debug_client").style.display = "none";
	}
}
function openClientTracePanel(mapNodeId, leafNodeId) {
	if (null == debugClientPanel) {
		var viewportWidth = YAHOO.util.Dom.getViewportWidth() * 0.8;
		var viewportHeight = YAHOO.util.Dom.getViewportHeight() * 0.8;
		if (viewportWidth >= 835) {
			viewportWidth = 835;
		}
		if (viewportHeight >= 560) {
			viewportHeight = 560;
		}
		createDebugClientPanel(viewportWidth, viewportHeight);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("debug_client").style.display = "";
	}
	debugClientPanel.show();
	var iframe = document.getElementById("debug_client");
	if (mapNodeId) {
		iframe.src = toolkitAction
				+ "?operation=initDebugClientFromTopoFloor&mapNodeId="
				+ mapNodeId;
	} else {
		iframe.src = toolkitAction
				+ "?operation=initDebugClientFromTopoFloor&leafNodeId="
				+ leafNodeId;
	}
}

var vlanProbePanel = null;
var vlanProbeIframeWindow;
function createVlanProbePanel(width, height) {
	var div = document.getElementById("vlanProbePanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vlan_probe");
	iframe.width = width;
	iframe.height = height;
	vlanProbePanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : true,
		visible : false,
		constraintoviewport : true
	});
	vlanProbePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(vlanProbePanel);
	vlanProbePanel.beforeHideEvent.subscribe(clearVlanProbeData);
	vlanProbePanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function clearVlanProbeData() {
	vlanProbeIframeWindow.onHidePage();
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("vlan_probe").style.display = "none";
	}
}
function openVlanProbePanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null == vlanProbePanel) {
		createVlanProbePanel(500, 400);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("vlan_probe").style.display = "";
	}
	vlanProbePanel.show();
	var iframe = document.getElementById("vlan_probe");
	iframe.src = toolkitAction + "?operation=initVlanProbePanel&leafNodeId="
			+ leafNodeId;
}

var pathProbePanel = null;
var pathProbeIframeWindow;
function createPathProbePanel(width, height) {
	var div = document.getElementById("pathProbePanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("path_probe");
	iframe.width = width;
	iframe.height = height;
	pathProbePanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : true,
		visible : false,
		constraintoviewport : true
	});
	pathProbePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(pathProbePanel);
	pathProbePanel.beforeHideEvent.subscribe(clearPathProbeData);
	pathProbePanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function clearPathProbeData() {
	pathProbeIframeWindow.onHidePage();
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("path_probe").style.display = "none";
	}
}
function openPathProbePanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null == pathProbePanel) {
		createPathProbePanel(500, 480);
	}
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("path_probe").style.display = "";
	}
	pathProbePanel.show();
	var iframe = document.getElementById("path_probe");
	iframe.src = toolkitAction + "?operation=initPathProbePanel&leafNodeId="
			+ leafNodeId;
}

var multicastMonitorPanel = null;
function createMulticastMonitorPanel(width, height) {
	var div = document.getElementById("multicastMonitorPanel");
	var iframe = document.getElementById("multicastMonitorFrame");
	iframe.width = width;
	iframe.height = height;
	multicastMonitorPanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : "contained",
		visible : false,
		constraintoviewport : true
	});
	multicastMonitorPanel.render();
	div.style.display = "";
	overlayManager.register(multicastMonitorPanel);
	multicastMonitorPanel.beforeHideEvent.subscribe(closeMulticastMonitorPanel);
	multicastMonitorPanel.beforeShowEvent.subscribe(function() {
		overlayManager.bringToTop(this)
	});
}

function openMulticastMonitorPanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null == multicastMonitorPanel) {
		createMulticastMonitorPanel(540, 260);
	}

	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("multicastMonitorFrame").style.display = "";
	}

	multicastMonitorPanel.show();
	var iframe = document.getElementById("multicastMonitorFrame");
	iframe.src = mapNodesAction + "?operation=initmulticastMonitor&leafNodeId="
			+ leafNodeId;
}
var multicastMonitorIFrameWindow;
function closeMulticastMonitorPanel() {
	multicastMonitorIFrameWindow.onHidePage();

	multicastMonitorPanel.hide();

	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("multicastMonitorFrame").style.display = "none";
	}
}

var fwPolicyRulePanel = null;
function openFirewallPolicyPanel(leafNodeId) {
	if (fwPolicyRulePanel == null) {
		createFwPolicyRulePanel();
	}
	showFwPolicyRulePanel(leafNodeId);
}

function createFwPolicyRulePanel(width, height) {
	var div = document.getElementById("fwPolicyRulePanel");
	width = width || 400;
	height = height || 150;
	var iframe = document.getElementById("fwPolicyRuleFrame");
	iframe.width = width;
	iframe.height = height;
	fwPolicyRulePanel = new YAHOO.widget.Panel(div, {
		width : (width + 20) + "px",
		fixedcenter : true,
		visible : false,
		constraintoviewport : true
	});
	fwPolicyRulePanel.render();
	div.style.display = "";
	overlayManager.register(fwPolicyRulePanel);
	fwPolicyRulePanel.beforeHideEvent.subscribe(clearFwPolicyRuleData);
	fwPolicyRulePanel.beforeShowEvent.subscribe(function() {
		overlayManager.bringToTop(this)
	});
}

function showFwPolicyRulePanel(leafNodeId) {
	leafNodeId = leafNodeId || 0;
	if (null != fwPolicyRulePanel) {
		if (YAHOO.env.ua.ie) {
			document.getElementById("fwPolicyRuleFrame").style.display = "";
		}
		fwPolicyRulePanel.show();
	}
	var iframe = document.getElementById("fwPolicyRuleFrame");
	iframe.src = toolkitAction + "?operation=initFwPolicyRulePanel&leafNodeId="
			+ leafNodeId;
}

function hideFwPolicyRulePanel() {
	if (null != fwPolicyRulePanel) {
		fwPolicyRulePanel.hide();
	}
}

function clearFwPolicyRuleData() {
	// fix bug 16599
	// fwPolicyRulePanel.onHidePage();

	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if (YAHOO.env.ua.ie) {
		document.getElementById("fwPolicyRuleFrame").style.display = "none";
	}
}

function requestMultipleItemCli(menuText) {
	doAjaxRequest("requestMultipleItemCli", menuText, cliInfoResult, true);
}

// locate ap feature
var locateAPPanel = null;
function createLocateAPPanel() {
	var div = window.document.getElementById('locateAPPanel');
	locateAPPanel = new YAHOO.widget.Panel(div, {
		width : "400px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true
	});
	locateAPPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(locateAPPanel);
	locateAPPanel.beforeShowEvent.subscribe(function() {
		overlayManager.bringToTop(this)
	});
}

function openLocateAPPanel() {
	doAjaxRequest("requestHiveAPModel", "", requestHiveAPModelResult, true);
}

function requestHiveAPModelResult(o) {
	eval("var result = " + o.responseText);
	if (waitingPanel != null) {
		waitingPanel.hide();
	}

	if (result.model) {
		if (null == locateAPPanel) {
			createLocateAPPanel();
		}

		// hide some color
		// var model = result.model;
		// var modelStr = result.modelStr;
		if (SUPPORTED_LOCATE.contains(result.model)) {
			var selectList = document.getElementById("locateAPColorMore");
			var green = document.createElement("option");
			green.text = "Green";
			green.value = "green";
			green.style.backgroundColor = "#00FF00"

			var red = document.createElement("option");
			red.text = "Red";
			red.value = "red";
			red.style.backgroundColor = "#FF0000"

			var yellow = document.createElement("option");
			yellow.text = "Yellow";
			yellow.value = "yellow";
			yellow.style.backgroundColor = "#FFFF00";

			var blue = document.createElement("option");
			blue.text = "Blue";
			blue.value = "blue";
			blue.style.backgroundColor = "#0000FF";

			var white = document.createElement("option");
			white.text = "White";
			white.value = "white";
			white.style.backgroundColor = "#FFFFFF";

			var orange = document.createElement("option");
			orange.text = "Orange";
			orange.value = "orange";
			orange.style.backgroundColor = "#FF7F00";

			var purple = document.createElement("option");
			purple.text = "Purple";
			purple.value = "purple";
			purple.style.backgroundColor = "#9F00C5";

			var amber = document.createElement("option");
			amber.text = "Amber";
			amber.value = "amber";
			amber.style.backgroundColor = "#FFBF00"

			var off = document.createElement("option");
			off.text = "Off";
			off.value = "off";
			off.style.backgroundColor = "#808080";

			switch (result.model) {

			case MODEL_121:
			case MODEL_141:
			case MODEL_230:
			case MODEL_370:
			case MODEL_390:
			case MODEL_BR100:
			case MODEL_BR200:
			case MODEL_BR200_WP:
			case MODEL_BR200_LTE_VZ: {
				removeAllOptions(selectList);
				try {
					selectList.add(amber, null);
					selectList.add(white, null);
					selectList.add(off, null);
				} catch (e) {
					selectList.add(amber);
					selectList.add(white);
					selectList.add(off);
				}
				break;
			}

			case MODEL_320:
			case MODEL_340: {

				removeAllOptions(selectList);
				try {
					selectList.add(green, null);
					selectList.add(red, null);
					selectList.add(orange, null);
					selectList.add(off, null);
				} catch (e) {
					selectList.add(green);
					selectList.add(red);
					selectList.add(orange);
					selectList.add(off);
				}
				break;
			}

			case MODEL_110:
			case MODEL_120:
			case MODEL_330:
			case MODEL_350: {
				removeAllOptions(selectList);
				try {
					selectList.add(green, null);
					selectList.add(orange, null);
					selectList.add(yellow, null);
					selectList.add(blue, null);
					selectList.add(purple, null);
					selectList.add(white, null);
					selectList.add(off, null);
				} catch (e) {
					selectList.add(green);
					selectList.add(orange);
					selectList.add(yellow);
					selectList.add(blue);
					selectList.add(purple);
					selectList.add(white);
					selectList.add(off);
				}
				break;
			}

			default:
				removeAllOptions(selectList);
				try {
					selectList.add(green, null);
					selectList.add(red, null);
					selectList.add(yellow, null);
					selectList.add(blue, null);
					selectList.add(purple, null);
					selectList.add(white, null);
					selectList.add(off, null);
				} catch (e) {
					selectList.add(green);
					selectList.add(red);
					selectList.add(yellow);
					selectList.add(blue);
					selectList.add(purple);
					selectList.add(white);
					selectList.add(off);
				}
				break;
			}

			// show or hide
			document.getElementById('noLedSection').style.display = "";
			document.getElementById('locateAPColorMore').style.display = "";
			document.getElementById('locateAPColorLess').style.display = "none";

			// init value
			document.getElementById('noLed').checked = false;
			selectNoLed(false);
			// document.getElementById('locateAPColorMore').options[0].selected=true;
			document.getElementById('locateAPBlink').options[0].selected = true;

			locateAPColorMoreChange();
			locateAPColorLessChange();
		} else {
			// in 3.4r3, locate hiveap only avaliable in ap 100 series.
			warnDialog.cfg.setProperty('text',
					"Locate Device is unavailable on the " + result.modelStr);
			warnDialog.show();
			return;

			/**
			 * // show or hide
			 * document.getElementById('noLedSection').style.display="none";
			 * document.getElementById('locateAPColorMore').style.display="none";
			 * document.getElementById('locateAPColorLess').style.display=""; //
			 * init value
			 * document.getElementById('locateAPColorLess').options[0].selected=true;
			 * document.getElementById('locateAPBlink').options[0].selected=true;
			 */
		}

		if (null != locateAPPanel) {
			locateAPPanel.show();
		}
	} else if (result.e) {
		warnDialog.cfg.setProperty('text', result.e);
		warnDialog.show();
	} else {
		warnDialog.cfg.setProperty('text',
				"Unable to determine the device model.");
		warnDialog.show();
	}
}
function removeAllOptions(data) {
	for ( var i = 0; i < data.options.length;) {
		data.remove(data.options[i]);
	}
}
function hideLocateAPPanel() {
	if (null != locateAPPanel) {
		locateAPPanel.hide();
	}
}

function selectNoLed(checked) {
	if (checked) {
		document.getElementById('locateAPColorMore').disabled = true;
		document.getElementById('locateAPBlink').disabled = true;
	} else {
		document.getElementById('locateAPColorMore').disabled = false;
		if (document.getElementById('locateAPColorMore').value != "off") {
			document.getElementById('locateAPBlink').disabled = false;
		}
	}
}

function submitLocateAP() {

	var color;
	var blink;
	var noled = document.getElementById('noLed').checked;
	if (!noled) {
		if (document.getElementById('locateAPColorMore').style.display != "none") {
			color = document.getElementById('locateAPColorMore').value;
		} else {
			color = document.getElementById('locateAPColorLess').value;
		}
		blink = document.getElementById('locateAPBlink').value;
	}

	doAjaxRequest("controlLedOfAP", "", controlLedResult, true, "noLed=" + noled
			+ "&ledColor=" + color + "&ledBlink=" + blink);
}

function controlLedResult(o) {
	eval("var result = " + o.responseText);
	if (waitingPanel != null) {
		waitingPanel.hide();
	}

	if (result.msg) {
		warnDialog.cfg.setProperty('text', result.msg);
		warnDialog.show();
	} else {
		showInfoDialog("The LED status was updated successfully.");
	}
}

function locateAPColorMoreChange() {
	var color = document.getElementById("locateAPColorMore");
	var colorValue = color.value;
	if (colorValue == "green") {
		color.style.background = "#00FF00";
	} else if (colorValue == "red") {
		color.style.background = "#FF0000";
	} else if (colorValue == "yellow") {
		color.style.background = "#FFFF00";
	} else if (colorValue == "blue") {
		color.style.background = "#0000FF";
	} else if (colorValue == "purple") {
		color.style.background = "#9F00C5";
	} else if (colorValue == "white") {
		color.style.background = "#FFFFFF";
	} else if (colorValue == "amber") {
		color.style.background = "#FFBF00";
	} else if (colorValue == "orange") {
		color.style.background = "#FF7F00";
	} else {
		color.style.background = "#808080";
	}

	if (colorValue == "off") {
		document.getElementById("locateAPBlink").disabled = true;
	} else {
		document.getElementById("locateAPBlink").disabled = false;
	}
}

function locateAPColorLessChange() {
	var color = document.getElementById("locateAPColorLess");
	if (color.value == "green") {
		color.style.background = "#00FF00";
	} else if (color.value == "red") {
		color.style.background = "#FF0000";
	} else if (color.value == "orange") {
		color.style.background = "#FF6000";
	} else {
		color.style.background = "#FFFFFF";
	}
}

// locate ap feature---end

function clearRadsecCredentials(menuText) {
	thisOperation = menuText;
	confirmDialog.cfg.setProperty('text', hiveApClearCredentialsMsg);
	confirmDialog.show();
	//doAjaxRequest("clearRadsecCerts", menuText, cliInfoResult, true);
}

// j_context

function zoomIt() {
	hm.map.clearAlarmsTimer();
	invalidateHiddenMaps();
	if (!hm.map.planningMode()) {
		uncheckAllCBs(false);
	}
	var zoomFactor = document.forms[formName].zoom.value;
	var oldScale = hm.map.scale;
	hm.map.setCanvasSize(this, zoomFactor);
	scaleDelta = hm.map.scale / oldScale;
	cancelSizeMap();
	cancelMeasuring();
	viewport.location.reload();
}

function uncheckAllCBs(all) {
	if (document.forms[formName].rates.checked) {
		hm.util.toggleHideElement("interferenceLegendDiv", true);
		hm.util.toggleHideElement("coverageLegendDiv", false);
		hm.util.toggleHideElement("snrLegendDiv", true);
		hm.util.toggleHideElement("ratesLegendDiv", true);
	}
	document.forms[formName].rates.checked = false;
	if (all) {
		document.forms[formName].heat.checked = false;
		document.forms[formName].channel.checked = false;
		document.forms[formName].interference.checked = false;
		document.forms[formName].clients.checked = false;
		document.forms[formName].rogues.checked = false;
	}
}

function enableHeatChannelBoxes() {
	toggleUnsizedItems(actualHeight > 0);
	var pm = hm.map.planningMode();
	createMapTabs(actualHeight > 0, pm);
	if (actualHeight > 0) {
		document.forms[formName].heat.disabled = false;
		document.forms[formName].channel.disabled = false;
		toggleCreateWall(true);
		toggleRemoveAps();
		document.forms[formName].clients.disabled = pm;
		document.forms[formName].rogues.disabled = pm;
		document.forms[formName].interference.disabled = false;
		var td = document.getElementById("intSnrLabel");
		if (td) {
			if (pm) {
				hm.util.replaceChildren(td, document.createTextNode("SNR"));
			} else {
				hm.util.replaceChildren(td, document
						.createTextNode("Interference"));
			}
		}
		document.forms[formName].rates.disabled = !pm;
		hm.util.toggleHideElement("actualDim3", pm);
		hm.util.toggleHideElement("actualDim4", !pm);
		disableShowFunction(pm);
		if (pm) {
			hm.map.toggleLockMapNodes(true);
			document.forms[formName].autoRssiThreshold.selectedIndex = document.forms[formName].rssiThreshold.selectedIndex;
			if (selectedLayers == 1) {
				document.forms[formName].heat.checked = true;
			} else if (selectedLayers == 2) {
				document.forms[formName].channel.checked = true;
			} else if (selectedLayers == 4) {
				document.forms[formName].interference.checked = true;
				hm.util.toggleHideElement("coverageLegendDiv", true);
				hm.util.toggleHideElement("interferenceLegendDiv", true);
				hm.util.toggleHideElement("snrLegendDiv", false);
			} else if (selectedLayers == 8) {
				document.forms[formName].rates.checked = true;
				hm.util.toggleHideElement("coverageLegendDiv", true);
				hm.util.toggleHideElement("ratesLegendDiv", false);
			}
			showFadeMargin();
			adjustTargetApp();
		} else if (getRapLabels() != "hn") {
			hm.map.changeRapNodeLabels();
		}
		updateRateThreshold();
		doChangeRssiThreshold();
		
		hm.map.hideRadioOptions();
	} else {
		hm.map.hideDistanceTool();
		document.forms[formName].measuringTool.checked = false;
	}
	if (moveUpItem == null) {
		var mi = 2;
		moveUpItem = mapOpsMenu.getItem(mi);
		// For folder nodes
		if (moveUpItem.cfg.getProperty("text") == 'Edit') {
			mi++;
		}
		moveUpItem = mapOpsMenu.removeItem(mi);
		moveDnItem = mapOpsMenu.removeItem(mi);
	}
	if (moveItem == null) {
		var mi = 2;
		moveItem = mapOpsMenu.getItem(mi);
		// For folder nodes
		if (moveItem.cfg.getProperty("text") != 'Move to Folder') {
			mi++;
		}
		moveItem = mapOpsMenu.removeItem(mi);
	}
	if (cloneItem == null) {
		// clone not supported in folder nodes
		cloneItem = mapOpsMenu.removeItem(2);
	}
	moveUpItem.cfg.setProperty("disabled", selectedNode.parent.isRoot());
	moveDnItem.cfg.setProperty("disabled", selectedNode.parent.isRoot());
	moveItem.cfg.setProperty("disabled", selectedNode.parent.isRoot());
	rmvItem.cfg.setProperty("disabled", selectedNode.parent.isRoot());
	if (selectedNode.data.tp == 1) {
		if (addBldItem != null) {
			mapOpsMenu.insertItem(addBldItem, 1);
			addBldItem = null;
		}
		mapOpsMenu.getItem(0).cfg.setProperty("text", addMapOp);
		mapOpsMenu.insertItem(moveItem, 3);
		moveItem = null;
		var mi = 3;
	} else {
		if (addBldItem == null) {
			addBldItem = mapOpsMenu.removeItem(1);
		}
		mapOpsMenu.insertItem(cloneItem, 2);
		cloneItem = null;
		var addFloor = addFloorOp;
		if (selectedNode.data.tp == 3) {
			addFloor += ' Below';
		} else {
			mapOpsMenu.insertItem(moveItem, 2);
			moveItem = null;
		}
		var mi = 2;
		mapOpsMenu.getItem(0).cfg.setProperty("text", addFloor);
	}
	if (selectedNode.data.tp == 3 || !sortFolders) {
		moveUpItem.cfg.setProperty("disabled",
				selectedNode.previousSibling == null);
		moveDnItem.cfg
				.setProperty("disabled", selectedNode.nextSibling == null);
		mapOpsMenu.insertItem(moveDnItem, mi);
		moveDnItem = null;
		mapOpsMenu.insertItem(moveUpItem, mi);
		moveUpItem = null;
	}

	// disable import Planning data for the floor node
	mapOpsMenu.getItem(2, planningOnly ? 1 : 2).cfg.setProperty("disabled",
			selectedNode.data.tp == 3);

	document.forms[formName].nodesLocked.disabled = false;
	document.forms[formName].mapOps.disabled = false;
	document.forms[formName].measuringTool.disabled = false;
	document.forms[formName].sizingTool.disabled = false;
	// disable buttons for read permission only users
	if (selectedNode.data.uiz || !hm.map.mapWritePermission) {
		document.forms[formName].nodesLocked.disabled = true;
		document.forms[formName].mapOps.disabled = true;
		document.forms[formName].measuringTool.disabled = true;
		document.forms[formName].sizingTool.disabled = true;
		disableReadOnlyGroups(true);
	} else if (hm.map.bldMode() || hm.map.mapWidth == 0) {
		disableShowFunction(true);
	} else if (!leafMapContainer) {
		// Usually there will not be any real APs at this level, but if there
		// are, they will be
		// unlocked, just like the container nodes at this level.
		document.forms[formName].nodesLocked.checked = false;
		hm.map.toggleLockMapNodes(false);
		disableShowFunction(true);
	}
	var img = hm.map.canvasWindow.document.getElementById("imageId");
	if (img) {
		img.src = canvasBackground;
	}
	// show Resize pannel
	if (selectedNode != null && !selectedNode.data.uiz
			&& hm.map.mapWritePermission && leafMapContainer
			&& actualHeight <= 0 && selectedNode.data.tp == 3
			&& hm.map.mapWidth > 0) {
		showSizingTool();
	}
	window.onresize = mapResizeHandler;
}

var mapOpsMenu, moveItem, cloneItem, moveUpItem, moveDnItem, rmvItem;
function createMapOpsMenu() {
	if(document.getElementById('mapOps')) {
		mapOpsMenu = new YAHOO.widget.Menu("mapopsmenu", {
			xy : [ 100, 100 ]
		});
		addMapOpsMenuItems();
		moveItem = mapOpsMenu.removeItem(3);
		cloneItem = mapOpsMenu.removeItem(3);
		moveUpItem = mapOpsMenu.removeItem(3);
		moveDnItem = mapOpsMenu.removeItem(3);
		rmvItem = mapOpsMenu.getItem(3);
		mapOpsMenu.subscribe('click', onMapOpsItemClick);
		mapOpsMenu.render("mapOps");
	}
}

function mapResizeHandler() {
	if (YAHOO.env.ua.ie > 0 || true) {
		document.forms[formName].mapOps.disabled = true;
		refreshMap();
	} else {
		hm.map.loadMap(hm.map.mapId);
	}
}

var addBldItem = null;
var mapSizePanel = null;
var cross1dd = null;
var cross2dd = null;
var useWidth = true;
var movedNodeId = null;

function showSizingTool() {
	if (mapSizePanel == null) {
		return;
	}
	cancelMeasuring();
	hm.map.showCrossHairs();
	hm.map.showMapSizePanel('mapOpsButton', 20);
	document.getElementById("sizingTool").checked = true;
	// show prompt message
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document.createTextNode(mapWidthSizeText));
	hm.util.show('processing');
}

function switchSizeWidth() {
	if (!useWidth) {
		useWidth = true;
		hm.map.showCrossHairs();
	}
}
function switchSizeHeight() {
	if (useWidth) {
		useWidth = false;
		hm.map.showCrossHairs();
	}
}
function updateSizeMap(baseUrl) {
	var actualMapWidth = document.getElementById('mapSizeWidth');
	var message = hm.util.validateNumberRange(actualMapWidth.value,
			useWidth ? 'Width' : 'Height', 0, 100000000, true);
	if (message != null) {
		hm.util.reportFieldError(actualMapWidth, message);
		actualMapWidth.focus();
		return;
	}
	if (useWidth) {
		var deltaX = Math.abs(YAHOO.util.Dom.getX(cross1dd.getDragEl())
				- YAHOO.util.Dom.getX(cross2dd.getDragEl()));
		var mapWidth = deltaX / hm.map.scale;
	} else {
		var deltaY = Math.abs(YAHOO.util.Dom.getY(cross1dd.getDragEl())
				- YAHOO.util.Dom.getY(cross2dd.getDragEl()));
		var mapWidth = deltaY / hm.map.scale;
	}
	if (mapWidth == 0) {
		return;
	}
	var actualMapWidth = document.getElementById('mapSizeWidth');
	if (hm.util.trim(actualMapWidth.value).length == 0) {
		return;
	}
	document.forms[formName].mapOps.disabled = true;
	var select = top.document.getElementById('mapSizeUnit');
	var url = baseUrl + hm.map.mapId + "&useWidth=" + useWidth
			+ "&mapWidthUnit=" + select.value + "&mapWidth=" + mapWidth
			+ "&actualMapWidth=" + actualMapWidth.value + "&ignore="
			+ new Date().getTime();
	actualMapWidth.value = '';
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : mapResized
	}, null);
}
function mapResized(o) {
	eval("var result = " + o.responseText);
	if (result) {
		hm.map.loadMap(hm.map.mapId);
	}
}
function cancelSizeMap() {
	if (mapSizePanel == null) {
		return;
	}
	mapSizePanel.cfg.setProperty('visible', false);
	document.getElementById("sizingTool").checked = false;
}
function closeSizeMap() {
	hideProcessing();
	cross1dd.getDragEl().style.display = "none";
	cross2dd.getDragEl().style.display = "none";
	document.getElementById("sizingTool").checked = false;
}
function createMapSizePanel() {
	var div = document.getElementById('mapSizePanel');
	mapSizePanel = new YAHOO.widget.Panel(div, {
		width : "310px",
		visible : false,
		draggable : true,
		constraintoviewport : true,
		close : true
	});
	mapSizePanel.render(document.body);
	div.style.display = "";
	mapSizePanel.beforeHideEvent.subscribe(closeSizeMap);
}

var treeWidthPanel = null;
function closeTreeWidth() {
	treeWidthPanel.cfg.setProperty('visible', false);
}
function createTreeWidthPanel(tw) {
	var div = document.getElementById('treeWidthPanel');
	treeWidthPanel = new YAHOO.widget.Panel(div, {
		width : "250px",
		visible : false,
		draggable : true,
		constraintoviewport : true,
		close : true
	});
	treeWidthPanel.render(document.body);
	div.style.display = "";
	var text = document.getElementById("treeWidth");
	text.value = tw.length == 0 ? 180 : tw;
}
function updateTreeWidth() {
	document.forms[formName].mapOps.disabled = true;
	var widthEl = document.getElementById("treeWidth");

	if (isNaN(widthEl.value)) {
		hm.util.reportFieldError(widthEl, 'Width is invalid.');
		widthEl.focus();
		return;
	}

	var width = Math.round(widthEl.value);
	var vw = YAHOO.util.Dom.getViewportWidth();
	if (width + 150 > vw) {
		width = vw - 150;
	}

	var message = hm.util.validateNumberRange(widthEl.value, 'Width', 150, 500);
	if (message != null) {
		hm.util.reportFieldError(widthEl, message);
		widthEl.focus();
		return false;
	}

	if (width < 150) {
		width = 150;
	} else if (width > 500) {
		width = 500;
	}
	closeTreeWidth();
	var url = "mapBld.action?operation=updateTreeWidth&treeWidth=" + width
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : reloadTopo
	});
}

var simApDetailsPanel = null;
function createSimApDetailsPanel() {
	var div = document.getElementById('simApDetailsPanel');
	simApDetailsPanel = new YAHOO.widget.Panel(div, {
		width : "310px",
		visible : false,
		fixedcenter : true,
		draggable : true,
		constraintoviewport : true,
		close : true
	// font on IE is not crisp when using FADE
	// ,effect:{
	// effect:YAHOO.widget.ContainerEffect.FADE,
	// duration:0.25
	// }
	});
	simApDetailsPanel.render(document.body);
	div.style.display = "";
}
function processSimApMenuItem(text, targetId) {
	if (text == "Remove") {
		hm.map.removeSimAp(targetId);
	} else if (text == "Edit") {
		var wlanNode = hm.map.findSimAp(targetId);
		if (!wlanNode) {
			return;
		}
		var input = document.getElementById('simApId');
		input.value = targetId;
		input = document.getElementById('simApName');
		input.value = wlanNode.jsonNode.hostName;
		var select = document.getElementById('simApModel');
		hm.map.setSelectedIndex(select, wlanNode.jsonNode.md);
		select = document.getElementById('wifi0Channel');
		var auto = wlanNode.jsonNode.ch1 < 0;
		hm.map.setSelectedIndex(select, auto ? 0 : wlanNode.jsonNode.ch1);
		if (auto) {
			hm.util.replaceChildren(
					document.getElementById('autoWifi0Channel'), document
							.createTextNode('(' + (-wlanNode.jsonNode.ch1)
									+ ')'));
		}
		hm.util.toggleHideElement('autoWifi0Channel', !auto
				|| !wlanNode.jsonNode.w0Ebd);
		select = document.getElementById('wifi0Power');
		select.selectedIndex = wlanNode.jsonNode.pwr1 - 1;

		getApModelChannels(wlanNode.jsonNode.md);
		auto = wlanNode.jsonNode.ch2 < 0;
		hm.map.setSelectedIndex(select, auto ? 0 : wlanNode.jsonNode.ch2);
		if (auto) {
			hm.util.replaceChildren(
					document.getElementById('autoWifi1Channel'), document
							.createTextNode('(' + (-wlanNode.jsonNode.ch2)
									+ ')'));
		}
		hm.util.toggleHideElement('autoWifi1Channel', !auto
				|| !wlanNode.jsonNode.w1Ebd);
		select = document.getElementById('wifi1Power');
		select.selectedIndex = wlanNode.jsonNode.pwr2 - 1;

		// select correct radio value
		var radioEl = document.getElementById("simApRadio");
		if (!wlanNode.jsonNode.w0Ebd && wlanNode.jsonNode.w1Ebd) {
			radioEl.value = RADIO_5GHZ;
		} else if (wlanNode.jsonNode.w0Ebd && !wlanNode.jsonNode.w1Ebd) {
			radioEl.value = RADIO_24GHZ;
		}
		updateDetailPanelItem(false);

		simApDetailsPanel.cfg.setProperty('visible', true);
	}
}
function getApModelChannels(model) {
	document.getElementById('simApUpdBtn').disabled = true;
	var url = "mapBld.action?operation=apChannels&id=" + hm.map.mapId
			+ "&treeWidth=" + model + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : processApModelChannels
	}, null);
}
function processApModelChannels(o) {
	eval("var data = " + o.responseText);
	if (data) {
		updateApModelChannels(document.getElementById('wifi1Channel'), data);
	}
	document.getElementById('simApUpdBtn').disabled = false;
}
function updateDetailPanelItem(updateChannels) {
	var select = document.getElementById('simApModel');
	if (updateChannels) {
		getApModelChannels(select.value);
		hm.util.toggleHideElement('autoWifi1Channel', true);
		hm.util.toggleHideElement('autoWifi0Channel', true);
	}
	var radioEl = document.getElementById("simApRadio");
	var radioRow = document.getElementById("simApRadioRow");
	var simApCh0Row = document.getElementById("simApCh0Row");
	var simApPwr0Row = document.getElementById("simApPwr0Row");
	var simApCh1Row = document.getElementById("simApCh1Row");
	var simApPwr1Row = document.getElementById("simApPwr1Row");
	radioEl.disabled = (MODEL_BR100 == select.value);
	if (MODEL_110 == select.value || MODEL_BR200_WP == select.value
			|| MODEL_BR200_LTE_VZ == select.value
			|| MODEL_BR100 == select.value) {
		if (MODEL_BR100 == select.value || radioEl.value == RADIO_24GHZ) {
			hm.util.show("simApCh0Row");
			hm.util.show("simApPwr0Row");
			hm.util.hide("simApCh1Row");
			hm.util.hide("simApPwr1Row");
		} else if (radioEl.value == RADIO_5GHZ) {
			simApCh1Row.cells[0].innerHTML = "Wifi0 Channel";
			simApPwr1Row.cells[0].innerHTML = "Wifi0 Power";
			hm.util.hide("simApCh0Row");
			hm.util.hide("simApPwr0Row");
			hm.util.show("simApCh1Row");
			hm.util.show("simApPwr1Row");
		}
		hm.util.show("simApRadioRow");
	} else {
		simApCh1Row.cells[0].innerHTML = "Wifi1 Channel";
		simApPwr1Row.cells[0].innerHTML = "Wifi1 Power";
		hm.util.hide("simApRadioRow");
		hm.util.show("simApCh0Row");
		hm.util.show("simApPwr0Row");
		hm.util.show("simApCh1Row");
		hm.util.show("simApPwr1Row");
	}
}
function processWallMenuItem(text, targetId) {
	hm.map.exitSelectedWall();
	if (text == "Remove") {
		hm.map.removeWall(targetId);
	} else if (text == "Move") {
		hm.map.moveSelectedWall(targetId);
	} else if (text == "Clone") {
		hm.map.cloneSelectedWall(targetId);
	} else {
		wallType = findWallType(text);
		if (wallType >= 0) {
			var wallIndex = findWallIndex(targetId);
			if (wallIndex < 0) {
				return;
			}
			var wall = hm.map.walls[wallIndex];
			hm.map.changeWallType(wall, wallIndex, wallType);
			hm.map.t1wall = wall;
			saveWalls();
		}
	}
}
function processPerimMenuItem(text, targetId) {
	if (text == "Remove") {
		doRemovePerimeter(targetId);
	} else {
		wallType = findWallType(text);
		if (wallType >= 0) {
			var prm = findPrm(targetId);
			if (prm != null) {
				prm.tp = wallType;
				updatePerimeter(prm.id, wallType);
			}
		}
	}
}
function addSimAp(hwModel, pwr1, ch1, pwr2, ch2) {
	hm.map.requestSimAp(getFrequency(),
			document.forms[formName].rssiThreshold.value,
			document.forms[formName].snrThreshold.value, hwModel, pwr1, ch1,
			pwr2, ch2);
}
function updateSimApMap() {
	var input = document.getElementById('simApId');
	var wlanNode = hm.map.findSimAp(input.value);
	if (!wlanNode) {
		return;
	}
	var simApCh = hm.map.getSimApsCh();
	var select = document.getElementById('simApModel');
	wlanNode.jsonNode.md = select.value;
	var hostName = hm.util.trim(document.getElementById('simApName').value);
	if (hostName.length > 0) {
		wlanNode.jsonNode.hostName = hostName;
	}
	select = document.getElementById('wifi0Channel');
	wlanNode.jsonNode.ch1 = select.value;
	select = document.getElementById('wifi0Power');
	wlanNode.jsonNode.pwr1 = select.value;
	select = document.getElementById('wifi1Channel');
	wlanNode.jsonNode.ch2 = select.value;
	select = document.getElementById('wifi1Power');
	wlanNode.jsonNode.pwr2 = select.value;
	select = document.getElementById('simApRadio');
	if (wlanNode.jsonNode.md == MODEL_110
			|| wlanNode.jsonNode.md == MODEL_BR200_WP
			|| wlanNode.jsonNode.md == MODEL_BR200_LTE_VZ) {
		if (select.value == RADIO_24GHZ) {
			wlanNode.jsonNode.w0Ebd = true;
			wlanNode.jsonNode.w1Ebd = false;
		} else if (select.value == RADIO_5GHZ) {
			wlanNode.jsonNode.w0Ebd = false;
			wlanNode.jsonNode.w1Ebd = true;
		}
	} else {
		wlanNode.jsonNode.w0Ebd = true;
		wlanNode.jsonNode.w1Ebd = wlanNode.jsonNode.md != MODEL_BR100;
	}
	simApDetailsPanel.cfg.setProperty('visible', false);
	invalidateHiddenMaps();
	hm.map.updateSimApLaps(hm.map.processSimApLap, getFrequency(),
			document.forms[formName].rssiThreshold.value,
			document.forms[formName].snrThreshold.value, getRateThreshold(),
			simApCh + "&pwr1=" + wlanNode.jsonNode.pwr1 + "&ch1="
					+ wlanNode.jsonNode.ch1 + "&pwr2=" + wlanNode.jsonNode.pwr2
					+ "&ch2=" + wlanNode.jsonNode.ch2 + "&hostName="
					+ wlanNode.jsonNode.hostName + "&hwModel="
					+ wlanNode.jsonNode.md + "&radio=" + select.value,
			wlanNode.jsonNode.nodeId);
}

var drawOverImages = new Array();
function onLineDrawOver(img, index) {
	if (drawOverImages.length == 0) {
		drawOverImages[0] = hm.util.loadImage(imagesBaseUrl
				+ "line-draw-off.png");
		drawOverImages[1] = hm.util.loadImage(imagesBaseUrl
				+ "line-draw-connected-off.png");
		drawOverImages[2] = hm.util.loadImage(imagesBaseUrl
				+ "line-draw-closed-off.png");
	}
	if (!drawModeSelected(index)) {
		img.className = "line_draw_hover";
	}
	img.src = drawOverImages[index].src;
	img.style.cursor = "pointer";
}

var drawOutImages = new Array();
function onLineDrawOut(img, index) {
	if (drawOutImages.length == 0) {
		drawOutImages[0] = hm.util
				.loadImage(imagesBaseUrl + "line-draw-on.png");
		drawOutImages[1] = hm.util.loadImage(imagesBaseUrl
				+ "line-draw-connected-on.png");
		drawOutImages[2] = hm.util.loadImage(imagesBaseUrl
				+ "line-draw-closed-on.png");
	}
	if (!drawModeSelected(index)) {
		img.className = "line_draw";
	}
	img.src = drawOutImages[index].src;
}

function drawModeSelected(index) {
	if (!hm.map.wallMode) {
		return false;
	} else if (hm.map.closedMode) {
		if (index != 2) {
			return false;
		}
	} else if (hm.map.connectedMode) {
		if (index != 1) {
			return false;
		}
	} else if (index != 0) {
		return false;
	}
	return true;
}
function lineDraw(connected, closed, id) {
	if (hm.map.wallMode) {
		if (hm.map.connectedMode == connected && hm.map.closedMode == closed) {
			if (closed) {
				hm.map.closeWall();
			}
			cancelWallOps();
			return;
		}
	}
	cancelWallOps();
	hm.map.showWallLayer(true);
	toggleCreateWall(false);
	wallType = document.forms[formName].newWallType.selectedIndex;
	// Single wall or multiple walls connected
	hm.map.connectedMode = connected;
	// A closed set of walls
	hm.map.closedMode = closed;
	// First wal in a set of connected walls
	hm.map.connectedStart = -1;
	var img = document.getElementById('line_draw' + id);
	img.className = "line_draw_selected";
}
function cancelWallOps() {
	hm.map.exitSelectedWall();
	hm.map.showWallLayer(false);
	hm.map.hideWallLabel();
	if (hm.map.wallMode) {
		toggleCreateWall(true);
	} else if (hm.map.moveWall) {
		hm.map.removePerimShapes();
	} else {
		top.toggleDrawPerimeter();
	}
}
function doAddAp() {
	cancelWallOps();
	document.getElementById('autoPlaceAps').disabled = true;
	document.getElementById('addAp').disabled = true;
	invalidateHiddenMaps();
	if (mapGroup.getCount() == 5) {
		mapGroup.removeButton(2);
	}
	addSimAp(document.forms[formName].defApModel.value,
			document.forms[formName].defWifi0Power.value,
			document.forms[formName].defWifi0Channel.value,
			document.forms[formName].defWifi1Power.value,
			document.forms[formName].defWifi1Channel.value);
}
function getItem(menu, menuItemText) {
	if (menu && menuItemText) {
		var items = menu.getItems();
		for ( var i = 0, j = items.length; i < j; i++) {
			if (items[i].cfg.getProperty("text") == menuItemText) {
				return items[i];
			}
		}
	}
	return null;
}

function cancelMeasuring() {
	movedNodeId = null;
	if (mtSetXY) {
		hm.map.initMeasuringTool();
	}
	hm.map.hideMeasuringTool();
	document.forms[formName].measuringTool.checked = false;
}
function toggleRemoveWalls() {
	var btn = document.getElementById('removeAllWalls');
	btn.blur();
	btn.disabled = hm.map.walls.length == 0;
}
function toggleRemoveAps() {
	var btnr = document.getElementById('removeAllAps');
	var btnr2 = document.getElementById('autoRemoveAllAps');
	var btna = document.getElementById('autoPlaceAps');
	if (hm.map.planningMode() && hm.map.simNodes.length == 0) {
		btnr.disabled = true;
		btnr2.disabled = true;
		btna.disabled = getFrequency() == 1
				&& document.forms[formName].defApModel.value == MODEL_BR100;
	} else {
		btnr.disabled = false;
		btnr2.disabled = false;
		btna.disabled = true;
	}
	btnr.blur();
	btnr2.blur();
	btna.blur();
	document.getElementById('addAp').disabled = false;
}
function toggleCreateWall(flag) {
	if (flag) {
		if (hm.map.iwall && hm.map.iwall.shape) {
			hm.map.canvasWindow.removeShape(hm.map.surface, hm.map.iwall.shape);
		}
		hm.map.iwall = null;
		hm.map.moveWall = false;
	}
	// One of the wall types selected
	hm.map.wallMode = !flag;
	if (flag) {
		toggleRemoveWalls();
	}
}
function findHwModel(text) {
	var select = document.getElementById('simApModel');
	for ( var i = 0; i < select.options.length; i++) {
		var option = select.options[i];
		if (select.options[i].text == text) {
			return select.options[i].value;
		}
	}
	return -1;
}
function findWallType(text) {
	for ( var i = 0; i < wallTypes.length; i++) {
		if (wallTypes[i].text == text) {
			return i;
		}
	}
	return -1;
}

function findPrm(nodeId) {
	for ( var i = 0; i < hm.map.prms.length; i++) {
		var prm = hm.map.prms[i].prm;
		for ( var j = 0; j < prm.length; j++) {
			var wall = prm[j];
			if (wall.shape.node.id == nodeId) {
				return hm.map.prms[i];
			}
		}
	}
	return null;
}

function findWall(nodeId) {
	var wallIndex = findWallIndex(nodeId);
	if (wallIndex < 0) {
		return null;
	} else {
		return hm.map.walls[wallIndex];
	}
}
function findWallIndex(nodeId) {
	for ( var i = 0; i < hm.map.walls.length; i++) {
		var wall = hm.map.walls[i];
		if (wall && wall.shape && wall.shape.node.id == nodeId) {
			return i;
		}
	}
	return -1;
}

function showMapOpsMenu() {
	mapOpsMenu.cfg.setProperty("x", YAHOO.util.Dom.getX("mapOps") + 1);
	mapOpsMenu.cfg.setProperty("y", YAHOO.util.Dom.getY("mapOps") + 20);
	mapOpsMenu.show();
}
function hoverMapOpsMenu() {
	mapOpsMenu.isShowing = true;
}
function hideMapOpsMenu() {
	mapOpsMenu.isShowing = false;
	setTimeout("if(!mapOpsMenu.isShowing){mapOpsMenu.hide()}", 1000);
}

var checkedServerItem = {
	selectedCount : 0
};
function onServerMenuItemClick(p_sType, p_aArguments, selector) {
	var checked = this.cfg.getProperty("checked");
	this.cfg.setProperty("checked", !checked);
	if (selector == null) {
		return;
	}
	var styleObj = hm.util.getStyle(selector, hm.map.canvasWindow.document);
	if (styleObj != null) {
		styleObj.display = !checked ? "" : "none";
	}
	if (!checked) {
		var elements = hm.util.getElementsByClassName(selector.substr(1),
				hm.map.canvasWindow.document);
		showNote(elements.length + " items found ...", 1);
		checkedServerItem.selectedCount += 1;
		checkedServerItem[selector] = this;
	} else {
		checkedServerItem.selectedCount -= 1;
		delete checkedServerItem[selector];
	}
}
function reloadServerIconStyle() {
	for ( var prop in checkedServerItem) {
		if (prop == "selectedCount")
			continue;
		var selector = checkedServerItem[prop].cfg.getProperty("onclick").obj;
		var checked = checkedServerItem[prop].cfg.getProperty("checked");
		var styleObj = hm.util.getStyle(selector, hm.map.canvasWindow.document);
		if (styleObj != null) {
			styleObj.display = checked ? "" : "none";
		}
	}
}

function toggleGrid(cb) {
	var grid = hm.map.canvasWindow.document.getElementById("grid");
	if (cb.checked) {
		if (gridBorderY == 0) {
			scaleDelta = 1;
			hm.map.loadMap(hm.map.mapId);
		} else {
			grid.style.display = "";
		}
	} else {
		grid.style.display = "none";
	}
}
function toggleSizingTool(cb) {
	if (cb.checked) {
		showSizingTool();
	} else {
		cancelSizeMap();
	}
}
function toggleMeasuringTool(cb) {
	if (actualHeight > 0) {
		if (cb.checked) {
			cancelSizeMap();
			hm.map.showMeasuringTool();
		} else {
			cancelMeasuring();
		}
	} else {
		if (cb.checked) {
			hm.map.showDistanceTool();
		} else {
			hm.map.hideDistanceTool();
		}
	}
}
var rssiData = null;
function clearRssiData() {
	if (rssiData) {
		hm.map.restoreRssiArea();
		hm.map.removeClientRssi(rssiData);
		rssiData = null;
	}
}
function toggleClients(cb) {
	if (cb.checked) {
		var td = document.getElementById("note");
		hm.util.replaceChildren(td, document
				.createTextNode("Estimating client positions..."));
		hm.util.show('processing');
		hm.map.requestMapObjects('clients', processClientLocations);
	} else {
		hm.map.removeClientNodes("clients");
		clientMac = "";
		clearRssiData();
		hideCoverage();
	}
}
function processClientLocations(o) {
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document
			.createTextNode("Estimating client positions... Done."));
	delayHideProcessing(0.5);
	hm.map.processClientNodes(o, false);
}
function showRssi(mac, parentId) {
	clearRssiData();
	if (parentId == "rogues") {
		hm.map.requestMapObjects("rogueRssi", hm.map.processRogueRssi, mac);
	} else if (parentId == "clients") {
		hm.map.requestMapObjects("clientRssi", hm.map.processClientRssi, mac);
	}
}
var clientCoverage = null;
function showCoverage(mac) {
	clientCoverage = mac;
	hm.map.requestMapObjects("clientRssi", showCoverageMap, mac);
}
function hideCoverage() {
	if (clientCoverage != null) {
		hideRssi(clientCoverage, "clients");
		clientCoverage = null;
		setMapVisibility(false);
		hm.map.canvasWindow.restoreClientContextMenu(null);
	}
}
function hideRssi(mac, parentId) {
	if (rssiData) {
		hm.map.restoreRssiArea();
		hm.map.removeClientRssi(rssiData);
		rssiData = null;
		hm.map.canvasWindow.restoreClientContextMenu(null);
	}
}

var acspLeafNodeId = null;
function showAcspCoverage() {
	acspLeafNodeId = document.forms[formName].leafNodeId.value;
	hm.map
			.requestMapObjects("acspNbrRssi", showAcspCoverageMap,
					acspLeafNodeId);
	if (waitingPanel != null) {
		waitingPanel.show();
	}
}

var acspNbrLinks = null;
var showAcspCoverageMap = function(o) {
	if (waitingPanel != null) {
		waitingPanel.hide();
	}
	eval("var data = " + o.responseText);
	if (data) {
		if (data.e) {
			showInfoDialog(data.e);
			return;
		}
		var toNode = hm.map.wlanNodesHash[data.apId];
		if (!toNode) {
			return;
		}
		hm.map.hideMesh();
		invalidateHeatChannelMap();
		for ( var i = 0; i < data.nbrs.length; i++) {
			var fromNode = hm.map.wlanNodesHash[data.nbrs[i].apId];
			if (fromNode) {
				var line = hm.map.getLinkLine(fromNode, toNode);
				data.nbrs[i].line = hm.map.canvasWindow.createRafaelLine(
						hm.map.surface, line, "#00AA00", 1, 2, "-");
				data.nbrs[i].div = hm.map.createLinkLabel(data.nbrs[i].rssi,
						line.x1, line.x2, line.y1, line.y2, 35);
			}
		}
		acspNbrLinks = data.nbrs;
		var cb = document.forms[formName].heat;
		cb.checked = true;
		toggleHeatMap(cb);
	}
}
function removeAcspNbrLinks(clear) {
	if (clear) {
		acspLeafNodeId = null;
	}
	var anl = acspNbrLinks;
	acspNbrLinks = null;
	if (!anl) {
		return;
	}
	var anchor = hm.map.canvasWindow.document.getElementById("anchor");
	for ( var i = 0; i < anl.length; i++) {
		if (anl[i].div) {
			anchor.removeChild(anl[i].div);
		}
		if (anl[i].line) {
			hm.map.canvasWindow.removeShape(hm.map.surface, anl[i].line);
		}
	}
}

function invalidateHiddenMaps() {
	heatMapHidden = false;
	channelMapHidden = false;
	interferenceMapHidden = false;
	ratesMapHidden = false;
}
function invalidateHeatChannelMap() {
	invalidateHiddenMaps();
	if (document.forms[formName].heat.checked
			|| document.forms[formName].channel.checked
			|| document.forms[formName].interference.checked
			|| document.forms[formName].rates.checked) {
		document.forms[formName].heat.checked = false;
		document.forms[formName].channel.checked = false;
		document.forms[formName].interference.checked = false;
		document.forms[formName].rates.checked = false;
		setMapVisibility(false);
	}
	removeAcspNbrLinks(false);
}
function disableHeatChannelMap(flag) {
	if (flag && !document.forms[formName].heat.checked
			&& !document.forms[formName].channel.checked
			&& !document.forms[formName].interference.checked) {
		return;
	}
	document.forms[formName].heat.disabled = flag;
	document.forms[formName].channel.disabled = flag;
	document.forms[formName].interference.disabled = flag;
}
function calibrateRogueRssi(mac, parentId) {
	if (parentId == "rogues") {
		var operation = "calibrateRogueRssi";
	} else if (parentId == "clients") {
		var operation = "calibrateClientRssi";
	} else {
		return;
	}
	invalidateHeatChannelMap();
	clearRssiData();
	hm.map.calibrateRssi(operation, mac, parentId);
}
function uncalibrateRogueRssi(mac, parentId) {
	if (parentId == "rogues") {
		var operation = "uncalibrateRogueRssi";
	} else if (parentId == "clients") {
		var operation = "uncalibrateClientRssi";
	} else {
		return;
	}
	invalidateHeatChannelMap();
	clearRssiData();
	hm.map.uncalibrateRssi(operation, mac, parentId);
}
function processCalibrateRssi(o) {
	eval("var data = " + o.responseText);
	if (data && data.added > 0) {
		hm.map.canvasWindow.restoreSampledClientContextMenu(data.mac);
		showNote(data.added + " measurements added.", 1);
	}
}
function processUncalibrateRssi(o) {
	eval("var data = " + o.responseText);
	if (data && data.removed > 0) {
		hm.map.canvasWindow.restoreUnsampledClientContextMenu(data.mac);
		showNote(data.removed + " measurements removed.", 1);
	}
}

function toggleRogues(cb) {
	if (cb.checked) {
		var td = document.getElementById("note");
		hm.util.replaceChildren(td, document
				.createTextNode("Estimating friendly/rogue AP positions..."));
		hm.util.show('processing');
		hm.map.requestMapObjects('rogues', processRogueLocations);
	} else {
		hm.map.removeClientNodes("rogues");
		clearRssiData();
	}
}
function processRogueLocations(o) {
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document
			.createTextNode("Estimating friendly/rogue AP positions... Done."));
	delayHideProcessing(0.5);
	hm.map.processClientNodes(o, true);
}
function toggleShowSpill(cb) {
	hm.map.refreshFromCache(true);
}
var heatMapHidden = false;
function toggleHeatMap(cb) {
	if (cb.checked) {
		selectedLayers = 1;
		if (heatMapHidden) {
			heatMapHidden = false;
			setMapVisibility(true);
			hm.map.refreshSpill(false);
		} else {
			hm.util.toggleHideElement("interferenceLegendDiv", true);
			hm.util.toggleHideElement("coverageLegendDiv", false);
			hm.util.toggleHideElement("snrLegendDiv", true);
			hm.util.toggleHideElement("ratesLegendDiv", true);
			channelMapHidden = false;
			document.forms[formName].channel.checked = false;
			interferenceMapHidden = false;
			document.forms[formName].interference.checked = false;
			ratesMapHidden = false;
			document.forms[formName].rates.checked = false;
			toggleHeatChannelMap();
		}
	} else {
		heatMapHidden = getFrequency() > 0 && acspLeafNodeId == null;
		setMapVisibility(false);
		hm.map.refreshSpill(true);
		removeAcspNbrLinks(true);
	}
	showFadeMargin();
}
var channelMapHidden = false;
function toggleChannelMap(cb) {
	if (cb.checked) {
		selectedLayers = 2;
		if (hm.map.planningMode()) {
			if (document.forms[formName].apLabels.selectedIndex != 1) {
				document.forms[formName].apLabels.selectedIndex = 1;
				hm.map.changeNodeLabels();
			}
		}
		if (channelMapHidden) {
			channelMapHidden = false;
			setMapVisibility(true);
			hm.map.refreshSpill(false);
		} else {
			hm.util.toggleHideElement("interferenceLegendDiv", true);
			hm.util.toggleHideElement("coverageLegendDiv", false);
			hm.util.toggleHideElement("snrLegendDiv", true);
			hm.util.toggleHideElement("ratesLegendDiv", true);
			heatMapHidden = false;
			document.forms[formName].heat.checked = false;
			interferenceMapHidden = false;
			document.forms[formName].interference.checked = false;
			ratesMapHidden = false;
			document.forms[formName].rates.checked = false;
			removeAcspNbrLinks(true);
			toggleHeatChannelMap();
		}
	} else {
		channelMapHidden = getFrequency() > 0;
		setMapVisibility(false);
		hm.map.refreshSpill(true);
	}
	showFadeMargin();
}
var interferenceMapHidden = false;
function toggleInterferenceMap(cb) {
	if (cb.checked) {
		selectedLayers = 4;
		if (interferenceMapHidden) {
			interferenceMapHidden = false;
			setMapVisibility(true);
		} else {
			var pm = hm.map.planningMode();
			hm.util.toggleHideElement("interferenceLegendDiv", pm);
			hm.util.toggleHideElement("coverageLegendDiv", true);
			hm.util.toggleHideElement("snrLegendDiv", !pm);
			hm.util.toggleHideElement("ratesLegendDiv", true);
			heatMapHidden = false;
			document.forms[formName].heat.checked = false;
			channelMapHidden = false;
			document.forms[formName].channel.checked = false;
			ratesMapHidden = false;
			document.forms[formName].rates.checked = false;
			removeAcspNbrLinks(true);
			toggleHeatChannelMap();
		}
	} else {
		interferenceMapHidden = getFrequency() > 0;
		setMapVisibility(false);
	}
	showFadeMargin();
}
var ratesMapHidden = false;
function toggleRatesMap(cb) {
	if (cb.checked) {
		selectedLayers = 8;
		if (ratesMapHidden) {
			ratesMapHidden = false;
			setMapVisibility(true);
		} else {
			hm.util.toggleHideElement("interferenceLegendDiv", true);
			hm.util.toggleHideElement("coverageLegendDiv", true);
			hm.util.toggleHideElement("snrLegendDiv", true);
			hm.util.toggleHideElement("ratesLegendDiv", false);
			heatMapHidden = false;
			document.forms[formName].heat.checked = false;
			channelMapHidden = false;
			document.forms[formName].channel.checked = false;
			interferenceMapHidden = false;
			document.forms[formName].interference.checked = false;
			toggleHeatChannelMap();
		}
	} else {
		ratesMapHidden = getFrequency() > 0;
		setMapVisibility(false);
	}
	showFadeMargin();
}
function setMapVisibility(visible) {
	if (hm.map.planningMode()) {
		var div = hm.map.canvasWindow.document.getElementById("plan");
		div.style.display = visible ? "" : "none";
	} else {
		var heatDiv = hm.map.canvasWindow.document.getElementById("heat");
		var bgDiv = hm.map.canvasWindow.document.getElementById("bgMap");
		if (visible) {
			heatDiv.style.display = "";
		} else {
			heatDiv.style.display = "none";
		}
	}
}
function getLayers() {
	var layers = 0;
	if (document.forms[formName].heat.checked) {
		layers = 1;
	}
	if (document.forms[formName].channel.checked) {
		layers += 2;
	}
	if (document.forms[formName].interference.checked) {
		layers += 4;
	}
	if (document.forms[formName].rates.checked) {
		layers += 8;
	}
	return layers;
}
function getFrequency() {
	return document.forms[formName].frequency.value == 5 ? 1 : 2;
}
function updateSpillOnly() {
	if (document.forms[formName].showSpill.checked && getLayers() == 0
			&& selectedNode.data.tp == 3) {
		hm.map.updateSpillCache(getFrequency(),
				document.forms[formName].rssiThreshold.value, false,
				hm.map.updateSpillCacheDone);
	}
}
function toggleFrequency(dd) {
	var five = dd.value == 5;
	hm.util.toggleHideElement("actualDim5", !five);
	hm.util.toggleHideElement("defWifi0Channel", five);
	hm.util.toggleHideElement("defWifi1Channel", !five);
	hm.util.toggleHideElement("defWifi0Power", five);
	hm.util.toggleHideElement("defWifi1Power", !five);
	if (hm.map.bldMode()) {
		hm.map.canvasWindow.updateNails();
		return;
	}
	if (hm.map.planningMode()) {
		adjustAutoPlacement(document.forms[formName].defApModel.value);
		hm.map.changeNodeLabels();
		updateSpillOnly();
	} else {
		disableHeatChannelMap(true);
		if (getLayers() == 0 && getRapLabels() == "cp") {
			hm.map.changeRapNodeLabels();
		}
	}
	if (five) {
		showRatesLegend(document.forms[formName].channelWidth);
	} else {
		hm.util.toggleHideElement("ratesLegendHT20", false);
		hm.util.toggleHideElement("ratesLegendHT40", true);
		hm.util.toggleHideElement("ratesLegendHT80", true);
	}
	updateRateThreshold();
	removeAcspNbrLinks(true);
	toggleHeatChannelMap();
}
function toggleHeatChannelMap() {
	clientCoverage = null;
	cancelSizeMap();
	closeSizeMap();
	var layers = getLayers();
	var frequency = getFrequency();
	var rssiColorCount = document.forms[formName].rssiThreshold.value - 35 + 1;
	if (layers == 0 || frequency == 0) {
		updateRssiLegend(rssiColors, rssiColorCount);
		invalidateHiddenMaps();
		setMapVisibility(false);
		return false;
	} else {
		if (!hm.map.planningMode()) {
			prepareHeatImg();
		}
		if (document.forms[formName].channel.checked) {
			if (channelRssiColors == null) {
				doChangeRssiThreshold();
				return;
			} else {
				updateRssiLegend(channelRssiColors, channelRssiColors.length);
			}
		} else if (document.forms[formName].heat.checked) {
			updateRssiLegend(rssiColors, rssiColorCount);
		}
		cancelMeasuring();

		if (hm.map.planningMode()) {
			hm.map.toggleHideSpill(layers >= 4); // CJS
			hm.map.updateSimApLaps(hm.map.processSimApLaps, frequency,
					document.forms[formName].rssiThreshold.value,
					document.forms[formName].snrThreshold.value,
					getRateThreshold(), null, "");
			if (hm.map.simNodes.length > 0) {
				requestPlanHeatImg(frequency);
			}
			setMapVisibility(true);
		} else {
			if (layers == 2) { // Channel map
				document.forms[formName].heat.disabled = false;
				document.forms[formName].channel.disabled = true;
				document.forms[formName].interference.disabled = false;
			} else if (layers == 4) { // Interference map
				document.forms[formName].heat.disabled = false;
				document.forms[formName].channel.disabled = false;
				document.forms[formName].interference.disabled = true;
			} else {
				document.forms[formName].heat.disabled = true;
				document.forms[formName].channel.disabled = false;
				document.forms[formName].interference.disabled = false;
			}
			requestHeatImg(layers, frequency, 0, 0);
			return true;
		}
	}
}
function planHeatImgLoaded() {
	hm.util.hide('processing');
}
function prepareHeatImg() {
	var heatDiv = hm.map.canvasWindow.document.getElementById("heat");
	var img = heatDiv.firstChild;
	if (img != null) {
		heatDiv.removeChild(img);
	}
	img = hm.map.canvasWindow.document.createElement("img");
	img.width = canvasWidth - gridBorderX;
	img.height = canvasHeight - gridBorderY;
	heatDiv.appendChild(img);
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document
			.createTextNode(getHeatMapWaitMessage()));
	hm.util.show('processing');
}

function getHeatMapUrl(layers, frequency, latchId, extra) {
	return mapsActionOperationUrl + "heatMap&id=" + hm.map.mapId + "&ignore="
			+ new Date().getTime() + "&rssiThreshold="
			+ document.forms[formName].rssiThreshold.value + "&frequency="
			+ frequency + "&layers=" + layers + "&scale=" + hm.map.scale
			+ extra + "&pageId=" + hm.map.pageId + "&latchId=" + latchId
			+ "&canvasWidth=" + (canvasWidth - gridBorderX) + "&canvasHeight="
			+ (canvasHeight - gridBorderY);
}

var currentLatchId = null;
function requestHeatImg(layers, frequency, x, y) {
	var latchId = new Date().getTime();
	currentLatchId = latchId;
	var heatDiv = hm.map.canvasWindow.document.getElementById("heat");
	var extra = "&xs=" + x + "&ys=" + y;
	if (clientCoverage != null) {
		extra += "&bssid=" + clientCoverage;
		acspLeafNodeId = null;
	} else if (acspLeafNodeId != null) {
		extra += "&acspId=" + acspLeafNodeId;
	} else {
		if (document.getElementById("calibrateHeatmap").checked) {
			hm.map.requestMapObjects('nextIds', function(o) {
				nextIdsDone(o, latchId, heatDiv, layers, frequency);
			}, frequency, latchId);
			return;
		}
	}
	var url = getHeatMapUrl(layers, frequency, latchId, extra);
	var img = heatDiv.firstChild;
	img.src = url;
	img.onload = function() {
		heatDiv.style.display = "";
		this.onload = null;
		hm.map.requestMapObjects('channels', function(o) {
			heatChannelMapDone(o, latchId, heatDiv);
		}, frequency, latchId);
	}
}
function nextIdsDone(o, latchId, heatDiv, layers, frequency) {
	eval("var data = " + o.responseText);
	if (!data || data.latchId != latchId || data.channels.length == 0) {
		disableHeatChannelMap(false);
		return;
	}
	var index = 0, updated = false;
	do {
		labelNode = data.channels[index++];
		if (updateRapLabel(labelNode)) {
			updated = true;
		}
	} while (index < data.channels.length && !labelNode.seen);
	if (!labelNode.seen) { // None seen, could be cached
		nextIdsFinish(data, updated, latchId, heatDiv);
		return;
	}
	if (latchId != currentLatchId) {
		return;
	}
	var img = heatDiv.firstChild;
	img.src = getHeatMapUrl(layers, frequency, latchId, "&nextId="
			+ labelNode.nodeId.substring(1));
	img.onload = function() {
		heatDiv.style.display = "";
		if (index == data.channels.length) { // Last item was seen
			this.onload = null;
			nextIdsFinish(data, updated, latchId, heatDiv);
			return;
		}
		do {
			labelNode = data.channels[index++];
			if (updateRapLabel(labelNode)) {
				updated = true;
			}
		} while (index < data.channels.length && !labelNode.seen);
		if (labelNode.seen && latchId == currentLatchId) {
			img.src = getHeatMapUrl(layers, frequency, latchId, "&nextId="
					+ labelNode.nodeId.substring(1));
		} else {
			this.onload = null;
			nextIdsFinish(data, updated, latchId, heatDiv);
		}
	}
}
function nextIdsFinish(data, updated, latchId, heatDiv) {
	if (latchId != currentLatchId) {
		return;
	}
	heatDiv.style.display = "";
	disableHeatChannelMap(false);
	if (updated) {
		if (document.forms[formName].rapLabels.selectedIndex != 1) {
			document.forms[formName].rapLabels.selectedIndex = 1;
		}
	}
	var td = document.getElementById("note");
	if (data.missing.length > 0) {
		onUnloadNotes();
		td.innerHTML = heatMissingRssiText + data.range + data.missing
				+ ".<br>" + heatMissingRssiMoreText;
		delayHideProcessing(15);
		if (!data.seen) {
			document.forms[formName].heat.checked = false;
			document.forms[formName].channel.checked = false;
			document.forms[formName].interference.checked = false;
			heatDiv.style.display = "none";
			return;
		}
	} else {
		hm.util.replaceChildren(td, document
				.createTextNode(getHeatMapWaitMessage() + " done."));
		delayHideProcessing(1);
	}
	if (data.hmr == 1 && hm.map.walls.length == 0 && hm.map.prms.length == 0) {
		hmcReload(latchId, heatDiv);
	}
}
function updateRapLabel(labelNode) {
	var wlanNode = hm.map.wlanNodesHash[labelNode.nodeId];
	if (wlanNode == null || wlanNode.jsonNode.container
			|| labelNode.apName.length == 0) {
		return false;
	}
	wlanNode.label = labelNode.apName;
	var labelDiv = hm.map.canvasWindow.document.getElementById('ll'
			+ labelNode.nodeId);
	while (labelDiv.childNodes.length > 0) {
		labelDiv.removeChild(labelDiv.firstChild);
	}
	// var b = hm.map.canvasWindow.document.createElement("b");
	// b.appendChild(hm.map.canvasWindow.document.createTextNode(labelNode.apName));
	labelDiv.appendChild(hm.map.canvasWindow.document
			.createTextNode(labelNode.apName));
	return true;
}
function heatChannelMapDone(o, latchId, heatDiv) {
	if (latchId != currentLatchId) {
		return;
	}
	disableHeatChannelMap(false);
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document.createTextNode(getHeatMapWaitMessage()
			+ " done."));
	var hideDelay = 1;
	if (clientCoverage != null) {
		delayHideProcessing(hideDelay); // No reload
		return;
	}
	if (document.forms[formName].rapLabels.selectedIndex != 1) {
		document.forms[formName].rapLabels.selectedIndex = 1;
	}
	var data = hm.map.processMapNodeLabels(o);
	if (!data || data.length == 0 || data[0].latchId != latchId
			|| !document.getElementById("calibrateHeatmap").checked) {
		delayHideProcessing(hideDelay);
		return;
	}
	if (data[0].hmr < 0) {
		onUnloadNotes();
		td = document.getElementById("note");
		hm.util.replaceChildren(td, document.createTextNode(heatNoRssiText));
		delayHideProcessing(10);
		document.forms[formName].heat.checked = false;
		document.forms[formName].channel.checked = false;
		document.forms[formName].interference.checked = false;
		setMapVisibility(false);
		return;
	} else if (data[0].hmr != 1) {
		delayHideProcessing(hideDelay);
		return;
	}
	delayHideProcessing(hideDelay);
	if (hm.map.walls.length == 0 && hm.map.prms.length == 0) {
		hmcReload(latchId, heatDiv);
	}
}
function hmcReload(latchId, heatDiv) {
	heatDiv.firstChild.src = mapsActionOperationUrl + "heatMapHr&id="
			+ hm.map.mapId + "&frequency=" + getFrequency() + "&layers="
			+ getLayers() + "&rssiThreshold="
			+ document.forms[formName].rssiThreshold.value + "&pageId="
			+ hm.map.pageId + "&latchId=" + latchId + "&ignore="
			+ new Date().getTime();
}
function fadeMarginChanged(combo) {
	url = mapsActionOperationUrl + "updateFadeMargin&id=" + hm.map.mapId
			+ "&fadeMargin=" + combo.value + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : fadeMarginUpdated
	}, null);
}
function snrThresholdChanged(combo) {
	url = mapsActionOperationUrl + "updateSnrThreshold&id=" + hm.map.mapId
			+ "&snrThreshold=" + combo.value + "&ignore="
			+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : snrThresholdUpdated
	}, null);
}

function showCoverageMap(o) {
	hm.map.processRssi(o, "clients", false);
	if (rssiData) {
		invalidateHiddenMaps();
		prepareHeatImg();
		var anchor = hm.map.canvasWindow.document.getElementById("clients");
		var clientDiv = hm.map.canvasWindow.document
				.getElementById(clientCoverage);
		var x = YAHOO.util.Dom.getX(clientDiv) + hm.map.rogueApIconSize
				- hm.map.viewWindow.gridBorderX - 1;
		var y = YAHOO.util.Dom.getY(clientDiv) + hm.map.rogueApIconSize
				- hm.map.viewWindow.gridBorderY;
		requestHeatImg(1, getFrequency(), x, y);
	} else {
		hideCoverage();
	}
}
function getHeatMapWaitMessage() {
	if (document.forms[formName].heat.checked) {
		return "Calculating RSSI map ...";
	} else if (document.forms[formName].channel.checked) {
		return "Calculating channel map ...";
	} else if (document.forms[formName].interference.checked) {
		return "Calculating interference map ...";
	} else {
		return "Calculating client RSSI map ...";
	}
}
var rssiColors = [ '#800000', '#951500', '#aa2b00', '#bf4000', '#d45500',
		'#ea6b00', '#ff8000', '#ff8c00', '#ff9700', '#ffa300', '#ffae00',
		'#ffba00', '#ffc500', '#ffd100', '#ffdc00', '#ffe800', '#fff300',
		'#ffff00', '#d4ff0b', '#aaff15', '#80ff20', '#55ff2b', '#2bff35',
		'#00ff40', '#00ff58', '#00ff70', '#00ff88', '#00ff9f', '#00ffb7',
		'#00ffcf', '#00ffe7', '#00ffff', '#00f1fe', '#01e4fc', '#01d6fb',
		'#01c8f9', '#02bbf8', '#02adf6', '#029ff5', '#0292f4', '#0384f2',
		'#0376f1', '#066de3', '#0963d4', '#0c5ac6', '#0f50b8', '#1246aa',
		'#153d9c', '#18338d', '#1b2a7f', '#1e2071', '#211662', '#240c54',
		'#270346', '#2a0038', '#2d002a' ];
function createRssiLegend() {
	for ( var i = 0; i < rssiColors.length; i++) {
		document.writeln('<td title="', -i - 35, '" style="background-color:',
				rssiColors[i], '" width="3" height="18">&nbsp;</td>');
	}
}
function createSnrLegend(snrThreshold) {
	for ( var i = 0; i < rssiColors.length; i++) {
		var color = 60 - i >= snrThreshold ? rssiColors[i] : '#fff';
		document.writeln('<td title="', 60 - i, '" style="background-color:',
				color, '" width="3" height="18">&nbsp;</td>');
	}
}
function updateSnrLegend() {
	var snrThreshold = document.forms[formName].snrThreshold.value;
	var legend = document.getElementById("snrLegend");
	var colorIndex = 0;
	for ( var i = 2; i < legend.childNodes.length; i++) {
		var td = legend.childNodes[i];
		if (td.nodeName == "TD") {
			if (colorIndex <= 60 - snrThreshold) {
				td.style.backgroundColor = rssiColors[colorIndex++];
			} else if (colorIndex++ < rssiColors.length) {
				td.style.backgroundColor = "";
			}
		}
	}
}
function updateRssiLegend(colors, subset) {
	var legend = document.getElementById("coverageLegend");
	var colorIndex = 0;
	for ( var i = 2; i < legend.childNodes.length; i++) {
		var td = legend.childNodes[i];
		if (td.nodeName == "TD") {
			if (colorIndex < subset) {
				td.style.backgroundColor = colors[colorIndex++];
			} else if (colorIndex++ < rssiColors.length) {
				td.style.backgroundColor = "";
			}
		}
	}
}
function showFadeMargin() {
	var noise = document.forms[formName].rates.checked
			|| (hm.map.planningMode() && document.forms[formName].interference.checked);
	hm.util
			.toggleHideElement("actualDim4a", noise
					|| selectedNode.data.tp != 3);
	hm.util.toggleHideElement("actualDim4b", noise);
	hm.util.toggleHideElement("actualDim4c", !noise);
}
function fadeMarginUpdated(o) {
	eval("var data = " + o.responseText);
	if (data) {
		toggleHeatChannelMap();
	}
}
function snrThresholdUpdated(o) {
	eval("var data = " + o.responseText);
	if (data) {
		updateSnrLegend();
		toggleHeatChannelMap();
	}
}

function changeDefApModel(cb) {
	adjustAutoPlacement(cb.value);
	if (getFrequency() == 2) {
		return;
	}
	var url = "mapBld.action?operation=apChannels&id=" + hm.map.mapId
			+ "&treeWidth=" + cb.value + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : newDefApModelChannels
	}, null);
}
function adjustAutoPlacement(apModel) {
	var disable = hm.map.simNodes.length > 0
			|| (getFrequency() == 1 && apModel == MODEL_BR100);
	var btn = document.getElementById('autoPlaceAps');
	if (btn.disabled != disable) {
		btn.disabled = disable;
	}
}
function newDefApModelChannels(o) {
	eval("var data = " + o.responseText);
	if (data) {
		updateApModelChannels(document.forms[formName].defWifi1Channel, data);
	}
}
function updateApModelChannels(select, channels) {
	var value = select.value;
	select.options.length = 1;
	for ( var i = 1; i < channels.length; i++) {
		select.options[select.options.length] = new Option(channels[i],
				channels[i]);
	}
	hm.map.setSelectedIndex(select, value);
}
function getApLabels() {
	return document.forms[formName].apLabels.value;
}
function getRapLabels() {
	return document.forms[formName].rapLabels.value;
}

var bit_rates_ht20, bit_rates_ht40, bit_rates_ht80, rate_colors_ht20, rate_colors_ht40, rate_colors_ht80;

function channelWidthChanged(combo) {
	showRatesLegend(combo);
	var url = mapsActionOperationUrl + "updateChannelWidth&id=" + hm.map.mapId
			+ "&channelWidth=" + combo.value + "&hwModel="
			+ document.forms[formName].defApModel.value + "&ignore="
			+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : channelWidthUpdated
	}, null);
}

function channelWidthUpdated(o) {
	eval("var data = " + o.responseText);
	if (data) {
		updateApModelChannels(document.forms[formName].defWifi1Channel,
				data.channels);
		hm.map.repairSimApDetails(data.planned, null, false);
	}
	rateThresholdChanged();
}
function showRatesLegend(combo) {
	hm.util.toggleHideElement("ratesLegendHT20", combo.value != 1);
	hm.util.toggleHideElement("ratesLegendHT40", combo.value != 2
			&& combo.value != 3);
	hm.util.toggleHideElement("ratesLegendHT80", combo.value != 4);
}

function getChannelWidth() {
	return document.forms[formName].channelWidth.value;
}
function getRateThreshold() {
	if (getChannelWidth() == 1 || getFrequency() == 2) {
		return document.forms[formName].rateThresholdHT20.value;
	} else if (getChannelWidth() == 4) {
		return document.forms[formName].rateThresholdHT80.value;
	} else {
		return document.forms[formName].rateThresholdHT40.value;
	}
}
function rateThresholdChanged() {
	updateRateThreshold();
	toggleHeatChannelMap();
}
function updateRateThreshold() {
	if (document.forms[formName].channelWidth.value == 1 || getFrequency() == 2) {
		var legend = document.getElementById("legendHT20");
		var bit_rates = bit_rates_ht20;
		var rate_colors = rate_colors_ht20;
	} else if (document.forms[formName].channelWidth.value == 4) {
		var legend = document.getElementById("legendHT80");
		var bit_rates = bit_rates_ht80;
		var rate_colors = rate_colors_ht80;
	} else {
		var legend = document.getElementById("legendHT40");
		var bit_rates = bit_rates_ht40;
		var rate_colors = rate_colors_ht40;
	}
	var rateThreshold = getRateThreshold();
	var rateIndex = bit_rates.length - 1;
	var rate = bit_rates[rateIndex];
	var colorIndex = rate_colors[0];
	var j = 0;
	for ( var i = 2; i < legend.childNodes.length; i++) {
		var td = legend.childNodes[i];
		if (td.nodeName == "TD") {
			if (rate_colors[j] != colorIndex) {
				rate = bit_rates[--rateIndex];
				colorIndex = rate_colors[j];
			}
			if (rate >= rateThreshold) {
				td.style.backgroundColor = rssiColors[colorIndex];
			} else {
				td.style.backgroundColor = "";
			}
			j++;
		}
	}
}
function createRatesLegendBar(bit_rates, rate_colors) {
	var rateIndex = bit_rates.length - 1;
	var rate = bit_rates[rateIndex];
	var colorIndex = rate_colors[0];
	for ( var i = 0; i < rssiColors.length; i++) {
		if (rate_colors[i] != colorIndex) {
			rate = bit_rates[--rateIndex];
			colorIndex = rate_colors[i];
		}
		document.writeln('<td title="', rate,
				' Mbps" style="background-color:', rssiColors[colorIndex],
				'" width="3" height="18">&nbsp;</td>');
	}
}

function createHT20RatesLegend() {
	bit_rates_ht20 = [ 1, 6, 12, 18, 24, 36, 52, 78, 104, 117, 130 ];
	var rateIndex = 0;
	var rate = bit_rates_ht20[rateIndex];
	var colorIndex = rssiColors.length - 1;
	var fromRate = 0;
	rate_colors_ht20 = new Array();
	for ( var i = colorIndex; i >= 0; i--) {
		if (fromRate + 1.5 > rate) {
			if (rateIndex + 1 < bit_rates_ht20.length) {
				rate = bit_rates_ht20[++rateIndex];
				colorIndex = i == 13 ? 14 : i;
			} else {
				colorIndex = 3;
			}
		}
		rate_colors_ht20[i] = colorIndex;
		fromRate += 2.5;
	}
	rate_colors_ht20[24] = 34;
	rate_colors_ht20[34] = 41;
	rate_colors_ht20[51] = 50;
	rate_colors_ht20[54] = 53;
	createRatesLegendBar(bit_rates_ht20, rate_colors_ht20);
}
function createHT40RatesLegend() {
	bit_rates_ht40 = [ 1, 6, 12, 18, 24, 27, 36, 48, 54, 81, 108, 130, 162,
			216, 247, 270 ];
	var rateIndex = 0;
	var rate = bit_rates_ht40[rateIndex];
	var colorIndex = rssiColors.length - 1;
	var fromRate = 0;
	rate_colors_ht40 = new Array();
	for ( var i = colorIndex; i >= 0; i--) {
		if (fromRate + 4 > rate) {
			if (rateIndex + 1 < bit_rates_ht40.length) {
				rate = bit_rates_ht40[++rateIndex];
				colorIndex = i == 6 ? 8 : i == 12 ? 14 : i;
			} else {
				colorIndex = 3;
			}
		}
		rate_colors_ht40[i] = colorIndex;
		fromRate += 5;
	}
	createRatesLegendBar(bit_rates_ht40, rate_colors_ht40);
}
function createHT80RatesLegend() {
	bit_rates_ht80 = [ 1, 6, 24, 54, 81, 108, 130, 162, 216, 260, 390, 520,
			650, 780, 910, 1040, 1170 ];
	var colorIndex = rssiColors.length - 1;
	rate_colors_ht80 = new Array();
	rate_colors_ht80[colorIndex] = colorIndex--;
	rate_colors_ht80[colorIndex] = colorIndex--;
	rate_colors_ht80[colorIndex] = colorIndex--;
	var fromRate = 66;
	var rateIndex = 3;
	var rate = bit_rates_ht80[rateIndex];
	for ( var i = colorIndex; i >= 0; i--) {
		if (fromRate + 20 > rate) {
			if (rateIndex + 1 < bit_rates_ht80.length) {
				rate = bit_rates_ht80[++rateIndex];
				colorIndex = i;
			} else {
				colorIndex = 3;
			}
		}
		rate_colors_ht80[i] = colorIndex;
		fromRate += 22;
	}
	createRatesLegendBar(bit_rates_ht80, rate_colors_ht80);
}
var channelRssiColors = null;
function processRssiRange(o) {
	eval("var data = " + o.responseText);
	if (data) {
		channelRssiColors = new Array();
		for ( var i = 0; i < data.length; i++) {
			channelRssiColors[channelRssiColors.length] = data[i].rc;
		}
		var mac = clientCoverage;
		if (mac != null) {
			hideCoverage();
		}
		toggleHeatChannelMap();
		if (mac != null) {
			showCoverage(mac);
		}
	}
}
function doChangeRssiThreshold() {
	hm.map.requestMapObjects("rssiRange", processRssiRange,
			document.forms[formName].rssiThreshold.value);
}
function rssiThresholdChanged() {
	if (hm.map.planningMode()) {
		updateSpillOnly();
		adjustTargetApp();
	} else {
		disableHeatChannelMap(true);
	}
	doChangeRssiThreshold();
}
function targetAppChanged(cb) {
	var ta = cb.value;
	var st = 80;
	if (ta == 200) {
		st = 70;
	} else if (ta == 300) {
		st = 67;
	} else if (ta == 400) {
		st = 62;
	}
	document.forms[formName].rssiThreshold.value = st;
	var sti = document.forms[formName].rssiThreshold.selectedIndex;
	document.forms[formName].autoRssiThreshold.selectedIndex = sti;
	document.forms[formName].intRssiThreshold.selectedIndex = sti;
	doChangeRssiThreshold();
}
function adjustTargetApp() {
	var st = document.forms[formName].autoRssiThreshold.value;
	var ta = 400;
	if (st > 62) {
		ta = 300;
	}
	if (st > 67) {
		ta = 200;
	}
	if (st > 75) {
		ta = 100;
	}
	document.forms[formName].targetApp.value = ta;
}
function toggleFullScreen() {
	document.forms[formName].mapOps.disabled = true;
	var td = document.getElementById("note");
	var msg = "Resizing ...";
	hm.util.replaceChildren(td, document.createTextNode(msg));
	hm.util.show('processing');
	if (fullScreenMode) {
		document.location.href = mapsActionOperationUrl + "viewNoFullScreen";
	} else {
		document.location.href = mapsActionOperationUrl + "viewFullScreen";
	}
}
function onCheckedButtonChange(event) {
	var button = this.get("checkedButton");
	var tab = button.get("value");
	hm.util.toggleHideElement("mapTabPanel", tab != "map" && tab != "dvs");
	hm.util.toggleHideElement("wallsTabPanel", tab != "walls");
	hm.util.toggleHideElement("apsTabPanel", tab != "aps");
	hm.util.toggleHideElement("autoApsTabPanel", tab != "autoAps");
	if (tab == "dvs") {
		hm.util.toggleHideElement("actualDims", true);
		hm.util.toggleHideElement("actualDim2", true);
		hm.util.toggleHideElement("actualDim2b1", true);
		hm.util.toggleHideElement("actualDim2b2", true);
		hm.util.toggleHideElement("actualDim2a", true);
		hm.util.toggleHideElement("actualDim2c", true);
		// var div = document.getElementById("moveAPsPanel");
		// div.style.position = "absolute";
		// div.style.top = 70 + "px";
		// div.style.left = 3 + "px";
		// div.style.width = top.actualViewportWidth + "px";
		// div.style.height = (top.actualViewportHeight + 2) + "px";
		// div.style.backgroundColor = "#fff";
		document.getElementById("dvsLbl").innerHTML = "Devices on '"
				+ selectedNode.label + "'";
		moveAPsPanel.header.innerHTML = "Select Devices for '"
				+ selectedNode.label + "'";
		moveAPsPanel.cfg.setProperty('visible', true);
		resetApListItems();
	} else {
		if (moveAPsPanel.cfg.getProperty('visible')) {
			moveAPsPanel.cfg.setProperty('visible', false);
			showActualDims();
		}
	}
}
function closeMoveAPsPanel() {
	if (mapGroup.get("checkedButton").get("value") == "dvs") {
		mapGroup.check(0);
		showActualDims();
	}
}
function resetApListItems() {
	disableApListButtons(true);
	var select = document.getElementById('leftAPs');
	select.options.length = 0;
	var url = "mapAps.action?operation=avlb" + hm.map.addCanvasAttrs();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
		success : avlbDone
	});
	var select = document.getElementById('rightAPs');
	select.options.length = 0;
	var options = new Array();
	for ( var i = 0; i < hm.map.wlanNodes.length; i++) {
		var node = hm.map.wlanNodes[i];
		if (!node.jsonNode.container && node.jsonNode.apId.charAt(0) != 'M') {
			options[options.length] = {
				text : node.jsonNode.apName,
				value : node.jsonNode.nodeId.substring(1)
			};
		}
	}
	options.sort(hm.map.sortByText);
	for ( var i = 0; i < options.length; i++) {
		select.options[select.options.length] = new Option(options[i].text,
				options[i].value);
	}
}
function avlbDone(o) {
	eval("var data = " + o.responseText);
	if (!data || data.pageId != hm.map.pageId) {
		return;
	}
	var select = document.getElementById('leftAPs');
	for ( var i = 0; i < data.aps.length; i++) {
		select.options[select.options.length] = new Option(data.aps[i].nm,
				data.aps[i].id);
	}
}
function disableApListButtons(flag) {
	document.getElementById('updateApList').disabled = flag;
	document.getElementById('resetApList').disabled = flag;
}
function moveApListItems(srcElem, tgtElem) {
	moveAPsChanged = true;
	var len = srcElem.length;
	hm.map.moveSelectedOptions(srcElem, tgtElem, true, '', '', 0);
	if (len != srcElem.length) {
		disableApListButtons(false);
	}
}
function updateApListItems() {
	disableApListButtons(true);
	disableReadOnlyGroups(true);
	moveAPsPanel.cfg.setProperty('visible', false);
	hm.util.show('processing');
	var select = document.getElementById('rightAPs');
	var fd = "";
	for ( var i = 0; i < select.options.length; i++) {
		fd += "&selectedIds=" + select.options[i].value;
	}
	var url = "mapAps.action?operation=upaps" + hm.map.addCanvasAttrs();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {
		success : upapsDone
	}, fd);
}
function upapsDone(o) {
	eval("var data = " + o.responseText);
	if (data) {
	}
	hm.map.loadMap(hm.map.mapId);
}

var mapGroup = null;
var mapBtnWalls, mapBtnDev, mapBtnAPs, mapBtnAuto;
function createMapTabs(sized, pm) {
	var btnDev = {
		label : "Devices",
		value : "dvs"
	};
	var btnAPs = {
		label : "Planned APs",
		value : "aps"
	};
	var btnAuto = {
		label : "Auto Placement",
		value : "autoAps"
	};
	if (mapGroup == null) {
		mapGroup = new YAHOO.widget.ButtonGroup({
			id : "mapGroupId",
			name : "mapGroupName",
			container : "mapTabs"
		});
		mapGroup.addButtons([ {
			label : "View",
			value : "map",
			checked : true
		}, {
			label : "Walls",
			value : "walls"
		}, btnDev, btnAPs, btnAuto ]);
		mapGroup.on("checkedButtonChange", onCheckedButtonChange);
		mapBtnWalls = mapGroup.getButton(1);
		mapBtnDev = mapGroup.getButton(2);
		mapBtnAPs = mapGroup.getButton(3);
		mapBtnAuto = mapGroup.getButton(4);
	}
	if (!sized || !pm || mapGroup.get("checkedButton").get("value") == "dvs") {
		mapGroup.check(0);
	}
	mapBtnWalls.set("disabled", !sized);
	if (apCount == 0 || !sized || hm.map.simNodes.length > 0) {
		if (mapGroup.getCount() % 2 > 0) {
			mapGroup.removeButton(2);
		}
	} else {
		if (mapGroup.getCount() % 2 == 0) {
			removePlannedPlacementBtns();
			mapBtnDev = mapGroup.addButton(btnDev);
		} else {
			mapBtnDev.set("disabled", false);
		}
	}
	if (!sized || !pm) {
		removePlannedPlacementBtns();
	} else {
		if (mapGroup.getCount() < 4) {
			mapBtnAPs = mapGroup.addButton(btnAPs);
			mapBtnAuto = mapGroup.addButton(btnAuto);
		} else {
			mapBtnAPs.set("disabled", false);
			mapBtnAuto.set("disabled", false);
		}
	}
}
function removePlannedPlacementBtns() {
	var first = mapGroup.getCount() - 2;
	if (first > 1) {
		mapGroup.removeButton(first);
		mapGroup.removeButton(first);
	}
}
function disableReadOnlyGroups(disabled) {
	mapBtnWalls.set("disabled", disabled);
	mapBtnDev.set("disabled", disabled);
	mapBtnAPs.set("disabled", disabled);
	mapBtnAuto.set("disabled", disabled);
}
function doAutoSimAps() {
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document.createTextNode(autoPlacementMsg));
	hm.util.show('processing');
	invalidateHiddenMaps();
	if (mapGroup.getCount() == 5) {
		mapGroup.removeButton(2);
	}
	hm.map.autoSimAps(document.forms[formName].defApModel.value,
			document.forms[formName].defWifi0Power.value,
			document.forms[formName].defWifi0Channel.value,
			document.forms[formName].defWifi1Power.value,
			document.forms[formName].defWifi1Channel.value);
}
function doAutoPlaceAps(btn) {
	cancelWallOps();
	btn.disabled = true;
	doAutoSimAps();
}
function doRemoveAllAps(btn) {
	thisOperation = "removeAllAPs";
	confirmDialog.cfg.setProperty('text',
			"<html><body>Are you sure you want to remove all APs on "
					+ selectedNode.label + " ?</body></html>");
	confirmDialog.show();
}
function confirmRemoveAllAps() {
	cancelWallOps();
	var btn = document.getElementById('removeAllAps');
	btn.disabled = true;
	btn = document.getElementById('autoRemoveAllAps');
	btn.disabled = true;
	hm.map.removeAllSimAps();
}
function doRemovePerimeter(nodeId) {
	var pi = 0;
	if (nodeId) {
		pi = parseInt(nodeId.substring(nodeId.indexOf('_') + 1));
	}
	if (hm.map.newPerim) {
		hm.map.newPerim = false;
	} else {
		removePerimeter(pi);
	}
	hm.map.removePerimById(pi);
}
function doCreatePerimeter() {
	hm.map.showWallLayer(true);
	cancelMeasuring();
}
function doTogglePerimeter(btn) {
	var close = "Close Perimeter (Dbl Click)";
	if (btn.value == close) {
		if (hm.map.perim.length > 0) {
			var wall = hm.map.perim[hm.map.perim.length - 1];
			if (wall.shape) {
				hm.map.canvasWindow.removeShape(hm.map.surface, wall.shape);
				wall.shape = null;
			}
		}
		hm.map.wallDblClick();
	} else {
		hm.map.wallDblClick(); // In case wall mode is still on
		cancelWallOps();
		btn.style.width = "170px";
		btn.value = close;
		btn.blur();
		doCreatePerimeter();
	}
}
function toggleDrawPerimeter() {
	var btn = document.getElementById('togglePerimeter');
	btn.disabled = false;
	var draw = "Draw Perimeter";
	if (btn.value != draw) {
		btn.style.width = "110px";
		btn.value = draw;
	}
	btn.blur();
}
function disablePerimeter() {
	document.getElementById('togglePerimeter').disabled = true;
}
function doRemoveAllWalls(btn) {
	thisOperation = "removeAllWalls";
	confirmDialog.cfg
			.setProperty('text',
					"<html><body>Are you sure you want to remove all walls ?</body></html>");
	confirmDialog.show();
}
function confirmedRemoveAllWalls() {
	cancelWallOps();
	var btn = document.getElementById('removeAllWalls');
	btn.disabled = true;
	hm.map.removeAllWalls();
}
function toggleMesh(cb) {
	hm.map.mesh = cb.checked;
	if (cb.checked) {
		hm.map.drawLinks();
	} else {
		hm.map.hideLinks();
	}
}
function toggleEthernet(cb) {
	hm.map.ethernet = cb.checked;
	if (cb.checked) {
		hm.map.drawEth();
	} else {
		hm.map.hideEth();
	}
}
function wallSettings() {
	alert("CJS Overlay for wall settings.");
}
// Import/Export the Planning Data
function requestPlanningData(mapId) {
	var YUC = YAHOO.util.Connect;
	var planningDataResult = function(o) {
		hm.util.hide('processing');
		eval("var result = " + o.responseText);
		if (result.succ) {
			showHangMessage(result.msg + "&nbsp;&nbsp;&nbsp;&nbsp;");
			var btn = document.getElementById("downloadBtn");
			if (btn && result.fileName) {
				btn.onclick = function() {
					document.forms['maps'].fileName.value = result.fileName;
					submitAction('maps', 'download');
				};
				hm.util.show('downloadSection');
			}
		} else {
			showErrorMessage(result.msg);
		}
	}
	var td = document.getElementById("note");
	if ('undefined' == td) {
		td = top.document.getElementById("note");
	}
	hm.util.replaceChildren(td, document.createTextNode("Creating Data..."));
	if (!mapId) {
		mapId = hm.map.mapId;
	}
	var url = mapsActionOperationUrl + "createPlanningData&id=" + mapId
			+ "&ignore=" + new Date().getTime();
	YUC.asyncRequest("GET", url, {
		success : planningDataResult
	}, null);
	showProcessing();
	
	initNoteSection();
}
var uploadXmlPanel;
function displayUploadXmlPanel(mapContainerId) {
	var uploadEl = document.getElementById("xmlUpload_uploadXml"), overrideBgEl = document
			.getElementById("overrideBgChk"), overrideBgRowEl = document
			.getElementById("overrideBgRow");

	if (null == uploadXmlPanel) {
		// create panel
		var div = document.getElementById('importXmlPanel');
		uploadXmlPanel = new YAHOO.widget.Panel(div, {
			visible : false,
			fixedcenter : true,
			// close: false,
			draggable : false,
			// modal:true,
			constraintoviewport : true,
			underlay : "none",
			zIndex : 1
		});
		uploadXmlPanel.render(document.body);
		div.style.display = "";

		YAHOO.util.Event.addListener(uploadEl, "change", function() {
			var text = this.value.trim();
			if (text.length > 0 && text.lastIndexOf(".tar") >= 0) {
				overrideBgRowEl.style.display = "";
			} else {
				overrideBgEl.checked = false;
				overrideBgRowEl.style.display = "none";
			}
		});
	}
	hm.util.hideFieldError();
	uploadXmlPanel.cfg.setProperty('visible', true);
	if (uploadEl) {
		uploadEl.value = "";
	}
	if (overrideBgEl) {
		overrideBgEl.checked = false;
		overrideBgRowEl.style.display = "none";
	}
	document.forms["xmlUpload"].id.value = mapContainerId;
}
function uploadXmlPlanningData() {
	var YUC = YAHOO.util.Connect;
	var errorRow = new Object();
	errorRow.id = 'ErrorMsgRow';
	var uploadXmlPlanningDataSuccess = function(o) {
		if (waitingPanel != null) {
			waitingPanel.hide();
		}
		try {
			eval("var result = " + o.responseText);
			if (result.succ) {
				// refresh
				document.location.reload();
			} else {
				reportFileError(errorRow, result.msg);
			}
		} catch(e) {
			top.showWarnDialog(o.responseText);
		}
	}
	var uploadEl = document.getElementById("xmlUpload_uploadXml");
	if (!validateUploadFileExtension(uploadEl, [ ".tar", ".xml" ])) {
		hm.util.reportFieldError(errorRow, "The file format is invalid.");
		return;
	}
	YUC.setForm("xmlUpload", true, true);
	var transaction = YUC.asyncRequest("post", xmlUploadUrl, {
		upload : uploadXmlPlanningDataSuccess
	}, null);
	if (waitingPanel != null) {
		waitingPanel.show();
	}
}
function reportFileError(element, message) {
	// Reset the timer
	clearTimeout(hm.util.fieldErrorTimeoutId);
	// Field has had error message before, just change the text
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (fe) {
		var span = document.getElementById("text" + feId);
		span.innerHTML = message;
		if (hm.util.vectorContains(hm.util.fieldErrorIds, feId)) {
			// Field error message is still visible
		} else {
			hm.util.fieldErrorIds.push(feId);
			hm.util.showFieldError(feId);
		}
		hm.util.delayHideFieldError(2);
	}
}
function isNull(element) {
	if (element == 'undefined' || null == element) {
		return true;
	}
	return false;
}
function validateUploadFileExtension(fileEl, extensions) {
	if (isNull(fileEl) || isNull(extensions)) {
		return false;
	}
	var filePath = fileEl.value.toLowerCase();
	if (typeof (extensions) === 'object' && extensions instanceof Array) {
		for ( var index = extensions.length - 1; index >= 0; index--) {
			if (filePath.search(extensions[index].toLowerCase()) != -1) {
				return true;
			}
		}
	} else {
		if (filePath.search(extensions.toLowerCase()) != -1) {
			return true;
		}
	}
	return false;
}

var gOldError = window.onerror;
window.onerror = function myErrorHandler(errMsg, url, lineNum, colNum, error) {
	if(gOldError)
		return gOldError(errMsg, url, lineNum);
	
	var lErrMsg = errMsg.toLowerCase();
	if(lErrMsg.indexOf("syntax error") != -1 || lErrMsg.indexOf("syntaxerror") != -1) {
		// reload the page if syntax error (happened when parsing ajax response)
		//document.location.reload();
        
    	var noteTD = document.getElementById("noteTD");
    	YAHOO.util.Dom.removeClass(noteTD, "noteError");
    	noteTD.innerHTML = "Opps, there is a script error on this page! <a href=\"javascript: void(0);\" onclick=\"document.location.reload();\">Reload</a>";
    	hm.util.show("noteSection");
	}
	return false;
}