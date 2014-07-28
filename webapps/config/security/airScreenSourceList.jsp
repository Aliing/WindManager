<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'airScreenSource';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
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
	document.writeln('<td class="crumb" nowrap><s:property value="%{displayLabel}" /></td>');
}
</script>

<div id="content"><s:form action="airScreenSource">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"></td>
					<td><input type="button" name="ignore" value="Return"
						class="button" onClick="submitAction('return');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table id="hiveTable" cellspacing="0" cellpadding="0" border="0"
				class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<th><ah:sort name="profileName"
						key="config.air.screen.source.name" /></th>
					<th><ah:sort name="type" key="config.air.screen.source.type" /></th>
					<s:if test="%{actionType=='source'}">
						<th><ah:sort name="oui" key="config.air.screen.source.oui" /></th>
						<th><ah:sort name="encryptionMode"
							key="config.air.screen.source.encryption" /></th>
						<th><ah:sort name="authMode"
							key="config.air.screen.source.auth" /></th>
						<th><ah:sort name="minRssi"
							key="config.air.screen.source.rssi.start" /></th>
					</s:if>
					<s:elseif test="%{actionType=='behavior'}">
						<th><ah:sort name="connectionCase" key="config.air.screen.behavior.case" /></th>
						<th><ah:sort name="threshold"
							key="config.air.screen.behavior.threshold" /></th>
						<th><ah:sort name="interval"
							key="config.air.screen.behavior.interval" /></th>
					</s:elseif>
					<s:elseif test="%{actionType=='action'}">
						<th><ah:sort name="interval" key="config.air.screen.action.interval" /></th>
					</s:elseif>
					<th><ah:sort name="comment"
							key="config.air.screen.behavior.comment" /></th>
					<s:if test="%{showDomain}">
						<th><ah:sort name="owner.domainName" key="config.domain" /></th>
					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<s:if
							test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck"><input type="checkbox"
								disabled="disabled" /></td>
						</s:if>
						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<td class="list"><s:property value="profileName" /></td>
						<td class="list"><s:property value="typeString" /></td>
						<s:if test="%{actionType=='source'}">
							<td class="list"><s:property value="macOrOuiString" /></td>
							<td class="list"><s:property value="encryptionModeString" /></td>
							<td class="list"><s:property value="authModeString" /></td>
							<td class="list"><s:property value="rssiString" /></td>
						</s:if>
						<s:elseif test="%{actionType=='behavior'}">
							<td class="list"><s:property value="caseString" /></td>
							<td class="list"><s:property value="threshold" /></td>
							<td class="list"><s:property value="interval" /></td>
						</s:elseif>
						<s:elseif test="%{actionType=='action'}">
							<td class="list"><s:property value="intervalStr" />&nbsp;</td>
						</s:elseif>
						
						<td class="list"><s:property value="comment" /></td>
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
