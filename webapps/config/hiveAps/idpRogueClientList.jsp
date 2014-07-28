<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.hiveap.IdpAction"%>

<script>
var formName = 'idp';
var thisOperation;

function submitAction(operation) {

    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'refresh') {
		hm.util.checkRefreshIdp();
	} else {
        doContinueOper();
    }
}
function doContinueOper() {
	if(thisOperation != 'refreshFromCache'){
    	showProcessing();
    }
    document.forms[formName].listViewType.value = <%=IdpAction.LISTVIEW_ROGUECLIENT%>;
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function onLoadPage() {
	createBssidDetailPanel();
	startIdpPagingTimer();
}
function onUnloadPage() {
	clearTimeout(idpPagingTimeoutId);
}
var idpPagingLiveCount = 0;
var idpPagingTimeoutId;
function startIdpPagingTimer() {
	var interval = 10;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (idpPagingLiveCount++ < total) {
		idpPagingTimeoutId = setTimeout("pollIdpPagingCache()", interval * 1000);  // seconds
	}
}
function pollIdpPagingCache() {
	var url = "<s:url action="idp" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateIdp }, null);
}

//cached value for refresh which is updated when overlay is showing
var cachedRefresh = false;
function updateIdp(o) {
	eval("var updates = " + o.responseText);
	var unallowRefresh = isAnyOverlayShowing() || isAnyItemSelected();

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
	startIdpPagingTimer();
}

function isAnyOverlayShowing(){
	if(null != overlayManager){
		var overlays = overlayManager.overlays;
		for(var i=0; i<overlays.length; i++){
			if(overlays[i].cfg.getProperty("visible")){
				return true;
			}
		}
	}
	return false;
}

function isAnyItemSelected(){
	var selectedIds = hm.util.getSelectedIds();
	return selectedIds.length > 0 ? true : false;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

function doAjaxRequest(url, callback){
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : callback, failure : abortResult,timeout: 30000}, null);
}

var abortResult = function(o) {
	if(waitingPanel && waitingPanel != null){
		waitingPanel.hide();
	}
}

