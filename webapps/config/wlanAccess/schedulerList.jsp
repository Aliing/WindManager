<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>


<script>
var formName = 'scheduler';
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

<div id="content"><s:form action="scheduler">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="new" value="New" class="button"
						onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>" 
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('scheduler')"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
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
					<th width="150px"><ah:sort name="schedulerName"
						key="config.wlanAccess.scheduler.schedulerName" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
					<th width="150px"><ah:sort name="type"
						key="config.wlanAccess.scheduler.type" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
					<th width="150px"><ah:sort name="beginDate"
						key="config.wlanAccess.scheduler.beginDate" /></th>
					</s:if>
					<s:if test="%{columnId == 4}"><th width="150px"><ah:sort name="endDate"
						key="config.wlanAccess.scheduler.endDate" /></th>
					</s:if>
					<s:if test="%{columnId == 5}"><th width="150px"><ah:sort name="weeks"
						key="config.wlanAccess.scheduler.weeks" /></th>
					</s:if>
					<s:if test="%{columnId == 6}"><th width="150px"><ah:sort name="beginTime"
						key="config.wlanAccess.scheduler.beginTime1" /></th>
					</s:if>
					<s:if test="%{columnId == 7}"><th width="150px"><ah:sort name="endTime"
						key="config.wlanAccess.scheduler.endTime1" /></th>
					</s:if>
					<s:if test="%{columnId == 8}"><th width="150px"><ah:sort name="beginTimeS"
						key="config.wlanAccess.scheduler.beginTime2" /></th>
					</s:if>
					<s:if test="%{columnId == 9}"><th width="150px"><ah:sort name="endTimeS"
						key="config.wlanAccess.scheduler.endTime2" /></th>
					</s:if>
					<s:if test="%{columnId == 10}"><th width="150px"><ah:sort name="description"
						key="config.wlanAccess.scheduler.description" /></th>
					</s:if>
					</s:iterator>
					<%-- added by joseph chen , 05/06/2008 --%>
					<s:if test="%{showDomain}">
					    <th align="left" nowrap><ah:sort name="owner.domainName" key="config.domain" /></th>
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
       						<td class="list"><a href='<s:url action="scheduler"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/>
       							<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property value="schedulerName" /></a></td>
    					</s:if>
    					<s:else>
							<td class="list"><a
								href='<s:url action="scheduler"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
								value="schedulerName" /></a></td>
						</s:else>
						</s:if>
						<s:if test="%{columnId == 2}">
						<td class="list"><s:property value="recurrent" /></td>
						</s:if>
						<s:if test="%{columnId == 3}">
						<td class="list"><s:property value="beginDate" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 4}">
						<td class="list"><s:property value="endDate" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 5}">
						<td class="list"><s:property value="weeks" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 6}">
						<td class="list"><s:property value="beginTime" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 7}">
						<td class="list"><s:property value="endTime" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 8}">
						<td class="list"><s:property value="beginTimeS" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 9}">
						<td class="list"><s:property value="endTimeS" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 10}">
						<td class="list"><s:property value="description" />&nbsp;</td>
						</s:if>
						</s:iterator>
						<%-- added by joseph chen , 05/06/2008 --%>
						<s:if test="%{showDomain}">
						    <td class="list"><s:property value="owner.domainName" /></td>
						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
