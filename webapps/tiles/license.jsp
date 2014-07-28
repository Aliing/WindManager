<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'license';
var sendPanel;

function onLoadEvent() {
	var td = document.getElementById('filler');
	<s:if test="%{canImportLicense}">
	if ('none' == '<s:property value="%{activationDisplay}"/>') {
		<s:if test="%{showTwoSystemId}">
			td.height = 380;
		</s:if>
		<s:else>
			if ('none' == '<s:property value="%{oldLicenseDisplay}"/>') {
				td.height = 210;
			} else {
				td.height = 300;
			}
		</s:else>
	} else {
		<s:if test="%{showTwoSystemId}">
			td.height = 630;
		</s:if>
		<s:else>
			td.height = 390;
		</s:else>
	}
	</s:if>
	<s:else>
		td.height = 120;
	</s:else>
	/*
	if ('activate' == document.forms[formName].operation.value) {
		showActivation(true);
	}
	**/
	//Create send email Overlay
	var div = document.getElementById('sendPanel');
	sendPanel = new YAHOO.widget.Panel(div, { width:"390px",visible:false,draggable:true,constraintoviewport:true,modal:true } );
	var code = document.getElementById('title');
	sendPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code));
	sendPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 20);
	sendPanel.render();
	div.style.display = "";
	
	onLoadNotes();
}
function onUnloadEvent() {
	onUnloadNotes();
}
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}
function validate(operation) {
	if ('import' == operation) {
		var primary = document.forms[formName].primaryLicense;
		var errorTitle = '<s:text name="license.key" />';
		<s:if test="%{showTwoSystemId}">
			errorTitle = 'primary <s:text name="license.key" />';
		</s:if>
		if (primary.value.length == 0) {
			hm.util.reportFieldError(primary,'<s:text name="error.requiredField"><s:param>'+errorTitle+'</s:param></s:text>');
			primary.focus();
			return false;
		}
		<s:if test="%{showTwoSystemId}">		
			var second = document.forms[formName].secondaryLicense;
			if (second.value.length == 0) {
				hm.util.reportFieldError(second, '<s:text name="error.requiredField"><s:param>secondary <s:text name="license.key"/></s:param></s:text>');
				second.focus();
				return false;
			}
		</s:if>
	}
	if ('install' == operation) {
		var primary;
		if ('none' == '<s:property value="%{oldLicenseDisplay}"/>') {
			primary = document.getElementById("primaryOrderKey_1");
		} else {
			primary = document.getElementById("primaryOrderKey_2");
		}
		
		if (!validateActKey(primary, '<s:text name="order.key" />')) {
			return false;
		}
		document.forms[formName].primaryOrderKey.value = primary.value;
	}
	if ('activate' == operation) {
		var errorTitle = '<s:text name="license.activation.key.title" />';
		<s:if test="%{showTwoSystemId}">
			<s:if test="%{showPrimaryActKey}">
				errorTitle = 'primary <s:text name="license.activation.key.title" />';
				if (!validateActKey(document.forms[formName].primaryActKey, errorTitle)) {
					return false;
				}
			</s:if>
			<s:if test="%{showSecondaryActKey}">
				if (!validateActKey(document.forms[formName].secondaryActKey, 'secondary <s:text name="license.activation.key.title" />')) {
					return false;
				}
			</s:if>
		</s:if>
		<s:else>
			if (!validateActKey(document.forms[formName].primaryActKey, errorTitle)) {
				return false;
			}
		</s:else>	
	}
	if ('sendEmail' == operation) {
		var orderKeyEle = document.getElementById("orderId");
		if (orderKeyEle.value.length == 0) {
			hm.util.reportFieldError(orderKeyEle, '<s:text name="error.requiredField"><s:param><s:text name="license.activation.key.order.key" /></s:param></s:text>');
			orderKeyEle.focus();
			return false;
		}
	}
	return true;
}

function validateActKey(activeValue, title) {
	if (activeValue.value.length == 0) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		activeValue.focus();
		return false;
	}
	var subActive = activeValue.value.trim().split("-");
	if (6 != subActive.length) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		activeValue.focus();
		return false;
	}
	for (var i = 0; i < subActive.length; i++) {
		if(subActive[i].length != 5 || !hm.util.validateActivationKeyString(subActive[i])) {
			hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
			activeValue.focus();
        	return false;
		}
	}
	return true;
}

