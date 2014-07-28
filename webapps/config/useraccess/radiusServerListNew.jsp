<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'radiusSelectPage';
var YUE = YAHOO.util.Event;
YUE.onContentReady("selectRadiusServerDiv", function() {
	adjustEditLink();
	bindChkEvent();
}, this);

YUE.onContentReady("radiusSelectPage", function() {
	<s:if test="%{enabledCloudBased}">
	var changeRADIUSRadioBtn = function() {
		if(parseInt(this.value)) {
			hm.util.hide("radiusServerRow");
			hm.util.show("staticApRow");
			hm.util.hide("newBtn");
		} else {
			hm.util.show("radiusServerRow");
			hm.util.hide("staticApRow");
			hm.util.show("newBtn");
		}
	}
	YUE.on(["radioCloud", "radioServer"], "click", changeRADIUSRadioBtn);
	hm.util.hide("spaceEmptyRow");
	<s:if test="%{selectdCloudRadio}">
	Get("radioCloud").click();
	</s:if>
	<s:else>
	Get("radioServer").click();
	</s:else>
	</s:if>
}, this);

function finishSelectRS(){
	if(Get("radioCloud") && Get("radioCloud").checked) {
		selectDevice4RS();
	} else {
		var itemValue = hm.util.getSelectedCheckItems("selectRadiusServer");
		if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
			Get("errNote").innerHTML="There is no item.";
		    return;
		} else if (hm.util._LIST_SELECTION_NOSELECTION == itemValue) {
			Get("errNote").innerHTML='<s:text name="error.pleaseSelect"><s:param>RADIUS Server</s:param></s:text>';
		    return;
		} else {
			document.forms[formName].selectRadiusServerId.value = itemValue[0];
		}
		var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
		document.forms[formName].operation.value = "finishSelectRadiusServer";
		YAHOO.util.Connect.setForm(document.getElementById("radiusSelectPage"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succFinishSelectRS, failure : resultDoNothing, timeout: 60000}, null);	
	}
}

function newRadiusServer() {
	hideSubDialogOverlay();
	var url = '<s:url action="radiusAssignment" includeParams="none" />?operation=new&ssidForRadius='+'<s:property value="%{ssidForRs}" />'
		+'&radiusTypeFlag='+'<s:property value="%{radiusTypeFlag}" />'+'&jsonMode=true&ignore='+new Date().getTime();
	openIFrameDialog(950, 500, url);	
}

function editRadiusServer(rsId, event) {
	parent.hideSubDialogOverlay();
	var url = '<s:url action="radiusAssignment" includeParams="none" />?operation=edit&id='+rsId+'&ssidForRadius='+'<s:property value="%{ssidForRs}" />'
		+'&radiusTypeFlag='+'<s:property value="%{radiusTypeFlag}" />'+'&jsonMode=true&ignore='+new Date().getTime();
	parent.openIFrameDialog(950, 500, url);
	
	// stop bubble!!!!
    hm.util.stopBubble(event);	
}

