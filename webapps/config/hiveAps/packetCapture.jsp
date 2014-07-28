<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.monitor.HiveApToolkitAction"%>

<script type="text/javascript">

var formName = 'packetCaptureForm';
var url = "<s:url action='hiveApToolkit' includeParams='none' />";
var hideWifi1 = <s:property value="%{isHideWifi1}" />;

YAHOO.util.Event.addListener(window, "load", function() {

	//save this reference on the parent
	top.packetCaptureIframeWindow = window;
	
	<s:if test="%{isEnterFromTool}">
		createWaitingPanel();
	</s:if>
	
	if (hideWifi1)
	{
		hm.util.hide('interfaceSection');
	}
});

var beforeUnloadEvent = function(){
	clearTimeout(pollWifi0TimeoutId);
	clearTimeout(pollWifi1TimeoutId);
	clearTimeout(notesTimeoutId);
};

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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var pollWifi0TimeoutId;
var pollWifi1TimeoutId;
var notesTimeoutId;
var pollWifi0Count = 0;
var pollWifi1Count = 0;
var interval = 3;        // seconds
var duration = <s:property value="%{sessionTimeOut}" /> * 60;  // minutes * 60
var wifi0TotalCount = duration / interval;
var wifi1TotalCount = wifi0TotalCount;

var startPollWifi0Timer = function(){
	if (pollWifi0Count++ < wifi0TotalCount) {
		pollWifi0TimeoutId = setTimeout("pollWifi0CaptureStatus()", interval * 1000);  // seconds
	}
}

var pollWifi0CaptureStatus = function(){
	//showHangMessage('Querying capture status ...');
	document.forms["packetCaptureForm"].operation.value = 'pollWifi0CaptureStatus';
	ajaxRequest(formName, url,updateWifi0CaptureStatus, 'POST');
}

var startPollWifi1Timer = function(){
	if (pollWifi1Count++ < wifi1TotalCount) {
		pollWifi1TimeoutId = setTimeout("pollWifi1CaptureStatus()", interval * 1000);  // seconds
	}
}

var pollWifi1CaptureStatus = function(){
	//showHangMessage('Querying capture status ...');
	document.forms["packetCaptureForm"].operation.value = 'pollWifi1CaptureStatus';
	ajaxRequest(formName,url, updateWifi1CaptureStatus, 'POST');
}

function submitAction(operation)
{
	document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
}

var updateWifi0CaptureStatus = function(o){
	eval("var statusData = " + o.responseText);

	if (!statusData.result)
	{
		//result event is null
		if (statusData.checkAPResult)
		{
			showErrorMessage(statusData.checkAPResult+'<br>Packet capture has been stopped.');
			return;
		}

		showErrorMessage('Query capture status from AP failed, please check run state of AP and retry after capture stopped.');
		return;
	}

	if (!statusData.isFinished)
	{
		var total = statusData.total;
		var rx = statusData.rx;
		var tx = statusData.tx;

		refreshWifi0CaptureNumber(tx,rx,total);

		startPollWifi0Timer();
	}
	else
	{
		//download wifi0 packet
		showHangMessage("Interface wifi0 capture finished.");
		Get("startCaptureBtn").disabled = false;

		checkDownloadCaptureFile(<%=HiveApToolkitAction.INTERFACE_WIFI0%>);
	}
}

function checkDownloadCaptureFile(captureInterface)
{
	showHangMessage('Packet capturing is complete. Gathering results in a .cap file...');
	document.forms[formName].operation.value = 'checkDownloadCapture';
	document.forms[formName].captureInterface.value = captureInterface;
	ajaxRequest(formName,url, checkDownloadCaptureResult, 'POST');
}

function checkDownloadCaptureResult(o)
{
	eval("var result = " + o.responseText);
	if(result.message)
	{
		showErrorMessage(result.message);
	}
	else
	{
		showHangMessage("The .cap file is ready for download.");
		//submitAction("downloadCapture");
		hm.util.show('downloadSection');
	}
}

var updateWifi1CaptureStatus = function(o){
	eval("var statusData = " + o.responseText);

	if (!statusData.result)
	{
		//result event is null
		if (statusData.checkAPResult)
		{
			showErrorMessage(statusData.checkAPResult+'<br>Packet capture has been stopped.');
			return;
		}

		showErrorMessage('Query capture status from AP failed, please check run state of AP and retry after capture stopped.');
		return;
	}

	if (!statusData.isFinished)
	{
		var total = statusData.total;
		var rx = statusData.rx;
		var tx = statusData.tx;

		refreshWifi1CaptureNumber(tx,rx,total);

		startPollWifi1Timer();
	}
	else
	{
		//download wifi1 packet
		showHangMessage("Interface wifi1 capture finished.");

		Get("startCaptureBtn").disabled = false;

		checkDownloadCaptureFile(<%=HiveApToolkitAction.INTERFACE_WIFI1%>);
	}
}

