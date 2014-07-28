<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
var formName = 'mgtVlanDlg';

function saveVlanToNetWorkPolicy(operation) {
	if(validate(operation)){
		var url = "<s:url action='networkPolicy' includeParams='none' />"+ "?ignore="+new Date().getTime(); 
		 document.forms["mgtVlanDlg"].operation.value = operation;
		 YAHOO.util.Connect.setForm(document.forms["mgtVlanDlg"]);
		 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveMgtVlan, failure : failSaveMgtVlan, timeout: 60000}, null);
	}
}
	
var succSaveMgtVlan = function (o) {
	eval("var details = " + o.responseText);
	if (details.r) {
		Get("spanV_vlan").innerHTML=details.r1;
		Get("spanV_vlanNative").innerHTML=details.r2;
		hideMgtVlanDialogOverlay();
		fetchConfigTemplate2Page(true);
	} else{
		if (details.e) {
			alert(details.e);
		}
		return;
	}
}

var failSaveMgtVlan = function(o){
	
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation =="newVlanNative" || operation =="editVlanNative") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editVlanNative'){
				url = url + "&vlanNativeId=" + document.forms[formName].vlanNativeIdSelect.value + "&parentDomID=" + "vlanNativeIdSelect,vlanIdSelect";
			} else if (operation == 'newVlanNative'){
				url = url + "&parentDomID=" + "vlanNativeIdSelect,vlanIdSelect";
			}
			openIFrameDialog(820,450, url);
		} else if (operation =="newVlan" || operation =="editVlan") {
			var url = '<s:url action="networkPolicy" includeParams="none"/>' 
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&ignore=' + new Date().getTime();
			
			if (operation == 'editVlan'){
				url = url + "&vlanId=" + document.forms[formName].vlanIdSelect.value + "&parentDomID=" + "vlanIdSelect,vlanNativeIdSelect";
			} else if (operation == 'newVlan'){
				url = url + "&parentDomID=" + "vlanIdSelect,vlanNativeIdSelect";
			}
			openIFrameDialog(800,450, url);
		} 
	}
}

function validate(operation){
	if(operation == "editVlanNative"){
		var value = hm.util.validateListSelection("vlanNativeIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanNativeId.value = value;
		}
	}
	if(operation == "editVlan"){
		var value = hm.util.validateListSelection("vlanIdSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanId.value = value;
		}
	}
	if (operation == 'newVlan' ||
		operation == 'newVlanNative' ||
		operation == 'editVlan' ||
		operation == 'editVlanNative' ) {
		return true;
	}
	if (!validateVLAN(true)){
		return false;
	}
	
	if (!validateVLANNative(true)) {
		return false;
	}
	return true;
}

function validateVLAN(flag){
	var vlannames = document.getElementById("vlanIdSelect");
	var vlanValue = document.forms[formName].inputVlanIdValue;
	if (flag){
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlan" /></s:param></s:text>');
		    vlanValue.focus();
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlan" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlan"), message);
	        vlanValue.focus();
	        return false;
	    }
	    document.forms[formName].vlanId.value = -1;
	} else {
		document.forms[formName].vlanId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}

function validateVLANNative(flag){
	var vlannames = document.getElementById("vlanNativeIdSelect");
	var vlanValue = document.forms[formName].inputVlanNativeIdValue;
	if (flag) {
		if ("" == vlanValue.value) {
		    hm.util.reportFieldError(document.getElementById("errorDisplayVlanNative"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.configTemplate.vlanNative" /></s:param></s:text>');
		    vlanValue.focus();
			return false;
		}
	}
	if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, '<s:text name="config.configTemplate.vlanNative" />',1,4094);
	    if (message != null) {
	        hm.util.reportFieldError(document.getElementById("errorDisplayVlanNative"), message);
	        vlanValue.focus();
	        return false;
	    }
	    document.forms[formName].vlanNativeId.value = -1;
	} else {
		document.forms[formName].vlanNativeId.value = vlannames.options[vlannames.selectedIndex].value;
	}
	return true;
}
	
function prepareSaveBtPerrmit(){
<s:if test="%{savePermit==false}">
	Get("btSaveSetting").style.display="none";
</s:if>
<s:else>
	Get("btSaveSetting").style.display="";
</s:else>
}
window.setTimeout("prepareSaveBtPerrmit()", 100);
</script>

<div style="padding: 0px;" ><s:form action="networkPolicy" name="mgtVlanDlg" id="mgtVlanDlg">
	<s:hidden name="operation" />
	<s:hidden name="vlanId" />
	<s:hidden name="vlanNativeId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding-left: 10px;" align="left">
				<img class="dialogTitleImg" width="40px" height="40px"  src="images/hm_v2/profile/HM-icon-VPN_service.png">
				<span class="npcHead1">
					<s:text name="config.configTemplate.vlanSettingTitle" />
				</span>
			</td>
			<td align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton" ><a href="javascript:void(0);"
								class="btCurrent" onclick="hideMgtVlanDialogOverlay();"
								title="<s:text name="config.mgt.vlan.dialog.cancel"/>">
								<span><s:text name="config.mgt.vlan.dialog.cancel" /></span>
							</a>
						</td>
						<td width="20px">&nbsp;</td>
						<td class="npcButton" ><a href="javascript:void(0);" id="btSaveSetting"
								class="btCurrent" onClick="saveVlanToNetWorkPolicy('saveMgtVlan');"
								title="<s:text name="config.mgt.vlan.dialog.save"/>">
								<span><s:text name="config.mgt.vlan.dialog.save" />
								</span>
							</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="15px"/>
		</tr>
		<tr>
			<td colspan="2">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						
						<td class="labelT1" width="135px" nowrap="nowrap"><s:text
							name="config.configTemplate.vlan" /></td>
						<td width="180px">
							<ah:createOrSelect divId="errorDisplayVlan" list="list_vlan" typeString="Vlan" 
								selectIdName="vlanIdSelect" inputValueName="inputVlanIdValue" swidth="130px" />
						</td>
						<td style="padding-left: 20px; display:<s:property value="%{hideNativeVlan}"/>" class="labelT1" width="140px" nowrap="nowrap"><s:text
							name="config.configTemplate.vlanNative" /></td>
						<td style="display:<s:property value="%{hideNativeVlan}"/>">
							<ah:createOrSelect divId="errorDisplayVlanNative" list="list_vlan" typeString="VlanNative" 
								selectIdName="vlanNativeIdSelect" inputValueName="inputVlanNativeIdValue" swidth="130px"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="15px"/>
		</tr>
	</table>
</s:form>
</div>

