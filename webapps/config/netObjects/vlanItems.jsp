<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style>
td.listString {
	padding-right: 25px;
	font-size: 14px;
	padding-bottom: 8px;
	padding-top: 3px;
	cursor: pointer;
}
table.view tr.even.hover {
	background-color: #FFE43F;
}
table.view tr.odd.hover {
	background-color: #FFE43F;
}
</style>
<script>
var formName = 'vlanItemsForm';
function onLoadPage() {
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialogWithoutPosChg(450,350);
	 	}
 	</s:if>
}
var domainId=<s:property value="domainId" />;
</script>

<div id="content">
<s:form action="networkPolicy" name="vlanItemsForm" id="vlanItemsForm">
	<s:hidden name="operation" />
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="vlanItemsDlgTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td>
					<table>
						<tr>
							<td width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="dialogPanelTitle"><s:text name="config.networkpolicy.ssid.list.vlan"/></td>
									<td style="padding-left:10px;">
										<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
											<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
												alt="" class="dblk"/>
										</a>
									</td>
								</tr>
							</table>
							</td>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="right" style="padding-left:10px;" width="80px" nowrap><a id="close_dlg_link" href="javascript:void(0);" class="btCurrent"
										onclick="javascript: parent.hideIFrameNoModalPanel(true);" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</div>
		<div style="width:100%; height: 220px; overflow: auto;">
		<s:if test="%{jsonMode == true && contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
				<tr>
					<td>
						<div>
							<table id="vlan_items_container_tbl" class="view" cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<th style="width:20%;">
										<s:text name="config.networkpolicy.ssid.list.vlan" />										
									</th>
									<th style="width: 20%;">
										<s:text name="config.vlan.vlanId.classification.type" />
									</th>
									<th style="width: 50%;">
										<s:text name="config.vlan.vlanId.value" />
									</th>
								</tr>
								<tbody>
									<s:iterator value="%{vlan.items}" status="status" id="items">
									<s:if test="%{#status.even}">
										<tr class="even">
									</s:if>
									<s:else>
										<tr class="odd">
									</s:else>
										<td class="listString">
											<span><s:property value="%{#items.vlanId}" /></span>
										</td>
										<td class="listString">
											<span><s:property value="%{#items.useTypeName}" /></span>
										</td>
										<td class="listString">											
											<span><s:property value="getTagValue(domainId)"/></span>
										</td>
									</tr>
									</s:iterator>
								</tbody>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<div>
			<table align="center">
				<tr style="height: 15px"><td></td></tr>
				<tr>
					<s:if test="%{updateDisabled != 'disabled'}">
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="javascript: parent.hideIFrameNoModalPanel(true);" title="<s:text name="common.button.ok"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.ok"/></span></a></td>
					</s:if>
					<s:else>
						<td class="npcButton"></td>
					</s:else>
				</tr>
			</table>
		</div>
	</s:if>
</s:form>
</div>
<script>
$(function() {
	$("#vlan_items_container_tbl tbody tr").mouseover(function(e) {
		$(this).addClass("hover");
	}).mouseout(function(e) {
		$(this).removeClass("hover");
	});
});
</script>