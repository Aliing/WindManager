<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'permanentAccount';
var thisOperation;

function submitAction(operation) {
    thisOperation = operation;

    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'activate') {
    	hm.util.checkAndConfirmMultiple('activate');
    } else if(operation == 'email') {
    	hm.util.checkAndConfirmMultiple('email');
    } else {
    	doContinueOper();
    }
}

function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function printAccount() {
	// check item selection
	var inputElements = document.getElementsByName('selectedIds');

	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to print");
		warnDialog.show();
		return;
	}

	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}

	var selectCount = 0;

	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			selectCount++;
		}
	}

	if (selectCount != 1) {
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}

	var boIds = hm.util.getSelectedIds();
	var url = "<s:url action='permanentAccount' includeParams='none'/>"
				+ "?operation=print&userId=" + boIds[0];

	window.open(url, 'printWindow', 'height=500, width=600, menubar=yes,toolbar=no,location=no,directories=no,scrollbars=yes, resizable, top=250,left=400');

}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="permanentAccount">
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
					<td><input type="button" name="ignore" value="Activate"
						class="button" onClick="submitAction('activate');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Print"
						id="printButton"
						class="button" onClick="printAccount();"
						style="display: <s:property value="%{showCreate}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
					<td><input type="button" name="ignore" value="Email"
						class="button" onClick="submitAction('email');"
						style="display: <s:property value="%{showCreate}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
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
						<th><ah:sort name="userName" key="gml.temporary.userName" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
						<s:if test="%{fullMode}">
						<th><ah:sort name="localUserGroup" key="gml.temporary.userGroup" /></th>
					</s:if>
						<s:elseif test="%{easyMode}">
							<th><ah:sort name="localUserGroup" key="gml.temporary.ssid" /></th>
						</s:elseif>
					</s:if>
					<s:if test="%{columnId == 3}">
						<th><ah:sort name="activated" key="gml.permanent.activation" /></th>
					</s:if>
					<s:if test="%{columnId == 6}">
						<th><s:text name="gml.temporary.startTime" /></th>
					</s:if>
					<s:if test="%{columnId == 7}">
						<th><s:text name="gml.temporary.endTime" /></th>
					</s:if>
					<s:if test="%{columnId == 4}">
						<th><ah:sort name="description" key="gml.permanent.description" /></th>
					</s:if>
					<s:if test="%{columnId == 5}">
						<th><ah:sort name="mailAddress" key="gml.temporary.email" /></th>
					</s:if>
					</s:iterator>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck"><ah:checkItem /></td>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
		   						<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="permanentAccount.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="userName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="permanentAccount.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="userName" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list"><s:property value="userGroupName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list"><s:property value="activationValue" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<td class="list"><s:property value="startTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 7}">
								<td class="list"><s:property value="expiredTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list"><s:property value="mailAddress" />&nbsp;</td>
							</s:elseif>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>