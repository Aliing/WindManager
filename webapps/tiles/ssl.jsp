<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
function onLoadEvent() {
	ssl();
}
function ssl() {
    var href = document.location.href;
    var sslHref = href.replace("http:", "https:");
    if (sslHref.indexOf("ssl.action") > 0) {
	    sslHref = sslHref.replace("ssl.action", "login.action");
    }
    if (document.location.port == 8080) {
	    sslHref = sslHref.replace(document.location.port, "8443");
	}
    document.location.href = sslHref;
}
</script>

<html>
<head>
<title><s:text name="feature.ssl" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<script type="text/javascript"
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" />"></script>
<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<style>
.login {
	font-size: 11pt;
}
</style>
</head>
<body class="body_bg" onload="onLoadEvent()"
	leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
	</tr>
	<tr id="note" style="display:none">
		<td style="padding: 4px 0 0 4px">
		<div>
		<table border="0" cellspacing="0" cellpadding="0" width="500"
			class="note">
			<tr>
				<td class="noteError" id="noteText">&nbsp;</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td style="padding: 4px 0 0 4px">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><img
					src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
				<td rowspan="2" class="menu_bg" style="padding: 6px 0 0 0;"
					valign="top" width="330">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td class="crumb" colspan="2">SSL Redirection</td>
					</tr>
					<tr>
						<td colspan="2" style="padding: 4px 0 12px 0;">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine" width="100%"><img
									src="<s:url value="/images/spacer.gif" includeParams="none"/>"
									height="1" class="dblk" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td class="login">HTTP is disabled. Click <a
							href="javascript: ssl()">here</a> to redirect to HTTPS.</td>
					</tr>
				</table>
				</td>
				<td><img
					src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
			</tr>
			<tr>
				<td class="menu_bg" height="50"></td>
				<td class="menu_bg"></td>
			</tr>
			<tr>
				<td><img
					src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
				<td class="menu_bg" height="1"><img
					src="<s:url value="/images/spacer.gif" includeParams="none"/>"
					width="100%" height="1" alt="" class="dblk" /></td>
				<td><img
					src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
					width="9" height="9" alt="" class="dblk" /></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
