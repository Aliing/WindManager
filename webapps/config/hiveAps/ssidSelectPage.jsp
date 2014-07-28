<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'ssidSelectPage';
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

function editSsidDialog(ssidId, event) {
         // close this dialog
         hideSubDialogOverlay();
         // expand the subdrawer
         editSsid(ssidId);
         
         // stop bubble!!!!
         hm.util.stopBubble(event);

         // init the callback function
		// this function will be called when back to the NetworkPolicy drawer from the sub drawer,
		// and destroy after invoked.
         networkPolicyCallbackFn = function() {
        	 var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveSsidPage"
			 	+ "&ignore="+new Date().getTime();
			addRemoveSsid(url);
         }
}

function cloneSsidDialog(ssidId, event) {
         // close this dialog
         hideSubDialogOverlay();
         // expand the subdrawer
         cloneSsid(ssidId);
         
         // stop bubble!!!!
         hm.util.stopBubble(event);

         // init the callback function
		// this function will be called when back to the NetworkPolicy drawer from the sub drawer,
		// and destroy after invoked.
         networkPolicyCallbackFn = function() {
        	 var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveSsidPage"
			 	+ "&ignore="+new Date().getTime();
			addRemoveSsid(url);
         }
}

function checkSelectedSsidList() {
    var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms["ssidSelectPage"].operation.value = "checkSsidsUsed";
    YAHOO.util.Connect.setForm(document.getElementById("ssidSelectPage"));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
            {success : succCheckSsidList, failure : resultDoNothing, timeout: 60000}, null);    
}
var succCheckSsidList = function(o) {
    eval("var details = " + o.responseText);
    if(details.err) {
        var cancelBtn = function(){
            if(null != subDialogOverlay){
                subDialogOverlay.cfg.setProperty('visible', true);
            }
            this.hide();
        };
        var mybuttons = [ { text:"OK", handler: function(){this.hide();finishSelectSsid();} }, 
                          { text:"Cancel", handler: cancelBtn, isDefault:true} ];
        var ssidWarningMsg = "<html><body>"+"<s:text name='config.networkpolicy.ssid.list.warning'/>" + details.err +"</body></html>";
        var dlg = userDefinedConfirmDialog(ssidWarningMsg, mybuttons, "Warning");
        if(null != subDialogOverlay){
            subDialogOverlay.cfg.setProperty('visible', false);
        }
        dlg.show();
    } else {
        finishSelectSsid();
    }
}

YAHOO.util.Event.onContentReady("selectSsidIdsDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="ssidSelectPage" id="ssidSelectPage">
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
							<ah:checkList name="selectSsidIds" multiple="true" width="100%" itemWidth="175px" list="ssidProfilesList" listKey="id" listValue="value" value="selectSsidIds" editEvent="editSsidDialog" cloneEvent="cloneSsidDialog"/>
						</td>
					</tr>
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="checkSelectedSsidList();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
									<td width="40px">&nbsp;</td>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="newSsid();" title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
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
