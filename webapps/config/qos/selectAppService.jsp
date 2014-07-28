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
								<s:text name="geneva_26.config.appservice.select.title"/>
							</td>
							<td align="right" style="padding-left:10px;" width="80px" nowrap><a href="javascript:void(0);" class="btCurrent"
							onclick="hideContentSelectServicePanel();return false;" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding-left:5px;padding-right: 5px">
						<table cellspacing="0" cellpadding="0" align="center" border="0" width="100%">
						<tr>
							<td colspan="3"  height="10px"></td>
						 </tr>
						<tr>
							<td width="500px" height="386px">
								<div class="yui-navset">
								    <ul class="yui-nav">
								        <li id="systemTab" class="selected"><a href="javascript:void(0);" onclick="changeTab('systemTab'); return false;"><em><s:text name="geneva_26.config.system.application.tab"/></em></a></li>
								        <li id="customTab"><a href="javascript:void(0);" onclick="changeTab('customTab'); return false;"><em><s:text name="geneva_26.config.custom.application.tab"/></em></a></li>
								    </ul>  
								    <div class="yui-content" style="background:#ffffff;height:340px;">
								        <div id="systemDiv">
										<table>
											<tr><td>
											<table width="100%">
													<tr>
														<td>
															<table width="100%">
															<tr>
																<td width="140px">
																	<span id="currentAvailableSystemNum"><s:property value="%{unSelectedApp.size()}"/></span> Available (<s:property value="%{unSelectedApp.size()}"/> Total)
												                	<input type="hidden" id="totalSystemAppNum" value="<s:property value="%{unSelectedApp.size()}"/>"/>
												                </td>
												                <%-- <td align="right">
																	<input type="radio" onclick="changeService('application');" checked="checked" id="application_radio"/><s:text name="geneva_26.config.system.application.search.application"/>&nbsp;&nbsp;
																	<input type="radio" onclick="changeService('group');" id="group_radio"/><s:text name="geneva_26.config.system.application.search.group"/>&nbsp;&nbsp;
																	<input type="text" id="system_search_key" name="systemSearchKey" onkeydown="javascript:return appService.enterKeywords(event,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
																	 <a href="javascript: void(0);" onclick="appService.searchApps('leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');">
												                         <img src="<s:url value="/images/search/Search-PMS296.png" includeParams="none"/>" border="0" width="20" height="15" alt="" />
												                    </a>
																</td> --%>
																<td align="right">
																	<input type="radio" onclick="changeService('application');" checked="checked" id="application_radio"/><s:text name="geneva_26.config.system.application.search.application"/>&nbsp;&nbsp;
																	<input type="radio" onclick="changeService('group');" id="group_radio"/><s:text name="geneva_26.config.system.application.search.group"/>&nbsp;&nbsp;
																</td>
																<td id="appSearchTd" width="140px">
																	<div id="searchKeyContainerDiv">
																	<input type="text" id="system_search_key" name="searchKey" onkeydown="javascript:return appService.enterKeywords(event,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
																	<div id="searchKeyContainer"></div>
												                    </div>
																</td>
																<td id="groupSearchTd" width="140px" style="display:none;">
																	<div id="searchGroupKeyContainerDiv">
																	<input type="text" id="system_search_groupkey" name="searchKey" onkeydown="javascript:return appService.enterKeywords(event,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
																	<div id="searchGroupKeyContainer"></div>
												                    </div>
																</td>
																<td>
																	<div style="width: 20px; padding-left: 5px;">
																	<a class="searchElement" href="javascript: void(0);" onclick="appService.searchApps('leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');"> <img class="dinl"
																		src="<s:url value="/images/search/Search-WarmGrey11.png" />"
																		width="20" height="20" alt="Search" title="Search" /> </a>
																	</div>
																</td>
															</tr>
															</table>
														</td>
													</tr>
												</table>
											</td></tr>
											<tr><td>
												<div class="appdivcontainer">
													<table id="system_left_thead_table_id">
														<tbody>
														<tr>
															<th class="thchk" align="center"><input type="checkbox" id="checkAllSystem" onClick="checkAllSystemItem(this);" /></th>
															<th class="thapphead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,1,'',this);">Application</th>
															<th class="thhead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,2,'float',this);">Usage(Last Day)</th>
															<th class="thhead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,3,'float',this);">Usage(Last 30 Days)</th>
															<th class="thapphead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,4,'',this);">Group</th>
														</tr>
														</tbody>
													</table>
													</div>
									                <div class="appcontainer">
									        		<table class="appshow" id="system_left_table_id" style="table-layout: fixed;">
														<tbody id="leftSystemAppTable">
									        		         <s:iterator value="%{unSelectedApp}" status="status">
														          <tr class="even" appId="<s:property value='id'/>" appType="<s:property value="%{appType}"/>">
														          		<td class="tdsystemchk">
																		 <input type="checkbox" name="systemAppIds" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
																		 </td>
																		 <td class="tdsystemappname">
																		 <a href="#" onclick="javascript:void(0);return false;" title="<s:property value="%{description}"/>"><s:property value="%{shortName}"/></a>
																		 </td>
												        		        <td class="tdsystemusage" realValue="<s:property value='lastDayUsage'/>"><s:property value='lastDayUsageStr'/></td>
												        		        <td class="tdsystemusage" realValue="<s:property value='lastMonthUsage'/>"><s:property value="lastMonthUsageStr"/></td>
												        		        <td class="tdsystemgroup"><s:property value="appGroupName"/></td>
												        		  </tr>	
														      </s:iterator>      
									        		    </tbody>
									                </table>
									                </div>
											</td></tr>
										</table>
									</div>
									<div id="customDiv" class="yui-hidden">
										<table>
										<tr><td>
										<table width="100%">
												<tr>
													<td>
														<table width="100%">
														<tr>
															<td width="200px">
																<span id="currentAvailableCustomNum"><s:property value="%{unSelectedCustomAppList.size()}"/></span> Available (<s:property value="%{unSelectedCustomAppList.size()}"/> Total)
											                	<input type="hidden" id="totalCustomAppNum" value="<s:property value="%{unSelectedCustomAppList.size()}"/>"/>
											                </td>
											               <%--  <td align="right">
																<input type="text" id="custom_search_key" name="customSearchKey" onkeydown="javascript:appService.enterKeywords(event,'leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');" value="" />
																 <a href="javascript: void(0);" onclick="appService.searchApps('leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');">
											                         <img src="<s:url value="/images/search/Search-PMS296.png" includeParams="none"/>" border="0" width="20" height="15" alt="" />
											                    </a>
															</td> --%>
															<td style="padding-left:150px;">
																<div id="searchCustomKeyContainerDiv">
																<input type="text" id="custom_search_key" name="customSearchKey" onkeydown="javascript:appService.enterKeywords(event,'leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');" value="" />
																<div id="searchCustomKeyContainer"></div>
											                    </div>
															</td>
															<td>
																<div style="width: 20px; padding-left: 5px;">
																<a class="searchElement" href="javascript:void(0);" onclick="appService.searchApps('leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');"> <img class="dinl"
																	src="<s:url value="/images/search/Search-WarmGrey11.png" />"
																	width="20" height="20" alt="Search" title="Search" /> </a>
																</div>
															</td>
														</tr>
														</table>
													</td>
												</tr>
											</table>
										</td></tr>
										<tr><td>
											<div class="customappdivcontainer">
												<table id="custom_left_thead_table_id">
													<tbody>
													<tr>
														<th class="thchk" align="center"><input type="checkbox" id="checkAllCustom" onClick="checkAllCustomItem(this);" /></th>
														<th class="thcustomapphead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,1,'',this);">Application</th>
														<th class="thcustomhead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,2,'float',this);">Usage (Last Day)</th>
														<th class="thcustomhead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,3,'float',this);">Usage (Last 30 Days)</th>
														<!-- <th class="thapphead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,4,'',this);">Group</th> -->
													</tr>
													</tbody>
												</table>
												</div>
								                <div class="appcontainer">
								        		<table class="appshow" id="custom_left_table_id" style="table-layout: fixed;">
													<tbody id="leftCustomAppTable">
													<s:iterator value="%{unSelectedCustomAppList}" status="status">
														<tr class="even" appId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>">
															 <td class="tdcustomchk">
															 <input type="checkbox" name="customAppIds" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
															 </td>
															 <td class="tdcustomappname">
															 <a href="#" onclick="javascript:void(0);return false;" title="<s:property value="%{description}"/>">
															 <s:property value="%{customAppName}" escape="false"/>
															 </a>
															 </td>
															 <td class="tdcustomusage" realValue="<s:property value="%{lastDayUsage}"/>">
															 <s:property value="%{lastDayUsageStr}"/>
															 </td>
															 <td class="tdcustomusage" realValue="<s:property value="%{lastMonthUsage}"/>">
															 <s:property value="%{lastMonthUsageStr}"/>
															 </td>
															 <%-- <td width="130px" style="display:none;padding-left:5px;"><s:property value="appGroupName"/></td> --%>
														</tr>
													</s:iterator>
													</tbody>
												</table>
								                </div>
										</td></tr>
									</table>
									</div>
								</div>
								</div>
							</td>
							<td  width="60px" height="386px">
								<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
						             <tr>
						                <td  style="padding-left:5px;padding-right:5px;text-align:center;">
						                <br/>
						                <input type="button" value=">" onclick="appService.addApp()" class="transfer">
								         <br/><br/><br/>
								         <input type="button" value="<" onclick="appService.removeApp()" class="transfer">
						                </td>
						             </tr>
						    	</table>
							</td>
							<td width="500px" height="386px">
							<div class="yui-navset" style="width: 500px;">
										<table width="100%"><tr style="line-height:20px;"><td>
												<s:text name="geneva_26.config.appservice.selected.title"/>
										</td></tr></table>
									<div class="yui-content" style="background:#ffffff;">
										<table>
											<tr>
											    <td>
											    	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
											             <tr style="line-height:36px;">
											                <td>
											                <span id="currentSelectAllNum">0</span> Selected (100 Max)
											                <input type="hidden" id="currentSelectSystemNum" value="0"/>
				                							<input type="hidden" id="currentSelectCustomNum" value="0"/>
											                </td>
											             </tr>
											             <tr id="messageError">
											                <td>
											                <div class="appdivcontainer">
															<table id="right_thead_table_id" class="apptheadshow">
																<tbody>
																<tr>
																	<th class="thchk" align="center"><input type="checkbox" id="checkAllApp" onClick="checkAllAppItem(this);" /></th>
																	<th class="thapphead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,1,'',this);">Application</th>
																	<th class="thhead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,2,'float',this);">Usage(Last Day)</th>
																	<th class="thhead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,3,'float',this);">Usage(Last 30 Days)</th>
																	<th class="thapphead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,4,'',this);">Group</th>
																</tr>
																</tbody>
															</table>
															</div>
											                <div class="appcontainer">
											        		<table class="appshow" id="right_table_id" style="table-layout: fixed;">
															<tbody id="rightAppTable">
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
				<td align="center" style="padding-top: 2px; padding-bottom: 5px; width:100%;">
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

