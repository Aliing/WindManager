<%@taglib prefix="s" uri="/struts-tags"%>
<script>
	function showDealyAlarmEnable(checked){
		Get("delayAlarmEnableTr").style.display = checked? "":"none";
	}
</script>

<div id="deviceAdvancedSettings">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.advanced.label" />','advancedSettings');</script></td>
		</tr>
		<tr>
			<td>
			<div id="advancedSettings"
				style="display: <s:property value="%{dataSource.advSettingsDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">

				<s:if test="%{dataSource.deviceInfo.deviceTypeAp}">
				<tr id="vlanSetting"
					style="display: <s:property value="%{vlanSettingStyle}"/>">
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
								id="overrideVlan" name="overrideVlan"
								value="%{nativeVlanOverride}"
								onclick="changeNativeVlanInput(this.checked);"></s:checkbox>
							</td>
							<td width="180px"><s:if test="%{easyMode}">
								<span><s:text
									name="hiveAp.nativeVlan.override.express" /></span>
							</s:if> <s:else>
								<span><s:text name="hiveAp.nativeVlan.override" /></span>
							</s:else></td>
							<td><s:textfield name="dataSource.nativeVlan"
								size="24" disabled="%{nativeVlanDisabled}"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="4" title="%{vlanRange}" /> <s:text
								name="hiveAp.nativeVlan.range" /></td>
						</tr>
						<tr>
							<td style="padding: 5px 0 5px 10px;"><s:checkbox
								id="overrideMgtVlan" name="overrideMgtVlan"
								value="%{mgtVlanOverride}"
								onclick="changeMgtVlanInput(this.checked);"></s:checkbox>
							</td>
							<td><s:if test="%{easyMode}">
								<span><s:text
									name="hiveAp.mgtVlan.override.express" /></span>
							</s:if> <s:else>
								<span><s:text name="hiveAp.mgtVlan.override" /></span>
							</s:else></td>
							<td><s:textfield name="dataSource.mgtVlan" size="24"
								disabled="%{mgtVlanDisabled}"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="4" title="%{mgtVlanRange}" /> <s:text
								name="hiveAp.nativeVlan.range" /></td>
						</tr>
						<s:if test="%{easyMode}">
							<tr>
								<td style="padding: 5px 0 5px 10px;"><s:checkbox
									name="dataSource.enableDas"
									value="%{dataSource.enableDas}"></s:checkbox></td>
								<td><span><s:text
									name="config.configTemplate.enableAirTime" /></span></td>
								<td></td>
							</tr>
						</s:if>
					</table>
					</td>
				</tr>
				</s:if>

				<!--start br100 PMTUD  -->
				<s:if test="%{dataSource.deviceInfo.containsRouterFunc}">
				<tr id="brPmtudSettings">
					<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr id="enableOverrideBrPMTUDTr">
						<td><table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.enableOverrideBrPMTUD" value="dataSource.enableOverrideBrPMTUD"
																		onclick="enableOverrideBrPMTUD(this.checked);"/></td>
								<td><s:text name="hiveAp.vpn.br.override.pmtud.mss"/></td>
							</tr>
						</table></td></tr>
						<tr id="enableBrPMTUDTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
						<td><table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td style="padding: 5px 0 5px 25px;"><s:checkbox name="dataSource.enableBrPMTUD" value="dataSource.enableBrPMTUD"
																			disabled="%{!dataSource.enableOverrideBrPMTUD}"/></td>
								<td><s:text name="hiveAp.vpn.enablepmtud"/></td>
							</tr>
						</table></td></tr>
						<tr id="monitorBrMSSTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
						<td><table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td  style="padding: 5px 0 5px 25px;"><s:checkbox name="dataSource.monitorBrMSS" value="dataSource.monitorBrMSS"
																			disabled="%{!dataSource.enableOverrideBrPMTUD}"
																			onclick="enableMonitorBrMSS(this.checked);"/></td>
								<td><s:text name="hiveAp.vpn.enablemonitormss"/></td>
							</tr>
						</table></td></tr>
						<tr id="monitorBrMSSNoteTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
							<td style="padding-left: 45px;padding-bottom: 5px;" class="noteInfo">
								<s:text name="hiveAp.vpn.enablemonitormss.note"/></td>
						</tr>
						<tr id="thresholdBrForAllTCPTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" style="padding-left: 45px;" width="360" ><s:text name="hiveAp.vpn.threshold.all.tcp.connections" /></td>
										<td><s:textfield name="dataSource.thresholdBrForAllTCP" size="18"
												disabled="%{!dataSource.monitorBrMSS&&!dataSource.enableOverrideBrPMTUD}"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
											<s:text name="hiveAp.vpn.threshold.range"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr id="thresholdBrThroughVPNTunnelTr" style="display:<s:property value="%{brRouteIntervalStyle}" />">
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" style="padding-left: 45px;" width="360" ><s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" /></td>
										<td><s:textfield name="dataSource.thresholdBrThroughVPNTunnel" size="18"
												disabled="%{!dataSource.monitorBrMSS&&!dataSource.enableOverrideBrPMTUD}"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
											<s:text name="hiveAp.vpn.threshold.range"/></td>
									</tr>
								</table>
							</td>
						</tr>
					</table></td>
				</tr>
				<!--end br100 PMTUD  -->
				<tr id="brRouteInterval" style="display:<s:property value="%{brRouteIntervalStyle}" />">
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td width="220" class="labelT1"><s:text name="hiveAp.vpn.br.route.interval" /></td>
								<td><s:textfield name="dataSource.routeInterval" size="18"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									<s:text name="hiveAp.vpn.br.route.interval.range"/></td>
							</tr>
							<tr id="maxPowerSourceRow">
								<td width="220" class="labelT1"><s:text name="hiveAp.vpn.br.route.powersource.maximum" /></td>
								<td><s:textfield name="dataSource.maxPowerSource" size="18"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="2"/>
									<s:text name="hiveAp.vpn.br.route.powersource.range"/></td>
							</tr>
						</table>
					</td>
				</tr>
				</s:if>

				<!-- start CVG PMTUD -->
				<s:if test="%{dataSource.deviceInfo.onlyCVG}">
				<tr id="cvgPmtudSettings">
					<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr id="enableCvgPMTUDTr">
						<td><table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.enableCvgPMTUD" value="dataSource.enableCvgPMTUD" /></td>
								<td><s:text name="hiveAp.vpn.enablepmtud"/></td>
							</tr>
						</table></td></tr>
						<tr id="monitorCvgMSSTr">
						<td><table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td  style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.monitorCvgMSS" value="dataSource.monitorCvgMSS"
																			onclick="enableMonitorCvgMSS(this.checked);"/></td>
								<td><s:text name="hiveAp.vpn.enablemonitormss"/></td>
							</tr>
						</table></td></tr>
						<tr id="monitorCvgMSSNoteTr">
							<td style="padding-left: 28px;padding-bottom: 5px;" class="noteInfo">
								<s:text name="hiveAp.vpn.enablemonitormss.note"/></td>
						</tr>
						<tr id="thresholdCvgForAllTCPTr">
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" style="padding-left: 28px;" width="360" ><s:text name="hiveAp.vpn.threshold.all.tcp.connections" /></td>
										<td><s:textfield name="dataSource.thresholdCvgForAllTCP" size="18"
												disabled="%{!dataSource.monitorCvgMSS}"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
											<s:text name="hiveAp.vpn.threshold.range"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr id="thresholdCvgThroughVPNTunnelTr">
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" style="padding-left: 28px;" width="360" ><s:text name="hiveAp.vpn.threshold.tcp.connections.vpn.tunnel" /></td>
										<td><s:textfield name="dataSource.thresholdCvgThroughVPNTunnel" size="18"
												disabled="%{!dataSource.monitorCvgMSS}"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
											<s:text name="hiveAp.vpn.threshold.range"/></td>
									</tr>
								</table>
							</td>
						</tr>
					</table></td>
				</tr>
				<!-- end CVG PMTUD -->
				<tr id="vpnIpTrackTr">
				<td><table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td class="labelT1" width="200px"><s:text
							name="hiveAp.vpn.gateway.ip.track.label" /></td>
						<td><s:select list="%{availableIpTracks}" listKey="id"
							listValue="value" name="vpnIpTrackId"
							cssStyle="width: 150px;" />
							<s:if test="%{writeDisabled == 'disabled'}">
							<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
							<a class="marginBtn"
								href="javascript:submitAction('newIpTrack')"><img
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
								href="javascript:submitAction('editIpTrack')"><img
								class="dinl" src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
						</td>
					</tr>
				</table></td></tr>
				</s:if>

				<tr id="auditScheduler" style="display: <s:property value="%{auditSchedulerStyle}"/>">
				<td><table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td class="labelT1" width="200px"><s:text
							name="hiveAp.scheduler.label" /></td>
						<td><s:select list="%{schedulers}" listKey="id"
							listValue="value" name="scheduler"
							cssStyle="width: 150px;" /> <s:if
							test="%{writeDisabled == 'disabled'}">
							<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
						</s:if> <s:else>
							<a class="marginBtn"
								href="javascript:submitAction('newScheduler')"><img
								class="dinl" src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
						</s:else> <s:if test="%{writeDisabled == 'disabled'}">
							<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
						</s:if> <s:else>
							<a class="marginBtn"
								href="javascript:submitAction('editScheduler')"><img
								class="dinl" src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
						</s:else></td>
					</tr>
				</table></td></tr>
				<tr>
				<td><table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
							name="dataSource.includeTopologyInfo" value="%{dataSource.includeTopologyInfo}"></s:checkbox>
						</td>
						<td><span><s:text name="glasgow_12.hiveAp.insert.topology" /></span></td>
					</tr>
					<tr>
						<td style="padding: 5px 0 5px 30px;" colspan="10" align="left" class="noteInfo"><s:text name="glasgow_12.hiveAp.insert.topology.note" /></td>
					</tr>
				</table></td></tr>
				<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" width="200px"><s:text
									name="hiveAp.interface.mtu.ethernet.label" /></td>
								<td><s:textfield name="dataSource.interfaceMtu4Ethernet" size="6" maxlength="4"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								<s:text name="hiveAp.interface.mtu.ethernet.range"/></td>
							</tr>
							<tr style="display:none;">
								<td class="labelT1" width="200px"><s:text
									name="hiveAp.interface.mtu.mgt0.label" /></td>
								<td><s:textfield name="dataSource.interfaceMtu4Mgt0" size="6" maxlength="4"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								<s:text name="hiveAp.interface.mtu.mgt0.range"/></td>
							</tr>
						</table>
					</td>
				</tr>
				</s:if>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
				<!--tr id="distributedPriority"
					style="display: <s:property value="%{distributedPriorityStyle}"/>">
					<td>
					<table cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td class="labelT1" width="350px"><s:text
								name="hiveAp.distributedPriority" /></td>
							<td><s:select list="%{enumDistributedPriority}"
								listKey="key" listValue="value"
								name="dataSource.distributedPriority"
								value="%{dataSource.distributedPriority}"
								cssStyle="width:120px;" /></td>
						</tr>
					</table>
					</td>
				</tr -->

				<s:if test="%{dataSource.deviceInfo.deviceTypeAp}">
				<tr id="virtualConnectSection"
					style="display: <s:property value="%{virtualConnectDisplay}"/>">
					<td valign="top" width="100%" style="padding-top: 5px;">
					<fieldset><legend><s:text
						name="hiveAp.virtualConnection" /></legend>
					<table cellspacing="0" cellpadding="0" border="0"
						width="100%" class="embedded">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding: 0 0 0 0;">
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<td colspan="10" align="left" class="noteInfo"><s:text
										name="hiveAp.virtualConnection.note" /></td>
								</tr>
								<tr id="newButtonVirtualConnect">
									<td colspan="5" style="padding-top: 5px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New"
												class="button"
												onClick="showCreateSection('virtualConnect');"
												<s:property value="writeDisabled" />></td>
											<td><input type="button" name="ignore"
												value="Remove" class="button"
												onClick="doRemoveVirtualConnect();"
												<s:property value="writeDisabled" />></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display: none;" id="createButtonVirtualConnect">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore"
												value="Apply" class="button"
												onClick="doAddVirtualConnect();"></td>
											<td><input type="button" name="ignore"
												value="Remove" class="button"
												onClick="doRemoveVirtualConnect();"></td>
											<td><input type="button" name="ignore"
												value="Cancel" class="button"
												onClick="hideCreateSection('virtualConnect');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<s:if test="%{jsonMode}">
										<div style="overflow-x:auto;overflow-y:hidden;width: 687px;">
									</s:if>
									<s:else>
										<div>
									</s:else>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td colspan="9">
														<table id="virtualConnectTblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
															<tr id="headerSectionVirtualConnect">
																<th align="left" style="padding-left: 0;"><input
																	type="checkbox" id="checkAllVirtualConnect"
																	onClick="toggleCheckAllVirtualConnects(this);"></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.name" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.action" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.inInterface" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.sourceMac" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.destMac" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.txMac" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.outInterface" /></th>
																<th align="left" nowrap="nowrap"><s:text
																	name="hiveAp.virtualConnection.rxMac" /></th>
															</tr>
															<tr style="display: none;"
																id="createSectionVirtualConnect">
																<td class="listHead">&nbsp;</td>
																<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																	name="virtualConnectName" size="16" maxlength="32" /><br>
																<s:text name="config.configTemplate.configName.range" /></td>
																<td class="listHead" valign="top"><s:select
																	name="virtualConnectAction"
																	list="%{virtualConnectActions}" listKey="key"
																	listValue="value" cssStyle="width: 110px;"
																	onchange="selectVirtualAction(this.value)" /></td>
																<td class="listHead" valign="top"><s:select
																	name="virtualConnectInterface_in"
																	list="%{virtualConnectInterfaces}" listKey="key"
																	listValue="value" cssStyle="width: 110px;"
																	onchange="selectInInterface(this.value)" /></td>
																<td class="listHead" valign="top"><s:textfield
																	name="virtualConnectSourceMac" size="16" maxlength="12"
																	onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<br>
																<s:text name="hiveAp.virtualConnection.sourceMac.range" /></td>
																<td class="listHead" valign="top"><s:textfield
																	name="virtualConnectDestMac" size="16" maxlength="12"
																	onkeypress="return hm.util.keyPressPermit(event,'hex');" />
																<br>
																<s:text name="config.macOrOui.addressRange" /></td>
																<td class="listHead" valign="top"><s:textfield
																	name="virtualConnectTxMac" size="16" maxlength="12"
																	onkeypress="return hm.util.keyPressPermit(event,'hex');"
																	disabled="true" /> <br>
																<s:text name="config.macOrOui.addressRange" /></td>
																<td class="listHead" valign="top"><s:select
																	name="virtualConnectInterface_out"
																	list="%{virtualConnectInterfaces}" listKey="key"
																	listValue="value" cssStyle="width: 110px;"
																	onchange="selectOutInterface(this.value)" /></td>
																<td class="listHead" valign="top"><s:textfield
																	name="virtualConnectRxMac" size="16" maxlength="12"
																	onkeypress="return hm.util.keyPressPermit(event,'hex');"
																	disabled="true" /> <br>
																<s:text name="config.macOrOui.addressRange" /></td>
															</tr>
															<s:iterator value="%{dataSource.virtualConnections}"
																status="status">
																<tr id = "virtualConnectionsOnly">
																	<td class="listCheck"><s:checkbox
																		name="virtualConnectIndices"
																		fieldValue="%{#status.index}" /></td>
																	<td class="list"><s:property value="forwardName" /></td>
																	<td class="list"><s:property
																		value="forwardAction4Display" /></td>
																	<td class="list"><s:property
																		value="interface_in4Display" /></td>
																	<td class="list"><s:property value="sourceMac" /></td>
																	<td class="list"><s:property value="destMac" /></td>
																	<td class="list"><s:property value="txMac" /></td>
																	<td class="list"><s:property
																		value="interface_out4Display" /></td>
																	<td class="list"><s:property value="rxMac" /></td>
																</tr>
															</s:iterator>
														</table>
													</td>
												</tr>
												<tr>
													<td colspan="10" width="100%">
														<table id="virtualConnectTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
															<s:if test="%{virtualConnectGridCount > 0}">
																<s:generator separator="," val="%{' '}"
																	count="%{virtualConnectGridCount}">
																	<s:iterator>
																		<tr>
																			<td class="list" colspan="9">&nbsp;</td>
																		</tr>
																	</s:iterator>
																</s:generator>
															</s:if>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="30"></td>
						</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				</s:if>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<s:if test="%{easyMode}">
							<tr>
								<td style="padding: 5px 0 5px 10px;" align="left" colspan="2"><s:checkbox
									name="dataSource.enableDelayAlarm" value="%{dataSource.enableDelayAlarm}"></s:checkbox>
									<span><s:text name="guadalupe_01.enable.capwap.alarm"/></span>
								</td>
							</tr>
							</s:if>
							<s:else>
							<tr id="overrideEnableDelayAlarmTr">
								<td style="padding: 5px 0 5px 10px;" width="15px"><s:checkbox
									name="dataSource.overrideEnableDelayAlarm" id="overrideEnableDelayAlarm" value="%{dataSource.overrideEnableDelayAlarm}"
									onclick="showDealyAlarmEnable(this.checked);"></s:checkbox>
								</td>
								<td><span><s:text name="guadalupe_01.enable.capwap.alarm.override"/></span></td>
							</tr>
							<tr style="display: <s:property value="%{delayAlarmEnableStyle}"/>" id="delayAlarmEnableTr">
								<td style="padding: 5px 0 5px 30px;" align="left" colspan="2" id="enableDelayAlarmTd"><s:checkbox
									name="dataSource.enableDelayAlarm" value="%{dataSource.enableDelayAlarm}"></s:checkbox>
									<span><s:text name="guadalupe_01.enable.capwap.alarm"/></span>
								</td>
							</tr>
							</s:else>
						</table>
					</td>
				</tr>
				<tr style="display: <s:property value="%{supplementalCLIStyle}"/>" >
					<td>
						<table cellspacing="0"
							cellpadding="0" border="0">
							<tr>
								<td class="labelT1" width="220px"><s:text
										name="hollywood_02.supp_cli_setting" /></td>
								<td><s:select
										list="%{list_cliBlob}" listKey="id"
										listValue="value"
										name="supplementalCLIId"
										cssStyle="width: 150px;" /> <s:if
										test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New"
											title="New" />
									</s:if> <s:else>
										<a class="marginBtn"
											href="javascript:submitAction('newSuppCLI')"><img
											class="dinl"
											src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New"
											title="New" /></a>
									</s:else> <s:if
										test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify"
											title="Modify" />
									</s:if> <s:else>
										<a class="marginBtn"
											href="javascript:submitAction('editSuppCLI')"><img
											class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify"
											title="Modify" /></a>
									</s:else></td>
							</tr>
						</table>
					</td>
			  </tr>
				
			</table>
			</div>
			</td>
		</tr>
	</table>
</div>