function changeSendPanel(checked) {
	if (checked) {
		document.getElementById("orderId").value = "";
		<s:if test="%{!hasConfigEmail}">
			var warnDialog =
			     new YAHOO.widget.SimpleDialog("warnDialog",
			              { width: "350px",
			                fixedcenter: true,
			                visible: false,
			                draggable: true,
			                modal:true,
			                close: true,
			                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
			                constraintoviewport: true,
			                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
			              } );
			warnDialog.setHeader("Warning");
			warnDialog.render(document.body);
			warnDialog.cfg.setProperty('text', '<s:text name="error.license.activation.key.send.email"/>');
			warnDialog.show();
			return;
		</s:if>
	}
	if (sendPanel != null) {
		sendPanel.cfg.setProperty('visible', checked);
	}
}

var handleNo = function() {
    this.hide();
};

function getOrImport(ifGet) {
	document.getElementById("emailMessage").style.display = ifGet ? "" : "none";
	document.getElementById("importMessage").style.display = ifGet ? "none" : "";
}

/*
function showActivation(ifShow) {
	document.getElementById("plusActive").style.display= ifShow ? "none" : "";
	document.getElementById("minusActive").style.display= ifShow ? "" : "none";
	document.getElementById("detailActive").style.display= ifShow ? "" : "none";
}
**/

</script>

