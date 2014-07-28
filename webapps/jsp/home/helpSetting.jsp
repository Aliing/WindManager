<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'helpSetting';

function submitAction(operation) {

	if (operation == 'update')
	{
		showProcessing();
	}

	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}



function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="helpSetting">
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
								<input type="button" name="update" value="Update" class="button"
									onClick="submitAction('update');"
									<s:property value="writeDisabled" />>
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
				<td style="padding-top: 5px;">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="600">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding-left: 5px;" colspan="2">
								<s:text name="config.helpSetting.helpdir"/>
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td style="padding-left: 35px;">
								<s:radio label="Gender" name="selectSetting" 
									list="#{'1':''}"
									disabled="%{disabledValue}"/>
							</td>
							<td>
								<s:textfield id="helpDir1" name="helpDir1" cssStyle="width:500px" readonly="true"/>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding-left: 35px;">
								<s:radio label="Gender" name="selectSetting"
									list="#{'2':''}" 
									disabled="%{disabledValue}"/>
							</td>
							<td>
								<s:textfield id="helpDir2" name="helpDir2" cssStyle="width:500px"/>
							</td>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
