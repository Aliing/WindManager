<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script src="<s:url value="/js/hm.map.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css"/>" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-beta-min.js" />"></script>
<style type="text/css">
<!--
    #cliInfoPanel .bd {
        padding:0;
    }
    #cliInfoPanel .bd_top{
        background-color:#eee;
    }
    #cliInfoPanel .cli_viewer{
    	padding: 10px;
        overflow:auto;
        height:25em;
    	font-family: sans-serif, Arial, Helvetica, Verdana;
        background-color:#fff;
    }
    #cliInfoPanel .ft {
        height:15px;
        padding:0;
    }
    #cliInfoPanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }
    #detailsPanel .bd,
    #globalPanel .bd {
    	overflow: auto;
    }
-->
</style>

<script>
var formName = 'maps';
var setting_formName = 'mapSettings';
var upload_formName = 'mapUpload';
function submitAction(formName, operation) {
	if(validate(operation)){
		if (operation == "saveNodes") {
			saveNodes()
			return;
		} else if (operation == "create") {
			createNode();
			return;
		} else if (operation == "cancel_map") {
			if (detailsPanel != null) {
				detailsPanel.cfg.setProperty('visible', false);
			}
			return;
		} else if (operation == "cancel_global"){
			if (globalPanel != null) {
				globalPanel.cfg.setProperty('visible', false);
			}
			return;
		} else if (operation == "cancel_addDel"){
			if (addDelPanel != null) {
				addDelPanel.cfg.setProperty('visible', false);
			}
			return;
		} else if (operation == "cancel_review"){
			if (imageReviewPanel != null) {
				imageReviewPanel.cfg.setProperty('visible', false);
			}
			return;
		} else if (operation == "createMap") {
			checkMapName("createMap")
			return;
		} else if (operation == "updateMap") {
			checkMapName("updateMap")
			return;
		} else if (operation == "uploadImage") {
//			uploadBackgroundImage();
//			return;
		}
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function validate(operation){
	if(!validateMapName(operation)){
		return false;
	}
	if(!validateUploadImage(operation)){
		return false;
	}
	return true;
}

function validateMapName(operation){
	if(operation == 'createMap' || operation == 'updateMap'){
		var mapNameElement = document.getElementById("mapName");
		// Device should support names with blanks starting from R3
		var message = hm.util.validateNameWithBlanks(mapNameElement.value, '<s:text name="topology.map.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(mapNameElement, message);
        	mapNameElement.focus();
        	return false;
    	}
	}
	return true;
}

function validateUploadImage(operation){
	if(operation == 'uploadImage'){
		var uploadImageElement = document.getElementById("imageFile");
		if(uploadImageElement.value.toLowerCase().search(".jpg") == -1
			&& uploadImageElement.value.toLowerCase().search(".png") == -1){
			hm.util.reportFieldError(uploadImageElement, '<s:text name="error.map.background.image.format"></s:text>');
			uploadImageElement.focus();
			return false;
		}
	}
	return true;
}

function setGridBorder(actualHeight) {
	if (document.forms[formName].grid == undefined) {
		return;
	}
	if (document.forms[formName].grid.checked && actualHeight > 0) {
		gridBorderX = 15;
		while (actualHeight >= 100) {
			gridBorderX += 7;
			actualHeight /= 10;
		}
		gridBorderY = 15;
	} else {
		gridBorderX = 0;
		gridBorderY = 0;
	}
	var div = document.getElementById("actualDims");
	var td = document.getElementById("zoomLabel");
	if (actualHeight > 0) {
		div.style.display = "";
		td.style.display = "none";
		document.forms[formName].heat.checked = false;
		document.forms[formName].channel.checked = false;
		document.forms[formName].clients.checked = false;
	} else {
		div.style.display = "none";
		td.style.display = "";
	}
}

var canvasWidth;
var canvasHeight;
var canvasBackground;
var scaleDelta = -1;
var gridBorderX = 0;
var gridBorderY = 0;
var gridSize;
var lengthUnit;
var actualGridSize;
var actualHeight;

function onLoadPage() {
//	alert("version: " + YAHOO.env.getVersion("dom").version);
	// Load icon images
	hm.map.loadIcons("<s:url includeParams="none" value="/images" />");
	// hm.map.loadImageBaseUrl("<s:url includeParams="none" value="/images" />");

	// load summary overlay pop up flag
	hm.map.loadPopUpFlag(<s:property value="%{popUpFlag}"/>);

	// Overlay for create/edit maps
	createMapDetailsPanel();
	// Overlay for sizing maps
	createMapSizePanel();
	// Overlay for add/delete images
	createMapAddDelPanel();
	// Overlay for images review
	createImageReviewPanel();
	// Overlay for global settings
	createMapGlobalPanel();
	// Overlay for confirm delete map dialog
	createConfirmDialog();
	// Overlay for set interval dialog
	createInfoDialog();
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

	// Subscribe for node selected event
	dojo.event.topic.subscribe("mapSelected", mapSelected);

	// Select top level map
	var mapSelector = dojo.widget.getWidgetById("mapSelector");
	// If current map is selected again, refresh it also.
	mapSelector.eventNames.dblselect = "mapSelected";
	//	alert("selected node: " + mapSelector.selectedNode);
	var mapHierarchy = dojo.widget.getWidgetById("mapHierarchy");
	var readNodes = new Array();
	var writeNodes = new Array();
	var globalReadNodes = new Array();
	var globalWriteNodes = new Array();
	var floorReadNodes = new Array();
	var floorWriteNodes = new Array();
	findTriggerNodes(mapHierarchy.children, readNodes, writeNodes, globalReadNodes,
					 globalWriteNodes, floorReadNodes, floorWriteNodes);
	createContextMenu(readNodes, writeNodes, globalReadNodes, globalWriteNodes,
					  floorReadNodes, floorWriteNodes);
	var selectedMapId = <s:property value="%{selectedMapId}"/>;
	var selectedMap = null;
	if (selectedMapId > 0) {
		selectedMap = findMapNode(mapHierarchy.children, selectedMapId);
	}
	if (selectedMap == null && mapHierarchy.children.length > 0) {
		selectedMap = mapHierarchy.children[0];
	}
	if (selectedMap != null) {
		mapSelector.doSelect(selectedMap);
		hm.map.mapId = selectedMap.widgetId;
		hm.map.requestMapDetails(processRootMapDetails);
//		no need to publish this event, because a different Ajax callback function is required
//		dojo.event.topic.publish(mapSelector.eventNames.select, {node: rootMap} );
		if (hm.map.popUpFlag) {
			//retrieve selected Map network summary info
			displaySummaryPanel(hm.map.mapId, selectedMap.title);
		}
	}
	window.onresize = refreshMap;

}
function mapSelected(message) {
	hm.map.loadMap(message.node.widgetId);
}
function findTriggerNodes(nodeList, readNodes, writeNodes, globalReadNodes,
						  globalWriteNodes, floorReadNodes, floorWriteNodes) {
	for (var i = 0; i < nodeList.length; i++) {
		var writePermission = mps[nodeList[i].widgetId];
		var levelNum = mls[nodeList[i].widgetId];
		if (levelNum == 1) {
			if (writePermission) {
				globalWriteNodes[globalWriteNodes.length] = nodeList[i].labelNode;
			} else {
				globalReadNodes[globalReadNodes.length] = nodeList[i].labelNode;
			}
		} else if (levelNum == 7) {
			//limit the maximum number of levels to 7.
			if (writePermission) {
				floorWriteNodes[floorWriteNodes.length] = nodeList[i].labelNode;
			} else {
				floorReadNodes[floorReadNodes.length] = nodeList[i].labelNode;
			}
		} else {
			if (writePermission) {
				writeNodes[writeNodes.length] = nodeList[i].labelNode;
			} else {
				readNodes[readNodes.length] = nodeList[i].labelNode;
			}
		}
		findTriggerNodes(nodeList[i].children, readNodes, writeNodes, globalReadNodes,
						 globalWriteNodes, floorReadNodes, floorWriteNodes);
	}
}
function findMapNode(nodeList, mapId) {
	for (var i = 0; i < nodeList.length; i++) {
		if (nodeList[i].widgetId == mapId) {
			return nodeList[i];
		}
		var mapNode = findMapNode(nodeList[i].children, mapId);
		if (mapNode != null) {
			return mapNode;
		}
	}
	return null;
}
function toggleWidthUnit(select) {
	var text = document.getElementById('mapWidth');
	if (text.value.length != 0) {
		var feetToMeters = 0.3048;
		if (select.value == 2) {
			text.value /= feetToMeters;
		} else {
			text.value *= feetToMeters;
		}
	}
}
var detailsPanel = null;
function createMapDetailsPanel() {
	var div = document.getElementById('detailsPanel');
	detailsPanel = new YAHOO.widget.Panel(div, {
		width:"450px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		constraintoviewport:true
// font on IE is not crisp when using FADE
//		,effect:{
//			effect:YAHOO.widget.ContainerEffect.FADE,
//			duration:0.25
//			}
		});
	detailsPanel.render(document.body);
	div.style.display = "";
}
var addDelPanel = null;
function createMapAddDelPanel() {
	var div = document.getElementById('addDelPanel');
	addDelPanel = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		constraintoviewport:true
// font on IE is not crisp when using FADE
//		,effect:{
//			effect:YAHOO.widget.ContainerEffect.FADE,
//			duration:0.25
//			}
		});
	addDelPanel.render(document.body);
	div.style.display = "";
}

