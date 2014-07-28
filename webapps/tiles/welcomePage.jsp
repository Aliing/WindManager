<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.be.parameter.BeParaModule"%>

<script>
var formName = 'startHere';
var infoDialog;
var initialTimeZone;

function onLoadEvent() {
	onLoadNotes();
	initialTimeZone = '<s:property value="%{timezone}"/>';
}
function onUnloadEvent() {
	onUnloadNotes();
}
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}
function validate(operation) {
	if ('' == '<s:property value="%{showUpConfig}"/>' && 'continue' == operation) {
		// express mode
		<s:if test="%{!displayEnterprise}">
			// Hive Name
			var networkEl = document.getElementById(formName + "_networkName");
			if(networkEl.value.length > 0){
				var message = hm.util.validateName(networkEl.value, '<s:text name="hm.config.start.network" />');
		    	if (message != null) {
		    		hm.util.reportFieldError(networkEl, message);
		    		networkEl.focus();
		        	return false;
		    	}
		    	if ("<%=BeParaModule.DEFAULT_HIVEID_PROFILE_NAME%>" == networkEl.value || "<%=BeParaModule.DEFAULT_DEVICE_GROUP_NAME%>"
		    	    == networkEl.value || "<%=BeParaModule.DEFAULT_SERVICE_ALG_NAME%>" == networkEl.value) {
		    		hm.util.reportFieldError(networkEl, '<s:text name="error.objectExists"><s:param>'+networkEl.value+'</s:param></s:text>');
		    		networkEl.focus();
		        	return false;
		    	}
			} else {
				hm.util.reportFieldError(networkEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.config.start.network" /></s:param></s:text>');
				networkEl.focus();
		    	return false;
			}
			
			// select express mode
			if (document.getElementById("quickStartPwdTr").style.display == "none") {
				// HiveManager Password
				//return validateHivemanagerPassword();
				if(!validateHivemanagerPassword()){
					return false;
				}
			} else {
				if (!validateHivemanagerPassword()) {
					return false;
				}
				// quick start Password
				if(!validateQuickStartPassword()){
					return false;
				}
			}
		// enterprise mode
		</s:if>
		<s:else>
			if (!validateHiveApPassword()) {
				return false;
			}
			// HiveManager Password
			if(!validateHivemanagerPassword()){
				return false;
			}
		</s:else>
		// validate if timezone is changed
		if(!validateTimeZoneChange(operation)){
			return false;
		}
	}
	return true;
}

function validateTimeZoneChange(operation){
	var isInHomeDomain = <s:property value="isInHomeDomain" />;
 	if (isInHomeDomain && 'continue' == operation){
		if(initialTimeZone != Get(formName+"_timezone").value){
			openTimeZoneConfirmDlg();
			return false;
		}else{
			document.forms[formName].rebootFlag.value = false;
		}
   	}else{
   		document.forms[formName].rebootFlag.value = true;
   	}
 	return true;
}

function saveTimeZoneConfig(operation){
	var timeZoneReboot =document.getElementById(formName+"_timeZoneReboot1");
	if(timeZoneReboot.checked){
		var url = "<s:url action='startHere' includeParams='none'/>" +"?ignore=" + new Date().getTime();
		document.forms[formName].operation.value = operation;
		document.forms[formName].rebootFlag.value = true;
		YAHOO.util.Connect.setForm(document.getElementById(formName));
		hm.util.show("processing");
		Get("rebootOk").disabled = true;
		Get("rebootCancel").disabled = true;
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:updateDateTimeResult,timeout: 120000}, null);
	}else{
		document.forms[formName].rebootFlag.value = false;
		document.forms[formName].operation.value = operation;
		Get("rebootOk").disabled = true;
		Get("rebootCancel").disabled = true;
		document.forms[formName].submit();
	}
}

function updateDateTimeResult(o)
{
	eval("var details = " + o.responseText);
	if(details.succ){
		if (details.restart)
		{
			Get("showMessage").innerHTML = details.message;
			setTimeout("hideMessage()", 5 * 12000);
			url = "<s:url action='startHere' includeParams='none'/>?operation=restartHM&ignore=" + new Date().getTime();
			YAHOO.util.Connect.asyncRequest('POST', url, {}, null);
		}
	}else{
		Get("showMessage").innerHTML = details.message;
		setTimeout("hideMessage()", 5 * 2000);
	}
}

