<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.hiveap.IdpAction"%>

<script type="text/javascript">
// "click" event handler for each menu item

function onMoveToMenuClick(p_sType, p_aArgs, p_oValue) {
	submitAction(p_oValue);
}

function onViewMenuClick(p_sType, p_aArgs, p_oValue) {
	if (managementPanel != null) {
		managementPanel.cfg.setProperty('visible', true);
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=editEnclosedRogueAps&ignore=" + new Date().getTime();
		doAjaxRequest(url, doEditResponse);
	}
}

function onSettingMenuClick(p_sType, p_aArgs, p_oValue) {
//	alert("p_sType:"+ p_sType +" p_aArgs:"+ p_aArgs +" p_oValue:" + p_oValue);
	if(p_oValue == 'interval'){
		// show refresh interval setting panel
		if (intervalSettingPanel != null) {
			intervalSettingPanel.cfg.setProperty('visible', true);
			var url = "<s:url action='idp' includeParams='none' />" + "?operation=editInterval&ignore=" + new Date().getTime();
			doAjaxRequest(url, doEditResponse);
		}
	}else if (p_oValue == 'strength') {
		// show signal strength setting panel
		if (strengthSettingPanel != null) {
			strengthSettingPanel.cfg.setProperty('visible', true);
			var url = "<s:url action='idp' includeParams='none' />" + "?operation=editThreshold&ignore=" + new Date().getTime();
			doAjaxRequest(url, doEditResponse);
		}
	}
}
function onMitigationMenuClick(p_sType, p_aArgs, p_oValue) {
	submitAction(p_oValue);
}

