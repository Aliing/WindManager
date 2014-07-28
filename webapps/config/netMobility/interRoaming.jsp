<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'interSubnetRoaming';

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_roamingName").disabled == false) {
		document.getElementById(formName + "_dataSource_roamingName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		show_hideL3Setting(false);
		return true;
	}

	if (!validateRoamingName()) {
		return false;
	}

	var hideFileElement = document.getElementById(formName + "_dataSource_enabledL3Setting");
	if (hideFileElement.checked) {
		if (!validateAliveInterval()) {
			return false;
		}
		if (!validateAliveAgeout()) {
			return false;
		}
		if (!validateUpdateInterval()) {
			return false;
		}
		if (!validateUpdateAgeout()) {
			return false;
		}
	}

	return true;
}

function validateRoamingName() {
    var inputElement = document.getElementById(formName + "_dataSource_roamingName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.inter.roaming.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateAliveInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_keepAliveInterval");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.aliveInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.aliveInterval" />',
                                                       <s:property value="%{keepAliveIntervalRange.min()}" />,
                                                       <s:property value="%{keepAliveIntervalRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateAliveAgeout() {
      var inputElement = document.getElementById(formName + "_dataSource_keepAliveAgeout");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.aliveAgeout" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.aliveAgeout" />',
                                                       <s:property value="%{keepAliveAgeoutRange.min()}" />,
                                                       <s:property value="%{keepAliveAgeoutRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateUpdateInterval() {
      var inputElement = document.getElementById(formName + "_dataSource_updateInterval");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.updateInterval" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.updateInterval" />',
                                                       <s:property value="%{updateIntervalRange.min()}" />,
                                                       <s:property value="%{updateIntervalRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateUpdateAgeout() {
      var inputElement = document.getElementById(formName + "_dataSource_updateAgeout");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.inter.roaming.updateAgeout" /></s:param></s:text>');
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.inter.roaming.updateAgeout" />',
                                                       <s:property value="%{updateAgeoutRange.min()}" />,
                                                       <s:property value="%{updateAgeoutRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	inputElement.focus();
            return false;
      }
      return true;
}

function show_hideL3Setting(checked) {
    var hideL3Setting = document.getElementById("hideL3Setting");
	if (checked){
		hideL3Setting.style.display="block";
	}
	if (!checked){
		hideL3Setting.style.display="none";
		document.getElementById(formName + "_dataSource_keepAliveInterval").value="10";
		document.getElementById(formName + "_dataSource_keepAliveAgeout").value="5";
		document.getElementById(formName + "_dataSource_updateInterval").value="60";
		document.getElementById(formName + "_dataSource_updateAgeout").value="30";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="interSubnetRoaming" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedRoamingName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="interSubnetRoaming">

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
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
							class="button" onClick="submitAction('update');"
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
			
			<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="700px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="130"><s:text
								name="config.inter.roaming.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.roamingName" size="24"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="%{roamingNameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
								name="config.inter.roaming.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.inter.roaming.description" /></td>
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />&nbsp;<s:text
								name="config.inter.roaming.description.range" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding:0 4px 0 4px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
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
							<td style="padding:0 2px 0 6px"><s:checkbox
									name="dataSource.enabledL3Setting" 
									value="%{dataSource.enabledL3Setting}"
									onclick="show_hideL3Setting(this.checked);" /></td>
							<td><s:text name="config.inter.roaming.l3Setting" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td>
						<div style="display:<s:property value="%{hideL3Setting}"/>" id="hideL3Setting">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding:5px 5px 5px 8px">
									<fieldset><legend><s:text 
											name="config.inter.roaming.keepalives" /></legend>
										<div>
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="2px"/>
												</tr>
												<tr>
													<td width="50px"><s:text
														name="config.inter.roaming.aliveInterval" /></td>
													<td width="250px"><s:textfield name="dataSource.keepAliveInterval"
														size="12" maxlength="6" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														&nbsp;<s:text name="config.inter.roaming.aliveInterval.range" /></td>
													<td width="210px"><s:text
														name="config.inter.roaming.aliveAgeout" /></td>
													<td width="190px"><s:textfield name="dataSource.keepAliveAgeout"
														size="12" maxlength="4" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														&nbsp;<s:text name="config.inter.roaming.aliveAgeout.range" /></td>
												</tr>
											</table>	
										</div>
									</fieldset>
								</td>
							</tr>

							<tr>								
								<td style="padding:5px 5px 5px 8px">
									<fieldset><legend><s:text 
											name="config.inter.roaming.cacheUpdate" /></legend>
										<div>
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td height="2px"/>
												</tr>
												<tr>
													<td width="50px"><s:text
														name="config.inter.roaming.updateInterval" /></td>
													<td width="250px"><s:textfield name="dataSource.updateInterval"
														size="12" maxlength="5" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														&nbsp;<s:text name="config.inter.roaming.updateInterval.range" /></td>
													<td width="210px"><s:text
														name="config.inter.roaming.updateAgeout" /></td>
													<td width="190px"><s:textfield name="dataSource.updateAgeout"
														size="12" maxlength="4" 
														onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
														&nbsp;<s:text name="config.inter.roaming.updateAgeout.range" /></td>
												</tr>
											</table>	
										</div>
									</fieldset>
								</td>
							</tr>
						</table>
					    </div>
					</td>
				</tr>

				<tr>
				<td height="8"></td>
				</tr>
			</table>
			</td>
		</tr>

	</table>
</s:form></div>
