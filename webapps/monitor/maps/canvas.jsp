<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>

<script>
function onLoadCanvas() {
	top.requestNodes(window);
//	var myLogReader = new YAHOO.widget.LogReader();
//	YAHOO.log("New map loaded ...", "warn");

}
function createSurface(div, width, height) {
	return dojo.gfx.createSurface(div, width, height);
}
function createLine(surface, line, r, g, b, alpha, width) {
	return surface.createLine(line)
		.setStroke({color: [r, g, b, alpha], width: width})
		.applyTransform(dojo.gfx.matrix.identity);
}
function createToolTip(nodeId, text) {
	var tooltip = new YAHOO.widget.Tooltip("tt_" + nodeId, { context: nodeId, text: text, showDelay:500, zIndex : 100 } );
}
var targetId;
function onMenuTrigger(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	if (event.target) {
		var tgt = event.target;
	} else {
		var tgt = event.srcElement;
	}
	targetId = tgt.parentNode.id;
	if(targetId == ""){
		//for click on P, AAA, P/AAA stuff, they are childNode of LabelDiv;
		targetId = tgt.parentNode.parentNode.id;
	}
//	alert("event: " + event + ", target: " + targetId);
}
function onMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	//menu item is disabled, do nothing.
	if(menuItem.cfg.getProperty("disabled") == true){
//		alert("This menu item is disabled.");
		return;
	}
	var leafNodeId = targetId.replace(/n/,"");
	leafNodeId = leafNodeId.replace(/l/,"");
	//alert("menu item: " + menuItem.index + ", target node: " + targetId);
	var text = menuItem.cfg.getProperty("text");
	if(text == "<s:text name="topology.menu.diagnostics"/>"
	|| text == "<s:text name="topology.menu.statistics"/>"
	|| text == "<s:text name="topology.menu.hiveAp.updates"/>") {
		return;
	}else if(text == "<s:text name="topology.menu.client.information"/>" ){
		top.clearClientInfoTable();
		top.retrieveClientInfo(targetId);
	}else if (text == "<s:text name="topology.menu.neighbor.information"/>" ) {
		top.clearNeighborInfoTable();
		top.retrieveNeighborInfo(targetId);
	}else if (text == "<s:text name="hiveAp.update.configuration"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeConfiguration&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.image"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeImage&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.l7.signature"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeSignature&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.cwp.single"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeCwp&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.cert.single"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeCert&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.configuration.single"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeConfigurationS&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.bootstrap"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeBootstrap&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="hiveAp.update.countryCode"/>") {
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=upgradeCountryCode&hmListType=managedHiveAps&leafNodeId=" + leafNodeId + "&ignore=" + new Date().getTime();
		top.window.location.replace(redirect_url);
	}else if (text == "<s:text name="topology.menu.hiveAp.alarm"/>") {
		top.retrieveAlarmInfo(targetId);
	}else{
		top.requestSingleItemCli(text, targetId);
	}
}
var nodeMenu;
function recreateContextMenu(triggerIds) {
	nodeMenu.cfg.setProperty('trigger', triggerIds);
}

function createContextMenu(triggerIds,writePermission) {
//	alert("canvas:"+writePermission);
    var aItems = [
    	[
        {text: "<s:text name="topology.menu.client.information"/>"},
        {text: "<s:text name="topology.menu.neighbor.information"/>"}
        ],
		[
        {
            text: "<s:text name="topology.menu.diagnostics"/>",
            submenu: {
                id: "diagnostics", // Id for the submenu element to be created
                // Array of YAHOO.widget.MenuItem configuration properties
                itemdata: ["<s:text name="topology.menu.diagnostics.ping"/>",
                //		   "<s:text name="topology.menu.diagnostics.traceroute"/>",
                 		   "<s:text name="topology.menu.diagnostics.showlog"/>",
                 		   "<s:text name="topology.menu.diagnostics.showversion"/>",
                 		   "<s:text name="topology.menu.diagnostics.showrunningconfig"/>",
                 		   "<s:text name="topology.menu.diagnostics.showiproutes"/>",
                 		   "<s:text name="topology.menu.diagnostics.showmacroutes"/>",
                 		   "<s:text name="topology.menu.diagnostics.showarpcache"/>",
                 		   "<s:text name="topology.menu.diagnostics.showroamingcache"/>",
                 		   "<s:text name="topology.menu.diagnostics.showl3roamingneighbors"/>",
                 		   "<s:text name="topology.menu.diagnostics.showl3roamingexstations"/>",
                 		   "<s:text name="topology.menu.diagnostics.showl3roamingimstations"/>",
                 		   "<s:text name="topology.menu.diagnostics.showcpu"/>",
                		   "<s:text name="topology.menu.diagnostics.showmemory"/>"]
            }
        },
        {
            text: "<s:text name="topology.menu.statistics"/>",
            submenu: {
                id: "statistics", // Id for the submenu element to be created
                // Array of YAHOO.widget.MenuItem configuration properties
                itemdata: ["<s:text name="topology.menu.statistics.acsp"/>",
                		   "<s:text name="topology.menu.statistics.interface"/>"]
            }
        }
        ],
        [
        {
            text: "<s:text name="topology.menu.hiveAp.updates"/>",
            submenu: {
                id: "updates", // Id for the submenu element to be created
                // Array of YAHOO.widget.MenuItem configuration properties
                itemdata: ["<s:text name="hiveAp.update.configuration"/>",
                		   "<s:text name="hiveAp.update.image"/>",
                		   //"<s:text name="hiveAp.update.l7.signature"/>",
                		   "<s:text name="hiveAp.update.cwp.single"/>",
                		   "<s:text name="hiveAp.update.cert.single"/>",
                		   "<s:text name="hiveAp.update.configuration.single"/>",
                		   "<s:text name="hiveAp.update.bootstrap"/>",
                		   "<s:text name="hiveAp.update.countryCode"/>"]
            },
            disabled: !writePermission
        }
        ],
        [
		{text: "<s:text name="topology.menu.hiveAp.reboot"/>",disabled: !writePermission}
        ],
        [
        {text: "<s:text name="topology.menu.hiveAp.alarm"/>"}
        ]
    ];





//	nodeMenu = new YAHOO.widget.ContextMenu("nodeMenu", { trigger: triggerIds });
	nodeMenu = new YAHOO.widget.ContextMenu("nodeMenu");
	nodeMenu.cfg.setProperty('trigger', triggerIds);
    nodeMenu.addItems(aItems);
	nodeMenu.subscribe('click',onMenuItemClick);
	nodeMenu.triggerContextMenuEvent.subscribe(onMenuTrigger);
//	nodeMenu.render(document.body); doesn't work on Firefox because of z-index
	nodeMenu.render('anchor');
}

