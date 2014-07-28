<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'tvResourceMap';  
function onLoadPage() {
	if (Get(formName + "_dataSource_resource").disabled == false) {
		Get(formName + "_dataSource_resource").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
    	var resource = document.getElementById(formName + "_dataSource_resource");
		var message = hm.util.validateNameWithBlanks(resource.value, '<s:text name="config.tv.resource" />');
    	if (message != null) {
    		hm.util.reportFieldError(resource, message);
        	resource.focus();
        	return false;
    	}
    	
		var alias = document.getElementById(formName + "_dataSource_alias");
		if (alias.value.length ==0) {
	        hm.util.reportFieldError(alias, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.alias" /></s:param></s:text>');
	        alias.focus();
	        return false;
		} else {
			if (!hm.util.validateIpAddress(alias.value)) {
				hm.util.reportFieldError(alias, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.alias" /></s:param></s:text>');
	        	alias.focus();
			    return false;
			}
		}
    	
    	var port = document.getElementById(formName + "_dataSource_port");
    	if (port.value.length == 0) {
			 hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.resource.port" /></s:param></s:text>');
			 port.focus();
			 return false;
		 }
		 var message = hm.util.validateIntegerRange(port.value, '<s:text name="config.tv.resource.port" />', 1, 65535);
		 if (message != null) {
			 hm.util.reportFieldError(port,message);
			 port.focus();
			 return false;
		 }

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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="tvResourceMap" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="tvResourceMap">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_TV_RESOURCEMAP%>');">
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
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="720px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						
						<tr>
							<td class="labelT1"><s:text
								name="config.tv.resource" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.resource" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
								maxlength="32"  disabled="%{disabledName}" />&nbsp;<s:text
								name="config.tv.className.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.alias" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.alias" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'ip');"
								maxlength="15"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.resource.port" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.port" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="5" />&nbsp;<s:text
								name="config.tv.resource.port.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.hp.description" /></td>	
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="256" />&nbsp;<s:text
								name="config.tv.description.range" /></td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
