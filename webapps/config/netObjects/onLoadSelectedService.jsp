<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.NetworkService"%>

<div id="content"><s:form action="networkService" id="networkService" name="networkService">
		<s:hidden name="operation" />
		<s:hidden name="id"/>
		<s:hidden name="onloadSelectedService"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<s:if test="%{jsonMode == true}">
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-network_services.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								</s:if>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.title.networkService"/>
									</s:if> <s:else>
										<s:text name="config.title.networkService.edit"/>
									</s:else>
								</td>
								<s:if test="%{jsonMode == true}">
								<td style="padding-left:10px;">
									<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');return false;" >
										<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
											alt="" class="dblk"/>
									</a>
								</td>
								</s:if>
							</tr>
						</table>
						</td>
						<td align="right">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="hideSelfShowParent();return false;" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
												<td width="20px">&nbsp;</td>
												<s:if test="%{dataSource.id == null}">
													<s:if test="%{writeDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveNetworkServiceJson('create');return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{updateDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveNetworkServiceJson('update');return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
				<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td style="padding: 6px 5px 6px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0">
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
									<td colspan="2" width="500"><s:textfield name="dataSource.description"
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
<script>
var PROTOCOL_ID_TCP = <%=NetworkService.PROTOCOL_ID_TCP%>
var	PROTOCOL_ID_UDP = <%=NetworkService.PROTOCOL_ID_UDP%>
var PROTOCOL_ID_SVP = <%=NetworkService.PROTOCOL_ID_SVP%>
var PROTOCOL_ID_CUSTOM = <%=NetworkService.PROTOCOL_ID_CUSTOM%>

var service_formName = 'networkService';
function saveNetworkServiceJson(operation) {
	if (validateNetwork(operation)){
		var url =  "<s:url action='networkService' includeParams='none' />" +
			"?onloadSelectedService=true&jsonMode=false"+
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
				var tr = document.createElement("tr");
				tr.className = "even";
				$(tr).attr("serviceId",details.nId);
				$(tr).attr("appType","0");
				var tdEl = document.createElement("td");
				$(tdEl).width(50);
				$(tdEl).attr("align","center");
				$(tdEl).attr("style","padding-left:2px;vertical-align: middle;");
				var tdChkEl = document.createElement('input');
				tdChkEl.type = "checkbox";
				$(tdChkEl).attr("appType","0");
				tdEl.appendChild(tdChkEl);
				tr.appendChild(tdEl);
				var tdNameEl = document.createElement("td");
				$(tdNameEl).width(240);
				var hlepLink = document.createElement("a");
				hlepLink.href = "#";
				hlepLink.onclick="javascript: void(0);return false;";
				$(hlepLink).attr("title",details.nDesc);
				hlepLink.innerHTML=details.nName;
				tdNameEl.appendChild(hlepLink);
				tr.appendChild(tdNameEl);
				var leftTable = getDom('leftServiceTable');
				var list = leftTable.getElementsByTagName('tr');
				var thead = list[1];
				leftTable.insertBefore(tr, thead);
				var totalNum = document.getElementById("totalService").value;
				appService.changeCurrentSearchNum("currentAvailableNum",parseInt(totalNum) + 1);
				appService.changeCurrentSearchNum("showTotalService",parseInt(totalNum) + 1);
				appService.changeCurrentTotalNum("totalService",parseInt(totalNum) + 1);
			}else{
				var leftTable = getDom('leftServiceTable');
				var list = leftTable.getElementsByTagName('tr');
				for(var i=0; i<list.length; i++){
					var chck = list[i].getElementsByTagName("input")[0];
					if (chck.checked == true) {
						var hrefName = list[i].getElementsByTagName('a')[0];
						hrefName.title = details.nDesc;
					}
				}
			}
			hideSelectNetworkServicePanel();
			showSelectServicePanel();
		} else {
			hm.util.displayJsonErrorNote(details.m);
		}
	}catch(e){
		return null;
	}
	
}


var resultDoNothing = function(o) {
	// do nothing now
}


function validateNetwork(operation) {
	if (!validateServiceName()) {
		return false;
	}
	var inputElement = document.getElementById(service_formName + "_dataSource_description");
	var description = inputElement.value;
	if(description.toLowerCase() == "null"){
		hm.util.reportFieldError(inputElement, '<s:text name="config.ns.description" />'+" cannot be '"+description+"'");
       	inputElement.focus();
       	return false;
	}
	if (!validateProtocolNumber()) {
		return false;
	}
	if (!validatePortNumber()) {
		return false;
	}
	
	if (!validateIdleTimeout()) {
		return false;
	}
	return true;
}

/* function loadNetServicePage() {
	if (document.getElementById(service_formName + "_dataSource_serviceName").disabled == false) {
		document.getElementById(service_formName + "_dataSource_serviceName").focus();
	}else{
		document.getElementById(service_formName + "_dataSource_description").focus();
	}
}

window.setTimeout("loadNetServicePage()", 200); */

function validateServiceName(){
	var inputElement = document.getElementById(service_formName + "_dataSource_serviceName");
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
	var protocolId = document.getElementById(service_formName + "_dataSource_protocolId").value;
	var inputElement = document.getElementById(service_formName + "_dataSource_protocolNumber");
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
	var protocolId = document.getElementById(service_formName + "_dataSource_protocolId").value;
	var inputElement = document.getElementById(service_formName + "_dataSource_portNumber");

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
	var inputElement = document.getElementById(service_formName + "_dataSource_idleTimeout");
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
	var protocolNumberElement = document.getElementById(service_formName + "_dataSource_protocolNumber");
	var portNumberElement = document.getElementById(service_formName + "_dataSource_portNumber");
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

</script>