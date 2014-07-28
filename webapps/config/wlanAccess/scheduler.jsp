<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

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
                    var beginDate_doc = document.getElementById("beginDateTime");
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
                    var endDate_doc = document.getElementById("endDateTime");
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

        function onButtonClick3() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu3.setBody("&#32;");
            oCalendarMenu3.body.id = "calendarcontainer3";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu3.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu3.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar3", oCalendarMenu3.body.id,{navigator: true});
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {

                    oCalendarMenu3.show();

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
                    var endDate_doc = document.getElementById("beginDate");
                    endDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu3.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick3);

        };

        function onButtonClick4() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu4.setBody("&#32;");
            oCalendarMenu4.body.id = "calendarcontainer4";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu4.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu4.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar4", oCalendarMenu4.body.id,{navigator: true});
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {

                    oCalendarMenu4.show();

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
                oCalendarMenu4.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick4);

        };
        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");
        var oCalendarMenu2 = new YAHOO.widget.Overlay("calendarmenu");

        var oCalendarMenu3 = new YAHOO.widget.Overlay("calendarmenu");
        var oCalendarMenu4 = new YAHOO.widget.Overlay("calendarmenu");
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
         // Create a Button instance of type "menu"
        var startDateButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker3",
                                            label: "",
                                            menu: oCalendarMenu3,
                                            container: "showBeginDate" });

        // Create a Button instance of type "menu"
        var endDateButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker4",
                                            label: "",
                                            menu: oCalendarMenu4,
                                            container: "showEndDate" });
        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startDateTimeButton.on("click", onButtonClick1);
        endDateTimeButton.on("click", onButtonClick2);
        startDateButton.on("click", onButtonClick3);
        endDateButton.on("click", onButtonClick4);
	});

</script>


<script>
function onLoadPage() {
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(800,450);
	 	}
	 </s:if>
}

var formName = 'scheduler';
var type;
var count='0';
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'cancel<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function saveScheduler(operation) {
	if(validate(operation)){
		var url = "<s:url action='scheduler' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
		document.forms["scheduler"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["scheduler"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveScheduler, failure : failSaveSchduler, timeout: 60000}, null);
	}
}

var succSaveScheduler = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.id != null && details.id != ''){
					hm.util.insertSelectValue(details.id, details.name, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		alert("error");
	}
}

var failSaveSchduler = function(o){
	
}

