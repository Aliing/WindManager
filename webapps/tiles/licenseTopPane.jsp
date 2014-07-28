<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<html>
<head>
<title><s:property value="%{licTile}" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=8;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />

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

function fixLogoPng(){
	//var img = document.getElementById("logo_img");
	// to fix all png images in IE6.
	for(var i=0; i<document.images.length; i++){
		var img = document.images[i];
		var imgName = img.src;
        if (null != imgName && imgName.search(/.png/i) > 0){
        	var imgID = (img.id) ? "id='" + img.id + "' " : "";
        	var imgClass = (img.className) ? "class='" + img.className + "' " : "";
        	var imgTitle = (img.title) ? "title='" + img.title + "' " : "title='" + img.alt + "' ";
        	var imgStyle = "display:inline-block;" + img.style.cssText;
        	if (img.align == "left") imgStyle = "float:left;" + imgStyle
        	if (img.align == "right") imgStyle = "float:right;" + imgStyle
        	if (img.parentElement.href) imgStyle = "cursor:hand;" + imgStyle
        	var strNewHTML = "<span " + imgID + imgClass + imgTitle
        					+ " style=\"" + "width:" + img.width + "px; height:" + img.height + "px;" + imgStyle + ";"
        					+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
        					+ "(src=\'" + img.src + "\', sizingMethod='scale');\"></span>";
        	img.outerHTML = strNewHTML;
        	i--; // avoid it not take effect
        }
	}
}
var sysName = '<s:property value="%{systemNmsName}"/>';
var BrowserCheck = {
	$: function(o){
		return typeof o == 'string' ? document.getElementById(o):o;
	},
	init: function() {
		var type = BrowserCheck.unSupportMsg();
	    if (type != null && BrowserCheck.Cookie.get('ignore_browser_check') != 'on') {
	        if (BrowserCheck.$('browser_compatibility')) {
	        	BrowserCheck.$('browser_compatibility').innerHTML = BrowserCheck.getMessage(type);
	        	if(type == 'warning'){
	        		YAHOO.util.Dom.setStyle('browser_compatibility','width','650px');
	        	}else{
	        		YAHOO.util.Dom.setStyle('browser_compatibility','width','600px');
			    }
	        	YAHOO.util.Dom.setStyle('browser_compatibility','display','');
	        }
	    }
	    else {
	    	if (BrowserCheck.$('browser_compatibility')) {
	    		YAHOO.util.Dom.setStyle('browser_compatibility','width','0px');
	        	YAHOO.util.Dom.setStyle('browser_compatibility','display','none');
	        }
	    }
	},
	unSupportMsg: function(){
		var isChrome = window.MessageEvent&&!document.getBoxObjectFor;
		if(!YAHOO.env.ua.ie && !YAHOO.env.ua.gecko && !isChrome){
			return 'unsupport';
		}
		if(YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7){
			return 'warning';
		}
		return null;
	},
	getMessage: function(type) {
	    <s:if test="%{planningOnly||planner}">
		    var msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Planner does not support this browser. You might encounter problems if you continue using it.</div>";
		    if (type == 'warning') {
		        msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Because this is an earlier browser version, some advanced Planner features might not be supported.</div>";
		    }
	    </s:if>
	    <s:else>
		    var msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;"+sysName+" does not support this browser. You might encounter problems if you continue using it.</div>";
		    if (type == 'warning') {
		        msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Because this is an earlier browser version, some advanced "+sysName+" features might not be supported.</div>";
		    }
	    </s:else>
	    msg += ' <div style="float: right;"><a href="#closeCheck" onclick="BrowserCheck.ignore();"><img alt="Ignore it" title="Ignore it" src="<s:url value="/images/cancel.png" includeParams="none"/>" width="16" class="dinl"></a>'
	    return msg;
	},
	ignore: function() {
		if (BrowserCheck.$('browser_compatibility')) {
			YAHOO.util.Dom.setStyle('browser_compatibility','width','0px');
			YAHOO.util.Dom.setStyle('browser_compatibility','display','none');
	    }
		BrowserCheck.Cookie.erase('ignore_browser_check');
		BrowserCheck.Cookie.set('ignore_browser_check', 'on');
	},
	Cookie: {
	    set: function(name, value, daysToExpire) {
	        var expire = '';
	        if (!daysToExpire) daysToExpire = 365;
	        var d = new Date();
	        d.setTime(d.getTime() + (86400000 * parseFloat(daysToExpire)));
	        expire = 'expires=' + d.toGMTString();
	        var path = "path=/"
	        var cookieValue = escape(name) + '=' + escape(value || '') + '; ' + path + '; ' + expire + ';';
	        return document.cookie = cookieValue;
	    },
	    get: function(name) {
	        var cookie = document.cookie.match(new RegExp('(^|;)\\s*' + escape(name) + '=([^;\\s]+)'));
	        return (cookie ? unescape(cookie[2]) : null);
	    },
	    erase: function(name) {
	        var cookie = BrowserCheck.Cookie.get(name) || true;
	        BrowserCheck.Cookie.set(name, '', -1);
	        return cookie;
	    }
	}
}
YAHOO.util.Event.onDOMReady(function () {
	BrowserCheck.init();
});

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
var aboutHMPanel;

