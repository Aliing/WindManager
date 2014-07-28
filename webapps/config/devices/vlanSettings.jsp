<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%> 

<script type="text/javascript">

function validateVlanSettingsReservedVlans(operation){
	if(operation == 'create2' || operation == 'update2'){
		var vlan = document.getElementById(formName+'_dataSource_resrvedVlans');
		if (vlan.value.length == 0) {
			hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.vlan.settings.reserved.vlan" /></s:param></s:text>');
			showVLANSettingsSetting();
			vlan.focus();
			return false;
		}
		var message = hm.util.validateIntegerRange(vlan.value, '<s:text name="config.switchSettings.vlan.settings.reserved.vlan" />',
		                                           <s:property value="2" />,
		                                           <s:property value="3967" />);
		if (message != null) {
			hm.util.reportFieldError(vlan, message);
			showVLANSettingsSetting();
			vlan.focus();
			return false;
		}
	}
    return true;
}

function showVLANSettingsSetting(){
	showHideContent("vlanSettings", "")
}

</script>

<div id="vlanSettingsdiv">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<!--VLAN Settings  -->
		<tr>
			<td><div><table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.switchSettings.vlan.settings" />','vlanSettings');</script></td>
				</tr>
				<tr>
					<td style="padding-left:7px"><div id="vlanSettings" style="display: <s:property value="%{dataSource.resrvedVlansSettingStyle}"/>">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr id="reservedVlanTr">
								<td class="labelT1" width="220px"><label><s:text
									name="config.switchSettings.vlan.settings.reserved.vlan" /></label></td>
								<td width="400px">
									<s:textfield name="dataSource.resrvedVlans" maxlength="4"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />
									<s:text name="config.switchSettings.vlan.settings.reserved.vlan.range"></s:text>
								</td>
							</tr>
							<tr id="reservedVlanExampleTr">
								<td colspan="2" class="noteInfo" style="padding-left: 10px;">
									<s:text name="config.switchSettings.vlan.settings.reserved.vlan.example"></s:text>
								</td>
							</tr>
						</table>
					</div></td>
				</tr>
			</table></div></td>
		</tr>
	</table>
</div>