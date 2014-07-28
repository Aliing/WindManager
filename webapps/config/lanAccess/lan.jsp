<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<style type="text/css">
img.selected {
	border: 4px solid #0092d6;
}
img.unselected {
	border: 4px solid #fff;
}
</style>
<script type="text/javascript">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="dnsService" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}

var formName = 'lanProfiles';
var selectUIElement;
function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		// json mode
		var src;
		if("newServiceFilter" == operation) {
			src = '<s:url action="lanProfiles" includeParams="none" />?operation=newServiceFilter&jsonMode=true&ignore='
					+ new Date().getTime();
			// set the UI for sub dialog
			selectUIElement = Get(formName + "_serviceFilter");
		} else if ("editServiceFilter" == operation) {
			var value = Get(formName + "_serviceFilter").value;
			src = '<s:url action="lanProfiles" includeParams="none" />?operation=editServiceFilter&serviceFilter='+value 
					+ '&jsonMode=true&ignore=' +new Date().getTime();
			// set the UI for sub dialog
			selectUIElement = Get(formName + "_serviceFilter");
		} else if ("newIpDos" == operation) {
			src = '<s:url action="lanProfiles" includeParams="none" />?operation=newIpDos&jsonMode=true&ignore='
					+ new Date().getTime();
			// set the UI for sub dialog
			selectUIElement = Get(formName + "_ipDos");
		} else if ("editIpDos" == operation) {
			var value = Get(formName + "_ipDos").value;
			src = '<s:url action="lanProfiles" includeParams="none" />?operation=editIpDos&ipDos='+value 
					+ '&jsonMode=true&ignore=' +new Date().getTime();
			// set the UI for sub dialog
			selectUIElement = Get(formName + "_ipDos");
		}
		openIFrameDialog(800,550,src);
		</s:if>
		<s:else>
		// normal mode
		if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
			showProcessing();
		}
		
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
		</s:else>
	}
}
<s:if test="%{jsonMode}">
window.setTimeout("onloadEvent()", 100);
	<s:if test="%{writeDisabled!=''}">
	showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
	showHideNetworkPolicySubSaveBT(true);
	</s:else>
</s:if>
</script>

