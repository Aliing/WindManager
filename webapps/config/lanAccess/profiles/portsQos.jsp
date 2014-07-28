<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.port.PortAccessProfile"%>
<script type="text/javascript">
function enabledVoipCheckBox(checked, ifId) {
	if (checked){
		Get(formName + "_dataSource_" + ifId).readOnly=false;
	} else {
		Get(formName + "_dataSource_" + ifId).readOnly=true;
		Get(formName + "_dataSource_" + ifId).value=100
	}
}

function clickQosClassificationMode(value){
	if(value == 0){ //trust
		$('input[name="dataSource.qosClassificationTrustMode"]').attr("disabled",false);
		var enableTrustedProiority = $('#'+formName+"_dataSource_enableTrustedProiority");
		enableTrustedProiority.attr("disabled",false);
		if($('#'+formName+"_dataSource_enableTrustedProiority").attr("checked")){
			$('#trustedPriority').attr("disabled",false);
		} else {
			$('#trustedPriority').attr("disabled",true);
		}
		$('#untrustedPriority').attr("disabled",true);
	} else {
		$('input[name="dataSource.qosClassificationTrustMode"]').attr("disabled",true);
		$('input[name="dataSource.enableTrustedProiority"]').attr("disabled",true);
		$('#trustedPriority').attr("disabled",true);
		$('#untrustedPriority').attr("disabled",false);
	}
}

function chickQosMark(checked){
	$('input[name="dataSource.qosMarkMode"]').attr("disabled",!checked);
}

function chickEnableTrustedProiority(checked){
	$('#trustedPriority').attr("disabled",!checked);
}

