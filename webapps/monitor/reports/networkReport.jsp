<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<tiles:insertDefinition name="tabView" />

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/paginator/assets/skins/sam/paginator.css"  includeParams="none"/>" />
<link type="text/css" rel="stylesheet" href="<s:url value="/css/widget/ahReportChart.css" includeParams="none"/>"></link>

<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/assets/skins/sam/skin.css" includeParams="none" />?v=<s:property value="verParam" />" />

<script type="text/javascript"
		src="<s:property value="yuiBase" />/treeview/treeview-min.js"></script>
	
<style type="text/css">
/* YUI Tree overrides */
body .ygtvlabel,body .ygtvlabel:link,body .ygtvlabel:visited,body .ygtvlabel:hover
	{
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 12px;
	color: #003366;
	white-space: nowrap;
}

body .ygtvlabel:hover {
	text-decoration: underline;
}

body .ygtvlabelSel,body .ygtvlabelSel:link,body .ygtvlabelSel:visited,body .ygtvlabelSel:hover
	{
	font-weight: bold;
	background-color: #00FFFF;
}

body .ygtvfocus {
	background-color: #FFFFFF;
}

body .ygtvfocus .ygtvlabel,body .ygtvfocus .ygtvlabel:link,body .ygtvfocus .ygtvlabel:visited,body .ygtvfocus .ygtvlabel:hover
	{
	background-color: #00FFFF;
}

a.ygtvspacer {
	text-decoration: none;
}

a.ygtvspacer:active,a.ygtvspacer:focus {
	-moz-outline: none;
	outline: none;
	ie-dummy: expression(this.hideFocus =                                   true);
}

.yui-skin-sam .yui-navset .yui-content {
	background: #FFFFFF;
}
/* YUI Layout overrides */
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-noft {
	border-bottom: 0;
}

.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-nohd {
	border-top: 0;
}

.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
	background-color: #fff;
	border: 0;
}

.rpTitle {
	padding-left:2px;
	color: #777777;
	font-size: 20px;
	font-weight: bold;
	font-family: Arial, Helvetica, Verdana, sans-serif;
}
.rpSubTitle {
	padding-left:2px;
	color: #777777;
	font-size: 14px;
	font-weight: bold;
	font-family: Arial, Helvetica, Verdana, sans-serif;
}
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

#calendarpicker1 button {
    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
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
		createWaitingPanel();
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
        function onButtonClick1() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu1.setBody("&#32;");
            oCalendarMenu1.body.id = "calendarcontainer1";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu1.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu1.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar2", oCalendarMenu1.body.id);
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {
                    oCalendarMenu1.show();
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
                    var endDate_doc = document.getElementById("endTime");
                    endDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu1.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick1);

        };

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu1 = new YAHOO.widget.Overlay("calendarmenu");

        // Create a Button instance of type "menu"
        var startTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "startdatefields" });

        // Create a Button instance of type "menu"
        var endTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu1,
                                            container: "enddatefields" });

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton.on("click", onButtonClick);
        endTimeButton.on("click", onButtonClick1);

	});

</script>
<script>
var formName = 'networkUsageReport';
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
var selectedNode = null;
var selectedNodeSec = null;
var mapHierarchy = [ <ah:mapTree /> ];
var mapTree = null;
var labelClicked = null;

function createTreeForReport(selectLocationId, treeViewDivId) {
	mapTree = new YAHOO.widget.TreeView(treeViewDivId);
	for (var i = 0; i < mapHierarchy.length; i++) {
		if(mapHierarchy[i] != undefined){
			if (mapHierarchy[i].items.length==0 &&  mapHierarchy[i].label.indexOf("Uninitialized") > -1){
				mapHierarchy[i].label = mapHierarchy[i].label.replace("Uninitialized","All");
				mapHierarchy[i].title = mapHierarchy[i].title.replace("Uninitialized","All");
				mapHierarchy[i].id=-1;
			}
		}
	}
	
	populateTreeForReport(mapHierarchy, mapTree.getRoot(), 0);
	mapTree.subscribe("labelClick", function(node) {
		if(node.data.uiz){
			return;
		}
		labelClicked = node.label;
		highlightNodeForReport(node, treeViewDivId);
		if (treeViewDivId=="treeDiv") {
			changeLocation(node.data.id);
			document.forms[formName].locationId.value = node.data.id;
		} else {
			changeLocationForAp(node.data.id);
			document.forms[formName].cuLocationId.value = node.data.id;
		}
	});
	mapTree.subscribe("clickEvent", function(node) {
	});
	
	if (treeViewDivId=="treeDivSec") {
		mapTree.subscribe("dblClickEvent", function(nodeItem) {
			var node = nodeItem.node;
			if(node.data.uiz){
				return;
			}
			changeLocationForAp(node.data.id);
			document.forms[formName].cuLocationId.value = node.data.id;
			submitAction('run');
		});
	}
	
	mapTree.subscribe("expand", function(node) {
		if (labelClicked == node.label) {
			labelClicked = null;
			return false;
		}
		//expandTreeNode(node.data.id, true);
	});
	mapTree.subscribe("collapse", function(node) {
		if (labelClicked == node.label) {
			labelClicked = null;
			return false;
		}
		//expandTreeNode(node.data.id, false);
	});
	mapTree.render();
	if (selectLocationId!=null && selectLocationId!= undefined) {
		var cuNode = findTreeNodeByDataId(mapTree.getRoot(), selectLocationId);
		if (cuNode!=null && cuNode!= undefined) {
			//labelClicked = cuNode.label;
			highlightNodeForReport(cuNode,treeViewDivId);
		}
	}	
}

