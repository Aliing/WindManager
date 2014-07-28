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

#newCustomAppPanelId.yui-panel {
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
	width: 30px;
    background-color: #EEEEEE;
    height: 20px;
    vertical-align: middle;
    padding: 0 3px 0 1px;
}
.thhead{
	width: 270px;
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
#custom_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 20px;
width: 200px;
padding: 2px 2px 2px 6px;
}
#searchKeyContainerDiv{float:left; width: 200px; padding-top: 2px;height:22px;}
.searchElement{display: block;float: left;}
</style> 
<script type="text/javascript">
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
	initAutoSearch();
}
function initAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	var tempstr;
	<s:iterator value="searchCustomAppList" id="customName">
	tempstr = "<s:property value='%{customName}' escape='false'/>";
	tempstr = tempstr.replace(/\\\\/g, "\\");
	tempstr = tempstr.replace(/&amp;/g,"&");
	appArray[index++] = tempstr;
	</s:iterator>
	
	var appDataSource = new YAHOO.util.LocalDataSource(appArray);
	 // Optional to define fields for single-dimensional array
    appDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
     var oAC = new YAHOO.widget.AutoComplete("custom_search_key", "searchKeyContainer", appDataSource);
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
function setSelectedVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.searchApps(showName);
}
</script>
<div style="width:100%; height:800px;">
<s:form action="customApp"  id="customAppList" name="customAppList">
<s:hidden name="selectedAppIds" id="selectedAppIds"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
	</table>
	<div class="yui-navset">
	    <ul class="yui-nav">
	        <li id="systemTab"><a href="javascript:void(0);" onclick="changeTab();"><em><s:text name="geneva_26.config.system.application.tab"/></em></a></li>
	        <li id="customTab" class="selected"><a href="javascript:void(0);"><em><s:text name="geneva_26.config.custom.application.tab"/></em></a></li>
	    </ul>  
	    <div class="yui-content" style="background:#ffffff;">
	        <div id="systemDiv">
				<table>	
					<tr>
						<td style="padding-top: 5px;padding-left:15px;">
						<table width="100%">
							<tr>
								<td>
									<table>
									<tr>
										<td><input type="button" name="add" value="Add"
											class="button" onClick="addCustomApp();" <s:property value="writeDisabled" />></td>
										<td><input type="button" name="delete" value="Delete"
											class="button" onClick="submitAction('remove');" <s:property value="writeDisabled" />></td>
									</tr>
									</table>
								</td>
								<td>
									<table width="100%">
									<tr>
										<td style="padding-left:450px;">
											<div id="searchKeyContainerDiv">
											<input type="text" id="custom_search_key" name="searchKeyWord" onkeydown="javascript:enterOnClick(event);" value="" />
											<div id="searchKeyContainer"></div>
						                    </div>
										</td>
										<td>
											<div style="width: 20px; padding-left: 5px;">
											<a class="searchElement" href="javascript:void(0);" onclick="appService.searchApps();"> <img class="dinl"
												src="<s:url value="/images/search/Search-WarmGrey11.png" />"
												width="20" height="20" alt="Search" title="Search" /> </a>
											</div>
										</td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td class="noteInfo" colspan="2" style="padding-left:5px;width:300px;">
									<s:text name="geneva_26.info.custom.application.config"/>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td><tiles:insertDefinition name="notes" /></td>
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
																<th class="thchk" align="center"><input type="checkbox" id="checkAll" onclick="checkAllItem(this)" /></th>
																<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,1,'',this);">Application</th>
																<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,2,'float',this);">Usage (Last Day)</th>
																<th class="thhead SortNone" onclick="sortTable('thead_table_id','left_table_id','leftServiceTable',0,3,'float',this);">Usage (Last 30 Days)</th>
															</tr>
															</tbody>
														</table>
														</div>
														<div class="container">
														<table class="show" id="left_table_id" style="table-layout: fixed;">
															<tbody id="leftServiceTable">
															<s:iterator value="%{customAppList}" status="status">
																<tr class="even" serviceId="<s:property value="%{id}"/>" >
																	 <td width="30px" align="center" style="padding: 0 3px 0 1px; vertical-align: middle;">
																	 <input type="checkbox" name="customAppIds" serviceId="<s:property value="%{id}"/>" />
																	 </td>
																	 <td width="270px">
																	 <s:if test="'' != writeDisabled">
																	 <s:property value="%{customAppName}" escape="false"/>
																	 </s:if>
																	 <s:else>
																	 <a href="javascript:void(0);" title="<s:property value="%{description}"/>" onclick="editCustomApp('<s:property value="%{id}"/>')">
																	 <s:property value="%{customAppName}" escape="false"/>
																	 </a>
																	 </s:else>
																	 </td>
																	 <td  width="270px" realValue="<s:property value="%{lastDayUsage}"/>">
																	 <s:property value="%{lastDayUsageStr}"/>
																	 </td>
																	 <td  width="270px" realValue="<s:property value="%{lastMonthUsage}"/>">
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
		</div>
	</div>
	</div>