var imageReviewPanel = null;
function createImageReviewPanel() {
	var div = document.getElementById('imageReviewPanel');
	imageReviewPanel = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:5
		});
	imageReviewPanel.render(document.body);
	div.style.display = "";
}

var globalPanel = null;
function createMapGlobalPanel() {
	var div = document.getElementById("globalPanel");
	globalPanel = new YAHOO.widget.Panel(div, {
		width:"400px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		constraintoviewport:true
// font on IE is not crisp when using FADE
//		,effect:{
//			effect:YAHOO.widget.ContainerEffect.FADE,
//			duration:0.25
//			}
		});
	globalPanel.render(document.body);
	div.style.display = "";
}
var confirmDialog = null;
function createConfirmDialog() {
	confirmDialog = new YAHOO.widget.SimpleDialog("deletionDlg", {
		width: "300px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport:true,
		icon: YAHOO.widget.SimpleDialog.ICON_WARN,
		buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
				   { text:"&nbsp;No&nbsp;",  handler:handleNo }
				 ]
		});
	confirmDialog.render(document.body);
}
var infoDialog = null;
function createInfoDialog() {
	infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "350px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon: YAHOO.widget.SimpleDialog.ICON_INFO,
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]
		});
	infoDialog.render(document.body);
}

function showReviewPanel(){
	reviewImages();
	if (imageReviewPanel != null) {
		imageReviewPanel.cfg.setProperty('visible', true);
	}
}


function onUnloadPage() {
}
function createContextMenu(readNodes, writeNodes, globalReadNodes, globalWriteNodes,
						   floorReadNodes, floorWriteNodes) {
	if( readNodes.length > 0 ){
		var readNodeMenu = new YAHOO.widget.ContextMenu("readNodeMenu", { trigger: readNodes });
		readNodeMenu.clickEvent.subscribe(onMenuItemClick);
		readNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		readNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New", { disabled: true }));
		readNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit", { disabled: true }));
		readNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove", { disabled: true }));
		readNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		readNodeMenu.render(document.body);
	}

	if( writeNodes.length > 0 ){
		var writeNodeMenu = new YAHOO.widget.ContextMenu("writeNodeMenu", { trigger: writeNodes });
		writeNodeMenu.clickEvent.subscribe(onMenuItemClick);
		writeNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		writeNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New"));
		writeNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		writeNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove"));
		writeNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		writeNodeMenu.render(document.body);
	}

	if( globalReadNodes.length > 0 ){
		var globalReadNodeMenu = new YAHOO.widget.ContextMenu("globalReadNodeMenu", { trigger: globalReadNodes });
		globalReadNodeMenu.clickEvent.subscribe(onMenuItemClick);
		globalReadNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New", { disabled: true }));
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit", { disabled: true }));
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove", { disabled: true }));
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Global Settings", { disabled: true }));
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Add/Delete Image", { disabled: true }));
		globalReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		globalReadNodeMenu.render(document.body);
	}

	if( globalWriteNodes.length > 0 ){
		var globalWriteNodeMenu = new YAHOO.widget.ContextMenu("globalWriteNodeMenu", { trigger: globalWriteNodes });
		globalWriteNodeMenu.clickEvent.subscribe(onMenuItemClick);
		globalWriteNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New"));
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove", { disabled: true }));
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Global Settings"));
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Add/Delete Image"));
		globalWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		globalWriteNodeMenu.render(document.body);
	}

	if( floorReadNodes.length > 0 ){
		var floorReadNodeMenu = new YAHOO.widget.ContextMenu("floorReadNodeMenu", { trigger: floorReadNodes });
		floorReadNodeMenu.clickEvent.subscribe(onMenuItemClick);
		floorReadNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		floorReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New", { disabled: true }));
		floorReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit", { disabled: true }));
		floorReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove", { disabled: true }));
		floorReadNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		floorReadNodeMenu.render(document.body);
	}

	if( floorWriteNodes.length > 0 ){
		var floorWriteNodeMenu = new YAHOO.widget.ContextMenu("floorWriteNodeMenu", { trigger: floorWriteNodes });
		floorWriteNodeMenu.clickEvent.subscribe(onMenuItemClick);
		floorWriteNodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
		floorWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("New", { disabled: true }));
		floorWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Edit"));
		floorWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Remove"));
		floorWriteNodeMenu.addItem(new YAHOO.widget.ContextMenuItem("Network Summary"));
		floorWriteNodeMenu.render(document.body);
	}
}
var targetId;
function onMenuTrigger(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	if (event.target) {
		targetNode = event.target;
	} else {
		targetNode = event.srcElement;
	}
	targetId = targetNode.parentNode.getAttribute("treenode");
}
function onMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	var targetMap = dojo.widget.getWidgetById(targetId);
	var mapName = targetMap.title;

	//menu item is disabled, do nothing.
	if(menuItem.cfg.getProperty("disabled") == true){
	//	alert("This menu item is disabled.");
		return;
	}

	if (menuItem.cfg.getProperty("text") == "New") {
		document.forms[setting_formName].id.value = targetId;
		document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
		newInformation(targetId);
		displaydetailsPanelButton(false, mapName);
		detailsPanel.cfg.setProperty('visible', true);
		document.getElementById("mapName").focus();
	} else if (menuItem.cfg.getProperty("text") == "Edit"){
		document.forms[setting_formName].id.value = targetId;
		document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
		editInformation(targetId);
		displaydetailsPanelButton(true, mapName);
		detailsPanel.cfg.setProperty('visible', true);
		document.getElementById("mapName").focus();
	} else if (menuItem.cfg.getProperty("text") == "Remove"){
		if (confirmDialog != null) {
			confirmDialog.setHeader("Warning");
			confirmDialog.cfg.setProperty("text","<html><body>Are you sure you want to delete <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'"+ mapName + "'?</body></html>");
			confirmDialog.show();
			handleType = 'removeMap';
		}
	} else if (menuItem.cfg.getProperty("text") == "Global Settings") {
		editGlobalParams(targetId);
		globalPanel.cfg.setProperty('visible', true);
		document.getElementById("refreshInterval").focus();
	} else if (menuItem.cfg.getProperty("text") == "Network Summary") {
		displaySummaryPanel(targetId, mapName);
	} else if (menuItem.cfg.getProperty("text") == "Add/Delete Image") {
		document.forms[upload_formName].domainMapId.value = targetId;
		addDelPanel.cfg.setProperty('visible', true);
	}
}

