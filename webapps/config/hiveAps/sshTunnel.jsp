<%@ taglib prefix="s" uri="/struts-tags"%>

<style>
<!--
html {
	overflow: hidden;
}

.required {
	color: #FF0000;
}

#wrap {
	height: 200px;
	width: 99%;
	overflow: auto;
	margin: 5px 0 2px 2px;
}

fieldset {
	margin: 0;
}
-->
</style>

<script type="text/javascript">
<!--

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	//save this reference on the parent
	top.sshTunnelIframeWindow = window;
	if(hostName){
		top.updateSshTunnelPanelTitle(hostName);
	}
	var width = document.documentElement.offsetWidth - 34;
	if(width > 0){
		document.getElementById("wrap").style.width = width + "px";
	}
}

var formName = "sshTunnel";

function requestAction(action){
	if(validate(action)){
		var url = "<s:url action="hiveApToolkit" includeParams="none"></s:url>" + "?operation="+action
				 + "&ignore="+ + new Date().getTime();
		//alert(url);
		Get("content").innerHTML = "&nbsp;";
		ajaxRequest("sshTunnel", url, requestResult);
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
	}
}
function validate(action){
	if("startTunnel" == action){
		var el1 = Get(formName + "_sshServerUrl");
		var message = hm.util.validateName(el1.value, '<s:text name="monitor.hiveAp.ssh.url" />');
    	if (message != null) {
    		hm.util.reportFieldError(el1, message);
        	el1.focus();
        	return false;
    	}
    	var el2 = Get(formName + "_sshServerPort");
    	if(el2.value.length > 0 && el2.value != 22){
			var message = hm.util.validateIntegerRange(el2.value, '<s:text name="monitor.hiveAp.ssh.serverPort" />',
			                                           <s:property value="1025" />,
			                                           <s:property value="65535" />);
			if (message != null) {
				hm.util.reportFieldError(el2, message);
				el2.focus();
				return false;
			}
    	}
    	var el3 = Get(formName + "_sshServerUser");
		var message = hm.util.validateName(el3.value, '<s:text name="monitor.hiveAp.ssh.user" />');
    	if (message != null) {
    		hm.util.reportFieldError(el3, message);
        	el3.focus();
        	return false;
    	}
    	var el4 = Get("chkToggleDisplay").checked ? Get("sshServerPsd"):Get("sshServerPsd_text");
		var message = hm.util.validateName(el4.value, '<s:text name="monitor.hiveAp.ssh.password" />');
    	if (message != null) {
    		hm.util.reportFieldError(el4, message);
        	el4.focus();
        	return false;
    	}
    	var el5 = Get(formName + "_sshTunnelPort");
    	if(el5.value.length == 0){
			hm.util.reportFieldError(el5, '<s:text name="error.requiredField"><s:param><s:text name="monitor.hiveAp.ssh.tunnelPort" /></s:param></s:text>');
			el5.focus();
			return false;
	 	}
		var message = hm.util.validateIntegerRange(el5.value, '<s:text name="monitor.hiveAp.ssh.tunnelPort" />',
		                                           <s:property value="1025" />,
		                                           <s:property value="65535" />);
		if (message != null) {
			hm.util.reportFieldError(el5, message);
			el5.focus();
			return false;
		}
		var el6 = Get(formName + "_sshTunnelTmt");
    	if(el6.value.length != 0){
			var message = hm.util.validateIntegerRange(el6.value, '<s:text name="monitor.hiveAp.ssh.tunnelTimeout" />',
			                                           <s:property value="0" />,
			                                           <s:property value="6000" />);
			if (message != null) {
				hm.util.reportFieldError(el6, message);
				el6.focus();
				return false;
			}
	 	}
	}
	return true;
}
var requestResult = function(o){
	if(null != top.waitingPanel){
		top.waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.msg){
		Get("content").innerHTML = "<pre>" + result.msg.replace(/\n/g,"<br />") + "</pre>";
	}
}
function onHidePage(){

}

//-->
</script>
<div><s:form action="hiveApToolkit" id="sshTunnel" name="sshTunnel">
	<s:hidden name="hiveApId" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Start"
						class="button" onClick="requestAction('startTunnel');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Stop"
						class="button" onClick="requestAction('stopTunnel');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Show"
						class="button" onClick="requestAction('showTunnel');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td colspan="10">
			<table class="embeddBox" border="0" cellspacing="0" cellpadding="0"
				width="100%">
				<tr>
					<td height="5px">
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					</td>
				</tr>
				<tr>
					<td class="labelT1"><s:text name="monitor.hiveAp.ssh.url" /><span
						class="required"> *</span></td>
					<td><s:textfield name="sshServerUrl" size="32"
						onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.ssh.serverPort" /></td>
					<td><s:textfield name="sshServerPort" maxlength="5" size="12"
						onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
						name="monitor.hiveAp.ssh.serverPort.note"></s:text></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text name="monitor.hiveAp.ssh.user" /><span
						class="required"> *</span></td>
					<td><s:textfield name="sshServerUser" maxlength="32" size="32"
						onkeypress="return hm.util.keyPressPermit(event,'name');" /> <s:text
						name="monitor.hiveAp.ssh.user.note"></s:text></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text name="monitor.hiveAp.ssh.password" /><span
						class="required"> *</span></td>
					<td><s:password id="sshServerPsd" name="sshServerPsd"
						maxlength="32" size="32"
						onkeypress="return hm.util.keyPressPermit(event,'password');" /><s:textfield
						id="sshServerPsd_text" name="sshServerPsd" maxlength="32"
						size="32"
						onkeypress="return hm.util.keyPressPermit(event,'password');"
						cssStyle="display:none" disabled="true" /> <s:text
						name="monitor.hiveAp.ssh.password.note"></s:text></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><s:checkbox id="chkToggleDisplay" name="ignore"
								value="true"
								onclick="hm.util.toggleObscurePassword(this.checked,['sshServerPsd'],['sshServerPsd_text']);" />
							</td>
							<td><s:text name="admin.user.obscurePassword" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.ssh.tunnelPort" /><span class="required">
					*</span></td>
					<td><s:textfield name="sshTunnelPort" maxlength="5" size="12"
						onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
						name="monitor.hiveAp.ssh.tunnelPort.note"></s:text></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.ssh.tunnelTimeout" /></td>
					<td><s:textfield name="sshTunnelTmt" maxlength="4" size="12"
						onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
						name="monitor.hiveAp.ssh.tunnelTimeout.note"></s:text></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td colspan="10">
			<fieldset><legend><s:text
				name="monitor.hiveAp.ssh.result.tag" /></legend>
				<div id="wrap"><div id="content"></div></div>
			</fieldset>
			</td>
		</tr>
	</table>
</s:form></div>
