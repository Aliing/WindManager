<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'cwpCertMgmt';
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		
		<s:if test="%{jsonMode}">
		if(operation == 'cancel'+'<s:property value="lstForward"/>' ) {
			parent.closeIFrameDialog();
			return;
		}
		if (operation == 'create'+'<s:property value="lstForward"/>' 
				|| operation == 'update'+'<s:property value="lstForward"/>') {
			var url = "<s:url action='cwpCertMgmt' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
			YAHOO.util.Connect.setForm(document.forms[formName]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', 
					url, {success : succSaveCWPCert, failure : failSaveCWPCert, timeout: 60000}, null);
			return;
		}
		if (operation == 'importCert'
			|| operation == 'importKey'){
			document.forms[formName].parentIframeOpenFlg.value = true;
		}
		</s:if>
		
		if (operation != 'create') {
			showProcessing();
		}
	    document.forms[formName].submit();
	}
}
var succSaveCWPCert = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.succ) {
			var selectElement = parent.document.getElementById("keyFileName");
			if(selectElement) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, selectElement, false, true);
				}
			}
			parent.closeIFrameDialog();
		} else {
			var errorRow = new Object();
			errorRow.id='ErrorRow';
			if (details.error) {
				hm.util.reportFieldError(errorRow, details.msg);
			}
			return;
		}
	}catch(e){
		// do nothing now
	}	
}
var failSaveCWPCert = function(o) {}
function onLoadPage()
{
	if (document.getElementById("radio_concatenate").checked)
	{
		selectConcatenate(true);
	}
	else
	{
		selectCreateNewCert(true);
	}
	<s:if test="%{jsonMode == true}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(910, 480);
		}
	</s:if>
}

//var $ = function(o){ return typeof o == 'string'? document.getElementById(o): o};

