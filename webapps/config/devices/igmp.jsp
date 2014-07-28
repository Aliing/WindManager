<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div>
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
<link rel="stylesheet" type="text/css" href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />


<script type="text/javascript">
function showIgmpSetting(){
	showHideContent("igmpDiv", "");
}
function validateIgmp(operation){
	if(operation == 'create2' || operation == 'update2'){
		if(Get(formName + "_dataSource_overrideIgmpSnooping").checked){
			if(!validateIgmpValue()){
				return false;
			}
		}else{
			var globalDelayLeaveQueryInterval = document.getElementById(formName+'_dataSource_globalDelayLeaveQueryInterval');
			var globalDelayLeaveQueryCount = document.getElementById(formName+'_dataSource_globalDelayLeaveQueryCount');
			var globalRouterPortAginTime = document.getElementById(formName+'_dataSource_globalRouterPortAginTime');
			var globalRobustnessCount = document.getElementById(formName+'_dataSource_globalRobustnessCount');
			if(globalDelayLeaveQueryInterval && globalDelayLeaveQueryInterval.value.length == 0){
				globalDelayLeaveQueryInterval.value = 1;
			}
			if(globalDelayLeaveQueryCount && globalDelayLeaveQueryCount.value.length == 0){
				globalDelayLeaveQueryCount.value = 2;
			}
			if(globalRouterPortAginTime && globalRouterPortAginTime.value.length == 0){
				globalRouterPortAginTime.value = 250;
			}
			if(globalRobustnessCount && globalRobustnessCount.value.length == 0){
				globalRobustnessCount.value = 2;
			}
		}
		return true;
	}
	return true;
}


