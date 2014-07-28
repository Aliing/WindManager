<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="ethConfigStyle">
	<s:if test="%{!dataSource.deviceInfo.cvgAsL3Vpn && !dataSource.deviceInfo.deviceTypeSwitch}">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethNetwork.label" />','networkSettings');</script></td>
		</tr>
		<tr>
			<td>
			<div id="networkSettings"
				style="display: <s:property value="%{dataSource.networkSettingsDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
			    <tr>
			    	<td colspan="2" class="noteInfo" style="padding-left: 20px;"><s:text name="hiveAp.interface.wanorder.note" /></td>
			    </tr>
				<s:if test="%{dataSource.deviceInfo.apEthernetLess_2}">
				<tr id="hiveApNetworkSettings"
					style="display: <s:property value="%{hiveApNetworkSettingsStyle}"/>"><td><table cellspacing="0" cellpadding="0" border="0" width="100%">

					<tr id="ethModeSettings">
						<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr id="ethSetupTr">
								<td class="labelT1" width="140px"><s:text
									name="hiveAp.ethernet.setup" /></td>
								<td><s:select name="dataSource.ethConfigType"
									list="%{enumEthernetSetups}" listKey="key"
									listValue="value" cssStyle="width: 200px;"
									onchange="ethSetupChanged();" /></td>
								<td></td>
								<td></td>
							</tr>
							<tr id="eth0ModeTr">
								<td class="labelT1" width="140px" id="eth0ModeLabelTd"><s:text
									name="hiveAp.ethernet.eth0.mode" /></td>
								<td width="160px"><s:select
									name="dataSource.eth0.operationMode"
									onchange="eth0OperationModeChange(this.value);"
									value="%{dataSource.eth0.operationMode}"
									list="%{enumEthOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 200px;" /></td>
							</tr>
							<tr id="eth1ModeTr">
								<td class="labelT1" width="140px" id="eth1ModeLabelTd"><s:text
									name="hiveAp.ethernet.eth1.mode" /></td>
								<td width="160px"><s:select
									name="dataSource.eth1.operationMode"
									onchange="eth1OperationModeChange(this.value);"
									value="%{dataSource.eth1.operationMode}"
									list="%{enumEthOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 200px;" /></td>
							</tr>
							<tr id="red0ModeTr">
								<td class="labelT1" width="140px"><s:text
									name="hiveAp.ethernet.red0.mode" /></td>
								<td width="160px"><s:select
									name="dataSource.red0.operationMode"
									onchange="red0OperationModeChange(this.value);"
									value="%{dataSource.red0.operationMode}"
									list="%{enumRedOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 200px;" /></td>
							</tr>
							<tr id="agg0ModeTr">
								<td class="labelT1" width="140px"><s:text
									name="hiveAp.ethernet.agg0.mode" /></td>
								<td width="160px"><s:select
									name="dataSource.agg0.operationMode"
									onchange="agg0OperationModeChange(this.value);"
									value="%{dataSource.agg0.operationMode}"
									list="%{enumRedOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 200px;" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="radioConfigTr" style="display: none;">
						<td>
						<table border="0" cellspacing="0" cellpadding="0"
							width="100%">
							<tr>
								<td class="labelT1" width="140px"><s:text
									name="hiveAp.radio.setup" /></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td class="labelT1" width="140px">wifi0</td>
								<td width="170px"><s:select
									name="dataSource.wifi0.operationMode"
									value="%{dataSource.wifi0.operationMode}"
									list="%{enumWifiOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 152px"
									onchange="operationModeChanged(this.value,'wifi0')" /></td>
								<td id="customizeWifi1Label" class="labelT1" width="140px"
									style="visibility: <s:property value="%{customizeWifi1Style}"/>">wifi1</td>
								<td id="customizeWifi1Selector"
									style="visibility: <s:property value="%{customizeWifi1Style}"/>">
								<s:select name="dataSource.wifi1.operationMode"
									value="%{dataSource.wifi1.operationMode}"
									list="%{enumWifiOperationMode}" listKey="key"
									listValue="value" cssStyle="width: 152px"
									onchange="operationModeChanged(this.value,'wifi1')" /></td>
							</tr>
						</table>
						</td>
					</tr>

					<tr id="macLearningSettings">
						<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td height="5px"></td>
							</tr>
							<tr id="eth0MacLearningTr"
								style="display: <s:property value="%{eth0MacLearningStyle}"/>">
								<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth0 MAC Learning -->
								<fieldset><legend><s:text
									name="hiveAp.ethernet.eth0.settings" /></legend>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<%-- <tr>
																<td class="headCheck"><s:checkbox name="dataSource.eth0.macLearningEnabled" onclick="eth0MacLearningEnabled(this.checked);"/></td>
																<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
															</tr> --%>
											<tr id="eth0MacLearningEnabledTr"<%--<style="display: <s:property value="%{eth0MacLearningEnabledStyle}"/>"--%>>
												<td></td>
												<td class="labelT1" style="padding-left: 0" width="127px"><s:text
													name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
												<td><s:textfield name="dataSource.eth0.idelTimeout"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													size="24" maxlength="4" /> <s:text
													name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-left: 4px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td height="5px"></td>
											</tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','eth0BridgeAdvSettings');</script></td>
											</tr>
											<tr>
												<td>
												<div id="eth0BridgeAdvSettings"
													style="display: <s:property value="%{dataSource.eth0BridgeAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr
														style="display: <s:property value="%{fullModeConfigStyle}"/>">
														<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td class="labelT1" width="80px"><label><s:text
																	name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																<td><s:select name="userProfileEth0"
																	cssStyle="width: 220px;" list="%{userProfiles}"
																	listKey="id" listValue="value" /></td>
																<td><s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('newUserProfileEth0')"><img
																		class="dinl"
																		src="<s:url value="/images/new.png" />" width="16"
																		height="16" alt="New" title="New" /></a>
																</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('editUserProfileEth0')"><img
																		class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td>
														<fieldset><legend><s:text
															name="hiveAp.ethernet.macLearning.mac.label" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<s:push value="%{eth0MacOptions}">
																	<td colspan="3"><tiles:insertDefinition
																		name="optionsTransfer" /></td>
																</s:push>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
												</table>
												</div>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</fieldset>
								</td>
							</tr>
							<tr id="eth1MacLearningTr"
								style="display: <s:property value="%{eth1MacLearningStyle}"/>">
								<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth1 MAC Learning -->
								<fieldset><legend><s:text
									name="hiveAp.ethernet.eth1.settings" /></legend>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<%-- <tr>
																<td class="headCheck"><s:checkbox name="dataSource.eth1.macLearningEnabled" onclick="eth1MacLearningEnabled(this.checked);"/></td>
																<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
															</tr> --%>
											<tr id="eth1MacLearningEnabledTr"<%--style="display: <s:property value="%{eth1MacLearningEnabledStyle}"/>" --%>>
												<td></td>
												<td class="labelT1" style="padding-left: 0" width="127px"><s:text
													name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
												<td><s:textfield name="dataSource.eth1.idelTimeout"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													size="24" maxlength="4" /> <s:text
													name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-left: 4px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td height="5px"></td>
											</tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','eth1BridgeAdvSettings');</script></td>
											</tr>
											<tr>
												<td>
												<div id="eth1BridgeAdvSettings"
													style="display: <s:property value="%{dataSource.eth1BridgeAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr
														style="display: <s:property value="%{fullModeConfigStyle}"/>">
														<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td class="labelT1" width="80px"><label><s:text
																	name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																<td><s:select name="userProfileEth1"
																	cssStyle="width: 220px;" list="%{userProfiles}"
																	listKey="id" listValue="value" /></td>
																<td><s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('newUserProfileEth1')"><img
																		class="dinl"
																		src="<s:url value="/images/new.png" />" width="16"
																		height="16" alt="New" title="New" /></a>
																</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('editUserProfileEth1')"><img
																		class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td>
														<fieldset><legend><s:text
															name="hiveAp.ethernet.macLearning.mac.label" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<s:push value="%{eth1MacOptions}">
																	<td colspan="3"><tiles:insertDefinition
																		name="optionsTransfer" /></td>
																</s:push>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
												</table>
												</div>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</fieldset>
								</td>
							</tr>
							<tr id="agg0MacLearningTr"
								style="display: <s:property value="%{agg0MacLearningStyle}"/>">
								<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Agg0 MAC Learning -->
								<fieldset><legend><s:text
									name="hiveAp.ethernet.agg0.settings" /></legend>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<%--<tr>
																<td class="headCheck"><s:checkbox name="dataSource.agg0.macLearningEnabled" onclick="agg0MacLearningEnabled(this.checked);"/></td>
																<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
															</tr>--%>
											<tr id="agg0MacLearningEnabledTr"<%--style="display: <s:property value="%{agg0MacLearningEnabledStyle}"/>"--%>>
												<td></td>
												<td class="labelT1" style="padding-left: 0" width="127px"><s:text
													name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
												<td><s:textfield name="dataSource.agg0.idelTimeout"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													size="24" maxlength="4" /> <s:text
													name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-left: 4px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td height="5px"></td>
											</tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','agg0BridgeAdvSettings');</script></td>
											</tr>
											<tr>
												<td>
												<div id="agg0BridgeAdvSettings"
													style="display: <s:property value="%{dataSource.agg0BridgeAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr
														style="display: <s:property value="%{fullModeConfigStyle}"/>">
														<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td class="labelT1" width="80px"><label><s:text
																	name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																<td><s:select name="userProfileAgg0"
																	cssStyle="width: 220px;" list="%{userProfiles}"
																	listKey="id" listValue="value" /></td>
																<td><s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('newUserProfileAgg0')"><img
																		class="dinl"
																		src="<s:url value="/images/new.png" />" width="16"
																		height="16" alt="New" title="New" /></a>
																</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('editUserProfileAgg0')"><img
																		class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td>
														<fieldset><legend><s:text
															name="hiveAp.ethernet.macLearning.mac.label" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<s:push value="%{agg0MacOptions}">
																	<td colspan="3"><tiles:insertDefinition
																		name="optionsTransfer" /></td>
																</s:push>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
												</table>
												</div>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</fieldset>
								</td>
							</tr>
							<tr id="red0MacLearningTr"
								style="display: <s:property value="%{red0MacLearningStyle}"/>">
								<td style="padding-left: 10px; padding-bottom: 10px;"><!-- Eth0 MAC Learning -->
								<fieldset><legend><s:text
									name="hiveAp.ethernet.red0.settings" /></legend>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<%--<tr>
																<td class="headCheck"><s:checkbox name="dataSource.red0.macLearningEnabled" onclick="red0MacLearningEnabled(this.checked);"/></td>
																<td class="labelT1" style="padding-left:0" colspan="2"><s:text name="hiveAp.ethernet.macLearning.label" /></td>
															</tr>--%>
											<tr id="red0MacLearningEnabledTr"<%--style="display: <s:property value="%{red0MacLearningEnabledStyle}"/>"--%>>
												<td></td>
												<td class="labelT1" style="padding-left: 0" width="127px"><s:text
													name="hiveAp.ethernet.macLearning.idelTimeout" /></td>
												<td><s:textfield name="dataSource.red0.idelTimeout"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													size="24" maxlength="4" /> <s:text
													name="hiveAp.ethernet.macLearning.idelTimeout.range" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-left: 4px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td height="5px"></td>
											</tr>
											<tr>
												<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.macLearning.advance" />','red0BridgeAdvSettings');</script></td>
											</tr>
											<tr>
												<td>
												<div id="red0BridgeAdvSettings"
													style="display: <s:property value="%{dataSource.red0BridgeAdvDisplayStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr
														style="display: <s:property value="%{fullModeConfigStyle}"/>">
														<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<td class="labelT1" width="80px"><label><s:text
																	name="hiveAp.ethernet.macLearning.userProfile" /></label></td>
																<td><s:select name="userProfileRed0"
																	cssStyle="width: 220px;" list="%{userProfiles}"
																	listKey="id" listValue="value" /></td>
																<td><s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('newUserProfileRed0')"><img
																		class="dinl"
																		src="<s:url value="/images/new.png" />" width="16"
																		height="16" alt="New" title="New" /></a>
																</s:else> <s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																</s:if> <s:else>
																	<a class="marginBtn"
																		href="javascript:submitAction('editUserProfileRed0')"><img
																		class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td>
														<fieldset><legend><s:text
															name="hiveAp.ethernet.macLearning.mac.label" /></legend>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="5px"></td>
															</tr>
															<tr>
																<s:push value="%{red0MacOptions}">
																	<td colspan="3"><tiles:insertDefinition
																		name="optionsTransfer" /></td>
																</s:push>
															</tr>
														</table>
														</fieldset>
														</td>
													</tr>
												</table>
												</div>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</fieldset>
								</td>
							</tr>
						</table></td>
					</tr>
					<tr id="ethAdvanceSettings">
						<td style="padding-left: 10px;">
						<table border="0" cellspacing="0" cellpadding="0"
							width="100%">
							<tr id="ethAdvSettingsTr">
								<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethernet.adv.label" />','ethAdvSettings');</script></td>
							</tr>
							<tr>
								<td>
								<div id="ethAdvSettings"
									style="display: <s:property value="%{dataSource.advEthSettingsDisplayStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0"
									width="100%">
									<tr>
										<td>
										<fieldset>
										<table cellspacing="0" cellpadding="0" border="0"
											width="100%" class="embedded">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<th align="left" width="50px"><s:text
													name="hiveAp.lanIf" /></th>
												<th align="left" width="70px"><s:text
													name="hiveAp.if.adminState" /></th>
												<th align="left" width="90px"><s:text
													name="hiveAp.ethernet.multiple.native.vlan" />
												<br>
												<s:text name="hiveAp.ethernet.multiple.native.vlan.range"/></th>
												<th align="left" width="80px"><s:text
													name="hiveAp.if.allowedVlan" /></th>
												<th align="left" width="110px"><s:text
													name="hiveAp.if.duplex" /></th>
												<th align="left" width="90px"><s:text
													name="hiveAp.if.speed" /></th>
												<th id="eth0RoleHeader" align="left" nowrap="nowrap"
													style="display: <s:property value="eth1StuffStyle"/>"><s:text
													name="hiveAp.if.bindRole" /></th>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
											<tr id="eth0Row">
												<td class="list"><s:text name="hiveAp.if.eth0" /></td>
												<td class="list"><s:select
													name="dataSource.eth0.adminState"
													value="%{dataSource.eth0.adminState}"
													list="%{enumAdminStateType}" listKey="key"
													listValue="value" /></td>
												<td class="list"><s:textfield
													name="dataSource.eth0.multiNativeVlan"  size="8"
													maxlength="4" title="%{multiNativeVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');"/></td>
												<td class="list"><s:textfield
													name="dataSource.eth0.allowedVlan" size="8"
													maxlength="255" title="%{allowedVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
												<td class="list"><s:select
													name="dataSource.eth0.duplex"
													value="%{dataSource.eth0.duplex}"
													list="%{enumDuplexType}" listKey="key"
													listValue="value" cssStyle="width: 106px;" /></td>
												<td class="list"><s:select
													name="dataSource.eth0.speed"
													value="%{dataSource.eth0.speed}"
													list="%{enumSpeedType}" listKey="key"
													listValue="value" cssStyle="width: 84px;"
													onchange="changeSpeed(this)" /></td>
												<td id="eth0RoleColumn" class="list"
													style="display: <s:property value="eth1StuffStyle"/>"><s:select
													name="dataSource.eth0.bindRole"
													value="%{dataSource.eth0.bindRole}"
													list="%{enumBindRole}" listKey="key" listValue="value"
													cssStyle="width: 80px;" /></td>
											</tr>
											<%-- add row 'eth1', 'red0', 'agg0' --%>
											<tr id="eth1Row"
												style="display: <s:property value="eth1StuffStyle"/>">
												<td class="list"><s:text name="hiveAp.if.eth1" /></td>
												<td class="list"><s:select
													name="dataSource.eth1.adminState"
													value="%{dataSource.eth1.adminState}"
													list="%{enumAdminStateType}" listKey="key"
													listValue="value" /></td>
												<td class="list"><s:textfield
													name="dataSource.eth1.multiNativeVlan"  size="8"
													maxlength="4" title="%{multiNativeVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');"/></td>
												<td class="list"><s:textfield
													name="dataSource.eth1.allowedVlan" size="8"
													maxlength="255" title="%{allowedVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
												<td class="list"><s:select
													name="dataSource.eth1.duplex"
													value="%{dataSource.eth1.duplex}"
													list="%{enumDuplexType}" listKey="key"
													listValue="value" cssStyle="width: 106px;" /></td>
												<td class="list"><s:select
													name="dataSource.eth1.speed"
													value="%{dataSource.eth1.speed}"
													list="%{enumSpeedType}" listKey="key"
													listValue="value" cssStyle="width: 84px;" /></td>
												<td id="eth1RoleColumn" class="list"><s:select
													name="dataSource.eth1.bindRole"
													value="%{dataSource.eth1.bindRole}"
													list="%{enumBindRole}" listKey="key" listValue="value"
													cssStyle="width: 80px;" /></td>
											</tr>
											<tr id="red0Row"
												style="display: <s:property value="eth1StuffStyle"/>">
												<td class="list"><s:text name="hiveAp.if.red0" /></td>
												<td class="list"><s:select
													name="dataSource.red0.adminState"
													value="%{dataSource.red0.adminState}"
													list="%{enumAdminStateType}" listKey="key"
													listValue="value" /></td>
												<td class="list"><s:textfield
												name="dataSource.red0.multiNativeVlan"  size="8"
												maxlength="4"/></td>

												<td class="list"><s:textfield
													name="dataSource.red0.allowedVlan" size="8"
													maxlength="255" title="%{allowedVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
												<td class="list" colspan="6">&nbsp;</td>
											</tr>
											<tr id="agg0Row"
												style="display: <s:property value="eth1StuffStyle"/>">
												<td class="list"><s:text name="hiveAp.if.agg0" /></td>
												<td class="list"><s:select
													name="dataSource.agg0.adminState"
													value="%{dataSource.agg0.adminState}"
													list="%{enumAdminStateType}" listKey="key"
													listValue="value" /></td>
												<td class="list"><s:textfield
												name="dataSource.agg0.multiNativeVlan"  size="8"
												maxlength="4"/></td>
												<td class="list"><s:textfield
													name="dataSource.agg0.allowedVlan" size="8"
													maxlength="255" title="%{allowedVlanTitle}"
													onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
												<td class="list" colspan="6">&nbsp;</td>
											</tr>
										</table>
										</fieldset>
										</td>
									</tr>
								</table>
								</div>
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table></td>
				</tr>
				</s:if>

				<s:if test="%{dataSource.deviceInfo.routerOrEthernetMore_2}">
				<tr>
					<td>
					<div id="routerNetworkSetting">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr id="radioConfigBR">
							<td class="labelT1">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<!-- tr>
									<td>
										<fieldset>
											<legend><s:text name="hiveAp.radio.setup" /></legend>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" width="80px">wifi0</td>
													<td width="270px"><s:select
														name="wifi0ModeBR"
														id="wifi0ModeBR"
														value="%{dataSource.wifi0.operationMode}"
														list="%{enumWifiOperationMode}" listKey="key"
														listValue="value" cssStyle="width: 152px"
														onchange="operationModeChanged(this.value,'wifi0')" /></td>
													<td id="customizeWifi1LabelBR" class="labelT1" width="80px"
														style="visibility: <s:property value="%{customizeWifi1Style}"/>">wifi1</td>
													<td id="customizeWifi1SelectorBR"
														style="visibility: <s:property value="%{customizeWifi1Style}"/>">
													<s:select name="wifi1ModeBR"
														id="wifi1ModeBR"
														value="%{dataSource.wifi1.operationMode}"
														list="%{enumWifiOperationMode}" listKey="key"
														listValue="value" cssStyle="width: 152px"
														onchange="operationModeChanged(this.value,'wifi1')" /></td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr -->
							</table>
							</td>
						</tr>
						<tr id="devicePortSettings">
							<td class="labelT1" style="padding: 0 0 5px 10px">
							<fieldset>
							<legend><s:text name="hiveAp.brRouter.port.settings" /></legend>
							<table id="interfaceEthSettingTable" cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
								<tr>
									<td height="10"></td>
								</tr>
								<tr id="br_head_setting">
									<th align="left" width="30px"><s:text
											name="hiveAp.brRouter.port.settings.port" /></th>
									<th align="left" width="30px"><s:text
											name="hiveAp.brRouter.port.settings.role" /></th>
									<th id="head_priority" align="left" width="50px"><s:text
											name="hiveAp.brRouter.port.settings.priority" /></th>
									<th id="head_enableNat" align="left" width="40px"><s:text
											name="hiveAp.brRouter.port.settings.enableNat" /></th>
									<th id="head_disablePortforwarding" align="left" width="70px"><s:text
											name="hiveAp.brRouter.port.settings.disablePortForwarding" /></th>
									<th id="head_connectionType" align="left" width="70px"><s:text
											name="hiveAp.brRouter.port.settings.connectionType" /></th>
									<th id="head_staticIp" align="left" width="75px">
									<table>
										<tr><td><s:text name="hiveAp.brRouter.port.settings.staticIp" /></td></tr>
					                    <tr><td><s:text name="hiveAp.brRouter.port.settings.netmask" /></td></tr>			
									</table> 
									</th>
									<th id="head_defaultGateway" align="left" width="75px"><s:text
											name="hiveAp.brRouter.port.settings.defaultGateway" /></th>
									<th id="head_pppoeAuth" align="left" width="140px"><s:text
											name="hiveAp.brRouter.port.settings.pppoeAuth" /></th>
								</tr>
								
							<s:hidden name="wanCount" value="%{switchWanPortSettings.size}"/>
							<s:hidden name="havePPPoE" value="%{havePPPoE}"/>
						   <tr>
		                       <td height="5"  colspan="10">
		                         <table>
			                        <tr><td id="error_messageId" colspan="10"></td></tr>
	                             </table>
		                      </td>
		                    </tr> 
						 	<s:iterator  value="switchWanPortSettings" status="status" id="item"> 
						 	<tr>
			                      <td height="5"  colspan="10">
			                        <table>
				                      <tr>
					                      <td id="error_message_<s:property value="%{#status.index}" />" colspan="10"></td>
				                      </tr>
			                       </table>
			                      </td>
		                    </tr> 
								<tr id="switch<s:property value="%{#item.deviceIfType}" />" class="<s:property value="%{#rowClass}"/>">
								 	<td class="list"><s:property  value="%{#item.interfaceName}"/> 
								 	<s:hidden id="hide_interfaceName_%{#status.index}" name="switchWanPortSettings[%{#status.index}].interfaceName" value="%{#item.interfaceName}"/>
 									</td> 
 									<td class="list"> 
										<s:text name="hiveAp.vpnGateway.if.wan" /> 
										<s:hidden name="switchWanPortSettings[%{#status.index}].deviceIfType" value="%{#item.deviceIfType}"></s:hidden>
 									</td> 
									<td class="list">
									<s:if test="enumPrioritySwitchType !=null">
									<s:select name="switchWanPortSettings[%{#status.index}].wanOrder" onchange="selectOrderTrigger(this);"
											id="hiveAp_wansort_%{#status.index}" value="%{#item.wanOrder}" cssStyle="width: 80px; "
											list="%{enumPrioritySwitchType}" listKey="key" listValue="value" />
									<s:hidden name="hidden_wan_%{#status.index}" value="%{#item.wanOrder}"/>
 									</s:if></td> 
									<td  class="list"><s:checkbox name="switchWanPortSettings[%{#status.index}].enableNat" value="%{#item.enableNat}"/>
 											</td> 
									<td  class="list"><s:checkbox name="switchWanPortSettings[%{#status.index}].disablePortForwarding" value="%{#item.disablePortForwarding}"/>
 											</td> 
 									<td class="list" > 
 									<s:if test="!#item.portUSB">
									<s:select name="switchWanPortSettings[%{#status.index}].connectionType" value="%{#item.connectionType}" cssStyle="width: 80px; "
									 id="hiveAp_connectionType_%{#status.index}" list="%{enumConnectionType1}" listValue="value" listKey="key" onchange="changeConnectionForSwitchRouter(this);"/>
 									</s:if></td> 
 									<td class="list" > 
									<div id="ipAddressDiv_<s:property value="%{#status.index}"/>" 
									         style="display:<s:property value='%{switchWanPortSettings[#status.index].staticIpFlag}'/>">
										<s:textfield name="switchWanPortSettings[%{#status.index}].ipAndNetmask" size="11" id="switch_ipAddress_%{#status.index}"
										         onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/>
 									</div> 
 									</td> 
									<td class="list" > 
										<div id="defaultGateway_<s:property value="%{#status.index}"/>" 
										   style="display:<s:property value='%{switchWanPortSettings[#status.index].staticIpFlag}'/>">
											<s:textfield name="switchWanPortSettings[%{#status.index}].gateway" size="11" id="switch_gateway_%{#status.index}"
											      onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/>
 										</div> 
 									</td> 
							
 									<td  class="list"> 
									<div id="pppoeAuthrouter_<s:property value="%{#status.index}"/>" 
									       style="display:<s:property value='%{#item.pPPoEFlag}'/>">
										<s:select list="%{pppoeAuthProfiles}" listKey="id" listValue="value" name="switchWanPortSettings[%{#status.index}].pppoeID"
										value="%{pppoeAuthProfile}"	id="hiveAp_pppoeAuthProfile_%{#status.index}"  cssStyle="width: 60px;" /> 
 											<a class="marginBtn" href="javascript:submitAction('newPPPoE')"><img 
												class="dinl" src="<s:url value="/images/new.png" />" width="16"
												height="16" alt="New" title="New" /></a> 
										
 											<a class="marginBtn" href="javascript:submitAction('editPPPoE')"><img 
												class="dinl" src="<s:url value="/images/modify.png" />" width="16"
 												height="16" alt="Modify" title="Modify" /></a> 
										
 										</div> 
 									</td> 
								</tr> 
								</s:iterator>  
								



							</table>

							<s:if test="%{dataSource.deviceInfo.onlyRouterFunc}">
							<div id="routingPolicySettings"><table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td height="10px"/>
								</tr>
								<tr>
									<td>
										<s:checkbox name="enabledOverrideRoutingPolicy" onclick="enabledOverrideRoutingPolicyFc(this.checked);"/>
										<s:text name="hiveAp.routingPolicy.override"/>
									</td>
								</tr>
								<tr style="display:<s:property value='routingPolicyDetailDivStyle'/>" id="routingPolicyDetailDiv">
									<td style="padding-left: 30px">
									  <table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="120px"><s:text name="config.networkpolicy.routingPolicy.title"/> </td>
												<td width="200px"><s:select
													name="routingPolicyId" list="%{list_routingPolicy}" listKey="id"
													listValue="value" cssStyle="width: 140px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newRoutingPbrPolicy')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editRoutingPbrPolicy')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table></div>
							</s:if>
 <!-- 
							<s:if test="%{dataSource.deviceInfo.containsRouterFunc}">
							<div id="voipSettings">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td height="10px"/>
								</tr>
								<tr>
									<td> <s:checkbox name="dataSource.enabledOverrideVoipSetting" onclick="enabledOverrideVoip(this.checked);"/>
										<s:text name="hiveAp.brRouter.port.settings.voipTitle"/></td>
								</tr>
								<tr id="voipDetailDiv" style="display:<s:property value='voipDetailDivStyle'/>">
									<td style="padding-left: 15px">
										<table cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
											<tr>
												<th align="left" class="noUnderline" width="50px">&nbsp;</th>
												<th align="left" class="noUnderline" width="230px" colspan="2"><s:text
													name="hiveAp.brRouter.port.settings.voipLimitDn" /></th>
												<th align="left" class="noUnderline" width="230px" colspan="2"><s:text
													name="hiveAp.brRouter.port.settings.voipLimitUp" /></th>
											</tr>
											<tr>
												<th align="left" width="50px"><s:text
													name="hiveAp.brRouter.port.settings.port" /></th>
												<th align="left" width="80px" ><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
												<th align="left" width="230px"><s:text name="hiveAp.brRouter.port.settings.voipMaxRate"/></th>
												<th align="left" width="80px" ><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
												<th align="left" width="230px"><s:text name="hiveAp.brRouter.port.settings.voipMaxRate"/></th>
											</tr>
											<s:iterator  value="switchWanPortSettings" status="status" id="item">
												<tr>
												<td class="list"><s:property  value="%{#item.interfaceName}"/></td>
												<td class="list"><s:checkbox name="switchWanPortSettings[%{#status.index}].enableMaxDownload"  onclick="enabledVoipCheckBox(this.checked, '_maxDownload_%{#status.index}')"></s:checkbox></td>
												<td class="list"><s:textfield id="hiveAp_maxDownload_%{#status.index}" name="switchWanPortSettings[%{#status.index}].maxDownload" size="5" maxlength="5"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" 
														readonly="%{!#item.enableMaxDownload}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
												<td class="list"><s:checkbox name="switchWanPortSettings[%{#status.index}].enableMaxUpload" onclick="enabledVoipCheckBox(this.checked, '_maxUpload_%{#status.index}')"></s:checkbox></td>
												<td class="list"><s:textfield id="hiveAp_maxUpload_%{#status.index}" name="switchWanPortSettings[%{#status.index}].maxUpload" size="5" maxlength="5"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" 
														readonly="%{!#item.enableMaxUpload}"/>&nbsp;<s:text name="hiveAp.brRouter.port.settings.voipLimitRange"/></td>
											</tr>
											</s:iterator>
										
										</table>
									</td>
								</tr>
							</table>
							</div>
							</s:if>
							 -->
							<s:if test="%{dataSource.deviceInfo.isSupportAttribute(@com.ah.bo.hiveap.DeviceInfo@SPT_PSE)}">
							<div id="pseSettingAllDiv">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.brRouter.pse.settings" />','pseSettingDiv');</script></td>
								</tr>
								<tr>
									<td>
										<div id="pseSettingDiv" style="display: <s:property value="%{dataSource.pseSettingsDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr><td height="10px"/>
												</tr>
												<tr>
													<td valign="top" style="padding-left: 15px">
														<table cellspacing="0" cellpadding="0" border="0" width="100%"  class="embedded">
															<tr>
																<th align="left" width="50px"><s:text name="hiveAp.brRouter.port.settings.port" /></th>
																<th align="left" width="80px"><s:text name="hiveAp.brRouter.port.settings.voipEnabled"/></th>
																<th align="left" width="160px"><s:text name="hiveAp.brRouter.pse.settings.mode"/></th>
																<th align="left" width="150px"><s:text name="hiveAp.brRouter.pse.settings.priority"/></th>
															</tr>
															<tr>
																<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth1"/></td>
																<td class="list"><s:checkbox name="branchRouterEth1.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth1PseState')"></s:checkbox></td>
																<td class="list"><s:select name="branchRouterEth1.pseState" id="eth1PseState" cssStyle="width: 130px;"
																		value="%{branchRouterEth1.pseState}" list="%{enumPseType}" listKey="key"
																		listValue="value" disabled="branchRouterEth1.disabledPseState"/></td>
																<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																	list="#{'0':''}" value="%{radioPsePriority}" />&nbsp;</td>
															</tr>
															<tr>
																<td class="list"><s:text name="hiveAp.autoProvisioning.br100.if.port.eth2"/></td>
																<td class="list"><s:checkbox name="branchRouterEth2.pseEnabled"  onclick="enabledPseCheckBox(this.checked, 'eth2PseState')"></s:checkbox></td>
																<td class="list"><s:select name="branchRouterEth2.pseState" id="eth2PseState" cssStyle="width: 130px;"
																		value="%{branchRouterEth2.pseState}" list="%{enumPseType}" listKey="key"
																		listValue="value" disabled="branchRouterEth2.disabledPseState"/></td>
																<td class="list" align="center"><s:radio label="Gender" name="radioPsePriority"
																	list="#{'1':''}" value="%{radioPsePriority}" />&nbsp;</td>
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
							</s:if>

							</fieldset>
							</td>
						</tr>

						<s:if test="%{dataSource.deviceInfo.onlyRouterFunc}">
						<tr id="usbSettings">
							<td><table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td style="padding-left: 10px">
										<fieldset>
											<legend><s:text name="hiveAp.brRouter.usb.modem.settings" /></legend>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td height="10"></td>
												</tr>
												<tr id= "usbConnectionModelTr" <s:if test="usbPriorityPrimary">style="display: none;"</s:if> >
													<td width="120"></td>
													<td><s:radio label="Gender"
															name="dataSource.usbConnectionModel" list="%{usbConnectNeeded}"
															listKey="key" listValue="value"
															onclick="this.blur();" /></td>
													<td width="50"></td>
													<td><s:radio label="Gender"
															name="dataSource.usbConnectionModel" list="%{usbConnectAlways}"
															listKey="key" listValue="value"
															onclick="this.blur();" /></td>
													<td width="120"></td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td colspan="10"><table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
														<!--
															<th align="left" style="padding-left: 0;" width="30"><input
																type="checkbox" id="checkAll"
																onClick="toggleCheckAllDynamicRoutes(this);"></th>
														 -->
															<th align="left" width="180"><s:text
																name="hiveAp.brRouter.usb.modemProfile.modem" /></th>
															<th align="left" width="80"><s:text
																name="hiveAp.brRouter.usb.modemProfile.apn" /></th>
															<th align="left" width="80" nowrap="nowrap" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																name="hiveAp.brRouter.usb.modemProfile.dialup" /></th>
															<th align="left" width="120" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																name="hiveAp.brRouter.usb.modemProfile.userId" /></th>
															<th align="left" width="100" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																name="hiveAp.brRouter.usb.modemProfile.password" /></th>
															<th align="left" width="90" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:text
																name="hiveAp.brRouter.usb.obscure.password" /></th>
															<th align="left" width="100" style="display:<s:property value='modemUsb4Br200LteStyle'/>"><s:text
																name="hiveAp.brRouter.usb.modemProfile.cellularMode"/></th>
														</tr>
														<s:iterator value="%{dataSource.usbModemList}"
															status="status">
															<tr>
															<!--
																<td class="listCheck"><s:checkbox
																	name="usbConnectionIndices"
																	fieldValue="%{#status.index}" /></td>
															 -->
																<td class="list"><s:property value="displayName"/></td>
																<td style="display:none"><s:textfield name="modemName" value="%{modemName}" /></td>
																<td style="display:none"><s:textfield name="displayName" value="%{displayName}" /></td>
																<td class="list"><s:textfield name="apn" value="%{apn}" size="10" /></td>
																<td class="list" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:textfield name="dialupNum" value="%{dialupNum}" size="8" /></td>
																<td class="list" style="display:<s:property value='modemUsb2OtherStyle'/>"><s:textfield name="userId" value="%{userId}" size="16" /></td>
																<td class="list" colspan="2" style="display:<s:property value='modemUsb2OtherStyle'/>">
																	<s:password name="password" value="%{password}" showPassword="true" size="10" 
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	<s:textfield name="password_text" value="%{password}" size="10" disabled="true" cssStyle="display: none" 
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	<s:checkbox name="ignore" cssStyle="margin-left: 20px" value="true" onclick="hm.util.toggleObscurePasswordList(this, 'password', 'password_text');" disabled="%{writeDisable4Struts}"/>
																</td>
																<td class="list" style="display:<s:property value='modemUsb4Br200LteStyle'/>"><s:select name="cellularMode" list="%{enumCellularMode}" value="%{cellularMode}" listKey="key" listValue="value" /></td>
															</tr>
														</s:iterator>
														<!--
														<s:if test="%{usbGridCount > 0}">
															<s:generator separator="," val="%{' '}"
																count="%{gridCount}">
																<s:iterator>
																	<tr>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
														</s:if>
														 -->
													</table></td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table></td>
						</tr>
						</s:if>
					</table>
					</div>
					</td>
				</tr>
				</s:if>
			</table>
			</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>