function validate(operation) {
     if(operation == 'cancel<s:property value="lstForward"/>')
       return true;

      inputElement=document.getElementById("schedulerName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.wlanAccess.scheduler.schedulerName" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }

      inputElement = document.getElementById("recurrentType");
      if(count=='0')
         type=inputElement.value;
      if(type=="recurrent")
      {
         var chbox;
         if(!checkTime("endTime","beginTime"))
            return false;
         chbox=document.getElementById("chboxTimeS");
         if(chbox.checked && !checkTime("endTimeS","beginTimeS"))
            return false;
         if(chbox.checked && !compareFirstTimeAndSecondTime())
            return false;

         chbox = document.getElementById("chboxDate");
         if (chbox.checked)
         {
             var begindate = document.getElementById("beginDate");
             if (begindate.value.length == 0)
             {
                hm.util.reportFieldError(Get("beginDateErrorDiv"), '<s:text name="error.requiredField"><s:param><s:text name="config.wlanAccess.scheduler.beginDate" /></s:param></s:text>');
                begindate.focus();
                return false;
             }
             var chbox_end = document.getElementById("chboxEndDate");
             var enddate = document.getElementById("endDate");
             if(chbox_end.checked && enddate.value.length==0)
             {
                  hm.util.reportFieldError(Get("endDateErrorDiv"), '<s:text name="error.requiredField"><s:param><s:text name="config.wlanAccess.scheduler.endDate" /></s:param></s:text>');
                  enddate.focus();
                  return false;
             }
             else
             if(chbox_end.checked && !checkDate("endDate","beginDate"))
                  return false;
         }
      }
      else
      if(type=="once")
      {
         var beginDate_obj = document.getElementById("beginDateTime");
         if (beginDate_obj.value.length == 0) {
            hm.util.reportFieldError(beginDate_obj, '<s:text name="error.requiredField"><s:param><s:text name="config.wlanAccess.scheduler.beginDate" /></s:param></s:text>');
            beginDate_obj.focus();
            return false;
         }
         var endDate_obj = document.getElementById("endDateTime");
         if (endDate_obj.value.length == 0) {
            hm.util.reportFieldError(endDate_obj, '<s:text name="error.requiredField"><s:param><s:text name="config.wlanAccess.scheduler.endDate" /></s:param></s:text>');
            endDate_obj.focus();
            return false;
         }
         if(!checkDateTime("endDateTime","beginDateTime"))
            return false;
      }

	return true;
}
function checkDateTime(endDate_obj_id,beginDate_obj_id)
{
   var endDate_obj=document.getElementById(endDate_obj_id);
   var beginDate_obj=document.getElementById(beginDate_obj_id);
   var hour_obj=document.getElementById(beginDate_obj_id+"H");
   var minute_obj=document.getElementById(beginDate_obj_id+"M");
   var begin_date_time=beginDate_obj.value+" "+hour_obj.value+":"+minute_obj.value;
   hour_obj=document.getElementById(endDate_obj_id+"H");
   minute_obj=document.getElementById(endDate_obj_id+"M");
   var end_date_time=endDate_obj.value+" "+hour_obj.value+":"+minute_obj.value;
   if(hm.util.compareDatetime(end_date_time,begin_date_time)!="1"){
       hm.util.reportFieldError(beginDate_obj, '<s:text name="error.date.compare.endDateLower"><s:param><s:text name="config.wlanAccess.scheduler.beginDate" /></s:param></s:text>');
       beginDate_obj.focus();
       return false;
   }
   return true;
}
function checkDate(endDate_obj_id,beginDate_obj_id)
{
   var endDate_obj=document.getElementById(endDate_obj_id);
   var beginDate_obj=document.getElementById(beginDate_obj_id);

   if(parseInt(hm.util.compareDate(endDate_obj.value,beginDate_obj.value))<0){
       hm.util.reportFieldError(Get("beginDateErrorDiv"), '<s:text name="error.date.compare.endDateLower"><s:param><s:text name="config.wlanAccess.scheduler.beginDate" /></s:param></s:text>');
       beginDate_obj.focus();
       return false;
   }
   return true;
}
function checkTime(endTime_obj_id,beginTime_obj_id)
{
   var hour_obj=document.getElementById(beginTime_obj_id+"H");
   var minute_obj=document.getElementById(beginTime_obj_id+"M");
   var begin_time=hour_obj.value+":"+minute_obj.value;
   hour_obj=document.getElementById(endTime_obj_id+"H");
   minute_obj=document.getElementById(endTime_obj_id+"M");
   var end_time=hour_obj.value+":"+minute_obj.value;
   if(hm.util.compareTime(end_time,begin_time)!="1"){
       hm.util.reportFieldError(hour_obj, '<s:text name="error.time.compare.endTimeLower"><s:param><s:text name="config.wlanAccess.scheduler.beginTime" /></s:param></s:text>');
       hour_obj.focus();
       return false;
   }
   return true;
}
function compareFirstTimeAndSecondTime()
{
   var hour_obj=document.getElementById("endTimeH");
   var minute_obj=document.getElementById("endTimeM");
   var first=hour_obj.value+":"+minute_obj.value;
   hour_obj=document.getElementById("beginTimeSH");
   minute_obj=document.getElementById("beginTimeSM");
   var second=hour_obj.value+":"+minute_obj.value;
   if(parseInt(hm.util.compareTime(second,first))<0){
       hm.util.reportFieldError(hour_obj, '<s:text name="error.time.compare.firstAndSecond"><s:param><s:text name="config.wlanAccess.scheduler.beginTime" /></s:param></s:text>');
       hour_obj.focus();
       return false;
   }
   return true;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="scheduler" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="%{displayName}" />\'</td>');
		</s:else>
	</s:else>
}
function showRecurrent(radio)
{
  type=radio;
  count='1';
  if(radio=="recurrent")
  {
     recurrentType="type_recurrent";
     var obj=document.getElementById("recurrent_div");
     obj.style.display="block";
     var obj1=document.getElementById("once_div");
     obj1.style.display="none";
  }
  if(radio=="once")
  {
     recurrentType="type_once";
     var obj=document.getElementById("once_div");
     obj.style.display="block";
     var obj1=document.getElementById("recurrent_div");
     obj1.style.display="none";
  }
}