var intervalSettingPanel = null;
function createIntervalSettingPanel(){
	var div = document.getElementById('intervalSettingPanel');
	intervalSettingPanel = new YAHOO.widget.Panel(div, {
		width:"310px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	intervalSettingPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(intervalSettingPanel);
	intervalSettingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

var strengthSettingPanel = null;
function createStrengthSettingPanel(){
	var div = document.getElementById('strengthSettingPanel');
	strengthSettingPanel = new YAHOO.widget.Panel(div, {
		width:"310px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	strengthSettingPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(strengthSettingPanel);
	strengthSettingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
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

function validate(){
	var intervalEl = document.getElementById("refreshInterval");
	var message = hm.util.validateIntegerRange(intervalEl.value, '<s:text name="idp.refresh.interval.label" />',
	                                           <s:property value="10" />,
	                                           <s:property value="1440" />);
	if (message != null) {
		hm.util.reportFieldError(intervalEl, message);
		intervalEl.focus();
		return false;
	}
	return true;
}

function updateRefreshInterval(){
	if(!validate()){
		return;
	}
	if (intervalSettingPanel != null) {
		intervalSettingPanel.cfg.setProperty('visible', false);
		var interval = document.getElementById("refreshInterval").value;
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=updateInterval&interval=" + interval + "&ignore=" + new Date().getTime();
		doAjaxRequest(url, doUpdateResponse);
	}
}
function hideIntervalSettingPanel(){
	if (intervalSettingPanel != null) {
		intervalSettingPanel.cfg.setProperty('visible', false);
	}
}
function updateStrengthThreshold(){
	if (strengthSettingPanel != null) {
		strengthSettingPanel.cfg.setProperty('visible', false);
		var threshold = document.getElementById("rssiThreshold").value;
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=updateThreshold&threshold=" + threshold + "&ignore=" + new Date().getTime();
		doAjaxRequest(url, doUpdateResponse);
	}
}
function hideStrengthSettingPanel(){
	if (strengthSettingPanel != null) {
		strengthSettingPanel.cfg.setProperty('visible', false);
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
	if(result.itv != undefined){// show interval panel
		document.getElementById("refreshInterval").value = result.itv;
	}else if(result.trh != undefined){// show threshold panel
		document.getElementById("rssiThreshold").value = -result.trh;
	}else if(result.eros){// show enclosed rogue ap panel
		var items = result.eros;
		var selectEl = document.getElementById("enclosedItems");
		selectEl.length = 0;
		selectEl.length = items.length;
		for(var i=0; i<items.length; i++){
			selectEl.options[i].value = items[i].key;
			selectEl.options[i].text = items[i].value;
		}
	}else if(result.eros_r){// remove enclosed rogue ap
		var items = result.eros_r;
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
function doUpdateResponse(o){
	eval("var result = " + o.responseText);
	if(result.suc){
		showInfoDialog("Update Successfully");
	}else{
		showInfoDialog("Update failed");
	}
}

function hideManagementPanel(){
	if(null != managementPanel){
		managementPanel.hide();
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
		thisOperation = "removeEnclosedRogueAps";
		hm.util.confirmRemoveItems();
	}
}

function showClients(bssid, reportId){
//	alert("entry id:"+ bssid + ", report hiveap:" + reportId);
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=showMitigate&bssid="
			+ bssid + "&nodeId=" + reportId + "&ignore=" + new Date().getTime();
		doAjaxRequest(url, doMitigateResponse);
}

function doMitigateResponse(o){
	eval("var result = " + o.responseText);
	alert("result");
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
			if(item.onMap != null){
				context += "<td class=\"list\" width=\"80px\">"+item.onMap+"</td>";
			} else {
				context += "<td class=\"list\" width=\"80px\">"+"-</td>";
			}
			if(item.rpTime != null){
				context += "<td class=\"list\" width=\"80px\">"+item.rpTime+"</td>";
			}
			if(item.client != null){
				if(item.client > 0 && item.mit){
					var url = "<s:url action='idp' includeParams='none' />" + "?operation=clients&listType=rogueClient&nodeId=" + item.rp + "&parentBssid" + item.bssid + new Date().getTime();
					context += "<td class=\"list\" width=\"40px\">"+"<a href='"+ url + "'>"+item.client+"</a>"+"</td>";
				}else{
					context += "<td class=\"list\" width=\"40px\">"+item.client+"</td>";
				}
			}
			if(item.mit != null){
				context += "<td class=\"list\" width=\"50px\" align=\"center\">"+item.mit+"</td>";
			}
			if(item.mode != null){
				context += "<td class=\"list\" width=\"50px\" align=\"center\">"+item.mode+"</td>";
			}
			if(item.channel != null){
				context += "<td class=\"list\" width=\"50px\" align=\"center\">"+item.channel+"</td>";
			}
			if(item.inNet != null){
				context += "<td class=\"list\" width=\"70px\" align=\"center\">"+item.inNet+"</td>";
			}
			if(item.rssi != null){
				context += "<td class=\"list\" width=\"50px\">"+item.rssi+"</td>";
			}
			if(item.support != null){
				context += "<td class=\"list\" width=\"80px\">"+item.support+"</td>";
			}
			if(item.complince != null){
				context += "<td class=\"list\" width=\"80px;\">"+item.complince+"</td>";
			}
			context +="</tr>";
		}
		context += "</table>";
		tableContext.innerHTML = context;
		bssidDetailPanel.cfg.setProperty("width", "850px");
		bssidDetailPanel.show();
	}
}

var clientId;
function showLocation() {
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length != 1){
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}
	clientId = selectedIds[0];
	requestLocation("locateRogue", clientId, locationSucceeded);
}
function showClientLocation(id) {
	clientId = id;
	requestLocation("locateRogue", clientId, locationSucceeded);
}
function requestLocation(operation, clientId, callback) {
	url = "<s:url action='maps' includeParams='none' />?operation=" + operation + "&clientId=" + clientId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:callback,failure:requestFailed,timeout: 10000}, null);
}
var locationInfoDialog = null;
var locationSucceeded = function(o){
	eval("var result = " + o.responseText);
	if (locationInfoDialog == null) {
		locationInfoDialog = new YAHOO.widget.SimpleDialog("locationInfoDialog",
              { width: "550px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal: true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
              } );
     	locationInfoDialog.setHeader("Notice");
     	locationInfoDialog.render(document.body);
	}
	if (result.msg) {
		locationInfoDialog.cfg.setProperty('text', result.msg);
		locationInfoDialog.show();
	}
	if (result.mapId) {
		var url = "<s:url action='maps' includeParams='none' />?operation=mapRogue&selectedMapId=" + result.mapId + "&clientId=" + clientId;
		window.location.href = url;
	}
}
var requestFailed = function(o) {
	warnDialog.cfg.setProperty('text', "Operation failed.  Please try again later.");
	warnDialog.show();
}

var formName = 'idp';
var thisOperation;

function submitAction(operation) {

    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'toFriendly') {
		hm.util.checkAndConfirmFriendly();
	} else if (operation == 'refresh') {
		hm.util.checkRefreshIdp();
	} else if (operation == 'mitigate') {
		hm.util.checkAndConfirmMitigate();
	} else if (operation == 'nomitigate') {
		hm.util.checkAndConfirmTerminateMitigate();
	} else {
        doContinueOper();
    }
}
function doContinueOper() {
	if(thisOperation == 'removeEnclosedRogueAps'){
		var url = "<s:url action='idp' includeParams='none' />" + "?operation=removeEnclosedRogueAps&bssidString="+selectItems+"&ignore=" + new Date().getTime();
		doAjaxRequest(url, doEditResponse);	
		return;
	}
	if(thisOperation != 'switch' && thisOperation != 'refreshFromCache'){
    	showProcessing();
    }
    document.forms[formName].listViewType.value = <%=IdpAction.LISTVIEW_ROGUEAP%>;
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function onLoadPage() {
// create pannel
	createIntervalSettingPanel();
	createStrengthSettingPanel();
	createManagementPanel();
	createBssidDetailPanel();

// management... menu
	var mMenu = new YAHOO.widget.Menu("m_menu", { fixedcenter: false });
	mMenu.addItems([
		    [
		        { text: '<s:text name="hiveAp.menu.move.friendly"/>', onclick: { fn: onMoveToMenuClick, obj: "toFriendly" } },
		        { text: '<s:text name="idp.list.view.item.location"/>', onclick: { fn: showLocation, obj: "showLocation" } }
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
				{ text: '<s:text name="hiveAp.menu.view.rogue"/>', onclick: { fn: onViewMenuClick, obj: "showManagementPanel" } },
		        { text: '<s:text name="idp.refresh.interval"/>', onclick: { fn: onSettingMenuClick, obj: "interval" } },
		        { text: '<s:text name="idp.signal.strength"/>', onclick: { fn: onSettingMenuClick, obj: "strength" } }
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

// mitigation... menu
	var iMenu = new YAHOO.widget.Menu("i_menu", { fixedcenter: false });
	iMenu.addItems([
		    [
		        { text: '<s:text name="idp.mitigation.start"/>', onclick: { fn: onMitigationMenuClick, obj: "mitigate" } },
		        { text: '<s:text name="idp.mitigation.stop"/>', onclick: { fn: onMitigationMenuClick, obj: "nomitigate" } }
			]
	]);
	iMenu.subscribe("beforeShow", function(){
		showOrHideMitigationTip(true);
		var x = YAHOO.util.Dom.getX('i_menutoggle');
		var y = YAHOO.util.Dom.getY('i_menutoggle');
		YAHOO.util.Dom.setX('i_menu', x);
		YAHOO.util.Dom.setY('i_menu', y+20);
	});
	iMenu.subscribe("hide", function(){
		showOrHideMitigationTip(false);
	});
	
	iMenu.render();

	YAHOO.util.Event.addListener("i_menutoggle", "click", iMenu.show, null, iMenu);
	
	startIdpPagingTimer();
	startRogueApClientCountTimer();
}
function onUnloadPage() {
	clearTimeout(idpPagingTimeoutId);
	clearTimeout(rogueApClientCountTimeoutId);
}

function showOrHideMitigationTip(blnShown) {
	if (blnShown) {
		Get("tipMsgContainer").innerHTML = "<span style='background-color: #FFFFFF; padding: 0 15px;'>" 
				+ "Note: No mitigation CLI will be sent to device if auto mitigation mode is set in WIPS policy."
				+ "</span>";
	} else {
		Get("tipMsgContainer").innerHTML = "";
	}
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
	var extOrderBy = "<s:property value="#request.extOrderBy" />";
	if (extOrderBy == null || extOrderBy == "") {
		var url = "<s:url action="idp" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateIdp }, null);
	}
}
var rogueApClientCountLiveCount = 0;
var rogueApClientCountTimeoutId;
function startRogueApClientCountTimer() {
	var interval = 60;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (rogueApClientCountLiveCount++ < total) {
		rogueApClientCountTimeoutId = setTimeout("refreshClientCounts()", interval * 1000);  // seconds
	}
}
function refreshClientCounts() {
	var extOrderBy = "<s:property value="#request.extOrderBy" />";
	var extAscending = "<s:property value="#request.extAscending" />";
	if (extOrderBy == "clientCountRogueAp") {
		clearTimeout(rogueApClientCountTimeoutId);
		window.location.href = "idp.action?operation=sort&extOrderBy="+extOrderBy+"&extAscending="+extAscending;
	}
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
	var all_rb = '<input type="radio" name="category" id="category1" value="all" onclick="submitAction(\'switch\')"/><label for="category1"><s:text name="idp.category.label.all"/></label>&nbsp;';
	var all_rb_ck = '<input type="radio" name="category" id="category1" value="all" checked onclick="submitAction(\'switch\')"/><label for="category1"><s:text name="idp.category.label.all"/></label>&nbsp;';
	var innet_rb = '<input type="radio" name="category" id="category2" value="innet" onclick="submitAction(\'switch\')"/><label for="category2"><s:text name="idp.category.label.inNet"/></label>&nbsp;';
	var innet_rb_ck = '<input type="radio" name="category" id="category2" value="innet" checked onclick="submitAction(\'switch\')"/><label for="category2"><s:text name="idp.category.label.inNet"/></label>&nbsp;';
	var onmap_rb = '<input type="radio" name="category" id="category3" value="onmap" onclick="submitAction(\'switch\')"/><label for="category3"><s:text name="idp.category.label.onMap"/></label>&nbsp;';
	var onmap_rb_ck = '<input type="radio" name="category" id="category3" value="onmap" checked onclick="submitAction(\'switch\')"/><label for="category3"><s:text name="idp.category.label.onMap"/></label>&nbsp;';
	var strong_rb = '<input type="radio" name="category" id="category4" value="strong" onclick="submitAction(\'switch\')"/><label for="category4"><s:text name="idp.category.label.strong"/></label>&nbsp;';
	var strong_rb_ck = '<input type="radio" name="category" id="category4" value="strong" checked onclick="submitAction(\'switch\')"/><label for="category4"><s:text name="idp.category.label.strong"/></label>&nbsp;';
	var weak_rb = '<input type="radio" name="category" id="category5" value="weak" onclick="submitAction(\'switch\')"/><label for="category5"><s:text name="idp.category.label.weak"/></label>&nbsp;';
	var weak_rb_ck = '<input type="radio" name="category" id="category5" value="weak" checked onclick="submitAction(\'switch\')"/><label for="category5"><s:text name="idp.category.label.weak"/></label>&nbsp;';
	<s:if test="%{innetCategory}">
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>( <s:text name="idp.list.view.category.filter" /> ' + all_rb + innet_rb_ck + onmap_rb + strong_rb + weak_rb + ')</td>');
	</s:if>
	<s:elseif test="%{onmapCategory}">
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>( <s:text name="idp.list.view.category.filter" /> ' + all_rb + innet_rb + onmap_rb_ck + strong_rb + weak_rb + ')</td>');
	</s:elseif>
	<s:elseif test="%{strongCategory}">
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>( <s:text name="idp.list.view.category.filter" /> ' + all_rb + innet_rb + onmap_rb + strong_rb_ck + weak_rb + ')</td>');
	</s:elseif>
	<s:elseif test="%{weakCategory}">
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>( <s:text name="idp.list.view.category.filter" /> ' + all_rb + innet_rb + onmap_rb + strong_rb + weak_rb_ck + ')</td>');
	</s:elseif>
	<s:else>
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>( <s:text name="idp.list.view.category.filter" /> ' + all_rb_ck + innet_rb + onmap_rb + strong_rb + weak_rb + ')</td>');
	</s:else>
}
</script>

<div id="content"><s:form action="idp">
	<s:hidden name="cacheId" />
	<s:hidden name="listViewType" />
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td colspan="10" id="tipMsgContainer" class="noteInfo"></td>
				</tr>
				<tr>
					<td><input type="button" name="refresh" value="Refresh"
						class="button" onClick="submitAction('refresh');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="mitigate" value="Mitigation..."
						class="button" id="i_menutoggle"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="setting" value="Settings..."
						class="button" id="s_menutoggle"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Management..."
						class="button" id="m_menutoggle" style="width: 100px"
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
					<td>
					<div id="i_menu" class="yuimenu"></div>
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
							<s:elseif test="%{columnId == 11}">
								<th><ah:sort name="mapId" key="monitor.hiveAp.report.onMap" />
								</th>
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
							<s:elseif test="%{columnId == 12}">
								<th><ah:sort name="mitigated"
									key="monitor.hiveAp.report.mitigation" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 13}">
								<th><ah:sortext name="clientCountRogueAp" key="monitor.hiveAp.report.clients" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 16}">
								<th><ah:sort name="ifIndex"
									key="monitor.hiveAp.report.hiveAp.bssid" /></th>
							</s:elseif>
							<s:elseif test="%{columnId == 17}">
								<th><s:text name="monitor.hiveAp.report.mode" /></th>
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
					<s:iterator value="page" status="status" id="pageRow">
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
								<s:elseif test="%{columnId == 11}">
									<s:if test="%{rssiCount > 0}">
										<td class="list" nowrap="nowrap"><a href="#location" onclick="showClientLocation(<s:property value="#pageRow.id" />);">
											<s:property value="mapName" /></a>
											&nbsp;
										</td>
									</s:if>
									<s:else>
										<td class="list" nowrap="nowrap">
											<s:property value="mapName" />
											&nbsp;
										</td>
									</s:else>
								</s:elseif>
								<s:elseif test="%{columnId == 5}">
									<td class="list" nowrap="nowrap"><s:property
										value="rssiDbm" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 6}">
									<td class="list"><s:property value="supportString" />
									&nbsp;</td>
								</s:elseif>
								<s:elseif test="%{columnId == 7}">
									<td class="list"><s:property value="complianceString" /></td>
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
								<s:elseif test="%{columnId == 12}">
									<td class="list" nowrap="nowrap"><s:property
										value="mitigatedString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 17}">
									<td class="list" nowrap="nowrap"><s:property
										value="modeString" /></td>
								</s:elseif>
								<s:elseif test="%{columnId == 13}">
									<s:if test="%{clientCount > 0}">
										<td class="list" align="center"><a
											href='<s:url action="idp" includeParams="none"><s:param name="operation" value="%{'clients'}"/>
																					   <s:param name="listType" value="%{'rogueClient'}"/>
																					   <s:param name="nodeId" value="reportNodeId"/>
																					   <s:param name="parentBssid" value="ifMacAddress"/></s:url>'>
										<s:property value="clientCount" /></a></td>
									</s:if>
									<s:else>
										<td class="list" align="center"><s:property
											value="clientCount" /></td>
									</s:else>
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
							<s:elseif test="%{columnId == 11}">
								<th><s:text name="monitor.hiveAp.report.onMap" /></th>
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
					<s:iterator value="page" status="status" id="pageRow">
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
								<s:elseif test="%{columnId == 11}">
									<s:if test="%{rssiCount > 0}">
										<td class="list" nowrap="nowrap"><a href="#location" onclick="showClientLocation(<s:property value="#pageRow.id" />);">
											<s:property value="mapName" /></a>
											&nbsp;
										</td>
									</s:if>
									<s:else>
										<td class="list" nowrap="nowrap">
											<s:property value="mapName" />
											&nbsp;
										</td>
									</s:else>
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
<div id="intervalSettingPanel" style="display: none;">
<div class="hd"><s:text name="idp.refresh.interval" /></div>
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
						<td width="100px;"><s:text name="idp.refresh.interval.label" /></td>
						<td><s:textfield name="refreshInterval" size="8"
							maxlength="4"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
						<td nowrap="nowrap">&nbsp;<s:text
							name="idp.refresh.interval.range" /></td>
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
				<td><input type="button" name="ignore" value="Update"
					class="button" 
					onClick="updateRefreshInterval();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="hideIntervalSettingPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
<div id="strengthSettingPanel" style="display: none;">
<div class="hd"><s:text name="idp.signal.strength" /></div>
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
						<td width="120"><s:text name="idp.signal.strength.threshold" /></td>
						<td align="right"><s:select id="rssiThreshold"
							name="rssiThreshold" list="%{rssiThresholdValues}" listKey="id"
							listValue="value" /></td>
						<td nowrap="nowrap" style="padding: 1px 0px 1px 5px;">dBm</td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
					<tr>
						<td colspan="3" class="sepLine"><img
							src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
					<tr>
						<td colspan="3" class="noteInfo"><s:text
							name="idp.signal.strength.threshold.note" /></td>
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
				<td><input type="button" name="ignore" value="Update"
					class="button" 
					onClick="updateStrengthThreshold();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="hideStrengthSettingPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
<div id="managementPanel" style="display: none;">
<div class="hd"><s:text name="hiveAp.menu.view.rogue" /></div>
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
							name="hiveAp.menu.view.rogue.note" /></td>
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
							name="monitor.hiveAp.report.onMap" /></th>
						<th align="left" width="70px"><s:text
							name="monitor.hiveAp.report.time" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.clients" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.mitigation" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.mode" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.channel" /></th>
						<th align="left" width="67px"><s:text
							name="monitor.hiveAp.report.network" /></th>
						<th align="left" width="50px"><s:text
							name="monitor.hiveAp.report.rssi" /></th>
						<th align="left"><s:text name="monitor.hiveAp.report.support" /></th>
						<th align="left"><s:text
							name="monitor.hiveAp.report.noncompliant" /></th>
					</tr>
					<tr>
						<td colspan="12">
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
<tiles:insertDefinition name="mitigationManual" />