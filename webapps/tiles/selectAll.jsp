<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<s:if test="%{pageCount > 1}">
	<s:hidden name="allItemsSelected" id="allItemsSelectedVar" value="false" />
	<tr class="pageSelected" id="pageItemsSelectedRow" style="display:none">
		<td colspan="100" class="list" align="center">All <b><s:property
			value="%{availablePageRowCount}" /></b> items on this page are selected.&nbsp;&nbsp;<a
			href="javascript: hm.util.setAllItemsSelected();" id="allItemsSelectedVarSelect">Select all <b><s:property
			value="%{availableRowCount}" /><s:property
			value="%{pagePlus}" /></b> items.</a><span 
			id="availableRowCountSpan" style="display: none;"><s:property
			value="%{availableRowCount}" /><s:property value="%{pagePlus}" /></span></td>
	</tr>
	<tr class="allSelected" id="allItemsSelectedRow" style="display:none">
		<td colspan="100" class="list" align="center">All <b><s:property
			value="%{availableRowCount}" /></b> items are selected.&nbsp;&nbsp;<a
			href="javascript: hm.util.clearSelection();" id="allItemsSelectedVarClear">Clear selection.</a></td>
	</tr>
</s:if>
<s:if test="%{allItemsSelected}"><%-- in order to select all items which selected last time --%>
<tr>
	<td>
		<script type="text/javascript">
			YAHOO.util.Event.onDOMReady(function () {
				var checkAll = document.getElementById('checkAll');
				if (checkAll) {
					checkAll.checked = true;
					hm.util.toggleCheckAll(checkAll);
					hm.util.setAllItemsSelected();
				}
			});
		</script>
	</td>
</tr>
</s:if>
