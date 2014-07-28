<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<div>
	<table class="view" border="0" cellspacing="0" cellpadding="0"
		style="table-layout: fixed; width: 100%;margin-top: 0px">
		<tr class="even" style="height: 30px">
			<s:if test="%{showType==0}">
				<td width="150px"><s:text name="config.vpn.subnet.ipMappingSerarch.branchNetwork"></s:text></td>
			</s:if>
			<s:else>
				<td width="150px"><s:text name="config.vpn.subnet.ipMappingSerarch.positionId"></s:text> </td>
			</s:else>
			<td><s:text name="config.vpn.subnet.ipMappingSerarch.ipAddress"></s:text> </td>
		</tr>
		<s:iterator value="%{branchResult}" status="status">
			<tiles:insertDefinition name="rowClass" />
			<tr class="<s:property value="%{#rowClass}"/>" style="height: 25px">
				<td><s:property value="id" /></td>
				<td><s:property value="value" /></td>
			</tr>
		</s:iterator>
	</table>
</div>
