<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<s:if test="%{customerRegistered == 1}">
<script type="text/javascript" src="<s:url value="/js/doT.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<style type="text/css">
<!--
.table th,
.table td {
  padding: 8px;
  line-height: 20px;
  text-align: left;
  vertical-align: top;
  border-bottom: 1px solid #dddddd;
}

.table th {
  font-weight: bold;
  background: #eeeeee;
}
.table td.even{
	background: #FCFCFC;
}
.tip_label{
	font-style: italic;
	color: #999;
}
span.hl{
	font-weight: bold;
	color: #FFCC00;
}
#refreshHolder{
	display: block;
	width: 16px;
	height: 16px;
}
a.refresh_pause{
	background: url("<s:url value="/images/sa/pause-off.png" includeParams="none"/>") no-repeat scroll 0 0 transparent
}
a.refresh_pause:hover{
	background: url("<s:url value="/images/sa/pause-on.png" includeParams="none"/>") no-repeat scroll 0 0 transparent
}
a.refresh_resume{
	background: url("<s:url value="/images/sa/resume-off.png" includeParams="none"/>") no-repeat scroll 0 0 transparent
}
a.refresh_resume:hover{
	background: url("<s:url value="/images/sa/resume-on.png" includeParams="none"/>") no-repeat scroll 0 0 transparent
}
-->
</style>
<script id="trackClientsTmpl" type="text/x-dot-template">
<table width="100%" class="table" border="0" cellspacing="0" cellpadding="0">
{{ var count = 0, cls=""; }}
{{ for (var i = 0, l = it.d.length; i < l; i++) { }}
	{{ if(!it.f || new RegExp(it.f, "ig").test(it.d[i].m)){ count++; cls=count%2==0?"even":""; }}
			<tr>
				{{ if(!it.f) { }}
					<td width="160px" class="list {{=cls}}">{{=it.d[i].m}}</td>
				{{ } else { }}
					<td width="160px" class="list {{=cls}}">{{=it.d[i].m.replace(new RegExp(it.f, "ig"), function(word){return "<span class='hl'>"+word+"</span>";}) }}</td>
				{{ } }}
				<td class="list {{=cls}}">{{=it.d[i].t}}</td>
			</tr>
	{{ } }}
{{ } }}
{{ for (var i=0, l = 5 - count; i < l; i++) { }}
			<tr>
				<td width="160px" class="list">&nbsp;</td>
				<td class="list">&nbsp;</td>
			</tr>
{{ } }}
</table>
</script>
<script type="text/javascript">
var trackPanel;
var trackPollingId=0;
var trackMacAddress="";
var trackList = [];
var trackTimerId = 0;
function createTrackPanel(){
	var div = window.document.getElementById('trackPanel');
	trackPanel = new YAHOO.widget.Panel(div, { width:"480px", visible:false, modal:true, fixedcenter:true, draggable:true, constraintoviewport:true } );
	trackPanel.render(document.body);
	trackPanel.beforeHideEvent.subscribe(beforeTrackPanelHide);
	div.style.display = "";
}

function showTrackPanel(mac, storeName, sensorName){
	if(mac == undefined){
		showWarnDialog("The target sensor device is null");
		return;
	}
	if(null == trackPanel){
		createTrackPanel();
	}
	var header = sensorName == undefined ? "" : (storeName == undefined ? sensorName : sensorName + " - " + storeName);
	trackPanel.setHeader(header);
	clearTrackData();
	trackPanel.show();
	trackMacAddress = mac;
	startTracking();
	autoRefreshed = true;
	showPauseRefreshHolder();
}

function hideTrackPanel(){
	if(null != trackPanel){
		trackPanel.hide();
	}
}

function beforeTrackPanelHide(){
	stopTracking();
	stopTrackPolling();
}

function updateTrackingConnectionUI(result){
	var isConnected = result.conn;
	var present = result.p;
	var statusContext = isConnected? '<img class="dinl" hspace="2" src="<s:url value="/images/HM-capwap-up.png"/>" title="' + (result.s || "") + '">' : '<img class="dinl" hspace="2" src="<s:url value="/images/HM-capwap-down.png"/>" title="' + (result.s || "") + '">'
	document.getElementById("sensorStatusTd").innerHTML = present ? (statusContext || "&nbsp;") : "&nbsp;";
	document.getElementById("sensorConnTd").innerHTML = result.c || "&nbsp;";
}

function startTracking(){
	var url = "<s:url action='retailAnalytics' includeParams='none'/>?operation=startTracking&sensorName="+trackMacAddress+"&ignore=" + new Date().getTime();
	ajaxRequest(null, url, startTrackingSuccess, "POST");
}

