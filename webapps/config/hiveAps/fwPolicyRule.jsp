<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
	var formName2 = 'fwPolicyRuleForm';
	var thisOperation;
	
	function submitAction(operation) {
		thisOperation = operation;
		Get("resultInfoPanel").innerHTML = "";
		if (operation == 'fwPolicyRuleMgr') {
			var confirmText = '';
			if (document.getElementById(formName2 + '_fwPolicyRuleOpertiondisable').checked) {
				confirmText = 'All firewall policy rules will be disabled.';
			} else if (document.getElementById(formName2 + '_fwPolicyRuleOpertionenable').checked) {
				confirmText = 'All firewall policy rules will be enabled.';
			} else {
				warnDialog.cfg.setProperty('text', 'Please select an operation.');
				warnDialog.show();
				return;
			}
			confirmDialog.cfg.setProperty('text', "<html><body>"+confirmText+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
			confirmDialog.show();
		}
	}
	
	function doContinueOper() {
		if (thisOperation == 'fwPolicyRuleMgr') {
			setFirewallPolicyRuleSetting();
		}
	}
	
	var setFirewallPolicyRuleSetting = function() {
		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
		document.forms[formName2].operation.value = "fwPolicyRuleMgr";
		ajaxRequest(formName2, url, succSetFwPolicyRule);
	}
	
	var succSetFwPolicyRule = function(o) {
		eval("var result = " + o.responseText);
		if (result.r == true) {
			hm.util.replaceChildren(Get("resultInfoPanel"), document.createTextNode(result.m));
			Get("resultInfoPanel").className = "noteInfo";
			Get("noteTable").className = "note";
		} else {
			hm.util.replaceChildren(Get("resultInfoPanel"), document.createTextNode(result.m));
			Get("noteTable").className = "noteError";
		}
	}
</script>
<div>
<s:form action="hiveApToolkit" id="fwPolicyRuleForm" name="fwPolicyRuleForm">
	<s:hidden name="operation" />
	<s:hidden name="hiveApId" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" id="noteTable">
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td id="resultInfoPanel"></td>
					</tr>
					<tr>
						<td height="5"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="100%">
					<tr>
						<td>
							<div id="fwPolicyRuleOpSelection" style="margin: 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td width="250px"><s:radio 
												label="Gender" name="fwPolicyRuleOpertion" 
												list="%{fwPolicyRuleDisableOption}" 
												onclick="this.blur();" 
												listKey="key" listValue="value" /></td>
									</tr>
									<tr><td height="10px"></td></tr>
									<tr>
										<td width="250px"><s:radio 
												label="Gender" name="fwPolicyRuleOpertion" 
												list="%{fwPolicyRuleEnableOption}" 
												onclick="this.blur();"
												listKey="key" listValue="value" /></td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td height="20px"></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" align="center">
					<tr>
						<td><input type="button" name="ignore" value="Submit"
							class="button" onClick="submitAction('fwPolicyRuleMgr');">
						</td>
					</tr>
				</table></td>
		</tr>
	</table>
</s:form>
</div>