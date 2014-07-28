<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
var filterFormName = 'clientSurveyFilter';
function submitFilterAction(operation) {
	if (validateFilter(operation)) {
		document.forms[filterFormName].operation.value = operation;
    	document.forms[filterFormName].submit();
    }
}
function validateFilter(operation) {
	return true;
}
</script>
<div id="leftFilter"><s:form action="clientSurvey"
	id="clientSurveyFilter" name="clientSurveyFilter">
	<s:hidden name="operation" />
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="filterSep" colspan="2">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="sepLine"><img
						src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="filterH1" colspan="2">Filter by</td>
		</tr>
		<tr>
			<td class="filterT1">Map</td>
			<td style="padding-left:4px;"><s:select id="filterMap" name="filterMap" headerKey="-2"
				headerValue="All" list="filterMaps" listKey="id" listValue="value"
				cssStyle="width:128px;" /></td>
		</tr>
		<tr>
			<td colspan="2" class="filterBtn"><input type="button"
				name="ignore" value="Search" class="button"
				onClick="submitFilterAction('search');"></td>
		</tr>
	</table>
</s:form></div>
