<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>
var formName = 'hiveApUpdate';
var mulselected = false;
function onLoadPage() {
	handleDdisplay();
	var selectedIds = document.getElementsByName("selectedIds");
	for (var i=0;i<selectedIds.length;i++) {
		YAHOO.util.Event.on(selectedIds[i], "click", function(e){
			handleDdisplay(); 
		});
	}
	showUnsupportedApsMessage();
}

function showUnsupportedApsMessage(){
	var unsupportedApsMessage  = "<s:property value='%{unsupportedApsMessage}' />" ;
	if(unsupportedApsMessage){
		showInfoDialog(unsupportedApsMessage);
	}
}

function handleDdisplay() {
	var selectedIds = document.getElementsByName("selectedIds");
	var count=0;
	for (var i=0;i<selectedIds.length;i++) {
		if(selectedIds[i].checked) {
			count= count+1;
		}
	}
	if (count > 1) {
		mulselected = true;
	} else {
		mulselected = false;
	}
	
	if (mulselected) {
		document.getElementById("hideStaticIp").style.display="none";
		document.getElementById("hideStaticGateway").style.display="none";
		
		var ipModes=document.getElementsByName("ipMode"); 
		for (var i =0;i<ipModes.length;i++) {
			if (ipModes[i].value == 2) {
				ipModes[i].disabled = true;
				ipModes[i].checked = false;
			} else {
				ipModes[i].checked = true;
			}
		}
	} else {
		var ipModes=document.getElementsByName("ipMode"); 
		for (var i =0;i<ipModes.length;i++) {
			if (ipModes[i].value == 2) {
				if (document.getElementById("enableNetdump").checked) {
					ipModes[i].disabled = false;
				} else {
					ipModes[i].disabled = true;
				}
				if (ipModes[i].checked) {
					document.getElementById("hideStaticIp").style.display="";
					document.getElementById("hideStaticGateway").style.display="";
				}
			}
		}
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(!validateApSelection(operation)){
		return false;
	}
	if(!validateNetdump(operation)){
		return false;
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'updateNetdump'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				break;
			}
		}
		if(!isSelected){
			var listElement = document.getElementById('checkAll');
			hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
			return false;
		}
	}
	return true;
}

