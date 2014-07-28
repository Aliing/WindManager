<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'serviceFilter';
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
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="serviceFilter">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="New"
						class="button" onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>"
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('serviceFilter')"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table id="hiveTable" cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="filterName" key="config.serviceFilter.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="enableSSH" key="config.serviceFilter.enableSSH" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="enableTelnet" key="config.serviceFilter.enableTelnet" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><ah:sort name="enablePing" key="config.serviceFilter.enablePing" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
							<th><ah:sort name="enableSNMP" key="config.serviceFilter.enableSNMP" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
							<th><ah:sort name="interTraffic" key="config.serviceFilter.inter.station.traffic" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">
							<th><ah:sort name="description" key="config.serviceFilter.description" /></th>
						</s:if>
					</s:iterator>
					<s:if test="%{showDomain}">
         				<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>

				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
					    <s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
					       <td class="listCheck"><input type="checkbox" disabled /></td>
					   	</s:if>
					   	<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="serviceFilter.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="filterName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="serviceFilter.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="filterName" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list"><s:property value="enableSSH" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="enableTelnet" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list"><s:property value="enablePing" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list"><s:property value="enableSNMP" /></td>
							</s:if>
							<s:if test="%{columnId == 6}">
								<td class="list"><s:property value="interTraffic" /></td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list">&nbsp;<s:property value="description" /></td>
   							</s:if>
   						</s:iterator>
   						<s:if test="%{showDomain}">
         					<td class="list"><s:property value="%{owner.domainName}" /></td>
   						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
