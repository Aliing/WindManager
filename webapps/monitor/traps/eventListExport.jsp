<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<%
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition",
			"attachment; filename=\"events.xls\"");
%>

<script>
function onLoadEvent() {
}
function onUnloadEvent() {
}
</script>

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<%--
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  --%>

<link rel="stylesheet"
	href="https://<%=request.getServerName()%>:<%=request.getServerPort()%>/<%=request.getContextPath()%>/css/hm.css"
	type="text/css" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="https://<%=request.getServerName()%>:<%=request.getServerPort()%>/<%=request.getContextPath()%>/yui/fonts/fonts-min.css" />

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>

<%-- 
<script src="<s:url value="/yui/yahoo/yahoo-min.js" />"></script>
<script src="<s:url value="/yui/dom/dom-min.js" />"></script>
<script src="<s:url value="/yui/event/event-min.js" />"></script>
  Bundles all 3 above --%>
<script type="text/javascript"
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none"/>"></script>

<script
	src="<s:url value="/yui/connection/connection-min.js" includeParams="none"/>"></script>
</head>
<body class="body_bg yui-skin-sam" onload="onLoadEvent();"
	onunload="onUnloadEvent();" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0">
<table cellspacing="0" cellpadding="0" border="0" width="100%"
	class="view">
	<tr>
		<th class="check"></th>
		<th align="left" nowrap><s:text name="monitor.alarms.apName" /></th>
		<th align="left" nowrap><s:text name="monitor.alarms.apId" /></th>
		<th id="codeHeader" align="left" nowrap><s:text
			name="monitor.alarms.alarmTime" /></th>
		<th align="left" nowrap><s:text name="monitor.alarms.alarmDesc" /></th>
		<th align="left" nowrap><s:text name="monitor.alarms.objectName" /></th>
		<s:if test="%{showDomain}">
			<th aligh="left" nowrap><s:text name="config.domain" /></th>
		</s:if>
	</tr>
	<s:if test="%{page.size() == 0}">
		<ah:emptyList />
	</s:if>
	<tiles:insertDefinition name="selectAll" />
	<s:iterator value="page" status="status">
		<tiles:insertDefinition name="rowClass" />
		<tr class="<s:property value="%{#rowClass}"/>">
			<td class="listCheck"><ah:checkItem /></td>
			<td class="list"><s:property value="%{apName}" /></td>
			<td class="list" align="left">"<s:property value="%{apId}" />"</td>
			<td class="list" nowrap><s:property value="%{trapTimeExcel}" /></td>
			<td class="list"><s:property value="%{trapDesc}" /></td>
			<td class="list"><s:property value="%{objectName}" /></td>
			<s:if test="%{showDomain}">
				<td class="list"><s:property value="%{owner.domainName}" /></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
</body>
</html>
