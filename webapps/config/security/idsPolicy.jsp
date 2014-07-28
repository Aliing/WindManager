<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0;
}
-->
</style>
<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>

var formName = 'idsPolicy';
function onLoadPage() {
	var expanding = <s:property value="%{expanding}"/>;
	var operation = "<s:property value="%{operation}"/>";
	if(operation == 'new' || expanding){
		showCreateSection();
	}
	var el = document.getElementById(formName + "_dataSource_policyName");
	if(!el.disabled){el.focus();}
	<s:if test="%{jsonMode}">
		top.changeIFrameDialog(860, 600);
	</s:if>
}

function getSelectedCount(objSelect)
{
   var count=0;
  for(var i=0;i<objSelect.options.length;i++)
  {
       var el=objSelect.options[i];
       if(el.selected)count++;
  }
return count;
}

function submitAction(operation) {
	if (validate(operation)) {
		//bug 14595
		if(operation == 'create' || operation == "update"){
			var checkedSsid = document.getElementsByName("ssidIndices");
			if (document.getElementsByName("dataSource.ssidEnable")[0].checked && (checkedSsid == null || checkedSsid == undefined || checkedSsid.length <= 0)){
				warnDialog.cfg.setProperty('text', "Because SSID detection is enabled, you must select at least one SSID.");
				warnDialog.show();
				return false;
			}
		}

		if (operation != 'addSsidProfiles' &&
		    operation != 'removeSsidProfiles') {
			showProcessing();
		}
		//close create section after apply ssid profiles
		if(operation == 'addSsidProfiles'){
			document.getElementById("expanding").value="false";

		}

		hm.options.selectAllOptions('macOrOuis');
		hm.options.selectAllOptions('vlans');
		if(operation=="newVlan" || operation=="editVlan" || operation=="newMacOrOui" || operation=="editMacOrOui") {
			<s:if test="%{jsonMode}">
				document.forms[formName].parentIframeOpenFlg.value = true;
			</s:if>
		}



		//save style values
		Get(formName + "_dataSource_optionDisplayStyle").value = Get("options").style.display;
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function showOptionContent(){
	showHideContent("options","");
}

function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_IDS_POLICY%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == "newMacOrOui" ||
		operation == "newSsid" ||
		operation == "newVlan" )  {
		if (operation == "<%=Navigation.L2_FEATURE_IDS_POLICY%>" ||
			operation == 'cancel' + '<s:property value="lstForward"/>') {
			document.getElementById(formName + "_dataSource_mitigatePeriod").value=1;
			document.getElementById(formName + "_dataSource_mitigateDuration").value=1440;
			document.getElementById(formName + "_dataSource_mitigateQuiet").value=3600;
			// document.getElementById(formName + "_dataSource_staReportPeriod").value = 1;
			// document.getElementById(formName + "_dataSource_staReportDuration").value = 300;
			// document.getElementById(formName + "_dataSource_staReportInterval").value = 3600;
			// document.getElementById(formName + "_dataSource_staReportAgeout").value = 10;
			document.getElementById(formName + "_dataSource_detectorAps").value=1;
			document.getElementById(formName + "_dataSource_deAuthTime").value=60;
			document.getElementById(formName + "_dataSource_staReportAgeTime").value=3600;
		} else {
			if(!validateIdsMitigation("update")){
				return false;
			}
		}
		return true;
	}

	if (!validateIdsName(operation)) {
		return false;
	}

	if(!validateIdsMitigation(operation)) {
		return false;
	}

	if (!validateSsidProfile(operation)){
		return false;
	}

	if (!validateIdsSsidProfile(operation)){
		return false;
	}
	if(operation == "editMacOrOui"){
		var value = hm.util.validateOptionTransferSelection("macOrOuis");
		if(value < 0){
			return false
		}else{
			document.forms[formName].macOrOui.value = value;
		}
	}

	if(operation == "editVlan"){
		var value = hm.util.validateOptionTransferSelection("vlans");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlan.value = value;
		}
	}

	if(operation == "editSsid"){
		var value = hm.util.validateListSelection(formName + "_ssidIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ssid.value = value;
		}
	}

	return true;
}

