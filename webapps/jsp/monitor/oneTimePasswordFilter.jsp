<%@taglib prefix="s" uri="/struts-tags"%>

<style type="text/css">
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
#calendarpicker1 button, #calendarpicker2 button {
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
                    var beginDate_doc = document.getElementById("dateSent");
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
                    var endDate_doc = document.getElementById("dateActivated");
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
        var sentDateTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "showSentDateTime" });

        // Create a Button instance of type "menu"
        var activatedDateTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "showActivatedDateTime" });
        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        sentDateTimeButton.on("click", onButtonClick1);
        activatedDateTimeButton.on("click", onButtonClick2);
	});

</script>

<script>
var filterFormName = 'otpFilter';

function submitFilterAction(operation) {
	if (validateFilter(operation)) {
		document.forms[filterFormName].operation.value = operation;
    	document.forms[filterFormName].submit();
    	hideOverlay();
    }
}
function validateFilter(operation) {
	return true;
}

function clearSentDate(){
	document.getElementById("dateSent").value = "";
}

function clearActivatedDate(){
	document.getElementById("dateActivated").value = "";
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

function initialValues(){
	document.getElementById("password").value = '';
	document.getElementById("username").value = '';
	document.getElementById("email").value = '';
	document.getElementById("dateSent").value = '';
	document.getElementById("dateActivated").value = '';
}
</script>

<div id="filterPanel" style="display: none;">
	<div class="hd">
		<s:text name="monitor.otp.list.filter.title"/>
	</div>
	<div class="bd">
		<s:form action="oneTimePassword" id="otpFilter" name="otpFilter">
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
												<s:text name="monitor.otp.list.password" />
											</td>
											<td>
												<s:textfield name="password" id="password" size="15" maxlength="64" />
											</td>
										</tr>
										<tr>
											<td class="filterT1">
												<s:text name="monitor.otp.list.username" />
											</td>
											<td>
												<s:textfield name="username" id="username" size="15" maxlength="64" />
											</td>
										</tr>
										<tr>
											<td class="filterT1">
												<s:text name="monitor.otp.list.email" />
											</td>
											<td>
												<s:textfield name="email" id="email" size="15" maxlength="64" />
											</td>
										</tr>
										<tr>
											<td class="filterT1">
												<s:text name="monitor.otp.list.sent.date" />
											</td>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
															<s:textfield name="dateSent" id="dateSent"
																readonly="true" size="15" maxlength="10" onclick="clearSentDate();"/>
														</td>
														<td>
															<div id="showSentDateTime"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td class="filterT1">
												<s:text name="monitor.otp.list.activate.date" />
											</td>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td>
															<s:textfield name="dateActivated" id="dateActivated"
																readonly="true" size="15" maxlength="10" onclick="clearActivatedDate();" />
														</td>
														<td>
															<div id="showActivatedDateTime" />
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
