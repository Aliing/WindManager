<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.hiveap.IdpAction"%>

<script type="text/javascript">
function onMoveToMenuClick(p_sType, p_aArgs, p_oValue) {
	submitAction(p_oValue);
}

function onViewMenuClick(p_sType, p_aArgs, p_oValue) {
	if (managementPanel != null) {
		managementPanel.cfg.setProperty('visible', true);
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=editEnclosedFriendlyAps&ignore=" + new Date().getTime();
		doAjaxRequest(url, doEditResponse);
	}
}

function removeEnclosedRogueAPs(){
	var selectEl = document.getElementById("enclosedItems");
	selectItems = new Array();
	for(var i=0; i<selectEl.options.length; i++){
		if(selectEl.options[i].selected == true){
			selectItems.push(selectEl.options[i].value);
		}
	}
	if(selectItems.length ==0 ){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.selectObject" />");
			warnDialog.show();
		}
	}else if(selectItems.length==1 && selectItems[0]<0){
		if(null != warnDialog){
			warnDialog.cfg.setProperty('text', "<s:text name="info.emptyList" />");
			warnDialog.show();
		}
	}else{
		thisOperation = "removeEnclosedFriendlyAps";
		hm.util.confirmRemoveItems();
	}
}

function doAjaxRequest(url, callback){
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : callback, failure : abortResult,timeout: 30000}, null);
}

var abortResult = function(o) {
	if(waitingPanel && waitingPanel != null){
		waitingPanel.hide();
	}
}

function doEditResponse(o){
	eval("var result = " + o.responseText);
	if(result.efris){// show enclosed friendly ap panel
		var items = result.efris;
		var selectEl = document.getElementById("enclosedItems");
		selectEl.length = 0;
		selectEl.length = items.length;
		for(var i=0; i<items.length; i++){
			selectEl.options[i].value = items[i].key;
			selectEl.options[i].text = items[i].value;
		}
	}else if(result.efris_r){// remove enclosed friendly ap
		var items = result.efris_r;
		var selectEl = document.getElementById("enclosedItems");
		selectEl.length = 0;
		selectEl.length = items.length;
		for(var i=0; i<items.length; i++){
			selectEl.options[i].value = items[i].key;
			selectEl.options[i].text = items[i].value;
		}
		if(result.info){
			showInfoDialog(result.info);
		}
		shouldRefreshPage = true;
	}
}

function hideManagementPanel(){
	if(null != managementPanel){
		managementPanel.hide();
	}
}

function refreshPageAfterRemovingPanelList() {
	if (shouldRefreshPage == true) {
		shouldRefreshPage = false;
		submitAction(null);
	}
}
var shouldRefreshPage = false;

var managementPanel = null;
function createManagementPanel(){
	var div = document.getElementById('managementPanel');
	managementPanel = new YAHOO.widget.Panel(div, {
		width:"310px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	managementPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(managementPanel);
	managementPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
	managementPanel.hideEvent.subscribe(function(){refreshPageAfterRemovingPanelList()});
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
	bssidDetailPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
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
		var context = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">";
		for(var i=0; i< result.length; i++){
			var item = result[i];
			context +="<tr>";
			if(item.rp != null){
				context += "<td class=\"list\" width=\"100px\">"+item.rp+"</td>";
			}
			if(item.rpb != null){
				context += "<td class=\"list\" width=\"100px\">"+item.rpb+"</td>";
			}
			if(item.rpTime != null){
				context += "<td class=\"list\" width=\"80px\">"+item.rpTime+"</td>";
			}
			if(item.channel != null){
				context += "<td class=\"list\" width=\"50px\" align=\"center\">"+item.channel+"</td>";
			}
			if(item.inNet != null){
				context += "<td class=\"list\" width=\"65px\" align=\"center\">"+item.inNet+"</td>";
			}
			if(item.rssi != null){
				context += "<td class=\"list\" width=\"50px\">"+item.rssi+"</td>";
			}
			if(item.support != null){
				context += "<td class=\"list\" width=\"102px\">"+item.support+"</td>";
			}
			if(item.complince != null){
				context += "<td class=\"list\" width=\"100px\">"+item.complince+"&nbsp;</td>";
			}
			context +="</tr>";
		}
		context += "</table>";
		tableContext.innerHTML = context;
		bssidDetailPanel.cfg.setProperty("width", "750px");
		bssidDetailPanel.show();
	}
}

var formName = 'idp';
var thisOperation;

function submitAction(operation) {

    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'toRogue') {
		hm.util.checkAndConfirmRogue();
	} else if (operation == 'refresh') {
		hm.util.checkRefreshIdp();
	} else {
        doContinueOper();
    }
}
function doContinueOper() {
	if(thisOperation == 'removeEnclosedFriendlyAps'){
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=removeEnclosedFriendlyAps&bssidString="+selectItems+"&ignore=" + new Date().getTime();
		doAjaxRequest(url, doEditResponse);	
		return;
	}
	if(thisOperation != 'switch' && thisOperation != 'refreshFromCache'){
    	showProcessing();
    }
    if(thisOperation == 'filterManaged'){
    	document.forms[formName].filterManagedHiveAPBssid.value = document.forms[formName].filterCbx.checked;
    }
    document.forms[formName].listViewType.value = <%=IdpAction.LISTVIEW_FRIENDLYAP%>;
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function onLoadPage() {
	createManagementPanel();
	createBssidDetailPanel();
// management... menu
	// "click" event handler for each menu item
	var mMenu = new YAHOO.widget.Menu("m_menu", { fixedcenter: false });
	mMenu.addItems([
		    [
		        { text: '<s:text name="hiveAp.menu.move.rogue"/>', onclick: { fn: onMoveToMenuClick, obj: "toRogue" } }
			]
	]);
	mMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('m_menutoggle');
		var y = YAHOO.util.Dom.getY('m_menutoggle');
		YAHOO.util.Dom.setX('m_menu', x);
		YAHOO.util.Dom.setY('m_menu', y+20);
	});
	
	mMenu.render();

	YAHOO.util.Event.addListener("m_menutoggle", "click", mMenu.show, null, mMenu);

	
	// setting... menu
	var sMenu = new YAHOO.widget.Menu("s_menu", { fixedcenter: false });
	sMenu.addItems([
		    [
				{ text: '<s:text name="hiveAp.menu.view.friendly"/>', onclick: { fn: onViewMenuClick, obj: "showManagementPanel" } }
			]
	]);
	sMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('s_menutoggle');
		var y = YAHOO.util.Dom.getY('s_menutoggle');
		YAHOO.util.Dom.setX('s_menu', x);
		YAHOO.util.Dom.setY('s_menu', y+20);
	});
	
	sMenu.render();

	YAHOO.util.Event.addListener("s_menutoggle", "click", sMenu.show, null, sMenu);
	
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
	<s:if test="%{filterManagedHiveAPBssid}">
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />', 
			'&nbsp;&nbsp;</td><td nowrap>(</td><td nowrap><input type="checkbox" checked="checked" onclick="submitAction(\'filterManaged\')" name="filterCbx" /> </td>',
			'<td nowrap><s:text name="idp.list.view.filter.managed" /> )</td>');
	</s:if>
	<s:else>
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />', 
			'&nbsp;&nbsp;</td><td nowrap>(</td><td nowrap><input type="checkbox" onclick="submitAction(\'filterManaged\')"  name="filterCbx" /> </td>',
			'<td nowrap><s:text name="idp.list.view.filter.managed" /> )</td>');
	</s:else>
}
</script>

