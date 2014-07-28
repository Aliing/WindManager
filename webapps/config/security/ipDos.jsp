<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'ipDos';
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_dosPreventionName").disabled == false) {
		document.getElementById(formName + "_dataSource_dosPreventionName").focus();
	}
	if (document.forms[formName].operation.value =='new') {
		initValue();
	}
	initTextLength();
}

function initTextLength() {
	document.getElementById("alarmThreshold_0").maxLength=3;
	document.getElementById("alarmThreshold_1").maxLength=3;
	document.getElementById("alarmThreshold_2").maxLength=7;
	document.getElementById("alarmThreshold_3").maxLength=7;
	document.getElementById("alarmThreshold_4").maxLength=5;
	document.getElementById("alarmThreshold_5").maxLength=5;
	document.getElementById("alarmThreshold_6").maxLength=2;
	document.getElementById("alarmThreshold_7").maxLength=4;
	for (var i=0;i<8;i++) {
		var inputElement = document.getElementById("dosActionTime_" + i);
		var inputElementAction = document.getElementById("dosAction_" + i);
		if (inputElementAction.value =='DISCONNECT' || inputElementAction.value =='BAN_FOREVER') {
			inputElement.readOnly = true;
		}
	}
}

function saveIpDos(operation){
	 if (operation == 'create') {
	 	 url = "<s:url action='ipDos' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	 } else if (operation == 'update'){
		 var id;
		 <s:if test="%{dataSource.id != null}">
		     id = <s:property value="dataSource.id"/>; 
		 </s:if>
	 	 url = "<s:url action='ipDos' includeParams='none' />?jsonMode=true&id="+id+"&ignore="+new Date().getTime();
	 }
	document.forms["ipDos"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("ipDos"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succNewIpDos, failure : resultDoNothing, timeout: 60000}, null);	

}

var succNewIpDos = function(o) {
	try {
		eval("var details = " + o.responseText);
		var notesTimeout;
		var errorRow = new Object();
		errorRow.id='ErrorRow';
		if (details.error) {
			hm.util.reportFieldError(errorRow, details.msg);
			return;
		}
		
		if(details.t){
			if(details.id != null && details.name != null){
				if("create" == details.option){
					if(parent.selectUIElement) { 
						dynamicAddSelect(parent.selectUIElement,details.name,details.id);
					}
			   }else if("update" == details.option){
					if(parent.selectUIElement) {
						parent.selectUIElement.value = details.id;
					}
				 
			   }
			} 
		}
		parent.closeIFrameDialog();
		
		
	}catch(e){
		alert("error"+e);
		return;
	}
}
var resultDoNothing = function(o){
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
	    if ('cancel' == operation) {
			parent.closeIFrameDialog();	
		} else{
			saveIpDos(operation);
		}
		</s:if>
		<s:else>
		    document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
	}
}
function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>' || operation == 'cancel') {
		initValue();
		return true;
	}
	if (!validateDosName()) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_0",1,100)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_1",1,100)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_2",1,1000000)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_3",1,1000000)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_4",1,10000)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_5",1,10000)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_6",2,10)) {
		return false;
	}
	
	if (!validateThreshold("alarmThreshold_7",1,3600)) {
		return false;
	}
	
	if (!validateTextValue()) {
		return false;
	}
	
	return true;
}

function validateDosName() {
    var inputElement = document.getElementById(formName + "_dataSource_dosPreventionName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.security.ipdos.ipDosName" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}

    return true;
}

function validateThreshold(strId,minValue,maxValue) {
	var inputElement = document.getElementById(strId);
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.security.ipdos.threshold" /></s:param></s:text>');
		inputElement.focus();
		return false;
	}
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.ipdos.threshold" />',minValue,maxValue);
     if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		inputElement.focus();
		return false;
     }
     return true;
}

function validateTextValue() {

	 for (var i=0;i<8;i++) {
		var inputElement = document.getElementById("dosActionTime_" + i);
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.security.ipdos.duration" /></s:param></s:text>');
			inputElement.focus();
			return false;
		}
		var message
		if(i ==7) {
	     	message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.ipdos.duration" />',1,100000000);
	    } else {
	     	message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.ipdos.duration" />',1,1024000);
	    }
	    if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			inputElement.focus();
			return false;
	     }
	 }   
     return true;
}

function initValue() {

    for (var i=0;i<8;i++) {
        var alarmInterval_value=document.getElementById("dosActionTime_" + i);
        var enabled_value=document.getElementById("enabled_" + i);
        alarmInterval_value.value="10";
        enabled_value.checked = false;
    } 
	document.getElementById("alarmThreshold_0").value="20";
	document.getElementById("alarmThreshold_1").value="50";
	document.getElementById("alarmThreshold_2").value="1000";
	document.getElementById("alarmThreshold_3").value="100";
	document.getElementById("alarmThreshold_4").value="100";
	document.getElementById("alarmThreshold_5").value="100";
	document.getElementById("alarmThreshold_6").value="3";
	document.getElementById("alarmThreshold_7").value="5";
}

