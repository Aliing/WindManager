<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'temporaryAccount';
var thisOperation;

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_visitorName").disabled == false) {
		document.getElementById(formName + "_dataSource_visitorName").focus();
	}
	
	changeUserGroup();
}


function submitAction(operation) {
    thisOperation = operation;
	
	if(validate(operation)) {
	    doContinueOper();   
	}
}

function validate(operation) {
	if('cancel<s:property value="lstForward"/>' == operation){
		return true;
	}
	
	if(!validateVisitorName()){
		return false;
	}
	
	if(!validateEmail()){
		return false;
	}
	
	if(!validateVisitorCompany()){
		return false;
	}
	
	if(!validateSponsor()){
		return false;
	}
	
	return true;
}

function validateVisitorName() {
	var name = document.getElementById(formName + "_dataSource_visitorName");
	var message = hm.util.validateStringWithBlank(name.value, '<s:text name="gml.temporary.visitor" />');
	
	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	
	return true;
}

function validateEmail() {
	var email = document.getElementById(formName + "_dataSource_mailAddress");
	
	if(email.value.trim().length == 0) {
		hm.util.reportFieldError(email, 
			'<s:text name="error.requiredField"><s:param><s:text name="gml.temporary.email" /></s:param></s:text>');
       	email.focus();
       	return false;
	}
	
	if(!hm.util.validateEmail(email.value)) {
		hm.util.reportFieldError(email, 
			'<s:text name="error.gml.temporary.email.invalid" />');
       	email.focus();
       	return false;
	}
   	
	return true;
}

function validateVisitorCompany() {
	var name = document.getElementById(formName + "_dataSource_visitorCompany");
	var message = hm.util.validateStringWithBlank(name.value, '<s:text name="gml.temporary.company" />');
	
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	return true;
}

function validateSponsor() {
	var name = document.getElementById(formName + "_dataSource_sponsor");
	var message = hm.util.validateUsername(name.value, '<s:text name="gml.temporary.sponsor" />');
	
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	return true;
}

function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].operationClass.value = "createAccounts";
    document.forms[formName].submit();
}

function changeUserGroup() {
	var element = document.getElementById("userGroupId");
	var url = '<s:url action="temporaryAccount" includeParams="none"></s:url>' 
				+ "?operation=changeUserGroup" + "&userGroupId=" + element.value
				+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : changeUser, failure : connectFailed}, null);
}

var changeUser = function(o) {
	eval("var details = " + o.responseText);
	
	if(details.length == 1) {
		document.getElementById("pskTD").innerHTML = details[0].psk;
		document.getElementById(formName + "_dataSource_mailAddress").value = details[0].email;
		document.getElementById("startTD").innerHTML = details[0].start;
		document.getElementById("expiredTD").innerHTML = details[0].expired;
		document.getElementById(formName + "_dataSource_sponsor").value = details[0].sponsor;
		var ssidEl = document.getElementById("ssid");
		ssidEl.options.length = 0;
		if(details[0].ssidList){
			for(var i=0;i<details[0].ssidList.length;i++){
				ssidEl.options[ssidEl.options.length] = new Option(details[0].ssidList[i],details[0].ssidList[i]);
			}
		}
		//document.getElementById("commentBox").value = details[0].comment;
	} else if(details.length > 1) {
		var table = Get('ppskTable');
		
		/*
		 * remove existing rows
		 */
		for(var i=table.rows.length - 1; i>=1; i--) {
			table.deleteRow(i);
		}
		
		for(var i=0; i<details.length; i++) {
			// radio button
	    	var newRow = table.insertRow(-1);
	    	var oCell = newRow.insertCell(-1);
	    	
	    	oCell.style.align = "left";
	    	oCell.style.width = "10px";
	    	oCell.className = "listCheck";
	    	
	    	if(i == 0) {
				oCell.innerHTML = "<input type='radio' name='ppskIds' value='" + details[i].id + "' checked/>";
	    	} else {
	    		oCell.innerHTML = "<input type='radio' name='ppskIds' value='" + details[i].id + "' />";
	    	}
			
			// psk
	    	/* oCell = newRow.insertCell(-1);
	    	oCell.style.align = "left";
	    	oCell.style.width = "70px";
	    	oCell.className = "list";
			oCell.innerHTML = details[i].psk; */
			
			// start time
	    	oCell = newRow.insertCell(-1);
	    	oCell.style.align = "left";
	    	oCell.style.width = "160px";
	    	oCell.className = "list";
			oCell.innerHTML = details[i].start;
			
			// start time
	    	oCell = newRow.insertCell(-1);
	    	oCell.style.align = "left";
	    	oCell.style.width = "160px";
	    	oCell.className = "list";
			oCell.innerHTML = details[i].expired;
		}
    	
		openPpskSelectPanel();
	} else {
		hm.util.reportFieldError(Get("userGroupId"), 
				'<s:text name="gml.temporary.group.noAccount" />');
	}
	
};

var connectFailed = function(o) {

};

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="temporaryAccount" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('Create </td>');
	</s:else>
}  
</script>