<script>
function loadPage() {
	initAppAutoSearch();
	initGroupAutoSearch();
	initCustomAutoSearch();
}

window.setTimeout("loadPage()", 200);
function initAppAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	<s:iterator value="unSelectedApp">
	appArray[index++] = "<s:property value='%{shortName}'/>";
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
function initCustomAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	var tempstr;
	<s:iterator value="searchUnSelectedCustomAppList" id="customAppName">
	tempstr = "<s:property value='%{customAppName}' escape='false'/>";
	tempstr = tempstr.replace(/\\\\/g, "\\");
	tempstr = tempstr.replace(/&amp;/g,"&");
	appArray[index++] = tempstr;
	</s:iterator>
	
	var appDataSource = new YAHOO.util.LocalDataSource(appArray);
	 // Optional to define fields for single-dimensional array
    appDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
     var oAC = new YAHOO.widget.AutoComplete("custom_search_key", "searchCustomKeyContainer", appDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedCustomVal);
    return {
        oDS: appDataSource,
        oAC: oAC
    };
}
function setSelectedVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');
}
function setSelectedCustomVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName,'leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');
}
function changeTab(tab){
	if("systemTab" == tab){
		getDom("leftSystemAppTable").style.display = "";
		$("#customTab").removeClass("selected");
		$("#systemTab").addClass("selected");
		$("#systemDiv").removeClass("yui-hidden");
		$("#customDiv").addClass("yui-hidden");
	}else{
		getDom("leftSystemAppTable").style.display = "none";
		$("#systemTab").removeClass("selected");
		$("#customTab").addClass("selected");
		$("#customDiv").removeClass("yui-hidden");
		$("#systemDiv").addClass("yui-hidden");
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

function checkAllSystemItem(checkAll) {
	var rightTable = getDom("leftSystemAppTable");
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

function checkAllCustomItem(checkAll) {
	var rightTable = getDom("leftCustomAppTable");
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

function checkAllAppItem(checkAll) {
	var rightTable = getDom("rightAppTable");
	var trArray = rightTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		var cb = trArray[i].getElementsByTagName("input")[0];
		if (cb.checked != checkAll.checked) {
			cb.checked = checkAll.checked;
		}
	}
} 

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

 appService.showAllApp = function(leftTable,currentAvailableNum,selectedAppNum,totalAppNum) {
	var trs = getDom(leftTable).getElementsByTagName('tr');
	for (var i = 0; i < trs.length; i++) {
		trs[i].style.display = "";
	}
	var currentSelectNum = parseInt(getDomValue(selectedAppNum));
	var totalAppNum = parseInt(getDomValue(totalAppNum));
	this.changeAvailableNum(currentAvailableNum, totalAppNum - currentSelectNum);
}

appService.changeCurrentSelectNum = function(hiddenSelectedNumId,number) {
	var currentValue = getDom("currentSelectAllNum").innerHTML;
	var currentSelectNum = parseInt(currentValue);
	currentSelectNum = currentSelectNum + number;
	getDom("currentSelectAllNum").innerHTML = currentSelectNum;
	var hiddenSelectedNum = getDomValue(hiddenSelectedNumId);
	getDom(hiddenSelectedNumId).value = parseInt(hiddenSelectedNum) + number;
}

appService.changeAvailableNum = function(currentAvailableNum,number) {
	getDom(currentAvailableNum).innerHTML = number;
}

appService.changeDeltaAvailableNum = function(currentAvailableNumId,number) {
	var currentValue = getDom(currentAvailableNumId).innerHTML;
	var currentAvailableNum = parseInt(currentValue);
	currentAvailableNum = currentAvailableNum + number;
	getDom(currentAvailableNumId).innerHTML = currentAvailableNum;
}

appService.addApp = function() {
	var list;
	var leftTable;
	var inputElements;
	var div = getDom("systemDiv");
	var trArray = new Array();
	var hiddenSelectedNumId;
	var currentAvailableNumId;
	if(div.className == 'yui-hidden'){
		leftTable = getDom("leftCustomAppTable");
		list = leftTable.getElementsByTagName('tr');
		inputElements = document.getElementsByName('customAppIds');
		hiddenSelectedNumId = "currentSelectCustomNum";
		currentAvailableNumId = "currentAvailableCustomNum";
	}else{
		leftTable = getDom("leftSystemAppTable");
		list = leftTable.getElementsByTagName('tr');
		inputElements = document.getElementsByName('systemAppIds');
		hiddenSelectedNumId = "currentSelectSystemNum";
		currentAvailableNumId = "currentAvailableSystemNum";
	}
	
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (cb.checked) {
				trArray.push(list[i]);
			}
		}
	}
	if(trArray.length == 0){
		return false;
	}
	var rightTable = getDom("rightAppTable");
	var rightTableTr = rightTable.getElementsByTagName('tr');
	
	if((trArray.length + rightTableTr.length) > 100){
		var rightAppDiv = getDom('messageError');
		hm.util.reportFieldError(rightAppDiv, '<s:text name="geneva_26.config.appservice.select.maximum"/>');
		return false;
	}
	if(div.className == 'yui-hidden'){
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("td")[0].className = "tdsystemchk";
			trArray[i].getElementsByTagName("input")[0].checked = false;
			trArray[i].getElementsByTagName("td")[1].className = "tdsystemappname";
			trArray[i].getElementsByTagName("td")[2].className = "tdsystemusage";
			trArray[i].getElementsByTagName("td")[3].className = "tdsystemusage";
			var tdgroup = document.createElement("td");
			tdgroup.className = "tdsystemgroup";
			tdgroup.appendChild(document.createTextNode("Custom"));
			trArray[i].appendChild(tdgroup);
			rightTable.appendChild(trArray[i]);
		}
	}else{
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("input")[0].checked = false;
			rightTable.appendChild(trArray[i]);
		}
	}
	
	this.changeCurrentSelectNum(hiddenSelectedNumId,trArray.length);
	this.changeDeltaAvailableNum(currentAvailableNumId,0 - trArray.length);
}

