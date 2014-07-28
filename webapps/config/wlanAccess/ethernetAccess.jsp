<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'ethernetAccess';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_ethernetName").disabled) {
		document.getElementById(formName + "_dataSource_ethernetName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('selectMacAddress');
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == 'create<s:property value="lstForward"/>' && !validateEthernetName()) {
		return false;
	}
	
	if ('<%=Navigation.L2_FEATURE_ETHERNET_ACCESS%>' == operation 
		|| operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_idleTimeout").value = "180";
		return true;
	} else {
		if(!validateIdleTimeout()) {
			return false;
		}
	}
	
	if(operation == "editUserProfile"){
		var value = hm.util.validateListSelection(formName + "_userProfileId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].userProfileId.value = value;
		}
	}
	
	if(operation == "editMacAddress"){
		var value = hm.util.validateOptionTransferSelection("selectMacAddress");
		if(value < 0){
			return false
		}else{
			document.forms[formName].macAddress.value = value;
		}
	}
	return true;
}

function validateIdleTimeout() {
    if("" == document.getElementById("hideIdle").style.display)
	{
	    var inputElement = document.getElementById(formName + "_dataSource_idleTimeout");
	    if(document.getElementById(formName + "_dataSource_enableIdle").checked) {
	    	if (inputElement.value.length == 0) {
	            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ethernet.access.idle" /></s:param></s:text>');
	            inputElement.focus();
	            return false;
	        }
	        var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ethernet.access.idle" />',10,3600);
		    if (message != null) {
		        hm.util.reportFieldError(inputElement, message);
		        inputElement.focus();
		        return false;
		    }
	    } else {
	    	inputElement.value = "180";
	    }   
	} else {
		document.getElementById(formName + "_dataSource_enableIdle").checked = true;
		document.getElementById(formName + "_dataSource_idleTimeout").value = "180";
	}
    return true;
}
function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}

function validateEthernetName() {
    var inputElement = document.getElementById(formName + "_dataSource_ethernetName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.ipFilter.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function enableIdle(checked) {
	document.getElementById("hideIdle").style.display = checked ? "" : "none";
	if(!checked) {
		document.getElementById(formName + "_dataSource_enableIdle").checked=true;
		document.getElementById(formName + "_dataSource_idleTimeout").disabled =false;
		document.getElementById(formName + "_dataSource_idleTimeout").value="180";
	}
}

function enableIdleTimeout(checked) {
	document.getElementById(formName + "_dataSource_idleTimeout").disabled =!checked;
	if(!checked) {
		document.getElementById(formName + "_dataSource_idleTimeout").value="180";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ethernetAccess" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="ethernetAccess">
	<s:hidden name="macAddress" />
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
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_ETHERNET_ACCESS%>');">
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
			<td style="padding-top: 5px;">
			
			<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="470">
				<tr>
					<td style="padding-left:4px" colspan="3">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="80"><label><s:text
								name="config.ipFilter.name" /><font color="red"><s:text name="*"/></font></label></td>
							<td colspan="2"><s:textfield name="dataSource.ethernetName" size="24"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="%{nameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
								name="config.ipFilter.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.ipFilter.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />&nbsp;<s:text
								name="config.ipFilter.description.range" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding:6px 4px 6px 4px" colspan="3">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="sepLine"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding-left:4px" colspan="3">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="80"><label><s:text
								name="config.ethernet.access.user.profile" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:select name="userProfileId" cssStyle="width: 220px;" disabled="%{disableUserProfile}"
								list="%{userProfileList}" listKey="id" listValue="value" /></td>
							<td>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('newUserProfile')"><img class="dinl"
									src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('editUserProfile')"><img class="dinl"
									src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td colspan="3">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding:0 2px 0 14px"><s:checkbox
								name="dataSource.macLearning"
								value="%{dataSource.macLearning}" onclick="enableIdle(this.checked);" /></td>
							<td width="120px"><s:text
								name="config.ethernet.access.macLearn.check" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hideIdle}"/>" id="hideIdle">
					<td colspan="3">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding:0 2px 0 24px"><s:checkbox
								name="dataSource.enableIdle"
								value="%{dataSource.enableIdle}" onclick="enableIdleTimeout(this.checked);" /></td>
							<td width="120px"><s:text
								name="config.ethernet.access.idle.check" /></td>
							<td><s:textfield name="dataSource.idleTimeout" size="10"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="4" disabled="%{!dataSource.enableIdle}"/>&nbsp;<s:text
								name="config.ethernet.access.timeout" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
				<td style="padding:4px 130px 0 15px">
					<fieldset><legend><s:text 
							name="config.ethernet.access.mac.field" /></legend>
						<div>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr id="macOption">
									<td height="5">
								</tr>
								<tr>
									<s:push value="%{macAddressOptions}">
										<td colspan="3"><tiles:insertDefinition
											name="optionsTransfer"/></td>
									</s:push>
								</tr>
								<tr>
									<td height="5"></td>
								</tr>
								<%--<tr>
									<td style="padding-left:40px;">
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('newMacAddress')"><img class="dinl"
											src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('editMacAddress')"><img class="dinl"
											src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</td>
								</tr>--%>
							</table>
						</div>
					</fieldset>
				</td>
				</tr>
				<tr>
				<td height="8"></td>
				</tr>
			</table>
			</td>
		</tr>

	</table>
</s:form></div>
