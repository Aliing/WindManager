<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<style type="text/css">
td.title {
	font-weight: bold;
}
</style>
<script>
var formName = 'subnetworkAllocations';
var thisOperation;
function submitAction(operation, hiveApId) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'export') {
	    document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
    } else if (operation == 'editAp') {
        document.forms[formName].selctedHiveApId.value = hiveApId;
        doContinueOper();
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

var YUE = YAHOO.util.Event;
YUE.onDOMReady(function() {
	var networkId = <s:property value='currentNetworkId' />;
	if(networkId > 0) {
		hm.util.show("vlanCol");
		hm.util.show("webSecCol");
		hm.util.show("countCol");
	}
});
</script>

<div id="content"><s:form action="subnetworkAllocations">
	<s:hidden name="selctedHiveApId"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td style="padding-left: 20px;">
			<table>
				<tr>
					<td class="title" width="180px"><s:text name="monitor.subnetwork.allocation.networkname"/></td>
					<td class="title" width="100px" id="webSecCol" style="display: none;"><s:text name="config.vpn.network.webSecurity"/></td>
					<td class="title" width="200px" id="countCol" style="display: none;"><s:text name="monitor.subnetwork.allocation.subnetwork.count"/></td>
				</tr>
				<tr>
					<td>
						<s:select list="networkList" cssStyle="width: 120px;"
							name="currentNetworkId"
							title="Change the network to see the subnetwork allocation list."
							onchange="submitAction('changeType');"
							listKey="id" listValue="value"/>
					</td>
					<td id="webSecuValue"><s:property value="webSecurityStr" /></td>
					<td id="countValue"><s:property value="subNetworkCountStr" /></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="export" value="Export"
						class="button" onClick="submitAction('export');"
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
							<th align="left" nowrap><s:text name="hiveAp.hostName" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">	
							<th align="left" nowrap><ah:sort name="hiveApMac" key="hiveAp.macaddress" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">	
							<th align="left" nowrap><ah:sort name="localNetwork" key="monitor.subnetwork.localNetwork" /></th>
						</s:if>
						<s:if test="%{columnId == 10}">	
							<th align="left" nowrap><ah:sort name="network" key="monitor.subnetwork.natNetwork" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">	
							<th align="left" nowrap><ah:sort name="parentNetwork" key="config.userprofile.network.object" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">	
							<th align="left" nowrap><s:text name="monitor.subnetwork.allocation.dhcppool" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">	
							<th align="left" nowrap><ah:sort name="firstIp" key="hiveAp.gateway" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">	
							<th align="left" nowrap><s:text name="hiveAp.classification.tag1" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">	
							<th align="left" nowrap><s:text name="hiveAp.classification.tag2" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">	
							<th align="left" nowrap><s:text name="hiveAp.classification.tag3" /></th>
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
									<s:if test="%{null == relativeAP.id}">
		       						<td class="list"><s:property value="relativeAP.hostName" /></td>
									</s:if>
									<s:else>
		       						<td class="list">
		       							<a href='javascript: void(0);' onclick='submitAction("editAp", <s:property value="relativeAP.id"/>);return false;'>
		       							<s:property value="relativeAP.hostName" /></a></td>
									</s:else>
		    					</s:if>
		    					<s:else>
									<s:if test="%{null == relativeAP.id}">
									<td class="list"><s:property value="relativeAP.hostName" /></td>
									</s:if>
									<s:else>
									<td class="list">
										<a href='javascript: void(0);' onclick='submitAction("editAp", <s:property value="relativeAP.id"/>);return false;'>
										<s:property value="relativeAP.hostName" /></a></td>
									</s:else>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">	
								<td class="list"><s:property value="hiveApMac" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">	
								<td class="list"><s:property value="localNetwork" /></td>
							</s:if>
							<s:if test="%{columnId == 10}">	
								<td class="list"><s:property value="network" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">	
								<td class="list"><s:property value="prentLocalNetworkStr" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">	
								<td class="list"><s:property value="dhcpPool" /></td>
							</s:if>
							<s:if test="%{columnId == 6}">	
								<td class="list"><s:property value="firstIp" /></td>
							</s:if>
							<!-- tag1 -->
							<s:if test="%{columnId == 7}">	
								<td class="list"><s:if test="%{null !=relativeAP}"><s:property value="relativeAP.classificationTag1" /></s:if></td>
							</s:if>
							<!-- tag2 -->
							<s:if test="%{columnId == 8}">	
								<td class="list"><s:if test="%{null !=relativeAP}"><s:property value="relativeAP.classificationTag2" /></s:if></td>
							</s:if>
							<!-- tag3 -->
							<s:if test="%{columnId == 9}">	
								<td class="list"><s:if test="%{null !=relativeAP}"><s:property value="relativeAP.classificationTag3" /></s:if></td>
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