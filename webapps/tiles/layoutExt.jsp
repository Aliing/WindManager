<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<html>
<head>
<title><s:property value="%{displayedTitle}"/></title>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />

<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>

<!-- CSS -->
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none"/> " />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>" />
<!-- JS -->
<script	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />"></script>
<script	src="<s:url value="/yui/animation/animation-min.js" includeParams="none" />"></script>
<script	src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none" />"></script>
<script	src="<s:url value="/yui/connection/connection-min.js" includeParams="none" />"></script>
<script	src="<s:url value="/yui/container/container-min.js" includeParams="none" />"></script>
<script	src="<s:url value="/yui/menu/menu-min.js" includeParams="none"/>"></script>
<script src="<s:url value="/yui/element/element-beta-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>"></script>
<style type="text/css">
.client {
	background-image: url(<s:url value="/images/hm/bkg.gif" includeParams="none"/>);
}
</style>
<script type="text/javascript">
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
                text: "<html><body>This operation will remove the selected items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
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
//	alert("YUI DOM version: " + YAHOO.env.getVersion("dom").version);
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
	hm.util.onUnloadValidation();
	onUnloadPage();
}
</script>
</head>
<body class="body_bg yui-skin-sam client" onload="onLoadEvent();" onunload="onUnloadEvent();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
	</tr>
	<tr>
		<td valign="top" class="client" style="padding: 4px 4px 0px 5px;" width="100%">
			<tiles:insertAttribute name="body" /></td>
	</tr>
</table>
</body>
</html>