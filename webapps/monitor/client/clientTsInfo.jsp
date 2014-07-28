<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!-- WMM-AC Tsinfo -->
<div class="hd">
	<s:text name="monitor.activeClient.operation.tsinfo.title" />
</div>

<div class="bd">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" border="0" id="tbl_tsinfo"
					class="view">
					<tr>
						<th class="colList" width="50em;"></th>
						<th class="colList" width="50em;"><s:text
								name="monitor.activeClient.operation.tsinfo.table.tid" /></th>
						<th class="colList" width="50em" align="center"><s:text
								name="monitor.activeClient.operation.tsinfo.table.admctl" /></th>
						<th class="colList" width="50em" align="center"><s:text
								name="monitor.activeClient.operation.tsinfo.table.ac" /></th>
						<th class="colList" width="80em" align="center"><s:text
								name="monitor.activeClient.operation.tsinfo.table.direction" />
						</th>
						<th class="colList" width="150em;"><s:text
								name="monitor.activeClient.operation.tsinfo.table.up" /></th>
						<th class="colList" width="150em;"><s:text
								name="monitor.activeClient.operation.tsinfo.table.psb" /></th>
						<th class="colList" width="150em;" align="center"><s:text
								name="monitor.activeClient.operation.tsinfo.table.medium" /></th>
					</tr>
					<s:if test="%{clientTsinfo != null && clientTsinfo.size > 0}">
					<tbody>
					<s:iterator value="%{clientTsinfo}" status="status"	id="clientTsinfo">
						<s:if test="%{#clientTsinfo.tid != null && #clientTsinfo.tid != ''}">
							<tiles:insertDefinition name="rowClass" />
							<tr id="row_<s:property value='%{#clientTsinfo.tid}' />" class="<s:property value="%{#rowClass}"/>"
								style="height: 25px;">
								<td class="colList" style="padding-left: 3px">
									<input id="tids_<s:property value='%{#clientTsinfo.tid}' />" name="tids" type="checkbox" value="<s:property value='%{#clientTsinfo.tid}' />"/>
								</td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.tid}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.admctl}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.ac}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.direction}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.up}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.psb}" /></td>
								<td class="colList" style="padding-left: 3px"><s:property
										value="%{#clientTsinfo.mediumTime}" /></td>
							</tr>
						</s:if>
						</s:iterator>
					</tbody>
				</s:if>
				<s:else>
					<tr style="height: 25px;">
						<td id="noItem" class="colList" style="padding-left: 3px" colspan="10"><span><s:text
								name="config.optionsTransfer.none" /></span></td>
					</tr>
				</s:else>
				</table>
			</td>
		</tr>
		<tr>
			<td align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<s:if test="%{clientTsinfo != null && clientTsinfo.size > 0}">
							<td><input type="button" name="commit"
								value="<s:text name="monitor.activeClient.operation.tsinfo.delete.button" />"
								class="button" onClick="deleteTs(getSelectedIds());"
								<s:property value="writeDisabled" />></td>
						</s:if>
						<td>
							<input type="button" name="cancel" onClick="hideEditTsinfoPanel();"
							value="<s:text name="common.button.cancel" />" class="button" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
function getSelectedIds () {
	var cbs = document.getElementsByName('tids');
	var selecteds = new Array();
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			selecteds[selecteds.length] = cbs[i].value;
		}
	}
	return selecteds;
}


</script>
