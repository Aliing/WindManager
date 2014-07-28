<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div>
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
<script type="text/javascript">
	function validateSwichPSE(operation){
		if(operation == 'create2' || operation == 'update2'){
			var enableSwitchPse = document.getElementById(formName+"_dataSource_enableSwitchPse").checked;
			if(!enableSwitchPse){
				return true;
			}
			var maxpowerBudget = document.getElementById(formName+"_dataSource_maxpowerBudget");
			if(maxpowerBudget.value.length == 0) {
				showSwitchPseContent();
		    	hm.util.reportFieldError(maxpowerBudget,
		            '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.switch.pse.max.power.budget" /></s:param></s:text>');
		    	maxpowerBudget.focus();
		        return false;
		    }

			var minValue=0;
			var maxValue=195;
			if(MODEL_SR24 == '<s:property value="dataSource.hiveApModel" />'){
				maxValue = 195;
			} else if(MODEL_SR2124P == '<s:property value="dataSource.hiveApModel" />'){
				maxValue = 408
			} else if(MODEL_SR2148P == '<s:property value="dataSource.hiveApModel" />'){
				maxValue = 779
			}

	        var message = hm.util.validateIntegerRange(maxpowerBudget.value,
	                '<s:text name="hiveAp.switch.pse.max.power.budget" />', minValue, maxValue);
	        if(message) {
	        	showSwitchPseContent();
	            hm.util.reportFieldError(maxpowerBudget, message);
	            maxpowerBudget.focus();
	            return false;
	        }

	        var powerGuardBand = document.getElementById(formName+"_dataSource_powerGuardBand");
			if(powerGuardBand.value.length == 0) {
				showSwitchPseContent();
		    	hm.util.reportFieldError(powerGuardBand,
		            '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.switch.pse.power.guard.band" /></s:param></s:text>');
		    	powerGuardBand.focus();
		        return false;
		    }

	        var message = hm.util.validateIntegerRange(powerGuardBand.value,
	                '<s:text name="hiveAp.switch.pse.power.guard.band" />', 2, 25);
	        if(message) {
	        	showSwitchPseContent();
	            hm.util.reportFieldError(powerGuardBand, message);
	            powerGuardBand.focus();
	            return false;
	        }
		}
		return true;
	}

	function showSwitchPseContent(){
		showHideContent("devicePSEDiv","");
	}

	function clickEnableSwitchPse(checked){
		if(checked){
			$("#"+formName+"_dataSource_maxpowerBudget").attr("disabled",false);
			$("#"+formName+"_dataSource_managementType").attr("disabled",false);
			$("#"+formName+"_dataSource_powerGuardBand").attr("disabled",false);
			$("#"+formName+"_dataSource_enablePoeLegacy").attr("disabled",false);
			$("#"+formName+"_dataSource_enablePoeLldp").attr("disabled",false);
		} else {
			$("#"+formName+"_dataSource_maxpowerBudget").attr("disabled",true);
			$("#"+formName+"_dataSource_managementType").attr("disabled",true);
			$("#"+formName+"_dataSource_powerGuardBand").attr("disabled",true);
			$("#"+formName+"_dataSource_enablePoeLegacy").attr("disabled",true);
			$("#"+formName+"_dataSource_enablePoeLldp").attr("disabled",true);
		}
	}
	
	function chgManageType(value){
		if(<s:property value="poeLldpDisplay" />){
			if(value == 0){
				$("#poeLldpTr").show();
			} else {
				$("#poeLldpTr").hide();
			}
		} else {
			$("#poeLldpTr").hide();
		}
	}
</script>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.switch.pse.title" />','devicePSEDiv');</script></td>
		</tr>
		<tr>
			<td>
				<div id="devicePSEDiv" style="display: none;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" colspan="2" style="padding-left: 17px;">
								<s:checkbox name="dataSource.enableSwitchPse"  onclick="clickEnableSwitchPse(this.checked)"/>
								<s:text	name="hiveAp.switch.pse.enable" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="150" style="padding-left: 20px;">
								<s:text	name="hiveAp.switch.pse.max.power.budget"/></td>
							<td><s:textfield name="dataSource.maxpowerBudget" maxlength="3"
								disabled="%{!dataSource.enableSwitchPse}"
								onkeypress="return hm.util.keyPressPermit(event,'ten');" />
								<s:if test="%{is2124PSwitch}">
									<s:text name="hiveAp.switch.pse.max.power.budget.range.2124p" />
								</s:if>
								<s:elseif test="%{is2024Switch || is2024PSwitch}">
									<s:text name="hiveAp.switch.pse.max.power.budget.range" />
								</s:elseif>
								<s:elseif test="%{is2148PSwitch}">
									<s:text name="geneva_02.hiveAp.switch.pse.max.power.budget.range.2148p" />
								</s:elseif>
								<s:else>
									<s:text name="hiveAp.switch.pse.max.power.budget.range" />
								</s:else>
								
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="150" style="padding-left: 20px;">
								<s:text	name="hiveAp.switch.pse.management.type" /></td>
							<td><s:select name="dataSource.managementType" 
								disabled="%{!dataSource.enableSwitchPse}"
								onchange="chgManageType(this.value);"
								list="%{enumManagementType}" listKey="key" listValue="value" cssStyle="width: 100px;" /></td>
						</tr>
						<tr id ="poeLldpTr" style="display: <s:property value="%{poeLldpDisplay&&dataSource.managementType == 0?'':'none'}"/>">
							<td class="labelT1" colspan="2" style="padding-left: 17px;">
								<s:checkbox name="dataSource.enablePoeLldp" disabled="%{!dataSource.enableSwitchPse}"/>
								<s:text	name="glasgow_32.hiveAp.switch.pse.poe.lldp" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="150" style="padding-left: 20px;">
								<s:text	name="hiveAp.switch.pse.power.guard.band"/></td>
							<td><s:textfield name="dataSource.powerGuardBand" maxlength="2" size="3"
								disabled="%{!dataSource.enableSwitchPse}"
								onkeypress="return hm.util.keyPressPermit(event,'ten');" />
								<s:text name="hiveAp.switch.pse.power.guard.band.range"></s:text>
							</td>
						</tr>
						<tr style="display: <s:property value="%{poeLegacyDisplay?'':'none'}"/>">
							<td class="labelT1" colspan="2" style="padding-left: 17px;">
								<s:checkbox name="dataSource.enablePoeLegacy" disabled="%{!dataSource.enableSwitchPse}"/>
								<s:text	name="geneva_17.hiveAp.switch.pse.poe.legacy" /></td>
						</tr>
						<tr>
							<td class="noteInfo" colspan="2" style="padding-left: 20px;">
								<s:if test="poeLldpDisplay">
									<s:text name="glasgow_32.hiveAp.switch.pse.note"/>
								</s:if>
								<s:elseif test="poeLegacyDisplay">
									<s:text name="geneva_17.hiveAp.switch.pse.note.new"/>
								</s:elseif>
								<s:else>
									<s:text name="geneva_17.hiveAp.switch.pse.note.old"/>
								</s:else>
								
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</s:if>
</div>