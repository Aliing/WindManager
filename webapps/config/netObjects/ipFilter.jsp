<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'ipFilter';

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_filterName").disabled == false) {
		document.getElementById(formName + "_dataSource_filterName").focus();
	}
	<s:if test="%{jsonMode}">
		top.changeIFrameDialog(740, 450);
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('selectIpAddress');
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>'
		|| operation=='newIpAddress') {
		return true;
	}
	if(operation == "editIpAddress"){
		var value = hm.util.validateOptionTransferSelection("selectIpAddress");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipAddressId.value = value;
		}
		return true;
	}

	if (!validateFilterName()) {
		return false;
	}
	if(operation == 'update' || operation == 'create'){
		var selectIP=document.getElementById("selectIpAddress");
		if (selectIP.length==0 || (selectIP.length ==1 && selectIP.options[0].value<0)) {
			hm.util.reportFieldError(document.getElementById("selectTB"), '<s:text name="error.requiredField"><s:param><s:text name="config.ipFilter.selectedIpAddress" /></s:param></s:text>');
            return false;
		}
	}

	return true;
}

function validateFilterName() {
    var inputElement = document.getElementById(formName + "_dataSource_filterName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.ipFilter.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}
function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='ipFilter' includeParams='none' />" + "?jsonMode=true"
				+ "&ignore=" + new Date().getTime();
		if (operation == 'create') {
			//
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		hm.options.selectAllOptions('selectIpAddress');
		YAHOO.util.Connect.setForm(document.forms["ipFilter"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ipFilter" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>
</script>
<div id="content"><s:form action="ipFilter"  name="ipFilter" id="ipFilter">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<div id="vlanTitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td  width="75%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-mgmt_IP_Filters.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.mgtipfilters.dialog.new.title"/>&nbsp;</td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.mgtipfilters.dialog.edit.title"/>&nbsp;</td>
							</s:else>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td width="25%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vlanSaveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>
	<s:hidden name="ipAddressId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode == false}">
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
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}">
				<table border="0" cellspacing="0" cellpadding="0" width="630">
			</s:if>
			<s:else>
				<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="630">
			</s:else>
				<tr>
					<td style="padding-left:4px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="130"><s:text
								name="config.ipFilter.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.filterName" size="24"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="%{nameLength}" disabled="%{disabledName}"/> <s:text
								name="config.ipFilter.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1"><s:text name="config.ipFilter.description" /></td>
							<td><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" /> <s:text
								name="config.ipFilter.description.range" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
				<td style="padding:4px 4px 0 4px">
					<fieldset><legend><s:text
							name="config.ipFilter.tabTitle" /></legend>
						<div>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td height="5" style="padding-left:180px">
									<table cellspacing="0" cellpadding="0" border="0" id ="selectTB">
									</table>
									</td>
								</tr>
								<tr>
									<s:push value="%{ipAddressOptions}">
										<td colspan="3"><tiles:insertDefinition
											name="optionsTransfer"/></td>
									</s:push>
								</tr>
								<tr>
									<td height="5"></td>
								</tr>
								<%--<tr>
									<td width="100" style="padding-left:40px;">
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('newIpAddress')"><img class="dinl"
											src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('editIpAddress')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else>
									</td>
								</tr>--%>
							</table>
						</div>
					</fieldset>
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