<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'macDos';
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_dosPreventionName").disabled == false) {
		document.getElementById(formName + "_dataSource_dosPreventionName").focus();
	}
	if (document.forms[formName].operation.value =='new') {
		initValue();
	}

	initTextEnabled();
	
	<s:if test="%{jsonMode}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(800, 550);
		}
	</s:if>
}

function initTextEnabled() {
	for (var i=0;i<8;i++) {
		var inputElement = document.getElementById("dosActionTime_" + i);
		if (i == 0 || i ==1 || i ==3 || i ==4 || i ==6) {
			inputElement.readOnly = true;
		} else {
			if (document.getElementById("banForever_" + i).checked) {
				inputElement.readOnly = false;
			} else {
				inputElement.readOnly = true;
			}
		}
	}
}

function saveMacDos(operation){
/**
	var id = null;
	var radioMacDosmac=document.getElementById(formName + "_radioMacDosmac").checked;
	var radioMacDosstation=document.getElementById(formName + "_radioMacDosstation").checked;
	
	var macDosId = document.getElementById("macDos_parentDomID").value;
	if(radioMacDosmac){
		if(parent.document.getElementById(macDosId).value){
			id = parent.document.getElementById(macDosId).value;
		}
	}else if(radioMacDosstation){
		if(parent.document.getElementById(macDosId).value){
			id = parent.document.getElementById(macDosId).value;
		}
	}
	**/
	var url = "<s:url action='macDos' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	document.forms["macDos"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("macDos"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succNewMacDos, failure : resultDoNothing, timeout: 60000}, null);	
}

var succNewMacDos = function(o) {
	try {
		eval("var details = " + o.responseText);
		if(details.error){
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		}else{
			if(details.id != null && details.name != null){
				if('create<s:property value="lstForward"/>' == details.option){
					if(details.parentDomID){
						var ssidDosSelect = parent.document.getElementById(details.parentDomID);
						dynamicAddSelect(ssidDosSelect,details.name,details.id);
					}
			   }else if('update<s:property value="lstForward"/>' == details.option){
				   if(details.parentDomID){
					   parent.document.getElementById(details.parentDomID).value = details.id;
				   }
			   }
			} 
		}
		parent.closeIFrameDialog();
	}catch(e){
		//alert("error");
		return;
	}
}
var resultDoNothing = function(o){
}

function submitAction(operation) {
	if (validate(operation)) {
	    <s:if test="%{jsonMode && !parentIframeOpenFlg}">
		    if (operation == 'cancel' + '<s:property value="lstForward"/>') {
				parent.closeIFrameDialog();	
			} else{
				saveMacDos(operation);
			}
		</s:if>
		<s:else>
		    if (operation != 'create<s:property value="lstForward"/>') {
				showProcessing();
			}
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

	if (!validateTextValue()) {
		return false;
	}
	return true;
}

function validateDosName() {
    var inputElement = document.getElementById(formName + "_dataSource_dosPreventionName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.security.macdos.ssidDosName" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateTextValue() {
	 for (var i=0;i<8;i++) {
		var inputElement = document.getElementById("alarmThreshold_" + i);
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.security.macdos.alarmThreshold" /></s:param></s:text>');
			inputElement.focus();
			return false;
		}
	     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.macdos.alarmThreshold" />',0,2000000000);
	     if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			inputElement.focus();
			return false;
	     }

		var inputElement = document.getElementById("alarmInterval_" + i);
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.security.macdos.alarmInterval" /></s:param></s:text>');
			inputElement.focus();
			return false;
		}
	     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.macdos.alarmInterval" />',0,2000000000);
	     if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			inputElement.focus();
			return false;
	     }

		var inputElement = document.getElementById("dosActionTime_" + i);
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.security.macdos.block" /></s:param></s:text>');
			inputElement.focus();
			return false;
		}
	     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.security.macdos.block" />',0,2000000000);
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
        var alarmInterval_value=document.getElementById("alarmInterval_" + i);
        var enabled_value=document.getElementById("enabled_" + i);
        alarmInterval_value.value="60";
        enabled_value.checked = true;
    }
    var radioMacDosmac=document.getElementById(formName + "_radioMacDosmac").checked;
    var radioMacDosstation=document.getElementById(formName + "_radioMacDosstation").checked;
  	if(radioMacDosmac)
  	{
    	for (var i=0;i<8;i++) {
        	var alarmThreshold_value=document.getElementById("alarmThreshold_" + i);
        	if (i ==0 ) {
        		alarmThreshold_value.value="12000";
        	} else if ( i == 1) {
        		alarmThreshold_value.value="24000";
        	} else if ( i == 3) {
        		alarmThreshold_value.value="2400";
        	} else if (i == 4 || i == 6) {
            	alarmThreshold_value.value="1200";
        	} else {
        		alarmThreshold_value.value="6000";
        	}
    	}
  	}
  	if(radioMacDosstation)
  	{
    	for (var i=0;i<8;i++) {
       		var alarmThreshold_value=document.getElementById("alarmThreshold_" + i);
        	if (i ==0) {
        		alarmThreshold_value.value="1200";
        	} else if (i == 1) {
        		alarmThreshold_value.value="2400";
        	} else if (i == 3) {
        		alarmThreshold_value.value="240";
        	} else if (i == 4 || i == 6) {
            	alarmThreshold_value.value="120";
        	} else {
        		alarmThreshold_value.value="600";
        	}

        	var dosActionTime_value=document.getElementById("dosActionTime_" + i);
        	if (i ==2 || i == 5 || i == 7) {
        		dosActionTime_value.value="60";
        		document.getElementById("dosActionTime_" + i).readOnly = false;
        		document.getElementById("banForever_" + i).checked = true;
        	} else {
        		dosActionTime_value.value="0";
        	}
    	}
  	}
}