var bssidDetailPanel = null;
function createBssidDetailPanel(){
	var div = document.getElementById('bssidDetailPanel');
	bssidDetailPanel = new YAHOO.widget.Panel(div, {
		width:"200px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	bssidDetailPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(bssidDetailPanel);
}

var bssidDetailPanelTitle = "<s:text name="idp.view.bssid.detail.title" />"
function showBssidDetail(bssid, domainId){
	var url = "<s:url action='idp' includeParams='none' />" + "?operation=showBssidDetails&bssid="
			 + bssid + "&domainId=" + domainId + "&ignore=" + new Date().getTime();
	doAjaxRequest(url, doBssidDetailResponse);
	bssidDetailPanel.header.innerHTML = bssidDetailPanelTitle + " - " + bssid;
//	bssidDetailPanel.show();
}
function doBssidDetailResponse(o){
	eval("var result = " + o.responseText);
	if(result){
		var tableContext = document.getElementById("detailContext");
		var context = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">";
		for(var i=0; i< result.length; i++){
			var item = result[i];
			context +="<tr>";
			if(item.rp != null){
				context += "<td class=\"list\" width=\"120px\">"+item.rp+"</td>";
			}
			if(item.rpb != null){
				context += "<td class=\"list\" width=\"100px\">"+item.rpb+"</td>";
			}
			if(item.rpTime != null){
				context += "<td class=\"list\" width=\"150px\">"+item.rpTime+"</td>";
			}
			if(item.channel != null){
				context += "<td class=\"list\" width=\"50px\" align=\"center\">"+item.channel+"</td>";
			}
		//	if(item.inNet != null){
		//		context += "<td class=\"list\" width=\"65px\" align=\"center\">"+item.inNet+"</td>";
		//	}
			if(item.rssi != null){
				context += "<td class=\"list\" width=\"50px\">"+item.rssi+"</td>";
			}
		//	if(item.support != null){
		//		context += "<td class=\"list\" width=\"102px\">"+item.support+"</td>";
		//	}
			if(item.complince != null){
				context += "<td class=\"list\">"+item.complince+"</td>";
			}
			context +="</tr>";
		}
		context += "</table>";
		tableContext.innerHTML = context;
		bssidDetailPanel.cfg.setProperty("width", "750px");
		bssidDetailPanel.show();
	}
}


</script>

<div id="content"><s:form action="idp">
	<s:hidden name="cacheId" />
	<s:hidden name="listViewType" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="refresh" value="Refresh"
						class="button" onClick="submitAction('refresh');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td style="padding: 3px 2px 0 8px;"><s:text name="idp.list.view.display.mode" /></td>
					<td><s:radio label="Gender" name="viewMode"
						list="%{viewModeOption2}" listKey="key" listValue="value"
						onclick="submitAction('switchViewMode')"></s:radio><s:radio
						label="Gender" name="viewMode" list="%{viewModeOption1}"
						listKey="key" listValue="value"
						onclick="submitAction('switchViewMode')"></s:radio></td>
				</tr>
			</table>
			</td>
		</tr>

		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td><s:if test="%{detailViewMode}">
				<table id="hiveTable" cellspacing="0" cellpadding="0" border="0"
					class="view" width="100%">
					<tr>
						<th class="check"><input type="checkbox" id="checkAll"
							onClick="hm.util.toggleCheckAll(this);"></th>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<th><ah:sort name="ifMacAddress"
									key="monitor.rogueClient.clientMac" /></th>
							</s:if>
							<s:elseif test="%{columnId == 10}">
								<th><s:text name="hiveAp.macaddress.macOui" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 2}">
								<th><ah:sort name="ssid" key="monitor.hiveAp.report.ifSSID" />
								</th>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<th><ah:sort name="channel"
									key="monitor.hiveAp.report.channel" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<th><ah:sort name="inNetworkFlag"
									key="monitor.hiveAp.report.network" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<th><ah:sort name="rssi" key="monitor.hiveAp.report.rssi" />
								</th>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<th><ah:sort name="stationData"
									key="monitor.hiveAp.report.support" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 7}">
								<th><ah:sort name="compliance"
									key="monitor.hiveAp.report.noncompliant" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 8}">
								<th><ah:sort name="reportNodeId"
									key="monitor.hiveAp.report.hiveAp.NodeId" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 9}">
								<th><ah:sort name="reportTime.time"
									key="monitor.hiveAp.report.time" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 16}">
								<th><ah:sort name="ifIndex"
									key="monitor.hiveAp.report.hiveAp.bssid" /></th>
							</s:elseif>
						</s:iterator>
						<s:if test="%{showDomain}">
							<th><ah:sort name="owner.domainName" key="config.domain" />
							</th>
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
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<td class="list"><s:property value="ifMacAddress" /></td>
								</s:if>
								<s:elseif test="%{columnId == 10}">
									<td class="list"><s:property value="vendor" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 2}">
									<td class="list"><s:property value="ssid" /> &nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 3}">
									<td class="list" align="center"><s:property
										value="channelString" /> &nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 4}">
									<td class="list" align="center"><s:property
										value="networkString" /> &nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 5}">
									<td class="list" nowrap="nowrap"><s:property
										value="rssiDbm" /> &nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 6}">
									<td class="list"><s:property value="supportString" />
									&nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 7}">
									<td class="list"><s:property value="complianceString" />
									&nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 8}">
									<s:if test="%{null != reportHostName}">
										<td class="list"><a
										href='<s:url value="hiveApMonitor.action" includeParams="none"><s:param name="operation" value="%{'hiveApDetails'}"/><s:param name="id" value="%{reportHiveAPId}"/></s:url>'><s:property
										value="reportHostName" /></a></td>
									</s:if>
									<s:else>
										<td class="list"><s:property value="reportNodeId" /></td>
									</s:else>
								</s:elseif>
								<s:elseif test="%{columnId == 9}">
									<td class="list" nowrap="nowrap"><s:property
										value="reportTimeString" /> &nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 16}">
									<td class="list" nowrap="nowrap"><s:property
										value="reportedBssid" /></td>
								</s:elseif>
							</s:iterator>
							<s:if test="%{showDomain}">
								<td class="list"><s:property value="%{owner.domainName}" />
								</td>
							</s:if>
						</tr>
					</s:iterator>
				</table>
			</s:if> <s:else>
				<table id="hiveTable" cellspacing="0" cellpadding="0" border="0"
					class="view" width="100%">
					<tr>
						<th class="check"><input type="checkbox" id="checkAll"
							onClick="hm.util.toggleCheckAll(this);"></th>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<th><ah:sort name="ifMacAddress"
									key="monitor.rogueClient.clientMac" /></th>
							</s:if>
							<s:elseif test="%{columnId == 10}">
								<th><s:text name="hiveAp.macaddress.macOui" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 2}">
								<th><s:text name="monitor.hiveAp.report.ifSSID" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 14}">
								<th><s:text name="monitor.hiveAp.report.highest.rssi" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 15}">
								<th><s:text name="monitor.hiveAp.lastDetectedTime" /></th>
							</s:elseif>
						</s:iterator>
						<s:if test="%{showDomain}">
							<th><ah:sort name="owner.id" key="config.domain" /></th>
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
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<td class="list"><a href="#showDetails"
										onclick="showBssidDetail('<s:property value="ifMacAddress" />',<s:property value="%{owner.id}" />)"><s:property
										value="ifMacAddress" /></a></td>
								</s:if>
								<s:elseif test="%{columnId == 10}">
									<td class="list"><s:property value="vendor" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 2}">
									<td class="list"><s:property value="ssid" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 14}">
									<td class="list"><s:property
										value="highestRSSIReportString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 15}">
									<td class="list" nowrap="nowrap"><s:property
										value="lastestReportedString" /></td>
								</s:elseif>
							</s:iterator>
							<s:if test="%{showDomain}">
								<td class="list"><s:property value="%{owner.domainName}" /></td>
							</s:if>
						</tr>
					</s:iterator>
				</table>
			</s:else></td>
		</tr>
	</table>
</s:form></div>
<div id="bssidDetailPanel" style="display: none;">
<div class="hd"><s:text name="idp.view.bssid.detail.title" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 8px 5px 8px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<th align="left" width="120px"><s:text
							name="monitor.hiveAp.report.hiveAp.NodeId" /></th>
						<th align="left" width="100px"><s:text
							name="monitor.hiveAp.report.hiveAp.bssid" /></th>
						<th align="left" width="150px"><s:text
							name="monitor.hiveAp.report.time" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.channel" /></th>
						<!-- <th align="left" width="65px"><s:text
							name="monitor.hiveAp.report.network" /></th> -->
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.rssi" /></th>
						<!-- <th align="left"><s:text name="monitor.hiveAp.report.support" /></th> -->
						<th align="left"><s:text
							name="monitor.hiveAp.report.noncompliant" /></th>
					</tr>
					<tr>
						<td colspan="10">
						<div id="detailContext"
							style="overflow: auto; width: 100%; height: 200px"></div>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>