<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.be.admin.QueueOperation.BackupStatusItem"%>

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

#calendarpicker1 button,#calendarpicker2 button {
	background: url(<s:url value="/images/calendar_icon.gif" includeParams="none" />) center center no-repeat;
	margin: 2px 0; /* For IE */
	height: 1.5em; /* For IE */
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
	waitingPanel.setHeader("Preparing the backup file...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var formName = 'backupForm';

var backupStatusOverLay;
var pollTimeoutId;
var interval = 5;

function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
	
	// create backup status overlay
	var div = document.getElementById('backupStatusPanel');
	backupStatusOverLay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		close:false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:4
		});
	backupStatusOverLay.render(document.body);
	div.style.display = "";
}

function onUnloadPage() {
	clearTimeout(pollTimeoutId);
}

function backupImmediate()
{
	document.forms["backupForm"].operation.value = "backupImmediate";

	var formObject = document.getElementById('backupForm');
	YAHOO.util.Connect.setForm(formObject);

	var url = "<s:url action='backupDB' includeParams='none' />";

	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:backupResult}, null);

	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function backupResult(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var data = " + o.responseText);
	if (data.result)
	{
		<s:if test="%{isInHomeDomain}">
			showHangMessage(data.message);
			hm.util.show('downloadSection');
		</s:if>
		<s:else>
			document.getElementById("statusImgTD").style.display="block";
			document.getElementById("waitingTD").style.display="block";
			document.getElementById("cancelTD").style.display="block";
		
			pollBackupStatus();
			if(waitingPanel != null){
				waitingPanel.show();
			}
		</s:else>	
	}
	else
	{
		showErrorMessage(data.message);
	}
}

function pollBackupStatus() {
	var url = "<s:url action='backupDB' includeParams='none' />?operation=pollBackupStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : backupStatus }, null);
}

function backupStatus(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var data = " + o.responseText);
	var status = data.status;
	if (status == <%=BackupStatusItem.BACKUP_FINISHED%>)
	{
		if(null != backupStatusOverLay){
			backupStatusOverLay.cfg.setProperty('visible', false);
		}
	
		clearTimeout(pollTimeoutId);
		if (data.success) {
			showHangMessage(data.message);
			hm.util.show('downloadSection');
		} else {
			showErrorMessage(data.message);
		}
		
		return;
	} else if (status == <%=BackupStatusItem.BACKUP_RUNNING%>)
	{
		document.getElementById("statusImgTD").style.display="none";
		document.getElementById("backupStatusTD").innerHTML = "<td id='backupStatusTD'>"+ data.message +"</td>";
		document.getElementById("waitingTD").style.display="block";
		document.getElementById("cancelTD").style.display="none";
		
		pollTimeoutId = setTimeout("pollBackupStatus()", interval * 1000);  // seconds
	} else if (status == <%=BackupStatusItem.BACKUP_WAITTING%>)
	{
		document.getElementById("backupStatusTD").innerHTML = "<td id='backupStatusTD'>"+ data.message +"</td>";
		document.getElementById("waitingTD").style.display="none";
		
		pollTimeoutId = setTimeout("pollBackupStatus()", interval * 1000);  // seconds
	}
	
	if(null != backupStatusOverLay){
		backupStatusOverLay.cfg.setProperty('visible', true);
	}
}

function cancelBackup()
{
	var url = "<s:url action='backupDB' includeParams='none' />?operation=cancelBackup&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : cancelBackupStatus }, null);
}

function cancelBackupStatus(o)
{
	eval("var data = " + o.responseText);
	if (data.success)
	{
		clearTimeout(pollTimeoutId);
		if(null != backupStatusOverLay){
			backupStatusOverLay.cfg.setProperty('visible', false);
		}
	}
}