function hideMessage()
{
	hm.util.hide('showMessage');
}

function validateHivemanagerPassword(){
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay1").checked){
       passwordElement = document.getElementById("adminPassword");
       confirmElement = document.getElementById("cfAdminPassword");
    }else{
       passwordElement = document.getElementById("adminPassword_text");
       confirmElement = document.getElementById("cfAdminPassword_text");
    }
    var limitLength = <s:property value="%{!displayEnterprise}"/> ? 8 : 1;
    if(<s:property value="%{!displayEnterprise}"/> || (passwordElement.value.length > 0
    	|| confirmElement.value.length > 0)){
    	return hm.util.validateOptionNewPasswordLength(passwordElement, confirmElement, '<s:text name="hm.config.start.hivemanager.password" />',
    			'<s:text name="hm.config.start.hivemanager.password.confirm.hivemanager" />', limitLength, '<s:text name="hm.config.start.hivemanager.password.note" />');
    }
    return true;
}

function validateHiveApPassword(){
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay").checked){
       passwordElement = document.getElementById("hiveApPassword");
       confirmElement = document.getElementById("cfHiveApPassword");
    }else{
       passwordElement = document.getElementById("hiveApPassword_text");
       confirmElement = document.getElementById("cfHiveApPassword_text");
    }
    if(passwordElement.value.length > 0 || confirmElement.value.length > 0){
       	return hm.util.validateOptionNewPasswordLength(passwordElement, confirmElement, '<s:text name="hm.config.start.hiveAp.password" />',
       			'<s:text name="hm.config.start.hiveAp.password.confirm" />', 5, '<s:text name="hm.config.start.hiveAp.password.note" />');
    }
    return true;
}

function validateQuickStartPassword(){
	if (document.getElementById("quickStartPwdTr").style.display == "none" || !Get("chkUpdatePresharedKey").checked) {
		document.getElementById("quickPassword").value = "";
	    document.getElementById("cfQuickPassword").value = "";
	    document.getElementById("quickPassword_text").value = "";
	    document.getElementById("cfQuickPassword_text").value = "";
		return true;
	}
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay2").checked){
       passwordElement = document.getElementById("quickPassword");
       confirmElement = document.getElementById("cfQuickPassword");
    }else{
       passwordElement = document.getElementById("quickPassword_text");
       confirmElement = document.getElementById("cfQuickPassword_text");
    }
    return hm.util.validateOptionNewPasswordLength(passwordElement, confirmElement, '<s:text name="hm.missionux.wecomle.update.preshared.key.title.default" />',
    			'<s:text name="hm.config.start.hivemanager.password.confirm.quickstart" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />');
}

function hideMessageInSession() {
	if ('' == '<s:property value="%{showUpConfig}"/>') {
		submitAction("startHere");
	} else {
		submitAction("continueKey");
	}
}

function showUpQuickStartPwd(flag) {
	document.getElementById("quickStartPwdTr").style.display = flag ? "" : "none";
}

function showInfoDialog(info)
{
	if (infoDialog == null)
	{
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "350px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon: YAHOO.widget.SimpleDialog.ICON_INFO,
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]});
		infoDialog.setHeader("Information");
		infoDialog.render(document.body);
	}
	
	infoDialog.cfg.setProperty("text",info);
	infoDialog.show();
}

var timeZoneConfirmDlg = null;
YAHOO.util.Event.onDOMReady(initTimeZoneConfirmDlg);
function initTimeZoneConfirmDlg() {
// create Dialog overlay
	var div = document.getElementById('timeZoneConfirmDlgId');
	timeZoneConfirmDlg = new YAHOO.widget.Panel(div, {
		width:"400px",
		visible:false,
		fixedcenter:false,
		close: false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		underlay: "none",
		zIndex:1
		});
	timeZoneConfirmDlg.render(document.body);
	div.style.display = "";
}
function openTimeZoneConfirmDlg(){
	if(null != timeZoneConfirmDlg){
		timeZoneConfirmDlg.center();
		timeZoneConfirmDlg.cfg.setProperty('visible', true);
	}
}