function validateQos(){
	if(selectedPortType == 6){
		if (Get(formName + "_dataSource_enableEthLimitDownloadBandwidth").checked) {
			var ethLimitDownloadRateElement = document.getElementById(formName + "_dataSource_ethLimitDownloadRate");
			var ethLimitDownloadRateMessage = hm.util.validateIntegerRange(ethLimitDownloadRateElement.value, '<s:text name="config.configTemplate.voip.maxdownloadrate" />', 10, 15000);
			if (ethLimitDownloadRateMessage != null) {
				hm.util.reportFieldError(ethLimitDownloadRateElement, ethLimitDownloadRateMessage);
				ethLimitDownloadRateElement.focus();
				return false;
			}
		}
		if (Get(formName + "_dataSource_enableEthLimitUploadBandwidth").checked) {
			var ethLimitUploadRateElement = document.getElementById(formName + "_dataSource_ethLimitUploadRate");
			var ethLimitUploadRateMessage = hm.util.validateIntegerRange(ethLimitUploadRateElement.value, '<s:text name="config.configTemplate.voip.maxuploadrate" />', 10, 15000);
			if (ethLimitUploadRateMessage != null) {
				hm.util.reportFieldError(ethLimitUploadRateElement, ethLimitUploadRateMessage);
				ethLimitUploadRateElement.focus();
				return false;
			}		
		}
		
		if (Get(formName + "_dataSource_enableUSBLimitDownloadBandwidth").checked) {
			var usbLimitDownloadRateElement = document.getElementById(formName + "_dataSource_usbLimitDownloadRate");
			var usbLimitDownloadRateMessage = hm.util.validateIntegerRange(usbLimitDownloadRateElement.value, '<s:text name="config.configTemplate.voip.maxdownloadrate" />', 10, 15000);
			if (usbLimitDownloadRateMessage != null) {
				hm.util.reportFieldError(usbLimitDownloadRateElement, usbLimitDownloadRateMessage);
				usbLimitDownloadRateElement.focus();
				return false;
			}
		}
		if (Get(formName + "_dataSource_enableUSBLimitUploadBandwidth").checked) {
			var usbLimitUploadRateElement = document.getElementById(formName + "_dataSource_usbLimitUploadRate");
			var usbLimitUploadRateMessage = hm.util.validateIntegerRange(usbLimitUploadRateElement.value, '<s:text name="config.configTemplate.voip.maxuploadrate" />', 10, 15000);
			if (usbLimitUploadRateMessage != null) {
				hm.util.reportFieldError(usbLimitUploadRateElement, usbLimitUploadRateMessage);
				usbLimitUploadRateElement.focus();
				return false;
			}
		}
	}
	
	return true;
}
</script>
<fieldset>
	<legend><s:text name="config.port.qos.setting.label" /></legend >
	<div id="qosSettingsDiv" style="display: <s:property value="%{!dataSource.wanPortStyle}"/>">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td class="labelT1"><s:text name="config.port.qos.setting.classification.label" /></td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td valign="top" class="labelT1" colspan="2">
								<s:radio label="Gender" 
									name="dataSource.qosClassificationMode" list="%{qosTrusted}"
									id="qosClassificationTrusted"
									listKey="key" listValue="value" 
									onclick="clickQosClassificationMode(this.value)"/>
							</td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2" style="padding: 9px 0px 5px 32px"><s:text name="config.port.qos.setting.classification.trusted.label" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 32px; width: 150px;">
								<s:radio label="Gender" 
									name="dataSource.qosClassificationTrustMode" list="%{qosDscp}"
									id="qosClassificationDscp"
									listKey="key" listValue="value" 
									disabled="%{!enableQosTrusted}"/>
							</td>
							<td class="labelT1">
								<s:radio label="Gender" 
									name="dataSource.qosClassificationTrustMode" list="%{qos8021p}"
									id="qosClassification8021p"
									listKey="key" listValue="value" 
									disabled="%{!enableQosTrusted}"/>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding-left: 16px;">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1">
											<s:checkbox 
												name="dataSource.enableTrustedProiority" 
												onclick="chickEnableTrustedProiority(this.checked);"
												disabled="%{!enableQosTrusted}"/>
										</td>
										<td style="padding: 9px 0px 5px 0px"><s:text name="config.port.qos.setting.classification.trusted.priority" /></td>
										<td class="labelT1">
											<s:select name="dataSource.trustedPriority" id="trustedPriority"
												list="%{enumUntrustedPriority}" listKey="key" listValue="value"
												cssStyle="width: 130px;" disabled="%{!enableQosUntrustedPriority}"></s:select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td class="labelT1" colspan="2">
							<s:radio label="Gender" 
								name="dataSource.qosClassificationMode" list="%{qosUntrusted}"
								id="qosClassificationUntrusted"
								listKey="key" listValue="value" 
								onclick="clickQosClassificationMode(this.value)"/>
						</td>
						
					</tr>
					<tr>
						<td class="labelT1" style="padding-left: 32px;"><s:text name="config.port.qos.setting.classification.untrusted.priority" /></td>
						<td class="labelT1" style="padding-top: 6px;">
							<s:select name="dataSource.untrustedPriority" id="untrustedPriority"
								list="%{enumUntrustedPriority}" listKey="key" listValue="value"
								cssStyle="width: 130px;" disabled="%{enableQosTrusted}"></s:select>
						</td>
					</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="config.port.qos.setting.marking.label" /></td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td valign="top" class="labelT1">
							<s:checkbox 
								name="dataSource.enableQosMark" 
								onclick="chickQosMark(this.checked);"/>
						</td>
						<td valign="top" style="padding: 10px 0px 5px 0px"><s:text name="config.port.qos.setting.marking.title" /></td>
					</tr>
					<tr>
						<td colspan="2">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1"  style="padding-left: 32px; width: 145px;">
										<s:radio label="Gender" 
											name="dataSource.qosMarkMode" list="%{qosDscp}"
											id="qosMarkModeDscp"
											listKey="key" listValue="value" 
											disabled="%{!dataSource.enableQosMark}"/>
									</td>
									<td class="labelT1">
										<s:radio label="Gender" 
											name="dataSource.qosMarkMode" list="%{qos8021p}"
											id="qosMarkMode8021p"
											listKey="key" listValue="value" 
											disabled="%{!dataSource.enableQosMark}"/>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div id="qosSettings4WanDiv" style="display: <s:property value="%{dataSource.wanPortStyle}"/>">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<th width="100px"><s:text name="config.port.qos.setting.interface.title"/></th>
				<th colspan="2" width="300px">
					<div align="left" ><span style="padding-left: 10px;"><s:text name="config.configTemplate.voip.enablelimitbandwidth.download.title" /></span></div>
					<div align="left"><span style="padding-left: 15px;"><s:text name="config.configTemplate.voip.enable" /></span>
						 <span style="padding-left: 32px;"><s:text name="config.configTemplate.voip.maxrate" /></span>
					</div>
				</th>
				<th colspan="2" width="300px">
					<div align="left"><span style="padding-left: 10px;"><s:text name="config.configTemplate.voip.enablelimitbandwidth.upload.title" /></span></div>
					<div align="left"><span style="padding-left: 15px;"><s:text name="config.configTemplate.voip.enable" /></span>
						 <span style="padding-left: 32px;"><s:text name="config.configTemplate.voip.maxrate" /></span>
					</div>
				</th>
			</tr>
			<tr>
				<td class="list" style="width: 100px;"><s:text name="config.port.qos.setting.interface.eth" /></td>
				<td class="list" align="right" style="width: 30px;">
						<s:checkbox name="dataSource.enableEthLimitDownloadBandwidth" onclick="enabledVoipCheckBox(this.checked,'ethLimitDownloadRate')"/>
				</td>
				<td class="list" align="left" style="width: 150px;padding-left: 44px;">
						<s:textfield name="dataSource.ethLimitDownloadRate" size="10"
							onkeypress="return hm.util.keyPressPermit(event,'ten');"
							maxlength="5" readonly="%{!dataSource.enableEthLimitDownloadBandwidth}"/>
						<s:text name="config.configTemplate.voip.enablelimitbandwidth.range" />
				</td>
				<td class="list" align="right" style="width: 30px;">
						<s:checkbox name="dataSource.enableEthLimitUploadBandwidth" onclick="enabledVoipCheckBox(this.checked,'ethLimitUploadRate')"/>
				</td>
				<td class="list" align="left" style="width: 150px;padding-left: 42px;">
					<s:textfield name="dataSource.ethLimitUploadRate" size="10"
						onkeypress="return hm.util.keyPressPermit(event,'ten');"
						maxlength="5" readonly="%{!dataSource.enableEthLimitUploadBandwidth}"/>
						<s:text name="config.configTemplate.voip.enablelimitbandwidth.range" />
				</td>
			</tr>
			<tr>
				<td class="list"><s:text name="config.port.qos.setting.interface.usb" /></td>
				<td class="list" align="right" style="width: 30px;">
						<s:checkbox name="dataSource.enableUSBLimitDownloadBandwidth" onclick="enabledVoipCheckBox(this.checked,'usbLimitDownloadRate')"/>
				</td>
				<td class="list" align="left" style="width: 150px;padding-left: 44px;">
					<s:textfield name="dataSource.usbLimitDownloadRate" size="10"
						onkeypress="return hm.util.keyPressPermit(event,'ten');"
						maxlength="5" readonly="%{!dataSource.enableUSBLimitDownloadBandwidth}"/>
						<s:text name="config.configTemplate.voip.enablelimitbandwidth.range" />
				</td>
				<td class="list" align="right" style="width: 30px;">
					<s:checkbox name="dataSource.enableUSBLimitUploadBandwidth" onclick="enabledVoipCheckBox(this.checked,'usbLimitUploadRate')"/>
				</td>
				<td class="list" align="left" style="width: 150px;padding-left: 42px;">	
					<s:textfield name="dataSource.usbLimitUploadRate" size="10"
						onkeypress="return hm.util.keyPressPermit(event,'ten');"
						maxlength="5" readonly="%{!dataSource.enableUSBLimitUploadBandwidth}"/>
						<s:text name="config.configTemplate.voip.enablelimitbandwidth.range" />
				</td>
			</tr> 
		</table>
	</div>
</fieldset>

	