appService.removeApp = function() {
	var systemLeftTable = getDom("leftSystemAppTable");
	var customLeftTable = getDom("leftCustomAppTable");
	var rightTable = getDom("rightAppTable");
	var list = rightTable.getElementsByTagName('tr');
	var systemTrArray = new Array();
	var customTrArray = new Array();
	var n = list.length;
	
	for(var i = 0; i < n; i++) {
		var tr = list[i];
		var tdchk = list[i].getElementsByTagName("input")[0];
		if(tdchk.checked){
			if (tr.getAttribute("appType") == 0) {
				systemTrArray.push(list[i]);
			}else{
				customTrArray.push(list[i]);
			}
		}
	}
	if(systemTrArray.length > 0 && customTrArray.length > 0){
		for (var i = 0; i < systemTrArray.length; i++) {
			systemTrArray[i].getElementsByTagName("input")[0].checked = false;
			systemLeftTable.appendChild(systemTrArray[i]);
		}
		for (var i = 0; i < customTrArray.length; i++) {
			customTrArray[i].getElementsByTagName("td")[0].className = "tdcustomchk";
			customTrArray[i].getElementsByTagName("input")[0].checked = false;
			customTrArray[i].getElementsByTagName("td")[1].className = "tdcustomappname";
			customTrArray[i].getElementsByTagName("td")[2].className = "tdcustomusage";
			customTrArray[i].getElementsByTagName("td")[3].className = "tdcustomusage";
			var tdgroup = customTrArray[i].getElementsByTagName("td")[4];
			customTrArray[i].removeChild(tdgroup);
		    customLeftTable.appendChild(customTrArray[i]);
		}
		this.changeCurrentSelectNum("currentSelectSystemNum",0 - systemTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableSystemNum",systemTrArray.length);
		this.changeCurrentSelectNum("currentSelectCustomNum",0 - customTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableCustomNum",customTrArray.length);
	}else if(systemTrArray.length > 0 && customTrArray.length == 0){
		for (var i = 0; i < systemTrArray.length; i++) {
			systemTrArray[i].getElementsByTagName("input")[0].checked = false;
		    systemLeftTable.appendChild(systemTrArray[i]);
		}
		getDom("leftSystemAppTable").style.display = "";
		getDom("systemDiv").className = "";
		getDom("customDiv").className = "yui-hidden";
		getDom("customTab").className = "";
		getDom("systemTab").className = "selected";
		this.changeCurrentSelectNum("currentSelectSystemNum",0 - systemTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableSystemNum",systemTrArray.length);
	}else if(systemTrArray.length == 0 && customTrArray.length > 0){
		for (var i = 0; i < customTrArray.length; i++) {
			customTrArray[i].getElementsByTagName("td")[0].className = "tdcustomchk";
			customTrArray[i].getElementsByTagName("input")[0].checked = false;
			customTrArray[i].getElementsByTagName("td")[1].className = "tdcustomappname";
			customTrArray[i].getElementsByTagName("td")[2].className = "tdcustomusage";
			customTrArray[i].getElementsByTagName("td")[3].className = "tdcustomusage";
			var tdgroup = customTrArray[i].getElementsByTagName("td")[4];
			customTrArray[i].removeChild(tdgroup);
		    customLeftTable.appendChild(customTrArray[i]);
		}
		getDom("leftSystemAppTable").style.display = "none";
		getDom("systemDiv").className = "yui-hidden";
		getDom("customDiv").className = "";
		getDom("systemTab").className = "";
		getDom("customTab").className = "selected";
		this.changeCurrentSelectNum("currentSelectCustomNum",0 - customTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableCustomNum",customTrArray.length);
	}else{
		return false;
	}
	
}

appService.filterApps = function(value,leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum) {
	var isAppFilter;
	var keywords = value;
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp(leftTable,currentAvailableNum,selectedAppNum,totalAppNum);
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		return false;
	}
	var num = 0;
	keywords = keywords.toUpperCase();
	var tableObj = getDom(leftTable);
	var trs = tableObj.getElementsByTagName('tr');
	if(radioCheck == ""){
		for (var i = 0; i < trs.length; i++) {
			appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}else{
		isAppFilter = getDom(radioCheck).checked;
		for (var i = 0; i < trs.length; i++) {
			if (isAppFilter) { //compare app name
				appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			} else { //compare app group name
				appInfo = trs[i].getElementsByTagName('td')[4].innerHTML.trim().toUpperCase();
			}
			
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}
	
	this.changeAvailableNum(currentAvailableNum,num);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

appService.searchApps = function(leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum) {
	var isAppFilter;
	var keywords;
	if(radioCheck == ""){
		keywords = getDomValue("custom_search_key");
	}else{
		isAppFilter = getDom(radioCheck).checked;
		if (isAppFilter) { 
			keywords = getDomValue("system_search_key");
		}else{
			keywords = getDomValue("system_search_groupkey");
		}
	}
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp(leftTable,currentAvailableNum,selectedAppNum,totalAppNum);
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		return false;
	}
	var num = 0;
	keywords = keywords.toUpperCase();
	var tableObj = getDom(leftTable);
	var trs = tableObj.getElementsByTagName('tr');
	if(radioCheck == ""){
		for (var i = 0; i < trs.length; i++) {
			appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}else{
		for (var i = 0; i < trs.length; i++) {
			if (isAppFilter) { //compare app name
				appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			} else { //compare app group name
				appInfo = trs[i].getElementsByTagName('td')[4].innerHTML.trim().toUpperCase();
			}
			
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}
	
	this.changeAvailableNum(currentAvailableNum,num);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

appService.enterKeywords = function(event,leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum){
	var keycode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if(keycode == 13){
		if(waitingPanel != null){
			waitingPanel.show();
		}
		this.searchApps(leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum);
		return false;
	}	
}


function doSubmitNewService() {
	var networkService_head = "Application Service: "; 
	var selectedNetworkServiceIds = "";
	var selectedNetworkServiceNames = "";
	var list = getDom('rightAppTable').getElementsByTagName('tr');
	var totalNum = 0;
	for(var i = 0; i < list.length; i++) {
		if (list[i].getAttribute("appId") != null && list[i].getAttribute("appId") != "" && list[i].getAttribute("appId") != "undefined") {
			//for identify service and custom application
			var appType = list[i].getAttribute("appType");
			selectedNetworkServiceIds = selectedNetworkServiceIds + list[i].getAttribute("appId") + appType + ",";
			var tdValue = list[i].getElementsByTagName("a")[0].innerHTML;
			tdValue = tdValue.trim().replace(/\\/g, "\\\\").replace(/&amp;/g,"&");
			selectedNetworkServiceNames = selectedNetworkServiceNames + networkService_head + tdValue + ",";
			totalNum ++;
		}
	}
	if (totalNum > 100) {
		var rightAppDiv = getDom('showErrMsg');
		hm.util.reportFieldError(rightAppDiv, '<s:text name="geneva_26.config.appservice.select.maximum"/>');
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
