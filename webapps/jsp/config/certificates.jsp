<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'certificates';
var thisOperation;

function submitAction(operation) 
{
    thisOperation = operation;
    if (operation == 'remove') 
    {
        confirmDelete();
    }
    else if (operation == 'export')
    {
    	confirmExport();
    }
    else 
    {
        doContinueOper();
    }   
}

function doContinueOper() 
{
	if (validate(thisOperation)) 
	{
		if (thisOperation != 'export')
		{
			showProcessing();
		}
	    
	    document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
	}
}

function validate(operation) 
{	   	
	return true;
}

function confirmDelete()
{
	<s:if test="%{fileList.size() == 0}">
		warnDialog.cfg.setProperty('text', "There is no item to delete.");
		warnDialog.show();
		return;
	</s:if>
	
	var selectedCbs = getSelectedCANames();
	if (selectedCbs.length == 0)
	{
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	
	confirmDialog.show();
}

function checkIsSelectedOneItem()
{
	var selectedIds = getSelectedCANames();
	if(selectedIds.length != 1){
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return false;
	}
	
	return true;
}

function getSelectedCANames() {
	var cbs = document.getElementsByName('selectedCAName');
	var selecteds = new Array();
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			selecteds[selecteds.length] = cbs[i].value;
		}
	}
	return selecteds;
}

function confirmExport()
{
	<s:if test="%{fileList.size() == 0}">
		warnDialog.cfg.setProperty('text', "There is no item to export.");
		warnDialog.show();
		return;
	</s:if>
	
	if (!checkIsSelectedOneItem())
	{
		return;
	}
	
	doContinueOper();
}

function toggleCheckAllFiles(checkAll) {
	var inputElements = document.getElementsByName('selectedCAName');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (!cb.disabled && cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="certificates">
		<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="export" value="Export" class="button"
									onClick="submitAction('export');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="import" value="Import" class="button"
									onClick="submitAction('import');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="remove" value="Remove" class="button"
									onClick="submitAction('remove');"
									<s:property value="writeDisabled" />>
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
					<table cellspacing="0" cellpadding="0" border="0" class="view">
						<tr>
							<th class="check">
								<input type="checkbox" id="checkAll"
									onClick="toggleCheckAllFiles(this);">
							</th>
							<th>
								<s:text name="admin.certificates.fileName" />
							</th>
							<th>
								<s:text name="admin.certificates.fileSize" />
							</th>
							<th>
								<s:text name="admin.certificates.createTime" />
							</th>
							<s:if test="%{showDomain}">
								<th>
									<s:text name="config.domain" />
								</th>
							</s:if>
						</tr>
						<s:if test="%{fileList.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="fileList" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<s:if
									test="%{showDomain && domainName!='home' && domainName!='global'}">
									<td class="listCheck">
										<input type="checkbox" disabled />
									</td>
								</s:if>
								<s:else>
									<td class="listCheck">
										<input type="checkbox" name="selectedCAName"
											value="<s:property value="fileName" />" />
									</td>
								</s:else>
								<td class="list">
									<s:property value="fileName" />
									&nbsp;
								</td>
								<td class="list">
									<s:property value="fileSize" />
									&nbsp;
								</td>
								<td class="list">
									<s:property value="createTime" />
									&nbsp;
								</td>
								<s:if test="%{showDomain}">
									<td class="list">
										<s:property value="domainName" />
										&nbsp;
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
