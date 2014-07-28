<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'radiusProxy';
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

<div id="content"><s:form action="radiusProxy">
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
						class="button" onclick="hm.paintbrush.triggerPaintbrush('radiusProxy')"
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
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="proxyName" key="config.radiusProxy.name" /></th>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<th><ah:sort name="proxyFormat" key="config.radiusProxy.format" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 3}">
							<th><ah:sort name="retryDelay" key="config.radiusProxy.retry.delay" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 4}">
							<th><ah:sort name="retryCount" key="config.radiusProxy.retry.count" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 5}">
							<th><ah:sort name="deadTime" key="config.radiusProxy.dead.time" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 6}">
							<th><ah:sort name="description" key="config.ipFilter.description" /></th>
						</s:elseif>
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
										href='<s:url value="radiusProxy.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="proxyName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="radiusProxy.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="proxyName" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list">
									<s:property value="formatStr" />
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list">
									<s:property value="retryDelay" />
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list">
									<s:property value="retryCount" />
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list">
									<s:property value="deadTime" />
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:elseif>
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
