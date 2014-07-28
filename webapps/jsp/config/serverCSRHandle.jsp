<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'serverCSR';

function submitAction(operation) {
	if (validate(operation)) {
			
		if (operation == 'ok' && document.getElementById("validity").disabled == false )
		{
			//export operation will not show processing message
			showProcessing();
		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) 
{
	if (operation != 'ok')
	{
		return true;
	}
	
	if (document.getElementById("validity").disabled == false)
	{
		var validity = document.getElementById("validity");
		if ( validity.value.length == 0) 
		{
            hm.util.reportFieldError(validity, '<s:text name="error.requiredField"><s:param><s:text name="admin.serverCSR.validity" /></s:param></s:text>');
            validity.focus();
            return false;
   	 	}
		
		if ( validity.value > 7300) 
		{
            hm.util.reportFieldError(validity, '<s:text name="error.notLargerThan"><s:param><s:text name="admin.serverCSR.validity" /></s:param><s:param>20 years(7300 days)</s:param></s:text>');
            validity.focus();
            return false;
   	 	}
	}
    
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function selectExportCSR(checked)
{
	if (checked)
	{
		document.getElementById("validity").disabled = true;
		document.getElementById("chkCombine").disabled = true;
	}
}

function selectSignCSR(checked,value)
{
	if (checked)
	{
		if(value == 'signCSR'){
			document.getElementById("validity").disabled = false;
			document.getElementById("chkCombine").disabled = false;
		}else if(value == 'clientCSR'){
			document.getElementById("validity").disabled = true;
			document.getElementById("chkCombine").disabled = true;
		}
		
	}
}

</script>

<div id="content">
	<s:form action="serverCSR">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="ok" value="Ok" class="button"
									onClick="submitAction('ok');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Cancel" class="button"
									onClick="submitAction('<%=Navigation.L2_FEATURE_ADMINISTRATORS%>');">
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="700">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding:0 0 0 5px" colspan=2>
								<s:radio label="Gender" id="exportCSR" name="handleMode"
									list="#{'exportCSR':'Export'}"
									onclick="selectExportCSR(this.checked);" value="%{handleMode}" />
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding:0 0 0 5px" colspan=2>
								<s:radio label="Gender" id="signCSR" name="handleMode"
									list="%{serverCSRRadio}"
									listKey="key" listValue="value"
									onclick="selectSignCSR(this.checked,this.value);" value="%{handleMode}" />
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="70" style="padding:5px 0 0 30px">
								<label>
									<s:text name="admin.serverCSR.validity" />
								</label>
							</td>
							<td>
								<s:textfield id="validity" name="validity"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									disabled="%{disabledSign}" maxlength="4" />
								<s:text name="admin.serverCSR.days" />
							</td>
							<td>
								<s:checkbox name="combineFile" id="chkCombine" disabled="%{disabledSign}" />
								<s:text name="admin.serverCSR.combine" />
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
