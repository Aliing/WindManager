<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
.warningMsgMitigate{max-height:600px;overflow-y:auto;}
</style>

<script type="text/javascript">
var manualMitigatePanel;
var reassignFormName = 'rogueApManualMode';

YAHOO.util.Event.onDOMReady(function(){
	//Create enter entitle key panel
	var div = document.getElementById('manualMitigatePanel');
	manualMitigatePanel = new YAHOO.widget.Panel(div, { width:"520px",visible:false,fixedcenter:true,draggable:false,constraintoviewport:true,modal:true,zIndex:100 } );
	manualMitigatePanel.render(document.body);
	// enterPanel.moveTo(1,1);//fix scroll bar issue
	div.style.display = "";
	
	<s:if test="%{null != manualModeAp && manualModeAp.sameHiveApList.size() > 0}">
		changeManualMitigatePanel(true);
	</s:if>
});

function changeManualMitigatePanel(checked) {
	if (manualMitigatePanel != null) {
		manualMitigatePanel.cfg.setProperty('visible', checked);
	}
	if (!checked) {
		submitMitigateManualAction("manualMitigateCancel");
	}
}

function submitMitigateManualAction(operValue) {
	document.forms[reassignFormName].operation.value = operValue;
	document.forms[reassignFormName].submit();
}
</script>

<div id="manualMitigatePanel" style="display: none;" class="warningMsgMitigate">
<s:form action="idp" id="rogueApManualMode" name="rogueApManualMode">
<s:hidden name="operation" />
	<div class="bd">
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="500px">
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td style="padding:0 10px 0 10px">
<table cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td>
			<s:text name="monitor.rogueAp.mitigation.manual.mode.message"><s:param><s:property value="manualModeAp.rogueApMac" /></s:param>
			<s:param><s:property value="manualModeAp.rogueApVendor" /></s:param></s:text>
		</td>
	</tr>
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
				<tr>
					<th>&nbsp;</th>
					<th nowrap>
						<s:text name="monitor.rogueAp.mitigation.manual.mode.title.ap"/>
					</th>
					<th nowrap>
						<s:text name="monitor.hiveAp.report.rssi"/>
					</th>
					<th nowrap>
						<s:text name="monitor.rogueAp.mitigation.manual.mode.title.channel"/>
					</th>
					<th nowrap>
						<s:text name="monitor.hiveAp.report.clients"/>
					</th>
				</tr>
				<s:iterator value="manualModeAp.sameHiveApList" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck"><input type="checkbox" name="selectedIdpAps"
						onClick="hm.util.toggleCheck(this);" value="<s:property value="idpId" />-<s:property value="apMac" />" /></td>
						<td class="list"><a href='<s:url value="hiveApMonitor.action" includeParams="none">
							<s:param name="operation" value="%{'hiveApDetails'}"/><s:param name="id" value="%{apId}"/></s:url>'><s:property
							value="hostName" /></a></td>
   						<td class="list"><s:property value="rssiStr" />&nbsp;</td>
   						<td class="list"><s:property value="sameChannel" /></td>
   						<td class="list"><s:property value="clientCount" /></td>
					</tr>
				</s:iterator>
			</table>
		</td>
	</tr>
	<tr>
		<td height="10"></td>
	</tr>
</table>
</td>
			</tr>
			<tr>
				<td align="center">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="ignore" value="Mitigate"
							class="button" onClick="submitMitigateManualAction('manualMitigate');"></td>
						<td><input type="button" name="ignore" value="Cancel"
							class="button" onClick="changeManualMitigatePanel(false);"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td height="4"></td>
			</tr>
		</table>
	</div>
</s:form>
</div>