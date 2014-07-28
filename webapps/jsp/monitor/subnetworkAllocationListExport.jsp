<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<%
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition",
			"attachment; filename=\"subnetwork allocation.xls\"");
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
	<table>
		<tr>
			<td class="title"><s:text name="monitor.subnetwork.allocation.networkname"/></td>
			<s:if test="%{currentNetworkId > 0}">
			<td class="title"><s:text name="config.vpn.network.webSecurity"/></td>
			<td class="title"><s:text name="monitor.subnetwork.allocation.subnetwork.count"/></td>
			</s:if>
		</tr>
		<tr>
			<td align="left"><s:property value="currentNetworkName" /></td>
			<td align="left"><s:property value="webSecurityStr" /></td>
			<td align="left"><s:property value="subNetworkCountStr" /></td>
		</tr>
	</table>	
	<table cellspacing="0" cellpadding="0" border="0" width="100%" class="view">
		<tr>
			<th class="check"></th>
			<th align="left" nowrap><s:text name="hiveAp.hostName" /></th>
			<th align="left" nowrap><s:text name="hiveAp.macaddress" /></th>
			<th align="left" nowrap><s:text name="monitor.subnetwork.allocation" /></th>
			<th align="left" nowrap><s:text name="config.userprofile.network.object" /></th>
			<th align="left" nowrap><s:text name="monitor.subnetwork.allocation.dhcppool" /></th>
			<th align="left" nowrap><s:text name="hiveAp.gateway" /></th>
			<th align="left" nowrap><s:text name="hiveAp.classification.tag1" /></th>
			<th align="left" nowrap><s:text name="hiveAp.classification.tag2" /></th>
			<th align="left" nowrap><s:text name="hiveAp.classification.tag3" /></th>
			<s:if test="%{showDomain}">
				<th aligh="left" nowrap><s:text name="config.domain" /></th>
			</s:if>
		</tr>
		<s:if test="%{totalList.size() == 0}">
			<ah:emptyList />
		</s:if>
		<tiles:insertDefinition name="selectAll" />
		<s:iterator value="totalList" status="status">
			<tiles:insertDefinition name="rowClass" />
			<tr class="<s:property value="%{#rowClass}"/>">
				<td class="listCheck"><ah:checkItem /></td>
				<td class="list"><s:property value="%{relativeAP.hostName}" /></td>
				<td class="list" align="left"><s:property value="%{hiveApMac}" /></td>
				<td class="list"><s:property value="%{network}" /></td>
				<td class="list"><s:property value="%{parentNetworkStr}" /></td>
				<td class="list"><s:property value="%{dhcpPool}" /></td>
				<td class="list"><s:property value="%{firstIp}" /></td>
				<td class="list"><s:property value="%{relativeAP.classificationTag1}" /></td>
				<td class="list"><s:property value="%{relativeAP.classificationTag2}" /></td>
				<td class="list"><s:property value="%{relativeAP.classificationTag3}" /></td>
				<s:if test="%{showDomain}">
					<td class="list"><s:property value="%{owner.domainName}" /></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</body>
</html>