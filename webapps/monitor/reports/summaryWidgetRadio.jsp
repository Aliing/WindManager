<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<clients>
		<item>
			<wlanType>11a</wlanType>
			<count><s:property value="%{count11a}" /></count>
		</item>
		<item>
			<wlanType>11b</wlanType>
			<count><s:property value="%{count11b}" /></count>
		</item>
		<item>
			<wlanType>11g</wlanType>
			<count><s:property value="%{count11g}" /></count>
		</item>
		<item>
			<wlanType>11na</wlanType>
			<count><s:property value="%{count11na}" /></count>
		</item>
		<item>
			<wlanType>11ng</wlanType>
			<count><s:property value="%{count11ng}" /></count>
		</item>
		<item>
			<wlanType>wired</wlanType>
			<count><s:property value="%{countwired}" /></count>
		</item>
	</clients>
</summary>
