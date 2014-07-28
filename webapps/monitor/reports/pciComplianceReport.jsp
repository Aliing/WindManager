<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>

<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding-left: 10px;
}

#calendarpicker button, #calendarpicker1 button, #calendarpicker2 button {
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
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar1", oCalendarMenu1.body.id);
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
        
        function onButtonClick2() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu2.setBody("&#32;");
            oCalendarMenu2.body.id = "calendarcontainer2";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu2.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu2.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar2", oCalendarMenu2.body.id);
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {
                    oCalendarMenu2.show();
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
                    var endDate_doc = document.getElementById("startTimeForSchedule");
                    endDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu2.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick2);

        };

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu1 = new YAHOO.widget.Overlay("calendarmenu");
        
     // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu2 = new YAHOO.widget.Overlay("calendarmenu");

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
        
     // Create a Button instance of type "menu"
        var startTimeForScheduleButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "startdatefieldsForSchedule" });

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton.on("click", onButtonClick);
        endTimeButton.on("click", onButtonClick1);
        startTimeForScheduleButton.on("click", onButtonClick2);

	});

</script>

<script>
var formName = 'pciCompliance';

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'export') {
			//showProcessing();
			createTechData();
			return;
		} 
		//alert(operation + "---result.success");
	    //showProcessing();
	    beforeSubmitAction(document.forms[formName]);
	    document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function createTechData()
{
   	var beginDate=document.getElementById("startTime").value;
   	var endDate=document.getElementById("endTime").value;
	var mapId=document.getElementById("locationId").value;
	var reportName = document.getElementById(formName + "_dataSource_name").value;
	var url = "<s:url action='pciCompliance' includeParams='none' />" + "?operation=export&startTime="+ beginDate +"&endTime=" + endDate + "&locationId="+ mapId + "&reportName="+ reportName + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createDataResult, failure:abortResult,timeout: 3000000}, null);
	if(top.waitingPanel != null) {
		top.waitingPanel.show();
	}
}
var createDataResult = function(o) {
	
	hm.util.hide('processing');
	if(top.waitingPanel != null) {
		top.waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	//alert(result);
	if(result.success) {
		showHangMessage("Download exported file.");
		hm.util.show('downloadSection');
	} else {
		showErrorMessage("Create export file failed!");
	}
}

var abortResult = function(o)
{
	if(top.waitingPanel != null) {
		top.waitingPanel.hide();
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
		     hm.util.reportFieldError(beginDate, 'End date is lower than the start date.');
		     beginDate.focus();
		     return false;
		 }
	 } else {
	  	hm.util.reportFieldError(beginDate, 'Start date and end date should be set.');
		beginDate.focus();
		return false;
	 }

    return true;
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
	
	if (document.getElementById("excuteType1").checked) {
		if (!validateDateTime()) {
			return false;
		}
	} else {
		if (operation == 'export') {
			hm.util.reportFieldError(Get("errorForExport"), "Export operation only support for immediately type.");
			Get("excuteType1").focus();
			return false;
		}
	}

	if (document.getElementById("excuteType2").checked) {
		if (document.getElementById("startTimeForSchedule").value=="") {
		  hm.util.reportFieldError(document.getElementById("startTimeForSchedule"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.guiStartTime" /></s:param></s:text>');
          return false;
		}

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
	var divImmediately = document.getElementById("hideImmediately");
	if (value == "2") {
  		divSchedule.style.display="";
  		divImmediately.style.display="none";
	} else {
		divImmediately.style.display="";
	 	divSchedule.style.display="none";
	}
}

function show_hideRecurrence(value) {
	var divRecurrence = document.getElementById("hideRecurrence");
	if (value) {
  		divRecurrence.style.display="";
	} else {
	  	divRecurrence.style.display="none";
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

function onLoadPage() {
	if (document.getElementById(formName +"_dataSource_recurrenceType1").checked) {
		document.getElementById("selectWeekDay").disabled=true;
	} else {
		document.getElementById("selectWeekDay").disabled=false;
	}

	if (document.getElementById(formName + "_dataSource_name").disabled == false) {
		document.getElementById(formName + "_dataSource_name").focus();
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="pciCompliance" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}

</script>
<div id="content"><s:form action="pciCompliance">
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
					<td><input type="button" name="ignore" value="Export"
						class="button" onClick="submitAction('export');"
						<s:property value="writeDisabled" />></td>
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
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="730px">
					<tr>
						<td height="4px" />
					</tr>
					<tr>
						<td class="labelT1" width="150px"><s:text
							name="report.reportList.name" /><font color="red"><s:text
							name="*" /></font></td>
						<td ><s:textfield name="dataSource.name"
							size="24"
							onkeypress="return hm.util.keyPressPermit(event,'name');"
							maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
							name="report.reportList.name.range" /></td>
					</tr>
					<tr>
						<td class="labelT1" width="150px"><s:text
							name="report.reportList.location" /></td>
						<td ><s:select name="locationId" id="locationId"
							list="%{location}" listKey="id" listValue="value"
							value="locationId" cssStyle="width: 150px;" /></td>
					</tr>
					<tr><td colspan="2"><table><tr><td><div id="errorForExport"/></td></table></td></tr>
					<tr>
						<td style="padding-left: 5px;" colspan="2" ><s:radio label="Gender"
							name="dataSource.excuteType" id="excuteType"
							onclick="show_hideSchedule(this.value);"
							list="#{'1':'Immediately run a report'}" /></td>
					</tr>
					<tr>
						<td colspan="2">
							<div style="display:<s:property value="%{hideImmediately}"/>" id="hideImmediately">
							<table border="0" cellspacing="0" cellpadding="0" width="720px">
								<tr>
								<td style="padding: 10px 15px 10px 15px">
									<fieldset>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="6px" />
										</tr>
										<tr>
											<td width="60px" class="labelT1"><s:text
													name="config.wlanAccess.scheduler.beginDate" /></td>
											<td>		
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="60px"><s:textfield name="startTime"
															id="startTime" value="%{startTime}" readonly="true"
															size="10" maxlength="10" /></td>
														<td width="15px">
														<div id="startdatefields"></div>
														</td>
														<td width="30px" />
														<td width="60px" class="labelT1"><s:text
															name="config.wlanAccess.scheduler.endDate" /></td>
														<td width="60px"><s:textfield name="endTime"
															id="endTime" value="%{endTime}" readonly="true"
															size="10" maxlength="10" /></td>
														<td width="15px">
														<div id="enddatefields"></div>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
								</td>
								</tr>
							</table>
							
						</div>
						</td>
					</tr>
					<tr>
						<td style="padding-left: 5px;" colspan="2" ><s:radio label="Gender"
							name="dataSource.excuteType" id="excuteType"
							onclick="show_hideSchedule(this.value);"
							list="#{'2':'Schedule a recurring series of reports'}" /></td>
					</tr>
					<tr>
						<td colspan="2">
						<div style="display:<s:property value="%{hideSchedule}"/>" id="hideSchedule">
						<table border="0" cellspacing="0" cellpadding="0" width="720px">
							<tr>
								<td style="padding: 10px 15px 10px 15px">
								<fieldset>
								<div>
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td height="6px" />
									</tr>
									<tr>
										<td colspan="2" class="labelT1">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="150px" nowrap="nowrap"><s:text
													name="report.reportList.guiStartTime" /></td>
												<td width="60px"><s:textfield name="startTimeForSchedule"
													id="startTimeForSchedule" value="%{startTimeForSchedule}" readonly="true"
													size="10" maxlength="10" /></td>
												<td width="15px">
												<div id="startdatefieldsForSchedule"></div>
												</td>
												<td width="80px"><s:select name="startHour"
													id="startHour" value="%{startHour}" list="%{lstHours}"
													listKey="id" listValue="value" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td class="labelT1" width="150px"><s:text
											name="report.reportList.reportPeriod" /></td>
										<td><s:select name="dataSource.reportPeriod"
											list="%{enumReportPeriod}" listKey="key" listValue="value"
											value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
									</tr>
									<tr>
										<td colspan="2" style="padding-left: 6px"><s:checkbox name="dataSource.enabledRecurrence"
											value="%{dataSource.enabledRecurrence}"
											onclick="show_hideRecurrence(this.checked);"/> <s:text
											name="report.reportList.enabledRecurrence" /></td>
									</tr>
									<tr>
										<td colspan="2">
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
														<td style="padding-left: 35px;" width="125px"><s:radio
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
									<tr>
										<td colspan="2">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td width="150px" class="labelT1" nowrap="nowrap"><s:text
													name="report.reportList.emailAddress" /><font color="red">
													<s:text name="*" /></font></td>
												<td><s:textfield name="dataSource.emailAddress"
													id="emailAddress" size="42" maxlength="128" />
													<s:text name="report.reportList.email.emailNoteRange" /></td>
											</tr>
											<tr>
												<td colspan="2" nowrap="nowrap" style="padding-left: 10px" class="noteInfo"><s:text
														name="report.reportList.email.note" /></td>
											</tr>
											<tr>
												<td colspan="2" nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
														name="report.reportList.email.emailNote" /></td>
											</tr>
										</table>
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
						<td height="8"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form>
</div>

