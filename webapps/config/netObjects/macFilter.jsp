<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>

var formName = 'macFilters';
var buttonShowing;
var displayErrorObj ;
var delgridCountFlg = true;
function onLoadPage() {
    displayErrorObj = document.getElementById("checkAll");
	if (document.getElementById("filterName").disabled == false) {
			document.getElementById("filterName").focus();
	}
	var operation = "<s:property value="%{operation}"/>";
    buttonShowing =<s:property value="%{buttonShowing}"/>;
	if((operation=='continue' || operation=='removeFilterInfo') && buttonShowing)
	{
	   showCreateSection();
	}
	if(operation=='new')
    {
		showCreateSection();
    }

}
<s:if test="%{jsonMode}">
	setTimeout("onLoadPage()", 10);
	if (top.isIFrameDialogOpen()) {
		top.changeIFrameDialog(730, 450);
	}
</s:if>

function submitAction(operation) {

    if(validate(operation)){
    	if (operation=="newFilterInfo" || operation=="editFilterInfo") {
    		<s:if test="%{jsonMode==true}">
				if (parent!=null && !parent.isIFrameDialogOpen()) {
					//do nothing now
				} else {
					if (operation != 'create' &&
						    operation != 'addFilterInfo' &&
						    operation != 'removeFilterInfo') {
					       	showProcessing();
						}
					document.macFilters.operation.value = operation;
					document.forms[formName].parentIframeOpenFlg.value = true;
				    document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if (operation != 'create' &&
					    operation != 'addFilterInfo' &&
					    operation != 'removeFilterInfo') {
				       	showProcessing();
					}
				document.macFilters.operation.value = operation;
			    document.forms[formName].submit();
			</s:else>
    	} else {
			document.forms[formName].operation.value = operation;
		    <s:if test="%{jsonMode == true && contentShownInSubDrawer == true}">
				if (operation == 'removeFilterInfo' 
						|| operation == 'addFilterInfo') {
					//do nothing now
				} else {
					document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if (operation != 'create' &&
				    operation != 'addFilterInfo' &&
				    operation != 'removeFilterInfo') {
			       	showProcessing();
				}
				document.forms[formName].submit();
			</s:else>
		}
    }
}

function saveMacFilter(operation){
	if(validate(operation)){
		  var url = "<s:url action='macFilters' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime(); 
			document.forms["macFilters"].operation.value = operation;
			YAHOO.util.Connect.setForm(document.forms["macFilters"]);
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveMacFilter, failure : resultDoNothing, timeout: 60000}, null);
	}
}

