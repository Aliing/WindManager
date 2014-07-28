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
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<s:if test="%{useCdn}">
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/assets/skins/sam/skin.css" />
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/utilities/utilities.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/container/container-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/menu/menu-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/button/button-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/layout/layout-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/treeview/treeview-min.js"></script>
</s:if>
<s:else>
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/assets/skins/sam/skin.css" includeParams="none" />?v=<s:property value="verParam" />" />
	<script
		src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/menu/menu-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/logger/logger-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/treeview/treeview-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
	<script
		src="<s:url value="/yui/layout/layout-min.js" includeParams="none" />"></script>
</s:else>
<link rel="shortcut icon"
	href="<s:url value="/images/favicon.ico" includeParams="none" />"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<s:url value="/css/hm.css" includeParams="none" />?v=<s:property value="verParam" />"
	type="text/css" />
<style>
html,body {
	overflow: hidden;
}

body,form {
	margin: 0;
	padding: 0;
}
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
/* YUI Tree overrides */
body .ygtvlabel,body .ygtvlabel:link,body .ygtvlabel:visited,body .ygtvlabel:hover {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 12px;
	color: #003366;
	white-space: nowrap;
}

body .ygtvlabel:hover {
	text-decoration: underline;
}

body .ygtvlabelSel,body .ygtvlabelSel:link,body .ygtvlabelSel:visited,body .ygtvlabelSel:hover {
	font-weight: bold;
}

body .ygtvfocus {
	background-color: #FFFFFF;
}

body .ygtvfocus .ygtvlabel,body .ygtvfocus .ygtvlabel:link,body .ygtvfocus .ygtvlabel:visited,body .ygtvfocus .ygtvlabel:hover {
	background-color: #FFFFFF;
}

a.ygtvspacer {
	text-decoration: none;
}

a.ygtvspacer:active,a.ygtvspacer:focus {
	-moz-outline: none;
	outline: none;
	ie-dummy: expression(this.hideFocus = true);
}

/* YUI Layout overrides */
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-noft {
	border-bottom: 0;
}

.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-nohd {
	border-top: 0;
}

.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
	background-color: #fff;
	border: 0;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/swfupload.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script
	src="<s:url value="/js/swfupload/swfupload.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/swfupload.queue.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/fileprogress.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/handlers.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
var confirmDialog;
var warnDialog;
var infoDialog;
var alarmsAction = "<s:url action='alarms' includeParams='none' />";

function onLoadNotes() {
}
function onLoadPage() {
}
var sortFolders;
function onLoadEvent() {
	initPanel();
	<s:if test="%{!initializePage}">
		createTree();
		sortFolders = !<s:property value="%{userContext.orderFolders}" />;
	</s:if>
	
	onLoadNotes();
	onLoadPage();
	initConfirmDialog();
	initWarnDialog();
	hm.util.startSystemStatusTimer(alarmsAction);
<%--
	<s:if test="%{showPlanningTool}">
		openPlanningPanel();
	</s:if>
  --%>
	<s:if test="%{!initializePage}">
		var url = "mapAps.action?operation=tree&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {
			success : mapHierarchyLoaded
		});
	</s:if>
}
function mapHierarchyLoaded(o) {
	eval("mapHierarchy = [" + o.responseText + "]");
	scanTree(mapHierarchy, mapTree.getRoot(), 0);
	mapTree.render();
	if (selectedNode != null) {
		highlightNode(selectedNode);
	}
	createTreeMenus();
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	onUnloadPage();
}
function onBeforeUnloadEvent(){
	onBeforeUnloadMap();
}
function onBeforeUnloadMap(){
}

var selectedNode = null;
var mapHierarchy = [ <ah:mapTree /> ];

