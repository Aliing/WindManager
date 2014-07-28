<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<tiles:insertDefinition name="tabView" />
<tiles:insertDefinition name="flashHeader" />

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/paginator/assets/skins/sam/paginator.css"  includeParams="none"/>" />

<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/element/element-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/datatable/datatable-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/paginator/paginator-min.js"  includeParams="none"/>"></script>



<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}
a.top_menu_report_sel        {color: #FFCC00; text-decoration: none;}
a.top_menu_report_sel:link   {color: #FFCC00; text-decoration: none;}
a.top_menu_report_sel:hover  {color: #FFCC00; text-decoration: underline;}
a.top_menu_report_sel:active {color: #FFCC00; text-decoration: none;}

.top_menu_report_item {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	font-weight: bold;
	padding: 0 10px 0px 10px;
	color: #FFFFFF;
}
.top_menu_report_bg_sel{
	background: #444;
	height: 25px;
}
#calendarcontainer {
	padding-left: 10px;
}

#calendarpicker button {
    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
}

.yui-skin-sam .yui-dt table { 
	width: 100%; 
} 

.closePanelTD { 
	text-align: right;
} 
.closePanelTD span{ 
	cursor:pointer;
	font-weight: bold;
} 
</style>
<script type="text/javascript">
    function formatValue(value){
        var v=value;
        if(value.length==0)
           return v;
        if(parseInt(value)<=9)
           v="0"+value;
        return v;
    }
YAHOO.util.Event.onDOMReady(function () {
        function onButtonClick() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu.setBody("&#32;");
            oCalendarMenu.body.id = "calendarcontainer";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id);
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {
                    oCalendarMenu.show();
                }, 0);
            });

            /*
                Subscribe to the Calendar instance's "select" event to
                update the month, day, year form fields when the user
                selects a date.
            */
            oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
                var aDate;
                if (p_aArgs) {
                    aDate = p_aArgs[0][0];
                    var beginDate_doc = document.getElementById("startTime");
                    beginDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick);

        };

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");
        // Create a Button instance of type "menu"
        var startTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "startdatefields" });


        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton.on("click", onButtonClick);

	});

</script>
<script>
var formName = 'reportList';
var allTabs;
var generalTab;
var reportTab;

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
	if (!document.getElementById("enabledEmail").checked) {
		document.getElementById("emailAddress").value="";
		document.getElementById("emailAddress").readOnly=true;
	}

	if (document.getElementById(formName +"_dataSource_recurrenceType1").checked) {
		document.getElementById("selectWeekDay").disabled=true;
	} else {
		document.getElementById("selectWeekDay").disabled=false;
	}
	var showReportTab = <s:property value="%{showReportTab}"/>;
	if (document.getElementById(formName + "_dataSource_name").disabled == false && !showReportTab) {
		document.getElementById(formName + "_dataSource_name").focus();
	}

	allTabs = new YAHOO.widget.TabView("reportTabs", {activeIndex:0});
	generalTab = allTabs.getTab(0);
	reportTab = allTabs.getTab(1);

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

