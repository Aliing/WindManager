<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.wlan.RadioProfile"%>
<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0 4px 6px 4px;
}
-->
td.labelT2 {
	padding: 5px 0px 5px 12px;
	align: left;
}
</style>
<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'radioProfile';
var MODE_A = <%=RadioProfile.RADIO_PROFILE_MODE_A%>;
var MODE_NA = <%=RadioProfile.RADIO_PROFILE_MODE_NA%>;
var MODE_NG = <%=RadioProfile.RADIO_PROFILE_MODE_NG%>;
var MODE_BG = <%=RadioProfile.RADIO_PROFILE_MODE_BG%>;
var PREAMBLE_SHORT = <%=RadioProfile.RADIO_PROFILE_PREAMBLE_SHORT%>;
var THRESHOLD_OFF = <%=RadioProfile.RADIO_ROAMING_THRESHOLD_OFF%>;
var CHANNEL_WIDTH_20 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20%>;
var CHANNEL_WIDTH_40A = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A%>;
var CHANNEL_REGION_USA = <%=RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US%>;
var CHANNEL_MODEL_3 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3%>;
var CHANNEL_MODEL_4 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_4%>;
//Added for Casablanca
var MODE_PREFER5G = <%=RadioProfile.BAND_STEERING_MODE_PREFER5G%>;
var MODE_BALANCEBAND = <%=RadioProfile.BAND_STEERING_MODE_BALANCEBAND%>;
var MODE_AIRETIMEBASED = <%=RadioProfile.LOAD_BALANCE_MODE_AIRTIME_BASED%>;
//Added for 11ac
var MODE_AC = <%=RadioProfile.RADIO_PROFILE_MODE_AC%>;
var CHANNEL_WIDTH_80 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80%>;
var CHANNEL_WIDTH_40 = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40%>;
var CHANNEL_WIDTH_40B = <%=RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B%>;
function onLoadPage() {
	<s:if test="%{dataSource.id == null}">
		document.getElementById(formName + "_dataSource_radioName").focus();
//		var mode = document.getElementById(formName + "_dataSource_radioMode").value;
//		if (MODE_NA == mode) {
//			document.getElementById(formName + "_dataSource_channelWidth").value=CHANNEL_WIDTH_40A;
//			changeChannel(CHANNEL_WIDTH_40A);
//		} else {
//			document.getElementById(formName + "_dataSource_channelWidth").value=CHANNEL_WIDTH_20;
//			changeChannel(CHANNEL_WIDTH_20);
//		}
	</s:if>
	<s:if test="%{jsonMode}">
		<s:if test="%{dataSource.id != null && null != operation && ('create' == operation || 'update' == operation)}">
		 	<s:if test="%{'update' == operation && dataSource.id != id}">
   				return;
			</s:if>
			<s:else>
				top.closeIFrameDialog();
				top.updateRadioProfiles('<s:property value="dataSource.id"/>','<s:property value="dataSource.radioMode"/>');
			</s:else>
		</s:if>
		<s:else>
			top.changeIFrameDialog(910, 700);
		</s:else>
	</s:if>
}

function submitAction(operation) {
	<s:if test="%{jsonMode}">
		if ('cancel' == operation) {
			top.closeIFrameDialog();	
		}
	</s:if>
	
	if (validate(operation)) {
		showProcessing();
		//enable all disabled checkboxes, so that action can set correct value of the checkbox
		var highDensityEnable = document.getElementById(formName + "_dataSource_enableHighDensity").checked;
		if(!highDensityEnable){
			document.getElementById(formName + "_dataSource_enableBroadcastProbe").disabled = false;
			document.getElementById(formName + "_dataSource_enableContinuousProbe").disabled = false;
		}
		//save style values
		Get(formName + "_dataSource_channelPowerStyle").value = Get("channelPower").style.display;
		Get(formName + "_dataSource_radioSettingStyle").value = Get("radioSetting").style.display;
		Get(formName + "_dataSource_wmmQosStyle").value = Get("wmmQos").style.display;
		Get(formName + "_dataSource_chainStyle").value = Get("chain").style.display;
		Get(formName + "_dataSource_interferenceStyle").value = Get("interference").style.display;
		// Get(formName + "_dataSource_loadBalanceStyle").value = Get("loadBalance").style.display;
		Get(formName + "_dataSource_backhaulStyle").value = Get("backhaul").style.display;
		Get(formName + "_dataSource_clientStyle").value = Get("clientSel").style.display;
		Get(formName + "_dataSource_slaStyle").value = Get("slaSel").style.display;
		Get(formName + "_dataSource_highDensityStyle").value = Get("highDensity").style.display;
		Get(formName + "_dataSource_wipsServerStyle").value = Get("wipsSel").style.display;
		Get(formName + "_dataSource_presenceServerStyle").value = Get("presenceSel").style.display;
		Get(formName + "_dataSource_sensorScanStyle").value = Get("sensorSel").style.display;
		
		hm.options.selectAllOptions('macOrOuis');
		
		if(operation=="newMacOrOui" || operation=="editMacOrOui") {
			<s:if test="%{jsonMode}">
				document.forms[formName].parentIframeOpenFlg.value = true;
			</s:if>
		}
		
		<s:if test="%{!jsonMode}">
			document.forms[formName].operation.value = operation;
	    	document.forms[formName].submit();
		</s:if>
		<s:else>
			if ('cancel' == operation) {
				top.closeIFrameDialog();	
			} else {
				if ('create' == operation) {
					<s:if test="%{dataSource.id != null}">
						operation = 'update';
					</s:if>
				}
				document.forms[formName].operation.value = operation;
	   			document.forms[formName].submit();
			}
		</s:else>
	}
}

