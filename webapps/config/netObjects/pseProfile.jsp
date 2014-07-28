<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.PseProfile" %>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script>
var formName = 'pseProfile';
var THRESHOLD_POWER_AF = <%=PseProfile.THRESHOLD_POWER_AF%>;
var THRESHOLD_POWER_AT = <%=PseProfile.THRESHOLD_POWER_AT%>;
var THRESHOLD_POWER_AF_RANGE = <%=PseProfile.THRESHOLD_POWER_AF_RANGE%>;
var THRESHOLD_POWER_AT_RANGE = <%=PseProfile.THRESHOLD_POWER_AT_RANGE%>;

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_name").disabled) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(770, 450);
		}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		    if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else if (operation == 'create<s:property value="lstForward"/>' 
				|| operation == 'update<s:property value="lstForward"/>'
				|| operation == 'create' 
				|| operation == 'update'){
				savePseProfile(operation);
			} else {
				if ('<%=Navigation.L2_FEATURE_PSE_PROFILE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			    document.forms[formName].submit();
			}
		</s:if>
		<s:else>
			if ('<%=Navigation.L2_FEATURE_PSE_PROFILE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
	}
}

function savePseProfile(operation) {
	if (validate(operation)) {
		var url = "<s:url action='pseProfile' includeParams='none' />" + "?jsonMode=true" 
		+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["pseProfile"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succPseProfile, failure : failPseProfile, timeout: 60000}, null);
	}
}

