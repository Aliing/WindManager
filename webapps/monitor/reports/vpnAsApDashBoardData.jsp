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
</apdashboard>
