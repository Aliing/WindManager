<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'vlanGroup';

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_vlanGroupName").disabled == false) {
		document.getElementById(formName + "_dataSource_vlanGroupName").focus();
	}
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(810,400);
	 	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>'
				|| operation == 'update' + '<s:property value="lstForward"/>'
				|| operation == 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
		
			document.forms[formName].operation.value = operation;
	    	document.forms[formName].submit();


	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_VLAN_GROUP%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}

    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_vlanGroupName");
		var message = hm.util.validateName(name.value, '<s:text name="config.vlanGroup.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}


    if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update'+'<s:property value="lstForward"/>' 
    	|| operation == 'update' || operation == 'create') {
    	if(!validateVlans()){
    		return false;
    	}
    }
	return true;
}

function validateVlans(){
	var vlan = document.getElementById('vlanGroup_dataSource_vlans');
	if(vlan.value.length == 0){
		hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
		vlan.focus();
        return false;
	}
	var vlanList =vlan.value.split(',');
	var vlanValues = new Array();
	var vlanRangerValues = new Array();
	for(var j=0;j<vlanList.length;j++){
		var pattern = /^(\d+-)?\d+$/;
		if(!pattern.test(vlanList[j])){
			hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
			vlan.focus();
	        return false;
		}
		var vlans = vlanList[j].split('-');
		var numVlan0=Number(vlans[0]);
		var numVlan1=Number(vlans[1]);
		var pattern_value = /^[1-9]\d{0,2}$|^[1-3]\d{3}$|^40[0-8][0-9]$|^409[0-4]$/; //1-4094
		for(var i=0;i<vlans.length;i++) {
			if(!pattern_value.test(vlans[i])){
				hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
				vlan.focus();
		        return false;
			}
		}
	    if(vlans.length>1 && numVlan0 > numVlan1){
	    	hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
			vlan.focus();
	        return false;
		}
	    if(vlans.length>1){
	    	for(var i=0;i<=numVlan1-numVlan0;i++){
				if(vlanValues.contains(numVlan0+i)){
					hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
					vlan.focus();
			        return false;
				} else {
					vlanValues.push(numVlan0+i);
				}
			}
	    } else {
	    	if(vlanValues.contains(numVlan0)){
				hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
				vlan.focus();
		        return false;
			} else {
				vlanValues.push(numVlan0);
			}
	    }
		
		if(vlanRangerValues.contains(vlanList[j])){
			hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
			vlan.focus();
	        return false;
		} else {
			vlanRangerValues.push(vlanList[j]);
		}
	}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vlanGroup" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}

</script>
<div id="content"><s:form action="vlanGroup" name="vlanGroup" id="vlanGroup">
	<s:hidden name="parentDomID"/>
	<s:if test="%{jsonMode == true}">
	<s:hidden name="id" />
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<div class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0"  width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-vlans-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.vlanGroup"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.vlanGroup.edit"/></td>
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
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
								<s:if test="'' == writeDisabled">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
								</td>
							</s:if>
							<s:else>
								<td class="npcButton">
								<s:if test="%{'' == updateDisabled}">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
								</s:if>
							</s:else>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode==false}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<s:if test="%{dataSource.id == null}">
							<td><input type="button" name="create" value="<s:text name="button.create"/>"
								class="button"
								onClick="submitAction('create<s:property value="lstForward"/>');"
								<s:property value="writeDisabled" />>
							</td>
						</s:if>
						<s:else>
							<td><input type="button" name="update" value="<s:text name="button.update"/>"
								class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
								<s:property value="updateDisabled" />></td>
						</s:else>
						<s:if test="%{lstForward == null || lstForward == ''}">
							<td><input type="button" name="cancel" value="Cancel"
								class="button"
								onClick="submitAction('<%=Navigation.L2_FEATURE_VLAN_GROUP%>');">
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
	</s:if>
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
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="700">
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.vlanGroup.name" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield size="24"
								name="dataSource.vlanGroupName" maxlength="%{vlanGroupNameLength}"
								disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
								name="config.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.wlanAccess.scheduler.description" /></label></td>
							<td><s:textfield name="dataSource.description"
								size="60" maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.vlanGroup.vlans" /></label></td>
							<td><s:textfield name="dataSource.vlans" onkeypress="return hm.util.keyPressPermit(event,'attribute');"/> 
								<s:text name="config.vlanGroup.vlans.range"/></td>
						</tr>
						<tr>
							<td colspan="2" class="noteInfo" style="padding-left: 10px;"><s:text name="config.vlanGroup.valns.note"/></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>
