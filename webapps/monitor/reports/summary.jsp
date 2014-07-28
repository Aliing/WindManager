<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<tiles:insertDefinition name="flashHeader" />

<link href="<s:url value="/css/hm.widget.css"  includeParams="none"/>" rel="stylesheet" type="text/css" />
<script src="<s:url value="/yui/cookie/cookie-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/yui/selector/selector-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>


<script language='JavaScript' type='text/javascript'>
var formName = 'reports';

function onLoadPage() {
	
	
}
YAHOO.util.Event.onDOMReady(function () {initCookies();initWidgetPage();});


function initCookies(){
	// BEGIN :: Check if cookie is set
	var initCookieString = '<s:property value="initCookieString"/>';
	if (initCookieString!='' && initCookieString!='NULL') {
		var containerRef = [];
		var node;
		
		var clmCookie=initCookieString.split("|");
		var cArray1,cArray2;
		if (clmCookie && clmCookie.length>0) {
			cArray1 = clmCookie[0].split(",");
		}
		if (clmCookie && clmCookie.length>1) {
			cArray2 = clmCookie[1].split(",");
		}
		
		// BEGIN :: Removing the nodes
		var len = cArray1.length;
		for(var i=0;i<len;i++){
			var menArray1=cArray1[i].split(";");
			node = document.getElementById(menArray1[0]);	
			node?containerRef[menArray1[0]] = node.parentNode.removeChild(node):"";
		}

		len = cArray2.length;
		for(var i=0;i<len;i++){
			var menArray2=cArray2[i].split(";");
			node = document.getElementById(menArray2[0]);	
			node?containerRef[menArray2[0]] = node.parentNode.removeChild(node):"";
		}

		// END :: Removing the nodes
			
		// BEGIN :: Adding the nodes
		len = cArray1.length;
		var col = document.getElementById("Column1");
		var tmpCR;
		for(var i=0;i<len;i++){
			var menArray1=cArray1[i].split(";");
			if (menArray1.length>0 && menArray1[0]!=''){
				tmpCR = containerRef[menArray1[0]]; 
				if (menArray1.length>1) {
					if (menArray1[1]=="collapsed"){
						tmpCR?YAHOO.util.Dom.addClass(tmpCR,'collapsed'):"";
					}
				}
				tmpCR?col.appendChild(tmpCR):"";
			}
		}

		len = cArray2.length;
		var col = document.getElementById("Column2");
		for(var i=0;i<len;i++){
			var menArray2=cArray2[i].split(";");
			if (menArray2.length>0 && menArray2[0]!=''){
				tmpCR = containerRef[menArray2[0]]; 
				if (menArray2.length>1) {
					if (menArray2[1]=="collapsed"){
						tmpCR?YAHOO.util.Dom.addClass(tmpCR,'collapsed'):"";
					}
				}
				tmpCR?col.appendChild(tmpCR):"";
			}
		}
		// END :: Adding the nodes	
	} 
	// END :: Check if cookie is set
}

var setCookies = function(removeId) {
	// BEGIN :: Calculate cookie expiration to 14 days from today
	//var date = new Date();
	//date.setTime(date.getTime()+(14*24*60*60*1000)); 
	// END :: Calculate cookie expiration to 14 days from today
	var getNode = function(node) {
		return (node.id==="widgetAPhealth"
		||node.id==="widgetAPsecurity"
		||node.id==="widgetAPcompliance"
		||node.id==="widgetAPalarm"
		||node.id==="widgetAPbandwidth"
		||node.id==="widgetAPsla"
		||node.id==="widgetAPversion"
		||node.id==="widgetCinfo"
		||node.id==="widgetCvendor"
		||node.id==="widgetCradio"
		||node.id==="widgetCuserprofile"
		||node.id==="widgetCsla"
		||node.id==="widgetScpu"
		||node.id==="widgetSinfo"
		||node.id==="widgetSperformanceInfo"
		||node.id==="widgetSuser"
		||node.id==="widgetAPmostClientCount"
		||node.id==="widgetAPmostBandwidth"
		||node.id==="widgetAPmostInterference"
		||node.id==="widgetAPmostCrcError"
		||node.id==="widgetAPmostTxRetry"
		||node.id==="widgetAPmostRxRetry"
		||node.id==="widgetCmostTxAirtime"
		||node.id==="widgetCmostRxAirtime"
		||node.id==="widgetCmostFailure"
		||node.id==="widgetAPuptime"
		||node.id==="widgetActiveUser"
		||node.id==="widgetAuditLog"
		
		);
	}
	var createString = function(colId) {
		var nodes = YAHOO.util.Dom.getChildrenBy(document.getElementById(colId), getNode);
		var list = [];
		var l = nodes.length;
		for(var i=0;i<l;i++) {
			if (YAHOO.util.Dom.hasClass(nodes[i],'collapsed')){
				list[i] = nodes[i].id + ";" + "collapsed";
			} else {
				list[i] = nodes[i].id + ";" + "noncollapsed";
			}
		}
		
		return list.toString();
	}
	
	var cookieString=createString("Column1") + "|" + createString("Column2");
	var url = "reports.action?operation=saveCookie" + "&cookieString="+cookieString+ "&removeWdigetId=" + removeId +"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {}, null);
}
//YAHOO.util.Event.on(window, "unload", setCookies); 	