function submitAction(operation) {
	
	if (validate(operation))
	{
		if (operation == 'download')
		{
			//document.forms[formName].inputPath.value = inputPath;
		}
		else
		{
			showErrorMessage("Your request is being processed ...");
		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation)
{
	if (operation != 'backupSchedule')
	{
		return true;
	}

	var beginDate = document.getElementById("beginDateTime");
	if ( beginDate.value.length == 0)
	{
        hm.util.reportFieldError(beginDate, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.date" /></s:param></s:text>');
        beginDate.focus();
        return false;
 	}

 	var cbEndTime = document.getElementById("cbEndTime");
 	var endDate = document.getElementById("endDateTime");
 	if (cbEndTime.checked)
  	{
  		if ( endDate.value.length == 0)
		{
            hm.util.reportFieldError(endDate, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.date" /></s:param></s:text>');
            endDate.focus();
            return false;
   	 	}

   	 	// if endtime< starttime, should show a error message
   	 	var compareDateResult = parseInt(hm.util.compareDate(endDate.value,beginDate.value));
   		if(compareDateResult < 0)
   		{
	        hm.util.reportFieldError(beginDate, '<s:text name="error.date.compare.endDateLower"><s:param><s:text name="admin.backupDB.startDate" /></s:param></s:text>');
	        beginDate.focus();
	        return false;
   		}

   		if(compareDateResult == 0)
   		{
   			var hour_obj=document.getElementById("beginDateTimeH");
		    var minute_obj=document.getElementById("beginDateTimeM");
		    var begin_time=hour_obj.value+":"+minute_obj.value;
		    hour_obj=document.getElementById("endDateTimeH");
		    minute_obj=document.getElementById("endDateTimeM");
		    var end_time=hour_obj.value+":"+minute_obj.value;
		    if(hm.util.compareTime(end_time,begin_time)!="1")
		    {
	        	hm.util.reportFieldError(hour_obj, '<s:text name="error.time.compare.endTimeLower"><s:param><s:text name="admin.backupDB.startTime" /></s:param></s:text>');
		    	hour_obj.focus();
	       		return false;
	        }
   		}

  	}

 	var cbRecur = document.getElementById("cbRecur");
 	var interval = document.getElementById("interval");
 	if (cbRecur.checked)
 	{
  	 	if ( interval.value.length == 0)
		{
            hm.util.reportFieldError(interval, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.interval" /></s:param></s:text>');
            interval.focus();
            return false;
   	 	} else if (!isValidInterval(interval))
   	 	{
	   	 	hm.util.reportFieldError(interval, 'The interval value is invalid.');
	        interval.focus();
	        return false;
   	   	}
  	 }

 	var ip = document.getElementById("serverIP");
 	if (ip.value.length == 0)
	{
        hm.util.reportFieldError(ip, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.ip" /></s:param></s:text>');
        ip.focus();
        return false;
    }

	var port = document.getElementById("port");
	if (port.value.length == 0)
	{
        hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.port" /></s:param></s:text>');
        port.focus();
        return false;
     }
     else if ( !isValidPort(port) )
     {
     	hm.util.reportFieldError(port, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.backupDB.port" /></s:param><s:param><s:text name="admin.backupDB.portRange" /></s:param></s:text>');
		port.focus();
		return false;
     }

	var filePath = document.getElementById("filePath");
	if (filePath.value.length == 0)
	{
        hm.util.reportFieldError(filePath, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.filePath" /></s:param></s:text>');
        filePath.focus();
        return false;
    }

 	var userName = document.getElementById("userName");
 	if (userName.value.length == 0)
	{
        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.user" /></s:param></s:text>');
        userName.focus();
        return false;
    }

    var password;
    if (document.getElementById("chkToggleDisplay_remote").checked)
	{
		password = document.getElementById("password");
	}
	else
	{
		password = document.getElementById("password_text");
	}

    if (password.value.length == 0)
	{
	    hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.password" /></s:param></s:text>');
	    password.focus();
	    return false;
    }

	return true;
}

function isValidInterval(interval)
{
	var intValue = parseInt(interval.value);
	if ( intValue>0 && intValue<366)
	{
		return true;
	}

	return false;
}

function isValidPort(port)
{
	var intValue = parseInt(port.value);
	if ( intValue>=0 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

function clearDate()
{
	document.getElementById("beginDateTime").value = "";
	document.getElementById("endDateTime").value = "";
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function selectScheduleBackup(checked)
{
	if (checked)
	{
		hm.util.show('scheduleSection');
		document.getElementById("okBtnTD").style.display="block";
		document.getElementById("backupBtnTD").style.display="none";
	}
	else
	{
		hm.util.hide('scheduleSection');
		document.getElementById("okBtnTD").style.display="none";
		document.getElementById("backupBtnTD").style.display="block";
	}
}

function selectedEndtime(checked)
{
	document.getElementById("endDateTimeDiv").disabled = !checked;
	document.getElementById("endDateTimeH").disabled = !checked;
	document.getElementById("endDateTimeM").disabled = !checked;

	if(!checked)
	{
		// clear input
		document.getElementById("endDateTime").value = "";
		//document.getElementById("endDateTimeH").listKey="0";
		//document.getElementById("endDateTimeM").value="00";
	}
}

function selectRecur(checked)
{
	document.getElementById("interval").disabled = !checked;
	if (!checked)
	{
		document.getElementById("interval").value = "";
	}
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteInfo";
	hm.util.show("noteSection");
}

function hideMessage()
{
	hm.util.hide('noteSection');
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

function clickFTPBtn()
{
	document.forms[formName].operation.value = 'ftpTransfer';
	hm.util.show("transferSection");
	hm.util.hide("downloadBtnSection");
	hm.util.hide("noteTD");
	
}

function clickSCPBtn()
{
	document.forms[formName].operation.value = 'scpTransfer';
	hm.util.show("transferSection");
	hm.util.hide("downloadBtnSection");
	hm.util.hide("noteTD");
}

function submitTransferAction() {
	
	if (validateTransfer())
	{
		var formObject = document.getElementById(formName);
		YAHOO.util.Connect.setForm(formObject);

		var url = "<s:url action='backupDB' includeParams='none' />";
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:transferResult}, null);

		if(waitingPanel != null){
			waitingPanel.setHeader("Transferring the backup file...");
			waitingPanel.show();
		}
	}
}

function transferResult(o)
{
	if(waitingPanel != null){
		waitingPanel.setHeader("Preparing the backup file...");
		waitingPanel.hide();
	}

	cancelTransfer();
	
	eval("var data = " + o.responseText);
	if (data.success)
	{
		showHangMessage(data.message);
	} 
	else 
	{
		showErrorMessage(data.message);
	}
}

function validateTransfer()
{
	var transferServer = document.getElementById("transferServer");
 	if (transferServer.value.length == 0)
	{
        hm.util.reportFieldError(transferServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.ip" /></s:param></s:text>');
        transferServer.focus();
        return false;
    }

	var transferPort = document.getElementById("transferPort");
	if (transferPort.value.length == 0)
	{
        hm.util.reportFieldError(transferPort, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.port" /></s:param></s:text>');
        transferPort.focus();
        return false;
     }
     else if ( !isValidPort(transferPort) )
     {
     	hm.util.reportFieldError(transferPort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.backupDB.port" /></s:param><s:param><s:text name="admin.backupDB.portRange" /></s:param></s:text>');
     	transferPort.focus();
		return false;
     }

	var filePath = document.getElementById("transferFilePath");
	if (filePath.value.length == 0)
	{
        hm.util.reportFieldError(filePath, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.filePath" /></s:param></s:text>');
        filePath.focus();
        return false;
    }

 	var userName = document.getElementById("transferUserName");
 	if (userName.value.length == 0)
	{
        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.user" /></s:param></s:text>');
        userName.focus();
        return false;
    }

    var password;
    if (document.getElementById("chkToggleDisplay").checked)
	{
		password = document.getElementById("transferPassword");
	}
	else
	{
		password = document.getElementById("transferPassword_text");
	}

    if (password.value.length == 0)
	{
	    hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="admin.backupDB.password" /></s:param></s:text>');
	    password.focus();
	    return false;
    }
	
	return true;
}

function cancelTransfer()
{
	hm.util.hide("transferSection");
	hm.util.show("downloadBtnSection");
	hm.util.show("noteTD");
}

</script>

<div id="content"><s:form action="backupDB"
	enctype="multipart/form-data" method="POST" id="backupForm"
	name="backupForm">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td id="backupBtnTD"
						style="display:<s:property value="%{hideImmediate}"/>"><input
						type="button" name="backup" value="Back Up" class="button"
						onClick="backupImmediate();" <s:property value="writeDisabled" />>
					</td>
					<td id="okBtnTD"
						style="display:<s:property value="%{hideSchedule}"/>"><input
						type="button" name="ok" value="OK" class="button"
						onClick="submitAction('backupSchedule');"
						<s:property value="writeDisabled" />></td>
					<s:if test="%{showSchedule}">
						<td><input type="button" name="stop" value="Stop Schedule"
							class="button long" onClick="submitAction('stop');"
							<s:property value="disabledStopSchedule" />
							<s:property value="writeDisabled" />></td>
					</s:if>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="10">
			<div id="noteSection" style="display: none">
			<table width="630px" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td id="noteTD"></td>
					<td class="buttons">
					<div id="downloadSection" style="display: none">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr id="downloadBtnSection">
							<td><input type="button" id="ignore" name="ignore"
								value="Download" class="button"
								onClick="submitAction('download');"></td>
							<td><input type="button" id="ignore" name="ignore"
								value="FTP" class="button" onClick="clickFTPBtn();"></td>
							<td><input type="button" id="ignore" name="ignore"
								value="SCP" class="button" onClick="clickSCPBtn();"></td>
							<td><input type="button" id="cancelBtn" name="ignore"
								value="Cancel" class="button" onClick="initNoteSection();">
							</td>
						</tr>
						<tr id="transferSection" style="display: none">
							<td colspan="4">
							<table>
								<tr>
									<td class="buttons">
									<table>
										<tr>
											<td><input type="button" id="ignore" name="ignore"
												value="Transfer" class="button"
												onClick="submitTransferAction();"></td>
											<td><input type="button" id="ignore" name="ignore"
												value="Cancel" class="button"
												onClick="cancelTransfer();"></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table>
										<tr>
											<td class="labelT1" width="150px"><label> <s:text
												name="admin.backupDB.ip" /><font color="red"><s:text
												name="*" /> </font> </label></td>
											<td class="labelT1" width="150px"><s:textfield
												id="transferServer" name="transferServer" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
											<td class="labelT1" width="50px"><label> <s:text
												name="admin.backupDB.port" /><font color="red"><s:text
												name="*" /> </font> </label></td>
											<td class="labelT1" width="250px"><s:textfield
												id="transferPort" name="transferPort" maxlength="5"
												onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											<s:text name="admin.backupDB.portRange" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label> <s:text
												name="admin.backupDB.filePath" /><font color="red"><s:text
												name="*" /> </font> </label></td>
											<td class="labelT1" colspan=3><s:textfield
												id="transferFilePath" name="transferFilePath" size="67" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label> <s:text
												name="admin.backupDB.user" /><font color="red"><s:text
												name="*" /> </font> </label></td>
											<td class="labelT1"><s:textfield id="transferUserName"
												name="transferUserName" maxlength="32" /></td>
											<td class="labelT1" width="80"><label> <s:text
												name="admin.backupDB.password" /><font color="red"><s:text
												name="*" /> </font> </label></td>
											<td class="labelT1"><s:password id="transferPassword"
												name="transferPassword" maxlength="32" showPassword="true" />
											<s:textfield id="transferPassword_text"
												name="transferPassword" value="" maxlength="32"
												cssStyle="display:none" disabled="true" /></td>
										</tr>
										<tr>
											<td colspan="3">&nbsp;</td>
											<td style="padding-left: 10px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><s:checkbox id="chkToggleDisplay" name="ignore"
														value="true" disabled="%{writeDisable4Struts}"
														onclick="hm.util.toggleObscurePassword(this.checked,['transferPassword'],['transferPassword_text']);" />
													</td>
													<td><s:text name="admin.user.obscurePassword" /></td>
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
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<s:if test="%{showSchedule}">
			<tr>
				<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="700">
					<tr>
						<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
							type="password"></td>
					</tr>
					<tr>
						<td style="padding: 0 0 0 5px" colspan=2><s:radio
							label="Gender" id="partBackup" name="backupScope"
							list="#{'partBackup':'Configuration backup only'}"
							value="%{backupScope}" onclick="document.getElementById('dumpBackupdump').disabled = true;document.getElementById('gzBackupgz').checked = checked" /></td>
					</tr>
					<tr>
						<td style="padding: 10px 0 0 5px" colspan=2><s:radio
							label="Gender" id="fullBackup" name="backupScope"
							list="#{'fullBackup':'Full backup'}" value="%{backupScope}" onclick="document.getElementById('dumpBackupdump').disabled = false" /></td>
					</tr>
					<s:if test="domainType == true">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td style="padding: 0 4px 0 4px" colspan=2>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine"><img
									src="<s:url value="/images/spacer.gif"/>" height="1"
									class="dblk" /></td>
							</tr>
						</table>
						</td>
					</tr>
					</s:if>
					<tr  <s:if test="domainType != true">style='display: none'</s:if>>
						<td style="padding: 10px 0 0 5px">
							<s:radio id="dumpBackup" name="backupType"
							list="#{'dump':'Standard Backup'}" value="%{backupType}" />
							<br>
							<div style="padding-left:20px "><s:text name="admin.backupDB.StandardBackup"></s:text></div>
							<br>
							<s:radio id="gzBackup" name="backupType"
							list="#{'gz':'Backup for Upgrades'}" value="%{backupType}" />
							<br>
							<div style="padding-left:20px "><s:text name="admin.backupDB.BackupForUpgrades"></s:text></div>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td style="padding: 0 4px 0 4px" colspan=2>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine"><img
									src="<s:url value="/images/spacer.gif"/>" height="1"
									class="dblk" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td style="padding: 0 0 0 5px"><s:checkbox name="schedule"
							id="schedule" onclick="selectScheduleBackup(this.checked);" /> <label>
						<s:text name="admin.backupDB.schedule" /> </label></td>
					</tr>
					<tr style="display:<s:property value="%{hideSchedule}"/>"
						id="scheduleSection">
						<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding: 5px 0 0 30px" colspan=2>
								<fieldset style="width: 630px"><legend> <s:text
									name="admin.backupDB.dateTime" /> </legend>
								<div>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td width="80px"><label> <s:text
											name="admin.backupDB.startTime" /> </label></td>
										<td width="40px"><label> <s:text
											name="admin.backupDB.date" /> </label></td>
										<td width="60px"><s:textfield name="beginDateTime"
											id="beginDateTime" value="%{beginDateTime}" readonly="true"
											size="10" maxlength="10" /></td>
										<td width="50px">
										<div id="beginDateTimeDiv"></div>
										</td>
										<td width="300px"><s:select id="beginDateTimeH"
											name="beginDateTimeH" value="%{beginDateTimeH}"
											list="enumHours" listKey="key" listValue="value" /> <s:select
											id="beginDateTimeM" name="beginDateTimeM"
											value="%{beginDateTimeM}" list="enumMinutes" listKey="key"
											listValue="value" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td width="80px"><s:checkbox name="endTime"
											id="cbEndTime" onclick="selectedEndtime(this.checked)" /> <label>
										<s:text name="admin.backupDB.endTime" /> </label></td>
										<td width="40px"><label> <s:text
											name="admin.backupDB.date" /> </label></td>
										<td width="60px"><s:textfield name="endDateTime"
											id="endDateTime" value="%{endDateTime}" readonly="true"
											size="10" maxlength="10" /></td>
										<td width="50px">
										<div id="endDateTimeDiv"></div>
										</td>
										<td width="300px"><s:select id="endDateTimeH"
											name="endDateTimeH" value="%{endDateTimeH}" list="enumHours"
											listKey="key" listValue="value" disabled="%{disabledEndTime}" />
										<s:select id="endDateTimeM" name="endDateTimeM"
											value="%{endDateTimeM}" list="enumMinutes" listKey="key"
											listValue="value" disabled="%{disabledEndTime}" /></td>
									</tr>
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td width="80px"><s:checkbox name="recurring"
											id="cbRecur" onclick="selectRecur(this.checked)" /> <label>
										<s:text name="admin.backupDB.recurring" /> </label></td>
										<td width="40px"><label> <s:text
											name="admin.backupDB.interval" /> </label></td>
										<td colspan=3><s:textfield id="interval" name="interval"
											maxlength="3" size="15" disabled="%{disabledRecur}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />
										<label> <s:text name="admin.backupDB.intervalDays" />
										</label></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
								</table>
								</div>
								</fieldset>
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td style="padding: 5px 0 0 30px" colspan=2>
								<fieldset style="width: 630px"><legend> <s:text
									name="admin.backupDB.remoteSite" /> </legend>
								<div>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="10"></td>
									</tr>
									<tr>
										<td class="labelT1" width="150px"><label> <s:text
											name="admin.backupDB.remoteSite" /> </label></td>
										<td class="labelT1" colspan="3"><s:select
											name="scheduleProtocol" id="scheduleProtocol"
											cssStyle="width: 150px;" list="%{scheduleProtocols}"
											listKey="key" listValue="value" /></td>
									</tr>
									<tr>
										<td class="labelT1" width="150px"><label> <s:text
											name="admin.backupDB.ip" /><font color="red"><s:text
											name="*" /> </font> </label></td>
										<td class="labelT1" width="150px"><s:textfield
											id="serverIP" name="serverIP" maxlength="32"
											onkeypress="return hm.util.keyPressPermit(event,'name');" />
										</td>
										<td class="labelT1" width="50px"><label> <s:text
											name="admin.backupDB.port" /><font color="red"><s:text
											name="*" /> </font> </label></td>
										<td class="labelT1" width="250px"><s:textfield id="port"
											name="port" maxlength="%{portLength}"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />
										<s:text name="admin.backupDB.portRange" /></td>
									</tr>
									<tr>
										<td class="labelT1"><label> <s:text
											name="admin.backupDB.filePath" /><font color="red"><s:text
											name="*" /> </font> </label></td>
										<td class="labelT1" colspan=3><s:textfield id="filePath"
											name="filePath" size="67" /></td>
									</tr>
									<tr>
										<td class="labelT1"><label> <s:text
											name="admin.backupDB.user" /><font color="red"><s:text
											name="*" /> </font> </label></td>
										<td class="labelT1"><s:textfield id="userName"
											name="userName" maxlength="%{userNameLength}" /></td>
										<td class="labelT1" width="80"><label> <s:text
											name="admin.backupDB.password" /><font color="red"><s:text
											name="*" /> </font> </label></td>
										<td class="labelT1"><s:password id="password"
											name="password" maxlength="%{passwdLength}"
											showPassword="true" /> <s:textfield id="password_text"
											name="password" value="" maxlength="%{passwdLength}"
											cssStyle="display:none" disabled="true" /></td>
									</tr>
									<tr>
										<td colspan="3">&nbsp;</td>
										<td style="padding-left: 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><s:checkbox id="chkToggleDisplay_remote" name="ignore"
													value="true" disabled="%{writeDisable4Struts}"
													onclick="hm.util.toggleObscurePassword(this.checked,['password'],['password_text']);" />
												</td>
												<td><s:text name="admin.user.obscurePassword" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td height="10"></td>
									</tr>
								</table>
								</div>
								</fieldset>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
				</td>
			</tr>
		</s:if>
		<s:else>
			<tr>
				<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="500">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td style="padding: 10px 0 20px 10px" align="center"
							valign="middle"><label> <strong><s:text
							name="admin.backupDB.info" /> </strong> </label></td>
					</tr>
				</table>
				</td>
			</tr>
		</s:else>
	</table>
</s:form></div>

<div id="backupStatusPanel" style="display: none">
<div class="hd">Back up Database ...</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table>
			<tr>
				<td width="20px">
				<div id="statusImgTD">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img id="ignore"
							src="<s:url value="/images/waitingSquare.gif" />" /></td>
					</tr>
				</table>
				</div>
				</td>
				<td id="backupStatusTD"></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr style="padding-top: 5px">
		<td id="waitingTD" style="padding-left: 15px"><img
			src="<s:url value="/images/waiting.gif" />" /></td>
	</tr>
	<tr style="padding-top: 5px">
		<td align="center" id="cancelTD"><input type="button"
			id="cancelBtn" name="ignore" value="Cancel" class="button"
			onClick="cancelBackup();" /></td>
	</tr>
</table>
</div>
</div>

<script type="text/javascript">
YAHOO.util.Event.onDOMReady(function () {
	if(<s:property value="%{showLastBackup}" />){
		showHangMessage('<s:property value="%{lastBackupMsg}" />');
		hm.util.show('downloadSection');
	}
	
});
</script>
