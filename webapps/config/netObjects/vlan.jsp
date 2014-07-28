<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />     


<script>
var formName = 'vlan';
var operName = 'create';
var saveVlanflag=false;
var ruleIndex= '';
var USETYPE_GLOBAL = <%=SingleTableItem.TYPE_GLOBAL%>;
var USETYPE_MAP = <%=SingleTableItem.TYPE_MAP%>;
var USETYPE_HIVEAP = <%=SingleTableItem.TYPE_HIVEAPNAME%>;
var USETYPE_CLASSIFIER = <%=SingleTableItem.TYPE_CLASSIFIER%>;

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_vlanName").disabled == false) {
		document.getElementById(formName + "_dataSource_vlanName").focus();
	}
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(910,450);
	 	}
	 	<s:if test="%{dataSource.id == null && contentShownInSubDrawer}">
	 	if(subDrawerOperation) {
	 		subDrawerOperation='createVLAN';
	 	}
	 	</s:if>	 	
	</s:if>
	if(!window.jQuery) {
		head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(!window.jQuery.ui) {
		head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
			"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
	function(){
		initClassifierWidget();
	});
	
}


function checkConfilct(operation,isInConfigure,callbackMehod,tagKeyVal) {
	saveVlanflag=isInConfigure;
	var vlu=$("#vlanClassifierTagContainer").find("input#vlan_vlanId").val();
	var isTagVisable=$("#vlanClassifierTagContainer").is(':visible');
	if(vlu!=null&&vlu!=""&&(operation=='create'||operation=='update')&&isTagVisable){
		  var DOM = YAHOO.util.Dom;	    
	    var newRulePanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierNewRulePanel', {width: 140, closeIcon: 'images/cancel.png'});
	    
        DOM.get('ahTempClassifierNewRuleBtn').onclick = function(){
    		$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    		newRulePanel.openDialog();
    		DOM.get('returnEditNewRule').focus();
    	};
	    
	     DOM.get('returnEditNewRule').onclick = function(){     		
    		newRulePanel.closeDialog();
  		 };
  		 
   		 DOM.get('discardNewRule').onclick = function(){ 
    		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey="+tagKeyVal+"&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
	      	operName=operation;
	    	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : callbackMehod}, null);
     		newRulePanel.closeDialog();
    	 };
    	 $('#classfierNewRulePanelAPanel_c').keyup(function(e) {
           if (event.which === 13) {
        		 newRulePanel.closeDialog();
          }
       });
    	 
    	 $("input#ahTempClassifierNewRuleBtn").click();
	       return;	  
	}
	var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey="+tagKeyVal+"&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
	operName=operation;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : callbackMehod}, null);
}

function submitAction(operation) {		
	if(operation=='create'||operation=='update'||operation.indexOf('create')!=-1||operation.indexOf('update')!=-1){		
		checkConfilct(operation,false,succView,1);
		return;
	}
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>'
				|| operation == 'update' + '<s:property value="lstForward"/>'
				|| operation == 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}
function succView(o) {	
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) {		
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		}
		else{
		if (validate(operName)) {
			document.forms[formName].operation.value = operName;
	    	document.forms[formName].submit();
		 	}
		}
	}
	
}
function saveAllItem() {
	var confilictVal=$("input#ahTempClassfierConfilict").val();
	if(confilictVal == 'true'){
		   for(var i=0; i<ruleIndex.length; i++) {	
				var tempIndex=parseInt(ruleIndex[i])+1;
				$("li.item:nth-child("+tempIndex+")").addClass('needAlertInRed');
		}
	}
	else{
		if(saveVlanflag){
			if (validate(operName)) {
				var url = "";
				if (operName == 'create') {
					url = "<s:url action='vlan' includeParams='none' />" + "?jsonMode=true" 
							+ "&ignore=" + new Date().getTime(); 
				} else if (operName == 'update') {
					url = "<s:url action='vlan' includeParams='none' />" + "?jsonMode=true" 
							+ "&ignore=" + new Date().getTime(); 
				}
				document.forms[formName].operation.value = operName;
				YAHOO.util.Connect.setForm(document.forms["vlan"]);
				var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveVlan, failure : failSaveVlan, timeout: 60000}, null);
			}
		}else{
			if (validate(operName))
			{
				document.forms[formName].operation.value = operName;
		    	document.forms[formName].submit();
			}
		}
		
	 }
}	
function saveVlan(operation) {
    checkConfilct(operation,true,succsSaveView,1);	
}

