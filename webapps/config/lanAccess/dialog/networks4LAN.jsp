<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
function onLoadPage() {
}
function submitAction(operation) {
	if (validate(operation)) {
	}
}
function validate(operation) {
	return true;
}
YAHOO.util.Event.onContentReady("untaggedNetworkIdDiv", adjustEditLink, this);
</script>

<div style="padding: 0; height: 260px;"><s:form action="lanProfiles" name="networksSelectPage" id="networksSelectPage">
<s:hidden name="selectedLANId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr id="fe_ErrorNetworkRow" style="display: none;">
			<td class="noteError" id="textfe_ErrorNetworkRow" colspan="4" style="padding-left: 20px;">To be changed</td>
		</tr>
		<tr>
            <td style="padding: 5px 20px;"><s:text name="config.lanProfile.network.note" /></td>
        </tr>
		<tr>
			<td>
			<div id="listContainer">
				<div id="untaggedTextId_div" class="plainTab activeTab"><span class="npcHead2 activeTab" id="untaggedTextId">Untagged/Native</span></div>
				<div id="taggedTextId_div" class="plainTab" style="margin-top: 50px;"><span class="npcHead2 inactiveTab" id="taggedTextId">&nbsp;&nbsp;&nbsp;Tagged</span></div>
				<div class="tabSection" id="untaggedSectionId">
				<ah:checkList name="untaggedNetworkId" height="165px" width="100%"
				    editEvent="editNetwork4LAN" cloneEvent="cloneNetwork4LAN"
					list="networkList" listKey="id" listValue="value" value="untaggedNetworkId" containerId="untaggedNetworkContainerId"/>
				</div>
				<div class="tabSection" style="display: none;" id="taggedSectionId">
				<ah:checkList name="taggedNetworkIds" multiple="true" height="165px" width="100%"
				    editEvent="editNetwork4LAN" cloneEvent="cloneNetwork4LAN"
					list="networkList" listKey="id" listValue="value" value="taggedNetworkIds" containerId="taggedNetworkContainerId"/>
				</div>
				<div class="npcButton" style="position: absolute;left: 190px;top:330px;">
					<s:if test="%{writeDisabled==''}">
						<a href="javascript: void(0);" style="margin-right:20px;" onclick="newNetwork4LAN();return false;" 
						class="btCurrent" title="<s:text name="common.button.create.new"/>"><span style="text-align:center;"><s:text name="common.button.create.new"/></span></a>
                       <a href="javascript: void(0);" class="btCurrent"
                               style="margin-left:20px;" 
                               onclick="selectNetworks(); return false;" 
                               title="<s:text name="common.button.ok"/>"><span style="text-align:center;"><s:text name="common.button.ok"/></span></a>
					</s:if>
				</div>
			</div>
			</td>
		</tr>
	</table>
	</s:form>
