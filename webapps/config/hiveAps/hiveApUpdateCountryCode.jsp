<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>
var formName = 'hiveApUpdate';

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function submitActionJson(operation){
	if(!validate(operation)){
		return false;
	}
	var countryCode = document.getElementById("countryCode").value;
	var offsetTime  = document.getElementById("countryCode_offSet").value;
	parent.uploadCountryCodeJson(operation, countryCode, offsetTime);
}

function validate(operation) {
	if(!validateApSelection(operation)){
		return false;
	}
	if(!validateParameters(operation)){
		return false;
	}
	return true;
}

function validateParameters(operation){
	if(operation == 'updateCountryCode' || operation == 'updateCountryCodeJson'){
		var offsetElement = document.getElementById("countryCode_offSet");
		if(offsetElement.value.length == 0){
			hm.util.reportFieldError(offsetElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.configuration.offsetTime" /></s:param></s:text>');
			return false;
		}
		var message = hm.util.validateIntegerRange(offsetElement.value, '<s:text name="hiveAp.update.configuration.offsetTime" />',
		                                           <s:property value="0" />,
		                                           <s:property value="3600" />);
		if (message != null) {
			hm.util.reportFieldError(offsetElement, message);
			return false;
		}
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'updateCountryCode' || operation == 'updateCountryCodeJson'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				break;
			}
		}
		if(!isSelected){
			var listElement = document.getElementById('checkAll');
			hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
			return false;
		}
	}
	return true;
}

function toggleCheckAllHiveAps(cb){
	var cbs = document.getElementsByName('hiveApIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.countryCode"/> </td>');
}

function countryCodeChange(element){
	if(element.value == 392){
		document.getElementById("noteForJapan").style.display = "";
	}else{
		document.getElementById("noteForJapan").style.display = "none";
	}
}
</script>
<div id="content"><s:form action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode}" >
			<tr><td align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton" width="86px">
							<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('updateCountryCodeJson');" title="Upload" <s:property value="writeDisabled" />><span>Upload</span></a>
						</td>
						<td class="npcButton" width="86px">
							<a href="javascript:void(0);" class="btCurrent" onclick="parent.hideCountryCodePanel();" title="Cancel" <s:property value="writeDisabled" />><span>Cancel</span></a>
						</td>
					</tr>
				</table>
			<td></tr>
		</s:if>
		<s:else>
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Upload"
								class="button" onClick="submitAction('updateCountryCode');"
								<s:property value="writeDisabled" />></td>
							<td><input type="button" name="cancel" value="Cancel"
								class="button"
								onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
								<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}" >
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
			</s:if>
			<s:else>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="550px">
			</s:else>
				<tr>
					<td style="padding: 4px 10px 10px 10px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
						<td>
							<div>
								<fieldset><legend><s:text name="hiveAp.update.countryCode.tag"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
								<td class="labelT1" width="120px" nowrap><s:text name="hiveAp.update.countryCode.label" /></td>
								<td nowrap><s:select name="countryCode" id="countryCode" list="%{countryCodeValues}"
					            listKey="key" listValue="value" onchange="countryCodeChange(this);"/></td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								<tr id="noteForJapan" style="display:none">
									<td class="noteInfo" colspan="10" style="padding-left : 10px">
										<s:text name="hiveAp.update.countryCode.japan.note"/>
									</td>
								</tr>
								</table>
								</fieldset>
							</div>
						</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
							<div>
								<fieldset><legend><s:text name="hiveAp.update.configuration.time.tag"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="10"></td>
								</tr>
								<tr>
									<td class="labelT1" width="120px" nowrap><label>Activate after</label></td>
									<td><s:textfield id="countryCode_offSet" name="countryCode_offSet" value="%{countryCode_offSet}" size="4"
										maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
										<s:text name="hiveAp.update.configuration.activateAfterTip"/></td>
								</tr>
								<tr>
									<td height="10"></td>
								</tr>
								</table>
								</fieldset>
							</div>
							</td>
						</tr>
						<s:if test="%{!jsonMode}">
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="countryCode" key="hiveAp.countryCode" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck"><ah:checkItem /></td>
											<td class="list"><s:property value="hostName" /></td>
											<td class="list"><s:property value="macAddress" /></td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="countryName" escape="false" />&nbsp;</td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
						</s:if>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>