function succsSaveView(o) {	
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) {		
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		}
		else{		
		if (validate(operName)) {
			var url = "";
			if (operName == 'create') {
				url = "<s:url action='vlan' includeParams='none' />" + "?jsonMode=true" 
						+ "&ignore=" + new Date().getTime(); 
			} else if (operName == 'update') {
				url = "<s:url action='vlan' includeParams='none' />" + "?jsonMode=true" 
						+ "&ignore=" + new Date().getTime(); 
			}
			document.forms[formName].operation.value = operName;
			YAHOO.util.Connect.setForm(document.forms["vlan"]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveVlan, failure : failSaveVlan, timeout: 60000}, null);
		}
		
		}
	}
	
}

var succSaveVlan = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentDomID = details.parentDomID;
			if(parentDomID){
				var parentVlanSelects = parentDomID.split(',');
				if(parentVlanSelects.length > 1){
					for (var i=0;i<parentVlanSelects.length;i++) {
						var parentVlanSelect = parent.document.getElementById(parentVlanSelects[i]);
						if(parentVlanSelect != null) {
							if(details.addedId != null && details.addedId != ''){
								if(i==0){
									hm.util.insertSelectValue(details.addedId, details.addedName, parentVlanSelect, false, true);
								} else {
									hm.util.insertSelectValue(details.addedId, details.addedName, parentVlanSelect, false, false);
								}
								
							}
						}
					}
				} else {
					if (parentDomID=='vlanForMapping_parentDomID') {
						top.showVlanMappingSelectDialog();
					} else {
						var parentVlanSelect = parent.document.getElementById(parentDomID);
						if(parentVlanSelect != null) {
							if(details.addedId != null && details.addedId != ''){
								hm.util.insertSelectValue(details.addedId, details.addedName, parentVlanSelect, false, true);
							}
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

var failSaveVlan = function(o) {
	// do nothing now
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_VLAN%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		var vlanIds = document.getElementsByName('vlanIds');
		if(vlanIds.length > 0) {
			for(var i = 0; i < vlanIds.length; i ++) {
				vlanIds[i].value = 1;
			}
		}
		return true;
	}

    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_vlanName");
		var message = hm.util.validateName(name.value, '<s:text name="config.vlan.vlanName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}

	var table = document.getElementById("checkAll");

    if (operation == 'removeVlan' || operation == 'removeVlanNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.vlan.vlanId" /></s:param></s:text>');
			return false;
		}
	}

    if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update'+'<s:property value="lstForward"/>' 
    	|| operation == 'update' || operation == 'create') {
    	var vlanIds = document.getElementsByName('vlanIds');
		var table = document.getElementById("checkAll");
		if(vlanIds.length == 0)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.vlan.vlanId" /></s:param></s:text>');
       		table.focus();
       		return false;
		}

		for(var i = 0; i < vlanIds.length; i ++) {
			if(!checkVlanId(vlanIds[i], '<s:text name="config.vlan.vlanId" />'+' in '+(i+1)+' row')) {
				return false;
			}
		}
    }
	return true;
}