<style>
.login {
	font-size: 11pt;
}
</style>
<s:form action="license">
	<s:hidden name="operation" />
	<s:hidden name="primaryOrderKey" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="100" alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td style="padding: 4px 0 0 4px" align="center" valign="middle">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><img
						src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
					<td rowspan="2" class="menu_bg" style="padding: 6px 0 0 0;"
						valign="top" width="640" align="left">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr height="5">
						</tr>
						<tr>
							<td class="crumb" colspan="2"><s:text
								name="admin.license.licenseInfo"><s:param><s:property value="%{systemNmsName}"/></s:param></s:text></td>
						</tr>
						<tr>
							<td style="padding: 4px 0 12px 0;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td class="sepLine" width="100%"><img
										src="<s:url value="/images/spacer.gif" includeParams="none"/>"
										height="1" class="dblk" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td align="left">
							<table border="0" cellspacing="0" cellpadding="0">
								<s:if test="%{!showTwoSystemId}">
									<tr>
										<td class="login" colspan="2" style="padding-left: 4px;">
											<s:property value="%{hmIdInfo}" /></td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
								</s:if>
								<s:else>
									<tr>
										<td class="login" colspan="2" style="padding-left: 4px;"><s:property value="%{primarySystemId}" /></td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td class="login" colspan="2" style="padding-left: 4px;"><s:property value="%{secondarySystemId}" /></td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
								</s:else>
								<tr>
									<td align="left">
									<table border="0" cellspacing="0" cellpadding="0" width="530"
										class="note">
										<tr>
											<td height="4"></td>
										</tr>
										<tr>
											<td class="noteInfo"><font color="red"><s:property
												value="%{licenseMessage}" escape="false" /></font></td>
										</tr>
										<tr>
											<td height="4"></td>
										</tr>
									</table>
									</td>
									<td style="display:<s:property value="%{inforDisplay}"/>"
										align="left" valign="middle">&nbsp;
										<input type="button" name="ignore" value="Continue" class="button"
										onclick="submitAction('continue');" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr height="5">
						</tr>
						<tr>
							<td style="padding: 0 0 8px 0px" colspan="2">
								<tiles:insertDefinition name="notes" />
							</td>
						</tr>
						<s:if test="%{canImportLicense}">
						<tr>
							<td style="padding: 0 5px 8px 5px" colspan="2" class="noteInfo">
								<s:text name="license.get.entitlement.key.note"><s:param><s:property value="%{titleParam}" />
								</s:param><s:param><s:property value="%{emailMessage}" /></s:param><s:param><s:property value="%{orderEmail}" /></s:param></s:text>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table>
									<s:if test="%{!oEMSystem}">
									<tr style="display:<s:property value="%{oldLicenseDisplay==''?'none':''}" />">
										<td>
											<table>
												<tr>
													<td style="padding-left:15px" width="95px"><s:text name="order.key" /><font color="red"><s:text name="*" /></font></td>
													<td><s:textfield id="primaryOrderKey_1" value="%{primaryOrderKey}" maxlength="41" cssStyle="width:400px"
														onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
													<td><input type="button" name="ignore" value="Enter" class="button" onclick="submitAction('install');" /></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{oldLicenseDisplay}" />">
										<td>
											<table>
												<tr>
													<td class="login"><s:radio label="Gender" name="radioMethod" list="%{radioEmailMessage}" listKey="key" listValue="value"
													onclick="getOrImport(true);" /></td>
												</tr>
												<tr id="emailMessage" style="display:<s:property value="%{radioMethod==1?'':'none'}" />">
													<td style="padding-left:5px">
														<table>
															<tr>
																<td style="padding-left:10px" width="95px"><s:text name="order.key" /><font color="red"><s:text name="*" /></font></td>
																<td><s:textfield id="primaryOrderKey_2" value="%{primaryOrderKey}" maxlength="41" cssStyle="width:400px"
																	onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
																<td><input type="button" name="ignore" value="Enter" class="button" onclick="submitAction('install');" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td class="login"><s:radio label="Gender" name="radioMethod" list="%{radioImportMessage}" listKey="key" listValue="value"
													onclick="getOrImport(false);" /></td>
												</tr>
												<tr id="importMessage" style="display:<s:property value="%{radioMethod==2?'':'none'}" />">
													<td style="padding-left:5px">
														<table>
															<tr>
																<td align="left">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<s:if test="%{showTwoSystemId}">
																			<tr>
																				<td height="2"></td>
																			</tr>
																			<tr>
																				<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{primarySystemId}" /></td>
																			</tr>
																			<tr>
																				<td height="2"></td>
																			</tr>
																			<tr>
																				<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
																				<td rowspan="2"><s:textarea name="primaryLicense" value="%{primaryLicense}" cssStyle="width:400px" rows="2" /></td>
																			</tr>
																			<tr>
																				<td height="2"></td>
																			</tr>
																			<tr>
																				<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{secondarySystemId}" /></td>
																			</tr>
																			<tr>
																				<td height="2"></td>
																			</tr>
																			<tr>
																				<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
																				<td rowspan="2"><s:textarea name="secondaryLicense" value="%{secondaryLicense}" cssStyle="width:400px" rows="2" />&nbsp;&nbsp;</td>
																				<td rowspan="2" valign="middle"><input type="button" name="ignore" value="Install" class="button"
																					onclick="submitAction('import');" /></td>
																			</tr>
																		</s:if>
																		<s:else>
																			<tr>
																				<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
																				<td rowspan="2"><s:textarea name="primaryLicense" value="%{primaryLicense}" cssStyle="width:400px" rows="2" />&nbsp;&nbsp;</td>
																				<td rowspan="2" valign="middle"><input type="button" name="ignore" value="Install" class="button"
																					onclick="submitAction('import');" /></td>
																			</tr>
																		</s:else>
																	</table>
																</td>
															</tr>
															<tr>
																<td height="4"></td>
															</tr>
															<tr style="display:<s:property value="%{activationDisplay}"/>">
																<td align="left">
																<fieldset><legend><s:text
																		name="license.activation.key.information" /></legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" colspan="3"><font color="red"><s:property
																			value="%{activationMessage}" escape="false" /></font></td>
																	</tr>
																	<s:if test="%{showTwoSystemId}">
																		<s:if test="%{showPrimaryActKey}">
																		<tr>
																			<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{primarySystemId}" /></td>
																		</tr>
																		<tr>
																			<td height="2"></td>
																		</tr>
																		<tr>
																			<td class="labelT1" width="93px"><s:text name="license.activation.key.title" /></td>
																			<td><s:textfield name="primaryActKey" value="%{primaryActKey}" maxlength="41" cssStyle="width:400px"
																				onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
																			<s:if test="%{!showSecondaryActKey}">
																				<td><input type="button" name="ignore" value="Activate" class="button"
																					onclick="submitAction('activate');" /></td>
																			</s:if>
																		</tr>
																		<tr>
																			<td height="2"></td>
																		</tr>
																		</s:if>
																		<s:if test="%{showSecondaryActKey}">
																			<tr>
																				<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{secondarySystemId}" /></td>
																			</tr>
																			<tr>
																				<td height="2"></td>
																			</tr>
																			<tr>
																				<td class="labelT1" width="93px"><s:text name="license.activation.key.title" /></td>
																				<td><s:textfield name="secondaryActKey" value="%{secondaryActKey}" maxlength="41" cssStyle="width:400px"
																					onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
																				<td><input type="button" name="ignore" value="Activate" class="button"
																					onclick="submitAction('activate');" /></td>
																			</tr>
																		</s:if>
																	</s:if>
																	<s:else>
																		<tr>
																			<td class="labelT1" width="93px"><s:text name="license.activation.key.title" /></td>
																			<td><s:textfield name="primaryActKey" value="%{primaryActKey}" maxlength="41" cssStyle="width:400px"
																				onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
																			<td><input type="button" name="ignore" value="Activate" class="button"
																				onclick="submitAction('activate');" /></td>
																		</tr>	
																	</s:else>					
																</table>
																</fieldset>
																</td>
															</tr>
															<tr>
																<td height="4"></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									</s:if>
									<s:else>
										<tr>
											<td align="left">
												<table border="0" cellspacing="0" cellpadding="0">
													<s:if test="%{showTwoSystemId}">
														<tr>
															<td height="2"></td>
														</tr>
														<tr>
															<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{primarySystemId}" /></td>
														</tr>
														<tr>
															<td height="2"></td>
														</tr>
														<tr>
															<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
															<td rowspan="2"><s:textarea name="primaryLicense" value="%{primaryLicense}" cssStyle="width:400px" rows="2" /></td>
														</tr>
														<tr>
															<td height="2"></td>
														</tr>
														<tr>
															<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{secondarySystemId}" /></td>
														</tr>
														<tr>
															<td height="2"></td>
														</tr>
														<tr>
															<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
															<td rowspan="2"><s:textarea name="secondaryLicense" value="%{secondaryLicense}" cssStyle="width:400px" rows="2" />&nbsp;&nbsp;</td>
															<td rowspan="2" valign="middle"><input type="button" name="ignore" value="Install" class="button"
																onclick="submitAction('import');" /></td>
														</tr>
													</s:if>
													<s:else>
														<tr>
															<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
															<td rowspan="2"><s:textarea name="primaryLicense" value="%{primaryLicense}" cssStyle="width:400px" rows="2" />&nbsp;&nbsp;</td>
															<td rowspan="2" valign="middle"><input type="button" name="ignore" value="Install" class="button"
																onclick="submitAction('import');" /></td>
														</tr>
													</s:else>
												</table>
											</td>
										</tr>
									</s:else>
								</table>
							</td>
						</tr>
						</s:if>
					</table>
					</td>
					<td><img
						src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td id="filler" class="menu_bg" height="360"></td>
					<td class="menu_bg"></td>
				</tr>
				<tr>
					<td><img
						src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
					<td class="menu_bg" height="1"><img
						src="<s:url value="/images/spacer.gif" includeParams="none"/>"
						width="100%" height="1" alt="" class="dblk" /></td>
					<td><img
						src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<div id="sendPanel" style="display: none;">
		<div class="hd"><s:text name="license.get.entitlement.key.email.title" /></div>
		<div class="bd">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="370px">
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td class="labelT1" width="95px"><s:text name="license.activation.key.order.key" /><font color="red"><s:text name="*" /></font></td>
					<td><s:textfield id="orderId" name="orderId" value="%{orderId}" maxlength="32" size="42" />
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Send"
								class="button" onClick="submitAction('sendEmail');"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="changeSendPanel(false);"></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
			</table>
		</div>
		</div>
</s:form>
