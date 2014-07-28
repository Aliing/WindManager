<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'mgmtServiceDns';
var buttonShowing;
var displayErrorObj;
function onLoadPage() {	
	if (document.getElementById("mgmtName").disabled == false) {
			document.getElementById("mgmtName").focus();
	}
	displayErrorObj = document.getElementById("checkAll");
	var operation = "<s:property value="%{operation}"/>";
	buttonShowing = <s:property value="%{buttonShowing}"/>;
	if((operation=='continue' || operation=='removeDns') && buttonShowing) {
	   showCreateSection();
	}
	if(operation=='new') {
		showCreateSection();
    }
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(750, 450);
	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'addDns' && operation != 'removeDns') {
			showProcessing();
		} 
		
		document.forms[formName].operation.value = operation;		
		document.forms[formName].submit();
	}
}

function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MGMT_SERVICE_DNS%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == "newIpAddressDns") {
		return true;
	}
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create'){
       if (!validateName()) {
		   return false;
	   }
    }
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>'
    		 || operation == 'create' || operation == 'update'){
       if (document.getElementById("domainName").value.length > 0 && !validateDomainName()) {
		   return false;
	   }
	   if (!validateIpAddress()) {
	   	   return false;
	   }
    }		
    if(operation=='addDns') {
    	var ipnames = document.getElementById("myIpSelect");
		var ipValue = document.forms[formName].inputIpValue;
		var showError = document.getElementById("errorDisplay");
		
		if ("" == ipValue.value) {
	        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.mgmtservice.address" /></s:param></s:text>');
	        ipValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				hm.util.reportFieldError(showError, '<s:text name="error.config.mgmt.service.dns.inputIp"/>');
		       	ipValue.focus();
		       	return false;
		    }
			document.forms[formName].ipAddress.value = -1;
		} else {
			document.forms[formName].ipAddress.value = ipnames.options[ipnames.selectedIndex].value;
		}
		
		if (!checkItemLimit()) {
			return false;
		}
    }
    if(operation=='removeDns') {
    	var inputElement = document.getElementsByName("ipAddressDnsIndices");
	    if (inputElement.length == 0) {
	        hm.util.reportFieldError(displayErrorObj, '<s:text name="info.emptyList" />');
	        displayErrorObj.focus();
	        return false;
	    }
	    if (!checkSelectedItems()) {
	    	return false;
	    }
    }
    
    if(operation == 'newIpAddress' || operation == 'editIpAddress') {
		if(operation == "editIpAddress"){
			var value = hm.util.validateListSelection("myIpSelect");
			if(value < 0){
				return false
			}else{
				document.forms[formName].ipAddress.value = value;
			}
		}

		<s:if test="%{jsonMode}">
		top.changeIFrameDialog(950, 450);
		</s:if>
	}
	
	return true;
}
function checkItemLimit(){
    var inputElement = document.getElementsByName('ipAddressDnsIndices');
	if (inputElement.length>=3) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.limitReachedDns"></s:text>');
        displayErrorObj.focus();
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
function validateDomainName() {
      var inputElement = document.getElementById("domainName");
      var message = hm.util.validateDnsDomainString(inputElement.value, '<s:text name="config.mgmtservice.domain.name" />');
      if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
      }  
      return true;
}
function checkSelectedItems() {
	if (!hm.util.hasCheckedBoxes(document.getElementsByName("ipAddressDnsIndices"))) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
        displayErrorObj.focus();
		return false;
	}
	return true;
}
function validateIpAddress(){
     var inputElement = document.getElementsByName("ipAddressDnsIndices");
      if (inputElement.length == 0) {
            hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.address" /></s:param></s:text>');
            displayErrorObj.focus();
            return false;
      }
      return true;
}
function showCreateSection() {
    hm.util.hide('newButton');
	   hm.util.show('createButton');
	   hm.util.show('createSection');
	   // to fix column overlap issue on certain browsers
	   var trh = document.getElementById('headerSection');
	   var trc = document.getElementById('createSection');
	   var table = trh.parentNode;	
	   table.removeChild(trh);
	   table.insertBefore(trh, trc);
	   document.getElementById("buttonShowing").value="true";
}
function hideCreateSection() {
    hm.util.hide('createButton');
    hm.util.show('newButton');
    hm.util.hide('createSection');
	document.getElementById("buttonShowing").value="false";
}
function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}
function toggleCheckAll(cb) {
	var cbs = document.getElementsByName('ipAddressDnsIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='mgmtServiceDns' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mgmtServiceDns"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
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

<s:if test="%{!jsonMode}">
function insertPageContext() {
    <s:if test="%{lstTitle!=null && lstTitle.size>1}">
       document.writeln('<td class="crumb" nowrap>');
       <s:iterator value="lstTitle">
           document.writeln(" <s:property/> ");
       </s:iterator>
       document.writeln('</td>');
    </s:if>
    <s:else>
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtServiceDns" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
    </s:else>
}
</s:if>

</script>
<div id="content"><s:form action="mgmtServiceDns" name="mgmtServiceDns" id="mgmtServiceDns">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg"/>
	<s:hidden name="id" />
	<div id="titleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-DNS_Assignments.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceDns"/></td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceDns.edit"/></td>
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
					<s:if test="%{!parentIframeOpenFlg}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</s:if>
					<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
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
    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
    <s:hidden name="ipAddress"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_MGMT_SERVICE_DNS%>');">
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
				<table cellspacing="0" cellpadding="0" border="0" width="640px">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="640px">
			</s:else>
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="120">
							    <s:text	name="config.mgmtservice.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.mgmtName" size="24" id="mgmtName"
								maxlength="%{nameLength}" disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
								<s:text name="config.name.range"/></td>
						</tr>		
						<tr>
					        <td class="labelT1" width="120"><s:text	name="config.mgmtservice.domain.name" /></td>
					        <td colspan="3"><s:textfield name="dataSource.domainName" size="24" id="domainName"
					        	onkeypress="return hm.util.keyPressPermit(event,'dnsDomain');"
						        maxlength="%{domainNameLength}" /> 
						        <s:text name="config.domain.name.range"/></td>
				        </tr>				
						<tr>
							<td class="labelT1" width="120"><s:text name="config.mgmtservice.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="3"></td>
				</tr>
				
				<tr>
				   <td style="padding:6px 6px 0px 6px;">
				     <table cellspacing="0" cellpadding="0" border="0" width="100%">
				        <tr>
				         <td class="sepLine" colspan="3" style="padding:0px 10px 0px 10px;"><img
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
							<table cellspacing="0" cellpadding="0" border="0" width="100%"
								class="embedded">
								<tr id="newButton">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
												class="button" onClick="showCreateSection();"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="writeDisabled" />
												class="button"
												onClick="submitAction('removeDns');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButton">
									<td colspan="5" style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addDns');"></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button"
												onClick="submitAction('removeDns');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection();"></td>
											
										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0;"><input
										type="checkbox" id="checkAll" 
										onClick="toggleCheckAll(this);"></th>
									<th align="left" width="250" >
										<s:text name="config.mgmtservice.address" /></th>
									<th align="left"><s:text name="config.mgmtservice.description"/></th>
								</tr>
								<tr style="display:none;" id="createSection">
									<td class="listHead" width="10">&nbsp;</td>
									<td class="listHead" valign="top" width="380" style="padding:2px 5px 5px 0px;">
									    <ah:createOrSelect divId="errorDisplay"
													list="availableIpAddressDns" typeString="IpAddress"
													selectIdName="myIpSelect" inputValueName="inputIpValue" />
									</td>
									<td class="listHead"><s:textfield name="dnsDescription" size="36"
							        	maxlength="%{descriptionLength}"/><BR><s:text name="config.description.range"/></td>
								</tr>
								
								<s:iterator value="%{dataSource.dnsInfo}" status="status" id="dnsInfoId">
									<tr>
										<td class="listCheck">
									        <s:checkbox name="ipAddressDnsIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list">
											<s:property value="ipAddress.addressName"/></td>										
										<td class="list"><s:textfield 
											value="%{dnsDescription}"  name="descriptionDns" 
											size="36" maxlength="%{descriptionLength}"/></td>										
									</tr>
								</s:iterator>
								<s:if test="%{gridCount > 0}">
									<s:generator separator="," val="%{' '}" count="%{gridCount}">
										<s:iterator>
											<tr>
												<td class="list" colspan="3">&nbsp;</td>
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
