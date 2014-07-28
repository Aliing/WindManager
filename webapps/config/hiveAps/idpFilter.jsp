<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
var filterFormName = 'idpFilter';
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
		document.getElementById(filterFormName+"_bssid").value ="";
	}
	return true;
}

var filterOverlay = null;
function createFilterOverlay() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"370px",
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
	//initial textfiled value;
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
function filterChanged(value,changeype){
	filterChangedType=changeype;
//	alert(value);
	if(value == -1){
		document.getElementById(filterFormName+"_bssid").value = '';
		submitFilterAction('switch');
	}else{
		url = "<s:url action='idp' includeParams='none' />" + "?operation=requestFilter&filterName=" + encodeURIComponent(value) + "&ignore=" + new Date().getTime();
//		alert(url);
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:filterResult,failure:connectedFailed,timeout: 60000}, null);
	}
}

function editFilterOverlay(){
	filterChanged(document.getElementById("filterSelect").value, 2);
}

var filterResult = function(o) {
	eval("var result = " + o.responseText);
	if(result.name){
		document.getElementById(filterFormName+"_filterName").value = result.name;
	}
	if(result.bssid){
		document.getElementById(filterFormName+"_bssid").value = result.bssid;
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
	document.getElementById(filterFormName+"_filterName").value = '';
	document.getElementById(filterFormName+"_bssid").value = '';
}

</script>
<div id="leftFilter">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="filterSep" colspan="2">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="sepLine"><img
						src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="filterH1">Filter </td>
			<td><s:select name="filter" headerKey="-1" headerValue="None" id="filterSelect"
				list="filterList" cssStyle="width:100px;" onchange="filterChanged(this.value, 1);"/></td>
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
						<%--<td class="filterBtn" style="padding-left: 9px;"><input type="button"
							name="ignore" value="New" class="button" style="width: 76px;"
							onClick="openFilterOverlay();"></td>
						<td class="filterBtn" style="padding-left: 0px;"><input type="button"
							name="ignore" value="Remove" class="button"
							onClick="submitFilterAction('removeFilter');"></td>--%>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="filterPanel" style="display: none;">
	<div class="hd">Filter By</div>
	<div class="bd"><s:form action="idp" id="idpFilter" name="idpFilter">
	<s:hidden name="operation" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td colspan="2">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td style="padding: 6px 0 5px 0;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="130px"><s:property value="filterBssidText"/></td>
								<td><s:textfield name="bssid" maxlength="12" size="24"/></td>
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
			<td class="labelT1" width="135px"><s:text name="hiveAp.filter.name"/></td>
			<td><s:textfield name="filterName" maxlength="20" size="24"/></td>
		</tr>
		<tr>
			<td style="padding-top: 8px;" colspan="2">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Search"
						id="search" class="button" onClick="submitFilterAction('search');"></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button" onClick="hideOverlay();"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table></s:form>
	</div>
	</div>
</div>