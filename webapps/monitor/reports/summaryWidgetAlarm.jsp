<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<alarms>
		<item>
			<severity>Critical</severity>
			<count><s:property value="%{criticalAlarmCount}" /></count>
		</item>
		<item>
			<severity>Major</severity>
			<count><s:property value="%{majorAlarmCount}" /></count>
		</item>
		<item>
			<severity>Minor</severity>
			<count><s:property value="%{minorAlarmCount}" /></count>
		</item>
		<item>
			<severity>Cleared</severity>
			<count><s:property value="%{clearedAlarmCount}" /></count>
		</item>
	</alarms>
</summary>
