<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.teacherView.TvClass"%>
<script>
var formName = 'tvClass';  
function onLoadPage() {
	if (Get(formName + "_dataSource_className").disabled == false) {
		Get(formName + "_dataSource_className").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == "newTeacher" && <s:property value="createFromVHM"/>){
		warnDialog.cfg.setProperty('text', '<s:text name="error.teacherView.teacher.vhm.create"/>');
		warnDialog.show();
		return false;
	}
	
	if(operation == "editCart"){
		var value = hm.util.validateListSelection(formName + "_cartId");
		if(value < 0){
			return false
		}else{
			document.forms[formName].cartId.value = value;
		}
	}

    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_className");
		var message = hm.util.validateName(name.value, '<s:text name="config.tv.className" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    	
    	var subject = document.getElementById(formName + "_dataSource_subject");
		message = validateString(subject.value, '<s:text name="config.tv.subject" />');
    	if (message != null) {
    		hm.util.reportFieldError(subject, message);
        	subject.focus();
        	return false;
    	}
    	
    	//var teacher = document.getElementById(formName + "_dataSource_teacherId");
    	var teacher = document.getElementById("teacherId");
    	if (teacher.value=='' || teacher.value=='None available'){
			hm.util.reportFieldError(teacher, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.teacher" /></s:param></s:text>');
        	teacher.focus();
        	return false;
    	}
    	
    	if (Get(formName + "_dataSource_rosterType").value==<%=TvClass.TV_ROSTER_TYPE_COMPUTERCART%>){
	    	var cart = document.getElementById(formName + "_cartId");
	    	if (cart.value=='' || cart.value<0){
				hm.util.reportFieldError(cart, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.cart" /></s:param></s:text>');
	        	return false;
	    	}
    	}
	}
	
	var table = document.getElementById("checkAll");
    if (operation == 'addSchedule') {
    	if (Get(formName + "_addMon").checked==false && Get(formName + "_addTue").checked==false &&
    		Get(formName + "_addWed").checked==false && Get(formName + "_addThu").checked==false &&
    		Get(formName + "_addFri").checked==false && Get(formName + "_addSat").checked==false &&
    		Get(formName + "_addSun").checked==false){
    		
    		hm.util.reportFieldError(Get(formName + "_addSun"), '<s:text name="error.requiredField"><s:param><s:text name="config.tv.weekDay" /></s:param></s:text>');
	        Get(formName + "_addSun").focus();
	        return false;
    	}
    	if (Get(formName + "_addSHour").value>Get(formName + "_addEHour").value) {
    	    hm.util.reportFieldError(Get(formName + "_addSHour"), '<s:text name="error.notLargerThan"><s:param><s:text name="config.tv.startTime" /></s:param><s:param><s:text name="config.tv.endTime" /></s:param></s:text>');
	        Get(formName + "_addSHour").focus();
	        return false;
    	} else {
    		if (Get(formName + "_addSHour").value==Get(formName + "_addEHour").value){
	    		if (Get(formName + "_addSMin").value>=Get(formName + "_addEMin").value){
		    		hm.util.reportFieldError(Get(formName + "_addSMin"), '<s:text name="error.notLargerThan"><s:param><s:text name="config.tv.startTime" /></s:param><s:param><s:text name="config.tv.endTime" /></s:param></s:text>');
			        Get(formName + "_addSMin").focus();
			        return false;
	    		}
	    	}
    	}
    }

    if (operation == 'removeSchedule' || operation == 'removeScheduleNone') {
		var cbs = document.getElementsByName('scheduleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param>schedule</s:param></s:text>');
			return false;
		}
	}
	
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
    	var macIds = document.getElementsByName('scheduleIndices');
		if(macIds.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>schedule</s:param></s:text>');
       		table.focus();
       		return false;
		}
    }

	return true;
}

function validateString(value, label) {
	if (value.length == 0 || value.trim().length == 0) {
		return label + " is a required field.";
	}
	
	for(var i = 0; i< value.length; i++){
		var charCode = value.charCodeAt(i);
		
		// do not accept character: +, ', \
		if(charCode == 39 
			|| charCode == 43
			|| charCode == 92){
			return label + " cannot contain '" + String.fromCharCode(charCode) + "'.";
		}
	}	
}

