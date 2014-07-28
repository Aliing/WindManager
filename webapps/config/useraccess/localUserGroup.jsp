<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>

<style type="text/css">
div.yuimenu.bd {
	zoom: normal;
}

#calendarcontainer, #calendarcontainer1,#calendarcontainer2 {
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
                    var endDate_doc = document.getElementById("startTimeBulk");
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
        var startTimeBulkButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "startdateBulkfields" });                       

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton.on("click", onButtonClick);
        endTimeButton.on("click", onButtonClick1);
        startTimeBulkButton.on("click", onButtonClick2);

	});

</script>

<script>
var formName = 'localUserGroup';
    
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_groupName").disabled) {
		document.getElementById(formName + "_dataSource_groupName").focus();
	}
	initShowTime();
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(910, 500);
	}
	</s:if>
}

function initShowTime(){
	if (document.getElementById(formName + "_blnStartTime").checked){
		document.getElementById("startdatefields").style.display="block";
		document.getElementById("startHour").disabled=false;
		document.getElementById("startMin").disabled=false;
	} else {
		document.getElementById("startdatefields").style.display="none";
		document.getElementById("startHour").disabled=true;
		document.getElementById("startMin").disabled=true;
	}

	if (document.getElementById(formName + "_blnEndTime").checked){
		document.getElementById("enddatefields").style.display="block";
		document.getElementById("endHour").disabled=false;
		document.getElementById("endMin").disabled=false;
	} else {
		document.getElementById("enddatefields").style.display="none";
		document.getElementById("endHour").disabled=true;
		document.getElementById("endMin").disabled=true;
	}
}
var schedulerSelectId = 'localUserGroup_schedulerId';
function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode && !parentIframeOpenFlg}">
			if(operation == 'newSchedule'){
				var url = "<s:url action='localUserGroup' includeParams='none' />?operation="+operation+"&jsonMode=true" 
				+ "&parentDomID=" + schedulerSelectId
	 			+ "&ignore="+new Date().getTime();
			}else if(operation == 'editSchedule'){
				var url = "<s:url action='localUserGroup' includeParams='none' />?operation="+operation+"&jsonMode=true"
				+ "&schedulerId="+document.forms[formName].schedulerId.value 
				+ "&parentDomID=" + schedulerSelectId
	 			+ "&ignore="+new Date().getTime();
			}
			openIFrameDialog(800, 450, url);
		</s:if>
		<s:else>
			if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
			
			var userType = document.getElementById("userTypeRadio1");
			if(!userType.checked){
				document.getElementById("voiceDevice").checked = false;
			}
			
		    document.forms[formName].submit();
		</s:else>
		
	}
}

function validate(operation) {
	if(operation == "editSchedule"){
		var value = hm.util.validateListSelection(formName + "_schedulerId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].schedulerId.value = value;
		}
	}
	if('<%=Navigation.L2_FEATURE_LOCAL_USER_GROUP%>' == operation 
		|| operation == 'cancel<s:property value="lstForward"/>'
		|| operation == 'newSchedule' || operation =='editSchedule') {
		if (operation == 'cancel<s:property value="lstForward"/>'){
			document.getElementById(formName + "_dataSource_pskLength").value=8;
			document.getElementById("user").value=12;
			document.getElementById("vlan").value=12;
			document.getElementById("time").value=12;
		}
		
		return true;
	}
	var name = document.getElementById(formName + "_dataSource_groupName");
	var message = hm.util.validateSsid(name.value, '<s:text name="config.localUserGroup.groupName" />');
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	if (!checkNumbers()) {
		return false;
	}
	
	if (!checkUserNamePerfix()){
		return false;
	}
	
	if (!checkPskSecret()) {
		return false;
	}
	
	if (!checkPskLocation()){
		return false;
	}
	
	if(!checkPskLength()){
		return false;
	}
	
	if (!checkPskPeriod()){
		return false;
	}
	
	if (!checkBulkPskValue()){
		return false;
	}
	
	return true;
}

