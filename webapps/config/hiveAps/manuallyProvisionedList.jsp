<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>

var formName = 'hiveAp';
var thisOperation;
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'multiEdit') {
		hm.util.checkAndConfirmModify();
	}else if (operation == 'remove') {
		hm.util.checkAndConfirmDelete();
	}else if (operation == 'accept') {
		hm.util.checkAndConfirmAccept();
	}else if (operation == 'clone') {
		hm.util.checkAndConfirmClone();
	}else if (validate(operation)) {
		doContinueOper();
	}
}

function doContinueOper() {
	showProcessing();
	//change operation name while multiEdit single one.
	if(thisOperation == 'multiEdit'){
		var selectedIds = hm.util.getSelectedIds();
		if (selectedIds.length == 1) {
			thisOperation = "edit";
			document.forms[formName].id.value = selectedIds[0];
		}
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function validate(operation) {
	return true;
}
function onLoadPage() {
	startHiveApPagingTimer();
}
function onUnloadPage() {
	clearTimeout(hiveApPagingTimeoutId);
}
var hiveApPagingLiveCount = 0;
var hiveApPagingTimeoutId;
function startHiveApPagingTimer() {
	var interval = 10;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (hiveApPagingLiveCount++ < total) {
		hiveApPagingTimeoutId = setTimeout("pollHiveApPagingCache()", interval * 1000);  // seconds
	}
}
function pollHiveApPagingCache() {
	var url = "<s:url action="hiveAp" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateHiveAp }, null);
}

//cached value for refresh which is updated when overlay is showing or item be selected
var cachedRefresh = false;
function updateHiveAp(o) {
	eval("var updates = " + o.responseText);

	var unallowRefresh = isAnyItemSelected();

	for (var i = 0; i < updates.length; i++) {
		if (updates[i].id < 0) {
			if(unallowRefresh){
				cachedRefresh = true;
			}else{
				submitAction('refreshFromCache');
				return;
			}
		}
	}
	if(!unallowRefresh && cachedRefresh){
		submitAction('refreshFromCache');
		return;
	}
	startHiveApPagingTimer();
}

function isAnyItemSelected(){
	var selectedIds = hm.util.getSelectedIds();
	return selectedIds.length > 0 ? true : false;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="hiveAp">
	<s:hidden name="cacheId" />
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
					<td><input type="button" name="ignore" value="New(Full)"
						class="button" onClick="submitAction('new2');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="New(Easy)"
						class="button" onClick="submitAction('new3');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Modify"
						class="button" onClick="submitAction('multiEdit');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Accept"
						class="button" onClick="submitAction('accept');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<s:if test="%{!oEMSystem}">
					<td><input type="button" name="ignore" value="Import"
						class="button" onClick="submitAction('importNew');"
						<s:property value="writeDisabled" />></td>
					</s:if>
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
							<th><ah:sort name="hostName" key="hiveAp.hostName" /></th>
						</s:if>
						<s:if test="%{columnId == 12}">
							<th><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
						</s:if>
						<s:if test="%{columnId == 13}">
							<th><ah:sort name="configTemplate" key="hiveAp.template" /></th>
						</s:if>
						<s:if test="%{columnId == 14}">
							<th><ah:sort name="mapContainer" key="hiveAp.topology" /></th>
						</s:if>
						<s:if test="%{columnId == 15}">
							<th><ah:sort name="dhcp" key="hiveAp.head.dhcp" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
						</s:if>
						<s:if test="%{columnId == 16}">
							<th><ah:sort name="netmask" key="hiveAp.netmask" /></th>
						</s:if>
						<s:if test="%{columnId == 17}">
							<th><ah:sort name="gateway" key="hiveAp.gateway" /></th>
						</s:if>
						<s:if test="%{columnId == 18}">
							<th><ah:sort name="location" key="hiveAp.location" /></th>
						</s:if>
						<s:if test="%{columnId == 19}">
							<th><ah:sort name="adminUser" key="hiveAp.currentUser" /></th>
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
       						<td class="listCheck"><input type="checkbox" disabled="disabled" /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<s:if test="%{showDomain}">
		       						<td class="list"><a href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/>
		       							<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property value="hostName" /></a></td>
		    					</s:if>
		    					<s:else>
									<td class="list"><a
									href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
									value="hostName" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 12}">
								<td class="list"><s:property value="macAddress" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 13}">
								<td class="list"><s:property value="configTemplateName" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 14}">
								<td class="list"><s:property value="topologyName" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 15}">
								<td class="list"><s:property value="dhcpString" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="ipAddress" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 16}">
								<td class="list"><s:property value="netmask" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 17}">
								<td class="list"><s:property value="gateway" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 18}">
								<td class="list"><s:property value="location" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 19}">
								<td class="list"><s:property value="adminUser" />&nbsp;</td>
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
