<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script
	src="<s:url value="/js/raphael210-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/hm.cs.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/hm.sa.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
function onLoadPage() {
	createCharts();
	window.setTimeout(pollHandler, 10000);
}
function pollHandler() {
	var url = '<s:url action="spectralAnalysis" includeParams="none"></s:url>' + "?operation=updateInterferenceData&currentApId="+'<s:property value="id"/>' + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessAA, timeout: 60000 }, null);
}
var detailsSuccessAA = function(o) {
	eval("var details = " + o.responseText);
	var s = details.s;
	//alert(s);
	Get("tableDiv").innerHTML=s;
	window.setTimeout(pollHandler, 10000);
};


var dash = null;
function onUnloadPage() {
	hm.cs.closeDash(dash);
	//alert(window.location.href);
}
var autoRefresh = false;
function onBeforeUnloadEvent(e){
	
	if (autoRefresh) {
		autoRefresh = false; // No user prompt
		return;
	}
	var trackTitle = '<s:property value="trackChangeTitle" />';
	var tips = "Leaving this page does not stop the spectrum analyzer, which can affect " + '<s:text name="report.summary.apInfo.apName"/>' + " performance."
				+"\n\n(To disable pop-up messages, change the Prompt Unsaved Changes setting on \"Home\">\""+trackTitle+"\")";
	hm.util.hide("processing");
	// for IE and FF
	e = e || window.event
	if(e){
		e.returnValue = tips;
	}
	// for Safari
	return tips;
}
var navTop = 80+4, navLeft = ${leftWidth+20+5};
function createCharts() {
	<s:if test="%{easyMode}">
		navTop++;
	</s:if>
	hm.cs.loadIcons("<s:url includeParams="none" value="/images" />");
	dash = hm.cs.createDash(<s:property value="aid" />, "content", navTop, navLeft, 130, dashToolbar, dashBottom, "<s:property value="tilesStatus" />");
	hm.sa.initFFTChart(dash, 0, <s:property value="fftBand" />, <s:property value="fftChannels" />,
								<s:property value="fftCenter" />, <s:property value="fftSpan" />,
								<s:property value="fftRefLevel" />, <s:property value="fftVertScale" />,
								<s:property value="fftSample" />);
	hm.sa.initDutyChart(dash, 1, <s:property value="dutyBand" />, <s:property value="dutyChannels" />,
								 <s:property value="dutyCenter" />, <s:property value="dutySpan" />,
								 <s:property value="dutyMin" />, <s:property value="dutyMax" />,
								 <s:property value="dutySample" />);
	hm.sa.initSweptFFTChart(dash, 2, <s:property value="sweptFftBand" />, <s:property value="sweptFftChannels" />,
								<s:property value="sweptFftCenter" />, <s:property value="sweptFftSpan" />,
								dashDataUrl()+"sweptFft");
	hm.sa.initSweptDutyChart(dash, 3, <s:property value="sweptDutyBand" />, <s:property value="sweptDutyChannels" />,
								<s:property value="sweptDutyCenter" />, <s:property value="sweptDutySpan" />,
								dashDataUrl()+"sweptDuty");							 
	hm.cs.startDash(dash, dashDataUrl()+"fft");
}
<%-- Global toolbar --%>
function dashToolbar(div, width, height) {
	var lbl2 = hm.cs.createLabel(7, 8, "Current AP: " + '<s:property value="currentAPHostName"/>', "titleLbl", "currentLabelId");
	div.appendChild(lbl2);
	var btSetting = createImageBt(hm.cs.settingsOff,hm.cs.settingsOn,4,28 + lbl2.offsetWidth,btSettingClick,"Settings", "btSetting");
	div.appendChild(btSetting);
	var lbl = hm.cs.createLabel(7, 28 + lbl2.offsetWidth + 20, "Settings", "titleLbl");
	div.appendChild(lbl);
	
	lbl = hm.cs.createLabel(7, 28 + lbl2.offsetWidth + 100, '<s:property value="currentParamsIf"/>',"titleLbl" );
	div.appendChild(lbl);
	
	lbl = hm.cs.createLabel(7, 28 + lbl2.offsetWidth + 220, '<s:property value="CurrentParamsChannels"/>',"titleLbl" );
	div.appendChild(lbl);

	lbl = hm.cs.createLabel(7, 28 + lbl2.offsetWidth + lbl.offsetWidth + 250, '<s:property value="currentParamsRemainTime"/>',"titleLbl", "remainTimeDiv" );
	div.appendChild(lbl);
	
	lbl = hm.cs.createLabel(6, width-90, "Stop", "titleLbl");
	div.appendChild(lbl);
	var btStop = createImageBt(hm.cs.stopOff,hm.cs.stopOn,4,width-60,btStopClick,"Stop");
	div.appendChild(btStop);
	lbl = hm.cs.createLabel(6, width-170, "Return", "titleLbl");
	div.appendChild(lbl);
	var btReturn = createImageBt(hm.cs.returnOff,hm.cs.returnOn,4,width-125,btReturnClick,"Return");
	div.appendChild(btReturn);

}

