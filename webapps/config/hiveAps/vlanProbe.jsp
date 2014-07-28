<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<!--CSS file (default YUI Sam Skin) -->
<link type="text/css" rel="stylesheet"
	href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<!-- Dependencies -->
<script type="text/javascript"
	src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>
<!-- Source files -->
<script type="text/javascript"
	src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>

<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
	white-space: nowrap;
}

.yui-skin-sam .yui-dt-scrollable .yui-dt-hd,.yui-skin-sam .yui-dt-scrollable .yui-dt-bd{
	border: medium none;
}
</style>

<style>
<!--
html {
	overflow: hidden;
}
.required {
	color: #FF0000;
}

#results{
	padding: 5px 0 0 0;
}

fieldset {
	margin: 0;
}
-->
</style>

<script type="text/javascript">
<!--
var basic;
var results = [];
var formName = "vlanProbe";
var pollTimeoutId;
var pollCount = 0;

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	initDataTable(180, 460);
	<s:if test="%{isEnterFromTool}">
		//add event listener to handle when page unloaded!
		YAHOO.util.Event.addListener(window, "beforeunload", onHidePage);
		createWaitingPanel();
	</s:if>
	<s:else>
		//save this reference on the parent
		top.vlanProbeIframeWindow = window;
		if(hostName){
			top.updateVlanProbePanelTitle(hostName);
		}
	</s:else>
	Get(formName + "_vlanProbeFrom").focus();
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

var initDataTable = function(h, w){
    basic = function() {
        var myColumnDefs = [
            {key:"<s:text name='monitor.hiveAp.vlan.probe.column.vlanId' />", sortable:true, formatter:YAHOO.widget.DataTable.formatNumber, resizeable:false, width: 130},
            {key:"<s:text name='monitor.hiveAp.vlan.probe.column.available' />", sortable:true, resizeable:false, width: 100},
            {key:"<s:text name='monitor.hiveAp.vlan.probe.column.subnet' />", sortable:true, resizeable:false, width: 150}
        ];

        var myDataSource = new YAHOO.util.DataSource(results);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["<s:text name='monitor.hiveAp.vlan.probe.column.vlanId' />",
            		 "<s:text name='monitor.hiveAp.vlan.probe.column.available' />",
            		 "<s:text name='monitor.hiveAp.vlan.probe.column.subnet' />"]
        };
        var myDataTable = new YAHOO.widget.DataTable("results",
                myColumnDefs, myDataSource, {scrollable: true, width: w+"px", height: h+"px"});
		myDataTable.subscribe("rowMouseoverEvent", myDataTable.onEventHighlightRow);
		myDataTable.subscribe("rowMouseoutEvent", myDataTable.onEventUnhighlightRow);
		//myDataTable.subscribe("rowClickEvent", myDataTable.onEventSelectRow);
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
}

function onHidePage(){
	clearTimeout(pollTimeoutId);
	try{
		var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = "closeVlanGroup";
		ajaxRequest(formName, url,function(){/*dummy*/});
	}catch(e){}
}

var pollVlanProbe = function(){
	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
	document.forms[formName].operation.value = "pollVlanProbe";
	ajaxRequest(formName, url,updateVlanProbe);
}

var startPollTimer = function(){
	var interval = 5;        // seconds
	var duration = (vlanProbeTo-vlanProbeFrom+1)*vlanProbeRetries*vlanProbeTimeout;
	//var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var times = 5; // just for ensuring that get all the data from backstage
	var total = duration / interval+ times;
	if (pollCount++ < total) {
		pollTimeoutId = setTimeout("pollVlanProbe()", interval * 1000);  // seconds
	}
}

var updateVlanProbe = function(o){
	eval("var result = " + o.responseText);

	var states = result.states;
	var probes = result.vlanProbes;
	//update button state
	updateButtonStates(states);
	//update vlan probe
	updateVlanProbes(probes);
	if(states.stop_suc || states.aborted || states.finished){
		if(pollTimeoutId){clearTimeout(pollTimeoutId);}
	}else{
		startPollTimer();
	}
}

