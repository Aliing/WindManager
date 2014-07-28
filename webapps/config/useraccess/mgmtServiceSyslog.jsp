<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'mgmtServiceSyslog';
var buttonShowing;
var displayErrorObj;
function onLoadPage() {	
    displayErrorObj = document.getElementById("checkAll");
    
	if (document.getElementById("mgmtName").disabled == false) {
			document.getElementById("mgmtName").focus();
	}
	
	var operation = "<s:property value="%{operation}"/>";
	buttonShowing = <s:property value="%{buttonShowing}"/>;
	if((operation=='continue' || operation=='removeSyslog' || operation=='addSyslog'
		|| operation=='edit') && buttonShowing) {
	   showCreateSection();
	}
	if(operation=='new') {
		showCreateSection();
    }
	<s:if test="%{jsonMode}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(800,550);
	 	}
	</s:if>
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
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create' &&
		    operation != 'addSyslog' &&
		    operation != 'removeSyslog') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;	
	    document.forms[formName].submit();
	}
}

function saveSyslog(operation) {
	if (validate(operation)){
		var	url = "<s:url action='mgmtServiceSyslog' includeParams='none' />" + "?jsonMode=true" 
			+ "&ignore=" + new Date().getTime(); 
		if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mgmtServiceSyslog"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveSyslog, failure : failSave, timeout: 60000}, null);
	}
	
}

