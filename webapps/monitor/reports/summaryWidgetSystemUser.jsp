<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<numberOfLogin><s:property value="%{numberOfLogin}" /></numberOfLogin>
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
</summary>
