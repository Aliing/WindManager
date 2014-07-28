<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'localUser';  
var isEasyMode = <s:property value="%{blnEasyMode}"/>;
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_userName").disabled) {
		document.getElementById(formName + "_dataSource_userName").focus();
	} else {
		//if (document.getElementById("userTypeRadio2").checked) {
			document.getElementById(formName + "_userGroupId").disabled=true;
		//}
		document.getElementById("userTypeRadio1").disabled=true;
		document.getElementById("userTypeRadio2").disabled=true;
		document.getElementById("userTypeRadio3").disabled=true;
	}
	if (document.getElementById("userTypeRadio2").checked) {
		document.getElementById(formName + "_dataSource_userName").readOnly=true;
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == "editGroup"){
		var value = hm.util.validateListSelection(formName + "_userGroupId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userGroupId.value = value;
		}
	}
	if('<%=Navigation.L2_FEATURE_LOCAL_USER%>' == operation 
		|| operation == 'newGroup'
		|| operation == 'editGroup'
		|| operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	
	if (!checkUserGroup()) {
		return false;
	}

	var name = document.getElementById(formName + "_dataSource_userName");
	var message = hm.util.validatePskUserName(name.value, '<s:text name="config.localUser.userName" />');
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	if (document.getElementById("userTypeRadio1").checked) {
		if (!checkPassword()) {
			return false;
		}
	} else if (document.getElementById("userTypeRadio3").checked) {
		if (!validateManualPassword()) {
			return false;
		}
	}
	
	if (!checkEmailAddress()){
		return false;
	}
	return true;
}

function checkUserGroup() {
	var userGroupId = document.getElementById(formName + "_userGroupId");
	if (userGroupId.value == null || userGroupId.value < 0) {
		if (isEasyMode) {
			if (document.getElementById("userTypeRadio1").checked) {
				hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.tag" /></s:param></s:text>');
			}else {
				hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.head.ssid" /></s:param></s:text>');
			}
		} else {
			hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.userGroup" /></s:param></s:text>');
		}
		userGroupId.focus();
		return false;
	}
	return true;
}

function checkEmailAddress(){
	if (document.getElementById(formName + "_dataSource_mailAddress").value.length==0) {
		return true;
	}
	var emails = document.getElementById(formName + "_dataSource_mailAddress").value.split(";");
	for (var i=0;i<emails.length;i++) {
		if (i==emails.length-1 && emails[i].trim()=="") {
			break;
		}
		if (!hm.util.validateEmail(emails[i].trim())) {
			hm.util.reportFieldError(document.getElementById(formName + "_dataSource_mailAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUser.emailTitle" /></s:param></s:text>');
			document.getElementById(formName + "_dataSource_mailAddress").focus();
			return false;
		}
	}
	return true;
}

function checkPassword() {
	var password;
	var confirm;
	if (document.getElementById("chkPasswordDisplay").checked) {
		password = document.getElementById("localUserPassword");
		confirm = document.getElementById("confirm");
	} else {
		password = document.getElementById("localUserPassword_text");
		confirm = document.getElementById("confirm_text");
	}

	var message = hm.util.validatePassword(password.value, '<s:text name="config.localUser.password" />');
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, '<s:text name="config.localUser.confirm" />');
   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
       	confirm.focus();
       	return false;
   	}
   	
  	if (password.value.trim().length < 8) {
           hm.util.reportFieldError(password, '<s:text name="error.keyValueRange"><s:param><s:text name="config.localUser.password" /></s:param><s:param><s:text name="config.localUser.passwordAsciiRange" /></s:param></s:text>');
           password.focus();
           return false;
     }

     if (confirm.value.trim().length < 8) {
           hm.util.reportFieldError(confirm, '<s:text name="error.keyValueRange"><s:param><s:text name="config.localUser.confirm" /></s:param><s:param><s:text name="config.localUser.passwordAsciiRange" /></s:param></s:text>');
           confirm.focus();
           return false;
     }
   	
	if (password.value != confirm.value) {
        hm.util.reportFieldError(confirm, '<s:text name="error.notEqual"><s:param><s:text name="config.localUser.confirm" /></s:param><s:param><s:text name="config.localUser.password" /></s:param></s:text>');
        confirm.focus();
        return false;
    }
	return true;
}