function setActionTimeEnabled(index) {
    var dosAction=document.getElementById("dosAction_" + index);
    var dosActionTime=document.getElementById("dosActionTime_" + index);
    if (dosAction.value=='DISCONNECT' || dosAction.value=='BAN_FOREVER') {
		dosActionTime.readOnly=true;
    } else {
    	dosActionTime.readOnly=false;
    }
    if (dosAction.value=='ALARM') {
    	dosActionTime.value="10";
    }
    if (dosAction.value=='DROP') {
    	dosActionTime.value="1";
    }
    if (dosAction.value=='BAN') {
    	dosActionTime.value="3600";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ipDos" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedDosPreventionName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedDosPreventionName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>	
}

</script>

<div id="content"><s:form action="ipDos" id="ipDos">
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
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
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<s:else>
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0"  width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_DOS.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.networkpolicy.ipdos.dialog.new.title"/>
								</s:if> <s:else>
									<s:text name="config.networkpolicy.ipdos.dialog.edit.tile"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
									<s:if test="%{'' == writeDisabled}">
										<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
									</s:if>
								</td>
							</s:if>
							<s:else>
								<td class="npcButton">
									<s:if test="%{'' == updateDisabled}">
										<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
									</s:if>
								</td>
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
		<tr id="fe_ErrorRow" style="display: none">
			<td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table  cellspacing="0" cellpadding="0" border="0" width="700">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700">
			</s:else>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="112px"><label><s:text
									name="config.security.ipdos.ipDosName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.dosPreventionName"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="24" maxlength="%{ipDosNameLength}" disabled="%{disabledName}"/>
									&nbsp;<s:text name="config.ipFilter.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.security.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" /> <s:text
									name="config.security.description.range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>	
					<td style="padding:0 4px 6px 8px">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">

						</tr>
						<tr>
							<th align="left"><s:text
								name="config.security.ipdos.screenType" /></th>
							<th align="left"><s:text
								name="config.security.ipdos.threshold" /></th>
							<th align="left"><s:text name="config.security.ipdos.action" /></th>
							<th align="left"><s:text
								name="config.security.ipdos.duration" /></th>
							<th align="center"><s:text
								name="config.security.dos.isEnable" /></th>
						</tr>
						<s:iterator id="dosParams"
							value="%{dataSource.dosParamsMap.values()}" status="status">
							<tr class="list">
								<td class="list"><s:property value="%{value}" /></td>
								<td class="list"><s:textfield name="alarmThreshold"
									value="%{alarmThreshold}" 
									size="10"
									id="alarmThreshold_%{#status.index}" 
									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									<s:if test="%{#status.index==0 ||#status.index==1}">
										<s:text name="config.security.ipdos.unit.airTime" />
									</s:if>
									<s:elseif test="%{#status.index==2 ||#status.index==3}"> 	
										<s:text name="config.security.ipdos.unit.packets" />
									</s:elseif>	
									<s:elseif test="%{#status.index==4 ||#status.index==5}"> 	
										<s:text name="config.security.ipdos.unit.milliseconds" />
									</s:elseif>	
									<s:elseif test="%{#status.index==6}"> 	
										<s:text name="config.security.ipdos.unit.srcIpMac" />
									</s:elseif>	
									<s:else> 	
										<s:text name="config.security.ipdos.unit.rejects" />
									</s:else>
									</td>
								<s:if test="%{#status.index==7}">	
									<td class="list"><s:select name="dosAction" id ="dosAction_%{#status.index}" 
										value="%{dosAction}" list="dosActionValuesReject" listKey="key"
										listValue="value" onchange="setActionTimeEnabled(%{#status.index});"/></td>
									<td class="list">
										<s:textfield name="dosActionTime" value="%{dosActionTime}"  id="dosActionTime_%{#status.index}" 
										maxlength="9" size="15"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									</td>
								</s:if>
								<s:else> 
									<td class="list"><s:select name="dosAction" id ="dosAction_%{#status.index}" 
										value="%{dosAction}" list="dosActionValues" listKey="key"
										listValue="value" onchange="setActionTimeEnabled(%{#status.index});"/></td>
									<td class="list">
										<s:textfield name="dosActionTime" value="%{dosActionTime}"  id="dosActionTime_%{#status.index}" 
										maxlength="7" size="15"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									</td>
								</s:else>	
								<td class="listCheck" align="center"><s:checkbox
									name="enabled" id ="enabled_%{#status.index}" fieldValue="%{#status.index}" /></td>
							</tr>
						</s:iterator>
							<tr class="list">
									<td class="list" colspan="2"><s:text name="config.security.ipdos.enabledSynCheck" /></td>
									<td class="list" colspan="2"><FONT color="#003366"><s:text name="config.security.ipdos.synCheckNote" /></FONT></td>
									<td class="listCheck" align="center"><s:checkbox
										name="dataSource.enabledSynCheck"
										value="%{dataSource.enabledSynCheck}" /></td>
							</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