function highlightNodeForReport(node,treeViewDivId) {
	// expands parent nodes
	var parentNode = node.parent;
	while(null != parentNode.parent){
		parentNode.expand();
		parentNode = parentNode.parent;
	}
	if (treeViewDivId=="treeDiv") {
		if (selectedNode != null) {
			YAHOO.util.Dom.removeClass(selectedNode.labelElId, 'ygtvlabelSel');
		}
		selectedNode = node;
		YAHOO.util.Dom.addClass(selectedNode.labelElId, 'ygtvlabelSel');
	} else {
		if (selectedNodeSec != null) {
			YAHOO.util.Dom.removeClass(selectedNodeSec.labelElId, 'ygtvlabelSel');
		}
		selectedNodeSec = node;
		YAHOO.util.Dom.addClass(selectedNodeSec.labelElId, 'ygtvlabelSel');
	}
}

/*
 * Find tree node
 */
function findTreeNodeByDataId(treeNode, nodeDataId) {
	for (var i = 0; i < treeNode.children.length; i++) {
		if (treeNode.children[i].data.id == nodeDataId) {
			return treeNode.children[i];
		} else {
			var node = findTreeNodeByDataId(treeNode.children[i], nodeDataId);
			if (node != null) {
				return node;
			}
		}
	}
	return null;
}

function populateTreeForReport(items, parentNode, depth) {
	var superUserLogin = false;
	var mapCount = 0;
	for (var i = 0; i < items.length; i++) {
		if(items[i] != undefined){
			mapCount++;
		}
	}
	if (mapCount>1) {
		superUserLogin = true;
	}
	for (var i = 0; i < items.length; i++) {
		if(items[i] != undefined){
			if (superUserLogin) {
				if (items[i].label.indexOf("home - ")!=0) {
					continue;
				}
			}
			var node = new YAHOO.widget.TextNode(items[i], parentNode); // depth
																		// < 2
			node.title = null; 
			populateTree(items[i].items, node, depth+1);
			
		}
	}
}

function populateTree(items, parentNode, depth) {
	for (var i = 0; i < items.length; i++) {
		if(items[i] != undefined){
			var node = new YAHOO.widget.TextNode(items[i], parentNode); // depth
																		// < 2
			node.title = null;  // Interferes with menus
			// href not works, trigger in event instead
			// node.href = "javascript:nodeSelected(" + node.index + ")";
			populateTree(items[i].items, node, depth+1);
		}
	}
}

