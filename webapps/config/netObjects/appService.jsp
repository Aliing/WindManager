<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/widget/tableSort.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/tableSort.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/tabview/assets/skins/sam/tabview.css" includeParams="none" />" />
<style type="text/css">
  .ul {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
    }
    .um {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
    }
    .ur {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
    }
    .ml {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
    }
    .mm {
    height:100%;
    background-color: #f9f9f7;
    }
    .mr {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
    }
    .bl {
    width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
    }
    .bm {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
    }

	.br {
	width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
    }

#newServicePanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}

.innerPage {
	background-color: #f9f9f7;
}

.divcontainer {
	border: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 850px;
	height: 20px;
}

div.container {
	border: 1px solid #666666;
	overflow-x: hidden;
	overflow-y: auto;
	width: 850px;
	height: 400px;
}
table.show {
	width: 840px;
}
.thchk {
	width: 40px;
    background-color: #EEEEEE;
    height: 20px;
    vertical-align: middle;
    padding: 0 3px 0 1px;
}
.thhead{
	width: 200px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    height: 20px;
    padding: 0 3px;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
table.show tr td {
	height: 20px;
}
#system_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 20px;
width: 200px;
padding: 2px 2px 2px 6px;
}
#system_search_groupkey{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 20px;
width: 200px;
padding: 2px 2px 2px 6px;
}
#searchKeyContainerDiv{width: 200px; padding-top: 2px;height:22px;}
#searchGroupKeyContainerDiv{width: 200px; padding-top: 2px;height:22px;}
.searchElement{display: block;float: left;}
</style> 
<script>
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
	initAppAutoSearch();
	initGroupAutoSearch();
}
function initAppAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	<s:iterator value="allAppNames" id="appName">
	appArray[index++] = "<s:property value='appName'/>";
	</s:iterator>
	
	var appDataSource = new YAHOO.util.LocalDataSource(appArray);
	 // Optional to define fields for single-dimensional array
    appDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("system_search_key", "searchKeyContainer", appDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedVal);
    return {
        oDS: appDataSource,
        oAC: oAC
    };
}

function initGroupAutoSearch(){
	// init DataSource
	var groupArray = new Array();
	var index = 0;
	<s:iterator value="allGroupNames" id="groupName">
	groupArray[index++] = "<s:property value='groupName'/>";
	</s:iterator>
	
	var groupDataSource = new YAHOO.util.LocalDataSource(groupArray);
	 // Optional to define fields for single-dimensional array
    groupDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("system_search_groupkey", "searchGroupKeyContainer", groupDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedVal);
    return {
        oDS: groupDataSource,
        oAC: oAC
    };
}

function setSelectedVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName);
}

