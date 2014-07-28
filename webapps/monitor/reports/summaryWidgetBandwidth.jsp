<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<bindWidthRate>
		<s:iterator value="%{bindWidthRate}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</bindWidthRate>
</summary>