function submitAction(operation) {
	if (!checkTotalWidgetCount()){
		return false;
	}
	showProcessing();
	document.forms[formName].operation.value = operation;
 	beforeSubmitAction(document.forms[formName]);
	document.forms[formName].submit();
}

function checkTotalWidgetCount(){
	var i=0;
	if(Get("ckwidgetAPhealth").checked) {
		i++;
	}
	if(Get("ckwidgetAPsecurity").checked) {
		i++;
	}
	if(Get("ckwidgetAPcompliance").checked) {
		i++;
	}
	if(Get("ckwidgetAPalarm").checked) {
		i++;
	}
	if(Get("ckwidgetAPbandwidth").checked) {
		i++;
	}
	if(Get("ckwidgetAPsla").checked) {
		i++;
	}
	if(Get("ckwidgetAPversion").checked) {
		i++;
	}
	if(Get("ckwidgetCinfo").checked) {
		i++;
	}
	if(Get("ckwidgetCvendor").checked) {
		i++;
	}
	if(Get("ckwidgetCradio").checked) {
		i++;
	}
	if(Get("ckwidgetCuserprofile").checked) {
		i++;
	}
	if(Get("ckwidgetCsla").checked) {
		i++;
	}
	<s:if test="%{booleanSuperUser}">
		if(Get("ckwidgetScpu").checked) {
			i++;
		}
		if(Get("ckwidgetSinfo").checked) {
			i++;
		}
		if(Get("ckwidgetSuser").checked) {
			i++;
		}
		<s:if test="%{booleanOnline}">
			if(Get("ckwidgetSperformanceInfo").checked) {
				i++;
			}
		</s:if>
	</s:if>

	if(Get("ckwidgetAPmostClientCount").checked) {
		i++;
	}
	if(Get("ckwidgetAPmostBandwidth").checked) {
		i++;
	}
	if(Get("ckwidgetAPmostInterference").checked) {
		i++;
	}
	if(Get("ckwidgetAPmostCrcError").checked) {
		i++;
	}
	if(Get("ckwidgetAPmostTxRetry").checked) {
		i++;
	}
	if(Get("ckwidgetAPmostRxRetry").checked) {
		i++;
	}
	if(Get("ckwidgetCmostTxAirtime").checked) {
		i++;
	}
	if(Get("ckwidgetCmostRxAirtime").checked) {
		i++;
	}
	if(Get("ckwidgetCmostFailure").checked) {
		i++;
	}
	if(Get("ckwidgetAuditLog").checked) {
		i++;
	}
	if(Get("ckwidgetAPuptime").checked) {
		i++;
	}
	if(Get("ckwidgetActiveUser").checked) {
		i++;
	}
	if (i>10) {
		hm.util.reportFieldError(Get("showError"),'The dashboard supports max 10 widgets at a time.');
		return false;
	}
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" />');
	<s:if test="%{hMOnline && domain.domainName!='home'}">
		document.writeln('(<s:property value="domain.vhmID" />)');
	</s:if>
	document.writeln('</td>');

}

function insertSummaryPageContext() {
	<s:if test="%{!inPassiveNode}">
		document.writeln('<td class="addContentSytle" nowrap><a href="#1" onclick="showHideAdvancePanel();"><span id="addContentSpan">Add Content &#187;</span></a>');
		document.writeln('</td>');
	</s:if>
}
function showHideAdvancePanel(){
    if (Get('eyebrow').style.display!='none') {
    	Get("addContentSpan").innerHTML="Add Content &#187;"
    	Get('eyebrow').style.display="none"
    } else {
        Get('eyebrow').style.display="block";
        Get("addContentSpan").innerHTML="Close Add Content &#171;"
    }
    return false;
}
/**
function showAdvancePanel(){
	Get('eyebrow').style.display="block";
}

function hideAdvancePanel(){
	Get('eyebrow').style.display="none";
}
**/
</script>

