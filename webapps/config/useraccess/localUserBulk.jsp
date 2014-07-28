<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'localUser';  

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'createBulk') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == "editGroupBulk"){
		var value = hm.util.validateListSelection(formName + "_userGroupId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userGroupId.value = value;
		}
	}
	if('<%=Navigation.L2_FEATURE_LOCAL_USER%>' == operation 
		|| operation == 'newGroupBulk'
		|| operation == 'editGroupBulk'
		|| operation == 'cancel') {
		if (operation == 'cancel') {
			document.getElementById("userNumber").value=0;
		}
		return true;
	}

	if (!checkUserGroup()){
		return false;
	}
	
	if (!checkCreateNumber()){
		return false;
	}
	
	if (!checkEmailAddress()){
		return false;
	}

	return true;
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var activeUser = details.active;
	var revokeUser = details.revoke;
	var remainUser = details.remain;
	document.getElementById("activeUser").innerHTML =activeUser;
	document.getElementById("revokeUser").innerHTML =revokeUser;
	document.getElementById("remainUser").innerHTML =remainUser;
};

var detailsFailed = function(o) {
	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function changeUserGroup(){
	var userGroupId = document.getElementById(formName + "_userGroupId").value;
	var url = '<s:url action="localUser"><s:param name="operation" value="changeUserGroup"/></s:url>' + "&userGroupId="+userGroupId  + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function checkUserGroup() {
	var userGroupId = document.getElementById(formName + "_userGroupId");
	if (userGroupId.value == null || userGroupId.value < 0) {
		<s:if test="%{fullMode}">
			hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.bulk.pskGroup" /></s:param></s:text>');
		</s:if>
		<s:else>
			hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.bulk.pskSsid" /></s:param></s:text>');
		</s:else>
		userGroupId.focus();
		return false;
	}
	return true;
}

function checkCreateNumber() {
	var userNumber = document.getElementById("userNumber");
	if (userNumber.value.length ==0){
	    hm.util.reportFieldError(userNumber, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.bulk.userNumber" /></s:param></s:text>');
	    userNumber.focus();
	}
	
	var message = hm.util.validateIntegerRange(userNumber.value, '<s:text name="config.localUser.bulk.userNumber" />',1,9999);
   	if (message != null) {
        hm.util.reportFieldError(userNumber, message);
        userNumber.focus();
        return false;
   	}
	return true;
}

function checkEmailAddress(){
//		if (document.getElementById(formName + "_dataSource_mailAddress").value=="") {
//		  hm.util.reportFieldError(document.getElementById(formName + "_dataSource_mailAddress"), '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.emailTitle" /></s:param></s:text>');
//          return false;
//		}
	if (document.getElementById(formName + "_dataSource_mailAddress").value.length==0) {
		return true;
	}
	var emails = document.getElementById(formName + "_dataSource_mailAddress").value.split(";");
	for (var i=0;i<emails.length;i++) {
		if (i==emails.length-1 && emails[i].trim()=="") {
			break;
		}
		if (!hm.util.validateEmail(emails[i].trim())) {
			hm.util.reportFieldError(document.getElementById(formName + "_dataSource_mailAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUser.emailTitle" /></s:param></s:text>');
			document.getElementById(formName + "_dataSource_mailAddress").focus();
			return false;
		}
	}
	return true;
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="localUser" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="localUser">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="create" value="Create"
						class="button"
						onClick="submitAction('createBulk');"
						<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_LOCAL_USER%>');">
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700px">
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td colspan="2">
						<table  cellspacing="0" cellpadding="0" border="0">	
							<tr>
								<td class="labelT1" width="160px">
									<s:if test="%{fullMode}">
										<s:text name="config.localUser.bulk.pskGroup" /><font color="red"><s:text name="*"/></font>
									</s:if>
									<s:else>
										<s:text name="config.localUser.bulk.pskSsid" /><font color="red"><s:text name="*"/></font>
									</s:else>
								</td>
								<td width="270px"><s:select name="userGroupId" cssStyle="width: 270px;"
									list="%{localUserGroup}" listKey="id" listValue="value" 
									onchange="changeUserGroup();"/>
								</td>
								<s:if test="%{fullMode}">
								<td>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newGroupBulk')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
								</td>
								<td>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('editGroupBulk')"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
								</td>
								</s:if>
							<tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="labelT1" width="160px"><s:text
						name="config.localUser.bulk.userNumber" /><font color="red"><s:text name="*"/></font></td>
					<td><s:textfield size="24"
						name="userNumber" maxlength="4" id="userNumber"
						onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
						name="config.localUser.bulk.userNumberRange" /></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="config.localUser.description" /></td>
					<td><s:textfield size="48"
						name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
						name="config.ssid.description_range" /></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text name="config.localUser.emailTitle" /></td>
					<td><s:textfield name="dataSource.mailAddress" size="48"
						maxlength="128" />&nbsp;<s:text
						name="config.localUser.email.emailNoteRange" /></td>
				</tr>
				<tr>
					<td colspan="2" nowrap="nowrap" style="padding-left: 30px" class="noteInfo"><s:text
							name="report.reportList.email.note" /></td>
				</tr>
				<tr>
					<td colspan="2" nowrap="nowrap" style="padding-left: 63px" class="noteInfo"><s:text
							name="report.reportList.email.emailNote" /></td>
				</tr>
				<tr>
					<td style="padding: 4px 4px 4px 4px;" colspan="2"> 
						<fieldset><legend><s:text name="config.localUser.autoUser.info" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td height="4px"></td>
								</tr>
								<tr>
									<td colspan="2" class="noteInfo" style="padding-left: 12px"><s:text name="config.localUser.br100.limit.note"/></td>
								</tr>
								<tr>
									<td width="200px" style="padding-left: 30px"><s:text name="config.localUser.autoUser.activeUser" /></td>
									<td id="activeUser"><s:property value="activeUser"/></td>
								</tr>
								<tr>
									<td style="padding-left: 30px"><s:text name="config.localUser.autoUser.revokeUser" /></td>
									<td id="revokeUser"><s:property value="revokeUser"/></td>
								</tr>
								<tr>
									<td style="padding-left: 30px"><s:text name="config.localUser.autoUser.remainUser" /></td>
									<td id="remainUser"><s:property value="remainUser"/></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>
