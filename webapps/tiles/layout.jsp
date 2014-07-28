<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tiles:importAttribute name="leftWidth" scope="request" />
<!-- CSS -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css"/>" />
<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" />"></script>
<!-- OPTIONAL: Animation (only required if enabling Animation) -->
<script src="<s:url value="/yui/animation/animation-min.js" />"></script>

<!-- OPTIONAL: Drag & Drop (only required if enabling Drag & Drop) -->
<script src="<s:url value="/yui/dragdrop/dragdrop-min.js" />"></script>

<!-- OPTIONAL: Connection (only required if performing asynchronous submission) -->
<script src="<s:url value="/yui/connection/connection-min.js" />"></script>

<!-- Source file -->
<script src="<s:url value="/yui/container/container-min.js" />"></script>

<script>
var confirmDialog;
var warnDialog;
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

function initWarnDialog() {
	warnDialog =
     new YAHOO.widget.SimpleDialog("warnDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
              } );
     warnDialog.setHeader("Warning");
     warnDialog.render(document.body);
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
function onLoadPaging() {
}
function onLoadPage() {
}
function onLoadEvent() {
	onLoadNotes();
	onLoadPaging();
	onLoadPage();
	initConfirmDialog();
	initWarnDialog();
	hm.util.registerRowClickedEvents();
	sizeLeftMenu();
	window.onresize = sizeLeftMenu;
	hm.util.startSystemStatusTimer("<s:url action="alarms" includeParams="none" />");
//	alert("YUI DOM version: " + YAHOO.env.getVersion("dom").version);
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	hm.util.onUnloadValidation();
	onUnloadPage();
	hm.util.clearSystemStatusTimer();
}
function sizeLeftMenu() {
	var div = document.getElementById("leftMenu");
	if (div != null) {
		sizeLeftMenuScroll(div);
		return;
	}
	var td = document.getElementById('lms');
	if (td == null) {
		return;
	}
	var vpHeight = YAHOO.util.Dom.getViewportHeight();
	var lse = document.getElementById('lse');
	if (lse == null) {
		return;
	}
	var extra = vpHeight - YAHOO.util.Dom.getY(lse) - 13 + parseInt(td.height) + YAHOO.util.Dom.getDocumentScrollTop();
	if (YAHOO.env.ua.ie == 7) {
		extra += 3;
	}
	if (extra > 0) {
		td.height = extra;
	} else {
		td.height = 1;
	}
}
// With scroll bars
function sizeLeftMenuScroll(div) {
	var vpHeight = YAHOO.util.Dom.getViewportHeight();
	var lss = document.getElementById('lss');
	if (lss == null) {
		return;
	}
	var lse = document.getElementById('lse');
	if (lse == null) {
		return;
	}
	var lsh = YAHOO.util.Dom.getY(lse) - YAHOO.util.Dom.getY(lss);

	var lms = YAHOO.util.Dom.getY(div);

	var lmh = vpHeight - lms - lsh - 36;
	if (YAHOO.env.ua.ie) {
		lmh += 3;
	}

	scrollTo(0, 0);

	if (lmh < 100) {
		lmh = 100;
	}

	if (YAHOO.env.ua.ie) {
		div.style.height = lmh;
	} else {
		div.style.height = lmh + "px;";
	}
}
</script>

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style>
td.clients {
	background-image: url(../images/hm/bkg.gif);
}
</style>
<%--
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  --%>

<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css"/>" />

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>

<%--
<script src="<s:url value="/yui/yahoo/yahoo-min.js" />"></script>
<script src="<s:url value="/yui/dom/dom-min.js" />"></script>
<script src="<s:url value="/yui/event/event-min.js" />"></script>
  Bundles all 3 above --%>
<script type="text/javascript"
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none"/>"></script>

<script
	src="<s:url value="/yui/connection/connection-min.js" includeParams="none"/>"></script>
</head>
<body class="body_bg skin_hm yui-skin-sam" onload="onLoadEvent();"
	onunload="onUnloadEvent();" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
	</tr>
	<tr>
		<td style="padding: 4px 0px 0px 4px;" valign="top">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><img
					src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
				<td class="menu_bg"><img
					src="<s:url value="/images/spacer.gif" />" width="${leftWidth}"
					height="1" alt="" class="dblk" /></td>
				<td><img
					src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
			</tr>
			<tr>
				<td class="menu_bg"></td>
				<td class="menu_bg" colspan="2"><tiles:insertAttribute
					name="leftMenu" /></td>
			</tr>
			<tr>
				<td class="menu_bg"></td>
				<td class="menu_bg" colspan="2"><tiles:insertAttribute
					name="leftFilter" /></td>
			</tr>
			<tr>
				<td class="menu_bg" colspan="3" height="1" id="lms"></td>
			</tr>
			<tr>
				<td><img
					src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
				<td class="menu_bg"></td>
				<td><img
					src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
			</tr>
			<tr>
				<td height="3"></td>
			</tr>
<%--
			<tr>
				<td colspan="3"><tiles:insertAttribute name="statusView" /></td>
			</tr>
  --%>
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
				<td class="menu_bg"></td>
				<td><img
					src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
			</tr>
		</table>
		</td>
		<td valign="top" class="client" style="padding: 4px 4px 0px 5px;"
			width="100%"><tiles:insertAttribute name="body" /></td>
	</tr>
</table>
</body>
</html>
