<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.CustomApplicationRule"%>
<style type="text/css">
.rulecontainer {
	border-bottom: 1px solid #a8a8a8;
	overflow-x: auto;
	overflow-y: hidden;
	width: 600px;
	height: 20px;
}

.showcontainer {
	overflow-x: hidden;
	overflow-y: auto;
	width: 610px;
	height: 150px;
}
.thead{
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    padding-top: 2px;
}
.tdtypecontent{
	width: 150px;
    height: 20px;
    text-align: left;
    padding-left:10px;
}
.tdcontent{
	width: 100px;
    height: 20px;
    text-align: left;
    padding-left:10px;
}
.tdportcontent{
	width: 80px;
    height: 20px;
    text-align: left;
    padding-left:10px;
}
.tdopercontent{
	width: 70px;
    height: 20px;
    text-align: left;
    padding-left:10px;
}
.tdhostcontent{
	width: 200px;
    height: 20px;
    text-align: left;
    padding-left:20px;
    word-break: break-all;
    word-wrap: break-word;
}
</style>
<div id="content"><s:form action="customApp">
		<s:hidden name="operation" />
		<s:hidden name="id"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="geneva_26.config.title.custom.application.new"/>
									</s:if> <s:else>
										<s:text name="geneva_26.config.title.custom.application.edit"/>
									</s:else>
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
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="hideSelectServicePanel();return false;" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
												<td width="20px">&nbsp;</td>
												<s:if test="%{dataSource.id == null}">
													<s:if test="%{writeDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveCustomAppJson('create');return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{updateDisabled == 'disabled'}">
														<td>&nbsp;</td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveCustomAppJson('update');return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
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
									<td class="labelT1" width="150"><label><s:text name="geneva_26.config.custom.application.name"/><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.customAppName"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="32" maxlength="32" />&nbsp;<s:text
										name="geneva_26.config.custom.application.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="150"><s:text name="geneva_26.config.custom.application.description"/></td>
									<td width="450"><s:textfield name="dataSource.description"
										size="45" maxlength="64" />&nbsp;<s:text
										name="geneva_26.config.custom.application.description.range" /></td>
								</tr>
								<tr style="display:none;">
									<td class="labelT1" width="150"><label><s:text name="geneva_26.config.custom.application.idletimeout"/><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.idleTimeout"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										size="11" maxlength="5" />&nbsp;<s:text
										name="geneva_26.config.custom.application.idletimeout.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" colspan="2" style="font-size: 15px;"><s:text name="geneva_26.config.custom.application.rules"/></td>
								</tr>
								<%-- <tr>
									<td class="labelT1 noteInfo" colspan="2"><s:text name="geneva_26.config.custom.application.rules.note"/></td>
								</tr> --%>
								<tr><td colspan="2" align="left">
									<table width="100%">
										<tr><td>
											<label id="validateError"></label>
										</td></tr>
									</table>
								</td></tr>
								<tr>
									<td class="labelT1" colspan="2">
										<table>
											<tr>
												<td>
													<s:select id="detection_id" name="detectionType" list="%{enumDetectionType}"
														listKey="key" listValue="value" onchange="detectionChanged(this.value)" />
												</td>
												<td>
													<s:select id="hostname_protocol_id" name="protocol" list="%{enumHostNameProtocol}"
														listKey="key" listValue="value" />
													<s:select cssStyle="display:none;" id="ipport_protocol_id" name="protocol" list="%{enumIpPortProtocol}"
														listKey="key" listValue="value" />
												</td>
												<td id="ruleName_td_id">
													<input id="ruleval_id" type="text" name="ruleName" value="" style="width:180px;" maxlength="100" size="32" />
												</td>
												<td>
													<input style="display:none;" id="port_id" type="text" name="portName" value="-1" style="width:40px;" maxlength="5" size="5" />
													<label style="display:none;" id="port_id_range">&nbsp;<s:text name="config.location.server.port.range"/></label>
												</td>
												<td>
													<input class="button" type="button" name="add" value="Add" onclick="addRule();" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<div class="rulecontainer">
											<table id="thead_table_id">
												<tbody>
												<tr>
													<th width="150px" class="thead"><s:text name="geneva_26.config.custom.application.rule.title.type"/></th>
													<th width="100px" class="thead"><s:text name="geneva_26.config.custom.application.rule.title.protocol"/></th>
													<th width="200px" class="thead"><s:text name="geneva_26.config.custom.application.rule.title.ip.hostname"/></th>
													<th width="80px" class="thead"><s:text name="geneva_26.config.custom.application.rule.title.port"/></th>
													<th width="70px" class="thead"></th>
												</tr>
												</tbody>
											</table>
										</div>
										<div class="showcontainer">
											<table>
												<tbody id="content_table_id">
												<s:iterator value="%{dataSource.rules}" status="status">
														<tr>
															<td class="tdtypecontent">
																<s:property value="detectionTypeStr" />
																<input type="hidden" name="ruleIds" value="<s:property value="ruleId" />"/>
																<input type="hidden" name="customDetectionTypes" value="<s:property value="detectionType" />"/>
															</td>
															<td class="tdcontent">
																<s:property value="protocolTypeStr" />
																<input type="hidden" name="customProtocols" value="<s:property value="protocolId" />"/>
															</td>
															<td class="tdhostcontent">
																<s:property value="ruleValue" />
																<input type="hidden" name="customRules" value="<s:property value="ruleValue" />"/>
															</td>
															<td class="tdportcontent">
																<s:if test="%{-1 != portNumber}">
																<s:property value="portNumber" />
																</s:if>
																<input type="hidden" name="customPorts" value="<s:property value="portNumber" />"/>
															</td>
															<td class="tdopercontent">
																<a href="javascript: void(0);" onclick="delRule(this);return false;">
																<img width="16" height="16" title="remove" alt="remove" src="<s:url value="/images/ahdatatable/hm-trash.png" />" />
																</a>
															</td>
														</tr>
													</s:iterator>	
													</tbody>
											</table>
										</div>
									</td>
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
var DETECTION_TYPE_HOSTNAME = <%=CustomApplicationRule.DETECTION_TYPE_HOSTNAME%>;
var	DETECTION_TYPE_IPADDRESS = <%=CustomApplicationRule.DETECTION_TYPE_IPADDRESS%>;
var DETECTION_TYPE_PORT = <%=CustomApplicationRule.DETECTION_TYPE_PORT%>;
var intervalId;
function initRuleTip(){
	var hostNameProtocol = document.getElementById("hostname_protocol_id");
	var ruleValueEl = document.getElementById("ruleval_id");
	var ruleValueTip;
	if(hostNameProtocol.style.display == "none"){
		if(ruleValueEl == null){return;}
		ruleValueTip = function(){
				if("" == ruleValueEl.value){
					ruleValueEl.style.backgroundImage = "url(<s:url value='/images/serveripaddress-tip.png' includeParams='none'/>)";
				}else{
					ruleValueEl.style.backgroundImage = "none";
				}
			}
	}else{
		if(ruleValueEl == null){return;}
 		ruleValueTip = function(){
				if("" == ruleValueEl.value){
					ruleValueEl.style.backgroundImage = "url(<s:url value='/images/hostname-tip.png' includeParams='none'/>)";
				}else{
					ruleValueEl.style.backgroundImage = "none";
				}
			}	
	}
	intervalId = window.setInterval(ruleValueTip, 100);
}
function initPortTip(){
	var portEl = document.getElementById("port_id");
	if(portEl == null){return;}
	var portTip = function(){
			if(portEl.value.length == 0){
				portEl.style.width = "40px";
				portEl.style.backgroundImage = "url(<s:url value='/images/port-tip.png' includeParams='none'/>)";
			}else{
				portEl.style.backgroundImage = "none";
			}
		}	
	window.setInterval(portTip, 100);
}
window.setTimeout("initPortTip()",200);
window.setTimeout("initRuleTip()",200);
function loadCustomAppPage() {
	/* if (document.getElementById(custom_fromName + "_dataSource_customAppName").disabled == false) {
		document.getElementById(custom_fromName + "_dataSource_customAppName").focus();
	}else{
		document.getElementById(custom_fromName + "_dataSource_description").focus();
	} */
	if('<s:property value="operation"/>' == 'new'){
		document.getElementById(custom_fromName + "_dataSource_idleTimeout").value = 300;
	}
}