function checkVlanId(vlanId, title){
	var table = document.getElementById("vlanItemsRow");
	if (vlanId.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        vlanId.focus();
        return false;
    } else if(vlanId.value.substring(0,1) == '0') {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		vlanId.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(vlanId.value, title,1,4094);
    if (message != null) {
        hm.util.reportFieldError(table, message);
        vlanId.focus();
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

function changeType(type) {
	var hidename = document.getElementById("hideName");
	var hidelocation = document.getElementById("hideLocation");
	var hidehive = document.getElementById("hideHiveAP");
	var hideclass = document.getElementById("hideClassifier");

	switch(parseInt(type)) {
		case USETYPE_GLOBAL:
			hidename.style.display= "";
	    	hidelocation.style.display="none";
			hidehive.style.display="none";
			hideclass.style.display="none";
			break;
		case USETYPE_MAP:
			hidename.style.display= "none";
	    	hidelocation.style.display="";
			hidehive.style.display="none";
			hideclass.style.display="none";
			break;
		case USETYPE_HIVEAP:
			hidename.style.display= "none";
	    	hidelocation.style.display="none";
			hidehive.style.display="";
			hideclass.style.display="none";
			break;
		case USETYPE_CLASSIFIER:
			hidename.style.display= "none";
	    	hidelocation.style.display="none";
			hidehive.style.display="none";
			hideclass.style.display="";
			break;
		default:
			break;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vlan" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

function validateVlanForJson(operation){
    return validate(operation);
}
<s:if test="%{jsonMode && contentShownInSubDrawer}">
window.setTimeout("onLoadPage()", 100);
    <s:if test="%{writeDisabled!=''}">
    showHideNetworkPolicySubSaveBT(false);
    </s:if>
    <s:else>
    showHideNetworkPolicySubSaveBT(true);
    </s:else>
</s:if>
</script>
<div id="content"><s:form action="vlan" name="vlan" id="vlan">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="id" />
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg" />
	<s:hidden name="contentShowType" />
	<s:hidden name="selectedLANId" />
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
	<div id="vlanTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0"  width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-vlans-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.userprofile.vlan.dialog.new.title"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.userprofile.vlan.dialog.edit.title"/></td>
						</s:else>
						<td style="padding-left:10px;">
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
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveVlan('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="saveVlan('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
				</s:if>
				<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
				</s:else>
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
							<td><input type="button" name="create" value="<s:text name="button.create"/>"
								class="button"
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
								onClick="submitAction('<%=Navigation.L2_FEATURE_VLAN%>');">
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
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<s:if test="%{jsonMode == true && contentShownInDlg == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="800">
			</s:if>
			<s:elseif test="%{jsonMode}">
				<table cellspacing="0" cellpadding="0" border="0" width="800">
			</s:elseif>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="800">
			</s:else>
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.vlan.vlanName" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield size="24"
								name="dataSource.vlanName" maxlength="%{vlanNameLength}"
								disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
								name="config.ssid.ssidName_range" /></td>
						</tr>
						<tr>
							<td style="padding:6px 0px 6px 0px" colspan="2">
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
							<td colspan="2" style="padding:4px 0px 4px 4px;" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" class="embedded" style="width: 100%">
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" id="newItemBtn"
													<s:property value="updateDisabled" />></td>																								
													<td><input type="button" name="ignore" value="Reset Order" id="resetButton"
														class="button" <s:property value="updateDisabled" />></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
											    <s:if test="%{jsonMode && contentShownInSubDrawer}">
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button"
													id="addItemBtn"></td>												
											    </s:if>
											    <s:else>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" id="addItemBtn"></td>												
											    </s:else>
												<td><input type="button" name="ignore" value="Cancel"
												    id="cancelItemBtn" class="button"></td>
												 <td><input type="button" name="ignore" value="Reset Order" id="resetButton"
														class="button" <s:property value="updateDisabled" />></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr id="headerSection">
										<!-- th align="left" style="padding-left: 0;" width="10"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAllRules(this);"></th-->											
										<th align="left" width="100"><s:text
											name="config.vlan.vlanId" />
											<input type="checkbox" id="checkAll" style="display:none">
											</th>
										<th align="left" width="100"><s:text
											name="config.ipAddress.type" /></th>
										<th align="left" width="300"><s:text
											name="config.ipAddress.value" /></th>
										<th align="left" width="100"><s:text
											name="config.ipAddress.description" /></th>
									</tr>
									<tr id="vlanClassifierTagContainer"></tr>
									<tr id="vlanItemsRow"/>
									<s:if test="%{gridCount.size > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="6">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
									<tr>
											<td class="noteInfo" colspan="8"><s:text name="config.common.classifier.tag.note"/></td>
									</tr>									
								</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>

<div id="classfierNewRulePanel" style="display: none;height: 90px">
	<table width="140" border="0" >
		<tr>
		  <td align="left" ><s:text name="home.hmSettings.devicetag.newrulealert" /></td>
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="Yes" id="discardNewRule">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <input type="button" value="No" id="returnEditNewRule">
           </td>
        </tr>
     </table>
</div>  

<div id="classfierConfilictPanel" style="display: none;height: 80px">
	<table width="140" border="0" >
		<tr>
		  <td align="left" ><s:text name="home.hmSettings.devicetag.confilctalert" /></td>
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="OK" id="cancelInClassfierConfilictPanel">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <input type="button" value="Fix" id="fixInClassfierConfilictPanel">
           </td>
        </tr>
     </table>
</div>
 

<div id="classfierHelpWholePanel" style="display: none;height: 160px">
	<div id="classfierHelpPanel" style="overflow: auto;word-wrap: break-word; word-break: normal;"></div>	
	<input id="classfierHelpOkButton" type="button" value="OK" style="margin-left: 180px;">
</div>

<div id="classfierPopupWholePanel" style="display: none;height: 100px">
	<div id="classfierPopupPanelTitle"></div>
	<div id="classfierPopupPanel" style="overflow: auto;"></div>	
	<input id="classfierPopupPanelOkButton" type="button" value="OK" style="margin-left: 80px; margin-top: 10px;">
</div>

<div id="classfierEditPanel" style="display: none;">
	<table width="270" border="0" >
		<tr id="editClassifierTagContainer">
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="Save" id="saveInEditClassifierTagContainer">
              <input type="button" value="Cancel" id="cancelInEditClassifierTagContainer">
           </td>
        </tr>
     </table>
</div>		

<div style="display: none;">
<input type="button" value="AH Panel" id="ahTempClassifierMatchBtn">
<input type="button" value="AH Panel" id="ahTempClassifierHelpBtn">
<input type="button" value="Submit" id="ahTempClassifierEditBtn">
<input type="button" value="Submit" id="ahTempClassifierConfilictBtn">
<input type="button" value="Submit" id="ahTempClassifierNewRuleBtn">
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
<input type="text" value="Submit" id="ahTempClassfierConfilict" value="">
</div>

<style type="text/css">
#editClassifierTagContainer td.listHead {
    background-color: #FFFFFF; 
    height: 24px;   
}
#editClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#editClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier td#deviceCtner div ul li a.none{
	margin-left: 5px;	
	width: 120px;
} 
.ui-classifier td#topologyCtner div ul li a.none{
	margin-left: 5px;	
	width: 120px;
}
.ui-classifier td#tagCtner div ul li a.none{
	margin-left: 75px;
	width: 120px;	
}
.ui-classifier td#topologyCtner div ul li  input{
	margin-left: 5px;
    width: 140px;
}
.ui-classifier td#deviceCtner div ul li  input{
	margin-left: 5px;
    width: 140px;
}
.ui-classifier td#tagCtner div ul li  input{
	margin-left: 75px;
    width: 140px;
}
#vlanClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#vlanClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 120px;
 }

 #resetButton {
 	width: 90px;
 } 
