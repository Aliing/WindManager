<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="editBox view" width="100%">
				<tr>
					<td class="list" colspan="3">
					<s:if test="%{testFailType == 0}">
						<s:if test="%{hmolConnTestResult.noConnectDevicesListLength > 1}">
							<s:property value="%{hmolConnTestResult.noConnectDevicesListLength}" />&nbsp;Devices
						</s:if>
						<s:else>
							<s:property value="%{hmolConnTestResult.noConnectDevicesListLength}" />&nbsp;Device
						</s:else>
						<s:text name="admin.updateSoftware.testResult.noconnect.title"/>
					</s:if>
					<s:else>
						<s:if test="%{hmolConnTestResult.failConnectDeviceListLength > 1}">
							<s:property value="%{hmolConnTestResult.failConnectDeviceListLength}" />&nbsp;Devices
						</s:if>
						<s:else>
							<s:property value="%{hmolConnTestResult.failConnectDeviceListLength}" />&nbsp;Device
						</s:else>
						<s:text name="admin.updateSoftware.testResult.failconnect.title1"/>
						<s:property value="%{hmolConnTestResult.serviceAddress}" /> 
						<s:text name="admin.updateSoftware.testResult.failconnect.title2"/>
					</s:else>
					</td>
				</tr>
				<tr>
					<!-- <th align="left" nowrap><s:text name="config.ssidProfile.enableARateSet" /></th> -->
					<th align="left" nowrap>Host Name</th>
					<th align="left" nowrap>IP Address</th>
					<th align="left" nowrap>Device Model</th>
				</tr>
					<s:if test="%{testFailType == 0}">
						<s:iterator value="%{hmolConnTestResult.noConnectDevicesList}" status="status">
							<tiles:insertDefinition name="rowClass" />
						<tr class="<s:property value="%{#rowClass}"/>">
							<td class="list"><s:property value="hostName" />&nbsp;</td>
							<td class="list"><s:property value="ipAddress" />&nbsp;</td>
							<td class="list"><s:property value="hwModel" />&nbsp;</td>
						</tr>
						</s:iterator>
					</s:if>
					<s:else>
						<s:iterator value="%{hmolConnTestResult.failConnectDeviceList}" status="status">
							<tiles:insertDefinition name="rowClass" />
						<tr class="<s:property value="%{#rowClass}"/>">
							<td class="list"><s:property value="hostName" />&nbsp;</td>
							<td class="list"><s:property value="ipAddress" />&nbsp;</td>
							<td class="list"><s:property value="hwModel" />&nbsp;</td>
						</tr>
						</s:iterator>
					</s:else>
			</table>
			</td>
		</tr>
	</table>
</div>