window.setTimeout("loadCustomAppPage()", 200);

var custom_fromName = 'customApp';

function saveCustomAppJson(operation) {
	if (validateCustomApp(operation)){
		var url =  "<s:url action='customApp' includeParams='none' />" + "?&selectAdd=<s:property value='selectAdd'/>&ignore="+new Date().getTime();
		document.forms[custom_fromName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById(custom_fromName));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
				{success : succSaveCustomApp, failure : resultDoNothing, timeout: 60000}, null);
	}
}

var succSaveCustomApp = function (o) {
	try{
		eval("var details = " + o.responseText);
		if (details.t == true) {
			if(details.selectAdd == true){
				var tr = document.createElement("tr");
				tr.className = "even";
				$(tr).attr("appId",details.appId);
				$(tr).attr("appType",details.appType);
				var tdEl = document.createElement("td");
				tdEl.className = "tdcustomchk";
				var tdChkEl = document.createElement('input');
				tdChkEl.type = "checkbox";
				tdChkEl.name = "customAppIds";
				$(tdChkEl).attr("serviceId",details.appId);
				$(tdChkEl).attr("appType",details.appType);
				tdEl.appendChild(tdChkEl);
				tr.appendChild(tdEl);
				var tdNameEl = document.createElement("td");
				tdNameEl.className = "tdcustomappname";
				var hlepLink = document.createElement("a");
				hlepLink.href = "javascript:void(0);";
				$(hlepLink).attr("title",details.appDesc);
				hlepLink.innerHTML=details.appName;
				tdNameEl.appendChild(hlepLink);
				tr.appendChild(tdNameEl);
				var tdUsageEl = document.createElement("td");
				tdUsageEl.className = "tdcustomusage";
				$(tdUsageEl).attr("realValue","0");
				tdUsageEl.innerHTML="0.00 KB";
				tr.appendChild(tdUsageEl);
				var tdmonthUsageEl = document.createElement("td");
				tdmonthUsageEl.className = "tdcustomusage";
				$(tdmonthUsageEl).attr("realValue","0");
				tdmonthUsageEl.innerHTML="0.00 KB";
				tr.appendChild(tdmonthUsageEl);
				var leftTable = getDom('leftCustomAppTable');
				var list = leftTable.getElementsByTagName('tr');
				var thead = list[1];
				leftTable.insertBefore(tr, thead);
				appService.changeNumber(1);
				hideSelectServicePanel();
			}else{
				hideSelectServicePanel();
				window.location.href = "<s:url action='customApp' includeParams='none' />"+"?oper="+details.m+"&appName="+encodeURIComponent(details.appName)+ "&ignore="+new Date().getTime();
			}
			return;
		} else {
			hm.util.displayJsonErrorNote(details.m);
			return;
		}
	}catch(e){
		return;
	}
	
}


