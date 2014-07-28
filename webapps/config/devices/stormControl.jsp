<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.hiveap.ConfigTemplateStormControl"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>

<div>
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
<script type="text/javascript">
	var BPS_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE%>;
	var PPS_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE%>;
	var PERCENTAGE_DEFULT_VALUE = <%=ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE%>;
	<s:if test="%{dataSource.hiveApModel == <%=HiveAp.HIVEAP_MODEL_SR2148P%>}">
		var DEVICE_IF_TYPE_SFP1 = <%=AhInterface.DEVICE_IF_TYPE_ETH49%>;
		var DEVICE_IF_TYPE_SFP2 = <%=AhInterface.DEVICE_IF_TYPE_ETH50%>;
		var DEVICE_IF_TYPE_SFP3 = <%=AhInterface.DEVICE_IF_TYPE_ETH51%>;
		var DEVICE_IF_TYPE_SFP4 = <%=AhInterface.DEVICE_IF_TYPE_ETH52%>;
	</s:if>
	<s:else>
		var DEVICE_IF_TYPE_SFP1 = <%=AhInterface.DEVICE_IF_TYPE_ETH25%>;
		var DEVICE_IF_TYPE_SFP2 = <%=AhInterface.DEVICE_IF_TYPE_ETH26%>;
		var DEVICE_IF_TYPE_SFP3 = <%=AhInterface.DEVICE_IF_TYPE_ETH27%>;
		var DEVICE_IF_TYPE_SFP4 = <%=AhInterface.DEVICE_IF_TYPE_ETH28%>;
	</s:else>
	
	
	function changeAllTrafficTypeCheckBox(index){
		if (document.getElementById('arrayAllTrafficType_' + index).checked){
			Get('arrayBroadcast_' + index).checked=true;
			Get('arrayUnknownUnicast_' + index).checked=true;
			Get('arrayMulticast_' + index).checked=true;
			Get('arrayTcpsyn_' + index).checked=true;
			Get('arrayRateLimitValue_' + index).disabled = false;
			Get('scIterfaceType_' + index).disabled = false;
			Get('arrayRateLimitType_' + index).disabled = false;
		} else {
			Get('arrayBroadcast_' + index).checked=false;
			Get('arrayUnknownUnicast_' + index).checked=false;
			Get('arrayMulticast_' + index).checked=false;
			Get('arrayTcpsyn_' + index).checked=false;
			Get('arrayRateLimitValue_' + index).disabled = true;
			Get('scIterfaceType_' + index).disabled = true;
			Get('arrayRateLimitType_' + index).disabled = true;
		}
	}

	function changeTrafficTypeCheckBox(index){
			var allTrafficType = document.getElementById('arrayAllTrafficType_' + index);
			if(Get('arrayBroadcast_' + index).checked
					&& Get('arrayUnknownUnicast_' + index).checked
					&& Get('arrayMulticast_' + index).checked
					&& Get('arrayTcpsyn_' + index).checked){
				allTrafficType.checked= true;
			} else {
				allTrafficType.checked = false;
			}

			if(Get('arrayBroadcast_' + index).checked
					|| Get('arrayUnknownUnicast_' + index).checked
					|| Get('arrayMulticast_' + index).checked
					|| Get('arrayTcpsyn_' + index).checked){
				Get('arrayRateLimitValue_' + index).disabled = false;
				Get('scIterfaceType_' + index).disabled = false;
				Get('arrayRateLimitType_' + index).disabled = false;
			} else {
				Get('arrayRateLimitValue_' + index).disabled = true;
				Get('scIterfaceType_' + index).disabled = true;
				Get('arrayRateLimitType_' + index).disabled = true;
			}


	}

	function changeRateLimitType(index,value,interfaceNum){
		var text ='';
		var rateLimitValue = document.getElementById('arrayRateLimitValue_' + index);
		if(value==0){
			if(interfaceNum == DEVICE_IF_TYPE_SFP1 || interfaceNum == DEVICE_IF_TYPE_SFP2
					|| interfaceNum == DEVICE_IF_TYPE_SFP3 || interfaceNum == DEVICE_IF_TYPE_SFP4){
				text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps.sfp"/>';
				rateLimitValue.maxLength=8
				rateLimitValue.value=BPS_DEFULT_VALUE;
			} else {
				text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"/>';
				rateLimitValue.maxLength=7
				rateLimitValue.value=BPS_DEFULT_VALUE;
			}
		} else if(value==1) {
			text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"/>';
			rateLimitValue.maxLength=10
			rateLimitValue.value=PPS_DEFULT_VALUE;
		} else {
			text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"/>';
			rateLimitValue.maxLength=3
			rateLimitValue.value=PERCENTAGE_DEFULT_VALUE;
		}
		//$("#scRateLimitRange_"+ index).text(text);
		Get('scRateLimitRange_' + index).innerHTML = text;
	}

	function changestormMode(value,length){
		for(var i=0;i<length;i++){
			var rateLimitValue = document.getElementById('arrayRateLimitValue_' + i);
			var limitType = $("#arrayRateLimitType_"+i).val();
			if(value == 0){
				$("#stormLimitTypePacketId_"+i).hide();
				$("#arrayRateLimitType_"+i).show();
				if(limitType==2){
					text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"/>';
					rateLimitValue.maxLength=3;
					if(!Get('arrayBroadcast_' + i).checked
							&& !Get('arrayUnknownUnicast_' + i).checked
							&& !Get('arrayMulticast_' + i).checked
							&& !Get('arrayTcpsyn_' + i).checked){
						rateLimitValue.value=PERCENTAGE_DEFULT_VALUE;
					}

				} else {
					var interfaceNum = $("#scIterfaceType_"+i).val();
					if(interfaceNum == DEVICE_IF_TYPE_SFP1 || interfaceNum == DEVICE_IF_TYPE_SFP2
							|| interfaceNum == DEVICE_IF_TYPE_SFP3 || interfaceNum == DEVICE_IF_TYPE_SFP4){
						text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps.sfp"/>';
						rateLimitValue.maxLength=8;
					} else {
						text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"/>';
						rateLimitValue.maxLength=7;
					}
					
					if(!Get('arrayBroadcast_' + i).checked
							&& !Get('arrayUnknownUnicast_' + i).checked
							&& !Get('arrayMulticast_' + i).checked
							&& !Get('arrayTcpsyn_' + i).checked){
						rateLimitValue.value=BPS_DEFULT_VALUE;
					}
				}
			} else {
				$("#stormLimitTypePacketId_"+i).show();
				$("#arrayRateLimitType_"+i).hide();
				text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"/>';
				var interfaceNum = $("#scIterfaceType_"+i).val();
				if(interfaceNum == DEVICE_IF_TYPE_SFP1 || interfaceNum == DEVICE_IF_TYPE_SFP2
						|| interfaceNum == DEVICE_IF_TYPE_SFP3 || interfaceNum == DEVICE_IF_TYPE_SFP4){
					text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps.sfp"/>';
					rateLimitValue.maxLength=10;
				} else {
					text = '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"/>';
					rateLimitValue.maxLength=9;
				}
				
				if(!Get('arrayBroadcast_' + i).checked
						&& !Get('arrayUnknownUnicast_' + i).checked
						&& !Get('arrayMulticast_' + i).checked
						&& !Get('arrayTcpsyn_' + i).checked){
					rateLimitValue.value=PPS_DEFULT_VALUE;
				}
			}
			Get('scRateLimitRange_' + i).innerHTML = text;
		}
	}

	function enableOverrideStormControl(checked){
		if (checked) {
			Get("stormControlDetailTr").style.display="";
		} else {
			Get("stormControlDetailTr").style.display="none";
		}
	}

	function validateStormControl(operation){
		if(operation == 'create2' || operation == 'update2'){
			var table = document.getElementById("tbl_storm");
			var rowCount = table.rows.length-2; //remove note and header line
			for(var i=0;i<rowCount;i++){
				if(Get('arrayBroadcast_' + i) == null){ continue;}
				if(!Get('arrayBroadcast_' + i).checked
						&& !Get('arrayUnknownUnicast_' + i).checked
						&& !Get('arrayMulticast_' + i).checked
						&& !Get('arrayTcpsyn_' + i).checked){
					continue;
				}
				var rateLimitValue = document.getElementById("arrayRateLimitValue_"+i);
				var minValue = 0;
				var maxValue = 100;
				var ifType = document.getElementById("scIterfaceType_"+i);
				var rateLimitType = document.getElementById("arrayRateLimitType_"+i);
				var rateLimitValue = document.getElementById("arrayRateLimitValue_"+i);

				var selectedMode = $('input:radio[name="dataSource.switchStormControlMode"]:checked').val();
				if(selectedMode == 0){
					if(rateLimitType.value==0){
						var interfaceNum = $("#scIterfaceType_"+i).val();
						if(interfaceNum == DEVICE_IF_TYPE_SFP1 || interfaceNum == DEVICE_IF_TYPE_SFP2
								|| interfaceNum == DEVICE_IF_TYPE_SFP3 || interfaceNum == DEVICE_IF_TYPE_SFP4){
							maxValue=10000000;
						} else {
							maxValue=1000000;
						}
						
					} else if(rateLimitType.value==1){
						maxValue=1000000000;
					} else if(rateLimitType.value==2){
						maxValue= 100
					}
				} else {
					var interfaceNum = $("#scIterfaceType_"+i).val();
					if(interfaceNum == DEVICE_IF_TYPE_SFP1 || interfaceNum == DEVICE_IF_TYPE_SFP2
							|| interfaceNum == DEVICE_IF_TYPE_SFP3 || interfaceNum == DEVICE_IF_TYPE_SFP4){
						maxValue=1000000000;
					} else {
						maxValue=100000000;
					}
				}

				if(rateLimitValue.value.length == 0) {
			    	hm.util.reportFieldError(rateLimitValue,
			            '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" /></s:param></s:text>');
			        rateLimitValue.focus();
			        return false;
			    }
		        var message = hm.util.validateIntegerRange(rateLimitValue.value,
		                '<s:text name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" />', minValue, maxValue);
		        if(message) {
		            hm.util.reportFieldError(rateLimitValue, message);
		            rateLimitValue.focus();
		            return false;
		        }

			}
		}
		return true;
	}
