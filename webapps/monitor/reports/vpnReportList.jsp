<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'vpnReportList';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else if (operation == 'run') {
        hm.util.checkAndConfirmRun();
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
-->
</style>

<div id="content"><s:form action="vpnReportList">
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
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Run Now"
						class="button" onClick="submitAction('run');"
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
			<table id ="hiveTable" cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="name" key="report.reportList.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="reportType" key="report.reportList.title.preference" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="excuteType" key="report.reportList.excuteType" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><ah:sort name="emailAddress" key="report.reportList.emailAddress" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">	
							<th><s:text name="report.reportList.startTime" /></th>
						</s:if>
					</s:iterator>
					<s:if test="%{showDomain}">
         				<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<s:if test="%{hasPredefinedReport}">
					<tr>
						<td class="tableClassifyLabel" colspan="100">
							<s:text name="hm.recurreport.list.report.predefined" />
						</td>
					</tr>
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<s:if test="%{defaultFlag}">
						<s:set name="rowStatus" value="%{'defaultFlag'}" />
					</s:if>
					<s:else>
						<s:if test="%{hasPredefinedReport && #rowStatus != ''}">
							<tr>
								<td class="tableClassifyLabel" colspan="100">
									<s:text name="hm.recurreport.list.report.userdefined" />
								</td>
							</tr>
						</s:if>
						<s:set name="rowStatus" value="%{''}" />
					</s:else>
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
										href='<s:url value="vpnReportList.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="vpnReportList.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list"><s:property value="reportTypeShowInGUI" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="excuteTypeString" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list">&nbsp;<s:property value="emailAddress" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list">&nbsp;<s:property value="nextScheduleTimeString" /></td>
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