function validate(operation) {
	
	if (operation == 'cancel'
			|| operation == 'cancel'+'<s:property value="lstForward"/>'){
		return true;
	}
	if (operation == 'importCert'
			|| operation == 'importKey'){
		return true;
	}
	
   	
	if (document.getElementById("radio_concatenate").checked)
	{
		//create cert from existing certificate and private key
		var certName = document.getElementById("certName_concate");
		if ( certName.value.length == 0) 
		{
	        hm.util.reportFieldError(certName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.certName" /></s:param></s:text>');
	        certName.focus();
	        return false;
	  	}
	  	var message = validateInput(certName.value, '<s:text name="config.cwpCert.certName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(certName, message);
	       	certName.focus();
	       	return false;
	   	}
	   	
	   	var certificateFile = document.getElementById("certificateFile");
		if ( certificateFile.value.length == 0) 
		{
	        hm.util.reportFieldError(certificateFile, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.cert" /></s:param></s:text>');
	        certificateFile.focus();
	        return false;
	  	}
	  	
	  	var password;
		var confirmPasswd;
		if (document.getElementById("chkToggleDisplay").checked)
		{
			password = document.getElementById("password");
			confirmPasswd = document.getElementById("confirmPassword");
		}
		else
		{
			password = document.getElementById("password_text");
			confirmPasswd = document.getElementById("confirmPassword_text");
		}
	  	
	  	if (confirmPasswd.value.valueOf() != password.value.valueOf() )
	  	{
	  		hm.util.reportFieldError(confirmPasswd, '<s:text name="error.notEqual"><s:param><s:text name="config.cwpCert.confirmPassword" /></s:param><s:param><s:text name="config.cwpCert.password" /></s:param></s:text>');
	        confirmPasswd.focus();
	        return false;
	  	}
		
	}
	else
	{
		//create a new self signed cert
		var certName = document.getElementById("certName_new");
		if ( certName.value.length == 0) 
		{
	        hm.util.reportFieldError(certName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.certName" /></s:param></s:text>');
	        certName.focus();
	        return false;
	  	}
	  	
	  	var message = validateInput(certName.value, '<s:text name="config.cwpCert.certName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(certName, message);
	       	certName.focus();
	       	return false;
	   	}
	   	
		var commonName = document.getElementById("commonName");
		if ( commonName.value.length == 0) 
		{
	        hm.util.reportFieldError(commonName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.commonName" /></s:param></s:text>');
	        commonName.focus();
	        return false;
	  	}
	  	var message = validateInput(commonName.value, '<s:text name="config.cwpCert.commonName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(commonName, message);
	       	commonName.focus();
	       	return false;
	   	}
	  	
	  	var orgName = document.getElementById("orgName");
		if ( orgName.value.length == 0) 
		{
	        hm.util.reportFieldError(orgName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.orgName" /></s:param></s:text>');
	        orgName.focus();
	        return false;
	  	}
	  	var message = validateInput(orgName.value, '<s:text name="config.cwpCert.orgName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(orgName, message);
	       	orgName.focus();
	       	return false;
	   	}
	  	
	  	var orgUnit = document.getElementById("orgUnit");
		if ( orgUnit.value.length == 0) 
		{
	        hm.util.reportFieldError(orgUnit, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.orgUnit" /></s:param></s:text>');
	        orgUnit.focus();
	        return false;
	  	}
	  	var message = validateInput(orgUnit.value, '<s:text name="config.cwpCert.orgUnit" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(orgUnit, message);
	       	orgUnit.focus();
	       	return false;
	   	}
	  	
	  	var localName = document.getElementById("localName");
		if ( localName.value.length == 0) 
		{
	        hm.util.reportFieldError(localName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.localName" /></s:param></s:text>');
	        localName.focus();
	        return false;
	  	}
	  	var message = validateInput(localName.value, '<s:text name="config.cwpCert.localName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(localName, message);
	       	localName.focus();
	       	return false;
	   	}
	    
	    var stateName = document.getElementById("stateName");
		if ( stateName.value.length == 0) 
		{
	        hm.util.reportFieldError(stateName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.stateName" /></s:param></s:text>');
	        stateName.focus();
	        return false;
	  	}
	  	var message = validateInput(stateName.value, '<s:text name="config.cwpCert.stateName" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(stateName, message);
	       	stateName.focus();
	       	return false;
	   	}
	  	
	  	var countryCode = document.getElementById("countryCode");
		if ( countryCode.value.length == 0) 
		{
	        hm.util.reportFieldError(countryCode, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.countryCode" /></s:param></s:text>');
	        countryCode.focus();
	        return false;
	  	}
	  	else if (countryCode.value.length != 2)
	  	{
	  		hm.util.reportFieldError(countryCode, 'Country Code need 2 characters.');
	        countryCode.focus();
	        return false;
	  	}
	  	
	  	var message = validateCountryCodeInput(countryCode.value, '<s:text name="config.cwpCert.countryCode" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(countryCode, message);
	       	countryCode.focus();
	       	return false;
	   	}

		var validity = document.getElementById("validity");
		if ( validity.value.length == 0) 
		{
	        hm.util.reportFieldError(validity, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.validity" /></s:param></s:text>');
	        validity.focus();
	        return false;
	  	}
		
		if ( validity.value > 7300) 
		{
	        hm.util.reportFieldError(validity, '<s:text name="error.notLargerThan"><s:param><s:text name="config.cwpCert.validity" /></s:param><s:param>20 years(7300 days)</s:param></s:text>');
	        validity.focus();
	        return false;
		 }
	  	
	}
	
	return true;
}

function selectConcatenate(checked)
{
	if (checked)
	{
		hm.util.show('concateSection');
		hm.util.hide('newCertSection');
	}
}

function selectCreateNewCert(checked)
{
	if (checked)
	{
		hm.util.hide('concateSection');
		hm.util.show('newCertSection');
	}
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

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="cwpCertMgmt" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		document.writeln('New </td>');
	</s:else>	
}
</script>

<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
	<s:form action="cwpCertMgmt">
	<s:if test="%{jsonMode}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg" />
	<s:hidden name="contentShowType" />
		<div style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="80%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-cerificate_Mgmt.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.cwpCertificate"/></td>
						</s:if>
						<td style="padding-left:10px;">
							<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
								<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
									alt="" class="dblk"/>
							</a>
						</td>
						
					</tr>
				</table>
				</td>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
							<s:if test="%{dataSource.id == null}">
							<td class="npcButton">
								<s:if test="%{'' == writeDisabled}">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
							</td>
							</s:if>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</div>
	</s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:else>
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
								<input type="button" name="ignore" value="Create" class="button"
									onClick="submitAction('create<s:property value="lstForward"/>');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Cancel" class="button"
									onClick="submitAction('cancel<s:property value="lstForward"/>');">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</s:else>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr id="fe_ErrorRow" style="display: none">
				<td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
			</tr>
			<tr>
				<td>
					<s:if test="%{jsonMode}">
					<table cellspacing="0" cellpadding="0" border="0"
						width="800px">
					</s:if>
					<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="800px">
					</s:else>
						<tr>
							<td height="10">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							</td>
						</tr>
						<tr>
							<td style="padding:0 0 0 5px" width="300px">
								<s:radio label="Gender" id="radio_" name="createCertWay"
									list="#{'concatenate':'Concatenate an existing certificate and private key'}"
									onclick="selectConcatenate(this.checked);"
									value="%{createCertWay}" />
							</td>
							<td style="padding:0 0 0 5px">
								<s:radio label="Gender" id="radio_" name="createCertWay"
									list="#{'new':'Create a new self-signed certificate'}"
									onclick="selectCreateNewCert(this.checked);"
									value="%{createCertWay}" />
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr style="display:''" id="concateSection">
							<td colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.certName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="certName_concate" name="certName_concate"
												onkeypress="return keyPressCheck(event);" maxlength="20" />
											<s:text name="config.cwpCert.certNameRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.cert" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:select id="certificateFile" name="certificateFile"
												cssStyle="width: 303px;" list="%{availableCaFile}"
												value="certificateFile" />
											<input type="button" value="Import" class="button short"
												onClick="submitAction('importCert');">
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.privateKey" />
											</label>
										</td>
										<td>
											<s:select id="privateKey" name="privateKeyFile"
												cssStyle="width: 303px;" list="%{availableKeyFile}"
												value="privateKeyFile" />
											<input type="button" value="Import" class="button short"
												onClick="submitAction('importKey');">
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.password" />
											</label>
										</td>
										<td>
											<s:password id="password" name="password" size="32"
												maxlength="32" />
											<s:textfield id="password_text" name="password" size="32"
												maxlength="32" cssStyle="display:none" disabled="true" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.confirmPassword" />
											</label>
										</td>
										<td>
											<s:password id="confirmPassword" name="confirmPassword"
												size="32" maxlength="32" />
											<s:textfield id="confirmPassword_text" name="confirmPassword"
												size="32" maxlength="32" cssStyle="display:none" />
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
															value="true"
															onclick="hm.util.toggleObscurePassword(this.checked,['password','confirmPassword'],['password_text','confirmPassword_text']);" />
													</td>
													<td>
														<s:text name="admin.user.obscurePassword" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;" width="155px">
											<label>
												<s:text name="config.cwpCert.description" />
											</label>
										</td>
										<td>
											<s:textfield id="description_concate" name="description_concate"
												maxlength="64" size="64" />
											<s:text name="config.cwpCert.64charsRangeExt" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr style="display:none" id="newCertSection">
							<td colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.certName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="certName_new" name="certName_new"
												onkeypress="return keyPressCheck(event);" maxlength="20" />
											<s:text name="config.cwpCert.certNameRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.description" />
											</label>
										</td>
										<td>
											<s:textfield id="description_new" name="description_new"
												maxlength="64" size="64" />
											<s:text name="config.cwpCert.64charsRangeExt" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" width="155px" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.commonName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="commonName" name="commonName" maxlength="64"
												size="64" onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.64charsRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.orgName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="orgName" name="orgName" maxlength="64"
												size="64" onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.64charsRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.orgUnit" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="orgUnit" name="orgUnit" maxlength="64"
												size="64" onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.64charsRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.localName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="localName" name="localName" maxlength="64"
												size="64" onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.64charsRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.stateName" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="stateName" name="stateName" maxlength="64"
												size="64" onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.64charsRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.countryCode" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="countryCode" name="countryCode"
												maxlength="2" size="2"
												onkeypress="return countryCodePressCheck(event);" />
											<s:text name="config.cwpCert.countryCodeRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.email" />
											</label>
										</td>
										<td>
											<s:textfield id="email" name="email" maxlength="64" size="64"
												onkeypress="return keyPressCheck(event);" />
											<s:text name="config.cwpCert.emailRange" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.validity" /><font color="red"><s:text name="*" /> </font>
											</label>
										</td>
										<td>
											<s:textfield id="validity" name="validity"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"
												maxlength="4" />
											<s:text name="config.cwpCert.days" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 15px;">
											<label>
												<s:text name="config.cwpCert.keySize" />
											</label>
										</td>
										<td>
											<s:select id="keySize" name="keySize" list="enumKeySize"
												listKey="key" listValue="value" />
											<s:text name="config.cwpCert.bytes" />
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