function disableBan(value,status){
	if (value) {
		document.getElementById("dosActionTime_" + status).readOnly = false;
	} else {
		document.getElementById("dosActionTime_" + status).readOnly = true;
		document.getElementById("dosActionTime_" + status).value=60;
	}
}

function showBlock(type)
{
	var table = document.getElementById("dosParamsTable");
	initValue();
  	var div_block_head=document.getElementById("actionTime_div_head");
  	div_block_head.style.display="none";
  	if(type=="mac")
  	{
		table.width = 670;
    	div_block_head.style.display="none";
  	}
  	if(type=="station")
  	{
		table.width = 700;
    	div_block_head.style.display="block";
  	}

 	for (var i=0;i<8;i++) {
    	var div_block=document.getElementById("actionTime_div_" + i);
  		if (div_block !=null && div_block !='undefined') {
  	  		div_block.style.display="none";
  	  		if(type=="ssid")
	  		{
	    		div_block.style.display="none";
	  		}
	  		if(type=="station")
	  		{
	    		div_block.style.display="block";
	  		}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="macDos" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

<div id="content"><s:form action="macDos" id="macDos">
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="id" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
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
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-MAC_Dos.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.networkpolicy.macdos.dialog.new.title"/>
								</s:if> <s:else>
									<s:text name="config.networkpolicy.macdos.dialog.edit.tile"/>
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
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
										<td width="20px">&nbsp;</td>
										<s:if test="%{dataSource.id == null}">
											<s:if test="%{writeDisabled == ''}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:if>
										</s:if>
										<s:else>
											<s:if test="%{updateDisabled == ''}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"   onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
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
			</td>
		</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="650"
					id="dosParamsTable">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="650"
					id="dosParamsTable">
			</s:else>
				<tr>
					<td>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="90px" nowrap="nowrap"><label><s:text
								name="config.security.macdos.ssidDosName" /><font color="red"><s:text name="*"/></font></label></td>
							<td width="310px" nowrap="nowrap"><s:textfield
								name="dataSource.dosPreventionName" size="24"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="%{dosNameLength}" disabled="%{disabledName}" />
								&nbsp;<s:text name="config.ipFilter.name.range" /></td>
							<td class="labelT1" width="60px" nowrap="nowrap"><s:text
								name="config.security.macdos.dosTypeName" />:</td>
							<td width="95px"><s:radio label="Gender" name="radioMacDos"
								list="%{macRadioType}" onclick="showBlock(this.value)"
								listKey="key" listValue="value"
								disabled="%{disabledRadioButton}"/></td>
							<td width="80px"><s:radio label="Gender" name="radioMacDos"
								list="#{'station':'Station'}" onclick="showBlock(this.value)"
								disabled="%{disabledRadioButton}"/></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.security.description" /></td>
							<td colspan="4"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" /> <s:text
								name="config.security.description.range" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding:0 4px 6px 8px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>

							<th align="left" style="display:<s:property value="block"/>"
								id="dosType_div_head" width="145px"><s:text
								name="config.security.macdos.dosType" /></th>
							<th align="left" style="display:<s:property value="block"/>"
								id="alarmThreshold_div_head" width="140px"><s:text
								name="config.security.macdos.alarmThreshold" /></th>
							<th align="left" style="display:<s:property value="block"/>"
								id="alarmInterval_div_head" width="120px"><s:text
								name="config.security.macdos.alarmInterval" /></th>
							<th align="left"
								style="display:<s:property value="%{actionTime_div}"/>"
								id="actionTime_div_head" width="125px"><s:text
								name="config.security.macdos.block" /></th>
							<th align="center" style="display:<s:property value="block"/>"
								id="enable_div_head"><s:text
								name="config.security.dos.isEnable" /></th>
						</tr>
						<s:iterator id="dosParams"
							value="%{dataSource.dosParamsMap.values()}" status="status">
							<tr class="list">
								<td class="list" style="display:<s:property value="block"/>"
									id="dosType_div"><s:property value="%{value}" /></td>
								<td class="list" style="display:<s:property value="block"/>"
									id="alarmThreshold_div"><s:textfield name="alarmThreshold"
									value="%{alarmThreshold}" id="alarmThreshold_%{#status.index}"
									maxlength="10" size="10"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
								<td class="list" style="display:<s:property value="block"/>"
									id="alarmInterval_div"><s:textfield name="alarmInterval"
									value="%{alarmInterval}" id="alarmInterval_%{#status.index}"
									maxlength="10" size="10"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
								<td class="list"
									style="display:<s:property value="%{actionTime_div}"/>"
									id="actionTime_div_<s:property value="%{#status.index}"/>" nowrap="nowrap">
									<s:if test="%{#status.index ==2 || #status.index ==5 || #status.index ==7}">
										<s:checkbox name="banForever%{#status.index}" id="banForever_%{#status.index}"
											onclick="disableBan(this.checked,%{#status.index});" title="Not checked means forever"/>
										<s:textfield name="dosActionTime" value="%{dosActionTime}"
											id="dosActionTime_%{#status.index}" maxlength="10" size="10"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									</s:if>
									<s:else>
									<s:textfield name="dosActionTime" value="%{dosActionTime}"
										id="dosActionTime_%{#status.index}" maxlength="10" size="13"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />
									</s:else>
								</td>
								<td class="listCheck" align="center"
									style="display:<s:property value="block"/>" id="enable_div"><s:checkbox
									name="enabled" id="enabled_%{#status.index}"
									fieldValue="%{#status.index}" /></td>
							</tr>

						</s:iterator>
					</table>
					</td>
				</tr>
			</table>
		</td>
		</tr>
	</table>
</s:form></div>