var succSaveMacFilter = function(o) {
	try {
		
		eval("var details = " + o.responseText);
		
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById("leftOptions_macFilters");
			if(parentSelectDom != null) {
				if(details.id != null && details.name != null){
					hm.util.insertSelectValue(details.id, details.name, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
		
	}catch(e){
		alert("error");
		return;
	}
}

var resultDoNothing = function(o){
}

function showCreateSection() {
    hm.util.hide('newButton');
	   hm.util.show('createButton');
	   hm.util.show('createSection');
	   // to fix column overlap issue on certain browsers
	   var trh = document.getElementById('headerSection');
	   var trc = document.getElementById('createSection');
	   var table = trh.parentNode;
	   table.removeChild(trh);
	   table.insertBefore(trh, trc);
	   document.getElementById("buttonShowing").value="true";
}
function hideCreateSection() {
    hm.util.hide('createButton');
    hm.util.show('newButton');
    hm.util.hide('createSection');
	document.getElementById("buttonShowing").value="false";
}
function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected && options[i].value > 0 ) {
			return true;
		}
	}
	return false;
}
function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MAC_FILTERS%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == "newFilterInfo") {
		return true;
	}
	if(operation == "editFilterInfo"){
		var value = hm.util.validateListSelection("macOrOuiIds");
		if(value < 0){
			return false
		}else{
			document.forms[formName].macOrOuiId.value = value;
		}
		return true;
	}
	
	if((operation == 'create'+'<s:property value="lstForward"/>' || operation=='create') && !validateName()) {
	    return false;
	}
	if(operation == 'create'+'<s:property value="lstForward"/>' || 
		operation == 'update'+'<s:property value="lstForward"/>' || 
		operation=='create' || operation == 'update') {
	    if(!checkMac())
	        return false;
	}
	if(operation=="addFilterInfo" && !validateMacOrOui())
	  return false;
	if(operation=="removeFilterInfo" && !checkRemoveSelectedOptions())
	  return false;
	return true;
}
function checkMac()
{
    var inputElement = document.getElementsByName('macOrOuiIndices');
	if (inputElement.length == 0) {
        hm.util.reportFieldError(displayErrorObj,'<s:text name="error.requiredField"><s:param><s:text name="config.macFilter.address" /></s:param></s:text>');
		return false;
	}
	return true;
}
function validateName() {
       var inputElement = document.getElementById("filterName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.macFilter.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }
   return true;
}

function toggleCheckAll(cb) {
	var cbs = document.getElementsByName('macOrOuiIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function checkRemoveSelectedOptions() {
	var inputElement = document.getElementsByName('macOrOuiIndices');
	if (inputElement.length == 0) {
		hm.util.reportFieldError(displayErrorObj, '<s:text name="info.emptyList"></s:text>');
		return false;
	}
	if (!hm.util.hasCheckedBoxes(inputElement)) {
        hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return false;
	}
	return true;
}

function checkRemoveSelect(cb){
   var cbs = document.getElementsByName('macOrOuiIndices');
   for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked)
		    return true;
	}
   return false;
}