function openAboutPage()
{
	if (aboutHMPanel == null)
	{
		createAboutHMPanel();
	}
	
	aboutHMPanel.cfg.setProperty('visible', true);
}

function createAboutHMPanel()
{
	var div = document.getElementById('aboutHMPanel');
	aboutHMPanel = new YAHOO.widget.Panel(div, {
		width:"400px",
		visible:false,
		fixedcenter:true,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	aboutHMPanel.render(document.body);
	div.style.display = "";
}

function hideAboutHMPanel(){
	if(null != aboutHMPanel){
		aboutHMPanel.cfg.setProperty('visible', false);
	}
}

<s:if test="%{userContext != null && userContext.getUserName().length() > 0}">
//common function for profile edit page (folding label)
function insertFoldingLabelContext(labelName, contentId){
	document.writeln('<span style="cursor: pointer;" onclick="alternateFoldingContent(\''+ contentId + '\');">');
	document.writeln('<img id="' + contentId + 'ShowImg" src="<s:url value="/images/expand_plus.gif" includeParams="none"/>" \
			alt="Show Option" class="expandImg" style="display: inline"/>' + 
			'<img id="' + contentId + 'HideImg" src="<s:url value="/images/expand_minus.gif" includeParams="none"/>" \
			alt="Hide Option" class="expandImg" style="display: none"/>');
	document.writeln(labelName);
	document.writeln('</span>');

	//adjust icons after page loaded!
	YAHOO.util.Event.onDOMReady(function () {
		adjustFoldingIcon(contentId);
	});
}

var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

function alternateFoldingContent(contentId){
	var contentEl = Get(contentId);
	var cssStyle = contentEl.style.display=="none" ? "":"none";
	showHideContent(contentId, cssStyle);
}

function showHideContent(contentId, cssStyle){
	var contentEl = Get(contentId);
	if(contentEl) {
		contentEl.style.display=cssStyle;
		adjustFoldingIcon(contentId);
	}
}

function adjustFoldingIcon(contentId){
	var contentEl = Get(contentId);
	if(contentEl == null){return;}
	var showEl = Get(contentId+"ShowImg");
	var hideEl = Get(contentId+"HideImg");
	if(contentEl.style.display=="none"){
		showEl&&(showEl.style.display = "inline");
		hideEl&&(hideEl.style.display = "none");
	}else{
		showEl&&(showEl.style.display = "none");
		hideEl&&(hideEl.style.display = "inline");
	}
}
</s:if>
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
		<td colspan="2">
			<s:if test="%{easyMode}">
				<div style="position: relative;" class="skin_hme">
			</s:if>
			<s:else>
				<div style="position: relative;">
			</s:else>
			<div id="browser_compatibility" class="unsupported"
				style="display: none;"></div>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{easyMode}">
						<td style="padding-left: 5px;" class="top_nav_bg" width="88"><img
							id="logo_img" src="<s:url value="/images/company_logo.png"/>"
							width="88" height="39" alt="" class="dblk"></td>
						<td class="top_nav_bg" width="100%">&nbsp;</td>
						<td width="680"><img
							src="<s:url value="/images/hm/header-light-graphic.png" includeParams="none"/>"
							width="680" alt="" class="dblk"></td>
					</s:if>
					<s:else>
						<td class="top_nav_bg" style="padding-left: 5px;">
							<s:if test="%{oEMSystem}">
								<img id="logo_img" src="<s:url value="/images/company_logo_reverse.png"/>"
									width="255" height="55" alt="" class="dblk" />
							</s:if>
							<s:else>
								<img id="logo_img" src="<s:url value="/images/company_logo_reverse.png"/>"
									width="128" height="55" alt="" class="dblk" />
							</s:else>
						</td>
						<td class="top_nav_bg" width="100%"></td>
						<td class="top_nav_bg" width="28"><div style="width: 28px;"></div></td>
					</s:else>
					<td class="top_nav_right_bg" align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<s:if test="%{teacherView}">
							<tr>
								<td class="hm_logo"><b>Teacher</b>View</td>
							</tr>
							<tr>
								<td class="hm_version" align="right" valign="bottom" colspan="2">
									<s:property
										value="%{versionInfo.getTvMainVer()}" />r<s:property
										value="%{versionInfo.getTvSubVer()}" /><s:text name="hm.version.subversion"/>
								</td>
							</tr>
						</s:if>
						<s:else>
							<tr>
								<s:if test="%{planningOnly||planner}">
									<td class="hm_logo">Planner</td>
								</s:if>
								<s:else>
									<s:if test="%{oEMSystem}">
										<td class="hm_logo" nowrap="nowrap"><s:property value="%{systemNmsNameTop}" escapeHtml="false"/></td>
									</s:if>
									<s:else>
									<td class="hm_logo"><s:property value="%{systemNmsNameTop}" escapeHtml="false"/></td>
								</s:else>
								</s:else>
								<td class="hm_logo" style="padding: 0 5px 0 2px;font-size: 9px;"
									valign="top">&#174</td>
							</tr>
							<tr>
								<td class="hm_version" align="right" valign="bottom" colspan="2">
								<s:if test="%{!(planningOnly||planner)}">
									<s:if test="%{easyMode}">Express&nbsp;&nbsp;</s:if>
									<s:elseif test="%{fullMode}">Enterprise&nbsp;&nbsp;</s:elseif>
								</s:if>
								<s:property
									value="%{versionInfo.getMainVersion()}" />r<s:property
									value="%{versionInfo.getSubVersion()}" /><s:text name="hm.version.subversion"/>
								</td>
							</tr>
						</s:else>
					</table>
					</td>
				</tr>
			</table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="top_menu_bg">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="10"><img
								src="<s:url value="/images/spacer.gif" includeParams="none"/>"
								width="10" alt="" class="dblk"></td>
						</tr>
					</table>
					</td>
					<td class="top_menu_bg" width="100%">&nbsp;</td>
					<s:if
						test="%{userContext != null && userContext.getUserName().length() > 0}">
						<s:if test="%{!easyMode}">
							<td class="top_menu_bg" width="28"><div style="width: 28px;"></div></td>
						</s:if>
						<td class="top_menu_bg">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<s:if
									test="%{userContext != null && userContext.getUserName().length() > 0}">
									<td id="login_name" class="top_right_menu_bg top_menu_item"
										style="color: #444;" nowrap="nowrap"><s:property
										value="%{userContext.domainUserName}" /></td>
									<s:if test="%{easyMode}">
										<td width="4" id="td_logout"><img
											src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
											width="4" alt="" class="dblk"></td>
									</s:if>
									<s:else>
										<td width="2" id="td_logout"><img
											src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
											width="2" alt="" class="dblk"></td>
									</s:else>
									<s:if test="%{showStagingSwitch}">
										<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
											class="top_right_menu" href="<s:property value="%{myHivePage}"/>">MyHive</a></td>
										<s:if test="%{easyMode}">
											<td width="4"><img
												src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
												width="4" alt="" class="dblk"></td>
										</s:if>
										<s:else>
											<td width="2"><img
												src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
												width="2" alt="" class="dblk"></td>
										</s:else>
									</s:if>
										<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
											class="top_right_menu"
											href="<s:url value="logout.action" includeParams="none"/>">Log
										Out</a></td>
								</s:if>
								<s:else>
									<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
										class="top_right_menu"
										href="<s:url value="login.action" includeParams="none"/>">Log
									In</a></td>
								</s:else>
								<s:if test="%{easyMode}">
									<td width="4"><img
										src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
										width="4" alt="" class="dblk"></td>
								</s:if>
								<s:else>
									<td width="2"><img
										src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
										width="2" alt="" class="dblk"></td>
								</s:else>
								<td class="top_right_menu_bg top_menu_item" id="about" width="40px"
									style="padding-right: 2px;"><a class="top_right_menu"
									href="javascript:void(0);" onclick="javascript:openAboutPage(); return false;">About</a></td>
								<td width="100%" class="top_right_menu_bg">&nbsp;</td>
							</tr>
						</table>
						</td>
					</s:if>
					<s:else>
						<td class="top_menu_bg">&nbsp;</td>
						<td class="top_menu_bg" width="100%">&nbsp;</td>
					</s:else>
				</tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td class="dark" valign="top">
		</td>
		<td valign="top" style="padding: 10px 4px 0px 5px;" align="center">
			<tiles:insertAttribute name="body" />
		</td>
	</tr>
</table>
<div id="aboutHMPanel" style="display: none;">
	<s:if test="%{teacherView}">
		<div class="hd">About TeacherView</div>
	</s:if>
	<s:else>
		<div class="hd">About <s:property value="%{systemNmsName}"/></div>
	</s:else>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td style="padding-left: 5px;">
					<img id="logo_img" src="<s:url value="/images/company_logo.png"/>" alt="" class="company_logo">
				</td>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<s:if test="%{hMOnline && domain.domainName!='home'}">
						<tr>
							<td class="labelT1" width="100px">
								<s:text name="admin.vhmMgr.vhmID" />:
							</td>
							<td class="labelT1">
								<s:property value="domain.vhmID" />
							</td>
						</tr>
						</s:if>
						<tr>
							<td class="labelT1" width="100px">
								Software Version:
							</td>
							<td class="labelT1">
								<s:if test="%{teacherView}">
									<s:property
										value="%{versionInfo.getTvMainVer()}" />r<s:property
										value="%{versionInfo.getTvSubVer()}" /><s:text name="hm.version.subversion"/>
								</s:if>
								<s:else>
									<s:property
										value="%{versionInfo.getMainVersion()}" />r<s:property
										value="%{versionInfo.getSubVersion()}" /><s:text name="hm.version.subversion"/>
								</s:else>
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="100px">
								Build Time:
							</td>
							<td class="labelT1">
								<s:property value="sessionVersionInfo.getBuildTime()" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding:10px 4px 10px 4px" colspan=2>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="sepLine">
								<img src="<s:url value="/images/spacer.gif"/>" height="1"
									class="dblk" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="right" colspan="2">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="OK"
								class="button" onClick="hideAboutHMPanel();"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>