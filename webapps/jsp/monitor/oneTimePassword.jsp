<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>

<script>
var formName = 'oneTimePassword';

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>'
				|| operation == 'update' + '<s:property value="lstForward"/>'
				|| operation == 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}

function validate(operation) {
    if(operation == 'assign'+'<s:property value="lstForward"/>' || operation == 'assign') {
		var emailAddr = document.getElementById(formName + "_dataSource_emailAddress");
		if(emailAddr.value.length > 0 && !hm.util.validateEmail(emailAddr.value))
		{
			hm.util.reportFieldError(emailAddr, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.otp.list.email" /></s:param></s:text>');
			emailAddr.focus();
			return false;
		}
	}
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="oneTimePassword" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('Assign </td>');
}
</script>
<div id="content"><s:form action="oneTimePassword" name="oneTimePassword" id="oneTimePassword">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Assign"
						class="button"
						onClick="submitAction('assign<s:property value="lstForward"/>');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="700">
					<tr>
						<td style="padding: 6px 4px 5px 4px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="100"><label><s:text
									name="monitor.otp.list.password" /></label></td>
								<td><s:property value="dataSource.oneTimePassword"/></td>
							</tr>
							<tr>
								<td class="labelT1" width="120"><label><s:text
									name="monitor.otp.list.username" /></label></td>
								<td colspan="2"><s:textfield
									name="dataSource.userName" size="24" maxlength="32" disabled="%{disableAssign}"/>&nbsp;<s:text
									name="monitor.otp.list.username.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="120"><label><s:text
									name="monitor.otp.list.email" /></label></td>
								<td colspan="2"><s:textfield
									name="dataSource.emailAddress" size="48" maxlength="128" disabled="%{disableAssign}"/>&nbsp;<s:text
									name="monitor.otp.list.email.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="120"><label><s:text
									name="monitor.otp.list.description" /></label></td>
								<td colspan="2"><s:textfield
									name="dataSource.description" size="48" maxlength="128"/>&nbsp;<s:text
									name="monitor.otp.list.description.range" /></td>
							</tr>
						</table>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>
