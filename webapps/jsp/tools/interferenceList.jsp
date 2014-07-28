<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'spectralAnalysis';
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
function onLoadPage() {
	window.setTimeout(pollHandler, 60000);
}

function pollHandler() {
	var href = window.location.href;
	window.location.replace(href);
	return;
}
</script>

<div id="content"><s:form action="spectralAnalysis">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
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
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th> <s:text name="hm.tool.snp.title.apName" /></th>
					<th> <s:text name="hm.tool.snp.title.devicetype" /></th>
					<th> <s:text name="hm.tool.snp.title.signalMin" /></th>
					<th> <s:text name="hm.tool.snp.title.signalMax" /></th>
					<th> <s:text name="hm.tool.snp.title.time" /></th>
					<th> <s:text name="hm.tool.snp.title.channel" /></th>
					<th> <s:text name="hm.tool.snp.title.centerFreq" /></th>
					<th> <s:text name="hm.tool.snp.title.bandwidth" /></th>
				</tr>
				<s:if test="%{interferenceLst.size() == 0}">
					<ah:emptyList />
				</s:if>
				<s:iterator value="interferenceLst" status="status">
					<s:if test="%{#status.even}">
						<s:set name="orowClass" value="%{'even'}" />
					</s:if>
					<s:else>
						<s:set name="orowClass" value="%{'odd'}" />
					</s:else>
					<tr class="<s:property value="%{#orowClass}"/>">
						<td class="list"><s:property value="apName" /></td>
					 	<td class="list"><s:property value="deviceTypeString" /></td>
						<td class="list"><s:property value="signalMin" /></td>
						<td class="list"><s:property value="signalMax" /></td>
						<td class="list"><s:property value="timeString" /></td>
						<td class="list"><s:property value="channel" /></td>
						<td class="list"><s:property value="centerFreq" /></td>
						<td class="list"><s:property value="bandwidthValue" /></td>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
