<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<compliance>
		<item>
			<severity>Weak</severity>
			<count><s:property value="%{poorAp}" /></count>
		</item>
		<item>
			<severity>Acceptable</severity>
			<count><s:property value="%{goodAp}" /></count>
		</item>
		<item>
			<severity>Strong</severity>
			<count><s:property value="%{excellentAp}" /></count>
		</item>
	</compliance>
	<noDataTitle><s:text name="report.summary.compliance.nodata.title"/></noDataTitle>
	
</summary>
