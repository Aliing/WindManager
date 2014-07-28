<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
//--- Edit Event ---
function editPortAccessInList(portId, event) {
    viewPortAccess(portId);
    hm.util.stopBubble(event);
}

//--- Clone Event ---
function clonePortAccessInList(portId, event) {
	clickNewPortAccess(true, portId);
    hm.util.stopBubble(event);
}
//--- Clone Event ---
function rmPortAccessInList(portId, event) {
    doNetworkPolicyContinueOper = function() {
    	removePortAccess(portId);
        hm.util.stopBubble(event);
        networkPolicyCallbackFn = null;
    };
    
    hm.util.confirmRemoveItems();
}

YAHOO.util.Event.onContentReady("selectedAccessIdsDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="portConfigure" name="listPortAccess" id="listPortAccess">
<s:hidden name="operation" />
<s:hidden name="portTemplateId" />
<s:hidden name="portNum" />
<s:hidden name="deviceType" />
<s:hidden name="tmpIndex" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><div id="errNote" class="noteError"></div></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td>
                            <ah:checkList name="selectedAccessIds" width="100%"
                                editEvent="editPortAccessInList"
                                cloneEvent="clonePortAccessInList"
                                removeEvent="rmPortAccessInList"
                                itemWidth="175px"
                                height="150px"
                                menuContainerStyle="width:55px;"
                                list="portAccessList" listKey="id" listValue="value" value="selectedAccessIds"/>
                        </td>
                    </tr>
                    <tr>
                        <td height="15px"/>
                    </tr>
                    <tr>
                        <td align="center" width="100%">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="selectedAccess();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
                                    <td width="40px">&nbsp;</td>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="clickNewPortAccess();" title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
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