<div id="content">
<s:form action="temporaryAccount">
<s:hidden name="operationClass" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="allocate" value="<s:text name="Save"/>"
							class="button" onClick="submitAction('allocated');"
							></td>
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
					</td>
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
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="550px">
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td style="padding:0 2px 0 10px" colspan="2">
						<FONT color="blue"><s:property value="leftUserCount" /></FONT>
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<s:if test="%{fullMode}">
									<td class="labelT1" width="150px">
										<s:text name="gml.temporary.userGroup" />
									</td>
								</s:if>
								<s:elseif test="%{easyMode}">
									<td class="labelT1" width="150px">
										<s:text name="gml.temporary.ssid" />
									</td>
								</s:elseif>
								<td><s:select id="userGroupId" name="userGroupId" 
										value="%{userGroupId}" list="%{availableUserGroups}" 
										listKey="id" listValue="value"
										onchange="changeUserGroup();"
										cssStyle="width: 180px;" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.visitor" /><font color="red"><s:text name="*"/></font>
								</td>
								<td><s:textfield size="36"
									name="dataSource.visitorName" maxlength="%{visitorNameLength}"
									cssStyle="width: 260px;" 
									onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />&nbsp;<s:text
									name="gml.temporary.visitor.range" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.email" /><font color="red"><s:text name="*"/></font>
								</td>
								<td><s:textfield size="36"
									cssStyle="width: 260px;" 
									name="dataSource.mailAddress" maxlength="%{mailAddressLength}"
									 />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.psk" />
								</td>
								<td id="pskTD">
									<s:property value="%{dataSource.strPsk}"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.startTime" />
								</td>
								<td id="startTD">
									<s:property value="%{dataSource.startTimeString}"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.endTime" />
								</td>
								<td id="expiredTD">
									<s:property value="%{dataSource.expiredTimeString}"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.company" /><font color="red"><s:text name="*"/></font>
								</td>
								<td><s:textfield size="36"
									name="dataSource.visitorCompany" maxlength="%{visitorCompanyLength}"
									cssStyle="width: 260px;" 
									onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />&nbsp;<s:text
									name="gml.temporary.visitor.range" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.sponsor" /><font color="red"><s:text name="*"/></font>
								</td>
								<td><s:textfield size="36"
									name="dataSource.sponsor" maxlength="%{sponsorLength}"
									cssStyle="width: 260px;" 
									onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="gml.temporary.sponsor.range" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<s:if test="%{fullMode}">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.ssid" />
								</td>
								<td><s:select id="ssid" name="dataSource.ssidName" 
										value="%{dataSource.ssidName}" list="%{availableSsids}" 
										cssStyle="width: 180px;" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				</s:if>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="150px">
									<s:text name="gml.temporary.comment" />
								</td>
								<td><s:textfield size="36" id="commentBox"
									cssStyle="width: 260px;" 
									name="dataSource.description" maxlength="%{descriptionLength}"/>&nbsp;<s:text
									name="config.ssid.description_range" />
								</td>
							</tr>
						</table>
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
<div id="ppskSelectPanel" style="display: none;">
	<div class="hd"><s:text name="gml.temporary.select.ppsk" /></div>
	<div class="bd">
		<table id="ppskTable" cellspacing="0" cellpadding="0" border="0" 
			height="100%" width="100%" class="view">
			<tr>
				<th><span id="notesTD">&nbsp;</span></th>
				<!-- <th>Private PSK</th> -->
				<th><s:text name="gml.temporary.startTime" /></th>
				<th><s:text name="gml.temporary.endTime" /></th>
			</tr>
		</table>
		<table cellspacing="0" cellpadding="0" border="0" width="100%" >
			<tr>
				<td align="center">
					<input type="button" name="ok" value="OK"
							class="button" onClick="selectPPSK();">
				</td>
			</tr>
		</table>
	</div>
</div>
<script>
var ppskSelectPanel = null;

function createPpskSelectPanel(width, height){
	var div = Get("ppskSelectPanel");
	ppskSelectPanel = new YAHOO.widget.Panel(div, 
	                                        { width:width+"px", 
											  fixedcenter:"contained", 
											  visible:false,
											  draggable: true,
											  modal: true,
											  constraintoviewport:true } );
	ppskSelectPanel.render(document.body);
	div.style.display="";
}

function openPpskSelectPanel() {
	if(ppskSelectPanel == null) {
		createPpskSelectPanel(400, 300);
	}
	
	
	ppskSelectPanel.show();
}

function closePpskSelectPanel() {
	if(ppskSelectPanel != null) {
		ppskSelectPanel.hide();
	}
}

function selectPPSK() {
	var radios = document.getElementsByName('ppskIds');
	var pskId = -1;
	
	for(var i=0; i<radios.length; i++) {
		if(radios[i].checked) {
			pskId = radios[i].value;
			break;
		}
	}
	
	
	if (pskId == -1) {
		hm.util.reportFieldError(document.getElementById("notesTD"), 'Please select one private PSK.');
		return false;
	}
	
	closePpskSelectPanel();

	var group = document.getElementById("userGroupId");
	var url = '<s:url action="temporaryAccount" includeParams="none"></s:url>' 
				+ "?operation=selectPPSK" + "&userGroupId=" + group.value
				+ "&ppskId=" + pskId
				+ "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : displayPPSK, failure : connectFailed}, null);
}

var displayPPSK = function(o) {
	eval("var details = " + o.responseText);
	
	document.getElementById("pskTD").innerHTML = details.psk;
	document.getElementById(formName + "_dataSource_mailAddress").value = details.email;
	document.getElementById("startTD").innerHTML = details.start;
	document.getElementById("expiredTD").innerHTML = details.expired;
	document.getElementById(formName + "_dataSource_sponsor").value = details.sponsor;
	//document.getElementById("commentBox").value = details.comment;
	
	var ssidEl = document.getElementById("ssid");
	ssidEl.options.length = 0;
	if(details.ssidList){
		for(var i=0;i<details.ssidList.length;i++){
			ssidEl.options[ssidEl.options.length] = new Option(details.ssidList[i],details.ssidList[i]);
		}
	}
}
</script>