function resetRemainTimes(vals) {
	Get("remainTimeDiv").innerHTML="<label>" + vals + "</label>";
}

function btStopClick() {
	<s:if test="%{writeDisabled==''}">
		autoRefresh = true; 
		window.location.href = 'spectralAnalysis.action?operation=stop&currentApId='+'<s:property value="id"/>';
	</s:if>
}

function btReturnClick() {
	window.location.href = 'hiveAp.action?operation=managedHiveAps';
}

function btSettingClick() {
	<s:if test="%{writeDisabled==''}">
		openParamOverlay();
	</s:if>
}

function createImageBt(iconOff, iconOn, top, left, callback,title, ids) {
	var img = document.createElement("img");
	img.src = iconOff.src;
	img.style.cursor = "pointer";
	img.width = 20;
	img.height = 20;
	img.title = title;
	if (ids) {
		img.id=ids;
	}
	var over = function(e) {
		img.src = iconOn.src;
	};
	var out = function(e) {
		img.src = iconOff.src;
	};
	YAHOO.util.Event.addListener(img, "mouseover", over);
	YAHOO.util.Event.addListener(img, "mouseout", out);
	
	var zoom = function(e) {
		callback();
	};
	img.onclick = zoom;
	var div = document.createElement("div");
	div.style.position = "absolute";
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = "20px";
	div.style.height = "20px";
	div.appendChild(img);
	return div;
}

function dashBottom(div, width, height) {
	var div11 = document.createElement("div");
	div11.innerHTML=Get("interferenceDiv").innerHTML;
	
	div11.style.display="";
	div11.style.position = "absolute";
	div11.style.top = "7px";
	div11.style.left ="8px";
	div11.style.height=height-10 + 'px';
	div11.style.width=width-10 + 'px';
	div.appendChild(div11);
}

function dashDataUrl() {
	return "spectralAnalysis.action?aid="+dash.aid+"&operation=";
}
function settingsUrl(chart) {
	return "spectralAnalysis.action?ignore="+new Date().getTime()+"&aid="+chart.dash.aid+"&operation=";
}
function hideLeftMenu() {
	if (dash) {
		hm.cs.moveDash(dash, dash.top, 20, dash.bottom, 0);
	}
	if (YAHOO.env.ua.ie > 0) {
		autoRefresh = true; // To avoid user prompt, false onBeforeUnloadEvent trigger
	}
}
function showLeftMenu() {
	hm.cs.moveDash(dash, dash.top, navLeft, dash.bottom, 0);
	if (YAHOO.env.ua.ie > 0) {
		autoRefresh = true; // To avoid user prompt, false onBeforeUnloadEvent trigger
	}
}
</script>
<script type="text/javascript">
document.writeln('<style>html, body { overflow: hidden; }</style>');
</script>
<style>
.defaultLbl {
	font-size: 12px;
	white-space: nowrap;
}

