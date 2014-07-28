<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script>
var formName = 'accessConsole';
var KEY_MGMT_OPEN = <%=SsidProfile.KEY_MGMT_OPEN%>;
var KEY_MGMT_AUTO_WPA_PSK = <%=SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK%>;
var KEY_MGMT_WPA_PSK = <%=SsidProfile.KEY_MGMT_WPA_PSK%>;
var keyMgmt;

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_consoleName").disabled) {
		document.getElementById(formName + "_dataSource_consoleName").focus();
	}
	keyMgmt = '<s:property value="dataSource.mgmtKey"/>';
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(830, 450);
		}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		    if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else if (operation == 'create<s:property value="lstForward"/>' 
				|| operation == 'update<s:property value="lstForward"/>'
				|| operation == 'create' 
				|| operation == 'update'){
				hm.options.selectAllOptions('selectMacFilter');
				saveAccessConsole(operation);
			} else {
				if ('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
				hm.options.selectAllOptions('selectMacFilter');
				Get(formName + "_dataSource_optionDisplayStyle").value = Get("optionSet").style.display;
			    document.forms[formName].submit();
			}
		</s:if>
		<s:else>
			if ('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
			hm.options.selectAllOptions('selectMacFilter');
			Get(formName + "_dataSource_optionDisplayStyle").value = Get("optionSet").style.display;
		    document.forms[formName].submit();
		</s:else>
	}
}

function saveAccessConsole(operation) {
	if (validate(operation)) {
		var url = "<s:url action='accessConsole' includeParams='none' />" + "?jsonMode=true" 
		+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["accessConsole"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succAccessConsole, failure : failAccessConsole, timeout: 60000}, null);
	}
}

var succAccessConsole = function(o) {
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
	}catch(e){
		// do nothing now
	}
}

var failAccessConsole = function(o) {
	// do nothing now
}

function validate(operation) {
	if ((operation == 'create<s:property value="lstForward"/>' || operation == 'create') && !validateName()) {
		return false;
	}
	if ('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_maxClient").value = "2";
		return true;
	}
	
	if(operation == "editMacFilter"){
		var value = hm.util.validateOptionTransferSelection("selectMacFilter");
		if(value < 0){
			showOptionSettingsContent();
			return false
		}else{
			document.forms[formName].macFilter.value = value;
		}
	}

	if(operation == 'update<s:property value="lstForward"/>' 
	|| operation == 'create<s:property value="lstForward"/>'
	|| operation == 'update' 
	|| operation == 'create'){
		if (KEY_MGMT_OPEN != keyMgmt) {
			if(!validateKeyValue()) {
				return false;
			}
		}
		if(!validateMaxClient()) {
			return false;
		}
	}
	return true;
}

function showOptionSettingsContent(){
    showHideContent("optionSet","");
}

function validateMaxClient() {
	var inputElement = document.getElementById(formName + "_dataSource_maxClient");
    if (inputElement.value.length == 0) {
    	showOptionSettingsContent();
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.maxClient" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ssid.maxClient" />',
      											<s:property value="%{maxClientRange.min()}" />,
      											<s:property value="%{maxClientRange.max()}" />);
    if (message != null) {
    	showOptionSettingsContent();
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    } 
    return true;
}