function validateIdsSsidProfile(operation){
	if (operation == 'removeSsidProfiles'){
		var cbs = document.getElementsByName('ssidIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAll");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ids.ssidList" /></s:param></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAll");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ids.ssidList" /></s:param></s:text>');
			return false;
		}
	}
	return true;
}

function validateSsidProfile(operation){
	if (operation == 'addSsidProfiles') {
		inputElement = document.getElementById(formName+"_ssidIds");
		displayElement = document.getElementById("checkAll");
		if (!hasSelectedOptions(inputElement.options)) {
            hm.util.reportFieldError(displayElement, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.ids.ssidList" /></s:param></s:text>');
            inputElement.focus();
			return false;
		}
	}
	return true;
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}


function validateIdsName(operation) {
	if (operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>') {
	    var inputElement = document.getElementById(formName + "_dataSource_policyName");
	    var message = hm.util.validateName(inputElement.value, '<s:text name="config.ids.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(inputElement, message);
        	inputElement.focus();
        	return false;
    	}
	}
    return true;
}

function validateIdsMitigation(operation) {
	if (operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>'
		|| operation == 'update'+'<s:property value="lstForward"/>'
		|| operation == 'update') {
	    var period = document.getElementById(formName + "_dataSource_mitigatePeriod");
	    if (!checkInputRange(period, '<s:text name="config.ids.tab.general.mitigation.period" />',
    									<s:property value="%{mitigatePeriodRange.min()}" />,
    									<s:property value="%{mitigatePeriodRange.max()}" />)) {
	    	showOptionContent();
            return false;
   		}
   		var authTime = document.getElementById(formName + "_dataSource_deAuthTime");
	    if (!checkInputRange(authTime, '<s:text name="config.ids.tab.general.mitigation.deauth.time" />', 0,2592000)) {
	    	showOptionContent();
            return false;
   		}
   		var duration = document.getElementById(formName + "_dataSource_mitigateDuration");
	    if (duration.value != 0 && !checkInputRange(duration, '<s:text name="config.ids.tab.general.mitigation.duration" />',
    									60,2592000)) {
	    	showOptionContent();
            return false;
   		}
   		var quiet = document.getElementById(formName + "_dataSource_mitigateQuiet");
	    if (quiet.value != 0 && !checkInputRange(quiet, '<s:text name="config.ids.tab.general.mitigation.quiet" />',
    									60,2592000)) {
	    	showOptionContent();
            return false;
   		}
   		if(duration.value != 0 && parseInt(duration.value) <parseInt(quiet.value)){
   			hm.util.reportFieldError(quiet, '<s:text name="error.notLargerThan"><s:param><s:text name="config.ids.tab.general.mitigation.quiet" /></s:param><s:param><s:text name="config.ids.tab.general.mitigation.duration" /></s:param></s:text>');
   			quiet.focus();
   			showOptionContent();
   			return false;
   		}
   		var deteAp = document.getElementById(formName + "_dataSource_detectorAps");
	    if (!checkInputRange(deteAp, '<s:text name="config.ids.tab.general.mitigation.detector.aps" />', 0,1024)) {
	    	showOptionContent();
            return false;
   		}
   		var staReportEnabled = document.getElementById(formName + "_dataSource_staReportEnabled");
   		var agetime = document.getElementById(formName + "_dataSource_staReportAgeTime");
	    if(staReportEnabled.checked){
	    	if (!checkInputRange(agetime, '<s:text name="glasgow_25.config.ids.station.report.agetime" />',
    									60,86400)) {
	    	showOptionContent();
            return false;
   			}
	    }else{
	    	document.getElementById(formName + "_dataSource_staReportAgeTime").value=3600;
	    }
	    
	}
    return true;
}

function checkInputRange(inputElement, title, min, max) {
	if (inputElement.value.length == 0) {
		showOptionContent();
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(inputElement.value, title, min, max);
    if (message != null) {
    	showOptionContent();
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function displayOptions(eid,isChecked){
 var inputElement = document.getElementById(eid);
	if(isChecked){
		inputElement.style.display="";
	}else{
		inputElement.style.display="none";
	}
}

function setEncryptionType(isChecked){
var inputElement = document.getElementById("encryptionTypeId");
	if(isChecked){
		inputElement.disabled = false;
	}else{
		inputElement.disabled = true;
	}
}

function setEncryptionsType(isChecked){
	var allEncryptionChecks = document.getElementsByName('encryptionIndices');
	var allEncryptionTypes = document.getElementsByName('encryptionTypes');
	for(var i=0; i<allEncryptionChecks.length; i++){
		allEncryptionTypes[i].disabled = !allEncryptionChecks[i].checked;
	}
}

function toggleCheckAllSsidProfiles(cb){
	var cbs = document.getElementsByName('ssidIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function synchPeriodValue(period){
	var _dataSource_staReportPeriod = document.getElementById(formName + "_dataSource_staReportPeriod");
	var _dataSource_mitigatePeriod = document.getElementById(formName + "_dataSource_mitigatePeriod");
	if(period ==_dataSource_staReportPeriod){
		_dataSource_mitigatePeriod.value = _dataSource_staReportPeriod.value;
	}else{
		_dataSource_staReportPeriod.value = _dataSource_mitigatePeriod.value;
	}
}

function showCreateSection() {
   hm.util.hide('newButton');
   hm.util.show('createButton');
   hm.util.show('createSection');
   // to fix column overlap issue on certain browsers
   var trh = document.getElementById('headerSection');
   var trc = document.getElementById('createSection');
   var table = trh.parentNode;
   table.removeChild(trh);
   table.insertBefore(trh, trc);
   document.getElementById("expanding").value="true";
}

function hideCreateSection() {
   hm.util.hide('createButton');
   hm.util.show('newButton');
   hm.util.hide('createSection');
   document.getElementById("expanding").value="false";
}

function showOrHideMitigation(checked) {
	if (checked) {
		hm.util.show('mitigationAutoCheckbox');
	} else {
		document.getElementById(formName + "_dataSource_inSameNetwork").checked=true;
		hm.util.hide('mitigationAutoCheckbox');
	}
}

function enableMitigationAutoCheckbox(checked) {
	if (!checked) {
		showWarnDialog('<s:text name="config.ids.tab.general.mitigation.mode.auto.checkbox.warning" />');
	}
}
function save(operation) {//add nxma
	if (validate(operation)) {
		url = "<s:url action='idsPolicy' includeParams='none' />" + "?jsonMode=true"
				+ "&ignore=" + new Date().getTime();
		if (operation == 'create') {
			//
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}

		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('macOrOuis');
		hm.options.selectAllOptions('vlans');

		YAHOO.util.Connect.setForm(document.forms["idsPolicy"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}
var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
}
<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="idsPolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedIdsName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedIdsName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</s:if>
</script>

<div id="content"><s:form action="idsPolicy">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg" />
	<s:hidden name="contentShowType" />
	<div id="vlanTitleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="80%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-WIPS.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.wips.dialog.new.title"/></td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.wips.dialog.edit.title"/></td>
							</s:else>
							<td style="padding-left:10px;">
							<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
								<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
									alt="" class="dblk"/>
							</a>
						</td>
						</tr>
					</table>
					</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="idsSaveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="idsSaveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>
	<s:hidden name="selectDosType" />
	<s:hidden name="expanding" id="expanding" value="%{expanding}"/>
	<s:hidden name="macOrOui" />
	<s:hidden name="vlan" />
	<s:hidden name="ssid" />
	<s:hidden name="dataSource.optionDisplayStyle"></s:hidden>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:if test="%{jsonMode == false}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_IDS_POLICY%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table style="padding: 5px;" cellspacing="0" cellpadding="0" border="0" width="750px">
			</s:if>
			<s:else>
				<table class="editBox" style="padding: 5px;" cellspacing="0" cellpadding="0" border="0" width="750px">
			</s:else>
				<tr>
					<td><!-- definition -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr><td height="4px"></td></tr>
							<tr>
								<td class="labelT1" width="100px"><label><s:text
									name="config.ids.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.policyName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="%{policyNameLength}" disabled="%{disabledName}" /> <s:text
									name="config.ids.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.ids.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" /> <s:text
									name="config.ids.description.range" /></td>
							</tr>
							<tr><td height="4px"></td></tr>
						</table>
					</td>
				</tr>
				<tr>
					<td><!-- ap intrusion -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr><td height="4px"></td></tr>
							<tr>
								<td style="padding:0 2px 0 6px"><s:checkbox
									name="dataSource.rogueDetectionEnable"
									value="%{dataSource.rogueDetectionEnable}"/></td>
								<td><s:text name="config.ids.rogueDetection" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="4px"></td></tr>
				<tr>
					<td style="padding: 0 5px;"><!-- ap policy -->
						<fieldset><legend><s:text name="config.ids.tab.apPolicy" /></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelH1"><s:text name="config.ids.apPolicyLabel"/></td>
							</tr>
							<tr>
								<td>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td style="padding:0 2px 0 0px" width="25px"><s:checkbox
											name="dataSource.shortPreambleEnable"
											value="%{dataSource.shortPreambleEnable}"/></td>
										<td><s:text name="config.ids.shortPreamble" />
											<s:text name="config.ids.shortPreamble.note" /></td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.shortBeanchIntervalEnable"
											value="%{dataSource.shortBeanchIntervalEnable}"/></td>
										<td><s:text name="config.ids.shortBeaconInterval" /></td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.wmmEnable"
											value="%{dataSource.wmmEnable}"/></td>
										<td><s:text name="config.ids.wmm" /></td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td class="sepLine" colspan="3"><img
											src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.ouiEnable"
											value="%{dataSource.ouiEnable}" onclick="displayOptions('macOrOuiOptionsRow',this.checked)"/></td>
										<td><s:text name="config.ids.oui" /></td>
									</tr>
									<tr id="macOrOuiOptionsRow" style="display:<s:property value="macOrOuiOptionsRowStatus"/>">
										<td></td>
										<td style="padding: 4px 0 0 0;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr><td style="padding-bottom: 3px;"><s:text name="config.ids.oui.checked" /></td></tr>
											<tr>
												<td style="padding-right:15px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:push value="%{macOrOuiOptions}">
															<td><tiles:insertDefinition
																name="optionsTransfer" /></td>
														</s:push>
													</tr>
													<%--<tr>
														<td height="5"></td>
													</tr>
													<tr>
														<td style="padding:0 5px 0 25px;">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newMacOrOui')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editMacOrOui')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>--%>
												</table>
												</td>
											</tr>
											<tr>
												<td height="15"/>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.inNetworkEnable"
											value="%{dataSource.inNetworkEnable}"/></td>
										<td><s:text name="config.ids.inNetwork" /></td>
									</tr>
									<tr id="vlanOptionsRow" style="display:none">
										<td></td>
										<td style="padding: 4px 0 0 0;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding-right:15px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:push value="%{vlanOptions}">
															<td><tiles:insertDefinition
																name="optionsTransfer" /></td>
														</s:push>
													</tr>
													<%--<tr>
														<td style="padding:0 5px 0 25px;">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newVlan')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editVlan')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>--%>
												</table>
												</td>
											</tr>
											<tr>
												<td height="15"/>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.ssidEnable"
											value="%{dataSource.ssidEnable}" onclick="displayOptions('ssidOptionsRow',this.checked)"/></td>
										<td><s:text name="config.ids.ssid" /></td>
									</tr>
									<tr id="ssidOptionsRow" style="display:<s:property value="ssidOptionsRowStatus"/>">
										<td></td>
										<td style="padding: 4px 0 0 0;">
										<table cellspacing="0" cellpadding="0" border="0">
											<tr><td colspan="5">
														<s:text name="config.ids.ssid.checked"></s:text>
													</td> </tr>
											<tr id="newButton">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">

													<tr>
														<td><input type="button" name="ignore" value="New"
															class="button" onClick="showCreateSection();" <s:property value="writeDisabled" />></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button"
															onClick="submitAction('removeSsidProfiles');" <s:property value="writeDisabled" />></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display:none;" id="createButton">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="Apply"
															class="button" onClick="submitAction('addSsidProfiles');"></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button" onClick="submitAction('removeSsidProfiles');"></td>
														<td><input type="button" name="ignore" value="Cancel"
															class="button" onClick="hideCreateSection();"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="headerSection">
												<th align="left" style="padding-left: 0;"><input
													type="checkbox" id="checkAll"
													onClick="toggleCheckAllSsidProfiles(this);"></th>
												<th align="left" width="200px"><s:text name="config.ids.ssidList" /></th>
												<th align="center" width="150px"><s:text name="config.ids.encryptionEnable" /></th>
												<th align="left" width="200px"><s:text name="config.ids.encryptionType" /></th>
											</tr>
											<tr style="display:none;" id="createSection">
												<td class="listHead"></td>
												<td class="listHead" valign="top">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><s:select multiple="true" size="6" name="ssidIds" onclick="hm.util.showtitle(this);"
															list="%{availableSsidProfiles}" listKey="id" listValue="value"
															value="%{ssid}" cssStyle="width: 150px;" /></td>
													</tr>
												</table>
												</td>
												<td class="listHead" valign="top" align="center">
													<s:checkbox name="encryptionCheck" onclick="setEncryptionType(this.checked)"/></td>
												<td class="listHead" valign="top">
													<s:select name="encryptionType" id="encryptionTypeId" list="%{enumEncryptionType}" listKey="key" listValue="value" disabled="true" cssStyle="width: 130px;"/></td>
											</tr>
											<s:iterator value="%{dataSource.idsSsids}" status="status">
												<tr>
													<td class="listCheck"><s:checkbox name="ssidIndices"
														fieldValue="%{#status.index}" /></td>
													<td class="list"><s:property
														value="ssidProfile.ssidName" /></td>
													<td class="list" align="center"><s:checkbox
														name="encryptionIndices" fieldValue="%{#status.index}"
														value="%{encryptionEnable}" onclick="setEncryptionsType(this.checked)"/></td>
													<td class="list"><s:select name="encryptionTypes"
														value="%{encryptionType}" list="%{enumEncryptionType}" listKey="key"
														listValue="value" disabled="%{!encryptionEnable}"/></td>
													<td><s:hidden name="rowIndices" value="%{#status.index}"/></td>
												</tr>
											</s:iterator>
											<s:if test="%{gridCount > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount}">
													<s:iterator>
														<tr>
															<td class="list" colspan="6">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>
				<tr><td height="8px"></td></tr>
				<tr>
					<td style="padding: 0 5px;"><!-- client policy -->
						<fieldset><legend><s:text name="config.ids.tab.clientPolicy" /></legend>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.networkDetectionEnable"
											value="%{dataSource.networkDetectionEnable}"/></td>
										<td><s:text name="config.ids.networkDetection" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<!--
							<tr>
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 0px"><s:checkbox
											name="dataSource.staReportEnabled" onclick="setStaReportEnabled(this);"
											value="%{dataSource.staReportEnabled}"/></td>
										<td><s:text name="config.ids.station.report" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td id="staReportConfigTd" style="padding-left: 12px; display: <s:property value="dataSource.staReportEnabled?'':'none'"/>">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="labelT1" width="365px"><label><s:text
											name="config.ids.station.report.duration" /></label></td>
										<td><s:textfield size="6" name="dataSource.staReportDuration"
											maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="config.ids.station.report.duration.note" /></td>
									</tr>
									<tr>
										<td class="labelT1"><label><s:text
											name="config.ids.station.report.interval" /></label></td>
										<td><s:textfield size="6" name="dataSource.staReportInterval"
											maxlength="5" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="config.ids.station.report.interval.note" /></td>
									</tr>
									<tr>
										<td class="labelT1"><label><s:text
											name="config.ids.station.report.period" /></label></td>
										<td><s:textfield size="6" name="dataSource.staReportPeriod" onblur="synchPeriodValue(this);"
											maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="config.ids.station.report.period.note" /></td>
									</tr>
									<tr>
										<td class="labelT1"><label><s:text
											name="config.ids.station.report.ageout" /></label></td>
										<td><s:textfield size="6" name="dataSource.staReportAgeout"
											maxlength="2" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="config.ids.station.report.ageout.note" /></td>
									</tr>
								</table>
								</td>
							</tr>
							-->
						</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td><!-- option -->
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.ids.optional.settings" />','options');</script></td>
							</tr>
							<tr>
								<td style="padding-left: 10px;">
									<div id="options" style="display: <s:property value="%{dataSource.optionDisplayStyle}"/>">
									<fieldset><legend><s:text
										name="config.ids.tab.general.mitigation.mode" /></legend>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr><td height="5px" /></tr>
										<tr>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding-left:5px" width="120"><s:radio
															label="Gender" name="radioMitigationMode"
															list="#{'manual':'Manual'}"
															onclick="showOrHideMitigation(false);"
															value="%{radioMitigationMode}" /></td>
														<td style="padding-left:5px" width="150"><s:radio label="Gender"
															name="radioMitigationMode" list="#{'semiAuto':'Semi-automatic'}"
															onclick="showOrHideMitigation(false);" value="%{radioMitigationMode}" /></td>
														<td style="padding-left:5px"><s:radio label="Gender"
															name="radioMitigationMode" list="#{'auto':'Automatic'}"
															onclick="showOrHideMitigation(true);" value="%{radioMitigationMode}" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr id="mitigationAutoCheckbox" style="display:<s:property value="%{'auto'==radioMitigationMode?'':'none'}"/>">
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td height="5px"></td>
													</tr>
													<tr>
														<td style="padding:0 2px 0 10px"><s:checkbox name="dataSource.inSameNetwork"
														 onclick="enableMitigationAutoCheckbox(this.checked);" /></td>
														<td><s:text name="config.ids.tab.general.mitigation.mode.auto.checkbox" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</fieldset>
									<table><tr><td></td></tr></table>
									<fieldset><legend><s:text
										name="config.ids.tab.general.mitigation" /></legend>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr><td height="5px" /></tr>
										<tr>
											<td class="labelT1" width="300px"><label><s:text
												name="config.ids.tab.general.mitigation.period" /></label></td>
											<td><s:textfield size="6" name="dataSource.mitigatePeriod" onblur="synchPeriodValue(this);"
												maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.ids.tab.general.mitigation.period.range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label><s:text
												name="config.ids.tab.general.mitigation.deauth.time" /></label></td>
											<td><s:textfield size="6" name="dataSource.deAuthTime"
												maxlength="7" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.ids.tab.general.mitigation.deauth.time.range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label><s:text
												name="config.ids.tab.general.mitigation.duration" /></label></td>
											<td><s:textfield size="6" name="dataSource.mitigateDuration"
												maxlength="7" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.ids.tab.general.mitigation.duration.range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label><s:text
												name="config.ids.tab.general.mitigation.quiet" /></label></td>
											<td><s:textfield size="6" name="dataSource.mitigateQuiet"
												maxlength="7" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.ids.tab.general.mitigation.quiet.range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label><s:text
												name="config.ids.tab.general.mitigation.detector.aps" /></label></td>
											<td><s:textfield size="6" name="dataSource.detectorAps"
												maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.ids.tab.general.mitigation.detector.range" /></td>
										</tr>
									</table>
									</fieldset>
									<table>
										<tr>
											<td width="10px">
												<s:checkbox name="dataSource.staReportEnabled" value="%{dataSource.staReportEnabled}" onclick="displayOptions('ageTimeRow',this.checked)"/>
											</td>
											<td style="padding-top:4px">
												<s:text name="config.ids.enable.rogue.client.reporting" />
											</td>
										</tr>
										<tr id="ageTimeRow" style="display:<s:property value="ageTimeRowStatus"/>">
											<td style="padding-left:12px;" colspan="2">
												<table>
													<tr><td class="labelT1"><s:text name="glasgow_25.config.ids.station.report.agetime" /></td>
													<td style="padding-left:38px;"><s:textfield size="6" name="dataSource.staReportAgeTime"
														maxlength="5" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
														<s:text name="glasgow_25.config.ids.station.report.agetime.range" /></td>
											</td>
										</tr>
									</table>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>