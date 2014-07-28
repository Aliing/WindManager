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
var formName = 'userAttribute';
var operName = 'create';
var saveInJsonflag=false;
var ruleIndex= '';
var USETYPE_GLOBAL = <%=SingleTableItem.TYPE_GLOBAL%>;
var USETYPE_MAP = <%=SingleTableItem.TYPE_MAP%>;
var USETYPE_HIVEAP = <%=SingleTableItem.TYPE_HIVEAPNAME%>;
var USETYPE_CLASSIFIER = <%=SingleTableItem.TYPE_CLASSIFIER%>;
    
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_attributeName").disabled) {
		document.getElementById(formName + "_dataSource_attributeName").focus();
	}
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(960,400);
	 	}
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
	saveInJsonflag=isInConfigure;
	var vlu=$("#userAttrClassifierTagContainer").find("input#userAttribute_attributeValue").val();
	var isTagVisable=$("#userAttrClassifierTagContainer").is(':visible');
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
	 if(operation=='create'||operation=='update'){
			checkConfilct(operation,false,succView,8);
			return;
		} 
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>' 
			|| operation == 'update<s:property value="lstForward"/>') {
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
	    if(saveInJsonflag){
	 		var url = "<s:url action='userAttribute' includeParams='none' />?ignore="+new Date().getTime();
			document.forms[formName].operation.value = operName;
			YAHOO.util.Connect.setForm(document.getElementById("userAttribute"));
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveUserAttributeJsonDlg, failure : failSaveUserAttributeJsonDlg, timeout: 60000}, null);
		}
		else if (validate(operName)) {
			document.forms[formName].operation.value = operName;
	    	document.forms[formName].submit();
		}
	 }
}		

<s:if test="%{jsonMode == true}">


function saveUserAttributeJsonDlg(operation) {
    checkConfilct(operation,true,saveUserAttributeJsonDlgSucc,8);		
}

function saveUserAttributeJsonDlgSucc(o) {
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) 
	{	
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		}
		else{
		if (validate(operName)) {
				var url = "<s:url action='userAttribute' includeParams='none' />?ignore="+new Date().getTime();
				document.forms[formName].operation.value = operName;
				YAHOO.util.Connect.setForm(document.getElementById("userAttribute"));
				var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveUserAttributeJsonDlg, failure : failSaveUserAttributeJsonDlg, timeout: 60000}, null);
			}			
		}	
	}	
}
var succSaveUserAttributeJsonDlg = function(o) {
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
var failSaveUserAttributeJsonDlg = function(o) {
}
</s:if>
function validate(operation) {
	if('<%=Navigation.L2_FEATURE_USER_PROFILE_ATTRIBUTE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}
	
	if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_attributeName");
		var message = hm.util.validateName(name.value, '<s:text name="config.userAttribute.attributeName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}
	
	var table = document.getElementById("checkAll");
    if (operation == 'addAttribute') {
    	 if(!checkAttributeValue(document.getElementById(formName + "_attributeValue"), '<s:text name="config.userAttribute.attributeValue" />')) {
			return false;
		}
		 
		 var hidename = document.getElementById("hideName");
		 var hidelocation = document.getElementById("hideLocation");
		 var hidehive = document.getElementById("hideHiveAP");
		 var hideclass = document.getElementById("hideClassifier");
		 if(hidename.style.display == "none") {	
		 	if(hidelocation.style.display == "") {
		 		var location = document.forms[formName].locationId;
		 		if(location.value == -1) {
		 			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.location" /></s:param></s:text>');
	        		location.focus();
	        		return false;
		 		}
		 	}
		 	if(hidehive.style.display == "") {
		 		var typename = document.getElementById(formName + "_typeName");
		
				if(typename.value.length == 0) {
					hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
	        		typename.focus();
	        		return false;
				} else if (typename.value.indexOf(' ') > -1) {
	        		hm.util.reportFieldError(table, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
	        		typename.focus();
	        		return false;
	    		}
		 	}
		 	if(hideclass.style.display == "") {
		 		var tag1 = document.getElementById(formName + "_tag1");
		 		var tag2 = document.getElementById(formName + "_tag2");
		 		var tag3 = document.getElementById(formName + "_tag3");
		 		if(tag1.value.length == 0 && tag2.value.length == 0 && tag3.value.length == 0){
					hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.classifier.tag" /></s:param></s:text>');
		 			tag1.focus();
		 			return false;
				}
		 	}    
		 }
    }
    if (operation == 'removeAttribute' || operation == 'removeAttributeNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.userAttribute.attributeValue" /></s:param></s:text>');
			return false;
		}
	} 
    
    if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update<s:property value="lstForward"/>'
    	|| operation == 'create' 
    	|| operation == 'update'
    	) {
    	var attributeValues = document.getElementsByName('attributeValues');		
		var table = document.getElementById("checkAll");
		if(attributeValues.length == 0)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.userAttribute.attributeValue" /></s:param></s:text>');
       		table.focus();
       		return false;
		} 	
		
		for(var i = 0; i < attributeValues.length; i ++) {
			if(!checkAttributeValue(attributeValues[i], '<s:text name="config.userAttribute.attributeValue" />'+' in '+(i+1)+' row')) {
				return false;
			}
		}
    }
	return true;
}

