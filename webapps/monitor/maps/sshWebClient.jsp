<!-- quirks for IE -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<html>
<head>
<title>SSH Client</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<link rel="stylesheet" type="text/css" title="White"
	href="<s:url value="/css/sshSkins/white.css" includeParams="none" />" />
<link rel="alternate stylesheet" type="text/css" title="Black"
	href="<s:url value="/css/sshSkins/black.css" includeParams="none" />" />
<script
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/connection/connection-min.js" includeParams="none" />"></script>
<script src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>
<style type="text/css">
/* GLOBAL */
* {
	margin: 0;
	padding: 0;
	font-weight: normal;
	font-size: 10pt;
	font-family: courier new, courier, verdana, helvetica, arial, fixedsys,
		sans-serif;
}

/* Showing vertical scrollbar always */
html {
	overflow-y: scroll;
}

form {
	padding: 0;
	margin: 0;
}

#switchBtn {
	/*display: none;*/
	position: absolute;
	right: 160px;
	top: 10px;
}

#connectionBtn {
	position: absolute;
	right: 30px;
	top: 10px;
	z-index: 20;
}

#topLogin_box {
	display: none;
	position: absolute;
	right: 20px;
	text-align: left;
	top: 5px;
	width: 650px;
	height: 22px;
	padding: 6px 6px;
	z-index: 10;
}

#content {
	margin: 5px 0 0 5px;
}

#info {
	position: absolute;
	top: 50px;
	left: 20px;
}

#input {
	padding: 0 1px 0 0;
	border: 0;
	width: 550px;
}
</style>
<script type="text/javascript">
<s:if test="%{denyAccess}">
function onLoadEvent(){}
function onUnloadEvent(){}
function haltEvent(event){}
</s:if>
<s:else>
var formName = "sshWebClient";
var endWithMore = false;
var cursorExist = false;
function setCursor(flag){
	cursorExist = flag;
}

function getKey( event ){
    if (window.event){
        return window.event.keyCode;
    }else if (event){
        return event.which;
    }else{
        return null;
    }
}

function keyPressAction(event) {
	var keyCode = getKey(event);

	if (keyCode != undefined) {
		if (keyCode == 13) {// Enter
			document.getElementById(formName + "_inputConsole").value = document.getElementById("input").value;
			requestCli("requestCli", keyCode);
		} else if (keyCode == 63) {// '?' character
			document.getElementById(formName + "_inputConsole").value = document.getElementById("input").value;
			requestCli("requestCli", keyCode);
	  	} else {
		  	//if is end with '--More--' string
		  	if(endWithMore){
				document.getElementById(formName + "_inputConsole").value = document.getElementById("input").value;
				requestCli("requestCli", keyCode);
			}
		}
	}
}

function keyDownAction(event) {
	var keyCode = getKey(event);
	if (keyCode == 9) {// Tab
		var value = document.getElementById("input").value.trim();
		if (value.length >= 0 ){//fix complete key word with more that one choice.
			document.getElementById(formName + "_inputConsole").value = document.getElementById("input").value;
			requestCli("requestCli", keyCode);
		} else {
			document.getElementById("input").focus();
		}

		return false;
	} else if (keyCode == 38) {// Up
		requestCli("requestUp", keyCode);
	} else if (keyCode == 40) {//Down
		requestCli("requestDown", keyCode);
	}
}

function focusAction(event) {
	clearInterval(intervalId);
	setCursor(true);
}

function inputKeyDownAction(event) {
	var keyCode = getKey(event);
	if(keyCode == 13){//Enter
		reConnect(document.getElementById("connect"));
	}
}