</script>
<div style="width:100%; height:800px;">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
	</table>
	<div class="yui-navset">
	    <ul class="yui-nav">
	        <li id="systemTab" class="selected"><a href="javascript:void(0);"><em><s:text name="geneva_26.config.system.application.tab"/></em></a></li>
	        <li id="customTab"><a href="javascript:void(0);" onclick="changeTab();"><em><s:text name="geneva_26.config.custom.application.tab"/></em></a></li>
	    </ul>  
	    <div class="yui-content" style="background:#ffffff;">
	        <div id="systemDiv">
			<table>	
				<tr>
					<td>
					<table width="100%">
						<tr>
							<td>
								<table>
								<tr>
									<td style="padding-left:15px;"><input type="button" name="settings" value="Settings"
										class="button" onClick="doSubmitAppServiceSettings();" <s:property value="writeDisabled" />  title="Set application idle timeout" /></td>
								</tr>
								</table>
							</td>
							<td style="padding-left: 330px;">
								<table width="100%">
								<tr>
									<td align="right">
										<input type="radio" onclick="changeService('application');" checked="checked" id="application_radio"/><s:text name="geneva_26.config.system.application.search.application"/>&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="radio" onclick="changeService('group');" id="group_radio"/><s:text name="geneva_26.config.system.application.search.group"/>&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td id="appSearchTd" width="200px">
										<div id="searchKeyContainerDiv">
										<input type="text" id="system_search_key" name="searchKey" onkeydown="javascript:enterOnClick(event);" value="" />
										<div id="searchKeyContainer"></div>
					                    </div>
									</td>
									<td id="groupSearchTd" width="200px" style="display:none;">
										<div id="searchGroupKeyContainerDiv">
										<input type="text" id="system_search_groupkey" name="searchKey" onkeydown="javascript:enterOnClick(event);" value="" />
										<div id="searchGroupKeyContainer"></div>
					                    </div>
									</td>
									<td>
									<div style="width: 40px; padding-left: 5px;">
									<a class="searchElement" href="#search" onclick="appService.searchApps();"> <img class="dinl"
										src="<s:url value="/images/search/Search-WarmGrey11.png" />"
										width="20" height="20" alt="Search" title="Search" /> </a>
									</div>
									</td>
								</tr>
								</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 5px;">
						<table cellspacing="0" cellpadding="0" border="0" width="80%" height="350px">
								<tr>
									<td  style="padding-left:20px;padding-bottom:20px;" align="left" width="40%">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td id="showErrMsg"></td>
												<td>
												  <div class="divcontainer">
													<table id="thead_table_id" class="theadshow">
														<tbody>
														<tr>
															<th class="thchk" align="center"><input type="checkbox" id="checkAll" onclick="checkAll(this)" /></th>
															<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,1,'',this);"><s:text name="geneva_26.config.application.content.application"/></th>
															<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,2,'',this);"><s:text name="geneva_26.config.application.content.group"/></th>
															<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,3,'float',this);"><s:text name="geneva_26.config.application.content.lastday"/></th>
															<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,4,'float',this);"><s:text name="geneva_26.config.application.content.last30days"/></th>
														</tr>
														</tbody>
													</table>
													</div>
													<div class="container">
													<table class="show" id="left_table_id" style="table-layout: fixed;">
														<tbody id="leftServiceTable">
														<s:iterator value="%{allApps}" status="status">
															<tr class="even" serviceId="<s:property value="%{id}"/>" >
																 <td width="40px" align="center" style="vertical-align: middle;padding: 0 3px 0 1px;">
																 <input type="checkbox" name="selectedIds" serviceId="<s:property value="%{id}"/>" />
																 </td>
																 <td width="200px">
																 <%-- <input type="hidden" value="<s:property value="%{description}"/>"> --%>
																 <s:if test="'' != writeDisabled">
																 <s:property value="%{shortName}"/>
																 </s:if>
																 <s:else>
																 <a href="javascript:void(0);" title="<s:property value="%{description}"/>" onclick="changeTimeoutSettings('<s:property value="%{id}"/>')">
																 <s:property value="%{shortName}"/>
																 </a>
																 </s:else>
																 </td>
																 <td  width="200px">
																 <s:property value="%{appGroupName}"/>
																 </td>
																 <td  width="200px" realValue="<s:property value="%{lastDayUsage}"/>">
																 <s:property value="%{lastDayUsageStr}"/>
																 </td>
																 <td  width="200px" realValue="<s:property value="%{lastMonthUsage}"/>">
																 <s:property value="%{lastMonthUsageStr}"/>
																 </td>
															</tr>
														</s:iterator>
														</tbody>
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
			<s:form action="appService" id="appService">
			<s:hidden id="ids" name="ids"></s:hidden>
			<s:hidden name="operation" />
			</s:form>
		</div>
	</div>
</div>
<div id="newServicePanelId" style="display: none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM" style="width:650px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newServicePanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:650px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
</div>
<script>
var newServicePanel = null;
function preparePanels4SelectService() {
	var div = document.getElementById('newServicePanelId');
	newServicePanel = new YAHOO.widget.Panel(div, {
		width:"600px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:3
		});
	newServicePanel.render(document.body);
	div.style.display = "";
}

