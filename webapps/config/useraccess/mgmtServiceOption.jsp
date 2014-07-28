<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.util.EnumConstUtil"%>
<%@page import="com.ah.bo.wlan.Cwp" %>
<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0 4px 6px 4px;
}
-->
</style>
<script>
var formName = 'mgmtServiceOption';
var AUTH_TYPE_LOCAL = <%=EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL%>;

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	var operation = '<s:property value="%{operation}"/>';
	var expanding_vlanid = <s:property value="%{expanding_vlanid}"/>;
	if (!document.getElementById("mgmtName").disabled) {
			document.getElementById("mgmtName").focus();
	}
	//add by nxma
	if(operation == 'new'|| expanding_vlanid){
		showCreateSection('multicast');
    }
	//if(operation == 'edit'||operation == 'clone'|| expanding_vlanid){
		//toggleDisplayMultipleVlan();
    //}

	//added for PMTUD
	updateForMonitorMSS();

	<s:if test="%{jsonMode}">
		top.changeIFrameDialog(920, 600);
	</s:if>
}
function multicastselectClick(element){
	if(element.checked){
		showCreateSection('multicast');
	}
}
function showCreateSection(type) {
    if(type=='multicast'){
       hm.util.hide('newButtonMulticast');
	   hm.util.show('createButtonMulticast');
	   hm.util.show('createSectionMulticast');
 	   // to fix column overlap issue on certain browsers
 	   var trh = document.getElementById('headerSectionMulticast');
 	   var trc = document.getElementById('createSectionMulticast');
 	   var table = trh.parentNode;
 	   table.removeChild(trh);
 	   table.insertBefore(trh, trc);
 	   document.getElementById("expanding_vlanid").value="true";
     }
}
function hideCreateSection(type) {
    if(type=='multicast'){
  		hm.util.hide('createButtonMulticast');
        hm.util.show('newButtonMulticast');
        hm.util.hide('createSectionMulticast');
        document.getElementById("expanding_vlanid").value="false";
    }
}

function toggleCheckAllMultipleVlan(checkBox){
	var checkBoxs = document.getElementsByName('multiplevlanIndices');
	for (var i = 0; i < checkBoxs.length; i++) {
		if (checkBoxs[i].checked != checkBox.checked) {
			checkBoxs[i].checked = checkBox.checked;
		}
	}
}
/*
function validateIpnetmask(operation){
	if(operation == 'addMultipleVlan'){
		var ipRouteIpElement = document.getElementById(formName + "_ipInput");
		var displayElement = document.getElementById("checkAllMulticast");
		var rows =document.getElementById("IpAndNetmaskTable").rows;
		if(rows != null){
			for(var i=3; i<rows.length; i++){
				var td = rows[i].cells[1];
				if(null != td){
					var ip = td.innerHTML;
					if(ipRouteIpElement.value.match(ip.trim())){
						hm.util.reportFieldError(displayElement, '<s:text name="error.addSameNameObjectExist"><s:param><s:text name="config.mgmtservice.service.GER.tunneling.ip" /></s:param></s:text>');
						return false;
					}
				}
			}
		}
	}
	return true;
}*/
function validateNetmask(netmaskEl){
	var displayElement = document.getElementById("checkAllMulticast");
	if (netmaskEl.value.length == 0) {
		netmaskEl.value = "255.255.255.255";
  	}
	if (! hm.util.validateMask(netmaskEl.value)) {
		hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.cfg.ipRoute.mask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
	}
	return true;
}
function validateMulticastIpAddress(netmaskEl){
	var displayElement = document.getElementById("checkAllMulticast");
	var vlans = netmaskEl.value.split(".");
	var vlan = vlans[0];
	number = parseInt(vlan);
	if(number<224||number>239){
		hm.util.reportFieldError(displayElement, "<s:text name="config.mgmtservice.service.GER.tunneling.multicast.ip.note"></s:text>");
		netmaskEl.focus();
		return false;
	}
	return true;
}

