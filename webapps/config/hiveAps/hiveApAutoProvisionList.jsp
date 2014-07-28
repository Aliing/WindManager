<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'autoProvisioningConfig';
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
	if(thisOperation == 'removeSnOut'){
		//var url = "<s:url action='autoProvisioningConfig' includeParams='none' />" + "?operation=removeSnOut&snString="+selectItems+"&ignore=" + new Date().getTime();
		//doAjaxRequest(url, doEditResponse);
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />";
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : doEditResponse }, "operation=removeSnOut&snString="+selectItems+"&ignore=" + new Date().getTime());
		return;
	}else if(thisOperation == 'saveSn'){
		if(!validateSerialNumber()){
			return;
		}
	}else if(thisOperation == 'removeIpSubNetworkOut'){
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />";
		//doAjaxRequest(url, doIpManagementOutResponse);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : doIpManagementOutResponse }, "operation=removeIpSubNetworkOut&ipSubNetworkString="+selectItems+"&ignore=" + new Date().getTime());
		return;
	}else if (thisOperation == 'saveIpSubNetwork'){
		enterIpSubNetworks();
		return;
	}
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function validateSerialNumber(){
	var scanEl = document.getElementById("scanSerialNumbers");
	if(scanEl.value.length == 0){
		hm.util.reportFieldError(scanEl, "Serial Numbers is a required.");
		scanEl.focus();
		return false;
	}
	var key = (YAHOO.env.ua.ie||YAHOO.env.ua.opera)? "\r\n" : "\n";
	var sns = scanEl.value.split(key);
	for(var i=0; i<sns.length; i++){
		var item = sns[i];
		if(item.length >0 && item.length != 14){
			hm.util.reportFieldError(scanEl, "The length of a serial number is 14.");
			scanEl.focus();
			return false;
		}
	}
	document.forms[formName].scanSerialNumbers.value = scanEl.value;
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
YAHOO.util.Event.onDOMReady(function () {
	createManagementPanel();
	createIpManagementPanel();
	var operation = "<s:property value="%{operation}" />";
	if("continue" == operation|| "saveSn" == operation){
		showManagementPanel();
	} else if ("continue1" == operation) {
		showIPManagementPanel();
	}
});

var managementPanel = null;
function createManagementPanel(){
	var div = document.getElementById('managementPanel');
	managementPanel = new YAHOO.widget.Panel(div, {
		width:"430px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	managementPanel.render(document.body);
	div.style.display = "";
}

var ipManagementPanel = null;
function createIpManagementPanel(){
	var div = document.getElementById('ipManagementPanel');
	ipManagementPanel = new YAHOO.widget.Panel(div, {
		width:"430px",
		visible:false,
		draggable:true,
		fixedcenter:true,
		constraintoviewport:true
		});
	ipManagementPanel.render(document.body);
	div.style.display = "";
}

function showManagementPanel() {
	if (managementPanel != null) {
		managementPanel.cfg.setProperty('visible', true);
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />" + "?operation=editSnOut&ignore=" + new Date().getTime();
		doAjaxRequest(url, doEditResponse);
		showPanel(0);
	}
}

function showIPManagementPanel() {
	if (ipManagementPanel != null) {
		ipManagementPanel.cfg.setProperty('visible', true);
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />" + "?operation=ipManagementOut&ignore=" + new Date().getTime();
		doAjaxRequest(url, doIpManagementOutResponse);
		showIpPanel(0);
	}
}

function removeSerialNumbers(){
	var selectEl = document.getElementById("snItems");
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
		thisOperation = "removeSnOut";
		hm.util.confirmRemoveItems();
	}
}

function removeIpSubNetworks(){
	var selectEl = document.getElementById("ipItems");
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
		thisOperation = "removeIpSubNetworkOut";
		hm.util.confirmRemoveItems();
	}
}

function enterIpSubNetworks(){
	if (validateIpSubnetworks()) {
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />" + "?ignore=" + new Date().getTime();
		document.forms[formName].operation.value = 'enterIpSubNetworkOut';
		YAHOO.util.Connect.setForm(document.forms[formName]);
		doAjaxRequest(url, doIpManagementOutResponse1);
	}
}

function validateIpSubnetworks(){
	var scanEl = document.getElementById("enteredIpSubnetworks");
	if(scanEl.value.length == 0){
		hm.util.reportFieldError(scanEl, "IP Subnetworks is required.");
		scanEl.focus();
		return false;
	}
	var ipSubsValue = scanEl.value.replace(/\r\n|\n/ig,',');
	ipSubsValue = ipSubsValue.replace(/[,]{2,}/ig,',');
	var ips = ipSubsValue.split(',');
	for(var i=0; i<ips.length; i++){
		var item = ips[i];
		if (item == null || item == '') continue;
		var entry = item.split("/");
		if (entry.length < 2) {
			if (!hm.util.validateIpAddress(item)) {
				hm.util.reportFieldError(scanEl, "IP subnetwork format is invalid.");
				scanEl.focus();
				return false;
			}
		} else {
			if (!hm.util.validateIpAddress(entry[0]) || entry[1]>=32 || entry[1]<=0) {
				hm.util.reportFieldError(scanEl, "IP subnetwork format is invalid.");
				scanEl.focus();
				return false;
			}
		}
	}
	document.forms[formName].ipSubNetworkString.value = ipSubsValue;
	return true;
}

function isNullString(str) {
	if (str == null || str == '') {
		return true;
	}
	return false;
}

function hideManagementPanel(){
	if(managementPanel != null){
		managementPanel.cfg.setProperty('visible', false);
	}
}

function hideIpManagementPanel(){
	if(ipManagementPanel != null){
		ipManagementPanel.cfg.setProperty('visible', false);
	}
}

function showPanel(index){
	var scanBtnTr = document.getElementById("scanBtnTr");
	var selectTr = document.getElementById("selectTr");
	var scanTr= document.getElementById("scanTr");
	var selectBtnTr = document.getElementById("selectBtnTr");
	var summaryTr = document.getElementById("summaryTr");
	var summaryBtnTr = document.getElementById("summaryBtnTr");

	var scanEl = document.getElementById("scanSerialNumbers");
	var searchEl = document.getElementById("searchSerialNumber");
	switch(index){
	case 0:// list panel
		if(selectTr)selectTr.style.display = "";
		if(scanTr)scanTr.style.display = "none";
		if(summaryTr)summaryTr.style.display = "none";
		if(selectBtnTr)selectBtnTr.style.display = "";
		if(scanBtnTr)scanBtnTr.style.display = "none";
		if(summaryBtnTr)summaryBtnTr.style.display = "none";
		if(null != managementPanel){
			managementPanel.header.innerHTML = "<s:text name="hiveAp.autoProvisioning.aclType.sn" />";
		}
		if(scanEl){scanEl.value = "";}
		if(searchEl){searchEl.value = "";}
		break;
	case 1: // scan panel
		if(selectTr)selectTr.style.display = "none";
		if(scanTr)scanTr.style.display = "";
		if(summaryTr)summaryTr.style.display = "none";
		if(selectBtnTr)selectBtnTr.style.display = "none";
		if(scanBtnTr)scanBtnTr.style.display = "";
		if(summaryBtnTr)summaryBtnTr.style.display = "none";
		if(null != managementPanel){
			managementPanel.header.innerHTML = "<s:text name="hiveAp.autoProvisioning.scan.sn" />";
		}
		if(scanEl){scanEl.focus();}
		if(searchEl){searchEl.value = "";}
		break;
	case 2: // review panel
		if(selectTr)selectTr.style.display = "none";
		if(scanTr)scanTr.style.display = "none";
		if(summaryTr)summaryTr.style.display = "";
		if(selectBtnTr)selectBtnTr.style.display = "none";
		if(scanBtnTr)scanBtnTr.style.display = "none";
		if(summaryBtnTr)summaryBtnTr.style.display = "";
		if(null != managementPanel){
			managementPanel.header.innerHTML = "<s:text name="hiveAp.autoProvisioning.review.sn" />";
		}
		if(scanEl){scanEl.value = "";}
		if(searchEl){searchEl.focus();}
		// retrive serial numbers here, instead of retrive on page loaded.
		var url = "<s:url action='autoProvisioningConfig' includeParams='none' />" + "?operation=fetchAllSerialNumber&ignore=" + new Date().getTime();
		doAjaxRequest(url, fillAllSerialNumber);
		break;
	}
}

function showIpPanel(index){
	var selectIpTr = document.getElementById("selectIpTr");
	var enterIpTr = document.getElementById("enterIpTr");
	var selectBtnIpTr= document.getElementById("selectBtnIpTr");
	var enterBtnIpTr = document.getElementById("enterBtnIpTr");

	switch(index){
	case 0:// list panel
		if(selectIpTr)selectIpTr.style.display = "";
		if(selectBtnIpTr)selectBtnIpTr.style.display = "";
		if(enterIpTr)enterIpTr.style.display = "none";
		if(enterBtnIpTr)enterBtnIpTr.style.display = "none";
		if(null != ipManagementPanel){
			ipManagementPanel.header.innerHTML = "<s:text name="hiveAp.autoProvisioning.ip.management.popwin.title" />";
		}
		break;
	case 1: // enter panel
		if(selectIpTr)selectIpTr.style.display = "none";
		if(selectBtnIpTr)selectBtnIpTr.style.display = "none";
		if(enterIpTr)enterIpTr.style.display = "";
		if(enterBtnIpTr)enterBtnIpTr.style.display = "";
		if(null != ipManagementPanel){
			ipManagementPanel.header.innerHTML = "<s:text name="hiveAp.autoProvisioning.ip.management.popwin.enter.title" />";
		}
		break;
	}
}

function fillAllSerialNumber(o){
	eval("var data = " + o.responseText);
	results = data;
	if(typeof searchSn == 'function'){
		searchSn();
	}
}

function clearSerialNumbers(){
	var scanEl = document.getElementById("scanSerialNumbers");
	if(scanEl){scanEl.value = "";}
}

function clearIpSubNetworks() {
	var enterEl = document.getElementById("enteredIpSubnetworks");
	if(enterEl){enterEl.value = "";}
}

function doAjaxRequest(url, callback){
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success : callback }, null);
}