function fetchSelectServiceNewDlg(value) {
	document.getElementById("ids").value=value;
	var url = "";
	url = "<s:url action='appService' includeParams='none' />?&ignore="+new Date().getTime();
	document.forms["appService"].operation.value = 'settings';
	YAHOO.util.Connect.setForm(document.getElementById("appService"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	set_innerHTML("newServicePanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newServicePanelContentId", showSelectServicePanel, this);
}

var resultDoNothing = function(){}

function showSelectServicePanel(){
	if(null != newServicePanel){
		newServicePanel.cfg.setProperty('visible', true);
		newServicePanel.center();
	}
}

function hideSelectServicePanel(){
	if(null != newServicePanel){
		set_innerHTML("newServicePanelContentId", "");
		newServicePanel.cfg.setProperty('visible', false);
	}
}

YAHOO.util.Event.onContentReady("newServicePanelId", function() {
	preparePanels4SelectService();
}, this);

var appService = appService || {};

appService.selectColor = "#0093D1";

function getDomValue(id) {
	return document.getElementById(id).value;
}

function getDom(id) {
	return document.getElementById(id);
}

String.prototype.startWith = function(str){     
	var reg=new RegExp("^"+str);     
	return reg.test(this);        
}

appService.isAppSelected = function(currentObj) {
	if (currentObj.style.backgroundColor != "") {
		return true;
	} else {
		return false;
	}
}

appService.selectAppOrGroup = function(currentObj) {
	if (currentObj.style.backgroundColor != "") {
		currentObj.style.backgroundColor = "";
	} else {
		currentObj.style.backgroundColor = appService.selectColor;
	}
}

appService.selectApp = function(currentObj) {
	currentObj.style.backgroundColor = appService.selectColor;
}

appService.unSelectApp = function(currentObj) {
	currentObj.style.backgroundColor = "";
}
appService.hiddenApp = function(currentObj) {
	currentObj.style.display = "none";
}

appService.showApp = function(currentObj) {
	currentObj.style.display = "";
}

appService.showAllApp = function(currentObj) {
	var trs = getDom('leftServiceTable').getElementsByTagName('tr');
	for (var i = 0; i < trs.length; i++) {
		trs[i].style.display = "";
	}
}

appService.filterApps = function(value) {
	var keywords = value;
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp();
		return;
	}
	keywords = keywords.toUpperCase();
	var tableObj = getDom('leftServiceTable');
	var trs = tableObj.getElementsByTagName('tr');
	var appRadioCheck = getDom("application_radio").checked;
	if(trs.length > 0){
		var appName;
		for (var i = 0; i < trs.length; i++) {
				if(appRadioCheck){
					appName = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
				}else{
					appName = trs[i].getElementsByTagName('td')[2].innerHTML.trim().toUpperCase();
				}
				if (appName.startWith(keywords)) {
					this.showApp(trs[i]);
				} else {
					this.hiddenApp(trs[i]);
				}
		}
	}
}

appService.searchApps = function() {
	var appRadioCheck = getDom("application_radio").checked;
	var keywords;
	if(appRadioCheck){
	    keywords = getDomValue("system_search_key");
	}else{
		keywords = getDomValue("system_search_groupkey");
	}
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp();
		return;
	}
	keywords = keywords.toUpperCase();
	var tableObj = getDom('leftServiceTable');
	var trs = tableObj.getElementsByTagName('tr');
	
	if(trs.length > 0){
		var appName;
		for (var i = 0; i < trs.length; i++) {
				if(appRadioCheck){
					appName = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
				}else{
					appName = trs[i].getElementsByTagName('td')[2].innerHTML.trim().toUpperCase();
				}
				if (appName.startWith(keywords)) {
					this.showApp(trs[i]);
				} else {
					this.hiddenApp(trs[i]);
				}
		}
	}
}

function enterOnClick(event){
	var keycode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if(keycode == 13){
		appService.searchApps();
		return false;
	}	
}

appService.showAppDescription = function(e, tdObj) {
	showInfoDialog(tdObj.parentNode.getElementsByTagName("input")[0].value || " ");
	hm.util.stopBubble(e);
}


function doSubmitAppServiceSettings() {
	var selectedNetworkServiceIds = "";
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
    		var serviceId = cb.getAttribute("serviceId");
			if (cb.checked) {
				if (serviceId != null && serviceId != "" && serviceId != "undefined") {
					selectedNetworkServiceIds = selectedNetworkServiceIds + serviceId + ",";
				}
			}
		}
	}
	if (selectedNetworkServiceIds.length > 0) {
		selectedNetworkServiceIds = selectedNetworkServiceIds.substring(0, selectedNetworkServiceIds.length - 1);
		fetchSelectServiceNewDlg(selectedNetworkServiceIds);
	}else{
		var rightAppDiv = getDom('showErrMsg');
		hm.util.reportFieldError(rightAppDiv, 'Please select at least one application service.');
		rightAppDiv.focus();
    	return;
	}
}

function changeService(service){
	if (service == 'application') {
			getDom("application_radio").checked = true;
			getDom("group_radio").checked = false;
			getDom("groupSearchTd").style.display = "none";
			getDom("appSearchTd").style.display = "";
			getDom("system_search_key").value = "";
	} else {
		getDom("application_radio").checked = false;
		getDom("group_radio").checked = true;
		getDom("groupSearchTd").style.display = "";
		getDom("appSearchTd").style.display = "none";
		getDom("system_search_groupkey").value = "";
	}
}

function changeTab(){
	if(waitingPanel != null){
		waitingPanel.show();
	}
	window.location.href = "<s:url action='customApp' includeParams='none' />";
}

function changeTimeoutSettings(id){
	fetchSelectServiceNewDlg(id);
}

function checkAll(checkAll) {
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (cb.checked != checkAll.checked && cb.parentNode.parentNode.style.display != 'none' ) {
				cb.checked = checkAll.checked;
			}
		}
	}
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
	waitingPanel.setHeader("Preparing resources...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}
</script>