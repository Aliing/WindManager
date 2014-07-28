<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.HmStartConfig"%>
<%@page import="com.ah.be.parameter.BeParaModule"%>

<script>
<s:if test="%{hideTopPannel}">
var formName = 'startHere';
</s:if>
<s:else>
var formName = 'startHereMgr';
</s:else>
var configuredType;
var thisOperation;
var originalType;
var defaultAccessMode;
var currentAccessMode;
var defaultAuthorizedTime;
var confirmMessage;
var userNameForCheck;

function onLoadPage() {
	configuredType = "<s:property value="dataSource.modeType" />";
	originalType = "<s:property value="dataSource.modeType" />";
	<s:if test="%{!hideAccessPannel}">
	initAccessStatus();
	</s:if>
	<s:if test="%{null != userContext.getCustomerId() && '' != userContext.getCustomerId()}">
		userNameForCheck = '<s:property value="%{userContext.getUserFullName()}"/>';
	</s:if>
	<s:else>
		userNameForCheck = '<s:property value="%{userContext.getUserName()}"/>';
	</s:else>
}
function initAccessStatus()
{
	defaultAccessMode="<s:property value="defaultAccessMode" />";
	defaultAuthorizedTime="<s:property value="defaultAuthorizedTime" />";
	currentAccessMode=defaultAccessMode;
	switch(parseInt(defaultAccessMode))
	{
	case 1:"";
	case 2:document.getElementById("authorizedTime"+defaultAccessMode).value=defaultAuthorizedTime;
			document.getElementById("remainingTimeDetails").style.display="";break;
	default:document.getElementById("remainingTimeDetails").style.display="none";
	}
	changeAccessMode(defaultAccessMode);
}
function validateAuthorizedTime(accessMode)
{
	if(parseInt(accessMode)<1||parseInt(accessMode)>2)
		{
		 return true;
		}
	var authorizedTime =document.getElementById("authorizedTime"+accessMode);
	
		 if(authorizedTime.value<=0||authorizedTime.value>48)
			 
		 {
			 hm.util.reportFieldError(authorizedTime, '<s:text name="error.access.hours.invalid"><s:param>'+authorizedTime.value+'</s:param></s:text>');
			 authorizedTime.value="";
			 authorizedTime.focus();
			 return false;
		 }
	return true;
}
function changeAccessMode(accessMode)
{
	currentAccessMode=accessMode;
	 var authorizedTime1=document.getElementById("authorizedTime1");
	 var authorizedTime2=document.getElementById("authorizedTime2");
	 
	 if(accessMode=='1')
   		{
	   		if(accessMode==defaultAccessMode)
			{
	   			authorizedTime1.value=defaultAuthorizedTime;
			}
	   		else{
	   			authorizedTime1.value=24;
	   		}
	   		authorizedTime1.disabled=false;
	   		authorizedTime2.disabled=true;
	   		authorizedTime2.value="";
   		}
   	else if(accessMode=="2")
   		{
   			if(accessMode==defaultAccessMode)
			{
   				authorizedTime2.value=defaultAuthorizedTime;
			}
   			else{
   				authorizedTime2.value=24;
   			}
   			authorizedTime2.disabled=false;
   			authorizedTime1.disabled=true;
   			authorizedTime1.value="";
   		}
   	else{
   		authorizedTime1.value="";
   		authorizedTime2.value="";
   		authorizedTime1.disabled=true;
   		authorizedTime2.disabled=true;
   		
   		}
}

function isAccessStatusChanged()
{
	 
	if(currentAccessMode!=defaultAccessMode)
		{
		return true;
		}
	else if(defaultAccessMode=="1"||defaultAccessMode=="2")
    	{
			
		    var currentAuthorizedTime=document.getElementById("authorizedTime"+currentAccessMode).value;
		    if(currentAuthorizedTime!=defaultAuthorizedTime)
		    	{
		    		return true;
		    	}
    		
    	}
	return false;
	
}

function showEndDateConfirmDialog()
{	
	var configureTime=document.getElementById("authorizedTime"+currentAccessMode).value;
	
	url = "<s:url action='startHereMgr' includeParams='none' />" + "?operation=getEstamiteEndDate&leftHours="+encodeURIComponent(configureTime);

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: callBack,timeout: 5000}, null);

}
function callBack(obj)
{
	eval("var result = " + obj.responseText);
	var endTime;
	if(result.endDate)
	{
		endTime= result.endDate;
		switch(parseInt(currentAccessMode))
		{
		case 1:confirmMessage=confirmMessage+'<s:text name="info.home.start.here.accessOption.confirm.1"/> '+endTime;showConfirmDialog(confirmMessage);break;
		case 2:confirmMessage=confirmMessage+'<s:text name="info.home.start.here.accessOption.confirm.2"/> '+endTime;showConfirmDialog(confirmMessage);break;
		}
		
	}
	
}
function showConfirmDialog(message)
{
	if(!message||message=='')
		{
			doContinueOper();
		}
	else{
			confirmDialog.cfg.setProperty('text',message);
			confirmDialog.show();	
	}
}
function confirmSubmit(message)
{
	if(isAccessStatusChanged())
	{
		switch(parseInt(currentAccessMode))
		{
		case 1:confirmMessage=message;showEndDateConfirmDialog();break;
		case 2:confirmMessage=message;showEndDateConfirmDialog();break;
		case 3:confirmMessage=message+'<s:text name="info.home.start.here.accessOption.confirm.3"/> ';showConfirmDialog(confirmMessage);break;
		case 4:confirmMessage=message+'<s:text name="info.home.start.here.accessOption.confirm.4"/> ';showConfirmDialog(confirmMessage);break;
		case 0:confirmMessage=message+'<s:text name="info.home.start.here.accessOption.confirm.0"/> ';showConfirmDialog(confirmMessage);break;
		}
		document.forms[formName].accessChanged.value = "true";
	}
	else{
			showConfirmDialog(message);
			document.forms[formName].accessChanged.value = "false";
	}
	
}

