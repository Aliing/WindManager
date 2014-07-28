<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
var navTree = new Array();
<s:iterator id="l2Feature" value="%{selectedL1Feature.childNodes}" status="l2Status">
	navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />, id: "<s:property value="key" />", oe: <s:property value="selectedTree" />};
	<s:iterator id="l3Feature" value="%{#l2Feature.childNodes}" status="l3Status">
		navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />};
		<s:iterator id="l4Feature" value="%{#l3Feature.childNodes}" status="l4Status">
			navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />};
			<s:iterator id="l5Feature" value="%{#l4Feature.childNodes}" status="l5Status">
				navTree[navTree.length] = {cc: <s:property value="childCount" />, e: <s:property value="expanded" />};
			</s:iterator>
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
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<s:if test="%{#l2Status.even}">
					<tr>
						<td colspan="10" height="20px">
						</td>
					</tr>
					<tr>
						<td style="padding:0 4px 0 4px" colspan=10>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td class="sepLine">
										<img src="<s:url value="/images/spacer.gif"/>" height="1"
											class="dblk" />
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="10" height="10px">
						</td>
					</tr>
				</s:if>
			
				<s:if test="%{#l2Feature.headerNode}">
					<tr>
						<td><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="10" height="1" alt="" class="dblk" /></td>
						<td colspan="2" width="100%"><a
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
								<td class="<s:property value="%{#headerFeature.tdStyleClass}"/>Grdt">&nbsp;</td>
								<s:if test="%{lstTitle.size > 1}">
									<td style="padding-left: 9px;<s:property value="%{#paddingTop}"/>" class="<s:property value="%{#headerFeature.styleClass}"/> <s:property value="%{#headerFeature.tdStyleClass}"/>"
										colspan="2"><s:property
										value="%{#headerFeature.shortDescription}" /></td>
								</s:if>
								<s:else>
									<td
										style="padding-left: 9px;<s:property value="%{#paddingTop}"/>" class="<s:property value="%{#headerFeature.tdStyleClass}"/>"
										colspan="2"><a
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
									style="padding-left: 11px;<s:property value="%{#paddingTop}"/>"
									colspan="2"><s:property
									value="%{#headerFeature.shortDescription}" /></td>
							</tr>
						</s:else>
						<s:iterator id="l3Feature" value="%{#headerFeature.childNodes}"
							status="l3Status">
							<tr id="mr<s:property
							value="%{#l3Feature.rowIndex}" />"
								style="<s:property value="%{#l3Feature.rowDisplay}"/>">
								<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
								<td style="padding-left: 8px;"
									class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><img
									src="<s:url value="%{#l3Feature.treeImage}" includeParams="none"/>"
									alt="" class="dblk" width="23" height="23" /></td>
								<s:if test="%{lstTitle.size > 1}">
									<td width="100%"
										class="<s:property value="%{#l3Feature.styleClass}"/> <s:property value="%{#l3Feature.tdStyleClass}"/>"
										style="padding-left:2px;"><s:property
										value="%{#l3Feature.shortDescription}" /></td>
								</s:if>
								<s:else>
									<td width="100%"
										class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><a
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
					<tr>
						<td class="<s:property value="%{#l2Feature.tdStyleClass}"/>Grdt"><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="10" height="1" alt="" class="dblk" /></td>
						<s:if
							test="%{#l2Feature.collapsible && #l2Feature.childCount > 0}">
							<td colspan="4" width="100%" style="padding-left: 1px;"><a
								id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
								class="<s:property value="%{#l2Feature.styleClass}"/>"
								href="javascript: toggleHeader(<s:property
							value="%{#l2Feature.rowIndex}" />, <s:property
							value="%{#l2Feature.childCount}" />);"><s:property
								value="%{#l2Feature.shortDescription}" /></a></td>
						</s:if>
						<s:elseif test="%{lstTitle.size > 1}">
							<td colspan="4" width="100%"
								class="<s:property value="%{#l2Feature.styleClass}"/> <s:property value="%{#l2Feature.tdStyleClass}"/>"
								style="padding-left: 4px;"><s:property
								value="%{#l2Feature.shortDescription}" /></td>
						</s:elseif>
						<s:else>
							<td colspan="4" width="100%"
								class="<s:property value="%{#l2Feature.tdStyleClass}"/>"
								style="padding-left: 1px;"><a
								id="a<s:property
							value="%{#l2Feature.rowIndex}" />"
								class="<s:property value="%{#l2Feature.styleClass}"/>"
								href="<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l2Feature.key}" /></s:url>"><s:property
								value="%{#l2Feature.shortDescription}" /></a></td>
						</s:else>
					</tr>
					<s:iterator id="l3Feature" value="%{#l2Feature.childNodes}"
						status="l3Status">
						<s:if test="%{#l3Feature.headerNode}">
							<tr id="mr<s:property
								value="%{#l3Feature.rowIndex}" />"
								style="<s:property value="%{#l3Feature.rowDisplay}"/>">
								<td class="<s:property value="%{#l3Feature.tdStyleClass}" />Grdt">&nbsp;</td>
								<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><img
										src="<s:url value="/images/spacer.gif" includeParams="none"/>"
										width="10" height="1" alt="" class="dblk" /></td>
								<td class="leftNavH3" colspan="3" width="100%"><s:property
											value="%{#l3Feature.shortDescription}" /></td>
							</tr>
							<s:iterator id="l4Feature" value="%{#l3Feature.childNodes}"
								status="l4Status">
								<s:if test="%{#l4Status.first}">
									<s:set name="paddingTop" value="%{'padding-top: 2px;'}" />
								</s:if>
								<s:else>
									<s:set name="paddingTop" value="%{''}" />
								</s:else>
								<s:if test="%{#l4Feature.summary}">
									<%-- a header node with a summary page --%>
									<tr id="mr<s:property value="%{#l4Feature.rowIndex}" />" 
										style="<s:property value="%{#l4Feature.rowDisplay}"/>">
										<td class="<s:property value="%{#l4Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
										<td class="<s:property value="%{#l4Feature.tdStyleClass}"/>"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="10" height="1" alt="" class="dblk" /></td>
										<s:if test="%{lstTitle.size > 1}">
											<td style="padding-left: 11px;<s:property value="%{#paddingTop}"/>" class="<s:property value="%{#l4Feature.styleClass}"/> <s:property value="%{#l4Feature.tdStyleClass}"/>"
												colspan="3"><s:property
												value="%{#l4Feature.shortDescription}" /></td>
										</s:if>
										<s:else>
											<td
												style="padding-left: 11px;<s:property value="%{#paddingTop}"/>" class="<s:property value="%{#l4Feature.tdStyleClass}"/>"
												colspan="3"><a
												class="<s:property value="%{#l4Feature.styleClass}"/>"
												href='<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l4Feature.key}" /></s:url>'><s:property
												value="%{#l4Feature.shortDescription}" /></a></td>
										</s:else>
									</tr>
								</s:if>
								<s:else>
									<tr
										id="mr<s:property
									value="%{#l4Feature.rowIndex}" />"
										style="<s:property value="%{#l4Feature.rowDisplay}"/>">
										<td colspan="2"></td>
										<td class="leftNavH3"
											style="padding-left: 11px;<s:property value="%{#paddingTop}"/>"
											colspan="3"><s:property
											value="%{#l4Feature.shortDescription}" /></td>
									</tr>
								</s:else>
								<s:iterator id="l5Feature" value="%{#l4Feature.childNodes}"
									status="l5Status">
									<tr id="mr<s:property
									value="%{#l5Feature.rowIndex}" />"
										style="<s:property value="%{#l5Feature.rowDisplay}"/>">
										<td class="<s:property value="%{#l5Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
										<td class="<s:property value="%{#l5Feature.tdStyleClass}"/>" colspan="2"><img
											src="<s:url value="/images/spacer.gif" includeParams="none"/>"
											width="10" height="1" alt="" class="dblk" /></td>
										
										<td
											class="<s:property value="%{#l5Feature.tdStyleClass}"/>"><img
											src="<s:url value="%{#l5Feature.treeImage}" includeParams="none"/>"
											alt="" class="dblk" width="23" height="23" /></td>
										<s:if test="%{lstTitle.size > 1}">
											<td width="100%"
												class="<s:property value="%{#l5Feature.styleClass}"/> <s:property value="%{#l5Feature.tdStyleClass}"/>"
												style="padding-left:2px;"><s:property
												value="%{#l5Feature.shortDescription}" /></td>
										</s:if>
										<s:else>
											<td width="100%"
												class="<s:property value="%{#l5Feature.tdStyleClass}"/>"><a
												class="<s:property value="%{#l5Feature.styleClass}"/>"
												style="padding-left:2px;"
												href='<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l5Feature.key}" /></s:url>'><s:property
												value="%{#l5Feature.shortDescription}" /></a></td>
										</s:else>
									</tr>
								</s:iterator>
							</s:iterator>
						</s:if>
						<s:else>
							<tr id="mr<s:property
								value="%{#l3Feature.rowIndex}" />"
								style="<s:property value="%{#l3Feature.rowDisplay}"/>">
								
								<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
								<td class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><img
									src="<s:url value="/images/spacer.gif" includeParams="none"/>"
									width="10" height="1" alt="" class="dblk" /></td>
							
								<s:if
									test="%{#l3Feature.childCount > 0}">
									<td class="leftNavH3" colspan="3" width="100%" style="padding-left: 1px;"><s:property
											value="%{#l3Feature.shortDescription}" /></td>
								</s:if>
								<s:elseif test="%{lstTitle.size > 1}">
									<td width="100%" colspan="3"
										class="<s:property value="%{#l3Feature.styleClass}"/> <s:property value="%{#l3Feature.tdStyleClass}"/>"
										style="padding-left:2px;"><s:property
										value="%{#l3Feature.shortDescription}" /></td>
								</s:elseif>
								<s:else>
									<td width="100%" colspan="3"
										class="<s:property value="%{#l3Feature.tdStyleClass}"/>"><a
										class="<s:property value="%{#l3Feature.styleClass}"/>"
										style="padding-left:2px;"
										href="<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l3Feature.key}" /></s:url>"><s:property
										value="%{#l3Feature.shortDescription}" /></a></td>
								</s:else>
							</tr>

							<s:iterator id="l4Feature" value="%{#l3Feature.childNodes}"
								status="l4Status">
								<tr id="mr<s:property
								value="%{#l4Feature.rowIndex}" />"
									style="<s:property value="%{#l4Feature.rowDisplay}"/>">
									<td class="<s:property value="%{#l4Feature.tdStyleClass}"/>Grdt">&nbsp;</td>
									<td class="<s:property value="%{#l4Feature.tdStyleClass}"/>"><img
										src="<s:url value="/images/spacer.gif" includeParams="none"/>"
										width="10" height="1" alt="" class="dblk" /></td>
									<td class="<s:property value="%{#l4Feature.tdStyleClass}"/>"><img
										src="<s:url value="%{#l4Feature.treeImage}" includeParams="none"/>"
										alt="" class="dblk" width="18" height="18" /></td>
									<s:if test="%{lstTitle.size > 1}">
										<td width="100%" colspan="2"
											class="<s:property value="%{#l4Feature.styleClass}"/> <s:property value="%{#l4Feature.tdStyleClass}"/>"
											style="padding-left:2px;"><s:property
											value="%{#l4Feature.shortDescription}" /></td>
									</s:if>
									<s:else>
										<td width="100%" colspan="2"
											class="<s:property value="%{#l4Feature.tdStyleClass}"/>"><a
											class="<s:property value="%{#l4Feature.styleClass}"/>"
											style="padding-left:2px;"
											href='<s:url action="%{selectedL1Feature.action}" includeParams="none"><s:param name="operation" value="%{#l4Feature.key}" /></s:url>'><s:property
											value="%{#l4Feature.shortDescription}" /></a></td>
									</s:else>
								</tr>
							</s:iterator>
						</s:else>
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
	</s:iterator>
</table>
</div>
