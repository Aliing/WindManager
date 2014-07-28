<%@ taglib prefix="s" uri="/struts-tags"%>

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
var formName = "pathProbe";
var pollTimeoutId;
var pollCount = 0;

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	initDataTable(180, 460);
	//save this reference on the parent
	top.pathProbeIframeWindow = window;
	if(hostName){
		top.updatePathProbePanelTitle(hostName);
	}
	Get(formName + "_pathProbeDest").focus();
}

var initDataTable = function(h, w){
    basic = function() {
        var myColumnDefs = [
            {key:"<s:text name='monitor.hiveAp.path.probe.column.hopCount' />", sortable:true, formatter:YAHOO.widget.DataTable.formatNumber, resizeable:false, width: 140},
            {key:"<s:text name='monitor.hiveAp.path.probe.column.nodeMac' />", sortable:true, resizeable:false, width: 100},
            {key:"<s:text name='monitor.hiveAp.path.probe.column.ipAddress' />", sortable:true, resizeable:false, width: 150}
        ];

        var myDataSource = new YAHOO.util.DataSource(results);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["<s:text name='monitor.hiveAp.path.probe.column.hopCount' />",
            		 "<s:text name='monitor.hiveAp.path.probe.column.nodeMac' />",
            		 "<s:text name='monitor.hiveAp.path.probe.column.ipAddress' />"]
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
		document.forms[formName].operation.value = "closePathGroup";
		ajaxRequest(formName, url,function(){/*dummy*/});
	}catch(e){}
}

var pollPathProbe = function(){
	var url = '<s:url action="hiveApToolkit" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
	document.forms[formName].operation.value = "pollPathProbe";
	ajaxRequest(formName, url,updatePathProbe);
}

var startPollTimer = function(){
	var interval = 5;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (pollCount++ < total) {
		pollTimeoutId = setTimeout("pollPathProbe()", interval * 1000);  // seconds
	}
}

