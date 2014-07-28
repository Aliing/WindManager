<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.monitor.AhEvent"%>

<script>
// QUIRKS FLAG, FOR BOX MODEL
var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
// UNDERLAY/IFRAME SYNC REQUIRED
var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

var formName = 'events';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else{
        doContinueOper();
    }
}
function doContinueOper() {
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function replaceText(nodeId, text) {
	var td = document.getElementById(nodeId);
	if (td != null) {
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(text));
	}
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams='none'/>" />');
	waitingPanel.render(document.body);
	overlayManager.register(waitingPanel);
	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

var detailsPanel = null;
var logSettingPanel = null;
function onLoadPage() {
	var div = document.getElementById('detailsPanel');
	detailsPanel = new YAHOO.widget.Panel(div, { width:"500px", fixedcenter:true, visible:false, draggable:true, constraintoviewport:true } );
	var code = document.getElementById('codeHeader');
//	detailsPanel.moveTo(YAHOO.util.Dom.getX(code) + 2, YAHOO.util.Dom.getY(code) + 25);
//	detailsPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code) + 2);
//	detailsPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 25);
	detailsPanel.render();
	div.style.display = "";
	overlayManager.register(detailsPanel);
	detailsPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});

	// create log setting overlay
	var div = document.getElementById('logSettingPanel');
	logSettingPanel = new YAHOO.widget.Panel(div, {
		width:"500px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	logSettingPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(logSettingPanel);
	logSettingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});

	createWaitingPanel();

	if (<s:property value="hide4Setting" />)
	{
		hm.util.hide("settingsTD");
	}
	startEventsPagingTimer();
}

function openLogSettingPanel()
{
	if(null != logSettingPanel){
		var url = "<s:url action="events" includeParams="none" />?operation=editEventLogSettings&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : doEditResponse,timeout: 30000}, null);
	}
}

function hideLogSettingPanel(){
	if(null != logSettingPanel){
		logSettingPanel.cfg.setProperty('visible', false);
	}
}

