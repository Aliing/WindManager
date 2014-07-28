<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script>
var formName = 'hiveApUpdate';

function onLoadPage(){
	showUnsupportedApsMessage();
}
function showUnsupportedApsMessage(){
	var unsupportedApsMessage  = "<s:property value='%{unsupportedApsMessage}' />" ;
	if(unsupportedApsMessage){
		showInfoDialog(unsupportedApsMessage);
	}
}

function submitAction(operation) {
	if('updateOutdoorSettings'==operation){
		if(validate()){
			confirmDialog.cfg.setProperty('text', '<s:text name="confirm.hiveAp.after.operation.auto.reboot"/>');
			confirmDialog.show();
		}
	}else{
		showProcessing();
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function doContinueOper() {
	showProcessing();
	document.forms[formName].operation.value = 'updateOutdoorSettings';
	document.forms[formName].submit();
}
function validate() {
	if(!validateRadio()){
		return false;
	}
	if(!validateParameters()){
		return false;
	}
	if(!validateApSelection()){
		return false;
	}
	return true;
}
function validateRadio(){
	var radioes = document.getElementsByName("outdoorString");
	var isAllunchecked = true;
	for(var i = 0; i < radioes.length; i++){
		if(radioes[i].checked){
			isAllunchecked = false;
			break;
		}
	}
	if(isAllunchecked){
			hm.util.reportFieldError(radioes[0], '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.isOutdoor" /></s:param></s:text>');
			return false;
	}
	return true;
}

function validateParameters(){
	var offsetElement = document.getElementById("commonOffSet");
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
	return true;
}

function validateApSelection(){
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
	document.writeln('<s:text name="topology.menu.hiveAp.outdoor.settings"/> </td>');
}
</script>
<div id="content"><s:form action="hiveApUpdate">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('updateOutdoorSettings');"
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="550px">
				<tr>
					<td style="padding: 4px 10px 10px 10px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
						<td>
							<div>
								<fieldset><legend><s:text name="hiveAp.isOutdoor"/></legend>
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="10"></td>
								</tr>
								<s:set name="_indoor" value="%{getText('hiveAp.isOutdoor.dsp.false')}"></s:set>
								<s:set name="_outdoor" value="%{getText('hiveAp.isOutdoor.dsp.true')}"></s:set>
								<tr>
									<td><s:radio list="#{'false':#_indoor ,'true':#_outdoor}"
													label="Gender" name="outdoorString" />
									</td>
								</tr>
								<tr>
											<td height="5px"></td>
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
									<td class="labelT1" width="120px" nowrap><label>Activate after</label><font color="red"><s:text name="*"/></font></td>
									<td><s:textfield id="commonOffSet" name="commonOffSet" value="%{commonOffSet}" size="4"
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
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
									<tr>
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="isOutdoor" key="hiveAp.isOutdoor" /></th>
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
											<s:if test="%{isOutdoor==null}" >
												<td class="list">&nbsp;</td>
											</s:if>
											<s:elseif test="%{isOutdoor}">
												<td class="list"><s:text name="hiveAp.isOutdoor.dsp.true"/>&nbsp;</td>
											</s:elseif>
											<s:elseif test="%{!isOutdoor}">
												<td class="list"><s:text name="hiveAp.isOutdoor.dsp.false"/>&nbsp;</td>
											</s:elseif>
											
										</tr>
									</s:iterator>
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
</s:form></div>