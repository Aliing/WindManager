<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.be.common.NmsUtil"%>

<script>
var formName = 'vhmManagement';
var thisOperation;
var disableVHM;
var sysName = '<s:property value="%{systemNmsName}"/>';
var isHmol = <%=NmsUtil.isHostedHMApplication()%>;

function onLoadPage() 
{
	disableVHM = document.getElementById("disableVHM").checked;
	
	var defaultUserSection = document.getElementById('defaultUserSection');
	var defaultUserGroup = document.getElementById('defaultUserGroup');
	var foldingLabel = document.getElementById('foldingLabel');
	var disableVHMSection = document.getElementById('disableVHMSection');
	<s:if test="%{dataSource.id == null}">
		disableVHMSection.style.display = "none";
	</s:if>
	<s:else>
		defaultUserSection.style.display = "none";
		
		if (document.getElementById('supportFullMode').checked)
		{
			document.getElementById('supportFullModeDiv').style.display = "none";
		}
	</s:else>
	
	<s:if test="%{!GMDisplay}">
		document.getElementById('gmTD').style.display="none";
	</s:if>
	
	if (isHmol) {
		defaultUserGroup.style.display = "none";
		foldingLabel.style.display = "none";
	}
}


function submitAction(operation) 
{
    thisOperation = operation;
    if (!validate(operation)) 
	{
		return;
	}
    
    if (operation == 'create') 
    {
		if (isHmol && <s:property value="%{radiuLoginAuth}" />)
	   	{
	   		warnDialog.cfg.setProperty('text', '<s:text name="administrate.vhm.create.auth.mode" />');
			warnDialog.show();
			return;
	   	}
	   	
	   	if (isHmol && <s:property value="%{theFirstDomainCreate}" />)
	   	{
	   		confirmDialog.cfg.setProperty('text', "<html><body>This operation will disable RADIUS authentication for " + sysName + " administrators.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	   		confirmDialog.show();
	   	}
	   	else
	   	{
	   		doContinueOper();
	   	}
    } 
    else
    	 {
    	 	doContinueOper();
    	 }
}

function doContinueOper() 
{
    showProcessing();
	
	//save style values
	Get(formName + "_dataSource_defaultUserGroupStyle").value = Get("defaultUserGroupSection").style.display;
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function validate(operation) {
	
	if (operation != 'create' && operation!='update')
	{
		return true;
	}

	var vhmName = document.getElementById("vhmName");
	if(!vhmName.disabled){
	    var message = hm.util.validateDirectoryName(vhmName.value, '<s:text name="admin.vhmMgr.vhmName" />');
		if (message != null) {
		    hm.util.reportFieldError(vhmName, message);
		    vhmName.focus();
		    return false;
		}
	}
	
	if (<%=NmsUtil.isHostedHMApplication()%>) {
		var vhmID = document.getElementById("vhmID");
		if (vhmID.value.length == 0) 
		{
	        hm.util.reportFieldError(vhmID, '<s:text name="error.requiredField"><s:param><s:text name="admin.vhmMgr.vhmID" /></s:param></s:text>');
			if (!vhmID.disabled) {vhmID.focus();}
	        return false;
	    }
	    if (vhmID.value.length < 6) 
		{
	        hm.util.reportFieldError(vhmID, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.vhmMgr.vhmID" /></s:param><s:param><s:text name="admin.vhmMgr.vhmID.range" /></s:param></s:text>');
			if (!vhmID.disabled) {vhmID.focus();}
			return false;
	    }
	} else {
		var minAPNum = <s:property value="%{minAPNum}" />;    
		var remainingAPNum = <s:property value="%{remainingAPNum}" />;
		var maxAp = document.getElementById("vhmMaxAP");
		if (maxAp.value<minAPNum || maxAp.value>remainingAPNum)
		{
			hm.util.reportFieldError(maxAp, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.vhmMgr.maxAPNum" /></s:param><s:param><s:property value="%{minAPNum}" />-<s:property value="%{remainingAPNum}" /></s:param></s:text>');
			maxAp.focus();
			return false;
		}
		
		// check group attribute
		if (!checkAllGroupAttribute()) {
			return false;
		}
	}
	
	// check max simulated ap number and client number
	var simuAp = document.getElementById(formName + "_dataSource_maxSimuAp");
	var message = hm.util.validateIntegerRange(simuAp.value, '<s:text name="admin.vhmMgr.maxSimulatedAPNum" />', 1, parseInt('<s:text name="admin.vhmMgr.maxSimulatedAPNum.maxValue" />'));
    if (message != null) {
        hm.util.reportFieldError(simuAp, message);
        simuAp.focus();
        return false;
    }
    
    var simuClient = document.getElementById(formName + "_dataSource_maxSimuClient");
    message = hm.util.validateIntegerRange(simuClient.value, '<s:text name="admin.vhmMgr.maxSimulatedClientNum" />', 1, parseInt('<s:text name="admin.vhmMgr.maxSimulatedClientNum.maxValue" />'));
    if (message != null) {
        hm.util.reportFieldError(simuClient, message);
        simuClient.focus();
        return false;
    }

	<s:if test="%{dataSource.id == null}">
		var userName = document.getElementById("userName");
		if (userName.value.length == 0) 
		{
	        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.vhmMgr.userName" /></s:param></s:text>');
	        userName.focus();
	        return false;
	    }
	    if (userName.value.indexOf(' ') > -1) {
	        hm.util.reportFieldError(userName, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.vhmMgr.userName" /></s:param></s:text>');
	        userName.focus();
	        return false;
		}
		
		var password;
		var passwordConfirm;
		if (document.getElementById("chkToggleDisplay").checked)
		{
			password = document.getElementById("adminPassword");
			passwordConfirm = document.getElementById("passwordConfirm");
		}
		else
		{
			password = document.getElementById("adminPassword_text");
			passwordConfirm = document.getElementById("passwordConfirm_text");
		}
		
		if (!hm.util.validateUserNewPasswordFormat(password, passwordConfirm, '<s:text name="admin.user.password.new" />',
    			'<s:text name="admin.user.password.confirm" />', 8, '<s:text name="admin.vhmMgr.passwordRange" />', userName.value)) {
			return false;
		}
	    
	    var emailAddr = document.getElementById('userEmailAddr');
	    if (emailAddr.value.length == 0) 
		{
	        hm.util.reportFieldError(emailAddr, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
	        emailAddr.focus();
	        return false;
	    }
	    else if (!hm.util.validateEmail(emailAddr.value))
		{
			hm.util.reportFieldError(emailAddr, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
			emailAddr.focus();
			return false;
		}
	</s:if>
		
	return true;
}

function checkAllGroupAttribute() {
	
	var ids = new Array("monitoringId", "monitoringConfigId", "rfPlanningId", "userMngAdminId", "userMngOperatorId", "teacherId");
	var names = new Array('<s:text name="admin.vhmmgr.groupid.monitoring" />', '<s:text name="admin.vhmmgr.groupid.configuration.monitoring" />', '<s:text name="admin.vhmmgr.groupid.rf.planning" />','<s:text name="admin.vhmmgr.groupid.usermanager.admin" />','<s:text name="admin.vhmmgr.groupid.usermanager.operator" />', '<s:text name="admin.vhmmgr.groupid.teacher" />');
	var attributes = new Array();
	var el;
	var result = true;
	
	// check empty and range
	for (var i = 0; i < ids.length; i++) {
		el = document.getElementById(ids[i]);
		attributes.push(el);
		//if (!checkOneAttribute(el, names[i])) {
		if (!checkOneAttribute(el, "Group Attribute")) {
			result = false;
		}
	}
	
	// one duplicate with the other
	if (result) {
		for (var i = 0; i < attributes.length-1; i++) {
			for (var j = i + 1; j < attributes.length; j++) {
				if (attributes[i].value == attributes[j].value) {
					showError(attributes[i], '<s:text name="error.config.vhmManagement.groupIdDuplicate"><s:param>' + names[j] + '</s:param></s:text>')
					result = false;
					break;
				}
			}
			
			if (!result) {
				break;
			}
		}
	}
	
	return result;
}

function checkOneAttribute(el, attributeName) {
	if (el.value.length == 0) {
        showError(el, '<s:text name="error.requiredField"><s:param>' + attributeName + '</s:param></s:text>');
        return false;
    }
    var message = hm.util.validateIntegerRange(el.value, attributeName,10, 65535);
    if (message != null) {
        showError(el, message);
        return false;
    }
    return true;
}

function showError(el, msg) {
    hm.util.reportFieldError(el, msg);
    el.focus();
}

function vhmIDKeyPress(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}

	if((48 <= keycode && keycode <=57) || (65 <= keycode && keycode <=90)
				|| (97 <= keycode && keycode <=122))
	{
		return true;
	}
	
	return false;
}

function inputVHMID(value)
{
	document.getElementById('vhmID').value = value.toUpperCase();
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="vhmManagement" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="displayName" />\'</td>');
	</s:else>
}
</script>

<div id="content">
	<s:form action="vhmManagement">
	<s:hidden name="dataSource.defaultUserGroupStyle"></s:hidden>
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
										onClick="submitAction('update');"
										<s:property value="updateDisabled" />>
								</td>
							</s:else>
							<s:if test="%{lstForward == null || lstForward == ''}">
								<td>
									<input type="button" name="cancel" value="Cancel"
										class="button"
										onClick="submitAction('<%=Navigation.L2_FEATURE_VHMMANAGEMENT%>');">
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
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="650px">
						<tr>
							<td height="10">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="198">
								<label>
									<s:text name="admin.vhmMgr.vhmName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="vhmName" name="dataSource.domainName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'directory');"
									maxlength="%{domainNameLength}" disabled="%{disabledName}" />
								<s:text name="admin.vhmMgr.name.range" />
							</td>
						</tr>
						<tr style="display:<s:property value="%{hide4VHMID}"/>">
							<td class="labelT1" width="198">
								<label>
									<s:text name="admin.vhmMgr.vhmID" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:text name="admin.vhmMgr.vhmIDPrefix" />
								<s:textfield id="vhmID" name="vhmID" size="17"
									onkeypress="return vhmIDKeyPress(event);"
									onkeyup="inputVHMID(this.value)" onchange="inputVHMID(this.value)"
									maxlength="6" disabled="%{disabledName}" />
								<s:text name="admin.vhmMgr.vhmID.range" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" nowrap>
								<label>
									<s:text name="admin.vhmMgr.maxAPNum" /><font color="red"><s:text name="*" />&nbsp;&nbsp;</font>
								</label>
							</td>
							<td>
								<s:textfield id="vhmMaxAP" name="dataSource.maxApNum" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="%{vhmMaxAPNumLength}" disabled="%{disabledVHMIP}" />
								(<s:property value="%{minAPNum}" />-<s:property value="%{remainingAPNum}" />)
							</td>
						</tr>
						<tr>
							<td class="labelT1" nowrap>
								<label>
									<s:text name="admin.vhmMgr.maxSimulatedAPNum" /><font color="red"><s:text name="*" />&nbsp;&nbsp;</font>
								</label>
							</td>
							<td>
								<s:textfield name="dataSource.maxSimuAp" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="%{maxSimuApNumLength}" />
								(1-<s:text name="admin.vhmMgr.maxSimulatedAPNum.maxValue" />)
							</td>
						</tr>
						<tr>
							<td class="labelT1" nowrap>
								<label>
									<s:text name="admin.vhmMgr.maxSimulatedClientNum" /><font color="red"><s:text name="*" />&nbsp;&nbsp;</font>
								</label>
							</td>
							<td>
								<s:textfield name="dataSource.maxSimuClient" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="%{maxSimuClientNumLength}" />
								(1-<s:text name="admin.vhmMgr.maxSimulatedClientNum.maxValue" />)
							</td>
						</tr>
						<tr>
							<td class="labelT1">
								<label>
									<s:text name="admin.vhmMgr.description" />
								</label>
							</td>
							<td>
								<s:textfield id="comment" name="dataSource.comment" size="35" maxlength="64" />
								<s:text name="admin.vhmMgr.description.range" />
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding-left: 5px;" id="gmTD">
								<s:checkbox id="supportGM" name="dataSource.supportGM" />
								<label>
									<s:text name="admin.vhmMgr.userMgr" />
								</label>
							</td>
						</tr>
						<tr style="display:<s:property value="%{hide4OemSystem}"/>" id="supportFullModeDiv">
							<td colspan="2" style="padding-left: 5px;">
								<s:checkbox id="supportFullMode" name="dataSource.supportFullMode" />
								<label>
									<s:text name="admin.vhmMgr.fullMode" />
								</label>
							</td>
						</tr>
						<tr>
							<td style="padding-left: 10px;" colspan="2"
								id="defaultUserSection">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="5"></td>
									</tr>
									<tr>
										<td>
											<fieldset style="width: 650px">
												<legend>
													<s:text name="admin.vhmMgr.defaultUser" />
												</legend>
												<div>
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td height="5"></td>
														</tr>
														<tr>
															<td class="labelT1">
																<label>
																	<s:text name="admin.user.emailAddress" /><font color="red"><s:text name="*" /> </font>
																</label>
															</td>
															<td>
																<s:textfield id="userEmailAddr" name="userEmailAddr" maxlength="128" size="40" />
																<s:text name="admin.email.address.range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1" width="160px">
																<label>
																	<s:text name="admin.vhmMgr.userName" /><font color="red"><s:text name="*" /> </font>
																</label>
															</td>
															<td>
																<s:textfield id="userName" name="userName" size="40"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"
																	maxlength="128" />
																<s:text name="admin.email.address.range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1">
																<label>
																	<s:text name="admin.vhmMgr.password" /><font color="red"><s:text name="*" /> </font>
																</label>
															</td>
															<td>
																<s:password id="adminPassword" name="adminPassword"
																	size="24" maxlength="32" />
																<s:textfield id="adminPassword_text"
																	name="adminPassword" disabled="true" size="24"
																	maxlength="32" cssStyle="display:none" />
																<s:text name="admin.vhmMgr.passwordRange" />
															</td>
														</tr>
														<tr>
															<td class="labelT1">
																<label>
																	<s:text name="admin.user.password.confirm" /><font color="red"><s:text name="*" /> </font>
																</label>
															</td>
															<td>
																<s:password id="passwordConfirm" name="passwordConfirm"
																	size="24" maxlength="32" />
																<s:textfield id="passwordConfirm_text"
																	name="passwordConfirm" disabled="true" size="24"
																	maxlength="32" cssStyle="display:none" />
																<s:text name="admin.vhmMgr.passwordRange" />
															</td>
														</tr>
														<tr>
															<td>
																&nbsp;
															</td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggleDisplay" name="ignore"
																				value="true" disabled="%{writeDisable4Struts}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','passwordConfirm'],['adminPassword_text','passwordConfirm_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td height="5"></td>
														</tr>
													</table>
												</div>
											</fieldset>
										</td>
									</tr>
									<tr>
										<td height="5"></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td style="padding-left: 5px;" colspan="2" id="disableVHMSection">
								<s:checkbox name="disableVHM" id="disableVHM" />
								<label>
									<s:text name="admin.vhmMgr.disableVHM" />
								</label>
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr id="foldingLabel">
							<td style="padding-left: 10px;" colspan="2"><script type="text/javascript">insertFoldingLabelContext('<s:text name="admin.vhmMgr.groupid.defaultUserGroup" />','defaultUserGroupSection');</script></td>
						</tr>
						<tr id="defaultUserGroup">
							<td style="padding-left: 10px;" colspan="2" >
								<div id="defaultUserGroupSection" style="display:<s:property value="%{dataSource.defaultUserGroupStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="5"></td>
									</tr>
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.vhmmgr.groupid.monitoring" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="monitoringId" name="dataSource.monitoringId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="175px">
														<label>
															<s:text name="admin.vhmmgr.groupid.configuration.monitoring" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="monitoringConfigId" name="dataSource.monitoringConfigId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												<s:if test="%{dataSource.domainName != 'home'}">
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.vhmmgr.groupid.rf.planning" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="rfPlanningId" name="dataSource.rfPlanningId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												</s:if>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.vhmmgr.groupid.usermanager.admin" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="userMngAdminId" name="dataSource.userMngAdminId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.vhmmgr.groupid.usermanager.operator" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="userMngOperatorId" name="dataSource.userMngOperatorId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.vhmmgr.groupid.teacher" /><font color="red"><s:text name="*" /> </font>
														</label>
													</td>
													<td>
														<s:textfield id="teacherId" name="dataSource.teacherId" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="5" size="32" /><s:if test="%{dataSource.id == null || updateDisabled != 'disabled'}">
														<s:text name="admin.usergroup.attributeRange" /></s:if>
													</td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="5"></td>
									</tr>
								</table>
								</div>
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
