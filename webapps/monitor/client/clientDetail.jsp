<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<link rel="stylesheet" href="css/hm.css">
<link rel="stylesheet" href="css/te.css" />
<link rel="stylesheet" href="css/data_table.css" />
<script src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/mvc/ae.js"></script>

<tiles:insertDefinition name="flashHeader" />
<script>
var formName = 'clientMonitor';
var thisOperation;

function onLoadPage() 
{
	// Overlay for waiting dialog
	createWaitingPanel();
} 

function onUnloadPage() {
}

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

function syncClientInfo(targetId){
	if(targetId == undefined){
		return;
	}
	
	url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=syncClientInfo&id=" + targetId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: synchronizeResult, failure:abortResult,timeout: 35000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var abortResult = function(o) 
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
}

var synchronizeResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.success){
		var redirect_url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=showDetail&id=" + result.clientId 
							+ "&listType=" + result.listType
							+ "&ignore=" + new Date().getTime();
		window.location.replace(redirect_url);
	}else{
//		alert("refresh failed.");
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', "<s:text name='error.monitor.activeClient.refresh.failed'/>");
			warnDialog.show();
		}
	}
}

var clientID;
function deauthClient(targetId)
{
	thisOperation = 'deauthClientDetail';
	clientID = targetId;
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will deauth the client.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

function doContinueOper()
{
	if (thisOperation == 'deauthClientDetail')
	{
		url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=deauthClientDetail&id=" + clientID + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: deauthResult, failure:abortResult,timeout: 35000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
}

var deauthResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.success){
		var redirect_url = "<s:url action='clientMonitor' includeParams='none' />?listType="+result.listType;
		window.location.replace(redirect_url);
	}else{
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', result.message);
			warnDialog.show();
		}
	}
}

function startSpectralAnalysis() {
    var hiveApId = '<s:property value="%{associationApId}"/>';
	var url = '<s:url action="mapNodes" includeParams="none"></s:url>' + "?operation=fetchHiveApInterfaceInfo&hiveApId="+hiveApId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessApIf, failure : resultDoNothing, timeout: 60000 }, null);
}

var resultDoNothing = function(o) {
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
}

var detailsSuccessApIf = function (o) {
	eval("var details = " + o.responseText);
	var v = details.v;
	if (v==0) {
		if(waitingPanel != null) {
			waitingPanel.hide();
		}
		warnDialog.cfg.setProperty('text', details.m);
		warnDialog.show();
	} else {
		var runAP='<s:property value="%{associationApId}"/>';
		var runInterface=v;
		var runChannelWifi0="";
		var runChannelWifi1="";
		if (v==1) {
			runChannelWifi0="1-13";
		} else {
			runChannelWifi1="36-165";
		}
		var runInterval=1;
		var runTime=5;
		var url = '<s:url action="spectralAnalysis" includeParams="none"></s:url>' + "?operation=updateSettingsParamsFromAp&runAP="+runAP + "&runInterface="+runInterface +"&runChannelWifi0="+runChannelWifi0 + "&runChannelWifi1="+runChannelWifi1 +"&runInterval="+runInterval +"&runTime="+runTime + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessIf, failure:resultDoNothing, timeout: 120000 }, null);
		if (waitingPanel!=null) {
			waitingPanel.show();
		}
	}
}

var detailsSuccessIf = function(o) {
	eval("var details = " + o.responseText);
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
	var s = details.s;
	var err = details.e;
	if (s) {
	 	var hiveApId = '<s:property value="%{associationApId}"/>';
		window.location.href = 'spectralAnalysis.action?operation=view&id='+hiveApId;
	} else {
		if (err) {
			warnDialog.cfg.setProperty('text', err);
			warnDialog.show();
		}
	}

};

function launchSpectrumAnalysis(vid) {
	window.location.href = 'spectralAnalysis.action?operation=view&id='+vid;
}


function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="clientMonitor" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln(' \'<s:property value="changedName" />\'</td>');
}

</script>
<style>
td.panelLabel {
	padding: 0px 0px 5px 8px;
	width: 20%;
	color: #003366;
}

