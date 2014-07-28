<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>

<script>
var formName = 'apSimulator';
var BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var SW2024 = <%=HiveAp.HIVEAP_MODEL_SR24%>;
var SW2048 = <%=HiveAp.HIVEAP_MODEL_SR48%>;
var SW2024P = <%=HiveAp.HIVEAP_MODEL_SR2024P%>;
var SW2124P = <%=HiveAp.HIVEAP_MODEL_SR2124P%>;
var SW2148P = <%=HiveAp.HIVEAP_MODEL_SR2148P%>;

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

YAHOO.util.Event.addListener(window, "load", function() {

		createWaitingPanel();
		// Disable the 'Simulate' button if the remain simulate HiveAP number equals zero
		<s:if test="%{remainingAPNum <= 0}">
			YAHOO.util.Dom.setAttribute(Get('simulateButton'), "disabled", "disabled");
		</s:if>

});

function simulateOperation(operation)
{
	initNoteSection();
	
	if (validate(operation)) 
	{
		document.forms["simulatorForm"].operation.value = operation;
	
		var formObject = document.getElementById('simulatorForm');
		YAHOO.util.Connect.setForm(formObject);

		var transaction = YAHOO.util.Connect.asyncRequest('POST', "<s:url action='apSimulator' includeParams='none' />", { success:simulatorResult}, null);
	
		if(waitingPanel != null)
		{
			waitingPanel.show();
		}
	}
}
var remainingAPNum;
function simulatorResult(o)
{
	eval("var data = " + o.responseText);
	
	if (data.succ)
	{
		showHangMessage(data.message);
	} else {
		showErrorMessage(data.message);
	}
	
	remainingAPNum = data.apNumber;
	if (remainingAPNum > 0) {
		Get("apRange").innerHTML="(1-"+remainingAPNum+")";
	} else {
		Get("apRange").innerHTML="(0-0)";
		YAHOO.util.Dom.setAttribute(Get('simulateButton'), "disabled", "disabled");
		resetSimulateForm();
	}
	
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
}

function resetSimulateForm() {
	document.getElementById("apNumber").value = "0";
	document.getElementById("clientNumber").value = "0";	
}

function validate(operation) 
{
	var apNumber = document.getElementById("apNumber");
	var clientNumber = document.getElementById("clientNumber");
	var appleRate = document.getElementById("appleRate");
	var dellRate = document.getElementById("dellRate");
	var hpRate = document.getElementById("hpRate");
	var lenovoRate = document.getElementById("lenovoRate");
	
	if (null == remainingAPNum) {
		remainingAPNum = <s:property value="%{remainingAPNum}" />;
	}
    var minApNum = 0 == remainingAPNum ? 0:1;
    var message = hm.util.validateIntegerRange(apNumber.value, '<s:text name="tools.simulator.apNumber" />', minApNum, remainingAPNum);
    if (message != null) {
        hm.util.reportFieldError(Get("apNumber"), message);
        apNumber.focus();
        return false;
    }
	
	var clientNum = <s:property value="%{domain.getMaxSimuClient()}" />;
   	message = hm.util.validateIntegerRange(clientNumber.value, '<s:text name="tools.simulator.clientNumberPerAP" />', 0, clientNum);
    if (message != null) {
        hm.util.reportFieldError(clientNumber, message);
        clientNumber.focus();
        return false;
    }
	
	if (!isValidClientRate(appleRate.value,dellRate.value,hpRate.value,lenovoRate.value))
	{
		hm.util.reportFieldError(appleRate, 'The total percent of client vendor distribution should be 100.');
		appleRate.focus();
		return false;
	}

	return true;
}

function isSwitchProduct(hiveApModel){
	return hiveApModel == SW2024 
			|| hiveApModel == SW2048
			|| hiveApModel == SW2024P
			|| hiveApModel == SW2124P
			|| hiveApModel == SW2148P;
}

function isValidAPNumber(number)
{
	var intValue = number.valueOf();
	var remainingAPNum = <s:property value="%{remainingAPNum}" />;
	if (intValue >= 1 && intValue <= remainingAPNum )
	{
		return true;
	}
	
	return false;
}

