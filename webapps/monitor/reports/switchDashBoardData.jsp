<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<apdashboard>
	<totalMemo><s:property value="%{totalMem}" /></totalMemo>
	<freeMemo><s:property value="%{freeMem}" /></freeMemo>
	<usageMemo><s:property value="%{usedMem}" /></usageMemo>
	<apCpuPencent><s:text name="report.summary.apInfo.apCpuMemUsage"/></apCpuPencent>
	<chartTitles>
		<vpnAvailability><s:text name="report.summary.deviceInfo.vpn.availability"/></vpnAvailability>
		<vpnThroughPut><s:text name="report.summary.deviceInfo.vpn.throughPut"/></vpnThroughPut>
		<vpnLatency><s:text name="report.summary.deviceInfo.vpn.latency"/></vpnLatency>
	</chartTitles>
	<cpuUsage>
		<s:iterator value="%{cpuUsage}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</cpuUsage>
	<memoryUsage>
		<s:iterator value="%{memUsage}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</memoryUsage>
	<users>
		<s:iterator value="%{uniqueClients}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</users>
	<userVendor>
		<s:iterator value="%{clientOuis}" status="status">
		<item>
			<name>"<s:property value="%{value}" />"</name>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</userVendor>
	
	<vpnAvailabilityData>
		<availabilityData>
		<s:iterator value="%{vpn_availability}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</availabilityData>
		<upTime>
		<s:iterator value="%{vpn_uptime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</upTime>
	</vpnAvailabilityData>
	
	<vpnThroughPutData>
		<tunnelName>
			<tunnel1><s:property value="%{vpn_tunnel1_name}" /></tunnel1>
			<tunnel2><s:property value="%{vpn_tunnel2_name}" /></tunnel2>
		</tunnelName>
		<tunnel1Data>
		<dataIn>
		<s:iterator value="%{vpn_throughput_tunnel1_in}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataIn>
		<dataOut>
		<s:iterator value="%{vpn_throughput_tunnel1_out}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataOut>
		</tunnel1Data>
		<tunnel2Data>
		<dataIn>
		<s:iterator value="%{vpn_throughput_tunnel2_in}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataIn>
		<dataOut>
		<s:iterator value="%{vpn_throughput_tunnel2_out}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataOut>
		</tunnel2Data>
	</vpnThroughPutData>
	
	<vpnLatencyData>
		<tunnelName>
			<tunnel1><s:property value="%{vpn_latency_tunnel1_name}" /></tunnel1>
			<tunnel2><s:property value="%{vpn_latency_tunnel2_name}" /></tunnel2>
		</tunnelName>
		<tunnel1Data>
			<s:iterator value="%{vpn_latency_tunnel1}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</tunnel1Data>
		<tunnel2Data>
			<s:iterator value="%{vpn_latency_tunnel2}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</tunnel2Data>
	</vpnLatencyData>
	
	<deviceInfo>
		<s:if test="%{blnSwitchAsBr}">
			<blnDeviceTypeBr>true</blnDeviceTypeBr>
		</s:if>
		<s:else>
			<blnDeviceTypeBr>false</blnDeviceTypeBr>
		</s:else>
	</deviceInfo>
	
</apdashboard>
