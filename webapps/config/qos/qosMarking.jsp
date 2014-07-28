<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'qosMarking';
var displayErrorObj;
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	} 
}

function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if (validate(operation)) {
		var url =  "<s:url action='qosMarking' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["qosMarking"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById("qosMarking"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveQosMarking, failure : resultDoNothing, timeout: 60000}, null);	
	}
}
var resultDoNothing = function(o) {
//	alert("failed.");
};

var succSaveQosMarking = function (o) {
	eval("var details = " + o.responseText);
	if (details.t) {
		if (details.n){
			if (details.pId && details.pId!='') {
				hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), false, true);
				if(parent.qosUIElement){
					parent.qosUIElement.attr("disabled",false);
				}
			} 
		}
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}


function validate(operation) {
    var inputElement;
    if(operation=='create' || operation=='create<s:property value="lstForward"/>'
    	|| operation=='update<s:property value="lstForward"/>'
       || operation=='update'){
       inputElement=document.getElementById("qosName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.qos.classification.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }     
      if(!validateValueRange())
         return false;
    }
    
	return true;
}
function validateValueRange() {
      var inputElement;
     
      for(var i=0;i<8;i++)
      {
          var inputElement=document.getElementById("protocolPValue_"+i);
           if (inputElement.value.length == 0) {
               hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.marking.protocolP.value" /></s:param></s:text>');
               inputElement.focus();
               return false;
           }
           var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.qos.marking.protocolP.value" />',0,7);
           if (message != null) {
               hm.util.reportFieldError(displayErrorObj, message);
               inputElement.focus();
               return false;
           }
          var inputElement=document.getElementById("protocolDValue_"+i);
          if (inputElement.value.length == 0) {
               hm.util.reportFieldError(displayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.marking.protocolD.value" /></s:param></s:text>');
               inputElement.focus();
               return false;
           }
           var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.qos.marking.protocolD.value" />',0,63);
           if (message != null) {
               hm.util.reportFieldError(displayErrorObj, message);
               inputElement.focus();
               return false;
           }
      }      
      
      return true;
}

function onLoadPage() {
	displayErrorObj = document.getElementById("displayErrorInfo");
	var chboxP=<s:property value="%{chboxP}"/>;
	initCheckBox("p",chboxP);
    var chboxD=<s:property value="%{chboxD}"/>;
	initCheckBox("d",chboxD);
}
function initCheckBox(obj,bln)
{
   if(obj=="p")
      setChecked("protocolPValue_",bln);  
    if(obj=="d")
      setChecked("protocolDValue_",bln);
}
function isDisabledAll(value){
   var obj=document.getElementById(value);
   if(value=="pP")
      setChecked("protocolPValue_",obj.checked);
   else
   if(value=="pD")
      setChecked("protocolDValue_",obj.checked);    
}
function setChecked(name,bln)
{
   var obj;
   for(var i=0;i<8;i++)
   {
      obj=document.getElementById(name+i);
      obj.disabled=!bln;
   }
}

function initValues(value){
   var obj=document.getElementById(value);
   if(!obj.checked){
     if(value=="pP")
      for(var i=0;i<8;i++)
      {
         var obj=document.getElementById("protocolPValue_"+i);  
                
            if(i==0)
               obj.value="7";
            if(i==1)
               obj.value="6";  
            if(i==2)
               obj.value="5";
            if(i==3)
               obj.value="4";  
            if(i==4)
               obj.value="3";
            if(i==5)
               obj.value="0";  
            if(i==6)
               obj.value="2";
            if(i==7)
               obj.value="1";  
      } 
     if(value=="pD")
      for(var i=0;i<8;i++)
      {
         var obj=document.getElementById("protocolDValue_"+i);  
                
            if(i==0)
               obj.value="56";
            if(i==1)
               obj.value="48";  
            if(i==2)
               obj.value="40";
            if(i==3)
               obj.value="32";  
            if(i==4)
               obj.value="24";
            if(i==5)
               obj.value="0";  
            if(i==6)
               obj.value="16";
            if(i==7)
               obj.value="8";  
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="qosMarking" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="displayName" />\'</td>');
		</s:else>
	</s:else>
}

</script>

<div id="content"><s:form action="qosMarking" id="qosMarking">
	<s:if test="%{jsonMode==true}">
		<s:hidden name="jsonMode" />
		<s:hidden name="operation" />
		<s:hidden name="id" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-Marker-Maps.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.title.marking"/>
									</s:if>
									<s:else>
										<s:text name="config.title.marking.edit"/>
									</s:else>
								</td>
								<td style="padding-left:10px;">
									<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
										<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
											alt="" class="dblk"/>
									</a>
								</td>
							</tr>
						</table>
						</td>
						<td align="right">
						<s:if test="%{!parentIframeOpenFlg}">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton">
								<s:if test="%{dataSource.id == null}">
									<s:if test="%{writeDisabled == 'disabled'}">
										<a href="javascript:void(0);" class="btCurrent" onclick="return false;" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:else>
								</s:if>
								<s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<a href="javascript:void(0);" class="btCurrent" onclick="return false;" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:else>
								</s:else>
							</tr>
						</table>
						</s:if>
						<s:else>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<s:if test="%{dataSource.id == null}">
									<s:if test="%{writeDisabled != 'disabled'}">
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
									</s:if>
								</s:if>
								<s:else>
									<s:if test="%{updateDisabled != 'disabled'}">
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
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
		</table>
		</div>
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
								onClick="submitAction('<%=Navigation.L2_FEATURE_QOS_MARKING%>');">
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
		</table>
	</s:else>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="780">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="780">
			</s:else>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding: 10px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.qos.classification.name" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="dataSource.qosName" size="24"
								maxlength="%{nameLength}" id="qosName"	disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
								<s:text name="config.name.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.qos.classification.description" /></label></td>
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" /> 
								<s:text name="config.description.range"/></td>
						</tr>
						
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding: 0px 4px 0 4px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="sepLine"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
				   <td style="padding: 0px 4px 0 6px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
					     <td class="noteInfo" colspan="2" style="padding-left: 15px;">
					       <s:text  name="Note: The egress wireless interface is marked with 802.11e automatically." />
						 </td>
			            </tr>
			            <tr>
					     <td  align="left" height="1" style="padding: 0px 0px 0 16px;">
					        <s:hidden name="displayErrorInfo" id="displayErrorInfo"/>
					     </td>
					     <td width="100%" height="1">
					     </td>
			            </tr>
					</table>
					</td>
				</tr>
				<tr>
					<td align="left" style="padding: 4px 5px 1px 20px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td style="padding-right:30px;" class="borderRight">
							<table cellspacing="0" cellpadding="0" border="0">
							    
								<tr>
								    <td><s:checkbox name="chboxP" id="pP" disabled="%{disableQosClass}"
									    onclick="isDisabledAll('pP');initValues('pP');" />
									     <s:text name="config.qos.protocolP"/></td>
							    </tr>
								<tr>
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.class.name" /></th>
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.wmm.name"/></th>
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.protocolP.value"/></th>
								</tr>
								<s:iterator id="protocolP_id" value="%{protocolD.values()}"	status="status">
									<tr class="list" align="center">										
										<td class="list" align="left"><s:property value="%{key}"/></td>
										<td class="list" align="left"><s:property value="%{value}"/></td>
										<td class="list" align="left"><s:textfield size="10"
										    name="protocolPValue" 	id="protocolPValue_%{#status.index}" 
											value="%{protocolPValue[#status.index]}" maxlength="1"
										    disabled="%{disableQosClass}"  onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>										
									</tr>
								</s:iterator>
								<tr>
					               <td height="4"></td>
				                </tr>
							</table>
							</td>
							<td style="padding-left:30px;padding-right:30px;" >
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><s:checkbox name="chboxD" id="pD" disabled="%{disableQosClass}"
									    onclick="isDisabledAll('pD');initValues('pD');"/>
									     <s:text name="config.qos.protocolD"/></td>
								</tr>
								<tr>									
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.class.name"/></th>
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.wmm.name"/></th>
									<th align="left" style="padding-top:5px;"><s:text
										name="config.qos.marking.protocolD.value" /></th>
								</tr>
								<s:iterator id="protocolD_id" value="%{protocolD.values()}"	status="status">
									<tr class="list" align="center">									
										<td class="list" align="left"><s:property value="%{key}"/></td>
										<td class="list" align="left"><s:property value="%{value}"/></td>
										<td class="list" align="left"><s:textfield  size="10"
										    name="protocolDValue" id="protocolDValue_%{#status.index}" 
										    value="%{protocolDValue[#status.index]}" maxlength="2"
										    disabled="%{disableQosClass}"  onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>	
									</tr>
								</s:iterator>
								<tr>
					               <td height="4"></td>
				                </tr>
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