function validateIpAndNetmask(operation){
	if(operation == 'addMultipleVlan'){
		var ipRouteIpElement = document.getElementById(formName + "_ipInput");
		var ipRouteMaskElement = document.getElementById(formName + "_netmaskInput");
		var displayElement = document.getElementById("checkAllMulticast");
		if (ipRouteIpElement.value.length == 0) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.service.GER.tunneling.ip" /></s:param></s:text>');
			ipRouteIpElement.focus();
			return false;
		}
		if (!hm.util.validateIpAddress(ipRouteIpElement.value)) {
			hm.util.reportFieldError(displayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.mgmtservice.service.GER.tunneling.multicast.ip" /></s:param></s:text>');
			ipRouteIpElement.focus();
			return false;
		}
		if (!validateMulticastIpAddress(ipRouteIpElement)) {
			return false;
		}
		if (!validateNetmask(ipRouteMaskElement)) {
			return false;
		}
	}if (operation == 'removeMultipleVlan'){
		var cbs = document.getElementsByName('multiplevlanIndices');
		if (cbs.length == 0) {
			var feChild = document.getElementById("checkAllMulticast");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			var feChild = document.getElementById("checkAllMulticast");
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
	}
	return true;
}

function beforeSubmitForMss(){
	var thresholdForAllTCP = document.getElementById(formName + "_dataSource_thresholdForAllTCP");
	if (thresholdForAllTCP.value.length == 0){
		thresholdForAllTCP.value = 0;
	}
	var thresholdThroughVPNTunnel = document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel");
	if (thresholdThroughVPNTunnel.value.length == 0){
		thresholdThroughVPNTunnel.value = 0;
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'addMultipleVlan'&&
			operation != 'removeMultipleVlan') {
			showProcessing();
		}
		//close create section after apply ssid profiles
		if(operation == 'addMultipleVlan'){
			document.getElementsByName("multicastselect").value=1;
			document.getElementById("expanding_vlanid").value="false";
		}
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		<s:if test="%{jsonMode}">
			document.forms[formName].parentIframeOpenFlg.value = true;
		</s:if>
		beforeSubmitForMss();
		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
	    if ("update"!=operation &&
	    	"cancel"!=operation &&
			"addMultipleVlan"!=operation&&
			"removeMultipleVlan"!=operation) {
 			//document.forms[formName].target = "_parent";
 		}
 		if ("cancel" == operation || "update" == operation) {
 		    var parentEl = parent.document.getElementById('apAnimation');
 		    if (parentEl) {
 			    YAHOO.util.Dom.setStyle(parentEl, 'height', parentHeight + 'px');
 		    }
 		}
	    document.forms[formName].submit();
	    document.forms[formName].target = "_self";
	}
}
function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MGMT_SERVICE_OPTION%>" || operation == 'cancel<s:property value="lstForward"/>') {
		cancelTempAlarmThreshold();
		return true;
	}

    if (!validateName(operation)) {
		return false;
	}
    /**
    if(!validateIpnetmask(operation)){
    	return false;
    }*/
    if(!validateIpAndNetmask(operation)){
    	return false;
    }

	if (!validateAirtimePerSecond()) {
		return false;
	}

	if (!validateRoamingGuaranteedAirtime()) {
		return false;
	}

	if (!validateForwardMaxMac()) {
		return false;
	}

	if (!validateForwardMaxIp()) {
		return false;
	}

	if (!validatePpskAutoSaveInt()){
		return false;
	}

	if (!validateForMonitorMSS()){
		return false;
	}

	if (!validateTcpMss()) {
		return false;
	}

	if (!validateTempAlarmThreshold()) {
		return false;
	}

    if (!validateFansUnderSppedAlarmThreshold()) {
        return false;
    }

	if(operation == "editRadius"){
		var value = hm.util.validateListSelection(formName + "_radiusId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].radiusId.value = value;
		}
	}
	return true;
}

function cancelTempAlarmThreshold() {
	document.getElementById(formName + "_dataSource_tempAlarmThreshold").value="75";
	document.getElementById(formName + "_dataSource_airtimePerSecond").value="500";
	document.getElementById(formName + "_dataSource_roamingGuaranteedAirtime").value="20";
	document.getElementById(formName + "_dataSource_forwardMaxMac").value="0";
	document.getElementById(formName + "_dataSource_forwardMaxIp").value="0";
	document.getElementById(formName + "_dataSource_tcpMssThreshold").value="0";
}

