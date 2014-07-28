<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" href="css/hm.css">
<link rel="stylesheet" href="css/te.css" />
<link rel="stylesheet" href="css/data_table.css" />
<style>
td.panelLabel {
	padding: 0px 0px 5px 8px;
	width: 20%;
	color: #003366;
}

td.panelText {
	width: 30%;
}
</style>

<script>
	var formName = "clientCaMgmt";

	function submitAction(operation) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}

	function insertPageContext() {
		document
				.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
		document.writeln('</td>');
	}
</script>


<div id="content">
	<s:form action="clientCaMgmt">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context"></tiles:insertDefinition>
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="import" value="Import"
								class="button" onclick="submitAction('importCa');" /></td>
								<td><input type="button" name="useDefault" value="Use Default CA"
								class="button" style="width:100px" onclick="submitAction('useDefault');" /></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes"></tiles:insertDefinition>
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td style="padding:15px 10px 10px 10px">
									<fieldset> 
									<legend><s:text name="admin.clientCa.caInfo" /></legend> 
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="5" />
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.caName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="caName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.commonName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="commonName" /></font>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.orgName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="orgName" /></font></td>
										</tr>
										<tr>

											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.ouName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="ouName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.localityName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="localityName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.stateName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="stateName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.countryName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="countryName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.serialName" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="serialName" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.validFrom" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="validFrom" /></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="20%"><label> <s:text
														name="admin.clientCa.validTo" />
											</label></td>
											<td width="30%"><font color="#003366"><s:property value="validTo" /></font></td>
										</tr>
									</table>
									</fieldset>
									<!-- <tr>
							<td>
								<table class="view" border="0" cellspacing="0" cellpadding="0"
									width="600px" style="padding:6px 4px 5px 4px">
									
								</table>
							</td>
						</tr> -->
								
							</td>
						</tr>
					</table>

				</td>
			</tr>

		</table>
	</s:form>
</div>