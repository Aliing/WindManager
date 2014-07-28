<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'compliancePolicy';

function submitAction(operation) {
	showProcessing();
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

function resetGuiValue(){
	document.getElementById(formName + '_dataSource_clientOpen1').checked =true;
	document.getElementById(formName + '_dataSource_clientOpenAuth2').checked =true;
	document.getElementById(formName + '_dataSource_clientWep1').checked =true;
	document.getElementById(formName + '_dataSource_clientPsk2').checked =true;
	document.getElementById(formName + '_dataSource_clientPrivatePsk3').checked =true;
	document.getElementById(formName + '_dataSource_client8021x3').checked =true;

	document.getElementById(formName + '_dataSource_hiveApSsh3').checked =true;
	document.getElementById(formName + '_dataSource_hiveApTelnet1').checked =true;
	document.getElementById(formName + '_dataSource_hiveApPing2').checked =true;
	document.getElementById(formName + '_dataSource_hiveApSnmp1').checked =true;
	
	document.getElementById(formName + '_dataSource_passwordSSID').checked =true;
	document.getElementById(formName + '_dataSource_passwordHive').checked =true;
	document.getElementById(formName + '_dataSource_passwordCapwap').checked =true;
	document.getElementById(formName + '_dataSource_passwordHiveap').checked =true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="compliancePolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a>');
}
</script>
<style>
<!--
.td_back_color {
/*	background-color: #DBDBDB; */
	background-color: #c1e4ff;
	padding: 4px 4px 4px 4px;
}
.td_white_color {
/*	background-color: #DBDBDB; */
	background-color: white;
	padding: 4px 4px 4px 4px;
}
-->
</style>
<div id="content"><s:form action="compliancePolicy">

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('update');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Reset"
						class="button" onClick="resetGuiValue();" ></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			
			<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="650px">
				<tr>
					<td style="padding: 10px 10px 10px 10px;">
					<table border="1" cellspacing="0"  cellpadding="0" width=100%>
						<tr>
							<td class="labelT1" width="100px"></td>
							<td>
								<table border="0" cellspacing="0"  cellpadding="0" width=100%>
									<tr>
										<td class="labelT1" width="200px"></td>
										<td class="labelT1" width="70px"><B><s:text name="compliancePolicy.poor"/></B></td>
										<td class="labelT1" width="70px"><B><s:text name="compliancePolicy.good"/></B></td>
										<td class="labelT1" width="70px"><B><s:text name="compliancePolicy.excellent"/></B></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td width="100px" class="labelT1"><s:text name="compliancePolicy.clientAccess"/></td>
							<td>
								<table border="0" cellspacing="0"  cellpadding="0" width=100%>
									<tr>
										<td width="200px" class="td_back_color">Open</td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientOpen" list="#{'1':''}" value="%{dataSource.clientOpen}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientOpen" list="#{'2':''}" value="%{dataSource.clientOpen}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientOpen" list="#{'3':''}" value="%{dataSource.clientOpen}"/></td>
									</tr>
									<tr>	
										<td width="200px" class="td_white_color">Open with Authentication</td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientOpenAuth" list="#{'1':''}" value="%{dataSource.clientOpenAuth}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientOpenAuth" list="#{'2':''}" value="%{dataSource.clientOpenAuth}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientOpenAuth" list="#{'3':''}" value="%{dataSource.clientOpenAuth}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_back_color">WEP</td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientWep" list="#{'1':''}" value="%{dataSource.clientWep}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientWep" list="#{'2':''}" value="%{dataSource.clientWep}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientWep" list="#{'3':''}" value="%{dataSource.clientWep}"/></td>
									</tr>
									<tr>	
										<td width="200px" class="td_white_color">WPA or WPA2 Personal (PSK)</td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientPsk" list="#{'1':''}" value="%{dataSource.clientPsk}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientPsk" list="#{'2':''}" value="%{dataSource.clientPsk}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.clientPsk" list="#{'3':''}" value="%{dataSource.clientPsk}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_back_color">WPA or WPA2 with Private PSK</td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientPrivatePsk" list="#{'1':''}" value="%{dataSource.clientPrivatePsk}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientPrivatePsk" list="#{'2':''}" value="%{dataSource.clientPrivatePsk}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.clientPrivatePsk" list="#{'3':''}" value="%{dataSource.clientPrivatePsk}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_white_color">WPA or WPA2 Enterprise (802.1X)</td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.client8021x" list="#{'1':''}" value="%{dataSource.client8021x}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.client8021x" list="#{'2':''}" value="%{dataSource.client8021x}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.client8021x" list="#{'3':''}" value="%{dataSource.client8021x}"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td  width="100px" class="labelT1"><s:text name="compliancePolicy.hiveapAccess"/></td>
							<td>
								<table border="0" cellspacing="0"  cellpadding="0" width=100%>
									<tr>
										<td width="200px" class="td_back_color">SSH</td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApSsh" list="#{'1':''}" value="%{dataSource.hiveApSsh}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApSsh" list="#{'2':''}" value="%{dataSource.hiveApSsh}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApSsh" list="#{'3':''}" value="%{dataSource.hiveApSsh}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_white_color">Telnet</td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApTelnet" list="#{'1':''}" value="%{dataSource.hiveApTelnet}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApTelnet" list="#{'2':''}" value="%{dataSource.hiveApTelnet}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApTelnet" list="#{'3':''}" value="%{dataSource.hiveApTelnet}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_back_color">Ping</td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApPing" list="#{'1':''}" value="%{dataSource.hiveApPing}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApPing" list="#{'2':''}" value="%{dataSource.hiveApPing}"/></td>
										<td width="70px" class="td_back_color"><s:radio label="Gender" name="dataSource.hiveApPing" list="#{'3':''}" value="%{dataSource.hiveApPing}"/></td>
									</tr>
									<tr>
										<td width="200px" class="td_white_color">SNMP</td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApSnmp" list="#{'1':''}" value="%{dataSource.hiveApSnmp}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApSnmp" list="#{'2':''}" value="%{dataSource.hiveApSnmp}"/></td>
										<td width="70px" class="td_white_color"><s:radio label="Gender" name="dataSource.hiveApSnmp" list="#{'3':''}" value="%{dataSource.hiveApSnmp}"/></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding: 4px 10px 4px 10px;"> 
						<fieldset><legend><s:text name="compliancePolicy.passwordAccess"/></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td height="4px"/>
							</tr>
							<tr>
								<td> <s:checkbox name="dataSource.passwordSSID"></s:checkbox>
								<s:text name="compliancePolicy.passwordSSID"/></td>
								<td> <s:checkbox name="dataSource.passwordHiveap"></s:checkbox>
								<s:text name="compliancePolicy.passwordHiveap"/></td>
							</tr>
							<tr>
								<td> <s:checkbox name="dataSource.passwordHive"></s:checkbox>
								<s:text name="compliancePolicy.passwordHive"/></td>
								<td> <s:checkbox name="dataSource.passwordCapwap"></s:checkbox>
								<s:text name="compliancePolicy.passwordCapwap"/></td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>

				<tr>
					<td height="8"></td>
				</tr>
			</table>
			</td>
		</tr>

	</table>
</s:form></div>
