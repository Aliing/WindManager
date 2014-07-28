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
		<wanAvailability><s:text name="report.summary.deviceInfo.cvg.wan.availability"/></wanAvailability>
		<wanThroughPut><s:text name="report.summary.deviceInfo.cvg.wan.throughPut"/></wanThroughPut>
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
	<vpnAvailabilityData>
		<availabilityData>
		<s:iterator value="%{cvg_vpn_availability}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</availabilityData>
		<upTime>
		<s:iterator value="%{cvg_vpn_uptime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</upTime>
	</vpnAvailabilityData>
	
	<wanAvailabilityData>
		<availabilityData>
		<s:iterator value="%{cvg_wan_availability}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</availabilityData>
		<upTime>
		<s:iterator value="%{cvg_wan_uptime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</upTime>
	</wanAvailabilityData>
	
	<wanThroughPutData>
		<dataIn>
		<s:iterator value="%{cvg_wan_throughput_in}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataIn>
		<dataOut>
		<s:iterator value="%{cvg_wan_throughput_out}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
		</s:iterator>
		</dataOut>
	</wanThroughPutData>
	
</apdashboard>