function isChboxEnabled(type)
{
    var chbox=document.getElementById(type);
    var div_id=type+"_div";
    var obj=document.getElementById(div_id);

    if(type=="chboxTime" || type=="chboxDateTime")
    {
       chbox.checked="true";
    }

    if(type=="chboxTimeS")
    {
       var hideH=document.getElementById("beginTimeSH");
       var hideM=document.getElementById("beginTimeSM");
       hideH.disabled=!chbox.checked;
       hideM.disabled=!chbox.checked;
       if(hideH.disabled)
          hideH.value="0";
       if(hideM.disabled)
          hideM.value="0";
       hideH=document.getElementById("endTimeSH");
       hideM=document.getElementById("endTimeSM");
       hideH.disabled=!chbox.checked;
       hideM.disabled=!chbox.checked;
        if(hideH.disabled)
          hideH.value="0";
       if(hideM.disabled)
          hideM.value="0";
    }
    if(type=="chboxWeek")
    {
       var hide=document.getElementById("weekBegin");
       hide.disabled=!chbox.checked;
       if(hide.disabled)
          hide.value="Monday";
       hide=document.getElementById("weekEnd");
       hide.disabled=!chbox.checked;
       if(hide.disabled)
          hide.value="Monday";
    }
    if(type=="chboxDate")
    {
       var hide=document.getElementById("showBeginDate");
       hide.style.display=chbox.checked?"":"none";
       var endDate=document.getElementById("chboxEndDate");
       if(!chbox.checked){
            endDate.checked=chbox.checked;
            hide=document.getElementById("showEndDate");
            hide.style.display=chbox.checked?"":"none";
       }
       endDate.disabled=!chbox.checked;
       var beginDateValue=document.getElementById("beginDate");
       var endDateValue=document.getElementById("endDate");
       if(hide.style.display=="none")
       {
          beginDateValue.value="";
          endDateValue.value="";
       }
    }
    if(type=="chboxEndDate")
    {
        hide=document.getElementById("showEndDate");
        hide.style.display=chbox.checked?"":"none";
        var endDateValue=document.getElementById("endDate");
        if(hide.style.display=="none")
        {
           endDateValue.value="";
        }
    }
}
<s:if test="%{jsonMode==true && contentShownInDlg == false}">
	<s:if test="%{writeDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
</s:if>
</script>

