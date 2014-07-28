<%@taglib prefix="s" uri="/struts-tags"%>
<script type="text/javaScript">

function validateConfigTemplate(operation){
	if(operation == 'create2' || operation == 'update2'){
		var inputElement = document.getElementById(formName + "_configTemplate");
		if(!inputElement){
			return true;
		}
		if(inputElement.value <= 0){
		    hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.template" /></s:param></s:text>');
		    inputElement.focus();
		    return false;
		}
	}
	return true;
}

function generateRealmName(){
	var topologyMapId = document.getElementById(formName + "_topology").value;
	var templateId = document.getElementById(formName + "_configTemplate").value;
	var lockRealmNameEl = document.getElementById(formName + "_dataSource_lockRealmName");
	var realmNameValue = document.getElementById(formName + "_dataSource_realmName").value;
	if(!lockRealmNameEl.checked){
		var url = "<s:url action='hiveAp' includeParams='none' />?operation=generateRealmName"
			+ "&configTemplate=" + templateId
			+"&topologyMapId="+topologyMapId
			+"&oldRealmName="+realmNameValue
			+"&lockRealmName="+lockRealmNameEl.checked
			+"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : setRealmName}, null);
	}
}

var setRealmName =  function (o){
	eval("var details = " + o.responseText);
	var strRealmName = details.realmName;
	var realmName = document.getElementById(formName+"_dataSource_realmName");
	if(realmName){
		realmName.value = strRealmName;
	}
	if(details.tmplateId){
		document.getElementById(formName+"_dataSource_portTemplate").value=details.tmplateId;
	}
	//changeNetworkPolicy();
	
}

function deviceFuncChange(deviceType){
	$("#" + formName + "_oldDeviceType").attr("value", $("#hiveApDeviceTypeValue").val());
	
	var cancelBtn = function(){
		$("#" + formName + "_dataSource_deviceType").attr("value", $("#hiveApDeviceTypeValue").val());
		this.hide();
	};
	
	var mybuttons = [ { text:"OK", handler: function(){this.hide();deviceFuncChangeContinueOper();} }, 
                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
    var deviceTypeChangeMsg = "<html><body>"+'<s:text name ="warning.hiveAp.deviceType.changed" />' +"</body></html>";
    var dlg = userDefinedConfirmDialog(deviceTypeChangeMsg, mybuttons, "Warning");
    dlg.show();
}

function deviceFuncChangeContinueOper() {
	<s:if test="%{jsonMode}">
		var url = '<s:url action="hiveAp" includeParams="none"></s:url>' + "?operation=deviceModeChange&ignore="+new Date().getTime();
		document.forms[formName].jsonMode.value = true;
		YAHOO.util.Connect.setForm(Get(formName));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succNewHiveAp}, null);
	</s:if>
	<s:else>
		submitAction("deviceModeChange");
	</s:else>
}

</script>
<div>
	<%-- add this password dummy to fix issue with auto complete function --%>
	<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password" />
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="2px"></td>
		</tr>
		<tr>
			<td class="labelT1" width="150px"><s:text
				name="hiveAp.hostName" /><font color="red"><s:text
				name="*" /></font></td>
			<td width="200px"><s:textfield name="dataSource.hostName" size="24"
				title="%{hostnameRange}"
				onkeypress="return hm.util.keyPressPermit(event,'name');"
				maxlength="%{hostNameLength}" /></td>
			<td class="labelT1" width="120px"><s:text name="hiveAp.model" /></td>
			<td><s:property value="dataSource.deviceModelName"/></td>
		</tr>
		<tr>
			<td class="labelT1"><s:text name="hiveAp.macaddress" /><font
				color="red"><s:text name="*" /></font></td>
			<td><s:textfield name="dataSource.macAddress" size="24"
				onkeypress="return hm.util.keyPressPermit(event,'hex');"
				maxlength="%{macAddressLength}" disabled="%{disabledName}" /></td>
			<td class="labelT1"><s:text name="hiveAp.device.type" /></td>
			<td>
				<s:hidden id="hiveApDeviceTypeValue" value="%{dataSource.deviceType}" />
				<s:select name="dataSource.deviceType"
					value="%{dataSource.deviceType}" list="%{dataSource.deviceInfo.deviceTypeEnum}"
					listKey="key" listValue="value" cssStyle="width: 198px;"
					onchange="deviceFuncChange(this.value); "
					disabled="%{deviceTypeDisabled}"  />
			</td>
		</tr>
		<tr>
			<td class="labelT1"><s:text name="hiveAp.location" /></td>
			<td><s:textfield name="dataSource.location" size="24"
				maxlength="32" disabled="%{isBr100}" 
				onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
				title="%{locationRange}" /></td>
			<td class="labelT1"><s:text name="hiveAp.topology" /></td>
			<td><s:select list="%{topologys}" listKey="id"
				listValue="value" name="topology" cssStyle="width: 198px;" 
				onchange="generateRealmName();"/></td>
		</tr>
		<tr style="display: <s:property value='mucDisplay'/>">
			<td><s:checkbox name="dataSource.manageUponContact"
				value="%{dataSource.manageUponContact}" /></td>
			<td style="padding-left: 2px"><s:text
				name="hiveAp.manageUponContact" /></td>
		</tr>
		<s:if test="%{dataSource.deviceInfo.cvgAsL3Vpn}">
			<tr id="cvgNetworkTr">
				<td class="labelT1"><s:text name="hiveAp.cvg.mgt.network"/><font color="red"><s:text name="*" /></font></td>
				<td><s:select list="%{cvgMgtNetworkList}" listKey="id" listValue="value"
					 name="cvgMgtNetwork" cssStyle="width: 198px;"/></td>
				<td class="labelT1"><s:text name="hiveAp.cvg.mgt.vlan"/><font color="red"><s:text name="*" /></font></td>
				<td><s:select list="%{cvgMgtVlanList}" listKey="id" listValue="value"
					 name="cvgMgtVlan" cssStyle="width: 198px;"/></td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.cvg.vpnService"/></td>
				<td><s:property value="dataSource.vpnServerName" /></td>
			</tr>
		</s:if>
		<s:else>
			<tr id="networkPolicyTr" style="display: <s:property value="%{configTemplateStyle}"/>">
				<td class="labelT1"><s:text name="hiveAp.template" /></td>
				<td><s:select
					list="%{configTemplates}" listKey="id" listValue="value"
					name="configTemplate" cssStyle="width: 198px;" 
					onchange="requestTemplateInfo(this);generateRealmName();" />
				</td>

				<td class="labelT1"><s:text name="hiveAp.port.template" /></td>
				<td><s:textfield name="dataSource.portTemplate" readonly="true" size="24"/>
				</td>
			</tr>
		</s:else>
		
	</table>
</div>