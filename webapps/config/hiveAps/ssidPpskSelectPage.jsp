<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'ssidPpskServerIpSelectForm';

function finishSelectPpskServerIp4Ssid(){
	var itemValue = hm.util.getSelectedCheckItems('selectedPpskServerIds');
	if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
		Get("errNotePpskServerIpSelectSsid").innerHTML="There is no item.";
	    return;
	} else if (hm.util._LIST_SELECTION_NOSELECTION == itemValue || itemValue == null) {
		Get("errNotePpskServerIpSelectSsid").innerHTML='<s:text name="error.pleaseSelect"><s:param>Private PSK Server</s:param></s:text>';
	    return;
	} else if (itemValue.length > 1) {
		Get("errNotePpskServerIpSelectSsid").innerHTML="Please select one Private PSK Server.";
	    return;
	}
	var url = "<s:url action='ssidProfilesSimple' includeParams='none' />?ignore="+new Date().getTime();
	document.getElementById(formName + "_operation").value = "finishSelectPpsk4Ssid";
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSelectPpskServerIp4Ssid, failure : resultDoNothing, timeout: 60000}, null);	
}

var succSelectPpskServerIp4Ssid = function(o) {
	if (o.resultStatus == false) {
		Get("errNotePpskServerIpSelectSsid").innerHTML= o.errMsg;
	} else {
		fetchConfigTemplate2Page(true);
		hideSubDialogOverlay();
	}
}

</script>

<div id="content"><s:form action="ssidProfilesSimple" name="ssidPpskServerIpSelectForm" id="ssidPpskServerIpSelectForm">
<s:hidden name="operation" />
<s:hidden name="ssidId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><div class="noteInfo" style="width:90%;padding-left: 5px;"><s:property value="ppskNoteInfo" /></div></td>
		</tr>
		<tr>
			<td><div id="errNotePpskServerIpSelectSsid" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td>
							<ah:checkList name="selectedPpskServerIds" list="allPpskServerList" listKey="id" listValue="value" 
									value="defaultPpskServerIds" multiple="false" width="100%" autoSort="false"/>
						</td>
					</tr>
					<tr>
						<td height="15px"></td>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<s:if test="%{saveSsidPermit == true}">
										<td class="npcButton" align="center"><a href="javascript:void(0);" onclick="javascript:finishSelectPpskServerIp4Ssid();" class="btCurrent" title="<s:text name="common.button.ok"/>"><span><s:text name="common.button.ok"/></span></a></td>
									</s:if>
									<s:else>
										<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent" title="<s:text name="common.button.ok"/>"><span><s:text name="common.button.ok"/></span></a></td>
									</s:else>
									</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</s:form>
</div>
