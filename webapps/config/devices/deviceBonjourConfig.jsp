<%@taglib prefix="s" uri="/struts-tags"%>
<div id="deviceBonjourGatewayConfig">
	<s:if test="%{dataSource.deviceInfo.isSupportAttribute(@com.ah.bo.hiveap.DeviceInfo@SPT_BONJOUR_SERVICE)}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.bonjourGateway.label" />','bonjourGatewayConfig');</script></td>
		</tr>
		<tr>
			<td>
				<div id="bonjourGatewayConfig"
					style="display: <s:property value="%{dataSource.bjgwConfigDisplayStyle}"/>">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td class="labelT1" width="100"><label><s:text
							name="hiveAp.bonjour.gateway.priority" /><font color="red"><s:text name="*" /></font></label></td>
						<td><s:textfield name="dataSource.priority" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
							<s:text name="hiveAp.bonjour.gateway.priority.range"/></td>
					</tr>
					<tr>
						<td class="labelT1" width="100"><label><s:text
							name="monitor.bonjour.gateway.realm.modifyname" /></label></td>
						<td><s:textfield name="dataSource.realmName" maxlength="128" size="48" onkeypress="return hm.util.keyPressPermit(event,'name');"/>
							<s:text name="config.BonjourGatewaySetting.description.range"/></td>
					</tr>
					<tr>
						<td class="labelT1" colspan="2">
							<s:checkbox name="dataSource.lockRealmName" />
							<s:text name="hiveAp.bonjour.gateway.lockRealmName" /></td>
					</tr>
					<tr>
						<td colspan="2" class="noteInfo" style="padding-left: 10px;"><s:text name="hiveAp.bonjour.gateway.realm.name.note"/></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>