function haltEvent(event){
	var keyCode = getKey(event);
	var inputEl = document.getElementById("input");
	var event = window.event || event;
	// force input filed to be focused.
	if(null != inputEl && !inputEl.disabled && !cursorExist){
		if(!event.ctrlKey && ((keyCode >=48 && keyCode <=57)
			||(keyCode >=65 && keyCode <=90)
			||(keyCode >=96 && keyCode <=111)
			||(keyCode >=186 && keyCode <=192)
			||(keyCode >=219 && keyCode <=222)
			|| keyCode == 13))
		inputEl.focus();
		inputEl.value += "";
	}
	// Ctrl + C
	if (event.ctrlKey && keyCode == 67){
		document.getElementById(formName + "_inputConsole").value = "C";
		requestCli("requestCli", 17);
	}
}

function focusInputEl(){
//	alert("aa");
	document.getElementById("input").focus();
}

var allowed = true;//avoid multiple request at the same time;
function requestCli(operation, keyCode){
	if(!allowed){
		return;
	}
	// argument formId can be the id or name attribute value of the
	// HTML form, or an HTML form object.
	document.getElementById(formName + "_operation").value = operation;
	document.getElementById(formName + "_keyCode").value = keyCode;
	var formObject = document.getElementById('sshWebClient');
	YAHOO.util.Connect.setForm(formObject);
	// This facilitates a POST transaction.
	// An HTTP GET can be used as well.

	var url = "<s:url action='sshWebClient' includeParams='none' />";
//	alert(url);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:cliInfoResult,failure:connectedFailed,timeout: 60000}, null);
	allowed = false;//lock the request function;
}

var intervalId;
var cliInfoResult = function(o){
	eval("var result = " + o.responseText);
	//hide the initial error message;
	hm.util.hide('notes');
	if(result.suc){
		//<pre> in CSS doesn't work in IE, add here.
		document.getElementById("shellContent").innerHTML = "<pre>" + result.context + "</pre>";
		//document.getElementById("shellContent").scrollTop = 2000;
		resetFloatElements();
		intervalId = setInterval("focusInputEl()", 200);
		endWithMore = isEndWithMore(result.context);
	}
	if(result.closed){//disconnect successfully
		connectionClosed();
	}
	if(result.opened){//connect successfully
		connectionOpened();
		//update connetion info;
		document.getElementById(formName + "_connectionInfo").value = result.connectionInfo;
		document.getElementById(formName + "_connectionId").value = result.connectionId;
		document.getElementById(formName + "_channelId").value = result.channelId;
		// refresh content;
		//<pre> in CSS doesn't work in IE, add here.
		document.getElementById("shellContent").innerHTML = "<pre>" + result.context + "</pre>";
		resetFloatElements();
		intervalId = setInterval("focusInputEl()", 200);
	}
	if(result.command != undefined){
		document.getElementById("input").value = result.command;
	}
	if(result.error){//error message
//		alert(result.error);
		var el = document.getElementById("processing");
		hm.util.reportFieldError(el, result.error);
		connectionClosed();
	}
	allowed = true;//unlock the request function;
}
var connectedFailed = function(o) {
//	alert("connected failed.");
	connectionClosed();
	allowed = true;//unlock the request function;
}

function connectionClosed(){
	document.getElementById(formName + "_username").disabled = false;
	document.getElementById(formName + "_pwd").disabled = false;
	document.getElementById(formName + "_tunnelTimeout").disabled = false;
	document.getElementById(formName + "_username").className = "activeInput";
	document.getElementById(formName + "_pwd").className = "activeInput";
	document.getElementById(formName + "_tunnelTimeout").className = "activeInput";
	//change content style;
	document.getElementById("shellContent").className = "inactiveContent";
	//show login box;
	document.getElementById("topLogin_box").className = "activeLabel";
	document.getElementById("topLogin_box").style.display = "block";
	//change connect button;
	document.getElementById("connect").className = "activeLabel";
	document.getElementById("connect").value = "Connect";
	document.getElementById("connect").disabled = false;
	if(document.getElementById("input")){
		document.getElementById("input").disabled = true;
	}
	document.getElementById("shellContent").innerHTML = "";
}

