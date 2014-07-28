<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.NetworkService"%>

<script>
var PROTOCOL_ID_TCP = <%=NetworkService.PROTOCOL_ID_TCP%>
var	PROTOCOL_ID_UDP = <%=NetworkService.PROTOCOL_ID_UDP%>
var PROTOCOL_ID_SVP = <%=NetworkService.PROTOCOL_ID_SVP%>
var PROTOCOL_ID_CUSTOM = <%=NetworkService.PROTOCOL_ID_CUSTOM%>


var formName = 'networkService';
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function saveNetworkServiceJson(operation) {
	if (validate(operation)){
		var url =  "<s:url action='networkService' includeParams='none' />" +
			"?jsonMode=true"+
			"&ignore="+new Date().getTime();
		document.forms["networkService"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById("networkService"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
				{success : succSaveNetworkService, failure : resultDoNothing, timeout: 60000}, null);
	}
	
}

var succSaveNetworkService = function (o) {
	try{
		eval("var details = " + o.responseText);
		if (details.t) {
			if (details.n){
				if (details.pId=='leftOptions_tvNetworkIds') {
					hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), false, false);
				} else {
					hm.util.insertSelectValue(details.nId, details.nName, parent.document.getElementById(details.pId), false, true);
				}
			}
			parent.closeIFrameDialog();
		} else {
			hm.util.displayJsonErrorNote(details.m);
		}
	}catch(e){
		alert("e == "+e);
	}
	
}


var resultDoNothing = function(o) {
	// do nothing now
}


function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_NETWORK_SERVICE%>" || operation == 'cancel' + '<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_protocolId").value = 0;
		document.getElementById(formName + "_dataSource_protocolNumber").value = 0;
		document.getElementById(formName + "_dataSource_portNumber").value = 0;
		document.getElementById(formName + "_dataSource_idleTimeout").value = 0;
		return true;
	}

	if (!validateServiceName()) {
		return false;
	}
	var inputElement = document.getElementById(formName + "_dataSource_description");
	var description = inputElement.value;
	if(description.toLowerCase() == "null"){
		hm.util.reportFieldError(inputElement, '<s:text name="config.ns.description" />'+" cannot be '"+description+"'");
       	inputElement.focus();
       	return false;
	}
	//var selectService = document.getElementById(formName + "_dataSource_serviceType2");
	//if(!selectService.checked){
		if (!validateProtocolNumber()) {
			return false;
		}
		if (!validatePortNumber()) {
			return false;
		}
	//}
	
	if (!validateIdleTimeout()) {
		return false;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="networkService" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_serviceName").disabled == false) {
		document.getElementById(formName + "_dataSource_serviceName").focus();
	}
	if ("new" == '<s:property value="operation"/>') {
		 document.getElementById(formName + "_dataSource_idleTimeout").value="";
		 document.getElementById(formName + "_dataSource_portNumber").value="";
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(810, 350);
		}
	</s:if>
}

function validateServiceName(){
	var inputElement = document.getElementById(formName + "_dataSource_serviceName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.ns.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}else{
   		var servicename = inputElement.value;
   		if(servicename.toLowerCase() == "null"){
   			hm.util.reportFieldError(inputElement, '<s:text name="config.ns.name" />'+" cannot be '"+servicename+"'");
   	       	inputElement.focus();
   	       	return false;
   		}
   	}
	return true;
}

