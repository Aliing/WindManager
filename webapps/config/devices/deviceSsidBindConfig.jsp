<%@taglib prefix="s" uri="/struts-tags"%>
<div id="ssidBindConfigStyle">
	<s:if test="%{dataSource.deviceInfo.supportWifiRadio}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.ssidAllocation.label" />','ssidAllocation');</script></td>
		</tr>
		<tr>
			<td>
			<div id="ssidAllocation"
				style="display: <s:property value="%{dataSource.ssidAllocationDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td valign="top">
					<table cellspacing="0" cellpadding="0" border="0"
						width="600px">
						<tr>
							<td id="wifi2GSsidAllocation" valign="top"
								style="display: <s:property value="%{wifi2GSsidAllocationStyle}"/>">
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<th width="10px"><input type="checkbox"
										id="checkAll_0"
										onClick="toggleCheckAllWifiSsids(this,'ssid0Indices');"></th>
									<th nowrap="nowrap" align="left"
										style="padding-left: 10px"><s:text
										name="hiveAp.ssidAllocation.wifi0.label" /></th>
								</tr>
								<tr>
									<td colspan="10" id="wifi0ssidTable">
									<table cellspacing="0" cellpadding="0" border="0">
										<s:iterator value="%{wifi0Ssids}" status="status">
											<tr valign="top">
												<td style="padding: 5px 3px;" width="10px"><s:checkbox
													name="ssid0Indices"
													onclick="setCheckBoxSelected('checkAll_0', 'ssid0Indices');"
													fieldValue="%{ssid}" value="%{checked}" /></td>
												<td class="labelT1"><s:label value="%{ssidName}"
													title="%{tooltip}" /></td>
											</tr>
										</s:iterator>
									</table>
									</td>
								</tr>
							</table>
							</td>
							<!-- s:if test="%{dataSource.deviceInfo.twoRadio}" -->
							<td id="wifi2GSsidAllocationSpacer" nowrap="nowrap"
								style="display: <s:property value="%{wifi2GSsidAllocationStyle}"/>">&nbsp;&nbsp;</td>
							<td id="wifi5GSsidAllocation" valign="top"
								style="display: <s:property value="%{wifi5GSsidAllocationStyle}"/>">
							<table cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<th width="10px"><input type="checkbox"
										id="checkAll_1"
										onClick="toggleCheckAllWifiSsids(this,'ssid1Indices');"></th>
									<th nowrap="nowrap" align="left"
										style="padding-left: 10px"><s:text
										name="hiveAp.ssidAllocation.wifi1.label" /></th>
								</tr>
								<tr>
									<td colspan="10" id="wifi1ssidTable">
									<table cellspacing="0" cellpadding="0" border="0">
										<s:iterator value="%{wifi1Ssids}" status="status">
											<tr valign="top">
												<td style="padding: 5px 3px;" width="10px"><s:checkbox
													name="ssid1Indices"
													onclick="setCheckBoxSelected('checkAll_1', 'ssid1Indices');"
													fieldValue="%{ssid}" value="%{checked}" /></td>
												<td class="labelT1"><s:label value="%{ssidName}"
													title="%{tooltip}" /></td>
											</tr>
										</s:iterator>
									</table>
									</td>
								</tr>
							</table>
							</td>
							<!-- /s:if -->
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