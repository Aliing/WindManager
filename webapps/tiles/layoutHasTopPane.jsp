<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style>
td.clients {
	background-image: url(<s:url value="/images/hm/bkg.gif" includeParams="none"/>);
}
</style>

<link rel="stylesheet"
	href="<s:url value="/css/hm.css" includeParams="none"/>"
	type="text/css" />
<link rel="shortcut icon"
	href="<s:url value="/images/favicon.ico" includeParams="none"/>"
	type="image/x-icon" />

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<!-- CSS -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none"/>?v=<s:property value="verParam" /> " />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/autocomplete/assets/skins/sam/autocomplete.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<!-- JS -->
<script
	src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/menu/menu-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/autocomplete/autocomplete-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var confirmDialog;
var warnDialog;
var oPopup;

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

function onLoadPage() {
}
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
<body class="body_bg client skin_hm yui-skin-sam" onload="onLoadEvent();"
	onunload="onUnloadEvent();" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0">
<script type="text/javascript">
	//avoid access hm from iframe
	if (top.location != self.location){
		top.location=self.location;
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
	</tr>
	<tr>
		<td class="dark" valign="top">
		</td>
		<td valign="top" style="padding: 10px 4px 0px 5px;" align="center">
			<tiles:insertAttribute name="body" /></td>
	</tr>
</table>
</body>
</html>
