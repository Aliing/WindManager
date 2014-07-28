<%@taglib prefix="s" uri="/struts-tags"%>
<div id="routerWanSettings">
	<s:if test="dataSource.deviceInfo.onlyRouterFunc">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
			<fieldset>
			<table cellspacing="0" cellpadding="0" border="0">
				<tr id="routerWanRow"><td colspan="10"><table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td class="labelT1">
							<s:checkbox name="branchRouterEth0.enableDhcp" onclick="brRouterEth0DhcpChanged(this.checked);"/>
							<s:text name="hiveAp.br100.dhcp.enable"/>
						</td>
					</tr>
					<tr id="brRouterEth0DhcpSettings"
						style="display: <s:property value='brRouterEth0DhcpSettingsStyle'/>">
						<td style="padding-left: 22px;"><table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="110px">
									<s:text name="hiveAp.br100.wan.ipaddress" />
								</td>
								<td class="labelT1" width="300px">
									<s:textfield name="branchRouterEth0.ipAndNetmask" size="18"
										maxlength="18"
										onkeypress="return hm.util.keyPressPermit(event,'ipMask');" />
									<s:text name="config.vpn.subnet.ipnetwork.range" />
								</td>
								<td class="labelT1" width="100px">
									<s:text name="hiveAp.gateway" />
								</td>
								<td class="labelT1" width="200px">
									<s:textfield name="branchRouterEth0.gateway" size="18" maxlength="15"
										onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
								</td>
							</tr>
						</table></td>
					</tr>
				</table></td></tr>
			</table>
			</fieldset>
			</td>
		</tr>
	</table>
	</s:if>
</div>