<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="ethConfigCwpStyle">
	<s:if test="%{dataSource.deviceInfo.apEthernetLess_2}">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ethCwp.label" />','ethcwpSettings');</script></td>
		</tr>
		<tr>
			<td>
			<div id="ethcwpSettings"
				style="display: <s:property value="%{dataSource.ethCwpSettingDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td style="padding-left: 8px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><s:checkbox name="dataSource.ethCwpEnableEthCwp"
								onclick="changeEnableEthCwp(this.checked)"></s:checkbox></td>
							<td width="230px" class="labelT1"
								style="padding-left: 0px;"><label><s:text
								name="hiveAp.ethCwp.enable.cwp" /></label></td>
							<td id="ethCwpProfileTd"
								style="display: <s:property value="%{dataSource.ethCwpEnableEthCwp?'':'none'}" />">
							<s:select name="cwpProfile" list="%{cwps}" listKey="id"
								listValue="value" onchange="cwpProfileChanged();"
								cssStyle="width: 200px; font-size:100%"></s:select> <s:if
								test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
							</s:if> <s:else>
								<a class="marginBtn"
									href="javascript:submitAction('newEthCwpCwpProfile')"><img
									class="dinl" src="<s:url value="/images/new.png" />"
									width="16" height="16" alt="New" title="New" /></a>
							</s:else> <s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
									src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
							</s:if> <s:else>
								<a class="marginBtn"
									href="javascript:submitAction('editEthCwpCwpProfile')"><img
									class="dinl" src="<s:url value="/images/modify.png" />"
									width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else></td>
						</tr>
						<tr id="ethCwpMacAuthTr"
							style="display: <s:property value="%{ethCwpMacAuthSettingDisplayStyle}"/>">
							<td><s:checkbox name="dataSource.ethCwpEnableMacAuth"
								onclick="changeEnableEthMacAuth(this.checked)"></s:checkbox></td>
							<td class="labelT1" style="padding-left: 0px;"><label><s:text
								name="hiveAp.ethCwp.enable.macAuth" /></label></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr id="ethCwpRadiusContentSettingTr"
					style="display: <s:property value="%{ethCwpRadiusContentSettingDisplayStyle}"/>">
					<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr id="ethCwpRadiusSelectionTr"
							style="display: <s:property value="%{ethCwpRadiusSelectionDisplayStyle}"/>">
							<td style="padding-left: 18px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="230px"><s:text
										name="hiveAp.ethCwp.radius.profile" /></td>
									<td><s:select name="ethCwpRadiusClient"
										list="%{radiusClientProfiles}" listKey="id"
										listValue="value" cssStyle="width: 200px;"></s:select> <s:if
										test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
									</s:if> <s:else>
										<a class="marginBtn"
											href="javascript:submitAction('newEthCwpRadiusClient')"><img
											class="dinl" src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New" title="New" /></a>
									</s:else> <s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
									</s:if> <s:else>
										<a class="marginBtn"
											href="javascript:submitAction('editEthCwpRadiusClient')"><img
											class="dinl" src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text
										name="hiveAp.ethCwp.authMethod" /></td>
									<td><s:select name="dataSource.ethCwpAuthMethod"
										list="%{enumAuthMethod}" listKey="key" listValue="value"
										cssStyle="width: 200px;"></s:select></td>
								</tr>
								<tr>
									<td height="2px"></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="ethCwpUserProfileSelectionTr"
							style="display: <s:property value="%{ethCwpUserProfileSelectionDisplayStyle}"/>">
							<td>
							<fieldset><legend><s:text
								name="hiveAp.ethCwp.userprofile.tag" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr id="ethCwpUserProfileRegTr"
									style="display: <s:property value="%{ethCwpUserProfileRegDisplayStyle}"/>">
									<td>
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%">
										<tr>
											<td class="labelT1"><s:text
												name="hiveAp.ethCwp.default.reg.userprofile" /></td>
										</tr>
										<tr>
											<td style="padding-left: 66px;"><s:select
												name="ethDefaultRegUserprofile" list="%{userProfiles}"
												listKey="id" listValue="value"
												cssStyle="width: 200px;"></s:select> <s:if
												test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:submitAction('newEthCwpDefaultRegUserProfile')"><img
													class="dinl" src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
											</s:else> <s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:submitAction('editEthCwpDefaultRegUserProfile')"><img
													class="dinl"
													src="<s:url value="/images/modify.png" />" width="16"
													height="16" alt="Modify" title="Modify" /></a>
											</s:else></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="ethCwpUserProfileAuthTr"
									style="display: <s:property value="%{ethCwpUserProfileAuthDisplayStyle}"/>">
									<td>
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%">
										<tr id="ethCwpUserProfileAuthLabelTr"
											style="display: <s:property value="%{ethCwpUserProfileAuthLabelDisplayStyle}"/>">
											<td class="labelT1"><s:text
												name="hiveAp.ethCwp.default.auth.userprofile" /></td>
										</tr>
										<tr id="ethCwpUserProfileDefaultLabelTr"
											style="display: <s:property value="%{ethCwpUserProfileDefaultLabelDisplayStyle}"/>">
											<td class="labelT1"><s:text
												name="hiveAp.ethCwp.userprofile.default.label" /></td>
										</tr>
										<tr>
											<td style="padding-left: 66px;"><s:select
												name="ethDefaultAuthUserprofile"
												list="%{userProfiles}" listKey="id" listValue="value"
												cssStyle="width: 200px;"></s:select> <s:if
												test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:submitAction('newEthCwpDefaultAuthUserProfile')"><img
													class="dinl" src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
											</s:else> <s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
											</s:if> <s:else>
												<a class="marginBtn"
													href="javascript:submitAction('editEthCwpDefaultAuthUserProfile')"><img
													class="dinl"
													src="<s:url value="/images/modify.png" />" width="16"
													height="16" alt="Modify" title="Modify" /></a>
											</s:else></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="ethCwpUserProfilesTr"
									style="display: <s:property value="%{ethCwpUserProfilesDisplayStyle}"/>">
									<td>
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%">
										<tr>
											<td class="labelT1"><s:text
												name="hiveAp.ethCwp.userprofiles" /></td>
										</tr>
										<tr>
											<s:push value="%{ethUserprofileOptions}">
												<td style="padding-left: 66px;"><tiles:insertDefinition
													name="optionsTransfer" /></td>
											</s:push>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="ethCwpUserProifleParamsTr"
									style="display: <s:property value="%{ethCwpUserProifleParamsDisplayStyle}"/>">
									<td>
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%">
										<tr>
											<td style="padding-left: 8px;">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td><s:checkbox
														name="dataSource.ethCwpLimitUserProfiles"
														onclick="changeEnableEthLimitUserprofile(this.checked)"></s:checkbox></td>
													<td class="labelT1" style="padding-left: 0px;"><label><s:text
														name="hiveAp.ethCwp.userprofile.limit" /></label></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td id="ethLimitUserprofileContent"
												style="display: <s:property value="%{dataSource.ethCwpLimitUserProfiles?'':'none'}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td style="padding-left: 20px;">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td class="labelT1" width="215px"><s:text
																name="hiveAp.ethCwp.userprofile.action" /></td>
															<td><s:select
																name="dataSource.ethCwpDenyAction"
																list="%{enumDenyAction}" listKey="key"
																listValue="value"
																onchange="ethDenyActionChanged(this.value);"
																cssStyle="width: 112px;"></s:select></td>
														</tr>
														<tr>
															<td class="labelT1"><s:text
																name="hiveAp.ethCwp.userprofile.actionTime" /></td>
															<td><s:textfield
																name="dataSource.ethCwpActiveTime" maxlength="10"
																disabled="%{ethCwpActionTimeDisabled}" size="16"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />
															<s:text
																name="hiveAp.ethCwp.userprofile.actionTime.note"></s:text></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td style="padding-left: 25px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td><s:checkbox
																name="dataSource.ethCwpEnableStriction"></s:checkbox></td>
															<td class="labelT1" style="padding-left: 0px;"><label><s:text
																name="hiveAp.ethCwp.userprofile.deauthenticate" /></label></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
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