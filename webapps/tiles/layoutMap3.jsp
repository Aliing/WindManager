<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%--
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
  --%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<tiles:importAttribute name="leftWidth" scope="request" />

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<%--
<script type="text/javascript" src="/struts/dojoroot/dojo/dojo.js"
	djConfig="parseOnLoad: true, isDebug: false">
</script>
 --%>
<%--
<style type="text/css">
@import "/struts/dojoroot/dijit/themes/tundra/tundra.css";
</style>
 --%>
<%--
<script type="text/javascript">
	dojo.require("dojo.parser");
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.SplitContainer");
	dojo.require("dijit.layout.ContentPane");
</script>
 --%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/treeview/assets/skins/sam/treeview.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/assets/skins/sam/layout.css" includeParams="none"/>" />
<link rel="shortcut icon"
	href="<s:url value="/images/favicon.ico" includeParams="none" />"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<s:url value="/css/hm.css" includeParams="none" />"
	type="text/css" />
<style>
html,body {
	overflow: hidden;
}

body,form {
	margin: 0;
	padding: 0;
}

#statusDiv{
	position: absolute;
	bottom: 0px;
}

/* YUI Tree overrides */
body .ygtvlabel,body .ygtvlabel:link,body .ygtvlabel:visited,body .ygtvlabel:hover
	{
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 12px;
	color: #003366;
}

body .ygtvlabel:hover {
	text-decoration: underline;
}

body .ygtvlabelSel,body .ygtvlabelSel:link,body .ygtvlabelSel:visited,body .ygtvlabelSel:hover {
	font-weight: bold;
}

body .ygtvfocus {
	background-color:#FFFFFF;
}
body .ygtvfocus .ygtvlabel, body .ygtvfocus .ygtvlabel:link, body .ygtvfocus .ygtvlabel:visited, body .ygtvfocus .ygtvlabel:hover {
	background-color:#FFFFFF;
}

/*YUI Layout overrieds */
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-noft {
	border-bottom:0;
}
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-nohd {
	border-top:0;
}
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
	background-color:#E1E1E1;
}
</style>

<script
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/animation/animation-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/connection/connection-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/container/container-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/menu/menu-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/element/element-beta-min.js"  includeParams="none"/>"></script>
<script
	src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>"></script>
<script
	src="<s:url value="/yui/logger/logger-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/treeview/treeview-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/layout/layout-min.js" includeParams="none" />"></script>

<script type="text/javascript">
var confirmDialog;
function initConfirmDialog() {
	confirmDialog =
     new YAHOO.widget.SimpleDialog("confirmDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                text: "<html><body>This operation will remove the selected item(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
                           { text:"&nbsp;No&nbsp;", handler:handleNo } ]
              } );
     confirmDialog.setHeader("Confirm");
     confirmDialog.render(document.body);
}

var handleYes = function() {
    this.hide();
    doContinueOper();
};

var handleNo = function() {
    this.hide();
};

function onLoadNotes() {
}
function onLoadPage() {
}
function onLoadEvent() {
//	alert("dojo version: " + dojo.version);
	initPanel();
	createTree();
	onLoadNotes();
	onLoadPage();
	initConfirmDialog();
	hm.util.startSystemStatusTimer("<s:url action="alarms" includeParams="none" />");
	if(YAHOO.env.ua.ie == 6){
		//fix PNG bug in IE6
		fixLogoPng();
	}
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	onUnloadPage();
}
function connectSplitContainer() {
//	var sc = dijit.byId("treeSplitContainer");
//	dojo.connect(sc, "endSizing", treeResized);
}
function treeResized() {
	hm.map.loadMap(selectedNode.data.id);
}
var mapTree = null;
var labelClicked = false;
function createTree() {
	mapTree = new YAHOO.widget.TreeView("treeDiv");
	populateTree(mapHierarchy, mapTree.getRoot(), 0);
	mapTree.subscribe("labelClick", function(node) {
		labelClicked = true;
		highlightNode(node);
		hm.map.loadMap(node.data.id);
	});
	mapTree.subscribe("expand", function(node) {
		if (labelClicked) {
			labelClicked = false;
			return false;
		}
	});
	mapTree.subscribe("collapse", function(node) {
		if (labelClicked) {
			labelClicked = false;
			return false;
		}
	});
	mapTree.render();
	var treeWidth = maxTreeWidth(mapTree.getRoot().children);
	var treeDiv = YAHOO.util.Dom.get("treeDiv");
	treeDiv.style.width = treeWidth + "px";
	while (!treeIndent(mapTree.getRoot().children, 0)) {
		treeWidth += 5;
		treeDiv.style.width = treeWidth + "px";
	}

	var leftWidth = parseInt('${leftWidth}');
	if(treeWidth < leftWidth - 25){
		treeWidth = leftWidth - 25;
	}

	treeWidth += 15;
	treeDiv.style.width = treeWidth + "px";
	treeTrim(mapTree.getRoot().children, 0);
}
function populateTree(items, parentNode, depth) {
	for (var i = 0; i < items.length; i++) {
		if (items[i] != undefined) {
			var node = new YAHOO.widget.TextNode(items[i], parentNode, true); // depth < 2
//			node.href = "javascript:nodeSelected(" + node.index + ")";
			populateTree(items[i].items, node, depth+1);
		}
	}
}
function maxTreeWidth(nodes) {
	var maxWidth = 0;
	for (var i = 0; i < nodes.length; i++) {
		var labelX = YAHOO.util.Dom.getX(nodes[i].labelElId);
		if (labelX > maxWidth) {
			maxWidth = labelX;
		}
		var treeWidth = maxTreeWidth(nodes[i].children);
		if (treeWidth > maxWidth) {
			maxWidth = treeWidth;
		}
	}
	return maxWidth;
}
function treeIndent(nodes, parentLabelX) {
	for (var i = 0; i < nodes.length; i++) {
		var labelX = YAHOO.util.Dom.getX(nodes[i].labelElId);
		if (parentLabelX > 0 && labelX - parentLabelX != 18) {
			return false;
		}
		if (!treeIndent(nodes[i].children, labelX)) {
			return false;
		}
	}
	return true;
}
function treeTrim(nodes, depth) {
	for (var i = 0; i < nodes.length; i++) {
		if (depth < 1) {
			treeTrim(nodes[i].children, depth + 1);
		} else if (depth == 1) {
			nodes[i].collapseAll();
		}
	}
}
function nodeSelected(index) {
	var node = mapTree.getNodeByIndex(index);
	highlightNode(node);
	hm.map.loadMap(node.data.id);
}
var selectedNode = null;
function highlightNode(node) {
	if (selectedNode != null) {
		YAHOO.util.Dom.removeClass(selectedNode.labelElId, 'ygtvlabelSel');
	}
	selectedNode = node;
	YAHOO.util.Dom.addClass(selectedNode.labelElId, 'ygtvlabelSel');
}
var mapHierarchy = [ <ah:mapTree /> ];