function hideTimeZoneConfirmDlg(){
	if(null != timeZoneConfirmDlg){
		timeZoneConfirmDlg.cfg.setProperty('visible', false);
	}
}

function checkToUpdatePresharedKey(el) {
	if (el.checked) {
		Get("update_preshared_key_section").style.display = "block";
		var quickPwd = Get("quickPassword").value;
		if (!Get("chkToggleDisplay2").checked) {
			quickPwd = Get("quickPassword_text").value;
		}
		if (quickPwd == null || quickPwd == "") {
			var hmPwd = Get("adminPassword").value;
			if (!Get("chkToggleDisplay1").checked) {
				hmPwd = Get("adminPassword_text").value;
			}
			Get("quickPassword").value = hmPwd;
			Get("quickPassword_text").value = hmPwd;
			Get("cfQuickPassword").value = hmPwd;
			Get("cfQuickPassword_text").value = hmPwd;
		}
	} else {
		Get("update_preshared_key_section").style.display = "none";
	}
};
</script>

<s:form action="startHere">
	<s:hidden name="operation" />
	<s:hidden name="rebootFlag" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="120" alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td style="padding: 4px 0 0 4px" align="center" valign="middle">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="10">
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					</td>
				</tr>
				<tr>
					<td align="left" valign="top" style="padding: 0 10px 0 10px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr height="5">
						</tr>
						<tr>
							<td class="crumb" colspan="2"><s:text name="feature.welcome.page.title"></s:text></td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr style="display:<s:property value="%{showUpConfig}"/>">
							<td><tiles:insertDefinition name="notes" /></td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr style="display:<s:property value="%{showUpConfig}"/>">
							<td>
								<table>
									<s:if test="%{!displayEnterprise}">
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="240px">
																	<label>
																		<s:text name="hm.config.start.network" /><font color="red">*</font>
																	</label>
																</td>
																<td>
																	<s:textfield name="networkName" size="46" maxlength="32"
																		 onkeypress="return hm.util.keyPressPermit(event,'name');"/>
																	<s:text name="config.radiusOnHiveAp.nameRange1"/>
																</td>
															</tr>
															<tr>
																<td class="labelT1">
																	<label>
																		<s:text name="hm.config.start.hivemanager.password" /><font color="red">*</font>
																	</label><a class="marginBtn" 
																						href="javascript:showInfoDialog('<s:text name="hm.config.start.hivemanager.password.infodlg.note" />')">?</a>
																</td>
																<td>
																	<s:password name="adminPassword" size="46" maxlength="32"
																		 onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword" showPassword="true"/>
																	<s:textfield name="adminPassword" size="46" maxlength="32"
																		 onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword_text" disabled="true" cssStyle="display: none;"/>
																	<s:text name="hm.config.start.hivemanager.password.note"/>
																</td>
															</tr>
															<tr>
																<td class="labelT1">
																	<label>
																		<s:text name="hm.config.start.hivemanager.password.confirm.hivemanager" /><font color="red">*</font>
																	</label>
																</td>
																<td>
																	<s:password size="46" maxlength="32" value="%{adminPassword}"
																		 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword" showPassword="true"/>
																	<s:textfield size="46" maxlength="32" value="%{adminPassword}"
																		 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword_text" disabled="true" cssStyle="display: none;"/>
																	 <s:checkbox id="chkToggleDisplay1" name="ignore" value="true"
																	 	onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','cfAdminPassword'],['adminPassword_text','cfAdminPassword_text']);" />
																	 <s:text name="admin.user.obscurePassword" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hmModeType==1?'none':''}"/>" id="quickStartPwdTr">
													<td>
														<div style="margin: 5px 0 5px 6px;">
															<input type="checkbox" name="chkUpdatePresharedKey" id="chkUpdatePresharedKey" onclick="javascript: checkToUpdatePresharedKey(this);"></input>
															<label for="chkUpdatePresharedKey"><s:text name="hm.missionux.wecomle.update.preshared.key.tip" /></label>
														</div>
														<div id="update_preshared_key_section" style="display: none;">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td class="labelT1" width="240px">
																		<label>
																			<s:text name="hm.missionux.wecomle.update.preshared.key.title.default" /><font color="red">*</font>
																		</label><a class="marginBtn" 
																							href="javascript:showInfoDialog('<s:text name="hm.config.start.welcome.setting.quick.start.pwd.infodlg.note" />')">?</a>
																	</td>
																	<td>
																		<s:password name="quickPassword" size="46" maxlength="32"
																			 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword" showPassword="true"/>
																		<s:textfield name="quickPassword" size="46" maxlength="32"
																			 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword_text" disabled="true" cssStyle="display: none;"/>
																		<s:text name="hm.config.start.hivemanager.password.note"/>
																	</td>
																</tr>
																<tr>
																	<td class="labelT1">
																		<label>
																			<s:text name="hm.missionux.wecomle.update.preshared.key.title.confirm" /><font color="red">*</font>
																		</label>
																	</td>
																	<td>
																		<s:password size="46" maxlength="32" value="%{quickPassword}"
																			 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword" showPassword="true"/>
																		<s:textfield size="46" maxlength="32" value="%{quickPassword}"
																			 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword_text" disabled="true" cssStyle="display: none;"/>
																		 <s:checkbox id="chkToggleDisplay2" name="ignore" value="true"
																		 	onclick="hm.util.toggleObscurePassword(this.checked,['quickPassword','cfQuickPassword'],['quickPassword_text','cfQuickPassword_text']);" />
																		 <s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
																<tr><td height="2px"></td></tr>
															</table>
														</div>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="240px">
																	<label>
																		<s:text name="hm.config.start.network.admin.time.zone" />
																	</label>
																</td>
																<td>
																	<s:select name="timezone"
																		list="%{enumTimeZone}" listKey="key" listValue="value" cssStyle="width: 265px;" />
																</td>
															</tr>
														<%-- 	<tr style="display:<s:property value="%{isInHomeDomain?'':'none'}"/>">
																<td class="noteInfo" style="padding-left:8px" colspan="2">
																	<label>
																		<s:text name="hm.config.start.timezone.note" />
																	</label>
																</td>
															</tr> --%>
														</table>
													</td>
												</tr>
												<tr><td height="5px"></td></tr>
												<tr>
													<td>
														<s:radio label="Gender" name="hmModeType" list="%{modeType1}" disabled="%{disableExpressMode}" listKey="key" listValue="value" onclick="showUpQuickStartPwd(false);" />
													</td>
												</tr>
												<tr><td height="2px"></td></tr>
												<tr>
													<td>
														<s:radio label="Gender" name="hmModeType" list="%{modeType2}" disabled="%{disableMode}" listKey="key" listValue="value" onclick="showUpQuickStartPwd(true);" />
													</td>
												</tr>
												<tr><td height="4px"></td></tr>
												<tr>
													<td class="noteInfo" style="padding-left:20px">
														<label>
															<s:text name="hm.config.start.welcome.setting.mode.note" />
														</label>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									</s:if>
									<s:else>
									<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1" colspan="2">
													<label>
														<s:text name="hm.config.start.security.note" />
													</label>
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="200px">
													<label>
														<s:text name="hm.config.start.hiveAp.password" />
													</label>
												</td>
												<td>
													<s:password name="hiveApPassword" size="46" maxlength="32"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="hiveApPassword" showPassword="true"/>
													<s:textfield name="hiveApPassword" size="46" maxlength="32"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="hiveApPassword_text" disabled="true" cssStyle="display: none;"/>
													<s:text name="hm.config.start.hiveAp.password.note"/>
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<label>
														<s:text name="hm.config.start.hiveAp.password.confirm" />
													</label>
												</td>
												<td>
													<s:password size="46" maxlength="32" value="%{hiveApPassword}"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfHiveApPassword" showPassword="true"/>
													<s:textfield size="46" maxlength="32" value="%{hiveApPassword}"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfHiveApPassword_text" disabled="true" cssStyle="display: none;"/>
												</td>
											</tr>
											<tr>
												<td></td>
												<td>
													 <s:checkbox id="chkToggleDisplay" name="ignore" value="true"
													 	onclick="hm.util.toggleObscurePassword(this.checked,['hiveApPassword','cfHiveApPassword'],['hiveApPassword_text','cfHiveApPassword_text']);" />
													 <s:text name="admin.user.obscurePassword" />
												</td>
											</tr>
											<tr><td height="5px"></td></tr>
											<tr>
												<td class="labelT1">
													<label>
														<s:text name="hm.config.start.hivemanager.password" />
													</label>
												</td>
												<td>
													<s:password name="adminPassword" size="46" maxlength="32"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword" showPassword="true"/>
													<s:textfield name="adminPassword" size="46" maxlength="32"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword_text" disabled="true" cssStyle="display: none;"/>
													<s:text name="hm.config.start.hivemanager.password.note.blackbox"/>
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<label>
														<s:text name="hm.config.start.hivemanager.password.confirm" />
													</label>
												</td>
												<td>
													<s:password size="46" maxlength="32" value="%{adminPassword}"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword" showPassword="true"/>
													<s:textfield size="46" maxlength="32" value="%{adminPassword}"
														 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword_text" disabled="true" cssStyle="display: none;"/>
												</td>
											</tr>
											<tr>
												<td></td>
												<td>
													 <s:checkbox id="chkToggleDisplay1" name="ignore" value="true"
													 	onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','cfAdminPassword'],['adminPassword_text','cfAdminPassword_text']);" />
													 <s:text name="admin.user.obscurePassword" />
												</td>
											</tr>
										</table>
										</td>
									</tr>
									</s:else>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr style="display:<s:property value="%{showUpConfig}"/>">
										<td height="15"></td>
									</tr>
									<tr>
										<td class="crumb" colspan="2" id="lsInfoInTop"><s:property value="%{licenseMessage}" escape="false"/></td>
									</tr>
									<tr>
										<td height="4" id="contentCtrl"></td>
									</tr>
									<tr>
										<s:if test="%{inforDisplay==''}">
											<td style="display:<s:property value="%{enterKeyPannel}"/>" align="left" valign="middle">
											<input type="button" name="ignore" value="Enter Key" class="button"
											onclick="changeEnterPanel(true);" /></td>
											<td align="right" valign="middle">
											<input type="button" name="ignore" value="Continue" class="button"
											onclick="submitAction('continue');" /></td>
										</s:if>
										<s:else>
											<td style="display:<s:property value="%{enterKeyPannel}"/>" align="center" valign="middle">
											<input type="button" name="ignore" value="Enter Key" class="button"
											onclick="changeEnterPanel(true);" /></td>
										</s:else>
									</tr>
								</table>
							</td>
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
		<tr style="display:<s:property value="%{showUpConfig}"/>">
			<td>
				<div id="timeZoneConfirmDlgId" style="display: none;">
					<div class="hd">
						Confirm
					</div>
					<div class="bd">
						 <table>
						 	<tr>
								<td colspan="2">
									<div id="processing" style="display:none">
									<table width="100%" border="0" cellspacing="0" cellpadding="0"
										class="note">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td class="noteError" id="showMessage">Your request is being processed ...</td>
										</tr>
										<tr>
											<td height="6"></td>
										</tr>
									</table>
									</div>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<s:text name="hm.config.start.welcome.setting.timezone.reboot.note"/>
								</td>
							</tr>
						 	<tr>
								<td colspan="2" ><s:radio onclick="this.blur();" label="Gender"	name="timeZoneReboot" value="1" list="%{timeZoneRebootNow}" listKey="key" listValue="value"/></td>
							</tr>
							<tr>
								<td colspan="2"><s:radio onclick="this.blur();"  label="Gender"	name="timeZoneReboot" list="%{timeZoneRebootCancel}" listKey="key" listValue="value"/></td>
							</tr>
					   		<tr>
					   			<td>
					   			<input type="button" id="rebootOk" name="ignore" value="Ok" class="button"
																onclick="saveTimeZoneConfig('continue');" />
					   			</td>
					   			<td>
					   			<input type="button" id="rebootCancel" name="ignore" value="Cancel" class="button"
																onclick="hideTimeZoneConfirmDlg();" />
					   			</td>
					   		</tr>
					   </table>
					</div>
				</div>
			</td>
		</tr>
	</table>
	<tiles:insertDefinition name="licenseInTitle" />
</s:form>