<style>
<!--
.saveConfigPanelHandel a.min  {
    float: left;
    display: inline;
    background: url(<s:url value="/images/buttons.gif" includeParams="none"/>) no-repeat -52px 0;
    width: 14px;
    height: 14px;
    text-indent: -9999em;
    margin: 0px 4px 4px 2px;
    outline: none;
}
.saveConfigPanelHandel a.save  {
    float: right;
    display: inline;
    background: url(<s:url value="/images/save.png" includeParams="none"/>) no-repeat 0 0;
    width: 14px;
    height: 14px;
    margin: 0px 4px 4px 2px;
    text-indent: -9999em;
    outline: none;
}

td.subList {
	padding: 2px 0px 2px 10px;
	border-bottom: 1px solid #cccccc;
	font-size: x-small;
}

.saveConfigPanelHandel a.close  {
    float: right;
    display: inline;
    background: url(<s:url value="/images/cancel.png" includeParams="none"/>) no-repeat 0 0;
    width: 14px;
    height: 14px;
    margin: 0px 4px 4px 2px;
    text-indent: -9999em;
    outline: none;
}

.saveConfigPanelHandel span {
    padding: 0 3px;
    float: left;
}
.collapsed .saveConfigPanelHandel a.min {background-position:-38px 0;}

.saveConfigPanelHandel {
	width: 100%;
	height: 14px;
	border-bottom: 0;
	text-align: left;
	color: #303030;
	background-color: #C1E4FF;
	font-weight: bold;
	padding: 2px 0;
	overflow: hidden;
}	

.saveConfigPanelContext {
	width: 100%;
	overflow: hidden;
	-moz-border-radius-bottomleft: 2px;
    -moz-border-radius-bottomright: 2px;
    -webkit-border-bottom-left-radius: 2px;
    -webkit-border-bottom-right-radius: 2px;
}

.saveConfigPanel {
	border: 2px solid #C1E4FF;
	background-color: #fff;
	color: #000;
	width: 99%;
	margin: 2px;
	margin-bottom: 8px;
	overflow: hidden;
	-moz-border-radius: 4px;
    -webkit-border-radius: 4px;
}

.addContentSytle {
	float: right;
	color: #303030;
	font-weight: bold;
    margin: 0px 4px 4px 2px;
    outline: none;
}

-->
</style>


