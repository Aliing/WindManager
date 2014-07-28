<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<style type="text/css">
td.title {
	font-weight: bold;
}
</style>
<script>
var formName = 'oneTimePassword';
var thisOperation;
function submitAction(operation, hiveApId) {
    thisOperation = operation;
    if (operation == 'assign') {
        hm.util.checkAndConfirmAssign();
    }else if(operation == "send"){
    	 hm.util.checkAndConfirmMultiple('send');
    }else if(operation == "revoke"){
    	 hm.util.checkAndConfirmMultiple('revoke');
    }else if (operation == 'export') {
	    document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
    } else {
        doContinueOper();
    }   
}
function doContinueOper() {
	showProcessing();
	if (thisOperation == 'assign'){
    	var selectedIds = hm.util.getSelectedIds();
    	if (selectedIds.length == 1) {
    		thisOperation = "edit";
    		document.forms[formName].id.value = selectedIds[0];
    	}
    }
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}

var YUE = YAHOO.util.Event;
YUE.onDOMReady(function() {
	
});
</script>

<div id="content"><s:form action="oneTimePassword">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="generate" value="Generate"
						class="button" onClick="openGenerateOTP();"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="assign" value="Assign"
						class="button" onClick="submitAction('assign');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="send" value="Send"
						class="button" onClick="submitAction('send');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="revoke" value="Revoke"
						class="button" onClick="submitAction('revoke');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="import" value="Import"
						class="button" onClick="submitAction('import');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="export" value="Export"
						class="button" onClick="submitAction('export');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="filter" value="Filter"
						class="button" onClick="openFilterOverlay();"
						<s:property value="writeDisabled" />>
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
			<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><s:text name="monitor.otp.list.password" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">	
							<th align="left" nowrap><ah:sort name="userName" key="monitor.otp.list.username" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">	
							<th align="left" nowrap><s:text name="monitor.otp.list.email" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">	
							<th align="left" nowrap><ah:sort name="dateSentStamp" key="monitor.otp.list.sent.date" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">	
							<th align="left" nowrap><ah:sort name="dateActivateStamp" key="monitor.otp.list.activate.date" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">	
							<th align="left" nowrap><s:text name="monitor.otp.list.device.model"/></th>
						</s:if>
						<s:if test="%{columnId == 7}">	
							<th align="left" nowrap><s:text name="monitor.otp.list.device.identifier" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">	
							<th align="left" nowrap><s:text name="monitor.otp.list.autoprovision" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">	
							<th align="left" nowrap><s:text name="monitor.otp.list.description" /></th>
						</s:if>
					</s:iterator>
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
						<s:if
							test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck">
								<input type="checkbox" disabled />
							</td>
						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="oneTimePassword.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="oneTimePassword" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="oneTimePassword.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="oneTimePassword" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">	
								<td class="list">&nbsp;<s:property value="userName" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">	
								<td class="list">&nbsp;<s:property value="emailAddress" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">	
								<td class="list">&nbsp;<s:property value="dateSent" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">	
								<td class="list">&nbsp;<s:property value="dateActivate" /></td>
							</s:if>
							<s:if test="%{columnId == 6}">	
								<td class="list">&nbsp;<s:property value="deviceModelString" /></td>
							</s:if>
							<s:if test="%{columnId == 7}">	
								<td class="list">&nbsp;<s:property value="macAddressFormat" /></td>
							</s:if>
							<s:if test="%{columnId == 8}">	
								<td class="list">&nbsp;<s:property value="hiveApAutoProvisionName" /></td>
							</s:if>
							<s:if test="%{columnId == 9}">	
								<td class="list">&nbsp;<s:property value="description" /></td>
							</s:if>
						</s:iterator>
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
<tiles:insertDefinition name="oneTimePasswordFilter" />
<tiles:insertDefinition name="oneTimePasswordGenerate" /> 