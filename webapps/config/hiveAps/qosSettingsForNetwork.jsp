<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
function showOrHideMapPanel(value){
	if (value) {
		Get("hideOverrideMapPanelDiv").style.display="block";
	}  else {
		Get("hideOverrideMapPanelDiv").style.display="none";
	}
}

function changeCheckboxValue(idStart,index, ethFlg){
	var changeId = document.getElementById(idStart + "_" + index);
	if (changeId.checked) {
		if (idStart=='arrayCheckE') {
			document.getElementById('arrayCheckD_' + index).checked=false;
		} else if (idStart=='arrayCheckET'){
			document.getElementById('arrayCheckDT_' + index).checked=false;
		} else if (idStart=='arrayCheckP'){
			document.getElementById('arrayCheckD_' + index).checked=false;
		} else if (idStart=='arrayCheckPT'){
			document.getElementById('arrayCheckDT_' + index).checked=false;
		} else if (idStart=='arrayCheckD'){
			if (ethFlg) {
				document.getElementById('arrayCheckP_' + index).checked=false;
			} else {
				document.getElementById('arrayCheckE_' + index).checked=false;
			}
		} else if (idStart=='arrayCheckDT'){
			if (ethFlg) {
				document.getElementById('arrayCheckPT_' + index).checked=false;
			} else {
				document.getElementById('arrayCheckET_' + index).checked=false;
			}
		}
	}
}

function changeSsidOnlyCheckBox(index, count, name){


	for(var i=0;i<count;i++){
		Get('arrayNetwork_' + i).disabled=false;
		Get('arrayMacOui_' + i).disabled=false;
		Get('arraySsid_' + i).disabled=false;
		Get('arrayCheckD_' + i).disabled=false;
		Get('arrayCheckDT_' + i).disabled=false;
		
		if (Get('arrayCheckE_' + i)!=null && Get('arrayCheckE_' + i)!='undefined') {
			Get('arrayCheckE_' + i).disabled=false;
			Get('arrayCheckET_' + i).disabled=false;
		}
		if (Get('arrayCheckP_' + i)!=null && Get('arrayCheckP_' + i)!='undefined') {
			Get('arrayCheckP_' + i).disabled=false;
			Get('arrayCheckPT_' + i).disabled=false;
		}
	}

	if (document.getElementById('arraySsidOnly_' + index).checked){	
		Get('arrayNetwork_' + index).checked=false;
		Get('arrayMacOui_' + index).checked=false;
		Get('arraySsid_' + index).checked=false;
		Get('arrayCheckD_' + index).checked=false;
		Get('arrayCheckDT_' + index).checked=false;
		
		Get('arrayNetwork_' + index).disabled=true;
		Get('arrayMacOui_' + index).disabled=true;
		Get('arraySsid_' + index).disabled=true;
		Get('arrayCheckD_' + index).disabled=true;
		Get('arrayCheckDT_' + index).disabled=true;
		if (name=='ssid') {
			Get('arrayCheckE_' + index).checked=false;
			Get('arrayCheckET_' + index).checked=false;
			Get('arrayCheckE_' + index).disabled=true;
			Get('arrayCheckET_' + index).disabled=true;
		} else {
			Get('arrayCheckP_' + index).checked=false;
			Get('arrayCheckPT_' + index).checked=false;
			Get('arrayCheckP_' + index).disabled=true;
			Get('arrayCheckPT_' + index).disabled=true;
		}
		for(var i=0;i<count;i++){
			if (i!=index){
				Get('arraySsidOnly_' + i).checked=false;
			}
		}
	}
}

function validateSlaSettings(){
	var slaInterval = Get("networkPolicyQoS_dataSource_slaInterval");
	var message = hm.util.validateIntegerRange(slaInterval.value, '<s:text name="config.userprofile.sla.interval" />',
            <s:property value="%{slaIntervalRange.min()}" />,
            <s:property value="%{slaIntervalRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(slaInterval, message);
		slaInterval.focus();
		return false;
	}
	return true;
}

function prepareQosSaveBtPerrmit(){
	<s:if test="%{savePermit==false}">
		Get("btQoSSetting").style.display="none";
	</s:if>
	<s:else>
		Get("btQoSSetting").style.display="";
	</s:else>
}
window.setTimeout("prepareQosSaveBtPerrmit()", 100);