function validateMacOrOui(){
     var inputElement = document.getElementById("macOrOuiIds");
     if (!hasSelectedOptions(inputElement.options)) {
            hm.util.reportFieldError(displayErrorObj, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.macFilter.address" /></s:param></s:text>');
            inputElement.focus();
			return false;
		}
      return true;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="macFilters" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
	</s:else>
}

</script>
<div id="content"><s:form action="macFilters" id="macFilters" name="macFilters">
	<s:if test="%{jsonMode}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:hidden name="macOrOuiId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
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
	  <s:if test="%{jsonMode == true}">
	  <tr>
		<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="84%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-MAC-filters.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.macFilter"/>
								</s:if> <s:else>
									<s:text name="config.title.macFilter.edit"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<s:if test="%{!parentIframeOpenFlg}">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
												<s:if test="%{dataSource.id == null}">
													<s:if test="%{writeDisabled == 'disabled'}">
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="saveMacFilter('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{updateDisabled == 'disabled'}">
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="saveMacFilter('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:else>
											</tr>
										</table>
									</s:if>
									<s:else>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
												
												<s:if test="%{dataSource.id == null}">
													<s:if test="%{writeDisabled == 'disabled'}">
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:if>
												<s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right;" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:if>
													<s:else>
														<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
													</s:else>
												</s:else>
											</tr>
										</table>
									</s:else>
								</td>
							</tr>
						</table>
					</td>
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
			<s:if test="%{jsonMode == true}">
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="560">
			</s:else>	
				<tr>
					<td width="100%">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="80"><label><s:text
										name="config.macFilter.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td><s:textfield name="dataSource.filterName"
										id="filterName" size="24" maxlength="%{nameLength}" disabled="%{disableName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" />
										<s:text name="config.name.range"/></td>
								</tr>

								<tr>
									<td class="labelT1" width="80"><label><s:text
										name="config.macFilter.description" /></label></td>
									<td colspan="4"><s:textfield name="dataSource.description"
										size="45" maxlength="%{descriptionLength}" />
										<s:text name="config.description.range"/></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="sepLine" colspan="4" width="100%"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td style="padding:6px 0px 4px 10px;" valign="top" width="470">
							<table cellspacing="0" cellpadding="0" border="0" width="100%"
								class="embedded" id="tbl_id">

								<tr id="newButton">
									<td colspan="5"  style="padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Add" <s:property value="writeDisabled" />
												class="button" onClick="showCreateSection();"></td>
											<td><input type="button" name="ignore" value="Remove" <s:property value="writeDisabled" />
												class="button"
												onClick="submitAction('removeFilterInfo');"></td>
										</tr>
									</table>
									</td>
								</tr>

								<tr style="display:none;" id="createButton">
									<td colspan="5" style="padding-left: 0px; padding-bottom: 2px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="ignore" value="Apply"
												class="button" onClick="submitAction('addFilterInfo');"></td>
											<td><input type="button" name="ignore" value="Remove"
												class="button"
												onClick="submitAction('removeFilterInfo');"></td>
											<td><input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideCreateSection();"></td>

										</tr>
									</table>
									</td>
								</tr>
								<tr id="headerSection">
									<th align="left" style="padding-left: 0;" width="10px">
									    <input	type="checkbox" id="checkAll" onClick="toggleCheckAll(this);"></th>
									<th align="left" style="padding-left: 0;" width="150">
									    <s:text name="config.macFilter.address" /></th>
									<th align="left" style="padding-left: 0;" width="150">
										<s:text name="config.macFilter.description" /></th>
									<th align="left" width="100" style="padding-left: 0;">
									    <s:text name="config.macFilter.action"/></th>
								</tr>

								<tr style="display:none;" id="createSection">
								    <td class="listHead" width="10px">&nbsp;</td>
								    <td class="listHead" valign="top" style="padding:2px 5px 5px 0px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:select multiple="true" size="6" name="macOrOuiIds"  id="macOrOuiIds"
												list="%{availableMacOrOui}" listKey="id" onclick="hm.util.showtitle(this);"
												listValue="value" cssStyle="width: 200px;" /></td>
											<td valign="top" style="padding-left:3px;">
												<s:if test="%{fullMode}">
													<a class="marginBtn" href="javascript:submitAction('newFilterInfo')">
														<img class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													<a class="marginBtn" href="javascript:submitAction('editFilterInfo')">
													<img class="dinl" src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:if>
												<s:elseif test="%{easyMode}">
													<a class="marginBtn" href="javascript:hm.simpleObject.newSimple(hm.simpleObject.TYPE_MAC,'','macOrOuiIds','',<s:property value="%{domainId}"/>)">
														<img class="dinl" src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													<a class="marginBtn" href="javascript:hm.simpleObject.removeSimple(hm.simpleObject.TYPE_MAC,'macOrOuiIds','')"><img class="dinl"
													src="<s:url value="/images/cancel.png" />"
													width="16" height="16" alt="Remove" title="Remove" /></a>
												</s:elseif>		
											</td>
										</tr>
									</table>
									</td>
									<td class="listHead" valign="top"  style="padding:2px 0px 0px 0px;">
									</td>
									<td class="listHead" valign="top"  style="padding:2px 0px 0px 0px;">
									     <s:select name="filterAction"
							        	 list="%{enumActionValues}" listKey="key" listValue="value" /></td>
								</tr>

								<s:iterator value="%{dataSource.filterInfo}" status="status" id="filterInfoId">
									<tr>
									    <td class="listCheck">
									        <s:checkbox name="macOrOuiIndices"
											fieldValue="%{#status.index}" /></td>
										<td class="list"><s:property value="macOrOui.macOrOuiName"/></td>
										<td class="list"><s:property value="macOrOui.description"/></td>
										<td class="list"><s:select
											name="actionIndex" id="actionIndex" value="%{#filterInfoId.filterAction}"
											list="%{enumActionValues}" listKey="key" listValue="value"/></td>
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
</s:form></div>