function validateManualPassword() {
   	if (!validateAsciiKeyConfirmValue("pskPassword1","pskConfirmPassword1")){
   		return false;
   	}
    return true;
}

function validateAsciiKeyConfirmValue(elementKey,elementConfirm) {
	  var keyElement;
	  var confirmElement;
	  if (document.getElementById("chkToggleDisplay1").checked) {
	  	keyElement = document.getElementById(elementKey);
	  	confirmElement = document.getElementById(elementConfirm);
	  } else {
	  	keyElement = document.getElementById(elementKey+ "_text");
	  	confirmElement = document.getElementById(elementConfirm+ "_text");
	  }

	  if (keyElement.value.length ==0) {
	         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.password" /></s:param></s:text>');
	         keyElement.focus();
	         return false;
      }

      if (confirmElement.value.length == 0) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.confirm" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }

      var message = hm.util.validateSsid(keyElement.value, '<s:text name="config.localUser.password" />');
	  if (message != null) {
	      hm.util.reportFieldError(keyElement, message);
	      keyElement.focus();
	      return false;
	  }

      if (keyElement.value.trim().length < 8) {
            hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.localUser.password" /></s:param><s:param><s:text name="config.localUser.passwordAsciiRange" /></s:param></s:text>');
            keyElement.focus();
            return false;
      }

      if (confirmElement.value.trim().length < 8) {
            hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.localUser.confirm" /></s:param><s:param><s:text name="config.localUser.passwordAsciiRange" /></s:param></s:text>');
            confirmElement.focus();
            return false;
      }

      if (keyElement.value != confirmElement.value) {
	      	hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.localUser.confirm" /></s:param><s:param><s:text name="config.localUser.password" /></s:param></s:text>');
	    	keyElement.focus();
	    	return false;
      }

      return true;
}

function changeUserType(userType){

	var url = '<s:url action="localUser"><s:param name="operation" value="changeUserType"/></s:url>' + "&userType="+userType  + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback3, null);

	if (userType==1) {
		if (isEasyMode) {
			document.getElementById("userGroupTitle").innerHTML ="Device";
		}
		document.getElementById("showEmailInfoDiv").style.display="none";
		document.getElementById(formName + "_dataSource_mailAddress").value="";
		document.getElementById("showAutoUserInfoDiv").style.display="none";
		document.getElementById("showPasswordInfoDiv").style.display="block";
		document.getElementById("passwordAsciiDiv").style.display="none";
		document.getElementById(formName + "_dataSource_userName").readOnly=false;
		document.getElementById(formName + "_dataSource_userName").value="";
		document.getElementById("localUserPassword").value ="";
		document.getElementById("confirm").value ="";
		document.getElementById("localUserPassword_text").value ="";
		document.getElementById("confirm_text").value ="";
	}
	if (userType==2) {
		if (isEasyMode) {
			document.getElementById("userGroupTitle").innerHTML ="SSID";
		}
		document.getElementById("showEmailInfoDiv").style.display="block";
		document.getElementById("showAutoUserInfoDiv").style.display="block";
		document.getElementById("showPasswordInfoDiv").style.display="none";
		document.getElementById("passwordAsciiDiv").style.display="none";
		document.getElementById(formName + "_dataSource_userName").readOnly=true;
	}
	if (userType==3) {
		if (isEasyMode) {
			document.getElementById("userGroupTitle").innerHTML ="SSID";
		}
		document.getElementById("showEmailInfoDiv").style.display="block";
		document.getElementById("showAutoUserInfoDiv").style.display="none";
		document.getElementById("showPasswordInfoDiv").style.display="none";
		document.getElementById("passwordAsciiDiv").style.display="block";
		document.getElementById(formName + "_dataSource_userName").readOnly=false;
		document.getElementById(formName + "_dataSource_userName").value="";
		document.getElementById("pskPassword1").value ="";
		document.getElementById("pskConfirmPassword1").value ="";
		document.getElementById("pskPassword1_text").value ="";
		document.getElementById("pskConfirmPassword1_text").value ="";
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	if (document.getElementById("chkPasswordDisplay").checked) {
		document.getElementById("localUserPassword").value =value;
		document.getElementById("confirm").value =value;
	} else {
		document.getElementById("localUserPassword_text").value =value;
		document.getElementById("confirm_text").value =value;
	}

};