function checkAttributeValue(attriValue, title) {
	var table = document.getElementById("checkAll");
	
	if (attriValue.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        attriValue.focus();
        return false;
    } else {
	    var attrInThis = new Array();
		var attributes = attriValue.value.split(",");
		if (attributes.length > 9)
		{
			hm.util.reportFieldError(table, '<s:text name="error.entryLimit"><s:param>'+title+'</s:param><s:param value="9" /></s:text>');
	        attriValue.focus();
	        return false;
		}
		for (var i = 0; i < attributes.length; i++)
		{
			var str_attribute = attributes[i];
			if("" == str_attribute)
			{
				hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
	        	attriValue.focus();
	        	return false;
			}
	
			if(!isNaN(str_attribute))
			{
				// it is a number;
				var message = hm.util.validateIntegerRange(str_attribute, title,1,4095);
	      		if (message != null) {
	            	hm.util.reportFieldError(table, message);
	           		attriValue.focus();
	            	return false;
	      		}
	      		for(var j = 0; j < attrInThis.length; j++)
	      		{
				if (attrInThis[j] == str_attribute)
				{
					hm.util.reportFieldError(table, '<s:text name="error.sameObjectExists"><s:param>'+title+'</s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				}
				attrInThis.push(str_attribute);
			}
			else
			{
				// it is a range;
				var str_range = str_attribute.split("-");
				if (str_range.length != 2)
				{
					hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				else
				{
					if(!isNaN(str_range[0]) && !isNaN(str_range[1]))
					{
						var message1 = hm.util.validateIntegerRange(str_range[0], title,1,4095);
						var message2 = hm.util.validateIntegerRange(str_range[1], title,1,4095);
	      				if (message1 != null) {
	            			hm.util.reportFieldError(table, message1);
	           				attriValue.focus();
	            			return false;
	      				}
	      				if (message2 != null) {
	            			hm.util.reportFieldError(table, message2);
	           				attriValue.focus();
	            			return false;
	      				}
						if (parseInt(str_range[0]) >= parseInt(str_range[1]))
						{
							hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
	        				attriValue.focus();
	        				return false;	
						}
						for(var j = 0; j < attrInThis.length; j++)
	      				{
							if (attrInThis[j] == str_range)
							{
								hm.util.reportFieldError(table, '<s:text name="error.sameObjectExists"><s:param>'+title+'</s:param></s:text>');
	        					attriValue.focus();
	        					return false;
							}
						}
						attrInThis.push(str_range);			
					}
					else
					{
						hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
	        			attriValue.focus();
	        			return false;
					}
				}
			}
		}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="userAttribute" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>
<div id="content"><s:form action="userAttribute">
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="userAttributeTitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-User_Profile_Atribute_Groups.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.attribute"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.attribute.edit"/></td>
						</s:else>
						<td style="padding-left: 10px">
						    <a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
                                   alt="" class="dblk" />
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
							<s:if test="%{writeDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onclick="saveUserAttributeJsonDlg('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onclick="saveUserAttributeJsonDlg('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
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
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userAttributeSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
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
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_USER_PROFILE_ATTRIBUTE%>');">
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
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="850">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="850">
			</s:else>
				<tr>
					<td style="padding: 6px 5px 5px 5px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" width="100"><label><s:text
								name="config.userAttribute.attributeName" /><font color="red"><s:text name="*"/></font></label></td>
							<td><s:textfield size="24"
								name="dataSource.attributeName" maxlength="%{nameLength}"
								disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'name');"/>&nbsp;<s:text
								name="config.ssid.ssidName_range" /></td>
						</tr>
						<tr>
							<td style="padding:6px 4px 6px 4px" colspan="2">
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
										<td colspan="5" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
													class="button" id="newItemBtn" ></td>	
												<td><input type="button" name="ignore" value="Reset Order"
													id="resetButton" class="button" ></td>	
																						
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="5" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													id="addItemBtn" class="button" ></td>
									  		<td><input type="button" name="ignore" value="Cancel"
													id="cancelItemBtn" class="button" ></td>
											<td><input type="button" name="ignore" value="ResetOrder"
													id="resetButton" class="button" ></td>	
											</tr>
										</table>
										</td>
									</tr>
																		
									<tr>										
										<td colspan="4" class="noteInfo"><label><s:text
											name="config.userAttribute.attributeRange" /></label></td>
									</tr>
									
									<tr>										
										<td height="8"></td>
									</tr>
									
									<tr id="headerSection">
										<!-- th align="left" style="padding-left: 0;" width="10"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAllRules(this);"></th-->
										<th align="left" width="150"><s:text
											name="config.userAttribute.attributeValue" />
											<input type="checkbox" id="checkAll" style="display:none">
										</th>
										<th align="left" width="110"><s:text
											name="config.ipAddress.type" /></th>
										<th align="left" width="300"><s:text
											name="config.ipAddress.value" /></th>
										<th align="left" width="240"><s:text
											name="config.ipAddress.description" /></th>
									</tr>									
									<tr id="userAttrClassifierTagContainer"></tr>									
									<tr id="userAttrItemsRow"/>								
									
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
<div id="classfierHelpWholePanel" style="display: none;height: 160px">
	<div id="classfierHelpPanel" style="overflow: auto;word-wrap: break-word; word-break: normal;"></div>	
	<input id="classfierHelpOkButton" type="button" value="OK" style="margin-left: 180px;">
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
<div id="classfierPopupWholePanel" style="display: none;height: 100px">
	<div id="classfierPopupPanelTitle"></div>
	<div id="classfierPopupPanel" style="overflow: auto;"></div>	
	<input id="classfierPopupPanelOkButton" type="button" value="OK" style="margin-left: 180px; margin-top: 10px;">
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
<input type="button" value="Submit" id="ahTempClassifierEditBtn">
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
<input type="text" value="Submit" id="ahTempClassfierConfilict" value="">
<input type="button" value="Submit" id="ahTempClassifierConfilictBtn">
<input type="button" value="AH Panel" id="ahTempClassifierHelpBtn">
<input type="button" value="Submit" id="ahTempClassifierNewRuleBtn">
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
#userAttrClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#userAttrClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 120px;
 }

#userAttrItemsRow span.optmenu {    
    left: 35px;
}
#resetButton {
 	width: 90px;
 } 
</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script>!window.jQuery.ui && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>     
<script src="<s:url value="/js/widget/dialog/panel.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script>
function initClassifierWidget() {	
		var ct = $("#userAttrClassifierTagContainer").classifierTag(
				{
					key: 8,					
					itemWidths:{value: 140, type: 90, tagValue: 300, desc: 240},
					validateFn: function(){return checkAttributeValue(Get('userAttribute_attributeValue'), '<s:text name="config.userAttribute.attributeValue" />');},
					checkable:null,
					widgetWidth: {desc: 240},
					valueProps: {items: {id: 'userAttribute_attributeValue',field: 'attributeValue', onkeypress: 'return hm.util.keyPressPermit(event,\'attribute\');',  size: 12,maxlength: 50}, desc: '<br><s:text name="config.userAttribute.attributeValue.allow" />', validateFn: function(){return checkAttributeValue(Get('userAttribute_attributeValue'), '<s:text name="config.userAttribute.attributeValue" />');}},
					itemTableId: 'userAttrItemsRow',
					itemEditable: {value: true, valueName: 'attributeValues', desc: true, descName: 'descriptions'}
				});
		var domainId=<s:property value="domainId" />;	
		if(<s:property value="%{dataSource.items.size()==0}"/>) {
		  $("#userAttrClassifierTagContainer").classifierTag('addEmptyGlobalItem');
		} else {			
			<s:iterator value="%{dataSource.items}" status="status">
			var item = {};
			item.id = <s:property value="%{#status.index}"/>;
			item.value = '<s:property value="%{attributeValue}"/>';
			item.type = <s:property value="%{type}"/>;
			item.tagValue = '<s:property value="getTagValue(domainId)"/>';
			item.desc = '<s:property value="%{descriptionStr}"/>';
			if(item.type == 1) {
				$("#userAttrClassifierTagContainer").classifierTag('addItem', item, -1);
			} else {
				$("#userAttrClassifierTagContainer").classifierTag('addItem', item);
			}
			</s:iterator>
		}
		 $("#userAttrClassifierTagContainer").hide();	
		 $('input#newItemBtn').click(function() {
			hm.util.hide('newButton');
			hm.util.show('createButton');
			hm.util.show('userAttrClassifierTagContainer');
		});
		$('input#cancelItemBtn').click(function() {
	   	 hm.util.hide('createButton');
	   	 hm.util.show('newButton');
	  	  hm.util.hide('userAttrClassifierTagContainer');		
		});
		$('input#addItemBtn').click(function() {
			$("#userAttrClassifierTagContainer").classifierTag('saveItem');
		});
		$('input#resetButton').click(function() {
			$("#userAttrClassifierTagContainer").classifierTag('resetOrder');
		});
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

YAHOO.util.Event.onDOMReady(function(){
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
					{tagKey: 8, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName}, 
				function(data) {
					if(data.succ) {	
						//just set the edited value back to jsp page
						var htmlIdx=parseInt(idx)+1;
						var targetString=data.value;
						$("#userAttrItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
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
    					key: 8,
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
    }, 1000);  
    
});
</script>
