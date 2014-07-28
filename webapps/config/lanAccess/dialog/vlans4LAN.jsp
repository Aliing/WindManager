<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
YAHOO.util.Event.onContentReady("untaggedVlanIdsDiv", adjustEditLink, this);
</script>

<div style="padding: 0; height: 260px;"><s:form action="lanProfiles" name="vlanSelectPage" id="vlanSelectPage">
<s:hidden name="selectedLANId" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr id="fe_ErrorVlanRow" style="display: none;">
            <td class="noteError" id="textfe_ErrorVlanRow" colspan="4" style="padding-left: 20px;">To be changed</td>
        </tr>
        <tr>
            <td style="padding: 5px 20px;"><s:text name="config.lanProfile.vlan.note" /></td>
        </tr>
        <tr>
            <td>
            <div id="listContainer">
                <div id="untaggedTextId_div" class="plainTab activeTab"><span class="npcHead2 activeTab" id="untaggedTextId">Untagged/Native</span></div>
                <div id="taggedTextId_div" class="plainTab" style="margin-top: 50px;"><span class="npcHead2 inactiveTab" id="taggedTextId">&nbsp;&nbsp;&nbsp;Tagged</span></div>
                <div class="tabSection" id="untaggedSectionId">
                <ah:checkList name="untaggedVlanIds" height="165px" width="100%"
                    editEvent="editVlan4LAN" cloneEvent="cloneVlan4LAN"
                    list="vlanList" listKey="id" listValue="value" value="untaggedVlanIds" containerId="untaggedVlanContainerId"/>
                </div>
                <div class="tabSection" style="display: none;" id="taggedSectionId">
                <ah:checkList name="taggedVlanIds" multiple="true" height="165px" width="100%"
                    editEvent="editVlan4LAN" cloneEvent="cloneVlan4LAN"
                    list="vlanList" listKey="id" listValue="value" value="taggedVlanIds" containerId="taggedVlanContainerId"/>
                </div>
                <div class="npcButton" style="position: absolute;left: 190px;top:330px;">
                    <s:if test="%{writeDisabled==''}">
                        <a href="javascript: void(0);" style="margin-right:20px;" onclick="newVlan4LAN();return false;" 
                        class="btCurrent" title="<s:text name="common.button.create.new"/>"><span style="text-align:center;"><s:text name="common.button.create.new"/></span></a>
                       <a href="javascript: void(0);" class="btCurrent"
                               style="margin-left:20px;" 
                               onclick="selectVlans(); return false;" 
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
        YUE.onContentReady("taggedVlanIdsDiv", adjustEditLink, this);
    }
}
// -------- re-bind the Networks to LAN --------
function selectVlans() {
    var url = "<s:url action='lanProfiles' includeParams='none' />?operation=selectVlans4LAN"
        + "&ignore="+new Date().getTime();
    YAHOO.util.Connect.setForm(document.getElementById("vlanSelectPage"));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
            {success: finishSelectVlans, failure: failureSelectVlans, timeout: 60000}, null);
}
var finishSelectVlans = function(o) {
    eval("var details = " + o.responseText);
    if(details.succ) {
        hideSelectNetworkDialog()
        fetchConfigTemplate2Page(true);
    } else {
        var errorRow = new Object();
        errorRow.id='ErrorVlanRow';
        hm.util.reportFieldError(errorRow, details.errorMsg);
    }
}
var failureSelectVlans = function(o) {}
// ---------- show the Network in draw ----------
function newVlan4LAN() {
    subDrawerCloneOperation = "";
    var url = "<s:url action='vlan' includeParams='none' />?operation=new&jsonMode=true"
            +"&selectedLANId=" + YUD.get("vlanSelectPage_selectedLANId").value + "&ignore=" + new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('get', url, 
            {success : newVlanProfile, timeout: 60000}, null);

    hideSelectNetworkDialog();
}
var newVlanProfile = function(o) {
	   subDrawerOperation= "createVLAN";
	    // set the sub drawer title
	    if(subDrawerCloneOperation == "cloneVLAN") {
	        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone VLAN"));
	    } else {
	        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New VLAN"));
	    }
	    accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	    // get the sub drawer content
	    var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	    set_innerHTML(subDrawerContentId, o.responseText);
	    notesTimeoutId = setTimeout("hideNotes()", 4000);
}
function editVlan4LAN(vlanId, event) {
    // close this dialog
    hideSelectNetworkDialog();
    // expand the subdrawer
    editVlan(vlanId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    networkPolicyCallbackFn = function() {
    	selectVlans4Lan(YUD.get("vlanSelectPage_selectedLANId").value);
    }
}
function cloneVlan4LAN(vlanId, event) {
    // close this dialog
    hideSelectNetworkDialog();
    // expand the subdrawer
    cloneVlan(vlanId);
    // stop bubble!!!!
    hm.util.stopBubble(event);
    networkPolicyCallbackFn = function() {
    	selectVlans4Lan(YUD.get("vlanSelectPage_selectedLANId").value);
    }
}
function cloneVlan(vlanId) {
    var vlanArray = new Array();
    vlanArray.push(vlanId);
    subDrawerCloneOperation="cloneVLAN";
    var url = "<s:url action='vlan' includeParams='none' />?operation=clone&selectedIds=" + vlanArray 
            + "&selectedLANId=" + YUD.get("vlanSelectPage_selectedLANId").value
            + "&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
            {success : newVlanProfile, failure : resultDoNothing, timeout: 60000}, null);  
}
</script>