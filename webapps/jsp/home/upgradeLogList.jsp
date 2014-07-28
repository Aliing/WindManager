<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
	var formName = 'upgradeLog';
	var thisOperation;

	function submitAction(operation) {
		thisOperation = operation;
		if (operation == 'remove') {
			hm.util.checkAndConfirmDelete();
		} else {
			doContinueOper();
		}
	}

	function doContinueOper() {
		showProcessing();
		document.forms[formName].operation.value = thisOperation;
		document.forms[formName].submit();
	}

	function validate(operation) {
		return true;

	}

	function insertPageContext() {
		document
				.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
	}
</script>

<div id="content">
	<s:form action="upgradeLog">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Remove"
								class="button" onClick="submitAction('remove');"
								<s:property value="writeDisabled" />></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0">
						<s:if test="%{page.size() > 0}">
							<tr>
								<td style="padding: 8px 4px 0 32px">
									<table cellspacing="0" cellpadding="0" border="0" class="view">
										<tr>
											<td style="font-size: 13px"><s:property
													value="%{systemNmsName}" /> <s:property
													value="%{systemVersion}" /> <s:text
													name="admin.upgradeLog.upgradeTime" /> <s:property
													value="%{upgradeTime}" /></td>
										</tr>
									</table>
								</td>
							</tr>
						</s:if>
						<tr>
							<td style="padding: 0 4px 0 8px">
								<table cellspacing="0" cellpadding="0" border="0" class="view">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th><ah:sort name="formerContent"
														key="admin.upgradeLog.formerContent" /></th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th><ah:sort name="postContent"
														key="admin.upgradeLog.postContent" /></th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th><ah:sort name="recommendAction"
														key="admin.upgradeLog.recommendAction" /></th>
											</s:if>
											<s:if test="%{columnId == 4}">
												<th><ah:sort name="annotation"
														key="admin.upgradeLog.annotation" /></th>
											</s:if>
											<s:if test="%{columnId == 5}">
												<th><ah:sort name="logTimeStamp.time"
														key="admin.upgradeLog.upgradeTime" /></th>
											</s:if>
										</s:iterator>
										<s:if test="%{showDomain}">
											<th><ah:sort name="owner.domainName" key="config.domain" /></th>
										</s:if>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<s:iterator value="page" status="status" var="pageRow">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<s:if
												test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
												<td class="listCheck"><input type="checkbox" disabled /></td>
											</s:if>
											<s:else>
												<td class="listCheck"><ah:checkItem /></td>
											</s:else>
											<s:iterator value="%{selectedColumns}">
												<s:if test="%{columnId == 1}">
													<td class="list"><s:property value="%{formerContent}" escape="false"/>
													</td>
												</s:if>
												<s:if test="%{columnId == 2}">
													<td class="list"><s:property value="%{postContent}" /></td>
												</s:if>
												<s:if test="%{columnId == 3}">
													<td class="list"><s:property
															value="%{recommendAction}" /></td>
												</s:if>
												<s:if test="%{columnId == 4}">
													<td class="list"><a
														href='<s:url value="upgradeLog.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
																value="annotation" /></a></td>
												</s:if>
												<s:if test="%{columnId == 5}">
													<td class="list"><s:property value="%{logTimeString}" /></td>
												</s:if>
											</s:iterator>
											<s:if test="%{showDomain}">
												<td class="list"><s:property
														value="%{owner.domainName}" /></td>
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