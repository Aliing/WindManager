<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
	<s:if test="%{jsonMode == true}">
	body {
		background-color: transparent;
	}
	</s:if>
</style>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script>

var formName = 'hiveProfilesForm';
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_hiveName").disabled == false) {
		document.getElementById(formName + "_dataSource_hiveName").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(810, 450);
		}
	</s:if>
}
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		if (operation == 'newHiveDos') {
			document.forms[formName].selectDosType.value = 'mac';
		}
		if (operation == 'newStationDos') {
			document.forms[formName].selectDosType.value = 'station';
		}
		hm.options.selectAllOptions('macFilters');
		Get(formName + "_dataSource_advancePanelStyle").value = Get("advancePanel").style.display;
	    document.forms[formName].submit();
	}
}

function validateHiveForJson(operation) {
	if (validate(operation)) {
		hm.options.selectAllOptions('macFilters');
		return true;
	}
	return false;
}

function validate(operation) {
	if(operation == "editHiveDos"){
		var value = hm.util.validateListSelection(formName + "_hiveDos");
		if(value < 0){
			return false
		}else{
			document.forms[formName].hiveDos.value = value;
		}
	}
	if(operation == "editStationDos"){
		var value = hm.util.validateListSelection(formName + "_stationDos");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].stationDos.value = value;
		}
	}
	if(operation == "editMacFilter"){
		var value = hm.util.validateOptionTransferSelection("macFilters");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].macFilter.value = value;
		}
	}
	if (operation == "newHiveDos" ||
		operation == "newStationDos" ||
//		operation == "newRoaming" ||
		operation == "newMacFilter" ||
		operation == "editHiveDos" ||
		operation == "editStationDos" ||
		operation == "editMacFilter" ) {
		if (!validateL3TrafficPort()) {
			return false;
		}
		if (!validateRtsThreshold()) {
			return false;
		}
		if (!validateFragThreshold()) {
			return false;
		}
		if (!validatePollingInterval()) {
			return false;
		}
		if (document.getElementById(formName + "_dataSource_enabledL3Setting").checked) {
			if (!validateAliveInterval()) {
				return false;
			}
			if (!validateAliveAgeout()) {
				return false;
			}
			if (!validateUpdateInterval()) {
				return false;
			}
			if (!validateUpdateAgeout()) {
				return false;
			}
		}
		return true;
	}
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_l3TrafficPort").value=0;
		document.getElementById(formName + "_dataSource_rtsThreshold").value=0;
		document.getElementById(formName + "_dataSource_fragThreshold").value=0;
		document.getElementById(formName + "_dataSource_pollingInterval").value=0;
		show_hideL3Setting(false);
		return true;
	}

	if (!validateHiveName()) {
		return false;
	}

	if (document.getElementById(formName + "_dataSource_enabledPassword").checked == true) {
		if (Get("manRadioPass2").checked==true) {
			if(!validateKeyConfirmValue("hivePassword","confirmPassword")) {
				return false;
			}
		}
	}

	if (!validateRtsThreshold()) {
		return false;
	}

	if (!validateFragThreshold()) {
		return false;
	}

	if (!validateL3TrafficPort()) {
		return false;
	}

	if (!validatePollingInterval()) {
			return false;
	}

	if (document.getElementById(formName + "_dataSource_enabledL3Setting").checked) {
		if (!validateAliveInterval()) {
			return false;
		}
		if (!validateAliveAgeout()) {
			return false;
		}
		if (!validateUpdateInterval()) {
			return false;
		}
		if (!validateUpdateAgeout()) {
			return false;
		}
	}

	return true;
}

function validateHiveName() {
    var inputElement = document.getElementById(formName + "_dataSource_hiveName");
	var message = hm.util.validateSsid(inputElement.value, '<s:text name="config.hp.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}

//     if (inputElement.value.indexOf('/') > -1 || inputElement.value.indexOf('\'') > -1) {
//       hiveProfileTabs.set('activeIndex', 0);
//       hm.util.reportFieldError(inputElement, '<s:text name="error.name.containsInvalidChar"><s:param><s:text name="config.hp.name" /></s:param></s:text>');
//       inputElement.focus();
//       return false;
//  	}
    return true;
}

function validateRtsThreshold() {
      var inputElement = document.getElementById(formName + "_dataSource_rtsThreshold");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.rtsThreshold" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.hp.rtsThreshold" />',
                                                       <s:property value="%{rtsThresholdRange.min()}" />,
                                                       <s:property value="%{rtsThresholdRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
      }
      return true;
}