var succPseProfile = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentDomId = details.parentDomID
			if(parentDomId){
				var _parentDomId = parentDomId.split(",");
				if(details.addedId != null && details.addedId != ''){
					if (Object.prototype.toString.call(_parentDomId) == '[object Array]') {
						for (var i=0;i<_parentDomId.length;i++) {
							var parentSelectDom = parent.document.getElementById(_parentDomId[i]);
							if(parentSelectDom != null) {
								if(i==0){
									hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
								} else {
									hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, false);
								}
								
							}
						}
					} else {
						var parentSelectDom = parent.document.getElementById(_parentDomId);
						if(parentSelectDom != null) {
							hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
						}
					}
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failPseProfile = function(o) {
	// do nothing now
}

function validate(operation) {
    if ((operation == 'create<s:property value="lstForward"/>' || operation == 'create') && !validateName()) {
		return false;
	}
	if ('<%=Navigation.L2_FEATURE_PSE_PROFILE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	if(operation == 'update<s:property value="lstForward"/>' 
	|| operation == 'create<s:property value="lstForward"/>'
	|| operation == 'update' 
	|| operation == 'create'){
		var thresholdPower = $('#'+formName+'_dataSource_powerMode option:selected').val();
		var minValue=100;
		var maxValue=100;
		if(thresholdPower ==1){
			maxValue = THRESHOLD_POWER_AF_RANGE;
		} else if(thresholdPower ==2){
			maxValue = THRESHOLD_POWER_AT_RANGE;
		}
		var inputElement = document.getElementById(formName + "_dataSource_thresholdPower");
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.pse.threshold.power" />', minValue, maxValue);
		if (message != null) {
			hm.util.reportFieldError(inputElement, message);
			inputElement.focus();
			return false;
		}
	}

	return true;
}

function validateName() {
    var inputElement = document.getElementById(formName + "_dataSource_name");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.pse.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function changePowerMode(value){
	var text ='';
	var node ='';
	var thresholdPower = document.getElementById(formName+'_dataSource_thresholdPower');
	if(value==1){ //802.3af
		text = '<s:text name="config.pse.threshold.power.af.range"/>';
		thresholdPower.value=THRESHOLD_POWER_AF;
		node  = '<s:text name="gotham_21.config.pse.threshold.power.range.note"><s:param>'+THRESHOLD_POWER_AF+'</s:param></s:text>';
	} else if(value==2) { //802.3at
		text = '<s:text name="config.pse.threshold.power.at.range"/>';
		thresholdPower.value=THRESHOLD_POWER_AT;
		//node  = '<s:text name="gotham_21.config.pse.threshold.power.range.note"><s:param>'+THRESHOLD_POWER_AT+'</s:param></s:text>';
	}
	
	Get('thresholdPowerRangeId').innerHTML = text;
	$("#powerLimitNoteInfo").html(node);
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="pseProfile" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
	<s:form action="pseProfile" id="pseProfile">
		<s:if test="%{jsonMode == true}">
			<s:hidden name="id" />
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<s:hidden name="contentShowType" />
			<s:hidden name="parentDomID" />
			<s:hidden name="parentIframeOpenFlg" />
			<div class="topFixedTitle">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td style="padding: 10px 10px 10px 10px">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td align="left">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><img
													src="<s:url value="/images/hm_v2/profile/HM-icon-PSE.png" includeParams="none"/>"
													width="40" height="40" alt="" class="dblk" /></td>
												<td class="dialogPanelTitle"><s:if
														test="%{dataSource.id == null}">
														<s:text name="config.pse.title" />
													</s:if> <s:else>
														<s:text name="config.pse.title.edit" />
													</s:else></td>
												<td style="padding-left: 10px;"><a
													href="javascript:void(0);"
													onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');">
														<img
														src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
														alt="" class="dblk" />
												</a></td>
											</tr>
										</table>
									</td>
									<td align="right">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="npcButton"><a href="javascript:void(0);"
																class="btCurrent"
																onClick="submitAction('cancel<s:property value="lstForward"/>');"
																title="<s:text name="common.button.cancel"/>"><span
																	style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text
																			name="common.button.cancel" /></span></a></td>
															<td width="20px">&nbsp;</td>
															<s:if test="%{dataSource.id == null}">
																<s:if test="%{updateDisabled == 'disabled'}">
																	<td class="npcButton"></td>
																</s:if>
																<s:else>
																	<td class="npcButton"><a
																		href="javascript:void(0);" class="btCurrent"
																		onClick="submitAction('create<s:property value="lstForward"/>');"
																		title="<s:text name="common.button.save"/>"><span
																			style="padding-bottom: 2px; padding-top: 2px;"><s:text
																					name="common.button.save" /></span></a></td>
																</s:else>
															</s:if>
															<s:else>
																<s:if test="%{updateDisabled == 'disabled'}">
																	<td class="npcButton"></td>
																</s:if>
																<s:else>
																	<td class="npcButton"><a
																		href="javascript:void(0);" class="btCurrent"
																		onClick="submitAction('update<s:property value="lstForward"/>');"
																		title="<s:text name="common.button.save"/>"><span
																			style="padding-bottom: 2px; padding-top: 2px;"><s:text
																					name="common.button.save" /></span></a></td>
																</s:else>
															</s:else>
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
		</s:if>
		<s:if test="%{jsonMode==false}">
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
										<s:property value="updateDisabled" />></td>
								</s:if>
								<s:else>
									<td><input type="button" name="ignore"
										value="<s:text name="button.update"/>" class="button"
										onClick="submitAction('update<s:property value="lstForward"/>');"
										<s:property value="updateDisabled" />></td>
								</s:else>
								<s:if test="%{lstForward == null || lstForward == ''}">
									<td><input type="button" name="cancel" value="Cancel"
										class="button"
										onClick="submitAction('<%=Navigation.L2_FEATURE_PSE_PROFILE%>');">
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
			</table>
		</s:if>
		<s:if test="%{jsonMode == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				class="topFixedTitle">
				</s:if>
				<s:else>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						</s:else>
						<tr>
							<td><tiles:insertDefinition name="notes" /></td>
						</tr>
						<tr>
							<td style="padding-top: 5px;">
								<table class="editBox" border="0" cellspacing="0"
									cellpadding="0" width="660">
									<tr>
										<td height="10">
											<%-- add this password dummy to fix issue with auto complete function --%>
											<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
											type="password">
										</td>
									</tr>
									<tr>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="145"><s:text
															name="config.pse.name" /><font color="red"><s:text
																name="*" /></font></td>
													<td><s:textfield name="dataSource.name" size="24"
															onkeypress="return hm.util.keyPressPermit(event,'name');"
															maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
															name="config.ipFilter.name.range" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="145"><s:text
															name="config.pse.description" /></td>
													<td><s:textfield name="dataSource.description"
															size="48" maxlength="%{descriptionLength}" />&nbsp;<s:text
															name="config.ipFilter.description.range" /></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="145"><s:text
															name="config.pse.powerMode" /></td>
													<td><s:select name="dataSource.powerMode"
															list="%{enumPowerMode}" listKey="key" listValue="value"
															onchange="changePowerMode(this.value)"
															cssStyle="width: 100px;" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="145"><s:text
															name="config.pse.threshold.power" /></td>
													<td><s:textfield name="dataSource.thresholdPower"
															maxlength="5" size="5" 
															onkeypress="return hm.util.keyPressPermit(event,'ten');"/> 
															<label id="thresholdPowerRangeId"><s:property value="%{dataSource.thresholdPowerRange}"/></label></td>
												</tr>
												<tr>
													<td colspan="2" style="padding-left: 10px;" class="noteInfo" id="powerLimitNoteInfo">
														<s:property value="powerLimitNote"/>
													</td>
												</tr>
												<tr>
													<td class="labelT1" width="145"><s:text name="config.pse.priority" /></td>
													<td><s:select name="dataSource.priority"
															list="%{enumPriority}" listKey="key" listValue="value"
															cssStyle="width: 100px;" /></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</s:form>
			</div>