<div id="content"><s:form action="reports">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="flashHeader" /></td>
		</tr>

		<tr>
			<td style="padding-top: 3px;">  
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td ><div><table><tr><td><span id="showError"/></td></tr></table></div></td>
					</tr>
					<tr>
						<td>
							<div id="eyebrow" style="display:none" class="saveConfigPanel collapsed">
								<div class="saveConfigPanelHandel">
									<span>Add Content</span>
									<a href="javascript:void(0);" class="close" title="close" onclick="showHideAdvancePanel();"></a>
									<a href="javascript:void(0);" class="save" title="save configuration" onclick="submitAction('saveConfig');"></a>
									
                        		</div>
								<div class="saveConfigPanelContext">
									<table width="100%" border="0" cellspacing="0" cellpadding="0" >
										<tr>
											<td  style="padding: 4px 4px 4px 4px" width="50%" valign="top">
												<fieldset>
												<legend><s:text name="report.summary.widgetitle.deviceInfo"/> </legend>
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPhealth" name="ckwidgetAPhealth" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apHealth" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPalarm" name="ckwidgetAPalarm" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apAlarm" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPsla" name="ckwidgetAPsla" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apSla" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostClientCount" name="ckwidgetAPmostClientCount" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostClient" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostBandwidth" name="ckwidgetAPmostBandwidth" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostBandwidth" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostInterference" name="ckwidgetAPmostInterference" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostInterference" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostCrcError" name="ckwidgetAPmostCrcError" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostCrcError" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostTxRetry" name="ckwidgetAPmostTxRetry" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostTxRetry" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPmostRxRetry" name="ckwidgetAPmostRxRetry" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.apInfo.mostRxRetry" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPcompliance" name="ckwidgetAPcompliance" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apCompliance" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPversion" name="ckwidgetAPversion" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apVersion" />
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
											<td style="padding: 4px 4px 4px 4px" width="50%" valign="top">
												<fieldset>
												<legend><s:text name="report.summary.widgetitle.clientInfo"/> </legend>
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<s:checkbox id="ckwidgetCinfo" name="ckwidgetCinfo" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.cInfo" />
														</td>
														
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCradio" name="ckwidgetCradio" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.cRadio" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCsla" name="ckwidgetCsla" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.cSla" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCuserprofile" name="ckwidgetCuserprofile" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.cUserprofile" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCvendor" name="ckwidgetCvendor" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.cVendor" />
														</td>
													</tr>												
													<tr>
														<td>
															<s:checkbox id="ckwidgetActiveUser" name="ckwidgetActiveUser" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.activeUser" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCmostTxAirtime" name="ckwidgetCmostTxAirtime" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.cInfo.mosttxAirtime" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCmostRxAirtime" name="ckwidgetCmostRxAirtime" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.cInfo.mostrxAirtime" />
														</td>
													</tr>
													<tr>
														<td>	
															<s:checkbox id="ckwidgetCmostFailure" name="ckwidgetCmostFailure" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.cInfo.mostFailure" />
														</td>
													</tr>
												</table>
												</fieldset>
											</td>
										</tr>
										<tr>
											<td  style="padding: 4px 4px 4px 4px" width="50%" valign="top">
												<fieldset>
												<legend><s:text name="report.summary.widgetitle.netWorkInfo"/> </legend>
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPsecurity" name="ckwidgetAPsecurity" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apSecurity" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPbandwidth" name="ckwidgetAPbandwidth" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apBandwidth" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAPuptime" name="ckwidgetAPuptime" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.apUptime" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetAuditLog" name="ckwidgetAuditLog" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.auditLog" />
															</td>
														</tr>
														<s:if test="%{booleanSuperUser && booleanOnline}">
														<tr>
															<td>	
																<s:checkbox id="ckwidgetSperformanceInfo" name="ckwidgetSperformanceInfo" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.sperformanceInfo" />
															</td>
														</tr>
														</s:if>
													</table>
												</fieldset>
											</td>
										<s:if test="%{booleanSuperUser}" >
											<td  style="padding: 4px 4px 4px 4px" width="50%" valign="top">
												<fieldset>
												<legend><s:text name="report.summary.widgetitle.hmInfo"/> </legend>
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:checkbox id="ckwidgetSinfo" name="ckwidgetSinfo" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.sinfo" />
															</td>
														</tr>
														<tr>
															<td>
																<s:checkbox id="ckwidgetSuser" name="ckwidgetSuser" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.suser" />
															</td>
														</tr>
														<tr>	
															<td>
																<s:checkbox id="ckwidgetScpu" name="ckwidgetScpu" onclick="return checkTotalWidgetCount();"></s:checkbox><s:text name="report.summary.widgetitle.scpu" />
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</s:if>	
										</tr>
									</table>
								</div>
							</div>
						</td>
					</tr>
					
					<tr>
						<td>
							<div class="FixedWidth">
								<div id="Column1">
								<s:if test="%{ckwidgetSinfo && booleanSuperUser}">
									<div id="widgetSinfo" class="widgetRec">
										<div id="widgetSinfoHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.sinfo" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
							                	<tr>
							                		<td class="labelT1" width="120px">
							                			<s:text name="report.summary.sInfo.hostname" />
							                		</td>
							                		<td>
							                			<s:property value="hmHostname" />
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.softVersion" />
							                		</td>
							                		<td>
							                			<s:property value="hmVersion" />
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.buildtime" />
							                		</td>
							                		<td>
							                			<s:property value="buildTime" />
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.modelNumber" />
							                		</td>
							                		<td>
							                			<s:property value="hmModel" />
							                		</td>
							                	</tr>
							                	<s:if test="%{showSerialNumber}">
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.serialNumber" />
							                		</td>
							                		<td>
							                			<s:property value="hmSN" />
							                		</td>
							                	</tr>
							                	</s:if>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.uptime" />
							                		</td>
							                		<td>
							                			<s:property value="systemUpTime" />
							                		</td>
							                	</tr>
							                	<s:if test="%{booleanOnline==false}">
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.haStatus" />
							                		</td>
							                		<td>
							                			<s:property value="haStatus" />
							                		</td>
							                	</tr>
							                	<s:if test="%{showReplicateStatus}">
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.replication" />
							                		</td>
							                		<td>
							                			<s:property value="replicateStatus" />
							                		</td>
							                	</tr>
							                	</s:if>
							                	</s:if>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.mgtPort" />
							                		</td>
							                		<td>
							                			<s:property value="mgtState" />
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.sInfo.lanPort" />
							                		</td>
							                		<td>
							                			<s:property value="lanState" />
							                		</td>
							                	</tr>
						                	</table>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetScpu && booleanSuperUser}">
									<div id="widgetScpu" class="widgetRec">
										<div id="widgetScpuHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.scpu" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_systemcpu" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_systemcpu_h" />',
							                			'<s:property value="wf_systemcpu" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetAPhealth}">
									<div id="widgetAPhealth" class="widgetRec">
										<div id="widgetAPhealthHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apHealth" /></span>
                        				</div>
										
										<div class="widgetContext">
											<table  border="0" cellspacing="0" cellpadding="0">
												<tr>
							                		<td class="labelT1" width="300px">
							                			<s:text name="report.summary.apSecurity.apSecNew" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="hiveAp.action">
							                			<s:param name="operation" value="%{'view'}"/>
							                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
							                			<s:param name="dashCondition" value="%{'autoDiscoverHiveAps'}"/></s:url>'>
							                			<s:property value="apSecNew" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1" width="300px">
							                			<s:text name="report.summary.apHealth.apUp" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="hiveAp.action">
							                			<s:param name="operation" value="%{'view'}"/>
							                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
							                			<s:param name="dashCondition" value="%{'apHthUp'}"/></s:url>'>
							                			<s:property value="apHthUp" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.apHealth.apDown" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="hiveAp.action">
							                			<s:param name="operation" value="%{'view'}"/>
							                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
							                			<s:param name="dashCondition" value="%{'apHthDown'}"/></s:url>'>
							                			<s:property value="apHthDown" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.apHealth.apAlarm" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="hiveAp.action">
							                			<s:param name="operation" value="%{'view'}"/>
							                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
							                			<s:param name="dashCondition" value="%{'alarm'}"/></s:url>'>
							                			<s:property value="apHthAlarm" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.apHealth.apOutdate" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="hiveAp.action"> 
							                			<s:param name="operation" value="%{'view'}"/>
							                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
							                			<s:param name="dashCondition" value="%{'outofData'}"/></s:url>'>
							                			<s:property value="apHthOutdate" /></a>
							                		</td>
							                	</tr>
							                	
						                	</table>
										</div>
									</div>
									</s:if>
									<s:if test="%{ckwidgetCinfo}">
									<div id="widgetCinfo" class="widgetRec">
										<div id="widgetCinfoHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.cInfo" /></span>
										</div>
										<div class="widgetContext">
											 <table border="0" cellspacing="0" cellpadding="0">
							                	<tr>
							                		<td class="labelT1" width="300px">
							                			<s:text name="report.summary.cInfo.activeClientCount" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="monitorMenu.action">
							                			<s:param name="operation" value="%{'clientMonitor'}"/></s:url>'>
							                			<s:property value="activeClientCount" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.cInfo.maxClientCount" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="reportList.action">
							                			<s:param name="operation" value="%{'runLink'}"/>
							                			<s:param name="listType" value="%{'maxClient'}"/>
							                			<s:param name="buttonType" value="%{'maxClient'}"/></s:url>'>
							                			<s:property value="maxClientCount" /></a>
							                		</td>
							                	</tr>
						                	</table>
										</div>
									</div>
									</s:if>
									<s:if test="%{ckwidgetCsla}">
									<div id="widgetCsla" class="widgetRec">
										<div id="widgetCslaHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.cSla" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_clientsla" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_clientsla_h" />',
							                			'<s:property value="wf_clientsla" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetAPmostBandwidth}">
									<div id="widgetAPmostBandwidth" class="widgetRec">
										<div id="widgetAPmostBandwidthHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostBandwidth" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.bandwidth"/>
													</td>
												</tr>
												<s:iterator value="%{apMostBandwidthData}" status="status">
													<tr>
														<td class="subList"><s:property value="key" /></td>
														<td class="subList"><s:property value="value" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetAPmostInterference}">
									<div id="widgetAPmostInterference" class="widgetRec">
										<div id="widgetAPmostInterferenceHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostInterference" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.wifi0Rate"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.wifi1Rate"/>
													</td>
												</tr>
												<s:iterator value="%{lstInterferenceCrcError}" status="status">
													<tr>
														<td class="subList"><s:property value="name" /></td>
														<td class="subList"><s:property value="txdata" /></td>
														<td class="subList"><s:property value="rxdata" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetAPmostCrcError}">
									<div id="widgetAPmostCrcError" class="widgetRec">
										<div id="widgetAPmostCrcErrorHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostCrcError" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.interfaceName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.reportTime"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.timePeriod"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.crcErrorCount"/>
													</td>
												</tr>
												<s:iterator value="%{crcErrorTop}" status="status">
													<tr>
														<td class="subList"><s:property value="apName" /></td>
														<td class="subList"><s:property value="ifName" /></td>
														<td class="subList"><s:property value="timeStampString" /></td>
														<td class="subList"><s:property value="collectPeriod" /></td>
														<td class="subList"><s:property value="crcErrorRate" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetAPmostTxRetry}">
									<div id="widgetAPmostTxRetry" class="widgetRec">
										<div id="widgetAPmostTxRetryHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostTxRetry" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.interfaceName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.reportTime"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.timePeriod"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.txRetryCount"/>
													</td>
												</tr>
												<s:iterator value="%{txRetryRateTop}" status="status">
													<tr>
														<td class="subList"><s:property value="apName" /></td>
														<td class="subList"><s:property value="ifName" /></td>
														<td class="subList"><s:property value="timeStampString" /></td>
														<td class="subList"><s:property value="collectPeriod" /></td>
														<td class="subList"><s:property value="txRetryRate" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetAPmostRxRetry}">
									<div id="widgetAPmostRxRetry" class="widgetRec">
										<div id="widgetAPmostRxRetryHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostRxRetry" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.interfaceName"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.reportTime"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.timePeriod"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.rxRetryCount"/>
													</td>
												</tr>
												<s:iterator value="%{rxRetryRateTop}" status="status">
													<tr>
														<td class="subList"><s:property value="apName" /></td>
														<td class="subList"><s:property value="ifName" /></td>
														<td class="subList"><s:property value="timeStampString" /></td>
														<td class="subList"><s:property value="collectPeriod" /></td>
														<td class="subList"><s:property value="rxRetryRate" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

						            <s:if test="%{ckwidgetAPsecurity}">
									<div id="widgetAPsecurity" class="widgetRec">
										<div id="widgetAPsecurityHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apSecurity" /></span>
										</div>
										<div class="widgetContext">
											<table  border="0" cellspacing="0" cellpadding="0">
												<tr>
							                		<td class="labelT1" width="220px">
							                			<s:text name="report.summary.apSecurity.apSecFriend" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="monitorMenu.action">
							                			<s:param name="operation" value="%{'friendlyAps'}"/></s:url>'>
							                			<s:property value="apSecFriend" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.apSecurity.apSecRogue" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="monitorMenu.action">
							                			<s:param name="operation" value="%{'rogueAps'}"/></s:url>'>
							                			<s:property value="apSecRogue" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.cInfo.rogueClientCountInNet" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="idp.action"> 
							                			<s:param name="listType" value="%{'rogueClient'}"/>
							                			<s:param name="operation" value="%{'rogueClientInNet'}"/></s:url>'>
							                			<s:property value="rogueClientCountInNet" /></a>
							                		</td>
							                	</tr>
							                	<tr>
							                		<td class="labelT1">
							                			<s:text name="report.summary.cInfo.rogueClientCountOnMap" />
							                		</td>
							                		<td>
							                			<a href='<s:url value="idp.action"> 
							                			<s:param name="listType" value="%{'rogueClient'}"/>
							                			<s:param name="operation" value="%{'rogueClientOnMap'}"/></s:url>'>
							                			<s:property value="rogueClientCountOnMap" /></a>
							                		</td>
							                	</tr>
						                	</table>
										</div>
									</div>
									</s:if>
						            <s:if test="%{ckwidgetAPcompliance}">
									<div id="widgetAPcompliance" class="widgetRec">
										<div id="widgetAPcomplianceHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apCompliance" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_compliance" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_compliance_h" />',
							                			'<s:property value="wf_compliance" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetAPuptime}">
									<div id="widgetAPuptime" class="widgetRec">
										<div id="widgetAPuptimeHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apUptime" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_apuptime" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_apuptime_h" />',
							                			'<s:property value="wf_apuptime" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetActiveUser}">
									<div id="widgetActiveUser" class="widgetRec">
										<div id="widgetActiveUserHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.activeUser" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_activeuser" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_activeuser_h" />',
							                			'<s:property value="wf_activeuser" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
								</div>
								
								
								<div id="Column2">
						            <s:if test="%{ckwidgetAPalarm}">
									<div id="widgetAPalarm" class="widgetRec">
										<div id="widgetAPalarmHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apAlarm" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_alarm" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_alarm_h" />',
							                			'<s:property value="wf_alarm" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>

						            <s:if test="%{ckwidgetAPmostClientCount}">
									<div id="widgetAPmostClientCount" class="widgetRec">
										<div id="widgetAPmostClientCountHandle" class="widgetHandle">
											<span><s:text name="report.summary.apInfo.mostClient" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.apName"/>
													</td>
													<td class="labelT1">
														<s:text name="monitor.activeClient.map"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.apInfo.clientCount"/>
													</td>
												</tr>
												<s:iterator value="%{apmaxClientList}" status="status">
													<tr>
														<td class="subList"><s:property value="name" /></td>
														<td class="subList"><s:property value="location" />&nbsp;</td>
														<td class="subList"><s:property value="slaCount" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>
						           
						           	<s:if test="%{ckwidgetAPbandwidth}">
									<div id="widgetAPbandwidth" class="widgetRec">
										<div id="widgetAPbandwidthHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apBandwidth" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_bandwidth" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_bandwidth_h" />',
							                			'<s:property value="wf_bandwidth" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
						           	<s:if test="%{ckwidgetCradio}">
									<div id="widgetCradio" class="widgetRec">
										<div id="widgetCradioHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.cRadio" /></span>
										</div>
										<div class="widgetContext">
											 <script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_radio" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_radio_h" />',
							                			'<s:property value="wf_radio" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetAPsla}">
									<div id="widgetAPsla" class="widgetRec">
										<div id="widgetAPslaHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apSla" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_apsla" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_apsla_h" />',
							                			'<s:property value="wf_apsla" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetSuser && booleanSuperUser}">
									<div id="widgetSuser" class="widgetRec">
										<div id="widgetSuserHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.suser" /></span>
										</div>
										<div class="widgetContext">
											 <script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_systemuser" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_systemuser_h" />',
							                			'<s:property value="wf_systemuser" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetSperformanceInfo && booleanSuperUser && booleanOnline}">
									<div id="widgetSperformanceInfo" class="widgetRec">
										<div id="widgetSperformanceInfoHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.sperformanceInfo" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_systemperfomance" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_systemperfomance_h" />',
							                			'<s:property value="wf_systemperfomance" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
						
						            <s:if test="%{ckwidgetCvendor}">
									<div id="widgetCvendor" class="widgetRec">
										<div id="widgetCvendorHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.cVendor" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_vendor" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_vendor_h" />',
							                			'<s:property value="wf_vendor" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetCuserprofile}">
									<div id="widgetCuserprofile" class="widgetRec">
										<div id="widgetCuserprofileHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.cUserprofile" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_userprofile" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_userprofile_h" />',
							                			'<s:property value="wf_userprofile" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetCmostTxAirtime}">
									<div id="widgetCmostTxAirtime" class="widgetRec">
										<div id="widgetCmostTxAirtimeHandle" class="widgetHandle">
											<span><s:text name="report.summary.cInfo.mosttxAirtime" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.client.clientMac"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.reportTime"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.timePeriod"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.txAirtime"/>
													</td>
												</tr>
												<s:iterator value="%{txAirtimeTop}" status="status">
													<tr>
														<td class="subList"><s:property value="clientMac" /></td>
														<td class="subList"><s:property value="timeStampString" /></td>
														<td class="subList"><s:property value="collectPeriod" /></td>
														<td class="subList"><s:property value="txAirTime" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetCmostRxAirtime}">
									<div id="widgetCmostRxAirtime" class="widgetRec">
										<div id="widgetCmostRxAirtimeHandle" class="widgetHandle">
											<span><s:text name="report.summary.cInfo.mostrxAirtime" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="report.client.clientMac"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.reportTime"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.timePeriod"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.rxAirtime"/>
													</td>
												</tr>
												<s:iterator value="%{rxAirtimeTop}" status="status">
													<tr>
														<td class="subList"><s:property value="clientMac" /></td>
														<td class="subList"><s:property value="timeStampString" /></td>
														<td class="subList"><s:property value="collectPeriod" /></td>
														<td class="subList"><s:property value="rxAirTime" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>

									<s:if test="%{ckwidgetCmostFailure}">
									<div id="widgetCmostFailure" class="widgetRec">
										<div id="widgetCmostFailureHandle" class="widgetHandle">
											<span><s:text name="report.summary.cInfo.mostFailure" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="labelT1">
														<s:text name="report.client.clientMac"/>
													</td>
													<td class="labelT1">
														<s:text name="report.summary.cInfo.failureCount"/>
													</td>
												</tr>
												<s:iterator value="%{lstClietnFailures}" status="status">
													<tr>
														<td class="subList"><s:property value="value" /></td>
														<td class="subList"><s:property value="key" /></td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>
									
						            <s:if test="%{ckwidgetAPversion}">
									<div id="widgetAPversion" class="widgetRec">
										<div id="widgetAPversionHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.apVersion" /></span>
										</div>
										<div class="widgetContext">
											<script type="text/javascript">
							                			insertWidgitFlash('<s:url value="/monitor/reports/" includeParams="none"/>'+'<s:property value="wf_apversion" />',
							                			'<s:property value="width" />',
							                			'<s:property value="wf_apversion_h" />',
							                			'<s:property value="wf_apversion" />',
							                			'<s:property value="bgcolor" />'
							                			);
							                </script>
										</div>
									</div>
									</s:if>
									
									<s:if test="%{ckwidgetAuditLog}">
									<div id="widgetAuditLog" class="widgetRec">
										<div id="widgetAuditLogHandle" class="widgetHandle">
											<span><s:text name="report.summary.widgetitle.auditLog" /></span>
										</div>
										<div class="widgetContext">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1">
														<s:text name="admin.auditLog.userName"/>
													</td>
													<td class="labelT1">
														<s:text name="admin.auditLog.hostIP"/>
													</td>
													<td class="labelT1">
														<s:text name="admin.auditLog.operation"/>
													</td>
													<td class="labelT1">
														<s:text name="admin.auditLog.status"/>
													</td>
													<td class="labelT1">
														<s:text name="admin.auditLog.time"/>
													</td>
													<s:if test="%{showDomain}">
														<td class="labelT1">
															<s:text name="config.domain"/>
														</td>
													</s:if>
												</tr>
												<s:if test="%{lstAuditLog.size() == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="%{lstAuditLog}" status="status">
													<tr>
														<td class="subList"><s:property value="%{userOwner}" /></td>
														<td class="subList"><s:property value="%{hostIP}" /></td>
														<td class="subList"><s:property value="%{opeationComment}" /></td>
														<td class="subList"><s:property value="%{statusStr}" /></td>
														<td class="subList"><s:property value="%{logTime}" /></td>
														<s:if test="%{showDomain}">
															<td class="subList">
																<s:property value="%{owner.domainName}" />
															</td>
														</s:if>
													</tr>
												</s:iterator>
											</table>
										</div>
									</div>
									</s:if>
								</div>
							</div>
						 </td>
					</tr>
				</table>  
			 </td>
		</tr>
	</table>
