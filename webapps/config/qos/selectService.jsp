<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<div id="content">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" align="right" width="100%">
						<tr>
							<td class="dialogPanelTitle" width="100%" align="left">
								<s:text name="geneva_26.config.networkservice.select.title"/>
							</td>
							<td align="right" style="padding-left:10px;" width="80px" nowrap><a href="javascript:void(0);" class="btCurrent"
							onclick="hideContentSelectServicePanel();return false;" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding-left:10px;padding-right: 10px">
						<table cellspacing="0" cellpadding="0" align="center" border="0" width="100%">
						 <tr>
						 	<td width="320px" height="373px">
						 		<div class="yui-navset" style="width: 320px;">
										<table width="100%"><tr style="line-height:20px;"><td>
												<s:text name="geneva_26.config.networkservice.select.available.services"/>
										</td></tr></table>
									<div class="yui-content" style="background:#ffffff;">
						 			<table cellspacing="0" cellpadding="0" border="0" width="100%">
						 				<tr>
						 					<td>
									 			<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<span id="currentAvailableNum"><s:property value="%{unSelectedService.size()}"/></span> Available (<span id="showTotalService"><s:property value="%{unSelectedService.size()}"/></span> Total)
									                	<input type="hidden" id="totalService" value="<s:property value="%{unSelectedService.size()}"/>"/>
									                </td>
									                <%-- <td align="right">
													<input type="text" id="search_service_key" name="searchKey" onkeydown="javascript:enterOnClick(event);" value="" />
													 <a href="javascript:void(0)" onclick="appService.searchApps();return false;">
								                         <img src="<s:url value="/images/search/Search-PMS296.png" includeParams="none"/>" border="0" width="20" height="15" alt="" />
								                    </a>
													</td> --%>
													<td>
														<div id="searchServiceKeyContainerDiv">
														<input type="text" id="search_service_key" name="searchKey" onkeydown="javascript:enterOnClick(event);" value="" />
														<div id="searchServiceKeyContainer"></div>
									                    </div>
													</td>
													<td>
														<div style="width: 20px; padding-top: 5px;">
														<a class="searchElement" href="javascript:void(0);" onclick="appService.searchApps();return false;"> <img class="dinl"
															src="<s:url value="/images/search/Search-WarmGrey11.png" />"
															width="20" height="15" alt="Search" title="Search" /> </a>
														</div>
													</td>
												</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<div class="divcontainer">
													<table id="left_thead_table_id">
														<tbody>
														<tr>
															<th class="thservicechk" align="center"><input type="checkbox" id="checkAllLeft" onClick="checkAllLeftItem(this);" /></th>
															<th class="thservicehead SortNone" onclick="sortTable('left_thead_table_id','left_table_id','leftServiceTable',0,1,'',this);">Service</th>
														</tr>
														</tbody>
													</table>
													</div>
													<div class="container">
													<table class="show" id="left_table_id" style="table-layout: fixed;">
														<tbody id="leftServiceTable">
														<s:iterator value="%{unSelectedService}" status="status">
															<tr class="even" serviceId="<s:property value='id'/>" appType="<s:property value="%{appType}"/>">
													          		<td width="50px" align="center" style="padding-left:2px;vertical-align: middle;">
																	 <input type="checkbox" appType="<s:property value="%{appType}"/>" />
																	 </td>
																	 <td width="240px">
																	 <a  href="#" onclick="javascript:void(0);return false;" title="<s:property value="%{description}"/>">
																	 <s:property value="%{serviceName}" escape="false"/>
																	 </a>
																	 </td>
															</tr>
														</s:iterator>
														</tbody>
													</table>
													</div>
											</td>
										</tr>
									</table>
						 		</div>
						 		</div>
						 	</td>
						 	<td width="60px" height="373px">
						 		<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
						             <tr>
						                <td align="center">
						                <s:if test="%{writeDisabled == 'disabled'}">
						                	<img class="dinl" src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
			                				<img class="dinl" src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" />
						                </s:if>
						                <s:else>
						                	<a style="padding-right:20px;" class="marginBtn" href="javascript:void(0);" onclick="editNetworkService();return false;"><img class="dinl"
													src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
			                				<a class="marginBtn"  href="javascript:void(0);" onclick="addNetworkService();return false;"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
						                </s:else>
										<br/><br/><br/>
						                <input type="button" value=">" onclick="appService.addApp()" class="transfer">
								         <br/><br/><br/>
								         <input type="button" value="<" onclick="appService.removeApp()" class="transfer">
						                </td>
						             </tr>
						    	</table>
						 	</td>
						 	<td width="320px" height="373px">
						 		<div class="yui-navset" style="width: 320px;">
										<table width="100%"><tr style="line-height:20px;"><td>
												<s:text name="geneva_26.config.networkservice.selected.title"/>
										</td></tr></table>
									<div class="yui-content" style="background:#ffffff;">
							 		<table>
										<tr>
									    	<td>
										 		<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
										                <td style="padding:2px;">
										                <span id="currentSelectNum">0</span> Selected (100 Max)
										                </td>
										             </tr>
													<tr id="showErrMsg">
														<td>
															<div class="divcontainer">
															<table id="right_thead_table_id">
																<tbody>
																<tr>
																	<th class="thservicechk" align="center"><input type="checkbox" id="checkAllRight" onClick="checkAllRightItem(this);" /></th>
																	<th class="thservicehead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightServiceTable',0,1,'',this);">Service</th>
																</tr>
																</tbody>
															</table>
															</div>
															<div class="container">
															<table class="show" id="right_table_id" style="table-layout: fixed;" >
																<tbody id="rightServiceTable">
																</tbody>
															</table>
															</div>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
							</div>
						 	</td>
						 </tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding-top: 2px; padding-bottom: 5px; width:100%;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr>
							<td>
								<input  class="button" id="selectedService" type="button" value="OK" onclick="doSubmitNewService()" name="select service" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
