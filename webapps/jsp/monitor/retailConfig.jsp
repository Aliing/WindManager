<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css" href="<s:url value="/css/jquery.powertip.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<script type="text/javascript" src="<s:url value="/js/jquery.powertip-1.1.0.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script type="text/javascript" src="<s:url value="/js/doT.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<style type="text/css">
<!--
#retailConfig .title{
	margin: 0;
	font-size: 16px;
}
#retailConfig .info{
	padding: 5px 0;
	margin-top: 5px;
	font-size: 14px;
	color: #333;
}
#retailConfig .tip{
	color: #666;
	padding-left: 15px;
}
#retailConfig .link{
	padding-left: 15px;
	margin-top: 5px;
}

.moveBox{
	margin-top: 50px; 
	padding-left: 2px
}

.moveBox div{
	cursor: pointer;
	margin: 2px 0;
}

.table th,
.table td {
  padding: 8px;
  line-height: 20px;
  text-align: left;
  vertical-align: top;
  border-bottom: 1px solid #dddddd;
}

.table th {
  font-weight: bold;
  background: #eeeeee;
}

.devices {
	width: 200px;
}

.devicesTitle{
	font-style: italic;
	color: #333;
}
#questionMark{
	font-weight: bold;
	cursor: pointer;
	color: blue;
}
#powerTip{
	white-space: normal;
}
-->
</style>

<script id="availableDevicesTmpl" type="text/x-dot-template">
    <div class="devicesTitle"><s:text name="presence.retail.analytics.store.config.mapping.available" /> ({{=it.length}})</div>
    <div>
    	<select multiple="multiple" id="avaliableDevices" name="avaliableDevices" class="devices" size="17">
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<option value={{=it[i].mac}}>{{=it[i].host}}</option>
{{ } }}
		</select>
    </div>
</script>

<script id="selectedDevicesTmpl" type="text/x-dot-template">
    <div class="devicesTitle"><s:text name="presence.retail.analytics.store.config.mapping.selected" /> ({{=it.length}})</div>
    <div>
    	<select multiple="multiple" id="selectedDevices" name="selectedDevices" class="devices" size="8">
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<option value={{=it[i].mac}}>{{=it[i].host}}</option>
{{ } }}
    	</select>
    </div>
</script>

<script id="otherDevicesTmpl" type="text/x-dot-template">
    <div class="devicesTitle"><s:text name="presence.retail.analytics.store.config.mapping.other" /> ({{=it.length}})</div>
    <div>
    	<select multiple="multiple" name="otherDevices" class="devices" size="6">
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<option value={{=it[i].mac}}>{{=it[i].host}}</option>
{{ } }}
		</select>
    </div>
</script>

<script id="storeListTmpl" type="text/x-dot-template">
<table width="100%" class="table" border="0" cellspacing="0" cellpadding="0">
<tr>
	<th width="5px"></th>
	<th><s:text name="presence.retail.analytics.store.list.name" /></th>
	<th><s:text name="presence.retail.analytics.store.list.device" /></th>
	<th><s:text name="presence.retail.analytics.store.list.description" /></th>
</tr>
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<tr>
				<td class="list"><input type="checkbox" name="selectedStores" value="{{=it[i].name}}"></td>
				<td class="list"><a onclick="editStore({{=it[i].id}});" href="javascript: ;;">{{=it[i].name}}</a></td>
				<td class="list">{{=it[i].count}}</td>
				<td class="list">{{=it[i].addr + (it[i].city ? (", " + it[i].city) : "") + (it[i].state ? (", " + it[i].state) : "")}}</td>
			</tr>
{{ } }}
</table>
</script>