function doEditResponse(o){
	eval("var result = " + o.responseText);
	if(result.obj){// show sn panel
		var items = result.obj;
		var selectEl = document.getElementById("snItems");
		selectEl.length = 0;
		selectEl.length = items.length;
		for(var i=0; i<items.length; i++){
			selectEl.options[i].value = items[i].key;
			selectEl.options[i].text = items[i].value;
		}
	}
	if(result.info){
		showInfoDialog(result.info);
	}
}

function doIpManagementOutResponse(o){
	eval("var result = " + o.responseText);
	if(result.obj){// show ip management panel
		var items = result.obj;
		var selectEl = document.getElementById("ipItems");
		selectEl.length = 0;
		selectEl.length = items.length;
		for(var i=0; i<items.length; i++){
			selectEl.options[i].value = items[i].key;
			selectEl.options[i].text = items[i].value;
		}
	}
	if(result.info){
		showInfoDialog(result.info);
	}
}

function doIpManagementOutResponse1(o){
	doIpManagementOutResponse(o);
	showIpPanel(0);
	document.getElementById("enteredIpSubnetworks").value = '';
}
</script>

<s:if test="%{showLogoutMenu}">
<!--CSS file (default YUI Sam Skin) -->
<link type="text/css" rel="stylesheet"
	href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<!-- Dependencies -->