function validateIgmpValue(){
	var globalDelayLeaveQueryInterval = document.getElementById(formName+'_dataSource_globalDelayLeaveQueryInterval');
	var globalDelayLeaveQueryCount = document.getElementById(formName+'_dataSource_globalDelayLeaveQueryCount');
	var globalRouterPortAginTime = document.getElementById(formName+'_dataSource_globalRouterPortAginTime');
	var globalRobustnessCount = document.getElementById(formName+'_dataSource_globalRobustnessCount');
	if(globalDelayLeaveQueryInterval){
		if(globalDelayLeaveQueryInterval.value.length != 0){
			var message = hm.util.validateIntegerRange(globalDelayLeaveQueryInterval.value, '<s:text name="config.switchSettings.igmp.delay.leave.interval" />',
	                <s:property value="1" />,
	                <s:property value="25" />);
			if (message != null) {
			hm.util.reportFieldError(globalDelayLeaveQueryInterval, message);
			showIgmpSetting();
			globalDelayLeaveQueryInterval.focus();
			return false;
			}
		}else{
			hm.util.reportFieldError(globalDelayLeaveQueryInterval, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.interval" /></s:param></s:text>');
			showIgmpSetting();
			globalDelayLeaveQueryInterval.focus();
			return false;
		}
	}
	if(globalDelayLeaveQueryCount){
		if(globalDelayLeaveQueryCount.value.length != 0){
			var message = hm.util.validateIntegerRange(globalDelayLeaveQueryCount.value, '<s:text name="config.switchSettings.igmp.delay.leave.count" />',
	                <s:property value="1" />,
	                <s:property value="7" />);
			if (message != null) {
			hm.util.reportFieldError(globalDelayLeaveQueryCount, message);
			showIgmpSetting();
			globalDelayLeaveQueryCount.focus();
			return false;
			}
		}else{
			hm.util.reportFieldError(globalDelayLeaveQueryCount, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.count" /></s:param></s:text>');
			showIgmpSetting();
			globalDelayLeaveQueryCount.focus();
			return false;
		}
	}
	if(globalRouterPortAginTime){
		if(globalRouterPortAginTime.value.length != 0){
			var message = hm.util.validateIntegerRange(globalRouterPortAginTime.value, '<s:text name="config.switchSettings.igmp.router.aging.time" />',
	                <s:property value="30" />,
	                <s:property value="1000" />);
			if (message != null) {
			hm.util.reportFieldError(globalRouterPortAginTime, message);
			showIgmpSetting();
			globalRouterPortAginTime.focus();
			return false;
			}
		}else{
			hm.util.reportFieldError(globalRouterPortAginTime, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.router.aging.time" /></s:param></s:text>');
			showIgmpSetting();
			globalRouterPortAginTime.focus();
			return false;
		}
	}
	if(globalRobustnessCount){
		if(globalRobustnessCount.value.length != 0){
			var message = hm.util.validateIntegerRange(globalRobustnessCount.value, '<s:text name="config.switchSettings.igmp.robustness.count" />',
	                <s:property value="1" />,
	                <s:property value="3" />);
			if (message != null) {
			hm.util.reportFieldError(globalRobustnessCount, message);
			showIgmpSetting();
			globalRobustnessCount.focus();
			return false;
			}
		}else{
			hm.util.reportFieldError(globalRobustnessCount, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.robustness.count" /></s:param></s:text>');
			showIgmpSetting();
			globalRobustnessCount.focus();
			return false;
		}
	}
	return true;
}

var ahDataTableIgmp;
function validateDelayLeaveQueryInterval(data){
	var inputElement = data;
	if (inputElement.length != 0) {
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.delay.leave.interval" />',
                1,
                 25);
		if (message != null) {
		hm.util.reportFieldError(document.getElementById("igmpTableError"), message);
		return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("igmpTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.interval" /></s:param></s:text>');
		return false;
	}

	return true;
}

function validateDelayLeaveQueryCount(data){
	var inputElement = data;
	if (inputElement.length != 0) {
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.delay.leave.count" />',
                1,
                7);
		if (message != null) {
		hm.util.reportFieldError(document.getElementById("igmpTableError"), message);
		return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("igmpTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.delay.leave.count" /></s:param></s:text>');
		return false;
	}

	return true;
}

function validateRouterPortAgingTime(data){
	var inputElement = data;
	if (inputElement.length != 0) {
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.router.aging.time" />',
                30,
                1000);
		if (message != null) {
		hm.util.reportFieldError(document.getElementById("igmpTableError"), message);
		return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("igmpTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.router.aging.time" /></s:param></s:text>');
		return false;
	}

	return true;
}

function validateRobustnessCount(data){
	var inputElement = data;
	if (inputElement.length != 0) {
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.robustness.count" />',
                1,
                3);
		if (message != null) {
		hm.util.reportFieldError(document.getElementById("igmpTableError"), message);
		return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("igmpTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.robustness.count" /></s:param></s:text>');
		return false;
	}

	return true;
}

function validateIgmpVlanId(data){
	var inputElement = data;
	if(inputElement.length != 0){
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.vlan.id" />',
	            1,4094);
		if (message != null) {
			hm.util.reportFieldError(document.getElementById("igmpTableError"), message);
			return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("igmpTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.vlan.id" /></s:param></s:text>');
		return false;
	}

	return true;
}
function onLoadAhIgmpDataTable() {
	var dataSourceIgmp = eval('<s:property escape="false" value="ahIgmpDtDatas"/>');
	var ahIgmpDtClumnDefs = eval('<s:property escape="false" value="ahIgmpDtClumnDefs"/>');
	var editInfo = "<s:property  escape='false' value='igmpEditInfo'/>";
	if (editInfo) {
		eval("var ahDtEditInfo = " + "<s:property  escape='false' value='igmpEditInfo'/>");
	}else {
		var ahDtEditInfo;
	}

    var myConfigs = {
  		editInfo:{
  			name:"igmpEditInfo",
  		}
    }

    $.extend(true, myConfigs, ahDtEditInfo)
    var myColumnDefs = [
		{
			type: "text",
			mark: "igmpVlanIds",
			editMark:"igmpVlanId_edit",
			display: '<s:text name="config.switchSettings.igmp.vlan.id" /> <s:text name="config.switchSettings.igmp.vlan.id.range"/>',
			defaultValue: "",
			width:"50px",
			validate:validateIgmpVlanId,
			maxlength:4
		},
		{
			type: "checkbox",
			mark: "igmpSnoopings",
			editMark:"igmpSnooping_edit",
			display: '<s:text name="config.switchSettings.igmp.snooping" />',
			defaultValue: true,
			width:"100px"
		},
		{
			type: "checkbox",
			mark: "immediateLeaves",
			editMark:"immediateLeave_edit",
			display: '<s:text name="config.switchSettings.igmp.immediate.leave" />',
			defaultValue: false,
			width:"100px"
		},
		{
			type: "text",
			mark: "delayLeaveQueryIntervals",
			editMark:"delayLeaveQueryInterval_edit",
			display: '<s:text name="config.switchSettings.igmp.delay.leave.interval" /> <s:text name="config.switchSettings.igmp.delay.leave.interval.range"/>',
			defaultValue: "1",
			width:"150px",
			validate:validateDelayLeaveQueryInterval,
			maxlength:2
		},
		{
			type: "text",
			mark: "delayLeaveQueryCounts",
			editMark:"delayLeaveQueryCount_edit",
			display: '<s:text name="config.switchSettings.igmp.delay.leave.count" /> <s:text name="config.switchSettings.igmp.delay.leave.count.range"/>',
			defaultValue: 2,
			width:"150px",
			validate:validateDelayLeaveQueryCount,
			maxlength:1
		},
		{
			type: "text",
			mark: "routerPortAginTimes",
			editMark:"routerPortAginTime_edit",
			display: '<s:text name="config.switchSettings.igmp.router.aging.time" /> <s:text name="config.switchSettings.igmp.router.aging.time.range"/>',
			defaultValue: "250",
			width:"150px",
			validate:validateRouterPortAgingTime,
			maxlength:4
		},
		{
			type: "text",
			mark: "robustnessCounts",
			editMark:"robustnessCount_edit",
			display: '<s:text name="config.switchSettings.igmp.robustness.count" /> <s:text name="config.switchSettings.igmp.robustness.count.range"/>',
			defaultValue: "2",
			width:"100px",
			validate:validateRobustnessCount,
			maxlength:1
		}
	];

	var myColumns = [];
	for (var i = 0; i < myColumnDefs.length; i++) {
		var optionTmp = myColumnDefs[i];
		var bln = false;
		if(ahIgmpDtClumnDefs){
			for (var j = 0; j < ahDtClumnDefs.length; j++) {
				if (myColumnDefs[i].mark == ahDtClumnDefs[j].mark) {
					optionTmp = $.extend(true, optionTmp, ahDtClumnDefs[j]);
					myColumns.push(optionTmp);
					bln = true;
					break;
				}
			}
		}
		if(!bln){
			myColumns.push(optionTmp);
		}

	}
    ahDataTableIgmp = new AhDataTablePanel.DataTablePanel("ahDataTableIgmp",myColumns,dataSourceIgmp,myConfigs);
    ahDataTableIgmp.render();
}

var ahDataTableMulticastGroup;

function validateMulticastGroupVlanId(data){
	var inputElement = data;
	if(inputElement.length != 0){
		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.vlan.id" />',
                   1,4094);
		if (message != null) {
			hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), message);
			return false;
		}
	}else{
		hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.vlan.id" /></s:param></s:text>');
		return false;
	}
	return true;
}
function validateMulticastGroupIpAddress(data){
	if (data.length == 0) {
		hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.multicastgroup.settings.ipaddress" /></s:param></s:text>');
		return false;
  	}
	if (! hm.util.validateIpAddressExtension(data)) {
		hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), '<s:text name="error.formatInvalid"><s:param><s:text name="config.switchSettings.multicastgroup.settings.ipaddress" /></s:param></s:text>');
		return false;
	}else{
		var ip_item = [];
		ip_item = data.split(".");
		if(((224 == ip_item[0]) && (0 == ip_item[1]) && (0 == ip_item[2])) || 224 > ip_item[0] || 239 < ip_item[0]){
			hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), '<s:text name="config.switchSettings.igmp.multicastgroup.ipaddress.range"><s:param><s:text name="config.switchSettings.multicastgroup.settings.ipaddress" /></s:param></s:text>');
			return false;
		}
	}
	return true;
}
function validateInterface(data){
	if (data.length == 0) {
		hm.util.reportFieldError(document.getElementById("multicastGroupTableError"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.multicastgroup.settings.interface" /></s:param></s:text>');
		return false;
  	}
	return true;
}
function onLoadAhMulticastGroupDataTable() {
	var dataSourceMulticastGroup = eval('<s:property escape="false" value="ahMulticastGroupDtDatas"/>');
	var ahMulticastGroupDtClumnDefs = eval('<s:property escape="false" value="ahMulticastGroupDtClumnDefs"/>');
	var editInfo = "<s:property  escape='false' value='multicastGroupEditInfo'/>";
	if (editInfo) {
		eval("var ahDtEditInfo = " + "<s:property  escape='false' value='multicastGroupEditInfo'/>");
	}else {
		var ahDtEditInfo;
	}

    var myConfigs = {
  		editInfo:{
  			name:"multicastGroupEditInfo",
  		}
    }

    $.extend(true, myConfigs, ahDtEditInfo)
    var myColumnDefs = [
		{
			type: "text",
			mark: "multicastGroupVlanIds",
			editMark:"multicastGroupVlanId_edit",
			display: '<s:text name="config.switchSettings.igmp.vlan.id" /> <s:text name="config.switchSettings.igmp.vlan.id.range"/>',
			defaultValue: "",
			width:"100px",
			validate:validateMulticastGroupVlanId,
			maxlength:4
		},
		{
			type: "text",
			mark: "multicastGroupIpAddresses",
			editMark:"multicastGroupIpAddress_edit",
			display: '<s:text name="config.switchSettings.multicastgroup.settings.ipaddress" />',
			defaultValue: "",
			width:"200px",
			validate:validateMulticastGroupIpAddress,
			maxlength:15
		},
		{
			type: "dropdownMultiple",
			mark: "multicastGroupInterfaces",
			editMark:"multicastGroupInterface_edit",
			display: '<s:text name="config.switchSettings.multicastgroup.settings.interface" />',
			defaultValue: "",
			validate:validateInterface,
			width:"100px"
		}
	];

	var myColumns = [];
	for (var i = 0; i < myColumnDefs.length; i++) {
		var optionTmp = myColumnDefs[i];
		var bln = false;
		if(ahMulticastGroupDtClumnDefs){
			for (var j = 0; j < ahMulticastGroupDtClumnDefs.length; j++) {
				if (myColumnDefs[i].mark == ahMulticastGroupDtClumnDefs[j].mark) {
					optionTmp = $.extend(true, optionTmp, ahMulticastGroupDtClumnDefs[j]);
					myColumns.push(optionTmp);
					bln = true;
					break;
				}
			}
		}
		if(!bln){
			myColumns.push(optionTmp);
		}

	}
	ahDataTableMulticastGroup = new AhDataTablePanel.DataTablePanel("ahDataTableMulticastGroup",myColumns,dataSourceMulticastGroup,myConfigs);
	ahDataTableMulticastGroup.render();
}

function enabledOverrideIgmpSnoopingCheckBox(value){
	if (value) {
		document.getElementById("global_igmp").style.display= "";
	} else {
		document.getElementById("global_igmp").style.display= "none";
	}
}

function showHideDefaultIgmpSettingsDiv(value) {
	if (value == 1) {
		document.getElementById("hideDefaultIGMPSettingsDiv").style.display = "none";
		document.getElementById("showDefaultIGMPSettingsDiv").style.display = "block";
	}
	if (value == 2) {
		document.getElementById("hideDefaultIGMPSettingsDiv").style.display = "block";
		document.getElementById("showDefaultIGMPSettingsDiv").style.display = "none";
	}
}

function showHideIndividualVLANDiv(value) {
	if (value == 1) {
		document.getElementById("hideIndividualVLANDiv").style.display = "none";
		document.getElementById("showIndividualVLANDiv").style.display = "block";
	}
	if (value == 2) {
		document.getElementById("hideIndividualVLANDiv").style.display = "block";
		document.getElementById("showIndividualVLANDiv").style.display = "none";
	}
}

function showHideStaticGroupDiv(value) {
	if (value == 1) {
		document.getElementById("hideStaticGroupDiv").style.display = "none";
		document.getElementById("showStaticGroupDiv").style.display = "block";
	}
	if (value == 2) {
		document.getElementById("hideStaticGroupDiv").style.display = "block";
		document.getElementById("showStaticGroupDiv").style.display = "none";
	}
}
</script>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.switchSettings.igmp.settings" />','igmpDiv');</script></td>
		</tr>
		<tr>
			<td>
				<table id="igmpDiv" style="padding-left: 15px;display: <s:property value="%{dataSource.igmpDivStyle}"/>" cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
							<s:checkbox onclick="enabledOverrideIgmpSnoopingCheckBox(this.checked);"
								name="dataSource.overrideIgmpSnooping" /> <s:text
								name="config.switchSettings.igmp.snooping.settings.override"></s:text>
						</td>
					</tr>
					<tr>
					<td id="global_igmp" style="padding-left: 22px;display: <s:property value="%{enabledOverrideIgmpMode}"/>">
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<div id="hideDefaultIGMPSettingsDiv">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1"
													onclick="showHideDefaultIgmpSettingsDiv(1);"
													style="cursor: pointer;">
													<img style="display:inline;"
													src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg"/>&nbsp;
													<s:text name="config.switchSettings.igmp.settings.default"></s:text>
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div id="showDefaultIGMPSettingsDiv" style="display:none;">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1"
														onclick="showHideDefaultIgmpSettingsDiv(2);"
														style="cursor: pointer;"><img style="display:inline;"
														src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
														alt="Hide Option" class="expandImg" />&nbsp;
														<s:text name="config.switchSettings.igmp.settings.default"></s:text></td>
												</tr>
												<tr>
													<td style="padding-left: 22px;">
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1">
																	<s:checkbox
																		name="dataSource.enableImmediateLeave"/> <s:text
																		name="config.switchSettings.igmp.immediate.leave"></s:text>
																</td>
																<td style="padding-left:50px;" class="labelT1">
																	<s:checkbox
																		name="dataSource.enableReportSuppression"/> <s:text
																		name="config.switchSettings.igmp.report.suppression"></s:text>
																</td>
															</tr>
															<tr>
																<td width="200px" class="labelT1">
																	<s:text name="config.switchSettings.igmp.delay.leave.interval"></s:text>
																</td>
																<td class="labelT1">
																<s:textfield name="dataSource.globalDelayLeaveQueryInterval" maxlength="2"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																<s:text name="config.switchSettings.igmp.delay.leave.interval.range"></s:text>
																</td>
															</tr>
															<tr>
																<td class="labelT1">
																	<s:text name="config.switchSettings.igmp.delay.leave.count"></s:text>
																</td>
																<td class="labelT1">
																<s:textfield name="dataSource.globalDelayLeaveQueryCount" maxlength="1"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																<s:text name="config.switchSettings.igmp.delay.leave.count.range"></s:text>
																</td>
															</tr>
															<tr>
																<td class="labelT1">
																	<s:text name="config.switchSettings.igmp.router.aging.time"></s:text>
																</td>
																<td class="labelT1">
																<s:textfield name="dataSource.globalRouterPortAginTime" maxlength="4"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																<s:text name="config.switchSettings.igmp.router.aging.time.range"></s:text>
																</td>
															</tr>
															<tr>
																<td class="labelT1">
																	<s:text name="config.switchSettings.igmp.robustness.count"></s:text>
																</td>
																<td class="labelT1">
																<s:textfield name="dataSource.globalRobustnessCount" maxlength="1"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																<s:text name="config.switchSettings.igmp.robustness.count.range"></s:text>
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
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<div id="hideIndividualVLANDiv">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1"
													onclick="showHideIndividualVLANDiv(1);"
													style="cursor: pointer;">
													<img style="display:inline;"
													src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg"/>&nbsp;
													<s:text name="config.switchSettings.igmp.settings.config" />
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div id="showIndividualVLANDiv" style="display:none;">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1"
													onclick="showHideIndividualVLANDiv(2);"
													style="cursor: pointer;"><img style="display:inline;"
													src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
													alt="Hide Option" class="expandImg" />&nbsp;
													<s:text name="config.switchSettings.igmp.settings.config" /></td>
											</tr>
											<tr>
												<td style="padding-left: 22px;">
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
															<label id="igmpTableError"></label>
															</td>
															<td>
															<div id="ahDataTableIgmp"></div>
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
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<div id="hideStaticGroupDiv">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1"
													onclick="showHideStaticGroupDiv(1);"
													style="cursor: pointer;">
													<img style="display:inline;"
													src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
													alt="Show Option" class="expandImg"/>&nbsp;
													<s:text name="config.switchSettings.multicastgroup.settings.config" />
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div id="showStaticGroupDiv" style="display:none;">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1"
													onclick="showHideStaticGroupDiv(2);"
													style="cursor: pointer;"><img style="display:inline;"
													src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
													alt="Hide Option" class="expandImg" />&nbsp;
													<s:text name="config.switchSettings.multicastgroup.settings.config" /></td>
											</tr>
											<tr>
												<td style="padding-left: 22px;">
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
															<label id="multicastGroupTableError"></label>
															</td>
															<td>
															<div id="ahDataTableMulticastGroup"></div>
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
					</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:if>
</div>