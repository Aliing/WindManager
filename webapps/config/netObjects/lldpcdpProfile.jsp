<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.LLDPCDPProfile"%>


<script>

function onLoadPage()
{
	<s:if test="%{jsonMode}">
			top.changeIFrameDialog(760, 750);
	</s:if>
}

var formName = 'lldpcdpProfiles';
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
	if (validate(thisOperation)) {
		showProcessing();
		document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if ('<%=Navigation.L2_FEATURE_LLDPCDP_PROFILE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	//name
	var profileName = document.getElementById("profileName");
	if (profileName.value.length == 0) {
		hm.util.reportFieldError(profileName, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.name" /></s:param></s:text>');
		profileName.focus();
		return false;
	}

	if(operation == 'create'+'<s:property value="lstForward"/>' || operation=='create') {

		var name = document.getElementById("profileName");
		var message = hm.util.validateName(name.value, '<s:text name="config.lldpcdpprofile.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}

	if (document.getElementById("chkLLDP").checked
			||document.getElementById("chkLLDPHostPorts").checked
			|| document.getElementById("chkLLDPNonHostPorts").checked)
	{
		var lldpMaxEntries = document.getElementById("lldpMaxEntries");
		if (lldpMaxEntries.value.length == 0) {
			hm.util.reportFieldError(lldpMaxEntries, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.lldp.maxEntries" /></s:param></s:text>');
			lldpMaxEntries.focus();
			return false;
		}

	    var message = hm.util.validateIntegerRange(lldpMaxEntries.value, '<s:text name="config.lldpcdpprofile.lldp.maxEntries" />',1,128);
	    if (message != null) {
			hm.util.reportFieldError(lldpMaxEntries, message);
			lldpMaxEntries.focus();
			return false;
	    }

		var lldpHoldTime = document.getElementById("lldpHoldTime");
	
		var lldpTimer = document.getElementById("lldpTimer");
	
		var lldpMaxPower = document.getElementById("lldpMaxPower");
		if (lldpHoldTime.value.length == 0) {
			hm.util.reportFieldError(lldpHoldTime, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.holdTime" /></s:param></s:text>');
			lldpHoldTime.focus();
			return false;
		}

	    var message = hm.util.validateIntegerRange(lldpHoldTime.value, '<s:text name="config.lldpcdpprofile.holdTime" />',0,65535);
	    if (message != null) {
			hm.util.reportFieldError(lldpHoldTime, message);
			lldpHoldTime.focus();
			return false;
	    }

	    if (lldpTimer.value.length == 0) {
			hm.util.reportFieldError(lldpTimer, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.timer" /></s:param></s:text>');
			lldpTimer.focus();
			return false;
		}

	    var message = hm.util.validateIntegerRange(lldpTimer.value, '<s:text name="config.lldpcdpprofile.timer" />',5,65534);
	    if (message != null) {
			hm.util.reportFieldError(lldpTimer, message);
			lldpTimer.focus();
			return false;
	    }
		//validate for aps and brs
	    if(document.getElementById("chkLLDP").checked){
	    	 if (lldpMaxPower.value.length == 0) {
	 			hm.util.reportFieldError(lldpMaxPower, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.maxPower" /></s:param></s:text>');
	 			lldpMaxPower.focus();
	 			return false;
	 		}

	 	    var message = hm.util.validateIntegerRange(lldpMaxPower.value, '<s:text name="config.lldpcdpprofile.maxPower" />',1,250);
	 	    if (message != null) {
	 			hm.util.reportFieldError(lldpMaxPower, message);
	 			lldpMaxPower.focus();
	 			return false;
	 	    }
	    }
	    //validate for switches
		if(document.getElementById("chkLLDPHostPorts").checked
				|| document.getElementById("chkLLDPNonHostPorts").checked){
			var delayTime = Get("delayTime");
		    if (delayTime.value.length == 0) {
				hm.util.reportFieldError(delayTime, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.lldp.reinit.delay" /></s:param></s:text>');
				delayTime.focus();
				return false;
			}

		    var message = hm.util.validateIntegerRange(delayTime.value, '<s:text name="config.switchSettings.lldp.reinit.delay" />',2,5);
		    if (message != null) {
				hm.util.reportFieldError(delayTime, message);
				delayTime.focus();
				return false;
		    }

		    var repeatCount = Get("repeatCount");
		    if (repeatCount.value.length == 0) {
				hm.util.reportFieldError(repeatCount, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.lldp.fast.start.repeatcount" /></s:param></s:text>');
				repeatCount.focus();
				return false;
			}

		    var message = hm.util.validateIntegerRange(repeatCount.value, '<s:text name="config.switchSettings.lldp.fast.start.repeatcount" />',1,10);
		    if (message != null) {
				hm.util.reportFieldError(repeatCount, message);
				repeatCount.focus();
				return false;
		    }
		}
	}

	if (document.getElementById("chkCDP").checked
			|| document.getElementById("chkCDPHostPorts").checked
			|| document.getElementById("chkCDPNonHostPorts").checked)
	{
		var cdpMaxEntries = document.getElementById("cdpMaxEntries");
		if (cdpMaxEntries.value.length == 0) {
			hm.util.reportFieldError(cdpMaxEntries, '<s:text name="error.requiredField"><s:param><s:text name="config.lldpcdpprofile.cdp.maxEntries" /></s:param></s:text>');
			cdpMaxEntries.focus();
			return false;
		}

	    var message = hm.util.validateIntegerRange(cdpMaxEntries.value, '<s:text name="config.lldpcdpprofile.cdp.maxEntries" />',1,128);
	    if (message != null) {
			hm.util.reportFieldError(cdpMaxEntries, message);
			cdpMaxEntries.focus();
			return false;
	    }
	}

    return true;
}

function clickEnableLLDP(checked)
{
	if(checked){
		document.getElementById('chkReceiveOnly').disabled = !checked;
		document.getElementById('lldpMaxEntries').disabled = !checked;
		document.getElementById('lldpHoldTime').disabled = !checked;
		document.getElementById('lldpTimer').disabled = !checked;
		document.getElementById('lldpMaxPower').disabled = !checked;
	}else{
		if(document.getElementById("chkLLDPHostPorts").checked || document.getElementById("chkLLDPNonHostPorts").checked){
			document.getElementById('chkReceiveOnly').disabled = checked;
			document.getElementById('lldpMaxEntries').disabled = checked;
			document.getElementById('lldpHoldTime').disabled = checked;
			document.getElementById('lldpTimer').disabled = checked;
			document.getElementById('lldpMaxPower').disabled = !checked;
			document.getElementById('delayTime').disabled = checked;
			document.getElementById('repeatCount').disabled = checked;
		}else{
			document.getElementById('chkReceiveOnly').disabled = !checked;
			document.getElementById('lldpMaxEntries').disabled = !checked;
			document.getElementById('lldpHoldTime').disabled = !checked;
			document.getElementById('lldpTimer').disabled = !checked;
			document.getElementById('lldpMaxPower').disabled = !checked;
			document.getElementById('delayTime').disabled = !checked;
			document.getElementById('repeatCount').disabled = !checked;
		}
	}
}

function clickEnableLLDPHostPorts(checked)
{
	if(checked){
		document.getElementById('chkReceiveOnly').disabled = !checked;
		document.getElementById('lldpMaxEntries').disabled = !checked;
		document.getElementById('lldpHoldTime').disabled = !checked;
		document.getElementById('lldpTimer').disabled = !checked;
		document.getElementById('delayTime').disabled = !checked;
		document.getElementById('repeatCount').disabled = !checked;
	}else{
		if(document.getElementById("chkLLDP").checked || document.getElementById("chkLLDPNonHostPorts").checked){
			document.getElementById('chkReceiveOnly').disabled = checked;
			document.getElementById('lldpMaxEntries').disabled = checked;
			document.getElementById('lldpHoldTime').disabled = checked;
			document.getElementById('lldpTimer').disabled = checked;
			if(document.getElementById("chkLLDP").checked){
				document.getElementById('lldpMaxPower').disabled = checked;
			}else{
				document.getElementById('lldpMaxPower').disabled = !checked;
			}
			if(document.getElementById("chkLLDPNonHostPorts").checked){
				document.getElementById('delayTime').disabled = checked;
				document.getElementById('repeatCount').disabled = checked;
			}else{
				document.getElementById('delayTime').disabled = !checked;
				document.getElementById('repeatCount').disabled = !checked;
			}
		}else{
			document.getElementById('chkReceiveOnly').disabled = !checked;
			document.getElementById('lldpMaxEntries').disabled = !checked;
			document.getElementById('lldpHoldTime').disabled = !checked;
			document.getElementById('lldpTimer').disabled = !checked;
			document.getElementById('lldpMaxPower').disabled = !checked;
			document.getElementById('delayTime').disabled = !checked;
			document.getElementById('repeatCount').disabled = !checked;
		}
	}
}

function clickEnableLLDPNonHostPorts(checked)
{
	if(checked){
		document.getElementById('chkReceiveOnly').disabled = !checked;
		document.getElementById('lldpMaxEntries').disabled = !checked;
		document.getElementById('lldpHoldTime').disabled = !checked;
		document.getElementById('lldpTimer').disabled = !checked;
		document.getElementById('delayTime').disabled = !checked;
		document.getElementById('repeatCount').disabled = !checked;
	}else{
		if(document.getElementById("chkLLDP").checked || document.getElementById("chkLLDPHostPorts").checked){
			document.getElementById('chkReceiveOnly').disabled = checked;
			document.getElementById('lldpMaxEntries').disabled = checked;
			document.getElementById('lldpHoldTime').disabled = checked;
			document.getElementById('lldpTimer').disabled = checked;
			if(document.getElementById("chkLLDP").checked){
				document.getElementById('lldpMaxPower').disabled = checked;
			}else{
				document.getElementById('lldpMaxPower').disabled = !checked;
			}
			if(document.getElementById("chkLLDPHostPorts").checked){
				document.getElementById('delayTime').disabled = checked;
				document.getElementById('repeatCount').disabled = checked;
			}else{
				document.getElementById('delayTime').disabled = !checked;
				document.getElementById('repeatCount').disabled = !checked;
			}
		}else{
			document.getElementById('chkReceiveOnly').disabled = !checked;
			document.getElementById('lldpMaxEntries').disabled = !checked;
			document.getElementById('lldpHoldTime').disabled = !checked;
			document.getElementById('lldpTimer').disabled = !checked;
			document.getElementById('lldpMaxPower').disabled = !checked;
			document.getElementById('delayTime').disabled = !checked;
			document.getElementById('repeatCount').disabled = !checked;
		}
	}
}


function clickEnableCDP(checked)
{
	if(checked){
		document.getElementById('cdpMaxEntries').disabled = !checked; 
	}else{
		if(document.getElementById("chkCDPHostPorts").checked || document.getElementById("chkCDPNonHostPorts").checked){
			document.getElementById('cdpMaxEntries').disabled = checked;
		}else{
			document.getElementById('cdpMaxEntries').disabled = !checked;
		}
	}
}

function clickEnableCDPHostPorts(checked)
{
	if(checked){
		document.getElementById('cdpMaxEntries').disabled = !checked; 
	}else{
		if(document.getElementById("chkCDP").checked || document.getElementById("chkCDPNonHostPorts").checked){
			document.getElementById('cdpMaxEntries').disabled = checked;
		}else{
			document.getElementById('cdpMaxEntries').disabled = !checked;
		}
	}
}

function clickEnableCDPNonHostPorts(checked)
{
	if(checked){
		document.getElementById('cdpMaxEntries').disabled = !checked; 
	}else{
		if(document.getElementById("chkCDP").checked || document.getElementById("chkCDPHostPorts").checked){
			document.getElementById('cdpMaxEntries').disabled = checked;
		}else{
			document.getElementById('cdpMaxEntries').disabled = !checked;
		}
	}
}

function saveLLdpPro(operation) {
	if (validate(operation)) {
		url = "<s:url action='lldpcdpProfiles' includeParams='none' />" + "?jsonMode=true"
				+ "&ignore=" + new Date().getTime();
		if (operation == 'create') {
			//
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["lldpcdpProfiles"]);
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="lldpcdpProfiles" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="displayName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

</script>

<div id="content">
	<s:form action="lldpcdpProfiles">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<div id="vlanTitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-LLDP_CDP_Profiles.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.lldp.dialog.new.title"/>
								</s:if>
								<s:else>
									<s:text name="config.lldp.dialog.edit.title"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveLLdpPro('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveLLdpPro('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>

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
										onClick="submitAction('<%=Navigation.L2_FEATURE_LLDPCDP_PROFILE%>');">
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
				<td>
					<table cellspacing="0" cellpadding="0" border="0" class="editBox"
						width="650px">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td colspan="2">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" width="150">
											<s:text name="config.lldpcdpprofile.name" /><font color="red"><s:text name="*" /> </font>
										</td>
										<td>
											<s:textfield id="profileName" name="dataSource.profileName"
												size="24"
												onkeypress="return hm.util.keyPressPermit(event,'name');"
												maxlength="32" disabled="%{disabledName}" />
											<s:text name="config.lldpcdpprofile.name.range" />
										</td>
									</tr>
									<tr>
										<td class="labelT1">
											<s:text name="config.lldpcdpprofile.description" />
										</td>
										<td>
											<s:textfield name="dataSource.description" size="48"
												maxlength="64" />
											<s:text name="config.lldpcdpprofile.description.range" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2">
								<s:checkbox name="dataSource.enableLLDP" id="chkLLDP"
									value="%{dataSource.enableLLDP}"
									onclick="clickEnableLLDP(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableLLDP" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="280px;">
								<s:checkbox name="dataSource.enableLLDPHostPorts" id="chkLLDPHostPorts"
									value="%{dataSource.enableLLDPHostPorts}"
									onclick="clickEnableLLDPHostPorts(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableLLDPHostPorts" />
							</td>
							<td class="labelT1">
								<s:checkbox name="dataSource.enableLLDPNonHostPorts" id="chkLLDPNonHostPorts"
									value="%{dataSource.enableLLDPNonHostPorts}"
									onclick="clickEnableLLDPNonHostPorts(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableLLDPNonHostPorts" />
							</td>
						</tr>
						<tr>
							<td style="padding-left: 15px;" colspan="2">
								<fieldset style="width: 550px">
									<legend>
										<s:text name="config.lldpcdpprofile.LLDPSetting" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="4"></td>
											</tr>
											<tr>
												<td colspan="2" class="noteInfo" style="padding-left: 10px;">
													<s:text name="config.lldpcdpprofile.LLDPSetting.note"></s:text>
												</td>
											</tr>
											<tr id="chkReceiveOnlyTr">
												<td class="labelT1" colspan="2" style="padding-left:8px;">
													<%-- <s:checkbox name="dataSource.lldpReceiveOnly" disabled="%{lldpCommonFilesStatus}"
														id="chkReceiveOnly" value="%{dataSource.lldpReceiveOnly}" />
													<s:text name="config.lldpcdpprofile.receiveOnly" /> --%>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck">
																<s:checkbox name="dataSource.lldpReceiveOnly" disabled="%{lldpCommonFilesStatus}"
																	id="chkReceiveOnly" value="%{dataSource.lldpReceiveOnly}" />
															</td>
															<td class="labelT1" style="padding-left: 0"><s:text name="config.lldpcdpprofile.receiveOnly" /></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="300px">
													<s:text name="config.lldpcdpprofile.lldp.maxEntries" />
												</td>
												<td>
													<s:textfield name="dataSource.lldpMaxEntries"
														id="lldpMaxEntries" size="8" maxlength="3"
														disabled="%{lldpCommonFilesStatus}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.lldpcdpprofile.maxEntries.range" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="config.lldpcdpprofile.holdTime" />
												</td>
												<%-- <td id="nonSwitchHoldTimeTd" style="display: <s:property value="adaptedNonSwitchStyle"/>">
													<s:textfield name="dataSource.lldpHoldTime" disabled="%{nonSwitchDisabled}"
														id="lldpHoldTime" size="8" maxlength="3"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text  name="config.lldpcdpprofile.holdTime.range" />
												</td> --%>
												<td id="switchHoldTimeTd">
													<s:textfield name="dataSource.lldpHoldTime"
														id="lldpHoldTime" size="8" maxlength="5"
														disabled="%{lldpCommonFilesStatus}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.switchSettings.lldp.hold.time.range" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="config.lldpcdpprofile.timer" />
												</td>
												<%-- <td id="nonSwitchTimeTd" style="display: <s:property value="adaptedNonSwitchStyle"/>">
													<s:textfield name="dataSource.lldpTimer" id="lldpTimer"
														size="8" maxlength="3" disabled="%{nonSwitchDisabled}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text  name="config.lldpcdpprofile.timer.range" />
												</td> --%>
												<td>
													<s:textfield name="dataSource.lldpTimer" id="lldpTimer"
														size="8" maxlength="5" disabled="%{lldpCommonFilesStatus}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text  name="config.switchSettings.lldp.update.time.range" />
												</td>
											</tr>
											<tr id="lldpMaxPowerTr">
												<td class="labelT1">
													<s:text name="config.lldpcdpprofile.maxPower" />
												</td>
												<td>
													<s:textfield name="dataSource.lldpMaxPower" id="lldpMaxPower"
														size="8" maxlength="3" disabled="%{lldpApBrOnlyStatue}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.lldpcdpprofile.maxPower.range" />
												</td>
											</tr>
											<tr id="lldpMaxPowerNoteTr" style="display: <s:property value="adaptedNonSwitchStyle"/>">
												<td class="labelT1">
												</td>
												<td>
													<s:text name="config.lldpcdpprofile.maxPower.note" />
												</td>
											</tr>
											<!--Added from Chesapeake  -->
											<tr id="delayTimeTr">
												<td class="labelT1">
													<s:text name="config.switchSettings.lldp.reinit.delay" />
												</td>
												<td>
													<s:textfield name="dataSource.delayTime" id="delayTime"
														size="8" maxlength="1" disabled="%{lldpSwitchOnlyStatue}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.switchSettings.lldp.reinit.delay.range" />
												</td>
											</tr>
											<tr id="repeatcountTr">
												<td class="labelT1">
													<s:text name="config.switchSettings.lldp.fast.start.repeatcount" />
												</td>
												<td>
													<s:textfield name="dataSource.repeatCount" id="repeatCount"
														size="8" maxlength="2" disabled="%{lldpSwitchOnlyStatue}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.switchSettings.lldp.fast.start.repeatcount.range" />
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2">
								<s:checkbox name="dataSource.enableCDP" id="chkCDP"
									value="%{dataSource.enableCDP}"
									onclick="clickEnableCDP(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableCDP" />
							</td>
						</tr>
						<tr>
							<td class="labelT1">
								<s:checkbox name="dataSource.enableCDPHostPorts" id="chkCDPHostPorts"
									value="%{dataSource.enableCDPHostPorts}"
									onclick="clickEnableCDPHostPorts(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableCDPHostPorts" />
							</td>
							<td class="labelT1">
								<s:checkbox name="dataSource.enableCDPNonHostPorts" id="chkCDPNonHostPorts"
									value="%{dataSource.enableCDPNonHostPorts}"
									onclick="clickEnableCDPNonHostPorts(this.checked);" />
								<s:text name="config.lldpcdpprofile.enableCDPNoneHostPorts" />
							</td>
						</tr>
						<tr>
							<td style="padding-left: 15px;" colspan="2">
								<fieldset style="width: 500px">
									<legend>
										<s:text name="config.lldpcdpprofile.CDPSetting" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" width="300px">
													<s:text name="config.lldpcdpprofile.cdp.maxEntries" />
												</td>
												<td>
													<s:textfield name="dataSource.cdpMaxEntries"
														id="cdpMaxEntries" size="8" maxlength="3"
														disabled="%{cdpMaxEntriesStatus}"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													<s:text name="config.lldpcdpprofile.maxEntries.range" />
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
