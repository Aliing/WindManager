<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.network.StpSettings"%>
<s:hidden name="writeDisabled" value="%{writeDisabled}" />

<script>

var STP_MODE_MSTP = <%=StpSettings.STP_MODE_MSTP%>;
var STP_MODE_STP = <%=StpSettings.STP_MODE_STP%>;
var STP_MODE_RSTP = <%=StpSettings.STP_MODE_RSTP%>;

	function showHideSwitchSettingsDiv(value) {
		if (value == 1) {
			document.getElementById("showSwitchSettingsDiv").style.display = "none";
			document.getElementById("hideSwitchSettingsDiv").style.display = "block";
			var overrideStp = $("#" + formName + "_dataSource_deviceStpSettings_overrideStp");
			if(overrideStp){
				changeOverride(overrideStp.attr("checked") == "checked");	
			}
		}
		if (value == 2) {
			document.getElementById("showSwitchSettingsDiv").style.display = "block";
			document.getElementById("hideSwitchSettingsDiv").style.display = "none";
		}
	}

	function showHideDeviceSettingsDiv(value) {
		if (value == 1) {
			document.getElementById("showDeviceSettingsDiv").style.display = "none";
			document.getElementById("hideDeviceSettingsDiv").style.display = "block";
		}
		if (value == 2) {
			document.getElementById("showDeviceSettingsDiv").style.display = "block";
			document.getElementById("hideDeviceSettingsDiv").style.display = "none";
		}
	}

	function showHidePortLevelSettingsDiv(value) {
		if (value == 1) {
			document.getElementById("showPortLevelSettingsDiv").style.display = "none";
			document.getElementById("hidePortLevelSettingsDiv").style.display = "block";
		}
		if (value == 2) {
			document.getElementById("showPortLevelSettingsDiv").style.display = "block";
			document.getElementById("hidePortLevelSettingsDiv").style.display = "none";
		}
	}

	function showHideMstPortPrioritySettingsDiv(value) {
		if (value == 1) {
			document.getElementById("showMstPortPrioritySettingsDiv").style.display = "none";
			document.getElementById("hideMstPortPrioritySettingsDiv").style.display = "block";
		}
		if (value == 2) {
			document.getElementById("showMstPortPrioritySettingsDiv").style.display = "block";
			document.getElementById("hideMstPortPrioritySettingsDiv").style.display = "none";
		}
	}
	
	function showHideInstancePriorityDiv(value) {
		if (value == 1) {
			document.getElementById("showInstancePriorityDiv").style.display = "none";
			document.getElementById("hideInstancePriorityDiv").style.display = "block";
		}
		if (value == 2) {
			document.getElementById("showInstancePriorityDiv").style.display = "block";
			document.getElementById("hideInstancePriorityDiv").style.display = "none";
		}
	}

	function enabledStpCheckBox(value) {
		if (value) {
			$("#enabledStpMode").removeAttr("disabled");
			$("#deviceStpTable").removeAttr("disabled");
			$("#mstiTable").removeAttr("disabled");
			$("#portLevelTable").removeAttr("disabled");
			
			$("#enabledStpMode").show();
			$("#deviceStpTable").show();
			$("#mstiTable").show();
			$("#portLevelTable").show();
		} else {
			$("#enabledStpMode").attr("disabled","");
			$("#deviceStpTable").attr("disabled","");
			$("#portLevelTable").attr("disabled","");
			$("#mstiTable").attr("disabled", "");
			
			$("#enabledStpMode").hide();
			$("#deviceStpTable").hide();
			$("#portLevelTable").hide();
			$("#mstiTable").hide();
		}
	}

	function changeInterfaceStp(checked, portNum, isAuthEnabled){
		if (checked) {
			$("#edgePort_" + portNum).removeAttr("disabled");
			$("#pathCost_" + portNum).removeAttr("readOnly");
			$("#device_priority_" + portNum).removeAttr("disabled");
			
			if(isAuthEnabled){
				$("#edgePort_" + portNum).attr("disabled",true);
			}
			
			if ($("#edgePort_" + portNum).attr("checked")){
				$("#bpduMode_" + portNum).removeAttr("disabled");
			}else {
				$("#bpduMode_" + portNum).attr("disabled",true);
			}
			$("#stp_status_" + portNum).attr("value",true);
		} else {
			$("#edgePort_" + portNum).attr("disabled",true);
			$("#pathCost_" + portNum).attr("readonly",true);
			$("#device_priority_" + portNum).attr("disabled",true);
			$("#bpduMode_" + portNum).attr("disabled",true);
			$("#stp_status_" + portNum).attr("value",false);
		}
	}
	
	function changeEdgePort(checked,portNum) {
		
		if(checked){
			$("#bpduMode_" + portNum).removeAttr("disabled");
			$("#edge_port_status_" + portNum).attr("value",true);
		}else{
			$("#bpduMode_" + portNum).attr("disabled",true);
			$("#edge_port_status_" + portNum).attr("value",false);
		}
	}
	
	function changeBpduMode(data,id){
		document.getElementById("hidden_bpduMode_" + id).value = data;
	}
	
	function changePriority(data,id){
		document.getElementById("hidden_device_priority_" + id).value = data;
	}
	
	function changeInstancePriority(data,id){
		document.getElementById("instance_priority_" + id).value = data;
	}
	
	
