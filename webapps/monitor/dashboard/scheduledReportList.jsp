<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
<!--
td.tableClassifyLabel{
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-style: normal;
	font-weight: bold;
	color: #fff;
	background-color: #333333;
	padding: 2px 4px;
	cursor: default;
}
.status-icon {
	height: 16px;
	width: 16px;
	margin: 0 auto;
}
.status-icon.status0 {
	background: url(images/status0.png) no-repeat scroll 0 0 transparent;
}
.status-icon.status1 {
	background: url(images/status1.png) no-repeat scroll 0 0 transparent;
}
.status-icon.status2 {
	background: url(images/status5.png) no-repeat scroll 0 0 transparent;
}
-->
</style>
<script>
var formName = 'recurReport';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else if (operation == 'enable'){
    	hm.util.checkAndConfirmTriggerReport(true);
    } else if (operation == 'disable'){
    	hm.util.checkAndConfirmTriggerReport(false);
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

<div id="content"><s:form action="recurReport">
	<s:hidden name="editType"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="<s:text name="common.button.create.new" />"
						class="button" onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="common.button.enable" />"
						class="button" onClick="submitAction('enable');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="common.button.disable" />"
						class="button" onClick="submitAction('disable');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="common.button.clone" />"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="common.button.remove" />"
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
				<table cellspacing="0" cellpadding="0" border="0" width="800px"	class="view">
					<tr>
						<th class="check"><input type="checkbox" id="checkAll"
							onClick="hm.util.toggleCheckAll(this);"></th>
						<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
						<th align="left" nowrap><ah:sort name="dashName"
							key="hm.recurreport.list.report.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
						<th align="left" nowrap><ah:sort name="reportScheduleStatus"
							key="hm.recurreport.list.report.status" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
						<th align="left" nowrap><ah:sort name="refrequency"
							key="hm.recurreport.list.report.period" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
						<th align="left" nowrap><ah:sort name="reEmailAddress"
							key="hm.recurreport.list.report.email" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
						<th align="left" nowrap><s:text name="hm.recurreport.list.report.description" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
						<th align="left" nowrap><s:text name="geneva_04.recurreport.list.report.next.report.on" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">
							<th align="left" nowrap><s:text name="geneva_04.missionux.report.list.title.templateName" /></th>
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
								<s:if test="false">
									<td class="list" nowrap><s:property value="dashName" /></td>
								</s:if>
								<s:else>
									<s:if test="%{showDomain}">
										<td class="list" nowrap><a
											href='<s:url value="recurReport.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
											value="dashName" /></a></td>
									</s:if>
									<s:else>
										<td class="list" nowrap><a
											href='<s:url value="recurReport.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
											value="dashName" /></a></td>
									</s:else>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
							<td class="list">
								<div class='status-icon status<s:property value="%{reportScheduleStatus}"/>'></div>
							</td>
							</s:if>
							<s:if test="%{columnId == 3}">
							<td class="list"><s:property value="%{refrequencyString}" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
							<td class="list" nowrap><s:property value="%{reEmailAddress}" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">
							<td class="list"><s:property value="%{description}" /></td>
							</s:if>
							<s:if test="%{columnId == 6}">
							<td class="list"><s:property value="%{dashPDFReportTimeString}" /></td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list"><s:property value="%{dashRelatedTemplateString}" /></td>
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