</div>
<div id="newNetworkServicePanelId" style="display: none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM" style="width:800px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newNetworkServicePanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:800px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
</div>
<script>
function loadPage() {
	initServiceAutoSearch();
}

window.setTimeout("loadPage()", 200);
function initServiceAutoSearch(){
	// init DataSource
	var serviceArray = new Array();
	var index = 0;
	var tempstr;
	<s:iterator value="searchUnSelectedService" id="serviceName">
	tempstr = "<s:property value='%{serviceName}' escape='false'/>";
	tempstr = tempstr.replace(/\\\\/g, "\\");
	tempstr = tempstr.replace(/&amp;/g,"&");
	serviceArray[index++] = tempstr;
	</s:iterator>
	var serviceDataSource = new YAHOO.util.LocalDataSource(serviceArray);
	 // Optional to define fields for single-dimensional array
    serviceDataSource.responseSchema = {fields : "serviceName"};
	
    //Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("search_service_key", "searchServiceKeyContainer", serviceDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedVal);
    return {
        oDS: serviceDataSource,
        oAC: oAC
    };
}
function setSelectedVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName);
}
function checkAllLeftItem(checkAll) {
	var leftTable = getDom("leftServiceTable");
	var trArray = leftTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		if(trArray[i].style.display == ""){
			var cb = trArray[i].getElementsByTagName("input")[0];
			if (cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

function checkAllRightItem(checkAll) {
	var rightTable = getDom("rightServiceTable");
	var trArray = rightTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		if(trArray[i].style.display == ""){
			var cb = trArray[i].getElementsByTagName("input")[0];
			if (cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

var newNetworkServicePanel = null;
function preparePanels4SelectNetworkService() {
	var div = document.getElementById('newNetworkServicePanelId');
	newNetworkServicePanel = new YAHOO.widget.Panel(div, {
		width:"780px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:5
		});
	newNetworkServicePanel.render(document.body);
	div.style.display = "";
}

function fetchSelectNetworkServiceNewDlg(id) {
	var url = "";
	if(id == "-1"){
		url = "<s:url action='networkService' includeParams='none' />?operation=new&onloadSelectedService=true&jsonMode=<s:property value="%{jsonMode}"/>"
			 + "&ignore="+new Date().getTime();
	}else{
		url = "<s:url action='networkService' includeParams='none' />?operation=edit&onloadSelectedService=true&jsonMode=<s:property value="%{jsonMode}"/>&id="+id
		 + "&ignore="+new Date().getTime();
	}
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceDlg, failure : resultNothing, timeout: 60000}, null);
}

var succFetchServiceDlg = function(o) {
	if(newNetworkServicePanel == null ){
		preparePanels4SelectNetworkService();
	}
	hideContentSelectServicePanel();
	set_innerHTML("newNetworkServicePanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newNetworkServicePanelContentId", showSelectNetworkServicePanel, this);
}

var resultNothing = function(){}

function showSelectNetworkServicePanel(){
	if(null != newNetworkServicePanel){
		newNetworkServicePanel.cfg.setProperty('visible', true);
		newNetworkServicePanel.center();
	}
}

function hideSelectNetworkServicePanel(){
	if(null != newNetworkServicePanel){
		set_innerHTML("newNetworkServicePanelContentId", "");
		newNetworkServicePanel.cfg.setProperty('visible', false);
	}
}

function hideSelfShowParent(){
	hideSelectNetworkServicePanel();
	showSelectServicePanel();
}

var appService = appService || {};

appService.selectColor = "#0093D1";

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

appService.changeCurrentSelectNum = function(id,number) {
	var currentValue = parseInt(getDom(id).innerHTML);
	var currentSelectNum = currentValue + number;
	getDom(id).innerHTML = currentSelectNum;
}

appService.changeCurrentSearchNum = function(id,number) {
	getDom(id).innerHTML = number;
}

appService.changeCurrentTotalNum = function(id,number) {
	getDom(id).value = number;
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
	var totalService = parseInt(getDomValue("totalService"));
	var currentSelectedService = parseInt(getDom("currentSelectNum").innerHTML);
	this.changeCurrentSearchNum("currentAvailableNum",totalService - currentSelectedService);
}

appService.addApp = function() {
	var leftTable = getDom('leftServiceTable');
	var rightTable = getDom('rightServiceTable');
	var list = leftTable.getElementsByTagName('tr');
	var trArray = new Array();

	for(var i=0; i<list.length; i++){
		var chck = list[i].getElementsByTagName("input")[0];
		if (chck.checked == true) {
			trArray.push(list[i]);
		}
	}
	
	var rightTableTr = rightTable.getElementsByTagName('tr');
	
	if((trArray.length + rightTableTr.length) > 100){
		var rightAppDiv = getDom('showErrMsg');
		hm.util.reportFieldError(rightAppDiv, '<s:text name="geneva_26.config.networkservice.select.maximum" />');
		return false;
	}

	this.changeCurrentSelectNum("currentSelectNum",trArray.length);
	var leftNum = getDom("currentAvailableNum").innerHTML.trim();
	this.changeCurrentSearchNum("currentAvailableNum",parseInt(leftNum) - trArray.length);
	if(trArray.length >0){
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("input")[0].checked = false;
			rightTable.appendChild(trArray[i]);
		}
	}
	
}



appService.removeApp = function() {
	var leftTable = getDom('leftServiceTable');
	var rightTable = getDom('rightServiceTable');
	var rlist = rightTable.getElementsByTagName('tr');
	var trArray = new Array();
	
	for(var i = 0; i < rlist.length; i++) {
		var chck = rlist[i].getElementsByTagName("input")[0];
		if (chck.checked == true) {
			trArray.push(rlist[i]);
		}
	}

	if(trArray.length > 0){
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("input")[0].checked = false;
			leftTable.appendChild(trArray[i]);
		}
	}
	this.changeCurrentSelectNum("currentSelectNum",0 - trArray.length);
	var leftNum = getDom("currentAvailableNum").innerHTML.trim();
	this.changeCurrentSearchNum("currentAvailableNum",parseInt(leftNum) + trArray.length);
}

appService.filterApps = function(value) {
	var keywords = value;
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp();
		return false;
	}
	keywords = keywords.toUpperCase();
	var tableObj = getDom('leftServiceTable');
	var trs = tableObj.getElementsByTagName('tr');
	var trArray = new Array();
	if(trs.length > 0){
		for (var i = 0; i < trs.length; i++) {
				var appName = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
				if (appName.startWith(keywords)) {
					this.showApp(trs[i]);
					trArray.push(trs[i]);
				} else {
					this.hiddenApp(trs[i]);
				}
		}
	}
	this.changeCurrentSearchNum("currentAvailableNum",trArray.length);
}

appService.searchApps = function() {
	var keywords = getDomValue("search_service_key");
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp();
		return false;
	}
	keywords = keywords.toUpperCase();
	var tableObj = getDom('leftServiceTable');
	var trs = tableObj.getElementsByTagName('tr');
	var trArray = new Array();
	if(trs.length > 0){
		for (var i = 0; i < trs.length; i++) {
				var appName = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
				if (appName.startWith(keywords)) {
					this.showApp(trs[i]);
					trArray.push(trs[i]);
				} else {
					this.hiddenApp(trs[i]);
				}
		}
	}
	this.changeCurrentSearchNum("currentAvailableNum",trArray.length);
}

