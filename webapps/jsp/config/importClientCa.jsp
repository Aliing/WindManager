<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
	var formName = "clientCaMgmt";
	
	function submitAction(operation) {
		if(validate(operation)){
			document.forms[formName].operation.value = operation;
			document.forms[formName].submit();
		}
	}

	function insertPageContext() {
		document
				.writeln('<td class="crumb" nowrap><a href="<s:url action="clientCaMgmt" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		document.writeln('Update </td>');
	}
	
	function validate(operation){
		if('return' == operation ||
				'importCert' == operation ||
				'importKey' == operation){
			return true;
		}
		
		 var ca = document.getElementById("selCerts");
		if(ca.value.length == 0){
			hm.util.reportFieldError(ca,'<s:text name="home.hmSettings.clientManagement.useCustomerCA.caRequired"></s:text>');
			ca.focus();
			return false;
		}
		
		var key = document.getElementById("selKeys");
		if(key.value.length == 0){
			hm.util.reportFieldError(key,'<s:text name="home.hmSettings.clientManagement.useCustomerCA.caKeyRequired"></s:text>');
			ca.focus();
			return false;
		} 
		var pw;
		var conpw;
		if(document.getElementById("chkToggle_ca").checked){
			pw = document.getElementById("password");
			conpw = document.getElementById("confPassword");
		}else{
			pw = document.getElementById("passwordText");
			conpw = document.getElementById("confPasswordText");
		}
		if(pw.value.length == 0 && conpw.value.length == 0){
			return true;
		}
		if(pw.value.length < 4){
			hm.util.reportFieldError(password, '<s:text name="error.keyLengthRange"><s:param><s:text name="admin.hiveManagerCA.password" /></s:param><s:param><s:text name="admin.hiveManagerCA.passwordRange" /></s:param></s:text>');
		    password.focus();
		    return false;
		}
		if (pw.value.indexOf(' ') > -1) {
	        hm.util.reportFieldError(pw, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.hiveManagerCA.password" /></s:param></s:text>');
	        password.focus();
	        return false;
		}
		if (conpw.value.length == 0){
		     hm.util.reportFieldError(conpw, '<s:text name="error.requiredField"><s:param><s:text name="admin.hiveManagerCA.confirmPasswd" /></s:param></s:text>');
		     confirmPasswd.focus();
		     return false;
		}
		if(pw.value.valueOf() != conpw.value.valueOf()){
			hm.util.reportFieldError(conpw, '<s:text name="error.notEqual"><s:param><s:text name="admin.hiveManagerCA.confirmPasswd" /></s:param><s:param><s:text name="admin.hiveManagerCA.password" /></s:param></s:text>');
		    confirmPasswd.focus();
		    return false;
		}
		
		return true;
	}
</script>
<!--  <enctype="multipart/form-data" method="POST"> -->
<div id="content">
	<s:form action="clientCaMgmt">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="update" value="Update"
								class="button" onclick="submitAction('updateCa');" /></td>
							<td><input type="button" name="return" value="Return"
								class="button" onclick="submitAction('return');" /></td>
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
				<td style="padding-top: 5px">
					<table class="editBox" cellspacing="0" cellpadding="0"
						width="600px">
						<tr>
							<td height="10" />
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label> <s:text name="admin.clientCa.certificate" />
							</label>
							</td>
							<td>
							<%-- <s:file id="caFile" name="upload" accept="application/x-x509-ca-cert" size="60" /> --%>
							<s:select id="selCerts" list="%{certs}" name="selCert" cssStyle="width:240px" value="selCert"></s:select>
							<input type="button" name="import" value="Import" class="button short" onclick="submitAction('importCert');" />
						   </td>
						</tr>
						<tr>
							<td></td>
							<td><font color="blue"><s:text
										name="home.hmSettings.clientManagement.useCustomerCA.note"></s:text></font>
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label> <s:text name="admin.clientCa.prvKey" />
							</label>
							</td>
							<td>
							<%-- <s:file id="caKeyFile" name="upload" accept="application/x-x509-ca-cert" size="60" /> --%>
									<s:select id="selKeys" list="%{certs}" name="selKey" cssStyle="width:240px" value="selKey"></s:select>
									<input type="button" name="import" value="Import" class="button short" onclick="submitAction('importKey');" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td><font color="blue"><s:text
										name="home.hmSettings.clientManagement.useCustomerKey.note"></s:text></font>
							</td>
						</tr>
						<tr style="display:none">
						    <td />
						    <td>
						         <s:textfield id="cont" autocomplete="off"></s:textfield> 
						    </td>
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label> <s:text name="admin.clientCa.password" />
							</label>
							</td>
							<td>
							    <s:password id="password" name="password" size="24" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
								<s:textfield id="passwordText" name="passwordText" size="24" cssStyle="display:none" disabled="true" 
								onkeypress="return hm.util.keyPressPermit(event,'password');"/>
								<s:text name="admin.hiveManagerCA.passwordRange" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label> <s:text name="admin.clientCa.confPassword" />
							</label>
							</td>
							<td><s:password id="confPassword" name="confPassword" size="24" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
							    <s:textfield id="confPasswordText" name="confPasswordText" cssStyle="display:none" disabled="true" size="24" 
							    onkeypress="return hm.util.keyPressPermit(event,'password');"/>
								<s:text name="admin.hiveManagerCA.passwordRange" /></td>
						</tr>
						<tr>
							<td></td>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><s:checkbox id="chkToggle_ca" name="ignore"
												value="true" 
												onclick="hm.util.toggleObscurePassword(this.checked,['password','confPassword'],['passwordText','confPasswordText']);" />
										</td>
										<td><s:text name="admin.user.obscurePassword" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10" />
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>