.ui-classifier-items ul.itemContainer li{
 	background-image:none;
}
.ui-classifier td.defaultContainer li{
    background-image:none;
}
#vlanClassifierTagContainer td.defaultContainer ul{
 	width:auto;
 	list-style:none;
}
.ui-classifier-items ul.optmenu-container{
    width: auto;
}


</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script>!window.jQuery.ui && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/dialog/panel.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script>
function initButton() {		
$('input#newItemBtn').click(function() {
		hm.util.hide('newButton');
		hm.util.show('createButton');
		hm.util.show('vlanClassifierTagContainer');	
	});
	$('input#cancelItemBtn').click(function() {
	    hm.util.hide('createButton');
	    hm.util.show('newButton');
	    hm.util.hide('vlanClassifierTagContainer');		
	});
	$('input#addItemBtn').click(function() {
		$("#vlanClassifierTagContainer").classifierTag('saveItem');
	});
	$('input#resetButton').click(function() {
		$("#vlanClassifierTagContainer").classifierTag('resetOrder');
	});	
	var DOM = YAHOO.util.Dom;
    var aerohivePanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierPopupWholePanel', {width: 200, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierMatchBtn').onclick = function(){    	
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	aerohivePanel.openDialog();
    };
    
    DOM.get('classfierPopupPanelOkButton').onclick = function(){ 
    	aerohivePanel.closeDialog();
    };
    
    var helpPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierHelpWholePanel', {width: 420, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierHelpBtn').onclick = function(){    	
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	helpPanel.openDialog();
    };
    DOM.get('classfierHelpOkButton').onclick = function(){ 
    	helpPanel.closeDialog();
    };
    
    var confilictPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierConfilictPanel', {width: 140, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierConfilictBtn').onclick = function(){
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	confilictPanel.openDialog();
    };
    DOM.get('cancelInClassfierConfilictPanel').onclick = function(){ 
    	$("input#ahTempClassfierConfilict").attr("value","false");
    	saveAllItem();
    	confilictPanel.closeDialog();
    };
    DOM.get('fixInClassfierConfilictPanel').onclick = function(){ 
    	$("input#ahTempClassfierConfilict").attr("value","true");
    	saveAllItem();
    	confilictPanel.closeDialog();
    };
    
    var editPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierEditPanel', {width: 270, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierEditBtn').onclick = function(){   
    	$("#editClassifierTagContainer").show();
    	editPanel.openDialog();
    };
    
    
    DOM.get('cancelInEditClassifierTagContainer').onclick = function(){ 
    	editPanel.closeDialog();
    };
    
    DOM.get('saveInEditClassifierTagContainer').onclick = function(){     
    	var stag1=$("#editClassifierTagContainer").find("li#li_tag_input_0").find("input").val();
    	var stag2=$("#editClassifierTagContainer").find("li#li_tag_input_1").find("input").val();
    	var stag3=$("#editClassifierTagContainer").find("li#li_tag_input_2").find("input").val();
    	
    	var dvcName=$("#editClassifierTagContainer").find("li#li_device_input").find("input").val();    		
    	var topoName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId_hidden").val();
    	var tpName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId").val();
    		
    	var idx=$("input#ahTempClassifierIndexValue").val();	
    	var itemType=$("input#ahTempClassifierItemType").val();	
    	if(itemType==4&&stag1==""&&stag2==""&&stag3==""){
    		alert('<s:text name="home.hmSettings.devicetag.deviceTagNull" />');return;
    	}
		if(itemType==3&&dvcName==""){
			alert('<s:text name="home.hmSettings.devicetag.deviceNameNull" />');return;
    	}
		if(itemType==2&&topoName==""&&tpName==""){
			alert('<s:text name="home.hmSettings.devicetag.topologyNull" />');return;
		} 
    	if(itemType==2&&topoName==""){editPanel.closeDialog(); return;}
	    $.getJSON("classifierTag.action", 
					{tagKey: 1, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName}, 
				function(data) {
					if(data.succ) {	
						//just set the edited value back to jsp page
						var htmlIdx=parseInt(idx)+1;					
						var targetString=data.value;
						$("#vlanItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
						editPanel.closeDialog();
					}else{
						var ttab = document.getElementById("editClassifierTagContainer");
						hm.util.reportFieldError(ttab,data.errmsg);
					}
				})
				.error(function() {
					debug("editItems, Error occurs...");
				});    	
    };
    
       setTimeout(function(){		
    	 var ctt=$("#editClassifierTagContainer").classifierTag(
    				{
    					key: 1,
    					types:  [ {key: 2, text: 'Topology Node'},null,null],
    					itemWidths:{tagValue: 120 },
    					widgetWidth: {desc: 0},
    					valueProps: null,
    					itemEditable: false,
    					describable: false,	
    					zindexDisplay: true,
    					needShowTagFields: true,
    					needShowDeviceNames: true,
    					needShowTopology: true
    				}
    		); 
    }, 500); 
}

