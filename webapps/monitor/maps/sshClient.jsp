<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<html>
<head>
<title><s:property value="%{sshWindowTitle}" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<script type="text/javascript" src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<style>
html, body {
	width: 100%;
	height: 100%;
	border: none;
	padding: 0 0 0 0;
	margin: 0 0 0 0;
	/*	overflow: hidden; */
}
</style>

<script>
var formName = "sshClient";
function beforeUnloadEvent() {
	try{
		var url = '<s:url action="sshClient" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = "closeSshTunnel";
		YAHOO.util.Connect.setForm(document.getElementById(formName));
		YAHOO.util.Connect.asyncRequest('post', url, {success : function(){/*dummy*/}}, null);
	}catch(e){}
}
function onLoadPage(){

}
function submitAction(operation) {
	document.configTest.operation.value = operation;
    document.configTest.submit();
}

</script>
</head>
<body onbeforeunload="beforeUnloadEvent();">
<script type="text/javascript">
	//avoid access hm from iframe
	if (top.location != self.location){
		top.location=self.location;
	}
</script>
<s:form action="sshClient" method="post" >
<s:hidden name="hiveApId"></s:hidden>
<s:hidden name="operation"></s:hidden>
</s:form>
<div style="width: 100%; height: 99%">
<applet codebase="<s:url value="/applets/"/>" hspace="0" vspace="0"
	code="com.ericdaugherty.soht.client.applet.AppletProxy.class"
	archive="soht-client.jar" width="100%" height="100%">
<param name="host" value="<s:property value="%{host}" />">
<!--
<param name="user" value="<s:property value="%{user}" />">
<param name="password" value="<s:property value="%{password}" />">
-->
<param name="port" value="<s:property value="%{port}" />">
<param name="proxyTimeout" value="<s:property value="%{proxyTimeout}" />">
<param name="serverURL" value="<s:property value="%{serverURL}" />">
<param name="serverUsername" value="<s:property value="%{serverUsername}" />">
<param name="serverPassword" value="<s:property value="%{serverPassword}" />">
<!-- 
<param name="localPort" value="<s:property value="%{localPort}" />">
 -->
<param name="httpProxyHost" value="<s:property value="%{httpProxyHost}" />">
<param name="httpProxyPort" value="<s:property value="%{httpProxyPort}" />">
<param name="httpProxyUsername" value="<s:property value="%{httpProxyUsername}" />">
<param name="httpProxyPassword" value="<s:property value="%{httpProxyPassword}" />">
<param name="persistentConnectivity" value="<s:property value="%{persistentConnectivity}" />">
</applet>
<s:property value="%{requiredJreVersion}" escapeHtml="false" />
</div>
</body>
</html>