function validateProtocolNumber(){
	var protocolId = document.getElementById(formName + "_dataSource_protocolId").value;
	var inputElement = document.getElementById(formName + "_dataSource_protocolNumber");
	if(protocolId == PROTOCOL_ID_TCP
	|| protocolId == PROTOCOL_ID_UDP
	|| protocolId == PROTOCOL_ID_SVP){
		inputElement.value = 0;
		return true;
	}

	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ns.protocolNumber" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ns.protocolNumber" />',
	                                           <s:property value="1" />,
	                                           <s:property value="255" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

function validatePortNumber(){
	var protocolId = document.getElementById(formName + "_dataSource_protocolId").value;
	var inputElement = document.getElementById(formName + "_dataSource_portNumber");

	if(protocolId == PROTOCOL_ID_SVP
	|| protocolId == PROTOCOL_ID_CUSTOM){
		inputElement.value = 0;
		return true;
	}
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ns.portNumber" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ns.portNumber" />',
												<s:property value="1" />,
												<s:property value="65535" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

function validateIdleTimeout(){
	var inputElement = document.getElementById(formName + "_dataSource_idleTimeout");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ns.idleTimeout" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ns.idleTimeout" />',
												<s:property value="0" />,
												<s:property value="65535" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

function spChanged(selectedValue){
	var protocolNumber = document.getElementById("protocolNumber");
	var portNumber = document.getElementById("portNumber");
	var protocolNumberElement = document.getElementById(formName + "_dataSource_protocolNumber");
	var portNumberElement = document.getElementById(formName + "_dataSource_portNumber");
	switch(selectedValue){
		case PROTOCOL_ID_TCP+"":
			portNumberElement.value = "";
			protocolNumberElement.value = 6;
			portNumber.style.display = "";
			protocolNumber.style.display="none";
			break;
		case PROTOCOL_ID_UDP+"":
			portNumberElement.value = "";
			protocolNumberElement.value = 17;
			portNumber.style.display = "";
			protocolNumber.style.display="none";
			break;
		case PROTOCOL_ID_SVP+"":
			protocolNumberElement.value = 119;
			portNumber.style.display = "none";
			protocolNumber.style.display="none";
			break;
		case PROTOCOL_ID_CUSTOM+"":
			protocolNumberElement.value = "";
			portNumber.style.display = "none";
			protocolNumber.style.display="";
			break;
	}
}

function changeService(service){
	if (service == 'networkService') {
			hm.util.show('protocolId_tr');
			if("new" != '<s:property value="operation"/>'){
				if('<s:property value="protocolNumberTRStatus"/>' == ""){
					hm.util.show('protocolNumber');
				}else{
					hm.util.hide('protocolNumber');
				}
				if('<s:property value="portNumberTRStatus"/>' == ""){
					hm.util.show('portNumber');
				}else{
					hm.util.hide('portNumber');
				}
			}else{
				var selectValue = document.getElementById(formName + "_dataSource_protocolId");
				if(selectValue){
					spChanged(selectValue.value);
				}
			}
			hm.util.show('algType_tr');
			hm.util.hide('l7_app_tr');
	} else {
		hm.util.hide('protocolId_tr');
		hm.util.hide('protocolNumber');
		hm.util.hide('portNumber');
		hm.util.hide('algType_tr');
		hm.util.show('l7_app_tr');
	}
}
</script>
<div id="content"><s:form action="networkService" id="networkService" name="networkService">
<s:hidden name="parentDomID" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id"/>
		<s:hidden name="parentIframeOpenFlg" />
		<s:hidden name="contentShowType" />
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
								onClick="submitAction('<%=Navigation.L2_FEATURE_NETWORK_SERVICE%>');">
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
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-network_services.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.title.networkService"/>
									</s:if> <s:else>
										<s:text name="config.title.networkService.edit"/>
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
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<s:if test="%{!parentIframeOpenFlg}">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
												<td width="20px">&nbsp;</td>
												<s:if test="%{dataSource.id == null}">
													<s:if test="%{writeDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveNetworkServiceJson('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{updateDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveNetworkServiceJson('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
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
													<s:if test="%{writeDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{updateDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
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
				</td>
			</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
				<s:if test="%{jsonMode}">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
				</s:if>
				<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700">
				</s:else>
				<tr>
					<td style="padding: 6px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="150"><label><s:text
										name="config.ns.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td colspan="2"><s:textfield name="dataSource.serviceName"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="24" maxlength="%{serviceNameLength}"
										disabled="%{disabledName}" />&nbsp;<s:text
										name="config.ns.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="150"><s:text
										name="config.ns.description" /></td>
									<td colspan="2" width="450"><s:textfield name="dataSource.description"
										size="48" maxlength="%{descriptionLength}" />&nbsp;<s:text
										name="config.ns.description.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="150"><label><s:text
										name="config.ns.idleTimeout" /><font color="red"><s:text name="*"/></font></label></td>
									<td colspan="2"><s:textfield name="dataSource.idleTimeout" size="11"
										maxlength="5"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
										name="config.ns.idleTimeoutRange" /></td>
								</tr>
								<!--tr>
									<td class="sepLine" colspan="3"><img
										src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
								</tr-->
								<%-- <tr>
									<td class="labelT1" width="150"><s:radio label="Gender"
										 name="dataSource.serviceType"
										list="#{'1':'Network Service'}" value="%{serviceType}"  disabled="%{serviceTypeDisabled}"  
										onclick="changeService('networkService');" /></td>
									<td colspan="2" class="labelT1" width="150"><s:radio label="Gender"
										 name="dataSource.serviceType"
										list="#{'2':'Layer 7 Service'}" value="%{serviceType}"  disabled="%{serviceTypeDisabled}" 
										onclick="changeService('l7Service');" /></td>
								</tr> --%>
								<tr id="protocolId_tr" style="display:<s:property value="%{protocolIdTRStatus}"/>">
									<td class="labelT1" width="150"><s:text
										name="config.ns.protocolId" /></td>
									<td colspan="2"><s:select name="dataSource.protocolId"
										value="dataSource.protocolId" list="%{enmuProtocolId}"
										listKey="key" listValue="value"
										onchange="spChanged(this.value)" /></td>
								</tr>
								<tr id="protocolNumber"
									style="display:<s:property value="%{protocolNumberTRStatus}"/>">
									<td class="labelT1" width="150"><label><s:text
										name="config.ns.protocolNumber" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.protocolNumber"
										size="11" maxlength="3"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
										name="config.ns.protocolRange" /></td>
								</tr>
								<tr id="portNumber"
									style="display:<s:property value="%{portNumberTRStatus}"/>">
									<td class="labelT1" width="150"><label><s:text
										name="config.ns.portNumber" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.portNumber" size="11"
										maxlength="5"
										onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
										name="config.ns.portRange" /></td>
								</tr>
								
								<tr id="algType_tr" style="display:<s:property value="%{algTypeTRStatus}"/>">
									<td class="labelT1" width="150"><s:text
										name="config.ns.algTypename" /></td>
									<td colspan="2"><s:select name="dataSource.algType"
										value="dataSource.algType" list="%{enmuAlgType}"
										listKey="key" listValue="value" /></td>
								</tr>
								<%-- <tr id="l7_app_tr" style="display:<s:property value="%{l7AppTRStatus}"/>">
									<td class="labelT1" width="150"><s:text
										name="config.ns.l7app" /></td>
									<td colspan="2"><s:select name="dataSource.appId"
										value="dataSource.appId" list="%{l7AppList}"
										listKey="appCode" listValue="appName" /></td>
								</tr> --%>
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
