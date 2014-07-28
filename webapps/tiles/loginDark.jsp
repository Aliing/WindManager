<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

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
.login_user_pass input{
	border: 1px solid #8B96A9;
	background: #fff no-repeat left;
}
.login_user_pass input:focus{
	border-color: #0495D3;
}

</style>

<script>
var formName = 'authenticate';
function onLoadEvent() {
	<s:iterator value="actionErrors">
		showError("<s:property escape="false" />");
	</s:iterator>
	document.forms[formName].userName.focus();
	document.forms[formName].userName.onkeydown = loginKeyHandler;
	document.forms[formName].password.onkeydown = loginKeyHandler;
	if(YAHOO.env.ua.ie == 6){
		//fix PNG bug in IE6
		fixLogoPng();
	}
	initUsernameTip();
	// hide the message banner if jump back to login page
    var msgBanner = document.getElementById("contentCtrl");
    if(msgBanner) {
        msgBanner.style.display = "none";
    } 
}

function initUsernameTip(){
	var usernameEl = document.getElementById("userName");
	if(usernameEl == null){return;}
	var updateUsernameTip = function(){
			if(usernameEl.value.length == 0){
				usernameEl.style.backgroundImage = "url(<s:url value="/images/username-tip.png" includeParams="none"/>)";
			}else{
				usernameEl.style.backgroundImage = "none";
			}
		}
	window.setInterval(updateUsernameTip, 100);
<%-- Doesn't works for FF when add the username by cache value.
	var properytchangeHandle = function(){
			if(event.propertyName == "value"){
				updateUsernameTip();
			}
		}
	updateUsernameTip();
	if(YAHOO.env.ua.ie){//IE
		usernameEl.onpropertychange = properytchangeHandle;
	}else{//DOM
		YAHOO.util.Event.addListener("userName", "input", updateUsernameTip);
	}
--%>
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

</head>
<body class="body_bg client skin_hm" onload="onLoadEvent()"
	onunload="onUnloadEvent()" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">
<script type="text/javascript">
	//avoid access hm from iframe
	if (top.location != self.location){
		top.location=self.location;
	}
</script>
<s:form action="authenticate" id="authenticateFormID" name="authenticate">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2"><tiles:insertAttribute name="topPane" /></td>
		</tr>
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="100" alt="" class="dblk" /></td>
		</tr>
		<tr id="note" style="display: none">
			<td style="padding: 4px 0 0 0px" align="center">
			<div>
			<table border="0" cellspacing="0" cellpadding="0" width="355"
				class="noteError">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="noteError" id="noteText" align="center">&nbsp;</td>
				</tr>
				<tr>
					<td height="6"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="3" alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td height="34%" align="center" valign="middle">
			<table width="345" border="1" cellpadding="0" cellspacing="0"
				bordercolor="#000000">
				<tr>
					<td>
					<table width="345" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="skin_hm">
							<table width="345" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="top_menu_bg" style="height: 25px; border-bottom: none;">&nbsp;</td>
									<td width="98"><img
										src="<s:url value="/images/hm/login.jpg" />" width="98"
										height="25" alt="" class="dblk" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td class="login_user_pass">
							<table width="345" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10" valign="middle" colspan="2"><img
										src="<s:url value="/images/spacer.gif" />" width="1"
										height="10" alt="" class="dblk" /></td>
								</tr>
								<tr>
									<td width="100" height="40" valign="middle" class="tab-up" style="padding-left:20px;">
									Username:</td>
									<td height="40" style="padding-left:10px" valign="middle"><s:textfield
										name="userName" id="userName" cssStyle="width:200px" /></td>
								</tr>
								<tr>
									<td width="100" height="40" valign="middle" class="tab-up" style="padding-left:20px">
									Password:</td>
									<td height="40" style="padding-left:10px" valign="middle"><s:password
										name="password" cssStyle="width:200px" /></td>
									<td width="100" height="40" valign="middle"></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td width="210" height="50" valign="middle" style="padding-left:10px"><a
										href="javascript:submitAction('login')"><img class="dinl"
										src="<s:url value="/images/hm/button-login-large.png" />" /></a></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form>
</body>
</html>
<script>
if (document.getElementById("authenticateFormID").parentNode.nodeName!='BODY'){
	if (window.prevantPrompt) {
		window.prevantPrompt();
	}
	window.location.href = 'login.action';
}
</script>