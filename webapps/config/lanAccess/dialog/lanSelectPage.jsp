<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'lanSelectPage';
function onLoadPage() {

}
function submitEditAction(operation,ssidId){
	document.forms[formName].ssidId.value = ssidId;
	submitAction(operation);
}
function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
	    document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	return true;
}

//--- Check Select ---
function checkSelectedLAN() {
    var itemValue = hm.util.getSelectedCheckItems("selectedLanIds");
    if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
        Get("errNote").innerHTML="There is no item.";
        return;
    }
    var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms["lanSelectPage"].operation.value = "checkLANs";
    YAHOO.util.Connect.setForm(document.getElementById("lanSelectPage"));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
            {success : succCheckLANs, failure : resultDoNothing, timeout: 60000}, null);    
}
var succCheckLANs = function(o) {
    eval("var details = " + o.responseText);
    if(details.err) {
        var cancelBtn = function(){
            if(null != subDialogOverlay){
                subDialogOverlay.cfg.setProperty('visible', true);
            }
            this.hide();
        };
        var mybuttons = [ { text:"OK", handler: function(){this.hide();selectedLAN();} }, 
                          { text:"Cancel", handler: cancelBtn, isDefault:true} ];
        var lanWarningMsg = "<html><body>"+"<s:text name='config.lanProfile.list.warning'/>" + details.err +"</body></html>";
        var dlg = userDefinedConfirmDialog(lanWarningMsg, mybuttons, "Warning");
        if(null != subDialogOverlay){
            subDialogOverlay.cfg.setProperty('visible', false);
        }
        dlg.show();
    } else {
        selectedLAN();
    }
}

//--- Edit Event ---
function editLANInList(lanId, event) {
    // close this dialog
    hideSubDialogOverlay();
    // expand the subdrawer
    viewLAN(lanId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
	//this function will be called when back to the NetworkPolicy drawer from the sub drawer,
	//and destroy after invoked.
    networkPolicyCallbackFn = function() {
   		addRemoveLan();
    }
}

//--- Clone Event ---
function cloneLANInList(lanId, event) {
    // close this dialog
    hideSubDialogOverlay();
    // expand the subdrawer
    cloneLAN(lanId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
    //this function will be called when back to the NetworkPolicy drawer from the sub drawer,
    //and destroy after invoked.
    networkPolicyCallbackFn = function() {
        addRemoveLan();
    }
}

YAHOO.util.Event.onContentReady("selectedLanIdsDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="lanSelectPage" id="lanSelectPage">
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
							<ah:checkList name="selectedLanIds" width="100%"
								editEvent="editLANInList"
								cloneEvent="cloneLANInList" 
								multiple="true"
								itemWidth="175px"
								list="lanProfilesList" listKey="id" listValue="value" value="selectedLanIds"/>
						</td>
					</tr>
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="checkSelectedLAN();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
									<td width="40px">&nbsp;</td>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="clickNewLAN(<s:property value='%{wirelessMode}'/>);" title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
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
