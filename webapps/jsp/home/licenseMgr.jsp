<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style type="text/css">
table.view tr.normalKeyRow{
}

.expiredEntitleKeyLabel {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-style: normal;
	font-weight: bold;
	color: #FF3300;
	background-color: #335779;
	padding: 2px 4px;
	margin-top: 1px;
}
</style>
<script>
var formName = 'licenseMgr';
var deviceInfoPanel;

function onLoadPage() {
	<s:if test="%{warnToEnableAcm}">
		showWarnDialog('<s:text name="glasgow_14.info.acm.entitlement.key.enable.acm.first" />');
	</s:if>
	
	//Create device inventory detail Overlay
	var div = document.getElementById('deviceInfoPanel');
	deviceInfoPanel = new YAHOO.widget.Panel(div, { width:"200px",visible:false,draggable:false,constraintoviewport:true } );
	deviceInfoPanel.render(document.body);
	div.style.display = "";
}

function submitAction(operation) 
{
	if (validate(operation)) 
	{
		<s:if test="%{oldLicenseDisplay == ''}">
			Get(formName + "_licenseStyle").value = Get("licenseStrDiv").style.display;
		</s:if>
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
		if ("export_orderkey" != operation) {
			showProcessing();
		}
	}
}

function validate(operation) 
{
	if (operation == 'import') {
		var errorTitle = '<s:text name="license.key" />';
		<s:if test="%{showTwoSystemId}">
			errorTitle = 'primary <s:text name="license.key" />';
		</s:if>
		var licenseFile = document.forms[formName].primaryLicense;
		if (licenseFile.value.length ==0 )
		{
			hm.util.reportFieldError(licenseFile, '<s:text name="error.requiredField"><s:param>'+errorTitle+'</s:param></s:text>');
			showHideContent("licenseStrDiv","");
	        licenseFile.focus();
	        return false;
		}
		<s:if test="%{showTwoSystemId}">
			var licenseFile = document.forms[formName].secondaryLicense;
			if (licenseFile.value.length ==0 )
			{
				hm.util.reportFieldError(licenseFile, '<s:text name="error.requiredField"><s:param>secondary <s:text name="license.key" /></s:param></s:text>');
				showHideContent("licenseStrDiv","");
		        licenseFile.focus();
		        return false;
			}
		</s:if>
	}
	if ('activate' == operation) {
		var errorTitle = '<s:text name="license.activation.key.title" />';
		<s:if test="%{showTwoSystemId}">
			errorTitle = 'primary <s:text name="license.activation.key.title" />';
		</s:if>
		if (!validateActKey(document.forms[formName].primaryActKey, errorTitle)) {
			return false;
		}
		<s:if test="%{showTwoSystemId}">
			if (!validateActKey(document.forms[formName].secondaryActKey, 'secondary <s:text name="license.activation.key.title" />')) {
				return false;
			}
		</s:if>
	}
	if(operation == 'activate_orderkey'){
		if (!validateActKey(document.forms[formName].primaryOrderKey, '<s:text name="order.key" />')) {
			return false;
		}
	}
	return true;
}

function validateActKey(activeValue, title) {
	if (activeValue.value.length == 0) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if ('activate' == operation) {
			showHideContent("licenseStrDiv","");
		}
		activeValue.focus();
		return false;
	}
	var subActive = activeValue.value.trim().split("-");
	if (6 != subActive.length) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if ('activate' == operation) {
			showHideContent("licenseStrDiv","");
		}
		activeValue.focus();
		return false;
	}
	for (var i = 0; i < subActive.length; i++) {
		if(subActive[i].length != 5 || !hm.util.validateActivationKeyString(subActive[i])) {
			hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
			if ('activate' == operation) {
				showHideContent("licenseStrDiv","");
			}
			activeValue.focus();
        	return false;
		}
	}
	return true;
}