function initPanel(){
	var scroll = true;
	if (YAHOO.env.ua.webkit > 0) {
		scroll = false;
	}
	<s:if test="%{fullScreenMode}">
		var height = 28;
	</s:if>
	<s:else>
		var height = 83;
	</s:else>
	<s:if test="%{easyMode}">
		//height++;
	</s:if>
	var tw = "<s:property value="%{userContext.treeWidth}" />";
	var layout = new YAHOO.widget.Layout({
             units: [
                { position: 'top', height: height, body: 'top1', gutter: '0' },
                { position: 'left', width: tw },
                { position: 'center', body: 'center1', scroll: scroll }
            ]
    });
	layout.on('render', function() {
	var el = layout.getUnitByPosition('left').get('wrap');
	var layout2 = new YAHOO.widget.Layout(el, {
		parent: layout,
        units: [
			{ position: 'top', body: 'top2', height: 32 },
			{ position: 'center',  width: tw ,scroll: true }
			
			<s:if test="%{planningOnly}">
				,{ position: 'bottom', body: 'supportDiv', height: 300 }
			</s:if>
			<s:elseif test="%{fullScreenMode}">
				,{ position: 'bottom', body: 'fullScreenDiv', height: 3 }
			</s:elseif>
			<s:else>
				,{ position: 'bottom', body: 'statusDiv', height: 102 }
			</s:else>
		]
		});
		
		layout2.on('render', function() {
			var ele = layout2.getUnitByPosition('center').get('wrap');
			var layout3 = new YAHOO.widget.Layout(ele, {
				parent: layout2,
		        units: [
					{ position: 'center',  body: 'treeDiv',scroll: true }
				]
				});
			
			layout3.render();
		});
		
		layout2.render();
	});
    var div = document.getElementById('top2');
    div.style.display = "";
    div = document.getElementById('statusDiv');
    if (div) {
	    div.style.display = "";
    }
    div = document.getElementById('supportDiv');
    if (div) {
	    div.style.display = "";
    }
    div = document.getElementById('center1');    
    div.style.display = "";
    layout.render();
}
document.oncontextmenu = function(){return false;};
</script>
</head>

<body class="body_bg skin_hm yui-skin-sam tundra soria nihilo"
	onload="onLoadEvent()" onunload="onUnloadEvent()" leftmargin="0"
	topmargin="0" marginwidth="0" marginheight="0"
	onbeforeunload="onBeforeUnloadEvent();">
<script type="text/javascript">
	//avoid access hm from iframe
	if (top.location != self.location){
		top.location=self.location;
	}
</script>
<div id="top1"><tiles:insertAttribute name="topPane" /></div>
<div id="top2"
	style="padding-top: 3px; background-color: #E1E1E1; display: none;">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td><img
			src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg" width="100%"></td>
		<td><img
			src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<s:if test="%{!initializePage}">
	<tr>
		<td class="menu_bg" style="padding: 0 6px 0 5px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="yexpall" id="yexpall" onclick="mapTree.expandAll()" title="Expand All"></td><td id="yexpcol"></td><td class="ycolall" id="ycolall" onclick="mapTree.collapseAll()" title="Collapse All"></td>
						</tr>
					</table>
				</td>
				<td width="100%">
				</td><td nowrap style="color: #003366;padding-right: 2px;">Sort folders</td>
				<td><input type="checkbox" style="height: 14px;margin:0;padding:0;" name="sortFolders" onclick="toggleSortFolders(this);"></input></td>
			</tr>
<%--
			<tr>
				<td class="leftNavH1" style="padding-bottom: 5px;"><s:property
					value="selectedL1Feature.description" /></td>
			</tr>
  --%>
		</table>
		</td>
	</tr>
<%--
	<tr><td class="menu_bg" colspan="3" style="padding-top: 2px; border-bottom: 1px solid #003366;"></td></tr>
  --%>
	</s:if>
</table>
</div>
<div id="treeDiv" style="background-color: #fff; padding-left: 5px;"></div>

<s:if test="%{planningOnly}">
	<div id="supportDiv" style="width: 100%; display: none;"><tiles:insertAttribute
		name="techSupport" /></div>
</s:if>
<s:elseif test="%{fullScreenMode}">
	<div id="fullScreenDiv" style="background-color: #E1E1E1;"><img
		width="1" height="3"
		src="<s:url value="/images/spacer.gif" includeParams="none"/>" alt=""
		class="dblk" /></div>
</s:elseif>
<s:else>
	<div id="statusDiv" style="width: 100%; display: none;"><tiles:insertAttribute
		name="statusView" /></div>
</s:else>
<div id="center1"
	style="padding: 3px 0 0 3px; background-color: #E1E1E1; display: none;"><tiles:insertAttribute
	name="body" /></div>

<s:if test="%{planningOnly}">
	<div id="planningPanel" style="display: none;">
	<div class="hd"><s:text name="topology.menu.planner.setting" /></div>
	<div class="bd"><iframe id="planning_tool" name="planning_tool"
		scrolling="no" frameborder="0" style="background-color: #999;" src="">
	</iframe></div>
	</div>
	<script type="text/javascript">
var planningPanel = null;
var confirmed = false;
function openPlanningPanel(){
	if(null == planningPanel){
		createPlanningPanel();
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("planning_tool").style.display = "";
	}
	var iframe = document.getElementById("planning_tool");
	iframe.src ="<s:url value='planTool.action' includeParams='none' />?operation=initPlanningPanelFromTopo";
	planningPanel.show();
}
</script>
</s:if>

</body>
</html>
