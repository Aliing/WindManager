<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'mgmtServiceSnmp';
var buttonShowing;
var displayErrorObj;
function onLoadPage() {	
    displayErrorObj = document.getElementById("checkAll");
    
	if (document.getElementById("mgmtName").disabled == false) {
			document.getElementById("mgmtName").focus();
	}
	var operation = "<s:property value="%{operation}"/>";
	buttonShowing = <s:property value="%{buttonShowing}"/>;
	if((operation == 'continue' || operation=='removeSnmp' || operation=='addSnmp'
		|| operation=='edit') && buttonShowing) {
	   showCreateSection();
	}
	if(operation=='new') {
		showCreateSection();
    }
	<s:if test="%{jsonMode}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(870,550);
	 	}
	</s:if>
}

function showCreateSection() {
    hm.util.hide('newButton');
	   hm.util.show('createButton');
	   hm.util.show('createSection');
	   // to fix column overlap issue on certain browsers
	   //var trh = document.getElementById('headerSection');
	   //var trc = document.getElementById('createSection');
	   //var table = trh.parentNode;	
	   //table.removeChild(trh);
	   //table.insertBefore(trh, trc);
	   document.getElementById("buttonShowing").value="true";
	   changeSnmpVersion(Get("snmpVersion").value);
}
function hideCreateSection() {
    hm.util.hide('createButton');
    hm.util.show('newButton');
    hm.util.hide('createSection');
	   document.getElementById("buttonShowing").value="false";
}

function saveSnmp(operation) {
	if (validate(operation)){
		var	url = "<s:url action='mgmtServiceSnmp' includeParams='none' />" + "?jsonMode=true" 
			+ "&ignore=" + new Date().getTime(); 
		if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mgmtServiceSnmp"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveSnmp, failure : failSave, timeout: 60000}, null);
	}
	
}

var succSaveSnmp = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create' &&
		    operation != 'addSnmp' &&
		    operation != 'removeSnmp') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}



function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MGMT_SERVICE_SNMP%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == 'newIpAddressSnmp') {
		return true;
	}

	if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>'){
       if (!validateName()) {
		   return false;
	   }
       
       if(!validateContact()){
    	   return false;
       }
    }
    if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update' || operation=='update<s:property value="lstForward"/>'){
    	 if(!validateContact()){
      	   return false;
         }
    	
    	if (document.getElementById("enableSnmp").checked && 
       		document.getElementsByName("ipAddressSnmpIndices").length == 0) {
       		hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.server.address" /></s:param></s:text>');
            displayErrorObj.focus();
            return false;
       }
       if (!checkCommunitiesString()) {
		   return false;
	   }
	   
	   if (!checkV3ListString()) {
		   return false;
	   }

    }
    if(operation=='addSnmp')
      if(!validateIpAddress() || !checkItemLimit() || !checkCommunityString() || !checkPasswordWhenAdd() ||!checkV3ListString())
             return false;
    if(operation=='removeSnmp' && !checkSelectedItems())
          return false;
          
    if(operation == 'editIpAddressSnmp') {
    	var value = hm.util.validateListSelection("myIpSelect");
    	
    	if(value < 0){
			return false;
		}else{
			document.forms[formName].ipAddressSnmpIds.value = value;
		}
    	
    }
    
	return true;
}
function checkCommunitiesString() { 
    var inputElement = document.getElementsByName('communities');
    for(var i=0;i<inputElement.length;i++)
    {	
    	if (Get("snmpVersions_" + i).value==0 || Get("snmpVersions_" + i).value==1){
	    	var oneCom = Get("communities_" + i);
	        var message = hm.util.validateName(oneCom.value, '<s:text name="config.mgmtservice.community" />');
		    if (message != null) {
		        hm.util.reportFieldError(oneCom, message);
		        oneCom.focus();
		        return false;
		    }
	    } else {
	    	var oneUser = Get("userNames_" + i);
	        var message = hm.util.validateName(oneUser.value, '<s:text name="config.mgmtservice.snmp.admin" />');
		    if (message != null) {
		        hm.util.reportFieldError(oneUser, message);
		        oneUser.focus();
		        return false;
		    }
	    }
    }
    return true;
}

