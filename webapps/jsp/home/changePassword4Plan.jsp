<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script type="text/javascript">

var formName = 'changePassword4Plan';
var userNameForCheck;

YAHOO.util.Event.addListener(window, "load", function() {
	<s:if test="%{null != userContext.getCustomerId() && '' != userContext.getCustomerId()}">
		userNameForCheck = '<s:property value="%{userContext.getUserFullName()}"/>';
	</s:if>
	<s:else>
		userNameForCheck = '<s:property value="%{userContext.getUserName()}"/>';
	</s:else>
});

function submitAction(operation)
{
	document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
}


function showHangMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteInfo";
	hm.util.show("noteSection");
}

function showNormalMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteInfo";
	hm.util.show("noteSection");
	notesTimeoutId = setTimeout("hm.util.wipeOut('noteSection', 800)", 10 * 1000)
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

function showErrorMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteError";
	hm.util.show("noteSection");
}

function validate()
{
	var oldPassword;
	var newPassword;
	var confirmPassword;
	if (document.getElementById("chkToggleDisplay").checked)
	{
		oldPassword=document.getElementById("passwordOld");
		newPassword=document.getElementById("passwordNew");
		confirmPassword=document.getElementById("passwordConfirm");
	}
	else
	{
		oldPassword=document.getElementById("passwordOld_text");
		newPassword=document.getElementById("passwordNew_text");
		confirmPassword=document.getElementById("passwordConfirm_text");
	}

	if(oldPassword.value.length==0)
	{
	    hm.util.reportFieldError(oldPassword, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password.old" /></s:param></s:text>');
        oldPassword.focus();
        return false;
	}

	if(newPassword.value.length==0)
	{
	    hm.util.reportFieldError(newPassword, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password.new" /></s:param></s:text>');
        newPassword.focus();
        return false;
	}

	return hm.util.validateUserNewPasswordFormat(newPassword, confirmPassword, '<s:text name="admin.user.password.new" />',
			'<s:text name="admin.user.password.confirm" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />', userNameForCheck);
}

function saveChanges()
{
	if (validate())
	{
		document.forms[formName].operation.value = 'changPassword';
		ajaxRequest(formName,"<s:url action='userPasswordModify' includeParams='none' />", changPasswordResult, 'POST');

		if(top.waitingPanel != null)
		{
			top.waitingPanel.show();
		}
	}
}

function changPasswordResult(o)
{
	if(top.waitingPanel != null)
	{
		top.waitingPanel.hide();
	}
	
	eval("var result = " + o.responseText);
	if (result.success)
	{
		showHangMessage(result.message);
	} 
	else {
		showErrorMessage(result.message);
		
	}
}

function selectFilterSetting(checked)
{
	if (checked)
	{
		hm.util.show('captureFilterSection');
	}
	else
	{
		hm.util.hide('captureFilterSection');
	}
}

function initNoteSection()
{
	hm.util.hide('downloadSection');
	hm.util.hide('noteSection');
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div><s:form action="userPasswordModify" id="changePassword4Plan"
	name="changePassword4Plan">
	<s:hidden name="operation" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" id="ignore" name="ignore"
						value="Save" class="button" onClick="saveChanges();"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding-left: 5px;">
			<div id="noteSection" style="display: none">
			<table width="400px" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td id="noteTD"></td>
					<td class="buttons">
					<div id="downloadSection" style="display: none">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" id="downloadBtn" name="ignore"
								value="Download" class="button"
								onClick="submitAction('downloadCapture');"></td>
							<td><input type="button" id="cancelBtn" name="ignore"
								value="Cancel" class="button" onClick="initNoteSection();">
							</td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="100%">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td class="labelT1" width="160"><label> <s:text
						name="admin.user.password.old" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:password id="passwordOld" name="passwordOld" size="24"
						maxlength="%{passwdLength}" /> <s:textfield id="passwordOld_text"
						name="passwordOld" size="24" maxlength="%{passwdLength}"
						cssStyle="display:none" disabled="true" /> <s:text
						name="admin.user.password.ranger" /></td>
				</tr>
				<tr>
					<td class="labelT1" width="160"><label> <s:text
						name="admin.user.password.new" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:password id="passwordNew" name="passwordNew" size="24"
						maxlength="%{passwdLength}" /> <s:textfield id="passwordNew_text"
						name="passwordNew" size="24" maxlength="%{passwdLength}"
						cssStyle="display:none" disabled="true" /> <s:text
						name="hm.config.start.hivemanager.password.note" /></td>
				</tr>
				<tr>
					<td class="labelT1" width="160"><label> <s:text
						name="admin.user.password.confirm" /><font color="red"><s:text
						name="*" /> </font> </label></td>
					<td><s:password id="passwordConfirm" name="passwordConfirm"
						size="24" maxlength="%{passwdLength}" /> <s:textfield
						id="passwordConfirm_text" name="passwordConfirm" size="24"
						maxlength="%{passwdLength}" cssStyle="display:none"
						disabled="true" /> <s:text name="hm.config.start.hivemanager.password.note" />
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><s:checkbox id="chkToggleDisplay" name="ignore"
								value="true"
								onclick="hm.util.toggleObscurePassword(this.checked,['passwordOld','passwordNew','passwordConfirm'],['passwordOld_text','passwordNew_text','passwordConfirm_text']);" />
							</td>
							<td><s:text name="admin.user.obscurePassword" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
