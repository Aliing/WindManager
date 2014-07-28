<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'tvComputerCart';  
function onLoadPage() {
	if (Get(formName + "_dataSource_cartName").disabled == false) {
		Get(formName + "_dataSource_cartName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
    if(operation == 'create'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_cartName");
		var message = hm.util.validateName(name.value, '<s:text name="config.tv.cartName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}
	
	var table = document.getElementById("checkAll");
    if (operation == 'addMac') {
    	if(Get(formName + "_macAddStr").value.length==0){
    		hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
	        Get(formName + "_macAddStr").focus();
	        return false;
    	}
    	if(Get(formName + "_macAddStr").value.length<12){
    		hm.util.reportFieldError(table, '<s:text name="error.keyLengthRange"><s:param><s:text name="config.macOrOui.macAddress" /></s:param><s:param><s:text name="config.tv.macOrOui.addressRange" /></s:param></s:text>');
	        Get(formName + "_macAddStr").focus();
	        return false;
    	}
    	
    	if (Get(formName + "_macAddStr").value.length==12){
    		var pattern1 = /^[a-fA-F0-9]{12}/;
    		if (!pattern1.test(Get(formName + "_macAddStr").value)){
	    		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
		        Get(formName + "_macAddStr").focus();
		        return false;
    		}
    	} else if (Get(formName + "_macAddStr").value.length==17){
	    	var pattern2 = /^[a-fA-F0-9]{2}([-]{1}[a-fA-F0-9]{2}){5}/;
	    	var pattern3 = /^[a-fA-F0-9]{2}([:]{1}[a-fA-F0-9]{2}){5}/;
	    	if (!pattern2.test(Get(formName + "_macAddStr").value) && !pattern3.test(Get(formName + "_macAddStr").value)){
	    		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
		        Get(formName + "_macAddStr").focus();
		        return false;
    		}
    	} else {
    		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
	        Get(formName + "_macAddStr").focus();
	        return false;
    	}
    	
    	if(Get(formName + "_nameAddStr").value.length==0){
    		hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.computerName" /></s:param></s:text>');
	        Get(formName + "_nameAddStr").focus();
	        return false;
    	}
    	
    	if(Get(formName + "_nameAddStr").value.length>128){
    		hm.util.reportFieldError(table, '<s:text name="error.keyLengthRange"><s:param><s:text name="config.tv.computerName" /></s:param><s:param><s:text name="config.tv.computerName.range" /></s:param></s:text>');
	        Get(formName + "_nameAddStr").focus();
	        return false;
    	}

    }

    if (operation == 'removeMac' || operation == 'removeMacNone') {
		var cbs = document.getElementsByName('macIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
			return false;
		}
	}
	
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
    	var macIds = document.getElementsByName('macIndices');
		if(macIds.length == 0)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.macOrOui.macAddress" /></s:param></s:text>');
       		table.focus();
       		return false;
		}
    }

	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('macIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function toggleAllCheckBox(cb){
	var allCheckBox = document.getElementById('checkAll');
	if(cb.checked == false){
		allCheckBox.checked = false;
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="tvComputerCart" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="tvComputerCart">
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_TV_COMPUTERCART%>');">
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
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="580px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.cartName" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.cartName" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="128" disabled="%{disabledName}" />&nbsp;<s:text
								name="config.tv.cartName.range" /></td>
						</tr>
						<tr>		
							<td class="labelT1"><s:text
								name="config.hp.description" /></td>	
							<td><s:textfield name="dataSource.description" size="28"
								maxlength="256" />&nbsp;<s:text
								name="config.tv.description.range" /></td>
						</tr>
						<tr>
							<td style="padding:6px 0px 6px 0px" colspan="2">
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
							<td colspan="2" style="padding:4px 0px 4px 4px;" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" class="embedded">
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="3" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" onClick="showCreateSection();"
													<s:property value="updateDisabled" />></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeMac');"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="3" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" onClick="submitAction('addMac');"></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button"
													onClick="submitAction('removeMacNone');"></td>
												<td><input type="button" name="ignore" value="Cancel"
													class="button" onClick="hideCreateSection();"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr id="headerSection">
										<th align="left" style="padding-left: 0;" width="10px"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAllRules(this);"></th>
										<th align="left" width="300px"><s:text
											name="config.macOrOui.macAddress" /><font color="red"><s:text name="*"/></font></th>
										<th align="left" width="350px"><s:text
											name="config.tv.computerName" /><font color="red"><s:text name="*"/></font></th>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
										<td class="listHead" width="10">&nbsp;</td>
										<td class="listHead" valign="top"><s:textfield size="20" name="macAddStr"
											maxlength="17"
											onkeypress="return hm.util.keyPressPermit(event,'hexWithChar');" /><br>
											<s:text name="config.tv.macOrOui.addressRange" /></td>
										<td class="listHead" valign="top"><s:textfield size="20" name="nameAddStr"
											maxlength="128"
											onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
											<s:text name="config.tv.computerName.range" /></td>
									</tr>
									<s:iterator value="%{dataSource.items}" status="status">
										<tr>
											<td class="listCheck"><s:checkbox name="macIndices"
												fieldValue="%{#status.index}" 
												onClick="toggleAllCheckBox(this);"/></td>
											<td class="list" width="300px"><s:property value="stuMac"/></td>
											<td class="list" width="350px"><s:property value="stuName"/>&nbsp;</td>
										</tr>
									</s:iterator>
									<s:if test="%{gridCount > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="6">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