<div id="content">
<s:form action="lanProfiles">
	<s:hidden name="interfacesMode" />
	<s:hidden name="editMacFilterId" />
	<s:hidden name="wirelessMode" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<s:if test="%{!jsonMode}">
		<tr><td><tiles:insertDefinition name="context" /></td></tr>
		<tr>
			<td class="buttons">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_DNS_SERVICE%>');">
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
		<tr><td><tiles:insertDefinition name="notes" /></td></tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}">
			<table cellspacing="0" cellpadding="0" border="0" width="795">
			</s:if>
			<s:else>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="795">
			</s:else>
				<tr>
					<td style="padding: 6px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="125"><label><s:text
										name="config.dnsService.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td colspan="2"><s:textfield name="dataSource.name"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="24" maxlength="%{profileNameLength}"
										disabled="%{disabledName}" />&nbsp;<s:text
										name="config.ns.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="125"><s:text
										name="config.dnsService.description" /></td>
									<td colspan="2"><s:textfield name="dataSource.description"
										size="48" maxlength="%{profileDescirptionLength}" />&nbsp;<s:text
										name="config.ns.description.range" /></td>
								</tr>
								<tr><td height="4px;"/></tr>
								<tr id="interfaceSection">
									<td colspan="3">
									<fieldset>
										<legend><s:text name="config.title.lanProfile.interfaces"/></legend>
										<table align="center" style="border-collapse: collapse;">
										<tr><td colspan="6"><span style="position: relative;left:-220px;width: 300px; display: inline-block;"><s:text name="config.lanProfile.interfaces.desc" /></span></td></tr>
										<tr>
											<td style="padding: 5px 0px 0px 10px;width: 30px;text-align: center;"><img class="unselected" style="width: 30px;height: 30px;" src='<s:url value="/images/hm_v2/profile/hm-icon-lan-eth0.png"/>'/><br>&nbsp;ETH0</td>
											<td style="padding: 5px 10px 0px 10px;width: 30px;"><img id="eth1Img" class="unselected" src='<s:url value="/images/hm_v2/profile/hm-icon-lan.png"/>'/><br>&nbsp;ETH1</td>
											<td width="1px"><hr size="50" width="1px" /></td>
											<td style="padding: 5px 10px 0px 10px;width: 30px;"><img id="eth2Img" class="unselected" src='<s:url value="/images/hm_v2/profile/hm-icon-lan.png"/>'/><br>&nbsp;ETH2</td>
											<td style="padding: 5px 10px 0px 10px;width: 30px;"><img id="eth3Img" class="unselected" src='<s:url value="/images/hm_v2/profile/hm-icon-lan.png"/>'/><br>&nbsp;ETH3</td>
											<td style="padding: 5px 0px 0px 10px;width: 30px;"><img id="eth4Img" class="unselected" src='<s:url value="/images/hm_v2/profile/hm-icon-lan.png"/>'/><br>&nbsp;ETH4</td>
										</tr>
										<tr style="display: none;">
											<td colspan="2" style="padding: 0 5px 0 10px;"><hr size="1" width="100%"/></td>
											<td colspan="2"><font style="font-size: 10px; color:#474646"><s:text name="config.guid.hiveAp.list.branchRouters"></s:text></font></td>
											<td colspan="2" style="padding-left: 5px;"><hr size="1" width="100%"/></td>
										</tr>
										<tr>
											<td style="padding: 0 5px 0 0;" colspan="3"><hr size="1" width="100%"/></td>
											<td colspan="2"><span style="position: relative;left:-100px;display: inline-block;background-color: white;"><font style="font-size: 9px; color:#474646">HiveAP 330/350</font></span></td>
										</tr>
										</table>
									</fieldset>
									</td>
								</tr>
								<tr><td height="10px;"/></tr>
								<tr>
									<td colspan="3">
									<s:checkbox name="dataSource.enabled8021Q" onclick="enableTrunkMode(this.checked);"/><label for="lanProfiles_dataSource_enabled8021Q"><s:text name="config.lanProfile.trunk.desc" /></label>
									</td>
								</tr>
								<tr><td height="10px;"/></tr>
								<tr id="accessSecuritySection">
									<td colspan="3">
									<fieldset>
										<legend><s:text name="config.title.lanProfile.access"/></legend>
										<table>
										    <s:if test="%{!wirelessMode}">
											<tr>
												<td style="padding:0 4px 0 6px">
													<s:checkbox name="dataSource.enabled8021X" onclick="enable802dot1XMode(this.checked);"/>
													<label for="lanProfiles_dataSource_enabled8021X"><s:text name="config.lanProfile.enable.802dot1X" /></label>
												</td>
											</tr>
										    </s:if>
											<tr>
												<td style="padding:0 4px 0 6px">
													<s:checkbox name="dataSource.cwpSelectEnabled" onclick="enableCWPAuth(this.checked);" />
													<label for="lanProfiles_dataSource_cwpSelectEnabled"><s:text name="hiveAp.ethCwp.enable.cwp" /></label>
												</td>
											</tr>
											<tr id="enableMacAuthRow">
												<td style="padding:0 4px 0 26px">
													<s:checkbox name="dataSource.macAuthEnabled" onclick="changeMacAuth(this.checked);"/>
													<label for="lanProfiles_dataSource_macAuthEnabled"><s:text name="config.ssid.enabledMAC" /></label>
												</td>
											</tr>
											<tr id="macAuthRow">
											     <td style="padding-left: 50px">
											         <s:text name="config.ssid.radiusAuth" />&nbsp;&nbsp;&nbsp;
											         <s:select name="dataSource.authProtocol"
                                                            list="%{enumRadiusAuth}" listKey="key"
                                                            listValue="value" cssStyle="width: 160px;" />
											     </td>
											</tr>
										</table>
									</fieldset>
									</td>
								</tr>
								<tr><td height="4px;"/></tr>
								<tr id="optionSection">
									<td colspan="3">
									<fieldset>
										<legend><s:text name="config.ssid.allOption.legend" /></legend>
										<table>
											<tr>
												<td id="dosSettings">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td height="4px"/>
													</tr>
													<tr>
														<td style="padding-left: 10px">
															<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr>
																	<td class="labelT1" width="220px"><s:text
																		name="config.lanProfile.mgtServiceFilter" /></td>
																	<td width="200px" style="padding:0 5px 0 10px;"><s:select
																		name="serviceFilter" list="%{serviceFilterProfiles}" listKey="id"
																		listValue="value" cssStyle="width: 200px;" /></td>
																	<td width="20px">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn"
																			src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newServiceFilter')"><img class="dinl"
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
																			<a class="marginBtn" href="javascript:submitAction('editServiceFilter')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />"
																			width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td height="4px"/>
													</tr>
													<tr id="authSequenceRow">
														<td style="padding-left: 10px">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="220px"><s:text name="config.ssid.auth.sequence"></s:text></td>
																	<td width="200px" class="labelT1"><s:select name="dataSource.authSequence"
																		list="%{enumAuthSequence}" listKey="key" listValue="value"
																		value="dataSource.authSequence" cssStyle="width: 300px;" />
																	</td>
																</tr>
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
							</table>
							</td>
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
<script>
var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event;

var interfaceModeArray;
var images;

YUE.onDOMReady(onloadEvent);

