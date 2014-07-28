<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'hiveManagerCA';

function submitAction(operation) {
	if (validate(operation)) {
			
		if (operation == 'create')
		{
			showProcessing();
		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) 
{
	if (operation != 'create')
	{
		return true;
	}
    
    var commonName = document.getElementById("commonName");
	if ( commonName.value.length == 0) 
	{
        hm.util.reportFieldError(commonName, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.commonName" /></s:param></s:text>');
        commonName.focus();
        return false;
  	}
  	var message = validateInput(commonName.value, '<s:text name="admin.hiveManagerCA.commonName" />');
   	if (message != null) {
   		hm.util.reportFieldError(commonName, message);
       	commonName.focus();
       	return false;
   	}
  	
  	var orgName = document.getElementById("orgName");
	if ( orgName.value.length == 0) 
	{
        hm.util.reportFieldError(orgName, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.orgName" /></s:param></s:text>');
        orgName.focus();
        return false;
  	}
  	var message = validateInput(orgName.value, '<s:text name="admin.hiveManagerCA.orgName" />');
   	if (message != null) {
   		hm.util.reportFieldError(orgName, message);
       	orgName.focus();
       	return false;
   	}
  	
  	
  	var orgUnit = document.getElementById("orgUnit");
	if ( orgUnit.value.length == 0) 
	{
        hm.util.reportFieldError(orgUnit, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.orgUnit" /></s:param></s:text>');
        orgUnit.focus();
        return false;
  	}
  	var message = validateInput(orgUnit.value, '<s:text name="admin.hiveManagerCA.orgUnit" />');
   	if (message != null) {
   		hm.util.reportFieldError(orgUnit, message);
       	orgUnit.focus();
       	return false;
   	}
  	
  	var localName = document.getElementById("localName");
	if ( localName.value.length == 0) 
	{
        hm.util.reportFieldError(localName, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.localName" /></s:param></s:text>');
        localName.focus();
        return false;
  	}
  	var message = validateInput(localName.value, '<s:text name="admin.hiveManagerCA.localName" />');
   	if (message != null) {
   		hm.util.reportFieldError(localName, message);
       	localName.focus();
       	return false;
   	}
    
    var stateName = document.getElementById("stateName");
	if ( stateName.value.length == 0) 
	{
        hm.util.reportFieldError(stateName, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.stateName" /></s:param></s:text>');
        stateName.focus();
        return false;
  	}
  	var message = validateInput(stateName.value, '<s:text name="admin.hiveManagerCA.stateName" />');
   	if (message != null) {
   		hm.util.reportFieldError(stateName, message);
       	stateName.focus();
       	return false;
   	}
  	
  	var countryCode = document.getElementById("countryCode");
	if ( countryCode.value.length == 0) 
	{
        hm.util.reportFieldError(countryCode, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.countryCode" /></s:param></s:text>');
        countryCode.focus();
        return false;
  	}
  	else if (countryCode.value.length != 2)
  	{
  		hm.util.reportFieldError(countryCode, 'Country Code need 2 characters.');
        countryCode.focus();
        return false;
  	}
  	var message = validateCountryCodeInput(countryCode.value, '<s:text name="admin.hiveManagerCA.countryCode" />');
   	if (message != null) {
   		hm.util.reportFieldError(countryCode, message);
       	countryCode.focus();
       	return false;
   	}
  	
	var validity = document.getElementById("validity");
	if ( validity.value.length == 0) 
	{
        hm.util.reportFieldError(validity, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.validity" /></s:param></s:text>');
        validity.focus();
        return false;
  	}
	if ( validity.value > 7300) 
	{
		hm.util.reportFieldError(validity, '<s:text name="error.notLargerThan"><s:param><s:text name="admin.hiveManagerCA.validity" /></s:param><s:param>20 years(7300 days)</s:param></s:text>');
		validity.focus();
		return false;
	 }	
  	
  	var password;
	var confirmPasswd;
	if (document.getElementById("chkToggleDisplay").checked)
	{
		password = document.getElementById("password");
		confirmPasswd = document.getElementById("confirmPasswd");
	}
	else
	{
		password = document.getElementById("password_text");
		confirmPasswd = document.getElementById("confirmPasswd_text");
	}
  	
	if ( password.value.length < 4) 
	{
        hm.util.reportFieldError(password, '<s:text name="error.keyLengthRange"><s:param><s:text name="admin.hiveManagerCA.password" /></s:param><s:param><s:text name="admin.hiveManagerCA.passwordRange" /></s:param></s:text>');
        password.focus();
        return false;
  	}
  	
  	if (password.value.indexOf(' ') > -1) {
        hm.util.reportFieldError(password, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.hiveManagerCA.password" /></s:param></s:text>');
        password.focus();
        return false;
	}
  	
    if ( confirmPasswd.value.length == 0) 
	{
        hm.util.reportFieldError(confirmPasswd, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.confirmPasswd" /></s:param></s:text>');
        confirmPasswd.focus();
        return false;
  	}
  	else if (confirmPasswd.value.valueOf() != password.value.valueOf() )
  	{
  		hm.util.reportFieldError(confirmPasswd, '<s:text name="error.notEqual"><s:param><s:text name="admin.hiveManagerCA.confirmPasswd" /></s:param><s:param><s:text name="admin.hiveManagerCA.password" /></s:param></s:text>');
        confirmPasswd.focus();
        return false;
  	}
    
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function validateInput(value,label)
{
	for(var i = 0; i< value.length; i++){
		var charCode = value.charCodeAt(i);
		if(!((charCode >= 32 && charCode <= 34) || (128 > charCode && charCode > 36)))
		{
			return label + " cannot contain '" + String.fromCharCode(charCode) + "'.";
		}
	}
}

function validateCountryCodeInput(value,label)
{
	for(var i = 0; i< value.length; i++){
		var charCode = value.charCodeAt(i);
		if(!((charCode >= 65 && charCode <= 90) || (charCode >= 97 && charCode <=122)))
		{
			return label + " cannot contain '" + String.fromCharCode(charCode) + "'.";
		}
	}
}

function keyPressCheck(e)
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
	
	if(keycode != 35 && keycode != 36)
	{
		return true;
	}
	
	return false;
}

function countryCodePressCheck(e)
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
	
	if (keycode >= 65 && keycode <=90)
	{
		return true;
	}
	
	if (keycode >= 97 && keycode <=122)
	{
		return true;
	}
	
	return false;
}

</script>

<div id="content">
	<s:form action="hiveManagerCA">
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
							<td>
								<input type="button" name="create" value="Create" class="button"
									onClick="submitAction('create');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Cancel" class="button"
									onClick="submitAction('cancel');">
							</td>
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
				<td style="padding-top: 5px;">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="800">
						<tr>
							<td height="10">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="155" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.commonName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="commonName" name="commonName"
									maxlength="%{commonNameLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.64charsRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.orgName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="orgName" name="orgName"
									maxlength="%{orgNameLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.64charsRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.orgUnit" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="orgUnit" name="orgUnit"
									maxlength="%{orgUnitLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.64charsRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.localName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="localName" name="localName"
									maxlength="%{localNameLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.64charsRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.stateName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="stateName" name="stateName"
									maxlength="%{stateNameLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.64charsRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.countryCode" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="countryCode" name="countryCode"
									maxlength="%{countryCodeLength}" size="2"
									onkeypress="return countryCodePressCheck(event);" />
								<s:text name="admin.hiveManagerCA.countryCodeRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.email" />
								</label>
							</td>
							<td>
								<s:textfield id="email" name="email"
									maxlength="%{emailAddrLength}" size="64"
									onkeypress="return keyPressCheck(event);" />
								<s:text name="admin.hiveManagerCA.emailRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.validity" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="validity" name="validity"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="%{validityLength}" />
								<s:text name="admin.hiveManagerCA.days" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.keySize" />
								</label>
							</td>
							<td>
								<s:select id="keySize" name="keySize" value="%{keySize}"
									list="enumKeySize" listKey="key" listValue="value" />
								<s:text name="admin.hiveManagerCA.bytes" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.password" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:password id="password" name="password" size="20"
									maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');" />
								<s:textfield id="password_text" name="password" size="20"
									maxlength="%{passwordLength}" cssStyle="display:none"
									disabled="true"
									onkeypress="return hm.util.keyPressPermit(event,'password');" />
								<s:text name="admin.hiveManagerCA.passwordRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.hiveManagerCA.confirmPasswd" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:password id="confirmPasswd" name="confirmPasswd" size="20"
									maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');" />
								<s:textfield id="confirmPasswd_text" name="confirmPasswd"
									size="20" maxlength="%{passwordLength}"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									cssStyle="display:none" disabled="true" />
								<s:text name="admin.hiveManagerCA.passwordRange" />
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
											<s:checkbox id="chkToggleDisplay" name="ignore" value="true"
												onclick="hm.util.toggleObscurePassword(this.checked,['password','confirmPasswd'],['password_text','confirmPasswd_text']);" />
										</td>
										<td>
											<s:text name="admin.user.obscurePassword" />
										</td>
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
	</s:form>
</div>
