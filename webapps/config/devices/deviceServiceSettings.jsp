<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="serviceSettingStyle">
	<s:if test="%{!dataSource.deviceInfo.cvgAsL3Vpn}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.service.label" />','serviceSettings');</script></td>
		</tr>
		<tr>
			<td>
			<div id="serviceSettings"
				style="display: <s:property value="%{dataSource.serviceSettingsDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="5px"></td>
						</tr>
						<s:if test="%{dataSource.deviceInfo.isSupportAttribute(@com.ah.bo.hiveap.DeviceInfo@SPT_RADIUS_SERVER)}">
						<tr id="brAsRadiusServerDisplayDiv" style="display: <s:property value="%{brAsRadiusServerDisplayStyle}"/>">
							<td colspan="4" style="padding: 2px 0 2px 6px">
								<s:checkbox name="dataSource.enabledBrAsRadiusServer" onclick="changeEnabledBrAsRadiusServer(this.checked)"></s:checkbox>
								<s:text name="hiveAp.enableBrAsRadiusServer"/>
							</td>
						</tr>
						<tr id="brAsRadiusServerOverrideDisplayDiv" style="display: <s:property value="%{brAsRadiusServerOverrideDisplayDiv}"/>">
							<td colspan="4" style="padding: 2px 0 2px 26px">
								<s:checkbox name="dataSource.enabledOverrideRadiusServer" onclick="changeEnabledBrAsRadiusServerOverride(this.checked)"></s:checkbox>
								<s:text name="hiveAp.enableBrAsRadiusServerOverride"/>
							</td>
						</tr>
						<tr id="radiusServerRowDiv" style="display: <s:property value="%{radiusServerRowDivStyle}"/>">
							<td colspan="10"><table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td id="radioServerHeadTd">&nbsp;</td>
									<td class="labelT1" width="150px" style="padding-right: 20px;">
										<span id="radiusServerLabelId">
										<s:property value="radiusServerLabelValue"/></span></td>
									<td><s:select list="%{radiusServers}" listKey="id"
										listValue="value" name="radiusServer"
										cssStyle="width: 150px;" />
									<s:if test="%{!easyMode}">
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
												src="<s:url value="/images/new_disable.png" />"
												width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn"
												href="javascript:submitAction('newRadius')"><img
												class="dinl" src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
												src="<s:url value="/images/modify_disable.png" />"
												width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn"
												href="javascript:submitAction('editRadius')"><img
												class="dinl" src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</s:if></td>
								</tr>
							</table></td>
						</tr>
						<tr id="radiusProxyRowDiv" style="display: <s:property value="%{radiusProxyRowDivStyle}"/>">
							<td colspan="10">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td id="radioProxyHeadTd">&nbsp;</td>
										<td class="labelT1" width="150px"
											style="padding-right: 20px;"><span id="radiusProxyLabelId">
											<s:property value="radiusProxyLabelValue"/> </span></td>
										<td><s:select list="%{radiusProxys}" listKey="id"
											listValue="value" name="radiusProxy"
											cssStyle="width: 150px;" />
										<s:if test="%{!easyMode}">
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
											</s:if>
											<s:else>
												<a class="marginBtn"
													href="javascript:submitAction('newRadiusProxy')"><img
													class="dinl" src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
											</s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
											</s:if>
											<s:else>
												<a class="marginBtn"
													href="javascript:submitAction('editRadiusProxy')"><img
													class="dinl" src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else>
										</s:if></td>
									</tr>
								</table>
							</td>
						</tr>
						</s:if>
						<!-- tr id="brAsPpskServerDisplayDiv" style="display: <s:property value="%{brAsPpskServerDisplayStyle}"/>">
							<td colspan="4" style="padding: 2px 0 2px 6px">
								<s:checkbox name="dataSource.enabledBrAsPpskServer"></s:checkbox>
								<s:text name="hiveAp.enableBrAsPpskServer"/>
							</td>
						</tr-->
						<tr id="useNasIdenInRadiusReq">
							<td class="labelT1" colspan="4" style="padding: 5px 0 0px 10px"><s:text
								name="hiveAp.nasIdentifier" /></td>
						</tr>
						<tr id="radioNasTypeTr2" >
							<td colspan="10" style="padding-left: 22px" class="labelT1"><table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td ><s:radio label="Gender"
										name="dataSource.nasIdentifierType" list="%{radioNasType1}"
										listKey="key" listValue="value"
										onchange="radioNasTypeChanged(this.value);"
										onclick="this.blur();" /></td>
								</tr>
							</table></td>
						</tr>
						<tr id="rradioNasTypeTr2" >
							<td colspan="10" style="padding-left: 22px" class="labelT1"><table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td width="230px"><s:radio label="Gender"
										name="dataSource.nasIdentifierType" list="%{radioNasType2}"
										listKey="key" listValue="value"
										onchange="radioNasTypeChanged(this.value);"
										onclick="this.blur();" /></td>
									<td><s:textfield name="dataSource.customizedNasIdentifier" size="24"
											maxlength="64" disabled="%{customizedNasIdenReadonly}"/></td>
									<td>&nbsp;<s:text name="hiveAp.nasIdentifier.range" /></td>
								</tr>
							</table></td>
						</tr>
						
						<s:if test="%{IDMEnableForCurrentVHM}">
						<tr>
							<td colspan="10">
								<fieldset><legend><s:text name="hiveAp.idmAuthProxy.label" /></legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td>
												<s:checkbox name="dataSource.enableIDMAuthProxy"/>
												<s:text name="hiveAp.server.idmAuthProxy.enable" />
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						</s:if>
						
						<s:if test="%{dataSource.deviceInfo.deviceTypeAp}">
						<tr id="vpnRuleTr" style="display: <s:property value="vpnRuleStyle"/>">
							<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td class="labelT1" style="padding-right: 40px;" colspan="2"><s:text
										name="hiveAp.server.vpn.role" /></td>
									<td><s:select list="%{enumVPNMarkType}" listKey="key"
										listValue="value" name="dataSource.vpnMark"
										cssStyle="width: 150px;" /></td>
								</tr>
							</table></td>
						</tr>
						
						<tr id="dhcpServerOptionsRow"
							style="display: <s:property value="dhcpServerOptionsRowStyle"/>">
							<td colspan="10">
							<fieldset><legend><s:text
								name="hiveAp.dhcpServerLabel" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%" class="embedded">
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td style="padding-left: 10px;">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<s:push value="%{dhcpServerOptions}">
												<td><tiles:insertDefinition
													name="optionsTransfer" /></td>
											</s:push>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						</s:if>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>