function startTrackingSuccess(o){
	eval("var result = " + o.responseText);
	updateTrackingConnectionUI(result);
	var present = result.p;
	if(present){
		startTrackPolling();		
	}else{
		hm.util.reportFieldError(document.getElementById("sensorStatusTd"), "Device cannot be found.");
	}
}

function stopTracking(){
	var url = "<s:url action='retailAnalytics' includeParams='none'/>?operation=stopTracking&ignore=" + new Date().getTime();
	ajaxRequest(null, url, null, "POST");
}

function startTrackPolling(){
	trackPollingId = setTimeout(trackPolling, 10000);// 10 seconds
}

function stopTrackPolling(){
	clearTimeout(trackPollingId);
}

function trackPolling(){
	var url = "<s:url action='retailAnalytics' includeParams='none'/>?operation=trackPolling&sensorName="+trackMacAddress+"&ignore=" + new Date().getTime();
	ajaxRequest(null, url, trackPollingSuccess);
}

function trackPollingSuccess(o){
	eval("var result = " + o.responseText);
	updateTrackingConnectionUI(result);
	var list = result.list || [];
	list.reverse();
	trackList = list;
	var filter = document.getElementById("trackClientFilter").value;
	fillDeviceData("trackClientsTmplWrapper", "trackClientsTmpl", {"f": filter, "d": trackList});
	document.getElementById("trackClientCount").innerHTML = list.length;
	startTrackPolling();
}

function clearTrackData(){
	document.getElementById("sensorStatusTd").innerHTML = "&nbsp;";
	document.getElementById("sensorConnTd").innerHTML = "&nbsp;";
	document.getElementById("trackClientCount").innerHTML = "0";
	document.getElementById("trackClientFilter").value = "";
	fillDeviceData("trackClientsTmplWrapper", "trackClientsTmpl", {"f": "", "d": []});
}

function fillDeviceData(elem, tmpl, json){
	document.getElementById(elem).innerHTML = doT.template(document.getElementById(tmpl).innerHTML)(json);
}
// window onreize
YAHOO.util.Event.on(window, "resize", resizeIframe);
YAHOO.util.Event.on(window, "load", resizeIframe);
function resizeIframe(){
	var vpHeight = YAHOO.util.Dom.getViewportHeight();
	var dashboardIfTop = YAHOO.util.Dom.getY("dashboardIframe");
	var dashboardIframe = document.getElementById("dashboardIframe");
	dashboardIframe.height = (vpHeight - dashboardIfTop - 10) > 0 ? (vpHeight - dashboardIfTop - 10) + "px" : "0px";
	//var sensorDataIfTop = YAHOO.util.Dom.getY("sensorDataIframe");
	var sensorDataIframe = document.getElementById("sensorDataIframe");
	sensorDataIframe.height = (vpHeight - dashboardIfTop - 10) > 0 ? (vpHeight - dashboardIfTop - 10) + "px" : "0px";
}
YAHOO.util.Event.on("trackClientFilter", "keyup", filterTrackClient);
function filterTrackClient(){
	clearTimeout(trackTimerId);
	trackTimerId = setTimeout(function(){
		var filter = document.getElementById("trackClientFilter").value;
		fillDeviceData("trackClientsTmplWrapper", "trackClientsTmpl", {"f": filter, "d": trackList});
	}, 200);
}

var autoRefreshed = true;
function updateRefreshHolder(){
	autoRefreshed = !autoRefreshed;
	if(autoRefreshed){
		showPauseRefreshHolder();
		startTrackPolling();
	}else{
		showResumeRefreshHolder();
		stopTrackPolling();
	}
}
function showPauseRefreshHolder(){
	var refreshHolder = document.getElementById("refreshHolder");
	refreshHolder.className = "refresh_pause";
	refreshHolder.title = "Pause";
	document.getElementById("tip_label").style.display = "";
}
function showResumeRefreshHolder(){
	var refreshHolder = document.getElementById("refreshHolder");
	refreshHolder.className = "refresh_resume";
	refreshHolder.title = "Resume";
	document.getElementById("tip_label").style.display = "none";
}
</script>
</s:if><s:elseif test="%{customerRegistered == 2}">
<script type="text/javascript">
var registerPanel;
function createRegisterPanel(){
	var div = document.getElementById('euclidRegisterPanel');
	registerPanel = new YAHOO.widget.Panel(div, { width:"600px", visible:false, modal:true, fixedcenter:"contained", draggable:true, constraintoviewport:true } );
	registerPanel.render(document.body);
	div.style.display = "";
}
function showRegisterPanel(){
	if(null == registerPanel){
		createRegisterPanel();
		document.getElementById("registerIframe").src = "<s:url value='/tiles/Euclid_Aerohive_Eula.htm'  includeParams='none'/>?ignore="+new Date().getTime();
	}
	registerPanel.show();
}
function hideRegisterPanel(){
	if(null != registerPanel){
		registerPanel.hide();
	}
}
function acceptRegister(acceptBtn){
	submitAction('register'); 
	acceptBtn.value = "<s:text name="button.accepting" />";
	acceptBtn.disabled = true;
}
</script>
</s:elseif>