function validateFragThreshold() {
      var inputElement = document.getElementById(formName + "_dataSource_fragThreshold");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.fragThreshold" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.hp.fragThreshold" />',
                                                       <s:property value="%{fragThresholdRange.min()}" />,
                                                       <s:property value="%{fragThresholdRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
      }
      return true;
}

function validatePollingInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_pollingInterval");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.pollingInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.hp.pollingInterval" />',
                                                       <s:property value="%{pollingIntervalRange.min()}" />,
                                                       <s:property value="%{pollingIntervalRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
      }
      return true;
}

function validateL3TrafficPort() {
      var inputElement = document.getElementById(formName + "_dataSource_l3TrafficPort");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.l3TrafficPort" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.hp.l3TrafficPort" />',
                                                       <s:property value="%{l3TrafficPortRange.min()}" />,
                                                       <s:property value="%{l3TrafficPortRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
      }
// 	  if ((parseInt(inputElement.value) - 3000)%4 !=0) {
// 	        hm.util.reportFieldError(inputElement, '<s:text name="error.valueInvalid"><s:param><s:text name="config.hp.l3TrafficPort" /></s:param></s:text>');
//             inputElement.focus();
//             return false;
// 	  }

      return true;
}

function validateKeyConfirmValue(elementKey,elementConfirm) {
	  var keyElement;
	  var confirmElement;
	  if (document.getElementById("chkToggleDisplay").checked) {
	  	keyElement = document.getElementById(elementKey);
	  	confirmElement = document.getElementById(elementConfirm);
	  } else {
	  	keyElement = document.getElementById(elementKey+ "_text");
	  	confirmElement = document.getElementById(elementConfirm+ "_text");
	  }

	  if (keyElement.value.length ==0) {
	         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.password" /></s:param></s:text>');
	         keyElement.focus();
	         return false;
      }

      if (confirmElement.value.length == 0) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.hp.confirmPassword" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }

      var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.hp.password" />');
	  if (message != null) {
	      hm.util.reportFieldError(keyElement, message);
	      keyElement.focus();
	      return false;
	  }

      if (keyElement.value.length < 8) {
            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.hp.password" /></s:param><s:param><s:text name="config.hp.password.range" /></s:param></s:text>');
            keyElement.focus();
            return false;
      }

      if (confirmElement.value.length < 8) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.hp.confirmPassword" /></s:param><s:param><s:text name="config.hp.password.range" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }

      if (keyElement.value != confirmElement.value) {
	      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.hp.confirmPassword" /></s:param><s:param><s:text name="config.hp.password" /></s:param></s:text>');
	    	keyElement.focus();
	    	return false;
      }

      return true;
}

function setPasswordEdit(checked) {
    var hidePassword = document.getElementById("hidePassword");
	if (!checked){
		hidePassword.style.display="none";
		Get("hidePasswordTextBox").style.display ="none";
		Get("manRadioPass2").checked=true;
		document.getElementById("hivePassword").value ="";
		document.getElementById("confirmPassword").value ="";
		document.getElementById("hivePassword_text").value ="";
		document.getElementById("confirmPassword_text").value ="";

	}
	if (checked){
		hidePassword.style.display="block";
		Get("hidePasswordTextBox").style.display ="block";
		Get("manRadioPass2").checked=true;
		document.getElementById("hivePassword").value ="";
		document.getElementById("confirmPassword").value ="";
		document.getElementById("hivePassword_text").value ="";
		document.getElementById("confirmPassword_text").value ="";
	}
}

function showBlock(type){
	if (type==1) {
		Get("hidePasswordTextBox").style.display ="none"
		generalPassword();
	} else {
		document.getElementById("hivePassword").value ="";
		document.getElementById("confirmPassword").value ="";
		document.getElementById("hivePassword_text").value ="";
		document.getElementById("confirmPassword_text").value ="";
		Get("hidePasswordTextBox").style.display ="block"
	}
}

function showAdvancePanel(){
    showHideContent("advancePanel","");
}

function setThresholdEdit(checked) {
    var hideThreshold = document.getElementById("hideThreshold");
	if (!checked){
		hideThreshold.style.display="none";
		document.getElementById(formName + "_dataSource_connectionThreshold").value ="-80";
		document.getElementById(formName + "_dataSource_pollingInterval").value ="1";
	}
	if (checked){
		hideThreshold.style.display="block";
	}
}

function validateAliveInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_keepAliveInterval");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.aliveInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.aliveInterval" />',
                                                       <s:property value="%{keepAliveIntervalRange.min()}" />,
                                                       <s:property value="%{keepAliveIntervalRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateAliveAgeout() {
      var inputElement = document.getElementById(formName + "_dataSource_keepAliveAgeout");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.aliveAgeout" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.aliveAgeout" />',
                                                       <s:property value="%{keepAliveAgeoutRange.min()}" />,
                                                       <s:property value="%{keepAliveAgeoutRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateUpdateInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_updateInterval");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.updateInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.updateInterval" />',
                                                       <s:property value="%{updateIntervalRange.min()}" />,
                                                       <s:property value="%{updateIntervalRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateUpdateAgeout() {
      var inputElement = document.getElementById(formName + "_dataSource_updateAgeout");
      if (inputElement.value.length == 0) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.updateAgeout" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.updateAgeout" />',
                                                       <s:property value="%{updateAgeoutRange.min()}" />,
                                                       <s:property value="%{updateAgeoutRange.max()}" />);
      if (message != null) {
      		showAdvancePanel();
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function show_hideL3Setting(checked) {
    var hideL3Setting = document.getElementById("hideL3Setting");
	if (checked){
		hideL3Setting.style.display="block";
		hm.util.hideFieldError();
	}
	if (!checked){
		hideL3Setting.style.display="none";
		document.getElementById(formName + "_dataSource_keepAliveInterval").value="10";
		document.getElementById(formName + "_dataSource_keepAliveAgeout").value="5";
		document.getElementById(formName + "_dataSource_updateInterval").value="60";
		document.getElementById(formName + "_dataSource_updateAgeout").value="60";
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;

	if (document.getElementById("chkToggleDisplay").checked) {
		document.getElementById("hivePassword").value =value;
		document.getElementById("confirmPassword").value =value;
	} else {
		document.getElementById("hivePassword_text").value =value;
		document.getElementById("confirmPassword_text").value =value;
	}

};

var detailsFailed = function(o) {
//	alert("failed.");
};

function saveHiveProfileJsonDlg(operation) {
	if (validate(operation)) {
		var url = "";
		if (operation == 'create') {
			url = "<s:url action='hiveProfiles' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		} else if (operation == 'update') {
			url = "<s:url action='hiveProfiles' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('macFilters');
		YAHOO.util.Connect.setForm(document.forms["hiveProfilesForm"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveHiveProfileJsonDlg, failure : failSaveHiveProfileJsonDlg, timeout: 60000}, null);
	}
}

var succSaveHiveProfileJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.reportFieldError(Get("errMsgJson4hiveProfiles"), details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
				parentSelectDom.focus();
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
	}
};

var failSaveHiveProfileJsonDlg = function(o) {
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function generalPassword(){
	var url = '<s:url action="hiveProfiles"><s:param name="operation" value="genenatePassword"/></s:url>';
	<s:if test="%{jsonMode}">
		url = '<s:url action="hiveProfiles" includeParams="none" />?operation=genenatePassword&jsonMode=true&ignore='+new Date().getTime();
	</s:if>
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveProfiles" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedHiveName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedHiveName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="hiveProfiles" id="hiveProfilesForm" name="hiveProfilesForm">
	<s:hidden name="selectDosType" />
	<s:hidden name="macFilter" />
	<s:hidden name="dataSource.advancePanelStyle"/>
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="jsonMode" />
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
	<div id="hiveProfileDlgTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="80%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-users-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.hive"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.hive.edit"/></td>
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
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="hiveProfileDlgSaveBtnId" style="float: right;" onclick="saveHiveProfileJsonDlg('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="hiveProfileDlgSaveBtnId" style="float: right;" onclick="saveHiveProfileJsonDlg('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:else>
					</tr>
				</table>
				</s:if>
				<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="hiveProfileDlgSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="hiveProfileDlgSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	<table width="95%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		<tr>
			<td><table><tr><td><span id="errMsgJson4hiveProfiles"/></span></td></tr></table></td>
		</tr>
	</s:if>
	<s:else>
		<s:if test="%{jsonMode == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><table><tr><td><span id="errMsgJson4hiveProfiles"/></span></td></tr></table></td>
			</tr>
		</s:if>
		<s:else>
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
						<td><input type="button" name="ignore" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
					</tr>
				</table>
				</td>
			</tr>
		</s:else>
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table border="0" cellspacing="0" cellpadding="0" width="700px">
			</s:if>
			<s:else>
				<table border="0" cellspacing="0" cellpadding="0" width="700px" class="editBox">
			</s:else>
					<tr>
						<td>
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="180"><s:text
									name="config.hp.name" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.hiveName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"
									maxlength="%{hiveNameLength}" disabled="%{disabledName}" />&nbsp;<s:text
									name="config.hp.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.hp.l3TrafficPort" /></td>
								<td><s:textfield
									name="dataSource.l3TrafficPort" size="24" maxlength="5"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.hp.l3TrafficPort.range" /></td>
							</tr>
							<tr>
							<td class="labelT1"></td>
								<td class="noteInfo"><s:text
									name="config.hp.l3TrafficPort.note" /></td>
							</tr>
							<tr><td height="5px"></td> </tr>
							<tr>
								<td class="labelT1"><s:text name="config.hp.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" />&nbsp;<s:text
									name="config.hp.description.range" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td style="padding:4px 4px 4px 4px">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine" colspan="3"><img
									src="<s:url value="/images/spacer.gif"/>" height="1"
									class="dblk" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding:0 2px 0 6px"><s:checkbox
									name="dataSource.enabledPassword"
									value="%{dataSource.enabledPassword}"
									onclick="setPasswordEdit(this.checked);" /></td>
								<td><s:text name="config.hp.enabledPassword" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
					<tr>
						<td>
							<div style="display:<s:property value="%{hidePassword}"/>" id="hidePassword">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 30px"><s:radio label="Gender" name="dataSource.generatePasswordType"
											list="#{1:'Automatically generate password'}" id="autoRadioPass"
											onclick="showBlock(this.value)" />
										</td>
									</tr>
									<tr>
										<td style="padding:0 2px 0 30px">
										<s:radio label="Gender" name="dataSource.generatePasswordType"
											list="#{2:'Manually enter password'}"  id="manRadioPass"
											onclick="showBlock(this.value)" />
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td style="padding:0 2px 0 60px">
						<div style="display:<s:property value="%{hidePasswordTextBox}"/>" id="hidePasswordTextBox">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="120"><s:text
									name="config.hp.password" /></td>
								<td><s:password name="dataSource.hivePassword" showPassword="true"
									id="hivePassword" size="48" maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');"/>
									<s:textfield name="dataSource.hivePassword" cssStyle="display:none" disabled="true"
									id="hivePassword_text" size="48" maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
								<td>&nbsp;<s:text name="config.hp.password.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.hp.confirmPassword" /></td>
								<td><s:password name="confirmPassword" id="confirmPassword"
									value="%{dataSource.hivePassword}" size="48" showPassword="true"
									maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');"/>
									<s:textfield name="confirmPassword" id="confirmPassword_text"
									value="%{dataSource.hivePassword}" size="48"
									maxlength="%{passwordLength}" cssStyle="display:none" disabled="true"
									onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<s:checkbox id="chkToggleDisplay" name="ignore" value="true" disabled="%{writeDisable4Struts}"
													onclick="hm.util.toggleObscurePassword(this.checked,['hivePassword','confirmPassword'],['hivePassword_text','confirmPassword_text']);" />
											</td>
											<td>
												<s:text name="admin.user.obscurePassword" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						</div>
						</td>
					</tr>
					<tr>
						<td height="8"></td>
					</tr>
				    <tr>
				       <td style="padding: 0 0 6px 10px;">
							<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.ssid.allOption.legend" />','advancePanel');</script>
				       </td>
				    </tr>
				    <tr>
				       <td style="padding: 0 0 6px 30px;">
				           <div id="advancePanel" style="display: <s:property value="%{dataSource.advancePanelStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td style="padding-top:4px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="150px"><s:text
															name="config.hp.rtsThreshold" /></td>
														<td><s:textfield
															name="dataSource.rtsThreshold" size="33" maxlength="4"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
															name="config.hp.rtsThreshold.range" /></td>
													</tr>
													<tr>
														<td class="labelT1" width="150px"><s:text
															name="config.hp.fragThreshold" /></td>
														<td><s:textfield
															name="dataSource.fragThreshold" size="33" maxlength="4"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
															name="config.hp.fragThreshold.range" /></td>
													</tr>
												</table>
												</td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding-left: 6px;"><s:checkbox
															name="dataSource.enabledThreshold"
															value="%{dataSource.enabledThreshold}"
															onclick="setThresholdEdit(this.checked);"/></td>
														<td><s:text name="config.hp.enabledThreshold" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<td>
													<div style="display:<s:property value="%{hideThreshold}"/>"
														id="hideThreshold">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td style="padding-left: 30px;"><s:text name="config.hp.connectionThreshold" />&nbsp;&nbsp;&nbsp;</td>
																<td><s:select name="dataSource.connectionThreshold"
																	list="%{enumConnectionThreshold}" listKey="key" listValue="value"
																	value="dataSource.connectionThreshold" cssStyle="width: 200px;" /></td>
															</tr>
															<tr>
																<td colspan="2" height="6"></td>
															</tr>
															<tr>
																<td style="padding-left: 30px;"><s:text name="config.hp.pollingInterval" />&nbsp;&nbsp;&nbsp;</td>
																<td><s:textfield
																	name="dataSource.pollingInterval" size="33" maxlength="2"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																	name="config.hp.pollingInterval.range" /></td>
															</tr>
														</table>
													</div>
												</td>
											</tr>
										</table>
										</td>
									</tr>

									<tr>
										<td height="6"></td>
									</tr>
									<tr>
										<td style="padding:4px 4px 4px 4px">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td class="sepLine" colspan="3"><img
													src="<s:url value="/images/spacer.gif"/>" height="1"
													class="dblk" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-top:2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="150px"><s:text
													name="config.hp.hiveDos" /></td>
												<td width="200px" style="padding-right:5px;"><s:select
													name="hiveDos" list="%{hiveDosParameterProfiles}" listKey="id"
													listValue="value" cssStyle="width: 200px;" /></td>
												<td>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newHiveDos')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
												</td>
												<td>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editHiveDos')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="140px"><s:text
													name="config.hp.stationDos" /></td>
												<td style="padding-right:5px;"><s:select
													name="stationDos" list="%{stationDosParameterProfiles}"
													listKey="id" listValue="value" cssStyle="width: 200px;" /></td>
												<td>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newStationDos')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
												</td>
												<td>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editStationDos')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
											<%--tr>
												<td class="labelT1" width="140px"><s:text
													name="config.hp.l3Roaming" /></td>
												<td style="padding-right:5px;"><s:select
													name="roamingId" list="%{interRoaming}" listKey="id"
													listValue="value" cssStyle="width: 200px;" /></td>
												<td><input type="button" name="newRoaming" value="New" <s:property value="writeDisabled" />
													class="button short" onClick="submitAction('newRoaming');"></td>
											</tr--%>
										</table>
										</td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding:0 2px 0 6px"><s:checkbox
														name="dataSource.enabledL3Setting"
														value="%{dataSource.enabledL3Setting}"
														onclick="show_hideL3Setting(this.checked);" /></td>
												<td><s:text name="config.inter.roaming.l3Setting" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td>
											<div style="display:<s:property value="%{hideL3Setting}"/>" id="hideL3Setting">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding:5px 5px 5px 8px">
														<fieldset><legend><s:text
																name="config.inter.roaming.keepalives" /></legend>
															<div>
																<table border="0" cellspacing="0" cellpadding="0" width="100%">
																	<tr>
																		<td height="2px"/>
																	</tr>
																	<tr>
																		<td width="210px"><s:text
																			name="config.inter.roaming.aliveInterval" /></td>
																		<td><s:textfield name="dataSource.keepAliveInterval"
																			size="12" maxlength="6"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			&nbsp;<s:text name="config.inter.roaming.aliveInterval.range" /></td>
																	</tr>
																	<tr>
																		<td height="2px"/>
																	</tr>

																	<tr>
																		<td width="210px"><s:text
																			name="config.inter.roaming.aliveAgeout" /></td>
																		<td><s:textfield name="dataSource.keepAliveAgeout"
																			size="12" maxlength="4"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			&nbsp;<s:text name="config.inter.roaming.aliveAgeout.range" /></td>
																	</tr>
																</table>
															</div>
														</fieldset>
													</td>
												</tr>
												<tr>
													<td style="padding:5px 5px 5px 8px">
														<fieldset><legend><s:text
																name="config.inter.roaming.cacheUpdate" /></legend>
															<div>
																<table border="0" cellspacing="0" cellpadding="0" width="100%">
																	<tr>
																		<td height="2px"/>
																	</tr>
																	<tr>
																		<td width="210px"><s:text
																			name="config.inter.roaming.updateInterval" /></td>
																		<td><s:textfield name="dataSource.updateInterval"
																			size="12" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			&nbsp;<s:text name="config.inter.roaming.updateInterval.range" /></td>
																	</tr>
																	<tr>
																		<td height="2px"/>
																	</tr>
																	<tr>
																		<td width="210px"><s:text
																			name="config.inter.roaming.updateAgeout" /></td>
																		<td><s:textfield name="dataSource.updateAgeout"
																			size="12" maxlength="4"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			&nbsp;<s:text name="config.inter.roaming.updateAgeout.range" /></td>
																	</tr>
																	<tr>
																		<td style="padding:0 2px 0 0px" colspan="2"><s:checkbox
																				name="dataSource.neighborTypeAccess"
																				value="%{dataSource.neighborTypeAccess}"/>
																			<s:text name="config.inter.roaming.neighborTypeAccess" /></td>
																	</tr>
																	<tr>
																		<td colspan="2" style="padding-left: 22px" class="noteInfo"><s:text
																			name="config.inner.roaming.note" /></td>
																	</tr>
																	<tr>
																		<td style="padding:0 2px 0 0px" colspan="2"><s:checkbox
																				name="dataSource.neighborTypeBack"
																				value="%{dataSource.neighborTypeBack}"/>
																			<s:text name="config.inter.roaming.neighborTypeBack" /></td>
																	</tr>
																</table>
															</div>
														</fieldset>
													</td>
												</tr>
											</table>
										    </div>
										</td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td style="padding:4px 4px 4px 4px">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td class="sepLine" colspan="3"><img
													src="<s:url value="/images/spacer.gif"/>" height="1"
													class="dblk" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding: 4px 0 0 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>

												<td style="padding-right:15px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:push value="%{macFilterOptions}">
															<td colspan="3"><tiles:insertDefinition
																name="optionsTransfer" /></td>
														</s:push>
													</tr>
													<tr>
														<td height="5"></td>
													</tr>
													<tr>
														<td nowrap="nowrap"><s:text name="config.hp.defaultAction" /></td>
														<td><s:select name="dataSource.defaultAction"
															list="%{enumFilterAction}" listKey="key" listValue="value"
															value="dataSource.defaultAction" cssStyle="width: 100px;" />
														</td>
													</tr>
												</table>
												</td>
											</tr>
										</table>
										</td>
									</tr>

									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td style="padding:4px 4px 4px 4px">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td class="sepLine" colspan="3"><img
													src="<s:url value="/images/spacer.gif"/>" height="1"
													class="dblk" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1"><s:text name="config.hp.priority.title"/>
											</tr>
											<tr>
												<td style="padding-left:20px;">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td width="60px" class="labelT1"><s:text name="config.hp.eth0Priority" /></td>
														<td><s:select name="dataSource.eth0Priority"
															list="%{enumPriority}" listKey="key" listValue="value"
															value="dataSource.eth0Priority" cssStyle="width: 80px;" />
														</td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.hp.eth1Priority" /></td>
														<td><s:select name="dataSource.eth1Priority"
															list="%{enumPriority}" listKey="key" listValue="value"
															value="dataSource.eth1Priority" cssStyle="width: 80px;" />
														</td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.hp.agg0Priority" /></td>
														<td><s:select name="dataSource.agg0Priority"
															list="%{enumPriority}" listKey="key" listValue="value"
															value="dataSource.agg0Priority" cssStyle="width: 80px;" />
														</td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.hp.red0Priority" /></td>
														<td><s:select name="dataSource.red0Priority"
															list="%{enumPriority}" listKey="key" listValue="value"
															value="dataSource.red0Priority" cssStyle="width: 80px;" />
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
						</td>
					</tr>
					<tr>
						<td height="4"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>

<s:if test="%{jsonMode == true}">
<script type="text/javascript">
	function judgeFoldingIcon4HiveProfile() {
		adjustFoldingIcon('advancePanel');
	}

	YAHOO.util.Event.onContentReady("advancePanel", judgeFoldingIcon4HiveProfile, this);
</script>
</s:if>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
	<script>
		setCurrentHelpLinkUrl('<s:property value="helpLink" />');
	</script>
</s:if>