function onLoadPage() {
	<s:if test="%{dataSource.forSample}">
		Get("treeDiv").innerHTML="&nbsp;All";
	</s:if>
	<s:else>
		createTreeForReport(<s:property value="%{locationId}"/>, "treeDiv");
	</s:else>
//	if (!document.getElementById("enabledEmail").checked) {
//		document.getElementById("emailAddress").value="";
//		document.getElementById("emailAddress").readOnly=true;
//	}
/**
	if (document.getElementById(formName +"_dataSource_recurrenceType1").checked) {
		document.getElementById("selectWeekDay").disabled=true;
	} else {
		document.getElementById("selectWeekDay").disabled=false;
	}
**/
	var showReportTab = <s:property value="%{showReportTab}"/>;
	if (document.getElementById(formName + "_dataSource_name").disabled == false && !showReportTab) {
		document.getElementById(formName + "_dataSource_name").focus();
	}

	allTabs = new YAHOO.widget.TabView("reportTabs", {activeIndex:0});
	generalTab = allTabs.getTab(0);
	reportTab = allTabs.getTab(1);

	if (showReportTab) {
		allTabs.set('activeIndex',1);
		document.getElementById("export").disabled = false;
		document.getElementById("email").disabled = false;
		<s:if test="%{dataSource.forSample}">
			Get("treeDivSec").innerHTML="&nbsp;&nbsp;All";
		</s:if>
		<s:else>
			createTreeForReport(<s:property value="%{cuLocationId}"/>, "treeDivSec");
		</s:else>
	} else {
		allTabs.removeTab(reportTab);
		document.getElementById("export").disabled = true;
		document.getElementById("email").disabled = true;
	}
	createWaitingPanel();
	var node = Get("lstDeviceSelect");
	if (node) {
		for (var i = 0; i < node.childNodes.length; i++) {
			
			if (node.childNodes[i].nodeName == "OPTION") {
				if (node.childNodes[i].value != "-1#") {
					if (node.childNodes[i].value == "-2#" || node.childNodes[i].value == "-3#") {
						node.childNodes[i].text="|_" + node.childNodes[i].text;
					} else {
						node.childNodes[i].text="|_ _" + node.childNodes[i].text;
					}
				}
			}
		}
	}
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
		} else if (operation == 'sendMail') {
			sendMail();
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
	var message = hm.util.validateNameWithBlanks(inputElement.value, '<s:text name="report.reportList.name" />');
	if (message != null) {
		allTabs.set('activeIndex',0);
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
	
	if (document.getElementById("excuteType2").checked) {
		if (Get(formName + "_dataSource_customDay").checked) {
			if (!Get("cDay1").checked &&
				!Get("cDay2").checked &&
				!Get("cDay3").checked &&
				!Get("cDay4").checked &&
				!Get("cDay5").checked &&
				!Get("cDay6").checked &&
				!Get("cDay7").checked) {
				allTabs.set('activeIndex',0);
				hm.util.reportFieldError(document.getElementById("errorCustomTime"), '<s:text name="error.requiredField"><s:param><s:text name="report.networkusage.customDay"/></s:param></s:text>');
				return false;
			}
		}
		
		if (Get(formName + "_dataSource_customTime").checked) {
			if (parseInt(Get("customTimeStart").value)>=parseInt(Get("customTimeEnd").value)) {
				hm.util.reportFieldError(Get("customTimeStart"), '<s:text name="error.shourldLargerThan"><s:param>To time</s:param><s:param>From time</s:param></s:text>');
				allTabs.set('activeIndex',0);
				return false;
			}
		}
	

		if (document.getElementById("emailAddress").value.trim()=="") {
		  hm.util.reportFieldError(document.getElementById("emailAddress"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
		  allTabs.set('activeIndex',0);
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
				allTabs.set('activeIndex',0);
				return false;
			}
		}
	}

	
	if (document.getElementById("excuteType1").checked && Get("reportPeriod").value==9) {
		if (document.getElementById("startTime").value=="") {
		  hm.util.reportFieldError(document.getElementById("startTime"), '<s:text name="error.requiredField"><s:param>From time</s:param></s:text>');
		  allTabs.set('activeIndex',0);
		  return false;
		}
		if (document.getElementById("endTime").value=="") {
		  hm.util.reportFieldError(document.getElementById("startTime"), '<s:text name="error.requiredField"><s:param>To time</s:param></s:text>');
		  allTabs.set('activeIndex',0);
		  return false;
		}
		
		if (Get("startTime").value >Get("endTime").value
			 || (Get("startTime").value ==Get("endTime").value
			 &&  parseInt(Get("startHour").value) >= parseInt(Get("endHour").value))) {
		     hm.util.reportFieldError(Get("startTime"), '<s:text name="error.shourldLargerThan"><s:param>To time</s:param><s:param>From time</s:param></s:text>');
		     allTabs.set('activeIndex',0);
		     return false;
		}
	}
	
	if (operation=='sendMail') {
		if (document.getElementById("onceMail").value.trim()=="") {
			  hm.util.reportFieldError(document.getElementById("emailErrorDiv"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
			  return false;
			}

			var emails = document.getElementById("onceMail").value.split(";");
			for (var i=0;i<emails.length;i++) {
				if (i==emails.length-1 && emails[i].trim()=="") {
					break;
				}
				if (!hm.util.validateEmail(emails[i].trim())) {
					hm.util.reportFieldError(document.getElementById("emailErrorDiv"), '<s:text name="error.formatInvalid"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
					document.getElementById("onceMail").focus();
					return false;
				}
			}
	}

	return true;
}

//
//function enabledEmailAddress() {
//	if (!document.getElementById("enabledEmail").checked) {
//		document.getElementById("emailAddress").value="";
//		document.getElementById("emailAddress").readOnly=true;
//	} else {
//		document.getElementById("emailAddress").readOnly=false;
//	}
//}


function sendMail() {
	var mailValue = document.getElementById("onceMail").value;
	var url = "<s:url action='networkUsageReport' includeParams='none' />" + "?operation=sendMail&mailAddress=" + mailValue + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: sendMailResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
		hideOverlay();
	}
}

var sendMailResult = function(o){
	hm.util.hide('processing');
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.success)
	{
		showHangMessage("success mail report file.");
	}
	else
	{
		showErrorMessage("mail report file failed!");
	}
}

function createTechData()
{
	var url = "<s:url action='networkUsageReport' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
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
	if(result.success)
	{
		showHangMessage("Download exported file.");
		hm.util.show('downloadSection');
	}
	else
	{
		showErrorMessage("Create export file failed!");
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

function validateDateTime() {
   	 var beginDate=document.getElementById("startTime");
   	 var endDate=document.getElementById("endTime");
   	 if (beginDate.value!='' && endDate.value!='') {
		 if(hm.util.compareDate(endDate.value,beginDate.value)=="-1"){
		     hm.util.reportFieldError(beginDate, 'From date is lower than the start date.');
		     beginDate.focus();
		     allTabs.set('activeIndex',0);
		     return false;
		 }
	 } else {
	  	hm.util.reportFieldError(beginDate, 'From date and To date should be set.');
		beginDate.focus();
		allTabs.set('activeIndex',0);
		return false;
	 }

    return true;
}

function show_hideSchedule(value) {
	if (value=='1') {
		Get("immediatelyDiv").style.display='';
		Get("schedulerDiv").style.display='none';
	} else {
		Get("immediatelyDiv").style.display='none';
		Get("schedulerDiv").style.display='';
	}
}

function changeRunPeriod(value) {
	if (value==9) {
		Get("customTimeDiv").style.display="";
	} else {
		Get("customTimeDiv").style.display="none";
	}
}

function customDayClick(checked) {
	if (checked) {
		Get("cDay1").disabled=false;
		Get("cDay2").disabled=false;
		Get("cDay3").disabled=false;
		Get("cDay4").disabled=false;
		Get("cDay5").disabled=false;
		Get("cDay6").disabled=false;
		Get("cDay7").disabled=false;
	} else {
		Get("cDay1").disabled=true;
		Get("cDay2").disabled=true;
		Get("cDay3").disabled=true;
		Get("cDay4").disabled=true;
		Get("cDay5").disabled=true;
		Get("cDay6").disabled=true;
		Get("cDay7").disabled=true;
	}
}

function customTimeClick(checked) {
	if (checked) {
		Get("customTimeStart").disabled=false;
		Get("customTimeEnd").disabled=false;
	} else {
		Get("customTimeStart").disabled=true;
		Get("customTimeEnd").disabled=true;
	}

}
function changeFrequency(value) {
	if (value==1) {
		Get("schedulerDailyDiv").style.display="";
	} else {
		Get("schedulerDailyDiv").style.display="none";
	}
}

function changeLocation(value){
	var url = '<s:url action="networkUsageReport" includeParams='none'/>?operation=changeLocation' + "&locationId="+value+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
}

function changeLocationForAp(value){
	var url = '<s:url action="networkUsageReport" includeParams='none'/>?operation=changeLocationForAp' + "&ssid=" + Get("ssidListSelect").value +"&cuLocationId="+value+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callbackAp, null);
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

var detailsSuccessAp = function(o) {
	eval("var details = " + o.responseText);
	var td = document.getElementById(details.id);
	var value = details.v;
	td.length=0;
	td.length=value.length;
	for(var i = 0; i < value.length; i ++) {
		if (value[i].v != "-1#") {
			if (value[i].v == "-2#" || value[i].v == "-3#") {
				td.options[i].text="|_" + value[i].k;
			} else {
				td.options[i].text="|_ _" + value[i].k;
			}
		} else {
			td.options[i].text=value[i].k;
		}
		td.options[i].value=value[i].v;
	}
	td.value=value[0].v;
};
var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

var callbackAp = {
	success : detailsSuccessAp,
	failure : detailsFailed
};

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="networkUsageReport" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="networkUsageReport">
	<s:hidden name="tabIndex" />
	<s:hidden name="buttonType" />
	<s:hidden name="locationId" />
	<s:hidden name="cuLocationId" />
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
							value="Save" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="Save" class="button"
							onClick="submitAction('update');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Export PDF" id="export"
						class="button" onClick="submitAction('createDownloadData');"
						<s:property value="updateDisabled" />></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
					<td><input type="button" name="ignore" value="Email" id="email"
						class="button" onClick="openFilterOverlay();"
						<s:property value="updateDisabled" />></td>
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
							name="report.networkusage.tab.option" /></em></a></li>
						<li><a href="#tab2"><em><s:text
							name="report.networkusage.tab.view" /></em></a></li>
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
									<td class="labelT1" width="120px"><s:text
										name="report.reportList.name" /><font color="red"><s:text
										name="*" /></font></td>
									<td ><s:textfield name="dataSource.name"
										size="24" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
										disabled="%{disabledName}" />&nbsp;<s:text
										name="report.reportList.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="120px"><s:text
										name="report.reportList.clientAuth.description" /></td>
									<td ><s:textfield name="dataSource.description"
										size="48" maxlength="64" />&nbsp;<s:text
										name="config.description.range" /></td>
								</tr>
								<tr><td height="4px"/></tr>
								<tr>
									<td class="labelT1" width="120px" valign="top"><s:text
										name="report.networkusage.devicegroup" /><font color="red"><s:text
										name="*" /></font></td>
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr><td width="200px"><fieldset style="padding: 0 1px 8px"><table border="0" cellspacing="0" cellpadding="0">
												<tr><td height="2px"/></tr>
												<tr>
													<td class="rpSubTitle"> <s:text name="report.networkusage.topogroup" /></td>
												</tr>
												<tr><td><div id="treeDiv" style="background-color: #fff; padding-left: 5px;"></div></td></tr>
											</table></fieldset>
											</td></tr>
										</table>
									</td>
								</tr>
								<tr><td height="4px"/></tr>
								<tr>
									<td class="labelT1"> <s:text name="report.networkusage.ssidgroup" /><font color="red"><s:text
										name="*" /></font></td>
									<td><s:select name="ssid" id="ssidListSelect"
										list="%{lstSsid}" listKey="key" listValue="value"
										value="ssid" cssStyle="width: 200px;" /></td>
								</tr>
								<tr><td height="6px"></td></tr>
								<tr>
									<td class="labelT1" valign="top"> <s:text name="report.reportList.reportPeriod" /><font color="red">*</font></td>
									<td>
										<fieldset>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><s:radio label="Gender" disabled="%{disabledName}"
														name="dataSource.excuteType" id="excuteType"
														onclick="show_hideSchedule(this.value);"
														list="#{'2':'Schedule a recurring series of reports'}" /></td>
												</tr>
												<tr id="schedulerDiv" style="display:<s:property value="%{schedulerDivDisplay}"/>">
													<td style="padding-left: 10px;"  >
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td colspan="3" class="noteInfo" style="padding-left: 10px;">
																<s:text name="report.networkusage.note.cannotchange" /></td>
															</tr>
															<tr>
																<td width="140px" style="padding-left: 10px;" valign="middle"> <s:text name="report.networkusage.frequency" /></td>
																<td width="160px" valign="middle"><s:select name="dataSource.frequency"
																	list="%{lstFrequency}" listKey="key" listValue="value"
																	value="dataSource.frequency" cssStyle="width: 150px;" 
																	onchange="changeFrequency(this.options[this.selectedIndex].value);"/></td>
																<td id="schedulerDailyDiv" style="display:<s:property value="%{schedulerDailyDivDisplay}"/>">
																	<table  border="0" cellspacing="0" cellpadding="0">
																		<tr><td colspan="2"><table><tr><td><span id="errorCustomTime"/></span></td></tr></table></td></tr>
																		<tr>
																			<td width="150px" style="padding: 2px 2px 2px 4px">
																				<s:checkbox name="dataSource.customDay" onclick="customDayClick(this.checked);"></s:checkbox><s:text name="report.networkusage.customDay" /></td>
																			<td style="padding: 2px 2px 2px 4px" width="250px">
																				<s:checkbox name="dataSource.customTime" onclick="customTimeClick(this.checked);"></s:checkbox><s:text name="report.networkusage.customTime" /></td>
																		</tr>
																		<tr>
																			<td style="padding: 2px 2px 2px 4px">
																				<table  border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td><s:checkbox name="c7" id="cDay7" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c1" id="cDay1" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c2" id="cDay2" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c3" id="cDay3" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c4" id="cDay4" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c5" id="cDay5" disabled="%{customDayEnabled}"/></td>
																						<td><s:checkbox name="c6" id="cDay6" disabled="%{customDayEnabled}"/></td>
																					</tr>
																					<tr>
																						<td>&nbsp;&nbsp;S</td>
																						<td>&nbsp;&nbsp;M</td>
																						<td>&nbsp;&nbsp;T</td>
																						<td>&nbsp;&nbsp;W</td>
																						<td>&nbsp;&nbsp;T</td>
																						<td>&nbsp;&nbsp;F</td>
																						<td>&nbsp;&nbsp;S</td>
																					</tr>
																				</table>
																			</td>
																			
																			<td style="padding: 2px 2px 2px 4px" nowrap="nowrap"> 
																			<s:select name="dataSource.customTimeStart"
																				value="%{dataSource.customTimeStart}" list="%{lstHours}"
																				id="customTimeStart"
																				listKey="id" listValue="value" disabled="%{customTimeEnabled}"/>--
																			<s:select name="dataSource.customTimeEnd"
																				value="%{dataSource.customTimeEnd}" list="%{lstHours}"
																				id="customTimeEnd"
																				listKey="id" listValue="value" disabled="%{customTimeEnabled}"/></td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr><td height="4px"/>
															</tr>
															<tr>
																<td class="labelT1"><s:text
																	name="report.reportList.emailAddress" /><font color="red"><s:text
																	name="*" /></font></td>
																<td nowrap="nowrap" colspan="2"><s:textfield name="dataSource.emailAddress"
																	id="emailAddress" size="54" maxlength="128" />
																	<s:text name="report.reportList.email.emailNoteRange" /></td>
															</tr>
															<tr>
																<td nowrap="nowrap"  class="noteInfo" colspan="3" style="padding-left: 10px"><s:text
																		name="report.reportList.email.note" /></td>
															</tr>
															<tr>
																<td colspan="3" nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
																		name="report.reportList.email.emailNote" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td><s:radio label="Gender" disabled="%{disabledName}"
														name="dataSource.excuteType" id="excuteType"
														onclick="show_hideSchedule(this.value);"
														list="#{'1':'Immediately run a report'}" /></td>
												</tr>
												<tr id="immediatelyDiv" style="display:<s:property value="%{immediatelyDivDisplay}"/>">
													<td style="padding-left: 20px;" >
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="120px" nowrap="nowrap"> <s:text name="report.networkusage.timerange" /></td>
																<td><s:select name="dataSource.reportPeriod"
																	id="reportPeriod"
																	list="%{lstReportPeriod}" listKey="key" listValue="value"
																	onchange="changeRunPeriod(this.options[this.selectedIndex].value);"
																	value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
															</tr>
															<tr>
																<td colspan="2" id="customTimeDiv" style="display: <s:property value="%{customTimeDivDisplay}"/>"> 
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td class="labelT1" style="padding-left: 30px" colspan="2"> <s:text name="report.networkusage.customdatetime" /></td>
																		</tr>
																		<tr>
																			<td class="labelT1" style="padding-left: 30px"> <s:text name="report.networkusage.from" /></td>
																			<td> <s:text name="report.networkusage.to" /></td>
																		</tr>
																		<tr>
																			<td style="padding-left: 34px">
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td width="60px"><s:textfield name="startTime"
																							id="startTime" value="%{startTime}" readonly="true"
																							size="10" maxlength="10" /></td>
																						<td width="15px">
																						<div id="startdatefields"></div>
																						</td>
																						<td width="80px"><s:select name="startHour"
																							id="startHour" value="%{startHour}" list="%{lstHours}"
																							listKey="id" listValue="value" /></td>
																						<td>--</td>
																					</tr>
																				</table>
																			</td>
																			<td>
																				<table border="0" cellspacing="0" cellpadding="0">
																					<tr>
																						<td width="60px"><s:textfield name="endTime"
																							id="endTime" value="%{endTime}" readonly="true"
																							size="10" maxlength="10" /></td>
																						<td width="15px">
																						<div id="enddatefields"></div>
																						</td>
																						<td width="80px"><s:select name="endHour"
																							id="endHour" value="%{endHour}" list="%{lstHours}"
																							listKey="id" listValue="value" /></td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td colspan="2"><input type="button" name="ignore" value="View Now"
																	class="button" onClick="submitAction('run');"
																	<s:property value="updateDisabled" />></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
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

					<!-- end general  -->
					<!-- begin report  -->
					<div id="tab2">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td width ="20%" valign="top">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="200px" colspan="2" class="rpTitle"><s:property value="%{dataSource.name}" /></td>
								</tr>
								<tr>
									<td width="200px" colspan="2" class="rpSubTitle"><s:text name="report.networkusage.title"/></td>
								</tr>
								<tr>
									<td>&nbsp;<s:text name="report.networkusage.from"/>: </td><td><s:property value="%{dataSource.runStartTimeString}"/></td>
								</tr>
								<tr>
									<td>&nbsp;<s:text name="report.networkusage.to"/>:</td><td><s:property value="%{dataSource.runEndTimeString}"/></td>
								</tr>
								<tr>
									<td height="15px"/>
								</tr>
								<tr>
									<td colspan="2">
										<fieldset style="padding: 0 1px 8px">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="rpSubTitle"> <s:text name="report.networkusage.topogroup" /></td>
											</tr>
											<tr><td><div id="treeDivSec" style="background-color: #fff;"></div></td></tr>
										</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td height="15px"/>
								</tr>
								<tr>
									<td colspan="2"><table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="rpSubTitle"><s:text name="report.networkusage.deviceList"/></td>
											</tr>
											<tr>
												<td><s:select name="cuDeivce" size="15" id="lstDeviceSelect"
													list="%{lstDevice}" listKey="key" listValue="value"
													value="cuDeivce" cssStyle="width: 210px;" /></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
							</td>
							<td width="90%" valign="top">
								<table border="1" bordercolor="#CECECE" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{buttonType==1}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','1');"><s:text name="report.networkusage.title.bandwidth"/></a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','1');"><s:text name="report.networkusage.title.bandwidth"/></a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{buttonType==2}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','2');"><s:text name="report.networkusage.title.client"/></a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','2');"><s:text name="report.networkusage.title.client"/></a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{buttonType==3}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','3');"><s:text name="report.networkusage.title.ssid"/></a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','3');"><s:text name="report.networkusage.title.ssid"/></a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{buttonType==4}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','4');"><s:text name="report.networkusage.title.sla"/></a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','4');"><s:text name="report.networkusage.title.sla"/></a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{buttonType==5}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','5');"><s:text name="report.networkusage.title.error"/></a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','5');"><s:text name="report.networkusage.title.error"/></a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
												</tr>
											</table>
										</td>
									</tr>
									
									<tr>
										<td style="padding-left: 10px">
										<s:if test="%{dataSource.forSample}">
										<div style="margin-top: 5px;">
											<table>
												<tr>
													<td class="note noteInfo">Note:&nbsp;<a style="color:#0033AA;" href="###" onClick="submitAction('cancel<s:property value="lstForward"/>');"
													><span><s:text name="info.report.tip.sample.data"/></span></a></td>
												</tr>
											</table>
										</div>
										</s:if>
										<s:if test="%{showReportTab}">
										<div id='anchorContainer' style='display: inline-block; margin-top: 10px; width: 100%;'>
											<s:if test="%{buttonType==1}">
												<div class='anchorTextContainer'><a id='chart_device_bandwidth_anchor_text' 
													name='chart_device_bandwidth_anchor_text' style='display:none;'><s:text 
														name="report.title.text.bandwidth.usage.device"/></a></div>
												<div class='anchorTextContainer'><a id='bandwidth_usage_by_ssid_anchor_text' 
													name='bandwidth_usage_by_ssid_anchor_text' style='display:none;'><s:text 
														name="report.title.text.bandwidth.usage.ssid"/></a></div>
												<div class='anchorTextContainer'><a id='out_bandwidth_over_time_anchor_text' 
													name='out_bandwidth_over_time_anchor_text' style='display:none;'><s:text 
														name="report.title.text.bandwidth.out.over.time"/></a></div>
												<div class='anchorTextContainer'><a id='in_bandwidth_over_time_anchor_text' 
													name='in_bandwidth_over_time_anchor_text' style='display:none;'><s:text 
														name="report.title.text.bandwidth.in.over.time"/></a></div>
												<div class='anchorTextContainer'><a id='top_n_clients_bandwidth_anchor_text' 
													name='top_n_clients_bandwidth_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.data.usage"/></a></div>
											</s:if>
											<s:elseif test="%{buttonType==2}">
												<div class='anchorTextContainer'><a id='clients_number_by_ap_anchor_text' 
													name='clients_number_by_ap_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.by.device"/></a></div>
												<div class='anchorTextContainer'><a id='clients_number_over_time_anchor_text' 
													name='clients_number_over_time_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.over.time"/></a></div>
												<div class='anchorTextContainer'><a id='client_distribution_by_ssid_anchor_text' 
													name='client_distribution_by_ssid_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.max.by.ssid"/></a></div>
												<div class='anchorTextContainer'><a id='client_distribution_by_ssid_ghz24_anchor_text' 
													name='client_distribution_by_ssid_ghz24_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.max.by.ssid.24"/></a></div>
												<div class='anchorTextContainer'><a id='client_distribution_by_ssid_ghz50_anchor_text' 
													name='client_distribution_by_ssid_ghz50_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.max.by.ssid.50"/></a></div>
												<div class='anchorTextContainer'><a id='client_type_distribution_anchor_text' 
													name='client_type_distribution_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.type.distribution"/></a></div>
											</s:elseif>
											<s:elseif test="%{buttonType==3}">
												<div class='anchorTextContainer'><a id='ssid_client_count_anchor_text' 
													name='ssid_client_count_anchor_text' style='display:none;'><s:text 
														name="report.title.text.clients.per.ssid"/></a></div>
												<div class='anchorTextContainer'><a id='ssid_client_bandwidth_anchor_text' 
													name='ssid_client_bandwidth_anchor_text' style='display:none;'><s:text 
														name="report.title.text.bandwidth.per.ssid"/></a></div>
											</s:elseif>
											<s:elseif test="%{buttonType==4}">
												<div class='anchorTextContainer'><a id='sla_device_anchor_text' 
													name='sla_device_anchor_text' style='display:none;'><s:text 
														name="report.title.text.sla.device"><s:param>All</s:param></s:text></a></div>
												<div class='anchorTextContainer'><a id='sla_client_anchor_text' 
													name='sla_client_anchor_text' style='display:none;'><s:text 
														name="report.title.text.sla.client"><s:param>All</s:param></s:text></a></div>
											</s:elseif>
											<s:elseif test="%{buttonType==5}">
												<div class='anchorTextContainer'><a id='device_error_anchor_text' 
													name='device_error_anchor_text' style='display:none;'><s:text 
														name="report.title.text.errors.device"/></a></div>
											</s:elseif>
										</div>
										<div id='chart_device_bandwidth' class='rpContainer' style='display:none;'></div>
										<div id='bandwidth_usage_by_ssid' class='rpContainer' style='display:none;'></div>
										<div id='out_bandwidth_over_time' class='rpContainer' style='display:none;'></div>
										<div id='in_bandwidth_over_time' class='rpContainer' style='display:none;'></div>
										<div id='ssid_client_count' class='rpContainer' style='display:none;'></div>
										<div id='ssid_client_bandwidth' class='rpContainer' style='display:none;'></div>
										<div id='clients_number_by_ap' class='rpContainer' style='display:none;'></div>
										<div id='clients_number_over_time' class='rpContainer' style='display:none;'></div>
										<div id='client_distribution_by_ssid' class='rpContainer' style='display:none;'></div>
										<div id='client_distribution_by_ssid_ghz24' class='rpContainer' style='display:none;'></div>
										<div id='client_distribution_by_ssid_ghz50' class='rpContainer' style='display:none;'></div>
										<div id='client_type_distribution' class='rpContainer' style='display:none;'></div>
										<s:if test="%{calTopNClients}">
											<div id='top_n_clients_bandwidth' class='rpContainer' style='display:none;'></div>
										</s:if>
										<div id='device_error' class='rpContainer' style='display:none;'></div>
										<div id='sla_device' class='rpContainer' style='display:none;'></div>
										<div id='sla_client' class='rpContainer' style='display:none;'></div>
										</s:if></td>
									</tr>
								</table>
							</td>
						</tr>
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

<script>
var emailFormName = 'emailFormPanel';
YAHOO.util.Event.onDOMReady(init);
var filterOverlay = null;
function init() {
// create filter overlay
	var div = document.getElementById('emailPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"530px",
		visible:false,
		fixedcenter:true,
		draggable:false,
		modal:false,
		constraintoviewport:true,
		zIndex:999
		});
	filterOverlay.render(document.body);
	div.style.display = "";
}
function openFilterOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}
</script>
<div id="emailPanel" style="display: none;">
	<div class="hd">
		Email Report
	</div>
	<div class="bd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="labelT1">
						<s:text name="report.networkusage.onceMail.text" />
					</td>
				</tr>
				<tr><td><table><tr><td><div id="emailErrorDiv"/></td></table></td></tr>
				<tr>
					<td style="padding-left: 10px">
						<s:textfield name="onceMail" id="onceMail" maxlength="128"
							size="50" />&nbsp;<s:text name="report.reportList.email.emailNoteRange" />
					</td>
				</tr>
				<tr><td height="8px"/></tr>
				<tr>
					<td nowrap="nowrap"  class="noteInfo" style="padding-left: 10px"><s:text
							name="report.reportList.email.note" /></td>
				</tr>
				<tr>
					<td nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
							name="report.reportList.email.emailNote" /></td>
				</tr>
				<tr>
					<td height="8px"></td>
				</tr>
				<tr>
					<td  align="right">
						<input type="button" name="ignore" value="Send""
							class="button" onClick="submitAction('sendMail');">
					</td>
				</tr>
			</table>
	</div>