<script>
var formName = 'retailAnalytics';

function onLoadPage(){
	openDashboard();
	<%-- CFD-404, Euclid may return incorrect data, this action is used to sync to update cache data --%>
	setTimeout(function(){
			syncCustomerInfoFromEuclid();
		}, 5000);
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'configure'){
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation){
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
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
	waitingPanel.showMaskEvent.subscribe(function(){
		if(this.mask){
			var cover = YAHOO.util.Dom.getElementBy(function(el){return el.className=="yui-content";}, "div", "retailAnalyticsTabs");
			var xy = YAHOO.util.Dom.getXY(cover);
			var h = YAHOO.util.Dom.getRegion(cover).height;
			var w = YAHOO.util.Dom.getRegion(cover).width;
			this.mask.style.height = h + "px";
			this.mask.style.width = w + "px";
			YAHOO.util.Dom.setXY(this.mask, xy);
		}
	}, waitingPanel, true);
	waitingPanel.center = function(){
		var cover = YAHOO.util.Dom.getElementBy(function(el){return el.className=="yui-content";}, "div", "retailAnalyticsTabs");
		var xy = YAHOO.util.Dom.getXY(cover);
		var h = YAHOO.util.Dom.getRegion(cover).height;
		var w = YAHOO.util.Dom.getRegion(cover).width;
		var x = w/2 - this.element.offsetWidth/2 + xy[0];
		var y = h/2 - this.element.offsetHeight/2 + xy[1];
		this.element.style.left = x + "px";
		this.element.style.top = y + "px";
		this.syncPosition();
		this.cfg.refireEvent("iframe");		
	}
	waitingPanel.render(document.body);
}
var openIframe=true;
function openSensorData(){
	var iframe = document.getElementById("sensorDataIframe");
	if(openIframe){
		createWaitingPanel();
		waitingPanel.show();
		iframe.src ="<s:url value='retailAnalytics.action' includeParams='none' />?operation=retailSensorData&ignore="+new Date().getTime();
	}
	openIframe=false;
}

function openDashboard(){
	var iframe = document.getElementById("dashboardIframe");
	iframe && (iframe.src = "<s:property value="customerRetailDashboardUrl"/>&ignore="+new Date().getTime());
}

function syncCustomerInfoFromEuclid(){
	var url = "<s:url action='retailAnalytics' includeParams='none'/>" + "?operation=syncPresenceCustomerInfo&ignore=" + new Date().getTime();
    ajaxRequest(null, url, syncPresenceCustomerInfoDone);
}

function syncPresenceCustomerInfoDone(o){
	// do nothing...
}
</script>

<div id="content">
	<s:form action="retailAnalytics">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{customerRegistered == 2}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td style="padding-right: 10px">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding: 10px">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr><td><s:text name="presence.retail.analytics.config.desc1" /></td></tr>
									<tr><td height="10"></td></tr>
									<tr><td><s:text name="presence.retail.analytics.config.desc2" /></td></tr>
									<tr><td height="15"></td></tr>
									<tr>
										<td>
											<input type="button" name="config" value="<s:text name="button.configure" />" class="button"
												onClick="showRegisterPanel();" <s:property value="writeDisabled" />>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
			</s:if><s:elseif test="%{customerRegistered == 1}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
				<%-- <s:if test="%{customerLiteVersion}"> --%>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="config" value="<s:text name="button.configure" />"
							class="button" onClick="submitAction('configure');"
							<s:property value="writeDisabled" />></td>
						<%-- <td class="link">&nbsp;&nbsp;<a target="_blank" href="<s:property value="customerRetailPermiumSignUrl"/>"><s:text name="presence.retail.analytics.config.sign" /></a></td> --%>
					</tr>
				</table>
				<%-- </s:if> --%>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<div id="retailAnalyticsTabs" class="yui-navset">
					    <ul class="yui-nav">
					        <li class="selected"><a href="#tb1"><em><s:text name="presence.retail.analytics.tab.dashboard" /></em></a></li>
					        <li><a href="#tb2"><em><s:text name="presence.retail.analytics.tab.monitor" /></em></a></li>
					    </ul>            
					    <div class="yui-content">
					        <div id="tb1"><iframe id="dashboardIframe" frameborder="0" width="100%" height="600px" src=""></iframe></div>
					        <div id="tb2"><iframe id="sensorDataIframe" frameborder="0" width="100%" height="600px" src=""></iframe></div>
					    </div>
					</div>
