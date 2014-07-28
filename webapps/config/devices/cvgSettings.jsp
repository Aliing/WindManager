<%@taglib prefix="s" uri="/struts-tags"%>
<div>
	<s:if test="%{dataSource.vpnGateway}">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td height="10px"/>
	</tr>
	<tr><td><fieldset>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr id="cvgWanIntSettings">
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td style="padding-left: 8px;"><table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr id="cvgPortSettingHead">
								<th align="left" width="50px"><s:text
										name="hiveAp.cfg.staticRoute.interface" /></th>
								<th align="left" width="200px"><s:text
										name="hiveAp.ipAddress.cvg" /></th>
								<th align="left" width="80px"><s:text
										name="hiveAp.gateway" /></th>
								<th id="cvgHeadAdmin" align="left" width="50px"><s:text
										name="hiveAp.if.adminState" /></th>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
							<tr id="cvgWanPortSettings">
								<td class="list"><s:text name="hiveAp.vpnGateway.if.wan.display"/><font
									color="red"><s:text name="*" /></font></td>
								<td class="list"><s:textfield name="wanInterface.ipAndNetmask" size="18" maxlength="18"
									onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/>
									<s:text name="config.vpn.subnet.ipnetwork.range" /></td>
								<td class="list"><s:textfield name="wanInterface.gateway" size="18" maxlength="15"
									onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
								<td class="list" id="cvgWanAdmin"><s:select
										name="wanInterface.adminState"
										value="%{wanInterface.adminState}"
										list="%{enumAdminStateType}" listKey="key" listValue="value"
										onchange="" /></td>
							</tr>
							<s:if test="%{dataSource.deviceInfo.cvgAsL3Vpn}">
							<tr>
								<td height="5"></td>
							</tr>
							<tr id="cvgLanPortSettings" style="display:<s:property value="%{cVGLanPortStyle}" />">
								<td class="list"><s:text name="hiveAp.vpnGateway.if.lan.display"/></td>
								<td class="list"><s:textfield name="lanInterface.ipAndNetmask" size="18" maxlength="18"
									onkeypress="return hm.util.keyPressPermit(event,'ipMask');"/></td>
								<td class="list">&nbsp;</td>
								<td class="list" id="cvgLanAdmin"><s:select
										name="lanInterface.adminState"
										value="%{lanInterface.adminState}"
										list="%{enumAdminStateType}" listKey="key" listValue="value"
										onchange="" /></td>
							</tr>
							</s:if>
						</table></td>
					</tr>
				</table>
			</td>
		</tr>

		<!-- RoutingProfiles start -->
		<s:if test="%{dataSource.deviceInfo.isSupportAttribute(@com.ah.bo.hiveap.DeviceInfo@SPT_DYNAMIC_ROUTING)}">
		<tr id="dynamicRoutingSettings">
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td>
										<s:checkbox name="dataSource.routingProfile.enableDynamicRouting" id="enableDynamicRouting"
											onclick="selectEnableDynamicRouting(this.checked);" />
										<label>
											<s:text name="hiveAp.vpnGateway.routing.profile.title" />
										</label>
									</td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								<tr style="display:<s:property value="enableDynamicRoutingStyle"/>" id="dynamicRoutingSection">
									<td style="padding-left:20px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr id="dynamicRoutingNote"/>													
										<tr><td height="5px"></td></tr>
										<tr>
											<td>
												<s:select id="typeFlag" cssStyle="width:100px"  name="dataSource.routingProfile.typeFlag" list="dynamicRoutingList" value="defaultDynamicRouting"
													listKey="id" listValue="value" onchange="showDynamicRoutingContent();"></s:select>
											</td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td><fieldset>
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1"><label><s:text name="hiveAp.vpnGateway.routing.profile.route.advertisement" /></label></td>
																	<td style="padding-left: 50px;">
																		<s:checkbox name="dataSource.routingProfile.enableRouteWan" id="enableRouteWan"
																			value="%{dataSource.routingProfile.enableRouteWan}" >
																		</s:checkbox>
																			<label>
																				<s:text name="hiveAp.vpnGateway.if.wan.display" />
																			</label>
																	</td>
																	<td style="padding-left: 80px; display:<s:property value="%{cVGLanPortStyle}"/>;">
																		<s:checkbox name="dataSource.routingProfile.enableRouteLan" id="enableRouteLan"
																			value="%{dataSource.routingProfile == null || dataSource.routingProfile.enableRouteLan ? true : fasle }" >
																		</s:checkbox>
																			<label>
																				<s:text name="hiveAp.vpnGateway.if.lan.display" />
																			</label>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr style="padding-left: 25px">
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																<td class="labelT1">
																	<table border="0" cellspacing="0" cellpadding="0" width="100%">
																		<tr>
																			<td >
																				<s:checkbox name="dataSource.routingProfile.useMD5" id="useMD5" onclick="selectUseMD5(this.checked);" />
																				<label><s:text name="config.routingProfiles.useMD5" /></label>
																			</td>
																		</tr>
																	</table>

																</td>
																<td class="labelT1">
																	<table border="0" cellspacing="0" cellpadding="0" width="100%">
																		<tr style="display:<s:property value="useMD5Style" />" id="useMD5Section">
																			<td style="padding-left: 36px;">
																				<s:text name="config.routingProfiles.password" /><font color="red"><s:text name="*"/></font>&nbsp;&nbsp;
																			</td>
																			<td>
																				<s:password name="dataSource.routingProfile.password" id="md5Password"
																					size="24" maxlength="32" onkeypress="return hm.util.keyPressPermit(event,'password');"
																					showPassword="true"/>
																				<s:textfield name="dataSource.routingProfile.password" id="md5Password_text"
																					size="24" maxlength="32" cssStyle="display:none;"  disabled="true"
																					onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																			</td>
																			<td>
																				<s:checkbox id="chkToggleDisplay_md5"
																					name="ignore" value="true"
																					onclick="hm.util.toggleObscurePassword(this.checked,
																						['md5Password'],
																						['md5Password_text']);" />
																			</td>
																			<td>
																				<s:text name="admin.user.obscurePassword" />
																			</td>
																		</tr>
																	</table>
																</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr style="padding-left: 25px;display:<s:property value="bgpStyle"/>" id="bgpSection">
														<td colspan="2" style="padding-top: 0px">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="200"><label><s:text
																		name="config.routingProfiles.autoSysNm" /><font color="red"><s:text name="*"/></font>
																		</label></td>
																	<td><s:textfield size="24" name="dataSource.routingProfile.autonmousSysNm" id="autonmousSysNm" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text
																		name="config.routingProfiles.autoSysNm.range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="200"><label><s:text
																		name="config.routingProfiles.keepalive" />
																		</label></td>
																	<td><s:textfield size="24" name="keepalive" id="keepalive" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text
																		name="config.routingProfiles.keepalive.range" /></td>
																</tr>
																<tr>
																	<td class="labelT1" width="200"><label><s:text
																		name="config.routingProfiles.bgpRouterID" />
																		</label></td>
																	<td><s:textfield size="24" name="bgpRouterId" id="bgpRouterId" maxlength="15"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																</tr>
															</table>
															</td>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td></td>
																	<td><label><s:text
																		name="config.routingProfiles.bgpNeighbors" /><font color="red"><s:text name="*"/></font>
																		</label></td>
																</tr>
																<tr>
																	<td></td>
																	<td><s:textarea name="bgpNeighbors" id="bgpNeighbors" wrap="true" cssStyle="width:200px"
																		rows="5" /></td>
																</tr>
																<tr></tr>

															</table>
															</td>
															</tr>
														</table>
														</td>
													</tr>
													<tr style="padding-left: 25px;display:<s:property value="ospfStyle"/>" id="ospfSection">
														<td colspan="2" style="padding-top: 0px">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="60"><label><s:text
																		name="config.routingProfiles.area" />
																		</label></td>
																	<td><s:textfield size="20" name="area" id="area" maxlength="15"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																	<td class="labelT1" width="60"><label><s:text
																		name="config.routingProfiles.routerId" />
																		</label></td>
																	<td><s:textfield size="20" name="routerId" id="routerId" maxlength="15"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</fieldset></td>
										</tr>
										<tr>
											<td height="10"></td>
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
		</s:if>

		<!-- CVG Internal Netowrks  -->
		<tr id="internalNetworkSettings">
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td style="padding-left: 5px;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.internalNetwork" />','intNetwork');</script></td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td style="padding-left:20px;">
									<div id="intNetwork" style="display: <s:property value="%{dataSource.internalNetworksDisplayStyle}"/>">
									<fieldset>
									<table cellspacing="0" cellpadding="0" border="0" class="embedded">
										<tr>
											<td height="10"></td>
										</tr>
										<tr id="newButtonIntNetwork">
											<td colspan="3" style="padding-bottom: 2px;" >
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="New"
														class="button" onClick="showCreateSection('intNetwork');"
														<s:property value="writeDisabled" /> ></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="doRemoveIntNetwork();"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:none" id="createButtonIntNetwork">
											<td colspan="3" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
														class="button" <s:property value="writeDisabled" /> onClick="doAddIntNetwork();"></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="doRemoveIntNetwork();"></td>
													<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
														class="button" onClick="hideCreateSection('intNetwork');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSectionIntNetwork">
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<th align="left" style="padding-left: 0;" width="30"><input
														type="checkbox" id="checkAllIntNetwork"
														onClick="toggleCheckAllIntNetwork(this);"></th>
													<th align="left" width="200"><s:text
														name="hiveAp.cvg.internalNetwork.network" /></th>
													<th align="left" width="200"><s:text
														name="hiveAp.cvg.internalNetwork.netmask" /></th>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:none" id="createSectionIntNetwork">
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="listHead" width="30">&nbsp;</td>
													<td class="listHead" valign="top" width="200"><s:textfield size="20" name="interNetIpInput" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
													<td class="listHead" valign="top" width="200"><s:textfield size="20" name="interNetMaskInput" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0" id="tbnet_id">
											<s:iterator value="%{dataSource.internalNetworks}" status="status">
												<tr>
													<td class="listCheck" width="30"><s:checkbox name="intNetworkIndices"
														fieldValue="%{#status.index}" /></td>
													<td class="list" width="200"><s:property value="internalNetwork" /></td>
													<td class="list" width="200"><s:property value="netmask" /></td>
												</tr>
											</s:iterator>
											</table>
											</td>
										</tr>
										<tr>
											<td colspan="5" width="100%">
												<table id="intNetworkTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
												<s:if test="%{gridCount > 0}">
													<s:generator separator="," val="%{' '}"
														count="%{gridCount}">
														<s:iterator>
															<tr>
																<td class="list" colspan="6">&nbsp;</td>
															</tr>
														</s:iterator>
													</s:generator>
												</s:if>
												</table>
											</td>
										</tr>
									</table>
									</fieldset>
									</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>

		<!-- CVG Static Routers  -->
		<tr id="cvgStaticRouterSettings">
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td style="padding-left: 5px;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.staticRoutes" />','staticRoutes');</script></td>
								</tr>
								<tr>
									<td style="padding-left:20px;">
									<div id="staticRoutes" style="display: <s:property value="%{dataSource.staticRoutesDisplayStyle}"/>">
									<fieldset>
										<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="tbl_id">
											<tr>
												<td height="10"></td>
											</tr>
											<tr id="newButtonStaticRoutes">
													<td colspan="3" style="padding-bottom: 2px;" >
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="New"
																class="button" onClick="showCreateSection('staticRoutes');"
																<s:property value="writeDisabled" />></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeStaticRoute');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:none" id="createButtonStaticRoutes">
													<td colspan="3" style="padding-bottom: 2px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																class="button" <s:property value="writeDisabled" /> onClick="submitAction('addStaticRoute');"></td>
															<td><input type="button" name="ignore" value="Remove"
																class="button" <s:property value="writeDisabled" />
																onClick="submitAction('removeStaticRouteNone');"></td>
															<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																class="button" onClick="hideCreateSection('staticRoutes');"></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="headerSectionStaticRoutes">
													<th align="left" style="padding-left: 0;" width="10"><input
														type="checkbox" id="checkAllRoute"
														onClick="toggleCheckAllRoutingProfilesStaticRoutes(this);"></th>
													<th align="left" width="200"><s:text
														name="config.routingProfiles.ip" /></th>
													<th align="left" width="200"><s:text
														name="config.routingProfiles.netmask" /></th>
													<th align="left" width="200"><s:text
														name="config.routingProfiles.gateway" /></th>
													<th align="left" width="200"><s:text
														name="hiveAp.cvg.internalNetwork.distribute" /></th>
												</tr>
												<tr style="display:none" id="createSectionStaticRoutes">
													<td class="listHead" width="10">&nbsp;</td>
													<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteIpInput" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
													<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteMaskInput" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
													<td class="listHead" valign="top"><s:textfield size="20" name="staticRouteGwInput" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
													<td class="listHead" valign="top" width="200"><s:checkbox name="distributeNet" /></td>
												</tr>
												<tr><td colspan="10">
													<table border="0" cellspacing="0" cellpadding="0" id="tbCVGIpRoute_id">
														<s:iterator value="%{dataSource.ipRoutes}" status="status">
															<tr>
																<td class="listCheck"><s:checkbox name="routingProfilesStaticRoutesIndices"
																	fieldValue="%{#status.index}" /></td>
																<td class="list" width="200"><s:property value="sourceIp" /></td>
																<td class="list" width="200"><s:property value="netmask" /></td>
																<td class="list" width="200"><s:property value="gateway" /></td>
																<td class="list" width="200"><s:checkbox name ="distributeBR" disabled="true" /></td>
															</tr>
														</s:iterator>
													</table>
												</td></tr>
												<tr>
													<td colspan="10" width="100%">
														<table id="cvgStaticRouteTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
														<s:if test="%{ipRouteCount > 0}">
															<s:generator separator="," val="%{' '}"
																count="%{ipRouteCount}">
																<s:iterator>
																	<tr>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</s:iterator>
															</s:generator>
														</s:if>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</fieldset></td></tr>
	<tr><td height="5px"></td></tr>
	</table>
	</s:if>
</div>