<div id="content"><s:form action="idp">
	<s:hidden name="cacheId" />
	<s:hidden name="listViewType" />
	<s:hidden name="filterManagedHiveAPBssid" />
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
					<td><input type="button" name="setting" value="Settings..."
						class="button" id="s_menutoggle"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Management..."
						class="button" id="m_menutoggle" style="width: 100px;"
						<s:property value="writeDisabled" />></td>
					<td style="padding: 3px 2px 0 8px;"><s:text name="idp.list.view.display.mode" /></td>
					<td><s:radio label="Gender" name="viewMode"
						list="%{viewModeOption2}" listKey="key" listValue="value"
						onclick="submitAction('switchViewMode')"></s:radio><s:radio
						label="Gender" name="viewMode" list="%{viewModeOption1}"
						listKey="key" listValue="value"
						onclick="submitAction('switchViewMode')"></s:radio></td>
					<td>
					<div id="m_menu" class="yuimenu"></div>
					</td>
					<td>
					<div id="s_menu" class="yuimenu"></div>
					</td>
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
									key="monitor.hiveAp.report.ifBSSID" /></th>
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
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<td class="list"><s:property value="ifMacAddress" /></td>
								</s:if>
								<s:elseif test="%{columnId == 10}">
									<td class="list"><s:property value="vendor" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 2}">
									<td class="list"><s:property value="ssid" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 3}">
									<td class="list" align="center"><s:property
										value="channel" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 4}">
									<td class="list" align="center"><s:property
										value="networkString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 5}">
									<td class="list" nowrap="nowrap"><s:property
										value="rssiDbm" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 6}">
									<td class="list"><s:property value="supportString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 7}">
									<td class="list"><s:property value="complianceString" />
									</td>
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
										value="reportTimeString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 16}">
									<td class="list" nowrap="nowrap"><s:property
										value="reportedBssid" /></td>
								</s:elseif>
							</s:iterator>
							<s:if test="%{showDomain}">
								<td class="list"><s:property value="%{owner.domainName}" /></td>
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
									key="monitor.hiveAp.report.ifBSSID" /></th>
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
<div id="managementPanel" style="display: none;">
<div class="hd"><s:text name="hiveAp.menu.view.friendly" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 8px 5px 8px 5px;">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td><select style="width: 150px;" id="enclosedItems"
							multiple="multiple" size="10"></select></td>
						<td width="5px"></td>
						<td class="noteInfo" valign="top"><s:text
							name="hiveAp.menu.view.friendly.note" /></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 7px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="button" name="ignore" value="Remove"
					class="button" 
					onClick="removeEnclosedRogueAPs();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="hideManagementPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
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
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<th align="left" width="100px"><s:text
							name="monitor.hiveAp.report.hiveAp.NodeId" /></th>
						<th align="left" width="100px"><s:text
							name="monitor.hiveAp.report.hiveAp.bssid" /></th>
						<th align="left" width="80px"><s:text
							name="monitor.hiveAp.report.time" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.channel" /></th>
						<th align="left" width="65px"><s:text
							name="monitor.hiveAp.report.network" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.rssi" /></th>
						<th align="left"><s:text name="monitor.hiveAp.report.support" /></th>
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