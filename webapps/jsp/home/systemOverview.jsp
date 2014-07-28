<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<tiles:insertDefinition name="flashHeader" />

<script language='JavaScript' type='text/javascript'>
function fromFlash(f) {
	alert("from flash: " + f);
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>
<div id="content"><s:form action="systemOverview">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td style="padding-top: 4px;">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td><tiles:insertDefinition name="flash" /></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
