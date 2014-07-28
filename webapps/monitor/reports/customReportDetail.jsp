<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.performance.AhCustomReport"%>

<tiles:insertDefinition name="tabView" />

<script>
var formName = 'customReportList';
var allTabs;
var generalTab;
var reportTab;
var REPORT_TYPE_AP=<%=AhCustomReport.REPORT_TYPE_HIVEAP%>;
var REPORT_TYPE_CLIETN=<%=AhCustomReport.REPORT_TYPE_CLIENT%>;
var REPORT_TYPE_SSID=<%=AhCustomReport.REPORT_TYPE_SSID%>;

var REPORT_DETAIL_TYPE_U=<%=AhCustomReport.REPORT_DETAILTYPE_UNIQUE%>;
var REPORT_DETAIL_TYPE_C=<%=AhCustomReport.REPORT_DETAILTYPE_COUNT%>;
var REPORT_DETAIL_TYPE_V=<%=AhCustomReport.REPORT_DETAILTYPE_VALUE%>;
var REPORT_DETAIL_TYPE_A=<%=AhCustomReport.REPORT_DETAILTYPE_AVERAGE%>;

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
	waitingPanel.setHeader("Request is being processed...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_name").disabled == false) {
		document.getElementById(formName + "_dataSource_name").focus();
	}

	allTabs = new YAHOO.widget.TabView("reportTabs", {activeIndex:0});
	generalTab = allTabs.getTab(0);
	reportTab = allTabs.getTab(1);

	var showReportTab = <s:property value="%{showReportTab}"/>;
	if (showReportTab) {
		allTabs.set('activeIndex',1);
		if(document.getElementById("export")) {
			document.getElementById("export").disabled = false;
		}
	} else {
		allTabs.removeTab(reportTab);
		if(document.getElementById("export")) {
			document.getElementById("export").disabled = true;
		}
	}
	
	createWaitingPanel();
}


