<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'cwpCertMgmt';
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
	<s:if test="%{page.size() == 0}">
		warnDialog.cfg.setProperty('text', "There is no item to delete.");
		warnDialog.show();
		return;
	</s:if>

	var selectedIds = hm.util.getSelectedIds();
	if (selectedIds.length == 0)
	{
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	confirmDialog.show();
}

function confirmExport()
{
	<s:if test="%{page.size() == 0}">
		warnDialog.cfg.setProperty('text', "There is no item to export.");
		warnDialog.show();
		return;
	</s:if>

	var selectedIds = hm.util.getSelectedIds();
	if (selectedIds.length == 0)
	{
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}

	if (selectedIds.length > 1)
	{
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}

	doContinueOper();
}

function toggleCheckAllFiles(cb) {
	var cbs = document.getElementsByName('cbCertFile');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="cwpCertMgmt">
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
								<input type="button" name="new" value="New" class="button"
									onClick="submitAction('new');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="export" value="Export" class="button"
									onClick="submitAction('export');"
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
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view">
									<tr>
										<th class="check">
											<input type="checkbox" id="checkAll"
												onClick="hm.util.toggleCheckAll(this);">
										</th>
										<s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th>
													<ah:sort name="certName" key="config.cwpCert.certName" />
												</th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th>
													<ah:sort name="srcCertName" key="config.cwpCert.cert" />
												</th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th>
													<ah:sort name="srcKeyName" key="config.cwpCert.privateKey" />
												</th>
											</s:if>
											<s:if test="%{columnId == 4}">
												<th>
													<ah:sort name="encrypted" key="config.cwpCert.encrypted" />
												</th>
											</s:if>
											<s:if test="%{columnId == 5}">
												<th>
													<ah:sort name="description"
														key="config.cwpCert.description" />
												</th>
											</s:if>
										</s:iterator>
										<s:if test="%{showDomain}">
											<th>
												<ah:sort name="owner.domainName" key="config.domain" />
											</th>
										</s:if>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status" id="pageRow">
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
														<a
															href='<s:url action="cwpCertMgmt"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
																value="certName" /> </a>
													</td>
												</s:if>
												<s:if test="%{columnId == 2}">
													<td class="list">
														<s:property value="srcCertName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 3}">
													<td class="list">
														<s:property value="srcKeyName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 4}">
													<td class="list">
														<s:property value="encryptedShow" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 5}">
													<td class="list">
														<s:property value="description" />
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
			</td>
			</tr>
		</table>
	</s:form>
</div>