function getDeviceInventoryDetail() {
	var code = document.getElementById("deviceInventoryTd");
	deviceInfoPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code) + 180);
	deviceInfoPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 40);
	
	deviceInfoPanel.cfg.setProperty('visible', true);
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}
</script>

<div id="content"><s:form action="licenseMgr">
	<s:hidden name="licenseStyle" />
	<s:hidden name="userRegInfoStyle" />
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="4"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="900px">
				<tr>
					<td style="padding: 10px 10px 10px 10px">
					<fieldset><legend><s:text name="admin.license.licenseInfo"><s:param><s:property value="%{systemNmsName}"/></s:param>
						</s:text></legend>
					<div>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1"><s:property
								value="%{orderKeyInfoByStr}" escape="false" /></td>
							<td class="labelT1" style="padding-left:50px" id="deviceInventoryTd"><s:property
								value="%{deviceInfoByStr}" escape="false" /></td>
							<s:if test="%{canImportLicense && page.size() > 0}">
								<td class="labelT1" style="padding-left:30px">
								<s:text name="admin.license.orderkey.export.button.title" />
								<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" name="ignoreE" value="<s:text name="admin.license.orderkey.export.button.txt" />"
								class="button" onclick="submitAction('export_orderkey');" /></td>
							</s:if>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<!--<s:if test="%{canImportLicense}">-->
					<tr>
						<td style="padding: 0px 10px 10px 10px">
						<fieldset><legend><s:text
							name="admin.license.orderKey" /></legend>
						<div>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="5px"></td>
							</tr>
							<tr>
								<td class="labelT1" width="110px"><s:text name="order.key" /><font color="red"><s:text name="*" /></font></td>
								<td><s:textfield name="primaryOrderKey" value="%{primaryOrderKey}"
									maxlength="41" cssStyle="width:450px"
									onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
								<td><input type="button" name="ignore" value="<s:text name="admin.license.orderkey.export.enter.txt" />"
									class="button" onclick="submitAction('activate_orderkey');" /></td>
								
							</tr>
						</table>
						</div>
						</fieldset>
						</td>
					</tr>
					<tr>
						<td style="padding: 0px 10px 10px 10px">
						<fieldset><legend><s:text name="admin.license.historyOrderKey" /></legend>
						<div>
						<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
							<tr>
								<th align="left" nowrap><s:text name="order.key" /></th>
								<th align="left" nowrap><s:text name="admin.license.orderKey.type" /></th>
								<th align="left" nowrap><s:text name="admin.license.orderKey.support.ap" /></th>
								<s:if test="%{!isInHomeDomain && !oEMSystem}">
									<th align="left" nowrap><s:text name="admin.license.orderKey.subscription.end" /></th>
								</s:if>
								<th align="left" nowrap><s:text name="admin.license.orderKey.support.cvg" /></th>
								<s:if test="%{isInHomeDomain}">
									<th align="left" nowrap><s:text name="admin.license.orderKey.support.vhm" /></th>
								</s:if>
								<s:else>
									<s:if test="%{!oEMSystem}">
										<th align="left" nowrap><s:text name="admin.license.orderKey.subscription.end" /></th>
									</s:if>
								</s:else>
								<s:if test="%{!oEMSystem}">
									<th align="left" nowrap><s:text name="admin.license.orderKey.support.end" /></th>
								</s:if>
								<th align="left" nowrap><s:text name="admin.license.orderKey.support.time" /></th>
								<th align="left" nowrap><s:text name="admin.license.orderKey.support.active" /></th>
							</tr>
							<s:if test="%{page.size() == 0}">
								<ah:emptyList />
							</s:if>
							<s:iterator value="page" status="status">
								<tiles:insertDefinition name="rowClass" />
								<s:if test="%{statusFlag == 1 && cvgStatusFlag == 1}">
									<s:set name="statusClass" value="%{'normalKeyRow'}" />
								</s:if>
								<s:elseif test="%{statusFlag == 2 || cvgStatusFlag == 2}">
									<s:if test="%{#statusClass != 'disableKeyRow'}">
										<tr><td class="expiredEntitleKeyLabel" colspan="10"><s:text name="activate.order.key"><s:param>Invalid</s:param></s:text></td></tr>
									</s:if>
									<s:set name="statusClass" value="%{'disableKeyRow'}" />
								</s:elseif>
								<s:else>
									<s:if test="%{#statusClass != ''}">
										<tr><td class="expiredEntitleKeyLabel" colspan="10"><s:text name="activate.order.key"><s:param>Expired</s:param></s:text></td></tr>
									</s:if>
									<s:set name="statusClass" value="%{''}" />
								</s:else>
								<tr class="<s:property value="%{#rowClass}"/>" <s:property value="%{#statusClass}"/>>
									<td class="list"><s:property value="orderKey" /></td>
									<td class="list"><s:property value="licenseTypeStr" /></td>
									<s:if test="%{isInHomeDomain}">
										<td class="list"><s:property value="numberOfAps" /></td>
									</s:if>
									<s:else>
										<td class="list" style="cursor:default;" title="<s:property value="deviceStatusStr" />"><s:property value="numberOfAps" /></td>
									</s:else>
									<s:if test="%{!isInHomeDomain && !oEMSystem}">
										<td class="list"><s:property value="subEndTimeStr" /></td>
									</s:if>
									<s:if test="%{isInHomeDomain}">
										<td class="list"><s:property value="numberOfCvgs" /></td>
									</s:if>
									<s:else>
										<td class="list" style="cursor:default;" title="<s:property value="cvgStatusStr" />"><s:property value="numberOfCvgs" /></td>
									</s:else>
									<s:if test="%{isInHomeDomain}">
										<td class="list"><s:property value="numberOfVhms" /></td>
									</s:if>
									<s:else>
										<s:if test="%{!oEMSystem}">
											<td class="list"><s:property value="cvgSubEndTimeStr" /></td>
										</s:if>
									</s:else>
									<s:if test="%{!oEMSystem}">
										<td class="list"><s:property value="supportEndTimeStr" /></td>
									</s:if>
									<s:if test="%{isPermanentLicense}">
										<td class="list">N/A</td>
									</s:if>
									<s:else>
										<td class="list"><s:property value="numberOfEvalValidDays" /></td>
									</s:else>
									<td class="list"><s:property value="activeTimeStr" /></td>
								</tr>
							</s:iterator>
						</table>
						</div>
						</fieldset>
						</td>
					</tr>
					<tr style="display:<s:property value="%{oldLicenseDisplay}"/>">
						<td style="padding: 0 0 10px 10px;"><script type="text/javascript">insertFoldingLabelContext('<s:text name="license.string.install.advance" />','licenseStrDiv');</script></td>
					</tr>
					<tr>
						<td>
							<div id="licenseStrDiv" style="display: <s:property value="%{licenseStyle}"/>">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td style="padding: 0 10px 10px 10px">
										<fieldset><legend><s:text name="admin.license.import" /></legend>
										<div>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td height="5px"></td>
											</tr>
											<s:if test="%{showTwoSystemId}">
												<tr>
													<td class="labelT1" colspan="3"><s:property
														value="%{primarySystemId}" /></td>
												</tr>
												<tr>
													<td style="padding-left: 10px" width="113px" rowspan="2"
														valign="middle"><s:text name="license.key" /> <font
														color="red"><s:text name="*" /></font></td>
													<td rowspan="2"><s:textarea name="primaryLicense"
														value="%{primaryLicense}" cssStyle="width:450px" rows="2" />
													</td>
												</tr>
												<tr>
													<td height="2px"></td>
												</tr>
												<tr>
													<td class="labelT1" colspan="3"><s:property
														value="%{secondarySystemId}" /></td>
												</tr>
												<tr>
													<td style="padding-left: 10px" width="113px" rowspan="2"
														valign="middle"><s:text name="license.key" /> <font
														color="red"><s:text name="*" /></font></td>
													<td rowspan="2"><s:textarea name="secondaryLicense"
														value="%{secondaryLicense}" cssStyle="width:450px" rows="2" />&nbsp;&nbsp;</td>
													<td rowspan="2" valign="middle"><input type="button"
														name="import" value="Install" class="button"
														onClick="submitAction('import');"></td>
												</tr>
											</s:if>
											<s:else>
												<tr>
													<td style="padding-left: 10px" width="113px" rowspan="2"
														valign="middle"><s:text name="license.key" /> <font
														color="red"><s:text name="*" /></font></td>
													<td rowspan="2"><s:textarea name="primaryLicense"
														value="%{primaryLicense}" cssStyle="width:450px" rows="2" />&nbsp;&nbsp;</td>
													<td rowspan="2" valign="middle"><input type="button"
														name="import" value="Install" class="button"
														onClick="submitAction('import');"></td>
												</tr>
											</s:else>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
									<tr style="display:<s:property value="%{activationDisplay}"/>">
										<td style="padding: 0 10px 10px 10px">
										<fieldset><legend> <s:text
											name="activate.license.activation.key">
											<s:param>Activate</s:param>
										</s:text> </legend>
										<div>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>&nbsp;</td>
												<td colspan="2"><font color="red"><s:property
													value="%{activationMessage}" escape="false" /></font></td>
											</tr>
											<s:if test="%{showTwoSystemId}">
												<tr>
													<td class="labelT1" colspan="3"><s:property
														value="%{primarySystemId}" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="110px"><s:text
														name="license.activation.key.title" /></td>
													<td colspan="2"><s:textfield name="primaryActKey"
														value="%{primaryActKey}" maxlength="41" cssStyle="width:450px"
														onkeypress="return hm.util.keyPressPermit(event,'activation');" /></td>
												</tr>
												<tr>
													<td class="labelT1" colspan="3"><s:property
														value="%{secondarySystemId}" /></td>
												</tr>
												<tr>
													<td class="labelT1" width="110px"><s:text
														name="license.activation.key.title" /></td>
													<td><s:textfield name="secondaryActKey"
														value="%{secondaryActKey}" maxlength="41"
														cssStyle="width:450px"
														onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
													<td><input type="button" name="ignore" value="Activate"
														class="button" onclick="submitAction('activate');" /></td>
												</tr>
											</s:if>
											<s:else>
												<tr>
													<td class="labelT1" width="110px"><s:text
														name="license.activation.key.title" /></td>
													<td><s:textfield name="primaryActKey"
														value="%{primaryActKey}" maxlength="41" cssStyle="width:450px"
														onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
													<td><input type="button" name="ignore" value="Activate"
														class="button" onclick="submitAction('activate');" /></td>
												</tr>
											</s:else>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<s:if test="%{showUpUserRegInfoInLsPage}">
						<tr>
							<td style="padding: 0 0 10px 10px;"><script type="text/javascript">insertFoldingLabelContext('<s:text name="admin.license.send.userReg.info.pannel.title" />','userRegInfoDiv');</script></td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 10px 10px">
								<div id="userRegInfoDiv" style="display: <s:property value="%{userRegInfoStyle}"/>">
									<fieldset>
										<tiles:insertDefinition name="userRegisterInfo" />
									</fieldset>							
								</div>
							</td>
						</tr>
					</s:if>
				<!--</s:if>-->
			</table>
			</td>
		</tr>
	</table>
	<div id="deviceInfoPanel" style="display: none;">
	<div class="hd">
		<s:text name="admin.license.orderkey.export.device.inventory" />
	</div>
	<div class="bd">
		<s:property value="%{deviceInventoryDetail}" escape="false" />
	</div>
	</div>
</s:form>
</div>
