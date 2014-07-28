<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script
	src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>

<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding: 10px;
}

#calendarpicker1 button, #calendarpicker2 button {
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
        function onButtonClick1() {

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
                    var endDate_doc = document.getElementById("endTime");
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
        var oCalendarMenu2 = new YAHOO.widget.Overlay("calendarmenu");
        // Create a Button instance of type "menu"
        var startTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "beginDateTimeDiv" });

        // Create a Button instance of type "menu"
        var endTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "endDateTimeDiv" });
        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton.on("click", onButtonClick1);
        endTimeButton.on("click", onButtonClick2);
	});

</script>

<script>
var formName = 'auditLog';
function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'refresh')
		{
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
    }
}
function validate(operation)
{
	if (operation != 'refresh')
	{
		return true;
	}

	if (!validateTime())
	{
		return false;
	}

	return true;
}

function validateTime()
{
	var timeRange = document.getElementById("timeRange");
	if (timeRange.checked)
	{
		var startTimeElement = document.getElementById("startTime");
		var endTimeElement = document.getElementById("endTime");
		var startHourElement = document.getElementById("startHour");
		var endHourElement = document.getElementById("endHour");
		var errElement = document.getElementById("messageSection");

		if (startTimeElement.value >endTimeElement.value
			 || (startTimeElement.value ==endTimeElement.value
			 &&  parseInt(startHourElement.value) > parseInt(endHourElement.value)))
		{
		     hm.util.reportFieldError(errElement, '<s:text name="error.notLargerThan"><s:param><s:text name="admin.audtiLog.startTime" /></s:param><s:param><s:text name="admin.audtiLog.endTime" /></s:param></s:text>');
			return false;
		}
	}

    return true;
}

function insertPageContext()
{
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}

function selectTimeRange(checked)
{
	if (checked)
	{
		document.getElementById("startHour").disabled = false;
		document.getElementById("beginDateTimeDiv").disabled = false;
		document.getElementById("endHour").disabled = false;
		document.getElementById("endDateTimeDiv").disabled = false;
	}
	else
	{
		document.getElementById("startHour").disabled = true;
		document.getElementById("beginDateTimeDiv").disabled = true;
		document.getElementById("endHour").disabled = true;
		document.getElementById("endDateTimeDiv").disabled = true;
	}
}

</script>

<div id="content">
	<s:form action="auditLog">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="refresh" value="Refresh"
									class="button" onClick="submitAction('refresh');">
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" width="800px">
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td width="70px" class="labelT1">
											<label>
												<s:text name="admin.auditLog.userName" />
											</label>
										</td>
										<td width="100px" style="padding-left: 5px;">
											<s:select name="userId" value="%{userId}" list="users"
												listKey="id" listValue="userName" />
										</td>
										<td width="20px" style="padding-left: 15px;">
											<s:checkbox name="timeRange" id="timeRange"
												onclick="selectTimeRange(this.checked);" />
										</td>
										<td width="80px">
											<label>
												<s:text name="admin.auditLog.timeRange" />
											</label>
										</td>
										<td width="150px" style="padding-left: 5px;">
											<table id="messageSection" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td>
														<s:textfield name="startTime" id="startTime"
															value="%{startTime}" readonly="true" size="10"
															maxlength="10" />
													</td>
													<td width="30px">
														<div id="beginDateTimeDiv" />
													</td>
													<td>
														<s:select name="startHour" id="startHour"
															value="%{startHour}" list="%{lstHours}" listKey="id"
															listValue="value" disabled="%{!timeRange}" />
													</td>
												</tr>
											</table>
										</td>
										<td width="20px" align=center>
											<label>
												<s:text name="admin.audtiLog.to" />
											</label>
										</td>
										<td width="150px">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td>
														<s:textfield name="endTime" id="endTime"
															value="%{endTime}" readonly="true" size="10"
															maxlength="10" />
													</td>
													<td width="30px">
														<div id="endDateTimeDiv" />
													</td>
													<td>
														<s:select name="endHour" id="endHour" value="%{endHour}"
															list="%{lstHours}" listKey="id" listValue="value"
															disabled="%{!timeRange}" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td style="padding:0 4px 0 4px">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="sepLine" colspan="3">
											<img src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td style="padding:0 4px 0 8px">
								<table cellspacing="0" cellpadding="0" border="0" width="100%"
									class="view">
									<tr>
										<s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th align="left" nowrap>
													<ah:sort name="owner" key="admin.auditLog.userName" />
												</th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th align="left" nowrap>
													<ah:sort name="hostIP" key="admin.auditLog.hostIP" />
												</th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th align="left" nowrap>
													<s:text name="admin.auditLog.operation" />
												</th>
											</s:if>
											<s:if test="%{columnId == 4}">
												<th align="left" nowrap>
													<ah:sort name="status" key="admin.auditLog.status" />
												</th>
											</s:if>
											<s:if test="%{columnId == 5}">
												<th align="left" nowrap>
													<ah:sort name="logTimeStamp" key="admin.auditLog.time" />
												</th>
											</s:if>
										</s:iterator>
										<s:if test="%{showDomain}">
											<th>
												<ah:sort name="owner.domainName" key="config.domain" />
											</th>
										</s:if>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<s:iterator value="%{selectedColumns}">
												<s:if test="%{columnId == 1}">
													<td class="list">
														<s:property value="%{userOwner}" />
													</td>
												</s:if>
												<s:if test="%{columnId == 2}">
													<td class="list">
														<s:property value="%{hostIP}" />
													</td>
												</s:if>
												<s:if test="%{columnId == 3}">
													<td class="list">
														<s:property value="%{opeationComment}" />
													</td>
												</s:if>
												<s:if test="%{columnId == 4}">
													<td class="list">
														<s:property value="%{statusStr}" />
													</td>
												</s:if>
												<s:if test="%{columnId == 5}">
													<td class="list" nowrap>
														<s:property value="%{logTime}" />
													</td>
												</s:if>
											</s:iterator>
											<s:if test="%{showDomain}">
												<td class="list">
													<s:property value="%{owner.domainName}" />
												</td>
											</s:if>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
