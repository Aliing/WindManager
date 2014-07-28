<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.network.AirScreenSource"%>
<%@page import="com.ah.bo.network.AirScreenAction"%>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<style>
<!--
fieldset {
	padding: 0 10px 10px 10px;
	margin: 0;
}
-->
</style>
<script type="text/javascript">
var formName = 'airScreenRules';

var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

function newSource(){
	Get("sourceCreation").style.display = "";
	var nameEl = Get(formName + "_dataSource_tempSource_profileName");
	if (nameEl.disabled) {
		nameEl.value = "";
		nameEl.disabled =  false;
		submitAction("newSource");
	}
	nameEl.focus();
}

function editSource(){
	submitAction("editSource");
}

function hideSourceCreateSection(){
	Get("sourceCreation").style.display = "none";
}

function newBehaviorContent(){
	Get("behaviorCreation").style.display = "";
	var nameEl = Get(formName + "_dataSource_tempBehavior_profileName");
	if (nameEl.disabled) {
		nameEl.value = "";
		nameEl.disabled =  false;
		submitAction("newBehavior");
	}
	nameEl.focus();
}

function editBehaviorContent(){
	submitAction("editBehavior");
}

function hideBehaviorCreateSection(){
	Get("behaviorCreation").style.display = "none";
}

function newActionContent(){
	Get("actionCreation").style.display = "";
	var nameEl = Get(formName + "_dataSource_tempAction_profileName");
	if (nameEl.disabled) {
		nameEl.value = "";
		nameEl.disabled =  false;
		submitAction("newAction");
	}
	nameEl.focus();
}

function editActionContent(){
	submitAction("editAction");
}

function hideActionCreateSection(){
	Get("actionCreation").style.display = "none";
}

function submitAction(operation) {
	if(operation == "newBehaviorContent"){
		newBehaviorContent();
	}else if(operation == "editBehaviorContent"){
		editBehaviorContent();
	}else if(operation == "newActionContent"){
		newActionContent();
	}else if(operation == "editActionContent"){
		editActionContent();
	}else{
		thisOperation = operation;
		if (validate(operation)) {
			doContinueOper();
		}
	}
}

function doContinueOper() {
	if(thisOperation == 'create<s:property value="lstForward"/>' || thisOperation == 'update<s:property value="lstForward"/>'){
		showProcessing();
	}
	
	document.forms[formName].operation.value = thisOperation;
	hm.options.selectAllOptions('behaviors');
	hm.options.selectAllOptions('actions');
	//save style values
	Get(formName + "_dataSource_sourceCreationDisplayStyle").value = Get("sourceCreation").style.display;
	Get(formName + "_dataSource_behaviorCreationDisplayStyle").value = Get("behaviorCreation").style.display;
	Get(formName + "_dataSource_actionCreationDisplayStyle").value = Get("actionCreation").style.display;
    document.forms[formName].submit();
}

function validate(operation) {
	if(operation == 'cancel<s:property value="lstForward"/>' 
		|| operation == 'viewSources' || operation == 'viewBehaviors' || operation == 'viewActions'
		|| operation == 'clearSource' || operation == 'clearBehavior' || operation == 'clearAction' ) {
		resetSourceCreation();
		resetBehaviorCreation();
		resetActionCreation();
		return true;
	}
	if(operation == "editOui"){
		var value = hm.util.validateListSelection("myOuiSelect");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].sourceOuiId.value = value;
			resetSourceCreation();
			resetBehaviorCreation();
			resetActionCreation();
			return true;
		}
	}
	if(operation == "editSource"){
		var value = hm.util.validateListSelection(formName + "_sourceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].sourceId.value = value;
			resetSourceCreation();
			resetBehaviorCreation();
			resetActionCreation();
			return true;
		}
	}
	if(operation == "editBehavior"){
		var value = hm.util.validateOptionTransferSelection("behaviors");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].behaviorId.value = value;
			resetSourceCreation();
			resetBehaviorCreation();
			resetActionCreation();
			return true;
		}
	}
	if(operation == "editAction"){
		var value = hm.util.validateOptionTransferSelection("actions");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].actionId.value = value;
			resetSourceCreation();
			resetBehaviorCreation();
			resetActionCreation();
			return true;
		}
	}
	if(operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>'
		|| operation == 'create' || operation == 'update'){
		if(!validateProfileName()){
			return false;
		}
		if(!validateSourceSelection()){
			return false;
		}
		if(!validateBehaviorSelection()){
			return false;
		}
		if(!validateActionSelection()){
			return false;
		}
		resetSourceCreation();
		resetBehaviorCreation();
		resetActionCreation();
		return true;
	}
	if(operation == 'createSource' || operation == 'updateSource'){
		if(!validateSourceCreation(operation)){
			return false;
		}
		resetBehaviorCreation();
		resetActionCreation();
		return true;
	}
	if(operation == 'createBehavior' || operation == 'updateBehavior'){
		if(!validateBehaviorCreation(operation)){
			return false;
		}
		resetSourceCreation();
		resetActionCreation();
		return true;
	}
	if(operation == 'createAction' || operation == 'updateAction'){
		if(!validateActionCreation(operation)){
			return false;
		}
		resetSourceCreation();
		resetBehaviorCreation();
		return true;
	}
	return true;
}

