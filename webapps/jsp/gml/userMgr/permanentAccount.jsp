<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'permanentAccount';

function submitAction(operation) {
	if (validate(operation)) {
//		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
//			showProcessing();
//		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == 'cancel') {
		return true;
	}
	
	if (!checkUserGroup()) {
		return false;
	}
	
	if (!checkUserName()) {
		return false;
	}

	if (!checkPassword()) {
		return false;
	}
	
	if(!validateEmail()){
		return false;
	}
	
	return true;
}

function checkUserGroup() {
	var userGroupId = document.getElementById(formName + "_userGroupId");
	
	if (userGroupId.value == null || userGroupId.value < 0) {
		hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.userGroup" /></s:param></s:text>');
		userGroupId.focus();
		return false;
	}
	
	return true;
}

function checkUserName() {
	var name = document.getElementById(formName + "_dataSource_userName");
	var message = hm.util.validatePskUserName(name.value, '<s:text name="config.localUser.userName" />');
	
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	return true;
}

function checkPassword() {
	var password;
	var confirm;
	
	if (document.getElementById("chkToggleDisplay").checked) {
		password = document.getElementById("password");
		confirm = document.getElementById("confirmPassword");
	} else {
		password = document.getElementById("password_text");
		confirm = document.getElementById("confirmPassword_text");
	}
	
	var message = hm.util.validatePassword(password.value, '<s:text name="config.localUser.password" />');
   	
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
       	password.focus();
       	return false;
   	}
   	
   	if(password.value.length < 8) {
		hm.util.reportFieldError(password, 'The length of <s:text name="config.localUser.password" /> should be <s:text name="gml.permanent.password.range" />');
       	password.focus();
       	return false;
	}
	
   	var message = hm.util.validatePassword(confirm.value, '<s:text name="config.localUser.confirm" />');

   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
       	confirm.focus();
       	return false;
   	}

   	if(confirm.value.length < 8) {
		hm.util.reportFieldError(confirm, 'The length of <s:text name="config.localUser.confirm" /> should be <s:text name="gml.permanent.password.range" />');
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

function validateEmail() {
	var email = document.getElementById(formName + "_dataSource_mailAddress");
	
	if(email.value.trim().length == 0) {
		return true;
	}
	
	if(!hm.util.validateEmail(email.value)) {
		hm.util.reportFieldError(email, 
			'<s:text name="error.gml.temporary.email.invalid" />');
       	email.focus();
       	return false;
	}
   	
	return true;
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="permanentAccount" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}  
</script>

<div id="content"><s:form action="permanentAccount">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update');"
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700px">
				<tr>
					<td height="4">
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<s:if test="%{fullMode}">
									<td class="labelT1" width="150px">
										<s:text name="gml.temporary.userGroup" /><font color="red"><s:text name="*"/></font>
									</td>
								</s:if>
								<s:elseif test="%{easyMode}">
									<td class="labelT1" width="150px">
										<s:text name="gml.temporary.ssid" /><font color="red"><s:text name="*"/></font>
									</td>
								</s:elseif>
								<td><s:select name="userGroupId" cssStyle="width: 270px;"
									list="%{localUserGroup}" listKey="id" listValue="value" 
									disabled="%{disabledName}"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><label><s:text
									name="config.localUser.userName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="48"
									name="dataSource.userName" maxlength="%{userNameLength}"
									disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'pskUserName');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><label><s:text
									name="config.localUser.password" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:password size="48" name="dataSource.localUserPassword" id="password"
									maxlength="63" showPassword="true"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
									<s:textfield id="password_text" name="dataSource.localUserPassword"
									size="48" maxlength="63" cssStyle="display:none" disabled="true"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
									<s:text name="gml.permanent.password.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="150px"><label><s:text
									name="config.localUser.confirm" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:password id="confirmPassword" showPassword="true"
									name="confirmPassword" value="%{dataSource.localUserPassword}" size="48"
									maxlength="63"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
									<s:textfield id="confirmPassword_text"
									name="confirmPassword" value="%{dataSource.localUserPassword}" size="48"
									maxlength="63" cssStyle="display:none" disabled="true"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
									<s:text name="gml.permanent.password.range" />
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<s:checkbox id="chkToggleDisplay" name="ignore" value="true" disabled="%{writeDisable4Struts}"
													onclick="hm.util.toggleObscurePassword(this.checked,['password','confirmPassword'],['password_text','confirmPassword_text']);" />
											</td>
											<td>
												<s:text name="admin.user.obscurePassword" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.email" />
								</td>
								<td><s:textfield size="36"
									name="dataSource.mailAddress" maxlength="%{mailAddressLength}"
									 />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUser.description" /></td>
								<td><s:textfield size="48"
									name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
						</table>
					</td>	
				</tr>		
				
			</table>
			</td>
		</tr>		
	</table>
</s:form></div>