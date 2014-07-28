<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'locationServer';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_name").disabled) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(950, 450);
	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		if (document.getElementById(formName + "_dataSource_ekahauPort").value==""){
			document.getElementById(formName + "_dataSource_ekahauPort").value==1;
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if ('<%=Navigation.L2_FEATURE_LOCATION_SERVER%>' == operation 
		|| operation == 'cancel<s:property value="lstForward"/>') {
		initGuiFieldValue();
		return true;
	}
	if (operation=='newIpAddress') {
		var ipnames = document.getElementById("myIpSelect");
		if (hm.util.hasSelectedOptionSameValue(ipnames, document.getElementById("dataSource.ipInputValue"))) {
			document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
		} else {
			document.forms[formName].ipAddressId.value = -1;
		}
	}
	if(operation == "editIpAddress"){
		var value = hm.util.validateListSelection("myIpSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipAddressId.value = value;
		}
	}

	<s:if test="%{jsonMode}">
	if (operation=='newIpAddress' || operation == "editIpAddress") {
		top.changeIFrameDialog(950, 450);
	}
	</s:if>

	if (operation == 'create<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_name");
	   	if (!checkNameValid(name, '<s:text name="config.ipFilter.name" />', name)) {
	       	return false;
	   	}
	}
	if (operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'
		 || operation == 'create' || operation == 'update'){
		
		if (document.getElementById('aeroscout2').checked)
		{
			var showError = document.getElementById("errorDisplay");
			var ipnames = document.getElementById("myIpSelect");
			var ipValue = document.getElementById("dataSource.ipInputValue");
			if ("" == ipValue.value) {
		        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusAssign.serverName" /></s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
				if (!hm.util.validateIpAddress(ipValue.value)) {
					if (!checkNameValid(ipValue, '<s:text name="config.radiusAssign.serverName" />', showError)) {
				       	return false;
				   	}
				}
				document.forms[formName].ipAddressId.value = -1;
			} else {
				document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
			}
			if (document.getElementById(formName + "_dataSource_enableTag").checked) {
				if (!checkRateThreshold(document.getElementById(formName + "_dataSource_tagThreshold"))) {
					return false;
				}
			}
			if (document.getElementById(formName + "_dataSource_enableStation").checked) {
				if (!checkRateThreshold(document.getElementById(formName + "_dataSource_stationThreshold"))) {
					return false;
				}
			}
			if (document.getElementById(formName + "_dataSource_enableRogue").checked) {
				if (!checkRateThreshold(document.getElementById(formName + "_dataSource_rogueThreshold"))) {
					return false;
				}
			}
		} else if (document.getElementById('ekahau3').checked){
			if (!checkEkahauValu()){
				return false;
			}
		}
		else
		{
			if (!checkAerohiveValue())
			{
				return false;
			}
		}
	}

	return true;
}

function checkEkahauValu(){
	var showError = document.getElementById("errorDisplay");
	var ipnames = document.getElementById("myIpSelect");
	var ipValue = document.getElementById("dataSource.ipInputValue");
	if ("" == ipValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusAssign.serverName" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			if (!checkNameValid(ipValue, '<s:text name="config.radiusAssign.serverName" />', showError)) {
		       	return false;
		   	}
		}
		document.forms[formName].ipAddressId.value = -1;
	} else {
		document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
	}

	if (!checkEkahauThreshold(document.getElementById(formName + "_dataSource_ekahauTagThreshold"))) {
		return false;
	}

	var ekahauMac = document.getElementById(formName + '_dataSource_ekahauMac');
	var ekahauPort = document.getElementById(formName + '_dataSource_ekahauPort');
	
	if (ekahauPort.value.length ==0) {
        hm.util.reportFieldError(ekahauPort, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.port" /></s:param></s:text>');
        ekahauPort.focus();
        return false;
	} else {
	    var message = hm.util.validateIntegerRange(ekahauPort.value, '<s:text name="config.location.server.port" />',
	    					<s:property value="%{ekahauPortRange.min()}" />,<s:property value="%{ekahauPortRange.max()}" />);
	    if (message != null) {
	        hm.util.reportFieldError(ekahauPort, message);
	        ekahauPort.focus();
	        return false;
	    }
	}
	
	if (ekahauMac.value.length ==0) {
        hm.util.reportFieldError(ekahauMac, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.macAddress" /></s:param></s:text>');
        ekahauMac.focus();
        return false;
	} else if (!hm.util.validateMacAddress(ekahauMac.value, 12)) {
		hm.util.reportFieldError(ekahauMac, '<s:text name="error.formatInvalid"><s:param><s:text name="config.location.server.macAddress" /></s:param></s:text>');
        ekahauMac.focus();
        return false;
	}

	return true;
}

function checkNameValid(name, title, errorDis) {
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(errorDis, message);
       	name.focus();
       	return false;
   	}
   	return true;
}

