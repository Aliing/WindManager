<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'userProfiles';
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
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content"><s:form action="userProfiles">
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
						class="button" onclick="hm.paintbrush.triggerPaintbrush('userProfiles')"
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
						<th align="left" nowrap="nowrap"><ah:sort name="userProfileName" key="config.userprofile.name" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
						<th align="left" nowrap="nowrap"><ah:sort name="attributeValue" key="config.userprofile.attribute" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
						<th align="left" nowrap="nowrap"><ah:sort name="vlan.vlanName" key="config.userprofile.vlan" /></th>
					</s:if>
					<s:if test="%{columnId == 4}">
						<th align="left" nowrap="nowrap"><ah:sort name="blnUserManager" key="config.userprofile.userManager.title" /></th>
					</s:if>
					<s:if test="%{columnId == 5}">
						<th align="left" nowrap="nowrap"><ah:sort name="qosRateControl" key="config.userprofile.qoss" /></th>
					</s:if>
					<s:if test="%{columnId == 6}">
						<th align="left" nowrap="nowrap"><s:text name="config.userprofile.tunnels" /></th>
					</s:if>
					<s:if test="%{columnId == 7}">
						<th align="left" nowrap="nowrap"><ah:sort name="description" key="config.userprofile.description" /></th>
					</s:if>
					<s:if test="%{columnId == 8}">
						<th align="left" nowrap="nowrap"><ah:sort name="macPolicyFrom" key="config.userprofile.mac.from.policy" /></th>
					</s:if>
					<s:if test="%{columnId == 9}">
						<th align="left" nowrap="nowrap"><ah:sort name="macPolicyTo" key="config.userprofile.mac.to.policy" /></th>
					</s:if>
					<s:if test="%{columnId == 10}">
						<th align="left" nowrap="nowrap"><ah:sort name="ipPolicyFrom" key="config.userprofile.ip.from.policy" /></th>
					</s:if>
					<s:if test="%{columnId == 11}">
						<th align="left" nowrap="nowrap"><ah:sort name="ipPolicyTo" key="config.userprofile.ip.to.policy" /></th>
					</s:if>
					<s:if test="%{columnId == 12}">
						<th align="left" nowrap="nowrap"><ah:sort name="userProfileAttribute" key="config.userprofile.attributes" /></th>
					</s:if>
					<s:if test="%{columnId == 13}">
						<th align="left" nowrap="nowrap"><ah:sort name="guarantedAirTime" key="config.userprofile.guarantedAirTime" /></th>
					</s:if>
					<s:if test="%{columnId == 14}">
						<th align="left" nowrap="nowrap"><ah:sort name="enableShareTime" key="config.userprofile.enableShareTime" /></th>
					</s:if>
					<s:if test="%{columnId == 15}">
						<th align="left" nowrap="nowrap"><ah:sort name="slaEnable" key="config.userprofile.sla.enable" /></th>
					</s:if>
					</s:iterator>
					<%-- added by joseph chen , 05/06/2008 --%>
					<s:if test="%{showDomain}">
					    <th align="left" nowrap="nowrap"><ah:sort name="owner.domainName" key="config.domain" /></th>
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
       						<td class="listCheck"><input type="checkbox" disabled="disabled" /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
						<s:if test="%{showDomain}">
       						<td class="list"><a href='<s:url action="userProfiles"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/>
       							<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property value="userProfileName" /></a></td>
    					</s:if>
    					<s:else>
							<td class="list"><a
								href='<s:url action="userProfiles"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
								value="userProfileName" /></a></td>
						</s:else>
						</s:if>
						<s:if test="%{columnId == 2}">
							<td class="list"><s:property value="attributeValue" /></td>
						</s:if>
						<s:if test="%{columnId == 3}">
							<td class="list"><s:property value="vlanValue" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 4}">
							<td class="list"><s:property value="displayUserManager" /></td>
						</s:if>
						<s:if test="%{columnId == 5}">
							<td class="list"><s:property value="qosValue" /></td>
						</s:if>
						<s:if test="%{columnId == 6}">
							<td class="list"><s:property value="tunnelUsedString" /></td>
						</s:if>
						<s:if test="%{columnId == 7}">
							<td class="list"><s:property value="description" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 8}">
							<td class="list"><s:property value="macPolicyFromValue" /></td>
						</s:if>
						<s:if test="%{columnId == 9}">
							<td class="list"><s:property value="macPolicyToValue" /></td>
						</s:if>
						<s:if test="%{columnId == 10}">
							<td class="list"><s:property value="ipPolicyFromValue" /></td>
						</s:if>
						<s:if test="%{columnId == 11}">
							<td class="list"><s:property value="ipPolicyToValue" /></td>
						</s:if>
						<s:if test="%{columnId == 12}">
							<td class="list"><s:property value="attributeGroupValue" />&nbsp;</td>
						</s:if>
						<s:if test="%{columnId == 13}">
							<td class="list"><s:property value="guarantedAirTimeValue" /></td>
						</s:if>
						<s:if test="%{columnId == 14}">
							<td class="list"><s:property value="enableShareTimeValue" /></td>
						</s:if>
						<s:if test="%{columnId == 15}">
							<td class="list"><s:property value="slaEnable" /></td>
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
