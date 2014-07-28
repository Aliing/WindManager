<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<users>
		<s:iterator value="%{users}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</users>
</summary>