function validateName() {
    var inputElement = document.getElementById(formName + "_dataSource_consoleName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.ipFilter.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function validateKeyValue() {
	var keyElement;
    var confirmElement;
    if (document.getElementById("chkToggleDisplay").checked) {
    	keyElement = document.getElementById("keyValue");
    	confirmElement = document.getElementById("confirmKeyValue");
    } else {
    	keyElement = document.getElementById("keyValue_text");
    	confirmElement = document.getElementById("confirmKeyValue_text");
    }

	if (keyElement.value.length ==0) {
		hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.access.console.key" /></s:param></s:text>');
	    keyElement.focus();
	    return false;
    }

    if (confirmElement.value.length == 0) {
        hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.access.console.key.confirm" /></s:param></s:text>');
        confirmElement.focus();
        return false;
    }
    if (keyElement.value.length < 8) {
        hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.access.console.key" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
        keyElement.focus();
        return false;
    }

    if (confirmElement.value.length < 8) {
        hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.access.console.key.confirm" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
        confirmElement.focus();
        return false;
    }
      
    var message = hm.util.validatePassword(keyElement.value, '<s:text name="config.access.console.key" />');
	if (message != null) {
	    hm.util.reportFieldError(keyElement, message);
	    keyElement.focus();
	    return false;
	}
	
	if (keyElement.value != confirmElement.value) {
     	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.access.console.key.confirm" /></s:param><s:param><s:text name="config.access.console.key" /></s:param></s:text>');
   	    keyElement.focus();
   	    return false;
    }
    return true;
}

function keyManagementChange(keyValue) {
	var hideKey = document.getElementById("hideAsciiKey");
	var confirmKey = document.getElementById("hideAsciiKeyConf");
	var obscureKey = document.getElementById("hideAsciiKeyObscure");	
	var encryption = document.getElementById(formName + "_dataSource_encryption");
	var hideKeyMgtNote=document.getElementById("keymgtNoteTr");
	hideKeyMgtNote.style.display="none";
	
	if (KEY_MGMT_OPEN == keyValue) {
		hideKey.style.display = "none";
		confirmKey.style.display = "none";
		obscureKey.style.display = "none";
		encryption.length=0;
		encryption.length=1;
		encryption.options[0].value='0';
		encryption.options[0].text='NONE';
	} else {
		hideKey.style.display = "";
		confirmKey.style.display = "";
		obscureKey.style.display = "";
		if (KEY_MGMT_AUTO_WPA_PSK == keyValue) {
			encryption.length=0;
			encryption.length=1;
			encryption.options[0].value='5';
			encryption.options[0].text='Auto-TKIP or CCMP (AES)';
		} else {
			encryption.length=0;
			encryption.length=2;
			encryption.options[0].value='1';
			encryption.options[0].text='CCMP(AES)';
			encryption.options[1].value='2';
			encryption.options[1].text='TKIP';
		}
	}
    if (keyValue==KEY_MGMT_WPA_PSK) {
      	hideKeyMgtNote.style.display="";
    }
	keyMgmt = keyValue;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="accessConsole" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>
<div id="content"><s:form action="accessConsole" id="accessConsole">
	<s:hidden name="macFilter" />
	<s:hidden name="dataSource.optionDisplayStyle"></s:hidden>
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-access-consoles.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.access.console.title"/>
								</s:if> <s:else>
									<s:text name="config.access.console.title.edit"/>
								</s:else>
							</td>
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
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
										<td width="20px">&nbsp;</td>
										<s:if test="%{dataSource.id == null}">
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:else>
										</s:if>
										<s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:else>
										</s:else>
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
	</div>
	</s:if>
	<s:if test="%{jsonMode==false}">
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
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_ACCESS_CONSOLE%>');">
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
	</table>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">		
			<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="720">
				<tr>
					<td>
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="145"><s:text
									name="config.ipFilter.name" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.consoleName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="%{nameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
									name="config.ipFilter.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.access.console.mode" /></td>
								<td><s:select name="dataSource.consoleMode"
									list="%{enumConsoleMode}" listKey="key" listValue="value" cssStyle="width: 80px;" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.ipFilter.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" />&nbsp;<s:text
									name="config.ipFilter.description.range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:4px 5px 0 5px">
					<fieldset><legend><s:text 
							name="config.access.console.security" /></legend>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td style="padding-right:5px"><s:radio label="Gender" name="dataSource.mgmtKey" list="%{enumKeyMgmt1}" disabled="%{disableMode}"
											listKey="key" listValue="value" onclick="keyManagementChange(this.value);" /></td>
										<td style="padding-right:5px"><s:radio label="Gender" name="dataSource.mgmtKey" list="%{enumKeyMgmt2}" disabled="%{disableMode}"
											listKey="key" listValue="value" onclick="keyManagementChange(this.value);" /></td>
										<td style="padding-right:5px"><s:radio label="Gender" name="dataSource.mgmtKey" list="%{enumKeyMgmt3}" disabled="%{disableMode}"
											listKey="key" listValue="value" onclick="keyManagementChange(this.value);" /></td>
										<td><s:radio label="Gender" name="dataSource.mgmtKey" list="%{enumKeyMgmt4}" disabled="%{disableMode}"
											listKey="key" listValue="value" onclick="keyManagementChange(this.value);" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr id="keymgtNoteTr" style="display:<s:property value="hideKeyManagementNote"/>">
							<td class="noteInfo" style="padding: 4px 0 4px 10px"><s:text name="config.ssid.keymanagement.note"></s:text></td>
						</tr>
						<tr>
							<td style="padding-top:3px">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" width="129"><s:text name="config.ssid.encriptionMethord" /></td>
										<td><s:select name="dataSource.encryption"
											list="%{enumEncryption}" listKey="key" listValue="value" cssStyle="width: 200px;" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<!--  
						<tr>
							<td class="labelT1"><s:text name="config.ssid.authenticationMethord" /></td>
							<td><s:select list="#{'0':'OPEN'}" disabled="true" cssStyle="width: 200px;" /></td>
						</tr>
						-->
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr style="display:<s:property value="%{hideAsciiKey}"/>" id="hideAsciiKey">
										<td class="labelT1" width="129"><s:text name="config.access.console.key" /><font color="red"><s:text name="*"/></font></td>
										<td><s:password id="keyValue" name="dataSource.asciiKey" size="48" maxlength="63" showPassword="true"
											onkeypress="return hm.util.keyPressPermit(event,'password');"/>
											<s:textfield id="keyValue_text" name="dataSource.asciiKey" size="48" maxlength="63" cssStyle="display:none"
											onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" />
											<s:text name="config.ssid.keyValue_range" /></td>
									</tr>
									<tr style="display:<s:property value="%{hideAsciiKey}"/>" id="hideAsciiKeyConf">
										<td class="labelT1"><s:text name="config.access.console.key.confirm" /><font color="red"><s:text name="*"/></font></td>
										<td><s:password id="confirmKeyValue" size="48" maxlength="63" showPassword="true" value="%{dataSource.asciiKey}" 
											onkeypress="return hm.util.keyPressPermit(event,'password');"/>
											<s:textfield id="confirmKeyValue_text" size="48" maxlength="63" cssStyle="display:none"
											onkeypress="return hm.util.keyPressPermit(event,'password');" />
											<s:text name="config.ssid.keyValue_range" />
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideAsciiKey}"/>" id="hideAsciiKeyObscure">
										<td>&nbsp;</td>
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<s:checkbox id="chkToggleDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['keyValue','confirmKeyValue'],['keyValue_text','confirmKeyValue_text']);"
															disabled="%{writeDisable4Struts}" />
													</td>
													<td>
														<s:text name="config.access.console.key.obscure" />
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
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
			       	<td style="padding-left: 5px;"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.optional" />','optionSet');</script></td>
			    </tr>
			    <tr>
			       	<td style="padding-left: 10px;">
			           	<div id="optionSet" style="display: <s:property value="%{dataSource.optionDisplayStyle}"/>">
			            	<table cellspacing="0" cellpadding="0" border="0">  
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td style="padding:0 2px 0 5px"><s:checkbox name="dataSource.hideSsid" /></td>
											<td><s:text name="config.access.console.hide.ssid" /></td>
										</tr>
										<tr>
											<td style="padding:0 2px 0 5px"><s:checkbox name="dataSource.enableTelnet" /></td>
											<td><s:text name="config.access.console.enable.telnet" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="135"><s:text name="config.ssid.maxClient" /><font color="red"><s:text name="*"/></font></td>
											<td><s:textfield name="dataSource.maxClient" size="10" maxlength="2"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.access.console.max.client.range" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td style="padding:4px 130px 0 15px">
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
												<%--<td width="110" style="padding-left:20px;">
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newMacFilter')"><img class="dinl"
														src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editMacFilter')"><img class="dinl"
														src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>--%>
												<td nowrap="nowrap"><s:text name="config.ssid.defaultAction" /></td>
												<td><s:select name="dataSource.defaultAction"
													list="%{enumFilterAction}" listKey="key" listValue="value" cssStyle="width: 100px;" />
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