function changeRosterType(value){
	if (value==<%=TvClass.TV_ROSTER_TYPE_STUDENT%>){
		Get("hideCartDiv").style.display="none";
	} else {
		Get("hideCartDiv").style.display="";
	}
}

function clickMonFri(value){
	if (value) {
		Get(formName + "_addMon").checked=true;
		Get(formName + "_addTue").checked=true;
		Get(formName + "_addWed").checked=true;
		Get(formName + "_addThu").checked=true;
		Get(formName + "_addFri").checked=true;
	} else {
		Get(formName + "_addMon").checked=false;
		Get(formName + "_addTue").checked=false;
		Get(formName + "_addWed").checked=false;
		Get(formName + "_addThu").checked=false;
		Get(formName + "_addFri").checked=false;
	}
}

function clickMonToFri(value){
	if(!value){
		Get(formName + "_addMonFri").checked=false;
	}else{
		if(Get(formName + "_addMon").checked && 
		   Get(formName + "_addTue").checked &&
		   Get(formName + "_addWed").checked &&
		   Get(formName + "_addThu").checked &&
		   Get(formName + "_addFri").checked){
			Get(formName + "_addMonFri").checked=true;
		}else{
			Get(formName + "_addMonFri").checked=false;
		}
	}
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');

}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('scheduleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function toggleAllCheckBox(cb){
	var allCheckBox = document.getElementById('checkAll');
	if(cb.checked == false){
		allCheckBox.checked = false;
	}
}

/*
 * add for create teacher UI, start
 */
var newTeacherPanel = null;
YAHOO.util.Event.onDOMReady(preparePanels4NewTeacher);
function preparePanels4NewTeacher() {
	var div = document.getElementById('newTeacherPanelId');
	newTeacherPanel = new YAHOO.widget.Panel(div, {
		width:"750px",
		underlay: "none",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:999
		});
	newTeacherPanel.render();
	div.style.display = "";
}

function clearNewTeacherWindow() {
	var emailAddr = document.getElementById("emailAddress");
	if (emailAddr != null) {
		emailAddr.value = "";
	}
	var userName = document.getElementById("userName");
	if (userName != null) {
		userName.value = "";
	}
	var description = document.getElementById("description");
	if (description != null) {
		description.value = "";
	}
	var i18n = document.getElementById(formName + "_i18n");
	if (i18n != null) {
		$("#" + formName + "_i18n option[value='0']").attr("selected", "selected");
	}
	var timeZone = document.getElementById(formName + "_timeZone");
	var loginUsersTimeZone = document.getElementById("loginUsersTimeZone"); // int value
	if (timeZone != null && loginUsersTimeZone != null) {
		$("#" + formName + "_timeZone option[value='"+ loginUsersTimeZone.value +"']").attr("selected", "selected");
	} else {
		$("#" + formName + "_timeZone option:first").attr("selected", "selected");
	}
	var dateFormat = document.getElementById(formName + "_dateFormat");
	if (dateFormat != null) {
		$("#" + formName + "_dateFormat option:first").attr("selected", "selected");
	}
	var timeFormat0 = document.getElementById(formName + "_timeFormat0");
	if (timeFormat0 != null) {
		timeFormat0.checked = 'checked';
	}
}

function showNewTeacherPanel(){
	if(null != newTeacherPanel){
		// clear input value on new teacher popup window
		clearNewTeacherWindow();
		
		//newTeacherPanel.cfg.setProperty("context", ["globalSettingsId", "tr", "tr"]);
		newTeacherPanel.center();
		newTeacherPanel.cfg.setProperty('visible', true);
	}
}

function hideNewTeacherPanel(){
	if(null != newTeacherPanel){
		//set_innerHTML("newTeacherPanelContentId", "");
		newTeacherPanel.cfg.setProperty('visible', false);
	}
}

var doCheckUserEmailSuccess = function(o)  {
	eval("var data = " + o.responseText);
	if (data) {
		if (data.exist) {
			// user already exist, show error msg
			var emailAddr = document.getElementById("emailAddress");
			hm.util.reportFieldError(emailAddr, data.msg);
			emailAddr.focus();
		}
	}
};