function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'createDownloadData')
		{
			//showProcessing();
			createTechData();
			return;
		} else if (operation == 'download') {

		} else {
			showProcessing();
		}
		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
		
		document.forms[formName].tabIndex.value = allTabs.get('activeIndex');
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == 'cancel') {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_name");
	var message = hm.util.validateName(inputElement.value, '<s:text name="report.reportList.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}

	return true;
}



var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td = document.getElementById(details.id);
	var value = details.v;
	td.length=0;
	td.length=value.length;
	for(var i = 0; i < value.length; i ++)
	{
		td.options[i].text=value[i];
		td.options[i].value=value[i];
	}
	td.value=value[0];
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function showSsidName(obj) {
	var name = obj.options[obj.selectedIndex].text;
	hm.util.showtitle(obj);
	var url = '<s:url action="customReportList"><s:param name="operation" value="viewSsid"/></s:url>' + "&selectHiveAPName="+encodeURIComponent(name)+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function clickClientIp(value) {
	if (value) {
		Get("hideClientIpAddressDiv").style.display="block";
		Get(formName + "_dataSource_authIp").value="";
	} else {
		Get("hideClientIpAddressDiv").style.display="none";
		Get(formName + "_dataSource_authIp").value="";
	}
}
function clickClientHostName(value) {
	if (value) {
		Get("hideClientHostNameDiv").style.display="block";
		Get(formName + "_dataSource_authHostName").value="";
	} else {
		Get("hideClientHostNameDiv").style.display="none";
		Get(formName + "_dataSource_authHostName").value="";
	}
}
function clickClientUserName(value) {
	if (value) {
		Get("hideClientUserNameDiv").style.display="block";
		Get(formName + "_dataSource_authUserName").value="";
	} else {
		Get("hideClientUserNameDiv").style.display="none";
		Get(formName + "_dataSource_authUserName").value="";
	}
}

function changeReportDetailType(value){
	Get("hideSortKeyHiveApUDiv").style.display="none";
	Get("hideSortKeyHiveApCDiv").style.display="none";
	Get("hideSortKeyHiveApVDiv").style.display="none";
	Get("hideSortKeyClientUDiv").style.display="none";
	Get("hideSortKeyClientCDiv").style.display="none";
	Get("hideSortKeyClientVDiv").style.display="none";
	Get("hideSortKeyClientADiv").style.display="none";
	Get("hideSortKeySsidCDiv").style.display="none";
	Get("hideSortKeySsidVDiv").style.display="none";
	
	Get("hideReportFieldHiveApUDiv").style.display="none";
	Get("hideReportFieldHiveApCDiv").style.display="none";
	Get("hideReportFieldHiveApVDiv").style.display="none";
	Get("hideReportFieldClientUDiv").style.display="none";
	Get("hideReportFieldClientCDiv").style.display="none";
	Get("hideReportFieldClientVDiv").style.display="none";
	Get("hideReportFieldSsidCDiv").style.display="none";
	Get("hideReportFieldSsidVDiv").style.display="none";
	
	var typeValue = Get(formName + "_dataSource_reportType").value;
	if (typeValue==REPORT_TYPE_AP) {
		if (value == REPORT_DETAIL_TYPE_U) {
			Get("hideSortKeyHiveApUDiv").style.display="block";
			Get("hideReportFieldHiveApUDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="none";
			Get("hideReportPeriodDiv").style.display="none";
		} else if (value == REPORT_DETAIL_TYPE_C || value == REPORT_DETAIL_TYPE_A) {
			Get("hideSortKeyHiveApCDiv").style.display="block";
			Get("hideReportFieldHiveApCDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="block";
			Get("hideReportPeriodDiv").style.display="block";
		} else if (value == REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeyHiveApVDiv").style.display="block";
			Get("hideReportFieldHiveApVDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="block";
			Get("hideReportPeriodDiv").style.display="block";
		}
	} else if (typeValue==REPORT_TYPE_CLIETN){
		if (value == REPORT_DETAIL_TYPE_U) {
			Get("hideSortKeyClientUDiv").style.display="block";
			Get("hideReportFieldClientUDiv").style.display="block";
		} else if (value == REPORT_DETAIL_TYPE_C) {
			Get("hideSortKeyClientCDiv").style.display="block";
			if (Get(formName + "_longSortClientC").value==2299) {
				Get("hideReportFieldClientCDiv").style.display="none";
			} else {
				Get("hideReportFieldClientCDiv").style.display="block";
			}
		} else if (value == REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeyClientVDiv").style.display="block";
			Get("hideReportFieldClientVDiv").style.display="block";
		} else if (value == REPORT_DETAIL_TYPE_A ) {
			Get("hideSortKeyClientADiv").style.display="block";
			Get("hideReportFieldClientCDiv").style.display="block";
		}
	} else {
		if (value == REPORT_DETAIL_TYPE_C || value == REPORT_DETAIL_TYPE_A ) {
			Get("hideSortKeySsidCDiv").style.display="block";
			Get("hideReportFieldSsidCDiv").style.display="block";
		} else if (value == REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeySsidVDiv").style.display="block";
			Get("hideReportFieldSsidVDiv").style.display="block";
		} 
	}	
}

function changeReportType(value){
	Get("hideSortKeyHiveApUDiv").style.display="none";
	Get("hideSortKeyHiveApCDiv").style.display="none";
	Get("hideSortKeyHiveApVDiv").style.display="none";
	Get("hideSortKeyClientUDiv").style.display="none";
	Get("hideSortKeyClientCDiv").style.display="none";
	Get("hideSortKeyClientVDiv").style.display="none";
	Get("hideSortKeyClientADiv").style.display="none";
	Get("hideSortKeySsidCDiv").style.display="none";
	Get("hideSortKeySsidVDiv").style.display="none";
	
	Get("hideReportFieldHiveApUDiv").style.display="none";
	Get("hideReportFieldHiveApCDiv").style.display="none";
	Get("hideReportFieldHiveApVDiv").style.display="none";
	Get("hideReportFieldClientUDiv").style.display="none";
	Get("hideReportFieldClientCDiv").style.display="none";
	Get("hideReportFieldClientVDiv").style.display="none";
	Get("hideReportFieldSsidCDiv").style.display="none";
	Get("hideReportFieldSsidVDiv").style.display="none";
	
	if (value==REPORT_TYPE_AP) {
		var detailType = document.getElementById(formName+"_dataSource_reportDetailType");
		detailType.length=0;
		detailType.length=4;
		detailType.options[0].value=REPORT_DETAIL_TYPE_U;
		detailType.options[0].text='Unique Value';
		detailType.options[0].selected=true;
		detailType.value=detailType.options[0].value;
		detailType.text=detailType.options[0].text;
		detailType.options[1].value=REPORT_DETAIL_TYPE_C;
		detailType.options[1].text='Sum of Values';
		detailType.options[2].value=REPORT_DETAIL_TYPE_V;
		detailType.options[2].text='Individual Value';
		detailType.options[3].value=REPORT_DETAIL_TYPE_A;
		detailType.options[3].text='Average Values';
	
		//Get("hideUniqueRadioDiv").style.display="block";
		Get("hideSsidNameDiv").style.display="none";
		
		Get("hideClientMacConditionDiv").style.display="none";
		Get(formName + "_dataSource_authMac").value="";
		
		Get("hideClientOtherConditionDiv").style.display="none";
		Get(formName + "_checkIp").checked=false;
		Get(formName + "_checkHostName").checked=false;
		Get(formName + "_checkUserName").checked=false;
		
		Get("hideClientIpAddressDiv").style.display="none";
		Get(formName + "_dataSource_authIp").value="";
		Get("hideClientHostNameDiv").style.display="none";
		Get(formName + "_dataSource_authHostName").value="";
		Get("hideClientUserNameDiv").style.display="none";
		Get(formName + "_dataSource_authUserName").value="";
		
		
		if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_U) {
			Get("hideSortKeyHiveApUDiv").style.display="block";
			Get("hideReportFieldHiveApUDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="none";
			Get("hideReportPeriodDiv").style.display="none";
			
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_C || 
			Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_A) {
			Get("hideSortKeyHiveApCDiv").style.display="block";
			Get("hideReportFieldHiveApCDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="block";
			Get("hideReportPeriodDiv").style.display="block";
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeyHiveApVDiv").style.display="block";
			Get("hideReportFieldHiveApVDiv").style.display="block";
			Get("hideInterfaceRoleDiv").style.display="block";
			Get("hideReportPeriodDiv").style.display="block";
		}
	} else if (value==REPORT_TYPE_CLIETN){
		var detailType = document.getElementById(formName+"_dataSource_reportDetailType");
		detailType.length=0;
		detailType.length=4;
		detailType.options[0].value=REPORT_DETAIL_TYPE_U;
		detailType.options[0].text='Unique Value';
		detailType.options[0].selected=true;
		detailType.value=detailType.options[0].value;
		detailType.text=detailType.options[0].text;
		detailType.options[1].value=REPORT_DETAIL_TYPE_C;
		detailType.options[1].text='Sum of Values';
		detailType.options[2].value=REPORT_DETAIL_TYPE_V;
		detailType.options[2].text='Individual Value';
		detailType.options[3].value=REPORT_DETAIL_TYPE_A;
		detailType.options[3].text='Average Values';
		
		Get("hideSsidNameDiv").style.display="none";
		Get("hideInterfaceRoleDiv").style.display="none";
		Get("hideReportPeriodDiv").style.display="block";
		
		Get("hideClientMacConditionDiv").style.display="block";
		Get("hideClientOtherConditionDiv").style.display="block";
		
		if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_U) {
			Get("hideSortKeyClientUDiv").style.display="block";
			Get("hideReportFieldClientUDiv").style.display="block";
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_C) {
			Get("hideSortKeyClientCDiv").style.display="block";
			if (Get(formName + "_longSortClientC").value==2299) {
				Get("hideReportFieldClientCDiv").style.display="none";
			} else {
				Get("hideReportFieldClientCDiv").style.display="block";
			}
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeyClientVDiv").style.display="block";
			Get("hideReportFieldClientVDiv").style.display="block";
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_A) {
			Get("hideSortKeyClientADiv").style.display="block";
			Get("hideReportFieldClientCDiv").style.display="block";
		}
	} else {
		var detailType = document.getElementById(formName+"_dataSource_reportDetailType");
		detailType.length=0;
		detailType.length=3;
		detailType.options[0].value=REPORT_DETAIL_TYPE_C;
		detailType.options[0].text='Sum of Values';
		detailType.options[0].selected=true;
		detailType.value=detailType.options[0].value;
		detailType.text=detailType.options[0].text;
		detailType.options[1].value=REPORT_DETAIL_TYPE_V;
		detailType.options[1].text='Individual Value';
		detailType.options[2].value=REPORT_DETAIL_TYPE_A;
		detailType.options[2].text='Average Values';
		
		Get("hideSsidNameDiv").style.display="block";
		Get("hideInterfaceRoleDiv").style.display="none";
		
		Get("hideClientMacConditionDiv").style.display="none";
		Get(formName + "_dataSource_authMac").value="";
		
		Get("hideClientOtherConditionDiv").style.display="none";
		Get(formName + "_checkIp").checked=false;
		Get(formName + "_checkHostName").checked=false;
		Get(formName + "_checkUserName").checked=false;
		
		Get("hideClientIpAddressDiv").style.display="none";
		Get(formName + "_dataSource_authIp").value="";
		Get("hideClientHostNameDiv").style.display="none";
		Get(formName + "_dataSource_authHostName").value="";
		Get("hideClientUserNameDiv").style.display="none";
		Get(formName + "_dataSource_authUserName").value="";
		
		Get("hideReportPeriodDiv").style.display="block";
		
		//Get(formName+"_dataSource_reportDetailType").value = REPORT_DETAIL_TYPE_U;
		
		if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_U) {
			//Get("hideSortKeySsidCDiv").style.display="none";
			//Get("hideReportFieldSsidCDiv").style.display="none";
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_C || 
			Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_A) {
			Get("hideSortKeySsidCDiv").style.display="block";
			Get("hideReportFieldSsidCDiv").style.display="block";
		} else if (Get(formName+"_dataSource_reportDetailType").value==REPORT_DETAIL_TYPE_V) {
			Get("hideSortKeySsidVDiv").style.display="block";
			Get("hideReportFieldSsidVDiv").style.display="block";
		} 
	}					
}

function createTechData()
{
	var url = "<s:url action='customReportList' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createDataResult, failure:abortResult,timeout: 600000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}
var createDataResult = function(o)
{	
	hm.util.hide('processing');
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.success) {
		showHangMessage("Download exported file.");
		hm.util.show('downloadSection');
	} else {
		if (result.eword) {
			showErrorMessage(result.eword);
		} else {
			showErrorMessage("Create export file failed!");
		}
	}
}

var abortResult = function(o)
{
	hm.util.hide('processing');
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}
function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	hm.util.show("noteSection");
}

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
}

function initNoteSection()
{
	hm.util.hide('downloadSection');
	hm.util.hide('noteSection');
}

function clickCheckAll(checkAll,name){
	var inputElements = document.getElementsByName('select' + name + 'Ids');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
    		if (cb.value!=1101 && cb.value!=1102 && cb.value!=1201 && cb.value!=1202 && 
    			cb.value!=1301 && cb.value!=1302 && cb.value!=1303 && 
    			cb.value!=2101 && cb.value!=2102 && cb.value!=2103 && 
    			cb.value!=2201 && cb.value!=2202 && cb.value!=2203 &&
    			cb.value!=2301 && cb.value!=2302 && cb.value!=2303 && cb.value!=2304 &&
    			cb.value!=3201 && cb.value!=3202 && 
    			cb.value!=3301 && cb.value!=3302 && cb.value!=3303){
				cb.checked = checkAll.checked;
				//this.toggleRow(cb);
			}
		}
	}
}
function clickCheckItem(checkOne,name){
	//alert(checkOne.value);
    if (checkOne.value==1101 || checkOne.value==1102 || checkOne.value==1201 || checkOne.value==1202 ||
    	checkOne.value==1301 || checkOne.value==1302 || checkOne.value==1303 || 
    	checkOne.value==2101 || checkOne.value==2102 || checkOne.value==2103 || 
    	checkOne.value==2201 || checkOne.value==2202 || checkOne.value==2203 ||
    	checkOne.value==2301 || checkOne.value==2302 || checkOne.value==2303 || checkOne.value==2304 ||
    	checkOne.value==3201 || checkOne.value==3202 || 
    	checkOne.value==3301 || checkOne.value==3302 || checkOne.value==3303){
		return false;
	}
	if (!checkOne.checked){
		Get("check" + name + "All").checked=false;
	}
}

