<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'mailList';

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'refresh')
		{
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
    }
}

function validate(operation)
{
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

</script>

<div id="content">
	<s:form action="mailList">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
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
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<th>
										<ah:sort name="owner.domainName" key="config.domain" />
									</th>
								</s:if>
								<s:if test="%{columnId == 2}">
									<th>
										<s:text name="admin.emailNotify.toEmail1" />
									</th>
								</s:if>
								<s:if test="%{columnId == 3}">
									<th>
										<s:text name="admin.emailNotify.toEmail2" />
									</th>
								</s:if>
								<s:if test="%{columnId == 4}">
									<th>
										<s:text name="admin.emailNotify.toEmail3" />
									</th>
								</s:if>
								<s:if test="%{columnId == 5}">
									<th>
										<s:text name="admin.emailNotify.toEmail4" />
									</th>
								</s:if>
								<s:if test="%{columnId == 6}">
									<th>
										<s:text name="admin.emailNotify.toEmail5" />
									</th>
								</s:if>
							</s:iterator>
						</tr>
						<s:if test="%{page.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="page" status="status" id="pageRow">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 1}">
										<td class="list">
											<s:property value="#pageRow.owner.domainName" />
										</td>
									</s:if>
									<s:if test="%{columnId == 2}">
										<td class="list">
											<s:property value="toEmail1" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 3}">
										<td class="list">
											<s:property value="toEmail2" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 4}">
										<td class="list">
											<s:property value="toEmail3" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 5}">
										<td class="list">
											<s:property value="toEmail4" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 6}">
										<td class="list">
											<s:property value="toEmail5" />
											&nbsp;
										</td>
									</s:if>
								</s:iterator>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