function enterOnClick(event){
	var keycode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if(keycode == 13){
		appService.searchApps();
		return false;
	}	
}

function editNetworkService(){
	var list = getDom('leftServiceTable').getElementsByTagName('tr');
	var n = list.length;
	var totalNum = 0;
	if(n >0){
		for(var i = 0; i < n; i++) {
			var chck = list[i].getElementsByTagName("input")[0];
			if (chck.checked == true) {
				totalNum ++;
			}
		}
	}
	
	if (totalNum == 1) {
		for(var i = 0; i < n; i++) {
			var chck = list[i].getElementsByTagName("input")[0];
			if (chck.checked == true) {
				getDom("selectedServiceId").value = list[i].getAttribute("serviceId");
				break;
			}
		}
		if(getDom("selectedServiceId").value == "-1"){
			showWarnDialog('<s:text name="geneva_26.config.networkservice.select.one.valid"/>');
			return;
		}
		fetchSelectNetworkServiceNewDlg(getDom("selectedServiceId").value);
	}else{
		showWarnDialog('<s:text name="geneva_26.config.networkservice.select.one"/>');
		return;
	}
}
	
function addNetworkService(){
	fetchSelectNetworkServiceNewDlg("-1");
}
	


function doSubmitNewService() {
	var networkService_head = "Network Service: "; 
	var selectedNetworkServiceIds = "";
	var selectedNetworkServiceNames = "";
	var list = getDom('rightServiceTable').getElementsByTagName('tr');
	var totalNum = 0;
	for(var i = 0; i < list.length; i++) {
		if (list[i].getAttribute("serviceId") != null && list[i].getAttribute("serviceId") != "" && list[i].getAttribute("serviceId") != "undefined") {
			var appType = list[i].getAttribute("appType");
			selectedNetworkServiceIds = selectedNetworkServiceIds + list[i].getAttribute("serviceId") + appType + ",";
			var tdValue = list[i].getElementsByTagName("a")[0].innerHTML;
			tdValue = tdValue.trim().replace(/\\/g, "\\\\").replace(/&amp;/g,"&");
			selectedNetworkServiceNames = selectedNetworkServiceNames + networkService_head + tdValue + ",";
			totalNum ++;
		}
	}
	if (totalNum > 100) {
		var rightAppDiv = getDom('showErrMsg');
		hm.util.reportFieldError(rightAppDiv, '<s:text name="geneva_26.config.networkservice.select.maximum" />');
		rightAppDiv.focus();
    	return false;
	}
	if (selectedNetworkServiceIds.length > 0) {
		selectedNetworkServiceIds = selectedNetworkServiceIds.substring(0, selectedNetworkServiceIds.length - 1);
	}
	var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
	widget.changeSelectedServiceNum(totalNum);
	getSelectedValues(selectedNetworkServiceIds,selectedNetworkServiceNames);
	hideContentSelectServicePanel();
}

function getSelectedValues(selectedNetworkServiceIds,selectedNetworkServiceNames){
	
	var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
	var selectedValues = "";
	var selectedNetworkServiceId = new Array();
	var selectedNetworkServiceName = new Array();
	if(selectedNetworkServiceIds != "" && selectedNetworkServiceNames != ""){
		selectedNetworkServiceId = selectedNetworkServiceIds.split(",");
		selectedNetworkServiceName = selectedNetworkServiceNames.split(",");
		if(selectedNetworkServiceId.length > 0){
			for(var i=0; i<selectedNetworkServiceId.length; i++){
				selectedValues = selectedValues + "{value:"+selectedNetworkServiceId[i]+", text:\""+selectedNetworkServiceName[i]+"\"},";
			}
		}
	}
	
	if (widget) {
		selectedValues = selectedValues == "" ? "" : selectedValues.substring(0, selectedValues.length - 1);
		widget.changeHiddenValue(selectedValues);  
	}
}

</script>