<script type="text/javascript"
	src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>
<!-- Source files -->
<script type="text/javascript"
	src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>

<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
	white-space: nowrap;
	text-align: left;
}

.yui-skin-sam .yui-dt-scrollable .yui-dt-hd,.yui-skin-sam .yui-dt-scrollable .yui-dt-bd{
	border: medium none;
}
</style>

<script type="text/javascript">
var results = [];
var basic;

var initDataTable = function(h, w){
    basic = function() {
        var myColumnDefs = [
            {key:"<s:text name='hiveAp.autoProvisioning.search.col.sn' />", sortable:true, resizeable:false, width: 150},
            {key:"<s:text name='hiveAp.autoProvisioning.search.col.vhm' />", sortable:true, resizeable:false, width: 180}
        ];

        var myDataSource = new YAHOO.util.DataSource(results);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["<s:text name='hiveAp.autoProvisioning.search.col.sn' />",
            		 "<s:text name='hiveAp.autoProvisioning.search.col.vhm' />"]
        };
        var myDataTable = new YAHOO.widget.DataTable("serialSummary",
                myColumnDefs, myDataSource, {scrollable: true, width: w+"px", height: h+"px"});
		myDataTable.subscribe("rowMouseoverEvent", myDataTable.onEventHighlightRow);
		myDataTable.subscribe("rowMouseoutEvent", myDataTable.onEventUnhighlightRow);
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
}
YAHOO.util.Event.onDOMReady(function () {
	initDataTable(150,395);
});

