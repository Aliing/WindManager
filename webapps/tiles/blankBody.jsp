<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
function insertPageContext() {
<s:if test="%{selectedL2Feature == null}">
	document.writeln('<td class="crumb" nowrap>...</td>');
</s:if>
<s:else>
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
</s:else>
}
</script>
<s:form action="hive">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
	</table>
	<s:hidden name="operation" />
</s:form>
