<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<slaApLastHour><s:property value="%{slaApTitle}" /></slaApLastHour>
	<apSlaType><s:property value="%{apSlaType}"/></apSlaType>	
	<slaApBad>
		<s:iterator value="%{lstSlaApBad}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApBad>
	<slaApYellow>
		<s:iterator value="%{lstSlaApYellow}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApYellow>
	<slaApClear>
		<s:iterator value="%{lstSlaApClear}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApClear>
</summary>
