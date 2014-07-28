<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'tvStudentRoster';  
function onLoadPage() {
	if (Get(formName + "_dataSource_studentId").disabled == false) {
		Get(formName + "_dataSource_studentId").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == "editClass"){
		var value = hm.util.validateListSelection(formName + "_classId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].classId.value = value;
		}
	}
	
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
		var id = document.getElementById(formName + "_dataSource_studentId");
		var message = hm.util.validateName(id.value, '<s:text name="config.tv.studentId" />');
    	if (message != null) {
    		hm.util.reportFieldError(id, message);
        	id.focus();
        	return false;
    	}

		var studentName = document.getElementById(formName + "_dataSource_studentName");
    	if (studentName.value.length == 0) {
    		hm.util.reportFieldError(studentName, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.studentName" /></s:param></s:text>');
    		studentName.focus();
        	return false;
    	}
    	
    	var tvclass = document.getElementById(formName + "_classId");
    	if (tvclass.value=='' || tvclass.value<0){
			hm.util.reportFieldError(tvclass, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.class" /></s:param></s:text>');
        	tvclass.focus();
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="tvStudentRoster" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="tvStudentRoster">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_TV_STUDENTROSTER%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
					</td>
					</s:else>
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
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="720px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.studentId" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.studentId" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="128" disabled="%{disabledName}" />&nbsp;<s:text
								name="config.tv.studentId.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.studentName" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.studentName" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
								maxlength="128"/>&nbsp;<s:text
								name="config.tv.studentName.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.tv.class" /><font color="red"><s:text name="*"/></font></td>
							<td style="padding-right: 5px;"><s:select
								name="classId" list="%{lstClass}" listKey="id"
								listValue="value" cssStyle="width: 175px;" />
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('newClass')"><img class="dinl"
									src="<s:url value="/images/new.png" />"
									width="16" height="16" alt="New" title="New" /></a>
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('editClass')"><img class="dinl"
									src="<s:url value="/images/modify.png" />"
									width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else>
							</td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.hp.description" /></td>	
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="256" />&nbsp;<s:text
								name="config.tv.description.range" /></td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
