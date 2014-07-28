<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.UserProfile"%>

<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0;
}
<s:if test="%{jsonMode == true}">
body {
	background-color: transparent;
}
</s:if>
-->
</style>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'userProfiles2Form';
var thisOperation;
function showQosSettingsContent(){
	showHideContent("qosSettings","");
}

function showFirewallsContent(){
	showHideContent("firewalls","");
}

function showGreVpnTunnelsContent(){
	showHideContent("greVpnTunnels","");
}

function showSlaContent(){
	showHideContent("sla","");
}

function showAdvContent(){
	showHideContent("adv","");
}

function showCliClaContent(){
	showHideContent("cliCla","");
}

function tunnelSettingChanged(radioBox){
	if(radioBox.value == <%=UserProfile.TUNNEL_NO%>){
		Get("greTunnelSetting").style.display = "none";
		//Get("vpnTunnelSetting").style.display = "none";
	}else if(radioBox.value == <%=UserProfile.TUNNEL_GRE%>){
		Get("greTunnelSetting").style.display = "";
		//Get("vpnTunnelSetting").style.display = "none";
	}else if(radioBox.value == <%=UserProfile.TUNNEL_VPN%>){
		Get("greTunnelSetting").style.display = "none";
		//Get("vpnTunnelSetting").style.display = "";
	}
}

function changeMarkerMap(value){
	var checked = false;
	if(value == -1){
		checked = false;
	} else {
		checked = true;
	}
	$('input[name="dataSource.qosMarkTypeMode"]').attr("disabled",!checked);
}

function showSlaConfig(slaChecked){
	var slaBandwidth = Get(formName + "_dataSource_slaBandwidth");
	var slaAction = Get(formName + "_dataSource_slaAction");
	if(slaChecked){
		slaBandwidth.disabled = false;
		slaAction.disabled = false;
	}else{
		slaBandwidth.disabled = true;
		slaAction.disabled = true;
	}
}

function showReassignPanel(assignChecked) {
	Get("reassignPanel").style.display = assignChecked ? "" : "none";
}

function checkRemoveActionOperation(operation){
    if(operation == 'removeTunnel') {
    	var value = hm.util.validateListSelection("tunnel");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].tunnel.value = value;
		}
    }
    if(operation == 'removeMacPolicyF') {
    	var value = hm.util.validateListSelection("macPolicyFrom");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].macPolicyFrom.value = value;
		}
    }
    if(operation == 'removeMacPolicyT') {
    	var value = hm.util.validateListSelection("macPolicyTo");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].macPolicyTo.value = value;
		}
    }
    if(operation == 'removeIpPolicyF') {
    	var value = hm.util.validateListSelection("ipPolicyFrom");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].ipPolicyFrom.value = value;
		}
    }
    if(operation == 'removeIpPolicyT') {
    	var value = hm.util.validateListSelection("ipPolicyTo");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].ipPolicyTo.value = value;
		}
    }
    return true;
}

function submitRemoveAction(operation) {
	if (checkRemoveActionOperation(operation)){
		thisOperation = operation;
	    if (operation == 'removeMacPolicyT') {
	    	hm.util.confirmRemoveItems();
	    } else if (operation == 'removeMacPolicyF') {
	    	hm.util.confirmRemoveItems();
	    } else if (operation == 'removeIpPolicyT') {
	    	hm.util.confirmRemoveItems();
	    } else if (operation == 'removeIpPolicyF') {
	    	hm.util.confirmRemoveItems();
	    } else if (operation == 'removeTunnel') {
	    	hm.util.confirmRemoveItems();
	    } else {
	    	doContinueOper();
	    }
    }
}
function doContinueOper() {
     submitAction(thisOperation)
}

var vlanSelectId = 'myVlanSelect';
var macPolicyFId = 'macPolicyFrom,macPolicyTo';
var macPolicyTId = 'macPolicyTo,macPolicyFrom';
var ipPolicyTId = 'ipPolicyTo,ipPolicyFrom';
var ipPolicyFId = 'ipPolicyFrom,ipPolicyTo';
var tunnelSelectId = 'tunnel';
var qosSelectId = 'qos';
var markerMapSelectId = "markerMapId";
var macOurSelectId = formName+'_macObjId';
var domainSelectId = formName+'_domObjId';
var userAttrSelectId = 'myAttriSelect';
var osObjectSelectId = formName+'_osObjId';
var schedulerSelectId = 'leftOptions_schedulers';
var qosUIElement;
function submitAction(operation) {

	if (validate(operation)) {
		prepareBeforeSubmitUserProfile(operation);
		<s:if test="%{jsonMode == false}">
			if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
			if ("update" + '<s:property value="lstForward"/>'!=operation &&
			 	operation != 'cancel' + '<s:property value="lstForward"/>' &&
			 	operation !='removeMacPolicyF' &&
			 	operation !='removeMacPolicyT' &&
			 	operation !='removeIpPolicyT' &&
			 	operation !='removeIpPolicyF' &&
			 	operation !='removeTunnel') {
				document.forms[formName].target = "_parent";
			}
		    document.forms[formName].submit();
	    </s:if>
		<s:else>
	    	//content opened in subdrawer
	    	<s:if test="%{contentShownInDlg == false}">
	    		if(operation == 'newUserProfileSchedule'){
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&parentDomID=" + schedulerSelectId
				 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 450, url);
				}else if(operation == 'editUserProfileSchedule'){
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&scheduler="+document.forms[formName].scheduler.value
							+ "&parentDomID=" + schedulerSelectId
				 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(800,450, url);
				} else if (operation == 'newVlanId') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&parentDomID="+vlanSelectId
				 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 400, url);
				} else if (operation == 'editVlanId') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&vlanId="+document.forms[formName].vlanId.value
							+ "&parentDomID="+vlanSelectId
		 					+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 400, url);
				} else if (operation == 'newMacPolicyT') {
					document.forms[formName].macPolicyType.value='macTo';
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+macPolicyTId
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 500, url);
				} else if (operation == 'editMacPolicyT') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+macPolicyTId
					+ "&macPolicyTo="+document.forms[formName].macPolicyTo.value
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 500, url);
				} else if (operation == 'newMacPolicyF') {
					document.forms[formName].macPolicyType.value='macFrom';
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+macPolicyFId
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 500, url);
				} else if (operation == 'editMacPolicyF') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+macPolicyFId
					+ "&macPolicyFrom="+document.forms[formName].macPolicyFrom.value
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 500, url);
				}else if (operation == 'newIpPolicyT') {
					document.forms[formName].ipPolicyType.value='ipTo';
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+ipPolicyTId
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(1010, 650, url);
				} else if (operation == 'editIpPolicyT') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+ipPolicyTId
					+ "&ipPolicyTo="+document.forms[formName].ipPolicyTo.value
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(1010, 650, url);
				} else if (operation == 'newIpPolicyF') {
					document.forms[formName].ipPolicyType.value='ipFrom';
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+ipPolicyFId
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(1010, 650, url);
				} else if (operation == 'editIpPolicyF') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID="+ipPolicyFId
					+ "&ipPolicyFrom="+document.forms[formName].ipPolicyFrom.value
					+ "&ignore="+new Date().getTime();
					openIFrameDialog(1010, 650, url);
				} else if (operation == 'newTunnel') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+tunnelSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 600, url);
				} else if (operation == 'editTunnel') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+tunnelSelectId
						+ "&tunnel="+document.forms[formName].tunnel.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 600, url);
				} else if (operation == 'newQos') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+qosSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(870, 600, url);
				} else if (operation == 'editQos') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+qosSelectId
						+ "&qos="+document.forms[formName].qos.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(870, 600, url);
				} else if (operation == 'newMarkerMap') {
					qosUIElement = $('input[name="dataSource.qosMarkTypeMode"]');
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+markerMapSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(870, 520, url);
				} else if (operation == 'editMarkerMap') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+markerMapSelectId
						+ "&markerMapId="+document.forms[formName].markerMapId.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(870, 520, url);
				} else if (operation == 'addPolicyRules'
						|| operation == 'removePolicyRulesNone'
						|| operation == 'removePolicyRules') {
					submitAsynActionSubDrawerUserprofile(operation);
				} else if (operation == 'newMac') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+macOurSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 450, url);
				} else if (operation == 'editMac') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+qosSelectId
						+ "&editObjId="+document.forms[formName].macObjId.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 450, url);
				} else if (operation == 'newDomain') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+domainSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(680, 400, url);
				} else if (operation == 'editDomain') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+domainSelectId
						+ "&editObjId="+document.forms[formName].domObjId.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(680, 400, url);
				} else if (operation == 'newUserAttribute') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+userAttrSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(900, 400, url);
				} else if (operation == 'editUserAttribute') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+userAttrSelectId
						+ "&userAttribute="+document.forms[formName].userAttribute.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(900, 400, url);
				} else if (operation == 'newOs') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+osObjectSelectId
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(710, 560, url);
				} else if (operation == 'editOs') {
					var url = "<s:url action='userProfiles' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+osObjectSelectId
						+ "&editObjId="+document.forms[formName].osObjId.value
						+ "&ignore="+new Date().getTime();
					openIFrameDialog(710, 560, url);
				}
	    	</s:if>
	    	//content opened in iframe dialog
	    	<s:else>
	    		submitJsonUserProfileAction(operation);
	    	</s:else>
		</s:else>
	}

}

