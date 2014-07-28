<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
var formName = "remoteSniffer";

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	
	if(hostName){
		top.updateRemoteSnifferTitle(hostName);
	}
	
	//save this reference on the parent
	top.remoteSnifferIFrameWindow = window;
}

function onHidePage() {
	hm.util.hide("noteDiv");
}

function requestAction(action){
	if(validate(action)){
		hm.util.hide("noteDiv");
		
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
		
		var url = "<s:url action="hiveApToolkit" includeParams="none"></s:url>" + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = action;
		ajaxRequest(formName, url, requestActionProcess);
	}
}

var requestActionProcess = function(o){
	if(null != top.waitingPanel){
		top.waitingPanel.hide();
	}
		
	eval("var result = " + o.responseText);
	
	if(result.r) {
		showInfoMessage(result.m);	
	} else {
		showErrorMessage(result.m);
	}
}


function cancel() {
	top.clearRemoteSnifferPanel();
}

function validate(action) {
	if(!validatePassword()) {
		return false;
	}
	
	if(!validatePort()) {
		return false;
	}
	
	return true;
}

function validatePassword() {

	if(!document.getElementById("enableRemoteSniffer").checked) {
		return true;
	}
	
	if(document.getElementById("userName").value.trim().length == 0) {
		return true;
	}
	
	var password = document.getElementById("password").value.trim();
	
	if(password.length == 0) {
		showErrorMessage("Password is required when User Name is configured.");
		document.getElementById("password").focus();
		return false;
	}
	
	return true;
}

function validatePort() {

	if(!document.getElementById("enableRemoteSniffer").checked) {
		return true;
	}
	
	var port = document.getElementById("port").value.trim();
	
	if(port.length == 0) {
		return true;
	}
	
	if(port < 1024 || port > 65535) {
		showErrorMessage("Port should be between 1024 and 65535.");
		document.getElementById("port").select();
		document.getElementById("port").focus();
		return false;
	}

	return true;
}

function showErrorMessage(message)
{
	Get("noteId").innerHTML = message;
	Get("noteId").className="noteError";
	hm.util.show("noteDiv");
}

function showInfoMessage(message)
{
	Get("noteId").innerHTML = message;
	Get("noteId").className="noteInfo";
	hm.util.show("noteDiv");
}

function switchRemoteSniffer(checked) {
	document.getElementById("userName").disabled = !checked;
	document.getElementById("password").disabled = !checked;
	document.getElementById("port").disabled = !checked;
	document.getElementById("host").disabled = !checked;
	document.getElementById("promiscuous").disabled = !checked;
}
</script>

<div><s:form action="hiveApToolkit" 
			id="remoteSniffer" name="remoteSniffer" 
			enctype="multipart/form-data" method="POST">
<s:hidden name="operation" />
<s:hidden name="hiveApId" />
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="buttons">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<input id="save" type="button" name="ignore" value="Save"
						class="button" onClick="requestAction('setRemoteSniffer');"
						<s:property value="writeDisabled" />>
				</td>
				<td>
					<input id="return" type="button" name="ignore" value="Return"
						class="button" onClick="cancel();"
						<s:property value="writeDisabled" />>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="5px" colspan="2"></td>
	</tr>
	<tr>
		<td colspan="10">
			<div id="noteDiv" style="display:none">
			<table border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td id="noteId"></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td>
		<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="490px">
			<tr>
				<td height="5px" colspan="2"></td>
			</tr>
			<tr>
				<td colspan="2">
				<table>
					<tr>
						<td align="right">
							<s:checkbox name="enableRemoteSniffer" id="enableRemoteSniffer" value="true"
								onchange="switchRemoteSniffer(this.checked);"/>
						</td>
						<td align="left">
							<s:text name="topology.menu.remoteSniffer.enable" />
						</td>		
					</tr>
				</table>
			</tr>
			<tr>
				<td height="5px" colspan="2"></td>
			</tr>
			<tr>
				<td class="labelT1" width="120px" style="padding-left: 16px;">
					<s:text name="topology.menu.remoteSniffer.userName" />
				</td>
				<td >
					<s:textfield name="userName" id="userName"
						maxlength="32" cssStyle="width:120px;" 
						onkeypress="return hm.util.keyPressPermit(event,'username');"/>
					<s:text name="topology.menu.remoteSniffer.userName.range" />
				</td>
			</tr>
			<tr>
				<td class="labelT1" width="120px" style="padding-left: 16px;">
					<s:text name="topology.menu.remoteSniffer.password" />
				</td>
				<td >
					<s:textfield name="password" id="password"
						maxlength="32" cssStyle="width:120px;" 
						onkeypress="return hm.util.keyPressPermit(event,'password');"/>
					<s:text name="topology.menu.remoteSniffer.userName.range" />
				</td>
			</tr>
			<tr>
				<td class="labelT1" width="120px" style="padding-left: 16px;">
					<s:text name="topology.menu.remoteSniffer.port" />
				</td>
				<td >
					<s:textfield name="port" id="port" value="2002"
						maxlength="5" cssStyle="width:120px;" 
						onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
					<s:text name="topology.menu.remoteSniffer.port.range" />
				</td>
			</tr>
			<tr>
				<td class="labelT1" width="140px" style="padding-left: 16px;">
					<s:text name="topology.menu.remoteSniffer.host" />
				</td>
				<td >
					<s:textfield name="host" id="host" 
						maxlength="32" cssStyle="width:120px;" 
						onkeypress="return hm.util.keyPressPermit(event,'username');"/>
					<s:text name="topology.menu.remoteSniffer.userName.range" />
				</td>
			</tr>
			<tr>
				<td colspan="2" >
				<table>
					<tr>
						<td style="padding-left: 12px;">
							<s:checkbox name="promiscuous" id="promiscuous"/>
						</td>
						<td align="left">
							<s:text name="topology.menu.remoteSniffer.promiscuous" />
						</td>		
					</tr>
				</table>
				
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="5px"></td>
	</tr>
	
</table>
</s:form></div>