td.panelText {
	width: 30%;
}
.healthTd {

  	vertical-align: middle;
}
</style>

<div id="content" width=""><s:form action="clientMonitor">
	<s:hidden name="id"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Refresh"
						class="button"
						onClick="syncClientInfo(<s:property value="%{activeClient.id}"/>);"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Deauth"
						class="button"
						onClick="deauthClient(<s:property value="%{activeClient.id}"/>);"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table style="padding: 6px 4px 5px 4px;" cellpadding="0"
				cellspacing="0" border="0" class="editBox" width="1000px">
				<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td>
									<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="labelT1"><b><s:text
												name="monitor.client.clientHostname" /></b></td>
										</tr>
										<tr>
											<td class="panelText" style="padding-left: 25px"><s:property value="%{hostName}" /></td>
										</tr>	
										<tr>
											<td class="labelT1"><b><s:text
												name="monitor.client.clientMac" /></b></td>
										</tr>
										<tr>
											<td class="panelText" style="padding-left: 25px"><s:property value="%{macAddress}" /></td>
										</tr>
									</table>
								</td>
								<td>
									<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="panelText" nowrap="nowrap" colspan="3">
												<table border="0" cellPadding="0" cellSpacing="0">
													<tr >
														<td rowspan="3" style="padding-right: 1px">
															<s:if test="%{clientScore>=0 && clientScore<25}">
																<img src="<s:url value="/images/client/SLA-red_Main.png" />"
																	title="<s:property value="clientScore" />" border="0px"  align="top" class="dinl">
															</s:if>
															<s:elseif test="%{clientScore>=25 && clientScore<50}">
																<img src="<s:url value="/images/client/SLA-yellow_Main.png" />"
																	title="<s:property value="clientScore" />" border="0px"  align="top" class="dinl">
															</s:elseif>
															<s:else>
																<img src="<s:url value="/images/client/SLA-green_Main.png" />"
																	title="<s:property value="clientScore" />" border="0px"  align="top" class="dinl">
															</s:else>
														</td>
														<s:if test="%{wiredClient}">
															<td class="healthTd" colspan="2"></td>
														</s:if>
														<s:else>
															<td class="healthTd">
																<s:if test="%{clientRadioScore>=0 && clientRadioScore<25}">
																	<img src="<s:url value="/images/client/SLA-red_radio.png" />"
																		title="<s:property value="clientRadioScore" />" border="0px" align="top" class="dinl">
																</s:if>
																<s:elseif test="%{clientRadioScore>=25 && clientRadioScore<50}">
																	<img src="<s:url value="/images/client/SLA-yellow_radio.png" />"
																		title="<s:property value="clientRadioScore" />" border="0px" align="top" class="dinl">
																</s:elseif>
																<s:else>
																	<img src="<s:url value="/images/client/SLA-green_radio.png" />"
																		title="<s:property value="clientRadioScore" />" border="0px" align="top" class="dinl">
																</s:else>
															</td>
															<td style="padding-left: 10px">
																<b><s:text name="monitor.client.health.radio" /></b>&nbsp;<s:property value="clientRadioScore" />
															</td>
														</s:else>
														<td rowspan="3" style="padding-left: 20px">
															<s:if test="%{!wiredClient && clientRadioScore<50}">
																<s:text name="monitor.client.health.launchSnpMessage"></s:text>
																<a href='#snp' onclick="startSpectralAnalysis();"><s:text name="monitor.client.health.launchSnp"/></a>
															</s:if>
														</td>
													</tr>
													<tr >
														<td class="healthTd">
															<s:if test="%{clientIpNetworkScore>=0 && clientIpNetworkScore<25}">
																<img src="<s:url value="/images/client/SLA-red_network.png" />"
																	title="<s:property value="clientIpNetworkScore" />" border="0px"   align="top" class="dinl">
															</s:if>
															<s:elseif test="%{clientIpNetworkScore>=25 && clientIpNetworkScore<50}">
																<img src="<s:url value="/images/client/SLA-yellow_network.png" />"
																	title="<s:property value="clientIpNetworkScore" />" border="0px"   align="top" class="dinl">
															</s:elseif>
															<s:else>
																<img src="<s:url value="/images/client/SLA-green_network.png" />"
																	title="<s:property value="clientIpNetworkScore" />" border="0px"   align="top" class="dinl">
															</s:else>
														</td>
														<td style="padding-left: 10px">
															<b><s:text name="monitor.client.health.network" /></b>&nbsp;<s:property value="clientIpNetworkScore" />
														</td>
													</tr>
													<tr >
														<td class="healthTd">
															<s:if test="%{clientApplicationScore>=0 && clientApplicationScore<25}">
																<img src="<s:url value="/images/client/SLA-red_application.png" />"
																	title="<s:property value="clientApplicationScore" />" border="0px"   align="top" class="dinl">
															</s:if>
															<s:elseif test="%{clientApplicationScore>=25 && clientApplicationScore<50}">
																<img src="<s:url value="/images/client/SLA-yellow_application.png" />"
																	title="<s:property value="clientApplicationScore" />" border="0px"   align="top" class="dinl">
															</s:elseif>
															<s:else>
																<img src="<s:url value="/images/client/SLA-green_application.png" />"
																	title="<s:property value="clientApplicationScore" />" border="0px"   align="top" class="dinl">
															</s:else>
														</td>
														<td style="padding-left: 10px">
															<b><s:text name="monitor.client.health.application" /></b>&nbsp;<s:property value="clientApplicationScore" />
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
				<tr>
					<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%"
						class="view">
						<tr>
							<th colspan="4" align="left"><s:text
								name="monitor.client.system.title" /></th>
						</tr>
						<tr>
							<td class="panelLabel"><s:text
								name="monitor.client.association.time" /></td>
							<td class="panelText"><s:property
								value="%{associationTimeString}" /></td>
							<td class="panelLabel"><s:text
								name="monitor.client.association.duration" /></td>
							<td class="panelText"><s:property value="%{duration}" /></td>
						</tr>
						<tr>
							<td class="panelLabel"><s:text
								name="monitor.client.clientIP" /></td>
							<td class="panelText"><s:property value="%{ipAddress}" /></td>
							
							<td class="panelLabel"><s:text
								name="monitor.client.clientNatIP" /></td>
							<td class="panelText"><s:property value="%{natIpAddress}" /></td>
						</tr>
						
						<tr>
						<s:if test="%{wiredClient}">
						        <td class="panelLabel">&nbsp;</td>
								<td class="panelText">&nbsp;</td>
								<td class="panelLabel"><s:text
									name="monitor.client.ssid.lan" /></td>
								<td class="panelText"><s:property value="%{ssid}" /></td>
							</s:if>
							<s:else>
								<td class="panelLabel"><s:text
									name="monitor.client.radio.mode" /></td>
								<td class="panelText"><s:property value="%{radioModeString}" />
								</td>
								<td class="panelLabel"><s:text
									name="monitor.client.ssid" /></td>
								<td class="panelText"><s:property value="%{ssid}" /></td>
							</s:else>
						</tr>
						
						<s:if test="%{!wiredClient}">
							<tr>
								<td class="panelLabel"><s:text name="monitor.client.RSSI" />
								</td>
								<td class="panelText"><s:property value="%{rssi}" /></td>
								<td class="panelLabel"><s:text name="monitor.client.SNR" />
								</td>
								<td class="panelText"><s:property value="%{snr}" /></td>
							</tr>
						</s:if>
						<tr>
							<td class="panelLabel"><s:text
								name="report.client.table.clientUserName" /></td>
							<td class="panelText"><s:property value="%{userName}" /></td>
							<s:if test="%{wiredClient}">
								<td class="panelLabel"><s:text name="monitor.client.deviceName" />
								</td>
							</s:if>
							<s:else>
								<td class="panelLabel"><s:text name="monitor.client.apName" />
								</td>
							</s:else>
							<td class="panelText"><a
								href='<s:url action="hiveAp" includeParams="none">
													  <s:param name="hmListType" value="%{'managedHiveAps'}"/>
													  <s:param name="operation" value="%{'showHiveApDetails'}"/>
													  <s:param name="id" value="%{associationApId}"/></s:url>'>
							<s:property value="%{associationApName}" /> </a></td>
						</tr>
						<s:if test="%{!wiredClient}">
						<tr>
							<td class="panelLabel"><s:text
								name="monitor.client.transmit.data.rate" /></td>
							<td class="panelText"><s:property value="%{tx}" /></td>
							<td class="panelLabel"><s:text
								name="monitor.client.receive.data.rate" /></td>
							<td class="panelText"><s:property value="%{rx}" /></td>
						</tr>
						</s:if>
						<tr>
							<td class="panelLabel"><s:text
								name="monitor.client.userProfileID" /></td>
							<td class="panelText"><s:property value="%{userProfileID}" />
							</td>
							<td class="panelLabel" nowrap><s:text
								name="report.client.table.clientCWPUsed" /></td>
							<td class="panelText" nowrap="nowrap"><s:property
								value="clientCWPUsedString" /> &nbsp;</td>
						</tr>
						<tr>
							<td class="panelLabel" nowrap><s:text
								name="report.client.table.clientAuthMethod" /></td>
							<td class="panelText" nowrap="nowrap"><s:property
								value="clientAuthMethodString" /> &nbsp;</td>
							<s:if test="%{wiredClient}">
								<td class="panelLabel">&nbsp;</td>
								<td class="panelText">&nbsp;</td>
							</s:if>
							<s:else>
								<td class="panelLabel" nowrap><s:text
									name="report.client.table.clientEncryptionMethod" /></td>
								<td class="panelText" nowrap="nowrap"><s:property
									value="clientEncryptionMethodString" /> &nbsp;</td>
							</s:else>
						</tr>
						<tr>
							<td class="panelLabel" nowrap><s:text
								name="report.client.table.clientVLAN" /></td>
							<td class="panelText" nowrap="nowrap"><s:property
								value="clientVLAN" /> &nbsp;</td>
							<s:if test="%{wiredClient}">
							<!-- client os show here if is wired client, in order to reduce row with only one column -->
								<s:if test="%{fullMode}">
									<td class="panelLabel" nowrap><s:text
										name="monitor.client.osInfo" /></td>
									<td class="panelText" nowrap="nowrap"><s:property
										value="clientOsInfo" /> &nbsp;</td>	
								</s:if>
							</s:if>
							<s:else>
								<td class="panelLabel" nowrap><s:text
									name="report.client.table.clientChannel" /></td>
								<td class="panelText" nowrap="nowrap"><s:property
									value="clientChannel" /> &nbsp;</td>
							</s:else>
						</tr>
						<s:if test="%{!wiredClient}">
						<tr>
							<td class="panelLabel" nowrap><s:text
								name="monitor.client.bssid" /></td>
							<td class="panelText" nowrap="nowrap"><s:property
								value="clientBSSID" /> &nbsp;</td>
							<!-- client os show here if is wireless client -->
							<s:if test="%{fullMode}">
								<td class="panelLabel" nowrap><s:text
									name="monitor.client.osInfo" /></td>
								<td class="panelText" nowrap="nowrap"><s:property
									value="clientOsInfo" /> &nbsp;</td>	
							</s:if>
						</tr>
						<s:if test="%{easyMode == false}">
						<tr>
							<td class="panelLabel"><s:text
								name="monitor.client.email" /></td>
							<td class="panelText"><s:property value="%{userEmail}" /></td>
							<td class="panelLabel"><s:text
								name="monitor.client.company" /></td>
							<td class="panelText"><s:property value="%{userCompany}" />
							</td>
						</tr>
						</s:if>
						</s:if>
						<tr>
							<td class="panelLabel" nowrap><s:text
								name="monitor.client.memo" /></td>
							<td class="panelText" colspan="3"><s:property
								value="%{memo}" /></td>
						</tr>
						</table>
					</td>
				</tr>

				<s:if test="%{enableClientManagementAndEnrolled}">
				<tr>
					<td valign="top">
						<tiles:insertDefinition name="clientACMDetail" />				
					</td>
				</tr>
				</s:if>
						
	
				<s:if test="%{wiredClient == false}">
				<tr>
					<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%"
						class="view">
						<tr>
							<th align="left"><s:text
								name="monitor.client.statistics.title" /></th>
						</tr>
						<tr>
							<td>
							<s:if test="%{newReportDataList!=null && newReportDataList.size>0}">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td><tiles:insertDefinition name="flash" /></td>
									</tr>
								</table>
							</s:if>
							
							<s:elseif test="%{association_stats.size<2}">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.statTime" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.statTime" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxMICFailures" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxMICFailures" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientLastRxRate" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientLastRxRate" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientLastTxRate" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientLastTxRate" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxDataFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxDataFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxDataFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxDataFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxDataOctets" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxDataOctets" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxDataOctets" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxDataOctets" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxMgtFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxMgtFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxMgtFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxMgtFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxUnicastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxUnicastFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxUnicastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxUnicastFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxMulticastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxMulticastFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxMulticastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxMulticastFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxBroadcastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxBroadcastFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxBroadcastFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxBroadcastFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientRxAirtime" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientRxAirtime" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxAirtime" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxAirtime" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxBeFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxBeDataFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxBgFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxBgDataFrames" /> &nbsp;</td>
									</tr>
									<tr>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxViFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxViDataFrames" /> &nbsp;</td>
										<td class="panelLabel" nowrap><s:text
											name="report.client.table.clientTxVoFrames" /></td>
										<td class="panelText" nowrap="nowrap"><s:property
											value="oneAssociation.clientTxVoDataFrames" /> &nbsp;</td>
									</tr>
								</table>
							</s:elseif>
							<s:else>
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td><tiles:insertDefinition name="flash" /></td>
									</tr>
								</table>
							</s:else>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				</s:if>
				<tr>
					<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%"
						class="view">
						<tr>
							<th colspan="6" align="left"><s:text
								name="monitor.client.association.title" /></th>
						</tr>
						<tr>
							<s:if test="%{wiredClient}">
								<td class="list" align="left"><b><s:text
									name="monitor.client.deviceName" /> </b></td>
								<td class="list" align="left"><b><s:text
									name="monitor.client.ssid.lan" /> </b></td>
							</s:if>
							<s:else>
								<td class="list" align="left"><b><s:text
									name="monitor.client.apName" /> </b></td>
								<td class="list" align="left"><b><s:text
									name="monitor.client.ssid" /> </b></td>
								<td class="list" align="left"><b><s:text
									name="monitor.client.bssid" /> </b></td>
							</s:else>
							<td class="list" align="left"><b><s:text
								name="monitor.client.association.time" /> </b></td>
							<td class="list" align="left"><b><s:text
								name="monitor.client.disassociation.time" /> </b></td>
							<td class="list" align="left"><b><s:text
								name="monitor.client.session.duration" /> </b></td>
						</tr>
						<s:iterator value="%{history_sessions}" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<td class="list" nowrap="nowrap"><s:property value="apName" />
								&nbsp;</td>
								<td class="list" nowrap="nowrap"><s:property
									value="clientSSID" /> &nbsp;</td>
								<s:if test="%{wiredClient == false}">
								<td class="list" nowrap="nowrap"><s:property
									value="clientBSSID" /> &nbsp;</td>
								</s:if>
								<td class="list" nowrap="nowrap"><s:property
									value="startTimeShow" /></td>
								<td class="list" nowrap="nowrap"><s:property
									value="endTimeShow" /></td>
								<td class="list" nowrap="nowrap"><s:property
									value="durationString" /></td>
							</tr>
						</s:iterator>
						<s:if test="%{gridCount_h > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount_h}">
								<s:iterator>
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" colspan="6">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
						<s:else>
							<tr>
								<td colspan="6" style="padding-left: 4px">
								<s:if test="%{clientReportsPermission == true}">
								<a href='<s:url action="reportList" includeParams='none'>
													<s:param name="listType" value="%{'clientReports'}"/>
													<s:param name="buttonType" value="%{'clientSession'}"/>
													<s:param name="operation" value="%{'runLink'}"/>
													<s:param name="reportLinkClientMac" value="%{macAddress}"/></s:url>'>More...</a>
								</s:if>
								</td>
							</tr>
						</s:else>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>