var updatePathProbe = function(o){
	eval("var result = " + o.responseText);
	
	var states = result.states;
	var probes = result.pathProbes;
	//update button state
	updateButtonStates(states);
	//update path probe
	updatePathProbes(probes);
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

var updatePathProbes = function(probes){
	if(probes){
		basic.oDT.getRecordSet().replaceRecords(probes);
		basic.oDT.render();
	}
}

var updateButtonStates = function(states){
	var msg = states.msg;
	if(states.start_failed){
		if(Get("start").disabled){Get("start").disabled = false;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteError";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
	}else if(states.starting){
		if(!Get("start").disabled){Get("start").disabled = true;};
		if(msg){
			Get("noteId").innerHTML = msg;
			Get("noteId").className="noteInfo";
			Get("noteDiv").style.display="";
		}else{
			Get("noteDiv").style.display="none";
		}
		if(states.cookieId){Get("cookieId").value = states.cookieId;};
	}else if(states.start_suc){
		if(Get("start").disabled){Get("start").disabled = false;};
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



function requestAction(action){
	if(validate(action)){
		var url = "<s:url action="hiveApToolkit" includeParams="none"></s:url>" + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = action;
		//alert(url);
		if("startPathProbe" == action){
			basic.oDT.getRecordSet().replaceRecords([]);
			basic.oDT.render();
		}
		Get("noteDiv").style.display="none";
		ajaxRequest(formName, url, requestActionProcess);
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
	}
}

function validate(action){
	if("startPathProbe" == action){
    	var el1 = Get(formName + "_pathProbeDest");
    	var message = hm.util.validateName(el1.value, '<s:text name="monitor.hiveAp.path.probe.destination" />');
    	if (message != null) {
    		hm.util.reportFieldError(el1, message);
        	el1.focus();
        	return false;
    	}
		var el2 = Get(formName + "_pathProbePackageSize");
		if(el2.value.length > 0){
			var message = hm.util.validateIntegerRange(el2.value, '<s:text name="monitor.hiveAp.path.probe.packageSize" />',
			                                           <s:property value="256" />,
			                                           <s:property value="1400" />);
			if (message != null) {
				hm.util.reportFieldError(el1, message);
				el2.focus();
				return false;
			}
		}
		var el3 = Get(formName + "_pathProbeSource");
		if(!checkMacAddress(el3, '<s:text name="monitor.hiveAp.path.probe.source" />')){
			return false;
		}
    	var el4 = Get(formName + "_pathProbeTimeout");
    	if(el4.value.length > 0){
			var message = hm.util.validateIntegerRange(el4.value, '<s:text name="monitor.hiveAp.path.probe.responseTimeout" />',
			                                           <s:property value="1" />,
			                                           <s:property value="30" />);
			if (message != null) {
				hm.util.reportFieldError(el4, message);
				el4.focus();
				return false;
			}
    	}
    	var el5 = Get(formName + "_pathProbeTtl");
    	if(el5.value.length > 0){
			var message = hm.util.validateIntegerRange(el5.value, '<s:text name="monitor.hiveAp.path.probe.ttl" />',
			                                           <s:property value="1" />,
			                                           <s:property value="255" />);
			if (message != null) {
				hm.util.reportFieldError(el5, message);
				el5.focus();
				return false;
			}
    	}
    	var el6 = Get(formName + "_pathProbeRequestNum");
    	if(el6.value.length > 0){
			var message = hm.util.validateIntegerRange(el6.value, '<s:text name="monitor.hiveAp.path.probe.requestNum" />',
			                                           <s:property value="1" />,
			                                           <s:property value="64" />);
			if (message != null) {
				hm.util.reportFieldError(el6, message);
				el6.focus();
				return false;
			}
    	}
   	}
   	return true;
}
function checkMacAddress(el, label){
	if(el.value.length == 0){
        hm.util.reportFieldError(el, '<s:text name="error.requiredField"><s:param>'+label+'</s:param></s:text>');
        el.focus();
        return false;
	} else if (!hm.util.validateMacAddress(el.value, 12)) {
		hm.util.reportFieldError(el, '<s:text name="error.formatInvalid"><s:param>'+label+'</s:param></s:text>');
		el.focus();
		return false;
	}
	return true;
}
//-->
</script>
<div><s:form action="hiveApToolkit" id="pathProbe" name="pathProbe">
	<s:hidden name="hiveApId" />
	<s:hidden name="operation" />
	<s:hidden name="debugGroupId" value="%{debugGroupId}" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input id="start" type="button" name="ignore" value="Start"
						class="button" onClick="requestAction('startPathProbe');"
						<s:property value="writeDisabled" />></td>
					<td><input id="stop" type="button" name="ignore" value="Stop"
						class="button" onClick="requestAction('stopPathProbe');"
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
						<td id="noteId"></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="10">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0"
				width="100%">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.destination" /><span
						class="required"> *</span></td>
					<td><s:textfield name="pathProbeDest" size="16" maxlength="16"
						onkeypress="return hm.util.keyPressPermit(event,'name');" /> <s:text
						name="monitor.hiveAp.path.probe.destination.note" /></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.packageSize" /></td>
					<td><s:textfield name="pathProbePackageSize" maxlength="4"
						size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
					<s:text name="monitor.hiveAp.path.probe.packagesize.note"></s:text></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.source" /></td>
					<td><s:textfield name="pathProbeSource" maxlength="12"
						size="16" onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.responseTimeout" /></td>
					<td><s:textfield name="pathProbeTimeout" maxlength="2"
						size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
					<s:text name="monitor.hiveAp.path.probe.responseTimeout.note"></s:text></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.ttl" /></td>
					<td><s:textfield name="pathProbeTtl" maxlength="3"
						size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
					<s:text name="monitor.hiveAp.path.probe.ttl.note"></s:text></td>
				</tr>
				<tr>
					<td class="labelT1"><s:text
						name="monitor.hiveAp.path.probe.requestNum" /></td>
					<td><s:textfield name="pathProbeRequestNum" maxlength="2"
						size="16" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
					<s:text name="monitor.hiveAp.path.probe.requestNum.note"></s:text></td>
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
	</table>
	<fieldset>
		<legend><s:text name="monitor.hiveAp.path.probe.result.tag" /></legend>
		<div id="results"></div>
	</fieldset>
</s:form></div>