var handleType;
var handleYes = function(){
	this.hide();
	if('removeMap' == handleType){
		checkDeletionRestrict();
	}else if ('removeImage' == handleType) {
		calDeleteImage();
	}
}
var handleNo = function(){
	this.hide();
}

function displaySummaryPanel(mapId,mapName){
	retrieveSummaryInfo(mapId);
	var dialogTitleDiv = document.getElementById("summaryTitle");
	dialogTitleDiv.innerHTML = "Network Summary - " + mapName;
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
}

function displaydetailsPanelButton(isEditDialog, mapName) {
	var createBtnElement = document.getElementById("createTd");
	var updateBtnElement = document.getElementById("updateTd");
	var dialogTitleDiv = document.getElementById("dialogTitle");
	var mapNameElement = document.getElementById("mapName");

	if(isEditDialog){
		createBtnElement.style.display = "none";
		updateBtnElement.style.display = "";
		dialogTitleDiv.innerHTML = "Edit Map - "+mapName;
	}else{
		createBtnElement.style.display = "";
		updateBtnElement.style.display = "none";
		dialogTitleDiv.innerHTML = "New Map (Submap for "+mapName+" )";
		mapNameElement.value = "";
	}
}

function toggleWriteButtons(writePermission) {
	var saveBtn = document.getElementById("saveNodesButton");
	saveBtn.disabled = !writePermission;
	var lockBtn = document.getElementById("toggleLockButton");
	lockBtn.value = "Unlock";
	lockBtn.disabled = !writePermission;
	var sizeBtn = document.getElementById("toggleSizeButton");
	sizeBtn.disabled = !writePermission;
}
function processRootMapDetails(o) {
	eval("var data = " + o.responseText);
	if (data) {
		hm.map.mapWritePermission = data.writePermission;
		toggleWriteButtons(data.writePermission);
		canvasBackground = data.bg;
		gridSize = data.gridSize;
		actualHeight = 0;
		if (gridSize > 0) {
			hm.map.setLengthUnit(data);
			actualGridSize = data.actualGridSize;
			actualHeight = data.actualHeight;
			actualScale = data.actualHeight / data.height;
			document.forms[formName].toggleSize.value = "Resize";
		}
		setGridBorder(actualHeight);
		hm.map.setViewportSize("viewport", data.width, data.height);
		hm.map.setCanvasSize(top, 1);
		moveSummaryPanel();
		if (viewport.location.href.indexOf("loadingMap") == -1) {
			viewport.location.replace(viewport.location + "Map");
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
function refreshMap() {
	submitAction("maps", "view");
}

function requestNodes(canvasWindow) {
	if (scaleDelta > 0) {
		// Client size re-scaling
		hm.map.zoomMapNodes(canvasWindow, scaleDelta);
	} else {
		// Fetch nodes from DB
		hm.map.requestMapNodes(canvasWindow);
	}
}

function createNode() {
	var newId = YAHOO.util.Dom.generateId();
	hm.map.addNodeFromUI(newId, 60, 50);
}

var movedNodes;
function saveNodes() {
	var form = document.createElement("form");
	movedNodes = hm.map.createNodeElements(form);
	if (movedNodes > 0) {
		var td = document.getElementById("note");
		hm.util.replaceChildren(td, document.createTextNode("Saving icon positions ..."/*"Saving " + movedNodes + " node position(s) ..."*/));
		hm.util.show('processing');
		submitFormElements("maps.action", form, nodesSaved);
	}
}

function nodesSaved() {
	movedNodeId = null;
	hm.map.clearNodeMoved();
	var td = document.getElementById("note");
	hm.util.replaceChildren(td, document.createTextNode("Saving icon positions ... done."/*"Saving " + movedNodes + " node position(s) ... done."*/));
	delayHideProcessing(1);
	if (document.forms[formName].clients.checked) {
		hm.map.removeClientNodes();
		hm.map.requestMapObjects('clients', hm.map.processClients, getFrequency());
	}
	toggleHeatChannelMap();
}

/*
 * Submit a form as an Ajax request
 */
function submitFormElements(url, form, callback) {
	var params = new Array();
	params['operation'] = "saveNodes";
	params['id'] = hm.map.mapId;
	params['scale'] = hm.map.scale;
	// To prevent browser from returning a cached version
	var dateId = new Date();
	params['ignore'] = new Date().getTime();
	var bindArgs = {
		url: url,
		error: function(type, data, evt){
			alert("Error in Ajax form submit: " + data);
		},
//		timeoutSeconds: 2,
//		timeout: function(type, data, evt){
//			alert("Ajax submit timed out.");
//		},
		content: params,
        formNode: form
	};
	var req = dojo.io.bind(bindArgs);
	dojo.event.connect(req, "load", callback);
}

var processingTimeoutId;
function delayHideProcessing(seconds) {
	processingTimeoutId = setTimeout("hideProcessing()", seconds * 1000);  // seconds
}
function hideProcessing() {
	dojo.lfx.html.wipeOut('processing', 200).play();
}
function onUnloadNotes() {
    clearTimeout(processingTimeoutId);
}
function onUnloadPage() {
	hm.map.clearAlarmsTimer();
//	hm.map.clearReloadViewportTimer();
}

var detailSuccess = function(o) {
	eval("var detail = " + o.responseText);
	var iconElement = document.getElementById("mapIcon");
	var imageElement = document.getElementById("mapImage");
	var mapNameElement = document.getElementById("mapName");
	var mapWidthElement = document.getElementById("mapWidth");
	var mapEnvElement = document.getElementById("mapEnv");
	var intervalElement = document.getElementById("refreshInterval");
	var reviewImageElement = document.getElementById("mapReviewImage");
	var popUpFlagElement = document.getElementById("popUpFlag");

	var icons = detail.icons;
	var images = detail.images;
	var map = detail.map;
	var msg = detail.msg;
	var interval = detail.interval;
	var flag = detail.flag;
	if(icons){
		iconElement.length = icons.length;
		for(var i=0; i< icons.length; i++){
			iconElement.options[i].value = icons[i].v;
			iconElement.options[i].text = icons[i].t;
		}
		if(iconElement.length > 0){
			iconElement.selectedIndex = 0;
		}
		//reset this selection value
		if(mapEnvElement.length > 1){
			mapEnvElement.selectedIndex = mapEnvElement.length - 2;
		}
		//reset this textfield value
		mapWidthElement.value = '';
	}
	if(images){
		imageElement.length = images.length;
		reviewImageElement.length = images.length;
		for(var i=0; i< images.length; i++){
			imageElement.options[i].value = images[i].t;
			imageElement.options[i].text = images[i].t;
			reviewImageElement.options[i].value = images[i].t;
			reviewImageElement.options[i].text = images[i].t;
		}
		if(imageElement.length > 0 ){
			imageElement.selectedIndex = 0;
		}
	}
	if (map) {
		mapEnvElement.value = map.mapEnv;
		mapNameElement.value = map.mapName;
		if (map.mapWidth == 0) {
			mapWidthElement.value = '';
		} else {
			mapWidthElement.value = map.mapWidth;
			var select = document.getElementById('mapWidthUnit');
			select.options[map.lengthUnit - 1].selected = true;
		}
		imageElement.value = map.image;
		iconElement.value = map.icon;
	}
	if(interval){
		intervalElement.value = interval;
	}
	if(flag != undefined){
		if(flag){
			popUpFlagElement.checked = true;
		}else{
			popUpFlagElement.checked = false;
		}
	}
	if(msg){
		if (infoDialog != null) {
			infoDialog.setHeader("Information");
			infoDialog.cfg.setProperty("text",msg);
			infoDialog.show();
		}
	}
}

var detailFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailSuccess,
	failure : detailFailed,
	upload : uploadResult
};

function editInformation(id) {
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=edit&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function newInformation(id) {
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=new&domainMapId=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function editGlobalParams(id) {
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=editGlobalParams&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function reviewImages() {
	var domainMapId = document.forms[upload_formName].domainMapId.value;
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=reviewImages&domainMapId=" + domainMapId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

//this function does not work as well ??
function uploadBackgroundImage() {
	var YUC = YAHOO.util.Connect;
	YUC.setForm(document.forms[setting_formName],true);
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=uploadImage&"+ "&ignore=" + new Date().getTime();
	alert(url);
	var transaction = YUC.asyncRequest('POST', url, callback, null);
}

var uploadResult = function (o) {
	eval("var result = " + o.responseText);
	alert(result);
}

function setGlobalParams(){
	var intervalTfd = document.getElementById("refreshInterval");
	var showOnHover = document.getElementById("showOnHover");
	var showRssi = document.getElementById("showRssi");
	var popUpFlagElement = document.getElementById("popUpFlag");
	var flagValue = popUpFlagElement.checked;
	if (intervalTfd.value.length == 0) {
		hm.util.reportFieldError(intervalTfd, '<s:text name="error.requiredField"><s:param><s:text name="topology.map.refresh.time" /></s:param></s:text>');
		intervalTfd.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(intervalTfd.value, '<s:text name="topology.map.refresh.time" />',
	                                           <s:property value="30" />,
	                                           <s:property value="900" />);
	if (message != null) {
		hm.util.reportFieldError(intervalTfd, message);
		intervalTfd.focus();
		return;
	}
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=setGlobalParams&refreshInterval=" + intervalTfd.value + "&id="+ targetId + "&ppFlag=" + flagValue + "&showRssi=" + showRssi.checked + "&showOnHover=" + showOnHover.checked + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:globalSuccess,failure:connectedFailed,argument:{ppFlag:flagValue} }, null);
	globalPanel.cfg.setProperty('visible', false);
	if (!flagValue) {
		networkSummaryPanel.cfg.setProperty('visible', false);
	}
}

var globalSuccess = function(o){
	eval("var result = " + o.responseText);
	if(result.msg){
		if (infoDialog != null) {
			infoDialog.setHeader("Information");
			infoDialog.cfg.setProperty("text",result.msg);
			infoDialog.show();
		}
	}
	hm.map.loadPopUpFlag(o.argument.ppFlag);
}

function deleteImage() {
	var imageElement = document.getElementById("mapReviewImage");
	var imageValue = imageElement.options[imageElement.selectedIndex].text;
	if (imageValue.length == 0) {
		hm.util.reportFieldError(imageElement, '<s:text name="error.pleaseSelect"><s:param><s:text name="topology.map.background.image" /></s:param></s:text>');
		imageElement.focus();
		return;
	}
	if (confirmDialog != null) {
		confirmDialog.setHeader("Warning");
		confirmDialog.cfg.setProperty("text","<html><body>Are you sure you want to delete<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'"+ imageValue + "'?</body></html>");
		confirmDialog.show();
		handleType = 'removeImage';
	}
}
function calDeleteImage(){
	var imageElement = document.getElementById("mapReviewImage");
	var imageValue = imageElement.options[imageElement.selectedIndex].text;
	var domainMapId = document.forms[upload_formName].domainMapId.value;
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=deleteImage&imageName=" + imageValue + "&domainMapId="+domainMapId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function checkMapName(operation) {
	var newMapName = document.getElementById("mapName").value;
	if(operation == "createMap"){
		url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=checkMapName&newMapName=" + newMapName + "&domainMapId=" + targetId + "&ignore=" + new Date().getTime();
	}else if (operation == "updateMap"){
		url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=checkMapName&newMapName=" + newMapName + "&domainMapId=" + targetId + "&id="+ targetId + "&ignore=" + new Date().getTime();
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:checkMapNameResult,failure:connectedFailed,argument:{opt:operation}}, null);
}

function checkDeletionRestrict() {
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=checkDeletionRestrict&id="+ targetId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:checkDeletionRestrictResult,failure:connectedFailed}, null);
}

var checkMapNameResult = function(o){
	eval("var result = " + o.responseText);
	if(result.v){
		var mapNameElement = document.getElementById("mapName");
		hm.util.reportFieldError(mapNameElement, result.v);
		return;
	}else{
		if (detailsPanel != null) {
			detailsPanel.cfg.setProperty('visible', false);
		}
		document.forms[setting_formName].operation.value = o.argument.opt;
		document.forms[setting_formName].submit();
	}
}

var checkDeletionRestrictResult = function(o){
	eval("var result = " + o.responseText);
	if(!result.removable){
		if (infoDialog != null) {
			infoDialog.setHeader("Error");
			infoDialog.cfg.setProperty("text",result.msg);
			infoDialog.show();
		}
		return;
	}else{
		document.forms[setting_formName].operation.value = "removeMap";
		document.forms[setting_formName].id.value = targetId;
		document.forms[setting_formName].selectedMapId.value = hm.map.mapId;
		document.forms[setting_formName].submit();
	}
}

var connectedFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

var clientInfoPanel = null;
function createClientInfoPanel() {
	var div = window.document.getElementById('clientInfoPanel');
	clientInfoPanel = new YAHOO.widget.Panel(div, { width:"620px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	clientInfoPanel.render(document.body);
	div.style.display = "";
}

var neighborInfoPanel = null;
function createNeighborInfoPanel() {
	var div = window.document.getElementById('neighborInfoPanel');
	neighborInfoPanel = new YAHOO.widget.Panel(div, { width:"620px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	neighborInfoPanel.render(document.body);
	div.style.display = "";
}
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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var networkSummaryPanel = null;
function createNetworkSummaryPanel() {
	var div = window.document.getElementById('networkSummaryPanel');
	networkSummaryPanel = new YAHOO.widget.Panel(div, { width:"350px", visible:false, draggable:true, constraintoviewport:true } );
	networkSummaryPanel.cfg.setProperty('x', document.body.clientWidth-360);
	networkSummaryPanel.cfg.setProperty('y', 125);
	networkSummaryPanel.render(document.body);
	div.style.display = "";
}

var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, { width:"450px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	cliInfoPanel.render(document.body);
	div.style.display = "";
	cliInfoPanel.hideEvent.subscribe(resetParams);
	// Create Resize instance, binding it to the 'cliInfoPanel' DIV
	var resize = new YAHOO.util.Resize('cliInfoPanel', {
	    handles: ['br'],
	    autoRatio: false,
	    minWidth: 400,
	    minHeight: 200,
	    status: true,
	    proxy: true
	});

	// Setup resize handler to update the size of the Panel's body element
	// whenever the size of the 'cliInfoPanel' DIV changes
	resize.on('resize', resizeCliPanel, cliInfoPanel, true);
}

function resizeCliPanel(obj){
    var panelHeight = obj.height;
    resetPanelSize(panelHeight, this);
}

function resetPanelSize(panelHeight, panel){
	// QUIRKS FLAG, FOR BOX MODEL
	var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

	// UNDERLAY/IFRAME SYNC REQUIRED
	var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

    var headerHeight = panel.header.offsetHeight; // Content + Padding + Border
    var footerHeight = panel.footer.offsetHeight; // Content + Padding + Border
    var bodyTopHeight = YAHOO.util.Dom.get('bd_top').offsetHeight;

    var bodyHeight = (panelHeight - headerHeight - footerHeight);
    var bodyContentHeight = (IE_QUIRKS) ? bodyHeight-bodyTopHeight : bodyHeight - 20 - bodyTopHeight;

    YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('cli_viewer'), 'height', bodyContentHeight + 'px');

    if (IE_SYNC) {
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

function resetParams(){
	//alert("reset params...");
	var interfaceEl = document.getElementById("interfaceType");
	interfaceEl.selectedIndex = 0;
}

var apInfoPanel = null;
function createAPInfoPanel() {
	var div = window.document.getElementById('apInfoPanel');
	apInfoPanel = new YAHOO.widget.Panel(div, { width:"450px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true, modal:false } );
	apInfoPanel.render(document.body);
	div.style.display = "";
}

function retrieveClientInfo(leafNodeId){
	leafNodeId = leafNodeId.replace(/n/,"");
	leafNodeId = leafNodeId.replace(/l/,"");
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=retrieveClientInfo&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:clientInfoResult,failure:connectedFailed,timeout: 60000 }, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function retrieveNeighborInfo(leafNodeId){
	leafNodeId = leafNodeId.replace(/n/,"");
	leafNodeId = leafNodeId.replace(/l/,"");
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=retrieveNeighborInfo&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:neighborInfoResult,failure:connectedFailed,timeout: 60000 }, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function requestSingleItemCli(menuText, leafNodeId){
	leafNodeId = leafNodeId.replace(/n/,"");
	leafNodeId = leafNodeId.replace(/l/,"");
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=requestSingleItemCli&leafNodeId=" + leafNodeId + "&menuText="+menuText+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000, argument:{leafNodeId:leafNodeId,menuText:menuText}}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function retrieveIfDetail() {
	var interfaceElement = document.getElementById("interfaceType");
	var item = interfaceElement.options[interfaceElement.selectedIndex].text;
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=requestSingleItemCli&leafNodeId=" + selectedLeafNodeId + "&menuText="+selectedMenuText+"&value1="+item+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000, argument:{leafNodeId:selectedLeafNodeId,menuText:selectedMenuText}}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
	document.getElementById("cli_viewer").innerHTML = '';
}

function ping() {
	var targetIpElement = document.getElementById("targetIp");
	if (targetIpElement.value.length == 0) {
        hm.util.reportFieldError(targetIpElement, '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
        targetIpElement.focus();
        return;
  	}
   	if (! hm.util.validateIpAddress(targetIpElement.value)) {
		hm.util.reportFieldError(targetIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
		targetIpElement.focus();
		return;
	}
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=requestSingleItemCli&leafNodeId=" + selectedLeafNodeId + "&menuText="+selectedMenuText+"&value1="+targetIpElement.value+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000, argument:{leafNodeId:selectedLeafNodeId,menuText:selectedMenuText}}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
	document.getElementById("cli_viewer").innerHTML = '';
}

function traceroute(){
	var tracerouteIpElement = document.getElementById("tracerouteIp");
	var tracerouteTimeout = document.getElementById("tracerouteTimeout");
	if (tracerouteIpElement.value.length == 0) {
        hm.util.reportFieldError(tracerouteIpElement, '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
        tracerouteIpElement.focus();
        return;
  	}
   	if (! hm.util.validateIpAddress(tracerouteIpElement.value)) {
		hm.util.reportFieldError(tracerouteIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
		tracerouteIpElement.focus();
		return;
	}
	if (tracerouteTimeout.value.length == 0) {
        hm.util.reportFieldError(tracerouteTimeout, '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.diagnostics.traceroute.timeout" /></s:param></s:text>');
        tracerouteTimeout.focus();
        return;
  	}
	var message = hm.util.validateIntegerRange(tracerouteTimeout.value, '<s:text name="topology.menu.diagnostics.traceroute.timeout" />',
	                                           <s:property value="10" />,
	                                           <s:property value="120" />);
	if (message != null) {
		hm.util.reportFieldError(tracerouteTimeout, message);
		tracerouteTimeout.focus();
		return;
	}
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=requestSingleItemCli&leafNodeId="
		+ selectedLeafNodeId + "&menuText="+selectedMenuText+"&value1="
		+ tracerouteIpElement.value + "&value2="+ tracerouteTimeout.value +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000, argument:{leafNodeId:selectedLeafNodeId,menuText:selectedMenuText}}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
	document.getElementById("cli_viewer").innerHTML = '';
}

function retrieveAlarmInfo(leafNodeId){
	leafNodeId = leafNodeId.replace(/n/,"");
	leafNodeId = leafNodeId.replace(/l/,"");
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=alarm&leafNodeId=" + leafNodeId +"&ignore="+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:alarmInfo,failure:null }, null);

}

function retrieveSummaryInfo(mapId){
	url = "<s:url action='mapSettings' includeParams='none' />" + "?operation=retrieveSummaryInfo&id=" + mapId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: summaryInfoResult, failure:null}, null);
}

var summaryInfoResult = function(o) {
	eval("var result = " + o.responseText);
	setupSummaryInfo(result);
}

function setupSummaryInfo(result){
	//set up HiveAp count;
	if(result.m_up){
		var up_td = document.getElementById("upManagedHiveApTd");
		var up_a = document.createElement("a");
		var up_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=view&hmListType=managedHiveAps&mapId=" + result.map_Id + "&isConnected=true" + "&ignore=" + new Date().getTime();
		up_a.setAttribute("href",up_url);
		up_a.innerHTML = result.m_up;
		up_td.innerHTML = '';
		up_td.appendChild(up_a);
	}
	if(result.n_up){
		var up_td = document.getElementById("upNewHiveApTd");
		var up_a = document.createElement("a");
		var up_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=view&hmListType=newHiveAps&mapId=" + result.map_Id + "&isConnected=true" + "&ignore=" + new Date().getTime();
		up_a.setAttribute("href",up_url);
		up_a.innerHTML = result.n_up;
		up_td.innerHTML = '';
		up_td.appendChild(up_a);
	}
	//set down HiveAp count;
	if(result.m_down){
		var down_td = document.getElementById("downManagedHiveApTd");
		var down_a = document.createElement("a");
		var down_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=view&hmListType=managedHiveAps&mapId=" + result.map_Id + "&isConnected=false" + "&ignore=" + new Date().getTime();
		down_a.setAttribute("href",down_url);
		down_a.innerHTML = result.m_down;
		down_td.innerHTML = '';
		down_td.appendChild(down_a);
	}
	if(result.n_down){
		var down_td = document.getElementById("downNewHiveApTd");
		var down_a = document.createElement("a");
		var down_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=view&hmListType=newHiveAps&mapId=" + result.map_Id + "&isConnected=false" + "&ignore=" + new Date().getTime();
		down_a.setAttribute("href",down_url);
		down_a.innerHTML = result.n_down;
		down_td.innerHTML = '';
		down_td.appendChild(down_a);
	}
	//set client count;
	if(result.client){
		var client_td = document.getElementById("activeClientTd");
		var client_a = document.createElement("a");
		var client_url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=view&mapContainerId=" + result.map_Id + "&ignore=" + new Date().getTime();
		client_a.setAttribute("href",client_url);
		client_a.innerHTML = result.client;
		client_td.innerHTML = '';
		client_td.appendChild(client_a);
	}
}

var alarmInfo = function(o) {
	eval("var result = " + o.responseText);
	var redirect_url = "<s:url action='alarms' includeParams='none' />" + "?operation=search&apId=" + result.v + "&ignore=" + new Date().getTime();
	window.location.replace(redirect_url);
}

var _abgModelItems = ['all','wifi0','wifi1','eth0','mgt0','wifi0.1','wifi0.2',
					 'wifi0.3','wifi0.4','wifi0.5','wifi0.6','wifi0.7','wifi1.1',
					 'wifi1.2','wifi1.3','wifi1.4','wifi1.5','wifi1.6','wifi1.7'];
var _11nModelItems = ['all','wifi0','wifi1','eth0','eth1','mgt0','agg0','red0',
					 'wifi0.1','wifi0.2','wifi0.3','wifi0.4','wifi0.5','wifi0.6',
					 'wifi0.7','wifi0.8','wifi1.1','wifi1.2','wifi1.3','wifi1.4',
					 'wifi1.5','wifi1.6','wifi1.7','wifi1.8'];
var updateInterfaceItems = function(is11n){
	var interfaceEl = document.getElementById("interfaceType");
	if(is11n){// the ap is 11n model
		interfaceEl.length = _11nModelItems.length;
		for(var i=0; i<_11nModelItems.length; i++){
			interfaceEl.options[i].value = _11nModelItems[i];
			interfaceEl.options[i].text = _11nModelItems[i];
		}
	}else{
		interfaceEl.length = _abgModelItems.length;
		for(var i=0; i<_abgModelItems.length; i++){
			interfaceEl.options[i].value = _abgModelItems[i];
			interfaceEl.options[i].text = _abgModelItems[i];
		}
	}
}

var selectedLeafNodeId;
var selectedMenuText;
var cliInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	selectedLeafNodeId = o.argument.leafNodeId;
	selectedMenuText = o.argument.menuText;
	var pingTr = document.getElementById("pingTr");
	var tracerouteTr = document.getElementById("tracerouteTr");
	var interfaceTr = document.getElementById("interfaceTr");
	if("<s:text name="topology.menu.statistics.interface"/>" == result.t){
		pingTr.style.display = "none";
		tracerouteTr.style.display = "none";
		interfaceTr.style.display = "";
		updateInterfaceItems(result.is11n);
	}else if ("<s:text name="topology.menu.diagnostics.ping"/>" == result.t) {
		pingTr.style.display = "";
		tracerouteTr.style.display = "none";
		interfaceTr.style.display = "none";
		if(result.ip){
			document.getElementById("targetIp").value = result.ip;
		}
	}else if ("<s:text name="topology.menu.diagnostics.traceroute"/>" == result.t) {
		pingTr.style.display = "none";
		tracerouteTr.style.display = "";
		interfaceTr.style.display = "none";
		if(result.ip){
			document.getElementById("tracerouteIp").value = result.ip;
		}
	}else{
		pingTr.style.display = "none";
		tracerouteTr.style.display = "none";
		interfaceTr.style.display = "none";
	}
	if(result.t){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = result.t;
	}
	if(result.h){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = dialogTitleDiv.innerHTML + ' - ' + result.h;
	}

	var cliDiv = document.getElementById("cli_viewer");
	cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g,"<br>") + "</pre>";
	var panelHeight = YAHOO.util.Dom.get('cliInfoPanel').offsetHeight;
    resetPanelSize(panelHeight, cliInfoPanel);
	cliInfoPanel.cfg.setProperty('visible', true);
}

var clientInfoResult = function(o) {
	eval("var result = " + o.responseText);
	var clientInfoElement = document.getElementById("clientInfoLabel");
	var clientInfoTable = document.getElementById("clientInfoTable");
	var titleDiv = document.getElementById("clientTitle");
	retrieveResult(result,clientInfoTable,clientInfoElement,titleDiv);
	clientInfoPanel.cfg.setProperty('visible', true);
}

var neighborInfoResult = function(o) {
	eval("var result = " + o.responseText);
	var neighborInfoElement = document.getElementById("neighborInfoLabel");
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	var titleDiv = document.getElementById("neighborTitle");
	retrieveResult(result,neighborInfoTable,neighborInfoElement,titleDiv);
	neighborInfoPanel.cfg.setProperty('visible', true);
}

function retrieveResult(result, table, tableLabelElement, dialogTitleDiv) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.h){
		var currentStr = dialogTitleDiv.innerHTML;
		if(currentStr.indexOf(' - ')>0){
			dialogTitleDiv.innerHTML = currentStr.slice(0,currentStr.indexOf(' - '))+ ' - ' + result.h;
		}else{
			dialogTitleDiv.innerHTML = currentStr + ' - ' + result.h;
		}
	}
	if(result.e){
		hm.util.reportFieldError(tableLabelElement, result.e);
		return;
	}
	if(result.data){
		var data = result.data;
		for(var i=0; i<data.length; i++){
			var rowData = data[i].rowData;
			if(i+1 < table.rows.length){
				table.deleteRow(i+1);
			}
			var row = table.insertRow(i+1);
			for(var j=0; j<rowData.length; j++){
				var cell = row.insertCell(j);
				cell.className = 'list';
				cell.innerHTML = rowData[j].v + "&nbsp;";
			}
		}
	}
}


function clearClientInfoTable(){
	var clientInfoTable = document.getElementById("clientInfoTable");
	clearTableData(clientInfoTable);
}
function clearNeighborInfoTable(){
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	clearTableData(neighborInfoTable);
}

function clearTableData(table){
	var rowCount = table.rows.length;
	for(var i=1; i< rowCount; i++){
		table.deleteRow(1);
	}
	for(var j=0; j<12; j++){
		var row = table.insertRow(1);
		for(var k=0; k<table.rows[0].cells.length; k++){
			var cell = row.insertCell(k);
			cell.className = 'list';
			cell.innerHTML = "&nbsp;";
		}
	}
}

function displayAPInfoPanel(targetId){
	targetId = targetId.replace(/n/,"");
	targetId = targetId.replace(/l/,"");
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=retrieveApInfo&leafNodeId=" + targetId +"&ignore="+ new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:apInfo,failure:null }, null);
}

var detailAp_id = null;
var detailAp_domainId = null;
var apInfo = function(o){
	eval("var result = " + o.responseText);
	if(result.v){
		var apInfoLabel = document.getElementById("apInfoLabel");
		hm.util.reportFieldError(apInfoLabel, result.v);
		if(null != apInfoPanel){
			apInfoPanel.cfg.setProperty('visible', true);
		}
		return;
	}
	if(result.h){
		var titleDiv = document.getElementById("apInfoTitle");
		var currentStr = titleDiv.innerHTML;
		if(currentStr.indexOf(' - ')>0){
			titleDiv.innerHTML = currentStr.slice(0,currentStr.indexOf(' - '))+ ' - ' + result.h;
		}else{
			titleDiv.innerHTML = currentStr + ' - ' + result.h;
		}
	}
	replaceText("apName",result.h);
	replaceText("apIp",result.apIp);
	replaceText("apMac",result.apMac);
	replaceText("apLocation",result.apLocation);
	replaceText("apClientCount",result.apClientCount);
	replaceText("apActiveSsid",result.apActiveSsid);

	detailAp_id = result.id;
	detailAp_domainId = result.domainId;
	if(null != apInfoPanel){
		apInfoPanel.cfg.setProperty('visible', true);
		//request to synchronize SSID from device; needn't do it right now.
		//synchronizeSsidInfo(result.id);
	}
}
function replaceText(nodeId, text) {
	var td = document.getElementById(nodeId);
	if (td != null) {
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(text));
	}
}

function hiveApDetails(){
	var url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=showHiveApDetails&id="+detailAp_id
		+ "&domainId=" + detailAp_domainId + "&hmListType=managedHiveAps";
	window.location.replace(url);
}

function synchronizeSsidInfo(targetId){
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=syncSsidInfo&hiveApId=" + detailAp_id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: updateSsidText, failure:connectedFailed,timeout: 10000 }, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
	replaceText("apActiveSsid","");
}

var updateSsidText = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.apActiveSsid){
		replaceText("apActiveSsid",result.apActiveSsid);
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
</style>
<div id="content"><s:form action="maps">
	<s:hidden name="operation" />
	<table width="100" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="mapContext" /></td>
		</tr>
		<tr>
			<td><img src="<s:url value="/images/spacer.gif"/>" height="2"
				alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td>
			<div id="processing" style="display:none">
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="noteInfo" id="note">Your request is being processed
					...</td>
				</tr>
				<tr>
					<td height="6"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td><img src="<s:url value="/images/spacer.gif"/>" height="1"
				alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td>
			<div><iframe id="viewport" name="viewport" width="700"
				frameborder="0" height="400" style="background-color: #FFFFFF;"
				src="<s:url value="maps.action"><s:param name="operation" value="%{'loading'}"/></s:url>">
			</iframe></div>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="detailsPanel" style="display: none;">
<div class="hd" id="dialogTitle">Map Details</div>
<div class="bd"><s:form action="mapSettings">
	<s:hidden name="operation" />
	<s:hidden name="selectedMapId" />
	<s:hidden name="id" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
				width="100%">
				<tr>
					<td style="padding: 6px 5px 5px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1"><s:text name="topology.map.name" /> <font
								color="red"><s:text name="*" /></font></td>
							<td style="padding-left: 10px;"><s:textfield id="mapName"
								size="24" name="mapName"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="16" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="topology.map.icon" /></td>
							<td style="padding-left: 10px;"><select id="mapIcon"
								name="mapIcon" style="width: 150px;"></select></td>
						</tr>
						<tr>
							<td class="labelT1" nowrap><s:text
								name="topology.map.background.image" /></td>
							<td style="padding-left: 10px;"><select id="mapImage"
								name="mapImage" style="width: 150px;"></select></td>
						</tr>
						<tr>
							<td class="labelT1">Environment</td>
							<td style="padding-left: 10px;"><s:select id="mapEnv"
								name="mapEnv" list="%{enumMapEnv}" listKey="key"
								listValue="value" cssStyle="width: 150px;" /></td>
						</tr>
						<tr>
							<td class="labelT1" nowrap>Width (optional)</td>
							<td style="padding-left: 10px;"><s:textfield id="mapWidth"
								size="24" name="mapWidth" maxlength="16" cssStyle="height:22px;"
								onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
							<td style="padding: 0 0 0px 4px;"><select id="mapWidthUnit"
								name="mapWidthUnit" onchange="toggleWidthUnit(this)">
								<option value="1">meters</option>
								<option value="2">feet</option>
							</select></td>
							<td width="100%"></td>
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
					<td id="createTd" style="display: none;"><input type="button"
						name="ignore" value="Create" class="button"
						onClick="submitAction('mapSettings','createMap');"></td>
					<td id="updateTd" style="display: none;"><input type="button"
						name="ignore" value="Update" class="button"
						onClick="submitAction('mapSettings','updateMap');"></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="submitAction('mapSettings','cancel_map');"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
</div>

<div id="addDelPanel" style="display: none;">
<div class="hd">Add/Delete Image</div>
<div class="bd"><s:form action="mapUpload"
	enctype="multipart/form-data" method="post">
	<s:hidden name="operation" />
	<s:hidden name="domainMapId" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
				width="100%">
				<tr>
					<td style="padding: 6px 5px 5px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr id="imageUploadTr">
							<td class="labelT1"><s:text name="topology.map.upload" /></td>
							<td colspan="2"><s:file id="imageFile" name="imageFile"
								size="30px" value="%{imageFile}" /></td>
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
					<td><input type="button" id="imageUpload" name="ignore"
						value="Upload" class="button"
						onClick="submitAction('mapUpload','uploadImage');"></td>
					<td><input type="button" name="ignore" value="Review"
						class="button" onClick="showReviewPanel();"></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('mapUpload','cancel_addDel');"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
</div>

<div id="imageReviewPanel" style="display: none;">
<div class="hd">Review Images</div>
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
							name="mapReviewImage" style="width: 150px;"></select></td>
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
				<td><input type="button" name="ignore" value="Delete"
					id="imageDeletion" class="button" onClick="deleteImage();"></td>
				<%-- <td><input type="button" name="ignore" value="Cancel"
					class="button" onClick="submitAction('mapUpload','cancel_review');"></td> --%>
			</tr>
		</table>
		</td>
	</tr>
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
						<td class="labelT1"><s:text name="topology.map.refresh.time" /><font color="red"><s:text name="*" /></font></td>
						<td><s:textfield name="refreshInterval" size="6"
							maxlength="4" id="refreshInterval"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
							name="topology.map.refresh.time.range" /></td>
					</tr>
					<tr>
						<td height="4px"></td>
					</tr>
					<tr>
						<td class="sepLine" colspan="3"><img
							src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
					</tr>
					<tr>
						<td height="4px"></td>
					</tr>
					<tr>
						<td colspan="3" style="padding-left: 4px">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td width="20px"><s:checkbox id="popUpFlag"
									name="popUpFlag" /></td>
								<td><s:text name="topology.map.popUpSummary.flag" /></td>
							</tr>
							<tr>
								<td width="20px"><s:checkbox id="showRssi" name="showRssi" /></td>
								<td><s:text name="topology.map.linkRssi.flag" /></td>
							</tr>
							<tr>
								<td width="20px"><s:checkbox id="showOnHover"
									name="showOnHover" /></td>
								<td><s:text name="topology.map.hoverAp.flag" /></td>
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
					value="Update" class="button" onClick="setGlobalParams();"></td>
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
<div class="hd" id="clientTitle"><s:text
	name="topology.menu.client.information" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td><label id="clientInfoLabel"></label></td>
		<td>
		<div
			style="height:300px; width: 600px;overflow-x:scroll;overflow-y:scroll;">
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
<div class="hd" id="neighborTitle"><s:text
	name="topology.menu.neighbor.information" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td><label id="neighborInfoLabel"></label></td>
		<td>
		<div
			style="height:300px; width: 600px;overflow-x:scroll;overflow-y:scroll;">
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
<div class="hd" id="summaryTitle">Network Summary</div>
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
						<td class="labelT1" width="200px"><s:text
							name="topology.map.networkSummary.totalManagedAp" /></td>
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
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
						<td class="labelT1" width="200px"><s:text
							name="topology.map.networkSummary.totalNewAp" /></td>
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
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
							src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
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
		<td class="labelT1" width="100px" style="padding-right:10px;"><s:text
			name="topology.menu.diagnostics.ping.name" /></td>
		<td><s:textfield id="targetIp" maxlength="15" size="15px" /></td>
		<td><input type="button" id="ping" name="ignore" value="Ping"
			class="button" onClick="ping();"></td>
	</tr>
	<tr id="tracerouteTr" style="display: none;">
		<td class="labelT1" width="100px" style="padding-right:10px;"><s:text
			name="topology.menu.diagnostics.ping.name" /></td>
		<td><s:textfield id="tracerouteIp" maxlength="15" size="15px" />
		<s:text name="topology.menu.diagnostics.traceroute.timeout" /> <s:textfield
			id="tracerouteTimeout" maxlength="3" size="3px" /></td>
		<td><input type="button" id="traceroute" name="ignore"
			value="Traceroute" class="button" onClick="traceroute();"></td>
	</tr>
	<tr id="interfaceTr" style="display: none;">
		<td class="labelT1" width="100px"><s:text
			name="topology.map.statistics.interface.name" /></td>
		<td><s:select id="interfaceType" list="%{enumInterfaceType}"
			listKey="key" listValue="value" cssStyle="width: 80px;"
			onchange="retrieveIfDetail();" /></td>
	</tr>
</table>
</div>
<div id="cli_viewer" class="cli_viewer"></div>
</div>
<div class="ft"></div>
</div>
<div id="apInfoPanel" style="display: none;">
<div class="hd" id="apInfoTitle"><s:text name="report.summary.widgetitle.apInfo" /></div>
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
		<td class="panelLabel"><s:text name="monitor.hiveAp.clientCount" /></td>
		<td class="panelText" id="apClientCount">.</td>
	</tr>
	<tr>
		<td class="panelLabel" valign="top"><s:text
			name="monitor.hiveAp.activeSsid" /> &nbsp;&nbsp;&nbsp; [<a
			href="javascript: synchronizeSsidInfo();"><s:text
			name="monitor.hiveAp.ssidRefresh" /></a>]</td>
		<td class="panelText" id="apActiveSsid">.</td>
	</tr>
	<tr>
		<td class="panelText" height="4"></td>
	</tr>
	<tr>
		<td class="labelT1" colspan="2"><a
			href="javascript: hiveApDetails();"><s:text
			name="monitor.hiveAp.more" /></a></td>
	</tr>
</table>
</div>
</div>
