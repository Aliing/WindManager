<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.HmAccessControl"%>

<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<script>
var formName = 'hmAccess';
var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

var thisOperation;
function submitAction(operation){
    thisOperation = operation;
    if (operation == 'remove') 
    {
        hm.util.checkAndConfirmDelete();
    } 
    else 
    {
        doContinueOper();
    }   
}

function doContinueOper(){
	if (validate(thisOperation)) 
	{
	    showProcessing();
	    document.forms[formName].operation.value = thisOperation;
	    hm.options.selectAllOptions("allowedIps");
	    hm.options.selectAllOptions("deniedIps");
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	var typeCbs = document.getElementsByName("dataSource.controlType");
	for(var i=0; i<typeCbs.length; i++){
		var typeCb = typeCbs[i];
		if(typeCb.checked && typeCb.value == <%=HmAccessControl.CONTROL_TYPE_PERMIT%>){
			if(Get("allowedIps").length < 1){
				hm.util.reportFieldError(Get("allowedIps"), '<s:text name="error.pleaseAddItems"></s:text>');
				showAllowCreateSection();
				return false;
			}
		}
	}
	return true;
}

function controlTypeChanged(cb){
	if(cb.value == <%=HmAccessControl.CONTROL_TYPE_DENY%>){
		Get("allowSection").style.display = "none";
		Get("denySection").style.display = "";
		Get("allowLabel").style.display = "none";
		Get("denyLabel").style.display = "";
	}else{
		Get("allowSection").style.display = "";
		Get("denySection").style.display = "none";
		Get("allowLabel").style.display = "";
		Get("denyLabel").style.display = "none";
	}
}

function showAllowCreateSection(){
	hm.util.show('createAllowButton');
	hm.util.show('allowCreateSection');
	hm.util.hide('newAllowButton');
	var ip = document.getElementById("allowIp");
	if(ip){
		ip.focus();
	}
}

function hideAllowCreateSection(){
	hm.util.hide('createAllowButton');
	hm.util.hide('allowCreateSection');
	hm.util.show('newAllowButton');
}

function showDenyCreateSection(){
	hm.util.show('createDenyButton');
	hm.util.show('denyCreateSection');
	hm.util.hide('newDenyButton');
	var ip = document.getElementById("denyIp");
	if(ip){
		ip.focus();
	}
}

function hideDenyCreateSection(){
	hm.util.hide('createDenyButton');
	hm.util.hide('denyCreateSection');
	hm.util.show('newDenyButton');
}

function removeAllowIpAddress(){
	removeIpAddress("allowedIps");
}

function removeDenyIpAddress(){
	removeIpAddress("deniedIps");
}

function addAllowIpAddress(){
	addIpAddress("allowIp","allowMask","allowedIps");
}

function addDenyIpAddress(){
	addIpAddress("denyIp","denyMask","deniedIps");
}

function removeIpAddress(ipList){
	var listEl = Get(ipList);
	if(listEl.length == 0){
		return;
	}
	var items = listEl.options;
	var anySelected = false;
	for(var i=items.length; i>0; i--){
		var item = items[i-1];
		if(item.selected){
			anySelected = true;
			listEl.remove(i-1);
		}
	}
	if(!anySelected){
		hm.util.reportFieldError(listEl, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return;
	}
}

function addIpAddress(ipAddress, netmask, ipList){
	if(!validateIp(ipAddress)||!validateNetmask(netmask)){
		return;
	}
	var ipEl = Get(ipAddress);
	var maskEl = Get(netmask);
	var listEl = Get(ipList);
	var value = ipEl.value + "/" + maskEl.value;
	if(!validateExisted(listEl, value, ipEl)){
		return;
	}
	var option = new Option(value,value);
	try{
		listEl.add(option, null);
	}catch(e){
		listEl.add(option);
	}
	ipEl.value = maskEl.value = "";
	ipEl.focus();
}

function validateExisted(list, value, dispEl){
	var listEl = Get(list);
	if(listEl && value){
		var items = listEl.options;
		for(var i=0; i<items.length; i++){
			var item = items[i].text;
			if(item == value){
				hm.util.reportFieldError(dispEl, '<s:text name="error.addObjectExists"></s:text>');
				return false;
			}
		}
	}
	return true;
}

function validateIp(ipEl){
	var ipAddressEl = Get(ipEl);
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.access.control.ip" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.access.control.ip" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validateNetmask(maskEl){
	var netmaskEl = Get(maskEl);
	if (netmaskEl.value.length == 0) {
		netmaskEl.value = "255.255.255.255";
  	}
	if (! hm.util.validateMask(netmaskEl.value)) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.access.control.mask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
	}
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="hmAccess">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="update" value="Update"
						class="button" onClick="submitAction('update');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0"
				width="650px">
				<tr>
					<td><!-- access control type -->
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="labelT1" width="145px"><s:text
										name="hm.access.control.type"></s:text></td>
									<td width="65px"><s:radio
										onclick="controlTypeChanged(this);" label="Gender"
										name="dataSource.controlType" list="%{controlType1}"
										listKey="key" listValue="value" /></td>
									<td><s:radio onclick="controlTypeChanged(this);"
										label="Gender" name="dataSource.controlType"
										list="%{controlType2}" listKey="key" listValue="value" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
						</tr>
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr id="allowLabel"
									style="display:<s:property value="%{allowStyle}"/>">
									<td width="155px"></td>
									<td style="padding-left: 4px;" colspan="10" class="noteInfo"><s:text
										name="hm.access.control.allow.note" /></td>
								</tr>
								<tr id="denyLabel"
									style="display:<s:property value="%{denyStyle}"/>">
									<td width="155px"></td>
									<td style="padding-left: 4px;" colspan="10" class="noteInfo"><s:text
										name="hm.access.control.deny.note" /></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
				<tr id="allowSection"
					style="display:<s:property value="%{allowStyle}"/>">
					<td><!-- allowed access -->
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="hm.access.control.allow"></s:text></td>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr id="newAllowButton">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="80px"><input type="button" name="ignore"
												value="Add" class="button"
												onClick="showAllowCreateSection();"
												<s:property value="writeDisabled" />></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button" onClick="removeAllowIpAddress();"
												<s:property value="writeDisabled" />></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display: none;" id="createAllowButton">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="80px"><input type="button" name="ignore"
												value="Apply" class="button" onClick="addAllowIpAddress();">
											</td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideAllowCreateSection();">
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display: none;" id="allowCreateSection">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td style="padding-left: 0;" class="labelT1 listHead" width="70px"><s:text
												name="hm.access.control.ip"></s:text></td>
											<td class="listHead"><s:textfield id="allowIp" size="18" maxlength="18"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>

											<td class="labelT1 listHead" width="60px"><s:text
												name="hm.access.control.mask"></s:text></td>
											<td class="listHead"><s:textfield id="allowMask" size="18" maxlength="18"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td height="5px"></td>
								</tr>
								<tr>
									<td colspan="2"><s:select multiple="true" size="12"
										id="allowedIps" name="allowedIps" list="%{allowedIps}"
										cssStyle="width: 220px;" /></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr id="denySection"
					style="display:<s:property value="%{denyStyle}"/>">
					<td><!-- denied access -->
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="hm.access.control.deny"></s:text></td>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr id="newDenyButton">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="80px"><input type="button" name="ignore"
												value="Add" class="button"
												onClick="showDenyCreateSection();"
												<s:property value="writeDisabled" />></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button" onClick="removeDenyIpAddress();"
												<s:property value="writeDisabled" />></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display: none;" id="createDenyButton">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="80px"><input type="button" name="ignore"
												value="Apply" class="button" onClick="addDenyIpAddress();">
											</td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideDenyCreateSection();"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display: none;" id="denyCreateSection">
									<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td style="padding-left: 0;" class="labelT1 listHead" width="70px"><s:text
												name="hm.access.control.ip"></s:text></td>
											<td class="listHead"><s:textfield id="denyIp" size="18" maxlength="18"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>

											<td class="labelT1 listHead" width="60px"><s:text
												name="hm.access.control.mask"></s:text></td>
											<td class="listHead"><s:textfield id="denyMask" size="18" maxlength="18"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td height="5px"></td>
								</tr>
								<tr>
									<td colspan="2"><s:select multiple="true" size="12"
										id="deniedIps" name="deniedIps" list="%{deniedIps}"
										cssStyle="width: 220px;" /></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td><!-- deny behavior -->
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="hm.access.control.behavior"></s:text></td>
							<td><s:select name="dataSource.denyBehavior"
								list="%{denyBehaviors}" listKey="key" listValue="value"
								cssStyle="width: 220px;"></s:select></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
