<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
YAHOO.util.Event.onDOMReady(init);

var multiEditPanel = null;

function init() {
	//create multi edit overlay
	div = document.getElementById('multiEditPanel');
	multiEditPanel = new YAHOO.widget.Panel(div, {
			width:"420px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true
			});
	multiEditPanel.render(document.body);
	div.style.display = "";
}

var formName = 'clientModifications';
var thisOperation;

function submitAction(operation) 
{
    thisOperation = operation;
    if (operation == 'remove') 
    {
        hm.util.checkAndConfirmDelete();
    }
    //else if (operation == 'add')
    //{
    //	checkDuplicateEntry();
    //}
    else 
    {
        doContinueOper();
    }   
}

function doContinueOper() 
{
	if (validate(thisOperation)) 
	{
	    showProcessing();
	    document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if (operation != 'add')
	{	
		return true;
	}
	
	var clientMac = document.getElementById("clientMac");
	var clientIP = document.getElementById("clientIP");
	var hostname = document.getElementById("clientHostname");
	var username = document.getElementById("clientUsername");
	var comment1 = document.getElementById("comment1");
	var comment2 = document.getElementById("comment2");
	
	if (clientMac.value.length == 0)
	{
		hm.util.reportFieldError(clientMac, '<s:text name="error.requiredField"><s:param><s:text name="monitor.activeClient.clientMac" /></s:param></s:text>');
        clientMac.focus();
        return false;
	}
	else if (!hm.util.validateMacAddress(clientMac.value,12))
	{
		hm.util.reportFieldError(clientMac, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.activeClient.clientMac" /></s:param></s:text>');
		clientMac.focus();
		return false;
	}
	
	if (clientIP!=null && clientIP.value.length > 0 && !hm.util.validateIpAddress(clientIP.value)) 
	{
		hm.util.reportFieldError(clientIP, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.client.clientIP" /></s:param></s:text>');
		clientIP.focus();
		return false;
	}
	
	if ((clientIP == null || clientIP.value.length == 0) 
		 && (hostname == null || hostname.value.length == 0) 
		 && (username == null || username.value.length == 0)
		 && (comment1 == null || comment1.value.length == 0) 
		 && (comment2 == null || comment2.value.length == 0))
	{
		hm.util.reportFieldError(clientIP, 'Please fill in some fields besides MAC Address.');
		clientIP.focus();
		return false;
	}		
	
	return true;
}

function openModifyDialog()
{
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0) 
	{
		warnDialog.cfg.setProperty('text', "There is no item to operation.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) 
	{
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	}
	else
	{
		url = "<s:url action='clientModifications' includeParams='none' />" + "?operation=initEditValues&selectedIDStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: showMultiEditOverlay, failure:showMultiEditOverlay,timeout: 5000}, null);
	}
}

function showMultiEditOverlay(o)
{
	eval("var result = " + o.responseText);
	if(result.eUserName)
	{
		document.getElementById("editUserName").value = result.eUserName;
	}
	else
	{
		document.getElementById("editUserName").value = "";
	}
	
	if(result.eHostName)
	{
		document.getElementById("editHostName").value = result.eHostName;
	}
	else
	{
		document.getElementById("editHostName").value = "";
	}
	
	if(result.eIP)
	{
		document.getElementById("editIP").value = result.eIP;
	}
	else
	{
		document.getElementById("editIP").value = "";
	}
	
	if(result.eComment1)
	{
		document.getElementById("editComment1").value = result.eComment1;
	}
	else
	{
		document.getElementById("editComment1").value = "";
	}
	
	if(result.eComment2)
	{
		document.getElementById("editComment2").value = result.eComment2;
	}
	else
	{
		document.getElementById("editComment2").value = "";
	}
	
	if (hm.util.getSelectedIds().length > 1)
	{
		document.getElementById("flagEditIP").disabled = true;
	}
	else
	{
		document.getElementById("flagEditIP").disabled = false;
	}
	
	document.getElementById("flagEditUserName").checked =false;
	document.getElementById("flagEditHostName").checked =false;
	document.getElementById("flagEditIP").checked =false;
	document.getElementById("flagEditComment1").checked =false;
	document.getElementById("flagEditComment2").checked =false;
	document.getElementById("editUserName").disabled=true;
	document.getElementById("editHostName").disabled=true;
	document.getElementById("editIP").disabled=true;
	document.getElementById("editComment1").disabled=true;
	document.getElementById("editComment2").disabled=true;
	
	hm.util.hideFieldError();
	multiEditPanel.cfg.setProperty('visible', true);
}

function saveEditResults()
{
	if (!validateModifyClients())
	{
		return;
	}
	
	multiEditPanel.cfg.setProperty('visible', false);
	
	if (document.getElementById("flagEditUserName").checked || document.getElementById("flagEditHostName").checked
		|| document.getElementById("flagEditIP").checked || document.getElementById("flagEditComment1").checked
		||document.getElementById("flagEditComment2").checked)
	{
		submitFormAction('multiEditForm','saveEditResults');
	}
	else
	{
		hm.util.clearSelection();		
	}
}

function validateModifyClients()
{
	var clientIP = document.getElementById("editIP");
	
	if (document.getElementById("flagEditIP").checked)
	{
		if (clientIP.value.length > 0 && !hm.util.validateIpAddress(clientIP.value)) 
		{
			hm.util.reportFieldError(clientIP, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.client.clientIP" /></s:param></s:text>');
			clientIP.focus();
			return false;
		}		
	}
	
	var bol_ip = clientIP.value.length == 0;
	var bol_hostName = document.getElementById("editHostName").value.length == 0;
	var bol_userName = document.getElementById("editUserName").value.length == 0;
	var bol_comment1 = document.getElementById("editComment1").value.length == 0;
	var bol_comment2 = document.getElementById("editComment2").value.length == 0;
	
	if (bol_ip && bol_hostName && bol_userName && bol_comment1 && bol_comment2)
	{
		hm.util.reportFieldError(clientIP, 'Please fill in some fields or please just remove this item.');
		clientIP.focus();
		return false;
	}
	
	return true;
}

function submitFormAction(formName, operation)
{
	showProcessing();		
	document.forms[formName].operation.value = operation;
	document.forms[formName].selectAll.value = isSelectAll();
	document.forms[formName].selectedIDStr.value = hm.util.getSelectedIds();
    document.forms[formName].submit();
}

function isSelectAll()
{
	return document.getElementById("checkAll").checked;
}

function selectEditUserName(checked)
{
	if (checked)
	{
		document.getElementById("editUserName").disabled = false;
	}
	else
	{
		document.getElementById("editUserName").disabled = true;
	}
}

function selectEditHostName(checked)
{
	if (checked)
	{
		document.getElementById("editHostName").disabled = false;
	}
	else
	{
		document.getElementById("editHostName").disabled = true;
	}
}

function selectEditClietIP(checked)
{
	if (checked)
	{
		document.getElementById("editIP").disabled = false;
	}
	else
	{
		document.getElementById("editIP").disabled = true;
	}
}

function selectEditComment1(checked)
{
	if (checked)
	{
		document.getElementById("editComment1").disabled = false;
	}
	else
	{
		document.getElementById("editComment1").disabled = true;
	}
}

function selectEditComment2(checked)
{
	if (checked)
	{
		document.getElementById("editComment2").disabled = false;
	}
	else
	{
		document.getElementById("editComment2").disabled = true;
	}
}

/**
function checkDuplicateEntry()
{
	url = "<s:url action='clientModifications' includeParams='none' />" + "?operation=checkDuplicateEntry&ignore=" + new Date().getTime();
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkDuplicateResult, failure:abortResult,timeout: 5000}, null);
}

var checkDuplicateResult = function(o)
{
	eval("var result = " + o.responseText);
	if(result.success){
		doContinueOper();
	}else{
		warnDialog.cfg.setProperty('text', "Same item already exists.");
		warnDialog.show();
	}
}

var abortResult = function(o) 
{
	hm.util.clearSelection();
}
**/

function showCreateSection() 
{
	//document.getElementById("addBtn").disabled=true;
	//document.getElementById("removeBtn").disabled=true;
	document.getElementById("addBtn").style.display = "none";
	document.getElementById("removeBtn").style.display = "none";
	document.getElementById("modifyBtn").style.display = "none";
	hm.util.show('createButton');
	hm.util.show('createSection');
	// to fix column overlap issue on certain browsers
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;	
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() 
{
	//document.getElementById("addBtn").disabled=false;
	//document.getElementById("removeBtn").disabled=false;
	document.getElementById("addBtn").style.display = "block";
	document.getElementById("removeBtn").style.display = "block";
	document.getElementById("modifyBtn").style.display = "block";
	hm.util.hide('createButton');
	hm.util.hide('createSection');
	
	//clear input fields
	document.getElementById("clientMac").value = "";
	document.getElementById("clientIP").value = "";
	document.getElementById("clientHostname").value = "";
	document.getElementById("clientUsername").value = "";
	document.getElementById("comment1").value = "";
	document.getElementById("comment2").value = "";
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content">
	<s:form action="clientModifications">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr id="newButton">
							<td>
								<input type="button" id="addBtn" name="add" value="Add"
									class="button" onClick="showCreateSection();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" id="modifyBtn" name="ignore" value="Modify"
									class="button" onClick="openModifyDialog();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" id="removeBtn" name="remove" value="Remove"
									class="button" onClick="submitAction('remove');"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
						<tr style="display:none;" id="createButton">
							<td>
								<input type="button" name="ignore" value="Apply" class="button"
									onClick="submitAction('add');">
							</td>
							<td>
								<input type="button" name="ignore" value="Cancel" class="button"
									onClick="hideCreateSection();">
							</td>
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
				<td>
					<table cellspacing="0" cellpadding="0" border="0" class="view"
						width="500">
						<tr id="headerSection">
							<th class="check">
								<input type="checkbox" id="checkAll"
									onClick="hm.util.toggleCheckAll(this);">
							</th>
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<th>
										<ah:sort name="clientMac" key="monitor.activeClient.clientMac" />
									</th>
								</s:if>
								<s:if test="%{columnId == 2}">
									<th>
										<ah:sort name="clientIP" key="monitor.activeClient.clientIP" />
									</th>
								</s:if>
								<s:if test="%{columnId == 3}">
									<th>
										<ah:sort name="clientHostname"
											key="monitor.activeClient.clientHostName" />
									</th>
								</s:if>
								<s:if test="%{columnId == 4}">
									<th>
										<ah:sort name="clientUsername"
											key="monitor.activeClient.clientUserName" />
									</th>
								</s:if>
								<s:if test="%{columnId == 5}">
									<th>
										<ah:sort name="comment1" key="monitor.client.comment1" />
									</th>
								</s:if>
								<s:if test="%{columnId == 6}">
									<th>
										<ah:sort name="comment2" key="monitor.client.comment2" />
									</th>
								</s:if>
							</s:iterator>
							<s:if test="%{showDomain}">
								<th>
									<ah:sort name="owner.domainName" key="config.domain" />
								</th>
							</s:if>
						</tr>
						<tr style="display:none;" id="createSection">
							<td class="listHead"></td>
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<td class="listHead" valign="top">
										<s:textfield id="clientMac" name="dataSource.clientMac"
											size="16"
											onkeypress="return hm.util.keyPressPermit(event,'hex');"
											maxlength="12" />
									</td>
								</s:if>
								<s:if test="%{columnId == 2}">
									<td class="listHead" valign="top">
										<s:textfield id="clientIP" name="dataSource.clientIP"
											size="16"
											onkeypress="return hm.util.keyPressPermit(event,'ip');" />
									</td>
								</s:if>
								<s:if test="%{columnId == 3}">
									<td class="listHead" valign="top">
										<s:textfield id="clientHostname"
											name="dataSource.clientHostname" size="16" maxlength="32" />
									</td>
								</s:if>
								<s:if test="%{columnId == 4}">
									<td class="listHead" valign="top">
										<s:textfield id="clientUsername"
											name="dataSource.clientUsername" size="16" maxlength="32" />
									</td>
								</s:if>
								<s:if test="%{columnId == 5}">
									<td class="listHead" valign="top">
										<s:textfield id="comment1" name="dataSource.comment1"
											size="16" maxlength="32" />
									</td>
								</s:if>
								<s:if test="%{columnId == 6}">
									<td class="listHead" valign="top">
										<s:textfield id="comment2" name="dataSource.comment2"
											size="16" maxlength="32" />
									</td>
								</s:if>
							</s:iterator>
						</tr>
						<s:if test="%{page.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="page" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<s:if
									test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
									<td class="listCheck">
										<input type="checkbox" disabled />
									</td>
								</s:if>
								<s:else>
									<td class="listCheck">
										<ah:checkItem />
									</td>
								</s:else>
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 1}">
										<td class="list">
											<s:property value="clientMac" />
										</td>
									</s:if>
									<s:if test="%{columnId == 2}">
										<td class="list">
											<s:property value="clientIP" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 3}">
										<td class="list">
											<s:property value="clientHostname" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 4}">
										<td class="list">
											<s:property value="clientUsername" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 5}">
										<td class="list">
											<s:property value="comment1" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 6}">
										<td class="list">
											<s:property value="comment2" />
											&nbsp;
										</td>
									</s:if>
								</s:iterator>
								<s:if test="%{showDomain}">
									<td class="list">
										<s:property value="%{owner.domainName}" />
									</td>
								</s:if>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<div id="multiEditPanel" style="display:none">
	<div class="hd">
		Modify
	</div>
	<div class="bd">
		<s:form action="clientModifications" id="multiEditForm"
			name="multiEditForm">
			<s:hidden name="operation" />
			<s:hidden name="selectAll" />
			<s:hidden name="selectedIDStr" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td style="padding: 6px 5px 5px 10px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" width="120px">
												<s:checkbox name="flagEditIP" id="flagEditIP"
													onclick="selectEditClietIP(this.checked);" />
												<s:text name="monitor.client.clientIP" />
											</td>
											<td>
												<s:textfield name="editIP" id="editIP" maxlength="32"
													size="20" disabled="true"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditHostName" id="flagEditHostName"
													onclick="selectEditHostName(this.checked);" />
												<s:text name="monitor.client.clientHostname" />
											</td>
											<td>
												<s:textfield name="editHostName" id="editHostName"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditUserName" id="flagEditUserName"
													onclick="selectEditUserName(this.checked);" />
												<s:text name="monitor.client.clientUserName" />
											</td>
											<td>
												<s:textfield name="editUserName" id="editUserName"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditComment1" id="flagEditComment1"
													onclick="selectEditComment1(this.checked);" />
												<s:text name="monitor.client.comment1" />
											</td>
											<td>
												<s:textfield name="editComment1" id="editComment1"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditComment2" id="flagEditComment2"
													onclick="selectEditComment2(this.checked);" />
												<s:text name="monitor.client.comment2" />
											</td>
											<td>
												<s:textfield name="editComment2" id="editComment2"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td height="4px"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Save" class="button"
										onClick="saveEditResults();">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button"
										onClick="multiEditPanel.cfg.setProperty('visible', false);">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
