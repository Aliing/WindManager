<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<systemOverview>
	<hmHostname><s:property value="%{hmHostname}" /></hmHostname>
	<hmVersion><s:property value="%{hmVersion}" /></hmVersion>
	<buildTime><s:property value="%{buildTime}" /></buildTime>
	<hmModel><s:property value="%{hmModel}" /></hmModel>
	<systemUpTime><s:property value="%{systemUpTime}" /></systemUpTime>
	<mgtState><s:property value="%{mgtState}" /></mgtState>
	<lanState><s:property value="%{lanState}" /></lanState>
	<haStatus><s:property value="%{haStatus}" /></haStatus>
	<showReplicateStatus><s:property value="%{showReplicateStatus}" /></showReplicateStatus>
	<replicateStatus><s:property value="%{replicateStatus}" /></replicateStatus>
	<numberOfLogin><s:property value="%{numberOfLogin}" /></numberOfLogin>
	<totalMemo><s:property value="%{totalMemo}" /></totalMemo>
	<freeMemo><s:property value="%{freeMemo}" /></freeMemo>
	<usageMemo><s:property value="%{usageMemo}" /></usageMemo>
	

	<numPackage><s:property value="%{numPackage}" /></numPackage>
	<numEvent><s:property value="%{numEvent}" /></numEvent>
	<numAlarm><s:property value="%{numAlarm}" /></numAlarm>
	<numActiveClient><s:property value="%{numActiveClient}" /></numActiveClient>
	<numDelat><s:property value="%{numDelat}" /></numDelat>
	<numAuditRequest><s:property value="%{numAuditRequest}" /></numAuditRequest>
	<numBackup><s:property value="%{numBackup}" /></numBackup>
	<numRestore><s:property value="%{numRestore}" /></numRestore>
	<numUpgrade><s:property value="%{numUpgrade}" /></numUpgrade>
	
	<loginUsers>
		<s:iterator value="%{loginUsers}" id="loginUser" status="status">
		<item>
			<count><s:property value="%{#status.index + 1}"/></count>
			<userName><s:property value="%{#loginUser.userName}" /></userName>
			<userIpAddress><s:property value="%{#loginUser.userIpAddress}" /></userIpAddress>
			<idleTime><s:property value="%{#loginUser.userSessionUndoTime}" /></idleTime>
			<sessionTotalTime><s:property value="%{#loginUser.userSessionTotalTime}" /></sessionTotalTime>
			<removeSessionId><s:property value="%{#loginUser.sessionId}" /></removeSessionId>
		</item>
		</s:iterator>
	</loginUsers>
	
	<cpuUsage>
		<s:iterator value="%{cpuUse}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</cpuUsage>
	
	<memoryUsage>
		<s:iterator value="%{memoryUse}" status="status">
		<item>
			<count><s:property/></count>
		</item>
		</s:iterator>
	</memoryUsage>
	
</systemOverview>
