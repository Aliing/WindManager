<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'bonjourGwList';
// --------- select -----------//
function selectedBonjourGw() {
    var itemValue = hm.util.getSelectedCheckItems("selectedBonjourGwIds");
    var idPara = "";
    if (hm.util._LIST_SELECTION_NOITEM != itemValue && hm.util._LIST_SELECTION_NOSELECTION != itemValue) {
        idPara = "&selectBonjourGwId="+itemValue[0];
    }
    var url = "<s:url action='networkPolicy' includeParams='none' />?operation=finishSelectBonjourGw"
    		+ idPara +"&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('get', url, 
            {success : finishSelectedBonjourGw, failure : resultDoNothing, timeout: 60000}, null);  
}
// ---- New --------//
function clickNewBonjourGw() {
	hideSubDialogOverlay();
    var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=new&jsonMode=true&ignore="
        +new Date().getTime();
    openIFrameDialog(800, 450, url);
}
//--- Edit ---//
function editBonjourGwList(bonjourGwId, event) {
    var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=edit&jsonMode=true&id="
    		+ bonjourGwId + "&ignore="+new Date().getTime();
    parent.openIFrameDialog(800, 450, url);
    
    // stop bubble!!!!
    hm.util.stopBubble(event);    
}
//--- Clone ---//
function cloneBonjourGwList(id, event) {
    var bonjourIds = new Array();
    bonjourIds.push(id);
    var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=clone&jsonMode=true&selectedIds="
    		+ bonjourIds + "&ignore="+new Date().getTime();
    parent.openIFrameDialog(800, 450, url);
    
    // stop bubble!!!!
    hm.util.stopBubble(event);    
}

YAHOO.util.Event.onContentReady("selectedBonjourGwIdsDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="bonjourGwList" id="bonjourGwList">
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
                            <ah:checkList name="selectedBonjourGwIds" width="100%"
                                editEvent="editBonjourGwList"
                                cloneEvent="cloneBonjourGwList" 
                                height="150px"
                                list="availableBonjourGw" listKey="id" listValue="value" value="selectBonjourGwId"/>
                        </td>
                    </tr>
                    <tr>
                        <td height="15px"/>
                    </tr>
                    <tr>
                        <td align="center" width="100%">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" 
                                    <s:if test="%{writeDisabled != 'disabled'}">
                                    onclick="selectedBonjourGw();" 
                                    </s:if>
                                    title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
                                    <td width="40px">&nbsp;</td>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" 
                                    <s:if test="%{writeDisabled != 'disabled'}">
                                    onclick="clickNewBonjourGw();"
                                    </s:if> 
                                    title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
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