<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<report>
	<title><s:property value="%{selectedL2Feature.description}" /></title>
	<reportResult>
		<s:iterator value="%{reportResult}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
		</s:iterator>
	</reportResult>
</report>