function searchSn(){
	var searchEl = document.getElementById("searchSerialNumber");
	var filterArray = [];
	var reg = new RegExp(searchEl.value);
	for(var i in results){
		if(reg.test(results[i]["<s:text name='hiveAp.autoProvisioning.search.col.sn' />"])){
			filterArray.push(results[i]);
		}
	}
	basic.oDT.getRecordSet().replaceRecords(filterArray);
	basic.oDT.render();
}

</script>
</s:if>

<div id="content"><s:form action="autoProvisioningConfig">
<s:hidden name="scanSerialNumbers"></s:hidden>
	<s:hidden name="ipSubNetworkString"></s:hidden>
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
					<td><input type="button" name="ignore" value="SN Management"
						class="button" style="width: 110px" onClick="showManagementPanel();"
						<s:property value="writeDisabled" />></td>
					<td style="display: <s:property value="%{wirelessRoutingStyle}"/>"><input type="button" name="ignore" value="<s:text name='hiveAp.autoProvisioning.button.text.ip.management' />"
						class="button" style="width: 110px" onClick="showIPManagementPanel();"
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
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="name"
								key="hiveAp.autoProvisioning.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="autoProvision"
								key="hiveAp.autoProvisioning.title.flag" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="modelType" key="hiveAp.model" /></th>
						</s:if>
						<s:if test="%{columnId == 11}">
							<th><ah:sort name="deviceType" key="hiveAp.device.type" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><ah:sort name="uploadImage"
								key="hiveAp.autoProvisioning.title.uploadImage" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
							<th><ah:sort name="imageVersion"
								key="hiveAp.autoProvisioning.imageVersion.label" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
							<th><ah:sort name="imageName"
								key="hiveAp.autoProvisioning.imageName.label" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">
							<th><ah:sort name="uploadConfig"
								key="hiveAp.autoProvisioning.title.uploadConfig" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">
							<th><ah:sort name="configTemplateId" key="hiveAp.template" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">
							<th><ah:sort name="wifi0ProfileId"
								key="hiveAp.autoProvisioning.title.defaultRadioProfile.0" /></th>
						</s:if>
						<s:if test="%{columnId == 10}">
							<th><ah:sort name="wifi1ProfileId"
								key="hiveAp.autoProvisioning.title.defaultRadioProfile.1" /></th>
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
							<td class="listCheck"><input type="checkbox"
								disabled="disabled" /></td>
						</s:if>
						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url action="autoProvisioningConfig"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/>
       							<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="name" /></a>&nbsp;</td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url action="autoProvisioningConfig"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="name" /></a>&nbsp;</td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list"><s:property value="autoProvisionString" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="modelTypeString" /></td>
							</s:if>
							<s:if test="%{columnId == 11}">
								<td class="list"><s:property value="deviceTypeString" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list"><s:property value="uploadImageString" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list"><s:property value="hiveOsVerString" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 6}">
								<td class="list"><s:property value="imageName" />&nbsp;</td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list"><s:property value="uploadConfigString" /></td>
							</s:if>
							<s:if test="%{columnId == 8}">
								<td class="list"><s:property
									value="configTemplate.configName" /></td>
							</s:if>
							<s:if test="%{columnId == 9}">
								<td class="list"><s:property value="radio0ProfileName" /></td>
							</s:if>
							<s:if test="%{columnId == 10}">
								<td class="list"><s:property value="radio1ProfileName" /></td>
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
<div id="managementPanel" style="display: none;">
<div class="hd"><s:text name="hiveAp.autoProvisioning.aclType.sn" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 8px 5px 8px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr id="selectTr">
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><select style="width: 150px;" id="snItems"
									multiple="multiple" size="12"></select></td>
								<td width="5px"></td>
								<td class="noteInfo" valign="top"><s:text
									name="hiveAp.autoProvisioning.sn.note" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="scanTr" style="display: none;">
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td></td>
								<td><s:textarea rows="9" cols="30" id="scanSerialNumbers" name="scanSerialNumbers"
								 	onkeypress="return hm.util.keyPressWithEnterPermit(event,'alphaNum-_');" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<s:if test="%{showLogoutMenu}">
					<tr id="summaryTr" style="display: none;">
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><div id="serialSummary"></div></td>
							</tr>
							<tr>
								<td style="padding-top: 5px;">
									<LABEL style="padding-right: 45px;"><s:text name="hiveAp.autoProvisioning.search.sn"></s:text></LABEL>
									<input id="searchSerialNumber" type="text" size="28" maxlength="20" >
								</td>
							</tr>
						</table>
						</td>
					</tr>
					</s:if>
				</table>

				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 7px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr id="selectBtnTr">
				<td><input type="button" name="ignore" value="Import SN"
					class="button" 
					onClick="submitAction('importSn2');"></td>
				<td><input type="button" name="ignore" value="Enter SN"
					class="button" 
					onClick="showPanel(1);"></td>
				<s:if test="%{showLogoutMenu}">
				<td><input type="button" name="ignore" value="Review"
					class="button" 
					onClick="showPanel(2);"></td>
				</s:if>
				<td><input type="button" name="ignore" value="Remove"
					class="button" 
					onClick="removeSerialNumbers();"></td>
				<td><input type="button" name="ignore" value="Close"
					class="button" 
					onClick="hideManagementPanel();"></td>
			</tr>
			<tr id="scanBtnTr" style="display: none;">
				<td><input type="button" name="ignore" value="Save"
					class="button" 
					onClick="submitAction('saveSn');"></td>
				<td><input type="button" name="ignore" value="Clear"
					class="button" 
					onClick="clearSerialNumbers();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="showPanel(0);"></td>
			</tr>
			<s:if test="%{showLogoutMenu}">
			<tr id="summaryBtnTr" style="display: none;">
				<td><input type="button" name="ignore" value="Search"
					class="button" 
					onClick="searchSn();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" 
					onClick="showPanel(0);"></td>
			</tr>
			</s:if>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="ipManagementPanel" style="display: none;">