function checkRateThreshold(inputElement) {
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.threshold" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.location.server.threshold" />',
    											<s:property value="%{thresholdRange.min()}" />,<s:property value="%{thresholdRange.max()}" />);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function checkEkahauThreshold(inputElement) {
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.ekahauThreshold" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.location.server.ekahauThreshold" />',
    											<s:property value="%{thresholdRange.min()}" />,<s:property value="%{thresholdRange.max()}" />);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function checkAerohiveValue()
{
	var rssiThreshold = document.getElementById('rssiChangeThreshold');
	var reportIterval = document.getElementById('locationReportInterval');
	var rssiValidPeriod = document.getElementById('rssiValidPeriod');
	var rssiHoldCount = document.getElementById('rssiHoldCount');
	var suppressionCount = document.getElementById('suppressionCount');
	
	if (rssiThreshold.value.length == 0) {
        hm.util.reportFieldError(rssiThreshold, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.rssiThreshold" /></s:param></s:text>');
        rssiThreshold.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(rssiThreshold.value, '<s:text name="config.location.server.rssiThreshold" />',1,5);
    if (message != null) {
		hm.util.reportFieldError(rssiThreshold, message);
		rssiThreshold.focus();
		return false;
    }
    
    if (rssiValidPeriod.value.length == 0) {
        hm.util.reportFieldError(rssiValidPeriod, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.rssiValidPeriod" /></s:param></s:text>');
        rssiValidPeriod.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(rssiValidPeriod.value, '<s:text name="config.location.server.rssiValidPeriod" />',15,1200);
    if (message != null) {
		hm.util.reportFieldError(rssiValidPeriod, message);
		rssiValidPeriod.focus();
		return false;
    }
    
    if (rssiHoldCount.value.length == 0) {
        hm.util.reportFieldError(rssiHoldCount, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.rssiHoldCount" /></s:param></s:text>');
        rssiHoldCount.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(rssiHoldCount.value, '<s:text name="config.location.server.rssiHoldCount" />',0,10);
    if (message != null) {
		hm.util.reportFieldError(rssiHoldCount, message);
		rssiHoldCount.focus();
		return false;
    }
    
    if (reportIterval.value.length == 0) {
        hm.util.reportFieldError(reportIterval, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.reportInterval" /></s:param></s:text>');
        reportIterval.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(reportIterval.value, '<s:text name="config.location.server.reportInterval" />',15,1200);
    if (message != null) {
		hm.util.reportFieldError(reportIterval, message);
		reportIterval.focus();
		return false;
    }
    
    if (suppressionCount.value.length == 0) {
        hm.util.reportFieldError(suppressionCount, '<s:text name="error.requiredField"><s:param><s:text name="config.location.server.suppressionCount" /></s:param></s:text>');
        suppressionCount.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(suppressionCount.value, '<s:text name="config.location.server.suppressionCount" />',0,80);
    if (message != null) {
		hm.util.reportFieldError(suppressionCount, message);
		suppressionCount.focus();
		return false;
    }
    
    return true;
}

function enableTagThresh(checked) {
	document.getElementById(formName + "_dataSource_tagThreshold").disabled =!checked;
	if(!checked) {
		document.getElementById(formName + "_dataSource_tagThreshold").value="1000";
	}
}

function enableStaThresh(checked) {
	document.getElementById(formName + "_dataSource_stationThreshold").disabled =!checked;
	if(!checked) {
		document.getElementById(formName + "_dataSource_stationThreshold").value="200";
	}
}

function enableRogueThresh(checked) {
	document.getElementById(formName + "_dataSource_rogueThreshold").disabled =!checked;
	if(!checked) {
		document.getElementById(formName + "_dataSource_rogueThreshold").value="50";
	}
}

function initGuiFieldValue(){
	document.getElementById(formName + "_dataSource_tagThreshold").value="1000";
	document.getElementById(formName + "_dataSource_stationThreshold").value="200";
	document.getElementById(formName + "_dataSource_rogueThreshold").value="50";
	document.getElementById(formName + "_dataSource_ekahauPort").value="";
	document.getElementById(formName + "_dataSource_ekahauMac").value="01188E000000";
	document.getElementById(formName + "_dataSource_ekahauTagThreshold").value="1000";
	document.getElementById('rssiChangeThreshold').value="3";
	document.getElementById('rssiValidPeriod').value="60";
	document.getElementById('rssiHoldCount').value="0";
	document.getElementById('locationReportInterval').value="60";
	document.getElementById('suppressionCount').value="0";
}

function selectAerohive(checked)
{
	hm.util.show('aerohiveSection');
	hm.util.hide('aeroscoutSection');
	hm.util.hide('aeroscoutSection2');
	hm.util.hide('ekahauSection');
	initGuiFieldValue();
}

function selectAeroscout(checked)
{
	hm.util.hide('aerohiveSection');
	hm.util.show('aeroscoutSection');
	hm.util.show('aeroscoutSection2');
	hm.util.hide('ekahauSection');
	initGuiFieldValue();
}
function selectEkahau(checked)
{
	hm.util.hide('aerohiveSection');
	hm.util.hide('aeroscoutSection2');
	hm.util.show('aeroscoutSection');
	hm.util.show('ekahauSection');
	initGuiFieldValue();

}

function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='locationServer' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
		if (operation == 'create') {
			// 
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["locationServer"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
			if (details.serviceType == 1) {
				parent.document.getElementById("hideClientWatchDiv").style.display="";
			} else {
				parent.document.getElementById("hideClientWatchDiv").style.display="none";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="locationServer" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</s:if>
</script>
<div id="content">
	<s:form action="locationServer">
		<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<div id="titleDiv" style="margin-bottom:15px;">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-location_servers.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.location.server.title"/>
									</s:if>
									<s:else>
										<s:text name="config.location.server.title.edit"/>
									</s:else>
									&nbsp;
								</td>
								<td>
									<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
										<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
											alt="" class="dblk" />
									</a>
								</td>
							</tr>
						</table>
						</td>
						<td align="right">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
								<s:if test="%{dataSource.id == null}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</tr>
						</table>
						</td>
					</tr>
				</table>
		</div>
		</s:if>
		<s:hidden name="ipAddressId" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{jsonMode == false}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td>
									<input type="button" name="ignore"
										value="<s:text name="button.create"/>" class="button"
										onClick="submitAction('create<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="ignore"
										value="<s:text name="button.update"/>" class="button"
										onClick="submitAction('update<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
								</td>
							</s:else>
							<s:if test="%{lstForward == null || lstForward == ''}">
								<td>
									<input type="button" name="cancel" value="Cancel"
										class="button"
										onClick="submitAction('<%=Navigation.L2_FEATURE_LOCATION_SERVER%>');">
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="cancel" value="Cancel"
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
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td style="padding-top: 5px;">
				<s:if test="%{jsonMode == true}">
					<table cellspacing="0" cellspacing="0" cellpadding="0" border="0" width="800px">
				</s:if>
				<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
				</s:else>
						<tr>
							<td style="padding-left:4px">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td class="labelT1" width="95">
											<label>
												<s:text name="config.ipFilter.name" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield name="dataSource.name" size="24"
												onkeypress="return hm.util.keyPressPermit(event,'name');"
												maxlength="%{nameLength}" disabled="%{disabledName}" />
											<s:text name="config.ipFilter.name.range" />
										</td>
									</tr>
									<tr>
										<td class="labelT1">
											<s:text name="config.ipFilter.description" />
										</td>
										<td>
											<s:textfield name="dataSource.description" size="48"
												maxlength="%{commentLength}" />
											<s:text name="config.ipFilter.description.range" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td style="padding-left: 6px;">
								<fieldset style="width: 750px">
									<legend>
										<s:text name="config.location.server.settings" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td height="10px"></td>
											</tr>
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:checkbox name="dataSource.enableServer"
																	value="%{dataSource.enableServer}" />
															</td>
															<td>
																<s:text name="config.location.server.enable" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td height="6"></td>
											</tr>
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:radio label="Gender" id="aerohive"
																	name="dataSource.serviceType"
																	list="%{locationType1}" listKey="key" listValue="value"
																	onclick="selectAerohive(this.checked);"
																	value="%{dataSource.serviceType}" />
															</td>
															<td style="padding-left: 10px">
																<s:radio label="Gender" id="aeroscout"
																	name="dataSource.serviceType"
																	list="%{locationType2}" listKey="key" listValue="value"
																	onclick="selectAeroscout(this.checked);"
																	value="%{dataSource.serviceType}" />
															</td>
															<td style="padding-left: 10px">
																<s:radio label="Gender" id="ekahau"
																	name="dataSource.serviceType"
																	list="%{locationType3}" listKey="key" listValue="value"
																	onclick="selectEkahau(this.checked);"
																	value="%{dataSource.serviceType}" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td height="5px"></td>
											</tr>
											<tr style="display:<s:property value="%{aerohiveDisplay}"/>"
												id="aerohiveSection">
												<td colspan="3">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr style="padding-left: 8px">
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="200px">
																			<label>
																				<s:text name="config.location.server.rssiThreshold" /><font color="red"><s:text name="*" /> </font>
																			</label>
																		</td>
																		<td>
																			<s:textfield id="rssiChangeThreshold"
																				name="dataSource.rssiChangeThreshold"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				size="20" maxlength="1" />
																			<s:text
																				name="config.location.server.rssiThreshold.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<label>
																				<s:text
																					name="config.location.server.rssiValidPeriod" /><font color="red"><s:text name="*" /> </font>
																			</label>
																		</td>
																		<td>
																			<s:textfield id="rssiValidPeriod"
																				name="dataSource.rssiValidPeriod"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				size="20" maxlength="4" />
																			<s:text
																				name="config.location.server.rssiValidPeriod.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<label>
																				<s:text name="config.location.server.rssiHoldCount" /><font color="red"><s:text name="*" /> </font>
																			</label>
																		</td>
																		<td>
																			<s:textfield id="rssiHoldCount"
																				name="dataSource.rssiHoldCount"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				size="20" maxlength="2" />
																			<s:text
																				name="config.location.server.rssiHoldCount.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<label>
																				<s:text name="config.location.server.reportInterval" /><font color="red"><s:text name="*" /> </font>
																			</label>
																		</td>
																		<td>
																			<s:textfield id="locationReportInterval"
																				name="dataSource.locationReportInterval"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				size="20" maxlength="4" />
																			<s:text
																				name="config.location.server.reportInterval.range" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1">
																			<label>
																				<s:text name="config.location.server.suppressionCount" /><font color="red"><s:text name="*" /> </font>
																			</label>
																		</td>
																		<td>
																			<s:textfield id="suppressionCount"
																				name="dataSource.reportSuppressCount"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"
																				size="20" maxlength="2" />
																			<s:text
																				name="config.location.server.suppressionCount.range" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{aeroscoutDisplay}"/>"
												id="aeroscoutSection">
												<td colspan="3">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="180px">
																			<label>
																				<s:text name="config.radiusAssign.serverName" />
																			</label><font color="red"><s:text name="*" /> </font>
																		</td>
																		<td>
																			<ah:createOrSelect divId="errorDisplay"
																				list="availableIpAddress" typeString="IpAddress"
																				selectIdName="myIpSelect"
																				inputValueName="dataSource.ipInputValue" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{aeroscoutDisplay2}"/>"
												id="aeroscoutSection2">
												<td colspan="3">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td height="6"></td>
														</tr>
														<tr>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td style="padding:0 2px 0 8px">
																			<s:checkbox name="dataSource.enableTag"
																				value="%{dataSource.enableTag}"
																				onclick="enableTagThresh(this.checked);" />
																		</td>
																		<td>
																			<s:text name="config.location.server.enable.tag" />
																		</td>
																		<td style="padding:0 5px 0 43px" width="100">
																			<s:text name="config.location.server.threshold" />
																		</td>
																		<td>
																			<s:textfield size="10" name="dataSource.tagThreshold"
																				disabled="%{!dataSource.enableTag}" maxlength="6"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			&nbsp;
																			<s:text name="config.location.server.threshold.range" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td height="6"></td>
														</tr>
														<tr>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td style="padding:0 2px 0 8px">
																			<s:checkbox name="dataSource.enableStation"
																				value="%{dataSource.enableStation}"
																				onclick="enableStaThresh(this.checked);" />
																		</td>
																		<td>
																			<s:text name="config.location.server.enable.station" />
																		</td>
																		<td style="padding:0 5px 0 23px" width="100">
																			<s:text name="config.location.server.threshold" />
																		</td>
																		<td>
																			<s:textfield size="10"
																				name="dataSource.stationThreshold"
																				disabled="%{!dataSource.enableStation}"
																				maxlength="6"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			&nbsp;
																			<s:text name="config.location.server.threshold.range" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td height="6"></td>
														</tr>
														<tr>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td style="padding:0 2px 0 8px">
																			<s:checkbox name="dataSource.enableRogue"
																				value="%{dataSource.enableRogue}"
																				onclick="enableRogueThresh(this.checked);" />
																		</td>
																		<td>
																			<s:text name="config.location.server.enable.rogue" />
																		</td>
																		<td style="padding:0 5px 0 10px" width="100">
																			<s:text name="config.location.server.threshold" />
																		</td>
																		<td>
																			<s:textfield size="10"
																				name="dataSource.rogueThreshold"
																				disabled="%{!dataSource.enableRogue}" maxlength="6"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			&nbsp;
																			<s:text name="config.location.server.threshold.range" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{ekahauDisplay}"/>"
												id="ekahauSection">
												<td colspan="3">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td colspan="2">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="180px">
																			<s:text name="config.location.server.ekahauThreshold"></s:text><font color="red"><s:text name="*" /> </font>
																		</td>
																		<td>
																			<s:textfield size="20" maxlength="6"
																				name="dataSource.ekahauTagThreshold"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield>
																			<s:text name="config.location.server.ekahauThreshold.range"></s:text>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td colspan="2">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="180px">
																			<s:text name="config.location.server.port"></s:text><font color="red"><s:text name="*" /> </font>
																		</td>
																		<td>
																			<s:textfield size="20" maxlength="5"
																				name="dataSource.ekahauPort"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield>
																			<s:text name="config.location.server.port.range"></s:text>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td colspan="2">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="180px">
																			<s:text name="config.location.server.macAddress"></s:text><font color="red"><s:text name="*" /> </font>
																		</td>
																		<td>
																			<s:textfield size="20" maxlength="12"
																				name="dataSource.ekahauMac"
																				onkeypress="return hm.util.keyPressPermit(event,'hex');"></s:textfield>
																			<s:text name="config.location.server.macAddress.range"></s:text>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td height="5px"></td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="8"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
