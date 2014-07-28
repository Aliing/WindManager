<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'authenticate';
function onLoadEvent() {
	<s:iterator value="actionErrors">
		showError("<s:property escape="false" />");
	</s:iterator>
	document.forms[formName].userName.focus();
	document.forms[formName].userName.onkeydown = loginKeyHandler;
	document.forms[formName].password.onkeydown = loginKeyHandler;
	// hide the message banner if jump back to login page
	var msgBanner = document.getElementById("contentCtrl");
	if(msgBanner) {
		msgBanner.style.display = "none";
	} 
}
function onUnloadEvent() {
}
function loginKeyHandler(e) {
	if (!e) {
		// Use window.event for IE
		e = window.event;
	}
	if (e && e.keyCode==13) {
		submitAction('login');
	}
}
function submitAction(operation) {
	if (validate(operation)) {
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (document.forms[formName].userName.value.length == 0) {
		showError('<s:text name="error.usernameRequired"/>');
		return false;
	} else if (document.forms[formName].password.value.length == 0) {
		showError('<s:text name="error.passwordRequired"/>');
		return false;
	}
	return true;
}
function showError(message) {
	var td = document.getElementById("noteText");
	td.removeChild(td.firstChild);
	td.appendChild(document.createTextNode(message));
	YAHOO.util.Dom.setStyle('note', "display", "");
}
</script>

<html>
<head>
<title><s:text name="feature.hmLogin" /></title>
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
<body class="body_bg" onload="onLoadEvent()" onunload="onUnloadEvent()"
	leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<s:form action="authenticate">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
		</tr>
		<tr id="note" style="display:none">
			<td style="padding: 4px 0 0 4px">
			<div>
			<table border="0" cellspacing="0" cellpadding="0" width="500"
				class="noteError">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="noteError" id="noteText">&nbsp;</td>
				</tr>
				<tr>
					<td height="6"></td>
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
						valign="top" width="260">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="crumb" colspan="2">Sign in</td>
						</tr>
						<tr>
							<td colspan="2" style="padding: 4px 0 8px 0;">
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
							<td height="10"></td>
						</tr>
						<tr>
							<td align="right" class="login">Username:</td>
							<td style="padding-left: 10px;"><s:textfield name="userName"
								size="25" maxlength="32" /></td>
						</tr>
						<tr>
							<td height="6"></td>
						</tr>
						<tr>
							<td align="right" class="login">Password:</td>
							<td style="padding-left: 10px;"><s:password name="password"
								size="25" maxlength="32" /></td>
						</tr>
						<tr>
							<td height="8"></td>
						</tr>
						<tr>
							<td></td>
							<td style="padding-left: 9px;"><input type="button"
								name="ignore" value="Sign in" class="button"
								onclick="submitAction('login');" /></td>
						</tr>
					</table>
					</td>
					<td><img
						src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td class="menu_bg" height="115"></td>
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
</s:form>
</body>
</html>
