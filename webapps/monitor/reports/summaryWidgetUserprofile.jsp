<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<clientUserProfile>
		<s:iterator value="%{lstClientUserProfile}" status="status">
		<item>
			<userProfileId><s:property value="%{value}" /></userProfileId>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</clientUserProfile>
</summary>
