<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%> 

<script type="text/javascript">

function validateVlanSettingsReservedVlans(operation){
	if(operation == 'create2' || operation == 'update2'){
		var vlan = document.getElementById(formName+'_dataSource_resrvedVlans');
		if(vlan){
			/* if(vlan.value.length == 0){
				hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.vlan.settings.reserved.vlan" /></s:param></s:text>');
				vlan.focus();
		        return false;
			}
			var vlanList =vlan.value.split(',');
			var vlanValues = new Array();
			var vlanRangerValues = new Array();
			for(var j=0;j<vlanList.length;j++){
				var pattern = /^(\d+-)?\d+$/;
				if(!pattern.test(vlanList[j])){
					hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.switchSettings.vlan.settings.reserved.vlan" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
				var vlans = vlanList[j].split('-');
				var numVlan0=Number(vlans[0]);
				var numVlan1=Number(vlans[1]);
				var pattern_value = /^[2-9]$|^[1-9]\d{1,2}$|^[1-2]\d{3}$|^3\d[0-6][0-7]$/; //2-3967
				for(var i=0;i<vlans.length;i++) {
					if(!pattern_value.test(vlans[i])){
						hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.switchSettings.vlan.settings.reserved.vlan" /></s:param></s:text>');
						vlan.focus();
				        return false;
					}
				}
			    if(vlans.length>1 && numVlan0 > numVlan1){
			    	hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.switchSettings.vlan.settings.reserved.vlan" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
			    if(vlans.length>1){
			    	for(var i=0;i<=numVlan1-numVlan0;i++){
						if(vlanValues.contains(numVlan0+i)){
							hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
							vlan.focus();
					        return false;
						} else {
							vlanValues.push(numVlan0+i);
						}
					}
			    } else {
			    	if(vlanValues.contains(numVlan0)){
						hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
						vlan.focus();
				        return false;
					} else {
						vlanValues.push(numVlan0);
					}
			    }
				
				if(vlanRangerValues.contains(vlanList[j])){
					hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
					vlan.focus();
			        return false;
				} else {
					vlanRangerValues.push(vlanList[j]);
				}
			} */
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
							<tr>
								<td class="labelT1" width="140px"><label><s:text
									name="config.switchSettings.vlan.settings.reserved.vlan" /></label></td>
								<td width="400px">
									<s:textfield name="dataSource.resrvedVlans" maxlength="4"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />
									<s:text name="config.switchSettings.vlan.settings.reserved.vlan.range"></s:text>
								</td>
							</tr>
							<tr>
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