</s:form>
</div>

<s:if test="%{showUpgradeLog}">
<div id="upgradeLogPanel" style="display:none">
	<div class="hd">System Upgrade Log</div>
	<div class="bd">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
				<p>
					<s:text name="feature.ssl"/> has generated a log containing a list of configuration items that have been modified by the latest upgrade 
					( <font style="font-weight:bold">HM Admin > Logs > Upgrade Log</font> ).<br><br> 
					Each log entry describes the configuration change and includes one or more recommended actions to take. <br><br>
					To ensure that your configuration is correct and complete, please view the log and take action where necessary.
				</p>
			</td>
		</tr>
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding:0 2px 0 10px">
						<s:radio label="Gender" name="logAction" 
							list="#{'view':''}" id="radioLogView" value="%{defaultLogAction}"/>
					</td>
					<td width="250px" style="padding:4px 2px 3px 0">
						<s:text name="report.summary.upgradeLog.view" />
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td style="padding:0 2px 0 10px">
						<s:radio label="Gender" name="logAction" 
							list="#{'remind':''}" id="radioLogRemind" value="%{defaultLogAction}"/>
					</td>
					<td width="250px" style="padding:4px 2px 3px 0">
						<s:text name="report.summary.upgradeLog.remind" />
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td style="padding:0 2px 0 10px">
						<s:radio label="Gender" name="logAction" 
							list="#{'ignore':''}" id="radioLogIgnore" value="%{defaultLogAction}"/>
					</td>
					<td width="3600px" style="padding:4px 2px 3px 0">
						<s:text name="report.summary.upgradeLog.ignore" />
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="16"></td>
		</tr>
		<tr>
			<td style="padding-top: 8px;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="OK"
						class="button" onClick="doLogAction();"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</div>
