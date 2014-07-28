<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
//--- Check Select ---
function checkSelectedPort() {
    var itemValue = hm.util.getSelectedCheckItems("selectedPortTempalteIds");
    if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
        Get("errNote").innerHTML="There is no item.";
        return;
    }
    var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms['listPortTemplates'].operation.value = "checkPortTemplates";
    YAHOO.util.Connect.setForm(document.getElementById('listPortTemplates'));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
            {success : succCheckPorts, failure : resultDoNothing, timeout: 60000}, null);    
}
var succCheckPorts = function(o) {
    eval("var details = " + o.responseText);
    if(details.err) {
        var cancelBtn = function(){
            if(null != subDialogOverlay){
                subDialogOverlay.cfg.setProperty('visible', true);
            }
            this.hide();
        };
        var mybuttons = [ { text:"OK", handler: function(){this.hide();selectedPort();} }, 
                          { text:"Cancel", handler: cancelBtn, isDefault:true} ];
        var lanWarningMsg = "<html><body>"+"<s:text name='config.lanProfile.list.warning'/>" + details.err +"</body></html>";
        var dlg = userDefinedConfirmDialog(lanWarningMsg, mybuttons, "Warning");
        if(null != subDialogOverlay){
            subDialogOverlay.cfg.setProperty('visible', false);
        }
        dlg.show();
    } else {
        selectedPort();
    }
}

//--- Edit Event ---
function editPortTemplateInList(portId, event) {
	var networkPolicyId
	if(Get('listPortTemplates_networkPolicyID')){
		networkPolicyId = Get('listPortTemplates_networkPolicyID').value;
	}
	var selectTmpIdStr
	if(Get('listPortTemplates_selectTmpIdStr')){
		selectTmpIdStr = Get('listPortTemplates_selectTmpIdStr').value;
	}
    // expand the subdrawer
    viewPort(portId,null,selectTmpIdStr,networkPolicyId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    networkPolicyCallbackFn = null;
}

//--- Clone Event ---
function clonePortTemplateInList(portId, event) {
    // expand the subdrawer
    clonePort(portId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
    //this function will be called when back to the NetworkPolicy drawer from the sub drawer,
    //and destroy after invoked.
    networkPolicyCallbackFn = null;
}
//--- Clone Event ---
function rmPortTemplateInList(portId, event) {
    doNetworkPolicyContinueOper = function() {
        removePort(portId);
        hm.util.stopBubble(event);
        networkPolicyCallbackFn = null;
    };
    
    hm.util.confirmRemoveItems();
}
//listPortTemplates
function haveNonDefaultTemplate(portId, event){
	 checkIsNonDefault(portId);
}
YAHOO.util.Event.onContentReady("selectedPortTempalteIdsDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="listPortTemplates" id="listPortTemplates">
<s:hidden name="operation" />
<s:hidden name="networkPolicyID" />
<s:hidden name="selectTmpIdStr" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><span style="font-size:15px;"><s:text name="config.portTemplate.list.help.info" /></span></td>
        </tr>
        <tr>
            <td><div id="errNote" class="noteError"></div></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%" >
                    <tr>
                        <td >
                            <ah:checkList name="selectedPortTempalteIds" width="100%"
                                editEvent="editPortTemplateInList"
                                cloneEvent="clonePortTemplateInList"
                                removeEvent="rmPortTemplateInList"
                                multiple="true"
                                itemWidth="430px"
                                height="160px"
                                grayOutSimilar="true"
                                clickEvent="haveNonDefaultTemplate"
                                menuContainerStyle="width:55px;"
                                list="portTemplateList" listKey="id" listValue="value" value="selectedPortTempalteIds"/>
                        </td>
                    </tr>
                    <tr>
                        <td height="15px"/>
                    </tr>
                    <tr>
                        <td align="center" width="100%">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="checkSelectedPort();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
                                    <td width="40px">&nbsp;</td>
                                    <td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="clickNewPort(<s:property value='%{wirelessMode}'/>);" title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
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