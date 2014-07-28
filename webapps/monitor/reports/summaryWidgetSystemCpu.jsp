<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<cpuUsage>
		<s:iterator value="%{cpuUse}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</cpuUsage>
	<totalMemo><s:property value="%{totalMemo}" /></totalMemo>
	<freeMemo><s:property value="%{freeMemo}" /></freeMemo>
	<usageMemo><s:property value="%{usageMemo}" /></usageMemo>
	<memoryUsage>
		<s:iterator value="%{memoryUse}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</memoryUsage>
</summary>
