<%@taglib prefix="s" uri="/struts-tags"%>
<div>
	<s:if test="%{dataSource.deviceInfo.supportWifiRadio}">
	<fieldset>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="5"></td>
		</tr>
		<tr id="wifiConfigTitle">
			<th align="left" width="80px"><s:text
				name="hiveAp.wlanIf" /></th>
			<th align="left" width="80px"><s:text
				name="hiveAp.if.radioMode" /></th>
			<th align="left" width="200px"><s:text
				name="hiveAp.if.radioProfile" /></th>
			<th align="left" width="80px"><s:text
				name="hiveAp.if.adminState" /></th>
			<th align="left" width="85px"><s:text
				name="hiveAp.if.channel" /></th>
			<th align="left" nowrap="nowrap"><s:text
				name="hiveAp.if.power" /></th>
		</tr>
		<tr id="wifiConfig_blank">
			<td height="5"></td>
		</tr>
		<tr id="wifi0Row">
			<td class="list"><span id="wifi0Label"><s:property
				value="%{wifi0Label}" /></span> <s:text name="hiveAp.if.wifi0" /></td>
			<td id="wifi0RadioMode" class="list"><s:property
				value="%{wifi0RadioModeLabel}" /></td>
			<td class="list"><s:select name="wifi0RadioProfile"
				list="%{wifi0RadioProfiles}" listKey="id" listValue="value"
				cssStyle="width: 150px;" onchange="selectRadioProfile(this)" />
			<s:if test="%{writeDisabled == 'disabled'}">
				<img class="dinl marginBtn"
					src="<s:url value="/images/new_disable.png" />" width="16"
					height="16" alt="New" title="New" />
			</s:if> <s:else>
				<a class="marginBtn"
					href="javascript:newSimpleRadioProfile('wifi0')"><img
					class="dinl" src="<s:url value="/images/new.png" />"
					width="16" height="16" alt="New" title="New" /></a>
			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
				<img class="dinl marginBtn"
					src="<s:url value="/images/modify_disable.png" />"
					width="16" height="16" alt="Modify" title="Modify" />
			</s:if> <s:else>
				<a class="marginBtn"
					href="javascript:editWifi0RadioProfile()"><img
					class="dinl" src="<s:url value="/images/modify.png" />"
					width="16" height="16" alt="Modify" title="Modify" /></a>
			</s:else></td>
			<td class="list"><s:select
				name="dataSource.wifi0.adminState"
				value="%{dataSource.wifi0.adminState}"
				list="%{enumAdminStateType}" listKey="key" listValue="value"
				onchange="adminStatusChanged(this, 'wifi0');" /></td>
			<td class="list"><s:select
				name="dataSource.wifi0.channel"
				value="%{dataSource.wifi0.channel}" list="%{wifi0Channel}"
				listKey="key" listValue="value" /></td>
			<td class="list"><s:select name="dataSource.wifi0.power"
				value="%{dataSource.wifi0.power}" list="%{enumPowerType}"
				listKey="key" listValue="value" /></td>
		</tr>
		<tr>
			<td colspan="10"><div id="errNoteForRadiobg"/></div>
		</tr>
		<tr>
			<td colspan="10">
			<div id="wifi0RadioProfileCreateSection"
				style="display: <s:property value="%{dataSource.tempWifi0RadioProfileCreateDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-bottom: 2px; padding-left: 10px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{wifi0RadioProfileNameDisabled}">
								<td><input type="button" name="ignore"
									value="Apply" class="button"
									onClick="submitAction('updateWifi0RadioProfile');"
									<s:property value="wifi0RadioProfileApplyDisabled"/>></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"
									value="Apply" class="button"
									onClick="submitAction('createWifi0RadioProfile');"></td>
							</s:else>
							<s:if test="%{wifi0RadioProfileNameDisabled}">
								<td><input type="button" name="ignore"
									value="Cancel" class="button"
									onClick="submitAction('clearWifi0RadioProfile');"></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"
									value="Cancel" class="button"
									onClick="hideSimpleCreateSection('wifi0RadioProfileCreateSection');"></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="sepLine"><img
						src="<s:url value="/images/spacer.gif"/>" height="2"
						class="dblk" /></td>
				</tr>
				<tr>
					<td>
					<table class="listembedded" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="170px"><s:text
								name="config.radioProfile.name" /></td>
							<td><s:textfield
								name="dataSource.tempWifi0RadioProfile.radioName"
								size="32" maxlength="%{radioProfileNameLength}"
								disabled="%{wifi0RadioProfileNameDisabled}" /> <s:text
								name="config.ssid.ssidName_range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.radioProfile.radioMode" /></td>
							<td><s:select
								name="dataSource.tempWifi0RadioProfile.radioMode"
								disabled="%{wifi0RadioProfileNameDisabled}"
								list="%{enumRadioMode}" listKey="key" listValue="value"
								cssStyle="width: 110px"
								onchange="changeWifi0RadioMode(this.value);" /></td>
						</tr>
						<tr id="channelWidthTr0"
							style="display: <s:property value="%{wifi0RadioProfileChannelStyle}"/>">
							<td class="labelT1"><s:text
								name="config.radioProfile.channel.width" /></td>
							<td><s:select
								name="dataSource.tempWifi0RadioProfile.channelWidth"
								list="%{enumRadioChannelWidth}" listKey="key"
								listValue="value" cssStyle="width: 110px" /></td>
						</tr>
						<tr>
							<s:if test="%{wifi0RadioProfileNameDisabled}">
								<td class="labelT1" colspan="2"
									style="padding-right: 30px;"><a class="textLink"
									href="javascript:editWifi0RadioProfile();">More
								Settings...</a></td>
							</s:if>
							<s:else>
								<td class="labelT1" colspan="2"
									style="padding-right: 30px;"><a class="textLink"
									href="javascript:newWifi0RadioProfile();">More
								Settings...</a></td>
							</s:else>
						</tr>
						<tr>
							<td height="10px" colspan="2"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<s:if test="%{dataSource.deviceInfo.twoRadio}">
		<tr id="wifiConfig_blank">
			<td height="5px"></td>
		</tr>
		<tr id="wifi1Row"
			style="display: <s:property value="wifi1StuffStyle"/>">
			<td class="list"><span id="wifi1Label"><s:property
				value="%{wifi1Label}" /></span> <s:text name="hiveAp.if.wifi1" /></td>
			<td id="wifi1RadioMode" class="list"><s:property
				value="%{wifi1RadioModeLabel}" /></td>
			<td class="list"><s:select name="wifi1RadioProfile"
				list="%{wifi1RadioProfiles}" listKey="id" listValue="value"
				cssStyle="width: 150px;" onchange="selectRadioProfile(this)" />
			<s:if test="%{writeDisabled == 'disabled'}">
				<img class="dinl marginBtn"
					src="<s:url value="/images/new_disable.png" />" width="16"
					height="16" alt="New" title="New" />
			</s:if> <s:else>
				<a class="marginBtn"
					href="javascript:newSimpleRadioProfile('wifi1')"><img
					class="dinl" src="<s:url value="/images/new.png" />"
					width="16" height="16" alt="New" title="New" /></a>
			</s:else> <s:if test="%{writeDisabled == 'disabled'}">
				<img class="dinl marginBtn"
					src="<s:url value="/images/modify_disable.png" />"
					width="16" height="16" alt="Modify" title="Modify" />
			</s:if> <s:else>
				<a class="marginBtn"
					href="javascript:editWifi1RadioProfile()"><img
					class="dinl" src="<s:url value="/images/modify.png" />"
					width="16" height="16" alt="Modify" title="Modify" /></a>
			</s:else></td>
			<td class="list"><s:select
				name="dataSource.wifi1.adminState"
				value="%{dataSource.wifi1.adminState}"
				list="%{enumAdminStateType}" listKey="key" listValue="value"
				onchange="adminStatusChanged(this, 'wifi1');" /></td>
			<td class="list"><s:select
				name="dataSource.wifi1.channel"
				value="%{dataSource.wifi1.channel}" list="%{wifi1Channel}"
				listKey="key" listValue="value" /></td>
			<td class="list"><s:select name="dataSource.wifi1.power"
				value="%{dataSource.wifi1.power}" list="%{enumPowerType}"
				listKey="key" listValue="value" /></td>
		</tr>
		<tr>
			<td colspan="10"><div id="errNoteForRadioa"/></div>
		</tr>
		<tr>
			<td colspan="10">
			<div id="wifi1RadioProfileCreateSection"
				style="display: <s:property value="%{dataSource.tempWifi1RadioProfileCreateDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-bottom: 2px; padding-left: 10px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{wifi1RadioProfileNameDisabled}">
								<td><input type="button" name="ignore"
									value="Apply" class="button"
									onClick="submitAction('updateWifi1RadioProfile');"
									<s:property value="wifi1RadioProfileApplyDisabled"/>></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"
									value="Apply" class="button"
									onClick="submitAction('createWifi1RadioProfile');"></td>
							</s:else>
							<s:if test="%{wifi1RadioProfileNameDisabled}">
								<td><input type="button" name="ignore"
									value="Cancel" class="button"
									onClick="submitAction('clearWifi1RadioProfile');"></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"
									value="Cancel" class="button"
									onClick="hideSimpleCreateSection('wifi1RadioProfileCreateSection');"></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="sepLine"><img
						src="<s:url value="/images/spacer.gif"/>" height="2"
						class="dblk" /></td>
				</tr>
				<tr>
					<td>
					<table class="listembedded" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="170px"><s:text
								name="config.radioProfile.name" /></td>
							<td><s:textfield
								name="dataSource.tempWifi1RadioProfile.radioName"
								size="32" maxlength="%{radioProfileNameLength}"
								disabled="%{wifi1RadioProfileNameDisabled}" /> <s:text
								name="config.ssid.ssidName_range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.radioProfile.radioMode" /></td>
							<td><s:select
								name="dataSource.tempWifi1RadioProfile.radioMode"
								disabled="%{wifi1RadioProfileNameDisabled}"
								list="%{enumRadioMode}" listKey="key" listValue="value"
								cssStyle="width: 110px"
								onchange="changeWifi1RadioMode(this.value);" /></td>
						</tr>
						<tr id="channelWidthTr1"
							style="display: <s:property value="%{wifi1RadioProfileChannelStyle}"/>">
							<td class="labelT1"><s:text
								name="config.radioProfile.channel.width" /></td>
							<td><s:select
								name="dataSource.tempWifi1RadioProfile.channelWidth"
								list="%{enumRadioChannelWidth}" listKey="key"
								listValue="value" cssStyle="width: 110px" /></td>
						</tr>
						<tr>
							<s:if test="%{wifi1RadioProfileNameDisabled}">
								<td class="labelT1" colspan="2"
									style="padding-right: 30px;"><a class="textLink"
									href="javascript:editWifi1RadioProfile();">More
								Settings...</a></td>
							</s:if>
							<s:else>
								<td class="labelT1" colspan="2"
									style="padding-right: 30px;"><a class="textLink"
									href="javascript:newWifi1RadioProfile();">More
								Settings...</a></td>
							</s:else>
						</tr>
						<tr>
							<td height="10px" colspan="2"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		</s:if>
	</table>
	</fieldset>
	</s:if>
</div>