function submitAction(operation) {
	thisOperation = operation;
	if (validate()){
		<s:if test="%{!hideAccessPannel}">
			var message='';
			if(originalType != configuredType)
			{
				message='<s:text name="info.home.start.here.mode.change.disable.routing"/><br><br> ';
			}
			confirmSubmit(message);
		</s:if>
		<s:else>
			if (originalType != configuredType) {
				confirmDialog.cfg.setProperty('text', '<s:text name="info.home.start.here.mode.change.disable.routing"/>');
				confirmDialog.show();
			} else {
				doContinueOper();
			}
		</s:else>
	
		
	}
}

function doContinueOper() {
	showProcessing();
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function validate(){
	if(!validateHiveApPassword()){
		return false;
	}
	<s:if test="%{hideTopPannel}">
		if(!validateHivemanagerPassword()){
			return false;
		}
	</s:if>

	if ("<%=HmStartConfig.HM_MODE_EASY%>" == configuredType) {
		if(!validateNetworkName()){
			return false;
		}
		if(!validateNtpServer()){
			return false;
		}
		if (!validateDnsServer()) {
			return false;
		}
		if(document.getElementById(formName + "_dataSource_useAccessConsole").checked && !validateKeyValue()){
			return false;
		}
	} else if (document.getElementById("quickStartPwdTr").style.display == "") {
		// quick start Password
		return validateQuickStartPassword();
	}
	<s:if test="%{!hideAccessPannel}">
	if(isAccessStatusChanged())
		{
		if(!validateAuthorizedTime(currentAccessMode))
			{
			return false;
			}
		}
	</s:if>
	return true;
}

function validateRadioSettings(){
	var configuredType = "<s:property value="dataSource.modeType" />";
	var dataSourceId = "<s:property value="dataSource.id" />"

	var radioTypes = document.getElementsByName("dataSource.modeType");

	var valueChecked = false;
	for(var i=0; i<radioTypes.length; i++){
		if(radioTypes[i].checked){
			valueChecked = true;
			break;
		}
	}
	if(!valueChecked){
        hm.util.reportFieldError(radioTypes[0], '<s:text name="error.requiredField"><s:param><s:text name="hm.config.start.setting.label" /></s:param></s:text>');
        return false;
	}
	return true;
}

function validateNetworkName(){
	var networkEl = document.getElementById(formName + "_dataSource_networkName");
	if(networkEl.value.length > 0){
		var message = hm.util.validateName(networkEl.value, '<s:text name="hm.config.start.network" />');
    	if (message != null) {
    		hm.util.reportFieldError(networkEl, message);
    		networkEl.focus();
        	return false;
    	}
    	if ("<%=BeParaModule.DEFAULT_HIVEID_PROFILE_NAME%>" == networkEl.value || "<%=BeParaModule.DEFAULT_DEVICE_GROUP_NAME%>"
    	    == networkEl.value || "<%=BeParaModule.DEFAULT_SERVICE_ALG_NAME%>" == networkEl.value) {
    		hm.util.reportFieldError(networkEl, '<s:text name="error.objectExists"><s:param>'+networkEl.value+'</s:param></s:text>');
    		networkEl.focus();
        	return false;
    	}
	} else {
		hm.util.reportFieldError(networkEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.config.start.network" /></s:param></s:text>');
		networkEl.focus();
    	return false;
	}
	return true;
}

function validateNtpServer(){
	var ntpServerEl = document.getElementById(formName + "_ntpServer");
	if(ntpServerEl.value.length > 0){
		var message = hm.util.validateName(ntpServerEl.value, '<s:text name="hm.config.start.ntp" />');
    	if (message != null) {
    		hm.util.reportFieldError(ntpServerEl, message);
    		ntpServerEl.focus();
        	return false;
    	}
	}
	return true;
}

function validateDnsServer(){
	var dnsServerEl = document.getElementById(formName + "_dnsSer1");
	var dnsServerE2 = document.getElementById(formName + "_dnsSer2");
	if(dnsServerEl.value.length > 0){
    	if (!hm.util.validateIpAddress(dnsServerEl.value)) {
    		hm.util.reportFieldError(dnsServerEl, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.config.start.dns.primary"/></s:param></s:text>');
			dnsServerEl.focus();
			return false;
    	}
    	if(dnsServerE2.value.length > 0){
	    	if (!hm.util.validateIpAddress(dnsServerE2.value)) {
	    		hm.util.reportFieldError(dnsServerE2, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.config.start.dns.secondary"/></s:param></s:text>');
				dnsServerE2.focus();
				return false;
	    	}
	    	if (dnsServerEl.value == dnsServerE2.value) {
	    		hm.util.reportFieldError(dnsServerE2, '<s:text name="error.equal"><s:param><s:text name="hm.config.start.dns.primary"/></s:param><s:param><s:text name="hm.config.start.dns.secondary"/></s:param></s:text>');
				dnsServerE2.focus();
				return false;
	    	}
		}
	} else if (dnsServerE2.value.length > 0) {
		hm.util.reportFieldError(dnsServerEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.config.start.dns.primary" /></s:param></s:text>');
		dnsServerEl.focus();
    	return false;
	}
	return true;
}

function validateHivemanagerPassword(){
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay1").checked){
       passwordElement = document.getElementById("adminPassword");
       confirmElement = document.getElementById("cfAdminPassword");
    }else{
       passwordElement = document.getElementById("adminPassword_text");
       confirmElement = document.getElementById("cfAdminPassword_text");
    }
    if(passwordElement.value.length > 0 || confirmElement.value.length > 0){
    	return hm.util.validateUserNewPasswordFormat(passwordElement, confirmElement, '<s:text name="hm.config.start.hivemanager.password" />',
    			'<s:text name="hm.config.start.hivemanager.password.confirm" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />', userNameForCheck);
    }
    return true;
}

function validateHiveApPassword(){
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay").checked){
       passwordElement = document.getElementById("hiveApPassword");
       confirmElement = document.getElementById("cfHiveApPassword");
    }else{
       passwordElement = document.getElementById("hiveApPassword_text");
       confirmElement = document.getElementById("cfHiveApPassword_text");
    }
    //bug 24562 fix
    return hm.util.validateUserNewPasswordFormat(passwordElement, confirmElement, '<s:text name="hm.config.start.hiveAp.password" />',
    			'<s:text name="hm.config.start.hiveAp.password.confirm" />', 8, '<s:text name="hm.config.start.hiveAp.password.note" />', userNameForCheck);
}

function validateQuickStartPassword(){
	var passwordElement;
	var confirmElement;
    if (document.getElementById("chkToggleDisplay3").checked){
       passwordElement = document.getElementById("quickPassword");
       confirmElement = document.getElementById("cfQuickPassword");
    }else{
       passwordElement = document.getElementById("quickPassword_text");
       confirmElement = document.getElementById("cfQuickPassword_text");
    }
    return hm.util.validateOptionNewPasswordLength(passwordElement, confirmElement, '<s:text name="hm.missionux.wecomle.update.preshared.key.title.default" />',
    			'<s:text name="hm.config.start.hivemanager.password.confirm" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />');
}

function validateKeyValue() {
	var keyElement;
    var confirmElement;
    if (document.getElementById("chkToggleDisplay2").checked) {
    	keyElement = document.getElementById("keyValue");
    	confirmElement = document.getElementById("confirmKeyValue");
    } else {
    	keyElement = document.getElementById("keyValue_text");
    	confirmElement = document.getElementById("confirmKeyValue_text");
    }
    
    return hm.util.validateOptionNewPasswordLength(keyElement, confirmElement, '<s:text name="hm.config.start.access.console.password" />',
    			'<s:text name="config.access.console.key.confirm" />', 8, '<s:text name="config.ssid.keyValue_range" />');
}


function selectExpress(checked) {
	document.getElementById("expDetail").style.display=checked?"":"none";
	configuredType = checked ? "<%=HmStartConfig.HM_MODE_EASY%>" : "<%=HmStartConfig.HM_MODE_FULL%>";
	document.getElementById("quickStartPwdTr").style.display = checked ? "none" : "";
	document.getElementById("quickStartPwdConfirmTr").style.display = checked ? "none" : "";
	document.getElementById("quickStartPwdNoteTr").style.display = checked ? "none" : "";
}

function showConsolePass(checked) {
	document.getElementById("consoleDetail").style.display=checked?"":"none";
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<s:if test="%{hideTopPannel}">
<div id="content" style="text-align: left;width: 680px;padding-top:10px;">
	<s:form action="startHere">
	<s:hidden name="operation" />
	<s:hidden name="accessChanged" />
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="startHereDetail" />
				</td>
			</tr>
		</table>
	</s:form>
</div>
</s:if>
<s:else>
<div id="content">
	<s:form action="startHereMgr">
	<s:hidden name="accessChanged" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" id="ignore" name="ignore" value="Update"
									class="button" onClick="submitAction('update');"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="startHereDetail" />
				</td>
			</tr>
		</table>
	</s:form>
</div>
</s:else>