<div class="hd"><s:text name="hiveAp.autoProvisioning.ip.management.popwin.title" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 8px 5px 8px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr id="selectIpTr">
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><select style="width: 150px;" id="ipItems"
									multiple="multiple" size="12"></select></td>
								<td width="5px"></td>
								<td class="noteInfo" valign="top"><s:text
									name="hiveAp.autoProvisioning.ip.management.popwin.note" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="enterIpTr" style="display: none;">
						<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td></td>
								<td><s:textarea rows="9" cols="30" id="enteredIpSubnetworks" name="enteredIpSubnetworks"
									onkeypress="return hm.util.keyPressWithEnterPermit(event,'ipMask');"/></td>
							</tr>
						</table>
						</td>
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
			<tr id="selectBtnIpTr">
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.import" />"
					class="button" 
					onClick="submitAction('importIpSubNetwork2');"></td>
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.enter" />"
					class="button" 
					onClick="showIpPanel(1);"></td>
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.remove" />"
					class="button" 
					onClick="removeIpSubNetworks();"></td>
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.cancel" />"
					class="button" 
					onClick="hideIpManagementPanel();"></td>
			</tr>
			<tr id="enterBtnIpTr" style="display: none;">
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.save" />"
					class="button" 
					onClick="submitAction('saveIpSubNetwork');"></td>
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.clear" />"
					class="button" 
					onClick="clearIpSubNetworks();"></td>
				<td><input type="button" name="ignore" value="<s:text name="hiveAp.autoProvisioning.ip.management.popwin.button.cancel" />"
					class="button" 
					onClick="showIpPanel(0);"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>