<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<numPackage><s:property value="%{numPackage}" /></numPackage>
	<numEvent><s:property value="%{numEvent}" /></numEvent>
	<numAlarm><s:property value="%{numAlarm}" /></numAlarm>
	<numActiveClient><s:property value="%{numActiveClient}" /></numActiveClient>
	<numDelat><s:property value="%{numDelat}" /></numDelat>
	<numAuditRequest><s:property value="%{numAuditRequest}" /></numAuditRequest>
	<numBackup><s:property value="%{numBackup}" /></numBackup>
	<numRestore><s:property value="%{numRestore}" /></numRestore>
	<numUpgrade><s:property value="%{numUpgrade}" /></numUpgrade>
</summary>
