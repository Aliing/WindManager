<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'tunnelSetting';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_tunnelName").disabled) {
		document.getElementById(formName + "_dataSource_tunnelName").focus();
	}
	<s:if test="%{jsonMode==true}">
	    if (top.isIFrameDialogOpen()) {
	        top.changeIFrameDialog(800, 600);
	    }
 	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		if(document.getElementById(formName + "_dataSource_enableType2").checked) {
			hm.options.selectAllOptions('selectedIps');
		}
	    document.forms[formName].submit();
	}
}

function saveTunnelSetting(operation) {
	if (validate(operation)) {
		if(document.getElementById(formName + "_dataSource_enableType2").checked) {
			hm.options.selectAllOptions('selectedIps');
		}
		var url = "";
		if (operation == 'create') {
			url = "<s:url action='tunnelSetting' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		} else if (operation == 'update') {
			url = "<s:url action='tunnelSetting' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["tunnelSetting"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveJsonTunnelSettingDlg, failure : failSaveJsonTunnelSettingDlg, timeout: 60000}, null);
	}
}

var succSaveJsonTunnelSettingDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	} catch(e) {
		// do nothing now.
	}
}

var failSaveJsonTunnelSettingDlg = function(o) {
	// do nothing now.
}

function validate(operation) {
	if (operation == 'cancel<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_unroamingInterval").value="60";
		document.getElementById(formName + "_dataSource_unroamingAgeout").value="0";
		return true;
	}
	if (operation == 'newIpAddress' || operation == 'newipAddress' || operation == "editIpAddress" || operation == "editipAddress") {
		document.getElementById(formName + "_dataSource_unroamingInterval").value="60";
		document.getElementById(formName + "_dataSource_unroamingAgeout").value="0";
		if (operation == 'newIpAddress') {
			var ipnames = document.getElementById("myIpSelect");
			if (hm.util.hasSelectedOptionSameValue(ipnames, document.getElementById("dataSource.ipInputValue"))) {
				document.forms[formName].ipId.value = ipnames.options[ipnames.selectedIndex].value;
			} else {
				document.forms[formName].ipId.value = -1;
			}
		} else if (operation != "newipAddress"){
			var value;
			if (operation == "editIpAddress") {
				value = hm.util.validateListSelection("myIpSelect");
			} else {
				value = hm.util.validateOptionTransferSelection("selectedIps");
			}
			if(value < 0){
				return false
			} else if (operation == "editipAddress") {
				document.forms[formName].singleIpId.value = value;
			} else if (operation == "editIpAddress") {
				document.forms[formName].ipId.value = value;
			}
		}
	}

	if (operation == 'create' || operation == 'create<s:property value="lstForward"/>') {
		if (!validateTunnelName()) {
			return false;
		}
	}
	if (operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'
			|| operation == 'update' || operation == 'create') {
		if(document.getElementById(formName + "_dataSource_enableType1").checked) {	
			if (!validateUnroamingInterval()) {
				return false;
			}
			if (!validateUnroamingAgeout()) {
				return false;
			}
		}
		if(document.getElementById(formName + "_dataSource_enableType2").checked) {
			document.getElementById(formName + "_dataSource_unroamingInterval").value="60";
			document.getElementById(formName + "_dataSource_unroamingAgeout").value="0";
			if (document.getElementById(formName + "_dataSource_tunnelToType1").checked) {
				if (!validateSelectIpAddress()) {
					return false;
				}
			}
			if (document.getElementById(formName + "_dataSource_tunnelToType2").checked) {
				if (!checkIpAddressRange()) {
					return false;
				}
			}
			if (document.getElementById("selectedIps").length < 1) {
				hm.util.reportFieldError(document.getElementById("ipOption"), '<s:text name="error.requiredField"><s:param><s:text name="config.tunnelSetting.enabledFrom" /></s:param></s:text>');
				return false;
			}
			if (!validateKeyConfirmValue()) {
				return false;
			}
		}
	}
	return true;
}

function validateUnroamingInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_unroamingInterval");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.tunnelSetting.unroamingInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.tunnelSetting.unroamingInterval" />',
                                                       <s:property value="%{unroamingIntervalRange.min()}" />,
                                                       <s:property value="%{unroamingIntervalRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateUnroamingAgeout() {
      var inputElement = document.getElementById(formName + "_dataSource_unroamingAgeout");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.tunnelSetting.unroamingAgeout" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.tunnelSetting.unroamingAgeout" />',
                                                       <s:property value="%{unroamingAgeoutRange.min()}" />,
                                                       <s:property value="%{unroamingAgeoutRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateTunnelName() {
    var inputElement = document.getElementById(formName + "_dataSource_tunnelName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.tunnelSetting.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateSelectIpAddress() {
	var ipnames = document.getElementById("myIpSelect");
	var ipValue = document.getElementById("dataSource.ipInputValue");
	var showError = document.getElementById("errorDisplay");
	if ("" == ipValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.tunnelSetting.ipAddress" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tunnelSetting.ipAddress" /></s:param></s:text>');
			ipValue.focus();
			return false;
		}
		document.forms[formName].ipId.value = -1;
	} else {
		document.forms[formName].ipId.value = ipnames.options[ipnames.selectedIndex].value;
	}
    return true;
}