</div>
<script type="text/javascript">
var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event;
//YUE.onDOMReady(onloadEvent);
window.setTimeout("onloadEvent()", 100);
function onloadEvent() {
	YUE.on(["untaggedTextId_div", "taggedTextId_div"], "click", changeNetworksLayout);
	<s:if test="%{noCorrelationNetworks}">
	changeTabSelectedStyle("taggedTextId_div");
	</s:if>
}
//-------- change the dialog style --------
var changeNetworksLayout = function() {
	changeTabSelectedStyle(this.id);
}
function changeTabSelectedStyle(elId) {
   if("untaggedTextId_div" == elId) {
        if(!YUD.hasClass(YUD.get("untaggedTextId_div"), "activeTab")) {
            YUD.addClass(YUD.get("untaggedTextId_div"), "activeTab");
        }
        YUD.removeClass(YUD.get("taggedTextId_div"), "activeTab");
        if(!YUD.hasClass(YUD.get("untaggedTextId"), "activeTab")) {
            YUD.replaceClass(YUD.get("untaggedTextId"), "inactiveTab", "activeTab");
            YUD.setStyle(YUD.get("untaggedSectionId"), "display", "");
            YUD.setStyle(YUD.get("taggedSectionId"), "display", "none");
            YUD.replaceClass(YUD.get("taggedTextId"), "activeTab", "inactiveTab");
        }
    } else {
        if(!YUD.hasClass(YUD.get("taggedTextId_div"), "activeTab")) {
            YUD.addClass(YUD.get("taggedTextId_div"), "activeTab");
        }
        YUD.removeClass(YUD.get("untaggedTextId_div"), "activeTab");
        if(!YUD.hasClass(YUD.get("taggedTextId"), "activeTab")) {
            YUD.replaceClass(YUD.get("taggedTextId"), "inactiveTab", "activeTab");
            YUD.setStyle(YUD.get("taggedSectionId"), "display", "");
            YUD.setStyle(YUD.get("untaggedSectionId"), "display", "none");
            YUD.replaceClass(YUD.get("untaggedTextId"), "activeTab", "inactiveTab");
        }
        YUE.onContentReady("taggedNetworkIdsDiv", adjustEditLink, this);
    }
}
// -------- re-bind the Networks to LAN --------
function selectNetworks() {
	var url = "<s:url action='lanProfiles' includeParams='none' />?operation=selectNetworks4LAN"
		+ "&ignore="+new Date().getTime();
	YAHOO.util.Connect.setForm(document.getElementById("networksSelectPage"));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
			{success: finishSelectNetworks, failure: failureSelectNeworks, timeout: 60000}, null);
}
var finishSelectNetworks = function(o) {
	eval("var details = " + o.responseText);
	if(details.succ) {
		hideSelectNetworkDialog()
		fetchConfigTemplate2Page(true);
	} else {
		var errorRow = new Object();
		errorRow.id='ErrorNetworkRow';
		hm.util.reportFieldError(errorRow, details.errorMsg);
	}
}
var failureSelectNeworks = function(o) {}
// ---------- show the Network in draw ----------
function newNetwork4LAN() {
	subDrawerCloneOperation = "";
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=new&jsonMode=true"
			+"&selectedLANId=" + YUD.get("networksSelectPage_selectedLANId").value + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, 
			{success : newVPNNetwork, timeout: 60000}, null);
	
	hideSelectNetworkDialog();
}
var newVPNNetwork = function(o) {
   subDrawerOperation= "createVpnNetwork";
    
    // set the sub drawer title
    if(subDrawerCloneOperation == "cloneVpnNetwork") {
	    accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone Network"));
    } else {
        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New Network"));
    }
    accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	// get the sub drawer content
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 4000);
}
//-----------Edit the Networks
function editNetwork4LAN(networkdId, event) {
    // close this dialog
    hideSelectNetworkDialog();
    // expand the subdrawer
    editVpnNetwork(networkdId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
    //this function will be called when back to the NetworkPolicy drawer from the sub drawer,
    //and destroy after invoked.
    networkPolicyCallbackFn = function() {
    	selectNetworks4Lan(YUD.get("networksSelectPage_selectedLANId").value);
    }
}
function cloneNetwork4LAN(networkdId, event) {
    // close this dialog
    hideSelectNetworkDialog();
    // expand the subdrawer
    cloneVpnNetwork(networkdId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    // init the callback function
    //this function will be called when back to the NetworkPolicy drawer from the sub drawer,
    //and destroy after invoked.
    networkPolicyCallbackFn = function() {
    	selectNetworks4Lan(YUD.get("networksSelectPage_selectedLANId").value);
    }
}
function cloneVpnNetwork(networkId) {
    var networkArray = new Array();
    networkArray.push(networkId);
    subDrawerCloneOperation="cloneVpnNetwork";
    var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=clone&selectedIds=" + networkArray 
    		+ "&selectedLANId=" + YUD.get("networksSelectPage_selectedLANId").value
            + "&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
            {success : newVPNNetwork, failure : resultDoNothing, timeout: 60000}, null);  
}
</script>