function prepareBeforeSubmitUserProfile(operation) {
	hm.options.selectAllOptions('schedulers');
	if(operation=='newMacPolicyT')
	   document.forms[formName].macPolicyType.value='macTo';
	if(operation=='newMacPolicyF')
	   document.forms[formName].macPolicyType.value='macFrom';
	if(operation=='newIpPolicyT')
	   document.forms[formName].ipPolicyType.value='ipTo';
	if(operation=='newIpPolicyF')
	   document.forms[formName].ipPolicyType.value='ipFrom';
	//save style values
	Get(formName + "_dataSource_firewallDisplayStyle").value = Get("firewalls").style.display;
	Get(formName + "_dataSource_qosSettingDisplayStyle").value = Get("qosSettings").style.display;
	Get(formName + "_dataSource_greVpnTunnelDisplayStyle").value = Get("greVpnTunnels").style.display;
	Get(formName + "_dataSource_scheduleDisplayStyle").value = Get("schedules").style.display;
	Get(formName + "_dataSource_slaDisplayStyle").value = Get("sla").style.display;
	<s:if test="%{!easyMode}">
	Get(formName + "_dataSource_advSettingDisplayStyle").value = Get("adv").style.display;
		<s:if test="%{showUpReassign}">
			Get(formName + "_dataSource_clientClassDisplayStyle").value = Get("cliCla").style.display;
		</s:if>
	</s:if>
}

function submitAsynActionSubDrawerUserprofile(operation) {
	var url = "<s:url action='userProfiles' includeParams='none' />"
		+"?jsonMode=true&ignore="+new Date().getTime();
	prepareBeforeSubmitUserProfile(operation);
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.forms[formName]);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succAsynSubDrawerUserprofile, failure : resultDoNothing, timeout: 60000}, null);
}

var succAsynSubDrawerUserprofile = function(o) {
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 4000);
}

function submitJsonUserProfileAction(operation) {
	if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
		showProcessing();
	}
	prepareBeforeSubmitUserProfile(operation);
	document.forms[formName].operation.value = operation;

	document.forms[formName].jsonMode.value = true;
	if (operation == 'newVlanId' || operation == 'editVlanId') {
		document.forms[formName].parentDomID.value = vlanSelectId;
	} else if (operation == 'newUserProfileSchedule' || operation == 'editUserProfileSchedule') {
		document.forms[formName].schedulerListName.value = 'leftOptions_schedulers';
	} else if (operation == 'newMacPolicyT' || operation == 'editMacPolicyT') {
		document.forms[formName].parentDomID.value = macPolicyTId;
	} else if (operation == 'newMacPolicyF' || operation == 'editMacPolicyF') {
		document.forms[formName].parentDomID.value = macPolicyFId;
	} else if (operation == 'newIpPolicyT' || operation == 'editIpPolicyT') {
		document.forms[formName].parentDomID.value = ipPolicyTId;
	} else if (operation == 'newIpPolicyF' || operation == 'editIpPolicyF') {
		document.forms[formName].parentDomID.value = ipPolicyFId;
	} else if (operation == 'newTunnel' || operation == 'editTunnel') {
		document.forms[formName].parentDomID.value = tunnelSelectId;
	} else if (operation == 'newQos' || operation == 'editQos') {
		document.forms[formName].parentDomID.value = qosSelectId;
	} else if (operation == 'newMarkerMap' || operation == 'editMarkerMap'){
		document.forms[formName].parentDomID.value = markerMapSelectId;
	} else if (operation == 'newMac' || operation == 'editMac') {
		document.forms[formName].parentDomID.value = macOurSelectId;
	} else if (operation == 'newDomain' || operation == 'editDomain') {
		document.forms[formName].parentDomID.value = domainSelectId;
	} else if (operation == 'newUserAttribute' || operation == 'editUserAttribute') {
		document.forms[formName].parentDomID.value = userAttrSelectId;
	} else if (operation == 'newOs' || operation == 'editOs') {
		document.forms[formName].parentDomID.value = osObjectSelectId;
	}
    document.forms[formName].submit();
}

function saveUserProfileJsonDlg(operation) {
	if (validate(operation)) {
		var url = "";
		if (operation == 'create') {
			url = "<s:url action='userProfiles' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		} else if (operation == 'update') {
			url = "<s:url action='userProfiles' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["userProfiles2Form"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveUserProfileJsonDlg, failure : failSaveUserProfileJsonDlg, timeout: 60000}, null);
	}
}

var succSaveUserProfileJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
					dealGroupUserProfileChg(details.parentDomID, details.addedId, details.addedName);
				}
				parentSelectDom.focus();
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSaveUserProfileJsonDlg = function(o) {
	// do nothing now.
}

function dealGroupUserProfileChg(eleId, addedId, addedName) {
	var hiveApUPElementIds = new Array('hiveAp_ethDefaultRegUserprofile','hiveAp_ethDefaultAuthUserprofile','leftOptions_ethUserProfiles',
										'hiveAp_userProfileEth0', 'hiveAp_userProfileEth1', 'hiveAp_userProfileAgg0', 'hiveAp_userProfileRed0');
	var bulkChg = false;
	for (var i = 0; i < hiveApUPElementIds.length; i++) {
		if (hiveApUPElementIds[i] == eleId) {
			bulkChg = true;
			break;
		}
	}
	if (bulkChg) {
		for (var i = 0; i < hiveApUPElementIds.length; i++) {
			if (hiveApUPElementIds[i] == eleId) {
				continue;
			}
			var parentSelectDom = parent.document.getElementById(hiveApUPElementIds[i]);
			if(parentSelectDom != null) {
				hm.util.insertSelectValue(addedId, addedName, parentSelectDom, false, false);
			}
		}
	}
	return bulkChg;
}