var detailsFailed = function(o) {
	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function generalPassword(){
	var url = '<s:url action="localUser"><s:param name="operation" value="genenatePassword"/></s:url>';
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}
function generalPasswordAscii(){
	var userGroupId = document.getElementById(formName + "_userGroupId").value;
	var userNameLength = document.getElementById(formName + "_dataSource_userName").value.trim().length;
	var url = '<s:url action="localUser"><s:param name="operation" value="genenatePassword"/></s:url>' + "&genPasswordType=ascii" + "&userGroupId="+userGroupId + "&userNameLen="+userNameLength + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback4, null);
}

var detailsSuccess4 = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	if (document.getElementById("chkToggleDisplay1").checked) {
		document.getElementById("pskPassword1").value =value;
		document.getElementById("pskConfirmPassword1").value =value;
	} else {
		document.getElementById("pskPassword1_text").value =value;
		document.getElementById("pskConfirmPassword1_text").value =value;
	}

};
var callback4 = {
	success : detailsSuccess4,
	failure : detailsFailed
};

function changeUserGroup(){
	if (document.getElementById("userTypeRadio2").checked) {
		var userGroupId = document.getElementById(formName + "_userGroupId").value;
		var url = '<s:url action="localUser"><s:param name="operation" value="changeUserGroup"/></s:url>' + "&userGroupId="+userGroupId  + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback2, null);
	}
}

var detailsSuccess2 = function(o) {
	eval("var details = " + o.responseText);
	var activeUser = details.active;
	var revokeUser = details.revoke;
	var remainUser = details.remain;
	var userName = details.userName;
	if (document.getElementById("userTypeRadio2").checked) {
		document.getElementById(formName + "_dataSource_userName").value =userName;
	}
	document.getElementById("activeUser").innerHTML =activeUser;
	document.getElementById("revokeUser").innerHTML =revokeUser;
	document.getElementById("remainUser").innerHTML =remainUser;
};

var callback2 = {
	success : detailsSuccess2,
	failure : detailsFailed
};

var detailsSuccess3 = function(o) {
	eval("var result = " + o.responseText);
	var selectGroup = document.getElementById(formName + "_userGroupId");
	selectGroup.length=0;
	selectGroup.length=result.length;
	for(var i = 0; i < result.length; i ++)
	{	
		selectGroup.options[i].text=result[i].value;
		selectGroup.options[i].value=result[i].id;
	}
	selectGroup.options[0].selected=true;
	selectGroup.value=selectGroup.options[0].value;
	selectGroup.text=selectGroup.options[0].text;
	changeUserGroup();

};

var callback3 = {
	success : detailsSuccess3,
	failure : detailsFailed
};