function refreshWifi0CaptureNumber(tx,rx,total)
{
	var wifi0Tx = document.getElementById('wifi0Tx');
	wifi0Tx.innerHTML = "<td id='wifi0Tx'><input id='wifi0TxNumber' type='text' readonly='readonly' value='"+tx+"' name='wifi0TxNumber'/></td>";

	var wifi0Rx = document.getElementById('wifi0Rx');
	wifi0Rx.innerHTML = "<td id='wifi0Rx'><input id='wifi0RxNumber' type='text' readonly='readonly' value='"+rx+"' name='wifi0RxNumber'/></td>";

	var wifi0Total = document.getElementById('wifi0Total');
	wifi0Total.innerHTML = "<td id='wifi0Total'><input id='wifi0TotalNumber' type='text' readonly='readonly' value='"+total+"' name='wifi0TotalNumber'/></td>";
}

function refreshWifi1CaptureNumber(tx,rx,total)
{
	var wifi1Tx = document.getElementById('wifi1Tx');
	wifi1Tx.innerHTML = "<td id='wifi1Tx'><input id='wifi1TxNumber' type='text' readonly='readonly' value='"+tx+"' name='wifi1TxNumber'/></td>";

	var wifi1Rx = document.getElementById('wifi1Rx');
	wifi1Rx.innerHTML = "<td id='wifi1Rx'><input id='wifi1RxNumber' type='text' readonly='readonly' value='"+rx+"' name='wifi1RxNumber'/></td>";

	var wifi1Total = document.getElementById('wifi1Total');
	wifi1Total.innerHTML = "<td id='wifi1Total'><input id='wifi1TotalNumber' type='text' readonly='readonly' value='"+total+"' name='wifi1TotalNumber'/></td>";
}

var captureInterface;

function startCapture()
{
	initNoteSection();

	if (validate("startCapture"))
	{
		if (hideWifi1)
		{
			captureInterface = <%=HiveApToolkitAction.INTERFACE_WIFI0%>;
		} else {
			captureInterface = Get('startCaptureInterface').value;
		}
	
		document.forms["packetCaptureForm"].operation.value = "startCapture";
		<s:if test="%{isEnterFromTool}">
		document.forms[formName].hiveApId.value = document.getElementById("selectedHiveAp").value;
		</s:if>

		var formObject = document.getElementById('packetCaptureForm');
		YAHOO.util.Connect.setForm(formObject);

		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:startCaptureResult}, null);

		if(top.waitingPanel != null)
		{
			top.waitingPanel.show();
		}
	}
}

function startCaptureResult(o)
{
	eval("var data = " + o.responseText);

	hm.util.hide("wifi0Section");
	hm.util.hide("wifi1Section");

	if (data.wifi0)
	{
		if (data.result_wifi0)
		{
			hm.util.show("wifi0Section");
			refreshWifi0CaptureNumber(0,0,0);
			startPollWifi0Timer();

			Get("startCaptureBtn").disabled = true;
			showHangMessage("Capturing packets ...");
		}
		else
		{
			//
			showErrorMessage('Start capture failed. Please check version,run state and capture status of AP.');
		}
	}

	if (data.wifi1)
	{
		if (data.result_wifi1)
		{
			hm.util.show("wifi1Section");
			refreshWifi1CaptureNumber(0,0,0);
			startPollWifi1Timer();

			Get("startCaptureBtn").disabled = true;
			showHangMessage("Capturing packets ...");
		}
		else
		{
			//
			showErrorMessage('Start capture failed. Please check version,run state and capture status of AP.');
		}
	}

	if(top.waitingPanel != null)
	{
		top.waitingPanel.hide();
	}
}

function stopCapture()
{
	initNoteSection();

	if (validate("stopCapture"))
	{
		document.forms["packetCaptureForm"].operation.value = "stopCapture";
		<s:if test="%{isEnterFromTool}">
		document.forms[formName].hiveApId.value = document.getElementById("selectedHiveAp").value;
		</s:if>

		var formObject = document.getElementById('packetCaptureForm');
		YAHOO.util.Connect.setForm(formObject);

		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:stopCaptureResult}, null);

		if(top.waitingPanel != null)
		{
			top.waitingPanel.show();
		}
	}
}

