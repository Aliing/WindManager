<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="l3RoamingStyle">
	<s:if test="%{dataSource.deviceInfo.supportL3Roaming}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.cfg.l3Roaming" />','l3Roaming');</script></td>
		</tr>
		<tr>
			<td>
			<div id="l3Roaming"
				style="display: <s:property value="%{dataSource.l3RoamingDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
					<table cellspacing="0" cellpadding="0" border="0"
						width="100%" class="embedded">
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td style="padding-right: 10px;" id="l3RoamingThresholdStyle" style="display: <s:property value='l3RoamingThresholdStyle'/>">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="150px"
										style="padding-right: 32px;"><s:text
										name="hiveAp.cfg.l3Roaming.threshold" /></td>
									<td><s:select name="dataSource.tunnelThreshold"
										value="%{dataSource.tunnelThreshold}"
										list="%{enumTunnelThresholdType}" listKey="key"
										listValue="value" cssStyle="width: 80px;" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><label id="l3RoamingLabel"></label></td>
									<td width="338px">
									<fieldset><legend><s:text
										name="hiveAp.cfg.l3Roaming.included" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<s:push value="%{includedNeighborOptions}">
												<td><tiles:insertDefinition
													name="optionsTransfer" /></td>
											</s:push>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
									</table>
									</fieldset>
									</td>
									<td width="5px"></td>
									<td width="338px">
									<fieldset><legend><s:text
										name="hiveAp.cfg.l3Roaming.excluded" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<s:push value="%{excludedNeighborOptions}">
												<td><tiles:insertDefinition
													name="optionsTransfer" /></td>
											</s:push>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
									</table>
									</fieldset>
									</td>
								</tr>
							</table>
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