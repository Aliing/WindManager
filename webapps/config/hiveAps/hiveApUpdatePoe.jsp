<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>
var formName = 'hiveApUpdate';

function onLoadPage(){
	var listElement = document.getElementById('checkAll');
	hm.util.reportFieldError(listElement, '<s:text name="hiveAp.update.poe.tips"/>');
}

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(!validateApSelection(operation)){
		return false;
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'updatePoe'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				break;
			}
		}
		if(!isSelected){
			var listElement = document.getElementById('checkAll');
			hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
			return false;
		}
	}
	return true;
}

function toggleCheckAllHiveAps(cb){
	var cbs = document.getElementsByName('hiveApIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.poe"/> </td>');
}
</script>
<div id="content"><s:form action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('updatePoe');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
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
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="550px">
				<tr>
					<td style="padding: 4px 10px 10px 10px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
						<td>
							<div>
								<fieldset><legend><s:text name="hiveAp.update.poe.label"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
								<td class="labelT1" nowrap><s:text name="hiveAp.update.poe.label" />&nbsp;&nbsp;&nbsp;&nbsp;
								<s:select name="maxPower" id="maxPower" list="%{maxPowers}"
					            listKey="key" listValue="value"/></td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td colspan="20" class="noteInfo" style="padding-left: 10px;"><s:text name="hiveAp.update.poe.note" /></td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								</table>
								</fieldset>
							</div>
						</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="softVer" key="monitor.hiveAp.sw" /></th>
										<th align="left"><ah:sort name="location" key="hiveAp.location" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck"><ah:checkItem /></td>
											<td class="list"><s:property value="hostName" /></td>
											<td class="list"><s:property value="macAddress" /></td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
											<td class="list"><s:property value="location" />&nbsp;</td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>