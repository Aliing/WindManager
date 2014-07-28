<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
function onLoadPaging() {
	var gotoPage = document.getElementById('gotoPageId');
	if (gotoPage != null) {
    	gotoPage.onkeydown = gotoPageKeyHandler;
	}
}
function gotoPageKeyHandler(e) {
	if (!e) {
		// Use window.event for IE
		e = window.event;
	}
	if (e && e.keyCode==13) {
		submitAction('gotoPage');
	}
}
function submitResizePageAction() {
	submitAction('<%=Navigation.OPERATION_RESIZE_PAGE%>');
}
</script>
<s:hidden name="pageIndex" />
<s:iterator id="carryId" value="allSelectedIds" status="status">
	<s:hidden name="allSelectedIds" value="%{carryId}" />
</s:iterator>
<s:iterator value="page" status="status">
	<s:hidden name="pageIds" value="%{id}" />
</s:iterator>
<td class="menu_bg" style="padding: 2px 0px 2px 11px;" align="right">
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td nowrap="nowrap" style="padding: 3px 10px 0px 3px;">Total: <s:property
			value="%{rowCount}" /><s:property value="%{pagePlus}" /></td>
		<td nowrap="nowrap" style="padding: 3px 4px 0px 3px;">Items per page:</td>
		<td style="padding: 2px 10px 0px 0px;"><s:select name="pageSize" cssClass="normal"
			list="#{'15':'15', '20':'20', '25':'25', '30':'30', '50':'50', '100':'100', '200':'200', '500':'500'}"
			value="pageSize" onchange="%{'submitResizePageAction();'}" /></td>
		<s:if test="%{pageIndex > 1}">
			<td><a
				href="javascript: submitAction('<%=Navigation.OPERATION_FIRST_PAGE%>');">
			<img id="firstPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/first.png" />"
				onMouseOver="this.src='<s:url value="/images/paging/first_over.png" />'"
				onMouseOut="this.src='<s:url value="/images/paging/first.png" />'"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-first.png" />"
				onMouseOver="this.src='<s:url value="/images/hm/express/hm-page-first-disable.png" />'"
				onMouseOut="this.src='<s:url value="/images/hm/express/hm-page-first.png" />'"
				width="16" height="16"
				</s:else>
				title="First" border="0px" class="dblk">
			</a></td>
			<td style="padding-left: 0px"><a
				href="javascript: submitAction('<%=Navigation.OPERATION_PREVIOUS_PAGE%>');">
			<img id="previousPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/prev.png" />"
				onMouseOver="this.src='<s:url value="/images/paging/prev_over.png" />'"
				onMouseOut="this.src='<s:url value="/images/paging/prev.png" />'"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-back.png" />"
				onMouseOver="this.src='<s:url value="/images/hm/express/hm-page-back-disable.png" />'"
				onMouseOut="this.src='<s:url value="/images/hm/express/hm-page-back.png" />'"
				width="16" height="16"
				</s:else>
				title="Previous" border="0px" class="dblk">
			</a></td>
		</s:if>
		<s:else>
			<td><img id="firstPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/firstd.png" />"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-first-disable.png" />"
				width="16" height="16"
				</s:else>
				class="dblk"></td>
			<td style="padding-left: 0px"><img id="previousPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/prevd.png" />" 
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-back-disable.png" />" 
				width="16" height="16"
				</s:else>
				class="dblk"></td>
		</s:else>
		<td id="pageNumber" nowrap="nowrap" style="padding: 3px 4px 0px 4px;"><s:property
			value="%{pageIndex}" /> / <s:property value="%{pageCount}" /><s:property
			value="%{pagePlus}" /></td>
		<s:if test="%{pageIndex < pageCount}">
			<td style="padding-right: 0px"><a
				href="javascript: submitAction('<%=Navigation.OPERATION_NEXT_PAGE%>');">
			<img id="nextPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/next.png" />"
				onMouseOver="this.src='<s:url value="/images/paging/next_over.png" />'"
				onMouseOut="this.src='<s:url value="/images/paging/next.png" />'"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-forward.png" />"
				onMouseOver="this.src='<s:url value="/images/hm/express/hm-page-forward-disable.png" />'"
				onMouseOut="this.src='<s:url value="/images/hm/express/hm-page-forward.png" />'"
				width="16" height="16"
				</s:else>
				title="Next" border="0px" class="dblk">
			</a></td>
			<td><a
				href="javascript: submitAction('<%=Navigation.OPERATION_LAST_PAGE%>');">
			<img id="lastPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/last.png" />"
				onMouseOver="this.src='<s:url value="/images/paging/last_over.png" />'"
				onMouseOut="this.src='<s:url value="/images/paging/last.png" />'"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-last.png" />"
				onMouseOver="this.src='<s:url value="/images/hm/express/hm-page-last-disable.png" />'"
				onMouseOut="this.src='<s:url value="/images/hm/express/hm-page-last.png" />'"
				width="16" height="16" 
				</s:else>
				title="Last" border="0px" class="dblk">
			</a></td>
		</s:if>
		<s:else>
			<td style="padding-right: 0px"><img id="nextPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/nextd.png" />"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-forward-disable.png" />"
				width="16" height="16"
				</s:else>
				class="dblk"></td>
			<td style="padding-left: 2px"><img id="lastPage"
				<s:if test="%{null == exConfigGuideFeature}">
				src="<s:url value="/images/paging/lastd.png" />"
				width="24" height="24"
				</s:if>
				<s:else>
				src="<s:url value="/images/hm/express/hm-page-last-disable.png" />"
				width="16" height="16"
				</s:else> 
				class="dblk"></td>
		</s:else>
		<td style="padding: 2px 2px 0px 12px;"><s:textfield
			name="gotoPage" id="gotoPageId" cssStyle="width: 30px;" cssClass="normal" /></td>
		<td><a
			href="javascript: submitAction('<%=Navigation.OPERATION_GOTO_PAGE%>');">
		<img id="gotoPage"
			<s:if test="%{null == exConfigGuideFeature}">
			src="<s:url value="/images/paging/gotopage.png" />" 
			onMouseOver="this.src='<s:url value="/images/paging/gotopage_over.png" />'"
			onMouseOut="this.src='<s:url value="/images/paging/gotopage.png" />'"
			width="24" height="24"
			</s:if>
			<s:else>
			src="<s:url value="/images/hm/express/hm-page-jump.png" />" 
			width="16" height="16"
			</s:else>
			title="Go to page" border="0px" class="dblk">
		</a></td>
	</tr>
</table>
</td>
