<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'vpnNetworks';
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
function changeSubItem(selectElement, networkId) {
	var selectedItemValue;
	if("localIpNetworkId" == selectElement.id) {
		document.getElementById("ipNetworkId").selectedIndex = selectElement.selectedIndex;
		selectedItemValue = document.getElementById("ipNetworkId").options[selectElement.selectedIndex].value;
	} else if ("ipNetworkId" == selectElement.id){
		document.getElementById("localIpNetworkId").selectedIndex = selectElement.selectedIndex;
		selectedItemValue = selectElement.options[selectElement.selectedIndex].value;
	} else {
		alert("error");
	}
	var succChangeSubItem = function(o){
		eval("var details = " + o.responseText);
			var dhcpCell = Get("subDHCPCell_"+networkId);
			if(dhcpCell) {
				dhcpCell.innerHTML = details.dhcp;
			}
			var ntpCell = Get("subNTPServerCell_"+networkId);
			if(ntpCell) {
				ntpCell.innerHTML = details.ntpServer;
			}
			var leaseTImeCell = Get("subLeaseTimeCell_"+networkId);
			if(leaseTImeCell) {
				leaseTImeCell.innerHTML = details.leaseTime;
			}
			var domainCell = Get("subDomainNameCell_"+networkId);
			if(domainCell) {
				domainCell.innerHTML = details.domainName;
			}
	}
	var failChangeSubItem = function(o){}
	var url = "<s:url action='vpnNetworks' includeParams='none' />" + "?operation=getSubNetworkItem"
			+ "&selectedNetworkId=" + networkId +"&selectedSubItem="+selectedItemValue
			+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('get',
			url, {success : succChangeSubItem, failure : failChangeSubItem, timeout: 60000}, null);
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="vpnNetworks">
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
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view" id="networkTable" width="100%">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="networkName"
								key="config.vpn.network.name" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="webSecurity"
								key="config.vpn.network.webSecurity" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><s:text name="config.vpn.network.dnsService" /></th>
						</s:if>
						<s:if test="%{columnId == 12}">
							<th><s:text name="config.vpn.network.localSubnetwork" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
							<th><s:text name="config.vpn.network.subnetwork" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
							<th><s:text name="config.vpn.network.enableDhcpTitle" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">
							<th><s:text name="config.vpn.network.ntpServerIp" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">
							<th><s:text name="config.vpn.network.leaseTime" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">
							<th><s:text name="config.vpn.network.domainName" /></th>
						</s:if>
						<s:if test="%{columnId == 10}">
							<th><ah:sort name="description"
								key="config.vpn.network.description" /></th>
						</s:if>
                        <s:if test="%{columnId == 11}">
                            <th><ah:sort name="networkType"
                                key="config.vpn.network.type" /></th>
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
								<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url action="vpnNetworks"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="networkName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url action="vpnNetworks"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="networkName" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="webSecurityString" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url action="dnsService.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{vpnDnsService.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="vpnDnsService.serviceName" /></a>&nbsp;</td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url action="dnsService.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{vpnDnsService.id}"/></s:url>'><s:property
										value="vpnDnsService.serviceName" /></a>&nbsp;</td>
								</s:else>
							</s:if>
							<s:if test="%{networkType != 2}">
							<s:if test="%{columnId == 12}">
								<td class="list">
								<s:select list="subItems" id="localIpNetworkId" listKey="localIpNetwork" listValue="localIpNetwork" cssStyle="width: 160px;" onchange="changeSubItem(this, %{#pageRow.id});"/>&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list">
								<s:select list="subItems" id="ipNetworkId" listKey="ipNetwork" listValue="ipNetwork" cssStyle="width: 160px;" onchange="changeSubItem(this, %{#pageRow.id});"/>&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 6}">
								<td class="list" id="subDHCPCell_<s:property value="#pageRow.id"/>"><s:property value="subItems.get(0).enableDhcpStr" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list" id="subNTPServerCell_<s:property value="#pageRow.id"/>"><s:property value="subItems.get(0).ntpServerIp" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 8}">
								<td class="list" id="subLeaseTimeCell_<s:property value="#pageRow.id"/>"><s:property value="subItems.get(0).leaseTimeStr" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 9}">
								<td class="list" id="subDomainNameCell_<s:property value="#pageRow.id"/>"><s:property value="subItems.get(0).domainName" />&nbsp;</td>
							</s:if>
							</s:if>
							<s:else>
							<s:if test="%{columnId == 12}">
								<td class="list"><s:select list="subItems" listKey="localIpNetwork" listValue="localIpNetwork" cssStyle="width: 160px;"/>&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list"><s:select list="subItems" listKey="ipNetwork" listValue="ipNetwork" cssStyle="width: 160px;"/>&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 6}">
								<td class="list"><s:property value="enableDhcpString" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list"><s:property value="ntpServerIp" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 8}">
								<td class="list"><s:property value="leaseTimeString" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 9}">
								<td class="list"><s:property value="domainName" />&nbsp;</td>
							</s:if>
							</s:else>
							<s:if test="%{columnId == 10}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:if>
                            <s:if test="%{columnId == 11}">
                                <td class="list"><s:property value="networkTypeString" />&nbsp;</td>
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