var resultDoNothing = function(o) {
	// do nothing now
}

function validateCustomApp(operation) {
	if (!validateCustomAppName()) {
		return false;
	}
	if(!validateDescription()){
		return false;
	}
	if(!validateIdleTimeout()){
		return false;
	}
	return true;
}

function validateCustomAppName(){
	var inputElement = document.getElementById(custom_fromName + "_dataSource_customAppName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="geneva_26.config.custom.application.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}else{
   		var servicename = inputElement.value;
   		if(servicename.toLowerCase() == "null"){
   			hm.util.reportFieldError(inputElement, '<s:text name="geneva_26.config.custom.application.name" />'+" cannot be '"+servicename+"'");
   	       	inputElement.focus();
   	       	return false;
   		}
   	}
	return true;
}

function validateDescription(){
	var inputElement = document.getElementById(custom_fromName + "_dataSource_description");
	var description = inputElement.value;
	if(description.toLowerCase() == "null"){
		hm.util.reportFieldError(inputElement, '<s:text name="geneva_26.config.custom.application.description" />'+" cannot be '"+description+"'");
       	inputElement.focus();
       	return false;
	}
	return true;
}

function validateIdleTimeout(){
	var inputElement = document.getElementById(custom_fromName + "_dataSource_idleTimeout");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="geneva_26.config.custom.application.idletimeout" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="geneva_26.config.custom.application.idletimeout" />',
												<s:property value="0" />,
												<s:property value="65535" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

function addRule(){
	if(! validateMaxRule()){
		return false;
	}
	if(! validateRuleValue()){
		return false;
	}
	if(! validatePortNumber()){
		return false;
	}
	var selectedDetectionType = document.getElementById("detection_id").value;
	var selectedDetectionTypeIndex = document.getElementById("detection_id").selectedIndex;
	var selectedDetectionTypeText = document.getElementById("detection_id").options[selectedDetectionTypeIndex].text;
	var selectedProtocol = "";
	var selectedProtocolIndex = -1; 
	var selectedProtocolText = "";
	var ruleName = "";
	var hostNameProtocol = document.getElementById("hostname_protocol_id");
	var ipportProtocol = document.getElementById("ipport_protocol_id");
	if(hostNameProtocol.style.display == "" && ipportProtocol.style.display == "none"){
		selectedProtocol = hostNameProtocol.value;
		selectedProtocolIndex = hostNameProtocol.selectedIndex;
		selectedProtocolText = hostNameProtocol.options[selectedProtocolIndex].text;
		ruleName = document.getElementById("ruleval_id").value;
	}else if(hostNameProtocol.style.display == "none" && ipportProtocol.style.display == ""){
		selectedProtocol = ipportProtocol.value;
		selectedProtocolIndex = ipportProtocol.selectedIndex;
		selectedProtocolText = ipportProtocol.options[selectedProtocolIndex].text;
		ruleName = document.getElementById("ruleval_id").value;
	}
	var portName = document.getElementById("port_id").value;
	
	if(! validdateSameRule(selectedDetectionType,selectedProtocol,ruleName,portName)){
		return false;
	}
	
	
	var trEl = document.createElement("tr");
	for(var i=0 ;i<5; i++){
		var tdEl = document.createElement("td");
		switch (i){
			case 0:
				tdEl.className = "tdtypecontent";
				tdEl.innerHTML = selectedDetectionTypeText;
				var tdHideEl = document.createElement('input');
				tdHideEl.type = "hidden";
				tdHideEl.name = "customDetectionTypes";
				tdHideEl.value = selectedDetectionType;
				tdEl.appendChild(tdHideEl);
				var tdHideEl = document.createElement('input');
				tdHideEl.type = "hidden";
				tdHideEl.name = "ruleIds";
				tdHideEl.value = "";
				tdEl.appendChild(tdHideEl);
				break;
			case 1:
				tdEl.className = "tdcontent";
				tdEl.innerHTML = selectedProtocolText;
				var tdHideEl = document.createElement('input');
				tdHideEl.type = "hidden";
				tdHideEl.name = "customProtocols";
				tdHideEl.value = selectedProtocol;
				tdEl.appendChild(tdHideEl);
				break;
			case 2:
				tdEl.className = "tdhostcontent";
				tdEl.innerHTML = ruleName;
				var tdHideEl = document.createElement('input');
				tdHideEl.type = "hidden";
				tdHideEl.name = "customRules";
				tdHideEl.value = ruleName;
				tdEl.appendChild(tdHideEl);
				break;
			case 3:
				tdEl.className = "tdportcontent";
				tdEl.innerHTML = portName == "-1"?"":portName;
				var tdHideEl = document.createElement('input');
				tdHideEl.type = "hidden";
				tdHideEl.name = "customPorts";
				tdHideEl.value = portName;
				tdEl.appendChild(tdHideEl);
				break;
			case 4:
				tdEl.className = "tdopercontent";
				var hlepLink = document.createElement("a");
				hlepLink.href = "javascript: void(0);";
				$(hlepLink).attr("onclick","delRule(this);return false;");
				var image = document.createElement('img');
				image.width=16;
				image.height=16;
				image.title="remove";
				image.alt="remove";
				image.src='images/ahdatatable/hm-trash.png';
				hlepLink.appendChild(image)
				tdEl.appendChild(hlepLink);
				break;
			default:
				break;
		}
		trEl.appendChild(tdEl);
	}
	document.getElementById("content_table_id").appendChild(trEl);
	document.getElementById("port_id").value = "";
	document.getElementById("port_id_range").value = "";
	document.getElementById("ruleval_id").value = "";
}

function delRule(obj){
	obj.parentNode.parentNode.id = "delete_row_id";
	var cancelBtn = function(){
		obj.parentNode.parentNode.id = "";
		this.hide();
	};
	var mybuttons = [ { text:"Yes", handler: function(){this.hide();doDeleteContinueOper();}, isDefault:true }, 
                      { text:"No", handler: cancelBtn} ];
    var ruleDeleteMsg = "<html><body>This operation will remove the current item.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>";
    var dlg = userDefinedConfirmDialog(ruleDeleteMsg, mybuttons, "Confirm");
    dlg.show();
	
}

function doDeleteContinueOper()
{
	var removeObj = document.getElementById("delete_row_id");
	document.getElementById("content_table_id").removeChild(removeObj);
}

function validateMaxRule(){
	var error_msg = document.getElementById("validateError");
	var customDetectionTypes = document.getElementsByName('customDetectionTypes');
	if(customDetectionTypes == null){
		return true;
	}else if(customDetectionTypes.length == 64){
		hm.util.reportFieldError(error_msg, '<s:text name="geneva_26.error.custom.application.rule.permit.maximum"/>');
		return false;
	}
	return true;
}


function validdateSameRule(hostname,protocol,rulevalue,port){
	var error_msg = document.getElementById("validateError");
	var customDetectionTypes = document.getElementsByName('customDetectionTypes');
	var customProtocols = document.getElementsByName('customProtocols');
	var customRules = document.getElementsByName('customRules');
	var customPorts = document.getElementsByName('customPorts');
	if(customDetectionTypes.length >0){
		var result = true;
		for(var i=0; i<customDetectionTypes.length; i++){
			if((hostname == customDetectionTypes[i].value) && (protocol == customProtocols[i].value) &&
					(rulevalue == customRules[i].value) && (port == customPorts[i].value)){
				result = false;
				break;
			}
		}
		if(! result){
			hm.util.reportFieldError(error_msg, '<s:text name="geneva_26.error.custom.application.rule.add.failure"/>');
			return false;
		}else{
			return true;
		}
	}else{
		return true;
	}
}

function validateRuleValue(){
	var error_msg = document.getElementById("validateError");
	var detectionType = document.getElementById("detection_id").value;
	var inputElement = document.getElementById("ruleval_id");
	if(detectionType == DETECTION_TYPE_PORT){
		inputElement.value = "";
		return true;
	}
	
	if(detectionType == DETECTION_TYPE_HOSTNAME){
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(error_msg, '<s:text name="error.requiredField"><s:param><s:text name="geneva_26.config.custom.application.rule.hostname" /></s:param></s:text>');
			return false;
		}else{
			//var prefix_suffix_rule = /^(\*\.){0,1}(([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])\.){0,1}?([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])(\.(com|edu|gov|int|mil|net|org|biz|info|name|museum|coop|aero|[a-z][a-z])){0,1}(\.[a-z][a-z]|\.\*){0,1}$/gi;
			//var prefix_suffix_reg = new RegExp(prefix_suffix_rule);
			//var prefix_rule = /^(\*\.){0,1}(([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])\.){0,1}?([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])(com|edu|gov|int|mil|net|org|biz|info|name|museum|coop|aero|[a-z][a-z])(\.[a-z][a-z]|){0,1}$/gi;
			//var suffix_rule = /^(([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])\.){0,1}?([a-zA-Z0-9]\w{0,61}?[a-zA-Z0-9]|[a-zA-Z0-9])(\.(com|edu|gov|int|mil|net|org|biz|info|name|museum|coop|aero|[a-z][a-z])){0,1}(\.[a-z][a-z]|\.\*){0,1}$/gi;
			var prefix_rule = /^(\*\.){0,1}([A-Za-z0-9-]+\.)+[A-Za-z]+$/gi;
			var suffix_rule = /^([A-Za-z0-9-]+\.)+[A-Za-z]+(\.\*){0,1}$/gi;
			var prefix_reg = new RegExp(prefix_rule);
			var suffix_reg = new RegExp(suffix_rule);
			if((! hm.util.validateIpAddressExtension(inputElement.value)) && (! prefix_reg.test(inputElement.value)) && (! suffix_reg.test(inputElement.value))){
				hm.util.reportFieldError(error_msg, '<s:text name="geneva_26.error.custom.application.rule.host.name.invalid" />');
				return false;
			}
		}
	}
	
	if(detectionType == DETECTION_TYPE_IPADDRESS){
		var ipaddress = inputElement.value; 
		if (ipaddress.length == 0) {
			hm.util.reportFieldError(error_msg, '<s:text name="error.requiredField"><s:param><s:text name="geneva_26.config.custom.application.rule.ip" /></s:param></s:text>');
			return false;
		}
		if(ipaddress.indexOf("-") != -1){
			var ipaddressArr=ipaddress.split("-");
			var len=ipaddressArr.length;
			if(len == 2){
				var rel = false;
				for (var i=0;i<len;i++) {
					if (! hm.util.validateIpAddressExtension(ipaddressArr[i])) {
						rel = true;
						break;
				   	}
				}
				if(rel){
					hm.util.reportFieldError(error_msg, 'ip address range format is invalid.');
					return false;
				}else{
					var startIP = ipaddressArr[0];
					var endIP = ipaddressArr[1];
					if(! hm.util.compareIpAddress(startIP,endIP)){
						hm.util.reportFieldError(error_msg, startIP+' cannot be larger than '+endIP);
						return false;
					}
				}
			}else{
				hm.util.reportFieldError(error_msg, '<s:text name="error.formatInvalid"><s:param><s:text name="geneva_26.config.custom.application.rule.ip" /></s:param></s:text>');
				return false;
			}
		}else{
			if (! hm.util.validateIpAddressExtension(ipaddress)) {
				hm.util.reportFieldError(error_msg, '<s:text name="error.formatInvalid"><s:param><s:text name="geneva_26.config.custom.application.rule.ip" /></s:param></s:text>');
				return false;
			}
		}
		
	}
	return true;
}

