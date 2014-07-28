<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'userGroups';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else {
        doContinueOper();
    }   
}
function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content">
	<s:form action="userGroups">
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
									<s:property value="writeDisabled4HHM" />>
							</td>
							<td>
								<input type="button" name="ignore" value="Clone" class="button"
									onClick="submitAction('clone');"
									<s:property value="writeDisabled4HHM" />>
							</td>
							<td>
								<input type="button" name="remove" value="Remove" class="button"
									onClick="submitAction('remove');"
									<s:property value="writeDisabled4HHM" />>
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
									onClick="hm.util.toggleCheckAll(this);">
							</th>
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<th>
										<ah:sort name="groupName" key="config.userGroup.groupName" />
									</th>
								</s:if>
								<s:if test="%{columnId == 2}">
									<s:if test="%{showAttribute}">
										<th>
											<ah:sort name="groupAttribute"
												key="admin.usergroup.attribute" />
										</th>
									</s:if>
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
									test="%{(showDomain && owner.domainName!='home' && owner.domainName!='global') || GMUserGroup}">
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
												href='<s:url action="userGroups"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
													value="groupName" /> </a>
										</td>
									</s:if>
									<s:if test="%{columnId == 2}">
										<s:if test="%{showAttribute}">
											<td class="list">
												<s:property value="groupAttribute" />
											</td>
										</s:if>
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
