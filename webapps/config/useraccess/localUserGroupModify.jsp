<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>
var formName = 'localUserGroup';

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'updateMulti') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_LOCAL_USER_GROUP%>' == operation
		|| operation == 'cancel') {
		return true;
	}
	if (!checkNumbers()) {
		return false;
	}

	return true;
}

function checkNumbers() {
	var userId = document.getElementById("user");
	var vlan = document.getElementById("vlan");
	var time = document.getElementById("time");
	
	if (userId.value != "[-No Change-]") {
		if(userId.value.length > 0)
		{
			if(userId.value.length > 1)
			{
				if(userId.value.substring(0,1) == '0')
				{
					hm.util.reportFieldError(userId, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.profileId" /></s:param></s:text>');
					userId.focus();
					return false;
				}
			}
			var message = hm.util.validateIntegerRange(userId.value, '<s:text name="config.localUserGroup.profileId" />',0,4095);
	      	if (message != null) {
	            hm.util.reportFieldError(userId, message);
	           	userId.focus();
	            return false;
	      	}
		}
		
	}
	if (vlan.value != "[-No Change-]") {
		if(vlan.value.length > 0)
		{
			if(vlan.value.length > 1)
			{
				if(vlan.value.substring(0,1) == '0')
				{
					hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.vlanId" /></s:param></s:text>');
					vlan.focus();
					return false;
				}
			}
			var message = hm.util.validateIntegerRange(vlan.value, '<s:text name="config.localUserGroup.vlanId" />',1,4094);
	      	if (message != null) {
	            hm.util.reportFieldError(vlan, message);
	           	vlan.focus();
	            return false;
	      	}
		}
	}
	if (time.value != "[-No Change-]") {
		if(time.value.length > 0)
		{
			if(time.value.length > 1)
			{
				if(time.value.substring(0,1) == '0')
				{
					hm.util.reportFieldError(time, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
					time.focus();
					return false;
				}
			}
			if (time.value.length == 1) {
				if(time.value!= '0')
				{
					hm.util.reportFieldError(time, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
					time.focus();
					return false;
				}
			
			} else {
				var message = hm.util.validateIntegerRange(time.value, '<s:text name="config.localUserGroup.reauthTime" />',600,86400);
		      	if (message != null) {
		            hm.util.reportFieldError(time, message);
		           	time.focus();
		            return false;
		      	}
	      	}
		} else {
		    hm.util.reportFieldError(time, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
		    time.focus();
		    return false;
		}
	}
	return true;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>0}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="localUserGroup" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="localUserGroup">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="update" value="<s:text name="button.update"/>"
						class="button" onClick="submitAction('updateMulti');"
						<s:property value="writeDisabled" />></td>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_LOCAL_USER_GROUP%>');">
						</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700px">
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td> 
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.profileId" /></td>
								<td><s:textfield size="24" name="strUserProfileId" id="user" 
									value="%{strVlanId}" maxlength="13" />&nbsp;<s:text
									name="config.localUserGroup.profileIdRange" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.localUserGroup.vlanId" /></td>
								<td><s:textfield size="24" name="strVlanId" id="vlan"
									value="%{strVlanId}" maxlength="13" />&nbsp;<s:text
									name="config.localUserGroup.vlanIdRange" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.localUserGroup.reauthTime" /></td>
								<td><s:textfield size="24" name="strReauthTime" id="time"
									value="%{strVlanId}" maxlength="13" />&nbsp;<s:text
									name="config.localUserGroup.reauthTimeRange" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.localUser.description" /></td>
								<td><s:textfield size="48" name="strDescription"
								    value="%{strDescription}" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding: 4px 4px 4px 4px;"> 
						<fieldset><legend><s:text name="config.localUserGroup.credential" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td><s:radio label="Gender" id="credentialTypeRadio"
											name="intCredentialType"
											list="#{0:'[-No Change-]'}"
											value="%{intCredentialType}" /></td>
									<td><s:radio label="Gender" id="credentialTypeRadio"
											name="intCredentialType"
											list="#{1:'Save credentials to flash (persistent after reboot)'}"
											value="%{intCredentialType}" /></td>
									<td><s:radio label="Gender" id="credentialTypeRadio"
											name="intCredentialType"
											list="#{2:'Save credentials to DRAM only (not persistent)'}"
											value="%{intCredentialType}" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>

				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
