<%@ taglib prefix="s" uri="/struts-tags"%>

<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
</style>


<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding: 10px;
}
#calendarpicker1 button, #calendarpicker2 button, #calendarpicker3 button, #calendarpicker4 button {
    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
}
 #month-field,
    #day-field {
        width: 2em;
    }

    #year-field {
        width: 3em;
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
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id,{navigator: true});
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
                    var beginDate_doc = document.getElementById("beginDate");
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
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar2", oCalendarMenu2.body.id,{navigator: true});
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
                    var endDate_doc = document.getElementById("endDate");
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
        var startDateTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "showBeginDateTime" });

        // Create a Button instance of type "menu"
        var endDateTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "showEndDateTime" });
        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startDateTimeButton.on("click", onButtonClick1);
        endDateTimeButton.on("click", onButtonClick2);
	});

</script>


<script>
var filterFormName = 'alarmFilter';
var filterChangedType=1;

function submitFilterAction(operation) {
	if (validateFilter(operation)) {
		document.forms[filterFormName].operation.value = operation;
    	document.forms[filterFormName].submit();
    }
}
function validateFilter(operation) {
	if (operation=='removeFilter') {
		if (document.getElementById("filterSelect").value == '-1'){
			return false;
		}
	}
	return true;
}

function clearStartDate(){
	document.getElementById("beginDate").value = "";
}

function clearEndDate(){
	document.getElementById("endDate").value = "";
}

var filterOverlay = null;
function createFilterOverlay() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	filterOverlay.render(document.body);
	div.style.display = "";
	overlayManager.register(filterOverlay);
	filterOverlay.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openFilterOverlay(){
	if(null == filterOverlay){
		createFilterOverlay();
	}
	initialValues();
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}

function filterChanged(value,changeype){
	filterChangedType=changeype;
	if(value == -1){
		submitFilterAction('view');
	}else{
		url = "<s:url action='alarms' includeParams='none' />" + "?operation=requestFilterValues&filter=" + encodeURIComponent(value) + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:filterResult,failure:connectedFailed,timeout: 60000}, null);
	}
}

function editFilterOverlay(){
	filterChanged(document.getElementById("filterSelect").value, 2);
}

var filterResult = function(o) {
	eval("var result = " + o.responseText);
	if(result.nodeId){
		document.getElementById("apId").value = result.nodeId;
	}else{
		document.getElementById("apId").value = "";
	}
	if(result.aSeverity){
		document.getElementById("severity").key = result.aSeverity;
	}else{
		document.getElementById("severity").key = -1;
	}
	if(result.aComponent){
		document.getElementById("component").value = result.aComponent;
	}else{
		document.getElementById("component").value = "";
	}
	if(result.aBeginDate){
		document.getElementById("beginDate").value = result.aBeginDate;
	}else{
		document.getElementById("beginDate").value = "";
	}
	if(result.aBeginTime){
		document.getElementById("beginTimeH").value = parseInt(result.aBeginTime);
	}else{
		document.getElementById("beginTimeH").value = 0;
	}
	if(result.aEndDate){
		document.getElementById("endDate").value = result.aEndDate;
	}else{
		document.getElementById("endDate").value = "";
	}
	if(result.aEndTime){
		document.getElementById("endTimeH").value = parseInt(result.aEndTime);
	}else{
		document.getElementById("endTimeH").value = 0;
	}
	if(result.fName){
		document.getElementById("filterName").value = result.fName;
	}else{
		document.getElementById("filterName").value = "";
	}
	
	if (filterChangedType ==1) {
		submitFilterAction('search');
	} else {
		if(null == filterOverlay){
			createFilterOverlay();
		}
		filterOverlay.cfg.setProperty('visible', true);
	}
}

var connectedFailed = function(o) {
	//
}