/* 	function displayAll(data){
		if(data){
			$("#tbl_stp tbody.tbody").find("input[name = enableStpStatus]").each(function (){
			    if (!this.checked) {
			        $(this).parent().parent().show();
			    }
			});
		}else{
			$("#tbl_stp tbody.tbody").find("input[name = enableStpStatus]").each(function (){
			    if (!this.checked) {
			        $(this).parent().parent().hide();
			    }
			});
		}

		$("#tbl_stp tbody.tbody tr:visible:odd").each(function() {
		    $(this).removeClass().addClass("odd");
		});
		$("#tbl_stp tbody.tbody tr:visible:even").each(function() {
		    $(this).removeClass("even").removeClass("odd").addClass("even");
		});
	}
	 */
	
	function changeOverride(data){
		if(data){
			$("#enableStpCheckbox").show();
			$("#"+ formName + "_dataSource_deviceStpSettings_enableStp").removeAttr("disabled");
			enabledStpCheckBox($("#" + formName + "_dataSource_deviceStpSettings_enableStp").attr("checked"));
		}else{
			$("#enableStpCheckbox").hide();
			$("#"+ formName + "_dataSource_deviceStpSettings_enableStp").attr("disabled",true);
			enabledStpCheckBox(data);
		}
	}
	

	function onLoadAhDataTableForMstp(){
		var mstp_dataSource = eval('<s:property escape="false" value="mstpi_ahDtDatas"/>');
		var mstp_ahDtClumnDefs = eval('<s:property escape="false" value="mstpi_ahDtClumnDefs"/>');
		
		var myColumnDefs = [
		            		{
		            			type: "dropdown",
		            			mark: "mstp_interface",
		            			editMark:"edit_interface",
		            			display: '<s:text name="config.switchSettings.deviceSettings.allports.interface"/>',
		            			/* validate:validateVlans, */
		            			width:"100px",
		            		},
		            		{
		            			type: "dropdown",
		            			mark: "mstp_instance",
		            			editMark:"edit_instance",
		            			display: '<s:text name="config.switchSettings.mstp.instance"/> <s:text name="config.switchSettings.mstp.instance.range" />',
		            			width:"100px",
		            			changeCol:2,
		            			defaultValue: 0
		            		},
		                    {
		            			type: "dropdown",
		            			mark: "mstp_priority",
		            			editMark:"edit_prioity",
		            			display: '<s:text name="config.switchSettings.deviceSettings.allports.priority"/>  <s:text name="config.switchSettings.deviceSettings.allports.priority.range" />',
		            			defaultValue: 8,
		            			width:"120px",
		            		},
		            		{
		            			type: "text",
		            			mark: "mstp_path_cost",
		            			display: '<s:text name="config.switchSettings.deviceSettings.allports.path.cost"/>  <s:text name="config.switchSettings.deviceSettings.allports.path.cost.range" />',
		            			editMark:"edit_path_cost",
		            			defaultValue: "",
		            			keypress:"ten",
		            			validate:validateMstiPathCost,
		            			width:"150px",
		            			maxlength:9
		            		}
		            	];
		
		var myColumns = [];
		for (var i = 0; i < myColumnDefs.length; i++) {
			var optionTmp = myColumnDefs[i];
			var bln = false;
			if(mstp_ahDtClumnDefs){
				for (var j = 0; j < mstp_ahDtClumnDefs.length; j++) {
					if (myColumnDefs[i].mark == mstp_ahDtClumnDefs[j].mark) {
						optionTmp = $.extend(true, optionTmp, mstp_ahDtClumnDefs[j]);
						myColumns.push(optionTmp);
						bln = true;
						break;
					}
				}
			}
			if(!bln){
				myColumns.push(optionTmp);
			}
			
		}
		var ahDataTable = new AhDataTablePanel.DataTablePanel("ahDataTableForMSTP",myColumns,mstp_dataSource);
		ahDataTable.render();
	}
	
	function validateForceVersion(){
		var overrideStp = $("#" + formName + "_dataSource_deviceStpSettings_overrideStp");
		var enableStp = $("#" + formName + "_dataSource_deviceStpSettings_enableStp");
		if(overrideStp.attr("checked") == "checked" && enableStp.attr("checked") == "checked"){
			var inputElement = document.getElementById(formName + "_dataSource_deviceStpSettings_forceVersionString");
			if (inputElement.value.length == 0 || inputElement.value == "") {
		        return true;
		    }
			
			if (inputElement.value == 1) {
				 hm.util.reportFieldError(inputElement,'<s:text name="error.stp.force.version.not.support"><s:param>'+inputElement.value+'</s:param></s:text>');
				 $("#deviceStpTable").show();
				 showHideSwitchSettingsDiv(1);
			     showHideDeviceSettingsDiv(1);
			     inputElement.focus();
			     return false;
			}
			
			var maxVal;
			var minVal = 0;
			
			var stpMode = document.getElementById("stpMode");
			if(stpMode.value == STP_MODE_RSTP){
				maxVal = 2;
			}else if(stpMode.value == STP_MODE_STP && inputElement.value != 0) {
				hm.util.reportFieldError(inputElement,'<s:text name="error.stp.force.version.not.support"><s:param>'+inputElement.value+'</s:param></s:text>');
				showHideSwitchSettingsDiv(1);
				$("#deviceStpTable").show();
				showHideDeviceSettingsDiv(1);
				inputElement.focus();
				return false;
			}else{
				maxVal = 3;
			}
			
		    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.switchSettings.deviceSettings.force.version" />',minVal,maxVal);
		    if (message != null) {
		        hm.util.reportFieldError(inputElement, message);
		        showHideSwitchSettingsDiv(1);
				$("#deviceStpTable").show();
				showHideDeviceSettingsDiv(1);
		        inputElement.focus();
		        return false;
		    }
		}
	    return true;
	}
	
	function validateDevicePathCost(){
		var overrideStp = $("#" + formName + "_dataSource_deviceStpSettings_overrideStp");
		var enableStp = $("#" + formName + "_dataSource_deviceStpSettings_enableStp");
		var device_path_cost = document.getElementsByName("device_path_cost");
		if(overrideStp.attr("checked") == "checked" && enableStp.attr("checked") == "checked"){
			for (var i = 0; i < device_path_cost.length; i++) {
				if(device_path_cost[i].value != ""){
					var message = hm.util.validateIntegerRange(device_path_cost[i].value, '<s:text name="config.switchSettings.deviceSettings.allports.path.cost" />',1,200000000);
					if (message != null) {
				        hm.util.reportFieldError(device_path_cost[i], message);
				        showHideSwitchSettingsDiv(1);
				        $("#portLevelTable").show();
				        showHidePortLevelSettingsDiv(1);
				        device_path_cost[i].focus();
				        return false;
				    }	    	
				}
			}
		}
		
		return true;
	}
	
	function validateMstiPriority(data){
		var overrideStp = $("#" + formName + "_dataSource_deviceStpSettings_overrideStp");
		var enableStp = $("#" + formName + "_dataSource_deviceStpSettings_enableStp");
		if(overrideStp.attr("checked") == "checked" && enableStp.attr("checked") == "checked"){
			if(data.length < 1 || data == ""){
	    		hm.util.reportFieldError(document.getElementById("errorMessage"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.deviceSettings.allports.priority" /></s:param></s:text>');
	    		$("#mstiTable").show();
	    		return false;
	    	}
			
			var message = hm.util.validateIntegerRange(data, '<s:text name="config.switchSettings.deviceSettings.allports.priority" />',0,240);
			if (message != null) {
		        hm.util.reportFieldError(document.getElementById("errorMessage"), message);
		        $("#mstiTable").show();
		        return false;
		    }
		}
		return true;
	}
	
	function validateMstiPathCost(data){
		var overrideStp = $("#" + formName + "_dataSource_deviceStpSettings_overrideStp");
		var enableStp = $("#" + formName + "_dataSource_deviceStpSettings_enableStp");
		if(data == ""){
			return true;
		}
		if(overrideStp.attr("checked") == "checked" && enableStp.attr("checked") == "checked"){
			var message = hm.util.validateIntegerRange(data, '<s:text name="config.switchSettings.deviceSettings.allports.path.cost" />',1,200000000);
			if (message != null) {
		        hm.util.reportFieldError(document.getElementById("errorMessage"), message);
		        return false;
		    }
		}
		return true;
	}
	
	function changeForwardingTime(data){
		$("#device_max_age").attr("value",data);
		$("#max_age").attr("value",data);
	}
	
	function changeRestrict(checked){
		if(checked){
			Get("forceVer").disabled = false;
		}else{
			Get("forceVer").disabled = true;
		}
	}
</script>

<!-- switch settings -->
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24 && dataSource.configTemplate.switchSettings.stpSettings.enableStp}">
	<tr>
		<td>
			<div style="display:<s:property value="%{showSwitchSettingsDiv}"/>"
				id="showSwitchSettingsDiv">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="height: 10px"></td>
					</tr>
					<tr>
						<td onclick="showHideSwitchSettingsDiv(1);"
							style="cursor: pointer; padding-left: 0px;"><img
							src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
							alt="Show Option" class="expandImg" style="display: inline" />&nbsp;<s:text
								name="config.switchSettings.stpSettings" /></td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			<div style="display:<s:property value="%{hideSwitchSettingsDiv}"/>"
				id="hideSwitchSettingsDiv">
				<table cellspacing="0" cellpadding="0" border="0" width="100%"
					style="padding-left: 0px;">
					<tr>
						<td style="height: 10px"></td>
					</tr>
					<tr>
						<td onclick="showHideSwitchSettingsDiv(2);"
							style="cursor: pointer; padding-left: 0px;"><img
							src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
							alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;<s:text
								name="config.switchSettings.stpSettings" /></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td style="padding-left: 15px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td style="padding: 5px 0px 0px 0px;width:25px;">
													<s:hidden id="configTemplateStp" value="%{dataSource.configTemplate.switchSettings.stpSettings.enableStp}" />
													<s:checkbox
														name="dataSource.deviceStpSettings.overrideStp"
														onclick="changeOverride(this.checked);" />
												</td>
												<td style="padding: 3px 0px 0px 0px;">
													<s:text name="config.switchSettings.deviceSettings.stp.override" />
												</td>
											</tr>
											<tr id="enableStpCheckbox"
												style="display:<s:property value='showHideStpCheckbox'/>">
												<td style="padding: 5px 0px 0px 0px;width:25px;"><s:checkbox
														name="dataSource.deviceStpSettings.enableStp"
														onclick="enabledStpCheckBox(this.checked);"
														disabled="!dataSource.deviceStpSettings.overrideStp && !dataSource.deviceStpSettings.enableStp" />
													<s:hidden id="stpMode" value="%{dataSource.deviceStpSettings.stp_mode}"/>
												</td>
												<td style="padding: 3px 0px 0px 0px;">
													 <s:text
														name="config.switchSettings.enableSTP"></s:text>
												</td>
											</tr>
											<tr>
												<td colspan="4" style="padding-left:15px">
													<table border="0" cellspacing="0" cellpadding="0"
														width="100%" id="deviceStpTable"
														disabled="<s:property value="%{dataSource.deviceStpSettings.enabledStpMode}"/>">
														<tr>
															<td colspan="4">
																<div
																	style="display:<s:property value="%{showDeviceSettingsDiv}"/>"
																	id="showDeviceSettingsDiv">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td class="labelT1"
																				onclick="showHideDeviceSettingsDiv(1);"
																				style="cursor: pointer;"><img
																				src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																				alt="Show Option" class="expandImg"
																				style="display: inline" />&nbsp;&nbsp;<s:text
																					name="config.switchSettings.deviceSettings" /></td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
														<tr>
															<td colspan="4">
																<div
																	style="display:<s:property value="%{hideDeviceSettingsDiv}"/>"
																	id="hideDeviceSettingsDiv">
																	<table cellspacing="0" cellpadding="0" border="0"
																		width="100%">
																		<tr>
																			<td class="labelT1"
																				onclick="showHideDeviceSettingsDiv(2);"
																				style="cursor: pointer;"><img
																				src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																				alt="Hide Option" class="expandImg"
																				style="display: inline" />&nbsp;&nbsp;<s:text
																					name="config.switchSettings.deviceSettings" /></td>
																		</tr>
																		<tr>
																			<td style="padding-left: 20px">
																				<table border="0" cellspacing="0" cellpadding="0"
																					width="100%">
																					<tr>
																						<td>
																							<table cellspacing="0" cellpadding="0"
																								border="0">
																								<tr id="helloTime">
																									<td class="labelT1" width="150px">
																										<s:text
																											name="config.switchSettings.deviceSettings.hello.time"></s:text>
																									</td>
																									<td>
																										<s:hidden id="device_helloTime" name="dataSource.deviceStpSettings.helloTime" value="%{dataSource.deviceStpSettings.helloTime}"/>
																										<s:select name="helloTime" list="%{helloTimeItem}" listKey="key" cssStyle="width:80px" disabled="true"
																											listValue="value" value="%{dataSource.deviceStpSettings.helloTime}"/><s:text
																											name="config.switchSettings.deviceSettings.hello.time.unit"></s:text>
																									</td>
																								</tr>
																								<tr>
																									<td class="labelT1">
																										<s:text
																											name="config.switchSettings.deviceSettings.forward.delay"></s:text>
																									</td>
																									<td>
																										<s:select name="dataSource.deviceStpSettings.timerItem" list="%{forwardingDelayItem}" 
																											listKey="key" cssStyle="width:80px;" onchange="changeForwardingTime(this.value)"
																											listValue="value" value="%{dataSource.deviceStpSettings.timerItem}"/><s:text
																											name="config.switchSettings.deviceSettings.hello.time.unit"></s:text>
																									</td>
																								</tr>
																								<tr>
																									<td class="labelT1">
																										<s:text
																											name="config.switchSettings.deviceSettings.max.age"></s:text>
																									</td>
																									<td>
																										<s:hidden id="device_max_age" name="dataSource.deviceStpSettings.maxAge" value="%{dataSource.deviceStpSettings.timerItem}"/>
																										<s:select id="max_age" name="maxAge" list="%{maxAgeItem}" listKey="key" cssStyle="width:80px" disabled="true" 
																											listValue="value" value="%{dataSource.deviceStpSettings.timerItem}"/><s:text
																											name="config.switchSettings.deviceSettings.hello.time.unit"></s:text>
																									</td>
																								</tr>
																								<tr>
																									<td class="labelT1">
																										<s:text
																											name="config.switchSettings.deviceSettings.priority"></s:text>
																									</td>
																									<td>
																										<s:select name="dataSource.deviceStpSettings.times" list="%{priorityList}" listKey="key" cssStyle="width:80px;"  
																											listValue="value" value="%{dataSource.deviceStpSettings.times}"/>
																									</td>
																								</tr>
																								<tr>
																									<td colspan="4" style="padding-bottom: 5px;">
																										<table border="0" cellspacing="0" cellpadding="0" width="100%">
																											<s:if test="%{dataSource.deviceStpSettings.stp_mode != 1}">
																												<tr>
																													<td style="padding-left: 7px; padding-top: 3px;width:25px">
																														<s:checkbox name="dataSource.deviceStpSettings.restrict" value="%{dataSource.deviceStpSettings.restrict}"
																																	onchange="changeRestrict(this.checked)"/>
																													</td>
																													<td style="padding: 5px 5px 0px 0px; width: 180px;">
																														<s:text	name="config.switchSettings.deviceSettings.force.version.restrict" />
																													</td>
																													<td style="padding-top: 3px;">
																														<s:select id="forceVer" name="dataSource.deviceStpSettings.forceVersionString" list="%{forceVersionItem}" listKey="key" disabled="%{!dataSource.deviceStpSettings.restrict}"
																															listValue="value" value="%{dataSource.deviceStpSettings.forceVersion == -1 ? dataSource.deviceStpSettings.stp_mode : dataSource.deviceStpSettings.forceVersion}"/>
																													</td>
																												</tr>
																											</s:if>
																											<s:else>
																												<td style="padding-left: 7px; padding-top: 3px;width:25px">
																													<s:checkbox name="dataSource.deviceStpSettings.restrict" value="%{dataSource.deviceStpSettings.restrict}"
																																disabled="true" />
																												</td>
																												<td style="padding: 5px 5px 0px 0px;">
																													<s:text	name="config.switchSettings.deviceSettings.force.version.restrict" />
																												</td>
																												<td></td>
																											</s:else>
																											<tr>
																												<td class="noteInfo" style="padding-left: 10px; padding-top: 5px" colspan="4">
																													<s:text	name="config.switchSettings.deviceSettings.force.version.restrict.note" />
																												</td>
																											</tr>
																										</table>
																									</td>
																								</tr>
																								<s:if test="mstpEnable">
																									<tr>
																										<td colspan="4">
																											<table border="0" cellspacing="0" cellpadding="0" width="100%" id="instancePriority"
																													disabled="<s:property value="%{dataSource.deviceStpSettings.enabledStpMode}"/>">
																												<tr>
																													<td>
																														<div
																															style="display:<s:property value="%{showMstPortPrioritySettingsDiv}"/>"
																															id="showInstancePriorityDiv">
																															<table cellspacing="0" cellpadding="0" border="0">
																																<tr>
																																	<td class="labelT1"
																																		onclick="showHideInstancePriorityDiv(1);"
																																		style="cursor: pointer;"><img
																																		src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																																		alt="Show Option" class="expandImg"
																																		style="display: inline" />&nbsp;&nbsp;<s:text
																																		name="config.switchSettings.mstp.instance.priority" /></td>
																																</tr>
																															</table>
																														</div>
																													</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																									<tr>
																										<td colspan="4">
																											<div
																												style="display:<s:property value="%{hideMstPortPrioritySettingsDiv}"/>"
																												id="hideInstancePriorityDiv">
																												<table cellspacing="0" cellpadding="0"
																													border="0" width="100%">
																													<tr>
																														<td class="labelT1"
																															onclick="showHideInstancePriorityDiv(2);"
																															style="cursor: pointer;"><img
																															src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																															alt="Hide Option" class="expandImg"
																															style="display: inline" />&nbsp;&nbsp;<s:text
																																name="config.switchSettings.mstp.instance.priority" /></td>
																													</tr>
																													<tr>
																														<td style="padding-left: 30px">
																															<table cellspacing="0" cellpadding="0"
																																border="0" id="tbl_instance_priority"
																																class="view">
																																<tr>
																																	<th class="colList" width="150em;">
																																		<s:text
																																			name="config.switchSettings.mstp.instance" />
																																	</th>
																																	<th class="colList" width="150em;">
																																		<s:text
																																			name="config.switchSettings.deviceSettings.allports.priority" />
																																		<s:text
																																			name="config.switchSettings.mstp.priority.range" />
																																	</th>
																																</tr>
																																<s:iterator
																																	value="%{allInstancePrioritySettings}"
																																	status="status"
																																	id="allInstancePrioritySettings">
																																	<tiles:insertDefinition name="rowClass" />
																																	<tr
																																		class="<s:property value="%{#rowClass}"/>"
																																		style="height: 25px;">
																																		<td class="colList">
																																			<s:label name="deviceInstance"
																																				value="%{#allInstancePrioritySettings.instance}" />
																																			<s:hidden name="device_instance_id" value="%{#allInstancePrioritySettings.instance}"/>
																																		</td>
																																		<td><s:hidden
																																				id="instance_priority_%{#allInstancePrioritySettings.instance}"
																																				name="device_instance_priority"
																																				value="%{#allInstancePrioritySettings.times}" />
																																			<s:select
																																				list="%{instancePriorityList}"
																																				listKey="id" listValue="value"
																																				value="%{#allInstancePrioritySettings.times}"
																																				cssStyle="width:100px"
																																				onchange="changeInstancePriority(this.value,%{#allInstancePrioritySettings.instance})" />
																																		</td>
																																	</tr>
																																</s:iterator>
																															</table>
																														</td>
																													</tr>
																												</table>
																											</div>
																										</td>
																									</tr>
																								</s:if>
																							</table>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td colspan="4" style="padding-left:15px">
													<table border="0" cellspacing="0" cellpadding="0"
														width="100%" id="portLevelTable"
														disabled="<s:property value="%{dataSource.deviceStpSettings.enabledStpMode}"/>">
														<tr>
															<td>
																<div
																	style="display:<s:property value="%{showPortLevelSettingsDiv}"/>"
																	id="showPortLevelSettingsDiv">
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td class="labelT1"
																				onclick="showHidePortLevelSettingsDiv(1);"
																				style="cursor: pointer;"><img
																				src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																				alt="Show Option" class="expandImg"
																				style="display: inline" />&nbsp;&nbsp;<s:text
																				name="config.switchSettings.portLevelSettings" /></td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
														<tr>
															<td colspan="4">
																<div
																	style="display:<s:property value="%{hidePortLevelSettingsDiv}"/>"
																	id="hidePortLevelSettingsDiv">
																	<table cellspacing="0" cellpadding="0" border="0"
																		width="100%">
																		<tr>
																			<td class="labelT1"
																				onclick="showHidePortLevelSettingsDiv(2);"
																				style="cursor: pointer;"><img
																				src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																				alt="Hide Option" class="expandImg"
																				style="display: inline" />&nbsp;&nbsp;<s:text
																				name="config.switchSettings.portLevelSettings" /></td>
																		</tr>
																		<tr>
																			
																			<td style="padding-left: 15px">
																				<table cellspacing="0" cellpadding="0" border="0"
																					width="90%">
																					<tr>
																						<td class="noteInfo" style="padding-left: 20px">
																							<s:text name="warning.stp.edge.port.enabled.warning" />
																						</td>
																					</tr>
																				</table>
																				
																			</td>			
																		</tr>
																		<tr>
																			<td style="padding-left: 15px">
																				<table cellspacing="0" cellpadding="0" border="0"
																					width="90%">
																					<tr>
																						<td style="padding-left: 20px;">
																							<table cellspacing="0" cellpadding="0"
																								border="0" id="tbl_stp" class="view">
																								<tr>
																									<th class="colList" width="80em;"><s:text
																											name="config.switchSettings.deviceSettings.allports.interface" /></th>
																									<th class="colList" width="50em"
																										align="center"><s:text
																											name="config.switchSettings.deviceSettings.allports.enable" /></th>
																									<th class="colList" width="80em"
																										align="center"><s:text
																											name="config.switchSettings.deviceSettings.allports.edge" /></th>
																									<th class="colList" width="150em;"><s:text
																											name="config.switchSettings.deviceSettings.allports.bpdu.mode" /></th>
																									<th class="colList" width="150em;"><s:text
																											name="config.switchSettings.deviceSettings.allports.priority" />
																										<s:text
																											name="config.switchSettings.deviceSettings.allports.priority.range" /></th>
																									<th class="colList" width="150em;"
																										align="center"><s:text
																											name="config.switchSettings.deviceSettings.allports.path.cost" />
																										<s:text
																											name="config.switchSettings.deviceSettings.allports.path.cost.range" /></th>
																								</tr>
																								<tbody class="tbody">
																									<s:iterator value="%{allPortLevelSettings}"
																										status="status" id="allPortLevelSettings">
																									<tiles:insertDefinition name="rowClass" />
																										<tr
																											class="<s:property value="%{#rowClass}"/>"
																											style="height: 25px;">
																											<td class="colList"><s:hidden
																												name="interfaceNums"
																												value="%{#allPortLevelSettings.interfaceNum}" />
																												<s:hidden name="devicePortNames"
																													value="%{#allPortLevelSettings.devicePortName}" 
																													disabled="%{!#allPortLevelSettings.enableStp || #allPortLevelSettings.portChannelMemberPort}" />
																												<%-- <s:property value="%{#allPortLevelSettings.devicePortName}"/> --%>
																												<s:label name="devicePortNames"
																													value="%{#allPortLevelSettings.devicePortName}" />
																											</td>
																											<td class="colList">
																												<s:hidden id="stp_status_%{#allPortLevelSettings.interfaceNum}" name="enableStpStatus" value="%{#allPortLevelSettings.enableStp}"/>
																												<s:checkbox
																													name="interfaceStpEnable"
																													value="%{#allPortLevelSettings.enableStp}"
																													onclick="changeInterfaceStp(this.checked,%{#allPortLevelSettings.interfaceNum},%{#allPortLevelSettings.enableAuth})"
																													fieldValue="%{#allPortLevelSettings.interfaceNum}" disabled="%{#allPortLevelSettings.portChannelMemberPort}"/>
																											</td>
																											<td class="colList" align="center">
																												<s:hidden id="edge_port_status_%{#allPortLevelSettings.interfaceNum}" name="edgePorts" value="%{#allPortLevelSettings.edgePort}"/>
																												<s:checkbox
																													id="edgePort_%{#allPortLevelSettings.interfaceNum}"
																													name="interfaceEdgePorts"
																													value="%{#allPortLevelSettings.edgePort}"
																													onclick="changeEdgePort(this.checked,%{#allPortLevelSettings.interfaceNum})"
																													disabled="%{!#allPortLevelSettings.enableStp || #allPortLevelSettings.portChannelMemberPort || #allPortLevelSettings.enableAuth}"
																													fieldValue="%{#allPortLevelSettings.interfaceNum}" />
																											</td>
																											<td class="colList"><s:hidden
																													id="hidden_bpduMode_%{#allPortLevelSettings.interfaceNum}"
																													name="hidden_bpduModes"
																													value="%{#allPortLevelSettings.bpduMode}" />
																												<s:select
																													id="bpduMode_%{#allPortLevelSettings.interfaceNum}"
																													name="bpduModes"
																													value="%{#allPortLevelSettings.bpduMode}"
																													list="%{bpduModeItem}" listKey="key"
																													listValue="value" cssStyle="width:100px"
																													disabled="%{!#allPortLevelSettings.edgePort || !#allPortLevelSettings.enableStp || #allPortLevelSettings.portChannelMemberPort}"
																													onchange="changeBpduMode(this.value,%{#allPortLevelSettings.interfaceNum})" />
																											</td>
																											<td class="colList">
																											<s:hidden
																													id="hidden_device_priority_%{#allPortLevelSettings.interfaceNum}"
																													name="device_priority"
																													value="%{#allPortLevelSettings.times}" />
																												<s:select id="device_priority_%{#allPortLevelSettings.interfaceNum}"
																														  name="device_stp_priority" list="%{devicePriorityList}" 
																													      listKey="key" listValue="value" value="%{#allPortLevelSettings.times}" 
																													      cssStyle="width:100px" 
																													      disabled="%{!#allPortLevelSettings.enableStp || #allPortLevelSettings.portChannelMemberPort}"
																													      onchange="changePriority(this.value,%{#allPortLevelSettings.interfaceNum})" />
																											</td>
																											<td class="colList">
																												<s:textfield
																													name="device_path_cost"
																													id="pathCost_%{#allPortLevelSettings.interfaceNum}"
																													value="%{#allPortLevelSettings.defaultPathCost}"
																													onkeypress="return hm.util.keyPressPermit(event,'ten');"
																													size="7" cssStyle="width:100px"
																													readonly="%{!#allPortLevelSettings.enableStp || #allPortLevelSettings.portChannelMemberPort}" />
																											</td>
																										</tr>
																									</s:iterator>
																								</tbody>
																							</table>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
														<s:if test="mstpEnable">
															<tr>
																<td colspan="4">
																	<div
																		style="display:<s:property value="%{showMstPortPrioritySettingsDiv}"/>"
																		id="showMstPortPrioritySettingsDiv">
																		<table cellspacing="0" cellpadding="0" border="0">
																			<tr>
																				<td class="labelT1"
																					onclick="showHideMstPortPrioritySettingsDiv(1);"
																					style="cursor: pointer;"><img
																					src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																					alt="Show Option" class="expandImg"
																					style="display: inline" />&nbsp;&nbsp;<s:text
																					name="config.switchSettings.mstp.priority.settings" />
																				</td>
																			</tr>
																		</table>
																	</div>
																</td>
															</tr>
														</s:if>
													</table>
													<table border="0" cellspacing="0" cellpadding="0"
														width="100%" id="mstiTable"
														disabled="<s:property value="%{dataSource.deviceStpSettings.enabledStpMode}"/>">
														<tr>
															<td colspan="4">
																<div
																	style="display:<s:property value="%{hideMstPortPrioritySettingsDiv}"/>"
																	id="hideMstPortPrioritySettingsDiv">
																	<table cellspacing="0" cellpadding="0" border="0"
																		width="100%">
																		<tr>
																			<td class="labelT1" colspan="5"
																				onclick="showHideMstPortPrioritySettingsDiv(2);"
																				style="cursor: pointer;"><img
																				src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																				alt="Hide Option" class="expandImg"
																				style="display: inline" />&nbsp;&nbsp;<s:text
																				name="config.switchSettings.mstp.priority.settings" />
																			</td>
																		</tr>
																		<tr>
																			<td style="padding-left:25px">
																				<s:label id="errorMessage" />
																			</td>
																			<td colspan="4">
																				<div id="ahDataTableForMSTP"></div>
																			</td>
																		</tr>
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
			</div>
		</td>
	</tr>
</s:if>
<s:elseif test="%{dataSource.deviceInfo.sptEthernetMore_24}">
	<tr>
		<td>
			<div style="display:<s:property value="%{showSwitchSettingsDiv}"/>"
				id="showSwitchSettingsDiv">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="height: 10px"></td>
					</tr>
					<tr>
						<td onclick="showHideSwitchSettingsDiv(1);"
							style="cursor: pointer; padding-left: 0px;"><img
							src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
							alt="Show Option" class="expandImg" style="display: inline" />&nbsp;<s:text
								name="config.switchSettings.stpSettings" /></td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			<div style="display:<s:property value="%{hideSwitchSettingsDiv}"/>"
				id="hideSwitchSettingsDiv">
				<table cellspacing="0" cellpadding="0" border="0" width="100%"
					style="padding-left: 0px;">
					<tr>
						<td style="height: 10px"></td>
					</tr>
					<tr>
						<td onclick="showHideSwitchSettingsDiv(2);"
							style="cursor: pointer; padding-left: 0px;"><img
							src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
							alt="Hide Option" class="expandImg" style="display: inline" />&nbsp;<s:text
								name="config.switchSettings.stpSettings" /></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td style="padding-left: 15px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="noteInfo" style="padding: 5px 0px 0px 4px;">
													<s:text name="note.stp.override.settings"/>
												</td>
											</tr>
											<tr>
												<td style="padding: 5px 0px 0px 0px;">
												<s:checkbox
														name="overrideStp"
														disabled="true"/>
														<s:text
														name="config.switchSettings.deviceSettings.stp.override" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
</s:elseif>