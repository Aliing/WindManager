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
var formName = 'vpnReportList';
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

function showTunnelName(obj) {
	var name = obj.options[obj.selectedIndex].text;
	hm.util.showtitle(obj);
	var url = '<s:url action="vpnReportList"><s:param name="operation" value="viewTunnel"/></s:url>' + "&selectHiveAPName="+encodeURIComponent(name)+"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function createTechData()
{
	var url = "<s:url action='vpnReportList' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
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

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vpnReportList" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="vpnReportList">
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
									<div style="display:<s:property value="%{showReportType}"/>" id="showReportTypeDiv">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="labelT1" width="150px"><s:text
													name="report.reportList.title.preference" /></td>
												<td colspan="3"><s:select name="dataSource.reportType"
													list="%{enumReportType}" listKey="key" listValue="value"
													value="dataSource.reportType" cssStyle="width: 150px;"/></td>
											</tr>
										</table>
									</div>
									
									</td>
								</tr>
								<tr>
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.deviceName" /></td>
									<td colspan="3"><s:textfield name="dataSource.apName"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" /></td>
								</tr>
								
								<tr>
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.reportPeriod" /></td>
									<td colspan="3"><s:select name="dataSource.reportPeriod"
										list="%{enumReportPeriod}" listKey="key" listValue="value"
										value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
								</tr>

							</table>
							</td>
						</tr>
						<tr>
							<td style="padding-left: 5px;"><s:radio label="Gender"
								name="dataSource.excuteType" id="excuteType"
								onclick="show_hideSchedule(this.value);"
								list="#{'1':'Immediately run a report'}" /></td>
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

					<!-- end general  -->
					<!-- begin report  -->
					<div id="tab2">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
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
								<s:if test="%{dataSource.reportType=='vpnThroughput' || dataSource.reportType=='vpnLatency' || dataSource.reportType=='vpnAvailability' || dataSource.reportType=='wanThroughput' || dataSource.reportType=='wanAvailability'}">
									<tr>
										<td width="85px" nowrap="nowrap"><s:text
											name="report.reportList.title.currentBR" /></td>
										<td><s:property value="%{reportAPName}" /></td>
									</tr>
									<s:if test="%{dataSource.reportType=='vpnThroughput' || dataSource.reportType=='vpnLatency' || dataSource.reportType=='vpnAvailability'}">
										<tr>
											<td width="85px" nowrap="nowrap"><s:text
												name="report.reportList.title.currentTunnel" /></td>
											<td><s:property value="%{reportTunnelName}" /></td>
										</tr>
									
										<tr>
											<td colspan="2" style="padding: 2px 0 2px 0">
											<fieldset><legend><s:text name="report.reportList.title.availableBR" /></legend>
												<s:select name="reportAPName" size="18"
													list="%{lstHiveAPName}"
													cssStyle="width: 190px;"
													ondblclick="submitAction('run');"
													onclick="showTunnelName(this);"
													onchange="showTunnelName(this);"/>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td colspan="2" style="padding: 2px 0 2px 0">
											<fieldset><legend><s:text name="report.reportList.title.availableTunnel" /></legend>
												<s:select name="reportTunnelName" size="6"
													list="%{lstReportTunnelName}"
													id="reportTunnelName"
													cssStyle="width: 190px;"
												ondblclick="submitAction('run');"/>
											</fieldset>
											</td>
										</tr>
									</s:if>
									<s:else>
										<tr>
											<td colspan="2" style="padding: 2px 0 2px 0">
											<fieldset><legend><s:text name="report.reportList.title.availableBR" /></legend>
												<s:select name="reportAPName" size="18"
													list="%{lstHiveAPName}"
													cssStyle="width: 190px;"
													ondblclick="submitAction('run');"/>
											</fieldset>
											</td>
										</tr>
									
									</s:else>
								
								</s:if>
								
								
								<s:if test="%{dataSource.reportType=='gwVpnAvailability' || dataSource.reportType=='gwWanThroughput' ||dataSource.reportType=='gwWanAvailability' }">
									<tr>
										<td width="85px"><s:text
											name="report.reportList.title.currentVG" /></td>
										<td><s:property value="%{reportAPName}" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding: 2px 0 2px 0">
										<fieldset><legend><s:text name="report.reportList.title.availableGW" /></legend>
											<s:select name="reportAPName" size="18"
												list="%{lstHiveAPName}"
												cssStyle="width: 190px;"
												ondblclick="submitAction('run');"/>
										</fieldset>
										</td>
									</tr>
								</s:if>
								
							</table>
							</td>
							<td width="80%" valign="top">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<s:if test="%{dataSource.reportType=='vpnThroughput' || dataSource.reportType=='vpnLatency' || dataSource.reportType=='vpnAvailability'}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='vpnAvailability'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnAvailability');">VPN Availability</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnAvailability');">VPN Availability</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='vpnThroughput'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnThroughput');">VPN Throughput</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnThroughput');">VPN Throughput</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='vpnLatency'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnLatency');">VPN Latency</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','vpnLatency');">VPN Latency</a>
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
									<s:elseif test="%{dataSource.reportType=='wanThroughput' || dataSource.reportType=='wanAvailability'}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='wanAvailability'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','wanAvailability');">WAN Availability</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','wanAvailability');">WAN Availability</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='wanThroughput'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','wanThroughput');">WAN Throughput</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','wanThroughput');">WAN Throughput</a>
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
									<s:elseif test="%{dataSource.reportType=='gwVpnAvailability'}">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='gwVpnAvailability'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwVpnAvailability');">VPN Availability</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwVpnAvailability');">VPN Availability</a>
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
									<s:elseif test="%{dataSource.reportType=='gwWanThroughput' ||dataSource.reportType=='gwWanAvailability' }">
									<tr>
										<td class="top_menu_report_bg_sel">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='gwWanAvailability'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwWanAvailability');">WAN Availability</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwWanAvailability');">WAN Availability</a>
														</td>
													</s:else>
													<td width="2"><img
														src="<s:url value="/images/hm/nav-brown-divider.gif"/>"
														width="2" alt="" class="dblk"></td>
													<s:if test="%{dataSource.reportType=='gwWanThroughput'}">
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_report_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwWanThroughput');">WAN Throughput</a>
														</td>
													</s:if>
													<s:else>
														<td class="top_menu_report_item" nowrap="nowrap">
															<a class="top_menu_sel" href="javascript:void(0);" onClick="submitActionChange('run','gwWanThroughput');">WAN Throughput</a>
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