</div>
<script type="text/javascript">
var upgradeLogPanel = null;

createUpgradeLogPanel();
showUpgradeLogPanel();

function createUpgradeLogPanel() {
	var div = document.getElementById('upgradeLogPanel');
		upgradeLogPanel = new YAHOO.widget.Panel(div, {
			width:"520px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true,
			modal:true,
			close:false,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration: 1}
			});
		upgradeLogPanel.render(document.body);
		div.style.display = "";
}
	
function showUpgradeLogPanel() {
	upgradeLogPanel.cfg.setProperty('visible', true);
}

function closeUpgradeLogPanel() {
	upgradeLogPanel.cfg.setProperty('visible', false);		
}
	
function showUpgradeLog() {
	closeUpgradeLogPanel();
	
	var url = '<s:url action="upgradeLog" includeParams="none"><s:param name="operation" value="none"/></s:url>';
	window.location.href = url;
}

function doLogAction() {
	var radios = document.getElementsByName("logAction");
	var thisOperation;
	
	if(radios[0].checked) {
		thisOperation = "viewUpgradeLog";	
	} else if(radios[1].checked) {
		thisOperation = "remindUpgradeLog";	
	} else if(radios[2].checked) {
		thisOperation = "ignoreUpgradeLog";
}

	closeUpgradeLogPanel();
	var url = '<s:url action="reports" includeParams="none" />?operation=' + thisOperation;
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {}, null);
	
	if(radios[0].checked) {
		showUpgradeLog();
	}	
}
</script>
</s:if>