function onloadEvent(){
	// attach the click event
	images = ['eth1Img', 'eth2Img', 'eth3Img', 'eth4Img'];
	YUE.on(images, "click", selectInterfacesMode);
	// set the defaul interfacesMode
	var interfacesModeString = document.forms[formName].interfacesMode.value;
	if(interfacesModeString == null || interfacesModeString == '') {
		interfaceModeArray = [false,false,false,false];
	} else {
		interfaceModeArray = interfacesModeString.split(",");
		initInterfacesMode(interfaceModeArray);
	}
	debug("init interfaceModeArray: "+interfaceModeArray);
	// initial UI according 802.1Q on|off
	enableTrunkMode(<s:property value="dataSource.enabled8021Q"/>);
	// initial CWP
	enableCWPAuth(<s:property value="dataSource.cwpSelectEnabled"/>);
	// initial MAC Auth
	changeMacAuth(<s:property value="dataSource.macAuthEnabled"/>);
	// initial 8021X
	enable802dot1XMode(!<s:property value="dataSource.enabled8021Q"/> && <s:property value="dataSource.enabled8021X"/>);
}

var selectInterfacesMode = function() {
	debug("select the Interfaces Mode:"+this.id +" index:"+getOptionModeIndex(this.id));
	if(YUD.hasClass(YUD.get(this.id),  "selected")) {
		YUD.replaceClass(YUD.get(this.id), "selected", "unselected");
		interfaceModeArray[getOptionModeIndex(this.id)] = false;
	} else {
		YUD.replaceClass(YUD.get(this.id), "unselected", "selected");
		interfaceModeArray[getOptionModeIndex(this.id)] = true;
	}
	document.forms[formName].interfacesMode.value = interfaceModeArray;
}
function initInterfacesMode(modeArray) {
	for(var index=0; index<modeArray.length; index++) {
		var flag = modeArray[index];
		debug("flag: "+flag);
		if(flag == 'true') {
			YUD.replaceClass(YUD.get(images[index]), "unselected", "selected");
		}
	}
}
function getOptionModeIndex(id) {
	for(var index=0; index<images.length; index++) {
		if(images[index] == id) {
			return index;
		}
	}
}

function validate(operation) {
	// go thru if cancel
	if('<%=Navigation.L2_FEATURE_DNS_SERVICE%>' == operation 
			|| 'cancel' + '<s:property value="lstForward"/>' == operation) {
		return true;
	}
	
	if(operation == "editIpDos"){
		var value = hm.util.validateListSelection(formName + "_ipDos");
		if(value < 0){
			return false;
		}
	}
	if(operation == "editServiceFilter"){
		var value = hm.util.validateListSelection(formName + "_serviceFilter");
		if(value < 0){
			return false;
		}
	}
	
	return validateLANForm();
}

function validateLANForm() {
	// basic
    var nameElement = Get(formName + "_dataSource_name");
    if (nameElement.value.length == 0) {
        hm.util.reportFieldError(nameElement, 
                '<s:text name="error.requiredField"><s:param><s:text name="config.dnsService.name" /></s:param></s:text>');
        nameElement.focus();
        return false;
    }
    
    return true;
}

function enableTrunkMode(flag) {
	debug(flag+" typeof:"+typeof(flag));
	if(flag === true) {
		// Trunk
		hm.util.hide("accessSecuritySection");
		hm.util.hide("authSequenceRow");
		resetChkCWP();
		resetChk8021X();
	} else {
		hm.util.show("accessSecuritySection");
		hm.util.show("authSequenceRow");
	}
}
function enableCWPAuth(flag) {
	var chkCWPEl = Get(formName + "_dataSource_cwpSelectEnabled");
	if(chkCWPEl && flag) {
		hm.util.show("enableMacAuthRow");
		resetChk8021X();
	} else {
		hm.util.hide("enableMacAuthRow");
		Get(formName + "_dataSource_macAuthEnabled").checked = false;
		hm.util.hide("macAuthRow");
	}
}
function resetChkCWP() {
	var chkCWPEl = Get(formName + "_dataSource_cwpSelectEnabled");
	if(chkCWPEl) {
		chkCWPEl.checked = false;
		enableCWPAuth(false);
	}
}
function changeMacAuth(flag) {
	if(flag) {
		hm.util.show("macAuthRow");
	} else {
		hm.util.hide("macAuthRow");
	}
}
function enable802dot1XMode(flag) {
   var chk8021XEl = Get(formName + "_dataSource_enabled8021X");
    if(chk8021XEl && flag) {
    	resetChkCWP();
    }
}
function resetChk8021X() {
	var chk8021XEl = Get(formName + "_dataSource_enabled8021X");
	if(chk8021XEl) {
		chk8021XEl.checked = false;
	}
}
function disableChkElement(element, flag) {
    if(element) element.disabled = flag;
}
function promptPortTypeChanged() {return false;}
function debug(msg) {
	//console.debug(msg);
}
<s:if test="%{jsonMode == true}">
   setCurrentHelpLinkUrl('<s:property value="helpLink" />');
</s:if>
</script>