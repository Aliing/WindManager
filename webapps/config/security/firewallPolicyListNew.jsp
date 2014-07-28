<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'fwPolicySelectPage';

YAHOO.util.Event.onContentReady("selectFwPolicyDiv", adjustEditLink, this);

function finishSelectFw(){
	var itemValue = hm.util.getSelectedCheckItems("selectFwPolicy");
	var idPara = "";
	if (hm.util._LIST_SELECTION_NOITEM != itemValue && hm.util._LIST_SELECTION_NOSELECTION != itemValue) {
		idPara = "&selectFwPolicyId="+itemValue[0];
	}
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=finishSelectFwPolicy"+idPara+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFinishSelectFw, failure : resultDoNothing, timeout: 60000}, null);	
}

function newFwPolicy() {
	hideSubDialogOverlay();
	var url = "<s:url action='firewallPolicy' includeParams='none' />?operation=newFw&ignore="+new Date().getTime();
	openIFrameDialog(900, 450, url);	
}

function editFirewallPolicy(fwId, event) {
	parent.hideSubDialogOverlay();
	
	// open firewall policy edit dialog
	var url = "<s:url action='firewallPolicy' includeParams='none' />?operation=editFw&contentShowType=dlg&id="+fwId+"&ignore="+new Date().getTime();
	parent.openIFrameDialog(800, 450, url);
	
   	// stop bubble!!!!
    hm.util.stopBubble(event);
}
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="fwPolicySelectPage" id="fwPolicySelectPage">
<s:hidden name="operation" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><div id="errNote" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td>
							<ah:checkList name="selectFwPolicy" list="availableFwPolicy" listKey="id" listValue="value"
							 value="selectFwPolicyId" editEvent="editFirewallPolicy" width="100%"/>
						</td>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<s:if test="%{writeDisabled == 'disabled'}">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
										<td width="40px">&nbsp;</td>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" title="<s:text name="config.v2.select.network.policy.button.new"/>"><span><s:text name="config.v2.select.network.policy.button.new"/></span></a></td>
									</tr>
								</s:if>
								<s:else>
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="finishSelectFw();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
										<td width="40px">&nbsp;</td>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="newFwPolicy();" title="<s:text name="config.v2.select.network.policy.button.new"/>"><span><s:text name="config.v2.select.network.policy.button.new"/></span></a></td>
									</tr>
								</s:else>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</s:form>
</div>
