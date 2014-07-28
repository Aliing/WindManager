<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script type="text/javascript">

var formName = "sshClient";

function onLoadPage() {
	document.getElementById(formName + "_proxyTimeout").focus();
}

function submitAction(operation){
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation){
	var proxyTimeout = document.getElementById(formName + "_proxyTimeout");
	// var localPort = document.getElementById(formName + "_localPort");
	var enableHttpProxy = document.getElementById(formName + "_enableHttpProxy");
	if(proxyTimeout.value.length == 0){
		hm.util.reportFieldError(proxyTimeout, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.proxyTimeout" /></s:param></s:text>');
		proxyTimeout.focus();
		return false;
 	}
	var message = hm.util.validateIntegerRange(proxyTimeout.value, '<s:text name="hm.ssh.config.proxyTimeout" />',
	                                           <s:property value="1" />,
	                                           <s:property value="60" />);
	if (message != null) {
		hm.util.reportFieldError(proxyTimeout, message);
		proxyTimeout.focus();
		return false;
	}
	/**
	if(localPort.value.length == 0){
		hm.util.reportFieldError(localPort, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.localPort" /></s:param></s:text>');
		localPort.focus();
		return false;
 	}
	var message = hm.util.validateIntegerRange(localPort.value, '<s:text name="hm.ssh.config.localPort" />',
	                                           <s:property value="1" />,
	                                           <s:property value="65535" />);
	if (message != null) {
		hm.util.reportFieldError(localPort, message);
		localPort.focus();
		return false;
	}
	**/
	if(enableHttpProxy.checked){
		var httpProxyHost = document.getElementById(formName + "_httpProxyHost");
		var httpProxyPort = document.getElementById(formName + "_httpProxyPort");
		var httpProxyUsername = document.getElementById(formName + "_httpProxyUsername");
		var httpProxyPassword = document.getElementById(formName + "_httpProxyPassword");
		if (httpProxyHost.value.length == 0) {
			hm.util.reportFieldError(httpProxyHost, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.httpProxy.host" /></s:param></s:text>');
			httpProxyHost.focus();
			return false;
	  	}
		if(httpProxyPort.value.length == 0){
			hm.util.reportFieldError(httpProxyPort, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.httpProxy.port" /></s:param></s:text>');
			httpProxyPort.focus();
			return false;
	 	}
		var message = hm.util.validateIntegerRange(httpProxyPort.value, '<s:text name="hm.ssh.config.httpProxy.port" />',
		                                           <s:property value="1" />,
		                                           <s:property value="65535" />);
		if (message != null) {
			hm.util.reportFieldError(httpProxyPort, message);
			httpProxyPort.focus();
			return false;
		}
		if (httpProxyUsername.value.length > 0 || httpProxyPassword.value.length > 0) {
			if (httpProxyUsername.value.length == 0) {
				hm.util.reportFieldError(httpProxyUsername, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.httpProxy.username" /></s:param></s:text>');
				httpProxyUsername.focus();
				return false;
		  	}
			if (httpProxyPassword.value.length == 0) {
				hm.util.reportFieldError(httpProxyPassword, '<s:text name="error.requiredField"><s:param><s:text name="hm.ssh.config.httpProxy.password" /></s:param></s:text>');
				httpProxyPassword.focus();
				return false;
		  	}
	  	}
	}
	return true;
}

function insertPageContext(){
	document.writeln('<td class="crumb" nowrap><s:property value="sshConfigWindowTitle" /></td>');
}

function useHttpProxy(checked){
	var httpProxyHost = document.getElementById(formName + "_httpProxyHost");
	var httpProxyPort = document.getElementById(formName + "_httpProxyPort");
	var httpProxyUsername = document.getElementById(formName + "_httpProxyUsername");
	var httpProxyPassword = document.getElementById(formName + "_httpProxyPassword");
	var persistentConnectivity = document.getElementById(formName + "_persistentConnectivity");
	var httpProxyConfigTr1 = document.getElementById("httpProxyConfigTr1");
	var httpProxyConfigTr2 = document.getElementById("httpProxyConfigTr2");
	httpProxyConfigTr1.style.display = checked? "" : "none";
	httpProxyConfigTr2.style.display = checked? "" : "none";
	httpProxyHost.disabled = !checked;
	httpProxyPort.disabled = !checked;
	httpProxyUsername.disabled = !checked;
	httpProxyPassword.disabled = !checked;
	persistentConnectivity.disabled = !checked;
}

function onLoadPage(){
	var enableHttpProxy = document.getElementById(formName + "_enableHttpProxy");
	useHttpProxy(enableHttpProxy.checked);
}
</script>
<style type="text/css">
html,body {
	background-color: #DDDEEE;
}
</style>
<div id="content" style="padding-top: 5px;">
	<s:form action="sshClient" method="post" >
	<s:hidden name="hiveApId"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Start Proxy"
						class="button" onClick="submitAction('ssh');" <s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="5px">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
								</td>
							</tr>
							<tr>
								<td class="labelT1" width="180px"><label><s:text
									name="hm.ssh.config.proxyTimeout" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="proxyTimeout"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									size="8" maxlength="2" />
									<s:text name="hm.ssh.config.proxyTimeout.note" /></td>
							</tr>
							<!--  
							<tr>
								<td class="labelT1"><label><s:text
									name="hm.ssh.config.localPort" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="localPort"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									size="8" maxlength="5" />
									<s:text name="hm.ssh.config.localPort.note" /></td>
							</tr>
							-->
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-left: 5px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><s:checkbox name="enableHttpProxy" onclick="useHttpProxy(this.checked)"></s:checkbox></td>
								<td class="labelT1" style="padding-left: 0;"><s:text name="hm.ssh.config.enable.httpProxy" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="httpProxyConfigTr1" style="display: none;">
					<td style="padding-left: 15px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="165px"><label><s:text
									name="hm.ssh.config.httpProxy.host" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="httpProxyHost"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="32" maxlength="128" />
									<s:text name="hm.ssh.config.httpProxy.host.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="hm.ssh.config.httpProxy.port" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="httpProxyPort"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									size="8" maxlength="5" />
									<s:text name="hm.ssh.config.httpProxy.port.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="hm.ssh.config.httpProxy.username" /></label></td>
								<td><s:textfield name="httpProxyUsername"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="32" maxlength="64" />
									<s:text name="hm.ssh.config.httpProxy.username.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="hm.ssh.config.httpProxy.password" /></label></td>
								<td><s:password showPassword="true" name="httpProxyPassword"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="32" maxlength="64" />
									<s:text name="hm.ssh.config.httpProxy.password.note" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="httpProxyConfigTr2" style="display: none;">
					<td style="padding-left: 20px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><s:checkbox name="persistentConnectivity"></s:checkbox></td>
								<td class="labelT1" style="padding-left: 0;"><s:text name="hm.ssh.config.httpProxy.persistent" /></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</s:form>
</div>