function validateProfileName() {
    var inputElement = Get(formName + "_dataSource_profileName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.air.screen.rule.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateSourceSelection(){
	return true;
}

function validateSourceCreation(operation){
	if(!validateSourceName(operation)){
		return false;
	}
	if (!validateSourceOui(operation)) {
		return false;
	}
	if(!validateRssiValues(operation)){
		return false;
	}
	return true;
}

function validateSourceOui(operation) {
	var ouinames = document.getElementById("myOuiSelect");
	var ouiValue = document.getElementById("dataSource.inputOuiText");
	var showError = document.getElementById("errorDisplay");
	if ("" == ouiValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.air.screen.source.oui" /></s:param></s:text>');
        ouiValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ouinames, ouiValue)) {
		if (!hm.util.validateMacAddress(ouiValue.value, 6)) {
			hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.air.screen.source.oui" /></s:param></s:text>');
			ouiValue.focus();
			return false;
		}
		document.forms[formName].sourceOuiId.value = -1;
	} else {
		document.forms[formName].sourceOuiId.value = ouinames.options[ouinames.selectedIndex].value;
	}
	return true;
}

function resetSourceCreation(){
	var minRssiElement = Get(formName + "_dataSource_tempSource_minRssi");
	var maxRssiElement = Get(formName + "_dataSource_tempSource_maxRssi");
	if(isNaN(minRssiElement.value)){
		minRssiElement.value = 3;
	}
	if(isNaN(maxRssiElement.value)){
		maxRssiElement.value = 65;
	}
}

function validateBehaviorSelection(){
	var behaviorsEl = Get("behaviors");
	var displayEl = Get("behaviorInfoEl");
	if(behaviorsEl){
		var count = behaviorsEl.length;
		if(count > 4){
			hm.util.reportFieldError(displayEl, '<s:text name="error.airscreen.overflow.behaviors" />');
			return false;
		}
	}
	return true;
}

function validateBehaviorCreation(operation){
	if(!validateBehaviorName(operation)){
		return false;
	}
	if(!validateThreshold(operation)){
		return false;
	}
	if(!validateInterval(operation)){
		return false;
	}
	return true;
}

function resetBehaviorCreation(){
	var thresholdElement = Get(formName + "_dataSource_tempBehavior_threshold");
	var intervalElement = Get(formName + "_dataSource_tempBehavior_interval");
	if(isNaN(thresholdElement.value)){
		thresholdElement.value = 10;
	}
	if(isNaN(intervalElement.value)){
		intervalElement.value = 60;
	}
}

function validateActionSelection(){
	var actionsEl = Get("actions");
	var displayEl = Get("actionInfoEl");
	if(actionsEl){
		var count = actionsEl.length;
		if(count > 8){
			hm.util.reportFieldError(displayEl, '<s:text name="error.airscreen.overflow.actions" />');
			return false;
		}
	}
	return true;
}

function validateActionCreation(operation){
	if(!validateActionName(operation)){
		return false;
	}
	if(!validateActionInterval(operation)){
		return false;
	}
	return true;
}

function resetActionCreation(){
	var intervalElement = Get(formName + "_dataSource_tempAction_interval");
	if(isNaN(intervalElement.value)){
		intervalElement.value = 3600;
	}
}

