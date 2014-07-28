<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'serverCSR';

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
  	
	if ( password.value.length > 0 && password.value.length < 4) 
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
  	
    if (confirmPasswd.value.valueOf() != password.value.valueOf() )
  	{
  		hm.util.reportFieldError(confirmPasswd, '<s:text name="error.notEqual"><s:param><s:text name="admin.hiveManagerCA.confirmPasswd" /></s:param><s:param><s:text name="admin.hiveManagerCA.password" /></s:param></s:text>');
        confirmPasswd.focus();
        return false;
  	}
  	
  	var csrFile = document.getElementById("fileName");
  	if (csrFile.value.length == 0)
  	{
  		hm.util.reportFieldError(csrFile, '<s:text name="error.requiredField"><s:param><s:text name="admin.serverCSR.fileName" /></s:param></s:text>');
        csrFile.focus();
        return false;
  	}
  	if (csrFile.value.indexOf(' ') > -1) {
        hm.util.reportFieldError(csrFile, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.serverCSR.fileName" /></s:param></s:text>');
        csrFile.focus();
        return false;
	}
  	//the file name can not contain '(',')'
  	if (/[\(\)]/.exec(csrFile.value)) {
  		hm.util.reportFieldError(csrFile, '<s:text name="error.name.containsInvalidChar"><s:param><s:text name="admin.serverCSR.fileName" /></s:param></s:text>');
        csrFile.focus();
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

function keyDownCheck(e,type)
{
	//disable '(,)'
	//shift+9,shift+0,shift+insert
	if (type == "filename") {  
		var disabled = {9:0,0:0};
		var shiftMod = (window.event) ? window.event.shiftKey : e.shiftKey;
		var key = (window.event) ? window.event.keyCode : e.which;
		var keyChar = String.fromCharCode(key).toLowerCase();
		return (shiftMod && (keyChar in disabled || key == 45)) ? false : true;
	}
	return true;
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

<div id="content"><s:form action="serverCSR">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="create" value="Create"
						class="button" onClick="submitAction('create');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button" onClick="submitAction('cancel');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="800">
				<tr>
					<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password"></td>
				</tr>
				<tr>
					<td class="labelT1" width="155" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.commonName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="commonName" name="commonName"
						maxlength="%{commonNameLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.64charsRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.orgName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="orgName" name="orgName"
						maxlength="%{orgNameLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.64charsRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.orgUnit" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="orgUnit" name="orgUnit"
						maxlength="%{orgUnitLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.64charsRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.localName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="localName" name="localName"
						maxlength="%{localNameLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.64charsRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.stateName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="stateName" name="stateName"
						maxlength="%{stateNameLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.64charsRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.countryCode" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="countryCode" name="countryCode"
						maxlength="%{countryCodeLength}" size="2"
						onkeypress="return countryCodePressCheck(event);" /> <s:text
						name="admin.serverCSR.countryCodeRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.email" /> </label></td>
					<td><s:textfield id="email" name="email"
						maxlength="%{emailAddrLength}" size="64"
						onkeypress="return keyPressCheck(event);" /> <s:text
						name="admin.serverCSR.emailRange" /></td>
				</tr>
				<tr>
					<td></td>
					<td class="noteInfo"><s:text
						name="admin.serverCSR.subjectAlt.note" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.subjectAlt" /> </label></td>
					<td>
					<table>
						<tr>
							<td class="labelT1" width="140px" style="padding-left: 0;"><s:text
								name="admin.serverCSR.userFQDN" /></td>
							<td><s:textfield id="altEmail" name="altEmail" size="32"
								onkeypress="return keyPressCheck(event);" maxlength="128" /> <s:text
								name="admin.serverCSR.subjectAlt.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0;"><s:text name="admin.serverCSR.FQDN" /></td>
							<td><s:textfield id="altDNS" name="altDNS" size="32"
								onkeypress="return keyPressCheck(event);" maxlength="128" /> <s:text
								name="admin.serverCSR.subjectAlt.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0;"><s:text name="admin.serverCSR.ip" /></td>
							<td><s:textfield id="altIP" name="altIP" size="32"
								onkeypress="return hm.util.keyPressPermit(event,'ip');"
								maxlength="128" /> <s:text
								name="admin.serverCSR.subjectAlt.range" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.keySize" /> </label></td>
					<td><s:select id="keySize" name="keySize" value="%{keySize}"
						list="enumKeySize" listKey="key" listValue="value" /> <s:text
						name="admin.serverCSR.bytes" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.password" /> </label></td>
					<td><s:password id="password" name="password" size="20"
						maxlength="%{passwordLength}"
						onkeypress="return hm.util.keyPressPermit(event,'password');" />
					<s:textfield id="password_text" name="password" size="20"
						maxlength="%{passwordLength}" cssStyle="display:none"
						disabled="true"
						onkeypress="return hm.util.keyPressPermit(event,'password');" />
					<s:text name="admin.serverCSR.passwordRange" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.confirmPasswd" /> </label></td>
					<td><s:password id="confirmPasswd" name="confirmPasswd"
						size="20" maxlength="%{passwordLength}"
						onkeypress="return hm.util.keyPressPermit(event,'password');" />
					<s:textfield id="confirmPasswd_text" name="confirmPasswd" size="20"
						maxlength="%{passwordLength}" cssStyle="display:none"
						onkeypress="return hm.util.keyPressPermit(event,'password');" />
					<s:text name="admin.serverCSR.passwordRange" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><s:checkbox id="chkToggleDisplay" name="ignore"
								value="true"
								onclick="hm.util.toggleObscurePassword(this.checked,['password','confirmPasswd'],['password_text','confirmPasswd_text']);" />
							</td>
							<td><s:text name="admin.user.obscurePassword" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px;"><label>
					<s:text name="admin.serverCSR.fileName" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:textfield id="fileName" name="fileName" size="20"
						maxlength="%{fileNameLength}" onkeydown="return keyDownCheck(event,'filename');"/> <s:text
						name="admin.serverCSR.fileNameRange" /></td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