var doCheckUserEmailFailed = function(o) {
// 	alert("failed.");
};

var doCheckUserEmailCallback = {
	success : doCheckUserEmailSuccess,
	failure : doCheckUserEmailFailed
};

// check if can create user with input email
function doCheckUserEmail() {
	var emailAddr = document.getElementById("emailAddress");
	if (emailAddr.value.length > 0) {
		if (!hm.util.validateEmail(emailAddr.value)) {
			hm.util.reportFieldError(emailAddr, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.emailaddress" /></s:param></s:text>');
			emailAddr.focus();
			return;
		}
		
		var url = '<s:url action="tvClass" includeParams="none"/>?operation=doCheckUserEmail'
				+ '&emailAddress=' + encodeURIComponent(emailAddr.value)
				+ '&ignore=' + new Date().getTime();
		//debug('url=' + url);
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, doCheckUserEmailCallback);	
	}
}

var doCreateTeacherSuccess = function(o)  {
	eval("var data = " + o.responseText);
	if (data) {
		if (data.success) {
			// create user successfully, add new teacher to teacher list and show success msg
			var teacher = document.getElementById("teacherId");
			teacher.options.add(new Option(data.emailAddress, data.emailAddress)); // add new techer to list
			teacher.options[teacher.options.length - 1].selected = 'selected'; // set new teacher be selected
			
			// if first is option is 'None available', remove it
			var nonavaliable = '<s:text name="config.optionsTransfer.none"/>';
			//debug('value of non ava=' + nonavaliable);
			$("#teacherId option[value='"+ nonavaliable +"']").remove();
			
			//$("#teacherId option[value='"+ data.emailAddress +"']").attr("selected", "selected");
			//$("#teacherId option:last).attr("selected", "selected");
			hideNewTeacherPanel();
			showInfoDialog(data.msg);
		} else {
			warnDialog.cfg.setProperty('text', data.msg);
			warnDialog.show();
		}
	}
};

var doCreateTeacherFailed = function(o) {
// 	alert("failed.");
};

var doCreateTeacherCallback = {
	success : doCreateTeacherSuccess,
	failure : doCreateTeacherFailed
};

function doCreateTeacher() {

	var emailAddr = document.getElementById("emailAddress");
	if (emailAddr.value.length == 0) {
        hm.util.reportFieldError(emailAddr, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.emailaddress" /></s:param></s:text>');
        emailAddr.focus();
        return false;
    } else if (!hm.util.validateEmail(emailAddr.value)) {
		hm.util.reportFieldError(emailAddr, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.emailaddress" /></s:param></s:text>');
		emailAddr.focus();
		return false;
	}
	
	var userName = document.getElementById("userName");
	if (userName.value.length == 0) {
        hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.name" /></s:param></s:text>');
        userName.focus();
        return false;
    }
	
	document.forms[formName].operation.value = "doCreateTeacher";
	YAHOO.util.Connect.setForm(document.getElementById('tvClass'));
	var url = "<s:url action="tvClass" includeParams='none'/>";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, doCreateTeacherCallback, null);
}

var doRemoveTeacherSuccess = function(o)  {
	//debug('doRemoveTeacherSuccess, 0.result=' + o.responseText);
	eval("var data = " + o.responseText);
	if (data) {
		if (data.success) {
			// remove user successfully, remove such teacher from teacher list and show success msg
			$("#teacherId option[value='"+ data.emailAddress +"']").remove();
			
			if ($('#teacherId option').length == 0) {
				// if all options be removed, add option 'None available' to list
				var nonavaliable = '<s:text name="config.optionsTransfer.none"/>';
				$("#teacherId").append("<option value='" + nonavaliable + "'>" + nonavaliable +  "</option>");
				$('#teacherId option:last').attr('selected','selected'); 
				//$("#teacherId").options.add(new Option(nonavaliable, nonavaliable)); // add option 'None available' to list
				//$("#teacherId").options[teacher.options.length - 1].selected = 'selected'; // set 'None available' be selected
			}
			
			showInfoDialog(data.msg);
		} else {
			warnDialog.cfg.setProperty('text', data.msg);
			warnDialog.show();
		}
	}
};

var doRemoveTeacherFailed = function(o) {
// 	alert("failed.");
};

var doRemoveTeacherCallback = {
	success : doRemoveTeacherSuccess,
	failure : doRemoveTeacherFailed
};

var handleNoConfirmDialog = function() {
    this.hide();
    return;
};

var handleYesConfirmDialog = function() {
    this.hide();
	var teacher = document.getElementById("teacherId");
	var url = '<s:url action="tvClass" includeParams="none"/>?operation=doRemoveTeacher'
			+ '&selectedTeacher=' + encodeURIComponent(teacher.options[teacher.selectedIndex].value)
			+ '&ignore=' + new Date().getTime();
	//debug('url=' + url);
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, doRemoveTeacherCallback);	
};

