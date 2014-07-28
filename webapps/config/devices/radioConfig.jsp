<%@taglib prefix="s" uri="/struts-tags"%>
<div>
	<s:if test="%{dataSource.deviceInfo.supportWifiRadio}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><s:radio label="Gender"
				name="dataSource.radioConfigType" list="%{radioModeType1}"
				listKey="key" listValue="value"
				onchange="radioModeTypeChanged(this.value);"
				onclick="this.blur();" /></td>
		</tr>

		<tr id="radioConfigTypeTr2" >
			<td><s:radio label="Gender"
				name="dataSource.radioConfigType" list="%{radioModeType2}"
				listKey="key" listValue="value"
				onchange="radioModeTypeChanged(this.value);"
				onclick="this.blur();" /></td>
		</tr>
		<tr id="radioConfigTypeTr2_note"
			style="display: <s:property value='radioConfigType2NoteStyle'/>">
			<td style="padding-left: 22px;" class="noteInfo">
				<s:text name="hiveAp.radioMode.meshNote"/></td>
		</tr>

		<tr style="display:<s:property value='radioTypeAccessDualStyle'/>">
			<td><s:radio label="Gender"
				name="dataSource.radioConfigType" list="%{radioModeType5}"
				listKey="key" listValue="value"
				onchange="radioModeTypeChanged(this.value);"
				onclick="this.blur();" /></td>
		</tr>
		<tr id="radioConfigTypeDual_note"
			style="display: <s:property value='radioConfigDualNoteStyle'/>">
			<td style="padding-left: 22px;" class="noteInfo">
				<s:text name="hiveAp.radioMode.meshNote"/></td>
		</tr>

		<tr id = "radioConfigTypeCustomer"
			style="display: <s:property value="%{radioConfigCustomerStyle}"/>">
			<td><s:radio label="Gender"
				name="dataSource.radioConfigType" list="%{radioModeType4}"
				listKey="key" listValue="value"
				onchange="radioModeTypeChanged(this.value);"
				onclick="this.blur();" /></td>
		</tr>
		
		<tr id="enableEthBridge_id" >
			<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td><s:checkbox name="dataSource.enableEthBridge" onclick="clickEnableEthBridge(this.checked);"
							disabled="%{enableEthBridgeDisabled}" />
						<s:text name="hiveAp.radioMode.ethbridge"/>
					</td>
				</tr>
				<tr id="enableEthBridge_note"
					style="display: <s:property value="%{enableEthBridgeNodeStyle}"/>">
					<td style="padding-left: 25px;" class="noteInfo">
						<s:text name="hiveAp.radioMode.bridgeNote1"/><br>
						<s:text name="hiveAp.radioMode.bridgeNote2"/>
					</td>
				</tr>
			</table></td>
		</tr>
	</table>
	</s:if>
</div>