function stopCaptureResult(o)
{
	eval("var data = " + o.responseText);
	var isSucc = data.result;
	if (isSucc)
	{
		clearTimeout(pollWifi0TimeoutId);
		clearTimeout(pollWifi1TimeoutId);
		
		if (Get("startCaptureBtn").disabled)
		{
			tftpCapturedFile();
		} 
		else
		{
			hm.util.hide('downloadSection');
			showHangMessage("Packet capture has been stopped successfully.");
		}
	}
	else
	{
		showErrorMessage('Stop capture failed. Please check run state of AP.');
	}

	if(top.waitingPanel != null)
	{
		top.waitingPanel.hide();
	}
}

function tftpCapturedFile(captureInterface)
{
	showHangMessage('Download packet capture dump file from AP to HiveManager ...');
	document.forms[formName].operation.value = 'tftpCapturedFile';
	//document.forms[formName].captureInterface.value = captureInterface;
	ajaxRequest(formName,url, tftpCapturedFileResult, 'POST');
}

function tftpCapturedFileResult(o)
{
	eval("var data = " + o.responseText);
	if(data.result)
	{
		showHangMessage("Download packet capture dump file ...");
		//submitAction("downloadCapture");
		hm.util.show('downloadSection');
	}
	else
	{
		showErrorMessage("Download capture result from AP failed, please check run state of AP.");
	}
	
	Get("startCaptureBtn").disabled = false;
}


function showHangMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	hm.util.show("noteSection");
}