function validatePortNumber(){
	var detectionType = document.getElementById("detection_id").value;
	var error_msg = document.getElementById("validateError");
	var inputElement = document.getElementById("port_id");

	if(detectionType == DETECTION_TYPE_HOSTNAME){
		return true;
	}
	if(detectionType == DETECTION_TYPE_PORT){
		if (inputElement.value.length == 0) {
			hm.util.reportFieldError(error_msg, '<s:text name="error.requiredField"><s:param><s:text name="geneva_26.config.custom.application.rule.port" /></s:param></s:text>');
			return false;
		}
	}
	if(inputElement.value.length != 0){
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="geneva_26.config.custom.application.rule.port" />',
													<s:property value="1" />,
													<s:property value="65535" />);
		if (message != null) {
			hm.util.reportFieldError(error_msg, message);
			return false;
		}
	}
	
	return true;
}

function detectionChanged(selectedValue){
	var hostNameProtocol = document.getElementById("hostname_protocol_id");
	var ipportProtocol = document.getElementById("ipport_protocol_id");
	var ruleName = document.getElementById("ruleval_id");
	var port = document.getElementById("port_id");
	var portRange = document.getElementById("port_id_range");
	switch(selectedValue){
		case DETECTION_TYPE_HOSTNAME+"":
			ruleName.style.display = "";
			hostNameProtocol.style.display = "";
			ipportProtocol.style.display = "none";
			port.style.display = "none";
			portRange.style.display = "none";
			port.value = "-1";
			ruleName.value = "";
			window.clearInterval(intervalId);
			initRuleTip();
			break;
		case DETECTION_TYPE_IPADDRESS+"":
			ruleName.style.display = "";
			ipportProtocol.style.display = "";
			port.style.display = "";
			portRange.style.display = "";
			hostNameProtocol.style.display = "none";
			port.value = "";
			ruleName.value = "";
			window.clearInterval(intervalId);
			initRuleTip();
			break;
		case DETECTION_TYPE_PORT+"":
			ipportProtocol.style.display = "";
			port.style.display = "";
			portRange.style.display = "";
			hostNameProtocol.style.display = "none";
			ruleName.style.display = "none";
			port.value = "";
			ruleName.value = "";
			break;
	}
}
</script>