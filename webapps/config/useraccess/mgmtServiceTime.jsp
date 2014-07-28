<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'mgmtServiceTime';
var buttonShowing;
var displayErrorObj;
function onLoadPage() {	
    displayErrorObj = document.getElementById("checkAll");
    
	if (document.getElementById("mgmtName").disabled == false) {
			document.getElementById("mgmtName").focus();
	}
    var operation = "<s:property value="%{operation}"/>";
    buttonShowing =<s:property value="%{buttonShowing}"/>;
	if((operation == 'continue' || operation=='removeTime' || operation=='addTime'
		|| operation=='edit') && buttonShowing) {
	   showCreateSection();
	}
	if(operation=='new') {
		showCreateSection();
    }
	<s:if test="%{jsonMode}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(810,550);
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
		    operation != 'addTime' &&
		    operation != 'removeTime') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;	
	    document.forms[formName].submit();
	}
}

function saveServiceTime(operation) {
	if (validate(operation)){
		var	url = "<s:url action='mgmtServiceTime' includeParams='none' />" + "?jsonMode=true" 
			+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mgmtServiceTime"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succServiceTime, failure : failSave, timeout: 60000}, null);
	}
	
}

var succServiceTime = function(o) {
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
	if (operation == "<%=Navigation.L2_FEATURE_MGMT_SERVICE_TIME%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == "newIpAddressTime") {
		return true;
	}

    if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>'){
       if (!validateName()) {
		   return false;
	   }
    }
   
    if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>' 
    		|| operation=='update' || operation == 'update'+'<s:property value="lstForward"/>' ){
       if (!validateValueRange()) {
		   return false;
	   }
	   if (!document.getElementById("enableClock").disabled &&
			   !document.getElementById("enableClock").checked && 
       		document.getElementsByName("ipAddressTimeIndices").length == 0) {
       		hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.time.server.address" /></s:param></s:text>');
       		displayErrorObj.focus();
            return false;
       }
    }
    if(operation == 'editIpAddressTime') {
    	var value = hm.util.validateListSelection("myIpSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipAddressId.value = value;
		}
    }
	
    if(operation=='addTime')
      if(!validateIpDddress() ||  !checkItemLimit())
             return false;
    if(operation=='removeTime' && !checkSelectedItems())
        return false;
	return true;
}
function checkItemLimit(){
    var inputElement = document.getElementsByName('ipAddressTimeIndices');
	if (inputElement.length>=4) {
		//var feChild = document.getElementById('checkAll');
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.limitReachedNtp"></s:text>');
		return false;
	}
	return true;
}
function checkSelectedItems() {
   var inputElement = document.getElementsByName('ipAddressTimeIndices');
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
function validateValueRange() {                           
          var inputElement=document.getElementById("interval");
          if (inputElement.value.length == 0) {
               hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.interval" /></s:param></s:text>');
               inputElement.focus();
               return false;
           }
           var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.interval" />',
                                                       <s:property value="%{numberRange.min()}" />,
                                                       <s:property value="%{numberRange.max()}" />);
           if (message != null) {
               hm.util.reportFieldError(inputElement, message);
               inputElement.focus();
               return false;
           }
           
           return true;
}
function validateIpDddress(){
	var ipnames = document.getElementById("myIpSelect");
	var ipValue = document.forms[formName].inputIpValue;
	if ("" == ipValue.value) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.mgmtservice.time.server.address" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			var message = hm.util.validateName(ipValue.value, '<s:text name="config.mgmtservice.time.server.address" />');
		   	if (message != null) {
		   		hm.util.reportFieldError(displayErrorObj, message);
		   		ipValue.focus();
				return false;
		   	}
		}
		document.forms[formName].ipAddressId.value = -1;
	} else {
		document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
	}
	return true;
}

function toggleCheckAll(cb) {
	var cbs = document.getElementsByName('ipAddressTimeIndices');
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtServiceTime" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
    </s:else>
}
function isDisabledAll(value){
   if(value=="server"){
       timeRadioValue='server';
       var obj=document.getElementById('serverName');
       obj.disabled=false;
       obj.value="";
       document.getElementById('ipAddressTimeIds').disabled=true;
       document.getElementById('newIpAddressTime').disabled=true;
   }
   if(value=="address"){
       timeRadioValue='address';
       var obj=document.getElementById('serverName');
       obj.disabled=true;
       obj.value="";
       document.getElementById('ipAddressTimeIds').disabled=false;
       document.getElementById('newIpAddressTime').disabled=false;
   }
}

function selectNtpClient(ch)
{
   var obj=document.getElementById('enableClock');
   obj.checked=ch.checked;
   obj.disabled=!ch.checked;
   obj=document.getElementById('createIpAddress');
   obj.style.display="none";
}

function selectSyncClock(ch)
{  
   var obj=document.getElementById('createIpAddress');
   if(!ch.checked){
      obj.style.display="";
   }
   else{
      obj.style.display="none";
   }
}
function initNtpAndSync(bln_ntp,bln_sync)
{   
   var ntp=document.getElementById('enableNtp');
   var sync=document.getElementById('enableSync');
   var address=document.getElementById('createIpAddress');
   ntp.checked=bln_ntp;
   if(!bln_ntp)
   {
      sync.disabled=!bln_ntp;
      address.style.display="none";
   }
   else
   {
      sync.disabled=bln_ntp;
      sync.checked=bln_sync;
      if(bln_sync)
         address.style.display="none";
      else 
         address.style.display="";
   }  
}

