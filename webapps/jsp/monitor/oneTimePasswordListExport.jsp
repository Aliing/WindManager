<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<%
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition",
			"attachment; filename=\"Device Registration Code.xls\"");
%>

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />

<link rel="stylesheet"
	href="https://<%=request.getServerName()%>:<%=request.getServerPort()%>/<%=request.getContextPath()%>/css/hm.css"
	type="text/css" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="https://<%=request.getServerName()%>:<%=request.getServerPort()%>/<%=request.getContextPath()%>/yui/fonts/fonts-min.css" />

</head>
<body class="body_bg yui-skin-sam" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0">
	<table cellspacing="0" cellpadding="0" border="0" width="100%" class="view">
		<tr>
			<th class="check"></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.password" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.username" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.email" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.sent.date" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.activate.date" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.device.model"/></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.device.identifier" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.autoprovision" /></th>
			<th align="left" nowrap><s:text name="monitor.otp.list.description" /></th>
			<th aligh="left" nowrap><s:text name="config.domain" /></th>
		</tr>
		<s:if test="%{totalList.size() == 0}">
			<ah:emptyList />
		</s:if>
		<tiles:insertDefinition name="selectAll" />
		<s:iterator value="page" status="status">
			<tiles:insertDefinition name="rowClass" />
			<tr class="<s:property value="%{#rowClass}"/>">
				<td class="listCheck"><ah:checkItem /></td>
				<td><s:property value="oneTimePassword" /></td>
				<td class="list">&nbsp;<s:property value="userName" /></td>
				<td class="list">&nbsp;<s:property value="emailAddress" /></td>
				<td class="list">&nbsp;<s:property value="dateSent" /></td>
				<td class="list">&nbsp;<s:property value="dateActivate" /></td>
				<td class="list">&nbsp;<s:property value="deviceModelString" /></td>
				<td class="list">&nbsp;<s:property value="macAddressFormat" /></td>
				<td class="list">&nbsp;<s:property value="hiveApAutoProvisionName" /></td>
				<td class="list">&nbsp;<s:property value="description" /></td>
				<s:if test="%{showDomain}">
					<td class="list"><s:property value="%{owner.domainName}" /></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</body>
</html>