.titleLbl {
	font-size: 12px;
	color: #003366;
	font-weight: bold;
	white-space: nowrap;
}

.markerLbl {
	font-size: 12px;
	color: #fff;
	white-space: nowrap;
}
.channelLbl {
	font-size: 12px;
	color: #000;
	font-weight: bold;
	white-space: nowrap;
}
</style>
<div id="content"></div>
<script>
var paramFormName = 'paramSetting';
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
	waitingPanel.setHeader("Updating parameters...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

YAHOO.util.Event.onDOMReady(init);
var paramOverlay = null;
function init() {
	var div = document.getElementById('paramPanel');
	paramOverlay = new YAHOO.widget.Panel(div, {
		width:"520px",
		visible:false,
		fixedcenter:false,
		draggable:false,
		modal:false,
		underlay:"none",
		close:false,
		constraintoviewport:true,
		zIndex:9999
		});
	paramOverlay.render(document.body);
	div.style.display = "";
	createWaitingPanel();
}
function openParamOverlay(){
	initialValues();
	if(null != paramOverlay){
		var x = YAHOO.util.Dom.getX(Get("currentLabelId"));
		var y = YAHOO.util.Dom.getY(Get("currentLabelId"));
		paramOverlay.cfg.setProperty("x",x-8);
		paramOverlay.cfg.setProperty("y",y +25);
		paramOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != paramOverlay){
		Get("errMsgTr").style.display="none";
		paramOverlay.cfg.setProperty('visible', false);
	}
}
function initialValues(){
	var ifVal = '<s:property value="runningApInfoIf"/>';
	document.getElementById(paramFormName+"_runInterface").value = ifVal;
	document.getElementById(paramFormName+"_runChannelWifi0").value = '<s:property value="runningApInfoCh0"/>';
	document.getElementById(paramFormName+"_runChannelWifi1").value = '<s:property value="runningApInfoCh1"/>';
	document.getElementById(paramFormName+"_runInterval").value = '<s:property value="runningApInfoInterval"/>';
	document.getElementById(paramFormName+"_runTime").value = '<s:property value="runningApInfoTime"/>';

	if (ifVal==1) {
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="none";
	} else if (ifVal==2){
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="";
	} else if (ifVal==3){
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="";
	} else {
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="none";
	}
}
function submitParamAction(operation) {
	if (validate(operation)) {
		var runAP='<s:property value="id"/>';
		var runInterface=document.getElementById(paramFormName + "_runInterface").value;
		var runChannelWifi0=document.getElementById(paramFormName + "_runChannelWifi0").value;
		var runChannelWifi1=document.getElementById(paramFormName + "_runChannelWifi1").value;
		var runInterval=document.getElementById(paramFormName + "_runInterval").value;
		var runTime=document.getElementById(paramFormName + "_runTime").value;
		var url = '<s:url action="spectralAnalysis" includeParams="none"></s:url>' + "?operation=updateSettingsParams&runAP="+runAP + "&runInterface="+runInterface +"&runChannelWifi0="+runChannelWifi0 + "&runChannelWifi1="+runChannelWifi1 +"&runInterval="+runInterval +"&runTime="+runTime + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessIf, timeout: 120000 }, null);
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
		autoRefresh=true;
		var locaHrefUrl = 'spectralAnalysis.action?operation=view&id='+'<s:property value="id"/>';
		if (dash) {
			locaHrefUrl+= '&tilesStatus='+hm.cs.getTilesStatus(dash);
		}
		window.location.href = locaHrefUrl;
	} else {
		if (err) {
			Get("errMsg").innerHTML =err;
			Get("errMsgTr").style.display="";
		}
	}

};