function isValidClientNumber(number)
{
	var intValue = number.valueOf();
	var clientNum = <s:property value="%{domain.getMaxSimuClient()}" />;
	if (intValue >= 0 && intValue <= clientNum ) {
		return true;
	} else {
		hm.util.reportFieldError(clientNumber, '<s:text name="error.keyValueRange"><s:param><s:text name="tools.simulator.clientNumberPerAP" /></s:param><s:param>(0-clientNum)</s:param></s:text>');
		clientNumber.focus();
		return false;
	}
	
	return false;
}

function isValidClientRate(apple,dell,hp,lenovo)
{
	var totalValue = parseInt(apple) + parseInt(dell) + parseInt(hp) + parseInt(lenovo);
	if (totalValue == 100)
	{
		return true;
	}
	
	return false;
}

function showHangMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteInfo";
	hm.util.show("noteSection");
}

function showNormalMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteInfo";
	hm.util.show("noteSection");
	notesTimeoutId = setTimeout("hm.util.wipeOut('noteSection', 800)", 10 * 1000)
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

function showErrorMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	Get("noteTD").className="noteError";
	hm.util.show("noteSection");
}

function initNoteSection()
{
	hm.util.hide('noteSection');
}

// fix bug 16829
function changeApModel(model){
	var clientNumber = document.getElementById("clientNumber");
	if(model == BR200 || isSwitchProduct(model)){
		clientNumber.value=0;
		clientNumber.disabled = true;
	} else {
		clientNumber.disabled = false;
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="apSimulator" id="simulatorForm" name="simulatorForm">
		<s:if test="%{!isEnterFromTool}">
			<s:hidden name="operation" />
		</s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{isEnterFromTool}">
				<tr>
					<td>
						<tiles:insertDefinition name="context" />
					</td>
				</tr>
			</s:if>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" id="simulateButton" name="ignore" value="Simulate"
									class="button" onClick="simulateOperation('simulate');"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding-left: 5px;">
					<div id="noteSection" style="display:none">
						<table width="400px" border="0" cellspacing="0" cellpadding="0"
							class="note">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td id="noteTD">
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="550px">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="275px" style="padding-left: 15px;">
								<label>
									<s:text name="tools.simulator.apType" />
								</label>
							</td>
							<td>
								<s:select name="hiveApModel" value="%{hiveApModel}"
									list="%{apModelList}" listKey="key" listValue="value"
									cssStyle="width: 142px;" onchange="changeApModel(this.value);"/>
							</td>
						</tr>
						<tr id="fe_apNumber" style="display: none;">
							<td/><td id="textfe_apNumber" class="noteError">ToBeChanged</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="tools.simulator.apNumber" />
								</label>
							</td>
							<td >
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td><s:textfield id="apNumber" name="apNumber" size="16"
											maxlength="%{maxSimuApNumLength}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;</td>
										<td id="apRange">
											<s:if test="%{remainingAPNum <= 0}">
												<s:text name="(0-0)" />
											</s:if>
											<s:else>
												(1-<s:property value="%{remainingAPNum}" />)
											</s:else>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="tools.simulator.clientNumberPerAP" />
								</label>
							</td>
							<td>
								<s:textfield id="clientNumber" name="clientNumber" size="16"
									maxlength="%{maxSimuClientNumLength}"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />
								(0-<s:property value="%{domain.getMaxSimuClient()}" />)
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td style="padding-left: 15px;" colspan="2">
								<fieldset style="width: 392px">
									<legend>
										<s:text name="tools.simulator.clientVendor" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td class="labelT1" width="80px" style="padding-left: 15px;">
													<label>
														<s:text name="tools.simulator.apple" />
													</label>
												</td>
												<td>
													<s:textfield id="appleRate" name="appleRate" size="10"
														maxlength="2"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													&nbsp;%
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="80px" style="padding-left: 15px;">
													<label>
														<s:text name="tools.simulator.dell" />
													</label>
												</td>
												<td>
													<s:textfield id="dellRate" name="dellRate" size="10"
														maxlength="2"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													&nbsp;%
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="80px" style="padding-left: 15px;">
													<label>
														<s:text name="tools.simulator.hp" />
													</label>
												</td>
												<td>
													<s:textfield id="hpRate" name="hpRate" size="10"
														maxlength="2"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													&nbsp;%
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="80px" style="padding-left: 15px;">
													<label>
														<s:text name="tools.simulator.lenovo" />
													</label>
												</td>
												<td>
													<s:textfield id="lenovoRate" name="lenovoRate" size="10"
														maxlength="2"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
													&nbsp;%
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
