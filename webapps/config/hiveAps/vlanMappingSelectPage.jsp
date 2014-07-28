<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'vlanMappingSelectPage';

function finishSelectVlanMappingOK(){
	if(validate("finishSelectVlanMappingOK")){
		var url =  "<s:url action='networkPolicy' includeParams='none' />";
		var edittingFormName = 'vlanMappingSelectPage';
		url = url + "?ignore=" + new Date().getTime();
		document.forms[edittingFormName].operation.value = "finishSelectVlanMappingOK";
		YAHOO.util.Connect.setForm(document.getElementById(edittingFormName));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFinishSelectVlanMappingOK, failure : resultDoNothing, timeout: 60000}, null);
	}
	
}

var succFinishSelectVlanMappingOK = function (o){
	eval("var details = " + o.responseText);
	if (details.t) {
		hideSubDialogOverlay();
		refreshNetworkObjPage();
	}else {
		if (details.m){
			hm.util.show("errNote");
			Get("errNote").className="noteError";
			Get("errNote").innerHTML=details.m;
			var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
		}
	}
};

function submitAction(operation) {
	if (validate(operation)) {
		if (operation =="newVlanMapping" || operation =="editVlanMapping") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editVlanMapping'){
				url = url + "&vlanMappingId=" + document.forms[formName].vlanMappingIdSelect.value + "&parentDomID=" + "vlanMappingIdSelect";
			} else if (operation == 'newVlanMapping'){
				url = url + "&parentDomID=" + "vlanMappingIdSelect";
			}
			openIFrameDialog(800,450, url);
		} 
	}
}

function validate(operation){
	if(operation == "editVlanMapping"){
		var value = hm.util.validateListSelection("vlanMappingIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanMappingId.value = value;
		}
	}
	if (operation == 'newVlanMapping' ||
		operation == 'editVlanMapping') {
		return true;
	}
	if (!validateVLAN(true)){
		return false;
	}

	return true;
}

function validateVLAN(flag){
	var vlannames = document.getElementById("vlanMappingIdSelect");
	var vlanValue = document.forms[formName].inputVlanMappingIdValue;
	if (flag){
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlanMap.vlan" /></s:param></s:text>');
		    vlanValue.focus();
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlanMap.vlan" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), message);
	        vlanValue.focus();
	        return false;
	    }
	    document.forms[formName].vlanMappingId.value = -1;
	} else {
		document.forms[formName].vlanMappingId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}


</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="vlanMappingSelectPage" id="vlanMappingSelectPage">
<s:hidden name="operation" />
<s:hidden name="vlanMappingId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><div id="errNote" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td class="labelT1" width="40px" nowrap="nowrap"><s:text
							name="config.configTemplate.vlanMap.vlan" /></td>
						<td>
							<ah:createOrSelect divId="errorDisplayVlan" list="vlanUserAddList" typeString="VlanMapping" 
								selectIdName="vlanMappingIdSelect" inputValueName="inputVlanMappingIdValue" swidth="140px" />
						</td>
					</tr>
					<!-- tr>
						<td>
							<ah:checkList name="selectVlanNetworkVlanIds" multiple="true" width="100%" itemWidth="175px" list="vlanUserAddList" listKey="id" listValue="value" value="selectVlanNetworkVlanIds" editEvent="editVlanForMappingDialog"/>
						</td>
					</tr-->
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td align="center" width="100%" colspan="2">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="finishSelectVlanMappingOK();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
									<td width="40px">&nbsp;</td>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="hideSubDialogOverlay();" title="<s:text name="config.mgt.vlan.dialog.cancel"/>"><span><s:text name="config.mgt.vlan.dialog.cancel"/></span></a></td>
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