function checkV3ListString() { 
    var inputElement = document.getElementsByName('communities');
    for(var i=0;i<inputElement.length;i++)
    {	
    	if (Get("snmpVersions_" + i).value==0 || Get("snmpVersions_" + i).value==1){
	    	var oneCom = Get("communities_" + i);
	        var message = hm.util.validateName(oneCom.value, '<s:text name="config.mgmtservice.community" />');
		    if (message != null) {
		        hm.util.reportFieldError(oneCom, message);
		        oneCom.focus();
		        return false;
		    }
	    } else {
	    	var oneUser = Get("userNames_" + i);
	        var message = hm.util.validateName(oneUser.value, '<s:text name="config.mgmtservice.snmp.admin" />');
		    if (message != null) {
		        hm.util.reportFieldError(oneUser, message);
		        oneUser.focus();
		        return false;
		    }
		    
		    var oneOperation = Get("snmpOperations_" + i);
		    if (oneOperation.value==2 || oneOperation.value == 3){
		    	if (Get("authMethods_" + i).value==-1){
		    		hm.util.reportFieldError(Get("authMethods_" + i), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.auth" /></s:param></s:text>');
					Get("authMethods_" + i).focus();
		        	return false;
		    	}
		    }
		    
		    if (Get("authMethods_" + i).value!=-1){
		    	if (Get("authPasses_" +i).value.length==0){
		    		hm.util.reportFieldError(Get("authPasses_" + i), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param></s:text>');
					Get("authPasses_" + i).focus();
		        	return false;
		    	}
		    	if (Get("authPasses_" +i).value.length<8){
		    		hm.util.reportFieldError(Get("authPasses_" + i), '<s:text name="error.keyValueRange"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param><s:param><s:text name="config.mgmtService.snmp.v3.pass.range" /></s:param></s:text>');
					Get("authPasses_" + i).focus();
		        	return false;
		    	}
		    }
		    if (Get("encryMethods_" + i).value!=-1){
		    	if (Get("snmpOperations_"+i).value>1 && Get("authMethods_" + i).value==-1){
		    		hm.util.reportFieldError(Get("authMethods_" + i), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.auth" /></s:param></s:text>');
					Get("authMethods_" + i).focus();
		        	return false;
		    	}
		    
		    	if (Get("encryPasses_" +i).value.length==0){
		    		hm.util.reportFieldError(Get("encryPasses_" + i), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param></s:text>');
					Get("encryPasses_" + i).focus();
		        	return false;
		    	}
		    	if (Get("encryPasses_" +i).value.length<8){
		    		hm.util.reportFieldError(Get("encryPasses_" + i), '<s:text name="error.keyValueRange"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param><s:param><s:text name="config.mgmtService.snmp.v3.pass.range" /></s:param></s:text>');
					Get("encryPasses_" + i).focus();
		        	return false;
		    	}
		    }
	    }
    }
    return true;
}

function checkCommunityString(){
	if (Get("snmpVersion").value==2){
		return true;
	}  
      var inputElement = document.getElementById('community');
      var message = hm.util.validateName(inputElement.value, '<s:text name="config.mgmtservice.community" />');
	  if (message != null) {
	      hm.util.reportFieldError(inputElement, message);
	      inputElement.focus();
	      return false;
	  }
      return true;
}

function checkPasswordWhenAdd(){
	if (Get("snmpVersion").value!=2){
		return true;
	} 
	
 	var inputElement = document.getElementById("userName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.mgmtservice.snmp.admin" />');
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
	if (Get("snmpOperation").value==2||Get("snmpOperation").value==3){
		if (Get("authPassMethod").value==-1){
			hm.util.reportFieldError(Get("authPassMethod"), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.auth" /></s:param></s:text>');
			Get("authPassMethod").focus();
			return false;
		}
	}
	var password;
	var confirm;
	if (Get("authPassMethod").value!=-1){
		if (document.getElementById("chkToggleDisplay0").checked) {
			password = document.getElementById("authPass");
			confirm = document.getElementById("authPassConfirm");
		} else {
			password = document.getElementById("authPass_text");
			confirm = document.getElementById("authPassConfirm_text");
		}
		if (password.value.length == 0) {
			hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param></s:text>');
			password.focus();
			return false;
		}
		if (password.value.length <8) {
			hm.util.reportFieldError(password, '<s:text name="error.keyValueRange"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param><s:param><s:text name="config.mgmtService.snmp.v3.pass.range" /></s:param></s:text>');
			password.focus();
			return false;
		}
		if (confirm.value.length == 0) {
			hm.util.reportFieldError(confirm, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password.confirm" /></s:param></s:text>');
			confirm.focus();
			return false;
		}
		if (password.value.length > 0 || confirm.value.length > 0) {
			if(!checkPassword(password, confirm, '<s:text name="config.mgmtservice.snmp.password" />', '<s:text name="config.mgmtservice.snmp.password.confirm" />')) {
	       		return false;
	   		}
		}
	}
	if (Get("encryPassMethod").value!=-1){
		if (Get("snmpOperation").value>1 && Get("authPassMethod").value==-1){
			hm.util.reportFieldError(Get("authPassMethod"), '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.auth" /></s:param></s:text>');
			Get("authPassMethod").focus();
			return false;
		}
	
		if (document.getElementById("chkToggleDisplay1").checked) {
			password = document.getElementById("encryPass");
			confirm = document.getElementById("encryPassConfirm");
		} else {
			password = document.getElementById("encryPass_text");
			confirm = document.getElementById("encryPassConfirm_text");
		}
		if (password.value.length == 0) {
			hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param></s:text>');
			password.focus();
			return false;
		}
		
		if (password.value.length <8) {
			hm.util.reportFieldError(password, '<s:text name="error.keyValueRange"><s:param><s:text name="config.mgmtservice.snmp.password" /></s:param><s:param><s:text name="config.mgmtService.snmp.v3.pass.range" /></s:param></s:text>');
			password.focus();
			return false;
		}
		
		if (confirm.value.length == 0) {
			hm.util.reportFieldError(confirm, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.snmp.password.confirm" /></s:param></s:text>');
			confirm.focus();
			return false;
		}
		
		if (password.value.length > 0 || confirm.value.length > 0) {
			if(!checkPassword(password, confirm, '<s:text name="config.mgmtservice.snmp.password" />', '<s:text name="config.mgmtservice.snmp.password.confirm" />')) {
	       		return false;
	   		}
		}
	}
	return true;
}

function checkPassword(password, confirm, passtitle, confirmtitle)
{
	var message = hm.util.validatePassword(password.value, passtitle);
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, confirmtitle);
   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
       	confirm.focus();
       	return false;
   	}
	if (password.value != confirm.value) {
        hm.util.reportFieldError(confirm, '<s:text name="error.notEqual"><s:param>'+confirmtitle+'</s:param><s:param>'+passtitle+'</s:param></s:text>');
        confirm.focus();
        return false;
    }
    return true;
}

function checkItemLimit(){
    var inputElement = document.getElementsByName('ipAddressSnmpIndices');	
	if (inputElement.length>=4) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.limitReachedSnmp"></s:text>');
		return false;
	}
	return true;
}
function validateName() {      
      var inputElement = document.getElementById("mgmtName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.mgmtservice.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }
      
      return true;
}
function validateContact() {      
      var inputElement = document.getElementById("contact");
      var message = hm.util.validateName(inputElement.value, '<s:text name="config.mgmtservice.contact" />');
       if (inputElement.value.length > 0 && message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }
      return true;
}

function validateIpAddress(){
	var ipnames = document.getElementById("myIpSelect");
	var ipValue = document.forms[formName].inputIpValue;
	var showError = document.getElementById("errorDisplay");
	
	if ("" == ipValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.mgmtservice.snmp.server.address" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			var message = hm.util.validateName(ipValue.value, '<s:text name="config.mgmtservice.snmp.server.address" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(showError, message);
	        	ipValue.focus();
	        	return false;
	    	}
	    }
		document.forms[formName].ipAddressSnmpIds.value = -1;
	} else {
		document.forms[formName].ipAddressSnmpIds.value = ipnames.options[ipnames.selectedIndex].value;
	}
    return true;
}

function checkSelectedItems() {
    var inputElement = document.getElementsByName('ipAddressSnmpIndices');
	if (inputElement.length == 0) {
	    hm.util.reportFieldError(displayErrorObj, '<s:text name="info.emptyList" />');
	    displayErrorObj.focus();
	    return false;
	}
	if (!hm.util.hasCheckedBoxes(inputElement)) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return false;
	}
	return true;
}
function toggleCheckAll(cb) {
	var cbs = document.getElementsByName('ipAddressSnmpIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeSnmpVersion(value){
	if (value==0 || value==1) {
		Get("v1v2Div").style.display="block";
		Get("v3Div").style.display="none";
	} else {
		Get("v1v2Div").style.display="none";
		Get("v3Div").style.display="block";
	}
}

function changeSnmpVersionList(value, key){
	
	if (value==0 || value==1) {
		Get("userNames_" + key).readOnly=true;
		Get("authPasses_" + key).readOnly=true;
		Get("encryPasses_" + key).readOnly=true;
		Get("communities_" + key).readOnly=false;
		
		
		//Get("userNames_" + key).visable=false;
		//Get("authMethods_" + key).visable=false;
		//Get("encryMethods_" + key).visable=false;
		//Get("communities_" + key).visable=true;
		
	} else {
		Get("userNames_" + key).readOnly=false;
		Get("authPasses_" + key).readOnly=false;
		Get("encryPasses_" + key).readOnly=false;
		Get("communities_" + key).readOnly=true;
		
		//Get("userNames_" + key).visable=true;
		//Get("authMethods_" + key).visable=true;
		//Get("encryMethods_" + key).visable=true;
		//Get("communities_" + key).visable=false;
	}
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtServiceSnmp" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
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
function isDisabledAll(value){

   if(value=="server"){
       snmpRadioValue='server';
       var obj=document.getElementById('serverName');
       obj.disabled=false;
       obj.value="";
       document.getElementById('ipAddressSnmpIds').disabled=true;
       document.getElementById('newIpAddressSnmp').disabled=true;
   }
   if(value=="address"){
       snmpRadioValue='address';
       var obj=document.getElementById('serverName');
       obj.disabled=true;
       obj.value="";
       document.getElementById('ipAddressSnmpIds').disabled=false;
       document.getElementById('newIpAddressSnmp').disabled=false;
   }
}
</script>
<div id="content"><s:form action="mgmtServiceSnmp" id="mgmtServiceSnmp" name="mgmtServiceSnmp">
    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
    <s:hidden name="ipAddressSnmpIds"/>
    <s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		
		<div id="vlanTitleDiv" class="topFixedTitle" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td  align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-SNMP_Assignment.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceSnmp"/></td>
							</s:if>
							<s:else>
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceSnmp.edit"/></td>
							</s:else>
							<td style="padding-left: 10px">
							    <a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
	                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
	                                   alt="" class="dblk" />
	                            </a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style=" margin-right: 20px;" tittle="<s:text name="common.button.cancel"/>" onclick="parent.closeIFrameDialog();"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<!-- <td width="25px">&nbsp;</td> -->
							<s:if test="%{dataSource.id == null}">
								<s:if test="'' == writeDisabled">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onclick="saveSnmp('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{'' == updateDisabled}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="saveSnmp('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		</table>
		</div>
		
	</s:if>
	
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
	<s:if test="%{jsonMode == false}">
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_MGMT_SERVICE_SNMP%>');">
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
	</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table border="0" cellspacing="0" cellpadding="0" width="760">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="760">
			</s:else>
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="120"><s:text
								name="config.mgmtservice.name" /><font color="red"><s:text name="hm.common.required"/></font></td>
							<td><s:textfield name="dataSource.mgmtName" size="24" id="mgmtName"
								maxlength="%{nameLength}" disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="config.name.range"/></td>
						</tr>		
						
						<tr>
							<td class="labelT1" width="120"><s:text
								name="config.mgmtservice.contact" /></td>
							<td><s:textfield name="dataSource.contact" size="24" id="contact"
								maxlength="%{nameLength}" onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
								<s:text name="config.mgmtService.snmp.contact.range"/></td>
						</tr>
								
						<tr>
							<td class="labelT1" width="120"><s:text name="config.mgmtservice.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
						<tr>
					      <td height="3"></td>
				        </tr>
						<tr>
					        <td colspan="2" style="padding:6px 0px 4px 9px;">
					        	<s:checkbox id="enableSnmp" name="dataSource.enableSnmp" />
					        	<s:text name="config.mgmtservice.enableSnmp"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					        	<s:checkbox id="enableCapwap" name="dataSource.enableCapwap" />
					        	<s:text name="config.mgmtservice.enableCapwap"/>
					        </td>
				        </tr>	
					</table>
					</td>
				</tr>
				<tr>
					<td height="3"></td>
				</tr>
				
				<tr>
				   <td style="padding:0px 6px 0px 6px;">
				     <table cellspacing="0" cellpadding="0" border="0" width="100%">
				        <tr>
				         <td class="sepLine" colspan="2" style="padding:0px 10px 0px 10px;"><img
						   src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
						</tr>
					 </table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>				
				
				<tr>
					<td style="padding:0px 5px 0px 10px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr> 
							<td valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%"	class="embedded">							
							    <tr id="newButton">
									<td colspan="6" style="padding-left: 0px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
												class="button" onClick="showCreateSection();"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="writeDisabled" />
												class="button"
												onClick="submitAction('removeSnmp');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButton">
									<td colspan="11" style="padding-left: 0px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply" <s:property value="updateDisabled" />
												class="button" onClick="submitAction('addSnmp');"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="updateDisabled" />
												class="button"
												onClick="submitAction('removeSnmp');"></td>
											<td><input type="button" name="ignore" value="Cancel" <s:property value="updateDisabled" />
												class="button" onClick="hideCreateSection();"></td>											
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createSection">
									<td colspan="11" style="padding: 4px 4px 4px 4px;"> 
										<fieldset><legend><s:text name="config.mgmtservice.snmp.title" /></legend>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td  class="labelT1" width="120px"><s:text name="config.mgmtservice.snmp.server.address" /><font color="red"><s:text name="hm.common.required"/></font></td>
												<td width="400px" id="ipAddressNameSelect" colspan="2">
													<ah:createOrSelect divId="errorDisplay" list="availableIpAddressSnmp" typeString="IpAddressSnmp"
													selectIdName="myIpSelect" inputValueName="inputIpValue" swidth="192px" tlength="32"/>
												</td>
											</tr>
											<tr>
												<td class="labelT1"><s:text name="config.mgmtservice.version"/></td>
										        <td colspan="2"><s:select name="snmpVersion" id="snmpVersion"
													list="%{enumVersionValues}" listKey="key" listValue="value" cssStyle="width: 192px;" 
													onchange="changeSnmpVersion(this.options[this.selectedIndex].value)"/></td>
											</tr>
											<tr>
												<td class="labelT1"><s:text name="config.mgmtservice.operation"/></td>
											    <td colspan="2"><s:select name="snmpOperation" id="snmpOperation"
													list="%{enumOperationValues}" listKey="key"	listValue="value" cssStyle="width: 192px;" /></td>
											</tr>
											<tr>
												<td colspan="3">
													<div style="display:none;" id="v1v2Div">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td width="120px" class="labelT1"><s:text name="config.mgmtservice.community"/></td>
																<td><s:textfield name="community" size="32"
														        	id="community" maxlength="%{nameLength}" onkeypress="return hm.util.keyPressPermit(event,'name');"/>
														        	</td>
														        <td>&nbsp;<s:text name="config.mgmtService.snmp.community.range"/></td>
														    </tr>
													    </table>
													</div>
										        </td>
							        		</tr>
							        		<tr>
							        			<td colspan="3">
													<div style="display:none;" id="v3Div">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td width="120px" class="labelT1"><s:text name="config.mgmtservice.snmp.admin"/><font color="red"><s:text name="hm.common.required"/></font></td>
														        <td><s:textfield name="userName" size="32"
														        	id="userName" maxlength="%{nameLength}" onkeypress="return hm.util.keyPressPermit(event,'name');"/>
															    	</td>
															    <td>&nbsp;<s:text name="config.mgmtService.snmp.community.range"/></td>
														    </tr>
															<tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.auth"/><font color="red"><s:text name="hm.common.required"/></font></td>
														        <td colspan="2"><s:select name="authPassMethod" id="authPassMethod"
														        	headerKey="-1" headerValue=""
																	list="%{enumAuthPassMethodValues}" listKey="key" listValue="value" cssStyle="width: 192px;" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.password"/><font color="red"><s:text name="hm.common.required"/></font></td>
																<td><s:password name="authPass" size="32" showPassword="true"
														        	id="authPass" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
														        	
														        	<s:textfield id="authPass_text" name="authPass"
																		size="32" maxlength="64" cssStyle="display:none" disabled="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
														        <td>&nbsp;<s:text name="config.mgmtService.snmp.v3.pass.range"/></td>
														    </tr>
														    <tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.password.confirm" /><font color="red"><s:text name="hm.common.required"/></font></td>
																<td><s:password id="authPassConfirm" showPassword="true"
																	name="authPassConfirm" value="%{authPass}" size="32"
																	maxlength="64" 
																	onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																	<s:textfield id="authPassConfirm_text"
																	name="authPassConfirm" value="%{authPass}" size="32"
																	maxlength="64" cssStyle="display:none" disabled="true"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td>
																				<s:checkbox id="chkToggleDisplay0" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																					onclick="hm.util.toggleObscurePassword(this.checked,['authPass','authPassConfirm'],['authPass_text','authPassConfirm_text']);" />
																			</td>
																			<td>
																				<s:text name="admin.user.obscurePassword" />
																			</td>
																		</tr>
																	</table>
																 </td>
															</tr>
														     
														     
															<tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.encry"/></td>
														        <td colspan="2"><s:select name="encryPassMethod" id="encryPassMethod"
														        	headerKey="-1" headerValue=""
																	list="%{enumEncryPassMethodValues}" listKey="key"	listValue="value" cssStyle="width: 192px;" /></td>
															</tr>
														     <tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.password"/></td>
																<td><s:password name="encryPass" size="32" showPassword="true"
														        	id="encryPass" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"/>
														        	
														        	<s:textfield id="encryPass_text" name="encryPass"
																		size="32" maxlength="64" cssStyle="display:none" disabled="true"
																		onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
														        <td>&nbsp;<s:text name="config.mgmtService.snmp.v3.pass.range"/></td>
														    </tr>
														    <tr>
																<td class="labelT1"><s:text name="config.mgmtservice.snmp.password.confirm" /></td>
																<td><s:password id="encryPassConfirm" showPassword="true"
																	name="encryPassConfirm" value="%{encryPass}" size="32"
																	maxlength="64" 
																	onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																	<s:textfield id="encryPassConfirm_text"
																	name="encryPassConfirm" value="%{encryPass}" size="32"
																	maxlength="64" cssStyle="display:none" disabled="true"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td>
																				<s:checkbox id="chkToggleDisplay1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
																					onclick="hm.util.toggleObscurePassword(this.checked,['encryPass','encryPassConfirm'],['encryPass_text','encryPassConfirm_text']);" />
																			</td>
																			<td>
																				<s:text name="admin.user.obscurePassword" />
																			</td>
																		</tr>
																	</table>
																 </td>
															</tr>
														 </table>
													</div>
												</td>
											</tr>
										</table>
										</fieldset>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0;" width="10"><input
										type="checkbox" id="checkAll" name="snmpIndices"
										onClick="toggleCheckAll(this);"></th>
									<th align="left" width="200">
									    <s:text name="config.mgmtservice.snmp.server.address"/></th>
									<th align="left" width="80"><s:text name="config.mgmtservice.version"/></th>
									<th align="left" width="100"><s:text name="config.mgmtservice.operation" /></th>
									<th align="left" width="120"><s:text name="config.mgmtservice.community"/></th>
									<th align="left" width="120"><s:text name="config.mgmtservice.snmp.admin"/></th>
									<th align="left" width="80"><s:text name="config.mgmtservice.snmp.auth"/></th>
									<th align="left" width="120"><s:text name="config.mgmtservice.snmp.password"/></th>
									<th align="left" width="80"><s:text name="config.mgmtservice.snmp.encry"/></th>
									<th align="left" width="120"><s:text name="config.mgmtservice.snmp.password"/></th>
									
								</tr>
								<s:iterator value="%{dataSource.snmpInfo}"	status="status" id="snmpInfoId">
									<tr>
										<td class="listCheck"  style="padding:2px 0px 0px 0px;" >
										    <s:checkbox name="ipAddressSnmpIndices"
											fieldValue="%{#status.index}" /></td>	
										<td class="list" style="padding-left: 0px;">
										    <s:property value="serverName" /></td>
										<s:if test="%{readOnlyV1V2}">
											<td class="list"><s:select name="snmpVersions" cssStyle="width: 50px;" 
												id="snmpVersions_%{#status.index}"
												list="%{enumVersionV3Values}" listKey="key" listValue="value"
												value="%{snmpVersion}" onchange="changeSnmpVersionList(this.options[this.selectedIndex].value,%{#status.index})"/></td>	
										</s:if> 
										<s:else>
											<td class="list"><s:select name="snmpVersions" cssStyle="width: 50px;" 
												id="snmpVersions_%{#status.index}"
												list="%{enumVersionV1V2Values}" listKey="key" listValue="value"
												value="%{snmpVersion}" onchange="changeSnmpVersionList(this.options[this.selectedIndex].value,%{#status.index})"/></td>	
										</s:else>
										<td class="list"><s:select name="snmpOperations" id="snmpOperations_%{#status.index}" cssStyle="width: 100px;" 
											list="%{enumOperationValues}" listKey="key" listValue="value"
											value="%{snmpOperation}" /></td>										
										<td class="list"><s:textfield  name="communities" id="communities_%{#status.index}"
											size="14" maxlength="%{nameLength}" onkeypress="return hm.util.keyPressPermit(event,'name');"
											value="%{community}" readonly="%{readOnlyV1V2}"/></td>	
										<td class="list"><s:textfield  name="userNames" id="userNames_%{#status.index}"
											size="14" maxlength="%{nameLength}" onkeypress="return hm.util.keyPressPermit(event,'name');"
											value="%{userName}" readonly="%{readOnlyV3}"/></td>
										<s:if test="%{readOnlyV1V2}">
											<td class="list"><s:select name="authMethods" cssStyle="width: 50px;" id="authMethods_%{#status.index}"
												list="%{enumAuthPassMethodValues}" listKey="key" listValue="value"
												headerKey="-1" headerValue=""
												value="%{authPassMethod}" /></td>	
											<td class="list"><s:password name="authPasses" id="authPasses_%{#status.index}" showPassword="true"
												size="14" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"
												value="%{authPass}" readonly="%{readOnlyV3}"/></td>
											<td class="list"><s:select name="encryMethods" cssStyle="width: 50px;" id="encryMethods_%{#status.index}"
												list="%{enumEncryPassMethodValues}" listKey="key" listValue="value"
												headerKey="-1" headerValue=""
												value="%{encryPassMethod}" /></td>	
											<td class="list"><s:password name="encryPasses" id="encryPasses_%{#status.index}" showPassword="true"
												size="14" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"
												value="%{encryPass}" readonly="%{readOnlyV3}"/></td>
										</s:if>	
										<s:else>
											<td class="list"><s:select name="authMethods" cssStyle="width: 50px;" id="authMethods_%{#status.index}"
												list="%{enumAuthPassMethodValuesNull}" listKey="key" listValue="value"
												headerKey="-1" headerValue="" onchange="this.value=-1;"
												value="%{authPassMethod}" /></td>
											<td class="list"><s:password name="authPasses" id="authPasses_%{#status.index}" showPassword="true"
												size="14" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"
												value="%{authPass}" readonly="%{readOnlyV3}"/></td>
											<td class="list"><s:select name="encryMethods" cssStyle="width: 50px;" id="encryMethods_%{#status.index}"
												list="%{enumEncryPassMethodValuesNull}" listKey="key" listValue="value"
												headerKey="-1" headerValue="" onchange="this.value=-1;"
												value="%{encryPassMethod}" /></td>	
											<td class="list"><s:password name="encryPasses" id="encryPasses_%{#status.index}" showPassword="true"
												size="14" maxlength="64" onkeypress="return hm.util.keyPressPermit(event,'password');"
												value="%{encryPass}" readonly="%{readOnlyV3}"/></td>
										</s:else>
										
											
									</tr>
								</s:iterator>
								<s:if test="%{gridCount > 0}">
									<s:generator separator="," val="%{' '}" count="%{gridCount}">
										<s:iterator>
											<tr>
												<td class="list" colspan="11">&nbsp;</td>
											</tr>
										</s:iterator>
									</s:generator>
								</s:if>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				
				<tr>
					<td height="4"></td>
				</tr>
				
			</table>		
			</td>
		</tr>
	</table>
</s:form></div>
