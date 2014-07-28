<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<slaClientLastHour><s:property value="%{slaClientTitle}" /></slaClientLastHour>
	<clientSlaType><s:property value="%{clientSlaType}"/></clientSlaType>
	<slaClientBad>
		<s:iterator value="%{lstSlaClientBad}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientBad>
	<slaClientYellow>
		<s:iterator value="%{lstSlaClientYellow}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientYellow>
	<slaClientClear>
		<s:iterator value="%{lstSlaClientClear}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientClear>
</summary>