function checkBulkPskValue() {
	if (document.getElementById("userTypeRadio2").checked) {
		if (document.getElementById(formName + "_dataSource_validTimeType").value==2){
			if (Get(formName + "_dataSource_blnBulkType").checked) {
			 	var beginDate=document.getElementById("startTimeBulk");
				if (beginDate.value=='') {
					hm.util.reportFieldError(beginDate, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.bulkStartTime" /></s:param></s:text>');
					showHideOption(1);
					beginDate.focus();
					return false;
				}
				
				var bulkLifeDay = document.getElementById(formName + "_lifeTimeDay");
				var message = hm.util.validateIntegerRange(bulkLifeDay.value, '<s:text name="config.localUserGroup.lifeTime" />',0,365);
		      	if (message != null) {
		            hm.util.reportFieldError(bulkLifeDay, message);
		            showHideOption(1);
		           	bulkLifeDay.focus();
		            return false;
		      	}
		      	
		      	if (bulkLifeDay.value==0 && Get(formName + "_lifeTimeHour").value==0 && Get(formName + "_lifeTimeMin").value==0) {
		      		hm.util.reportFieldError(bulkLifeDay, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.lifeTime" /></s:param></s:text>');
		      		showHideOption(1);
		           	bulkLifeDay.focus();
		           	return false;
		      	}
				
				var bulkintervalday = document.getElementById(formName + "_dataSource_intervalDay");
				var message = hm.util.validateIntegerRange(bulkintervalday.value, '<s:text name="config.localUserGroup.bulkInterval" />',0,365);
		      	if (message != null) {
		            hm.util.reportFieldError(bulkintervalday, message);
		            showHideOption(1);
		           	bulkintervalday.focus();
		            return false;
		      	}
		      	
		      	if (bulkintervalday.value==0 && Get(formName + "_dataSource_intervalHour").value==0 && Get(formName + "_dataSource_intervalMin").value==0) {
		      		hm.util.reportFieldError(bulkintervalday, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.bulkInterval" /></s:param></s:text>');
		      		showHideOption(1);
		           	bulkintervalday.focus();
		           	return false;
		      	}
				
				
				var bulkuserNumber = document.getElementById(formName + "_dataSource_bulkNumber");
				var message = hm.util.validateIntegerRange(bulkuserNumber.value, '<s:text name="config.localUserGroup.bulkNumber" />',1,9999);
		      	if (message != null) {
		            hm.util.reportFieldError(bulkuserNumber, message);
		            showHideOption(1);
		           	bulkuserNumber.focus();
		            return false;
		      	}
		      	
		      	var bulkindexRange = document.getElementById(formName + "_dataSource_indexRange");
				var message = hm.util.validateIntegerRange(bulkindexRange.value, '<s:text name="config.localUserGroup.indexonetime" />',1,9999);
		      	if (message != null) {
		            hm.util.reportFieldError(bulkindexRange, message);
		            showHideOption(1);
		           	bulkindexRange.focus();
		            return false;
		      	}
			}
		}
	}
	return true;
}

function checkNumbers() {
	var userId = document.getElementById("user");
	var vlan = document.getElementById("vlan");
	var time = document.getElementById("time");
	
	if(userId.value.length > 0)
	{
		if(userId.value.length > 1)
		{
			if(userId.value.substring(0,1) == '0')
			{
				hm.util.reportFieldError(userId, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.profileId" /></s:param></s:text>');
				userId.focus();
				return false;
			}
		}
		var message = hm.util.validateIntegerRange(userId.value, '<s:text name="config.localUserGroup.profileId" />',0,4095);
      	if (message != null) {
            hm.util.reportFieldError(userId, message);
           	userId.focus();
            return false;
      	}
// 	} else {
// 	    hm.util.reportFieldError(userId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.profileId" /></s:param></s:text>');
//	    userId.focus();
//	    return false;
	}
	
	if(vlan.value.length > 0)
	{
		if(vlan.value.length > 1)
		{
			if(vlan.value.substring(0,1) == '0')
			{
				hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.vlanId" /></s:param></s:text>');
				vlan.focus();
				return false;
			}
		}
		var message = hm.util.validateIntegerRange(vlan.value, '<s:text name="config.localUserGroup.vlanId" />',1,4094);
      	if (message != null) {
            hm.util.reportFieldError(vlan, message);
           	vlan.focus();
            return false;
      	}
//	} else {
//	    hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.vlanId" /></s:param></s:text>');
//	    vlan.focus();
//	    return false;
	}
	
	if(time.value.length > 0)
	{
		if(time.value.length > 1)
		{
			if(time.value.substring(0,1) == '0')
			{
				hm.util.reportFieldError(time, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
				time.focus();
				return false;
			}
		}
		if (time.value.length == 1) {
			if(time.value!= '0')
			{
				hm.util.reportFieldError(time, '<s:text name="error.formatInvalid"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
				time.focus();
				return false;
			}
		
		} else {
			var message = hm.util.validateIntegerRange(time.value, '<s:text name="config.localUserGroup.reauthTime" />',600,86400);
	      	if (message != null) {
	            hm.util.reportFieldError(time, message);
	           	time.focus();
	            return false;
	      	}
      	}
	} else {
	    hm.util.reportFieldError(time, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.reauthTime" /></s:param></s:text>');
	    time.focus();
	    return false;
	}
	return true;
}

function checkSaveFlash(){
	if (!document.getElementById("userTypeRadio1").checked){
		if (document.getElementById("credentialTypeRadio2").checked){
			hm.util.reportFieldError(document.getElementById("credentialTypeRadio1"), 'Private PSK users only support save credentials to flash');
	    	document.getElementById("credentialTypeRadio1").focus();
			return false;
		}
	}
	return true;
}

function checkUserNamePerfix(){
	if (document.getElementById("userTypeRadio2").checked) {
		var userNamePerfix = document.getElementById(formName + "_dataSource_userNamePrefix");
		if (userNamePerfix.value.length ==0) {
		    hm.util.reportFieldError(userNamePerfix, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.userNamePrefix" /></s:param></s:text>');
		    userNamePerfix.focus();
		    return false;
		}
		var message = hm.util.validatePskUserName(userNamePerfix.value, '<s:text name="config.localUserGroup.userNamePrefix" />');
	   	if (message != null) {
	   		hm.util.reportFieldError(userNamePerfix, message);
	       	userNamePerfix.focus();
	       	return false;
	   	}
	}
	return true;
}

function checkPskSecret(){
	if (document.getElementById("userTypeRadio2").checked) {
		var pskSecret = document.getElementById(formName + "_dataSource_pskSecret");
		if (pskSecret.value.length ==0) {
		    hm.util.reportFieldError(pskSecret, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.secret" /></s:param></s:text>');
		    pskSecret.focus();
		    return false;
		}
		
		var message = hm.util.validatePassword(pskSecret.value, '<s:text name="config.localUserGroup.secret" />');
		if (message != null) {
		   	hm.util.reportFieldError(pskSecret, message);
		    pskSecret.focus();
	    	return false;
		}
	}
	return true;
}

function checkPskLocation(){
	if (document.getElementById("userTypeRadio2").checked) {
		var pskLocation = document.getElementById(formName + "_dataSource_pskLocation");
		if (pskLocation.value.length>0){
			var message = hm.util.validateStringWithBlank(pskLocation.value, '<s:text name="config.localUserGroup.loaction" />');
		   	if (message != null) {
		   		hm.util.reportFieldError(pskLocation, message);
		       	pskLocation.focus();
		       	showHideOption(1);
		       	return false;
		   	}
	   	}
	}
	
	return true;
}

function checkPskLength(){
	if (document.getElementById("userTypeRadio2").checked) {
		var pskLength = document.getElementById(formName + "_dataSource_pskLength");
		if (pskLength.value.length ==0) {
		    hm.util.reportFieldError(pskLength, '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.pskLength" /></s:param></s:text>');
		    pskLength.focus();
		    showHideOption(1);
		    return false;
		}
		
		var message = hm.util.validateIntegerRange(pskLength.value, '<s:text name="config.localUserGroup.pskLength" />',8,63);
      	if (message != null) {
            hm.util.reportFieldError(pskLength, message);
            showHideOption(1);
           	pskLength.focus();
            return false;
      	}
		if (document.getElementById(formName + "_dataSource_pskGenerateMethod").value==2){
	    	  var userLength = document.getElementById(formName + "_dataSource_userNamePrefix").value.trim().length + 4;
	          var passwordLength = document.getElementById(formName + "_dataSource_pskLength").value;
	          var contactLength = document.getElementById(formName + "_dataSource_concatenateString").value.trim().length;
	          var pskAllLength = parseInt(userLength) + parseInt(passwordLength) + parseInt(contactLength);
	          if (parseInt(pskAllLength) >63) {
		          hm.util.reportFieldError(document.getElementById(formName + "_dataSource_pskLength"), "The length of User+String+Password is " + pskAllLength +". It cannot be more than 63.");
		          showHideOption(1);
		          document.getElementById(formName + "_dataSource_pskLength").focus();
		          return false;
	          }
	    }
	}

	return true;
      }


function checkPskPeriod(){
	if (!document.getElementById("userTypeRadio1").checked) {
		if (document.getElementById(formName + "_dataSource_validTimeType").value==1){
			return validateDateTime();
		}
		if (document.getElementById(formName + "_dataSource_validTimeType").value==2){
			return validSchedule();
		}
	}
	return true;
}

function validateDateTime() {
	if (document.getElementById("showTimeDiv").style.display!="none") {
	   	 var beginDate=document.getElementById("startTime");
	   	 var endDate=document.getElementById("endTime");
	   	 if (beginDate.value!='' && endDate.value!='') {
		   	 var hourObj=document.getElementById("startHour");
		   	 var minuObj=document.getElementById("startMin");
		   	 var begin_date_time=beginDate.value+" "+hourObj.value+":"+minuObj.value;

		   	 var endDate=document.getElementById("endTime");
		   	 hourObj=document.getElementById("endHour");
		   	 minuObj=document.getElementById("endMin");
			 var end_date_time=endDate.value+" "+hourObj.value+":"+minuObj.value;
			 if(hm.util.compareDatetime(end_date_time,begin_date_time)!="1"){
			     hm.util.reportFieldError(beginDate, '<s:text name="error.date.compare.endDateLower"><s:param><s:text name="config.localUserGroup.effectiveStartTime" /></s:param></s:text>');
			     showHideOption(1);
			     beginDate.focus();
			     return false;
			 }
		 } else if (beginDate.value=='' && endDate.value=='') {
		  	hm.util.reportFieldError(beginDate, 'At least one time should be set.');
		 	showHideOption(1);
			beginDate.focus();
			return false;
		 }
	}

    return true;
}

function validSchedule() {
	if (document.getElementById("showScheduleDiv").style.display!="none") {
		if (document.getElementById(formName + "_schedulerId").value<0) {
          hm.util.reportFieldError(document.getElementById(formName + "_schedulerId"), '<s:text name="error.requiredField"><s:param><s:text name="config.localUserGroup.schedule" /></s:param></s:text>');
          showHideOption(1);
          document.getElementById(formName + "_schedulerId").focus();
          return false;
		}
	}
    return true;
}

function enableStartTime(checked) {
	if (checked) {
		document.getElementById("startdatefields").style.display="block";
		document.getElementById("startTime").value="";
		document.getElementById("startHour").disabled=false;
		document.getElementById("startHour").value=0;
		document.getElementById("startMin").disabled=false;
		document.getElementById("startMin").value=0;
	} else {
		document.getElementById("startdatefields").style.display="none";
		document.getElementById("startTime").value="";
		document.getElementById("startHour").disabled=true;
		document.getElementById("startHour").value=0;
		document.getElementById("startMin").disabled=true;
		document.getElementById("startMin").value=0;
	}
}

function enableEndTime(checked) {
	if (checked) {
		document.getElementById("enddatefields").style.display="block";
		document.getElementById("endTime").value="";
		document.getElementById("endHour").disabled=false;
		document.getElementById("endHour").value=0;
		document.getElementById("endMin").disabled=false;
		document.getElementById("endMin").value=0;
	} else {
		document.getElementById("enddatefields").style.display="none";
		document.getElementById("endTime").value="";
		document.getElementById("endHour").disabled=true;
		document.getElementById("endHour").value=0;
		document.getElementById("endMin").disabled=true;
		document.getElementById("endMin").value=0;
	}
}

function showTimeSchedule(value){
	if (value == 1) {
		document.getElementById("showTimeDiv").style.display="block";
		document.getElementById("showScheduleDiv").style.display="none";
		Get("showBulkEnabledDiv").style.display="none";
	} else if(value == 2){
		document.getElementById("showTimeDiv").style.display="none";
		document.getElementById("showScheduleDiv").style.display="block";
		if (document.getElementById("userTypeRadio2").checked) {
			Get("showBulkEnabledDiv").style.display="";
		} else {
			Get("showBulkEnabledDiv").style.display="none";
		}
	} else {
		document.getElementById("showTimeDiv").style.display="none";
		document.getElementById("showScheduleDiv").style.display="none";
		Get("showBulkEnabledDiv").style.display="none";

	}
	initBulkSessionValue();
}

function initBulkSessionValue(){
	Get(formName + "_dataSource_blnBulkType").checked=false;
	Get("showBulkTypeDiv").style.display="none";
	Get(formName + "_dataSource_bulkNumber").value=1;
	Get(formName + "_dataSource_indexRange").value=10;
	Get(formName + "_dataSource_intervalDay").value=0;
	Get(formName + "_lifeTimeDay").value=0;
	Get("startTimeBulk").value="";
	Get("startHourBulk").selectedIndex=0;
	Get("startMinBulk").selectedIndex=0;
	Get(formName + "_lifeTimeHour").selectedIndex=0;
	Get(formName + "_lifeTimeMin").selectedIndex=0;
	Get(formName + "_dataSource_intervalMin").selectedIndex=0;
	Get(formName + "_dataSource_intervalHour").selectedIndex=0;
}

function showHideOption(value){
	if (value==1) {
		document.getElementById("showOptionDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="block";
		document.getElementById("hideDetailOptionDiv").style.display="block";
	}
	if (value==2) {
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideDetailOptionDiv").style.display="none";
	}
}

function changeUserType(userType){
	if (userType==1) {
		document.getElementById("showAutoCreateDiv").style.display="none";
		document.getElementById("showOptionDiv").style.display="none";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideDetailOptionDiv").style.display="none";
		document.getElementById("showAutoCreateLocationDiv").style.display="none";
		document.getElementById("showRadioDramDiv").style.display="block";
		document.getElementById("credentialTypeRadio1").checked=true;
		document.getElementById("voice_device").style.display="block";
		
		Get("showBulkEnabledDiv").style.display="none";
		initBulkSessionValue();
	}
	if (userType==2) {
		document.getElementById("showAutoCreateDiv").style.display="block";
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideDetailOptionDiv").style.display="none";
		document.getElementById("showAutoCreateLocationDiv").style.display="block";
		document.getElementById("showRadioDramDiv").style.display="none";
		document.getElementById("credentialTypeRadio1").checked=true;
		document.getElementById("voice_device").style.display="none";
		var validTimeType = document.getElementById(formName + "_dataSource_validTimeType");
		if (validTimeType.length<3) {
			validTimeType.length=3;
			validTimeType.options[2].text="Recurring";
			validTimeType.options[2].value=2;
		}
		if (document.getElementById(formName + "_dataSource_validTimeType").value==2) {
			Get("showBulkEnabledDiv").style.display="";
			initBulkSessionValue();
		} else {
			Get("showBulkEnabledDiv").style.display="none";
			initBulkSessionValue();
		}
	}
	if (userType==3) {
		document.getElementById("showRadioDramDiv").style.display="none";
		document.getElementById("credentialTypeRadio1").checked=true;
		document.getElementById("showAutoCreateDiv").style.display="none";
		document.getElementById("showOptionDiv").style.display="block";
		document.getElementById("hideOptionDiv").style.display="none";
		document.getElementById("hideDetailOptionDiv").style.display="none";
		document.getElementById("showAutoCreateLocationDiv").style.display="none";
		document.getElementById("voice_device").style.display="none";
		
		Get("showBulkEnabledDiv").style.display="none";
		initBulkSessionValue();
		
		var validTimeType = document.getElementById(formName + "_dataSource_validTimeType");
		if (validTimeType.value==2) {
			validTimeType.options[0].selected=true;
			validTimeType.value=validTimeType.options[0].value;
			validTimeType.text=validTimeType.options[0].text
			showTimeSchedule(0);
		}
		if (validTimeType.length==3) {
			validTimeType.length=2;
		}
	}
}
function changePskGenerateMethod(value){
	if (value==1){
		document.getElementById("showConcatenateStringDiv").style.display="none";
	} else {
		document.getElementById("showConcatenateStringDiv").style.display="block";
	}
}
function pskFormatCheckChange() {
	var chkDig=document.getElementById(formName + "_dataSource_blnCharDigits");
	var chkLet=document.getElementById(formName + "_dataSource_blnCharLetters");
	var chkSpe=document.getElementById(formName + "_dataSource_blnCharSpecial");
	if (!chkDig.checked && !chkLet.checked && !chkSpe.checked) {
		chkLet.checked = true;
	}
}

function displayNoteMessage(value) {
	if (value==2) {
		document.getElementById("showNoteMessageDiv").style.display="block";
	} else {
		document.getElementById("showNoteMessageDiv").style.display="none";
	}
}

function enableBulkType(checked) {
	if (checked) {
		Get("showBulkTypeDiv").style.display="";
		Get("showScheduleDiv").style.display="none";
	} else {
		Get("showBulkTypeDiv").style.display="none";
		Get("showScheduleDiv").style.display="";
	}
	
}

function generateSecret(){
	var str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!#$%&'()*+,-./:;<=>@[]^_`{|}~";
	var strLen = str.length;
	var buf = new Array(64);
	for (var i = 0; i < 64; i++) {
		buf[i] = str.charAt(Math.floor(Math.random()*strLen));
	}
	document.getElementById(formName + "_dataSource_pskSecret").value=buf.join("");
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="localUserGroup" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

function validateLocalUserGroupForJson(operation) {
	if (!validate(operation)) {
		return false;
	}
	return true;
}

function onLoadForJson(){
	onLoadPage();
	<s:if test="%{updateDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
}

<s:if test="%{jsonMode==true && !parentIframeOpenFlg}">
	window.setTimeout("onLoadForJson()", 100);
</s:if>
</script>
<div id="content"><s:form action="localUserGroup" id="localUserGroup">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="titleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-Local_User_Groups-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<td class="dialogPanelTitle">
							<s:if test="%{dataSource.id == null}">
							<s:text name="config.title.localUserGroup.new"/>
							</s:if>
							<s:else>
							<s:text name="config.title.localUserGroup.edit"/></s:else>
						</td>
					</tr>
				</table>
				</td>
				<td align="right">
				<s:if test="%{!parentIframeOpenFlg}">
				<!-- TODO -->
				</s:if>
				<s:else>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode==false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_LOCAL_USER_GROUP%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="800px">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
			</s:else>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td> 
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><label><s:text
									name="config.localUserGroup.groupName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="24"
									name="dataSource.groupName" maxlength="%{groupNameLength}"
									disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'ssid');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.localUserGroup.description" /></label></td>
								<td><s:textfield size="48"
									name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:4px 4px 4px 4px">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="sepLine"><img
									src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td style="padding: 4px 4px 4px 4px;"> 
						<fieldset><legend><s:text name="config.localUserGroup.userType" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td style="display:<s:property value="%{showRADIUS}"/>"><s:radio label="Gender" id="userTypeRadio"
											name="dataSource.userType"
											list="#{1:'RADIUS users'}"
											value="%{dataSource.userType}"
											disabled="%{changeDisabled}"
											onclick="changeUserType(1);" /></td>
									<td style="display:<s:property value="%{showPSK}"/>"><s:radio label="Gender" id="userTypeRadio"
											name="dataSource.userType"
											list="#{2:'Automatically generated private PSK users'}"
											value="%{dataSource.userType}"
											disabled="%{changeDisabled || changeUserTypeFlag}"
											onclick="changeUserType(2);" /></td>
									<td style="display:<s:property value="%{showPSK}"/>"><s:radio label="Gender" id="userTypeRadio"
											name="dataSource.userType"
											list="#{3:'Manually created private PSK users'}"
											value="%{dataSource.userType}"
											disabled="%{changeDisabled || changeUserTypeFlag}"
											onclick="changeUserType(3);" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td> 
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.profileId" /></td>
								<td><s:textfield size="24" name="strOneUserProfileId" id="user" 
									value="%{strOneUserProfileId}" maxlength="4"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.localUserGroup.profileIdRange" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.localUserGroup.vlanId" /></td>
								<td><s:textfield size="24" name="strOneVlanId" id="vlan"
									value="%{strOneVlanId}" maxlength="4"
									title="This VLAN ID overrides the one in the user profile."
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.localUserGroup.vlanIdRange" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text
									name="config.localUserGroup.reauthTime" /></td>
								<td><s:textfield size="24" name="dataSource.reauthTime" id="time"
									value="%{dataSource.reauthTime}" maxlength="5"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.localUserGroup.reauthTimeRange" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding: 0 4px 0 4px;"> 
					<div style="display:<s:property value="%{showRadioDram}"/>" id="showRadioDramDiv">
						<fieldset><legend><s:text name="config.localUserGroup.credential" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td><s:radio label="Gender" id="credentialTypeRadio"
											name="dataSource.credentialType"
											list="#{1:'Save credentials to flash (persistent after reboot)'}"
											value="%{dataSource.credentialType}" /></td>
									<td>
										<s:radio label="Gender" id="credentialTypeRadio"
											name="dataSource.credentialType"
											list="#{2:'Save credentials to DRAM only (not persistent)'}"
											value="%{dataSource.credentialType}" />
										</td>	
								</tr>
							</table>
						</fieldset>
					</div>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%" id="voice_device" 
								style="display:<s:property value="%{hideVoiceDevice}" />">
							<tr>
								<td class="noteInfo"  style="padding:5px 5px 3px 8px" colspan="2">
									<s:text name="config.localUserGroup.voice.device.note" />
								</td>
							</tr>
							<tr>
								<td style="width: 20px; padding: 0px 5px 5px;">
									<s:checkbox id="voiceDevice" name="dataSource.voiceDevice" disabled="%{changeDisabled}" 
												value="%{dataSource.voiceDevice}" />
								</td>
								<td style="padding-bottom:2px">
									<s:text name="config.localUserGroup.voice.device" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td> 
						<div style="display:<s:property value="%{showAutoCreate}"/>" id="showAutoCreateDiv">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.userNamePrefix" /><font color="red"><s:text name="*"/></font></td>
								<td colspan="2"><s:textfield size="24" name="dataSource.userNamePrefix"
									value="%{dataSource.userNamePrefix}" maxlength="28"
									onkeypress="return hm.util.keyPressPermit(event,'pskUserName');" />	
									<s:text name="config.localUserGroup.userNamePrefixRange" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.secret" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield size="24" name="dataSource.pskSecret"
									value="%{dataSource.pskSecret}" maxlength="64"
									onkeypress="return hm.util.keyPressPermit(event,'password');"/>&nbsp;<s:text
									name="config.localUserGroup.secretRange" />
									<input type="button" name="generate" value="Generate"
									class="button"
									onClick="generateSecret();"></td>
							</tr>
						</table>
						</div>
					</td>
				</tr>
				<tr>
					<td> 
						<div style="display:<s:property value="%{showOption}"/>" id="showOptionDiv">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" onclick="showHideOption(1);" style="cursor: pointer"><img
									src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
									alt="Show Option" class="expandImg" style="display: inline"
									 />&nbsp;&nbsp;<s:text name="config.localUserGroup.pskOption"/></td>
							</tr>
						</table>
						</div>
					</td>
				</tr>
				<tr>
					<td> 
						<div style="display:<s:property value="%{hideOption}"/>" id="hideOptionDiv">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" style="cursor: pointer" onclick="showHideOption(2);"><img
									src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
									alt="Hide Option" class="expandImg" style="display: inline"
									 />&nbsp;&nbsp;<s:text name="config.localUserGroup.pskOption"/></td>
							</tr>
						</table>
						</div>
					</td>
				</tr>
				<tr>
					<td style="padding: 4px 4px 4px 30px">
						<div style="display:<s:property value="%{hideOption}"/>" id="hideDetailOptionDiv">
						
						<div style="display:<s:property value="%{showAutoCreate}"/>" id="showAutoCreateLocationDiv">
						<fieldset><legend><s:text name="config.localUserGroup.pskOptionAutoPass" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="138px"><s:text
										name="config.localUserGroup.loaction" /></td>
									<td><s:textfield size="33" name="dataSource.pskLocation"
										value="%{dataSource.pskLocation}" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>&nbsp;<s:text
										name="config.localUserGroup.loactionRange" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text
										name="config.localUserGroup.pskLength" /></td>
									<td><s:textfield size="33" name="dataSource.pskLength"
										value="%{dataSource.pskLength}" maxlength="2"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text
										name="config.localUserGroup.pskLengthRange" /></td>
								</tr>
							</table>
						</fieldset>
						</div>
						
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.pskGenerateMethod" /></td>
								<td><s:select name="dataSource.pskGenerateMethod"
									list="%{enumPskGenerateMethod}" listKey="key" listValue="value"
									value="%{dataSource.pskGenerateMethod}" cssStyle="width: 200px;"
									onchange="changePskGenerateMethod(this.options[this.selectedIndex].value);" /></td>
							</tr>
						</table>
						
						<div style="display:<s:property value="%{showConcatenateString}"/>" id="showConcatenateStringDiv">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.concatenateString" /></td>
								<td><s:textfield size="33" name="dataSource.concatenateString"
									value="%{dataSource.concatenateString}" maxlength="8"
									onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>&nbsp;<s:text
									name="config.localUserGroup.concatenateStringRange" /></td>

							</tr>
						</table>
						</div>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="admin.timeSet.timeZone" />
								</td>
								<td>
									<s:select name="dataSource.timezone" value="%{dataSource.timezone}"
										list="enumTimeZone" listKey="key" listValue="value"
										cssStyle="width: 200px;" />
								</td>
							</tr>
						</table>

						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="150px"><s:text
									name="config.localUserGroup.validTimeType" /></td>
								<td><s:select name="dataSource.validTimeType"
									list="%{enumValidTimeType}" listKey="key" listValue="value"
									value="dataSource.validTimeType" cssStyle="width: 200px;"
									onchange="showTimeSchedule(this.options[this.selectedIndex].value);"/>
								</td>
							</tr>
						</table>
						
						<div style="display:<s:property value="%{showTime}"/>" id="showTimeDiv">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td height="2px" />
							</tr>
							<tr>
								<td width="20px" style="padding-left: 30px"><s:checkbox
									name="blnStartTime"
									value="%{blnStartTime}"
									onclick="enableStartTime(this.checked);"/></td>
								<td width="60px"><s:text
									name="config.localUserGroup.effectiveStartTime" /></td>
								<td width="60px"><s:textfield name="startTime"
									id="startTime" value="%{startTime}" readonly="true"
									size="10" maxlength="10" /></td>
								<td width="15px">
								<div id="startdatefields"></div>
								</td>
								<td width="75px"><s:select name="startHour"
									id="startHour" value="%{startHour}" list="%{lstHours}"
									listKey="key" listValue="value" /></td>
								<td width="90px"><s:select name="startMin"
									id="startMin" value="%{startMin}" list="%{lstMins}"
									listKey="key" listValue="value" /></td>

								<td width="20px"><s:checkbox
									name="blnEndTime"
									value="%{blnEndTime}"
									onclick="enableEndTime(this.checked);"/></td>
								<td width="40px"><s:text
									name="config.localUserGroup.effectiveEndTime" /></td>
								<td width="60px"><s:textfield name="endTime"
									id="endTime" value="%{endTime}" readonly="true"
									size="10" maxlength="10" /></td>
								<td width="15px">
								<div id="enddatefields"></div>
								</td>
								<td width="75px"><s:select name="endHour"
									id="endHour" value="%{endHour}" list="%{lstHours}"
									listKey="key" listValue="value" /></td>
								<td width="210px"><s:select name="endMin"
									id="endMin" value="%{endMin}" list="%{lstMins}"
									listKey="key" listValue="value" /></td>
							</tr>
						</table>
						</div>
						
						<div style="display:<s:property value="%{showSchedule}"/>" id="showScheduleDiv">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="240px"><s:text name="config.localUserGroup.schedule" /></td>
								<td width="200px"><s:select name="schedulerId" list="%{schedulerProfiles}" listKey="id"
									listValue="value" cssStyle="width: 200px;" value="%{schedulerId}" />
								</td>
								<td width="30px">
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newSchedule')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
								</td>
								<td>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('editSchedule')"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
								</td>
							</tr>
						</table>
						</div>
						
						
						<div style = "display:<s:property value="%{showBulkEnable}"/>" id="showBulkEnabledDiv">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td style="padding: 4px 0 4px 6px;"><s:checkbox name="dataSource.blnBulkType" onclick="enableBulkType(this.checked);"></s:checkbox>
								 	<s:text name="config.localUserGroup.enableBulkType" /></td>
							</tr>
						</table>
						
						<div style="padding-left:25px; display:<s:property value="%{showBulkType}"/>" id="showBulkTypeDiv">
						
							<table border="0" cellspacing="0" cellpadding="0" width="100%">	
								<tr>
									<td class="labelT1" width="250px"><s:text
										name="config.localUserGroup.bulkStartTime" /><font color="red"><s:text name="*"/></font></td>
									<td><table border="0" cellspacing="0" cellpadding="0"><tr>	
									<td width="90px"><s:textfield name="startTimeBulk"
										id="startTimeBulk" value="%{startTimeBulk}" readonly="true"
										size="10" maxlength="10" /></td>
									<td width="15px">
									<div id="startdateBulkfields"></div>
									</td>
									<td><s:select name="startHourBulk"
										id="startHourBulk" value="%{startHourBulk}" list="%{lstHours}"
										listKey="key" listValue="value" /></td>
									<td><s:select name="startMinBulk"
										id="startMinBulk" value="%{startMinBulk}" list="%{lstMins}"
										listKey="key" listValue="value" /></td>
									</tr></table></td>
									
								</tr>
								<tr>
									<td class="labelT1" width="250px"><s:text
										name="config.localUserGroup.lifeTime" /><font color="red"><s:text name="*"/></font></td>
									<td><table border="0" cellspacing="0" cellpadding="0"><tr>	
									<td width="185px"><s:textfield name="lifeTimeDay"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										size="10" maxlength="3" />
										<s:text name="config.localUserGroup.lifeTime.range" />
									</td>
									<td><s:select name="lifeTimeHour"
										 	value="%{lifeTimeHour}" list="%{lstHours}"
										listKey="key" listValue="value" />
										<s:select name="lifeTimeMin"
											value="%{lifeTimeMin}" list="%{lstMins}"
										listKey="key" listValue="value" />
									</td></tr></table></td>
								</tr>
								<tr>
									<td class="labelT1" width="250px"><s:text
										name="config.localUserGroup.bulkInterval" /><font color="red"><s:text name="*"/></font></td>
									<td><table border="0" cellspacing="0" cellpadding="0"><tr>	
									<td width="185px"><s:textfield name="dataSource.intervalDay"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										size="10" maxlength="3" />
										<s:text name="config.localUserGroup.bulkInterval.day.range" />
									</td>
									
									<td><s:select name="dataSource.intervalHour"
										 	value="%{dataSource.intervalHour}" list="%{lstHours}"
										listKey="key" listValue="value" />
										<s:select name="dataSource.intervalMin"
											value="%{dataSource.intervalMin}" list="%{lstMins}"
										listKey="key" listValue="value" />
									</td></tr></table></td>
								</tr>
								<tr>
									<td class="labelT1" width="250px"><s:text
										name="config.localUserGroup.bulkNumber" /><font color="red"><s:text name="*"/></font></td>
									<td ><s:textfield name="dataSource.bulkNumber"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										size="10" maxlength="4" />
										<s:text name="config.localUserGroup.bulkNumber.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="250px"><s:text
										name="config.localUserGroup.indexonetime" /><font color="red"><s:text name="*"/></font></td>
									<td ><s:textfield name="dataSource.indexRange"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										size="10" maxlength="4" />
										<s:text name="config.localUser.bulk.userNumberRange" /></td>
								</tr>
								
							</table>
						
						</div>
						</div>
						
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="450px" colspan="3">
									<s:text name="config.localUserGroup.pskFormat.character" />
								</td>
							</tr>
							<tr>
								<td style="padding-left: 10px">
									<s:checkbox name="dataSource.blnCharLetters" onclick="pskFormatCheckChange();"></s:checkbox>
									<s:text name="config.localUserGroup.pskFormat.character.letters" />
								</td>
								<td>
									<s:checkbox name="dataSource.blnCharDigits" onclick="pskFormatCheckChange();"></s:checkbox>
									<s:text name="config.localUserGroup.pskFormat.character.digits" />
								</td>
								<td>
									<s:checkbox name="dataSource.blnCharSpecial" onclick="pskFormatCheckChange();"></s:checkbox>
									<s:text name="config.localUserGroup.pskFormat.character.special" />
								</td>
							</tr>
						</table>
						
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td height="5px" colspan="2">
								</td>
							</tr>
							<tr>
								<td width="150px" style="padding-left: 10px"><s:text name="config.localUserGroup.pskFormat.restrictions" /></td>
								<td>
									<s:select name="dataSource.personPskCombo"
									id="personPskCombo" value="dataSource.personPskCombo" list="%{lstPersonPskCombo}"
									listKey="key" listValue="value" cssStyle="width: 350px;"
									onchange="displayNoteMessage(this.options[this.selectedIndex].value);"/></td>
							</tr>
							<tr>
								<td height="3px" colspan="2">
								</td>
							</tr>
						</table>
						<div style="display:<s:property value="%{showNoteMessage}"/>" id="showNoteMessageDiv">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td style="padding-left:15px" class="noteInfo" colspan="2">
										<s:text name="config.localUserGroup.pskFormat.charTypeRule.note" />
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
	</table>
</s:form></div>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
    <script>
       setCurrentHelpLinkUrl('<s:property value="helpLink" />');
    </script>
</s:if>