function initialValues(){
	document.getElementById("apId").value = '';
	document.getElementById("severity").key = -1;
	document.getElementById("component").value = '';
	document.getElementById("beginDate").value = '';
	document.getElementById("beginTimeH").value = 0;
	document.getElementById("endDate").value = '';
	document.getElementById("endTimeH").value = 0;
	document.getElementById("filterName").value = '';
}
</script>
<div id="leftFilter">
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="filterSep" colspan="2">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td class="sepLine">
							<img src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="filterH1">
				Filter
			</td>
			<td>
				
				<s:select id="filterSelect" name="filter" headerKey="-1"
					headerValue="None" list="filterList" cssStyle="width:100px;"
					onchange="filterChanged(this.value, 1);" />
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td colspan="2">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding-left: 50px;">
							<a class="marginBtn" href="javascript:openFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:editFilterOverlay();">
								<img class="dinl" src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
						</td>
						<td>
							<a class="marginBtn" href="javascript:submitFilterAction('removeFilter');">
								<img class="dinl" src="<s:url value="/images/cancel.png" />"
								width="16" height="16" alt="Remove" title="Remove" /></a>
						</td>
						
						<%-- td class="filterBtn" style="padding-left: 9px;">
							<input type="button" name="ignore" value="New" class="button"
								onClick="openFilterOverlay();">
						<td class="filterBtn" style="padding-left: 0px;" width="100%">
							<input type="button" name="ignore" value="Remove" class="button"
								onClick="submitFilterAction('removeFilter');">
								--%>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	<div id="filterPanel" style="display: none;">
		<div class="hd">
			Filter Alarm By
		</div>
		<div class="bd">
			<s:form action="alarms" id="alarmFilter" name="alarmFilter">
				<s:hidden name="operation" />
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td colspan="2">
							<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
									width="100%">
								<tr>
									<td style="padding: 6px 5px 5px 5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="filterT1">
													Node ID
												</td>
												<td>
													<s:textfield name="apId" id="apId" size="15" maxlength="12" />
												</td>
											</tr>
											<tr>
												<td class="filterT1">
													Severity
												</td>
												<td>
													<s:select name="severity" id="severity" headerKey="-1" headerValue="All"
														list="enumSeverity" listKey="key" listValue="value" />
												</td>
											</tr>
											<tr>
												<td class="filterT1">
													Component
												</td>
												<td>
													<s:textfield name="component" id="component" value="%{component}" size="15" maxlength="30" />
												</td>
											</tr>
											<tr>
												<td class="filterT1">
													<s:text name="monitor.events.filterStartTime"/>
												</td>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<s:textfield name="beginDate" id="beginDate" value="%{beginDate}"
																	readonly="true" size="15" maxlength="10" onclick="clearStartDate();"/>
															</td>
															<td>
																<div id="showBeginDateTime"/>
															</td>
															<td>
														 		<s:select name="beginTimeH" id="beginTimeH" value="%{beginTimeH}" 
														 			list="enumHours" listKey="key" listValue="value" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="filterT1">
													<s:text name="monitor.events.filterEndTime"/>
												</td>
												<td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<s:textfield name="endDate" id="endDate" value="%{endDate}" 
																	readonly="true" size="15" maxlength="10" onclick="clearEndDate();" />
															</td>
															<td>
																<div id="showEndDateTime" />
															</td>
															<td>
																<s:select name="endTimeH" id="endTimeH" value="%{endTimeH}" 
																	list="enumHours" listKey="key" listValue="value" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="5px"/>
					</tr>
					<tr>
						<td class="labelT1" width="160px">
							<s:text name="monitor.activeClient.filter.name"/>
						</td>
						<td>
							<s:textfield name="filterName" id="filterName" maxlength="32" size="24"/>
						</td>
					</tr>
					<tr>
						<td style="padding-top: 8px;" colspan="2">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="button" name="ignore" value="Search" id="Search"
											class="button" onClick="submitFilterAction('search');" />
									</td>
									<td>
										<input type="button" name="ignore" value="Cancel" id="Cancel"
											class="button" onClick="hideOverlay();" />
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</s:form>
		</div>
	</div>
</div>