function validateSourceName(operation) {
	if("createSource"==operation){
	    var inputElement = Get(formName + "_dataSource_tempSource_profileName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.air.screen.source.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
	}
    return true;
}

function validateRssiValues(operation) {
	var minRssiElement = Get(formName + "_dataSource_tempSource_minRssi");
	var maxRssiElement = Get(formName + "_dataSource_tempSource_maxRssi");
	if (minRssiElement.value.length == 0) {
		hm.util.reportFieldError(minRssiElement, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.source.rssi.start" /></s:param></s:text>');
		minRssiElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(minRssiElement.value, '<s:text name="config.air.screen.source.rssi.start" />',
	                                           <s:property value="3" />,
	                                           <s:property value="65" />);
	if (message != null) {
		hm.util.reportFieldError(minRssiElement, message);
		minRssiElement.focus();
		return false;
	}

	if (maxRssiElement.value.length == 0) {
		hm.util.reportFieldError(minRssiElement, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.source.rssi.to" /></s:param></s:text>');
		maxRssiElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(maxRssiElement.value, '<s:text name="config.air.screen.source.rssi.to" />',
	                                           <s:property value="3" />,
	                                           <s:property value="65" />);
	if (message != null) {
		hm.util.reportFieldError(minRssiElement, message);
		maxRssiElement.focus();
		return false;
	}
	if(parseInt(maxRssiElement.value) < parseInt(minRssiElement.value)){
		hm.util.reportFieldError(minRssiElement, '<s:text name="error.notLargerThan"><s:param><s:text name="config.air.screen.source.rssi.start" /></s:param><s:param><s:text name="config.air.screen.source.rssi.to" /></s:param></s:text>');
		minRssiElement.focus();
		return false;
	}
	return true;
}

function validateBehaviorName(operation) {
	if("createBehavior"==operation){
	    var inputElement = Get(formName + "_dataSource_tempBehavior_profileName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.air.screen.behavior.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
		if (hm.util.hasSelectedOptionSameValue("leftOptions_behaviors", inputElement) ||
			hm.util.hasSelectedOptionSameValue("behaviors", inputElement)) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.objectExists"><s:param>'+inputElement.value+'</s:param></s:text>');
		    inputElement.focus();
		    return false;
		}
	}
    return true;
}

function validateThreshold(operation){
	var thresholdElement = Get(formName + "_dataSource_tempBehavior_threshold");
	if (thresholdElement.value.length == 0) {
		hm.util.reportFieldError(thresholdElement, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.behavior.threshold" /></s:param></s:text>');
		thresholdElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(thresholdElement.value, '<s:text name="config.air.screen.behavior.threshold" />',
	                                           <s:property value="1" />,
	                                           <s:property value="2147483647" />);
	if (message != null) {
		hm.util.reportFieldError(thresholdElement, message);
		thresholdElement.focus();
		return false;
	}
	return true;
}

function validateInterval(operation){
	var intervalElement = Get(formName + "_dataSource_tempBehavior_interval");
	if (intervalElement.value.length == 0) {
		hm.util.reportFieldError(intervalElement, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.behavior.interval" /></s:param></s:text>');
		intervalElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(intervalElement.value, '<s:text name="config.air.screen.behavior.interval" />',
	                                           <s:property value="1" />,
	                                           <s:property value="2147483647" />);
	if (message != null) {
		hm.util.reportFieldError(intervalElement, message);
		intervalElement.focus();
		return false;
	}
	if (intervalElement.value % 5 != 0){
		hm.util.reportFieldError(intervalElement, '<s:text name="error.notDivideBy"><s:param><s:text name="config.air.screen.behavior.interval" /></s:param><s:param>5</s:param></s:text>');
		intervalElement.focus();
		return false;
	}
	return true;
}

function validateActionName(operation) {
	if("createAction"==operation){
	    var inputElement = Get(formName + "_dataSource_tempAction_profileName");
		var message = hm.util.validateName(inputElement.value, '<s:text name="config.air.screen.action.name" />');
		if (message != null) {
		    hm.util.reportFieldError(inputElement, message);
		    inputElement.focus();
		    return false;
		}
		if (hm.util.hasSelectedOptionSameValue("leftOptions_actions", inputElement) ||
			hm.util.hasSelectedOptionSameValue("actions", inputElement)) {
			hm.util.reportFieldError(inputElement, '<s:text name="error.objectExists"><s:param>'+inputElement.value+'</s:param></s:text>');
		    inputElement.focus();
		    return false;
		}
	}
    return true;
}

function validateActionInterval(operation){
	var intervalElement = Get(formName + "_dataSource_tempAction_interval");
	if (intervalElement.value.length == 0) {
		hm.util.reportFieldError(intervalElement, '<s:text name="error.requiredField"><s:param><s:text name="config.air.screen.action.interval" /></s:param></s:text>');
		intervalElement.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(intervalElement.value, '<s:text name="config.air.screen.action.interval" />',
	                                           <s:property value="0" />,
	                                           <s:property value="360000" />);
	if (message != null) {
		hm.util.reportFieldError(intervalElement, message);
		intervalElement.focus();
		return false;
	}
	return true;
}

function authModeChange(authValue){
	var encryptionTr = Get("encryptionTr");
	if(!encryptionTr){
		return;
	}
	if(authValue == "<%=AirScreenSource.AUTH_MODE_WPA%>"
		|| authValue == "<%=AirScreenSource.AUTH_MODE_WPA2_8021X%>"
		|| authValue == "<%=AirScreenSource.AUTH_MODE_WPA2_PSK%>"
		|| authValue == "<%=AirScreenSource.AUTH_MODE_WPA_8021X%>"
		|| authValue == "<%=AirScreenSource.AUTH_MODE_WPA_PSK%>"){
		encryptionTr.style.display = "";
	}else{
		encryptionTr.style.display = "none";
	}
}

function actionTypeChanged(actionType){
	var actionIntervalTr = Get("actionIntervalTr");
	if(!actionIntervalTr){
		return;
	}
	if(actionType == "<%=AirScreenAction.TYPE_LOCAL_BAN%>"){
		actionIntervalTr.style.display = "";
	}else{
		actionIntervalTr.style.display = "none";
		//reset interval value if required
		var intervalElement = Get(formName + "_dataSource_tempAction_interval");
		if(isNaN(intervalElement.value)){
			intervalElement.value = 3600;
		}
	}
}


function onLoadPage() {
	if(Get(formName + "_dataSource_profileName").disabled == false){
		Get(formName + "_dataSource_profileName").focus();
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="airScreenRules" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>

<div id="content"><s:form action="airScreenRules">
	<s:hidden name="dataSource.sourceCreationDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.behaviorCreationDisplayStyle"></s:hidden>
	<s:hidden name="dataSource.actionCreationDisplayStyle"></s:hidden>
	<s:hidden name="behaviorId"></s:hidden>
	<s:hidden name="actionId"></s:hidden>
	<s:hidden name="sourceOuiId" />
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
							onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
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
			<td style="padding-top: 5px;">
			<table class="editBox" style="padding: 0 4px 6px 4px;"
				cellspacing="0" cellpadding="0" border="0" width="680px">
				<tr>
					<td><!-- definition -->
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="130px"><label><s:text
								name="config.air.screen.rule.name" /><font color="red"><s:text
								name="*" /></font></label></td>
							<td><s:textfield name="dataSource.profileName"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								size="32" maxlength="%{profileNameLength}"
								disabled="%{disabledName}" /> <s:text
								name="config.air.screen.rule.name.note" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text
								name="config.air.screen.rule.comment" /></td>
							<td><s:textfield name="dataSource.comment" size="64"
								maxlength="%{commentLength}" /> <s:text
								name="config.air.screen.rule.comment.note" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td><!-- source -->
					<div>
					<fieldset><legend><s:text name="config.air.screen.source.tag" /></legend>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr><!-- source selection -->
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="labelT1" width="120px"><s:text
										name="config.air.screen.rule.source" /></td>
									<td><s:select name="sourceId" list="%{sources}" listKey="id"
										listValue="value" cssStyle="width: 180px;" /></td>
									<td>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:newSource()"><img class="dinl"
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
											<a class="marginBtn" href="javascript:editSource()"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</td>
									<td style="padding:2px 5px 0 150px" valign="top">
										<input type="button" style="width: 140px;" name="ignore" value="<s:text name='config.air.screen.rule.sourceBtn' />" class="button" onClick="submitAction('viewSources');"
										<s:property value="writeDisabled" />>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr><!-- source creation -->
							<td id="sourceCreation" style="display: <s:property value="%{dataSource.sourceCreationDisplayStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding-bottom: 2px; padding-left: 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<s:if test="%{sourceNameDisabled}">
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('updateSource');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('createSource');"></td>
												</s:else>
												<s:if test="%{sourceNameDisabled}">
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="submitAction('clearSource');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="hideSourceCreateSection();"></td>
												</s:else>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td class="sepLine"><img
											src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
									</tr>
									<tr>
										<td>
											<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" width="120px"><s:text
														name="config.air.screen.source.name" /></td>
													<td><s:textfield name="dataSource.tempSource.profileName" size="32"
														maxlength="%{sourceNameLength}" disabled="%{sourceNameDisabled}" /> <s:text
														name="config.air.screen.source.name.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.source.comment" /></td>
													<td><s:textfield name="dataSource.tempSource.comment" size="48"
														maxlength="%{sourceCommentLength}" /> <s:text
														name="config.air.screen.source.comment.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.source.type" /></td>
													<td><s:select name="dataSource.tempSource.type" list="%{sourceTypes}"
														listKey="key" listValue="value" cssStyle="width: 180px;" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.source.oui" /></td>
													<td>
														<ah:createOrSelect divId="errorDisplay" swidth="180px"
															list="ouis" typeString="Oui" selectIdName="myOuiSelect"
															inputValueName="dataSource.inputOuiText" />
													</td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.source.auth" /></td>
													<td><s:select name="dataSource.tempSource.authMode" list="%{sourceAuthModes}"
														listKey="key" listValue="value" cssStyle="width: 180px;" onchange="authModeChange(this.value);" /></td>
												</tr>
												<tr id="encryptionTr" style="display: <s:property value="%{encryptionTrStyle}"/>">
													<td class="labelT1"><s:text
														name="config.air.screen.source.encryption" /></td>
													<td><s:select name="dataSource.tempSource.encryptionMode"
														list="%{sourceEncryptionModes}" listKey="key" listValue="value"
														cssStyle="width: 180px;" /></td>
												</tr>
												<tr>
													<td colspan="2">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="120px"><s:text
																name="config.air.screen.source.rssi.start" /></td>
															<td><s:textfield name="dataSource.tempSource.minRssi" maxlength="2"
																size="4"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
															<td class="labelT1" width="20px"><s:text
																name="config.air.screen.source.rssi.to" /></td>
															<td><s:textfield name="dataSource.tempSource.maxRssi" maxlength="2"
																size="4"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text
																name="config.air.screen.source.rssi.note" /></td>
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
					</fieldset>
					</div>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td><!-- behavior -->
					<div style="position: relative;">
					<div style="position: absolute; right: 15px; top: 22px;">
						<input type="button" style="width: 140px;" name="ignore" value="<s:text name='config.air.screen.rule.behaviorBtn' />" class="button" onClick="submitAction('viewBehaviors');"
							<s:property value="writeDisabled" />>
					</div>
					<fieldset><legend><s:text name="config.air.screen.behavior.tag" /></legend>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr><!-- behavior selection -->
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td><label id="behaviorInfoEl"></label></td>
									</tr>
									<tr>
										<s:push value="%{behaviorOptions}">
											<td colspan="3"><tiles:insertDefinition name="optionsTransfer" /></td>
										</s:push>
									</tr>
									<%--<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding:0 5px 0 25px;">
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/new_disable.png" />"
												width="16" height="16" alt="New" title="New" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:newBehavior()"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
											</s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/modify_disable.png" />"
												width="16" height="16" alt="Modify" title="Modify" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:editBehavior()"><img class="dinl"
												src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else>
										</td>
									</tr>--%>
								</table>
							</td>
						</tr>
						<tr><!-- behavior creation -->
							<td id="behaviorCreation" style="display: <s:property value="%{dataSource.behaviorCreationDisplayStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding-bottom: 2px; padding-left: 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<s:if test="%{behaviorNameDisabled}">
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('updateBehavior');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('createBehavior');"></td>
												</s:else>
												<s:if test="%{behaviorNameDisabled}">
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="submitAction('clearBehavior');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="hideBehaviorCreateSection();"></td>
												</s:else>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td class="sepLine"><img
											src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
									</tr>
									<tr>
										<td>
											<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" width="120px"><s:text
														name="config.air.screen.behavior.name" /></td>
													<td><s:textfield name="dataSource.tempBehavior.profileName" size="32"
														maxlength="%{behaviorNameLength}" disabled="%{behaviorNameDisabled}" /> <s:text
														name="config.air.screen.behavior.name.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.behavior.comment" /></td>
													<td><s:textfield name="dataSource.tempBehavior.comment" size="48"
														maxlength="%{behaviorCommentLength}" /> <s:text
														name="config.air.screen.behavior.comment.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.behavior.type" /></td>
													<td><s:select name="dataSource.tempBehavior.type" list="%{behaviorTypes}"
														listKey="key" listValue="value" cssStyle="width: 180px;" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.behavior.case" /></td>
													<td><s:select name="dataSource.tempBehavior.connectionCase" list="%{behaviorCases}"
														listKey="key" listValue="value" cssStyle="width: 180px;" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.behavior.threshold" /></td>
													<td><s:textfield name="dataSource.tempBehavior.threshold" maxlength="10"
																size="18"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text name="config.air.screen.behavior.threshold.note"/></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.behavior.interval" /></td>
													<td><s:textfield name="dataSource.tempBehavior.interval" maxlength="10"
																size="18"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text name="config.air.screen.behavior.interval.note"/></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					</fieldset>
					</div>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td><!-- action -->
					<div style="position: relative;">
					<div style="position: absolute; right: 15px; top: 22px;">
						<input type="button" style="width: 140px;" name="ignore" value="<s:text name='config.air.screen.rule.actionBtn' />" class="button" onClick="submitAction('viewActions');"
							<s:property value="writeDisabled" />>
					</div>
					<fieldset><legend><s:text name="config.air.screen.action.tag" /></legend>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr><!-- action selection -->
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td><label id="actionInfoEl"></label></td>
									</tr>
									<tr>
										<s:push value="%{actionOptions}">
											<td colspan="3"><tiles:insertDefinition name="optionsTransfer" /></td>
										</s:push>
									</tr>
									<%--<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding:0 5px 0 25px;">
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/new_disable.png" />"
												width="16" height="16" alt="New" title="New" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:newAction()"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
											</s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<img class="dinl marginBtn"
												src="<s:url value="/images/modify_disable.png" />"
												width="16" height="16" alt="Modify" title="Modify" />
											</s:if>
											<s:else>
												<a class="marginBtn" href="javascript:editAction()"><img class="dinl"
												src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
											</s:else>
										</td>
									</tr>--%>
								</table>
							</td>
						</tr>
						<tr><!-- action creation -->
							<td id="actionCreation" style="display: <s:property value="%{dataSource.actionCreationDisplayStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td height="5px"></td>
									</tr>
									<tr>
										<td style="padding-bottom: 2px; padding-left: 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<s:if test="%{actionNameDisabled}">
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('updateAction');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="submitAction('createAction');"></td>
												</s:else>
												<s:if test="%{actionNameDisabled}">
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="submitAction('clearAction');"></td>
												</s:if>
												<s:else>
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="hideActionCreateSection();"></td>
												</s:else>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td class="sepLine"><img
											src="<s:url value="/images/spacer.gif"/>" height="2" class="dblk" /></td>
									</tr>
									<tr>
										<td>
											<table class="listembedded" cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td class="labelT1" width="120px"><s:text
														name="config.air.screen.action.name" /></td>
													<td><s:textfield name="dataSource.tempAction.profileName" size="32"
														maxlength="%{actionNameLength}" disabled="%{actionNameDisabled}" /> <s:text
														name="config.air.screen.action.name.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.action.comment" /></td>
													<td><s:textfield name="dataSource.tempAction.comment" size="48"
														maxlength="%{actionCommentLength}" /> <s:text
														name="config.air.screen.action.comment.note" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="config.air.screen.action.type" /></td>
													<td><s:select name="dataSource.tempAction.type" list="%{actionTypes}"
														listKey="key" listValue="value" cssStyle="width: 180px;" onchange="actionTypeChanged(this.value);" /></td>
												</tr>
												<tr id="actionIntervalTr" style="display: <s:property value="%{actionIntervalTrStyle}"/>">
													<td class="labelT1"><s:text
														name="config.air.screen.action.interval" /></td>
													<td><s:textfield name="dataSource.tempAction.interval" maxlength="10"
																size="18"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /> <s:text name="config.air.screen.action.interval.note"/></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					</fieldset>
					</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>