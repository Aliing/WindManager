<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'clientSurvey';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else {
    	doContinueOper();
	}
}
function doContinueOper() {
	showProcessing();
	document.forms[formName].operation.value = thisOperation;
	document.forms[formName].submit();
}
YAHOO.util.Event.onDOMReady(function () {
	createPowerSettingPanel();
	createRssiSettingPanel();

	var sMenu = new YAHOO.widget.Menu("s_menu", { fixedcenter: false });
	sMenu.addItems([
		    [
		        { text: '<s:text name="topology.menu.client.power"/>', onclick: { fn: onSettingMenuClick, obj: "erp" } },
		        { text: '<s:text name="topology.menu.client.rssi.range"/>', onclick: { fn: onSettingMenuClick, obj: "rssi" } }
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

});
var powerSettingPanel = null;
function createPowerSettingPanel(){
	var div = document.getElementById('powerSettingPanel');
	powerSettingPanel = new YAHOO.widget.Panel(div, {
		width:"345px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	powerSettingPanel.render(document.body);
	div.style.display = "";
}
var rssiSettingPanel = null;
function createRssiSettingPanel(){
	var div = document.getElementById('rssiSettingPanel');
	rssiSettingPanel = new YAHOO.widget.Panel(div, {
		width:"260px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	rssiSettingPanel.render(document.body);
	div.style.display = "";
}
function onSettingMenuClick(p_sType, p_aArgs, p_oValue) {
	if (p_oValue == 'erp') {
		if (powerSettingPanel != null) {
			powerSettingPanel.cfg.setProperty('visible', true);
			var url = "<s:url action='clientSurvey' includeParams='none' />" + "?operation=editPower&ignore=" + new Date().getTime();
			doAjaxRequest(url, doEditResponse);
		}
	} else if (p_oValue == 'rssi') {
		if (rssiSettingPanel != null) {
			rssiSettingPanel.cfg.setProperty('visible', true);
			var url = "<s:url action='clientSurvey' includeParams='none' />" + "?operation=editRssi&ignore=" + new Date().getTime();
			doAjaxRequest(url, doEditResponse);
		}
	}
}
function validatePower(){
	var erpEl = document.getElementById("erp");
	var message = hm.util.validateIntegerRange(erpEl.value, 'Power', 5, 20);
	if (message != null) {
		hm.util.reportFieldError(erpEl, message);
		erpEl.focus();
		return false;
	}
	return true;
}
function updatePowerSetting(){
	if(!validatePower()){
		return;
	}
	if (powerSettingPanel != null) {
		powerSettingPanel.cfg.setProperty('visible', false);
		var erp = document.getElementById("erp").value;
		var useErp = !document.getElementById("useErp").checked;
		var url = "<s:url action='clientSurvey' includeParams='none' />" + "?operation=updatePower&power=" + erp + "&usePower=" + useErp + "&ignore=" + new Date().getTime();
		doAjaxRequest(url, doUpdateResponse);
	}
}
function hidePowerSettingPanel(){
	if (powerSettingPanel != null) {
		powerSettingPanel.cfg.setProperty('visible', false);
	}
}
function validateRssi(){
	var lowRssiEl = document.getElementById("rssiLabel");
	var lowRssi = document.getElementById("lowRssiThreshold").value;
	var highRssi = document.getElementById("highRssiThreshold").value;
	if (-highRssi < -lowRssi) {
		var message = "Low threshold must be less or equal to high threshold.";
		hm.util.reportFieldError(lowRssiEl, message);
		lowRssiEl.focus();
		return false;
	}
	return true;
}
function updateRssiSetting(){
	if(!validateRssi()){
		return;
	}
	if (rssiSettingPanel != null) {
		rssiSettingPanel.cfg.setProperty('visible', false);
		var lowRssi = document.getElementById("lowRssiThreshold").value;
		var highRssi = document.getElementById("highRssiThreshold").value;
		var url = "<s:url action='clientSurvey' includeParams='none' />" + "?operation=updateRssi&lowRssi=" + lowRssi + "&highRssi=" + highRssi + "&ignore=" + new Date().getTime();
		doAjaxRequest(url, doUpdateRssiResponse);
	}
}
function hideRssiSettingPanel(){
	if (rssiSettingPanel != null) {
		rssiSettingPanel.cfg.setProperty('visible', false);
	}
}
function doAjaxRequest(url, callback){
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : callback, failure : abortResult, timeout: 30000}, null);
}
var abortResult = function(o) {
}
function doEditResponse(o){
	eval("var result = " + o.responseText);
	if (result.erp != undefined){
		var erp = document.getElementById("erp");
		erp.value = result.erp;
		erp.disabled = !result.useErp;
		document.getElementById("useErp").checked = !result.useErp;
	} else if (result.lowRssi != undefined){
		document.getElementById("lowRssiThreshold").value = -result.lowRssi;
		document.getElementById("highRssiThreshold").value = -result.highRssi;
	}
}
function doUpdateResponse(o){
	eval("var result = " + o.responseText);
	if (result.suc) {
		showInfoDialog("Update Successful.");
	} else {
		showInfoDialog("Update failed.");
	}
}
function doUpdateRssiResponse(o){
	eval("var result = " + o.responseText);
	if (result.suc) {
		submitAction('list');
	} else {
		showInfoDialog("Update failed.");
	}
}
function useErpEvent(checked) {
	document.getElementById("erp").disabled = checked;
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>
<div id="content"><s:form action="clientSurvey">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="setting" value="Settings..."
						class="button" id="s_menutoggle"
						<s:property value="writeDisabled" />></td>
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
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<th><ah:sort name="parentMap.mapName" key="hiveAp.topology" /></th>
					<th><ah:sort name="rid" key="monitor.client.rid" /></th>
<%--
					<th><s:text name="monitor.client.position" /></th>
					<th><ah:sort name="elevation" key="monitor.client.elevation" /></th>
  --%>
					<th><ah:sort name="frequency" key="monitor.client.frequency" /></th>
					<th><ah:sort name="tid" key="monitor.client.tid" /></th>
					<th><s:text name="monitor.client.position" /></th>
					<th><ah:sort name="erp" key="monitor.client.terp" /></th>
					<th>&nbsp;<ah:sort name="rssi" key="monitor.client.rssi" /></th>
					<th><ah:sort name="note" key="monitor.client.note" /></th>
					<th><ah:sort name="timeStamp.time" key="monitor.hiveAp.report.time" /></th>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck"><ah:checkItem /></td>
						<td class="list"><s:property value="mapName" /></td>
						<td class="list"><s:property value="rid" /></td>
<%--
						<td class="list"><s:property value="rposition" /></td>
						<td class="list"><s:property value="elevationString" /></td>
  --%>
						<td class="list"><s:property value="frequency" /></td>
						<td class="list"><s:property value="tid" /></td>
						<td class="list"><s:property value="tposition" /></td>
						<td class="list"><s:property value="erpString" /></td>
						<s:if test="%{filtered}">
							<td class="list" style="background-color: rgb(255, 221, 170);"><s:property
								value="rssiString" /></td>
						</s:if>
						<s:else>
							<td class="list"><s:property value="rssiString" /></td>
						</s:else>
						<td class="list"><s:property value="noteString" /></td>
						<td class="list"><s:property value="timeStampString" /></td>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="powerSettingPanel" style="display: none;">
<div class="hd"><s:text name="topology.menu.client.power" /></div>
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
						<td width="160px;"><s:text
							name="topology.menu.client.power.estimated" /></td>
						<td><s:checkbox name="useErp"
							onclick="useErpEvent(this.checked);" /></td>
					</tr>
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td><s:text name="topology.menu.client.power.actual" /></td>
						<td><s:textfield name="erp" size="8" maxlength="4"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
						<td nowrap="nowrap">&nbsp;&nbsp;<s:text
							name="topology.menu.client.power.range" /></td>
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
					onClick="updatePowerSetting();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="hidePowerSettingPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
<div id="rssiSettingPanel" style="display: none;">
<div class="hd"><s:text name="topology.menu.client.rssi.range" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 7px 5px 8px 5px;">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td height="1" colspan="3">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td id="rssiLabel"></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td width="145px;"><s:text
							name="topology.menu.client.rssi.range.high" /></td>
						<td align="right"><s:select id="highRssiThreshold"
							name="highRssiThreshold" list="%{rssiThresholdValues}"
							listKey="id" listValue="value" /></td>
						<td nowrap style="padding: 1px 0px 1px 5px;">dBm</td>
					</tr>
					<tr>
						<td height="5" colspan="3"></td>
					</tr>
					<tr>
						<td><s:text name="topology.menu.client.rssi.range.low" /></td>
						<td align="right"><s:select id="lowRssiThreshold"
							name="lowRssiThreshold" list="%{rssiThresholdValues}"
							listKey="id" listValue="value" /></td>
						<td nowrap style="padding: 1px 0px 1px 5px;">dBm</td>
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
					onClick="updateRssiSetting();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="hideRssiSettingPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>
