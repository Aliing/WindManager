<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
span.userRegiInfoNote {
	font-family: Arial, Helvetica, Verdana sans-serif;
	font-size: 14px;
	font-weight: bold;
	color: #003366;
}
</style>

<script type="text/javascript">
var formUserRegName = 'licenseMgr';

function submitUserRegInfoAction() {
	if (validateUserRegInfo()) {
		<s:if test="%{'licenseMgr' == selectedL2Feature.key}">
			Get(formName + "_userRegInfoStyle").value = Get("userRegInfoDiv").style.display;
		</s:if>
		document.forms[formUserRegName].operation.value = 'submitUserRegInfo';
    	document.forms[formUserRegName].submit();
    	<s:if test="%{'licenseMgr' != selectedL2Feature.key}">
			closeUserRegInfoWarningPanel();
		</s:if>
	}
}

function validateUserRegInfo() {
	if (!checkUserRegInputLength(document.getElementById(formUserRegName + "_userRegInfo_company"), '<s:text name="admin.license.send.userReg.info.company" />', 3)) {
		return false;
	}
	
	// check the country
	var stateEle = document.getElementById(formUserRegName + "_userRegInfo_country");
	if (stateEle.selectedIndex == 0) {
		hm.util.reportFieldError(stateEle, '<s:text name="error.pleaseSelect"><s:param><s:text name="admin.license.send.userReg.info.country" /></s:param></s:text>');
		stateEle.focus();
		return false;
	}
	
	if (!checkUserRegInputLength(document.getElementById(formUserRegName + "_userRegInfo_name"), '<s:text name="admin.license.send.userReg.info.user" />', 3)) {
		return false;
	}
	if (!checkUserRegEmailAddress(document.getElementById(formUserRegName + "_userRegInfo_email"), '<s:text name="admin.license.send.userReg.info.user.email" />')) {
		return false;
	}
	
	var phone = document.getElementById(formUserRegName + "_userRegInfo_telephone");
	if (!checkUserRegInputLength(phone, '<s:text name="admin.license.send.userReg.info.user.phone" />', 3)) {
		return false;
	}
	return true;
}

function checkUserRegInputLength(element, title, flag) {
	if (element.value.length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		element.focus();
		return false;
	}
	var message;
	switch (flag) {
		case 2:
			message = hm.util.validateName(element.value, title);
			break;
		case 3:
			message = hm.util.validateSsid(element.value, title);
			break;
	}
   	if (message != null) {
   		hm.util.reportFieldError(element, message);
       	element.focus();
       	return false;
   	}
	return true;
}

function checkUserRegEmailAddress(element, title){
	if (!checkUserRegInputLength(element, title, 2)) {
		return false;
	}
	if (!hm.util.validateEmail(element.value.trim())) {
		hm.util.reportFieldError(element, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		element.focus();
		return false;
	}
	return true;
}
</script>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td style="padding:2px 20px 2px 5px">
					<span class="userRegiInfoNote"><s:text name="admin.license.send.userReg.info.title"></s:text></span>
				</td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td class="labelT1" width="120"><label><s:text name="admin.license.send.userReg.info.company" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="userRegInfo.company" size="36" maxlength="64"
								onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
								<s:text name="config.radiusOnHiveAp.passRange" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.country" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:select name="userRegInfo.country" list="%{strCountry}" listKey="value"
									listValue="value" cssStyle="width: 280px" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.address1" /></label></td>
							<td><s:textfield name="userRegInfo.addressLine1" onkeypress="return hm.util.keyPressPermit(event,'ssid');"
								size="36" maxlength="256" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.address2" /></label></td>
							<td><s:textfield name="userRegInfo.addressLine2" onkeypress="return hm.util.keyPressPermit(event,'ssid');"
								size="36" maxlength="256" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.zip" /></label></td>
							<td><s:textfield name="userRegInfo.postalCode" onkeypress="return hm.util.keyPressPermit(event,'ssid');"
								size="36" maxlength="32" />
								<s:text name="config.name.range" /></td>
						</tr>
						<tr>
							<td height="20px"></td>
						</tr>
						<tr>
							<td colspan="2"><s:text name="admin.license.send.userReg.info.contact.title" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.user" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="userRegInfo.name" onkeypress="return hm.util.keyPressPermit(event,'ssid');"
								size="36" maxlength="32" />
								<s:text name="config.radiusOnHiveAp.nameRange1" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.user.email" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="userRegInfo.email" onkeypress="return hm.util.keyPressPermit(event,'name');"
								size="36" maxlength="64" />
								<s:text name="config.radiusOnHiveAp.passRange" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text name="admin.license.send.userReg.info.user.phone" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="userRegInfo.telephone" onkeypress="return hm.util.keyPressPermit(event,'ssid');"
								 size="36" maxlength="32" />
								<s:text name="config.radiusOnHiveAp.nameRange1" /></td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><input type="button" name="save" value="Submit" class="button"
											onClick="submitUserRegInfoAction();"></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>