var requestActionProcess = function(o){
	if(null != top.waitingPanel){
		top.waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	var states = result.states;
	//update button state
	updateButtonStates(states);
	//start timer when it's start action
	if(states.starting || states.stopping){
		if(pollTimeoutId){clearTimeout(pollTimeoutId);}
		startPollTimer();
	}
	if(states.aborted || states.start_failed || states.stop_failed || states.finished){
		if(pollTimeoutId){clearTimeout(pollTimeoutId);}
	}
}

var updateVlanProbes = function(probes){
	if(probes){
		var previousTop = basic.oDT.getBdContainerEl().scrollTop;
		basic.oDT.getRecordSet().replaceRecords(probes);
		basic.oDT.render();
		basic.oDT.getBdContainerEl().scrollTop = previousTop;
	}
}

var previousCookieId = 0;
var updateButtonStates = function(states){
	var msg = states.msg;
	if(states.start_failed){
		if(Get("start").disabled){Get("start").disabled = false;};
		if(Get("selectedHiveAp") && Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteError";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
		//once start failed, replace cookieId with the previous one.
		if(states.start_failed){
			Get("cookieId").value = previousCookieId;
		}
	}else if(states.starting){
		if(!Get("start").disabled){Get("start").disabled = true;};
		if(Get("selectedHiveAp") && !Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = true;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
		if(states.cookieId && Get("cookieId").value != states.cookieId){
			previousCookieId = Get("cookieId").value;//store previous cookieId;
			Get("cookieId").value = states.cookieId;
		}
	}else if(states.start_suc){
		if(!Get("start").disabled){Get("start").disabled = true;};
		if(Get("selectedHiveAp") && !Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = true;};
		if(Get("stop").disabled){Get("stop").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}else if(states.stop_failed){
		if(Get("stop").disabled){Get("stop").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteError";
			Get("noteDiv").style.display="";
		}
	}else if(states.stopping){
		if(!Get("stop").disabled){Get("stop").disabled = true;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}else if(states.stop_suc){
		if(Get("start").disabled){Get("start").disabled = false;};
		if(Get("selectedHiveAp") && Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = false;};
		if(Get("stop").disabled){Get("stop").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}else if(states.aborted){
		if(Get("start").disabled){Get("start").disabled = false;};
		if(Get("selectedHiveAp") && Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = false;};
		if(Get("stop").disabled){Get("stop").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteError";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}else if(states.finished){
		if(Get("start").disabled){Get("start").disabled = false;};
		if(Get("selectedHiveAp") && Get("selectedHiveAp").disabled){Get("selectedHiveAp").disabled = false;};
		if(Get("stop").disabled){Get("stop").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}
}

var vlanProbeTo = 0;
var vlanProbeFrom = 0;
var vlanProbeRetries = 0; 
var vlanProbeTimeout = 0;
function requestAction(action){
	if(validate(action)){
		<s:if test="%{isEnterFromTool}">
		document.forms[formName].hiveApId.value = document.getElementById("selectedHiveAp").value;
		</s:if>
		var url = "<s:url action="hiveApToolkit" includeParams="none"></s:url>" + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = action;
		//alert(url);
		if("startVlanProbe" == action){
			basic.oDT.getRecordSet().replaceRecords([]);
			basic.oDT.render();
		}
		Get("noteDiv").style.display="none";
		vlanProbeTo = Get(formName+"_vlanProbeTo").value;
		vlanProbeFrom = Get(formName + "_vlanProbeFrom").value;
		vlanProbeRetries = Get(formName + "_vlanProbeRetries").value; 
		vlanProbeTimeout = Get(formName + "_vlanProbeTimeout").value;
		pollCount=0;
		ajaxRequest(formName, url, requestActionProcess);
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
	}
}

function validate(action){
	if("startVlanProbe" == action){
    	var el1 = Get(formName + "_vlanProbeFrom");
    	if(el1.value.length == 0){
			hm.util.reportFieldError(el1, '<s:text name="error.requiredField"><s:param><s:text name="monitor.hiveAp.vlan.probe.vlanRange" /></s:param></s:text>');
			el1.focus();
			return false;
	 	}
		var message = hm.util.validateIntegerRange(el1.value, '<s:text name="monitor.hiveAp.vlan.probe.vlanRange" />',
		                                           <s:property value="1" />,
		                                           <s:property value="4094" />);
		if (message != null) {
			hm.util.reportFieldError(el1, message);
			el1.focus();
			return false;
		}
		var el2 = Get(formName + "_vlanProbeTo");
    	if(el2.value.length == 0){
			hm.util.reportFieldError(el1, '<s:text name="error.requiredField"><s:param><s:text name="monitor.hiveAp.vlan.probe.vlanRange" /></s:param></s:text>');
			el2.focus();
			return false;
	 	}
		var message = hm.util.validateIntegerRange(el2.value, '<s:text name="monitor.hiveAp.vlan.probe.vlanRange" />',
		                                           <s:property value="1" />,
		                                           <s:property value="4094" />);
		if (message != null) {
			hm.util.reportFieldError(el1, message);
			el2.focus();
			return false;
		}
		if(parseInt(el2.value) < parseInt(el1.value)){
			hm.util.reportFieldError(el1, '<s:text name="error.notLargerThan"><s:param><s:text name="monitor.hiveAp.vlan.probe.vlanRange" /></s:param><s:param><s:text name="monitor.hiveAp.vlan.probe.vlanRange.to" /></s:param></s:text>');
			return false;
		}
    	var el3 = Get(formName + "_vlanProbeRetries");
    	if(el3.value.length > 0){
			var message = hm.util.validateIntegerRange(el3.value, '<s:text name="monitor.hiveAp.vlan.probe.retries" />',
			                                           <s:property value="1" />,
			                                           <s:property value="10" />);
			if (message != null) {
				hm.util.reportFieldError(el3, message);
				el3.focus();
				return false;
			}
    	}
    	var el4 = Get(formName + "_vlanProbeTimeout");
    	if(el4.value.length > 0){
			var message = hm.util.validateIntegerRange(el4.value, '<s:text name="monitor.hiveAp.vlan.probe.timeout" />',
			                                           <s:property value="1" />,
			                                           <s:property value="60" />);
			if (message != null) {
				hm.util.reportFieldError(el4, message);
				el4.focus();
				return false;
			}
    	}
   	}
   	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
//-->
</script>
<div><s:form action="hiveApToolkit" id="vlanProbe" name="vlanProbe">
	<s:hidden name="hiveApId" />
	<s:hidden name="cookieId" id="cookieId"/>
	<s:hidden name="debugGroupId" value="%{debugGroupId}" />
	<s:if test="%{!isEnterFromTool}">
		<s:hidden name="operation" />
	</s:if>
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<s:if test="%{isEnterFromTool}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		</s:if>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input id="start" type="button" name="ignore" value="Start"
						class="button" onClick="requestAction('startVlanProbe');"
						<s:property value="writeDisabled" />></td>
					<td><input id="stop" type="button" name="ignore" value="Stop"
						class="button" onClick="requestAction('stopVlanProbe');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="10">
				<div id="noteDiv" style="display:none">
				<table width="450px" border="0" cellspacing="0" cellpadding="0"
					class="note">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td id="noteId"></td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="10">
			<s:if test="%{isEnterFromTool}">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="550px" style="padding: 8px;">
			</s:if>
			<tr>
				<td>
				<table class="embeddBox" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td height="5px"></td>
					</tr>
					<s:if test="%{isEnterFromTool}">
					<tr>
						<td class="labelT1"><s:text name="hiveAp.tag" /><span
							class="required"> *</span></td>
						<td><s:select id="selectedHiveAp" list="hiveAps" listKey="id" listValue="value"
								cssStyle="width:142px;"/></td>
					</tr>
					</s:if>
					<tr>
						<td class="labelT1"><s:text
							name="monitor.hiveAp.vlan.probe.vlanRange" /><span
							class="required"> *</span></td>
						<td><s:textfield name="vlanProbeFrom" size="3" maxlength="4"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
							name="monitor.hiveAp.vlan.probe.vlanRange.to" /> <s:textfield
							name="vlanProbeTo" size="3" maxlength="4"
							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
							name="monitor.hiveAp.vlan.probe.vlanRange.note" /></td>
					</tr>
					<tr>
						<td class="labelT1"><s:text
							name="monitor.hiveAp.vlan.probe.retries" /></td>
						<td><s:textfield name="vlanProbeRetries" maxlength="2"
							size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
						<s:text name="monitor.hiveAp.vlan.probe.retries.note"></s:text></td>
					</tr>
					<tr>
						<td class="labelT1"><s:text
							name="monitor.hiveAp.vlan.probe.timeout" /></td>
						<td><s:textfield name="vlanProbeTimeout" maxlength="2"
							size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
						<s:text name="monitor.hiveAp.vlan.probe.timeout.note"></s:text></td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td>
				<fieldset>
					<legend><s:text name="monitor.hiveAp.vlan.probe.result.tag" /></legend>
					<div id="results"></div>
				</fieldset>
				</td>
			</tr>
			<s:if test="%{isEnterFromTool}">
			</table>
			</s:if>
			</td>
		</tr>
	</table>
</s:form></div>