function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="localUser" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content">
	<s:form action="localUser">
		<s:hidden name="userType" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
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
									<s:property value="writeDisabled" />></td>
							</s:else>
							<s:if test="%{lstForward == null || lstForward == ''}">
								<td><input type="button" name="cancel" value="Cancel"
									class="button"
									onClick="submitAction('<%=Navigation.L2_FEATURE_LOCAL_USER%>');">
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
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="720px">
						<tr>
							<td height="4">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
								type="password">
							</td>
						</tr>
						<tr>
							<td style="padding: 4px 4px 4px 4px;">
								<fieldset>
									<legend>
										<s:text name="config.localUserGroup.userType" />
									</legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td><s:radio label="Gender" id="userTypeRadio"
													name="dataSource.userType" list="#{1:'RADIUS user'}"
													value="%{dataSource.userType}" onclick="changeUserType(1);" /></td>
											<td><s:radio label="Gender" id="userTypeRadio"
													name="dataSource.userType"
													list="#{2:'Automatically generated private PSK user'}"
													value="%{dataSource.userType}" onclick="changeUserType(2);" /></td>
											<td><s:radio label="Gender" id="userTypeRadio"
													name="dataSource.userType"
													list="#{3:'Manually created private PSK user'}"
													value="%{dataSource.userType}" onclick="changeUserType(3);" /></td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" width="150px"><label
											id="userGroupTitle"><s:property
													value="userGroupTitle" /></label><font color="red"><s:text
													name="*" /></font></td>
										<td width="270px"><s:select name="userGroupId"
												cssStyle="width: 272px;" list="%{localUserGroup}"
												listKey="id" listValue="value" onchange="changeUserGroup();" />
										</td>
										<s:if test="%{fullMode}">
											<td><s:if test="%{showNewGroupButton == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />" width="16"
														height="16" alt="New" title="New" />
												</s:if> <s:else>
													<a class="marginBtn"
														href="javascript:submitAction('newGroup')"><img
														class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
												</s:else></td>
											<td><s:if test="%{showNewGroupButton == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
												</s:if> <s:else>
													<a class="marginBtn"
														href="javascript:submitAction('editGroup')"><img
														class="dinl" src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else></td>
										</s:if>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" width="150px"><label><s:text
													name="config.localUser.userName" /><font color="red"><s:text
														name="*" /></font></label></td>
										<td><s:textfield size="48" name="dataSource.userName"
												maxlength="%{userNameLength}" disabled="%{disabledName}"
												onkeypress="return hm.util.keyPressPermit(event,'pskUserName');" />&nbsp;<s:text
												name="config.ssid.ssidName_range" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="%{showPasswordAscii}"/>"
									id="passwordAsciiDiv">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" width="150px"><label><s:text
														name="config.localUser.password" /><font color="red"><s:text
															name="*" /></font></label></td>
											<td><s:password size="48" name="pskPassword1"
													id="pskPassword1" maxlength="63" showPassword="true"
													onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
												<s:textfield id="pskPassword1_text" name="pskPassword1"
													size="48" maxlength="63" cssStyle="display:none"
													disabled="true"
													onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
												<s:text name="config.localUser.passwordAsciiRange" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="150px"><label><s:text
														name="config.localUser.confirm" /><font color="red"><s:text
															name="*" /></font></label></td>
											<td><s:password id="pskConfirmPassword1"
													showPassword="true" name="pskConfirmPassword1"
													value="%{pskPassword1}" size="48" maxlength="63"
													onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
												<s:textfield id="pskConfirmPassword1_text"
													name="pskConfirmPassword1" value="%{pskPassword1}"
													size="48" maxlength="63" cssStyle="display:none"
													disabled="true"
													onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
												<input type="button" name="ignore" value="Generate"
												class="button" onClick="generalPasswordAscii();"
												title="Auto Generate Password"
												<s:property value="writeDisabled" /> /></td>
										</tr>
										<tr>
											<td>&nbsp;</td>
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><s:checkbox id="chkToggleDisplay1" name="ignore"
																value="true" disabled="%{writeDisable4Struts}"
																onclick="hm.util.toggleObscurePassword(this.checked,['pskPassword1','pskConfirmPassword1'],['pskPassword1_text','pskConfirmPassword1_text']);" />
														</td>
														<td><s:text name="admin.user.obscurePassword" /></td>
														<s:if test="%{easyMode}">
															<td class="noteInfo">&nbsp;&nbsp;<s:text
																	name="config.localUser.passwordAsciiInfo" />
															</td>
														</s:if>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="%{showPasswordInfo}"/>"
									id="showPasswordInfoDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" width="150px"><label> <s:text
														name="config.localUser.password" /><font color="red"><s:text
															name="*" /></font></label></td>
											<td><s:password size="48"
													name="dataSource.localUserPassword" id="localUserPassword"
													maxlength="63" showPassword="true"
													onkeypress="return hm.util.keyPressPermit(event,'password');" />
												<s:textfield id="localUserPassword_text"
													name="dataSource.localUserPassword" size="48"
													maxlength="63" cssStyle="display:none"
													onkeypress="return hm.util.keyPressPermit(event,'password');"
													disabled="true" /> <s:text
													name="config.localUser.passwordAsciiRange" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="150px"><label> <s:text
														name="config.localUser.confirm" /><font color="red"><s:text
															name="*" /></font></label></td>
											<td><s:password size="48" id="confirm" maxlength="63"
													showPassword="true" value="%{dataSource.localUserPassword}"
													onkeypress="return hm.util.keyPressPermit(event,'password');" />
												<s:textfield id="confirm_text" size="48" maxlength="63"
													cssStyle="display:none"
													onkeypress="return hm.util.keyPressPermit(event,'password');" />
												<input type="button" name="ignore" value="Generate"
												class="button" onClick="generalPassword();"
												title="Auto Generate Password"
												<s:property value="writeDisabled" /> /></td>
										</tr>
										<tr>
											<td>&nbsp;</td>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td><s:checkbox id="chkPasswordDisplay" name="ignore"
																value="true"
																onclick="hm.util.toggleObscurePassword(this.checked,['localUserPassword','confirm'],['localUserPassword_text','confirm_text']);"
																disabled="%{writeDisable4Struts}" /></td>
														<td><s:text name="admin.user.obscurePassword" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>

						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" width="150px"><s:text
												name="config.localUser.description" /></td>
										<td><s:textfield size="48" name="dataSource.description"
												maxlength="%{commentLength}" />&nbsp;<s:text
												name="config.ssid.description_range" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<div style="display:<s:property value="%{showEmailInfo}"/>"
									id="showEmailInfoDiv">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" width="150px"><s:text
													name="config.localUser.emailTitle" /></td>
											<td><s:textfield name="dataSource.mailAddress" size="48"
													maxlength="128" />&nbsp;<s:text
													name="config.localUser.email.emailNoteRange" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="150px"></td>
											<td colspan="2" nowrap="nowrap" class="noteInfo"><s:text
													name="report.reportList.email.note" />
													</br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<s:text name="report.reportList.email.emailNote"></s:text>
													</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 4px 4px 4px 4px;" colspan="2">
								<div style="display:<s:property value="%{showAutoUserInfo}"/>"
									id="showAutoUserInfoDiv">
									<fieldset>
										<legend>
											<s:text name="config.localUser.autoUser.info" />
										</legend>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="4px"></td>
											</tr>
											<tr>
												<td width="200px" style="padding-left: 30px"><s:text
														name="config.localUser.autoUser.activeUser" /></td>
												<td id="activeUser"><s:property value="activeUser" /></td>
											</tr>
											<tr>
												<td style="padding-left: 30px"><s:text
														name="config.localUser.autoUser.revokeUser" /></td>
												<td id="revokeUser"><s:property value="revokeUser" /></td>
											</tr>
											<tr>
												<td style="padding-left: 30px"><s:text
														name="config.localUser.autoUser.remainUser" /></td>
												<td id="remainUser"><s:property value="remainUser" /></td>
											</tr>
										</table>
									</fieldset>
								</div>
							</td>
						</tr>

						<tr>
							<td height="5"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