function initClassifierWidget() {	
  setTimeout(function(){	
		var ct = $("#vlanClassifierTagContainer").classifierTag(
				{
					key: 1,
					checkable:null,
					itemWidths:{value: 80, type: 90, tagValue: 290 ,desc: 80},
					widgetWidth: {desc: 200},
					valueProps: {items: {id: 'vlan_vlanId', onkeypress: 'return hm.util.keyPressPermit(event,\'ten\');', maxlength: 4, size: 8}, desc: '<br>(1-4094)', validateFn: function(){return checkVlanId(Get('vlan_vlanId'), '<s:text name="config.vlan.vlanId" />');}},
					itemTableId: 'vlanItemsRow',
					itemEditable: {value: true, valueName: 'vlanIds', desc: true, descName: 'descriptions'}
				});
		
		var domainId=<s:property value="domainId" />;		
		if(<s:property value="%{dataSource.items.size()==0}"/>) {
		    $("#vlanClassifierTagContainer").classifierTag('addEmptyGlobalItem');
		} else {
			<s:iterator value="%{dataSource.items}" status="status">
			var item = {};
			item.id = <s:property value="%{#status.index}"/>;
			item.value = '<s:property value="%{vlanId}"/>';
			item.type = <s:property value="%{type}"/>;
			item.tagValue = '<s:property value="getTagValue(domainId)"/>';			
			item.desc = '<s:property value="%{descriptionStr}" escapeHtml="false"/>';
			if(item.type == 1) {
				$("#vlanClassifierTagContainer").classifierTag('addItem', item, -1);
			} else {
				$("#vlanClassifierTagContainer").classifierTag('addItem', item);
			}
			</s:iterator>
		}
		if($('createButton').is(':hidden')) {
			 $("#vlanClassifierTagContainer").hide();
		} else {
			 $("#vlanClassifierTagContainer").show();
		}
		$("#vlanClassifierTagContainer").hide();	
		initButton();
	 },10); 
}

function widgetClick(sType,sTagValue) {		
	var ids= $("#editClassifierTagContainer").find("td.listHead:nth-child(1)").attr("id");
	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
	if(ids==undefined)
	$("#editClassifierTagContainer").find("td.listHead:nth-child(1)").remove();
	if(sType==4){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").show();
	}
	if(sType==3){
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#deviceCtner").show();
	}
	if(sType==2){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").show();
	}
}
</script>