function showNormalMessage(message)
{
	Get("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
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
	// comment it at first.
	//notesTimeoutId = setTimeout("hm.util.wipeOut('noteSection', 800)", 20 * 1000)
}

function validate(operation)
{
	<s:if test="%{isEnterFromTool}">
		if (document.getElementById("selectedHiveAp").value == -1)
		{
			showErrorMessage('There are no avaliable <s:text name="hm.config.guide.hiveAp.title" /> to operate.');
			return false;
		}
	</s:if>

	if (operation == 'startCapture')
	{
		var captureCount = document.getElementById("captureCount");
		if (captureCount.value.length == 0)
		{
			hm.util.reportFieldError(captureCount, '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.packetCapture.count" /></s:param></s:text>');
			captureCount.focus();
			return false;
		}

		var message = hm.util.validateIntegerRange(captureCount.value, '<s:text name="topology.menu.packetCapture.count" />',1,100000);
	    if (message != null)
	    {
			hm.util.reportFieldError(captureCount, message);
			captureCount.focus();
			return false;
	    }
	}

	return true;
}

function selectFilterSetting(checked)
{
	if (checked)
	{
		hm.util.show('captureFilterSection');
	}
	else
	{
		hm.util.hide('captureFilterSection');
	}
}

function initNoteSection()
{
	hm.util.hide('downloadSection');
	hm.util.hide('noteSection');
}

function selectTrafficType(value)
{
	var captureSubtype = document.getElementById('captureSubtype');

	captureSubtype.length=0;

	//
	if(value == <%=HiveApToolkitAction.TRAFFIC_TYPE_ALL%>)
	{
		//all, disable sub type select
		captureSubtype.disabled = true;
	}
	else if (value == <%=HiveApToolkitAction.TRAFFIC_TYPE_DATA%>)
	{
		//data
		captureSubtype.disabled = false;
		captureSubtype.length=5;
		
		captureSubtype.options[0].text = 'All';
		captureSubtype.options[0].value = -1;
		
		captureSubtype.options[1].text = 'Data';
		captureSubtype.options[1].value = 0;

		captureSubtype.options[2].text = 'Null';
		captureSubtype.options[2].value = 4;

		captureSubtype.options[3].text = 'Qos Data';
		captureSubtype.options[3].value = 8;

		captureSubtype.options[4].text = 'Qos Null';
		captureSubtype.options[4].value = 12;
	}
	else if (value == <%=HiveApToolkitAction.TRAFFIC_TYPE_CTL%>)
	{
		//ctrl
		captureSubtype.disabled = false;
		captureSubtype.length=7;

		captureSubtype.options[0].text = 'All';
		captureSubtype.options[0].value = -1;

		captureSubtype.options[1].text = 'ACK';
		captureSubtype.options[1].value = 13;

		captureSubtype.options[2].text = 'Block ACK';
		captureSubtype.options[2].value = 9;

		captureSubtype.options[3].text = 'Block ACK Request';
		captureSubtype.options[3].value = 8;

		captureSubtype.options[4].text = 'CTS';
		captureSubtype.options[4].value = 12;

		captureSubtype.options[5].text = 'PS-Poll';
		captureSubtype.options[5].value = 10;

		captureSubtype.options[6].text = 'RTS';
		captureSubtype.options[6].value = 11;
	}
	else if (value == <%=HiveApToolkitAction.TRAFFIC_TYPE_MGMT%>)
	{
		//mgmt
		captureSubtype.disabled = false;
		captureSubtype.length=11;

		captureSubtype.options[0].text = 'All';
		captureSubtype.options[0].value = -1;

		captureSubtype.options[1].text = 'Action';
		captureSubtype.options[1].value = 13;

		captureSubtype.options[2].text = 'Association Request';
		captureSubtype.options[2].value = 0;

		captureSubtype.options[3].text = 'Association Response';
		captureSubtype.options[3].value = 1;

		captureSubtype.options[4].text = 'Authentication';
		captureSubtype.options[4].value = 11;

		captureSubtype.options[5].text = 'Beacon';
		captureSubtype.options[5].value = 8;
		
		captureSubtype.options[6].text = 'Deauthentication';
		captureSubtype.options[6].value = 12;
		
		captureSubtype.options[7].text = 'Disassociation';
		captureSubtype.options[7].value = 10;
		
		captureSubtype.options[8].text = 'Probe Request';
		captureSubtype.options[8].value = 4;
		
		captureSubtype.options[9].text = 'Reassociation Request';
		captureSubtype.options[9].value = 2;

		captureSubtype.options[10].text = 'Reassociation Response';
		captureSubtype.options[10].value = 3;
	}

	if (captureSubtype.options.length > 0)
	{
		captureSubtype.options[0].selected=true;
		captureSubtype.value=captureSubtype.options[0].value;
		captureSubtype.text=captureSubtype.options[0].text;
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

</script>
<div>
	<s:form action="hiveApToolkit" id="packetCaptureForm"
		name="packetCaptureForm">
		<s:if test="%{!isEnterFromTool}">
			<s:hidden name="operation" />
		</s:if>
		<s:hidden name="hiveApId" />
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
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
								<input type="button" id="startCaptureBtn" name="ignore"
									value="Start" class="button" onClick="startCapture();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" id="stopCaptureBtn" name="ignore"
									value="Stop" class="button" onClick="stopCapture();"
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
								<td class="buttons">
									<div id="downloadSection" style="display:none">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<input type="button" id="downloadBtn" name="ignore"
														value="Download" class="button"
														onClick="submitAction('downloadCapture');">
												</td>
												<td>
													<input type="button" id="cancelBtn" name="ignore"
														value="Cancel" class="button"
														onClick="initNoteSection();">
												</td>
											</tr>
										</table>
									</div>
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
					<s:if test="%{isEnterFromTool}">
						<table class="editBox" border="0" cellspacing="0" cellpadding="0"
							width="650px" style="padding: 5px;">
					</s:if>
					<s:else>
						<table class="editBox" cellspacing="0" cellpadding="0"
							border="0" width="100%">
					</s:else>
							<tr>
								<td height="5px"></td>
							</tr>
							<s:if test="%{isEnterFromTool}">
							<tr>
								<td class="labelT1" width="200px">
									<s:text name="hiveAp.tag" /><span style="color: #FF0000;"> *</span>
								</td>
								<td>
									<s:select id="selectedHiveAp" list="hiveAps" listKey="id" listValue="value"
										cssStyle="width:150px;"/>
								</td>
							</tr>
							</s:if>
							<tr id="interfaceSection">
								<td class="labelT1">
									<s:text name="topology.menu.packetCapture.interface" />
								</td>
								<td>
									<s:select name="captureInterface" id="startCaptureInterface"
										list="wifiInterfaceList" listKey="key" listValue="value"
										cssStyle="width:150px;" />
								</td>
							</tr>
							<tr>
								<td class="labelT1">
									<s:text name="topology.menu.packetCapture.count" />
								</td>
								<td>
									<s:textfield name="captureCount" id="captureCount"
										maxlength="6" cssStyle="width:146px;" />
									<s:text name="topology.menu.packetCapture.countRange" />
								</td>
							</tr>
							<tr>
								<td class="labelT1" colspan="2">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td>
												<s:radio label="Gender" id="" name="promiscuousWhenCapture"
													list="%{promiscuousWhenCaptureList}"
													listKey="key" listValue="value"
													value="%{promiscuousWhenCapture}" />

											</td>
										</tr>
										<tr>
											<td>
												<s:radio label="Gender" id="" name="promiscuousWhenCapture"
													list="#{true:'Capture all wireless traffic on the same channel as the selected radio interface (promiscuous mode)'}"
													value="%{promiscuousWhenCapture}" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" class="labelT1" width="200px">
									<s:checkbox name="trafficFilterFlag" id="trafficFilterFlag"
										onclick="selectFilterSetting(this.checked);" />
									<label>
										<s:text name="topology.menu.packetCapture.trafficFilter" />
									</label>
								</td>
							</tr>
							<tr style="display:none" id="captureFilterSection">
								<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td class="labelT1" width="100px">
												<s:text name="topology.menu.packetCapture.trafficType" />
											</td>
											<td>
												<s:select name="captureTrafficType" id="captureTrafficType"
													list="trafficTypeList" listKey="key" listValue="value"
													cssStyle="width:154px;"
													onchange="selectTrafficType(this.value)" />
											</td>
											<td class="labelT1" width="100px">
												<s:text name="topology.menu.packetCapture.subtype" />
											</td>
											<td>
												<s:select name="captureSubtype" id="captureSubtype"
													list="subTypeList" listKey="key" listValue="value"
													cssStyle="width:154px;" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.bssid" />
											</td>
											<td colspan="3">
												<s:textfield name="captureBSSID" id="captureBSSID"
													onkeypress="return hm.util.keyPressPermit(event,'hex');"
													cssStyle="width:150px;" maxlength="12" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.srcMac" />
											</td>
											<td>
												<s:textfield name="captureSrcMac" id="captureSrcMac"
													onkeypress="return hm.util.keyPressPermit(event,'hex');"
													cssStyle="width:150px;" maxlength="12" />
											</td>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.destMac" />
											</td>
											<td>
												<s:textfield name="captureDestMac" id="captureDestMac"
													onkeypress="return hm.util.keyPressPermit(event,'hex');"
													cssStyle="width:150px;" maxlength="12" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.txMac" />
											</td>
											<td>
												<s:textfield name="captureTxMac" id="captureTxMac"
													onkeypress="return hm.util.keyPressPermit(event,'hex');"
													cssStyle="width:150px;" maxlength="12" />
											</td>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.rxMac" />
											</td>
											<td>
												<s:textfield name="captureRxMac" id="captureRxMac"
													onkeypress="return hm.util.keyPressPermit(event,'hex');"
													cssStyle="width:150px;" maxlength="12" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.error" />
											</td>
											<td>
												<s:select name="captureErrorValue" id="captureErrorValue"
													list="errorConditionList" listKey="key" listValue="value"
													cssStyle="width:154px;" />
											</td>
											<td class="labelT1">
												<s:text name="topology.menu.packetCapture.ethType" />
											</td>
											<td>
												<s:select name="captureEthType" id="captureEthType"
													list="ethValueList" listKey="key" listValue="value"
													cssStyle="width:154px;" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr id="wifi0Section" style="display:none">
								<td class="labelT1" colspan="2">
									<fieldset style="width: 500px">
										<legend>
											Interface Wifi0 Capture Status
										</legend>
										<div>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.txNumber" />
														</label>
													</td>
													<td id="wifi0Tx">
														<s:textfield id="wifi0TxNumber" name="wifi0TxNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.rxNumber" />
														</label>
													</td>
													<td id="wifi0Rx">
														<s:textfield id="wifi0RxNumber" name="wifi0RxNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.totalNumber" />
														</label>
													</td>
													<td id="wifi0Total">
														<s:textfield id="wifi0TotalNumber" name="wifi0TotalNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
											</table>
										</div>
									</fieldset>
								</td>
							</tr>
							<tr id="wifi1Section" style="display:none">
								<td class="labelT1" colspan="2">
									<fieldset style="width: 500px">
										<legend>
											Interface Wifi1 Capture Status
										</legend>
										<div>
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.txNumber" />
														</label>
													</td>
													<td id="wifi1Tx">
														<s:textfield id="wifi1TxNumber" name="wifi1TxNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.rxNumber" />
														</label>
													</td>
													<td id="wifi1Rx">
														<s:textfield id="wifi1RxNumber" name="wifi1RxNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="200px">
														<label>
															<s:text name="topology.menu.packetCapture.totalNumber" />
														</label>
													</td>
													<td id="wifi1Total">
														<s:textfield id="wifi1TotalNumber" name="wifi1TotalNumber"
															readonly="true" />
													</td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
											</table>
										</div>
									</fieldset>
								</td>
							</tr>
						</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