function onLoadPage() {
	if (document.getElementById("userProfileName").disabled == false) {
		document.getElementById("userProfileName").focus();
    }
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(810,700);
	 	}
	</s:if>
}
function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_USER_PROFILE%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	if (operation == "newVlanId" ||
		operation == "newTunnel" ||
		operation == "newQos" ||
		operation == "newUserAttribute" ||
		operation == "newMacPolicyT" ||
		operation == "newMacPolicyF" ||
		operation == "newIpPolicyT" ||
		operation == "newIpPolicyF" ||
		operation == "newUserProfileSchedule"
		|| operation == 'newMac'
		|| operation == 'newOs'
		|| operation == 'newMarkerMap'
		|| operation == 'newDomain'){
		var slaBandwidth = Get(formName + "_dataSource_slaBandwidth");
		if(isNaN(slaBandwidth.value)){slaBandwidth.value = 1000;}

		var abgMode = Get(formName + "_dataSource_policingRate");
		if(isNaN(abgMode.value)){abgMode.value = 54000;}
		var nMode = Get(formName + "_dataSource_policingRate11n");
		if(isNaN(nMode.value)){nMode.value = 1000000;}
		var acMode = Get(formName + "_dataSource_policingRate11ac");
		if(isNaN(acMode.value)){acMode.value = 1000000;}
		var weight = Get(formName + "_dataSource_schedulingWeight");
		if(isNaN(weight.value)){weight.value = 10;}

		var vlans = document.getElementById("myVlanSelect");
		if (hm.util.hasSelectedOptionSameValue(vlans, document.forms[formName].inputVlanValue)) {
			document.forms[formName].vlanId.value = vlans.options[vlans.selectedIndex].value;
		} else {
			document.forms[formName].vlanId.value = -1;
		}

		var attris = document.getElementById("myAttriSelect");
		if (hm.util.hasSelectedOptionSameValue(attris, document.forms[formName].inputAttriValue)) {
			document.forms[formName].userAttribute.value = attris.options[attris.selectedIndex].value;
		} else {
			document.forms[formName].userAttribute.value = -1;
		}
		return true;
	}

	if (operation == 'create' || operation == 'create<s:property value="lstForward"/>') {
		if (!validateName())
			return false;
	}

	if(!validateValueRange())
        return false;
	if (operation == 'create<s:property value="lstForward"/>'
			|| operation == 'update<s:property value="lstForward"/>'
			|| operation == 'create'
			|| operation == 'update') {
	    if (!checkFirewallPolicy(document.getElementById("macPolicyFrom"), document.getElementById("macPolicyTo"),
	    	document.forms[formName].macAction)
	    	|| !checkFirewallPolicy(document.getElementById("ipPolicyFrom"), document.getElementById("ipPolicyTo"),
	    	document.forms[formName].ipAction))
	    	return false;

    	if (!validateVlanValue()){
		    return false;
	    }

    	if(!validateMarkerMap()){
    		return false;
    	}
	    if (!validateTunnelSettings()){
	    	return false;
	    }
	    if(!checkMustInput())
		    return false;
	    if (!validateSlaSettings()){
	    	return false;
	    }
	}
	<s:if test="%{!easyMode}">
		if (!validateGuaranteedAirTime()) {
	    	return false;
	    }
		if (!validateAttributeGroup()){
			return false;
		}
		var feChild = document.getElementById("checkAll");
		var cbs = document.getElementsByName('ruleIndices');
		if (operation == 'addPolicyRules') {
			if (cbs.length >= 255) {
				showCliClaContent();
				hm.util.reportFieldError(feChild, '<s:text name="error.objectReachLimit"></s:text>');
				return false;
			}
			return true;
		}
		if (operation == 'removePolicyRules' || operation == 'removePolicyRulesNone') {
			if (cbs.length == 0) {
				showCliClaContent();
				hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
				return false;
			}
			if (!hm.util.hasCheckedBoxes(cbs)) {
				showCliClaContent();
	            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ipPolicy.rules" /></s:param></s:text>');
				return false;
			}
			return true;
		}
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>'
				|| operation == 'create' || operation == 'update') {
			<s:if test="%{showUpReassign}">
			if (Get(formName + "_dataSource_enableAssign").checked) {
				if (cbs.length == 0) {
					showCliClaContent();
					hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
					return false;
				}
			}
			</s:if>
		}
    </s:if>

    if (!validateRateLimit()){
    	return false;
    }

    if(operation == 'editUserProfileSchedule') {
    	var value = hm.util.validateOptionTransferSelection("schedulers");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].scheduler.value = value;
		}

    }

    if(operation == 'editUserAttribute') {
    	var value = hm.util.validateListSelection("myAttriSelect");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].userAttribute.value = value;
		}

    }

    if(operation == 'editQos') {
    	var value = hm.util.validateListSelection("qos");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].qos.value = value;
		}

    }
    
    if(operation == 'editMarkerMap') {
    	var value = hm.util.validateListSelection("markerMapId");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].markerMapId.value = value;
		}

    }

	if(operation == 'editTunnel') {
    	var value = hm.util.validateListSelection("tunnel");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].tunnel.value = value;
		}

    }

    if(operation == 'editVlanId') {
		var value = hm.util.validateListSelection("myVlanSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanId.value = value;
		}
    }

    if(operation == 'editMacPolicyF') {
    	var value = hm.util.validateListSelection("macPolicyFrom");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].macPolicyFrom.value = value;
		}
    }

    if(operation == 'editMacPolicyT') {
    	var value = hm.util.validateListSelection("macPolicyTo");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].macPolicyTo.value = value;
		}
    }

    if(operation == 'editIpPolicyF') {
    	var value = hm.util.validateListSelection("ipPolicyFrom");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].ipPolicyFrom.value = value;
		}
    }

    if(operation == 'editIpPolicyT') {
    	var value = hm.util.validateListSelection("ipPolicyTo");
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].ipPolicyTo.value = value;
		}
    }

    if(operation == 'editAsRuleGroup') {
    	var value = hm.util.validateListSelection("asRuleGroup");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].asRuleGroup.value = value;
		}
    }

    if(operation == "editMac" || operation == "editOs" || operation == "editDomain"){
		var value
		if (operation == "editMac") {
			value = hm.util.validateListSelection(formName + "_macObjId");
		} else if (operation == "editDomain") {
			value = hm.util.validateListSelection(formName + "_domObjId");
		} else {
			value = hm.util.validateListSelection(formName + "_osObjId");
		}
		if(value < 0){
			return false
		}else{
			document.forms[formName].editObjId.value = value;
		}
	}
    return true;
}

