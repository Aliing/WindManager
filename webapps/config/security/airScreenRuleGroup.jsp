<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script src="<s:url value="/js/hm.options.js" />"></script>

<script type="text/javascript">
var formName = 'airScreenRuleGroups';

var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

function submitAction(operation) {
	thisOperation = operation;

	if (validate(operation)) {
		doContinueOper();
	}
}

function doContinueOper() {
	showProcessing();
	document.forms[formName].operation.value = thisOperation;
	hm.options.selectAllOptions('rules');
    document.forms[formName].submit();
}

function validate(operation) {
	if(operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	if(operation == "newRule" || operation == "editRule"){
		if(operation == "editRule"){
			var value = hm.util.validateOptionTransferSelection("rules");
			if(value < 0){
				return false;
			}else{
				document.forms[formName].rule.value = value;
			}
		}
		return true;
	}
	if(!validateProfileName()){
		return false;
	}
	if(!validateRuleCount()){
		return false;
	}
	
	return true;
}

function validateRuleCount(){
	var selectedRuleEl = Get("rules");
	var displayEl = Get("ruleInfoEl");
	if(selectedRuleEl){
		var count = selectedRuleEl.length;
		if(count == 0){
			hm.util.reportFieldError(displayEl, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.rule.fullName" /></s:param></s:text>');
			return false;
		}else if(count > 8){
			hm.util.reportFieldError(displayEl, '<s:text name="error.airscreen.overflow.rules" />');
			return false;
		}
	}
	return true;
}

function validateProfileName() {
    var inputElement = Get(formName + "_dataSource_profileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.air.screen.rule.group.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function onLoadPage() {
	if(Get(formName + "_dataSource_profileName").disabled == false){
		Get(formName + "_dataSource_profileName").focus();
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="airScreenRuleGroups" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>

<div id="content"><s:form action="airScreenRuleGroups">
	<s:hidden name="rule" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" style="padding: 0 4px 6px 4px;" cellspacing="0" cellpadding="0" border="0" width="550px">
				<tr>
					<td><!-- definition -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="130px"><label><s:text
									name="config.air.screen.rule.group.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.profileName"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="32" maxlength="%{profileNameLength}" disabled="%{disabledName}"/>
									&nbsp;<s:text name="config.air.screen.rule.group.name.note" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.air.screen.rule.group.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{commentLength}" /> <s:text
									name="config.air.screen.rule.group.description.note" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="sepLine" colspan="3"><img
									src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td style="padding-left: 10px;"><!-- rules -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td><label id="ruleInfoEl"></label></td>
							</tr>
							<tr>
								<s:push value="%{ruleOptions}">
									<td colspan="3"><tiles:insertDefinition name="optionsTransfer" /></td>
								</s:push>
							</tr>
							<%--<tr>
								<td height="5px"></td>
							</tr>
							<tr>
								<td style="padding:0 5px 0 25px;">
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newRule')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('editRule')"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
								</td>
							</tr>--%>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>