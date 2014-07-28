<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<tiles:insertDefinition name="tabView" />

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>

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
var formName = 'reportLoginUser';
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
	if (document.getElementById(formName + "_dataSource_name").disabled == false) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
	if (!document.getElementById("enabledEmail").checked) {
		document.getElementById("emailAddress").value="";
		document.getElementById("emailAddress").readOnly=true;
	}

	if (document.getElementById(formName+"_dataSource_recurrenceType1").checked) {
		document.getElementById("selectWeekDay").disabled=true;
	} else {
		document.getElementById("selectWeekDay").disabled=false;
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

function submitSortAction(operation,sortField) {
	document.forms[formName].sortIndex.value = sortField;
	submitAction(operation);
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

function createTechData()
{
	var url = "<s:url action='reportLoginUser' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="reportLoginUser" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
<div id="content"><s:form action="reportLoginUser">
	<s:hidden name="tabIndex" />
	<s:hidden name="sortIndex" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
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
					<td width="100%"><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
					<td align="right">
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
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.name" /><font color="red"><s:text
										name="*" /></font></td>
									<td><s:textfield name="dataSource.name"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
										name="report.reportList.name.range" /></td>
								</tr>
								<tr style="display:<s:property value="%{showDetailDomain}"/>">
									<td class="labelT1" width="150px"><s:text
										name="report.plannerInfo.detailDomain" /></td>
									<td><s:select name="dataSource.detailDomainName"
										list="%{detailDomain}"
										value="dataSource.detailDomainName" cssStyle="width: 150px;" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="150px"><s:text
										name="report.reportList.reportPeriod" /></td>
									<td><s:select name="dataSource.reportPeriod"
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
													<div id="startdatefields" />
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
					<table border="0" cellspacing="0" cellpadding="0" width="100%" class="view">
						
						<s:if test="%{dataSource.reportType=='summaryUserUsage'}">
							<tr>
								<th align="left"><a href="javascript:submitSortAction('changeSort',1)"><s:text name="admin.user.userName" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',2)"><s:text name="admin.user.userFullName" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',3)"><s:text name="admin.user.emailAddress" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',4)"><s:text name="report.plannerInfo.apCount" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',5)"><s:text name="report.plannerInfo.loginCount" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',6)"><s:text name="report.plannerInfo.currentLoginCount" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',7)"><s:text name="report.plannerInfo.totalLoginTime" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',8)"><s:text name="report.plannerInfo.lastLoginTime" /></a></th>
								<th align="left"><s:text name="config.domain" /></th>
							</tr>
							<s:if test="%{reportResult!=null && reportResult.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="reportResult" status="status">
								<s:if test="%{#status.index >= cuStartItem && #status.index < cuEndItem}">
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" ><s:property value="%{userName}" /></td>
										<td class="list" ><s:property value="%{userFullName}" />&nbsp;</td>
										<td class="list" ><s:property value="%{emailAddress}" />&nbsp;</td>
										<td class="list" ><s:property value="%{apCount}" />&nbsp;</td>
										<td class="list" ><s:property value="%{loginCount}" />&nbsp;</td>
										<td class="list" ><s:property value="%{currentLoginCount}" />&nbsp;</td>
										<td class="list" ><s:property value="%{totalLoginTimeString}" />&nbsp;</td>
										<td class="list" ><s:property value="%{lastLoginTimeString}" />&nbsp;</td>
										<td class="list" ><s:property value="%{owner.domainName}" /></td>
									</tr>
								</s:if>
							</s:iterator>
						</s:if>	
						<s:elseif test="%{dataSource.reportType=='detailUserUsage'}">
							<tr>
								<th align="left"><a href="javascript:submitSortAction('changeSort',1)"><s:text name="admin.user.userName" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',2)"><s:text name="admin.user.userFullName" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',3)"><s:text name="admin.user.emailAddress" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',4)"><s:text name="report.plannerInfo.apCount" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',7)"><s:text name="report.plannerInfo.sessionTime" /></a></th>
								<th align="left"><a href="javascript:submitSortAction('changeSort',8)"><s:text name="report.plannerInfo.loginTime" /></a></th>
								<th align="left"><s:text name="config.domain" /></th>
							</tr>
							<s:if test="%{reportResult!=null && reportResult.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="reportResult" status="status">
								<s:if test="%{#status.index >= cuStartItem && #status.index < cuEndItem}">
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" ><s:property value="%{userName}" /></td>
										<td class="list" ><s:property value="%{userFullName}" />&nbsp;</td>
										<td class="list" ><s:property value="%{emailAddress}" />&nbsp;</td>
										<td class="list" ><s:property value="%{apCount}" />&nbsp;</td>
										<td class="list" ><s:property value="%{totalLoginTimeString}" />&nbsp;</td>
										<td class="list" ><s:property value="%{lastLoginTimeString}" />&nbsp;</td>
										<td class="list" ><s:property value="%{owner.domainName}" /></td>
									</tr>
								</s:if>
							</s:iterator>
						</s:elseif>
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
