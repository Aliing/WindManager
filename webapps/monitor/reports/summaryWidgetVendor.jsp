<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<clientVendor>
		<s:iterator value="%{lstClientVendorCount}" status="status">
		<item>
			<severity><s:property value="%{value}" /></severity>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</clientVendor>
</summary>