function connectionOpened(){
	document.getElementById(formName + "_username").disabled = true;
	document.getElementById(formName + "_pwd").disabled = true;
	document.getElementById(formName + "_tunnelTimeout").disabled = true;
	document.getElementById(formName + "_username").className = "inactiveInput";
	document.getElementById(formName + "_pwd").className = "inactiveInput";
	document.getElementById(formName + "_tunnelTimeout").className = "inactiveInput";
	//change content style;
	document.getElementById("shellContent").className = "activeContent";
	//hide login box;
	document.getElementById("topLogin_box").className = "inactiveLabel";
	document.getElementById("topLogin_box").style.display = "none";
	//change connect button;
	document.getElementById("connect").className = "activeLabel";
	document.getElementById("connect").value = "Disconnect";
	document.getElementById("connect").disabled = false;
}

function reConnect(btn){
	if("Disconnect" == btn.value){//disconnect;
		requestCli("disconnectConnection", -1);
		btn.disabled = true;
		btn.className = 'inactiveLabel';
	}else if ("Connect" == btn.value) {//connect;
		if(validate("Connect")){
			requestCli("connect",-1);
			btn.disabled = true;
			btn.className = 'inactiveLabel';
		}
	}
}

function validate(operation){
	if("Connect" == operation){
		var timeoutEl = document.getElementById(formName + "_tunnelTimeout");
		var reportEl = document.getElementById("processing");
		if (timeoutEl.value.length == 0) {
			hm.util.reportFieldError(reportEl, '<s:text name="error.requiredField"><s:param>"Timeout"</s:param></s:text>');
			timeoutEl.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(timeoutEl.value, 'Timeout',
		                                           <s:property value="0" />,
		                                           <s:property value="6000" />);
		if (message != null) {
			hm.util.reportFieldError(reportEl, message);
			timeoutEl.focus();
			return false;
		}
	}
	return true;
}

function switchSkin(title){
	var btnEl = document.getElementById("switch");
	setStyle(title);
	if("White" == title){
		btnEl.value = "Black";
	}else if ("Black" == title){
		btnEl.value = "White";
	}
	//focus input element;
	var obj = document.getElementById('input');
	if(obj != undefined){
		obj.focus();
	}
}

function setStyle(title) {
	var i, links;
	links   = document.getElementsByTagName("link");
	for(i=0; i < links.length; i++) {
		if(links[i].getAttribute("rel").indexOf("style") != -1
		   && links[i].getAttribute("title")) {
			links[i].disabled = true;
		}
		if(links[i].getAttribute("title").indexOf(title) != -1){
			links[i].disabled = false;
		}
	}
}

function getActiveStyleSheet() {
	var i, links;
	links   = document.getElementsByTagName("link");
	for(i=0; i < links.length; i++) {
		if(links[i].getAttribute("rel").indexOf("style") != -1
		&& links[i].getAttribute("title") && !links[i].disabled) {
			return links[i].getAttribute("title");
		}
	}
	return null;
}

//create cookie
function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}else {
		expires = "";
	}
	document.cookie = name+"="+value+expires+";path=/";
}
//find cookie
function readCookie(name) {
   var nameEQ = name + "=";
   var ca = document.cookie.split(';');
   for(var i=0;i < ca.length;i++) {
     var c = ca[i];
     while (c.charAt(0)==' '){
     	c = c.substring(1,c.length);
     }
     if (c.indexOf(nameEQ) == 0){
     	return c.substring(nameEQ.length,c.length);
     }
   }
   return null;
}


function closeWindow() {
//	alert("window closing...");
	if(window.opener && window.opener.ajaxRequest){
		var connectionId = document.getElementById(formName + "_connectionId").value;
		var connectionInfo = document.getElementById(formName + "_connectionInfo").value;
		var channelId = document.getElementById(formName + "_channelId").value;
		var url = "<s:url action='sshWebClient' includeParams='none' />?operation=disconnectChannel&connectionId=" 
			+ connectionId +"&connectionInfo=" + connectionInfo + "&channelId=" + channelId;
		window.opener.ajaxRequest(null, url);
	}else{
		allowed = true; // disconnectChanel request need to send out!
		requestCli("disconnectChannel",-1);
	}
}


