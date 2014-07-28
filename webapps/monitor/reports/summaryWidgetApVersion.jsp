<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<hiveapVersion>
		<s:iterator value="%{hiveApVersionMap}" status="status" id="keysetValue">
		<item>
			<version><s:property value="key"/></version>
			<count><s:property value="value"/></count>
		</item>
		</s:iterator>
	</hiveapVersion>
</summary>
