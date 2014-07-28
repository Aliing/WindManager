<%@taglib prefix="s" uri="/struts-tags"%>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td style="padding-top:4px;">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td class="labelT1" nowrap><label>MAC Address:</label></td>
				<td style="padding: 0 10px 0 5px;"><s:if
					test="%{availableFilters.size() > 0}">
					<s:select name="selectedId" list="availableFilters" listKey="id"
						listValue="filterName" cssStyle="width: 150px;" />
				</s:if> <s:else>
					<s:select name="selectedId" list="#{'-1':'None available'}"
						cssStyle="width: 150px;" />
				</s:else></td>
				<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="add" value="Add"
							class="button" onClick="addFilterAction();"></td>
						<td><input type="button" name="remove" value="Remove"
							class="button" onClick="removeFilterAction();"></td>
					</tr>
				</table>
				</td>
				<td width="100%">&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td>
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<th align="left">&nbsp;</th>
				<th align="left"><s:text name="config.macFilter.name" /></th>
				<th align="left" nowrap><s:text name="config.macFilter.address" /></th>
				<th align="left" nowrap><s:text name="config.macFilter.action" /></th>
				<th align="left" nowrap><s:text
					name="config.macFilter.description" /></th>
			</tr>
			<s:iterator id="macAddress" value="macFilters" status="status">
				<tr class="list">
					<td class="listCheck"><input type="checkbox"
						name="selectedIds" value="<s:property value="id" />"></td>
					<td class="list"><a
						href='<s:url value="macFilters.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'><s:property
						value="filterName" /></a></td>
					<td class="list"><s:property value="macAddress" /></td>
					<td class="list"><s:property value="filterAction" /></td>
					<td class="list"><s:property value="description" /></td>
				</tr>
			</s:iterator>
		</table>
		</td>
	</tr>
</table>