</script>
<div id="content">
   <s:form action="mgmtServiceTime" id="mgmtServiceTime" name="mgmtServiceTime">
   	    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
        <s:hidden name="ipAddressId" />
        <s:if test="%{jsonMode}">
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<s:hidden name="parentDomID" />
			<s:hidden name="parentIframeOpenFlg"/>
			<s:hidden name="id" />
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_MGMT_SERVICE_TIME%>');">
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
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-NTP_assignments.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceTime"/></td>
							</s:if>
							<s:else>
								<td class="dialogPanelTitle"><s:text name="config.title.mgmtServiceTime.edit"/></td>
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
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style=" margin-right: 20px;" tittle="<s:text name="common.button.cancel"/>" onclick="parent.closeIFrameDialog();"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<!-- <td width="25px">&nbsp;</td> -->
							<s:if test="%{dataSource.id == null}">
								<s:if test="'' == writeDisabled">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onclick="saveServiceTime('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="'' == writeDisabled">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="saveServiceTime('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a></td>
								</s:if>
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
			</td>
		</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table border="0" cellspacing="0" cellpadding="0" width="700">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="700">
			</s:else>
				<tr>
					<td >
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="100" style="padding:6px 0px 4px 10px;"><s:text
								name="config.mgmtservice.name" /><font color="red"><s:text name="*"/></font></td>
							<td colspan="2" ><s:textfield name="dataSource.mgmtName" size="24" id="mgmtName"
								maxlength="%{nameLength}" disabled="%{disabledName}"
								onkeypress="return hm.util.keyPressPermit(event,'name');"  /> 
								<s:text name="config.name.range"/></td>
						</tr>
						
						<tr>
							<td class="labelT1"><s:text name="config.mgmtservice.interval" /><font color="red"><s:text name="*"/></font></td>
							<td colspan="2" ><s:textfield name="dataSource.interval" size="24" maxlength="5" id="interval"
							onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
							<s:text name="config.mgmtService.time.interval.range"/></td>
						</tr>		
						
						<tr>
							<td class="labelT1"><s:text name="config.mgmtservice.timeZone" /></td>
							<td colspan="2" ><s:select name="dataSource.timeZone" 
							     list="%{enumTimeZoneValues}" listKey="key" listValue="value"
							     cssStyle="width: 320px;"/> </td>
						</tr>
								
						<tr>
							<td class="labelT1"><s:text name="config.mgmtservice.description" /></td>
							<td colspan="2" ><s:textfield name="dataSource.description" size="57"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
						
						<tr>
					      <td height="3"></td>
				        </tr>
				        <tr>
					        <td colspan="2" class="labelT1"  style="padding:6px 0px 0px 9px;"><s:checkbox id="enableNtp"
					            name="dataSource.enableNtp" onclick="selectNtpClient(this)"/><s:text name="config.mgmtservice.enableNtp" /></td>
					        
				        </tr>
				        <tr>
					        <td colspan="2" class="labelT1"  style="padding:0px 0px 0px 9px;"><s:checkbox id="enableClock" 
					            name="dataSource.enableClock" onclick="selectSyncClock(this)" disabled="%{!dataSource.enableNtp}"/>
					            <s:text name="config.mgmtservice.syncClock" /></td>
				        </tr>	
						<tr>
					      <td height="3"></td>
				        </tr>
					</table>
					</td>
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
				
				<tr style="display:<s:property value="%{displayIp}"/>;" id="createIpAddress">
					<td style="padding:0px 5px 4px 10px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%"	class="embedded">
							    <tr id="newButton">
									<td colspan="4" style="padding-left: 6px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
												class="button" onClick="showCreateSection();"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="writeDisabled" />
												class="button" 
												onClick="submitAction('removeTime');"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr style="display:none;" id="createButton">
									<td colspan="4" style="padding-left: 6px;padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addTime');" ></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button" 
												onClick="submitAction('removeTime');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection();"></td>
											
										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0px;"><input
										type="checkbox" id="checkAll" name="timeIndices"
										onClick="toggleCheckAll(this);"></th>
									<th align="left" style="padding-left: 0px;">
									    <s:text name="config.mgmtservice.time.server.address"/></th>
									<th align="left"><s:text name="config.mgmtservice.description"/></th>
								</tr>
								<tr style="display:none;" id="createSection">
								    <td class="listHead" style="padding:5px 0px 2px 0px;" valign="top">&nbsp;</td>
								    <td class="listHead" style="padding:3px 0px 2px 0px;" valign="top" width="300"> 
									  <table style="padding:0px 0px 0px 0px;">								    
								        <tr id="ipAddressNameSelect" style="padding:0px 0px 0px 0px;">
									      <td  valign="top" style="padding:0px 0px 0px 0px;">
											<ah:createOrSelect divId="errorDisplay"
												list="availableIpAddressTime" typeString="IpAddressTime"
												selectIdName="myIpSelect" swidth="190px"
												inputValueName="inputIpValue" />
										   </td>
								        </tr>
									  </table>									  
									</td>
									<td class="listHead" style="padding:5px 0px 2px 3px;" valign="top" width="320">									
									    <s:textfield name="timeDescription" size="28" maxlength="%{descriptionLength}"/><br><s:text 
									    name="config.description.range"/></td>								        							   
								</tr>										
								
								<s:iterator value="%{dataSource.timeInfo}"	status="status" id="timeInfoId">
									<tr>
										<td class="listCheck" style="padding:2px 0px 0px 0px;" >
										   <s:checkbox name="ipAddressTimeIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list" style="padding:2px 0px 0px 0px;">
											<s:property value="serverName" /></td>
										<td class="list"><s:textfield  name="timeDescriptions" 
											size="28" maxlength="%{descriptionLength}"
											value="%{timeDescription}"/></td>										
									</tr>
								</s:iterator>
								<s:if test="%{gridCount > 0}">
									<s:generator separator="," val="%{' '}" count="%{gridCount}">
										<s:iterator>
											<tr>
												<td class="list" colspan="4">&nbsp;</td>
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
