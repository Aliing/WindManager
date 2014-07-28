<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'sshKeysGen';

function submitAction(operation) {
	if (validate(operation)) 
	{
		showProcessing();
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function validate(operation) 
{
  	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="sshKeysGen">
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
								<input type="button" name="ignore" value="OK" class="button"
									onClick="submitAction('genKey');"
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
				<td>
					<table class="editBox" width="500px" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="180px">
								<label>
									<s:text name="config.sshkeyGen.algorithm" />
								</label>
							</td>
							<td>
								<s:select id="genAlgorithm" name="genAlgorithm"
									value="%{genAlgorithm}" list="enumAlgorithm" listKey="key"
									listValue="value" />
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