function removeTeacher() {
	
	var teacher = document.getElementById("teacherId");
	if (teacher.value=='' || teacher.value=='None available'){
		hm.util.reportFieldError(teacher, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.teacher" /></s:param></s:text>');
    	teacher.focus();
    	return;
	}
	
	confirmDialog.cfg.setProperty('buttons', [ { text:"Yes", handler: handleYesConfirmDialog, isDefault:true },
	                                           { text:"&nbsp;No&nbsp;", handler: handleNoConfirmDialog } ]);
	confirmDialog.cfg.setProperty('text', "<s:text name='info.teacherView.remove.teacher.confirm.msg'></s:text>");
	confirmDialog.show();
}

var successNotesTimeoutId;
function delaySuccessHideNotes(seconds, elId) {
	successNotesTimeoutId = setTimeout('hideSuccessNotes("'+ elId +'")', seconds * 5000);  // seconds
}
function hideSuccessNotes(elId) {
	hm.util.wipeOut(elId, 800);
}

var debug = function(msg) {
    if(window.console && console.debug) {
        if(typeof msg == 'string' || typeof msg == 'number' || typeof msg == 'boolean') {
            console.debug(msg);
        } else {
            console.dir(msg);
        }
    }
};
/*
 * add for create teacher UI, end
 */

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="tvClass" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}
</script>
<div id="content">
<s:form action="tvClass">
	<s:hidden id="loginUsersTimeZone" value="%{timeZone}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_TV_CLASS%>');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="720px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4" colspan="2"></td>
						</tr>
						<tr>
							<td class="labelT1" width="150px"><s:text
								name="config.tv.className" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.className" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="32" disabled="%{disabledName}" />&nbsp;<s:text
								name="config.tv.className.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.tv.subject" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.subject" size="28"
								maxlength="64" />&nbsp;<s:text name="config.tv.subject.range" /></td>

						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.tv.teacher" /><font color="red"><s:text name="*"/></font></td>
							<td style="padding-right: 5px;"><s:select id="teacherId" 
								name="dataSource.teacherId" list="%{lstTeacher}" listKey="key"
								listValue="value" cssStyle="width: 226px;" />
								<s:if test="%{writeDisabled == 'disabled' || switchToNoneHomeDomain}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<s:if test="%{showNewTeacherLinkForCustomerWithCid}">
										<a class="marginBtn" href="javascript:showNewTeacherPanel();"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newTeacher')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
								</s:else>
								<s:if test="%{showNewTeacherLinkForCustomerWithCid}">
									<s:if test="%{writeDisabled == 'disabled' || switchToNoneHomeDomain}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/delete_disable.png" />"
										width="16" height="16" alt="Remove Account" title="Remove Account" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:removeTeacher()"><img class="dinl"
										src="<s:url value="/images/delete.png" />"
										width="16" height="16" alt="Remove Account" title="Remove Account" /></a>
									</s:else>
								</s:if>
							</td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.tv.rosterType" /></td>
							<td style="padding-right: 5px;"><s:select
								name="dataSource.rosterType" list="%{enumRosterType}" listKey="key"
								listValue="value" cssStyle="width: 226px;" disabled="%{disabledName}"
								onchange="changeRosterType(this.value);"/>
							</td>
						</tr>
						<tr style="display:<s:property value="hideCartDiv"/>" id="hideCartDiv">
							<td class="labelT1"><s:text
								name="config.tv.cart" /></td>
							<td style="padding-right: 5px;"><s:select
								name="cartId" list="%{lstCart}" listKey="id"
								listValue="value" cssStyle="width: 175px;" />
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('newCart')"><img class="dinl"
									src="<s:url value="/images/new.png" />"
									width="16" height="16" alt="New" title="New" /></a>
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:submitAction('editCart')"><img class="dinl"
									src="<s:url value="/images/modify.png" />"
									width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else>
							</td>
						</tr>
						<tr>		
								
							<td class="labelT1"><s:text
								name="config.hp.description" /></td>	
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="256" />&nbsp;<s:text
								name="config.tv.description.range" /></td>
						</tr>
						<tr>
							<td style="padding:6px 0px 6px 0px" colspan="2">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td class="sepLine"><img
										src="<s:url value="/images/spacer.gif"/>" height="1"
										class="dblk" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr style="display:<s:property value="%{showMaxPeriodNote}"/>">
							<td class="noteInfo" style="padding-left: 30px" colspan="2">
								<s:text name="config.tv.classPeriod" /></td>
						</tr>
						<tr>
							<td colspan="2" style="padding:4px 0px 4px 4px;" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" class="embedded">
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="5" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" onClick="showCreateSection();"
													<s:property value="applyUpdateDisabled" />></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeSchedule');"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="5" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" <s:property value="applyUpdateDisabled" /> onClick="submitAction('addSchedule');"></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeScheduleNone');"></td>
												<td><input type="button" name="ignore" value="Cancel"
													class="button" onClick="hideCreateSection();"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
										<td colspan="5" style="padding: 4px 4px 4px 2px">
											<div><fieldset>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="100px"><s:text name="config.tv.weekDay"/><font color="red"><s:text name="*"/></font></td>
													<td width="80px"><s:checkbox name="addSun"/><s:text name="config.tv.weekday.1"/></td>
													<td width="80px"><s:checkbox name="addMon" onclick="clickMonToFri(this.checked);"/><s:text name="config.tv.weekday.2"/></td>
													<td width="80px"><s:checkbox name="addTue" onclick="clickMonToFri(this.checked);"/><s:text name="config.tv.weekday.3"/></td>
													<td width="130px"><s:checkbox name="addWed" onclick="clickMonToFri(this.checked);"/><s:text name="config.tv.weekday.4"/></td>
												</tr>
												<tr>
													<td class="labelT1"></td>
													<td width="80px"><s:checkbox name="addThu" onclick="clickMonToFri(this.checked);"/><s:text name="config.tv.weekday.5"/></td>
													<td width="80px"><s:checkbox name="addFri" onclick="clickMonToFri(this.checked);"/><s:text name="config.tv.weekday.6"/></td>
													<td width="80px"><s:checkbox name="addSat"/><s:text name="config.tv.weekday.7"/></td>
													<td width="130px"><s:checkbox name="addMonFri" onclick="clickMonFri(this.checked);"/><s:text name="config.tv.weekday.8"/></td>
													
												</tr>
												
												<tr>
													<td class="labelT1"><s:text name="config.tv.startTime"/><font color="red"><s:text name="*"/></font></td>
													<td colspan="4" style="padding-top: 4px"> <s:select name="addSHour" list="%{lstHour}" listKey="key"
															listValue="value" cssStyle="width: 50px;" />&nbsp;:
														<s:select name="addSMin" list="%{lstMin}" listKey="key"
															listValue="value" cssStyle="width: 50px;" /> </td>	
												</tr>
												<tr>
													<td class="labelT1"><s:text name="config.tv.endTime"/><font color="red"><s:text name="*"/></font></td>
													<td colspan="4"> <s:select name="addEHour" list="%{lstHour}" listKey="key"
															listValue="value" cssStyle="width: 50px;" />&nbsp;:
														<s:select name="addEMin" list="%{lstMin}" listKey="key"
															listValue="value" cssStyle="width: 50px;" /> </td>
												</tr>
												<tr>
													<td class="labelT1"><s:text name="config.tv.room"/></td>
													<td colspan="4"><s:textfield size="30" name="addRoom" maxlength="256" /></td>
												</tr>
											</table>
											</fieldset>
											</div>
										</td>
									</tr>
									<tr id="headerSection">
										<th align="left" style="padding-left: 0;" width="10px"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAllRules(this);"></th> 
										<th align="left" width="220px"><s:text
											name="config.tv.weekDay" /></th>
										<th align="left" width="140px"><s:text
											name="config.tv.startTime" /></th>
										<th align="left" width="140px"><s:text
											name="config.tv.endTime" /></th>
										<th align="left" width="200px"><s:text
											name="config.tv.room" /></th>
									</tr>
									<s:iterator value="%{dataSource.items}" status="status">
										<tr>
											<td class="listCheck"><s:checkbox name="scheduleIndices"
												fieldValue="%{#status.index}" 
												onClick="toggleAllCheckBox(this);"/></td>
											<td class="list"><s:property value="weekdaySecString"/></td>
											<td class="list"><s:property value="startTime"/></td>
											<td class="list"><s:property value="endTime"/></td>
											<td class="list"><s:property value="room"/></td>
										</tr>
									</s:iterator>
									<s:if test="%{gridCount > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="6">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
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
	<!-- make customer with CID can create teacher end -->
	<div id="newTeacherPanelId" style="display: none;">
		<div class="hd">
			<s:text name="config.tv.new.teacher" />
		</div>
		<div class="bd">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
				width="730px">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px" colspan="2">
						<font size="4"><s:text name="config.tv.account.details" /></font>
					</td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label><s:text
								name="config.tv.emailaddress" /><font color="red"><s:text
									name="hm.common.required" /></font></label></td>
					<td><s:textfield id="emailAddress" name="emailAddress" maxlength="128"
					onkeypress="return hm.util.keyPressPermit(event,'name');" 
					onblur="javascript:doCheckUserEmail()"
					cssStyle="width: 270px;" />
					<s:text name="config.tv.emailaddress.range" /></td>
				</tr>
				<tr id="msgRow_email" style="display:none"><td/>
					<td id="msgTd_email" class="noteInfo" colspan="2">
					<div style="width:550px" id="msg_email"/>
					</td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label><s:text
								name="config.tv.name" /><font color="red"><s:text
									name="hm.common.required" /></font></label></td>
					<td><s:textfield id="userName" name="userName" maxlength="128"
					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" 
					cssStyle="width: 270px;" />
					<s:text name="config.tv.name.range" /></td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label><s:text
								name="config.tv.description" /></td>
					<td><s:textfield id="description" name="description" maxlength="128"
					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" 
					cssStyle="width: 270px;" />
					<s:text name="config.tv.description.range" /></td>
				</tr>
				<tr>
					<td class="labelT1" style="padding-left: 15px" colspan="2">
						<font size="4"><s:text name="config.tv.account.preferences" /></font>
					</td>
				</tr>
				
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label> <s:text
								name="config.tv.language" />
					</label></td>
					<td><s:select name="i18n"
							list="enumLanguage" listKey="key" listValue="value"
							cssStyle="width:275px;" /></td>
				</tr>
