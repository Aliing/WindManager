<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script src="<s:url value="/js/hm.options.js" />"></script>

<script>
var formName = 'radiusUserProfileRule';

function onLoadPage() {
	if (!document.getElementById("radiusUserProfileRuleName").disabled) {
		document.getElementById("radiusUserProfileRuleName").focus();
	}
}

function submitAction(operation) {

    if(validate(operation)){
        if (operation != 'create' &&
        		operation != 'newUserProfile') {
			showProcessing();
		}
		
        document.radiusUserProfileRule.operation.value = operation;
        hm.options.selectAllOptions('selectUserProfile');
	    document.forms[formName].submit();
    }	
}

function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_RADIUS_USER_PROFILE_RULE%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}
	
	if((operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create')  && !validateName()) {
	    return false;
	}

	if(!validateActionTime())
		return false;
		
	return true;
}

function validateName() {
	var inputElement = document.getElementById("radiusUserProfileRuleName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.radiusUserProfileRule.name" />');
    
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
   }  
   
   return true;
}

function validateActionTime() {
	var inputElement = document.getElementById("actionTime");
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusUserProfileRule.actionTime" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, 
    											'<s:text name="config.radiusUserProfileRule.actionTime" />', 
    											<s:property value="%{actionTimeRange.min()}" />,
		                                        <s:property value="%{actionTimeRange.max()}" />);
		                                        
	if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
   
   return true;
}

function changeDenyAction(select) {
	if(select.value == 1) {
		document.getElementById("actionTime").disabled = false;
	} else {
		document.getElementById("actionTime").value = 60;
		document.getElementById("actionTime").disabled = true;
	}
}

function setAllUserProfilePermitted(checked) {
	if(checked)
		document.getElementById("permitUserProfile").style.display = "none";
	else
		document.getElementById("permitUserProfile").style.display = "";	
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="radiusUserProfileRule" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
	</s:else>
}
</script>

<div>
<s:form action="radiusUserProfileRule">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><tiles:insertDefinition name="context" /></td>
	</tr>
	<tr>
		<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td>
							<input type="button" name="ignore" value="<s:text name="button.create"/>" class="button" onClick="submitAction('create<s:property value="lstForward" />');" <s:property value="writeDisabled" /> >
						</td>
					</s:if>
					<s:else>
						<td>
							<input type="button" name="ignore" value="<s:text name="button.update"/>" class="button" onClick="submitAction('update');" <s:property value="updateDisabled" /> >
						</td>
					</s:else>
					<td>
						<input type="button" name="ignore" value="Cancel" class="button" onClick="submitAction('cancel<s:property value="lstForward"/>');">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><tiles:insertDefinition name="notes" /></td>
	</tr>
	<tr>
		<td stype="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="500">
				<tr>
					<td style="padding: 10px 5px 6px 5px;" width="100%">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="80">
									<label><s:text name="config.radiusUserProfileRule.name" /><font color="red"><s:text name="*"/></font></label>
								</td>
								<td>
									<s:textfield name="dataSource.radiusUserProfileRuleName" id="radiusUserProfileRuleName" size="24" maxlength="%{nameLength}" disabled="%{disabledName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" /> <s:text name="config.name.range"/>
								</td>
							</tr>
							<tr>
								<td class="labelT1" width="80">
									<label><s:text name="config.radiusUserProfileRule.description" /></label>
								</td>
								<td colspan="4">
									<s:textfield name="dataSource.description" size="48" maxlength="%{descriptionLength}" /> <s:text name="config.description.range"/>
								</td>
							</tr>
							<tr>
								<td class="labelT1" width="80">
									<label><s:text name="config.radiusUserProfileRule.denyAction" /></label>
								</td>
								<td>
									<s:select name="dataSource.denyAction"	value="%{dataSource.denyAction}" list="%{enumDenyAction}" listKey="key"
										listValue="value" cssStyle="width: 108px;" onchange="changeDenyAction(this)"/>
								</td>
							</tr>
							<tr>
								<td class="labelT1" width="80">
									<label><s:text name="config.radiusUserProfileRule.actionTime" /></label>
								</td>
								<td>
									<s:textfield name="dataSource.actionTime" id="actionTime" maxlength="9" size="15"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled="%{actionTimeDisabled}" /> <s:text name="config.radiusUserProfileRule.actionTimeRange" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:0px 4px 0px 4px" colspan="3">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine">
									<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding: 0px 5px 0px 10px;" width="100%">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td style="padding:6px 0px 0px 0px;">
									<s:checkbox name="dataSource.strict" value="%{dataSource.strict}" /> <s:text name="config.radiusUserProfileRule.strict"/>
								</td>
							</tr>
							<tr>
								<td style="padding:4px 0px 4px 0px;">
									<s:checkbox name="dataSource.allUserProfilesPermitted" value="%{dataSource.allUserProfilesPermitted}" onclick="setAllUserProfilePermitted(this.checked);"/>
										<s:text name="config.radiusUserProfileRule.allPermitted"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="permitUserProfile" style="display:<s:property value="%{selectPermitted}" />">
					<td style="padding:4px 130px 0 15px;">
					 	<fieldset>
							<legend><s:text name="config.radiusUserProfileRule.fieldset" /></legend>  
							<div>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr id="upOption">
										<td height="5" />
									</tr>
									<tr>
										<s:push value="%{userProfileOptions}">
											<td colspan="3">
												<tiles:insertDefinition	name="optionsTransfer"/>
											</td>
										</s:push>
									</tr>
									<tr>
										<td height="5" />
									</tr>
									<tr>
										<td style="padding-left:43px;">
											<input type="button" name="newUserProfile" value="New" class="button short" onClick="submitAction('newUserProfile');" <s:property value="writeDisabled" />>
										</td>
									</tr>
								</table>
							</div>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</s:form>
</div>