<div id="content">
  <s:form action="scheduler" >
  	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="titleDiv" class="topFixedTitle">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-schedules-big.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}"><s:text name="config.title.schedules"/></s:if>
								<s:else><s:text name="config.title.schedules.edit"/></s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk" />
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<s:if test="%{!parentIframeOpenFlg}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == 'disabled'}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onclick="saveScheduler('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onclick="saveScheduler('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</s:else>
						</tr>
					</table>
					</s:if>
					<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="schedulerDlgId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</s:else>
					</td>
				</tr>
			</table>
		</div>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		</s:if>
		<tr style="display:<s:property value="%{hide}"/>" >
		  <td><s:textfield value="%{radioRecurrent}" name="recurrentType" id="recurrentType"/></td>
		</tr>
		<s:if test="%{jsonMode == false}">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
		</tr>

		<tr>
			<td>
			<s:if test="%{jsonMode == true}">
				<s:if test="contentShownInDlg == false">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
				</s:if>
				<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				</s:else>
			</s:if>
			<s:else>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700">
			</s:else>
			    <!-- begin name and comment-->
				<tr>
				  <td style="padding: 10px 5px 0px 10px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="115" style="padding:10px 0px 0px 0px">
									<label><s:text name="config.wlanAccess.scheduler.schedulerName" /><font color="red"><s:text name="*"/></font></label></td>
									<td style="padding:6px 0px 0px 0px"><s:textfield name="dataSource.schedulerName" id="schedulerName"
										size="40" maxlength="%{nameLength}" disabled="%{disableName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" />
										<s:text name="config.name.range"/></td>
								</tr>
								<tr>
									<td class="labelT1" width="115" style="padding:10px 0px 0px 0px"><label><s:text
										name="config.wlanAccess.scheduler.description" /></label></td>
									<td style="padding:6px 0px 0px 0px"><s:textfield name="dataSource.description"
										size="60" maxlength="%{descriptionLength}" />
										<s:text name="config.description.range"/></td>
								</tr>
							</table>
							</td>
						</tr>
					 </table>
					</td>
				</tr>
			    <!-- end name and comment-->

				<!-- begin radio button-->
				<tr>
					<td style="padding: 0px 5px 6px -1px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr >
			              <td class="labelT1"><s:radio label="Gender" name="radioRecurrent" id="radioRecurrent"
				             list="#{'once':'One-Time'}"
				               onclick="showRecurrent(this.value)"/><s:radio label="Gender" name="radioRecurrent" id="radioRecurrent"
				             list="#{'recurrent':'Recurrent'}"
				               onclick="showRecurrent(this.value)"/></td>
		                </tr>
		            </table>
		          </td>
		         </tr>
				<!-- end radio button-->
				<tr>
				   <td style="padding:0px 4px 0px 4px;" colspan="2">
				     <table cellspacing="0" cellpadding="0" border="0" width="100%">
				        <tr>
				         <td class="sepLine" colspan="2"><img
						   src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
						</tr>
					 </table>
					</td>
				</tr>

				<!-- begin date or time setting-->
				<tr>
				 <td style="padding: 5px 5px 6px 10px;">
				  <!-- begin recurrent-->
			      <div style="display:<s:property value="%{recurrent_div}"/>" id="recurrent_div">
					<table cellspacing="0" cellpadding="0" border="0">
			           <!-- begin time No.1-->
				       <tr>
				         <td>
				             <table border="0" cellspacing="0" cellpadding="0" width="115">
				              <tr>
				          	   <td style="padding:10px 0px 0px 0px"><s:checkbox
				                  name="chboxTime"  id="chboxTime"
				                  onclick="isChboxEnabled(this.id)"/>
				                  <s:text name="config.wlanAccess.scheduler.time"/><font color="red"><s:text name="*"/></font></td>
				              </tr>
				             </table>
				         </td>
				         <td colspan="2">
				            <table border="0" cellspacing="0" cellpadding="0">
				              <tr>
				          	   <td style="padding:10px 0px 0px 0px" colspan="2">
				          	      <s:select name="beginTimeH" id="beginTimeH" value="%{beginTimeH}"
								   list="enumHours" listKey="key" listValue="value" />
								     <s:select name="beginTimeM" id="beginTimeM" value="%{beginTimeM}"
								   list="enumMinutes" listKey="key" listValue="value" />
								</td>
								<td width="70" style="padding:10px 0px 0px 10px" >
						          <s:text name="config.wlanAccess.scheduler.endTime" /></td>
							   <td style="padding:10px 0px 0px 0px" >
								 <s:select name="endTimeH" id="endTimeH" value="%{endTimeH}"
								   list="enumHours" listKey="key" listValue="value"/>
								     <s:select name="endTimeM" id="endTimeM" value="%{endTimeM}"
								   list="enumMinutes" listKey="key" listValue="value" /></td>
				              </tr>
				            </table>
				         </td>
					   </tr>
					   <!-- end time No.1-->


				       <!-- begin time No.2-->
				       <tr>
				         <td>
				           <div style="display:<s:property value="block"/>" >
				             <table border="0" cellspacing="0" cellpadding="0" width="115">
				              <tr>
				          	   <td style="padding:10px 0px 0px 0px"><s:checkbox
				                  name="chboxTimeS"  id="chboxTimeS"
				                  onclick="isChboxEnabled(this.id)"/>
				              <s:text name="config.wlanAccess.scheduler.timeS"/></td>
				              </tr>
				             </table>
				           </div>
				         </td>
				         <td colspan="2">
				           <table border="0" cellspacing="0" cellpadding="0">
				             <tr>
				          	   <td style="padding:10px 0px 0px 0px" colspan="2">
				          	       <s:select name="beginTimeSH"  value="%{beginTimeSH}"
								   list="enumHours" listKey="key" listValue="value" id="beginTimeSH" disabled="%{hideBeginTimeSH}"/>
								     <s:select name="beginTimeSM"  value="%{beginTimeSM}"
								   list="enumMinutes" listKey="key" listValue="value" id="beginTimeSM" disabled="%{hideBeginTimeSM}"/>
						       </td>
						       <td width="70" style="padding:10px 0px 0px 10px" >
						       <s:text name="config.wlanAccess.scheduler.endTimeS" />
							   </td>
						       <td style="padding:10px 0px 0px 0px" >
								 <s:select name="endTimeSH" value="%{endTimeSH}"
								   list="enumHours" listKey="key" listValue="value" id="endTimeSH" disabled="%{hideEndTimeSH}"/>
								     <s:select name="endTimeSM"  value="%{endTimeSM}"
								   list="enumMinutes" listKey="key" listValue="value" id="endTimeSM" disabled="%{hideEndTimeSM}"/></td>
				             </tr>
				           </table>
				         </td>
					    </tr>
				       <!-- end time No.2-->


				       <!-- begin week-->
				       <tr>
				         <td>
				           <div style="display:<s:property value="block"/>">
				             <table border="0" cellspacing="0" cellpadding="0" width="115">
				              <tr>
				          	   <td style="padding:10px 0px 0px 0px"><s:checkbox
				                  name="chboxWeek"  id="chboxWeek"
				                  onclick="isChboxEnabled(this.id)"/>
				              <s:text name="config.wlanAccess.scheduler.weekBegin"/></td>
				              </tr>
				             </table>
				           </div>
				         </td>
				         <td colspan="2">
				            <table border="0" cellspacing="0" cellpadding="0">
				              <tr>
				          	   <td style="padding:10px 0px 0px 0px" colspan="2">
								 <s:select name="weekBegin" value="%{weekBegin}"
								   list="weekValues" listKey="key" listValue="value"
								   id="weekBegin" disabled="%{hideWeekBegin}" cssStyle="width: 110px;"/>
						       </td>
						       <td width="70" style="padding:10px 0px 0px 20px" >
						       <s:text name="config.wlanAccess.scheduler.weekEnd" />
							   </td>
						       <td style="padding:10px 0px 0px 0px" >
								 <s:select name="weekEnd" value="%{weekEnd}"
								   list="weekValues" listKey="key" listValue="value"
								   id="weekEnd" disabled="%{hideWeekEnd}" cssStyle="width: 110px;"/></td>
				              </tr>
				            </table>
				         </td>
					   </tr>
				       <!-- end week-->


						<tr><td colspan="2"><table><tr><td><div id="beginDateErrorDiv"/></td></table></td></tr>
				       <!-- begin date-->
				       <tr>
				         <td>
				           <div style="display:<s:property value="block"/>">
				             <table border="0" cellspacing="0" cellpadding="0" width="115">
				              <tr>
				          	   <td style="padding:8px 0px 0px 0px"><s:checkbox
				                  name="chboxDate"  id="chboxDate"
				                  onclick="isChboxEnabled(this.id)"/>
				              <s:text name="config.wlanAccess.scheduler.beginDate"/></td>
				              </tr>

				             </table>
				           </div>
				         </td>
				         <td>
				            <table border="0" cellspacing="0" cellpadding="0">
				              <tr>
				          	   <td style="padding:8px 0px 0px 0px">
				          	     <s:textfield name="beginDate" id="beginDate" value="%{beginDate}" readonly="true" size="12" maxlength="20"/>
				          	     </td>
				          	     <td width="30" style="padding:8px 0px 0px 0px">
				          	     <div id="showBeginDate" style="display:<s:property value="hideBeginDate"/>"/>
							   </td>
				              </tr>
				            </table>
				         </td>
					   </tr>
						<tr><td colspan="2"><table><tr><td><div id="endDateErrorDiv"/></td></table></td></tr>
					    <tr>
				         <td>
				           <div style="display:<s:property value="block"/>" >
				             <table border="0" cellspacing="0" cellpadding="0" width="115">
				              <tr>
				          	   <td style="padding:8px 0px 0px 0px"><s:checkbox
								 name="chboxEndDate" id="chboxEndDate"  disabled="%{hideChboxEndDate}"
								  onclick="isChboxEnabled(this.id)"/>
								  <s:text name="config.wlanAccess.scheduler.endDate"/></td>
				              </tr>
				             </table>
				           </div>
				         </td>
				         <td>
				            <table border="0" cellspacing="0" cellpadding="0">
				              <tr>
				                <td style="padding:8px 0px 0px 0px">
				          	      <s:textfield name="endDate" id="endDate" value="%{endDate}" readonly="true" size="12" maxlength="20"/>
				                </td>
				          	    <td id="chboxEndDate_div" width="30" style="padding:8px 0px 0px 0px">
				          	     <div id="showEndDate" style="display:<s:property value="hideEndDate"/>"/>
							    </td>
				              </tr>
				            </table>
				         </td>
					   </tr>
				       <!-- end date-->


					 </table>
				  </div>
				  <!-- end recurrent-->

				  <!-- begin once-->
				  <div style="display:<s:property value="%{once_div}"/>" id="once_div">
				    <table border="0" cellspacing="0" cellpadding="0">
				       <tr>
				         <td width="125px" style="padding:5px 0 0 0px">
				          <s:text name="config.wlanAccess.scheduler.begin.once"/><font color="red"><s:text name="*"/></font></td>

		               <td style="padding:5px 0 0 0">
		          	     <s:textfield name="beginDateTime" id="beginDateTime" value="%{beginDateTime}"
		          	      readonly="true" size="12" maxlength="20"/></td>

		          	   <td width="20" style="padding:5px 2px 0 0">
		          	     <div id="showBeginDateTime"/>
					   </td>
					   <td style="padding:5px 0 0 0">
		          	    <s:select name="beginDateTimeH" id="beginDateTimeH" value="%{beginDateTimeH}"
						   list="enumHours" listKey="key" listValue="value" />
						     <s:select name="beginDateTimeM" id="beginDateTimeM" value="%{beginDateTimeM}"
						   list="enumMinutes" listKey="key" listValue="value" /></td>
					   </tr>
					   <tr>
					   <td width="125px" style="padding:10px 0 0 0px">
				          <s:text name="config.wlanAccess.scheduler.end.once"/><font color="red"><s:text name="*"/></font></td>
					   <td style="padding:10px 0 0 0">
		          	     	<s:textfield name="endDateTime" id="endDateTime" value="%{endDateTime}"
		          	     	readonly="true" size="12" maxlength="20"/></td>

		          	    <td width="20" style="padding:10px 2px 0 0">
		         	      <div id="showEndDateTime"/>
					    </td>
					    <td style="padding:10px 0 0 0">
		          	     	<s:select name="endDateTimeH" id="endDateTimeH" value="%{endDateTimeH}"
						   	list="enumHours" listKey="key" listValue="value" />
						    <s:select name="endDateTimeM" id="endDateTimeM" value="%{endDateTimeM}"
						   list="enumMinutes" listKey="key" listValue="value" />
						   </td>
				      </tr>
				    </table>
				  </div>
				  <!-- end once-->
				 </td>
			    </tr>
				<!-- end date or time setting-->

			</table>
			</td>
		</tr>
	</table>
</s:form>
</div>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
	<script>
		setCurrentHelpLinkUrl('<s:property value="helpLink" />');
	</script>
</s:if>