function validateNetdump(operation){
	if(operation == 'updateNetdump'){
		var enableNDele = document.getElementById('enableNetdump');
		if(!enableNDele.checked){
			return true;
		}else{
			var netdumpEle = document.getElementById('netdumpServer');
			var netdumpVlan = document.getElementById('netdumpVlan');
			var netdumpNVlan = document.getElementById('netdumpNVlan');
			var netdumpDevice = document.getElementById('netdumpDevice');
			var netdumpGateway = document.getElementById('netdumpGateway');
			var ipModes=document.getElementsByName("ipMode"); 
			
			if(netdumpEle.value.length == 0){
				hm.util.reportFieldError(netdumpEle, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.bootstrap.netdump.server" /></s:param></s:text>');
				netdumpEle.focus();
	        	return false;
			}
			if (! hm.util.validateIpAddress(netdumpEle.value)) {
				hm.util.reportFieldError(netdumpEle, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.update.bootstrap.netdump.server" /></s:param></s:text>');
				netdumpEle.focus();
				return false;
			}
			
			if(!checkVlanId(netdumpVlan,'<s:text name="hiveAp.update.bootstrap.netdump.vlan" />')){
				 return false;
			}
			
			if(!checkVlanId(netdumpNVlan,'<s:text name="hiveAp.update.bootstrap.netdump.nvlan" />')){
				return false;
			}
			
			for (var i=0;i<ipModes.length;i++) {
				if (ipModes[i].value == 1 && ipModes[i].checked) {
					return true;
				}
			}
			
			//check device
			if(netdumpDevice.value.length == 0){
				hm.util.reportFieldError(netdumpDevice, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.bootstrap.netdump.device.ipAddress" /></s:param></s:text>');
	            netdumpDevice.focus();
	            return false;
			}
			if(netdumpDevice.value.indexOf("/") == -1){
				hm.util.reportFieldError(netdumpDevice, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.bootstrap.netdump.device.netmask" /></s:param></s:text>');
	            netdumpDevice.focus();
	            return false;
			}
			var ipStr = netdumpDevice.value.substring(0, netdumpDevice.value.indexOf("/"));
			var maskInt = netdumpDevice.value.substring(netdumpDevice.value.indexOf("/")+1);
			if(maskInt > 32){
				hm.util.reportFieldError(netdumpDevice, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.update.bootstrap.netdump.device.netmask" /></s:param></s:text>');
				netdumpDevice.focus();
				return false;
			}
			var maskStr = hm.util.intToStringNetMask(maskInt);
			if (! hm.util.validateIpAddress(ipStr)) {
				hm.util.reportFieldError(netdumpDevice, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.update.bootstrap.netdump.device.ipAddress" /></s:param></s:text>');
				netdumpDevice.focus();
				return false;
			}
			if(! hm.util.validateMask(maskStr)){
				hm.util.reportFieldError(netdumpDevice, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.update.bootstrap.netdump.device.netmask" /></s:param></s:text>');
				netdumpDevice.focus();
				return false;
			}
			
			//check gateway
			if (netdumpGateway.value.length != 0) {
				if (! hm.util.validateIpAddress(netdumpGateway.value)) {
					hm.util.reportFieldError(netdumpGateway, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.update.bootstrap.netdump.gateway" /></s:param></s:text>');
					netdumpGateway.focus();
					return false;
				}
				//check if they are in the same subnet
				//var message = hm.util.validateIpSubnet(ipStr, '<s:text name="hiveAp.ipAddress" />', netdumpGateway.value, '<s:text name="hiveAp.update.bootstrap.netdump.gateway" />', maskStr);
				//if(null != message){
				//	hm.util.reportFieldError(netdumpDevice, message);
				//	netdumpGateway.focus();
				//	return false;
				//}
			} else {
				hm.util.reportFieldError(netdumpGateway, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.bootstrap.netdump.gateway" /></s:param></s:text>');
				netdumpGateway.focus();
	        	return false;
			}
			
			return true;
		}
	}
	return true;
}

function checkVlanId(vlanId, title){
	if (vlanId.value.length == 0) {
		return true;
    } else if(vlanId.value.substring(0,1) == '0') {
		hm.util.reportFieldError(vlanId, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		vlanId.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(vlanId.value, title,1,4094);
    if (message != null) {
        hm.util.reportFieldError(vlanId, message);
        vlanId.focus();
        return false;
    }
	return true;
}

function toggleCheckAllHiveAps(cb){
	var cbs = document.getElementsByName('hiveApIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.netdump"/> </td>');
}

function selectedNetdump(checked)
{
	var ipModes=document.getElementsByName("ipMode"); 
	if (checked)
	{
		document.getElementById("netdumpServer").disabled = false;
		document.getElementById("netdumpVlan").disabled = false;
		document.getElementById("netdumpNVlan").disabled = false;
		document.getElementById("netdumpDevice").disabled = false;
		document.getElementById("netdumpGateway").disabled = false;
		for (var i =0;i<ipModes.length;i++) {
			if (ipModes[i].value == 2) {
				ipModes[i].disabled = false || mulselected;
			} else {
				ipModes[i].disabled = false;
			}
		}
	}else{
		document.getElementById("netdumpServer").disabled = true;
		document.getElementById("netdumpVlan").disabled = true;
		document.getElementById("netdumpNVlan").disabled = true;
		document.getElementById("netdumpDevice").disabled = true;
		document.getElementById("netdumpGateway").disabled = true;
		for (var i =0;i<ipModes.length;i++) {
			ipModes[i].disabled = true;
		}
	}
}

function changeIpMode(ipMode){
	if (ipMode == 1) {
		document.getElementById("hideStaticIp").style.display="none";
		document.getElementById("hideStaticGateway").style.display="none";
	} else {
		document.getElementById("hideStaticIp").style.display="";
		document.getElementById("hideStaticGateway").style.display="";
	}
}

</script>
<div id="content"><s:form action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('updateNetdump');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
						<s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="600px">
				<tr>
					<td style="padding: 4px 10px 10px 10px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
						<td>
							<div>
								<fieldset><legend><s:text name="hiveAp.update.bootstrap.netdump.tag"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td colspan="2" style="padding-left: 5px;">
											<s:checkbox name="enableNetdump" id="enableNetdump" onclick="selectedNetdump(this.checked);" />
											<s:text name="hiveAp.update.bootstrap.netdump.enable" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" width="250px" style="padding-left: 15px;">
											<s:text name="hiveAp.update.bootstrap.netdump.server" /><font color="red"><s:text name="*" /></font>
										</td>
										<td>
											<s:textfield name="netdumpServer" id="netdumpServer" disabled="true" size="24" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" width="250px" style="padding-left: 15px;">
											<s:text name="hiveAp.update.bootstrap.netdump.vlan" />
										</td>
										<td>
											<s:textfield name="netdumpVlan" id="netdumpVlan" disabled="true" size="24" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
												<s:text name="monitor.hiveAp.vlan.probe.vlanRange.note" />
										</td>
									</tr>
									<tr>
										<td class="labelT1" width="250px" style="padding-left: 15px;">
											<s:text name="hiveAp.update.bootstrap.netdump.nvlan" />
										</td>
										<td>
											<s:textfield name="netdumpNVlan" id="netdumpNVlan" disabled="true" size="24" maxlength="4"
												 onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
												 <s:text name="monitor.hiveAp.vlan.probe.vlanRange.note" />
										</td>
									</tr>
									<s:if test="%{showPort}">
										<tr>
											<td class="labelT1" width="250px" style="padding-left: 15px;">
												<s:text name="geneva_16.hiveAp.update.bootstrap.netdump.transport.settings"/>
												<a href="javascript:showInfoDialog('<s:text name="geneva_16.hiveAp.update.bootstrap.netdump.transport.tooltip"/>')"
												 class="marginBtn">?</a>
											</td>
											<td>
												<s:select id="transportEth" name="transportEth" cssStyle="width:80px" 
												 list="#{'eth1':'Eth1'}" listKey="key" listValue="value"></s:select>
												<font style="font-size: 18px;vertical-align: middle;">/</font>
												<s:select id="transportY" name="transportY" cssStyle="width:70px"
												 list="%{portList}" listKey="id" listValue="value" value="1"></s:select>
											</td>
										</tr>
									</s:if>
									<tr>
										<td class="labelT1" style="padding-left: 15px;"><s:radio label="Gender" id="ipMode"
													name="ipMode"
													disabled="true"
													list="#{1:'DHCP'}"
													onclick="changeIpMode(1);" /></td>
										<td><s:radio label="Gender" id="ipMode"
													name="ipMode"
													disabled="true"
													list="#{2:'Static'}"
													onclick="changeIpMode(2);" /></td>
									</tr>
									<tr id="hideStaticIp" style="display: none;">
										<td class="labelT1" width="250px" style="padding-left: 15px;" >
											<s:text name="hiveAp.update.bootstrap.netdump.device" /><font color="red"><s:text name="*" /></font>
										</td>
										<td>
											<s:textfield name="netdumpDevice" id="netdumpDevice" disabled="true" size="24" 
												maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ipMask');" />
												<s:text name="hiveAp.update.bootstrap.netdump.device.range" />
										</td>
									</tr>
									<tr id="hideStaticGateway" style="display: none;">
										<td class="labelT1" width="250px" style="padding-left: 15px;">
											<s:text name="hiveAp.update.bootstrap.netdump.gateway" /><font color="red"><s:text name="*" /></font>
										</td>
										<td>
											<s:textfield name="netdumpGateway" id="netdumpGateway" disabled="true" size="24" 
												maxlength="15" onkeypress="return hm.util.keyPressPermit(event,'ip');"/>
										</td>
									</tr>
									
									<tr>
										<td height="10"></td>
									</tr>
								</table>
								</fieldset>
							</div>
						</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);handleDdisplay();"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="softVer" key="monitor.hiveAp.sw" /></th>
										<th align="left"><ah:sort name="location" key="hiveAp.location" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck"><ah:checkItem /></td>
											<td class="list"><s:property value="hostName" /></td>
											<td class="list"><s:property value="macAddress" /></td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
											<td class="list"><s:property value="location" />&nbsp;</td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>