var succSaveSyslog = function(o) {
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

function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MGMT_SERVICE_SYSLOG%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == "newIpAddressSyslog") {
		return true;
	}

    if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>'){
       if (!validateName()) {
		   return false;
	   }
       
	   var elements = document.getElementsByName("ipAddressSyslogIndices");

	   if (elements.length == 0) {
       		hm.util.reportFieldError(document.getElementById("checkAll"),
       		 '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.syslog.server.address" /></s:param></s:text>');
            document.getElementById("checkAll").focus();
        return false;
       }
    }
   
    if(operation=='addSyslog')
      if(!validateIpAddress() || !checkItemLimit())
             return false;
    
    if(operation=='removeSyslog' && !checkSelectedItems())
         return false;
	
    if(operation == 'editIpAddressSyslog') {
    	var value = hm.util.validateListSelection("myIpSelect");
    	
    	if(value < 0){
        	return false;
	}else{
		document.forms[formName].ipAddressSyslogId.value = value;
	}
    	
    }
    
    
	return true;
}
function checkItemLimit(){
    var inputElement = document.getElementsByName('ipAddressSyslogIndices');
	if (inputElement.length>=4) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.limitReachedSyslog"></s:text>');
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
function checkSelectedItems() {
	var inputElement = document.getElementsByName("ipAddressSyslogIndices");
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

function validateIpAddress(){
	var ipnames = document.getElementById("myIpSelect");
	var ipValue = document.forms[formName].inputIpValue;
	var showError = document.getElementById("errorDisplay");
	
	if ("" == ipValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.mgmtservice.syslog.server.address" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			var message = hm.util.validateName(ipValue.value, '<s:text name="config.mgmtservice.syslog.server.address" />');
	    	if (message != null) {
	    		hm.util.reportFieldError(showError, message);
	        	ipValue.focus();
	        	return false;
	    	}
	    }
		document.forms[formName].ipAddressSyslogId.value = -1;
	} else {
		document.forms[formName].ipAddressSyslogId.value = ipnames.options[ipnames.selectedIndex].value;
	}
    return true;
}

function toggleCheckAll(cb) {
	var cbs = document.getElementsByName('ipAddressSyslogIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtServiceSyslog" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
    </s:else>
}

</script>
<div id="content">
   <s:form action="mgmtServiceSyslog" id="mgmtServiceSyslog" name="mgmtServiceSyslog">
    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
    <s:hidden name="ipAddressSyslogId"/>
    <s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
	</s:if>
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_MGMT_SERVICE_SYSLOG%>');">
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
		<s:else>
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-syslog_assignments.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceSyslog"/></td>
							</s:if>
							<s:else>
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceSyslog.edit"/></td>
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
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onclick="saveSyslog('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="'' == writeDisabled">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="saveSyslog('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
					</td>
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
				<table  border="0" cellspacing="0" cellpadding="0" width="690">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="690">
			</s:else>
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="100"><s:text
								name="config.mgmtservice.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.mgmtName" size="24" id="mgmtName"
								maxlength="%{nameLength}" disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="config.name.range"/> </td>
						</tr>		
						
						<tr>
							<td class="labelT1" width="100"><s:text
								name="config.mgmtservice.facility" /></td>
							<td><s:select name="facilitySyslog" 
							     list="%{enumFacilityValues}" listKey="key" listValue="value"
							     value="%{facilitySyslog}" cssStyle="width: 150px;"/> </td>
						</tr>
						
						<tr>
							<td class="labelT1" width="100">
							    <s:text name="config.mgmtservice.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding:6px 0px 4px 6px;" colspan="2">
								<s:checkbox name="dataSource.internalServer"></s:checkbox>
								<s:text name="config.mgmtservice.internalServer" /></td>
						</tr>
						<tr>
					      <td height="3"></td>
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
					<td style="padding:0px 5px 0px 5px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr> 							
							<td valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%"	class="embedded">
							    <tr id="newButton">
									<td colspan="5" style="padding-left: 0px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
												class="button" onClick="showCreateSection();"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="writeDisabled" />
												class="button"
												onClick="submitAction('removeSyslog');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButton">
									<td colspan="5" style="padding-left: 0px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addSyslog');"></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button"
												onClick="submitAction('removeSyslog');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection();"></td>
											
										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0;" width="10"><input
										type="checkbox" id="checkAll" name="syslogIndices"
										onClick="toggleCheckAll(this);"></th>
									<th align="left" width="300">	 
									     <s:text name="config.mgmtservice.syslog.server.address"/></th>
									<th align="left" width="110"><s:text name="config.mgmtservice.severity"/></th>
									<th align="left" width="150"><s:text name="config.mgmtservice.description"/></th>
								</tr>
								<tr style="display:none;" id="createSection">
								    <td class="listHead" width="10">&nbsp;</td>
									<td class="listHead" valign="top" width="360" id="ipAddressNameSelect">
										<ah:createOrSelect divId="errorDisplay" list="availableIpAddressSyslog" typeString="IpAddressSyslog"
										selectIdName="myIpSelect" inputValueName="inputIpValue" />
									</td>
							        <td class="listHead" valign="top" width="110">
							           <s:select  name="severitySyslog" id="severitySyslog"
										list="%{enumSeverityValues}" listKey="key" 
										listValue="value" cssStyle="width:110px;" value="%{severityDefaultValue}"/></td>
									<td class="listHead" valign="top" width="150">
									   <s:textfield name="syslogDescription" size="26"
							        	maxlength="%{descriptionLength}"/><br><s:text name="config.description.range"/></td>
								</tr>
																
								<s:iterator value="%{dataSource.syslogInfo}" status="status" id="syslogInfoId">
									<tr>
										<td class="listCheck">
										    <s:checkbox name="ipAddressSyslogIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list">
										    <s:property value="serverName"/></td>
										<td class="list"><s:select name="severitiesSyslog"
											list="%{enumSeverityValues}" listKey="key" listValue="value" cssStyle="width:110px;"
											value="%{enumSeverityValues[#syslogInfoId.severity]}" /></td>																		
										<td class="list"><s:textfield  name="syslogDescriptions" 
											size="26" maxlength="%{descriptionLength}"
											value="%{syslogDescription}"/></td>										
									</tr>
								</s:iterator>
								<s:if test="%{gridCount > 0}">
									<s:generator separator="," val="%{' '}" count="%{gridCount}">
										<s:iterator>
											<tr>
												<td class="list" colspan="5">&nbsp;</td>
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
