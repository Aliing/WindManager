<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
var filterFormName = 'activeClientFilter';
var filterChangedType=1;
function submitFilterAction(operation) {
	if (validateFilter(operation)) {
		document.forms[filterFormName].operation.value = operation;
    	document.forms[filterFormName].submit();
    }
}
function validateFilter(operation) {
	if (operation=='removeFilter') {
		if (document.getElementById("filterSelect").value == '-1'){
			return false;
		}
	}
	
	return true;
}

var filterOverlay = null;
function createFilterOverlay() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	filterOverlay.render(document.body);
	div.style.display = "";
	overlayManager.register(filterOverlay);
	filterOverlay.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openFilterOverlay(){
	if(null == filterOverlay){
		createFilterOverlay();
	}
	initialValues();
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}

function provisionFlagClick(checked){
	var provision_el = document.getElementById(filterFormName + "_filterProvision");
	provision_el.disabled = !checked;
}
function filterChanged(value,changeype){
	filterChangedType=changeype;
	if(value == -1){
		submitFilterAction('view');
	}else{
		url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=requestFilterValues&filter=" + encodeURIComponent(value) + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:filterResult,failure:connectedFailed,timeout: 60000}, null);
	}
}

function editFilterOverlay(){
	filterChanged(document.getElementById("filterSelect").value, 2);
}

var filterResult = function(o) {
	eval("var result = " + o.responseText);
	if(typeof result.fname != 'undefined'){
		document.getElementById("filterName").value = result.fname;
		Get("filterNameCheckBox").checked=true;
		document.getElementById("filterName").readOnly=false;
	} else {
		Get("filterNameCheckBox").checked=false;
		document.getElementById("filterName").value = '';
		document.getElementById("filterName").readOnly=true;
	}
	if(typeof result.fApName != 'undefined'){
		document.getElementById("filterApName").value = result.fApName;
	}else{
		document.getElementById("filterApName").value = "";
	}
	if(typeof result.fClientMac != 'undefined'){
		document.getElementById("filterClientMac").value = result.fClientMac;
	}else{
		document.getElementById("filterClientMac").value = "";
	}
	if(typeof result.fClientIP != 'undefined'){
		document.getElementById("filterClientIP").value = result.fClientIP;
	}else{
		document.getElementById("filterClientIP").value = "";
	}
	if(typeof result.fClientHostName != 'undefined'){
		document.getElementById("filterClientHostName").value = result.fClientHostName;
	}else{
		document.getElementById("filterClientHostName").value = "";
	}
	if(typeof result.fClientUserName != 'undefined'){
		document.getElementById("filterClientUserName").value = result.fClientUserName;
	}else{
		document.getElementById("filterClientUserName").value = "";
	}
	
	if(typeof result.fOverallClientHealth != 'undefined' && !isNaN(result.fOverallClientHealth) && result.fOverallClientHealth>=0){
		document.getElementById("filterOverallClientHealth").value = result.fOverallClientHealth;
	}else{
		document.getElementById("filterOverallClientHealth").value = "";
	}
	if(typeof result.fClientOsInfo != 'undefined'){
		document.getElementById("filterClientOsInfo").value = result.fClientOsInfo;
	}else{
		document.getElementById("filterClientOsInfo").value = "";
	}
	if(typeof result.fClientVLAN != 'undefined' && !isNaN(result.fClientVLAN) && result.fClientVLAN>=0){
		document.getElementById("filterClientVLAN").value = result.fClientVLAN;
	}else{
		document.getElementById("filterClientVLAN").value = "";
	}
	if(typeof result.fClientUserProfId != 'undefined' && !isNaN(result.fClientUserProfId) && result.fClientUserProfId>=0){
		document.getElementById("filterClientUserProfId").value = result.fClientUserProfId;
	}else{
		document.getElementById("filterClientUserProfId").value = "";
	}
	if(typeof result.fClientChannel != 'undefined' && !isNaN(result.fClientChannel) && result.fClientChannel>=0){
		document.getElementById("filterClientChannel").value = result.fClientChannel;
	}else{
		document.getElementById("filterClientChannel").value = "";
	}
	
	if(result.fMap){
		document.getElementById("filterMap").value = result.fMap;
	}
	if (filterChangedType ==1) {
		submitFilterAction('search');
	} else {
		if(null == filterOverlay){
			createFilterOverlay();
		}
		filterOverlay.cfg.setProperty('visible', true);
	}
}

var connectedFailed = function(o) {
	//
}

function initialValues(){
	document.getElementById("filterName").value = '';
	Get("filterNameCheckBox").checked=false;
	document.getElementById("filterName").readOnly=true;
	
	document.getElementById("filterApName").value = '';
	document.getElementById("filterClientMac").value = '';
	document.getElementById("filterClientIP").value = '';
	document.getElementById("filterClientHostName").value = '';
	document.getElementById("filterClientUserName").value = '';
	document.getElementById("filterOverallClientHealth").value = '';
	document.getElementById("filterClientOsInfo").value = '';
	document.getElementById("filterClientVLAN").value = '';
	document.getElementById("filterClientUserProfId").value = '';
	document.getElementById("filterClientChannel").value = '';
	
	document.getElementById("filterMap").selectedIndex = 0;
}