function validate(operation) {
	if (!checkInterface()){
		return false;
	}
	if (!checkChannelWifi0()){
		return false;
	}
	if (!checkChannelWifi1()){
		return false;
	}
	if (!checkInterval()){
		return false;
	}
	return true;
}

function checkInterface() {
	if (Get(paramFormName + "_runInterface").value==-1){
		hm.util.reportFieldError(Get(paramFormName + "_runInterface"), '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.interface"/></s:param></s:text>');
       	Get(paramFormName + "_runInterface").focus();
       	return false;
	}
	return true;
}

function checkChannelWifi0(){
	if (Get(paramFormName + "_runInterface").value==2) {
		return true;
	}
	var attriValue = Get(paramFormName + "_runChannelWifi0");
	if (attriValue.value.length == 0) {
        hm.util.reportFieldError(attriValue, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
        attriValue.focus();
        return false;
    } else {
	    var attrInThis = new Array();
		var attributes = attriValue.value.split(",");
		for (var i = 0; i < attributes.length; i++)
		{
			var str_attribute = attributes[i];
			if("" == str_attribute)
			{
				hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        	attriValue.focus();
	        	return false;
			}
	
			if(!isNaN(str_attribute))
			{
				// it is a number;
				var message = hm.util.validateIntegerRange(str_attribute, '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
	      		if (message != null) {
	            	hm.util.reportFieldError(attriValue, message);
	           		attriValue.focus();
	            	return false;
	      		}
	      		for(var j = 0; j < attrInThis.length; j++)
	      		{
				if (attrInThis[j] == str_attribute)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				}
				attrInThis.push(str_attribute);
			}
			else
			{
				// it is a range;
				var str_range = str_attribute.split("-");
				if (str_range.length != 2)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				else
				{
					if(!isNaN(str_range[0]) && !isNaN(str_range[1]))
					{
						var message1 = hm.util.validateIntegerRange(str_range[0], '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
						var message2 = hm.util.validateIntegerRange(str_range[1], '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
	      				if (message1 != null) {
	            			hm.util.reportFieldError(attriValue, message1);
	           				attriValue.focus();
	            			return false;
	      				}
	      				if (message2 != null) {
	            			hm.util.reportFieldError(attriValue, message2);
	           				attriValue.focus();
	            			return false;
	      				}
						if (parseInt(str_range[0]) >= parseInt(str_range[1]))
						{
							hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        				attriValue.focus();
	        				return false;	
						}
						for(var j = 0; j < attrInThis.length; j++)
	      				{
							if (attrInThis[j] == str_range)
							{
								hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        					attriValue.focus();
	        					return false;
							}
						}
						attrInThis.push(str_range);			
					}
					else
					{
						hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        			attriValue.focus();
	        			return false;
					}
				}
			}
		}
    } 
	return true;
}
function checkChannelWifi1(){
	if (Get(paramFormName + "_runInterface").value==1) {
		return true;
	}
	var arrWifi1= [36,40,44,48,52,56,60,64,100,104,108,112,116,136,140,149,153,157,161,165];

	var attriValue = Get(paramFormName + "_runChannelWifi1");
	if (attriValue.value.length == 0) {
        hm.util.reportFieldError(attriValue, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
        attriValue.focus();
        return false;
    } else {
	    var attrInThis = new Array();
		var attributes = attriValue.value.split(",");
		for (var i = 0; i < attributes.length; i++)
		{
			var str_attribute = attributes[i];
			if("" == str_attribute)
			{
				hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        	attriValue.focus();
	        	return false;
			}
	
			if(!isNaN(str_attribute))
			{
				// it is a number;
				var message = hm.util.validateIntegerRange(str_attribute, '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
	      		if (message != null) {
	            	hm.util.reportFieldError(attriValue, message);
	           		attriValue.focus();
	            	return false;
	      		}
	      		
	      		// fnr add valid channel check
	      		var invalidChannel = false;
	      		for(var j=0; j<arrWifi1.length; j++) {
	      			if (arrWifi1[j]==str_attribute) {
						invalidChannel=true;
						break;
	      			}
	      		}
	      		if (invalidChannel==false) {
	      			hm.util.reportFieldError(attriValue, 'Channel number ' + str_attribute +  ' is invalid.');
		        	attriValue.focus();
		        	return false;
	      		}
	      		
	      		for(var j = 0; j < attrInThis.length; j++)
	      		{
				if (attrInThis[j] == str_attribute)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				}
				attrInThis.push(str_attribute);
			}
			else
			{
				// it is a range;
				var str_range = str_attribute.split("-");
				if (str_range.length != 2)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				else
				{
					if(!isNaN(str_range[0]) && !isNaN(str_range[1]))
					{
						var message1 = hm.util.validateIntegerRange(str_range[0], '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
						var message2 = hm.util.validateIntegerRange(str_range[1], '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
	      				if (message1 != null) {
	            			hm.util.reportFieldError(attriValue, message1);
	           				attriValue.focus();
	            			return false;
	      				}
	      				if (message2 != null) {
	            			hm.util.reportFieldError(attriValue, message2);
	           				attriValue.focus();
	            			return false;
	      				}
						if (parseInt(str_range[0]) >= parseInt(str_range[1]))
						{
							hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        				attriValue.focus();
	        				return false;	
						}
						
						// fnr add valid channel check
						var invalidChannel = false;
			      		for(var j=0; j<arrWifi1.length; j++) {
			      			if (arrWifi1[j]==str_range[0]) {
								invalidChannel=true;
								break;
			      			}
			      		}
			      		if (invalidChannel==false) {
			      			hm.util.reportFieldError(attriValue, 'Channel number ' + str_range[0] +  ' is invalid.');
				        	attriValue.focus();
				        	return false;
			      		}
			      		invalidChannel = false;
			      		for(var j=0; j<arrWifi1.length; j++) {
			      			if (arrWifi1[j]==str_range[1]) {
								invalidChannel=true;
								break;
			      			}
			      		}
			      		if (invalidChannel==false) {
			      			hm.util.reportFieldError(attriValue, 'Channel number ' + str_range[1] +  ' is invalid.');
				        	attriValue.focus();
				        	return false;
			      		}
						
						for(var j = 0; j < attrInThis.length; j++)
	      				{
							if (attrInThis[j] == str_range)
							{
								hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        					attriValue.focus();
	        					return false;
							}
						}
						attrInThis.push(str_range);			
					}
					else
					{
						hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        			attriValue.focus();
	        			return false;
					}
				}
			}
		}
    } 
	return true;
}
function checkInterval(){
	var vInterval = Get(paramFormName + "_runInterval");
    if (vInterval.value.length == 0) {
          hm.util.reportFieldError(vInterval, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.interval"/></s:param></s:text>');
          vInterval.focus();
          return false;
      }
      var message = hm.util.validateIntegerRange(vInterval.value, '<s:text name="hm.tool.snp.interval" />', 1, 30);
      if (message != null) {
          hm.util.reportFieldError(vInterval, message);
          vInterval.focus();
          return false;
      }
	return true;
}

function changeInterfaceType(value){
	if (value==1) {
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="none";
	} else if (value==2){
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="";
	} else if (value==3){
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="";
	} else {
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="none";
	}
	Get(paramFormName + "_runChannelWifi0").value="";
	Get(paramFormName + "_runChannelWifi1").value="";
}

function getTdWidth(vid) {
	alert(Get(vid).style.width);
	return Get(vid).style.width;
}
</script>
<div id="paramPanel" style="display: none;">
	<div class="bd">
		<s:form action="spectralAnalysis" id="paramSetting"
			name="paramSetting">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td style="padding: 6px 5px 5px 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr style="display: none;" id="errMsgTr">
											<td colspan="2"><font color="red"><b><span id="errMsg"></span></b></font></td>
										</tr>
										<tr>
											<td class="labelT1" width="150px"><s:text name="hm.tool.snp.interface"></s:text></td>
											<td><s:select name="runInterface"
													list="%{interfaceLst}" listKey="key"
													onchange="changeInterfaceType(this.options[this.selectedIndex].value);"
													listValue="value" cssStyle="width: 125px;" /></td>
										</tr>
										<tr id="wifi0Channel" style="display: <s:property value="%{showWifi0Channel}"/>">
											<td class="labelT1"><s:text name="hm.tool.snp.channel.wifi0"></s:text></td>
											<td><s:textfield name="runChannelWifi0" size="19"
												title="Separate entries by commas, and use a dash for ranges (e.g. 1, 6, 7-12)."
											onkeypress="return hm.util.keyPressPermit(event,'attribute');"/>
											&nbsp;<s:text name="hm.tool.snp.channel.wifi0.range"/></td>
										</tr>
										<tr id="wifi1Channel" style="display: <s:property value="%{showWifi1Channel}"/>">
											<td class="labelT1"><s:text name="hm.tool.snp.channel.wifi1"></s:text></td>
											<td><s:textfield name="runChannelWifi1" size="19"
											title="Separate entries by commas, and use a dash for ranges (e.g. 36, 40, 48-165)."
											onkeypress="return hm.util.keyPressPermit(event,'attribute');"/>
											&nbsp;<s:text name="hm.tool.snp.channel.wifi1.range"/></td>
										</tr>
										<tr>
											<td class="labelT1"><s:text name="hm.tool.snp.interval"></s:text></td>
											<td><s:textfield name="runInterval" size="19"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"
												maxlength="2" />&nbsp;<s:text
												name="hm.tool.snp.interval.range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><s:text name="hm.tool.snp.runTime"></s:text></td>
											<td><s:select name="runTime"
													list="%{timeLst}" listKey="id"
													listValue="value" cssStyle="width: 125px;" /></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-top: 8px;" colspan="2" align="center">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Update" id="update"
										class="button" onClick="submitParamAction('update');">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideOverlay();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div style="display: none;" id="interferenceDiv">

	<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
			<tr>
				<th width="90px"> <s:text name="hm.tool.snp.title.apName" /></th>
				<th width="110px"> <s:text name="hm.tool.snp.title.devicetype" /></th>
				<th width="105px"> <s:text name="hm.tool.snp.title.time" /></th>
				<th width="100px"> <s:text name="hm.tool.snp.title.channel" /></th>
				<th width="100px"> <s:text name="hm.tool.snp.title.centerFreq" /></th>
				<th width="120px"> <s:text name="hm.tool.snp.title.bandwidth" /></th>
			</tr>
			<tr>
			<td colspan="8">
	<div style="height: 92px; width: 100%; overflow-x: hidden;overflow-y: auto" id="tableDiv">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<s:if test="%{interferenceLst.size() == 0}">
				<ah:emptyList />
			</s:if>
			<s:else>
			<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<s:iterator value="interferenceLst" status="status">
				<s:if test="%{#status.even}">
					<s:set name="orowClass" value="%{'even'}" />
				</s:if>
				<s:else>
					<s:set name="orowClass" value="%{'odd'}" />
				</s:else>
				<tr class="<s:property value="%{#orowClass}"/>">
					<td class="list" width="100px"><s:property value="apName" /></td>
				 	<td class="list" width="120px"><s:property value="deviceTypeString" /></td>
					<td class="list" width="120px"><s:property value="timeString" /></td>
					<td class="list" width="110px"><s:property value="channel" /></td>
					<td class="list" width="110px"><s:property value="centerFreq" /></td>
					<td class="list" width="110px"><s:property value="bandwidthValue" /></td>
				</tr>
			</s:iterator>
			</table>
			</td>
			</tr>
			</s:else>
		</table>
	</div>
	</td>
	</tr>
	</table>
</div>