function validateName(operation) {
	if(operation == 'create' || operation == 'update'
	|| operation =='create'+'<s:property value="lstForward"/>'
	|| operation =='update'+'<s:property value="lstForward"/>'){
     var inputElement = document.getElementById("mgmtName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.mgmtservice.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }
	}
    return true;
}

function validateTempAlarmThreshold() {
	var inputElement = document.getElementById(formName + "_dataSource_tempAlarmThreshold");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.tempAlarmThreshold" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.tempAlarmThreshold" />',
                                                       <s:property value="%{tempAlarmThresholdRange.min()}" />,
                                                       <s:property value="%{tempAlarmThresholdRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateFansUnderSppedAlarmThreshold() {
    var inputElement = document.getElementById(formName + "_dataSource_fansUnderSpeedAlarmThreshold");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.fansUnderSpeedAlarmThreshold" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.fansUnderSpeedAlarmThreshold" />',
                                                       <s:property value="%{fansUnderSpeedAlarmThresholdRange.min()}" />,
                                                       <s:property value="%{fansUnderSpeedAlarmThresholdRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
      }
      return true;
}

function validateForwardMaxMac() {
	if(!document.getElementById(formName + "_dataSource_enableForwardMaxMac").checked) {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_forwardMaxMac");
	if (inputElement.value.length == 0) {
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.disableForwardMaxMac" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.disableForwardMaxMac" />', 1, 8000);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
       	return false;
    }
    return true;
}

function validateForwardMaxIp() {
	if(!document.getElementById(formName + "_dataSource_enableForwardMaxIp").checked) {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_forwardMaxIp");
	if (inputElement.value.length == 0) {
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.disableForwardMaxIp" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.disableForwardMaxIp" />', 1, 8000);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function validatePpskAutoSaveInt(){
	var ppskAutoSaveEle = document.getElementById(formName + "_dataSource_ppskAutoSaveInt");
	if(ppskAutoSaveEle.value.length == 0){
		hm.util.reportFieldError(ppskAutoSaveEle, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.ppskAutoSaveInt"/></s:param></s:text>');
		ppskAutoSaveEle.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(ppskAutoSaveEle.value, '<s:text name="config.mgmtservice.ppskAutoSaveInt"/>', 60, 3600);
	if(message != null){
		hm.util.reportFieldError(ppskAutoSaveEle, message);
		ppskAutoSaveEle.focus();
		return false;
	}
	return true;
}

function validateTcpMss(){
	if(!document.getElementById(formName + "_dataSource_enableTcpMss").checked) {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_tcpMssThreshold");
	if (inputElement.value.length == 0) {
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.enableTcpMssThresold" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    if(parseInt(inputElement.value) !== 0){
        var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.enableTcpMssThresold" />', 64, 1414);
        if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            inputElement.focus();
            return false;
        }
    }
    return true;
}

function validateAirtimePerSecond() {
	var disableCallAdmissionControl = document.getElementById(formName + "_dataSource_disableCallAdmissionControl");
	if(!disableCallAdmissionControl.checked){
		var inputElement = document.getElementById(formName + "_dataSource_airtimePerSecond");
		if (inputElement.value.length == 0) {
	    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.airtimePerSecond" /></s:param></s:text>');
	        inputElement.focus();
	        return false;
	    }
	    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.airtimePerSecond" />',
	                                                       <s:property value="%{airtimePerSecondRange.min()}" />,
	                                                       <s:property value="%{airtimePerSecondRange.max()}" />);
	    if (message != null) {
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	    }
	}
    return true;
}

function validateRoamingGuaranteedAirtime() {
	var disableCallAdmissionControl = document.getElementById(formName + "_dataSource_disableCallAdmissionControl");
	if(!disableCallAdmissionControl.checked){
		var inputElement = document.getElementById(formName + "_dataSource_roamingGuaranteedAirtime");
		if (inputElement.value.length == 0) {
	    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.mgmtservice.roamingGuaranteedAirtime" /></s:param></s:text>');
	        inputElement.focus();
	        return false;
	    }
	    var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.roamingGuaranteedAirtime" />',
	                                                       <s:property value="%{roamingGuaranteedAirtimeRange.min()}" />,
	                                                       <s:property value="%{roamingGuaranteedAirtimeRange.max()}" />);
	    if (message != null) {
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	    }
	}
    return true;
}

function changeAuthType(type) {
	var ifLocalAuth = AUTH_TYPE_LOCAL==type;
    document.getElementById("radius").style.display = ifLocalAuth ? "none" : "";
   	if (ifLocalAuth) {
   		document.getElementById(formName + "_dataSource_radiusAuthType").value=<%=Cwp.AUTH_METHOD_PAP%>;
   	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td = document.getElementById(details.id);
	if (td != null) {
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(details.v));
	}
};

var detailsFailed = function(o) {
	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function changeMacAuth() {
	// argument formId can be the id or name attribute value of the
    // HTML form, or an HTML form object.
    document.forms[formName].operation.value = 'changeMacAuth';
	var formObject = document.getElementById('mgmtServiceOption');
	YAHOO.util.Connect.setForm(formObject);
	var url = '<s:url action="mgmtServiceOption" includeParams="none"></s:url>';
	var cObj = YAHOO.util.Connect.asyncRequest('POST', url, callback);
}

function changeCac(checked){
	var airtimePerSecond = document.getElementById(formName + "_dataSource_airtimePerSecond");
	var roamingGuaranteedAirtime = document.getElementById(formName + "_dataSource_roamingGuaranteedAirtime");
	airtimePerSecond.disabled = checked;
	roamingGuaranteedAirtime.disabled = checked;
}

function changeForwardMaxMac(checked) {
	var maxMac = document.getElementById(formName + "_dataSource_forwardMaxMac");
	maxMac.disabled = !checked;
	if (!checked) {
		maxMac.value = "0";
	}
}

function changeForwardMaxIp(checked) {
	var maxIp = document.getElementById(formName + "_dataSource_forwardMaxIp");
	maxIp.disabled = !checked;
	if (!checked) {
		maxIp.value = "0";
	}
}

function changeTcpMss(checked) {
	var tcpMssThreshold = document.getElementById(formName + "_dataSource_tcpMssThreshold");
	tcpMssThreshold.disabled = !checked;
	if (!checked) {
		tcpMssThreshold.value = "0";
	}
}
//added for Os detection start
function checkOsDetection(checked) {
	if (checked)
	{
		hm.util.show('osDetectionSection');
	}
	else
	{
		hm.util.hide('osDetectionSection');
	}
}
//added for Os detection end

//added for PMTUD start
function enableMonitorMSS(cbxValue){
	var thresholdForAllTCP = document.getElementById(formName + "_dataSource_thresholdForAllTCP");
	var thresholdThroughVPNTunnel = document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel");
	thresholdForAllTCP.disabled = !cbxValue;
	thresholdThroughVPNTunnel.disabled = !cbxValue;
	if(!cbxValue){
		thresholdForAllTCP.value ="";
		thresholdThroughVPNTunnel.value ="";
	}
}

function validateForMonitorMSS() {
	if(!document.getElementById(formName + "_dataSource_monitorMSS").checked) {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_thresholdForAllTCP");
	if (inputElement.value.length > 0) {
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.mgmtservice.threshold.all.tcp.connections" />', 64, 1460);
	    if (message != null) {
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	    }
    }

    var inputElement2 = document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel");
	if (inputElement2.value.length > 0) {
		 var message2 = hm.util.validateIntegerRange(inputElement2.value, '<s:text name="config.mgmtservice.threshold.tcp.connections.vpn.tunnel" />', 64, 1460);
		    if (message2 != null) {
		        hm.util.reportFieldError(inputElement2, message2);
		        inputElement2.focus();
		        return false;
		    }
    }
    return true;
}

function updateForMonitorMSS(){
	var overrideEl = document.getElementById(formName + "_dataSource_monitorMSS");
	if(!overrideEl.checked){
		document.getElementById(formName + "_dataSource_thresholdForAllTCP").value="";
		document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdForAllTCP").value=="0"){
		document.getElementById(formName + "_dataSource_thresholdForAllTCP").value="";
	}
	if(document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel").value=="0"){
		document.getElementById(formName + "_dataSource_thresholdThroughVPNTunnel").value="";
	}
}
//added for PMTUD end
function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='mgmtServiceOption' includeParams='none' />" + "?jsonMode=true"
				+ "&ignore=" + new Date().getTime();
		if (operation == 'create') {
			//
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mgmtServiceOption"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
}

function openQuestionMask() {
	showInfoDialog('<s:text name="config.mgmtservice.detection.methods.information"/>');
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtServiceOption" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
    </s:else>
}
</s:if>

</script>
<div id="content"><s:form action="mgmtServiceOption">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg" />
	<div id="vlanTitleDiv" class="topFixedTitle" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="82%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-MGMT_options.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.mgmtoptions.dialog.new.title"/>&nbsp;</td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.mgmtoptions.dialog.edit.title"/>&nbsp;</td>
							</s:else>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td width="28%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>

<!--add by nxma -->
<s:hidden name="expanding_vlanid" id="expanding_vlanid" value="%{expanding_vlanid}" />
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
	<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_MGMT_SERVICE_OPTION%>');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}">
				<table style="padding: 0 4px 6px 4px;" border="0" cellspacing="0" cellpadding="0" width="810px">
			</s:if>
			<s:else>
				<table class="editBox" style="padding: 0 4px 6px 4px;" border="0" cellspacing="0" cellpadding="0" width="820px">
			</s:else>
				<tr>
					<td><!-- definition -->
						<table border="0" cellspacing="0" cellpadding="0">
							<tr><td height="4px"></td></tr>
							<tr>
								<td class="labelT1" width="120px"><s:text
									name="config.mgmtservice.name" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.mgmtName" size="24" id="mgmtName"
									maxlength="%{nameLength}" disabled="%{disabledName}"
									onkeypress="return hm.util.keyPressPermit(event,'name');" />
									<s:text name="config.name.range"/></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.mgmtservice.description" /></td>
								<td><s:textfield name="dataSource.description" size="60"
									maxlength="%{descriptionLength}" />
									<s:text name="config.description.range"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="5px"></td></tr>

				<tr>
					<td><!-- GRE tunneling selective multicast forwarding Section -->
					<fieldset><legend><s:text
						name="config.mgmtservice.service.GER.tunneling.tag" /></legend>
					<table border="0" cellspacing="0" cellpadding="0" width="720px">
						<tr>
							<td>
							<s:set name="_block" value="%{getText('config.mgmtservice.service.GER.tunneling.block')}"></s:set>
							<s:set name="_allow" value="%{getText('config.mgmtservice.service.GER.tunneling.allow')}"></s:set>

								<tr>
									<td>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
										<td colspan="2">
											<s:radio name="multicastselect"
												list="#{1:#_block,2:#_allow}"  listKey="key" listValue="value" value="%{dataSource.multicastselect}"
												onclick="multicastselectClick(this);" />
										</td>
										</tr>
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0"
												width="100%">
												<tr>
													<td height="5px"></td>
												</tr>

													<tr id="multicastSection"
														style="display: <s:property value="%{multipleVlanDisplay}"/>">
														<td valign="top" width="100%" style="padding-top: 5px;">
														<fieldset><legend><s:text
															name="config.mgmtservice.service.GER.tunneling.exception" /></legend>
														<table cellspacing="0" cellpadding="0" border="0"
															width="450px" class="embedded">
															<tr>
																<td height="10"></td>
															</tr>
															<tr>
																<td style="padding: 4px 0 0 0;">
																<table cellspacing="0" cellpadding="0" border="0"
																	width="100%" id="IpAndNetmaskTable">
																	<tr id="newButtonMulticast">
																		<td colspan="5" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore"
																					value="New" class="button"
																					onClick="showCreateSection('multicast');"
																					<s:property value="writeDisabled" />></td>
																				<td><input type="button" name="ignore"
																					value="Remove" class="button"
																					onClick="submitAction('removeMultipleVlan');"
																					<s:property value="writeDisabled" />></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display: none;" id="createButtonMulticast">
																		<td colspan="5" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore"
																					value="Apply" class="button"
																					onClick="submitAction('addMultipleVlan');"></td>
																				<td><input type="button" name="ignore"
																					value="Remove" class="button"
																					onClick="submitAction('removeMultipleVlan');"></td>
																				<td><input type="button" name="ignore"
																					value="Cancel" class="button"
																					onClick="hideCreateSection('multicast');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerSectionMulticast">
																		<th align="left" style="padding-left: 0;"><input
																			type="checkbox" id="checkAllMulticast"
																			onClick="toggleCheckAllMultipleVlan(this);"></th>
																		<th align="left"><s:text
																			name="config.mgmtservice.service.GER.tunneling.ip" />
																		</th>
																		<th align="left"><s:text
																			name="config.mgmtservice.service.GER.tunneling.netmask" />
																		</th>
																	</tr>
																	<tr style="display: none;" id="createSectionMulticast">
																		<td class="listHead">&nbsp;</td>
																		<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																			name="ipInput" size="15" maxlength="15" />
																		</td>
																		<td class="listHead" valign="top" nowrap="nowrap"><s:textfield
																			name="netmaskInput" size="15" maxlength="15" />
																		</td>
																		</tr>
																	<s:iterator value="%{dataSource.multipleVlan}"
																		status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox
																				name="multiplevlanIndices" fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="ip" />
																			<td class="list"><s:property value="netmask" />
																		</tr>
																	</s:iterator>
																	<s:if test="%{gridCount > 0}">
																		<s:generator separator="," val="%{' '}"
																			count="%{gridCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="6">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																</table>
																</td>
															</tr>
														</table>
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
									</td>
								</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<!--end-->

				<tr>
					<td><!-- Service Control -->
						<fieldset><legend><s:text name="config.mgmtservice.service.tag" /></legend>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.disableCallAdmissionControl"
													onclick="changeCac(this.checked);"/>
													<s:text name="config.mgmtservice.disableCAC"/></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="padding-left: 18px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="232px" style="padding-left: 5px"><s:text name="config.mgmtservice.airtimePerSecond" /></td>
												<td><s:textfield name="dataSource.airtimePerSecond"
													size="6" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"
													disabled="%{dataSource.disableCallAdmissionControl}" />
													<s:text name="config.mgmtservice.airtimePerSecondRange" /></td>
											</tr>
											<tr>
												<td class="labelT1" style="padding-left: 5px"><s:text name="config.mgmtservice.roamingGuaranteedAirtime" /></td>
												<td><s:textfield name="dataSource.roamingGuaranteedAirtime"
													size="6" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"
													disabled="%{dataSource.disableCallAdmissionControl}" />
													<s:text name="config.mgmtservice.roamingGuaranteedAirtimeRange" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr><td><table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding-left: 0" class="labelT1" width="255px"><s:checkbox name="dataSource.enableForwardMaxMac"
														onclick="changeForwardMaxMac(this.checked);"/>
														<s:text name="config.mgmtservice.disableForwardMaxMac" /></td>
													<td style="padding-left: 0"><s:textfield name="dataSource.forwardMaxMac"
														size="6" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"
														disabled="!dataSource.enableForwardMaxMac" />
														<s:text name="config.mgmtservice.forwardMaxMacRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableForwardMaxIp"
														onclick="changeForwardMaxIp(this.checked);"/>
														<s:text name="config.mgmtservice.disableForwardMaxIp" /></td>
													<td><s:textfield name="dataSource.forwardMaxIp"
														size="6" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"
														disabled="!dataSource.enableForwardMaxIp" />
														<s:text name="config.mgmtservice.forwardMaxIpRange" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableTcpMss"
														onclick="changeTcpMss(this.checked);"/>
														<s:text name="config.mgmtservice.enableTcpMssThresold" /></td>
													<td><s:textfield name="dataSource.tcpMssThreshold"
														size="6" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"
														disabled="!dataSource.enableTcpMss" />
														<s:text name="config.mgmtservice.enableTcpMssThresold.note" /></td>
												</tr>
												<tr>
													<td class="labelT1" style="padding-left: 23px">
														<s:text name="config.mgmtservice.ppskAutoSaveInt"/></td>
													<td><s:textfield name="dataSource.ppskAutoSaveInt" size="6" maxlength="4"
														onkeypress="return hm.util.keyPressPermit(event,'ten');" />
														<s:text name="config.mgmtservice.ppskAutoSaveInt.note"/></td>
												</tr>
											</table></td></tr>
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="180px" style="padding-left: 0"><s:checkbox name="dataSource.disableResetButton"/>
																<s:text name="config.mgmtservice.disableResetButton" /></td>
															<td class="labelT1" width="180px" style="padding-left: 0"><s:checkbox name="dataSource.disableConsolePort"/>
																<s:text name="config.mgmtservice.disableConsolePort"/></td>
															<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableSmartPoe"/>
																<s:text name="config.mgmtservice.enableSmartPoe"/></td>
														</tr>
														<tr>
															<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.disableProxyArp"/>
																<s:text name="config.mgmtservice.disableProxyArp"/></td>
															<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.disableSsid"/>
																<s:text name="config.mgmtservice.disableSsid"/></td>
															<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enablePCIData"/>
																<s:text name="config.mgmtservice.enablePCIData"/></td>
														</tr>
														<tr>
															<td colspan="2" class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableIcmpRedirect"/>
																<s:text name="config.mgmtservice.enableIcmpRedirect"/></td>
															<td style="padding: 5px 0 5px 10px;"><s:checkbox name="dataSource.enablePMTUD" value="dataSource.enablePMTUD" />
																<s:text name="config.mgmtservice.enablepmtud"/></td>
														</tr>
														<!-- added for OS Detection start-->
														<tr>
															<td colspan="3">
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																	<tr>
																		<td class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableOsdetection"
																			onclick="checkOsDetection(this.checked);"/>
																			<s:text name="config.mgmtservice.enableosdetection"/></td>
																	</tr>
																	<tr style="display:<s:property value="%{hideOsDetection}"/>;" id="osDetectionSection">
																		<td >
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding-left: 15px;">
																						<s:text name="config.mgmtservice.osdetection.title"/>
																						<a style="padding-left: 15px;" href="javascript: openQuestionMask();">?</a>
																					</td>
																				</tr>
																				<tr>
																					<td style="padding-left: 20px;"><s:radio label="Gender"
																						name="dataSource.osDetectionMethod" list="%{osDetectionMethodDhcp}"
																						listKey="key" listValue="value"
																						value="%{dataSource.osDetectionMethod}" /></td>
																				</tr>
																				<tr>
																					<td style="padding-left: 20px;"><s:radio label="Gender"
																						name="dataSource.osDetectionMethod" list="%{osDetectionMethodHttp}"
																						listKey="key" listValue="value"
																						value="%{dataSource.osDetectionMethod}" /></td>
																				</tr>
																				<tr>
																					<td style="padding-left: 20px;"><s:radio label="Gender"
																						name="dataSource.osDetectionMethod" list="%{osDetectionMethodBoth}"
																						listKey="key" listValue="value"
																						value="%{dataSource.osDetectionMethod}" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<!-- added for OS Detection end-->
														<!-- added for PMTUD start-->
														<tr>
															<td colspan="3" class="labelT1" style="padding-left: 0">
																<s:checkbox name="dataSource.monitorMSS" value="dataSource.monitorMSS"
																			onclick="enableMonitorMSS(this.checked);"/>
																<s:text name="config.mgmtservice.enablemonitormss"/></td>
														</tr>
														<tr>
															<td colspan="3" class="noteInfo" style="padding-left: 25px"><s:text name="config.mgmtservice.enablemonitormss.note"/></td>
														</tr>
														<tr>
															<td colspan="3">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" style="padding-left: 25px;" width="360" ><s:text name="config.mgmtservice.threshold.all.tcp.connections" /></td>
																		<td><s:textfield name="dataSource.thresholdForAllTCP" size="18"
																				disabled="%{!dataSource.monitorMSS}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			<s:text name="hiveAp.vpn.threshold.range"/></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td colspan="3">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" style="padding-left: 25px;" width="360" ><s:text name="config.mgmtservice.threshold.tcp.connections.vpn.tunnel" /></td>
																		<td><s:textfield name="dataSource.thresholdThroughVPNTunnel" size="18"
																				disabled="%{!dataSource.monitorMSS}"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																			<s:text name="config.mgmtservice.threshold.rage"/></td>
																	</tr>
																</table>
															</td>
														</tr>
														<!-- added for PMTUD end-->

														<!-- added for sync valnid start-->
														<tr>
															<td colspan="3" class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableSyncVlanId"/>
																<s:text name="config.mgmtservice.enablesyncvlan.title"/></td>

														</tr>
														<tr>
															<td colspan="3" class="noteInfo" style="padding-left: 25px">
																<s:text name="config.mgmtservice.enablesyncvlan.note"/></td>
														</tr>
														<!-- added for sync valnid end-->
														<!-- added for capture data start-->
														<tr>
															<td colspan="3" class="labelT1" style="padding-left: 0"><s:checkbox name="dataSource.enableCaptureDataByCWP"/>
																<s:text name="geneva_09.config.mgmtservice.enablecapturedata.title"/></td>

														</tr>
														<!-- added for capture data end-->
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td><!-- Firewall Policy Global Logging Options -->
						<fieldset><legend><s:text name="config.mgmtservice.option.icsa" /></legend>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td rowspan="2" class="labelH1" valign="middle"><s:text name="config.mgmtservice.option.icsa.log"/></td>
									<td class="labelT1" style="padding: 5px 0 4px;"><s:checkbox name="dataSource.logDroppedPackets"/>
										<s:text name="config.mgmtservice.option.icsa.log.droppedPackets"/></td>
								</tr>
								<tr>
									<td class="labelT1" style="padding: 4px 0 5px;"><s:checkbox name="dataSource.logFirstPackets"/>
										<s:text name="config.mgmtservice.option.icsa.log.firstPackets"/></td>
								</tr>
								<tr>
									<td rowspan="2" class="labelH1" valign="middle"><s:text name="config.mgmtservice.option.icsa.drop"/></td>
									<td class="labelT1" style="padding: 5px 0 4px;"><s:checkbox name="dataSource.dropFragmentedIpPackets"/>
										<s:text name="config.mgmtservice.option.icsa.drop.ipPackets"/></td>
								</tr>
								<tr>
									<td class="labelT1" style="padding: 4px 0 5px;"><s:checkbox name="dataSource.dropNonMgtTraffic"/>
										<s:text name="config.mgmtservice.option.icsa.drop.nonMgtTraffic"/></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td><!-- System Settings -->
						<fieldset><legend><s:text name="config.mgmtservice.system.tag" /></legend>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="labelT1" width="243px"><s:text
										name="config.mgmtservice.userAuth" /></td>
									<td><s:select name="dataSource.userAuth" list="%{userAuthValues}" onchange="changeAuthType(this.options[this.selectedIndex].value)"
							             listKey="key" listValue="value" cssStyle="width: 152px;" /></td>
								</tr>
								<tr style="display:<s:property value="%{displayRadius}"/>" id="radius">
									<td colspan="2">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="243px">
													<s:text name="admin.management.authType" />
												</td>
												<td>
													<s:select name="dataSource.radiusAuthType"
														cssStyle="width: 152px;" list="%{authTypeValues}"
														listKey="key" listValue="value" />
												</td>
											</tr>
											<tr>
												<td class="labelT1"><s:text
													name="config.radiusAssign.serverTab" /></td>
												<td><s:select name="radiusId" cssStyle="width: 152px;"
													list="%{radiusAssignServers}" listKey="id" listValue="value" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newRadius')"><img class="dinl"
														src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editRadius')"><img class="dinl"
														src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="labelT1"><s:text
										name="config.mgmtservice.option.led" /></td>
									<td><s:select name="dataSource.systemLedBrightness" list="%{enumSystemLed}"
							             listKey="key" listValue="value" cssStyle="width: 152px;" />
							             <s:if test="%{!oEMSystem}">
											<s:text name="config.mgmtservice.ledSupport"/>
										 </s:if>
							        </td>
								</tr>
								<tr>
									<td height="5px;"></td>							
								</tr>
								<tr>
									<td></td>
									<td>							             
										<s:if test="%{!oEMSystem}">
											<s:text name="config.mgmtservice.ledSupport.note"/>
										 </s:if>
									</td>
								</tr>
								<tr>
									<td height="5px;"></td>							
								</tr>								
								<tr>
									<td class="labelT1"><s:text
										name="config.mgmtservice.tempAlarmThreshold" /><font color="red"><s:text name="*"/></font></td>
									<td><s:textfield name="dataSource.tempAlarmThreshold" size="12" maxlength="2" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
						            	<s:if test="%{!oEMSystem}">
											<s:text name="config.mgmtservice.tempAlarm.support" />
										 </s:if>
						            </td>
								</tr>
                                <tr>
                                    <td class="labelT1"><s:text
                                        name="config.mgmtservice.fansUnderSpeedAlarmThreshold" /><font color="red"><s:text name="*"/></font></td>
                                    <td><s:textfield name="dataSource.fansUnderSpeedAlarmThreshold" size="12" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
                                        <s:if test="%{!oEMSystem}">
                                            <s:text name="config.mgmtservice.fansUnderSpeedAlarm.support" />
                                         </s:if>
                                    </td>
                                </tr>
								<tr>
									<td class="labelT1"><label><s:text
										name="config.radiusAssign.macAuthFormat" />&nbsp;<s:text name="config.radiusAssign.delimiter" /></label></td>
									<td><s:select name="dataSource.macAuthDelimiter" cssStyle="width: 90px;"
										list="%{enumMacDelimiter}" listKey="key" listValue="value" onchange="changeMacAuth();" />&nbsp;
										<s:text name="config.radiusAssign.style" />&nbsp;<s:select name="dataSource.macAuthStyle" cssStyle="width: 125px;"
										list="%{enumMacStyle}" listKey="key" listValue="value" onchange="changeMacAuth();" />&nbsp;
										<s:text name="config.radiusAssign.case.sensitivity" />&nbsp;<s:select name="dataSource.macAuthCase" cssStyle="width: 100px;"
										list="%{enumMacCase}" listKey="key" listValue="value" onchange="changeMacAuth();" /></td>
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
