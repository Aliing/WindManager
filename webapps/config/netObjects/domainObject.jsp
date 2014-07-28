<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'domainObject';
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_objName").disabled == false) {
		document.getElementById(formName + "_dataSource_objName").focus();
	}
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(680,400);
	 	}
	 </s:if>
}

function saveDomainObjectJsonDlg(operation){
	if (validate(operation) == false) {
		return;
	}
	var url = "<s:url action='domainObject' includeParams='none' />?ignore="+new Date().getTime();
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("domainObject"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveDomainObjectJsonDlg, failure : failSaveDomainObjectJsonDlg, timeout: 60000}, null);	
}

var succSaveDomainObjectJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	} catch(e) {
	}
}
var failSaveDomainObjectJsonDlg = function(o){
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_DOMAIN_OBJECT%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}
	if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_objName");
		var message = hm.util.validateName(name.value, '<s:text name="config.osObject.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    	if (name.value == '<s:text name="config.ipPolicy.any" />') {
    		hm.util.reportFieldError(name, '<s:text name="error.ipOrMacOrService.nameLimit"><s:param><s:text name="config.ipPolicy.any" /></s:param></s:text>');
        	name.focus();
        	return false;
    	}
	}

	var table = document.getElementById("checkAll");
    if (operation == 'addDomName') {
    	var cbs = document.getElementsByName('ruleIndices');
    	
    	if(Get(formName + "_objType").value == 2 || Get(formName + "_dataSource_objType").value == 2){
    		if (cbs.length >= 64) {
    			hm.util.reportFieldError(table, '<s:text name="error.objectReachLimit"></s:text>');
    			return false;
    		}
    	}else{
    		if (cbs.length >= 32) {
    			hm.util.reportFieldError(table, '<s:text name="error.objectReachLimit"></s:text>');
    			return false;
    		}
    	}
		
    	// domain name
		var fieldValue = document.forms[formName].domName;
		if (!checkDomObject('<s:text name="config.domainObject.nameList" />', fieldValue)) {
			return false;
		}
    }

    if (operation == 'removeDomName' || operation == 'removeDomNameNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text
													name="config.domainObject.nameList" /></s:param></s:text>');
			return false;
		}
	}
	if(operation == 'create'+'<s:property value="lstForward"/>' 
			|| operation == 'create'
			|| operation == 'update'
			|| operation == 'update'+'<s:property value="lstForward"/>') {
    	var domNames = document.getElementsByName('domNames');
		var table = document.getElementById("checkAll");
		if(domNames.length == 0)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.domainObject.nameList" /></s:param></s:text>');
       		table.focus();
       		return false;
		}

		for(var i = 0; i < domNames.length; i ++) {
			if (!checkDomObject('<s:text name="config.domainObject.nameList" />'+' in '+(i+1)+' row',domNames[i])) {
				return false;
			}
		}
    }
	return true;
}

function checkDomObject(title, element) {
	var table = document.getElementById("checkAll");
    var message = hm.util.validateName(element.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(table, message);
       	element.focus();
       	return false;
   	}
	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');

	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
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

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="domainObject" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

<div id="content"><s:form action="domainObject" id="domainObject" onsubmit="return false;">
	<s:hidden name="objType"></s:hidden>
	<s:hidden name="objTypeDisabled"></s:hidden>
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="domainObjectTitleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%" >
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-device_domain_objects.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.domainObject"/>&nbsp;</td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.domainObject.edit"/>&nbsp;</td>
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
				<td align="right">
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{''== writeDisabled}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="domainObjectSaveBtnId" onclick="saveDomainObjectJsonDlg('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="%{''== updateDisabled }">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="domainObjectSaveBtnId" onclick="saveDomainObjectJsonDlg('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
						</s:else>
					</tr>
				</table>
				</s:if>
				<s:else>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="domainObjectSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="domainObjectSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == false}">
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
								<s:property value="writeDisabled" />>
							</td>
						</s:if>
						<s:else>
							<td><input type="button" name="update" value="<s:text name="button.update"/>"
								class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
								<s:property value="updateDisabled" />></td>
						</s:else>
						<s:if test="%{lstForward == null || lstForward == ''}">
							<td><input type="button" name="cancel" value="Cancel"
								class="button"
								onClick="submitAction('<%=Navigation.L2_FEATURE_DOMAIN_OBJECT%>');">
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
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0"
					width="450" >
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="450">
			</s:else>
			
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="100"><label><s:text
										name="config.osObject.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield
										name="dataSource.objName" size="32"
										maxlength="%{NameLength}" disabled="%{disabledName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
										name="config.ssid.ssidName_range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="100"><label><s:text
										name="config.domainObject.type" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:select  name="dataSource.objType" value="dataSource.objType" disabled="%{disabledName || objTypeDisabled}" list="%{enumDomainObjectType}" listKey="key" listValue="value" cssStyle="width: 165px;"></s:select></td>
								</tr>
								<tr>
									<td style="padding:6px 0px 6px 0px" colspan="3">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td class="sepLine"><img
												src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="3" style="padding:4px 0px 4px 4px;" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" class="embedded">
											<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
												<td colspan="7" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="New"
															class="button" onClick="showCreateSection();"
															<s:property value="updateDisabled" />></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button" <s:property value="updateDisabled" />
															onClick="submitAction('removeDomName');"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
												<td colspan="7" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
															class="button" onClick="submitAction('addDomName');"></td>
														<td><input type="button" name="ignore" value="Remove"
															class="button"
															onClick="submitAction('removeDomNameNone');"></td>
														<td><input type="button" name="ignore" value="Cancel"
															class="button" onClick="hideCreateSection();"></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="headerSection">
												<th align="left" style="padding-left: 0;" width="10"><input
													type="checkbox" id="checkAll"
													onClick="toggleCheckAllRules(this);"></th>
												<th align="left" width="250"><s:text
													name="config.domainObject.nameList" /><br>
													<s:text name="config.ssid.ssidName_range" /></th>
												<th align="left" width="250"><s:text
													name="config.ipAddress.description" />
													<s:text name="config.ssid.description_range" /></th>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
												<td class="listHead" width="10">&nbsp;</td>
												<td class="listHead" valign="top">
													<s:textfield size="30" name="domName" maxlength="32" /></td>
												<td width="100" class="listHead" valign="top"><s:textfield size="48" name="description"
													maxlength="%{commentLength}" /></td>
											</tr>
											<s:iterator value="%{dataSource.items}" status="status">
												<tr>
													<td class="listCheck"><s:checkbox name="ruleIndices"
														fieldValue="%{#status.index}" /></td>
													<td class="list" width="100"><s:textfield size="30" name="domNames"
														maxlength="32" value="%{domainName}" /></td>
													<td width="100" class="list"><s:textfield size="48" name="descriptions" value="%{description}"
														maxlength="%{commentLength}" /></td>
												</tr>
											</s:iterator>
											<s:if test="%{gridCount > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount}">
													<s:iterator>
														<tr>
															<td class="list" colspan="3">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>
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
		</td>
	</tr>
	</table>
</s:form></div>
