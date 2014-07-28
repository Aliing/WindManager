<%@taglib prefix="s" uri="/struts-tags"%>
<div id="mgt0InterfaceConfigStyle">
	<s:if test="%{dataSource.deviceInfo.deviceTypeAp || dataSource.deviceInfo.deviceTypeSwitch}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.mgt0Interface" />','mgt0DhcpSettings');</script></td>
		</tr>
		<tr>
			<td>
			<div id="mgt0DhcpSettings" style="display: <s:property value='dataSource.mgt0DhcpSettingsStyle'/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr><td style="padding-left:10px"><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
						<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="5px"></td>
							</tr>
							<tr>
								<td width="160px"><s:radio label="Gender"
									name="mgt0NetworkType" list="%{Mgt0NetworkType1}"
									listKey="key" listValue="value"
									onchange="radioMgt0NetworkType(this.value,true);"
									onclick="this.blur();" /></td>

								<td width="280px"><s:radio label="Gender"
									name="mgt0NetworkType" list="%{Mgt0NetworkType2}"
									listKey="key" listValue="value"
									onchange="radioMgt0NetworkType(this.value,true);"
									onclick="this.blur();" /></td>

								<td width="200x"><s:radio label="Gender"
									name="mgt0NetworkType" list="%{Mgt0NetworkType3}"
									listKey="key" listValue="value"
									onchange="radioMgt0NetworkType(this.value,true);"
									onclick="this.blur();" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" id="dhcpLabel" width="250px">
									<s:if test="%{mgt0NetworkType == 3}">
										<s:text name="hiveAp.default.ipAddress" />
									</s:if>
									<s:elseif test="%{mgt0NetworkType == 2}">
										<s:text name="hiveAp.mgt0Interface.dhcpFallback" />
									</s:elseif>
									<s:else>
										<s:text name="hiveAp.ipAddress" />
									</s:else>
								</td>
								<td width="160px"><s:if test="%{mgt0NetworkType == 3}">
									<s:textfield name="dataSource.cfgIpAddress" size="15"
										maxlength="%{cfgIpAddressLength}"
										title="%{defaultIpPrefixFormat}" />
								</s:if> <s:else>
									<s:textfield name="dataSource.cfgIpAddress" size="15"
										maxlength="%{cfgIpAddressLength}" />
								</s:else></td>

								<td class="labelT1" width="60px"><s:text name="hiveAp.netmask" /></td>
								<td width="160px"><s:if
									test="%{mgt0NetworkType == 3}">
									<s:textfield name="dataSource.cfgNetmask" size="15"
										maxlength="%{cfgNetmaskLength}"
										title="%{defaultNetmaskFormat}" />
								</s:if> <s:else>
									<s:textfield name="dataSource.cfgNetmask" size="15"
										maxlength="%{cfgNetmaskLength}" />
								</s:else></td>

								<td width="100px" class="labelT1" id="gatewayLbTd"
									style="display: <s:property value="%{gatewayVisibilityStyle}"/>;"><s:text
									name="hiveAp.gateway" /></td>
								<td id="gatewayTd"
									style="display: <s:property value="%{gatewayVisibilityStyle}"/>;"><s:textfield
									name="dataSource.cfgGateway" size="15"
									maxlength="%{cfgGatewayLength}"
									/></td>

							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<s:if test="%{mgt0NetworkType == 3}">
									<td class="labelT1" id="dhcpTimeoutLbTd" width="250px"
										style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;">
								</s:if>
								<s:else>
									<td class="labelT1" id="dhcpTimeoutLbTd" width="120px"
										style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;">
								</s:else>
									<s:text name="hiveAp.dhcpTimeout" /></td>
								<td id="dhcpTimeoutTd" width="117px"
									style="display: <s:property value="%{dhcpTimeoutVisibilityStyle}"/>;"><s:textfield
									name="dataSource.dhcpTimeout" size="3"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="4" title="%{dhcpTimeoutRange}"
									/>&nbsp;<s:text name="hiveAp.dhcpTimeout.unit" /></td>

								<td class="headCheck" id="addressOnlyLbTd"
									style="padding-left: 50px;display: <s:property value="%{addressOnlyVisibilityStyle}"/>;"><s:checkbox
									name="dataSource.addressOnly"
									value="%{dataSource.addressOnly}"
									/></td>
								<td class="labelT1" id="addressOnlyTd"
									style="display: <s:property value="%{addressOnlyVisibilityStyle}"/>;"><s:text
									name="hiveAp.addressOnly" /></td>
							</tr>
						</table>
						</td>
					</tr>
					</table></td>
					</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>