<script type="text/javascript">
var formName = 'retailAnalytics';
var retailConfigFormName = "storeConfigForm";

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'back'){
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function doContinueOper(){
	removeStore();
}

//don't allow chars : 34" 36$ 37% 38& 44, 59; and 63?
var _STRINGAllowedPresence = [32,33,35,39,40,41,42,43,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
var _STRINGAllowedPresenceName = [45,48,49,50,
      						51,52,53,54,55,56,57,65,66,67,68,69,
      						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
      						89,90,95,97,98,99,100,101,102,103,104,105,
      						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
      						120,121,122];
						
function validate(operation){
	if("postStore" == operation){
		var storeName = document.getElementById(retailConfigFormName + "_storeName");
		var storeAddr = document.getElementById(retailConfigFormName + "_storeAddr");
		var storeCity = document.getElementById(retailConfigFormName + "_storeCity");
		var storeState = document.getElementById(retailConfigFormName + "_storeState");
		var message = _validateString(_STRINGAllowedPresenceName, storeName.value, '<s:text name="presence.retail.analytics.store.config.name" />');
		if (message != null) {
		    hm.util.reportFieldError(document.getElementById("storeNameInput"), message);
		    storeName.focus();
		    return false;
		}
		<%--if(storeAddr.value.length > 0 ){
		var message = _validateString(_STRINGAllowedPresence, storeAddr.value, '<s:text name="presence.retail.analytics.store.config.address" />');
		if (message != null) {
		    hm.util.reportFieldError(storeAddr, message);
		    storeAddr.focus();
		    return false;
		}
		}
		if(storeCity.value.length > 0 ){
		var message = _validateString(_STRINGAllowedPresence, storeCity.value, '<s:text name="presence.retail.analytics.store.config.city" />');
		if (message != null) {
		    hm.util.reportFieldError(storeCity, message);
		    storeCity.focus();
		    return false;
		}
		}
		if(storeState.value.length > 0 ){
		var message = _validateString(_STRINGAllowedPresence, storeState.value, '<s:text name="presence.retail.analytics.store.config.state" />');
		if (message != null) {
		    hm.util.reportFieldError(storeState, message);
		    storeState.focus();
		    return false;
		}
		}--%>
		return true;
	}else if("removeStore" == operation){
		var stores = document.getElementsByName("selectedStores");
		var selectedCount = 0;
		for(var i=0; i<stores.length; i++){
			if(stores[i].checked){
				selectedCount++;
			}
		}
		if(selectedCount != 1){
			showWarnDialog("<s:text name="action.error.select.one.item" />");
			return false;
		}
		return true;
	}else if("newStore" == operation){
		var stores = _Data.stores || [];
		if(stores.length >= 12){
			showWarnDialog("<s:text name="error.presence.store.upto.maximum" />");
			return false;
		}
		return true;
	}
	return true;
}

var storePanel;
var loadingInnerHTML;
function onLoadPage(){
	loadingInnerHTML = document.getElementById("storeListTmplWrapper").innerHTML;
	loadStoreData();
	createTooltip();
}

function createTooltip(){
	$('#questionMark').data('powertipjq', $('<div style="width: 230px;"><s:text name="presence.retail.analytics.store.config.mapping.tip" /></p>'));
   	$('#questionMark').powerTip({
   		placement: 'e',
   		mouseOnToPopup: true
   	});
}

function createStorePanel(){
	var div = window.document.getElementById('storePanel');
	storePanel = new YAHOO.widget.Panel(div, { width:"480px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	storePanel.render(document.body);
	div.style.display = "";
}

function newStore(){
	<%--can create more stores for lite customer
	if(!validate("newStore")){
		return false;
	}--%>
	if(null == storePanel){
		createStorePanel();
	}
	document.getElementById(retailConfigFormName + "_storeName").value = "";
	document.getElementById(retailConfigFormName + "_storeAddr").value = "";
	document.getElementById(retailConfigFormName + "_storeCity").value = "";
	document.getElementById(retailConfigFormName + "_storeState").value = "";
	
	hm.util.show("storeNameInput");
	hm.util.hide("storeNameLabel")
	
	var deviceArrays = getDeviceArrays();
	fillDeviceData("availableDevicesTmplWrapper", "availableDevicesTmpl", deviceArrays.t);
	fillDeviceData("selectedDevicesTmplWrapper", "selectedDevicesTmpl", deviceArrays.s);
	fillDeviceData("otherDevicesTmplWrapper", "otherDevicesTmpl", deviceArrays.o);
	storePanel.setHeader("<s:text name="presence.retail.analytics.store.config.title" />");
	storePanel.show();
	document.getElementById(retailConfigFormName + "_storeName").focus();
}

function editStore(storeId){
	var storeData = _Data[storeId];
	if(storeData == undefined){
		showWarnDialog("Cannot load store data for store: " + storeId);
		return;
	}
	if(null == storePanel){
		createStorePanel();
	}
	var store = storeData.store;
	var sensors = storeData.sensors;
	var name = store.name;
	var addr = store.address || "";
	var city = store.city || "";
	var state = store.state || "";
	var timezone = store.time_zone || "";
	
	document.getElementById(retailConfigFormName + "_storeName").value = name;
	document.getElementById(retailConfigFormName + "_storeAddr").value = addr;
	document.getElementById(retailConfigFormName + "_storeCity").value = city;
	document.getElementById(retailConfigFormName + "_storeState").value = state;
	document.getElementById(retailConfigFormName + "_storeTimezone").value = timezone;
	document.getElementById("storeNameLabel").innerHTML = name;
	
	hm.util.hide("storeNameInput");
	hm.util.show("storeNameLabel")
	
	var deviceArrays = getDeviceArrays(storeId);
	fillDeviceData("availableDevicesTmplWrapper", "availableDevicesTmpl", deviceArrays.t);
	fillDeviceData("selectedDevicesTmplWrapper", "selectedDevicesTmpl", deviceArrays.s);
	fillDeviceData("otherDevicesTmplWrapper", "otherDevicesTmpl", deviceArrays.o);
	storePanel.setHeader("<s:text name="presence.retail.analytics.store.config.title" />" + " - " + name);
	storePanel.show();
	document.getElementById(retailConfigFormName + "_storeAddr").focus();
}

function getDeviceArrays(storeId){
	var total=[], selected=[], other=[], length=_TotalDevices.length;
	var stores = _Data.stores ? _Data.stores : [];
	for(var i=0; i<length; i++){
		var device = _TotalDevices[i];
		var flag = "t";
		for(var j=0; j<stores.length; j++){
			var currentStoreId = stores[j].id;
			var sensors = _Data[currentStoreId].sensors;
			if(sensors != undefined && sensors.length > 0){
				for(var k=0; k<sensors.length; k++){
					var sensorMac = sensors[k].mac_address;
					if(device.mac == sensorMac){
						flag = currentStoreId == storeId ? "s" : "o";
						break;
					}
				}
			}
		}
		if("s" == flag){
			selected.push(device);
		}else if("o" == flag){
			other.push(device);
		}else{
			total.push(device);
		}
	}
	return {"t": total, "s": selected, "o": other};
}

function selectedAllDevices(selectElement){
	var selectElement = document.getElementById(selectElement);
	var options = selectElement.options;
	for(var i=0; i<options.length; i++){
		options[i].selected = true;
	}
}

function hideStorePanel(){
	if(null != storePanel){
		storePanel.hide();
	}
}

function postStore(){
	if(validate("postStore")){
		selectedAllDevices("selectedDevices");
		showWaitingPanel();
		var isEdit = document.getElementById("storeNameInput").style.display == "none";
		var url = "<s:url action='retailAnalytics' includeParams='none'/>";
        url += (isEdit ? "?operation=updateStore&ignore=" : "?operation=createStore&ignore=") + new Date().getTime();
		ajaxRequest("storeConfigForm", url, createStoreSuccess, "POST");
	}
}

function createStoreSuccess(o){
	hideWaitingPanel();
	eval("var result = " + o.responseText);
	if(result.msg){
		showWarnDialog(result.msg);
	}
	if(result.fs){
		showWarnDialog(result.fs);
	}else if(result.suc){
		showPageNotes(result.suc, {type: "info"});
		hideStorePanel();
		loadStoreData();
	}
}

function removeStoreRequest(){
	//fix bug 33013
	/* if(validate("removeStore")){
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected item.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	} */
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected item.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

function removeStore(){
	showWaitingPanel();
	var url = "<s:url action='retailAnalytics' includeParams='none'/>?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = "removeStore";
	ajaxRequest("retailAnalytics", url, removeStoreSuccess, "POST");
}

function removeStoreSuccess(o){
	hideWaitingPanel();
	eval("var result = " + o.responseText);
	if(result.msg){
		showWarnDialog(result.msg);
	}
	if(result.suc){
		showPageNotes(result.suc, {type: "info"});
		loadStoreData();
	}
}

function fillDeviceData(elem, tmpl, json){
	document.getElementById(elem).innerHTML = doT.template(document.getElementById(tmpl).innerHTML)(json);
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="retailAnalytics" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="button.configure" />');
	document.writeln('</td>');}
</script>

<script type="text/javascript">
var _Data = {};
var _TotalDevices = <s:property value="totalDevices" escapeHtml="false" />;
	
function loadStoreData(){
	document.getElementById("storeActionTd").style.display = "none";
	document.getElementById("storeRefreshTd").style.display = "none";
	document.getElementById("storeListTmplWrapper").innerHTML = loadingInnerHTML;
	var url = "<s:url action='retailAnalytics' includeParams='none'/>" + "?operation=loadStoreData&ignore=" + new Date().getTime();
    ajaxRequest(null, url, loadStoreDataDone);
}

function loadStoreDataDone(o){
	eval("var result = " + o.responseText);
	if(result.stores){
		_Data = result;
		var stores = _Data.stores;
		var json = [];
		if(stores != null){
			for(var i=0; i<stores.length; i++){
				var id = stores[i].id;
				var storeObj = result[id];
				var count = storeObj ? (result[id].sensors ? storeObj.sensors.length:0) : 0;
				json.push({"id": stores[i].id,"name": stores[i].name, "count": count, "desc": stores[i].client_supplied_id || "", "addr": stores[i].address || "", "city": stores[i].city || "", "state": stores[i].state || ""});
			}
		}
		fillDeviceData("storeListTmplWrapper", "storeListTmpl", json);
		document.getElementById("storeActionTd").style.display = "";
	}else{
		showWarnDialog(result.msg);
		document.getElementById("storeRefreshTd").style.display = "";
	}
}

function refreshStores(){
	loadStoreData();
}

function addDevice(){
	var available = document.getElementById("avaliableDevices");
	var selected = document.getElementById("selectedDevices")
	var size = available.options.length;
	var adds = [];
	for(var i=0; i<size; i++){
		if(available.options[i].selected){
			adds.push({"text": available.options[i].text, "value": available.options[i].value});
		}
	}
	if(adds.length == 0){
		showWarnDialog("<s:text name="info.selectObject" />");
	}else{
		addOptions(selected, adds, true);
		removeOptions(available, adds);
	}
	verifySelectedDeviceRadioProfile(adds);
}

function removeDevice(){
	var available = document.getElementById("avaliableDevices");
	var selected = document.getElementById("selectedDevices")
	var size = selected.options.length;
	var removes = [];
	for(var i=0; i<size; i++){
		if(selected.options[i].selected){
			removes.push({"text": selected.options[i].text, "value": selected.options[i].value});
		}
	}
	if(removes.length == 0){
		showWarnDialog("<s:text name="info.selectObject" />");
	}else{
		removeOptions(selected, removes);
		addOptions(available, removes, false);
	}
}

function verifySelectedDeviceRadioProfile(items){
	var unpass = [];
	for(var i=0; i<items.length; i++){
		var mac = items[i].value;
		for(var j=0; j<_TotalDevices.length; j++){
			var device = _TotalDevices[j];
			if(!device.enabled && device.mac == mac){
				unpass.push(device.host);
				break;
			}
		}
	}
	if(unpass.length > 0){
		showWarnDialog('<s:text name="warn.presence.device.radioprofile.enable" />'.replace(/\{0\}/, unpass.join(", ")));
	}
}

</script>

<script type="text/javascript">
function addOptions(selector, items, isSelect){
	if(null == selector){
		return;
	}
	for(var i=0; i<items.length; i++){
		var text = items[i].text, value = items[i].value;
		var option = new Option(text, value, isSelect);
		try{
			selector.add(option, null); // DOM
		}catch(e){
			selector.add(option); // IE
		}
	}
}
function removeOptions(selector, items){
	if(null == selector){
		return;
	}
	for(var j=0; j<items.length; j++){
		for(var i=0; i<selector.length; i++){
			if(selector.options[i].value == items[j].value){
				selector.remove(i);
				break;
			}
		}
	}
}
var waitingPanel = null;
function createWaitingPanel() {
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
	waitingPanel.setHeader("Processing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}
function showWaitingPanel(){
	if(null == waitingPanel){
		createWaitingPanel();
	}
	waitingPanel.show();
}
function hideWaitingPanel(){
	if(null != waitingPanel){
		waitingPanel.hide();
	}
}
</script>

<div id="content">
	<s:form action="retailAnalytics">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="ignore" value="<s:text name="button.back" />"
							class="button" onClick="submitAction('back');"
							<s:property value="writeDisabled" />></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table id="retailConfig" class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
						<tr>
							<td style="padding: 10px">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td>
											<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr><td><div class="title"><s:text name="presence.retail.analytics.config.summary" /></div></td></tr>
												<tr>
													<td class="sepLine" colspan="3"><img
														src="<s:url value="/images/spacer.gif"/>" height="1"
														class="dblk" /></td>
												</tr>
												<tr><td><div class="info"><s:text name="presence.retail.analytics.config.step1" /></div></td></tr>
												<tr><td><div class="tip"><s:text name="presence.retail.analytics.config.comment1" /></div></td></tr>
												<tr><td><div class="info"><s:text name="presence.retail.analytics.config.step2" /></div></td></tr>
												<tr><td><div class="tip"><s:text name="presence.retail.analytics.config.comment2" /></div></td></tr>
												<%-- <tr><td><div class="link"><a target="_blank" href="<s:property value="customerRetailPermiumSignUrl"/>"><s:text name="presence.retail.analytics.config.sign" /></a></div></td></tr> --%>
											</table>
										</td>
									</tr>
									<tr>
										<td height="20"></td>
									</tr>
									<tr>
										<td>
											<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><div class="title"><s:text name="presence.retail.analytics.store.label" /></div></td>
												</tr>
												<tr>
													<td class="sepLine" colspan="3"><img
														src="<s:url value="/images/spacer.gif"/>" height="1"
														class="dblk" /></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="5"></td>
									</tr>
									<tr>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="display: none; height: 22px;" id="storeActionTd">
														<input type="button" name="new" value="New" class="button"
														onClick="newStore();" <s:property value="writeDisabled" />>
														&nbsp;
														<input type="button" name="remove" value="<s:text name="button.remove" />" class="button"
														onClick="removeStoreRequest();" <s:property value="writeDisabled" />>
													</td>
													<td style="display: none; height: 22px;" id="storeRefreshTd">
														<input type="button" name="refresh" value="<s:text name="common.button.refresh" />" class="button"
														onClick="refreshStores();" <s:property value="writeDisabled" />>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="5"></td>
									</tr>
									<tr>
										<td id="storeListTmplWrapper">
											<table width="100%" class="table" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<th><s:text name="presence.retail.analytics.store.list.name" /></th>
													<th><s:text name="presence.retail.analytics.store.list.device" /></th>
													<th><s:text name="presence.retail.analytics.store.list.description" /></th>
												</tr>
												<tr>
													<td colspan="3" align="center">Loading data...</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td height="10"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<div id="storePanel" style="display: none;">
    <div class="hd"><s:text name="presence.retail.analytics.store.config.title" /></div>
    <div class="bd"><s:form action="retailAnalytics" name="storeConfigForm" id="storeConfigForm">
    	<table class="settingBox" width="100%" border="0" cellspacing="0" cellpadding="0">
    		<tr>
    			<td>
    				<table border="0" cellspacing="0" cellpadding="0">
    					<tr>
    						<td class="labelT1" width="120px"><s:text name="presence.retail.analytics.store.config.name" /><font color="red"><s:text name="*" /></font></td>
    						<td><span id="storeNameInput"><s:textfield name="storeName" cssStyle="width:180px;" size="32" maxlength="32" /> <s:text name="character.range.1.to.32" /></span><label id="storeNameLabel"></label></td>
    					</tr>
    					<tr>
    						<td class="labelT1"><s:text name="presence.retail.analytics.store.config.timezone" /><font color="red"><s:text name="*" /></font></td>
    						<td><s:select name="storeTimezone" cssStyle="width:186px;" list="%{timeZones}" listKey="key" listValue="value" value="%{storeTimezone}" /></td>
    					</tr>
    					<tr>
    						<td class="labelT1"><s:text name="presence.retail.analytics.store.config.address" /></td>
    						<td><s:textfield name="storeAddr" cssStyle="width:180px;" size="32" maxlength="256" /></td>
    					</tr>
    					<tr>
    						<td class="labelT1"><s:text name="presence.retail.analytics.store.config.city" /></td>
    						<td><s:textfield name="storeCity" cssStyle="width:180px;" size="32" maxlength="256" /></td>
    					</tr>
    					<tr>
    						<td class="labelT1"><s:text name="presence.retail.analytics.store.config.state" /></td>
    						<td><s:textfield name="storeState" cssStyle="width:180px;" size="32" maxlength="256" /></td>
    					</tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">
    					<tr><td class="labelT1"><s:text name="presence.retail.analytics.store.config.mapping" />&nbsp;<span id="questionMark">?</span></td></tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding-left: 10px;">
    				<table border="0" cellspacing="0" cellpadding="0">
    					<tr>
    						<td rowspan="2" id="availableDevicesTmplWrapper"></td>
    						<td width="20px" rowspan="2" valign="top">
    							<div class="moveBox">
    								<div><input type="button" class="transfer"  onclick="addDevice();" value="&gt;"></div>
    								<div><input type="button" class="transfer"  onclick="removeDevice();" value="&lt;"></div>
    							</div>
    						</td>
    						<td valign="top" id="selectedDevicesTmplWrapper"></td>
    					</tr>
    					<tr>
    						<td valign="bottom" id="otherDevicesTmplWrapper"></td>
    					</tr>
    				</table>
    			</td>
    		</tr>
			<tr>
				<td height="10"></td>
			</tr>
    		<tr>
    			<td style="padding-left: 10px;">
    				<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="<s:text name="button.create" />"
								class="button" onClick="postStore();"
								<s:property value="writeDisabled" />></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideStorePanel();"
								<s:property value="writeDisabled" />></td>
						</tr>
    				</table>
    			</td>
    		</tr>
			<tr>
				<td height="10"></td>
			</tr>
    	</table></s:form>
    </div>
</div>
<s:if test="%{firstTime}">
<tiles:insertDefinition name="retailDecal" />
<script type="text/javascript">
YAHOO.util.Event.on(window, "load", function(){showDecalPanel();});
</script>
</s:if>