</div>


<script type="text/javascript">
<s:if test="%{showReportTab}">
	head.js("<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />", 
			"<s:url value="/js/jquery.overlay.min.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/innerhtml.js" includeParams="none" />?v=<s:property value="verParam" />", 
			"<s:url value="/js/widget/chart/highcharts.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/exporting.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/ahReportChart.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/chartControls.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/theme/defaultTheme.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/render/defaultRender.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/dataRender/defaultData.js" includeParams="none" />?v=<s:property value="verParam" />",
			function() {
				doRenderProperCharts();
			}
	);
	
	function doRenderProperCharts() {
		<s:if test="%{buttonType==1}">
			head.js("<s:url value="/js/widget/chart/arcGroupSending.js" includeParams="none" />?v=<s:property value="verParam" />",	
				"<s:url value="/monitor/reports/networkReportjs/deviceBandwidthUsage.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/outBandwidthOverTime.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/inBandwidthOverTime.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/bandwidthUsageBySsid.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientsTopN.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/doArcGroupSend.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					doInitAnchors();
				});
		</s:if>
		<s:if test="%{buttonType==2}">
			head.js("<s:url value="/js/widget/chart/arcGroupSending.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientsNumberByAP.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientsNumberOverTime.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientDistributionBySsid.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientDistributionBySsidGHz24.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientDistributionBySsidGHz50.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/clientTypeDistribution.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/doArcGroupSend.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					doInitAnchors();
				});
		</s:if>
		<s:if test="%{buttonType==3}">
			head.js("<s:url value="/monitor/reports/networkReportjs/usageSsidClient.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/usageSsidClientBandwidth.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					doInitAnchors();
				});
		</s:if>
		<s:if test="%{buttonType==4}">
			head.js("<s:url value="/monitor/reports/networkReportjs/slaDevice.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/monitor/reports/networkReportjs/slaClient.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					doInitAnchors();
				});
		</s:if>
		<s:if test="%{buttonType==5}">
			head.js("<s:url value="/monitor/reports/networkReportjs/deviceError.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					doInitAnchors();
				});
		</s:if>
	}
	
	function doInitAnchors() {
		$("div#anchorContainer>div>a[href]:gt(0)").before("<span>&nbsp;|&nbsp;</span>");
	}
</s:if>

</script>