function filterNameCheckBoxClick() {
	if (Get("filterNameCheckBox").checked) {
		document.getElementById("filterName").value = '';
		document.getElementById("filterName").readOnly=false;
	} else {
		document.getElementById("filterName").value = '';
		document.getElementById("filterName").readOnly=true;
	}

}

</script>
<div id="leftFilter">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="filterSep" colspan="2">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td class="sepLine">
							<img src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="filterH1">
				Filter
			</td>
			<td>
				<s:select id="filterSelect" name="filter" headerKey="-1"
					headerValue="None" list="filterList" cssStyle="width:100px;"
					onchange="filterChanged(this.value, 1);" />
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td colspan="2">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding-left: 50px;">
							<a class="marginBtn" href="javascript:openFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:editFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:submitFilterAction('removeFilter');">
								<img class="dinl" src="<s:url value="/images/cancel.png" />"
								width="16" height="16" alt="Remove" title="Remove" /></a>
						</td>
						
						<%-- td class="filterBtn" style="padding-left: 9px;">
							<input type="button" name="ignore" value="New" class="button"
								onClick="openFilterOverlay();">
						<td class="filterBtn" style="padding-left: 0px;" width="100%">
							<input type="button" name="ignore" value="Remove" class="button"
								onClick="submitFilterAction('removeFilter');">
								--%>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="filterPanel" style="display: none;">
		<div class="hd">
			Filter Active Clients By
		</div>
		<div class="bd">
			<s:form action="clientMonitor" id="activeClientFilter"
				name="activeClientFilter">
				<s:hidden name="operation" />
				<s:hidden name="listType" />
				<s:hidden name="blnInnerSearch" value="true" />
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td colspan="2">
							<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<td style="padding: 6px 5px 5px 5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" width="136px">
													<s:text name="monitor.activeClient.clientMac" />
												</td>
												<td>
													<s:textfield id="filterClientMac" name="filterClientMac"
														maxlength="12" size="24"
														onkeypress="return hm.util.keyPressPermit(event,'hex');" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientIP" />
												</td>
												<td>
													<s:textfield id="filterClientIP" name="filterClientIP"
														maxlength="15" size="24"
														onkeypress="return hm.util.keyPressPermit(event,'ip');" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientHostName" />
												</td>
												<td>
													<s:textfield id="filterClientHostName"
														name="filterClientHostName" maxlength="32" size="24" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientUserName" />
												</td>
												<td>
													<s:textfield id="filterClientUserName"
														name="filterClientUserName" maxlength="32" size="24" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.apName" />
												</td>
												<td>
													<s:textfield id="filterApName" name="filterApName"
														maxlength="32" size="24" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.map" />
												</td>
												<td>
													<s:select id="filterMap" name="filterMap" headerKey="-2"
														headerValue="All" list="filterMaps" listKey="id"
														listValue="value" cssStyle="width:152px;" />
												</td>
											</tr>
											<!--Added for Dakar begin -->
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.overallClientHealth" />
												</td>
												<td>
													<s:textfield  id="filterOverallClientHealth" name="filterOverallClientHealth"
														maxlength="3" size="24" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientOsinfo" />
												</td>
												<td>
													<s:textfield id="filterClientOsInfo" name="filterClientOsInfo"
														maxlength="32" size="24" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.filterClientVLAN" />
												</td>
												<td>
													<s:textfield  id="filterClientVLAN" name="filterClientVLAN"
														maxlength="4" size="24" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientUserProfId" />
												</td>
												<td>
													<s:textfield  id="filterClientUserProfId" name="filterClientUserProfId"
														maxlength="4" size="24" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												</td>
											</tr>
											<tr>
												<td class="labelT1">
													<s:text name="monitor.activeClient.clientChannel" />
												</td>
												<td>
													<s:textfield  id="filterClientChannel" name="filterClientChannel"
														maxlength="3" size="24" onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												</td>
											</tr>
											<!--Added for Dakar end -->
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
						<td colspan="2">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><s:checkbox name="filterNameCheckBox" id="filterNameCheckBox" onclick="filterNameCheckBoxClick();"></s:checkbox> </td>
									<td class="labelT1" width="140px" >
										<s:text name="monitor.activeClient.filter.name" />
									</td>
									<td>
										<s:textfield id="filterName" name="filterName" maxlength="32"
											size="24" readonly="true"/>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding-top: 8px;" colspan="2">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="button" name="ignore" value="Search" id="search"
											class="button" onClick="submitFilterAction('search');">
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
</div>
