<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.admin.LogSettings"%>
<style>
td.labelT2 {
	padding: 8px 0px 6px 15px;
	vertical-align: top;
	line-height: 15px;
	width: 300px;
	
}
</style>
<script>
var formName = 'alarms';
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

var logSettingPanel = null;
function onLoadPage() {
	
	// create log setting overlay
	var div = document.getElementById('logSettingPanel');
	logSettingPanel = new YAHOO.widget.Panel(div, {
		width:"530px",
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
	startAlarmsPagingTimer();
}
function onUnloadPage() {
	clearTimeout(alarmsPagingTimeoutId);
}

function openLogSettingPanel()
{
	if(null != logSettingPanel){
        var url = "<s:url action="alarms" includeParams="none" />?operation=editAlarmLogSettings&ignore=" + new Date().getTime();
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
	if(result.alarmInterval != undefined){
		document.getElementById("alarmInterval").value = result.alarmInterval;
	}
	if(result.retainUnclearDays != undefined){
		document.getElementById("retainUnclearDays").value = result.retainUnclearDays;
	}
	if(result.maxRecords != undefined){
		document.getElementById("maxRecords").value = result.maxRecords;
	}
	if(result.reminderDays != undefined){
		document.getElementById("reminderDays").value = result.reminderDays;
	}
	logSettingPanel.cfg.setProperty('visible', true);
}
var alarmsPagingLiveCount = 0;
var alarmsPagingTimeoutId;
function startAlarmsPagingTimer() {
	var interval = 10;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (alarmsPagingLiveCount++ < total) {
		alarmsPagingTimeoutId = setTimeout("pollAlarmsPagingCache()", interval * 1000);  // seconds
	}
}
function pollAlarmsPagingCache() {
	var url = "<s:url action="alarms" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateAlarms }, null); 	
}

var cachedRefresh = false;
function updateAlarms(o) {
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
		if(!unallowRefresh && cachedRefresh){
			submitAction('refreshFromCache');
			return;
		}
		
		var td = document.getElementById("sev" + updates[i].id);
		if (td != null) {
			td.className = "list severity" + updates[i].sev;
			td.removeChild(td.firstChild);
			td.appendChild(document.createTextNode(updates[i].sevs));
		}
	}
	startAlarmsPagingTimer();
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
		        { text: '<s:text name="monitor.alarms.logRetain"/>'}
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
	
	if (text == '<s:text name="monitor.alarms.logRetain"/>') 
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
	var alarmInterval = document.getElementById("alarmInterval");
	var retainUnclearDays = document.getElementById("retainUnclearDays");
	var maxRecords = document.getElementById("maxRecords");
	var reminderDays = document.getElementById("reminderDays");
	
	if (alarmInterval.value.length == 0) 
	{
        hm.util.reportFieldError(alarmInterval, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_19.admin.logSet.alarmInterval" /></s:param></s:text>');
        alarmInterval.focus();
        return false;
    }
	
	if (retainUnclearDays.value.length == 0) 
	{
        hm.util.reportFieldError(retainUnclearDays, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_19.admin.logSet.retainUnclearDays" /></s:param></s:text>');
        retainUnclearDays.focus();
        return false;
    }
	if (maxRecords.value.length == 0) 
	{
        hm.util.reportFieldError(maxRecords, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_19.admin.logSet.maxRecords" /></s:param></s:text>');
        maxRecords.focus();
        return false;
    }
	if (reminderDays.value.length == 0) 
	{
        hm.util.reportFieldError(reminderDays, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_19.admin.logSet.reminderDays" /></s:param></s:text>');
        reminderDays.focus();
        return false;
    }
	
   if (!isValidInterval(alarmInterval.value)) 
    {
		hm.util.reportFieldError(alarmInterval, '<s:text name="error.keyValueRange"><s:param><s:text name="glasgow_19.admin.logSet.alarmInterval" /></s:param><s:param><s:text name="glasgow_19.admin.logSet.days" /></s:param></s:text>');
		alarmInterval.focus();
		return false;
	}
   if (!isValidRetainUnclearDays(retainUnclearDays.value)) 
    {
		hm.util.reportFieldError(retainUnclearDays, '<s:text name="error.keyValueRange"><s:param><s:text name="glasgow_19.admin.logSet.retainUnclearDays" /></s:param><s:param><s:text name="glasgow_19.admin.logSet.retainUnclearRange" /></s:param></s:text>');
		retainUnclearDays.focus();
		return false;
	}
   if (!isValidMaxRecords(maxRecords)) 
    {
		return false;
	}
   if (!isValidReminderDays(reminderDays.value)) 
    {
		hm.util.reportFieldError(reminderDays, '<s:text name="error.keyValueRange"><s:param><s:text name="glasgow_19.admin.logSet.reminderDays" /></s:param><s:param><s:text name="glasgow_19.admin.logSet.reminderRange" /></s:param></s:text>');
		reminderDays.focus();
		return false;
	}
	
	url = "<s:url action='alarms' includeParams='none' />" + "?operation=updateLogSetting"
		+ "&logSettings.alarmInterval="+alarmInterval.value
		+ "&logSettings.alarmRetainUnclearDays="+retainUnclearDays.value
		+ "&logSettings.alarmMaxRecords="+maxRecords.value
		+ "&logSettings.alarmReminderDays="+reminderDays.value
		+"&ignore=" + new Date().getTime();

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

function isValidRetainUnclearDays(obj)
{
	var intInterval = obj.valueOf();
	if ( intInterval >=1 && intInterval <= 100 )
	{
		return true;
	}
	
	return false;
}

function isValidMaxRecords(obj)
{
	var intInterval = obj.value.valueOf();
	var minValue;
	var maxValue;
	var errorParam;
	var 
	<s:if test="%{HMOnline}">
	   minValue=<%=LogSettings.ALARM_HMOL_MIN_RECORDS%>;
	   maxValue=<%=LogSettings.ALARM_HMOL_MAX_RECORDS%>;
	   errorParam="<s:text name="glasgow_19.admin.logSet.maxRecordsHMOLRange" />";
    </s:if><s:else>
       minValue=<%=LogSettings.ALARM_MIN_RECORDS%>;
       maxValue=<%=LogSettings.ALARM_MAX_RECORDS%>;
       errorParam="<s:text name="glasgow_19.admin.logSet.maxRecordsHMRange" />";
    </s:else>
	if ( intInterval >=minValue && intInterval <= maxValue )
	{
		return true;
	}
	hm.util.reportFieldError(maxRecords, '<s:text name="error.keyValueRange"><s:param><s:text name="glasgow_19.admin.logSet.maxRecords" /></s:param><s:param>'+errorParam+'</s:param></s:text>');
	maxRecords.focus();
	return false;
}

function isValidReminderDays(obj)
{
	var intInterval = obj.valueOf();
	if ( intInterval >=7 && intInterval <= 90 )
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
		showInfoDialog("Alarm log settings were updated successfully.");
	}else{
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', "Unable to update alarms log settings. DB error.");
			warnDialog.show();
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content"><s:form action="alarms">
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
					<td><input type="button" name="clear" value="Clear"
						class="button" onClick="submitAction('clear');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
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
					<th align="left" nowrap><ah:sort name="severity"
						key="monitor.alarms.severity" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
					<th align="left" nowrap><ah:sort name="apName"
						key="monitor.alarms.apName" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
					<th align="left" nowrap><ah:sort name="apId"
						key="monitor.alarms.apId" /></th>
					</s:if>
					<s:if test="%{columnId == 4}">
					<th align="left" nowrap><ah:sort name="trap_time"
						key="monitor.alarms.alarmTime" /></th>
					</s:if>
					<s:if test="%{columnId == 5}">
					<th align="left" nowrap><ah:sort name="clear_time"
						key="monitor.alarms.clearTime" /></th>	
					</s:if>
					<s:if test="%{columnId == 6}">
					<th align="left" nowrap><s:text name="monitor.alarms.alarmDesc" /></th>
					</s:if>
					<s:if test="%{columnId == 7}">
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
						<s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck"><input type="checkbox" disabled /></td>
						</s:if>
						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
						<td class="list severity<s:property value="%{severity}" />"
							id="sev<s:property value="%{#pageRow.id}"/>"><s:property
							value="%{severityString}" /></td>
						</s:if>
						<s:if test="%{columnId == 2}">
						<td class="list"><s:property value="%{apName}" /></td>
						</s:if>
						<s:if test="%{columnId == 3}">
						<td class="list"><s:property value="%{apId}" /></td>
						</s:if>
						<s:if test="%{columnId == 4}">
						<td class="list" nowrap><s:property value="%{trapTimeString}" /></td>
						</s:if>
						<s:if test="%{columnId == 5}">
						<td class="list" nowrap><s:property value="%{clearTimeString}" /></td>
						</s:if>
						<s:if test="%{columnId == 6}">
						<td class="list"><s:property value="%{trapDescString}" escape="false"/></td>
						</s:if>
						<s:if test="%{columnId == 7}">
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
		Alarm Log Settings
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
								<td class="labelT2">
									<label>
										<s:text name="glasgow_19.admin.logSet.alarmInterval"/><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="alarmInterval"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" 
										maxlength="2" size="8"/>
								<s:text name="glasgow_19.admin.logSet.days" />
								</td>
							</tr>
							<tr>
								<td class="labelT2">
									<label>
										<s:text name="glasgow_19.admin.logSet.retainUnclearDays"/><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="retainUnclearDays" value=""
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="3" size="8"/>
								   <s:text name="glasgow_19.admin.logSet.retainUnclearRange"/>
								</td>
							</tr>
							<tr>
								<td class="labelT2">
									<label>
										<s:text name="glasgow_19.admin.logSet.maxRecords"/><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="maxRecords" value=""
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="6" size="8"/>
								   <s:if test="%{HMOnline}">
								      <s:text name="glasgow_19.admin.logSet.maxRecordsHMOLRange"/>
								   </s:if><s:else>
								     <s:text name="glasgow_19.admin.logSet.maxRecordsHMRange"/>
								   </s:else>
								</td>
							</tr>
							<tr>
								<td class="labelT2">
									<label>
										<s:text name="glasgow_19.admin.logSet.reminderDays"/><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="reminderDays" value=""
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="2" size="8"/>
								  <s:text name="glasgow_19.admin.logSet.reminderRange"/>
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
	
</s:form></div>