<!-- 					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td height="10"></td>
						</tr>
					</table> -->
				</td>
			</tr>
			</s:elseif><s:else>
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td style="padding-right: 10px">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td style="padding: 10px">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr><td class="noteError">
									<s:text name="error.presence.service.available" />&nbsp;&nbsp;
									<input type="button" name="retry" value="<s:text name="button.retry" />" class="button"
												onClick="location.reload();">
									</td></tr>
									<tr><td height="10"></td></tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
			</s:else>
		</table>
	</s:form>
</div>
<div id="euclidRegisterPanel" style="display: none;">
    <div class="hd"><s:text name="presence.retail.analytics.license.title" /></div>
    <div class="bd">
    	<table class="settingBox" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<div style="height: 480px">
					<iframe id="registerIframe" frameborder="0" width="100%" height="100%" src=""></iframe>
					</div>
				</td>
			</tr>
    	</table>
    </div>
    <div class="ft">
    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    		<tr>
    			<td align="center">
    				<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td><input type="button" name="ignore" value="<s:text name="button.accept" />"
								class="button" onClick="acceptRegister(this);"
								<s:property value="writeDisabled" />></td>
							<td><input type="button" name="ignore" value="<s:text name="button.decline" />"
								class="button" onClick="hideRegisterPanel();"
								<s:property value="writeDisabled" />></td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
    				</table>
    			</td>
    		</tr>  	
    	</table>
    </div>
</div>

<s:if test="%{customerRegistered == 1}">
<tiles:insertDefinition name="tabView" />
<script type="text/javascript">
var tabView = new YAHOO.widget.TabView("retailAnalyticsTabs");
tabView.on("activeIndexChange", function(event){
	if(event.newValue == 1){
		openSensorData();
	}
});
</script>
<div id="trackPanel" style="display: none;">
    <div class="hd"></div>
    <div class="bd">
    	<table class="settingBox" width="100%" border="0" cellspacing="0" cellpadding="0">
    		<tr>
    			<td>
    				<table border="0" cellspacing="0" cellpadding="0">
    					<tr>
    						<td class="labelT1" colspan="2"><strong><s:text name="presence.retail.analytics.monitor.track.summary" /></strong></td>
    					</tr>
    					<tr>
    						<td class="labelT1" width="150px"><s:text name="presence.retail.analytics.monitor.track.status" /></td>
    						<td id="sensorStatusTd"> </td>
    					</tr>
    					<tr>
    						<td class="labelT1"><s:text name="presence.retail.analytics.monitor.track.last.conn.time" /></td>
    						<td id="sensorConnTd"> </td>
    					</tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding: 2px 10px;">
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">
    					<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
    					</tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">
    				    <tr>
    				        <td class="labelT1" colspan="2"><strong><s:text name="presence.retail.analytics.monitor.track.client.summary" /></strong></td>
    				     </tr>
    					<tr><td class="labelT1"><strong id="trackClientCount"></strong>&nbsp;<s:text name="presence.retail.analytics.monitor.track.client.count" /></td></tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">
    					<tr>
    						<td class="labelT1"><input size="24" id="trackClientFilter" name="trackClientFilter" placeholder="search" /></td>
    						<td>&nbsp;</td>
    						<td width="160px" id="tip_label" class="tip_label"><s:text name="presence.retail.analytics.monitor.track.refresh.tip" /></td>
    						<td width="25px"><a id="refreshHolder" onclick="updateRefreshHolder();" href="javascript:;"></a></td>
    					</tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding: 10px;">
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table width="100%" class="table" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<th width="160px"><s:text name="presence.retail.analytics.monitor.track.table.client.mac" /></th>
									<th><s:text name="presence.retail.analytics.monitor.track.table.client.first.time" /></th>
								</tr>
								</table>
							</td>
						</tr>
    					<tr>
    						<td>
    							<div style="width: 100%; height: 200px; overflow-y: auto;" id="trackClientsTmplWrapper"></div>
    						</td>
    					</tr>
    				</table>
    			</td>
    		</tr>
			<tr>
				<td height="10"></td>
			</tr>
    		<tr>
    			<td style="padding-left: 10px;">
    				<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="<s:text name="button.close" />"
								class="button" onClick="hideTrackPanel();"
								<s:property value="writeDisabled" />></td>
						</tr>
    				</table>
    			</td>
    		</tr>
			<tr>
				<td height="10"></td>
			</tr>
    	</table>
    </div>
</div>
<tiles:insertDefinition name="retailDecal" />
</s:if>