function selectDevice4RS() {
    var itemValue = hm.util.getSelectedCheckItems("selectStaticAp");
    if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
        Get("errNote").innerHTML="There is no item.";
        return;
    } else if (hm.util._LIST_SELECTION_NOSELECTION == itemValue) {
        Get("errNote").innerHTML='<s:text name="error.pleaseSelect"><s:param>Device As Proxy Server</s:param></s:text>';
        return;
    } else {
        document.forms[formName].selectStaticApId.value = itemValue[0];
    }
    var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms[formName].operation.value = "selectedAsp4CloudAuth";
    YAHOO.util.Connect.setForm(document.getElementById("radiusSelectPage"));
    YAHOO.util.Connect.asyncRequest('POST', url, 
            {success : succEnableCloudAuth, failure : resultDoNothing, timeout: 60000}, null);	
}
function succEnableCloudAuth(o) {
	eval("var detail = " + o.responseText);
	if(detail.succ) {
		hideSubDialogOverlay();
	    var notStr = '<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/>';
	    var flag = <s:property value="%{radiusTypeFlag}" />;
	    var ssidId = <s:property value='%{ssidForRs}' />;
	    if (flag == 2) {
	        notStr = '<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/>';
	    }
		if(detail.serverId && detail.serverName) {
		    Get("radiusTD"+flag+"_"+ssidId).innerHTML='<table cellspacing="0" cellpadding="0" border="0"><tr><td> '+ 
		    '<a class="npcLinkA" href="javascript:void(0);" onClick="showRadiusServerSelectDialog('+ssidId+','+detail.serverId+','+flag+');">'+
		    detail.serverName+'</a></td></tr><tr><td class="smallTd">&nbsp;'+notStr+'</td></tr></table>';
		}
	} else {
		if(detail.errMsg) {
			Get("errNote").innerHTML = detail.errMsg;
		} else {
			Get("errNote").innerHTML = "Unknow error.";
		}
	}
}
// bind the onclick event to the checkbox
function bindChkEvent() {
	if(Get("radioCloud")) {
		Get("radioCloud").onclick = function() {
			var radiusServers = document.getElementsByName("selectRadiusServer");
			if(this.checked){
				// diselect the item in list if enable Cloud Auth support
				for(var index = radiusServers.length-1; index >=0; index--) {
					if(radiusServers[index].type == 'checkbox' && radiusServers[index].checked) {
	                    for (var eRow = radiusServers[index]; eRow && eRow.nodeType != 1; eRow = eRow.nextSibling) {
	                        // This loop looks for the first non-textNode element
	                    }
	                    if(eRow) {
		                    hm.util.selectRow(eRow, false);
	                    }
					}
				}
			}
		}
	}
}
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="radiusSelectPage" id="radiusSelectPage">
<s:hidden name="operation" />
<s:hidden name="selectRadiusServerId" />
<s:hidden name="selectStaticApId" />
<s:hidden name="ssidForRs" value="%{ssidForRs}" />
<s:hidden name="radiusTypeFlag" value="%{radiusTypeFlag}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><div id="errNote" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
				    <s:if test="%{enabledCloudBased}">
				    <tr>
				        <td style="padding-top: 10px;"><input type="radio" name="enabledCloud" id="radioCloud" value="1"><label for="radioCloud">Cloud-Based</label></td>
				    </tr>
				    <tr>
				        <td style="padding: 5px 0 10px 0;"><input type="radio" name="enabledCloud" id="radioServer" value="0"><label for="radioServer">Radius Server</label></td>
				    </tr>
					<tr id="staticApRow" style="display: none;">
						<td>
							<ah:checkList name="selectStaticAp" list="availableStaticAps" listKey="id" listValue="value"
							 value="selectStaticApId" width="100%" containerId="staticApContainerId"/>
						</td>
					</tr>
				    </s:if>
					<tr id="radiusServerRow">
						<td>
							<ah:checkList name="selectRadiusServer" list="availableRadiusServer" listKey="id" listValue="value"
							 value="selectRadiusServerId" editEvent="editRadiusServer" width="100%" containerId="radiusServerContainerId"/>
						</td>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<s:if test="%{writeDisabled == 'disabled'}">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
										<td width="40px">&nbsp;</td>
										<td class="npcButton" id="newBtn"><a href="javascript:void(0);" class="btCurrent" title="<s:text name="config.v2.select.network.policy.button.new"/>"><span><s:text name="config.v2.select.network.policy.button.new"/></span></a></td>
									</tr>
								</s:if>
								<s:else>
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="finishSelectRS();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
										<td width="40px">&nbsp;</td>
										<td class="npcButton" id="newBtn"><a href="javascript:void(0);" class="btCurrent" onclick="newRadiusServer();" title="<s:text name="config.v2.select.network.policy.button.new"/>"><span><s:text name="config.v2.select.network.policy.button.new"/></span></a></td>
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
