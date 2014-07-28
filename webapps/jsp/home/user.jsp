<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.admin.HmUserGroup"%>

<script>
var formName = 'users';

function onLoadPage() 
{
	<s:if test="%{operation == 'edit'}">
		hm.util.hide('passwordSection');
	</s:if>

	if (document.getElementById("email").disabled == false) {
		document.getElementById("email").focus();
	}
	
	// set right UI for different user group selection
	<s:if test="%{operation == 'new'}">
	var userGroupList = document.getElementById("userGroupList");
	if (userGroupList != null) {
		selectUserGroup(userGroupList.options[userGroupList.selectedIndex].text);		
	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('localUserGroups');
		hm.options.selectAllOptions('ssidProfiles');
		if ("create" + '<s:property value="lstForward"/>'!=operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
 			document.forms[formName].target = "_parent";
 		}
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	
	if ("create" + '<s:property value="lstForward"/>'!=operation && "update" + '<s:property value="lstForward"/>'!=operation)
	{
		return true;
	}
	
	var userName = document.getElementById("adminUserName");
	if (userName.value.length == 0) 
	{
        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.userName" /></s:param></s:text>');
        userName.focus();
        return false;
    }
    if (userName.value.indexOf(' ') > -1) {
        hm.util.reportFieldError(userName, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.user.userName" /></s:param></s:text>');
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
	
	if (operation!='update' || (operation == 'update' && document.getElementById('changePassword').checked) )
	{
		if (password.value.length == 0) 
		{
	        hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password" /></s:param></s:text>');
	        password.focus();
	        return false;
	    }
	    
		if (!hm.util.validateUserNewPasswordFormat(password, passwordConfirm, '<s:text name="admin.user.password" />',
    			'<s:text name="admin.user.password.confirm" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />', userName.value)) {
    		return false;
    	}
	}

	var emailAddr = document.getElementById("email");
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
	
	return true;
}

function changeUserPassword(checked)
{
	if(checked) {
		hm.util.show('passwordSection');
	} else {
		hm.util.hide('passwordSection');
	}
}

function selectUserGroup(value)
{
	if (value=='<%=HmUserGroup.GM_OPERATOR%>')	{
		hm.util.show('operatorSection');
	} else {
		hm.util.hide('operatorSection');
	}
	
	<s:if test="%{vhmAdminUser}">
		if (value=='<%=HmUserGroup.CONFIG%>') {
			hm.util.show('accessMyHiveTr');
		} else {
			hm.util.hide('accessMyHiveTr');
		}
	</s:if>
}

function selectUpdateLocalUserGroup(checked)
{
	if(checked) {
		hm.util.show('localUserGroupSection');
	} else {
		hm.util.hide('localUserGroupSection');
	}
}

function selectUpdateSSIDProfile(checked) 
{
	if(checked) {
		hm.util.show('ssidProfileSection');
	} else {
		hm.util.hide('ssidProfileSection');
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="users" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{operation == 'new'}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="displayName" />\'</td>');
		</s:else>
	</s:else>
}
</script>

<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 15px">
</s:if>
<s:else>
<div id="content">
</s:else>
<s:form action="users">
	<s:hidden name="updateAdminId" value="%{selectedId}"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled4HHM" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update');"
							<s:property value="writeDisabled4HHM" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_ADMINISTRATORS%>');">
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
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="680px">
				<tr>
					<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password"></td>
				</tr>
				<tr>
					<td class="labelT1" width="160px"><label> <s:text
						name="admin.user.emailAddress" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="email" name="dataSource.emailAddress"
						size="48" maxlength="%{emailLength}" disabled="%{disabledEmail}" />
					<s:text name="admin.email.address.range" /></td>
				</tr>
				<tr>
					<td class="labelT1"><label> <s:text
						name="admin.user.userName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="adminUserName" name="dataSource.userName"
						size="48"
						onkeypress="return hm.util.keyPressPermit(event,'password');"
						maxlength="%{userNameLength}" disabled="%{disabledName}" /> <s:text
						name="admin.email.address.range" /></td>
				</tr>
				<s:if test="%{operation == 'edit'}">
					<tr>
						<td colspan="2" class="labelT1"><s:checkbox
							id="changePassword" name="changePassword"
							onclick="changeUserPassword(this.checked);"
							disabled="%{writeDisabled4HHMStruts}" /> <label> <s:text
							name="admin.user.changePassword" /> </label></td>
					</tr>
					<tr>
						<td colspan="2" class="labelT1" id="passwordSection">
						<table border="0" cellspacing="0" cellpadding="0">
							</s:if>
							<tr>
								<td class="labelT1" width="160"><label> <s:text
									name="admin.user.password" /><font color="red"><s:text
									name="*" /> </font> </label></td>
								<td><s:password id="adminPassword" name="adminPassword"
									size="24" maxlength="%{passwdLength}" /> <s:textfield
									id="adminPassword_text" name="adminPassword" disabled="true"
									size="24" maxlength="%{passwdLength}" cssStyle="display:none" />
								<s:text name="hm.config.start.hivemanager.password.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label> <s:text
									name="admin.user.password.confirm" /><font color="red"><s:text
									name="*" /> </font> </label></td>
								<td><s:password id="passwordConfirm" name="passwordConfirm"
									size="24" maxlength="%{passwdLength}" /> <s:textfield
									id="passwordConfirm_text" name="passwordConfirm"
									disabled="true" size="24" maxlength="%{passwdLength}"
									cssStyle="display:none" /> <s:text
									name="hm.config.start.hivemanager.password.note" /></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><s:checkbox id="chkToggleDisplay" name="ignore"
											value="true" disabled="%{writeDisable4Struts}"
											onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','passwordConfirm'],['adminPassword_text','passwordConfirm_text']);" />
										</td>
										<td><s:text name="admin.user.obscurePassword" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<s:if test="%{operation == 'edit'}">
						</table>
						</td>
					</tr>
				</s:if>
				<tr>
					<td class="labelT1"><label> <s:text
						name="admin.user.userFullName" /> </label></td>
					<td colspan="2"><s:textfield id="userFullName"
						name="dataSource.userFullName" size="48"
						maxlength="%{userFullNameLength}" /> <s:text
						name="admin.fullname.range" /></td>
				</tr>
				<tr>
					<td class="labelT1"><label> <s:text
						name="admin.timeSet.timeZone" /> </label></td>
					<td><s:select id="timezone" name="timezone"
						value="%{timezone}" list="%{enumTimeZone}" listKey="key"
						listValue="value" cssStyle="width: 365px;" /></td>
				</tr>
				<tr>
					<td class="labelT1"><label> <s:text
						name="admin.user.userGroup" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:select name="selectedId" value="%{selectedId}"
						list="userGroups" listKey="id" listValue="groupName"
						id="userGroupList" disabled="%{updateAdminUser}"
						onchange="selectUserGroup(this.options[this.selectedIndex].text)"
						cssStyle="width: 365px;" /></td>
				</tr>
				<tr style="display: <s:property value="%{showUpAccessMyHive}"/>;" id="accessMyHiveTr">
					<td colspan="2">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1">
									<s:checkbox name="dataSource.accessMyhive" value="%{dataSource.accessMyhive}"> </s:checkbox>
									<s:text name="admin.user.sub.admin.access.myhive"></s:text>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr style="display: <s:property value="%{hide4Operator}"/>;" id="operatorSection">
					<td colspan="2">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1"><s:checkbox name="updateLocalUserGroup"
								id="updateLocalUserGroup"
								onclick="selectUpdateLocalUserGroup(this.checked);" /> <label>
							<s:property value="%{localUserGroupText}"/> </label></td>
						</tr>
						<tr id="localUserGroupSection" style="display:<s:property value="%{hide4LocalUserGroup}"/>;">
							<s:push value="%{localUserGroupOptions}">
								<td class="labelT1" style="padding-left: 15px;"><tiles:insertDefinition name="optionsTransfer" /></td>
							</s:push>
						</tr>
						<tr style="display:<s:property value="%{hide4EasyMode}"/>;">
							<td class="labelT1"><s:checkbox name="updateSSIDProfile"
								id="updateSSIDProfile"
								onclick="selectUpdateSSIDProfile(this.checked);" /> <label>
							<s:text name="admin.user.limit.ssidProfile" /> </label></td>
						</tr>
						<tr id="ssidProfileSection" style="display:<s:property value="%{hide4SSIDProfile}"/>;">
							<s:push value="%{ssidProfileOptions}">
								<td class="labelT1" style="padding-left: 15px;"><tiles:insertDefinition name="optionsTransfer" /></td>
							</s:push>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
			</table>
</s:form></div>
