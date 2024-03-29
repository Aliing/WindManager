<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'radiusUserProfileRule';
var thisOperation;

function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else {
        doContinueOper();
    }   
}
function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content">
<s:form name="radiusUserProfileRule">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><tiles:insertDefinition name="context" /></td>
	</tr>
	<tr>
		<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<input type="button" name="new" value="New" class="button" onClick="submitAction('new');" <s:property value="writeDisabled" />>
					</td>
					<td>
						<input type="button" name="new" value="Clone" class="button" onClick="submitAction('clone');" <s:property value="writeDisabled" />>
					</td>
					<td>
						<input type="button" name="remove" value="Remove" class="button" onClick="submitAction('remove');" <s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><tiles:insertDefinition name="notes" /></td>
	</tr>
	<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll" onClick="hm.util.toggleCheckAll(this);" ></th>
					<th align="left" nowrap><ah:sort name="radiusUserProfileRuleName" key="config.radiusUserProfileRule.name" /></th>
					<th align="left" nowrap><ah:sort name="description" key="config.radiusUserProfileRule.description" /></th>
					<s:if test="%{showDomain}">
					    <th align="left" nowrap><ah:sort name="owner.domainName" key="config.domain" /></th>
					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}" />" >
						<s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
       						<td class="listCheck"><input type="checkbox" disabled /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:if test="%{showDomain}">
       						<td class="list"><a href='<s:url action="radiusUserProfileRule"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/>
       							<s:param name="domainId" value="%{owner.id}"/></s:url>'><s:property value="radiusUserProfileRuleName" /></a></td>
    					</s:if>
    					<s:else>
							<td class="list"><a href='<s:url action="radiusUserProfileRule"><s:param name="operation" value="%{'edit'}" />
													<s:param name="id" value="%{id}" /></s:url>' ><s:property value="radiusUserProfileRuleName" /></a></td>
						</s:else>
						<td class="list"><s:property value="description" />&nbsp;</td>
						<s:if test="%{showDomain}">
						    <td class="list"><s:property value="owner.domainName" /></td>
						</s:if>
					</tr>
				</s:iterator>
			</table>
		</td>
	</tr>
</table>
</s:form>
</div>
