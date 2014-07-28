<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
var navTree = new Array();
<s:iterator id="l2Feature" value="%{selectedL1Feature.childNodes}" status="l2Status">
	navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />, id: "<s:property value="key" />", oe: <s:property value="selectedTree" />};
	<s:iterator id="l3Feature" value="%{#l2Feature.childNodes}" status="l3Status">
		navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />};
		<s:iterator id="l4Feature" value="%{#l3Feature.childNodes}" status="l4Status">
			navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />};
		</s:iterator>
	</s:iterator>
</s:iterator>
function toggleHeader(index, childCount) {
	var a = document.getElementById('a' + index);
	a.blur();
	toggleNode(index, childCount);
	var url = "<s:url value="navigationTree.action" includeParams="none"></s:url>";
	if (navTree[index].e) {
		hm.util.invokeTreeOperation(url, "expand", navTree[index].id);
		collapseOthers(index, url);
	} else {
		hm.util.invokeTreeOperation(url, "collapse", navTree[index].id);
	}
	sizeLeftMenu();
}
function toggleNode(index, childCount) {
	var node = navTree[index];
	for (var i = index; i < index + childCount; i++) {
		if (node.e) {
			hm.util.hide('mr' + (i + 1));
		} else {
			hm.util.show('mr' + (i + 1));
		}
	}
	node.e = !node.e;
}
function collapseOthers(index, url) {
	for (var i = 0; i < navTree.length; i++) {
		if (navTree[i].cc > 0) {
			if (i != index && navTree[i].e && !navTree[i].oe) {
				toggleNode(i, navTree[i].cc);
				hm.util.invokeTreeOperation(url, "collapse", navTree[i].id);
			}
			i += navTree[i].cc - 1;
		}
	}
}
function toggleSummary(index, childCount) {
	var a = document.getElementById('a' + index);
	a.blur();
	var url = "<s:url value="navigationTree.action" includeParams="none"></s:url>";
	for (var i = index + 1; i <= index - childCount; i++) {
		if (navTree[index].e) {
			hm.util.hide('mr' + i);
		} else {
			hm.util.show('mr' + i);
		}
		if (navTree[i].cc > 0) {
			if (navTree[index].e && navTree[i].e) {
				toggleNode(i, navTree[i].cc);
				hm.util.invokeTreeOperation(url, "collapse", navTree[i].id);
			}
			i += navTree[i].cc;
		}
	}
	navTree[index].e = !navTree[index].e;
	if (navTree[index].e) {
		hm.util.invokeTreeOperation(url, "expand", navTree[index].id);
		collapseOthers(index, url);
	} else {
		hm.util.invokeTreeOperation(url, "collapse", navTree[index].id);
	}
	
	handleConfigSearchBox(navTree[index]);
	
	sizeLeftMenu();
}
function handleConfigSearchBox(navNode) {
	// display or no for the search configuration function
	if(navNode.id == 'advancedConfig') {
		var section = document.getElementById("searchConfigSection");
		if(section) {
			if(navNode.e) {
				section.style.display = "";
			} else {
				section.style.display = "none";
			}
		}
	}
}
</script>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="leftNavH1"><s:property
			value="selectedL1Feature.description" /></td>
	</tr>