function initPanel(){
var layout = new YAHOO.widget.Layout({
             units: [
                { position: 'top', height: 82, body: 'top1', gutter: '0' },
                { position: 'left', minWidth: ${leftWidth}, width: 165, resize: true, body: 'left1', gutter: '0 5px 0 0', scroll:true },
                { position: 'center', body: 'body1' }
            ]
    });
    layout.on('render', function() {
    	var el = layout.getUnitByPosition('left');
		el.subscribe("endResize", treeResized );
    });
    layout.render();
}

</script>
</head>

<body class="skin_hm yui-skin-sam body_bg tundra soria nihilo"
	onload="onLoadEvent()" onunload="onUnloadEvent()">
<div style="background-color: #E1E1E1;">
<div id="top1"><tiles:insertAttribute
	name="topPane" /></div>
<div id="left1" style="padding-top: 4px;">
<div id="preview" title="Preview">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="body_bg"><img
			src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg" width="100%"></td>
		<td class="body_bg"><img
			src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
	<tr>
		<td class="menu_bg"></td>
		<td class="menu_bg">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="leftNavH1" style="padding-bottom: 5px;"><s:property
					value="selectedL1Feature.description" /></td>
			</tr>
		</table>
		</td>
		<td class="menu_bg"></td>
	</tr>
</table>
</div>
<div style="background-color: #FFFFFF; padding-left: 5px; overflow-x:auto;">
<div id="treeDiv"></div>
</div>
<div id="statusDiv" style="width: 100%;"><tiles:insertAttribute name="statusView" /></div>
</div>
<div id="body1" style="padding-top: 4px;"><tiles:insertAttribute name="body" /></div>
</div>
<%--
<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; padding: 0; margin: 0; border: 0;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="top"><tiles:insertAttribute
	name="topPane" /></div>
<div dojoType="dijit.layout.SplitContainer" orientation="horizontal"
	layoutAlign="client" sizerWidth="7" activeSizing="true"
	id="treeSplitContainer">
<div dojoType="dijit.layout.LayoutContainer" sizeShare="1"
	sizeMin="${leftWidth}" style="padding-left: 1px;">
<div dojoType="dijit.layout.ContentPane" title="Preview"
	layoutAlign="top" style="padding-top: 4px;">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="body_bg"><img
			src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg" width="100%"></td>
		<td class="body_bg"><img
			src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
	<tr>
		<td class="menu_bg"></td>
		<td class="menu_bg">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="leftNavH1" style="padding-bottom: 5px;"><s:property
					value="selectedL1Feature.description" /></td>
			</tr>
		</table>
		</td>
		<td class="menu_bg"></td>
	</tr>
</table>
</div>
<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
	style="background-color: #FFFFFF; padding-left: 5px; overflow-x:auto;">
<div id="treeDiv"></div>
</div>
<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom"><tiles:insertAttribute
	name="statusView" /></div>
</div>
<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
	sizeShare="9" style="padding-top: 4px;"><tiles:insertAttribute
	name="body" /></div>
</div>
</div> --%>
</body>
</html>