function doEditResponse(o){
	eval("var result = " + o.responseText);
	if(result.eventInterval != undefined){
		document.getElementById("eventInterval").value = result.eventInterval;
	}
	logSettingPanel.cfg.setProperty('visible', true);
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	for (var i = 0; i < details.length; i++) {
		if (details[i].id == "eventType") {
			var eventType = details[i].v;
		} else if (details[i].id == "eventTypeString") {
			var eventTypeString = details[i].v;
		} else {
			replaceText(details[i].id, details[i].v);
			if (details[i].id == "currentState") {
				replaceText("currentState2", details[i].v);
			}

			if (details[i].id == "ifIndex") {
				replaceText("ifIndex2", details[i].v);
			}
		}
	}

	var tr1 = document.getElementById("et1");
	var tr2 = document.getElementById("et2");
	var tr3 = document.getElementById("et3");
	var tr4 = document.getElementById("et4");
	var tr5 = document.getElementById("et5");
	var tr6 = document.getElementById("et6");
	var tr7 = document.getElementById("et7");

	if (eventType == <%=AhEvent.AH_EVENT_TYPE_THRESHOLD_CROSSING%>) {
		tr1.style.display = "";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "none";
		replaceText("eventType1", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_STATE_CHANGE%>) {
		tr1.style.display = "none";
		tr2.style.display = "";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "none";
		replaceText("eventType2", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE%>) {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "none";
		replaceText("eventType3", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_CLIENTINFO_CHANGE%>) {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "none";
		replaceText("eventType4", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_POE%>) {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "";
		tr6.style.display = "none";
		tr7.style.display = "none";
		replaceText("eventType5", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_CHANNELPOWERCHANGE%>) {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "";
		tr7.style.display = "none";
		replaceText("eventType6", eventTypeString + " Event");
	} else if (eventType == <%=AhEvent.AH_EVENT_TYPE_INTERFACECLIENT%>) {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "";
		replaceText("eventType7", eventTypeString + " Event");
	} else {
		tr1.style.display = "none";
		tr2.style.display = "none";
		tr3.style.display = "none";
		tr4.style.display = "none";
		tr5.style.display = "none";
		tr6.style.display = "none";
		tr7.style.display = "none";
	}
	if (detailsPanel != null) {
	    if (IE_SYNC) {
	        // Keep the underlay and iframe size in sync.
	        detailsPanel.sizeUnderlay();
	        // Syncing the iframe can be expensive. Disable iframe if you
	        // don't need it.
	        detailsPanel.syncIframe();
	    }
		detailsPanel.cfg.setProperty('visible', true);
	}
};
var detailsFailed = function(o) {
//	alert("failed.");
};
var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};
function eventDetails(url, id) {
	url = url + "?operation=view&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}
function onUnloadPage() {
	clearTimeout(eventsPagingTimeoutId);
}
var eventsPagingLiveCount = 0;
var eventsPagingTimeoutId;
function startEventsPagingTimer() {
	var interval = 10;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (eventsPagingLiveCount++ < total) {
		eventsPagingTimeoutId = setTimeout("pollEventsPagingCache()", interval * 1000);  // seconds
	}
}
function pollEventsPagingCache() {
	var url = "<s:url action="events" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateEvents }, null);
}

var cachedRefresh = false;

function updateEvents(o) {
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
	startEventsPagingTimer();
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

var settingsMenu;
function createSettingMenu()
{
	settingsMenu = new YAHOO.widget.Menu('settings_menu', { fixedcenter: false });
	var settingItems = [
			 [
		        { text: '<s:text name="monitor.events.logRetain"/>'}
			 ]
	];
	settingsMenu.addItems(settingItems);
	settingsMenu.subscribe('click', settingsMenuItemClick);
	settingsMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('settingsMenuBtn');
		var y = YAHOO.util.Dom.getY('settingsMenuBtn');
		YAHOO.util.Dom.setX('settings_menu', x);
		YAHOO.util.Dom.setY('settings_menu', y+20);
	});

	settingsMenu.render(document.body);
}

function settingsMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	var text = menuItem.cfg.getProperty("text");

	if (text == '<s:text name="monitor.events.logRetain"/>')
	{
		openLogSettingPanel();
	}
}

function showSettingMenu()
{
	settingsMenu.show();
}

function updateLogSetting()
{
	var eventInterval = document.getElementById("eventInterval");

	if (eventInterval.value.length == 0)
	{
        hm.util.reportFieldError(eventInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.logSet.eventInterval" /></s:param></s:text>');
        eventInterval.focus();
        return false;
    }
    else if (!isValidInterval(eventInterval.value))
    {
		hm.util.reportFieldError(eventInterval, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.logSet.eventInterval" /></s:param><s:param><s:text name="admin.logSet.days" /></s:param></s:text>');
		eventInterval.focus();
		return false;
	}

	url = "<s:url action='events' includeParams='none' />" + "?operation=updateLogSetting"
		+ "&eventInterval="+eventInterval.value+"&ignore=" + new Date().getTime();

	YAHOO.util.Connect.asyncRequest('get', url, {success:updateLogSettingResult}, null);

	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function isValidInterval(interval)
{
	var intInterval = interval.valueOf();
	if ( intInterval >=1 && intInterval <= 60 )
	{
		return true;
	}

	return false;
}

function updateLogSettingResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	hideLogSettingPanel();

	eval("var result = " + o.responseText);
	if(result.success){
		showInfoDialog("Event log settings were updated successfully.");
	}else{
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', "Unable to update event log settings. DB error.");
			warnDialog.show();
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>
<style>
td.panelLabel {
	padding: 0px 0px 5px 0px;
	color: #003366;
}
td.panelText {
	padding: 0px 0px 5px 10px;
	word-break: break-all;
	word-wrap: break-word;
}
td.fieldsetLabel {
	color: #003366;
}
</style>
<div id="content"><s:form action="events">
	<s:hidden name="cacheId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<script>createSettingMenu();
					</script>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Export"
						class="button" onClick="submitAction('export');"
						<s:property value="writeDisabled" />></td>
					<td id="settingsTD">
						<input type="button" name="ignore" value="Settings..." class="button"
							id="settingsMenuBtn" onclick="showSettingMenu();"
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
			<table cellspacing="0" cellpadding="0" border="0" width="100%"
				class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
					<s:if test="%{columnId == 1}">
					<th align="left" nowrap><ah:sort name="apName"
						key="monitor.alarms.apName" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
					<th align="left" nowrap><ah:sort name="apId"
						key="monitor.alarms.apId" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
					<th id="codeHeader" align="left" nowrap><ah:sort
						name="time" key="monitor.alarms.alarmTime" /></th>
					</s:if>
					<s:if test="%{columnId == 4}">
					<th align="left" nowrap><s:text name="monitor.alarms.alarmDesc" /></th>
					</s:if>
					<s:if test="%{columnId == 5}">
					<th align="left" nowrap><ah:sort name="objectName"
						key="monitor.alarms.objectName" /></th>
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
						<s:if
							test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck">
								<input type="checkbox" disabled />
							</td>
						</s:if>
						<s:else>
							<td class="listCheck">
								<ah:checkItem />
							</td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
						<td class="list"><s:property value="%{apName}" /></td>
						</s:if>
						<s:if test="%{columnId == 2}">
						<td class="list"><a
							href="javascript: eventDetails('<s:url action="events" includeParams="none"/>', <s:property value="%{#pageRow.id}" />)"><s:property
							value="%{apId}" /></a></td>
						</s:if>
						<s:if test="%{columnId == 3}">
						<td class="list" nowrap><s:property value="%{trapTimeString}" /></td>
						</s:if>
						<s:if test="%{columnId == 4}">
						<td class="list"><s:property value="%{trapDesc}" /></td>
						</s:if>
						<s:if test="%{columnId == 5}">
						<td class="list"><s:property value="%{objectName}" /></td>
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

	<div id="logSettingPanel" style="display: none;">
	<div class="hd">
		Event Log Settings
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td class="labelT1" width="200px" style="padding-left: 15px;">
									<label>
										<s:text name="admin.logSet.eventInterval" /><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="eventInterval" name="eventInterval"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="2" />
									<s:text name="admin.logSet.days" />
								</td>
							</tr>
							<tr>
								<td height="10"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Update" id="ignore"
										class="button" onClick="updateLogSetting();">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideLogSettingPanel();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
</s:form>
<div id="detailsPanel" style="display: none;">

<div class="hd">Event Details</div>

<div class="bd" style="background-color: #FFFFFF;">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="panelLabel" width="100"><s:text
			name="monitor.alarms.apName" /></td>
		<td class="panelText" id="apName">.</td>
	</tr>
	<tr>
		<td class="panelLabel"><s:text name="monitor.alarms.apId" /></td>
		<td class="panelText" id="apMac">.</td>
	</tr>
	<tr>
		<td class="panelLabel"><s:text name="monitor.alarms.alarmTime" /></td>
		<td class="panelText" id="trapTime">.</td>
	</tr>
	<tr>
		<td class="panelLabel" valign="top"><s:text
			name="monitor.alarms.alarmDesc" /></td>
		<td class="panelText" id="trapDesc">.</td>
	</tr>
	<tr>
		<td class="panelLabel"><s:text name="monitor.alarms.objectName" /></td>
		<td class="panelText" id="objectName">.</td>
	</tr>
	<%--
	<tr>
		<td class="panelLabel"><s:text name="monitor.alarms.eventType" /></td>
		<td class="panelText" id="eventType">.</td>
	</tr>
  --%>
	<tr id="et1" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType1">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel" width="100"><s:text
					name="monitor.alarms.curValue" /></td>
				<td class="panelText" id="curValue">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
					name="monitor.alarms.thresholdLow" /></td>
				<td class="panelText" id="thresholdLow">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
					name="monitor.alarms.thresholdHigh" /></td>
				<td class="panelText" id="thresholdHigh">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et2" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType2">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel" width="100"><s:text
					name="monitor.alarms.previousState" /></td>
				<td class="panelText" id="previousState">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
					name="monitor.alarms.currentState" /></td>
				<td class="panelText" id="currentState">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et3" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType3">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.alarms.objectType" /></td>
				<td class="panelText" id="objectType">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
					name="monitor.alarms.currentState" /></td>
				<td class="panelText" id="currentState2">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text
					name="monitor.alarms.ifIndex" /></td>
				<td class="panelText" id="ifIndex">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientCWPUsed" /></td>
				<td class="panelText" id="clientCWPUsed">.</td>
			</tr>
			<tr>
				<td class="panelLabel"  width="160"><s:text name="monitor.events.clientAuthMethod" /></td>
				<td class="panelText" id="clientAuthMethod">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientEncryptMethod" /></td>
				<td class="panelText" id="clientEncryptionMethod">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientMacProtocol" /></td>
				<td class="panelText" id="clientMacProtocol">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientVLAN" /></td>
				<td class="panelText" id="clientVLAN">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientUserProfId" /></td>
				<td class="panelText" id="clientUserProfId">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientChannel" /></td>
				<td class="panelText" id="clientChannel">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientBSSID" /></td>
				<td class="panelText" id="clientBSSID">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et4" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType4">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.ssid" /></td>
				<td class="panelText" id="ssid">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientIp" /></td>
				<td class="panelText" id="clientIp">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.clientHostName" /></td>
				<td class="panelText" id="clientHostName">.</td>
			</tr>
			<tr>
				<td class="panelLabel" width="100"><s:text name="monitor.events.clientUserName" /></td>
				<td class="panelText" id="clientUserName">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et5" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;">
		<legend	id="eventType5">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.powerSource" /></td>
				<td class="panelText" id="powerSource">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEEth0On" /></td>
				<td class="panelText" id="poEEth0On">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEEth0Pwr" /></td>
				<td class="panelText" id="poEEth0Pwr">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEEth1On" /></td>
				<td class="panelText" id="poEEth1On">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEEth1Pwr" /></td>
				<td class="panelText" id="poEEth1Pwr">.</td>
			</tr>
			<tr>
				<td class="panelLabel" width="160"><s:text name="monitor.events.poEEth0MaxSpeed" /></td>
				<td class="panelText" id="poEEth0MaxSpeed">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEEth1MaxSpeed" /></td>
				<td class="panelText" id="poEEth1MaxSpeed">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEWifi0Setting" /></td>
				<td class="panelText" id="poEWifi0Setting">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEWifi1Setting" /></td>
				<td class="panelText" id="poEWifi1Setting">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.poEWifi2Setting" /></td>
				<td class="panelText" id="poEWifi2Setting">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et6" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType6">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.alarms.ifIndex" /></td>
				<td class="panelText" id="ifIndex2">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.radioChannel" /></td>
				<td class="panelText" id="radioChannel">.</td>
			</tr>
			<tr>
				<td class="panelLabel" width="140"><s:text name="monitor.events.radioTxPower" /></td>
				<td class="panelText" id="radioTxPower">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
	<tr id="et7" style="display: none;">
		<td style="padding-top:3px;" colspan="2">
		<fieldset style="padding:0 10px 0px 10px;"><legend
			id="eventType7">.</legend>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td class="panelLabel" width="140px"><s:text name="monitor.events.alertType" /></td>
				<td class="panelText" id="alertType">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.thresholdValue" /></td>
				<td class="panelText" id="thresholdValue">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.shorttermValue" /></td>
				<td class="panelText" id="shorttermValue">.</td>
			</tr>
			<tr>
				<td class="panelLabel"><s:text name="monitor.events.snapshotValue" /></td>
				<td class="panelText" id="snapshotValue">.</td>
			</tr>
		</table>
		</fieldset>
		</td>
	</tr>
</table>
</div>
<%--
<div class="ft"></div>
  --%></div>

</div>