function showBox(topLogin_box){
	var btn_el = document.getElementById("connect");
	if('Disconnect' == btn_el.value){
		var obj = document.getElementById(topLogin_box);
		obj.style.display = 'block';
	}
}
function hideBox(topLogin_box){
	var btn_el = document.getElementById("connect");
	if('Disconnect' == btn_el.value){
		var obj = document.getElementById(topLogin_box);
		obj.style.display = 'none';
	}
}

window.onscroll = resetFloatElements;

function resetFloatElements(){
	var scrollTop = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
	document.getElementById("connectionBtn").style.top=(scrollTop + 10 )+"px";
	document.getElementById("switchBtn").style.top=(scrollTop + 10 )+"px";
	document.getElementById("topLogin_box").style.top=(scrollTop + 5 )+"px";
}

function onLoadPage(){
	<s:if test="%{connected}">
		connectionOpened();
	</s:if><s:else>
		connectionClosed();
	</s:else>
	var obj = document.getElementById('input');
	if(obj != undefined){
		obj.focus();
	}
}

function onLoadCookie(){
	var cookie = readCookie("style");
	var title = cookie ? cookie : "White";
	switchSkin(title);
}

function onLoadEvent() {
//	onLoadNotes();
	onLoadPage();
	onLoadCookie();
}
function onUnloadEvent(){
	closeWindow();
	var title = getActiveStyleSheet();
	createCookie("style", title, 365);
}

function isEndWithMore(message){
	message = message || "";
	if(message.indexOf("--More-- <span id='shell-cursor'>")<0){
		return false;
	}else{
		return true;
	}
}
</s:else>
</script>
</head>
<body onload="onLoadEvent();" onbeforeunload="onUnloadEvent();"
	onkeydown="haltEvent(event);">
<div id="info"><tiles:insertDefinition name="notes" /></div>
<s:if test="%{denyAccess}">
<div><s:property value="%{promptMessage}"/></div>
</s:if>
<s:else>
<div id="content"><s:form action="sshWebClient" onsubmit="return false;">
	<s:hidden name="connectionId" />
	<s:hidden name="channelId" />
	<s:hidden name="connectionInfo" />
	<s:hidden name="operation" />
	<s:hidden name="inputConsole" />
	<s:hidden name="keyCode" />

	<div id="switchBtn"><input type="button"
		title="Change display style" value="Black"
		onclick="switchSkin(this.value);" id="switch" class="activeLabel" /></div>
	<div id="connectionBtn"><input type="button" onclick="reConnect(this);"
		id="connect" value="Disconnect" /></div>
	<!-- <div id="connectionBtn"><input type="button"
		onmouseover="showBox('topLogin_box');"
		onmouseout="hideBox('topLogin_box');" onclick="reConnect(this);"
		id="connect" value="Disconnect" /></div>  -->
	<div id="topLogin_box"><s:text name="Admin Name:" /><s:textfield
		onkeydown="inputKeyDownAction(event);" name="username" size="16"
		disabled="true" onfocus="setCursor(true);" onblur="setCursor(false);" />
	<s:text name="Password:" /><s:password
		onkeydown="inputKeyDownAction(event);" showPassword="true" name="pwd"
		size="16" disabled="true" onfocus="setCursor(true);"
		onblur="setCursor(false);" /> <s:text name="Timeout:" /><s:textfield
		title="(0-6000 in minutes 0: No Timeout)"
		onkeydown="inputKeyDownAction(event);"
		onkeypress="return hm.util.keyPressPermit(event,'ten');"
		name="tunnelTimeout" size="4" disabled="true"
		onfocus="setCursor(true);" onblur="setCursor(false);" /></div>
	<div id="shellContent"><pre><s:property
		value="%{initialContent}" escape="false" /></pre></div>
</s:form></div>
</s:else>
</body>
</html>