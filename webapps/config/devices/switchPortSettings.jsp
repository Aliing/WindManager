<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>

<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
<script type="text/javascript">

	var deviceIfType_USB = <%=AhInterface.DEVICE_IF_TYPE_USB%>;

	function overrideNetworkPolicySetting(check){
		if(!check){
			var options = document.getElementsByName("lldpTransmit_check");
			var options2 = document.getElementsByName("lldpReceive_check");
			var options3 = document.getElementsByName("cdpReceive_check");
			var options4 = document.getElementsByName("clientReporting_check");
			var options5 = document.getElementsByName("portDescription_check");
			var options6 =  document.getElementsByName("adminState_select");
			var deviceIfType = document.getElementsByName("deviceIfType");

			for(var i = 0;i<options.length;i++){
				 if(deviceIfType_USB == deviceIfType[i].value){
					 continue;
				 }
				 options[i].checked = !check;
				 portSettingsValueCopy(options[i],true);
				 options2[i].checked = !check;
				 portSettingsValueCopy(options2[i],true);
				 options3[i].checked = !check;
				 portSettingsValueCopy(options3[i],true);
				 options4[i].checked = !check;
				 portSettingsValueCopy(options4[i],true);
				 options5[i].value = "";
				 portSettingsValueCopy(options5[i],false);
				 options6[i].value = 0;
				 portSettingsValueCopy(options6[i],false);
				 options[i].disabled = true;
				 options2[i].disabled = true;
				 options3[i].disabled = true;
				 options4[i].disabled = true;
				 options5[i].disabled = true;
				 
			}
		}else{
			var configTemplate = Get(formName + "_configTemplate");
			if(configTemplate){
				var url = "<s:url action='hiveAp' includeParams='none' />?operation=retriveNetworkPolicySetting"+"&configTemplate="+ configTemplate.value + "&ignore="+new Date().getTime();
				var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succOverrideNetworkPolicySetting, failure : resultDoNothing, timeout: 60000}, null);
			}
		}
	}
	
	var succOverrideNetworkPolicySetting=function(o){
		try {
			eval("var details = " + o.responseText);
			if (details.succ) {
				var options = document.getElementsByName("lldpTransmit_check");
				var options2 = document.getElementsByName("lldpReceive_check");
				var options3 = document.getElementsByName("cdpReceive_check");
				var options4 = document.getElementsByName("clientReporting_check");
				var options5 = document.getElementsByName("portDescription_check");
				var options6 =  document.getElementsByName("adminState_select");
				var deviceIfType = document.getElementsByName("deviceIfType");
				
				for(var i = 0;i<details.networkPlicyConfig.length;i++){
					 if(deviceIfType[i].value == details.networkPlicyConfig[i].deviceIfType && 
							 deviceIfType[i].value != deviceIfType_USB){
						 options[i].checked = details.networkPlicyConfig[i].lldpTransmitValue;
						 options[i].disabled = !details.networkPlicyConfig[i].lldpenable;
						 portSettingsValueCopy(options[i],true);
						 options2[i].checked = details.networkPlicyConfig[i].lldpReceiveValue;
						 options2[i].disabled = !details.networkPlicyConfig[i].lldpenable;
						 portSettingsValueCopy(options2[i],true);
						 options3[i].checked = details.networkPlicyConfig[i].cdpReceiveValue;
						 options3[i].disabled = !details.networkPlicyConfig[i].cdpEnable
						 portSettingsValueCopy(options3[i],true);
						 options4[i].checked = details.networkPlicyConfig[i].clientReporting;
						 options4[i].disabled = !details.networkPlicyConfig[i].enableClientReporting;
						 portSettingsValueCopy(options4[i],true);
						 
						 options5[i].value = details.networkPlicyConfig[i].portDescription;
						 options5[i].disabled = !details.networkPlicyConfig[i].enableOverridePortDes;
						 portSettingsValueCopy(options5[i],false);
						 
						 options6[i].value = details.networkPlicyConfig[i].adminState;
						 portSettingsValueCopy(options6[i],false);
						 
					 }
				}
			}
		}catch(e){

		}

	}
	
	var resultDoNothing = function(o){

	}

</script>

<script>
var ETH_DUPLEX_AUTO = <%=AhInterface.ETH_DUPLEX_AUTO%>;
var ETH_DUPLEX_HALF = <%=AhInterface.ETH_DUPLEX_HALF%>;
var ETH_DUPLEX_FULL = <%=AhInterface.ETH_DUPLEX_FULL%>;