<%-- 			<tr>
					<td class="labelT1"  width="160">
						<label>
							<s:text name="config.tv.css" />
						</label>
					</td>
					<td>
						<s:select name="gmCss" list="enumGmCss" listKey="key" listValue="value" cssStyle="width:100px;"/>
					</td>
				</tr> --%>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label> <s:text
								name="config.tv.time.zone" />
					</label></td>
					<td><s:select name="timeZone" value="%{timeZone}"
							list="%{enumTimeZone}" listKey="key" listValue="value"
							cssStyle="width:275px;" /></td>
				</tr>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label> <s:text
								name="config.tv.date.format" />
					</label></td>
					<td><s:select name="dateFormat"
							list="%{enumDateFormat}" listKey="key"
							listValue="value" 
							cssStyle="width:275px;" /></td>
				</tr>
				<tr>
					<td class="labelT1" width="160" style="padding-left: 30px"><label> <s:text
								name="config.tv.time.format" />
					</label></td>
					<td><s:radio name="timeFormat"
							list="%{enumTimeFormat}" listKey="key"
							listValue="value" ></s:radio></td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="Save"
									class="button" onClick="doCreateTeacher();"></td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideNewTeacherPanel();"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
			</table>
		</div>
	</div>
	<!-- make customer with CID can create teacher end -->
</s:form>
</div>
