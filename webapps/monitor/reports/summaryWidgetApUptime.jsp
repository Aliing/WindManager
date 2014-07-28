<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<hiveapUptime>
		<s:iterator value="%{apUptime}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</hiveapUptime>
	<apUptimeTitle><s:text name="report.summary.apInfo.upPencent"/></apUptimeTitle>
</summary>