</script>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.configTemplate.switchSettings.stormControl.title" />','stormControlDiv');</script></td>
		</tr>
		<tr>
			<td>
				<div id="stormControlDiv" style="display: <s:property value="%{dataSource.stormControlDivStyle}"/>">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td style="padding: 5px 0px 0px 15px;">
								<s:checkbox name="dataSource.enableOverrideStormControl" onclick="enableOverrideStormControl(this.checked);"/>
								<s:text name="config.configTemplate.switchSettings.stormControl.override.note"/></td>
						</tr>
						<tr id="stormControlDetailTr" style="display:<s:property value='stormControlDetailTrStyle'/>">
							<td style="padding-left: 20px;">
								<table cellspacing="0" cellpadding="0" border="0" class="view"
									id="tbl_storm">
									<tr>
										<td class="labelT1" align="center" colspan="4"><s:radio label="Gender" id="stormControlByteMode"
											name="dataSource.switchStormControlMode" list="%{stormRateLimitByte}"
											onchange="changestormMode(this.value,%{dataSource.stormControlList.size()});"
											listKey="key" listValue="value" /></td>
										<td class="labelT1" align="center" colspan="4"><s:radio label="Gender"  id="stormControlPackedMode"
											name="dataSource.switchStormControlMode" list="%{stormRateLimitPacket}"
											onchange="changestormMode(this.value,%{dataSource.stormControlList.size()});"
											listKey="key" listValue="value" /></td>
									</tr>
									<tr>
										<th align="center" width="100px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.interface" /></th>
										<th align="center" width="50px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.typeall" /></th>
										<th align="center" width="60px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.broadcast" /></th>
										<th align="center" width="100px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.unknownUnicast" /></th>
										<th align="center" width="50px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.muticast" /></th>
										<th align="center" width="50px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.tcpsyn" /></th>
										<th align="center" width="100px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.ratelimittype" /></th>
										<th align="left" width="180px;"><s:text
												name="config.configTemplate.switchSettings.stormControl.ratelimitvalue" /></th>
									</tr>
									<s:iterator value="%{dataSource.stormControlList}"
										status="status" id="templeteStormControl">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td align="center"><s:property value="interfaceName" />
												<s:hidden name="arrayInterfaceNum"
													id="scIterfaceType_%{#status.index}"
													disabled="%{#templeteStormControl.disableRateLimit}"
													value="%{#templeteStormControl.interfaceNum}" /></td>
											<td align="center"><s:checkbox
													name="arrayAllTrafficType"
													value="%{allTrafficType}"
													id="arrayAllTrafficType_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="changeAllTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
											<td align="center"><s:checkbox name="arrayBroadcast"
													value="%{#templeteStormControl.broadcast}"
													id="arrayBroadcast_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
											<td align="center"><s:checkbox
													name="arrayUnknownUnicast"
													value="%{#templeteStormControl.unknownUnicast}"
													id="arrayUnknownUnicast_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
											<td align="center"><s:checkbox name="arrayMulticast"
													value="%{#templeteStormControl.multicast}"
													id="arrayMulticast_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
											<td align="center"><s:checkbox name="arrayTcpsyn"
													value="%{#templeteStormControl.tcpsyn}"
													id="arrayTcpsyn_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="changeTrafficTypeCheckBox(%{#status.index});"></s:checkbox></td>
											<td align="center"><s:select
													name="arrayRateLimitType"
													value="%{#templeteStormControl.rateLimitType}"
													disabled="%{#templeteStormControl.disableRateLimit}"
													id="arrayRateLimitType_%{#status.index}"
													list="%{list_stormLimitType}"
													listKey="id"
													listValue="value"
													cssStyle="width: 88px;display: %{dataSource.showStormLimitTypeBased}"
													onchange="changeRateLimitType(%{#status.index},this.value,%{interfaceNum});"/>
													<label id="stormLimitTypePacketId_<s:property value="%{#status.index}"/>" style="display: <s:property value="%{dataSource.showStormLimitTypePacket}"/>">PPS</label></td>
											<td align="left"><s:textfield
													name="arrayRateLimitValue"
													value="%{#templeteStormControl.rateLimitValue}"
													disabled="%{#templeteStormControl.disableRateLimit}"
													id="arrayRateLimitValue_%{#status.index}"
													cssStyle="width:70px;"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													maxlength="%{#templeteStormControl.rateLimitValueLength}"/>
													<label id="scRateLimitRange_<s:property value='%{#status.index}'/>"><s:property value="%{#templeteStormControl.rateLimitRange}"/></label></td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</s:if>
</div>