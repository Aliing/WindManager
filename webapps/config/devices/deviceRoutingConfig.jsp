<%@taglib prefix="s" uri="/struts-tags"%>
<div id="routingConfigStyle">
	<s:if test="%{dataSource.deviceInfo.sptL2Routing}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.tab.routing" />','routing');</script></td>
		</tr>
		<tr>
			<td>
			<div id="routing"
				style="display: <s:property value="%{dataSource.routingDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0"
						width="100%">
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td>
							<fieldset><legend><s:text
								name="hiveAp.cfg.l2Route" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%" class="embedded">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td style="padding-left: 10px; padding-right: 0px;">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td width="120px" nowrap="nowrap"><s:text
												name="hiveAp.cfg.dynamicRoute.metric.interval" /></td>
											<td width="200px" nowrap="nowrap"><s:textfield
												name="dataSource.metricInteval" size="5" maxlength="5"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text
												name="hiveAp.cfg.dynamicRoute.metric.interval.range" /></td>
											<td width="100px" style="padding-left: 20px;"><s:text
												name="hiveAp.cfg.dynamicRoute.metric.type" /></td>
											<td width="100px"><s:select
												list="%{enumMetricType}" listKey="key"
												listValue="value" value="%{dataSource.metric}"
												name="dataSource.metric" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td class="sepLine" colspan="3"><img
										src="<s:url value="/images/spacer.gif"/>" height="1"
										class="dblk" /></td>
								</tr>
								<tr>
									<td style="padding: 4px 0 0 0;">
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%" id="dynamicTable">
										<tr id="newButton">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="New" class="button"
														onClick="showCreateSection('dynamic');"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveDynamicRoutes2();"
														<s:property value="writeDisabled" />></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display: none;" id="createButton">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="Apply" class="button"
														onClick="doAddDynamicRoute2();"></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveDynamicRoutes2();"></td>
													<td><input type="button" name="ignore"
														value="Cancel" class="button"
														onClick="hideCreateSection('dynamic');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSectionDynamicRoutes2">
											<td colspan="5"></td>
										</tr>
										<tr id="headerSection">
											<th align="left" style="padding-left: 0;" width="30"><input
												type="checkbox" id="checkAll"
												onClick="toggleCheckAllDynamicRoutes(this);"></th>
											<th align="left" width="120"><s:text
												name="hiveAp.cfg.dynamicRoute.neighborMac" /></th>
											<th align="left" width="200"><s:text
												name="hiveAp.cfg.dynamicRoute.metric.minimum" /></th>
											<th align="left" width="200"><s:text
												name="hiveAp.cfg.dynamicRoute.metric.maximum" /></th>
										</tr>
										<tr style="display: none;" id="createSection">
											<td class="listHead" width="30">&nbsp;</td>
											<td class="listHead" valign="top" nowrap="nowrap" width="120"><s:textfield
												name="neighborMac" size="16" maxlength="12"
												onkeypress="return hm.util.keyPressPermit(event,'hex');" />
											<br>
											<s:text name="config.macOrOui.addressRange" /></td>
											<td class="listHead" valign="top" width="200"><s:textfield
												name="routeMinimun" size="16" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<br>
											<s:text name="hiveAp.cfg.dynamicRoute.metric.range" /></td>
											<td class="listHead" valign="top" width="200"><s:textfield
												name="routeMaximun" size="16" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<br>
											<s:text name="hiveAp.cfg.dynamicRoute.metric.range" /></td>
										</tr>
										<tr>
											<td colspan="5">
												<table id="dynamicRoutes2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
										<s:iterator value="%{dataSource.dynamicRoutes}"
											status="status">
											<tr class="list">
												<td class="listCheck" width="30"><s:checkbox
													name="dynamicRouteIndices"
													fieldValue="%{#status.index}" /></td>
												<td class="list" width="120"><s:property value="neighborMac" /></td>
												<td class="list" width="200"><s:property value="routeMinimun" /></td>
												<td class="list" width="200"><s:property value="routeMaximun" /></td>
											</tr>
										</s:iterator>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="5" width="100%">
												<table id="dynamicRoutes2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
											<s:if test="%{gridCount > 0}">
											<s:generator separator="," val="%{' '}"
												count="%{gridCount}">
												<s:iterator>
													<tr>
														<td class="list" colspan="6" width="100%">&nbsp;</td>
													</tr>
												</s:iterator>
											</s:generator>
											</s:if>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td height="30"></td>
								</tr>
								<tr>
									<td class="sepLine" colspan="3"><img
										src="<s:url value="/images/spacer.gif"/>" height="1"
										class="dblk" /></td>
								</tr>
								<tr>
									<td style="padding: 4px 0 0 0;">
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%" id="staticTable">
										<tr id="newButtonStatic">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="New" class="button"
														onClick="showCreateSection('static');"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveStaticRoutes2();"
														<s:property value="writeDisabled" />></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display: none;" id="createButtonStatic">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="Apply" class="button"
														onClick="doAddStaticRoute2();"></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveStaticRoutes2();"></td>
													<td><input type="button" name="ignore"
														value="Cancel" class="button"
														onClick="hideCreateSection('static');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSectionStatic">
											<th align="left" style="padding-left: 0;" width="30"><input
												type="checkbox" id="checkAllStatic"
												onClick="toggleCheckAllStaticRoutes(this);"></th>
											<th align="left" width="180"><s:text
												name="hiveAp.cfg.staticRoute.destination" /></th>
											<th align="left" width="180"><s:text
												name="hiveAp.cfg.staticRoute.interface" /></th>
											<th align="left" width="180"><s:text
												name="hiveAp.cfg.staticRoute.nextHop" /></th>
										</tr>
										<tr style="display: none;" id="createSectionStatic">
											<td class="listHead">&nbsp;</td>
											<td class="listHead" valign="top"><s:textfield
												name="destinationMac" size="16" maxlength="12"
												onkeypress="return hm.util.keyPressPermit(event,'hex');" />
											<br>
											<s:text name="config.macOrOui.addressRange" /></td>
											<td class="listHead" valign="top"><s:select
												name="interfaceType" list="%{enumStaticRouteIfType}"
												listKey="key" listValue="value"
												cssStyle="width: 110px;" /></td>
											<td class="listHead" valign="top"><s:textfield
												name="nextHopMac" size="16" maxlength="12"
												onkeypress="return hm.util.keyPressPermit(event,'hex');" />
											<br>
											<s:text name="config.macOrOui.addressRange" /></td>
										</tr>
										<tr>
											<td colspan="5">
												<table id="staticRoute2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
										<s:iterator value="%{dataSource.staticRoutes}"
											status="status">
											<tr>
												<td class="listCheck" width="30"><s:checkbox
													name="staticRouteIndices"
													fieldValue="%{#status.index}" /></td>
												<td class="list" width="180"><s:property
													value="destinationMac" /></td>
												<td class="list" width="180"><s:select name="interfaceTypes"
													value="%{interfaceType}"
													list="%{enumStaticRouteIfType}" listKey="key"
													listValue="value" /></td>
												<td class="list" width="180"><s:property value="nextHopMac" /></td>
											</tr>
										</s:iterator>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="5" width="100%">
												<table id="staticRoute2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
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
									</td>
								</tr>
								<tr>
									<td height="30"></td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td>
							<fieldset><legend><s:text
								name="hiveAp.cfg.l3Route" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%" class="embedded">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td style="padding: 4px 0 0 0;">
									<table cellspacing="0" cellpadding="0" border="0"
										width="100%">
										<tr id="newButtonIp">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="New" class="button"
														onClick="showCreateSection('ip');"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveIpRoutes2();"
														<s:property value="writeDisabled" />></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display: none;" id="createButtonIp">
											<td colspan="5" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore"
														value="Apply" class="button"
														onClick="doAddIpRoute2();"></td>
													<td><input type="button" name="ignore"
														value="Remove" class="button"
														onClick="doRemoveIpRoutes2();"></td>
													<td><input type="button" name="ignore"
														value="Cancel" class="button"
														onClick="hideCreateSection('ip');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSectionIp">
											<th align="left" style="padding-left: 0;" width="60"><input
												type="checkbox" id="checkAllIp"
												onClick="toggleCheckAllIpRoutes(this);"></th>
											<th align="left" width="220"><s:text
												name="hiveAp.cfg.ipRoute.dest" /></th>
											<th align="left" width="150"><s:text
												name="hiveAp.cfg.ipRoute.mask" /></th>
											<th align="left" width="150"><s:text
												name="hiveAp.cfg.ipRoute.gateway" /></th>
										</tr>
										<tr style="display: none;" id="createSectionIp">
											<td class="listHead" width="60">&nbsp;</td>
											<td class="listHead" valign="top" nowrap="nowrap" width="220"><s:textfield
												name="ipRouteIpInput" size="16" maxlength="15" /></td>
											<td class="listHead" valign="top" width="150"><s:textfield
												name="ipRouteMaskInput" size="16" maxlength="15" /></td>
											<td class="listHead" valign="top" width="150"><s:textfield
												name="ipRouteGwInput" size="16" maxlength="15" /></td>
										</tr>
										<tr>
											<td colspan="5">
												<table id="ipRoutes2TblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
										<s:iterator value="%{dataSource.ipRoutes}"
											status="status">
											<tr>
												<td class="listCheck" width="60"><s:checkbox
													name="ipRouteIndices" fieldValue="%{#status.index}" /></td>
												<td class="list" width="220"><s:property value="sourceIp" /></td>
												<td class="list" width="150"><s:property value="netmask" /></td>
												<td class="list" width="150"><s:property value="gateway" /></td>
											</tr>
										</s:iterator>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="5" width="100%">
												<table id="ipRoutes2TblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
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
									</td>
								</tr>
								<tr>
									<td height="30"></td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
						<td>
						<table border="0" cellspacing="0" cellpadding="0"
							width="100%">
							<tr>
								<td height="5px"></td>
							</tr>
							<tr id="multipleVlanSection"
								style="display: <s:property value="%{multipleVlanDisplay}"/>">

								<td valign="top" width="100%" style="padding-top: 5px;">

								<fieldset><legend><s:text
									name="hiveAp.ethernet.multiple.route" /></legend>
								<table cellspacing="0" cellpadding="0" border="0"
									width="100%" class="embedded">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td style="padding: 4px 0 0 0;">
										<table cellspacing="0" cellpadding="0" border="0"
											width="100%" id="multipleVlanTable">
											<tr id="newButtonVlanId">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore"
															value="New" class="button"
															onClick="showCreateSection('vlanid');"
															<s:property value="writeDisabled" />></td>
														<td><input type="button" name="ignore"
															value="Remove" class="button"
															onClick="doRemoveMultipleVlan();"
															<s:property value="writeDisabled" />></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display: none;" id="createButtonVlanId">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore"
															value="Apply" class="button"
															onClick="doAddMultipleVlan();"></td>
														<td><input type="button" name="ignore"
															value="Remove" class="button"
															onClick="doRemoveMultipleVlan();"></td>
														<td><input type="button" name="ignore"
															value="Cancel" class="button"
															onClick="hideCreateSection('vlanid');"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="headerSectionVlanId">
												<th align="left" style="padding-left: 0;" width="180"><input
													type="checkbox" id="checkAllVlanId"
													onClick="toggleCheckAllMultipleVlan(this);"></th>
												<th align="left" width="400"><s:text
													name="hiveAp.ethernet.multiple.vlan" /></th>
											</tr>
											<tr style="display: none;" id="createSectionVlanId">
												<td class="listHead">&nbsp;</td>
												<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
													name="multiplevlanInput" size="16" maxlength="15" />
													<br>
												<s:text name="hiveAp.ethernet.multiple.vlan.range" /></td>
												</tr>
											<tr>
											<td colspan="5">
												<table id="multipleVlanTblData" cellspacing="0" cellpadding="0" border="0" class="embedded" >
											<s:iterator value="%{dataSource.multipleVlan}"
												status="status">
												<tr>
													<td class="listCheck"><s:checkbox
														name="multiplevlanIndices" fieldValue="%{#status.index}" /></td>
													<td class="list"><s:property value="vlanid" />

												</tr>
											</s:iterator>
												</table>
											</td>
											</tr>
											<tr>
											<td colspan="5" width="100%">
												<table id="multipleVlanTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
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
										</td>
									</tr>
									<tr>
										<td height="30"></td>
									</tr>
								</table>
								</fieldset>
								</td>
						</tr>
						</table>
						</td>
						</tr>
						<tr>
							<td height="10"></td>
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