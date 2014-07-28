<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'ipAddress';
    
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_profileName").disabled == false) {
		document.getElementById(formName + "_dataSource_profileName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}
	
	if(operation == 'create'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_profileName");
		var message = hm.util.validateName(name.value, '<s:text name="config.radiusOnHiveAp.radiusName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}	 
	}
	   
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update') {
    }
     
	return true;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ipAddress" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="ipAddress">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>" 
						class="button" onClick="submitAction('update');" 
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="750">
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.radiusOnHiveAp.radiusName" />
								<font color="red"><s:text name="*"/></font>
								</label></td>
							<td><s:textfield size="24" name="dataSource.profileName"
								maxlength="%{nameLength}" disabled="%{disabledName}"
								onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
								name="config.ssid.ssidName_range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text
								name="config.radiusOnHiveAp.description" /></label></td>
							<td><s:textfield size="48"
								name="dataSource.description" maxlength="%{descriptionLength}" />&nbsp;<s:text
								name="config.ssid.description_range" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
