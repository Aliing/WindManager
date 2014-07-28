<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<tiles:insertDefinition name="tabView" />
<script>
var formName = 'qosRateControl';
var diaplayErrorObj;
var weightArray;
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
<s:if test="%{jsonMode == true}">
function saveQosRateControlJsonDlg(operation) {
	if (validate(operation)) {
		var url = "<s:url action='qosRateControl' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["qosRateControl"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveQosRateControlJsonDlg, failure : failSaveQosRateControlJsonDlg, timeout: 60000}, null);
	}
}
var succSaveQosRateControlJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}
var failSaveQosRateControlJsonDlg = function(o) {
	//do nothing now.
}
</s:if>
function validate(operation) {
    if (operation == "<%=Navigation.L2_FEATURE_QOS_RATE_CONTROL%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_rateLimit").value=54000;
		document.getElementById(formName + "_dataSource_rateLimit11n").value=1000000;
		document.getElementById(formName + "_dataSource_rateLimit11ac").value=1000000;
		for(var i=0;i<8;i++) {
          document.getElementById("schedulingWeight_"+i).value=0;
          document.getElementById("policingRateLimit_"+i).value=0;
          document.getElementById("policing11nRateLimit_"+i).value=0;
          document.getElementById("policing11acRateLimit_"+i).value=0;
       }
		return true;
	}
    var inputElement;
    if(operation=='create' || operation=='create<s:property value="lstForward"/>'){
       inputElement=document.getElementById("qosName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.qos.classification.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }  
    }
    if(!validateValueRange())
       return false;
	return true;
}
function validateValueRange() {
       var obj_rate=document.getElementById(formName + "_dataSource_rateLimit");
       if (obj_rate.value.length == 0) {
           hm.util.reportFieldError(obj_rate, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.rateLimit.abg" /></s:param></s:text>');
           obj_rate.focus();
           return false;
       }
       var message = hm.util.validateIntegerRange(obj_rate.value, '<s:text name="config.qos.rateControl.rateLimit.abg" />',
                                                       <s:property value="%{rateLimitRange.min()}" />,
                                                       <s:property value="%{rateLimitRange.max()}" />);
       if (message != null) {
           hm.util.reportFieldError(obj_rate, message);
           obj_rate.focus();
           return false;
       }
       
       var obj_rate_11n=document.getElementById(formName + "_dataSource_rateLimit11n");
       if (obj_rate_11n.value.length == 0) {
           hm.util.reportFieldError(obj_rate_11n, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.rateLimit.11n" /></s:param></s:text>');
           obj_rate_11n.focus();
           return false;
       }
       var message11n = hm.util.validateIntegerRange(obj_rate_11n.value, '<s:text name="config.qos.rateControl.rateLimit.11n" />',
                                                       <s:property value="%{rateLimit11nRange.min()}" />,
                                                       <s:property value="%{rateLimit11nRange.max()}" />);
       if (message11n != null) {
           hm.util.reportFieldError(obj_rate_11n, message11n);
           obj_rate_11n.focus();
           return false;
       }
       
       var obj_rate_11ac=document.getElementById(formName + "_dataSource_rateLimit11ac");
       if (obj_rate_11ac.value.length == 0) {
           hm.util.reportFieldError(obj_rate_11ac, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.rateLimit.11ac" /></s:param></s:text>');
           obj_rate_11ac.focus();
           return false;
       }
       var message11ac = hm.util.validateIntegerRange(obj_rate_11ac.value, '<s:text name="config.qos.rateControl.rateLimit.11ac" />',
                                                       <s:property value="%{rateLimit11acRange.min()}" />,
                                                       <s:property value="%{rateLimit11acRange.max()}" />);
       
       if (message11ac != null) {
           hm.util.reportFieldError(obj_rate_11ac, message11ac);
           obj_rate_11ac.focus();
           return false;
       }
       
      for(var i=0;i<8;i++)
      {
          var inputElement=document.getElementById("schedulingWeight_"+i);
           if (inputElement.value.length == 0) {
               hm.util.reportFieldError(diaplayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.scheduling" /></s:param></s:text>');
               inputElement.focus();
               return false;
           }
           var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.qos.rateControl.scheduling" />',0,1000);
           if (message != null) {
               hm.util.reportFieldError(diaplayErrorObj, message);
               inputElement.focus();
               return false;
           }
          var inputElement=document.getElementById("policingRateLimit_"+i);
          if (inputElement.value.length == 0) {
               hm.util.reportFieldError(diaplayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.policing" /></s:param></s:text>');
               inputElement.focus();
               return false;
           }
           var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.qos.rateControl.policing" />',0,parseInt(obj_rate.value));
           if (message != null) {
               hm.util.reportFieldError(diaplayErrorObj, message);
               inputElement.focus();
               return false;
           }
          //Validate the 11n 
          var inputElement11n=document.getElementById("policing11nRateLimit_"+i);
          if (inputElement11n.value.length == 0) {
               hm.util.reportFieldError(diaplayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.policing11n" /></s:param></s:text>');
               inputElement11n.focus();
               return false;
           }
           var message11n = hm.util.validateIntegerRange(inputElement11n.value, '<s:text name="config.qos.rateControl.policing11n" />',0,parseInt(obj_rate_11n.value));
           if (message11n != null) {
               hm.util.reportFieldError(diaplayErrorObj, message11n);
               inputElement11n.focus();
               return false;
           }
           //Validate the 11ac
           var inputElement11ac=document.getElementById("policing11acRateLimit_"+i);
           if (inputElement11ac.value.length == 0) {
                hm.util.reportFieldError(diaplayErrorObj, '<s:text name="error.requiredField"><s:param><s:text name="config.qos.rateControl.policing11ac" /></s:param></s:text>');
                inputElement11ac.focus();
                return false;
            }
            var message11ac = hm.util.validateIntegerRange(inputElement11ac.value, '<s:text name="config.qos.rateControl.policing11ac" />',0,parseInt(obj_rate_11ac.value));
            if (message11ac != null) {
                hm.util.reportFieldError(diaplayErrorObj, message11ac);
                inputElement11ac.focus();
                return false;
            }
      }      
      
      return true;
}

function changeWeightPer()
{
      var inputElement;
      var array=new Array();
       var values=0; 
      for(var i=0;i<8;i++)
      {
         inputElement= document.getElementById("schedulingWeight_"+i);
         array[i]=parseInt(inputElement.value);
         values=parseInt(values)+parseInt(inputElement.value);
      }
      for(var i=0;i<8;i++)
      {
         inputElement= document.getElementById("weightPer_"+i);
         inputElement.value=calculate(array[i],values);
      }     
}
function calculate(value,values)
{
   var v=value*100;   
   if(values==0)
     return 0;
   else
      return parseInt(parseInt(parseInt(value)*100)/parseInt(values));
}
function enableSchedulingWeight(index,value)
{
   var obj=document.getElementById("schedulingWeight_"+index);
   var weight=obj.value;
   obj.value=weightArray[index];
   weightArray[index]=weight;
   if(value==1){
      obj.readOnly=true;  
   }   
   if(value==2)
      obj.readOnly=false;
      changeWeightPer();
}
function onLoadPage() {
   diaplayErrorObj=document.getElementById('displayErrorInfo');
   weightArray=new Array(8);
   weightArray=[0,0,0,0,0,0,0,0];
   <s:if test="%{jsonMode == true}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(1000,630);
		}
   </s:if>
   adjustKeyPressForReadonlyInput();
}

function adjustKeyPressForReadonlyInput() {
	$("input[readonly]").keydown(function(e){
		var keyCode = e.keyCode ? e.keyCode : e.which;
		if (keyCode === 8) {
			return false;
		}
		return true;
	});
}

function compareValue(value)
{
      var obj=document.getElementById("rateLimit");
      if(parseInt(value)>parseInt(obj.value))      
             return false;
      return true;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="qosRateControl" ></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
</script>
<div id="content"><s:form action="qosRateControl">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="vpnNetworkTitleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-qos-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.rateControl"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.rateControl.edit"/></td>
						</s:else>
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
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{writeDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="qosRateControlSaveBtnId" onclick="saveQosRateControlJsonDlg('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="qosRateControlSaveBtnId" onclick="saveQosRateControlJsonDlg('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
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
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="qosRateControlSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled != 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="qosRateControlSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
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
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
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
						<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_QOS_RATE_CONTROL%>');">
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
				<table cellspacing="0" cellpadding="0" border="0" width="900">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="900">
			</s:else>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding: 10px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="140"><label><s:text
								name="config.qos.classification.name" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="dataSource.qosName" size="24"
								maxlength="%{nameLength}" id="qosName"	disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
								<s:text name="config.name.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="140"><label><s:text
								name="config.qos.rateControl.rateLimit" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield name="dataSource.rateLimit" size="24"
								maxlength="5" onkeypress="return hm.util.keyPressPermit(event,'ten');"/> 
								<s:text name="config.rate.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="140"></td>
							<td style="padding: 2px 0"><s:textfield name="dataSource.rateLimit11n" size="24"
								maxlength="7" onkeypress="return hm.util.keyPressPermit(event,'ten');"/> 
								<s:text name="config.rate.range11n"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="140"></td>
							<td style="padding: 2px 0"><s:textfield name="dataSource.rateLimit11ac" size="24"
								maxlength="7" onkeypress="return hm.util.keyPressPermit(event,'ten');"/> 
								<s:text name="config.rate.range11ac"/></td>
						</tr>						
						<tr>
							<td class="labelT1"><label><s:text
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
					<td align="left" style="padding: 4px 5px 6px 5px;">
						<fieldset><legend><s:text
							name="config.qos.rateControl.table.title" /></legend>
							<table cellspacing="0" cellpadding="0" border="0">				   
								<tr>
					              <td colspan="7" align="left">
					                <s:hidden name="displayErrorInfo" id="displayErrorInfo"/>
					              </td>
			                    </tr>
								<tr>
									<th align="left"><s:text
										name="config.qos.rateControl.className" /></th>
									<th align="left"><s:text
										name="config.qos.rateControl.type" /></th>
									<th align="left"><s:text
										name="config.qos.rateControl.scheduling" /></th>
									<th align="left" width="100"><s:text
										name="config.qos.rateControl.weightPer" /></th>
									<th align="left"><s:text
										name="config.qos.rateControl.policing" /></th>
									<th align="left"><s:text
										name="config.qos.rateControl.policing11n" /></th>
									<th align="left"><s:text
										name="config.qos.rateControl.policing11ac" /></th>
								</tr>
								<s:iterator id="qosClass"
									value="qosClass.values()" status="status">
									<tr class="list" align="left">
										<td class="list" align="left"><s:textfield readonly="true"
											name="qosClassName" value="%{qosClassName[#status.index]}" 
											id="qosClassName_%{#status.index}" /></td>
										<td class="list" align="left"><s:select
											name="schedulingTypeName" value="%{schedulingTypeName[#status.index]}"
											list="%{enumSchedulingType}" listKey="key" listValue="value"
											id="schedulingTypeName_%{#status.index}" 
											onchange="enableSchedulingWeight('%{#status.index}',this.value);"/></td>
										<td class="list" align="left"><s:textfield
											name="schedulingWeight" id="schedulingWeight_%{#status.index}" size="5" 
											value="%{schedulingWeight[#status.index]}" 
											onkeypress="return hm.util.keyPressPermit(event,'ten');"
											onkeyup="changeWeightPer();" maxlength="4"
											readonly="%{weightDisabled[#status.index]}"/></td>
										<td class="list" align="left" ><s:textfield
											name="weightPer" value="%{weightPer[#status.index]}" id="weightPer_%{#status.index}"
											size="2" readonly="true"/> %</td>
										<td class="list" align="left"><s:textfield
											name="policingRateLimit" id="policingRateLimit_%{#status.index}" size="10"
											value="%{policingRateLimit[#status.index]}" maxlength="5"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>
										<td class="list" align="left"><s:textfield
											name="policing11nRateLimit" id="policing11nRateLimit_%{#status.index}" size="10"
											value="%{policing11nRateLimit[#status.index]}" maxlength="7"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>
										<td class="list" align="left"><s:textfield
											name="policing11acRateLimit" id="policing11acRateLimit_%{#status.index}" size="10"
											value="%{policing11acRateLimit[#status.index]}" maxlength="7"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"/></td>
									</tr>
								</s:iterator>
								<tr>
									<td height="5"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
