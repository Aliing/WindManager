<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<style>
#searchPanel {
	height: 800px;
	overflow-x: none;
	overflow-y: auto;
}

</style>
<script>
function searchOption()
{
	if (YAHOO.util.Dom.getStyle("searchOptionDiv","display") == 'block')
	{
		document.getElementById('searchOptionDiv').style.display="none";
	}
	else
	{
		document.getElementById('searchOptionDiv').style.display="block";
	}
}

function gotoPagePress(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	
	if (keycode == 13)
	{
		if (document.getElementById('searchGotoPageId').value.length > 0)
		{
			searchPaging('<%=Navigation.OPERATION_GOTO_PAGE%>');
		}
		
		return false;
	}
	
	// permit input number
	if(48 <= keycode && keycode <=57)
	{
		return true;
	}

	return false;
}
</script>

<div id="searchPanel">
	<s:form action="search" id="searchForm" name="searchForm">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td id="topTD">
								<s:if test="%{searchResult.totalCount > 0}">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<s:if test="%{searchResult.pageIndex > 1}">
												<td>
													<a
														href="javascript: searchPaging('<%=Navigation.OPERATION_PREVIOUS_PAGE%>');">
														<img src="<s:url value="/images/search/Back_off.png" />"
															onMouseOver="this.src='<s:url value="/images/search/Back_on.png" />'"
															onMouseOut="this.src='<s:url value="/images/search/Back_off.png" />'"
															title="Previous" width="24" height="24" border="0px"
															class="dblk"> </a>
												</td>
											</s:if>
											<s:else>
												<td>
													<img src="<s:url value="/images/search/Back_gray.png" />"
														width="24" height="24" class="dblk">
												</td>
											</s:else>
											<td id="pageNumber" class="searchPageNum" nowrap
												style="padding: 3px 4px 0px 4px;">
												<s:property value="%{searchResult.pageIndex}" />
												/
												<s:property value="%{searchResult.pageCount}" />
											</td>
											<s:if
												test="%{searchResult.pageIndex < searchResult.pageCount}">
												<td style="padding-right: 0px">
													<a
														href="javascript: searchPaging('<%=Navigation.OPERATION_NEXT_PAGE%>');">
														<img
															src="<s:url value="/images/search/Forward_off.png" />"
															onMouseOver="this.src='<s:url value="/images/search/Forward_on.png" />'"
															onMouseOut="this.src='<s:url value="/images/search/Forward_off.png" />'"
															title="Next" width="24" height="24" border="0px"
															class="dblk"> </a>
												</td>
											</s:if>
											<s:else>
												<td>
													<img
														src="<s:url value="/images/search/Forward_gray.png" />"
														width="24" height="24" class="dblk">
												</td>
											</s:else>
											<td>
												<s:textfield name="searchGotoPage" id="searchGotoPageId"
													cssStyle="width: 30px;" cssClass="normal" onkeypress="return gotoPagePress(event);"  />
											</td>
											<s:if
												test="%{searchResult.pageCount > 1}">
												<td width="100%" style="padding-left: 5px">
													<a
														href="javascript: searchPaging('<%=Navigation.OPERATION_GOTO_PAGE%>');">
														<img src="<s:url value="/images/search/GoTo_off.png" />"
															onMouseOver="this.src='<s:url value="/images/search/GoTo_on.png" />'"
															onMouseOut="this.src='<s:url value="/images/search/GoTo_off.png" />'"
															title="Go to page" width="24" height="24" border="0px"
															class="dblk"> </a>
												</td>
											</s:if>
											<s:else>
												<td width="100%" style="padding-left: 5px">
													<img
														src="<s:url value="/images/search/GoTo_gray.png" />"
														width="24" height="24" class="dblk">
												</td>
											</s:else>
										</tr>
									</table>
								</s:if>
							</td>
							<td align="right" style="padding-right:0px;" width="24px">
								<a href="javascript:searchOption()"><img
										src="<s:url value="/images/search/Settings_off.png" />"
										onMouseOver="this.src='<s:url value="/images/search/Settings_on.png" />'"
										onMouseOut="this.src='<s:url value="/images/search/Settings_off.png" />'"
										title="Search Options" width="24" height="24" border="0px"
										class="dblk"> </a>
							</td>
							<td align="right" style="padding-right:15px;" width="24px">
								<a href="javascript:closeSearchPanel()"><img
										src="<s:url value="/images/search/Cancel_off.png" />"
										onMouseOver="this.src='<s:url value="/images/search/Cancel_on.png" />'"
										onMouseOut="this.src='<s:url value="/images/search/Cancel_off.png" />'"
										title="Close Search" width="24" height="24" border="0px"
										class="dblk"> </a>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td colspan="2" style="padding-left: 20px">
								<div id="searchOptionDiv" style="display: none;">
									<div class="bd">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td>
													<fieldset style="width: 180px">
														<legend>
															Search Options
														</legend>
														<div>
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td height="5px"></td>
																</tr>
																<tr>
																	<td class="searchPageNum" nowrap align="left"
																		style="padding-left: 10px;">
																		Items per page:
																	</td>
																	<td>
																		<s:select cssClass="normal" id="searchPageSize" name="searchPageSize"
																			list="#{'20':'20', '30':'30', '50':'50', '100':'100'}"
																			value="%{searchResult.pageSize}"
																			onchange="%{'resizeSearchPage();'}" />
																	</td>
																</tr>
																<tr>
																	<td height="5px"></td>
																</tr>
															</table>
														</div>
													</fieldset>
												</td>
											</tr>
										</table>
									</div>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="resultTitle" style="padding-left: 5px">
					Search Results (<s:property value="searchResult.totalCount" /> found)
				</td>
			</tr>
			<tr>
				<td style="padding-left: 10px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<s:iterator id="searchItem" value="%{searchResult.pageResult}">
							<tr>
								<td style="font-size: 13px;">
									<a class="searchItem"
										href='<s:url value="%{#searchItem.url}" includeParams="none" />'
										title='<s:property value="%{#searchItem.title}" />'><s:property
											value="%{#searchItem.description}" /> </a>
								</td>
							</tr>
							<tr>
								<td style="font-size: 12px; padding-left: 10px">
									<s:property escape="false" value="%{#searchItem.detail}" />
								</td>
							</tr>
							<tr>
								<td height="2px"></td>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