function changeSortKeyType(value) {
	if (value==2299) {
		Get("hideReportFieldClientCDiv").style.display="none";
	} else {
		Get("hideReportFieldClientCDiv").style.display="block";
	}
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="customReportList" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}

function gotoPagePress(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	
	if (keycode == 13)
	{
		if (document.getElementById('cuSearchGotoPageId').value.length > 0)
		{
			submitAction('changeGotoPage');
		}
		
		return false;
	}
	
	// permit input number
	if(48 <= keycode && keycode <=57)
	{
		return true;
	}

	return false;
}

</script>
<div id="content"><s:form action="customReportList">
	<s:hidden name="tabIndex" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Run Now"
						class="button" onClick="submitAction('run');"
						<s:property value="updateDisabled" />></td>
					<s:if test="%{updateDisabled == ''}">
					<td><input type="button" name="ignore" value="Export" id="export"
						class="button" onClick="submitAction('createDownloadData');"
						<s:property value="updateDisabled" />></td>
					</s:if>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
					<td>
						<s:if test="%{reportResult!=null || reportResult.size > 0}">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td nowrap="nowrap" style="padding: 3px 10px 0px 30px;">Total: <s:property
										value="%{reportResult.size}" /></td>
									<td nowrap="nowrap" style="padding: 3px 4px 0px 3px;">Items per page:</td>
									<td style="padding: 2px 10px 0px 0px;"><s:select name="dataSource.cuPageSize"
										list="#{'15':'15', '20':'20', '25':'25', '30':'30', '50':'50', '100':'100'}"
										value="dataSource.cuPageSize" onchange="submitAction('changePageSize');" /></td>
									<s:if test="%{dataSource.cuPageIndex > 1}">
										<td style="padding-left: 10px">
											<a href="javascript: submitAction('changePreviousPage');">
												<img src="<s:url value="/images/search/Back_off.png" />"
													onMouseOver="this.src='<s:url value="/images/search/Back_on.png" />'"
													onMouseOut="this.src='<s:url value="/images/search/Back_off.png" />'"
													title="Previous" width="24" height="24" border="0px"
													class="dblk"> </a>
										</td>
									</s:if>
									<s:else>
										<td style="padding-left: 10px">
											<img src="<s:url value="/images/search/Back_gray.png" />"
												width="24" height="24" class="dblk">
										</td>
									</s:else>
									<td id="cuPageNumber" nowrap
										style="padding: 3px 4px 0px 4px;">
										<s:property value="%{dataSource.cuPageIndex}" />
										/
										<s:property value="%{dataSource.cuPageCount}" />
									</td>
									<s:if
										test="%{dataSource.cuPageIndex < dataSource.cuPageCount}">
										<td style="padding-right: 0px">
											<a href="javascript: submitAction('changeNextPage');">
												<img
													src="<s:url value="/images/search/Forward_off.png" />"
													onMouseOver="this.src='<s:url value="/images/search/Forward_on.png" />'"
													onMouseOut="this.src='<s:url value="/images/search/Forward_off.png" />'"
													title="Next" width="24" height="24" border="0px"
													class="dblk"> </a>
										</td>
									</s:if>
									<s:else>
										<td style="padding-right: 0px">
											<img
												src="<s:url value="/images/search/Forward_gray.png" />"
												width="24" height="24" class="dblk">
										</td>
									</s:else>
									<td valign="bottom" style="padding-left: 10px;">
										<s:textfield name="cuSearchGotoPage" id="cuSearchGotoPageId"
											cssStyle="width: 35px;" onkeypress="return gotoPagePress(event);"  />
									</td>
									<s:if
										test="%{dataSource.cuPageCount > 1}">
										<td style="padding-right: 20px">
											<a
												href="javascript: submitAction('changeGotoPage');">
												<img src="<s:url value="/images/search/GoTo_off.png" />"
													onMouseOver="this.src='<s:url value="/images/search/GoTo_on.png" />'"
													onMouseOut="this.src='<s:url value="/images/search/GoTo_off.png" />'"
													title="Go to page" width="24" height="24" border="0px"
													class="dblk"> </a>
										</td>
									</s:if>
									<s:else>
										<td style="padding-right: 20px">
											<img
												src="<s:url value="/images/search/GoTo_gray.png" />"
												width="24" height="24" class="dblk">
										</td>
									</s:else>
								</tr>
							</table>
						</s:if>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td colspan="10">
				<div id="noteSection" style="display:none">
					<table width="400px" border="0" cellspacing="0" cellpadding="0"
						class="note">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td id="noteTD">
							</td>
							<td class="buttons">
								<div id="downloadSection" style="display:none">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<input type="button" id="downloadBtn" name="ignore"
													value="Download" class="button"
													onClick="submitAction('download');">
											</td>
											<td>
												<input type="button" id="cancelBtn" name="ignore"
													value="Cancel" class="button"
													onClick="initNoteSection();">
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">

			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td>

					<div id="reportTabs" class="yui-navset">
					<ul class="yui-nav">
						<li class="selected"><a href="#tab1"><em><s:text
							name="report.reportList.tab.general" /></em></a></li>
						<li><a href="#tab2"><em><s:text
							name="report.reportList.tab.report" /></em></a></li>
					</ul>
					<div class="yui-content"><!-- begin general  -->
					<div id="tab1">

					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="4"></td>
								</tr>
								<tr>
									<td class="labelT1" width="130px"><s:text
										name="report.reportList.name" /><font color="red"><s:text
										name="*" /></font></td>
									<td colspan="3"><s:textfield name="dataSource.name"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="32" disabled="%{disabledName}" />&nbsp;<s:text
										name="report.reportList.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="130px"><s:text
										name="report.customReport.description" /></td>
									<td colspan="3" nowrap="nowrap"><s:textfield name="dataSource.description"
										size="48"
										maxlength="64"/>&nbsp;<s:text
										name="config.ssid.description_range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="130px"><s:text
										name="report.reportList.title.type" /></td>
									<td colspan="3"><s:select name="dataSource.reportType"
										list="%{enumReportType}" listKey="key" listValue="value"
										value="dataSource.reportType" cssStyle="width: 150px;" 
										onchange="changeReportType(this.options[this.selectedIndex].value);"/></td>
								</tr>
								<tr>
									<td class="labelT1" width="130px"><s:text
										name="report.reportList.location" /></td>
									<td width="180px"><s:select name="locationId"
										list="%{location}" listKey="id" listValue="value"
										value="locationId" cssStyle="width: 150px;" /></td>
									<td colspan="2">
										<div style="display:<s:property value="%{showReportPeriod}"/>" id="hideReportPeriodDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="110px"><s:text
														name="report.reportList.reportPeriod" /></td>
													<td><s:select name="dataSource.reportPeriod"
														list="%{enumReportPeriodType}" listKey="key" listValue="value"
														value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td class="labelT1" width="130px"><s:text
										name="report.reportList.apName" /></td>
									<td colspan="3"><s:textfield name="dataSource.apName"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="32" /></td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showSsidName}"/>" id="hideSsidNameDiv">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="130px"><s:text
													name="report.reportList.ssid" /></td>
												<td><s:textfield name="dataSource.ssidName"
													size="24"
													onkeypress="return hm.util.keyPressPermit(event,'name');"
													maxlength="32" /></td>
											</tr>
										</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showInterfaceRole}"/>" id="hideInterfaceRoleDiv">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="130px"><s:text
													name="report.customReport.interfaceRole" /></td>
												<td><s:select name="dataSource.interfaceRole"
													list="%{enumInterfaceType}" listKey="key" listValue="value"
													value="dataSource.interfaceRole" cssStyle="width: 150px;" /></td>
											</tr>
										</table>
										</div>
									</td>	
								</tr>
								
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showClientCondition}"/>" id="hideClientMacConditionDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.reportList.clientAuth.macAddress" /></td>
													<td width="200px"><s:textfield name="dataSource.authMac" size="24"
														onkeypress="return hm.util.keyPressPermit(event,'hex');"
														maxlength="12" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showClientCondition}"/>" id="hideClientOtherConditionDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.otherClientInfo" /></td>
													<td><s:checkbox name="checkIp" onclick="clickClientIp(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkIpAddress" /></td>
													<td><s:checkbox name="checkHostName" onclick="clickClientHostName(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkHostName" /></td>
													<td><s:checkbox name="checkUserName" onclick="clickClientUserName(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkUserName" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>

								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showClientIpAddress}"/>" id="hideClientIpAddressDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.reportList.title.currentClientIpAddress" /></td>
													<td><s:textfield name="dataSource.authIp"
														size="24"
														onkeypress="return hm.util.keyPressPermit(event,'ip');"
														maxlength="15" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showClientHostName}"/>" id="hideClientHostNameDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.reportList.title.currentClientHostName" /></td>
													<td><s:textfield name="dataSource.authHostName"
														size="24" maxlength="32" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showClientUserName}"/>" id="hideClientUserNameDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.reportList.title.currentClientUserName" /></td>
													<td><s:textfield name="dataSource.authUserName"
														size="24" maxlength="32" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td width="130px" class="labelT1" ><s:text
										name="report.customReport.reportAs" /></td>
									<td colspan="3"><s:select name="dataSource.reportDetailType"
										list="%{enumReportDetailType}" listKey="key" listValue="value"
										value="dataSource.reportDetailType" cssStyle="width: 250px;"
										onchange = "changeReportDetailType(this.options[this.selectedIndex].value)" /></td>
								</tr>
								<tr>
									<td colspan="4">
										<div style="display:<s:property value="%{showSortKeyHiveApU}"/>" id="hideSortKeyHiveApUDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortHiveApU"
														list="%{listSortByFieldHiveApU}" listKey="key" listValue="value"
														value="longSortHiveApU" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyHiveApC}"/>" id="hideSortKeyHiveApCDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortHiveApC"
														list="%{listSortByFieldHiveApC}" listKey="key" listValue="value"
														value="longSortHiveApC" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyHiveApV}"/>" id="hideSortKeyHiveApVDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortHiveApV"
														list="%{listSortByFieldHiveApV}" listKey="key" listValue="value"
														value="longSortHiveApV" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientU}"/>" id="hideSortKeyClientUDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortClientU"
														list="%{listSortByFieldClientU}" listKey="key" listValue="value"
														value="longSortClientU" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientC}"/>" id="hideSortKeyClientCDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortClientC"
														list="%{listSortByFieldClientC}" listKey="key" listValue="value"
														value="longSortClientC" cssStyle="width: 250px;" 
														onchange="changeSortKeyType(this.options[this.selectedIndex].value);"/></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientV}"/>" id="hideSortKeyClientVDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortClientV"
														list="%{listSortByFieldClientV}" listKey="key" listValue="value"
														value="longSortClientV" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientA}"/>" id="hideSortKeyClientADiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortClientA"
														list="%{listSortByFieldClientA}" listKey="key" listValue="value"
														value="longSortClientA" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeySsidC}"/>" id="hideSortKeySsidCDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortSsidC"
														list="%{listSortByFieldSsidC}" listKey="key" listValue="value"
														value="longSortSsidC" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeySsidV}"/>" id="hideSortKeySsidVDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="130px"><s:text
														name="report.customReport.sortingKey" /></td>
													<td><s:select name="longSortSsidV"
														list="%{listSortByFieldSsidV}" listKey="key" listValue="value"
														value="longSortSsidV" cssStyle="width: 250px;" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td width="130px" class="labelT1" ><s:text
										name="report.customReport.orderType" /></td>
									<td colspan="3"><s:select name="dataSource.sortByType"
										list="%{enumSortByType}" listKey="key" listValue="value"
										value="dataSource.sortByType" cssStyle="width: 250px;" /></td>
								</tr>
								<tr>
									<td colspan="4" style="padding-left: 10px">
										<div style="display:<s:property value="%{showSortKeyHiveApU}"/>" id="hideReportFieldHiveApUDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkHiveApUAll"
														onClick="clickCheckAll(this,'HiveApU');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 1 && detailType ==1}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApUIds" value="<s:property value="id" />" checked 
																		onClick="return clickCheckItem(this,'HiveApU');"/>
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApUIds" value="<s:property value="id" />"
																	onClick="return clickCheckItem(this,'HiveApU');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyHiveApC}"/>" id="hideReportFieldHiveApCDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkHiveApCAll"
														onClick="clickCheckAll(this,'HiveApC');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 1 && detailType ==2}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApCIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'HiveApC');" checked />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApCIds" value="<s:property value="id" />"
																	onClick="return clickCheckItem(this,'HiveApC');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyHiveApV}"/>" id="hideReportFieldHiveApVDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkHiveApVAll"
														onClick="clickCheckAll(this,'HiveApV');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 1 && detailType ==3}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApVIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'HiveApV');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectHiveApVIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'HiveApV');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientU}"/>" id="hideReportFieldClientUDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkClientUAll"
														onClick="clickCheckAll(this,'ClientU');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 2 && detailType ==1}">
														<tr class="<s:property value="%{#rowClass}"/>">
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectClientUIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'ClientU');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectClientUIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'ClientU');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientCField}"/>" id="hideReportFieldClientCDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkClientCAll"
														onClick="clickCheckAll(this,'ClientC');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 2 && detailType ==2}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectClientCIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'ClientC');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectClientCIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'ClientC');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeyClientV}"/>" id="hideReportFieldClientVDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkClientVAll"
														onClick="clickCheckAll(this,'ClientV');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 2 && detailType ==3}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectClientVIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'ClientV');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectClientVIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'ClientV');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeySsidC}"/>" id="hideReportFieldSsidCDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkSsidCAll"
														onClick="clickCheckAll(this,'SsidC');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 3 && detailType ==2}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectSsidCIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'SsidC');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectSsidCIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'SsidC');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
										<div style="display:<s:property value="%{showSortKeySsidV}"/>" id="hideReportFieldSsidVDiv">
											<table cellspacing="0" cellpadding="0" border="0" class="view">
												<tr>
													<th class="check"><input type="checkbox" id="checkSsidVAll"
														onClick="clickCheckAll(this,'SsidV');"></th>
													<th><s:text name="report.customReport.reportFieldName"/></th>
													<th><s:text name="report.customReport.strUnit"/></th>
												</tr>
												<s:if test="%{lstCustomField.size == 0}">
													<ah:emptyList />
												</s:if>
												<s:iterator value="lstCustomField" status="status">
													<s:if test="%{type == 3 && detailType ==3}">
														<tr>
															<s:if test="%{selected == true}">
																<td class="listCheck">
																	<input type="checkbox" name="selectSsidVIds" value="<s:property value="id" />" 
																		onClick="return clickCheckItem(this,'SsidV');" checked="checked" />
																</td>
															</s:if>
														   	<s:else>
																<td class="listCheck">
																	<input type="checkbox" name="selectSsidVIds" value="<s:property value="id" />"
																		onClick="return clickCheckItem(this,'SsidV');">
																</td>
															</s:else>
															<td class="list"><s:property value="fieldString" /></td>
															<td class="list"><s:property value="strUnit" /></td>
														</tr>
													</s:if>
												</s:iterator>
											</table>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="8"></td>
						</tr>
					</table>
					</div>

					<!-- end general  --> <!-- begin report  -->
					<div id="tab2">
					<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
						<s:if test="%{dataSource.reportType==2 && dataSource.longSortBy == 2299}">
							<tr>
								<th align="left"><s:text name="report.reportList.title.vendor" /></th>
								<th align="left"><s:text name="report.reportList.title.vendorCount" /></th>
							</tr>
						</s:if>
						<s:else>
							<tr>
								<s:iterator value="dataSource.customFields" status="status">
									<th><s:property value="fieldString"/></th>
								</s:iterator>
							</tr>
						</s:else>
						<s:if test="%{reportResult==null || reportResult.size == 0}">
							<ah:emptyList />
						</s:if>
						<s:iterator value="reportResult" status="stuts">
							<s:if test="%{#stuts.index >= cuStartItem && #stuts.index < cuEndItem}">
						    <tr>
						       	<s:iterator value="reportResult[#stuts.index]" > 
						       		<td class="list" nowrap="nowrap"> <s:property/></td> 
						       	</s:iterator> 
						    </tr>
						    </s:if>
						</s:iterator>
					</table>
					</div>
					</div>
					</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