</script>
<div>
<s:form action="networkPolicy" name="networkPolicyQoS" id="networkPolicyQoS">
	<table width="880px" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:0 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-qos-big.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle"><s:text name="config.configTemplate.qosSettings"/></td>
							</tr>
						</table>
					</td>
					<td align="right">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" onclick="hideQosSettingNewDialog();" class="btCurrent" title="<s:text name="config.networkpolicy.button.cancel"/>"><span><s:text name="config.networkpolicy.button.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton"><a href="javascript:void(0);" onclick="saveQosSettingNewDialog();" id="btQoSSetting" style="display:none;" class="btCurrent" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
							</tr>
						</table>
					</td>
				
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding: 4px 4px 4px 10px;">
				<table cellspacing="0" cellpadding="0" border="0" class="embedded">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td style="padding: 0 0 0 2px" width="90px"><s:text name="config.configTemplate.classifierMap" /></td>
									<td><s:select name="classifierMapId"
										list="%{list_classifierMap}" listKey="id" listValue="value"
										cssStyle="width: 230px;" /></td>
									<td>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('newClassifierMap')"><img class="dinl"
											src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('editClassifierMap')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</td>
									<td style="padding: 0 10px 0 30px"><s:text name="config.configTemplate.markerMap" /></td>
									<td><s:select name="markerMapId"
										list="%{list_markerMap}" listKey="id" listValue="value"
										cssStyle="width: 230px;" /></td>
									<td>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('newMarkerMap')"><img class="dinl"
											src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('editMarkerMap')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="4px"></td>
					</tr>
					<tr>
						<td style="padding: 2px 0 4px 6px">
							<s:checkbox name="dataSource.enabledMapOverride" value="%{dataSource.enabledMapOverride}" 
								onclick="showOrHideMapPanel(this.checked);"></s:checkbox>
								<s:text name="config.configTemplate.enabledMapOverride"></s:text>
						</td>
					</tr>
					<tr>
						<td height="4px"></td>
					</tr>
					<tr>
						<td style="padding-left: 25px">
							<div style="display:<s:property value="%{hideOverrideMapPanel}"/>" id="hideOverrideMapPanelDiv">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<th align="center" colspan="8"><s:text
										name="config.configTemplate.classificationOverride" /></th>
									<th align="center" colspan="2"><s:text
										name="config.configTemplate.markingOverride" /></th>
								</tr>
								<tr>
									<td class="list"><s:text
										name="config.configTemplate.interfaceSsid" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.ssidOnly" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.network" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.macOui" /></td>
									<td class="list" width="50px"><s:text
										name="config.configTemplate.qos.ssid" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.11e" />/<s:text
										name="config.configTemplate.qos.11p" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.diff" /></td>
									<td class="list" width="30px"></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.11e" />/<s:text
										name="config.configTemplate.qos.11p" /></td>
									<td class="list"><s:text
										name="config.configTemplate.qos.diff" /></td>
								</tr>
								<s:iterator value="%{dataSource.ssidInterfaces.values}"
									status="status" id="templateSsid">
									<s:if test="%{interfaceName=='eth0'}">
										<tr>
											<td valign="top" class="list"><s:property
												value="%{interfaceName}" /></td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsidOnly" value="%{#templateSsid.ssidOnlyEnabled}" 
													id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
													onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth0');"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayNetwork" value="%{#templateSsid.networkServicesEnabled}" 
													id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}" 
													disabled="%{#templateSsid.disabledField}" ></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayMacOui" value="%{#templateSsid.macOuisEnabled}" 
													id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsid" value="%{#templateSsid.ssidEnabled}" 
													id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckP" value="%{#templateSsid.CheckP}" 
													id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
													disabled="%{#templateSsid.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckD" value="%{#templateSsid.checkD}" 
													id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
													disabled="%{#templateSsid.disabledField}"></s:checkbox>
											</td>
											<td class="list" width="30px">&nbsp;</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckPT" value="%{#templateSsid.CheckPT}" 
													id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
													disabled="%{#templateSsid.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckDT" value="%{#templateSsid.checkDT}" 
													id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid.disabledField}"
													onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);" ></s:checkbox>
											</td>
											<%--td valign="top" class="list">
												<s:select name="qosClassifications"
													value="%{#templateSsid.classfierAndMarker.id}"
													id="classification_%{#status.index}"
													list="%{list_qosClassification}" listKey="id"
													listValue="value" cssStyle="width: 230px;"
													onchange="addClassification('%{#status.index}','%{#templateSsid.interfaceName}');" />
											</td--%>
										</tr>
									</s:if>
								</s:iterator>
								<s:iterator value="%{dataSource.ssidInterfaces.values}"
									status="status" id="templateSsid3">
									<s:if test="%{interfaceName=='eth1'}">
										<tr>
											<td valign="top" class="list"><s:property
												value="%{interfaceName}" /></td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsidOnly" value="%{#templateSsid3.ssidOnlyEnabled}" 
													id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
													onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'eth1');"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayNetwork" value="%{#templateSsid3.networkServicesEnabled}" 
													id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayMacOui" value="%{#templateSsid3.macOuisEnabled}" 
													id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsid" value="%{#templateSsid3.ssidEnabled}" 
													id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckP" value="%{#templateSsid3.CheckP}" 
													id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckD" value="%{#templateSsid3.checkD}" 
													id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td class="list" width="30px">&nbsp;</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckPT" value="%{#templateSsid3.CheckPT}" 
													id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckDT" value="%{#templateSsid3.checkDT}" 
													id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
													disabled="%{#templateSsid3.disabledField}"></s:checkbox>
											</td>
											<s:if test="!oEMSystem">
											<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
											</s:if>
										</tr>
									</s:if>
								</s:iterator>
								<s:iterator value="%{dataSource.ssidInterfaces.values}"
									status="status" id="templateSsid4">
									<s:if test="%{interfaceName=='red0'}">
										<tr>
											<td valign="top" class="list"><s:property
												value="%{interfaceName}" /></td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsidOnly" value="%{#templateSsid4.ssidOnlyEnabled}" 
													id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
													onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'red0');"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayNetwork" value="%{#templateSsid4.networkServicesEnabled}" 
													id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayMacOui" value="%{#templateSsid4.macOuisEnabled}" 
													id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsid" value="%{#templateSsid4.ssidEnabled}" 
													id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckP" value="%{#templateSsid4.CheckP}" 
													id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckD" value="%{#templateSsid4.checkD}" 
													id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td class="list" width="30px">&nbsp;</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckPT" value="%{#templateSsid4.CheckPT}" 
													id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckDT" value="%{#templateSsid4.checkDT}" 
													id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
													disabled="%{#templateSsid4.disabledField}"></s:checkbox>
											</td>
											<s:if test="!oEMSystem">
											<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
											</s:if>
										</tr>
									</s:if>
								</s:iterator>
								<s:iterator value="%{dataSource.ssidInterfaces.values}"
									status="status" id="templateSsid5">
									<s:if test="%{interfaceName=='agg0'}">
										<tr>
											<td valign="top" class="list"><s:property
												value="%{interfaceName}" /></td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsidOnly" value="%{#templateSsid5.ssidOnlyEnabled}" 
													id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
													onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'agg0');"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayNetwork" value="%{#templateSsid5.networkServicesEnabled}" 
													id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayMacOui" value="%{#templateSsid5.macOuisEnabled}" 
													id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsid" value="%{#templateSsid5.ssidEnabled}" 
													id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckP" value="%{#templateSsid5.CheckP}" 
													id="arrayCheckP_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckP',%{#status.index}, true);"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckD" value="%{#templateSsid5.checkD}" 
													id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, true);"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td class="list" width="30px">&nbsp;</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckPT" value="%{#templateSsid5.CheckPT}" 
													id="arrayCheckPT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckPT',%{#status.index}, true);"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckDT" value="%{#templateSsid5.checkDT}" 
													id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, true);"
													disabled="%{#templateSsid5.disabledField}"></s:checkbox>
											</td>
											<s:if test="!oEMSystem">
											<td style="padding-left: 10px"><FONT color="blue"><s:text name="config.configTemplate.11nAPonly" /></FONT></td>
											</s:if>
										</tr>
									</s:if>
								</s:iterator>
								<s:iterator value="%{dataSource.ssidInterfaces.values}"
									status="status" id="templateSsid2">
									<s:if test="%{interfaceName!='eth0' && interfaceName!='eth1' && interfaceName!='red0' && interfaceName!='agg0'}">
										<tr>
											<td valign="top" class="list"><s:property
												value="%{interfaceName}" /></td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsidOnly" value="%{#templateSsid2.ssidOnlyEnabled}" 
													id="arraySsidOnly_%{#status.index}" fieldValue="%{interfaceName}" 
													onclick="changeSsidOnlyCheckBox(%{#status.index},%{dataSource.ssidInterfaces.size},'ssid');"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayNetwork" value="%{#templateSsid2.networkServicesEnabled}" 
													id="arrayNetwork_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayMacOui" value="%{#templateSsid2.macOuisEnabled}" 
													id="arrayMacOui_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arraySsid" value="%{#templateSsid2.ssidEnabled}" 
													id="arraySsid_%{#status.index}" fieldValue="%{interfaceName}"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckE" value="%{#templateSsid2.CheckE}" 
													id="arrayCheckE_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckE',%{#status.index}, false);"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckD" value="%{#templateSsid2.checkD}" 
													id="arrayCheckD_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckD',%{#status.index}, false);"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td class="list" width="30px">&nbsp;</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckET" value="%{#templateSsid2.CheckET}" 
													id="arrayCheckET_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckET',%{#status.index}, false);"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
											<td valign="top" class="list">
												<s:checkbox name="arrayCheckDT" value="%{#templateSsid2.checkDT}" 
													id="arrayCheckDT_%{#status.index}" fieldValue="%{interfaceName}"
													onclick="changeCheckboxValue('arrayCheckDT',%{#status.index}, false);"
													disabled="%{#templateSsid2.disabledField}"></s:checkbox>
											</td>
										</tr>
									</s:if>
								</s:iterator>
							</table>
							</div>
						</td>
					</tr>
					<tr>
						<td height="8px"></td>
					</tr>
					<tr>
						<td style="padding-left: 6px;">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td colspan="4" style="padding-top: 5px;" align="left" class="noteInfo"><s:text
										name="config.configTemplate.airtime.note" /></td>
								</tr>
								<tr>
									<td width="25px"><s:checkbox name="dataSource.enableAirTime"/></td>
									<td width="310px"><s:text name="config.configTemplate.enableAirTime" /></td>
									<td class="labelT1" width="140px">
										<s:text	name="config.userprofile.sla.interval" />
									</td>
									<td>
										<s:textfield name="dataSource.slaInterval" size="24" maxlength="4"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />
							     		<s:text name="config.userprofile.sla.interval.range"/>
							     	</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding: 4px 4px 4px 6px;">
						<s:if test="%{listQosRateLimit.size > 0}">
							<fieldset><legend><s:text name="config.configTemplate.block.policing" /></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td style="padding-top: 5px;" colspan="4" align="left" class="noteInfo"><s:text
											name="config.configTemplate.policyRateLimit.note" /></td>
									</tr>
									<tr>
										<s:if test="%{radioModeASize}">
											<td valign="top">
												<table cellspacing="0" cellpadding="0" border="0" class="embedded">
													<tr>
														<td nowrap="nowrap" style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
															name="config.configTemplate.model.typeA" /></b></td>
													</tr>
													<tr>
														<th align="center"><s:text
															name="config.configTemplate.model.name" /></th>
														<th align="center"><s:text
															name="config.configTemplate.model.rate" /><br>802.11a</th>
														<th align="center"><s:text
															name="config.configTemplate.model.rate" /><br>802.11na</th>
														<th align="center"><s:text
															name="config.configTemplate.model.weight" /></th>
														<th align="center"><s:text
															name="config.configTemplate.model.weightPercent" /></th>
													</tr>
													<s:iterator value="%{listQosRateLimit}" status="status" id="templateAModel">
														<s:if test="%{#templateAModel.radioMode == 2}">
															<tr>
																<td valign="top" class="list"><s:property
																	value="%{userProfile.userProfileName}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{policingRate}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{policingRate11n}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{schedulingWeight}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{weightPercent}" /></td>
															</tr>
														</s:if>
													</s:iterator>
												</table>
											</td>
											<td valign="top" style="padding-left: 30px;" />
										</s:if>
										<s:if test="%{radioModeBGSize}">
											<td valign="top" >
												<table cellspacing="0" cellpadding="0" border="0" class="embedded">
													<tr>
														<td nowrap="nowrap" style="padding: 5px 10px 10px 10px" colspan="5"><b><s:text
															name="config.configTemplate.model.typeBG" /></b></td>
													</tr>
													<tr>
														<th align="center"><s:text
															name="config.configTemplate.model.name" /></th>
														<th align="center"><s:text
															name="config.configTemplate.model.rate" /><br>802.11b/g</th>
														<th align="center"><s:text
															name="config.configTemplate.model.rate" /><br>802.11ng</th>
														<th align="center"><s:text
															name="config.configTemplate.model.weight" /></th>
														<th align="center"><s:text
															name="config.configTemplate.model.weightPercent" /></th>
													</tr>
													<s:iterator value="%{listQosRateLimit}"
														status="status" id="templateBGModel">
														<s:if test="%{#templateBGModel.radioMode == 1}">
															<tr>
																<td valign="top" class="list"><s:property
																	value="%{userProfile.userProfileName}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{policingRate}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{policingRate11n}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{schedulingWeight}" /></td>
																<td valign="top" class="list"><s:property
																	value="%{weightPercent}" /></td>
															</tr>
														</s:if>
													</s:iterator>
												</table>
											</td>
										</s:if>
									</tr>
								</table>
							</fieldset>
						</s:if>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form>
</div>