var ETH_SPEED_AUTO = <%=AhInterface.ETH_SPEED_AUTO%>;
var ETH_SPEED_10M = <%=AhInterface.ETH_SPEED_10M%>;
var ETH_SPEED_100M = <%=AhInterface.ETH_SPEED_100M%>;
var ETH_SPEED_1000M = <%=AhInterface.ETH_SPEED_1000M%>;
var ETH_SPEED_10000M = <%=AhInterface.ETH_SPEED_10000M%>;

var FLOW_CONTROL_STATUS_AUTO = <%=AhInterface.FLOW_CONTROL_STATUS_AUTO%>;
var FLOW_CONTROL_STATUS_ENABLE = <%=AhInterface.FLOW_CONTROL_STATUS_ENABLE%>;
var FLOW_CONTROL_STATUS_DISABLE = <%=AhInterface.FLOW_CONTROL_STATUS_DISABLE%>;

/** switch port settings script start */

	$(function(){
		switchPortOnload();
	});

	<s:if test="%{jsonMode == true}">
	window.setTimeout("switchPortOnload()", 1000);
	</s:if>

	function switchPortOnload(){
		$("#srPortSettingsId :checkbox").click(function(){
			portSettingsValueCopy(this, true);
		});

		$("#srPortSettingsId select").change(function(){
			selectEleListen(this);
			portSettingsValueCopy(this, false);
		});

		$("#srPortSettingsId :text").change(function(){
			portSettingsValueCopy(this, false);
		});
	}

	function showSPortSettingsContent(){
		showHideContent("switchPortSettings","");
	}

	function validateSwitchPortSettings(operation){
		if(operation == 'create2' || operation == 'update2'){
			var portTable = $("#srPortSettingsId");
			if(!portTable){
				return;
			}

			var trNodes = $(portTable).find("tr");
			for(var i=0; i<trNodes.length; i++){
				var trId = $(trNodes[i]).attr("id");
				if(!trId){
					continue;
				}

				var speedEle = $("#"+trId+" select[name='speed_select']");
				var duplexEle = $("#"+trId+" select[name='duplex_select']");
				var flowControlStatusEle = $("#"+trId+" select[name='flowControlStatus_select']");
				var debounceTimerText = $("#"+trId+" input[name='debounceTimer_text']");
				var portDescription = $("#"+trId+" input[name='portDescription_check']");

				var speedValue = speedEle[0].value;
				var duplexValue = duplexEle[0].value;
				var flowControlStatusValue = flowControlStatusEle[0].value;
				var debounceTimerTextValue = debounceTimerText[0].value;
				var portDescriptionValue = portDescription[0].value;

				if(speedValue == ETH_SPEED_AUTO && duplexValue != ETH_DUPLEX_AUTO){
					var parms = '<s:text name="error.switch.port.settings.autoMust">'
							+'<s:param><s:text name="error.switch.port.settings.speed" /></s:param>'
							+'<s:param><s:text name="error.switch.port.settings.duplex" /></s:param>'
							+'</s:text>';
					showSPortSettingsContent();
					hm.util.reportFieldError(duplexEle[0], parms);
					duplexEle[0].focus();
					return false;
				}else if(speedValue != ETH_SPEED_AUTO && duplexValue == ETH_DUPLEX_AUTO){
					var parms = '<s:text name="error.switch.port.settings.autoMust">'
						+'<s:param><s:text name="error.switch.port.settings.duplex" /></s:param>'
						+'<s:param><s:text name="error.switch.port.settings.speed" /></s:param>'
						+'</s:text>';
					showSPortSettingsContent();
					hm.util.reportFieldError(speedEle[0], parms);
					speedEle[0].focus();
					return false;
				}else if((speedValue == ETH_SPEED_1000M || speedValue == ETH_SPEED_10000M) && duplexValue != ETH_DUPLEX_FULL){
					var parms = '<s:text name="error.switch.port.settings.speed1g"/>';
					showSPortSettingsContent();
					hm.util.reportFieldError(duplexEle[0], parms);
					duplexEle[0].focus();
					return false;
				}else if(flowControlStatusValue == FLOW_CONTROL_STATUS_AUTO
						&& (speedValue != ETH_SPEED_AUTO || duplexValue != ETH_DUPLEX_AUTO)){
					var parms = '<s:text name="error.switch.port.settings.autoMustTwo">'
						+'<s:param><s:text name="error.switch.port.settings.flow" /></s:param>'
						+'<s:param><s:text name="error.switch.port.settings.duplex" /></s:param>'
						+'<s:param><s:text name="error.switch.port.settings.speed" /></s:param>'
						+'</s:text>';
					showSPortSettingsContent();
					hm.util.reportFieldError(flowControlStatusEle[0], parms);
					flowControlStatusEle[0].focus();
					return false;
				}
				if (debounceTimerTextValue.length == 0) {
					hm.util.reportFieldError(debounceTimerText[0], '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.switch.port.settings.column.debounceTimer" /></s:param></s:text>');
					showSPortSettingsContent();
					debounceTimerText[0].focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(debounceTimerTextValue, '<s:text name="hiveAp.switch.port.settings.column.debounceTimer" />',
				                                           <s:property value="0" />,
				                                           <s:property value="5000" />);
				if (message != null) {
					hm.util.reportFieldError(debounceTimerText[0], message);
					showSPortSettingsContent();
					debounceTimerText[0].focus();
					return false;
				}
				
				if(portDescriptionValue.length > 0){
					var message = hm.util.validateStringWithBlank(portDescriptionValue, '<s:text name="hiveAp.switch.port.settings.column.description" />');
			    	if (message != null) {
			    		hm.util.reportFieldError(portDescription[0], message);
			    		showSPortSettingsContent();
			    		portDescription[0].focus();
			        	return false;
			    	}
				}
			}
		}
		return true;
	}

	function portSettingsValueCopy(checkEle, checkbox){
		if(checkbox){
			$(checkEle).nextAll().attr("value", checkEle.checked);
		}else{
			$(checkEle).nextAll().attr("value", checkEle.value);
		}
	}

	function selectEleListen(selectEle){
		var eleName = $(selectEle).attr("name");
		var eleValue = $(selectEle).val();
		var trNode = $(selectEle).parent().parent();
		var trId = $(trNode).attr("id");

		if(eleName == "speed_select"){
			if(eleValue == ETH_SPEED_AUTO){
				$("#"+trId+" :hidden[name='duplex']").attr("value", ETH_DUPLEX_AUTO);
				$("#"+trId+" select[name='duplex_select']").attr("value", ETH_DUPLEX_AUTO);
			}else if(eleValue == ETH_SPEED_1000M || eleValue == ETH_SPEED_10000M){
				$("#"+trId+" :hidden[name='duplex']").attr("value", ETH_DUPLEX_FULL);
				$("#"+trId+" select[name='duplex_select']").attr("value", ETH_DUPLEX_FULL);
			}
		}else if(eleName == "duplex_select"){
			if(eleValue == ETH_DUPLEX_AUTO){
				$("#"+trId+" :hidden[name='speed']").attr("value", ETH_SPEED_AUTO);
				$("#"+trId+" select[name='speed_select']").attr("value", ETH_SPEED_AUTO);
			}
		}else if(eleName == "flowControlStatus_select"){
			if(eleValue == FLOW_CONTROL_STATUS_AUTO){
				$("#"+trId+" :hidden[name='duplex']").attr("value", ETH_DUPLEX_AUTO);
				$("#"+trId+" select[name='duplex_select']").attr("value", ETH_DUPLEX_AUTO);

				$("#"+trId+" :hidden[name='speed']").attr("value", ETH_SPEED_AUTO);
				$("#"+trId+" select[name='speed_select']").attr("value", ETH_SPEED_AUTO);
			}
		}

	}

/** switch port settings script end */

</script>

<div>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.switch.port.settings.label" />','switchPortSettings');</script></td>
		</tr>
		<tr>
			<td style="padding-left: 20px;">
			<div id="switchPortSettings" style="display: <s:property value="%{dataSource.switchPortSettingsStyle}"/>">
				<table cellspacing="0" cellpadding="0" border="0">
					<!-- Override LLDP/CDP -->
					<tr>
						<td colspan="12" style="padding-top:5px;">
							<s:checkbox id="overrideLldpCdp" name="dataSource.overrideNetworkPolicySetting" onclick="overrideNetworkPolicySetting(this.checked);"/>
							<s:text name="config.switchSettings.override.networkPolicy.settings"/>
						</td>
					</tr>
					<tr>
						<td><table id="srPortSettingsId" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.Interface" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.adminState" /></th>
								<!-- th align="left"><s:text
									name="hiveAp.switch.port.settings.column.nativeVlan" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.allowedVlans" /></th -->
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.duplex" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.speed" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.flowControl" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.autoMdix" /></th>
								<!--  th align="left"><s:text
									name="hiveAp.switch.port.settings.column.mtu" /></th -->
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.debounceTimer" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.lldpTransmit" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.lldpReceive" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.cdpReceive" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.clientReporting" /></th>
								<th align="left"><s:text
									name="hiveAp.switch.port.settings.column.description" /></th>
							</tr>
							<s:iterator value="dInterfaces" status="status">
								<tr id="r<s:property value="deviceIfType" />" class="<s:property value="%{#rowClass}"/>">
									<td>
										<s:hidden name="deviceIfType" value="%{deviceIfType}" />
										<span title='<s:property value="memberStr"/>' >
											<s:property value="interfaceNameEnum"/>
										</span>
									</td>
									<td>
										<s:select name="adminState_select" value="%{adminState}" list="%{enumAdminStateType}"
											listKey="key" listValue="value" cssStyle="width: 60px; "
											disabled="%{bindPortChannel}" />
										<s:hidden name="adminState" value="%{adminState}" />
									</td>
									<!--  td>
										<s:textfield name="nativeVlan_text" value="%{nativeVlan}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"
											disabled="%{bindPortChannel}"
											size="1" />
										<s:hidden name="nativeVlan" value="%{nativeVlan}" />
									</td>
									<td>
										<s:textfield name="allowedVlan_text" value="%{allowedVlan}" disabled="%{bindPortChannel}" size="5" />
										<s:hidden name="allowedVlan" value="%{allowedVlan}" />
									</td -->
									<td>
										<s:select name="duplex_select" value="%{duplex}" list="%{enumDuplexType}"
											disabled="%{bindPortChannel || portUSB}" cssStyle="width: 60px; "
											listKey="key" listValue="value"/>
										<s:hidden name="duplex" value="%{duplex}" />
									</td>
									<td>
										<s:select name="speed_select" value="%{speed}" list="%{enumSpeedType}"
											disabled="%{bindPortChannel || portUSB}" cssStyle="width: 60px; "
											listKey="key" listValue="value" />
										<s:hidden name="speed" value="%{speed}" />
									</td>
									<td>
										<s:select name="flowControlStatus_select" value="%{flowControlStatus}" list="%{enumFlowCtlType}"
											disabled="%{bindPortChannel || portUSB}" cssStyle="width: 74px; "
											listKey="key" listValue="value" />
										<s:hidden name="flowControlStatus" value="%{flowControlStatus}" />
									</td>
									<td>
										<s:checkbox name="autoMdix_check" value="%{autoMdix}"
											disabled="%{portSFP || portChannel || portUSB}" />
										<s:hidden name="autoMdix" value="%{autoMdix}" />
									</td>
									<!--  td>
										<s:textfield name="mtu" value="%{mtu}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"
											size="1"/>
									</td -->
									<td>
										<s:textfield name="debounceTimer_text" value="%{debounceTimer}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');"
											disabled="%{portChannel || portUSB}"  title="%{debounceTimerRange}" maxlength="4" size="4"/>
										<s:hidden name="debounceTimer" value="%{debounceTimer}" />
									</td>
									<td>
										<s:checkbox name="lldpTransmit_check" value="%{lldpTransmit}"
											disabled="%{portChannel || !dataSource.overrideNetworkPolicySetting || !lldpEnable || portUSB}"/>
										<s:hidden name="lldpTransmit" value="%{lldpTransmit}" />
									</td>
									<td>
										<s:checkbox name="lldpReceive_check" value="%{lldpReceive}"
											disabled="%{portChannel || !dataSource.overrideNetworkPolicySetting || !lldpEnable || portUSB}"/>
										<s:hidden name="lldpReceive" value="%{lldpReceive}" />
									</td>
									<td>
										<s:checkbox name="cdpReceive_check" value="%{cdpReceive}"
											disabled="%{portChannel || !dataSource.overrideNetworkPolicySetting || !cdpEnable || portUSB }" />
										<s:hidden name="cdpReceive" value="%{cdpReceive}" />
									</td>
									<td>
										<s:checkbox name="clientReporting_check" value="%{clientReporting}"
											disabled="%{!dataSource.overrideNetworkPolicySetting || !enableClientReporting }" />
										<s:hidden name="clientReporting" value="%{clientReporting}" />
									</td>
									<td>
										<s:textfield name="portDescription_check" value="%{portDescription}"
											disabled="%{!dataSource.overrideNetworkPolicySetting || !enableOverridePortDescription }" title="%{portDescription}"  maxlength="32" size="16" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
										<s:hidden name="portDescription" value="%{portDescription}" />
									</td>
								</tr>
							</s:iterator>
						</table></td>
					</tr>

				</table>
			</div>
			</td>
		</tr>
	</table>
</div>
</s:if>