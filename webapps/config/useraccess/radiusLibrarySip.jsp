<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.RadiusLibrarySipRule"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css" includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>

<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding: 10px;
}

#calendarpicker button {
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

var navConfig = {   
	strings : {   
		month: "Choose Month",   
		year: "Enter Year",   
		submit: "OK",   
		cancel: "Cancel",   
		invalidYear: "Please enter a valid year"  
	},   
	monthFormat: YAHOO.widget.Calendar.SHORT,   
	initialFocus: "year"  
};   

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
            oCalendar.cfg.setProperty("navigator", navConfig);
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
                    var beginDate_doc = document.getElementById("fieldTime");
                    beginDate_doc.value = aDate[0] + "-" + formatValue(aDate[1]) + "-" + formatValue(aDate[2]);
                }
                oCalendarMenu.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick);

        };

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

        // Create a Button instance of type "menu"
        var syncTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "fieldTimeDiv" });

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        syncTimeButton.on("click", onButtonClick);
	});
</script>
<script>
var formName = 'librarySip';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(900, 760);
	}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_RADIUS_LIBRARY_SIP%>' == operation || operation == 'newUserGroup' || operation == 'newDefUserGroup'
	 	|| operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	if(operation == "editUserGroup" || operation == "editDefUserGroup"){
		var value = operation == "editUserGroup" ? hm.util.validateListSelection("groupList")
				: hm.util.validateListSelection("defGroupId");
		if(value < 0){
			return false
		}
	}

	var feChild = document.getElementById("checkAll");
	if (operation == 'addPolicyRules') {
		// max items
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 64) {
			hm.util.reportFieldError(feChild, '<s:text name="error.objectReachLimit"></s:text>');
			return false;
		}
		
		// sip field
		var fieldSel = document.getElementById("myFieldSelect");
		var fieldValue = document.forms[formName].field;
		var showError = document.getElementById("errorDisplay");
		if ("" == fieldValue.value) {
	        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radius.library.sip.policy.rule.field" /></s:param></s:text>');
	        fieldValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(fieldSel, fieldValue)) {
			if (fieldValue.value.length == 2) {
				if (!hm.util.validateSipFieldString(fieldValue.value)) {
					hm.util.reportFieldError(showError, '<s:text name="error.config.auth.radius.library.sip.field.value" />');
			        fieldValue.focus();
			        return false;
				}
				document.forms[formName].fieldId.value = -1;
			} else {
				hm.util.reportFieldError(showError, '<s:text name="error.config.auth.radius.library.sip.value.length"><s:param><s:text name="config.radius.library.sip.policy.rule.field" /></s:param><s:param>2</s:param></s:text>');
			    fieldValue.focus();
				return false;
			}
		} else {
			document.forms[formName].fieldId.value = fieldSel.options[fieldSel.selectedIndex].value;
		}
		
		// sip operation
		var sipTr = document.getElementById("radiusLibrarySipOperation");
		var operIndex = sipTr.options[sipTr.selectedIndex].value;
		
		// equal, greater than, less than
		if (operIndex == <%=RadiusLibrarySipRule.SIP_OPERATOR_EQUAL%> || operIndex == <%=RadiusLibrarySipRule.SIP_OPERATOR_GREATER_THAN%>
			|| operIndex == <%=RadiusLibrarySipRule.SIP_OPERATOR_LESS_THAN%>) {
			if (document.getElementById("valueNumber").style.display == "") {
				var valueInt = document.forms[formName].valueInt;
				var message = hm.util.validateIntegerRange(valueInt.value, '<s:text name="config.radius.library.sip.policy.rule.value" />', 0, 65535);
			    if (message != null) {
			        hm.util.reportFieldError(valueInt, message);
			        valueInt.focus();
			        return false;
			    }
			}
		// not occur before or after
		} else if (operIndex != <%=RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_AFTER%> && operIndex != <%=RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_BEFORE%>) {
			// sip value
			var fId = document.forms[formName].fieldId.value;

			// must input value (AA, AE, AO)
			if (fId == 1 || fId == 2 || fId == 5) {
				var field = document.forms[formName].valueStr;
				if (!checkIfInput(field, '<s:text name="config.radius.library.sip.policy.rule.value" />')) {
					return false;
				}
			} else {
				if (document.getElementById("valueText").style.display == "") {
					var valueStr = document.forms[formName].valueStr;
					if (valueStr.value.length > 0) {
						// BZ(hold items limit), CA(overdue items limit), CB(charged items limit)
						if (fId == 17 || fId == 18 || fId == 19) {
							if (valueStr.value.length != 4) {
								hm.util.reportFieldError(valueStr, '<s:text name="error.config.auth.radius.library.sip.value.length"><s:param><s:text name="config.radius.library.sip.policy.rule.value" /></s:param><s:param>4</s:param></s:text>');
					        	valueStr.focus();
								return false;
							}
							var message = hm.util.validateIntegerRange(valueStr.value, '<s:text name="config.radius.library.sip.policy.rule.value" />', 1, 9999);
						    if (message != null) {
						        hm.util.reportFieldError(valueStr, message);
						        valueStr.focus();
						        return false;
						    }
						// BV(fee amount), CC(fee limit)
						} else if (fId == 16 || fId == 20) {
							var floatPat=/^(\+|-)?\d+($|\.\d+$)/;
							if (valueStr.value.match(floatPat) == null) {
								hm.util.reportFieldError(valueStr, '<s:text name="error.formatInvalid"><s:param><s:text name="config.radius.library.sip.policy.rule.value" /></s:param></s:text>');
					        	valueStr.focus();
								return false;
							}
						// BE(e-mail address)
						} else if (fId == 11) {
							if (!hm.util.validateEmail(valueStr.value)) {
								hm.util.reportFieldError(valueStr, '<s:text name="error.formatInvalid"><s:param><s:text name="config.radius.library.sip.policy.rule.value" /></s:param></s:text>');
					        	valueStr.focus();
								return false;
							}
						}
					}
				}
			} 
		}
		
		// user group
		var userGroup = document.forms[formName].groupId;
		if (parseInt(userGroup.value) <= 0) {
            hm.util.reportFieldError(userGroup, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.radius.library.sip.policy.rule.goup" /></s:param></s:text>'); 
            userGroup.focus(); 
			return false;
		}
		
		// message length
		var accessMessage = document.forms[formName].accessMessage;
		if (accessMessage.value.length > 256) {
			hm.util.reportFieldError(accessMessage, '<s:text name="error.config.auth.radius.library.sip.message.length"></s:text>'); 
            accessMessage.focus(); 
			return false;
		}
	} 
	if (operation == 'removePolicyRules' || operation == 'removePolicyRulesNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ipPolicy.rules" /></s:param></s:text>');  
			return false;
		}
	} 
	if (operation == 'create'+'<s:property value="lstForward"/>' 
		|| operation == 'update<s:property value="lstForward"/>'
		|| operation == 'create'
		|| operation == 'update'){
		var name = document.getElementById(formName + "_dataSource_policyName");
		var message = hm.util.validateName(name.value, '<s:text name="config.ipPolicy.policyName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    	var defGroup = document.getElementById("defGroupId");
		if (defGroup.value <= 0) {
            hm.util.reportFieldError(defGroup, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.radius.library.sip.policy.rule.goup" /></s:param></s:text>'); 
            defGroup.focus(); 
			return false;
		}
		
		// default rule message length
		var defMessage = document.getElementById(formName + "_dataSource_defMessage");
		if (defMessage.value.length > 256) {
			hm.util.reportFieldError(defMessage, '<s:text name="error.config.auth.radius.library.sip.message.length"></s:text>'); 
            defMessage.focus(); 
			return false;
		}
    }
	return true;
}

function checkIfInput(inputElement, title)
{
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function changeFieldOperation(fieldOpr) {
	var fieldSel = document.getElementById("myFieldSelect");
	hm.util.radiusLibrarySip(fieldOpr, fieldSel.options[fieldSel.selectedIndex].value, false);
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
	var cbs = document.getElementsByName('ruleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="librarySip" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedPolicyName" />\'</td>');
		</s:else>
	</s:else>	
}
</s:if>
</script>
<div id="content"><s:form action="librarySip">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
		<s:hidden name="id"/>
	</s:if>
	<s:hidden name="fieldId" />
	<s:if test="%{jsonMode == true}">
		<div id="titleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-hiveap-radius-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<td class="dialogPanelTitle">
							<s:if test="%{dataSource.id == null}">
							<s:text name="config.title.radius.library.sip.new"/>
							</s:if>
							<s:else>
							<s:text name="config.title.radius.library.sip.edit"/></s:else>
						</td>
					</tr>
				</table>
				</td>
				<td align="right">
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
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<div style="margin:0 auto; width:100%;">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIUS_LIBRARY_SIP%>');">
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
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellspacing="0" cellpadding="0" border="0">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0">
			</s:else>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="150px"><label><s:text
									name="config.ipPolicy.policyName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="27" name="dataSource.policyName"
									maxlength="%{policyNameLength}" disabled="%{disabledName}" 
									onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.ipPolicy.description" /></label></td>
								<td><s:textfield size="54" name="dataSource.description"
									maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr>
								<td height="2"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:4px 10px 0 10px">
						<fieldset><legend><s:text name="config.ipPolicy.rules" /></legend>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td style="padding:4px 0 10px 5px">
									<fieldset style="width: 600px;"><legend><s:text name="config.radius.library.sip.policy.rule.default" /></legend>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td colspan="2">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labelT1" width="112px">
																<label><s:text name="config.radius.library.sip.policy.rule.goup" /></label><font color="red"><s:text name="*"/></font></td>
															<td><s:select name="defGroupId" cssStyle="width: 300px;" id="defGroupId"
																list="%{localUserGroup}" listKey="id" listValue="value" />
															</td>
															<td>
																<s:if test="%{writeDisabled == 'disabled'}">
																	<img class="dinl marginBtn"
																	src="<s:url value="/images/new_disable.png" />"
																	width="16" height="16" alt="New" title="New" />
																</s:if>
																<s:else>
																	<a class="marginBtn" href="javascript:submitAction('newDefUserGroup')"><img class="dinl"
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
																	<a class="marginBtn" href="javascript:submitAction('editDefUserGroup')"><img class="dinl"
																	src="<s:url value="/images/modify.png" />"
																	width="16" height="16" alt="Modify" title="Modify" /></a>
																</s:else>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="112px"><label><s:text
													name="config.radius.library.sip.policy.rule.action" /><font color="red"><s:text name="*"/></font></label>
												</td>
												<td><s:select name="dataSource.defAction"
													list="%{enumAction}" listKey="key" listValue="value" cssStyle="width: 100px;" /></td>
											</tr>
											<tr>
												<td class="labelT1"><label><s:text
													name="config.radius.library.sip.policy.rule.message" /></label>
												</td>
												<td><s:textarea name="dataSource.defMessage" cssStyle="width: 300px;" rows="2"
													 onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
													<s:text name="config.radiusOnHiveAp.computerOu" /></td>
											</tr>
											<tr>
												<td>&nbsp;</td>
												<td><label><s:text name="config.radius.library.sip.policy.rule.message.des" /></label>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td style="padding:4px 0px 4px 0px;" valign="top">
									<table cellspacing="0" cellpadding="0" border="0" class="embedded">
										<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
											<td colspan="4" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
														class="button" onClick="showCreateSection();"></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="submitAction('removePolicyRules');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
											<td colspan="4" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
														class="button" onClick="submitAction('addPolicyRules');"></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="submitAction('removePolicyRulesNone');"></td>
													<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
														class="button" onClick="hideCreateSection();"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
											<td colspan="4" style="padding-left: 5px;">
											<fieldset style="width: 600px;background-color: #edf5ff">
											<legend><s:text name="config.radius.library.sip.policy.add" /></legend>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td colspan="2">
														<table>
															<tr>
																<td class="labelT1" width="107px">
																	<label>
																		<s:text name="config.radius.library.sip.policy.rule.field" />
																	</label><font color="red"><s:text name="*" /></font>
																</td>
																<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<ah:createOrSelect divId="errorDisplay" list="availableSipFields" swidth="300px"
																		selectIdName="myFieldSelect" inputValueName="field" hideButton="true" />
																	</table>
																</td>
																<td>
																	<s:text name="config.radius.library.sip.policy.rule.field.range" />
																</td>
															</tr>
														</table>
													</td>
													
												</tr>
												<tr>
													<td class="labelT1" width="112px"><label><s:text
														name="config.radius.library.sip.policy.rule.oper" /><font color="red"><s:text name="*"/></font></label>
													</td>
													<td><s:select name="sipOperation" id="radiusLibrarySipOperation"
														list="%{enumOperation}" listKey="key" listValue="value" cssStyle="width: 300px;"
														onchange="changeFieldOperation(this.options[this.selectedIndex].value);" /></td>
												</tr>
												<tr>
													<td class="labelT1"><label><s:text
														name="config.radius.library.sip.policy.rule.value" /></label>
													</td>
													<td id="valueText">
														<s:textfield name="valueStr" onkeypress="return hm.util.keyPressPermit(event,'name');"
														size="48" maxlength="%{policyNameLength}" value="%{valueStr}" />
														<s:text name="config.ns.name.range" />
													</td>
													<td style="display:none" id="valueNumber">
														<s:textfield name="valueInt" onkeypress="return hm.util.keyPressPermit(event,'ten');"
														size="12" maxlength="5" value="%{valueInt}" />
														<s:text name="config.ipAddress.classifier.range" />
													</td>
													<td style="display:none" id="valueList">
														<s:select id="valueListSelect" name="valueSelect" list="%{ruleFieldValue}" listKey="value" listValue="value" cssStyle="width: 100px;" />
													</td>
													<td style="display:none" id="valueTime">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td>
																	<s:textfield name="fieldTimeStr" id="fieldTime" maxlength="10"
																		value="%{fieldTimeStr}" readonly="true" size="10" />
																</td>
																<td>
																	<div id="fieldTimeDiv" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td class="labelT1"><label><s:text name="config.radius.library.sip.policy.rule.goup" /><font color="red"><s:text name="*"/></font></label></td>
													<td><s:select name="groupId" cssStyle="width: 300px;" id="groupList"
														list="%{localUserGroup}" listKey="id" listValue="value" />
														<s:if test="%{writeDisabled == 'disabled'}">
															<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" />
														</s:if>
														<s:else>
															<a class="marginBtn" href="javascript:submitAction('newUserGroup')"><img class="dinl"
															src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
														</s:else>
														<s:if test="%{writeDisabled == 'disabled'}">
															<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
															width="16" height="16" alt="Modify" title="Modify" />
														</s:if>
														<s:else>
															<a class="marginBtn" href="javascript:submitAction('editUserGroup')"><img class="dinl"
															src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
														</s:else>
													</td>
												</tr>
												<tr>
													<td class="labelT1"><label><s:text
														name="config.radius.library.sip.policy.rule.action" /><font color="red"><s:text name="*"/></font></label>
													</td>
													<td><s:select name="accessAction"
														list="%{enumAction}" listKey="key" listValue="value" cssStyle="width: 100px;" /></td>
												</tr>
												<tr>
													<td class="labelT1"><label><s:text
														name="config.radius.library.sip.policy.rule.message" /></label>
													</td>
													<td><s:textarea name="accessMessage" cssStyle="width: 300px;" rows="2"
														 onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
														<s:text name="config.radiusOnHiveAp.computerOu" /></td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td><label><s:text
														name="config.radius.library.sip.policy.rule.message.des" /></label>
													</td>
												</tr>
											</table>
											</fieldset>
											</td>
										</tr>
										<tr id="headerSection">
											<th align="left" style="padding-left: 0;" width="10"><input
												type="checkbox" id="checkAll"
												onClick="toggleCheckAllRules(this);"></th>
											<th width="50"><s:text
												name="config.ipPolicy.ruleId" /></th>
											<th width="100"><s:text
												name="config.radius.library.sip.policy.rule.field" /></th>
											<th width="600"><s:text
												name="config.radius.library.sip.policy.rule.description.title" /></th>
										</tr>
										<s:if test="%{gridCount > 0}">
											<s:generator separator="," val="%{' '}" count="%{gridCount}">
												<s:iterator>
													<tr>
														<td class="list" colspan="4">&nbsp;</td>
													</tr>
												</s:iterator>
											</s:generator>
										</s:if>	
										<tr>
											<td valign="top" colspan="4">
												<table cellspacing="0" cellpadding="0" border="0"
													class="embedded" id="policyTable">
													<s:iterator value="%{dataSource.rules}" status="status">
														<tr class="list">
															<td class="listCheck" width="10"><s:checkbox name="ruleIndices"
																fieldValue="%{#status.index}" /></td>
															<td class="list" width="50" align="center">
																<b><s:property value="ruleId" /></b></td>
															<td class="list" width="100" align="center">
																<b><s:property value="field" /></b></td>
															<td class="list" width="600" align="center">
																<s:property value="ruleDescription" escape="false" />
																<s:hidden name="ordering" value="%{#status.index}" /></td>
														</tr>
													</s:iterator>						
												</table>
											</td>
											<s:if test="%{dataSource.rules.size() > 1}">
												<td valign="top" style="padding: 0px 0px 0 10px;">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td><input type="button" class="moveRow" value="Up"
																onclick="hm.util.moveRowsUp('policyTable');" /></td>
														</tr>
														<tr>
															<td><input type="button" class="moveRow" value="Down"
																onclick="hm.util.moveRowsDown('policyTable');" /></td>
														</tr>
														<s:if test="%{dataSource.rules.size() > 15}">
														<s:generator separator="," val="%{' '}" count="%{dataSource.rules.size()-2}">
															<s:iterator>
																<tr>
																	<td>&nbsp;</td>
																</tr>
															</s:iterator>
														</s:generator>
															<tr>
																<td><input type="button" class="moveRow" value="Up"
																	onclick="hm.util.moveRowsUp('policyTable');" /></td>
															</tr>
															<tr>
																<td><input type="button" class="moveRow" value="Down"
																	onclick="hm.util.moveRowsDown('policyTable');" /></td>
															</tr>
														</s:if>
													</table>
												</td>
											</s:if>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
			</table>
		</td>
		</tr>
	</table>
	<s:if test="%{jsonMode == true}">
		</div>
	</s:if>
</s:form></div>