</s:form>
</div>
<div id="newCustomAppPanelId" style="display: none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM" style="width:750px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newCustomAppPanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:750px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
</div>
<script>
var thisOperation;
var formName = "customAppList";
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
		hm.util.checkAndConfirmDeleteRule('customAppIds');
    }
}

function doContinueOper()
{
	var selectedAppIds = "";
	var inputElements = document.getElementsByName('customAppIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (cb.checked) {
    			var serviceId = cb.getAttribute("serviceId");
				if (serviceId != null && serviceId != "" && serviceId != "undefined") {
					selectedAppIds = selectedAppIds + serviceId + ",";
				}
			}
		}
	}
	if (selectedAppIds.length > 0) {
		selectedAppIds = selectedAppIds.substring(0, selectedAppIds.length - 1);
		getDom("selectedAppIds").value = selectedAppIds;
		document.forms[formName].operation.value = thisOperation;
		var url =  "<s:url action='customApp' includeParams='none' />" +
		"?ignore="+new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("customAppList"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
				{success : succRemoveCustomApplication, failure : resultDoNothing, timeout: 60000}, null);
	}
} 
var succRemoveCustomApplication = function (o) {
	try{
		eval("var details = " + o.responseText);
		if (details.t == false) {
			hm.util.displayJsonErrorNote(details.m);
			return;
		}else{
			window.location.href = "<s:url action='customApp' includeParams='none' />"+"?oper="+details.m+"&removeIdNum="+details.removeIdNum+ "&ignore="+new Date().getTime();
			return;
		}
		
	}catch(e){
		return;
	}
	
}


var resultDoNothing = function(o) {
	// do nothing now
}

function checkAllItem(checkAll) {
	var inputElements = document.getElementsByName('customAppIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

var newCustomAppPanel = null;
function preparePanels4SelectService() {
	var div = document.getElementById('newCustomAppPanelId');
	newCustomAppPanel = new YAHOO.widget.Panel(div, {
		width:"700px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:3
		});
	newCustomAppPanel.render(document.body);
	div.style.display = "";
}

function fetchSelectServiceNewDlg(value) {
	var url = "";
	if(value == ""){
		url = "<s:url action='customApp' includeParams='none' />?operation=new&selectAdd=false" + "&ignore="+new Date().getTime();
	}else{
		url = "<s:url action='customApp' includeParams='none' />?operation=edit" + "&id="+encodeURIComponent(value)
		 + "&ignore="+new Date().getTime();
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	set_innerHTML("newCustomAppPanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newCustomAppPanelContentId", showSelectServicePanel, this);
}

var resultDoNothing = function(){}

function showSelectServicePanel(){
	if(null != newCustomAppPanel){
		newCustomAppPanel.cfg.setProperty('visible', true);
		newCustomAppPanel.center();
	}
}

function hideSelectServicePanel(){
	if(null != newCustomAppPanel){
		set_innerHTML("newCustomAppPanelContentId", "");
		newCustomAppPanel.cfg.setProperty('visible', false);
	}
}

YAHOO.util.Event.onContentReady("newCustomAppPanelId", function() {
	preparePanels4SelectService();
}, this);


var appService = appService || {};

function getDomValue(id) {
	return document.getElementById(id).value;
}

function getDom(id) {
	return document.getElementById(id);
}

String.prototype.startWith = function(str){
	var result = false;
	try{
		var reg=new RegExp("^"+str);
		result = reg.test(this);
	}catch(e){
		result = contains(this,str);
	}
	return result;       
}

function contains(string,substr){
	 string = string.toLowerCase();
	 substr = substr.toLowerCase();
	 string = string.replace(/&amp;/g,"&");
	 var startChar = substr.substring(0, 1);
	 var strLen = substr.length;
	 for (var j = 0; j < string.length - strLen + 1; j++) {
	  if (string.charAt(j) == startChar)
	  {
	   if (string.substring(j, j + strLen) == substr)
	   {
	    return true;
	   }
	  }
	 }
	 return false;
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

appService.searchApps = function(value) {
	var keywords;
	if(value){
		keywords = value;
	}else{
		keywords = getDomValue("custom_search_key");
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
				appName = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
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

function changeTab(){
	if(waitingPanel != null){
		waitingPanel.show();
	}
	window.location.href = "<s:url action='appService' includeParams='none' />";
}

function addCustomApp(){
	fetchSelectServiceNewDlg("");
}

function editCustomApp(id){
	fetchSelectServiceNewDlg(id);
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