function validateName() {
    var inputElement = document.getElementById("userProfileName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.userprofile.name" />');
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}
function checkMustInput() {
    var inputElement=document.getElementById("qos");
    if (parseInt(inputElement.value)<0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.qosset" /></s:param></s:text>');
         showQosSettingsContent();
         inputElement.focus();
         return false;
     }
     return true;
}
function validateValueRange() {
    var inputElement=document.getElementById("attributeValue");
    if (inputElement.value.length == 0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.attribute" /></s:param></s:text>');
         inputElement.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.attribute" />',
                                                 <s:property value="%{numberRange.min()+1}" />,
                                                 <s:property value="%{numberRange.max()}" />);
     if (message != null) {
         hm.util.reportFieldError(inputElement,message);
         inputElement.focus();
         return false;
     }

     return true;
}

function validateMarkerMap(){
	var markerMapEle = document.getElementById('markerMapId');
	var markerMapName = $("#markerMapId option:selected").text();
	var pattern_value = /^0*[0-7]$/;
	if(pattern_value.test(markerMapName)){
		hm.util.reportFieldError(markerMapEle, '<s:text name="error.tip.userprofile.marker.mapping.title.not.allow"></s:text>');
		markerMapEle.focus();
        return false;
	}
	return true;
}

function validateVlanValue() {
	var showError = document.getElementById("errorDisplay");
	var vlans = document.getElementById("myVlanSelect");
	var vlanEle = document.forms[formName].inputVlanValue;
	if ("" == vlanEle.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.userprofile.vlan" /></s:param></s:text>');
        vlanEle.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(vlans, vlanEle)) {
		var message = hm.util.validateIntegerRange(vlanEle.value, '<s:text name="config.userprofile.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanEle.focus();
            return false;
        }
		document.forms[formName].vlanId.value = -1;
	} else {
		document.forms[formName].vlanId.value = vlans.options[vlans.selectedIndex].value;
	}
	return true;
}

function validateAttributeGroup() {
	var attris = document.getElementById("myAttriSelect");
	var attriEle = document.forms[formName].inputAttriValue;
	if ("" != attriEle.value) {
        if (!hm.util.hasSelectedOptionSameValue(attris, attriEle)) {
			var message = checkAttributeFormat(attriEle, '<s:text name="config.userprofile.attributeGroup" />');
			if (message != null) {
				showAdvContent();
	            hm.util.reportFieldError(document.getElementById("errorAttribute"), message);
	            attriEle.focus();
	            return false;
	        }
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(attris, attriEle)) {
		document.forms[formName].userAttribute.value = -1;
	} else {
		document.forms[formName].userAttribute.value = attris.options[attris.selectedIndex].value;
	}
	return true;
}

function checkAttributeFormat(attriValue, title) {
	var attrInThis = new Array();
	var attributes = attriValue.value.split(",");
	if (attributes.length > 9) {
		return '<s:text name="error.entryLimit"><s:param>'+title+'</s:param><s:param value="9" /></s:text>';
	}
	for (var i = 0; i < attributes.length; i++) {
		var str_attribute = attributes[i];
		if("" == str_attribute) {
			return '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>';
		}

		if(!isNaN(str_attribute)) {
			// it is a number;
			var message = hm.util.validateIntegerRange(str_attribute, title,1,4095);
	     	if (message != null) {
	           	return message;
	     	}
	     	for(var j = 0; j < attrInThis.length; j++) {
				if (attrInThis[j] == str_attribute) {
					return '<s:text name="error.sameObjectExists"><s:param>'+title+'</s:param></s:text>';
				}
			}
			attrInThis.push(str_attribute);
		} else {
			// it is a range;
			var str_range = str_attribute.split("-");
			if (str_range.length != 2) {
				return '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>';
			} else {
				if(!isNaN(str_range[0]) && !isNaN(str_range[1])) {
					var message1 = hm.util.validateIntegerRange(str_range[0], title,1,4095);
					var message2 = hm.util.validateIntegerRange(str_range[1], title,1,4095);
	     			if (message1 != null) {
	           			return message1;
	     			}
	     			if (message2 != null) {
	           			return message2;
	     			}
					if (parseInt(str_range[0]) >= parseInt(str_range[1])) {
						return '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>';
					}
					for(var j = 0; j < attrInThis.length; j++) {
						if (attrInThis[j] == str_range) {
							return '<s:text name="error.sameObjectExists"><s:param>'+title+'</s:param></s:text>';
						}
					}
					attrInThis.push(str_range);
				} else {
					return '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>';
				}
			}
		}
	}
	return null;
}

function validateRateLimit(){
	var abgMode=document.getElementById(formName + "_dataSource_policingRate");
    if (abgMode.value.length == 0) {
         hm.util.reportFieldError(document.getElementById("rateMessage"), '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.abgMode" /></s:param></s:text>');
         showQosSettingsContent();
         abgMode.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(abgMode.value, '<s:text name="config.userprofile.abgMode" />', 0, 54000);
     if (message != null) {
         hm.util.reportFieldError(document.getElementById("rateMessage"),message);
         showQosSettingsContent();
         abgMode.focus();
         return false;
     }

     var nMode=document.getElementById(formName + "_dataSource_policingRate11n");
    if (nMode.value.length == 0) {
         hm.util.reportFieldError(document.getElementById("rateMessage"), '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.nMode" /></s:param></s:text>');
         showQosSettingsContent();
         nMode.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(nMode.value, '<s:text name="config.userprofile.nMode" />', 0, 2000000);
     if (message != null) {
         hm.util.reportFieldError(document.getElementById("rateMessage"),message);
         showQosSettingsContent();
         nMode.focus();
         return false;
     }
     
     var acMode=document.getElementById(formName + "_dataSource_policingRate11ac");
     if (acMode.value.length == 0) {
          hm.util.reportFieldError(document.getElementById("rateMessage"), '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.acMode" /></s:param></s:text>');
          showQosSettingsContent();
          acMode.focus();
          return false;
      }
      var message = hm.util.validateIntegerRange(acMode.value, '<s:text name="config.userprofile.acMode" />', 0, 2000000);
      if (message != null) {
          hm.util.reportFieldError(document.getElementById("rateMessage"),message);
          showQosSettingsContent();
          acMode.focus();
          return false;
      }

    var weight=document.getElementById(formName + "_dataSource_schedulingWeight");
    if (weight.value.length == 0) {
         hm.util.reportFieldError(document.getElementById("rateMessage"), '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.weight" /></s:param></s:text>');
         showQosSettingsContent();
         weight.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(weight.value, '<s:text name="config.userprofile.weight" />', 0, 1000);
     if (message != null) {
         hm.util.reportFieldError(document.getElementById("rateMessage"),message);
         showQosSettingsContent();
         weight.focus();
         return false;
     }

     return true;

}

function validateTunnelSettings(){
	var radioBoxs = document.getElementsByName("tunnelType");
	var radioBoxValue;
	for(var i=0; i<radioBoxs.length; i++){
		if(radioBoxs[i].checked){
			radioBoxValue = radioBoxs[i].value;
			break;
		}
	}
	if(radioBoxValue == <%=UserProfile.TUNNEL_GRE%>){
		var tunnelEl = document.getElementById("tunnel");
		if(tunnelEl.value < 0){
			hm.util.reportFieldError(tunnelEl, '<s:text name="error.requiredField"><s:param><s:text name="config.userprofile.tunnel" /></s:param></s:text>');
         	showGreVpnTunnelsContent();
         	tunnelEl.focus();
			return false;
		}
	}
	return true;
}

function validateSlaSettings(){
	var slaEnable = Get(formName + "_dataSource_slaEnable");
	var slaBandwidth = Get(formName + "_dataSource_slaBandwidth");
	if(slaEnable.checked){
		var message = hm.util.validateIntegerRange(slaBandwidth.value, '<s:text name="config.userprofile.sla.bandwidth" />',
		                <s:property value="%{slaBandwidthRange.min()}" />,
		                <s:property value="%{slaBandwidthRange.max()}" />);
		if (message != null) {
			hm.util.reportFieldError(slaBandwidth, message);
			showSlaContent();
			slaBandwidth.focus();
			return false;
		}
	}
	return true;
}

function validateGuaranteedAirTime() {
	var inputElement = document.getElementById(formName + "_dataSource_guarantedAirTime");
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.guarantedAirTime" />',
                                                       <s:property value="%{guaranteedAirTimeRange.min()}" />,
                                                       <s:property value="%{guaranteedAirTimeRange.max()}" />);

    if (message != null) {
    	hm.util.reportFieldError(inputElement, message);
    	showQosSettingsContent();
        inputElement.focus();
        return false;
    }

    return true;
}

function changeAirTime(value) {
	if(value == 0) {
		document.getElementById("enableShareTime").disabled = true;
		document.getElementById("enableShareTime").checked = false;
	} else {
		document.getElementById("enableShareTime").disabled = false;
	}
}

function checkFirewallPolicy(from, to, action, title) {
	if (parseInt(from.value)<0 && parseInt(to.value)<0 && 'None' != action.value) {
		hm.util.reportFieldError(action, '<s:text name="error.user.profile.firewall.action.blank"></s:text>');
        showFirewallsContent();
        action.focus();
        return false;
	}
	if ((parseInt(from.value)>0 || parseInt(to.value)>0) && 'None' == action.value) {
		hm.util.reportFieldError(action, '<s:text name="error.user.profile.firewall.action"></s:text>');
        showFirewallsContent();
        action.focus();
        return false;
	}
	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('ruleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function openQuestionMask() {
	showInfoDialog('<s:text name="config.userprofile.osobject.information"/>');
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="userProfiles" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id==null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="displayName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="displayName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>
<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
<s:form action="userProfiles" name="userProfiles2Form" id="userProfiles2Form">
	<s:hidden name="ipPolicyType" />
	<s:hidden name="macPolicyType" />
	<s:hidden name="scheduler" />
	<s:hidden name="vlanId" />
	<s:hidden name="userAttribute" />
	<s:hidden name="dataSource.firewallDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.qosSettingDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.greVpnTunnelDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.scheduleDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.slaDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.clientClassDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.advSettingDisplayStyle"></s:hidden>
	<s:hidden name="editObjId" />
	<s:hidden name="domainNameId" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="dealingParentId" />
		<s:hidden name="dealUserProfile4Who" />
		<s:hidden name="userProfileSubTabId" />
		<s:hidden name="upType" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="schedulerListName" />
	</s:if>
	<s:hidden name="blnForceUpControl" />
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="userProfileDlgTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="80%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-users-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.userProfile"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.userProfile.edit"/></td>
						</s:else>
						<td style="padding-left:10px;">
							<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
								<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
									alt="" class="dblk"/>
							</a>
						</td>
					</tr>
				</table>
				</td>
				<td>
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userProfileDlgSaveBtnId" style="float: right;" onclick="saveUserProfileJsonDlg('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userProfileDlgSaveBtnId" style="float: right;" onclick="saveUserProfileJsonDlg('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:else>
					</tr>
				</table>
				</s:if>
				<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userProfileDlgSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userProfileDlgSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<div style="margin:0 auto; width:100%;">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_USER_PROFILE%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
			<table style="padding: 0 10px 6px 4px;" cellspacing="0" cellpadding="0" border="0">
			</s:if>
			<s:else>
			<table class="editBox" style="padding: 0 10px 6px 4px;" cellspacing="0" cellpadding="0" border="0" width="700px">
			</s:else>
				<tr>
					<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="200px"><s:text
								name="config.userprofile.name" /><font color="red"><s:text name="*"/></font></td>
							<td style="padding:6px 0px 0px 0px;"><s:textfield name="dataSource.userProfileName" size="24" id="userProfileName"
								maxlength="%{nameLength}" disabled="%{disabledName}"
								onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="config.name.range"/></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.userprofile.attribute" /><font color="red"><s:text name="*"/></font></td>
							<td style="padding:6px 0px 0px 0px;"><s:textfield name="dataSource.attributeValue" size="24" maxlength="4" id="attributeValue"
							     disabled="%{disabledName}"  onkeypress="return hm.util.keyPressPermit(event,'ten');"
							     title="%{attributeTooltip}"/>
							     <s:text name="config.userprofile.attrubute.range"/></td>
						</tr>
						<!--<tr style="display:<s:property value="wirelessRoutingEnable?'':'none'"/>">-->
						<tr>
					    	<td class="labelT1">
								<s:text name="config.userprofile.vlan" /><font color="red"><s:text name="*"/></font></td>
					    	<td style="padding:6px 0px 0px 0px;">
								<ah:createOrSelect divId="errorDisplay" list="vlanIdList" typeString="VlanId"
									selectIdName="myVlanSelect" inputValueName="inputVlanValue" swidth="150px"
									stitle="config.userprofile.vlan.tooltip" />
						    </td>
						</tr>

						<!--<tr style="display:<s:property value="displayVlan"/>" id="vlanObjTr">
							<td class="labelT1"><s:text name="config.userprofile.vlan" /><font color="red"><s:text name="*"/></font></td>
							<td style="padding:6px 0px 0px 0px;">
								<ah:createOrSelect divId="errorDisplay" list="vlanIdList" typeString="VlanId"
								selectIdName="myVlanSelect" inputValueName="inputVlanValue" swidth="150px"
								stitle="config.userprofile.vlan.tooltip" />
							</td>
						</tr>-->
						<tr>
							<td class="labelT1"><s:text name="config.userprofile.description" /></td>
							<td style="padding:6px 0px 0px 0px;"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range" /></td>
						</tr>
						<tr>
							<td style="padding: 2px 0 4px 6px" colspan="2">
								<s:checkbox name="dataSource.blnUserManager"></s:checkbox>
								<s:text name="config.userprofile.userManager" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<%-- tr>
					<td>
					<div style="display: <s:property value="%{showUserCategoryDiv}"/>" id="userCategoryDiv" >
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="100px">
								<s:text name="config.userprofile.template" />
							</td>
							<td>
								<s:radio label="Gender"	name="dataSource.userCategory" list="%{tempEmployee}" listKey="key" listValue="value"
								onclick="templateChange(this.value)" /></td>
							<td style="padding-left:15px">
								<s:radio label="Gender"	name="dataSource.userCategory" list="%{tempGuest}" listKey="key" listValue="value"
								onclick="templateChange(this.value)" /></td>
							<td style="padding-left:15px">
								<s:radio label="Gender"	name="dataSource.userCategory" list="%{tempVoice}" listKey="key" listValue="value"
								onclick="templateChange(this.value)" /></td>
							<td style="padding-left:15px">
								<s:radio label="Gender"	name="dataSource.userCategory" list="%{tempCustom}" listKey="key" listValue="value"
								onclick="templateChange(this.value)" /></td>
						</tr>
					</table>
					</div>
					</td>
				</tr> --%>
				<tr>
					<td width="100%">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<fieldset><legend><s:text name="config.userprofile.optional.setting" /></legend>
								<table width="100%" cellspacing="0" cellpadding="0" border="0">
									<tr><td height="10px"></td></tr>
									<!-- GRE Tunnels -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.tunnels" />','greVpnTunnels');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="greVpnTunnels" style="display: <s:property value="%{dataSource.greVpnTunnelDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr><td height="5px"></td></tr>
												<tr>
													<td><s:radio onclick="tunnelSettingChanged(this);" label="Gender"	name="tunnelType" list="%{tunnelOption1}" listKey="key" listValue="value"/></td>
												</tr>
												<tr>
													<td><s:radio onclick="tunnelSettingChanged(this);" label="Gender"	name="tunnelType" list="%{tunnelOption2}" listKey="key" listValue="value"/></td>
												</tr>
												<tr>
													<td style="padding-left: 15px;">
														<div id="greTunnelSetting" style="display: <s:property value="%{greTunnelSettingStyle}"/>">
														<table cellspacing="0" cellpadding="0" border="0">
															 <tr>
														    	<td class="labelT1" width="170px"><s:text
																	name="config.userprofile.tunnel" /></td>
																<td width="260"><s:select id="tunnel" name="tunnel" value="%{tunnel}"
																    list="%{tunnelList}" listKey="id" listValue="value"
																    title="%{tunnelTooltip}" cssStyle="width: 150px;"  />
															        <s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/new_disable.png" />"
																		width="16" height="16" alt="New" title="New" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('newTunnel')"><img class="dinl"
																		src="<s:url value="/images/new.png" />"
																		width="16" height="16" alt="New" title="New" /></a>
																	</s:else>
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/modify_disable.png" />"
																		width="16" height="16" alt="Modify" title="Modify" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitAction('editTunnel')"><img class="dinl"
																		src="<s:url value="/images/modify.png" />"
																		width="16" height="16" alt="Modify" title="Modify" /></a>
																	</s:else>
																	<s:if test="%{easyMode}">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn"
																			src="<s:url value="/images/cancel_disable.png" />"
																			width="16" height="16" alt="Remove" title="Remove" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitRemoveAction('removeTunnel')"><img class="dinl"
																			src="<s:url value="/images/cancel.png" />"
																			width="16" height="16" alt="Remove" title="Remove" /></a>
																		</s:else>
																	</s:if>
															    </td>
															</tr>
														</table>
														</div>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr><td height="10px"></td></tr>
									<!-- fire walls -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.firewall" />','firewalls');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="firewalls" style="display: <s:property value="%{dataSource.firewallDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0"
												title="If a firewall policy is not specified, all client traffic is permitted">
											<tr><td height="5px"></td></tr>
											<s:if test="%{firewallNote != ''}">
											<tr>
												<td class="noteInfo">
												<span style="padding-top: 5px; padding-bottom: 5px; padding-left: 25px;"><s:property
													value="firewallNote"/></span></td>
											</tr>
											<tr><td height="5px"></td></tr>
											</s:if>
											<tr>
												<td>
												<fieldset style="width: 460px"><legend><s:text name="config.userprofile.mac.firewall" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="173px"><s:text
																	name="config.userprofile.from.policy" /></td>
																<td width="250"><s:select id="macPolicyFrom" name="macPolicyFrom" value="%{macPolicyFrom}"
																    list="%{macPolicyFromList}" listKey="id" listValue="value"
																    cssStyle="width: 150px;" />
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newMacPolicyF')"><img class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editMacPolicyF')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
																<s:if test="%{easyMode}">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/cancel_disable.png" />"
																		width="16" height="16" alt="Remove" title="Remove" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitRemoveAction('removeMacPolicyF')"><img class="dinl"
																		src="<s:url value="/images/cancel.png" />"
																		width="16" height="16" alt="Remove" title="Remove" /></a>
																	</s:else>
																</s:if>
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:text
																	name="config.userprofile.to.policy" /></td>
																<td width="250"><s:select id="macPolicyTo" name="macPolicyTo" value="%{macPolicyTo}"
																    list="%{macPolicyToList}" listKey="id" listValue="value"
																    cssStyle="width: 150px;" />
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newMacPolicyT')"><img class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editMacPolicyT')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
																<s:if test="%{easyMode}">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/cancel_disable.png" />"
																		width="16" height="16" alt="Remove" title="Remove" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitRemoveAction('removeMacPolicyT')"><img class="dinl"
																		src="<s:url value="/images/cancel.png" />"
																		width="16" height="16" alt="Remove" title="Remove" /></a>
																	</s:else>
																</s:if>
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:text
																	name="config.userprofile.action" /></td>
																<td width="220">
																   <s:select name="macAction"	value="%{macAction}"
																    list="enumDefaultAction"	listKey="key" listValue="value"
																    cssStyle="width: 150px;" /></td>
															</tr>

														</table>
														</td>
													</tr>
												</table>
												</fieldset>
												</td>
											</tr>
											<tr><td height="10px"></td></tr>
											<tr>
												<td>
												<fieldset style="width: 460px"><legend><s:text name="config.userprofile.ip.firewall" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding-top:4px;">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="173px"><s:text
																	name="config.userprofile.from.policy" /></td>
																<td width="250"><s:select id="ipPolicyFrom" name="ipPolicyFrom" value="%{ipPolicyFrom}"
																    list="%{ipPolicyFromList}" listKey="id" listValue="value"
																    cssStyle="width: 150px;" />
															    <s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newIpPolicyF')"><img class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editIpPolicyF')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
																<s:if test="%{easyMode}">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/cancel_disable.png" />"
																		width="16" height="16" alt="Remove" title="Remove" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitRemoveAction('removeIpPolicyF')"><img class="dinl"
																		src="<s:url value="/images/cancel.png" />"
																		width="16" height="16" alt="Remove" title="Remove" /></a>
																	</s:else>
																</s:if>

															    </td>
															</tr>
															<tr>
																<td class="labelT1"><s:text
																	name="config.userprofile.to.policy" /></td>
																<td width="250"><s:select id="ipPolicyTo" name="ipPolicyTo" value="%{ipPolicyTo}"
																    list="%{ipPolicyToList}" listKey="id" listValue="value"
																    cssStyle="width: 150px;" />
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newIpPolicyT')"><img class="dinl"
																	src="<s:url value="/images/new.png" />"
																	width="16" height="16" alt="New" title="New" /></a>
																</s:else>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/modify_disable.png" />"
																	width="16" height="16" alt="Modify" title="Modify" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('editIpPolicyT')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
																<s:if test="%{easyMode}">
																	<s:if test="%{writeDisabled == 'disabled'}">
																		<img class="dinl marginBtn"
																		src="<s:url value="/images/cancel_disable.png" />"
																		width="16" height="16" alt="Remove" title="Remove" />
																	</s:if>
																	<s:else>
																		<a class="marginBtn" href="javascript:submitRemoveAction('removeIpPolicyT')"><img class="dinl"
																		src="<s:url value="/images/cancel.png" />"
																		width="16" height="16" alt="Remove" title="Remove" /></a>
																	</s:else>
																</s:if>
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:text
																	name="config.userprofile.action" /></td>
																<td width="220"><s:select name="ipAction"	value="%{ipAction}"
																    list="enumDefaultAction"	listKey="key" listValue="value"
																    cssStyle="width: 150px;" /></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>
												</fieldset>
												</td>
											</tr>
											<%--
											<tr><td height="5px"></td></tr>
											<tr>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" style="padding-left: 20px;" width="173px"><s:text
																name="config.air.screen.rule.group.fullName" /></td>
															<td width="220px"><s:select id="asRuleGroup" name="asRuleGroup" value="%{asRuleGroup}"
															    list="%{asRuleGroupList}" listKey="id" listValue="value"
															    cssStyle="width: 150px;" />
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/new_disable.png" />"
																width="16" height="16" alt="New" title="New" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('newAsRuleGroup')"><img class="dinl"
																src="<s:url value="/images/new.png" />"
																width="16" height="16" alt="New" title="New" /></a>
															</s:else>
															<s:if test="%{writeDisabled == 'disabled'}">
																<img class="dinl marginBtn"
																src="<s:url value="/images/modify_disable.png" />"
																width="16" height="16" alt="Modify" title="Modify" />
															</s:if>
															<s:else>
																<a class="marginBtn" href="javascript:submitAction('editAsRuleGroup')"><img class="dinl"
																src="<s:url value="/images/modify.png" />"
																width="16" height="16" alt="Modify" title="Modify" /></a>
															</s:else>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											--%>
											</table>
											</div>
										</td>
									</tr>
									<tr><td height="10px"></td></tr>
									<!-- QoS settings -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.qoss" />','qosSettings');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="qosSettings" style="display: <s:property value="%{dataSource.qosSettingDisplayStyle}"/>">
											<table width="620px" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1"><s:text name="config.configTemplate.markerMap" /></td>
													<td><s:select name="markerMapId" id="markerMapId"
														list="%{markerMapList}" listKey="id" listValue="value"
														onchange="changeMarkerMap(this.value);"
														cssStyle="width: 180px;" />
														<s:if test="%{writeDisabled == 'disabled'}">
															<img class="dinl marginBtn"
															src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" />
														</s:if>
														<s:else>
															<a class="marginBtn" href="javascript:submitAction('newMarkerMap')"><img class="dinl"
															src="<s:url value="/images/new.png" />"
															width="16" height="16" alt="New" title="New" /></a>
														</s:else>
														<s:if test="%{writeDisabled == 'disabled'}">
															<img class="dinl marginBtn"
															src="<s:url value="/images/modify_disable.png" />"
															width="16" height="16" alt="Modify" title="Modify" />
														</s:if>
														<s:else>
															<a class="marginBtn" href="javascript:submitAction('editMarkerMap')"><img class="dinl"
															src="<s:url value="/images/modify.png" />"
															width="16" height="16" alt="Modify" title="Modify" /></a>
														</s:else>
													</td>
												</tr>
												<tr>
													<td colspan="2" style="padding-left: 10px;">
														<s:text name="config.userprofile.qos.marktype"></s:text>
													</td>
												</tr>
												<tr>
												<td colspan="2">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1"  style="padding-left: 32px; width: 145px;">
																<s:radio label="Gender" 
																	name="dataSource.qosMarkTypeMode" list="%{qosDscp}"
																	listKey="key" listValue="value" 
																	disabled="%{!dataSource.enableQosMarkType}"/>
															</td>
															<td class="labelT1">
																<s:radio label="Gender" 
																	name="dataSource.qosMarkTypeMode" list="%{qos8021p}"
																	listKey="key" listValue="value" 
																	disabled="%{!dataSource.enableQosMarkType}"/>
															</td>
														</tr>
													</table>
												</td>
												</tr>
												<tr>
													<td class="labelT1" width="184px"><s:text name="config.userprofile.qosset" /><font color="red"><s:text name="*"/></font></td>
													<td width="240px"><s:select id="qos" name="qos" value="%{qos}"
													    list="%{qosList}" listKey="id" listValue="value"
													    cssStyle="width: 150px;" />
											    <s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:submitAction('newQos')"><img class="dinl"
													src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
												</s:else>
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:submitAction('editQos')"><img class="dinl"
													src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else>
												</td>
												</tr>
												<s:if test="%{!easyMode}">
												<tr>
													<%-- <td style="padding:8px 0px 0px 10px;">
														<s:checkbox name="dataSource.enableCallAdmissionControl" onchange="changeCallAdmissionControl(this.checked);"></s:checkbox>
													</td>--%>
													<td class="labelT1">
														<s:text	name="config.userprofile.guarantedAirTime" />
													</td>
													<td width="320">
														<s:textfield name="dataSource.guarantedAirTime" size="24" maxlength="3"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" onkeyup="changeAirTime(this.value);" />
											     		<s:text name="config.userprofile.guaranteedAirTime.range"/>
											     	</td>
												</tr>
												<tr>
													<td colspan="2">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td style="padding-left: 5px;">
																<s:checkbox	id="enableShareTime" name="dataSource.enableShareTime"
																	value="%{dataSource.enableShareTime}" disabled="%{enableShareTime}" />
															</td>
															<td>
																<s:text name="config.userprofile.enableShareTime" />
															</td>
														</tr>
													</table>
													</td>
												</tr>
												</s:if>
												<tr>
													<td class="labelT1"> <s:text name="config.userprofile.raidoPolicyRate" /></td>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr><td colspan="4"><table><tr><td><div id="rateMessage"/></td></tr></table></td></tr>
															<tr>
																<td width="120px">
																	<s:text name="config.userprofile.abgMode" />
																</td>
																<td width="120px">
																	<s:text name="config.userprofile.nMode" />
																</td>
																<td width="120px">
																	<s:text name="config.userprofile.acMode" />
																</td>
																<td width="120px">
																	<s:text name="config.userprofile.weight" />
																</td>
															</tr>
															<tr>
																<td>
																	<s:textfield name="dataSource.policingRate" size="10" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																</td>
																<td>
																	<s:textfield name="dataSource.policingRate11n" size="10" maxlength="7"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																</td>
																<td>
																	<s:textfield name="dataSource.policingRate11ac" size="10" maxlength="7"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																</td>
																<td>
																	<s:textfield name="dataSource.schedulingWeight" size="10" maxlength="4"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																</td>
															</tr>
															<tr height="2px">
															</tr>
														</table>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<tr><td height="10px"></td></tr>
									<!-- Schedules -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.tab.schedule" />','schedules');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 15px;">
											<div id="schedules" style="display: <s:property value="%{dataSource.scheduleDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
											    <tr>
											       <td height="5"></td>
											    </tr>
												<tr>
													<td><s:text name="config.userprofile.denyaction"/></td>
													<td style="padding-left:10px;">
                                                           <s:select list="%{scheduleDenyModeList}" cssStyle="width:180px;"
																 listKey="key" listValue="value" name="dataSource.scheduleDenyMode"></s:select>
                                                     </td>
												</tr>
											</table>
											<table cellspacing="0" cellpadding="0" border="0">
											    <tr>
											       <td height="5"></td>
											    </tr>
												<tr>
													<s:push value="%{schedulerOptions}">
														<td><tiles:insertDefinition name="optionsTransfer" /></td>
													</s:push>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<%--<tr>
													<td style="padding-left:40px;">
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newUserProfileSchedule')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editUserProfileSchedule')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
													</td>
												</tr> --%>
											</table>
											</div>
										</td>
									</tr>
									<tr><td height="10px"></td></tr>
									<!-- Bandwidth Sentinel -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.sla" />','sla');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="sla" style="display: <s:property value="%{dataSource.slaDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr><td height="5px"></td></tr>
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td style="padding-left: 5px;">
																<s:checkbox	name="dataSource.slaEnable" value="%{dataSource.slaEnable}" onclick="showSlaConfig(this.checked);" />
															</td>
															<td style="padding-left: 0">
																<s:text name="config.userprofile.sla.enable" />
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="184px">
																<s:text	name="config.userprofile.sla.bandwidth" />
															</td>
															<td width="320px">
																<s:textfield name="dataSource.slaBandwidth" size="24" maxlength="6"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled="%{slaConfigDisabled}" />
													     		<s:text name="config.userprofile.sla.bandwidth.range"/>
													     	</td>
														</tr>
														<tr>
															<td class="labelT1">
																<s:text	name="config.userprofile.sla.action" />
															</td>
															<td>
																<s:select list="%{slaAction}" cssStyle="width:150px;"
																 listKey="key" listValue="value" name="dataSource.slaAction"  disabled="%{slaConfigDisabled}" ></s:select>
													     	</td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									<s:if test="%{!easyMode}">
									<s:if test="%{showUpReassign}">
									<tr><td height="10px"></td></tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.reassign.setting.title" />','cliCla');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="cliCla" style="display: <s:property value="%{dataSource.clientClassDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr><td height="5px"></td></tr>
												<!-- Reassign setting -->
												<tr>
													<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td style="padding-left: 5px;">
																<s:checkbox	name="dataSource.enableAssign" onclick="showReassignPanel(this.checked);" />
															</td>
															<td style="padding-left: 0">
																<s:text name="config.userprofile.reassign.enable" />
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr id="reassignPanel" style="display: <s:property value="%{dataSource.enableAssign?'':'none'}"/>">
													<td style="padding:0 10px 0 10px">
														<fieldset><legend><s:text name="config.ipPolicy.rules" /></legend>
															<table cellspacing="0" cellpadding="0" border="0" width="725px">
																<tr>
																	<td style="padding:4px 0px 4px 0px;" valign="top">
																		<table cellspacing="0" cellpadding="0" border="0" class="embedded">

																			<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
																				<td colspan="6" style="padding-bottom: 2px;">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																							class="button" onClick="showCreateSection();"></td>
																						<td><input type="button" name="ignore" value="Remove"
																							class="button" <s:property value="writeDisabled" />
																							onClick="submitAction('removePolicyRules');"></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
																				<td colspan="6" style="padding-bottom: 2px;">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																							class="button" onClick="submitAction('addPolicyRules');"></td>
																						<td><input type="button" name="ignore" value="Remove"
																							class="button" <s:property value="writeDisabled" />
																							onClick="submitAction('removePolicyRulesNone');"></td>
																						<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																							class="button" onClick="hideCreateSection();"></td>
																					</tr>
																				</table>
																				</td>
																			</tr>
																			<tr id="headerSection">
																				<th align="left" style="padding-left: 0;" width="10"><input
																					type="checkbox" id="checkAll"
																					onClick="toggleCheckAllRules(this);"></th>
																				<th align="left" width="50"><s:text
																					name="config.ipPolicy.ruleId" /></th>
																				<th align="left" width="155"><s:text
																					name="config.devicePolicy.mac" /></th>
																				<th align="left" width="155"><s:text
																					name="config.devicePolicy.os" />
																					<a style="padding-left: 15px;" href="javascript: openQuestionMask();">?</a>
																				</th>
																				<th align="left" width="155"><s:text
																					name="config.devicePolicy.domain" /></th>
																				<s:if test="%{enableClientManagement}">
																				<th align="left" width="155"><s:text
																					name="config.devicePolicy.ownership" /></th>
																				</s:if>
																				<th align="left" width="150"><s:text
																					name="config.devicePolicy.userProfile" /></th>
																			</tr>
																			<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
																				<td class="listHead" width="10" nowrap>&nbsp;</td>
																				<td class="listHead" width="50" nowrap>&nbsp;</td>
																				<td class="listHead" valign="top" nowrap>
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																							<td><s:select name="macObjId" list="%{availableMacAddress}" listKey="id" listValue="value"
																								cssStyle="width: 150px;" /></td>
																						</tr>
																						<tr>
																							<td valign="top" style="padding-left:105px;">
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('newMac')"><img class="dinl"
																									src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																								</s:else>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify" title="Modify" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('editMac')"><img class="dinl"
																									src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																								</s:else>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<td class="listHead" valign="top" nowrap>
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																							<td><s:select name="osObjId" list="%{availableOsObjects}" listKey="id" listValue="value"
																								cssStyle="width: 150px;" /></td>
																						</tr>
																						<tr>
																							<td valign="top" style="padding-left:105px;">
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('newOs')"><img class="dinl"
																									src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																								</s:else>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify" title="Modify" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('editOs')"><img class="dinl"
																									src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																								</s:else>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<td class="listHead" valign="top" nowrap>
																					<table border="0" cellspacing="0" cellpadding="0">
																						<tr>
																								<td><s:select name="domObjId" list="%{availableDomainObjects}" listKey="id" listValue="value"
																									cssStyle="width: 150px;" /></td>
																						</tr>
																						<tr>
																							<td valign="top" style="padding-left:105px;">
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																									width="16" height="16" alt="New" title="New" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('newDomain')"><img class="dinl"
																									src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																								</s:else>
																								<s:if test="%{writeDisabled == 'disabled'}">
																									<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																									width="16" height="16" alt="Modify" title="Modify" />
																								</s:if>
																								<s:else>
																									<a class="marginBtn" href="javascript:submitAction('editDomain')"><img class="dinl"
																									src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																								</s:else>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<s:if test="%{enableClientManagement}">
																				<td class="listHead" valign="top" nowrap>
																					<s:select name="ownershipId" cssStyle="width: 70px;"
																						list="%{enumOwnership}" listKey="key" listValue="value" /></td>
																				</s:if>
																				<td class="listHead" valign="top" nowrap>
																					<s:select name="userProfileId" cssStyle="width: 150px;"
																						list="%{availableUserProfiles}" listKey="id" listValue="value" /></td>
																			</tr>
																			<s:if test="%{gridCount > 0}">
																				<s:generator separator="," val="%{' '}" count="%{gridCount}">
																					<s:iterator>
																						<tr>
																							<td class="list" colspan="6">&nbsp;</td>
																						</tr>
																					</s:iterator>
																				</s:generator>
																			</s:if>
																		<tr>
																		<s:if test="%{enableClientManagement}">
																			<td valign="top" colspan="7">
																			</s:if>
																			<s:else>
																			<td valign="top" colspan="6">
																			</s:else>
																				<table cellspacing="0" cellpadding="0" border="0"
																					class="embedded" id="policyTable">
																					<s:iterator value="%{dataSource.assignRules}" status="status">
																						<tr>
																							<td class="listCheck" width="10"><s:checkbox name="ruleIndices"
																								fieldValue="%{#status.index}" /></td>
																							<td class="list" width="50"><s:property value="ruleId" /></td>
																							<td class="list" width="205">&nbsp;&nbsp;
																								<s:if test="%{macObj == null}">
																									<s:text name="config.ipPolicy.any" />
																								</s:if>
																								<s:else
																									><s:property value="macObj.macOrOuiName" />
																								</s:else>
																							</td>
																							<td class="list" width="205">&nbsp;&nbsp;
																								<s:if test="%{osObj == null}">
																									<s:text name="config.ipPolicy.any" /></s:if>
																								<s:else
																									><s:property value="osObj.osName" /></s:else>
																							</td>
																							<td class="list" width="205">&nbsp;&nbsp;
																								<s:if test="%{domObj == null}">
																									<s:text name="config.ipPolicy.any" /></s:if>
																								<s:else
																									><s:property value="domObj.objName" /></s:else>
																							</td>
																							<s:if test="%{enableClientManagement}">
																							<td class="list" width="70"><s:property value="ownershipName" /></td>
																							</s:if>
																							<td width="150" class="list" valign="top"><s:select cssStyle="width: 150px;"
																								list="%{availableUserProfiles}" listKey="id"
																								listValue="value" name="userProfileIds" value="%{userProfileId}"/>
																								<s:hidden name="ordering" value="%{#status.index}" /></td>
																							</tr>
																					</s:iterator>
																				</table>
																			</td>
																			<s:if test="%{dataSource.assignRules.size() > 1}">
																				<td valign="top" style="padding: 0px 0px 0 10px;">
																					<table cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<td><input type="button" class="moveRow" value="Up"
																								onclick="hm.util.moveRowsUp('policyTable');" /></td>
																						</tr>
																						<tr>
																							<td><input type="button" class="moveRow" value="Down"
																								onclick="hm.util.moveRowsDown('policyTable');" /></td>
																						</tr>
																						<s:if test="%{dataSource.assignRules.size() > 15}">
																						<s:generator separator="," val="%{' '}" count="%{dataSource.assignRules.size()-2}">
																							<s:iterator>
																								<tr>
																									<td>&nbsp;</td>
																								</tr>
																							</s:iterator>
																						</s:generator>
																							<tr>
																								<td><input type="button" class="moveRow" value="Up"
																									onclick="hm.util.moveRowsUp('policyTable');" /></td>
																							</tr>
																							<tr>
																								<td><input type="button" class="moveRow" value="Down"
																									onclick="hm.util.moveRowsDown('policyTable');" /></td>
																							</tr>
																						</s:if>
																					</table>
																				</td>
																			</s:if>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
														</fieldset>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									</s:if>
									<tr><td height="10px"></td></tr>
									<!-- Advance setting -->
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.userprofile.advanced" />','adv');</script></td>
									</tr>
									<tr>
										<td style="padding-left: 5px;">
											<div id="adv" style="display: <s:property value="%{dataSource.advSettingDisplayStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="184px"><s:text name="config.userprofile.attributeGroup" /></td>
																<td>
																	<ah:createOrSelect divId="errorAttribute" swidth="150px"
																		list="userAttributeList" typeString="UserAttribute"
																		selectIdName="myAttriSelect" stitle="config.userprofile.attributeGroup.tooltip"
																		inputValueName="inputAttriValue" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
											</div>
										</td>
									</tr>
									</s:if>
								</table>
								</fieldset>
							</td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<s:if test="%{jsonMode == true}">
		</div>
	</s:if>
</s:form></div>

<s:if test="%{jsonMode == true}">
<script type="text/javascript">
	function judgeFoldingIcon4UserProfile() {
		onLoadPage();
		<s:if test="%{contentShownInSubDrawer == true}">
			<s:if test="%{saveJsonModePermit}">
				showHideNetworkPolicySubSaveBT(true);
			</s:if>
			<s:else>
				showHideNetworkPolicySubSaveBT(false);
			</s:else>
		</s:if>
		adjustFoldingIcon('greVpnTunnels');
		adjustFoldingIcon('firewalls');
		adjustFoldingIcon('qosSettings');
		adjustFoldingIcon('schedules');
		adjustFoldingIcon('sla');
		adjustFoldingIcon('cliCla');
		adjustFoldingIcon('adv');

	}

	setTimeout("judgeFoldingIcon4UserProfile()", 100);
</script>
</s:if>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
	<script>
		setCurrentHelpLinkUrl('<s:property value="helpLink" />');
	</script>
</s:if>