function checkIpAddressRange() {
	var ipRangeStart = document.getElementById(formName + "_dataSource_ipRangeStart");
	var ipRangeEnd = document.getElementById(formName + "_dataSource_ipRangeEnd");

	if (!hm.util.validateIpAddress(ipRangeStart.value)) {
		hm.util.reportFieldError(ipRangeStart, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tunnelSetting.startIpAddress" /></s:param></s:text>');
		ipRangeStart.focus();
		return false;
	}

	if (!hm.util.validateIpAddress(ipRangeEnd.value)) {
		hm.util.reportFieldError(ipRangeEnd, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tunnelSetting.endIpAddress" /></s:param></s:text>');
		ipRangeEnd.focus();
		return false;
	}
   	var ipRangeStartValue = hm.util.trim(ipRangeStart.value).split(".");
	var ipRangeEndValue = hm.util.trim(ipRangeEnd.value).split(".");
	var ipCorrect = false;
	if (parseInt(ipRangeStartValue[0]) < parseInt(ipRangeEndValue[0])) {
		ipCorrect = true;
	} else if (parseInt(ipRangeStartValue[0]) == parseInt(ipRangeEndValue[0])){
		if (parseInt(ipRangeStartValue[1]) < parseInt(ipRangeEndValue[1])) {
			ipCorrect = true;
		} else if (parseInt(ipRangeStartValue[1]) == parseInt(ipRangeEndValue[1])) {
			if (parseInt(ipRangeStartValue[2]) < parseInt(ipRangeEndValue[2])) {
				ipCorrect = true;
			} else if (parseInt(ipRangeStartValue[2]) == parseInt(ipRangeEndValue[2])){
				if (parseInt(ipRangeStartValue[3]) <= parseInt(ipRangeEndValue[3])) {
					ipCorrect = true;
				}
			}
		}
	}
	
	if (ipCorrect == false) {
		hm.util.reportFieldError(ipRangeStart, '<s:text name="error.notLargerThan"><s:param><s:text name="config.tunnelSetting.startIpAddress" /></s:param><s:param><s:text name="config.tunnelSetting.endIpAddress" /></s:param></s:text>');
		ipRangeStart.focus();
		return false;
	}
	return true;
}

function validateKeyConfirmValue() {
      var keyElement;
      var confirmElement;
	  if (document.getElementById("chkPasswordDisplay").checked) {
		  keyElement = document.getElementById("tunnelPassword");
		  confirmElement = document.getElementById("confirmPassword");
	  } else {
		  keyElement = document.getElementById("tunnelPassword_text");
		  confirmElement = document.getElementById("confirmPassword_text");
	  }
      
	  if (keyElement.value.length ==0) {
	      hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.tunnelSetting.password" /></s:param></s:text>');
	      keyElement.focus();
	      return false;
      }

      if (confirmElement.value.length == 0) {
          hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.tunnelSetting.confirmPassword" /></s:param></s:text>');
          confirmElement.focus();
          return false;
      }

	  var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.tunnelSetting.password" />');
	  if (message != null) {
	      hm.util.reportFieldError(keyElement, message);
	      keyElement.focus();
	      return false;
	  }

      if (keyElement.value != confirmElement.value) {
	      hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.tunnelSetting.confirmPassword" /></s:param><s:param><s:text name="config.tunnelSetting.password" /></s:param></s:text>');
	      keyElement.focus();
	      return false;
      }     
      return true;
}

function showFirstBlock(checked)
{
  	var div_block_first=document.getElementById("hideFirst");
  	var div_block_second=document.getElementById("hideSecond");
  	div_block_first.style.display = checked ? "" : "none";
  	div_block_second.style.display = checked ? "none" : "";
}

function showFirstChild(checked)
{
	var ipList = document.getElementById("myIpSelect");
	ipList.disabled = !checked;
	var ipValue = document.getElementById("dataSource.ipInputValue");
	ipValue.disabled = !checked;
	<s:if test="%{fullMode}">
	if (checked) {
		if ('' == '<s:property value="writeDisabled" />') {
			document.getElementById("disabledButton").style.display="none";
			document.getElementById("enableButton").style.display="";
		}
	} else {
		document.getElementById("disabledButton").style.display="";
		document.getElementById("enableButton").style.display="none";
	}
	</s:if>
	document.getElementById(formName + "_dataSource_ipRangeStart").disabled = checked;
	document.getElementById(formName + "_dataSource_ipRangeEnd").disabled = checked;
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	document.getElementById("tunnelPassword").value=value;
	document.getElementById("confirmPassword").value=value;
	document.getElementById("tunnelPassword_text").value=value;
	document.getElementById("confirmPassword_text").value=value;
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function generalPassword(){
	var url = '<s:url action="tunnelSetting" includeParams="none" />?operation=genenatePassword&key='+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

var simpleIpCallBack = function (data) {
	if (data && data.items) {
		hm.simpleObject.removeOptions(document.getElementById("leftOptions_selectedIps"), data.items);
	}
}

var multiIpCallBack = function (data) {
	if (data) {
		if (data.type == "add" && data.item) {
			hm.simpleObject.addOption(document.getElementById("myIpSelect"), data.item.key, data.item.value, false)
		} else if (data.type == "remove" && data.items) {
			hm.simpleObject.INPUT_FIELD_ID = "dataSource.ipInputValue";
			hm.simpleObject.removeOptions(document.getElementById("myIpSelect"), data.items);
		}
	}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="tunnelSetting" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedTunnelName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="tunnelSetting">
	<s:hidden name="singleIpId" />
	<s:hidden name="ipId" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="id" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="tunnulSettingTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-tunnel_policies.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.tunnelSetting"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.tunnelSetting.edit"/></td>
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
				<td align="right">
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{writeDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onclick="saveTunnelSetting('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onclick="saveTunnelSetting('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
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
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="tunnelSettingSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>

					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');">
					</td>

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
			<table border="0" cellspacing="0" cellpadding="0" width="680">
			</s:if>
			<s:else>
			<table class="editBox" border="0" cellspacing="0" cellpadding="0"
				width="680">
			</s:else>
				<tr>
					<td style="padding:0 0 0 10px">
					<%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="140"><s:text name="config.tunnelSetting.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.tunnelName" size="24"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="%{tunnelNameLength}" disabled="%{disabledName}" />&nbsp;<s:text
								name="config.tunnelSetting.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.tunnelSetting.description" /></td>
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />&nbsp;<s:text
								name="config.tunnelSetting.description.range" /></td>
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
					<td style="padding:2px 4px 2px 4px">
					<fieldset><legend><s:text
						name="config.tunnelSetting.tab" /></legend>
					<div>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="2px" />
						</tr>
						<tr>
							<td><s:radio label="Gender" name="dataSource.enableType"
								list="#{1:''}"
								value="%{dataSource.enableType}" onclick="showFirstBlock(true)" />
							<s:text name="config.tunnelSetting.enabledDynamic" /></td>	
						</tr>
						<tr>
							<td style="padding:4px 4px 4px 25px">
							<table border="0" cellspacing="0" cellpadding="0" id="hideFirst"
								style="display:<s:property value="hideFirst"/>">
								<!--  
								<tr>
									<td><s:checkbox id="roamingEnable"
										name="dataSource.roamingEnable"
										value="%{dataSource.roamingEnable}" /><s:text name="config.tunnelSetting.roamingEnable" /></td>
								</tr>
								-->
								<tr>
								<td style="padding:5px 0px 5px 2px">
									<fieldset><legend><s:text 
											name="config.tunnelSetting.unroaming" /></legend>
										<div>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="2px"/>
												</tr>
												<tr>
													<td width="50px"><s:text
														name="config.tunnelSetting.unroamingInterval" /></td>
													<td width="160px"><s:textfield name="dataSource.unroamingInterval"
														size="4" maxlength="3" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														<s:text name="config.tunnelSetting.unroamingInterval.range" /></td>
													<td width="180px"><s:text
														name="config.tunnelSetting.unroamingAgeout" /></td>
													<td><s:textfield name="dataSource.unroamingAgeout"
														size="8" maxlength="10" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														<s:text name="config.tunnelSetting.unroamingAgeout.range" /></td>
												</tr>
											</table>	
										</div>
									</fieldset>
								</td>
								</tr>
							</table>
							</td>
						</tr>

						<tr>
							<td><s:radio label="Gender" name="dataSource.enableType"
								list="#{2:''}"
								value="%{dataSource.enableType}" onclick="showFirstBlock(false)" />
							 <s:text name="config.tunnelSetting.enabledStatic" /></td>	
						</tr>
						<tr>
							<td style="padding:4px 4px 4px 15px">
							<table border="0" cellspacing="0" cellpadding="0" id="hideSecond"
								style="display:<s:property value="hideSecond"/>">
								<tr>
									<td>
									<fieldset>
										<legend><s:text name="config.tunnelSetting.enabledTo" /></legend>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="3px" />
												</tr>
												<tr>
													<td><s:radio
														name="dataSource.tunnelToType" list="#{1:''}"
														value="%{dataSource.tunnelToType}"
														onclick="showFirstChild(true)" /></td>
													<td width="100px"><s:text name="config.tunnelSetting.ipAddress" /></td>
													<td colspan="2">
														<ah:createOrSelect divId="errorDisplay" list="availableIpAddress" typeString="IpAddressTunnel"
															selectIdName="myIpSelect" inputValueName="dataSource.ipInputValue" hideButton="true"
															callbackFn="simpleIpCallBack" />
													</td>
													<s:if test="%{fullMode}">
													<td style="padding:0 5px 0 5px;display:<s:property value="enableButton"/>" id="enableButton">
														<a class="marginBtn" href="javascript:submitAction('newIpAddress')"><img class="dinl"
															src="<s:url value="/images/new.png" />"
															width="16" height="16" alt="New" title="New" /></a>
														<a class="marginBtn" href="javascript:submitAction('editIpAddress')"><img class="dinl"
															src="<s:url value="/images/modify.png" />"
															width="16" height="16" alt="Modify" title="Modify" /></a>
													</td>
													<td style="padding:0 5px 0 5px;display:<s:property value="disabledButton"/>" id="disabledButton">
														<img class="dinl marginBtn"
															src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" />
														<img class="dinl marginBtn"
															src="<s:url value="/images/modify_disable.png" />"
															width="16" height="16" alt="Modify" title="Modify" />
													</td>
													</s:if>
												</tr>
												<tr>
													<td height="6px" />
												</tr>
												<tr>
													<td><s:radio name="dataSource.tunnelToType"
														list="#{2:''}" value="%{dataSource.tunnelToType}"
														onclick="showFirstChild(false)" /></td>
													<td width="100px"><s:text name="config.tunnelSetting.ipAddressRange" />
														&nbsp;&nbsp;&nbsp;&nbsp;<s:text
														name="config.tunnelSetting.start" /></td>
													<td width="160px" align="left"><s:textfield
														name="dataSource.ipRangeStart" size="24"
														disabled="%{disabledIpRange}" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
													<td width="20px"><s:text name="config.tunnelSetting.end" /></td>
													<td><s:textfield name="dataSource.ipRangeEnd" size="24"
														disabled="%{disabledIpRange}" maxlength="15"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td height="2px" />
								</tr>
								<tr>
									<td style="padding:4px 0px 4px 0px">
										<fieldset><legend><s:text 
												name="config.tunnelSetting.enabledFrom" /></legend>
											<div>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td height="5">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																	<label id="ipOption"></label>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<s:push value="%{ipAddressOptions}">
															<td colspan="3"><tiles:insertDefinition
																name="optionsTransfer"/></td>
														</s:push>
													</tr>
													<tr>
														<td height="5"></td>
													</tr>
													<%--<tr>
														<td style="padding:0 5px 0 35px;">
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newipAddress')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editipAddress')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
														</td>
													</tr>--%>
												</table>
											</div>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
									<fieldset>
										<legend><s:text name="config.tunnelSetting.authentication" /></legend>
										<table>
											<tr>
												<td width="115px"><s:text name="config.tunnelSetting.password" /><font color="red"><s:text name="*"/></font></td>
												<td><s:password name="dataSource.password" size="24" id="tunnelPassword" showPassword="true"
													maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
													<s:textfield id="tunnelPassword_text" name="dataSource.password" size="24" maxlength="64" cssStyle="display:none"
													disabled="true" onkeypress="return hm.util.keyPressPermit(event,'password');" /></td>
												<td width="100px">&nbsp;<s:text
													name="config.tunnelSetting.password.range" /></td>
											</tr>
	
											<tr>
												<td style="padding:2px 0 0 0"><s:text name="config.tunnelSetting.confirmPassword" /><font color="red"><s:text name="*"/></font></td>
												<td style="padding:2px 0 0 0"><s:password id="confirmPassword" value="%{dataSource.password}"  showPassword="true"
													size="24" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
													<s:textfield id="confirmPassword_text" size="24" maxlength="64" cssStyle="display:none"
													onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
												<td width="100px">&nbsp;<input type="button" name="ignore" value="Generate"
													class="button" onClick="generalPassword();" title="Auto Generate Password"
													<s:property value="writeDisabled" />></td>
											</tr>
                                            <tr>
												<td>&nbsp;</td>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<s:checkbox id="chkPasswordDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['tunnelPassword','confirmPassword'],['tunnelPassword_text','confirmPassword_text']);" 
																	disabled="%{writeDisable4Struts}" />
															</td>
															<td>
																<s:text name="admin.user.obscurePassword" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</fieldset>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
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