function submitActionChange(operation,buttonType) {
	document.forms[formName].buttonType.value = buttonType;
	submitAction(operation);
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

	if (document.getElementById("excuteType2").checked) {
		if (document.getElementById("startTime").value=="") {
		  hm.util.reportFieldError(document.getElementById("startTime"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.guiStartTime" /></s:param></s:text>');
          return false;
		}
		if (!document.getElementById("enabledEmail").checked) {
		  hm.util.reportFieldError(document.getElementById("enabledEmail"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
          return false;
		}
	}

	if (document.getElementById("enabledEmail").checked) {
		if (document.getElementById("emailAddress").value.trim()=="") {
		  hm.util.reportFieldError(document.getElementById("emailAddress"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
          return false;
		}

		var emails = document.getElementById("emailAddress").value.split(";");
		for (var i=0;i<emails.length;i++) {
			if (i==emails.length-1 && emails[i].trim()=="") {
				break;
			}
			if (!hm.util.validateEmail(emails[i].trim())) {
				hm.util.reportFieldError(document.getElementById("emailAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
				document.getElementById("emailAddress").focus();
				return false;
			}
		}
	}
	return true;
}

function show_hideSchedule(value) {
	var divSchedule = document.getElementById("hideSchedule");
	if (value == "2") {
  		divSchedule.style.display="";
	} else {
	  divSchedule.style.display="none";
	}
}

function show_hideRecurrence(value) {
	var divSchedule = document.getElementById("hideRecurrence");
	if (value) {
  		divSchedule.style.display="";
	} else {
	  divSchedule.style.display="none";
	}
}

function enabledEmailAddress() {
	if (!document.getElementById("enabledEmail").checked) {
		document.getElementById("emailAddress").value="";
		document.getElementById("emailAddress").readOnly=true;
	} else {
		document.getElementById("emailAddress").readOnly=false;
	}
}

function enabledWeekDay(value) {
	var selectWeekDay = document.getElementById("selectWeekDay");
	if (value=='1'){
		selectWeekDay.disabled=true;
	} else {
		selectWeekDay.disabled=false;
	}
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
	var url = '<s:url action="reportList"><s:param name="operation" value="viewSsid"/></s:url>' + "&selectHiveAPName="+encodeURIComponent(name)+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function changeReportType(value){
	if (value=='clientSla'|| value=='hiveApSla'){
		document.getElementById("slaTypeDiv").style.display="block";
	} else {
		document.getElementById("slaTypeDiv").style.display="none";
	}
	if (value=='radioInterference'|| value=='clientSession'){
		document.getElementById("hideTimeAggregation").style.display="none";
	} else {
		document.getElementById("hideTimeAggregation").style.display="block";
	}
}

function changeNewOldReportType(value) {
	<s:if test="%{selectedL2Feature.key=='clientReports'}">
		if (value == "1") {
			Get("showReportTypeDiv").style.display="block"
			Get(formName + "_dataSource_reportType").value='clientSession';
			changeReportType("clientSession");
		} else {
			Get("showReportTypeDiv").style.display="none"
			Get(formName + "_dataSource_reportType").value='clientSession';
			changeReportType("clientSession");
		}
	</s:if>
	<s:elseif test="%{selectedL2Feature.key=='radioReports'}">
		if (value == "1") {
			Get("showReportTypeDiv").style.display="block"
			Get(formName + "_dataSource_reportType").value='channelPowerNoise';
			changeReportType("channelPowerNoise");
		} else {
			Get("showReportTypeDiv").style.display="none"
			Get(formName + "_dataSource_reportType").value='channelPowerNoise';
			changeReportType("channelPowerNoise");
			document.getElementById("hideTimeAggregation").style.display="none";
		}
	
	</s:elseif>
}

function createTechData()
{
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createDataResult, failure:abortResult,timeout: 3000000}, null);
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

var currentAP=null;
var currentClient=null;

/**
var initDatatableCSla= false;
var initDatatableCTxDrop= false;
var initDatatableCRxDrop= false;
var initDatatableCAirtime= false;


var initDatatableAPSla= false;
var initDatatableAPCrc= false;
var initDatatableAPTxDrop= false;
var initDatatableAPRxDrop= false;
var initDatatableAPTxRetry= false;
var initDatatableAPAirtime= false;
**/

var basicCSla;
//var basicCTxDrop;
//var basicCRxDrop;
var basicCScore
var basicCAirtime;

var basicAPSla;
var basicAPCrc;
var basicAPTxDrop;
var basicAPRxDrop;
var basicAPTxRetry;
var basicAPAirtime;

var basicCSlaData = [];
//var basicCTxDropData = [];
//var basicCRxDropData = [];
var basicCScoreData = [];
var basicCAirtimeData = [];


var basicAPSlaData = [];
var basicAPCrcData = [];
var basicAPTxDropData = [];
var basicAPRxDropData = [];
var basicAPTxRetryData = [];
var basicAPAirtimeData = [];

YAHOO.example.ApSlaPagination = function() {
	basicAPSla=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", sortable:true, resizeable:true},
            {key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", sortable:true, resizeable:true},
            {key:"status", label:"<s:text name='report.reportList.sla.status'/>", sortable:true, resizeable:true},
            {key:"guaBandwidth", label:"<s:text name='report.reportList.sla.configBandwidth'/>",sortable:true, resizeable:true},
            {key:"actBandwidth", label:"<s:text name='report.reportList.sla.actualBandwidth'/>", sortable:true, resizeable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPSlaData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","clientMac","status","guaBandwidth","actBandwidth","channelCu","interferenceCu","txCu","rxCu","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComSlaPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComSla", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPSla=true;
}

YAHOO.example.ApCrcPagination = function() {
	basicAPCrc=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"ifName", label:"<s:text name='report.customReport.interfaceRole'/>", resizeable:true,sortable:true},
            {key:"crcErrorRate", label:"<s:text name='report.summary.apInfo.crcErrorCount'/>", resizeable:true,sortable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPCrcData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","ifName","crcErrorRate","channelCu","interferenceCu","txCu","rxCu","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComCrcPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComCrc", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPCrc=true;
}

YAHOO.example.ApTxDropPagination = function() {
	basicAPTxDrop=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"ifName", label:"<s:text name='report.customReport.interfaceRole'/>", resizeable:true,sortable:true},
            {key:"txDrop", label:"<s:text name='report.reportList.nonCompliance.txDrop'/>", resizeable:true,sortable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPTxDropData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","ifName","txDrop","channelCu","interferenceCu","txCu","rxCu","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComTxDropPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComTxDrop", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPTxDrop=true;
}

YAHOO.example.ApRxDropPagination = function() {
	basicAPRxDrop=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"ifName", label:"<s:text name='report.customReport.interfaceRole'/>", resizeable:true,sortable:true},
            {key:"rxDrop", label:"<s:text name='report.reportList.nonCompliance.rxDrop'/>", resizeable:true,sortable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPRxDropData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
             fields: ["name","ifName","rxDrop","channelCu","interferenceCu","txCu","rxCu","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComRxDropPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComRxDrop", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPRxDrop=true;
}

YAHOO.example.ApTxRetryPagination = function() {
	basicAPTxRetry=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"ifName", label:"<s:text name='report.customReport.interfaceRole'/>", resizeable:true,sortable:true},
            {key:"txRetry", label:"<s:text name='report.summary.apInfo.txRetryCount'/>", resizeable:true,sortable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPTxRetryData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
           fields: ["name","ifName","txRetry","channelCu","interferenceCu","txCu","rxCu","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComTxRetryPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComTxRetry", myColumnDefs,
                myDataSource, oConfigs);
                
        return {

            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPTxRetry=true;
}

YAHOO.example.ApAirtimePagination = function() {
	basicAPAirtime=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"ifName", label:"<s:text name='report.customReport.interfaceRole'/>", resizeable:true,sortable:true},
            {key:"txAirtime", label:"<s:text name='report.summary.cInfo.txAirtime'/>", resizeable:true,sortable:true},
            {key:"rxAirtime", label:"<s:text name='report.summary.cInfo.rxAirtime'/>", resizeable:true,sortable:true},
            {key:"channelCu", label:"<s:text name='report.reportList.sla.cu.channel'/>", sortable:true, resizeable:true},
            {key:"interferenceCu", label:"<s:text name='report.reportList.sla.cu.interf'/>", sortable:true, resizeable:true},
            {key:"txCu", label:"<s:text name='report.reportList.sla.cu.tx'/>", sortable:true, resizeable:true},
            {key:"rxCu", label:"<s:text name='report.reportList.sla.cu.rx'/>", sortable:true, resizeable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAPAirtimeData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","ifName","txAirtime","rxAirtime","channelCu","interferenceCu","txCu","rxCu","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'hiveApComAirtimePage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("hiveApComAirtime", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableAPAirtime=true;
}

function showCDetailPagging(type, name){
	if (currentClient==null) {
		currentClient = name;
	} else if (currentClient != name) {
		currentClient = name;
		Get("clientSlaDiv").style.display="none";
		Get("clientScoreDiv").style.display="none";
		Get("clientAirtimeDiv").style.display="none";
	}
	if (type == 'sla') {
		changeCSla(name);
	} else if (type=='score') {
		changeCScore(name);
	} else if (type=='airtime') {
		changeCAirtime(name);
	}

}
function showAPDetailPagging(type, name){
	if (currentAP==null) {
		currentAP = name;
	} else if (currentAP != name) {
		currentAP = name;
		Get("hiveApSlaDiv").style.display="none";
		Get("hiveApCrcDiv").style.display="none";
		Get("hiveApTxDropDiv").style.display="none";
		Get("hiveApRxDropDiv").style.display="none";
		Get("hiveApTxRetryDiv").style.display="none";
		Get("hiveApAirtimeDiv").style.display="none";
	}

	if (type == 'sla') {
		changeAPSla(name);
	} else if (type=='crc'){
		changeAPCrc(name);
	} else if (type=='txDrop') {
		changeAPTxDrop(name);
	} else if (type=='rxDrop') {
		changeAPRxDrop(name);
	} else if (type=='txRetry') {
		changeAPTxRetry(name);
	} else if (type=='airtime') {
		changeAPAirtime(name);
	}
}

function changeAPSla(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApSlaPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataSlaResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataSlaResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPSlaData = " + o.responseText);
	//if (!initDatatableAPSla){
		YAHOO.example.ApSlaPagination();
	//} else {
	//	basicAPSla.oDT.getRecordSet().replaceRecords(basicAPSlaData);
	//	basicAPSla.oDT.render();
	//	basicAPSla.oCON.render();
	//}
	Get("hiveApSlaDiv").style.display="block";
	Get("lblApSla").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' Throughput Detail Information"
}

function changeAPCrc(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApCrcPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataCrcResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataCrcResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPCrcData = " + o.responseText);
	//if (!initDatatableAPCrc){
		YAHOO.example.ApCrcPagination();
	//} else {
	//	basicAPCrc.oDT.getRecordSet().replaceRecords(basicAPCrcData);
	//	basicAPCrc.oDT.render();
	//	basicAPCrc.oCON.render();
	//}
	Get("hiveApCrcDiv").style.display="block";
	Get("lblApCrc").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' CRC Error Detail Information"
}

function changeAPTxDrop(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApTxDropPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataTxDropResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataTxDropResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPTxDropData = " + o.responseText);
	//if (!initDatatableAPTxDrop){
		YAHOO.example.ApTxDropPagination();
	//} else {
	//	basicAPTxDrop.oDT.getRecordSet().replaceRecords(basicAPTxDropData);
	//	basicAPTxDrop.oDT.render();
	//	basicAPTxDrop.oCON.render();
	//}
	Get("hiveApTxDropDiv").style.display="block";
	Get("lblApTxDrop").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' Tx Drop Detail Information"
}

function changeAPRxDrop(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApRxDropPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataRxDropResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataRxDropResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPRxDropData = " + o.responseText);
	//if (!initDatatableAPRxDrop){
		YAHOO.example.ApRxDropPagination();
	//} else {
	//	basicAPRxDrop.oDT.getRecordSet().replaceRecords(basicAPRxDropData);
	//	basicAPRxDrop.oDT.render();
	//	basicAPRxDrop.oCON.render();
	//}
	Get("hiveApRxDropDiv").style.display="block";
	Get("lblApRxDrop").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' Rx Drop Detail Information"
}

function changeAPTxRetry(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApTxRetryPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataTxRetryResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataTxRetryResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPTxRetryData = " + o.responseText);
	//if (!initDatatableAPTxRetry){
		YAHOO.example.ApTxRetryPagination();
	//} else {
	//	basicAPTxRetry.oDT.getRecordSet().replaceRecords(basicAPTxRetryData);
	//	basicAPTxRetry.oDT.render();
	//}
	Get("hiveApTxRetryDiv").style.display="block";
	Get("lblApTxRetry").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' Tx Retry Detail Information"
}

function changeAPAirtime(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showApAirtimePanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAPDataAirtimeResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createAPDataAirtimeResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicAPAirtimeData = " + o.responseText);
	//if (!initDatatableAPAirtime){
		YAHOO.example.ApAirtimePagination();
	//} else {
	//	basicAPAirtime.oDT.getRecordSet().replaceRecords(basicAPAirtimeData);
	//	basicAPAirtime.oDT.render();
	//}
	Get("hiveApAirtimeDiv").style.display="block";
	Get("lblApAirtime").innerHTML="<s:text name='hiveAp.tag'/>" + " '" + currentAP + "' Airtime Detail Information"
}

// for cleint 
function changeCSla(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showCSlaPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createCDataSlaResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createCDataSlaResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicCSlaData = " + o.responseText);
	//if (!initDatatableCSla){
		YAHOO.example.CSlaPagination();
	//} else {
	//	basicCSla.oDT.getRecordSet().replaceRecords(basicCSlaData);
	//	basicCSla.oDT.renderPaginator();
	//	basicCSla.oDT.render();
	//}
	Get("clientSlaDiv").style.display="block";
	Get("lblCSla").innerHTML="Client '" + currentClient + "' Throughput Detail Information"
}

function changeCScore(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showCScorePanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createCDataScoreResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createCDataScoreResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicCScoreData = " + o.responseText);
	YAHOO.example.CScorePagination();
	Get("clientScoreDiv").style.display="block";
	Get("lblCScore").innerHTML="Client '" + currentClient + "' Health Detail Information"
}

/**
function changeCTxDrop(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showCTxDropPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createCDataTxDropResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createCDataTxDropResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicCTxDropData = " + o.responseText);
	YAHOO.example.CTxDropPagination();
	Get("clientTxDropDiv").style.display="block";
	Get("lblCTxDrop").innerHTML="Client '" + currentClient + "' Tx Drop Detail Information"
}

function changeCRxDrop(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showCRxDropPanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createCDataRxDropResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createCDataRxDropResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicCRxDropData = " + o.responseText);
	//if (!initDatatableCRxDrop){
		YAHOO.example.CRxDropPagination();
	//} else {
	//	basicCRxDrop.oDT.getRecordSet().replaceRecords(basicCRxDropData);
	//	basicCRxDrop.oDT.render();
	//	basicCRxDrop.oCON.render();
	//}
	Get("clientRxDropDiv").style.display="block";
	Get("lblCRxDrop").innerHTML="Client '" + currentClient + "' Rx Drop Detail Information"
}
**/
function changeCAirtime(currentName){
	var url = "<s:url action='reportList' includeParams='none' />" + "?operation=showCAirtimePanel&currentAPName="+ currentName +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createCDataAirtimeResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createCDataAirtimeResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("basicCAirtimeData = " + o.responseText);
	//if (!initDatatableCAirtime){
		YAHOO.example.CAirtimePagination();
	//} else {
	//	basicCAirtime.oDT.getRecordSet().replaceRecords(basicCAirtimeData);
	//	basicCAirtime.oDT.render();
	//	basicCAirtime.oCON.render();
	//}
	Get("clientAirtimeDiv").style.display="block";
	Get("lblCAirtime").innerHTML="Client '" + currentClient + "' Airtime Detail Information"
}

YAHOO.example.CSlaPagination = function() {
	basicCSla=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", sortable:true, resizeable:true},
            {key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", sortable:true, resizeable:true},
            {key:"status", label:"<s:text name='report.reportList.sla.status'/>", sortable:true, resizeable:true},
            {key:"guaBandwidth", label:"<s:text name='report.reportList.sla.configBandwidth'/>",sortable:true, resizeable:true},
            {key:"actBandwidth", label:"<s:text name='report.reportList.sla.actualBandwidth'/>", sortable:true, resizeable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicCSlaData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","clientMac","status","guaBandwidth","actBandwidth","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'clientComSlaPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("clientComSla", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableCSla=true;
}

YAHOO.example.CScorePagination = function() {
	basicCScore=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
            {key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", resizeable:true,sortable:true},
            {key:"ssidName", label:"<s:text name='monitor.client.ssid.name'/>", resizeable:true,sortable:true},
            {key:"score", label:"<s:text name='report.reportList.nonCompliance.score'/>", resizeable:true,sortable:true},
            {key:"radioscore", label:"<s:text name='report.reportList.nonCompliance.radioscore'/>", resizeable:true,sortable:true},
            {key:"ipnetworkscore", label:"<s:text name='report.reportList.nonCompliance.ipnetworkscore'/>", resizeable:true,sortable:true},
            {key:"applicationscore", label:"<s:text name='report.reportList.nonCompliance.applicationscore'/>", resizeable:true,sortable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicCScoreData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","clientMac","ssidName","score","radioscore","ipnetworkscore","applicationscore","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'clientComScorePage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("clientComScore", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableCTxDrop=true;
}

/**
YAHOO.example.CTxDropPagination = function() {
	basicCTxDrop=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.apName'/>", resizeable:true,sortable:true},
            {key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", resizeable:true,sortable:true},
            {key:"ssidName", label:"<s:text name='monitor.client.ssid.name'/>", resizeable:true,sortable:true},
            {key:"txDrop", label:"<s:text name='report.reportList.nonCompliance.txDrop'/>", resizeable:true,sortable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicCTxDropData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","clientMac","ssidName","txDrop","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'clientComTxDropPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("clientComTxDrop", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableCTxDrop=true;
}

YAHOO.example.CRxDropPagination = function() {
	basicCRxDrop=function (){
        var myColumnDefs = [
			{key:"name", label:"<s:text name='report.reportList.apName'/>", resizeable:true,sortable:true},
            {key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", resizeable:true,sortable:true},
            {key:"ssidName", label:"<s:text name='monitor.client.ssid.name'/>", resizeable:true,sortable:true},
            {key:"rxDrop", label:"<s:text name='report.reportList.nonCompliance.rxDrop'/>", resizeable:true,sortable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicCRxDropData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","clientMac","ssidName","rxDrop","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'cleintComRxDropPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("clientComRxDrop", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableCRxDrop=true;
}
**/
YAHOO.example.CAirtimePagination = function() {
	basicCAirtime=function (){
        var myColumnDefs = [
            {key:"name", label:"<s:text name='report.reportList.deviceName'/>", resizeable:true,sortable:true},
			{key:"clientMac", label:"<s:text name='report.reportList.clientAuth.macAddress'/>", resizeable:true,sortable:true},
            {key:"ssidName", label:"<s:text name='monitor.client.ssid.name'/>", resizeable:true,sortable:true},
            {key:"txAirtime", label:"<s:text name='report.summary.cInfo.txAirtime'/>", resizeable:true,sortable:true},
            {key:"rxAirtime", label:"<s:text name='report.summary.cInfo.rxAirtime'/>", resizeable:true,sortable:true},
            {key:"collectPeriod", label:"<s:text name='report.summary.cInfo.timePeriod'/>", resizeable:true,sortable:true},
            {key:"reportTime", label:"<s:text name='report.reportList.title.time'/>", resizeable:true,sortable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicCAirtimeData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
           fields: ["name","clientMac","ssidName","txAirtime","rxAirtime","collectPeriod","reportTime"]
        };

        var oConfigs = {
        		sortedBy : {key:"reportTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    containers : 'clientComAirtimePage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("clientComAirtime", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
     //initDatatableCAirtime=true;
}

var abortResult = function(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function closePanel(closeId){
	Get(closeId).style.display="none";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="reportList" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="reportList">
	<s:hidden name="tabIndex" />
	<s:hidden name="buttonType" />
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
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.name" /><font color="red"><s:text
										name="*" /></font></td>
									<td colspan="3"><s:textfield name="dataSource.name"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
										name="report.reportList.name.range" /></td>
								</tr>
								<tr>
									<td colspan="4">  
									<div style="display:<s:property value="%{showNewOldReportType}"/>" id="showNewOldReportTypeDiv">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding-left: 5px;"><s:radio label="Gender"
												name="dataSource.newOldFlg" id="newOldType"
												onclick="changeNewOldReportType(this.value);"
												list="#{'1':'Old Report Version'}" /></td>
												<td style="padding-left: 5px;"><s:radio label="Gender"
												name="dataSource.newOldFlg" id="newOldType"
												onclick="changeNewOldReportType(this.value);"
												list="#{'2':'New Report Version'}" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="4"> 
									<div style="display:<s:property value="%{showReportType}"/>" id="showReportTypeDiv">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="150px"><s:text
													name="report.reportList.title.preference" /></td>
												<td colspan="3"><s:select name="dataSource.reportType"
													list="%{enumReportType}" listKey="key" listValue="value"
													value="dataSource.reportType" cssStyle="width: 150px;" 
													onchange="changeReportType(this.options[this.selectedIndex].value);"/></td>
											</tr>
										</table>
									</div>
									
									</td>
								</tr>
								<tr style="display:<s:property value="%{showApName}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.deviceName" /></td>
									<td colspan="3"><s:textfield name="dataSource.apName"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" /></td>
								</tr>
								<tr style="display:<s:property value="%{showSSID}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.ssid" /></td>
									<td colspan="3"><s:textfield name="dataSource.ssidName"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" /></td>
								</tr>
								<tr style="display:<s:property value="%{showRole}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.role" /></td>
									<td colspan="3"><s:select name="dataSource.role"
										list="%{enumRole}" listKey="key" listValue="value"
										value="dataSource.role" cssStyle="width: 150px;" /></td>
								</tr>
								<tr>
									<td colspan="4"> 
										<div style="display:<s:property value="%{showComplianceType}"/>" id="slaTypeDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="report.reportList.compliance.type" /></td>
													<td><s:select name="dataSource.complianceType"
														list="%{enumComplianceType}" listKey="key" listValue="value"
														value="dataSource.complianceType" cssStyle="width: 150px;" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr style="display:<s:property value="%{showLocation}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.location" /></td>
									<td colspan="3"><s:select name="locationId"
										list="%{location}" listKey="id" listValue="value"
										value="locationId" cssStyle="width: 150px;" /></td>
								</tr>
								<tr style="display:<s:property value="%{showClientAuth}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.clientAuth.authType" /></td>
									<td><s:select name="dataSource.authType"
										list="%{enumReportAuthType}" listKey="key" listValue="value"
										value="dataSource.authType" cssStyle="width: 150px;" /></td>
								</tr>
								<tr style="display:<s:property value="%{showClientMacAddressOnly}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.clientAuth.macAddress" /></td>
									<td colspan="3"><s:textfield name="dataSource.authMac"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'hex');"
										maxlength="12" /></td>
								</tr>
								<tr style="display:<s:property value="%{showClientMacAddress}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.title.currentClientHostName" /></td>
									<td colspan="3"><s:textfield name="dataSource.authHostName"
										size="24" maxlength="%{nameLength}" /></td>
								</tr>
								<tr style="display:<s:property value="%{showClientMacAddress}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.title.currentClientUserName" /></td>
									<td colspan="3"><s:textfield name="dataSource.authUserName"
										size="24" maxlength="%{nameLength}" /></td>
								</tr>
								<tr style="display:<s:property value="%{showClientMacAddress}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.title.currentClientIpAddress" /></td>
									<td colspan="3"><s:textfield name="dataSource.authIp"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'ip');"
										maxlength="15" /></td>
								</tr>
								<tr style="display:<s:property value="%{showReportPeriod}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.reportPeriod" /></td>
									<td><s:select name="dataSource.reportPeriod"
										list="%{enumReportPeriod}" listKey="key" listValue="value"
										value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
									<td> 
										<div style="display:<s:property value="%{hideTimeAggregation}"/>" id="hideTimeAggregation">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding-left: 15px;" width="110px"><s:text
														name="report.reportList.timeAggregation" /></td>
													<td><s:select name="dataSource.timeAggregation"
														list="%{enumTimeAggregation}" listKey="key" listValue="value"
														headerKey="0" headerValue="1 Hour"
														value="dataSource.timeAggregation" cssStyle="width: 150px;" /></td>
												</tr>
											</table>
										</div>
									</td>	
								</tr>

							</table>
							</td>
						</tr>
						<tr>
							<td style="padding-left: 5px;"><s:radio label="Gender"
								name="dataSource.excuteType" id="excuteType"
								onclick="show_hideSchedule(this.value);"
								list="#{'1':'Run a report immediately'}" /></td>
						</tr>
						<tr>
							<td height="6px"/>
						</tr>
						<tr>
							<td style="padding-left: 5px;"><s:radio label="Gender"
								name="dataSource.excuteType" id="excuteType"
								onclick="show_hideSchedule(this.value);"
								list="#{'2':'Schedule a recurring series of reports'}" /></td>
						</tr>
						<tr>
							<td height="6px"/>
						</tr>
						<tr>
							<td width="100%">
							<div style="display:<s:property value="%{hideSchedule}"/>" id="hideSchedule">
							<table border="0" cellspacing="0" cellpadding="0" width="480px">
								<tr>
									<td style="padding: 5px 15px 5px 15px">
									<fieldset>
									<div>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="4px" />
										</tr>
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="133px" nowrap="nowrap"><s:text
														name="report.reportList.guiStartTime" /></td>
													<td width="60px"><s:textfield name="startTime"
														id="startTime" value="%{startTime}" readonly="true"
														size="10" maxlength="10" /></td>
													<td width="15px">
													<div id="startdatefields"></div>
													</td>
													<td width="80px"><s:select name="startHour"
														id="startHour" value="%{startHour}" list="%{lstHours}"
														listKey="id" listValue="value" /></td>
												</tr>
											</table>
											</td>
										</tr>

										<tr>
											<td><s:checkbox name="dataSource.enabledRecurrence"
												value="%{dataSource.enabledRecurrence}"
												onclick="show_hideRecurrence(this.checked);"/> <s:text
												name="report.reportList.enabledRecurrence" /></td>
										</tr>
										<tr>
											<td>
											<div style="display:<s:property value="%{showRecurrence}"/>" id="hideRecurrence">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">

												<tr>
													<td style="padding-left: 35px;"><s:radio label="Gender"
														name="dataSource.recurrenceType" list="#{'1':'Daily'}"
														onclick="enabledWeekDay(this.value);"/></td>
												</tr>
												<tr>
													<td>
													<table border="0" cellspacing="0" cellpadding="0"
														width="100%">
														<tr>
															<td style="padding-left: 35px;" width="100px"><s:radio
																label="Gender" name="dataSource.recurrenceType"
																list="#{'2':'Weekly'}"
																onclick="enabledWeekDay(this.value);"/></td>
															<td><s:select name="dataSource.weekDay"
																id="selectWeekDay"
																value="dataSource.weekDay" list="%{enumWeekDay}"
																listKey="key" listValue="value"/></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
									</table>
									</div>
									</fieldset>
									</td>
								</tr>
							</table>
							</div>
							</td>
						</tr>
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td style="padding: 0 2px 0 6px" width="10px"><s:checkbox
										name="dataSource.enabledEmail" id="enabledEmail"
										value="%{dataSource.enabledEmail}" onclick="enabledEmailAddress();"
										disabled="%{emailUsabled}" /></td>
									<td width="134px"><s:text
										name="report.reportList.emailAddress" /></td>
									<td><s:textfield name="dataSource.emailAddress"
										id="emailAddress" size="54" maxlength="128" />
										<s:text name="report.reportList.email.emailNoteRange" /></td>
								</tr>
								<tr>
									<td colspan="3" nowrap="nowrap" style="padding-left: 30px" class="noteInfo"><s:text
											name="report.reportList.email.note" /></td>
								</tr>
								<tr>
									<td colspan="3" nowrap="nowrap" style="padding-left: 63px" class="noteInfo"><s:text
											name="report.reportList.email.emailNote" /></td>
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
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						
						<s:if test="%{dataSource.reportType=='hiveApNonCompliance'}">
							<tr>
								<th width="200px"><s:text name="report.reportList.deviceName" /></th>
								<th width="120px"> <s:text name="report.reportList.nonCompliance.slaInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.crcErrorIfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.txDropInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.rxDropInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.txRetryInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.airtimeIfo" /></th>
							</tr>
							<tr>
								<td colspan="7">
									<s:if test="%{hiveAPNonComplianceList.size() == 0}">
										<ah:emptyList />
									</s:if>
									<s:else>
										<div style="height:152px; overflow: auto;">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<s:iterator value="hiveAPNonComplianceList" status="status2">
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>">
														<td class="list" align="center" width="200px">
															<a href='<s:url value="hiveAp.action">
								                			<s:param name="operation" value="%{'view'}"/>
								                			<s:param name="hmListType" value="%{'managedHiveAps'}"/>
								                			<s:param name="filterMac" value="%{name}"/>
								                			<s:param name="dashCondition" value="%{'nonComplianceAp'}"/></s:url>'>
								                			<s:property value="name" /></a>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkSla}"> 
																<a href="#A" onclick="showAPDetailPagging('sla','<s:property value="name" />')"> 
																	<s:property value="slaCount" />
																</a>
															</s:if>
															<s:else>
																<s:property value="slaCount" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkCrc}"> 
																<a href="#A" onclick="showAPDetailPagging('crc','<s:property value="name" />')"> 
																	<s:property value="crcError" />
																</a>
															</s:if>
															<s:else>
																<s:property value="crcError" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkTxData}"> 
																<a href="#A" onclick="showAPDetailPagging('txDrop','<s:property value="name" />')"> 
																	<s:property value="txdata" />
																</a>
															</s:if>
															<s:else>
																<s:property value="txdata" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkRxData}"> 
																<a href="#A" onclick="showAPDetailPagging('rxDrop','<s:property value="name" />')"> 
																	<s:property value="rxdata" />
																</a>
															</s:if>
															<s:else>
																<s:property value="rxdata" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkTxRetry}"> 
																<a href="#A" onclick="showAPDetailPagging('txRetry','<s:property value="name" />')"> 
																	<s:property value="txRetry" />
																</a>
															</s:if>
															<s:else>
																<s:property value="txRetry" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkAirtime}"> 
																<a href="#A" onclick="showAPDetailPagging('airtime','<s:property value="name" />')"> 
																	<s:property value="airtime" />
																</a>
															</s:if>
															<s:else>
																<s:property value="airtime" />
															</s:else>
														</td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</s:else>
								</td>
							</tr>
							<tr>
								<td height="8px"></td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApSlaDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApSla"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComSlaPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApSlaDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComSla"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApCrcDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApCrc"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComCrcPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApCrcDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComCrc"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApTxDropDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApTxDrop"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComTxDropPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApTxDropDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComTxDrop"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApRxDropDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApRxDrop"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComRxDropPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApRxDropDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComRxDrop"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApTxRetryDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApTxRetry"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComTxRetryPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApTxRetryDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComTxRetry"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="7">
									<fieldset id="hiveApAirtimeDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblApAirtime"></span></td>
												<td align="right" style="padding-right: 30px"><div id="hiveApComAirtimePage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('hiveApAirtimeDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="hiveApComAirtime"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</s:if>
						<s:elseif test="%{dataSource.reportType=='clientNonCompliance'}">
							<tr>
								<th width="200px"><s:text name="report.reportList.clientAuth.macAddress" /></th>
								<th width="120px"> <s:text name="report.reportList.nonCompliance.slaInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.scoreInfo" /></th>
								<th width="120px"><s:text name="report.reportList.nonCompliance.airtimeIfo" /></th>
							</tr>
							<tr>
								<td colspan="4">
									<s:if test="%{clientNonComplianceList.size() == 0}">
										<ah:emptyList />
									</s:if>
									<s:else>
										<div style="height:152px; overflow: auto;">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<s:iterator value="clientNonComplianceList" status="status2">
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>">
														<td class="list" width="200px" align="center">
															<a href='<s:url value="clientMonitor.action">
									                			<s:param name="operation" value="%{'search'}"/>
									                			<s:param name="filterClientMac" value="%{name}"/></s:url>'>
									                			<s:property value="name" /></a>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkSla}"> 
																<a href="#C" onclick="showCDetailPagging('sla','<s:property value="name" />')"> 
																	<s:property value="slaCount" />
																</a>
															</s:if>
															<s:else>
																<s:property value="slaCount" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkScore}"> 
																<a href="#C" onclick="showCDetailPagging('score','<s:property value="name" />')"> 
																	<s:property value="score" />
																</a>
															</s:if>
															<s:else>
																<s:property value="score" />
															</s:else>
														</td>
														<td class="list" width="120px" align="center">
															<s:if test="%{showLinkAirtime}"> 
																<a href="#C" onclick="showCDetailPagging('airtime','<s:property value="name" />')"> 
																	<s:property value="airtime" />
																</a>
															</s:if>
															<s:else>
																<s:property value="airtime" />
															</s:else>
														</td>
													</tr>
												</s:iterator>
											</table>
										</div>
									</s:else>
								</td>
							</tr>
							<tr>
								<td height="8px"></td>
							</tr>
							<tr>
								<td colspan="4">
									<fieldset id="clientSlaDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblCSla"></span></td>
												<td align="right" style="padding-right: 30px"><div id="clientComSlaPage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('clientSlaDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="clientComSla"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="4">
									<fieldset id="clientScoreDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblCScore"></span></td>
												<td align="right" style="padding-right: 30px"><div id="clientComScorePage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('clientScoreDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="clientComScore"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="4">
									<fieldset id="clientAirtimeDiv" style="display: none;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td style="padding: 2px 2px 2px 5px; font-weight: bold"><span id="lblCAirtime"></span></td>
												<td align="right" style="padding-right: 30px"><div id="clientComAirtimePage"></div></td>
												<td class="closePanelTD"><span onclick="closePanel('clientAirtimeDiv');">x</span></td>
											</tr>
											<tr>
												<td colspan="3"><div id="clientComAirtime"></div></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='mostClientsAPs'}">
							<tr>
								<td colspan="10" align="center"><B><s:text name="report.reportList.mostClientsAPs.title" /></B></td>
							</tr>
							<tr>
								<th><s:text name="report.reportList.title.time" /></th>
								<th><s:text name="report.reportList.no1apName" /></th>
								<th><s:text name="report.reportList.title.totalClients" /></th>
								<th><s:text name="report.reportList.no2apName" /></th>
								<th><s:text name="report.reportList.title.totalClients" /></th>
								<th><s:text name="report.reportList.no3apName" /></th>
								<th><s:text name="report.reportList.title.totalClients" /></th>
								<th><s:text name="report.reportList.no4apName" /></th>
								<th><s:text name="report.reportList.title.totalClients" /></th>
								<th><s:text name="report.reportList.no5apName" /></th>
								<th><s:text name="report.reportList.title.totalClients" /></th>
							</tr>
							<s:if test="%{fiveMaxClientCount.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="fiveMaxClientCount" status="status" id="oneReportTime">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<s:iterator value="%{#oneReportTime}" status="status2">
										<s:if test="%{#status2.index==0}">
											<td class="list"><s:property value="%{reportTimeString}" /></td>
										</s:if>
										<td class="list" align="center"><s:property value="apName" /></td>
										<td class="list" align="center"><s:property value="clientCount" /></td>
									</s:iterator>
								</tr>
							</s:iterator>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='summaryUserUsage'}">
								<ah:emptyList />
						</s:elseif>	
						<s:elseif test="%{dataSource.reportType=='detailUserUsage'}">
								<ah:emptyList />
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='clientVendor'}">
							<tr>
								<th align="left"><s:text name="report.reportList.title.vendor" /></th>
								<th align="left"><s:text name="report.reportList.title.vendorCount" /></th>
							</tr>
							<s:if test="%{lstClientVendorCount.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="lstClientVendorCount" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list" ><s:property value="%{value}" /></td>
									<td class="list" ><s:property value="%{id}" /></td>
								</tr>
							</s:iterator>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='hiveApConnection'}">
							<tr>
								<th align="left"><s:text name="report.reportList.deviceName" /></th>
								<th align="left"><s:text name="report.reportList.deviceMac" /></th>
								<th align="left"><s:text name="monitor.alarms.alarmTime" /></th>
								<th align="left"><s:text name="report.reportList.connetc.type" /></th>
								<th align="left"><s:text name="report.reportList.connetc.reason" /></th>
							</tr>
							<s:if test="%{lstHiveApConnection.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="lstHiveApConnection" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list" ><s:property value="apName" /></td>
									<td class="list" ><s:property value="apId" /></td>
									<td class="list" ><s:property value="trapTimeString"/></td>
									<td class="list" ><s:property value="trapTypeString" /></td>
									<td class="list" ><s:property value="trapMessage" /></td>
								</tr>
							</s:iterator>
						</s:elseif>
						
						<s:elseif test="%{dataSource.reportType=='inventory'}">
							<tr>
								<th align="left"><s:text name="hiveAp.hostName" /></th>
								<th align="left"><s:text name="hiveAp.ipAddress" /></th>
								<th align="left"><s:text name="hiveAp.macaddress" /></th>
								<th align="left"><s:text name="hiveAp.serialNumber" /></th>
								<th align="left"><s:text name="hiveAp.apType" /></th>
								<th align="left"><s:text name="hiveAp.topology" /></th>
								<th align="left"><s:text name="monitor.hiveAp.connectionTime" /></th>
								<th align="left"><s:text name="monitor.hiveAp.model" /></th>
								<th align="left"><s:text name="monitor.hiveAp.sw" /></th>
							</tr>
							<s:if test="%{lstInventory.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="lstInventory" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list"><s:property value="hostName" /></td>
									<td class="list"><s:property value="ipAddress" />&nbsp;</td>
									<td class="list"><s:property value="macAddress" />&nbsp;</td>
									<td class="list"><s:property value="serialNumber" />&nbsp;</td>
									<td class="list"><s:property value="hiveApTypeString" />&nbsp;</td>
									<td class="list"><s:property value="topologyName" />&nbsp;</td>
									<td class="list"><s:property value="upTimeString" />&nbsp;</td>
									<td class="list"><s:property value="productName" />&nbsp;</td>
									<td class="list"><s:property value="softVer" />&nbsp;</td>
								</tr>
							</s:iterator>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='compliance'}">
							  <tr>
							  	<td colspan="12">
							  		<table border="0" cellspacing="0" cellpadding="0" width="100%">
							  			<tr>
							  				<td height="5px"></td>
							  			</tr>
										<tr>
											<td class="sepLine" colspan="2"><img
												src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" /></td>
										</tr>
								  		<tr>
							  				<td height="5px"></td>
							  			</tr>
							  		</table>
							  	</td>
							  </tr>
							  <tr>
							    <th align="center" rowspan="2"><s:text name="report.reportList.compliance.hiveApName" /></th>
							    <th align="center" colspan="3"><s:text name="report.reportList.compliance.clientAccess" /></th>
							    <th align="center" colspan="4"><s:text name="report.reportList.compliance.hiveAccess" /></th>
							    <th align="center" colspan="4"><s:text name="report.reportList.compliance.passwordAccess" /></th>
							  </tr>
							  <tr>
							    <th align="center"><s:text name="report.reportList.compliance.ssidName" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.accessSecurity" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.securityRating" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.ssh" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.telnet" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.ping" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.snmp" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.pskPass" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.hivePass" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.hiveApPass" /></th>
							    <th align="center"><s:text name="report.reportList.compliance.capwapPass" /></th>
							    <th align="center"></th>
							  </tr>
							<s:if test="%{lstCompliance.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="lstCompliance" status="status" id="complianceResult">
								<tr>
									<td class="list" align="center" rowspan="<s:property value="%{#complianceResult.ssidList.size + 1}" />"><s:property value="%{hiveApName}" /></td>
								</tr>
								<s:iterator value="%{#complianceResult.ssidList}" status="ssidStatus">
									<tr>
										<td class="list" align="center"><s:property value="%{ssidName}" /></td>
										<td class="list" align="center"><s:property value="%{ssidMethodString}" /></td>
										<td class="list" align="center"><s:property value="%{ratingString}" /></td>
										<td class="list" align="center"><s:property value="%{blnSshString}" /></td>
										<td class="list" align="center"><s:property value="%{blnTelnetString}" /></td>
										<td class="list" align="center"><s:property value="%{blnPingString}" /></td>
										<td class="list" align="center"><s:property value="%{blnSnmpString}" /></td>
										<td class="list" align="center"><s:property value="%{ssidPassString}" /></td>
										<s:if test="%{#ssidStatus.index==0}">
											<td class="list" align="center" rowspan="<s:property value="%{#complianceResult.ssidList.size + 1}" />"><s:property value="%{#complianceResult.hivePassString}" /></td>
											<td class="list" align="center" rowspan="<s:property value="%{#complianceResult.ssidList.size + 1}" />"><s:property value="%{#complianceResult.hiveApPassString}" /></td>
											<td class="list" align="center" rowspan="<s:property value="%{#complianceResult.ssidList.size + 1}" />"><s:property value="%{#complianceResult.capwapPassString}" /></td>
										</s:if>
									</tr>
								</s:iterator>
								<tr>
									<td colspan="12" style="border-bottom: 1px solid #808080; color: #003366;">
									</td>
								</tr>
							</s:iterator>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='clientAuth'}">
							<tr>
								<th align="left"><ah:sort name="remoteId" key="report.reportList.clientAuth.macAddress" /></th>
								<th align="left"><ah:sort name="remoteId" key="report.reportList.title.vendorName" /></th>
								<th align="left"><ah:sort name="clientHostName" key="report.reportList.title.currentClientHostName" /></th>
								<th align="left"><ah:sort name="clientUserName" key="report.reportList.title.currentClientUserName" /></th>
								<th align="left"><ah:sort name="clientIp" key="report.reportList.title.currentClientIpAddress" /></th>
								<th align="left"><ah:sort name="trapTime" key="report.reportList.clientAuth.eventTime" /></th>
								<th align="left"><s:text name="report.reportList.clientAuth.authType" /></th>
								<th align="left"><s:text name="report.reportList.clientAuth.description" /></th>
							</tr>
							<s:if test="%{page.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="page" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list"><s:property value="remoteId" />&nbsp;</td>
									<td class="list"><s:property value="getVendorNameString" />&nbsp;</td>
									<td class="list"><s:property value="clientHostName" />&nbsp;</td>
									<td class="list"><s:property value="clientUserName" />&nbsp;</td>
									<td class="list"><s:property value="clientIp" />&nbsp;</td>
									<td class="list"><s:property value="trapTimeString" />&nbsp;</td>
									<td class="list"><s:property value="codeString" />&nbsp;</td>
									<td class="list"><s:property value="trapDesc" />&nbsp;</td>
								</tr>
							</s:iterator>
						</s:elseif>
						<s:else>
						<tr>
							<td width ="20%" valign="top">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="85px" nowrap="nowrap"><s:text
										name="report.reportList.title.title" /></td>
									<td><s:property value="%{dataSource.name}" /></td>
								</tr>
								<tr>
									<td width="85px" nowrap="nowrap"><s:text
										name="report.reportList.title.type" /></td>
									<td><s:property value="%{dataSource.reportTypeShowInGUI}" />
									</td>
								</tr>
								<tr>
									<td width="85px" nowrap="nowrap"><s:text
										name="report.reportList.title.time" /></td>
									<td><s:property value="%{runReportTime}" /></td>
								</tr>
								<s:if test="%{dataSource.reportType!='securityRogueAPs' && dataSource.reportType!='securityRogueClients' && dataSource.reportType!='maxClient' && dataSource.reportType!='clientSession' && dataSource.reportType!='clientCount' && dataSource.reportType!='clientAirTime' && dataSource.reportType!='clientSla'}">
								<tr>
									<td width="85px" nowrap="nowrap"><s:text
										name="report.reportList.title.currentDevice" /></td>
									<td><s:property value="%{reportAPName}" /></td>
								</tr>
								</s:if>
								<s:if test="%{dataSource.reportType=='ssidTrafficMetrics' || dataSource.reportType=='ssidTroubleShooting' ||dataSource.reportType=='ssidAirTime' }">
									<tr>
										<td width="85px"><s:text
											name="report.reportList.title.currentSSID" /></td>
										<td><s:property value="%{reportSsidName}" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableDevice" /></legend>
											<s:select name="reportAPName" size="18"
												list="%{lstHiveAPName}"
												cssStyle="width: 190px;"
												ondblclick="submitAction('run');"
												onclick="showSsidName(this);"
												onchange="showSsidName(this);"/>
										</fieldset>
										</td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableSSID" /></legend>
											<s:select name="reportSsidName" size="6"
												list="%{lstReportSsidName}"
												id="reportSsidName"
												cssStyle="width: 190px;"
											ondblclick="submitAction('run');"/>
										</fieldset>
										</td>
									</tr>
								</s:if>
								<s:elseif test="%{dataSource.reportType=='meshNeighbors'}">
									<tr>
										<td width="85px"><s:text
											name="report.reportList.title.currentNeighborAP" /></td>
										<td><s:property value="%{reportNeighborAP}" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableDevice" /></legend>
											<s:select name="reportAPName" size="18"
												list="%{lstHiveAPName}"
												cssStyle="width: 190px;"
												ondblclick="submitAction('run');"
												onclick="showSsidName(this);"
												onchange="showSsidName(this);"/>
										</fieldset>
										</td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableNeighbor" /></legend>
											<s:select name="reportNeighborAP" size="6"
												list="%{lstReportNeighborAP}"
												id="reportNeighborAP"
												cssStyle="width: 190px;"
											ondblclick="submitAction('run');"/>
										</fieldset>
										</td>
									</tr>
								</s:elseif>

								<s:elseif test="%{dataSource.reportType=='clientSession' || dataSource.reportType=='clientAirTime' || dataSource.reportType=='clientSla'}">
									<tr>
										<td width="115px"><s:text
											name="report.reportList.title.currentClientMac" /></td>
										<td><s:property value="%{reportClientMac}" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableClientMac" /></legend>
											<s:select name="reportClientMacWhole" size="27"
												list="%{lstReportClientMac}"
												cssStyle="width: 300px;"
												ondblclick="submitAction('run');"
												onclick="hm.util.showtitle(this);"
												onchange="hm.util.showtitle(this);"/>
										</fieldset>
										</td>
									</tr>
								</s:elseif>
								<%--s:elseif test="%{dataSource.reportType=='clientAirTime' || dataSource.reportType=='clientSla'}">
									<tr>
										<td width="115px"><s:text
											name="report.reportList.title.currentClientMac" /></td>
										<td><s:property value="%{reportClientMac}" /></td>
									</tr>
									<tr>
										<td width="115px"><s:text
											name="report.reportList.title.currentClientIpAddress" /></td>
										<td><s:property value="%{reportClientIpAddress}" /></td>
									</tr>
									<tr>
										<td width="115px"><s:text
											name="report.reportList.title.currentClientHostName" /></td>
										<td><s:property value="%{reportClientHostName}" /></td>
									</tr>
									<tr>
										<td width="115px"><s:text
											name="report.reportList.title.currentClientUserName" /></td>
										<td><s:property value="%{reportClientUserName}" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableClientMac" /></legend>
											<s:select name="reportClientMacWhole" size="24"
												list="%{lstReportClientMac}"
												cssStyle="width: 300px;"
												ondblclick="submitAction('run');"
												onclick="hm.util.showtitle(this);"
												onchange="hm.util.showtitle(this);"/>
										</fieldset>
										</td>
									</tr>
								</s:elseif--%>
								<s:elseif test="%{dataSource.reportType!='securityRogueAPs' && dataSource.reportType!='securityRogueClients' && dataSource.reportType!='maxClient' && dataSource.reportType!='clientCount'}">
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
											<fieldset><legend><s:text name="report.reportList.title.availableDevice" /></legend>
												<s:select name="reportAPName" size="27"
													list="%{lstHiveAPName}"
													cssStyle="width: 190px;"
													ondblclick="submitAction('run');"
													onclick="hm.util.showtitle(this);"
													onchange="hm.util.showtitle(this);"
													/>
											</fieldset>
										</td>
									</tr>
								</s:elseif>
							</table>
							</td>
							<td width="80%">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<s:if test="%{(dataSource.reportType=='channelPowerNoise' || dataSource.reportType=='radioAirTime' || dataSource.reportType=='radioTrafficMetrics' || dataSource.reportType=='radioTroubleShooting' || dataSource.reportType=='radioInterference' || dataSource.reportType=='hiveApSla' || dataSource.reportType=='uniqueClientCount') && dataSource.newOldFlg==1}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='radioInterference'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioInterference');">Interference</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioInterference');">Interference</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='radioAirTime'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioAirTime');">Airtime Usage</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioAirTime');">Airtime Usage</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='hiveApSla'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','hiveApSla');">Device SLA</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','hiveApSla');">Device SLA</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='radioTrafficMetrics'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioTrafficMetrics');">Traffic Metrics</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioTrafficMetrics');">Traffic Metrics</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='radioTroubleShooting'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioTroubleShooting');">Troubleshooting</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','radioTroubleShooting');">Troubleshooting</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='uniqueClientCount'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','uniqueClientCount');">Unique Client Count</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','uniqueClientCount');">Unique Client Count</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='channelPowerNoise'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','channelPowerNoise');">Channel/Power/Noise</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','channelPowerNoise');">Channel/Power/Noise</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
												</tr>
											</table>
										</td>
									</tr>
									</s:if>	
									<s:elseif test="%{dataSource.reportType=='ssidTrafficMetrics' || dataSource.reportType=='ssidTroubleShooting' || dataSource.reportType=='ssidAirTime'}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='ssidAirTime'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidAirTime');">Airtime Usage</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidAirTime');">Airtime Usage</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='ssidTrafficMetrics'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidTrafficMetrics');">Traffic Metrics</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidTrafficMetrics');">Traffic Metrics</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='ssidTroubleShooting'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidTroubleShooting');">Troubleshooting</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','ssidTroubleShooting');">Troubleshooting</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
												</tr>
											</table>
										</td>
									</tr>
									</s:elseif>
									<s:elseif test="%{(dataSource.reportType=='clientSession' || dataSource.reportType=='clientAirTime' || dataSource.reportType=='clientSla') && dataSource.newOldFlg==1}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='clientSession'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientSession');">Client Sessions</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientSession');">Client Sessions</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='clientAirTime'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientAirTime');">Airtime Usage</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientAirTime');">Airtime Usage</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='clientSla'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientSla');">Client SLA</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','clientSla');">Client SLA</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
												</tr>
											</table>
										</td>
									</tr>
									</s:elseif>
									<tr>
										<td><s:if test="%{showReportTab}">
										<tiles:insertDefinition name="flash" />
										</s:if></td>
									</tr>
									<s:if test="%{dataSource.reportType=='hiveApSla' || dataSource.reportType=='clientSla'}">
									<tr>
										<td style="padding-left: 5px">
											<div style="overflow: scroll; height: 300px; position: relative; border-style: solid; border-width: 0;">
												<table border="0" cellspacing="0" cellpadding="0" width="100%" class="view">
													<tr>
														<th><s:text name="report.reportList.deviceName"></s:text></th>
														<th><s:text name="report.reportList.deviceMac"></s:text></th>
														<th><s:text name="report.reportList.title.currentClientMac"></s:text></th>
														<th><s:text name="report.reportList.sla.status"></s:text></th>
														<th><s:text name="report.reportList.title.time"></s:text></th>
														<th><s:text name="report.reportList.sla.configBandwidth"></s:text></th>
														<th><s:text name="report.reportList.sla.actualBandwidth"></s:text></th>
													</tr>
													<s:if test="%{lstHiveApSla!=null}">
													<s:iterator value="lstHiveApSla" status="status">
														<tiles:insertDefinition name="rowClass" />
														<tr class="<s:property value="%{#rowClass}"/>">
															<td class="list">
																<s:if test="%{dataSource.reportType=='clientSla'}">
																	<a href='<s:url action="reportList" includeParams='none'>
																		<s:param name="listType" value="%{'radioReports'}"/>
																		<s:param name="buttonType" value="%{'hiveApSlaFromClientSla'}"/>
																		<s:param name="operation" value="%{'runLink'}"/>
																		<s:param name="reportPeriodFromClient" value="%{dataSource.reportPeriod}"/>
																		<s:param name="reportAPName" value="%{apName}"/></s:url>'>
																		<s:property value="apName" />
																	</a>
																</s:if>
																<s:else>
																	<s:property value="apName" />&nbsp;
																</s:else>
															</td>
															<td class="list"><s:property value="apMac" />&nbsp;</td>
															<td class="list"><s:property value="clientMac" />&nbsp;</td>
															<td class="list"><s:property value="bandWidthSentinelStatusString" />&nbsp;</td>
															<td class="list"><s:property value="timeString" />&nbsp;</td>
															<td class="list"><s:property value="guaranteedBandWidth" />&nbsp;</td>
															<td class="list"><s:property value="actualBandWidth" />&nbsp;</td>
														</tr>
													</s:iterator>
													</s:if>
												</table>
											</div>
										</td>
									</tr>
									</s:if>
								</table>
							</td>
						</tr>
						</s:else>
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