</table>
<div id="leftMenuOff" style="overflow:auto;">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<s:iterator id="l2Feature" value="%{selectedL1Feature.childNodes}"
		status="l2Status">
		<%-- <s:if test="%{#l2Feature.shortDescription != 'Guided Configuration' || easyMode}"> //show 'Guided Configuration item always'--%>
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<s:if test="%{#l2Feature.headerNode}">
					<tr id="mr<s:property value="%{#l2Feature.rowIndex}" />"
						style="<s:property value="%{#l2Feature.rowDisplay}"/>">
						<td><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="10" height="1" alt="" class="dblk" /></td>
						<td colspan="2" width="100%"
							style="padding-left: <s:property value="%{#l2Feature.indent}"/>px;" nowrap="nowrap"><a
							id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
							class="<s:property value="%{#l2Feature.styleClass}" />"
							href="javascript: toggleHeader(<s:property
							value="%{#l2Feature.rowIndex}" />, <s:property
							value="%{#l2Feature.childCount}" />);"><s:property
							value="%{#l2Feature.shortDescription}" /></a></td>
					</tr>
					<s:iterator id="headerFeature" value="%{#l2Feature.childNodes}"
						status="headerStatus">
						<s:if test="%{#headerStatus.first}">
							<s:set name="paddingTop" value="%{'padding-top: 2px;'}" />
						</s:if>
						<s:else>
							<s:set name="paddingTop" value="%{''}" />
						</s:else>
						<s:if test="%{#headerFeature.summary}">
							<%-- a header node with a summary page --%>
							<tr
								id="mr<s:property
							value="%{#headerFeature.rowIndex}" />"
								style="<s:property value="%{#headerFeature.rowDisplay}"/>">
								<td
									class="<s:property value="%{#headerFeature.tdStyleClass}"/>Grdt">&nbsp;</td>
								<s:if test="%{lstTitle.size > 1}">
									<td
										style="padding-left: <s:property value="%{#l2Feature.indent+9}"/>px;<s:property value="%{#paddingTop}"/>"
										class="<s:property value="%{#headerFeature.styleClass}"/> <s:property value="%{#headerFeature.tdStyleClass}"/>"
										colspan="2" nowrap="nowrap"><s:property
										value="%{#headerFeature.shortDescription}" /></td>
								</s:if>
								<s:else>
									<td
										style="padding-left: <s:property value="%{#l2Feature.indent+9}"/>px;<s:property value="%{#paddingTop}"/>"
										class="<s:property value="%{#headerFeature.tdStyleClass}"/>"
										colspan="2" nowrap="nowrap"><a
										class="<s:property value="%{#headerFeature.styleClass}"/>"
										href='<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#headerFeature.key}" /></s:url>'><s:property
										value="%{#headerFeature.shortDescription}" /></a></td>
								</s:else>
							</tr>
						</s:if>
						<s:else>
							<tr
								id="mr<s:property
							value="%{#headerFeature.rowIndex}" />"
								style="<s:property value="%{#headerFeature.rowDisplay}"/>">
								<td></td>
								<td class="leftNavH3"
									style="padding-left: <s:property value="%{#l2Feature.indent+11}"/>px;<s:property value="%{#paddingTop}"/>"
									colspan="2" nowrap="nowrap"><s:property
									value="%{#headerFeature.shortDescription}" /></td>
							</tr>
						</s:else>
						<s:iterator id="l3Feature" value="%{#headerFeature.childNodes}"
							status="l3Status">
							<tr id="mr<s:property
							value="%{#l3Feature.rowIndex}" />"
								style="<s:property value="%{#l3Feature.rowDisplay}"/>">
								<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
								<td
									style="padding-left: <s:property value="%{#l2Feature.indent+8}"/>px;"
									class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><img
									src="<s:url value="%{#l3Feature.treeImage}" includeParams="none"/>"
									alt="" class="dblk" width="23" height="23" /></td>
								<s:if test="%{lstTitle.size > 1}">
									<td width="100%"
										class="<s:property value="%{#l3Feature.styleClass}"/> <s:property value="%{#l3Feature.tdStyleClass}"/>"
										style="padding-left:2px;" nowrap="nowrap"><s:property
										value="%{#l3Feature.shortDescription}" /></td>
								</s:if>
								<s:else>
									<td width="100%"
										class="<s:property value="%{#l3Feature.tdStyleClass}"/>" nowrap="nowrap"><a
										class="<s:property value="%{#l3Feature.styleClass}"/>"
										style="padding-left:2px;"
										href='<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l3Feature.key}" /></s:url>'><s:property
										value="%{#l3Feature.shortDescription}" /></a></td>
								</s:else>
							</tr>
						</s:iterator>
					</s:iterator>
				</s:if>
				<s:else>
					<s:if test="%{#l2Feature.indent < 0}">
						<tr>
							<td style="padding:5px 4px 5px 4px" colspan=10>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td class="sepLine"><img
										src="<s:url value="/images/spacer.gif"/>" height="1"
										class="dblk" /></td>
								</tr>
							</table>
							</td>
						</tr>
					</s:if>
					<tr id="mr<s:property value="%{#l2Feature.rowIndex}" />"
						style="<s:property value="%{#l2Feature.rowDisplay}"/>">
						<td class="<s:property value="%{#l2Feature.tdStyleClass}"/>Grdt"><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="10" height="1" alt="" class="dblk" /></td>
						<s:if test="%{#l2Feature.summary}">
							<td colspan="2" width="100%" nowrap="nowrap"><a
								id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
								class="leftNavH2"
								href="javascript: toggleSummary(<s:property
							value="%{#l2Feature.rowIndex}" />, <s:property
							value="%{#l2Feature.childCount}" />);"><s:property
								value="%{#l2Feature.shortDescription}" /></a></td>
						</s:if>
						<s:elseif
							test="%{#l2Feature.collapsible && #l2Feature.childCount > 0}">
							<td colspan="2" width="100%"
								style="padding-left: <s:property value="%{#l2Feature.indent+1}"/>px;" nowrap="nowrap"><a
								id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
								class="<s:property value="%{#l2Feature.styleClass}"/>"
								href="javascript: toggleHeader(<s:property
							value="%{#l2Feature.rowIndex}" />, <s:property
							value="%{#l2Feature.childCount}" />);"><s:property
								value="%{#l2Feature.shortDescription}" /></a></td>
						</s:elseif>
						<s:elseif test="%{lstTitle.size > 1}">
							<td colspan="2" width="100%"
								class="<s:property value="%{#l2Feature.styleClass}"/> <s:property value="%{#l2Feature.tdStyleClass}"/>"
								style="padding-left: <s:property value="%{#l2Feature.indent+4}"/>px;" nowrap="nowrap"><s:property
								value="%{#l2Feature.shortDescription}" /></td>
						</s:elseif>
						<s:else>
							<td colspan="2" width="100%"
								class="<s:property value="%{#l2Feature.tdStyleClass}"/>"
								style="padding-left: <s:property value="%{#l2Feature.indent+1}"/>px;" nowrap="nowrap"><a
								id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
								class="<s:property value="%{#l2Feature.styleClass}"/>"
								href="<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l2Feature.key}" /></s:url>"><s:property
								value="%{#l2Feature.shortDescription}" /></a></td>
						</s:else>
					</tr>
					<s:iterator id="l3Feature" value="%{#l2Feature.childNodes}"
						status="l3Status">
						<tr id="mr<s:property
							value="%{#l3Feature.rowIndex}" />"
							style="<s:property value="%{#l3Feature.rowDisplay}"/>">
							<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
							<td
								style="padding-left: <s:property value="%{#l2Feature.indent}"/>px;"
								class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><img
								src="<s:url value="%{#l3Feature.treeImage}" includeParams="none"/>"
								alt="" class="dblk" width="23" height="23" /></td>
							<s:if test="%{lstTitle.size > 1}">
								<td width="100%"
									class="<s:property value="%{#l3Feature.styleClass}"/> <s:property value="%{#l3Feature.tdStyleClass}"/>"
									style="padding-left:2px;" nowrap="nowrap"><s:property
									value="%{#l3Feature.shortDescription}" /></td>
							</s:if>
							<s:else>
								<td width="100%"
									class="<s:property value="%{#l3Feature.tdStyleClass}"/>" nowrap="nowrap"><a
									class="<s:property value="%{#l3Feature.styleClass}"/>"
									style="padding-left:2px;"
									href="<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l3Feature.key}" /></s:url>"><s:property
									value="%{#l3Feature.shortDescription}" /></a></td>
							</s:else>
						</tr>
					</s:iterator>
				</s:else>
			</table>
			</td>
		</tr>
		<s:if test="%{#l2Feature.collapsible && #l2Feature.childCount > 0}">
			<tr>
				<td height="1"></td>
			</tr>
		</s:if>
		<%-- </s:if> --%>
	</s:iterator>
	<s:if test="%{teacherView}">
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="Grdt"><img src="/hm/images/spacer.gif"
						width="10" height="1" alt="" class="dblk" /></td>
					<td colspan="2" width="100%" style="padding-left: 1px;">
						<a class="leftNavL1" href='<s:url value="teacherView.action"/>'>View Class</a>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</s:if>
</table>
</div>
