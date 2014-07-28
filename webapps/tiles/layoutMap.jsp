<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%--
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
  --%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<tiles:importAttribute name="leftWidth" scope="request" />

<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" />"></script>
<script src="<s:url value="/yui/animation/animation-min.js" />"></script>
<script src="<s:url value="/yui/dragdrop/dragdrop-min.js" />"></script>
<script src="<s:url value="/yui/connection/connection-min.js" />"></script>
<script src="<s:url value="/yui/container/container-min.js" />"></script>
<script>
function onLoadNotes() {
}
function onLoadPage() {
}
function onLoadEvent() {
	onLoadNotes();
	onLoadPage();
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
	hm.map.clearAlarmsTimer();
	hm.util.clearSystemStatusTimer();
}
</script>

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<s:head theme="simple" />
<script language="JavaScript" type="text/javascript">
	dojo.require("dojo.widget.*");
	dojo.require("dojo.widget.LayoutContainer");
	dojo.require("dojo.widget.ContentPane");
	dojo.require("dojo.widget.SplitContainer");
//	dojo.require("dojo.widget.LinkPane");
//	dojo.require("dojo.widget.ColorPalette");
//	dojo.require("dojo.widget.TabContainer");
	dojo.require("dojo.widget.Tree");
	dojo.require("dojo.lfx.html");
	dojo.require("dojo.event.*");
	dojo.hostenv.writeIncludes();
</script>
<script src="<s:url value="/js/hm.util.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css"/>" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
<style>
html, body {
	overflow: hidden;
}
#leftPane {
	margin: 0;
}
</style>
</head>
<body class="body_bg skin_hm yui-skin-sam" onload="onLoadEvent()"
	onunload="onUnloadEvent()" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">
<div dojoType="LayoutContainer" layoutChildPriority='top-bottom'
	style="width: 100%; height: 100%;">
<div dojoType="ContentPane" layoutAlign="top"><tiles:insertAttribute
	name="topPane" /></div>
<div dojoType="SplitContainer" orientation="horizontal" sizerWidth="4"
	activeSizing="0" isActiveResize="0" layoutAlign="client">
<div dojoType="LayoutContainer" layoutChildPriority='top-bottom'
	sizeMin="${leftWidth}" sizeShare="10">
<div dojoType="ContentPane" layoutAlign="top"
	style="background-color: #FFFFFF">
<table border="0" cellspacing="0" cellpadding="0">
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
				<td class="leftNavH1" style="padding-bottom: 8px;"><s:property
					value="selectedL1Feature.description" /></td>
			</tr>
		</table>
		</td>
		<td class="menu_bg"></td>
	</tr>
</table>
</div>
<div dojoType="ContentPane" layoutAlign="client"
	style="background-color: #FFFFFF; padding-left: 3px;">
<div dojoType="TreeSelector" widgetId="mapSelector"
	eventNames="select:mapSelected"></div>
<div dojoType="Tree" id="mapHierarchy" selector="mapSelector"
	toggle="fade" showRootGrid="true"><ah:tree /></div>
</div>
<div dojoType="ContentPane" layoutAlign="bottom"
	style="background-color: #FFFFFF">
<table border="0" cellspacing="0" cellpadding="0" class="body_bg">
	<tr>
		<td><img
			src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg" width="100%"></td>
		<td><img
			src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
	<tr>
		<td height="4"></td>
	</tr>
	<tr>
		<td><img
			src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg"></td>
		<td><img
			src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
	<tr>
		<td class="menu_bg"></td>
		<td class="menu_bg"><tiles:insertAttribute name="statusItems" /></td>
		<td class="menu_bg"></td>
	</tr>
	<tr>
		<td><img
			src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
		<td class="menu_bg" width="100%"></td>
		<td><img
			src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
			width="9" height="9" alt="" class="dblk" /></td>
	</tr>
</table>
</div>
</div>
<div dojoType="ContentPane" sizeShare="90" activeSizing="0"
	layoutAlign="bottom"><tiles:insertAttribute name="body" /></div>
</div>
</div>
</body>
</html>
