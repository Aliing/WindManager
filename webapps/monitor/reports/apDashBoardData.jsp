<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<apdashboard>
	<totalMemo><s:property value="%{totalMem}" /></totalMemo>
	<freeMemo><s:property value="%{freeMem}" /></freeMemo>
	<usageMemo><s:property value="%{usedMem}" /></usageMemo>
	<apCpuPencent><s:text name="report.summary.apInfo.apCpuUsage"/></apCpuPencent>
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
	<wifi0>
		<averageInterferenceCu>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{averageInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</averageInterferenceCu>
		<shotTermInterferenceCu>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{shortTermInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</shotTermInterferenceCu>
		
		<snapShotInterferenceCu>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{snapShotInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</snapShotInterferenceCu>
		<crcRate>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{crcError}" /></count>
			</item>
			</s:iterator>
		</crcRate>
		<interferenceCuThreshold>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{interferenceCUThreshold}" /></count>
			</item>
			</s:iterator>
		</interferenceCuThreshold>
		<crcRateThreshold>
			<s:iterator value="%{interferenceWifi0List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{crcErrorRateThreshold}" /></count>
			</item>
			</s:iterator>
		</crcRateThreshold>
	</wifi0>
	<wifi1>
		<averageInterferenceCu>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{averageInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</averageInterferenceCu>
		<shotTermInterferenceCu>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{shortTermInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</shotTermInterferenceCu>
		
		<snapShotInterferenceCu>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{snapShotInterferenceCU}" /></count>
			</item>
			</s:iterator>
		</snapShotInterferenceCu>
		<crcRate>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{crcError}" /></count>
			</item>
			</s:iterator>
		</crcRate>
		<interferenceCuThreshold>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{interferenceCUThreshold}" /></count>
			</item>
			</s:iterator>
		</interferenceCuThreshold>
		<crcRateThreshold>
			<s:iterator value="%{interferenceWifi1List}" status="status">
			<item>
				<date><s:property value="%{statTimeString}" /></date>
				<count><s:property value="%{crcErrorRateThreshold}" /></count>
			</item>
			</s:iterator>
		</crcRateThreshold>
	</wifi1>
	
	<acspData>
		<s:iterator value="%{acspNeighborList}" status="status">
		<item>
			<channelData><s:property value="%{channelNumber}" /></channelData>
			<bssidData><s:property value="%{bssid}" /></bssidData>
			<rssiData><s:property value="%{rssiDbm}" /></rssiData>
			<statTime><s:property value="%{reportTimeString}" /></statTime>
		</item>
		</s:iterator>
	</acspData>
	
</apdashboard>