</script>
<html>
<head>
<s:head theme="simple" />
<script language="JavaScript" type="text/javascript">
	dojo.require("dojo.gfx.*");
	dojo.hostenv.writeIncludes();
</script>

<!-- Menu CSS-->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none" />" />
<!-- Tooltip Dependency -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none" />" />
<!-- Logger -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/logger/assets/skins/sam/logger.css" includeParams="none" />" />
<script>
if (top.document.forms['maps'].zoom.value == 1) {
	document.writeln('<style>html, body { overflow: hidden; }</style>');
}
</script>
<style>
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
<%--
.yuimenu {
    background-color: #FFFFFF;
    border: 1px solid #999;
}
.yuimenu ul {
    padding:5px 1px;
}
.yuimenuitem a.selected {
    background-color:#0079d6;
}
.yuimenuitemlabel {
    padding:2px 15px;
}
.yui-skin-sam .yuimenuitem a.selected {
    background-color:#0079d6;
}
  --%>
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
	filter:alpha(opacity=65);
	opacity:.65;
}
.bgMap {
	filter:alpha(opacity=80);
	opacity:.80;
}
</style>
</head>
<body class="yui-skin-sam" onload="onLoadCanvas()">
<script type="text/javascript"
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />"></script>
<!-- Drag & Drop -->
<script type="text/javascript"
	src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none" />"></script>

<!-- Menu Dependency -->
<script type="text/javascript"
	src="<s:url value="/yui/container/container_core-min.js" includeParams="none" />"></script>

<!-- Tooltip Dependency -->
<script type="text/javascript"
	src="<s:url value="/yui/container/container-min.js" includeParams="none" />"></script>

<!-- Menu Source -->
<script type="text/javascript"
	src="<s:url value="/yui/menu/menu-min.js" includeParams="none" />"></script>

<!-- Logger -->
<script type="text/javascript"
	src="<s:url value="/yui/logger/logger-min.js" includeParams="none" />"></script>
<script>
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

</script>
<div
	style="position:absolute;top:0px;left:0px;background-color: #FFFFFF;">
<script>
document.writeln('<div id="bgMap" style="position:absolute;z-index:10;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;background-color: #FFFFFF;"><img id="imageId" style="display: block;" src="<s:url includeParams="none" value="" />',top.canvasBackground,'" width="',top.canvasWidth-top.gridBorderX,'" height="',top.canvasHeight-top.gridBorderY,'"/></div>');
document.writeln('<div id="clients" style="position:absolute;z-index:70;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="links" style="position:absolute;z-index:75;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="anchor" style="position:absolute;z-index:80;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
document.writeln('<div id="heat" class="heatMap" style="position:absolute;z-index:20;display:none;top:',top.gridBorderY,'px;left:',top.gridBorderX,'px;"></div>');
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

<div id="measuringTool" style="position:absolute;top:0px;left:0px;">

<div id="mtc1"
	style="position:absolute;top:100px;left:200px;width:50px;height:50px;">
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
	style="position:absolute;top:200px;left:100px;width:50px;height:50px;">
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
	style="position:absolute;top:200px;left:200px;width:50px;height:50px;">
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