function validate(operation) {
	var beaconPeriod = document.getElementById(formName + "_dataSource_beaconPeriod");
    var maxClients = document.getElementById(formName + "_dataSource_maxClients");
    var backgroundScan = document.getElementById(formName + "_dataSource_backgroundScan");

    var enableChannelModel = document.getElementById(formName + "_dataSource_useDefaultChannelModel");
    var enableChannel = document.getElementById(formName + "_dataSource_enableChannel");
    var fromHour = document.getElementById(formName + "_dataSource_fromHour");
    var fromMinute = document.getElementById(formName + "_dataSource_fromMinute");
    var toHour = document.getElementById(formName + "_dataSource_toHour");
    var toMinute = document.getElementById(formName + "_dataSource_toMinute");
    var channelClient = document.getElementById(formName + "_dataSource_channelClient");
    var enablePower = document.getElementById(formName + "_dataSource_enablePower");
    var transmitPower = document.getElementById(formName + "_dataSource_transmitPower");
    var radioRange = document.getElementById(formName + "_dataSource_radioRange");

    var interval = document.getElementById(formName + "_dataSource_interval");
    // var minCount = document.getElementById(formName + "_dataSource_minCount");
   //  var threshold = document.getElementById(formName + "_dataSource_threshold");
    var triggerTime = document.getElementById(formName + "_dataSource_triggerTime");
    var holdTime = document.getElementById(formName + "_dataSource_holdTime");
    // var loadBalance = document.getElementById(formName + "_dataSource_loadBalance");
    var backhaulFailover = document.getElementById(formName + "_dataSource_backhaulFailover");
    var defaultCca = document.getElementById(formName + "_dataSource_defaultCcaValue");
    var maximumCca = document.getElementById(formName + "_dataSource_maxCcaValue");
    var crcThreshold = document.getElementById(formName + "_dataSource_crcThreshold");
    var channelThreshold = document.getElementById(formName + "_dataSource_channelThreshold");
    var averageInterval = document.getElementById(formName + "_dataSource_averageInterval");
    
    // for channel switch
    var iuThreshold = document.getElementById(formName + "_dataSource_iuThreshold");
    var crcChannelThr = document.getElementById(formName + "_dataSource_crcChannelThr");

    // for high density
    var highDensity = document.getElementById(formName + "_dataSource_enableHighDensity");
    var enableBandSteering = document.getElementById(formName + "_dataSource_enableBandSteering");
	var enableClientLoadBalance = document.getElementById(formName + "_dataSource_enableClientLoadBalance");
	var enableSafetyNet = document.getElementById(formName + "_dataSource_enableSafetyNet");
	var enableSuppress = document.getElementById(formName + "_dataSource_enableSuppress");
	var crcErrorLimit = document.getElementById(formName + "_dataSource_crcErrorLimit");
	var cuLimit = document.getElementById(formName + "_dataSource_cuLimit");
	var maxInterference = document.getElementById(formName + "_dataSource_maxInterference");
	var clientHoldTime = document.getElementById(formName + "_dataSource_clientHoldTime");
	var safetyNetTimeout = document.getElementById(formName + "_dataSource_safetyNetTimeout");
	var suppressThreshold = document.getElementById(formName + "_dataSource_suppressThreshold");
	var bandSteeringMode = document.getElementById(formName + "_dataSource_bandSteeringMode");
	var limitNumber = document.getElementById(formName + "_dataSource_limitNumber");
	var minimumRatio = document.getElementById(formName + "_dataSource_minimumRatio");
	var queryInterval = document.getElementById(formName + "_dataSource_queryInterval");
	
	var trapInterval = document.getElementById(formName + "_dataSource_trapInterval");
	var agingTime = document.getElementById(formName + "_dataSource_agingTime");
	var aggrInterval = document.getElementById(formName + "_dataSource_aggrInterval");
	
	var broadcastProbe = document.getElementById(formName + "_dataSource_enableBroadcastProbe");
	var supressBPRByOUI = document.getElementById(formName + "_dataSource_enableSupressBPRByOUI");
    
	if('<%=Navigation.L2_FEATURE_RADIO_PROFILE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		beaconPeriod.value=0;
    	maxClients.value=0;
    	backgroundScan.value=0;
    	interval.value=0;
   		// minCount.value=0;
    	// threshold.value=0;
    	triggerTime.value=0;
    	holdTime.value=0;
    	backhaulFailover.value=0;
    	channelClient.value=0;
    	transmitPower.value=0;
    	radioRange.value=0;
    	defaultCca.value=0;
    	maximumCca.value=0;
    	crcThreshold.value = channelThreshold.value = averageInterval.value = 0;
    	crcErrorLimit.value = cuLimit.value = maxInterference.value = 0;
    	clientHoldTime.value = safetyNetTimeout.value = suppressThreshold.value = queryInterval.value = 0;
    	iuThreshold.value = crcChannelThr.value = 0;
    	trapInterval.value=<%=RadioProfile.DEFAULT_PRESENCE_TIME%>;
    	agingTime.value=<%=RadioProfile.DEFAULT_PRESENCE_TIME%>;
    	aggrInterval.value=<%=RadioProfile.DEFAULT_PRESENCE_TIME%>;
		return true;
	}
	var name = document.getElementById(formName + "_dataSource_radioName");
	if (operation == 'create' + '<s:property value="lstForward"/>' || operation == 'create') {
		var message = hm.util.validateName(name.value, '<s:text name="config.radioProfile.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    }

    if(backgroundScan.checked)
    {
    	if (!checkInputRange(interval, '<s:text name="config.radioProfile.interval" />',
    									<s:property value="%{intervalRange.min()}" />,
    									<s:property value="%{intervalRange.max()}" />, showChannelPowerContent)) {
            return false;
    	}
    	var scanClient = document.getElementById(formName + "_dataSource_clientConnect");
		var scanPower = document.getElementById(formName + "_dataSource_powerSave");
		if (!scanClient.checked && scanPower.checked) {
			hm.util.reportFieldError(scanClient, '<s:text name="error.config.network.radio.profile.background.scan" />');
       		scanClient.focus();
       		return false;
		}
    }
    
    Get(formName + "_dataSource_enabledPresence").disabled = false;
    <s:if test="%{presenceEnable && presenceRegistered}">
	   	  if (!checkInputRange(trapInterval, '<s:text name="config.presence.server.interval" />',
	  			<s:property value="%{trapIntervalRange.min()}" />,
	  			<s:property value="%{trapIntervalRange.max()}" />, showPresenceSelContent)) {
	  		return false;
	  	}
	      if (!checkInputRange(agingTime, '<s:text name="config.presence.server.agingtime" />',
	  			<s:property value="%{agingTimeRange.min()}" />,
	  			<s:property value="%{agingTimeRange.max()}" />, showPresenceSelContent)) {
	  		return false;
	  	}
	      if (!checkInputRange(aggrInterval, '<s:text name="config.presence.server.aggrInterval" />',
	  			<s:property value="%{aggrIntervalRange.min()}" />,
	  			<s:property value="%{aggrIntervalRange.max()}" />, showPresenceSelContent)) {
	  		return false;
	  	}
    	
	  </s:if>
	  
	  var scanAllChannel=document.getElementById(formName + "_dataSource_scanAllChannel");
  	  var channelValue=document.getElementById(formName + "_dataSource_scanChannels");
  	  if(!scanAllChannel.checked){
  	       var message = hm.util.validateName(channelValue.value, '<s:text name="config.presence.server.channels" />');
  	       var mode=document.getElementById(formName+"_dataSource_radioMode").value;
  	       var scanChannelMin=1;
  	       var scanChannelMax=13;
  	       if(MODE_A == mode || MODE_NA == mode || MODE_AC == mode) {
  	    	 scanChannelMin=36;
  	     	 scanChannelMax=165;
  	  	    }
  	    	if(message==null){
  	    		var splitValues = channelValue.value.split(",");
  	   	    	for(var i=0;i<splitValues.length;i++){
  	   	    		var value=splitValues[i];
  	   	    		if(value.length==0 || value.charAt(0)==0){
  	   	    		   value=0;
  	   	    		}
  	   	    		value=parseInt(value);
  	   	    		if(value<scanChannelMin || value>scanChannelMax || isNaN(value)){
  	   	    		   message='<s:text name="error.formatInvalid"><s:param><s:text name="config.presence.server.channels"/></s:param></s:text>';
  	   	    		   break;
  	   	    		}
  	   	    	}
  	    	}
  	    	if (message != null) {
  	    		showSensorSelContent();
  	    		hm.util.reportFieldError(channelValue, message);
  	    		channelValue.focus();
  	        	return false;
  	    	}
  	    }else{
  	    	channelValue.disabled=true;
  	    }
  		var dellTime=document.getElementById(formName + "_dataSource_dellTime"); 
  		if (!checkInputRange(dellTime, '<s:text name="config.presence.server.dwell.time" />',10,30000, showSensorSelContent)) {
  	      return false;
  	   }
  		
    // b/g or ng channel check
    if (document.getElementById("channelModelCheck").style.display == "") {
    	if (!enableChannelModel.checked && !checkChannelValue()) {
	    	return false;
	    }
    }
    // channel switch
    if (document.getElementById(formName + "_dataSource_channelSwitch").checked) {
   		if (!checkInputRange(iuThreshold, '<s:text name="config.radioProfile.channel.switch.iuThreshold" />',
    									<s:property value="%{channelSwitchRange.min()}" />,
    									<s:property value="%{channelSwitchRange.max()}" />, showChannelPowerContent)) {
            return false;
   		}
   		if (!checkInputRange(crcChannelThr, '<s:text name="config.radioProfile.interference.crcThreshold" />',
    									<s:property value="%{channelSwitchRange.min()}" />,
    									<s:property value="%{channelSwitchRange.max()}" />, showChannelPowerContent)) {
            return false;
   		}
    }
    
    if(enableChannel.checked)
    {
    	if(parseInt(fromHour.value) > parseInt(toHour.value) || (parseInt(fromHour.value)
    	 	== parseInt(toHour.value) && parseInt(fromMinute.value) >= parseInt(toMinute.value))) {
    		hm.util.reportFieldError(toHour, '<s:text name="error.time.compare.toTimeLower"><s:param>From Time</s:param><s:param>To Time</s:param></s:text>');
    		showChannelPowerContent();
       		toHour.focus();
       		return false;
    	}
    	if (!checkInputRange(channelClient, '<s:text name="config.radioProfile.channel.client" />',
    									<s:property value="%{channelClientRange.min()}" />,
    									<s:property value="%{channelClientRange.max()}" />, showChannelPowerContent)) {
            return false;
    	}
    }
    if(enablePower.checked)
    {
    	if (!checkInputRange(transmitPower, '<s:text name="config.radioProfile.power" />',
    									<s:property value="%{powerRange.min()}" />,
    									<s:property value="%{powerRange.max()}" />, showChannelPowerContent)) {
            return false;
    	}
    }
    if (!checkInputRange(radioRange, '<s:text name="config.radioProfile.radio.range" />',
			<s:property value="%{radioValueRange.min()}" />,
			<s:property value="%{radioValueRange.max()}" />, showChannelPowerContent)) {
		return false;
	}
    if (!checkInputRange(beaconPeriod, '<s:text name="config.radioProfile.period" />',
    									<s:property value="%{periodRange.min()}" />,
    									<s:property value="%{periodRange.max()}" />, showRadioSettingContent)) {
        return false;
    }
   	if (!checkWmmInputValue()) {
   	   	return false;
   	}
  	if (document.getElementById("nModeCca").style.display == "") {
  	  	if(document.getElementById(formName + "_dataSource_enableCca").checked){
	    	if (!checkInputRange(defaultCca, '<s:text name="config.radioProfile.11n.cca.default" />',
	    									<s:property value="%{ccaValueRange.min()}" />,
	    									<s:property value="%{ccaValueRange.max()}" />, showInterferenceContent)) {
	            return false;
	   		}
	   		if (!checkInputRange(maximumCca, '<s:text name="config.radioProfile.11n.cca.maximum" />',
	    									<s:property value="%{ccaValueRange.min()}" />,
	    									<s:property value="%{ccaValueRange.max()}" />, showInterferenceContent)) {
	            return false;
	   		}
	   		if(parseInt(defaultCca.value) > parseInt(maximumCca.value)) {
	    		hm.util.reportFieldError(defaultCca, '<s:text name="error.notLargerThan"><s:param><s:text name="config.radioProfile.11n.cca.default" />'+
	    			'</s:param><s:param><s:text name="config.radioProfile.11n.cca.maximum" /></s:param></s:text>');
	    		showInterferenceContent();
	       		defaultCca.focus();
	       		return false;
	    	}
  	  	}
  	  	if(document.getElementById(formName + "_dataSource_enableInterfernce").checked){
	   		if (!checkInputRange(crcThreshold, '<s:text name="config.radioProfile.interference.crcThreshold" />',
					<s:property value="%{crcThresholdRange.min()}" />,
					<s:property value="%{crcThresholdRange.max()}" />, showInterferenceContent)) {
				return false;
			}
	   		if (!checkInputRange(channelThreshold, '<s:text name="config.radioProfile.interference.channelThreshold" />',
					<s:property value="%{channelThresholdRange.min()}" />,
					<s:property value="%{channelThresholdRange.max()}" />, showInterferenceContent)) {
				return false;
			}
	   		if (!checkInputRange(averageInterval, '<s:text name="config.radioProfile.interference.averageInterval" />',
					<s:property value="%{averageIntervalRange.min()}" />,
					<s:property value="%{averageIntervalRange.max()}" />, showInterferenceContent)) {
				return false;
			}
  	  	}
    }else{
        //reset if needed
        if(document.getElementById(formName + "_dataSource_enableCca").checked){
            if(isNaN(defaultCca.value)){defaultCca.value = 33;}
            if(isNaN(maximumCca.value)){maximumCca.value = 55;}
        }
        if(document.getElementById(formName + "_dataSource_enableInterfernce").checked){
        	if(isNaN(crcThreshold.value)){crcThreshold.value = 20;}
        	if(isNaN(channelThreshold.value)){channelThreshold.value = 20;}
        	if(isNaN(averageInterval.value)){averageInterval.value = 5;}
        }
    }
    if(backhaulFailover.checked)
    {
    	if (!checkInputRange(triggerTime, '<s:text name="config.radioProfile.trigger" />',
    									<s:property value="%{trigTimeRange.min()}" />,
    									<s:property value="%{trigTimeRange.max()}" />, showBackhaulContent)) {
            return false;
    	}
    	if (!checkInputRange(holdTime, '<s:text name="config.radioProfile.holdTime" />',
    									<s:property value="%{holdTimeRange.min()}" />,
    									<s:property value="%{holdTimeRange.max()}" />, showBackhaulContent)) {
            return false;
   		}
   	}

    if(enableBandSteering.checked){
     	if(MODE_PREFER5G == bandSteeringMode.value){
   			if (!checkInputRange(limitNumber, '<s:text name="config.radioProfile.high.density.bandSteering.limitnumber.proberesponses" />',
   					<s:property value="%{limitNumberRange.min()}" />,
   					<s:property value="%{limitNumberRange.max()}" />,showOptimizManagementContent)) {
   				return false;
   			} 
   		}else{
   			if (!checkInputRange(minimumRatio, '<s:text name="config.radioProfile.high.density.bandSteering.minimumratio.clients" />',
   					<s:property value="%{minimumRatioRange.min()}" />,
   					<s:property value="%{minimumRatioRange.max()}" />,showOptimizManagementContent)) {
   				return false;
   			} 
   		}
   	}
   	
    if(enableClientLoadBalance.checked){
   	   	if (!checkInputRange(crcErrorLimit, '<s:text name="config.radioProfile.high.density.crcErrorLimit" />',
				<s:property value="%{crcErrorLimitRange.min()}" />,
				<s:property value="%{crcErrorLimitRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	   	if (!checkInputRange(cuLimit, '<s:text name="config.radioProfile.high.density.cuLimit" />',
				<s:property value="%{cuLimitRange.min()}" />,
				<s:property value="%{cuLimitRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	   	if (!checkInputRange(maxInterference, '<s:text name="config.radioProfile.high.density.maxInterference" />',
				<s:property value="%{maxInterferenceRange.min()}" />,
				<s:property value="%{maxInterferenceRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	   	if (!checkInputRange(clientHoldTime, '<s:text name="config.radioProfile.high.density.clientHoldTime" />',
				<s:property value="%{clientHoldTimeRange.min()}" />,
				<s:property value="%{clientHoldTimeRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	   	if (!checkInputRange(queryInterval, '<s:text name="config.radioProfile.pritimize.management.intervals.query.neighbors" />',
				<s:property value="%{queryIntervalRange.min()}" />,
				<s:property value="%{queryIntervalRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	 }
	if(enableSuppress.checked){
	    	if (!checkInputRange(suppressThreshold, '<s:text name="config.radioProfile.high.density.suppressThreshold" />',
				<s:property value="%{suppressThresholdRange.min()}" />,
				<s:property value="%{suppressThresholdRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	}
	if(enableSafetyNet.checked){
   	   	if (!checkInputRange(safetyNetTimeout, '<s:text name="config.radioProfile.high.density.safetyNetTimeout" />',
				<s:property value="%{safetyNetTimeoutRange.min()}" />,
				<s:property value="%{safetyNetTimeoutRange.max()}" />, showOptimizManagementContent)) {
			return false;
		}
	}
    if (!checkInputRange(maxClients, '<s:text name="config.radioProfile.client" />',
    									<s:property value="%{clientRange.min()}" />,
    									<s:property value="%{clientRange.max()}" />, showClientSelContent)) {
        return false;
    }
    
    if(operation == "editMacOrOui"){
		var value = hm.util.validateOptionTransferSelection("macOrOuis");
		if(value < 0){
			return false
		}else{
			document.forms[formName].macOrOui.value = value;
		}
	}
    
    if(operation == 'update<s:property value="lstForward"/>' || operation == 'create<s:property value="lstForward"/>'
		|| operation == 'update' || operation == 'create'){
    	if(highDensity.checked && !broadcastProbe.checked && supressBPRByOUI.checked){
        	if(Get("macOrOuis").options.length < 1){
        		 hm.util.reportFieldError(Get("supressBprOuiOptionsTb"), 
    		    	'<s:text name="error.requiredField"><s:param><s:text name="config.ids.selectedMacOrOuis" /></s:param></s:text>');
        		showHighDensityContent();
        		return false;
        	}
        }
    }
    
	return true;
}

function checkIfInput(inputElement, title, showContentFun)
{
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        showContentFun();
        inputElement.focus();
        return false;
    }
    return true;
}

function checkInputRange(inputElement, title, min, max, showContentFun)
{
	if (!checkIfInput(inputElement, title, showContentFun)) {
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, title,min,max);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        showContentFun();
        inputElement.focus();
        return false;
    }
    return true;
}

function checkWmmInputValue() {
	var acType;
	for (var i=0;i<4;i++) {
        switch(i) {
        	case 0:
        		acType = "Background's ";
        		break;
        	case 1:
        		acType = "Best-effort's ";
        		break;
        	case 2:
        		acType = "Video's ";
        		break;
        	case 3:
        		acType = "Voice's ";
        		break;
        	default:
        		break;
        }

        var min = document.getElementById("minimum_" + i);
        if (!checkInputRange(min, acType+'Minimum', 1, 15, showWmmQosContent)) {
            return false;
      	}
      	var max = document.getElementById("maximum_" + i);
        if (!checkInputRange(max, acType+'Maximum', 1, 15, showWmmQosContent)) {
            return false;
      	}
      	if (parseInt(min.value) > parseInt(max.value)) {
            hm.util.reportFieldError(min, '<s:text name="error.notLargerThan"><s:param>'+(acType+'Minimum')+'</s:param><s:param>'+'Maximum'+'</s:param></s:text>');
            showWmmQosContent();
            min.focus();
            return false;
      	}
        if (!checkInputRange(document.getElementById("aifs_" + i), acType+'AIFS', 1, 15, showWmmQosContent)) {
            return false;
      	}
        if (!checkInputRange(document.getElementById("txoplimit_" + i), acType+'Txoplimit', 0, 8192, showWmmQosContent)) {
            return false;
      	}
    } 
    return true;
}

function checkChannelValue() {
	var model = CHANNEL_MODEL_3 == document.getElementById(formName + "_dataSource_channelModel").value ? 3 : 4;
	var channel = document.getElementById(formName + "_dataSource_channelValue");
	var title = '<s:text name="config.radioProfile.channel.value" />';

	if (channel.value.length == 0) {
        hm.util.reportFieldError(channel, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        showChannelPowerContent();
        channel.focus();
        return false;
    } else {
		var values = channel.value.split("-");
		var isRight = true;
		var errorCode = "";
		if (values.length != model) {
			isRight = false;
	        errorCode = '<s:text name="error.config.network.radio.profile.channel.format.number"><s:param>'+model+'</s:param></s:text>';
		} else {
			for (var i = 0; i < values.length; i++) {
				var str_value = values[i];
				if("" == str_value || isNaN(str_value)) {
					isRight = false;
					errorCode = '<s:text name="error.config.network.radio.profile.channel.format.int.number" />';
					break;
				} else {
					// it is a number;
					var message = hm.util.validateIntegerRange(str_value, "", 1, 14);
		      		if (message != null) {
		            	isRight = false;
		            	errorCode = '<s:text name="error.config.network.radio.profile.channel.format.range" />';
						break;
		      		}
				}
			}
			overloop:
			for (var i = 0; i < values.length-1; i++) {
				var str_value = values[i];
				for (var j = i+1; j < values.length; j++) {
					if (parseInt(str_value) == parseInt(values[j])) {
						isRight = false;
						errorCode = '<s:text name="error.config.network.radio.profile.channel.format.same.number" />';
						break overloop;
					}
				}
			}
		}
		if (!isRight) {
			hm.util.reportFieldError(channel, errorCode);
			showChannelPowerContent();
	        channel.focus();
	        return false;
		}
    }
	return true;
}

function showUpNmodeChain(checked) {
	document.getElementById("nonDefaultChain").style.display = checked? "none" : "";
}

function enableScan(checked) {
	var scanInterval = document.getElementById(formName + "_dataSource_interval");
	scanInterval.disabled =!checked;
	var scanVoice = document.getElementById(formName + "_dataSource_trafficVoice");
	scanVoice.disabled =!checked;
	var scanClient = document.getElementById(formName + "_dataSource_clientConnect");
	scanClient.disabled =!checked;
	var scanPower = document.getElementById(formName + "_dataSource_powerSave");
	scanPower.disabled =!checked;
	if (!checked) {
		scanInterval.value = "10";
		scanVoice.checked = false;
		scanClient.checked = true;
		scanPower.checked = false;
	}
	document.getElementById("bgScanContent").style.display = checked? "" : "none";
}

function enableFailover(checked) {
	document.getElementById(formName + "_dataSource_triggerTime").disabled =!checked;
	document.getElementById(formName + "_dataSource_holdTime").disabled =!checked;
	if (!checked) {
		document.getElementById(formName + "_dataSource_triggerTime").value = "2";
		document.getElementById(formName + "_dataSource_holdTime").value = "30";
	}
}

function enableHighDensity(checked){
	document.getElementById(formName + "_dataSource_highDensityTransmitRate").disabled =!checked;
	document.getElementById(formName + "_dataSource_enableBroadcastProbe").disabled =!checked;
	document.getElementById(formName + "_dataSource_enableContinuousProbe").disabled =!checked;
	document.getElementById(formName + "_dataSource_enableSupressBPRByOUI").disabled =!checked;
	
	$("#supressBprOuiOptionsTr .transfer").attr("disabled",!checked);
	
	// for ACSP (remove this for bug 19240 from 6.0r1)
	/*if (checked && document.getElementById("channelModelCheck").style.display == "") {
		document.getElementById(formName + "_dataSource_useDefaultChannelModel").checked = true;
		enableChannelModel(true);
	}*/
}

function enableTxBeamforming(checked){
	if(checked){
		document.getElementById("txBeamformingMode").style.display = "";	
	}else{
		document.getElementById("txBeamformingMode").style.display = "none";
	}
}

function clientLoadBalanceChanged(checked){
	document.getElementById(formName + "_dataSource_loadBalancingMode").disabled =!checked;
	document.getElementById(formName + "_dataSource_crcErrorLimit").disabled =!checked;
	document.getElementById(formName + "_dataSource_cuLimit").disabled =!checked;
	document.getElementById(formName + "_dataSource_maxInterference").disabled =!checked;
	document.getElementById(formName + "_dataSource_clientHoldTime").disabled =!checked;
	document.getElementById(formName + "_dataSource_queryInterval").disabled =!checked;
}

function safetyNetChanged(checked){
	document.getElementById(formName + "_dataSource_safetyNetTimeout").disabled =!checked;
}

function suppressChanged(checked){
	document.getElementById(formName + "_dataSource_suppressThreshold").disabled =!checked;
}

function enableCcaValue(checked) {
	document.getElementById(formName + "_dataSource_defaultCcaValue").disabled =!checked;
	document.getElementById(formName + "_dataSource_maxCcaValue").disabled =!checked;
	if (!checked) {
		document.getElementById(formName + "_dataSource_defaultCcaValue").value = "33";
		document.getElementById(formName + "_dataSource_maxCcaValue").value = "55";
	}
}

function enableInterference(checked) {
	document.getElementById(formName + "_dataSource_crcThreshold").disabled =!checked;
	document.getElementById(formName + "_dataSource_channelThreshold").disabled =!checked;
	document.getElementById(formName + "_dataSource_averageInterval").disabled =!checked;
	if (!checked) {
		document.getElementById(formName + "_dataSource_crcThreshold").value = "20";
		document.getElementById(formName + "_dataSource_channelThreshold").value = "20";
		document.getElementById(formName + "_dataSource_averageInterval").value = "5";
	}
}

function enable11bClient(checked) {
	if (checked) {
		document.getElementById(formName + "_dataSource_deny11b").checked = false;
	}
	document.getElementById(formName + "_dataSource_deny11b").disabled =checked;
}

function enable11abgClient(checked) {
	if (checked) {
		document.getElementById(formName + "_dataSource_deny11abg").checked = false;
	}
	document.getElementById(formName + "_dataSource_deny11abg").disabled =checked;
}

function enableChannelModel(checked) {
	var region = document.getElementById(formName + "_dataSource_channelRegion");
	var model = document.getElementById(formName + "_dataSource_channelModel");
	var channel = document.getElementById(formName + "_dataSource_channelValue");
	region.disabled = checked;
	model.disabled = checked;
	channel.disabled = checked;
	if (checked) {
		region.value = CHANNEL_REGION_USA;
		model.value = CHANNEL_MODEL_3;
		channel.value = "01-06-11";
	}
	document.getElementById("channelModelContent").style.display = checked? "none" : "";
}

function enableChannelSwitch(checked) {
	document.getElementById("channelSwitchContent").style.display = checked? "" : "none";
}

function enableChannel(checked) {
	var fromH = document.getElementById(formName + "_dataSource_fromHour");
	var fromM = document.getElementById(formName + "_dataSource_fromMinute");
	var toH = document.getElementById(formName + "_dataSource_toHour");
	var toM = document.getElementById(formName + "_dataSource_toMinute");
	var client = document.getElementById(formName + "_dataSource_channelClient");
	fromH.disabled = !checked;
	fromM.disabled = !checked;
	toH.disabled = !checked;
	toM.disabled = !checked;
	client.disabled =!checked;
	if (!checked) {
		fromH.value = 0;
		fromM.value = 0;
		toH.value = 0;
		toM.value = 0;
		client.value = "0";
	}
	document.getElementById("channelSelectionContent").style.display = checked? "":"none";
}

function enablePower(checked) {
	var power = document.getElementById(formName + "_dataSource_transmitPower");
	power.disabled =!checked;
	if (!checked) {
		power.value = "20";
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td = document.getElementById(details.id);
	var rate = details.v;
	td.length=0;
	td.length=rate.length;
	for(var i = 0; i < rate.length; i ++) {
		td.options[i].value=i+1;
		td.options[i].text=rate[i];
	}
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

//VHT Only work under 11n and 20MHz
function vhtModeChange(){
	var phyMode = $("#radioProfile_dataSource_radioMode").val();
	var channelWidth = $("#radioProfile_dataSource_channelWidth").val();
	if(MODE_NG == phyMode && channelWidth == CHANNEL_WIDTH_20){
		$("#vhtSection").show();
	}else{
		$("#vhtSection").hide();
	}
}

function changeMode(mode) {
	var turbo = document.getElementById(formName + "_dataSource_turboMode");
	var preamble = document.getElementById('shortPreamble');
	var checkChannel = document.getElementById("channelModelCheck");
	var preambleContent = document.getElementById("preambleContent");
	var isNMode = MODE_NA == mode || MODE_NG == mode || MODE_AC == mode;
	document.getElementById("dfs").style.display = (MODE_NA == mode || MODE_A == mode || MODE_AC == mode) ? "" : "none";
	document.getElementById("nModeChannel").style.display = isNMode ? "" : "none";
	document.getElementById("ngModeChannelNote").style.display = MODE_NG == mode ? "" : "none";
	document.getElementById("nModeCca").style.display = isNMode ? "" : "none";
	document.getElementById("nModeGuard").style.display = isNMode ? "" : "none";
	document.getElementById("nModeAggregate").style.display = isNMode ? "" : "none";
	document.getElementById("nModeChain").style.display = isNMode ? "" : "none";
	document.getElementById("nModeClientDeny").style.display = isNMode ? "" : "none";
	<s:if test="%{!oEMSystem}">
		document.getElementById("abModeAntenna").style.display = isNMode ? "none" : "";
	</s:if>
	document.getElementById("turboModeContent").style.display = MODE_A == mode ? "none" : "none";
	//change interference section display style 
	document.getElementById("interferenceTr1").style.display = isNMode ? "" : "none";
	document.getElementById("interferenceTr2").style.display = isNMode ? "" : "none";
	document.getElementById("txBeamformingSection").style.display = (MODE_AC == mode) ? "" : "none";
	vhtModeChange();
	
	<s:if test="%{!oEMSystem}">
	var antennaLs = document.getElementById("antennaType28");
	var radioHz = "2.4";
	if(MODE_A == mode) {
		turbo.disabled = false;
		radioHz = "5";
	} else {
		turbo.disabled = true;
		turbo.checked = false;
	}
	// the antenna list change base on the radio mode
	antennaLs.options[1].text = '<s:text name="config.radioProfile.antenna28.type.1" ><s:param>'+radioHz+'</s:param></s:text>';
	antennaLs.options[2].text = '<s:text name="config.radioProfile.antenna28.type.2" ><s:param>'+radioHz+'</s:param></s:text>';
	</s:if>
	<s:else>
		var chainHeight = document.getElementById("chainBlankHeight");
		var chainTitle = document.getElementById("chainTitleOem");
		if(MODE_A == mode || MODE_BG == mode) {
		    chainHeight.style.display = "none";
		    chainTitle.style.display = "none";
		} else {
			chainHeight.style.display = "";
			chainTitle.style.display = "";
		}
	</s:else>
		
	if(MODE_A == mode || MODE_NA == mode || MODE_AC == mode) {
	    checkChannel.style.display = "none";
	    preambleContent.style.display = "none";
		preamble.disabled = true;
		preamble.value = PREAMBLE_SHORT;
		document.getElementById("channelDiv").innerHTML="<s:text name="config.presence.server.scanChannels.range2" />"
	} else {
		checkChannel.style.display = "";
		preambleContent.style.display = "";
		preamble.disabled = false;
		document.getElementById("channelDiv").innerHTML="<s:text name="config.presence.server.scanChannels.range1" />"
	}
	//for 11ac mode change
	if(MODE_AC == mode){
		var channelWidths = document.getElementById(formName + "_dataSource_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40+">40 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_80+">80 MHz</option>");
	}else{
		var channelWidths = document.getElementById(formName + "_dataSource_channelWidth");
		$(channelWidths).empty();
		$(channelWidths).append("<option selected='true' value="+CHANNEL_WIDTH_20+">20 MHz</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40A+">40-MHz above</option>");
		$(channelWidths).append("<option value="+CHANNEL_WIDTH_40B+">40-MHz below</option>");
	}
//	if (MODE_NA == mode) {
//		document.getElementById(formName + "_dataSource_channelWidth").value=CHANNEL_WIDTH_40A;
//		changeChannel(CHANNEL_WIDTH_40A);
//	} else {
//		document.getElementById(formName + "_dataSource_channelWidth").value=CHANNEL_WIDTH_20;
//		changeChannel(CHANNEL_WIDTH_20);
//	}
	
//	var url = '<s:url action="radioProfile"><s:param name="operation" value="changeRate"/></s:url>' + "&mode="+mode;
//	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	//Added for Casablanca
	bandSteeringShowControl(mode);
}

function changeChannelRegion(region) {
	var model = document.getElementById(formName + "_dataSource_channelModel");
	var channel = document.getElementById(formName + "_dataSource_channelValue");
	if(CHANNEL_REGION_USA == region) {
		model.value = CHANNEL_MODEL_3;
		channel.value = "01-06-11";
	} else {
		model.value = CHANNEL_MODEL_4;
		channel.value = "01-05-09-13";
	}
}

function changeChannelModel(model) {
	var region = document.getElementById(formName + "_dataSource_channelRegion");
	var channel = document.getElementById(formName + "_dataSource_channelValue");
	if (CHANNEL_MODEL_3 == model) {
		channel.value = "01-06-11";
	} else {
		channel.value = CHANNEL_REGION_USA == region.value ? "01-04-08-11" : "01-05-09-13";
	}
}

function showChannelPowerContent(){
	showHideContent("channelPower","");
}

function showRadioSettingContent(){
	showHideContent("radioSetting","");
}

function showWmmQosContent(){
	showHideContent("wmmQos","");
}

function showChainContent(){
	showHideContent("chain","");
}

function showInterferenceContent(){
	showHideContent("interference","");
}

function showBackhaulContent(){
	showHideContent("backhaul","");
}

function showClientSelContent(){
	showHideContent("clientSel","");
}

function showHighDensityContent(){
	showHideContent("highDensity","");
}

function showPresenceSelContent(){
	showHideContent("presenceSel","");
}
function showSensorSelContent(){
	showHideContent("sensorSel","");
}

function clickEnabledPresence(checked){
	if (!checked) {
		Get(formName + "_dataSource_trapInterval").value='<s:property value="%{dataSource.trapInterval}"/>';
		Get(formName + "_dataSource_agingTime").value='<s:property value="%{dataSource.agingTime}"/>';
		Get(formName + "_dataSource_aggrInterval").value='<s:property value="%{dataSource.aggrInterval}"/>';
		Get(formName + "_dataSource_trapInterval").disabled =true;
		Get(formName + "_dataSource_agingTime").disabled =true;
		Get(formName + "_dataSource_aggrInterval").disabled =true;
	} else {
		Get(formName + "_dataSource_trapInterval").disabled =false;
		Get(formName + "_dataSource_agingTime").disabled =false;
		Get(formName + "_dataSource_aggrInterval").disabled =false;
	}
	checkTrapInterval();
}
function scanAllChannel(checked){
	if(checked){
		document.getElementById("enabledChannelTr").style.display="none";
	}else{
		document.getElementById("enabledChannelTr").style.display="";
	}
}
//Added from Casablanca
function bandSteeringShowControl(mode){
	var isShow = MODE_A == mode || MODE_NA == mode || MODE_AC == mode;
	document.getElementById("bandSteering_title").style.display = isShow ? "none" : "";
	document.getElementById("bandSteering_note").style.display = isShow ? "none" : "";
	document.getElementById("bandSteering_check").style.display = isShow ? "none" : "";
	document.getElementById("bandSteering_mode").style.display = isShow ? "none" : "";
	var bandSteeringModeValue = document.getElementById(formName + "_dataSource_bandSteeringMode").value;
	
	if(!isShow){
		if(MODE_BALANCEBAND == bandSteeringModeValue){
			document.getElementById("bandSteering_minimumRatio").style.display = "";
			document.getElementById("bandSteering_limitNumber").style.display ="none"
		}else if(MODE_PREFER5G == bandSteeringModeValue){
			document.getElementById("bandSteering_minimumRatio").style.display = "none";
			document.getElementById("bandSteering_limitNumber").style.display =""
		}else{
			document.getElementById("bandSteering_minimumRatio").style.display = "none";
			document.getElementById("bandSteering_limitNumber").style.display ="none"
		}
	}else{
		document.getElementById("bandSteering_minimumRatio").style.display = "none";
		document.getElementById("bandSteering_limitNumber").style.display ="none"
	}
	
}

function enableBandSteering(checked){
	document.getElementById(formName + "_dataSource_bandSteeringMode").disabled =!checked;
	document.getElementById(formName + "_dataSource_limitNumber").disabled =!checked;
	document.getElementById(formName + "_dataSource_minimumRatio").disabled =!checked;
}

function changeBandSteeringMode(mode) {
	document.getElementById("bandSteering_limitNumber").style.display = MODE_PREFER5G == mode ? "" : "none";
	document.getElementById("bandSteering_minimumRatio").style.display = MODE_BALANCEBAND == mode ? "" : "none";
}

function changeLoadBalancingMode(mode) {
	document.getElementById("loadBalancing_description").style.display = MODE_AIRETIMEBASED == mode ? "" : "none";
	document.getElementById("loadBalancing_crcErrorLimit").style.display = MODE_AIRETIMEBASED == mode ? "" : "none";
	document.getElementById("loadBalancing_maxInterference").style.display = MODE_AIRETIMEBASED == mode ? "" : "none";
	document.getElementById("loadBalancing_cuLimit").style.display = MODE_AIRETIMEBASED == mode ? "" : "none";
}

function showOptimizManagementContent(){
	showHideContent("optimizManagement","");
}

function enableSuppressBPRByOUI(checked){
	document.getElementById("supressBprOuiOptionsTr").style.display = checked ? "" : "none";
}

function enableBroadcastProbe(checked){
	Get("enableSupressBPRByOUITr").style.display = checked ? "none" : "";
	if(checked){
		Get("supressBprOuiOptionsTr").style.display = "none";
	}else if(Get(formName + "_dataSource_enableSupressBPRByOUI").checked){
		Get("supressBprOuiOptionsTr").style.display = "";
	}else{
		Get("supressBprOuiOptionsTr").style.display = "none";
	}
}

function openQuestionMask(type){
	if(type == 1){
		showInfoDialog('<s:text name="defaultChainTip"/>');
	} else {
		showInfoDialog('<s:text name="chainTip"/>');
	}
	
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="radioProfile" includeParams="none"/>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</s:if>
</script>
<div id="content"><s:form action="radioProfile">
	<s:hidden name="dataSource.channelPowerStyle"></s:hidden>
	<s:hidden name="dataSource.radioSettingStyle"></s:hidden>
	<s:hidden name="dataSource.wmmQosStyle"></s:hidden>
	<s:hidden name="dataSource.chainStyle"></s:hidden>
	<s:hidden name="dataSource.interferenceStyle"></s:hidden>
	<s:hidden name="dataSource.backhaulStyle"></s:hidden>
	<s:hidden name="dataSource.clientStyle"></s:hidden>
	<s:hidden name="dataSource.slaStyle"></s:hidden>
	<s:hidden name="dataSource.wipsServerStyle"></s:hidden>
	<s:hidden name="dataSource.presenceServerStyle"></s:hidden>
	<s:hidden name="dataSource.sensorScanStyle"></s:hidden>
	<s:hidden name="dataSource.highDensityStyle"></s:hidden> 
	<s:hidden name="dataSource.optimizManagementStyle"></s:hidden>
	<s:hidden name="macOrOui" />
	
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentIframeOpenFlg" />
		<s:hidden name="contentShowType" />
		<s:hidden name="id" />
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{!jsonMode}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIO_PROFILE%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<s:else>
		<tr>
			<td>
			<div class="topFixedTitle">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-Radio_policies.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.radio"/>
								</s:if>
								<s:else>
									<s:text name="config.title.radio.edit"/>
								</s:else>
							</td>
							<td style="padding-left:10px;">
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel');" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<td class="npcButton">
								<s:if test="%{writeDisabled == 'disabled' || updateDisabled == 'disabled'}">
									&nbsp;
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a>
								</s:else>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		</s:else>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{!jsonMode}">
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="880px">
			</s:if>
			<s:else>
				<table cellspacing="0" cellpadding="0" border="0" class="topFixedTitle">
			</s:else>
				<tr>
					<td><tiles:insertDefinition name="notes" /></td>
				</tr>
				<tr><td height="4px"></td></tr>
				<tr>
					<td><!-- definition -->
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="200px"><label><s:text
									name="config.radioProfile.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="24" name="dataSource.radioName"
									maxlength="%{nameLength}" disabled="%{disabledName}"
									onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.radioProfile.description" /></label></td>
								<td><s:textfield size="48" name="dataSource.description"
									maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.radioProfile.radioMode" /></label></td>
								<td><s:select name="dataSource.radioMode" disabled="%{disableMode}"
									value="dataSource.radioMode" list="%{enumRadioMode}" listKey="key"
									listValue="value" cssStyle="width: 115px"
									onchange="changeMode(this.options[this.selectedIndex].value);" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td><!-- Optional Advanced Settings -->
						<fieldset><legend><s:text name="config.radioProfile.optional.tag" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<!-- Channel and Power -->
								<tr><td height="5px"></td></tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.channelPower.tag" />','channelPower');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="channelPower" style="display: <s:property value="%{dataSource.channelPowerStyle}"/>">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck"><s:checkbox
																name="dataSource.backgroundScan" onclick="enableScan(this.checked);" /></td>
															<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.backgroundScan" /></td>
														</tr>
														<tr style="display:<s:property value="%{bgScanStyle}"/>" id="bgScanContent">
															<td></td>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td class="labelT1" style="padding-left: 0;" width="168px"><label><s:text
																						name="config.radioProfile.interval" /></label></td>
																					<td><s:textfield size="10" name="dataSource.interval"
																						maxlength="4" disabled="%{!dataSource.backgroundScan}"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																						name="config.radioProfile.intervalRange" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td class="headCheck"><s:checkbox
																						name="dataSource.trafficVoice" disabled="%{!dataSource.backgroundScan}" /></td>
																					<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.traffic" /></td>
																				</tr>
																				<tr>
																					<td class="headCheck"><s:checkbox
																						name="dataSource.clientConnect" disabled="%{!dataSource.backgroundScan}" /></td>
																					<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.clientConnect" /></td>
																				</tr>
																				<tr>
																					<td class="headCheck"><s:checkbox
																						name="dataSource.powerSave" disabled="%{!dataSource.backgroundScan}" /></td>
																					<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.powerSave" /></td>
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
											<tr style="display:<s:property value="%{hide11naMode}"/>" id="dfs">
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck" width="20px"><s:checkbox name="dataSource.enableDfs" disabled="%{disableDfs}"/></td>
															<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.enable.dfs" /></td>
														</tr>
														<s:if test="%{disableDfs}">
														<tr>
															<td colspan="2" class="noteInfo" style="padding-left: 22px;"><s:text name="config.radioProfile.enable.dfs.note" /></td>
														</tr>
														</s:if>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{isAOrNAmode?'none':''}"/>" id="channelModelCheck" >
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck"><s:checkbox
																name="dataSource.useDefaultChannelModel" onclick="enableChannelModel(this.checked);" /></td>
															<td class="labelT1" style="padding:8px 100px 5px 0"><s:text name="config.radioProfile.channel.model.default" /></td>
														</tr>
														<tr style="display:<s:property value="%{channelModelStyle}"/>" id="channelModelContent">
															<td></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" style="padding-left:120px" width="45px"><label><s:text
																			name="config.radioProfile.channel.region" /></label></td>
																		<td style="padding-left: 3px;"><s:select name="dataSource.channelRegion" onchange="changeChannelRegion(this.options[this.selectedIndex].value);"
																		   	list="%{enumRegion}" listKey="key" listValue="value" disabled="%{dataSource.useDefaultChannelModel}" /></td>
																		<td class="labelT1" style="padding-left: 12px;"><label><s:text
																			name="config.radioProfile.channel.model" /></label></td>
																		<td style="padding-left: 4px;"><s:select name="dataSource.channelModel" onchange="changeChannelModel(this.options[this.selectedIndex].value);"
																		   	list="%{enumChannelModel}" listKey="key" listValue="value" disabled="%{dataSource.useDefaultChannelModel}" /></td>
																		<td class="labelT1" style="padding-left: 48px;"><label><s:text
																			name="config.radioProfile.channel.value" /></label></td>
																		<td style="padding-left: 4px;"><s:textfield size="10" name="dataSource.channelValue"
																			maxlength="11" disabled="%{dataSource.useDefaultChannelModel}"
																			onkeypress="return hm.util.keyPressPermit(event,'radioChannel');" /></td>
																	</tr>
																</table>
															</td>
														</tr>
														
													</table>
												</td>
											</tr>
											<tr>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck"><s:checkbox
																name="dataSource.channelSwitch" onclick="enableChannelSwitch(this.checked);" /></td>
															<td class="labelT1" style="padding:8px 100px 5px 0"><s:text name="config.radioProfile.channel.switch.title" /></td>
														</tr>
														<tr style="display:<s:property value="%{dataSource.channelSwitch?'':'none'}"/>" id="channelSwitchContent">
															<td></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td colspan="2">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td class="headCheck"><s:checkbox name="dataSource.stationConnect" /></td>
																					<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.channel.switch.title.station" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1" style="padding-left: 0;" width="168px"><label><s:text name="config.radioProfile.channel.switch.iuThreshold" /></label></td>
																		<td><s:textfield size="10" name="dataSource.iuThreshold" maxlength="2"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			<s:text name="config.radioProfile.channel.switch.range" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1" style="padding-left: 0;"><label><s:text name="config.radioProfile.interference.crcThreshold" /></label></td>
																		<td><s:textfield size="10" name="dataSource.crcChannelThr" maxlength="2"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			<s:text name="config.radioProfile.channel.switch.range" /></td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="headCheck"><s:checkbox
																name="dataSource.enableChannel" onclick="enableChannel(this.checked);" /></td>
															<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.channel" /></td>
														</tr>
														<tr style="display:<s:property value="%{channelSelectionStyle}"/>" id="channelSelectionContent">
															<td></td>
															<td>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" style="padding-left: 120px;" width="45px"><label><s:text
																			name="config.radioProfile.channel.from" /></label></td>
																		<td style="padding-left: 3px;"><s:select name="dataSource.fromHour" value="dataSource.fromHour"
																		   	list="%{enumHours}" listKey="key" listValue="value" disabled="%{!dataSource.enableChannel}" /></td>
																		<td style="padding-left: 2px;"><s:select name="dataSource.fromMinute" value="dataSource.fromMinute"
																		    list="%{enumMinutes}" listKey="key" listValue="value" disabled="%{!dataSource.enableChannel}" /></td>
																		<td class="labelT1" style="padding-left: 5px;"><label><s:text
																			name="config.radioProfile.channel.to" /></label></td>
																		<td style="padding-left: 4px;"><s:select name="dataSource.toHour" value="dataSource.toHour"
																		   	list="%{enumHours}" listKey="key" listValue="value" disabled="%{!dataSource.enableChannel}" /></td>
																		<td style="padding-left: 2px;"><s:select name="dataSource.toMinute" value="dataSource.toMinute"
																		    list="%{enumMinutes}" listKey="key" listValue="value" disabled="%{!dataSource.enableChannel}" /></td>
																		<td class="labelT1" style="padding-left: 5px;"><label><s:text
																			name="config.radioProfile.channel.client" /></label></td>
																		<td style="padding-left: 4px;"><s:textfield size="10" name="dataSource.channelClient"
																			maxlength="3" disabled="%{!dataSource.enableChannel}"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																			name="config.radioProfile.channel.client.range" /></td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="headCheck"><s:checkbox onclick="enablePower(this.checked);"
																name="dataSource.enablePower" value="%{dataSource.enablePower}" /></td>
															<td class="labelT1" style="padding-left: 0" width="168px"><s:text name="config.radioProfile.power" /></td>
															<td><s:textfield size="10" name="dataSource.transmitPower"
																maxlength="2" disabled="%{!dataSource.enablePower}"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																name="config.radioProfile.power.range" /></td>
														</tr>	
														<tr>
															<td class="labelT1" style="padding-left: 5px" colspan="2"><s:text name="config.radioProfile.radio.range" /></td>
															<td><s:textfield size="10" name="dataSource.radioRange" maxlength="5"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																<s:text name="config.radioProfile.radio.value.range" /></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeChannel">
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" style="padding-left: 5px;" width="185px"><label><s:text
																name="config.radioProfile.channel.width" /></label></td>
															<td>
															<s:if test="%{isACmode}">
																<s:select name="dataSource.channelWidth" value="dataSource.channelWidth" list="%{enumWidth11AC}"
																listKey="key" listValue="value" cssStyle="width: 115px" onchange="vhtModeChange();"/>
															</s:if>
															<s:else>
																<s:select name="dataSource.channelWidth" value="dataSource.channelWidth" list="%{enumWidth}"
																listKey="key" listValue="value" cssStyle="width: 115px" onchange="vhtModeChange();"/>
															</s:else>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{showNoteFor11ngMode}"/>" id="ngModeChannelNote">
												<td> 
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td colspan="2" class="noteInfo" style="padding-left: 5px;"><s:text name="config.radioProfile.channel.width.note" /></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- Radio Settings -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.radioSetting.tag" />','radioSetting');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="radioSetting" style="display: <s:property value="%{dataSource.radioSettingStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr style="display:<s:property value="%{preambleStyle}"/>" id="preambleContent">
																<td class="labelT1" style="padding-left: 5px;" width="185px"><label><s:text
																	name="config.radioProfile.preamble" /></label></td>
																<td><s:select name="dataSource.shortPreamble" value="dataSource.shortPreamble"  list="%{enumPreamble}"
																	listKey="key" listValue="value" cssStyle="width: 115px"
																	disabled="%{isAOrNAmode}" id="shortPreamble" /></td>
															</tr>
															<tr>
																<td class="labelT1" style="padding-left: 5px;" width="185px"><label><s:text
																	name="config.radioProfile.period" /></label></td>
																<td><s:textfield size="10" name="dataSource.beaconPeriod"
																	maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																	name="config.radioProfile.periodRange" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeGuard">
																<td class="headCheck"><s:checkbox name="dataSource.guardInterval"
																	value="%{dataSource.guardInterval}" /></td>
																<td class="labelT1" style="padding-left: 0"><s:text
																	name="config.radioProfile.guard.interval" /></td>
															</tr>
															<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeAggregate">
																<td class="headCheck"><s:checkbox name="dataSource.aggregateMPDU" 
																	value="%{dataSource.aggregateMPDU}" /></td>
																<td class="labelT1" style="padding-left: 0"><s:text
																	name="config.radioProfile.ampdu" /></td>
															</tr>
															<tr style="display:none" id="turboModeContent">
																<td class="headCheck"><s:checkbox
																	name="dataSource.turboMode" disabled="%{disableTurbomode}" /></td>
																<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.turboMode" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td style="padding: 2px 0 4px 0">
														<s:checkbox name="dataSource.enabledBssidSpoof" value="%{dataSource.enabledBssidSpoof}"> </s:checkbox>
														<s:text name="config.configTemplate.enabledBssidSpoof"></s:text>
													</td>
												</tr>
												<tr id="vhtSection" style="display:<s:property value="vhtSectionStyle"/>">
													<td style="padding: 2px 0 4px 0">
														<s:checkbox name="dataSource.enableVHT" value="%{dataSource.enableVHT}"> </s:checkbox>
														<s:text name="gw.config.configTemplate.vhtenable"></s:text>
													</td>
												</tr>		
												<tr>
													<td style="padding: 2px 0 4px 0">
														<s:checkbox name="dataSource.enableFrameburst" value="%{dataSource.enableFrameburst}"> </s:checkbox>
														<s:text name="gw.config.configTemplate.frameburstenable"></s:text>
													</td>
												</tr>	
												<tr id="txBeamformingSection" style="display:<s:property value="%{txbeamforimgTrStyle}"/>">
													<td style="padding: 2px 0 4px 0">									
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td>
																	<table cellspacing="0" cellpadding="0" border="0" width="100%;">																			
																		<tr>
																			<td colspan="2">
																				<s:checkbox name="dataSource.enabledTxbeamforming" value="%{dataSource.enabledTxbeamforming}" onclick="enableTxBeamforming(this.checked)"/>
																				<label for="radioProfile_dataSource_enabledTxbeamforming"><s:text name="gw.config.radioProfile.txbeamforming.enable"/></label>
																			</td>
																		</tr>
																		<tr><td height="5px"></td></tr>
																		<tr id="txBeamformingMode" style="display:<s:property value="%{txbeamforimgModeStyle}"/>">
																			<td style="padding-left:20px; width:200px;">
																				<label><s:text name="gw.config.radioProfile.txbeamforming.mode"/></label>																	
																			</td>																
																			<td>
																				<s:select name="dataSource.txBeamformingMode" value="dataSource.txBeamformingMode" list="%{enumTxBeamformingMode}" listKey="key"
																						listValue="value" cssStyle="width: 200px" />
																			</td>															
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>																						
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- WMM QoS -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.wmmQos.tag" />','wmmQos');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 25px;">
										<div id="wmmQos" style="display: <s:property value="%{dataSource.wmmQosStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<th align="center" width="138px"><s:text
																	name="config.radioProfile.wmm.ac" /></th>
																<th align="center" width="125px"><s:text
																	name="config.radioProfile.wmm.min" /></th>
																<th align="center"><s:text 
																	name="config.radioProfile.wmm.max" /></th>
																<th align="center"><s:text
																	name="config.radioProfile.wmm.aifs" /></th>
																<th align="center"><s:text
																	name="config.radioProfile.wmm.txop" /></th>
																<th align="center"><s:text
																	name="config.radioProfile.wmm.noAck" /></th>
															</tr>
															<s:iterator id="items"
																value="%{dataSource.wmmItems.values()}" status="status">
																<tr class="list">
																	<td class="list" align="center"><s:property value="%{value}" /></td>
																	<td class="list" align="center"><s:textfield name="minimums"
																		value="%{minimum}" maxlength="2" size="10"
																		id="minimum_%{#status.index}" 
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	</td>
																	<td class="list" align="center"><s:textfield name="maximums"
																		value="%{maximum}" maxlength="2" size="10"
																		id="maximum_%{#status.index}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	</td>
																	<td class="list" align="center"><s:textfield name="aifses"
																		value="%{aifs}" maxlength="2" size="10"
																		id="aifs_%{#status.index}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	</td>
																	<td class="list" align="center"><s:textfield name="txoplimits"
																		value="%{txoplimit}" maxlength="4" size="10"
																		id="txoplimit_%{#status.index}"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	</td>
																	<td class="listCheck" align="center"><s:checkbox value="%{noAck}"
																		name="noAcks" fieldValue="%{#status.index}" /></td>
																</tr>
															</s:iterator>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<!-- Antenna / Transmit and Receive Chain Settings -->
								<s:if test="%{oEMSystem}">
									<tr style="display:<s:property value="%{chainStyleForOem}"/>" id="chainBlankHeight"><td height="10px"></td></tr>
									<tr style="display:<s:property value="%{chainStyleForOem}"/>" id="chainTitleOem">
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.chainSetting.tag.oem" />','chain');</script></td>
									</tr>
								</s:if>
								<s:else>
									<tr><td height="10px"></td></tr>
									<tr>
										<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.chainSetting.tag" />','chain');</script></td>
									</tr>
								</s:else>
								<tr>
									<td style="padding-left: 5px;">
										<div id="chain" style="display: <s:property value="%{dataSource.chainStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<s:if test="%{!oEMSystem}">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr style="display:<s:property value="%{hide11nMode==''?'none':''}"/>" id="abModeAntenna">
																<td>
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td class="labelT1" width="180px"><s:text name="config.radioProfile.antenna.20type" /></td>
																			<td><s:select name="dataSource.antennaType20" list="%{enumAntenna20}"
																				listKey="key" listValue="value" cssStyle="width: 82px" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><s:text name="config.radioProfile.antenna.28type" /></td>
																			<td><s:select id="antennaType28" name="dataSource.antennaType28" list="%{enumAntenna28}"
																				listKey="key" listValue="value" cssStyle="width: 285px" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												</s:if>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeChain">
																<td>
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr><td colspan="2" height="5px"></td></tr>
																		<tr>
																			<td class="headCheck"><s:checkbox onclick="showUpNmodeChain(this.checked);"
																				name="dataSource.useDefaultChain" value="%{dataSource.useDefaultChain}" /></td>
																			<td class="labelT1" style="padding-left: 0">
																				<s:if test="%{oEMSystem}">
																					<s:text name="config.radioProfile.use.default.chain.oem" />
																				</s:if>
																				<s:else>
																					<s:if test="%{easyMode}">
																						<s:text name="config.radioProfile.use.default.chain.express" />
																					</s:if>
																					<s:else>
																						<s:text name="config.radioProfile.use.default.chain" />
																					</s:else>
																				</s:else>
																				<a style="padding-left: 15px;" href="javascript: openQuestionMask(1);">?</a>
																			</td>
																		</tr>
																		<tr style="display:<s:property value="%{dataSource.useDefaultChain?'none':''}"/>" id="nonDefaultChain">
																			<td>&nbsp;</td>
																			<td>
																				<table cellspacing="0" cellpadding="0" border="0">
																					<tr>
																						<td class="labelT1" width="160px"><label><s:text
																							name="config.radioProfile.transmit.chain" /></label></td>
																						<td><s:select name="dataSource.transmitChain" value="dataSource.transmitChain"  list="%{enumChain}"
																							listKey="key" listValue="value" cssStyle="width: 82px" />
																							<a style="padding-left: 15px;" href="javascript: openQuestionMask(2);">?</a>
																						</td>
																					</tr>
																					<tr>
																						<td class="labelT1"><label><s:text
																							name="config.radioProfile.receive.chain" /></label></td>
																						<td><s:select name="dataSource.receiveChain" value="dataSource.receiveChain"  list="%{enumChain}"
																							listKey="key" listValue="value" cssStyle="width: 82px" />
																							<a style="padding-left: 15px;" href="javascript: openQuestionMask(2);">?</a>
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
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- Interference -->
								<tr id="interferenceTr1" style="display: <s:property value="%{interferenceTrStyle}"/>">
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.interference.tag" />','interference');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="interference" style="display: <s:property value="%{dataSource.interferenceStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeCca">
																<td>
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr style="display:none">
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck"><s:checkbox onclick="enableCcaValue(this.checked);"
																							name="dataSource.enableCca" value="%{dataSource.enableCca}" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.11n.cca.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr style="display:none">
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="180px"><label><s:text
																							name="config.radioProfile.11n.cca.default" /></label></td>
																						<td><s:textfield size="10" name="dataSource.defaultCcaValue" maxlength="2" disabled="%{!dataSource.enableCca}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							<s:text name="config.radioProfile.11n.cca.value.range" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1"><label><s:text
																							name="config.radioProfile.11n.cca.maximum" /></label></td>
																						<td><s:textfield size="10" name="dataSource.maxCcaValue" maxlength="2" disabled="%{!dataSource.enableCca}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							<s:text name="config.radioProfile.11n.cca.value.range" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck"><s:checkbox onclick="enableInterference(this.checked);"
																							name="dataSource.enableInterfernce" value="%{dataSource.enableInterfernce}" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.interference.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="180px"><label><s:text
																							name="config.radioProfile.interference.crcThreshold" /></label></td>
																						<td><s:textfield size="10" name="dataSource.crcThreshold" maxlength="2" disabled="%{!dataSource.enableInterfernce}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							<s:text name="config.radioProfile.interference.crcThreshold.note" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1"><label><s:text
																							name="config.radioProfile.interference.channelThreshold" /></label></td>
																						<td><s:textfield size="10" name="dataSource.channelThreshold" maxlength="2" disabled="%{!dataSource.enableInterfernce}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							<s:text name="config.radioProfile.interference.channelThreshold.note" /></td>
																					</tr>
																					<tr>
																						<td class="labelT1"><label><s:text
																							name="config.radioProfile.interference.averageInterval" /></label></td>
																						<td><s:textfield size="10" name="dataSource.averageInterval" maxlength="2" disabled="%{!dataSource.enableInterfernce}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																							<s:text name="config.radioProfile.interference.averageInterval.note" /></td>
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
											</table>
										</div>
									</td>
								</tr>
								<tr id="interferenceTr2" style="display: <s:property value="%{interferenceTrStyle}"/>"><td height="10px"></td></tr>
								<!-- Load Balance removed from Beijing (3.5r3)-->
								<!--
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.balance.tag" />','loadBalance');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="loadBalance" style="display: <s:property value="%{dataSource.loadBalanceStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="headCheck"><s:checkbox
																				name="dataSource.loadBalance"
																				value="%{dataSource.loadBalance}" onclick="enableBalance(this.checked);" /></td>
																			<td class="labelT1" style="padding-left: 0"><s:text
																				name="config.radioProfile.balance.enable" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="labelT1" width="180px"><label><s:text
																				name="config.radioProfile.minCount" /></label></td>
																			<td><s:textfield size="10" name="dataSource.minCount"
																				maxlength="3" disabled="%{!dataSource.loadBalance}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.radioProfile.clientRange" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label><s:text
																				name="config.radioProfile.loadFactor" /></label></td>
																			<td><s:textfield size="10" name="dataSource.threshold"
																				maxlength="3" disabled="%{!dataSource.loadBalance}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.radioProfile.factorRange" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1"><label><s:text
																				name="config.radioProfile.roaming" /></label></td>
																			<td><s:select name="dataSource.roamingThreshold" value="dataSource.roamingThreshold"  list="%{enumRoaming}"
																				listKey="key" listValue="value" cssStyle="width: 82px"
																				disabled="%{!dataSource.loadBalance}" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								 -->
								<!-- Backhaul Failover -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.backhaul.tag" />','backhaul');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="backhaul" style="display: <s:property value="%{dataSource.backhaulStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="headCheck"><s:checkbox
																				name="dataSource.backhaulFailover"
																				value="%{dataSource.backhaulFailover}" onclick="enableFailover(this.checked);" /></td>
																			<td class="labelT1" style="padding-left: 0">
																				<s:if test="%{oEMSystem}">
																					<s:text name="config.radioProfile.failover.enable.no.notice" />
																				</s:if>
																				<s:else>
																					<s:text name="config.radioProfile.failover.enable" />
																				</s:else>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="labelT1" width="180px"><label><s:text
																				name="config.radioProfile.trigger" /></label></td>
																			<td><s:textfield size="10" name="dataSource.triggerTime"
																				maxlength="1" disabled="%{!dataSource.backhaulFailover}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.radioProfile.timeRange" /></td>
																			</tr>
																			<tr>
																			<td class="labelT1"><label><s:text
																				name="config.radioProfile.holdTime" /></label></td>
																			<td><s:textfield size="10" name="dataSource.holdTime"
																				maxlength="3" disabled="%{!dataSource.backhaulFailover}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																				name="config.radioProfile.holdTimeRange" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- Optimizing Management Traffic Setting -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.optimizing.management.setting.tag" />','optimizManagement');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="optimizManagement" style="display: <s:property value="%{dataSource.optimizManagementStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td style="padding-left: 5px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td id="bandSteering_title" style="display:<s:property value="%{bandSteeringStyle}" />" >
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1"><s:text name="config.radioProfile.high.density.bandSteering.title" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="bandSteering_note" style="display:<s:property value="%{bandSteeringStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="noteInfo" style="padding-left: 18px;"><s:text name="config.radioProfile.optimize.management.bandSteering.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="bandSteering_check" style="display:<s:property value="%{bandSteeringStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;"><s:checkbox name="dataSource.enableBandSteering" value="%{dataSource.enableBandSteering}" onclick="enableBandSteering(this.checked);" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.bandSteering.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="bandSteering_mode" style="display:<s:property value="%{bandSteeringStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="410px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.bandSteering.mode" /></label></td>
																						<td><s:select list="enumBandSteeringMode" listKey="key" listValue="value" name="dataSource.bandSteeringMode" 
																									disabled="%{!dataSource.enableBandSteering}"  
																									onchange="changeBandSteeringMode(this.options[this.selectedIndex].value);"/></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="bandSteering_limitNumber" style="display:<s:property value="%{modePreferStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="410px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.bandSteering.limitnumber.proberesponses" /></label></td>
																						<td><s:textfield size="10" name="dataSource.limitNumber"
																							maxlength="3" disabled="%{!dataSource.enableBandSteering}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.bandSteering.limitnumber.proberesponses.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="bandSteering_minimumRatio" style="display:<s:property value="%{modeBalanceBandStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="410px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.bandSteering.minimumratio.clients" /></label></td>
																						<td><s:textfield size="10" name="dataSource.minimumRatio"
																							maxlength="3" disabled="%{!dataSource.enableBandSteering}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.bandSteering.minimumratio.clients.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1"><s:text name="config.radioProfile.optimize.management.loadBalance.title" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;"><s:checkbox name="dataSource.enableClientLoadBalance" value="%{dataSource.enableClientLoadBalance}" onclick="clientLoadBalanceChanged(this.checked);" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.clientLoadBalance.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.optimize.management.loadBalance.mode" /></label></td>
																						<td><s:select list="enumLoadBalancingMode" listKey="key" listValue="value" name="dataSource.loadBalancingMode" 
																								disabled="%{!dataSource.enableClientLoadBalance}" 
																								onchange="changeLoadBalancingMode(this.options[this.selectedIndex].value);"/></td>
																						<td class="noteInfo" style="padding-left: 18px;"><s:text name="config.radioProfile.optimize.management.loadBalance.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="loadBalancing_description" style="display:<s:property value="%{balanceModeStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" style="padding-left: 35px"><s:text name="config.radioProfile.high.density.clientLoadBalance.description" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="loadBalancing_crcErrorLimit" style="display:<s:property value="%{balanceModeStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.crcErrorLimit" /></label></td>
																						<td><s:textfield size="10" name="dataSource.crcErrorLimit"
																							maxlength="2" disabled="%{!dataSource.enableClientLoadBalance}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.crcErrorLimit.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="loadBalancing_maxInterference" style="display:<s:property value="%{balanceModeStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.maxInterference" /></label></td>
																						<td><s:textfield size="10" name="dataSource.maxInterference"
																							maxlength="2" disabled="%{!dataSource.enableClientLoadBalance}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.maxInterference.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td id="loadBalancing_cuLimit" style="display:<s:property value="%{balanceModeStyle}" />">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.cuLimit" /></label></td>
																						<td><s:textfield size="10" name="dataSource.cuLimit"
																							maxlength="1" disabled="%{!dataSource.enableClientLoadBalance}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.cuLimit.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" style="padding-left: 35px;"><s:text name="config.radioProfile.high.density.clientHoldTime.description" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.clientHoldTime" /></label></td>
																						<td><s:textfield size="10" name="dataSource.clientHoldTime"
																							maxlength="3" disabled="%{!dataSource.enableClientLoadBalance}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.clientHoldTime.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.pritimize.management.intervals.query.neighbors" /></label></td>
																						<td><s:textfield size="10" name="dataSource.queryInterval"
																							maxlength="3" disabled="%{!dataSource.enableClientLoadBalance}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.pritimize.management.intervals.query.neighbors.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1"><s:text name="config.radioProfile.weak.snr.suppress.enable.title" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;"><s:checkbox name="dataSource.enableSuppress" value="%{dataSource.enableSuppress}" onclick="suppressChanged(this.checked);" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.suppress.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.suppressThreshold" /></label></td>
																						<td><s:textfield size="10" name="dataSource.suppressThreshold"
																							maxlength="3" disabled="%{!dataSource.enableSuppress}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.suppressThreshold.note" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1"><s:text name="config.radioProfile.pritimize.management.safetyNet.title" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;"><s:checkbox name="dataSource.enableSafetyNet" value="%{dataSource.enableSafetyNet}" onclick="safetyNetChanged(this.checked);" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.safetyNet.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="275px" style="padding-left: 35px;"><label><s:text name="config.radioProfile.high.density.safetyNetTimeout" /></label></td>
																						<td><s:textfield size="10" name="dataSource.safetyNetTimeout"
																							maxlength="3" disabled="%{!dataSource.enableSafetyNet}"
																							onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																							name="config.radioProfile.high.density.safetyNetTimeout.note" /></td>
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
											</table>
										</div>
									</td>
								</tr> 
								<!-- High Density -->
								<tr><td height="10px"></td></tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.high.density.tag" />','highDensity');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="highDensity" style="display: <s:property value="%{dataSource.highDensityStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="headCheck"><s:checkbox
																				name="dataSource.enableHighDensity" 
																				value="%{dataSource.enableHighDensity}" onclick="enableHighDensity(this.checked);" /></td>
																			<td class="labelT1" style="padding-left: 0"><s:text
																				name="config.radioProfile.high.density.enable" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td style="padding-left: 5px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="labelT1" width="300px"><label><s:text name="config.radioProfile.high.density.transmitRate" /></label></td>
																						<td><s:select list="enumHighDensityRate" listKey="key" listValue="value" name="dataSource.highDensityTransmitRate" disabled="%{!dataSource.enableHighDensity}" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;"><s:checkbox disabled="%{!dataSource.enableHighDensity}" name="dataSource.enableContinuousProbe" value="%{dataSource.enableContinuousProbe}" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.continuousProbe.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td class="headCheck" style="padding-left: 15px;">
																							<s:checkbox disabled="%{!dataSource.enableHighDensity}" name="dataSource.enableBroadcastProbe"
																								onclick="enableBroadcastProbe(this.checked);" value="%{dataSource.enableBroadcastProbe}" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.broadcastProbe.enable" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr id="enableSupressBPRByOUITr" style="display: <s:property value="%{supressBprByOUIStyle}"/>">
																						<td class="headCheck" style="padding-left: 35px;">
																							<s:checkbox disabled="%{!dataSource.enableHighDensity}" name="dataSource.enableSupressBPRByOUI" 
																								onclick="enableSuppressBPRByOUI(this.checked);"	value="%{dataSource.enableSupressBPRByOUI}" /></td>
																						<td class="labelT1" style="padding-left: 0"><s:text name="config.radioProfile.high.density.supress.broadcastProbe.oui.enable" /></td>
																					</tr>
																					<tr id="supressBprOuiOptionsTr" style="display: <s:property value="%{supressBprByOUIOptionStyle}"/>">
																						<td style="padding-left:60px;" colspan="2">
																							<table cellspacing="0" cellpadding="0" border="0" id="supressBprOuiOptionsTb">
																								<%-- <tr><td style="padding:10px 0;"><s:text name="config.radioProfile.high.density.supress.broadcastProbe.oui.prompt" /></td></tr> --%>
																								<tr>
																									<s:push value="%{supressBprOuiOptions}" >
																										<td><tiles:insertDefinition
																											name="optionsTransfer" /></td>
																									</s:push>
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
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- Client Selection -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.client.tag" />','clientSel');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="clientSel" style="display: <s:property value="%{dataSource.clientStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td>
																	<table cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td class="labelT1" width="180px"><label><s:text
																				name="config.radioProfile.client" /></label></td>
																			<td><s:textfield size="10" name="dataSource.maxClients"
																				maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;
																				<span id="show_maxclients_range">
<%-- 																				<s:if test="%{isACmode}">
																				<s:text name="config.radioProfile.clientRange.ac" />
																				</s:if>
																				<s:else>
 --%>																				<s:text name="config.radioProfile.clientRange" />
																				<%-- </s:else> --%>
																				</span>
																				</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr style="display:<s:property value="%{hide11nMode}"/>" id="nModeClientDeny">
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="headCheck"><s:checkbox disabled="%{dataSource.deny11abg}"  onclick="enable11abgClient(this.checked);"
																				name="dataSource.deny11b" value="%{dataSource.deny11b}" /></td>
																			<td class="labelT1" style="padding-left: 0" width="250px"><s:text
																				name="config.radioProfile.11bClient" /></td>
																		</tr>
																		<tr>
																			<td class="headCheck"><s:checkbox disabled="%{dataSource.deny11b}" onclick="enable11bClient(this.checked);"
																				name="dataSource.deny11abg" value="%{dataSource.deny11abg}" /></td>
																			<td class="labelT1" style="padding-left: 0" width="230px"><s:text
																				name="config.radioProfile.11nClient" /></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<!-- SLA Selection -->
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.sla.tag" />','slaSel');</script></td>
								</tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="slaSel" style="position: relative; display: <s:property value="%{dataSource.slaStyle}"/>">
											<div style="position: absolute; bottom: 5px; right: 10px;"><input type="button" name="ignore" value="Customize" class="button" onClick="showCustomizePanel();" <s:property value="writeDisabled" />></div>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="5px"></td></tr>
															<tr>
																<td><s:radio label="Gender"	name="dataSource.slaThoughput" list="%{slaThoughputOption1}" listKey="key" listValue="value"/></td>
															</tr>
															<tr>
																<td><s:radio label="Gender"	name="dataSource.slaThoughput" list="%{slaThoughputOption2}" listKey="key" listValue="value"/></td>
															</tr>
															<tr>
																<td><s:radio label="Gender"	name="dataSource.slaThoughput" list="%{slaThoughputOption3}" listKey="key" listValue="value"/></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
							  <tr><td height="10px"></td></tr>
							<!-- WIPS server Selection start-->
		                       <tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.wips.tag"/>','wipsSel');</script></td>
							   </tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="wipsSel" style="position: relative; display: <s:property value="%{dataSource.wipsServerStyle}"/>">
										 <table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT2" style="padding-left: 10px">
												 <s:checkbox name="dataSource.enableWips"
												  value="%{dataSource.enableWips}" onclick="checkWipsServer()"/>
									              <s:text name="config.wips.server.enable"/>
									              <span style="padding-left:2px" id="WipsNote"> </span>
									          <script type="text/javascript">
												function checkWipsServer(){
												  var checked=document.getElementById(formName + "_dataSource_enableWips").checked;
												  if(checked){
													   document.getElementById("WipsNote").innerHTML='<s:text name="config.wips.server.enabled.note"/>';
												   }else{
													   document.getElementById("WipsNote").innerHTML="";
												   }
												}
											   checkWipsServer()
										  </script>
								         </td>
									   </tr>
									 </table>
									</div>
								  </td>
								</tr>
							 <!-- WIPS server Selection end-->
							 
							 <!-- Presence server Selection -->
							<s:if test="%{presenceEnable}">
								<tr><td height="10px"></td></tr>
								<tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.presence.tag" />','presenceSel');</script></td>
							   </tr>
							</s:if>
							 <s:if test="%{!presenceRegistered}">
							 	<s:set name="tableStyle" value="%{'style=\"background: #eee; color: #666;\"'}" />
							 	<script>
							 		YAHOO.util.Event.onDOMReady(function(){
							 			Get(formName + "_dataSource_enabledPresence").disabled = true;
							 			Get(formName + "_dataSource_trapInterval").disabled =true;
							 			Get(formName + "_dataSource_agingTime").disabled =true;
							 			Get(formName + "_dataSource_aggrInterval").disabled =true;
							 		});
							 	</script>
							 </s:if>
			                  	<tr>
									<td style="padding-left: 5px;">
										<div id="presenceSel"
											style="position: relative; display: <s:property value="%{dataSource.presenceServerStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<s:if test="%{!presenceRegistered}">
												<tr><td>
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT2">
													<s:text name="config.presence.server.note.enable" /></td>
												</tr>
												</table>
												</td></tr>
												</s:if>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%" <s:property escapeHtml="false" value="%{#tableStyle}"/>>
															<tr>
																<td colspan="2" class="labelT2" style="padding-left: 10px"><s:checkbox
																		name="dataSource.enabledPresence"
																		value="%{dataSource.enabledPresence}"
																		onclick="clickEnabledPresence(this.checked)" />
																	<s:text name="config.presence.server.enabled" /></td>
															</tr>
															<tr>
																<td class="labelT1" width="120px"
																	style="padding-left: 20px"><s:text
																		name="config.presence.server.interval" /></td>
																<td><s:textfield
																		name="dataSource.trapInterval" size="12"
																		disabled="%{!dataSource.enabledPresence}"
																		maxlength="3" onmouseout="checkTrapInterval()"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text
																		name="config.presence.server.interval.range" />
																    <s:label id="trapIntervalLabel"></s:label>
																<script type="text/javascript">
																	function checkTrapInterval(){
																	    var trapInterval=document.getElementById(formName + "_dataSource_trapInterval");
																		var labelMsg=document.getElementById("trapIntervalLabel");
																		if(trapInterval.value<<%=RadioProfile.DEFAULT_PRESENCE_TIME%> 
																		 && document.getElementById(formName + "_dataSource_enabledPresence").checked){
																			labelMsg.innerHTML="<s:text name='error.presence.server.interval.range' />";
																		}else{
																			labelMsg.innerHTML="";
																		}
																	}
																	checkTrapInterval();
																</script>
																</td>
															</tr>
															<tr>
																<td class="labelT1" style="padding-left: 20px">
																	<s:text name="config.presence.server.agingtime" />
																</td>
																<td><s:textfield name="dataSource.agingTime"
																		size="12"
																		disabled="%{!dataSource.enabledPresence}"
																		maxlength="3"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text
																		name="config.presence.server.agingtime.range" />
																</td>
															</tr>
															<tr>
																<td class="labelT1" style="padding-left: 20px">
																	<s:text name="config.presence.server.aggrInterval" />
																</td>
																<td><s:textfield
																		name="dataSource.aggrInterval" size="12"
																		disabled="%{!dataSource.enabledPresence}"
																		maxlength="3"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text
																		name="config.presence.server.aggrInterval.range" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
							  <tr><td height="10px"></td></tr>
		                      <tr>
									<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radioProfile.scanchannel.tag" />','sensorSel');</script></td>
							   </tr>
								<tr>
									<td style="padding-left: 5px;">
										<div id="sensorSel" style="position: relative; display: <s:property value="%{dataSource.sensorScanStyle}"/>">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														   <tr height="5px"></tr>
															<tr>
																<td class="labelT2" width="125px" style="padding-left: 15px"><s:text
																		name="config.presence.server.dwell.time" /></td>
																<td><s:textfield name="dataSource.dellTime"
																		value="%{dataSource.dellTime==0?1200:dataSource.dellTime}"
																		size="12" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																	<s:text
																		name="config.presence.server.dwelltime.range" />
																</td>
															</tr>
	
															<tr>
																<td colspan="2" class="labelT2"><s:checkbox
																		name="dataSource.scanAllChannel"
																		value="%{dataSource.scanAllChannel}"
																		onclick="scanAllChannel(this.checked)" /> <s:text
																		name="config.presence.server.channel.list" /></td>
															</tr>
	
															<tr id="enabledChannelTr"
																style="display:<s:property value="%{!dataSource.scanAllChannel ?'':'none'}"/>">
																<td class="labelT2" width="100px"
																	style="padding-left: 15px"><s:text
																		name="config.presence.server.channels" /></td>
																<td><s:textfield
																		name="dataSource.scanChannels" size="12" maxlength="64"
																		onkeypress="return hm.util.keyPressPermit(event,'scanchannel');" />
																<span id="channelDiv"><s:if test="%{radio5G}">
																<s:text
																		name="config.presence.server.scanChannels.range2" />
																 </s:if><s:else>
																 <s:text
																		name="config.presence.server.scanChannels.range1" />
																 </s:else>
																</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="customizePanel" style="display: none;">
<div class="hd"><s:text name="config.radioProfile.sla.customize.title" /></div>
<div class="bd">
	<iframe id="customizeFrame" name="customizeFrame" width="0" height="0"
		frameborder="0" src="">
	</iframe>
</div>
</div>
<script type="text/javascript">
var customizePanel = null;
function createCustomizePanel(width, height){
	var div = document.getElementById("customizePanel");
	var iframe = document.getElementById("customizeFrame");
	iframe.width = width;
	iframe.height = height;
	customizePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	customizePanel.render();
	div.style.display="";
	customizePanel.beforeHideEvent.subscribe(customizePanelClosed);
}
function customizePanelClosed(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("customizeFrame").style.display = "none";
	}
}
function showCustomizePanel(){
	if(null == customizePanel){
		createCustomizePanel(600,420);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("customizeFrame").style.display = "";
	}
	customizePanel.show();
	var iframe = document.getElementById("customizeFrame");
	iframe.src ="<s:url value='slaMappingCustomize.action' includeParams='none' />?operation=init";
}
</script>
