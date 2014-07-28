<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'upgradeLog';

function onLoadPage() {
	document.getElementById("annotation").focus();
	document.getElementById("annotation").select();
}

function submitAction(operation) {

	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == 'update') {
		if(!validateAnnotation()) {
			return false;
		}
	}
	
	return true;
}

function validateAnnotation() {
	var element = document.getElementById("annotation");
	
	if(element.value.trim().length == 0) {
	 	hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="admin.upgradeLog.annotation" /></s:param></s:text>');
        element.focus();
        return false;
	}
	
	if(element.value.length > 512) {
		hm.util.reportFieldError(element, '<s:text name="error.admin.upgradeLog.annotation.tooLong"><s:param>512</s:param></s:text>');
	    element.focus();
	    return false;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="upgradeLog" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt;  Edit </td>');
	</s:else>
}

</script>

<div id="content">
<s:form action="upgradeLog">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update');"
							<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="submitAction('cancel');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">

			<table class="editBox" border="0" cellspacing="0" cellpadding="0"
				width="620">
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="labelT1" width="150"><s:text	name="admin.upgradeLog.formerContent" /></td>
					<td style="padding:0px 5px 0px 5px;"><font color="blue"><s:property value="%{dataSource.formerContent}" escape="false"/></font></td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="labelT1" width="150"><s:text	name="admin.upgradeLog.postContent" /></td>
					<td style="padding:0px 5px 0px 5px;"><font color="red"><s:property value="%{dataSource.postContent}" /></font></td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="labelT1" width="150"><s:text	name="admin.upgradeLog.recommendAction" /></td>
					<td style="padding:0px 5px 0px 5px;"><s:property value="%{dataSource.recommendAction}" /></td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="labelT1" width="150"><s:text	name="admin.upgradeLog.annotation" /></td>
					<td>
						<s:textarea id="annotation" name="dataSource.annotation" 
							 cols="54" rows="5" /> 
						<s:text name="admin.upgradeLog.annotation.range"></s:text>
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="labelT1" width="150"><s:text	name="admin.upgradeLog.upgradeTime" /></td>
					<td style="padding:0px 5px 0px 5px;"><s:property value="%{dataSource.logTimeString}" /></td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form>
</div>